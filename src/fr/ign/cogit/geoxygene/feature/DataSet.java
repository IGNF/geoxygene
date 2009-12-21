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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.metadata.MetadataManager;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.feature.type.GF_Constraint;
import fr.ign.cogit.geoxygene.schema.Produit;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.SchemaConceptuelJeu;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Classe mère pour tout jeu de données. Un DataSet peut par exemple
 * correspondre à une zone d'une BD, ou seulement un thème. Un DataSet est
 * constitué de manière récursive d'un ensemble de jeux de données, et d'un
 * ensemble de populations, elles mêmes constituées d'un ensemble d'éléments.
 * 
 * TODO Finir les annotations pour la persistance
 * 
 * @author Sébastien Mustière
 * @author Eric Grosso
 * @author Sandrine Balley
 * @author Julien Perret
 */
@Entity
public class DataSet {
	/** logger*/
	static Logger logger=Logger.getLogger(DataSet.class.getName());
	/** l'identifiant */
	protected int id;
	/**
	 * Renvoie l'identifiant
	 * @return l'identifiant
	 */
	@Id
	public int getId() {return this.id;}
	/** 
	 * Affecte un identifiant.
	 * @param Id un identifiant
	 */
	public void setId(int Id) {this.id = Id;}
	/**
	 * paramètre statique de connexion à la BD.
	 * <p>
	 * Ce paramètre est très utilisé dans GeOxygene
	 * TODO Remplacer cet attribut statique non protété par un singleton
	 */
	public static Geodatabase db;

	// /////////////////////////////////////////////////////
	// Constructeurs / Chargement / persistance
	// /////////////////////////////////////////////////////

	/** Constructeur par défaut. */
	public DataSet() {this.ojbConcreteClass = this.getClass().getName();}
	/**
	 * Constructeur par défaut, recopiant les champs de métadonnées du DataSet
	 * en paramètre sur le nouveau
	 */
	public DataSet(DataSet DS) {
		this.ojbConcreteClass = this.getClass().getName();
		if (DS == null) return;
		this.setNom(DS.getNom());
		this.setTypeBD(DS.getTypeBD());
		this.setModele(DS.getModele());
		this.setZone(DS.getZone());
		this.setDate(DS.getDate());
		this.setCommentaire(DS.getCommentaire());
	}

