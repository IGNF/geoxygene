package fr.ign.cogit.appli.commun.schema.structure.georoute.routier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.georoute.ElementGeoroute;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public abstract class TronconRoute extends ElementGeoroute {


	//////////////// GEOMETRIE //////////////////
	//    private GM_LineString geometrie = null;
	/** Renvoie la geometrie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** Definit la geometrie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	public String classementPhysique;
	public String getClassementPhysique() {return classementPhysique;}
	public void setClassementPhysique(String classementPhysique) {
		this.classementPhysique = classementPhysique;
	}

	public String classementFonctionnel;
	public String getClassementFonctionnel() {return classementFonctionnel;}
	public void setClassementFonctionnel(String classementFonctionnel) {
		this.classementFonctionnel = classementFonctionnel;
	}

	public String limiteAdministrative;
	public String getLimiteAdministrative() {return limiteAdministrative;}
	public void setLimiteAdministrative(String limite) {this.limiteAdministrative = limite;}


	public String dateMiseEnService;
	public String getDateMiseEnService() {return dateMiseEnService;}
	public void setDateMiseEnService(String dateMiseEnService) {
		this.dateMiseEnService = dateMiseEnService;
	}

	public String positionSol;
	public String getPositionSol() {return positionSol;}
	public void setPositionSol(String positionSol) {this.positionSol = positionSol;}


	public String niveauFranchissement;
	public String getNiveauFranchissement() {return niveauFranchissement;}
	public void setNiveauFranchissement(String niveau) {this.niveauFranchissement = niveau;}


	public String sensCirculation;
	public String getSensCirculation() {return sensCirculation;}
	public void setSensCirculation(String sens) {this.sensCirculation = sens;}


	public String nbVoies;
	public String getNbVoies() {return nbVoies;}
	public void setNbVoies(String S) {this.nbVoies = S;}


	public String restrictionAcces;
	public String getRestrictionAcces() {return restrictionAcces;}
	public void setRestrictionAcces(String acces) {this.restrictionAcces = acces;}


	public String voieDeBus;
	public String getVoieDeBus() {return voieDeBus;}
	public void setVoieDeBus(String voieDeBus) {this.voieDeBus = voieDeBus;}


	public String interditMatiereDanger;
	public String getInterditMatiereDanger() {return interditMatiereDanger;}
	public void setInterditMatiereDanger(String interditMD) {this.interditMatiereDanger = interditMD;}


	public String horaireLivraison;
	public String getHoraireLivraison() {return horaireLivraison;}
	public void setHoraireLivraison(String horaireLivraison) {this.horaireLivraison = horaireLivraison;}


	public String horaireInterditCirculation;
	public String getHoraireInterditCirculation() {return horaireInterditCirculation;}
	public void setHoraireInterditCirculation(String horaireCircu) {this.horaireInterditCirculation = horaireCircu;}


	public String restrictionMarchandise;
	public String getRestrictionMarchandise() {return restrictionMarchandise;}
	public void setRestrictionMarchandise(String restrictionMarchandise) {this.restrictionMarchandise = restrictionMarchandise;}


	public String restrictionPoids;
	public String getRestrictionPoids() {return restrictionPoids;}
	public void setRestrictionPoids(String restrictionPoids) {this.restrictionPoids = restrictionPoids;}


	public String restrictionHauteur;
	public String getRestrictionHauteur() {return restrictionHauteur;}
	public void setRestrictionHauteur(String restrictionHauteur) {this.restrictionHauteur = restrictionHauteur;}


	public String restrictionLongueur;
	public String getRestrictionLongueur() {return restrictionLongueur;}
	public void setRestrictionLongueur(String restrictionLongueur) {this.restrictionLongueur = restrictionLongueur;}


	public String restrictionLargeur;
	public String getRestrictionLargeur() {return restrictionLargeur;}
	public void setRestrictionLargeur(String restrictionLargeur) {this.restrictionLargeur = restrictionLargeur;}


	public String borneDebGauche;
	public String getBorneDebGauche() {return borneDebGauche;}
	public void setBorneDebGauche(String borneDebGauche) {this.borneDebGauche = borneDebGauche;}


	public String borneDebDroite;
	public String getBorneDebDroite() {return borneDebDroite;}
	public void setBorneDebDroite(String borneDebDroite) {this.borneDebDroite = borneDebDroite;}


	public String borneFinGauche;
	public String getBorneFinGauche() {return borneFinGauche;}
	public void setBorneFinGauche(String borneFinGauche) {this.borneFinGauche = borneFinGauche;}


	public String borneFinDroite;
	public String getBorneFinDroite() {return borneFinDroite;}
	public void setBorneFinDroite(String borneFinDroite) {this.borneFinDroite = borneFinDroite;}


	public String typeAdressage;
	public String getTypeAdressage() {return typeAdressage;}
	public void setTypeAdressage(String typeA) {this.typeAdressage = typeA;}


	public String nomRueGauche;
	public String getNomRueGauche() {return nomRueGauche;}
	public void setNomRueGauche(String nomRueGauche) {this.nomRueGauche = nomRueGauche;}


	public String inseeCommuneGauche;
	public String getInseeCommuneGauche() {return inseeCommuneGauche;}
	public void setInseeCommuneGauche(String inseeCommuneGauche) {
		this.inseeCommuneGauche = inseeCommuneGauche;
	}


	public String nomRueDroite;
	public String getNomRueDroite() {return nomRueDroite;}
	public void setNomRueDroite(String nomRueDroite) {
		this.nomRueDroite = nomRueDroite;
	}


	public String inseeCommuneDroite;
	public String getInseeCommuneDroite() {return inseeCommuneDroite;}
	public void setInseeCommuneDroite(String inseeCommuneDroite) {
		this.inseeCommuneDroite = inseeCommuneDroite;
	}

	public String itineraireVert;
	public String getItineraireVert() {return itineraireVert;}
	public void setItineraireVert(String itineraireVert) {this.itineraireVert = itineraireVert;}


	public String nomItineraire;
	public String getNomItineraire() {return nomItineraire;}
	public void setNomItineraire(String toponymeItineraire) {this.nomItineraire = toponymeItineraire;}


	public String numeroRoute;
	public String getNumeroRoute() {return numeroRoute;}
	public void setNumeroRoute(String numeroRoute) {this.numeroRoute = numeroRoute;}


	protected NoeudRoutier noeudIni;
	/** Recupere le noeud initial. */
	public NoeudRoutier getNoeudIni() {return noeudIni;}
	/** Definit le noeud initial, et met a jour la relation inverse. */
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
	/** Ne pas utiliser, necessaire au mapping*/
	public void setNoeudIniID(int I) {noeudIniID = I;}
	/** Ne pas utiliser, necessaire au mapping*/
	public int getNoeudIniID() {return noeudIniID;}



	/** Noeud final du troncon.
	 * <BR> <STRONG> Definition </STRONG>:
	 *      Relation topologique participant a la gestion de la logique de parcours du reseau routier :
	 *      Elle precise le noeud routier initial d'un troncon de route.
	 * <BR> <STRONG> Type </STRONG>:
	 *      NoeudRoutier.
	 */
	protected NoeudRoutier noeudFin;
	/** Recupere le noeud final. */
	public NoeudRoutier getNoeudFin() {return noeudFin;}
	/** Definit le noeud final, et met a jour la relation inverse. */
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
	/** Ne pas utiliser, necessaire au mapping*/
	public void setNoeudFinID(int I) {noeudFinID = I;}
	/** Ne pas utiliser, necessaire au mapping*/
	public int getNoeudFinID() {return noeudFinID;}


	/** Liste (non ordonnee) des non communication INI concernees par self
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
	 * <BR> <STRONG> Type des elements de la liste </STRONG>:
	 *      TronconRoute.
	 */
	protected List<NonCommunication> nonCommInis = new ArrayList<NonCommunication>();

	/** Recupere la liste des nonCommInis. */
	public List<NonCommunication> getNonCommInis() {return nonCommInis ; }
	/** Definit la liste des nonCommunication, et met a jour la relation inverse. */
	public void setNonCommInis(List<NonCommunication> L) {
		List<NonCommunication> old = new ArrayList<NonCommunication>(nonCommInis);
		Iterator<NonCommunication> it1 = old.iterator();
		while ( it1.hasNext() ) {
			NonCommunication O = it1.next();
			O.setTronconEntrant(null);
		}
		Iterator<NonCommunication> it2 = L.iterator();
		while ( it2.hasNext() ) {
			NonCommunication O = it2.next();
			O.setTronconEntrant(this);
		}
	}
	/** Recupere le ieme element de la liste des nonCommInis. */
	public NonCommunication getNonCommIni(int i) {return nonCommInis.get(i) ; }
	/** Ajoute un objet a la liste des arcs entrants, et met a jour la relation inverse NoeudFin. */
	public void addNonCommIni(NonCommunication O) {
		if ( O == null ) return;
		nonCommInis.add(O) ;
		O.setTronconEntrant(this) ;
	}
	/** Enleve un element de la liste des nonCommInis, et met a jour la relation inverse NoeudFin. */
	public void removeNonCommIni(NonCommunication O) {
		if ( O == null ) return;
		nonCommInis.remove(O) ;
		O.setTronconEntrant(null);
	}
	/** Vide la liste des nonCommInis, et met a jour la relation inverse NoeudFin. */
	public void emptyNonCommInis() {
		List<NonCommunication> old = new ArrayList<NonCommunication>(nonCommInis);
		Iterator<NonCommunication> it = old.iterator();
		while ( it.hasNext() ) {
			NonCommunication O = it.next();
			O.setTronconEntrant(null);
		}
	}

	/** Liste (non ordonnee) des non communication FIN concernees par self
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
	 * <BR> <STRONG> Type des elements de la liste </STRONG>:
	 *      TronconRoute.
	 */
	protected List<NonCommunication> nonCommFins = new ArrayList<NonCommunication>();

	/** Recupere la liste des nonCommFins. */
	public List<NonCommunication> getNonCommFins() {return nonCommFins ; }
	/** Definit la liste des nonCommunication, et met a jour la relation inverse. */
	public void setNonCommFins(List<NonCommunication> L) {
		List<NonCommunication> old = new ArrayList<NonCommunication>(nonCommFins);
		Iterator<NonCommunication> it1 = old.iterator();
		while ( it1.hasNext() ) {
			NonCommunication O = it1.next();
			O.setTronconSortant(null);
		}
		Iterator<NonCommunication> it2 = L.iterator();
		while ( it2.hasNext() ) {
			NonCommunication O = it2.next();
			O.setTronconSortant(this);
		}
	}
	/** Recupere le ieme element de la liste des nonCommFins. */
	public NonCommunication getNonCommFin(int i) {return nonCommFins.get(i) ; }
	/** Ajoute un objet a la liste des arcs entrants, et met a jour la relation inverse NoeudFin. */
	public void addNonCommFin(NonCommunication O) {
		if ( O == null ) return;
		nonCommFins.add(O) ;
		O.setTronconSortant(this) ;
	}
	/** Enleve un element de la liste des nonCommFins, et met a jour la relation inverse NoeudFin. */
	public void removeNonCommFin(NonCommunication O) {
		if ( O == null ) return;
		nonCommFins.remove(O) ;
		O.setTronconSortant(null);
	}
	/** Vide la liste des nonCommFins, et met a jour la relation inverse NoeudFin. */
	public void emptyNonCommFins() {
		List<NonCommunication> old = new ArrayList<NonCommunication>(nonCommFins);
		Iterator<NonCommunication> it = old.iterator();
		while ( it.hasNext() ) {
			NonCommunication O = it.next();
			O.setTronconSortant(null);
		}
	}

}
