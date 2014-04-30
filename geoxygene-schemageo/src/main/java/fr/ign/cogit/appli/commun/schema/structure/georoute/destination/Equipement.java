package fr.ign.cogit.appli.commun.schema.structure.georoute.destination;

import fr.ign.cogit.appli.commun.schema.structure.georoute.ElementGeoroute;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public class Equipement extends  ElementGeoroute {

	protected Acces acces;
	/** Récupère l'acces en relation */
	public Acces getAcces() {return acces;}
	/** Définit l'acces en relation, et met à jour la relation inverse. */
	public void setAcces(Acces O) {
		Acces old = acces;
		acces = O;
		if ( old != null ) old.setEquipement(null);
		if ( O != null ) {
			accesID = O.getId();
			if ( O.getEquipement() != this ) O.setEquipement(this);
		} else accesID = 0;
	}
	protected int accesID;
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public int getAccesID() {return accesID;}
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public void setAccesID (int I) {accesID = I;}

	//     private GM_Point geometrie = null;
	/** Renvoie la géométrie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}

}
