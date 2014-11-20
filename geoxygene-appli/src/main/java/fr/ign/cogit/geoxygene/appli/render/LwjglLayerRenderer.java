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
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLContext;
import fr.ign.cogit.geoxygene.appli.gl.GLTextureManager;
import fr.ign.cogit.geoxygene.appli.gl.GeoxygeneBlendingFactory;
import fr.ign.cogit.geoxygene.appli.gl.GeoxygeneBlendingMode;
import fr.ign.cogit.geoxygene.appli.gl.Subshader;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.mode.RenderingTypeMode;
import fr.ign.cogit.geoxygene.appli.render.primitive.DisplayableFactory;
import fr.ign.cogit.geoxygene.appli.render.primitive.GLDisplayable;
import fr.ign.cogit.geoxygene.appli.render.stats.RenderingStatistics;
import fr.ign.cogit.geoxygene.appli.render.texture.ShaderFactory;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
import fr.ign.cogit.geoxygene.appli.task.TaskListener;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
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
 * @author JeT
 * @see RenderingManager
 * @see Layer
 * @see LayerViewPanel
 */
public class LwjglLayerRenderer extends AbstractLayerRenderer implements
        TaskListener<GLDisplayable> {
    private static Logger logger = Logger.getLogger(LwjglLayerRenderer.class
            .getName()); // logger
    private static final float DEFAULT_POINT_SIZE = 3f;
    private static final float DEFAULT_LINE_WIDTH = 1f;

    protected EventListenerList listenerList = new EventListenerList();
    private LayerViewGLPanel layerViewPanel = null;
    private final Map<IFeature, Map<Symbolizer, GLDisplayable>> displayables = new HashMap<IFeature, Map<Symbolizer, GLDisplayable>>();
    private GeoxComplexRendererBasic partialRenderer = null;
    private boolean fboRendering = false;
    // private boolean needInitialization = false;
    private Symbolizer fboSymbolizer = null;
    private GLComplex fboPrimitive = null;
    private double fboOpacity = 1.;
    // stored renderer used to group successive render calls
    private GeoxComplexRenderer previousRenderer = null;
    private Subshader layerFilterSubshader = null; // filter associated to this
                                                   // layer
    private final Map<Symbolizer, Subshader> subshaders = new HashMap<Symbolizer, Subshader>();

    // WARNING: there is a filter associated with each symbolizer too ! do not
    // mix them

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
        this.layerFilterSubshader = ShaderFactory.createFilterShader(null);
        this.setLayerViewPanel(theLayerViewPanel);
        this.partialRenderer = new GeoxComplexRendererBasic(this, null);
    }

    /**
     * @param feature
     * @return the partialRenderer
     */
    public GeoxComplexRendererBasic getPartialRenderer(IFeature feature) {
        return this.partialRenderer;
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

    // private final FeatureRenderer getFeatureRenderer(Layer layer,
    // IFeature feature, Symbolizer symbolizer) {
    // GeoxComplexRenderer featureRenderer = this.renderers.get(feature);
    // if (featureRenderer != null) {
    // return featureRenderer;
    // }
    // featureRenderer = GeoxComplexRendererFactory.createFeatureRenderer(
    // layer, symbolizer, this);
    // this.renderers.put(feature, featureRenderer);
    // return featureRenderer;
    // }

    public GLContext getGlContext() throws GLException {
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
                        logger.warn("LwJGL Rendering failed for layer '"
                                + LwjglLayerRenderer.this.getLayer().getName()
                                + "': " + t.getMessage() + " ("
                                + t.getClass().getSimpleName() + ")");
                        logger.warn("GL Status is '"
                                + Util.translateGLErrorString(GL11.glGetError())
                                + "'");
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

                this.render(symbolizer, feature);
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
    // getFeatureRenderer
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

    private void render(final Symbolizer symbolizer, final IFeature feature)
            throws RenderingException {

        GLDisplayable displayable = this.getDisplayable(symbolizer, feature);

        if (displayable == null) {
            logger.warn(this.getClass().getSimpleName()
                    + " do not know how to render feature "
                    + feature.getGeom().getClass().getSimpleName());
            return;
        }
        RenderingStatistics.renderCoupleFeatureSymbolizer(feature, symbolizer);
        try {
            double layerOpacity = this.getLayer().getOpacity();
            // System.err.println("layer " + this.getLayer().getName()
            // + " opacity = " + layerOpacity);
            Collection<GLComplex> fullRepresentation = displayable
                    .getFullRepresentation();
            if (fullRepresentation == null) {
                GeoxComplexRendererBasic partialRenderer = this
                        .getPartialRenderer(feature);
                partialRenderer.setFBORendering(false);
                partialRenderer.localRendering(
                        displayable.getPartialRepresentation(), layerOpacity);
            } else {
                for (GLComplex complex : fullRepresentation) {
                    this.renderGLPrimitive(complex, layerOpacity);
                }
            }
        } catch (GLException e) {
            throw new RenderingException(e);
        }

    }

    /**
     * @param symbolizer
     * @param feature
     * @param layer
     * @param viewport
     * @return
     */
    private GLDisplayable getDisplayable(final Symbolizer symbolizer,
            final IFeature feature) {
        GLDisplayable displayable;
        synchronized (this.displayables) {
            // try to retrieve previously generated geometry
            displayable = this.getDisplayable(feature, symbolizer);
            // System.err.println("*********************** retrieve feature "
            // + feature.hashCode() + " & symbolizer "
            // + symbolizer.hashCode() + " association : " + displayable);
            // System.err.println("generate geometry from feature collection = "
            // + feature.getFeatureCollection(0).hashCode());
            // System.err.println("feature geom envelope = "
            // + feature.getGeom().getEnvelope());
            // System.err.println("feature collection envelope = "
            // + feature.getFeatureCollection(0).getEnvelope());
            if (displayable == null) {
                // if no displayable exists, create a new one

                IGeometry geometry = feature.getGeom();
                LwjglLayerRenderer layerRenderer = this;
                Viewport viewport = layerRenderer.getLayerViewPanel()
                        .getViewport();
                Layer layer = layerRenderer.getLayer();
                if (geometry == null) {
                    logger.warn("null geometry for feature " + feature.getId());
                    return null;
                }
                displayable = DisplayableFactory.generateDisplayable(feature,
                        symbolizer, geometry, layerRenderer, viewport, layer);

                // stores generated geom
                this.addDisplayable(feature, symbolizer, displayable);
            }
            // else {
            // System.err.println("*********************** feature " + feature
            // + " & symbolizer " + symbolizer
            // + " already associated to displayable " + displayable);
            // }
        }
        return displayable;
    }

    // /**
    // * Render a list of primitives
    // *
    // * @param complexes
    // * @param data
    // * .getViewport()
    // * @throws GLException
    // * @throws RenderingException
    // */
    // private final void renderGLPrimitive(Collection<GLComplex> complexes,
    // double opacity) throws GLException, RenderingException {
    // for (GLComplex complex : complexes) {
    // this.renderGLPrimitive(complex, opacity);
    // }
    //
    // }

    /**
     * Draw a filled shape with open GL
     * 
     * @throws GLException
     * @throws RenderingException
     */
    private final void renderGLPrimitive(GLComplex primitive, double opacity)
            throws GLException, RenderingException {
        // glEnableVertexAttribArray(COLOR_ATTRIBUTE_ID);
        if (this.getLayerViewPanel().useWireframe()) {
            RenderingStatistics.setUserMessage("Wireframe rendering");
            this.renderGLPrimitiveWireframe(primitive);
        } else {
            this.renderGLPrimitivePlain(primitive, opacity);
        }

    }

    /**
     * @param primitive
     * @param opacity
     * @throws GLException
     * @throws RenderingException
     */
    private final void renderGLPrimitivePlain(GLComplex primitive,
            double opacity) throws GLException, RenderingException {
        boolean quickRendering = this.getLayerViewPanel().getProjectFrame()
                .getMainFrame().getMode().getCurrentMode().getRenderingType() != RenderingTypeMode.FINAL
                || !this.getLayerViewPanel().useFBO();
        // System.err.println("rendering primitive "
        // + primitive.getMeshes().size() + " meshes");
        GeoxComplexRenderer currentRenderer = (GeoxComplexRenderer) primitive
                .getRenderer();
        if (currentRenderer == null) {
            logger.error("No renderer associated with full representation in layer "
                    + this.getLayer().getName());
            return;
        }
        // if (currentRenderer instanceof GeoxComplexRendererText
        // && (quickRendering || !this.getLayerViewPanel().useFBO())) {
        // // text rendering is a little bit time consuming. Skip it in quick
        // // rendering mode
        // return;
        // }
        if (quickRendering) {
            currentRenderer.setFBORendering(false);
            RenderingStatistics.setUserMessage("FBO is off");
            this.setFBORendering(false);
            currentRenderer.render(primitive, opacity);
        } else {
            RenderingStatistics.setUserMessage("FBO is on");
            this.setFBORendering(true);
            currentRenderer.setFBORendering(true);
            this.fboRendering(primitive, currentRenderer, opacity);
        }
    }

    static GeoxComplexRenderer staticPreviousRenderer = null;

    /**
     * Render a GL Primitive into the layer FBO. It waits for symbolizer changes
     * to draw the layer FBO into the PingPong FBO (drawLayerFBOinPingPongFBO)
     * 
     * @param primitive
     * @param renderer
     * @param opacity
     * @throws GLException
     * @throws RenderingException
     */
    private void fboRendering(GLComplex primitive,
            GeoxComplexRenderer renderer, double opacity) throws GLException,
            RenderingException {
        this.setFBORendering(true);
        // if (this.previousFBOImageWidth == -1
        // || this.previousFBOImageHeight == -1) {
        // this.initializeFBO();
        // GLTools.glCheckError("FBO init");
        // }
        Symbolizer currentSymbolizer = renderer.getSymbolizer();
        if (currentSymbolizer != this.fboSymbolizer
                || this.fboSymbolizer == null) {
            if (this.fboSymbolizer != null) {
                int antialisingSize = this.getLayerViewPanel()
                        .getAntialiasingSize() + 1;
                this.drawLayerFBOinPingPongFBO(this.fboSymbolizer,
                        this.fboOpacity, antialisingSize);
                GLTools.glCheckError("after drawing FBO Layer in FBO ping-pong");
            }
            this.clearFBOLayer(primitive, opacity, currentSymbolizer);
            this.clearCurrentPingPongFBO();
            GLTools.glCheckError("after FBO initialization");
        }

        this.getLayerViewPanel().drawInFBOLayer();

        // display the layer

        if (this.previousRenderer != renderer) {
            if (this.previousRenderer != null) {
                this.previousRenderer.switchRenderer();
            }
            renderer.activateRenderer();
            this.previousRenderer = renderer;
        }
        renderer.setFBORendering(true);
        renderer.render(primitive, 1f);
        renderer.setFBORendering(false);
        staticPreviousRenderer = renderer;

    }

    /**
     * Draw the Rendered FBO into the Ping Pong FBO
     * 
     * @param primitive
     * @param opacity
     * @param antialisingSize
     * @throws GLException
     */
    private void drawLayerFBOinPingPongFBO(Symbolizer symbolizer,
            double opacity, int antialisingSize) throws GLException {

        // System.err.println("FBO opacity = " + this.fboOpacity);
        // System.err.println("FBO primitive opacity = "
        // + this.fboPrimitive.getOverallOpacity());
        // System.err.println("primitive = " + this.fboPrimitive);
        GeoxygeneBlendingMode blendingMode = GeoxygeneBlendingFactory
                .getGeoxygeneBlendingMode(symbolizer.getBlendingMode(),
                        symbolizer.getFilter(), this.getLayerViewPanel());
        GLProgram program = blendingMode.getProgram();
        Subshader symbolizerFilterSubshader = this
                .getFilterSubshader(symbolizer);
        symbolizerFilterSubshader.setUniforms(program);
        this.getLayerViewPanel().drawInPingPong(program);

        GL30.glBindVertexArray(LayerViewGLPanel.getScreenQuad().getVaoId());
        // program.setUniform1f(
        // LayerViewGLPanel.objectOpacityUniformVarName,
        // (float) primitive.getOverallOpacity());
        // program
        // .setUniform1f(LayerViewGLPanel.globalOpacityUniformVarName,
        // (float) opacity);
        // System.err.println("render FBO quad with object opacity = "
        // + primitive.getOverallOpacity() + " & globalOpacity = "
        // + opacity);
        program.setUniform1f(LayerViewGLPanel.objectOpacityUniformVarName, 1f);
        program.setUniform1f(LayerViewGLPanel.globalOpacityUniformVarName,
                (float) (opacity));

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        GLTools.glCheckError("before FBO drawing textured quad");
        LwjglLayerRenderer.drawComplex(LayerViewGLPanel.getScreenQuad());
        // this.getScreenQuad().setColor(new Color(1f, 1f, 1f, .5f));
        GLTools.glCheckError("FBO drawing textured quad");

        GL30.glBindVertexArray(0); // unbind VAO
        GLTools.glCheckError("exiting FBO rendering");
        glBindTexture(GL_TEXTURE_2D, 0); // unbind texture

    }

    private Subshader getFilterSubshader(Symbolizer symbolizer) {
        Subshader subshader = this.subshaders.get(symbolizer);
        if (subshader == null) {
            subshader = ShaderFactory
                    .createFilterShader(symbolizer.getFilter());
            this.subshaders.put(symbolizer, subshader);
        }
        return subshader;
    }

    public final int getCanvasWidth() {
        if (this.getLayerViewPanel() != null) {
            return this.getLayerViewPanel().getCanvasWidth();
        }
        return 0;
    }

    public final int getCanvasHeight() {
        if (this.getLayerViewPanel() != null) {
            return this.getLayerViewPanel().getCanvasHeight();
        }
        return 0;
    }

    /**
     * @param primitive
     * @throws GLException
     */
    private final void renderGLPrimitiveWireframe(GLComplex primitive)
            throws GLException {
        glEnable(GL_BLEND);
        if (this.getLayerViewPanel().getAntialiasingSize() > 0) {
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
        this.wireframeRendering(primitive, DEFAULT_LINE_WIDTH,
                DEFAULT_POINT_SIZE);
    }

    /**
     * @param primitive
     * @throws GLException
     */
    private final void wireframeRendering(GLComplex primitive, float lineWidth,
            float pointSize) throws GLException {
        // Viewport viewport = this.getLayerViewPanel().getViewport();
        this.getGlContext().setCurrentProgram(
                LayerViewGLPanel.worldspaceColorProgramName);
        this.setGLViewMatrix(primitive.getMinX(), primitive.getMinY());
        GL30.glBindVertexArray(primitive.getVaoId());
        glDisable(GL_TEXTURE_2D); // if not set to disable, line smoothing won't
        // work
        GL11.glLineWidth(lineWidth);
        GL11.glPointSize(pointSize);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        LwjglLayerRenderer.drawComplex(primitive);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void initializeRendering() {
        this.previousRenderer = null;
    }

    @Override
    public void finalizeRendering() throws RenderingException {
        int antialisingSize = this.getLayerViewPanel().getAntialiasingSize() + 1;
        if (this.previousRenderer != null) {
            this.previousRenderer.switchRenderer();
            this.previousRenderer = null;
        }
        // draw the last FBO layer into Ping-Pong FBO
        if (this.fboSymbolizer != null) {
            try {
                this.drawLayerFBOinPingPongFBO(this.fboSymbolizer,
                        this.fboOpacity, antialisingSize);
            } catch (GLException e) {
                throw new RenderingException(e);
            }
            this.fboSymbolizer = null;

        }
    }

    private void clearCurrentPingPongFBO() throws GLException {
        GLTools.glCheckError("preparing next FBO Layer");
        this.getLayerViewPanel().clearCurrentPingPongFBO();
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
    private void clearFBOLayer(GLComplex primitive, double opacity,
            Symbolizer currentSymbolizer) throws GLException {
        GLTools.glCheckError("preparing next FBO Layer");
        // prepare next FBO
        this.fboSymbolizer = currentSymbolizer;
        this.fboOpacity = opacity;
        this.fboPrimitive = primitive;

        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this
                .getLayerViewPanel().getFboId());
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
        GL14.glBlendFuncSeparate(GL11.GL_ONE, GL11.GL_ZERO, GL11.GL_ZERO,
                GL11.GL_ZERO);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
        GLTools.glCheckError("finalizing FBO initialization");
    }

    public boolean getFBORendering() {
        return this.fboRendering;
    }

    /**
     * @param fboRendering
     *            the fboRendering to set
     */
    public final void setFBORendering(boolean fboRendering) {
        this.fboRendering = fboRendering;
    }

    /**
     * Get the displayable associated with the couple feature-symbolizer
     * 
     * @param feature
     * @param symbolizer
     * @return
     */
    private GLDisplayable getDisplayable(IFeature feature, Symbolizer symbolizer) {
        Map<Symbolizer, GLDisplayable> featureDisplayables = this.displayables
                .get(feature);
        if (featureDisplayables == null) {
            return null;
        }
        return featureDisplayables.get(symbolizer);
    }

    @Override
    public void reset() {
        synchronized (this.displayables) {
            this.displayables.clear();
        }

        // REF1
        // for (GeoxComplexRenderer renderer : this.renderers.values()) {
        // renderer.reset();
        // }
        TextureManager.getInstance().clearCache();
        GLTextureManager.getInstance().clearCache();
        // this.needInitialization = true;
    }

    /**
     * Set the GL uniform view matrix (stored in viewMatrixLocation) using a
     * viewport
     * 
     * @throws GLException
     */
    public final boolean setGLViewMatrix(final double minX, final double minY)
            throws GLException {
        final Viewport viewport = this.getLayerViewPanel().getViewport();
        AffineTransform modelToViewTransform = null;

        try {
            modelToViewTransform = viewport.getModelToViewTransform();
        } catch (NoninvertibleTransformException e1) {
            logger.error("Non invertible viewport matrix");
            return false;
        }

        GLProgram program = this.getGlContext().getCurrentProgram();
        if (program == null) {
            logger.error("setting GL view matrix with no current program. Exiting.");
            return false;
        }
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(
                LayerViewGLPanel.m00ModelToViewMatrixUniformVarName,
                (float) (modelToViewTransform.getScaleX()));
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(
                LayerViewGLPanel.m02ModelToViewMatrixUniformVarName,
                (float) (modelToViewTransform.getTranslateX() + minX
                        * modelToViewTransform.getScaleX()));
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(
                LayerViewGLPanel.m11ModelToViewMatrixUniformVarName,
                (float) (modelToViewTransform.getScaleY()));
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(
                LayerViewGLPanel.m12ModelToViewMatrixUniformVarName,
                (float) (modelToViewTransform.getTranslateY() + minY
                        * modelToViewTransform.getScaleY()));
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(LayerViewGLPanel.screenWidthUniformVarName, this
                .getLayerViewPanel().getCanvasWidth());
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(LayerViewGLPanel.screenHeightUniformVarName, this
                .getLayerViewPanel().getCanvasHeight());
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");

        // System.err.println("translation x = "
        // + (float) (modelToViewTransform.getTranslateX()) + " y = "
        // + (modelToViewTransform.getTranslateY()));
        // System.err.println("min x = " + (float) (minX) + " y = " + (minY));
        // System.err.println("scaling     x = "
        // + (float) (modelToViewTransform.getScaleX()) + " y = "
        // + (modelToViewTransform.getScaleY()));
        // System.err.println("canvas width = " + width + " height = " +
        // height);
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        return true;
    }

    private final void addDisplayable(IFeature feature, Symbolizer symbolizer,
            GLDisplayable displayable) {
        Map<Symbolizer, GLDisplayable> featureDisplayables = this.displayables
                .get(feature);
        if (featureDisplayables == null) {
            featureDisplayables = new HashMap<Symbolizer, GLDisplayable>();
            this.displayables.put(feature, featureDisplayables);
        }
        if (featureDisplayables.put(symbolizer, displayable) != displayable) {
            displayable.addTaskListener(this);
            // task is automatically started when added to the manager
            GeOxygeneEventManager.getInstance().getApplication()
                    .getTaskManager().addTask(displayable);
        }
    }

    private final void removeDisplayable(IFeature feature) {
        Map<Symbolizer, GLDisplayable> featureDisplayables = this.displayables
                .remove(feature);
        if (featureDisplayables != null) {
            for (GLDisplayable displayable : featureDisplayables.values()) {
                displayable.removeTaskListener(this);
            }
        }
    }

    @Override
    public final void onStateChange(GLDisplayable displayable,
            TaskState oldState) {
        if (!displayable.getState().isRunning()) {
            GeOxygeneEventManager.getInstance().getApplication().getMainFrame()
                    .getCurrentDesktop().repaint();
        }
    }

    /**
     * do a GL draw call for all complex meshes
     * 
     * @param primitive
     *            primitive to render
     */
    public static void drawComplex(GLComplex primitive) {
        RenderingStatistics.drawGLComplex(primitive);
        for (GLMesh mesh : primitive.getMeshes()) {
            RenderingStatistics.doDrawCall();
            // System.err.println("draw call for mesh " + mesh +
            // " indices from "
            // + mesh.getFirstIndex() + " to " + mesh.getLastIndex());
            GL11.glDrawElements(mesh.getGlType(),
                    mesh.getLastIndex() - mesh.getFirstIndex() + 1,
                    GL11.GL_UNSIGNED_INT, mesh.getFirstIndex()
                            * (Integer.SIZE / 8));
        }
    }

}
