/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.mgcp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public enum MGCPLandUseType {
  QUARRY, BUILT_UP, TIDAL_WATER, LAKE, RESERVOIR, RIVER, SOIL_SURFACE, CROP, GRASSLAND, THICKET, WOODED, MARSH, SWAMP;

  public static List<Color> getFillColors() {
    List<Color> colors = new ArrayList<Color>();
    // commercial color
    colors.add(new Color(255, 228, 196));
    // farmland color
    colors.add(null);
    // forest color
    colors.add(null);
    // industrial color
    colors.add(null);
    // meadow color
    colors.add(null);
    // orchard color
    colors.add(null);
    // railway color
    colors.add(null);
    // residential color
    colors.add(null);
    // retail color
    colors.add(null);
    // vineyard color
    colors.add(null);
    colors.add(null);
    colors.add(null);
    colors.add(null);
    return colors;
  }
}
