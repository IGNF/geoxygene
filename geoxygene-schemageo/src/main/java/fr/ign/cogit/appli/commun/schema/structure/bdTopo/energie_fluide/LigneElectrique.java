package fr.ign.cogit.appli.commun.schema.structure.bdTopo.energie_fluide;


import fr.ign.cogit.appli.commun.schema.structure.bdTopo.ElementBDTopo;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public abstract class LigneElectrique extends ElementBDTopo {

	/** Renvoie la géométrie de l'objet, castée plus précisément qu'avec la méthode getGeom() */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Définit la géométrie de l'objet, castée plus précisément qu'avec la méthode setGeom() */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	protected String voltage;
	public String getVoltage() {return this.voltage; }
	public void setVoltage (String Voltage) {voltage = Voltage; }

}
