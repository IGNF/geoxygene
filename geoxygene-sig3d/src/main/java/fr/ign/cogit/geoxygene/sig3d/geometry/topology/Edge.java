package fr.ign.cogit.geoxygene.sig3d.geometry.topology;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.calculation.minkowskisum.VContribution.MinkowskiSum;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

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
 * Arrète dans le modèle géométrico-topologique. Utilisé pour le calcul de
 * Buffer 3D. Permet d'accéder aux faces voisines et à ses extrémités Edge of
 * the geometrico-topologic model. Used to calculate the 3D buffer
 */
public class Edge extends Vecteur {

  List<Triangle> trianglesAdjacents;

  // indique si un edge est reflex, c'est à dire si il forme un angle (entre 2
  // faces) allant vers l'intérieur de l'objet
  private boolean isReflex;

  /**
   * @return détermine si l'edge est réflexe ou nom
   */
  public boolean isReflex() {

    Triangle tri1 = this.trianglesAdjacents.get(0);
    Triangle tri2 = this.trianglesAdjacents.get(1);

    // On ordonne les triangles de manière à ce que le premier
    // contienne le dit edge dans le bon sens
    if (!tri1.containEdge(this)) {

      Triangle tTemp = tri1;
      tri1 = tri2;
      tri2 = tTemp;

    }

    Vecteur vNorm1 = tri1.getPlanEquation().getNormale();
    Vecteur vNorm2 = tri2.getPlanEquation().getNormale();

    // Un premeir test pour garder les arrètes presque colinéaires
    if (Math.abs(vNorm1.prodScalaire(vNorm2)) > 1 - MinkowskiSum.ANGULAR_TOLERANCE) {

      return false;

    }

    // On regarde l'orientation du produit vectioriel des normales par
    // rapport à l'arrète
    // Si c'est négatif, l'ordre est inversé donc l'edge est reflex
    Vecteur tempTest = vNorm1.prodVectoriel(vNorm2);
    double prodS = tempTest.prodScalaire(this);

    this.isReflex = (prodS < -MinkowskiSum.NUMERIC_TOLERANCE);

    return this.isReflex;

  }

  private Vertex vertIni;
  private Vertex vertFin;

  /**
   * @return sommet initial
   */
  public Vertex getVertIni() {
    return this.vertIni;
  }

  /**
   * @return sommet final
   */
  public Vertex getVertFin() {
    return this.vertFin;
  }

  /**
   * Construit une arrète à partir de 2 sommets
   * 
   * @param a sommet initial
   * @param b sommet final
   */
  public Edge(Vertex a, Vertex b) {

    super(a, b);
    this.vertIni = a;
    this.vertFin = b;

    this.trianglesAdjacents = this.vertIni.hasTriangleCommun(this.vertFin);

  }

  /**
   * Renvoie la somme de minkowski de 2 edges
   * 
   * @param e la seconde arrête pour le calcul de la somme
   * @return renvoie une liste de triangle correspondant au calcul de la somme
   *         de Minkowski des 2 arrètes
   */
  public ArrayList<Triangle> sumEdge(Edge e) {

    ArrayList<Triangle> triL = new ArrayList<Triangle>();

    DirectPosition p1 = this.vertIni;
    DirectPosition p2 = this.vertFin;

    DirectPosition p11 = e.getVertIni();
    DirectPosition p12 = e.getVertFin();

    Vertex som1 = new Vertex(p1.getX() + p11.getX(), p1.getY() + p11.getY(),
        p1.getZ() + p11.getZ());
    Vertex som2 = new Vertex(p1.getX() + p12.getX(), p1.getY() + p12.getY(),
        p1.getZ() + p12.getZ());

    Vertex som3 = new Vertex(p2.getX() + p11.getX(), p2.getY() + p11.getY(),
        p2.getZ() + p11.getZ());
    Vertex som4 = new Vertex(p2.getX() + p12.getX(), p2.getY() + p12.getY(),
        p2.getZ() + p12.getZ());

    triL.add(new Triangle(som1, som2, som3));
    triL.add(new Triangle(som2, som4, som3));

    return triL;

  }

  /**
   * @return la liste de triangles voisins de l'arrète
   */
  public List<Triangle> getNeighbourTriangles() {

    return this.trianglesAdjacents;

  }

  /**
   * @return une nouvelle arrète avec sommets initiaux et finaux inversés
   */
  public Edge inverse() {
    return new Edge(this.vertFin, this.vertIni);

  }

  /**
   * 
   * @return une GM_LineString correspondant à l'arrête
   */
  public ILineString getLineString() {

    DirectPositionList dpl = new DirectPositionList();
    dpl.add(this.getVertIni());
    dpl.add(this.getVertFin());

    return new GM_LineString(dpl);

  }
  
  @Override
  public boolean equals(Object obj) {
    
    if(! (obj instanceof Edge)){
      return false;
    }

      
    Edge e = (Edge) obj; 
    
    if(this.vertIni.equals(e.getVertIni()) && this.vertFin.equals(e.getVertFin())){
      return true;
    }
    
    if(this.vertIni.equals(e.getVertFin()) && this.vertFin.equals(e.getVertIni())){
      return true;
    }
    
    
    return false;
  }
  
  
  
  @Override
  public String toString(){
    return "p1 " + vertIni.toString() +"    p2   " + vertFin.toString();
  }


}
