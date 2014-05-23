package fr.ign.cogit.geoxygene.osm.schema;

public enum OsmSource {
  UNKNOWN, DGI, BING, CORINE_LANDCOVER, STATIONS_GPL, PGS, SURVEY, EXTRAPOLATION, HISTORICAL, KNOWLEDGE, LANDSAT, LOCAL_ADMIN;

  public static OsmSource valueOfTag(String tagValue) {
    if (tagValue == null)
      return UNKNOWN;
    if (tagValue.startsWith("cadastre-dgi-fr")
        || tagValue.contains("cadastre-dgi-fr") || tagValue.contains("DGFiP"))
      return DGI;
    if (tagValue.equals("Bing") || tagValue.equals("bing")
        || tagValue.equals("Bing Sat")
        || tagValue.equals("Microsoft Bing satellite 2012"))
      return BING;
    if (tagValue.equals("Union européenne - SOeS, CORINE Land Cover, 2006."))
      return CORINE_LANDCOVER;
    if (tagValue.equals("stations.gpl.online.fr"))
      return STATIONS_GPL;
    if (tagValue.equals("PGS"))
      return PGS;
    if (tagValue.equals("survey") || tagValue.equals("GPS")
        || tagValue.equals("gps"))
      return SURVEY;
    if (tagValue.equals("extrapolation"))
      return EXTRAPOLATION;
    if (tagValue.equals("historical"))
      return HISTORICAL;
    if (tagValue.equals("knowledge"))
      return KNOWLEDGE;
    if (tagValue.equals("landsat"))
      return LANDSAT;
    if (tagValue.equals("ToulouseMetropole")
        || tagValue.equals("GrandToulouse") || tagValue.equals("GrandNancy")
        || tagValue.equals("Brest Metropole Oceane, 2009")
        || tagValue.equals("Nantes Métropole 11/2011")
        || tagValue.equals("Communauté Urbaine de Bordeaux - 09/2014")
        || tagValue.equals("opendata.paris.fr")
        || tagValue.startsWith("OpenData CAPP")
        || tagValue.equals("Grand Lyon - 10/2011"))
      return LOCAL_ADMIN;
    return UNKNOWN;
  }
}
