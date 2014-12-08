/**
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 * 
 */
package fr.ign.cogit.geoxygene.matching.beeri;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;
import fr.ign.cogit.geoxygene.util.string.ApproximateMatcher;

/**
 * 
 * Méthodes d'appariement de données ponctuelles 
 *    proposées par [Beeri et al. 2004]
 * 
 * 
 * @author Eric Grosso
 * @author Ana Maria Olteanu
 */
public class AppariementBeeri {
  
  private static final Logger LOGGER = Logger.getLogger(AppariementBeeri.class);
  
  /**
   * Appariement par recherche du plus proche voisin. Chaque élément de popRef
   * est apparié avec son plus proche voisin dans popComp 1ère Méthode proposée
   * par [Beeri et al 2004]
   * 
   * @param popRef Population d'objets avec une géométrie ponctuelle.
   * 
   * @param popComp Population d'objets avec une géométrie ponctuelle.
   * 
   * @param seuilDistanceMax Seuil de distance au dessous duquel on n'apparie
   *          pas deux objets.
   * 
   * @return Ensemble de liens d'appariement. Seulement des liens 1-1 sont
   *         créés. Leur évaluation est égale à 1 si on est sûr du résultat, à
   *         0.5 sinon.
   */
  public static EnsembleDeLiens appariementPPV(IPopulation<IFeature> popRef,
      IPopulation<IFeature> popComp, double seuilDistanceMax) {
    
    EnsembleDeLiens liens = new EnsembleDeLiens();
    ApproximateMatcher AM = new ApproximateMatcher();
    AM.setIgnoreCase(true);
    
    // Indexation de la population de comparaison
    if (!popComp.hasSpatialIndex()) {
      LOGGER.info("Indexation de popComp (oronymes)");
      popComp.initSpatialIndex(Tiling.class, true, 10);
    }
    if (!popRef.hasSpatialIndex()) {
      LOGGER.info("Indexation de popRef (points remarquable de relief).");
      popRef.initSpatialIndex(Tiling.class, true, 10);
    }
    
    // On parcourt les objets ref un par un
    for (IFeature objetRef : popRef) {
      
      IPopulation<IFeature> pop1Ref = new Population<IFeature>("Ref");
      pop1Ref.setFeatureType(objetRef.getFeatureType());
      pop1Ref.add(objetRef);
      
      // .select(((GM_Point) objetRef.getGeom()).getPosition(), seuilDistanceMax)
      IPopulation<IFeature> candidatsApp = popComp.selectionElementsProchesGenerale(pop1Ref, seuilDistanceMax);
      LOGGER.debug("Nb candidat = " + candidatsApp.size());
      if (candidatsApp.size() == 0) {
        continue;
      }
      
      // Pour chaque objet ref on calcule la distance à tous les objets comp proches
      // pour ne garder que le plus proche
      IFeature candidatRetenu = null;
      double distPP = seuilDistanceMax;
      for (IFeature objetComp : candidatsApp) {
        double distance = ((GM_Point) objetRef.getGeom()).getPosition().distance(((GM_Point) objetComp.getGeom()).getPosition());
        LOGGER.trace("Distance entre ref et comp = " + distance);
        if (distance <= distPP) {
          distPP = distance;
          candidatRetenu = objetComp;
        }
      }
      
      // On crée un nouveau lien avec sa géométrie et son évaluation
      if (candidatRetenu != null) {
        Lien lien = liens.nouvelElement();
        lien.addObjetRef(objetRef);
        lien.addObjetComp(candidatRetenu);
        GM_LineString ligne = new GM_LineString();
        ligne.addControlPoint(((GM_Point) objetRef.getGeom()).getPosition());
        ligne.addControlPoint(((GM_Point) candidatRetenu.getGeom())
            .getPosition());
        lien.setGeom(ligne);
      }
    }
    
    LOGGER.info("Nombre de liens trouvés = " + liens.size());
    return liens;
  }
  
