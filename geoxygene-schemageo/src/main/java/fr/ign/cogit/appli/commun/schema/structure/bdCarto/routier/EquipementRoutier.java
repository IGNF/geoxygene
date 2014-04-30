package fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Equipement routier.
 * <BR> <STRONG> Type </STRONG>:
 *      Objet simple
 * <BR> <STRONG> Localisation </STRONG>:
 *      Ponctuelle.
 * <BR> <STRONG> Liens </STRONG>:
 *      //Compose (lien inverse) "Route numerotée ou nommée"
 * <BR> <STRONG> Définition </STRONG>:
 *      La classe des équipements routiers regroupe : <UL>
 * <LI>     les aires de repos et les aires de service sur le réseau de type autoroutier ; </LI>
 * <LI>     les tunnels routiers d'une longueur inférieure à 200 mètres s'ils ne correspondent pas à une intersection avec d'autres tronçons des réseaux routier et ferré (sinon ce sont des franchissements) ; </LI>
 * <LI>     les gares de péage.  </LI> </UL>
 * <BR> <STRONG> Compatibilité entre attributs </STRONG> :
 * <UL>
 * <LI> compatiblite sur les toponymes ? </LI>
 * </UL>
 * 
 */

public abstract class EquipementRoutier extends ElementBDCarto {

	//     protected GM_Point geometrie = null;
	/** Renvoie la géométrie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}

	/** Nature
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Nature.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     1- aire de service  </LI>
	 * <LI>     2- aire de repos  </LI>
	 * <LI>     5- tunnel de moins de 200 mètres  </LI>
	 * <LI>     7- gare de péage </LI>
	 * </UL>
	 */
	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	/** Toponyme.
	 * <BR> <STRONG> Définition </STRONG>:
	 *   Un équipement porte en général un toponyme.
	 *   Il est composé de trois parties pouvant éventuellement ne porter aucune valeur (n'existe pas) :
	 *   un terme générique ou une désignation, texte d'au plus 40 caractères.
	 *   un article, texte d'au plus cinq caractères ;
	 *   un élément spécifique, texte d'au plus 80 caractères ;
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 */
	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

	/** Un troncon de route permet d'accéder à n équipements routier ,
	 *  par l'intermédiaire de la classe-relation Accede.
	 *  1 objet équipement peut etre en relation avec 0 ou n "objets-relation" Accede.
	 *  1 "objet-relation" Accede est en relation avec 1 objet équipement.
	 *
	 *  Les méthodes get (sans indice) et set sont nécessaires au mapping.
	 *  Les autres méthodes sont là seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	protected List<Accede> accedent = new ArrayList<Accede>();

	/** Récupère la liste des Accede en relation. */
	public List<Accede> getAccedent() {return accedent; }
	/** Définit la liste des Accede en relation, et met à jour la relation inverse. */
	public void setAccedent(List<Accede> L) {
		List<Accede> old = new ArrayList<Accede>(accedent);
		Iterator<Accede> it1 = old.iterator();
		while ( it1.hasNext() ) {
			Accede O = it1.next();
			O.setEquipement(null);
		}
		Iterator<Accede> it2 = L.iterator();
		while ( it2.hasNext() ) {
			Accede O = it2.next();
			O.setEquipement(this);
		}
	}
	/** Récupère le ième élément de la liste des Accede en relation. */
	public Accede getAccede(int i) {return accedent.get(i) ; }
	/** Ajoute un objet à la liste des objets en relation, et met à jour la relation inverse. */
	public void addAccede(Accede O) {
		if ( O == null ) return;
		accedent.add(O) ;
		O.setEquipement(this) ;
	}
	/** Enlève un élément de la liste des Accede en relation, et met à jour la relation inverse. */
	public void removeAccede(Accede O) {
		if ( O == null ) return;
		accedent.remove(O) ;
		O.setEquipement(null);
	}
	/** Vide la liste des Accede en relation, et met à jour la relation inverse. */
	public void emptyAccedent() {
		List<Accede> old = new ArrayList<Accede>(accedent);
		Iterator<Accede> it = old.iterator();
		while ( it.hasNext() ) {
			Accede O = it.next();
			O.setEquipement(null);
		}
	}



}
