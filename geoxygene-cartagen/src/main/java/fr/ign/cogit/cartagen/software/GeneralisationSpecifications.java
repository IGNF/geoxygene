/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/*
 * Créé le 19 juil. 2005
 */
package fr.ign.cogit.cartagen.software;

import java.io.File;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.software.interfacecartagen.annexes.GeneralisationConfigurationFrame;

/**
 * ensemble de parametres utilises pour la generalisation
 * @author julien Gaffuri
 */
public final class GeneralisationSpecifications {
  @SuppressWarnings("unused")
  private static Logger logger = Logger
      .getLogger(GeneralisationSpecifications.class.getName());

  // les parametres sont en mm carte, ou bien en m terrain
  // NB: distance_terrain_en_m = distance_carte_en_mm *
  // Legend.getSYMBOLISATI0N_SCALE()/1000.0

  // description
  private static String DESCRIPTION = "";

  public static String getDESCRIPTION() {
    return GeneralisationSpecifications.DESCRIPTION;
  }

  public static void setDESCRIPTION(String description) {
    GeneralisationSpecifications.DESCRIPTION = description;
  }

  // resolution but en m terrain
  private static double RESOLUTION = 1.0;

  public static double getRESOLUTION() {
    return GeneralisationSpecifications.RESOLUTION;
  }

  public static void setRESOLUTION(double resolution) {
    GeneralisationSpecifications.RESOLUTION = resolution;
  }

  // bati

  // taille
  // aire minimale que doit avoir tout batiment (en mm²)
  public static double AIRE_MINIMALE_BATIMENT = 0.32;
  // aire en deca de laquelle le batiment est supprime (en m²)
  public static double AIRE_SEUIL_SUPPRESSION_BATIMENT = 70.0;

  // granularite
  // longueur de cote de batiment a partir de laquelle la contrainte de
  // granularite est insatisfaite
  public static double LONGUEUR_MINI_GRANULARITE = 0.3; // en mm

  // equarrite
  // tolerance pour la rotation des segments du batiment (en degres).
  public static double TOLERANCE_ANGLE = 15.0;

  // convexite
  // ecart de convexite a partir duquel la contrainte de convexite est
  // insatisfaite
  public static double BUILDING_CONVEXITE_MINI = 0.01; // 1%

  // elongation
  // ecart d'elongation a partir duquel la contrainte de convexite est
  // insatisfaite
  public static double BUILDING_ELONGATION_MINI = 0.01; // 1%

  // altitude
  // dénivelée minimale (en m)
  public static double DENIVELLEE_MINI = 5.0;

  // proximite
  // distance minimale inter-batiments (en mm carte)
  public static double DISTANCE_SEPARATION_INTER_BATIMENT = 0.1;
  // distance maximale de déplacement d'un batiment (en mm carte)
  public static double DISTANCE_MAX_DEPLACEMENT_BATIMENT = 2.0;
  // distance minimale batiments-routes (en mm carte)
  public static double DISTANCE_SEPARATION_BATIMENT_ROUTE = 0.1;
  // distance minimale étendues eau-routes (en mm carte)
  public static double DISTANCE_SEPARATION_WATER_AREA_ROAD = 10;
  // lorsque les batiments ne peuvent pas etre deplaces pour resoudre conflit de
  // superposition, le batiment le plus en coflit est supprime.
  // ce seuil est le taux de superposition minimal que doit acvoir ce batiment
  // pour etre supprime
  public static double SEUIL_TAUX_SUPERPOSITION_SUPPRESSION = 0.3;
  // distance seuil en m (a mettre en mm plutôt?) pour definition de relation
  // de
  // proximite entre objets de l'ilot
  public static double DISTANCE_MAX_PROXIMITE = 30.0;

  // densite
  // Ratio max d'augmentation de la densité d'un ilot
  public static double RATIO_BLOCK_DENSITY = 1.0;
  // Densité limite avant grisage d'un ilot
  public static double DENSITE_LIMITE_GRISAGE_ILOT = 0.50;
  public static double DENSITE_RATIO_REDUCTION_MAX = 0.50;

  // conservation des grands batiments
  // aire (en m2) des grands batiments
  public static double GRANDS_BATIMENTS_AIRE = 600.0;

  // routier

  // empatement
  // Coeff de propagation pour les modifs routieres quand empatement
  public static double ROUTIER_COEFF_PROPAGATION_EMPATEMENT = 0.2;

  // impasse
  // longueur minimale d'une impasse (en m terrain)
  public static double ROADS_DEADEND_MIN_LENGTH = 2.0;

  // densite
  // total road symbol area / town area
  public static double ROAD_TOWN_DENSITY = 0.12;

  // hydro

  // proximite routier
  // distance de separation entre troncons hydrographiques et routiers (en mm
  // carte)
  public static double DISTANCE_SEPARATION_HYDRO_ROUTIER = 0.4;
  // le taux utilise pour calcul de satisfaction de contrainte de proximite
  // hydro routier
  public static double TAUX_SUPERPOSITION_HYDRO_ROUTIER = 0.02;

  // relief

  // distance de separation entre courbes de niveau en mm
  public static double DISTANCE_SEPARATION_INTER_CN = 0.1;

  /**
   * 
   * @param fichier
   */
  public static void enregistrer(File fichier) {
    JOptionPane.showMessageDialog(
        GeneralisationConfigurationFrame.getInstance(),
        "Fonctionnalité à venir... un jour... peut-etre..." + fichier,
        "A venir", JOptionPane.INFORMATION_MESSAGE);
  }

}
