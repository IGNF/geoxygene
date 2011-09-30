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



import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.Distribution;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.ParametresMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.agent.Agent;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.algo.GenerateurValeur;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

/**
 * @author Florence Curie
 *
 */
public class ComportementDeplacementBatimentCauseDistanceBatiment extends Comportement {
	private static Logger logger=Logger.getLogger(ComportementDeplacementBatimentCauseDistanceBatiment.class.getName());
	static double distanceMinimum = -1.0;//10;

	@Override
	public void declencher(Agent agent) {
		super.declencher(agent);
		if (agent instanceof AgentBatiment) {
			if (logger.isDebugEnabled()) logger.debug("ComportementDeplacementBatimentCauseDistanceBatiment");
			AgentBatiment agentBatiment = (AgentBatiment) agent;
			AgentZoneElementaireBatie agentZoneElementaireBatie = agentBatiment.getGroupeBatiments().getZoneElementaireBatie();
			
			// On récupère la Méthode de peuplement à appliquer et ses paramètres
			String methodePeuplement = agentZoneElementaireBatie.getMethodePeuplement();
			ParametresMethodesPeuplement parametresPeuplement = ConfigurationMethodesPeuplement.getInstance().getParametresMethodesPeuplement(methodePeuplement);
			
			Distribution distributiondistanceB = parametresPeuplement.getDistanceBatiment();
			distanceMinimum = GenerateurValeur.genererValeur(distributiondistanceB);
			
//			if (parametresPeuplement.getDistanceBatiment().getMoyenne()!=-1){
//				distanceMinimum = parametresPeuplement.getDistanceBatiment().getMoyenne();
//			}
			
			// On peut utiliser ce comportement seulement si il y a un autre bâtiment dans la zone élémentaire
			if ((agentBatiment.isSimulated())&&(agentZoneElementaireBatie.getBatiments().size()>1)&&(distanceMinimum!=-1)) {
				if (logger.isDebugEnabled()) logger.debug("Batiment initial = "+agentBatiment.getGeom());

				// recherche du bâtiment le plus proche de l'agentBatiment
				agentBatiment.calculBatimentLePlusProche();
				AgentBatiment batimentPP = agentBatiment.getBatimentLePlusProche();
				double distanceMinBatiment = agentBatiment.getDistanceBatimentLePlusProche();
				if (logger.isDebugEnabled()) logger.debug("Batiment le plus proche = "+batimentPP.getGeom());
				if (logger.isDebugEnabled()) logger.debug("Distance au batiment le plus proche = "+distanceMinBatiment);

				double tx=0;
				double ty=0;
				if (batimentPP!=null) {
					if (agentBatiment.getDistanceTronconLePlusProche()>0) {
						if (agentBatiment.getDistanceTronconLePlusProche()>distanceMinimum) {
							logger.debug("le bâtiment est trop loin du bâtiment le plus proche");
						} else{
							logger.debug("le bâtiment est trop près du bâtiment le plus proche");
						}
						IDirectPosition centroid = agentBatiment.getGeom().centroid();
						IDirectPosition centroidBatimentPP = batimentPP.getGeom().centroid();
						double dx = centroidBatimentPP.getX() - centroid.getX();
						double dy = centroidBatimentPP.getY() - centroid.getY();
						double distanceEntreCentroides = Math.sqrt(dx*dx+dy*dy);
						double decalage = distanceMinBatiment-distanceMinimum;
						if (distanceEntreCentroides>0.01) {
							tx+=dx*decalage/distanceEntreCentroides;
							ty+=dy*decalage/distanceEntreCentroides;
						}
					}
				}
				
				if ( (tx!=0) || (ty!=0) ) {
					if (logger.isDebugEnabled()) {
						logger.debug("translation du batiment = "+tx+"  "+ty);
					}
					agentBatiment.setGeom(agentBatiment.getGeom().translate(tx, ty, 0));
					if (logger.isDebugEnabled()) logger.debug("Batiment final = "+agentBatiment.getGeom());
				}
				// On met à jour les groupes de bâtiments
				agentBatiment.miseAJourGB();
				// on met à jour les distances aux autres éléments
				agentBatiment.calculRouteLaPlusProche();
				agentBatiment.calculTronconLePlusProche();
				agentBatiment.calculGroupeBatimentsLePlusProche();
				agentBatiment.calculBatimentLePlusProche();
				if (logger.isDebugEnabled()) {
					logger.debug("DistanceRouteLaPlusProche = "+agentBatiment.getDistanceRouteLaPlusProche());
					logger.debug("DistanceTronconLePlusProche = "+agentBatiment.getDistanceTronconLePlusProche());
					logger.debug("DistanceGroupeBatimentsLePlusProche = "+agentBatiment.getDistanceGroupeBatimentsLePlusProche());
					logger.debug("DistanceBatimentLePlusProche = "+agentBatiment.getDistanceBatimentLePlusProche());
				}
				
				if (logger.isDebugEnabled()) logger.debug("SurfaceBatimentsIntersectes="+agentBatiment.getSurfaceBatimentsIntersectes());
				if (logger.isDebugEnabled()) logger.debug("SurfaceDepassement="+agentBatiment.getSurfaceDepassement());

			}
		} else {
			logger.error("Agent de type "+agent.getClass()+" au lieu de AgentBatiment");
		}
	}

}
