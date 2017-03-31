package fr.ign.cogit.cartagen.core.genericschema.urban;

public enum BuildingCategory {
  UNKNOWN, HOUSING, INDUSTRIAL_COMMERCIAL, PUBLIC, RELIGIOUS, SPORTS, SPECIAL;

  public static BuildingCategory fromNatureName(String nature) {
    if (nature == null)
      return UNKNOWN;
    if (nature.equals(""))
      return UNKNOWN;
    if (nature.equals("Bâtiment public") || nature.equals("Mairie"))
      return PUBLIC;
    if (nature.equals("Bâtiment sportif"))
      return SPORTS;
    if (nature.contains("chrétien"))
      return RELIGIOUS;
    if (nature.contains("industriel") || nature.contains("Hangar")
        || nature.contains("commercial"))
      return INDUSTRIAL_COMMERCIAL;
    if (nature.equals("Château") || nature.equals("Serre")
        || nature.equals("Péage") || nature.equals("Gare")
        || nature.contains("remarquable"))
      return SPECIAL;
    return UNKNOWN;

  }
}
