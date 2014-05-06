package fr.ign.cogit.appli.commun.schema.structure.georoute.destination;

import fr.ign.cogit.appli.commun.schema.structure.georoute.ElementGeoroute;
import fr.ign.cogit.appli.commun.schema.structure.georoute.routier.NoeudRoutier;

public abstract class Acces extends ElementGeoroute {

	/** Creates new Accede */
	public Acces() {
	}

	public String nature;
	public String getNature() {return nature;}
	public void setNature(String nature) {this.nature = nature;}


	/** Un NoeudRoutier concerne un acces.
	 *  1 objet Acces est en relation avec 1 Noeud.
	 *  1 objet Noeud est en relation avec 0 ou 1 objet Acces.
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
		if ( old != null ) old.setAcces(null);
		if ( O != null ) {
			noeudID = O.getId();
			if ( O.getAcces() != this ) O.setAcces(this);
		} else noeudID = 0;
	}
	protected int noeudID;
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public int getNoeudID() {return noeudID;}
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public void setNoeudID (int I) {noeudID = I;}



	/** Un EquipementRoutier concerne un acces.
	 *  1 objet Acces est en relation avec 1 EquipementRoutier.
	 *  1 objet EquipementRoutier est en relation avec 1 objet Acces.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas etre utilisees
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	protected Equipement equipement;
	/** Recupere le noeud routier en relation */
	public Equipement getEquipement() {return equipement;}
	/** Definit le noeud routier en relation, et met a jour la relation inverse. */
	public void setEquipement(Equipement O) {
		Equipement old = equipement;
		equipement = O;
		if ( old != null ) old.setAcces(null);
		if ( O != null ) {
			equipementID = O.getId();
			if ( O.getAcces() != this ) O.setAcces(this);
		} else equipementID = 0;
	}
	protected int equipementID;
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public int getEquipementID() {return equipementID;}
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public void setEquipementID (int I) {equipementID = I;}

}
