package fr.ign.cogit.geoxygene.osm.lodanalysis;

import java.awt.Color;
import java.util.List;

public enum LoDCategory {
  STREET, CITY, COUNTY, REGION, COUNTRY;

  public static LoDCategory mean(List<LoDCategory> categories) {
    int sum = 0;
    for (LoDCategory cat : categories)
      sum += cat.ordinal();
    int mean = Math.round(sum / categories.size());
    return LoDCategory.values()[mean];
  }

  /**
   * Defines the minimum scale of a scale range related to each LoD Category.
   * @return
   */
  public double getMinScale() {
    if (this.equals(STREET))
      return 0.0;
    if (this.equals(CITY))
      return STREET.getMaxScale();
    if (this.equals(COUNTY))
      return CITY.getMaxScale();
    if (this.equals(REGION))
      return COUNTY.getMaxScale();
    if (this.equals(COUNTRY))
      return REGION.getMaxScale();
    return 0.0;
  }

  /**
   * Defines the maximum scale of a scale range related to each LoD Category.
   * @return
   */
  public double getMaxScale() {
    if (this.equals(STREET))
      return 15000.0;
    if (this.equals(CITY))
      return 50000.0;
    if (this.equals(COUNTY))
      return 150000.0;
    if (this.equals(REGION))
      return 750000.0;
    if (this.equals(COUNTRY))
      return 150000000.0;
    return 0.0;
  }

  /**
   * Gives a scale related to the mean of LoD categories. This computation is
   * based on the scale ranges defined for each LoD category.
   * @param mean
   * @return
   */
  public static double scaleFromCategoryMean(double mean) {
    double scale = 0.0;
    if (mean < 1.5) {
      scale = STREET.getMaxScale() / 1.5 * mean;
    } else if (mean < 2.5) {
      scale = (mean - 1.5) * (CITY.getMaxScale() - STREET.getMaxScale())
          + STREET.getMaxScale();
    } else if (mean < 3.5) {
      scale = (mean - 2.5) * (COUNTY.getMaxScale() - CITY.getMaxScale())
          + CITY.getMaxScale();
    } else if (mean < 4.5) {
      scale = (mean - 3.5) * (REGION.getMaxScale() - COUNTY.getMaxScale())
          + COUNTY.getMaxScale();
    } else {
      scale = (mean - 4.5) * (COUNTRY.getMaxScale() - REGION.getMaxScale())
          + REGION.getMaxScale();// 1:150M is
      // considered as
      // the max scale
    }
    return scale;
  }

  public Color getColor() {
    if (this.equals(STREET))
      return Color.GREEN;
    if (this.equals(CITY))
      return Color.YELLOW;
    if (this.equals(COUNTY))
      return Color.ORANGE;
    if (this.equals(REGION))
      return Color.RED;
    if (this.equals(COUNTRY))
      return Color.BLACK;
    return Color.BLACK;
  }
}
