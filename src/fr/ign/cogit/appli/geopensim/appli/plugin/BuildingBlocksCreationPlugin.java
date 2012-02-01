/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.appli.geopensim.appli.plugin;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.AgentFactory;
import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicBatiment;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Building Blocks Creation plugin.
 * @author Julien Perret
 */
public class BuildingBlocksCreationPlugin implements GeOxygeneApplicationPlugin,
    ActionListener {
  /**
   * Logger.
   */
  static Logger logger = Logger.getLogger(BuildingBlocksCreationPlugin.class.getName());

  private GeOxygeneApplication application = null;

  /**
   * Initialize the plugin.
   * @param application the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication application) {
    this.application = application;
    JMenu menu = null;
    for (Component c : application.getFrame().getJMenuBar().getComponents()) {
      if (c instanceof JMenu) {
        JMenu aMenu = (JMenu) c;
        if (aMenu.getText() != null && aMenu.getText().equalsIgnoreCase("Creation")) { //$NON-NLS-1$
          menu = aMenu;
        }
      }
    }
    if (menu == null) {
      menu = new JMenu("Creation");//$NON-NLS-1$
    }
    JMenuItem menuItem = new JMenuItem("Creation of Buildings Blocks" //$NON-NLS-1$
    );
    menuItem.addActionListener(this);
    menu.add(menuItem);
    application.getFrame().getJMenuBar()
        .add(menu, application.getFrame().getJMenuBar().getMenuCount() - 2);
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    ProjectFrame project = this.application.getFrame()
        .getSelectedProjectFrame();
    Set<Layer> selectedLayers = project.getLayerLegendPanel()
        .getSelectedLayers();
    if (selectedLayers.isEmpty()) {
      BuildingBlocksCreationPlugin.logger
          .error("You need to select at least one network layer."); //$NON-NLS-1$
      return;
    }
    Collection<IFeatureCollection<? extends IFeature>> buildingCollection = new HashSet<IFeatureCollection<? extends IFeature>>();
    CarteTopo carte = new CarteTopo("Carte");
    for (Layer layer : selectedLayers) {
      if (IPolygon.class.isAssignableFrom(layer.getFeatureCollection().getFeatureType().getGeometryType()) ||
          IMultiSurface.class.isAssignableFrom(layer.getFeatureCollection().getFeatureType().getGeometryType())) {
        buildingCollection.add(layer.getFeatureCollection());
      } else {
        carte.importClasseGeo(layer.getFeatureCollection());
      }
    }
    if (logger.isDebugEnabled())
      logger.debug("--- creation des noeuds --- ");
    carte.creeNoeudsManquants(1.0);

    if (logger.isDebugEnabled())
      logger.debug("--- fusion des noeuds --- ");
    carte.fusionNoeuds(1.0);

    if (logger.isDebugEnabled())
      logger.debug("--- découpage des arcs --- ");
    carte.decoupeArcs(1.0);

    if (logger.isDebugEnabled())
      logger.debug("--- filtrage des arcs doublons --- ");
    carte.filtreArcsDoublons();

    if (logger.isDebugEnabled())
      logger.debug("--- rend planaire --- ");
    carte.rendPlanaire(1.0);

    if (logger.isDebugEnabled())
      logger.debug("--- fusion des doublons --- ");
    carte.fusionNoeuds(1.0);

    if (logger.isDebugEnabled())
      logger.debug("--- filtrage des arcs doublons --- ");
    carte.filtreArcsDoublons();

    if (logger.isDebugEnabled())
      logger.debug("--- creation de la topologie des Faces --- ");
    carte.creeTopologieFaces();

    logger.info(carte.getListeFaces().size() + " faces trouvées");

    if (logger.isDebugEnabled())
      logger.debug("Création de l'Index spatial");
    carte.getPopFaces().initSpatialIndex(Tiling.class, false);

    logger.info("Index spatial initialisé");

    Population<AgentGeographique> agents = new Population<AgentGeographique>();
    Population<ZoneElementaireUrbaine> ilots = new Population<ZoneElementaireUrbaine>("BuildingBlocks");
    Population<Batiment> buildings = new Population<Batiment>();

    for (Face face : carte.getPopFaces()) {
      ZoneElementaireUrbaine zoneElementaire = ZoneElementaireUrbaine
      .newInstance(face.getGeometrie());
//      zoneElementaire1.setDateSourceSaisie(date);
      zoneElementaire.setInfinite(face.isInfinite());
      // ajout des bâtiments
      addBuildings(zoneElementaire, buildingCollection, buildings);        
      zoneElementaire.qualifier();
      AgentGeographique agent = AgentFactory
      .newAgentGeographique(ZoneElementaireUrbaine.class);
      agent.add(zoneElementaire);
      agents.add(agent);
      ilots.add(zoneElementaire);
      // max med min
    }
    FeatureType buildingBlockFeatureType = new FeatureType();
    buildingBlockFeatureType.setGeometryType(GM_Polygon.class);
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType("densite",
    "double"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType("idGeo",
    "integer"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType("nombreBatiments", "nbbat",
        "integer"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType(
        "elongation", "double"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType("convexite",
    "double"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType("aire",
    "double"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType(
        "MoyenneAiresBatiments", "moyairebat", "double"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType(
        "EcartTypeAiresBatiments", "ectairebat", "double"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType(
        "MaxAiresBatiments", "maxairebat", "double"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType(
        "MinAiresBatiments", "minairebat", "double"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType(
        "MedianeAiresBatiments", "medairebat", "double"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType(
        "MoyenneElongationBatiments", "moyelonbat", "double"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType(
        "EcartTypeElongationBatiments", "ectelonbat", "double"));
    // featureType.addFeatureAttribute(new AttributeType("maxelonbat",
    // "double"));
    // featureType.addFeatureAttribute(new AttributeType("minelonbat",
    // "double"));
    // featureType.addFeatureAttribute(new AttributeType("medelonbat",
    // "double"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType(
        "MoyenneConvexiteBatiments", "moyconvbat", "double"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType(
        "EcartTypeElongationBatiments", "ectconvbat", "double"));
    // featureType.addFeatureAttribute(new AttributeType("maxconvbat",
    // "double"));
    // featureType.addFeatureAttribute(new AttributeType("minconvbat",
    // "double"));
    // featureType.addFeatureAttribute(new AttributeType("medconvbat",
    // "double"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType(
        "ClassificationFonctionnelle", "classifonc", "integer"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType(
        "DateSourceSaisie", "datesource", "integer"));
    buildingBlockFeatureType.addFeatureAttribute(new AttributeType("Infinite",
    "boolean"));
    ilots.setFeatureType(buildingBlockFeatureType);
    logger.info("Saving urban blocks");
//    ShapefileWriter.write(ilotsDate1, outputDirectory + "ilots_" + date1
//        + ".shp");
    FeatureType buildingFeatureType = new FeatureType();
    buildingFeatureType.setGeometryType(GM_Polygon.class);
    buildingFeatureType.addFeatureAttribute(new AttributeType("zoneElem",
    "integer"));
    buildingFeatureType
    .addFeatureAttribute(new AttributeType("aire", "double"));
    buildingFeatureType.addFeatureAttribute(new AttributeType("convexite",
    "double"));
    buildingFeatureType.addFeatureAttribute(new AttributeType("elongation",
    "double"));
    buildingFeatureType.addFeatureAttribute(new AttributeType("biscornuite", 
        "biscornu", "string"));
    buildingFeatureType.addFeatureAttribute(new AttributeType("OrientationGenerale", "or_gene",
        "double"));
    buildingFeatureType.addFeatureAttribute(new AttributeType("OrientationCotes", "or_murs",
        "double"));
    // buildingFeatureType.addFeatureAttribute(new AttributeType("or_route",
    // "OrientationGeneraleRoute", "double"));
    // buildingFeatureType.addFeatureAttribute(new AttributeType("or_m_route",
    // "OrientationMursRoute", "double"));
    buildingFeatureType.addFeatureAttribute(new AttributeType("nature",
    "String"));
    buildingFeatureType.addFeatureAttribute(new AttributeType("DateSourceSaisie", "datesource",
        "integer"));
    buildingFeatureType.addFeatureAttribute(new AttributeType("typeFonctionnel", "type",
        "integer"));
    buildings.setFeatureType(buildingFeatureType);
    
    ilots.setClasse(ZoneElementaireUrbaine.class);
    ilots.setPersistant(false);
    logger.error("ilots " + ilots.size());
    project.getDataSet().addPopulation(ilots);
    project.addFeatureCollection(ilots, ilots.getNom(), null);
  }

  private void addBuildings(ZoneElementaireUrbaine buildingBlock,
      Collection<IFeatureCollection<? extends IFeature>> inputBuildings,
      Population<Batiment> buildingsPopulation) {
    Collection<IFeature> buildingsInBuildingBlock = null;
    for (IFeatureCollection<? extends IFeature> b : inputBuildings) {
      if (buildingsInBuildingBlock == null) {
        buildingsInBuildingBlock = (Collection<IFeature>) b.select(buildingBlock.getGeometrie());
      } else {
        buildingsInBuildingBlock.addAll(b.select(buildingBlock.getGeometrie()));
      }
    }
    Collection<Batiment> buildings = new ArrayList<Batiment>(0);
    for (IFeature buildingFeature : buildingsInBuildingBlock) {
        IGeometry geometry = null;
        if (buildingBlock.getGeometrie().contains(buildingFeature.getGeom())) {
            geometry = buildingFeature.getGeom();
        } else {
            // we need to decompose the building
            geometry = buildingFeature.getGeom().intersection(buildingBlock.getGeom());
        }
        if (geometry.area() > 1) {
            String nature = (String) buildingFeature.getAttribute("NATURE");
            if (nature == null) {
                nature = "NC";
            }
            if (geometry.isPolygon()) {
                BasicBatiment building = new BasicBatiment();
                building.setGeom(geometry);
                building.setDateSourceSaisie(buildingBlock.getDateSourceSaisie());
                building.setNature(nature);
                buildings.add(building);
                buildingsPopulation.add(building);
            } else {
                if (geometry.isMultiSurface()) {
                    for (GM_Polygon polygon : ((GM_MultiSurface<GM_Polygon>) geometry).getList()) {
                        if (polygon.area() > 1) {
                            BasicBatiment building = new BasicBatiment();
                            building.setGeom(polygon);
                            building.setDateSourceSaisie(buildingBlock.getDateSourceSaisie());
                            building.setNature(nature);
                            buildings.add(building);
                            buildingsPopulation.add(building);
                        }
                    }
                }
            }
        }
    }
    buildingBlock.construireGroupes(buildings);
    if (!buildingBlock.getGroupesBatiments().isEmpty()) {
      logger.info(buildingBlock.getGroupesBatiments().size() + " groupes de batiment");
      logger.info(buildingBlock.getBatiments().size() + " batiment");
    }
  }
}
