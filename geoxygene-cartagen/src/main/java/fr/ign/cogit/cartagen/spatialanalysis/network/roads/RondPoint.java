/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network.roads;

import java.util.Collection;
import java.util.HashSet;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.CarrefourComplexeImpl;

public class RondPoint extends CarrefourComplexeImpl {
  private double diameter;
  private int nbLegs;
  private HashSet<PatteOie> branchings;

  public double getDiameter() {
    return this.diameter;
  }

  public void setDiameter(double diameter) {
    this.diameter = diameter;
  }

  public int getNbLegs() {
    return this.nbLegs;
  }

  public void setNbLegs(int nbLegs) {
    this.nbLegs = nbLegs;
  }

  public HashSet<PatteOie> getBranchings() {
    return this.branchings;
  }

  public void setBranchings(HashSet<PatteOie> branchings) {
    this.branchings = branchings;
  }

  /**
   * Get the roundabout area from its geometry.
   * @return
   */
  public double getArea() {
    return this.getGeom().area();
  }

  /**
   * <p>
   * Determine if a road block object is a roundabout or not. Uses the
   * compactness Miller index measure to determine if the block is round shaped
   * with a compectness threshold of 0,97 (determined empirically) and a size
   * threshold as parameter.
   * 
   * @param block : the tested road block
   * @param sizeThreshold : the maximum area of a roundabout (40000 m² advised)
   * @return true if the block is a roundabout.
   * 
   */
  public static boolean isRoundAbout(Ilot block, double sizeThreshold) {
    // get the block geometry
    IPolygon geom = (IPolygon) block.getGeom();
    // get the area and perimeter of the geometry
    double area = geom.area();
    double perimeter = geom.perimeter();

    // if area is > to threshold, return false
    if (area > sizeThreshold) {
      return false;
    }

    // now check that the shape is rounded
    // then, we measure the Miller index on the geometry :
    // with A(area) et P(perimeter), the index is 4*Pi*S/P²
    double millerIndex = 4 * java.lang.Math.PI * area / (perimeter * perimeter);

    // if millerIndex < 0,97, it is not a roundabout (Sheeren, 2004)
    if (millerIndex < 0.95) {
      /** @author mdumont test valeur **/
      return false;
    }

    // else, the block is a roundabout
    return true;
  }

  /**
   * Constructor from the complete components of a characterised roundabout.
   * 
   * @param geom the round geometry of the roundabout
   * @param simples the simple crossroads contained in the roundabout
   * @param internalRoads the roads inside the roundabout
   * @param externalRoads the roads allowing access to the roundabout
   * @param branchings the branching crossroads connected to the roundabout
   * @author GTouya
   */
  public RondPoint(IPolygon geom, Collection<NoeudReseau> simples,
      HashSet<TronconDeRoute> internalRoads,
      HashSet<TronconDeRoute> externalRoads, HashSet<PatteOie> branchings) {
    this.setGeom(geom);
    this.setRoutesExternes(externalRoads);
    this.setRoutesInternes(internalRoads);
    this.setNoeuds(simples);
    this.branchings = branchings;
    double perimeter = geom.perimeter();
    this.diameter = perimeter / Math.PI;
    this.nbLegs = externalRoads.size();
  }

  /**
   * Constructor from a block that represents a roundabout (isRoundAbout returns
   * true). The characterisation of the roundabout is made based on the block
   * geometry and topology.
   * 
   * @param block the block that is a roundabout
   * @param roads the roads from the network used here
   * @param crossRoads the crossroads from the network used here
   * @author GTouya
   */
  public RondPoint(Ilot block, IFeatureCollection<TronconDeRoute> roads,
      IFeatureCollection<NoeudRoutier> crossRoads) {
    this.setGeom(block.getGeom());
    this.setRoutesExternes(new HashSet<TronconDeRoute>());
    this.setRoutesInternes(new HashSet<TronconDeRoute>());
    this.setNoeuds(new HashSet<NoeudReseau>());
    this.branchings = new HashSet<PatteOie>();
    double perimeter = ((IPolygon) block.getGeom()).perimeter();
    this.diameter = perimeter / Math.PI;

    // characterise the new roundabout
    IGeometry blockGeom = block.getGeom();
    this.getNoeuds().addAll(crossRoads.select(blockGeom));
    Collection<TronconDeRoute> roundRoads = roads.select(blockGeom);
    // loop on the roundRoads to find out if they are internal or external to
    // the roundabout
    for (TronconDeRoute r : roundRoads) {
      if (block.getGeom().buffer(0.5).contains(r.getGeom())) {
        this.getRoutesInternes().add(r);
      } else {
        this.getRoutesExternes().add(r);
      }
    }
  }

  /**
   * The method detects the branching crossroads connected to a roundabout. They
   * are added to the branching crossroads set and the internal and external
   * roads of the roundabout are updated.
   * 
   * @param allBranchings all the branching crossroads of the network used here
   * @author GTouya
   */
  public void addBranchingCrossRoads(IFeatureCollection<PatteOie> allBranchings) {
    this.branchings.addAll(allBranchings.select(this.getGeom()));

    for (PatteOie b : this.branchings) {
      b.setRoundAbout(this);
      this.getRoutesInternes().addAll(b.getRoutesInternes());
      this.getRoutesExternes().removeAll(b.getRoutesInternes());
      HashSet<TronconDeRoute> exts = b.getRoutesExternes();
      exts.removeAll(this.getRoutesInternes());
      this.getRoutesExternes().addAll(exts);
    }
  }

  public static RondPoint getRoundabout(Ilot block, Collection<RondPoint> rounds) {
    for (RondPoint r : rounds) {
      if (block.getGeom().equals(r.getGeom())) {
        return r;
      }
    }
    return null;
  }
}
