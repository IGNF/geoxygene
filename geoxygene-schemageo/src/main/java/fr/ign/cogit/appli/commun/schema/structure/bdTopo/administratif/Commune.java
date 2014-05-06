package fr.ign.cogit.appli.commun.schema.structure.bdTopo.administratif;


import fr.ign.cogit.appli.commun.schema.structure.bdTopo.ElementBDTopo;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public abstract class Commune extends ElementBDTopo {

	/** Renvoie la géométrie de l'objet, castée plus précisément qu'avec la méthode getGeom() */
	public GM_Polygon getGeometrie() {return (GM_Polygon)geom;}
	/** Définit la géométrie de l'objet, castée plus précisément qu'avec la méthode setGeom() */
	public void setGeometrie(GM_Polygon G) {this.geom = G;}

	protected String nom;
	public String getNom() {return this.nom; }
	public void setNom (String Nom) {nom = Nom; }

	protected String code_insee;
	public String getCode_insee() {return this.code_insee; }
	public void setCode_insee (String Code_insee) {code_insee = Code_insee; }

	protected String statut;
	public String getStatut() {return this.statut; }
	public void setStatut (String Statut) {statut = Statut; }

	protected String canton;
	public String getCanton() {return this.canton; }
	public void setCanton (String Canton) {canton = Canton; }

	protected String arrond;
	public String getArrond() {return this.arrond; }
	public void setArrond (String Arrond) {arrond = Arrond; }

	protected String depart;
	public String getDepart() {return this.depart; }
	public void setDepart (String Depart) {depart = Depart; }

	protected String region;
	public String getRegion() {return this.region; }
	public void setRegion (String Region) {region = Region; }

	protected double population_;
	public double getPopulation_() {return this.population_; }
	public void setPopulation_ (double Population_) {population_ = Population_; }

	protected String multi_cant;
	public String getMulti_cant() {return this.multi_cant; }
	public void setMulti_cant (String Multi_cant) {multi_cant = Multi_cant; }

}
