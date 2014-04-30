package fr.ign.cogit.appli.commun.schema.structure.bdCarto.equipements;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;


public abstract class EnceinteMilitaire extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des géométries multiples (plusieurs tronçons) */
	//	  private GM_Curve geometrie = null;
	/** Renvoie le GM_Polygon qui définit la géométrie de self */
	public GM_Polygon getGeometrie() {return (GM_Polygon)geom;}
	/** Définit le GM_Polygon qui définit la géométrie de self */
	public void setGeometrie(GM_Polygon geometrie) {this.geom = geometrie;}

	/////////////// ATTRIBUTS //////////////////
	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String toponyme;
	public String getToponyme() { return toponyme; }
	public void setToponyme(String toponyme) { this.toponyme = toponyme; }

}