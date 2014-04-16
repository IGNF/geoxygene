package test.App;

import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Color;
import java.awt.geom.Point2D;

import org.apache.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLContext;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLVertex;

/**
 * @author JeT
 * 
 */
public class BasicOpenGLCanvas extends AWTGLCanvas {

    private static final Logger logger = Logger.getLogger(BasicOpenGLCanvas.class.getName()); // logger

    private GLContext glContext = null;
    private GLComplex complex = null;

    /**
     * @throws LWJGLException
     * @throws GLException
     */
    public BasicOpenGLCanvas() throws LWJGLException, GLException {
        logger.info("Create " + this.getClass().getSimpleName());
    }

    private GLProgram getBasicProgram() throws GLException {
        GLProgram glProgram = new GLProgram("basic");
        int basicVertexShader = GLProgram.createVertexShader("./src/main/resources/shaders/basic.vert.glsl");
        int basicFragmentShader = GLProgram.createFragmentShader("./src/main/resources/shaders/basic.frag.glsl");
        glProgram.setVertexShader(basicVertexShader);
        glProgram.setFragmentShader(basicFragmentShader);

        glProgram.addInputLocation("vertexPosition", 50);
        glProgram.addInputLocation("vertexUV", 49);
        glProgram.addInputLocation("vertexColor", 48);
        return glProgram;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.lwjgl.opengl.AWTGLCanvas#initGL()
     */
    @Override
    protected void initGL() {
        logger.info("Initialise GL");
        super.initGL();
        this.glContext = new GLContext();

        try {
            this.glContext.addProgram(this.getBasicProgram());
        } catch (GLException e) {
            e.printStackTrace();
        }
        this.complex = this.createComplex();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.lwjgl.opengl.AWTGLCanvas#paintGL()
     */
    @Override
    protected void paintGL() {
        logger.info("Paint GL");
        try {
            this.glContext.setCurrentProgram("basic");
        } catch (GLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            this.makeCurrent();
        } catch (LWJGLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int vaoId = this.complex.getVaoId();
        int vboiId = this.complex.getVboIndicesId();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        glViewport(0, 0, this.getWidth(), this.getHeight());
        GL11.glClearColor(0f, 0f, 0f, 1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        // Bind to the VAO that has all the information about the vertices
        GL30.glBindVertexArray(vaoId);

        // Bind to the index VBO that has all the information about the order of the vertices
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);

        // Draw the vertices
        GLMesh mesh = this.complex.getMeshes().get(0);
        GL11.glDrawElements(mesh.getGlType(), mesh.getLastIndex() - mesh.getFirstIndex() + 1, GL11.GL_UNSIGNED_INT, mesh.getFirstIndex() * (Integer.SIZE / 8));
        // Put everything back to default (deselect)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        try {
            this.swapBuffers();
        } catch (LWJGLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    private GLComplex createComplex() {
        GLComplex complex = new GLComplex(0, 0);
        int v1 = complex.addVertex(new GLVertex(new Point2D.Double(-0.9, -0.9), new Point2D.Double(0., 0.), Color.red));
        int v2 = complex.addVertex(new GLVertex(new Point2D.Double(0.9, -0.9), new Point2D.Double(1., 0.), Color.green));
        int v3 = complex.addVertex(new GLVertex(new Point2D.Double(-0.9, 0.9), new Point2D.Double(0., 1.), Color.yellow));
        int v4 = complex.addVertex(new GLVertex(new Point2D.Double(0.9, 0.9), new Point2D.Double(1., 1.), Color.blue));
        GLMesh mesh = complex.addGLMesh(GL11.GL_TRIANGLE_STRIP);
        mesh.addIndex(v1);
        mesh.addIndex(v2);
        mesh.addIndex(v3);
        mesh.addIndex(v4);
        return complex;
    }

    //    public void setupQuad() {
    //        // Vertices, the order is not important.
    //        float[] vertices = { -0.5f, 0.5f, 0f,    // Left top         ID: 0
    //                -0.5f, -0.5f, 0f,   // Left bottom      ID: 1
    //                0.5f, -0.5f, 0f,    // Right bottom     ID: 2
    //                0.5f, 0.5f, 0f      // Right left       ID: 3
    //        };
    //        // Sending data to OpenGL requires the usage of (flipped) byte buffers
    //        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
    //        verticesBuffer.put(vertices);
    //        verticesBuffer.flip();
    //
    //        // OpenGL expects to draw vertices in counter clockwise order by default
    //        byte[] indices = {
    //                // Left bottom triangle
    //                0, 1, 2,
    //                // Right top triangle
    //                2, 3, 0 };
    //        indicesCount = indices.length;
    //        ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
    //        indicesBuffer.put(indices);
    //        indicesBuffer.flip();
    //
    //        // Create a new Vertex Array Object in memory and select it (bind)
    //        // A VAO can have up to 16 attributes (VBO's) assigned to it by default
    //        vaoId = GL30.glGenVertexArrays();
    //        GL30.glBindVertexArray(vaoId);
    //
    //        // Create a new Vertex Buffer Object in memory and select it (bind)
    //        // A VBO is a collection of Vectors which in this case resemble the location of each vertex.
    //        vboId = GL15.glGenBuffers();
    //        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
    //        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
    //        // Put the VBO in the attributes list at index 0
    //        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
    //        // Deselect (bind to 0) the VBO
    //        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    //
    //        // Deselect (bind to 0) the VAO
    //        GL30.glBindVertexArray(0);
    //
    //        // Create a new VBO for the indices and select it (bind)
    //        vboiId = GL15.glGenBuffers();
    //        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
    //        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
    //        // Deselect (bind to 0) the VBO
    //        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    //    }
}
