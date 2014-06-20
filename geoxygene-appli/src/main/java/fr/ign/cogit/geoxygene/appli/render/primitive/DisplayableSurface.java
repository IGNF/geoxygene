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
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLComplexFactory;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureTask;
import fr.ign.cogit.geoxygene.appli.task.AbstractTask;
import fr.ign.cogit.geoxygene.appli.task.TaskListener;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.texture.Texture;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleComplex;

/**
 * @author JeT
 * 
 */
public class DisplayableSurface extends AbstractTask implements GLDisplayable,
        TaskListener<TextureTask<?>> {

    private static final Logger logger = Logger
            .getLogger(DisplayableSurface.class.getName()); // logger
    private static final Colorizer partialColorizer = new SolidColorizer(
            Color.blue);

    private IFeature feature = null;
    private Viewport viewport = null;
    private final List<IPolygon> polygons = new ArrayList<IPolygon>();
    private Symbolizer symbolizer = null;
    private List<GLComplex> fullRepresentation = null;
    private GLComplex partialRepresentation = null;
    private long displayCount = 0; // number of time it has been displayed
    private Date lastDisplayTime; // last time it has been displayed

    // private Colorizer colorizer = null;
    // private Parameterizer parameterizer = null;

    // private Texture texture = null;

    /**
     * Constructor using a IMultiSurface
     */
    public DisplayableSurface(String name, Viewport viewport,
            IMultiSurface<?> multiSurface, IFeature feature,
            Symbolizer symbolizer) {
        super(name);
        this.viewport = viewport;
        this.feature = feature;
        this.symbolizer = symbolizer;

        for (Object polygon : multiSurface.getList()) {
            if (polygon instanceof IPolygon) {
                this.polygons.add((IPolygon) polygon);

            } else {
                logger.warn("multisurface does not contains only IPolygons but "
                        + polygon.getClass().getSimpleName());
            }
        }
    }

    /** 
     * 
     */
    public DisplayableSurface(String name, Viewport viewport, IPolygon polygon,
            IFeature feature, Symbolizer symbolizer) {
        super(name);
        this.symbolizer = symbolizer;
        this.polygons.add(polygon);
        this.viewport = viewport;
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

    /**
     * @return the displayCount
     */
    @Override
    public long getDisplayCount() {
        return this.displayCount;
    }

    /**
     * @return the lastDisplayTime
     */
    @Override
    public Date getLastDisplayTime() {
        return this.lastDisplayTime;
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

    /**
     * @return the viewport
     */
    public Viewport getViewport() {
        return this.viewport;
    }

    /**
     * @param viewport
     *            the viewport to set
     */
    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        // System.err.println("********************** START " + this.getName());
        super.setState(TaskState.INITIALIZING);
        this.fullRepresentation = null;
        super.setState(TaskState.RUNNING);

        // if (this.getTexture() != null) {
        // this.generateWithDistanceField();
        // }

        // if (this.symbolizer instanceof
        // DistanceFieldTexturedPolygonSymbolizer) {
        // DistanceFieldTexturedPolygonSymbolizer polygonSymbolizer =
        // (DistanceFieldTexturedPolygonSymbolizer) this.symbolizer;
        // this.generateWithDistanceFieldTexturedPolygonSymbolizer(polygonSymbolizer);
        // } else if (this.symbolizer instanceof TexturedPolygonSymbolizer) {
        // TexturedPolygonSymbolizer polygonSymbolizer =
        // (TexturedPolygonSymbolizer) this.symbolizer;
        // // System.err.println("********************** GENERATE WITH TEXTURE "
        // // + this.getName());
        // this.generateWithTexturedPolygonSymbolizer(polygonSymbolizer);
        // } else
        if (this.symbolizer instanceof PolygonSymbolizer) {
            PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) this.symbolizer;
            // System.err.println("********************** GENERATE WITH COLOR "
            // + this.getName());
            this.generateWithPolygonSymbolizer(polygonSymbolizer);
        } else {
            super.setState(TaskState.ERROR);
            return;
        }
        super.setState(TaskState.FINALIZING);
        super.setState(TaskState.FINISHED);
        // System.err.println("********************** FINISHED " +
        // this.getName());
    }

    synchronized private void generateWithPolygonSymbolizer(
            PolygonSymbolizer symbolizer) {
        Texture texture = symbolizer.getFill().getTexture();
        if (this.getFeature().getFeatureCollections().size() != 1) {
            logger.error("Feature "
                    + this.getFeature()
                    + " belongs to more than one feature collection, choose the first one arbitrarily");
        }
        IFeatureCollection<IFeature> featureCollection = this.getFeature()
                .getFeatureCollection(0);
        List<GLComplex> complexes = new ArrayList<GLComplex>();
        // IEnvelope envelope = IGeometryUtil.getEnvelope(this.polygons);
        IEnvelope envelope = featureCollection.envelope();
        double minX = envelope.minX();
        double minY = envelope.minY();
        if (texture != null) {
            // texture.setTextureDimension(2000, 2000);
            // logger.debug("feature rendering : id=" + this.feature.getId()
            // + " type=" + this.feature.getFeatureType()
            // + " collections = " + this.feature.getFeatureCollections());
            TextureTask<? extends Texture> textureTask;
            synchronized (TextureManager.getInstance()) {
                textureTask = TextureManager.getInstance().getTextureTask(
                        texture, featureCollection, this.getViewport());
                if (textureTask == null) {
                    logger.warn("textureTask returned a null value for feature "
                            + featureCollection);
                    return;
                }
                textureTask.addTaskListener(this);
                textureTask.start();
            }

            // do not wait for texture computation completion.

            if (textureTask.getState().isError()) {
                logger.error("texture generation task "
                        + textureTask.getState() + " finished with an error");
                logger.error(textureTask.getError());
                return;
            }
            // draw the texture image into resulting image
            switch (texture.getTextureDrawingMode()) {
            case VIEWPORTSPACE:
                BasicParameterizer parameterizer = new BasicParameterizer(
                        envelope, false, true);
                if (textureTask.getTextureWidth()
                        * textureTask.getTextureHeight() != 0) {
                    BasicTexture glTexture = new BasicTexture(
                            textureTask.getTextureWidth(),
                            textureTask.getTextureHeight());
                    glTexture.setScaleX(textureTask.getTexture()
                            .getScaleFactor().getX());
                    glTexture.setScaleY(textureTask.getTexture()
                            .getScaleFactor().getY());
                    GLSimpleComplex primitive = this
                            .generateWithTextureAndParameterizer(complexes,
                                    glTexture, parameterizer, envelope);
                    textureTask.addTaskListener(new TextureApplyer(primitive,
                            textureTask));
                }
                break;
            case SCREENSPACE:
                // drawTextureScreenspaceCoordinates(this.feature,
                // this.viewport, imgTexture);
                logger.warn("Screenspace coordinates textures are not yet implemented in GL rendering");
                break;
            default:
                logger.warn("Do not know how to draw texture type "
                        + texture.getTextureDrawingMode());
            }
        } else {

            this.generateWithSolidColor(symbolizer, complexes, envelope, minX,
                    minY);
        }
        GLComplex outline = GLComplexFactory.createPolygonOutlines(
                this.getName() + "-outline", this.polygons,
                symbolizer.getStroke(), minX, minY);
        // System.err.println("created outlined polygon " + this.getName());
        complexes.add(outline);
        this.fullRepresentation = complexes;

    }

    /**
     * @param symbolizer
     * @param complexes
     * @param envelope
     * @param minX
     * @param minY
     */
    private void generateWithSolidColor(PolygonSymbolizer symbolizer,
            List<GLComplex> complexes, IEnvelope envelope, double minX,
            double minY) {
        SolidColorizer colorizer = new SolidColorizer(symbolizer.getFill()
                .getColor());
        // System.err.println("create filled polygon " + this.getName());
        GLSimpleComplex content = GLComplexFactory.createFilledPolygons(
                this.getName() + "-filled", this.polygons, colorizer,
                new BasicParameterizer(envelope, false, false), minX, minY);
        // System.err.println("created filled polygon " + this.getName());
        content.setColor(symbolizer.getFill().getColor());
        complexes.add(content);
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
            List<GLComplex> complexes,
            fr.ign.cogit.geoxygene.util.gl.Texture texture,
            Parameterizer parameterizer, IEnvelope envelope) {
        double minX = envelope.minX();
        double minY = envelope.minY();
        GLSimpleComplex content = GLComplexFactory.createFilledPolygons(
                this.getName() + "-texture-filled", this.polygons, null,
                parameterizer, minX, minY);
        content.setTexture(texture);
        complexes.add(content);
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

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.render.primitive.GLDisplayable#
     * getPartialRepresentation()
     */
    @Override
    public GLComplex getPartialRepresentation() {
        if (this.partialRepresentation == null) {
            IEnvelope envelope = IGeometryUtil.getEnvelope(this.polygons);
            double minX = envelope.minX();
            double minY = envelope.minY();
            this.partialRepresentation = GLComplexFactory.createQuickPolygons(
                    this.getName() + "-partial", this.polygons,
                    partialColorizer, null, minX, minY);
        }
        this.displayIncrement();
        return this.partialRepresentation;
    }

    private void displayIncrement() {
        this.displayCount++;
        this.lastDisplayTime = new Date();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.render.primitive.GLDisplayable#
     * getFullRepresentation()
     */
    @Override
    public Collection<GLComplex> getFullRepresentation() {
        if (this.fullRepresentation != null) {
            this.displayIncrement();
        }
        return this.fullRepresentation;
    }

    @Override
    synchronized public void onStateChange(TextureTask<?> task,
            TaskState oldState) {
        if (task.getState().isFinished()) {
            this.notify();
            task.removeTaskListener(this);
        }

    }

    private static class TextureApplyer implements
            TaskListener<TextureTask<Texture>> {

        private final GLSimpleComplex primitive;

        public TextureApplyer(GLSimpleComplex primitive,
                TextureTask<? extends Texture> textureTask) {
            this.primitive = primitive;
            if (textureTask.getState().isFinished()) {
                ((BasicTexture) this.primitive.getTexture())
                        .setTextureImage(textureTask.getTexture()
                                .getTextureImage());
                textureTask.removeTaskListener(this);
            }
        }

        @Override
        public void onStateChange(TextureTask<Texture> task, TaskState oldState) {
            if (task.getState().isFinished()) {
                ((BasicTexture) this.primitive.getTexture())
                        .setTextureImage(task.getTexture().getTextureImage());
                task.removeTaskListener(this);
            }
        }

    }
}
