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
 *         GF_AssociationType proposé par le General Feature Model de la norme
 *         ISO1909
 */
public interface GF_AssociationType extends GF_FeatureType {

  /** Renvoie les feature types impliqués dans cette association. */
  public List<GF_FeatureType> getLinkBetween();

  /** Affecte une liste de feature types */
  public void setLinkBetween(List<GF_FeatureType> L);

  /** Renvoie le nombre de feature types impliqués dans cette association. */
  public int sizeLinkBetween();

  /** Ajoute un feature type. Execute un "addMemberOf" sur GF_FeatureType. */
  public void addLinkBetween(GF_FeatureType value);

  public GF_FeatureType getLinkBetweenI(int i);

  public void removeLinkBetwenn(GF_FeatureType value);

  /** Renvoie les roles de cette association. */
  @Override
  public List<GF_AssociationRole> getRoles();

  /** Affecte une liste de roles */
  @Override
  public void setRoles(List<GF_AssociationRole> L);

  /** Renvoie le nombre de roles. */
  @Override
  public int sizeRoles();

  /** Ajoute un role. */
  @Override
  public void addRole(GF_AssociationRole Role);

  @Override
  public GF_AssociationRole getRoleI(int i);

  @Override
  public void removeRole(GF_AssociationRole value);

}
