package fr.ign.cogit.appli.commun.schema.structure.bdCarto.hydro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;

/** Portion connexe du réseau hydrographique liée à un toponyme,
 *  possédant une source ou origine et un confluent ou embouchure */
@SuppressWarnings("unchecked")

public abstract class CoursDEau extends ElementBDCarto {

	/////////////// ATTRIBUTS //////////////////
	private String toponyme;
	public String getToponyme() {return toponyme;};
	public void setToponyme(String S) {toponyme = S;};

	// NB : impossible à remplir à partir des tables shape
	private int classification;
	public int getClassification() {return classification;};
	public void setClassification(int I) {classification = I;};

	/////////////// RELATIONS //////////////////
	/** Un cours d'eau est composé de tronçons hydrographiques.
	 * (relation n-m, a cours d'eau a au moins 1 tronçon, un tronçon fait partie de 0 à n cours d'eau.
	 *
	 *  Les méthodes get (sans indice) et set sont nécessaires au mapping.
	 *  Les autres méthodes sont là seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	private List troncons = new ArrayList();
	/** Récupère le troncons hydro en relation */
	public List getTroncons() {return troncons ; }
	/** Définit le troncons hydro en relation, et met à jour la relation inverse. */
	public void setTroncons (List L) {
		List old = new ArrayList(troncons);
		Iterator it1 = old.iterator();
		while ( it1.hasNext() ) {
			TronconHydrographique O = (TronconHydrographique)it1.next();
			troncons.remove(O);
			O.getCoursDEau().remove(this);
		}
		Iterator it2 = L.iterator();
		while ( it2.hasNext() ) {
			TronconHydrographique O = (TronconHydrographique)it2.next();
			troncons.add(O);
			O.getCoursDEau().add(this);
		}
	}
	/** Récupère le ième élément de la liste des troncons hydro en relation. */
	public TronconHydrographique getTroncon(int i) {return (TronconHydrographique)troncons.get(i) ; }
	/** Ajoute un élément à la liste des troncons hydro en relation, et met à jour la relation inverse. */
	public void addTroncon(TronconHydrographique O) {
		if ( O == null ) return;
		troncons.add(O) ;
		O.getCoursDEau().add(this);
	}
	/** Enlève un élément de la liste des troncons hydro en relation, et met à jour la relation inverse. */
	public void removeTroncon(TronconHydrographique O) {
		if ( O == null ) return;
		troncons.remove(O) ;
		O.getCoursDEau().remove(this);
	}
	/** Vide la liste des troncons hydro en relation, et met à jour la relation inverse. */
	public void emptyTroncons() {
		Iterator it = troncons.iterator();
		while ( it.hasNext() ) {
			TronconHydrographique O = (TronconHydrographique)it.next();
			O.getCoursDEau().remove(this);
		}
		troncons.clear();
	}

}
