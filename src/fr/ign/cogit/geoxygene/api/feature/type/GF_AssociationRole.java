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
 *         GF_AssociationRole proposé par le General Feature Model de la norme
 *         ISO19109
 */
public interface GF_AssociationRole extends GF_PropertyType {

  /** Renvoie le nombre de valeurs minimal de l'attribut. */
  public String getCardMin();

  /** Affecte un nombre de valeurs minimal à l'attribut. */
  public void setCardMin(String value);

  /** Renvoie le nombre de valeurs maximal de l'attribut. */
  public String getCardMax();

  /** Affecte un nombre de valeurs maximal à l'attribut. */
  public void setCardMax(String value);

  /** Renvoie le feature type auquel est rattaché la propriété. */
  public GF_AssociationType getAssociationType();

  /** Affecte un feature type à la propriété. */
  public void setAssociationType(GF_AssociationType AssociationType);

  /** Renvoie 1 si pour un rôle décrivant la relation "se compose de" et 0 sinon */
  public boolean getIsComposite();

  /**
   * Affecte 1 ou 0 selon que le rôle décrit la relation "se compose de" ou non.
   */
  public void setIsComposite(boolean value);

  /** Renvoie 1 si pour un rôle décrivant la relation "compose" et 0 sinon */
  public boolean getIsComponent();

  /** Affecte 1 ou 0 selon que le rôle décrit la relation "compose" ou non. */
  public void setIsComponent(boolean value);
}
