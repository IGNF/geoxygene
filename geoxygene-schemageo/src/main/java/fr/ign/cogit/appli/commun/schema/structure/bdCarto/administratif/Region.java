package fr.ign.cogit.appli.commun.schema.structure.bdCarto.administratif;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public abstract class Region extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des geometries multiples (plusieurs troncons) */
	//	  private GM_Curve geometrie = null;
	/** Renvoie le GM_Polygon qui definit la geometrie de self */
	public GM_Polygon getGeometrie() {return (GM_Polygon)geom;}
	/** Definit le GM_Polygon qui definit la geometrie de self */
	public void setGeometrie(GM_Polygon geometrie) {this.geom = geometrie;}

	/////////////// ATTRIBUTS //////////////////

	protected String nomRegion;
	public String getNomRegion() {return this.nomRegion; }
	public void setNomRegion (String NomRegion) {nomRegion = NomRegion; }

	protected String inseeRegion;
	public String getInseeRegion() {return this.inseeRegion; }
	public void setInseeRegion (String InseeRegion) {inseeRegion = InseeRegion; }

	protected GM_Point centroideRegion;
	public GM_Point getCentroideRegion() {return this.centroideRegion; }
	public void setCentroideRegion (GM_Point CentroideRegion) {centroideRegion = CentroideRegion; }

	/*protected double champXRegion;
	public double getChampXRegion() {return this.champXRegion; }
	public void setChampXRegion (double ChampXRegion) {champXRegion = ChampXRegion; }

	protected double champYRegion;
	public double getChampYRegion() {return this.champYRegion; }
	public void setChampYRegion (double ChampYRegion) {champYRegion = ChampYRegion; }*/

	/////////////// RELATIONS //////////////////



	/** Lien bidirectionnel 1-n vers Departement.
	 *  1 objet AAA est en relation avec n objets Departement (n pouvant etre nul).
	 *  1 objet Departement est en relation avec 1 objet AAA au plus.
	 *
	 *  NB: un objet AAA ne doit pas etre en relation plusieurs fois avec le meme objet BBB :
	 *  il est impossible de bien gerer des relations 1-n bidirectionnelles avec doublons.
	 *
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec
	 *  les methodes fournies.
	 * 
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	private List<Departement> departements = new ArrayList<Departement>();

	/** Recupere la liste des objets en relation. */
	public List<Departement> getDepartements() {return departements ; }

	/** Definit la liste des objets en relation, et met a jour la relation inverse. */
	public void setDepartements (List<Departement> L) {
		List <Departement>old = new ArrayList<Departement>(departements);
		Iterator<Departement> it1 = old.iterator();
		while ( it1.hasNext() ) {
			Departement O = it1.next();
			O.setRegion(null);
		}
		Iterator<Departement> it2 = L.iterator();
		while ( it2.hasNext() ) {
			Departement O = it2.next();
			O.setRegion(this);
		}
	}

	/** Recupere le ieme element de la liste des objets en relation. */
	public Departement getDepartement(int i) {return departements.get(i) ; }

	/** Ajoute un objet a la liste des objets en relation, et met a jour la relation inverse. */
	public void addDepartement (Departement O) {
		if ( O == null ) return;
		departements.add(O) ;
		O.setRegion(this) ;
	}

	/** Enleve un element de la liste des objets en relation, et met a jour la relation inverse. */
	public void removeDepartement (Departement O) {
		if ( O == null ) return;
		departements.remove(O) ;
		O.setRegion(null);
	}

	/** Vide la liste des objets en relation, et met a jour la relation inverse. */
	public void emptyDepartements () {
		List<Departement> old = new ArrayList<Departement>(departements);
		Iterator<Departement> it = old.iterator();
		while ( it.hasNext() ) {
			Departement O = it.next();
			O.setRegion(null);
		}
	}


}