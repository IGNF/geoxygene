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

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.Util;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneEventManager;
import fr.ign.cogit.geoxygene.appli.GeoxygeneConstants;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLContext;
import fr.ign.cogit.geoxygene.appli.gl.program.BlendingModeGLProgramBuilder;
import fr.ign.cogit.geoxygene.appli.gl.program.GLProgramBuilder;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.render.primitive.DisplayableFactory;
import fr.ign.cogit.geoxygene.appli.render.primitive.GLDisplayable;
import fr.ign.cogit.geoxygene.appli.render.stats.RenderingStatistics;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
import fr.ign.cogit.geoxygene.appli.task.TaskListener;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.AbstractLayer;
import fr.ign.cogit.geoxygene.style.BlendingMode;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;
import fr.ign.cogit.geoxygene.style.filter.LayerFilter;
import fr.ign.cogit.geoxygene.style.filter.LayerFilterIdentity;
import fr.ign.cogit.geoxygene.util.Pair;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.RenderingException;

/**
 * A renderer to render a {@link Layer} into a {@link LayerViewLwjgl1Panel}. It
 * draws directly the layer into the GL context contained into the
 * 
 * @author JeT, Bertrand Dumenieu
 * @see RenderingManager
 * @see Layer
 * @see LayerViewPanel
 */
public class LwjglLayerRenderer extends AbstractLayerRenderer implements TaskListener<GLDisplayable> {
    private static Logger logger = Logger.getLogger(LwjglLayerRenderer.class.getName()); // logger

    protected EventListenerList listenerList = new EventListenerList();
    private LayerViewGLPanel layerViewPanel = null;

    /**
     * A GLDisplayable is an OpenGL object representing the application of a
     * {@link Symbolizer} to a {@link IFeature}.
     */
    private final Map<IFeature, Map<Symbolizer, GLDisplayable>> dispcache = new HashMap<>();

    private Map<Symbolizer, DisplayableRenderer<? extends GLDisplayable>> mDispRenderers = null;

    private Symbolizer current_symbolizer = null;

    /**
     * Constructor of renderer using a {@link Layer} and a
     * {@link LayerViewPanel}.
     * 
     * @param theLayer
     *            a layer to render
     * @param theLayerViewPanel
     *            the panel to draws into
     */
    public LwjglLayerRenderer(final Layer layer_to_render, final LayerViewGLPanel theLayerViewPanel) {
        super(layer_to_render);
        this.setLayerViewPanel(theLayerViewPanel);
        this.mDispRenderers = new WeakHashMap<Symbolizer, DisplayableRenderer<? extends GLDisplayable>>();
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
        return new Runnable() {
            @Override
            public void run() {
                LayerViewGLPanel vp = LwjglLayerRenderer.this.getLayerViewPanel();
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
                    if (Math.min(vp.getWidth(), vp.getHeight()) <= 1) {
                        return;
                    }
                    // do the actual rendering
                    try {

                        LwjglLayerRenderer.this.renderHook(vp.getViewport().getEnvelopeInModelCoordinates());
                        GLTools.glCheckError("LWJGLLayerRenderer::renderHook()");
                    } catch (Throwable t) {
                        logger.warn("LwJGL Rendering failed for layer '" + LwjglLayerRenderer.this.getLayer().getName() + "': " + t.getMessage() + " (" + t.getClass().getSimpleName() + ")");
                        logger.warn("GL Status is '" + Util.translateGLErrorString(GL11.glGetError()) + "'");
                        t.printStackTrace();
                        return;
                    }
                } finally {
                    // we are no more in rendering progress
                    LwjglLayerRenderer.this.setRendering(false);
                }
            }
        };
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
        if (this.isCancelled() || this.getLayer().getFeatureCollection() == null || !this.getLayer().isVisible()) {
            return;
        }
        int featureRenderIndex = 0;

