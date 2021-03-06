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
 * the terms of the GNU Lesser General private License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General private License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General private License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.appli.gl;

import static org.lwjgl.util.glu.GLU.GLU_TESS_BEGIN;
import static org.lwjgl.util.glu.GLU.GLU_TESS_COMBINE;
import static org.lwjgl.util.glu.GLU.GLU_TESS_END;
import static org.lwjgl.util.glu.GLU.GLU_TESS_VERTEX;
import static org.lwjgl.util.glu.GLU.gluNewTess;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellator;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.render.primitive.Colorizer;
import fr.ign.cogit.geoxygene.appli.render.primitive.Parameterizer;
import fr.ign.cogit.geoxygene.appli.render.primitive.SolidColorizer;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
import fr.ign.cogit.geoxygene.appli.task.Task;
import fr.ign.cogit.geoxygene.appli.task.TaskManager;
import fr.ign.cogit.geoxygene.function.ConstantFunction;
import fr.ign.cogit.geoxygene.function.FunctionEvaluationException;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveParameter;
import fr.ign.cogit.geoxygene.style.texture.SimpleTexture;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLComplexRenderer;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleVertex;
import fr.ign.cogit.geoxygene.util.gl.GLTexture;
import fr.ign.cogit.geoxygene.util.math.BernsteinPolynomial;

/**
 * @author JeT Utility functions for GL drawing
 */
public class GLComplexFactory {

    private static final Logger logger = Logger.getLogger(GLComplexFactory.class.getName()); // logger
    private static final int BEZIER_SAMPLE_COUNT = 20;
    private static final Point2d DEFAULT_UV = new Point2d(0, 0);

    /**
     * Private constructor for utility class
     */
    private GLComplexFactory() {
        // utility class
    }

    /****************************************************************************************************
     * LINES
     */

    /**
     * Create a gl primitive that is just drawn as lines
     */
    public static GLSimpleComplex createQuickLine(String id, List<? extends ICurve> curves, final Color color, final Parameterizer parameterizer, double minX, double minY) {
        GLSimpleComplex primitive = new GLSimpleComplex(id, minX, minY);
        for (ICurve curve : curves) {
            createQuickLine(primitive, curve, color, parameterizer);
        }
        // primitive.setRenderer(renderer);
        return primitive;
    }

