package fr.ign.cogit.appli.commun.schema.shp.georoute.routier;

import fr.ign.cogit.appli.commun.schema.shp.georoute.ElementGeoroute;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public class CarrefourComplexe extends ElementGeoroute {

	//////////////// GEOMETRIE //////////////////
	//    private GM_Polygon geometrie = null;
	/** Renvoie le GM_Polygon qui définit la géométrie de self */
	public GM_Polygon getGeometrie() {return (GM_Polygon)this.geom;}
	/** Définit le GM_Polygon qui définit la géométrie de self */
	public void setGeometrie(GM_Polygon geometrie) {this.geom = geometrie;}

	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }
}
