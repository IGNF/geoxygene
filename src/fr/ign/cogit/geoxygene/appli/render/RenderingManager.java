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

package fr.ign.cogit.geoxygene.appli.render;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.Timer;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.LayerViewPanel;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * A rendering manager responsible for rendering layers in a
 * {@link LayerViewPanel}.
 * @author Julien Perret
 */
public class RenderingManager {
    /**
     * The logger.
     */
    private static final Logger LOGGER =
        Logger.getLogger(RenderingManager.class.getName());
    /**
     * @return The logger
     */
    public static Logger getLogger() { return LOGGER; }
    /**
     * The managed {@link LayerViewPanel} panel.
     */
    private LayerViewPanel layerViewPanel = null;
    /**
     * Set the managed {@link LayerViewPanel} panel.
     * @param aLayerViewPanel the managed {@link LayerViewPanel} panel.
     */
    public final void setLayerViewPanel(final LayerViewPanel aLayerViewPanel) {
        this.layerViewPanel = aLayerViewPanel;
    }
    /**
     * @return The managed {@link LayerViewPanel} panel.
     */
    public final LayerViewPanel getLayerViewPanel() {
        return this.layerViewPanel;
    }
    /**
     * Insertion-ordered map between a layer and its renderer.
     */
    private LinkedHashMap<Layer, LayerRenderer> rendererMap =
        new LinkedHashMap<Layer, LayerRenderer>();
    /**
     * The selection renderer used to render the selected features.
     */
    private SelectionRenderer selectionRenderer = null;
    /**
     * @return The selection renderer used to render the selected features
     */
    public SelectionRenderer getSelectionRenderer() {
        return this.selectionRenderer;
    }
    /**
     * The current daemon.
     */
    private Thread daemon = null;
    /**
     * Maximum time a daemon waits for a runnable to be added to the queue.
     */
    public static final long DAEMON_MAXIMUM_WAITING_TIME = 5000L;
    /**
     * Time between 2 repaintings of the panel during the rendering.
     */
    private static final int REPAINT_TIMER_DELAY = 400;
    /**
     * Queue containg the runnables, one for each layer.
     */
    private LinkedBlockingQueue<Runnable> runnableQueue =
        new LinkedBlockingQueue<Runnable>();
    /**
     * @return The queue containg the runnables, one for each layer.
     */
    public final LinkedBlockingQueue<Runnable> getRunnableQueue() {
        return this.runnableQueue;
    }
    /**
     * Create a new daemon.
     * @return the new daemon
     * @see Thread
     */
    private Thread createDaemon() {
        Thread newDaemon = new Thread() {
            @Override
            public void run() {
                try {
                    for (;;) {
                        Runnable runnable;
                        synchronized (
                                RenderingManager.this.getRunnableQueue()) {
                            if (RenderingManager.this.getRunnableQueue().
                                    isEmpty()) {
                                try {
                                    RenderingManager.this.getRunnableQueue().
                                    wait(DAEMON_MAXIMUM_WAITING_TIME);
                                } catch (InterruptedException ie) {
                                    if (getLogger().isTraceEnabled()) {
                                        getLogger().trace(ie.getMessage());
                                        //ie.printStackTrace();
                                    }
                                }
                            }
                            runnable = RenderingManager.this.
                            getRunnableQueue().poll();
                            if (getLogger().isTraceEnabled()) {
                                getLogger().trace(
                                        RenderingManager.this.
                                        getRunnableQueue().size()
                                        +
                                        " runnables in the queue" //$NON-NLS-1$
                                );
                            }
                            if (runnable == null) { return; }
                        }
                        try {
                            runnable.run();
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                } finally {
                    if (getLogger().isTraceEnabled()) {
                        getLogger().
                        trace("Deamon thread finished"); //$NON-NLS-1$
                    }
                }
            }
        };
        newDaemon.setDaemon(true);
        return newDaemon;
    }

    /**
     * The repaint timer used to repaint the panel during the rendering.
     * It allows for a progressive rendering.
     * @see Timer
     */
    private Timer repaintTimer = new Timer(
            REPAINT_TIMER_DELAY,
            new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
            for (Renderer renderer : getRenderers()) {
                // while a layer is being rendered, repaint the panel
                if (renderer.isRendering()) {
                    RenderingManager.this.getLayerViewPanel().superRepaint();
                    return;
                }
            }
            // when no more layer is being rendered, stop the timer
            RenderingManager.this.getRepaintTimer().stop();
            // repaint the panel
            RenderingManager.this.getLayerViewPanel().superRepaint();
        }
    });

    /**
     * @return The repaint timer
     */
    public final Timer getRepaintTimer() { return this.repaintTimer; }

    /**
     * Constructor of Rendering manager.
     * @param theLayerViewPanel the panel the rendering manager draws into
     */
    public RenderingManager(final LayerViewPanel theLayerViewPanel) {
        this.setLayerViewPanel(theLayerViewPanel);
        // set the repaint timer to coalesce multiple pending events in case
        // the application does not keep up
        this.getRepaintTimer().setCoalesce(true);
        this.selectionRenderer = new SelectionRenderer(theLayerViewPanel);
    }

    /**
     * Return the collection of managed renderers.
     * @return the collection of managed renderers
     * @see LayerRenderer
     */
    public final Collection<LayerRenderer> getRenderers() {
        return this.rendererMap.values();
    }

