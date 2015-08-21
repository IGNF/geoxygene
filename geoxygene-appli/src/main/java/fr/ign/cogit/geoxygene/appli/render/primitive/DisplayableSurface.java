/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.appli.render.primitive;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.lwjgl.opengl.GL11;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.BinaryGradientImage;
import fr.ign.cogit.geoxygene.appli.gl.BinaryGradientTexture;
import fr.ign.cogit.geoxygene.appli.gl.GLComplexFactory;
import fr.ign.cogit.geoxygene.appli.gl.GLSimpleComplex;
import fr.ign.cogit.geoxygene.appli.gl.GLTextComplex;
import fr.ign.cogit.geoxygene.appli.gl.RasterImage;
import fr.ign.cogit.geoxygene.appli.render.GeoxComplexRenderer;
import fr.ign.cogit.geoxygene.appli.render.GeoxComplexRendererBasic;
import fr.ign.cogit.geoxygene.appli.render.GeoxRendererManager;
import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.texture.BinaryGradientImageTask;
import fr.ign.cogit.geoxygene.appli.render.texture.GradientExpressiveRendering;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureTask;
import fr.ign.cogit.geoxygene.appli.task.TaskListener;
import fr.ign.cogit.geoxygene.appli.task.TaskManager;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.feature.FT_Coverage;
import fr.ign.cogit.geoxygene.style.Fill2DDescriptor;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;
import fr.ign.cogit.geoxygene.style.expressive.GradientSubshaderDescriptor;
import fr.ign.cogit.geoxygene.style.texture.BinaryGradientImageDescriptor;
import fr.ign.cogit.geoxygene.style.texture.TextureDescriptor;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLComplexRenderer;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleVertex;

/**
 * @author JeT A displayable surface is a displayable containing GL geometries
 *         matching IMultiSurfaces or IPolygon geoxygen geometries
 */
/**
 * @author Adminlocal
 * 
 */
public class DisplayableSurface extends AbstractDisplayable {

    private static final Logger logger = Logger
            .getLogger(DisplayableSurface.class.getName()); // logger
    private static final Colorizer partialColorizer = new SolidColorizer(
            Color.blue);
    private IFeature feature = null;

    private final List<IPolygon> polygons = new ArrayList<IPolygon>();

    /**
     * Constructor using a IMultiSurface
     */
    public DisplayableSurface(String name, Viewport viewport,
            IMultiSurface<?> multiSurface, IFeature feature,
            Symbolizer symbolizer, LwjglLayerRenderer layerRenderer,
            GLComplexRenderer partialRenderer) {
        super(name, viewport, layerRenderer, feature, symbolizer);
        this.setFeature(feature);
        for (Object polygon : multiSurface.getList()) {
            if (polygon instanceof IPolygon) {
                this.polygons.add((IPolygon) polygon);

            } else {
                logger.warn("multisurface does not contains only IPolygons but "
                        + polygon.getClass().getSimpleName());
            }
        }
        this.generatePartialRepresentation(partialRenderer);
    }

    /** 
     * 
     */
    public DisplayableSurface(String name, Viewport viewport, IPolygon polygon,
            IFeature feature, Symbolizer symbolizer,
            LwjglLayerRenderer layerRenderer, GLComplexRenderer partialRenderer) {
        super(name, viewport, layerRenderer, feature, symbolizer);
        this.setFeature(feature);
        this.polygons.add(polygon);
        this.generatePartialRepresentation(partialRenderer);
    }

    private void generatePartialRepresentation(GLComplexRenderer partialRenderer) {
        IEnvelope envelope = IGeometryUtil.getEnvelope(this.polygons);
        double minX = envelope.minX();
        double minY = envelope.minY();
        this.setPartialRepresentation(GLComplexFactory.createQuickPolygons(
                this.getName() + "-partial", this.polygons, partialColorizer,
                null, minX, minY, partialRenderer));
    }

    /**
     * @return the feature
     */
    @Override
    public IFeature getFeature() {
        return this.feature;
    }

