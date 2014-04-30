package fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.franchissement.Troncon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * Tronçon de route.
 * <BR> <STRONG> Type </STRONG>:
 *      Objet simple
 * <BR> <STRONG> Localisation </STRONG>:
 *      Linéraire.
 * <BR> <STRONG> Liens </STRONG>:
 *      //Compose (lien inverse) "Route numerotée ou nommée"
 * <BR> <STRONG> Définition </STRONG>:
 *      Les tronçons de route, chemins ou sentier. Les voies en construction sont retenues,
 *      sans raccordement au réseau existant, dans la mesure où les terrassements ont débuté sur le terrain.
 * <BR> <STRONG> Compatibilité entre attributs </STRONG> :
 * <UL>
 * <LI> Si "nbChaussees" = "2 chaussees" alors "nbVoies" = "sans objet" </LI>
 * <LI> Si "vocation" = "bretelle" alors "nbVoies" = "sans objet" </LI>
 * <LI> Si "nbChaussees" = "2 chaussees" alors "sens" != "sans objet" </LI>
 * <LI> Si "nbChaussees" = "2 chaussees" alors "nbVoiesMontantes" != "sans objet" </LI>
 * <LI> Si "nbChaussees" = "2 chaussees" alors "nbVoiesDescendantes" != "sans objet" </LI>
 * <LI> Si "nbChaussees" = "1 chaussee" alors "nbVoiesMontantes" = "sans objet" </LI>
 * <LI> Si "nbChaussees" = "1 chaussee" alors "nbVoiesDescendantes" = "sans objet" </LI>
 * <LI> Si "utilisation" = "cartographique seule" alors "acces" = "libre" </LI>
 * <LI> Si "etatPhysique" != "en construction ou en projet" alors "dateMiseEnservice" = "sans objet" </LI>
 * <LI> + compatiblite sur les toponymes ? </LI>
 * </UL>
 * 
 * @author braun
 */

public abstract class TronconRoute extends Troncon {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des géoémtries multiples (plusieurs tronçons) */
	//    protected GM_Curve geometrie = null;
	/** Renvoie le GM_LineString qui définit la géométrie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Définit le GM_LineString qui définit la géométrie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	/////////////// ATTRIBUTS //////////////////
	/** Vocation de la liaison.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Cet attribut matérialise une hiérarchisation du réseau routier basée, non pas sur un critère administratif,
	 *      mais sur l'importance des tronçons de route pour le trafic routier.
	 *      Ainsi, les 4 valeurs "type autoroutier" "liaison principale", "liaison régionale" et
	 *      "liaison locale" permettent un maillage de plus en plus dense du territoire.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     1- "type autoroutier" </LI>
	 * <LI>     2- "liaison principale" </LI>
	 * <LI>     6- "liaison régionale" </LI>
	 * <LI>     7- "liaison locale" </LI>
	 * <LI>     8- "bretelle" </LI>
	 * <LI>     10- "piste cyclable" </LI>
	 * </UL>
	 */
	protected String vocation;
	public String getVocation() { return vocation; }
	public void setVocation(String vocation) { this.vocation = vocation; }


	/** Nombre de chaussées.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Nombre de chaussées. Pour les voies à chaussées séparées: si elles sont contiguës, la BDCarto contient un tronçon à deux chaussées.
	 *      Si elle sont éloignées de plus de 100 m sur au moins 1km de long, la BDCarto contient deux tronçons à une chaussée.
	 * <BR> <STRONG> int </STRONG>:
	 *      entier.
	 * <BR> <STRONG> Valeurs possibles </STRONG>: <UL>
	 *  <LI>    1  </LI>
	 *  <LI>    2   </LI>
	 * </UL>
	 */
	protected int nbChaussees;
	public int getNbChaussees() { return nbChaussees; }
	public void setNbChaussees(int I) { this.nbChaussees = I; }


