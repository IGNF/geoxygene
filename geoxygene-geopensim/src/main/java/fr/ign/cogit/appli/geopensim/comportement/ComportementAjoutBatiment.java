/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for
 * the development and deployment of geographic (GIS) applications. It is a open
 * source
 * contribution of the COGIT laboratory at the Institut Géographique National
 * (the French
 * National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with
 * this library (see file LICENSE if present); if not, write to the Free
 * Software
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

import fr.ign.cogit.appli.geopensim.ConfigurationFormesBatimentsV2.ParametresForme;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.ParametresMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationTypesFonctionels;
import fr.ign.cogit.appli.geopensim.agent.Agent;
import fr.ign.cogit.appli.geopensim.agent.AgentGeographiqueCollection;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentGroupeBatiments;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentUniteBatie;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTroncon;
import fr.ign.cogit.appli.geopensim.feature.micro.TypeFonctionnel;
import fr.ign.cogit.appli.geopensim.geom.ConstructionBatiment;
import fr.ign.cogit.appli.geopensim.scheduler.Scheduler;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Chargeur;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.delaunay.AbstractTriangulation;
import fr.ign.cogit.geoxygene.contrib.delaunay.TriangulationJTS;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.JtsUtil;
import fr.ign.cogit.geoxygene.util.algo.MesureOrientationFeuille;
import fr.ign.cogit.geoxygene.util.algo.MesureOrientationV2;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

/**
 * @author Julien Perret et Florence Curie
 */
public class ComportementAjoutBatiment extends Comportement {
    private static Logger logger = Logger
            .getLogger(ComportementAjoutBatiment.class.getName());
    static double distanceMinimum = 1;

    @SuppressWarnings( { "unchecked" })
    @Override
    public void declencher(Agent agent) {
        super.declencher(agent);
        if (agent instanceof AgentZoneElementaireBatie) {
            if (logger.isDebugEnabled()) {
                logger.debug("Ajout d'un bâtiment à la zone élémentaire "
                        + agent);
            }
            AgentZoneElementaireBatie agentZoneElementaireBatie
            = (AgentZoneElementaireBatie) agent;
            // On récupère la Méthode de peuplement à appliquer et ses
            // paramètres
            ParametresMethodesPeuplement parametresPeuplement
            = ConfigurationMethodesPeuplement.getInstance().
            getParametresMethodesPeuplement(agentZoneElementaireBatie.choixMethodePeuplement());

            IGeometry reducedBlockGeometry = agentZoneElementaireBatie
            .getGeom().buffer(-parametresPeuplement.getDistanceRoute().getMoyenne());
            if (reducedBlockGeometry == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Ilot trop petit");
                }
                return;
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("ilot réduit = " + reducedBlockGeometry);
                }
            }
            IGeometry reducedBlockGeometryWithoutBuildings
            = (IGeometry) reducedBlockGeometry.clone();
            for (AgentBatiment batiment : agentZoneElementaireBatie
                .getBatiments()) {
                reducedBlockGeometryWithoutBuildings
                = reducedBlockGeometryWithoutBuildings.difference(batiment
                        .getGeom().buffer(parametresPeuplement
                                .getDistanceBatiment().getMoyenne()));
            }
            if (reducedBlockGeometryWithoutBuildings == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Ilot trop petit après enlèvement des bâtiments");
                }
                return;
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("ilot sans batiment = " + reducedBlockGeometryWithoutBuildings);
                }
            }
            // On crée un nouveau batiment
            AgentBatiment agentBatiment = new AgentBatiment();
            // Choix du type fonctionnel du nouveau bâtiment
            // Si il y a un type fonctionnel de bâtiment pour cette
            // Méthode peuplement
            int typeFonctionnel = parametresPeuplement.getTypeFonctionnel();
            if (typeFonctionnel == TypeFonctionnel.Quelconque) {
                typeFonctionnel = ConfigurationTypesFonctionels
                .getInstance().getTypeFonctionnelNewBuilding(agentZoneElementaireBatie);
            }
            agentBatiment.setTypeFonctionnel(typeFonctionnel);
            if (logger.isDebugEnabled()) {
                logger.debug("ajout d'un batiment de type = "
                        + agentBatiment.getTypeFonctionnel());
            }
            // Choix de la forme et de l'aire du nouveau bâtiment
            ParametresForme forme = agentZoneElementaireBatie
            .getParametresForme(agentBatiment.getTypeFonctionnel());
            if (forme == null) {
                logger.error("null building shape");
                return;
            } else {
                logger.error("shape = "+forme.getForme());
            }
            double aireFormeBatiment = forme.getTailleBatiment().getMoyenne();
            double elongationFormeBatiment = forme.getElongationBatiment().getMoyenne();
            if (elongationFormeBatiment == -1) {
                elongationFormeBatiment = 0.3+0.7*Math.random();
            }
            double buildingLength = Math.sqrt(aireFormeBatiment / elongationFormeBatiment);
            double buildingWidth = aireFormeBatiment / (buildingLength * 2);
            if (logger.isInfoEnabled()) {
                logger.info("ajout d'un batiment de taille = "
                        + aireFormeBatiment + " " + elongationFormeBatiment);
                logger.info("ajout d'un batiment de dimensions = "
                        + buildingLength + " " + buildingWidth);
            }
            double maximumDepth = getMaximumDepth(reducedBlockGeometryWithoutBuildings);
            if (buildingWidth > maximumDepth) {
                logger.info("buildingWidth (" + buildingWidth
                        + ") > maximumDepth (" + maximumDepth + ")");
                buildingWidth = maximumDepth;
            }
            if (buildingLength > maximumDepth) {
                logger.info("buildingLength (" + buildingLength
                        + ") > maximumDepth (" + maximumDepth + ")");
                buildingLength = maximumDepth;
            }
            if (aireFormeBatiment > maximumDepth * maximumDepth) {
                logger.info("aireFormeBatiment (" + aireFormeBatiment
                        + ") > maximumDepth² ("
                        + maximumDepth * maximumDepth + ")");
                aireFormeBatiment = maximumDepth * maximumDepth;
            }
            IGeometry medialAxis
            = reducedBlockGeometryWithoutBuildings
            .buffer(-buildingLength);
            if (medialAxis == null || medialAxis.isEmpty()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("medialAxis vide");
                }
                return;
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("medialAxis = " + medialAxis);
                }
            }
            IDirectPosition centre = medialAxis.coord().get(0);
            GM_Polygon buildingGeometry = (GM_Polygon) ConstructionBatiment.
            construire(forme.getForme(), centre,
                    buildingWidth, buildingLength);
            logger.info("new envelope " + buildingGeometry.envelope().width()
                    +  " " + buildingGeometry.envelope().length());

            // Choix de la taille du nouveau bâtiment
            double aireBatiment = 0;
            //Si il y a une aire de bâtiment associée à la forme
            if (aireFormeBatiment!=-1){
                aireBatiment = aireFormeBatiment;
                if (logger.isDebugEnabled()) logger.debug("aireBatiment = "+aireBatiment);
            }else{
                logger.error("pas d'aire définie");
            }
            // On met le bâtiment à la bonne taille
            if (aireBatiment != buildingGeometry.area()){
                double scale = Math.sqrt(aireBatiment / buildingGeometry.area());
                if (logger.isDebugEnabled()) logger.debug("facteur de l'homothétie = "+scale);
                IGeometry result=null;
                try {
                    Polygon polygonBatiment = (Polygon) AdapterFactory.
                    toGeometry(new GeometryFactory(), buildingGeometry);
                    Polygon nouvelleGeometrie = JtsUtil.homothetie(
                            polygonBatiment, scale);
                    result=JtsGeOxygene.makeGeOxygeneGeom(nouvelleGeometrie);
                    logger.info("new envelope " + result.envelope().width()
                            +  " " + result.envelope().length());
                } catch (Exception e) {
                    logger.error("Erreur dans la construction de la géométrie "
                            + e.getMessage());
                }
                if (result != null) {
                    buildingGeometry = (GM_Polygon) result;
                }
                if (logger.isDebugEnabled()) logger.debug("Batiment initial = "+buildingGeometry);
            }

            // On positionne le bâtiment par rapport à la route la plus proche
            // Si il y a des informations sur l'orientation de bâtiment pour cette Méthode peuplement
            double valeurAngle = 0;
            if (!parametresPeuplement.getParalleleRoute()){
                // On tire au sort pour savoir si ce bâtiment sera parallèle ou non (1 chance sur 2)
                double valAlea = Math.random();
                if (valAlea<0.5){valeurAngle = Math.PI/4;}
            }
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
            //pour qu'il soit parallèle à la route la plus proche
            if (orientationTroncon!=0.0){
                Polygon polygon = null;
                IGeometry result = null;
                try {
                    polygon = (Polygon)AdapterFactory.toGeometry(new GeometryFactory(), buildingGeometry);
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
                    logger.error("Erreur sur le bâtiment : "+buildingGeometry);
                    logger.error(e.getCause());
                    return;
                }
                if (result!=null){
                    buildingGeometry = (GM_Polygon) result;
                    if (logger.isDebugEnabled()) logger.debug("Batiment après réorientation = "+buildingGeometry);
                }
            }


            //GM_Polygon buildingGeometry = getBuildingGeometry(medialAxis, buildingWidth, buildingLength);
            if (logger.isDebugEnabled()) {
                logger.debug("buildingGeometry = " + buildingGeometry);
            }

            DataSet.getInstance().getPopulation("MedialAxis").clear();
            Population<DefaultFeature> pop
            = (Population<DefaultFeature>) DataSet.getInstance()
            .getPopulation("MedialAxis");
            pop.add(new DefaultFeature(medialAxis));

            // On attribut sa forme à l'agent Batiment
            agentBatiment.setGeom(buildingGeometry);
            agentBatiment.setSimulated(true);

            // on crée un groupe de batiments pour notre nouveau
            // batiment
            double seuilBuffer = 15.0;
            AgentGroupeBatiments agentGroupeBatiments = new AgentGroupeBatiments();
            Set<AgentBatiment> listeBatimentsGroupe = new HashSet<AgentBatiment>();
            listeBatimentsGroupe.add(agentBatiment);
            agentGroupeBatiments.setBatiments(listeBatimentsGroupe);
            // FIXME bufferPolygones or fermeture
            IGeometry resultat = JtsUtil.bufferPolygones(
                    listeBatimentsGroupe, seuilBuffer);
            agentGroupeBatiments.setGeom(resultat);
            agentGroupeBatiments.setSimulated(true);
            agentGroupeBatiments
            .setZoneElementaireBatie(agentZoneElementaireBatie);
