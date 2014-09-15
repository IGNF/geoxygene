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

package fr.ign.cogit.geoxygene.appli.render;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanelFactory.RenderingType;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.gl.RenderingException;

/**
 * A rendering manager responsible for rendering layers in a
 * {@link LayerViewPanel}.
 * 
 * @author Julien Perret
 */
public class SyncRenderingManager implements RenderingManager {

    private static final Logger logger = Logger
            .getLogger(SyncRenderingManager.class.getName()); // logger

    private LayerViewGLPanel layerViewPanel = null; // managed LayerViewPanel
    private RenderingType renderingType = null;
    // map between a layer and its renderer.
    private final LinkedHashMap<Layer, LwjglLayerRenderer> rendererMap = new LinkedHashMap<Layer, LwjglLayerRenderer>(); // Insertion-ordered
    // The selection renderer used to render the selected features
    private SelectionRenderer selectionRenderer = null;
    private boolean handlingDeletion;

    /**
     * Constructor of Rendering manager.
     * 
     * @param theLayerViewPanel
     *            the panel the rendering manager draws into
     * @param renderingType
     *            in sync rendering manager only JOGL and LWJGL types are
     *            allowed
     */
    public SyncRenderingManager(final LayerViewPanel theLayerViewPanel,
            final RenderingType renderingType) {
        this.setLayerViewPanel(theLayerViewPanel);
        if (renderingType != RenderingType.LWJGL) {
            throw new IllegalStateException(
                    "in sync rendering manager only LWJGL types are allowed (not "
                            + renderingType + ")");
        }
        this.renderingType = renderingType;
        this.selectionRenderer = new SelectionRenderer(theLayerViewPanel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#
     * setLayerViewPanel(fr.ign.cogit.geoxygene.appli.layerview.LayerViewPanel)
     */
    @Override
    public final void setLayerViewPanel(final LayerViewPanel aLayerViewPanel) {
        this.layerViewPanel = (LayerViewGLPanel) aLayerViewPanel;
    }

    /** @return The managed {@link LayerViewPanel} panel. */
    public final LayerViewGLPanel getLayerViewPanel() {
        return this.layerViewPanel;
    }

    /** @return The selection renderer used to render the selected features */
    @Override
    public SelectionRenderer getSelectionRenderer() {
        return this.selectionRenderer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#
     * getRenderers()
     */
    @Override
    public final Collection<LwjglLayerRenderer> getRenderers() {
        return this.rendererMap.values();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#renderAll
     * ()
     */
    @Override
    public final void renderAll() {
        if (this.getLayerViewPanel() == null
                || this.getLayerViewPanel().getProjectFrame() == null) {
            logger.warn(this.getClass().getSimpleName()
                    + " cannot render due to invalid parents : ");
            logger.info("LayerViewPanel = " + this.getLayerViewPanel());
            if (this.getLayerViewPanel() != null) {
                logger.info("ProjectFrame =  "
                        + this.getLayerViewPanel().getProjectFrame());
            }
            return;
        }
        synchronized (this.getLayerViewPanel().getProjectFrame().getSld().lock) {

            // render all layers
            for (Layer layer : this.getLayerViewPanel().getProjectFrame()
                    .getSld().getLayers()) {
                if (layer.isVisible()) {
                    synchronized (this.rendererMap) {

                        LwjglLayerRenderer renderer = this.rendererMap
                                .get(layer);
                        if (renderer == null) {
                            renderer = new LwjglLayerRenderer(layer,
                                    this.layerViewPanel);
                            // logger.debug("No renderer associated with layer "
                            // + layer.getName());
                            // logger.debug("List of all associations: ");
                            // for (Entry<Layer, LwjglLayerRenderer> r :
                            // this.rendererMap
                            // .entrySet()) {
                            // logger.debug("\t" + r.getKey().getName()
                            // + " associated with "
                            // + r.getValue().hashCode());
                            // }
                            this.rendererMap.put(layer, renderer);
                        }
                        this.render(renderer);
                    }
                }
            }
        }
        this.render(this.selectionRenderer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#addLayer
     * (fr.ign.cogit.geoxygene.style.Layer)
     */
    @Override
    public final void addLayer(final Layer layer) {
        synchronized (this.rendererMap) {
            if (this.rendererMap.get(layer) == null) {
                LwjglLayerRenderer renderer = null;
                switch (this.renderingType) {
                case LWJGL:
                    renderer = new LwjglLayerRenderer(layer,
                            this.getLayerViewPanel());
                    break;
                default:
                    logger.error("Cannot handle rendering type "
                            + this.renderingType + " in "
                            + this.getClass().getSimpleName());
                    return;
                }
                this.rendererMap.put(layer, renderer);
                // System.err.println("SyncRenderingManager associates layer "
                // + layer.getName() + " with renderer type "
                // + renderer.getClass().getSimpleName());
                // Adding the layer legend panel to the listeners of the
                // renderer
                renderer.addActionListener(this.getLayerViewPanel()
                        .getProjectFrame().getLayerLegendPanel());
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#removeLayer
     * (fr.ign.cogit.geoxygene.style.Layer)
     */
    @Override
    public final void removeLayer(final Layer layer) {
        synchronized (this.rendererMap) {
            if (this.rendererMap.get(layer) == null) {
                return;
            }
            this.rendererMap.remove(layer);
        }
    }

    private static int n = 0;

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#render
     * (fr.ign.cogit.geoxygene.appli.render.Renderer)
     */
    @Override
    public void render(final LayerRenderer renderer) {
        // if the renderer is already rendering, interrupt the current
        // rendering to start a new one
        if (renderer == null) {
            logger.error(this.getClass().getSimpleName()
                    + " is asked to render using a null renderer");
            Thread.dumpStack();
            return;
        }

        try {
            // initialize rendering
            renderer.initializeRendering();

            // create a new runnable for the rendering
            Runnable runnable = renderer.createRunnable();
            if (runnable != null) {
                try {
                    runnable.run(); // do not launch runnable into a thread,
                                    // just call the run method synchronously
                } catch (Exception e) {
                    logger.error("An error occurred during Sync Rendering : "
                            + e.getMessage());
                    e.printStackTrace();
                }
            }
            // clear the image cache
            renderer.finalizeRendering();
        } catch (RenderingException e) {
            e.printStackTrace();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#render
     * (java.lang.String)
     */
    @Override
    public void render(final String layerName) {
        for (Layer layer : this.getLayers()) {
            if (layer.getName().equalsIgnoreCase(layerName.toLowerCase())) {
                this.render(this.getRenderer(layer));
                return;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#getLayers
     * ()
     */
    @Override
    public final Collection<Layer> getLayers() {
        return this.rendererMap.keySet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#getRenderer
     * (fr.ign.cogit.geoxygene.style.Layer)
     */
    @Override
    public final LayerRenderer getRenderer(final Layer layer) {
        synchronized (this.rendererMap) {
            return this.rendererMap.get(layer);
        }
    }

    // /**
    // * Copy the rendered images to a 2D graphics in the same order the layers
    // were added to the manager.
    // * @param destination a 2D graphics to copy the images to
    // */
    // public final void copyTo(final Graphics2D destination) {
    // for (Layer layer :
    // this.getLayerViewPanel().getProjectFrame().getSld().getLayers()) {
    // if (layer.getOpacity() > 0.0d && this.rendererMap.get(layer) != null) {
    // this.rendererMap.get(layer).copyTo(destination);
    // }
    // }
    // this.selectionRenderer.copyTo(destination);
    // }

    /** Dispose of the manager. Cleans up all threads, renderers, daemons, etc. */
    @Override
    public final void dispose() {
        this.reset();
        this.rendererMap.clear();
    }

    /**
     * empty all cached values
     */
    public void reset() {
        synchronized (this.rendererMap) {
            for (Entry<Layer, LwjglLayerRenderer> r : this.rendererMap
                    .entrySet()) {
                r.getValue().reset();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#repaint
     * ()
     */
    @Override
    public void repaint() {
        // then we check if there is still a renderer working
        for (LayerRenderer r : this.getRenderers()) {
            if (r.isRendering() || !r.isRendered()) {
                //        SyncRenderingManager.logger.debug("Renderer " + r.isRendering() + " - " //$NON-NLS-1$ //$NON-NLS-2$
                // + r.isRendered());
                return;
            }
        }
        if (this.selectionRenderer != null
                && (this.selectionRenderer.isRendering() || !this.selectionRenderer
                        .isRendered())) {
            if (SyncRenderingManager.logger.isTraceEnabled()) {
                SyncRenderingManager.logger.trace("Renderer " //$NON-NLS-1$
                        + this.selectionRenderer.isRendering() + " - " //$NON-NLS-1$
                        + this.selectionRenderer.isRendered());
            }
            return;
        }
        if (SyncRenderingManager.logger.isTraceEnabled()) {
            SyncRenderingManager.logger.trace("Repaint"); //$NON-NLS-1$
        }
        // nothing is being rendered, we can actually repaint the panel
        SyncRenderingManager.this.getLayerViewPanel().superRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#isRendering
     * ()
     */
    @Override
    public boolean isRendering() {
        // rendering is not asynchronous
        return false;
    }

    @Override
    public boolean isHandlingDeletion() {
        return this.handlingDeletion;
    }

    @Override
    public void setHandlingDeletion(boolean handlingDeletion) {
        this.handlingDeletion = handlingDeletion;
    }
}
