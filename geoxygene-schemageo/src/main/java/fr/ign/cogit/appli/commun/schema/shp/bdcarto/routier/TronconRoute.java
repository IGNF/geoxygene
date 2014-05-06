package fr.ign.cogit.appli.commun.schema.shp.bdcarto.routier;

import fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public abstract class TronconRoute extends ElementBDCarto {

	/** Renvoie la géométrie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	protected String vocation;
	public String getVocation() {return this.vocation; }
	public void setVocation (String Vocation) {vocation = Vocation; }

	protected String nb_chausse;
	public String getNb_chausse() {return this.nb_chausse; }
	public void setNb_chausse (String Nb_chausse) {nb_chausse = Nb_chausse; }

	protected String nb_voies;
	public String getNb_voies() {return this.nb_voies; }
	public void setNb_voies (String Nb_voies) {nb_voies = Nb_voies; }

	protected String etat;
	public String getEtat() {return this.etat; }
	public void setEtat (String Etat) {etat = Etat; }

	protected String acces;
	public String getAcces() {return this.acces; }
	public void setAcces (String Acces) {acces = Acces; }

	protected String pos_sol;
	public String getPos_sol() {return this.pos_sol; }
	public void setPos_sol (String Pos_sol) {pos_sol = Pos_sol; }

	protected String res_vert;
	public String getRes_vert() {return this.res_vert; }
	public void setRes_vert (String Res_vert) {res_vert = Res_vert; }

	protected String sens;
	public String getSens() {return this.sens; }
	public void setSens (String Sens) {sens = Sens; }

	protected String nb_voies_m;
	public String getNb_voies_m() {return this.nb_voies_m; }
	public void setNb_voies_m (String Nb_voies_m) {nb_voies_m = Nb_voies_m; }

	protected String nb_voies_d;
	public String getNb_voies_d() {return this.nb_voies_d; }
	public void setNb_voies_d (String Nb_voies_d) {nb_voies_d = Nb_voies_d; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

	protected String usage_;
	public String getUsage_() {return this.usage_; }
	public void setUsage_ (String Usage_) {usage_ = Usage_; }

	protected String date_;
	public String getDate_() {return this.date_; }
	public void setDate_ (String Date_) {date_ = Date_; }

	protected String num_route;
	public String getNum_route() {return this.num_route; }
	public void setNum_route (String Num_route) {num_route = Num_route; }

	protected String class_adm;
	public String getClass_adm() {return this.class_adm; }
	public void setClass_adm (String Class_adm) {class_adm = Class_adm; }

	protected String gest_route;
	public String getGest_route() {return this.gest_route; }
	public void setGest_route (String Gest_route) {gest_route = Gest_route; }

}
