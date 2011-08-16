/*
 * Créé le 30 sept. 2004
 * 
 * Pour changer le modèle de ce fichier généré, allez à :
 * Fenêtre&gt;Préférences&gt;Java&gt;Génération de code&gt;Code et commentaires
 */
package fr.ign.cogit.geoxygene.api.feature.type;

/**
 * @author Balley
 * 
 *         GF_InheritanceRelation proposé par le General Feature Model de la
 *         norme ISO1909. Une relation d'héritage a exactement un subType et une
 *         superType.
 */
public interface GF_InheritanceRelation {

  /** Renvoie le nom. */
  public String getName();

  /** Affecte un nom. */
  public void setName(String Name);

  /** Renvoie la description. */
  public String getDescription();

  /** Affecte une description. */
  public void setDescription(String Description);

  /**
   * Renvoie l'attribut uniqueInstance. Dans la norme, cet attribut indique si
   * une instance du supertype peut être instance de plusieurs subtypes. La
   * norme est ici ambigüe puisqu'elle indique par ailleurs qu'une relation
   * d'heritage ne comporte qu'un subtype : cela signifierait que l'attribut
   * uniqueInstance est superflu au niveau de la relation d'héritage mais
   * devrait être défini au niveau du superType.
   * 
   * "UniqueInstance is a Boolean variable, where .TRUE. means that an instance
   * of the supertype shall not be an instance of more than one of the subtypes,
   * whereas .FALSE. means that an instance of the supertype may be an instance
   * of more than one subtype."
   */

  public boolean getUniqueInstance();

  /** Affecte l'attribut uniqueInstance.. */
  public void setUniqueInstance(boolean UniqueInstance);

  /** Renvoie la classe mère de la relation d'héritage. */
  public GF_FeatureType getSuperType();

  /** Affecte une classe mère à la relation d'héritage. */
  public void setSuperType(GF_FeatureType SuperType);

  /** Renvoie la classe fille de la relation d'héritage. */
  public GF_FeatureType getSubType();

  /** Affecte une classe fille à la relation d'héritage. */
  public void setSubType(GF_FeatureType SubType);
}
