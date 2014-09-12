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
package fr.ign.cogit.geoxygene.util.gl;

import static org.lwjgl.opengl.GL11.GL_FALSE;
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
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glShaderSource;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.vecmath.Point2d;

import org.apache.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.Util;
import org.lwjgl.util.Color;

/**
 * tool static class for GL
 * 
 * @author JeT
 * 
 */
public final class GLTools {

    private static final Random randomSeed = new Random(new Date().getTime());
    private static final Logger logger = Logger.getLogger(GLTools.class
            .getName()); // logger
    private static final Map<BufferedImage, Integer> existingTextureIDs = new HashMap<BufferedImage, Integer>();
    public static Map<Integer, String> shaderTypeNames = new HashMap<Integer, String>();
    static {
        shaderTypeNames.put(GL20.GL_FRAGMENT_SHADER, "GL_FRAGMENT_SHADER");
        shaderTypeNames.put(GL20.GL_VERTEX_SHADER, "GL_VERTEX_SHADER");
        shaderTypeNames.put(GL32.GL_GEOMETRY_SHADER, "GL_GEOMETRY_SHADER");
        shaderTypeNames.put(GL43.GL_COMPUTE_SHADER, "GL_COMPUTE_SHADER");
        shaderTypeNames.put(GL40.GL_TESS_EVALUATION_SHADER,
                "GL_TESS_EVALUATION_SHADER");
        shaderTypeNames.put(GL40.GL_TESS_CONTROL_SHADER,
                "GL_TESS_CONTROL_SHADER");
    }

    /**
     * Private constructor
     */
    private GLTools() {
        // utility class
    }

    /**
     * Display the content of a float buffer
     * 
     * @param buffer
     */
    public static void displayBuffer(FloatBuffer buffer) {
        buffer.mark();
        System.err.println("Float buffer limit = " + buffer.limit()
                + " capacity = " + buffer.capacity() + " position = "
                + buffer.position());
        for (int i = 0; i < buffer.limit(); i++) {
            float value = buffer.get(i);
            System.err.println("\t#" + i + ":" + value);
        }
        buffer.reset();
    }

    /**
     * Display the content of a float buffer
     * 
     * @param buffer
     */
    public static void displayBuffer(IntBuffer buffer) {
        buffer.mark();
        System.err.println("Int buffer limit = " + buffer.limit()
                + " capacity = " + buffer.capacity() + " position = "
                + buffer.position());
        for (int i = 0; i < buffer.limit(); i++) {
            int value = buffer.get(i);
            System.err.println("\t#" + i + ":" + value);
        }
        buffer.reset();
    }

    /**
     * check GL errors. Log an error if one occurred
     */
    public static boolean glCheckError() {
        try {
            Util.checkGLError();
            return true;
        } catch (OpenGLException e) {
            logger.error("GL error [" + e.getClass().getSimpleName() + "] "
                    + e.getMessage());
            return false;
        }
    }

    /**
     * check GL errors. Log an error if one occurred
     * 
     * @return true if no error
     */
    public static boolean glCheckError(String msg) {
        try {
            Util.checkGLError();
            return true;
        } catch (OpenGLException e) {
            logger.error(msg + " GL error [" + e.getClass().getSimpleName()
                    + "] " + e.getMessage());
            Thread.dumpStack();
            return false;
        }
    }

    /**
     * set gl texture coordinate from a Point2D point
     */
    public static void glTexCoord(final Point2d p) {
        GL11.glTexCoord2d(p.x, p.y);
    }

    /**
     * set gl vertex coordinate from a Point2D point
     */
    public static void glVertex(final Point2d p) {
        GL11.glVertex2d(p.x, p.y);
    }

    /**
     * set gl color from a LWJGL Color object
     */
    public static void glColor(final Color color) {
        GL11.glColor4d(color.getRed() / 255., color.getGreen() / 255.,
                color.getBlue() / 255., color.getAlpha() / 255.);
    }

    /**
     * set gl color from an AWT Color object
     */
    public static void glColor(final java.awt.Color color) {
        GL11.glColor4d(color.getRed() / 255., color.getGreen() / 255.,
                color.getBlue() / 255., color.getAlpha() / 255.);

    }

