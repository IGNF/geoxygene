/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.genealgorithms.facilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayLine;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea.TaxiwayType;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayLine;
import fr.ign.cogit.cartagen.genealgorithms.polygon.Skeletonize;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.clustering.AdjacencyClustering;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.generalisation.simplification.SimplificationAlgorithm;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.OrientationMeasure;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.MorphologyTransform;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

public class AirportTypification {

  private IAirportArea airport;
  private boolean runwayAreaElim = true;
  private double apronMinArea, apronSegLength;
  private double openThreshTaxi;

  public AirportTypification(IAirportArea airport) {
    super();
    this.airport = airport;
  }

  /**
   * Builds runway lines from areas and eliminate areas. The collapse is made
   * looking for the longest inside segment parallel to the runway orientation.
   * @throws Exception
   */
  public void collapseRunways() throws Exception {
    IPopulation<IRunwayLine> pop = CartAGenDoc.getInstance()
        .getCurrentDataset().getRunwayLines();
    for (IRunwayArea runway : this.airport.getRunwayAreas()) {
      // first find the runway orientation
      double orient = new OrientationMeasure(runway.getGeom())
          .getGeneralOrientation();
      // densify the runway geometry
      IPolygon geom = LineDensification.densification2(runway.getGeom(), 2.0);
      ILineString seg = CommonAlgorithmsFromCartAGen.getLongestInsideSegment(
          geom, orient);
      // eliminate the runway area
      if (this.runwayAreaElim) {
        runway.eliminateBatch();
      }
      // build a new runway line
      IRunwayLine line = CartagenApplication.getInstance().getCreationFactory()
          .createRunwayLine(seg);
      pop.add(line);
      this.airport.getRunwayLines().add(line);
      // TODO add to a population ?
    }
  }

  public void mergeRunways() throws Exception {
    AdjacencyClustering clusters = new AdjacencyClustering(
        this.airport.getRunwayAreas());
    for (Set<IGeneObj> cluster : clusters.getClusters()) {
      IGeneObj remaining = null;
      List<Geometry> list = new ArrayList<Geometry>();
      for (IGeneObj obj : cluster) {
        if (remaining == null) {
          remaining = obj;
        }
        list.add(JtsGeOxygene.makeJtsGeom(obj.getGeom()));
        obj.eliminateBatch();
        this.airport.getRunwayAreas().remove(obj);
      }
      if (remaining == null) {
        return;
      }
      // union of the geometries
      Geometry jtsUnion = JtsAlgorithms.union(list);
      IGeometry union = JtsGeOxygene.makeGeOxygeneGeom(jtsUnion);
      if (union instanceof IPolygon) {
        remaining.cancelElimination();
        remaining.setGeom(union);
        this.airport.getRunwayAreas().add((IRunwayArea) remaining);
      }
    }
  }

  public void simplifyAprons() {
    for (ITaxiwayArea taxi : this.airport.getTaxiwayAreas()) {
      // check to it's an apron
      if (taxi.getType().equals(TaxiwayType.TAXIWAY)) {
        continue;
      }
      // check apron size1
      if (taxi.getGeom().area() < this.apronMinArea) {
        taxi.eliminateBatch();
        continue;
      }
      // simplify feature
      IGeometry newGeom = SimplificationAlgorithm.simplification(
          taxi.getGeom(), this.apronSegLength);
      taxi.setGeom(newGeom);
    }
  }

