// Algorithme de génèralisation de bâtiments 3D inspiré de Martin Kada
//
// auteur : Aurélien Velten (2007)
// aidé par : Benoit Poupeaux, Julien Gaffuri, Guillaume Ménégaux

package fr.ign.cogit.geoxygene.sig3d.simplification.aglokada;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.calculation.Calculation3D;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

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
 * @author Aurélien Velten 
 * @version 0.1
 * 
 * Algorithme permettant de simplifier des bâtiments en 3D Algorithm for
 * building simplification
 * 
 * 
 */
public class Algo_kada {

  private final static Logger logger = Logger.getLogger(Algo_kada.class
      .getName());

  private List<GM_Solid> lSolid1 = new ArrayList<GM_Solid>();
  private List<GM_Solid> lSolid2 = new ArrayList<GM_Solid>();
  private List<IOrientableSurface> lFaces = new ArrayList<IOrientableSurface>();
  private List<IOrientableSurface> lVerticalFaces = new ArrayList<IOrientableSurface>();

  private List<IOrientableSurface> lRoofFaces = new ArrayList<IOrientableSurface>();

  private List<IOrientableSurface> lGroundFaces = new ArrayList<IOrientableSurface>();

  // ********************************* PARAMETRES
  // *********************************
  // angle de tolérance pour la détection des faces verticales (en degrès de
  // chaque côté) [2é par défaut]

  private double eliminationThreshold = 0.1;
  private double minimalFinalLength = 1.3;
  private double COSINUS_THRESHOLD = 0.1;
  private double STEP = 0.1;

  private double cutThreshold = 3;

  private double DEGREE_ACCEPTANCE = 2;

  private double RADIAN_ACCEPTANCE = this.DEGREE_ACCEPTANCE * (Math.PI / 2)
      / 90;

  GM_Solid initialSolid = new GM_Solid();

  /**
   * Constructeur vide permettant de modifier les paramètres avant exécution
   */
  public Algo_kada() {
    // Constructeur vide
  }

