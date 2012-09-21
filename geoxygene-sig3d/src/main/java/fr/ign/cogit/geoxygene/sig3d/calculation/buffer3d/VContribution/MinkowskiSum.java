package fr.ign.cogit.geoxygene.sig3d.calculation.buffer3d.VContribution;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Edge;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.TopoSphere;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Triangle;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Vertex;
import fr.ign.cogit.geoxygene.sig3d.tetraedrisation.TetraedrisationTopo;

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
 * Il s'agit de la classe permettant d'effectuer la somme de Minkowski entre 2
 * objets Pour le besoin du projet TerraMagna, l'Opération est effctuée entre
 * une sphère paramètrables et un autre objet Il est théoriquement possible
 * d'obtenir la somme de Minkowski entre 1 objet non convexe A et 1 objet
 * convexe B Ces objets doivent être exprimés dans le modèle exprimé dans le
 * package tétraèdrisation (Génèration à partir de
 * fr.ign.cogit.geoxygene.sig3d.tetraedrisation.TétraèdrisationTopo)
 * L'algorithme est décrit dans l'article : Barki H., Denis F, Dupont
 * Contributing F., Vertices-based Minkowski sum of a non-convex polyhedron
 * without fold and a convex polyhedron. . Dans IEEE International Conference on
 * Shape Modeling and Applications (SMI), IEEE Computer Society Press ed.
 * Beijing, China. 2009 TODO : Etendre la fonction pour des objets ponctuels,
 * linéaires et surfaciques TODO : Nettoyer la géométrie finale
 * 
 */
public class MinkowskiSum {
  // Pour simplifier la compréhension de l'algorithme le suffixe A est utilisé
  // pour les objets de la forme initiale et B pour la sphère

  // Tolérances pour àviter les erreurs numériques
  public static final double NUMERIC_TOLERANCE = Math.pow(10, -4);
  public static double ANGULAR_TOLERANCE = 0.3;

  // Les listes des triangles initiaux A àtant l'objet pouvant être concave
  private List<Triangle> lTrianglesA;
  private List<Vertex> lVertexA;

  // B est un objet connexe
  public List<Triangle> lTrianglesB;
  private List<Vertex> lVertexB;

  // Les différentes relations comme décrites dans l'article
  public List<Triangle> lRelationEdgeFace;
  public List<Triangle> lRelationVertexFace;
  public List<RelationFV> lRelationFaceVertex;

  // Les reflexes edges sont les arrètes provoquant un repli de l'espace
  public List<Edge> lEdgeReflex = new ArrayList<Edge>();

  /**
   * Point d'entrée de l'algorithme
   * 
   * @param feat L'entité sur lequel on applique l'algorithme TODO pour
   *          l'instant seuls les solides sont utilisables
   * @param p Centre de la sphère sur laquelle on applique l'algorithme (0,0,0)
   *          normalement sinon une translation est effectuée
   * @param radius Rayon de la sphère
   * @param numberCircles Détail de la sphère, il s'agit du nombre de cercles
   *          (assimilables à des parallèles) représentant la sphère
   */
  public MinkowskiSum(IFeature feat, IDirectPosition p, double radius,
      int numberCircles) {

    // Création de la sphère (ou peut mettre un autre solide convexe à la
    // place)
    TopoSphere s2 = new TopoSphere(p, radius, numberCircles);

    /*
     * Sinus de 2PI / 2 * nbcercle, cela corrspond à l'angle que nous sommes
     * prêts à accepter pour prendre un triangle. La sphère àtant discréte, il
     * parait normal d'accepter des angles de l'ordre des normales entre 2
     * triangles voisins lors des tests d'angle
     */

    MinkowskiSum.ANGULAR_TOLERANCE = Math.sin(Math.PI / (2 * numberCircles));

    // Initialisation des composants de B

    this.lTrianglesB = s2.getLTriangles();
    this.lVertexB = s2.getLVertex();

    // On tétraèdrise l'objet A en appliquant la topologie
    TetraedrisationTopo tetTopo = new TetraedrisationTopo(feat);
    tetTopo.tetraedrise(true, true);

    // Initialisation des composants de A

    this.lTrianglesA = tetTopo.getLTriangles();
    this.lVertexA = tetTopo.getLVertex();

    // On calcule la relation FV
    this.lRelationFaceVertex = this.computeRelations(this.lTrianglesA,
        this.lVertexB);

    // On calcule la relation FE
    this.lRelationEdgeFace = this.findTrianglEdge(this.lTrianglesB);

    // On calcule la relation FS
    this.lRelationVertexFace = this.cornerTriangle(this.lTrianglesB,
        this.lVertexA);

  }

