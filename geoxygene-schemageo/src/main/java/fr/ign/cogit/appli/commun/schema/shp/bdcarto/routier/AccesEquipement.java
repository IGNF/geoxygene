package fr.ign.cogit.appli.commun.schema.shp.bdcarto.routier;

import fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;


public abstract class AccesEquipement extends ElementBDCarto {

	/** Renvoie la géométrie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	protected double id_equipmt;
	public double getId_equipmt() {return this.id_equipmt; }
	public void setId_equipmt (double Id_equipmt) {id_equipmt = Id_equipmt; }

	protected double id_troncon;
	public double getId_troncon() {return this.id_troncon; }
	public void setId_troncon (double Id_troncon) {id_troncon = Id_troncon; }

	protected String cote;
	public String getCote() {return this.cote; }
	public void setCote (String Cote) {cote = Cote; }

}
