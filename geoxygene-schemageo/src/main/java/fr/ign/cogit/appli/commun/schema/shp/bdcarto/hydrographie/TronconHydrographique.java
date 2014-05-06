package fr.ign.cogit.appli.commun.schema.shp.bdcarto.hydrographie;

import fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public abstract class TronconHydrographique extends ElementBDCarto {

	/** Renvoie la géométrie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}


	protected String largeur;
	public String getLargeur() {return this.largeur; }
	public void setLargeur (String Largeur) {largeur = Largeur; }

	protected String etat;
	public String getEtat() {return this.etat; }
	public void setEtat (String Etat) {etat = Etat; }

	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String navigable;
	public String getNavigable() {return this.navigable; }
	public void setNavigable (String Navigable) {navigable = Navigable; }

	protected String pos_sol;
	public String getPos_sol() {return this.pos_sol; }
	public void setPos_sol (String Pos_sol) {pos_sol = Pos_sol; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

	protected String sens;
	public String getSens() {return this.sens; }
	public void setSens (String Sens) {sens = Sens; }

	protected String top_c_eau;
	public String getTop_c_eau() {return this.top_c_eau; }
	public void setTop_c_eau (String Top_c_eau) {top_c_eau = Top_c_eau; }



}