  /**
   * Permet de calculer les relations FV à savoir les relations entre une face
   * et un sommet/ La face est conservée si elle est dans l'espace positif formé
   * par les facettes du sommet Il s'agit du déplacement des faces obtenus en
   * déplaçant l'objet B sur les faces de A
   * 
   * @param lTriangleA les triangles de l'objet non convexe
   * @param lVertexB les sommets de l'objet convexe
   * @return
   */
  private List<RelationFV> computeRelations(List<Triangle> lTriangleA, // L'objet
      // A
      // décomposé
      // en
      // triangles
      List<Vertex> lVertexB) {// Les différents sommets de B

    int nbTrianglesA = this.lTrianglesA.size();
    // Initialisation des relations
    List<RelationFV> lRelationFV = new ArrayList<RelationFV>();

    // Pour chaque triangle de A, on parcourt les sommets de B
    // afin de déterminer le sommet de B le plus éloigné de la surface de A
    // on translatera l'objet A selon le vecteur OB
    for (int l = 0; l < nbTrianglesA; l++) {

      // On récupère un triangle et on crée une nouvelle relation
      Triangle triangle = lTriangleA.get(l);
      RelationFV rel = new RelationFV(triangle);
      ArrayList<Vertex> lSommetRelation = rel.getLVertex();

      // On récupère le vecteur normal de A
      Vecteur vNorm = triangle.getPlanEquation().getNormale();

      // On parcourt les sommet de B
      // On détermine le point de B le plus loin
      // grâce au max des produits scalaires
      int nbPoints = lVertexB.size();

      double val = 0;

      for (int i = 0; i < nbPoints; i++) {

        Vertex s = lVertexB.get(i);

        Vecteur v = new Vecteur(s.getX(), s.getY(), s.getZ());
        v.normalise();

        double valTemp = v.prodScalaire(vNorm);

        if (valTemp < val) {

          continue;
        }

        // Il s'agit d'une valeur supérieure.
        val = valTemp;

        lSommetRelation.clear();
        lSommetRelation.add(s);

      }

      // On ajoute la meilleure relation à la liste des relations
      // existantes
      lRelationFV.add(rel);

    }
    // Nous avons le résultat
    return lRelationFV;

  }

