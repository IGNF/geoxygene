package fr.ign.cogit.appli.commun.schema.structure.bdCarto.equipements;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;


public abstract class TransportParCable extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des géométries multiples (plusieurs tronçons) */
	//	  private GM_Curve geometrie = null;
	/** Renvoie le GM_LineString qui définit la géométrie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Définit le GM_LineString qui définit la géométrie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	/////////////// ATTRIBUTS //////////////////
	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

}