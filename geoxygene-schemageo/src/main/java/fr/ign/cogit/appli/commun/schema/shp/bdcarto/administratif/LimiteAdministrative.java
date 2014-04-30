package fr.ign.cogit.appli.commun.schema.shp.bdcarto.administratif;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * Classe geographique. Classe generee automatiquement par le chargeur de la
 * plate-forme Oxygene
 */
public abstract class LimiteAdministrative extends
		fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto {

	/**
	 * Renvoie la géométrie de l'objet, castée plus précisément qu'avec la
	 * méthode getGeom()
	 */
	public GM_LineString getGeometrie() {
		return (GM_LineString) geom;
	}

	/**
	 * Définit la géométrie de l'objet, castée plus précisément qu'avec la
	 * méthode setGeom()
	 */
	public void setGeometrie(GM_LineString G) {
		this.geom = G;
	}

	protected String nature;

	public String getNature() {
		return this.nature;
	}

	public void setNature(String Nature) {
		nature = Nature;
	}

	protected String precision_;

	public String getPrecision_() {
		return this.precision_;
	}

	public void setPrecision_(String Precision_) {
		precision_ = Precision_;
	}

}