  /**
   * Cette fonction permet de rechercher les relation ee-f Pour cela : - On
   * récupère les faces ayant contribué en phase 1 - A partir des facettes de
   * ces points on cherche les bords visibles du second, c'est à dire les degs
   * dont un triangle est visible par l'objet et pas l'autre - On regarde si la
   * face formée est cohérente
   * 
   * @param lTriB liste des triangles de l'objet B
   * @return une liste de triangles de l'objet A qui se verront appliquer une
   *         transformation via les arrêtes de l'objet B
   */
  protected List<Triangle> findTrianglEdge(List<Triangle> lTriB) {

    // Liste contenant le résultat de la conribution
    List<Triangle> lTrianglesFinale = new ArrayList<Triangle>();

    int nbElem = this.lRelationFaceVertex.size();
    int nbElemTriLB = lTriB.size();

    for (int i = 0; i < nbElem; i++) {

      RelationFV rel = this.lRelationFaceVertex.get(i);

      // On prend un triangle de l'ensemble A
      Triangle triA = rel.getTriangle();

      // On parcourt les arrètes de ce triangles
      List<Edge> lEdgeA = triA.calculEdge();

      // 3 arrètes c'est un triangle
      for (int j = 0; j < 3; j++) {
        Edge edgeA = lEdgeA.get(j);

        // Teste pour àviter la redondance : on ne traite que les arcs
        // dont un des coordonnées est positive
        // vu que l'on parcourt tous les triangles on aura l'edge dans
        // un sens ou dans l'autre

        if (edgeA.getZ() < 0) {
          continue;

        } else if (edgeA.getZ() == 0) {

          if (edgeA.getY() < 0) {

            continue;

          } else if (edgeA.getY() == 0) {

            if (edgeA.getX() < 0) {

              continue;
            }

          }

        }

        if (edgeA.isReflex()) {

          continue;
        }

        // On récupère les triangles adjacents à cet arrète
        List<Triangle> trianglesAdjacentsA = edgeA.getNeighbourTriangles();

        // On récupère le triangle adjacent qui n'est pas le triangle en
        // cours
        Triangle triangleAdjacentA = trianglesAdjacentsA.get(0);

        // Une arrète ayant seulement 2 triangles adjacents (sinon objet
        // n-manifold)
        // si le triangle obtenu est A alors l'adjacent est l'autre
        if (triangleAdjacentA == triA) {

          triangleAdjacentA = trianglesAdjacentsA.get(1);

        }

        // On obtient leur normales
        Vecteur vNormalTriA = triA.getPlanEquation().getNormale();
        Vecteur vNormalTriAdjacent = triangleAdjacentA.getPlanEquation()
            .getNormale();

        // On a besoin de récupèrer 2 vecteurs dans le sens des
        // aiguilles d'une montre
        Vecteur v1;
        Vecteur v2;

        v1 = vNormalTriA;
        v2 = vNormalTriAdjacent;

        // On vérifie si c'est un triangle plat (pas besoin de faire
        // contribuer l'edge normalement)
        Vecteur tempTest = v1.prodVectoriel(v2);

        if (tempTest.norme() < MinkowskiSum.NUMERIC_TOLERANCE) {

          continue;
        }

        // On parcourt les triangles de B (on cherche les arrètes
        // formant la limite entre les faces visibles et invisibles pour
        // edgeA)
        for (int k = 0; k < nbElemTriLB; k++) {
          // SoitTriB un triangle de l'ensemble de B
          Triangle triB = lTriB.get(k);

          // On récupère sa normale
          Vecteur normalTriB = triB.getPlanEquation().getNormale();

          // Porduit véctoriel négatif, face visible on àlimine (cela
          // àvite les redondance)
          if (edgeA.prodScalaire(normalTriB) < MinkowskiSum.NUMERIC_TOLERANCE) {

            continue;
          }

          // On récupère les arrètes de B
          List<Edge> lEdgeB = triB.calculEdge();

          // On les parcourt
          for (int n = 0; n < 3; n++) {

            Edge edgeB = lEdgeB.get(n);

            // On récup les triangles adjacents de B que l'on
            // ordonne
            List<Triangle> lTrianglesAdjacentsB = edgeB.getNeighbourTriangles();

            Triangle triangleAdjacentB = lTrianglesAdjacentsB.get(0);

            if (triangleAdjacentB == triB) {

              triangleAdjacentB = lTrianglesAdjacentsB.get(1);

            }

            // On regarde si la facette est invisble pour l'arrète,
            // C'est à dire que le produit vectoriel entre l'arrète
            // et la normale de la face est négatif
            // c'est invisible on garde, l'arrète délimite bien la
            // limite visible caché
            if (edgeA.prodScalaire(triangleAdjacentB.getPlanEquation()
                .getNormale()) > MinkowskiSum.NUMERIC_TOLERANCE) {

              continue;
            }

            // La normale de l'hypothétique face que l'on pourrait
            // ajouter
            Vecteur normalNouvelfacette = edgeA.prodVectoriel(edgeB);

            // On calcul ces produits mixtes pour savoir si la
            // normale résultante est cohérente avec les normales
            // Des faces voisines dans A (c'est à dire dans le demi
            // espace positif formé par les 2 vecteurs des triangles
            double prodMixt1 = (v1.prodVectoriel(normalNouvelfacette))
                .prodScalaire(edgeA);
            double prodMixt2 = (normalNouvelfacette.prodVectoriel(v2))
                .prodScalaire(edgeA);

            if (prodMixt1 < MinkowskiSum.NUMERIC_TOLERANCE
                && prodMixt2 < MinkowskiSum.NUMERIC_TOLERANCE) {
              // Ils sont de mêmes signe
              // Alors on ajoute edge + edTemp
              lTrianglesFinale.addAll(edgeA.sumEdge(edgeB));

            }

          }
        }

      }

    }

    // On renvoie les triangles obtenus ainsi

    return lTrianglesFinale;
  }