  /**
   * Appariement par recherche du plus proche voisin dans les deux sens. Chaque
   * élément A de popRef est apparié avec son plus proche voisin B dans popComp,
   * si et seulement si, dans l'autre sens, A est le plus proche voisin (dans
   * popRef) de B. 2ème méthode proposée par [Beeri et al. 2004]
   * 
   * @param popRef Population d'objets avec une géomtrie ponctuelle.
   * 
   * @param popComp Population d'objets avec une géomtrie ponctuelle.
   * 
   * @param seuilDistanceMax Seuil de distance au dessous duquel on n'apparie
   *          pas deux objets.
   * 
   * @return Ensemble de liens d'appariement. Seulement des liens 1-1 sont
   *         créés. Leur évaluation est égale à 1 si on est sûr du résultat(
   *         confiance>0.5), et à 0.5 sinon (confiance<0.5, !=0).
   */
  public static EnsembleDeLiens appariementPPVDansLesDeuxSens(
      IFeatureCollection<?> popRef, IFeatureCollection<?> popComp,
      double seuilDistanceMax) {
    // déterminer les liens d'appariement
    EnsembleDeLiens liensAppariement = new EnsembleDeLiens();
    Integer nbObj = 0, nbObjNonAp = 0, nbLienSur = 0, nbLienIncertain = 0, nbLiensNonAcceptes = 0, nbLienTresIncertain = 0;

    // ////// 1ere façon de faire
    // On calcule les liens avec la methode PPV de ref vers comp
    // On calcule les liens avec la methode PPV de comp vers ref
    // On compare les deux ensemble de liens obtenus

    // EnsembleDeLiens lienDirect=appariementPPV(popRef, popComp, 200, 0.5);
    // EnsembleDeLiens leinsInvers=appariementPPV( popComp, popRef, 200, 0.5);

    // ////// 2eme façon de faire
    // On calcule les liens comme avec la methode PPV de ref vers comp
    // et quand on a trouvé un lien on vérifie que ça marche dans l'autre sens

    double distance, distPP, distPP1, distanceCompRef;
    IFeatureCollection<?> candidatsApp;
    IFeature candidatRetenu, candidatRetenu1;
    IFeature objetRef, objetRef1, objetComp;
    List<IFeature> listNonApp = new ArrayList<IFeature>();
    Iterator<?> itCandidatsApp;
    Lien lien;
    // Indexation de la population de comparaison
    if (!popComp.hasSpatialIndex()) {
      System.out.println("Indexation des oronymes");
      popComp.initSpatialIndex(Tiling.class, true, 10);
    }
    if (!popRef.hasSpatialIndex()) {
      System.out.println("Indexation des points remarquable de relief");
      popRef.initSpatialIndex(Tiling.class, true, 10);
    }

    // On parcourt les objets ref un par un
    Iterator<?> itPopRef = popRef.getElements().iterator();
    while (itPopRef.hasNext()) {
      objetRef = (IFeature) itPopRef.next();
      candidatRetenu = null;
      candidatsApp = (IFeatureCollection<?>) popComp.select(
          ((GM_Point) objetRef.getGeom()).getPosition(), seuilDistanceMax);
      if (candidatsApp.size() == 0) {
        listNonApp.add(objetRef);
        nbObjNonAp++;
        continue;
      }
      // Pour chaque objet ref on calcule la distance à tous les objets comp
      // proches
      // pour ne garder que le plus proche
      itCandidatsApp = candidatsApp.getElements().iterator();
      distPP = seuilDistanceMax;
      while (itCandidatsApp.hasNext()) {
        objetComp = (IFeature) itCandidatsApp.next();
        distance = Distances.distance(
            ((GM_Point) objetRef.getGeom()).getPosition(),
            ((GM_Point) objetComp.getGeom()).getPosition());
        if (distance <= distPP) {
          distPP = distance;
          candidatRetenu = objetComp;
        }
      }
      if (candidatRetenu == null)
        continue;
      // pour l'objet candidat retenu on calcule la distance à tous les objets
      // ref
      // afin de garder que le plus proche objet; on teste si comp=pp(ref) et
      // ref=pp(comp)
      distPP1 = seuilDistanceMax;
      candidatRetenu1 = null;
      Iterator<?> itPopRef1 = popRef.getElements().iterator();

      while (itPopRef1.hasNext()) {
        objetRef1 = (IFeature) itPopRef1.next();
        distanceCompRef = Distances.distance(
            ((GM_Point) objetRef1.getGeom()).getPosition(),
            ((GM_Point) candidatRetenu.getGeom()).getPosition());
        if (distanceCompRef <= distPP1) {
          distPP1 = distanceCompRef;
          candidatRetenu1 = objetRef1;
        }
      }
      if (candidatRetenu1 == objetRef) {
        lien = liensAppariement.nouvelElement();
        lien.addObjetRef(objetRef);
        lien.addObjetComp(candidatRetenu);
        GM_LineString ligne = new GM_LineString();
        ligne.addControlPoint(((GM_Point) objetRef.getGeom()).getPosition());
        ligne.addControlPoint(((GM_Point) candidatRetenu.getGeom())
            .getPosition());
        lien.setGeom(ligne);
        nbObj++;
      } else {
        listNonApp.add(objetRef);
        nbObjNonAp++;
      }
    } // fin boucle 1 sur les ojets ref
    System.out.println("objets appariés :" + nbObj);

    // On crée un nouveau lien avec sa géométrie et son évaluation
    // On évalue la confiance des liens d'appariement,
    // et on ne garde que ceux pour lesquels on est assez surs

    EnsembleDeLiens liensAppariementSurs = new EnsembleDeLiens();
    Lien lienApp;
    double confiance = 0;

    // boucle sur chaque lienAppariement
    // calcul de la mesure de confiance; si la confiance (confiance>0.6 &
    // confiance<=1 ) le lien est sur;
    // si confiance est entre (0.4,0.6), le lien est incertain;
    // si le lien est très incertain
    // si non il n'est pas accepté;

    Iterator<?> itLiensAppariement = liensAppariement.getElements().iterator();
    while (itLiensAppariement.hasNext()) {
      lienApp = (Lien) itLiensAppariement.next();
      confiance = confiance(lienApp, popRef, popComp, seuilDistanceMax);
      lienApp.setEvaluation(confiance);
      if (confiance == 0)
        nbLiensNonAcceptes++;
      if (confiance > 0 & confiance <= 0.4) {
        lienApp.setEvaluation(0);
        liensAppariementSurs.add(lienApp);
        nbLienTresIncertain++;
      } else if (confiance > 0.6 & confiance <= 1) {
        lienApp.setEvaluation(1);
        liensAppariementSurs.add(lienApp);
        nbLienSur++;
      } else if (confiance > 0.4 & confiance <= 0.6) {
        lienApp.setEvaluation(0.5);
        liensAppariementSurs.add(lienApp);
        nbLienIncertain++;
      }
    }
    System.out.println("LiensSurs :" + nbLienSur);
    System.out.println("LiensIncertains :" + nbLienIncertain);
    System.out.println("LiensTrèsIncertains :" + nbLienTresIncertain);
    System.out.println("LiensNonAccepté :" + nbLiensNonAcceptes);

    return liensAppariementSurs;
  }