//            agentZoneElementaireBatie.getGroupesBatiments().add(
//                    agentGroupeBatiments);
            agentBatiment.setGroupeBatiments(agentGroupeBatiments);

            agentZoneElementaireBatie
            .addGroupeBatiments(agentGroupeBatiments);
            if (logger.isDebugEnabled())
                logger.debug("Groupe Batiment ajouté = "
                        + agentGroupeBatiments.getGeom());
            agentGroupeBatiments.addBatiment(agentBatiment);
            if (logger.isDebugEnabled())
                logger.debug("Batiment ajouté = "
                        + agentBatiment.getGeom());
            // Set<AgentAlignement> listeAlignements = new
            // HashSet<AgentAlignement>();
            // groupe.setAlignements(listeAlignements);
            agentGroupeBatiments.instancierContraintes();
            agentBatiment.miseAJourGB();

            // on met à jour les distances aux autres éléments
            agentBatiment.calculRouteLaPlusProche();
            agentBatiment.calculTronconLePlusProche();
            agentBatiment.calculGroupeBatimentsLePlusProche();
            agentBatiment.calculBatimentLePlusProche();
            if (logger.isDebugEnabled()) {
                logger.debug("DistanceRouteLaPlusProche = "
                        + agentBatiment.getDistanceRouteLaPlusProche());
                logger.debug("DistanceTronconLePlusProche = "
                        + agentBatiment
                        .getDistanceTronconLePlusProche());
                logger
                .debug("DistanceGroupeBatimentsLePlusProche = "
                        + agentBatiment
                        .getDistanceGroupeBatimentsLePlusProche());
                logger.debug("DistanceBatimentLePlusProche = "
                        + agentBatiment
                        .getDistanceBatimentLePlusProche());
            }

            // Affichage
            if (logger.isDebugEnabled()) {
                logger.debug("SurfaceBatimentsIntersectes = "
                        + agentBatiment
                        .getSurfaceBatimentsIntersectes());
                logger.debug("SurfaceDepassement = "
                        + agentBatiment.getSurfaceDepassement());
                logger.debug("nbgrbat = "
                        + agentZoneElementaireBatie
                        .getGroupesBatiments().size());
            }
            agentBatiment.instancierContraintes();
            agentBatiment.calculerSatisfaction();
            if (logger.isDebugEnabled())
                logger.debug("Satisfaction = "
                        + agentBatiment.getSatisfaction());
            Scheduler.getInstance();
            // on active le batiment construit
            agentBatiment.activer();
