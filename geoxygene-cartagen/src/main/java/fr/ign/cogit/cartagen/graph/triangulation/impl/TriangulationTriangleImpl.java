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
package fr.ign.cogit.cartagen.graph.triangulation.impl;

import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.cartagen.graph.TriangleFace;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationTriangle;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;

/**
 * A default triangle implementation
 * 
 * @author jgaffuri
 * 
 */
public class TriangulationTriangleImpl extends TriangleFace implements
    TriangulationTriangle {

  /**
	 */
  private TriangulationPoint point1;

  /**
   * @return
   */
  @Override
  public TriangulationPoint getPoint1() {
    return this.point1;
  }

  /**
	 */
  private TriangulationPoint point2;

  /**
   * @return
   */
  @Override
  public TriangulationPoint getPoint2() {
    return this.point2;
  }

  /**
	 */
  private TriangulationPoint point3;

  /**
   * @return
   */
  @Override
  public TriangulationPoint getPoint3() {
    return this.point3;
  }

  /**
	 */
  private IPolygon geom;

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.lib.triangulation.TriangulationTriangle#getGeom
   * ()
   */
  /**
   * @return
   */
  @Override
  public IPolygon getGeom() {
    return this.geom;
  }

  /**
   * Constructor
   * 
   * @param point1
   * @param point2
   * @param point3
   */
  public TriangulationTriangleImpl(TriangulationPoint point1,
      TriangulationPoint point2, TriangulationPoint point3) {
    super(point1, point2, point3);
    this.point1 = point1;
    this.point2 = point2;
    this.point3 = point3;

    // build the triangle geometry
    DirectPositionList dpl = new DirectPositionList();
    dpl.add(point1.getPosition());
    dpl.add(point2.getPosition());
    dpl.add(point3.getPosition());
    dpl.add(point1.getPosition());
    this.geom = new GM_Triangle(new GM_Ring(new GM_LineString(dpl)));

  }

  @Override
  public INode getNode1() {
    return this.point1;
  }

  @Override
  public INode getNode2() {
    return this.point2;
  }

  @Override
  public INode getNode3() {
    return this.point3;
  }

}