    /**
     * @param feature
     *            the feature to set
     */
    @Override
    public void setFeature(IFeature feature) {
        this.feature = feature;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#isProgressable()
     */
    @Override
    public boolean isProgressable() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#isPausable()
     */
    @Override
    public boolean isPausable() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#isStopable()
     */
    @Override
    public boolean isStoppable() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()data.getPolygons()
     */
    @Override
    public List<GLComplex> generateFullRepresentation() {
        List<GLComplex> complexes = new ArrayList<GLComplex>();
        if (this.getSymbolizer().isPolygonSymbolizer()) {
            PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) this
                    .getSymbolizer();
            // System.err.println("********************** GENERATE WITH COLOR "
            // + this.getName());
            List<GLComplex> fullRep = this
                    .generateWithPolygonSymbolizer(polygonSymbolizer);
            if (fullRep != null) {
                complexes.addAll(fullRep);
            }
            return complexes;
            
        } else if (this.getSymbolizer().isTextSymbolizer()) {
            TextSymbolizer symbolizer = (TextSymbolizer) this.getSymbolizer();
            GLTextComplex primitive = new GLTextComplex("toponym-"
                    + this.getName(), this.getFeature(), 0, 0);
            primitive.setRenderer(GeoxRendererManager.getOrCreateTextRenderer(
                    symbolizer, this.getLayerRenderer()));
            complexes.add(primitive);
            return complexes;
            
        } else if (this.getSymbolizer().isRasterSymbolizer()) {
            // Ajout @amasse 29/07/2015
            // Get back the symbolizer
            RasterSymbolizer rasterSymbolizer = (RasterSymbolizer) this.getSymbolizer(); 

            // Use the rasterSymbolizer for GLComplex generation
            List<GLComplex> fullRep = this
                    .generateWithRasterSymbolizer(rasterSymbolizer);

            if (fullRep != null) {
                complexes.addAll(fullRep);
            }
            return complexes;
            // END Ajout @amasse 29/07/2015
        }
        logger.error("Surface rendering do not handle "
                + this.getSymbolizer().getClass().getSimpleName());
        super.setState(TaskState.ERROR);
        return null;
    }

    synchronized private List<GLComplex> generateWithPolygonSymbolizer(
            PolygonSymbolizer symbolizer) {

        if (this.getFeature().getFeatureCollections().size() != 1) {
            logger.error("Feature "
                    + this.getFeature()
                    + " belongs to more than one feature collection, choose the first one arbitrarily");
        }
        IFeatureCollection<IFeature> featureCollection = this.getFeature()
                .getFeatureCollection(0);
        // this complexes collection will contain inner and outline
        List<GLComplex> complexes = new ArrayList<GLComplex>();

        // generate Inner Polygon part)
        this.createInnerPolygon(symbolizer, featureCollection, complexes);

        // generate Polygon Outline
        this.generatePolygonOutline(symbolizer, featureCollection, complexes);

        return complexes;

    }

    /**
     * @param symbolizer
     * @param featureCollection
     * @param complexes
     */
    private boolean createInnerPolygon(PolygonSymbolizer symbolizer,
            IFeatureCollection<IFeature> featureCollection,
            List<GLComplex> complexes) {
        if (symbolizer.getFill() == null)
            return false;
        Fill2DDescriptor fill2dDescriptor = symbolizer.getFill()
                .getFill2DDescriptor();
        if (fill2dDescriptor instanceof TextureDescriptor) {
            TextureDescriptor textureDescriptor = (TextureDescriptor) fill2dDescriptor;

            this.createWithTextureDescriptor(symbolizer, featureCollection,
                    complexes, textureDescriptor);
        } else if (fill2dDescriptor instanceof GradientSubshaderDescriptor) {
            GradientSubshaderDescriptor expressiveDescriptor = (GradientSubshaderDescriptor) fill2dDescriptor;
            BinaryGradientImageDescriptor textureDescriptor = new BinaryGradientImageDescriptor();
            textureDescriptor.setMapScale(expressiveDescriptor.getMapScale());
            textureDescriptor.setMaxCoastlineLength(expressiveDescriptor
                    .getMaxCoastlineLength());
            textureDescriptor.setTextureResolution(expressiveDescriptor
                    .getTextureResolution());
            textureDescriptor.setBlurSize(expressiveDescriptor.getBlurSize());
            this.createWithGradientTextureDescriptor(symbolizer,
                    featureCollection, complexes, textureDescriptor);
        } else {

            complexes.addAll(this.generateWithSolidColor(symbolizer,
                    featureCollection));
            return true;
        }
        return true;
    }

