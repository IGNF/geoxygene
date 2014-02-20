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
import java.util.Stack;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayLine;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea.TaxiwayType;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayLine;
import fr.ign.cogit.cartagen.genealgorithms.polygon.Skeletonize;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.spatialanalysis.clustering.AdjacencyClustering;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.generalisation.simplification.SimplificationAlgorithm;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.OrientationMeasure;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.MorphologyTransform;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

public class AirportTypification {

  private IAirportArea airport;
  private boolean runwayAreaElim = true;
  private double apronMinArea, apronSegLength;
  private double openThreshTaxi;
  /** The maximum area of a branching taxiway pattern. Default value = 7000 m² */
  private double branchingMaxArea;
  private double maxAngleBranching;
  private Set<TaxiwayBranching> branchings;
  private Set<TaxiwayBranchingGroup> branchingGroups;

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
    IPopulation<IRunwayLine> pop = CartAGenDocOld.getInstance()
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
      CartAGenDoc.getInstance().getCurrentDataset()
          .getCartagenPop(CartAGenDataSet.RUNWAY_LINE_POP).add(line);
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

  public void detectBranchingPatterns() {
    this.branchings = new HashSet<AirportTypification.TaxiwayBranching>();
    this.branchingGroups = new HashSet<AirportTypification.TaxiwayBranchingGroup>();

    // first make the taxiway network planar
    makeTaxiwaysPlanar();

    // then, search for simple branching patterns
    IFeatureCollection<IFeature> fc = new FT_FeatureCollection<IFeature>();
    fc.addAll(airport.getTaxiwayLines());
    IFeatureCollection<IFeature> fc2 = new FT_FeatureCollection<IFeature>();
    fc2.addAll(airport.getRunwayLines());
    CarteTopo carteTopo = new CarteTopo("build faces");
    carteTopo.importClasseGeo(fc, true);
    carteTopo.importClasseGeo(fc2, true);
    carteTopo.rendPlanaire(0.01);
    carteTopo.creeNoeudsManquants(0.01);
    carteTopo.creeTopologieArcsNoeuds(0.01);
    carteTopo.creeTopologieFaces();
    for (Face face : carteTopo.getListeFaces()) {
      if (face.getGeom().area() > branchingMaxArea)
        continue;
      // check if shape is triangular
      List<IDirectPosition> corners = new ArrayList<IDirectPosition>();
      int nbArcs = face.getArcsDirects().size()
          + face.getArcsIndirects().size();
      if (nbArcs > 3) {
        corners.addAll(CommonAlgorithmsFromCartAGen.getSharpAngleVertices(
            (IPolygon) face.getGeom(), maxAngleBranching));
        if (corners.size() != 3)
          continue;
      } else {
        for (Arc a : face.getArcsDirects()) {
          if (!corners.contains(a.getNoeudIni().getCoord()))
            corners.add(a.getNoeudIni().getCoord());
          if (!corners.contains(a.getNoeudFin().getCoord()))
            corners.add(a.getNoeudFin().getCoord());
        }
        for (Arc a : face.getArcsIndirects()) {
          if (!corners.contains(a.getNoeudIni().getCoord()))
            corners.add(a.getNoeudIni().getCoord());
          if (!corners.contains(a.getNoeudFin().getCoord()))
            corners.add(a.getNoeudFin().getCoord());
        }
      }
      // arrived here, the face is a simple taxiwayBranching
      // create the object
      Set<IGeneObj> inTaxiways = new HashSet<IGeneObj>();
      Set<Noeud> nodes = new HashSet<Noeud>();
      for (Arc arc : face.getArcsDirects()) {
        inTaxiways.add((IGeneObj) arc.getCorrespondant(0));
        nodes.addAll(arc.noeuds());
      }
      for (Arc arc : face.getArcsIndirects()) {
        inTaxiways.add((IGeneObj) arc.getCorrespondant(0));
        nodes.addAll(arc.noeuds());
      }
      Set<IGeneObj> outTaxiways = new HashSet<IGeneObj>();
      for (Noeud node : nodes) {
        for (Arc arc : node.arcs()) {
          if (inTaxiways.contains(arc.getCorrespondant(0)))
            continue;
          outTaxiways.add((IGeneObj) arc.getCorrespondant(0));
        }
      }
      this.branchings.add(new TaxiwayBranching(face.getGeometrie(), inTaxiways,
          outTaxiways, corners));
    }

    // then, search for double branching patterns within the detected branchings
    Stack<TaxiwayBranching> stack = new Stack<AirportTypification.TaxiwayBranching>();
    stack.addAll(this.branchings);
    while (!stack.isEmpty()) {
      TaxiwayBranching branch = stack.pop();
      Set<TaxiwayBranching> branchingGroup = new HashSet<AirportTypification.TaxiwayBranching>();
      branchingGroup.add(branch);
      Stack<TaxiwayBranching> neighbours = new Stack<AirportTypification.TaxiwayBranching>();
      // find neighbours
      IFeatureCollection<TaxiwayBranching> coln = new FT_FeatureCollection<AirportTypification.TaxiwayBranching>();
      coln.addAll(stack);
      for (TaxiwayBranching touching : coln.select(branch.getGeom())) {
        if (branch.groupingPossible(touching))
          neighbours.add(touching);
      }
      branchingGroup.addAll(neighbours);
      // make the group as long as some new compatible neighbours are found
      while (!neighbours.isEmpty()) {
        TaxiwayBranching neighbour = neighbours.pop();
        for (TaxiwayBranching touching : coln.select(neighbour.getGeom())) {
          if (branchingGroup.contains(touching))
            continue;
          if (neighbour.groupingPossible(touching))
            neighbours.add(touching);
        }
        branchingGroup.addAll(neighbours);
      }

      // check that there is a group
      if (branchingGroup.size() == 1)
        continue;

      // now remove the group from the stack to avoid creating twice the same
      // group
      stack.removeAll(branchingGroup);

      // create the new branching group
      this.branchingGroups.add(new TaxiwayBranchingGroup(branchingGroup));
      this.branchings.removeAll(branchingGroup);
    }

  }