	/** Nombre total de voies.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Nombre total de voies.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     S- "sans objet" (valeur obligatoire pour les voies a 2 chaussées et les bretelles d'échangeurs) </LI>
	 * <LI>     1- "1 voie" </LI>
	 * <LI>     2- "2 voies étroites" </LI>
	 * <LI>     3- "3 voies" (chaussée normalisée 10.50 m) </LI>
	 * <LI>     4- "4 voies" </LI>
	 * <LI>     7- "2 voies normalisées" (chaussée normalisée 7 m) </LI>
	 * <LI>     9- "plus de 4 voies" </LI>
	 * </UL>
	 */
	protected String nbVoies;
	public String getNbVoies() { return nbVoies;}
	public void setNbVoies(String nbVoies) { this.nbVoies = nbVoies; }


	/** Etat physique de la route.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Etat physique de la route.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     1- "route revêtue" </LI>
	 * <LI>     2- "route non revêtue" </LI>
	 * <LI>     3- "en construction ou en projet" </LI>
	 * <LI>     4- "chemin d'exploitation" </LI>
	 * <LI>     5- "sentier" </LI>
	 * </UL>
	 * <BR> <STRONG> Remarques</STRONG>:
	 *      Une route en construction ou en projet n'est pas raccordée au réseau existant.
	 */
	protected String etatPhysique;
	public String getEtatPhysique() { return etatPhysique; }
	public void setEtatPhysique(String etatPhysique) { this.etatPhysique = etatPhysique; }


	/** Accès.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Accès.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     1- "libre" </LI>
	 * <LI>     2- "à péage" </LI>
	 * <LI>     3- "interdit au public" </LI>
	 * <LI>     6- "fermeture saisonnière" </LI>
	 * </UL>
	 */
	public String acces;
	public String getAcces() { return acces; }
	public void setAcces(String acces) { this.acces = acces; }


	/** Position par rapport au sol.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Position par rapport au sol.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     1- "normal" </LI>
	 * <LI>     2- "sur viaduc ou sur pont" </LI>
	 * <LI>     3- "en tunnel, souterrain, couvert ou semi-couvert" </LI>
	 * </UL>
	 */
	protected String positionSol;
	public String getPositionSol() { return positionSol; }
	public void setPositionSol(String positionSol) { this.positionSol = positionSol; }


	/** Appartenance au réseau vert.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Il s'agit du réseau vert de transit (pas celui à l'intérieur des villes ni celui des poids-lourds).
	 * <BR> <STRONG> Boolean </STRONG>:
	 *      Chaîne de caractères.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     TRUE : appartient au réseau vert </LI>
	 * <LI>     FALSE : n'appartient pas au réseau vert </LI>
	 * </UL>
	 */
	public boolean reseauVert;
	public boolean getReseauVert() {  return reseauVert; }
	public void setReseauVert(boolean b) { this.reseauVert = b; }


	/** Sens.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Sens de circulation (par rapport au sens noeud initial vers le noeud final).
	 *      Il est géré de façon obligatoire sur les tronçons composant les voies à chaussées séparées éloignées
	 *      et sur les tronçons constituant un échangeur détaillé. Dans les autres cas, le sens est géré si l'information est connue.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     0- "double sens"  </LI>
	 * <LI>     2- "sens unique direct" (sens du tronçon) </LI>
	 * <LI>     3- "sens unique inverse" (sens inverse du tronçon) </LI>
	 * </UL>
	 */
	protected String sens;
	public String getSens() { return sens; }
	public void setSens(String sens) { this.sens = sens; }


	/** Nombre de voies chaussées montante.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Concerne uniquement les tronçons à chaussée séparée.
	 *      La chaussée montante est la chaussée dont la circulation se fait dansle sens noeud initial -> noeud final
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     S- "sans objet" </LI>
	 * <LI>     1- "1 voie" </LI>
	 * <LI>     2- "2 voies" </LI>
	 * <LI>     3- "3 voies" </LI>
	 * <LI>     4- "4 voies" </LI>
	 * <LI>     9- "plus de 4 voies" </LI>
	 * </UL>
	 */
	protected String nbVoiesMontantes;
	public String getNbVoiesMontantes() { return nbVoiesMontantes; }
	public void setNbVoiesMontantes(String nbVoiesMontantes) { this.nbVoiesMontantes = nbVoiesMontantes; }


