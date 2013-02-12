/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationRole;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_Constraint;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_InheritanceRelation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_Operation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_PropertyType;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * TODO implémenter les méthodes vides
 * @author Sandrine Balley
 * @author Nathalie Abadie
 */
public class FeatureType implements GF_FeatureType {

  /**
	 * 
	 */
  public FeatureType() {
    super();
    this.memberOf = new ArrayList<GF_AssociationType>();
    this.featureAttributes = new ArrayList<GF_AttributeType>();
    this.featureOperations = new ArrayList<GF_Operation>();
    this.roles = new ArrayList<GF_AssociationRole>();
  }

  /** copie un FeatureType sans ses references a d'autres objets */
  public FeatureType copie() {
    FeatureType ft = new FeatureType();
    ft.id = this.id;
    ft.definition = this.definition;
    ft.typeName = this.typeName;
    ft.nomClasse = this.nomClasse;
    return ft;
  }
  
  /**
   * Constructeur par copie
   * @param ft
   */
  public FeatureType(final FeatureType ft) {    
    this.setId(ft.getId());
    this.setDefinition(ft.getDefinition());
    this.setTypeName(ft.getTypeName());
    this.setNomClasse(ft.getNomClasse());
    this.setGeometryType(ft.getGeometryType());
  }

  Class<? extends IGeometry> geometryType = IGeometry.class;

  @Override
  public void setGeometryType(Class<? extends IGeometry> geometryType) {
    this.geometryType = geometryType;
  }

  @Override
  public Class<? extends IGeometry> getGeometryType() {
    return this.geometryType;
  }

  // /////////////////////////////////////////////////////
  // Ajout Nathalie///////////////////////////////////////
  // /////////////////////////////////////////////////////

  /** schéma auquel participe ce FeatureType */
  protected SchemaConceptuelProduit schema;

  /**
   * @param sISO
   */
  public void setSchema(SchemaConceptuelProduit sISO) {
    SchemaConceptuelProduit old = this.schema;
    this.schema = sISO;
    if (old != null) {
      old.getFeatureTypes().remove(this);
    }
    if (sISO != null) {
      if (!sISO.getFeatureTypes().contains(this)) {
        sISO.addFeatureType(this);
      }
    }
  }

  /**
   * @return the schema of this FeatureType
   */
  public SchemaConceptuelProduit getSchema() {
    return this.schema;
  }

  /** Identifiant d'un objet */
  protected int id;

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  /** Liste des roles associés par lien 1:n */
  protected List<GF_AssociationRole> roles;

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
  public void addRole(GF_AssociationRole Role) {
    this.roles.add(Role);
    if (Role.getFeatureType() != this) {
      Role.setFeatureType(this);
    }
  }

  /**
	 *
	 */
  @Override
  public AssociationRole getRoleI(int i) {
    return (AssociationRole) this.roles.get(i);
  }

  /**
   * @param nomRole
   * @return an AssociationRole with the given name
   */
  public AssociationRole getRoleByName(String nomRole) {
    List<GF_AssociationRole> listRoles = this.getRoles();
    for (int i = 0; i < listRoles.size(); i++) {
      if (((AssociationRole) listRoles.get(i)).getMemberName().equals(nomRole)) {
        return (AssociationRole) listRoles.get(i);
      }
    }
    System.err.println("Le role " + nomRole
        + " n'a pas été trouvé pour la classe " + this.getTypeName());
    return null;
  }

  /**
	 *
	 */
  @Override
  public void removeRole(GF_AssociationRole value) {
    if (value == null) {
      return;
    }
    this.roles.remove(value);
    value.setFeatureType(null);// gestion de la bi-direction
  }

