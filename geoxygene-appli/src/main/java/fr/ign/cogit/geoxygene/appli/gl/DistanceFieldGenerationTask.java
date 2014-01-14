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

package fr.ign.cogit.geoxygene.appli.gl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import fr.ign.cogit.geoxygene.appli.GeOxygeneEventManager;
import fr.ign.cogit.geoxygene.appli.render.primitive.DistanceFieldParameterizer;
import fr.ign.cogit.geoxygene.appli.task.AbstractTask;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.appli.ui.Message.MessageType;
import fr.ign.cogit.geoxygene.util.gl.TextureImageUtil;

/**
 * @author JeT
 *         Asynchronous task generating a texture file from
 *         DistanceFieldParameterization. Once finished the
 *         generatedTextureImage
 *         is filled
 */
public class DistanceFieldGenerationTask extends AbstractTask {

    public static enum DistanceFieldVisualizationType {
        TEXTURE, // apply source texture to (u,v) coordinates  
        UV, // display U,V coordinates with a Color LUT
        HEIGHT, // display the V coordinates with a GRAY GRADIENT

    }

    private DistanceFieldParameterizer parameterizer = null;
    private BufferedImage generatedTextureImage = null;
    private String textureFilename = null;
    private DistanceFieldVisualizationType visualizationType = DistanceFieldVisualizationType.TEXTURE;

    /**
     * Constructor
     * 
     * @param name
     * @param parameterizer
     * @param textureFilename
     */
    public DistanceFieldGenerationTask(final String name, final DistanceFieldParameterizer parameterizer, final String textureFilename,
            DistanceFieldVisualizationType visualizationType) {
        super(name);
        if (parameterizer == null || textureFilename == null) {
            throw new IllegalArgumentException("parameterizer or texture filename cannot be null:" + parameterizer + " '" + textureFilename + "'");
        }
        this.parameterizer = parameterizer;
        this.textureFilename = textureFilename;
        this.visualizationType = visualizationType;
    }

    @Override
    public void run() {
        try {
            this.setState(TaskState.RUNNING);
            switch (this.visualizationType) {
            case HEIGHT:
                this.generatedTextureImage = TextureImageUtil.toHeight(this.parameterizer.getTextureImage(), Color.black, Color.white);
                break;
            case UV:
                this.generatedTextureImage = TextureImageUtil.toColors(this.parameterizer.getTextureImage(), Color.red, Color.blue, Color.white);
                break;
            case TEXTURE:
                BufferedImage sourceTextureImage = ImageIO.read(new File(this.textureFilename));
                this.generatedTextureImage = TextureImageUtil.applyTexture(this.parameterizer.getTextureImage(), sourceTextureImage);
                break;
            }
            this.setState(TaskState.FINISHED);

        } catch (Exception e) {
            e.printStackTrace();
            this.setError(e);
            GeOxygeneEventManager.getInstance().getApplication().getMainFrame().addMessage(MessageType.ERROR, e.getMessage());
            this.setState(TaskState.ERROR);
        }
    }

    /**
     * @return the generatedTextureImage
     */
    public BufferedImage getGeneratedTextureImage() {
        return this.generatedTextureImage;
    }

    /**
     * @return the parameterizer
     */
    public DistanceFieldParameterizer getParameterizer() {
        return this.parameterizer;
    }

    /**
     * @return the textureFilename
     */
    public String getTextureFilename() {
        return this.textureFilename;
    }

    @Override
    public boolean isProgressable() {
        return false;
    }

    @Override
    public boolean isPausable() {
        return false;
    }

    @Override
    public boolean isStoppable() {
        return false;
    }

}