    /**
     * @param symbolizer
     * @param featureCollection
     * @param complexes
     * @param textureDescriptor
     * @return
     */
    private boolean createWithGradientTextureDescriptor(
            PolygonSymbolizer symbolizer,
            IFeatureCollection<IFeature> featureCollection,
            List<GLComplex> complexes,
            BinaryGradientImageDescriptor textureDescriptor) {

        BinaryGradientImageTask gradientTextureTask = null;
        synchronized (TextureManager.getInstance()) {
            gradientTextureTask = TextureManager
                    .getInstance()
                    .getGradientTextureTask(
                            "GradientTexture-"
                                    + String.valueOf(this.getFeature().getId()),
                                    textureDescriptor, featureCollection,
                                    this.getViewport());
        }
        try {
            TaskManager.waitForCompletion(gradientTextureTask);
        } catch (InterruptedException e) {
            logger.error("Texture Task error");
            e.printStackTrace();
        }
        // logger.debug("textureTask " + textureTask.hashCode()
        // + " terminated with glTexture " + textureTask.getTexture());
        if (gradientTextureTask.getState().isError()) {
            logger.error("gradient image generation task "
                    + gradientTextureTask.getState()
                    + " finished with an error");
            logger.error(gradientTextureTask.getError());
            gradientTextureTask.getError().printStackTrace();
            return false;
        }
        
        if (gradientTextureTask.getBinaryGradientImage() != null) {

            return this.generateWithGradientTexture(complexes,
                    featureCollection, symbolizer,
                    gradientTextureTask.getBinaryGradientImage());
        }
        throw new IllegalStateException(
                "createWithGradientTextureDescriptor has no valid options");
    }

    /**
     * @param complexes
     *            complex list to be filled
     * @param featureCollection
     *            feature collection containing the GeOx-geometry
     * @param symbolizer
     *            symbolizer
     * @param gradientTextureImage
     *            gradient texture image generated from symbolizer
     * @return
     */
    private boolean generateWithGradientTexture(List<GLComplex> complexes,
            IFeatureCollection<IFeature> featureCollection,
            PolygonSymbolizer symbolizer,
            BinaryGradientImage binaryGradientImage) {
        IEnvelope envelope = featureCollection.getEnvelope();
        BasicParameterizer parameterizer = new BasicParameterizer(envelope,
                false, true);
        SolidColorizer colorizer = new SolidColorizer(symbolizer.getFill()
                .getColor());

        double minX = envelope.minX();
        double minY = envelope.minY();
        GeoxComplexRenderer renderer = GeoxRendererManager
                .getOrCreateSurfaceRenderer(symbolizer, this.getLayerRenderer());
        GLSimpleComplex content = GLComplexFactory.createFilledPolygons(
                this.getName() + "-filled", this.polygons, colorizer,
                parameterizer, minX, minY, renderer);
        BinaryGradientTexture texture = new BinaryGradientTexture(
                binaryGradientImage);
        content.setTexture(texture);
        content.setColor(symbolizer.getFill().getColor());
        content.setOverallOpacity(symbolizer.getFill().getFillOpacity());
        Fill2DDescriptor fill2dDescriptor = symbolizer.getFill()
                .getFill2DDescriptor();
        if (fill2dDescriptor != null) {
            if (fill2dDescriptor instanceof GradientSubshaderDescriptor) {
                GradientSubshaderDescriptor gradientSubshaderDescriptor = (GradientSubshaderDescriptor) fill2dDescriptor;
                content.setExpressiveRendering(new GradientExpressiveRendering(
                        gradientSubshaderDescriptor));
            } else {
                throw new UnsupportedOperationException(this.getClass()
                        .getSimpleName()
                        + " Cannot handle "
                        + fill2dDescriptor.getClass().getSimpleName());
            }
        }
        complexes.add(content);
        return true;

    }

