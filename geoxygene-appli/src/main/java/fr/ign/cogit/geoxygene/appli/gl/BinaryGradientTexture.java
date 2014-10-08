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
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.appli.gl.BinaryGradientImage.GradientPixel;
import fr.ign.cogit.geoxygene.util.gl.Texture;

/**
 * @author JeT BinaryGradient texture returns the coordinates equal to the
 *         provided point. It contains a BinaryGradientImage and gives texture
 *         as a floating point texture to the GPU
 */
public class BinaryGradientTexture implements Texture {

    private static final Logger logger = Logger
            .getLogger(BinaryGradientTexture.class.getName()); // logger

    private int textureId = -1;
    private int textureSlot = GL13.GL_TEXTURE0;
    private String textureFilename = null;
    private BinaryGradientImage binaryGradientImage = null;
    private double scaleX, scaleY;

    private String uniformVarName;

    // private double minX = 0; // range of point coordinates in world space
    // private double maxX = 1; // range of point coordinates in world space
    // private double minY = 0; // range of point coordinates in world space
    // private double maxY = 1; // range of point coordinates in world space

    /**
     * Constructor
     */
    public BinaryGradientTexture() {
        this.scaleX = 1.;
        this.scaleY = 1.;
    }

    /**
     * Constructor with an image to read
     * 
     * @param textureFilename
     */
    public BinaryGradientTexture(final String textureFilename) {
        this();
        this.setTextureFilename(textureFilename);
    }

    // /**
    // * Constructor with image dimension
    // *
    // */
    // public BasicTexture(final int width, final int height) {
    // this();
    // this.createTextureImage(width, height);
    // }

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
        return this.uniformVarName;
    }

    /**
     * @return the scaleX
     */
    @Override
    public double getScaleX() {
        return this.scaleX;
    }

    /**
     * @param scaleX
     *            the scaleX to set
     */
    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
    }

    /**
     * @return the scaleY
     */
    @Override
    public double getScaleY() {
        return this.scaleY;
    }

    /**
     * @param scaleY
     *            the scaleY to set
     */
    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
    }

    /**
     * @param textureSlot
     *            the textureSlot to set
     */
    public void setTextureSlot(String uniformVarName, int textureSlot) {
        this.uniformVarName = uniformVarName;
        this.textureSlot = textureSlot;
    }

    /**
     * @return the generated texture id
     */
    protected final Integer getTextureId() {
        if (this.textureId < 0) {
            GL13.glActiveTexture(this.textureSlot);
            BinaryGradientImage gradientImage = this.getBinaryGradientImage();

            int width = gradientImage.getWidth();
            int height = gradientImage.getHeight();
            ByteBuffer buffer = BufferUtils
                    .createByteBuffer(width * height * 4);
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++) {
                    GradientPixel pixel = gradientImage.getPixel(x, y);
                    buffer.putFloat((float) pixel.uTexture);
                    buffer.putFloat((float) pixel.vTexture);
                    // !! compute tangent !!
                    buffer.putFloat((float) 0f);
                    buffer.putFloat((float) 0f);
                }

            buffer.rewind();

            // You now have a ByteBuffer filled with the color data of each
            // pixel.
            // Now just create a texture ID and bind it. Then you can load it
            // using
            // whatever OpenGL method you want, for example:

            this.textureId = glGenTextures(); // Generate texture ID
            glBindTexture(GL_TEXTURE_2D, textureId); // Bind texture ID

            glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGBA32F, width, height, 0,
                    GL11.GL_RGBA, GL11.GL_FLOAT, buffer);

            // Setup texture scaling filtering
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            // Send texel data to OpenGL
            glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_R32F, width, height, 0,
                    GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            // Return the texture ID so we can bind it later again
        }
        return this.textureId;
    }

    /**
     * @return the textureFilename
     */
    public final String getTextureFilename() {
        return this.textureFilename;
    }

    /**
     * @return the textureImage
     */
    public final BinaryGradientImage getBinaryGradientImage() {
        return this.binaryGradientImage;
    }

    /**
     * @return the texture image width (in pixels)
     */
    @Override
    public final int getTextureWidth() {
        if (this.binaryGradientImage == null) {
            return 0;
        }
        return this.binaryGradientImage.getWidth();
    }

    /**
     * @return the texture image height (in pixels)
     */
    @Override
    public final int getTextureHeight() {
        if (this.binaryGradientImage == null) {
            return 0;
        }
        return this.binaryGradientImage.getHeight();
    }

    /**
     * @param textureFilename
     *            the textureFilename to set
     */
    public final void setTextureFilename(final String textureFilename) {
        this.textureFilename = textureFilename;
        this.textureId = -1;
        this.binaryGradientImage = null;
    }

    /**
     * initialize the texture rendering
     */
    @Override
    public boolean initializeRendering() {
        Integer texIndex = this.getTextureId();
        if (texIndex == null) {
            GL11.glDisable(GL_TEXTURE_2D);
            return false;
        }
        glEnable(GL_TEXTURE_2D);
        glEnable(GL11.GL_BLEND);
        GL13.glActiveTexture(this.textureSlot);
        glBindTexture(GL_TEXTURE_2D, texIndex);
        return true;
    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see
    // * fr.ign.cogit.geoxygene.appli.gl.Texture#vertexCoordinates(javax.vecmath
    // * .Point2d)
    // */
    // @Override
    // public Point2d vertexCoordinates(final Point2d p) {
    // return this.vertexCoordinates(p.x, p.y);
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see fr.ign.cogit.geoxygene.appli.gl.Texture#vertexCoordinates(double,
    // * double)
    // */
    // @Override
    // public Point2d vertexCoordinates(final double x, final double y) {
    // Point2d p = new Point2d((x - this.minX) / (this.maxX - this.minX), (y -
    // this.minY) / (this.maxY - this.minY));
    // System.err.println("return vertex coordinate(" + x + "," + y + ") = " +
    // p);
    // return p;
    // }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.gl.Texture#finalizeRendering()
     */
    @Override
    public void finalizeRendering() {

    }

    // @Override
    // public void setRange(final double xmin, final double ymin, final double
    // xmax, final double ymax) {
    // this.minX = xmin;
    // this.maxX = xmax;
    // this.minY = ymin;
    // this.maxY = ymax;
    // }
    //
    // /**
    // * @return the minX
    // */
    // @Override
    // public double getMinX() {
    // return this.minX;
    // }
    //
    // /**
    // * @return the maxX
    // */
    // @Override
    // public double getMaxX() {
    // return this.maxX;
    // }
    //
    // /**
    // * @return the minY
    // */
    // @Override
    // public double getMinY() {
    // return this.minY;
    // }
    //
    // /**
    // * @return the maxY
    // */
    // @Override
    // public double getMaxY() {
    // return this.maxY;
    // }

    public void setTextureImage(BinaryGradientImage gradientImage) {
        this.binaryGradientImage = gradientImage;
        this.textureId = -1;
        // this.textureId = GLTools.loadTexture(this.textureImage);

    }

}
