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

package fr.ign.cogit.appli.geopensim.comportement;

import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.Agent;
import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentGroupeBatiments;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentMeso;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;

/**
 * @author Julien Perret
 *
 */
public class ComportementSuppressionComposantv2 extends Comportement {
	private static Logger logger=Logger.getLogger(ComportementSuppressionComposantv2.class.getName());

	@Override
	public void declencher(Agent agent) {
		super.declencher(agent);
		if (logger.isDebugEnabled()) logger.debug("suppression du composant le moins satisfait de "+agent);

		if (agent instanceof AgentMeso) {
			AgentMeso agentMeso = (AgentMeso) agent;
			//FIXME choisir les agents dans un ordre judicieux : ajouter Méthode choisirMeilleurAActiver à la classe AgentMeso
			//double satisfactionMin = Double.MAX_VALUE;
			for(AgentGeographique composant:agentMeso.getComposants()) {
				if (composant instanceof AgentGroupeBatiments){
					double satisfactionMin = Double.MAX_VALUE;
					AgentBatiment batimentASupprimer = null;
					AgentGroupeBatiments groupeBatiments = (AgentGroupeBatiments)composant;
					Set<AgentBatiment> batiments  = groupeBatiments.getBatiments();
					for (AgentBatiment batiment : batiments){
						if (((batiment.getSurfaceBatimentsIntersectes()>0)||(batiment.getSurfaceDepassement()>0))&&(batiment.isSimulated()==true)){
							batiment.calculerSatisfaction();
							double satisfaction = batiment.getSatisfaction();
							if (satisfaction < satisfactionMin) {
								satisfactionMin = satisfaction;
								batimentASupprimer = batiment;
							}
						}
					}
					
					if (batimentASupprimer!=null) {
						logger.debug("on supprime le batiment : "+batimentASupprimer +"de satisfaction S = " +satisfactionMin);
						batimentASupprimer.remove();
						groupeBatiments.removeComposant(batimentASupprimer);
						// On met à jour la densité de la zone élémentaire
						groupeBatiments.getZoneElementaireBatie().miseAjourDensite();
						logger.info("densiteeee : "+groupeBatiments.getZoneElementaireBatie().getDensite());
						// on force le calcul de la densite
						groupeBatiments.getZoneElementaireBatie().setDensite(-1);
						logger.info("densiteeee : "+groupeBatiments.getZoneElementaireBatie().getDensite());
					}
				}
			}
		} else {
			logger.error("Agent de type "+agent.getClass()+" au lieu de AgentMeso");
		}
		if (logger.isDebugEnabled()) logger.debug("Fin de la suppression du composant le moins satisfait de "+agent);
	}
}
