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

package fr.ign.cogit.appli.geopensim.agent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.IndexColumn;

import fr.ign.cogit.appli.geopensim.ConfigurationSimulation;
import fr.ign.cogit.appli.geopensim.agent.AgentModifie.Valeurs;
import fr.ign.cogit.appli.geopensim.agent.event.AgentCollectionEvent;
import fr.ign.cogit.appli.geopensim.agent.event.AgentCollectionListener;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.comportement.Comportement;
import fr.ign.cogit.appli.geopensim.contrainte.Contrainte;
import fr.ign.cogit.appli.geopensim.evolution.EvolutionRule;
import fr.ign.cogit.appli.geopensim.evolution.EvolutionRuleBase;
import fr.ign.cogit.appli.geopensim.feature.Changement;
import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicRepresentationFactory;
import fr.ign.cogit.appli.geopensim.feature.macro.PopulationUnites;
import fr.ign.cogit.appli.geopensim.feature.meso.UniteUrbaine;
import fr.ign.cogit.appli.geopensim.scheduler.Scheduler;
import fr.ign.cogit.appli.geopensim.scheduler.SchedulerEvent;
import fr.ign.cogit.appli.geopensim.util.Date;
import fr.ign.cogit.geoxygene.api.feature.event.FeatureCollectionEvent;
import fr.ign.cogit.geoxygene.api.feature.event.FeatureCollectionEvent.Type;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Elements Géographiques.
 * @author Julien Perret
 *
 */
