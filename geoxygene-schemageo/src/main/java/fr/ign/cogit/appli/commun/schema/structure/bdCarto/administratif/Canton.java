package fr.ign.cogit.appli.commun.schema.structure.bdCarto.administratif;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public abstract class Canton extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des geometries multiples (plusieurs troncons) */
	//	  private GM_Curve geometrie = null;
	/** Renvoie le GM_Polygon qui definit la geometrie de self */
	public GM_Polygon getGeometrie() {return (GM_Polygon)geom;}
	/** Definit le GM_Polygon qui definit la geometrie de self */
	public void setGeometrie(GM_Polygon geometrie) {this.geom = geometrie;}

	/////////////// ATTRIBUTS //////////////////

	protected String inseeCanton;
	public String getInseeCanton() {return this.inseeCanton; }
	public void setInseeCanton (String InseeCanton) {inseeCanton = InseeCanton; }


	/////////////// RELATIONS //////////////////

	/** Lien bidirectionnel 1-n vers Commune.
	 *  1 objet Canton est en relation avec n objets Commune (n pouvant etre nul).
	 *  1 objet Commune est en relation avec 1 objet Canton au plus.
	 *
	 *  NB: un objet Canton ne doit pas etre en relation plusieurs fois avec le meme objet Commune :
	 *  il est impossible de bien gerer des relations 1-n bidirectionnelles avec doublons.
	 *
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec
	 *  les methodes fournies.
	 * 
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	private List<Commune> communes = new ArrayList<Commune>();

	/** Recupere la liste des objets en relation. */
	public List<Commune> getCommunes() {return communes ; }

	/** Definit la liste des objets en relation, et met a jour la relation inverse. */
	public void setCommunes (List<Commune> L) {
		List <Commune>old = new ArrayList<Commune>(communes);
		Iterator<Commune> it1 = old.iterator();
		while ( it1.hasNext() ) {
			Commune O = it1.next();
			O.setCanton(null);
		}
		Iterator<Commune> it2 = L.iterator();
		while ( it2.hasNext() ) {
			Commune O = it2.next();
			O.setCanton(this);
		}
	}

	/** Recupere le ieme element de la liste des objets en relation. */
	public Commune getCommune(int i) {return communes.get(i) ; }

	/** Ajoute un objet a la liste des objets en relation, et met a jour la relation inverse. */
	public void addCommune (Commune O) {
		if ( O == null ) return;
		communes.add(O) ;
		O.setCanton(this) ;
	}

	/** Enleve un element de la liste des objets en relation, et met a jour la relation inverse. */
	public void removeCommune (Commune O) {
		if ( O == null ) return;
		communes.remove(O) ;
		O.setCanton(null);
	}

	/** Vide la liste des objets en relation, et met a jour la relation inverse. */
	public void emptyCommunes () {
		List<Commune> old = new ArrayList<Commune>(communes);
		Iterator<Commune> it = old.iterator();
		while ( it.hasNext() ) {
			Commune O = it.next();
			O.setCanton(null);
		}
	}



	/** Lien bidirectionnel 1-n vers Canton.
	 *  1 objet Arrondissement est en relation avec n objets Canton (n pouvant etre nul).
	 *  1 objet Canton est en relation avec 1 objet Arrondissement au plus.
	 *
	 *  Les methodes get et set sont utiles pour assurer la bidirection.
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	private Arrondissement arrondissement;

	/** Recupere l'objet en relation. */

	public Arrondissement getArrondissement() {return arrondissement;  }

	/** Definit l'objet en relation, et met a jour la relation inverse. */
	public void setArrondissement(Arrondissement O) {
		Arrondissement old = arrondissement;
		arrondissement = O;
		if ( old != null ) old.getCantons().remove(this);
		if ( O != null) {
			if ( ! O.getCantons().contains(this) ) O.getCantons().add(this);
		}
	}
}
