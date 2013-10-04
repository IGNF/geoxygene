package fr.ign.cogit.geoxygene.util;

import java.awt.Color;

public class ColorUtil {

  /**
   * A color with the specified opacity applied to the given color.
   * @param color the input color
   * @param opacity the opacity
   * @return a new color with the specified opacity applied to the given color
   */
  public static Color getColorWithOpacity(Color color, double opacity) {
    float[] symbolizerColorComponenents = color.getComponents(null);
    return new Color(
      symbolizerColorComponenents[0],
      symbolizerColorComponenents[1],
      symbolizerColorComponenents[2],
      symbolizerColorComponenents[3] * (float) opacity);
  }
}
