package fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.feature.type.FC_FeatureAttributeValue;
import fr.ign.cogit.geoxygene.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.feature.type.GF_Constraint;
import fr.ign.cogit.geoxygene.feature.type.GF_FeatureType;

/**
 * @author Sandrine Balley
 * 
 */
public class AttributeType implements GF_AttributeType {

  /**
	 * 
	 */

  public AttributeType() {
    super();
    this.valuesDomain = new ArrayList<FC_FeatureAttributeValue>();
  }

  /** Identifiant d'un objet */
  protected int id;

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  /** nom de l'attribut. */
  protected String memberName;

  /** Renvoie le nom de la propriété. */
  public String getMemberName() {
    return this.memberName;
  }

  /** Affecte un nom de propriété. */
  public void setMemberName(String MemberName) {
    this.memberName = MemberName;
  }

  /** Type de l'attribut. */
  protected String valueType;

  /** Type de domaine de valeur 0 pour non énuméré et 1 pour énuméré. */
  protected boolean valueDomainType;

  /** Definition du domaine de valeur POUR LES ENUMERES SEULEMENT. */
  protected List<FC_FeatureAttributeValue> valuesDomain;

  /** FC_featureType auquel se rapporte l'attribut* */
  protected GF_FeatureType featureType;

  /** Renvoie le feature type auquel est rattaché la propriété. */
  public GF_FeatureType getFeatureType() {
    return this.featureType;
  }

  /** Affecte un feature type à la propriété. */
  public void setFeatureType(GF_FeatureType FeatureType) {
    this.featureType = FeatureType;
  }

  /** definition de l'attribut. */
  protected String definition;

  /** Renvoie la définition. */
  public String getDefinition() {
    return this.definition;
  }

