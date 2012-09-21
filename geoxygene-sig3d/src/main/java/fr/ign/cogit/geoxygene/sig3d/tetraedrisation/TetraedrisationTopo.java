package fr.ign.cogit.geoxygene.sig3d.tetraedrisation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Edge;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Tetraedre;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Triangle;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Vertex;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

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
 * @author Bonin
 * @version 0.1
 * 
 * Permet d'effectuer la décomposition d'objets en utilisant une topologie (voir
 * package fr.ign.cogit.geoxygene.sig3d.geometry.topology). Fonnctionne de la
 * même manière que la classe Tetraedrisation Decomposition with topology
 * (package fr.ign.cogit.geoxygene.sig3d.geometry.topology)
 */
public class TetraedrisationTopo extends Tetraedrisation {

  /**
   * @return liste des tetraèdres générés
   */
  public List<Tetraedre> getlTetraedres() {
    return this.lTetraedres;
  }

  private final static Logger logger = Logger
      .getLogger(TetraedrisationTopo.class.getName());

  // Décomposition en termes de sommet triangle et arrètes
  private List<Vertex> lVertex = new ArrayList<Vertex>();
  private List<Triangle> lTriangles = new ArrayList<Triangle>();
  private List<Tetraedre> lTetraedres = new ArrayList<Tetraedre>();
  private List<Edge> lEdges = new ArrayList<Edge>();

  /**
   * @param feat constructeur à partir d'une entité
   */
  public TetraedrisationTopo(IFeature feat) {
    super(feat);
  }

  /**
   * @return liste des triangles générés
   */
  public List<Triangle> getLTriangles() {
    return this.lTriangles;

  }

  /**
   * @return liste des arrêtes générées
   */
  public List<Edge> getLlEdges() {
    return this.lEdges;

  }

  /**
   * @return liste des sommets générés
   */
  public List<Vertex> getLVertex() {
    return this.lVertex;

  }

