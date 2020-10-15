package fr.ign.cogit.geoxygene.util.gl;

import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2d;
import static org.lwjgl.opengl.GL11.glVertex2d;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class TessCallback extends GLUtessellatorCallbackAdapter {

    private static final Logger logger = LogManager.getLogger(TessCallback.class.getName()); // logger

    @Override
    public void edgeFlag(final boolean boundaryEdge) {
        //      System.err.println("edgeFlag " + boundaryEdge);
        super.edgeFlag(boundaryEdge);
    }

    @Override
    public void combine(final double[] coords, final Object[] data, final float[] weight, final Object[] outData) {
        double[] vertex = new double[6];
        // fill new vertex position
        vertex[0] = coords[0];
        vertex[1] = coords[1];
        vertex[2] = coords[2];
        outData[0] = vertex;
        // fill data by interpolation
        for (int i = 3; i < 6; i++) {
            vertex[i] = 0;
            for (int j = 0; j < 4; j++) {
                if (weight[j] > 0 && data[j] != null) {
                    vertex[i] += weight[j] * ((double[]) data[j])[i];
                }
            }
        }
        outData[0] = vertex;
    }

    @Override
    public void beginData(final int type, final Object polygonData) {
        //      System.err.println("beginData " + type + " " + polygonData);
        super.beginData(type, polygonData);
    }

    @Override
    public void edgeFlagData(final boolean boundaryEdge, final Object polygonData) {
        //      System.err.println("edgeFlagData " + boundaryEdge + " " + polygonData);
        super.edgeFlagData(boundaryEdge, polygonData);
    }

    @Override
    public void vertexData(final Object vertexData, final Object polygonData) {
        //      System.err.println("vertexData " + vertexData + " " + polygonData);
        super.vertexData(vertexData, polygonData);
    }

    @Override
    public void endData(final Object polygonData) {
        //      System.err.println("endData " + polygonData);
        super.endData(polygonData);
    }

    @Override
    public void combineData(final double[] coords, final Object[] data, final float[] weight, final Object[] outData, final Object polygonData) {
        throw new UnsupportedOperationException("TessCallback::combineData() is called by I thought it shouldn't...");
    }

    @Override
    public void begin(final int type) {
        //      System.err.println("begin " + type);
        //      glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glBegin(type);
    }

    @Override
    public void end() {
        //      System.err.println("end");
        glEnd();
        //      glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    @Override
    public void vertex(final Object coords) {
        //        System.out.println("" + ((double[]) coords)[0] + ";" + ((double[]) coords)[1] + ";" + ((double[]) coords)[2] + ";;" + ((double[]) coords)[3] + ";"
        //                + ((double[]) coords)[4]);
        //        GL11.glColor3d(((double[]) coords)[3], ((double[]) coords)[4], 0.);
        glTexCoord2d(((double[]) coords)[3], ((double[]) coords)[4]);
        glVertex2d(((double[]) coords)[0], ((double[]) coords)[1]);
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
        logger.error("Tesselation error : " + errnum + " + " + polygonData.toString());
        super.errorData(errnum, polygonData);
    }

}