//            Moteur.getListe().add(agentBatiment);
            // on force le calcul de la densite
            agentZoneElementaireBatie.setDensite(-1);
            /*
             * batiment.calculerSatisfaction();
             * if (batiment.getSatisfaction() < 90 ) {
             *
             * agentZoneElementaireBatie.getBatiments().remove(batiment);
             * batiment.setZoneElementaireBatie(null);
             * }
             */
            // ajouter un batiment dans le plus grand espace vide
            if (logger.isDebugEnabled())
                logger.debug("Fin de la densification de la zone élémentaire "
                        + agent);
            /*
             * for (AgentBatiment
             * batiment:agentZoneElementaireBatie.getBatiments()) {
             * batiment.activer();
             * }
             */
        } else {
            logger.error("Agent de type " + agent.getClass()
                    + " au lieu de AgentZoneElementaireBatie");
        }
    }

    /**
     * @param geometry
     * @return
     */
    @SuppressWarnings("unchecked")
    private double getMaximumDepth(
            IGeometry geometry) {
        if (geometry.isPolygon()) {
            return getMaximumDepth((GM_Polygon) geometry);
        }
        if (geometry.isMultiSurface()) {
            GM_MultiSurface<GM_Polygon> multiSurface
            = (GM_MultiSurface<GM_Polygon>) geometry;
            double maxDepth = 0;
            for (GM_Polygon polygon : multiSurface) {
                maxDepth = Math.max(maxDepth, getMaximumDepth(polygon));
            }
            return maxDepth;
        }
        logger.error("Geometry depth = "+geometry.getClass());
        return 0;
    }

    /**
     * @param polygon
     * @return
     */
    private double getMaximumDepth(
            GM_Polygon polygon) {
        AbstractTriangulation triangulation = new TriangulationJTS("Medial Axis Triangulation");
        IDirectPositionList list = polygon.getExterior().coord();
        importSegments(triangulation, list);
        GM_MultiCurve<GM_LineString> border = new GM_MultiCurve<GM_LineString>();
        border.add(new GM_LineString(list));
        for (IRing ring : polygon.getInterior()) {
            list = ring.coord();
            importSegments(triangulation, list);
            border.add(new GM_LineString(list));
        }
        try {
            triangulation.triangule("czevBQ");
            double maxDepth = 0;
            for (Noeud node : triangulation.getPopVoronoiVertices()) {
                if (polygon.contains(node.getGeometrie())) {
                    maxDepth = Math.max(maxDepth, node.getGeometrie().
                            distance(border));
                }
            }
            return maxDepth;
        } catch (Exception e) {
            logger.error("Triangulation failed");
            e.printStackTrace();
        }
        return 0;
    }

    private void importSegments(AbstractTriangulation triangulation,
            IDirectPositionList list) {
        Noeud previousNode = null;
        for (IDirectPosition p : list) {
            Noeud node = triangulation.getPopNoeuds().nouvelElement(p.toGM_Point());
            triangulation.addNoeud(node);
            if (previousNode != null) {
                Arc arc = triangulation.getPopArcs().nouvelElement(
                        new GM_LineString(new DirectPositionList(
                                Arrays.asList(previousNode.getCoord(),
                                        node.getCoord()))));
                arc.setNoeudIni(previousNode);
                arc.setNoeudFin(node);
                triangulation.addArc(arc);
            }
            previousNode = node;
        }
    }

    /**
     * @param medialAxis
     * @param buildingWidth
     * @param buildingLength
     * @return
     */
    @SuppressWarnings("unused")
    private GM_Polygon getBuildingGeometry(GM_Object medialAxis,
            double buildingWidth, double buildingLength) {
        // find the best points
        IDirectPositionList axis = getBuildingMedialAxis(medialAxis,
                buildingLength);
        if (logger.isDebugEnabled())
            logger.debug("axe du Batiment = "
                    + axis);
        if (axis == null) { return null; }
        // TODO deform the axis
        GM_Polygon buildingGeometry
        = (GM_Polygon) new JtsAlgorithms().buffer(new GM_LineString(axis),
                buildingWidth, 3, 2);
        return buildingGeometry;
    }

    /**
     * @param medialAxis
     * @param buildingLength
     * @return
     */
    @SuppressWarnings("unchecked")
    private IDirectPositionList getBuildingMedialAxis(IGeometry  medialAxis,
            double buildingLength) {
        if (medialAxis instanceof GM_Polygon) {
            return getBuildingMedialAxis(((GM_Polygon) medialAxis)
                    .getExterior().coord(), buildingLength);
        }
        if (medialAxis instanceof GM_MultiSurface<?>) {
            return getBuildingMedialAxis(
                    (GM_MultiSurface<GM_Polygon>) medialAxis, buildingLength);
        }
        logger.error("medialAxial of type " + medialAxis.getClass().
                getSimpleName());
        return null;
    }

    /**
     * @param coord
     * @param buildingLength
     * @return
     */
    private IDirectPositionList getBuildingMedialAxis(IDirectPositionList coord,
            double buildingLength) {
        IDirectPositionList axis = new DirectPositionList();
        double length = 0.0;
        axis.add(coord.get(0));
        for (int i = 1; i < coord.size(); i++) {
            if (length > buildingLength) { return axis; }
            axis.add(coord.get(i));
            if (logger.isDebugEnabled())
                logger.debug("axis " + i + " = "
                        + axis);
            double dx = coord.get(i).getX() - coord.get(i - 1).getX();
            double dy = coord.get(i).getY() - coord.get(i - 1).getY();
            length += Math.sqrt(dx * dx + dy * dy);
        }
        return axis;
    }

    /**
     * @param medialAxis
     * @param buildingLength
     * @return
     */
    private DirectPositionList getBuildingMedialAxis(
            GM_MultiSurface<GM_Polygon> medialAxis, double buildingLength) {
        return null;
    }

    /**
     * @param geomBatiment
     * @param geomZone
     * @return
     */
    @SuppressWarnings({ "unchecked", "unused" })
    private double getFacteur(IGeometry geomBatiment, IPolygon geomZone) {
        IGeometry difference = geomBatiment.difference(geomZone);
        if (logger.isDebugEnabled())
            logger.debug("intersection totale : " + difference);
        double facteur2 = 0;
        if (difference instanceof GM_Polygon) {
            IPolygon polygoneDifference = (GM_Polygon) difference;
            facteur2 = calculFacteur(geomBatiment, geomZone, polygoneDifference);
        } else if (difference instanceof GM_MultiSurface) {
            double facteur2Min = Double.MAX_VALUE;

            for (GM_Polygon poly : ((GM_MultiSurface<GM_Polygon>) difference)
                    .getList()) {
                GM_Polygon polygoneDifference = (GM_Polygon) poly;
                facteur2 = calculFacteur(geomBatiment, geomZone,
                        polygoneDifference);

                if (facteur2 < facteur2Min) {
                    facteur2Min = facteur2;
                }
            }
            if (facteur2Min > 0.0) {
                facteur2 = facteur2Min;
            }
        }
        return facteur2;
    }

    private double calculFacteur(IGeometry geomBatiment, IPolygon geomZone,
            IPolygon polygoneDifference) {
        double facteur = 0;
        // Détermination du vecteur
        IDirectPositionList listePoints = calculVecteurIntersecte(geomBatiment,
                geomZone, polygoneDifference);
        IDirectPosition pointPolygone = listePoints.get(1);
        IDirectPosition pointZone = listePoints.get(0);

        // Calcul de la longueur du vecteur
        double longueurRelle = Distances.distance(pointPolygone, pointZone);

        // Création d'une droite le long de ce vecteur
        double cosinus = (pointZone.getX() - pointPolygone.getX())
                / longueurRelle;
        double valX1 = pointPolygone.getX() + cosinus * 1000;
        double valX2 = pointPolygone.getX() - cosinus * 1000;
        double sinus = (pointZone.getY() - pointPolygone.getY())
                / longueurRelle;
        double valY1 = pointPolygone.getY() + sinus * 1000;
        double valY2 = pointPolygone.getY() - sinus * 1000;
        DirectPosition point1 = new DirectPosition(valX1, valY1);
        DirectPosition point2 = new DirectPosition(valX2, valY2);
        if (logger.isDebugEnabled()) {
            logger.debug(new GM_LineString(new DirectPositionList(Arrays
                    .asList((IDirectPosition) point1, (IDirectPosition) point2))));
        }

        // projection des points du polygone sur la droite
        DirectPositionList listePointsProjetes = new DirectPositionList();
        for (IDirectPosition point : geomBatiment.coord()) {
            IDirectPosition pointProjete = Operateurs.projection(point, point1,
                    point2);
            listePointsProjetes.add(pointProjete);
        }
        // recherche de la distance maximum entre points projetés selon cet axe
        double distanceMax = Double.MIN_VALUE;
        for (int i = 0; i < listePointsProjetes.size() - 2; i++) {
            for (int j = i; j < listePointsProjetes.size() - 1; j++) {
                double distance = listePointsProjetes.get(i).distance(
                        listePointsProjetes.get(j));
                if (distance > distanceMax) {
                    distanceMax = distance;
                }
            }
        }
        facteur = (distanceMax - longueurRelle) / distanceMax;
        if (logger.isDebugEnabled()) {
            logger.debug("distance max = " + distanceMax);
            logger.debug("facteur de redimensionnement = " + facteur);
        }
        return facteur;
    }

    /**
     * @param geomBatiment
     * @param geomZone
     * @return
     */
    @SuppressWarnings({ "unchecked", "unused" })
    private IDirectPosition getVecteurIntersecte(IGeometry geomBatiment,
            IPolygon geomZone) {
        IGeometry difference = geomBatiment.difference(geomZone);
        if (logger.isDebugEnabled())
            logger.debug("intersection totale : " + difference);
        double tx = 0;
        double ty = 0;
        if (difference instanceof GM_Polygon) {
            GM_Polygon polygoneDifference = (GM_Polygon) difference;
            // Détermination du vecteur intersecté
            IDirectPositionList listePoints = calculVecteurIntersecte(
                    geomBatiment, geomZone, polygoneDifference);
            IDirectPosition pointPolygone = listePoints.get(1);
            IDirectPosition pointZone = listePoints.get(0);
            // calcul du vecteur de translation
            if (!pointPolygone.equals(pointZone)) {
                double longueur = Distances.distance(pointPolygone, pointZone);
                // FIXME voir la distance...
                tx = (pointZone.getX() - pointPolygone.getX())
                        * (longueur + distanceMinimum) / longueur;
                ty = (pointZone.getY() - pointPolygone.getY())
                        * (longueur + distanceMinimum) / longueur;
                if (logger.isDebugEnabled()) {
                    logger.debug("tx = " + tx);
                    logger.debug("ty = " + ty);
                }
            }
        } else if (difference instanceof GM_MultiSurface) {
            double longueurMax = 0.0;
            double txMax = 0;
            double tyMax = 0;
            for (GM_Polygon poly : ((GM_MultiSurface<GM_Polygon>) difference)
                    .getList()) {
                GM_Polygon polygoneDifference2 = (GM_Polygon) poly;
                // Détermination du vecteur intersecté
                IDirectPositionList listePoints = calculVecteurIntersecte(
                        geomBatiment, geomZone, polygoneDifference2);
                IDirectPosition pointPolygone = listePoints.get(1);
                IDirectPosition pointZone = listePoints.get(0);
                // calcul du vecteur de translation
                if (!pointPolygone.equals(pointZone)) {
                    double longueur = Distances.distance(pointPolygone,
                            pointZone);
                    if (longueur > longueurMax) {
                        longueurMax = longueur;
                        txMax = pointZone.getX() - pointPolygone.getX();
                        tyMax = pointZone.getY() - pointPolygone.getY();
                        if (logger.isDebugEnabled())
                            logger.debug("longueurMax = " + longueurMax);
                    }
                }
            }
            if (longueurMax > 0.0) {
                // FIXME voir la distance...
                tx = txMax * (longueurMax + distanceMinimum) / longueurMax;
                ty = tyMax * (longueurMax + distanceMinimum) / longueurMax;
                if (logger.isDebugEnabled()) {
                    logger.debug("tx = " + tx);
                    logger.debug("ty = " + ty);
                }
            }
        }
        return new DirectPosition(tx, ty);
    }

    private IDirectPositionList calculVecteurIntersecte(IGeometry geomBatiment,
            IPolygon geomZone, IPolygon polygoneDifference) {

        IDirectPosition pointPolygone = JtsAlgorithms.getFurthestPoint(geomZone
                .exteriorLineString(), polygoneDifference.exteriorLineString());
        IDirectPosition pointZone = JtsAlgorithms.getClosestPoint(pointPolygone,
                geomZone.exteriorLineString());
        if (logger.isDebugEnabled()) {
            logger.debug("polygoneIntersection = " + polygoneDifference);
            logger.debug("vecteur = "
                    + new GM_LineString(new DirectPositionList(Arrays.asList(
                            pointZone, pointPolygone))));
            logger.debug(pointPolygone.distance(pointZone));
        }
        return new DirectPositionList(Arrays.asList(pointZone, pointPolygone));
    }

    @SuppressWarnings("unused")
    private List<AgentTroncon> getTronconADensifier(
            AgentZoneElementaireBatie zone) {

        // Récupération de la liste des batiments
        AgentUniteBatie unite = zone.getUniteBatie();

        // Création de la carte topo
        CarteTopo carteTopo = new CarteTopo("carteTopo");
        Chargeur.importClasseGeo(new FT_FeatureCollection<AgentTroncon>(zone
                .getTroncons()), carteTopo, true);
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

        for (Face face : carteTopo.getListeFaces()) {
            if (face.getGeom().contains(zone.getGeom().buffer(-2))) {
                // On récupère la liste des coordonnées de la face
                IDirectPositionList listePointsFace = face.getCoord();
                listePointsFace.remove(0);
                // la face est elle dans le sens trigonométrique ?
                double somme = 0;
                for (int i = 0; i < listePointsFace.size(); i++) {
                    IDirectPosition pointPrec = new DirectPosition();
                    if (i == 0) {
                        pointPrec = listePointsFace
                                .get(listePointsFace.size() - 1);
                    } else {
                        pointPrec = listePointsFace.get(i - 1);
                    }
                    IDirectPosition pointEncours = listePointsFace.get(i);
                    IDirectPosition pointSuiv = new DirectPosition();
                    if (i == listePointsFace.size() - 1) {
                        pointSuiv = listePointsFace.get(0);
                    } else {
                        pointSuiv = listePointsFace.get(i + 1);
                    }
                    Vecteur vect1 = (new Vecteur(pointPrec, pointEncours))
                            .vectNorme();
                    Vecteur vect2 = (new Vecteur(pointEncours, pointSuiv))
                            .vectNorme();
                    Vecteur vect3 = vect1.prodVectoriel(vect2);
                    somme += vect3.getZ();
                }
                // On retourne les faces qui ne sont pas dans le sens
                // trigonométrique
                IDirectPositionList listePoints2 = face.getCoord();
                if (somme < 0) {
                    listePoints2.inverseOrdre();
                    face.setCoord(listePoints2);
                    listePointsFace = face.getCoord();
                    listePointsFace.remove(0);
                }

                // On calcule les bissectrices
                List<Noeud> noeudsTraites = new ArrayList<Noeud>();
                for (Noeud noeud : face.noeuds()) {
                    if (!noeudsTraites.contains(noeud)) {
                        for (int i = 0; i < listePointsFace.size(); i++) {
                            if (listePointsFace.get(i).equals(noeud.getCoord())) {
                                // Récupération des noeuds
                                IDirectPosition noeudInitial = listePointsFace
                                        .get(i);
                                IDirectPosition pointPrecedent = new DirectPosition();
                                IDirectPosition pointSuivant = new DirectPosition();
                                if (i - 1 < 0) {
                                    pointPrecedent = listePointsFace.get(i - 1
                                            + listePointsFace.size());
                                } else {
                                    pointPrecedent = listePointsFace.get(i - 1);
                                }
                                if (i + 1 > listePointsFace.size() - 1) {
                                    pointSuivant = listePointsFace.get(i + 1
                                            - listePointsFace.size());
                                } else {
                                    pointSuivant = listePointsFace.get(i + 1);
                                }
                                // Création des vecteurs
                                Vecteur vect1 = new Vecteur(pointPrecedent,
                                        noeudInitial).vectNorme();
                                Vecteur vect2 = new Vecteur(noeudInitial,
                                        pointSuivant).vectNorme();
                                // produit vectoriel
                                Vecteur vect3 = vect1.prodVectoriel(vect2);
                                if (vect3.getZ() > 0) {
                                    vect2 = vect2.multConstante(-1);
                                } else {
                                    vect1 = vect1.multConstante(-1);
                                }
                                // Création du vecteur bissectrice
                                Vecteur vect4 = ((vect1.ajoute(vect2))
                                        .vectNorme())
                                        .multConstante(distanceInfinie);
                                DirectPosition pointFinLigne = new DirectPosition(
                                        noeudInitial.getX() + vect4.getX(),
                                        noeudInitial.getY() + vect4.getY());
                                bissectrices.add(new GM_LineString(
                                        new DirectPositionList(Arrays.asList(
                                                noeudInitial, pointFinLigne))));
                            }
                        }
                    }
                    noeudsTraites.add(noeud);
                }

                // Création du buffer
                IGeometry buffer = zone.getGeom().buffer(rayonBuffer);
                for (Arc arc : face.arcs()) {
                    // Si l'arc n'est pas dans le sens trigonométrique on
                    // l'inverse
                    int indice0 = listePointsFace.getList().indexOf(
                            arc.getCoord().get(0));
                    int indice1 = indice0 + 1;
                    if (indice1 > listePointsFace.size() - 1) indice1 = 0;
                    if (!listePointsFace.get(indice1).equals(
                            arc.getCoord().get(1))) {
                        IDirectPositionList listePointArc = arc.getCoord();
                        listePointArc.inverseOrdre();
                        arc.setCoord(listePointArc);
                    }
                    // logger.debug("arc traité : "+arc.getGeom());
                    if (!arc.isPendant()) {// On ne traite que les arcs qui ne
                                           // sont pas des impasses
                        GM_LineString lignePolygone = new GM_LineString(arc
                                .getCoord());

                        // On récupère les bissectrices correspondant au troncon
                        List<GM_LineString> cotes = new ArrayList<GM_LineString>();
                        for (GM_LineString ligne : bissectrices) {
                            if ((ligne.coord().contains(arc.getNoeudIni()
                                    .getCoord()))
                                    || (ligne.coord().contains(arc
                                            .getNoeudFin().getCoord()))) {
                                cotes.add(ligne);
                            }
                        }
                        // On vérifie qu'il existe bien deux bissectrices par
                        // troncon
                        if (cotes.size() == 2) {
                            // On vérifie que les bissectrices ne se croisent
                            // pas
                            IGeometry bissectricesIntersection = cotes.get(0)
                                    .intersection(cotes.get(1));
                            if (bissectricesIntersection instanceof GM_Point) {// Si
                                                                               // les
                                                                               // bissectrices
                                                                               // se
                                                                               // croisent
                                DirectPosition pointIntersection = new DirectPosition(
                                        ((GM_Point) bissectricesIntersection)
                                                .coord().get(0).getX(),
                                        ((GM_Point) bissectricesIntersection)
                                                .coord().get(0).getY());
                                lignePolygone.addControlPoint(lignePolygone
                                        .numPoints(), pointIntersection);
                                lignePolygone.addControlPoint(0,
                                        pointIntersection);
                            } else {
                                // Calcul du milieu de l'arc
                                IDirectPosition dps = arc.getNoeudIni()
                                        .getGeometrie().getPosition();
                                IDirectPosition dpf = arc.getNoeudFin()
                                        .getGeometrie().getPosition();
                                IDirectPosition intermediaire = new DirectPosition(
                                        (dps.getX() + dpf.getX()) / 2, (dps
                                                .getY() + dpf.getY()) / 2);

                                // Recherche des points au bout des bissectrices
                                IDirectPosition point1 = new DirectPosition();
                                IDirectPosition point2 = new DirectPosition();
                                if (cotes.get(0).coord().contains(
                                        arc.getCoord().get(0))) {
                                    point1 = cotes.get(0).coord().get(1);
                                    point2 = cotes.get(1).coord().get(1);
                                } else {
                                    point1 = cotes.get(1).coord().get(1);
                                    point2 = cotes.get(0).coord().get(1);
                                }

                                // Création de la ligne représentant le polygone
                                // présent entre les bissectrices
                                lignePolygone.addControlPoint(0, point1);
                                lignePolygone.addControlPoint(point2);
                                double angle = Angle.angleTroisPoints(point1,
                                        intermediaire, point2).getValeur();
                                double angleDep = (new Angle(intermediaire,
                                        point1)).getValeur();
                                double increment = angle
                                        / (nbPointPolygone + 1);
                                for (int i = 1; i <= nbPointPolygone; i++) {
                                    DirectPosition point = new DirectPosition(
                                            intermediaire.getX()
                                                    + distanceInfinie
                                                    * Math.cos(i * increment
                                                            + angleDep),
                                            intermediaire.getY()
                                                    + distanceInfinie
                                                    * Math.sin(i * increment
                                                            + angleDep));
                                    lignePolygone.addControlPoint(0, point);
                                }
                                lignePolygone.addControlPoint(0, point2);
                            }
                            // Transformation de la ligne en polygone
                            GM_Polygon polygone = new GM_Polygon(lignePolygone);

                            // intersection entre le polygone et le buffer
                            IGeometry polygoneInfluence = polygone
                                    .intersection(buffer);

                            // intersection entre le polygone d'influence et les
                            // contours de l'Unité batie
                            if (polygoneInfluence != null) {
                                polygoneInfluence = polygoneInfluence
                                        .intersection(unite.getGeom());
                            }
                            if (logger.isDebugEnabled())
                                logger.debug("polygoneInfluence : "
                                        + polygoneInfluence);

                            // Calcul de la densité
                            double densite = 0;
                            if (polygoneInfluence != null) {
                                Collection<AgentBatiment> listeBatiments = AgentGeographiqueCollection
                                        .getInstance().getBatiments().select(
                                                polygoneInfluence);
                                double aireBati = 0;
                                for (AgentBatiment bati : listeBatiments) {
                                    aireBati += bati.getGeom().area();
                                }
                                densite = aireBati / polygoneInfluence.area();
                            }

                            // Ajout du troncon et de la densité aux deux listes
                            if (listeDensites.size() == 0) {
                                listeDensites.add(0, densite);
                                listeTroncons.add(0, (AgentTroncon) arc
                                        .getCorrespondant(0));
                            } else {
                                int indice = -1;
                                for (int i = 0; i < listeDensites.size(); i++) {
                                    if (listeDensites.get(i) > densite) {
                                        indice = i;
                                    }
                                }
                                listeDensites.add(indice + 1, densite);
                                listeTroncons.add(indice + 1,
                                        (AgentTroncon) arc.getCorrespondant(0));
                            }

                        } else {
                            logger
                                    .error("Pas deux bissectrices pour le troncon"
                                            + arc.getCorrespondants().get(0));
                        }
                    }
                }
            }
        }
        return listeTroncons;
    }
}
