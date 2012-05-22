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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.Agent;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTroncon;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTronconRoute;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Chargeur;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * @author Julien Perret et Florence Curie
 *
 */
public class ComportementPrologementRoute extends Comportement {
	private static Logger logger=Logger.getLogger(ComportementPrologementRoute.class.getName());
	
	private double angleMax = 90.0;
	private int nbRays = 6;

	@SuppressWarnings("unchecked")
	@Override
	public void declencher(Agent agent) {
		super.declencher(agent);
		if (agent instanceof AgentTronconRoute) {
			if (logger.isDebugEnabled()) logger.debug("Début du prolongement de la route "+agent);
			AgentTronconRoute agentRoute = (AgentTronconRoute) agent;
			if (agentRoute.getZonesElementaires().size()!=1) {
				if (logger.isDebugEnabled()) logger.debug("Fin : ce n'est pas une impasse");
				return;
			}
			AgentZoneElementaireBatie agentZoneElementaireBatie = (AgentZoneElementaireBatie) agentRoute.getZonesElementaires().iterator().next();
			//if (logger.isDebugEnabled()) logger.debug("Distance route = "+agentZoneElementaireBatie.getDistanceALaRoute());
			// c'est une impasse
			CarteTopo carte = new CarteTopo("Carte troncons");
			Chargeur.importClasseGeo(new FT_FeatureCollection<AgentTroncon>(agentZoneElementaireBatie.getTroncons()), carte, true);
			carte.creeNoeudsManquants(1);
			carte.creeTopologieArcsNoeuds(1);
			carte.creeTopologieFaces();
			double angleInRadians = angleMax*Math.PI/(360.0*nbRays);
			Arc arcRoute = null; 
			for(Arc arc:carte.getPopArcs()) {
				if (arc.getCorrespondant(0).equals(agentRoute)) {
					arcRoute = arc;
				}
			}
			if (arcRoute==null) {
				if (logger.isDebugEnabled()) logger.debug("Fin : arc non trouvé ");
				return;
			}
			
			boolean start = false;
			List<DirectPosition> points = new ArrayList<DirectPosition>();
			IDirectPosition initialPosition = null;
			if (arcRoute.isPendant()&&((arcRoute.getNoeudIni().arcs().size()==1)||(arcRoute.getNoeudFin().arcs().size()==1))) {
				start = (arcRoute.getNoeudIni().arcs().size()==1);
				Noeud noeud = start?arcRoute.getNoeudIni():arcRoute.getNoeudFin();
				initialPosition = noeud.getGeometrie().getPosition();
				IDirectPosition previousPosition = (arcRoute.getGeometrie().getControlPoint(0).equals(initialPosition))?
							arcRoute.getGeometrie().getControlPoint(1):arcRoute.getGeometrie().getControlPoint(arcRoute.getGeometrie().sizeControlPoint()-2);
				double dx = initialPosition.getX()-previousPosition.getX();
				double dy = initialPosition.getY()-previousPosition.getY();
				double length = Math.sqrt(dx*dx+dy*dy);
				double theta = Math.atan2(dy, dx);
				dx /= length;
				dy /= length;
				length = 20;
				for(int i = -nbRays ; i < nbRays+1 ; i++) {
					DirectPosition finalPosition = new DirectPosition(initialPosition.getX()+length*Math.cos(theta+i*angleInRadians),initialPosition.getY()+length*Math.sin(theta+i*angleInRadians));
					points.add(finalPosition);
				}
			}
			if (initialPosition==null) {
				if (logger.isDebugEnabled()) logger.debug("Fin : l'arc n'est pas une impasse ");
				return;
			}

			List<DirectPosition> intersectedPoints = new ArrayList<DirectPosition>();
			for(DirectPosition point:points) {
				for(AgentBatiment batiment:agentZoneElementaireBatie.getBatiments()) {
					GM_LineString line = new GM_LineString(new DirectPositionList(Arrays.asList(initialPosition,point)));
					if (line.distance(batiment.getGeom())<10) {
						intersectedPoints.add(point);
						break;
					}
				}
			}
			points.removeAll(intersectedPoints);
			
			if (logger.isDebugEnabled()) logger.debug(points.size()+" points proposed");
			double distanceMin = Double.MAX_VALUE;
			
			if (logger.isDebugEnabled()) logger.debug("agent zone = "+agentZoneElementaireBatie);
			//if (logger.isDebugEnabled()) logger.debug("agent zone point le plus loin = "+agentZoneElementaireBatie.getPointLePlusLoinDeLaRoute());
			//if (logger.isDebugEnabled()) logger.debug("agent zone position = "+agentZoneElementaireBatie.getPointLePlusLoinDeLaRoute().getPosition());
			
			IGeometry zoneInaccessible = agentZoneElementaireBatie.getZoneInaccessible();
			GM_Polygon closestInaccessibleZone = null;
			if (zoneInaccessible.isPolygon()) closestInaccessibleZone=(GM_Polygon) zoneInaccessible;
			else if (zoneInaccessible.isMultiSurface()) {
				double minDistance = Double.MAX_VALUE;
				for(GM_Polygon polygon:(GM_MultiSurface<GM_Polygon>)zoneInaccessible) {
					double distance = polygon.distance(initialPosition.toGM_Point());
					if (distance<minDistance) {
						minDistance=distance;
						closestInaccessibleZone = polygon;
					}
				}
			}
			if (closestInaccessibleZone==null) {
				logger.error("Aucuine zone inaccessible trouvée");
				return;
			}
			IDirectPosition bestPoint=null;
			boolean intersectsTroncon = false;
			for(DirectPosition point:points) {
				GM_LineString line = new GM_LineString(new DirectPositionList(Arrays.asList(initialPosition,point)));
				if (logger.isDebugEnabled()) logger.debug("line = "+line);				
				for (Arc arc:carte.getPopArcs()) {
					if (!arc.equals(arcRoute)) {
					    IGeometry intersection = line.intersection(arc.getGeom());
						if ((intersection!=null)&&(intersection instanceof GM_Point)) {
							bestPoint=((GM_Point)intersection).getPosition();
							intersectsTroncon = true;
							break;
						}
					}
				}
				if (intersectsTroncon) break;
				
				//double distanceLine = point.distance(agentZoneElementaireBatie.getPointLePlusLoinDeLaRoute().getPosition());
				double distanceLine = closestInaccessibleZone.distance(point.toGM_Point());
				if(distanceLine<distanceMin) {
					distanceMin = distanceLine;
					bestPoint=point;
				}
			}
			if (bestPoint!=null) {
				if (logger.isDebugEnabled()) logger.debug("point = "+bestPoint+ " "+ intersectsTroncon);
				IDirectPositionList pointList = agentRoute.getGeom().coord();
				if (start) pointList.add(0,bestPoint);
				else pointList.add(bestPoint);
				agentRoute.setGeom(new GM_LineString(pointList));
				agentRoute.setSimulated(true);
				agentRoute.setImpasse(!intersectsTroncon);
				agentZoneElementaireBatie.setPointLePlusLoinDeLaRoute(null);
				//if (logger.isDebugEnabled()) logger.debug("Distance route après construction = "+agentZoneElementaireBatie.getDistanceALaRoute());
				// réinitialise le point le plus loin de la route pour recalculer la distance
			}
			
			if (logger.isDebugEnabled()) logger.debug("Fin de la construction d'une route dans la zone élémentaire "+agent);
		} else {
			logger.error("Agent de type "+agent.getClass()+" au lieu de AgentZoneElementaireBatie");
		}
	}
}
