/*
 * Créé le 30 sept. 2004
 * 
 * Pour changer le modèle de ce fichier généré, allez à :
 * Fenêtre&gt;Préférences&gt;Java&gt;Génération de code&gt;Code et commentaires
 */
package fr.ign.cogit.geoxygene.api.feature.type;

import java.util.List;

/**
 * @author Balley
 * 
 *         GF_PropertyType proposé par le General Feature Model de la norme
 *         ISO19109
 */
public interface GF_PropertyType {

  /** Renvoie l'identifiant. */
  public int getId();

  /** Affecte un identifiant. */
  public void setId(int Id);

  /** Renvoie le feature type auquel est rattachée la propriété. */
  public GF_FeatureType getFeatureType();

  /** Affecte un feature type à la propriété. */
  public void setFeatureType(GF_FeatureType FeatureType);

  /** Renvoie le nom de la propriété. */
  public String getMemberName();

  /** Affecte un nom de propriété. */
  public void setMemberName(String MemberName);

  /** Renvoie la définition. */
  public String getDefinition();

  /** Affecte une définition. */
  public void setDefinition(String Definition);

  /** Renvoie la liste des contraintes. */
  public List<GF_Constraint> getConstraint();

  /** Affecte une liste de contraintes */
  public void setConstraint(List<GF_Constraint> L);

  /** Ajoute une contrainte. */
  public void addConstraint(GF_Constraint value);

  /** Renvoie le nombre de contraintes. */
  public int sizeConstraint();
}