  // /** Liste des procedures de representation dans lesquelles le ft
  // intervient */
  // protected List <Modelisation> modelisation;
  //
  // /** Affecte la liste des procedures de representation dans lesquelles le
  // ft est implique */
  // public void setModelisation (List <Modelisation> mod) {
  // List<Modelisation> old = new ArrayList<Modelisation>(modelisation);
  // Iterator<Modelisation> it1=old.iterator();
  // while (it1.hasNext()){
  // Modelisation proc=it1.next();
  // this.modelisation.remove(proc);
  // proc.removeFtImplique(this);
  // }
  // Iterator<Modelisation> it2=mod.iterator();
  // while (it2.hasNext()){
  // Modelisation newProc=it2.next();
  // this.modelisation.add(newProc);
  // newProc.addFtImplique(this);
  // }
  // }
  //
  // /** Renvoie la liste des procedures de representation dans lesquelles le
  // ft est implique */
  // public List<Modelisation> getModelisation () {return this.modelisation;}
  //
  // /** Ajoute une procedure de representation à la liste */
  // public void addModelisation (Modelisation mod) {
  // if (mod==null) return;
  // modelisation.add(mod);
  // if (!mod.getFtImpliques().contains(this))
  // mod.getFtImpliques().add((GF_Itf_FeatureType) this);
  // }
  //
  // /** Enleve une procedure de representation de la liste */
  // public void removeModelisation (Modelisation mod) {
  // if (mod == null)
  // return;
  // this.modelisation.remove(mod);
  // mod.getFtImpliques().remove(this);
  // }
  //

  // /////////////////////////////////////////////////////

  protected String typeName;
  protected String definition;
  /** Les attributs de cette classe. */
  protected List<GF_AttributeType> featureAttributes;
  /** Les associations dans lesquelles est impliquée cette classe. */
  protected List<GF_AssociationType> memberOf;
  /** Les operations de cette classe. */
  protected List<GF_Operation> featureOperations;
  /** Les relations de généralisation dans lesquelles est impliquée la classe. */
  protected List<GF_InheritanceRelation> generalization = new ArrayList<GF_InheritanceRelation>();
  /** Les relations de spécialisation dans lesquelles est impliquée la classe. */
  protected List<GF_InheritanceRelation> specialization = new ArrayList<GF_InheritanceRelation>();
  /**
   * le nom de la Classe Java correspondant au FeatureType et contenant les
   * donnees
   */
  protected String nomClasse;

  public String getNomClasse() {
    return this.nomClasse;
  }

  public void setNomClasse(String value) {
    this.nomClasse = value;
  }

  /** proprietaire de l'élément (producteur=1, utilisateur=2)* */
  protected int proprietaire;

  /** Renvoie le proprietaire */
  public int getProprietaire() {
    return this.proprietaire;
  }

  /** Affecte le proprietaire */
  public void setProprietaire(int value) {
    this.proprietaire = value;
  }

  /**
   * Non standard Utile aux applications de transformation de schéma caractère
   * implicite ou explicite de l'élément : un featureType implicite n'a pas
   * d'instances à priori mais celles-ci pourront être dérivées d'elements
   * explicites par le biais de transformations
   **/
  protected boolean isExplicite;

  /** Renvoie le caractere explicite ou implicite */
  public boolean getIsExplicite() {
    return this.isExplicite;
  }

  /** Affecte le caractère implicite ou explicite */
  public void setIsExplicite(boolean value) {
    this.isExplicite = value;
  }

  /**
   * caractère instancié ou non de cette classe. Par exemple la classe Route
   * peut êtree abstraite et n'être implémentée que pas le biais de ses
   * sous-types Route principale et Route secondaire.
   */
  protected boolean isAbstract;

  @Override
  public boolean getIsAbstract() {
    return this.isAbstract;
  }

  @Override
  public void setIsAbstract(boolean IsAbstract) {
    this.isAbstract = IsAbstract;
  }

  /**
   * la définition semantique du featureType (sous la forme d'un String ou d'une
   * classe d'ontologie) ou un pointeur vers cette définition (sous la forme
   * d'une URI)
   */
  protected Object definitionReference;

  /**
   * @return the definitionReference
   */
  @Override
  public Object getDefinitionReference() {
    return this.definitionReference;
  }

  /**
   * @param definitionReference the definitionReference to set
   */
  @Override
  public void setDefinitionReference(Object definitionReference) {
    this.definitionReference = definitionReference;
  }

