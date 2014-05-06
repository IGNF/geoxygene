package fr.ign.cogit.appli.commun.schema.shp.bdcarto.toponymes;

import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;


/** Classe geographique. Classe generee automatiquement par le chargeur de la plate-forme Oxygene*/

public abstract class ZoneHabitat extends fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto {

	/** Renvoie la géométrie de l'objet, castée plus précisément qu'avec la méthode getGeom() */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Définit la géométrie de l'objet, castée plus précisément qu'avec la méthode setGeom() */
	public void setGeometrie(GM_Point G) {this.geom = G;}

	protected String insee;
	public String getInsee() {return this.insee; }
	public void setInsee (String Insee) {insee = Insee; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

	protected double id_bdcarto;
	@Override
	public double getId_bdcarto() {return this.id_bdcarto; }
	@Override
	public void setId_bdcarto (double Id_bdcarto) {id_bdcarto = Id_bdcarto; }

	protected String importance;
	public String getImportance() {return this.importance; }
	public void setImportance (String Importance) {importance = Importance; }

}
