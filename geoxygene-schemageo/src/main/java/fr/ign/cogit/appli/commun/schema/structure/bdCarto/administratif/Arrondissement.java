package fr.ign.cogit.appli.commun.schema.structure.bdCarto.administratif;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;


public abstract class Arrondissement extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des geometries multiples (plusieurs troncons) */
	//	  private GM_Curve geometrie = null;
	/** Renvoie le GM_Polygon qui definit la geometrie de self */
	public GM_Polygon getGeometrie() {return (GM_Polygon)geom;}
	/** Definit le GM_Polygon qui definit la geometrie de self */
	public void setGeometrie(GM_Polygon geometrie) {this.geom = geometrie;}

	/////////////// ATTRIBUTS //////////////////

	protected String inseeArrondissement;
	public String getInseeArrondissement() {return this.inseeArrondissement; }
	public void setInseeArrondissement (String InseeArrondissement) {inseeArrondissement = InseeArrondissement; }

	/////////////// RELATIONS //////////////////

	/** Lien bidirectionnel 1-n vers Canton.
	 *  1 objet Arrondissement est en relation avec n objets Canton (n pouvant etre nul).
	 *  1 objet Canton est en relation avec 1 objet Arrondissement au plus.
	 *
	 *  NB: un objet Arrondissement ne doit pas etre en relation plusieurs fois avec le meme objet Canton :
	 *  il est impossible de bien gerer des relations 1-n bidirectionnelles avec doublons.
	 *
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec
	 *  les methodes fournies.
	 * 
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	private List<Canton> cantons = new ArrayList<Canton>();

	/** Recupere la liste des objets en relation. */
	public List<Canton> getCantons() {return cantons ; }

	/** Definit la liste des objets en relation, et met a jour la relation inverse. */
	public void setCantons (List<Canton> L) {
		List <Canton>old = new ArrayList<Canton>(cantons);
		Iterator<Canton> it1 = old.iterator();
		while ( it1.hasNext() ) {
			Canton O = it1.next();
			O.setArrondissement(null);
		}
		Iterator<Canton> it2 = L.iterator();
		while ( it2.hasNext() ) {
			Canton O = it2.next();
			O.setArrondissement(this);
		}
	}

	/** Recupere le ieme element de la liste des objets en relation. */
	public Canton getCanton(int i) {return cantons.get(i) ; }

	/** Ajoute un objet a la liste des objets en relation, et met a jour la relation inverse. */
	public void addCanton (Canton O) {
		if ( O == null ) return;
		cantons.add(O) ;
		O.setArrondissement(this) ;
	}

	/** Enleve un element de la liste des objets en relation, et met a jour la relation inverse. */
	public void removeCanton (Canton O) {
		if ( O == null ) return;
		cantons.remove(O) ;
		O.setArrondissement(null);
	}

	/** Vide la liste des objets en relation, et met a jour la relation inverse. */
	public void emptyCantons () {
		List<Canton> old = new ArrayList<Canton>(cantons);
		Iterator<Canton> it = old.iterator();
		while ( it.hasNext() ) {
			Canton O = it.next();
			O.setArrondissement(null);
		}
	}



	/** Lien bidirectionnel 1-n vers Arrondissement.
	 *  1 objet Departement est en relation avec n objets Arrondissement (n pouvant etre nul).
	 *  1 objet Arrondissement est en relation avec 1 objet Departement au plus.
	 *
	 *  Les methodes get et set sont utiles pour assurer la bidirection.
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	private Departement departement;

	/** Recupere l'objet en relation. */

	public Departement getDepartement() {return departement;  }

	/** Definit l'objet en relation, et met a jour la relation inverse. */
	public void setDepartement(Departement O) {
		Departement old = departement;
		departement = O;
		if ( old != null ) old.getArrondissements().remove(this);
		if ( O != null) {
			if ( ! O.getArrondissements().contains(this) ) O.getArrondissements().add(this);
		}
	}
}