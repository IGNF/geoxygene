package fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;

public abstract class DebutSection extends ElementBDCarto {

	protected String gestionnaire;
	public String getGestionnaire() {return gestionnaire; }
	public void setGestionnaire (String S) {gestionnaire = S; }

	protected String sens;
	public String getSens() {return sens; }
	public void setSens (String S) {sens = S; }

	/** Les debuts de section se suivent.
	 *  1 objet Debut de section a 1 ou 0 successeur.
	 *  1 objet Debut de section a 1 ou 0 predecesseur.
	 * 
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas etre utilisees
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	protected DebutSection successeur;
	/** Recupere le successeur */
	public DebutSection getSuccesseur() {return successeur;}
	/** Definit le successeur, et met a jour la relation inverse. */
	public void setSuccesseur(DebutSection O) {
		DebutSection old = successeur;
		successeur = O;
		if ( old != null ) old.setPredecesseur(null);
		if ( O != null ) {
			successeurID = O.getId();
			if ( O.getPredecesseur() != this ) O.setPredecesseur(this);
		} else successeurID = 0;
	}
	protected int successeurID;
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public int getSuccesseurID() {return successeurID;}
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public void setSuccesseurID (int I) {successeurID = I;}


	protected DebutSection predecesseur;
	/** Recupere le predecesseur*/
	public DebutSection getPredecesseur() {return predecesseur;}
	/** Definit le predecesseur, et met a jour la relation inverse. */
	public void setPredecesseur(DebutSection O) {
		DebutSection old = predecesseur;
		predecesseur = O;
		if ( old != null ) old.setSuccesseur(null);
		if ( O != null ) {
			predecesseurID = O.getId();
			if ( O.getSuccesseur() != this ) O.setSuccesseur(this);
		} else predecesseurID = 0;
	}
	protected int predecesseurID;
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public int getPredecesseurID() {return predecesseurID;}
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public void setPredecesseurID (int I) {predecesseurID = I;}


	/** Une debut de section est situee sur un troncon.
	 *  1 objet DebutSection est en relation avec 1 Troncon.
	 *  1 objet Troncon est en relation avec 0 ou 1 objet DebutSection.
	 * 
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas etre utilisees
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	protected TronconRoute troncon;
	/** Recupere le troncon routier concerne */
	public TronconRoute getTroncon() {return troncon;}
	/** Definit le troncon routier concerne, et met a jour la relation inverse. */
	public void setTroncon(TronconRoute O) {
		TronconRoute old = troncon;
		troncon = O;
		if ( old != null ) old.setDebutSection(null);
		if ( O != null ) {
			tronconID = O.getId();
			if ( O.getDebutSection() != this ) O.setDebutSection(this);
		} else tronconID = 0;
	}
	protected int tronconID;
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public int getTronconID() {return tronconID;}
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public void setTronconID (int I) {tronconID = I;}

	//est situe sur
}
