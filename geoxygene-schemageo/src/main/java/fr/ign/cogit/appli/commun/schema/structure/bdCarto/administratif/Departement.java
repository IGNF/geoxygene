package fr.ign.cogit.appli.commun.schema.structure.bdCarto.administratif;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public abstract class Departement extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des géométries multiples (plusieurs tronçons) */
	//	  private GM_Curve geometrie = null;
	/** Renvoie le GM_Polygon qui définit la géométrie de self */
	public GM_Polygon getGeometrie() {return (GM_Polygon)geom;}
	/** Définit le GM_Polygon qui définit la géométrie de self */
	public void setGeometrie(GM_Polygon geometrie) {this.geom = geometrie;}

	/////////////// ATTRIBUTS //////////////////

	protected String nomDepartement;
	public String getNomDepartement() {return this.nomDepartement; }
	public void setNomDepartement (String NomDepartement) {nomDepartement = NomDepartement; }

	protected String inseeDepartement;
	public String getInseeDepartement() {return this.inseeDepartement; }
	public void setInseeDepartement (String InseeDepartement) {inseeDepartement = InseeDepartement; }

	protected GM_Point centroideDep;
	public GM_Point getCentroideDep() {return this.centroideDep; }
	public void setCentroideDep (GM_Point CentroideDep) {centroideDep = CentroideDep; }

	/*protected double champXDep;
	public double getChampXDep() {return this.champXDep; }
	public void setChampXDep (double ChampXDep) {champXDep = ChampXDep; }

	protected double champYDep;
	public double getChampYDep() {return this.champYDep; }
	public void setChampYDep (double ChampYDep) {champYDep = ChampYDep; }*/


	/////////////// RELATIONS //////////////////


	/** Lien bidirectionnel 1-n vers Arrondissement.
	 *  1 objet Departement est en relation avec n objets Arrondissement (n pouvant etre nul).
	 *  1 objet Arrondissement est en relation avec 1 objet Departement au plus.
	 *
	 *  NB: un objet Departement ne doit pas être en relation plusieurs fois avec le même objet Arrondissement :
	 *  il est impossible de bien gérer des relations 1-n bidirectionnelles avec doublons.
	 *
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec
	 *  les methodes fournies.
	 * 
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	private List<Arrondissement> arrondissements = new ArrayList<Arrondissement>();

	/** Récupère la liste des objets en relation. */
	public List<Arrondissement> getArrondissements() {return arrondissements ; }

	/** Définit la liste des objets en relation, et met à jour la relation inverse. */
	public void setArrondissements (List<Arrondissement> L) {
		List <Arrondissement>old = new ArrayList<Arrondissement>(arrondissements);
		Iterator<Arrondissement> it1 = old.iterator();
		while ( it1.hasNext() ) {
			Arrondissement O = it1.next();
			O.setDepartement(null);
		}
		Iterator<Arrondissement> it2 = L.iterator();
		while ( it2.hasNext() ) {
			Arrondissement O = it2.next();
			O.setDepartement(this);
		}
	}

	/** Récupère le ième élément de la liste des objets en relation. */
	public Arrondissement getArrondissement(int i) {return arrondissements.get(i) ; }

	/** Ajoute un objet à la liste des objets en relation, et met à jour la relation inverse. */
	public void addArrondissement (Arrondissement O) {
		if ( O == null ) return;
		arrondissements.add(O) ;
		O.setDepartement(this) ;
	}

	/** Enlève un élément de la liste des objets en relation, et met à jour la relation inverse. */
	public void removeArrondissement (Arrondissement O) {
		if ( O == null ) return;
		arrondissements.remove(O) ;
		O.setDepartement(null);
	}

	/** Vide la liste des objets en relation, et met à jour la relation inverse. */
	public void emptyArrondissements () {
		List<Arrondissement> old = new ArrayList<Arrondissement>(arrondissements);
		Iterator<Arrondissement> it = old.iterator();
		while ( it.hasNext() ) {
			Arrondissement O = it.next();
			O.setDepartement(null);
		}
	}



	/** Lien bidirectionnel 1-n vers Departement.
	 *  1 objet Region est en relation avec n objets Departement (n pouvant etre nul).
	 *  1 objet Departement est en relation avec 1 objet Region au plus.
	 *
	 *  Les méthodes get et set sont utiles pour assurer la bidirection.
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */

	private Region region;

	/** Récupère l'objet en relation. */

	public Region getRegion() {return region;  }

	/** Définit l'objet en relation, et met à jour la relation inverse. */
	public void setRegion(Region O) {
		Region old = region;
		region = O;
		if ( old != null ) old.getDepartements().remove(this);
		if ( O != null) {
			if ( ! O.getDepartements().contains(this) ) O.getDepartements().add(this);
		}
	}

}