        double startmem = Runtime.getRuntime().totalMemory() / 1024.;
        // get only visible features
        List<Pair<Symbolizer, IFeature>> featuresToRender = this.generateFeaturesToRender(envelope);
        if (Runtime.getRuntime().totalMemory() / 1024. - startmem > 0) {
            logger.debug("generateFeaturesToRender new memory allocated= " + (Runtime.getRuntime().totalMemory() / 1024. - startmem) + "");

        }
        // Init the FBO textures if FBO rendering is activated and we are not in
        // a quickrendering step.
        boolean quickrendering = this.getLayerViewPanel().getGLCanvas().isQuickRendering();
        if (!quickrendering) {
            try {
                // Cleanup the FBO Layer to be ready to render a new layer.
                this.clearFBOLayer();
                // activate the FBO : draw to ATTACHMENT0.
                this.getLayerViewPanel().getGLCanvas().drawInFBOLayer();
            } catch (GLException e) {
                e.printStackTrace();
            }
            RenderingStatistics.setUserMessage("LwjglLayerRenderer : Draw the layer in the FBO");
        } else {
            RenderingStatistics.setUserMessage("LwjglLayerRenderer : Draw the layer in the default buffer");
        }
        if (featuresToRender != null) {
            for (Pair<Symbolizer, IFeature> pair : featuresToRender) {
                if (this.isCancelled()) {
                    return;
                }
                Symbolizer symbolizer = pair.getU();
                IFeature feature = pair.getV();
                try {
                    this.render(symbolizer, feature);
                } catch (GLException e) {
                    logger.error("Render of feature " + feature.getId() + " failed");
                    e.printStackTrace();
                }
                featureRenderIndex++;
            }
            for(DisplayableRenderer<? extends GLDisplayable> e : this.mDispRenderers.values()){
                    e.switchRenderer();
            }
        }
        this.fireActionPerformed(new ActionEvent(this, 5, "Rendering finished", featureRenderIndex)); //$NON-NLS-1$
    }

    /**
     * Render a feature using a Symbolizer.
     * 
     * @param symbolizer
     *            the symbolizer to apply to this feature.
     * @param feature
     *            the feature to render
     * @throws GLException
     *             : if the rendering failed.
     */

    private boolean render(final Symbolizer symbolizer, final IFeature feature) throws RenderingException, GLException {

        GLDisplayable displayable = this.dispcache.get(feature) != null ? this.dispcache.get(feature).get(symbolizer) : null;
        if (displayable == null) {
            displayable = this.createNewDisplayable(symbolizer, feature);
            this.addDisplayable(feature, symbolizer, displayable);
        }
        if (displayable == null) {
            logger.warn(this.getClass().getSimpleName() + " do not know how to render a feature of class " + feature.getGeom().getClass().getSimpleName());
            return false;
        }
        RenderingStatistics.renderCoupleFeatureSymbolizer(feature, symbolizer);
        this.initializeRendering();

        // If FBO is active and this is not the same symbolizer as before (e.g.
        // there is several FeatureTypes for the same layer), send
        // the current rendered primitive to the pingpongFBO and create a new
        // buffer to allow different blendings for each symbolizers in a same
        // layer.
        if (this.current_symbolizer == null) {
            this.current_symbolizer = symbolizer;
        }
        if (this.current_symbolizer != symbolizer && !this.getLayerViewPanel().getGLCanvas().isQuickRendering()) {
            this.drawInPingPongFBO(); // Save the current rendered image to the
                                      // pingpong FBO
            this.clearFBOLayer(); // Clear The current FBO layer
            this.layerViewPanel.getGLCanvas().drawInFBOLayer();
            GLTools.glCheckError("after FBO initialization");
            this.current_symbolizer = symbolizer;
        }
        // FIXME rawtypes!
        DisplayableRenderer renderer = this.getRendererForDisplayable(symbolizer);
        boolean succ = true;
        if (renderer != null) {
            if (this.layerViewPanel.useWireframe()) {
                this.activateGLPrimitiveWireframe();
                renderer.setWireframeRendering(true);
            }
            succ = renderer.render(displayable, this.getLayer().getOpacity());
            renderer.setWireframeRendering(false);
            renderer.finalizeRendering();
        }
        return succ;
    }

    private DisplayableRenderer<? extends GLDisplayable> getRendererForDisplayable(Symbolizer s) {
        if (this.mDispRenderers.get(s) == null) {
            if (s instanceof LineSymbolizer)
                this.mDispRenderers.put(s, new DisplayableCurveRenderer(this.getLayerViewPanel().getViewport()));
            if (s instanceof PolygonSymbolizer || s instanceof RasterSymbolizer)
                this.mDispRenderers.put(s, new DisplayableSurfaceRenderer(this.getLayerViewPanel().getViewport()));
            if (s instanceof PointSymbolizer)
                this.mDispRenderers.put(s, new DisplayablePointRenderer(this.getLayerViewPanel().getViewport()));
            if (s instanceof TextSymbolizer)
                this.mDispRenderers.put(s, new DisplayableTextRenderer(this.getLayerViewPanel().getViewport()));
        }
        return this.mDispRenderers.get(s);
    }

    /**
     * @param symbolizer
     * @param feature
     * @param layer
     * @param viewport
     * @return
     */
    private GLDisplayable createNewDisplayable(final Symbolizer symbolizer, final IFeature feature) {
        GLDisplayable displayable;
        synchronized (this) {
            IGeometry geometry = feature.getGeom();
            Viewport viewport = this.getLayerViewPanel().getViewport();
            Layer layer = this.getLayer();
            if (geometry == null) {
                logger.warn("null geometry for feature " + feature.getId());
                return null;
            }
            displayable = DisplayableFactory.generateDisplayable(feature, symbolizer, geometry, viewport, layer, this.layerViewPanel.getSld().getSource());
            displayable.addTaskListener(this);
            this.addDisplayable(feature, symbolizer, displayable);
            // Build the displayable Full Representation in a separated Thread.
            GeOxygeneEventManager.getInstance().getApplication().getTaskManager().addTask(displayable);
        }
        return displayable;
    }

    /**
     * @param primitive
     * @throws GLException
     */
    private final void activateGLPrimitiveWireframe() throws GLException {
        glEnable(GL_BLEND);
        int aa = (int) GLContext.getActiveGlContext().getSharedUniform(GeoxygeneConstants.GL_VarName_AntialiasingSize);
        if (aa > 0) {
            glEnable(GL_LINE_SMOOTH);
            glEnable(GL11.GL_POINT_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            glHint(GL11.GL_POINT_SMOOTH_HINT, GL_NICEST);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        } else {
            glDisable(GL_LINE_SMOOTH);
            glDisable(GL11.GL_POINT_SMOOTH);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    /**
     * Draw the Rendered FBO into the Ping Pong FBO
     * 
     * @throws GLException
     */
    private void drawInPingPongFBO() throws GLException {
        if (this.current_symbolizer == null) {
            logger.error("The current symbolizer is NULL");
            return;
        }
        // Get the blending mode of this symbolizer and the filter of the
        // current Layer.
        BlendingMode bmode = this.current_symbolizer.getBlendingMode();
        if (bmode == null) {
            bmode = GeoxygeneConstants.GL_VarName_DefaultBlendingMode;
        }

        LayerFilter layerfilter = ((AbstractLayer) this.getLayer()).getFilter();
        if (layerfilter == null)
            layerfilter = new LayerFilterIdentity();
        String program_name = "GLProgram-" + (bmode.toString() + "-" + layerfilter.getClass().getSimpleName()).toLowerCase();
        GLProgram p = GLContext.getActiveGlContext().getProgram(program_name);
        if (p == null) {
            GLProgramBuilder builder = new GLProgramBuilder();
            builder.addDelegateBuilder(new BlendingModeGLProgramBuilder(bmode, layerfilter));
            p = builder.build(program_name, null);
            if (p == null) {
                logger.warn("Failed to create the blending program " + bmode);
                logger.warn("Fallback to the default blending program " + GeoxygeneConstants.GL_VarName_DefaultBlendingMode);
                bmode = GeoxygeneConstants.GL_VarName_DefaultBlendingMode;
                p = builder.build("GLProgram-" + (bmode.toString() + "-" + LayerFilterIdentity.class.getSimpleName()).toLowerCase(), null);
            }
            if (p == null) {
                logger.fatal("Failed to create the blending program " + bmode + ". Exiting the rendering.");
                return;
            }
            GLContext.getActiveGlContext().addProgram(p);
        }
        GLContext.getActiveGlContext().setCurrentProgram(p);
        this.getLayerViewPanel().getGLCanvas().drawInPingPong(p);

        GL30.glBindVertexArray(LayerViewGLPanel.getScreenQuad().getVaoId());

        p.setUniform(GeoxygeneConstants.GL_VarName_ObjectOpacityVarName, 1f);
        p.setUniform(GeoxygeneConstants.GL_VarName_GlobalOpacityVarName, 1f);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        GLTools.glCheckError("before FBO drawing textured quad");
        LwjglLayerRenderer.drawComplex(LayerViewGLPanel.getScreenQuad());
        GLTools.glCheckError("FBO drawing textured quad");
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL30.glBindVertexArray(0); // unbind VAO
        GLTools.glCheckError("exiting FBO rendering");
        glBindTexture(GL_TEXTURE_2D, 0); // unbind texture
    }

    @Override
    public void initializeRendering() {
    }

    @Override
    public void finalizeRendering() throws RenderingException {
        // draw the last FBO layer into Ping-Pong FBO
        try {
            boolean quickrendering = this.getLayerViewPanel().getGLCanvas().isQuickRendering();
            if (!quickrendering) {
                // Draw everything into the current PingPong and clean the fbo
                // layer
                this.drawInPingPongFBO();
                this.clearFBOLayer();
            }
        } catch (GLException e) {
            throw new RenderingException(e);
        }
    }

    /**
     * Initialize layer FBO to receive GL primitives from a set of identical
     * symbolizers
     * 
     * @param primitive
     * @param opacity
     * @param currentSymbolizer
     * @throws GLException
     */
    private void clearFBOLayer() throws GLException {
        GLTools.glCheckError("preparing next FBO Layer");
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.getLayerViewPanel().getGLCanvas().getFboId());
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        GLTools.glCheckError("bind frame buffer");

        // Blending mode in FBO drawing.
        GLTools.glCheckError("finalizing FBO initialization");
        GL11.glClearColor(0f, 0f, 0f, 0f);
        GLTools.glCheckError("finalizing FBO initialization");
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        GLTools.glCheckError("finalizing FBO initialization");
        glEnable(GL11.GL_BLEND);
        GLTools.glCheckError("finalizing FBO initialization");
        GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD, GL14.GL_MAX);
        GLTools.glCheckError("finalizing FBO initialization");
        GL14.glBlendFuncSeparate(GL11.GL_ONE, GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_ZERO);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
        GLTools.glCheckError("finalizing FBO initialization");
    }

    /**
     * Get the displayable associated with the couple feature-symbolizer
     * 
     * @param feature
     * @param symbolizer
     * @return
     */
    private GLDisplayable getDisplayable(IFeature feature, Symbolizer symbolizer) {
        Map<Symbolizer, GLDisplayable> featureDisplayables = this.dispcache.get(feature);
        if (featureDisplayables == null) {
            return null;
        }
        return featureDisplayables.get(symbolizer);
    }

    @Override
    public void reset() {
        synchronized (this.dispcache) {
            this.dispcache.clear();
        }
        TextureManager.getInstance().clearCache();
    }

    private final void addDisplayable(IFeature feature, Symbolizer symbolizer, GLDisplayable displayable) {
        if (this.dispcache.get(feature) == null) {
            this.dispcache.put(feature, new HashMap<Symbolizer, GLDisplayable>());
        }
        this.dispcache.get(feature).put(symbolizer, displayable);
    }

    private final void removeDisplayable(IFeature feature) {
        Map<Symbolizer, GLDisplayable> featureDisplayables = this.dispcache.remove(feature);
        if (featureDisplayables != null) {
            for (GLDisplayable displayable : featureDisplayables.values()) {
                displayable.removeTaskListener(this);
            }
        }
    }

    @Override
    public final void onStateChange(GLDisplayable displayable, TaskState oldState) {
        if (!displayable.getState().isRunning()) {
            GeOxygeneEventManager.getInstance().getApplication().getMainFrame().getCurrentDesktop().repaint();
        }
    }

    /**
     * Perform an OpenGL drawcall on a primitive and render it in the active
     * FrameBuffer. Any preloaded glprogram will be applied. <b>THIS METHOD
     * SHOULD NOT BE HERE!<b>
     * 
     * @param primitive
     *            primitive to render
     */
    // XXX THIS METHOD SHOULD NOT BE HERE!
    public static void drawComplex(GLComplex primitive) {

        RenderingStatistics.drawGLComplex(primitive);
        for (GLMesh mesh : primitive.getMeshes()) {
            RenderingStatistics.doDrawCall();
            GL11.glDrawElements(mesh.getGlType(), mesh.getLastIndex() - mesh.getFirstIndex() + 1, GL11.GL_UNSIGNED_INT, mesh.getFirstIndex() * (Integer.SIZE / 8));
        }
    }
}
