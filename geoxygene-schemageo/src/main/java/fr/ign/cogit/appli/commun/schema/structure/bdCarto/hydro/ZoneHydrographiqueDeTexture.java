package fr.ign.cogit.appli.commun.schema.structure.bdCarto.hydro;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/** Zone plate au drainage complexe dans laquelle circule un ensemble de portions de cours d'eau
 *  formant un entrelac de bras d'egale importance. */
public abstract class ZoneHydrographiqueDeTexture extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	//    private GM_Polygon geometrie = null;
	/** Renvoie le GM_Polygon qui definit la geometrie de self */
	public GM_Polygon getGeometrie() {return (GM_Polygon)geom;}
	/** Definit le GM_Polygon qui definit la geometrie de self */
	public void setGeometrie(GM_Polygon geometrie) {this.geom = geometrie;}

	/////////////// ATTRIBUTS //////////////////
	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

}