	/**
	 * Chargement des instances des populations persistantes d'un jeu de
	 * données.
	 */
	public void chargeElements() {
		if (!this.getPersistant()) {
			logger.warn("----- ATTENTION : Probleme au chargement du jeu de donnees " + this.getNom()); //$NON-NLS-1$
			logger.warn("----- Impossible de charger les elements d'un jeu de donnees non persistant"); //$NON-NLS-1$
			return;
		}
		// chargement recursif des dataset composants this
		for (DataSet DS:this.getComposants()) if (DS.getPersistant()) DS.chargeElements();
		// chargement recursif des populations de this
		logger.info("###### Chargement des elements du DataSet " + this.getNom()); //$NON-NLS-1$
		for (Population<? extends FT_Feature> pop:this.getPopulations()) if (pop.getPersistant()) pop.chargeElements();
	}
	/**
	 * Chargement des instances des populations persistantes d'un jeu de données
	 * qui intersectent une géométrie donnée (extraction géométrique).
	 * @param geom géométrie utilisée pour l'extraction géométrique.
	 */
	public void chargeElementsPartie(GM_Object geom) {
		if (!this.getPersistant()) {
			logger.warn("----- ATTENTION : Probleme au chargement du jeu de donnees " + this.getNom()); //$NON-NLS-1$
			logger.warn("----- Impossible de charger les elements d'un jeu de donnees non persistant"); //$NON-NLS-1$
			return;
		}
		// chargement recursif des dataset composants this
		for (DataSet DS:this.getComposants()) if (DS.getPersistant()) DS.chargeElementsPartie(geom);
		// chargement recursif des populations de this
		logger.info("###### Chargement des elements du DataSet " + this.getNom()); //$NON-NLS-1$
		for (Population<? extends FT_Feature> pop:this.getPopulations()) if (pop.getPersistant()) pop.chargeElementsPartie(geom);
	}
	/**
	 * Chargement des instances des populations persistantes d'un jeu de données
	 * qui intersectent une géométrie donnée. ATTENTION: les tables qui stockent
	 * les éléments doivent avoir été indexées dans Oracle. ATTENTION AGAIN:
	 * seules les populations avec une géométrie sont chargées.
	 * @param zoneExtraction zone utilisée pour l'extraction géométrique
	 */
	public void chargeElementsPartie(Extraction zoneExtraction) {chargeElementsPartie(zoneExtraction.getGeom());}
	/**
	 * méthode de chargement pour les test. Elle est un peu tordue dans le
	 * paramètrage mais permet de ne charger que ce qu'on veut. Elle permet de
	 * charger les instances des populations persistantes d'un jeu de données
	 * qui : - intersectent une géométrie donnée (extraction géométrique), - ET
	 * qui appartiennent à certains thèmes et populations précisés en entrée.
	 * 
	 * @param geom définit la zone d'extraction.
	 * 
	 * @param themes définit les sous-DS du DS à charger. Pour le DS lui-même, et
	 *            pour chaque sous-DS, on précise également quelles populations
	 *            sont chargées. Ce paramètre est une liste de liste de String
	 *            composée comme suit (si la liste est nulle on charge tout) :<ul>
	 * <li> 1/ Le premier élément est soit null (on charge alors toutes les 
	 * populations directement sous le DS), soit une liste contenant les noms 
	 * des populations directement sous le DS que l'on charge 
	 * (si la liste est vide, on ne charge rien).
	 * <li> 2/ Tous les autres éléments sont des listes (une pour chaque sous-DS) 
	 * qui contiennent chacune d'abord le nom d'un sous-DS que l'on veut charger,
	 * puis soit rien d'autre si on charge toutes les populations du sous-DS,
	 * soit le nom des populations du sous-DS que l'on veut charger.
	 * </ul>
	 * 
	 * <b>NB :</b> Attention aux majuscules et aux accents.
	 * <p>
	 * <b>EXEMPLE</b> de parametre themes pour un DS représentant la BDCarto, et
	 * spécifiant qu'on ne veut charger que les troncon et les noeud du thème
	 * routier, et les troncons du thème hydro, mais tout le thème ferré.
	 * <p> 
	 * <b>theme = {null, liste1, liste2, liste3}</b>, avec :
	 * <ul>
	 * <li> null car il n'y a pas de population directement sous le DS BDCarto,
	 * <li> liste1 = {"Routier","Tronçons de route", "Noeuds routier"}, 
	 * <li> liste2 = {"Hydrographie","Tronçons de cours d'eau"}, 
	 * <li> liste3 = {"ferré"}.
	 * </ul
	 */
	@SuppressWarnings("null")
	public void chargeExtractionThematiqueEtSpatiale(GM_Object geom, List<List<String>> themes) {
		if (!this.getPersistant()) {
			logger.warn("----- ATTENTION : Probleme au chargement du jeu de donnees " + this.getNom()); //$NON-NLS-1$
			logger.warn("----- Impossible de charger les elements d'un jeu de donnees non persistant"); //$NON-NLS-1$
			return;
		}

		List<String> themeACharger, extraitThemes;
		List<List<String>> populationsACharger;
		List<String> populationsACharger2;
		Iterator<List<String>> itThemes;
		Iterator<String> itPopulationsACharger;
		boolean aCharger;

		// chargement recursif des dataset composants this
		for (DataSet DS:this.getComposants()) {
			populationsACharger = null;
			if (themes == null) aCharger = true;
			else {
				itThemes = themes.iterator();
				themeACharger = itThemes.next();
				if (!itThemes.hasNext()) aCharger = true;
				else {
					aCharger = false;
					while (itThemes.hasNext()) {
						themeACharger = itThemes.next();
						if (DS.getNom().equals(themeACharger.get(0))) {
							aCharger = true;
							if (themeACharger.size() == 1) {
								populationsACharger = null;
								break;
							}
							extraitThemes = new ArrayList<String>(themeACharger);
							extraitThemes.remove(0);
							populationsACharger = new ArrayList<List<String>>();
							populationsACharger.add(extraitThemes);
							break;
						}
					}
				}
			}
			if (aCharger && DS.getPersistant())
				DS.chargeExtractionThematiqueEtSpatiale(geom, populationsACharger);
		}
		/** chargement des populations de this (directement sous this)*/
		if (themes == null) populationsACharger2 = null;
		else {
			itThemes = themes.iterator();
			populationsACharger2 = itThemes.next();
		}
		logger.info("###### Chargement des elements du DataSet " + this.getNom()); //$NON-NLS-1$
		for (Population<? extends FT_Feature> pop:this.getPopulations()) {
			if (populationsACharger2 == null) aCharger = true;
			else {
				aCharger = false;
				itPopulationsACharger = populationsACharger2.iterator();
				while (itPopulationsACharger.hasNext()) {
					String nomPopulation = itPopulationsACharger.next();
					if (pop.getNom().equals(nomPopulation)) {
						aCharger = true;
						break;
					}
				}
			}
			if (aCharger && pop.getPersistant()) {
				if (geom != null) pop.chargeElementsPartie(geom);
				else pop.chargeElements();
			}
		}
	}

