/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.graph;

import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;

/*
 * ###### IGN / CartAGen ###### Title: IEdge Description: An edge of a Graph
 * object. Author: J. Renard Version: 1.0 Changes: 1.0 (13/01/12) : creation
 */

public interface IEdge {

  /**
   * The universal id of the edge
   */
  public int getId();

  public void setId(int id);

  /**
   * the possible features attached to the edge
   */
  public Set<IFeature> getGeoObjects();

  public void setGeoObjects(Set<IFeature> geoObjects);

  /**
   * the initial node of the edge
   */
  public INode getInitialNode();

  public void setInitialNode(INode node);

  /**
   * the final node of the edge
   */
  public INode getFinalNode();

  public void setFinalNode(INode node);

  /**
   * Both nodes of the edge
   */
  public Set<INode> getNodes();

  /**
   * the weight of the edge
   */
  public double getWeight();

  public void setWeight(double weight);

  /**
   * The geometry of the edge
   */
  public ICurve getGeom();

  public void setGeom(ICurve geom);

  /**
   * The graph the edge is part of
   */
  public IGraph getGraph();

  public void setGraph(IGraph graph);

  /**
   * Boolean to know if the edge is oriented or not
   */
  public boolean isOriented();

}
