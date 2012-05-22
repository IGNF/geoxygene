/*
 * Créé le 30 sept. 2004
 * 
 * Pour changer le modèle de ce fichier généré, allez à :
 * Fenêtre&gt;Préférences&gt;Java&gt;Génération de code&gt;Code et commentaires
 */
package fr.ign.cogit.geoxygene.api.feature.type;

import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * @author Sandrine Balley GF_FeatureType proposé par le General Feature Model
 *         de la norme ISO1909
 */
public interface GF_FeatureType {
  /** Renvoie le nom de la classe géographique. */
  public String getTypeName();

  /** Affecte un nom. */
  public void setTypeName(String TypeName);

  /**
   * Renvoie le nom de la principale classe de la géométrie portée par le
   * featuretype.
   */
  public Class<? extends IGeometry> getGeometryType();

  /**
   * Affecte le nom de la principale classe de la géométrie portée par le
   * featuretype.
   */
  public void setGeometryType(Class<? extends IGeometry> GeometryType);

  /** Renvoie la description de la classe geographique. */
  public String getDefinition();

  /** Affecte une définition. */
  public void setDefinition(String Definition);

  /** Renvoie l'attribut isAbtract. */
  public boolean getIsAbstract();

  /** Affecte l'attribut isAbstract. */
  public void setIsAbstract(boolean IsAbstract);

  /**
   * renvoie la définition semantique du featureType (sous la forme d'un String
   * ou d'une classe d'ontologie) ou un pointeur vers cette définition (sous la
   * forme d'une URI)
   * 
   * Correspond à FC_DescriptionReference et FC_DescriptionSource proposés dans
   * la norme ISO19110
   * @return Object
   */
  public Object getDefinitionReference();

  public void setDefinitionReference(Object ref);

  /**
   * Renvoie les relations de généralisation dans lesquelles est impliquée la
   * classe.
   */
  public List<GF_InheritanceRelation> getGeneralization();

  /** Affecte une liste de generalisations */
  public void setGeneralization(List<GF_InheritanceRelation> L);

  /**
   * Renvoie le nombre de relation de généralisation dans lesquelles est
   * impliquée la classe.
   */
  public int sizeGeneralization();

  /**
   * Ajoute une relation de généralisation. Affecte automatiquement le sous-type
   * de cette relation.
   */
  public void addGeneralization(GF_InheritanceRelation value);

  public void removeGeneralization(GF_InheritanceRelation value);

  /**
   * Renvoie la liste des relations de spécialisation dans lesquelles est
   * impliquée la classe.
   */
  public List<GF_InheritanceRelation> getSpecialization();

  /** Affecte une liste de specialisations */
  public void setSpecialization(List<GF_InheritanceRelation> L);

  /**
   * Renvoie le nombre de relation de spécialisation dans lesquelles est
   * impliquée la classe.
   */
  public int sizeSpecialization();

  /**
   * Ajoute une relation de spécialisation. Affecte automatiquement le
   * super-type de cette relation.
   */
  public void addSpecialization(GF_InheritanceRelation value);

  public void removeSpecialization(GF_InheritanceRelation value);

  /** Renvoie la liste des propriétés. */
  public List<GF_PropertyType> getProperties();

  /** Affecte une liste de proprietes */
  public void setProperties(List<GF_PropertyType> L);

  /** Renvoie le nombre de propriétés. */
  public int sizeProperties();

  /** Renvoie les associations dans lesquelles est impliquée cette classe. */
  public List<GF_AssociationType> getMemberOf();

  /** Affecte une liste d'associations */
  public void setMemberOf(List<GF_AssociationType> L);

  /** Le nombre d'associations dans lesquelles est impliquée cette classe. */
  public int sizeMemberOf();

  /** Ajoute une association. */
  public void addMemberOf(GF_AssociationType value);

  /** Enleve une association. */
  public void removeMemberOf(GF_AssociationType value);

  /** Renvoie la liste des attributs. */
  public List<GF_AttributeType> getFeatureAttributes();

  /** Affecte une liste d'attributs */
  public void setFeatureAttributes(List<GF_AttributeType> L);

  public int sizeFeatureAttributes();

  public void addFeatureAttribute(GF_AttributeType value);

  public void removeFeatureAttribute(GF_AttributeType ftatt);

  /** Renvoie la liste des operations. */
  public List<GF_Operation> getFeatureOperations();

  /** Affecte une liste d'operations */
  public void setFeatureOperations(List<GF_Operation> L);

  public int sizeFeatureOperations();

  public void addFeatureOperation(GF_Operation value);

  /** Renvoie la liste des contraintes. */
  public List<GF_Constraint> getConstraint();

  /** Affecte une liste de contraintes */
  public void setConstraint(List<GF_Constraint> L);

  /** Ajoute une contrainte. */
  public void addConstraint(GF_Constraint value);

  /** Renvoie le nombre de contraintes. */
  public int sizeConstraint();

  /** Renvoie la liste des roles. */
  public List<GF_AssociationRole> getRoles();

  /** Affecte une liste de roles */
  public void setRoles(List<GF_AssociationRole> L);

  /** Renvoie le nombre de roles. */
  public int sizeRoles();

  /** Ajoute un role. */
  public void addRole(GF_AssociationRole Role);

  /** Renvoie un tole particulier */
  public GF_AssociationRole getRoleI(int i);

  /** enleve un role */
  public void removeRole(GF_AssociationRole value);
  
  /** renvoie un attribut*/
  public GF_AttributeType getFeatureAttributeByName(String nomAttribut);
}
