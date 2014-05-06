package fr.ign.cogit.appli.commun.schema.structure.bdCarto.franchissement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public abstract class Franchissement extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	//    protected GM_Point geometrie = null;
	/** Renvoie le GM_Point qui definit la geometrie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Definit le GM_Point qui definit la geometrie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}

	/////////////// ATTRIBUTS //////////////////
	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

	protected double cote;
	public double getCote() {return this.cote; }
	public void setCote (double Cote) {cote = Cote; }

	/////////////// RELATIONS //////////////////
	/** Un franchissement peut concerner plusieurs troncons hydro routier ou ferre,
	 * par l'intermediaire de la classe PassePar.
	 *  1 objet Franchissment peut etre en relation avec 2 ou n "objets-relation" PassePar.
	 *  1 "objet-relation" PassePar est en relation avec 1 objet Franchissement.
	 *
	 *  Les methodes get (sans indice) et set sont necessaires au mapping.
	 *  Les autres methodes sont la seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	protected List<PassePar> passentPar = new ArrayList<PassePar>();

	/** Recupere la liste des PassePar en relation. */
	public List<PassePar> getPassentPar() {return passentPar; }
	/** Definit la liste des PassePar en relation, et met a jour la relation inverse. */
	public void setPassentPar(List<PassePar> L) {
		List<PassePar> old = new ArrayList<PassePar>(passentPar);
		Iterator<PassePar> it1 = old.iterator();
		while ( it1.hasNext() ) {
			PassePar O = it1.next();
			O.setFranchissement(null);
		}
		Iterator<PassePar> it2 = L.iterator();
		while ( it2.hasNext() ) {
			PassePar O = it2.next();
			O.setFranchissement(this);
		}
	}
	/** Recupere le ieme element de la liste des PassePar en relation. */
	public PassePar getPassePar(int i) {return passentPar.get(i) ; }
	/** Ajoute un objet a la liste des objets en relation, et met a jour la relation inverse. */
	public void addPassePar(PassePar O) {
		if ( O == null ) return;
		passentPar.add(O) ;
		O.setFranchissement(this) ;
	}
	/** Enleve un element de la liste des PassePar en relation, et met a jour la relation inverse. */
	public void removePassePar(PassePar O) {
		if ( O == null ) return;
		passentPar.remove(O) ;
		O.setFranchissement(null);
	}
	/** Vide la liste des PassePar en relation, et met a jour la relation inverse. */
	public void emptyPassentPar() {
		List<PassePar> old = new ArrayList<PassePar>(passentPar);
		Iterator<PassePar> it = old.iterator();
		while ( it.hasNext() ) {
			PassePar O = it.next();
			O.setFranchissement(null);
		}
	}

}

