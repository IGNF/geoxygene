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

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * GL Primitive is a representation of a 2D Object with 2D coordinates, Texture
 * coordinates and Color components
 * It is composed of primitives types which are themselves composed of index
 * lists so it can mix different type of primitives, but the insertion order
 * is not kept
 * 
 * @author JeT
 * 
 */
public class GLComplex {

    private static final Logger logger = Logger.getLogger(GLComplex.class.getName()); // logger

    //    private static final Integer[] IntegerConversionObject = new Integer[] {}; // static object for list to array conversion
    private final List<GLVertex> vertices = new ArrayList<GLVertex>(); // raw list of all vertices
    private final List<GLMesh> meshes = new ArrayList<GLMesh>();
    private FloatBuffer verticesBuffer = null; // Vertex buffer (VBO array) constructed from vertices
    private IntBuffer indicesBuffer = null; // Index Buffer (VBO indices) constructed from flattened indicesPerType
    private Texture texture = null;

    private int vaoId = -1; // VAO index
    private int vboVerticesId = -1; // VBO Vertices index
    private int vboIndicesId = -1; // VBO Indices index
    private double minX = 0.;
    private double minY = 0.;
    private double overallOpacity = 1.;
    private GLRenderingCapability renderingCapability = null;
    private boolean mayOverlap = false; // set to true if polygons may overlap (invalid for transparency)

    public enum GLRenderingCapability {
        POSITION, COLOR, TEXTURE
    }

    /**
     * Default constructor
     */
    public GLComplex(double minX, double minY) {
        this.minX = minX;
        this.minY = minY;
    }

    /**
     * @return the opacity for that entire complex
     */
    public double getOverallOpacity() {
        return this.overallOpacity;
    }

    /**
     * Overall opacity is used to set the opacity of the FBO in which this
     * object has been drawn without an transparency.
     * Set the vertices opacity to 1
     */
    public void setOverallOpacity(double overallOpacity) {
        this.overallOpacity = overallOpacity;
        for (GLVertex vertex : this.vertices) {
            vertex.setAlpha(1.f);
        }
    }

    /**
     * @return true if contained geometries may overlap (invalid for
     *         transparency)
     */
    public boolean mayOverlap() {
        return this.mayOverlap;
    }

    /**
     * set it to true if contained geometries may overlap (invalid for
     * transparency)
     */
    public void setMayOverlap(boolean mayOverlap) {
        this.mayOverlap = mayOverlap;
    }

    /**
     * @return the renderingCapability
     */
    public GLRenderingCapability getRenderingCapability() {
        if (this.renderingCapability == null) {
            if (this.getTexture() != null) {
                this.renderingCapability = GLRenderingCapability.TEXTURE;
            } else {
                this.renderingCapability = GLRenderingCapability.COLOR;
            }
        }
        return this.renderingCapability;
    }

    /**
     * @return the texture
     */
    public Texture getTexture() {
        return this.texture;
    }

    /**
     * @param texture
     *            the texture to set
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
        this.renderingCapability = null;

    }

    /**
     * @return the minX
     */
    public double getMinX() {
        return this.minX;
    }

    /**
     * @return the minY
     */
    public double getMinY() {
        return this.minY;
    }

    /**
     * @return the vertices
     */
    public List<GLVertex> getVertices() {
        return this.vertices;
    }

    /**
     * @return the meshes
     */
    public List<GLMesh> getMeshes() {
        return this.meshes;
    }

    /**
     * Set the color of all vertices contained in this complex
     * 
     * @param color
     *            color to be set to all vertices
     */
    public void setColor(final java.awt.Color color) {
        for (GLVertex vertex : this.vertices) {
            vertex.setRGBA(color);
        }
    }

    public void invalidateBuffers() {
        if (this.vboIndicesId >= 0) {
            GL15.glDeleteBuffers(this.vboIndicesId);
            this.indicesBuffer = null;
            this.vboIndicesId = -1;
        }
        if (this.vboVerticesId >= 0) {
            GL15.glDeleteBuffers(this.vboVerticesId);
            this.verticesBuffer = null;
            this.vboVerticesId = -1;
        }
        if (this.vaoId >= 0) {
            GL30.glDeleteVertexArrays(this.vaoId);
            this.vaoId = -1;
        }
    }

    /**
     * add a vertex into the primitive and return the vertex index.
     * Vertex content is copied into a new Vertex
     * 
     * @param vertex
     * @return
     */
    public int addVertex(final GLVertex vertex) {
        this.vertices.add(new GLVertex(vertex));
        this.invalidateBuffers();
        return this.vertices.size() - 1;
    }

