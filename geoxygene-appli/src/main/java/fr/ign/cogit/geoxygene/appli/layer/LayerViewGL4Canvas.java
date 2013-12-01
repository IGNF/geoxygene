/**
 * 
 */
package fr.ign.cogit.geoxygene.appli.layer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;

import fr.ign.cogit.geoxygene.appli.gl.GL4Util;
import fr.ign.cogit.geoxygene.appli.gl.TexturedVertex;

/** @author JeT GL drawable canvas inserted into a LayerViewLwjglPanel */
public class LayerViewGL4Canvas extends LayerViewGLCanvas {

    private static final long serialVersionUID = 2813681374260169340L; // serializable

    // shaders
    private int program = 0;
    // Quad variables
    private int vaoId = 0;
    private int vboId = 0;
    private int vboiId = 0;
    private int indicesCount = 0;
    // Texture variables
    private final int[] texIds = new int[] { 0, 0 };
    private final int textureSelector = 0;

    /**
     * Constructor
     * 
     * @param parentPanel
     * @throws LWJGLException
     */
    public LayerViewGL4Canvas(final LayerViewGLPanel parentPanel) throws LWJGLException {
        super(parentPanel);
    }

    private void setupTextures() {
        this.texIds[0] = GL4Util.loadPNGTexture("./src/main/resources/textures/water.png",
                GL_TEXTURE0);
        this.texIds[1] = GL4Util.loadPNGTexture("./src/main/resources/textures/cell02.png",
                GL_TEXTURE0);
    }

    @Override
    protected void initGL() {
        super.initGL();
        glViewport(0, 0, this.getWidth(), this.getHeight());
        int vertShader = 0, fragShader = 0;
        try {
            vertShader = this.createShader("./src/main/resources/shaders/screen.vert",
                    GL_VERTEX_SHADER);
            fragShader = this.createShader("./src/main/resources/shaders/screen.frag",
                    GL_FRAGMENT_SHADER);
        } catch (Exception exc) {
            exc.printStackTrace();
            return;
        } finally {
            if (vertShader == 0) {
                logger.error("Unable to create vertex shader");
                return;
            }
            if (fragShader == 0) {
                logger.error("Unable to create fragment shader");
                return;
            }
        }
        this.program = glCreateProgram();
        if (this.program == 0) {
            logger.error("Unable to create GL program");
            return;
        }
        /*
         * 
         * if the vertex and fragment shaders setup sucessfully,
         * 
         * attach them to the shader program, link the sahder program
         * 
         * (into the GL context I suppose), and validate
         */
        glAttachShader(this.program, vertShader);
        glAttachShader(this.program, fragShader);        // Position information will be attribute 0
        glBindAttribLocation(this.program, 0, "in_Position");
        // Color information will be attribute 1
        glBindAttribLocation(this.program, 1, "in_Color");
        // Textute information will be attribute 2
        glBindAttribLocation(this.program, 2, "in_TextureCoord");
        glLinkProgram(this.program);
        if (glGetProgrami(this.program, GL_LINK_STATUS) == GL_FALSE) {
            logger.error(getLogInfo(this.program));
            return;
        }
        glValidateProgram(this.program);
        if (glGetProgrami(this.program, GL_VALIDATE_STATUS) == GL_FALSE) {
            logger.error(getLogInfo(this.program));
            return;
        }
        this.setupTextures();
    }

    private void setupQuad() {
        // We'll define our quad using 4 vertices of the custom 'TexturedVertex' class
        TexturedVertex v0 = new TexturedVertex();
        v0.setXYZ(-0.5f, 0.5f, 0);
        v0.setRGB(1, 0, 0);
        v0.setST(0, 0);
        TexturedVertex v1 = new TexturedVertex();
        v1.setXYZ(-0.5f, -0.5f, 0);
        v1.setRGB(0, 1, 0);
        v1.setST(0, 1);
        TexturedVertex v2 = new TexturedVertex();
        v2.setXYZ(0.5f, -0.5f, 0);
        v2.setRGB(0, 0, 1);
        v2.setST(1, 1);
        TexturedVertex v3 = new TexturedVertex();
        v3.setXYZ(0.5f, 0.5f, 0);
        v3.setRGB(1, 1, 1);
        v3.setST(1, 0);

        TexturedVertex[] vertices = new TexturedVertex[] { v0, v1, v2, v3 };
        // Put each 'Vertex' in one FloatBuffer
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length * TexturedVertex.elementCount);
        for (int i = 0; i < vertices.length; i++) {
            // Add position, color and texture floats to the buffer
            verticesBuffer.put(vertices[i].getElements());
        }
        verticesBuffer.flip();
        // OpenGL expects to draw vertices in counter clockwise order by default
        byte[] indices = { 0, 1, 2, 2, 3, 0 };
        this.indicesCount = indices.length;
        ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(this.indicesCount);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        // Create a new Vertex Array Object in memory and select it (bind)
        this.vaoId = glGenVertexArrays();
        glBindVertexArray(this.vaoId);

