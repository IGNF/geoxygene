package fr.ign.cogit.geoxygene.util.gl;

import org.apache.log4j.Logger;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellatorCallbackAdapter;

/**
 * @author JeT
 * 
 */
/**
 * Callback class used in gl tesselation process
 * 
 * @author JeT
 * 
 */
public class GLPrimitiveTessCallback extends GLUtessellatorCallbackAdapter {

    private static final Logger logger = Logger
            .getLogger(GLPrimitiveTessCallback.class.getName()); // logger
    private GLSimpleComplex primitive = null;
    private GLMesh currentGLMesh = null;

    /**
     * Constructor
     * 
     * @param primitive
     *            primitive filled by tesselation
     */
    public GLPrimitiveTessCallback(final GLSimpleComplex primitive) {
        this.primitive = primitive;
    }

    @Override
    public void edgeFlag(final boolean boundaryEdge) {
        // System.err.println("edgeFlag " + boundaryEdge);
        super.edgeFlag(boundaryEdge);
    }

    @Override
    public void combine(final double[] coords, final Object[] data,
            final float[] weight, final Object[] outData) {
        // System.err.println("combine " + coords[0] + "," + coords[1] + "," +
        // coords[2]);
        final int coordinatesElementCount = GLSimpleVertex.NumberOfFloatValues;
        float[] combinedData = new float[coordinatesElementCount];
        // fill new vertex position
        combinedData[0] = (float) coords[0];
        combinedData[1] = (float) coords[1];
        combinedData[2] = (float) coords[2];
        // fill data by interpolation
        for (int i = 3; i < coordinatesElementCount; i++) {
            combinedData[i] = 0;
            for (int j = 0; j < 4; j++) {
                if (weight[j] > 0 && data[j] != null) {
                    combinedData[i] += weight[j] * ((float[]) data[j])[i];
                }
            }
        }
        // System.err.println("combined Data = " +
        // Arrays.toString(combinedData));
        outData[0] = combinedData;
    }

    @Override
    public void beginData(final int type, final Object polygonData) {
        // System.err.println("beginData " + type + " " + polygonData);
        super.beginData(type, polygonData);
    }

    @Override
    public void edgeFlagData(final boolean boundaryEdge,
            final Object polygonData) {
        // System.err.println("edgeFlagData " + boundaryEdge + " " +
        // polygonData);
        super.edgeFlagData(boundaryEdge, polygonData);
    }

    @Override
    public void vertexData(final Object vertexData, final Object polygonData) {
        // System.err.println("vertexData " + vertexData + " " + polygonData);
        super.vertexData(vertexData, polygonData);
    }

    @Override
    public void endData(final Object polygonData) {
        // System.err.println("endData " + polygonData);
        super.endData(polygonData);
    }

    @Override
    public void combineData(final double[] coords, final Object[] data,
            final float[] weight, final Object[] outData,
            final Object polygonData) {
        // System.err.println("combineData " + coords[0] + "," + coords[1] + ","
        // + coords[2]);
        throw new UnsupportedOperationException(
                "TessCallback::combineData() is called. I thought it shouldn't...");
    }

    @Override
    public void begin(final int type) {
        this.currentGLMesh = this.primitive.addGLMesh(type);
        // System.err.println("begin " + type);
        // System.err.println("GL_TRIANGLE_FAN = " + GL11.GL_TRIANGLE_FAN);
        // System.err.println("GL_TRIANGLE_STRIP = " + GL11.GL_TRIANGLE_STRIP);
        // System.err.println("GL_TRIANGLES = " + GL11.GL_TRIANGLES);
        // System.err.println("GL_LINE_LOOP = " + GL11.GL_LINE_LOOP);
        // System.err.println("begin " + type);
    }

    @Override
    public void end() {
        this.currentGLMesh = null;
        // System.err.println("end");
    }

    @Override
    public void vertex(final Object coordsObject) {
        float[] coords = (float[]) coordsObject;
        // System.err.println("tess vertex " + coords[0] + " " + coords[1]);
        // System.err.println("tess     uv " + coords[3] + " " + coords[4]);
        // System.err.println("tess   rgba " + coords[5] + " " + coords[6] + " "
        // + coords[7] + " " + coords[8]);
        // glTexCoord2d(coords[3], coords[4]);
        GLSimpleVertex vertex = new GLSimpleVertex();
        vertex.setXYZ(coords[0], coords[1], coords[2]);
        vertex.setUV(coords[3], coords[4]);
        vertex.setRGBA(coords[5], coords[6], coords[7], coords[8]);
        int vertexId = this.primitive.addVertex(vertex);
        if (this.currentGLMesh != null) {

            this.currentGLMesh.addIndex(vertexId);
        }
    }

    @Override
    public void error(final int errnum) {
        String estring;
        estring = GLU.gluErrorString(errnum);
        logger.error("Tessellation Error Number: " + errnum);
        logger.error("Tessellation Error: " + estring);
        super.error(errnum);
    }

    @Override
    public void errorData(final int errnum, final Object polygonData) {
        logger.error("Tesselation error : " + errnum + " + "
                + polygonData.toString());
        super.errorData(errnum, polygonData);
    }

}
