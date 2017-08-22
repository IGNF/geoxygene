package fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util;

import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.OSMAnonymizedObject;

/**
 * Classe servant de clé primaire aux OSMAnonymizedResource
 * pour cela on prends l'identifiant, la version et le 
 * type d'un élément pour avoir des valeurs uniques.
 * 
 * La classe est immutable.
 * 
 * @author Matthieu Dufait
 */
public final class ObjectPrimaryKey {
  private OSMPrimitiveType elementType;
  private long id;
  
  /**
   * Constructeur à partir d'un OSMAnonymizedResource
   * @param res
   */
  public ObjectPrimaryKey(OSMAnonymizedObject obj) {
    elementType = obj.getElementType();
    id = obj.getId();
  }

  /**
   * Construit à partir des paramètre pour remplir les
   * attributs, doit être utiliser pour rechercher un élément
   * @param elementType
   * @param id
   */
  public ObjectPrimaryKey(OSMPrimitiveType elementType, long id) {
    this.elementType = elementType;
    this.id = id;
  }

  /**
   * Accesseur en lecture sur le type de primitive
   * @return elementType
   */
  public OSMPrimitiveType getElementType() {
    return elementType;
  }

  /**
   * Accesseur en lecture sur l'identifiant
   * @return id
   */
  public long getId() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((elementType == null) ? 0 : elementType.hashCode());
    result = prime * result + (int) (id ^ (id >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ObjectPrimaryKey other = (ObjectPrimaryKey) obj;
    if (elementType != other.elementType)
      return false;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ObjectPrimaryKey [elementType=" + elementType + ", id=" + id + "]";
  }
  
  
  
}
