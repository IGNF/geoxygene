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

package fr.ign.cogit.appli.geopensim.agent.meso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Transient;

import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.agent.AgentGeographiqueCollection;
import fr.ign.cogit.appli.geopensim.agent.event.AgentCollectionEvent;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.appli.geopensim.feature.meso.GroupeBatiments;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.geoxygene.api.feature.event.FeatureCollectionEvent;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;




/**
 * @author Florence Curie
 *
 */
public class AgentGroupeBatiments extends AgentMeso {
	//static Logger logger=Logger.getLogger(AgentGroupeBatiments.class.getName());
	/**
	 * Constructeur
	 */
	public AgentGroupeBatiments() {
		super();
		this.representationClass=GroupeBatiments.class;
		this.representationClassString=representationClass.getName();
		AgentGeographiqueCollection collection = AgentGeographiqueCollection.getInstance();
		collection.fireActionPerformed(new AgentCollectionEvent(
				collection, 
				this, 
				FeatureCollectionEvent.Type.ADDED));
	}

	/**
	 * Constructeur
	 */
	public AgentGroupeBatiments(int idGeo) {
		super(idGeo,GroupeBatiments.class);
		AgentGeographiqueCollection collection = AgentGeographiqueCollection.getInstance();
		collection.fireActionPerformed(new AgentCollectionEvent(
				collection, 
				this, 
				FeatureCollectionEvent.Type.ADDED));
	}
	
	/**
	 * Constructeur
	 */
	public AgentGroupeBatiments(GM_Polygon geometrie, AgentZoneElementaireBatie zoneElementaireBatie, Set<AgentBatiment> listeBatiments, boolean simule) {
		this();
		this.setGeom(geometrie);
		this.setSimulated(simule);
		this.setZoneElementaireBatie(zoneElementaireBatie);
		this.setBatiments(listeBatiments);
		zoneElementaireBatie.addGroupeBatiments(this);
		for (AgentBatiment bati:listeBatiments){
			bati.setGroupeBatiments(this);
		}
	}
	
	private AgentZoneElementaireBatie zoneElementaireBatie = null;
	/**
	 * Renvoie la valeur de l'attribut zoneElementaire.
	 * @return la valeur de l'attribut zoneElementaire
	 */
	@Transient
	public AgentZoneElementaireBatie getZoneElementaireBatie() {return this.zoneElementaireBatie;}
	/**
	 * Affecte la valeur de l'attribut zoneElementaire.
	 * @param zoneElementaire l'attribut zoneElementaire à affecter
	 */
	public void setZoneElementaireBatie(AgentZoneElementaireBatie zoneElementaire) {
		AgentZoneElementaireBatie zoneElementairePrecedente = this.zoneElementaireBatie;
		this.zoneElementaireBatie = zoneElementaire;
		if (((zoneElementairePrecedente==null)&&(this.zoneElementaireBatie!=null))||
				((zoneElementairePrecedente!=null)&&((this.zoneElementaireBatie==null)||!zoneElementairePrecedente.equals(this.zoneElementaireBatie)))){
			AgentGeographiqueCollection collection = AgentGeographiqueCollection.getInstance();
			collection.fireActionPerformed(new AgentCollectionEvent(
					collection, 
					this, 
					FeatureCollectionEvent.Type.CHANGED,
					"ZoneElementaireBatie",
					zoneElementairePrecedente,
					this.zoneElementaireBatie));
		}
	}
	
	private Set<AgentBatiment> batiments = new HashSet<AgentBatiment>(0);
	/**
	 * @return la valeur de l'attribut batiments
	 */
	@Transient
	public Set<AgentBatiment> getBatiments() {return this.batiments;}
	/**
	 * @param batiments l'attribut batiments à affecter
	 */
	public void setBatiments(Set<AgentBatiment> batiments) {
		Set<AgentBatiment> listeBatiPrecedent = new HashSet<AgentBatiment>(this.getBatiments());
//		for (AgentBatiment agentB:this.getBatiments())listeBatiPrecedent.add(agentB);
		this.batiments = batiments;

		AgentGeographiqueCollection collection = AgentGeographiqueCollection.getInstance();
		collection.fireActionPerformed(new AgentCollectionEvent(
				collection, 
				this, 
				FeatureCollectionEvent.Type.CHANGED,
				"Batiments",
				listeBatiPrecedent,
				this.getBatiments()));
	}

