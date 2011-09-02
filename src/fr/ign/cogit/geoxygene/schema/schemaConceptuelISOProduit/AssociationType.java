package fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationRole;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_Operation;

/**
 * @author Sandrine Balley
 * 
 */
public class AssociationType extends FeatureType implements GF_AssociationType {
  /**
   * attributs hérités de SC_FeatureType
   * 
   * protected List fc; protected String typeName; protected String definition;
   * protected int id; protected List featureAttributes; protected List
   * memberOf; protected List featureOperations; protected List generalization =
   * new ArrayList(); protected List specialization = new ArrayList();
   * 
   */
  public AssociationType() {
    super();
    this.featureAttributes = new ArrayList<GF_AttributeType>(0);
    this.featureOperations = new ArrayList<GF_Operation>(0);
    this.roles = new ArrayList<GF_AssociationRole>(0);
    this.linkBetween = new ArrayList<GF_FeatureType>(0);
  }

  /** Les feature types impliqués dans cette association. */
  protected List<GF_FeatureType> linkBetween;

  /** Les roles de cette association. */
  // protected List<GF_AssociationRole> roles;

  /**
   * le nom de la classe-association Java correspondante si elle existe (en cas
   * de cardinalité n-m)
   */
  protected String nomClasseAsso;

  public String getNomClasseAsso() {
    return this.nomClasseAsso;
  }

  public void setNomClasseAsso(String value) {
    this.nomClasseAsso = value;
  }

  /** flag agregation : 1 si l'association est de type agregation */
  protected boolean isAggregation;

  public void setIsAggregation(boolean value) {
    this.isAggregation = value;
  }

  public boolean getIsAggregation() {
    return this.isAggregation;
  }

  @Override
  public List<GF_FeatureType> getLinkBetween() {
    return this.linkBetween;
  }

  /** Affecte une liste de feature types */
  @Override
  public void setLinkBetween(List<GF_FeatureType> L) {
    List<GF_FeatureType> old = new ArrayList<GF_FeatureType>(this.linkBetween);
    Iterator<GF_FeatureType> it1 = old.iterator();
    while (it1.hasNext()) {
      GF_FeatureType scft = it1.next();
      this.linkBetween.remove(scft);
      scft.getMemberOf().remove(this);
    }
    Iterator<GF_FeatureType> it2 = L.iterator();
    while (it2.hasNext()) {
      GF_FeatureType scft = it2.next();
      this.linkBetween.add(scft);
      scft.getMemberOf().add(this);
    }
  }

  /** Renvoie le nombre de feature types impliqués dans cette association. */
  @Override
  public int sizeLinkBetween() {
    return this.linkBetween.size();
  }

  /** Ajoute un feature type. Execute un "addMemberOf" sur GF_FeatureType. */
  @Override
  public void addLinkBetween(GF_FeatureType value) {
    if (value == null) {
      return;
    }
    this.linkBetween.add(value);
    if (!value.getMemberOf().contains(this)) {
      value.getMemberOf().add(this);
    }
  }

  @Override
  public GF_FeatureType getLinkBetweenI(int i) {
    return this.linkBetween.get(i);
  }

  @Override
  public void removeLinkBetwenn(GF_FeatureType value) {
    if (value == null) {
      return;
    }
    this.linkBetween.remove(value);
    value.getMemberOf().remove(this);// gestion de la bi-direction
  }

  // //////////////////////////////////////////////////////////////////////////

  @Override
  public List<GF_AssociationRole> getRoles() {
    return this.roles;
  }

  /** Affecte une liste de roles */
  @Override
  public void setRoles(List<GF_AssociationRole> L) {
    this.roles = L;
  }

  /** Renvoie le nombre de roles. */
  @Override
  public int sizeRoles() {
    return this.roles.size();
  }

  /** Ajoute un role. */
  @Override
  public void addRole(GF_AssociationRole role) {
    this.roles.add(role);
    if (role.getAssociationType() != this) {
      role.setAssociationType(this);
    }
    if (!this.getLinkBetween().contains(role.getFeatureType())) {
      this.addLinkBetween(role.getFeatureType());
    }
  }

  @Override
  public AssociationRole getRoleI(int i) {
    return (AssociationRole) this.roles.get(i);
  }

  @Override
  public AssociationRole getRoleByName(String nomRole) {
    List<GF_AssociationRole> listRoles = this.getRoles();
    for (int i = 0; i < listRoles.size(); i++) {
      if (((AssociationRole) listRoles.get(i)).getMemberName().equals(nomRole)) {
        return (AssociationRole) listRoles.get(i);
      }
    }
    System.err.println("Le role " + nomRole
        + " n'a pas été trouvé pour l'association " + this.getTypeName());
    return null;
  }

  @Override
  public void removeRole(GF_AssociationRole value) {
    if (value == null) {
      return;
    }
    this.roles.remove(value);
    value.setAssociationType(null);// gestion de la bi-direction
  }

  // //////////////////////////////////////////////////////////////////////////

  /** boolean de valeur 1 si l'association est de nature spatiale */
  protected boolean isSpatial;

  public void setIsSpatial(boolean value) {
    this.isSpatial = value;
  }

  public boolean getIsSpatial() {
    return this.isSpatial;
  }

  /** boolean de valeur 1 si l'association est une aggregation */
  protected boolean isTopologic;

  public void setIsTopologic(boolean value) {
    this.isTopologic = value;
  }

  public boolean getIsTopologic() {
    return this.isTopologic;
  }
}
