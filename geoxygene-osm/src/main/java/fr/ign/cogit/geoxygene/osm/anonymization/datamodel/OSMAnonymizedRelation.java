package fr.ign.cogit.geoxygene.osm.anonymization.datamodel;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.OSMPrimitiveType;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.RoleMembre;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.TypeRelation;

/**
 * Classe représentant une Relation OpenStreetMap.
 * Utilise les TypeRelation de OSMRelation.
 * @author Matthieu Dufait
 */
public class OSMAnonymizedRelation extends AnonymizedPrimitiveGeomOSM {
  private TypeRelation type;
  private List<OSMAnonymizedRelationMember> members;
  
  /**
   * Constructeur initialisant l'objet avec les paramètres
   * @param type
   * @param members
   */
  public OSMAnonymizedRelation(TypeRelation type,
      List<OSMAnonymizedRelationMember> members) {
    super();
    this.type = type;
    this.members = members;
  }

  public OSMAnonymizedRelation(TypeRelation type) {
    this(type, new ArrayList<>());
  }

  /**
   * Accesseur en lecture sur le type
   * @return type
   */
  public TypeRelation getType() {
    return type;
  }

  /**
   * Accesseur en lecture sur la liste des membres
   * @return members
   */
  public List<OSMAnonymizedRelationMember> getMembers() {
    return members;
  }
  
  /**
   * Get the members of this relation with the role "outer".
   * From OSMRelation
   * @return
   */
  public List<OSMAnonymizedRelationMember> getOuterMembers() {
    List<OSMAnonymizedRelationMember> outers = new ArrayList<OSMAnonymizedRelationMember>();
    for (OSMAnonymizedRelationMember member : members) {
      if (member.getRole().equals(RoleMembre.OUTER))
        outers.add(member);
    }
    return outers;
  }

  /**
   * Get the members of this relation with the role "inner".
   * From OSMRelation
   * @return
   */
  public List<OSMAnonymizedRelationMember> getInnerMembers() {
    List<OSMAnonymizedRelationMember> inners = new ArrayList<OSMAnonymizedRelationMember>();
    for (OSMAnonymizedRelationMember member : members) {
      if (member.getRole().equals(RoleMembre.INNER))
        inners.add(member);
    }
    return inners;
  }

  @Override
  public OSMPrimitiveType getOSMPrimitiveType() {
    return OSMPrimitiveType.relation;
  }
}
