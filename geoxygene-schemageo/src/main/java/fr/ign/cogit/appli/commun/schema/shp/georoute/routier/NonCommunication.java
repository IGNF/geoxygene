package fr.ign.cogit.appli.commun.schema.shp.georoute.routier;

import fr.ign.cogit.appli.commun.schema.shp.georoute.ElementGeoroute;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public class NonCommunication extends ElementGeoroute {

	//     private GM_Point geometrie = null;
	/** Renvoie la géométrie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}

	protected double tr_entree;
	public double getTr_entree() {return this.tr_entree; }
	public void setTr_entree (double Tr_entree) {tr_entree = Tr_entree; }

	protected double tr_sortie;
	public double getTr_sortie() {return this.tr_sortie; }
	public void setTr_sortie (double Tr_sortie) {tr_sortie = Tr_sortie; }

	protected double noeud;
	public double getNoeud() {return this.noeud; }
	public void setNoeud (double Noeud) {noeud = Noeud; }


}
