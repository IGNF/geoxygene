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

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;

public abstract class SimpleCrossRoad extends AbstractFeature {

  public SimpleCrossRoad(IPoint point) {
    super(point);
    this.id = SimpleCrossRoad.counter.getAndIncrement();
  }

  public SimpleCrossRoad(NoeudRoutier node) {
    super(node.getGeom());
    this.node = node;
    this.id = SimpleCrossRoad.counter.getAndIncrement();
  }

  private HashSet<TronconDeRoute> roads;
  private IDirectPosition coord;
  private int degree;
  private int id;
  private NoeudRoutier node;

  private static AtomicInteger counter = new AtomicInteger();

  public IDirectPosition getCoord() {
    return this.coord;
  }

  public void setCoord(IDirectPosition coord) {
    this.coord = coord;
  }

  public int getDegree() {
    return this.degree;
  }

  public void setDegree(int degree) {
    this.degree = degree;
  }

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  public static String[] getAllSubClasses() {
    String[] classes = new String[6];
    classes[0] = TCrossRoad.class.toString();
    classes[1] = YCrossRoad.class.toString();
    classes[2] = ForkCrossRoad.class.toString();
    classes[3] = PlusCrossRoad.class.toString();
    classes[4] = StarCrossRoad.class.toString();
    classes[5] = StandardCrossRoad.class.toString();
    return classes;
  }

  public void setRoads(HashSet<TronconDeRoute> roads) {
    this.roads = roads;
  }

  public HashSet<TronconDeRoute> getRoads() {
    return roads;
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    // TODO Auto-generated method stub
    return null;
  }

  public NoeudRoutier getNode() {
    return node;
  }

  public void setNode(NoeudRoutier node) {
    this.node = node;
  }
}
