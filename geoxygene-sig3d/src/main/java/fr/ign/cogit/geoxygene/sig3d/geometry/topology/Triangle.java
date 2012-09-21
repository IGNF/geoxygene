package fr.ign.cogit.geoxygene.sig3d.geometry.topology;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.calculation.buffer3d.VContribution.MinkowskiSum;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
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
 *          Classe triangle utilisée dans le modèle topologique issue de la
 *          tétraédrisation Class of triangle used in the geometrico-topologic
 *          model
 * 
 */
public class Triangle extends GM_Triangle {



  /**
   * crée un triangle et affecte les sommes si ils sont au nombre de 3 sinon
   * renvoie un objet vide
   * 
   * @param listeSommets
   */
  public Triangle(Vertex[] listeSommets) {
    super(listeSommets[0], listeSommets[1], listeSommets[2]);
    if (listeSommets.length == 3) {
      this.lVertices = listeSommets;
    }
  
  }

  /**
   * créer un triangle à partir de 3 sommets
   * 
   * @param sommet1
   * @param sommet2
   * @param sommet3
   */
  public Triangle(Vertex sommet1, Vertex sommet2, Vertex sommet3) {
    super(sommet1, sommet2, sommet3);
    this.lVertices[0] = sommet1;
    this.lVertices[1] = sommet2;
    this.lVertices[2] = sommet3;
  }

  public Triangle() {
    // TODO Auto-generated constructor stub
  }

  public final double TOLERANCE = 0.001;

  private PlanEquation eq = null;

  private Box3D box = null;

  /**
   * @return plan d'équation d'un triangle
   */
  public PlanEquation getPlanEquation() {
    if (this.eq == null) {

      this.eq = new PlanEquation(this.lVertices[0], this.lVertices[1],
          this.lVertices[2]);
    }

    return this.eq;

  }

  private Vertex[] lVertices = new Vertex[3];

  public IDirectPosition getCenter(){
    
    
    double x = (this.lVertices[0].getX() + this.lVertices[1].getX() + this.lVertices[2].getX() )/3;
    double y = (this.lVertices[0].getY() + this.lVertices[1].getY() + this.lVertices[2].getY() )/3;
    double z = (this.lVertices[0].getZ() + this.lVertices[1].getZ() + this.lVertices[2].getZ() )/3;
    
    
    return new DirectPosition(x,y,z);
    
  }

  /**
   * @return le tableau des sommets
   */
  public Vertex[] getLVertices() {
    return this.lVertices;
  }

  /**
   * @param sommets le nouveau tableau de sommets
   */
  public void setLSommets(Vertex[] sommets) {
    this.lVertices = sommets;
  }

  /**
   * Convertit un triangle en géométrie ISO
   * 
   * @return une surface GeOxygene
   */
  public GM_OrientableSurface toGeoxygeneSurface() {

    DirectPositionList triangle = new DirectPositionList();
    triangle.add(this.lVertices[0]);

    triangle.add(this.lVertices[1]);
    triangle.add(this.lVertices[2]);
    triangle.add(this.lVertices[0]);

    GM_LineString ls = new GM_LineString(triangle);
    GM_Ring tri = new GM_Ring(ls);

    return new GM_Polygon(tri);
  }

  /**
   * Translate un triangle suivant un vecteur
   * 
   * @param v vecteur dont on translate le triangle
   * @return un nouveau triangle translaté de v
   */
  public Triangle translateTriangle(Vecteur v) {

    return this.translateTriangle(v.getX(), v.getY(), v.getZ());
  }

  /**
   * Translate un triangle suivant un vecteur x,y,z
   * 
   * @param x
   * @param y
   * @param z
   * @return un nouveau triangle translaté de x,y,z
   */
  public Triangle translateTriangle(double x, double y, double z) {

    Vertex s1 = new Vertex(this.lVertices[0].getX() + x,
        this.lVertices[0].getY() + y, this.lVertices[0].getZ() + z);
    Vertex s2 = new Vertex(this.lVertices[1].getX() + x,
        this.lVertices[1].getY() + y, this.lVertices[1].getZ() + z);
    Vertex s3 = new Vertex(this.lVertices[2].getX() + x,
        this.lVertices[2].getY() + y, this.lVertices[2].getZ() + z);

    return new Triangle(s1, s2, s3);
  }

