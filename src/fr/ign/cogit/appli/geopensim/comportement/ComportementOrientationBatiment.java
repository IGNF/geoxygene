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

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.ParametresMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.agent.Agent;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTroncon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.JtsUtil;
import fr.ign.cogit.geoxygene.util.algo.MesureOrientationFeuille;
import fr.ign.cogit.geoxygene.util.algo.MesureOrientationV2;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

/**
 * @author Florence Curie
 *
 */
public class ComportementOrientationBatiment extends Comportement {
	private static Logger logger=Logger.getLogger(ComportementOrientationBatiment.class.getName());
	static double distanceMinimum = 10;

	@Override
	public void declencher(Agent agent) {
		super.declencher(agent);
		if (agent instanceof AgentBatiment) {
			if (logger.isDebugEnabled()) logger.debug("ComportementOrientationBatiment");
			AgentBatiment agentBatiment = (AgentBatiment) agent;
			AgentZoneElementaireBatie agentZoneElementaireBatie = agentBatiment.getGroupeBatiments().getZoneElementaireBatie();
			
			// On récupère la Méthode de peuplement à appliquer et ses paramètres
			String methodePeuplement = agentZoneElementaireBatie.getMethodePeuplement();
			ParametresMethodesPeuplement parametresPeuplement = ConfigurationMethodesPeuplement.getInstance().getParametresMethodesPeuplement(methodePeuplement);
			// On se réaligne sur le route la plus proche 
			if (parametresPeuplement.getParalleleRoute()){
				
				// on cherche l'orientation du troncon le plus proche du centroid du batiment
				AgentTroncon tronconLePlusProche = agentBatiment.getTronconLePlusProche();
				GM_LineString geometrieTroncon = (GM_LineString)tronconLePlusProche.getGeom();
				double orientationTroncon = JtsUtil.projectionPointOrientationTroncon(agentBatiment.getGeom().centroid(), geometrieTroncon);
		
				// On positionne le bâtiment par rapport à la route la plus proche
				//pour qu'il soit parallèle à la route la plus proche
				Polygon polygon = null;
				IGeometry result = null;
				try {
					polygon = (Polygon)AdapterFactory.toGeometry(new GeometryFactory(), agentBatiment.getGeom());
					if (polygon!=null) {
						// Détermination de l'angle de rotation du bâtiment
						double orientationBatiment = MesureOrientationV2.getOrientationGenerale(polygon);
						// Si la valeur est égale à 999.9 ça peut vouloir dire que le batiment est carré 
						if (orientationBatiment==999.9){
							// dans ce cas on utilise les murs du batiment
							MesureOrientationFeuille mesureOrientation = new MesureOrientationFeuille(polygon,Math.PI * 0.5);
							double orientationCotes = mesureOrientation.getOrientationPrincipale();
							if (orientationCotes!=-999.9){
								orientationBatiment = orientationCotes;
							}
						}
						// On Détermine l'angle de rotation
						double angleRotation = orientationTroncon - orientationBatiment;
						if (logger.isDebugEnabled()) logger.debug("Orientation Batiment = " + orientationBatiment+ " angle de rotation = "+angleRotation);
						// Rotation du bâtiment
						Polygon nouvelleGeometrie = JtsUtil.rotation(polygon, (angleRotation));
						result= JtsGeOxygene.makeGeOxygeneGeom(nouvelleGeometrie);
						if (logger.isDebugEnabled()) logger.debug("Orientation Route = " + orientationTroncon);
					}
				}
				catch (Exception e) {
					logger.error("Erreur sur le bâtiment : "+agentBatiment.getGeom());
					logger.error(e.getCause());
					return;
				}
				if (result!=null){
					agentBatiment.setGeom(result);
					if (logger.isDebugEnabled()) logger.debug("Batiment après réorientation = "+agentBatiment.getGeom());
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
