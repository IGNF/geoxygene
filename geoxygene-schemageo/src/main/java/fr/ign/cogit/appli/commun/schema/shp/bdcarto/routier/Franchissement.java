package fr.ign.cogit.appli.commun.schema.shp.bdcarto.routier;

import fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public abstract class Franchissement extends ElementBDCarto {

	/** Renvoie la géométrie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_Point G) {this.geom = G;}

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

	protected double cote;
	public double getCote() {return this.cote; }
	public void setCote (double Cote) {cote = Cote; }

	protected String type_tron;
	public String getType_tron() {return this.type_tron; }
	public void setType_tron (String Type_tron) {type_tron = Type_tron; }

	protected double id_troncon;
	public double getId_troncon() {return this.id_troncon; }
	public void setId_troncon (double Id_troncon) {id_troncon = Id_troncon; }

	protected String mode_;
	public String getMode_() {return this.mode_; }
	public void setMode_ (String Mode_) {mode_ = Mode_; }

	protected double niveau;
	public double getNiveau() {return this.niveau; }
	public void setNiveau (double Niveau) {niveau = Niveau; }



}
