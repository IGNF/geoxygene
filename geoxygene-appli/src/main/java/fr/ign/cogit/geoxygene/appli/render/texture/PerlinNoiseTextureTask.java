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

import java.awt.image.BufferedImage;
import java.net.URI;

import org.apache.log4j.Logger;

import com.jhlabs.image.LinearColormap;
import com.jhlabs.image.TextureFilter;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.texture.PerlinNoiseTexture;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;

/**
 * @author JeT
 * 
 */
public class PerlinNoiseTextureTask extends AbstractTextureTask<BasicTexture> {

    private static final Logger logger = Logger
            .getLogger(PerlinNoiseTextureTask.class.getName()); // logger

    private IFeatureCollection<IFeature> featureCollection = null;
    private Viewport viewport = null;
    private int width = 0;
    private int height = 0;

    private PerlinNoiseTexture textureDescriptor = null;
    private BasicTexture basicTexture = null;

    /**
     * @param texture
     */
    public PerlinNoiseTextureTask(URI identifier,
            PerlinNoiseTexture descriptor,
            IFeatureCollection<IFeature> featureCollection) {
        super("PerlinNoise" + identifier);
        this.textureDescriptor = descriptor;
        this.basicTexture = new BasicTexture();
//        this.setViewport(viewport);
        this.setFeatureCollection(featureCollection);
        this.computeTextureDimension(featureCollection.envelope(),
                this.getTextureDescriptor().getTextureResolution());
        this.setNeedCaching(true);
        this.id = identifier;
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
     * @return the textureDescriptor
     */
    public PerlinNoiseTexture getTextureDescriptor() {
        return this.textureDescriptor;
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
            filter.setColormap(new LinearColormap(this.getTextureDescriptor()
                    .getColor1().getRGB(), this.getTextureDescriptor()
                    .getColor2().getRGB()));
            filter.setScale(this.getTextureDescriptor().getScale());
            filter.setStretch(this.getTextureDescriptor().getStretch());
            filter.setAmount(this.getTextureDescriptor().getAmount());
            filter.setAngle(this.getTextureDescriptor().getAngle());
            this.getTexture().createTextureImage(this.width, this.height);
            // logger.debug("set perlin texture image size: "
            // + this.getTexture().getTextureWidth() + "x"
            // + this.getTexture().getTextureHeight());
            BufferedImage imgTexture = this.getTexture().getTextureImage();
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
    private void computeTextureDimension(IEnvelope envelope,
            double dpiResolution) {
        if (envelope == null) {
            throw new IllegalArgumentException("envelope is not set");
        }
        this.width = (int) (envelope.width() * Viewport.getMETERS_PER_PIXEL() * dpiResolution);
        this.height = (int) (envelope.length() * Viewport.getMETERS_PER_PIXEL() * dpiResolution);
    }

    @Override
    public int getTextureWidth() {
        return this.width;
    }

    @Override
    public int getTextureHeight() {
        return this.height;
    }

    @Override
    public BasicTexture getTexture() {
        return this.basicTexture;
    }

    @Override
    public void setID(URI identifier) {
        // TODO Auto-generated method stub
        
    }

}
