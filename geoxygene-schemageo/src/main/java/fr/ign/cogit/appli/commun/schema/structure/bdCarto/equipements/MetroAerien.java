package fr.ign.cogit.appli.commun.schema.structure.bdCarto.equipements;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;


public abstract class MetroAerien extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des geometries multiples (plusieurs troncons) */
	//	  private GM_Curve geometrie = null;
	/** Renvoie le GM_LineString qui definit la geometrie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Definit le GM_LineString qui definit la geometrie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	/////////////// PAS D'ATTRIBUTS //////////////////

}