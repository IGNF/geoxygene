package fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;


public abstract class Route extends ElementBDCarto{

	public String numero;
	public String getNumero() {return numero;}
	public void setNumero(String S) {numero = S;}

	public String classement;
	public String getClassement() {return classement;}
	public void setClassement(String S) {this.classement = S;}

	public String gestionnaire;
	public String getGestionnaire() {return gestionnaire;}
	public void setGestionnaire(String S) {gestionnaire = S;}


	/** Lien bidirectionnel persistant 1-n vers troncon de route.
	 *  1 objet Route est en relation avec 1 ou n objets troncons.
	 *  1 objet Troncon est en relation avec 0 ou n objets routes.
	 *
	 *  NB: un objet route ne doit pas etre en relation plusieurs fois avec le meme objet troncon :
	 *  il est impossible de bien gerer des relations 1-n bidirectionnelles avec doublons.
	 *
	 *  Les methodes get (sans indice) et set sont necessaires au mapping.
	 *  Les autres methodes sont la seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	private List<TronconRoute> troncons = new ArrayList<TronconRoute>();
	/** Recupere le troncon routier en relation */
	public List<TronconRoute> getTroncons() {return troncons ; }
	/** Definit le troncon routier en relation, et met a jour la relation inverse. */
	public void setTroncons (List<TronconRoute> L) {
		List<TronconRoute> old = new ArrayList<TronconRoute>(troncons);
		Iterator<TronconRoute> it1 = old.iterator();
		while ( it1.hasNext() ) {
			TronconRoute O = it1.next();
			troncons.remove(O);
			O.setRoute(null);
		}
		Iterator<TronconRoute> it2 = L.iterator();
		while ( it2.hasNext() ) {
			TronconRoute O = it2.next();
			troncons.add(O);
			O.setRoute(this);
		}
	}
	/** Recupere le ieme element de la liste des troncons routier en relation. */
	public TronconRoute getTroncon(int i) {return troncons.get(i) ; }
	/** Ajoute un element a la liste des troncons routier en relation, et met a jour la relation inverse. */
	public void addTroncon(TronconRoute O) {
		if ( O == null ) return;
		troncons.add(O) ;
		O.setRoute(this);
	}
	/** Enleve un element de la liste des troncons routier en relation, et met a jour la relation inverse. */
	public void removeTroncon(TronconRoute O) {
		if ( O == null ) return;
		troncons.remove(O) ;
		O.setRoute(null);
	}
	/** Vide la liste des troncons routier en relation, et met a jour la relation inverse. */
	public void emptyTroncons() {
		Iterator<TronconRoute> it = troncons.iterator();
		while ( it.hasNext() ) {
			TronconRoute O = it.next();
			O.setRoute(null);
		}
		troncons.clear();
	}

}
