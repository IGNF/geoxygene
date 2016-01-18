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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.texture.SimpleTexture;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;

/**
 * @author JeT
 * 
 */
public class BasicTextureTask extends AbstractTextureTask<BasicTexture> {

    // The built texture
    private BasicTexture basicTexture = null;
    private SimpleTexture tex_descriptor = null;

    private static final Logger logger = Logger.getLogger(BasicTextureTask.class.getName()); // logger

    public BasicTextureTask(URI texture_identifier, SimpleTexture _tex_descriptor) {
        super("Basic" + texture_identifier);
        this.basicTexture = new BasicTexture();
        this.tex_descriptor = _tex_descriptor;
        this.id = texture_identifier;
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
            this.basicTexture.setTextureURL(this.tex_descriptor.getLocation());
            this.getTexture().setTextureImage(ImageIO.read(this.tex_descriptor.getLocation()));
            this.setNeedCaching(false);
            this.setState(TaskState.FINISHED);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            this.setState(TaskState.ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            this.setState(TaskState.ERROR);
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
        return "BasicTextureTask [Texture Identifier=" + this.getName() + ", basicTexture=" + this.basicTexture + ", toString()=" + super.toString() + "]";
    }

}