  /**
   * Permet d'effectuer la simplification d'un solid
   * 
   * @param solid le solide que l'on souhaite simplifier. Il doit s'agir d'un
   *          solide clos
   */
  public void process(GM_Solid solid) {

    this.initialSolid = solid;
    this.lFaces.addAll(solid.getFacesList());

    Algo_kada.logger
        .info("\n\n********* Algorithme de simplification 3D inspiré de l'algorithme de Martin Kada *********");
    Algo_kada.logger.info("\nLe bâtiment comporte " + this.lFaces.size()
        + " face(s)");
    Algo_kada.logger.info("Rappel des paramètres :tolerance_elimintation  : "
        + this.eliminationThreshold + "\nlongueur_minFinale"
        + this.minimalFinalLength + "\n tolérance_cosinus"
        + this.COSINUS_THRESHOLD + "\n");
    Algo_kada.logger.info("Suppression des faces non verticales (tolérance de "
        + this.DEGREE_ACCEPTANCE + "°)");

    Vecteur vertical = new Vecteur(0, 0, 1);

    // on élimine les faces qui ne sont pas quasi-verticales
    for (int i = 0; i < this.lFaces.size(); i++) {

      PlanEquation eqP = new PlanEquation(this.lFaces.get(i));

      Vecteur norm = eqP.getNormale();

      double prodscalaire = norm.prodScalaire(vertical);

      if (Math.abs(prodscalaire) < Math.sin(this.RADIAN_ACCEPTANCE)
          * norm.norme()) {
        this.lVerticalFaces.add(this.lFaces.get(i));
      } else {

        if (Math.abs(prodscalaire) + 0.01 < norm.norme()) {

          this.lRoofFaces.add(this.lFaces.get(i));
        }

      }
    }

    // pour arrèter l'algorithme s'il ne reste aucune face dans la liste de
    // faces verticales
    if (this.lVerticalFaces.size() == 0) {

      Algo_kada.logger
          .error("---> Il n'y a aucune face quasi-verticale, l'algorithme ne peut pas être appliqué");

      return;

    }

    Algo_kada.logger.info("---> Il reste " + this.lVerticalFaces.size()
        + " face(s) quasi-verticale(s) parmi les " + this.lFaces.size()
        + " face(s) de départ\n");

    // détection des différents coupes du bâtiment (selon l'altitude Z)
    ZCutDetection dcz = new ZCutDetection();

    List<List<IOrientableSurface>> LGP = dcz.groupingFaces(this.lVerticalFaces,
        this.cutThreshold);
    List<Double> lZtop = dcz.getlZtop();

    int nb_coupe = LGP.size();

    Algo_kada.logger.info("nombre de coupes = " + nb_coupe);

    // itération de l'algo pour chaque coupe en Z du bâtiment
    for (int nb = 0; nb < nb_coupe; nb++) {

      Algo_kada.logger.info("\n\n***** Coupe en Z numéro " + (nb + 1)
          + " *****");

      List<IOrientableSurface> listeFaces = new ArrayList<IOrientableSurface>();
      listeFaces = (LGP.get(nb));

      Algo_kada.logger.info("nombre de faces de la coupe = "
          + listeFaces.size());

      double Zmin = 30000;

      if (nb == 0) {
        Zmin = Calculation3D.pointMin(listeFaces.get(0)).getZ();

      } else {

        Zmin = lZtop.get(nb - 1).doubleValue();
      }

      double Zf = lZtop.get(nb).doubleValue();

      List<GM_LineString> LSegmentSol = new ArrayList<GM_LineString>();
      List<GM_LineString> lSSA2 = new ArrayList<GM_LineString>();
      List<GM_LineString> lSSA3 = new ArrayList<GM_LineString>();

      // projection des murs au sol afin d'obtenir la trace de la coupe au
      // "sol" (segments au sol)
      for (int i = 0; i < listeFaces.size(); i++) {
        GroundCasting proj_sol = new GroundCasting(listeFaces.get(i));
        IDirectPosition dp1 = proj_sol.getGroundCasting().coord().get(0);

        IDirectPosition dp2 = proj_sol.getGroundCasting().coord().get(1);

        if (!dp1.equals(dp2, 0.0001)) {
          LSegmentSol.add(proj_sol.getGroundCasting());
          // mise à niveau de tous les segments au sol par
          // l'intermédiaire du Zmin
          if (Zmin > proj_sol.getZ()) {
            Zmin = proj_sol.getZ();
          }
        }
      }

      Algo_kada.logger.info("Il en résulte " + LSegmentSol.size()
          + " lignes au sol avec pour altitude z = " + Zmin + " m");

      // on réordonne les segments au sol par adjacence
      LSegmentSol = new SegmentSorting(LSegmentSol).getLSSSorted();

      // détection des cycles par adjacence
      List<List<GM_LineString>> listeCycles = new ArrayList<List<GM_LineString>>();
      CyclesDetection dc = new CyclesDetection();
      listeCycles = dc.getListeCycles(LSegmentSol);

      // itération pour chaque cycle de chaque coupe en Z
      for (int c = 0; c < listeCycles.size(); c++) {

        Algo_kada.logger.info("\n---> cycle " + (c + 1));

        List<GM_LineString> lLATraiter = listeCycles.get(c);

        // si le cycle n'est pas fermé on créé un nouveau segment pour
        // fermer le cycle
        lLATraiter = new CycleClosure(lLATraiter, Zmin).getLSSColsed();

        if (lLATraiter.size() < 3) {
          continue;
        }

        for (double seuilActu = this.eliminationThreshold; seuilActu < this.STEP
            + this.minimalFinalLength; seuilActu = seuilActu + this.STEP) {

          // Nous àliminons les segments trop petits
          // lLATraiter = Util.elimination(lLATraiter, seuilActu);

          for (int i = 0; i < lLATraiter.size(); i++) {

            // On ajoute le segment précédent
            // Reste à savoir si d'autres segments seront insérés

            GM_LineString lActuel = lLATraiter.get(i);

            // On àlimine ce segment et on en tient pas compte

            lLATraiter.remove(i);

            GM_LineString lSuiva = null;

            // On àtablit les segments précédents et suivants
            int id_suiva = 0;
            if (i > lLATraiter.size() - 1) {

              lSuiva = lLATraiter.get(0);

            } else {

              id_suiva = i;
              lSuiva = lLATraiter.get(id_suiva);

            }

            GM_LineString lPrec = null;

            int id_pred = 0;

            if (i == 0) {

              id_pred = lLATraiter.size() - 1;
              lPrec = lLATraiter.get(id_pred);

            } else {

              id_pred = i - 1;
              lPrec = lLATraiter.get(id_pred);

            }

            if (Util.isColinear(lActuel, lPrec)) {
              // on les fusionne

              Algo_kada.logger.debug("Faces colinéaires");
              DirectPositionList dpl = new DirectPositionList();
              dpl.add(lPrec.coord().get(0));
              dpl.add(lActuel.coord().get(1));

              GM_LineString nouvelleLigne = new GM_LineString(dpl);

              lLATraiter.set(id_pred, nouvelleLigne);

              i = Math.max(-1, i - 1);

              continue;

            }

            if (Util.isColinear(lActuel, lSuiva)) {
              // on les fusionne
              Algo_kada.logger.debug("Faces colinéaires");

              DirectPositionList dpl = new DirectPositionList();
              dpl.add(lActuel.coord().get(0));
              dpl.add(lSuiva.coord().get(1));

              GM_LineString nouvelleLigne = new GM_LineString(dpl);

              lLATraiter.set(id_suiva, nouvelleLigne);

              i = Math.max(-1, i - 1);

              continue;

            }

            if (seuilActu < lActuel.length()) {
              lLATraiter.add(id_suiva, lActuel);
              continue;

            }

            // On vérifie que les 2 lignes se raccordent bien

            IDirectPosition pPred = lPrec.coord().get(1);
            IDirectPosition pSuiv = lSuiva.coord().get(0);

            // il y a eu un segment supprimé entre les 2 lignes, on
            // complète
            // 2 manière de complèter
            // Si l'angle est proche de 90 on rallonge les segments
            // de manière
            // à se qu'ils se raccordent
            // sinon on complète par un nouveau segment
            IDirectPosition pPred0 = lPrec.coord().get(0);
            IDirectPosition pSuiv1 = lSuiva.coord().get(1);

            Vecteur v1 = new Vecteur(pPred0.getX() - pPred.getX(),
                pPred0.getY() - pPred.getY(), 0);

            Vecteur v2 = new Vecteur(pSuiv.getX() - pSuiv1.getX(), pSuiv.getY()
                - pSuiv1.getY(), 0);

            double cosinus = v1.prodScalaire(v2);
            cosinus = cosinus / (lPrec.length() * lSuiva.length());

            // Les deux vecteurs sont orthogonaux
            if (Math.abs(cosinus) < this.COSINUS_THRESHOLD) {
              // Nous avons des angles quasi droit, on va
              // complèter les droite jusqu'a l'intersection
              Algo_kada.logger.debug("2 plans orthogonaux");

              DirectPosition A = new DirectPosition(pPred0.getX(),
                  pPred0.getY());
              DirectPosition B = new DirectPosition(pPred.getX(), pPred.getY());
              DirectPosition M = new DirectPosition(pSuiv.getX(), pSuiv.getY());

              DirectPosition nouvPoint = Util.casting(A, B, M);

              DirectPositionList dpL1 = new DirectPositionList();
              dpL1.add(pPred0);
              dpL1.add(nouvPoint);

              DirectPositionList dpL2 = new DirectPositionList();
              dpL2.add(nouvPoint);
              dpL2.add(pSuiv1);

              lPrec = new GM_LineString(dpL1);
              lSuiva = new GM_LineString(dpL2);

              // On modifie le segment précédent et le segment
              // actuel

              // On traite la ligne suivante

              lLATraiter.set(id_pred, lPrec);
              lLATraiter.set(id_suiva, lSuiva);

              // On retourne en arrière pour traiter ces segments
              i = Math.max(-1, i - 1);

              continue;

            }
            DirectPositionList nLDP = new DirectPositionList();
            nLDP.add(pPred);
            nLDP.add(pSuiv);

            GM_LineString gLS = new GM_LineString(nLDP);

            // Cas du segment en plus trop petit
            if (gLS.length() < seuilActu) {

              // Cas d'un chapeau très serré
              if (cosinus < this.COSINUS_THRESHOLD - 1) {
                // On oublie ces 2 segments et on crée un
                // segment partant
                // du premier point du premier segment au
                // dernier point du dernier segment

                Algo_kada.logger.debug("Chapeau serré");

                DirectPositionList nLDPred = new DirectPositionList();
                nLDPred.add(pPred0);
                nLDPred.add(pSuiv1);

                lPrec = new GM_LineString(nLDPred);

                lLATraiter.set(id_pred, lPrec);

                // On retourne en arrière pour traiter ces
                // segments
                i = Math.max(-1, i - 1);

                continue;

              }

              // théorie de "coin de table" à vérifier ....

              // cas de l'escalier et grand chapeau

              // Dans ce cas, on àlimine en déplacant la plus
              // courte des deux arrètes

              if (lPrec.length() <= lSuiva.length()) {

                // System.out
                // .println("Déplacement arrète, chapeau ou autre");
                DirectPositionList nLDPred = new DirectPositionList();
                nLDPred.add(pPred0);
                nLDPred.add(pSuiv);

                lPrec = new GM_LineString(nLDPred);
                lLATraiter.set(id_pred, lPrec);

                // On retourne en arrière pour traiter ces
                // segments
                i = Math.max(-1, i - 1);

              } else {

                // System.out
                // .println("Déplacement arrète, chapeau ou autre");
                // Si c'est la dernière itération il faut
                // remplacer le premier segment par lACTu
                if (i == lLATraiter.size() - 1) {

                  DirectPositionList nLDActu = new DirectPositionList();
                  nLDActu.add(pPred);
                  nLDActu.add(lLATraiter.get(0).coord().get(1));

                  lSuiva = new GM_LineString(nLDActu);

                  lLATraiter.set(0, lSuiva);

                  lLATraiter.set(id_pred, lPrec);
                  continue;

                }
                DirectPositionList nLDActu = new DirectPositionList();
                nLDActu.add(pPred);
                nLDActu.add(pSuiv1);

                lSuiva = new GM_LineString(nLDActu);
                lLATraiter.set(id_suiva, lSuiva);

                lLATraiter.set(id_pred, lPrec);

                // On retourne en arrière pour traiter
                // ces segments
                i = Math.max(-1, i - 1);

              }

              continue;

            }
            lLATraiter.add(i, gLS);

            Algo_kada.logger.debug("Chapeau de bonne taille");

            continue;

          }

        }

        // on réordonne les segments au sol approximants (LSSA2)
        SegmentSorting RS1 = new SegmentSorting(lLATraiter);
        lSSA2 = RS1.getLSSSorted();

        // on recolle les segments au sol approximants (LSSA3)
        SegmentAdjustement RS2 = new SegmentAdjustement(lSSA2, Zmin);
        lSSA3 = RS2.getLSSadjusted();

        // Ajout Mickael
        // Courbe décrivant le sommet du cycle
        DirectPositionList lP = new DirectPositionList();

        if (lSSA3.size() == 1) {

          Algo_kada.logger.warn("1 seul buffer, on jette");

          continue;
        }

        // contruction des murs à partir des segments au sol
        for (int i = 0; i < lSSA3.size(); i++) {
          IDirectPositionList LPointFaceSol = new DirectPositionList();
          GM_LineString LS = lSSA3.get(i);
          IDirectPosition dp1s = new DirectPosition(LS.coord().get(0).getX(),
              LS.coord().get(0).getY(), Zmin);
          IDirectPosition dp2s = new DirectPosition(LS.coord().get(1).getX(),
              LS.coord().get(1).getY(), Zmin);
          IDirectPosition dp3s = new DirectPosition(LS.coord().get(1).getX(),
              LS.coord().get(1).getY(), Zf);
          IDirectPosition dp4s = new DirectPosition(LS.coord().get(0).getX(),
              LS.coord().get(0).getY(), Zf);

          LPointFaceSol.add(dp1s);
          LPointFaceSol.add(dp4s);
          LPointFaceSol.add(dp3s);
          LPointFaceSol.add(dp2s);
          LPointFaceSol.add(dp1s);

          GM_LineString ls = new GM_LineString(LPointFaceSol);
          IOrientableSurface face_sol = new GM_Polygon(ls);

          this.lGroundFaces.add(face_sol);

          // on complète la liste
          lP.add(dp4s);
          lP.add(dp3s);
        }

        if (lSSA3.size() > 2) {

          // ajout du toit

          Algo_kada.logger.info("Nombre de pans de toit "
              + this.lRoofFaces.size());

          Algo_kada.logger.info("Nombre de pans de murs " + lSSA3.size());

          if (this.lRoofFaces.size() <= lSSA3.size()
              && this.lRoofFaces.size() > 4) {
            this.lGroundFaces.addAll(RoofCreation2.roof(lP, this.lRoofFaces,
                this.cutThreshold, this.eliminationThreshold));// this.seuil_coupe));

          } else {

            List<GM_LineString> LSSA22 = new ArrayList<GM_LineString>();
            int nn = lSSA3.size();

            for (int p = 0; p < nn; p++) {
              LSSA22.add(lSSA3.get(nn - p - 1));

            }

            this.lGroundFaces
                .addAll(new RoofConstruction(LSSA22, Zf).getRoof());

          }

          // ajout du sol

          // test

          this.lGroundFaces.addAll(new RoofConstruction(lSSA3, Zmin).getRoof());

        }
        // fin de l'itération des cycles

      }
      // fin de l'itération des coupes
    }
    // construction du bâtiment final (ajout des coupes dans un même objet
    // géographique)
    GM_Solid s = new GM_Solid(this.lGroundFaces);
    this.lSolid2.add(s);

    Algo_kada.logger.info("********* FIN DE L'ALGORITHME *********\n\n");

  }

