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

import javax.persistence.Entity;
import javax.persistence.Transient;

import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.appli.geopensim.feature.meso.UniteUrbaine;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;

/**
 * Agent Unité batie (i.e. ville).
 * 
 * @author Julien Perret
 *
 */
@Entity
public class AgentUniteBatie extends AgentMeso {
	//static Logger logger=Logger.getLogger(AgentUniteBatie.class.getName());

	/**
	 * 
	 */
	public AgentUniteBatie() {
		super();
		this.representationClass=UniteUrbaine.class;
		this.representationClassString=representationClass.getName();
	}

	/**
	 * Constructeur à partir d'un identififiant.
	 * @param idGeo identifiant de l'agent Unité batie.
	 */
	public AgentUniteBatie(int idGeo) {super(idGeo,UniteUrbaine.class);}

	/**
	 * Renvoie la valeur de l'attribut batiments.
	 * @return la valeur de l'attribut batiments
	 */
	@Transient
	public Set<AgentBatiment> getBatiments() {
	    Set<AgentBatiment> batiments = new HashSet<AgentBatiment>();
	    for(AgentZoneElementaireBatie zone:this.getZonesElementaires()){
	    	for (AgentGroupeBatiments groupeBatiments:zone.getGroupesBatiments()){
	    		batiments.addAll(groupeBatiments.getBatiments());
	    	}
	    }
	    return batiments;
	}

	private Set<AgentZoneElementaireBatie> zonesElementaires;
	/**
	 * Renvoie la valeur de l'attribut zonesElementaires.
	 * @return la valeur de l'attribut zonesElementaires
	 */
	@Transient
	public Set<AgentZoneElementaireBatie> getZonesElementaires() {return this.zonesElementaires;}
	/**
	 * Affecte la valeur de l'attribut zonesElementaires.
	 * @param zonesElementaires l'attribut zonesElementaires à affecter
	 */
	public void setZonesElementaires(Set<AgentZoneElementaireBatie> zonesElementaires) {this.zonesElementaires = zonesElementaires;}

	@Override
	public void prendreAttributsRepresentation(ElementRepresentation representation) {
		super.prendreAttributsRepresentation(representation);
		if (representation ==null) return;
		UniteUrbaine uniteUrbaine = (UniteUrbaine)representation;
		densiteInitiale=uniteUrbaine.getDensite();
		Set<AgentBatiment> batiments = new HashSet<AgentBatiment>();
		for (Batiment batiment:uniteUrbaine.getBatiments()) {
			batiments.add((AgentBatiment) batiment.getAgentGeographique());
		}
		//this.setBatiments(batiments);
		Set<AgentZoneElementaireBatie> newZonesElementaires = new HashSet<AgentZoneElementaireBatie>();
		for (ZoneElementaireUrbaine zone:uniteUrbaine.getZonesElementaires()) {
			newZonesElementaires.add((AgentZoneElementaireBatie) zone.getAgentGeographique());
		}
		this.setZonesElementaires(newZonesElementaires);
	}

	@Override
	public void setAttributsDateSimulee() {
		super.setAttributsDateSimulee();
		//FIXME voir densité but
		densiteBut=getDensite()*2;
		if (logger.isDebugEnabled()) logger.debug("densité But = "+densiteBut);
	}

	/**
	 * @return
	 */
	public double getDensite() {return calculDensite();}
	/**
	 * @return
	 */
	private double calculDensite() {
		double surfaceBatiments = 0.0;
		if (logger.isDebugEnabled()) logger.debug("Nb Batiments = "+this.getBatiments().size());
		for (AgentBatiment batiment:this.getBatiments()) surfaceBatiments+=batiment.getGeom().area();
		double resultat = surfaceBatiments/this.getGeom().area();
		if (logger.isDebugEnabled()) logger.debug("densité = "+resultat);
		return resultat;
	}
	double densiteInitiale = 0.0;
	/**
	 * @return
	 */
	public double getDensiteInitiale() {return densiteInitiale;}
	double densiteBut = 0.0;
	/**
	 * @return
	 */
	public double getDensiteBut() {return densiteBut;}

	@Override
	public ElementRepresentation construireRepresentationCourante() {
		UniteUrbaine unite = (UniteUrbaine) super.construireRepresentationCourante();
		unite.setDateSourceSaisie(this.getDateSimulee());
		for (AgentZoneElementaireBatie agentZone:this.getZonesElementaires()) {
			ZoneElementaireUrbaine zone = (ZoneElementaireUrbaine) agentZone.construireRepresentationCourante();
			unite.addZoneElementaire(zone);
			zone.setUniteUrbaine(unite);
		}
		return unite;
	}
	@Override
	public List<AgentGeographique> getComposants() {
	    List<AgentGeographique> composants = new ArrayList<AgentGeographique> ();
	    composants.addAll(this.getZonesElementaires());
	    return composants;
	}

	@Override
	public void removeComposant(AgentGeographique composantASupprimer) {
		this.getZonesElementaires().remove(composantASupprimer);
	}
}
