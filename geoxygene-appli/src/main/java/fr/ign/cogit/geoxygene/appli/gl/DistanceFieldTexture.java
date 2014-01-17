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

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import fr.ign.cogit.geoxygene.appli.render.primitive.DistanceFieldParameterizer;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.TextureImage;
import fr.ign.cogit.geoxygene.util.gl.TextureImageUtil;

/**
 * @author JeT
 * 
 */
public class DistanceFieldTexture extends BasicTexture {

    private DistanceFieldParameterizer parameterizer = null;
    private boolean firstCall = true;
    private int distanceFieldTextureId = -1;

    /**
     * Constructor
     */
    public DistanceFieldTexture(DistanceFieldParameterizer parameterizer) {
        this.parameterizer = parameterizer;
    }

    /**
     * @return the parameterizer
     */
    public DistanceFieldParameterizer getParameterizer() {
        return this.parameterizer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.util.gl.BasicTexture#initializeRendering()
     */
    @Override
    public boolean initializeRendering() {
        if (this.firstCall) {
            this.getParameterizer().initializeParameterization();
            TextureImage uvMap = this.getParameterizer().getTextureImage();
            try {
                // save image for debug purpose only
                String generatedTextureFilename = this.generateTextureFilename();
                TextureImageUtil.saveHeight(uvMap, generatedTextureFilename);
                super.setTextureImage(this.fillFinalTexture(uvMap));

                glEnable(GL_TEXTURE_2D);
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                GL13.glActiveTexture(GL13.GL_TEXTURE4);
                this.distanceFieldTextureId = glGenTextures(); //Generate texture ID
                glBindTexture(GL_TEXTURE_2D, this.distanceFieldTextureId); //Bind texture ID

                //Setup wrap mode
                //      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                //      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
                //      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                //      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

                //Setup texture scaling filtering
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

                //Send texel data to OpenGL
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RG32F, uvMap.getWidth(), uvMap.getHeight(), 0, GL_RG, GL_FLOAT, TextureImageUtil.toFloatBuffer(uvMap));

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            GL13.glActiveTexture(GL13.GL_TEXTURE4);
            glBindTexture(GL_TEXTURE_2D, this.distanceFieldTextureId); //Bind texture ID
        }
        return super.initializeRendering();
    }

    /**
     * @param textureImage
     * @return
     */
    private BufferedImage fillFinalTexture(TextureImage textureImage) {
        BufferedImage textureToApply = null;
        try {
            textureToApply = GLTools.loadImage("./src/main/resources/textures/mer cassini.png");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //TextureImageUtil.blurDistance(textureImage, 10);
        return TextureImageUtil.applyTexture(textureImage, textureToApply);
        //return TextureImageUtil.toHeight(textureImage, Color.white, Color.black);
    }

    /**
     * Generate a unique texture filename
     */
    private String generateTextureFilename() {
        return this.getParameterizer().getFeature().getId() + String.valueOf(new Date().getTime());
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.util.gl.BasicTexture#finalizeRendering()
     */
    @Override
    public void finalizeRendering() {
        if (this.firstCall) {
            this.getParameterizer().finalizeParameterization();
            this.firstCall = false;
        }
        super.finalizeRendering();
    }

}
