package fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Noeud du réseau routier.
 * <BR> <STRONG> Type </STRONG>:
 *      Objet simple
 * <BR> <STRONG> Localisation </STRONG>:
 *      Ponctuelle.
 * <BR> <STRONG> Liens </STRONG>:
 *      //Compose (lien inverse) "Route numerotée ou nommée"
 * <BR> <STRONG> Définition </STRONG>:
 *      Un noeud du réseau routier correspond à une extrémité de tronçon de route ou de liaison maritime ;
 *      il traduit une modification des conditions de circulation : ce peut être une intersection, un obstacle ou un changement de valeur d'attribut.
 *      Il n'y a pas à proprement parler de sélection des noeuds routiers :
 *      elle est déduite de celle des tronçons de route et des liaisons maritimes et bacs. Les carrefours aménagés d'une extension supérieure à 100
 *      mètres et les ronds-points d'un diamètre supérieur à 50 mètres sont des noeuds avec une nature spécifique ;
 *      si leur extension est inférieure ils sont considérés comme des carrefours simples.
 *      D'autre part, si leur extension est supérieure à 100 mètres, ils sont également détaillés en plusieurs carrefours simples au même titre que les
 *      échangeurs (ils ont alors 2 descriptions : une généralisée et une détaillée).
 * <BR> <STRONG> Compatibilité entre attributs </STRONG> :
 * <UL>
 * <LI> compatiblite sur les toponymes ? </LI>
 * </UL>
 * 
 * @author braun
 */

public abstract class NoeudRoutier extends ElementBDCarto {

