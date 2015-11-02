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

// GL import
import org.lwjgl.opengl.GL11;
//import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
//import org.lwjgl.opengl.GL44;

// Specific GL import
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL20.glUniform1i;

import java.net.MalformedURLException;
import java.net.URL;
// Java import
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.awt.Color;
//import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferDouble;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

// Geotools import
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.processing.Operation2D;
import org.geotools.coverage.processing.operation.*;

// Intern
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;

/**
 * @author amasse Raster image Do some cool stuff with Raster Image Implemented
 *         : Opacity, Colormap, ChannelSelection
 */
public class RasterImage extends BasicTexture {

    private String imageDataType = null;
    private int GLinternalFormat = GL11.GL_RGBA;
    private int GLinternalDataType = GL11.GL_UNSIGNED_BYTE;
    private int format = GL11.GL_RGB;

    private boolean isRead = false;

    private int width = 0;
    private int height = 0;
    private int nbBands = 0;
    private int nbBandsSelected = 0;
    private int[] bandsSelected = null;
    private int size = 0; // width * height

    private ByteBuffer bufferImage;

    // colormap
    private boolean defColormap = false;
    private ImageColormap imageColormap = null;

    // animation
    private int animate = 0;

    /**
     * Constructor
     */
    public RasterImage() {
        super();
    }