    /**
     * set gl clear color from a LWJGL Color object
     */
    public static void glClear(final Color color, int bufferId) {
        GL11.glClearColor(color.getRed() / 255f, color.getGreen() / 255f,
                color.getBlue() / 255f, color.getAlpha() / 255f);
        GL11.glClear(bufferId);
    }

    /**
     * set gl clear color from an AWT Color object
     */
    public static void glClear(final java.awt.Color color, int bufferId) {
        GL11.glClearColor(color.getRed() / 255f, color.getGreen() / 255f,
                color.getBlue() / 255f, color.getAlpha() / 255f);
        GL11.glClear(bufferId);
    }

    /**
     * set gl clear color from RGBA components
     */
    public static void glClear(float r, float g, float b, float a, int bufferId) {
        GL11.glClearColor(r, g, b, a);
        GL11.glClear(bufferId);
    }

    /**
     * load a texture in GL Context. If the image has already a texture ID,
     * retrieve it from a map value
     * 
     * @param image
     * @return
     */
    public static int loadOrRetrieveTexture(final BufferedImage image,
            boolean mipmap) {
        Integer existingTextureID = GLTools.existingTextureIDs.get(image);
        if (existingTextureID == null) {
            existingTextureID = loadTexture(image, mipmap);
            existingTextureIDs.put(image, existingTextureID);
        }
        return existingTextureID;
    }

