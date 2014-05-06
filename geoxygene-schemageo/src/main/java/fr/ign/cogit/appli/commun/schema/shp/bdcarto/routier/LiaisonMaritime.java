package fr.ign.cogit.appli.commun.schema.shp.bdcarto.routier;

import fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public abstract class LiaisonMaritime extends ElementBDCarto {

	/** Renvoie la géométrie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	protected String ouverture;
	public String getOuverture() {return ouverture; }
	public void setOuverture(String S) {ouverture = S; }

	protected String vocation;
	public String getVocation() {return vocation; }
	public void setVocation(String S) {vocation= S; }

	protected double duree;
	public double getDuree() {return duree; }
	public void setDuree(double D) {duree= D; }

	protected String toponyme;
	public String getToponyme() {return toponyme; }
	public void setToponyme(String S) {toponyme = S; }

}
