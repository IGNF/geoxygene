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

import fr.ign.cogit.cartagen.graph.Edge;
import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;

/**
 * A default segment implementation
 * 
 * @author jgaffuri
 * 
 */
public class TriangulationSegmentImpl extends Edge implements
    TriangulationSegment {

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
  private ILineSegment geom;

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.lib.triangulation.TriangulationSegment#getGeom
   * ()
   */
  /**
   * @return
   */
  @Override
  public ILineSegment getGeom() {
    return this.geom;
  }

  /**
   * Constructor
   * 
   * @param point1
   * @param point2
   */
  public TriangulationSegmentImpl(TriangulationPoint point1,
      TriangulationPoint point2) {
    super(point1, point2);
    this.point1 = point1;
    this.point2 = point2;

    ((TriangulationPointImpl) point1).getSegments().add(this);
    ((TriangulationPointImpl) point2).getSegments().add(this);

    // build the segment geometry
    this.geom = new GM_LineSegment(point1.getPosition(), point2.getPosition());
    super.setGeom(this.geom);
    super.setWeight(this.geom.length());
  }

  @Override
  public INode getInitialNode() {
    return this.point1;
  }

  @Override
  public INode getFinalNode() {
    return this.point2;
  }

}