    /**
     * Constructor with an image to read
     * 
     * @param textureFilename
     */
    public RasterImage(final String imageFilename) {
        this();
        try {
            super.setTextureURL(new URL(imageFilename));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the width
     */
    public int getNbBands() {
        return this.nbBands;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * @return the size (width*height*nbBands)
     */
    public int getSize() {
        return this.size;
    }

    /**
     * @return the generated texture id
     */

    protected final Integer getImageId() {
        if (this.textureId < 0) {
            // Declaration and initialization
            int target = GL_TEXTURE_2D;
            int levels = 0; // MipMap disabled

            // We generate a texture ID
            this.textureId = glGenTextures();

            // We bind the texture
            glBindTexture(target, this.textureId);

            // Give the buffer to the GPU
            glTexImage2D(target, levels, GLinternalFormat, width, height, 0, format, GLinternalDataType, bufferImage);

            // TODO : MipMap ?
            // glGenerateMipmap(GL_TEXTURE_2D);
            // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
            // GL_LINEAR_MIPMAP_LINEAR);

            // Define the rule for image rendering between 2 pixels
            // Be careful, GL_Linear is not always the solution
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
            // GL11.GL_NEAREST);
            // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,
            // GL11.GL_NEAREST);

            // TODO : unload buffer, is it working ?
            bufferImage.clear();
            // Too soon ?
            // GL44.glClearTexImage(target, levels, format, GLinternalDataType,
            // bufferImage);
        }

        // Return the texture ID so we can bind it later again
        return this.textureId;
    }

    public boolean getDefColormap() {
        return defColormap;
    }

    /**
     * initialize the texture rendering
     * 
     * @param programId
     */

    public boolean initializeRendering(int programId) {

        // Enable GL texture
//        glEnable(GL_TEXTURE_2D);

        // Go find imageID and pass buffer of data to GPU
        Integer imageIndex = this.getImageId(); 

        // Very important, activate the texture Slot
        GL13.glActiveTexture(this.getTextureSlot());
        // Send the uniform to shader and bind it
//        glUniform1i(GL20.glGetUniformLocation(programId, "bufferImage"), imageIndex);
        glBindTexture(GL_TEXTURE_2D, imageIndex);

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.gl.Texture#finalizeRendering()
     */

    public void finalizeRendering() {
        // nope
        // GL11.glDeleteTextures(getImageId());
    }

    /**
     * read a raster Image
     * 
     * @param image
     * @param symbolizer
     */
    public void readImage(GridCoverage2D image, RasterSymbolizer symbolizer) {
        System.out.println("READING THE RASTER");
        // To avoid multiple read
        if ((isRead == false) || (symbolizer.getChannelSelection() != null)) // &&(imageFilename
                                                                             // !=
                                                                             // null))
        {
            // Declaration and initialization
            // Set width, height, nbBands and compute size
            width = image.getRenderedImage().getWidth();
            height = image.getRenderedImage().getHeight();
            nbBands = image.getNumSampleDimensions();
            // size of the input image !!
            size = width * height * nbBands;
            // Determine data type of image
            imageDataType = image.getSampleDimension(0).getSampleDimensionType().name();

            // Channel Selection
            // TODO : VALIDATION of channel selection from symbolizer
            selectBands(image, symbolizer);

            // Determine the format
            format = generateFormatFromNbBandSelected();

            // Determine the data type
            GLinternalDataType = generateGLinternalDataTypeFromImageDataType();

            // Automatically detect the best GLinternalFormat
            GLinternalFormat = generateOptimalGLinternalFormat();

            // Buffer allocation and acquisition
            // INFO : bufferImage.order(ByteOrder.nativeOrder()); // to adjust
            // the ByteBuffer instance's endianness to match the current
            // platform.
            if ((GLinternalDataType == GL11.GL_BYTE) || (GLinternalDataType == GL11.GL_UNSIGNED_BYTE)) {
                byte[] byteData = ((DataBufferByte) image.getRenderedImage().getData().getDataBuffer()).getData();
                bufferImage = ByteBuffer.allocateDirect(byteData.length);
                bufferImage.order(ByteOrder.nativeOrder());
                bufferImage.put(ByteBuffer.wrap(byteData));
            } else if ((GLinternalDataType == GL11.GL_SHORT) || (GLinternalDataType == GL11.GL_UNSIGNED_SHORT)) {
                short[] shortData = ((DataBufferShort) image.getRenderedImage().getData().getDataBuffer()).getData();
                bufferImage = ByteBuffer.allocateDirect(shortData.length * 2);
                bufferImage.order(ByteOrder.nativeOrder());
                bufferImage.asShortBuffer().put(ShortBuffer.wrap(shortData));
            } else if ((GLinternalDataType == GL11.GL_INT) || (GLinternalDataType == GL11.GL_UNSIGNED_INT)) {
                int[] intData = ((DataBufferInt) image.getRenderedImage().getData().getDataBuffer()).getData();
                bufferImage = ByteBuffer.allocateDirect(intData.length * 4);
                bufferImage.order(ByteOrder.nativeOrder());
                bufferImage.asIntBuffer().put(IntBuffer.wrap(intData));
            } else if (GLinternalDataType == GL11.GL_FLOAT) {
                float[] floatData = ((DataBufferFloat) image.getRenderedImage().getData().getDataBuffer()).getData();
                bufferImage = ByteBuffer.allocateDirect(floatData.length * 4);
                bufferImage.order(ByteOrder.nativeOrder());
                bufferImage.asFloatBuffer().put(FloatBuffer.wrap(floatData));
            } else if (GLinternalDataType == GL11.GL_DOUBLE) {
                double[] doubleData = ((DataBufferDouble) image.getRenderedImage().getData().getDataBuffer()).getData();
                bufferImage = ByteBuffer.allocateDirect(doubleData.length * 8);
                bufferImage.order(ByteOrder.nativeOrder());
                bufferImage.asDoubleBuffer().put(DoubleBuffer.wrap(doubleData));
            } else {
                System.err.println("This type of data : " + GLinternalDataType + "is not recognized.");
            }

            // Rewind the buffer, not very with our way-to-put but why not
            bufferImage.rewind();

            // Now, bufferImage is ok, reading is complete
            isRead = true;

            // TEMP ?? animation
            animate = symbolizer.getAnimate();
        }
    }

    private int generateGLinternalDataTypeFromImageDataType() {
        if (imageDataType == "REAL_32BITS") {
            return GL11.GL_FLOAT;
        } else if (imageDataType == "REAL_64BITS") {
            return GL11.GL_DOUBLE;
        } else if (imageDataType == "SIGNED_8BITS") {
            return GL11.GL_BYTE;
        } else if (imageDataType == "SIGNED_16BITS") {
            return GL11.GL_SHORT;
        } else if (imageDataType == "SIGNED_32BITS") {
            return GL11.GL_INT;
        } else if (imageDataType == "UNSIGNED_1BIT") {
            System.err.println("This type of data : " + imageDataType + "is not implemented.");
            return -1; /* not done */
        } else if (imageDataType == "UNSIGNED_2BITS") {
            System.err.println("This type of data : " + imageDataType + "is not implemented.");
            return -1; /* not done */
        } else if (imageDataType == "UNSIGNED_4BITS") {
            System.err.println("This type of data : " + imageDataType + "is not implemented.");
            return -1; /* not done */
        } else if (imageDataType == "UNSIGNED_8BITS") {
            return GL11.GL_UNSIGNED_BYTE;
        } else if (imageDataType == "UNSIGNED_16BITS") {
            return GL11.GL_UNSIGNED_SHORT;
        } else if (imageDataType == "UNSIGNED_32BITS") {
            return GL11.GL_UNSIGNED_INT;
        } else {
            System.err.println("This type of data : " + imageDataType + "is not recognized.");
            return -1;
        }
    }

    private int generateFormatFromNbBandSelected() {
        if (nbBandsSelected == 1) {
            return GL11.GL_RED;
        } else if (nbBandsSelected == 2) {
            return GL30.GL_RG;
        } else if (nbBandsSelected == 3) {
            return GL11.GL_RGB;
        } else if (nbBandsSelected == 4) {
            return GL11.GL_RGBA;
        } else {
            /* nothing */
            return -1;
        }
    }

    private void selectBands(GridCoverage2D image, RasterSymbolizer symbolizer) {
        // Select bands of image from rastersymbolizer
        if (symbolizer.getChannelSelection() != null) {
            // TODO : not working yet:
            // We must read again the initial image and select the bands we want

            if (symbolizer.getChannelSelection().isGrayChannel()) {
                nbBandsSelected = 1;
                bandsSelected = new int[nbBandsSelected];
                // INFO : -1 is for conversion to 1...n to 0...n-1
                // representation
                bandsSelected[0] = symbolizer.getChannelSelection().getGrayChannel().getSourceChannelName() - 1;
            } else if (symbolizer.getChannelSelection().isRGBChannels()) {
                nbBandsSelected = 3;
                bandsSelected = new int[nbBandsSelected];
                bandsSelected[0] = symbolizer.getChannelSelection().getRedChannel().getSourceChannelName() - 1;
                bandsSelected[1] = symbolizer.getChannelSelection().getGreenChannel().getSourceChannelName() - 1;
                bandsSelected[2] = symbolizer.getChannelSelection().getBlueChannel().getSourceChannelName() - 1;
            }
        } else {
            // no explicit ChannelSelection in the SLD, default one !
            switch (nbBands) {
            case 1:
                nbBandsSelected = 1;
                bandsSelected = new int[nbBandsSelected];
                bandsSelected[0] = 0;
                break;
            case 2:
                nbBandsSelected = 2;
                bandsSelected = new int[nbBandsSelected];
                bandsSelected[0] = 0;
                bandsSelected[1] = 1;
                break;
            case 3:
                nbBandsSelected = 3;
                bandsSelected = new int[nbBandsSelected];
                bandsSelected[0] = 0;
                bandsSelected[1] = 1;
                bandsSelected[2] = 2;
                break;
            case 4:
                nbBandsSelected = 4;
                bandsSelected = new int[nbBandsSelected];
                bandsSelected[0] = 0;
                bandsSelected[1] = 1;
                bandsSelected[2] = 2;
                bandsSelected[3] = 3;
                break;
            default:
                nbBandsSelected = 3;
                bandsSelected = new int[nbBandsSelected];
                bandsSelected[0] = 0;
                bandsSelected[1] = 1;
                bandsSelected[2] = 2;
            }
        }
    }

    /*
     * Use this function to generate the best GLinternalFormat and thus optimize
     * GPU memory not easy to do better, because of the GL**.GL_*_*
     * specification, especially the GL version
     */
    private int generateOptimalGLinternalFormat() {
        if (defColormap) {
            // With a colormap, we must keep pixel information in the shader,
            // only solution, give float to shader
            if (nbBandsSelected == 1)
                return GL30.GL_R32F;
            else if (nbBandsSelected == 2)
                return GL30.GL_RG32F;
            else if (nbBandsSelected == 3)
                return GL30.GL_RGB32F;
            else
                return GL30.GL_RGBA32F;
        } else if (GLinternalDataType == GL11.GL_BYTE) {
            if (nbBandsSelected == 1)
                return GL30.GL_R8;
            else if (nbBandsSelected == 2)
                return GL30.GL_RG8;
            else if (nbBandsSelected == 3)
                return GL11.GL_RGB8;
            else
                return GL11.GL_RGBA8;
        } else if (GLinternalDataType == GL11.GL_SHORT) {
            if (nbBandsSelected == 1)
                return GL30.GL_R16;
            else if (nbBandsSelected == 2)
                return GL30.GL_RG16;
            else if (nbBandsSelected == 3)
                return GL11.GL_RGB16;
            else
                return GL11.GL_RGBA16;
        } else if (GLinternalDataType == GL11.GL_INT) {
            if (nbBandsSelected == 1)
                return GL30.GL_R32I;
            else if (nbBandsSelected == 2)
                return GL30.GL_RG32I;
            else if (nbBandsSelected == 3)
                return GL30.GL_RGB32I;
            else
                return GL30.GL_RGBA32I;
        } else if (GLinternalDataType == GL11.GL_FLOAT) {
            if (nbBandsSelected == 1)
                return GL30.GL_R32F;
            else if (nbBandsSelected == 2)
                return GL30.GL_RG32F;
            else if (nbBandsSelected == 3)
                return GL30.GL_RGB32F;
            else
                return GL30.GL_RGBA32F;
        } else if (GLinternalDataType == GL11.GL_DOUBLE) {
            // TODO : check that
            if (nbBandsSelected == 1)
                return GL30.GL_R32F;
            else if (nbBandsSelected == 2)
                return GL30.GL_RG32F;
            else if (nbBandsSelected == 3)
                return GL30.GL_RGB32F;
            else
                return GL30.GL_RGBA32F;
        } else if (GLinternalDataType == GL11.GL_UNSIGNED_BYTE) {
            // TODO check that too
            if (nbBandsSelected == 1)
                return GL30.GL_R8;
            else if (nbBandsSelected == 2)
                return GL30.GL_RG8;
            else if (nbBandsSelected == 3)
                return GL11.GL_RGB8;
            else
                return GL11.GL_RGBA8;
        } else if (GLinternalDataType == GL11.GL_UNSIGNED_SHORT) {
            if (nbBandsSelected == 1)
                return GL30.GL_R16;
            else if (nbBandsSelected == 2)
                return GL30.GL_RG16;
            else if (nbBandsSelected == 3)
                return GL11.GL_RGB16;
            else
                return GL11.GL_RGBA16;
        } else if (GLinternalDataType == GL11.GL_UNSIGNED_INT) {
            if (nbBandsSelected == 1)
                return GL30.GL_R32UI;
            else if (nbBandsSelected == 2)
                return GL30.GL_RG32UI;
            else if (nbBandsSelected == 3)
                return GL30.GL_RGB32UI;
            else
                return GL30.GL_RGBA32UI;
        } else {
            System.err.println("generateOptimalGLinternalFormat() failed to find the correct format");
            return -1; // nothing to do here with that
        }
    }

    public void readColormap(RasterSymbolizer rasterSymbolizer) {
        //
        if (rasterSymbolizer.getColorMap() != null) {
            defColormap = true;
            if (rasterSymbolizer.getColorMap().getInterpolate() != null) {
                // Case of "Interpolate" code 1
                // TODO : sort the list of points

                int nb_points = rasterSymbolizer.getColorMap().getInterpolate().getNbInterpolationPoint();
                imageColormap = new ImageColormap(1, nb_points);
                for (int i = 0; i < nb_points; i++) {
                    imageColormap.setValue(i, (float) rasterSymbolizer.getColorMap().getInterpolate().getInterpolationPoint().get(i).getData());
                    Color color = rasterSymbolizer.getColorMap().getInterpolate().getInterpolationPoint().get(i).getColor();
                    imageColormap.setColor(i, 0, color.getRed());
                    imageColormap.setColor(i, 1, color.getGreen());
                    imageColormap.setColor(i, 2, color.getBlue());
                    imageColormap.setColor(i, 3, color.getAlpha());
                }
            } else if (rasterSymbolizer.getColorMap().getCategorize() != null) {
                // Case of "Categorize" code 2
                int nb_points = rasterSymbolizer.getColorMap().getCategorize().getNbCategorizePoint();
                imageColormap = new ImageColormap(2, nb_points);
                for (int i = 0; i < nb_points; i++) {
                    imageColormap.setValue(i, (float) rasterSymbolizer.getColorMap().getCategorize().getThreshold(i));
                    Color color = rasterSymbolizer.getColorMap().getCategorize().getColor(i);
                    imageColormap.setColor(i, 0, color.getRed());
                    imageColormap.setColor(i, 1, color.getGreen());
                    imageColormap.setColor(i, 2, color.getBlue());
                    imageColormap.setColor(i, 3, color.getAlpha());
                }
            } else if (rasterSymbolizer.getColorMap().getIntervals() != null) {
                // Case of "Interpolate" code 3
                int nb_points = rasterSymbolizer.getColorMap().getIntervals().getNbIntervalsPoint();
                imageColormap = new ImageColormap(3, nb_points);
                for (int i = 0; i < nb_points; i++) {
                    imageColormap.setValue(i, (float) rasterSymbolizer.getColorMap().getIntervals().getIntervalsPoint().get(i).getData());
                    Color color = rasterSymbolizer.getColorMap().getIntervals().getIntervalsPoint().get(i).getColor();
                    imageColormap.setColor(i, 0, color.getRed());
                    imageColormap.setColor(i, 1, color.getGreen());
                    imageColormap.setColor(i, 2, color.getBlue());
                    imageColormap.setColor(i, 3, color.getAlpha());
                }
            } else {
                // colormap defined without categorize or interpolate section
                defColormap = false;
            }
        } else {
            // No colormap defined
            defColormap = false;
        }
    }

    public ImageColormap getImageColorMap() {
        return imageColormap;
    }

    public int getAnimate() {
        return animate;
    }
}