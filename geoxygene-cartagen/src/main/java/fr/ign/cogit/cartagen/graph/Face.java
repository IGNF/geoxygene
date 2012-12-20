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

import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/*
 * ###### IGN / CartAGen ###### Title: Face Description: A face of a
 * GraphWithFaces object. Author: G. Touya Version: 1.0 Changes: 1.0 (27/02/10)
 * : creation
 */

public class Face implements IFace {

  private static AtomicInteger counter = new AtomicInteger();

  private int id;
  private IPolygon geom;
  private IGraph graph;

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public IPolygon getGeom() {
    return this.geom;
  }

  @Override
  public void setGeom(IPolygon geom) {
    this.geom = geom;
  }

  @Override
  public IGraph getGraph() {
    return this.graph;
  }

  @Override
  public void setGraph(IGraph graph) {
    this.graph = graph;
  }

  public Face(IGraph graph, IPolygon geom) {
    super();
    this.graph = graph;
    this.geom = geom;
    this.id = graph.getId() * 100000 + Face.counter.incrementAndGet();
  }

  public Face() {
    super();
    this.id = 100000 + Face.counter.incrementAndGet();
  }

  @Override
  public boolean equals(Object obj) {
    // TODO Auto-generated method stub
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return this.id;
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return super.toString();
  }

}
