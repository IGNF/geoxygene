package fr.ign.cogit.geoxygene.osm.anonymization.datamodel;

import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.OSMPrimitiveType;

/**
 * Cette classe permet de stocker les informations 
 * d'un Node OSM pour l'anonymisation de la base
 * Les attributs lat et lon sont immutables car 
 * on considère que les informations doivent refleter
 * celles d'oSm et donc n'on pas à être modifiées après
 * initialisation.
 * 
 * @author Matthieu Dufait
 */
public class OSMAnonymizedNode extends AnonymizedPrimitiveGeomOSM {
  private double lat;
  private double lon;
  
  /**
   * Simple constructeur initialisant les attributs avec
   * les paramètres
   * @param lat
   * @param lon
   */
  public OSMAnonymizedNode(double lat, double lon) {
    super();
    this.lat = lat;
    this.lon = lon;
  }

  /**
   * Accesseur en lecture de la latitude
   * @return lat
   */
  public double getLat() {
    return lat;
  }

  /**
   * Accesseur en écriture de la longitude
   * @return lon
   */
  public double getLon() {
    return lon;
  }

  @Override
  public OSMPrimitiveType getOSMPrimitiveType() {
    return OSMPrimitiveType.node;
  }

  @Override
  public String toString() {
    return super.toString()+ ": [lat=" + lat + ", lon=" + lon + "]";
  }
}