    /**
     * Add a gl primitive described by the connectivity between vertices
     * The GLMesh is created and added to the complex
     * The returned Mesh must be filled afterward
     */
    public GLMesh addGLMesh(int glType) {
        GLMesh mesh = new GLMesh(glType, this);
        this.meshes.add(mesh);
        this.invalidateBuffers();
        return mesh;
    }

    /**
     * Remove a mesh from this complex
     */
    public boolean removeMesh(GLMesh mesh) {
        if (!this.meshes.remove(mesh)) {
            return false;
        }
        this.invalidateBuffers();
        return true;
    }

    /**
     * return the vertices buffer that has already been flipped at creation
     * and is rewinded at each method call
     * 
     * @return the verticesBuffer
     */
    public FloatBuffer getFlippedVerticesBuffer() {
        if (this.verticesBuffer == null) {
            this.verticesBuffer = BufferUtils.createFloatBuffer(this.vertices.size() * GLVertex.NumberOfFloatValues);
            //BufferUtils.createByteBuffer(this.vertices.size() * GLVertex.VERTEX_BYTESIZE).asFloatBuffer();
            for (GLVertex vertex : this.vertices) {
                // Add position, color and texture floats to the buffer
                //                System.err.println("add XYZ to vertex buffer: " + Arrays.toString(vertex.getXYZ()));
                //                System.err.println("add UV to vertex buffer: " + Arrays.toString(vertex.getUV()));
                //                System.err.println("add RGBA to vertex buffer: " + Arrays.toString(vertex.getRGBA()));
                this.verticesBuffer.put(new float[] { (vertex.getXYZ()[0]), (vertex.getXYZ()[1]), vertex.getXYZ()[2] });
                this.verticesBuffer.put(vertex.getUV());
                this.verticesBuffer.put(vertex.getRGBA());

            }
            this.verticesBuffer.flip();

        }
        this.verticesBuffer.rewind();
        return this.verticesBuffer;
    }

    /**
     * return the vertices index buffer that has already been flipped at
     * creation
     * and is rewinded at each method call
     * 
     * @return the indicesBuffer
     */
    public IntBuffer getFlippedIndicesBuffer() {
        if (this.indicesBuffer == null) {
            this.generateIndicesBuffers();
        }
        this.indicesBuffer.rewind();
        return this.indicesBuffer;
    }

    /**
     * Compute the acceleration structure for all the contained meshes
     * 
     * @param glComplex
     * @return
     */
    private void generateIndicesBuffers() {
        // count the total number of vertices
        int nbIndices = 0;
        for (GLMesh mesh : this.getMeshes()) {
            nbIndices += mesh.getIndices().size();
        }
        // fill the indices buffer (VBO indices)
        // and generate a range of indices in the indices buffer
        this.indicesBuffer = BufferUtils.createIntBuffer(nbIndices);
        int currentStartIndex = 0;
        for (GLMesh mesh : this.getMeshes()) {
            for (int nIndex = 0; nIndex < mesh.getIndices().size(); nIndex++) {
                this.indicesBuffer.put(mesh.getIndices().get(nIndex).intValue());
            }
            mesh.setFirstIndex(currentStartIndex);
            currentStartIndex = currentStartIndex + mesh.getIndices().size();
            mesh.setLastIndex(currentStartIndex - 1);
        }
        this.indicesBuffer.flip();
    }

    /**
     * Bind Buffers with gl Context
     */
    private void initializeShaderConfiguration() {
        // Create a new Vertex Array Object in memory and select it (bind)
        this.vaoId = GL30.glGenVertexArrays();
        if (this.vaoId <= 0) {
            logger.error("VAO ID is invalid " + this.vaoId);
        }
        glBindVertexArray(this.vaoId);

        // create the Vertex VBO
        this.vboVerticesId = glGenBuffers();
        if (this.vboVerticesId <= 0) {
            logger.error("VBO(Vertices) ID is invalid " + this.vboVerticesId);
        }
        glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vboVerticesId);
        glBufferData(GL_ARRAY_BUFFER, this.getFlippedVerticesBuffer(), GL_STATIC_DRAW);