	/** Nombre de voies chaussées descendante.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Concerne uniquement les tronçons à chaussée séparée.
	 *      La chaussée montante est la chaussée dont la circulation se fait dansle sens noeud final -> noeud initial
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     S- "sans objet" </LI>
	 * <LI>     1- "1 voie" </LI>
	 * <LI>     2- "2 voies" </LI>
	 * <LI>     3- "3 voies" </LI>
	 * <LI>     4- "4 voies" </LI>
	 * <LI>     9- "plus de 4 voies" </LI>
	 * </UL>
	 */
	protected String nbVoiesDescendantes;
	public String getNbVoiesDescendantes() { return nbVoiesDescendantes; }
	public void setNbVoiesDescendantes(String nbVoiesDescendantes) { this.nbVoiesDescendantes = nbVoiesDescendantes; }


	/** Toponyme.
	 * <BR> <STRONG> Définition </STRONG>:
	 * Seuls les noms de pont, viaduc, tunnel, sont portés par les tronçons de route ;
	 * les autres toponymes sont portés par des itinéraires routiers (voir plus loin B-c-3). Un toponyme est
	 * composé de trois parties pouvant éventuellement ne porter aucune valeur (n'existe pas ou inconnu s'il s'agit de pont, viaduc ou tunnel et sans objet sinon) :
	 * un terme générique ou une désignation, texte d'au plus 40 caractères.
	 * un article, texte d'au plus cinq caractères ;
	 * un élément spécifique, texte d'au plus 80 caractères ;
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 */
	protected String toponyme;
	public String getToponyme() { return toponyme; }
	public void setToponyme(String toponyme) { this.toponyme = toponyme; }


	/** Utilisation.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Permet de distinguer les tronçons en fonction leur utilisation potentielle
	 *      pour la description de logique de communication et/ou une représentation cartographique.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     1- "logique et cartographique" </LI>
	 * <LI>     2- "logique" </LI>
	 * <LI>     3- "cartographique" </LI>
	 * </UL>
	 */
	protected String utilisation;
	public String getUtilisation() { return utilisation; }
	public void setUtilisation(String utilisation) { this.utilisation = utilisation; }


	/** Date de mise en service.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Cet attribut n'est rempli que pour les tronçons en construction : mois et année de mise en service pour les tronçons en travaux.
	 *      Il est sans objet dans tous les autres cas.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Chaîne de caractères.
	 */
	protected String dateMiseEnService;
	public String getDateMiseEnService() { return dateMiseEnService; }
	public void setDateMiseEnService(String dateMiseEnService) { this.dateMiseEnService = dateMiseEnService; }


	/////////////// RELATIONS //////////////////

	/** Noeud initial du tronçon.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Relation topologique participant à la gestion de la logique de parcours du réseau routier :
	 *      Elle précise lengthnoeud routier initial d'un tronçon de route.
	 * <BR> <STRONG> Type </STRONG>:
	 *      NoeudRoutier.
	 * <BR> <STRONG> Cardinalité de la relation </STRONG>:
	 *      1 tronçon a 0 ou 1 noeud initial.
	 *      1 noeud à 0 ou n tronçons sortants.
	 */
	protected NoeudRoutier noeudIni;
	/** Récupère le noeud initial. */
	public NoeudRoutier getNoeudIni() {return noeudIni;}
	/** Définit le noeud initial, et met à jour la relation inverse. */
	public void setNoeudIni(NoeudRoutier O) {
		NoeudRoutier old = noeudIni;
		noeudIni = O;
		if ( old  != null ) old.getSortants().remove(this);
		if ( O != null ) {
			noeudIniID = O.getId();
			if ( !(O.getSortants().contains(this)) ) O.getSortants().add(this);
		} else noeudIniID = 0;
	}
	/** Pour le mapping avec OJB, dans le cas d'une relation 1-n, du cote 1 de la relation */
	protected int noeudIniID;
	/** Ne pas utiliser, nécessaire au mapping*/
	public void setNoeudIniID(int I) {noeudIniID = I;}
	/** Ne pas utiliser, nécessaire au mapping*/
	public int getNoeudIniID() {return noeudIniID;}



