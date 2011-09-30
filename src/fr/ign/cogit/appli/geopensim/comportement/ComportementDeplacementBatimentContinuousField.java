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

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.ParametresMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.agent.Agent;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTroncon;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.delaunay.NoeudDelaunay;

/**
 * Building displacement inspired by "A method for building displacement in
 * automated map generalisation", Anne Ruas, IJGIS, 1998, vol. 12, no. 8,
 * 789-803.
 * @author Julien Perret
 *
 */
public class ComportementDeplacementBatimentContinuousField
extends Comportement {
	private static Logger logger = Logger.getLogger(
	        ComportementDeplacementBatimentContinuousField.class.getName());

	@Override
	public void declencher(Agent agent) {
		super.declencher(agent);
		if (agent instanceof AgentZoneElementaireBatie) {
			if (logger.isDebugEnabled()) {
			    logger.debug("ComportementDeplacementBatimentContinuousField");
			}
			AgentZoneElementaireBatie agentZoneElementaireBatie
			= (AgentZoneElementaireBatie) agent;
			// computation of proximity relationships
			agentZoneElementaireBatie.buildBuildingTriangulation();
			// computation of initial displacement vectors
			Map<AgentBatiment, NoeudDelaunay> displaceableNodes
			= agentZoneElementaireBatie.getDisplaceableNodes();
			if (displaceableNodes.isEmpty()) {
			    logger.info("No building to displace");
			    return;
			}
			ParametresMethodesPeuplement paramPeuplement = agentZoneElementaireBatie.getParametresMethodesPeuplement();
			double distanceRoads = paramPeuplement.getDistanceRoute().getMoyenne();
			double distanceBuildings = paramPeuplement.getDistanceBatiment().getMoyenne();
			double satisfactionProximity = agentZoneElementaireBatie.getSatisfactionProximity();
			int numberOfTries = 0;
			double alpha = 0.2;
			while (true) {
			    for (AgentBatiment batiment : displaceableNodes.keySet()) {
			        NoeudDelaunay node = displaceableNodes.get(batiment);
			        IDirectPosition p1 = node.getGeometrie().getPosition();
			        List<Arc> edges = node.arcs();
		            double aggrDx = 0;
		            double aggrDy = 0;
                    boolean translate = false;
			        for (Arc edge : edges) {
			            NoeudDelaunay otherNode
			            = (NoeudDelaunay) edge.getNoeudIni();
			            IDirectPosition p2 = otherNode.getGeometrie().getPosition();
			            if (otherNode.equals(node)) {
			                otherNode = (NoeudDelaunay) edge.getNoeudFin();
			                p2 = otherNode.getGeometrie().getPosition();
			            }
			            IFeature feature = otherNode.getCorrespondant(0);
			            double distance = edge.getPoids();
			            double dx = (p1.getX() - p2.getX()) / edge.longueur();
			            double dy = (p1.getY() - p2.getY()) / edge.longueur();
			            if (feature instanceof AgentBatiment) {
                            // TODO DistanceMax à 30... ajouter une variable
			                if (distance < distanceBuildings || distance < 30) {
			                    aggrDx += dx * alpha * (distanceBuildings - distance);
			                    aggrDy += dy * alpha * (distanceBuildings - distance);
			                    translate = true;
			                }
			            } else {
			                if (feature instanceof AgentTroncon) {
                                // TODO DistanceMax à 30... ajouter une variable
			                    if (distance < distanceRoads || distance < 30) {
			                        aggrDx += dx * alpha * (distanceRoads - distance);
			                        aggrDy += dy * alpha * (distanceRoads - distance);
			                        translate = true;
	                            }
			                }
			            }
			        }
                    if (translate) {
                        IGeometry translated = batiment.getGeom().translate(aggrDx, aggrDy, 0);
                        batiment.setGeom(translated);
                        p1 = translated.centroid();
                        agentZoneElementaireBatie.updateProximity();
                    }
			        // On met à jour les groupes de bâtiments
			        batiment.miseAJourGB();
			        // on met à jour les distances aux autres éléments
			        batiment.calculRouteLaPlusProche();
			        batiment.calculTronconLePlusProche();
			        batiment.calculGroupeBatimentsLePlusProche();
			        batiment.calculBatimentLePlusProche();
			    }
			    logger.debug("numberOfTries = " + numberOfTries);
			    numberOfTries++;
			    double newSatisfactionProximity = agentZoneElementaireBatie.getSatisfactionProximity();
			    if (newSatisfactionProximity >= satisfactionProximity || numberOfTries > 20) {
			        /*
	                for (AgentBatiment batiment : displaceableNodes.keySet()) {
	                    NoeudDelaunay node = displaceableNodes.get(batiment);
	                    GM_Object neighborGeometry = null;
	                    List<Arc> edges = node.arcs();
	                    for (Arc edge : edges) {
	                        NoeudDelaunay otherNode
	                        = (NoeudDelaunay) edge.getNoeudIni();
	                        if (otherNode.equals(node)) {
	                            otherNode = (NoeudDelaunay) edge.getNoeudFin();
	                        }
	                        FT_Feature feature = otherNode.getCorrespondant(0);
	                        double distance = edge.getPoids();
	                        boolean subtract = false;
	                        if (feature instanceof AgentBatiment) {
	                            if (distance < distanceMinimumBuildings) {
	                                neighborGeometry = feature.getGeom().buffer(distanceMinimumBuildings);
	                                subtract = true;
	                            }
	                        } else {
	                            if (feature instanceof AgentTroncon) {
	                                if (distance < distanceMinimumRoads) {
	                                    neighborGeometry = feature.getGeom().buffer(distanceMinimumRoads);
	                                    subtract = true;
	                                }
	                            }
	                        }
	                        if (subtract) {
	                            GM_Object difference = batiment.getGeom().difference(neighborGeometry);
	                            batiment.setGeom(difference);
	                            agentZoneElementaireBatie.updateProximity();
	                        }
	                    }
	                    // On met à jour les groupes de bâtiments
	                    batiment.miseAJourGB();
	                    // on met à jour les distances aux autres éléments
	                    batiment.calculRouteLaPlusProche();
	                    batiment.calculTronconLePlusProche();
	                    batiment.calculGroupeBatimentsLePlusProche();
	                    batiment.calculBatimentLePlusProche();
	                }
	                */
			        return;
			    }
			    satisfactionProximity = newSatisfactionProximity;
			}
		} else {
			logger.error("Agent de type "+agent.getClass()+" au lieu de AgentZoneElementaireBatie");
		}
	}
}
