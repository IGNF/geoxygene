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

import fr.ign.cogit.appli.geopensim.agent.meso.AgentAlignement;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentGroupeBatiments;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentUniteBatie;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneAgregee;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentEspaceVide;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTronconChemin;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTronconCoursEau;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTronconRoute;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTronconVoieFerree;
import fr.ign.cogit.appli.geopensim.feature.meso.Alignement;
import fr.ign.cogit.appli.geopensim.feature.meso.GroupeBatiments;
import fr.ign.cogit.appli.geopensim.feature.meso.GroupeRoutesDansIlot;
import fr.ign.cogit.appli.geopensim.feature.meso.UniteUrbaine;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneAgregeeUrbaine;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.appli.geopensim.feature.micro.Carrefour;
import fr.ign.cogit.appli.geopensim.feature.micro.Cimetiere;
import fr.ign.cogit.appli.geopensim.feature.micro.EspaceVide;
import fr.ign.cogit.appli.geopensim.feature.micro.InfrastructureCommunication;
import fr.ign.cogit.appli.geopensim.feature.micro.Parking;
import fr.ign.cogit.appli.geopensim.feature.micro.SurfaceEau;
import fr.ign.cogit.appli.geopensim.feature.micro.TerrainSport;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconChemin;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconCoursEau;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconRoute;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconVoieFerree;
import fr.ign.cogit.appli.geopensim.feature.micro.Vegetation;

/**
 * @author Julien Perret
 *
 */
public class AgentFactory {
	
	public static int idGeo = 1;

	/**
	 * Création d'une nouvelle instance d'objet Géographique.
	 * @param representationClass classe des représentation de l'agent
	 * @return Nouvelle instance d'objet Géographique
	 */
	public static AgentGeographique newAgentGeographique(Class<?> representationClass) {
		return newAgentGeographique(idGeo++,representationClass);
	}
	/**
	 * Création d'une nouvelle instance d'objet Géographique.
	 * @param id identifiant de l'objet à créer
	 * @param representationClass classe des représentation de l'agent
	 * @return Nouvelle instance d'objet Géographique
	 */
	private static AgentGeographique newAgentGeographique(int id, Class<?> representationClass) {
		// instancier les contraintes et les comportements correspondant au type d'agent créé
		// agents méso
		if (representationClass.isAssignableFrom(UniteUrbaine.class)) {
			// l'agent est une Unité urbaine
			return new AgentUniteBatie(id);
		}
		if (representationClass.isAssignableFrom(ZoneAgregeeUrbaine.class)) {
			// l'agent est une zone agrégée urbaine
		    // FIXME ceci doit etre une classe abstraite mais il faut ajouter les sous classes
			return new AgentZoneAgregee(id);			
		}
		if (representationClass.isAssignableFrom(ZoneElementaireUrbaine.class)) {
			// l'agent est une zone élémentaire urbaine
			// TODO c'est là qu'il faut instancier les contraintes ??????
			//agent.addContrainte(new Densite(agent,10,10));
			return new AgentZoneElementaireBatie(id);
		}
		if (representationClass.isAssignableFrom(GroupeRoutesDansIlot.class)) {
			// l'agent est un groupe de routes dans un ilot
			return new AgentGeographique(id, representationClass);			
		}
		if (representationClass.isAssignableFrom(GroupeBatiments.class)) {
			// l'agent est un groupe de batiments dans un ilot
			return new AgentGroupeBatiments(id);
		}
		if (representationClass.isAssignableFrom(Alignement.class)) {
			// l'agent est un alignement dans un groupe de bâtiments
			return new AgentAlignement(id);
		}
		// agents micro
		if (representationClass.isAssignableFrom(Batiment.class)) {
			// l'agent est un Batiment
			// TODO c'est là qu'il faut instancier les contraintes ??????
			//agent.addContrainte(new Proximite(agent,10,10));
			return new AgentBatiment(id);			
		}
		if (representationClass.isAssignableFrom(Carrefour.class)) {
			// l'agent est un Carrefour
			return new AgentGeographique(id, representationClass);
		}
		if (representationClass.isAssignableFrom(Cimetiere.class)) {
			// l'agent est un Cimetiere
			return new AgentGeographique(id, representationClass);
		}
		if (representationClass.isAssignableFrom(EspaceVide.class)) {
			// l'agent est un EspaceVide
			return new AgentEspaceVide(id);
		}
		if (representationClass.isAssignableFrom(InfrastructureCommunication.class)) {
			// l'agent est une Infrastructure de Communication
			return new AgentGeographique(id, representationClass);
		}
		if (representationClass.isAssignableFrom(Parking.class)) {
			// l'agent est un Parking
			return new AgentGeographique(id, representationClass);
		}
		if (representationClass.isAssignableFrom(SurfaceEau.class)) {
			// l'agent est une Surface d'Eau
			return new AgentGeographique(id, representationClass);
		}
		if (representationClass.isAssignableFrom(TerrainSport.class)) {
			// l'agent est un Terrain de Sport
			return new AgentGeographique(id, representationClass);
		}
		if (representationClass.isAssignableFrom(TronconChemin.class)) {
			// l'agent est un Troncon
			return new AgentTronconChemin(id);
		}
		if (representationClass.isAssignableFrom(TronconCoursEau.class)) {
			// l'agent est un Troncon
			return new AgentTronconCoursEau(id);
		}
		if (representationClass.isAssignableFrom(TronconRoute.class)) {
			// l'agent est un Troncon
			return new AgentTronconRoute(id);
		}
		if (representationClass.isAssignableFrom(TronconVoieFerree.class)) {
			// l'agent est un Troncon
			return new AgentTronconVoieFerree(id);
		}
		if (representationClass.isAssignableFrom(Vegetation.class)) {
			// l'agent est une Vegetation
			return new AgentGeographique(id, representationClass);
		}
		return new AgentGeographique(id, representationClass);
	}

}