  /**
   * Seuil à partir duquel des buffers fusionnent
   * 
   * @param eliminationThreshold nouveau paramètre de fusions de buffers
   */
  public void setElminationThreshold(double eliminationThreshold) {
    this.eliminationThreshold = eliminationThreshold;
  }

  /**
   * Découpe en Z des batiments
   * 
   * @param cutThreshold indice de découpe en Z
   */
  public void setCutThreshold(double cutThreshold) {
    this.cutThreshold = cutThreshold;
  }

  /**
   * Longueur finale acceptable d'un mur
   * 
   * @param minimalFinalLength
   */
  public void setMinimalFinalLength(double minimalFinalLength) {
    this.minimalFinalLength = minimalFinalLength;
  }

  /**
   * Constructeur effectuant l'exécution
   * 
   * @param solid
   */
  public Algo_kada(GM_Solid solid) {
    this.process(solid);
  }

  // ********************** METHODES **********************

  /**
   * Liste des buffers au sol
   */
  public List<GM_Solid> getLSolid1() {
    return this.lSolid1;
  }

  /**
   * @return Liste contenant le (les ? ) objets simplifié (il pourrait y en
   *         avoir plusieurs ....)
   */
  public List<GM_Solid> getLSolid2() {
    return this.lSolid2;
  }

}