    /**
     * load a texture in GL context and return the texture id
     * 
     * @param image
     * @return the generated texture id
     */
    public static int loadTexture(final BufferedImage image, boolean mipmap) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0,
                image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth()
                * image.getHeight() * 4); // 4 for RGBA, 3 for RGB

        fillBuffer(image, pixels, buffer);

        buffer.rewind();

        int textureID = setGlTexture(image.getWidth(), image.getHeight(),
                mipmap, buffer);
        // Return the texture ID so we can bind it later again
        return textureID;
    }

    /**
     * @param image
     * @param mipmap
     * @param buffer
     * @return
     */
    private static int setGlTexture(final int width, int height,
            boolean mipmap, ByteBuffer buffer) {
        // You now have a ByteBuffer filled with the color data of each pixel.
        // Now just create a texture ID and bind it. Then you can load it using
        // whatever OpenGL method you want, for example:

        int textureID = glGenTextures(); // Generate texture ID
        glBindTexture(GL_TEXTURE_2D, textureID); // Bind texture ID

        // Setup wrap mode
        // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA,
                GL_UNSIGNED_BYTE, buffer);
        if (mipmap) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,
                    GL11.GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
                    GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL30.glGenerateMipmap(GL_TEXTURE_2D);
        }
        return textureID;
    }

    /**
     * @param image
     * @param pixels
     * @param buffer
     */
    private static void fillBuffer(final BufferedImage image, int[] pixels,
            ByteBuffer buffer) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) (pixel >> 16 & 0xFF)); // Red component
                buffer.put((byte) (pixel >> 8 & 0xFF)); // Green component
                buffer.put((byte) (pixel >> 0 & 0xFF)); // Blue component
                buffer.put((byte) (pixel >> 24 & 0xFF)); // Alpha component.
                                                         // Only for RGBA
                // System.err.println("transparency = " + (pixel >> 24 & 0xFF));
            }
        }
    }

    /**
     * return the size in bytes of a gl primitive type
     * 
     * @param glType
     *            GL_FLOAT, GL_INT, ...
     * @return
     */
    public static int sizeInBytes(int glType) {
        switch (glType) {
        case GL11.GL_FLOAT:
            return Float.SIZE / Byte.SIZE;
        case GL11.GL_INT:
            return Integer.SIZE / Byte.SIZE;
        default:
            throw new UnsupportedOperationException(
                    "Don't know the size of gl type " + glType);

        }
    }

    public static BufferedImage loadImage(final String loc) throws IOException {
        return ImageIO.read(new File(loc));
    }

    public static void glDrawString(final String s, double x, double y) {
        double startX = x;
        GL11.glBegin(GL11.GL_POINTS);
        for (char c : s.toLowerCase().toCharArray()) {
            if (c == 'a') {
                for (int i = 0; i < 8; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                    GL11.glVertex2d(x + 7, y - i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y - 8);
                    GL11.glVertex2d(x + i, y - 4);
                }
                x += 8;
            } else if (c == 'b') {
                for (int i = 0; i < 8; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                }
                for (int i = 1; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y);
                    GL11.glVertex2d(x + i, y - 4);
                    GL11.glVertex2d(x + i, y - 8);
                }
                GL11.glVertex2d(x + 7, y - 5);
                GL11.glVertex2d(x + 7, y - 7);
                GL11.glVertex2d(x + 7, y - 6);

                GL11.glVertex2d(x + 7, y - 1);
                GL11.glVertex2d(x + 7, y - 2);
                GL11.glVertex2d(x + 7, y - 3);
                x += 8;
            } else if (c == 'c') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2d(x + i, y);
                    GL11.glVertex2d(x + i, y - 8);
                }
                GL11.glVertex2d(x + 6, y - 1);
                GL11.glVertex2d(x + 6, y - 2);

                GL11.glVertex2d(x + 6, y - 6);
                GL11.glVertex2d(x + 6, y - 7);

                x += 8;
            } else if (c == 'd') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2d(x + i, y);
                    GL11.glVertex2d(x + i, y - 8);
                }
                GL11.glVertex2d(x + 6, y - 1);
                GL11.glVertex2d(x + 6, y - 2);
                GL11.glVertex2d(x + 6, y - 3);
                GL11.glVertex2d(x + 6, y - 4);
                GL11.glVertex2d(x + 6, y - 5);
                GL11.glVertex2d(x + 6, y - 6);
                GL11.glVertex2d(x + 6, y - 7);

                x += 8;
            } else if (c == 'e') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                }
                for (int i = 1; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y - 0);
                    GL11.glVertex2d(x + i, y - 8);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2d(x + i, y - 4);
                }
                x += 8;
            } else if (c == 'f') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                }
                for (int i = 1; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y - 8);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2d(x + i, y - 4);
                }
                x += 8;
            } else if (c == 'g') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2d(x + i, y);
                    GL11.glVertex2d(x + i, y - 8);
                }
                GL11.glVertex2d(x + 6, y - 1);
                GL11.glVertex2d(x + 6, y - 2);
                GL11.glVertex2d(x + 6, y - 3);
                GL11.glVertex2d(x + 5, y - 3);
                GL11.glVertex2d(x + 7, y - 3);

                GL11.glVertex2d(x + 6, y - 6);
                GL11.glVertex2d(x + 6, y - 7);

                x += 8;
            } else if (c == 'h') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                    GL11.glVertex2d(x + 7, y - i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y - 4);
                }
                x += 8;
            } else if (c == 'i') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2d(x + 3, y - i);
                }
                for (int i = 1; i <= 5; i++) {
                    GL11.glVertex2d(x + i, y - 0);
                    GL11.glVertex2d(x + i, y - 8);
                }
                x += 7;
            } else if (c == 'j') {
                for (int i = 1; i <= 8; i++) {
                    GL11.glVertex2d(x + 6, y - i);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2d(x + i, y - 0);
                }
                GL11.glVertex2d(x + 1, y - 3);
                GL11.glVertex2d(x + 1, y - 2);
                GL11.glVertex2d(x + 1, y - 1);
                x += 8;
            } else if (c == 'k') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                }
                GL11.glVertex2d(x + 6, y - 8);
                GL11.glVertex2d(x + 5, y - 7);
                GL11.glVertex2d(x + 4, y - 6);
                GL11.glVertex2d(x + 3, y - 5);
                GL11.glVertex2d(x + 2, y - 4);
                GL11.glVertex2d(x + 2, y - 3);
                GL11.glVertex2d(x + 3, y - 4);
                GL11.glVertex2d(x + 4, y - 3);
                GL11.glVertex2d(x + 5, y - 2);
                GL11.glVertex2d(x + 6, y - 1);
                GL11.glVertex2d(x + 7, y);
                x += 8;
            } else if (c == 'l') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                }
                for (int i = 1; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y);
                }
                x += 7;
            } else if (c == 'm') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                    GL11.glVertex2d(x + 7, y - i);
                }
                GL11.glVertex2d(x + 3, y - 6);
                GL11.glVertex2d(x + 2, y - 7);
                GL11.glVertex2d(x + 4, y - 5);

                GL11.glVertex2d(x + 5, y - 6);
                GL11.glVertex2d(x + 6, y - 7);
                GL11.glVertex2d(x + 4, y - 5);
                x += 8;
            } else if (c == 'n') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                    GL11.glVertex2d(x + 7, y - i);
                }
                GL11.glVertex2d(x + 2, y - 7);
                GL11.glVertex2d(x + 2, y - 6);
                GL11.glVertex2d(x + 3, y - 5);
                GL11.glVertex2d(x + 4, y - 4);
                GL11.glVertex2d(x + 5, y - 3);
                GL11.glVertex2d(x + 6, y - 2);
                GL11.glVertex2d(x + 6, y - 1);
                x += 8;
            } else if (c == 'o' || c == '0') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                    GL11.glVertex2d(x + 7, y - i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y - 8);
                    GL11.glVertex2d(x + i, y - 0);
                }
                x += 8;
            } else if (c == 'p') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2d(x + i, y - 8);
                    GL11.glVertex2d(x + i, y - 4);
                }
                GL11.glVertex2d(x + 6, y - 7);
                GL11.glVertex2d(x + 6, y - 5);
                GL11.glVertex2d(x + 6, y - 6);
                x += 8;
            } else if (c == 'q') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                    if (i != 1) {
                        GL11.glVertex2d(x + 7, y - i);
                    }
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y - 8);
                    if (i != 6) {
                        GL11.glVertex2d(x + i, y - 0);
                    }
                }
                GL11.glVertex2d(x + 4, y - 3);
                GL11.glVertex2d(x + 5, y - 2);
                GL11.glVertex2d(x + 6, y - 1);
                GL11.glVertex2d(x + 7, y);
                x += 8;
            } else if (c == 'r') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2d(x + i, y - 8);
                    GL11.glVertex2d(x + i, y - 4);
                }
                GL11.glVertex2d(x + 6, y - 7);
                GL11.glVertex2d(x + 6, y - 5);
                GL11.glVertex2d(x + 6, y - 6);

                GL11.glVertex2d(x + 4, y - 3);
                GL11.glVertex2d(x + 5, y - 2);
                GL11.glVertex2d(x + 6, y - 1);
                GL11.glVertex2d(x + 7, y);
                x += 8;
            } else if (c == 's') {
                for (int i = 2; i <= 7; i++) {
                    GL11.glVertex2d(x + i, y - 8);
                }
                GL11.glVertex2d(x + 1, y - 7);
                GL11.glVertex2d(x + 1, y - 6);
                GL11.glVertex2d(x + 1, y - 5);
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y - 4);
                    GL11.glVertex2d(x + i, y);
                }
                GL11.glVertex2d(x + 7, y - 3);
                GL11.glVertex2d(x + 7, y - 2);
                GL11.glVertex2d(x + 7, y - 1);
                GL11.glVertex2d(x + 1, y - 1);
                GL11.glVertex2d(x + 1, y - 2);
                x += 8;
            } else if (c == 't') {
                for (int i = 0; i <= 8; i++) {
                    GL11.glVertex2d(x + 4, y - i);
                }
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2d(x + i, y - 8);
                }
                x += 7;
            } else if (c == 'u') {
                for (int i = 1; i <= 8; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                    GL11.glVertex2d(x + 7, y - i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y - 0);
                }
                x += 8;
            } else if (c == 'v') {
                for (int i = 2; i <= 8; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                    GL11.glVertex2d(x + 6, y - i);
                }
                GL11.glVertex2d(x + 2, y - 1);
                GL11.glVertex2d(x + 5, y - 1);
                GL11.glVertex2d(x + 3, y);
                GL11.glVertex2d(x + 4, y);
                x += 7;
            } else if (c == 'w') {
                for (int i = 1; i <= 8; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                    GL11.glVertex2d(x + 7, y - i);
                }
                GL11.glVertex2d(x + 2, y);
                GL11.glVertex2d(x + 3, y);
                GL11.glVertex2d(x + 5, y);
                GL11.glVertex2d(x + 6, y);
                for (int i = 1; i <= 6; i++) {
                    GL11.glVertex2d(x + 4, y - i);
                }
                x += 8;
            } else if (c == 'x') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2d(x + i, y - i);
                }
                for (int i = 7; i >= 1; i--) {
                    GL11.glVertex2d(x + i, y - 8 + i);
                }
                x += 8;
            } else if (c == 'y') {
                GL11.glVertex2d(x + 4, y);
                GL11.glVertex2d(x + 4, y - 1);
                GL11.glVertex2d(x + 4, y - 2);
                GL11.glVertex2d(x + 4, y - 3);
                GL11.glVertex2d(x + 4, y - 4);

                GL11.glVertex2d(x + 3, y - 5);
                GL11.glVertex2d(x + 2, y - 6);
                GL11.glVertex2d(x + 1, y - 7);
                GL11.glVertex2d(x + 1, y - 8);

                GL11.glVertex2d(x + 5, y - 5);
                GL11.glVertex2d(x + 6, y - 6);
                GL11.glVertex2d(x + 7, y - 7);
                GL11.glVertex2d(x + 7, y - 8);
                x += 8;
            } else if (c == 'z') {
                for (int i = 1; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y);
                    GL11.glVertex2d(x + i, y - 8);
                    GL11.glVertex2d(x + i, y - i);
                }
                GL11.glVertex2d(x + 6, y - 7);
                x += 8;
            } else if (c == '1') {
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y);
                }
                for (int i = 1; i <= 8; i++) {
                    GL11.glVertex2d(x + 4, y - i);
                }
                GL11.glVertex2d(x + 3, y - 7);
                x += 8;
            } else if (c == '2') {
                for (int i = 1; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2d(x + i, y - 8);
                }
                GL11.glVertex2d(x + 1, y - 7);
                GL11.glVertex2d(x + 1, y - 6);

                GL11.glVertex2d(x + 6, y - 7);
                GL11.glVertex2d(x + 6, y - 6);
                GL11.glVertex2d(x + 6, y - 5);
                GL11.glVertex2d(x + 5, y - 4);
                GL11.glVertex2d(x + 4, y - 3);
                GL11.glVertex2d(x + 3, y - 2);
                GL11.glVertex2d(x + 2, y - 1);
                x += 8;
            } else if (c == '3') {
                for (int i = 1; i <= 5; i++) {
                    GL11.glVertex2d(x + i, y - 8);
                    GL11.glVertex2d(x + i, y);
                }
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2d(x + 6, y - i);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2d(x + i, y - 4);
                }
                x += 8;
            } else if (c == '4') {
                for (int i = 2; i <= 8; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                }
                for (int i = 2; i <= 7; i++) {
                    GL11.glVertex2d(x + i, y - 1);
                }
                for (int i = 0; i <= 4; i++) {
                    GL11.glVertex2d(x + 4, y - i);
                }
                x += 8;
            } else if (c == '5') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2d(x + i, y - 8);
                }
                for (int i = 4; i <= 7; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                }
                GL11.glVertex2d(x + 1, y - 1);
                GL11.glVertex2d(x + 2, y);
                GL11.glVertex2d(x + 3, y);
                GL11.glVertex2d(x + 4, y);
                GL11.glVertex2d(x + 5, y);
                GL11.glVertex2d(x + 6, y);

                GL11.glVertex2d(x + 7, y - 1);
                GL11.glVertex2d(x + 7, y - 2);
                GL11.glVertex2d(x + 7, y - 3);

                GL11.glVertex2d(x + 6, y - 4);
                GL11.glVertex2d(x + 5, y - 4);
                GL11.glVertex2d(x + 4, y - 4);
                GL11.glVertex2d(x + 3, y - 4);
                GL11.glVertex2d(x + 2, y - 4);
                x += 8;
            } else if (c == '6') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y);
                }
                for (int i = 2; i <= 5; i++) {
                    GL11.glVertex2d(x + i, y - 4);
                    GL11.glVertex2d(x + i, y - 8);
                }
                GL11.glVertex2d(x + 7, y - 1);
                GL11.glVertex2d(x + 7, y - 2);
                GL11.glVertex2d(x + 7, y - 3);
                GL11.glVertex2d(x + 6, y - 4);
                x += 8;
            } else if (c == '7') {
                for (int i = 0; i <= 7; i++) {
                    GL11.glVertex2d(x + i, y - 8);
                }
                GL11.glVertex2d(x + 7, y - 7);
                GL11.glVertex2d(x + 7, y - 6);

                GL11.glVertex2d(x + 6, y - 5);
                GL11.glVertex2d(x + 5, y - 4);
                GL11.glVertex2d(x + 4, y - 3);
                GL11.glVertex2d(x + 3, y - 2);
                GL11.glVertex2d(x + 2, y - 1);
                GL11.glVertex2d(x + 1, y);
                x += 8;
            } else if (c == '8') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                    GL11.glVertex2d(x + 7, y - i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y - 8);
                    GL11.glVertex2d(x + i, y - 0);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y - 4);
                }
                x += 8;
            } else if (c == '9') {
                for (int i = 1; i <= 7; i++) {
                    GL11.glVertex2d(x + 7, y - i);
                }
                for (int i = 5; i <= 7; i++) {
                    GL11.glVertex2d(x + 1, y - i);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y - 8);
                    GL11.glVertex2d(x + i, y - 0);
                }
                for (int i = 2; i <= 6; i++) {
                    GL11.glVertex2d(x + i, y - 4);
                }
                GL11.glVertex2d(x + 1, y - 0);
                x += 8;
            } else if (c == '.') {
                GL11.glVertex2d(x + 1, y);
                x += 2;
            } else if (c == ',') {
                GL11.glVertex2d(x + 1, y);
                GL11.glVertex2d(x + 1, y - 1);
                x += 2;
            } else if (c == '\n') {
                y -= 10;
                x = startX;
            } else if (c == ' ') {
                x += 8;
            }
        }
        GL11.glEnd();
    }

    public static org.lwjgl.util.Color glRandomColor() {

        return new org.lwjgl.util.Color(randomSeed.nextInt(256),
                randomSeed.nextInt(256), randomSeed.nextInt(256));
    }

    /**
     * Read a file as a single string
     * 
     * @param filename
     *            file name containing the content to be read
     * @return the file content as a single string
     * @throws Exception
     */
    public static String readFileAsString(String filename) throws IOException {
        StringBuilder source = new StringBuilder();
        FileInputStream in = new FileInputStream(filename);
        IOException exception = null;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            IOException innerExc = null;
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    source.append(line).append('\n');
                }
            } catch (IOException exc) {
                exception = exc;
            } finally {
                try {
                    reader.close();
                } catch (IOException exc) {
                    if (innerExc == null) {
                        innerExc = exc;
                    } else {
                        exc.printStackTrace();
                    }
                }
            }
            if (innerExc != null) {
                throw innerExc;
            }

        } finally {
            try {
                in.close();
            } catch (IOException exc) {
                if (exception == null) {
                    exception = exc;
                } else {
                    exc.printStackTrace();
                }
            }
            if (exception != null) {
                throw exception;
            }
        }
        return source.toString();
    }

    /**
     * Method to create a new shader
     * 
     * @param filename
     * @param shaderType
     * @return
     * @throws Exception
     */
    public static List<Integer> createShaders(int shaderType,
            String... shaderFilenames) throws GLException {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (String shaderFilename : shaderFilenames) {
            ids.add(createShader(shaderType, shaderFilename));
        }
        return ids;
    }

    public static int createShader(int shaderType, String shaderContent)
            throws GLException {
        int shader = 0;
        try {
            // logger.debug("shader " + shaderFilename
            // + " shaderType " + shaderTypeNames.get(shaderType));
            shader = glCreateShader(shaderType);
            if (shader == 0) {
                return 0;
            }
            glShaderSource(shader, shaderContent);
            glCompileShader(shader);
            if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
                logger.error("shader type " + shaderTypeNames.get(shaderType)
                        + "'" + shaderContent + "'");
                logger.error("shader content '" + shaderContent + "'");

                throw new RuntimeException("Error compiling shader file '"
                        + shaderContent + "': " + getShaderLogInfo(shader));
            }
            return shader;
        } catch (Exception exc) {
            throw new GLException(exc);
        }
    }

    public static String getShaderLogInfo(int obj) {
        return glGetShaderInfoLog(obj, 4096);
    }

    public static String getProgramLogInfo(int obj) {
        return glGetProgramInfoLog(obj, 4096);
    }

}