  /**
   * Translate un triangle suivant un sommet
   * 
   * @param dp le vecteur dont on translate le triangle
   * @return un triangle translaté suivant le vecteur dp
   */
  public Triangle translateTriangle(DirectPosition dp) {

    return this.translateTriangle(dp.getX(), dp.getY(), dp.getZ());

  }

  ArrayList<Edge> edgeContenus = null;

  /**
   * Permet de calculer les différentes arrètes autour d'un triangle Les arrètes
   * sont orientés et la géométries àtant 1 Manifold à la base Chaque arrète est
   * unique (compte tenue de son orientation)
   * 
   * @return une liste d'arrètes bordant le triangle
   */
  public List<Edge> calculEdge() {

    if (this.edgeContenus == null) {

      this.edgeContenus = new ArrayList<Edge>();
      this.edgeContenus.add(new Edge(this.lVertices[0], this.lVertices[1]));
      this.edgeContenus.add(new Edge(this.lVertices[1], this.lVertices[2]));
      this.edgeContenus.add(new Edge(this.lVertices[2], this.lVertices[0]));

    }

    return this.edgeContenus;

  }

  /**
   * Calcule une somme de minkoswki entre un triangle et un vecteur
   * 
   * @param edge le vecteur sur lequel on calcul la somme de Minkowski
   * @return les triangles issus de la somme
   */
  public ArrayList<Triangle> minkoVecteur(Edge edge) {

    ArrayList<Triangle> lTrianglesResultats = new ArrayList<Triangle>();
    IDirectPosition centre = this.Barycentre();

    double normeEdge = edge.norme();

    double cos = 0;
    int indice = 0;

    for (int i = 0; i < 3; i++) {

      Vecteur vTemp = new Vecteur(this.lVertices[i], centre);

      double cosTemp = vTemp.prodScalaire(edge) / (normeEdge * vTemp.norme());

      // Cosinus plus petit on continue
      if (Math.abs(cosTemp) < Math.abs(cos)) {

        continue;
      }

      cos = cosTemp;
      indice = i;

    }

    // Cos ne devrait pas pouvoir être nul

    Vecteur dp1;
    Vecteur dp2;

    // Sinon ca signifie la prèsence d'un triangle plat
    if (cos < 0) {
      dp1 = new Vecteur(edge.getVertIni());
      dp2 = new Vecteur(edge.getVertFin());

    } else {

      dp1 = new Vecteur(edge.getVertFin());
      dp2 = new Vecteur(edge.getVertIni());

    }

    // Cas ou la pointe est dans le sens opposé au vecteur
    lTrianglesResultats.add(this.translateTriangle(dp1));

    Vertex somIni1, SomIni2;

    if (indice == 0) {

      somIni1 = this.lVertices[1];
    } else {
      somIni1 = this.lVertices[0];

    }

    if (indice != 2) {

      SomIni2 = this.lVertices[2];
    } else {
      SomIni2 = this.lVertices[1];

    }

    Vertex som1, som2, som3, som4;

    som1 = new Vertex((dp1.translate(somIni1)).getCoordinate());
    som2 = new Vertex((dp1.translate(SomIni2)).getCoordinate());
    som3 = new Vertex((dp2.translate(somIni1)).getCoordinate());
    som4 = new Vertex((dp2.translate(SomIni2)).getCoordinate());

    lTrianglesResultats.add(new Triangle(som1, som2, som3));
    lTrianglesResultats.add(new Triangle(som2, som4, som3));

    return lTrianglesResultats;

  }

