package fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;

public abstract class CommunicationRestreinte extends ElementBDCarto {

	public String interdiction;
	public void setInterdiction(String S) {this.interdiction = S;}
	public String getInterdiction() {return interdiction;}

	public double restrictionPoids;
	public double getRestrictionPoids() {return restrictionPoids;}
	public void setRestrictionPoids(double S) {this.restrictionPoids = S;}

	public double restrictionHauteur;
	public double getRestrictionHauteur() {return restrictionHauteur;}
	public void setRestrictionHauteur(double S) {this.restrictionHauteur = S;}

	/** Une communication restreinte concerne un noeud.
	 *  1 objet CommunicationRoutiere est en relation avec 1 Noeud.
	 *  1 objet Noeud est en relation avec 0 ou 1 objet CommunicationRoutiere.
	 * 
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas etre utilisees
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	protected NoeudRoutier noeud;
	/** Recupere le noeud routier concerne */
	public NoeudRoutier getNoeud() {return noeud;}
	/** Definit le noeud routier concerne, et met a jour la relation inverse. */
	public void setNoeud(NoeudRoutier O) {
		NoeudRoutier old = noeud;
		noeud = O;
		if ( old != null ) old.setCommunication(null);
		if ( O != null ) {
			noeudID = O.getId();
			if ( O.getCommunication() != this ) O.setCommunication(this);
		} else noeudID = 0;
	}
	protected int noeudID;
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public int getNoeudID() {return noeudID;}
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public void setNoeudID (int I) {noeudID = I;}

}
