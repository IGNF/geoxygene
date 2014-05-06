package fr.ign.cogit.appli.commun.schema.structure.bdTopo.activite_bati;


import fr.ign.cogit.appli.commun.schema.structure.bdTopo.ElementBDTopo;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public abstract class PisteAerodrome extends ElementBDTopo {

	/** Renvoie la géométrie de l'objet, castée plus précisément qu'avec la méthode getGeom() */
	public GM_Polygon getGeometrie() {return (GM_Polygon)geom;}
	/** Définit la géométrie de l'objet, castée plus précisément qu'avec la méthode setGeom() */
	public void setGeometrie(GM_Polygon G) {this.geom = G;}

	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected double z_moyen;
	public double getZ_moyen() {return this.z_moyen; }
	public void setZ_moyen (double Z_moyen) {z_moyen = Z_moyen; }

}