        // Create a new Vertex Buffer Object in memory and select it (bind)
        this.vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

        // Put the position coordinates in attribute list 0
        glVertexAttribPointer(0, TexturedVertex.positionElementCount, GL_FLOAT, false, TexturedVertex.stride, TexturedVertex.positionByteOffset);
        // Put the color components in attribute list 1
        glVertexAttribPointer(1, TexturedVertex.colorElementCount, GL_FLOAT, false, TexturedVertex.stride, TexturedVertex.colorByteOffset);
        // Put the texture coordinates in attribute list 2
        glVertexAttribPointer(2, TexturedVertex.textureElementCount, GL_FLOAT, false, TexturedVertex.stride, TexturedVertex.textureByteOffset);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Deselect (bind to 0) the VAO
        glBindVertexArray(0);

        // Create a new VBO for the indices and select it (bind) - INDICES
        this.vboiId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.vboiId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

    }

    @Override
    protected void paintGL() {
        super.paintGL();
        if (!this.isDisplayable() || this.getContext() == null) {
            return;
        }
        try {
            this.makeCurrent();
        } catch (LWJGLException exception) {
            // if makeCurrent() throws an exception, then the canvas is not ready
            return;
        }        // System.err.println("-------------------------------------------------- paint GL --------------------------------");

        // Render
        glClear(GL_COLOR_BUFFER_BIT);
        this.setupQuad();
        glUseProgram(this.program);

        // Bind the texture
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, this.texIds[this.textureSelector]);

        // Bind to the VAO that has all the information about the vertices
        glBindVertexArray(this.vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        // Bind to the index VBO that has all the information about the order of the vertices
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.vboiId);

        // Draw the vertices
        glDrawElements(GL_TRIANGLES, this.indicesCount, GL_UNSIGNED_BYTE, 0);

        // Put everything back to default (deselect)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);

        glUseProgram(0);

        try {
            this.swapBuffers();
        } catch (LWJGLException e) {
            logger.error("unable to swap GL4 buffers");
        }
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        super.componentResized(e);
        if (this.getParentPanel() == null) {
            return;
        }
        if (this.getSize().equals(this.getParentPanel().getSize())) {
            return;
        }
        // System.err.println("component resize to " + this.getSize() +
        // " in GLCanvas");
        try {
            this.makeCurrent();
        } catch (LWJGLException exception) {
            // if makeCurrent() throws an exception, then the canvas is not ready
            return;
        }
        try {
            if (this.getContext() == null) {
                return;
            }
            this.setSize(this.getParentPanel().getSize());
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(0, this.getWidth(), this.getHeight(), 0, -1000, 1000);
            glMatrixMode(GL_MODELVIEW);
            glViewport(0, 0, this.getWidth(), this.getHeight());
        } catch (Exception e1) {
            // don't know hot to prevent/check this exception.
            // isDisplayable() and isValid() are both true at this point...
            logger.warn("Error resizing the heavyweight AWTGLCanvas : " + e1.getMessage());
            // e1.printStackTrace();
        }
        this.repaint(); // super.componentResized(e);
    }

    /**
     * paint overlays in GL windows
     */
    public void glPaintOverlays() {
    }

    private static String getLogInfo(int obj) {
        return glGetShaderInfoLog(obj, 4096);
    }

    private String readFileAsString(String filename) throws Exception {
        StringBuilder source = new StringBuilder();
        FileInputStream in = new FileInputStream(filename);
        Exception exception = null;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            Exception innerExc = null;
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    source.append(line).append('\n');
                }
            } catch (Exception exc) {
                exception = exc;
            } finally {
                try {
                    reader.close();
                } catch (Exception exc) {
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
        } catch (Exception exc) {
            exception = exc;
        } finally {
            try {
                in.close();
            } catch (Exception exc) {
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

    private int createShader(String filename, int shaderType) throws Exception {
        int shader = 0;
        try {
            shader = glCreateShader(shaderType);
            if (shader == 0) {
                return 0;
            }
            glShaderSource(shader, this.readFileAsString(filename));
            glCompileShader(shader);
            if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
                throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
            }
            return shader;
        } catch (Exception exc) {
            // TODO: delete shader
            //            glDeleteShader(shader);
            throw exc;
        }
    }
}
