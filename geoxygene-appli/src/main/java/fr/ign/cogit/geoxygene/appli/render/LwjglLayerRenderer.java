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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Util;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLTextureManager;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.render.primitive.FeatureRenderer;
import fr.ign.cogit.geoxygene.appli.render.primitive.GL4FeatureRenderer;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.util.Pair;
import fr.ign.cogit.geoxygene.util.gl.GLContext;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLTools;

/**
 * A renderer to render a {@link Layer} into a {@link LayerViewLwjgl1Panel}. It
 * draws directly the layer into the GL context contained into the
 * 
 * @author JeT
 * @see RenderingManager
 * @see Layer
 * @see LayerViewPanel
 */
public class LwjglLayerRenderer extends AbstractLayerRenderer {
    private static Logger logger = Logger.getLogger(LwjglLayerRenderer.class
            .getName()); // logger
    protected EventListenerList listenerList = new EventListenerList();
    // private final Map<String, PrimitiveRenderer> renderers = new
    // HashMap<String, PrimitiveRenderer>();
    // private PrimitiveRenderer defaultRenderer = null;
    // private final DensityFieldPrimitiveRenderer densityFieldPrimitiveRenderer
    // = new DensityFieldPrimitiveRenderer();
    private GL4FeatureRenderer gl4Renderer = null;
    private LayerViewGLPanel layerViewPanel = null;

    /**
     * Constructor of renderer using a {@link Layer} and a
     * {@link LayerViewPanel}.
     * 
     * @param theLayer
     *            a layer to render
     * @param theLayerViewPanel
     *            the panel to draws into
     */
    public LwjglLayerRenderer(final Layer theLayer,
            final LayerViewGLPanel theLayerViewPanel) {
        super(theLayer);
        this.setLayerViewPanel(theLayerViewPanel);
    }

