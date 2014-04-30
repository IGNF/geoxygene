package fr.ign.cogit.appli.commun.schema.shp.bdcarto.administratif;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * Classe geographique. Classe generee automatiquement par le chargeur de la
 * plate-forme Oxygene
 */

public abstract class Arrondissement extends
		fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto {

	/**
	 * Renvoie la géométrie de l'objet, castée plus précisément qu'avec la
	 * méthode getGeom()
	 */
	public GM_Polygon getGeometrie() {
		return (GM_Polygon) geom;
	}

	/**
	 * Définit la géométrie de l'objet, castée plus précisément qu'avec la
	 * méthode setGeom()
	 */
	public void setGeometrie(GM_Polygon G) {
		this.geom = G;
	}

	protected String insee_arr;

	public String getInsee_arr() {
		return this.insee_arr;
	}

	public void setInsee_arr(String Insee_arr) {
		insee_arr = Insee_arr;
	}

	protected String insee_dept;

	public String getInsee_dept() {
		return this.insee_dept;
	}

	public void setInsee_dept(String Insee_dept) {
		insee_dept = Insee_dept;
	}

	protected String insee_reg;

	public String getInsee_reg() {
		return this.insee_reg;
	}

	public void setInsee_reg(String Insee_reg) {
		insee_reg = Insee_reg;
	}

}
