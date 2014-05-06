package fr.ign.cogit.appli.commun.schema.structure.bdCarto.hydro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.franchissement.Troncon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;


@SuppressWarnings("unchecked")

public abstract class TronconHydrographique extends Troncon {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des geometries multiples (plusieurs troncons) */
	//    private GM_Curve geometrie = null;
	/** Renvoie le GM_LineString qui definit la geometrie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Definit le GM_LineString qui definit la geometrie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	/////////////// ATTRIBUTS //////////////////
	protected String largeur;
	public String getLargeur() {return this.largeur; }
	public void setLargeur (String Largeur) {largeur = Largeur; }

	protected String etat;
	public String getEtat() {return this.etat; }
	public void setEtat (String Etat) {etat = Etat; }

	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String navigabilite;
	public String getNavigabilite() {return this.navigabilite; }
	public void setNavigabilite (String Navigabilite) {navigabilite = Navigabilite; }

	protected String pos_sol;
	public String getPos_sol() {return this.pos_sol; }
	public void setPos_sol (String Pos_sol) {pos_sol = Pos_sol; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String S) {toponyme = S; }

	protected String sens;
	public String getSens() {return this.sens; }
	public void setSens (String Sens) {sens = Sens; }



	/////////////// RELATIONS //////////////////

	/** Un troncon peut appartenir a plusieurs cours d'eau
	 * (relation n-m, a cours d'eau a au moins 1 troncon, un troncon fait partie de 0 a n cours d'eau.
	 *
	 *  Les methodes get (sans indice) et set sont necessaires au mapping.
	 *  Les autres methodes sont la seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	private List coursDEau = new ArrayList();
	/** Recupere le CoursDEau en relation */
	public List getCoursDEau() {return coursDEau ; }
	/** Definit le CoursDEau en relation, et met a jour la relation inverse. */
	public void setCoursDEau(List L) {
		List old = new ArrayList(coursDEau);
		Iterator it1 = old.iterator();
		while ( it1.hasNext() ) {
			CoursDEau O = (CoursDEau)it1.next();
			coursDEau.remove(O);
			O.getTroncons().remove(this);
		}
		Iterator it2 = L.iterator();
		while ( it2.hasNext() ) {
			CoursDEau O = (CoursDEau)it2.next();
			coursDEau.add(O);
			O.getTroncons().add(this);
		}
	}
	/** Recupere le ieme element de la liste des CoursDEau en relation. */
	public CoursDEau getCoursDEau(int i) {return (CoursDEau)coursDEau.get(i) ; }
	/** Ajoute un element a la liste des CoursDEau en relation, et met a jour la relation inverse. */
	public void addCoursDEau(CoursDEau O) {
		if ( O == null ) return;
		coursDEau.add(O) ;
		O.getTroncons().add(this);
	}
	/** Enleve un element de la liste des CoursDEau en relation, et met a jour la relation inverse. */
	public void removeCoursDEau(CoursDEau O) {
		if ( O == null ) return;
		coursDEau.remove(O) ;
		O.getTroncons().remove(this);
	}
	/** Vide la liste des CoursDEau en relation, et met a jour la relation inverse. */
	public void emptyCoursDEau() {
		Iterator it = coursDEau.iterator();
		while ( it.hasNext() ) {
			CoursDEau O = (CoursDEau)it.next();
			O.getTroncons().remove(this);
		}
		coursDEau.clear();
	}



	/** Un troncon a un noeud initial.
	 *  1 objet Noeud est en relation "sortants" avec n objets TronconHydrographique (n>0).
	 *  1 objet TronconHydrographique est en relation "ini" avec 1 objet Noeud.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas etre utilisees
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	private NoeudHydrographique ini;
	/** Recupere le noeud initial. */
	public NoeudHydrographique getIni() {return ini;}
	/** Definit le noeud initial, et met a jour la relation inverse. */
	public void setIni(NoeudHydrographique O) {
		NoeudHydrographique old = ini;
		ini = O;
		if ( old  != null ) old.getSortants().remove(this);
		if ( O != null ) {
			iniID = O.getId();
			if ( !(O.getSortants().contains(this)) ) O.getSortants().add(this);
		} else iniID = 0;
	}
	/** Pour le mapping avec OJB, dans le cas d'une relation 1-n, du cote 1 de la relation */
	private int iniID;
	/** Ne pas utiliser, necessaire au mapping*/
	public void setIniID(int I) {iniID = I;}
	/** Ne pas utiliser, necessaire au mapping*/
	public int getIniID() {return iniID;}


	/** Un troncon a un noeud final.
	 *  1 objet Noeud est en relation "entrants" avec n objets TronconHydrographique (n>0).
	 *  1 objet TronconHydrographique est en relation "fin" avec 1 objet Noeud.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas etre utilisees
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	private NoeudHydrographique fin;
	/** Recupere le noeud final. */
	public NoeudHydrographique getFin() {return fin;}
	/** Definit le noeud final, et met a jour la relation inverse. */
	public void setFin(NoeudHydrographique O) {
		NoeudHydrographique old = fin;
		fin = O;
		if ( old  != null ) old.getEntrants().remove(this);
		if ( O != null ) {
			finID = O.getId();
			if ( !(O.getEntrants().contains(this)) ) O.getEntrants().add(this);
		} else finID = 0;
	}
	/** Pour le mapping avec OJB, dans le cas d'une relation 1-n, du cote 1 de la relation */
	private int finID;
	/** Ne pas utiliser, necessaire au mapping*/
	public void setFinID(int I) {finID = I;}
	/** Ne pas utiliser, necessaire au mapping*/
	public int getFinID() {return finID;}


}