	private Set<AgentAlignement> alignements = new HashSet<AgentAlignement>(0);
	/**
	 * @return la valeur de l'attribut alignements
	 */
	@Transient
	public Set<AgentAlignement> getAlignements() {return this.alignements;}
	/**
	 * @param alignements l'attribut alignements à affecter
	 */
	public void setAlignements(Set<AgentAlignement> alignements) {this.alignements = alignements;}
	
	/**
	 * @param batiment
	 */
	public void addBatiment(AgentBatiment batiment) {
	    this.batiments.add(batiment);
	    //this.getZoneElementaireBatie().getBatiments().add(batiment);
	    //this.getZoneElementaireBatie().getUniteBatie().getBatiments().add(batiment);
	    this.getZoneElementaireBatie().buildBuildingTriangulation();
	    AgentGeographiqueCollection.getInstance().getBatiments().add(batiment);
	}
	//	private Set<AgentAlignement> alignements;
	/**
	 * @return la valeur de l'attribut alignements
	 */
	@Transient
	//	public Set<AgentAlignement> getAlignements() {return this.alignements;}
	/**
	 * @param alignements l'attribut alignements à affecter
	 */
	//	public void setAlignements(Set<AgentAlignement> alignements) {this.alignements = alignements;}

	@Override
	public void prendreAttributsRepresentation(ElementRepresentation representation) {
		super.prendreAttributsRepresentation(representation);
	    if (this.getBatiments()!=null) {
//	      logger.info("" + AgentGeographiqueCollection.getInstance().getBatiments().size() + " batiments dont " + this.getBatiments().size() + " dans le groupe");
	      AgentGeographiqueCollection.getInstance().getBatiments().removeAll(this.getBatiments());
//          logger.info("" + AgentGeographiqueCollection.getInstance().getBatiments().size() + " batiments après");
	    }
		GroupeBatiments groupeBatiments = (GroupeBatiments)representation;
		// ZoneElementaireBatie
		if (groupeBatiments.getZoneElementaireUrbaine()==null) {
			logger.error("le groupe de bâtiments "+groupeBatiments+" n'a pas de zone élémentaire");
		} else {
			if (groupeBatiments.getZoneElementaireUrbaine().getAgentGeographique()==null) {
				logger.error(groupeBatiments.getZoneElementaireUrbaine()+" n'a pas d'agent");
			} else {
				this.setZoneElementaireBatie((AgentZoneElementaireBatie) groupeBatiments.getZoneElementaireUrbaine().getAgentGeographique());
			}
		}
		// Batiments
		if (this.getBatiments()!=null) {
		  for (AgentBatiment agentBatiment:this.getBatiments()) {
		    //agentBatiment.setGeom(null);
		    agentBatiment.setSupprime(true);
		  }
		  this.getBatiments().clear();
		}
		Set<AgentBatiment> batimentsGroupe = new HashSet<AgentBatiment>(0);
		for (Batiment batiment:groupeBatiments.getBatiments()) {
			AgentBatiment agentBatiment = (AgentBatiment)batiment.getAgentGeographique();
			agentBatiment.prendreAttributsRepresentation(batiment);
			batimentsGroupe.add(agentBatiment);
		}
		this.setBatiments(batimentsGroupe);
		if (this.getBatiments()!=null) {
//          logger.info("" + AgentGeographiqueCollection.getInstance().getBatiments().size() + " batiments dont " + this.getBatiments().size() + " dans le groupe");
		  AgentGeographiqueCollection.getInstance().getBatiments().addAll(this.getBatiments());
//          logger.info("" + AgentGeographiqueCollection.getInstance().getBatiments().size() + " batiments après");
		}
		// Alignement
		//		Set<AgentAlignement> alignements = new HashSet<AgentAlignement>();
		//		for (Alignement alignement:groupeBatiments.getAlignements()) {
		//			alignements.add((AgentAlignement) alignement.getAgentGeographique());
		//		}
		//		this.setAlignements(alignements);
	}

	@Override
	public ElementRepresentation construireRepresentationCourante() {
		GroupeBatiments groupeBatiments = (GroupeBatiments) super.construireRepresentationCourante();
		groupeBatiments.setZoneElementaireUrbaine((ZoneElementaireUrbaine) this.getZoneElementaireBatie().getRepresentationCourante());
		groupeBatiments.setDateSourceSaisie(this.getDateSimulee());
		for (AgentBatiment agentBatiment:this.getBatiments()) {
			Batiment batiment = (Batiment) agentBatiment.construireRepresentationCourante();
			groupeBatiments.addBatiment(batiment);
			batiment.setGroupeBatiments(groupeBatiments);
		}
		//		if (this.getAlignements()!=null){
		//			for (AgentAlignement agentAlignement:this.getAlignements()) {
		//				Alignement alignement = (Alignement) agentAlignement.construireRepresentationCourante();
		//				groupeBatiments.addAlignement(alignement);
		//				alignement.setGroupeBatiments(groupeBatiments);
		//			}
		//		}
		return groupeBatiments;
	}

