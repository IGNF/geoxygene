package fr.ign.cogit.appli.commun.schema.structure.bdCarto.habillage;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;


public abstract class ZoneOccupationDuSol extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des geometries multiples (plusieurs troncons) */
	//	  private GM_Curve geometrie = null;
	/** Renvoie le GM_Polygon qui definit la geometrie de self */
	public GM_Polygon getGeometrie() {return (GM_Polygon)geom;}
	/** Definit le GM_Polygon qui definit la geometrie de self */
	public void setGeometrie(GM_Polygon geometrie) {this.geom = geometrie;}

	/////////////// ATTRIBUTS //////////////////
	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

}
