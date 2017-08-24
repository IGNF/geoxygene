package fr.ign.cogit.geoxygene.osm.anonymization.junk;

import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.OSMAnonymizedResource;
import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.OSMPrimitiveType;

/**
 * Classe servant de clé primaire aux OSMAnonymizedResource
 * pour cela on prends l'identifiant, la version et le 
 * type d'un élément pour avoir des valeurs uniques.
 * 
 * La classe est immutable.
 * 
 * Classe dépréciée, elle n'était plus utile
 * après le developpement de ObjectPrimaryKey
 * 
 * @author Matthieu Dufait
 */
@Deprecated
public final class ElementPrimaryKey {
  private OSMPrimitiveType elementType;
  private long id;
  private int version;
  
  /**
   * Constructeur à partir d'un OSMAnonymizedResource
   * @param res
   */
  public ElementPrimaryKey(OSMAnonymizedResource res) {
    elementType = res.getGeom().getOSMPrimitiveType();
    id = res.getId();
    version = res.getVersion();
  }

  /**
   * Construit à partir des paramètre pour remplir les
   * attributs, doit être utiliser pour rechercher un élément
   * @param elementType
   * @param id
   * @param version
   */
  public ElementPrimaryKey(OSMPrimitiveType elementType, long id, int version) {
    this.elementType = elementType;
    this.id = id;
    this.version = version;
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

  /**
   * Accesseur en lecture sur la version
   * @return version
   */
  public int getVersion() {
    return version;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((elementType == null) ? 0 : elementType.hashCode());
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + version;
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
    ElementPrimaryKey other = (ElementPrimaryKey) obj;
    if (elementType != other.elementType)
      return false;
    if (id != other.id)
      return false;
    if (version != other.version)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ElementPrimaryKey [elementType=" + elementType + ", id=" + id
        + ", version=" + version + "]";
  }
  
}
