/*******************************************************************************
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
 *******************************************************************************/
/**
 *
 */
package fr.ign.cogit.appli.geopensim.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.persistence.Transient;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.event.AgentCollectionEvent;
import fr.ign.cogit.appli.geopensim.agent.event.AgentCollectionListener;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentAlignement;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentGroupeBatiments;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentUniteBatie;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneAgregee;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentEspaceVide;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTroncon;
import fr.ign.cogit.appli.geopensim.contrainte.Contrainte;
import fr.ign.cogit.appli.geopensim.evolution.EvolutionRule;
import fr.ign.cogit.appli.geopensim.feature.Changement;
import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicBatiment;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicTronconChemin;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicTronconCoursEau;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicTronconRoute;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicTronconVoieFerree;
import fr.ign.cogit.appli.geopensim.feature.macro.PopulationUnites;
import fr.ign.cogit.appli.geopensim.feature.meso.Alignement;
import fr.ign.cogit.appli.geopensim.feature.meso.GroupeBatiments;
import fr.ign.cogit.appli.geopensim.feature.meso.UniteUrbaine;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaire;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.appli.geopensim.feature.micro.EspaceVide;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconChemin;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconCoursEau;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconRoute;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconVoieFerree;
import fr.ign.cogit.appli.geopensim.scheduler.Scheduler;
import fr.ign.cogit.geoxygene.api.feature.event.FeatureCollectionListener;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.MathUtil;

/**
 * Collection d'éléments Géographiques.
 * @author Julien Perret
 */
public class AgentGeographiqueCollection implements Collection<AgentGeographique>, Agent {
	static Logger logger=Logger.getLogger(AgentGeographiqueCollection.class.getName());

	public AgentGeographiqueCollection() {
	  FeatureType featureType = new FeatureType();
	  featureType.addFeatureAttribute(new AttributeType("idGeo", "int"));
	  featureType.addFeatureAttribute(new AttributeType("densite", "double"));
      featureType.addFeatureAttribute(new AttributeType("densiteBut", "double"));
      featureType.addFeatureAttribute(new AttributeType("densiteMax", "double"));
      featureType.addFeatureAttribute(new AttributeType("weighedDensity", "double"));
      featureType.addFeatureAttribute(new AttributeType("classificationFonctionnelle", "int"));
      featureType.addFeatureAttribute(new AttributeType("classificationFonctionnelleBut", "int"));
      featureType.addFeatureAttribute(new AttributeType("classificationFonctionnelleMajo", "int"));
      featureType.addFeatureAttribute(new AttributeType("satisfaction", "double"));
      featureType.addFeatureAttribute(new AttributeType("normalLaw", "double"));
      featureType.addFeatureAttribute(new AttributeType("satisfactionComposants", "double"));
      featureType.addFeatureAttribute(new AttributeType("satisfactionProximity", "double"));
      featureType.addFeatureAttribute(new AttributeType("satisfactionTypesBatiments", "double"));
      featureType.addFeatureAttribute(new AttributeType("methodePeuplement", "String"));
      featureType.addFeatureAttribute(new AttributeType("distanceALaRoute", "double"));
      featureType.addFeatureAttribute(new AttributeType("distanceMoyennePlusProcheBatiment", "double"));
	  this.zonesElementairesBaties.setFeatureType(featureType);
	}
	protected List<AgentCollectionListener> listenerList
	= new ArrayList<AgentCollectionListener>(0);

	/**
	 * Ajoute un {@link FeatureCollectionListener}.
	 * <p>
	 * Adds a {@link FeatureCollectionListener}.
	 * @param l
	 *            le {@link FeatureCollectionListener} à ajouter. the
	 *            {@link FeatureCollectionListener} to be added.
	 */
	public void addAgentCollectionListener(AgentCollectionListener l) {
		this.listenerList.add(l);
	}
	public void removeAgentCollectionListener(AgentCollectionListener l) {
		this.listenerList.remove(l);
	}

