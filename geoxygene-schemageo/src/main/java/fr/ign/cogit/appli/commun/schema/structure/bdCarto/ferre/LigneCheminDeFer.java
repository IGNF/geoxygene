package fr.ign.cogit.appli.commun.schema.structure.bdCarto.ferre;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;


public abstract class LigneCheminDeFer extends ElementBDCarto {

	/////////////// ATTRIBUTS //////////////////

	private String caractTourist;
	public String getCaractTourist() {return caractTourist;};
	public void setCaractTourist(String CaractTourist) {caractTourist = CaractTourist;};

	private String toponyme;
	public String getToponyme() {return toponyme;};
	public void setToponyme(String Toponyme) {toponyme = Toponyme;};



	//	///////////// RELATIONS //////////////////
	/** Lien bidirectionnel 1-n vers TronconFerre.
	 *  1 objet LigneCheminDeFer est en relation avec n objets TronconFerre (n pouvant etre nul).
	 *  1 objet TronconFerre est en relation avec 1 objet LigneCheminDeFer au plus.
	 *
	 *  NB: un objet LigneCheminDeFer ne doit pas etre en relation plusieurs fois avec le meme objet TronconFerre :
	 *  il est impossible de bien gerer des relations 1-n bidirectionnelles avec doublons.
	 *
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec
	 *  les methodes fournies.
	 * 
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	private List<TronconFerre> tronconFerre = new ArrayList<TronconFerre>();

	/** Recupere la liste des objets en relation. */
	public List<TronconFerre> getTronconFerre() {return tronconFerre ; }

	/** Definit la liste des objets en relation, et met a jour la relation inverse. */
	public void setTronconFerre (List <TronconFerre>L) {
		List<TronconFerre> old = new ArrayList<TronconFerre>(tronconFerre);
		Iterator<TronconFerre> it1 = old.iterator();
		while ( it1.hasNext() ) {
			TronconFerre O = it1.next();
			O.setLigneCheminDeFer(null);
		}
		Iterator<TronconFerre> it2 = L.iterator();
		while ( it2.hasNext() ) {
			TronconFerre O = it2.next();
			O.setLigneCheminDeFer(this);
		}
	}

	/** Recupere le ieme element de la liste des objets en relation. */
	public TronconFerre getTronconFerre(int i) {return tronconFerre.get(i) ; }

	/** Ajoute un objet  a la liste des objets en relation, et met a jour la relation inverse. */
	public void addTronconFerre (TronconFerre O) {
		if ( O == null ) return;
		tronconFerre.add(O) ;
		O.setLigneCheminDeFer(this) ;
	}

	/** Enleve un element de la liste des objets en relation, et met a jour la relation inverse. */
	public void removeTronconFerre (TronconFerre O) {
		if ( O == null ) return;
		tronconFerre.remove(O) ;
		O.setLigneCheminDeFer(null);
	}

	/** Vide la liste des objets en relation, et met a jour la relation inverse. */
	public void emptyTronconFerre () {
		List<TronconFerre> old = new ArrayList<TronconFerre>(tronconFerre);
		Iterator<TronconFerre> it = old.iterator();
		while ( it.hasNext() ) {
			TronconFerre O = it.next();
			O.setLigneCheminDeFer(null);
		}
	}



}