	/**
	 * Pour un jeu de données persistant, détruit le jeu de données, ses thèmes
	 * et ses objets populations.
	 * <p>
	 * <b>ATTENTION :</b> ne détruit pas les éléments des populations
	 * (pour cela vider les tables Oracle).
	 */
	public void detruitJeu() {
		if (!this.getPersistant()) {
			logger.warn("----- ATTENTION : Probleme à la destruction du jeu de donnees " + this.getNom()); //$NON-NLS-1$
			logger.warn("----- Le jeu de données n'est pas persistant"); //$NON-NLS-1$
			return;
		}
		// destruction des populations de this
		for (Population<? extends FT_Feature> pop:this.getPopulations()) if (pop.getPersistant()) pop.detruitPopulation();
		// destruction recursive des dataset composants this
		for (DataSet DS:this.getComposants()) if (DS.getPersistant()) DS.detruitJeu();
		// destruction des zones d'extraction associées à this
		for (Extraction ex:this.getExtractions()) {
			logger.info("###### Destruction de la zone d'extraction " + ex.getNom()); //$NON-NLS-1$
			db.deletePersistent(ex);
		}
		// destruction de this
		logger.info("###### Destruction du DataSet " + this.getNom()); //$NON-NLS-1$
		db.deletePersistent(this);
	}

	/**
	 *  NB pour codeurs : laisser 'true' par défaut. Sinon, comme cet attribut 
	 *  n'est pas persistant, cela pose des problèmes au chargement 
	 *  (un thème persistant chargé a son attribut persistant à false.
	 */
	protected boolean persistant = true;
	/**
	 * Booléen spécifiant si le thème est persistant ou non (vrai par défaut).
	 * <p>
	 * <b>NB :</b> si un jeu de données est non persistant, tous ses thèmes sont non
	 * persistants. Mais si un jeu de données est persistant, certains de ses
	 * thèmes peuvent ne pas l'être.
	 * <p>
	 * <b>ATTENTION :</b> pour des raisons propres à OJB, même si la classe DataSet est
	 * concrète, il n'est pas possible de créer un objet PERSISTANT de cette
	 * classe, il faut utiliser les sous-classes.
	 * @return vrai si le jeu de donné est persistant, faux sinon
	 */
	public boolean getPersistant() {return this.persistant;}
	/**
	 * Booléen spécifiant si le thème est persistant ou non (vrai par défaut).
	 * <p>
	 * <b>NB :</b> si un jeu de données est non persistant, tous ses thèmes sont non
	 * persistants. Mais si un jeu de données est persistant, certains de ses
	 * thèmes peuvent ne pas l'être.
	 * <p>
	 * <b>ATTENTION :</b> pour des raisons propres à OJB, même si la classe DataSet est
	 * concrète, il n'est pas possible de créer un objet PERSISTANT de cette
	 * classe, il faut utiliser les sous-classes.
	 * @param b vrai si le jeu de donné est persistant, faux sinon
	 */
	public void setPersistant(boolean b) {this.persistant = b;}

	// /////////////////////////////////////////////////////
	// Metadonnées
	// /////////////////////////////////////////////////////
	/**
	 * Nom de la classe concrète de this : pour OJB, ne pas manipuler
	 * directement
	 */
	protected String ojbConcreteClass;
	public String getOjbConcreteClass() {return this.ojbConcreteClass;}
	public void setOjbConcreteClass(String S) {this.ojbConcreteClass = S;}
	
