/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.event.FeatureCollectionEvent;
import fr.ign.cogit.geoxygene.feature.event.FeatureCollectionListener;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

/**
 * Project Frame.
 * 
 * @author Julien Perret
 */
public class ProjectFrame extends JInternalFrame implements
                FeatureCollectionListener {
        static Logger logger = Logger.getLogger(ProjectFrame.class.getName());
        private static final long serialVersionUID = 1L;
        private LayerViewPanel layerViewPanel = null;

        /**
         * @return The {@link LayerViewPanel}
         */
        public LayerViewPanel getLayerViewPanel() {
                return this.layerViewPanel;
        }

        private JPanel layerLegendPanel = null;

        private JSplitPane splitPane = new JSplitPane();
        private StyledLayerDescriptor sld = new StyledLayerDescriptor();

        /**
         * Constructor
         * 
         * @param iconImage
         */
        public ProjectFrame(ImageIcon iconImage) {
                this.layerViewPanel = new LayerViewPanel();
                this.layerLegendPanel = new LayerLegendPanel(this.sld);
                this.setSize(600, 400);
                this.setResizable(true);
                this.setClosable(true);
                this.setMaximizable(true);
                this.setIconifiable(true);
                this.setFrameIcon(iconImage);
                this.getContentPane().setLayout(new BorderLayout());
                this.splitPane.setBorder(null);
                this.layerLegendPanel.setLayout(new BoxLayout(
                                this.layerLegendPanel, BoxLayout.PAGE_AXIS));
                this.getContentPane().add(this.splitPane, BorderLayout.CENTER);
                this.splitPane.add(this.layerLegendPanel, JSplitPane.LEFT);
                this.splitPane.add(this.layerViewPanel, JSplitPane.RIGHT);
                this.splitPane.setDividerLocation(200);
                this.splitPane.setOneTouchExpandable(true);
        }

        /**
         * @param population
         * @param name
         */
        public void addFeatureCollection(Population<DefaultFeature> population,
                        String name) {
                Layer layer = this.sld.createLayer(name, population
                                .getFeatureType().getGeometryType());
                this.addLayer(layer);
        }

        /**
         * @param layer
         */
        public void addLayer(Layer layer) {
                if (layer == null)
                        return;
                layer.getFeatureCollection().addFeatureCollectionListener(this);
                this.sld.add(layer);
                this.layerViewPanel.getRenderingManager().addLayer(layer);
                /*
                 * this.layerLegendPanel.add(new JLabel(layer.getName()));
                 * this.layerLegendPanel.repaint();
                 * this.layerLegendPanel.validate();
                 */
                this.featureCollectionToLayerMap.put(layer
                                .getFeatureCollection(), layer);
                this.layerViewPanel.repaint();
        }

        Map<FT_FeatureCollection<? extends FT_Feature>, Layer> featureCollectionToLayerMap = new HashMap<FT_FeatureCollection<? extends FT_Feature>, Layer>();

        /**
         * Dispose of the frame an its {@link LayerViewPanel}.
         */
        @Override
        public void dispose() {
                this.layerViewPanel.dispose();
                super.dispose();
        }

        public List<Layer> getLayers() {
                return this.sld.getLayers();
        }

        @Override
        public void changed(FeatureCollectionEvent event) {
                // logger.info("changed received "+event.getType());
                Layer layer = this.featureCollectionToLayerMap.get(event
                                .getSource());
                if (layer == null)
                        return;
                // this.layerViewPanel.superRepaint();
                if (event.getType() == FeatureCollectionEvent.Type.ADDED)
                        this.layerViewPanel.repaint(layer, event.getFeature());
                else
                        this.layerViewPanel.repaint(layer, (event.getFeature()
                                        .getGeom() != null) ? event
                                        .getFeature().getGeom().union(
                                                        event.getGeometry())
                                        : event.getGeometry());
                /*
                 * GM_Envelope envelope =
                 * event.getFeature().getGeom().envelope();
                 * try {
                 * Point2D upperCorner =
                 * layerViewPanel.getViewport().toViewPoint
                 * (envelope.getUpperCorner());
                 * Point2D lowerCorner =
                 * layerViewPanel.getViewport().toViewPoint
                 * (envelope.getLowerCorner());
                 * this.layerViewPanel.repaint(layer,
                 * (int)lowerCorner.getX(),(int
                 * )upperCorner.getY(),(int)(upperCorner
                 * .getX()-lowerCorner.getX(
                 * )),(int)(lowerCorner.getY()-upperCorner.getY()));
                 * } catch (NoninvertibleTransformException e)
                 * {e.printStackTrace();}
                 */
        }
}
