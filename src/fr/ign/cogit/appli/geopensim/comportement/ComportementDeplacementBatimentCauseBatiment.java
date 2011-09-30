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

import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.Distribution;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.ParametresMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.agent.Agent;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentGroupeBatiments;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.algo.GenerateurValeur;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

/**
 * @author Julien Perret
 *
 */
public class ComportementDeplacementBatimentCauseBatiment extends Comportement {
	private static Logger logger=Logger.getLogger(ComportementDeplacementBatimentCauseBatiment.class.getName());
	static double distanceMinimum = 1;

	@Override
	public void declencher(Agent agent) {
		super.declencher(agent);
		if (agent instanceof AgentBatiment) {
			if (logger.isDebugEnabled()) logger.debug("ComportementDeplacementBatimentCauseBatiment");
			AgentBatiment agentBatiment = (AgentBatiment) agent;
			AgentZoneElementaireBatie agentZoneElementaireBatie = agentBatiment.getGroupeBatiments().getZoneElementaireBatie();
			
			// On récupère la Méthode de peuplement à appliquer et ses paramètres
			String methodePeuplement = agentZoneElementaireBatie.getMethodePeuplement();
			ParametresMethodesPeuplement parametresPeuplement = ConfigurationMethodesPeuplement.getInstance().getParametresMethodesPeuplement(methodePeuplement);
			
			Distribution distributiondistanceB = parametresPeuplement.getDistanceBatiment();
			distanceMinimum = GenerateurValeur.genererValeur(distributiondistanceB);
			if (distanceMinimum==-1){distanceMinimum = 1;}
			
			if (agentBatiment.isSimulated()) {
				if (logger.isDebugEnabled()) logger.debug("Batiment initial = "+agentBatiment.getGeom());
				// pas de représentation, on peut déplacer
				double tx=0;
				double ty=0;
				IDirectPosition centroid = agentBatiment.getGeom().centroid();
				// test de l'intersection avec la zone élémentaire
				IDirectPosition vecteur = getVecteurIntersecte(agentBatiment.getGeom(),(GM_Polygon) agentZoneElementaireBatie.getGeom());
				tx+=vecteur.getX();
				ty+=vecteur.getY();
				// Test de l'intersection avec des bâtiments
				int nbBatimentIntersectes = 0;
				for (AgentGroupeBatiments agentGroupe:agentZoneElementaireBatie.getGroupesBatiments()){
					for(AgentBatiment agentBatimentZone:agentGroupe.getBatiments()) {
						if ( (!agentBatimentZone.equals(agentBatiment)) && (agentBatimentZone.getGeom().intersects(agentBatiment.getGeom())) ) {
							if (logger.isDebugEnabled()) logger.debug("Batiment zone = "+agentBatimentZone.getGeom());
							IGeometry intersection = agentBatimentZone.getGeom().intersection(agentBatiment.getGeom());
							double decalage = Math.sqrt(intersection.area());
							IDirectPosition centroidBatimentZone = agentBatimentZone.getGeom().centroid();
							double dx = centroid.getX() - centroidBatimentZone.getX();
							double dy = centroid.getY() - centroidBatimentZone.getY();
							double distanceEntreCentroides = Math.sqrt(dx*dx+dy*dy);
							if (distanceEntreCentroides<0.01) continue;
							nbBatimentIntersectes++;
							tx+=dx*decalage/distanceEntreCentroides;
							ty+=dy*decalage/distanceEntreCentroides;
						}
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

	/**
	 * @param geomBatiment
	 * @param geomZone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private IDirectPosition getVecteurIntersecte(IGeometry geomBatiment, IPolygon geomZone) {
	    IGeometry difference = geomBatiment.difference(geomZone);
		logger.debug("affichage : "+difference);
		logger.debug(difference.isPolygon());
		double tx=0;
		double ty=0;
		if ( difference instanceof GM_Polygon ) {
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
				logger.debug("tx = "+tx);
				logger.debug("ty = "+ty);
			}
		}
		else if(difference instanceof GM_MultiSurface){
			double longueurMax = 0.0;
			double txMax = 0;
			double tyMax = 0;
			for (GM_Polygon poly:((GM_MultiSurface<GM_Polygon>) difference).getList()) {
				GM_Polygon polygoneDifference2 = (GM_Polygon) poly;
				IDirectPosition pointLePlusLoin2 = JtsAlgorithms.getFurthestPoint(geomZone.exteriorLineString(),polygoneDifference2.exteriorLineString());
				IDirectPosition pointLePlusProche2 = JtsAlgorithms.getClosestPoint(pointLePlusLoin2,geomZone.exteriorLineString());
				tx=(pointLePlusProche2.getX()-pointLePlusLoin2.getX());
				ty=(pointLePlusProche2.getY()-pointLePlusLoin2.getY());
				if (logger.isDebugEnabled()) {
					logger.debug("polygoneIntersection = "+polygoneDifference2);
					logger.debug("vecteur = "+new GM_LineString(new DirectPositionList(Arrays.asList(pointLePlusProche2,pointLePlusLoin2))));
				}
				if ((tx!=0)||(ty!=0)) {
					double longueur = Math.sqrt(tx*tx+ty*ty);
					if (longueur>longueurMax){
						longueurMax = longueur;
						txMax = tx;
						tyMax = ty;
						logger.debug("longueurMax = "+ longueurMax);
					}
				}
			}
			if (longueurMax>0.0){
				// FIXME voir la distance...
				tx = txMax * (longueurMax+distanceMinimum)/longueurMax;
				ty = tyMax * (longueurMax+distanceMinimum)/longueurMax;
				logger.debug("tx = "+tx);
				logger.debug("ty = "+ty);
			}
		}
		return new DirectPosition(tx,ty);
	}

}
