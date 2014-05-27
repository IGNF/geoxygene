package fr.ign.cogit.geoxygene.util.gl;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGLComplex<VertexType extends GLVertex> implements
        GLComplex {

    private double minX = 0.;
    private double minY = 0.;
    private final List<GLInput> inputs = new ArrayList<GLInput>();
    private boolean mayOverlap = false; // set to true if polygons may
    // raw list of all vertices
    private final List<VertexType> vertices = new ArrayList<VertexType>();
    private final List<GLMesh> meshes = new ArrayList<GLMesh>();
    private int stride = -1;
    private String id = "no id defined";

    /**
     * Default constructor
     * 
     * @param minX
     * @param minY
     */
    public AbstractGLComplex(String id, double minX, double minY) {
        super();
        this.setId(id);
        this.setMinX(minX);
        this.setMinY(minY);
    }

    /**
     * Add an input variable for the vertex shader
     * 
     * @param variableName
     *            variable name as defined/used in the GLSL vertex shader
     * @param variableType
     *            variable type (GL_GLOAT, GL_DOUBLE, GL_INT, etc...)
     * @param variableComponentCount
     *            number of variable type in the variable (1, 2, 3 or 4)
     *            corresponding to (type, vec2, vec3, vec4)
     */
    public final boolean addInput(String variableName, int variableLocation,
            int variableType, int variableComponentCount, boolean normalized) {
        GLInput input = new GLInput(variableLocation, variableName,
                variableComponentCount, variableType, normalized);
        this.inputs.add(input);
        this.stride = -1; // invalidate stride value (lazy instantiation)
        return true;
    }

    /**
     * @return the id
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.util.gl.GLComplex#getStride()
     */
    @Override
    public int getStride() {
        if (this.stride <= 0) {
            this.stride = 0;
            for (GLInput input : this.getInputs()) {
                this.stride += input.getComponentCount()
                        * GLTools.sizeInBytes(input.getGlType());
            }
        }
        return this.stride;
    }

    /**
     * @return the inputs
     */
    public final List<GLInput> getInputs() {
        return this.inputs;
    }

    /**
     * @return the vertices
     */
    @Override
    public final List<VertexType> getVertices() {
        return this.vertices;
    }

    /**
     * @return the meshes
     */
    @Override
    public final List<GLMesh> getMeshes() {
        return this.meshes;
    }

    /**
     * @param minX
     *            the minX to set
     */
    public final void setMinX(double minX) {
        this.minX = minX;
    }

    /**
     * @param minY
     *            the minY to set
     */
    public final void setMinY(double minY) {
        this.minY = minY;
    }

    /**
     * @return true if contained geometries may overlap (invalid for
     *         transparency)
     */
    @Override
    public final boolean mayOverlap() {
        return this.mayOverlap;
    }

    /**
     * set it to true if contained geometries may overlap (invalid for
     * transparency)
     */
    public final void setMayOverlap(boolean mayOverlap) {
        this.mayOverlap = mayOverlap;
    }

    @Override
    public final double getMinX() {
        return this.minX;
    }

    @Override
    public final double getMinY() {
        return this.minY;
    }

    @SuppressWarnings("unchecked")
    public final int addVertex(final VertexType vertex) {
        this.vertices.add((VertexType) vertex.clone());
        this.invalidateBuffers();
        return this.vertices.size() - 1;
    }

    @Override
    public final GLMesh addGLMesh(int glType) {
        GLMesh mesh = new GLMesh(glType, this);
        this.meshes.add(mesh);
        this.invalidateBuffers();
        return mesh;
    }

    @Override
    public final boolean removeMesh(GLMesh mesh) {
        if (!this.meshes.remove(mesh)) {
            return false;
        }
        this.invalidateBuffers();
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + this.getId();
    }

}