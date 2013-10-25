package fr.ign.cogit.osm.schema;

public enum OsmSource {
  UNKNOWN, DGI, BING, CORINE_LANDCOVER, STATIONS_GPL, PGS, SURVEY;

  public static OsmSource valueOfTag(String tagValue) {
    if (tagValue == null)
      return UNKNOWN;
    if (tagValue.startsWith("cadastre-dgi-fr"))
      return DGI;
    if (tagValue.equals("Bing"))
      return BING;
    if (tagValue.equals("Union européenne - SOeS, CORINE Land Cover, 2006."))
      return CORINE_LANDCOVER;
    if (tagValue.equals("stations.gpl.online.fr"))
      return STATIONS_GPL;
    if (tagValue.equals("PGS"))
      return PGS;
    if (tagValue.equals("survey"))
      return SURVEY;
    return UNKNOWN;
  }
}