	/** Nom du jeu de données */
	protected String nom = ""; //$NON-NLS-1$
	public String getNom() {return this.nom;}
	public void setNom(String S) {this.nom = S;}
	
	/** Type de BD (BDcarto, BDTopo...). */
	protected String typeBD = ""; //$NON-NLS-1$
	public String getTypeBD() {return this.typeBD;}
	public void setTypeBD(String S) {this.typeBD = S;}
	
	/** Modèle utilisé (format shape, structuré...). */
	protected String modele = ""; //$NON-NLS-1$
	public String getModele() {return this.modele;}
	public void setModele(String S) {this.modele = S;}

	/** Zone Géographique couverte. */
	protected String zone = ""; //$NON-NLS-1$
	public String getZone() {return this.zone;}
	public void setZone(String S) {this.zone = S;}

	/** Date des données. */
	protected String date = ""; //$NON-NLS-1$
	public String getDate() {return this.date;}
	public void setDate(String S) {this.date = S;}

	/** Commentaire quelconque. */
	protected String commentaire = ""; //$NON-NLS-1$
	public String getCommentaire() {return this.commentaire;}
	public void setCommentaire(String S) {this.commentaire = S;}

	// /////////////////////////////////////////////////////
	// thèmes du jeu de données
	// /////////////////////////////////////////////////////
	/**
	 * Un DataSet se décompose récursivement en un ensemble de DataSet. Le lien
	 * de DataSet vers lui-même est un lien 1-n. Les méthodes get (sans indice)
	 * et set sont nécessaires au mapping. Les autres méthodes sont là seulement
	 * pour faciliter l'utilisation de la relation. ATTENTION: Pour assurer la
	 * bidirection, il faut modifier les listes uniquement avec ces methodes.
	 * NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas
	 * "null". Pour casser toutes les relations, faire setListe(new ArrayList())
	 * ou emptyListe().
	 */
	protected List<DataSet> composants = new ArrayList<DataSet>();
	/** Récupère la liste des DataSet composant this. */
	@OneToMany
	public List<DataSet> getComposants() {return this.composants;}
	/**
	 * définit la liste des DataSet composant le DataSet, et met à jour la
	 * relation inverse.
	 */
	public void setComposants(List<DataSet> L) {
		emptyComposants();
		for(DataSet dataset:L) dataset.setAppartientA(this);
	}

	/** Récupère le ième élément de la liste des DataSet composant this. */
	public DataSet getComposant(int i) {return this.composants.get(i);}
	/**
	 * Ajoute un objet à la liste des DataSet composant le DataSet, et met à
	 * jour la relation inverse.
	 */
	public void addComposant(DataSet O) {
		if (O == null) return;
		this.composants.add(O);
		O.setAppartientA(this);
	}
	/**
	 * enlève un élément de la liste DataSet composant this, et met à jour la
	 * relation inverse.
	 */
	public void removeComposant(DataSet O) {
		if (O == null) return;
		this.composants.remove(O);
		O.setAppartientA(null);
	}
	/**
	 * Vide la liste des DataSet composant this, et met à jour la relation
	 * inverse.
	 */
	public void emptyComposants() {
		List<DataSet> old = new ArrayList<DataSet>(this.composants);
		for(DataSet dataset:old) dataset.setAppartientA(null);
		this.composants.clear();
	}
	/** Récupère le DataSet composant de this avec le nom donné. 
	 * @param nomComposant nom du dataset à Récupèrer
	 * @return le DataSet composant de this avec le nom donné.
	 */
	public DataSet getComposant(String nomComposant) {
		for(DataSet dataset:this.getComposants()) if (dataset.getNom().equals(nomComposant)) return dataset;
		logger.warn("----- ATTENTION : DataSet composant #" + nomComposant + "# introuvable dans le DataSet " + this.getNom()); //$NON-NLS-1$ //$NON-NLS-2$
		return null;
	}

