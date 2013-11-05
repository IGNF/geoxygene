package fr.ign.cogit.osm.schema.landuse;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public enum OsmLandUseTypology {
  COMMERCIAL, FARMLAND, FOREST, INDUSTRIAL, MEADOW, ORCHARD, RAILWAY, RESIDENTIAL, RETAIL, VINEYARD;

  public static OsmLandUseTypology valueOfTagValue(String value) {
    return valueOf(value.toUpperCase());
  }

  public static List<Color> getFillColors() {
    List<Color> colors = new ArrayList<Color>();
    // commercial color
    colors.add(new Color(255, 228, 196));
    // farmland color
    colors.add(new Color(250, 234, 115));
    // forest color
    colors.add(new Color(34, 120, 15));
    // industrial color
    colors.add(new Color(201, 160, 220));
    // meadow color
    colors.add(new Color(87, 213, 59));
    // orchard color
    colors.add(new Color(0, 255, 127));
    // railway color
    colors.add(new Color(210, 202, 236));
    // residential color
    colors.add(Color.LIGHT_GRAY);
    // retail color
    colors.add(new Color(253, 191, 183));
    // vineyard color
    colors.add(new Color(109, 7, 26));
    return colors;
  }

  public String toTagValue() {
    return this.name().toLowerCase();
  }
}
