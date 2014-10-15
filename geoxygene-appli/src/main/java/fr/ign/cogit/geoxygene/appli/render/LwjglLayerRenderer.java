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
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.Util;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneEventManager;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLSimpleComplex;
import fr.ign.cogit.geoxygene.appli.gl.GLTextureManager;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.mode.RenderingTypeMode;
import fr.ign.cogit.geoxygene.appli.render.primitive.DisplayableCurve;
import fr.ign.cogit.geoxygene.appli.render.primitive.DisplayablePoint;
import fr.ign.cogit.geoxygene.appli.render.primitive.DisplayableSurface;
import fr.ign.cogit.geoxygene.appli.render.primitive.GLDisplayable;
import fr.ign.cogit.geoxygene.appli.render.primitive.GeoxComplexRenderer;
import fr.ign.cogit.geoxygene.appli.render.primitive.GeoxComplexRendererBasic;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
import fr.ign.cogit.geoxygene.appli.task.TaskListener;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.util.Pair;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLContext;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleVertex;
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

    private static final int COLORTEXTURE1_SLOT = 0;
    protected EventListenerList listenerList = new EventListenerList();
    private GLSimpleComplex screenQuad = null;
    // private final Map<String, PrimitiveRenderer> renderers = new
    // HashMap<String, PrimitiveRenderer>();
    // private PrimitiveRenderer defaultRenderer = null;
    // private final DensityFieldPrimitiveRenderer densityFieldPrimitiveRenderer
    // = new DensityFieldPrimitiveRenderer();

    // REF1
    // private final Map<IFeature, GeoxComplexRenderer> renderers = new
    // HashMap<IFeature, GeoxComplexRenderer>();
    private LayerViewGLPanel layerViewPanel = null;
    private final Map<IFeature, Map<Symbolizer, GLDisplayable>> displayables = new HashMap<IFeature, Map<Symbolizer, GLDisplayable>>();
    private GeoxComplexRendererBasic partialRenderer = null;
    private int fboId = -1;
    private int fboTextureId = -1;
    private int previousFBOImageWidth = -1;
    private int previousFBOImageHeight = -1;
    private boolean fboRendering = false;
    // private boolean needInitialization = false;
    private Symbolizer fboSymbolizer = null;
    private GLComplex fboPrimitive = null;
    private double fboOpacity = 1.;

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
        this.partialRenderer = new GeoxComplexRendererBasic(this, null);
        this.initializeScreenQuad();
    }

    /**
     * @param feature
     * @return the partialRenderer
     */
    public GeoxComplexRendererBasic getPartialRenderer(IFeature feature) {
        return this.partialRenderer;
    }

    /**
     * get the GL context from the layer view panel
     */
    public GLContext getGLContext() throws GLException {
        return this.layerViewPanel.getGlContext();
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
                    LwjglLayerRenderer.this.initializeRendering();
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
                        logger.warn("Open GL Error message = "
                                + Util.translateGLErrorString(GL11.glGetError()));
                        t.printStackTrace();
                        return;
                    }
                } finally {
                    try {
                        LwjglLayerRenderer.this.finalizeRendering();
                    } catch (RenderingException e) {
                        logger.error("An error ocurred finalizing "
                                + this.getClass().getSimpleName());
                    }
                    // we are no more in rendering progress
                    LwjglLayerRenderer.this.setRendering(false);
                    // FIXME Is this operation really useful or is it a patch?
                    vp.getRenderingManager().repaint();
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

    private void render(final Symbolizer symbolizer, final IFeature feature,
            final Layer layer) throws RenderingException {

        Viewport viewport = this.getLayerViewPanel().getViewport();

        GLDisplayable displayable = this.getDisplayable(symbolizer, feature,
                layer, viewport);

        if (displayable != null) {
            try {
                double layerOpacity = this.getLayer().getOpacity();
                Collection<GLComplex> fullRepresentation = displayable
                        .getFullRepresentation();
                if (fullRepresentation == null) {
                    GeoxComplexRendererBasic partialRenderer = this
                            .getPartialRenderer(feature);
                    partialRenderer.setFBORendering(false);
                    partialRenderer.localRendering(
                            displayable.getPartialRepresentation(),
                            layerOpacity);
                } else {
                    for (GLComplex complex : fullRepresentation) {
                        this.renderGLPrimitive(complex, layerOpacity);
                    }
                }
            } catch (GLException e) {
                throw new RenderingException(e);
            }
        } else {
            logger.warn(this.getClass().getSimpleName()
                    + " do not know how to render feature "
                    + feature.getGeom().getClass().getSimpleName());
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
            final IFeature feature, final Layer layer, Viewport viewport) {
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
                displayable = this.generateDisplayable(feature, layer,
                        symbolizer, viewport, displayable, geometry);
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

    /**
     * @return the fboId
     */
    public int getFboId() {
        return this.fboId;
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
                .getMainFrame().getMode().getCurrentMode().getRenderingType() != RenderingTypeMode.FINAL;
        // System.err.println("rendering primitive "
        // + primitive.getMeshes().size() + " meshes");
        GeoxComplexRenderer renderer = (GeoxComplexRenderer) primitive
                .getRenderer();
        if (renderer == null) {
            logger.error("No renderer associated with full representation a feature in layer "
                    + this.getLayer().getName());
        }

        if (this.getLayerViewPanel().useFBO() && !quickRendering) {
            this.setFBORendering(true);
            renderer.setFBORendering(true);
            this.fboRendering(primitive, renderer, opacity);
        } else {
            renderer.setFBORendering(false);
            this.setFBORendering(false);
            renderer.render(primitive, opacity);
        }
    }

    public final int getFBOImageWidth() {
        if (this.getFBORendering()) {
            int antialisingSize = this.getLayerViewPanel()
                    .getAntialiasingSize() + 1;

            return antialisingSize * this.getCanvasWidth();
        } else {
            return this.getCanvasWidth();
        }
    }

    public final int getFBOImageHeight() {
        if (this.getFBORendering()) {
            int antialisingSize = this.getLayerViewPanel()
                    .getAntialiasingSize() + 1;
            return antialisingSize * this.getCanvasHeight();
        } else {
            return this.getCanvasHeight();
        }
    }

    public final void invalidateFBO() {
        this.previousFBOImageWidth = -1;
        this.previousFBOImageHeight = -1;
    }

    /**
     * Render a GL Primitive into a FBO
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
                this.drawFBO(
                        this.fboOpacity * this.fboPrimitive.getOverallOpacity(),
                        antialisingSize);
            }
            this.initializeFBO(primitive, opacity, currentSymbolizer);
        }
        GL11.glDepthMask(false);
        glDisable(GL11.GL_DEPTH_TEST);

        GL11.glViewport(0, 0, this.getFBOImageWidth(), this.getFBOImageHeight());
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        GLTools.glCheckError("after setting draw buffer");

        glEnable(GL_TEXTURE_2D);
        // display the layer

        renderer.setFBORendering(true);
        renderer.render(primitive, 1f);
        renderer.setFBORendering(false);

    }

    /**
     * @param primitive
     * @param opacity
     * @param antialisingSize
     * @throws GLException
     */
    private void drawFBO(double opacity, int antialisingSize)
            throws GLException {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        GLProgram program = this.getGlContext().setCurrentProgram(
                LayerViewGLPanel.screenspaceAntialiasedTextureProgramName);
        // System.err.println("FBO opacity = " + this.fboOpacity);
        // System.err.println("FBO primitive opacity = "
        // + this.fboPrimitive.getOverallOpacity());
        // System.err.println("primitive = " + this.fboPrimitive);
        GLTools.glCheckError("FBO plain rendering");
        GL11.glViewport(0, 0, this.getCanvasWidth(), this.getCanvasHeight());
        GL11.glDrawBuffer(GL11.GL_BACK);
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL11.GL_POLYGON_SMOOTH);

        GLTools.glCheckError("FBO bind color texture");
        // System.err.println("Current program = "
        // + GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM));
        // System.err.println("set color texture 1 to " + COLORTEXTURE1_SLOT
        // + " in program " + program.getName() + " ("
        // + program.getProgramId() + ")");
        // for (String uniform : program.getUniformNames()) {
        // System.err.println("\t- " + uniform);
        // }
        program.setUniform1i(LayerViewGLPanel.colorTexture1UniformVarName,
                COLORTEXTURE1_SLOT);
        GLTools.glCheckError("FBO bind antialiasing");
        program.setUniform1i(LayerViewGLPanel.antialiasingSizeUniformVarName,
                antialisingSize);
        GLTools.glCheckError("FBO activate texture");
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + COLORTEXTURE1_SLOT);
        GLTools.glCheckError("FBO bound texture");
        glBindTexture(GL_TEXTURE_2D, this.getFBOTextureId());
        GLTools.glCheckError("FBO bound texture");

        GL11.glDepthMask(false);
        glDisable(GL11.GL_DEPTH_TEST);

        GL30.glBindVertexArray(this.getScreenQuad().getVaoId());
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
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR);
        //
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        GLTools.glCheckError("before FBO drawing textured quad");
        this.getScreenQuad().setOverallOpacity(1.);
        GLTools.drawComplex(this.getScreenQuad());
        // this.getScreenQuad().setColor(new Color(1f, 1f, 1f, .5f));
        GLTools.glCheckError("FBO drawing textured quad");

        GL30.glBindVertexArray(0); // unbind VAO
        GLTools.glCheckError("exiting FBO rendering");
        glBindTexture(GL_TEXTURE_2D, 0); // unbind texture
    }

    private int getFBOTextureId() {
        // System.err.println("get FBO texture ID : " + this.fboTextureId);
        if (this.fboTextureId == -1) {
            this.fboTextureId = GL11.glGenTextures();
            // System.err.println("generated FBO texture ID : "
            // + this.fboTextureId);
            if (this.fboTextureId < 0) {
                logger.error("Unable to use FBO texture");
            }
        }
        return this.fboTextureId;
    }

    public final int getCanvasWidth() {
        if (this.getLayerViewPanel() != null) {
            return this.getLayerViewPanel().getWidth();
        }
        return 0;
    }

    public final int getCanvasHeight() {
        if (this.getLayerViewPanel() != null) {
            return this.getLayerViewPanel().getHeight();
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
        GLTools.drawComplex(primitive);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void initializeRendering() {
        if (this.fboId == -1) {
            // generate an ID for the FBO
            this.fboId = glGenFramebuffers();
            if (this.fboId < 0) {
                logger.error("Unable to create frame buffer for FBO rendering");
            }

        }
    }

    @Override
    public void finalizeRendering() throws RenderingException {
        int antialisingSize = this.getLayerViewPanel().getAntialiasingSize() + 1;
        if (this.fboSymbolizer != null) {
            try {

                this.drawFBO(
                        this.fboOpacity * this.fboPrimitive.getOverallOpacity(),
                        antialisingSize);
            } catch (GLException e) {
                throw new RenderingException(e);
            }
            this.fboSymbolizer = null;
        }
    }

    /**
     * @param primitive
     * @param opacity
     * @param currentSymbolizer
     * @throws GLException
     */
    private void initializeFBO(GLComplex primitive, double opacity,
            Symbolizer currentSymbolizer) throws GLException {
        // prepare next FBO
        this.fboSymbolizer = currentSymbolizer;
        this.fboOpacity = opacity;
        this.fboPrimitive = primitive;
        // render primitive in a FBO (offscreen rendering)
        // bind a read-only framebuffer
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.getFboId());
        // this.setFBORendering(true);
        // check if the screen size has change since previous rendering

        int fboImageWidth = this.getFBOImageWidth();
        int fboImageHeight = this.getFBOImageHeight();
        if (this.previousFBOImageWidth != fboImageWidth
                || this.previousFBOImageHeight != fboImageHeight) {
            this.previousFBOImageWidth = fboImageWidth;
            this.previousFBOImageHeight = fboImageHeight;

            glBindTexture(GL_TEXTURE_2D, this.getFBOTextureId());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA8, fboImageWidth,
                    fboImageHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
                    (ByteBuffer) null);
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER,
                    GL30.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D,
                    this.getFBOTextureId(), 0);
            GLTools.glCheckError("FBO size modification");
            GL11.glViewport(0, 0, fboImageWidth, fboImageHeight);

            // check FBO status

            int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
            if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
                throw new GLException(
                        "Frame Buffer Object is not correctly initialized");
            }
            GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
            GLTools.glCheckError("after setting draw buffer");

            glEnable(GL_TEXTURE_2D);

            // System.err.println("fbo image size = " + fboImageWidth + "x"
            // + fboImageHeight + " " + this.getFBORendering());
            // System.err.println("antialiasing size = "
            // + this.getLayerViewPanel().getAntialiasingSize());
            GLTools.glCheckError("after setting viewport");

            GLTools.glCheckError("FBO initialization");
        }
        glDisable(GL11.GL_BLEND);
        GLTools.glCheckError("just before launching normal rendering within FBO context");

        GL11.glClearColor(0f, 0f, 0f, 0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
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
        float width = this.getCanvasWidth();
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        float height = this.getCanvasHeight();
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(LayerViewGLPanel.screenWidthUniformVarName, width);
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(LayerViewGLPanel.screenHeightUniformVarName,
                height);
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

    /**
     * @param feature
     * @param layer
     * @param symbolizer
     * @param viewport
     * @param displayable
     * @param geometry
     * @return
     */
    private GLDisplayable generateDisplayable(final IFeature feature,
            final Layer layer, final Symbolizer symbolizer,
            final Viewport viewport, GLDisplayable displayable,
            IGeometry geometry) {
        if (geometry == null) {
            logger.warn("null geometry for feature " + feature.getId());
            return null;
        } else if (geometry.isPolygon()) {
            DisplayableSurface displayablePolygon = new DisplayableSurface(
                    layer.getName() + "-polygon #" + feature.getId(), viewport,
                    (IPolygon) geometry, feature, symbolizer, this,
                    this.getPartialRenderer(feature));
            displayable = displayablePolygon;
        } else if (geometry.isMultiSurface()) {
            DisplayableSurface displayablePolygon = new DisplayableSurface(
                    layer.getName() + "-multisurface #" + feature.getId(),
                    viewport, (IMultiSurface<?>) geometry, feature, symbolizer,
                    this, this.getPartialRenderer(feature));
            displayable = displayablePolygon;
        } else if (geometry.isMultiCurve()) {
            DisplayableCurve displayableCurve = new DisplayableCurve(
                    layer.getName() + "-multicurve #" + feature.getId(),
                    viewport, (IMultiCurve<?>) geometry, symbolizer, this,
                    this.getPartialRenderer(feature));
            displayable = displayableCurve;
        } else if (geometry.isLineString()) {
            DisplayableCurve displayableLine = new DisplayableCurve(
                    layer.getName() + "-linestring #" + feature.getId(),
                    viewport, (ILineString) geometry, symbolizer, this,
                    this.getPartialRenderer(feature));
            displayable = displayableLine;
        } else if (geometry.isPoint() || (geometry instanceof IMultiPoint)) {
            DisplayablePoint displayablePoint = new DisplayablePoint(
                    layer.getName() + "-multipoint #" + feature.getId(),
                    viewport, geometry, symbolizer, this,
                    this.getPartialRenderer(feature));
            displayable = displayablePoint;
        } else {
            logger.warn("LwjglLayerRenderer cannot handle geometry type "
                    + geometry.getClass().getSimpleName());
        }
        return displayable;
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

    /**
     * @return the screenQuad
     */
    public final GLSimpleComplex getScreenQuad() {
        if (this.screenQuad == null) {
            this.initializeScreenQuad();
        }
        return this.screenQuad;
    }

    /**
     * 
     */
    private final void initializeScreenQuad() {
        this.screenQuad = new GLSimpleComplex("screen", 0f, 0f);
        GLMesh mesh = this.screenQuad.addGLMesh(GL11.GL_QUADS);
        mesh.addIndex(this.screenQuad.addVertex(new GLSimpleVertex(
                new Point2D.Double(-1, -1), new Point2D.Double(0, 0))));
        mesh.addIndex(this.screenQuad.addVertex(new GLSimpleVertex(
                new Point2D.Double(-1, 1), new Point2D.Double(0, 1))));
        mesh.addIndex(this.screenQuad.addVertex(new GLSimpleVertex(
                new Point2D.Double(1, 1), new Point2D.Double(1, 1))));
        mesh.addIndex(this.screenQuad.addVertex(new GLSimpleVertex(
                new Point2D.Double(1, -1), new Point2D.Double(1, 0))));
        this.screenQuad.setColor(Color.blue);
        this.screenQuad.setOverallOpacity(0.5);
    }

    @Override
    public final void onStateChange(GLDisplayable displayable,
            TaskState oldState) {
        if (!displayable.getState().isRunning()) {
            GeOxygeneEventManager.getInstance().getApplication().getMainFrame()
                    .getCurrentDesktop().repaint();
        }
    }

}