    /**
     * @param symbolizer
     * @param featureCollection
     * @return
     */
    private boolean generatePolygonOutline(PolygonSymbolizer symbolizer,
            IFeatureCollection<IFeature> featureCollection,
            List<GLComplex> complexes) {
        if (symbolizer.getStroke() == null)
            return false;

        IEnvelope envelope = featureCollection.getEnvelope();
        double minX = envelope.minX();
        double minY = envelope.minY();
        GeoxComplexRenderer renderer = GeoxRendererManager
                .getOrCreateLineRenderer(symbolizer, this.getLayerRenderer());
        GLComplex outline = GLComplexFactory.createPolygonOutlines(
                this.getName() + "-outline", this.polygons,
                symbolizer.getStroke(), minX, minY, renderer);
        complexes.add(outline);

        return true;
    }

    /**
     * Generate GL-geometry for a given feature collection using a simple
     * texture. A basic Parameterizer is used and a BasicRenderer is assigned to
     * GL-geometry
     * 
     * @param symbolizer
     *            symbolizer containing the textureDescriptor
     * @param featureCollection
     *            GeOx-geometry to be transformed to GL-geometry
     * @param complexes
     *            GL-complexes list to be filled with new GL-geometry
     * @param textureDescriptor
     *            texture descriptor extracted from symbolizer
     * @return
     */
    private boolean createWithTextureDescriptor(PolygonSymbolizer symbolizer,
            IFeatureCollection<IFeature> featureCollection,
            List<GLComplex> complexes, TextureDescriptor textureDescriptor) {
        IEnvelope envelope = featureCollection.getEnvelope();
        // logger.debug("feature rendering : id=" + this.feature.getId()
        // + " type=" + this.feature.getFeatureType()
        // + " collection = " + featureCollection);
        TextureTask<BasicTexture> textureTask;
        synchronized (TextureManager.getInstance()) {
            textureTask = TextureManager.getInstance().getTextureTask(
                    String.valueOf(this.getFeature().getId()),
                    textureDescriptor, featureCollection, this.getViewport());
        }
        if (textureTask == null) {
            logger.warn("textureTask returned a null value for feature "
                    + featureCollection);
            return false;
        }
        try {
            TaskManager.waitForCompletion(textureTask);
        } catch (InterruptedException e) {
            logger.error("Texture Task error");
            e.printStackTrace();
        }
        // logger.debug("textureTask " + textureTask.hashCode()
        // + " terminated with glTexture " + textureTask.getTexture());
        if (textureTask.getState().isError()) {
            logger.error("texture generation task " + textureTask.getState()
                    + " finished with an error");
            logger.error(textureTask.getError());
            textureTask.getError().printStackTrace();
            return false;
        }
        // draw the texture image into resulting image
        switch (textureDescriptor.getTextureDrawingMode()) {
        case VIEWPORTSPACE:
            BasicParameterizer parameterizer = new BasicParameterizer(envelope,
                    false, true);
            parameterizer
            .scaleX(1. / textureDescriptor.getScaleFactor().getX());
            parameterizer
            .scaleY(1. / textureDescriptor.getScaleFactor().getY());
            // logger.debug("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ generate textured polygon with parameterizer "
            // + parameterizer + " with envelope " + envelope);
            // logger.debug("envelope = " + envelope.hashCode());

            if (textureTask.getTexture() != null) {
                GeoxComplexRenderer renderer = GeoxRendererManager
                        .getOrCreateSurfaceRenderer(symbolizer,
                                this.getLayerRenderer());
                GLSimpleComplex inner = this
                        .generateWithTextureAndParameterizer(symbolizer,
                                textureTask.getTexture(), parameterizer,
                                envelope, renderer);
                inner.setOverallOpacity(symbolizer.getFill().getFillOpacity());
                complexes.add(inner);

            }

            break;
        case SCREENSPACE:
            // drawTextureScreenspaceCoordinates(this.feature,
            // this.viewport, imgTexture);
            logger.warn("Screenspace coordinates textures are not yet implemented in GL rendering");
            break;
        default:
            logger.warn("Do not know how to draw texture type "
                    + textureDescriptor.getTextureDrawingMode());
        }
        return true;
    }