	public void fireActionPerformed(AgentCollectionEvent event) {
		// Guaranteed to return a non-null array
		AgentCollectionListener[] listeners = this.listenerList
		.toArray(new AgentCollectionListener[0]);
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 1; i >= 0; i -= 1) {
			listeners[i].changed(event);
		}
	}

	private static AgentGeographiqueCollection instance;
	public static AgentGeographiqueCollection getInstance() {
	    if (instance == null) {
	      synchronized (AgentGeographiqueCollection.class) {
	        if (instance == null) {
	          instance = new AgentGeographiqueCollection();
	        }
	      }
	    }
	    return instance;
	}

	private boolean extraction = false;
	/**
	 * Renvoie vrai si les données chargées sont extraites d'une base plus grande, faux sinon.
	 * @return vrai si les données chargées sont extraites d'une base plus grande, faux sinon.
	 */
	public boolean isExtraction() {return this.extraction;}
	/**
	 * Affecte vrai si les données chargées sont extraites d'une base plus grande, faux sinon.
	 * @param extraction vrai si les données chargées sont extraites d'une base plus grande, faux sinon.
	 */
	public void setExtraction(boolean extraction) {this.extraction = extraction;}
	protected Map<Class<?>,SortedSet<Integer>> classDates = new HashMap<Class<?>,SortedSet<Integer>>();

	/*
	protected Class<?>[] classes = {
			Batiment.class,
			Cimetiere.class,
			Parking.class,
			SurfaceEau.class,
			TerrainSport.class,
			TronconChemin.class,
			TronconCoursEau.class,
			TronconRoute.class,
			TronconVoieFerree.class,
			Vegetation.class};
	 */

	protected Population<AgentUniteBatie> unitesBaties = new Population<AgentUniteBatie>("UniteBatie");
	/**
	 * Renvoie la valeur de l'attribut unitesBaties.
	 * @return la valeur de l'attribut unitesBaties
	 */
	public Population<AgentUniteBatie> getUnitesBaties() {return this.unitesBaties;}
	/**
	 * Affecte la valeur de l'attribut unitesBaties.
	 * @param unitesBaties l'attribut unitesBaties à affecter
	 */
	public void setUnitesBaties(Population<AgentUniteBatie> unitesBaties) {this.unitesBaties = unitesBaties;}

	protected Population<AgentUniteBatie> uniteNonBatie = new Population<AgentUniteBatie>("UniteNonBatie");
	/**
	 * Renvoie la valeur de l'attribut uniteNonBatie.
	 * @return la valeur de l'attribut uniteNonBatie
	 */
	public Population<AgentUniteBatie> getUniteNonBatie() {return this.uniteNonBatie;}
	/**
	 * Affecte la valeur de l'attribut uniteNonBatie.
	 * @param uniteNonBatie l'attribut uniteNonBatie à affecter
	 */
	public void setUniteNonBatie(Population<AgentUniteBatie> uniteNonBatie) {this.uniteNonBatie = uniteNonBatie;}

	protected Population<AgentZoneAgregee> zonesAgregees = new Population<AgentZoneAgregee>("ZoneAgregee");
	/**
	 * Renvoie la valeur de l'attribut zonesAgregees.
	 * @return la valeur de l'attribut zonesAgregees
	 */
	public Population<AgentZoneAgregee> getZonesAgregees() {return this.zonesAgregees;}
	/**
	 * Affecte la valeur de l'attribut zonesAgregees.
	 * @param zonesAgregees l'attribut zonesAgregees à affecter
	 */
	public void setZonesAgregees(Population<AgentZoneAgregee> zonesAgregees) {this.zonesAgregees = zonesAgregees;}

	protected Population<AgentZoneElementaireBatie> zonesElementairesBaties = new Population<AgentZoneElementaireBatie>("ZoneElementaireBatie");
	/**
	 * Renvoie la valeur de l'attribut zonesElementaires.
	 * @return la valeur de l'attribut zonesElementaires
	 */
	public Population<AgentZoneElementaireBatie> getZonesElementairesBaties() {return this.zonesElementairesBaties;}
	/**
	 * Affecte la valeur de l'attribut zonesElementaires.
	 * @param zonesElementaires l'attribut zonesElementaires à affecter
	 */
	public void setZonesElementairesBaties(Population<AgentZoneElementaireBatie> zonesElementaires) {this.zonesElementairesBaties = zonesElementaires;}

	protected Population<AgentZoneElementaireBatie> zonesElementairesNonBaties = new Population<AgentZoneElementaireBatie>("ZoneElementaireNonBatie");
	/**
	 * Renvoie la valeur de l'attribut zonesElementairesNonBaties.
	 * @return la valeur de l'attribut zonesElementairesNonBaties
	 */
	public Population<AgentZoneElementaireBatie> getZonesElementairesNonBaties() {return this.zonesElementairesNonBaties;}
	/**
	 * Affecte la valeur de l'attribut zonesElementairesNonBaties.
	 * @param zonesElementairesNonBaties l'attribut zonesElementairesNonBaties à affecter
	 */
	public void setZonesElementairesNonBaties(Population<AgentZoneElementaireBatie> zonesElementairesNonBaties) {this.zonesElementairesNonBaties = zonesElementairesNonBaties;}

	protected Population<AgentBatiment> batiments = new Population<AgentBatiment>("Batiment");
	/**
	 * Renvoie la valeur de l'attribut batiments.
	 * @return la valeur de l'attribut batiments
	 */
	public Population<AgentBatiment> getBatiments() {return this.batiments;}
	/**
	 * Affecte la valeur de l'attribut batiments.
	 * @param batiments l'attribut batiments à affecter
	 */
	public void setBatiments(Population<AgentBatiment> batiments) {this.batiments = batiments;}

	protected Population<AgentTroncon> tronconsCoursEau = new Population<AgentTroncon>("TronconCoursEau");
	/**
	 * Renvoie la valeur de l'attribut tronconsCoursEau.
	 * @return la valeur de l'attribut tronconsCoursEau
	 */
	public Population<AgentTroncon> getTronconsCoursEau() {return this.tronconsCoursEau;}
	/**
	 * Affecte la valeur de l'attribut tronconsCoursEau.
	 * @param tronconsCoursEau l'attribut tronconsCoursEau à affecter
	 */
	public void setTronconsCoursEau(Population<AgentTroncon> tronconsCoursEau) {this.tronconsCoursEau = tronconsCoursEau;}

	protected Population<AgentTroncon> tronconsChemin = new Population<AgentTroncon>("TronconChemin");
	/**
	 * Renvoie la valeur de l'attribut tronconsChemin.
	 * @return la valeur de l'attribut tronconsChemin
	 */
	public Population<AgentTroncon> getTronconsChemin() {return this.tronconsChemin;}
	/**
	 * Affecte la valeur de l'attribut tronconsChemin.
	 * @param tronconsChemin l'attribut tronconsChemin à affecter
	 */
	public void setTronconsChemin(Population<AgentTroncon> tronconsChemin) {this.tronconsChemin = tronconsChemin;}

	protected Population<AgentTroncon> tronconsRoute = new Population<AgentTroncon>("TronconRoute");
	/**
	 * Renvoie la valeur de l'attribut tronconsRoute.
	 * @return la valeur de l'attribut tronconsRoute
	 */
	public Population<AgentTroncon> getTronconsRoute() {return this.tronconsRoute;}
	/**
	 * Affecte la valeur de l'attribut tronconsRoute.
	 * @param tronconsRoute l'attribut tronconsRoute à affecter
	 */
	public void setTronconsRoute(Population<AgentTroncon> tronconsRoute) {this.tronconsRoute = tronconsRoute;}

	protected Population<AgentTroncon> tronconsVoieFerree = new Population<AgentTroncon>("TronconVoieFerree");
	/**
	 * Renvoie la valeur de l'attribut tronconsVoieFerree.
	 * @return la valeur de l'attribut tronconsVoieFerree
	 */
	public Population<AgentTroncon> getTronconsVoieFerree() {return this.tronconsVoieFerree;}
	/**
	 * Affecte la valeur de l'attribut tronconsVoieFerree.
	 * @param tronconsVoieFerree l'attribut tronconsVoieFerree à affecter
	 */
	public void setTronconsVoieFerree(Population<AgentTroncon> tronconsVoieFerree) {this.tronconsVoieFerree = tronconsVoieFerree;}

	protected Population<AgentEspaceVide> espacesVides = new Population<AgentEspaceVide>("EspaceVide");
	/**
	 * Renvoie la valeur de l'attribut espacesVides.
	 * @return la valeur de l'attribut espacesVides
	 */
	public Population<AgentEspaceVide> getEspacesVides() {return this.espacesVides;}
	/**
	 * Affecte la valeur de l'attribut espacesVides.
	 * @param espacesVides l'attribut espacesVides à affecter
	 */
	public void setEspacesVides(Population<AgentEspaceVide> espacesVides) {this.espacesVides = espacesVides;}

	protected Population<AgentGroupeBatiments> groupesBatiments = new Population<AgentGroupeBatiments>("GroupeBatiments");
	/**
	 * Renvoie la valeur de l'attribut groupesBatiments.
	 * @return la valeur de l'attribut groupesBatiments
	 */
	public Population<AgentGroupeBatiments> getGroupesBatiments() {return this.groupesBatiments;}
	/**
	 * Affecte la valeur de l'attribut batiments.
	 * @param groupesBatiments l'attribut groupesBatiments à affecter
	 */
	public void setGroupesBatiments(Population<AgentGroupeBatiments> groupesBatiments) {this.groupesBatiments = groupesBatiments;}

	protected Population<AgentAlignement> alignements = new Population<AgentAlignement>("Alignement");
	/**
	 * Renvoie la valeur de l'attribut alignements.
	 * @return la valeur de l'attribut alignements
	 */
	public Population<AgentAlignement> getAlignements() {return this.alignements;}
	/**
	 * Affecte la valeur de l'attribut alignements.
	 * @param groupesBatiments l'attribut alignements à affecter
	 */
	public void setAlignements(Population<AgentAlignement> alignements) {this.alignements = alignements;}
	
	protected Map<Integer,AgentGeographique> elements = new HashMap<Integer,AgentGeographique>();
	/**
	 * @return objet Géographiques de la collection
	 */
	public Map<Integer, AgentGeographique> getElements() {return elements;}
	/**
	 * @param elements objet Géographiques à affecter à la collection
	 */
	public void setElements(Map<Integer, AgentGeographique> elements) {this.elements = elements;}

	/**
	 * Charge les éléments Géographiques depuis la base de données et lance leur analyse.
	 * Les représentations associées à ces éléments sont chargées par la même occasion.
	 * @see AgentGeographiqueCollection#analyserElements()
	 */
	public void chargerPopulations() {
		long time = System.currentTimeMillis();
		if (logger.isInfoEnabled()) logger.info("Chargement des agents");
		List<AgentGeographique> agentsGeo = DataSet.db.loadAll(AgentGeographique.class);
		if (agentsGeo == null) {
			logger.error("Echec du chargement des agents Géographiques");
			return;
		}
		for(AgentGeographique element:agentsGeo) {
			if (logger.isTraceEnabled())logger.trace("Chargement de l'élément "+element);
			elements.put(element.getIdGeo(), element);
		}
		if (logger.isInfoEnabled()) logger.info("Fin du chargement : "+elements.size()+" elements chargés. Ca a pris "+(System.currentTimeMillis()-time)+" ms");
		/**
		 * FIXME normalement, l'analyse est faite à la création, pas besoin de la refaire au chargement.
		 * Par contre, on en a encore besoin à cause de l'analyse des dates !!!
		 * A séparer absolument !
		 */
		this.analyserElements();
		SortedSet<Integer> datesUnites = this.getClassDates().get(UniteUrbaine.class);
		if (datesUnites == null) { return; }
		for(Integer date:datesUnites) {
			Set<UniteUrbaine> unites = new HashSet<UniteUrbaine>(0);
			List<AgentGeographique> agents = this.getElementsGeo(UniteUrbaine.class, date);
			for(AgentGeographique agent:agents) {
				for(ElementRepresentation rep:agent.getElements()) {
					if (logger.isDebugEnabled()) {
					  logger.debug(rep);
					}
				}
				UniteUrbaine unite = (UniteUrbaine) agent.getRepresentation(date);
				//unite.qualifier();
				unites.add(unite);
			}
			PopulationUnites popUnites = new PopulationUnites(BasicBatiment.class,BasicTronconRoute.class, BasicTronconChemin.class, BasicTronconVoieFerree.class, BasicTronconCoursEau.class,date);
			popUnites.setElements(new FT_FeatureCollection<UniteUrbaine>(unites));
			this.populationsUnites.add(popUnites);
			agents = this.getElementsGeo(TronconChemin.class, date);
			for (AgentGeographique agent:agents) {
			  this.tronconsChemin.add((AgentTroncon) agent);
			}
			agents = this.getElementsGeo(TronconCoursEau.class, date);
			for (AgentGeographique agent:agents) {
			  this.tronconsCoursEau.add((AgentTroncon) agent);
			}
			agents = this.getElementsGeo(TronconRoute.class, date);
			for (AgentGeographique agent:agents) {
			  this.tronconsRoute.add((AgentTroncon) agent);
			}
			agents = this.getElementsGeo(TronconVoieFerree.class, date);
			for (AgentGeographique agent:agents) {
			  this.tronconsVoieFerree.add((AgentTroncon) agent);
			}
			agents = this.getElementsGeo(Batiment.class, date);
			for (AgentGeographique agent:agents) {
			  this.batiments.add((AgentBatiment) agent);
			}
			agents = this.getElementsGeo(GroupeBatiments.class, date);
			for (AgentGeographique agent:agents) {
			  this.groupesBatiments.add((AgentGroupeBatiments) agent);
			}
		}
		if (logger.isInfoEnabled()) logger.info("Fin du chargement et de l'analyse des agents. Ca a pris "+(System.currentTimeMillis()-time)+" ms");
		// TODO reconstruire le reste des populations d'Unités et le reste de la collection d'agents
	}

	/**
	 * Analyse les éléments Géographiques de la collection.
	 * Pour ce faire, on construit d'abord la liste des dates utilisées pour la saisie
	 * des représentations de chaque classe (bâtiments, routes, etc.), puis,
	 * pour chaque élément Géographique, on vérifie s'il possède des représentations à chaque date
	 * associée à sa classe.
	 * Il est important, pour bien évaluer les dates de création et de destruction des éléments Géographiques,
	 * que la même zone Géographique soit couverte à chaque date de saisie.
	 */
	public void analyserElements() {
		long time = System.currentTimeMillis();
		// mise à jour des dates des sources pour chaque classe d'éléments
		if (logger.isInfoEnabled()) logger.info("Analyse des "+elements.size()+" elements");
		for(AgentGeographique element:elements.values()) {
			Class<?> classe = element.getRepresentationClass();
			SortedSet<Integer> dates = classDates.get(classe);
			if (dates == null) {
				dates = new TreeSet<Integer>();
				classDates.put(classe,dates);
			}
			element.analyser();
			//ajoutDates(dates,element.getDates());
			dates.addAll(element.getDates());
		}
		// mise à jour des dates de création et de destruction des éléments en fonction des sources des populations
		for(AgentGeographique element:elements.values()) {
			Class<?> classe = element.getRepresentationClass();
			SortedSet<Integer> dates = classDates.get(classe);
			if (dates.isEmpty()) {
				logger.error("pas de dates trouvées pour la classe "+classe.getName());
				break;
			}
			if (element.getDates().isEmpty()) {
				logger.error("pas de dates trouvées pour l'agent "+element);
				break;
			}
			Integer[] datesAsArray = element.getDates().toArray(new Integer[element.getDates().size()]);
			int minDate = datesAsArray[0];
			int maxDate = datesAsArray[datesAsArray.length-1];
            List<Integer> classDatesAsList = new ArrayList<Integer>(dates);
			if (minDate>classDatesAsList.get(0)) {
				int index = classDatesAsList.indexOf(minDate);
				element.getCreation().setMin(classDatesAsList.get(index-1));
				element.getCreation().setMax(classDatesAsList.get(index));
			} else {
				element.getCreation().setMax(minDate);
			}
			if (maxDate<classDatesAsList.get(dates.size()-1)) {
				int index = classDatesAsList.indexOf(maxDate);
				element.getDestruction().setMin(classDatesAsList.get(index));
				element.getDestruction().setMax(classDatesAsList.get(index+1));
			} else {
				element.getDestruction().setMin(maxDate);
			}
		}
		/*
		for(AgentGeographique element:elements.values()) {
			if (!(element instanceof AgentZoneElementaire)) element.analyserChangements();
			// TODO réactiver l'analyse des changements
		}
		*/
		if (logger.isInfoEnabled()) logger.info("Fin de l'analyse des "+elements.size()+" elements chargés. Ca a pris "+(System.currentTimeMillis()-time)+" ms");
	}

	/**
	 * Ajoute les dates d'un élément Géographique aux dates associées à sa classe.
	 * On considére que les dates de sa classe sont déjà triés de façon ascendante.
	 * @param datesClasse dates associées aux éléments de même classe que l'élément traité.
	 * @param datesElement dates associées à l'élément traité.
	 */
	private void ajoutDates(List<Integer> datesClasse, List<Integer> datesElement) {
		for (Integer date:datesElement) {
			if (!datesClasse.contains(date)) {
				if (datesClasse.isEmpty()) {
					datesClasse.add(date);
				} else {
					boolean trouve = false;
					for(int index = 0 ; (index < datesClasse.size())&&!trouve ; index++) {
						if (date<datesClasse.get(index)) {
							datesClasse.add(index, date);
							trouve = true;
						}
					}
					if (!trouve&&date>datesClasse.get(datesClasse.size()-1))
						datesClasse.add(date);
				}
			}
		}
	}

	/**
	 * Renvoie la classe mère de tous les objets du type que la classe passée en paramètre.
	 * @param classe classe d'élément Géographique
	 * @return la classe mère de tous les objets du type que la classe
	 */
	/*
	public Class<?> getMicroClass(Class<? extends ElementGeo> classe) {
		for (int i = 0 ; i < classes.length ; i++) {
			if (classes[i].isAssignableFrom(classe)) {
				return classes[i];
			}
		}
		return null;
	}
	 */

	/**
	 * Rend les éléments Géographiques persistants.
	 */
	public void rendElementsPersistants() {
		DataSet.db.begin();
		this.makePersistent();
		DataSet.db.commit();
	}
	public void makePersistent() {
        for(AgentGeographique agent:elements.values()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Rend persistant "+agent);
            }
            for (ElementRepresentation representation:agent.getElements()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Rend persistant "+representation);
                    logger.debug("\t class "+representation.getClass());
                }
                DataSet.db.makePersistent(representation);
            }
            DataSet.db.makePersistent(agent);
        }
	}

	/**
	 * Renvoie l'élément Géographique d'identifiant idGeo.
	 * @param idGeo identifiant de l'élément Géographique recherché
	 * @return l'élément Géographique d'identifiant idGeo
	 */
	public AgentGeographique get(long idGeo) {return elements.get(idGeo);}
	public boolean add(AgentGeographique e) {
		elements.put(e.getIdGeo(), e);
		return true;
	}
	public boolean addAll(Collection<? extends AgentGeographique> c) {
		for(AgentGeographique e:c) add(e);
		return true;
	}
	public void clear() {elements.clear();}
	public boolean contains(Object o) {return elements.get(o)!=null;}
	public boolean containsAll(Collection<?> c) {
		for(Object o:c) {if (!contains(o))return false;}
		return true;
	}
	public boolean isEmpty() {return elements.isEmpty();}
	public Iterator<AgentGeographique> iterator() {return elements.values().iterator();}
	public boolean remove(Object o) {return elements.remove(((AgentGeographique)o).getIdGeo())!=null;}
	public boolean removeAll(Collection<?> c) {
		boolean result = true;
		for(Object o:c) {result = result&&remove(o);}
		return result;
	}
	public boolean retainAll(Collection<?> c) {
		boolean result = false;
		for(AgentGeographique e:this) {if (!c.contains(e)) {result = result|remove(e);}}
		return result;
	}
	public int size() {return elements.size();}
	public Object[] toArray() {return elements.values().toArray();}
	public <T> T[] toArray(T[] a) {return elements.values().toArray(a);}
	@Override
	public String toString() {
		String result = "";
		for (AgentGeographique element:this) {result+=element.toString();}
		return result;
	}
	//	/**
	//	 * Renvoie les classes Géographiques.
	//	 * @return les classes Géographiques
	//	 */
	//	public Class<?>[] getClasses() {
	//		return classes;
	//	}
	//
	//	/**
	//	 * Affecter les classes Géographiques.
	//	 * @param classes les classes Géographiques à affecter
	//	 */
	//	public void setClasses(Class<?>[] classes) {
	//		this.classes = classes;
	//	}

	/**
	 * Renvoie les dates associées aux classes Géographiques.
	 * @return les dates associées aux classes Géographiques
	 */
	public Map<Class<?>, SortedSet<Integer>> getClassDates() {return classDates;}

	/**
	 * Affecte les dates associées aux classes Géographiques.
	 * @param classDates dates associées aux classes Géographiques à affecter.
	 */
	public void setClassDates(Map<Class<?>, SortedSet<Integer>> classDates) {this.classDates = classDates;}

	/**
	 * Renvoie tous les éléments Géographiques existants à la date cherchée.
	 * @param date date
	 * @return toutes les éléments Géographiques existants à la date cherchée
	 */
	public List<AgentGeographique> getElementsGeo(int date) {
		List<AgentGeographique> elementsGeo = new ArrayList<AgentGeographique>(0);
		for (AgentGeographique agentGeographique:this) {
			if (agentGeographique.existsIn(date)) elementsGeo.add(agentGeographique);
		}
		return elementsGeo;
	}

	/**
	 * Renvoie tous les éléments Géographiques d'une certaine classe existants à la date cherchée.
	 * @param date date
	 * @return toutes les éléments Géographiques d'une certaine classe existants à la date cherchée
	 */
	public List<AgentGeographique> getElementsGeo(Class<?> classe, int date) {
		List<AgentGeographique> elementsGeo = new ArrayList<AgentGeographique>(0);
		for (AgentGeographique agentGeographique:this) {
			if ((agentGeographique.getRepresentationClass()!=null)&&classe.isAssignableFrom(agentGeographique.getRepresentationClass())&&agentGeographique.existsIn(date)) elementsGeo.add(agentGeographique);
		}
		return elementsGeo;
	}

	/**
	 * Renvoie toutes les représentations existantes à la date cherchée.
	 * @param date date
	 * @return toutes les représentations existantes à la date cherchée
	 */
	public List<ElementRepresentation> getRepresentations(int date) {
		List<ElementRepresentation> representations = new ArrayList<ElementRepresentation>(0);
		for (AgentGeographique agentGeographique:this) {
			ElementRepresentation rep = agentGeographique.getRepresentation(date);
			if (rep!=null) representations.add(rep);
		}
		return representations;
	}

	/**
	 * Renvoie toutes les représentations d'une certaine classe existant à la date cherchée.
	 * @param classe classe de représentations Géographiques
	 * @param date date
	 * @return toutes les représentations d'une certaine classe existant à la date cherchée
	 */
	public List<ElementRepresentation> getRepresentations(Class<?> classe, int date) {
		List<ElementRepresentation> representations = new ArrayList<ElementRepresentation>(0);
		for (AgentGeographique agentGeographique:this) {
			if (classe.isAssignableFrom(agentGeographique.getRepresentationClass())) {
				ElementRepresentation rep = agentGeographique.getRepresentation(date);
				if (rep!=null) representations.add(rep);
			}
		}
		return representations;
	}

	private SortedSet<Integer> datesPrincipales;
	private List<PopulationUnites> populationsUnites = new ArrayList<PopulationUnites>(0);

	/**
	 * Construit toutes les hiérarchies urbaines pour chaque date de la collection.
	 *
	 */
	public void construireHierarchies() {
		if (logger.isDebugEnabled()) logger.debug("Début de la qualification de la collection d'éléments Géographiques");
		Set<Class<?>> classes = classDates.keySet();
		datesPrincipales = classDates.get(classes.iterator().next());
		if (logger.isDebugEnabled()) {
			logger.debug("On prend l'hypothèse que toutes les populations ont les dates :");
			String s="";
			for (Integer i:datesPrincipales) s+=" - "+i;
			logger.debug(s);
		}
		populationsUnites = new ArrayList<PopulationUnites>(0);
		List<Integer> datesAsList = new ArrayList<Integer>(datesPrincipales);
		// on commence par la dernière date
		for (int indexDate = datesAsList.size()-1 ; indexDate>=0 ; indexDate--) {
			Integer date=datesAsList.get(indexDate);
			if (logger.isDebugEnabled()) logger.debug("Création des populations pour la date "+date);
			PopulationUnites popUnites = new PopulationUnites(BasicBatiment.class,BasicTronconRoute.class, BasicTronconChemin.class, BasicTronconVoieFerree.class, BasicTronconCoursEau.class,date);
			//popVilles.detruirePopulations();
			//popVilles.chargerElements();
			popUnites.addBatiments(this.getRepresentations(Batiment.class, date));
			popUnites.addTronconsRoute(this.getRepresentations(TronconRoute.class, date));
			popUnites.addTronconsChemin(this.getRepresentations(TronconChemin.class, date));
			popUnites.addTronconsVoieFerree(this.getRepresentations(TronconVoieFerree.class, date));
			popUnites.addTronconsCoursEau(this.getRepresentations(TronconCoursEau.class, date));
			popUnites.setExtraction(this.isExtraction());
			if (logger.isDebugEnabled()) {
				logger.debug(popUnites.getPopulationBatiments().size()+" batiments");
				logger.debug(popUnites.getPopulationRoutes().size()+" troncons de route");
				logger.debug(popUnites.getPopulationChemins().size()+" troncons de chemin");
				logger.debug(popUnites.getPopulationVoiesFerrees().size()+" troncons de voie ferrées");
				logger.debug(popUnites.getPopulationHydrographiques().size()+" troncons de cours d'eau");
			}
			popUnites.construireUnites();
			popUnites.qualifier();
			//popVilles.sauverPopulations();
			populationsUnites.add(popUnites);
			//TODO pour l'instant, on ne crée des agents méso que pour la dernière date...
			//if (date==datesPrincipales.get(datesPrincipales.size()-1))
			this.creerAgentsMeso(popUnites,date);
			/*
			for (Class<?> c:classes) {
				if (logger.isDebugEnabled()) logger.debug("Création des populations de la classe "+c);

				List<Integer> dates = classDates.get(c);
				if (logger.isDebugEnabled()) {
					String s="";
					for (Integer i:dates) s+=" - "+i;
					logger.debug(s);
				}

			}*/
		}
		int nbBatiments = 0;
		int nbBatimentsSansIlot = 0;
		List<Batiment> batimentsSansIlot = new ArrayList<Batiment>(0);
		for (PopulationUnites pop:populationsUnites) {
			nbBatiments+=pop.getPopulationBatiments().size();
			for (Batiment bat:pop.getPopulationBatiments()) {
				if (bat.getZoneElementaireUrbaine()==null) {nbBatimentsSansIlot++;batimentsSansIlot.add(bat);}
			}
		}
		logger.info(nbBatiments+" bâtiments trouvés dans toutes les populations");
		logger.info(nbBatimentsSansIlot+" bâtiments trouvés sans îlot dans toutes les populations");
		for (Batiment bat:batimentsSansIlot) {logger.info("Batiment "+bat.getId()+" sans Ilot a la date "+bat.getDateSourceSaisie());}
		if (logger.isDebugEnabled()) logger.debug("Fin de la construction des hiérarchies de la collection d'éléments Géographiques");
	}

	/**
	 * Qualification de la collection d'éléments Géographiques.
	 */
	public void qualifier() {
		for (AgentGeographique geo:this) geo.qualifier();
		if (logger.isDebugEnabled()) logger.debug("Fin de la qualification de la collection d'éléments Géographiques");
	}

	/**
	 * crée les agents méso correspondants aux représentations méso de la collection.
	 * TODO finir les instanciations !
	 */
	private void creerAgentsMeso(PopulationUnites unites,int date) {
		//zones élémentaires
		if (logger.isDebugEnabled()) logger.debug("Création des agents correspondant aux "+unites.getPopulationZonesElementaires().size()+" zones élémentaires existant en "+date);
        List<Integer> datesAsList = new ArrayList<Integer>(datesPrincipales);
		int indexDateCourante = datesAsList.lastIndexOf(date);
		for(ZoneElementaire zone:unites.getPopulationZonesElementaires()) {
			if (logger.isDebugEnabled()) logger.debug("Traitement de la zone "+zone);
			AgentGeographique agent = null;
			/**
			 *  si c'est la dernière date, il faut créer des agents pour toutes les représentations
			 *  sinon, il faut aller vérifier les agents créés pour la date suivante
			 */
			if (indexDateCourante!=datesAsList.size()-1) {
				int dateSuivante = datesAsList.get(indexDateCourante+1);
				if (logger.isDebugEnabled()) logger.debug("Comparaison des dates "+date+" et "+dateSuivante+". Traitement de la date "+indexDateCourante+" sur "+datesPrincipales.size());
				List<ZoneElementaire> zonesIntersectees = new ArrayList<ZoneElementaire>();
				List<ElementRepresentation> representationsDateSuivantes =  this.getRepresentations(ZoneElementaire.class,dateSuivante);
				if (logger.isDebugEnabled()) logger.debug(representationsDateSuivantes.size()+" représentations trouvées à la date suivante");
				for(ElementRepresentation rep:representationsDateSuivantes) {
					if (rep.getGeom().intersects(zone.getGeom())) {
						IGeometry intersection = rep.getGeom().intersection(zone.getGeom());
						if (intersection.area()>AgentGeographique.getSeuilIntersection()) {// on ne garde que les zones dont l'intersection est plus grande qu'un seuil
							zonesIntersectees.add((ZoneElementaire)rep);
						} else {
							if (logger.isDebugEnabled()) logger.debug("intersection trop petite entre "+zone+" et "+rep+" de surface "+intersection.area());
						}
					}
				}
				if (logger.isDebugEnabled()) logger.debug(zonesIntersectees.size()+" représentations intersectées à la date suivante");

				if (zonesIntersectees.size()<=1) {// on a aucune ou une seule zone intersectée... c'est le cas le plus simple
					double distanceMin = Double.MAX_VALUE;
					ZoneElementaire repMin = null;
					for (ZoneElementaire rep:zonesIntersectees) {
						double distance = Distances.distanceSurfacique((GM_Polygon)rep.getGeom(), (GM_Polygon)zone.getGeom());
						if (logger.isDebugEnabled()) logger.debug("Distance = "+distance);
						if (distance < distanceMin) {
							distanceMin = distance;
							repMin=rep;
						}
					}// parcours des zones intersectées
					if (repMin!=null) {
						int changement = Changement.Inconnu;
						if (distanceMin<AgentGeographique.getSeuilDistanceSurfacique()) {
							changement = Changement.Stabilite;// avec d = "+String.valueOf(distance)+"";
							// on ajoute la représentation à l'agent Géographique
							((ElementRepresentation)repMin).setChangement(changement);
							agent = repMin.getAgentGeographique();
						} else {
							// ce n'est pas exactement la même zone élémentaire, il faut Déterminer si elle a été découpée...
							if (zone.getGeom().area()<repMin.getGeom().area()) {// cette zone est plus petite que celle à la date suivante
								// TODO aggrandissement ou agregation ???
								changement = Changement.Aggrandissement;// avec d = "+String.valueOf(distance)+"";
								agent = repMin.getAgentGeographique();
								if (logger.isDebugEnabled()) logger.debug("Aggrandissement avec un agent possédant "+agent.getRepresentations(date).size()+" représentations en "+date);
							} else {// cette zone est plus grande que celle à la date suivante
								changement = Changement.Reduction;// avec d = "+String.valueOf(distance)+"";
								agent = repMin.getAgentGeographique();
								if (logger.isDebugEnabled()) logger.debug("Réduction avec un agent possédant "+agent.getRepresentations(date).size()+" représentations en "+date);
							}
							((ElementRepresentation)repMin).setChangement(changement);
						}
						if (logger.isDebugEnabled()) logger.debug("Changement = "+changement);
					}//repMin!=null
					if ((repMin==null)||(zonesIntersectees.isEmpty())) {// on n'intersecte pas la représentation ou pas de meilleure rep intersectée
						if (logger.isDebugEnabled()) logger.debug("ajout d'un nouvel agent zone élémentaire (pas d'intersection) pour la date "+date);
						agent = AgentFactory.newAgentGeographique(zone.getClass());
						this.add(agent);
					}//repMin==null || zonesIntersectees.isEmpty
					if (agent!=null) {
						agent.add((ElementRepresentation) zone);
						if (logger.isDebugEnabled()) logger.debug("Agent possédant "+agent.getRepresentations(date).size()+" représentations en "+date+" après ajout");
						creerHierarchieAgentZoneElementaire(agent,zone);
					} else {
						logger.error("agent=null");
					}
				} else {// on a plusieurs zones intersectées... c'est le cas le plus complexe
					agent = AgentFactory.newAgentGeographique(zone.getClass());
					if (logger.isDebugEnabled()) logger.debug("Ajout d'un nouvel agent "+agent+" avec plusieurs zones intersectées pour la date "+date);
					agent.add((ElementRepresentation) zone);
					this.add(agent);
					creerHierarchieAgentZoneElementaire(agent,zone);
					int changement = Changement.Decoupage;
					// TODO à revoir car beaucoup des découpages Détectés sont des reconfigurations plus complexes...
					for (ZoneElementaire rep:zonesIntersectees) {
						((ElementRepresentation)rep).setChangement(changement);
						AgentGeographique successeur = ((ElementRepresentation)rep).getAgentGeographique();
						agent.getSuccesseurs().add(successeur);
						successeur.getPredecesseurs().add(agent);
					}// parcours des zones intersectées
				}
			} else {// ce n'est pas la dernière date, on crée un agent
				if (logger.isDebugEnabled()) logger.debug("ajout d'un nouvel agent zone élémentaire pour la date "+date);
				agent = AgentFactory.newAgentGeographique(zone.getClass());
				this.add(agent);
				agent.add((ElementRepresentation) zone);
				((ElementRepresentation) zone).setChangement(Changement.Creation);
				creerHierarchieAgentZoneElementaire(agent,zone);
			}
		}
		// parcours des agents possédant des représentations à la date courante
		for(AgentGeographique agent:this.getElementsGeo(ZoneElementaireUrbaine.class, date)) {
			Set<ElementRepresentation> representations = agent.getRepresentations(date);
			if (representations.size()>1) {
				if (logger.isDebugEnabled()) logger.debug("Plusieurs représentations à la date "+date+" pour "+agent);
				for (ElementRepresentation pred:representations) {
					/*
					int changement = Changement.Agregation;
					agent.getRepresentation(date).setChangement(changement);
					*/
					AgentGeographique predecesseur = AgentFactory.newAgentGeographique(pred.getClass());
					if (logger.isDebugEnabled()) logger.debug("Ajout du prédécesseur "+predecesseur);
					predecesseur.add(pred);
					agent.getPredecesseurs().add(predecesseur);
					predecesseur.getSuccesseurs().add(agent);
				}
				agent.removeAll(representations);
				Set<ElementRepresentation> reps = agent.getRepresentations(this.getDates().get(indexDateCourante+1));
				if (reps==null) {
					logger.error("Pas de représentation à la date suivante : "+this.getDates().get(indexDateCourante+1));
				} else {
					if (reps.size()!=1) {
						logger.error(reps.size()+" représentation à la date suivante : "+this.getDates().get(indexDateCourante+1));
					} else {
						int changement = Changement.Agregation;
						reps.iterator().next().setChangement(changement);
					}
				}
			}
		}

		for(ElementRepresentation rep:unites.getPopulationZonesElementaires()) {
			if (rep.getAgentGeographique()==null) {
				logger.error("Attention : pas d'agent Géographique pour : "+rep);
			}
		}

		//zones agrégées
		//unites
		if (logger.isDebugEnabled()) logger.debug("Création des agents correspondant aux "+unites.size()+" Unités existant en "+date);
		for(UniteUrbaine unite:unites) {
			if (logger.isDebugEnabled()) logger.debug("Traitement de l'unite "+unite);
			AgentGeographique agent = null;
			/**
			 *  si c'est la dernière date, il faut créer des agents pour toutes les représentations
			 *  sinon, il faut aller vérifier les agents créés pour la date suivante
			 */
			if (indexDateCourante!=datesAsList.size()-1) {
				int dateSuivante = datesAsList.get(indexDateCourante+1);
				if (logger.isDebugEnabled()) logger.debug("Comparaison des dates "+date+" et "+dateSuivante+". Traitement de la date "+indexDateCourante+" sur "+datesPrincipales.size());
				List<UniteUrbaine> unitesIntersectees = new ArrayList<UniteUrbaine>();
				List<ElementRepresentation> representationsDateSuivantes =  this.getRepresentations(UniteUrbaine.class,dateSuivante);
				if (logger.isDebugEnabled()) logger.debug(representationsDateSuivantes.size()+" représentations trouvées à la date suivante");
				for(ElementRepresentation rep:representationsDateSuivantes) {
					if (rep.getGeom().intersects(unite.getGeom())) {
						IGeometry intersection = rep.getGeom().intersection(unite.getGeom());
						if (intersection.area()>AgentGeographique.getSeuilIntersection()) {// on ne garde que les zones dont l'intersection est plus grande qu'un seuil
							unitesIntersectees.add((UniteUrbaine)rep);
						} else {
							if (logger.isDebugEnabled()) logger.debug("intersection trop petite entre "+unite+" et "+rep+" de surface "+intersection.area());
						}
					}
				}
				if (logger.isDebugEnabled()) logger.debug(unitesIntersectees.size()+" représentations intersectées à la date suivante");

				if (unitesIntersectees.size()<=1) {// on a aucune ou une seule Unité intersectée... c'est le cas le plus simple
					double distanceMin = Double.MAX_VALUE;
					UniteUrbaine repMin = null;
					for (UniteUrbaine rep:unitesIntersectees) {
						double distance = Distances.distanceSurfacique((GM_Polygon)rep.getGeom(), (GM_Polygon)unite.getGeom());
						if (logger.isDebugEnabled()) logger.debug("Distance = "+distance);
						if (distance < distanceMin) {
							distanceMin = distance;
							repMin=rep;
						}
					}// parcours des zones intersectées
					if (repMin!=null) {
						int changement = Changement.Inconnu;
						if (distanceMin<AgentGeographique.getSeuilDistanceSurfacique()) {
							changement = Changement.Stabilite;// avec d = "+String.valueOf(distance)+"";
							// on ajoute la représentation à l'agent Géographique
							((ElementRepresentation)repMin).setChangement(changement);
							agent = repMin.getAgentGeographique();
						} else {
							// ce n'est pas exactement la même zone élémentaire, il faut Déterminer si elle a été découpée...
							if (unite.getGeom().area()<repMin.getGeom().area()) {// cette zone est plus petite que celle à la date suivante
								// TODO aggrandissement ou agregation ???
								changement = Changement.Aggrandissement;// avec d = "+String.valueOf(distance)+"";
								agent = repMin.getAgentGeographique();
								if (logger.isDebugEnabled()) logger.debug("Aggrandissement avec un agent possédant "+agent.getRepresentations(date).size()+" représentations en "+date);
							} else {// cette zone est plus grande que celle à la date suivante
								changement = Changement.Reduction;// avec d = "+String.valueOf(distance)+"";
								agent = repMin.getAgentGeographique();
								if (logger.isDebugEnabled()) logger.debug("Réduction avec un agent possédant "+agent.getRepresentations(date).size()+" représentations en "+date);
							}
							((ElementRepresentation)repMin).setChangement(changement);
						}
						if (logger.isDebugEnabled()) logger.debug("Changement = "+changement);
					}//repMin!=null
					if ((repMin==null)||(unitesIntersectees.isEmpty())) {// on n'intersecte pas la représentation ou pas de meilleure rep intersectée
						if (logger.isDebugEnabled()) logger.debug("ajout d'un nouvel agent zone élémentaire (pas d'intersection) pour la date "+date);
						agent = AgentFactory.newAgentGeographique(unite.getClass());
						this.add(agent);
					}//repMin==null || zonesIntersectees.isEmpty
					if (agent!=null) {
						agent.add(unite);
						if (logger.isDebugEnabled()) logger.debug("Agent possédant "+agent.getRepresentations(date).size()+" représentations en "+date+" après ajout");
						//creerHierarchieAgentUniteBatie(agent,unite);
					} else {
						logger.error("agent=null");
					}
				} else {// on a plusieurs zones intersectées... c'est le cas le plus complexe
					agent = AgentFactory.newAgentGeographique(unite.getClass());
					if (logger.isDebugEnabled()) logger.debug("Ajout d'un nouvel agent "+agent+" avec plusieurs Unités intersectées pour la date "+date);
					agent.add(unite);
					this.add(agent);
					//creerHierarchieAgentUniteBatie(agent,unite);
					int changement = Changement.Decoupage;
					// TODO à revoir car beaucoup des découpages Détectés sont des reconfigurations plus complexes...
					for (UniteUrbaine rep:unitesIntersectees) {
						((ElementRepresentation)rep).setChangement(changement);
						AgentGeographique successeur = ((ElementRepresentation)rep).getAgentGeographique();
						agent.getSuccesseurs().add(successeur);
						successeur.getPredecesseurs().add(agent);
					}// parcours des Unités intersectées
				}
			} else {// ce n'est pas la dernière date, on crée un agent
				if (logger.isDebugEnabled()) logger.debug("ajout d'un nouvel agent zone élémentaire pour la date "+date);
				agent = AgentFactory.newAgentGeographique(unite.getClass());
				this.add(agent);
				agent.add(unite);
				((ElementRepresentation) unite).setChangement(Changement.Creation);
				//creerHierarchieAgentUniteBatie(agent,unite);
			}
		}
		// parcours des agents possédant des représentations à la date courante
		for(AgentGeographique agent:this.getElementsGeo(UniteUrbaine.class, date)) {
			Set<ElementRepresentation> representations = agent.getRepresentations(date);
			if (representations.size()>1) {
				if (logger.isDebugEnabled()) logger.debug("Plusieurs représentations à la date "+date+" pour "+agent);
				for (ElementRepresentation pred:representations) {
					/*
					int changement = Changement.Agregation;
					agent.getRepresentation(date).setChangement(changement);
					*/
					AgentGeographique predecesseur = AgentFactory.newAgentGeographique(pred.getClass());
					if (logger.isDebugEnabled()) logger.debug("Ajout du prédécesseur "+predecesseur);
					predecesseur.add(pred);
					agent.getPredecesseurs().add(predecesseur);
					predecesseur.getSuccesseurs().add(agent);
				}
				agent.removeAll(representations);
				Set<ElementRepresentation> reps = agent.getRepresentations(this.getDates().get(indexDateCourante+1));
				if (reps==null) {
					logger.error("Pas de représentation à la date suivante : "+this.getDates().get(indexDateCourante+1));
				} else {
					if (reps.size()!=1) {
						logger.error(reps.size()+" représentation à la date suivante : "+this.getDates().get(indexDateCourante+1));
					} else {
						int changement = Changement.Agregation;
						reps.iterator().next().setChangement(changement);
					}
				}
			}
		}

		for(ElementRepresentation rep:unites) {
			if (rep.getAgentGeographique()==null) {
				logger.error("Attention : pas d'agent Géographique pour : "+rep);
			}
		}
		// Unités périurbaines
		/**
		 *  si c'est la dernière date, il faut créer des agents pour toutes les représentations
		 *  sinon, il faut aller vérifier les agents créés pour la date suivante
		 */
		if (indexDateCourante==datesAsList.size()-1) {
			AgentUniteBatie agent = (AgentUniteBatie) AgentFactory.newAgentGeographique(UniteUrbaine.class);
			agent.add(unites.getUnitePeriUrbaine());
			this.add(agent);
		} else {
			// Il ne devrait y en avoir qu'un seul
			AgentUniteBatie agent = (AgentUniteBatie) this.getElementsGeo(UniteUrbaine.class, datesAsList.get(datesAsList.size()-1)).get(0);
//			AgentUniteBatie agent = (AgentUniteBatie) this.getElementsGeo(UniteUrbaine.class, datesPrincipales.get(datesPrincipales.size()-1)).get(0);
			agent.add(unites.getUnitePeriUrbaine());
		}

		this.analyserElements();
	}

	/**
	 * @param agent
	 * @param zone
	 */
	private void creerHierarchieAgentZoneElementaire(AgentGeographique agent, ZoneElementaire zone) {
		if (zone.getClass().isAssignableFrom(ZoneElementaireUrbaine.class)) {
			long time = System.currentTimeMillis();
		    if (logger.isDebugEnabled()) logger.debug("Début de la création des Hierarchies d'Agents ZoneElementaire");
			this.getZonesElementairesBaties().add((AgentZoneElementaireBatie) agent);
			for (EspaceVide espaceVide :((ZoneElementaireUrbaine) zone).getEspacesVides()) {
				AgentEspaceVide agentEspace = (AgentEspaceVide) AgentFactory.newAgentGeographique(espaceVide.getClass());
				agentEspace.add(espaceVide);
				this.add(agentEspace);
				this.getEspacesVides().add(agentEspace);
			}
			// ajout de l'agent Groupebatiments
			for (GroupeBatiments groupeBatiments :((ZoneElementaireUrbaine) zone).getGroupesBatiments()) {
				AgentGroupeBatiments agentGroupeBatiments = (AgentGroupeBatiments) AgentFactory.newAgentGeographique(groupeBatiments.getClass());
				agentGroupeBatiments.add(groupeBatiments);
				this.add(agentGroupeBatiments);
				this.getGroupesBatiments().add(agentGroupeBatiments);
				// ajout des batiments
				for (Batiment batiment :groupeBatiments.getBatiments()) {
					this.getBatiments().add((AgentBatiment) batiment.getAgentGeographique());
				}
				// ajout de l'agent Alignement
				for (Alignement alignement : groupeBatiments.getAlignements()) {
					AgentAlignement agentAlignement = (AgentAlignement) AgentFactory.newAgentGeographique(Alignement.class);
					agentAlignement.add(alignement);
					this.add(agentAlignement);
					this.getAlignements().add(agentAlignement);
				}
			}
//			for (Batiment batiment :((ZoneElementaireUrbaine) zone).getBatiments()) {
//				this.getBatiments().add((AgentBatiment) batiment.getAgentGeographique());
//			}
			// vérification
			for (GroupeBatiments groupeBatiments :((ZoneElementaireUrbaine) zone).getGroupesBatiments()) {
			    if (!groupeBatiments.getAgentGeographique().getClass().isAssignableFrom(AgentGroupeBatiments.class)) {
				logger.error(groupeBatiments+" a pour agent "+groupeBatiments.getAgentGeographique());
			    }
			}
			if (logger.isDebugEnabled()) logger.debug("Fin de la création des Hierarchies d'Agents ZoneElementaire. Ca a pris "+(System.currentTimeMillis()-time)+" ms");
		} else {
			this.getZonesElementairesBaties().add((AgentZoneElementaireBatie) agent);
		}
	}

	int dateSimulee;
	@Override
	@Transient
	public int getDateSimulee() {return this.dateSimulee;}
	@Override
	public void setDateSimulee(int dateSimulee) {
		this.dateSimulee = dateSimulee;
		for (AgentGeographique agent:this.getElementsGeo(dateDebutSimulation)) {
			agent.setDateSimulee(dateSimulee);
		}
	}

	int dateDebutSimulation;
	@Override
	@Transient
	public int getDateDebutSimulation() {return this.dateDebutSimulation;}
	@Override
	public void setDateDebutSimulation(int dateDebutSimulation) {
		this.dateDebutSimulation = dateDebutSimulation;
		for (AgentGeographique agent:this.getElementsGeo(dateDebutSimulation)) {
			agent.setDateDebutSimulation(dateDebutSimulation);
		}
	}

	int dureePasSimulation;
	/**
	 * Renvoie la valeur de l'attribut dureePasSimulation.
	 * @return la valeur de l'attribut dureePasSimulation
	 */
	@Transient
	public int getDureePasSimulation() {return this.dureePasSimulation;}
	/**
	 * Affecte la valeur de l'attribut dureePasSimulation.
	 * @param dureePasSimulation l'attribut dureePasSimulation à affecter
	 */
	public void setDureePasSimulation(int dureePasSimulation) {this.dureePasSimulation = dureePasSimulation;}
	/**
	 * Evalue la Durée d'un pas de simulation à partir de l'ensemble des dates pour lesquelles on possède des représentations.
	 * TODO devrait on prendre la plus petite Durée entre deux dates, la plus grande ???
	 */
	public void calculDureePasSimulation() {
		// on prends l'hypothèse que toutes les classes ont les mêmes dates
		Set<Class<?>> classes = classDates.keySet();
		SortedSet<Integer> mainDates = classDates.get(classes.iterator().next());
		List<Double> differences = new ArrayList<Double>(0);
        List<Integer> datesAsList = new ArrayList<Integer>(mainDates);
		for(int index = 0 ; index < datesAsList.size()-1 ; index++) {
			differences.add(datesAsList.get(index+1).doubleValue()-datesAsList.get(index).doubleValue());
			double diff = datesAsList.get(index+1).doubleValue()-datesAsList.get(index).doubleValue();
			logger.debug("diff : "+  + diff+" entre les dates : "+datesAsList.get(index)+" et "+datesAsList.get(index+1));
		}
        if (mainDates.size()<2) {
            logger.warn("On ne peut pas évaluer la Durée d'un pas de simulation avec seulement une date de représentation");
            this.setDureePasSimulation(20);
        } else {
            this.setDureePasSimulation(new Double(MathUtil.moyenne(differences)).intValue());
        }
		if (logger.isDebugEnabled()) logger.debug("Durée du pas de simulation calculée = "+this.getDureePasSimulation());
	}

	@Override
	public void activer() {
		logger.info("Début de la simulation à la date "+dateDebutSimulation);
		List<AgentGeographique> elementsGeoDepart = getElementsGeo(dateDebutSimulation);
		logger.info("La simulation Débute avec "+elementsGeoDepart.size() + " éléments Géographiques");
		List<AgentGeographique> elementsGeoVillesDepart = getElementsGeo(UniteUrbaine.class,dateDebutSimulation);
		logger.info("La simulation Débute avec "+elementsGeoVillesDepart.size() + " villes");
		Scheduler.setAgentsAtraiter(elementsGeoVillesDepart);
		Scheduler.charger();
		Scheduler.getInstance().activer();
	}

	@Override
	public void cycleDeVie() {}

	@Override
	public void instancierContraintes() {
		for(AgentGeographique agent:this) {
			agent.instancierContraintes();
		}
	}

	@Override
	public List<Contrainte> getContraintes() {return null;}

	/**
	 * Renvoie la valeur de l'attribut populationsUnites.
	 * @return la valeur de l'attribut populationsUnites
	 */
	public List<PopulationUnites> getPopulationsUnites() {return this.populationsUnites;}

	public List<Integer> getDates() {
		if (this.getClassDates().isEmpty()) {
			logger.error("Pas d'agent Géographique");
		}
		// FIXME sélection les dates de façon plus intelligente
		Set<Integer> dates = new HashSet<Integer>();
		//classDates.get(classDates.keySet().iterator().next());
		for (Entry<Class<?>,SortedSet<Integer>> entry:this.getClassDates().entrySet()) {
			dates.addAll(entry.getValue());
		}
		int nbDates = dates.size();
		logger.debug(nbDates+" dates trouvées");
		Integer[] datesArray = dates.toArray(new Integer[0]);
		Arrays.sort(datesArray);
		return Arrays.asList(datesArray);
	}
	@Override
	public int getId() {return 0;}
	@Override
	public void setId(int id) {}
	@Override
	public double getSatisfaction() {
		// FIXME est-ce que ça a toujours un sens que la collection soit un agent
		return 0;
	}
	@Override
	public void setSatisfaction(double satisfaction) {
		// FIXME est-ce que ça a toujours un sens que la collection soit un agent
	}
	/**
	 *
	 */
	public void creerPopulations() {
		//FIXME recréer les populations !!!!
		List<AgentGeographique> unites = getElementsGeo(UniteUrbaine.class,dateSimulee);//collection.getUnitesBaties()
		for (AgentGeographique unite:unites) {
			if (!unite.isDeleted()) {
				logger.debug("construction d'une Unité urbaine");
				ElementRepresentation rep = unite.construireRepresentationCourante();
				rep.setDateSourceSaisie(dateSimulee);
				rep.setAgentGeographique(unite);
				unite.add(rep);
				// FIXME vérifier pour la date
				this.getClassDates().get(UniteUrbaine.class).add(this.getDateSimulee());
			}
		}
	}
    /**
     *
     */
    public void applyEvolutionRules(List<EvolutionRule> listeR) {
        for (AgentGeographique agent : this.getElementsGeo(this.
                getDateDebutSimulation())) {
            agent.applyEvolutionRules(listeR);
        }
    }
}
