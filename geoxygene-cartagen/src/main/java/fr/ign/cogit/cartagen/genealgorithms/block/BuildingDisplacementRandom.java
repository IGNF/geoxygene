/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.genealgorithms.block;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.spatialanalysis.measures.BlockBuildingsMeasures;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * 
 * algorithme de deplacement de batiments d'un ilot base sur une methode de
 * descente de gradient stochastique. le principe est de: 1. choisir un batiment
 * au hasard 2. faire subir a ce batiment un deplacement aleatoire 3. valider ce
 * deplacement ssi il a permis d'ameliorer le conflit du batiment. le conflit du
 * batiment est calculee en fonction de la part de sa surface superposee aux
 * autres objets desquels il doit s'eloigner (routes de l'ilot, autres
 * batiments)
 * 
 * Ces essais sont repetes jusqu'a ce que tout les batiments soient suffisamment
 * loin les uns des autres ou que le processus ne parvient plus a ameliorer
 * l'etat de l'ilot.
 * 
 * @author JGaffuri
 * @author GTouya
 * 
 */
public class BuildingDisplacementRandom {
  private static Logger logger = Logger
      .getLogger(BuildingDisplacementRandom.class.getName());

  private static double facteurLongueurDeplacement = 2;
  private static int nbEssaisParBatiment = 5;

  public static void compute(IUrbanBlock ai) {

    // recupere la moyenne des taux de superposition des batiments de l'ilot
    // (evalue la congestion moyenne des batiments de l'ilot)
    double tauxMoy = BlockBuildingsMeasures.getBuildingsOverlappingRateMean(ai);

    // fixe le nombre d'essais a faire: disons 30 essais par batiment.
    int essaisMax = nbEssaisParBatiment
        * BlockBuildingsMeasures.getBlockNonDeletedBuildingsNumber(ai);
    // le nombre d'essais infructueux
    int essais = 0;

    // tente de faire des deplacements de batimens au hasard tant que la moyenne
    // des taux de superposition des batiments de l'ilot est non nulle
    // ou que le nombre d'essais infructueux est plus petit que le nombre
    // maximum
    while (tauxMoy > 0 && essais < essaisMax) {

      // choisi un batiment en conflit au hasard
      IUrbanElement ab = ai.getUrbanElements()
          .get((int) (Math.random() * ai.getUrbanElements().size()));

      // si le batiment est supprimme, passer au suivant
      if (ab.isDeleted()) {
        continue;
      }

      // calcul de l'etat de superposition
      double taux = BlockBuildingsMeasures.getBuildingOverlappingRate(ab, ai);

      // si le batiment n'est pas superpose, passer au suivant
      if (taux == 0.0) {
        continue;
      }

      // tente de le déplacer au hasard (!)
      // direction aleatoire
      double angle = Math.random() * 2 * Math.PI;

      // longueur du deplacement dependant de la resolution des donnees
      double lg = Math.random() * facteurLongueurDeplacement
          * GeneralisationSpecifications.getRESOLUTION();
      double dx = Math.cos(angle) * lg;
      double dy = Math.sin(angle) * lg;

      // effectue la translation
      ab.setGeom(CommonAlgorithms.translation(ab.getGeom(), dx, dy));
      ab.registerDisplacement();

      // calcul de la nouvelle valeur de congestion moyenne des batiments de
      // l'ilot
      double nouvTauxMoy = BlockBuildingsMeasures
          .getBuildingsOverlappingRateMean(ai);

      // si la congestion n'a pas ete amelioree par le batiment
      // ou bien si le deplacement a trop deplace le batiment (il sort de son
      // ilot)
      // ou bien si le batiment est trop loin de sa position initiale

      if (nouvTauxMoy >= tauxMoy
          || !ai.getGeom().contains(new GM_Point(ab.getGeom().centroid()))
          || ab.getGeom().centroid()
              .distance(ab.getInitialGeom()
                  .centroid()) > GeneralisationSpecifications.DISTANCE_MAX_DEPLACEMENT_BATIMENT
                      * Legend.getSYMBOLISATI0N_SCALE() / 1000.0) {

        // annule le deplacement
        ab.setGeom(CommonAlgorithms.translation(ab.getGeom(), -dx, -dy));

        // incremente le nombre d'essais infructueux
        essais++;

        // passer au suivant
        continue;
      }

      // sinon, accepter le deplacement
      essais = 0;
      tauxMoy = nouvTauxMoy;
      if (logger.isTraceEnabled()) {
        logger.trace("taux=" + tauxMoy);
      }
    }
  }

