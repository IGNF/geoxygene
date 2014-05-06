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
 *      //Compose (lien inverse) "Route numerotee ou nommee"
 * <BR> <STRONG> Definition </STRONG>:
 *      La classe des equipements routiers regroupe : <UL>
 * <LI>     les aires de repos et les aires de service sur le reseau de type autoroutier ; </LI>
 * <LI>     les tunnels routiers d'une longueur inferieure a 200 metres s'ils ne correspondent pas a une intersection avec d'autres troncons des reseaux routier et ferre (sinon ce sont des franchissements) ; </LI>
 * <LI>     les gares de peage.  </LI> </UL>
 * <BR> <STRONG> Compatibilite entre attributs </STRONG> :
 * <UL>
 * <LI> compatiblite sur les toponymes ? </LI>
 * </UL>
 * 
 */

public abstract class EquipementRoutier extends ElementBDCarto {

	//     protected GM_Point geometrie = null;
	/** Renvoie la geometrie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Definit la geometrie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}

	/** Nature
	 * <BR> <STRONG> Definition </STRONG>:
	 *      Nature.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaine de caracteres.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     1- aire de service  </LI>
	 * <LI>     2- aire de repos  </LI>
	 * <LI>     5- tunnel de moins de 200 metres  </LI>
	 * <LI>     7- gare de peage </LI>
	 * </UL>
	 */
	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	/** Toponyme.
	 * <BR> <STRONG> Definition </STRONG>:
	 *   Un equipement porte en general un toponyme.
	 *   Il est compose de trois parties pouvant eventuellement ne porter aucune valeur (n'existe pas) :
	 *   un terme generique ou une designation, texte d'au plus 40 caracteres.
	 *   un article, texte d'au plus cinq caracteres ;
	 *   un element specifique, texte d'au plus 80 caracteres ;
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaine de caracteres.
	 */
	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

	/** Un troncon de route permet d'acceder a n equipements routier ,
	 *  par l'intermediaire de la classe-relation Accede.
	 *  1 objet equipement peut etre en relation avec 0 ou n "objets-relation" Accede.
	 *  1 "objet-relation" Accede est en relation avec 1 objet equipement.
	 *
	 *  Les methodes get (sans indice) et set sont necessaires au mapping.
	 *  Les autres methodes sont la seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	protected List<Accede> accedent = new ArrayList<Accede>();

	/** Recupere la liste des Accede en relation. */
	public List<Accede> getAccedent() {return accedent; }
	/** Definit la liste des Accede en relation, et met a jour la relation inverse. */
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
	/** Recupere le ieme element de la liste des Accede en relation. */
	public Accede getAccede(int i) {return accedent.get(i) ; }
	/** Ajoute un objet a la liste des objets en relation, et met a jour la relation inverse. */
	public void addAccede(Accede O) {
		if ( O == null ) return;
		accedent.add(O) ;
		O.setEquipement(this) ;
	}
	/** Enleve un element de la liste des Accede en relation, et met a jour la relation inverse. */
	public void removeAccede(Accede O) {
		if ( O == null ) return;
		accedent.remove(O) ;
		O.setEquipement(null);
	}
	/** Vide la liste des Accede en relation, et met a jour la relation inverse. */
	public void emptyAccedent() {
		List<Accede> old = new ArrayList<Accede>(accedent);
		Iterator<Accede> it = old.iterator();
		while ( it.hasNext() ) {
			Accede O = it.next();
			O.setEquipement(null);
		}
	}



}
