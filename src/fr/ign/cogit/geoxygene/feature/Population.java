/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package fr.ign.cogit.geoxygene.feature;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 *  Une population représente TOUS les objets d'une classe héritant de FT_Feature.
 *
 *  <P> Les objets qui la composent peuvent avoir une géometrie ou non.
 *  La population peut être persistante ou non, associée à un index spatial ou non.
 *
 *  <P> NB: une population existe indépendamment des ses éléments.
 *  Avant de charger ses éléments, la population existe mais ne contient aucun élément.
 * 
 * <P> Difference avec FT_FeatureCollection :
 * une Population est une FT_FeatureCollection possedant les proprietes suivantes.
 * <UL>
 * <LI> Lien vers DataSet. </LI>
 * <LI> Une population peut-etre persistante et exister independamment de ses elements. </LI>
 * <LI> Une population contient TOUS les elements de la classe. </LI>
 * <LI> Un element ne peut appartenir qu'a une seule population (mais a plusieurs FT_FeatureCollection). </LI>
 * <LI> Permet de gerer la persistence des elements de maniere efficace (via chargeElement(), nouvelElement(), etc.) </LI>
 * <LI> Possede quelques attributs (nom classe, etc.). </LI>
 * </UL>
 * TODO Finir les annotations pour la persistance
 * 
 * @author Sébastien Mustière
 * @author Sandrine Balley
 * @author Julien Perret 
 */
@Entity
public class Population<Feat extends FT_Feature> extends FT_FeatureCollection<Feat> {
	/** logger*/
	//static Logger logger=Logger.getLogger(Population.class.getName());
	/** Identifiant. Correspond au "cogitID" des tables du SGBD.*/
	protected int id;
	/** Renvoie l'identifiant. NB: l'ID n'est remplit automatiquement que si la population est persistante */
	@Id
	public int getId() {return this.id;}
	/** Affecte une valeur a l'identifiant */
	public void setId (int I) {this.id = I;}
	///////////////////////////////////////////////////////
	//      Constructeurs / Chargement / persistance
	///////////////////////////////////////////////////////
	/** Constructeur par défaut. Sauf besoins particuliers, utiliser plutôt l'autre constructeur */
	public Population() {}
	/**
	 * Constructeur à partir du nom de la population
	 * @param nom nom de la population.
	 */
	public Population(String nom) {
		super();
		this.setNom(nom);
	}
	/**
	 * Constructeur d'une population.
	 *  Une population peut être persistante ou non (la population elle-même est alors rendue persistante dans ce constructeur).
	 *  Une population a un nom logique (utile pour naviguer entre populations).
	 *  Les éléments d'une population se réalisent dans une classe concrete (classeElements).
	 *  <p>
	 *  <b>NB :</b> lors la construction, auncun élément n'est affectée à la population, cela doit être fait
	 *  à partir d'elements peristant avec chargeElements, ou a partir d'objets Java avec les setElements
	 * @param persistance si vrai, alors la population est persistante
	 * @param nomLogique nom de la population
	 * @param classeElements classe des éléments de la population
	 * @param drapeauGeom vrai si les éléments de la population portent une géométrie, faux sinon
	 */
	@SuppressWarnings("unchecked")
	public Population(boolean persistance, String nomLogique, Class<?> classeElements, boolean drapeauGeom) {
		this.setPersistant(persistance);
		this.setNom(nomLogique);
		this.setClasse((Class<Feat>)classeElements);
		this.flagGeom = drapeauGeom;
		if (persistance) DataSet.db.makePersistent(this);
	}
	/**
	 * Constructeur le plus adapté à l'utilisation des Populations dotées d'un
	 * lien vers le FeatureType correspondant.
	 * 
	 * @param ft
	 */
	public Population(FeatureType ft) {
		this.setFeatureType(ft);
		this.setNom(ft.getTypeName());
		this.setNomClasse(ft.getNomClasse());
	}
	/**
	 * Constructeur d'une population. Une population peut être persistante ou
	 * non (la population elle-même est alors rendue persistante dans ce
	 * constructeur). Une population a un nom logique (utile pour naviguer entre
	 * populations). Les éléments d'une population se réalisent dans une classe
	 * concrete (nom_classe_elements). 
	 * <p>
	 * <b>NB :</b> lors la construction, auncun élément
	 * n'est affecté à la population, cela doit être fait à partir d'elements
	 * peristant avec chargeElements, ou a partir d'objets Java avec les
	 * setElements
	 */
	public Population(FeatureType ft, boolean persistance, boolean drapeauGeom) {
		this.setFeatureType(ft);
		this.setNom(ft.getTypeName());
		this.setNomClasse(ft.getNomClasse());
		this.setPersistant(persistance);
		this.flagGeom = drapeauGeom;
		if (persistance) DataSet.db.makePersistent(this);
	}
	/**
	 * @param ft
	 * @param persistance
	 */
	public Population(FeatureType ft, boolean persistance) {
		/** attention nom de classe sans package, ca ne marche pas* */
		this.setFeatureType(ft);
		this.setNom(ft.getTypeName());
		this.setNomClasse(ft.getNomClasse());
		this.setPersistant(persistance);
		this.flagGeom = true;
		if (persistance) DataSet.db.makePersistent(this);
	}

