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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.appli.event.PaintListener;
import fr.ign.cogit.geoxygene.appli.event.ScalePaintListener;
import fr.ign.cogit.geoxygene.appli.mode.AbstractGeometryEditMode;
import fr.ign.cogit.geoxygene.appli.mode.CreateInteriorRingMode;
import fr.ign.cogit.geoxygene.appli.mode.CreateLineStringMode;
import fr.ign.cogit.geoxygene.appli.mode.CreatePolygonMode;
import fr.ign.cogit.geoxygene.appli.mode.Mode;
import fr.ign.cogit.geoxygene.appli.render.RenderUtil;
import fr.ign.cogit.geoxygene.appli.render.RenderingManager;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
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

    List<PaintListener> overlayListeners = new ArrayList<PaintListener>();
    public void addPaintListener(PaintListener listener) {
        this.overlayListeners.add(listener);
    }
    private void paintOverlays(final Graphics graphics) {
        for (PaintListener listener : this.overlayListeners) {
            listener.paint(this, graphics);
        }
    }

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
     * The rendering manager handling the rendering of the layers
     */
    public final void setRenderingManager(RenderingManager manager) {
        this.renderingManager = manager;
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

    private ProjectFrame projectFrame = null;
    public ProjectFrame getProjectFrame() {
        return this.projectFrame;
    }
    /**
     * Default Constructor.
     */
    public LayerViewPanel(final ProjectFrame frame) {
        this.projectFrame = frame;
        this.addPaintListener(new ScalePaintListener());
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
        if (layer.isVisible()) {
            if (this.renderingManager != null) {
                this.renderingManager.render(layer, feature);
            }
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
        if (layer.isVisible()) {
            if (this.renderingManager != null) {
                this.renderingManager.render(layer, geom);
            }
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
            Mode mode = this.getProjectFrame().getMainFrame()
            .getMode().getCurrentMode();
            g.setColor(new Color(1f,0f,0f));
            if (mode instanceof AbstractGeometryEditMode) {
                DirectPositionList points
                = new DirectPositionList();
                points.addAll(((AbstractGeometryEditMode) mode).getPoints());
                if (mode instanceof CreateLineStringMode) {
                    if (!points.isEmpty()) {
                        points.add(((AbstractGeometryEditMode) mode).getCurrentPoint());
                        RenderUtil.draw(new GM_LineString(points),
                                    this.getViewport(),
                                    (Graphics2D) g);
                    }
                } else {
                    if (mode instanceof CreatePolygonMode) {
                        if (!points.isEmpty()) {
                            DirectPosition start = points.get(0);
                            points.add(((AbstractGeometryEditMode) mode).getCurrentPoint());
                            if (points.size() > 2) {
                                points.add(start);
                                RenderUtil.draw(new GM_Polygon(new GM_LineString(points)),
                                            this.getViewport(),
                                            (Graphics2D) g);
                            } else {
                                if (points.size() == 2) {
                                    points.add(start);
                                    RenderUtil.draw(new GM_LineString(points),
                                                this.getViewport(),
                                                (Graphics2D) g);
                                }
                            }
                        }
                    } else {
                        if (mode instanceof CreateInteriorRingMode) {

                        } else {

                        }
                    }
                }
            }
            paintOverlays(g);
        } catch (Throwable t) {
            logger.error(I18N.getString("LayerViewPanel.PaintError")); //$NON-NLS-1$
            // TODO HANDLE EXCEPTIONS
        }
    }
	/**
	 * Returns the size of a pixel in meters.
	 * @return Taille d'un pixel en mètres (la longueur d'un coté de pixel de l'écran).
	 */
	public static double getMETERS_PER_PIXEL() { return METERS_PER_PIXEL; }
	/**
	 * Taille d'un pixel en mètres (la longueur d'un coté de pixel de l'écran)
	 * utilisé pour le calcul de l'echelle courante de la vue. Elle est calculée
	 * à partir de la résolution de l'écran en DPI. Par exemple si la résolution
	 * est 90DPI, c'est: 90 pix/inch = 1/90 inch/pix = 0.0254/90 meter/pix.
	 */
    private final static double METERS_PER_PIXEL = 0.02540005
    / Toolkit.getDefaultToolkit().getScreenResolution();
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
        if (this.getRenderingManager().getLayers().isEmpty()) {
            return null;
        }
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