	//     protected GM_Point geometrie = null;
	/** Renvoie la géométrie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}

	/** Type de noeud.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Type de noeud.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     11- carrefour simple, cul de sac, carrefour aménagé d'une extension inférieure à 100 mètres, ou rond-point d'un diamètre inférieur à 50 mètres </LI>
	 * <LI>     12- intersection représentant un carrefour aménagé d'une extension supérieure à 100 mètres sans toboggan ni passage inférieur </LI>
	 * <LI>     14- intersection représentant un rond-point (giratoire) d'un diamètre supérieur à 100 mètres d'axe à axe </LI>
	 * <LI>     15- carrefour aménagé avec passage inférieur ou toboggan quelle que soit son extension </LI>
	 * <LI>     16- intersection représentant un échangeur complet </LI>
	 * <LI>     17- intersection représentant un échangeur partiel </LI>
	 * <LI>     18- rond-point (giratoire) d'un diamètre compris entre 50 et 100 mètres </LI>
	 * <LI>     22- embarcadère de bac ou liaison maritime </LI>
	 * <LI>     23- embarcadère de liaison maritime situé hors du territoire BDCarto, positionné de façon fictive en limite de ce territoire </LI>
	 * <LI>     31- barrière interdisant la communication libre entre deux portions de route, régulièrement ou irrégulièrement entretenue </LI>
	 * <LI>     32- barrière de douane (hors CEE) </LI>
	 * <LI>     40- changement d'attribut </LI>
	 * <LI>     45- noeud créé par l'intersection entre une route nationale et la limite de département quand il n'existe pas de noeud au lieu de l'intersection ou noeud créé pour découper des grands tronçons de route (ex : autoroute).</LI>
	 * <LI>     50- noeud de communication restreinte (voir B-rs-4) : noeud créé quand il n?existe pas de noeud correspondant aux valeurs ci-dessus au lieu de la restriction.</LI>
	 * </UL>
	 */
	public String type;
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }


	/** Toponyme.
	 * <BR> <STRONG> Définition </STRONG>:
	 * Un noeud du réseau routier peut porter un toponyme, si l'un au moins des tronçons connectés appartient au réseau classé, et si le noeud appartient à l'un des types suivants :
	 * carrefour simple,
	 * carrefour aménagé d'une extension supérieure à 100 m,
	 * rond-point d'un diamètre supérieur à 100 m,
	 * rond-point,
	 * carrefour aménagé avec passage inférieur ou tobbogan,
	 * échangeur complet ou partiel.
	 * Un noeud composant un carrefour complexe ne porte généralement pas de toponyme.
	 * Le toponyme est composé de trois parties pouvant ne porter aucune valeur (n'existe pas dans les cas ci-dessus et sans objet sinon) :
	 * un terme générique ou une désignation, texte d'au plus 40 caractères.
	 * un article, texte d'au plus cinq caractères ;
	 * un élément spécifique, texte d'au plus 80 caractères ;
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 */

	public String toponyme;
	public String getToponyme() { return toponyme; }
	public void setToponyme(String toponyme) { this.toponyme = toponyme; }


	/** Cote.
	 * <BR> <STRONG> Définition </STRONG>:
	 * Nombre entier donnant l'altitude en mètres. Cet attribut peut ne porter aucune valeur.
	 * <BR> <STRONG> Type </STRONG>:
	 *      entier > 0.
	 *      NB : 9999 correspond à une cote inconnue.
	 */
	public int cote;
	public int getCote() { return cote; }
	public void setCote(int I) { cote = I;  }


	/** Liste (non ordonnée) des arcs sortants de self
	 * <BR> <STRONG> Définition </STRONG>:
	 *  1 objet Noeud est en relation "sortants" avec n objets TronçonRoutier.
	 *  1 objet TronçonRoutier est en relation "ini" avec 1 objet Noeud.
	 *
	 *  Les méthodes get (sans indice) et set sont nécessaires au mapping.
	 *  Les autres méthodes sont là seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 *      Relation topologique participant à la gestion de la logique de parcours du réseau routier :
	 *      Elle précise lengthnoeud routier initial d'un tronçon de route.
	 * <BR> <STRONG> Type des éléments de la liste </STRONG>:
	 *      TronconRoute.
	 * <BR> <STRONG> Cardinalité de la relation </STRONG>:
	 *      1 tronçon a 0 ou 1 noeud initial NoeudIni.
	 *      1 noeud à 0 ou n tronçons sortants.
	 */
	protected List<TronconRoute> sortants = new ArrayList<TronconRoute>();

	/** Récupère la liste des arcs sortants. */
	public List<TronconRoute> getSortants() {return sortants ; }
	/** Définit la liste des arcs sortants, et met à jour la relation inverse Ini. */
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
	/** Récupère le ième élément de la liste des arcs sortants. */
	public TronconRoute getSortant(int i) {return sortants.get(i) ; }
	/** Ajoute un objet à la liste des arcs sortants, et met à jour la relation inverse Ini. */
	public void addSortant(TronconRoute O) {
		if ( O == null ) return;
		sortants.add(O) ;
		O.setNoeudIni(this) ;
	}
	/** Enlève un élément de la liste des arcs sortants, et met à jour la relation inverse Ini. */
	public void removeSortant(TronconRoute O) {
		if ( O == null ) return;
		sortants.remove(O) ;
		O.setNoeudIni(null);
	}
	/** Vide la liste des arcs sortants, et met à jour la relation inverse Ini. */
	public void emptySortants() {
		List <TronconRoute>old = new ArrayList<TronconRoute>(sortants);
		Iterator<TronconRoute> it = old.iterator();
		while ( it.hasNext() ) {
			TronconRoute O = it.next();
			O.setNoeudIni(null);
		}
	}


	/** Liste (non ordonnée) des arcs entrants de self
	 * <BR> <STRONG> Définition </STRONG>:
	 *  1 objet Noeud est en relation "entrants" avec n objets TronçonRoutier.
	 *  1 objet TronçonRoutier est en relation "fin" avec 1 objet Noeud.
	 *
	 *  Les méthodes get (sans indice) et set sont nécessaires au mapping.
	 *  Les autres méthodes sont là seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 *      Relation topologique participant à la gestion de la logique de parcours du réseau routier :
	 *      Elle précise lengthnoeud routier initial d'un tronçon de route.
	 * <BR> <STRONG> Type des éléments de la liste </STRONG>:
	 *      TronconRoute.
	 * <BR> <STRONG> Cardinalité de la relation </STRONG>:
	 *      1 tronçon a 0 ou 1 noeud initial NoeudFin.
	 *      1 noeud à 0 ou n tronçons entrants.
	 */
	protected List<TronconRoute> entrants = new ArrayList<TronconRoute>();

	/** Récupère la liste des arcs entrants. */
	public List<TronconRoute> getEntrants() {return entrants ; }
	/** Définit la liste des arcs entrants, et met à jour la relation inverse NoeudFin. */
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
	/** Récupère le ième élément de la liste des arcs entrants. */
	public TronconRoute getEntrant(int i) {return entrants.get(i) ; }
	/** Ajoute un objet à la liste des arcs entrants, et met à jour la relation inverse NoeudFin. */
	public void addEntrant(TronconRoute O) {
		if ( O == null ) return;
		entrants.add(O) ;
		O.setNoeudFin(this) ;
	}
	/** Enlève un élément de la liste des arcs entrants, et met à jour la relation inverse NoeudFin. */
	public void removeEntrant(TronconRoute O) {
		if ( O == null ) return;
		entrants.remove(O) ;
		O.setNoeudFin(null);
	}
	/** Vide la liste des arcs entrants, et met à jour la relation inverse NoeudFin. */
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
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas être utilisées
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	protected CarrefourComplexe carrefourComplexe;
	/** Récupère le carrefourComplexe dont il est composant. */
	public CarrefourComplexe getCarrefourComplexe() {return carrefourComplexe;  }
	/** Définit le carrefourComplexe dont il est composant, et met à jour la relation inverse. */
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
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas être utilisées
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	protected CommunicationRestreinte communication;
	/** Récupère l'objet en relation. */
	public CommunicationRestreinte getCommunication() {return communication;  }
	/** Définit l'objet en relation, et met à jour la relation inverse. */
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



	/** Liste (non ordonnée) des liaisons maritimes sortants de self
	 * <BR> <STRONG> Définition </STRONG>:
	 *  1 objet Noeud est en relation "sortantsMaritime" avec 0 ou n objets liaisons maritimes.
	 *  1 objet liaison est en relation "ini" avec 1 objet Noeud.
	 *
	 *  Les méthodes get (sans indice) et set sont nécessaires au mapping.
	 *  Les autres méthodes sont là seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 *      Relation topologique participant à la gestion de la logique de parcours du réseau routier :
	 *      Elle précise lengthnoeud routier initial d'un tronçon de route.
	 * <BR> <STRONG> Type des éléments de la liste </STRONG>:
	 *      TronconRoute.
	 * <BR> <STRONG> Cardinalité de la relation </STRONG>:
	 *      1 tronçon a 0 ou 1 noeud initial NoeudIni.
	 *      1 noeud à 0 ou n tronçons sortants.
	 */
	protected List<LiaisonMaritime> sortantsMaritime = new ArrayList<LiaisonMaritime>();

	/** Récupère la liste des arcs Maritime sortants. */
	public List<LiaisonMaritime> getSortantsMaritime() {return sortantsMaritime ; }
	/** Définit la liste des arcs Maritime sortants, et met à jour la relation inverse Ini. */
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
	/** Récupère le ième élément de la liste des arcs Maritime sortants. */
	public LiaisonMaritime getSortantMaritime(int i) {return sortantsMaritime.get(i) ; }
	/** Ajoute un objet à la liste des arcs sortants, et met à jour la relation inverse Ini. */
	public void addSortantMaritime(LiaisonMaritime O) {
		if ( O == null ) return;
		sortantsMaritime.add(O) ;
		O.setNoeudIni(this) ;
	}
	/** Enlève un élément de la liste des arcs Maritime sortants, et met à jour la relation inverse Ini. */
	public void removeSortantMaritime(LiaisonMaritime O) {
		if ( O == null ) return;
		sortantsMaritime.remove(O) ;
		O.setNoeudIni(null);
	}
	/** Vide la liste des arcs Maritime sortants, et met à jour la relation inverse Ini. */
	public void emptySortantsMaritime() {
		List<LiaisonMaritime> old = new ArrayList<LiaisonMaritime>(sortantsMaritime);
		Iterator<LiaisonMaritime> it = old.iterator();
		while ( it.hasNext() ) {
			LiaisonMaritime O = it.next();
			O.setNoeudIni(null);
		}
	}


	/** Liste (non ordonnée) des liaisons maritimes entrants de self
	 * <BR> <STRONG> Définition </STRONG>:
	 *  1 objet Noeud est en relation "entrantsMaritime" avec 0 ou n objets liaisons maritimes.
	 *  1 objet liaison est en relation "fin" avec 1 objet Noeud.
	 *
	 *  Les méthodes get (sans indice) et set sont nécessaires au mapping.
	 *  Les autres méthodes sont là seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 *      Relation topologique participant à la gestion de la logique de parcours du réseau routier :
	 *      Elle précise lengthnoeud routier initial d'un tronçon de route.
	 * <BR> <STRONG> Type des éléments de la liste </STRONG>:
	 *      TronconRoute.
	 * <BR> <STRONG> Cardinalité de la relation </STRONG>:
	 *      1 tronçon a 0 ou 1 noeud initial NoeudFin.
	 *      1 noeud à 0 ou n tronçons entrants.
	 */
	protected List<LiaisonMaritime> entrantsMaritime = new ArrayList<LiaisonMaritime>();

	/** Récupère la liste des arcs Maritime entrants. */
	public List<LiaisonMaritime> getEntrantsMaritime() {return entrantsMaritime ; }
	/** Définit la liste des arcs Maritime entrants, et met à jour la relation inverse NoeudFin. */
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
	/** Récupère le ième élément de la liste des arcs Maritime entrants. */
	public LiaisonMaritime getEntrantMaritime(int i) {return entrantsMaritime.get(i) ; }
	/** Ajoute un objet à la liste des arcs entrants, et met à jour la relation inverse NoeudFin. */
	public void addEntrantMaritime(LiaisonMaritime O) {
		if ( O == null ) return;
		entrantsMaritime.add(O) ;
		O.setNoeudFin(this) ;
	}
	/** Enlève un élément de la liste des arcs Maritime entrants, et met à jour la relation inverse NoeudFin. */
	public void removeEntrantMaritime(LiaisonMaritime O) {
		if ( O == null ) return;
		entrantsMaritime.remove(O) ;
		O.setNoeudFin(null);
	}
	/** Vide la liste des arcs Maritime entrants, et met à jour la relation inverse NoeudFin. */
	public void emptyEntrantsMaritime() {
		List<LiaisonMaritime> old = new ArrayList<LiaisonMaritime>(entrantsMaritime);
		Iterator<LiaisonMaritime> it = old.iterator();
		while ( it.hasNext() ) {
			LiaisonMaritime O = it.next();
			O.setNoeudFin(null);
		}
	}



}
