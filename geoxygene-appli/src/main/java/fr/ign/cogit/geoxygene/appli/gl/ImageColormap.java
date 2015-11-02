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

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL20.glUniform1i;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.util.gl.BasicTexture;

/**
 * 
 * @author AMasse This class is used to unify Categorize and Interpolate parts
 *         of RasterSymbolizer for RasterImage
 */

public class ImageColormap extends BasicTexture {
    // declarations
    @SuppressWarnings("unused")
    private static int TYPE_INTERPOLATE = 1;
    @SuppressWarnings("unused")
    private static int TYPE_CATEGORIZE = 2;

    private int typeColormap = -1;
    private int nbPoints = 0;
    private float[] value = null;
    private int[][] color = null;

    // Constructors
    public ImageColormap(int typeColormap, int nbPoints) {
        this.typeColormap = typeColormap;
        this.nbPoints = nbPoints;
        value = new float[nbPoints];
        color = new int[nbPoints][4];
    }

    // Accesssors
    // Getters
    public int getTypeColormap() {
        return typeColormap;
    }

    public int getNbPoints() {
        return nbPoints;
    }

    public float getValue(int iPoint) {
        return value[iPoint];
    }

    public float[] getValue() {
        return value;
    }

    public int getColor(int iPoint, int iBand) {
        return color[iPoint][iBand];
    }

    public int[] getColor(int iPoint) {
        return color[iPoint];
    }

    public int[][] getColor() {
        return color;
    }

    // Setters
    public void setTypeColormap(int typeColormap) {
        this.typeColormap = typeColormap;
    }

    public void setNbPoints(int nbPoints) {
        this.nbPoints = nbPoints;
    }

    public void setValue(int iPoint, float value) {
        this.value[iPoint] = value;
    }

    public void setValue(float[] values) {
        if (values.length != nbPoints) {
            // Not the same size of elements, reallocation
            // value = new float[values.length];
            // this.value = values;
            System.err.println("Pb in setting Colormap values");
        } else {
            // Ok
            this.value = values;
        }
    }

    public void setColor(int iPoint, int iBand, int color) {
        this.color[iPoint][iBand] = color;
    }

    // Be aware that it is not a real rendering
    // We use a fake texture to send colormap information to the shaher
    // There is no magic here, sorry
//    public boolean initializeRendering(int programId) {
//
//        // Enable GL texture
//        glEnable(GL_TEXTURE_2D);
//        // Go find imageID and pass buffer of data to GPU
//        Integer imageIndex = this.getTextureId();
//        // Very important, activate the texture Slot
//        GL13.glActiveTexture(this.getTextureId() + imageIndex);
//        glBindTexture(GL_TEXTURE_2D, imageIndex);
//
//        return true;
//    }

    /**
     * @return the generated texture id
     */
    @Override
    public final Integer getTextureId() {
        if (this.textureId < 0) {
            // Declaration and initialization
            int target = GL_TEXTURE_2D;
            int levels = 0; // MipMap disabled

            // We generate a texture ID
            this.textureId = glGenTextures();

            // We bind the texture
            glBindTexture(target, this.textureId);

            // Give the buffer to the GPU
            ByteBuffer bufferColormap = ByteBuffer.allocateDirect(nbPoints * 2 * 4 * 4);
            bufferColormap.order(ByteOrder.nativeOrder());

            // In order, we send first colors and then associated values
            for (int i = 0; i < nbPoints; i++) {
                for (int j = 0; j < 4; j++) {
                    bufferColormap.putFloat((float) color[i][j]);
                }
            }
            for (int i = 0; i < nbPoints; i++) {
                for (int j = 0; j < 4; j++) {
                    bufferColormap.putFloat((float) value[i]);
                }
            }
            bufferColormap.rewind();

            glTexImage2D(target, levels, GL30.GL_RGBA32F, nbPoints, 2, 0, GL11.GL_RGBA, GL11.GL_FLOAT, bufferColormap);

            // TODO : useful ?
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            // TODO : unload buffer, is it working ?
            bufferColormap.clear();

        }

        // Return the texture ID so we can bind it later again
        return this.textureId;
    }

    // Cemetery
    // public void setColor(int[][] color) {
    // if (color.length != nbPoints*4) {
    // // Not the same size of elements, reallocation
    // // value = new float[values.length];
    // // this.value = values;
    // System.err.println("Pb in setting Colormap colors");
    // } else {
    // // Ok
    // this.color = color;
    // }
    // }

}
