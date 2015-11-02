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

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.net.URL;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.appli.gl.BinaryGradientImage;
import fr.ign.cogit.geoxygene.appli.gl.BinaryGradientImage.GradientPixel;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;
import fr.ign.cogit.geoxygene.util.gl.GLTexture;

/**
 * @author JeT BinaryGradient texture returns the coordinates equal to the
 *         provided point. It contains a BinaryGradientImage and gives texture
 *         as a floating point texture to the GPU
 */
public class BinaryGradientTexture extends BasicTexture {

    private static final Logger logger = Logger
            .getLogger(BinaryGradientTexture.class.getName()); // logger

    public static final String gradientUVMinUniformVarname = "uvMinGradientTexture";
    public static final String gradientUVRangeUniformVarname = "uvRangeGradientTexture";
    private BinaryGradientImage binaryGradientImage = null;

    private String samplerUniformVarName;
    /**
     * Constructor
     */
    public BinaryGradientTexture() {
        super();
    }

    /**
     * Constructor with an image to read
     * 
     * @param _tex_url : the texture location
     */
    public BinaryGradientTexture(final URL _tex_url) {
        this();
        this.setTextureFilename(_tex_url);
    }
    /**
     * Constructor with an image in memory
     * 
     * @param binaryGradientImage
     */
    public BinaryGradientTexture(BinaryGradientImage binaryGradientImage) {
        this();
        this.binaryGradientImage = binaryGradientImage;
        this.textureId = -1;
    }

    /**
     * @return the textureSlot
     */
    public int getTextureSlot() {
        return this.textureSlot;
    }

    /**
     * @return the uniformVarName
     */
    public String getUniformVarName() {
        return this.samplerUniformVarName;
    }

    /**
     * @param textureSlot
     *            the textureSlot to set
     */
    public void setTextureSlot(String uniformVarName, int textureSlot) {
        this.samplerUniformVarName = uniformVarName;
        this.textureSlot = textureSlot;
    }

    /**
     * @return the generated texture id
     */
    public final Integer getTextureId() {
        if (this.textureId < 0) {
            GL13.glActiveTexture(this.textureSlot);
            BinaryGradientImage gradientImage = this.getBinaryGradientImage();

            int width = gradientImage.getWidth();
            int height = gradientImage.getHeight();
            // 16 = 4 floats (float = 4 bytes) = 16
            ByteBuffer buffer = BufferUtils.createByteBuffer(width * height
                    * 16);
            double uMin = gradientImage.getuMin();
            double vMin = gradientImage.getvMin();
            double uRange = gradientImage.getuMax() - gradientImage.getuMin();
            double vRange = gradientImage.getvMax() - gradientImage.getvMin();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    GradientPixel pixel = gradientImage.getPixel(x, y);
                    buffer.putFloat((float) ((pixel.uTexture - uMin) / uRange));
                    buffer.putFloat((float) ((pixel.vTexture - vMin) / vRange));
                    // System.err.println("uvTexture = "
                    // + (float) ((pixel.uTexture - uMin) / uRange) + "x"
                    // + (float) ((pixel.vTexture - vMin) / vRange));
                    if (pixel.vGradient != null) {
                        buffer.putFloat((float) pixel.vGradient.x);
                        buffer.putFloat((float) pixel.vGradient.y);
                    } else {
                        // TODO: !! compute gradient !!
                        buffer.putFloat(0f);
                        buffer.putFloat(0f);
                    }
                }
            }

            buffer.rewind();

            // You now have a ByteBuffer filled with the color data of each
            // pixel.
            // Now just create a texture ID and bind it. Then you can load it
            // using
            // whatever OpenGL method you want, for example:

            this.textureId = glGenTextures(); // Generate texture ID
            glBindTexture(GL_TEXTURE_2D, this.textureId); // Bind texture ID

            glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGBA32F, width, height, 0,
                    GL11.GL_RGBA, GL11.GL_FLOAT, buffer);
            // Setup texture scaling filtering
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            // Return the texture ID so we can bind it later again
        }
        return this.textureId;
    }


    /**
     * @return the textureImage
     */
    public final BinaryGradientImage getBinaryGradientImage() {
        return this.binaryGradientImage;
    }


    /**
     * @param _tex_url
     *            the url of the texture to set
     */
    public final void setTextureFilename(final URL _tex_url) {
        super.setTextureURL(_tex_url);
        this.textureId = -1;
        this.binaryGradientImage = null;
    }

    /**
     * initialize the texture rendering
     */
    @Override
    public boolean initializeRendering(int programId) {
        Integer texIndex = this.getTextureId();
        if (texIndex == null) {
            GL11.glDisable(GL_TEXTURE_2D);
            return false;
        }
        glEnable(GL_TEXTURE_2D);
        GL13.glActiveTexture(this.textureSlot);
        glBindTexture(GL_TEXTURE_2D, texIndex);
        // vMin has a special value '-1' = out of polygon
        // consider that vMin is always equal to zero
        float uMin = (float) (double) this.binaryGradientImage.getuMin();
        float vMin = 0;
        float uRange = (float) (this.binaryGradientImage.getuMax() - this.binaryGradientImage
                .getuMin());
        float vRange = (float) (double) (this.binaryGradientImage.getvMax());
        GL20.glUniform2f(GL20.glGetUniformLocation(programId,
                gradientUVMinUniformVarname), uMin, vMin);
        GL20.glUniform2f(GL20.glGetUniformLocation(programId,
                gradientUVRangeUniformVarname), uRange, vRange);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.gl.Texture#finalizeRendering()
     */
    @Override
    public void finalizeRendering() {

    }

    public void setTextureImage(BinaryGradientImage gradientImage) {
        this.binaryGradientImage = gradientImage;
        this.textureId = -1;
        // this.textureId = GLTools.loadTexture(this.textureImage);

    }

}
