package fr.ign.cogit.appli.commun.schema.shp.georoute.routier;

import fr.ign.cogit.appli.commun.schema.shp.georoute.ElementGeoroute;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public class NoeudRoutier extends ElementGeoroute {

	//     private GM_Point geometrie = null;
	/** Renvoie la géométrie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}

	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }



}
