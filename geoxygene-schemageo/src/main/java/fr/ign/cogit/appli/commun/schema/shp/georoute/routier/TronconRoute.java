package fr.ign.cogit.appli.commun.schema.shp.georoute.routier;

import fr.ign.cogit.appli.commun.schema.shp.georoute.ElementGeoroute;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public class TronconRoute extends ElementGeoroute {

	//////////////// GEOMETRIE //////////////////
	//     private GM_LineString geometrie = null;
	/** Renvoie la géométrie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	protected String cl_fonc;
	public String getCl_fonc() {return this.cl_fonc; }
	public void setCl_fonc (String Cl_fonc) {cl_fonc = Cl_fonc; }

	protected String cl_physiq;
	public String getCl_physiq() {return this.cl_physiq; }
	public void setCl_physiq (String Cl_physiq) {cl_physiq = Cl_physiq; }

	protected String nb_voies;
	public String getNb_voies() {return this.nb_voies; }
	public void setNb_voies (String Nb_voies) {nb_voies = Nb_voies; }

	protected String rest_acces;
	public String getRest_acces() {return this.rest_acces; }
	public void setRest_acces (String Rest_acces) {rest_acces = Rest_acces; }

	protected String pos_sol;
	public String getPos_sol() {return this.pos_sol; }
	public void setPos_sol (String Pos_sol) {pos_sol = Pos_sol; }

	protected String res_vert;
	public String getRes_vert() {return this.res_vert; }
	public void setRes_vert (String Res_vert) {res_vert = Res_vert; }

	protected String sens;
	public String getSens() {return this.sens; }
	public void setSens (String Sens) {sens = Sens; }

	protected String limite;
	public String getLimite() {return this.limite; }
	public void setLimite (String Limite) {limite = Limite; }

	protected String niveau;
	public String getNiveau() {return this.niveau; }
	public void setNiveau (String Niveau) {niveau = Niveau; }

	protected String rest_poids;
	public String getRest_poids() {return this.rest_poids; }
	public void setRest_poids (String Rest_poids) {rest_poids = Rest_poids; }

	protected String rest_haut;
	public String getRest_haut() {return this.rest_haut; }
	public void setRest_haut (String Rest_haut) {rest_haut = Rest_haut; }

	protected String rest_long;
	public String getRest_long() {return this.rest_long; }
	public void setRest_long (String Rest_long) {rest_long = Rest_long; }

	protected String rest_larg;
	public String getRest_larg() {return this.rest_larg; }
	public void setRest_larg (String Rest_larg) {rest_larg = Rest_larg; }

	protected String mat_danger;
	public String getMat_danger() {return this.mat_danger; }
	public void setMat_danger (String Mat_danger) {mat_danger = Mat_danger; }

	protected String voie_bus;
	public String getVoie_bus() {return this.voie_bus; }
	public void setVoie_bus (String Voie_bus) {voie_bus = Voie_bus; }

	protected String rest_march;
	public String getRest_march() {return this.rest_march; }
	public void setRest_march (String Rest_march) {rest_march = Rest_march; }

	protected String livraison;
	public String getLivraison() {return this.livraison; }
	public void setLivraison (String Livraison) {livraison = Livraison; }

	protected String circul_int;
	public String getCircul_int() {return this.circul_int; }
	public void setCircul_int (String Circul_int) {circul_int = Circul_int; }

	protected String nom_rue_d;
	public String getNom_rue_d() {return this.nom_rue_d; }
	public void setNom_rue_d (String Nom_rue_d) {nom_rue_d = Nom_rue_d; }

	protected String nom_rue_g;
	public String getNom_rue_g() {return this.nom_rue_g; }
	public void setNom_rue_g (String Nom_rue_g) {nom_rue_g = Nom_rue_g; }

	protected String born_deb_d;
	public String getBorn_deb_d() {return this.born_deb_d; }
	public void setBorn_deb_d (String Born_deb_d) {born_deb_d = Born_deb_d; }

	protected String born_deb_g;
	public String getBorn_deb_g() {return this.born_deb_g; }
	public void setBorn_deb_g (String Born_deb_g) {born_deb_g = Born_deb_g; }

	protected String born_fin_d;
	public String getBorn_fin_d() {return this.born_fin_d; }
	public void setBorn_fin_d (String Born_fin_d) {born_fin_d = Born_fin_d; }

	protected String born_fin_g;
	public String getBorn_fin_g() {return this.born_fin_g; }
	public void setBorn_fin_g (String Born_fin_g) {born_fin_g = Born_fin_g; }

	protected String type_adr;
	public String getType_adr() {return this.type_adr; }
	public void setType_adr (String Type_adr) {type_adr = Type_adr; }

	protected String insee_comd;
	public String getInsee_comd() {return this.insee_comd; }
	public void setInsee_comd (String Insee_comd) {insee_comd = Insee_comd; }

	protected String insee_comg;
	public String getInsee_comg() {return this.insee_comg; }
	public void setInsee_comg (String Insee_comg) {insee_comg = Insee_comg; }

	protected String num_route;
	public String getNum_route() {return this.num_route; }
	public void setNum_route (String Num_route) {num_route = Num_route; }

	protected String nom_iti;
	public String getNom_iti() {return this.nom_iti; }
	public void setNom_iti (String Nom_iti) {nom_iti = Nom_iti; }

	protected String mise_serv;
	public String getMise_serv() {return this.mise_serv; }
	public void setMise_serv (String Mise_serv) {mise_serv = Mise_serv; }



}