  /**
   * @return le barycentre du triangle
   */
  public IDirectPosition Barycentre() {

    double x = this.lVertices[0].getX() + this.lVertices[1].getX()
        + this.lVertices[2].getX();
    double y = this.lVertices[0].getY() + this.lVertices[1].getY()
        + this.lVertices[2].getY();
    double z = this.lVertices[0].getZ() + this.lVertices[1].getZ()
        + this.lVertices[2].getZ();

    return new DirectPosition(x / 3, y / 3, z / 3);

  }

  /**
   * Vérifie si une arrête est contenu dans un triangle mais, ne vérifie que
   * l'égalité des objets
   * 
   * @param e une arrête dont on veut savoir si elle est contenu dans le
   *          triangle
   * @return indique si l'arrête est contenu dans un triangle
   */
  public boolean containEdge(Edge e) {

    return this.edgeContenus.contains(e);

  }

  /**
   * Vérifie si une arrête est contenu dans un triangle mais, vérifie sommet par
   * sommet
   * 
   * @param e une arrête dont on veut savoir si elle est contenu dans le
   *          triangle
   * @return indique si l'arrête est contenu dans un triangle
   */
  public boolean contientEdge(Edge e) {

    List<Edge> lEdge = this.calculEdge();

    for (int i = 0; i < 3; i++) {
      Edge eTemp = lEdge.get(i);

      if (e.getVertFin().equals(eTemp.getVertFin())
          && e.getVertIni().equals(eTemp.getVertIni())) {

        return true;
      }

    }

    return false;

  }

  /**
   * Fonction permettant de trouver le point d'intersection entre une droite et
   * une triangle
   * 
   * @param e l'arrête permettant de définir la droite
   * @return un sommet comme intersection d'un droite et d'un triangle. Renvoie
   *         null si il n'y a pas d'intersection
   */
  public Vertex intersection(Edge e) {
    // l.start + lambda *l.dir = point on plane

    double D = this.getPlanEquation().getCoeffd();
    Vecteur normal = this.getPlanEquation().getNormale();

    Vertex somIni = e.getVertIni();

    double lambda = (D - normal.prodScalaire(new Vecteur(somIni)))
        / e.prodScalaire(normal);

    // Is point on plane is between start and endpoint of line?
    // start + lambda * length = Point on top of triangle
    // --> lambda > 1 end point of line before intersection point
    // --> lambda < 0 intersection before start point
    if (lambda >= 0 - this.TOLERANCE && lambda <= 1 + this.TOLERANCE) {

      Vertex result = new Vertex(somIni.getX() + lambda * e.getX(),
          somIni.getY() + lambda * e.getY(), somIni.getZ() + lambda * e.getZ()

      );

      if (!this.liesInside(result)) {
        return null;
      }

      return result;
    }

    // Has no intersection Point
    return null;
  }

  /**
   * Vérifie si un point se trouve dans un triangle (en 3D)
   * 
   * @param p le point que l'on test
   * @return indique si il se trouve dans le triangle
   */
  public boolean liesInside(IDirectPosition p) {

    Vecteur vec1 = new Vecteur(p, this.lVertices[1]);
    Vecteur vec2 = new Vecteur(p, this.lVertices[2]);
    Vecteur vec3 = new Vecteur(p, this.lVertices[3]);

    Vecteur pdVec1 = vec1.prodVectoriel(vec2);
    Vecteur pdVec2 = vec2.prodVectoriel(vec3);
    Vecteur pdVec3 = vec3.prodVectoriel(vec1);

    double prodS1 = pdVec1.prodScalaire(pdVec2);
    double prodS2 = pdVec2.prodScalaire(pdVec3);
    double prodS3 = pdVec3.prodScalaire(pdVec1);

    return (prodS1 * prodS2 * prodS3 > MinkowskiSum.NUMERIC_TOLERANCE);

  }