    /**
     * Render all managed layers.
     */
    public final void renderAll() {
        // if the daemon is still alive, interrupt it
        if ((this.daemon != null) && this.daemon.isAlive()) {
            synchronized (this.daemon) { this.daemon.interrupt(); }
        }
        // create a new daemon
        this.daemon = createDaemon();
        // clear the queue of runnables
        this.getRunnableQueue().clear();
        // start the new daemon
        this.daemon.start();
        // render all layers
        for(Layer layer : this.getLayerViewPanel().getProjectFrame().
                getSld().getLayers()) {
            if (layer.isVisible()) {
                this.render(this.rendererMap.get(layer));
            }
        }
        this.render(this.selectionRenderer);
    }

    /**
     * Add a new layer to the manager and create the corresponding renderer.
     * @param layer the new layer to manage and render
     * @see Layer
     * @see LayerRenderer
     */
    public final void addLayer(final Layer layer) {
        if (this.rendererMap.get(layer) == null) {
            this.rendererMap.put(
                    layer,
                    new LayerRenderer(
                            layer,
                            layer.getFeatureCollection(),
                            this.getLayerViewPanel()));
        }
    }

    /**
     * Remove a layer from the manager.
     * @param layer the layer to remove
     * @see Layer
     * @see LayerRenderer
     */
    public final void removeLayer(final Layer layer) {
        if (this.rendererMap.get(layer) == null) {
            return;
        }
        this.rendererMap.remove(layer);
    }

    /**
     * Render a layer using the given renderer.
     * @param renderer the renderer to run
     * @see LayerRenderer
     */
    public void render(final Renderer renderer) {
        // if the renderer is already rendering, interrupt the current
        // rendering to start a new one
    	if (renderer == null) { return; }
        if (renderer.isRendering()) { renderer.cancel(); }
        // clear the image cache
        renderer.clearImageCache();
        // create a new runnable for the rendering
        Runnable runnable = renderer.createRunnable();
        if (runnable != null) {
            synchronized (this.getRunnableQueue()) {
                // add it to the queue
                this.getRunnableQueue().add(runnable);
                // notify the queue which should wake the daemon up as it
                // should be waiting on it
                this.getRunnableQueue().notify();
            }
        }
        // if the repaint timer is not running yet, start it
        if (!this.getRepaintTimer().isRunning()) {
            // repaint the panel
            this.getLayerViewPanel().superRepaint();
            // start the timer
            this.getRepaintTimer().start();
        }
    }

    /**
     * Return the collection of managed layers in the same order they were
     * added.
     * @return the collection of managed layers
     * @see Layer
     */
    public final Collection<Layer> getLayers() {
        return this.rendererMap.keySet();
    }

    public final Renderer getRenderer(Layer layer) {
        return this.rendererMap.get(layer);
    }

    /**
     * Copy the rendered images to a 2D graphics in the same order the layers
     * were added to the manager.
     * @param destination a 2D graphics to copy the images to
     */
    public final void copyTo(final Graphics2D destination) {
        for(Layer layer : this.getLayerViewPanel().getProjectFrame().
                getSld().getLayers()) {
            if (layer.isVisible()) {
                this.rendererMap.get(layer).copyTo(destination);
            }
        }
        this.selectionRenderer.copyTo(destination);
    }

    /**
     * Dispose of the manager. Cleans up all threads, renderers, daemons, etc.
     */
    public final void dispose() {
        this.rendererMap.clear();
        this.getRepaintTimer().stop();
        this.getRunnableQueue().clear();
        if (this.daemon != null) {
            synchronized (this.daemon) { this.daemon.interrupt(); }
        }
    }

    /**
     * Render a feature using a layer.
     * @param layer layer to render
     * @param feature feature to render
     */
    public final void render(final Layer layer, final FT_Feature feature) {
        Renderer renderer = this.rendererMap.get(layer);
        // if the renderer is not already finished, do nothing
        if (renderer == null || !renderer.isRendered()) { return; }
        // create a new runnable for the rendering
        Runnable runnable = renderer.createFeatureRunnable(feature);
        if (runnable != null) {
            synchronized (this.getRunnableQueue()) {
                // add it to the queue
                this.getRunnableQueue().add(runnable);
                // notify the queue which should wake the daemon up as it
                // should be waiting on it
                this.getRunnableQueue().notify();
            }
        }
        // if the repaint timer is not running yet, start it
        if (!this.getRepaintTimer().isRunning()) {
            // repaint the panel
            this.getLayerViewPanel().superRepaint();
            // start the timer
            this.getRepaintTimer().start();
        }
        this.render(this.selectionRenderer);
    }

    /**
     * Render part of a layer.
     * @param layer layer to render
     * @param geom geometry of the envelope to render
     */
    public final void render(final Layer layer, final GM_Object geom) {
        Renderer renderer = this.rendererMap.get(layer);
        // if the renderer is not already finished, do nothing
        if (renderer == null || !renderer.isRendered()) { return; }
        // create a new runnable for the rendering
        Runnable runnable = renderer.createLocalRunnable(geom);
        if (runnable != null) {
            synchronized (this.getRunnableQueue()) {
                // add it to the queue
                this.getRunnableQueue().add(runnable);
                // notify the queue which should wake the daemon up as it
                // should be waiting on it
                this.getRunnableQueue().notify();
            }
        }
        // if the repaint timer is not running yet, start it
        if (!this.getRepaintTimer().isRunning()) {
            // repaint the panel
            this.getLayerViewPanel().superRepaint();
            // start the timer
            this.getRepaintTimer().start();
        }
        this.render(this.selectionRenderer);
    }
}
