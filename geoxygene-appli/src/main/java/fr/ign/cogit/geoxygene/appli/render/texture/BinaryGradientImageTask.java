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

package fr.ign.cogit.geoxygene.appli.render.texture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.appli.gl.BinaryGradientImage;
import fr.ign.cogit.geoxygene.appli.gl.BinaryGradientImage.BinaryGradientImageParameters;
import fr.ign.cogit.geoxygene.appli.task.AbstractTask;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.texture.BinaryGradientImageDescriptor;

/**
 * @author JeT This Task generates a BinaryGradientImage. It does not transform
 *         it into a gradient image with false colors as GradientTextureTask
 *         does
 */
public class BinaryGradientImageTask extends AbstractTask {

    private static final Logger logger = Logger
            .getLogger(BinaryGradientImageTask.class.getName()); // logger
    // texture descriptor (from style)
    private BinaryGradientImageDescriptor binaryGradientImageDescriptor = null;
    private BinaryGradientImage binaryGradientImage = null;
    private IFeatureCollection<IFeature> featureCollection = null;
    private File binaryGradientImageFile = null;
    private boolean needWriting = false; // set to true after generation (not
                                         // when read)
    public static final double CM_PER_INCH = 2.540005;
    public static final double M_PER_INCH = CM_PER_INCH / 100.;

    /**
     * Using this constructor, the gradient image will be generated with the
     * descriptor and the feature collection
     * 
     * @param texture
     */
    public BinaryGradientImageTask(String name,
            BinaryGradientImageDescriptor textureDescriptor,
            IFeatureCollection<IFeature> featureCollection) {
        super("Gradient" + name);
        this.binaryGradientImageDescriptor = textureDescriptor;
        this.featureCollection = featureCollection;
        this.binaryGradientImageFile = null;
        this.needWriting = true;
    }

    /**
     * Using this constructor the gradient image will be read from the given
     * file
     * 
     * @param texture
     */
    public BinaryGradientImageTask(String name, File file) {
        super("Gradient" + name);
        this.binaryGradientImageDescriptor = null;
        this.featureCollection = null;
        this.binaryGradientImageFile = file;
        this.needWriting = false;
    }

    /**
     * get the image descriptor used to generate the gradient image. It can be
     * null if the gradient image has been read from a file
     * 
     * @return
     */
    public BinaryGradientImageDescriptor getBinaryGradientImageDescriptor() {
        return this.binaryGradientImageDescriptor;
    }

    /**
     * get the file associated with this gradient image generation/read task
     * 
     * @return
     */
    public File getBinaryGradientImageFile() {
        if (this.binaryGradientImageFile != null) {
            return this.binaryGradientImageFile;
        }
        return TextureManager.generateBinaryGradientImageUniqueFile(
                this.binaryGradientImageDescriptor, this.featureCollection);
    }

    /**
     * get the feature collection used to generate the gradient image. It can be
     * null if the gradient image has been read from a file
     * 
     * @return
     */
    public IFeatureCollection<IFeature> getFeatureCollection() {
        return this.featureCollection;
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
     * @see fr.ign.cogit.geoxygene.appli.task.Task#isStoppable()
     */
    @Override
    public boolean isStoppable() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if (this.binaryGradientImageFile != null) {
            this.readBinaryGradientImageFile();
        } else {
            this.generateBinaryGradientImage();
        }
    }

    /**
     * Generate the gradient image from 'this.binaryGradientImageDescriptor'
     */
    private void generateBinaryGradientImage() {
        this.setState(TaskState.WAITING);
        this.setState(TaskState.INITIALIZING);
        IEnvelope envelope = this.featureCollection.getEnvelope();

        double mapScale = this.binaryGradientImageDescriptor.getMapScale();
        int textureWidth = (int) (envelope.width()
                * this.binaryGradientImageDescriptor.getTextureResolution() / (M_PER_INCH * mapScale));
        int textureHeight = (int) (envelope.length()
                * this.binaryGradientImageDescriptor.getTextureResolution() / (M_PER_INCH * mapScale));
        int blurSize = this.binaryGradientImageDescriptor.getBlurSize();

        List<IPolygon> polygons = new ArrayList<IPolygon>();
        // convert the multisurface as a collection of polygons
        for (IFeature feature : this.featureCollection) {
            if (feature.getGeom() instanceof IMultiSurface<?>) {
                IMultiSurface<?> multiSurface = (IMultiSurface<?>) feature
                        .getGeom();
                for (IOrientableSurface surface : multiSurface.getList()) {
                    if (surface instanceof IPolygon) {
                        IPolygon polygon = (IPolygon) surface;
                        polygons.add(polygon);
                    } else {
                        logger.error("Distance Field Parameterizer does handle multi surfaces containing only polygons, not "
                                + surface.getClass().getSimpleName());
                    }
                }

            } else {
                System.err.println("geometry type not handled : "
                        + feature.getGeom().getClass().getSimpleName());
            }
        }
        double maxCoastLineLength = this.binaryGradientImageDescriptor
                .getMaxCoastlineLength();

        this.setState(TaskState.RUNNING);
        try {
            BinaryGradientImageParameters params = new BinaryGradientImageParameters(
                    textureWidth, textureHeight, polygons, envelope,
                    maxCoastLineLength, blurSize);
            this.binaryGradientImage = BinaryGradientImage
                    .generateBinaryGradientImage(params);
            if (this.binaryGradientImage == null) {
                this.setError(new IllegalStateException(
                        "GradientTextureImage returned a null texture"));
                this.setState(TaskState.ERROR);
                return;
            }
            this.setState(TaskState.FINISHED);
        } catch (Exception e) {
            this.setError(e);
            this.setState(TaskState.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Read the gradient image from 'this.binaryGradientImageFile'
     */
    private void readBinaryGradientImageFile() {
        this.setState(TaskState.WAITING);
        this.setState(TaskState.INITIALIZING);
        try {

            this.binaryGradientImage = BinaryGradientImage
                    .readBinaryGradientImage(this.binaryGradientImageFile);
            if (this.binaryGradientImage == null) {
                this.setError(new IllegalStateException(
                        "GradientTextureImage returned a null texture"));
                this.setState(TaskState.ERROR);
                return;
            }
            this.setState(TaskState.FINISHED);
        } catch (Exception e) {
            logger.error("An error occurred reading binary gradient image '"
                    + this.binaryGradientImageFile.getAbsolutePath() + "'");
            this.setError(e);
            this.setState(TaskState.ERROR);
        }
    }

    public BinaryGradientImage getBinaryGradientImage() {
        return this.binaryGradientImage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GradientImageTask [textureDescriptor="
                + this.binaryGradientImageDescriptor
                + ", gradientTextureImage=" + this.binaryGradientImage
                + ", featureCollection=" + this.featureCollection + "]";
    }

    public boolean needWriting() {
        return this.needWriting;
    }

}