  /**
   * Calcul l'intersection entre 2 triangles TODO : finish this funtion
   * 
   * @param t
   */
  @Deprecated
  public void cut(Triangle t) {

    if (!this.box.intersect(t.getBox3D())) {
      return;
    }

    DirectPosition ips[] = new DirectPosition[2];
    int index = 0;

    DirectPosition singleIP = null;

    for (int i = 0; i < 3; i++) {
      DirectPosition ip = t.intersection(this.edgeContenus.get(i));

      if (ip != null) {
        if (singleIP == null || !singleIP.equals(ip, this.TOLERANCE)) {
          singleIP = ip;
          ips[index++] = ip;
          if (index > 1) {
            break;
          }
        }
      }

    }

    // lines of t gets intersected with this plane
    // Check for intersection points inside, if necessary
    if (index < 2) {
      for (int i = 0; i < 3; i++) {
        DirectPosition ip = this.intersection(this.edgeContenus.get(i));
        if (ip != null) {
          if (singleIP == null || !singleIP.equals(ip, this.TOLERANCE)) {
            ips[index++] = ip;
            if (index > 1) {
              break;
            }
          }
        }
      }
    }
    if (index == 2) {
      // cutline.add(new UVLine(calculateUVCoord(ips[0]),
      // calculateUVCoord(ips[1])));
    }
  }

  /**
   * Translate les sommets contenu dans e et appartenant au triangle d'un
   * vecteur v
   * 
   * @param e l'arrête dont on teste les somemet
   * @param v le vecteur dont on effectue une translation
   * @return un nouveau triangle dont les sommets contenu dans e ont été
   *         translatés de v
   */
  public Triangle translateEdge(Edge e, Vecteur v) {

    Vertex[] lSom = new Vertex[3];

    for (int i = 0; i < 3; i++) {

      if (e.getVertIni().equals(this.lVertices[i], 0.01)) {

        lSom[i] = new Vertex((DirectPosition) v.translate(this.lVertices[i]));

        continue;
      }

      if (e.getVertFin().equals(this.lVertices[i], 0.01)) {
        lSom[i] = new Vertex((DirectPosition) v.translate(this.lVertices[i]));
        continue;
      }

      lSom[i] = this.lVertices[i];

    }

    return new Triangle(lSom);

  }

  /**
   * Translate les sommets contenu dans s et appartenant au triangle d'un
   * vecteur v
   * 
   * @param s le vecteur dont on teste les somemet
   * @param v le vecteur dont on effectue une translation
   * @return un nouveau triangle dont les sommets contenu dans s ont été
   *         translatés de v
   */
  public Triangle translateSommet(Vertex s, Vecteur v) {

    Vertex[] lSom = this.lVertices.clone();

    for (int i = 0; i < 3; i++) {

      if (lSom[i].equals(s)) {

        lSom[i] = new Vertex((DirectPosition) v.translate(lSom[i]));
        break;

      }

    }

    return new Triangle(lSom);

  }

  /**
   * Vérifie l'égalité entre 2 triangles en testant sommet par sommet
   * 
   * @param t le triangle dont on teste l'égalité
   * @return indique l'égalité des triangles
   */
  @Override
  public boolean equals(Object o) {
    

    if(!( o instanceof ITriangle )){
      
      
      return false;
    }
    


    ITriangle t = (ITriangle) o;
    
    IDirectPositionList som = t.coord();

    bouclei: for (int i = 0; i < 3; i++) {

      Vertex som1 = this.lVertices[i];

      for (int j = 0; j < 3; j++) {

        IDirectPosition som2 = som.get(i);

        if (som2 == null) {

          continue;
        }

        if (som1.equals(som2, 0.01)) {
          // On le vire pour àviter de trouver 3 fois la même
          // coordonnées (triangle mal formé)

          continue bouclei;

        }

      }
      // Si on trouve un point correspondant, on ne sort jamais de la
      // boucle j
      return false;

    }

    return true;

  }

