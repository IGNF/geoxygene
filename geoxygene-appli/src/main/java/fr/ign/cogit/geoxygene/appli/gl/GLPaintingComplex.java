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

import fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRendering;
import fr.ign.cogit.geoxygene.util.gl.AbstractGLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLInput;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLRenderingCapability;
import fr.ign.cogit.geoxygene.util.gl.GLTexture;
import fr.ign.cogit.geoxygene.util.gl.GLTools;

/**
 * @author JeT
 * 
 */
public class GLPaintingComplex extends AbstractGLComplex<GLPaintingVertex> {

    private static final Logger logger = Logger
            .getLogger(GLPaintingComplex.class.getName()); // logger

    // private static final Integer[] IntegerConversionObject = new Integer[]
    // {}; // static object for list to array conversion
    // raw list of all vertices
    private FloatBuffer verticesBuffer = null; // Vertex buffer (VBO
                                               // array)
    // constructed from vertices
    private IntBuffer indicesBuffer = null; // Index Buffer (VBO indices)

    private double overallOpacity = 1.;
    private int vaoId = -1; // VAO index
    private int vboVerticesId = -1; // VBO Vertices index
    private int vboIndicesId = -1; // VBO Indices index
    private GLPaintingRenderingCapability[] renderingCapabilities = null;
    private StrokeTextureExpressiveRendering expressiveRendering = null;
    private GLTexture brushTexture = null;
    private GLTexture paperTexture = null;

    public enum GLPaintingRenderingCapability implements GLRenderingCapability {
        EXPRESSIVE_STROKE_TEXTURE
    }

    /**
     * Default constructor
     */
    public GLPaintingComplex(String id, double minX, double minY) {
        super(id, minX, minY);
        this.addInput(GLPaintingVertex.vertexPositionVariableName,
                GLPaintingVertex.vertexPositionLocation, GL11.GL_FLOAT, 2,
                false);
        this.addInput(GLPaintingVertex.vertexUVVariableName,
                GLPaintingVertex.vertexUVLocation, GL11.GL_FLOAT, 2, false);
        this.addInput(GLPaintingVertex.vertexNormalVariableName,
                GLPaintingVertex.vertexNormalLocation, GL11.GL_FLOAT, 2, false);
        this.addInput(GLPaintingVertex.vertexCurvatureVariableName,
                GLPaintingVertex.vertexCurvatureLocation, GL11.GL_FLOAT, 1,
                false);
        this.addInput(GLPaintingVertex.vertexThicknessVariableName,
                GLPaintingVertex.vertexThicknessLocation, GL11.GL_FLOAT, 1,
                false);
        this.addInput(GLPaintingVertex.vertexColorVariableName,
                GLPaintingVertex.vertexColorLocation, GL11.GL_FLOAT, 4, false);
        this.addInput(GLPaintingVertex.vertexMaxUVariableName,
                GLPaintingVertex.vertexMaxULocation, GL11.GL_FLOAT, 1, false);

    }

    /**
     * @return the opacity for that entire complex
     */
    public double getOverallOpacity() {
        return this.overallOpacity;
    }

    /**
     * Overall opacity is used to set the opacity of the FBO in which this
     * object has been drawn without an transparency. Set the vertices opacity
     * to 1
     */
    public void setOverallOpacity(double overallOpacity) {
        this.overallOpacity = overallOpacity;
        // for (GLSimpleVertex vertex : this.vertices) {
        // vertex.setAlpha(1.f);
        // }
    }

    /**
     * @return the brushTexture
     */
    public GLTexture getBrushTexture() {
        if (this.brushTexture == null) {
            this.brushTexture = GLTextureManager.getInstance().getTexture(
                    this.getExpressiveRendering().getBrushTextureFilename());
            this.brushTexture.setMipmap(false);
        }
        return this.brushTexture;
    }

    /**
     * @param brushTexture
     *            the brushTexture to set
     */
    public void setBrushTexture(GLTexture brushTexture) {
        this.brushTexture = brushTexture;
    }

    /**
     * invalidate lazy getter
     */
    public void invalidateBrushTexture() {
        this.brushTexture = null;
    }

    /**
     * @return the paperTexture
     */
    public GLTexture getPaperTexture() {
        if (this.paperTexture == null) {
            if (this.getExpressiveRendering() == null) {
                logger.error("try to get paper texture filename from a GL primitive that has no expressive rendering class set...");
                logger.error("primitive ID = " + this.getId());
                return null;
            }
            this.paperTexture = GLTextureManager.getInstance().getTexture(
                    this.getExpressiveRendering().getPaperTextureFilename());
            this.paperTexture.setMipmap(false);
        }
        return this.paperTexture;
    }

    /**
     * @param paperTexture
     *            the paperTexture to set
     */
    public void setPaperTexture(GLTexture paperTexture) {
        this.paperTexture = paperTexture;
    }

    /**
     * invalidate lazy getter
     */
    public void invalidatePaperTexture() {
        this.paperTexture = null;
    }

    /**
     * @return the expressiveRendering
     */
    public StrokeTextureExpressiveRendering getExpressiveRendering() {
        return this.expressiveRendering;
    }

    /**
     * @param expressiveRendering
     *            the expressiveRendering to set
     */
    public void setExpressiveRendering(
            StrokeTextureExpressiveRendering expressiveRendering) {
        this.expressiveRendering = expressiveRendering;
    }

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

    @Override
    public FloatBuffer getFlippedVerticesBuffer() {
        if (this.verticesBuffer == null) {
            this.verticesBuffer = BufferUtils
                    .createFloatBuffer(this.getVertices().size()
                            * this.getStride() / (Float.SIZE / 8));
            for (GLPaintingVertex vertex : this.getVertices()) {
                this.verticesBuffer.put(vertex.getXY());
                this.verticesBuffer.put(vertex.getUV());
                this.verticesBuffer.put(vertex.getNormal());
                this.verticesBuffer.put(vertex.getCurvature());
                this.verticesBuffer.put(vertex.getThickness());
                this.verticesBuffer.put(vertex.getColor());
                this.verticesBuffer.put(vertex.getMaxU());
            }
            this.verticesBuffer.flip();

        }
        this.verticesBuffer.rewind();
        return this.verticesBuffer;
    }

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
            // System.err
            // .println("loc = " + input.getLocation() + " "
            // + input.getComponentCount() + " "
            // + input.getGlType() + " stride = "
            // + this.getStride() + " shift = " + byteShift);
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

    @Override
    public GLRenderingCapability[] getRenderingCapabilities() {
        if (this.renderingCapabilities == null) {
            this.renderingCapabilities = new GLPaintingRenderingCapability[] { GLPaintingRenderingCapability.EXPRESSIVE_STROKE_TEXTURE };
        }
        return this.renderingCapabilities;
    }

}
