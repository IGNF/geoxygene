package fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;

public abstract class Accede extends ElementBDCarto {

	protected String cote;
	public String getCote() {return cote; }
	public void setCote (String S) {cote = S; }

	/////////////// RELATIONS //////////////////
	/** Troncon concerné par la relation.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas être utilisées
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	protected TronconRoute troncon;
	/** Récupère le tronçon en relation. */
	public TronconRoute getTroncon() {return troncon;  }
	/** Définit le tronçon en relation, et met à jour la relation inverse. */
	public void setTroncon(TronconRoute O) {
		TronconRoute old = troncon;
		troncon = O;
		if ( old  != null ) old.getAccedent().remove(this);
		if ( O != null ) {
			tronconID = O.getId();
			if ( !(O.getAccedent().contains(this)) ) O.getAccedent().add(this);
		} else tronconID = 0;
	}
	/** Pour le mapping avec OJB, dans le cas d'une relation 1-n, du cote 1 de la relation */
	protected int tronconID;
	/** Ne pas utiliser, necessaire au mapping OJB */
	public void setTronconID(int I) {tronconID = I;}
	/** Ne pas utiliser, necessaire au mapping OJB */
	public int getTronconID() {return tronconID;}


	/** Equipement concerné par la relation.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas être utilisées
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	/** Ne pas utiliser. Pour le mapping avec OJB, dans le cas d'une relation 1-n, du cote 1 de la relation */
	protected EquipementRoutier equipement;
	/** Récupère le equipement en relation. */
	public EquipementRoutier getEquipement() {return equipement;  }
	/** Définit le equipement en relation, et met à jour la relation inverse. */
	public void setEquipement(EquipementRoutier O) {
		EquipementRoutier old = equipement;
		equipement = O;
		if ( old  != null ) old.getAccedent().remove(this);
		if ( O != null ) {
			equipementID = O.getId();
			if ( !(O.getAccedent().contains(this)) ) O.getAccedent().add(this);
		} else equipementID = 0;
	}
	/** Pour le mapping avec OJB, dans le cas d'une relation 1-n, du cote 1 de la relation */
	protected int equipementID;
	/** Ne pas utiliser, necessaire au mapping OJB */
	public void setEquipementID(int I) {equipementID = I;}
	/** Ne pas utiliser, necessaire au mapping OJB */
	public int getEquipementID() {return equipementID;}



}
