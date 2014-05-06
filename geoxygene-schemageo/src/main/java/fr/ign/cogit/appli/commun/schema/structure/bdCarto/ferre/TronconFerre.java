package fr.ign.cogit.appli.commun.schema.structure.bdCarto.ferre;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.franchissement.Troncon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;


public abstract class TronconFerre extends Troncon {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des geometries multiples (plusieurs troncons) */
	//    private GM_Curve geometrie = null;
	/** Renvoie le GM_LineString qui definit la geometrie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Definit le GM_LineString qui definit la geometrie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	/////////////// ATTRIBUTS //////////////////
	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String energie;
	public String getEnergie() {return this.energie; }
	public void setEnergie (String Energie) {energie = Energie; }

	protected String nbVoies;
	public String getNbVoies() { return nbVoies; }
	public void setNbVoies(String S) { this.nbVoies = S; }

	protected String largeur;
	public String getLargeur() {return this.largeur; }
	public void setLargeur (String Largeur) {largeur = Largeur; }

	protected String pos_sol;
	public String getPos_sol() {return this.pos_sol; }
	public void setPos_sol (String Pos_sol) {pos_sol = Pos_sol; }

	protected String classement;
	public String getClassement() {return this.classement; }
	public void setClassement (String Classement) {classement = Classement; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

	protected String touristique;
	public String getTouristique() {return this.touristique; }
	public void setTouristique (String Touristique) {touristique = Touristique; }

	protected String topoLigne;
	public String getTopoLigne() {return this.topoLigne; }
	public void setTopoLigne (String TopoLigne) {topoLigne = TopoLigne; }




	/////////////// RELATIONS //////////////////

	/** Un tronçon a un noeud initial.
	 *  1 objet Noeud est en relation "sortants" avec n objets TronvonFerre (n>0).
	 *  1 objet TronvonFerre est en relation "ini" avec 1 objet Noeud.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas etre utilisees
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	private NoeudFerre ini;
	/** Recupere le noeud initial. */
	public NoeudFerre getIni() {return ini;}
	/** Definit le noeud initial, et met a jour la relation inverse. */
	public void setIni(NoeudFerre O) {
		NoeudFerre old = ini;
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
	 *  1 objet Noeud est en relation "entrants" avec n objets TronconFerre (n>0).
	 *  1 objet TronconFerre est en relation "fin" avec 1 objet Noeud.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas etre utilisees
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	private NoeudFerre fin;
	/** Recupere le noeud final. */
	public NoeudFerre getFin() {return fin;}
	/** Definit le noeud final, et met a jour la relation inverse. */
	public void setFin(NoeudFerre O) {
		NoeudFerre old = fin;
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


	/** Lien bidirectionnel 1-n vers TronconFerre.
	 *  1 objet LigneCheminDeFer est en relation avec n objets TronconFerre (n pouvant etre nul).
	 *  1 objet TronconFerre est en relation avec 1 objet LigneCheminDeFer au plus.
	 *
	 *  Les methodes get et set sont utiles pour assurer la bidirection.
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	private LigneCheminDeFer ligneCheminDeFer;

	/** Recupere l'objet en relation. */

	public LigneCheminDeFer getLigneCheminDeFer() {return ligneCheminDeFer;  }

	/** Definit l'objet en relation, et met a jour la relation inverse. */
	public void setLigneCheminDeFer(LigneCheminDeFer O) {
		LigneCheminDeFer old = ligneCheminDeFer;
		ligneCheminDeFer = O;
		if ( old != null ) old.getTronconFerre().remove(this);
		if ( O != null) {
			if ( ! O.getTronconFerre().contains(this) ) O.getTronconFerre().add(this);
		}
	}
}
