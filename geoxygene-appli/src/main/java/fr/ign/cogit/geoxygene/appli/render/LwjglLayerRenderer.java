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

import test.app.GLPaintingVertex;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.render.primitive.FeatureRenderer;
import fr.ign.cogit.geoxygene.appli.render.primitive.GL4FeatureRenderer;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.util.Pair;
import fr.ign.cogit.geoxygene.util.gl.GLContext;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleVertex;
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
        super(theLayer, theLayerViewPanel);
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
        return (LayerViewGLPanel) super.getLayerViewPanel();
    }

    private final FeatureRenderer getRenderer(Symbolizer symbolizer,
            IFeature feature) {
        if (this.gl4Renderer == null) {
            try {
                this.gl4Renderer = new GL4FeatureRenderer(this,
                        LwjglLayerRenderer.getGL4Context());
            } catch (GLException e) {
                logger.error("impossible to generate a valid GL4 Context");
                e.printStackTrace();
            }
        }
        return this.gl4Renderer;
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
                // try {
                // // System.out.println("isCurrent = " + Display.isCurrent() +
                // " " + Display.isActive() + " " + Display.isCreated() + " "
                // } catch (LWJGLException e1) {
                // Log.warn("GL display is not ready. Rendering is aborted. " +
                // e1.getMessage());
                // return;
                // }
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
                        LwjglLayerRenderer.getGL4Context().initializeContext();
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
        this.getRenderer(symbolizer, feature).render(feature, layer,
                symbolizer, viewport);
    }

    @Override
    public void reset() {
        this.gl4Renderer.reset();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static GLContext glContext = null;

    public static final String m00ModelToViewMatrixUniformVarName = "m00";
    public static final String m02ModelToViewMatrixUniformVarName = "m02";
    public static final String m11ModelToViewMatrixUniformVarName = "m11";
    public static final String m12ModelToViewMatrixUniformVarName = "m12";
    public static final String screenWidthUniformVarName = "screenWidth";
    public static final String screenHeightUniformVarName = "screenHeight";
    public static final String globalOpacityUniformVarName = "globalOpacity";
    public static final String objectOpacityUniformVarName = "objectOpacity";
    public static final String colorTexture1UniformVarName = "colorTexture1";
    public static final String textureScaleFactorUniformVarName = "textureScaleFactor";
    public static final String antialiasingSizeUniformVarName = "antialiasingSize";

    public static final String paperTextureUniformVarName = "paperSampler";
    public static final String brushTextureUniformVarName = "brushSampler";
    public static final String brushWidthUniformVarName = "brushWidth";
    public static final String brushHeightUniformVarName = "brushHeight";
    public static final String brushStartWidthUniformVarName = "brushStartWidth";
    public static final String brushEndWidthUniformVarName = "brushEndWidth";
    // width of one brush pixel (mm)
    public static final String brushScaleUniformVarName = "brushScale";
    public static final String paperScaleUniformVarName = "paperScale";
    public static final String paperDensityUniformVarName = "paperDensity";
    public static final String brushDensityUniformVarName = "brushDensity";
    public static final String strokePressureUniformVarName = "strokePressure";
    public static final String sharpnessUniformVarName = "sharpness";
    public static final String strokePressureVariationAmplitudeUniformVarName = "pressureVariationAmplitude";
    public static final String strokePressureVariationWavelengthUniformVarName = "pressureVariationWavelength";
    public static final String strokeShiftVariationAmplitudeUniformVarName = "shiftVariationAmplitude";
    public static final String strokeShiftVariationWavelengthUniformVarName = "shiftVariationWavelength";
    public static final String strokeThicknessVariationAmplitudeUniformVarName = "thicknessVariationAmplitude";
    public static final String strokeThicknessVariationWavelengthUniformVarName = "thicknessVariationWavelength";

    public static final String basicProgramName = "Basic";
    public static final String linePaintingProgramName = "LinePainting";
    public static final String worldspaceColorProgramName = "WorldspaceColor";
    public static final String worldspaceTextureProgramName = "WorldspaceTexture";
    public static final String screenspaceColorProgramName = "ScreenspaceColor";
    public static final String screenspaceTextureProgramName = "ScreenspaceTexture";
    public static final String backgroundProgramName = "BackgroundTexture";
    public static final String screenspaceAntialiasedTextureProgramName = "ScreenspaceAntialiasedTexture";

    /**
     * This static method creates one GLContext containing all programs used to
     * render GeOxygene graphics elements
     * 
     * @return
     * @throws GLException
     */
    public static GLContext getGL4Context() throws GLException {
        if (glContext == null) {
            glContext = new GLContext();

            int worldspaceVertexShader = GLProgram
                    .createVertexShader("./src/main/resources/shaders/worldspace.vert.glsl");
            int screenspaceVertexShader = GLProgram
                    .createVertexShader("./src/main/resources/shaders/screenspace.vert.glsl");
            glContext.addProgram(createBasicProgram());

            glContext
                    .addProgram(createWorldspaceColorProgram(worldspaceVertexShader));
            glContext
                    .addProgram(createWorldspaceTextureProgram(worldspaceVertexShader));

            glContext
                    .addProgram(createScreenspaceColorProgram(screenspaceVertexShader));
            glContext
                    .addProgram(createScreenspaceTextureProgram(screenspaceVertexShader));
            glContext
                    .addProgram(createScreenspaceAntialiasedProgram(screenspaceVertexShader));

            // line painting
            int paintVertexShader = GLProgram
                    .createVertexShader("./src/main/resources/shaders/line.vert.glsl");
            int paintFragmentShader = GLProgram
                    .createFragmentShader("./src/main/resources/shaders/line.frag.glsl");
            glContext.addProgram(createPaintProgram(paintVertexShader,
                    paintFragmentShader));
            // background paper
            glContext.addProgram(createBackgroundTextureProgram());

        }
        return glContext;
    }

    /**
     * line painting program
     */
    private static GLProgram createPaintProgram(int basicVertexShader,
            int basicFragmentShader) throws GLException {
        // basic program
        GLProgram paintProgram = new GLProgram(linePaintingProgramName);
        paintProgram.setVertexShader(basicVertexShader);
        paintProgram.setFragmentShader(basicFragmentShader);
        paintProgram.addInputLocation(
                GLPaintingVertex.vertexPositionVariableName,
                GLPaintingVertex.vertexPositionLocation);
        paintProgram.addInputLocation(GLPaintingVertex.vertexUVVariableName,
                GLPaintingVertex.vertexUVLocation);
        paintProgram.addInputLocation(
                GLPaintingVertex.vertexNormalVariableName,
                GLPaintingVertex.vertexNormalLocation);
        paintProgram.addInputLocation(
                GLPaintingVertex.vertexCurvatureVariableName,
                GLPaintingVertex.vertexCurvatureLocation);
        paintProgram.addInputLocation(
                GLPaintingVertex.vertexThicknessVariableName,
                GLPaintingVertex.vertexThicknessLocation);
        paintProgram.addInputLocation(GLPaintingVertex.vertexColorVariableName,
                GLPaintingVertex.vertexColorLocation);
        paintProgram.addInputLocation(GLPaintingVertex.vertexMaxUVariableName,
                GLPaintingVertex.vertexMaxULocation);
        paintProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m02ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m11ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m12ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(screenWidthUniformVarName);
        paintProgram.addUniform(screenHeightUniformVarName);
        paintProgram.addUniform(paperTextureUniformVarName);
        paintProgram.addUniform(brushTextureUniformVarName);
        paintProgram.addUniform(brushWidthUniformVarName);
        paintProgram.addUniform(brushHeightUniformVarName);
        paintProgram.addUniform(brushStartWidthUniformVarName);
        paintProgram.addUniform(brushEndWidthUniformVarName);
        paintProgram.addUniform(brushScaleUniformVarName);
        paintProgram.addUniform(paperScaleUniformVarName);
        paintProgram.addUniform(paperDensityUniformVarName);
        paintProgram.addUniform(brushDensityUniformVarName);
        paintProgram.addUniform(strokePressureUniformVarName);
        paintProgram.addUniform(sharpnessUniformVarName);
        paintProgram.addUniform(strokePressureVariationAmplitudeUniformVarName);
        paintProgram
                .addUniform(strokePressureVariationWavelengthUniformVarName);
        paintProgram.addUniform(strokeShiftVariationAmplitudeUniformVarName);
        paintProgram.addUniform(strokeShiftVariationWavelengthUniformVarName);
        paintProgram
                .addUniform(strokeThicknessVariationAmplitudeUniformVarName);
        paintProgram
                .addUniform(strokeThicknessVariationWavelengthUniformVarName);
        paintProgram.addUniform(globalOpacityUniformVarName);
        paintProgram.addUniform(objectOpacityUniformVarName);
        paintProgram.addUniform(textureScaleFactorUniformVarName);

        return paintProgram;
    }

    /**
     * @throws GLException
     */
    private static GLProgram createBasicProgram() throws GLException {
        int basicVertexShader = GLProgram
                .createVertexShader("./src/main/resources/shaders/basic.vert.glsl");
        int basicFragmentShader = GLProgram
                .createFragmentShader("./src/main/resources/shaders/basic.frag.glsl");
        // basic program
        GLProgram basicProgram = new GLProgram(basicProgramName);
        basicProgram.setVertexShader(basicVertexShader);
        basicProgram.setFragmentShader(basicFragmentShader);
        basicProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        basicProgram.addInputLocation(GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        basicProgram.addInputLocation(GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        return basicProgram;
    }

    /**
     * @throws GLException
     */
    private static GLProgram createScreenspaceColorProgram(
            int screenspaceVertexShader) throws GLException {
        int screenspaceFragmentShader = GLProgram
                .createFragmentShader("./src/main/resources/shaders/polygon.color.frag.glsl");
        // basic program
        GLProgram screenspaceColorProgram = new GLProgram(
                screenspaceColorProgramName);
        screenspaceColorProgram.setVertexShader(screenspaceVertexShader);
        screenspaceColorProgram.setFragmentShader(screenspaceFragmentShader);
        screenspaceColorProgram.addInputLocation(
                GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        screenspaceColorProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        screenspaceColorProgram.addInputLocation(
                GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        screenspaceColorProgram.addUniform(globalOpacityUniformVarName);
        screenspaceColorProgram.addUniform(objectOpacityUniformVarName);

        return screenspaceColorProgram;
    }

    /**
     * @throws GLException
     */
    private static GLProgram createScreenspaceTextureProgram(
            int screenspaceVertexShader) throws GLException {
        int screenspaceFragmentShader = GLProgram
                .createFragmentShader("./src/main/resources/shaders/polygon.texture.frag.glsl");
        // basic program
        GLProgram screenspaceTextureProgram = new GLProgram(
                screenspaceTextureProgramName);
        screenspaceTextureProgram.setVertexShader(screenspaceVertexShader);
        screenspaceTextureProgram.setFragmentShader(screenspaceFragmentShader);
        screenspaceTextureProgram.addInputLocation(
                GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        screenspaceTextureProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        screenspaceTextureProgram.addInputLocation(
                GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        screenspaceTextureProgram.addUniform(globalOpacityUniformVarName);
        screenspaceTextureProgram.addUniform(objectOpacityUniformVarName);
        screenspaceTextureProgram.addUniform(colorTexture1UniformVarName);
        screenspaceTextureProgram.addUniform(textureScaleFactorUniformVarName);
        return screenspaceTextureProgram;
    }

    /**
     * @throws GLException
     */
    private static GLProgram createBackgroundTextureProgram()
            throws GLException {
        int backgroundVertexShader = GLProgram
                .createVertexShader("./src/main/resources/shaders/bg.vert.glsl");
        int backgroundFragmentShader = GLProgram
                .createFragmentShader("./src/main/resources/shaders/bg.frag.glsl");
        // basic program
        GLProgram backgroundTextureProgram = new GLProgram(
                backgroundProgramName);
        backgroundTextureProgram.setVertexShader(backgroundVertexShader);
        backgroundTextureProgram.setFragmentShader(backgroundFragmentShader);
        backgroundTextureProgram.addInputLocation(
                GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        backgroundTextureProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        backgroundTextureProgram.addUniform(colorTexture1UniformVarName);
        backgroundTextureProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        backgroundTextureProgram.addUniform(m02ModelToViewMatrixUniformVarName);
        backgroundTextureProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        backgroundTextureProgram.addUniform(m11ModelToViewMatrixUniformVarName);
        backgroundTextureProgram.addUniform(m12ModelToViewMatrixUniformVarName);
        backgroundTextureProgram.addUniform(screenWidthUniformVarName);
        backgroundTextureProgram.addUniform(screenHeightUniformVarName);

        return backgroundTextureProgram;
    }

    /**
     * @param worldspaceVertexShader
     * @throws GLException
     */
    private static GLProgram createWorldspaceColorProgram(
            int worldspaceVertexShader) throws GLException {

        int colorFragmentShader = GLProgram
                .createFragmentShader("./src/main/resources/shaders/polygon.color.frag.glsl");
        // color program
        GLProgram colorProgram = new GLProgram(worldspaceColorProgramName);
        colorProgram.setVertexShader(worldspaceVertexShader);
        colorProgram.setFragmentShader(colorFragmentShader);
        colorProgram.addInputLocation(GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        colorProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        colorProgram.addInputLocation(GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        colorProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        colorProgram.addUniform(m02ModelToViewMatrixUniformVarName);
        colorProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        colorProgram.addUniform(m11ModelToViewMatrixUniformVarName);
        colorProgram.addUniform(m12ModelToViewMatrixUniformVarName);
        colorProgram.addUniform(screenWidthUniformVarName);
        colorProgram.addUniform(screenHeightUniformVarName);
        colorProgram.addUniform(globalOpacityUniformVarName);
        colorProgram.addUniform(objectOpacityUniformVarName);
        colorProgram.addUniform(colorTexture1UniformVarName);

        return colorProgram;
    }

    /**
     * @param worldspaceVertexShader
     * @throws GLException
     */
    private static GLProgram createWorldspaceTextureProgram(
            int worldspaceVertexShader) throws GLException {

        int textureFragmentShader = GLProgram
                .createFragmentShader("./src/main/resources/shaders/polygon.texture.frag.glsl");
        // color program
        GLProgram textureProgram = new GLProgram(worldspaceTextureProgramName);
        textureProgram.setVertexShader(worldspaceVertexShader);
        textureProgram.setFragmentShader(textureFragmentShader);
        textureProgram.addInputLocation(GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        textureProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        textureProgram.addInputLocation(GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        textureProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        textureProgram.addUniform(m02ModelToViewMatrixUniformVarName);
        textureProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        textureProgram.addUniform(m11ModelToViewMatrixUniformVarName);
        textureProgram.addUniform(m12ModelToViewMatrixUniformVarName);
        textureProgram.addUniform(screenWidthUniformVarName);
        textureProgram.addUniform(screenHeightUniformVarName);
        textureProgram.addUniform(globalOpacityUniformVarName);
        textureProgram.addUniform(objectOpacityUniformVarName);
        textureProgram.addUniform(colorTexture1UniformVarName);
        textureProgram.addUniform(textureScaleFactorUniformVarName);

        return textureProgram;
    }

    /**
     * @param worldspaceVertexShader
     * @throws GLException
     */
    private static GLProgram createScreenspaceAntialiasedProgram(
            int screenspaceVertexShader) throws GLException {

        int antialiasedFragmentShader = GLProgram
                .createFragmentShader("./src/main/resources/shaders/antialiased.frag.glsl");
        // color program
        GLProgram antialisedProgram = new GLProgram(
                screenspaceAntialiasedTextureProgramName);
        antialisedProgram.setVertexShader(screenspaceVertexShader);
        antialisedProgram.setFragmentShader(antialiasedFragmentShader);
        antialisedProgram.addInputLocation(GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        antialisedProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        antialisedProgram.addInputLocation(
                GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        antialisedProgram.addUniform(globalOpacityUniformVarName);
        antialisedProgram.addUniform(objectOpacityUniformVarName);
        antialisedProgram.addUniform(colorTexture1UniformVarName);
        antialisedProgram.addUniform(textureScaleFactorUniformVarName);
        antialisedProgram.addUniform(antialiasingSizeUniformVarName);

        return antialisedProgram;
    }

}
