package fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.energie_fluide;


import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.ElementBDPays;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public abstract class LigneElectrique extends ElementBDPays {

	/** Renvoie la géométrie de l'objet, castée plus précisément qu'avec la méthode getGeom() */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Définit la géométrie de l'objet, castée plus précisément qu'avec la méthode setGeom() */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	protected String voltage;
	public String getVoltage() {return this.voltage; }
	public void setVoltage (String Voltage) {voltage = Voltage; }

}
