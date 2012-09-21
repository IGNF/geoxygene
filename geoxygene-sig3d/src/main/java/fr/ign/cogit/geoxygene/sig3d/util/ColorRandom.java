package fr.ign.cogit.geoxygene.sig3d.util;

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
 * 
 * Classe pour générer une couleur aléatoire.
 * 
 * 
 */
public class ColorRandom {

  public static Color getRandomColor() {
    int r = (int) (256 * Math.random());
    int g = (int) (256 * Math.random());
    int b = (int) (256 * Math.random());

    return new Color(r, g, b);

  }
}