@Entity
@MappedSuperclass
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class AgentGeographique extends FT_Feature implements /*Collection<ElementRepresentation>,*/ Agent, AgentCollectionListener {
	static Logger logger=Logger.getLogger(AgentGeographique.class.getName());

	protected static double seuilDistanceSurfacique=0.05;
	/**
	 * Renvoie la valeur de l'attribut seuilDistanceSurfacique.
	 * @return la valeur de l'attribut seuilDistanceSurfacique
	 */
	public static double getSeuilDistanceSurfacique() {return seuilDistanceSurfacique;}
	/**
	 * Affecte la valeur de l'attribut seuilDistanceSurfacique.
	 * @param seuilDistanceSurfacique l'attribut seuilDistanceSurfacique à affecter
	 */
	public static void setSeuilDistanceSurfacique(double seuilDistanceSurfacique) {AgentGeographique.seuilDistanceSurfacique = seuilDistanceSurfacique;}

	protected static double seuilDistanceLineaire=1.0;
	/**
	 * Renvoie la valeur de l'attribut seuilDistanceLineaire.
	 * @return la valeur de l'attribut seuilDistanceLineaire
	 */
	public static double getSeuilDistanceLineaire() {return seuilDistanceLineaire;}
	/**
	 * Affecte la valeur de l'attribut seuilDistanceLineaire.
	 * @param seuilDistanceLineaire l'attribut seuilDistanceLineaire à affecter
	 */
	public static void setSeuilDistanceLineaire(double seuilDistanceLineaire) {AgentGeographique.seuilDistanceLineaire = seuilDistanceLineaire;}

	protected static double seuilIntersection=1.0;
	/**
	 * @return l'attribut seuilIntersection
	 */
	public static double getSeuilIntersection() {return seuilIntersection;}
	/**
	 * Affecte la valeur de l'attribut seuilIntersection.
	 * @param seuilIntersection l'attribut seuilIntersection à affecter
	 */
	public static void setSeuilIntersection(double seuilIntersection) {AgentGeographique.seuilIntersection = seuilIntersection;}

	/**
	 * Date de création de l'objet.
	 */
	protected Date creation = new Date();
	/**
	 * Dates auxquelles cet élément Géographique possède des représentations.
	 */
	protected SortedSet<Integer> dates = new TreeSet<Integer>();
	/**
	 * @param dates
	 */
	public void setDates(SortedSet<Integer> dates) {this.dates = dates;}
	/**
	 * Date de destruction de l'objet.
	 */
	protected Date destruction = new Date();
	/**
	 * Liste des éléments de l'objet Géographique, i.e. ses représentations.
	 */
	protected List<ElementRepresentation> elements = new ArrayList<ElementRepresentation>();
	/**
	 * Identifiant Géographique de l'objet.
	 */
	protected int idGeo;

	private String ojbConcreteClass = AgentGeographique.class.getName();
	/**
	 * Renvoie la valeur de l'attribut ojbConcreteClass.
	 * @return la valeur de l'attribut ojbConcreteClass
	 */
	public String getOjbConcreteClass() {return this.ojbConcreteClass;}
	/**
	 * Affecte la valeur de l'attribut ojbConcreteClass.
	 * @param ojbConcreteClass l'attribut ojbConcreteClass à affecter
	 */
	public void setOjbConcreteClass(String ojbConcreteClass) {this.ojbConcreteClass = ojbConcreteClass;}

	protected Class<?> representationClass = null;
	protected String representationClassString = null;
	public String getRepresentationClassString() {return representationClassString;}

	public void setRepresentationClassString(String representationClassString) {
		this.representationClassString = representationClassString;
		try {
			this.representationClass = Class.forName(representationClassString);
		} catch (ClassNotFoundException e) {
			logger.error("La classe "+representationClassString+" n'existe pas.");
		}
	}

	/**
	 * Constructeur vide.
	 * Ne doit pas être utilisé.
	 */
	public AgentGeographique() {super();this.setOjbConcreteClass(this.getClass().getName());}
	/**
	 * Constructeur à partir d'un identifiant et d'un type de représentation.
	 * @param idGeo identifiant de l'agent
	 * @param representationClass type de représentation
	 */
	public AgentGeographique(int idGeo, Class<?> representationClass) {
		super();
		this.idGeo = idGeo;
		this.representationClass = representationClass;
		this.representationClassString = representationClass.getName();
		this.setOjbConcreteClass(this.getClass().getName());
	}
	/**
	 * Ajoute une représentation à l'objet Géographique.
	 * Met à jour par la même occasion les dates des représentations de l'objet
	 * ainsi que ses dates de création et de destruction.
	 * @see java.util.Collection#add(java.lang.Object)
	 * @param e représentation à ajouter
	 * @return vrai si la représentation a été ajoutée, faux sinon
	 */
	public boolean add(ElementRepresentation e) {
		if (logger.isDebugEnabled()) logger.debug("ajout d'une représentation "+e.getGeom());
		if (e.getDateSourceSaisie()<getCreation().getMax()) {
			getCreation().setMax(e.getDateSourceSaisie());
			logger.debug("date de création max modifiée à : "+e.getDateSourceSaisie());
		}
		if (e.getDateSourceSaisie()>getDestruction().getMin()) {
			getDestruction().setMin(e.getDateSourceSaisie());
			logger.debug("date de destruction min modifiée à : "+e.getDateSourceSaisie());
		}
		e.setAgentGeographique(this);
		this.dates.add(e.getDateSourceSaisie());
//		if (dates.isEmpty()) {
//			dates.add(e.getDateSourceSaisie());
//		} else {
//			for (int index = 0 ; index < dates.size() ; index++) {
//				if (e.getDateSourceSaisie()<dates.get(index)) {
//					dates.add(index, e.getDateSourceSaisie());
//					return elements.add(e);
//				}
//			}
//			if (e.getDateSourceSaisie()>dates.get(dates.size()-1))
//				dates.add(e.getDateSourceSaisie());
//		}
		return elements.add(e);
	}
	/**
	 * Ajoute une collection de représentations
	 * @param c collection de représentations à ajouter
	 * @return vrai si la collection a été entièrement ajoutée, faux sinon
	 */
	public boolean addAll(Collection<? extends ElementRepresentation> c) {return elements.addAll(c);}
	/**
	 * Analyse les représentations de l'élément Géographique :
	 * construit la liste des dates auxquelles il possède des représentations.
	 * Les dates de construction et de destruction ne sont pas évaluées :
	 * ceci est effectué au niveau de la collection d'éléments Géographiques.
	 * @see AgentGeographiqueCollection#analyserElements()
	 */
	public void analyser() {
	    if (logger.isTraceEnabled()) logger.trace("analyse des dates de l'élément Géographique "+this.getIdGeo()+" de type "+this.getRepresentationClass());
		dates.clear();
		for (ElementRepresentation rep:this.getElements()) {
			if (logger.isTraceEnabled()) logger.trace(rep);
			int date = rep.getDateSourceSaisie();
			dates.add(new Integer(date));
			// Si c'est la première représentation, on ajoute sa date source directement à la liste
//			if (dates.isEmpty()) dates.add(date);
//			else {
//				// Sinon, on l'insére au bon endroit dans la liste
//				if (!dates.contains(date)) {
//					boolean trouve = false;
//					for (int index = 0 ; index < dates.size()&&!trouve ; index++) {
//						if (date<dates.get(index)) {
//							dates.add(index, date);
//							trouve = true;
//						}
//					}
//					// Si on a pas inséré la date dans la liste et qu'elle est supérieure au max de la liste, on l'ajoute à la fin
//					if (!trouve&&date>dates.get(dates.size()-1)) dates.add(date);
//				}
//			}
		}
		if (logger.isTraceEnabled()) for(int i:dates) logger.trace(i+" ");
		// Si ce n'est pas une représentation méso, on analyse ses changements.
//		try {
//			if (!(MesoRepresentation.class.isAssignableFrom(this.getRepresentationClass()))) analyserChangements();
//		} catch (Exception e) {}
		if (logger.isTraceEnabled()) logger.trace("fin de l'analyse des dates de l'élément Géographique");
	}
	/**
	 * Analyse les changements entre les représentations de l'élément Géographique.
	 * FIXME Pour l'instant, les changement sont plutôt calculés dans la classe AgentGeographiqueCollection
	 * @see AgentGeographiqueCollection#creerAgentsMeso(PopulationUnites,int)
	 * @see AgentGeographique#analyser()
	 */
	public void analyserChangements() {
		if (logger.isTraceEnabled()) logger.trace("analyse des changements de l'élément Géographique "+this.getIdGeo());
		if (this.getCreation().getMin()<this.getCreation().getMax()) {
			this.getRepresentation(this.getCreation().getMax()).setChangement(Changement.Creation);
		}
		if (logger.isTraceEnabled()) {String s = ""; for (Integer date:dates) s+=" "+date; logger.trace(s);}
		Integer[] datesAsArray = dates.toArray(new Integer[dates.size()]);
		for (int i = 1 ; i < dates.size() ; i++) {
		    IGeometry geom1 = this.getRepresentation(datesAsArray[i-1]).getGeom();
		    IGeometry geom2 = this.getRepresentation(datesAsArray[i]).getGeom();
			int changement = Changement.Inconnu;
			if ((geom1.isPolygon()) && (geom2.isPolygon())) {
				GM_Polygon poly1 = (GM_Polygon) geom1;
				GM_Polygon poly2 = (GM_Polygon) geom2;
				double distance = Distances.distanceSurfacique(poly1, poly2);
				if (distance<seuilDistanceSurfacique) {
					changement = Changement.Stabilite;// avec d = "+String.valueOf(distance)+"";
				} else {
					if (poly1.area()<poly2.area()) {
						changement = Changement.Aggrandissement;// avec d = "+String.valueOf(distance)+"";
					} else {
						changement = Changement.Reduction;// avec d = "+String.valueOf(distance)+"";
					}
				}
			} else {
				if ((geom1.isLineString()) && (geom2.isLineString())) {
					GM_LineString line1 = (GM_LineString) geom1;
					GM_LineString line2 = (GM_LineString) geom2;
					double distance = Distances.hausdorff(line1, line2);
					if (distance > seuilDistanceLineaire) {
						changement = Changement.Modification;// avec d = "+String.valueOf(distance)+"";
					} else {
						changement = Changement.Stabilite;// avec d = "+String.valueOf(distance)+"";
					}
				}
				else {
					if (logger.isDebugEnabled()) {
						logger.debug("Agent "+this.getRepresentationClassString()+" - "+this.getId()+": 2 géométries non gérées "+geom1.getClass()+" "+geom2.getClass());
						logger.debug(datesAsArray[i-1]+": géométrie 1 = "+geom1);
						logger.debug(datesAsArray[i]+": géométrie 2 = "+geom2);
					}

				}
			}
			this.getRepresentation(datesAsArray[i]).setChangement(changement);
		}
		if (logger.isTraceEnabled()) logger.trace("fin de l'analyse des changements de l'élément Géographique");
	}
	/**
	 * Vide la liste de représentations
	 */
	public void clear() {elements.clear();}

	/**
	 * Vrai si l'agent contient la représentation en paramètre, faux sinon
	 * @param o représentation
	 * @return vrai si l'agent contient la représentation en paramètre, faux sinon
	 */
	public boolean contains(Object o) {return elements.contains(o);}

	/**
	 * Vrai si l'agent contient l'ensemble des représentations en paramètre, faux sinon
	 * @param c collection de représentations
	 * @return Vrai si l'agent contient l'ensemble des représentations en paramètre, faux sinon
	 */
	public boolean containsAll(Collection<?> c) {return elements.containsAll(c);}
	/**
	 * Renvoie une représentation de l'objet Géographique.
	 * @param index indice de la représentation
	 * @return la représentation d'indice index de l'objet Géographique.
	 */
	public ElementRepresentation get (int index) {return elements.get(index);}
	/**
	 * Renvoie la date de création de l'objet.
	 * @return la date de création de l'objet
	 */
	@OneToOne(cascade = CascadeType.ALL)
	public Date getCreation() {return creation;}
	/**
	 * Renvoie la date de destruction de l'objet.
	 * @return la date de destruction de l'objet
	 */
	@OneToOne(cascade = CascadeType.ALL)
	public Date getDestruction() {return destruction;}
	/**
	 * Renvoie les dates auxquelles cet élément Géographique possède des représentations.
	 * @return les dates auxquelles cet élément Géographique possède des représentations
	 */
    @CollectionOfElements
    @JoinTable(
            name="AgentDates",
            joinColumns = @JoinColumn(name="AgentId")
    )
    @Column(name="date", nullable=false)
    @IndexColumn(name="date_index")
    public SortedSet<Integer> getDates() {return dates;}
	/**
	 * Renvoie les représentation de cet élément Géographique.
	 * @return les représentation de cet élément Géographique
	 */
	@OneToMany(cascade = CascadeType.ALL)
	public List<ElementRepresentation> getElements() {return elements;}
	/**
	 * Renvoie l'identifiant. NB: l'identifiant n'est rempli automatiquement que pour les objets persistants
	 * On utilise ici l'identifiant idGeo.
	 */
    //@Id
	//public int getId() {return idGeo;}
	/**
	 * Renvoie l'identifiant Géographique de l'objet.
	 * @return l'identifiant Géographique
	 */
    @Transient
	public int getIdGeo() {return idGeo;}
	/**
	 * Renvoie la représentation de cet élément Géographique à la date donnée si elle existe, null sinon.
	 * TODO revoir le mécanisme au cas où l'agent possède plusieurs représentations à la même date (représentations simulées).
	 * @param date date
	 * @return la représentation de cet élément Géographique à la date donnée si elle existe, null sinon
	 */
	public ElementRepresentation getRepresentation(int date) {
		// s'il existe une représentation à cette date
		if (dates.contains(date)) {
			for (ElementRepresentation rep:this.getElements()) {
				if (rep.getDateSourceSaisie()==date)
					return rep;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Pas de représentation trouvée à la date "+date);
			logger.debug("Les représentations existantes sont les suivantes :");
			for (ElementRepresentation rep:this.getElements()) logger.debug(rep);
		}
		return null;
	}
	
	public ElementRepresentation getRepresentation(int date, int idSim) {
		// s'il existe une représentation à cette date
		if (dates.contains(date)) {
			for (ElementRepresentation rep:this.getElements()) {
				if ((rep.getDateSourceSaisie()==date)&&(rep.getIdSimul()==idSim))
					return rep;
			}
		}
		return null;
	}
	
	/**
	 * Renvoie la représentation de cet élément Géographique à la date donnée si elle existe, null sinon.
	 * @param date date
	 * @return la représentation de cet élément Géographique à la date donnée si elle existe, null sinon
	 */
	public Set<ElementRepresentation> getRepresentations(int date) {
		Set<ElementRepresentation> resultat = new HashSet<ElementRepresentation>();
		if (dates.contains(date)) {
			for (ElementRepresentation rep:this.getElements()) {
				if (rep.getDateSourceSaisie()==date) resultat.add(rep);
			}
		}
		return resultat;
	}
	int dateSimulee;
	@Override
	@Transient
	public int getDateSimulee() {return this.dateSimulee;}
	@Override
	public void setDateSimulee(int dateSimulee) {
		this.dateSimulee = dateSimulee;
		//TODO affecte ses valeurs objectifs à l'agent.
		this.setAttributsDateSimulee();
	}
	
	/**
	 * identifiant de la simulation (nécessaire en plus de la date dans le cas où il existe deux simulations à la même date).
	 */
	int idSimul;
	/**
	 * @param idSimul l'identifiant de la simulation.
	 */
	public void setIdSimul(int idSimul) {this.idSimul = idSimul;}
	/**
	 * @return l'identifiant de la simulation.
	 */
	public int getIdSimul(){return this.idSimul;}
	
	/**
	 * Affecte les attributs objectif à l'agent
	 */
	public void setAttributsDateSimulee() {
		//TODO affecte les valeurs objectif aux attributs
	}

	int dateDebutSimulation;
	@Override
	@Transient
	public int getDateDebutSimulation() {return this.dateDebutSimulation;}
	/**
	 * Affecte la valeur de l'attribut representationDebutSimulation.
	 * @param representationDebutSimulation l'attribut representationDebutSimulation à affecter
	 */
	public void setRepresentationDebutSimulation(ElementRepresentation representationDebutSimulation) {
		this.representationDebutSimulation = representationDebutSimulation;
	}

	private ElementRepresentation representationDebutSimulation;
	@Override
	public void setDateDebutSimulation(int dateDebutSimulation) {
		this.dateDebutSimulation = dateDebutSimulation;
		// affecte la représentation de Début de simulation à l'agent
		this.representationDebutSimulation = this.getRepresentation(dateDebutSimulation);
		if (representationDebutSimulation==null) {
			if (logger.isDebugEnabled()) logger.debug("L'agent ne possède pas de représentation à la date de Début de simulation : "+dateDebutSimulation);
			this.setSupprime(true);
			return;
		}
		this.prendreAttributsRepresentation(representationDebutSimulation);
	}
	/**
	 * Affecte les attributs de la représentation en paramètre à l'agent
	 * et affecte leurs valeurs initiales aux attributs
	 */
	public void prendreAttributsRepresentation(ElementRepresentation representation) {
	    if (representation == null) {
	      return;
	    }
	    this.setGeom(representation.getGeom());
	    this.setSatisfaction(representation.getSatisfaction());
	    this.representationCourante = representation;
		this.setSupprime(representation.isDeleted());
		this.setSimulated(representation.isSimulated());
		this.setIdSimul(representation.getIdSimul()); //new
		if (!(representation instanceof UniteUrbaine)) {
			logger.trace(representation.getGeom()+ " à la date : "+representation.getDateSourceSaisie()+ representation.getClass());
			this.setDateSimulee(representation.getDateSourceSaisie());
		}
		//FIXME voir la date
		//TODO affecte les valeurs initiales aux attributs
	}
	/**
	 * Construire une représentation à partir des attributs courants de l'agent
	 * @return
	 */
	public ElementRepresentation construireRepresentationCourante() {
		ElementRepresentation representation = new BasicRepresentationFactory().creerElementRepresentation(this.representationClass.getSimpleName());
		representation.setGeom(getGeom());
		representation.setDeleted(this.isDeleted());
		representation.setSimulated(this.isSimulated());
		representation.setSatisfaction(getSatisfaction());
		//FIXME voir la date
		if (logger.isTraceEnabled()) {
		    logger.trace("date simul : "+this.getDateSimulee());
		}
		representation.setDateSourceSaisie(dateSimulee);
		representation.setIdSimul(this.getIdSimul());
		representation.setAgentGeographique(this);
		this.representationCourante = representation;
		return representation;
	}

	private ElementRepresentation representationCourante;

	//private GM_Object geom;
	/**
	 * Renvoie la géométrie courante de l'agent
	 * @return la géométrie courante de l'agent
	 */
	//@Transient
	//public GM_Object getGeom() {return this.geom;}
	/**
	 * Affecte la géométrie courante de l'agent
	 * TODO cloner la géométrie ???
	 * @param geom géométrie de l'agent
	 */
	//public void setGeom(GM_Object geom) {this.geom = geom;}
	public void setGeom(IGeometry geom) {
		IGeometry geomPrecedente = this.geom;
		super.setGeom(geom);
		if (geomPrecedente!=this.geom){
			AgentGeographiqueCollection collection = AgentGeographiqueCollection.getInstance();
			collection.fireActionPerformed(new AgentCollectionEvent(
					collection, 
					this, 
					FeatureCollectionEvent.Type.CHANGED,
					"Geom",
					geomPrecedente,
					this.geom));
		}
	}
	
	/**
	 * @return Representation courante de l'agent.
	 */
	@Transient
	public ElementRepresentation getRepresentationCourante() {
	    if (representationCourante==null) this.construireRepresentationCourante();
	    return representationCourante/*getRepresentation(dateSimulee)*/;
	}
	/**
	 * @return Representation du Début de la simulation de l'agent.
	 */
	@Transient
	public ElementRepresentation getRepresentationDebutSimulation() {return this.representationDebutSimulation;}
	/**
	 * @return Classe des représentations portées par l'agent.
	 */
	public Class<?> getRepresentationClass() {return representationClass;}
	/**
	 * @return Vrai si l'agent ne possède aucune représentation, faux sinon.
	 */
	@Transient
	public boolean isEmpty() {return elements.isEmpty();}
	/**
	 * @return itérateur sur les représentations de l'agent.
	 */
	public Iterator<ElementRepresentation> iterator() {return elements.iterator();}
	/**
	 * Qualification de l'élément Géographique.
	 */
	public void qualifier() {
		if (logger.isDebugEnabled()) logger.debug("Début de la qualification de l'élément Géographique");
		for (ElementRepresentation rep:this.getElements()) {
		    rep.qualifier();
		}
		if (logger.isDebugEnabled()) logger.debug("Fin de la qualification de l'élément Géographique");
	}
	/**
	 * Supprime une représentation de l'agent.
	 * @param o représentation de l'agent
	 * @return Vrai si la représentation a été supprimée, faux sinon.
	 */
	public boolean remove(Object o) {return elements.remove(o);}
	/**
	 * Supprime une collection de représentations de l'agent.
	 * @param c représentations de l'agent
	 * @return Vrai si les représentations ont été supprimées, faux sinon.
	 */
	public boolean removeAll(Collection<?> c) {return elements.removeAll(c);}
	/**
	 * Conserve une collection de représentations de l'agent et supprime les autres.
	 * @param c représentations de l'agent
	 * @return Vrai si les représentations ont été conservées et les autres supprimées, faux sinon.
	 */
	public boolean retainAll(Collection<?> c) {return elements.retainAll(c);}
	/**
	 * Affecte la date de création de l'objet.
	 * @param creation la date de création de l'objet
	 */
	public void setCreation(Date creation) {this.creation = creation;}
	/**
	 * Affecte la date de destruction de l'objet.
	 * @param destruction la date de destruction de l'objet
	 */
	public void setDestruction(Date destruction) {this.destruction = destruction;}
	/**
	 * Affecte les représentation de cet élément Géographique.
	 * @param elements les représentation de cet élément Géographique à affecter
	 */
	public void setElements(List<ElementRepresentation> elements) {this.elements = elements;}
	/**
	 * Affecte un identifiant (ne pas utiliser si l'objet est persistant car cela est automatique)
	 * On affecte ici l'identifiant idGeo.
	 */
	@Override
	public void setId (int Id) {idGeo = Id;}
	/**
	 * Affecte l'identifiant Géographique de l'objet.
	 * @param idGeo identifiant Géographique
	 */
	public void setIdGeo(int idGeo) {this.idGeo = idGeo;}
	/**
	 * Affecte la classe des représentations de l'agent.
	 * @param representationClass classe des représentations de l'agent.
	 */
	public void setRepresentationClass(Class<?> representationClass) {
		this.representationClass = representationClass;
		this.representationClassString = representationClass.getName();
	}
	List<Contrainte> contraintes = new ArrayList<Contrainte>();

	
	public void ajouteListeners(){
		if (this instanceof AgentBatiment){
			AgentGeographiqueCollection.getInstance().addAgentCollectionListener(this);
		}
		if(this instanceof AgentZoneElementaireBatie){
			AgentGeographiqueCollection.getInstance().addAgentCollectionListener(this);
		}
	}
	
	public void supprimeListeners(){
		if (this instanceof AgentBatiment){
			AgentGeographiqueCollection.getInstance().removeAgentCollectionListener(this);
		}
		if(this instanceof AgentZoneElementaireBatie){
			AgentGeographiqueCollection.getInstance().removeAgentCollectionListener(this);
		}
	}
	
	@Override
	public void instancierContraintes() {contraintes = ConfigurationSimulation.getInstance().getContraintes(this.getRepresentationClass());}
	@OneToMany
    @org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})
	public List<Contrainte> getContraintes() {return contraintes;}
	/**
	 * Ajoute une contrainte à l'agent.
	 * @param contrainte contrainte à ajouter.
	 */
	public void addContrainte(Contrainte contrainte) {contraintes.add(contrainte);}
	@Override
	public void activer() {
	  cycleDeVie();
      Scheduler.getInstance().fireEvent(new SchedulerEvent(this, SchedulerEvent.Type.AGENT_FINISHED));
	}
	/**
	 * Cycle de vie de l'agent.
	 */
	public void cycleDeVie() {
	    if (logger.isDebugEnabled()) logger.debug("***** Activation de "+this+"  avec "+this.getContraintes().size()+" contrainte(s)");
		if (this.isDeleted()) {
		    if (logger.isDebugEnabled()) logger.debug("********* agent supprimé");
			return;
		}

		// Lecture de la variable de stockage des états
		boolean stockageEtat = ConfigurationSimulation.getInstance().getStockageEtat();

		// Stockage représentations
		boolean stockageRepresentation = true;
		
		//efface les etats eventuellement crees precedemment
		effacerEtats();

		//calcule sa satisfaction
		calculerSatisfaction();

		//recuperation des actions
		listeComportements();
		if (logger.isDebugEnabled()) logger.debug(" S="+getTexteSatisfaction()+"   "+getComportementsAEssayer().size()+" comportement(s) a essayer");

		//memorisation de l'etat courant, et du meilleur etat atteint
		representationRacine = construireRepresentationCourante();
		Etat etatCourant = new Etat();
		etatCourant.setSatisfaction(getSatisfaction());
		if (stockageRepresentation)etatCourant.setRepresentationAssociee(representationRacine);
		etatCourant.setNbEtatsSuccesseurs(1);
		Etat meilleurEtat = etatCourant;
		etatRacine = etatCourant;
		if (logger.isDebugEnabled()) logger.debug("nouvel état : "+etatCourant.getId());
		this.ajouteListeners();
		
		if (getSatisfaction() >= 99.9) {
		    if (logger.isDebugEnabled()) logger.debug("Etat initial parfait");
			this.getComportementsAEssayer().clear();
			this.supprimeListeners();
			// Pour le stockage des états
			if (!stockageEtat){
				if (logger.isDebugEnabled()) logger.debug("Effacement des états");
				effacerEtats();
			}
			
			return;
		}

		try {
		while (true) {
			Scheduler.getInstance().testIfStop();
			// s'il n'y a plus de comportement à essayer
			if(getComportementsAEssayer().isEmpty()) {

				//s'il n'existe pas de représentation précédente (dans ce cas, on est a l'etat initial)
				if(etatCourant.getPrecedent() != null) {

					//il y a un etat precedent: retourner dans cet etat pour continuer a essayer les actions restantes
					if (logger.isDebugEnabled()) logger.debug("   Plus d'action a essayer pour etat courant: retour a l'etat precedent");

					this.comportementsAEssayer=tableComportements.get(etatCourant);
					if (logger.isDebugEnabled()) logger.debug("comportementsAEssayer="+comportementsAEssayer);

					Etat etatAVirer = etatCourant;
					retourEtatPrecedent(etatCourant);
					etatCourant = etatCourant.getPrecedent();
					
					// Pour le stockage des états
					if (!stockageEtat){
						if (logger.isDebugEnabled()) logger.debug("Effacement des états précédents");
						effacerEtats(etatAVirer);
					}

					continue;
				}

				//il n'y a pas d'etat precedent. l'etat courant est l'etat initial. tout l'arbre a ete explore
				//toutes les actions possibles ont ete essayees; le meilleur etat possible a ete atteint

				//retour au meilleur etat atteint
				retourMeilleurEtat(etatCourant, meilleurEtat);

				if (logger.isDebugEnabled()) logger.debug("******* Fin activation de "+this+"; Plus d'action a essayer. retour au meilleur etat atteint (S="+getTexteSatisfaction()+")");
				this.getComportementsAEssayer().clear();
				this.supprimeListeners();
				// Pour le stockage des états
				if (!stockageEtat){
					if (logger.isDebugEnabled()) logger.debug("Effacement des états");
					effacerEtats();
				}

				return;
			}

			int nbEtatsMax = ConfigurationSimulation.getInstance().getNbMaxEtatsAVisiter();
			int nbEtats = etatCourant.getNbEtatsSuccesseurs();
			if(nbEtats>nbEtatsMax) {
				//on a essaye beaucoup d'etats; on estime que ca ne sert plus a rien de chercher
				//retour au meilleur etat atteint
				retourMeilleurEtat(etatCourant, meilleurEtat);

				if (logger.isDebugEnabled()) logger.debug("******* Fin activation de "+this+"; trop d'etats visites (S="+getTexteSatisfaction()+")");
				this.getComportementsAEssayer().clear();
				this.supprimeListeners();
				// Pour le stockage des états
				if (!stockageEtat){
					if (logger.isDebugEnabled()) logger.debug("Effacement des états");
					effacerEtats();
				}

				return;
			}

			//recupere l'action a essayer et l'enleve de la liste
			Comportement comportementAEssayer = getComportementAEssayer();
			getComportementsAEssayer().remove(comportementAEssayer);

			//recupere et enleve le prochain comportement a utiliser
			//Comportement comportement = comportements.get(0);
			//comportements.remove(comportement);

			// Vide le tableau des modifications avant d'appliquer le comportement
			this.tableauModifications.clear();
			
			//appliquer le comportement
			comportementAEssayer.declencher(this);
			
			// Affiche de la liste des modifications liées au comportement appliqué
			if (logger.isDebugEnabled()) logger.debug("affichage tableau de taille " + this.tableauModifications.size());
			for (AgentModifie ag:this.tableauModifications){
				if (logger.isDebugEnabled()) logger.debug(ag.getAgent()+" : "+ag.getStatut()+" avec geom : "+ag.getAgent().getGeom());
				Map<String,Valeurs> map = ag.getMapAttributValeur();
				for (String attrib : map.keySet()){
					if (logger.isDebugEnabled()) logger.debug("         attrib : "+ attrib +" valav : "+ map.get(attrib).getValeurInitiale()+" valap : "+ map.get(attrib).getValeurFinale());
				}
			}
			
			//calcule la nouvelle satisfaction
			calculerSatisfaction();
			if (logger.isDebugEnabled()) logger.debug(" S="+getTexteSatisfaction()+" après déclenchement du comportement");

			Etat etatPrecedent = etatCourant;
			//stocke le nouvel etat
			etatCourant = new Etat();
			etatCourant.setSatisfaction(getSatisfaction());
			etatCourant.setPrecedent(etatPrecedent);
			List<AgentModifie> listeModif = new ArrayList<AgentModifie>();
			for (AgentModifie agM : tableauModifications){listeModif.add(agM);}
			etatCourant.setListeModifications(listeModif);
			etatCourant.setComportement(comportementAEssayer);
			if (stockageRepresentation){
				etatCourant.setRepresentationAssociee(construireRepresentationCourante());
				etatCourant.getRepresentationAssociee().setComportement(comportementAEssayer);
			}
			if (logger.isDebugEnabled()) logger.debug("nouvel état : "+etatCourant.getId());
			if (etatPrecedent!=null) etatPrecedent.getSuccesseurs().add(etatCourant);
			ajouteUnEtat(etatCourant);
						
			// Gestion des comportements
			tableComportements.put(etatCourant,this.getComportementsAEssayer());
			listeComportements();

			//satisfaction parfaite
			if (satisfaction >= 99.9) {
				if (logger.isDebugEnabled()) logger.debug("   REUSSI -> etat parfait atteint (S="+getTexteSatisfaction()+")");
				this.getComportementsAEssayer().clear();
				this.supprimeListeners();
				// Pour le stockage des états
				if (!stockageEtat){
					if (logger.isDebugEnabled()) logger.debug("Effacement des états");
					effacerEtats();
				}

				return;
			}

			//etat valide
			if ( estValide(etatCourant) ) {
				if (logger.isDebugEnabled()) logger.debug("   REUSSI -> etat valide (S="+getTexteSatisfaction()+")");
				if (logger.isDebugEnabled()) logger.debug("   "+this.getComportementsAEssayer().size()+" nouvelles action(s) a essayer");

				//si la satisfaction de l'etat courant est superieure a celle du meilleur etat, stocke l'etat courant comme meilleur etat
				if(etatCourant.getSatisfaction() > meilleurEtat.getSatisfaction()) {
					if (logger.isDebugEnabled()) logger.debug("   meilleur etat jamais atteint !");
					meilleurEtat = etatCourant;
				}
			}

			//etat non valide
			else {
				if (logger.isDebugEnabled()) logger.debug("   ECHEC -> etat non valide (satisfaction deteriore ou inchange, S="+getTexteSatisfaction()+")");

				if (logger.isDebugEnabled()) logger.debug("(comportementsAEssayer="+comportementsAEssayer+")");
				//retrouve  l'etat precedent
				this.comportementsAEssayer=tableComportements.get(etatCourant);
				if (logger.isDebugEnabled()) logger.debug("comportementsAEssayer="+comportementsAEssayer);

				Etat etatAVirer = etatCourant;
				retourEtatPrecedent(etatCourant);
				etatCourant = etatCourant.getPrecedent();
				
				// Pour le stockage des états
				if (!stockageEtat){
					if (logger.isDebugEnabled()) logger.debug("Effacement des états précédents");
					effacerEtats(etatAVirer);
				}
			}

		}
		} catch (InterruptedException e) {
			logger.info("agent "+this+" interruption during simulation");
		}

		/*
		while (!getComportements().isEmpty()) {
			//memorise l'etat courant
			representationCourante = construireRepresentationCourante();
			//recupere et enleve le prochain comportement a utiliser
			Comportement comportement = comportements.get(0);
			comportements.remove(comportement);

			//appliquer le comportement
			comportement.declencher(this);
			//calcule la nouvelle satisfaction
			calculerSatisfaction();
			if (logger.isDebugEnabled()) logger.debug(" S="+getTexteSatisfaction()+" après déclenchement du comportement");
			if (getSatisfaction() == 100.0) {
				//satisfaction parfaite
				logger.info("	-> etat parfait atteint (S="+getTexteSatisfaction()+")");
				actionApresSatisfactionParfaite();
				return;
			}
			else if (satisfactionAmelioree()) {
				//satisfaction a ete amelioree
				logger.info("     -> etat ameliore (S="+getTexteSatisfaction()+")");
				actionApresSatisfactionAmelioree();
			}
			else {
				//satisfaction n'a pas progresse
				logger.info("     -> etat non ameliore (deteriore ou inchange, S="+getTexteSatisfaction()+")");
				actionApresSatisfactionDeterioree();
			}
		}
		*/
	}
	
	public void retourEtatPrecedent(Etat etatC){
		if (logger.isDebugEnabled()) logger.debug("retour à l'état : "+etatC.getPrecedent().getId());			
		Etat etatCourant = etatC;
		List<AgentModifie> listeModifs = new ArrayList<AgentModifie>();
		for (AgentModifie agentM:etatCourant.getListeModifications()){listeModifs.add(agentM);}
		for(AgentModifie agentMod:listeModifs){
			AgentGeographique agentGeo = agentMod.getAgent();
			// si l'agent vient d'être créé on le supprime 
			if (agentMod.getStatut().equals(FeatureCollectionEvent.Type.ADDED)){ 
				agentGeo.remove();
				if (logger.isDebugEnabled()) logger.debug("suppression de l'agent : "+agentGeo +" avec la geom : "+agentGeo.getGeom());	
			} // si l'agent vient d'être supprimé on le recrée
			else if(agentMod.getStatut().equals(FeatureCollectionEvent.Type.REMOVED)){ 
				agentGeo.ajoute();
				if (logger.isDebugEnabled()) logger.debug("création de l'agent : "+agentGeo+" avec la geom : "+agentGeo.getGeom());
			} // si l'agent a été modifié
			if((agentMod.getStatut().equals(FeatureCollectionEvent.Type.CHANGED))||
				((agentMod.getStatut().equals(FeatureCollectionEvent.Type.REMOVED))&&(agentMod.getMapAttributValeur().size()>0))){  				
				if (logger.isDebugEnabled()) logger.debug("modification de l'agent : "+agentGeo );
				Map<String,Valeurs> mapAttributValeur = new HashMap<String,Valeurs>();
				mapAttributValeur.putAll(agentMod.getMapAttributValeur());
				for(String attrib:mapAttributValeur.keySet()){
					Valeurs valeurs = mapAttributValeur.get(attrib);
					// On récupère la précédente valeur de l'attribut
					Object valPrec = valeurs.getValeurInitiale();
					// on remplace l'ancienne valeur de l'attribut par la nouvelle
					if (attrib.equalsIgnoreCase("geom")) {// Si l'attribut modifié est la géométrie : cas spécial
						if (logger.isDebugEnabled()) logger.debug("  set" + attrib);
						if (logger.isDebugEnabled()) logger.debug("    - valeur avant : "+ agentGeo.getGeom());
						agentGeo.setGeom((GM_Object) valPrec);
						if (logger.isDebugEnabled()) logger.debug("    - valeur après : "+ agentGeo.getGeom());
					}else {
						String nomSetFieldMethod = "set" + attrib;
						if (logger.isDebugEnabled()) logger.debug("  "+nomSetFieldMethod);
						int index=0;
						Method[] methods = agentGeo.getClass().getDeclaredMethods();
						for (int i=0;i<methods.length;i++){
							Method meth = methods[i]; 
							if (meth.getName().equalsIgnoreCase(nomSetFieldMethod)){index =i;}
						}
						Method methode = methods[index];
						if (logger.isDebugEnabled()) logger.debug("    - valeur avant : "+ agentGeo.getAttribute(attrib));
						try {
							methode.invoke(agentGeo, valPrec);
						} catch (IllegalArgumentException e) {
							logger.warn("Les paramètres founis à la Méthode \"set"+attrib+"\" ne sont pas du bon type sur la classe "+agentGeo.getClass());
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							logger.warn("La Méthode \"set"+attrib+"\" n'est pas accessible sur la classe "+agentGeo.getClass());
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							logger.warn("La Méthode \"set"+attrib+"\" a renvoyé une exception sur la classe "+agentGeo.getClass());
							e.printStackTrace();
						}
						if (logger.isDebugEnabled()) logger.debug("    - valeur après : "+ agentGeo.getAttribute(attrib));
					}
				}
			}
		}
	}
	
	public void retourMeilleurEtat(Etat etatC, Etat meilleurE){
		// Détermination de la liste de tous les états précédents l'état courant 
		List<Etat> listePredecesseursEtatCourant = new ArrayList<Etat>();
		listePredecesseursEtatCourant.add(etatC);
		Etat precEC = etatC.getPrecedent();
		while(precEC!=null){
			listePredecesseursEtatCourant.add(precEC);
			precEC = precEC.getPrecedent();
		}
		// Détermination de la liste de tous les états précédents le meilleur état
		List<Etat> listePredecesseursMeilleurEtat = new ArrayList<Etat>();
		listePredecesseursMeilleurEtat.add(meilleurE);
		Etat precME = meilleurE.getPrecedent();
		while(precME!=null){
			listePredecesseursMeilleurEtat.add(precME);
			precME = precME.getPrecedent();
		}
		// Recherche du plus proche état commun
		Etat etatCommun = null;
		for(Etat etat : listePredecesseursEtatCourant){
			if(listePredecesseursMeilleurEtat.contains(etat)){
				etatCommun = etat;
				break;
			}
		}
		if (logger.isDebugEnabled()){
			if (etatCommun!=null){logger.debug("Etat commun : "+etatCommun.getId());}
			else {logger.debug("Etat commun : "+null);}
		}
		// Suppression de ce qui est commun aux deux listes
		int index = listePredecesseursEtatCourant.indexOf(etatCommun);
		List<Etat> listePredecesseursEC = listePredecesseursEtatCourant.subList(0, index);
		int index2 = listePredecesseursMeilleurEtat.indexOf(etatCommun);
		List<Etat> listePredecesseursME = listePredecesseursMeilleurEtat.subList(0, index2);
		// Retour à l'état commun
		if (logger.isDebugEnabled()){
			logger.debug("Affichage des Predecesseurs de l'EC");
			for (Etat et : listePredecesseursEC){logger.debug(et.getId());}
		}
		for (Etat etat:listePredecesseursEC){
			retourEtatPrecedent(etat);
		}
		// Retour au meilleur état
		if (logger.isDebugEnabled()){
			logger.debug("Affichage des Predecesseurs du ME");
			for (Etat et : listePredecesseursME){logger.debug(et.getId());}
		}
		for (int i=listePredecesseursME.size();i>0;i--){
			Etat etat = listePredecesseursME.get(i-1);
			allerEtatSuivant(etat);
		}
	}
		
	public void allerEtatSuivant(Etat etatC){
		if (logger.isDebugEnabled()) logger.debug("aller à l'état "+etatC.getId());
		Etat etatCourant = etatC;
		List<AgentModifie> listeModifs = etatCourant.getListeModifications();
		for(AgentModifie agentMod:listeModifs){
			AgentGeographique agentGeo = agentMod.getAgent();
			// si l'agent vient d'être créé on le crée 
			if (agentMod.getStatut().equals(FeatureCollectionEvent.Type.ADDED)){ 
				agentGeo.ajoute();
				if (logger.isDebugEnabled()) logger.debug("création de l'agent : "+agentGeo +" avec la geom : "+agentGeo.getGeom());	
			} // si l'agent vient d'être supprimé on le supprime
			else if(agentMod.getStatut().equals(FeatureCollectionEvent.Type.REMOVED)){ 
				agentGeo.remove();
				if (logger.isDebugEnabled()) logger.debug("suppression de l'agent : "+agentGeo+" avec la geom : "+agentGeo.getGeom());
			} // si l'agent a été modifié
			else if(agentMod.getStatut().equals(FeatureCollectionEvent.Type.CHANGED)){  
				if (logger.isDebugEnabled()) logger.debug("modification de l'agent : "+agentGeo );
				for(String attrib:agentMod.getMapAttributValeur().keySet()){
					Valeurs valeurs = agentMod.getMapAttributValeur().get(attrib);
					// On récupère la précédente valeur de l'attribut
					Object valSuiv = valeurs.getValeurFinale();
					// on remplace l'ancienne valeur de l'attribut par la nouvelle
					if (attrib.equalsIgnoreCase("geom")) {// Si l'attribut modifié est la géométrie : cas spécial
						if (logger.isDebugEnabled()) logger.debug("  set" + attrib);
						if (logger.isDebugEnabled()) logger.debug("    - valeur avant : "+ agentGeo.getGeom());
						agentGeo.setGeom((GM_Object) valSuiv);
						if (logger.isDebugEnabled()) logger.debug("    - valeur après : "+ agentGeo.getGeom());
					}else {
						String nomSetFieldMethod = "set" + attrib;
						if (logger.isDebugEnabled()) logger.debug("  "+nomSetFieldMethod);
						int index=0;
						Method[] methods = agentGeo.getClass().getDeclaredMethods();
						for (int i=0;i<methods.length;i++){
							Method meth = methods[i]; 
							if (meth.getName().equalsIgnoreCase(nomSetFieldMethod)){index =i;}
						}
						Method methode = methods[index];
						if (logger.isDebugEnabled()) logger.debug("    - valeur avant : "+ agentGeo.getAttribute(attrib));
						try {
							methode.invoke(agentGeo, valSuiv);
						} catch (IllegalArgumentException e) {
							logger.warn("Les paramètres founis à la Méthode \"set"+attrib+"\" ne sont pas du bon type sur la classe "+agentGeo.getClass());
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							logger.warn("La Méthode \"set"+attrib+"\" n'est pas accessible sur la classe "+agentGeo.getClass());
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							logger.warn("La Méthode \"set"+attrib+"\" a renvoyé une exception sur la classe "+agentGeo.getClass());
							e.printStackTrace();
						}
						if (logger.isDebugEnabled()) logger.debug("    - valeur après : "+ agentGeo.getAttribute(attrib));
					}
				}
			}
		}	
	}

	Map<Etat,List<Comportement>> tableComportements = new HashMap<Etat,List<Comportement>>();

	private ElementRepresentation representationRacine=null;
	/**
	 * Renvoie la valeur de l'attribut representationRacine.
	 * @return la valeur de l'attribut representationRacine
	 */
	public ElementRepresentation getRepresentationRacine() {return this.representationRacine;}
	/**
	 * Affecte la valeur de l'attribut representationRacine.
	 * @param representationRacine l'attribut representationRacine à affecter
	 */
	public void setRepresentationRacine(ElementRepresentation representationRacine) {this.representationRacine = representationRacine;}
	
	private Etat etatRacine=null;
	/**
	 * @param etatRacine l'attribut etatRacine à affecter
	 */
	public void setEtatRacine(Etat etatRacine) {this.etatRacine = etatRacine;}
	/**
	 * @return la valeur de l'attribut etatRacine
	 */
	public Etat getEtatRacine() {return etatRacine;}

	/**
	 * @param representation
	 * @param prec
	 * @return
	 */
