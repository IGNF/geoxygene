package fr.ign.cogit.appli.commun.schema.structure.bdCarto.equipements;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;


public abstract class Digue extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des geometries multiples (plusieurs troncons) */
	//	  private GM_Curve geometrie = null;
	/** Renvoie le GM_LineString qui definit la geometrie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Definit le GM_LineString qui definit la geometrie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	/////////////// ATTRIBUTS //////////////////
	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

}