  @Override
  public List<GF_AttributeType> getFeatureAttributes() {
    return this.featureAttributes;
  }

  @Override
  public void setFeatureAttributes(List<GF_AttributeType> L) {
    this.featureAttributes = L;
  }

  @Override
  public int sizeFeatureAttributes() {
    return this.featureAttributes.size();
  }

  @Override
  public void addFeatureAttribute(GF_AttributeType value) {
    this.featureAttributes.add(value);
    if (value.getFeatureType() != this) {
      value.setFeatureType(this);
    }
  }

  // /////////////////////////Ajout Nathalie////////////////////////////////////
  public AttributeType getFeatureAttributeI(int i) {
    return (AttributeType) this.featureAttributes.get(i);
  }

  @Override
  public AttributeType getFeatureAttributeByName(String nomAttribut) {
    List<GF_AttributeType> listAttrib = this.getFeatureAttributes();
    for (int i = 0; i < listAttrib.size(); i++) {
      if (((AttributeType) listAttrib.get(i)).getMemberName().equalsIgnoreCase(
          nomAttribut)) {
        return (AttributeType) listAttrib.get(i);
      }
    }
    System.err.println("L'attribut " + nomAttribut
        + " n'a pas été trouvé pour la classe " + this.getTypeName());
    return null;
  }

  @Override
  public void removeFeatureAttribute(GF_AttributeType ftatt) {
    if (ftatt == null) {
      return;
    }
    this.featureAttributes.remove(ftatt);
    ftatt.setFeatureType(null);
  }

  public void emptyFeatureAttributes() {
    this.featureAttributes.clear();
  }

  // //////////////////////////////////////////////////////////////////////////
  @Override
  public List<GF_Operation> getFeatureOperations() {
    return this.featureOperations;
  }

  @Override
  public void setFeatureOperations(List<GF_Operation> L) {
    this.featureOperations = L;
  }

  @Override
  public int sizeFeatureOperations() {
    return this.featureOperations.size();
  }

  @Override
  public void addFeatureOperation(GF_Operation value) {
    this.featureOperations.add(value);
  }

  @Override
  public String getTypeName() {
    return this.typeName;
  }

  @Override
  public void setTypeName(String TypeName) {
    this.typeName = TypeName;
  }

  @Override
  public String getDefinition() {
    return this.definition;
  }

  @Override
  public void setDefinition(String Definition) {
    this.definition = Definition;
  }

  /**
   * Renvoie les relations d'héritage dans lesquelles est impliquée la classe en
   * tant que subType.
   */
  @Override
  public List<GF_InheritanceRelation> getGeneralization() {
    return this.generalization;
  }

  /** Affecte une liste de generalisations */
  @Override
  public void setGeneralization(List<GF_InheritanceRelation> L) {
    this.generalization = L;
  }

  /**
   * Renvoie le nombre de relation de généralisation dans lesquelles est
   * impliquée la classe.
   */
  @Override
  public int sizeGeneralization() {
    return this.generalization.size();
  }

  /**
   * Ajoute une relation de généralisation. Affecte automatiquement le sous-type
   * de cette relation.
   */
  @Override
  public void addGeneralization(GF_InheritanceRelation value) {
    this.generalization.add(value);
    if (value.getSubType() != this) {
      value.setSubType(this); // gestion de la
      // bi-direction
    }
  }

  // /////////////////////////Ajout Nathalie////////////////////////////////////
  public GF_InheritanceRelation getGeneralizationI(int i) {
    return this.generalization.get(i);
  }

  @Override
  public void removeGeneralization(GF_InheritanceRelation value) {
    if (value == null) {
      return;
    }
    this.generalization.remove(value);
    value.setSubType(null);// gestion de la bi-direction
  }

  // //////////////////////////////////////////////////////////////////////////
  /**
   * Renvoie la liste des relations d'héritage dans lesquelles est impliquée la
   * classeen tant que superType.
   */
  @Override
  public List<GF_InheritanceRelation> getSpecialization() {
    return this.specialization;
  }

