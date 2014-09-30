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
import fr.ign.cogit.geoxygene.appli.gl.GradientTextureImage;
import fr.ign.cogit.geoxygene.appli.gl.GradientTextureImage.GradientTextureImageParameters;
import fr.ign.cogit.geoxygene.appli.task.AbstractTask;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.texture.GradientTextureDescriptor;

/**
 * @author JeT This Task generates a GradientImage. It does not transform it
 *         into a gradient image with false colors as GradientTextureTask does
 */
public class GradientImageTask extends AbstractTask {

    private static final Logger logger = Logger
            .getLogger(GradientImageTask.class.getName()); // logger
    // texture descriptor (from style)
    private GradientTextureDescriptor textureDescriptor = null;
    private GradientTextureImage gradientTextureImage = null;
    private IFeatureCollection<IFeature> featureCollection = null;
    private File textureFile = null;

    public static final double CM_PER_INCH = 2.540005;
    public static final double M_PER_INCH = CM_PER_INCH / 100.;

    /**
     * @param texture
     */
    public GradientImageTask(String name,
            GradientTextureDescriptor textureDescriptor,
            IFeatureCollection<IFeature> featureCollection) {
        super("Gradient" + name);
        this.textureDescriptor = textureDescriptor;
        this.featureCollection = featureCollection;
        this.textureFile = null;
    }

    /**
     * @param texture
     */
    public GradientImageTask(String name, File file) {
        super("Gradient" + name);
        this.textureDescriptor = null;
        this.featureCollection = null;
        this.textureFile = file;
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
        if (this.textureFile != null) {
            this.setState(TaskState.WAITING);
            this.setState(TaskState.INITIALIZING);
            try {

                this.gradientTextureImage = GradientTextureImage
                        .readTextureImage(this.textureFile);
                if (this.gradientTextureImage == null) {
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
        } else {
            this.setState(TaskState.WAITING);
            this.setState(TaskState.INITIALIZING);
            IEnvelope envelope = this.featureCollection.getEnvelope();

            double mapScale = this.textureDescriptor.getMapScale();
            int textureWidth = (int) (envelope.width()
                    * this.textureDescriptor.getTextureResolution() / (M_PER_INCH * mapScale));
            int textureHeight = (int) (envelope.length()
                    * this.textureDescriptor.getTextureResolution() / (M_PER_INCH * mapScale));

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
            double maxCoastLineLength = this.textureDescriptor
                    .getMaxCoastlineLength();

            this.setState(TaskState.RUNNING);
            try {
                GradientTextureImageParameters params = new GradientTextureImageParameters(
                        textureWidth, textureHeight, polygons, envelope,
                        maxCoastLineLength);
                this.gradientTextureImage = GradientTextureImage
                        .generateGradientTextureImage(params);
                if (this.gradientTextureImage == null) {
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
    }

    public GradientTextureImage getGradientTextureImage() {
        return this.gradientTextureImage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GradientImageTask [textureDescriptor=" + this.textureDescriptor
                + ", gradientTextureImage=" + this.gradientTextureImage
                + ", featureCollection=" + this.featureCollection + "]";
    }

}
