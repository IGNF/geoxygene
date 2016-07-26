package fr.ign.cogit.geoxygene.sig3d.calculation.minkowskisum.VContribution;

import java.util.ArrayList;

import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Triangle;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Vertex;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 *
 * Classe permettant d'implémenter les relations FV comme décrit dans
 * Bakri[2009]
 * 
 * Barki H., Denis F, Dupont Contributing F., Vertices-based Minkowski sum of a
 * non-convex polyhedron without fold and a convex polyhedron. . Dans IEEE
 * International Conference on Shape Modeling and Applications (SMI), IEEE
 * Computer Society Press ed. Beijing, China. 2009
 * 
 */
public class RelationFV {

  private Triangle f;

  private ArrayList<Vertex> lV = new ArrayList<Vertex>();

  public RelationFV(Triangle tri) {

    this.f = tri;
  }

  public void clearLVertex() {
    this.lV.clear();

  }

  public void addVertex(Vertex s) {
    this.lV.add(s);

  }

  public Triangle getTriangle() {
    return this.f;
  }

  public ArrayList<Vertex> getLVertex() {
    return this.lV;
  }

}
