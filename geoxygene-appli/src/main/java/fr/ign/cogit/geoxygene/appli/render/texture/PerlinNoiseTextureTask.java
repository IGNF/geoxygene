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

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import com.jhlabs.image.LinearColormap;
import com.jhlabs.image.TextureFilter;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.texture.DimensionDescriptor;
import fr.ign.cogit.geoxygene.style.texture.PerlinNoiseTexture;
import fr.ign.cogit.geoxygene.util.ImageUtil;

/**
 * @author JeT
 * 
 */
public class PerlinNoiseTextureTask extends
        AbstractTextureTask<PerlinNoiseTexture> {

    private static final Logger logger = Logger
            .getLogger(PerlinNoiseTextureTask.class.getName()); // logger

    private IFeatureCollection<IFeature> featureCollection = null;
    private Viewport viewport = null;

    /**
     * @param texture
     */
    public PerlinNoiseTextureTask(PerlinNoiseTexture texture,
            IFeatureCollection<IFeature> featureCollection, Viewport viewport) {
        super(texture);
        this.setViewport(viewport);
        this.setFeatureCollection(featureCollection);
    }

    /**
     * @param viewport
     *            the viewport to set
     */
    public final void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    /**
     * @return the viewport
     */
    public Viewport getViewport() {
        return this.viewport;
    }

    /**
     * @return the featureCollection
     */
    public IFeatureCollection<IFeature> getFeatureCollection() {
        return this.featureCollection;
    }

    /**
     * @param featureCollection
     *            the featureCollection to set
     */
    public final void setFeatureCollection(
            IFeatureCollection<IFeature> featureCollection) {
        this.featureCollection = featureCollection;
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
        this.setState(TaskState.INITIALIZING);
        this.setState(TaskState.RUNNING);
        try {
            TextureFilter filter = new TextureFilter();
            filter.setColormap(new LinearColormap(this.getTexture().getColor1()
                    .getRGB(), this.getTexture().getColor2().getRGB()));
            filter.setScale(this.getTexture().getScale());
            filter.setStretch(this.getTexture().getStretch());
            filter.setAmount(this.getTexture().getAmount());
            filter.setAngle(this.getTexture().getAngle());
            IEnvelope envelope = this.getFeatureCollection().envelope();
            this.getTexture()
                    .setDimension(
                            new DimensionDescriptor(envelope.width(), envelope
                                    .length()));
            this.getTexture().setTextureDimension(
                    PerlinNoiseTextureTask.computeTextureDimension(envelope,
                            this.getViewport(), this.getTexture()
                                    .getTextureResolution()));
            logger.debug("set perlin texture image size: "
                    + this.getTexture().getTextureWidth() + "x"
                    + this.getTexture().getTextureHeight());
            BufferedImage imgTexture = ImageUtil.createBufferedImage(this
                    .getTexture().getTextureWidth(), this.getTexture()
                    .getTextureHeight());
            filter.filter(imgTexture, imgTexture);
            this.getTexture().setTextureImage(imgTexture);
            this.setState(TaskState.FINISHED);
        } catch (Exception e) {
            e.printStackTrace();
            this.setState(TaskState.ERROR);
        }

    }

    /**
     * Compute texture dimension depending on viewport and covered feature
     * collection
     * 
     * @return
     */
    private static Dimension computeTextureDimension(IEnvelope envelope,
            Viewport viewport, double dpiResolution) {
        int width = (int) (envelope.width() * Viewport.getMETERS_PER_PIXEL() * dpiResolution);
        int height = (int) (envelope.length() * Viewport.getMETERS_PER_PIXEL() * dpiResolution);
        return new Dimension(width, height);
    }
}
