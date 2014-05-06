package fr.ign.cogit.appli.commun.schema.structure.georoute.routier;

import fr.ign.cogit.appli.commun.schema.structure.georoute.ElementGeoroute;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public abstract class CarrefourComplexe extends ElementGeoroute {


	//////////////// GEOMETRIE //////////////////
	//    private GM_Polygon geometrie = null;
	/** Renvoie le GM_Polygon qui définit la géométrie de self */
	public GM_Polygon getGeometrie() {return (GM_Polygon)this.geom;}
	/** Définit le GM_Polygon qui définit la géométrie de self */
	public void setGeometrie(GM_Polygon geometrie) {this.geom = geometrie;}


	public String nature;
	public String getNature() {return nature;}
	public void setNature(String nature) {this.nature = nature;}

	public String nom;
	public String getNom() {return nom;}
	public void setNom(String nom) {this.nom = nom;}


}
