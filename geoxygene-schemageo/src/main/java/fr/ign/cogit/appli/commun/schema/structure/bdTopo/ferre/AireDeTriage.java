package fr.ign.cogit.appli.commun.schema.structure.bdTopo.ferre;


import fr.ign.cogit.appli.commun.schema.structure.bdTopo.ElementBDTopo;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public abstract class AireDeTriage extends ElementBDTopo {

	/** Renvoie la géométrie de l'objet, castée plus précisément qu'avec la méthode getGeom() */
	public GM_Polygon getGeometrie() {return (GM_Polygon)geom;}
	/** Definit la géométrie de l'objet, castée plus précisément qu'avec la méthode setGeom() */
	public void setGeometrie(GM_Polygon G) {this.geom = G;}

	protected double z_moyen;
	public double getZ_moyen() {return this.z_moyen; }
	public void setZ_moyen (double Z_moyen) {z_moyen = Z_moyen; }

}
