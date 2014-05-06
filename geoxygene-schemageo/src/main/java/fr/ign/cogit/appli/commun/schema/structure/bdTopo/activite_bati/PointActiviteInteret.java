package fr.ign.cogit.appli.commun.schema.structure.bdTopo.activite_bati;


import fr.ign.cogit.appli.commun.schema.structure.bdTopo.ElementBDTopo;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public abstract class PointActiviteInteret extends ElementBDTopo {

	/** Renvoie la géométrie de l'objet, castée plus précisément qu'avec la méthode getGeom() */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Définit la géométrie de l'objet, castée plus précisément qu'avec la méthode setGeom() */
	public void setGeometrie(GM_Point G) {this.geom = G;}

	protected String categorie;
	public String getCategorie() {return this.categorie; }
	public void setCategorie (String Categorie) {categorie = Categorie; }

	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }


}
