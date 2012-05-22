package fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit;

import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_InheritanceRelation;

/**
 * @author Sandrine Balley
 * 
 */
public class InheritanceRelation implements GF_InheritanceRelation {

  public InheritanceRelation() {
    super();
  }

  /** Identifiant d'un objet */
  protected int id;

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  /** Nom de la généralisation ou de la spécialisation. */
  protected String name;

  /** Renvoie le nom. */
  @Override
  public String getName() {
    return this.name;
  }

  /** Affecte un nom. */
  @Override
  public void setName(String Name) {
    this.name = Name;
  }

  /** Classe mère de la relation d'heritage. */
  protected FeatureType superType;

  /** Renvoie la classe mère de la relation d'héritage. */
  @Override
  public GF_FeatureType getSuperType() {
    return this.superType;
  }

  /** Affecte une classe mère à la relation d'héritage. */
  @Override
  public void setSuperType(GF_FeatureType SuperType) {
    FeatureType old = this.superType;
    this.superType = (FeatureType) SuperType;
    if (old != null) {
      old.getSpecialization().remove(this);
    }
    if (SuperType != null) {
      if (!SuperType.getSpecialization().contains(this)) {
        SuperType.addSpecialization(this);
      }
    }
  }

  /** Classe fille de la relation d'heritage. */
  protected FeatureType subType;

  /** Renvoie la classe fille de la relation d'héritage. */
  @Override
  public GF_FeatureType getSubType() {
    return this.subType;
  }

  /** Affecte une classe fille à la relation d'héritage. */
  @Override
  public void setSubType(GF_FeatureType SubType) {
    FeatureType old = this.subType;
    this.subType = (FeatureType) SubType;
    if (old != null) {
      old.getGeneralization().remove(this);
    }
    if (SubType != null) {
      if (!SubType.getGeneralization().contains(this)) {
        SubType.addGeneralization(this);
      }
    }
  }

  /** Description. */
  protected String description;

  /** Renvoie la description. */
  @Override
  public String getDescription() {
    return this.description;
  }

  /** Affecte une description. */
  @Override
  public void setDescription(String Description) {
    this.description = Description;
  }

  /**
   * TRUE si une instance de l'hyperclasse doit être au plus dans une
   * sous-classe, FALSE sinon.
   */
  protected boolean uniqueInstance;

  /** Renvoie l'attribut uniqueInstance. */
  @Override
  public boolean getUniqueInstance() {
    return this.uniqueInstance;
  }

  /** Affecte l'attribut uniqueInstance.. */
  @Override
  public void setUniqueInstance(boolean UniqueInstance) {
    this.uniqueInstance = UniqueInstance;
  }

  /**
   * booleen indiquant si l'heritage est represente par un "extends" en Java(1)
   * ou s'il n'est pas explicitement represente(0)
   */
  protected boolean boolExtends;

  public boolean getBoolExtends() {
    return this.boolExtends;
  }

  public void setBoolExtends(boolean value) {
    this.boolExtends = value;
  }

  /** proprietaire de l'élément (producteur=1, utilisateur=2) **/
  protected int proprietaire;

  /** Renvoie le proprietaire */
  public int getProprietaire() {
    return this.proprietaire;
  }

  /** Affecte le proprietaire */
  public void setProprietaire(int value) {
    this.proprietaire = value;
  }

}
