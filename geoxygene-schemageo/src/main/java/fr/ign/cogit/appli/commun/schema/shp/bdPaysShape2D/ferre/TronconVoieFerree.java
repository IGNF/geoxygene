package fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.ferre;


import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.ElementBDPays;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public abstract class TronconVoieFerree extends ElementBDPays {

	/** Renvoie la géométrie de l'objet, castée plus précisément qu'avec la méthode getGeom() */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Définit la géométrie de l'objet, castée plus précisément qu'avec la méthode setGeom() */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String electrifie;
	public String getElectrifie() {return this.electrifie; }
	public void setElectrifie (String Electrifie) {electrifie = Electrifie; }

	protected String franchisst;
	public String getFranchisst() {return this.franchisst; }
	public void setFranchisst (String Franchisst) {franchisst = Franchisst; }

	protected String largeur;
	public String getLargeur() {return this.largeur; }
	public void setLargeur (String Largeur) {largeur = Largeur; }

	protected double nb_voies;
	public double getNb_voies() {return this.nb_voies; }
	public void setNb_voies (double Nb_voies) {nb_voies = Nb_voies; }

	protected double posit_sol;
	public double getPosit_sol() {return this.posit_sol; }
	public void setPosit_sol (double Posit_sol) {posit_sol = Posit_sol; }

	protected double z_ini;
	public double getZ_ini() {return this.z_ini; }
	public void setZ_ini (double Z_ini) {z_ini = Z_ini; }

	protected double z_fin;
	public double getZ_fin() {return this.z_fin; }
	public void setZ_fin (double Z_fin) {z_fin = Z_fin; }

}
