package fr.ign.cogit.appli.commun.schema.shp.bdcarto.routier;

import fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public abstract class CommunicationRestreinte extends ElementBDCarto {

	/** Renvoie la géométrie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom= geometrie;}

	protected double id_noeud;
	public double getId_noeud() {return this.id_noeud; }
	public void setId_noeud (double Id_noeud) {id_noeud = Id_noeud; }

	protected double id_tro_ini;
	public double getId_tro_ini() {return this.id_tro_ini; }
	public void setId_tro_ini (double Id_tro_ini) {id_tro_ini = Id_tro_ini; }

	protected double id_tro_fin;
	public double getId_tro_fin() {return this.id_tro_fin; }
	public void setId_tro_fin (double Id_tro_fin) {id_tro_fin = Id_tro_fin; }

	protected String interdit;
	public String getInterdit() {return this.interdit; }
	public void setInterdit (String Interdit) {interdit = Interdit; }

	protected double rest_poids;
	public double getRest_poids() {return this.rest_poids; }
	public void setRest_poids (double Rest_poids) {rest_poids = Rest_poids; }

	protected double rest_haut;
	public double getRest_haut() {return this.rest_haut; }
	public void setRest_haut (double Rest_haut) {rest_haut = Rest_haut; }

}
