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

package test.App;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.swing.JFrame;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import fr.ign.cogit.geoxygene.util.gl.GLTools;

/**
 * @author JeT
 * 
 */
public class SimpleTextureApplication {

    public static String VertexShader = "\n" + "    #version 150 core\n"
            + "     \n" + "    in vec4 in_Position;\n"
            + "    in vec4 in_Color;\n" + "   in vec2 in_TextureCoord;\n"
            + "    \n" + "   out vec4 pass_Color;\n"
            + "   out vec2 pass_TextureCoord;\n" + "    \n"
            + "   void main(void) {\n" + "    gl_Position = in_Position;\n"
            + "     \n" + "    pass_Color = in_Color;\n"
            + "    pass_TextureCoord = in_TextureCoord;\n" + "    }\n";
    public static String FragmentShader = "\n"
            + "    #version 150 core\n"
            + "     \n"
            + "    uniform sampler2D texture_diffuse;\n"
            + "     \n"
            + "    in vec4 pass_Color;\n"
            + "    in vec2 pass_TextureCoord;\n"
            + "     \n"
            + "    out vec4 out_Color;\n"
            + "     \n"
            + "    void main(void) {\n"
            + "    out_Color = vec4( 0,1,0,1);\n"
            + "    // Override out_Color with our texture pixel\n"
            + "    //out_Color = texture(texture_diffuse, pass_TextureCoord);\n"
            + "    }";

    // Quad variables
    private static int vaoId = 0;
    private static int vboId = 0;
    private static int vboiId = 0;
    private static int indicesCount = 0;
    // Shader variables
    private static int vsId = 0;
    private static int fsId = 0;
    private static int pId = 0;
    // Texture variables
    private static int[] texIds = new int[] { 0, 0 };
    private static int textureSelector = 0;

    /**
     * 
     */
    public SimpleTextureApplication() {

    }

