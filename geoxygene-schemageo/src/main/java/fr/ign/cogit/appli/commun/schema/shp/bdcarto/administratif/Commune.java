package fr.ign.cogit.appli.commun.schema.shp.bdcarto.administratif;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * Classe geographique. Classe generee automatiquement par le chargeur de la
 * plate-forme Oxygene
 */
public abstract class Commune extends
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

	protected String nom_comm;

	public String getNom_comm() {
		return this.nom_comm;
	}

	public void setNom_comm(String Nom_comm) {
		nom_comm = Nom_comm;
	}

	protected String insee_comm;

	public String getInsee_comm() {
		return this.insee_comm;
	}

	public void setInsee_comm(String Insee_comm) {
		insee_comm = Insee_comm;
	}

	protected String statut;

	public String getStatut() {
		return this.statut;
	}

	public void setStatut(String Statut) {
		statut = Statut;
	}

	protected double x_commune;

	public double getX_commune() {
		return this.x_commune;
	}

	public void setX_commune(double X_commune) {
		x_commune = X_commune;
	}

	protected double y_commune;

	public double getY_commune() {
		return this.y_commune;
	}

	public void setY_commune(double Y_commune) {
		y_commune = Y_commune;
	}

	protected double superficie;

	public double getSuperficie() {
		return this.superficie;
	}

	public void setSuperficie(double Superficie) {
		superficie = Superficie;
	}

	protected double population_;

	public double getPopulation_() {
		return this.population_;
	}

	public void setPopulation_(double Population_) {
		population_ = Population_;
	}

	protected String insee_cant;

	public String getInsee_cant() {
		return this.insee_cant;
	}

	public void setInsee_cant(String Insee_cant) {
		insee_cant = Insee_cant;
	}

	protected String insee_arr;

	public String getInsee_arr() {
		return this.insee_arr;
	}

	public void setInsee_arr(String Insee_arr) {
		insee_arr = Insee_arr;
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

	protected String nom_region;

	public String getNom_region() {
		return this.nom_region;
	}

	public void setNom_region(String Nom_region) {
		nom_region = Nom_region;
	}

	protected String insee_reg;

	public String getInsee_reg() {
		return this.insee_reg;
	}

	public void setInsee_reg(String Insee_reg) {
		insee_reg = Insee_reg;
	}

}