    /**
     * @param layerViewPanel
     *            the layerViewPanel to set
     */
    public void setLayerViewPanel(LayerViewGLPanel layerViewPanel) {
        this.layerViewPanel = layerViewPanel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.AbstractLayerRenderer#getLayerViewPanel
     * ()
     */
    @Override
    public LayerViewGLPanel getLayerViewPanel() {
        return this.layerViewPanel;
    }

    private final FeatureRenderer getRenderer() {
        if (this.gl4Renderer == null) {
            try {
                this.gl4Renderer = new GL4FeatureRenderer(this,
                        this.getGlContext());
            } catch (GLException e) {
                logger.error("impossible to generate a valid GL4 Context");
                e.printStackTrace();
            }
        }
        return this.gl4Renderer;
    }

    private GLContext getGlContext() throws GLException {
        return this.getLayerViewPanel().getGlContext();
    }

    /**
     * Adds an <code>ActionListener</code>.
     * 
     * @param l
     *            the <code>ActionListener</code> to be added
     */
    @Override
    public void addActionListener(final ActionListener l) {
        this.listenerList.add(ActionListener.class, l);
    }

    /**
     * Notifies all listeners that have registered as interested for
     * notification on this event type. The event instance is lazily created.
     * 
     * @see EventListenerList
     */
    @Override
    protected void fireActionPerformed(final ActionEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = this.listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                // Lazily create the event:
                ((ActionListener) listeners[i + 1]).actionPerformed(event);
            }
        }
    }

    /**
     * Create a runnable for the renderer. A renderer create a new image to draw
     * into. If cancel() is called, the rendering stops as soon as possible.
     * When finished, set the variable rendering to false.
     * 
     * @return a new runnable
     * @see Runnable
     * @see #cancel()
     * @see #isRendering()
     */
    @Override
    public final Runnable createRunnable() {
        // this runnable is not dedicated to be launched into a thread.
        // It should be launched by a SyncRenderingManager which calls the run()
        // method as a singular method

        // logger.debug("isCreated() = " + Display.isCreated());
        // logger.debug("isVisible() = " + Display.isVisible());

        // logger.debug("isCreated() = " + Display.isVisible());
        // System.out.println("isActive " + Display.isActive() + " isCreated " +
        // Display.isCreated() + " isVisible " + Display.isVisible());
        // try {
        // if (GLContext.getCapabilities() == null) {
        // return null;
        // }
        // } catch (Exception e) {
        // return null;
        // }

        return new Runnable() {
            @Override
            public void run() {
                LayerViewGLPanel vp = LwjglLayerRenderer.this
                        .getLayerViewPanel();
                try {
                    // now, we are rendering and it's not finished yet
                    LwjglLayerRenderer.this.setRendering(true);
                    LwjglLayerRenderer.this.setRendered(false);
                    // if the rendering is cancelled: stop
                    if (LwjglLayerRenderer.this.isCancelled()) {
                        return;
                    }
                    // if either the width or the height of the panel is lesser
                    // than or equal to 0, stop
                    if (Math.min(vp.getWidth(), vp.getHeight()) <= 0) {
                        return;
                    }
                    // do the actual rendering
                    try {
                        // System.err.println("rendering layer "
                        // + LwjglLayerRenderer.this.getLayer().getName());
                        // System.err.println("rendering feature collection size "
                        // + LwjglLayerRenderer.this.getLayer()
                        // .getFeatureCollection().size());
                        // getGL4Context().checkContext();
                        LwjglLayerRenderer.this.renderHook(vp.getViewport()
                                .getEnvelopeInModelCoordinates());
                        GLTools.glCheckError("LWJGLLayerRenderer::renderHook()");
                    } catch (Throwable t) {
                        logger.warn("LwJGL Rendering failed: " + t.getMessage()
                                + " (" + t.getClass().getSimpleName() + ")");
                        logger.warn("Open GL Error message = "
                                + Util.translateGLErrorString(GL11.glGetError()));
                        t.printStackTrace();
                        return;
                    }
                } finally {
                    // we are no more in rendering progress
                    LwjglLayerRenderer.this.setRendering(false);
                    // FIXME Is this operation really useful or is it a patch?
                    vp.getRenderingManager().repaint();
                }
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.LayerRenderer#initializeRendering()
     */
    @Override
    public void initializeRendering() {
        // System.err.println("initialize Layer before rendering");
        // for (PrimitiveRenderer renderer : this.renderers.values()) {
        // try {
        // renderer.initializeRendering();
        // } catch (RenderingException e) {
        // e.printStackTrace();
        // }
        // }

        super.initializeRendering();
    }

    /**
     * Actually renders the layer in the open GL context. Stop if cancelled is
     * true.
     * 
     * @param theImage
     *            the image to draw into
     * @param envelope
     *            the envelope
     * @throws RenderingException
     * @see #cancel()
     */
    final void renderHook(final IEnvelope envelope) throws RenderingException {
        // if rendering has been cancelled or there is nothing to render, stop
        if (this.isCancelled()
                || this.getLayer().getFeatureCollection() == null
                || !this.getLayer().isVisible()) {
            return;
        }

        int featureRenderIndex = 0;
        // get only visible features
        List<Pair<Symbolizer, IFeature>> featuresToRender = this
                .generateFeaturesToRender(envelope);
        if (featuresToRender != null) {
            for (Pair<Symbolizer, IFeature> pair : featuresToRender) {
                if (this.isCancelled()) {
                    return;
                }
                Symbolizer symbolizer = pair.getU();
                IFeature feature = pair.getV();
                // System.err.println("rendering feature " + feature
                // + " with symbolizer " + symbolizer);
                this.render(symbolizer, feature, this.getLayer());
                featureRenderIndex++;
            }
        }
        this.fireActionPerformed(new ActionEvent(this, 5,
                "Rendering finished", featureRenderIndex)); //$NON-NLS-1$
    }

    // /**
    // * @param feature
    // * @param viewport
    // * @return
    // */
    // private DistanceFieldTexturedPolygonSymbolizer
    // generateDistanceFieldTexturedPolygonSymbolizer(Viewport viewport,
    // IFeature feature) {
    // DistanceFieldTexturedPolygonSymbolizer polygonSymbolizer = new
    // DistanceFieldTexturedPolygonSymbolizer(feature, viewport);
    // DistanceFieldTexture texture = new DistanceFieldTexture(viewport,
    // feature);
    // texture.setTextureToApply(new
    // BasicTexture("./src/main/resources/textures/mer cassini.png"));
    // texture.setUScale(10);
    // texture.setVScale(10);
    // polygonSymbolizer.setTexture(texture);
    // return polygonSymbolizer;
    // }
    //
    // /**
    // * @param feature
    // * @param viewport
    // * @return
    // */
    // private TexturedPolygonSymbolizer
    // generateTexturedPolygonSymbolizer(Viewport viewport, IFeature feature) {
    // IEnvelope envelope = feature.getGeom().getEnvelope();
    // TexturedPolygonSymbolizer polygonSymbolizer = new
    // TexturedPolygonSymbolizer(envelope, viewport);
    // BasicParameterizer parameterizer = new BasicParameterizer(envelope);
    // parameterizer.scaleX(10);
    // parameterizer.scaleY(10);
    // polygonSymbolizer.setParameterizer(parameterizer);
    // BasicTexture texture = new BasicTexture();
    // texture.setTextureFilename("./src/main/resources/textures/dense pine forest.jpg");
    // polygonSymbolizer.setTexture(texture);
    // return polygonSymbolizer;
    // }

    /**
     * Render a feature into an image using the given symbolizer.
     * 
     * @param symbolizer
     *            the symbolizer
     * @param feature
     *            the feature
     */

    private void render(final Symbolizer symbolizer, final IFeature feature,
            final Layer layer) throws RenderingException {
        Viewport viewport = this.getLayerViewPanel().getViewport();
        this.getRenderer().render(feature, layer, symbolizer, viewport);
    }

    @Override
    public void reset() {
        if (this.gl4Renderer != null) {
            this.gl4Renderer.reset();
            this.gl4Renderer = null;
        }
        this.getLayerViewPanel().reset();
        TextureManager.getInstance().clearCache();
        GLTextureManager.getInstance().clearCache();
    }

}