    /**
     * @param args
     * @throws LWJGLException
     */
    public static void main(String[] args) throws LWJGLException {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TextureGLCanvas glCanvas = new TextureGLCanvas();
        glCanvas.setBackground(Color.blue);
        frame.getContentPane().add(glCanvas, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseMoved(MouseEvent e) {
                frame.repaint();

            }

            @Override
            public void mouseDragged(MouseEvent e) {
                frame.repaint();
            }
        });
        frame.setVisible(true);
    }

    private static class TextureGLCanvas extends AWTGLCanvas {

        public TextureGLCanvas() throws LWJGLException {
            super();
        }

        @Override
        protected void initGL() {
            super.initGL();
            // Initialize OpenGL (Display)
            this.setupOpenGL();

            this.setupQuad();
            this.setupShaders();
            this.setupTextures();
        }

        @Override
        protected void paintGL() {
            // super.paintGL();
            textureSelector = 1 - textureSelector;
            System.err.println("render");
            // Render
            GL11.glClearColor(1, 0, 0, 1);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            GL20.glUseProgram(pId);
            // Bind the texture
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texIds[textureSelector]);
            // Bind to the VAO that has all the information about the vertices
            GL30.glBindVertexArray(vaoId);
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            // Bind to the index VBO that has all the information about the
            // order of the vertices
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
            // Draw the vertices
            GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount,
                    GL11.GL_UNSIGNED_BYTE, 0);
            // Put everything back to default (deselect)
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(2);
            GL30.glBindVertexArray(0);
            GL20.glUseProgram(0);
            try {
                this.swapBuffers();
            } catch (LWJGLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            this.exitOnGLError("loopCycle");
        }

        // public TheQuadExampleTextured() {
        //
        // while (!Display.isCloseRequested()) {
        // // Do a single loop (logic/render)
        // this.loopCycle();
        //
        // // Force a maximum FPS of about 60
        // Display.sync(60);
        // // Let the CPU synchronize with the GPU if GPU is tagging behind
        // Display.update();
        // }
        //
        // // Destroy OpenGL (Display)
        // this.destroyOpenGL();
        // }

        private void setupTextures() {
            texIds[0] = this
                    .loadPNGTexture(
                            "/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/images/compass_rose.png",
                            GL13.GL_TEXTURE0);
            texIds[1] = this
                    .loadPNGTexture(
                            "/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/images/herbes.png",
                            GL13.GL_TEXTURE0);

            this.exitOnGLError("setupTexture");
        }

        private void setupOpenGL() {

            // Setup an XNA like background color
            GL11.glClearColor(4f, 6f, 9f, 0f);

            // Map the internal OpenGL coordinate system to the entire screen
            GL11.glViewport(0, 0, 800, 600);

            this.exitOnGLError("setupOpenGL");
        }

        private void setupQuad() {
            // We'll define our quad using 4 vertices of the custom
            // 'TexturedVertex' class
            TexturedVertex v0 = new TexturedVertex();
            v0.setXYZ(-5f, 5f, 0);
            v0.setRGB(1, 0, 0);
            v0.setST(0, 0);
            TexturedVertex v1 = new TexturedVertex();
            v0.setXYZ(-5f, -5f, 0);
            v0.setRGB(0, 1, 0);
            v0.setST(0, 1);
            TexturedVertex v2 = new TexturedVertex();
            v0.setXYZ(5f, -5f, 0);
            v0.setRGB(0, 0, 1);
            v0.setST(1, 1);
            TexturedVertex v3 = new TexturedVertex();
            v0.setXYZ(5f, 5f, 0);
            v0.setRGB(1, 1, 1);
            v0.setST(1, 0);

            TexturedVertex[] vertices = new TexturedVertex[] { v0, v1, v2, v3 };
            // Put each 'Vertex' in one FloatBuffer
            FloatBuffer verticesBuffer = BufferUtils
                    .createFloatBuffer(vertices.length
                            * TexturedVertex.elementCount);
            for (int i = 0; i < vertices.length; i++) {
                // Add position, color and texture floats to the buffer
                verticesBuffer.put(vertices[i].getElements());
            }
            verticesBuffer.flip();
            // OpenGL expects to draw vertices in counter clockwise order by
            // default
            byte[] indices = { 0, 1, 2, 2, 3, 0 };
            indicesCount = indices.length;
            ByteBuffer indicesBuffer = BufferUtils
                    .createByteBuffer(indicesCount);
            indicesBuffer.put(indices);
            indicesBuffer.flip();

            // Create a new Vertex Array Object in memory and select it (bind)
            vaoId = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(vaoId);

            // Create a new Vertex Buffer Object in memory and select it (bind)
            vboId = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer,
                    GL15.GL_STATIC_DRAW);

            // Put the position coordinates in attribute list 0
            GL20.glVertexAttribPointer(0, TexturedVertex.positionElementCount,
                    GL11.GL_FLOAT, false, TexturedVertex.stride,
                    TexturedVertex.positionByteOffset);
            // Put the color components in attribute list 1
            GL20.glVertexAttribPointer(1, TexturedVertex.colorElementCount,
                    GL11.GL_FLOAT, false, TexturedVertex.stride,
                    TexturedVertex.colorByteOffset);
            // Put the texture coordinates in attribute list 2
            GL20.glVertexAttribPointer(2, TexturedVertex.textureElementCount,
                    GL11.GL_FLOAT, false, TexturedVertex.stride,
                    TexturedVertex.textureByteOffset);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

            // Deselect (bind to 0) the VAO
            GL30.glBindVertexArray(0);

            // Create a new VBO for the indices and select it (bind) - INDICES
            vboiId = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer,
                    GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

            this.exitOnGLError("setupQuad");
        }

        private void setupShaders() {
            // Load the vertex shader
            vsId = this.loadShader(VertexShader, GL20.GL_VERTEX_SHADER);
            // Load the fragment shader
            fsId = this.loadShader(SimpleTextureApplication.FragmentShader,
                    GL20.GL_FRAGMENT_SHADER);

            // Create a new shader program that links both shaders
            pId = GL20.glCreateProgram();
            GL20.glAttachShader(pId, vsId);
            GL20.glAttachShader(pId, fsId);

            // Position information will be attribute 0
            GL20.glBindAttribLocation(pId, 0, "in_Position");
            // Color information will be attribute 1
            GL20.glBindAttribLocation(pId, 1, "in_Color");
            // Textute information will be attribute 2
            GL20.glBindAttribLocation(pId, 2, "in_TextureCoord");

            GL20.glLinkProgram(pId);
            GL20.glValidateProgram(pId);

            this.exitOnGLError("setupShaders");
        }

        private int loadShader(String shaderSource, int type) {
            // StringBuilder shaderSource = new StringBuilder();
            int shaderID = 0;
            // try {
            // BufferedReader reader = new BufferedReader(new FileReader(
            // filename));
            // String line;
            // while ((line = reader.readLine()) != null) {
            // shaderSource.append(line).append("\n");
            // }
            // reader.close();
            // } catch (IOException e) {
            // System.err.println("Could not read file.");
            // e.printStackTrace();
            // System.exit(-1);
            // }
            shaderID = GL20.glCreateShader(type);
            GL20.glShaderSource(shaderID, shaderSource);
            GL20.glCompileShader(shaderID);
            if (GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                System.err.println("Could not compile shader.");
                System.exit(-1);
            }
            this.exitOnGLError("loadShader");
            return shaderID;
        }

        private int loadPNGTexture(String filename, int textureUnit) {
            ByteBuffer buf = null;
            int tWidth = 0;
            int tHeight = 0;
            try {
                // Open the PNG file as an InputStream
                InputStream in = new FileInputStream(filename);
                // Link the PNG decoder to this stream
                PNGDecoder decoder = new PNGDecoder(in);
                // Get the width and height of the texture
                tWidth = decoder.getWidth();
                tHeight = decoder.getHeight();
                // Decode the PNG file in a ByteBuffer
                buf = ByteBuffer.allocateDirect(4 * decoder.getWidth()
                        * decoder.getHeight());
                decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
                buf.flip();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
            // Create a new texture object in memory and bind it
            int texId = GL11.glGenTextures();
            GL13.glActiveTexture(textureUnit);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
            // All RGB bytes are aligned to each other and each component is 1
            // byte
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            // Upload the texture data and generate mip maps (for scaling)
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, tWidth,
                    tHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            // Setup the ST coordinate system
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
                    GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
                    GL11.GL_REPEAT);
            // Setup what to do when the texture has to be scaled
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            this.exitOnGLError("loadPNGTexture");
            return texId;
        }

        private void exitOnGLError(String errorMessage) {
            GLTools.glCheckError(errorMessage);
        }
    }

    public static class TexturedVertex {
        // Vertex data
        private float[] xyzw = new float[] { 0f, 0f, 0f, 1f };
        private float[] rgba = new float[] { 1f, 1f, 1f, 1f };
        private float[] st = new float[] { 0f, 0f };

        // The amount of bytes an element has
        public static final int elementBytes = 4;

        // Elements per parameter
        public static final int positionElementCount = 4;
        public static final int colorElementCount = 4;
        public static final int textureElementCount = 2;

        // Bytes per parameter
        public static final int positionBytesCount = positionElementCount
                * elementBytes;
        public static final int colorByteCount = colorElementCount
                * elementBytes;
        public static final int textureByteCount = textureElementCount
                * elementBytes;

        // Byte offsets per parameter
        public static final int positionByteOffset = 0;
        public static final int colorByteOffset = positionByteOffset
                + positionBytesCount;
        public static final int textureByteOffset = colorByteOffset
                + colorByteCount;

        // The amount of elements that a vertex has
        public static final int elementCount = positionElementCount
                + colorElementCount + textureElementCount;
        // The size of a vertex in bytes, like in C/C++: sizeof(Vertex)
        public static final int stride = positionBytesCount + colorByteCount
                + textureByteCount;

        // Setters
        public void setXYZ(float x, float y, float z) {
            this.setXYZW(x, y, z, 1f);
        }

        public void setRGB(float r, float g, float b) {
            this.setRGBA(r, g, b, 1f);
        }

        public void setST(float s, float t) {
            this.st = new float[] { s, t };
        }

        public void setXYZW(float x, float y, float z, float w) {
            this.xyzw = new float[] { x, y, z, w };
        }

        public void setRGBA(float r, float g, float b, float a) {
            this.rgba = new float[] { r, g, b, 1f };
        }

        // Getters
        public float[] getElements() {
            float[] out = new float[TexturedVertex.elementCount];
            int i = 0;

            // Insert XYZW elements
            out[i++] = this.xyzw[0];
            out[i++] = this.xyzw[1];
            out[i++] = this.xyzw[2];
            out[i++] = this.xyzw[3];
            // Insert RGBA elements
            out[i++] = this.rgba[0];
            out[i++] = this.rgba[1];
            out[i++] = this.rgba[2];
            out[i++] = this.rgba[3];
            // Insert ST elements
            out[i++] = this.st[0];
            out[i++] = this.st[1];

            return out;
        }

        public float[] getXYZW() {
            return new float[] { this.xyzw[0], this.xyzw[1], this.xyzw[2],
                    this.xyzw[3] };
        }

        public float[] getRGBA() {
            return new float[] { this.rgba[0], this.rgba[1], this.rgba[2],
                    this.rgba[3] };
        }

        public float[] getST() {
            return new float[] { this.st[0], this.st[1] };
        }
    }

}
