package fr.ign.cogit.appli.commun.schema.shp.bdcarto.ferre;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;


/** Classe geographique. Classe generee automatiquement par le chargeur de la plate-forme Oxygene*/

public abstract class TronconVoieFerree extends fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto {

	/** Renvoie la géométrie de l'objet, castée plus précisément qu'avec la méthode getGeom() */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Définit la géométrie de l'objet, castée plus précisément qu'avec la méthode setGeom() */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String energie;
	public String getEnergie() {return this.energie; }
	public void setEnergie (String Energie) {energie = Energie; }

	protected String nb_voies;
	public String getNb_voies() {return this.nb_voies; }
	public void setNb_voies (String Nb_voies) {nb_voies = Nb_voies; }

	protected String largeur;
	public String getLargeur() {return this.largeur; }
	public void setLargeur (String Largeur) {largeur = Largeur; }

	protected String pos_sol;
	public String getPos_sol() {return this.pos_sol; }
	public void setPos_sol (String Pos_sol) {pos_sol = Pos_sol; }

	protected String classement;
	public String getClassement() {return this.classement; }
	public void setClassement (String Classement) {classement = Classement; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

	protected String touristiq;
	public String getTouristiq() {return this.touristiq; }
	public void setTouristiq (String Touristiq) {touristiq = Touristiq; }

	protected String topo_ligne;
	public String getTopo_ligne() {return this.topo_ligne; }
	public void setTopo_ligne (String Topo_ligne) {topo_ligne = Topo_ligne; }

}