  /** Affecte une définition. */
  public void setDefinition(String Definition) {
    this.definition = Definition;
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
  public Object getDefinitionReference() {
    return this.definitionReference;
  }

  /**
   * @param definitionReference the definitionReference to set
   */
  public void setDefinitionReference(Object definitionReference) {
    this.definitionReference = definitionReference;
  }

  /** Nombre de valeurs minimal de l'attribut */
  protected int cardMin;

  /** Renvoie le nombre de valeurs minimal de l'attribut. */
  public int getCardMin() {
    return this.cardMin;
  }

  /** Affecte un nombre de valeurs minimal à l'attribut. */
  public void setCardMin(int CardMin) {
    this.cardMin = CardMin;
  }

  /** Nombre de valeurs maximal de l'attribut */
  protected int cardMax;

  /** Renvoie le nombre de valeurs maximal de l'attribut. */
  public int getCardMax() {
    return this.cardMax;
  }

  /** Affecte un nombre de valeurs maximal à l'attribut. */
  public void setCardMax(int CardMax) {
    this.cardMax = CardMax;
  }

  /**
   * le nom du champ correspondant a l'attribut dans la classe ou
   * classe-association Java correspondante
   */
  protected String nomField;

  public String getNomField() {
    return this.nomField;
  }

  public void setNomField(String value) {
    this.nomField = value;
  }

  /** booleen indiquant si l'attribut est de type spatial */
  protected boolean isSpatial;

  public boolean getIsSpatial() {
    return this.isSpatial;
  }

  public void setIsSpatial(boolean isSpatial) {
    this.isSpatial = isSpatial;
  }

  /** booleen indiquant si l'attribut est de type topologique */
  protected boolean isTopologic;

  public boolean getIsTopologic() {
    return this.isTopologic;
  }

  public void setIsTopologic(boolean isTopo) {
    this.isTopologic = isTopo;
  }

  /**
   * Non standard Utile aux applications de transformation de schéma caractère
   * implicite ou explicite de l'élément : un attributeType implicite n'a pas de
   * valeur à priori mais celle-ci pourra être dérivée d'elements explicites par
   * le biais de transformations
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

  /** Renvoie le type de l'attribut. */
  public String getValueType() {
    return this.valueType;
  }

  /** Affecte un type à l'attribut. */
  public void setValueType(String ValueType) {
    this.valueType = ValueType;
  }

  /** Renvoie le type de domaine de valeur de l'attribut. */
  public boolean getValueDomainType() {
    return this.valueDomainType;
  }

  /** Affecte un type à l'attribut. */
  public void setValueDomainType(boolean ValueDomainType) {
    this.valueDomainType = ValueDomainType;
  }

  /**
   * le domaine de valeurs (énuméré ou non) sous forme de chaîne de caractères
   * exemples : "positif", "toute extension de GM_Object", "oui/non/indéterminé"
   **/
  protected String domainOfValues;

  /** Renvoie le domaine de valeurs. */
  public String getDomainOfValues() {
    return this.domainOfValues;
  }

  /** Affecte un domaine de valeurs. */
  public void setDomainOfValues(String domain) {
    this.domainOfValues = domain;
  }

  // //////////////////////////////////////////////////////
  // Modifications Nathalie
  // //////////////////////////////////////////////////////
  /** Renvoie les valeurs possibles d'un attribut enumere. */
  public List<FC_FeatureAttributeValue> getValuesDomain() {
    return this.valuesDomain;
  }

  /**
   * Renvoie les valeurs possibles d'un attribut enumere sous forme de liste de
   * chaines de caractères.
   */
  public List<String> getValuesDomainToString() {
    List<String> valeurs = new ArrayList<String>();
    Iterator<FC_FeatureAttributeValue> it = this.valuesDomain.iterator();
    while (it.hasNext()) {
      FeatureAttributeValue val = (FeatureAttributeValue) it.next();
      valeurs.add(val.getLabel());
    }
    return valeurs;
  }

  /** Affecte une liste de valeurs possibles. */
  public void setValuesDomain(List<FC_FeatureAttributeValue> ValuesDomain) {
    List<FC_FeatureAttributeValue> old = new ArrayList<FC_FeatureAttributeValue>(
        this.valuesDomain);
    Iterator<FC_FeatureAttributeValue> it1 = old.iterator();
    while (it1.hasNext()) {
      FeatureAttributeValue ancienneValeur = (FeatureAttributeValue) it1.next();
      ancienneValeur.setFeatureAttribute(null);
    }
    Iterator<FC_FeatureAttributeValue> it2 = ValuesDomain.iterator();
    while (it2.hasNext()) {
      FeatureAttributeValue maValeur = (FeatureAttributeValue) it2.next();
      maValeur.setFeatureAttribute(this);
    }
  }

  /** Ajoute une valeur à la liste des valeurs possibles de l'attribut. */
  public void addValuesDomain(FC_FeatureAttributeValue value) {
    if (value == null) {
      return;
    }
    this.valuesDomain.add(value);
    if (!(value.getFeatureAttribute() == this)) {
      value.setFeatureAttribute(this);
    }
  }

  /** Enleve une valeur a la liste des valeurs possibles de l'attribut. */
  public void removeValuesDomain(FC_FeatureAttributeValue value) {
    if (value == null) {
      return;
    }
    this.valuesDomain.remove(value);
    value.setFeatureAttribute(null);
  }

  // //////////////////////////

  /**
   * Attribut en caractérisant un autre. La relation inverse n'est pas
   * implémentée.
   */
  protected GF_AttributeType characterize;

  /** Renvoie l'attribut que self caractérise. */
  public GF_AttributeType getCharacterize() {
    return this.characterize;
  }

  /** Affecte un attribut que self caractérise. */
  public void setCharacterize(GF_AttributeType characterize) {
    this.characterize = characterize;
  }

  /** Renvoie la liste des contraintes. */
  public List<GF_Constraint> getConstraint() {
    return null;
  }

  /** Affecte une liste de contraintes */
  public void setConstraint(List<GF_Constraint> L) {
  }

  /** Ajoute une contrainte. */
  public void addConstraint(GF_Constraint value) {
  }

  /** Renvoie le nombre de contraintes. */
  public int sizeConstraint() {
    return 0;
  }

}
