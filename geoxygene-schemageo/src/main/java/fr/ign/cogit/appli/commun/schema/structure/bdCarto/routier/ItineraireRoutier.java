package fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;

public abstract class ItineraireRoutier extends ElementBDCarto {

	protected String numero;
	public String getNumero() {return this.numero; }
	public void setNumero (String Numero) {numero = Numero; }

	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

	/** Lien bidirectionnel persistant n-m vers troncon de route.
	 *  1 objet Itineraire est en relation avec 1 ou n objets troncons.
	 *  1 objet Troncon est en relation avec 0 ou n objets itineraires.
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
	public List<TronconRoute> troncons = new ArrayList<TronconRoute>();
	/** Recupere les troncons routiers en relation */
	public List<TronconRoute> getTroncons() {return troncons ; }
	/** Definit les troncons routiers en relation, et met a jour la relation inverse. */
	public void setTroncons (List<TronconRoute> L) {
		List<TronconRoute> old = new ArrayList<TronconRoute>(troncons);
		Iterator<TronconRoute> it1 = old.iterator();
		while ( it1.hasNext() ) {
			TronconRoute O = it1.next();
			troncons.remove(O);
			O.getItineraires().remove(this);
		}
		Iterator<TronconRoute> it2 = L.iterator();
		while ( it2.hasNext() ) {
			TronconRoute O = it2.next();
			troncons.add(O);
			O.getItineraires().add(this);
		}
	}
	/** Recupere le ieme element de la liste des troncons routiers en relation. */
	public TronconRoute getTroncon(int i) {return troncons.get(i) ; }
	/** Ajoute un element a la liste des troncons routiers en relation, et met a jour la relation inverse. */
	public void addTroncon(TronconRoute O) {
		if ( O == null ) return;
		troncons.add(O) ;
		O.getItineraires().add(this);
	}
	/** Enleve un element de la liste des troncons routiers en relation, et met a jour la relation inverse. */
	public void removeTroncon(TronconRoute O) {
		if ( O == null ) return;
		troncons.remove(O) ;
		O.getItineraires().remove(this);
	}
	/** Vide la liste des troncons routiers en relation, et met a jour la relation inverse. */
	public void emptyTroncons() {
		Iterator<TronconRoute> it = troncons.iterator();
		while ( it.hasNext() ) {
			TronconRoute O = it.next();
			O.getItineraires().remove(this);
		}
		troncons.clear();
	}



}
