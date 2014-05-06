package fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.hydrographie;


import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.ElementBDPays;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public abstract class TronconDeCoursDEau extends ElementBDPays {

	/** Renvoie la géométrie de l'objet, castée plus précisément qu'avec la méthode getGeom() */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Définit la géométrie de l'objet, castée plus précisément qu'avec la méthode setGeom() */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	protected double artif;
	public double getArtif() {return this.artif; }
	public void setArtif (double Artif) {artif = Artif; }

	protected double fictif;
	public double getFictif() {return this.fictif; }
	public void setFictif (double Fictif) {fictif = Fictif; }

	protected String franchisst;
	public String getFranchisst() {return this.franchisst; }
	public void setFranchisst (String Franchisst) {franchisst = Franchisst; }

	protected String nom;
	public String getNom() {return this.nom; }
	public void setNom (String Nom) {nom = Nom; }

	protected double posit_sol;
	public double getPosit_sol() {return this.posit_sol; }
	public void setPosit_sol (double Posit_sol) {posit_sol = Posit_sol; }

	protected String regime;
	public String getRegime() {return this.regime; }
	public void setRegime (String Regime) {regime = Regime; }

	protected double z_ini;
	public double getZ_ini() {return this.z_ini; }
	public void setZ_ini (double Z_ini) {z_ini = Z_ini; }

	protected double z_fin;
	public double getZ_fin() {return this.z_fin; }
	public void setZ_fin (double Z_fin) {z_fin = Z_fin; }

}
