package fr.ign.cogit.appli.commun.schema.structure.bdCarto.hydro;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/** Concerne les plans d'eau, glaciers, baies, hauts fonds, marais... */
public abstract class ToponymeHydrographieSurfacique extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	//    private GM_Point geometrie = null;
	/** Renvoie le GM_Point qui définit la géométrie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Définit le GM_Point qui définit la géométrie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}


	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String I) {nature = I; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

	protected double cote;
	public double getCote() {return this.cote; }
	public void setCote (double Cote) {cote = Cote; }

	// NB : impossible à remplir à partir des tables shape
	protected int classification;
	public int getClassification() {return this.classification; }
	public void setClassification(int S) {classification = S; }
}
