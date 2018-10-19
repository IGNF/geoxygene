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

import org.apache.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.render.texture.ExpressiveRendering;
import fr.ign.cogit.geoxygene.util.gl.AbstractGLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLInput;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLRenderingCapability;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleVertex;
import fr.ign.cogit.geoxygene.util.gl.GLTexture;
import fr.ign.cogit.geoxygene.util.gl.GLTools;

/**
 * GL Primitive is a representation of a 2D Object with 2D coordinates, Texture
 * coordinates and Color components. It is composed of primitives types which are
 * themselves composed of index lists so it can mix different type of
 * primitives, but the insertion order is not kept
 * 
 * @author JeT
 * 
 */
public class GLTextComplex extends AbstractGLComplex<GLSimpleVertex> implements
        GLComplex {

    private static final Logger logger = Logger.getLogger(GLTextComplex.class
            .getName()); // logger

    private FloatBuffer verticesBuffer = null; // Vertex buffer (VBO array)
                                               // constructed from vertices
    private IntBuffer indicesBuffer = null; // Index Buffer (VBO indices)
                                            // constructed from flattened
                                            // indicesPerType
    private GLTexture texture = null;

    private int vaoId = -1; // VAO index
    private int vboVerticesId = -1; // VBO Vertices index
    private int vboIndicesId = -1; // VBO Indices index
    private double overallOpacity = 1.;
    private GLSimpleRenderingCapability[] renderingCapabilities = null;
    private ExpressiveRendering expressiveRendering = null;
    private IFeature feature = null;
    int stride = -1;

    public enum GLSimpleRenderingCapability implements GLRenderingCapability {
        POSITION, COLOR, TEXTURE,
    }

    /**
     * Default constructor
     */
    public GLTextComplex(String id, double minX, double minY, IFeature f) {
        super(id, minX, minY);
        this.addInput(GLSimpleVertex.VertexPositionVarName,
                GLSimpleVertex.vertexPostionLocation, GL11.GL_FLOAT, 3, false);
        this.addInput(GLSimpleVertex.VertexUVVarName,
                GLSimpleVertex.vertexUVLocation, GL11.GL_FLOAT, 2, false);
        this.addInput(GLSimpleVertex.VertexColorVarName,
                GLSimpleVertex.vertexColorLocation, GL11.GL_FLOAT, 4, false);
        this.feature = f;
    }


    /**
     * @return the opacity for that entire complex
     */
    @Override
    public double getOverallOpacity() {
        return this.overallOpacity;
    }

    /**
     * @param expressiveRendering
     *            the expressiveRendering to set
     */
    public void setExpressiveRendering(ExpressiveRendering expressiveRendering) {
        this.expressiveRendering = expressiveRendering;
    }

    /**
     * @return the expressiveRendering
     */
    public ExpressiveRendering getExpressiveRendering() {
        return this.expressiveRendering;
    }

    /**
     * Overall opacity is used to set the opacity of the FBO in which this
     * object has been drawn without an transparency. Set the vertices opacity
     * to 1
     */
    public void setOverallOpacity(double overallOpacity) {
        this.overallOpacity = overallOpacity;
    }

    /**
     * @return the renderingCapability
     */
    @Override
    public GLSimpleRenderingCapability[] getRenderingCapabilities() {
        if (this.renderingCapabilities == null) {
            if (this.getTexture() != null) {
                this.renderingCapabilities = new GLSimpleRenderingCapability[] {
                        GLSimpleRenderingCapability.COLOR,
                        GLSimpleRenderingCapability.TEXTURE };
            } else {
                this.renderingCapabilities = new GLSimpleRenderingCapability[] { GLSimpleRenderingCapability.COLOR };
            }
        }
        return this.renderingCapabilities;
    }

    /**
     * @return the texture
     */
    public GLTexture getTexture() {
        return this.texture;
    }

    /**
     * @param texture
     *            the texture to set
     */
    public void setTexture(GLTexture texture) {
        this.texture = texture;
        this.renderingCapabilities = null;

    }

    /**
     * Set the color of all vertices contained in this complex
     * 
     * @param color
     *            color to be set to all vertices
     */
    public void setColor(final java.awt.Color color) {
        for (GLSimpleVertex vertex : this.getVertices()) {
            vertex.setRGBA(color);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.util.gl.GLComplex#invalidateBuffers()
     */
    @Override
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
     * @return the feature
     */
    public IFeature getFeature() {
        return this.feature;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.util.gl.GLComplex#getFlippedVerticesBuffer()
     */
    @Override
    public FloatBuffer getFlippedVerticesBuffer() {
        if (this.verticesBuffer == null) {
            this.verticesBuffer = BufferUtils
                    .createFloatBuffer(this.getVertices().size()
                            * this.getStride() / (Float.SIZE / 8));
            // BufferUtils.createByteBuffer(this.vertices.size() *
            // GLSimpleVertex.VERTEX_BYTESIZE).asFloatBuffer();
            for (GLSimpleVertex vertex : this.getVertices()) {
                // Add position, color and texture floats to the buffer
                // System.err.println("add XYZ to vertex buffer: " +
                // Arrays.toString(vertex.getXYZ()));
                // System.err.println("add UV to vertex buffer: " +
                // Arrays.toString(vertex.getUV()));
                // System.err.println("add RGBA to vertex buffer: " +
                // Arrays.toString(vertex.getRGBA()));
                this.verticesBuffer.put(new float[] { (vertex.getXYZ()[0]),
                        (vertex.getXYZ()[1]), vertex.getXYZ()[2] });
                this.verticesBuffer.put(vertex.getUV());
                this.verticesBuffer.put(vertex.getRGBA());

            }
            this.verticesBuffer.flip();
            // System.err.println("create a new vertex buffer = ");
            // GLTools.displayBuffer(this.verticesBuffer);

        }
        this.verticesBuffer.rewind();
        return this.verticesBuffer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.util.gl.GLComplex#getFlippedIndicesBuffer()
     */
    @Override
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
                this.indicesBuffer
                        .put(mesh.getIndices().get(nIndex).intValue());
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
    private void generateVao() {
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

        int byteShift = 0;
        for (GLInput input : this.getInputs()) {
            GL20.glVertexAttribPointer(input.getLocation(),
                    input.getComponentCount(), input.getGlType(),
                    input.isNormalized(), this.getStride(), byteShift);
            // System.err.println("loc = " + input.getLocation() + " "
            // + input.getComponentCount() + " " + input.getGlType()
            // + " stride = " + this.getStride() + " shift = " + byteShift
            // + " name = " + input.getName() + " "
            // + this.getClass().getSimpleName());
            byteShift += input.getComponentCount()
                    * GLTools.sizeInBytes(input.getGlType());
            glEnableVertexAttribArray(input.getLocation());

        }
        // System.err.println("previous");
        // for (int nAttrib = 0; nAttrib < GLSimpleVertex.ATTRIBUTES_COUNT;
        // nAttrib++)
        // {
        // GL20.glVertexAttribPointer(GLSimpleVertex.ATTRIBUTES_ID[nAttrib],
        // GLSimpleVertex.ATTRIBUTES_COMPONENT_NUMBER[nAttrib],
        // GLSimpleVertex.ATTRIBUTES_TYPE[nAttrib],
        // false, GLSimpleVertex.VERTEX_BYTESIZE,
        // GLSimpleVertex.ATTRIBUTES_BYTEOFFSET[nAttrib]);
        // System.err.println("loc = " + GLSimpleVertex.ATTRIBUTES_ID[nAttrib] +
        // " " +
        // GLSimpleVertex.ATTRIBUTES_COMPONENT_NUMBER[nAttrib] + " "
        // + GLSimpleVertex.ATTRIBUTES_TYPE[nAttrib] + " stride = " +
        // GLSimpleVertex.VERTEX_BYTESIZE + " shift = " +
        // GLSimpleVertex.ATTRIBUTES_BYTEOFFSET[nAttrib]);
        // glEnableVertexAttribArray(GLSimpleVertex.ATTRIBUTES_ID[nAttrib]);
        //
        // }

        glBufferData(GL_ARRAY_BUFFER, this.getFlippedVerticesBuffer(),
                GL_STATIC_DRAW);

        // displayBuffer(this.getFlippedVerticesBuffer());

        // glBindBuffer(GL_ARRAY_BUFFER, 0);

        // create the index VBO
        this.vboIndicesId = glGenBuffers();
        if (this.vboIndicesId <= 0) {
            logger.error("VBO(Indices) ID is invalid " + this.vboIndicesId);
        }

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.vboIndicesId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, this.getFlippedIndicesBuffer(),
                GL_STATIC_DRAW);

        // displayBuffer(this.getFlippedIndicesBuffer());

        glBindVertexArray(0);

    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.util.gl.GLComplex#getVaoId()
     */
    @Override
    public int getVaoId() {
        if (this.vaoId <= 0) {
            this.generateVao();
        }
        return this.vaoId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.util.gl.GLComplex#getVboVerticesId()
     */
    @Override
    public int getVboVerticesId() {
        if (this.vboVerticesId <= 0) {
            this.generateVao();
        }
        return this.vboVerticesId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.util.gl.GLComplex#getVboIndicesId()
     */
    @Override
    public int getVboIndicesId() {
        if (this.vboIndicesId <= 0) {
            this.generateVao();
        }
        return this.vboIndicesId;
    }

    // /**
    // * Add all meshes from a gl complex
    // *
    // * @param subComplex
    // */
    // @Override
    // public void addGLComplex(GLComplex subComplex) {
    //
    // if (subComplex instanceof GLSimpleComplex) {
    // GLSimpleComplex subComplex = (GLSimpleComplex) subComplex;
    //
    // }
    // // map between old indices and new ones
    // HashMap<Integer, Integer> indicesLUT = new HashMap<Integer, Integer>();
    //
    // if (subComplex == null) {
    // return;
    // }
    // if (subComplex.mayOverlap()) {
    // this.setMayOverlap(true);
    // }
    // // add all vertices
    // int vertexOldIndex = 0;
    // for (GLSimpleVertex vertex : subComplex.getVertices()) {
    // int vertexNewIndex = this.addVertex(vertex);
    // indicesLUT.put(vertexOldIndex, vertexNewIndex);
    // vertexOldIndex++;
    // }
    // // add a copy of all meshes (map indices with computed LUT)
    // for (GLMesh oldMesh : subComplex.getMeshes()) {
    // GLMesh newMesh = this.addGLMesh(oldMesh.getGlType());
    // for (int oldIndex : oldMesh.getIndices()) {
    // newMesh.addIndex(indicesLUT.get(oldIndex));
    // }
    // }
    // this.invalidateBuffers();
    // }

}