        for (int nAttrib = 0; nAttrib < GLVertex.ATTRIBUTES_COUNT; nAttrib++) {

            glEnableVertexAttribArray(GLVertex.ATTRIBUTES_ID[nAttrib]);
            GL20.glVertexAttribPointer(GLVertex.ATTRIBUTES_ID[nAttrib], GLVertex.ATTRIBUTES_COMPONENT_NUMBER[nAttrib], GLVertex.ATTRIBUTES_TYPE[nAttrib],
                    false, GLVertex.VERTEX_BYTESIZE, GLVertex.ATTRIBUTES_BYTEOFFSET[nAttrib]);
            glEnableVertexAttribArray(GLVertex.ATTRIBUTES_ID[nAttrib]);
            //                        System.err.println("index = " + GLVertex.ATTRIBUTES_ID[nAttrib] + " size = " + GLVertex.ATTRIBUTES_COMPONENT_NUMBER[nAttrib] + " type = "
            //                                + GLVertex.ATTRIBUTES_TYPE[nAttrib] + " normalized = false stride = " + GLVertex.VERTEX_BYTESIZE + " offset = "
            //                               + GLVertex.ATTRIBUTES_BYTEOFFSET[nAttrib]);

        }

        //        displayBuffer(this.getFlippedVerticesBuffer());

        //        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // create the index VBO
        this.vboIndicesId = glGenBuffers();
        if (this.vboIndicesId <= 0) {
            logger.error("VBO(Indices) ID is invalid " + this.vboIndicesId);
        }

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.vboIndicesId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, this.getFlippedIndicesBuffer(), GL_STATIC_DRAW);

        //        displayBuffer(this.getFlippedIndicesBuffer());

        glBindVertexArray(0);

    }

    private static void displayBuffer(FloatBuffer buffer) {
        buffer.mark();
        System.err.println("Float buffer limit = " + buffer.limit() + " capacity = " + buffer.capacity() + " position = " + buffer.position());
        for (int i = 0; i < buffer.limit(); i++) {
            float value = buffer.get(i);
            System.err.println("\t#" + i + ":" + value);
        }
        buffer.reset();
    }

    private static void displayBuffer(IntBuffer buffer) {
        buffer.mark();
        System.err.println("Int buffer limit = " + buffer.limit() + " capacity = " + buffer.capacity() + " position = " + buffer.position());
        for (int i = 0; i < buffer.limit(); i++) {
            int value = buffer.get(i);
            System.err.println("\t#" + i + ":" + value);
        }
        buffer.reset();
    }

    /**
     * Get the Vertex Array Object Index associated with one GLContext. If not
     * defined already
     * all VBA & VBOs are generated (binded to the open GL context)
     * 
     * @return the vaoId
     */
    public int getVaoId() {
        if (this.vaoId <= 0) {
            this.initializeShaderConfiguration();
        }
        return this.vaoId;
    }

    /**
     * Get the Vertex Buffer Object Index associated with one GLContext. If not
     * defined already
     * all VBA & VBOs are generated (binded to the open GL context)
     * 
     * @return the vboArrayId
     */
    public int getVboVerticesId() {
        if (this.vboVerticesId <= 0) {
            this.initializeShaderConfiguration();
        }
        return this.vboVerticesId;
    }

    /**
     * Get the Vertex Array Object Index associated with one GLContext. If not
     * defined already
     * all VBA & VBOs are generated (binded to the open GL context)
     * 
     * @return the vboArrayId
     */
    public int getVboIndicesId() {
        if (this.vboIndicesId <= 0) {
            this.initializeShaderConfiguration();
        }
        return this.vboIndicesId;
    }

    /**
     * Add all meshes from a gl complex
     * 
     * @param subComplex
     */
    public void addGLComplex(GLComplex subComplex) {

        // map between old indices and new ones
        HashMap<Integer, Integer> indicesLUT = new HashMap<Integer, Integer>();

        if (subComplex == null) {
            return;
        }
        if (subComplex.mayOverlap()) {
            this.setMayOverlap(true);
        }
        // add all vertices
        int vertexOldIndex = 0;
        for (GLVertex vertex : subComplex.getVertices()) {
            int vertexNewIndex = this.addVertex(vertex);
            indicesLUT.put(vertexOldIndex, vertexNewIndex);
            vertexOldIndex++;
        }
        // add a copy of all meshes (map indices with computed LUT)
        for (GLMesh oldMesh : subComplex.getMeshes()) {
            GLMesh newMesh = this.addGLMesh(oldMesh.getGlType());
            for (int oldIndex : oldMesh.getIndices()) {
                newMesh.addIndex(indicesLUT.get(oldIndex));
            }
        }
        this.invalidateBuffers();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GLComplex [#vertices=" + this.vertices.size() + ", #meshes=" + this.meshes.size() + ", minX=" + this.minX + ", minY=" + this.minY + "]";
    }

}