	/** Relation inverse à Composants */
	private DataSet appartientA;
	/** Récupère le DataSet dont this est composant. */
	@ManyToOne
	public DataSet getAppartientA() {return this.appartientA;}
	/**
	 * définit le DataSet dont this est composant., et met à jour la relation inverse.
	 */
	public void setAppartientA(DataSet O) {
		DataSet old = this.appartientA;
		this.appartientA = O;
		if (old != null) old.getComposants().remove(this);
		if (O != null) {
			this.appartientAID = O.getId();
			if (!(O.getComposants().contains(this))) O.getComposants().add(this);
		} else this.appartientAID = 0;
	}
	private int appartientAID;
	/** Ne pas utiliser, necessaire au mapping OJB */
	public void setAppartientAID(int I) {this.appartientAID = I;}
	/** Ne pas utiliser, necessaire au mapping OJB */
	@Transient
	public int getAppartientAID() {return this.appartientAID;}

	/**
	 * Liste des population du DataSet. Les méthodes get (sans indice) et set
	 * sont nécessaires au mapping. Les autres méthodes sont là seulement pour
	 * faciliter l'utilisation de la relation. 
	 * <p>
	 * <b>ATTENTION :</b> Pour assurer la bidirection, il faut modifier 
	 * les listes uniquement avec ces methodes.
	 * <p>
	 * <b>NB :</b> si il n'y a pas d'objet en relation, la liste est vide mais n'est pas
	 * "null". Pour casser toutes les relations, faire setListe(new ArrayList())
	 * ou emptyListe().
	 */
	protected List<Population<? extends FT_Feature>> populations = new ArrayList<Population<? extends FT_Feature>>();
	/** Récupère la liste des populations en relation. */
	@OneToMany
	public List<Population<? extends FT_Feature>> getPopulations() {return this.populations;}
	/**
	 * définit la liste des populations en relation, et met à jour la relation
	 * inverse.
	 */
	public void setPopulations(List<Population<? extends FT_Feature>> L) {
		List<Population<? extends FT_Feature>> old = new ArrayList<Population<? extends FT_Feature>>(this.populations);
		for (Population<? extends FT_Feature> pop:old) pop.setDataSet(null);
		for (Population<? extends FT_Feature> pop:L) pop.setDataSet(this);
	}
	/** Récupère le ième élément de la liste des populations en relation. */
	public Population<? extends FT_Feature> getPopulation(int i) {return this.populations.get(i);}
	/**
	 * Ajoute un objet à la liste des populations en relation, et met à jour la
	 * relation inverse.
	 */
	public void addPopulation(Population<? extends FT_Feature> O) {
		if (O == null) return;
		this.populations.add(O);
		O.setDataSet(this);
	}
	/**
	 * enlève un élément de la liste des populations en relation, et met à jour
	 * la relation inverse.
	 * @param O élément à enlever
	 */
	public void removePopulation(Population<? extends FT_Feature> O) {
		if (O == null) return;
		this.populations.remove(O);
		O.setDataSet(null);
	}
	/**
	 * Vide la liste des populations en relation, et met à jour la relation
	 * inverse.
	 */
	public void emptyPopulations() {
		List<Population<? extends FT_Feature>> old = new ArrayList<Population<? extends FT_Feature>>(this.populations);
		for (Population<? extends FT_Feature> pop:old) pop.setDataSet(null);
		this.populations.clear();
	}

	/** Récupère la population avec le nom donné.
	 * @param nomPopulation nom de la population à Récupèrer
	 * @return la population avec le nom donné.
	 */
	public Population<? extends FT_Feature> getPopulation(String nomPopulation) {
		for (Population<? extends FT_Feature> pop:this.getPopulations()) if (pop.getNom().equals(nomPopulation))	return pop;
		//if (logger.isDebugEnabled()) logger.debug("=============== ATTENTION : population '" + nom + "' introuvable ==============");
		return null;
	}

	/** Liste des zones d'extraction définies pour ce DataSt */
	protected List<Extraction> extractions = new ArrayList<Extraction>();
	/** Récupère la liste des extractions en relation. */
	//@OneToMany
	@Transient
	public List<Extraction> getExtractions() {return this.extractions;}
	/** définit la liste des extractions en relation. */
	public void setExtractions(List<Extraction> L) {this.extractions = L;}
	/** Ajoute un élément de la liste des extractions en relation. */
	public void addExtraction(Extraction O) {this.extractions.add(O);}

