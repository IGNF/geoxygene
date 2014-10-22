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
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.texture.BasicTextureDescriptor;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;

/**
 * @author JeT
 * 
 */
public class BasicTextureTask extends AbstractTextureTask<BasicTexture> {

    // texture descriptor (from style)
    private BasicTextureDescriptor textureDescriptor = null;
    private BasicTexture basicTexture = null;

    private static final Logger logger = Logger
            .getLogger(BasicTextureTask.class.getName()); // logger

    /**
     * @param texture
     */
    public BasicTextureTask(String name,
            BasicTextureDescriptor textureDescriptor) {
        super("Basic" + name);
        this.textureDescriptor = textureDescriptor;
        this.basicTexture = new BasicTexture();
    }

    /**
     * @param texture
     */
    public BasicTextureTask(String name, File file) {
        super("Basic" + name);
        this.textureDescriptor = null;
        this.basicTexture = new BasicTexture(file.getAbsolutePath());
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
        this.setState(TaskState.WAITING);
        this.setState(TaskState.INITIALIZING);
        this.setState(TaskState.RUNNING);
        try {
            URL inputURL = null;
            if (this.textureDescriptor != null) {
                inputURL = new URL(this.textureDescriptor.getUrl());
            } else if (this.basicTexture.getTextureFilename() != null) {
                inputURL = new File(this.basicTexture.getTextureFilename())
                        .toURI().toURL();
            } else {
                logger.error("No valid image description for basic texture: "
                        + this.toString());
            }
            logger.debug("Reading file " + inputURL);
            this.getTexture().setTextureImage(ImageIO.read(inputURL));
            this.setNeedWriting(false);
            this.setState(TaskState.FINISHED);
        } catch (Exception e) {
            this.setNeedWriting(false);
            this.setError(e);
            this.setState(TaskState.ERROR);
            e.printStackTrace();
        }
    }

    @Override
    public int getTextureWidth() {
        return this.getTexture().getTextureWidth();
    }

    @Override
    public int getTextureHeight() {
        return this.getTexture().getTextureHeight();
    }

    @Override
    public BasicTexture getTexture() {
        return this.basicTexture;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BasicTextureTask [textureDescriptor=" + this.textureDescriptor
                + ", basicTexture=" + this.basicTexture + ", toString()="
                + super.toString() + "]";
    }

}
