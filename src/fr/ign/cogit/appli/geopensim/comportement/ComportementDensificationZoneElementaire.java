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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.appli.geopensim.ConfigurationFormesBatimentsV2;
import fr.ign.cogit.appli.geopensim.ConfigurationFormesBatimentsV2.ParametresForme;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.ParametresMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationTypesFonctionels;
import fr.ign.cogit.appli.geopensim.ConfigurationTypesFonctionels.Parametres;
import fr.ign.cogit.appli.geopensim.agent.Agent;
import fr.ign.cogit.appli.geopensim.agent.AgentGeographiqueCollection;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentGroupeBatiments;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentUniteBatie;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTroncon;
import fr.ign.cogit.appli.geopensim.algo.GenerateurValeur;
import fr.ign.cogit.appli.geopensim.feature.micro.FormeBatiment;
import fr.ign.cogit.appli.geopensim.feature.micro.TypeFonctionnel;
import fr.ign.cogit.appli.geopensim.geom.ConstructionBatiment;
import fr.ign.cogit.appli.geopensim.scheduler.Scheduler;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Chargeur;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.delaunay.AbstractTriangulation;
import fr.ign.cogit.geoxygene.contrib.delaunay.ChargeurTriangulation;
import fr.ign.cogit.geoxygene.contrib.delaunay.TriangulationJTS;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.JtsUtil;
import fr.ign.cogit.geoxygene.util.algo.MathUtil;
import fr.ign.cogit.geoxygene.util.algo.MesureOrientationFeuille;
import fr.ign.cogit.geoxygene.util.algo.MesureOrientationV2;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

/**
 * @author Julien Perret et Florence Curie
 *
 */
public class ComportementDensificationZoneElementaire extends Comportement {
	private static Logger logger=Logger.getLogger(ComportementDensificationZoneElementaire.class.getName());
	static double distanceMinimum = 1;

