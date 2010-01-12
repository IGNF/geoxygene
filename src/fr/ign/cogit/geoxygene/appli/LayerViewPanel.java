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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.appli.render.RenderingManager;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * Panel displaying layers.
 *
 * @author Julien Perret
 */
public class LayerViewPanel extends JPanel {
    /**
     * logger.
     */
    private static Logger logger = Logger.getLogger(LayerViewPanel.class
                .getName());
    /**
     * serial uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Rendering manager.
     */
    private RenderingManager renderingManager = new RenderingManager(this);

    /**
     * @return The rendering manager handling the rendering of the layers
     */
    public final RenderingManager getRenderingManager() {
        return this.renderingManager;
    }

    /**
     * Private viewport. Use getter or setter.
     */
    private Viewport viewport = new Viewport(this);

    /**
     * Private selected features. Use getter and setter.
     */
    private Set<FT_Feature> selectedFeatures = new HashSet<FT_Feature>();

    /**
     * The viewport of the panel.
     *
     * @return the viewport of the panel
     */
    public final Viewport getViewport() {
        return this.viewport;
    }

    /**
     * Default Constructor.
     */
    public LayerViewPanel() {
    }

    @Override
    public final void repaint() {
        if (this.renderingManager != null) {
            this.renderingManager.renderAll();
        }
        superRepaint();
    }

    /**
     * Repaint the feature in the given layer.
     *
     * @param layer
     *            layer in which to repaint the feature
     * @param feature
     *            feature to repaint
     */
    public final void repaint(final Layer layer, final FT_Feature feature) {
        if (this.renderingManager != null) {
            this.renderingManager.render(layer, feature);
        }
        superRepaint();
    }

    /**
     * Repaint.
     *
     * @param layer
     *            a layer
     * @param geom
     *            a geometry
     */
    public final void repaint(final Layer layer, final GM_Object geom) {
        if (this.renderingManager != null) {
            this.renderingManager.render(layer, geom);
        }
        superRepaint();
    }

    /**
     * Repaint the panel using the repaint method of the super class
     * {@link JPanel}.
     * Called in order to perform the progressive rendering.
     *
     * @see #paintComponent(Graphics)
     */
    public final void superRepaint() {
        super.repaint();
    }

    @Override
    public final void paintComponent(final Graphics g) {
        try {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
            super.paintComponent(g);
            // clear the graphics
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            // copy the result of the rendering manager to the panel
            this.renderingManager.copyTo((Graphics2D) g);
            firePainted(g);
        } catch (Throwable t) {
            logger.error(I18N.getString("LayerViewPanel.PaintError")); //$NON-NLS-1$
            // TODO HANDLE EXCEPTIONS
        }
    }

    /**
     * Notify the listeners that the panel has just finished repainting.
     *
     * @param graphics
     *            the graphics that was just painted
     */
    private void firePainted(final Graphics graphics) {
        // TODO NOTIFY LISTENERS
    }

    /**
     * Dispose of the panel and its rendering manager.
     */
    public final void dispose() {
        this.renderingManager.dispose();
        this.viewport = null;
        // TODO
    }

    /**
     * Get the envelope.
     *
     * @return The envelope of all layers of the panel in model coordinates
     */
    public final GM_Envelope getEnvelope() {
        Iterator<Layer> layerIterator = this.getRenderingManager().getLayers()
                    .iterator();
        GM_Envelope envelope = layerIterator.next().getFeatureCollection()
                    .envelope();
        while (layerIterator.hasNext()) {
            envelope.expand(layerIterator.next().getFeatureCollection()
                        .envelope());
        }
        return envelope;
    }

    /**
     * Getter for the selected feature.
     *
     * @return the features selected by the user
     */
    public final Set<FT_Feature> getSelectedFeatures() {
        return this.selectedFeatures;
    }
}
