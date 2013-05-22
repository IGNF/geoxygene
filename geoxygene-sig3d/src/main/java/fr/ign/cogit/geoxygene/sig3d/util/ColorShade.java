package fr.ign.cogit.geoxygene.sig3d.util;

import java.awt.Color;

import fr.ign.cogit.geoxygene.style.colorimetry.ColorReferenceSystem;


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
 * @author Buard Elodie
 * @author Charlotte Hoarau
 * @version 0.1
 * 
 * 
 * Propositions par Buard de dégradés de couleurs pour MNT. Les dégradés du
 * système de couleur du COGIT ont également été ajoutés. La méthode
 * getColorShadeList renvoie la liste des dégradés disponibles (un dégradé est
 * un tableau de couleur de longueur quelconque). En modifiant le résultat de
 * cette méthode, il est possible de modifier les dégradés apparaissant dans le
 * menu de gestion de styles des MNT dans le viewer.
 * 
 * Proposition of color shade (it is * a Color[]). By adding Color[] in the
 * result of getColorShadeList, modifications impact the possibilities offered
 * in the DTM style menu of the viewer
 * 
 */
public class ColorShade {

  /**
   * Renvoie la liste des dégradés disponibles. Cette liste est utilisée dans le
   * menu de gestion de styles des MNT (Classe FenetreMNT.java)
   * 
   * @return la liste des dégradés
   */
  public static Color[][] getColorShadeList() {

    int nbShade = 9;
    Color[][] shades = new Color[nbShade
        + ColorShade.getColorShadeCOGIT().length][];

    shades[0] = ColorShade.GREEN_YELLOW_WHITE;
    shades[1] = ColorShade.RED_ORANGE_WHITE;
    shades[2] = ColorShade.BLUE_PURPLE_WHITE;
    shades[3] = ColorShade.GREEN_BLUE_WHITE;
    shades[4] = ColorShade.YELLOW_BLUE_WHITE;
    shades[5] = ColorShade.RED_BLUE_WHITE;
    shades[6] = ColorShade.RED_PURPLE_YELLOW;
    shades[7] = ColorShade.BLUE_CYAN_GREEN_YELLOW_WHITE;
    shades[8] = ColorShade.BROWN_MONOCHROMATIC;

    Color[][] shadesCOGIT = ColorShade.getColorShadeCOGIT();
    for (int i = 0; i < shadesCOGIT.length; i++) {
      shades[i + nbShade] = shadesCOGIT[i];
    }
    return shades;
  }

  /**
   * Renvoie la liste des dégradés du système de couleur du COGIT.
   * @return la lisate des dégradés du système de couleur du COGIT.
   */
  public static Color[][] getColorShadeCOGIT() {
    ColorReferenceSystem crs = ColorReferenceSystem
        .unmarshall(ColorReferenceSystem.class.getResourceAsStream(
            "/color/ColorReferenceSystem.xml"));

    int nbSlices = crs.getSlices().size();
    Color[][] colorShadeCOGIT = new Color[nbSlices][];

    for (int i = 0; i < nbSlices; i++) {
      Color[] colors = new Color[crs.getSlices().get(i).getColors().size()];
      for (int j = 0; j < crs.getSlices().get(i).getColors().size(); j++) {
        colors[j] = crs.getSlices().get(i).getColors()
            .get(crs.getSlices().get(i).getColors().size() - j - 1).toColor();
      }
      colorShadeCOGIT[i] = colors;
    }

    return colorShadeCOGIT;
  }

  public static Color[] BLUE_CYAN_GREEN_YELLOW_WHITE = { new Color(0, 0, 255),
      new Color(0, 255, 255), new Color(0, 255, 0), Color.yellow, Color.white };

  public static Color[] BROWN_MONOCHROMATIC = { new Color(127, 68, 8),
      new Color(198, 95, 12), new Color(255, 136, 17),
      new Color(255, 178, 100), new Color(255, 255, 255) };

  public static Color[] GREEN_YELLOW_WHITE = { new Color(0, 90, 50),
      new Color(65, 171, 93), new Color(192, 229, 140), new Color(224, 200, 5),
      new Color(127, 88, 12), Color.white };

  public static Color[] RED_ORANGE_WHITE = { new Color(153, 0, 13),
      new Color(239, 59, 14), new Color(254, 107, 19), new Color(240, 157, 18),
      new Color(254, 226, 161), Color.white };

  public static Color[] BLUE_PURPLE_WHITE = { new Color(17, 17, 132),
      new Color(121, 144, 195), new Color(158, 154, 201),
      new Color(152, 112, 167), new Color(210, 188, 212), Color.white };

  public static Color[] GREEN_BLUE_WHITE = { new Color(0, 90, 50),
      new Color(65, 171, 93), new Color(43, 170, 162), new Color(67, 144, 193),
      new Color(158, 202, 225), Color.white };

  public static Color[] YELLOW_BLUE_WHITE = { new Color(191, 158, 10),
      new Color(224, 200, 5), new Color(190, 167, 64),
      new Color(116, 196, 118), new Color(146, 213, 201), Color.white };

  public static Color[] RED_BLUE_WHITE = { new Color(203, 24, 29),
      new Color(254, 107, 19), new Color(224, 200, 5),
      new Color(111, 198, 186), new Color(158, 202, 225), Color.white };

  public static Color[] RED_PURPLE_YELLOW = { new Color(203, 24, 29),
      new Color(218, 81, 137), new Color(152, 112, 167),
      new Color(158, 154, 201), new Color(188, 189, 220), Color.white };

  /**
   * Renvoie un dégradé inversé par rapport à celui donné en paramètres. Ce
   * dégradé est exprimé sous la fore d'un tableau de couleur.
   * 
   * @param shade Le dégradé à inverser
   * @return Le dégradé inversé
   */
  public static Color[] reverse(Color[] shade) {
    Color[] reverseShade = new Color[shade.length];
    for (int i = 0; i < shade.length; i++) {
      reverseShade[i] = shade[shade.length - i - 1];
    }
    return reverseShade;
  }

}
