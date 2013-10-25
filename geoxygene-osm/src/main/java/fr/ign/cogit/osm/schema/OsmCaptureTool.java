package fr.ign.cogit.osm.schema;

public enum OsmCaptureTool {
  POTLATCH, JOSM, MERKATOR, MAPZEN, ALMIEN, ALBAN, UNKNOWN;

  public static OsmCaptureTool valueOfTexte(String texte) {
    if (texte.equals(JOSM.name()))
      return JOSM;
    else if (texte.equals("Alban"))
      return ALBAN;
    else if (texte.startsWith("Potlatch"))
      return POTLATCH;
    else if (texte.startsWith("almien"))
      return ALMIEN;
    else
      return UNKNOWN;
    // TODO à modifier quand on aura d'autres exemples
  }
}