	@SuppressWarnings({ "unchecked" })
	@Override
	public void declencher(Agent agent) {
		super.declencher(agent);
		if (agent instanceof AgentZoneElementaireBatie) {
			if (logger.isDebugEnabled()) logger.debug("Densification de la zone élémentaire "+agent);
			AgentZoneElementaireBatie agentZoneElementaireBatie = (AgentZoneElementaireBatie) agent;

			// On récupère la Méthode de peuplement à appliquer et ses paramètres
			String methodePeuplement = agentZoneElementaireBatie.choixMethodePeuplement();
			ParametresMethodesPeuplement parametresPeuplement = ConfigurationMethodesPeuplement.getInstance().getParametresMethodesPeuplement(methodePeuplement);

			// construction des zones constructibles
			IGeometry zoneConstructible = agentZoneElementaireBatie.getZoneConstructible();
			// FIXME stocker et restaurer les espaces vides
			if (zoneConstructible.isEmpty()) {
				if (logger.isDebugEnabled()) logger.debug("Pas de zone constructible");
				// pas d'espace vide, il faut supprimer un/des batiments
			} else {
				if (logger.isDebugEnabled()) logger.debug("zoneConstructible = "+zoneConstructible);

				// On récupère les troncons avec la densité de batiments la plus forte
				List<AgentTroncon> listeTronconsClasses = getTronconADensifier(agentZoneElementaireBatie);

				IGeometry maxIntersection = null;

				int version = 2;

				if (version ==1){
					// Version1 : Recherche de la zone constructible la plus grande
					double maxArea = 0;
					if (zoneConstructible.isMultiSurface()) {
						for(GM_Polygon polygon:((GM_MultiSurface<GM_Polygon>) zoneConstructible)) {
							if (polygon.area()>maxArea) {
								maxArea = polygon.area();
								maxIntersection = polygon;
							}
						}
					} else if (zoneConstructible instanceof GM_Aggregate) {
						GM_Aggregate<GM_Object> aggregate = (GM_Aggregate<GM_Object>) zoneConstructible;
						for(GM_Object geometry:aggregate) {
							if (geometry.isPolygon()) {
								GM_Polygon polygon = (GM_Polygon) geometry;
								if (polygon.area()>maxArea) {
									maxArea = polygon.area();
									maxIntersection = polygon;
								}
							}
						}
					} else if (zoneConstructible.isPolygon()) {
						maxIntersection = zoneConstructible;
					}
				} else if(version ==2){
					//Version 2 : recherche de la zone constructible située à moins de 50 m des troncons les plus peuplés
					if (zoneConstructible instanceof GM_Aggregate) {
						GM_Aggregate<GM_Polygon> aggregate = (GM_Aggregate<GM_Polygon>) zoneConstructible;
						for (AgentTroncon troncon : listeTronconsClasses){
							double distanceMin = Double.MAX_VALUE;
							for(GM_Polygon polygon:aggregate) {
								GM_Polygon zoneConstruct = (GM_Polygon)polygon;
								GM_MultiCurve<IOrientableCurve> ligneExtZoneConstruct = new GM_MultiCurve<IOrientableCurve>();
								ligneExtZoneConstruct.add(zoneConstruct.exteriorLineString());
								logger.debug("intersectionLineString ext : "+zoneConstruct.exteriorLineString());
								double distance = ligneExtZoneConstruct.distance(troncon.getGeom());
								logger.debug("troncon : "+troncon.getGeom());
								logger.debug("dist :"+ distance);
								// FIXME taille mini arbitraire à 200m²
								if (distance < distanceMin){//&&(zoneConstruct.area()>200)) {
									distanceMin = distance;
									maxIntersection = polygon;
								}
							}
							if (distanceMin<50){break;}
						}
					} else {
						maxIntersection = zoneConstructible;
					}
				}
				if (maxIntersection!=null) {
					if (logger.isDebugEnabled()) logger.debug("maxIntersection = "+maxIntersection);
					// on a trouvé le meilleur espace vide
					AbstractTriangulation carte = new TriangulationJTS("triangulation");
					FT_FeatureCollection<DefaultFeature> listeFeatures = new FT_FeatureCollection<DefaultFeature>();
					listeFeatures.add(new DefaultFeature(maxIntersection));
					try {
						ChargeurTriangulation.importPolygoneEnPoints(listeFeatures, carte);
						carte.triangule("czeBQv");
					} catch (Exception e) {
						e.printStackTrace();
					}
					GM_Polygon intersectionPolygon = (GM_Polygon)maxIntersection;
					GM_MultiCurve<ILineString> intersectionLineString = new GM_MultiCurve<ILineString>();
					intersectionLineString.add(intersectionPolygon.exteriorLineString());
					logger.debug("intersectionLineString ext : "+intersectionPolygon.exteriorLineString());
					for(int i = 0 ; i < intersectionPolygon.sizeInterior() ; i++){
						intersectionLineString.add(intersectionPolygon.interiorLineString(i));
						logger.debug("intersectionLineString int : "+intersectionPolygon.interiorLineString(i));
					}

					// Recherche du centre du bâtiment
					IDirectPosition centre = null;
					if (version ==1){
						// Version 1
						double maxAire = 0;
						for(Noeud noeud : carte.getPopVoronoiVertices()) {
							double aire = noeud.getGeom().distance(intersectionLineString);
							if ((aire > maxAire)&&(maxIntersection.contains(noeud.getGeom()))) {
								maxAire=aire;
								centre = noeud.getGeometrie().getPosition();
							}
						}
					}else if (version==2){
						// Version 2
						for (AgentTroncon troncon : listeTronconsClasses){
							double distanceMin = Double.MAX_VALUE;
							for(Noeud noeud : carte.getPopVoronoiVertices()) {
								double distance = noeud.getGeom().distance(troncon.getGeom());
								if ((distance < distanceMin)&&(maxIntersection.contains(noeud.getGeom()))) {
									distanceMin = distance;
									centre = noeud.getGeometrie().getPosition();
								}
							}
							if (distanceMin<50){break;}
						}
					}

					if(centre==null) centre = maxIntersection.centroid();

					if (logger.isDebugEnabled()) {
						//logger.debug("meilleur espace vide = "+max.getGeom());
						logger.debug("meilleure intersection = "+maxIntersection);
						logger.debug("centroid du batiment = "+centre.toGM_Point());
					}

					// on cherche l'orientation du troncon le plus proche du centroid du batiment
					double distanceMini = Double.MAX_VALUE;
					double orientationTroncon = 0;
					//AgentTroncon tronconPlusProche = null;
					for (AgentTroncon troncon:agentZoneElementaireBatie.getTroncons()) {
						double distance = Distances.distance(centre, (GM_LineString)troncon.getGeom());
						if (distance<distanceMini) {
							distanceMini=distance;
							GM_LineString geometrieTroncon = (GM_LineString)troncon.getGeom();
							orientationTroncon = JtsUtil.projectionPointOrientationTroncon(centre, geometrieTroncon);
							//tronconPlusProche = troncon;
						}
					}


					// On crée un nouveau batiment
					AgentBatiment agentBatiment = new AgentBatiment();

					// Choix du type fonctionnel du nouveau bâtiment
					//Si il y a un type fonctionnel de bâtiment pour cette Méthode peuplement
					if (parametresPeuplement.getTypeFonctionnel()!=TypeFonctionnel.Quelconque){
						agentBatiment.setTypeFonctionnel(parametresPeuplement.getTypeFonctionnel());
					}else{
						// On Détermine le type fonctionel du nouveau bâtiment en fonction du type fonctionel objectif
						// On récupère les pourcentages objectifs de chaque type de bâtiments
						int homogeneiteTypeFonctionnelBatimentsBut = agentZoneElementaireBatie.getHomogeneiteTypesFonctionnelsBatimentsBut();
						Parametres parametresType = ConfigurationTypesFonctionels.getInstance().getParametres(homogeneiteTypeFonctionnelBatimentsBut);
						// On calcule les pourcentages de chaque type de bâtiments
						int nbBatimentsHab = 0;
						int nbBatimentsPub = 0;
						int nbBatimentsInd = 0;
						for(AgentBatiment batiment:agentZoneElementaireBatie.getBatiments()) {
							if (batiment.getTypeFonctionnel()==TypeFonctionnel.Habitat) nbBatimentsHab++;
							else if (batiment.getTypeFonctionnel()==TypeFonctionnel.Public) nbBatimentsPub++;
							else if (batiment.getTypeFonctionnel()==TypeFonctionnel.Industriel) nbBatimentsInd++;
						}
						int nbBatTotal = agentZoneElementaireBatie.getBatiments().size();
						double pourcHab = (double)nbBatimentsHab/nbBatTotal*100;
						double pourcInd = (double)nbBatimentsInd/nbBatTotal*100;
						double pourcPub = (double)nbBatimentsPub/nbBatTotal*100;

						// On ajoute le batiment en plus grande carence sinon tirage au sort parmi les trois
						double diffMaxi = 0;
						agentBatiment.setTypeFonctionnel(TypeFonctionnel.Quelconque);
						if(parametresType.getPourcentageHabitatMin()-pourcHab >diffMaxi){
							diffMaxi = parametresType.getPourcentageHabitatMin()-pourcHab;
							agentBatiment.setTypeFonctionnel(TypeFonctionnel.Habitat);
							logger.debug("diffMaxi hab : "+ diffMaxi);
						}
						if(parametresType.getPourcentageIndustrielMin()-pourcInd >diffMaxi){
							diffMaxi = parametresType.getPourcentageIndustrielMin()-pourcInd;
							agentBatiment.setTypeFonctionnel(TypeFonctionnel.Industriel);
							logger.debug("diffMaxi ind : "+ diffMaxi);
						}
						if(parametresType.getPourcentagePublicMin()-pourcPub>diffMaxi){
							diffMaxi = parametresType.getPourcentagePublicMin()-pourcPub;
							agentBatiment.setTypeFonctionnel(TypeFonctionnel.Public);
							logger.debug("diffMaxi pub : "+ diffMaxi);
						}
						if (agentBatiment.getTypeFonctionnel()==TypeFonctionnel.Quelconque){
							double alea = Math.random();
							if(alea<(1/3)){agentBatiment.setTypeFonctionnel(TypeFonctionnel.Habitat);}
							else if (alea<(2/3)){agentBatiment.setTypeFonctionnel(TypeFonctionnel.Industriel);}
							else{agentBatiment.setTypeFonctionnel(TypeFonctionnel.Public);}
						}
					}
					if (logger.isDebugEnabled()) {
						logger.debug("ajout d'un batiment de type = "+agentBatiment.getTypeFonctionnel());
					}

					IGeometry formeBatiment = null;
					while (formeBatiment == null){
						// Choix de la forme et de l'aire du nouveau bâtiment
						double aireInitiale = 0;
						FormeBatiment typeFormeBatiment = FormeBatiment.Carre ;
						double aireFormeBatiment = 0;
						double elongationBatiment = 0;
						double epaisseurBatiment = 0;

						List<ParametresForme> listeParametresForme = new ArrayList<ParametresForme>();
						// Si il y a des formes de bâtiment pour cette Méthode peuplement
						if (!parametresPeuplement.getFormeBatiment().isEmpty()){
							listeParametresForme = parametresPeuplement.getFormeBatiment();
						}else{// On Détermine la forme en fonction du type fonctionnel
							listeParametresForme = ConfigurationFormesBatimentsV2.getInstance().getParametres(agentBatiment.getTypeFonctionnel());
						}

						double aleaChoix = Math.random()*100;
						double frequenceCumulee = 0;
						double frequenceCumuleePrecedente = 0;
						for (ParametresForme param:listeParametresForme){
							frequenceCumuleePrecedente = frequenceCumulee;
							frequenceCumulee +=	param.getFrequence();
							if ((aleaChoix>frequenceCumuleePrecedente)&&(aleaChoix<frequenceCumulee)) {
								typeFormeBatiment = param.getForme();
								// Pour le moment on utilise seulement la moyenne (et pas l'écart type)
								// L'aire du bâtiment à construire
//								aireFormeBatiment = param.getTailleBatiment().getMoyenne();
								double aireMoy = param.getTailleBatiment().getMoyenne();
								if (aireMoy!=-1){
									double aireET = param.getTailleBatiment().getEcartType();
									if (aireET==-1){aireET = 5/100 * aireMoy;}
									aireFormeBatiment = GenerateurValeur.genererValeurLoiNormale(param.getTailleBatiment().getMoyenne(), param.getTailleBatiment().getEcartType());
								}
								// L'élongation et l'épaisseur du bâtiment à construire
								elongationBatiment = param.getElongationBatiment().getMoyenne();
								epaisseurBatiment = param.getEpaisseurBatiment().getMoyenne();
								break;
							}
						}
						if (logger.isDebugEnabled())logger.debug("ajout d'un batiment de forme = "+typeFormeBatiment);

						formeBatiment = ConstructionBatiment.construire(typeFormeBatiment, centre);
						aireInitiale = formeBatiment.area();

						// Choix de la taille du nouveau bâtiment
						double aireBatiment = 0;
						//Si il y a une aire de bâtiment associée à la forme
						if (aireFormeBatiment!=-1){
							aireBatiment = aireFormeBatiment;
							if (logger.isDebugEnabled()) logger.debug("aireBatiment = "+aireBatiment);
						}else{
							List<Double> listeAires = new ArrayList<Double>();
							// Si la zone élémentaire est vide de tous bâtiments
							if (agentZoneElementaireBatie.getBatiments().size()==0){
								// On crée la liste des aires de batiments utilisés pour le calcul de l'aire du batiment
								for(AgentZoneElementaireBatie voisin:agentZoneElementaireBatie.getVoisins()){
									logger.debug("voisin : "+voisin.getGeom());
									for (AgentBatiment batiment:voisin.getBatiments()) {
										listeAires.add(batiment.getGeom().area());
									}
								}
							}else{ // Si il existe déjà des bâtiments la zone élémentaire
								// On crée la liste des aires de batiments utilisés pour le calcul de l'aire du batiment
								if (logger.isDebugEnabled()) logger.debug("nbbatflo : "+ agentZoneElementaireBatie.getBatiments().size());
								for (AgentBatiment batiment:agentZoneElementaireBatie.getBatiments()) {
									listeAires.add(batiment.getGeom().area());
									if (logger.isDebugEnabled()) logger.debug(batiment.getGeom());
								}
							}
							// On Détermine la taille que devra avoir le nouveau bâtiment
							double moyenneAiresBatiments = MathUtil.moyenne(listeAires);
							double ecartTypeAiresBatiments = MathUtil.ecartType(listeAires, moyenneAiresBatiments);
//							aireBatiment = (ecartTypeAiresBatiments * Math.random())+moyenneAiresBatiments;
							aireBatiment = GenerateurValeur.genererValeurLoiNormale(moyenneAiresBatiments, ecartTypeAiresBatiments);

							if (logger.isDebugEnabled()) {
								logger.debug("moyenneAiresBatiments = "+moyenneAiresBatiments);
								logger.debug("ecartTypeAiresBatiments = "+ecartTypeAiresBatiments);
								logger.debug("aireBatiment = "+aireBatiment);
							}
						}

						// Test flo 25/05/2010
						if ((typeFormeBatiment==FormeBatiment.Carre)||(typeFormeBatiment==FormeBatiment.Rectangle)){
							formeBatiment = ConstructionBatiment.construire(typeFormeBatiment, centre, aireBatiment, elongationBatiment, epaisseurBatiment,-1);
						}
						// On met le bâtiment à la bonne taille
						if (aireBatiment != formeBatiment.area()){
							double scale = Math.sqrt(aireBatiment / formeBatiment.area());
							if (logger.isDebugEnabled()) logger.debug("facteur de l'homothétie = "+scale);
							IGeometry result=null;
							try {
								Polygon polygonBatiment = (Polygon)AdapterFactory.toGeometry(new GeometryFactory(), formeBatiment);
								Polygon nouvelleGeometrie = JtsUtil.homothetie(polygonBatiment, scale);
								result=JtsGeOxygene.makeGeOxygeneGeom(nouvelleGeometrie);
							} catch (Exception e) {
								logger.error("Erreur dans la construction de la géométrie "+e.getMessage());
							}
							if (result!=null){
								formeBatiment = result;
							}
							if (logger.isDebugEnabled()) logger.debug("Batiment initial = "+formeBatiment);
						}

						// On positionne le bâtiment par rapport à la route la plus proche
						// Si il y a des informations sur l'orientation de bâtiment pour cette Méthode peuplement
						double valeurAngle = 0;
						if (!parametresPeuplement.getParalleleRoute()){
							// On tire au sort pour savoir si ce bâtiment sera parallèle ou non (1 chance sur 2)
							double valAlea = Math.random();
							if (valAlea<0.5){valeurAngle = Math.PI/4;}
						}
						//pour qu'il soit parallèle à la route la plus proche
						if (orientationTroncon!=0.0){
							Polygon polygon = null;
							IGeometry result = null;
							try {
								polygon = (Polygon)AdapterFactory.toGeometry(new GeometryFactory(), formeBatiment);
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

									double angleRotation = orientationTroncon - orientationBatiment;
									if (logger.isDebugEnabled()) logger.debug("Orientation Batiment = " + orientationBatiment+ " angle de rotation = "+angleRotation);
									// Rotation du bâtiment
									Polygon nouvelleGeometrie = JtsUtil.rotation(polygon, (angleRotation+valeurAngle));
									result= JtsGeOxygene.makeGeOxygeneGeom(nouvelleGeometrie);
									if (logger.isDebugEnabled()) logger.debug("Orientation Route = " + orientationTroncon);
								}
							}
							catch (Exception e) {
								logger.error("Erreur sur le bâtiment : "+formeBatiment);
								logger.error(e.getCause());
								return;
							}
							if (result!=null){
								formeBatiment = result;
								if (logger.isDebugEnabled()) logger.debug("Batiment après réorientation = "+formeBatiment);
							}
						}

						// On vérifie que le bâtiment est contenu dans la zone élémentaire et n'intersecte pas d'autres batiments
						IGeometry espacePossible = agentZoneElementaireBatie.getGeom();
						IGeometry unionBatiments = new GM_Polygon();
						for (AgentBatiment batiment:agentZoneElementaireBatie.getBatiments()){
							espacePossible = espacePossible.difference(batiment.getGeom());
							unionBatiments = unionBatiments.union(batiment.getGeom());
						}
						if (logger.isDebugEnabled()) logger.debug("espacePossible : "+espacePossible);
						int nbIterationTotal = 0;
						int nbIterationPartiel = 0;
						double facteur = 0.0;
						while ((!espacePossible.contains(formeBatiment))&&(facteur!=1)&&(nbIterationTotal<8)){
							// Si le bâtiment n'est pas contenu dans la zone élémentaire
							nbIterationPartiel = 0;
							while ((!agentZoneElementaireBatie.getGeom().contains(formeBatiment))&&(facteur!=1)&&(nbIterationPartiel<8)){
								logger.debug("Le nouveau bâtiment n'est pas contenu dans la zone élémentaire");
								// Redimensionnement du bâtiment
								facteur = getFacteur(formeBatiment,(GM_Polygon) agentZoneElementaireBatie.getGeom());
								IGeometry result2=null;
								try {
									Polygon polygonBatiment = (Polygon)AdapterFactory.toGeometry(new GeometryFactory(), formeBatiment);
									Polygon nouvelleGeometrie = JtsUtil.homothetie(polygonBatiment, facteur);
									result2=JtsGeOxygene.makeGeOxygeneGeom(nouvelleGeometrie);
								} catch (Exception e) {
									logger.error("Erreur dans la construction de la géométrie "+e.getMessage());
								}
								if (result2!=null){formeBatiment = result2;}
								if (logger.isDebugEnabled()) logger.debug("Batiment après redimensionnement = "+formeBatiment);
								// déplacement du bâtiment
								double tx=0;
								double ty=0;
								IDirectPosition vecteur = getVecteurIntersecte(formeBatiment,(GM_Polygon) agentZoneElementaireBatie.getGeom());
								tx+=vecteur.getX();
								ty+=vecteur.getY();
								if ( (tx!=0) || (ty!=0) ) {
									formeBatiment = formeBatiment.translate(tx, ty, 0);
									if (logger.isDebugEnabled()) {
										logger.debug("translation du batiment = "+tx+"  "+ty);
										logger.debug("Batiment après translation = "+formeBatiment);
									}
								}
								nbIterationPartiel++;
							}
							// Si le bâtiment intersecte un autre bâtiment
							nbIterationPartiel = 0;
							while ((unionBatiments.intersects(formeBatiment))&&(nbIterationPartiel<8)){
								// déplacement du bâtiment
								IDirectPosition centroid = formeBatiment.centroid();
								double tx=0;
								double ty=0;
								for (AgentBatiment batiment:agentZoneElementaireBatie.getBatiments()){
									if (formeBatiment.intersects(batiment.getGeom())){
										logger.debug("Le nouveau bâtiment intersecte le batiment : "+batiment.getGeom());
										IGeometry intersection = batiment.getGeom().intersection(formeBatiment);
										double decalage = Math.sqrt(intersection.area());
										IDirectPosition centroidBatimentZone = batiment.getGeom().centroid();
										double dx = centroid.getX() - centroidBatimentZone.getX();
										double dy = centroid.getY() - centroidBatimentZone.getY();
										double distanceEntreCentroides = Math.sqrt(dx*dx+dy*dy);
										if (distanceEntreCentroides<0.01) continue;
										tx+=dx*decalage/distanceEntreCentroides;
										ty+=dy*decalage/distanceEntreCentroides;
									}
								}
								if ( (tx!=0) || (ty!=0) ) {
									formeBatiment = formeBatiment.translate(tx, ty, 0);
									if (logger.isDebugEnabled()) {
										logger.debug("translation du batiment = "+tx+"  "+ty);
										logger.debug("Batiment après translation = "+formeBatiment);
									}
								}
								// Redimensionnement du bâtiment
								if (unionBatiments.intersects(formeBatiment)){
									double aireDepassement = 0;
									for (AgentBatiment batiment:agentZoneElementaireBatie.getBatiments()){
										if (formeBatiment.intersects(batiment.getGeom())){
											logger.debug("Le nouveau bâtiment intersecte le batiment : "+batiment.getGeom());
											IGeometry intersection = batiment.getGeom().intersection(formeBatiment);
											aireDepassement += intersection.area();
										}
									}
									double facteurBB = (Math.sqrt(formeBatiment.area())-Math.sqrt(aireDepassement)) / Math.sqrt(formeBatiment.area());
									IGeometry result2=null;
									try {
										Polygon polygonBatiment = (Polygon)AdapterFactory.toGeometry(new GeometryFactory(), formeBatiment);
										Polygon nouvelleGeometrie = JtsUtil.homothetie(polygonBatiment, facteurBB);
										result2=JtsGeOxygene.makeGeOxygeneGeom(nouvelleGeometrie);
									} catch (Exception e) {
										logger.error("Erreur dans la construction de la géométrie "+e.getMessage());
									}
									if (result2!=null){formeBatiment = result2;}
									if (logger.isDebugEnabled()) logger.debug("Batiment après redimensionnement = "+formeBatiment);
								}
								nbIterationPartiel++;
							}
							nbIterationTotal++;
						}
						if (logger.isDebugEnabled()) logger.debug("Nombre d'itération pour rentrer dans l'espace vide : "+nbIterationTotal);
						if ((nbIterationPartiel==8)||(nbIterationTotal==8)||(facteur ==1)){
							if (logger.isDebugEnabled()) logger.debug("On annule la forme = "+ formeBatiment);
							formeBatiment = null;
						}

						// vérification des dimensions du bâtiment
						if(formeBatiment!=null){
							if (aireInitiale != formeBatiment.area()){
								if (!ConstructionBatiment.verifier(typeFormeBatiment, formeBatiment)){
									if (logger.isDebugEnabled()) logger.debug("On annule la forme = "+ formeBatiment);
									formeBatiment = null;
								}
							}
						}
					}

					// On attribut sa forme à l'agent Batiment
					agentBatiment.setGeom(formeBatiment);
					agentBatiment.setSimulated(true);

					// on crée un groupe de batiments pour notre nouveau batiment
					double seuilBuffer = 15.0;
					AgentGroupeBatiments agentGroupeBatiments = new AgentGroupeBatiments();
					Set<AgentBatiment> listeBatimentsGroupe = new HashSet<AgentBatiment>();
					listeBatimentsGroupe.add(agentBatiment);
					agentGroupeBatiments.setBatiments(listeBatimentsGroupe);
					// FIXME bufferPolygones or fermeture
					IGeometry resultat = JtsUtil.bufferPolygones(listeBatimentsGroupe,seuilBuffer);
					agentGroupeBatiments.setGeom(resultat);
					agentGroupeBatiments.setSimulated(true);
					agentGroupeBatiments.setZoneElementaireBatie(agentZoneElementaireBatie);
					agentBatiment.setGroupeBatiments(agentGroupeBatiments);
					
					agentZoneElementaireBatie.setDensite(-1);

					agentZoneElementaireBatie.addGroupeBatiments(agentGroupeBatiments);
					if (logger.isDebugEnabled()) logger.debug("Groupe Batiment ajouté = "+agentGroupeBatiments.getGeom());
					agentGroupeBatiments.addBatiment(agentBatiment);
					if (logger.isDebugEnabled()) logger.debug("Batiment ajouté = "+agentBatiment.getGeom());
					//					Set<AgentAlignement> listeAlignements = new HashSet<AgentAlignement>();
					//					groupe.setAlignements(listeAlignements);
					agentGroupeBatiments.instancierContraintes();
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

					// Affichage
					if (logger.isDebugEnabled()) {
						logger.debug("SurfaceBatimentsIntersectes = "+agentBatiment.getSurfaceBatimentsIntersectes());
						logger.debug("SurfaceDepassement = "+agentBatiment.getSurfaceDepassement());
						logger.debug("nbgrbat = "+agentZoneElementaireBatie.getGroupesBatiments().size());
					}
					agentBatiment.instancierContraintes();
					agentBatiment.calculerSatisfaction();
					if (logger.isDebugEnabled()) logger.debug("Satisfaction = "+agentBatiment.getSatisfaction());
					// on active le batiment construit
					agentBatiment.activer();
//		            Moteur.getListe().add(agentBatiment);

					/*
					batiment.calculerSatisfaction();
					if (batiment.getSatisfaction() < 90 ) {
						agentZoneElementaireBatie.getBatiments().remove(batiment);
						batiment.setZoneElementaireBatie(null);
					}
					 */
				}
				// ajouter un batiment dans le plus grand espace vide
			}
			if (logger.isDebugEnabled()) logger.debug("Fin de la densification de la zone élémentaire "+agent);
			// On met à jour la densité de la zone élémentaire
			agentZoneElementaireBatie.miseAjourDensite();

			/*
			for (AgentBatiment batiment:agentZoneElementaireBatie.getBatiments()) {
				batiment.activer();
			}
			 */
		} else {
			logger.error("Agent de type "+agent.getClass()+" au lieu de AgentZoneElementaireBatie");
		}
	}

	/**
	 * @param geomBatiment
	 * @param geomZone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private double getFacteur(IGeometry geomBatiment, IPolygon geomZone) {
	    IGeometry difference = geomBatiment.difference(geomZone);
		if (logger.isDebugEnabled()) logger.debug("intersection totale : "+difference);
		double facteur2=0;
		if ( difference instanceof GM_Polygon ) {
			GM_Polygon polygoneDifference = (GM_Polygon) difference;
			facteur2 = calculFacteur(geomBatiment, geomZone, polygoneDifference);
		}
		else if(difference instanceof GM_MultiSurface){
			double facteur2Min =Double.MAX_VALUE;

			for (GM_Polygon poly:((GM_MultiSurface<GM_Polygon>) difference).getList()) {
				GM_Polygon polygoneDifference = (GM_Polygon) poly;
				facteur2 = calculFacteur(geomBatiment, geomZone, polygoneDifference);

				if (facteur2<facteur2Min){
					facteur2Min = facteur2;
				}
			}
			if (facteur2Min>0.0){
				facteur2 = facteur2Min;
			}
		}
		return facteur2;
	}

	private double calculFacteur(IGeometry geomBatiment, IPolygon geomZone, IPolygon polygoneDifference) {
		double facteur = 0;
		// Détermination du vecteur
		IDirectPositionList listePoints = calculVecteurIntersecte(geomBatiment, geomZone, polygoneDifference);
		IDirectPosition pointPolygone = listePoints.get(1);
		IDirectPosition pointZone = listePoints.get(0);

		// Calcul de la longueur du vecteur
		double longueurRelle = pointPolygone.distance(pointZone);

		// Création d'une droite le long de ce vecteur
		double cosinus = (pointZone.getX()-pointPolygone.getX())/longueurRelle;
		double valX1 = pointPolygone.getX() + cosinus * 1000;
		double valX2 = pointPolygone.getX() - cosinus * 1000;
		double sinus = (pointZone.getY()-pointPolygone.getY())/longueurRelle;
		double valY1 = pointPolygone.getY() + sinus * 1000;
		double valY2 = pointPolygone.getY() - sinus * 1000;
		DirectPosition point1 = new DirectPosition(valX1,valY1);
		DirectPosition point2 = new DirectPosition(valX2,valY2);
		if (logger.isDebugEnabled()) {
            logger.debug(new GM_LineString(
                    new DirectPositionList(Arrays.asList(
                            (IDirectPosition) point1, (IDirectPosition) point2))));
		}

		// projection des points du polygone sur la droite
		IDirectPositionList listePointsProjetes = new DirectPositionList();
		for (IDirectPosition point: geomBatiment.coord()){
			IDirectPosition pointProjete = Operateurs.projection(point, point1, point2);
			listePointsProjetes.add(pointProjete);
		}
		// recherche de la distance maximum entre points projetés selon cet axe
		double distanceMax = Double.MIN_VALUE;
		for (int i = 0;i<listePointsProjetes.size()-2;i++){
			for (int j = i;j<listePointsProjetes.size()-1;j++){
				double distance = listePointsProjetes.get(i).distance(listePointsProjetes.get(j));
				if (distance>distanceMax){
					distanceMax=distance;
				}
			}
		}
		facteur = (distanceMax-longueurRelle)/distanceMax;
		if (logger.isDebugEnabled()) {
			logger.debug("distance max = "+distanceMax);
			logger.debug("facteur de redimensionnement = "+facteur);
		}
		return facteur;
	}

	/**
	 * @param geomBatiment
	 * @param geomZone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private IDirectPosition getVecteurIntersecte(IGeometry geomBatiment, IPolygon geomZone) {
	    IGeometry difference = geomBatiment.difference(geomZone);
		if (logger.isDebugEnabled()) logger.debug("intersection totale : "+difference);
		double tx = 0;
		double ty = 0;
		if ( difference instanceof GM_Polygon ) {
			GM_Polygon polygoneDifference = (GM_Polygon) difference;
			// Détermination du vecteur intersecté
			IDirectPositionList listePoints = calculVecteurIntersecte(geomBatiment, geomZone, polygoneDifference);
			IDirectPosition pointPolygone = listePoints.get(1);
			IDirectPosition pointZone = listePoints.get(0);
			// calcul du vecteur de translation
			if (!pointPolygone.equals(pointZone)) {
				double longueur = pointPolygone.distance(pointZone);
				// FIXME voir la distance...
				tx = (pointZone.getX()-pointPolygone.getX())*(longueur+distanceMinimum)/longueur;
				ty = (pointZone.getY()-pointPolygone.getY())*(longueur+distanceMinimum)/longueur;
				if (logger.isDebugEnabled()) {
					logger.debug("tx = "+tx);
					logger.debug("ty = "+ty);
				}
			}
		}
		else if(difference instanceof GM_MultiSurface){
			double longueurMax = 0.0;
			double txMax = 0;
			double tyMax = 0;
			for (GM_Polygon poly:((GM_MultiSurface<GM_Polygon>) difference).getList()) {
				GM_Polygon polygoneDifference2 = (GM_Polygon) poly;
				// Détermination du vecteur intersecté
				IDirectPositionList listePoints = calculVecteurIntersecte(geomBatiment, geomZone, polygoneDifference2);
				IDirectPosition pointPolygone = listePoints.get(1);
				IDirectPosition pointZone = listePoints.get(0);
				// calcul du vecteur de translation
				if (!pointPolygone.equals(pointZone)) {
					double longueur = pointPolygone.distance(pointZone);
					if (longueur>longueurMax){
						longueurMax = longueur;
						txMax = pointZone.getX()-pointPolygone.getX();
						tyMax = pointZone.getY()-pointPolygone.getY();
						if (logger.isDebugEnabled()) logger.debug("longueurMax = "+ longueurMax);
					}
				}
			}
			if (longueurMax>0.0){
				// FIXME voir la distance...
				tx = txMax * (longueurMax+distanceMinimum)/longueurMax;
				ty = tyMax * (longueurMax+distanceMinimum)/longueurMax;
				if (logger.isDebugEnabled()) {
					logger.debug("tx = "+tx);
					logger.debug("ty = "+ty);
				}
			}
		}
		return new DirectPosition(tx,ty);
	}

	private IDirectPositionList calculVecteurIntersecte(IGeometry geomBatiment, IPolygon geomZone, IPolygon polygoneDifference) {

		IDirectPosition pointPolygone = JtsAlgorithms.getFurthestPoint(geomZone.exteriorLineString(),polygoneDifference.exteriorLineString());
		IDirectPosition pointZone = JtsAlgorithms.getClosestPoint(pointPolygone,geomZone.exteriorLineString());
		if (logger.isDebugEnabled()) {
			logger.debug("polygoneIntersection = "+polygoneDifference);
			logger.debug("vecteur = "+new GM_LineString(new DirectPositionList(Arrays.asList(pointZone,pointPolygone))));
			logger.debug(pointPolygone.distance(pointZone));
		}
		return  new DirectPositionList(Arrays.asList(pointZone,pointPolygone));
	}

	private List<AgentTroncon> getTronconADensifier(AgentZoneElementaireBatie zone){

		// Récupération de la liste des batiments
		AgentUniteBatie unite = zone.getUniteBatie();

		// Création de la carte topo
		CarteTopo carteTopo = new CarteTopo("carteTopo");
		Chargeur.importClasseGeo(new FT_FeatureCollection<AgentTroncon>(zone.getTroncons()), carteTopo, true);
		carteTopo.creeNoeudsManquants(1.0);
		carteTopo.fusionNoeuds(1.0);
		carteTopo.filtreArcsDoublons();
		carteTopo.rendPlanaire(1.0);
		carteTopo.fusionNoeuds(1.0);
		carteTopo.filtreArcsDoublons();
		carteTopo.creeTopologieFaces();

		// Structure de stockage des résultats
		List<GM_LineString> bissectrices = new ArrayList<GM_LineString>();
		List<AgentTroncon> listeTroncons = new ArrayList<AgentTroncon>();
		List<Double> listeDensites = new ArrayList<Double>();
		// Paramètres
		double rayonBuffer = 300;
		double distanceInfinie = 1000;
		int nbPointPolygone = 4;

		for (Face face : carteTopo.getListeFaces()){
			if (face.getGeom().contains(zone.getGeom().buffer(-2))){
				// On récupère la liste des coordonnées de la face
				IDirectPositionList listePointsFace = face.getCoord();
				listePointsFace.remove(0);
				// la face est elle dans le sens trigonométrique ?
				double somme = 0;
				for (int i = 0;i<listePointsFace.size();i++){
					IDirectPosition pointPrec = new DirectPosition();
					if (i==0){pointPrec = listePointsFace.get(listePointsFace.size()-1);}
					else{pointPrec = listePointsFace.get(i-1);}
					IDirectPosition pointEncours = listePointsFace.get(i);
					IDirectPosition pointSuiv = new DirectPosition();
					if (i==listePointsFace.size()-1){pointSuiv = listePointsFace.get(0);}
					else{pointSuiv = listePointsFace.get(i+1);}
					Vecteur vect1 = (new Vecteur(pointPrec,pointEncours)).vectNorme();
					Vecteur vect2 = (new Vecteur(pointEncours,pointSuiv)).vectNorme();
					Vecteur vect3 = vect1.prodVectoriel(vect2);
					somme +=vect3.getZ();
				}
				// On retourne les faces qui ne sont pas dans le sens trigonométrique
				IDirectPositionList listePoints2 = face.getCoord();
				if (somme<0){
					listePoints2.inverseOrdre();
					face.setCoord(listePoints2);
					listePointsFace = face.getCoord();
					listePointsFace.remove(0);
				}

				// On calcule les bissectrices
				List<Noeud> noeudsTraites = new ArrayList<Noeud>();
				for (Noeud noeud : face.noeuds()){
					if (!noeudsTraites.contains(noeud)){
						for (int i = 0;i<listePointsFace.size();i++ ){
							if (listePointsFace.get(i).equals(noeud.getCoord())){
								// Récupération des noeuds
								IDirectPosition noeudInitial = listePointsFace.get(i);
								IDirectPosition pointPrecedent = new DirectPosition();
								IDirectPosition pointSuivant = new DirectPosition();
								if (i-1<0){pointPrecedent = listePointsFace.get(i-1+listePointsFace.size());}
								else {pointPrecedent = listePointsFace.get(i-1);}
								if (i+1>listePointsFace.size()-1){pointSuivant = listePointsFace.get(i+1-listePointsFace.size());}
								else {pointSuivant = listePointsFace.get(i+1);}
								//Création des vecteurs
								Vecteur vect1 = new Vecteur(pointPrecedent,noeudInitial).vectNorme();
								Vecteur vect2 = new Vecteur(noeudInitial,pointSuivant).vectNorme();
								// produit vectoriel
								Vecteur vect3 = vect1.prodVectoriel(vect2);
								if (vect3.getZ()>0){
									vect2 = vect2.multConstante(-1);
								}else {vect1 = vect1.multConstante(-1);}
								// Création du vecteur bissectrice
								Vecteur vect4 = ((vect1.ajoute(vect2)).vectNorme()).multConstante(distanceInfinie);
								DirectPosition pointFinLigne = new DirectPosition(noeudInitial.getX()+vect4.getX(),noeudInitial.getY()+vect4.getY());
								bissectrices.add(new GM_LineString(new DirectPositionList(Arrays.asList(noeudInitial,pointFinLigne))));
							}
						}
					}
					noeudsTraites.add(noeud);
				}

				// Création du buffer
				IGeometry buffer = zone.getGeom().buffer(rayonBuffer);
				for (Arc arc : face.arcs()){
					// Si l'arc n'est pas dans le sens trigonométrique on l'inverse
					int indice0 = listePointsFace.getList().indexOf(arc.getCoord().get(0));
					int indice1 = indice0 +1;
					if (indice1>listePointsFace.size()-1)indice1 = 0;
					if (!listePointsFace.get(indice1).equals(arc.getCoord().get(1))){
						IDirectPositionList listePointArc = arc.getCoord();
						listePointArc.inverseOrdre();
						arc.setCoord(listePointArc);
					}
					//logger.debug("arc traité : "+arc.getGeom());
					if (!arc.isPendant()){// On ne traite que les arcs qui ne sont pas des impasses
						GM_LineString lignePolygone = new GM_LineString(arc.getCoord());

						// On récupère les bissectrices correspondant au troncon
						List<GM_LineString> cotes = new ArrayList<GM_LineString>();
						for (GM_LineString ligne : bissectrices){
							if((ligne.coord().contains(arc.getNoeudIni().getCoord()))||(ligne.coord().contains(arc.getNoeudFin().getCoord()))){
								cotes.add(ligne);
							}
						}
						// On vérifie qu'il existe bien deux bissectrices par troncon
						if (cotes.size()==2){
							// On vérifie que les bissectrices ne se croisent pas
						    IGeometry bissectricesIntersection = cotes.get(0).intersection(cotes.get(1));
							if (bissectricesIntersection instanceof GM_Point){// Si les bissectrices se croisent
								DirectPosition pointIntersection = new DirectPosition(((GM_Point)bissectricesIntersection).coord().get(0).getX(),((GM_Point)bissectricesIntersection).coord().get(0).getY());
								lignePolygone.addControlPoint(lignePolygone.numPoints(), pointIntersection);
								lignePolygone.addControlPoint(0, pointIntersection);
							}else{
								// Calcul du milieu de l'arc
								IDirectPosition dps = arc.getNoeudIni().getGeometrie().getPosition();
								IDirectPosition dpf = arc.getNoeudFin().getGeometrie().getPosition();
								IDirectPosition intermediaire = new DirectPosition((dps.getX()+dpf.getX())/2,(dps.getY()+dpf.getY())/2);

								// Recherche des points au bout des bissectrices
								IDirectPosition point1 = new DirectPosition();
								IDirectPosition point2 = new DirectPosition();
								if (cotes.get(0).coord().contains(arc.getCoord().get(0))){
									point1 = cotes.get(0).coord().get(1);
									point2 = cotes.get(1).coord().get(1);
								}else{
									point1 = cotes.get(1).coord().get(1);
									point2 = cotes.get(0).coord().get(1);
								}

								// Création de la ligne représentant le polygone présent entre les bissectrices
								lignePolygone.addControlPoint(0, point1);
								lignePolygone.addControlPoint(point2);
								double angle = Angle.angleTroisPoints(point1, intermediaire, point2).getValeur();
								double angleDep = (new Angle(intermediaire,point1)).getValeur();
								double increment = angle/(nbPointPolygone+1);
								for (int i =1;i<=nbPointPolygone;i++){
									DirectPosition point = new DirectPosition(intermediaire.getX()+distanceInfinie*Math.cos(i*increment+angleDep),intermediaire.getY()+distanceInfinie*Math.sin(i*increment+angleDep));
									lignePolygone.addControlPoint(0, point);
								}
								lignePolygone.addControlPoint(0, point2);
							}
							// Transformation de la ligne en polygone
							GM_Polygon polygone = new GM_Polygon(lignePolygone);

							// intersection entre le polygone et le buffer
							IGeometry polygoneInfluence = polygone.intersection(buffer);

							// intersection entre le polygone d'influence et les contours de l'Unité batie
							if (polygoneInfluence!=null){
								polygoneInfluence = polygoneInfluence.intersection(unite.getGeom());
							}
							if (logger.isDebugEnabled()) logger.debug("polygoneInfluence : "+polygoneInfluence);

							// Calcul de la densité
							double densite = 0;
							if (polygoneInfluence!=null){
								Collection<AgentBatiment> listeBatiments = AgentGeographiqueCollection.getInstance().getBatiments().select(polygoneInfluence);
								double aireBati = 0;
								for (AgentBatiment bati:listeBatiments){
									aireBati+=bati.getGeom().area();
								}
								densite = aireBati / polygoneInfluence.area();
							}

							// Ajout du troncon et de la densité aux deux listes
							if (listeDensites.size()==0){
								listeDensites.add(0, densite);
								listeTroncons.add(0, (AgentTroncon) arc.getCorrespondant(0));
							}else{
								int indice=-1;
								for (int i=0;i<listeDensites.size();i++){
									if (listeDensites.get(i)>densite){
										indice = i;
									}
								}
								listeDensites.add(indice+1, densite);
								listeTroncons.add(indice+1, (AgentTroncon) arc.getCorrespondant(0));
							}

						}
						else{
							logger.error("Pas deux bissectrices pour le troncon"+ arc.getCorrespondants().get(0));
						}
					}
				}
			}
		}

		return listeTroncons;
	}
}
