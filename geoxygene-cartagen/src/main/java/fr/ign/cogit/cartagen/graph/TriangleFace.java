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

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/*
 * ###### IGN / CartAGen ###### Title: Face Description: A face of a
 * GraphWithFaces object. Author: G. Touya Version: 1.0 Changes: 1.0 (27/02/10)
 * : creation
 */

public class TriangleFace extends Face implements ITriangleFace {

  INode node1, node2, node3;

  public TriangleFace(INode node1, INode node2, INode node3, IGraph graph,
      IPolygon geom) {
    super(graph, geom);
    this.node1 = node1;
    this.node2 = node2;
    this.node3 = node3;
  }

  public TriangleFace(INode node1, INode node2, INode node3) {
    super();
    this.node1 = node1;
    this.node2 = node2;
    this.node3 = node3;
  }

  public TriangleFace() {
    super();
  }

  @Override
  public INode getNode1() {
    return this.node1;
  }

  @Override
  public INode getNode2() {
    return this.node2;
  }

  @Override
  public INode getNode3() {
    return this.node3;
  }

}