	@Override
	public List<AgentGeographique> getComposants() {
		List<AgentGeographique> composants = new ArrayList<AgentGeographique> (this.getBatiments().size());
		composants.addAll(this.getBatiments());
		//	    composants.addAll(this.getAlignements());
		return composants;
	}

	@Override
	public void removeComposant(AgentGeographique composantASupprimer) {
		//		this.getAlignements().remove(composantASupprimer);
		if (composantASupprimer instanceof AgentBatiment){
			Set<AgentBatiment> listeBatiPrecedent = new HashSet<AgentBatiment>(this.getBatiments());
//			for (AgentBatiment agentB:this.getBatiments()) {
//			  listeBatiPrecedent.add(agentB);
//			}
			this.getBatiments().remove(composantASupprimer);
			AgentGeographiqueCollection collection = AgentGeographiqueCollection.getInstance();
			collection.fireActionPerformed(new AgentCollectionEvent(
					collection, 
					this, 
					FeatureCollectionEvent.Type.CHANGED,
					"Batiments",
					listeBatiPrecedent,
					this.getBatiments()));
		}
	}

	/**
	 * @return la distance au groupe de batiment le plus proche de ce groupe de batiment
	 */
	public double getDistanceGroupeBatimentsLePlusProche() {
		AgentZoneElementaireBatie agentZoneElementaire = this.getZoneElementaireBatie();
		double distanceMin = Double.MAX_VALUE;
		for (AgentGroupeBatiments groupeBatis:agentZoneElementaire.getGroupesBatiments()) {
			if ((!this.equals(groupeBatis))&&(!groupeBatis.isDeleted())) {
				double distance = this.getGeom().distance(groupeBatis.getGeom());
				if (distance<distanceMin) {
					distanceMin=distance;
				}
			}
		}
		return distanceMin;
	}
	
	/**
	 * @return surfaceGroupesBatimentsIntersectes la surface d'intersection des groupes de bâtiments qui intersectent le groupe de bâtiments
	 */
	public double getSurfaceGroupesBatimentsIntersectes() {
		AgentZoneElementaireBatie agentZoneElementaire = this.getZoneElementaireBatie();
		if (agentZoneElementaire==null) return 0;
		double surfaceGroupesBatimentsIntersectes = 0;
		logger.debug("poly ref : "+this.getGeom());
		for (AgentGroupeBatiments groupeBati:agentZoneElementaire.getGroupesBatiments()){
			if ((!groupeBati.equals(this))&&(!groupeBati.isDeleted())) {
			    IGeometry intersection = groupeBati.getGeom().intersection(getGeom());
				if (intersection!=null) {
					surfaceGroupesBatimentsIntersectes+=intersection.area();
					logger.debug("poly intersect : "+groupeBati.isDeleted()+intersection.area()+" : "+groupeBati.getGeom());
				}
			}
		}
		return surfaceGroupesBatimentsIntersectes;
	}
	
	/**
	 * @return groupesBatimentsIntersectes le nombre de groupe de bâtiments qui intersectent le groupe de bâtiments
	 */
	public int getGroupesBatimentsIntersectes() {
		AgentZoneElementaireBatie agentZoneElementaire = this.getZoneElementaireBatie();
		if (agentZoneElementaire==null) return 0;
		int groupesBatimentsIntersectes = 1;
		for (AgentGroupeBatiments groupeBati:agentZoneElementaire.getGroupesBatiments()){
			if ((!groupeBati.equals(this))&&(!groupeBati.isDeleted())&&(groupeBati.getGeom().intersects(getGeom()))) {
				groupesBatimentsIntersectes=0;
			}
		}
		return groupesBatimentsIntersectes;
	}
	
	/**
	 * @return tailleMoyenne la taille moyenne des bâtiments du groupe de bâtiments
	 */
	public double getTailleMoyenne() {
		double tailleMoyenne = 0;
		for (AgentBatiment bati:this.getBatiments()){
			tailleMoyenne += bati.getGeom().area();
		}
		tailleMoyenne /= this.getBatiments().size();
		return tailleMoyenne;
	}
}
