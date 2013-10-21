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

package fr.ign.cogit.geoxygene.appli.plugin.cartetopo;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
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
  public final void initialize(final GeOxygeneApplication app) {
    this.application = app;
    
 // Check if the DataMatching menu exists. If not we create it.
    JMenu menu = null;
    String menuName = I18N.getString("CarteTopoPlugin.CarteTopoPlugin"); //$NON-NLS-1$
    for (Component c : application.getMainFrame().getMenuBar().getComponents()) {
      if (c instanceof JMenu) {
        JMenu aMenu = (JMenu) c;
        if (aMenu.getText() != null
            && aMenu.getText().equalsIgnoreCase(menuName)) {
          menu = aMenu;
        }
      }
    }
    if (menu == null) {
      menu = new JMenu(menuName);
    }
    
    // Add network data matching menu item to the menu.
    JMenuItem menuItem = new JMenuItem("Creation of Buildings Blocks"); //$NON-NLS-1$
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    // Refresh menu of the application
    application.getMainFrame().getMenuBar()
        .add(menu, application.getMainFrame().getMenuBar().getMenuCount() - 2);
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    ProjectFrame project = this.application.getMainFrame()
        .getSelectedProjectFrame();
    Set<Layer> selectedLayers = project.getLayerLegendPanel()
        .getSelectedLayers();
    if (selectedLayers.isEmpty()) {
      javax.swing.JOptionPane.showMessageDialog(null, "You need to select at least one layer.");
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

    project.addUserLayer(carte.getPopFaces(), carte.getPopFaces().getNom(), null);
  }
}