  /** Affecte une liste de specialisations */
  @Override
  public void setSpecialization(List<GF_InheritanceRelation> L) {
    this.specialization = L;
  }

  /**
   * Renvoie le nombre de relation de spécialisation dans lesquelles est
   * impliquée la classe.
   */
  @Override
  public int sizeSpecialization() {
    return this.specialization.size();
  }

  /**
   * Ajoute une relation de spécialisation. Affecte automatiquement le
   * super-type de cette relation.
   */
  @Override
  public void addSpecialization(GF_InheritanceRelation value) {
    this.specialization.add(value);
    if (value.getSuperType() != this) {
      value.setSuperType(this); // gestion de la
      // bi-direction
    }
  }

  // /////////////////////////Ajout
  // Nathalie////////////////////////////////////
  public GF_InheritanceRelation getSpecializationI(int i) {
    return this.specialization.get(i);
  }

  @Override
  public void removeSpecialization(GF_InheritanceRelation value) {
    if (value == null) {
      return;
    }
    this.specialization.remove(value);
    value.setSuperType(null);// gestion de la bi-direction
  }

  // //////////////////////////////////////////////////////////////////////////
  /** Renvoie les associations dans lesquelles est impliquée cette classe. */
  @Override
  public List<GF_AssociationType> getMemberOf() {
    return this.memberOf;
  }

  /** Affecte une liste d'associations */
  @Override
  public void setMemberOf(List<GF_AssociationType> L) {
    List<GF_AssociationType> old = new ArrayList<GF_AssociationType>(
        this.memberOf);
    Iterator<GF_AssociationType> it1 = old.iterator();
    while (it1.hasNext()) {
      GF_AssociationType scfa = it1.next();
      this.memberOf.remove(scfa);
      scfa.getLinkBetween().remove(this);
    }
    Iterator<GF_AssociationType> it2 = L.iterator();
    while (it2.hasNext()) {
      GF_AssociationType scfa = it2.next();
      this.memberOf.add(scfa);
      scfa.getLinkBetween().add(this);
    }
  }

  /** Le nombre d'associations dans lesquelles est impliquée cette classe. */
  @Override
  public int sizeMemberOf() {
    return this.memberOf.size();
  }

  /** Ajoute une association. */
  @Override
  public void addMemberOf(GF_AssociationType value) {
    if (value == null) {
      return;
    }
    this.memberOf.add(value);
    if (!value.getLinkBetween().contains(this)) {
      value.getLinkBetween().add(this);
    }
  }

  // /////////////////////////Ajout
  // Nathalie////////////////////////////////////
  public GF_AssociationType getMemberOfI(int i) {
    return this.memberOf.get(i);
  }

  @Override
  public void removeMemberOf(GF_AssociationType value) {
    if (value == null) {
      return;
    }
    this.memberOf.remove(value);
    value.getLinkBetween().remove(this);// gestion de la bi-direction
  }

  // //////////////////////////////////////////////////////////////////////////
  /** *******méthodes non utilisées de l'interface GF_Itf ********** */
  /**
   * Non implémenté
   */
  @Override
  public List<GF_PropertyType> getProperties() {
    return null;
  }

  /**
   * Non implémenté
   */
  @Override
  public void setProperties(List<GF_PropertyType> L) {
  }

  /**
   * Non implémenté
   */
  @Override
  public int sizeProperties() {
    return 0;
  }

  /**
   * Non implémenté
   * @param value
   */
  public void addProperty(GF_PropertyType value) {
  }

  /**
   * Non implémenté
   */
  @Override
  public List<GF_Constraint> getConstraint() {
    return null;
  }

  /**
   * Non implémenté
   */
  @Override
  public void setConstraint(List<GF_Constraint> L) {
  }

  /**
   * Non implémenté
   */
  @Override
  public int sizeConstraint() {
    return 0;
  }

  /**
   * Non implémenté
   */
  @Override
  public void addConstraint(GF_Constraint value) {
  }
}
