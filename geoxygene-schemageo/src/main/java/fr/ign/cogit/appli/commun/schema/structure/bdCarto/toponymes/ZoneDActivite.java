package fr.ign.cogit.appli.commun.schema.structure.bdCarto.toponymes;


import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public abstract class ZoneDActivite extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	//    private GM_Point geometrie = null;
	/** Renvoie le GM_Point qui definit la geometrie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Definit le GM_Point qui definit la geometrie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}

	/////////////// ATTRIBUTS //////////////////

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

}