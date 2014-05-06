package fr.ign.cogit.appli.commun.schema.structure.bdTopo.routier;


import fr.ign.cogit.appli.commun.schema.structure.bdTopo.ElementBDTopo;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public abstract class TronconDeRoute extends ElementBDTopo {

	/** Renvoie la géométrie de l'objet, castée plus précisément qu'avec la méthode getGeom() */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Définit la géométrie de l'objet, castée plus précisément qu'avec la méthode setGeom() */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String classement;
	public String getClassement() {return this.classement; }
	public void setClassement (String Classement) {classement = Classement; }

	protected String dep_gest;
	public String getDep_gest() {return this.dep_gest; }
	public void setDep_gest (String Dep_gest) {dep_gest = Dep_gest; }

	protected double fictif;
	public double getFictif() {return this.fictif; }
	public void setFictif (double Fictif) {fictif = Fictif; }

	protected String franchisst;
	public String getFranchisst() {return this.franchisst; }
	public void setFranchisst (String Franchisst) {franchisst = Franchisst; }

	protected double largeur;
	public double getLargeur() {return this.largeur; }
	public void setLargeur (double Largeur) {largeur = Largeur; }

	protected String nom;
	public String getNom() {return this.nom; }
	public void setNom (String Nom) {nom = Nom; }

	protected double nb_voies;
	public double getNb_voies() {return this.nb_voies; }
	public void setNb_voies (double Nb_voies) {nb_voies = Nb_voies; }

	protected String numero;
	public String getNumero() {return this.numero; }
	public void setNumero (String Numero) {numero = Numero; }

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