  /**
   * Permet de calculer les facettes de coins pour chaque sommet de A on
   * récupère les triangles de B dont les normales sont dans le frustum formées
   * par les arrètes passant par le sommet
   * 
   * @param lTrianglesB liste des triangles de l'objet B
   * @param lVertexA liste des vertex de l'objet A
   * @return On récupère les triangles de B qui seront conséervés à l'endroit
   *         des sommets de A pour contribuer à la somme finale
   */
  protected List<Triangle> cornerTriangle(List<Triangle> lTrianglesB,
      List<Vertex> lVertexA) {
    List<Triangle> lTrianglesCorner = new ArrayList<Triangle>();

    int nbSommetsA = lVertexA.size();
    int nbTrianglesB = lTrianglesB.size();

    // boucle sur A
    for (int i = 0; i < nbSommetsA; i++) {
      Vertex sommetA = lVertexA.get(i);

      // on récupère les triangles de A voisins au sommet A puis on
      // récupère les arrètes orientées
      // en direction de A de manière à former le frustum
      List<Triangle> lTrianglesA = sommetA.getLTRiRel();
      List<Edge> lEdgesFinA = MinkowskiSum.getListEdgeA(lTrianglesA, sommetA);

      if (lEdgesFinA == null) {

        continue;
      }

      int nbPairesEdge = lEdgesFinA.size();

      // Trop peu de paires, il s'agit d'un espce fermé

      if (nbPairesEdge < 6) {

        continue;
      }

      // On parcourt les triangles de B
      boucleVecteurB: for (int indTriangleB = 0; indTriangleB < nbTrianglesB; indTriangleB++) {

        Vecteur norm = lTrianglesB.get(indTriangleB).getPlanEquation()
            .getNormale();

        Edge edgePred;
        Edge edgeActu;

        // On parcourt les différentes paires et on calcul les produits
        // mixtes
        // pour savoir si le vecteur se situe au dessus de chaque
        // composante du frustum

        for (int k = 0; k < nbPairesEdge; k = k + 2) {

          edgePred = lEdgesFinA.get(k);
          edgeActu = lEdgesFinA.get(k + 1);

          edgePred.normalise();
          edgeActu.normalise();

          // On regarde si il est au dessus du plan formé par edgePred
          // & edgeActu

          double prodMixt = edgePred.prodVectoriel(edgeActu).prodScalaire(norm);

          // On teste par rapprot à la tolérence angulaire
          // A caus ede serreurs induites par la discrétisation
          if (prodMixt < -MinkowskiSum.ANGULAR_TOLERANCE) {

            continue boucleVecteurB;

          }

          // On regarde si il est inclus entre les 2 vecteur en
          // projection dans le plan edgePred edgeActu
          prodMixt = edgePred.prodScalaire(norm);

          if (prodMixt < -MinkowskiSum.ANGULAR_TOLERANCE) {

            continue boucleVecteurB;

          }

          prodMixt = edgeActu.prodScalaire(norm);

          if (prodMixt < -MinkowskiSum.ANGULAR_TOLERANCE) {

            continue boucleVecteurB;

          }

        }

        // On peut prendre la face issue de B
        lTrianglesCorner.add(lTrianglesB.get(indTriangleB).translateTriangle(
            sommetA));

        continue boucleVecteurB;

      }

    }

    return lTrianglesCorner;

  }