    /**
     * Create a gl primitive that is just drawn as lines
     */
    private static void createQuickLine(GLSimpleComplex primitive, final ICurve curve, final Color color, final Parameterizer parameterizer) {
        double minX = primitive.getMinX();
        double minY = primitive.getMinY();

        if (parameterizer != null) {
            parameterizer.initializeParameterization();
        }
        GLMesh outlineMesh = primitive.addGLMesh(GL11.GL_LINE_STRIP);
        for (IDirectPosition position : curve.coord()) {
            double p[] = new double[3];
            p[0] = position.getX() - minX;
            p[1] = position.getY() - minY;
            p[2] = 0; // position.getZ();
            Point2d uv = DEFAULT_UV;
            if (parameterizer != null) {
                uv = parameterizer.getTextureCoordinates(p);
            }
            GLSimpleVertex vertex = new GLSimpleVertex();
            vertex.setXYZ((float) p[0], (float) p[1], (float) p[2]);
            vertex.setUV((float) uv.x, (float) uv.y);
            vertex.setRGBA(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
            outlineMesh.addIndex(primitive.addVertex(vertex));
        }
    }

    /****************************************************************************************************
     * POLYGONS
     */

    /**
     * Create a gl primitive that is just polygons frontiers drawn as lines
     */
    public static GLSimpleComplex createQuickPolygons(String id, List<IPolygon> polygons, final Color color, final Parameterizer parameterizer, double minX, double minY) {
        GLSimpleComplex primitive = new GLSimpleComplex(id, minX, minY);

        for (IPolygon polygon : polygons) {
            createQuickPolygon(primitive, polygon, color, parameterizer);
        }
        // primitive.setRenderer(renderer);
        return primitive;
    }

    /**
     * Create a gl primitive filled surface
     */
    public static GLSimpleComplex createFilledPolygons(String id, List<IPolygon> polygons, final Color color, final Parameterizer parameterizer, double minX, double minY) {
        GLSimpleComplex primitive = new GLSimpleComplex(id, minX, minY);

        for (IPolygon polygon : polygons) {
            createFilledPolygon(primitive, polygon, color, parameterizer);
        }
        // primitive.setRenderer(renderer);
        return primitive;
    }

    /**
     * Tesselate an OGC IGeometry into a GLPrimitive
     */
    private static void createQuickPolygon(GLSimpleComplex primitive, final IPolygon polygon, final Color color, final Parameterizer parameterizer) {
        double minX = primitive.getMinX();
        double minY = primitive.getMinY();
        if (parameterizer != null) {
            parameterizer.initializeParameterization();
        }

        GLMesh outlineMesh = primitive.addGLMesh(GL11.GL_LINE_LOOP);
        for (int outerFrontierPointIndex = 0; outerFrontierPointIndex < polygon.exteriorCoord().size(); outerFrontierPointIndex++) {
            IDirectPosition outerFrontierPoint = polygon.exteriorCoord().get(outerFrontierPointIndex);
            double p[] = new double[3];
            p[0] = outerFrontierPoint.getX() - minX;
            p[1] = outerFrontierPoint.getY() - minY;
            p[2] = 0; // outerFrontierPoint.getZ();
            Point2d uv = DEFAULT_UV;
            if (parameterizer != null) {
                uv = parameterizer.getTextureCoordinates(p);
            }

            GLSimpleVertex vertex = new GLSimpleVertex();
            vertex.setXYZ((float) p[0], (float) p[1], (float) p[2]);
            vertex.setUV((float) uv.x, (float) uv.y);
            vertex.setRGBA(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
            int vertexId = primitive.addVertex(vertex);
            outlineMesh.addIndex(vertexId);
        }
        for (int innerFrontierIndex = 0; innerFrontierIndex < polygon.sizeInterior(); innerFrontierIndex++) {

            IDirectPositionList interiorCoord = polygon.interiorCoord(innerFrontierIndex);
            for (int innerFrontierPointIndex = 0; innerFrontierPointIndex < interiorCoord.size(); innerFrontierPointIndex++) {
                IDirectPosition innerFrontierPoint = interiorCoord.get(innerFrontierPointIndex);
                // Point2d innerFrontierTextureCoordinates =
                // polygon.getInnerFrontierTextureCoordinates(innerFrontierIndex,
                // innerFrontierPointIndex);
                double p[] = new double[3];
                p[0] = innerFrontierPoint.getX() - minX;
                p[1] = innerFrontierPoint.getY() - minY;
                p[2] = 0; // outerFrontierPoint.getZ();
                Point2d uv = DEFAULT_UV;
                if (parameterizer != null) {
                    uv = parameterizer.getTextureCoordinates(p);
                }

                GLSimpleVertex vertex = new GLSimpleVertex();
                vertex.setXYZ((float) p[0], (float) p[1], (float) p[2]);
                vertex.setUV((float) uv.x, (float) uv.y);
                vertex.setRGBA(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
                int vertexId = primitive.addVertex(vertex);
                outlineMesh.addIndex(vertexId);
            }
        }
        if (parameterizer != null) {
            parameterizer.finalizeParameterization();
        }
    }

    /**
     * Tesselate a polygon into a GLPrimitive
     */
    private static void createFilledPolygon(GLSimpleComplex primitive, final IPolygon polygon, final Color color, final Parameterizer parameterizer) {
        double minX = primitive.getMinX();
        double minY = primitive.getMinY();

        if (parameterizer != null) {
            parameterizer.initializeParameterization();
        }
        // tesselation
        GLUtessellator tesselator = gluNewTess();

        // Set callback functions
        GLPrimitiveTessCallback callback = new GLPrimitiveTessCallback(primitive);
        tesselator.gluTessCallback(GLU_TESS_VERTEX, callback);
        tesselator.gluTessCallback(GLU_TESS_BEGIN, callback);
        tesselator.gluTessCallback(GLU_TESS_END, callback);
        tesselator.gluTessCallback(GLU_TESS_COMBINE, callback);
        tesselator.gluTessProperty(GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_POSITIVE);

        tesselator.gluTessBeginPolygon(null);
        // outer frontier
        tesselator.gluTessBeginContour();
        for (int outerFrontierPointIndex = 0; outerFrontierPointIndex < polygon.exteriorCoord().size(); outerFrontierPointIndex++) {
            IDirectPosition outerFrontierPoint = polygon.exteriorCoord().get(outerFrontierPointIndex);
            // Point2d outerFrontierTextureCoordinates =
            // polygon.getOuterFrontierTextureCoordinates(outerFrontierPointIndex);
            // point coordinates
            double vertex[] = new double[3];
            vertex[0] = outerFrontierPoint.getX() - minX;
            vertex[1] = outerFrontierPoint.getY() - minY;
            vertex[2] = 0; // outerFrontierPoint.getZ();
            Point2d uv = DEFAULT_UV;
            if (parameterizer != null) {
                // vertex is expressed in local-object coordinates
                uv = parameterizer.getTextureCoordinates(vertex);
                // System.err.println("texture coordinates = " + uv +
                // " parameterizer = " +
                // parameterizer.getClass().getSimpleName());
            }

            float[] data = new float[] { (float) vertex[0], (float) vertex[1], (float) vertex[2], (float) uv.x, (float) uv.y, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f,
                    color.getAlpha() / 255f };
            // System.err.println("tess input data = " + Arrays.toString(data));
            tesselator.gluTessVertex(vertex, 0, data);
            // System.err.println("set exterior #" + outerFrontierPointIndex +
            // " vertex " + vertex[0] + ", " + vertex[1] + ", " + vertex[2] +
            // " uv = " + uv);
        }
        tesselator.gluTessEndContour();

        for (int innerFrontierIndex = 0; innerFrontierIndex < polygon.sizeInterior(); innerFrontierIndex++) {

            // IRing innerFrontier = polygon.getInterior(innerFrontierIndex);
            tesselator.gluTessBeginContour();

            IDirectPositionList interiorCoord = polygon.interiorCoord(innerFrontierIndex);
            for (int innerFrontierPointIndex = 0; innerFrontierPointIndex < interiorCoord.size(); innerFrontierPointIndex++) {
                IDirectPosition innerFrontierPoint = interiorCoord.get(innerFrontierPointIndex);
                // Point2d innerFrontierTextureCoordinates =
                // polygon.getInnerFrontierTextureCoordinates(innerFrontierIndex,
                // innerFrontierPointIndex);

                double vertex[] = new double[3];
                vertex[0] = innerFrontierPoint.getX() - minX;
                vertex[1] = innerFrontierPoint.getY() - minY;
                vertex[2] = 0; // innerFrontierPoint.getZ();
                Point2d uv = DEFAULT_UV;
                if (parameterizer != null) {
                    uv = parameterizer.getTextureCoordinates(vertex);
                }

                float[] data = new float[] { (float) vertex[0], (float) vertex[1], (float) vertex[2], (float) uv.x, (float) uv.y, color.getRed() / 255f, color.getGreen() / 255f,
                        color.getBlue() / 255f, color.getAlpha() / 255f };
                tesselator.gluTessVertex(vertex, 0, data);
                // System.err.println("set interior #" + innerFrontierIndex +
                // " vertex " + vertex[0] + ", " + vertex[1] + ", " +
                // vertex[2]);
            }

            tesselator.gluTessEndContour();
        }
        tesselator.gluTessEndPolygon();

        if (parameterizer != null) {
            parameterizer.finalizeParameterization();
        }
    }

    public static GLSimpleComplex createQuickPoints(String id, List<IGeometry> geometries, final Color color, final Parameterizer parameterizer, double minX, double minY) {
        GLSimpleComplex primitive = new GLSimpleComplex(id, minX, minY);
        GLMesh mesh = primitive.addGLMesh(GL11.GL_POINTS);

        for (IGeometry geometry : geometries) {
            createQuickPoints(primitive, mesh, geometry, color, parameterizer);
        }
        return primitive;
    }

    private static void createQuickPoints(GLSimpleComplex primitive, GLMesh mesh, IGeometry geometry, final Color color, final Parameterizer parameterizer) {
        double minX = primitive.getMinX();
        double minY = primitive.getMinY();
        double p[] = new double[3];
        p[0] = geometry.centroid().getX() - minX;
        p[1] = geometry.centroid().getY() - minY;
        p[2] = 0; // Z
        Point2d uv = DEFAULT_UV;
        if (parameterizer != null) {
            uv = parameterizer.getTextureCoordinates(p);
        }
        GLSimpleVertex vertex = new GLSimpleVertex();
        vertex.setXYZ((float) p[0], (float) p[1], (float) p[2]);
        vertex.setUV((float) uv.x, (float) uv.y);
        vertex.setRGBA(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        mesh.addIndex(primitive.addVertex(vertex));
    }

    public static GLSimpleComplex createColorizedPoints(String id, IGeometry geometry, final Colorizer colorizer, final Parameterizer parameterizer, double minX, double minY,
            GLComplexRenderer renderer) {
        GLSimpleComplex primitive = new GLSimpleComplex(id, minX, minY);
        // primitive.setRenderer(renderer);
        return primitive;
    }

    /*************************************************************************************
     * SHAPE
     */
    /**
     * Tesselate a list of java shape2D into a GLPrimitive
     */
    public static GLSimpleComplex toGLComplex(String id, final List<? extends Shape> shapes, double minX, double minY, GLComplexRenderer renderer) {
        GLSimpleComplex primitive = new GLSimpleComplex(id, minX, minY);

        for (Shape shape : shapes) {
            toGLComplex(primitive, shape);
        }
        // primitive.setRenderer(renderer);
        return primitive;
    }

    public static GLSimpleComplex toGLComplex(String id, Shape shape, double minX, double minY) {
        GLSimpleComplex primitive = new GLSimpleComplex(id, minX, minY);

        toGLComplex(primitive, shape);
        // primitive.setRenderer(renderer);
        return primitive;
    }

    /**
     * Tesselate a java shape2D into a GLPrimitive
     */
    private static void toGLComplex(GLSimpleComplex primitive, final Shape shape) {
        double minX = primitive.getMinX();
        double minY = primitive.getMinY();
        // tesselation
        GLUtessellator tesselator = gluNewTess();

        // Set callback functions
        GLPrimitiveTessCallback callback = new GLPrimitiveTessCallback(primitive);
        tesselator.gluTessCallback(GLU_TESS_VERTEX, callback);
        tesselator.gluTessCallback(GLU_TESS_BEGIN, callback);
        tesselator.gluTessCallback(GLU_TESS_END, callback);
        tesselator.gluTessCallback(GLU_TESS_COMBINE, callback);
        if (shape instanceof Path2D) {
            Path2D path = (Path2D) shape;

            switch (path.getWindingRule()) {
            case Path2D.WIND_EVEN_ODD:
                tesselator.gluTessProperty(GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_ODD);
                break;
            case Path2D.WIND_NON_ZERO:
                tesselator.gluTessProperty(GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_NONZERO);
                break;
            default:
                logger.warn("unknown winding rule " + path.getWindingRule());
            }
        }

        tesselator.gluTessBeginPolygon(null);

        float lastX = 0;
        float lastY = 0;
        float lastMoveX = 0;
        float lastMoveY = 0;

        double[] vertex = null;
        float[] data = null;
        float[] coords = new float[6];

        PathIterator iter = shape.getPathIterator(null); // ,5) add a number on
        // here to simplify
        // verts

        int rule = iter.getWindingRule();
        switch (rule) {
        case PathIterator.WIND_EVEN_ODD:
            tesselator.gluTessProperty(GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_ODD);
            break;
        case PathIterator.WIND_NON_ZERO:
            tesselator.gluTessProperty(GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_NONZERO);
            break;
        }
        // System.err.println("----------------------------------------------------------------------------");
        while (!iter.isDone()) {

            int currentSegment = iter.currentSegment(coords);
            coords[0] -= minX;
            coords[1] -= minY;
            coords[2] -= minX;
            coords[3] -= minY;
            coords[4] -= minX;
            coords[5] -= minY;
            switch (currentSegment) {

            case PathIterator.SEG_MOVETO: // 1 point (2 vars) in coords
                tesselator.gluTessBeginContour();
                lastX = lastMoveX = coords[0];
                lastY = lastMoveY = coords[1];
                vertex = new double[] { coords[0], coords[1], 0. };
                data = new float[] { coords[0], coords[1], 0, 0, 0, 0, 0, 0, 0 };
                tesselator.gluTessVertex(vertex, 0, data);
                // System.err.println(" shape " + vertex[0] + " " + vertex[1]);
                // System.err.println("MOVETO " + coords[0] + "x" + coords[1]);
                break;
            case PathIterator.SEG_LINETO: // 1 point
                vertex = new double[] { coords[0], coords[1], 0. };
                data = new float[] { coords[0], coords[1], 0, 0, 0, 0, 0, 0, 0 };
                // System.err.println(" shape " + vertex[0] + " " + vertex[1]);
                tesselator.gluTessVertex(vertex, 0, data);
                lastX = coords[0];
                lastY = coords[1];
                // System.err.println("LINETO " + coords[0] + "x" + coords[1]);
                break;

            case PathIterator.SEG_QUADTO: // 2 points (1 control point)
                // System.err.println("QUAD FROM " + lastX + "x" + lastY +
                // " TO " + coords[2] + "x" + coords[3]);
                for (int i = 1; i <= BEZIER_SAMPLE_COUNT; i++) {
                    float t = i / (float) BEZIER_SAMPLE_COUNT;

                    double px = BernsteinPolynomial.evalQuadratic(lastX, coords[0], coords[2], t);
                    double py = BernsteinPolynomial.evalQuadratic(lastY, coords[1], coords[3], t);
                    vertex = new double[] { px, py, 0 };
                    data = new float[] { (float) px, (float) py, 0, 0, 0, 0, 0, 0, 0 };
                    // System.err.println(" shape " + vertex[0] + " " +
                    // vertex[1]);
                    tesselator.gluTessVertex(vertex, 0, data);
                }
                lastX = coords[2];
                lastY = coords[3];
                break;

            case PathIterator.SEG_CUBICTO: // 3 points (2 control points)
                // System.err.println("CUBIC FROM " + lastX + "x" + lastY +
                // " TO " + coords[4] + "x" + coords[5]);
                for (int i = 1; i <= BEZIER_SAMPLE_COUNT; i++) {
                    float t = i / (float) BEZIER_SAMPLE_COUNT;

                    double px = BernsteinPolynomial.evalCubic(lastX, coords[0], coords[2], coords[4], t);
                    double py = BernsteinPolynomial.evalCubic(lastY, coords[1], coords[3], coords[5], t);
                    vertex = new double[] { px, py, 0 };
                    data = new float[] { (float) px, (float) py, 0, 0, 0, 0, 0, 0, 0 };
                    tesselator.gluTessVertex(vertex, 0, data);
                    // System.err.println(" shape " + vertex[0] + " " +
                    // vertex[1]);
                }
                lastX = coords[4];
                lastY = coords[5];
                break;

            case PathIterator.SEG_CLOSE:
                lastX = lastMoveX;
                lastY = lastMoveY;
                tesselator.gluTessEndContour();
                // System.err.println("CLOSE " + lastMoveX + "x" + lastMoveY);
                break;
            }
            iter.next();
        }
        tesselator.gluTessEndPolygon();
    }

    /**
     * Create a polygon outline with the given "stroke"
     * 
     * @param polygons
     * @param stroke
     * @param minX
     * @param minY
     * @return
     */
    public static GLComplex createPolygonOutlines(String id, List<IPolygon> polygons, Stroke stroke, double minX, double minY) {
        boolean simpleRendering = stroke.getExpressiveStroke() == null;
        if (!simpleRendering) {
            String method = stroke.getExpressiveStroke().getRenderingMethod();
            simpleRendering = method == null || method.isEmpty();
        }

        if (simpleRendering) {
            GLSimpleComplex primitive = new GLSimpleComplex(id, minX, minY);
            for (IPolygon polygon : polygons) {
                LineTesselator.createPolygonOutline(primitive, polygon, stroke, minX, minY);
            }
            primitive.setColor(stroke.getColor());
            primitive.setOverallOpacity(stroke.getColor().getAlpha() / 255.);
            return primitive;
        }
        ExpressiveDescriptor expressive = stroke.getExpressiveStroke();
        SimpleTexture paperTexture = (SimpleTexture) (expressive.getExpressiveParameter("paperTexture").getValue());
        ExpressiveParameter paperHeightInCm = expressive.getExpressiveParameter("PaperSizeInCm");
        ExpressiveParameter mapScale = expressive.getExpressiveParameter("PaperReferenceMapScale");
        ExpressiveParameter transitionSize = expressive.getExpressiveParameter("transitionSize");
        if (paperTexture == null || paperHeightInCm == null || mapScale == null || transitionSize == null) {
            logger.error("The ExpressiveStroke does not have the needed parameters to create an Expressive Stroke geometry");
            return null;
        }

        TextureManager.getInstance();
        GLTexture glPaperTex = TextureManager.getTexture(paperTexture);

        if (glPaperTex == null) {
            logger.error("Cannot create the paper texture : the texture cannot be built");
            return null;
        }
        List<ILineString> curves = new ArrayList<ILineString>();
        for (IPolygon polygon : polygons) {
            curves.add(new GM_LineString(polygon.getExterior().coord()));
            for (IRing interior : polygon.getInterior()) {
                curves.add(new GM_LineString(interior.coord()));
            }
        }
        GLBezierShadingComplex complex = createBezierThickCurves(id, stroke, minX, minY, curves, glPaperTex, (Double) paperHeightInCm.getValue(), (Double) mapScale.getValue(),
                (Double) transitionSize.getValue());
        return complex;
    }

    public static GLBezierShadingComplex createBezierThickCurves(String id, Stroke stroke, double minX, double minY, List<ILineString> curves, GLTexture glPaperTex, double paperHeightInCm,
            double mapScale, double transitionSize) {
        GLBezierShadingComplex complex = new GLBezierShadingComplex(id, minX, minY, glPaperTex);
        createBezierThickCurves(id, complex, stroke, minX, minY, curves, glPaperTex, paperHeightInCm, mapScale, transitionSize);
        return complex;
    }

    
    public static GLPaintingComplex createPaintingThickCurves(String id, Stroke stroke, double minX, double minY, List<ILineString> curves, GLTexture glPaperTex, double paperHeightInCm,
        double minAngle, int paperWidthInPixels,int paperHeightInPixels, double mapScale,int sampleSize) {
    GLPaintingComplex complex = new GLPaintingComplex(id, minX, minY);
    createPaintingThickCurves(id, complex, minX, minY, curves, glPaperTex, sampleSize, paperHeightInCm, mapScale, stroke.getStrokeWidth(),stroke.getStroke(), minAngle, paperWidthInPixels,paperHeightInPixels);
    return complex;
}

    
    

    /**
     * @param id
     * @param stroke
     * @param minX
     * @param minY
     * @param strtex
     * @param complex
     * @param curves
     */
    private static void createPaintingThickCurves(String id, GLPaintingComplex complex, double minX, double minY, List<ILineString> curves, GLTexture paperTexture, int sampleSize,
            double paperHeightInCm, double mapScale, double strokewidth, Color strokecolor, double minAngle, int paperWidthInPixels, int paperHeightInPixels) {
        // GLTexture paperTexture = GLTextureManager.getInstance().getTexture(
        // strtex.getPaperTextureFilename());
        Colorizer colorizer = new SolidColorizer(strokecolor);
        for (ILineString line : curves) {
            try {
                Task tesselateThickLineTask = LinePaintingTesselator.tesselateThickLine(id, complex, line.getControlPoint(), new ConstantFunction(strokewidth / 2.), new ConstantFunction(0),
                        sampleSize, minAngle, minX, minY, colorizer, paperWidthInPixels, paperHeightInPixels, paperHeightInCm, mapScale);
                tesselateThickLineTask.start();
                TaskManager.startAndWait(tesselateThickLineTask);
            } catch (FunctionEvaluationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void createBezierThickCurves(String id, GLBezierShadingComplex complex, Stroke stroke, double minX, double minY, List<ILineString> curves, GLTexture paperTexture,
            double paperHeightInCm, double mapScale, double transitionSize) {

        int paperWidthInPixels = paperTexture.getTextureWidth();
        int paperHeightInPixels = paperTexture.getTextureHeight();
        for (ILineString line : curves) {
            try {
                Task tesselateThickLineTask = BezierTesselator.tesselateThickLine(id, complex, line.getControlPoint(), stroke.getStrokeWidth(), transitionSize, minX, minY,
                        new SolidColorizer(stroke.getColor()), paperWidthInPixels, paperHeightInPixels, paperHeightInCm, mapScale);
                tesselateThickLineTask.start();
                TaskManager.startAndWait(tesselateThickLineTask);
            } catch (FunctionEvaluationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a polygon outline with the given "stroke"
     * 
     * @param data
     *            .getPolygons()
     * @param stroke
     * @param minX
     * @param minY
     * @return
     */
    public static GLSimpleComplex createShapeOutline(String id, Shape shape, Stroke stroke, double minX, double minY) {
        GLSimpleComplex primitive = LineTesselator.createThickLine(id, shape, stroke, minX, minY);
        // primitive.setRenderer(renderer);
        return primitive;
    }

}
