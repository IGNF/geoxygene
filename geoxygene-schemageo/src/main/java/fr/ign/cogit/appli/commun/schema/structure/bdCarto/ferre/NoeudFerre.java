package fr.ign.cogit.appli.commun.schema.structure.bdCarto.ferre;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public abstract class NoeudFerre extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	//    private GM_Point geometrie = null;
	/** Renvoie le GM_Point qui definit la geometrie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Definit le GM_Point qui definit la geometrie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}

	/////////////// ATTRIBUTS //////////////////
	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }


	/////////////// RELATIONS //////////////////
	/** Liste (non ordonnee) des arcs entrants de self
	 *  1 objet Noeud est en relation "entrants" avec n objets TronconFerre (n>0).
	 *  1 objet TronconFerre est en relation "fin" avec 1 objet Noeud.
	 *
	 *  Les methodes get (sans indice) et set sont necessaires au mapping.
	 *  Les autres methodes sont la seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	private List<TronconFerre> entrants = new ArrayList<TronconFerre>();

	/** Recupere la liste des arcs entrants. */
	public List<TronconFerre> getEntrants() {return entrants ; }
	/** Definit la liste des arcs entrants, et met a jour la relation inverse Fin. */
	public void setEntrants (List<TronconFerre> L) {
		List<TronconFerre> old = new ArrayList<TronconFerre>(entrants);
		Iterator<TronconFerre> it1 = old.iterator();
		while ( it1.hasNext() ) {
			TronconFerre O = it1.next();
			O.setFin(null);
		}
		Iterator<TronconFerre> it2 = L.iterator();
		while ( it2.hasNext() ) {
			TronconFerre O = it2.next();
			O.setFin(this);
		}
	}
	/** Recupere le ieme element de la liste des arcs entrants. */
	public TronconFerre getEntrant(int i) {return entrants.get(i) ; }
	/** Ajoute un objet a la liste des arcs entrants, et met a jour la relation inverse Fin. */
	public void addEntrant(TronconFerre O) {
		if ( O == null ) return;
		entrants.add(O) ;
		O.setFin(this) ;
	}
	/** Enleve un element de la liste des arcs entrants, et met a jour la relation inverse Fin. */
	public void removeEntrant(TronconFerre O) {
		if ( O == null ) return;
		entrants.remove(O) ;
		O.setFin(null);
	}
	/** Vide la liste des arcs entrants, et met a jour la relation inverse Fin. */
	public void emptyEntrants() {
		List<TronconFerre> old = new ArrayList<TronconFerre>(entrants);
		Iterator<TronconFerre> it = old.iterator();
		while ( it.hasNext() ) {
			TronconFerre O = it.next();
			O.setFin(null);
		}
	}





	/** Liste (non ordonnee) des arcs sortants de self
	 *  1 objet Noeud est en relation "sortants" avec n objets TronconFerre (n>0).
	 *  1 objet TronconFerre est en relation "ini" avec 1 objet Noeud.
	 *
	 *  Les methodes get (sans indice) et set sont necessaires au mapping.
	 *  Les autres methodes sont la seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	private List<TronconFerre> sortants = new ArrayList<TronconFerre>();

	/** Recupere la liste des arcs sortants. */
	public List<TronconFerre> getSortants() {return sortants ; }
	/** Definit la liste des arcs sortants, et met a jour la relation inverse Ini. */
	public void setSortants(List<TronconFerre> L) {
		List<TronconFerre> old = new ArrayList<TronconFerre>(sortants);
		Iterator<TronconFerre> it1 = old.iterator();
		while ( it1.hasNext() ) {
			TronconFerre O = it1.next();
			O.setIni(null);
		}
		Iterator<TronconFerre> it2 = L.iterator();
		while ( it2.hasNext() ) {
			TronconFerre O = it2.next();
			O.setIni(this);
		}
	}
	/** Recupere le ieme element de la liste des arcs sortants. */
	public TronconFerre getSortant(int i) {return sortants.get(i) ; }
	/** Ajoute un objet a la liste des arcs sortants, et met a jour la relation inverse Ini. */
	public void addSortant(TronconFerre O) {
		if ( O == null ) return;
		sortants.add(O) ;
		O.setIni(this) ;
	}
	/** Enleve un element de la liste des arcs sortants, et met a jour la relation inverse Ini. */
	public void removeSortant(TronconFerre O) {
		if ( O == null ) return;
		sortants.remove(O) ;
		O.setIni(null);
	}
	/** Vide la liste des arcs sortants, et met a jour la relation inverse Ini. */
	public void emptySortants() {
		List<TronconFerre> old = new ArrayList<TronconFerre>(sortants);
		Iterator<TronconFerre> it = old.iterator();
		while ( it.hasNext() ) {
			TronconFerre O = it.next();
			O.setIni(null);
		}
	}
}
