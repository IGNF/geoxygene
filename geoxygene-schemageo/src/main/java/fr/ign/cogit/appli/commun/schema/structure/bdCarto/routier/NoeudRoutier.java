package fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Noeud du reseau routier.
 * <BR> <STRONG> Type </STRONG>:
 *      Objet simple
 * <BR> <STRONG> Localisation </STRONG>:
 *      Ponctuelle.
 * <BR> <STRONG> Liens </STRONG>:
 *      //Compose (lien inverse) "Route numerotee ou nommee"
 * <BR> <STRONG> Definition </STRONG>:
 *      Un noeud du reseau routier correspond a une extremite de troncon de route ou de liaison maritime ;
 *      il traduit une modification des conditions de circulation : ce peut etre une intersection, un obstacle ou un changement de valeur d'attribut.
 *      Il n'y a pas a proprement parler de selection des noeuds routiers :
 *      elle est deduite de celle des troncons de route et des liaisons maritimes et bacs. Les carrefours amenages d'une extension superieure a 100
 *      metres et les ronds-points d'un diametre superieur a 50 metres sont des noeuds avec une nature specifique ;
 *      si leur extension est inferieure ils sont consideres comme des carrefours simples.
 *      D'autre part, si leur extension est superieure a 100 metres, ils sont egalement detailles en plusieurs carrefours simples au meme titre que les
 *      echangeurs (ils ont alors 2 descriptions : une generaliste et une detaillee).
 * <BR> <STRONG> Compatibilite entre attributs </STRONG> :
 * <UL>
 * <LI> compatiblite sur les toponymes ? </LI>
 * </UL>
 * 
 * @author braun
 */

public abstract class NoeudRoutier extends ElementBDCarto {