    /**
     * Generates the GL-geometry for the inner part of 'this.polygons'
     * GeOx-geometry using a polygon symbolizer containing a simple color. A
     * 
     * @param symbolizer
     * @param featureCollection
     * @return
     */
    private List<GLComplex> generateWithSolidColor(
            PolygonSymbolizer symbolizer,
            IFeatureCollection<IFeature> featureCollection) {
        List<GLComplex> complexes = new ArrayList<GLComplex>();
        SolidColorizer colorizer = new SolidColorizer(symbolizer.getFill()
                .getColor());
        IEnvelope envelope = featureCollection.getEnvelope();

        double minX = envelope.minX();
        double minY = envelope.minY();
        GeoxComplexRenderer renderer = GeoxRendererManager
                .getOrCreateSurfaceRenderer(symbolizer, this.getLayerRenderer());
        // System.err.println("create filled polygon " + this.getName());
        GLSimpleComplex content = GLComplexFactory.createFilledPolygons(
                this.getName() + "-filled", this.polygons, colorizer,
                new BasicParameterizer(envelope, false, false), minX, minY,
                renderer);
        // System.err.println("created filled polygon " + this.getName());
        content.setColor(symbolizer.getFill().getColor());
        content.setOverallOpacity(symbolizer.getFill().getFillOpacity());
        complexes.add(content);
        return complexes;
    }

    // private void generateWithTexturedPolygonSymbolizer(
    // TexturedPolygonSymbolizer symbolizer) {
    // throw new IllegalStateException(
    // "Set the envelope !. Should it be the feature collection, the polygon set or the feature envelope ???");
    // // fr.ign.cogit.geoxygene.util.gl.Texture texture =
    // // symbolizer.getTexture();
    // // Parameterizer parameterizer = symbolizer.getParameterizer();
    // // this.generateWithTextureAndParameterizer(texture, parameterizer, );
    // }

    /**
     * @param texture
     * @param parameterizer
     * @return
     */
    private GLSimpleComplex generateWithTextureAndParameterizer(
            PolygonSymbolizer symbolizer,
            fr.ign.cogit.geoxygene.util.gl.Texture texture,
            Parameterizer parameterizer, IEnvelope envelope,
            GLComplexRenderer renderer) {
        double minX = envelope.minX();
        double minY = envelope.minY();
        SolidColorizer colorizer = new SolidColorizer(symbolizer.getFill()
                .getColor());

        GLSimpleComplex content = GLComplexFactory.createFilledPolygons(
                this.getName() + "-texture-filled", this.polygons, colorizer,
                parameterizer, minX, minY, renderer);
        // GLSimpleComplex content = this.GLComplexQuadEnvelope(this.getName()
        // + "-texture-envelope", envelope, null, parameterizer, minX,
        // minY, renderer);
        // remove previous texture from texture manager
        if (content.getTexture() != null) {
            // FIXME
            // TextureManager.getInstance().uncacheTexture(content.getTexture());
        }
        content.setTexture(texture);

        return content;
    }

    // private void generateWithDistanceFieldTexturedPolygonSymbolizer(
    // List<GLComplex> complexes,
    // DistanceFieldTexturedPolygonSymbolizer symbolizer) {
    // IEnvelope envelope = IGeometryUtil.getEnvelope(this.polygons);
    // double minX = envelope.minX();
    // double minY = envelope.minY();
    // GLSimpleComplex content = GLComplexFactory.createFilledPolygons(
    // this.getName() + "-dfield-filled", this.polygons, null,
    // symbolizer.getParameterizer(), minX, minY);
    // content.setTexture(symbolizer.getTexture());
    // complexes.add(content);
    //
    // }

    // @Override
    // synchronized public void onStateChange(TextureTask<?> task,
    // TaskState oldState) {
    // if (task.getState().isFinished()) {
    // this.notify();
    // task.removeTaskListener(this);
    // }
    //
    // }

