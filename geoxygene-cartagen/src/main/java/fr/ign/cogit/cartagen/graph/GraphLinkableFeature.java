/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.cartagen.graph;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_Feature;

/**
 * A feature linked to a point agent
 * @author JGaffuri
 */
public class GraphLinkableFeature extends FT_Feature implements
    IGraphLinkableFeature {

  private IGeneObj feature;

  @Override
  public IGeneObj getFeature() {
    return this.feature;
  }

  /**
   * the referent graph node attached to the graphLinkableFeature
   */
  private INode referentGraphNode = null;

  @Override
  public INode getReferentGraphNode() {
    return this.referentGraphNode;
  }

  @Override
  public void setReferentGraphNode(INode referentGraphNode) {
    this.referentGraphNode = referentGraphNode;
  }

  /**
   * the proximity segments in the graph
   */
  private ArrayList<IEdge> proximitySegments = new ArrayList<IEdge>();

  @Override
  public ArrayList<IEdge> getProximitySegments() {
    return this.proximitySegments;
  }

  /**
   * Constructor
   * @param feature
   */
  public GraphLinkableFeature(IGeneObj feature) {
    this.feature = feature;
    feature.setLinkableFeature(this);
  }

  @Override
  public void clean() {
    this.getProximitySegments().clear();
    this.setReferentGraphNode(null);
  }

  @Override
  public IGeometry getGeom() {
    return this.getFeature().getGeom();
  }

  @Override
  public void setGeom(IGeometry g) {
    this.getFeature().setGeom(g);
  }

  @Override
  public IGeometry getSymbolGeom() {
    return this.getFeature().getSymbolGeom();
  }

  @Override
  public double getSymbolArea() {
    return this.getSymbolGeom().area();
  }

  @Override
  public boolean isDeleted() {
    return this.getFeature().isDeleted();
  }

  @Override
  public int getId() {
    return this.getFeature().getId();
  }

  @Override
  public void setId(int Id) {
    this.getFeature().setId(Id);
  }

  @Override
  public IPopulation<? extends IFeature> getPopulation() {
    return this.getFeature().getPopulation();
  }

  @Override
  public void setDeleted(boolean deleted) {
    this.getFeature().setDeleted(deleted);
  }

}
