package fr.ign.cogit.appli.commun.schema.structure.bdCarto.franchissement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;

public abstract class Troncon extends ElementBDCarto {

	/** Un tronçon peut passer par plusieurs franchissements,
	 * par l'intermédiaire de la classe-relation PassePar.
	 *  1 objet Troncon peut etre en relation avec 0 ou n objets-relations PassePar.
	 *  1 objet-relation PassePar est en relation avec 1 objet Troncon.
	 *
	 *  Les méthodes get (sans indice) et set sont nécessaires au mapping.
	 *  Les autres méthodes sont là seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	protected List<PassePar> passentPar = new ArrayList<PassePar>();

	/** Récupère la liste des PassePar en relation. */
	public List<PassePar> getPassentPar() {return passentPar; }
	/** Définit la liste des PassePar en relation, et met à jour la relation inverse. */
	public void setPassentPar(List <PassePar>L) {
		List<PassePar> old = new ArrayList<PassePar>(passentPar);
		Iterator<PassePar> it1 = old.iterator();
		while ( it1.hasNext() ) {
			PassePar O = it1.next();
			O.setTroncon(null);
		}
		Iterator<PassePar> it2 = L.iterator();
		while ( it2.hasNext() ) {
			PassePar O = it2.next();
			O.setTroncon(this);
		}
	}
	/** Récupère le ième élément de la liste des PassePar en relation. */
	public PassePar getPassePar(int i) {return passentPar.get(i) ; }
	/** Ajoute un objet à la liste des objets en relation, et met à jour la relation inverse. */
	public void addPassePar(PassePar O) {
		if ( O == null ) return;
		passentPar.add(O) ;
		O.setTroncon(this) ;
	}
	/** Enlève un élément de la liste des PassePar en relation, et met à jour la relation inverse. */
	public void removePassePar(PassePar O) {
		if ( O == null ) return;
		passentPar.remove(O) ;
		O.setTroncon(null);
	}
	/** Vide la liste des PassePar en relation, et met à jour la relation inverse. */
	public void emptyPassentPar() {
		List<PassePar> old = new ArrayList<PassePar>(passentPar);
		Iterator<PassePar> it = old.iterator();
		while ( it.hasNext() ) {
			PassePar O = it.next();
			O.setTroncon(null);
		}
	}

}