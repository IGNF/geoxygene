package fr.ign.cogit.appli.commun.schema.shp.bdcarto.hydrographie;

import fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public abstract class ZoneHydrographiqueTexture extends ElementBDCarto {

	/** Renvoie la géométrie de self */
	public GM_Polygon getGeometrie() {return (GM_Polygon)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_Polygon geometrie) {this.geom = geometrie;}


	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

}