  /**
   * Renvoie toutes les arrètes des triangles de lTRI se terminant par sommet
   * sous forme de paires consécutives et orientées si il y a trop d'edge
   * reflex, on ne considère pas cette liste Utile pour calculer les frustum
   * 
   * @param lTri
   * @param vertex
   * @return
   */
  private static List<Edge> getListEdgeA(List<Triangle> lTri, Vertex vertex) {
    // Sert à dérterminer le nombre de réflexes
    int reflex = 0;

    int nbTri = lTri.size();
    // Normalement il y a 2 edges par reflexes, cela permet d'eviter des
    // modifs de taille
    ArrayList<Edge> lEdgesFinaux = new ArrayList<Edge>(2 * nbTri);
    ArrayList<Vecteur> lNormales = new ArrayList<Vecteur>();

    // On traite chaque triangle
    // On récupère les arrètes on regarde le sommet initial ou final et on
    // ordonne
    boucletriangle: for (int i = 0; i < nbTri; i++) {

      Triangle t = lTri.get(i);

      List<Edge> lEdges = t.calculEdge();
      List<Edge> lEdgesTemp = new ArrayList<Edge>(2);

      for (int j = 0; j < 3; j++) {

        Edge e = lEdges.get(j);

        if (e.getVertFin().equals(vertex)) {

          if (e.isReflex()) {
            reflex++;

          }

          lEdgesTemp.add(e);
          continue;
        } else if (e.getVertIni().equals(vertex)) {

          lEdgesTemp.add(e.inverse());

          continue;
        }

      }

      Vecteur normal = t.getPlanEquation().getNormale();

      Vecteur prodVect = lEdgesTemp.get(0).prodVectoriel(lEdgesTemp.get(1));

      double prodS = prodVect.prodScalaire(normal);

      int nbNorm = lNormales.size();

      // Le produiScalaire permet d'ordonner les arrètes afin de passer
      // les tests effectués
      // Dans la determination des contribution par sommet
      if (prodS < 0) {

        for (int h = 0; h < nbNorm; h++) {

          Vecteur vTemp = lNormales.get(h);

          // Normale déjà trouvée aucun intérêt à décrire le frustum
          if (vTemp.prodVectoriel(prodVect).norme() < MinkowskiSum.NUMERIC_TOLERANCE) {

            continue boucletriangle;

          }

        }

        lNormales.add(prodVect);

        lEdgesFinaux.add(lEdgesTemp.get(1));
        lEdgesFinaux.add(lEdgesTemp.get(0));
      } else {
        prodVect = prodVect.multConstante(-1);

        for (int h = 0; h < nbNorm; h++) {

          Vecteur vTemp = lNormales.get(h);

          // Normale déjà trouvée aucun intérêt à décrire le frustum
          if (vTemp.prodVectoriel(prodVect).norme() < MinkowskiSum.NUMERIC_TOLERANCE) {

            continue boucletriangle;

          }

        }

        lNormales.add(prodVect);

        lEdgesFinaux.add(lEdgesTemp.get(0));
        lEdgesFinaux.add(lEdgesTemp.get(1));

      }

    }

    // Si il est reflex
    // On vérifie la compataibilité entre le frustum
    // et les polyones
    if (reflex > 0) {

      // plus de 1 reflexe on jette
      // Sinon il faut dfaire des tests supplémentaires

      if (reflex > 1) {

        return null;

      }
      int nbNorm = lNormales.size();
      int positif = 0;

      // On àlimine les cas ou les arrètes ne se trouvent pas en nombre
      // Suffisant dans les demis espaces positifs formés par les
      // triangles

      bouclestriangles: for (int i = 0; i < nbNorm; i++) {

        Vecteur normal = lNormales.get(i);

        int nbEdges = lEdgesFinaux.size();

        for (int j = 0; j < nbEdges; j = j + 2) {
          if (lEdgesFinaux.get(j).prodScalaire(normal) > MinkowskiSum.NUMERIC_TOLERANCE) {

            positif++;

            continue bouclestriangles;

          }

        }

      }

      if (positif < 3) {

        return null;
      }

    }

    return lEdgesFinaux;

  }

  /**
   * @return Renvoie la liste des faces calculés
   */
  public List<IOrientableSurface> getAllTriangle() {

    List<IOrientableSurface> lTriFinale = new ArrayList<IOrientableSurface>();
    lTriFinale.addAll(this.lRelationVertexFace);
    lTriFinale.addAll(this.lRelationEdgeFace);
    lTriFinale.addAll(MinkowskiSum.convertRelation(this.lRelationFaceVertex));
    return lTriFinale;
  }

  private static List<IOrientableSurface> convertRelation(List<RelationFV> rFV1) {

    int nbElem1 = rFV1.size();

    List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>(nbElem1);

    // Pour chaque objet on effectue la translation
    // Attention on ne considère que la relation est 1-1 (cas de la sphère)
    for (int i = 0; i < nbElem1; i++) {

      RelationFV rel = rFV1.get(i);

      Triangle tTemp = rel.getTriangle();

      int nbsomest = rel.getLVertex().size();

      for (int j = 0; j < nbsomest; j++) {

        lOS.add(tTemp.translateTriangle(rel.getLVertex().get(j))
            .toGeoxygeneSurface());

      }
    }

    return lOS;
  }

}
