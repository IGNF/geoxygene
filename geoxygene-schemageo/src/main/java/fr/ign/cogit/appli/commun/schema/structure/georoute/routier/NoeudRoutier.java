package fr.ign.cogit.appli.commun.schema.structure.georoute.routier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.georoute.ElementGeoroute;
import fr.ign.cogit.appli.commun.schema.structure.georoute.destination.Acces;
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
 *      Un noeud du réseau routier correspond à une extrémité de tronçon de route. Il traduit une modification des
 *      conditions de circulation.
 *      Pour les objets dans la partie interurbaine, les critères de sélection sont ceux de la BDCARTO V2 (cf.
 *      "Spécifications de contenu BDCARTO v2.3").
 */



public abstract class NoeudRoutier extends ElementGeoroute {

	//     private GM_Point geometrie = null;
	/** Renvoie la géométrie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** Définit la géométrie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}

	/** Nature de l'intersection.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Nature de l'intersection.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 * <BR> <STRONG> Valeurs </STRONG>:
	 *      - intersection simple : Endroit de l'espace routier où les routes se rejoignent ou se coupent au
	 *        même niveau. On place également des intersections simples dans les extrémités d'impasses.
	 *        Toute portion de l'espace routier symbolisant un choix d'au moins trois directions et de diamètre inférieur à 30 mètres lorsqu'on l'assimile à un
	 *        cercle.
	 *      - rond-point simple : Endroit de l'espace routier où les routes se rejoignent au même niveau, de
	 *         forme non exclusivement circulaire, possédant un terre-plein central infranchissable et ceinturé par une chaussée à sens unique. Les véhicules ne
	 *           s'y croisent pas.
	 *      - barrière de péage :  Lieu où l'on acquitte un droit de passage sur une voie publique ou un pont.
	 *      - Changement d'attribut :  élément signifiant un changement de valeur d'attribut (et notamment de
	 *           commune) sur un tronçon de route. Il s'agit d'un objet virtuel. On prend en compte les attributs suivants : classement physique, niveau au
	 *           franchissement, restriction d'accès, position par rapport au sol, nombres de
	 *           voies, INSEE commune gauche et droite ainsi que nom rue droite et
	 *           gauche. Les autre attributs ne changent de valeur qu'à une intersection.. Un
	 *           noeud ayant cette nature est appelé NCVA (voir glossaire)
	 *      - noeud d'accès : Objet virtuel utilisé pour mettre en relation un équipement (thème
	 *           destination) avec le réseau routier lorsqu'il n'existe pas d'autres Noeuds du
	 *           Réseau Routier à l'emplacement de l'accès. Les bornes postales au niveau
	 *      - franchissement : - en urbain - Un aménagement routier est considéré comme ponctuel si sa longueur
	 *           est inférieure à 50 mètres. L'objet ponctuel "franchissement" est placé à
	 *           l'endroit du franchissement. Pour les aménagements de plus de 50 mètres,
	 *           on place un objet ponctuel "franchissement" à l'emplacement réel du
	 *           franchissement (cf. attribut "position par rapport au sol" de la classe
	 *           "tronçon de route"). Dans le cas d'une intersection simple sur un
	 *           franchissement, on place seulement un franchissement sur le carrefour :
	 *           On saisit également les franchissements sur les voies ferrées et l'hydrographie. On place alors simplement un franchissement entre deux
	 *           tronçons de route, les tronçons portant le niveau 0 (pas d'information sur le fait qu'on passe sur de l'hydro ou une voie ferrée) : rivière
	 *                  - en interurbain - Un aménagement routier est considéré comme ponctuel si sa longueur est
	 *           inférieure à 200 mètres. On ne saisit pas les franchissements sur les voies ferrées ou sur l'hydrographie.
	 */
	public String nature;
	public String getNature() {return nature;}
	public void setNature(String nature) {this.nature = nature;}

	/** Toponyme.
	 * <BR> <STRONG> Définition </STRONG>:
	 * Dénomination usuelle du carrefour ou du franchissement. Il est composé d'un terme générique (rond-point de, place de, à) et d'un ou plusieurs
	 * noms propres ou communs. Il est écrit en majuscules et sans accent. Les abréviations utilisées pour le terme générique sont standardisées
	 * Il peut prendre les valeurs suivantes : <UL>
	 * <LI>     - sans objet : sur NCVA et Noeuds d'Accès </LI>
	 * <LI>     - inconnu </LI>
	 * <LI>     - "" : l'intersection ne porte pas de nom </LI>
	 * </UL>
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 */
	public String nom;
	public String getNom() {return nom;}
	public void setNom(String nom) {this.nom = nom;}



