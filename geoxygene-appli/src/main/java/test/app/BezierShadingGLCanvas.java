package test.app;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.appli.render.primitive.RandomColorizer;
import fr.ign.cogit.geoxygene.appli.task.Task;
import fr.ign.cogit.geoxygene.appli.task.TaskManager;
import fr.ign.cogit.geoxygene.function.FunctionEvaluationException;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLContext;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLProgramAccessor;
import fr.ign.cogit.geoxygene.util.gl.GLTools;

/**
 * @author JeT
 * 
 */
public class BezierShadingGLCanvas extends AWTGLCanvas implements
        MouseListener, MouseMotionListener, MouseWheelListener {

    private static final long serialVersionUID = 1695930822908723425L;

    private static final Logger logger = Logger
            .getLogger(BezierShadingGLCanvas.class.getName()); // logger

    private GLContext glContext = null;
    // private GLComplex complex = null;
    private AffineTransform pressedTransform = null;
    private AffineTransform transform = new AffineTransform();
    private int clickX = -1, clickY = -1;
    private boolean drag = false;
    private BezierShading app = null;
    private boolean renderingInProgress = false;

    // private boolean wireframe = false;

    /**
     * @throws LWJGLException
     */
    public BezierShadingGLCanvas(BezierShading app) throws LWJGLException {
        this.app = app;
        this.addMouseListener(this);
        this.addMouseWheelListener(this);
        this.addMouseMotionListener(this);
    }

    public void reloadContext() throws GLException {
        if (this.glContext != null) {
            try {
                this.makeCurrent();
            } catch (LWJGLException e) {
                e.printStackTrace();
            }

            this.glContext.disposeContext();
        }
        super.initGL();
        try {
            this.glContext = getGL4Context();
        } catch (GLException e) {
            e.printStackTrace();
        }

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
        try {
            this.glContext = getGL4Context();
        } catch (GLException e) {
            e.printStackTrace();
            System.exit(0);
        }
        this.setViewport(this.app.getEnvelope());

    }

    public void updateViewport() {
        this.setViewport(null);
    }

    public void setViewport(IEnvelope envelope) {
        if (envelope == null) {
            return;
        }

        this.transform = new AffineTransform();
        this.transform.translate(
                this.app.getEnvelope().getLowerCorner().getX(), this.app
                        .getEnvelope().getLowerCorner().getY());
        if (this.app.getEnvelope().width() * this.app.getEnvelope().length() > 0) {
            double scale = Math.min(
                    this.app.getEnvelope().width() / this.getWidth(), this.app
                            .getEnvelope().length() / this.getHeight());
            this.transform.scale(scale, scale);
        }
        // System.err.println(" set viewport to envelope "
        // + envelope.getLowerCorner() + " x "
        // + envelope.getUpperCorner());
        // System.err.println("transform = " + this.transform);
        // Point2D origin = new Point2D.Double(0, 0);
        // Point2D transformedOrigin = new Point2D.Double(0, 0);
        // Point2D inverseTransformedOrigin = new Point2D.Double(0, 0);
        // this.transform.transform(origin, transformedOrigin);
        // try {
        // this.transform.inverseTransform(origin, inverseTransformedOrigin);
        // } catch (NoninvertibleTransformException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // System.err.println("origin = " + origin);
        // System.err.println("transform = " + transformedOrigin);
        // System.err.println("inverse = " + inverseTransformedOrigin);

    }

    /**
     * Set the GL uniform view matrix (stored in viewMatrixLocation) using a
     * viewport
     * 
     * @throws GLException
     */
    private boolean setGLViewMatrix(GLComplex complex) throws GLException {
        double minX = complex.getMinX();
        double minY = complex.getMinY();

        AffineTransform modelToViewTransform = new AffineTransform(
                this.transform);
        try {
            modelToViewTransform.invert();
        } catch (NoninvertibleTransformException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        GLProgram program = this.glContext.getCurrentProgram();
        float m00 = (float) (modelToViewTransform.getScaleX());
        float m02 = (float) (modelToViewTransform.getTranslateX() + minX
                * modelToViewTransform.getScaleX());
        float m11 = (float) (modelToViewTransform.getScaleY());
        float m12 = (float) (modelToViewTransform.getTranslateY() + minY
                * modelToViewTransform.getScaleY());
        program.setUniform1f(
                BezierShadingGLCanvas.m00ModelToViewMatrixUniformVarName, m00);
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(
                BezierShadingGLCanvas.m02ModelToViewMatrixUniformVarName, m02);
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(
                BezierShadingGLCanvas.m11ModelToViewMatrixUniformVarName, m11);
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(
                BezierShadingGLCanvas.m12ModelToViewMatrixUniformVarName, m12);
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        float width = this.getWidth();
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        float height = this.getHeight();
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(BezierShadingGLCanvas.screenWidthUniformVarName,
                width);
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(BezierShadingGLCanvas.screenHeightUniformVarName,
                height);

        // // System.err.println("transform = " + this.transform + " screen = "
        // // + width + "x" + height);
        // System.err.println("translation m02 = " + m02 + " m12 = " + m12);
        // System.err.println("scaling     m00 = " + m00 + " m11 = " + m11);
        // System.err.println("canvas width = " + width + " height = " +
        // height);
        // // shader transform : -1 + 2 * (p.x * m00 + m02) / (screenWidth + 1),
        // 1
        // // - 2 * ( p.y * m11 + m12 ) / ( screenHeight + 1 )
        // float px = 0;
        // float py = 0;
        // float ppx = -1 + 2 * (px * m00 + m02) / (width + 1);
        // float ppy = 1 - 2 * (py * m11 + m12) / (height + 1);
        // System.err.println("sample transform: " + px + "x" + py + " => " +
        // ppx
        // + "x" + ppy);
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        return true;
    }

    /**
     * @param widthFunction
     * @param shiftFunction
     * @return
     */
    public static GLComplex createComplex(String id, List<ILineString> lines,
            double minX, double minY, double lineWidth, double transitionSize) {
        GLBezierShadingComplex complex = new GLBezierShadingComplex(id, minX,
                minY);
        for (ILineString line : lines) {
            try {
                Task task = BezierTesselator.tesselateThickLine(id, complex,
                        line.getControlPoint(), lineWidth, transitionSize,
                        minX, minY, new RandomColorizer());
                task.start();
                TaskManager.startAndWait(task);
                // LinePaintingTesselator
                // .tesselateThickLine(complex, line.getControlPoint(),
                // widthFunction, shiftFunction, sampleSize,
                // minAngle, minX, minY, new RandomColorizer());
            } catch (FunctionEvaluationException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return complex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.lwjgl.opengl.AWTGLCanvas#paintGL()
     */
    @Override
    protected void paintGL() {
        if (this.renderingInProgress) {
            return;
        }
        try {
            this.renderingInProgress = true;
            if (this.app.getLines() == null || this.app.getEnvelope() == null) {
                System.err.println("no file loaded. skip painting");
                return;
            }
            GLTools.glCheckError("start painting");
            try {
                this.makeCurrent();
            } catch (LWJGLException e) {
                e.printStackTrace();
            }
            try {
                GLProgram program = this.glContext
                        .setCurrentProgram(BezierShadingGLCanvas.bezierProgramName);
            } catch (GLException e1) {
                e1.printStackTrace();
            }
            GLComplex complex = createComplex(
                    "bezier #" + (new Date().getTime()), this.app.getLines(),
                    this.app.getEnvelope().minX(), this.app.getEnvelope()
                            .minY(), this.app.lineWidth,
                    this.app.transitionSize);
            try {
                this.setGLViewMatrix(complex);
                int vaoId = complex.getVaoId();
                if (vaoId <= 0) {
                    logger.error("invalid VAO ID for complex with "
                            + complex.getMeshes().size() + " meshes");
                }
                int vboiId = complex.getVboIndicesId();
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glViewport(0, 0, this.getWidth(), this.getHeight());
                GL11.glClearColor(1f, 1f, 1f, 1f);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK,
                        this.app.wireframe ? GL11.GL_LINE : GL11.GL_FILL);
                // Bind to the VAO that has all the information about the
                // vertices
                GL30.glBindVertexArray(vaoId);

                // Bind to the index VBO that has all the information about the
                // order of the vertices
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);

                //
                // GLTools.displayBuffer(complex.getFlippedVerticesBuffer());
                // GLTools.displayBuffer(complex.getFlippedIndicesBuffer());
                // Draw the vertices
                // System.err.println("drawing " + complex.getMeshes().size()
                // + " meshes");

                // for (int n = 4; n < complex.getVertices().size() && n < 11;
                // n++) {
                // GLBezierShadingVertex vertex = (GLBezierShadingVertex)
                // complex
                // .getVertices().get(n);
                // try {
                // AffineTransform modelToViewTransform = new AffineTransform(
                // this.transform);
                // modelToViewTransform.invert();
                // double minX = complex.getMinX();
                // double minY = complex.getMinY();
                // GLProgram program = this.glContext.getCurrentProgram();
                // float m00 = (float) (modelToViewTransform.getScaleX());
                // float m02 = (float) (modelToViewTransform
                // .getTranslateX() + minX
                // * modelToViewTransform.getScaleX());
                // float m11 = (float) (modelToViewTransform.getScaleY());
                // float m12 = (float) (modelToViewTransform
                // .getTranslateY() + minY
                // * modelToViewTransform.getScaleY());
                // int screenWidth = this.getWidth();
                // int screenHeight = this.getHeight();
                // Point2d p0t = new Point2d(
                // (vertex.getP0()[0] * m00 + m02)
                // / (screenWidth + 1), 1
                // - (vertex.getP0()[1] * m11 + m12)
                // / (screenHeight + 1));
                // Point2d p1t = new Point2d(
                // (vertex.getP1()[0] * m00 + m02)
                // / (screenWidth + 1), 1
                // - (vertex.getP1()[1] * m11 + m12)
                // / (screenHeight + 1));
                // Point2d p2t = new Point2d(
                // (vertex.getP2()[0] * m00 + m02)
                // / (screenWidth + 1), 1
                // - (vertex.getP2()[1] * m11 + m12)
                // / (screenHeight + 1));
                //
                // System.err.println(n + " " + vertex.getXY()[0] + "x"
                // + vertex.getXY()[1]);
                // System.err.println("\tp0 = " + vertex.getP0()[0] + "x"
                // + vertex.getP0()[1] + " => " + p0t);
                // System.err.println("\tp1 = " + vertex.getP1()[0] + "x"
                // + vertex.getP1()[1] + " => " + p1t);
                // System.err.println("\tp2 = " + vertex.getP2()[0] + "x"
                // + vertex.getP2()[1] + " => " + p2t);
                // } catch (NoninvertibleTransformException e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // }
                // }
                for (GLMesh mesh : complex.getMeshes()) {
                    GL11.glDrawElements(mesh.getGlType(), mesh.getLastIndex()
                            - mesh.getFirstIndex() + 1, GL11.GL_UNSIGNED_INT,
                            mesh.getFirstIndex() * (Integer.SIZE / 8));
                }
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
                //
                // System.err.println("Rendering -----------------------------------");
                // // AffineTransform modelToViewTransform = new
                // AffineTransform(
                // // this.transform);
                // // try {
                // // modelToViewTransform.invert();
                // // } catch (NoninvertibleTransformException e) {
                // // // TODO Auto-generated catch block
                // // e.printStackTrace();
                // // }
                // // double minX = complex.getMinX();
                // // double minY = complex.getMinY();
                // //
                // // float m00 = (float) (modelToViewTransform.getScaleX());
                // // float m02 = (float) (modelToViewTransform.getTranslateX()
                // +
                // minX
                // // * modelToViewTransform.getScaleX());
                // // float m11 = (float) (modelToViewTransform.getScaleY());
                // // float m12 = (float) (modelToViewTransform.getTranslateY()
                // +
                // minY
                // // * modelToViewTransform.getScaleY());
                // // float screenWidth = this.getWidth();
                // // float screenHeight = this.getHeight();
                // // System.err.println("min = " + minX + " " + minY);
                // // System.err.println("scale = " + m00 + " " + m11);
                // // System.err.println("translate = " + m02 + " " + m12);
                // // System.err.println("screen = " + screenWidth + " " +
                // // screenHeight);
                // // for (GLVertex vertex : complex.getVertices()) {
                // // // GLVertex vertex = complex.getVertices().get(0);
                // // // {
                // // System.err.println("\tvertex = " + vertex);
                // // // float px = complex.getFlippedVerticesBuffer().get(0);
                // // // float py = complex.getFlippedVerticesBuffer().get(1);
                // // // System.err.println("\tvertex = " + px + " x " + py);
                // // // float x = -1 + 2 * (px * m00 + m02) / (screenWidth +
                // 1);
                // // // float y = 1 - 2 * (py * m11 + m12) / (screenHeight +
                // 1);
                // // // System.err.println("\tpixel = " + x + "x" + y);
                // // }
                // // for (GLMesh m : complex.getMeshes()) {
                // // System.err.println("\tmesh = " + m);
                // // }
                // // Put everything back to default (deselect)
            } catch (GLException e1) {
                e1.printStackTrace();
            }
            try {
                this.swapBuffers();

            } catch (LWJGLException e) {
                e.printStackTrace();
            }
        } finally {
            this.renderingInProgress = false;
        }
    }

    /**
     * Convert world coordinates to (0 x 0) x (width x height)
     */
    public Point2D worldToScreen(Point2D pWorld) {
        Point2D.Double pScreen = new Point2D.Double();
        try {
            this.transform.inverseTransform(pWorld, pScreen);
        } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
        }

        return pScreen;
    }

    /**
     * Convert (0 x 0) x (width x height) to world coordinates
     */
    public Point2D screenToWorld(Point2D pScreen) {
        Point2D.Double pWorld = new Point2D.Double();
        this.transform.transform(pScreen, pWorld);
        return pWorld;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        final double precision = 0.1;
        double scale = -e.getWheelRotation();
        if (scale < 0) {
            scale = 1 - scale * precision;
        } else {
            scale = 1. / (1 + scale * precision);
        }
        Point2D screenPosition = new Point2D.Double(e.getX(), e.getY());
        this.transform.translate(screenPosition.getX(), screenPosition.getY());
        this.transform.scale(scale, scale);
        this.transform
                .translate(-screenPosition.getX(), -screenPosition.getY());
        this.repaint();

    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.drag = true;
        if (e.getButton() == MouseEvent.BUTTON2) {
            this.setViewport(this.app.getEnvelope());
            this.repaint();
        } else {
            this.clickX = e.getX();
            this.clickY = e.getY();
            this.pressedTransform = new AffineTransform(this.transform);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.clickX = -1;
        this.clickY = -1;
        this.drag = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.clickX != -1 && this.clickY != -1) {
            this.drag = true;
            this.transform = new AffineTransform(this.pressedTransform);
            this.transform.translate(-(e.getX() - this.clickX),
                    -(e.getY() - this.clickY));
            this.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Point2D screenPosition = new Point2D.Double(e.getX()
        // / (double) this.getWidth(), e.getY()
        // / (double) this.getHeight());
        // Point2D worldPosition = this.screenToWorld(screenPosition);
        // System.err.println("mouse = " + e.getX() + "x" + e.getY()
        // + " (pixel) unity = " + screenPosition.getX() + "x"
        // + screenPosition.getY() + " => " + worldPosition.getX() + "x"
        // + worldPosition.getY() + " (world)");
        // screenPosition = this.worldToScreen(worldPosition);
        // System.err.println("back to screen = " + e.getX() + "x" + e.getY()
        // + " (mouse) =? " + screenPosition.getX() + "x"
        // + screenPosition.getY() + " (recomputed)");

    }

    public void refresh() {
        this.repaint();
    }

    public static final String m00ModelToViewMatrixUniformVarName = "m00";
    public static final String m02ModelToViewMatrixUniformVarName = "m02";
    public static final String m11ModelToViewMatrixUniformVarName = "m11";
    public static final String m12ModelToViewMatrixUniformVarName = "m12";
    public static final String screenWidthUniformVarName = "screenWidth";
    public static final String screenHeightUniformVarName = "screenHeight";
    public static final String bezierProgramName = "Bezier";
    private static final String bezierFragmentShaderFilename = "/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/test/app/bezier.frag.glsl";
    private static final String bezierVertexShaderFilename = "/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/test/app/bezier.vert.glsl";

    public static GLContext getGL4Context() throws GLException {
        GLContext glContext = new GLContext();

        glContext.addProgram(bezierProgramName, new GLProgramAccessor() {

            @Override
            public GLProgram getGLProgram() throws GLException {
                String bezierVertexShader;
                try {
                    bezierVertexShader = GLTools
                            .readFileAsString(bezierVertexShaderFilename);
                    final String bezierFragmentShader = GLTools
                            .readFileAsString(bezierFragmentShaderFilename);
                    return createPaintProgram(bezierProgramName,
                            bezierVertexShader, bezierFragmentShader);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }
            }
        });
        return glContext;
    }

    /**
     * @throws GLException
     */
    private static GLProgram createPaintProgram(String paintProgramName,
            String basicVertexShader, String basicFragmentShader)
            throws GLException {
        // basic program
        GLProgram paintProgram = new GLProgram(paintProgramName);
        paintProgram.addVertexShader(basicVertexShader);
        paintProgram.addFragmentShader(basicFragmentShader);

        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexPositionVariableName,
                GLBezierShadingVertex.vertexPositionLocation);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexUsVariableName,
                GLBezierShadingVertex.vertexUsLocation);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexColorVariableName,
                GLBezierShadingVertex.vertexColorLocation);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexLineWidthVariableName,
                GLBezierShadingVertex.vertexLineWidthLocation);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexMaxUVariableName,
                GLBezierShadingVertex.vertexMaxULocation);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexP0VariableName,
                GLBezierShadingVertex.vertexP0Location);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexP1VariableName,
                GLBezierShadingVertex.vertexP1Location);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexP2VariableName,
                GLBezierShadingVertex.vertexP2Location);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexN0VariableName,
                GLBezierShadingVertex.vertexN0Location);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexN2VariableName,
                GLBezierShadingVertex.vertexN2Location);
        paintProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m02ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m11ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m12ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(screenWidthUniformVarName);
        paintProgram.addUniform(screenHeightUniformVarName);

        return paintProgram;
    }
}
