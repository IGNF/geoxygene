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

import java.util.Arrays;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.Agent;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTroncon;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTronconRoute;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

/**
 * @author Julien Perret
 *
 */
public class ComportementDeplacementBatimentAleatoire extends Comportement {
	private static Logger logger=Logger.getLogger(ComportementDeplacementBatimentAleatoire.class.getName());
	static double distanceMinimum = 10;

	@Override
	public void declencher(Agent agent) {
		super.declencher(agent);
		if (agent instanceof AgentBatiment) {
			if (logger.isDebugEnabled()) logger.debug("ComportementDeplacementBatiment");
			AgentBatiment agentBatiment = (AgentBatiment) agent;
			AgentZoneElementaireBatie agentZoneElementaireBatie = agentBatiment.getGroupeBatimentsLePlusProche().getZoneElementaireBatie();
			if (agentBatiment.isSimulated()) {
				if (logger.isDebugEnabled()) logger.debug("Batiment initial = "+agentBatiment.getGeom());
				// pas de représentation, on peut déplacer
				double distanceMin = Double.MAX_VALUE;
				for (AgentTroncon troncon:agentZoneElementaireBatie.getTroncons()) {
					if (AgentTronconRoute.class.isAssignableFrom(troncon.getClass())) {
						double distance = agentBatiment.getGeom().distance(troncon.getGeom());
						if (distance<distanceMin) {
							distanceMin=distance;
							agentBatiment.setRouteLaPlusProche((AgentTronconRoute)troncon);
							agentBatiment.setDistanceRouteLaPlusProche(distanceMin);
						}
					}
				}
				double tx=0;
				double ty=0;
				IDirectPosition centroid = agentBatiment.getGeom().centroid();
				if (agentBatiment.getRouteLaPlusProche()!=null) {
					if (logger.isDebugEnabled()) logger.debug("DistanceRouteLaPlusProche="+agentBatiment.getDistanceRouteLaPlusProche());
					// FIXME voir distanceMinimum ???
					if (agentBatiment.getDistanceRouteLaPlusProche()>distanceMinimum) {
						IDirectPosition pointLePlusProche = JtsAlgorithms.getClosestPoint(centroid,(GM_LineString) agentBatiment.getRouteLaPlusProche().getGeom());
						IDirectPosition pointLePlusProcheBatiment = JtsAlgorithms.getClosestPoint(pointLePlusProche,((GM_Polygon)agentBatiment.getGeom()).exteriorLineString());
						tx = (pointLePlusProche.getX()-pointLePlusProcheBatiment.getX());
						ty = (pointLePlusProche.getY()-pointLePlusProcheBatiment.getY());
						if ((tx!=0)||(ty!=0)) {
							double longueur = Math.sqrt(tx*tx+ty*ty);
							// FIXME voir la distance...
							tx*=(longueur-distanceMinimum)/longueur;
							ty*=(longueur-distanceMinimum)/longueur;
						}
					} else if (agentBatiment.getDistanceRouteLaPlusProche()>0) {
						IDirectPosition pointLePlusProche = JtsAlgorithms.getClosestPoint(centroid,(GM_LineString) agentBatiment.getRouteLaPlusProche().getGeom());
						IDirectPosition pointLePlusProcheBatiment = JtsAlgorithms.getClosestPoint(pointLePlusProche,((GM_Polygon)agentBatiment.getGeom()).exteriorLineString());
						tx = (pointLePlusProche.getX()-pointLePlusProcheBatiment.getX());
						ty = (pointLePlusProche.getY()-pointLePlusProcheBatiment.getY());
						if (logger.isDebugEnabled()) {
							logger.debug("vecteur = "+new GM_LineString(new DirectPositionList(Arrays.asList(pointLePlusProche,pointLePlusProcheBatiment))));
						}
						if ((tx!=0)||(ty!=0)) {
							double longueur = Math.sqrt(tx*tx+ty*ty);
							// FIXME voir la distance...
							tx*=(distanceMinimum-longueur)/longueur;
							ty*=(distanceMinimum-longueur)/longueur;
						}
					} else {// le batiment intersecte ou touche une route
						/*
						GM_Object difference = agentBatiment.getGeom().difference(agentBatiment.getZoneElementaireBatie().getGeom());
						if ( difference.isPolygon() && (difference instanceof GM_Polygon) ) {
							GM_Polygon polygoneDifference = (GM_Polygon) difference;
							DirectPosition pointLePlusLoin = new JtsAlgorithms().getPointLePlusLoin((GM_LineString) agentBatiment.getRouteLaPlusProche().getGeom(),polygoneDifference.exteriorLineString());
							DirectPosition pointLePlusProche = new JtsAlgorithms().getPointLePlusProche(pointLePlusLoin,(GM_LineString) agentBatiment.getRouteLaPlusProche().getGeom());
							tx=(pointLePlusProche.getX()-pointLePlusLoin.getX());
							ty=(pointLePlusProche.getY()-pointLePlusLoin.getY());
							if (logger.isDebugEnabled()) {
								logger.debug("polygoneIntersection = "+polygoneDifference);
								logger.debug("vecteur = "+new GM_LineString(new DirectPositionList(Arrays.asList(pointLePlusProche,pointLePlusLoin))));
							}
							if ((tx!=0)||(ty!=0)) {
								double longueur = Math.sqrt(tx*tx+ty*ty);
								// FIXME voir la distance...
								tx*=(longueur+distanceMinimum)/longueur;
								ty*=(longueur+distanceMinimum)/longueur;
							}
						}
						*/
					}
				} else {if (logger.isDebugEnabled()) logger.debug("Pas de RouteLaPlusProche");}
				IDirectPosition vecteur = getVecteurIntersecte(agentBatiment.getGeom(),(GM_Polygon) agentZoneElementaireBatie.getGeom());
				tx+=vecteur.getX();
				ty+=vecteur.getY();
				int nbBatimentIntersectes = 0;
				for(AgentBatiment agentBatimentZone:agentZoneElementaireBatie.getBatiments()) {
					if ( (!agentBatimentZone.equals(agentBatiment)) && (agentBatimentZone.getGeom().intersects(agentBatiment.getGeom())) ) {
						nbBatimentIntersectes++;
						IGeometry intersection = agentBatimentZone.getGeom().intersection(agentBatiment.getGeom());
						double decalage = Math.sqrt(intersection.area());
						IDirectPosition centroidBatimentZone = agentBatimentZone.getGeom().centroid();
						double dx = centroidBatimentZone.getX()-centroid.getX();
						double dy = centroidBatimentZone.getY()-centroid.getY();
						double distanceEntreCentroides = Math.sqrt(dx*dx+dy*dy);
						tx+=tx*decalage/distanceEntreCentroides;
						ty+=ty*decalage/distanceEntreCentroides;
					}
				}
				if ( (tx!=0) || (ty!=0) ) {
					if (logger.isDebugEnabled()) {
						logger.debug("translation du batiment = "+tx+"  "+ty);
						logger.debug(nbBatimentIntersectes+" batiments intersectés");
					}
					agentBatiment.setGeom(agentBatiment.getGeom().translate(tx, ty, 0));
					if (logger.isDebugEnabled()) logger.debug("Batiment final = "+agentBatiment.getGeom());
				}
				// on met à jour la route la plus proche
				distanceMin = Double.MAX_VALUE;
				for (AgentTroncon troncon:agentZoneElementaireBatie.getTroncons()) {
					if (AgentTronconRoute.class.isAssignableFrom(troncon.getClass())) {
						double distance = agentBatiment.getGeom().distance(troncon.getGeom());
						if (distance<distanceMin) {
							distanceMin=distance;
							agentBatiment.setRouteLaPlusProche((AgentTronconRoute)troncon);
							agentBatiment.setDistanceRouteLaPlusProche(distance);
						}
					}
				}
				if (logger.isDebugEnabled()) logger.debug("DistanceRouteLaPlusProche après déplacement="+agentBatiment.getDistanceRouteLaPlusProche());
				// distance batiment le plus proche ?
			}
		} else {
			logger.error("Agent de type "+agent.getClass()+" au lieu de AgentBatiment");
		}
	}

