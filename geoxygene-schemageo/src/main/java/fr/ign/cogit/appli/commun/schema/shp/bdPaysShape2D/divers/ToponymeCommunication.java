package fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.divers;


import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.ElementBDPays;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public abstract class ToponymeCommunication extends ElementBDPays {

	/** Renvoie la géométrie de l'objet, castée plus précisément qu'avec la méthode getGeom() */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Définit la géométrie de l'objet, castée plus précisément qu'avec la méthode setGeom() */
	public void setGeometrie(GM_Point G) {this.geom = G;}

	protected String nom;
	public String getNom() {return this.nom; }
	public void setNom (String Nom) {nom = Nom; }

	protected String importance;
	public String getImportance() {return this.importance; }
	public void setImportance (String Importance) {importance = Importance; }

	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

}
