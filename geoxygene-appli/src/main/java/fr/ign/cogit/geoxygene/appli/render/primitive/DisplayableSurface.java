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
import org.lwjgl.opengl.GL11;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLComplexFactory;
import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureTask;
import fr.ign.cogit.geoxygene.appli.task.TaskListener;
import fr.ign.cogit.geoxygene.appli.task.TaskManager;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.texture.TextureDescriptor;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLComplexRenderer;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleComplex;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleVertex;

/**
 * @author JeT A displayable surface is a displayable containing GL geometries
 *         matching IMultiSurfaces or IPolygon geoxygen geometries
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
        super(name, viewport, layerRenderer, symbolizer);
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
        super(name, viewport, layerRenderer, symbolizer);
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
    public IFeature getFeature() {
        return this.feature;
    }

    /**
     * @param feature
     *            the feature to set
     */
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
        if (this.getSymbolizer() instanceof PolygonSymbolizer) {
            PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) this
                    .getSymbolizer();
            // System.err.println("********************** GENERATE WITH COLOR "
            // + this.getName());
            List<GLComplex> fullRep = this
                    .generateWithPolygonSymbolizer(polygonSymbolizer);
            if (fullRep != null) {
                complexes.addAll(fullRep);
            }
        } else {
            super.setError(new IllegalStateException("task " + this.getName()
                    + " has no PolygonSymbolizer"));
            return null;
        }
        return complexes;
    }

    synchronized private List<GLComplex> generateWithPolygonSymbolizer(
            PolygonSymbolizer symbolizer) {
        TextureDescriptor textureDescriptor = symbolizer.getFill()
                .getTextureDescriptor();
        if (this.getFeature().getFeatureCollections().size() != 1) {
            logger.error("Feature "
                    + this.getFeature()
                    + " belongs to more than one feature collection, choose the first one arbitrarily");
        }
        IFeatureCollection<IFeature> featureCollection = this.getFeature()
                .getFeatureCollection(0);
        List<GLComplex> complexes = new ArrayList<GLComplex>();
        // IEnvelope envelope = IGeometryUtil.getEnvelope(this.polygons);
        IEnvelope envelope = featureCollection.getEnvelope();
        double minX = envelope.minX();
        double minY = envelope.minY();
        if (textureDescriptor != null) {
            // texture.setTextureDimension(2000, 2000);
            // logger.debug("feature rendering : id=" + this.feature.getId()
            // + " type=" + this.feature.getFeatureType()
            // + " collection = " + featureCollection);
            TextureTask<BasicTexture> textureTask;
            synchronized (TextureManager.getInstance()) {
                textureTask = TextureManager.getInstance().getTextureTask(
                        String.valueOf(this.getFeature().getId()),
                        textureDescriptor, featureCollection,
                        this.getViewport());
            }
            if (textureTask == null) {
                logger.warn("textureTask returned a null value for feature "
                        + featureCollection);
                return null;
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
                logger.error("texture generation task "
                        + textureTask.getState() + " finished with an error");
                logger.error(textureTask.getError());
                textureTask.getError().printStackTrace();
                return null;
            }
            // draw the texture image into resulting image
            switch (textureDescriptor.getTextureDrawingMode()) {
            case VIEWPORTSPACE:
                BasicParameterizer parameterizer = new BasicParameterizer(
                        envelope, false, true);
                // logger.debug("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ generate textured polygon with parameterizer "
                // + parameterizer + " with envelope " + envelope);
                // logger.debug("envelope = " + envelope.hashCode());

                if (textureTask.getTexture() != null) {
                    GeoxComplexRenderer renderer = GeoxRendererManager
                            .getOrCreateSurfaceRenderer(symbolizer,
                                    this.getLayerRenderer());
                    GLSimpleComplex inner = this
                            .generateWithTextureAndParameterizer(
                                    textureTask.getTexture(), parameterizer,
                                    envelope, renderer);
                    inner.setOverallOpacity(symbolizer.getFill()
                            .getFillOpacity());
                    complexes.add(inner);

                }
                // if (textureTask.getTextureWidth()
                // * textureTask.getTextureHeight() != 0) {
                // BasicTexture glTexture = new BasicTexture(
                // textureTask.getTextureWidth(),
                // textureTask.getTextureHeight());
                // glTexture.setScaleX(textureTask.getTexture().getScaleX());
                // glTexture.setScaleY(textureTask.getTexture().getScaleX());
                // GLSimpleComplex primitive = this
                // .generateWithTextureAndParameterizer(complexes,
                // textureTask.getTexture(), parameterizer, envelope);
                // textureTask.addTaskListener(new TextureApplyer(primitive,
                // textureTask));
                // }
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
        } else {
            GeoxComplexRenderer renderer = GeoxRendererManager
                    .getOrCreateSurfaceRenderer(symbolizer,
                            this.getLayerRenderer());
            complexes.addAll(this.generateWithSolidColor(symbolizer, envelope,
                    minX, minY, renderer));
        }
        GeoxComplexRenderer renderer = GeoxRendererManager
                .getOrCreateLineRenderer(symbolizer, this.getLayerRenderer());
        GLComplex outline = GLComplexFactory.createPolygonOutlines(
                this.getName() + "-outline", this.polygons,
                symbolizer.getStroke(), minX, minY, renderer);
        complexes.add(outline);
        // Thread.dumpStack();
        return complexes;

    }

    /**
     * @param symbolizer
     * @param complexes
     * @param envelope
     * @param minX
     * @param minY
     */
    private List<GLComplex> generateWithSolidColor(
            PolygonSymbolizer symbolizer, IEnvelope envelope, double minX,
            double minY, GLComplexRenderer renderer) {
        List<GLComplex> complexes = new ArrayList<GLComplex>();
        SolidColorizer colorizer = new SolidColorizer(symbolizer.getFill()
                .getColor());

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
            fr.ign.cogit.geoxygene.util.gl.Texture texture,
            Parameterizer parameterizer, IEnvelope envelope,
            GLComplexRenderer renderer) {
        double minX = envelope.minX();
        double minY = envelope.minY();
        GLSimpleComplex content = GLComplexFactory.createFilledPolygons(
                this.getName() + "-texture-filled", this.polygons, null,
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
}
