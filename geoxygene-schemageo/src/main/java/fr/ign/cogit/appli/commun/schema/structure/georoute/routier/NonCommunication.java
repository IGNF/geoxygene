package fr.ign.cogit.appli.commun.schema.structure.georoute.routier;

import fr.ign.cogit.appli.commun.schema.structure.georoute.ElementGeoroute;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public abstract class NonCommunication extends ElementGeoroute {

	//     private GM_Point geometrie = null;
	/** Renvoie la geometrie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Definit la geometrie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}


	/** Un troncon entrant concerne une non communication.
	 *  1 objet NonCommunication est en relation avec 1 troncon entrant.
	 *  1 objet Troncon entrant est en relation avec 0 ou 1 objet NonCommunication.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas etre utilisees
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	public TronconRoute tronconEntrant;
	public TronconRoute getTronconEntrant() {return tronconEntrant;}
	public void setTronconEntrant(TronconRoute O) {
		TronconRoute old = tronconEntrant;
		tronconEntrant = O;
		if ( old  != null ) old.getNonCommInis().remove(this);
		if ( O != null ) {
			tronconEntrantID = O.getId();
			if ( !(O.getNonCommInis().contains(this)) ) O.getNonCommInis().add(this);
		} else tronconEntrantID = 0;
	}
	protected int tronconEntrantID;
	public int getTronconEntrantID() {return tronconEntrantID;}
	public void setTronconEntrantID (int I) {tronconEntrantID = I;}


	/** Un troncon sortant concerne une non communication.
	 *  1 objet NonCommunication est en relation avec 1 troncon sortant.
	 *  1 objet Troncon sortant est en relation avec 0 ou 1 objet NonCommunication.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas etre utilisees
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	public TronconRoute tronconSortant;
	public TronconRoute getTronconSortant() {return tronconSortant;}
	public void setTronconSortant(TronconRoute O) {
		TronconRoute old = tronconSortant;
		tronconSortant = O;
		if ( old  != null ) old.getNonCommFins().remove(this);
		if ( O != null ) {
			tronconSortantID = O.getId();
			if ( !(O.getNonCommFins().contains(this)) ) O.getNonCommFins().add(this);
		} else tronconSortantID = 0;
	}
	protected int tronconSortantID;
	public int getTronconSortantID() {return tronconSortantID;}
	public void setTronconSortantID (int I) {tronconSortantID = I;}


	/** Un noeud concerne une non communication.
	 *  1 objet NonCommunication est en relation avec 1 Noeud.
	 *  1 objet Noeud est en relation avec 0 ou 1 objet NonCommunication.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas etre utilisees
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	protected NoeudRoutier noeud;
	/** Recupere le noeud routier en relation */
	public NoeudRoutier getNoeud() {return noeud;}
	/** Definit le noeud routier en relation, et met a jour la relation inverse. */
	public void setNoeud(NoeudRoutier O) {
		NoeudRoutier old = noeud;
		noeud = O;
		if ( old  != null ) old.getNonCommunications().remove(this);
		if ( O != null ) {
			noeudID = O.getId();
			if ( !(O.getNonCommunications().contains(this)) ) O.getNonCommunications().add(this);
		} else noeudID = 0;
	}
	protected int noeudID;
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public int getNoeudID() {return noeudID;}
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public void setNoeudID (int I) {noeudID = I;}



}
