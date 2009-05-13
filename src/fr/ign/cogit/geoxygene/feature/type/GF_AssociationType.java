/*
 * Cr�� le 30 sept. 2004
 *
 * Pour changer le mod�le de ce fichier g�n�r�, allez � :
 * Fen�tre&gt;Pr�f�rences&gt;Java&gt;G�n�ration de code&gt;Code et commentaires
 */
package fr.ign.cogit.geoxygene.feature.type;
import java.util.List;
/**
 * @author Balley
 *
 * GF_AssociationType propos� par le General Feature Model de la norme ISO1909
 */
public interface GF_AssociationType extends GF_FeatureType {

	/** Renvoie les feature types impliqu�s dans cette association. */
	public List<GF_FeatureType> getLinkBetween();
	/** Affecte une liste de feature types */
	public void setLinkBetween (List<GF_FeatureType> L);
	/** Renvoie le nombre de feature types impliqu�s dans cette association. */
	public int sizeLinkBetween ();
	/** Ajoute un feature type. Execute un "addMemberOf" sur GF_FeatureType.*/
	public void addLinkBetween (GF_FeatureType value);
	public GF_FeatureType getLinkBetweenI(int i);
	public void removeLinkBetwenn(GF_FeatureType value);


	/** Renvoie les roles de cette association. */
	public List<GF_AssociationRole> getRoles();
	/** Affecte une liste de roles */
	public void setRoles (List<GF_AssociationRole> L);
	/** Renvoie le nombre de roles. */
	public int sizeRoles();
	/** Ajoute un role. */
	public void addRole (GF_AssociationRole Role);
	public GF_AssociationRole getRoleI(int i);
	public void removeRole(GF_AssociationRole value);

}