	/** Chargement des éléments persistants d'une population.
	 *  Tous les éléments de la table correspondante sont chargés.
	 */
	public void chargeElements() {
		if (logger.isInfoEnabled()) logger.info("-- Chargement des elements de la population  "+this.getNom()); //$NON-NLS-1$
		if (!this.getPersistant()) {
			logger.warn("----- ATTENTION : Aucune instance n'est chargee dans la population "+this.getNom()); //$NON-NLS-1$
			logger.warn("-----             La population n'est pas persistante"); //$NON-NLS-1$
			return;
		}
		try {this.elements = DataSet.db.loadAll(this.classe);}
		catch (Exception e) {
			logger.error("----- ATTENTION : Chargement impossible de la population "+this.getNom()); //$NON-NLS-1$
			logger.error("-----             Sans doute un probleme avec le SGBD, ou table inexistante, ou pas de mapping "); //$NON-NLS-1$
			//e.printStackTrace();
			return;
		}
		if (logger.isInfoEnabled()) logger.info("-- "+this.size()+" instances chargees dans la population"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	/** Chargement des éléments persistants d'une population qui intersectent une géométrie donnée.
	 *  ATTENTION: la table qui stocke les éléments doit avoir été indexée dans le SGBD.
	 *  ATTENTION AGAIN: seules les populations avec une géométrie sont chargées.
	 */
	public void chargeElementsPartie(GM_Object geom) {
		if (logger.isInfoEnabled()) logger.info("-- Chargement des elements de la population  "+this.getNom()); //$NON-NLS-1$
		if (!this.getPersistant()) {
			logger.warn("----- ATTENTION : Aucune instance n'est chargee dans la population "+this.getNom()); //$NON-NLS-1$
			logger.warn("-----             La population n'est pas persistante"); //$NON-NLS-1$
			return;
		}
		if (!this.hasGeom()) {
			logger.warn("----- ATTENTION : Aucune instance n'est chargee dans la population "+this.getNom()); //$NON-NLS-1$
			logger.warn("-----             Les éléments de la population n'ont pas de géométrie"); //$NON-NLS-1$
			return;
		}
		try {
			this.elements = DataSet.db.loadAllFeatures(this.getClasse(), geom).getElements();
		} catch (Exception e) {
			logger.error("----- ATTENTION : Chargement impossible de la population "+this.getNom()); //$NON-NLS-1$
			logger.error("-----             La classe n'est peut-être pas indexée dans le SGBD"); //$NON-NLS-1$
			logger.error("-----             ou table inexistante, ou pas de mapping ou probleme avec le SGBD "); //$NON-NLS-1$
			return;
		}
		if (logger.isInfoEnabled()) logger.info("   "+this.size()+" instances chargees dans la population"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	/** Chargement des éléments persistants d'une population.
	 *  Tous les éléments de la table correspondante sont chargés.
	 *  Les données doivent d'abord avoir été indexées.
	 *  PB: TRES LENT !!!!!!!
	 */
	public void chargeElementsProches(Population<Feat> pop, double dist) {
		if (logger.isInfoEnabled()) {
			logger.info("-- Chargement des elements de la population  "+this.getNom()); //$NON-NLS-1$
			logger.info("-- à moins de "+dist+" de ceux de la population   "+pop.getNom()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (!this.getPersistant()) {
			logger.warn("----- ATTENTION : Aucune instance n'est chargee dans la population "+this.getNom()); //$NON-NLS-1$
			logger.warn("-----             La population n'est pas persistante"); //$NON-NLS-1$
			return;
		}
		try {
			Iterator<Feat> itPop = pop.getElements().iterator();
			Collection<Feat> selectionTotale = new HashSet<Feat>();
			while (itPop.hasNext()) {
				Feat objet = itPop.next();
				FT_FeatureCollection<Feat> selection = DataSet.db.loadAllFeatures(this.classe, objet.getGeom(), dist);
				selectionTotale.addAll(selection.getElements());
			}
			this.elements = new ArrayList<Feat>(selectionTotale);
		} catch (Exception e) {
			logger.error("----- ATTENTION : Chargement impossible de la population "+this.getNom()); //$NON-NLS-1$
			logger.error("-----             Sans doute un probleme avec le SGBD, ou table inexistante, ou pas de mapping "); //$NON-NLS-1$
			e.printStackTrace();
			return;
		}
		if (logger.isInfoEnabled()) logger.info("-- "+this.size()+" instances chargees dans la population"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/** Renvoie une population avec tous les éléments de this
	 *  situés à moins de "dist" des éléments de la population
	 *  Travail sur un index en mémoire (pas celui du SGBD).
	 *  Rmq : Fonctionne avec des objets de géométrie quelconque
	 */
	public Population<Feat> selectionElementsProchesGenerale(Population<Feat> pop, double dist) {
		Population<Feat> popTemporaire = new Population<Feat>();
		Population<Feat> popResultat = new Population<Feat>(false, this.getNom(), this.getClasse(),true);
		Set<Feat> selectionUnObjet, selectionTotale = new HashSet<Feat>();

		popTemporaire.addCollection(this);
		popTemporaire.initSpatialIndex(Tiling.class, true, 20);
		if (logger.isDebugEnabled()) logger.debug("Fin indexation "+(new Time(System.currentTimeMillis())).toString()); //$NON-NLS-1$
		Iterator<Feat> itPop = pop.getElements().iterator();
		while (itPop.hasNext()) {
			Feat objet = itPop.next();
			GM_Envelope enveloppe = objet.getGeom().envelope();
			double xmin = enveloppe.getLowerCorner().getX()-dist;
			double xmax = enveloppe.getUpperCorner().getX()+dist;
			double ymin = enveloppe.getLowerCorner().getY()-dist;
			double ymax = enveloppe.getUpperCorner().getY()+dist;
			enveloppe = new GM_Envelope(xmin,xmax,ymin,ymax);
			Collection<Feat> selection = popTemporaire.select(enveloppe);
			Iterator<Feat> itSel = selection.iterator();
			selectionUnObjet = new HashSet<Feat>();
			while (itSel.hasNext()) {
				Feat objetSel = itSel.next();
				//if (Distances.premiereComposanteHausdorff((GM_LineString)objetSel.getGeom(),(GM_LineString)objet.getGeom())<dist)
				if (objetSel.getGeom().distance(objet.getGeom())<dist) selectionUnObjet.add(objetSel);
			}
			popTemporaire.getElements().removeAll(selectionUnObjet);
			selectionTotale.addAll(selectionUnObjet);
		}
		popResultat.setElements(selectionTotale);
		return popResultat;
	}
	/** Renvoie une population avec tous les éléments de this
	 *  situés à moins de "dist" des éléments de la population pop.
	 */
	public Population<Feat> selectionLargeElementsProches(Population<Feat> pop, double dist) {
		Population<Feat> popTemporaire = new Population<Feat>();
		Population<Feat> popResultat = new Population<Feat>(false, this.getNom(), this.getClasse(),true);

		popTemporaire.addCollection(this);
		popTemporaire.initSpatialIndex(Tiling.class, true);
		Iterator<Feat> itPop = pop.getElements().iterator();
		while (itPop.hasNext()) {
			Feat objet = itPop.next();
			GM_Envelope enveloppe = objet.getGeom().envelope();
			double xmin = enveloppe.getLowerCorner().getX()-dist;
			double xmax = enveloppe.getUpperCorner().getX()+dist;
			double ymin = enveloppe.getLowerCorner().getY()-dist;
			double ymax = enveloppe.getUpperCorner().getY()+dist;
			enveloppe = new GM_Envelope(xmin,xmax,ymin,ymax);
			Collection<Feat> selection = popTemporaire.select(enveloppe);
			popTemporaire.getElements().removeAll(selection);
			popResultat.addAll(selection);
		}
		return popResultat;
	}
	/** Chargement des éléments persistants d'une population qui intersectent une zone d'extraction donnée.
	 *  ATTENTION: la table qui stocke les éléments doit avoir été indexée dans le SGBD.
	 *  ATTENTION AGAIN: seules les populations avec une géométrie sont chargées.
	 */
	public void chargeElementsPartie(Extraction zoneExtraction) {chargeElementsPartie(zoneExtraction.getGeom());}
	/** Detruit la population si elle est persistante,
	 *  MAIS ne détruit pas les éléments de cette population (pour cela vider la table correspondante dans le SGBD).
	 */
	public void detruitPopulation() {
		if (!this.getPersistant()) return;
		if (logger.isInfoEnabled()) logger.info("Destruction de la population des "+this.getNom()); //$NON-NLS-1$
		DataSet.db.deletePersistent(this);
	}
	///////////////////////////////////////////////////////
	//          Attributs décrivant la population
	///////////////////////////////////////////////////////
	/** Nom logique des éléments de la population.
	 *  La seule contrainte est de ne pas dépasser 255 caractères, les accents et espaces sont autorisés.
	 *  A priori, on met le nom des éléments au singulier.
	 *  Exemple: "Tronçon de route"
	 */
	protected String nom;
	public String getNom() {return this.nom; }
	public void setNom (String S) {this.nom = S; }

	/** Booléen spécifiant si la population est persistente ou non (vrai par défaut).  */
	// NB pour dévelopeurs : laisser 'true' par défaut.
	// Sinon, cela pose des problèmes au chargement (un thème persistant chargé a son attribut persistant à false).
	protected boolean persistant = true;
	/** Booléen spécifiant si la population est persistente ou non (vrai par défaut).  */
	public boolean getPersistant() {return this.persistant;}
	/** Booléen spécifiant si la population est persistente ou non (vrai par défaut).  */
	public void setPersistant(boolean b) {this.persistant = b;}

	///////////////////////////////////////////////////////
	//     Relations avec les thèmes et les étéments
	///////////////////////////////////////////////////////
	/** DataSet auquel apparient la population (une population appartient à un seul DataSet). */
	protected DataSet dataSet;
	/** Récupère le DataSet de la population. */
	@ManyToOne
	public DataSet getDataSet() {return this.dataSet;  }
	/** définit le DataSet de la population, et met à jour la relation inverse. */
	public void setDataSet(DataSet O) {
		DataSet old = this.dataSet;
		this.dataSet= O;
		if ( old  != null ) old.getPopulations().remove(this);
		if ( O != null ) {
			this.dataSetID = O.getId();
			if ( !(O.getPopulations().contains(this)) ) O.getPopulations().add(this);
		} else this.dataSetID = 0;
	}
	private int dataSetID;
	/** Ne pas utiliser, necessaire au mapping OJB */
	public void setDataSetID(int I) {this.dataSetID = I;}
	/** Ne pas utiliser, necessaire au mapping OJB */
	@Transient
	public int getDataSetID() {return this.dataSetID;}

	//////////////////////////////////////////////////
	// Methodes surchargeant des trucs de FT_FeatureCollection, avec une gestion de la persistance

	/** 
	 * enlève, ET DETRUIT si il est persistant, un élément de la liste des elements de la population,
	 * met également à jour la relation inverse, et eventuellement l'index.
	 * <p>
	 * <b>NB :</b> différent de remove (hérité de FT_FeatureCollection) qui ne détruit pas l'élément.
	 */
	public void enleveElement(Feat O) {
		super.remove(O);
		if ( this.getPersistant() ) DataSet.db.deletePersistent(O);
	}
	private static int idNouvelElement = 1;
	/** 
	 * crée un nouvel élément de la population, instance de sa classe par défaut, et l'ajoute à la population.
	 * <p>
	 *  Si la population est persistante, alors le nouvel élément est rendu persistant dans cette méthode
	 * <b>NB :</b> différent de add (hérité de FT_FeatureCollection) qui ajoute un élément déjà existant.
	 */
	public Feat nouvelElement() {return nouvelElement(null);}
	/**
	 * crée un nouvel élément de la population (avec la géométrie geom),
	 *  instance de sa classe par défaut, et l'ajoute à la population.
	 * <p>
	 *  Si la population est persistante, alors le nouvel élément est rendu persistant dans cette méthode
	 *  <b>NB :</b> différent de add (hérité de FT_FeatureCollection) qui ajoute un élément déjà existant.
	 */
	public Feat nouvelElement(GM_Object geom) {
		try {
			Feat elem = this.getClasse().newInstance();
			elem.setId(++idNouvelElement);
			elem.setGeom(geom);
			//elem.setPopulation(this);
			super.add(elem);
			if ( this.getPersistant() ) DataSet.db.makePersistent(elem);
			return elem;
		} catch (Exception e) {
			logger.error("ATTENTION : problème à la création d'un élément de la population "+this.getNom()); //$NON-NLS-1$
			logger.error("            Soit la classe des éléments est non valide : "+this.getNomClasse()); //$NON-NLS-1$
			logger.error("               Causes possibles : la classe n'existe pas? n'est pas compilée? est abstraite?"); //$NON-NLS-1$
			logger.error("            Soit problème à la mise à jour de l'index "); //$NON-NLS-1$
			logger.error("               Causes possibles : mise à jour automatique de l'index, mais l'objet n'a pas encore de géométrie"); //$NON-NLS-1$
			return null;
		}
	}
	/**
	 * crée un nouvel élément de la population, instance de sa classe par défaut, et l'ajoute à la population.
	 *  La création est effectuée à l'aide du constructeur spécifié par les tableaux signature(classe des
	 *  objets du constructeur), et param (objets eux-mêmes).
	 *  <p>
	 *  Si la population est persistante, alors le nouvel élément est rendu persistant dans cette méthode
	 *  <p>
	 * <b>NB :</b> différent de add (hérité de FT_FeatureCollection) qui ajoute un élément déjà existant.
	 * @param signature
	 * @param param
	 * @return a new Feature
	 */
	public Feat nouvelElement(Class<?>[] signature, Object[] param) {
		try {
			Feat elem = this.getClasse().getConstructor(signature).newInstance(param);
			super.add(elem);
			if ( this.getPersistant() ) DataSet.db.makePersistent(elem);
			return elem;
		} catch (Exception e) {
			logger.error("ATTENTION : problème à la création d'un élément de la population "+this.getNom()); //$NON-NLS-1$
			logger.error("            Classe des éléments non valide : "+this.getNomClasse()); //$NON-NLS-1$
			logger.error("            Causes possibles : la classe n'existe pas? n'est pas compilée?"); //$NON-NLS-1$
			return null;
		}
	}

	//////////////////////////////////////////////////
	// Copie de population
	/** 
	 * Copie la population passée en argument dans la population traitée (this).
	 * <p>
	 * <b>NB :<b>
	 * <ul>
	 * <li> 1/ ne copie pas l'eventuelle indexation spatiale,
	 * <li> 2/ n'affecte pas la population au DataSet de la population à copier.
	 * <li> 3/ mais recopie les autres infos: éléments, classe, FlagGeom, Nom et NomClasse
	 * </ul>
	 * @param populationACopier
	 */
	public void copiePopulation(Population<Feat> populationACopier){
		this.setElements(populationACopier.getElements());
		this.setClasse(populationACopier.getClasse());
		this.setFlagGeom(populationACopier.getFlagGeom());
		this.setNom(populationACopier.getNom());
		this.setNomClasse(populationACopier.getNomClasse());
	}
	// //////////////////////////////////////////////////////////////////////////////
	/**
	 * Complète Population.chargeElements().
	 * - On vérifie que la population correspond à une classe du schéma conceptuel du DataSet.
	 *   Si non, on initie les populations du DataSet en y incluant celle-ci.
	 * - Chaque FT_Feature chargé est renseigné avec sa population (donc son featureType).
	 */
	public void chargeElementsAvecMetadonnees(){
		if (logger.isInfoEnabled()) logger.info("-- Chargement des elements de la population  "+this.getNom()); //$NON-NLS-1$
		//		MdDataSet datasetContexte = (MdDataSet)this.getDataSet();
		//
		////		 je cherche via le featureType et le schema Conceptuel si on est dans le cadre d'un
		////		 dataset particulier. Si oui je me raccroche aux populations existantes de ce dataset
		//
		//
		//		if (datasetContexte==null){
		//			if (this.getFeatureType().getSchema()!=null){
		//				if (this.getFeatureType().getSchema().getDataset()!=null){
		//					datasetContexte = this.getFeatureType().getSchema().getDataset();
		//					// ce dataset avait-il déjà des populations ?
		//
		//				}
		//				else System.out.println("Vous êtes hors du contexte d'un MdDataSet");
		//			}
		//			else System.out.println("Vous êtes hors du contexte d'un SchemaConceptuelJeu");
		//		}
		//
		//		//
		//		// j'ai trouvé le MdDataSet dans lequel je travaille.
		//		// Je regarde si ses populations ont été initialisées. Si oui,
		//		// je prends la place de l'une d'elles. Si non, je les initialise
		//		// et je prends la place de l'une d'elles.
		//		//
		if (!this.getPersistant()) {
			logger.warn("----- ATTENTION : Aucune instance n'est chargee dans la population "+this.getNom()); //$NON-NLS-1$
			logger.warn("-----             La population n'est pas persistante"); //$NON-NLS-1$
			return;
		}
		try {
			if (logger.isTraceEnabled()) logger.trace("debut"); //$NON-NLS-1$
			FT_FeatureCollection<Feat> coll = DataSet.db.loadAllFeatures(this.getFeatureType());
			if (logger.isTraceEnabled()) logger.trace("milieu"); //$NON-NLS-1$
			this.addUniqueCollection(coll);
			if (logger.isTraceEnabled()) logger.trace("fin"); //$NON-NLS-1$

		} catch (Exception e) {
			logger.error("----- ATTENTION : Chargement impossible de la population "+this.getNom()); //$NON-NLS-1$
			logger.error("-----             Sans doute un probleme avec ORACLE, ou table inexistante, ou pas de mapping "); //$NON-NLS-1$
			e.printStackTrace();
			return;
		}
		if (logger.isInfoEnabled()) logger.info("-- "+this.size()+" instances chargees dans la population"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
