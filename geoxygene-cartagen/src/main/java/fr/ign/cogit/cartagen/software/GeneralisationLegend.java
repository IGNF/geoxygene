/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.cartagen.software;

import java.awt.Color;

import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;

/**
 * @author julien Gaffuri
 * 
 */
public class GeneralisationLegend {

  // batiments
  public static Color COULEUR_INTERIEUR_BATIMENTS = new Color(160, 160, 160);
  public static Color COULEUR_BORD_BATIMENTS = Color.DARK_GRAY;
  public static double LARGEUR_BORD_BATIMENTS_MM = 0.0;

  // alignements urbains
  public static Color ALIGNEMENT_SURFACE_COULEUR = new Color(115, 220, 115, 150);
  public static Color ALIGNEMENT_CONTOUR_COULEUR = new Color(115, 220, 115, 150);
  public static double ALIGNEMENT_CONTOUR_LARGEUR = 0.0;

  // ilots colores
  public static Color ILOTS_GRISES_COULEUR = Color.LIGHT_GRAY;

  // villes et ilots
  public static Color VILLE_SURFACE_COULEUR = new Color(254, 247, 158);
  public static Color VILLE_CONTOUR_COULEUR = new Color(254, 247, 158);
  public static double VILLE_CONTOUR_LARGEUR = 0.0;
  public static Color ILOT_SURFACE_COULEUR = new Color(255, 255, 255);
  public static Color ILOT_CONTOUR_COULEUR = Color.MAGENTA;
  public static double ILOT_CONTOUR_LARGEUR = 0.1;

  // courbes de niveau
  public static Color CN_COULEUR = new Color(210, 124, 30);
  public static double CN_LARGEUR_NORMALE = 0.07;
  public static double CN_LARGEUR_MAITRESSE = 0.15;
  public static double CN_EQUIDISTANCE_MAITRESSE = 100.0;

  // points cotes
  public static Color PTS_COTES_COULEUR = new Color(210, 124, 30);
  public static double PTS_COTES_LARGEUR = 0.5;

  // relief elements
  public static Color RELIEF_ELEM_LINE_COULEUR = new Color(167, 103, 38);
  public static double RELIEF_ELEM_LINE_LARGEUR = 0.8;

  // teintes hypsometriques
  private static Color[] teintesHypsometriques = new Color[] {
      new Color(123, 189, 8), new Color(189, 214, 99), new Color(173, 189, 90),
      new Color(247, 222, 66), new Color(214, 148, 66) };

  public static Color getTeinteHypsometrique(double z) {
    double d = (CartAGenDoc.getInstance().getCurrentDataset().getReliefField()
        .getZMax() - CartAGenDoc.getInstance().getCurrentDataset()
        .getReliefField().getZMin())
        / GeneralisationLegend.teintesHypsometriques.length;
    return GeneralisationLegend.teintesHypsometriques[(int) ((z - CartAGenDoc
        .getInstance().getCurrentDataset().getReliefField().getZMin()) / d)];
  }

  // zone arboree
  public static final Color ZA_COULEUR_VIDE = new Color(235, 235, 235);
  public static final Color ZA_COULEUR_FORET = new Color(163, 234, 111);
  public static final Color ZA_COULEUR_CONTOUR_FORET = new Color(91, 208, 91);
  public static final double ZA_LARGEUR_CONTOUR_FORET = 0.1;
  // zone d'activité
  public static final Color ZA_COULEUR_ACTIVITE = new Color(201, 160, 220);
  public static final Color ZA_COULEUR_CONTOUR_ACTIVITE = new Color(201, 160,
      220);
  public static final double ZA_LARGEUR_CONTOUR_ACTIVITE = 0.1;

  // administratif
  public static final Color ADMIN_COULEUR = new Color(200, 200, 200);
  public static final double ADMIN_LARGEUR = 0.3;
  public static final float ADMIN_POINTILLES = 10;
  public static final Color ISLAND_OUTLINE_COLOR = new Color(37, 253, 233);
  public static final double ISLAND_OUTLINE_WIDTH = 0.4;

  // masque (limites du jeu de donnees)
  public static Color MASQUE_COULEUR = Color.LIGHT_GRAY;
  public static double MASQUE_LARGEUR = 0.1;

  // reseau routier

  public static final Color ROUTIER_COULEUR_DESSOUS = Color.DARK_GRAY;

  // chemin
  public static final Color ROUTIER_COULEUR_0 = Color.DARK_GRAY;
  public static final double ROUTIER_LARGEUR_DESSOUS_0 = 0.15;
  public static final double ROUTIER_LARGEUR_DESSUS_0 = 0.15;

  // route
  public static final Color ROUTIER_COULEUR_1 = new Color(240, 240, 240);
  public static final double ROUTIER_LARGEUR_DESSOUS_1 = 0.5;
  public static final double ROUTIER_LARGEUR_DESSUS_1 = 0.3;

  // route secondaire
  public static final Color ROUTIER_COULEUR_2 = new Color(244, 255, 116);
  public static final double ROUTIER_LARGEUR_DESSOUS_2 = 0.65;
  public static final double ROUTIER_LARGEUR_DESSUS_2 = 0.45;

  // route principale
  public static final Color ROUTIER_COULEUR_3 = new Color(255, 94, 0);
  public static final double ROUTIER_LARGEUR_DESSOUS_3 = 0.9;
  public static final double ROUTIER_LARGEUR_DESSUS_3 = 0.7;

  // autoroute
  public static final Color ROUTIER_COULEUR_4 = Color.BLUE;
  public static final double ROUTIER_LARGEUR_DESSOUS_4 = 1.15;
  public static final double ROUTIER_LARGEUR_DESSUS_4 = 1.15;
  public static final double ROUTIER_LARGEUR_SEPARATEUR_4 = 0.2;
  public static final Color ROUTIER_COULEUR_SEPARATEUR_4 = Color.WHITE;

  // reseau hydrographique
  public static final Color RES_EAU_COULEUR = new Color(116, 170, 232);
  public static final double RES_EAU_LARGEUR = 0.2;
  public static final Color SURFACE_EAU_COULEUR = new Color(116, 170, 232);
  public static final Color SURFACE_EAU_COULEUR_CONTOUR = new Color(116, 170,
      232);
  public static final double SURFACE_EAU_LARGEUR_CONTOUR = 0.1;

  // reseau ferre
  public static final Color RES_FER_COULEUR = Color.BLACK;
  public static final double RES_FER_LARGEUR = 0.2;

  // reseau electrique
  public static final Color RES_ELEC_COULEUR = new Color(100, 100, 100);
  public static final double RES_ELEC_LARGEUR = 0.1;

  // miscellaneous features
  public static Color AIRPORT_SURFACE_COULEUR = Color.PINK;
  public static Color AIRPORT_CONTOUR_COULEUR = Color.PINK;
  public static double AIRPORT_CONTOUR_LARGEUR = 0.0;
  public static Color RUNWAY_SURFACE_COULEUR = Color.LIGHT_GRAY;
  public static Color RUNWAY_CONTOUR_COULEUR = Color.DARK_GRAY;
  public static double RUNWAY_CONTOUR_LARGEUR = 0.0;
}
