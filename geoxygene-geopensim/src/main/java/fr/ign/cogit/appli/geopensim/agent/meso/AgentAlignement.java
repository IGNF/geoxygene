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
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.appli.geopensim.feature.meso.Alignement;
import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;

/**
 * @author Florence Curie
 *
 */
public class AgentAlignement extends AgentMeso {
	//static Logger logger=Logger.getLogger(AgentAlignement.class.getName());
	/**
	 * 
	 */
	public AgentAlignement() {
		super();
		this.representationClass=Alignement.class;
		this.representationClassString=representationClass.getName();
	}

	/**
	 * 
	 */
	public AgentAlignement(int idGeo) {super(idGeo,Alignement.class);}

	private Set<AgentBatiment> batiments;
	/**
	 * @return la valeur de l'attribut batiments
	 */
	@Transient
	public Set<AgentBatiment> getBatiments() {return this.batiments;}
	/**
	 * @param batiments l'attribut batiments à affecter
	 */
	public void setBatiments(Set<AgentBatiment> batiments) {this.batiments = batiments;}

	@Override
	public void prendreAttributsRepresentation(ElementRepresentation representation) {
		super.prendreAttributsRepresentation(representation);
		Alignement alignement = (Alignement)representation;
		this.batiments = new HashSet<AgentBatiment>();
		for (Batiment batiment:alignement.getBatiments()) {
			this.batiments.add((AgentBatiment) batiment.getAgentGeographique());
		}
	}
	
	@Override
	public ElementRepresentation construireRepresentationCourante() {
		Alignement alignement = (Alignement) super.construireRepresentationCourante();
		alignement.setDateSourceSaisie(this.getDateSimulee());
		for (AgentBatiment agentBatiment:this.getBatiments()) {
			Batiment batiment = (Batiment) agentBatiment.construireRepresentationCourante();
			alignement.addBatiment(batiment);
			//batiment.setAlignement(alignement);
		}
		return alignement;
	}
	
	@Override
	public List<AgentGeographique> getComposants() {
	    List<AgentGeographique> composants = new ArrayList<AgentGeographique> ();
	    composants.addAll(this.getBatiments());
	    return composants;
	}

	@Override
	public void removeComposant(AgentGeographique composantASupprimer) {
		this.getBatiments().remove(composantASupprimer);
	}
}