  // ////////////////////////////////////////////////
  // Evaluation de la qualité du lien d'appariement/
  // ////////////////////////////////////////////////

  /**
   * Mesure de confiance d'un appariement entre points, telle que proposée dans
   * [Beeri et al. 2004]
   * 
   */
  public static double confiance(Lien lien, IFeatureCollection<?> popRef,
      IFeatureCollection<?> popComp, double seuilDistanceMax) {
    // A est l'objet ref du lien

    IFeature A = (IFeature) lien.getObjetsRef().get(0);
    IFeature B = (IFeature) lien.getObjetsComp().get(0);

    // calcul de la distance de A à B (A et B sont les objets ref et comp
    // pointés par le lien this)
    double dAB, dAB2, dBA2, confiance;
    dAB2 = 0;
    dBA2 = 0;

    // dAB=A.getGeom().distance((GM_Object)B.getGeom());
    dAB = Distances.distance(((GM_Point) B.getGeom()).getPosition(),
        ((GM_Point) A.getGeom()).getPosition());
    dAB2 = CalculDistance.deuxiemePlusProcheVoisin(A, B, popComp,
        seuilDistanceMax);
    dBA2 = CalculDistance.deuxiemePlusProcheVoisin(B, A, popRef,
        seuilDistanceMax);

    // calcul de la confiance
    confiance = 1 - dAB / (Math.min(dAB2, dBA2));
    return confiance;
  }