	// ////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * méthodes permettant de créer un jeu de données: <ul> <li> relié à un
	 * produit, donc potentiellement à de nombreuses métadonnées </li> <li>
	 * relié à 0 ou 1 schémaConceptuelJeu (un schémaConceptuelJeu est associé à
	 * 0 ou 1 jeu)</li> <li> composé de Populations dotées de métadonnées </li>
	 * </ul>
	 * 
	 * Comme indiqué dans la classe Population, les populations d'un DataSet ne
	 * sont pas destinées à être persistantes. Elles peuvent être initialisées à
	 * partir du schéma conceptuel, qui lui est persitent, grâce à la méthode
	 * DataSet.initPopulations()
	 */
	// //////////////////////////////////////////////////////////////////////////////////////////////

	/** *reference statique au repository OJB */
	public static MetadataManager metadataManager;
	/***************************************************************************
	 * Partie Description du DataSet : produit et schéma de données
	 **************************************************************************/
	protected Produit produit;
	/**
	 * @return the produit
	 */
	//@OneToOne
	@Transient
	public Produit getProduit() {return this.produit;}
	/**
	 * @param produit the produit to set
	 */
	public void setProduit(Produit produit) {this.produit = produit;}
	/**
	 * Schema conceptuel correspondant au jeu de donnees
	 */
	protected SchemaConceptuelJeu schemaConceptuel;
	/**
	 * Affecte le schema conceptuel correspondant au jeu de donnees
	 * @param schema le schema conceptuel correspondant au jeu de donnees
	 */
	public void setSchemaConceptuel(SchemaConceptuelJeu schema) {this.schemaConceptuel = schema;}
	/**
	 * Renvoie le schema conceptuel correspondant au jeu de donnees
	 * @return le schema conceptuel correspondant au jeu de donnees
	 */
	//@OneToOne
	@Transient
	public SchemaConceptuelJeu getSchemaConceptuel() {return this.schemaConceptuel;}
	/**
	 * Liste des contraintes (intégrité) s'appliquant à ce jeu
	 */
	public List<GF_Constraint> contraintes;
	/**
	 * Renvoie la liste des contraintes d'integrite s'appliquant a ce jeu de donnees
	 * @return liste des contraintes d'integrite s'appliquant a ce jeu de donnees
	 */
	//@OneToMany
	@Transient
	public List<GF_Constraint> getContraintes() {return this.contraintes;}
	/**
	 * Affecte la liste des contraintes d'integrite s'appliquant a ce jeu de donnees
	 * @param contraintes la liste des contraintes d'integrite s'appliquant a ce jeu de donnees
	 */
	public void setContraintes(List<GF_Constraint> contraintes) {this.contraintes = contraintes;}

	/***************************************************************************
	 * Partie Utilisation du DataSet : les données peuvent être accedées via des
	 * FT_Collection et via des Populations.
	 **************************************************************************/
	/**
	 * initialise la liste des populations du jeu en fonction du schéma
	 * conceptuel. Les données ne sont pas chargées.
	 */
	public void initPopulations() {
		SchemaConceptuelJeu schema = this.getSchemaConceptuel();
		List<Population<? extends FT_Feature>> listPop = new ArrayList<Population<? extends FT_Feature>>();
		for (int i = 0; i < schema.getFeatureTypes().size(); i++) {
			listPop.add(new Population<FT_Feature>((FeatureType) schema.getFeatureTypeI(i)));
		}
		this.setPopulations(listPop);
	}

	/**
	 * @param nomFeatureType nom du featuretype
	 * @return population dont le featuretype correspond au nom donné
	 */
	public Population<? extends FT_Feature> getPopulationByFeatureTypeName(String nomFeatureType) {
		for (int i = 0; i < this.getPopulations().size(); i++) {
			if (this.getPopulations().get(i).getFeatureType().getTypeName().equals(nomFeatureType)) {
				return this.getPopulations().get(i);
			}
		}
		logger.error("La Population " + nomFeatureType + " n'a pas été trouvée.");  //$NON-NLS-1$//$NON-NLS-2$
		return null;
	}

	private static DataSet dataSet = null;
	/**
	 * @return une instance du singleton DataSet
	 */
	public static DataSet getInstance() {
		if (dataSet==null) synchronized(DataSet.class) {if (dataSet==null) dataSet=new DataSet();}
		return dataSet;
	}
	
}