	/**
	 * @param geomBatiment
	 * @param geomZone
	 * @return
	 */
	private IDirectPosition getVecteurIntersecte(IGeometry geomBatiment, IPolygon geomZone) {
	    IGeometry difference = geomBatiment.difference(geomZone);
		double tx=0;
		double ty=0;
		if ( difference.isPolygon() && (difference instanceof GM_Polygon) ) {
			GM_Polygon polygoneDifference = (GM_Polygon) difference;
			IDirectPosition pointLePlusLoin = JtsAlgorithms.getFurthestPoint(geomZone.exteriorLineString(),polygoneDifference.exteriorLineString());
			IDirectPosition pointLePlusProche = JtsAlgorithms.getClosestPoint(pointLePlusLoin,geomZone.exteriorLineString());
			tx=(pointLePlusProche.getX()-pointLePlusLoin.getX());
			ty=(pointLePlusProche.getY()-pointLePlusLoin.getY());
			if (logger.isDebugEnabled()) {
				logger.debug("polygoneIntersection = "+polygoneDifference);
				logger.debug("vecteur = "+new GM_LineString(new DirectPositionList(Arrays.asList(pointLePlusProche,pointLePlusLoin))));
			}
			if ((tx!=0)||(ty!=0)) {
				double longueur = Math.sqrt(tx*tx+ty*ty);
				// FIXME voir la distance...
				tx*=(longueur+distanceMinimum)/longueur;
				ty*=(longueur+distanceMinimum)/longueur;
			}
		}
		return new DirectPosition(tx,ty);
	}

}