  /**
   * Appariement par calcul de probabilité entre popRef et popComp . Chaque
   * élément A de popRef est apparié avec son plus proche voisin B dans popComp,
   * si et seulement si, la probabilité qua A choisie B (dans popComp) est égale
   * avec la probabilité que B chosie A (dans popRef). 3ème méthode proposée par
   * [Beeri et al 2004]
   * 
   * @param popRef Population d'objets avec une géomtrie ponctuelle.
   * 
   * @param popComp Population d'objets avec une géomtrie ponctuelle.
   * 
   * @return Ensemble de liens d'appariement. Seulement des liens 1-1 sont
   *         créés. Leur évaluation est égale à 1 si on est sûr du résultat, à
   *         0.5 sinon.
   */
  public static EnsembleDeLiens appariementPobabilite(
      IFeatureCollection<?> popRef, IFeatureCollection<?> popComp,
      double seuilDistance, double alpha) {
    EnsembleDeLiens ensembleLiens = new EnsembleDeLiens();
    IFeatureCollection<?> candidatsApp, candidatsRef;
    IFeature objetRef, objetComp, candidatRetenu;
    List<IFeature> listNonApp = new ArrayList<IFeature>();
    Lien lien;
    Integer nbObjApparies, nbObjNonAp, nbLienIncertain, nbLienSur;
    nbObjApparies = 0;
    nbObjNonAp = 0;
    nbLienIncertain = 0;
    nbLienSur = 0;
    double distance, distanceTotaleRefComp = 0, distanceTotaleCompRef = 0, probabObjCompChoisieObjetRef = 0, probabObjRefChoisieObjComp = 0, confiance = 0, confianceMax = 0;

    /*
     * déterminer les liens d'appariement pour chaque objet A ( dans Ref ) on
     * calcule la probabilité qu'il chosie B (dans Comp)
     */

    // Indexation de la population de comparaison et de référence
    if (!popComp.hasSpatialIndex()) {
      System.out.println("Indexation des oronymes");
      popComp.initSpatialIndex(Tiling.class, true, 10);
    }
    if (!popRef.hasSpatialIndex()) {
      System.out.println("Indexation des points remarquable de relief");
      popRef.initSpatialIndex(Tiling.class, true, 10);
    }
    // On parcourt les objets ref un par un
    Iterator<?> itPopRef = popRef.getElements().iterator();
    while (itPopRef.hasNext()) {
      objetRef = (IFeature) itPopRef.next();
      candidatRetenu = null;
      candidatsApp = (IFeatureCollection<?>) popComp.select(
          ((GM_Point) objetRef.getGeom()).getPosition(), seuilDistance);
      if (candidatsApp.size() == 0) {
        listNonApp.add(objetRef);
        nbObjNonAp++;
        continue;
      }
      distanceTotaleRefComp = CalculProbabilites.calculDistanceTotale(objetRef,
          candidatsApp, alpha);
      confianceMax = 0;
      Iterator<?> itPopComp = candidatsApp.getElements().iterator();
      while (itPopComp.hasNext()) {
        objetComp = (IFeature) itPopComp.next();
        candidatsRef = (IFeatureCollection<?>) popRef.select(
            ((GM_Point) objetComp.getGeom()).getPosition(), seuilDistance);
        distanceTotaleCompRef = CalculProbabilites.calculDistanceTotale(
            objetComp, candidatsRef, alpha);
        distance = Distances.distance(
            ((GM_Point) objetRef.getGeom()).getPosition(),
            ((GM_Point) objetComp.getGeom()).getPosition());
        probabObjRefChoisieObjComp = Math.pow(distance, alpha)
            / distanceTotaleRefComp;
        probabObjCompChoisieObjetRef = Math.pow(distance, alpha)
            / distanceTotaleCompRef;
        confiance = Math.sqrt(probabObjRefChoisieObjComp
            * probabObjCompChoisieObjetRef);
        // on garde le candidat avec la confiance le pluc grande
        if (confiance > confianceMax) {
          confianceMax = confiance;
          candidatRetenu = objetComp;
        }
      }

      // Pour chaque objet retenu (il s'agit de l'objet pour lequel la confiance
      // est la plus grande)
      // on calcul le lien d'appariement
      if (candidatRetenu != null) {
        lien = ensembleLiens.nouvelElement();
        lien.addObjetRef(objetRef);
        lien.addObjetComp(candidatRetenu);
        GM_LineString ligne = new GM_LineString();
        ligne.addControlPoint(((GM_Point) objetRef.getGeom()).getPosition());
        ligne.addControlPoint(((GM_Point) candidatRetenu.getGeom())
            .getPosition());
        lien.setGeom(ligne);
        nbObjApparies++;
        lien.setEvaluation(confianceMax);
        if (confianceMax >= 0.5)
          nbLienSur++;
        else
          nbLienIncertain++; // lien incertain;
      } else {
        listNonApp.add(objetRef);
        nbObjNonAp++;
      }
    }
    System.out.println("objets appariés :" + nbObjApparies);
    System.out.println("objets non apparié :" + nbObjNonAp + " sur "
        + popRef.size() + " au total");
    System.out.println("liensSurs :" + nbLienSur);
    System.out.println("liensIncertains :" + nbLienIncertain);

    return ensembleLiens;
  }

