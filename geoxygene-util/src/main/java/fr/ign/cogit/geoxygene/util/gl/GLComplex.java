package fr.ign.cogit.geoxygene.util.gl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

public interface GLComplex {

    public abstract String getId();

    /**
     * stride is the size in bytes of one data chunk (GLVertex)
     * 
     * @return
     */
    public abstract int getStride();

    /**
     * @return the minX
     */
    public abstract double getMinX();

    /**
     * @return the minY
     */
    public abstract double getMinY();

    /**
     * @return the vertices
     */
    public abstract List<? extends GLVertex> getVertices();

    public abstract boolean mayOverlap();

    /**
     * @return the meshes
     */
    public abstract List<GLMesh> getMeshes();

    public abstract void invalidateBuffers();

    /**
     * Add a gl primitive described by the connectivity between vertices The
     * GLMesh is created and added to the complex The returned Mesh must be
     * filled afterward
     */
    public abstract GLMesh addGLMesh(int glType);

    /**
     * Remove a mesh from this complex
     */
    public abstract boolean removeMesh(GLMesh mesh);

    /**
     * return the vertices buffer that has already been flipped at creation and
     * is rewinded at each method call
     * 
     * @return the verticesBuffer
     */
    public abstract FloatBuffer getFlippedVerticesBuffer();

    /**
     * return the vertices index buffer that has already been flipped at
     * creation and is rewinded at each method call
     * 
     * @return the indicesBuffer
     */
    public abstract IntBuffer getFlippedIndicesBuffer();

    /**
     * Get the Vertex Array Object Index associated with one GLContext. If not
     * defined already all VBA & VBOs are generated (binded to the open GL
     * context)
     * 
     * @return the vaoId
     */
    public abstract int getVaoId();

    /**
     * Get the Vertex Buffer Object Index associated with one GLContext. If not
     * defined already all VBA & VBOs are generated (binded to the open GL
     * context)
     * 
     * @return the vboArrayId
     */
    public abstract int getVboVerticesId();

    /**
     * Get the Vertex Array Object Index associated with one GLContext. If not
     * defined already all VBA & VBOs are generated (binded to the open GL
     * context)
     * 
     * @return the vboArrayId
     */
    public abstract int getVboIndicesId();

    GLRenderingCapability[] getRenderingCapabilities();

    public abstract double getOverallOpacity();

    // /**
    // * Add all meshes from a gl complex
    // *
    // * @param subComplex
    // */
    // public abstract void addGLComplex(GLComplex subComplex);

}