	/** Noeud final du tronçon.
	 * <BR> <STRONG> Définition </STRONG>:
	 *      Relation topologique participant à la gestion de la logique de parcours du réseau routier :
	 *      Elle précise lengthnoeud routier initial d'un tronçon de route.
	 * <BR> <STRONG> Type </STRONG>:
	 *      NoeudRoutier.
	 * <BR> <STRONG> Cardinalité de la relation </STRONG>:
	 *      1 tronçon a 0 ou 1 noeud initial.
	 *      1 noeud à 0 ou n tronçons sortants.
	 */
	protected NoeudRoutier noeudFin;
	/** Récupère le noeud final. */
	public NoeudRoutier getNoeudFin() {return noeudFin;}
	/** Définit le noeud final, et met à jour la relation inverse. */
	public void setNoeudFin(NoeudRoutier O) {
		NoeudRoutier old = noeudFin;
		noeudFin = O;
		if ( old  != null ) old.getEntrants().remove(this);
		if ( O != null ) {
			noeudFinID = O.getId();
			if ( !(O.getEntrants().contains(this)) ) O.getEntrants().add(this);
		} else noeudFinID = 0;
	}
	/** Pour le mapping avec OJB, dans le cas d'une relation 1-n, du cote 1 de la relation */
	protected int noeudFinID;
	/** Ne pas utiliser, nécessaire au mapping*/
	public void setNoeudFinID(int I) {noeudFinID = I;}
	/** Ne pas utiliser, nécessaire au mapping*/
	public int getNoeudFinID() {return noeudFinID;}


	/** Lien bidirectionnel persistant la route dont il est composant.
	 *  1 objet Route est en relation avec 1 ou n objets troncons.
	 *  1 objet Troncon est en relation avec 0 ou 1 objet route.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas être utilisées
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	protected Route route;
	/** Récupère la route dont il est composant. */
	public Route getRoute() {return route;  }
	/** Définit la route dont il est composant, et met à jour la relation inverse. */
	public void setRoute(Route O) {
		Route old = route;
		route = O;
		if ( old  != null ) old.getTroncons().remove(this);
		if ( O != null ) {
			routeID = O.getId();
			if ( !(O.getTroncons().contains(this)) ) O.getTroncons().add(this);
		} else routeID = 0;
	}
	protected int routeID;
	/** Ne pas utiliser, necessaire au mapping OJB */
	public void setRouteID(int I) {routeID = I;}
	/** Ne pas utiliser, necessaire au mapping OJB */
	public int getRouteID() {return routeID;}


	/** Lien bidirectionnel précisant l'itinéraire dont il est composant.
	 *  1 objet itinéraire est en relation avec 1 ou n objets tronçons.
	 *  1 objet Troncon est en relation avec 0 ou n objet itinéraire.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas être utilisées
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	private List<ItineraireRoutier> itineraires = new ArrayList<ItineraireRoutier>();
	/** Récupère les itineraires dont le tronçon fait partie */
	public List<ItineraireRoutier> getItineraires() {return itineraires ; }
	/** Définit les itineraires en relation, et met à jour la relation inverse. */
	public void setItineraires(List<ItineraireRoutier> L) {
		List<ItineraireRoutier> old = new ArrayList<ItineraireRoutier>(itineraires);
		Iterator<ItineraireRoutier> it1 = old.iterator();
		while ( it1.hasNext() ) {
			ItineraireRoutier O = it1.next();
			itineraires.remove(O);
			O.getTroncons().remove(this);
		}
		Iterator<ItineraireRoutier> it2 = L.iterator();
		while ( it2.hasNext() ) {
			ItineraireRoutier O = it2.next();
			itineraires.add(O);
			O.getTroncons().add(this);
		}
	}
	/** Récupère le ième élément de la liste des itineraires en relation. */
	public ItineraireRoutier getItineraire(int i) {return itineraires.get(i) ; }
	/** Ajoute un élément à la liste des itineraires en relation, et met à jour la relation inverse. */
	public void addItineraire(ItineraireRoutier O) {
		if ( O == null ) return;
		itineraires.add(O) ;
		O.getTroncons().add(this);
	}
	/** Enlève un élément de la liste des itineraires en relation, et met à jour la relation inverse. */
	public void removeItineraire(ItineraireRoutier O) {
		if ( O == null ) return;
		itineraires.remove(O) ;
		O.getTroncons().remove(this);
	}
	/** Vide la liste des itineraires en relation, et met à jour la relation inverse. */
	public void emptyItineraires() {
		Iterator<ItineraireRoutier> it = itineraires.iterator();
		while ( it.hasNext() ) {
			ItineraireRoutier O = it.next();
			O.getTroncons().remove(this);
		}
		itineraires.clear();
	}

