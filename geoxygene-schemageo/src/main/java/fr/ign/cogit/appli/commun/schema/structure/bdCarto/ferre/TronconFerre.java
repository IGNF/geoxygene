package fr.ign.cogit.appli.commun.schema.structure.bdCarto.ferre;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.franchissement.Troncon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;


public abstract class TronconFerre extends Troncon {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des géométries multiples (plusieurs tronçons) */
	//    private GM_Curve geometrie = null;
	/** Renvoie le GM_LineString qui définit la géométrie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Définit le GM_LineString qui définit la géométrie de self */
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
	 *  1 objet Noeud est en relation "sortants" avec n objets TronçonFerre (n>0).
	 *  1 objet TronçonFerre est en relation "ini" avec 1 objet Noeud.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas être utilisées
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	private NoeudFerre ini;
	/** Récupère le noeud initial. */
	public NoeudFerre getIni() {return ini;}
	/** Définit le noeud initial, et met à jour la relation inverse. */
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
	/** Ne pas utiliser, nécessaire au mapping*/
	public void setIniID(int I) {iniID = I;}
	/** Ne pas utiliser, nécessaire au mapping*/
	public int getIniID() {return iniID;}


	/** Un tronçon a un noeud final.
	 *  1 objet Noeud est en relation "entrants" avec n objets TronçonFerre (n>0).
	 *  1 objet TronçonFerre est en relation "fin" avec 1 objet Noeud.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas être utilisées
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	private NoeudFerre fin;
	/** Récupère le noeud final. */
	public NoeudFerre getFin() {return fin;}
	/** Définit le noeud final, et met à jour la relation inverse. */
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
	/** Ne pas utiliser, nécessaire au mapping*/
	public void setFinID(int I) {finID = I;}
	/** Ne pas utiliser, nécessaire au mapping*/
	public int getFinID() {return finID;}


	/** Lien bidirectionnel 1-n vers TronconFerre.
	 *  1 objet LigneCheminDeFer est en relation avec n objets TronconFerre (n pouvant etre nul).
	 *  1 objet TronconFerre est en relation avec 1 objet LigneCheminDeFer au plus.
	 *
	 *  Les méthodes get et set sont utiles pour assurer la bidirection.
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	private LigneCheminDeFer ligneCheminDeFer;

	/** Récupère l'objet en relation. */

	public LigneCheminDeFer getLigneCheminDeFer() {return ligneCheminDeFer;  }

	/** Définit l'objet en relation, et met à jour la relation inverse. */
	public void setLigneCheminDeFer(LigneCheminDeFer O) {
		LigneCheminDeFer old = ligneCheminDeFer;
		ligneCheminDeFer = O;
		if ( old != null ) old.getTronconFerre().remove(this);
		if ( O != null) {
			if ( ! O.getTronconFerre().contains(this) ) O.getTronconFerre().add(this);
		}
	}
}