  /**
   * Conversion du résultat de la tétraèdrisation en géométrie lisible
   * 
   * @param mesh true pour avoir une surface triangulée, false pour avoir des
   *          tetraèdres
   */
  protected void convertJout(boolean mesh) {
    try {
      // On charge les sommets
      int nbPoints = this.jTetgenioOut.numberofpoints;
      List<Vertex> lPoints = new ArrayList<Vertex>(nbPoints);

      for (int i = 0; i < nbPoints; i++) {
        Vertex dp = new Vertex(this.jTetgenioOut.pointlist[3 * i],
            this.jTetgenioOut.pointlist[3 * i + 1],
            this.jTetgenioOut.pointlist[3 * i + 2]);
        lPoints.add(dp);
        this.lVertex.add(dp);
      }

      // On crée les triangles
      for (int i = 0; i < this.jTetgenioOut.numberoftrifaces; i++) {

        Vertex n0 = lPoints.get(this.jTetgenioOut.trifacelist[3 * i]);
        Vertex n2 = lPoints.get(this.jTetgenioOut.trifacelist[3 * i + 1]);
        Vertex n1 = lPoints.get(this.jTetgenioOut.trifacelist[3 * i + 2]);

        DirectPositionList triangle = new DirectPositionList();
        triangle.add(n0);
        triangle.add(n1);
        triangle.add(n2);
        triangle.add(n0);

        Vertex[] lSommets = new Vertex[3];

        lSommets[0] = n0;
        lSommets[1] = n1;
        lSommets[2] = n2;

        Triangle t = new Triangle(lSommets);

        n0.ajouteTriangle(t);
        n1.ajouteTriangle(t);
        n2.ajouteTriangle(t);

        this.lTriangles.add(t);

      }

      // On ne s'intéresse qu'a la surface triangulée
      if (mesh) {

        return;
      }

      // On crée les tétraèdres
      int nbTetra = this.jTetgenioOut.numberoftetrahedra;

      for (int i = 0; i < nbTetra; i++) {

        Vertex n0 = lPoints.get(this.jTetgenioOut.tetrahedronlist[4 * i]);
        Vertex n1 = lPoints.get(this.jTetgenioOut.tetrahedronlist[4 * i + 1]);
        Vertex n2 = lPoints.get(this.jTetgenioOut.tetrahedronlist[4 * i + 2]);
        Vertex n3 = lPoints.get(this.jTetgenioOut.tetrahedronlist[4 * i + 3]);

        Triangle tri1 = new Triangle(n2, n1, n0);
        Triangle tri2 = new Triangle(n1, n2, n3);
        Triangle tri3 = new Triangle(n0, n3, n2);
        Triangle tri4 = new Triangle(n3, n0, n1);

        ArrayList<Triangle> lTriangles = new ArrayList<Triangle>(4);
        lTriangles.add(tri1);
        lTriangles.add(tri2);
        lTriangles.add(tri3);
        lTriangles.add(tri4);

        /*
         * //Vérification de la position du dernier sommet par rapport à chaque
         * triangle //afin de garantir l'orientation des faces PlanEquation eq =
         * new PlanEquation(tri1); System.out.println(eq.equationSignum(n3));
         * PlanEquation eq2 = new PlanEquation(tri2);
         * System.out.println(eq2.equationSignum(n0)); PlanEquation eq3 = new
         * PlanEquation(tri3); System.out.println(eq3.equationSignum(n1));
         * PlanEquation eq4 = new PlanEquation(tri4);
         * System.out.println(eq4.equationSignum(n2));
         */

        Tetraedre tet = new Tetraedre(lTriangles);

        this.tetraedres.add(tet);
        this.lTetraedres.add(tet);
      }

      // On crée les relations de voisinages entre Tétraèdres
      for (int i = 0; i < nbTetra; i++) {

        List<Tetraedre> lVoisinsTetActu = this.lTetraedres.get(i)
            .getlNeighbour();

        for (int j = 0; j < 4; j++) {

          int indVoisin = this.jTetgenioOut.neighborlist[4 * i + j];

          if (indVoisin == -1) {
            continue;
          }

          lVoisinsTetActu.add(this.lTetraedres.get(indVoisin));
        }

      }

    } catch (Exception e) {
      e.printStackTrace();

    }

  }

  /**
   * Tetraedrisation sans contrainte
   * 
   * @param mesh true pour avoir une surface triangulée, false pour avoir des
   *          tetraèdres
   */
  @Override
  public void tetraedriseWithNoConstraint(boolean mesh) {

    if (this.geometry == null) {
      TetraedrisationTopo.logger.error(Messages.getString("3DGIS.ObjetNonSet"));
      return;
    }

    this.convertJin(false);
    // tetraedriseC("znY", jin, jout);
    this.tetraedriseC("znY", this.jTetgenioIn, this.jTetgenioOut);

    this.convertJout(mesh);

  }

  /**
   * Tetraedrisation sans contrainte
   * 
   * @param mesh true pour avoir une surface triangulée, false pour avoir des
   *          tetraèdres
   */
  @Override
  public void tetraedrise(boolean isConstrainted, boolean mesh) {

    if (isConstrainted) {
      this.tetraedriseWithConstraint(mesh);
    } else {
      this.tetraedriseWithNoConstraint(mesh);
    }

  }

  /**
   * la tetraèdrisation contrainte qui ne fonctionne pas l'option mesh permet de
   * ne récupèrrer que les triangles
   * 
   * @param mesh true pour avoir une surface triangulée, false pour avoir des
   *          tetraèdres
   */
  @Override
  public void tetraedriseWithConstraint(boolean mesh) {
    try {
      this.convertJin(true);

      this.tetraedriseC("pznQY", this.jTetgenioIn, this.jTetgenioOut);// pq1.2QYzna

      this.convertJout(mesh);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