	/** Liste (non ordonnée) des arcs sortants de self
	 * <BR> <STRONG> Définition </STRONG>:
	 *  1 objet Noeud est en relation "sortants" avec n objets TronconRoutier.
	 *  1 objet TronconRoutier est en relation "ini" avec 1 objet Noeud.
	 *
	 *  Les méthodes get (sans indice) et set sont nécessaires au mapping.
	 *  Les autres méthodes sont là seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 *      Relation topologique participant à la gestion de la logique de parcours du réseau routier :
	 *      Elle précise le noeud routier initial d'un troncon de route.
	 * <BR> <STRONG> Type des éléments de la liste </STRONG>:
	 *      TronconRoute.
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
	/** Enlève un élèment de la liste des arcs sortants, et met à jour la relation inverse Ini. */
	public void removeSortant(TronconRoute O) {
		if ( O == null ) return;
		sortants.remove(O) ;
		O.setNoeudIni(null);
	}
	/** Vide la liste des arcs sortants, et met à jour la relation inverse Ini. */
	public void emptySortants() {
		List<TronconRoute> old = new ArrayList<TronconRoute>(sortants);
		Iterator<TronconRoute> it = old.iterator();
		while ( it.hasNext() ) {
			TronconRoute O = it.next();
			O.setNoeudIni(null);
		}
	}


	/** Liste (non ordonnée) des arcs entrants de self
	 * <BR> <STRONG> Définition </STRONG>:
	 *  1 objet Noeud est en relation "entrants" avec n objets TronconRoutier.
	 *  1 objet TronconRoutier est en relation "fin" avec 1 objet Noeud.
	 *
	 *  Les methodes get (sans indice) et set sont necessaires au mapping.
	 *  Les autres methodes sont là seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 *      Relation topologique participant à la gestion de la logique de parcours du réseau routier :
	 *      Elle précise lengthnoeud routier initial d'un tronçon de route.
	 * <BR> <STRONG> Type des éléments de la liste </STRONG>:
	 *      TronconRoute.
	 */
	protected List<TronconRoute> entrants = new ArrayList<TronconRoute>();

	/** Récupere la liste des arcs entrants. */
	public List<TronconRoute> getEntrants() {return entrants ; }
	/** Definit la liste des arcs entrants, et met a jour la relation inverse NoeudFin. */
	public void setEntrants(List<TronconRoute> L) {
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



	/** Liste (non ordonnee) des non communication concernees par self
	 * <BR> <STRONG> definition </STRONG>:
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
	 * <BR> <STRONG> Type des elements de la liste </STRONG>:
	 *      TronconRoute.
	 */
	protected List<NonCommunication> nonCommunications = new ArrayList<NonCommunication>();

	/** Recupere la liste des nonCommunications. */
	public List<NonCommunication> getNonCommunications() {return nonCommunications ; }
	/** Definit la liste des nonCommunication, et met a jour la relation inverse. */
	public void setNonCommunications(List<NonCommunication> L) {
		List<NonCommunication> old = new ArrayList<NonCommunication>(nonCommunications);
		Iterator<NonCommunication> it1 = old.iterator();
		while ( it1.hasNext() ) {
			NonCommunication O = it1.next();
			O.setNoeud(null);
		}
		Iterator<NonCommunication> it2 = L.iterator();
		while ( it2.hasNext() ) {
			NonCommunication O = it2.next();
			O.setNoeud(this);
		}
	}
	/** Recupere le ieme element de la liste des nonCommunications. */
	public NonCommunication getNonCommunication(int i) {return nonCommunications.get(i) ; }
	/** Ajoute un objet à la liste des arcs entrants, et met a jour la relation inverse NoeudFin. */
	public void addNonCommunication(NonCommunication O) {
		if ( O == null ) return;
		nonCommunications.add(O) ;
		O.setNoeud(this) ;
	}
	/** Enleve un element de la liste des nonCommunications, et met a jour la relation inverse NoeudFin. */
	public void removeNonCommunication(NonCommunication O) {
		if ( O == null ) return;
		nonCommunications.remove(O) ;
		O.setNoeud(null);
	}
	/** Vide la liste des nonCommunications, et met a jour la relation inverse NoeudFin. */
	public void emptyNonCommunications() {
		List<NonCommunication> old = new ArrayList<NonCommunication>(nonCommunications);
		Iterator<NonCommunication> it = old.iterator();
		while ( it.hasNext() ) {
			NonCommunication O = it.next();
			O.setNoeud(null);
		}
	}


	protected Acces acces;
	/** Recupere l'acces en relation */
	public Acces getAcces() {return acces;}
	/** Definit l'acces en relation, et met a jour la relation inverse. */
	public void setAcces(Acces O) {
		Acces old = acces;
		acces = O;
		if ( old != null ) old.setNoeud(null);
		if ( O != null ) {
			accesID = O.getId();
			if ( O.getNoeud() != this ) O.setNoeud(this);
		} else accesID = 0;
	}
	protected int accesID;
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public int getAccesID() {return accesID;}
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public void setAccesID (int I) {accesID = I;}




}
