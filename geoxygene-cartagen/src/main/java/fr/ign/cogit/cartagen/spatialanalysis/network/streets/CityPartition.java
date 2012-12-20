/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network.streets;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.cartagen.core.genericschema.road.IRoadStroke;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.graph.Graph;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ElementZonalReseau;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ElementZonalReseauImpl;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class CityPartition extends AbstractFeature {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static AtomicInteger counter = new AtomicInteger();
  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private int id;
  private Graph graph = null;
  private HashSet<CityPartition> neighbours;
  private double density, area;
  private StreetNetwork net;
  private IPolygon geom;
  private HashSet<IUrbanBlock> blocks;
  private double meanDegree, meanProxi, meanBetween;// centralities
  private ElementZonalReseau geoxObj;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public CityPartition(IPolygon geom, StreetNetwork net,
      HashSet<IUrbanBlock> blocks) {
    super();
    this.geom = geom;
    this.net = net;
    this.blocks = blocks;
    this.area = geom.area();
    this.neighbours = new HashSet<CityPartition>();
    this.id = CityPartition.counter.getAndIncrement();
    this.density = (this.blocks.size()) / this.area;
  }

  // Getters and setters //
  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  public Graph getGraph() {
    return this.graph;
  }

  public void setGraph(Graph graph) {
    this.graph = graph;
  }

  public HashSet<CityPartition> getNeighbours() {
    return this.neighbours;
  }

  public void setNeighbours(HashSet<CityPartition> neighbours) {
    this.neighbours = neighbours;
  }

  public double getDensity() {
    return this.density;
  }

  public void setDensity(double density) {
    this.density = density;
  }

  public double getArea() {
    return this.area;
  }

  public void setArea(double area) {
    this.area = area;
  }

  public StreetNetwork getNet() {
    return this.net;
  }

  public void setNet(StreetNetwork net) {
    this.net = net;
  }

  @Override
  public IPolygon getGeom() {
    return this.geom;
  }

  public void setGeom(IPolygon geom) {
    this.geom = geom;
  }

  public HashSet<IUrbanBlock> getBlocks() {
    return this.blocks;
  }

  public void setBlocks(HashSet<IUrbanBlock> blocks) {
    this.blocks = blocks;
  }

  public double getMeanDegree() {
    return this.meanDegree;
  }

  public void setMeanDegree(double meanDegree) {
    this.meanDegree = meanDegree;
  }

  public double getMeanProxi() {
    return this.meanProxi;
  }

  public void setMeanProxi(double meanProxi) {
    this.meanProxi = meanProxi;
  }

  public double getMeanBetween() {
    return this.meanBetween;
  }

  public void setMeanBetween(double meanBetween) {
    this.meanBetween = meanBetween;
  }

  // Other public methods //
  public void cleanPartition() {
    this.blocks.clear();
    this.geom = null;
    this.graph = null;
    this.neighbours = null;
    this.net = null;
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////
  void aggregateWithPartition(CityPartition neighbour) {
    this.area += neighbour.area;
    this.neighbours.addAll(neighbour.neighbours);
    this.neighbours.remove(this);
    this.neighbours.remove(neighbour);
    // update the neighbour link the other way round
    for (CityPartition p : neighbour.neighbours) {
      if (!this.equals(p)) {
        p.neighbours.remove(neighbour);
        p.neighbours.add(this);
      }
    }
    this.density = (this.blocks.size() + neighbour.blocks.size()) / this.area;
    this.blocks.addAll(neighbour.blocks);
    this.geom = (IPolygon) this.geom.union(neighbour.geom);
    this.geoxObj = new ElementZonalReseauImpl();
    this.geoxObj.setGeom(this.geom);
  }

  /**
   * Check if a stroke entirely crosses this partition.
   * 
   * @param s
   * @return true if the stroke crosses this
   * @author GTouya
   */
  boolean isCrossedByStroke(IRoadStroke s) {
    return CommonAlgorithmsFromCartAGen.isLineCrossingPolygon(
        s.getGeomStroke(), this.geom);
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return null;
  }

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
