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
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.algo.GenerateurValeur;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

/**
 * @author Julien Perret et Florence Curie
 *
 */
public class ComportementDeplacementBatimentCauseRoute extends Comportement {
	private static Logger logger=Logger.getLogger(ComportementDeplacementBatimentCauseRoute.class.getName());
	static double distanceMinimum = -1.0;//10;

	@SuppressWarnings("unchecked")
	@Override
	public void declencher(Agent agent) {
		super.declencher(agent);
		if (agent instanceof AgentBatiment) {
			if (logger.isDebugEnabled()) logger.debug("ComportementDeplacementBatimentCauseRoute");
			AgentBatiment agentBatiment = (AgentBatiment) agent;
			AgentZoneElementaireBatie agentZoneElementaireBatie = agentBatiment.getGroupeBatiments().getZoneElementaireBatie();
			
			// On récupère la Méthode de peuplement à appliquer et ses paramètres
			String methodePeuplement = agentZoneElementaireBatie.getMethodePeuplement();
			ParametresMethodesPeuplement parametresPeuplement = ConfigurationMethodesPeuplement.getInstance().getParametresMethodesPeuplement(methodePeuplement);
			
			Distribution distributiondistanceR = parametresPeuplement.getDistanceRoute();
			distanceMinimum = GenerateurValeur.genererValeur(distributiondistanceR);
//			if (parametresPeuplement.getDistanceRoute().getMoyenne()!=-1){
//				distanceMinimum = parametresPeuplement.getDistanceRoute().getMoyenne();
//			}
			
			if ((agentBatiment.isSimulated())&&(distanceMinimum!=-1)) {
				if (logger.isDebugEnabled()) logger.debug("Batiment initial = "+agentBatiment.getGeom());
				// pas de représentation, on peut déplacer
				agentBatiment.calculTronconLePlusProche();
				
				double tx=0;
				double ty=0;
				if (agentBatiment.getTronconLePlusProche()!=null) {
					if (logger.isDebugEnabled()) logger.debug("DistanceTronconLePlusProche="+agentBatiment.getDistanceTronconLePlusProche());
					// FIXME voir distanceMinimum ???
					if (agentBatiment.getDistanceTronconLePlusProche()>0) {
						if (agentBatiment.getDistanceTronconLePlusProche()>distanceMinimum) {
							logger.debug("le bâtiment est trop loin du troncon le plus proche");
						} else{
							logger.debug("le bâtiment est trop près du troncon le plus proche");
						}
						IDirectPositionList listepoints = JtsAlgorithms.getClosestPoints(agentBatiment.getTronconLePlusProche().getGeom(), agentBatiment.getGeom());
						IDirectPosition pointLePlusProcheRoute = listepoints.get(0);
						IDirectPosition pointLePlusProcheBatiment = listepoints.get(1);
						tx = (pointLePlusProcheRoute.getX()-pointLePlusProcheBatiment.getX());
						ty = (pointLePlusProcheRoute.getY()-pointLePlusProcheBatiment.getY());
						if (logger.isDebugEnabled()) {
							logger.debug("vecteur = "+new GM_LineString(new DirectPositionList(Arrays.asList(pointLePlusProcheRoute,pointLePlusProcheBatiment))));
						}
						if ((tx!=0)||(ty!=0)) {
							double longueur = Math.sqrt(tx*tx+ty*ty);
							// FIXME voir la distance...
							tx*=(longueur-distanceMinimum)/longueur;
							ty*=(longueur-distanceMinimum)/longueur;
						}
					} else {// le batiment intersecte ou touche une route
						logger.debug("le bâtiment intersecte le troncon");
						IGeometry difference = agentBatiment.getGeom().difference(agentZoneElementaireBatie.getGeom());
						if (!difference.isEmpty()){
							logger.debug("troncon extérieur");
							logger.debug("la différence = "+difference);
						}else{
							logger.debug("troncon interieur");
							// Calcul de l'enveloppe convexe du batiment
							IGeometry enveloppeConvexe = agentBatiment.getGeom().convexHull();
							// Intersection avec le troncon le plus proche
							IGeometry troncon = agentBatiment.getTronconLePlusProche().getGeom();
							IGeometry intersection = enveloppeConvexe.intersection(troncon);
							// Construction de 2 boites
							double dimBoite = 100;
							GM_LineString ligne1 = new GM_LineString(intersection.coord());
							GM_LineString ligne2 = new GM_LineString(intersection.coord());
							IDirectPosition pointDebut = intersection.coord().get(0);
							IDirectPosition pointFin = intersection.coord().get(intersection.numPoints()-1);
							// Ajout du premier point
							Vecteur vect1 = new Vecteur(pointDebut,pointFin).vectNorme();
							ligne1.addControlPoint(vect1.multConstante(dimBoite/2).translate(pointFin));
							ligne2.addControlPoint(vect1.multConstante(dimBoite/2).translate(pointFin));
							// Ajout du deuxième point
							Vecteur vect2 = new Vecteur(vect1.getY(),-vect1.getX(),0).vectNorme();
							ligne1.addControlPoint(vect2.multConstante(dimBoite).translate(ligne1.endPoint()));
							ligne2.addControlPoint(vect2.multConstante(-dimBoite).translate(ligne2.endPoint()));
							// Ajout du troisième point
							ligne1.addControlPoint(vect1.multConstante(-dimBoite).translate(ligne1.endPoint()));
							ligne2.addControlPoint(vect1.multConstante(-dimBoite).translate(ligne2.endPoint()));
							// Ajout du quatrième point
							ligne1.addControlPoint(vect2.multConstante(-dimBoite).translate(ligne1.endPoint()));
							ligne2.addControlPoint(vect2.multConstante(dimBoite).translate(ligne2.endPoint()));
							ligne1.addControlPoint(pointDebut);
							ligne2.addControlPoint(pointDebut);
							GM_Polygon boite1 = new GM_Polygon(ligne1);
							GM_Polygon boite2 = new GM_Polygon(ligne2);
							logger.debug(boite1);
							logger.debug(boite2);
							// différence
							IGeometry difference2 = agentBatiment.getGeom().difference(boite1);
							IGeometry difference3 = agentBatiment.getGeom().difference(boite2);
							if (difference2.area()<difference3.area()){
								difference = difference2;
							}else{
								difference = difference3;
							}
							logger.debug("la différence = "+difference);
						}
						
						if ( difference.isPolygon() && (difference instanceof GM_Polygon) ) {
							GM_Polygon polygoneDifference = (GM_Polygon) difference;
							IDirectPosition pointLePlusLoin = JtsAlgorithms.getFurthestPoint((GM_LineString) agentBatiment.getTronconLePlusProche().getGeom(),polygoneDifference.exteriorLineString());
							IDirectPosition pointLePlusProche = JtsAlgorithms.getClosestPoint(pointLePlusLoin,(GM_LineString) agentBatiment.getTronconLePlusProche().getGeom());
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
						else if(difference instanceof GM_MultiSurface){
							double longueurMax = 0.0;
							double txMax = 0;
							double tyMax = 0;
							for (GM_Polygon poly:((GM_MultiSurface<GM_Polygon>) difference).getList()) {
								GM_Polygon polygoneDifference = (GM_Polygon) poly;
								IDirectPosition pointLePlusLoin = JtsAlgorithms.getFurthestPoint((GM_LineString) agentBatiment.getTronconLePlusProche().getGeom(),polygoneDifference.exteriorLineString());
								IDirectPosition pointLePlusProche = JtsAlgorithms.getClosestPoint(pointLePlusLoin,(GM_LineString) agentBatiment.getTronconLePlusProche().getGeom());
								tx=(pointLePlusProche.getX()-pointLePlusLoin.getX());
								ty=(pointLePlusProche.getY()-pointLePlusLoin.getY());
								if (logger.isDebugEnabled()) {
									logger.debug("polygoneIntersection = "+polygoneDifference);
									logger.debug("vecteur = "+new GM_LineString(new DirectPositionList(Arrays.asList(pointLePlusProche,pointLePlusLoin))));
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
					}
				} else {if (logger.isDebugEnabled()) logger.debug("Pas de TronconLePlusProche");}

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

				// distance batiment le plus proche ?
			}
		} else {
			logger.error("Agent de type "+agent.getClass()+" au lieu de AgentBatiment");
		}
	}
}