//	public int nbEtats(Etat etat){
//		if( etat.getSuccesseurs().isEmpty() ) return 1;
//		int nb=1;
//		for( Etat e : etat.getSuccesseurs() ) nb += nbEtats(e);
//		return nb;
//	}
	
	public void ajouteUnEtat(Etat etat){
		Etat et = etat;
		et.setNbEtatsSuccesseurs(1);
		while(et.getPrecedent()!=null){
			et = et.getPrecedent();
			et.setNbEtatsSuccesseurs(et.getNbEtatsSuccesseurs()+1);
		}
	}
	/**
	 * @param representation
	 * @param prec
	 * @return
	 */
	public boolean estValide(ElementRepresentation representation) {
		//l'etat d'un agent geographique objet est valide lorsque sa satisfaction est nettement plus grande a celle de sont etat precedent
		if( representation.getPrecedent() == null ) return true;
		logger.debug("satisfaction = "+getSatisfaction());
		logger.debug("satisfaction précédente = "+representation.getPrecedent().getSatisfaction());
		return getSatisfaction() - representation.getPrecedent().getSatisfaction() > ConfigurationSimulation.getInstance().getSeuilSatisfactionValidite();
	}
	
	/**
	 * @param representation
	 * @param prec
	 * @return
	 */
	public boolean estValide(Etat etat) {
		//l'etat d'un agent geographique objet est valide lorsque sa satisfaction est nettement plus grande a celle de sont etat precedent
		if( etat.getPrecedent() == null ) return true;
		logger.debug("satisfaction = "+getSatisfaction());
		logger.debug("satisfaction précédente = "+etat.getPrecedent().getSatisfaction());
		return getSatisfaction() - etat.getPrecedent().getSatisfaction() > ConfigurationSimulation.getInstance().getSeuilSatisfactionValidite();
	}
	
	/**
	 * efface l'arbre d'etats de l'agent
	 */
	public void effacerEtats(){
		if(etatRacine == null) return;
		effacerEtats(etatRacine);
		etatRacine = null;
	}
	/**
	 * efface un etat donne de l'agent, ainsi que tous ses etats successeurs eventuels
	 * @param etat
	 */
	private void effacerEtats(Etat etat) {
		//s'il n'y a pas d'etats successeurs, sortir
		if ( etat.getSuccesseurs().isEmpty() ) return;
		//effacement des etats successeurs (appels recusrsifs)
		for( Etat e : etat.getSuccesseurs() ) effacerEtats(e);
		etat.getSuccesseurs().clear();
	}

	/**
	 * @return TexteSatisfaction
	 */
	private String getTexteSatisfaction() {return String.valueOf(this.getSatisfaction());}

	protected Comportement getComportementAEssayer() {
		if(logger.isDebugEnabled()) logger.debug("choix du Comportement a essayer de "+this);

		//FIXME à vérifier ...
		//parmi les actions proposees par les contraintes ayant la plus forte priorite, prends celle ayant le plus fort poids
		/*
		double prioriteMax = Double.NEGATIVE_INFINITY;
		double poidsMax = Double.NEGATIVE_INFINITY;
		Comportement comportementAEssayer = null;


		for (Comportement action : getComportementsAEssayer()){

			//verification de la priorite
			if(logger.isTraceEnabled()) logger.trace("   action: "+action+" priorite= "+((Contrainte)action.get).getPriorite());
			if (((ContrainteGeographique)action.getContrainte()).getPriorite() < prioriteMax) continue;

			//verification du poids
			if(logger.isTraceEnabled()) logger.trace("   poids= "+action.getPoids());
			if (action.getPoids() <= poidsMax) continue;

			if(logger.isTraceEnabled()) logger.trace("      meilleure action!");
			actionAEssayer=action;
			prioriteMax=((ContrainteGeographique)action.getContrainte()).getPriorite();
			poidsMax=action.getPoids();
		}
		*/

		//if(logger.isTraceEnabled() && comportementAEssayer !=null) logger.trace("action: "+comportementAEssayer+" priorite= "+((ContrainteGeographique)actionAEssayer.getContrainte()).getPriorite()+" poids= "+actionAEssayer.getPoids());
		Comportement comportement = comportementsAEssayer.get(0);
		if(logger.isDebugEnabled()) logger.debug(comportement+" choisi");
		return comportement;
	}

	private List<Comportement> comportementsAEssayer;
	public List<Comportement> getComportementsAEssayer() { return this.comportementsAEssayer; }

	/**
	 *
	 */
	private void listeComportements() {
		if (logger.isTraceEnabled()) logger.trace("recupere actions des contraintes non satisfaites d'agent de type: " + this.getClass().getSimpleName());
		//recupere les action des contraintes non satisfaites et recalcule la priorite de chacune d'elles
		comportementsAEssayer = new ArrayList<Comportement>();
		if (this.isDeleted()) return;

		Comparator<Contrainte> PRIORITY_ORDER =
			new Comparator<Contrainte>() {
			public int compare(Contrainte e1, Contrainte e2) {
				if (e1.getPriorite()< e2.getPriorite()) return -1;
				return (e1.getPriorite()== e2.getPriorite())?0:1;
			}
		};
		final AgentGeographique agent = this;
		Comparator<Contrainte> SATISFACTION_ORDER =
			new Comparator<Contrainte>() {
			public int compare(Contrainte e1, Contrainte e2) {
				double satisfaction1 = e1.getSatisfaction(agent);
				double satisfaction2 = e2.getSatisfaction(agent);
				if (satisfaction1<satisfaction2) return -1;
				return (satisfaction1==satisfaction2)?0:1;
			}
		};

		List<Contrainte> contraintesNonSatisfaites = new ArrayList<Contrainte>();
		for(Contrainte contrainte:getContraintes()) {
			//si la contrainte est satisfaite, ne rien faire
			double satisfactionContrainte = contrainte.getSatisfaction(this);
			if( satisfactionContrainte >= 100.0/**(1-ConfigurationSimulation.getInstance().getSeuilSatisfactionValidite())*/) {
				if (logger.isTraceEnabled()) logger.trace("	"+contrainte+" (satisfaite)");
				continue;
			}
			contraintesNonSatisfaites.add(contrainte);
			//sinon, calculer sa priorité et proposer ses actions
			//TODO : voir priorité
		}
		boolean priority = true;// utiliser la priorité
		if (priority) Collections.sort(contraintesNonSatisfaites,PRIORITY_ORDER);
		else Collections.sort(contraintesNonSatisfaites,SATISFACTION_ORDER);
		for(Contrainte contrainte:contraintesNonSatisfaites) {
			if (logger.isDebugEnabled()) logger.debug("	"+contrainte+" (non satisfaite) avec priorité = "+contrainte.getPriorite()+" et "+contrainte.getComportements().size()+" comportements");
			List<Comportement> comportementsContrainte = contrainte.getComportements();
			comportementsAEssayer.addAll(comportementsContrainte);
		}
		if(logger.isTraceEnabled())logger.trace(comportementsAEssayer.size()+" comportements trouvés");
	}
	//List<Comportement> comportements = new ArrayList<Comportement>();
	/**
	 *
	@OneToMany
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})
	public List<Comportement> getComportements() {return comportements;}
	 */
	/**
	 * @param contraintes
	 */
	public void setContraintes(List<Contrainte> contraintes) {this.contraintes = contraintes;}
	/**
	public void setComportements(List<Comportement> comportements) {this.comportements = comportements;}
	 */

	protected double satisfaction=0.0;
	@Override
	public double getSatisfaction() {return this.satisfaction;}
	@Override
	public void setSatisfaction(double satisfaction) {this.satisfaction = satisfaction;}
	/**
	 * Calcul de la satisfaction de l'agent Géographique.
	 * C'est par defaut la satisfaction moyenne des contraintes pondérées par leur importance.
	 */
	public void calculerSatisfaction() {
		int nbContraintes=getContraintes().size();
		if(logger.isDebugEnabled()) logger.debug("satisfaction de "+this.getClass().getSimpleName()+" "+this.getIdGeo()+" (nb contraintes="+nbContraintes+") simule = "+this.getSimule());
		//si l'agent n'a pas de contrainte ou qu'il est supprime, il est parfaitement satisfait
		if ( nbContraintes==0 || this.isDeleted() ) {
			this.satisfaction=100.0;
			if(logger.isDebugEnabled()) logger.debug("   S=100");
			return;
		}
		//calcul de la moyenne des satisfactions des contrainte ponderee par leur importance
		double sommeSatisfactions=0.0;
		double sommeImportances=0.0;
		for(Contrainte contrainte:getContraintes()) {
		    if(logger.isDebugEnabled()) {
		        logger.debug("   Contrainte "+contrainte.getExpression()+" comportements ="+contrainte.getComportements().size());
		    }
			double satisfactionContrainte = contrainte.getSatisfaction(this);
			if (logger.isTraceEnabled()) {
			    logger.trace("		SatisfactionContrainte = "+satisfactionContrainte);
			}
			sommeSatisfactions += contrainte.getImportance()*satisfactionContrainte;
			if (logger.isTraceEnabled()) {
			    logger.trace("		SommeSatisfactions = "+sommeSatisfactions);
			}
			sommeImportances += contrainte.getImportance();
			if (logger.isTraceEnabled()) {
			    logger.trace("		SommeImportances = "+sommeImportances);
			}
			if(logger.isDebugEnabled()) {
			    logger.debug("   Contrainte "+contrainte.getClass().getSimpleName()+" imp="+contrainte.getImportance()+" s="+satisfactionContrainte);
			}
		}

		satisfaction=(sommeImportances==0)?100.0:sommeSatisfactions / sommeImportances;

		if(logger.isDebugEnabled()) {
		    logger.debug("satisfaction de " +
		        this.getClass().getSimpleName() + " S=" + satisfaction);
        }
	}

	protected boolean supprime=false;
	/**
	 * Renvoie l'état de suppression de l'agent.
	 * @return vrai si l'agent a été supprimé, faux sinon.
	 */
	@Transient
	public boolean isDeleted() {return this.supprime;}
	/**
	 * Affecte l'état de suppression de l'agent.
	 * @param supprime vrai si l'agent a été supprimé, faux sinon.
	 */
	public void setSupprime(boolean supprime) {	
		this.supprime = supprime;
	}
	
	public void remove() {
		this.setSupprime(true);
		AgentGeographiqueCollection collection = AgentGeographiqueCollection.getInstance();
		collection.fireActionPerformed(new AgentCollectionEvent(
					collection, 
					this, 
					FeatureCollectionEvent.Type.REMOVED,
					null,
					null,
					null));
	}
	
	public void ajoute() {
		this.setSupprime(false);
		AgentGeographiqueCollection collection = AgentGeographiqueCollection.getInstance();
		collection.fireActionPerformed(new AgentCollectionEvent(
					collection, 
					this, 
					FeatureCollectionEvent.Type.ADDED,
					null,
					null,
					null));
	}

	public int size() {return elements.size();}
	public Object[] toArray() {return elements.toArray();}
	public <T> T[] toArray(T[] a) {return elements.toArray(a);}
	@Override
	public String toString() {
		String result = this.getClass().getSimpleName()+" - "+this.getIdGeo();
		result+="("+this.getCreation().getMin()+"-"+this.getCreation().getMax()+") ";
		result+="("+this.getDestruction().getMin()+"-"+this.getDestruction().getMax()+") ";
		/*
		result+="\n";
		for (Integer date:this.getDates()) {
			result+=" - "+date;
		}
		result+="\n";
		for (ElementRepresentation rep:this.getElements()) {
			result+=" - "+rep.getIdRep()+" ("+rep.getDateSourceSaisie()+") - "+ rep.getClass().getName() +"\n";
		}
		*/
		return result;
	}

	/**
	 * @param date une date
	 * @return vrai si l'élément Géographique existe à la date spécifiée, faux sinon.
	 */
	public boolean existsIn(int date) {
		return (creation.getMax()<=date)&&((destruction.getMax()>date)||(destruction.getMax()==Integer.MIN_VALUE));
		// FIXME ???
		//return this.getRepresentation(date)!=null;
	}
	private Set<AgentGeographique> predecesseurs = new HashSet<AgentGeographique>();
	/**
	 * Renvoie la valeur de l'attribut predecesseurs.
	 * @return la valeur de l'attribut predecesseurs
	 */
	@ManyToMany
	public Set<AgentGeographique> getPredecesseurs() {return this.predecesseurs;}
	/**
	 * Affecte la valeur de l'attribut predecesseurs.
	 * @param predecesseurs l'attribut predecesseurs à affecter
	 */
	public void setPredecesseurs(Set<AgentGeographique> predecesseurs) {this.predecesseurs = predecesseurs;}

	private Set<AgentGeographique> successeurs = new HashSet<AgentGeographique>();
	/**
	 * Renvoie la valeur de l'attribut successeurs.
	 * @return la valeur de l'attribut successeurs
	 */
	@ManyToMany
	public Set<AgentGeographique> getSuccesseurs() {return this.successeurs;}
	/**
	 * Affecte la valeur de l'attribut successeurs.
	 * @param successeurs l'attribut successeurs à affecter
	 */
	public void setSuccesseurs(Set<AgentGeographique> successeurs) {this.successeurs = successeurs;}
	/**
	 * @return 0 si l'agent est simulé, 1 sinon
	 */
	public int getSimule() {return (this.isSimulated())?0:1;}
	private boolean simule = false;
	/**
	 * @return
	 */
	public boolean isSimulated() {return simule;}
	/**
	 * @param simule
	 */
	public void setSimulated(boolean simule) {this.simule=simule;}

	
	List<AgentModifie> tableauModifications = new ArrayList<AgentModifie>();
	
	private void addAgentModTableauModifications (AgentModifie agentMod){
		// Si l'agentMod est créé on l'ajoute
		if (agentMod.getStatut().equals(FeatureCollectionEvent.Type.ADDED)){
			String choix = "ajout";
			AgentModifie agentM = null;
			for (AgentModifie agentTab:tableauModifications){
				// Si lors d'une précédente étape l'agent avait été supprimé : il faut supprimé la suppression
				if ((agentMod.getAgent().equals(agentTab.getAgent()))&&
						(agentTab.getStatut().equals(FeatureCollectionEvent.Type.REMOVED))){
					// si il y avait des modifications avant suppression : on modifie le statut en changed
					if(agentTab.getMapAttributValeur().size()>0){
						choix = "remplacement";
						agentTab.setStatut(FeatureCollectionEvent.Type.CHANGED);
					}else{ // si pas de modifications avant : on le supprime
						choix = "suppression" ;
						agentM = agentTab;
					}
				}
			}
			if (choix.equals("suppression")){
				this.tableauModifications.remove(agentM);
			}else if (choix.equals("ajout")){
				this.tableauModifications.add(agentMod);
			}
		}// Si l'agent est supprimé on vérifie qu'il n'était pas créé dans cette liste
		else if (agentMod.getStatut().equals(FeatureCollectionEvent.Type.REMOVED)){
			String choix = "ajout";
			AgentModifie agentM = null;
			for (AgentModifie agentTab:tableauModifications){
				if (agentMod.getAgent().equals(agentTab.getAgent())){
					if (agentTab.getStatut().equals(FeatureCollectionEvent.Type.ADDED)){
						choix = "suppression" ;
						agentM=agentTab;
					}else if(agentTab.getStatut().equals(FeatureCollectionEvent.Type.CHANGED)){
						choix = "remplacement";
						agentTab.setStatut(FeatureCollectionEvent.Type.REMOVED);
					}
				}	
			}
			if (choix.equals("suppression")){
				this.tableauModifications.remove(agentM);
			}else if (choix.equals("ajout")){
				this.tableauModifications.add(agentMod);
			}
		}// Si l'agent est modifié on vérifie qu'il n'avait pas été modifié précédement
		else if (agentMod.getStatut().equals(FeatureCollectionEvent.Type.CHANGED)){
			boolean dansListe = false;
			boolean suppression = false;
			AgentModifie agentAsupp = null;
			// si l'agent est déjà dans la liste
			for (AgentModifie agentTab:this.tableauModifications){
				if (agentMod.getAgent().equals(agentTab.getAgent())){ 
					dansListe = true;
					String attASuprimer = "";
					Map<String,Valeurs> mapAgentMod = agentMod.getMapAttributValeur();
					for(String attrib : mapAgentMod.keySet()){
						// Si l'attribut est déjà modifié pour cet agent
						if (agentTab.getMapAttributValeur().containsKey(attrib)){
							Object valFinale = agentMod.getMapAttributValeur().get(attrib).getValeurFinale();
							Object valInitiale = agentTab.getMapAttributValeur().get(attrib).getValeurInitiale();
							if ((valInitiale==null)&&(valFinale==null)||
								((valInitiale!=null)&&(valFinale!=null)&&(valInitiale.equals(valFinale)))){ // on supprime l'attribut
								suppression = true;
								attASuprimer = attrib;
							}else{
								agentTab.getMapAttributValeur().get(attrib).setValeurFinale(valFinale);
							}
						}else{// l'attribut est ajouté
							agentTab.addAttributValeurs(attrib, mapAgentMod.get(attrib));
						}
					}
					if(suppression){
						agentTab.removeAttributValeurs(attASuprimer);
						if (agentTab.getMapAttributValeur().isEmpty()){
							agentAsupp = agentTab;
						}
					}
				}
			}
			if (agentAsupp!=null){// Si un agent du tableau n'a plus d'attribut modifié on le supprime
				// Si il ne fait pas partie des agents ajoutés et supprimés !!!
				if (agentAsupp.getStatut().equals(FeatureCollectionEvent.Type.CHANGED)){
					this.tableauModifications.remove(agentAsupp);
				}else{
					logger.debug("statut : "+agentAsupp.getStatut());
				}
			}
			// si l'agent n'est pas dans la liste il faut le créer
			if (dansListe==false){
				this.tableauModifications.add(agentMod);
			}
		}
	}
	
	@Override
	public void changed(AgentCollectionEvent event) {
		
			AgentGeographique ag = (AgentGeographique)event.getFeature();

			Type st = event.getType();
			AgentModifie agentMod = new AgentModifie(ag,st);
			if (event.getAttribut()!=null){
				String attrib = event.getAttribut();
				Object valAv = event.getValeurAvant();
				Object valAp = event.getValeurApres();
				agentMod.addAttributValeurs(attrib, valAv, valAp);
			}
			addAgentModTableauModifications(agentMod);
		}

    /**
     *
     */
    public void applyEvolutionRules(List<EvolutionRule> listeR) {
        EvolutionRuleBase ruleBase = EvolutionRuleBase.getInstance();
        ruleBase.apply(this,listeR);
    }
	
}
