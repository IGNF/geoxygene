package fr.ign.cogit.appli.commun.schema.shp.bdcarto.routier;

import fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public abstract class NoeudRoutier extends ElementBDCarto {

	/** Renvoie la géométrie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}

	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

	protected double cote;
	public double getCote() {return this.cote; }
	public void setCote (double Cote) {cote = Cote; }

	protected String num_carref;
	public String getNum_carref() {return this.num_carref; }
	public void setNum_carref (String Num_carref) {num_carref = Num_carref; }

	protected String nat_carref;
	public String getNat_carref() {return this.nat_carref; }
	public void setNat_carref (String Nat_carref) {nat_carref = Nat_carref; }

	protected String top_carref;
	public String getTop_carref() {return this.top_carref; }
	public void setTop_carref (String Top_carref) {top_carref = Top_carref; }

}
