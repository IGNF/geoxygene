package fr.ign.cogit.appli.commun.schema.structure.bdTopo.activite_bati;


import fr.ign.cogit.appli.commun.schema.structure.bdTopo.ElementBDTopo;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public abstract class SurfaceActivite extends ElementBDTopo {

	/** Renvoie la géométrie de l'objet, castée plus précisément qu'avec la méthode getGeom() */
	public GM_Polygon getGeometrie() {return (GM_Polygon)geom;}
	/** Définit la géométrie de l'objet, castée plus précisément qu'avec la méthode setGeom() */
	public void setGeometrie(GM_Polygon G) {this.geom = G;}

	protected String categorie;
	public String getCategorie() {return this.categorie; }
	public void setCategorie (String Categorie) {categorie = Categorie; }

}
