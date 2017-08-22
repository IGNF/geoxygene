package fr.ign.cogit.geoxygene.osm.anonymization.datamodel;

import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.OSMPrimitiveType;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.RoleMembre;

/**
 * Classe permettant d'enregistrer les informations d'un
 * membre d'une relation.
 * 
 * @author Matthieu Dufait
 */
public class OSMAnonymizedRelationMember {
  private RoleMembre role;
  private OSMAnonymizedObject ref;
  private OSMPrimitiveType type;
  
  /**
   * Constructeur initialisant les attributs 
   * avec les paramètres. Vérifie que le type
   * correspond au type de ref
   * La vérification ignore la casse.
   * @param role
   * @param ref
   * @param type
   */
  public OSMAnonymizedRelationMember(RoleMembre role,
      OSMAnonymizedObject ref, OSMPrimitiveType type) {
    super();
    this.role = role;
    this.ref = ref;
    this.type = ref.getElementType();
  }

  /**
   * Accesseur sur le role 
   * @return role
   */
  public RoleMembre getRole() {
    return role;
  }

  /**
   * Accesseur vers l'objet référencé
   * @return ref
   */
  public OSMAnonymizedObject getRef() {
    return ref;
  }

  /**
   * Accesseur vers le type d'objet référencé
   * @return type
   */
  public OSMPrimitiveType getType() {
    return type;
  }
}
