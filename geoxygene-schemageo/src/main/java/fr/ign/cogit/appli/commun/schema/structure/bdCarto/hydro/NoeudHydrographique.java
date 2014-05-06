package fr.ign.cogit.appli.commun.schema.structure.bdCarto.hydro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

@SuppressWarnings("unchecked")

public abstract class NoeudHydrographique extends ElementBDCarto {

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

	protected double cote;
	public double getCote() {return this.cote; }
	public void setCote (double Cote) {cote = Cote; }

	// NB : impossible a remplir a partir des tables shape
	protected int classification;
	public int getClassification() {return this.classification; }
	public void setClassification(int S) {classification = S; }

	// NB : impossible a remplir a partir des tables shape
	protected int caractereTouristique;
	public int getCaractereTouristique() {return this.caractereTouristique; }
	public void setCaractereTouristique(int S) {caractereTouristique = S; }




	/////////////// RELATIONS //////////////////
	/** Liste (non ordonnee) des arcs entrants de self
	 *  1 objet Noeud est en relation "entrants" avec n objets TronconHydrographique (n>0).
	 *  1 objet TronconHydrographique est en relation "fin" avec 1 objet Noeud.
	 *
	 *  Les methodes get (sans indice) et set sont necessaires au mapping.
	 *  Les autres methodes sont la seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	private List entrants = new ArrayList();

	/** Recupere la liste des arcs entrants. */
	public List getEntrants() {return entrants ; }
	/** Definit la liste des arcs entrants, et met a jour la relation inverse Fin. */
	public void setEntrants (List L) {
		List old = new ArrayList(entrants);
		Iterator it1 = old.iterator();
		while ( it1.hasNext() ) {
			TronconHydrographique O = (TronconHydrographique)it1.next();
			O.setFin(null);
		}
		Iterator it2 = L.iterator();
		while ( it2.hasNext() ) {
			TronconHydrographique O = (TronconHydrographique)it2.next();
			O.setFin(this);
		}
	}
	/** Recupere le ieme element de la liste des arcs entrants. */
	public TronconHydrographique getEntrant(int i) {return (TronconHydrographique)entrants.get(i) ; }
	/** Ajoute un objet a la liste des arcs entrants, et met a jour la relation inverse Fin. */
	public void addEntrant(TronconHydrographique O) {
		if ( O == null ) return;
		entrants.add(O) ;
		O.setFin(this) ;
	}
	/** Enleve un element de la liste des arcs entrants, et met a jour la relation inverse Fin. */
	public void removeEntrant(TronconHydrographique O) {
		if ( O == null ) return;
		entrants.remove(O) ;
		O.setFin(null);
	}
	/** Vide la liste des arcs entrants, et met a jour la relation inverse Fin. */
	public void emptyEntrants() {
		List old = new ArrayList(entrants);
		Iterator it = old.iterator();
		while ( it.hasNext() ) {
			TronconHydrographique O = (TronconHydrographique)it.next();
			O.setFin(null);
		}
	}





	/** Liste (non ordonnee) des arcs sortants de self
	 *  1 objet Noeud est en relation "sortants" avec n objets TronconHydrographique (n>0).
	 *  1 objet TronconHydrographique est en relation "ini" avec 1 objet Noeud.
	 *
	 *  Les methodes get (sans indice) et set sont necessaires au mapping.
	 *  Les autres methodes sont la seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	private List sortants = new ArrayList();

	/** Recupere la liste des arcs sortants. */
	public List getSortants() {return sortants ; }
	/** Definit la liste des arcs sortants, et met a jour la relation inverse Ini. */
	public void setSortants(List L) {
		List old = new ArrayList(sortants);
		Iterator it1 = old.iterator();
		while ( it1.hasNext() ) {
			TronconHydrographique O = (TronconHydrographique)it1.next();
			O.setIni(null);
		}
		Iterator it2 = L.iterator();
		while ( it2.hasNext() ) {
			TronconHydrographique O = (TronconHydrographique)it2.next();
			O.setIni(this);
		}
	}
	/** Recupere le ieme element de la liste des arcs sortants. */
	public TronconHydrographique getSortant(int i) {return (TronconHydrographique)sortants.get(i) ; }
	/** Ajoute un objet a la liste des arcs sortants, et met a jour la relation inverse Ini. */
	public void addSortant(TronconHydrographique O) {
		if ( O == null ) return;
		sortants.add(O) ;
		O.setIni(this) ;
	}
	/** Enleve un element de la liste des arcs sortants, et met a jour la relation inverse Ini. */
	public void removeSortant(TronconHydrographique O) {
		if ( O == null ) return;
		sortants.remove(O) ;
		O.setIni(null);
	}
	/** Vide la liste des arcs sortants, et met a jour la relation inverse Ini. */
	public void emptySortants() {
		List old = new ArrayList(sortants);
		Iterator it = old.iterator();
		while ( it.hasNext() ) {
			TronconHydrographique O = (TronconHydrographique)it.next();
			O.setIni(null);
		}
	}
}