  public static void compute(List<IBuilding> buildings,
      Collection<IRoadLine> roads, Collection<IWaterLine> rivers) {

    // recupere la moyenne des taux de superposition des batiments de l'ilot
    // (evalue la congestion moyenne des batiments de l'ilot)
    double tauxMoy = getBuildingsOverlappingRateMean(buildings, roads, rivers);

    // fixe le nombre d'essais a faire: disons 30 essais par batiment.
    int essaisMax = nbEssaisParBatiment * getNbNonDelBuildings(buildings);
    // le nombre d'essais infructueux
    int essais = 0;

    // tente de faire des deplacements de batimens au hasard tant que la moyenne
    // des taux de superposition des batiments de l'ilot est non nulle
    // ou que le nombre d'essais infructueux est plus petit que le nombre
    // maximum
    while (tauxMoy > 0 && essais < essaisMax) {

      // choisi un batiment en conflit au hasard
      IBuilding ab = buildings.get((int) (Math.random() * buildings.size()));

      // si le batiment est supprimme, passer au suivant
      if (ab.isDeleted()) {
        continue;
      }

      // calcul de l'etat de superposition
      double taux = getBuildingOverlap(ab, buildings, roads, rivers);

      // si le batiment n'est pas superpose, passer au suivant
      if (taux == 0.0) {
        continue;
      }

      // tente de le déplacer au hasard (!)
      // direction aleatoire
      double angle = Math.random() * 2 * Math.PI;

      // longueur du deplacement dependant de la resolution des donnees
      double lg = Math.random() * facteurLongueurDeplacement
          * GeneralisationSpecifications.getRESOLUTION();
      double dx = Math.cos(angle) * lg;
      double dy = Math.sin(angle) * lg;

      // effectue la translation
      ab.setGeom(CommonAlgorithms.translation(ab.getGeom(), dx, dy));
      ab.registerDisplacement();

      // calcul de la nouvelle valeur de congestion moyenne des batiments de
      // l'ilot
      double nouvTauxMoy = getBuildingsOverlappingRateMean(buildings, roads,
          rivers);

      // si la congestion n'a pas ete amelioree par le batiment
      // ou bien si le batiment est trop loin de sa position initiale

      if (nouvTauxMoy >= tauxMoy || ab.getGeom().centroid()
          .distance(ab.getInitialGeom()
              .centroid()) > GeneralisationSpecifications.DISTANCE_MAX_DEPLACEMENT_BATIMENT
                  * Legend.getSYMBOLISATI0N_SCALE() / 1000.0) {

        // annule le deplacement
        ab.setGeom(CommonAlgorithms.translation(ab.getGeom(), -dx, -dy));

        // incremente le nombre d'essais infructueux
        essais++;

        // passer au suivant
        continue;
      }

      // sinon, accepter le deplacement
      essais = 0;
      tauxMoy = nouvTauxMoy;
      if (logger.isTraceEnabled()) {
        logger.trace("taux=" + tauxMoy);
      }

    }
  }

  private static int getNbNonDelBuildings(Collection<IBuilding> buildings) {
    int nb = buildings.size();
    for (IBuilding b : buildings) {
      if (b.isDeleted())
        nb--;
    }
    return nb;
  }

