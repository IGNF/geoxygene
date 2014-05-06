package fr.ign.cogit.appli.commun.schema.structure.bdCarto.toponymes;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public abstract class ZoneReglementeeTouristique extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	//	  private GM_Point geometrie = null;
	/** Renvoie le GM_Point qui definit la geometrie de self */
	public GM_Polygon getGeometrie() {return (GM_Polygon)geom;}
	/** Definit le GM_Point qui definit la geometrie de self */
	public void setGeometrie(GM_Polygon geometrie) {this.geom = geometrie;}

	/////////////// ATTRIBUTS //////////////////
	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

}