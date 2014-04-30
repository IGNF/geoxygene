package fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;

public abstract class CarrefourComplexe extends ElementBDCarto {

	public String numero;
	public String getNumero() {return numero;}
	public void setNumero(String S) {this.numero = S;}

	public String nature;
	public String getNature() {return nature;}
	public void setNature(String S) {this.nature = S;}

	public String toponyme;
	public String getToponyme() {return toponyme;}
	public void setToponyme(String S) {this.toponyme = S;}

	/** Lien bidirectionnel persistant 1-n vers noeud routier.
	 *  1 objet Carrefour Complexe est en relation avec 1 ou n objets noeud.
	 *  1 objet noeud est en relation avec 0 ou 1 objet Carrefour complexe.
	 *
	 *  NB: un objet route ne doit pas être en relation plusieurs fois avec le même objet troncon :
	 *  il est impossible de bien gérer des relations 1-n bidirectionnelles avec doublons.
	 *
	 *  Les méthodes get (sans indice) et set sont nécessaires au mapping.
	 *  Les autres méthodes sont là seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	protected List<NoeudRoutier> noeuds = new ArrayList<NoeudRoutier>();

	/** Récupère la liste des noeuds. */
	public List<NoeudRoutier> getNoeuds() {return noeuds ; }
	/** Définit la liste des Noeuds, et met à jour la relation inverse. */
	public void setNoeuds (List <NoeudRoutier>L) {
		List<NoeudRoutier> old = new ArrayList<NoeudRoutier>(noeuds);
		Iterator<NoeudRoutier> it1 = old.iterator();
		while ( it1.hasNext() ) {
			NoeudRoutier O = it1.next();
			O.setCarrefourComplexe(null);
		}
		Iterator<NoeudRoutier> it2 = L.iterator();
		while ( it2.hasNext() ) {
			NoeudRoutier O = it2.next();
			O.setCarrefourComplexe(this);
		}
	}
	/** Récupère le ième élément composant. */
	public NoeudRoutier getNoeud(int i) {return noeuds.get(i) ; }
	/** Ajoute un objet à la liste des objets en relation, et met à jour la relation inverse. */
	public void addNoeud(NoeudRoutier O) {
		if ( O == null ) return;
		noeuds.add(O) ;
		O.setCarrefourComplexe(this) ;
	}
	/** Enlève un élément de la liste noeuds, et met à jour la relation inverse. */
	public void removeNoeud(NoeudRoutier O) {
		if ( O == null ) return;
		noeuds.remove(O) ;
		O.setCarrefourComplexe(null);
	}
	/** Vide la liste des noeuds, et met à jour la relation inverse. */
	public void emptyNoeuds() {
		List<NoeudRoutier> old = new ArrayList<NoeudRoutier>(noeuds);
		Iterator<NoeudRoutier> it = old.iterator();
		while ( it.hasNext() ) {
			NoeudRoutier O = it.next();
			O.setCarrefourComplexe(null);
		}
	}

}
