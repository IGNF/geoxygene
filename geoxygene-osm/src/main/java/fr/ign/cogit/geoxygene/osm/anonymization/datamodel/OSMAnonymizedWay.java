package fr.ign.cogit.geoxygene.osm.anonymization.datamodel;

import java.util.List;

import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.OSMPrimitiveType;

/**
 * Classe permettant d'enregistrer les informations 
 * sur un Way OpenStreetMap. Contient une List 
 * de OSMAnonymizedNode qui devrait être renseigné 
 * à la construction de l'objet.
 * 
 * @author Matthieu Dufait
 */
public class OSMAnonymizedWay extends AnonymizedPrimitiveGeomOSM {
  private List<OSMAnonymizedObject> composedOf;

  /**
   * Constructeur intialisant l'ArrayList avec le paramètre
   * @param composedOf
   */
  public OSMAnonymizedWay(List<OSMAnonymizedObject> composedOf) {
    super();
    if(composedOf == null)
      throw new IllegalArgumentException("ArrayList composedOf shouldn't be null.");
    this.composedOf = composedOf;
  }

  /**
   * Accesseur en lecture de composedOf
   * @return composedOf
   */
  public List<OSMAnonymizedObject> getComposedOf() {
    return composedOf;
  }
  
  /**
   * Détermine si le Way est un polygone, c'est à dire 
   * que le premier élément de composedOf est égal au dernier 
   * @return
   */
  public boolean isPolygon() {
    if (composedOf.get(0)
        .equals(composedOf.get(composedOf.size() - 1)))
      return true;
    return false;
  }

  @Override
  public OSMPrimitiveType getOSMPrimitiveType() {
    return OSMPrimitiveType.way;
  }

  @Override
  public String toString() {
    return super.toString()+ ":  composed of " + composedOf;
  }
  
  
}
