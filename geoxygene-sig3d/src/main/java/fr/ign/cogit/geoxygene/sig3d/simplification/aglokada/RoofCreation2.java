package fr.ign.cogit.geoxygene.sig3d.simplification.aglokada;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.sig3d.calculation.Proximity;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;

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
 * Autre essaie d'algorithme permettant de simplifier des toits
 * 
 * Test for roof simplification
 * 
 */

public class RoofCreation2 {

  // On a besoin :

  // IL revient toujours plus fort

  // Du batiment initial en ligne
  // Du batiment final
  // Des pans de toit

  // L'idée est de mapper l'ancien toit sur le nouveau bâtiment

  public static List<IOrientableSurface> roof(IDirectPositionList lSegBuilding,
      List<IOrientableSurface> lFacesRoofs, double threshold, double bufferSize) {

    int nbSegInit = lSegBuilding.size();

    DirectPositionList lSommetBatiment = new DirectPositionList();

    IDirectPosition pIni = lSegBuilding.get(nbSegInit - 1);
    lSommetBatiment.add(pIni);

    for (int i = 0; i < nbSegInit; i++) {

      IDirectPosition pAct = lSegBuilding.get(i);

      if (pAct.equals(pIni, 0.01)) {
        continue;
      }

      lSommetBatiment.add(pAct);
      pIni = pAct;

    }

    ArrayList<IOrientableSurface> toit = new ArrayList<IOrientableSurface>();

    // il s'agit du z de ref
    double z = lSommetBatiment.get(0).getZ();

    int nombrePans = lFacesRoofs.size();

    // On modifie individuellement chaque pan de toits

    for (int i = 0; i < nombrePans; i++) {
      IOrientableSurface faceTemp = lFacesRoofs.get(i);

      // Indique si un point est accepté
      boolean pointAccept = false;

      // Il y a des points communs

      IDirectPositionList lPointFaces = faceTemp.coord();
      int nbPoints = lPointFaces.size();

      // Liste de points qui formeront le toit
      DirectPositionList lPointToits = new DirectPositionList();

      // On traite chaque point du pan
      // pour savoir si il vaut mieux le garder ou pas
      for (int j = 0; j < nbPoints; j++) {

        IDirectPosition pTemp = lPointFaces.get(j);

        // il s'agit d'une arrète du toit, on le garde sans autre procès
        if (pTemp.getZ() > z + threshold / 2) {
          lPointToits.add(pTemp);

          continue;

        }
        Proximity cp = new Proximity();
        // On récupère le point le plus proche dans la liste des toits
        // du batiments
        // du point actuel

        cp.nearest(pTemp, lSommetBatiment);

        if (cp.distance > bufferSize + threshold) {

          // Le point du toit ne correspond à aucun point du batiment
          // simplifié
          // On l'ignore

          continue;
        }

        pointAccept = true;

        IDirectPosition pProche = cp.nearest;

        if (lPointToits.size() == 0) {
          lPointToits.add(pProche);
          continue;
        }

        if (!pProche.equals2D(lPointToits.get(lPointToits.size() - 1), 0.1)) {
          lPointToits.add(pProche);
        }

      }

      // On construit une surface supplémentaire qui constituera le toit
      if (pointAccept) {

        // Les points sont ajoutés par segment

        GM_LineString ls = new GM_LineString(lPointToits);
        GM_OrientableSurface oS = new GM_Polygon(ls);

        toit.add(oS);
      }
    }

    return toit;

  }

  // L'idée est de mapper l'ancien toit sur le nouveau bâtiment

  public static ArrayList<IOrientableSurface> roofIni(
      IDirectPositionList lBuildingSummit,
      ArrayList<IOrientableSurface> lFacesRoofs, double threshold,
      double bufferSize) {

    ArrayList<IOrientableSurface> toit = new ArrayList<IOrientableSurface>();

    // il s'agit du z de ref
    double z = lBuildingSummit.get(0).getZ();

    int nombrePans = lFacesRoofs.size();
    int nbpointsatraiter = lBuildingSummit.size();

    // Faire une correspondance entre les points du toits et du sommet du
    // batiment simplifié
    // Il s'agit d'une relation par numéro de lignes
    DirectPositionList lPointsCorresToits = new DirectPositionList();
    double[] distance = new double[nbpointsatraiter];
    Proximity cp = new Proximity();
    // On effectue le traitement pour chaquue sommet de l'ancien bâtiment
    for (int i = 0; i < nbpointsatraiter; i++) {

      // On lance l'Opérateur de calcul de proximité
      // on calcule le point du batiment simplifié le plus proche du toit
      // initial

      IDirectPosition tempP = lBuildingSummit.get(i);
      cp.nearest(tempP, lFacesRoofs);

      // C'est le point le plus proche dans cette facette d'un point du
      // batiment simplifié
      IDirectPosition pointCorres = cp.nearest2;

      // On complète le tableau de distances
      distance[i] = cp.distance;

      // On associe le point simplifié au point le plus proche du toit
      lPointsCorresToits.add(pointCorres);

    }

    // On modifie individuellement chaque pan de toits

    for (int i = 0; i < nombrePans; i++) {
      IOrientableSurface faceTemp = lFacesRoofs.get(i);

      // Il y a des points communs
      IDirectPositionList lPointFaces = faceTemp.coord();
      int nbPoints = lPointFaces.size();

      // Liste de points qui formeront le toit
      IDirectPositionList lPointToits = new DirectPositionList();

      // On traite chaque point du pan
      // pour savoir si il vaut mieux le garder ou pas
      for (int j = 0; j < nbPoints; j++) {

        IDirectPosition pTemp = lPointFaces.get(j);

        // il s'agit d'une arrète du toit, on le garde sans autre procès
        if (pTemp.getZ() > z + threshold / 2) {
          lPointToits.add(pTemp);
          continue;

        }

        // On récupère le point le plus proche dans la liste des toits
        // du batiments
        // du point actuel

        cp.nearest(pTemp, lBuildingSummit);

        if (cp.distance > bufferSize / 2 + threshold) {

          // Le point du toit ne correspond à aucun point du batiment
          // simplifié
          // On l'ignore

          continue;
        }

        // Sinon on l'ajoute
        lPointToits.add(cp.nearest);
      }

      // On construit une surface supplémentaire qui constituera le toit
      GM_LineString ls = new GM_LineString(lPointToits);
      GM_OrientableSurface oS = new GM_Polygon(ls);

      toit.add(oS);
    }

    return toit;

  }

}