  @SuppressWarnings("unchecked")
  public void collapseThinTaxiways() {
    for (ITaxiwayArea taxi : new HashSet<ITaxiwayArea>(
        this.airport.getTaxiwayAreas())) {
      // only treat the Taxiways
      if (taxi.getType().equals(TaxiwayType.APRON)) {
        continue;
      }
      // first 'open' the geometry to remove thin parts
      IGeometry newGeom = new MorphologyTransform(this.openThreshTaxi, 10)
          .opening(taxi.getGeom());
      if (newGeom != null) {
        IPolygon iniGeom = taxi.getGeom();
        // upadte new opened geometry
        if (newGeom instanceof IPolygon) {
          taxi.setGeom(newGeom);
        } else {
          boolean first = true;
          for (IOrientableSurface simple : new ArrayList<IOrientableSurface>(
              ((IMultiSurface<IOrientableSurface>) newGeom).getList())) {
            if (simple.area() < 4 * Math.pow(this.openThreshTaxi * 2, 2)) {
              ((IMultiSurface<IOrientableSurface>) newGeom).getList().remove(
                  simple);
              continue;
            }
            if (first) {
              taxi.setGeom(simple);
              first = false;
              continue;
            }
            ITaxiwayArea newTaxi = CartagenApplication.getInstance()
                .getCreationFactory()
                .createTaxiwayArea((IPolygon) simple, taxi.getType());
            this.airport.getTaxiwayAreas().add(newTaxi);
          }
        }
        // then collapse the thin parts into ITaxiwayLine features
        IGeometry thinParts = iniGeom.difference(newGeom);
        if (thinParts instanceof IPolygon & thinParts.area() > 500.0) {
          // then, the polygonal thin part is collapsed into a ITaxiwayLine.
          Set<ILineSegment> skeleton = Skeletonize
              .skeletonizeStraightSkeleton((IPolygon) thinParts);
          for (ILineString seg : Skeletonize.connectSkeletonToPolygon(skeleton,
              (IPolygon) thinParts)) {
            // build a new taxiway line
            ITaxiwayLine line = CartagenApplication.getInstance()
                .getCreationFactory().createTaxiwayLine(seg, taxi.getType());
            this.airport.getTaxiwayLines().add(line);
          }
        } else if (thinParts instanceof IMultiSurface) {
          for (IOrientableSurface simple : ((IMultiSurface<IOrientableSurface>) thinParts)
              .getList()) {
            if (simple.area() < 800.0) {
              continue;
            }
            // then, the polygonal thin part is collapsed into a ITaxiwayLine.
            Set<ILineSegment> skeleton = Skeletonize
                .skeletonizeStraightSkeleton((IPolygon) simple);
            for (ILineString seg : Skeletonize.connectSkeletonToPolygon(
                skeleton, (IPolygon) simple)) {
              // build a new taxiway line
              ITaxiwayLine line = CartagenApplication.getInstance()
                  .getCreationFactory().createTaxiwayLine(seg, taxi.getType());
              this.airport.getTaxiwayLines().add(line);
            }
          }
        }
      } else {
        IPolygon iniGeom = taxi.getGeom();
        taxi.eliminateBatch();
        // then, the taxiway feature is collapsed into a ITaxiwayLine.
        Set<ILineSegment> skeleton = Skeletonize
            .skeletonizeStraightSkeleton(iniGeom);
        for (ILineString seg : Skeletonize.connectSkeletonToPolygon(skeleton,
            iniGeom)) {
          // build a new taxiway line
          ITaxiwayLine line = CartagenApplication.getInstance()
              .getCreationFactory().createTaxiwayLine(seg, taxi.getType());
          this.airport.getTaxiwayLines().add(line);
        }
      }
    }
  }

  public double getApronSegLength() {
    return this.apronSegLength;
  }

  public void setApronSegLength(double apronSegLength) {
    this.apronSegLength = apronSegLength;
  }

  public boolean isRunwayAreaElim() {
    return this.runwayAreaElim;
  }

  public void setRunwayAreaElim(boolean runwayAreaElim) {
    this.runwayAreaElim = runwayAreaElim;
  }

  public double getApronMinArea() {
    return this.apronMinArea;
  }

  public void setApronMinArea(double apronMinArea) {
    this.apronMinArea = apronMinArea;
  }

  public double getOpenThreshTaxi() {
    return this.openThreshTaxi;
  }

  public void setOpenThreshTaxi(double openThreshTaxi) {
    this.openThreshTaxi = openThreshTaxi;
  }
}