    /**
     * Create a quad on an envelope (useful for debug purpose)
     * 
     * @param name
     * @param envelope
     * @param object
     * @param parameterizer
     * @param minX
     * @param minY
     * @param renderer
     * @return
     */
    private GLSimpleComplex GLComplexQuadEnvelope(String name,
            IEnvelope envelope, Object object, Parameterizer parameterizer,
            double minX, double minY, GLComplexRenderer renderer) {
        GLSimpleComplex primitive = new GLSimpleComplex(name + "-envelope",
                minX, minY);
        GLMesh quad = primitive.addGLMesh(GL11.GL_TRIANGLES);
        int nw = primitive.addVertex(new GLSimpleVertex(new float[] {
                (float) (envelope.getLowerCorner().getX() - minX),
                (float) (envelope.getLowerCorner().getY() - minY), 0 },
                new float[] { 0, 1 }));
        int ne = primitive.addVertex(new GLSimpleVertex(new float[] {
                (float) (envelope.getLowerCorner().getX() - minX),
                (float) (envelope.getUpperCorner().getY() - minY), 0 },
                new float[] { 0, 0 }));
        int sw = primitive.addVertex(new GLSimpleVertex(new float[] {
                (float) (envelope.getUpperCorner().getX() - minX),
                (float) (envelope.getLowerCorner().getY() - minY), 0 },
                new float[] { 1, 1 }));
        int se = primitive.addVertex(new GLSimpleVertex(new float[] {
                (float) (envelope.getUpperCorner().getX() - minX),
                (float) (envelope.getUpperCorner().getY() - minY), 0 },
                new float[] { 1, 0 }));
        quad.addIndices(nw, ne, se);
        quad.addIndices(nw, se, sw);
        primitive.setRenderer(new GeoxComplexRendererBasic(this
                .getLayerRenderer(), this.getSymbolizer()));
        return primitive;
    }

    /**
     * Apply texture when it has finished being generated
     * 
     * @author JeT
     * 
     */
    private static class TextureApplyer implements
    TaskListener<TextureTask<BasicTexture>> {

        private final GLSimpleComplex primitive;

        public TextureApplyer(GLSimpleComplex primitive,
                TextureTask<? extends BasicTexture> textureTask) {
            this.primitive = primitive;
            if (textureTask.getState().isFinished()) {
                ((BasicTexture) this.primitive.getTexture())
                .setTextureImage(textureTask.getTexture()
                        .getTextureImage());
                textureTask.removeTaskListener(this);
            }
        }

        @Override
        public void onStateChange(TextureTask<BasicTexture> task,
                TaskState oldState) {
            if (task.getState().isFinished()) {
                ((BasicTexture) this.primitive.getTexture())
                .setTextureImage(task.getTexture().getTextureImage());
                task.removeTaskListener(this);
            }
        }

    }

    synchronized private List<GLComplex> generateWithRasterSymbolizer(
            RasterSymbolizer rasterSymbolizer) {

        // Check : if more than 1 Collection, select the first one
        // -> I am not okay with that, but who am i to judge ? just a developer.
        if (this.getFeature().getFeatureCollections().size() != 1) {
            logger.error("Feature "
                    + this.getFeature()
                    + " belongs to more than one feature collection, choose the first one arbitrarily");
        }
        
        // Get back the feature collection an create a GLComplex list
        IFeatureCollection<IFeature> featureCollection = this.getFeature()
                .getFeatureCollection(0);
        List<GLComplex> complexes = new ArrayList<GLComplex>();

        // FIXME Is it really necessary ? in the content ?
        SolidColorizer colorizer = new SolidColorizer(java.awt.Color.GREEN);
        
        // Get back the envelope
        IEnvelope envelope = featureCollection.getEnvelope();
        double minX = envelope.minX();
        double minY = envelope.minY();
        
        // FIXME Is it really necessary ?
        BasicParameterizer parameterizer = new BasicParameterizer(envelope,
                false, true);
        
        // create the renderer
        // getOrCreateSurfaceRenderer do not manage rasterSymbolizer
        GeoxComplexRenderer renderer = GeoxRendererManager
                .getOrCreateSurfaceRenderer(rasterSymbolizer, this.getLayerRenderer());

        // Create a GLSimpleComplex object
        GLSimpleComplex content = GLComplexFactory.createFilledPolygons(
                this.getName() + "-texture-filled", this.polygons, colorizer,
                parameterizer, minX, minY, renderer);
        
        // Useless        
        //content.setColor(java.awt.Color.GREEN);
        
        // Create RasterImage and read it, once for all (the all shader life of course)
        RasterImage rasterImage = new RasterImage();
        rasterImage.readImage(((FT_Coverage) featureCollection.get(0)).coverage(),rasterSymbolizer);
        content.setRasterImage(rasterImage);
        
        // Colormap
        rasterImage.readColormap(rasterSymbolizer);
        
        content.setOverallOpacity(rasterSymbolizer.getOpacity());
        complexes.add(content);

        // return : GLComplex list
        return complexes;
    }
}