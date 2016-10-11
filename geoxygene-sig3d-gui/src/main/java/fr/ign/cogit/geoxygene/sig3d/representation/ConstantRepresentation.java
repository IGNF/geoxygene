package fr.ign.cogit.geoxygene.sig3d.representation;

import java.awt.Color;

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
 * Classe permettant de définir les facteurs d'échelles pour l'affichage This
 * class enable to parametrize some rendering constants
 */

public final class ConstantRepresentation {

  /**
   * Pour indiquer si on affiche dans la console le nombre d'images par secondes
   */
  public static boolean visualisingFPS = false;

  /**
   * Facteur d'echelle x,y
   */
  public static double scaleFactor = 1;

  /**
   * Pour savoir si il faut ou nom appliquer des facteurs d'échelle
   */
  public static double scaleFactorZ = 1;

  /**
   * Il s'agit de la distance minimale d'affichage Tout objet qui est à une
   * distance inférieure de la caméra ne sera pas affiché.
   */
  public static double frontClip = 5;

  /**
   * Il s'agit de la distance maximale d'affichage Tout objet qui est à une
   * distance supérieure de la caméra ne sera pas affiché.
   */
  public static double backClip = 1000;

  /**
   * Si true tient compte de l'orientation pour l'affichage sinon non utile pour
   * détecter les face mal orienté (objet inversé ou troué)
   */
  public static boolean cullMode = false;

  /**
   * Couleur de sélection des objets
   */
  public static Color selectionColor = new Color(0, 255, 255);
  
  
  /**
   * Couleur du fond de l'environnement 3D
   */
  public static Color backGroundColor = new Color(255, 255, 255);

}
