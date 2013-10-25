package fr.ign.cogit.osm.lodanalysis;

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
}