  private void makeTaxiwaysPlanar() {
    IFeatureCollection<IGeneObj> fc = new FT_FeatureCollection<IGeneObj>();
    for (ITaxiwayLine obj : airport.getTaxiwayLines())
      fc.add(obj);
    for (IRunwayLine obj : airport.getRunwayLines())
      fc.add(obj);
    CarteTopo carteTopo = new CarteTopo("build faces");
    carteTopo.importClasseGeo(fc, true);
    carteTopo.rendPlanaire(0.01);
    for (IGeneObj obj : fc) {
      try {
        // test if the section has been cut by topological map
        if (obj.getCorrespondants().size() == 1) {
          continue;
        }

        // update the section geometry with the first edge of the
        // topological
        // map
        obj.setGeom(obj.getCorrespondants().get(0).getGeom());

        // loop on the other edges to make new instances
        AbstractCreationFactory factory = CartAGenDoc.getInstance()
            .getCurrentDataset().getCartAGenDB().getGeneObjImpl()
            .getCreationFactory();
        for (int i = 1; i < obj.getCorrespondants().size(); i++) {
          ILineString newLine = (ILineString) obj.getCorrespondants().get(i)
              .getGeom();
          if (obj instanceof ITaxiwayLine) {
            ITaxiwayLine newObj = factory.createTaxiwayLine(newLine,
                TaxiwayType.TAXIWAY);
            newObj.setSymbolId(obj.getSymbolId());
            // copy attributes
            newObj.copyAttributes(obj);
            airport.getTaxiwayLines().add(newObj);
            newObj.setAirport(airport);
            CartAGenDoc.getInstance().getCurrentDataset()
                .getCartagenPop(CartAGenDataSet.TAXIWAY_LINE_POP).add(newObj);
          } else if (obj instanceof IRunwayLine) {
            IRunwayLine newObj = factory.createRunwayLine(newLine);
            newObj.setSymbolId(obj.getSymbolId());
            // copy attributes
            newObj.copyAttributes(obj);
            airport.getRunwayLines().add(newObj);
            newObj.setAirport(airport);
            CartAGenDoc.getInstance().getCurrentDataset()
                .getCartagenPop(CartAGenDataSet.RUNWAY_LINE_POP).add(newObj);
          }
        }

      } catch (SecurityException e1) {
        e1.printStackTrace();
      } catch (IllegalArgumentException e1) {
        e1.printStackTrace();
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

  public Set<TaxiwayBranching> getBranchings() {
    return branchings;
  }

  public void setBranchings(Set<TaxiwayBranching> branchings) {
    this.branchings = branchings;
  }

  public Set<TaxiwayBranchingGroup> getDoubleBranchings() {
    return branchingGroups;
  }

  public void setDoubleBranchings(Set<TaxiwayBranchingGroup> doubleBranchings) {
    this.branchingGroups = doubleBranchings;
  }

  public double getBranchingMaxArea() {
    return branchingMaxArea;
  }

  public void setBranchingMaxArea(double branchingMaxArea) {
    this.branchingMaxArea = branchingMaxArea;
  }

  public double getMaxAngleBranching() {
    return maxAngleBranching;
  }

  public void setMaxAngleBranching(double maxAngleBranching) {
    this.maxAngleBranching = maxAngleBranching;
  }

  public class TaxiwayBranching extends DefaultFeature {
    private IPolygon geom;
    private Set<IGeneObj> inTaxiways, outTaxiways;
    private List<IDirectPosition> corners;

    public TaxiwayBranching(IPolygon geom, Set<IGeneObj> inTaxiways,
        Set<IGeneObj> outTaxiways, List<IDirectPosition> corners) {
      super();
      this.geom = geom;
      this.inTaxiways = inTaxiways;
      this.outTaxiways = outTaxiways;
      this.corners = corners;
    }

    public IPolygon getGeom() {
      return geom;
    }

    public void setGeom(IPolygon geom) {
      this.geom = geom;
    }

    public Set<IGeneObj> getInTaxiways() {
      return inTaxiways;
    }

    public void setInTaxiways(Set<IGeneObj> inTaxiways) {
      this.inTaxiways = inTaxiways;
    }

    public Set<IGeneObj> getOutTaxiways() {
      return outTaxiways;
    }

    public void setOutTaxiways(Set<IGeneObj> outTaxiways) {
      this.outTaxiways = outTaxiways;
    }

    /**
     * Checks if it is possible to make a branching group out of two branching
     * taxiways.
     * @param branching
     * @return
     */
    public boolean groupingPossible(TaxiwayBranching branching) {
      // get branching corners
      List<IDirectPosition> corners1 = this.corners;
      List<IDirectPosition> corners2 = branching.corners;
      IGeometry intersection = this.getGeom().intersection(branching.getGeom());
      IDirectPosition start = intersection.coord().get(0);
      IDirectPosition end = intersection.coord().get(
          intersection.coord().size() - 1);
      if (corners1.contains(start) && corners1.contains(end))
        return true;
      if (corners2.contains(start) && corners2.contains(end))
        return true;
      return false;
    }

    private List<IGeneObj> getConnectedWay(boolean in, IDirectPosition corner) {
      List<IGeneObj> list = new ArrayList<IGeneObj>();
      if (in) {
        for (IGeneObj way : this.inTaxiways) {
          if (way.getGeom().coord().get(0).equals(corner, 0.001)
              || way.getGeom().coord().get(way.getGeom().coord().size() - 1)
                  .equals(corner, 0.001))
            list.add(way);
        }
        return list;
      } else {
        for (IGeneObj way : this.outTaxiways) {
          if (way.getGeom().coord().get(0).equals(corner, 0.001)
              || way.getGeom().coord().get(way.getGeom().coord().size() - 1)
                  .equals(corner, 0.001)) {
            list.add(way);
            return list;
          }
        }
      }
      return null;
    }

    /**
     * Collapse the branching taxiway, i.e. remove the slip lines and extend the
     * minor out taxiway to the major in taxiway (same as branching roads
     * collapse).
     */
    public void collapse() {
      IGeneObj slipWay1 = null, slipWay2 = null;
      IGeneObj minorOutWay = null;
      IDirectPosition cornerMain1 = null, cornerMain2 = null, cornerMinor = null;
      // first, find slipWay1, slipWay2, minorOutWay and related corners
      List<IDirectPosition> cornerList = new ArrayList<IDirectPosition>();
      cornerList.addAll(corners);
      cornerList.addAll(corners);
      double minColinearity = Double.MAX_VALUE;
      for (int i = 0; i < 3; i++) {
        IDirectPosition corner = cornerList.get(i);
        // try if corner is the minor corner
        // it means that both other corner have parallel out ways
        IDirectPosition corner2 = cornerList.get(i + 1);
        IDirectPosition corner3 = cornerList.get(i + 2);
        IGeneObj corner2Way = getConnectedWay(false, corner2).get(0);
        Vector2D v2 = new Vector2D(corner2Way.getGeom().coord().get(1), corner2);
        if (!corner2Way.getGeom().coord().get(0).equals(corner2))
          v2 = new Vector2D(corner2Way.getGeom().coord()
              .get(corner2Way.getGeom().coord().size() - 2), corner2);
        IGeneObj corner3Way = getConnectedWay(false, corner3).get(0);
        Vector2D v3 = new Vector2D(corner3Way.getGeom().coord().get(1), corner3);
        if (!corner3Way.getGeom().coord().get(0).equals(corner3))
          v3 = new Vector2D(corner3Way.getGeom().coord()
              .get(corner3Way.getGeom().coord().size() - 2), corner3);
        double colinearity = 2
            * Math.abs(v3.getX() * v2.getY() - v3.getY() * v2.getX())
            / (v2.norme() + v3.norme());
        if (colinearity < minColinearity) {
          minColinearity = colinearity;
          cornerMinor = corner;
          cornerMain1 = corner2;
          cornerMain2 = corner3;
          minorOutWay = getConnectedWay(false, cornerMinor).get(0);
          List<IGeneObj> slipWays = getConnectedWay(true, cornerMinor);
          slipWay1 = slipWays.get(0);
          slipWay2 = slipWays.get(1);
        }
      }

      // then, delete slipWay1 and slipWay2
      if (slipWay1 != null)
        slipWay1.eliminateBatch();
      if (slipWay2 != null)
        slipWay2.eliminateBatch();

      // now extend minorOutWay
      Vector2D direction = new Vector2D(minorOutWay.getGeom().coord().get(1),
          cornerMinor);
      if (!minorOutWay.getGeom().coord().get(0).equals(cornerMinor))
        direction = new Vector2D(minorOutWay.getGeom().coord()
            .get(minorOutWay.getGeom().coord().size() - 2), cornerMinor);
      Segment segment = new Segment(cornerMain1, cornerMain2)
          .extendAtExtremities(2.0);
      IDirectPosition proj = CommonAlgorithmsFromCartAGen.projection(
          cornerMinor, segment, direction);
      // check of the projected is on the outline or not
      if (!this.getGeom().exteriorLineString().intersects(proj.toGM_Point())) {
        // it means that the main way is not right but curved. Then, look for
        // the nearest point on the outline.
        proj = CommonAlgorithms.getNearestPoint(this.getGeom()
            .exteriorLineString(), proj.toGM_Point());
      }
      if (!minorOutWay.getGeom().coord().get(0).equals(cornerMinor)) {
        IDirectPositionList points = new DirectPositionList();
        points.addAll(minorOutWay.getGeom().coord());
        points.add(proj);
        ILineString newGeom = new GM_LineString(points);
        minorOutWay.setGeom(newGeom);
      } else {
        IDirectPositionList points = new DirectPositionList();
        points.addAll(minorOutWay.getGeom().coord());
        points.add(0, proj);
        ILineString newGeom = new GM_LineString(points);
        minorOutWay.setGeom(newGeom);
      }
    }
  }

  public class TaxiwayBranchingGroup {
    private IPolygon geom;
    private Set<IGeneObj> inTaxiways, outTaxiways;
    private Set<TaxiwayBranching> components;

    public TaxiwayBranchingGroup(Set<TaxiwayBranching> components) {
      super();
      this.components = components;
      computeGeom();
      this.inTaxiways = new HashSet<IGeneObj>();
      this.outTaxiways = new HashSet<IGeneObj>();
    }

    public IPolygon getGeom() {
      return geom;
    }

    public void setGeom(IPolygon geom) {
      this.geom = geom;
    }

    public Set<IGeneObj> getInTaxiways() {
      return inTaxiways;
    }

    public void setInTaxiways(Set<IGeneObj> inTaxiways) {
      this.inTaxiways = inTaxiways;
    }

    public Set<IGeneObj> getOutTaxiways() {
      return outTaxiways;
    }

    public void setOutTaxiways(Set<IGeneObj> outTaxiways) {
      this.outTaxiways = outTaxiways;
    }

    @SuppressWarnings("unchecked")
    private void computeGeom() {
      IGeometry geom = null;
      for (TaxiwayBranching obj : components) {
        if (geom == null)
          geom = obj.getGeom();
        else
          geom = geom.union(obj.getGeom());
      }
      if (geom instanceof IPolygon)
        this.setGeom((IPolygon) geom);
      else
        this.setGeom(CommonAlgorithmsFromCartAGen
            .getBiggerFromMultiSurface((IMultiSurface<IOrientableSurface>) geom));
    }

  }
}