  /**
   * Calcul dune boite 3D du triangle
   * 
   * @return une boite englobant le triangle
   */
  public Box3D getBox3D() {
    Vertex[] som = this.getLVertices();

    double xmin = Double.POSITIVE_INFINITY;
    double ymin = Double.POSITIVE_INFINITY;
    double zmin = Double.POSITIVE_INFINITY;
    double xmax = Double.NEGATIVE_INFINITY;
    double ymax = Double.NEGATIVE_INFINITY;
    double zmax = Double.NEGATIVE_INFINITY;

    for (int i = 0; i < 3; i++) {

      DirectPosition dp = som[i];
      xmin = Math.min(xmin, dp.getX());
      ymin = Math.min(ymin, dp.getY());
      zmin = Math.min(zmin, dp.getZ());

      xmax = Math.max(xmax, dp.getX());
      ymax = Math.max(ymax, dp.getY());
      zmax = Math.max(zmax, dp.getZ());

    }
    return new Box3D(

    new DirectPosition(xmin, ymin, zmin),

    new DirectPosition(xmax, ymax, zmax));

  }

  public boolean intersects(Triangle t) {

    for (int i = 0; i < 3; i++) {

      IDirectPosition dp = t.getPlanEquation().intersectionLinePlan(
          this.coord().get(i), this.coord().get(i + 1));

      if (t.liesInside(dp)) {

        return true;

      }

    }

    for (int i = 0; i < 3; i++) {

      IDirectPosition dp = this.getPlanEquation().intersectionLinePlan(
          t.coord().get(i), t.coord().get(i + 1));

      if (this.liesInside(dp)) {

        return true;

      }

    }

    return false;
  }

  /*
   * public boolean lieInsideRing(IDirectPosition dp) {
   * 
   * IDirectPositionList dpl = this.coord();
   * 
   * Vecteur normal = (new PlanEquation(dpl)).getNormale();
   * 
   * normal.normalise(); // Pour tous les points on mesure l'angle entre le
   * centre du point // candidat // et les sommets du polygone // PAs 0 = objet
   * à l'intérieur // Porduit mixte pour calculer tout cela int nbP =
   * dpl.size();
   * 
   * if (dpl.get(0).distance(dpl.get(nbP - 1)) != 0) { dpl.add(dpl.get(0)); }
   * 
   * Vecteur vPred, vActu;
   * 
   * vPred = new Vecteur(dp, dpl.get(0)); vPred.normalise();
   * 
   * double angleTotal = 0;
   * 
   * for (int i = 1; i < nbP; i++) { vActu = new Vecteur(dp, dpl.get(i));
   * vActu.normalise();
   * 
   * double cos = vPred.prodScalaire(vActu); double sin =
   * vPred.prodVectoriel(vActu).prodScalaire(normal);
   * 
   * double angle = Math.acos(cos);
   * 
   * if (sin < 0) {
   * 
   * angle = -angle;
   * 
   * }
   * 
   * angleTotal = angleTotal + angle;
   * 
   * vPred = vActu; }
   * 
   * return (Math.abs(angleTotal) > MinkowskiSum.NUMERIC_TOLERANCE);
   * 
   * }
   */

  public Vecteur getNormal() {

    return this.getPlanEquation().getNormale();
  }

  public void reversePoints() {

    Vertex vTemp = this.lVertices[0];

    this.lVertices[0] = this.lVertices[2];
    this.lVertices[2] = vTemp;

    this.edgeContenus = null;
  }

  List<Triangle> neighBourTriangles = null;

  public List<Triangle> getNeighBourTriangles() {

    if (neighBourTriangles == null) {
      neighBourTriangles = new ArrayList<Triangle>();

      for (Edge e : this.calculEdge()) {

        List<Triangle> lTriTemp = e.getNeighbourTriangles();

        for (Triangle triTemp : lTriTemp) {

          if (!triTemp.equals((Object)this)) {

            if (!neighBourTriangles.contains(triTemp)) {
              neighBourTriangles.add(triTemp);
            }

          }

        }

      }

    }

    return neighBourTriangles;

  }
}