  /**
   * @return the part of the urban element which is overlapping with the other
   *         objects buildings, rivers and roads - takes into account a
   *         separation distance.
   */
  private static IGeometry getBuildingOverlappingGeometry(IBuilding building,
      List<IBuilding> buildings, Collection<IRoadLine> roads,
      Collection<IWaterLine> rivers) {
    if (building.isDeleted()) {
      return null;
    }
    if (!buildings.contains(building)) {
      return null;
    }
    IGeometry geomSuperposition = null;
    double distSep;

    // superposition avec les autres batiments de l'ilot
    distSep = GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_BATIMENT
        * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    for (IBuilding b : buildings) {
      if (b == building) {
        continue;
      }
      if (b.isDeleted()) {
        continue;
      }
      if (b.getSymbolGeom().distance(building.getSymbolGeom()) > distSep) {
        continue;
      }
      IGeometry intersection = CommonAlgorithms
          .buffer(b.getSymbolGeom(), distSep)
          .intersection(building.getSymbolGeom());
      if (geomSuperposition == null) {
        geomSuperposition = intersection;
      } else {
        geomSuperposition = intersection.union(geomSuperposition);
      }
    }

    // superposition avec les troncons de l'ilot
    distSep = GeneralisationSpecifications.DISTANCE_SEPARATION_BATIMENT_ROUTE
        * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    for (INetworkSection tr : roads) {
      if (tr.isDeleted()) {
        continue;
      }
      IGeometry emp = SectionSymbol.getSymbolExtent(tr);
      if (emp.distance(building.getSymbolGeom()) > distSep) {
        continue;
      }
      IGeometry intersection = emp.buffer(distSep)
          .intersection(building.getSymbolGeom());
      if (geomSuperposition == null) {
        geomSuperposition = intersection;
      } else {
        geomSuperposition = intersection.union(geomSuperposition);
      }
    }
    for (INetworkSection tr : rivers) {
      if (tr.isDeleted()) {
        continue;
      }
      IGeometry emp = SectionSymbol.getSymbolExtent(tr);
      if (emp.distance(building.getSymbolGeom()) > distSep) {
        continue;
      }
      IGeometry intersection = emp.buffer(distSep)
          .intersection(building.getSymbolGeom());
      if (geomSuperposition == null) {
        geomSuperposition = intersection;
      } else {
        geomSuperposition = intersection.union(geomSuperposition);
      }
    }
    return geomSuperposition;
  }

  /**
   * @return the urban element overlapping rates, within [0,1]
   */
  private static double getBuildingOverlap(IBuilding building,
      List<IBuilding> buildings, Collection<IRoadLine> roads,
      Collection<IWaterLine> rivers) {
    IGeometry overGeom = getBuildingOverlappingGeometry(building, buildings,
        roads, rivers);
    if (overGeom == null || overGeom.isEmpty()) {
      return 0.0;
    }
    return overGeom.area();
  }

  /**
   * @return the buildings overlapping rates mean, within [0,1]
   */
  private static double getBuildingsOverlappingRateMean(
      List<IBuilding> buildings, Collection<IRoadLine> roads,
      Collection<IWaterLine> rivers) {
    if (buildings.size() == 0) {
      return 0.0;
    }
    double mean = 0.0;
    int nb = 0;
    for (IBuilding b : buildings) {
      if (b.isDeleted()) {
        continue;
      }
      mean += getBuildingOverlap(b, buildings, roads, rivers);
      nb++;
    }
    if (nb != 0) {
      mean /= nb;
    }
    return mean;
  }

  // ************************************************************//
  // Same algorithm, but works with simple IFeature instances //
  // ************************************************************//

  public static void computeFeats(List<IFeature> buildings,
      Collection<IFeature> networkSections) {

    Map<IFeature, IPolygon> initialGeoms = new HashMap<>();
    for (IFeature b : buildings)
      initialGeoms.put(b, (IPolygon) b.getGeom());

    // recupere la moyenne des taux de superposition des batiments de l'ilot
    // (evalue la congestion moyenne des batiments de l'ilot)
    double tauxMoy = getBuildingsOverlappingRateMeanFeats(buildings,
        networkSections);

    // fixe le nombre d'essais a faire: disons 30 essais par batiment.
    int essaisMax = nbEssaisParBatiment * getNbNonDelBuildingsFeats(buildings);
    // le nombre d'essais infructueux
    int essais = 0;

    // tente de faire des deplacements de batimens au hasard tant que la moyenne
    // des taux de superposition des batiments de l'ilot est non nulle
    // ou que le nombre d'essais infructueux est plus petit que le nombre
    // maximum
    while (tauxMoy > 0 && essais < essaisMax) {

      // choisi un batiment en conflit au hasard
      IFeature ab = buildings.get((int) (Math.random() * buildings.size()));

      // si le batiment est supprimme, passer au suivant
      if (ab.isDeleted()) {
        continue;
      }

      // calcul de l'etat de superposition
      double taux = getBuildingOverlap(ab, buildings, networkSections);

      // si le batiment n'est pas superpose, passer au suivant
      if (taux == 0.0) {
        continue;
      }

      // tente de le déplacer au hasard (!)
      // direction aleatoire
      double angle = Math.random() * 2 * Math.PI;

      // longueur du deplacement dependant de la resolution des donnees
      double lg = Math.random() * facteurLongueurDeplacement
          * GeneralisationSpecifications.getRESOLUTION();
      double dx = Math.cos(angle) * lg;
      double dy = Math.sin(angle) * lg;

      // effectue la translation
      ab.setGeom(CommonAlgorithms.translation(ab.getGeom(), dx, dy));

      // calcul de la nouvelle valeur de congestion moyenne des batiments de
      // l'ilot
      double nouvTauxMoy = getBuildingsOverlappingRateMeanFeats(buildings,
          networkSections);

      // si la congestion n'a pas ete amelioree par le batiment
      // ou bien si le batiment est trop loin de sa position initiale

      if (nouvTauxMoy >= tauxMoy || ab.getGeom().centroid()
          .distance(initialGeoms.get(ab)
              .centroid()) > GeneralisationSpecifications.DISTANCE_MAX_DEPLACEMENT_BATIMENT
                  * Legend.getSYMBOLISATI0N_SCALE() / 1000.0) {

        // annule le deplacement
        ab.setGeom(CommonAlgorithms.translation(ab.getGeom(), -dx, -dy));

        // incremente le nombre d'essais infructueux
        essais++;

        // passer au suivant
        continue;
      }

      // sinon, accepter le deplacement
      essais = 0;
      tauxMoy = nouvTauxMoy;
      if (logger.isTraceEnabled()) {
        logger.trace("taux=" + tauxMoy);
      }

    }
  }