	//     protected GM_Point geometrie = null;
	/** Renvoie la geometrie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Definit la geometrie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}

	/** Type de noeud.
	 * <BR> <STRONG> Definition </STRONG>:
	 *      Type de noeud.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaine de caracteres.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     11- carrefour simple, cul de sac, carrefour amenage d'une extension inferieure a 100 metres, ou rond-point d'un diamere inferieur a 50 metres </LI>
	 * <LI>     12- intersection representant un carrefour amenage d'une extension superieure a 100 metres sans toboggan ni passage inferieur </LI>
	 * <LI>     14- intersection representant un rond-point (giratoire) d'un diametre superieur a 100 metres d'axe a axe </LI>
	 * <LI>     15- carrefour amenage avec passage inferieur ou toboggan quelle que soit son extension </LI>
	 * <LI>     16- intersection representant un echangeur complet </LI>
	 * <LI>     17- intersection representant un echangeur partiel </LI>
	 * <LI>     18- rond-point (giratoire) d'un diametre compris entre 50 et 100 metres </LI>
	 * <LI>     22- embarcadere de bac ou liaison maritime </LI>
	 * <LI>     23- embarcadere de liaison maritime situe hors du territoire BDCarto, positionne de facon fictive en limite de ce territoire </LI>
	 * <LI>     31- barriere interdisant la communication libre entre deux portions de route, regulierement ou irregulierement entretenue </LI>
	 * <LI>     32- barriere de douane (hors CEE) </LI>
	 * <LI>     40- changement d'attribut </LI>
	 * <LI>     45- noeud cree par l'intersection entre une route nationale et la limite de departement quand il n'existe pas de noeud au lieu de l'intersection ou noeud cree pour decouper des grands troncons de route (ex : autoroute).</LI>
	 * <LI>     50- noeud de communication restreinte (voir B-rs-4) : noeud cree quand il n'existe pas de noeud correspondant aux valeurs ci-dessus au lieu de la restriction.</LI>
	 * </UL>
	 */
	public String type;
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }


	/** Toponyme.
	 * <BR> <STRONG> Definition </STRONG>:
	 * Un noeud du reseau routier peut porter un toponyme, si l'un au moins des troncons connectes appartient au reseau classe, et si le noeud appartient a l'un des types suivants :
	 * carrefour simple,
	 * carrefour amenage d'une extension superieure a 100 m,
	 * rond-point d'un diametre superieur a 100 m,
	 * rond-point,
	 * carrefour amenage avec passage inferieur ou tobbogan,
	 * echangeur complet ou partiel.
	 * Un noeud composant un carrefour complexe ne porte generalement pas de toponyme.
	 * Le toponyme est compose de trois parties pouvant ne porter aucune valeur (n'existe pas dans les cas ci-dessus et sans objet sinon) :
	 * un terme generique ou une designation, texte d'au plus 40 caracteres.
	 * un article, texte d'au plus cinq caracteres ;
	 * un element specifique, texte d'au plus 80 caracteres ;
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaine de caracteres.
	 */

	public String toponyme;
	public String getToponyme() { return toponyme; }
	public void setToponyme(String toponyme) { this.toponyme = toponyme; }


	/** Cote.
	 * <BR> <STRONG> Definition </STRONG>:
	 * Nombre entier donnant l'altitude en metres. Cet attribut peut ne porter aucune valeur.
	 * <BR> <STRONG> Type </STRONG>:
	 *      entier > 0.
	 *      NB : 9999 correspond a une cote inconnue.
	 */
	public int cote;
	public int getCote() { return cote; }
	public void setCote(int I) { cote = I;  }


	/** Liste (non ordonnee) des arcs sortants de self
	 * <BR> <STRONG> Definition </STRONG>:
	 *  1 objet Noeud est en relation "sortants" avec n objets TronconRoutier.
	 *  1 objet TronconRoutier est en relation "ini" avec 1 objet Noeud.
	 *
	 *  Les methodes get (sans indice) et set sont necessaires au mapping.
	 *  Les autres methodes sont la seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 *      Relation topologique participant a la gestion de la logique de parcours du reseau routier :
	 *      Elle precise lengthnoeud routier initial d'un troncon de route.
	 * <BR> <STRONG> Type des elements de la liste </STRONG>:
	 *      TronconRoute.
	 * <BR> <STRONG> Cardinalite de la relation </STRONG>:
	 *      1 troncon a 0 ou 1 noeud initial NoeudIni.
	 *      1 noeud a 0 ou n troncons sortants.
	 */
	protected List<TronconRoute> sortants = new ArrayList<TronconRoute>();

	/** Recupere la liste des arcs sortants. */
	public List<TronconRoute> getSortants() {return sortants ; }
	/** Definit la liste des arcs sortants, et met a jour la relation inverse Ini. */
	public void setSortants(List<TronconRoute> L) {
		List<TronconRoute> old = new ArrayList<TronconRoute>(sortants);
		Iterator<TronconRoute> it1 = old.iterator();
		while ( it1.hasNext() ) {
			TronconRoute O = it1.next();
			O.setNoeudIni(null);
		}
		Iterator<TronconRoute> it2 = L.iterator();
		while ( it2.hasNext() ) {
			TronconRoute O = it2.next();
			O.setNoeudIni(this);
		}
	}
	/** Recupere le ieme element de la liste des arcs sortants. */
	public TronconRoute getSortant(int i) {return sortants.get(i) ; }
	/** Ajoute un objet a la liste des arcs sortants, et met a jour la relation inverse Ini. */
	public void addSortant(TronconRoute O) {
		if ( O == null ) return;
		sortants.add(O) ;
		O.setNoeudIni(this) ;
	}
	/** Enleve un element de la liste des arcs sortants, et met a jour la relation inverse Ini. */
	public void removeSortant(TronconRoute O) {
		if ( O == null ) return;
		sortants.remove(O) ;
		O.setNoeudIni(null);
	}
	/** Vide la liste des arcs sortants, et met a jour la relation inverse Ini. */
	public void emptySortants() {
		List <TronconRoute>old = new ArrayList<TronconRoute>(sortants);
		Iterator<TronconRoute> it = old.iterator();
		while ( it.hasNext() ) {
			TronconRoute O = it.next();
			O.setNoeudIni(null);
		}
	}


	/** Liste (non ordonnee) des arcs entrants de self
	 * <BR> <STRONG> Definition </STRONG>:
	 *  1 objet Noeud est en relation "entrants" avec n objets TronconRoutier.
	 *  1 objet TronconRoutier est en relation "fin" avec 1 objet Noeud.
	 *
	 *  Les methodes get (sans indice) et set sont necessaires au mapping.
	 *  Les autres methodes sont la seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 *      Relation topologique participant a la gestion de la logique de parcours du reseau routier :
	 *      Elle precise lengthnoeud routier initial d'un troncon de route.
	 * <BR> <STRONG> Type des éléments de la liste </STRONG>:
	 *      TronconRoute.
	 * <BR> <STRONG> Cardinalite de la relation </STRONG>:
	 *      1 troncon a 0 ou 1 noeud initial NoeudFin.
	 *      1 noeud a 0 ou n troncons entrants.
	 */
	protected List<TronconRoute> entrants = new ArrayList<TronconRoute>();

	/** Recupere la liste des arcs entrants. */
	public List<TronconRoute> getEntrants() {return entrants ; }
	/** Definit la liste des arcs entrants, et met a jour la relation inverse NoeudFin. */
	public void setEntrants(List <TronconRoute>L) {
		List<TronconRoute> old = new ArrayList<TronconRoute>(entrants);
		Iterator<TronconRoute> it1 = old.iterator();
		while ( it1.hasNext() ) {
			TronconRoute O = it1.next();
			O.setNoeudFin(null);
		}
		Iterator<TronconRoute> it2 = L.iterator();
		while ( it2.hasNext() ) {
			TronconRoute O = it2.next();
			O.setNoeudFin(this);
		}
	}
	/** Recupere le ieme element de la liste des arcs entrants. */
	public TronconRoute getEntrant(int i) {return entrants.get(i) ; }
	/** Ajoute un objet a la liste des arcs entrants, et met a jour la relation inverse NoeudFin. */
	public void addEntrant(TronconRoute O) {
		if ( O == null ) return;
		entrants.add(O) ;
		O.setNoeudFin(this) ;
	}
	/** Enleve un element de la liste des arcs entrants, et met a jour la relation inverse NoeudFin. */
	public void removeEntrant(TronconRoute O) {
		if ( O == null ) return;
		entrants.remove(O) ;
		O.setNoeudFin(null);
	}
	/** Vide la liste des arcs entrants, et met a jour la relation inverse NoeudFin. */
	public void emptyEntrants() {
		List<TronconRoute> old = new ArrayList<TronconRoute>(entrants);
		Iterator<TronconRoute> it = old.iterator();
		while ( it.hasNext() ) {
			TronconRoute O = it.next();
			O.setNoeudFin(null);
		}
	}


	/** Lien bidirectionnel persistant la carrefourComplexe dont il est composant.
	 *  1 objet CarrefourComplexe est en relation avec 1 ou n objets noeuds.
	 *  1 objet Noeud est en relation avec 0 ou 1 objet carrefourComplexe.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas etre utilisees
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	protected CarrefourComplexe carrefourComplexe;
	/** Recupere le carrefourComplexe dont il est composant. */
	public CarrefourComplexe getCarrefourComplexe() {return carrefourComplexe;  }
	/** Definit le carrefourComplexe dont il est composant, et met a jour la relation inverse. */
	public void setCarrefourComplexe(CarrefourComplexe O) {
		CarrefourComplexe old = carrefourComplexe;
		carrefourComplexe = O;
		if ( old  != null ) old.getNoeuds().remove(this);
		if ( O != null ) {
			carrefourComplexeID = O.getId();
			if ( !(O.getNoeuds().contains(this)) ) O.getNoeuds().add(this);
		} else carrefourComplexeID = 0;
	}
	protected int carrefourComplexeID;
	/** Ne pas utiliser, necessaire au mapping OJB */
	public void setCarrefourComplexeID(int I) {carrefourComplexeID = I;}
	/** Ne pas utiliser, necessaire au mapping OJB */
	public int getCarrefourComplexeID() {return carrefourComplexeID;}



	/** Une communication restreinte concerne un noeud.
	 *  1 objet CommunicationRestreinte est en relation avec 1 Noeud.
	 *  1 objet Noeud est en relation avec 0 ou 1 objet CommunicationRoutiere.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas etre utilisees
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	protected CommunicationRestreinte communication;
	/** Recupere l'objet en relation. */
	public CommunicationRestreinte getCommunication() {return communication;  }
	/** Definit l'objet en relation, et met a jour la relation inverse. */
	public void setCommunication(CommunicationRestreinte O) {
		CommunicationRestreinte old = communication;
		communication = O;
		if ( old != null ) old.setNoeud(null);
		if ( O != null ) {
			communicationID = O.getId();
			if ( O.getNoeud() != this ) O.setNoeud(this);
		} else communicationID = 0;
	}
	protected int communicationID;
	/** Ne pas utiliser, necessaire au mapping OJB */
	public int getCommunicationID() {return communicationID;}
	/** Ne pas utiliser, necessaire au mapping OJB */
	public void setCommunicationID (int I) {communicationID = I;}



	/** Liste (non ordonnee) des liaisons maritimes sortants de self
	 * <BR> <STRONG> Definition </STRONG>:
	 *  1 objet Noeud est en relation "sortantsMaritime" avec 0 ou n objets liaisons maritimes.
	 *  1 objet liaison est en relation "ini" avec 1 objet Noeud.
	 *
	 *  Les methodes get (sans indice) et set sont necessaires au mapping.
	 *  Les autres methodes sont la seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 *      Relation topologique participant a la gestion de la logique de parcours du reseau routier :
	 *      Elle precise lengthnoeud routier initial d'un troncon de route.
	 * <BR> <STRONG> Type des elements de la liste </STRONG>:
	 *      TronconRoute.
	 * <BR> <STRONG> Cardinalite de la relation </STRONG>:
	 *      1 troncon a 0 ou 1 noeud initial NoeudIni.
	 *      1 noeud a 0 ou n troncons sortants.
	 */
	protected List<LiaisonMaritime> sortantsMaritime = new ArrayList<LiaisonMaritime>();

	/** Recupere la liste des arcs Maritime sortants. */
	public List<LiaisonMaritime> getSortantsMaritime() {return sortantsMaritime ; }
	/** Definit la liste des arcs Maritime sortants, et met a jour la relation inverse Ini. */
	public void setSortantsMaritime(List<LiaisonMaritime> L) {
		List <LiaisonMaritime>old = new ArrayList<LiaisonMaritime>(sortantsMaritime);
		Iterator<LiaisonMaritime> it1 = old.iterator();
		while ( it1.hasNext() ) {
			LiaisonMaritime O = it1.next();
			O.setNoeudIni(null);
		}
		Iterator<LiaisonMaritime> it2 = L.iterator();
		while ( it2.hasNext() ) {
			LiaisonMaritime O = it2.next();
			O.setNoeudIni(this);
		}
	}
	/** Recupere le ieme element de la liste des arcs Maritime sortants. */
	public LiaisonMaritime getSortantMaritime(int i) {return sortantsMaritime.get(i) ; }
	/** Ajoute un objet a la liste des arcs sortants, et met a jour la relation inverse Ini. */
	public void addSortantMaritime(LiaisonMaritime O) {
		if ( O == null ) return;
		sortantsMaritime.add(O) ;
		O.setNoeudIni(this) ;
	}
	/** Enlave un element de la liste des arcs Maritime sortants, et met a jour la relation inverse Ini. */
	public void removeSortantMaritime(LiaisonMaritime O) {
		if ( O == null ) return;
		sortantsMaritime.remove(O) ;
		O.setNoeudIni(null);
	}
	/** Vide la liste des arcs Maritime sortants, et met a jour la relation inverse Ini. */
	public void emptySortantsMaritime() {
		List<LiaisonMaritime> old = new ArrayList<LiaisonMaritime>(sortantsMaritime);
		Iterator<LiaisonMaritime> it = old.iterator();
		while ( it.hasNext() ) {
			LiaisonMaritime O = it.next();
			O.setNoeudIni(null);
		}
	}


	/** Liste (non ordonnee) des liaisons maritimes entrants de self
	 * <BR> <STRONG> Definition </STRONG>:
	 *  1 objet Noeud est en relation "entrantsMaritime" avec 0 ou n objets liaisons maritimes.
	 *  1 objet liaison est en relation "fin" avec 1 objet Noeud.
	 *
	 *  Les methodes get (sans indice) et set sont necessaires au mapping.
	 *  Les autres methodes sont la seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 *      Relation topologique participant a la gestion de la logique de parcours du reseau routier :
	 *      Elle precise lengthnoeud routier initial d'un troncon de route.
	 * <BR> <STRONG> Type des elements de la liste </STRONG>:
	 *      TronconRoute.
	 * <BR> <STRONG> Cardinalite de la relation </STRONG>:
	 *      1 troncon a 0 ou 1 noeud initial NoeudFin.
	 *      1 noeud a 0 ou n troncons entrants.
	 */
	protected List<LiaisonMaritime> entrantsMaritime = new ArrayList<LiaisonMaritime>();

	/** Recupere la liste des arcs Maritime entrants. */
	public List<LiaisonMaritime> getEntrantsMaritime() {return entrantsMaritime ; }
	/** Definit la liste des arcs Maritime entrants, et met a jour la relation inverse NoeudFin. */
	public void setEntrantsMaritime(List<LiaisonMaritime> L) {
		List<LiaisonMaritime> old = new ArrayList<LiaisonMaritime>(entrantsMaritime);
		Iterator<LiaisonMaritime> it1 = old.iterator();
		while ( it1.hasNext() ) {
			LiaisonMaritime O = it1.next();
			O.setNoeudFin(null);
		}
		Iterator<LiaisonMaritime> it2 = L.iterator();
		while ( it2.hasNext() ) {
			LiaisonMaritime O = it2.next();
			O.setNoeudFin(this);
		}
	}
	/** Recupere le ieme element de la liste des arcs Maritime entrants. */
	public LiaisonMaritime getEntrantMaritime(int i) {return entrantsMaritime.get(i) ; }
	/** Ajoute un objet a la liste des arcs entrants, et met a jour la relation inverse NoeudFin. */
	public void addEntrantMaritime(LiaisonMaritime O) {
		if ( O == null ) return;
		entrantsMaritime.add(O) ;
		O.setNoeudFin(this) ;
	}
	/** Enleve un element de la liste des arcs Maritime entrants, et met a jour la relation inverse NoeudFin. */
	public void removeEntrantMaritime(LiaisonMaritime O) {
		if ( O == null ) return;
		entrantsMaritime.remove(O) ;
		O.setNoeudFin(null);
	}
	/** Vide la liste des arcs Maritime entrants, et met a jour la relation inverse NoeudFin. */
	public void emptyEntrantsMaritime() {
		List<LiaisonMaritime> old = new ArrayList<LiaisonMaritime>(entrantsMaritime);
		Iterator<LiaisonMaritime> it = old.iterator();
		while ( it.hasNext() ) {
			LiaisonMaritime O = it.next();
			O.setNoeudFin(null);
		}
	}



}
