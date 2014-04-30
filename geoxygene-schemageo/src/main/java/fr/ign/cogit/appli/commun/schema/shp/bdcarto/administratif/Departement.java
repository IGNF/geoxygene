package fr.ign.cogit.appli.commun.schema.shp.bdcarto.administratif;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * Classe geographique. Classe generee automatiquement par le chargeur de la
 * plate-forme Oxygene
 */
public abstract class Departement extends
		fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto {

	/**
	 * Renvoie la geometrie de l'objet, castee plus precisement qu'avec la
	 * methode getGeom()
	 */
	public GM_Polygon getGeometrie() {
		return (GM_Polygon) geom;
	}

	/**
	 * Definit la géométrie de l'objet, castée plus précisément qu'avec la
	 * méthode setGeom()
	 */
	public void setGeometrie(GM_Polygon G) {
		this.geom = G;
	}

	protected String nom_dept;

	public String getNom_dept() {
		return this.nom_dept;
	}

	public void setNom_dept(String Nom_dept) {
		nom_dept = Nom_dept;
	}

	protected String insee_dept;

	public String getInsee_dept() {
		return this.insee_dept;
	}

	public void setInsee_dept(String Insee_dept) {
		insee_dept = Insee_dept;
	}

	protected double x_dept;

	public double getX_dept() {
		return this.x_dept;
	}

	public void setX_dept(double X_dept) {
		x_dept = X_dept;
	}

	protected double y_dept;

	public double getY_dept() {
		return this.y_dept;
	}

	public void setY_dept(double Y_dept) {
		y_dept = Y_dept;
	}

	protected String insee_reg;

	public String getInsee_reg() {
		return this.insee_reg;
	}

	public void setInsee_reg(String Insee_reg) {
		insee_reg = Insee_reg;
	}

}
