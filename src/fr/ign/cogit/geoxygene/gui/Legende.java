package fr.ign.cogit.geoxygene.gui;

import java.awt.Color;

/**
 * la legende: defini la symbolisation des objets
 * @author julien Gaffuri 29 janv. 2009
 * 
 */
public class Legende {

  // echelle de symbolisation
  private static double ECHELLE_SYMBOLISATI0N = 15000.0;

  public static double getECHELLE_SYMBOLISATION() {
    return Legende.ECHELLE_SYMBOLISATI0N;
  }

  public static void setECHELLE_SYMBOLISATION(double echelle_cible) {
    Legende.ECHELLE_SYMBOLISATI0N = echelle_cible;
  }

  // courbes de niveau
  public static Color CN_COULEUR = Color.ORANGE;
  public static double CN_LARGEUR_NORMALE = 0.15;
  public static double CN_LARGEUR_MAITRESSE = 0.3;
  public static double CN_EQUIDISTANCE_MAITRESSE = 100.0;

  // points cotes
  public static Color PTS_COTES_COULEUR = Color.ORANGE;
  public static double PTS_COTES_LARGEUR = 0.5;

  // zone arboree
  public static final Color ZA_COULEUR_VIDE = new Color(235, 235, 235);
  public static final Color ZA_COULEUR_FORET = new Color(163, 234, 111);
  public static final Color ZA_COULEUR_CONTOUR_FORET = new Color(91, 208, 91);
  public static final double ZA_LARGEUR_CONTOUR_FORET = 0.1;

  // administratif
  public static final Color ADMIN_COULEUR = new Color(200, 200, 200);
  public static final double ADMIN_LARGEUR = 0.15;
  public static final float[] ADMIN_POINTILLES = new float[] { 0, 6, 0, 6 };

}