  private static int getNbNonDelBuildingsFeats(Collection<IFeature> buildings) {
    int nb = buildings.size();
    for (IFeature b : buildings) {
      if (b.isDeleted())
        nb--;
    }
    return nb;
  }

  /**
   * @return the part of the urban element which is overlapping with the other
   *         objects buildings, rivers and roads - takes into account a
   *         separation distance.
   */
  private static IGeometry getBuildingOverlappingGeometry(IFeature building,
      List<IFeature> buildings, Collection<IFeature> networkSections) {
    if (building.isDeleted()) {
      return null;
    }
    if (!buildings.contains(building)) {
      return null;
    }
    IGeometry geomSuperposition = null;
    double distSep;

    // superposition avec les autres batiments de l'ilot
    distSep = GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_BATIMENT
        * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    for (IFeature b : buildings) {
      if (b == building) {
        continue;
      }
      if (b.isDeleted()) {
        continue;
      }

      if (b.getGeom().distance(building.getGeom()) > distSep) {
        continue;
      }
      IGeometry intersection = CommonAlgorithms.buffer(b.getGeom(), distSep)
          .intersection(building.getGeom());
      if (geomSuperposition == null) {
        geomSuperposition = intersection;
      } else {
        geomSuperposition = intersection.union(geomSuperposition);
      }
    }

    // superposition avec les troncons de l'ilot
    distSep = GeneralisationSpecifications.DISTANCE_SEPARATION_BATIMENT_ROUTE
        * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    for (IFeature tr : networkSections) {
      if (tr.isDeleted()) {
        continue;
      }
      IGeometry emp = SectionSymbol.getSymbolExtent(tr);
      if (emp.distance(building.getGeom()) > distSep) {
        continue;
      }
      IGeometry intersection = emp.buffer(distSep)
          .intersection(building.getGeom());
      if (geomSuperposition == null) {
        geomSuperposition = intersection;
      } else {
        geomSuperposition = intersection.union(geomSuperposition);
      }
    }

    return geomSuperposition;
  }

  /**
   * @return the urban element overlapping rates, within [0,1]
   */
  private static double getBuildingOverlap(IFeature building,
      List<IFeature> buildings, Collection<IFeature> networkSections) {
    IGeometry overGeom = getBuildingOverlappingGeometry(building, buildings,
        networkSections);
    if (overGeom == null || overGeom.isEmpty()) {
      return 0.0;
    }
    return overGeom.area();
  }

  /**
   * @return the buildings overlapping rates mean, within [0,1]
   */
  private static double getBuildingsOverlappingRateMeanFeats(
      List<IFeature> buildings, Collection<IFeature> networkSections) {
    if (buildings.size() == 0) {
      return 0.0;
    }
    double mean = 0.0;
    int nb = 0;
    for (IFeature b : buildings) {
      if (b.isDeleted()) {
        continue;
      }
      mean += getBuildingOverlap(b, buildings, networkSections);
      nb++;
    }
    if (nb != 0) {
      mean /= nb;
    }
    return mean;
  }

}
