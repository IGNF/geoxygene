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

import java.util.ArrayList;
import java.util.Collection;

import fr.ign.cogit.cartagen.graph.Node;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * A default point implementation
 * 
 * @author jgaffuri
 * 
 */
public class TriangulationPointImpl extends Node implements TriangulationPoint {

  /**
   * @return
   */
  @Override
  public IDirectPosition getPosition() {
    return this.getGeom().getPosition();
  }

  /**
	 */
  private int posTri = -1;

  @Override
  public int getIndex() {
    return this.posTri;
  }

  @Override
  public void setIndex(int posTri) {
    this.posTri = posTri;
  }

  /**
	 */
  IPoint geom;

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.lib.triangulation.TriangulationPoint#getGeom()
   */
  /**
   * @return
   */
  @Override
  public IPoint getGeom() {
    return this.geom;
  }

  @Override
  public void setGeom(IGeometry geom) {
    if (geom instanceof GM_Point) {
      this.geom = (IPoint) geom;
    }
  }

  /**
   * Constructor
   * 
   * @param position
   */
  public TriangulationPointImpl(IDirectPosition position) {
    super(new GM_Point(position));
    this.geom = super.getGeom();
    this.segments = new ArrayList<TriangulationSegment>();
  }

  /**
	 */
  private Collection<TriangulationSegment> segments;

  /**
   * @return The segments the point is linked to
   */
  public Collection<TriangulationSegment> getSegments() {
    return this.segments;
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.generalisation.lib.triangulation.TriangulationPoint#
   * isLinkedBySegment
   * (fr.ign.cogit.generalisation.lib.triangulation.TriangulationPoint)
   */
  @Override
  public boolean isLinkedBySegment(TriangulationPoint point) {
    for (TriangulationSegment seg : this.segments) {
      if (seg.getPoint1() == this && seg.getPoint2() == point
          || seg.getPoint2() == this && seg.getPoint1() == point) {
        return true;
      }
    }
    return false;
  }
}
