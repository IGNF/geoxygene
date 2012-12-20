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

import java.util.HashSet;

import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

/*
 * ###### IGN / CartAGen ###### Title: GraphWithFaces Description: A graph with
 * its arcs, nodes and faces and methods to build from Gothic and manipulate
 * Author: G. Touya Version: 1.0 Changes: 1.0 (27/02/10) : creation
 */

public class GraphWithFaces extends Graph {

  private HashSet<Face> faces;

  public HashSet<Face> getFaces() {
    return this.faces;
  }

  public void setFaces(HashSet<Face> faces) {
    this.faces = faces;
  }

  public GraphWithFaces(String name, boolean oriented,
      HashSet<ArcReseau> linearFeatures) {
    super(name, oriented, linearFeatures);
    // TODO Auto-generated constructor stub
  }
}