  /**
   * la méthode remplissageMatriceApp remplie une matrice de taille [popRef +1,
   * popComp+1] de la manière suivante : en ligne: popRef, en colonne : popComp
   * les éléments de la dernière ligne : représentent la probabilité que l'objet
   * popComp n'est choisie par aucun objet popRef les éléments de la dernière
   * colonne : représentent la probabilité que l'objet popRed n'est choisie par
   * aucun objet popComp 4ème méthode proposée par [Beeri et al 2004]
   */

  public static EnsembleDeLiens remplissageMatriceApp(
      IFeatureCollection<?> popRef, IFeatureCollection<?> popComp,
      double seuilDistance, double alpha) {
    IFeature objetRef, objetComp;
    IFeatureCollection<?> candidatsApp, candidatsRef;
    EnsembleDeLiens ensembleLiens = new EnsembleDeLiens();
    Lien lien;
    List<IFeature> listNonApp = new ArrayList<IFeature>();
    ApproximateMatcher AM = new ApproximateMatcher();
    AM.setIgnoreCase(true);
    double distance, distanceTotaleRefComp, distanceTotaleCompRef, produitRefNonChoisie, probabObjCompChoisieObjetRef, probabObjRefChoisieObjComp, produitProba, produitCompNonChoisie, maxConfianceLigne;
    distanceTotaleRefComp = 0;
    distanceTotaleCompRef = 0;
    probabObjRefChoisieObjComp = 0;
    probabObjCompChoisieObjetRef = 0;
    produitProba = 0;

    double minValue = Double.MIN_VALUE;

    int i, j, nbObjApparies, nbObjNonAp, nbLienSur, nbLienIncertain, liensJustes, liensNonJustes;
    nbObjApparies = 0;
    nbObjNonAp = 0;
    nbLienSur = 0;
    nbLienIncertain = 0;
    liensJustes = 0;
    liensNonJustes = 0;
    System.out.println("comp: " + popComp.size());
    System.out.println("ref: " + popRef.size());

    Matrice matriceRemplie = new Matrice(popRef, popComp);

    // initialisation matrice
    for (i = 0; i < matriceRemplie.nbLignes; i++) {
      for (j = 0; j < matriceRemplie.nbColonnes; j++) {
        matriceRemplie.valeurs[i][j] = 0;
      }
    }
    matriceRemplie.valeurs[matriceRemplie.nbLignes - 1][matriceRemplie.nbColonnes - 1] = 0.0;

    // Indexation de la population de comparaison et de référence
    if (!popComp.hasSpatialIndex()) {
      System.out.println("Indexation des oronymes");
      popComp.initSpatialIndex(Tiling.class, true, 10);
    }
    if (!popRef.hasSpatialIndex()) {
      System.out.println("Indexation des points remarquable de relief");
      popRef.initSpatialIndex(Tiling.class, true, 10);
    }
    // On parcourt les objets ref un par un
    for (i = 0; i < popRef.size(); i++) {
      produitRefNonChoisie = 1;
      objetRef = (IFeature) popRef.getElements().get(i);

      candidatsApp = (IFeatureCollection<?>) popComp.select(
          ((GM_Point) objetRef.getGeom()).getPosition(), seuilDistance);
      if (candidatsApp.size() == 0) {
        listNonApp.add(objetRef);
        nbObjNonAp++;
        matriceRemplie.valeurs[i][matriceRemplie.nbColonnes - 1] = 1;
        continue;
      }
      distanceTotaleRefComp = CalculProbabilites.calculDistanceTotale(objetRef,
          candidatsApp, alpha);

      for (j = 0; j < candidatsApp.size(); j++) {
        objetComp = (IFeature) candidatsApp.getElements().get(j);
        candidatsRef = (IFeatureCollection<?>) popRef.select(
            ((GM_Point) objetComp.getGeom()).getPosition(), seuilDistance);
        distanceTotaleCompRef = CalculProbabilites.calculDistanceTotale(
            objetComp, candidatsRef, alpha);
        distance = Distances.distance(
            ((GM_Point) objetRef.getGeom()).getPosition(),
            ((GM_Point) objetComp.getGeom()).getPosition());
        probabObjRefChoisieObjComp = Math.pow(distance, alpha)
            / distanceTotaleRefComp;
        probabObjCompChoisieObjetRef = Math.pow(distance, alpha)
            / distanceTotaleCompRef;
        produitProba = probabObjRefChoisieObjComp
            * probabObjCompChoisieObjetRef;
        produitRefNonChoisie = produitRefNonChoisie
            * (1 - probabObjRefChoisieObjComp);

        // remplissage de la matrice
        // rang de l'objet comp dans la liste de tous les objets comp
        int k = popComp.getElements().indexOf(objetComp);
        matriceRemplie.valeurs[i][k] = produitProba;
      }
      // remplissage de la dernière colonne
      if (produitRefNonChoisie == 0)
        produitRefNonChoisie = minValue;
      matriceRemplie.valeurs[i][matriceRemplie.nbColonnes - 1] = produitRefNonChoisie;
    }
    for (i = 0; i < popComp.size(); i++) {
      produitCompNonChoisie = 1;
      objetComp = (IFeature) popComp.getElements().get(i);
      candidatsRef = (IFeatureCollection<?>) popRef.select(
          ((GM_Point) objetComp.getGeom()).getPosition(), seuilDistance);
      if (candidatsRef.size() == 0) {
        matriceRemplie.valeurs[matriceRemplie.nbLignes - 1][i] = 1;
        continue;
      }
      distanceTotaleCompRef = CalculProbabilites.calculDistanceTotale(objetComp,
          candidatsRef, alpha);
      for (j = 0; j < candidatsRef.size(); j++) {
        objetRef = (IFeature) candidatsRef.getElements().get(j);
        candidatsApp = (IFeatureCollection<?>) popComp.select(
            ((GM_Point) objetRef.getGeom()).getPosition(), seuilDistance);
        distance = Distances.distance(
            ((GM_Point) objetRef.getGeom()).getPosition(),
            ((GM_Point) objetComp.getGeom()).getPosition());
        probabObjCompChoisieObjetRef = Math.pow(distance, alpha)
            / distanceTotaleCompRef;
        produitCompNonChoisie = produitCompNonChoisie
            * (1 - probabObjCompChoisieObjetRef);
      }
      if (produitCompNonChoisie == 0)
        produitCompNonChoisie = minValue;
      matriceRemplie.valeurs[matriceRemplie.nbLignes - 1][i] = produitCompNonChoisie;
    }
    // matriceRemplie.normalisationMatrice();
    // System.out.println("matrice normalisée");
    // matriceRemplie.affichageMatriceAppariement() ;
    for (i = 0; i < matriceRemplie.nbLignes - 1; i++) {
      maxConfianceLigne = 0;
      int jmax = -1;
      for (j = 0; j < matriceRemplie.nbColonnes - 1; j++) {
        if (matriceRemplie.valeurs[i][j] >= maxConfianceLigne) {
          maxConfianceLigne = matriceRemplie.valeurs[i][j];
          jmax = j;
        }
      }
      if (maxConfianceLigne == 0)
        continue;
      else if (maxConfianceLigne >= matriceRemplie.valeurs[i][matriceRemplie.nbColonnes - 1]) {
        lien = ensembleLiens.nouvelElement();
        objetRef = (IFeature) popRef.getElements().get(i);
        objetComp = (IFeature) popComp.getElements().get(jmax);
        lien.addObjetRef(objetRef);
        lien.addObjetComp(objetComp);
        GM_LineString ligne = new GM_LineString();
        ligne.addControlPoint(((GM_Point) objetRef.getGeom()).getPosition());
        ligne.addControlPoint(((GM_Point) objetComp.getGeom()).getPosition());
        lien.setGeom(ligne);
        // System.out.println("maxConfiance:"+maxConfianceLigne );
        lien.setEvaluation(maxConfianceLigne);
        nbObjApparies++;
      } else {
        objetRef = (IFeature) popRef.getElements().get(i);
        listNonApp.add(objetRef);
        nbObjNonAp++;
      }
    }
    
    LOGGER.info("objets appariés :" + nbObjApparies);
    LOGGER.info("objets non apparié :" + nbObjNonAp + " sur "
        + popRef.size() + " au total");
    LOGGER.info("liensSurs :" + nbLienSur);
    LOGGER.info("liensIncertains :" + nbLienIncertain);
    LOGGER.info("liensJustes :" + liensJustes);
    LOGGER.info("liensNonJustes :" + liensNonJustes);
    return ensembleLiens;
  }
  
