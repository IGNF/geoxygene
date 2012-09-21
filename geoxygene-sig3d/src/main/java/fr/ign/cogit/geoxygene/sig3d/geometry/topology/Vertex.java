package fr.ign.cogit.geoxygene.sig3d.geometry.topology;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

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
 * Sommet utilisé dans le modèle géométrico-topologique. Permet d'accéder aux
 * triangles dont il est le sommet Vertex used in the geometrico-topologic
 * model. It can acess to every related triangles.
 */
public class Vertex extends DirectPosition {

  /**
   * Contient la liste des triangles passant par le sommet
   */
  ArrayList<Triangle> lTriRel = new ArrayList<Triangle>();

  /**
   * Rajoute un triangle à la liste de triangles adjacents
   */
  public void ajouteTriangle(Triangle tri) {
    this.lTriRel.add(tri);

  }

  /**
   * Création d'un sommet à partir de 3 coordonnées
   * 
   * @param x
   * @param y
   * @param z
   */
  public Vertex(double x, double y, double z) {

    super(x, y, z);
  }

  /**
   * Création d'un sommet à partir d'un directposition
   * 
   * @param dp
   */
  public Vertex(IDirectPosition dp) {

    super(dp.getX(), dp.getY(), dp.getZ());
  }

  /**
   * Création d'un sommet à partir d'un tableau de double
   * 
   * @param coord
   */
  public Vertex(double[] coord) {

    super(coord);
  }

  /**
   * @return La liste des triangles liés au sommet
   */
  public List<Triangle> getLTRiRel() {

    return this.lTriRel;
  }

  /**
   * Permet de déterminer les triangles communs de 2 sommets On considère que
   * chaque triangle est unique en mémoire
   * 
   * @param som un sommet dont on cherche les triangles communs
   * @return les triangles communs aux deux somemts
   */
  public List<Triangle> hasTriangleCommun(Vertex som) {
    List<Triangle> lTriFinal = new ArrayList<Triangle>();

    List<Triangle> lTriangle1 = som.getLTRiRel();

    int nbElem = this.lTriRel.size();

    for (int i = 0; i < nbElem; i++) {

      Triangle tTemp = this.lTriRel.get(i);

      if (lTriangle1.indexOf(tTemp) != -1) {

        lTriFinal.add(tTemp);

      }

    }

    return lTriFinal;

  }

  @Override
  public boolean equals(Object o) {
    
    if(! (o instanceof Vertex)){
      return false;
    }
    

    return super.equals((Vertex)o);
  }
  

}
