package fr.ign.cogit.appli.commun.schema.shp.bdcarto.routier;

import fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public abstract class DebutSection extends ElementBDCarto {

	/** Renvoie la géométrie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	protected String gestion;
	public String getGestion() {return this.gestion; }
	public void setGestion (String Gestion) {gestion = Gestion; }

	protected String sens;
	public String getSens() {return this.sens; }
	public void setSens (String Sens) {sens = Sens; }

	protected double id_troncon;
	public double getId_troncon() {return this.id_troncon; }
	public void setId_troncon (double Id_troncon) {id_troncon = Id_troncon; }

	protected double id_sec_sui;
	public double getId_sec_sui() {return this.id_sec_sui; }
	public void setId_sec_sui (double Id_sec_sui) {id_sec_sui = Id_sec_sui; }

}