  /**
   * Appariement par recherche du plus proche voisin et qui prend en compte les
   * toponymes. Chaque élément de popRef est apparié avec son plus proche voisin
   * dans popComp
   * 
   * @param popComp Population d'objets avec une géomtrie ponctuelle.
   * @param popRef Population d'objets avec une géomtrie ponctuelle.
   * @param seuilDistanceMax Seuil de distance au dessous duquel on n'apparie
   *          pas deux objets.
   * @param attributeRef
   * @param attributeComp
   * 
   * @return Ensemble de liens d'appariement. 
   *         Seulement des liens 1-1 sont créés.
   * 
   */
public static EnsembleDeLiens appariementPPVEvalTop (
      IFeatureCollection<? extends IFeature> popRef, IFeatureCollection<? extends IFeature> popComp,
      double seuilEcart, double seuilDistanceMax,
      String attributeRef, String attributeComp) {
    
    EnsembleDeLiens liens = new EnsembleDeLiens();
    String oronyme, toponyme;
    
    ApproximateMatcher AM = new ApproximateMatcher();
    AM.setIgnoreCase(true);
    
    Lien lien;
    IFeature objetRef, objetComp;
    double distance, distPP;
    
    IFeatureCollection<? extends IFeature> candidatsApp;
    IFeature candidatRetenu = null;
    Iterator<IFeature> itCandidatsApp;
    int ecart, ecartRelatif;

    // Indexation de la population de comparaison
    if (!popComp.hasSpatialIndex()) {
      LOGGER.info("Indexation de popComp");
      popComp.initSpatialIndex(Tiling.class, true, 10);
    }

    int sizeRef = popRef.getElements().size();
    int sizeComp = popComp.getElements().size();
    LOGGER.info("Size popRef " + sizeRef);
    LOGGER.info("Size popComp = " + sizeComp);
    
    // On parcourt les objets ref un par un
    Iterator<IFeature> itPopRef = (Iterator<IFeature>) popRef.getElements().iterator();
    while (itPopRef.hasNext()) {
      objetRef = itPopRef.next();
      candidatsApp = (IFeatureCollection<? extends IFeature>) popComp.select(
          ((GM_Point) objetRef.getGeom()).getPosition(), seuilDistanceMax);
      if (candidatsApp.size() == 0)
        continue;
      // Pour chaque objet ref on calcule la distance à tous les objets comp
      // proches
      // pour ne garder que le plus proche
      itCandidatsApp = (Iterator<IFeature>) candidatsApp.getElements().iterator();
      distPP = seuilDistanceMax;
      while (itCandidatsApp.hasNext()) {
        objetComp = itCandidatsApp.next();
        distance = Distances.distance(
            ((GM_Point) objetRef.getGeom()).getPosition(),
            ((GM_Point) objetComp.getGeom()).getPosition());
        if (distance <= distPP) {
          distPP = distance;
          candidatRetenu = objetComp;
        }
      }
      // On crée un nouveau lien avec sa géométrie et son évaluation
      lien = liens.nouvelElement();
      lien.addObjetRef(objetRef);
      lien.addObjetComp(candidatRetenu);

      GM_LineString ligne = new GM_LineString();
      ligne.addControlPoint(((GM_Point) objetRef.getGeom()).getPosition());
      ligne
          .addControlPoint(((GM_Point) candidatRetenu.getGeom()).getPosition());
      lien.setGeom(ligne);
      oronyme = candidatRetenu.getAttribute(attributeRef).toString();
      oronyme = AM.processAccent(oronyme);
      toponyme = objetRef.getAttribute(attributeComp).toString();
      toponyme = AM.processAccent(toponyme);
      
      ecart = AM.match(toponyme, oronyme);
      ecartRelatif = 100 * ecart
          / Math.max(toponyme.length(), oronyme.length());
      if (ecart <= seuilEcart || oronyme.startsWith(toponyme)) {
        if (ecartRelatif > 50) {
          if (oronyme.startsWith(toponyme) || oronyme.endsWith(toponyme))
            lien.setEvaluation(0.5);
          else
            lien.setEvaluation(0);
        } else if (ecartRelatif >= 10 && ecartRelatif <= 50)
          lien.setEvaluation(0.5);
        else
          lien.setEvaluation(1);
      } else
        lien.setEvaluation(0);
    }
    return liens;
  }

}
