package fr.ign.cogit.appli.commun.schema.structure.bdCarto.administratif;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;


public abstract class Commune extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des geometries multiples (plusieurs troncons) */
	//	  private GM_Curve geometrie = null;
	/** Renvoie le GM_Polygon qui definit la geometrie de self */
	public GM_Polygon getGeometrie() {return (GM_Polygon)geom;}
	/** Definit le GM_Polygon qui definit la geometrie de self */
	public void setGeometrie(GM_Polygon geometrie) {this.geom = geometrie;}

	/////////////// ATTRIBUTS //////////////////
	protected String nomCommune;
	public String getNomCommune() {return this.nomCommune; }
	public void setNomCommune (String NomCommune) {nomCommune = NomCommune; }

	protected String inseeCommune;
	public String getInseeCommune() {return this.inseeCommune; }
	public void setInseeCommune (String InseeCommune) {inseeCommune = InseeCommune; }

	protected String statut;
	public String getStatut() {return this.statut; }
	public void setStatut (String Statut) {statut = Statut; }

	protected GM_Point centroideCom;
	public GM_Point getCentroideCom() {return this.centroideCom; }
	public void setCentroideCom (GM_Point CentroideCom) {centroideCom = CentroideCom; }

	/*protected double champXCom;
	public double getChampXCom() {return this.champXCom; }
	public void setChampXCom (double ChampXCom) {champXCom = ChampXCom; }

	protected double champYCom;
	public double getChampYCom() {return this.champYCom; }
	public void setChampYCom (double ChampYCom) {champYCom = ChampYCom; }*/

	protected double superficie;
	public double getSuperficie() {return this.superficie; }
	public void setSuperficie (double Superficie) {superficie = Superficie; }

	protected double nbHabitant;
	public double getNbHabitant() {return this.nbHabitant; }
	public void setNbHabitant (double NbHabitant) {nbHabitant = NbHabitant; }



	/////////////// RELATIONS //////////////////

	/** Lien bidirectionnel 1-n vers BBB.
	 *  1 objet AAA est en relation avec n objets BBB (n pouvant etre nul).
	 *  1 objet BBB est en relation avec 1 objet AAA au plus.
	 *
	 *  Les methodes get et set sont utiles pour assurer la bidirection.
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	private Canton canton;
	/** Recupere l'objet en relation. */
	public Canton getCanton() {return canton;  }
	/** Definit l'objet en relation, et met a jour la relation inverse. */
	public void setCanton(Canton O) {
		Canton old = canton;
		canton = O;
		if ( old != null ) old.getCommunes().remove(this);
		if ( O != null) {
			if ( ! O.getCommunes().contains(this) ) O.getCommunes().add(this);
		}
	}

}