	/** Un troncon de route permet d'accéder à n équipements routier ,
	 *  par l'intermédiaire de la classe-relation Accede.
	 *  1 objet troncon peut etre en relation avec 0 ou n "objets-relation" Accede.
	 *  1 "objet-relation" Accede est en relation avec 1 objet troncon.
	 *
	 *  Les méthodes get (sans indice) et set sont nécessaires au mapping.
	 *  Les autres méthodes sont là seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	protected List<Accede> accedent = new ArrayList<Accede>();

	/** Récupère la liste des Accede en relation. */
	public List<Accede> getAccedent() {return accedent; }
	/** Définit la liste des Accede en relation, et met à jour la relation inverse. */
	public void setAccedent(List <Accede>L) {
		List<Accede> old = new ArrayList<Accede>(accedent);
		Iterator<Accede> it1 = old.iterator();
		while ( it1.hasNext() ) {
			Accede O = it1.next();
			O.setTroncon(null);
		}
		Iterator <Accede>it2 = L.iterator();
		while ( it2.hasNext() ) {
			Accede O = it2.next();
			O.setTroncon(this);
		}
	}
	/** Récupère le ième élément de la liste des Accede en relation. */
	public Accede getAccede(int i) {return accedent.get(i) ; }
	/** Ajoute un objet à la liste des objets en relation, et met à jour la relation inverse. */
	public void addAccede(Accede O) {
		if ( O == null ) return;
		accedent.add(O) ;
		O.setTroncon(this) ;
	}
	/** Enlève un élément de la liste des Accede en relation, et met à jour la relation inverse. */
	public void removeAccede(Accede O) {
		if ( O == null ) return;
		accedent.remove(O) ;
		O.setTroncon(null);
	}
	/** Vide la liste des Accede en relation, et met à jour la relation inverse. */
	public void emptyAccedent() {
		List<Accede> old = new ArrayList<Accede>(accedent);
		Iterator<Accede> it = old.iterator();
		while ( it.hasNext() ) {
			Accede O = it.next();
			O.setTroncon(null);
		}
	}


	/** Une début de section est située sur un troncon.
	 *  1 objet DebutSection est en relation avec 1 Troncon.
	 *  1 objet Troncon est en relation avec 0 ou 1 objet DebutSection.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas être utilisées
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	protected DebutSection debutSection;
	/** Récupère l'objet en relation. */
	public DebutSection getDebutSection() {return debutSection;  }
	/** Définit l'objet en relation, et met à jour la relation inverse. */
	public void setDebutSection(DebutSection O) {
		DebutSection old = debutSection;
		debutSection = O;
		if ( old != null ) old.setTroncon(null);
		if ( O != null ) {
			debutSectionID = O.getId();
			if ( O.getTroncon() != this ) O.setTroncon(this);
		} else debutSectionID = 0;
	}
	protected int debutSectionID;
	/** Ne pas utiliser, necessaire au mapping OJB */
	public int getDebutSectionID() {return debutSectionID;}
	/** Ne pas utiliser, necessaire au mapping OJB */
	public void setDebutSectionID (int I) {debutSectionID = I;}


}
