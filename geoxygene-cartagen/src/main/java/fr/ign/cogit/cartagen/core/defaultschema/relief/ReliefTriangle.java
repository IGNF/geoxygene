/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.relief;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjSurfDefault;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefTriangle;
import fr.ign.cogit.cartagen.graph.IGraph;
import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.cartagen.graph.Node;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/*
 * ###### IGN / CartAGen ###### Title: ReliefElementPoint Description: Points
 * cotés Author: J. Renard Date: 30/06/2010
 */

public class ReliefTriangle extends GeneObjSurfDefault implements
    IReliefTriangle {

  INode pt1, pt2, pt3;

  @Override
  public INode getPt1() {
    return this.pt1;
  }

  @Override
  public INode getPt2() {
    return this.pt2;
  }

  @Override
  public INode getPt3() {
    return this.pt3;
  }

  /**
   * Constructor
   */
  public ReliefTriangle(IDirectPosition pt1, IDirectPosition pt2,
      IDirectPosition pt3) {
    super();
    this.pt1 = new Node(new GM_Point(pt1));
    this.pt2 = new Node(new GM_Point(pt2));
    this.pt3 = new Node(new GM_Point(pt3));
    this.setGeom(new GM_Triangle(pt1, pt2, pt3));
    this.setInitialGeom(new GM_Triangle(pt1, pt2, pt3));
    this.setEliminated(false);
  }

  @Override
  public INode getNode1() {
    return this.pt1;
  }

  @Override
  public INode getNode2() {
    return this.pt2;
  }

  @Override
  public INode getNode3() {
    return this.pt3;
  }

  @Override
  public void setGeom(IPolygon geom) {
    super.setGeom(geom);
  }

  @Override
  public IGraph getGraph() {
    return null;
  }

  @Override
  public void setGraph(IGraph graph) {
  }

}
