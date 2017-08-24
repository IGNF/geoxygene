package fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util;

/**
 * Enum permettant d'indiquer le type d'élément OSM.
 * 
 * @author Matthieu Dufait
 */
public enum OSMPrimitiveType {
  node, way, relation;
  
  
  /**
   * Permet de renvoyer le OSMPrimitiveType 
   * correspondant à la chaîne de caractères donnée
   * en paramètre, renvoie null si elle ne correpond 
   * à aucun type.
   * @param type chaine de caractères à traiter.
   * @return un OSMPrimitiveType ou null.
   */
  public static OSMPrimitiveType getTypeFromString(String type) {
    if(type.equalsIgnoreCase("Way"))
      return way;
    else if(type.equalsIgnoreCase("Node"))
      return node;
    else if(type.equalsIgnoreCase("Relation"))
        return relation;
    else 
      return null;
  }
}
