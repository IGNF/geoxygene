package test.app;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.appli.render.primitive.SolidColorizer;
import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.function.FunctionEvaluationException;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLContext;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLTools;

/**
 * @author JeT
 * 
 */
public class LinePaintingGLCanvas extends AWTGLCanvas implements MouseListener,
        MouseMotionListener, MouseWheelListener {

    private static final long serialVersionUID = 1695930822908723425L;

    private static final Logger logger = Logger
            .getLogger(LinePaintingGLCanvas.class.getName()); // logger

    private GLContext glContext = null;
    private GLComplex complex = null;
    private AffineTransform pressedTransform = null;
    private AffineTransform transform = new AffineTransform();
    private IEnvelope envelope = null;
    private int clickX = -1, clickY = -1;
    private boolean drag = false;
    private LinePaintingApplication app = null;

    private GLTexture paperTexture = null;
    private GLTexture brushTexture = null;

    /**
     * @throws LWJGLException
     */
    public LinePaintingGLCanvas(LinePaintingApplication app)
            throws LWJGLException {
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
            this.glContext = LinePaintingApplication.getGL4Context();
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
            this.glContext = LinePaintingApplication.getGL4Context();
        } catch (GLException e) {
            e.printStackTrace();
            System.exit(0);
        }
        this.setViewport(this.app.getEnvelope());

        // set textures
        this.invalidateBrushTexture();
        this.invalidatePaperTexture();
    }

    /**
     * 
     */
    public void invalidatePaperTexture() {
        this.paperTexture = null;
    }

    /**
     * 
     */
    public void invalidateBrushTexture() {
        this.brushTexture = null;
    }

    public void updateViewport() {
        this.setViewport(null);
    }

    public void setViewport(IEnvelope envelope) {
        if (envelope == null) {
            return;
        }

        this.envelope = new GM_Envelope(envelope.getLowerCorner(),
                envelope.getUpperCorner());
        this.transform = new AffineTransform();
        this.transform.translate(this.envelope.getLowerCorner().getX(),
                this.envelope.getLowerCorner().getY());
        if (this.envelope.width() * this.envelope.length() > 0) {
            double scale = Math.min(this.envelope.width() / this.getWidth(),
                    this.envelope.length() / this.getHeight());
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
        program.setUniform1f(
                LinePaintingApplication.m00ModelToViewMatrixUniformVarName,
                (float) (modelToViewTransform.getScaleX()));
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(
                LinePaintingApplication.m02ModelToViewMatrixUniformVarName,
                (float) (modelToViewTransform.getTranslateX() + minX
                        * modelToViewTransform.getScaleX()));
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(
                LinePaintingApplication.m11ModelToViewMatrixUniformVarName,
                (float) (modelToViewTransform.getScaleY()));
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(
                LinePaintingApplication.m12ModelToViewMatrixUniformVarName,
                (float) (modelToViewTransform.getTranslateY() + minY
                        * modelToViewTransform.getScaleY()));
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        float width = this.getWidth();
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        float height = this.getHeight();
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(LinePaintingApplication.screenWidthUniformVarName,
                width);
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(
                LinePaintingApplication.screenHeightUniformVarName, height);

        // System.err.println("transform = " + this.transform + " screen = "
        // + width + "x" + height);

        // System.err.println("translation x = "
        // + (float) (modelToViewTransform.getTranslateX()) + " y = "
        // + (modelToViewTransform.getTranslateY()));
        // System.err.println("scaling     x = "
        // + (float) (modelToViewTransform.getScaleX()) + " y = "
        // + (modelToViewTransform.getScaleY()));
        // System.err.println("canvas width = " + this.getWidth() + " height = "
        // + this.getHeight());
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        return true;
    }

    /**
     * @param widthFunction
     * @param shiftFunction
     * @return
     */
    public static GLComplex createLineComplex(String id,
            List<ILineString> lines, double minX, double minY) {

        GLPaintingComplex complex = new GLPaintingComplex(id, minX, minY);
        Random rnd = new Random(0);
        for (ILineString line : lines) {
            Color c = new Color(rnd.nextFloat(), rnd.nextFloat(),
                    rnd.nextFloat(), 1f);

            try {
                LinePaintingTesselator.tesselateThickLine(complex,
                        line.getControlPoint(),
                        LinePaintingApplication.DefaultLineWidthFunction,
                        LinePaintingApplication.DefaultLineShiftFunction,
                        Double.POSITIVE_INFINITY, 0, minX, minY,
                        new SolidColorizer(c));
            } catch (FunctionEvaluationException e) {
                e.printStackTrace();
            }
        }

        return complex;
    }

    /**
     * @param widthFunction
     * @param shiftFunction
     * @return
     */
    public static GLComplex createComplex(String id, List<ILineString> lines,
            double minX, double minY, Function1D widthFunction,
            Function1D shiftFunction, double sampleSize, double minAngle) {
        GLPaintingComplex complex = new GLPaintingComplex(id, minX, minY);
        for (ILineString line : lines) {
            try {
                LinePaintingTesselator.tesselateThickLine(complex, line
                        .getControlPoint(), widthFunction, shiftFunction,
                        sampleSize, minAngle, minX, minY, new SolidColorizer(
                                Color.black));
                // LinePaintingTesselator
                // .tesselateThickLine(complex, line.getControlPoint(),
                // widthFunction, shiftFunction, sampleSize,
                // minAngle, minX, minY, new RandomColorizer());
            } catch (FunctionEvaluationException e) {
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
        GLTools.glCheckError("start painting");
        try {
            this.makeCurrent();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
        if (this.brushTexture == null) {
            this.brushTexture = new GLTexture(this.app.brushTextureFilename);
            this.brushTexture.setMipmap(false);

        }
        if (this.paperTexture == null) {
            this.paperTexture = new GLTexture(this.app.paperTextureFilename);
            this.paperTexture.setMipmap(false);
        }
        try {
            GLProgram program = this.glContext
                    .setCurrentProgram(LinePaintingApplication.paintProgramName);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GLTools.glCheckError("enable paperTexture");
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GLTools.glCheckError("active paperTexture");
            GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                    this.paperTexture.getTextureId());
            GLTools.glCheckError("bind paperTexture");
            program.setUniform1i(
                    LinePaintingApplication.paperTextureUniformVarName, 0);

            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GLTools.glCheckError("active brushTexture");
            GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                    this.brushTexture.getTextureId());
            // GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
            // GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            // GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
            // GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GLTools.glCheckError("bind brushTexture");
            program.setUniform1i(
                    LinePaintingApplication.brushTextureUniformVarName, 1);

            GLTools.glCheckError("setUniform paperTexture");

            program.setUniform1f(
                    LinePaintingApplication.mapScaleDiv1000UniformVarName,
                    (float) (Math.abs(1. / this.transform.getScaleX())));

            program.setUniform1i(
                    LinePaintingApplication.brushWidthUniformVarName,
                    this.brushTexture.getTextureWidth());
            program.setUniform1i(
                    LinePaintingApplication.brushHeightUniformVarName,
                    this.brushTexture.getTextureHeight());
            program.setUniform1i(
                    LinePaintingApplication.brushStartWidthUniformVarName,
                    this.app.brushStartLength);
            program.setUniform1i(
                    LinePaintingApplication.brushEndWidthUniformVarName,
                    this.app.brushEndLength);
            program.setUniform1f(
                    LinePaintingApplication.brushScaleUniformVarName,
                    (float) (this.app.brushSize / this.brushTexture
                            .getTextureHeight()));
            program.setUniform1f(
                    LinePaintingApplication.paperScaleUniformVarName,
                    (float) (this.app.paperScaleFactor));
            program.setUniform1f(
                    LinePaintingApplication.paperDensityUniformVarName,
                    (float) (this.app.paperDensity));
            program.setUniform1f(
                    LinePaintingApplication.brushDensityUniformVarName,
                    (float) (this.app.brushDensity));
            program.setUniform1f(
                    LinePaintingApplication.strokePressureUniformVarName,
                    (float) (this.app.strokePressure));
            program.setUniform1f(
                    LinePaintingApplication.sharpnessUniformVarName,
                    (float) (this.app.sharpness));
            program.setUniform1f(
                    LinePaintingApplication.strokePressureVariationAmplitudeUniformVarName,
                    (float) (this.app.strokePressureVariationAmplitude));
            program.setUniform1f(
                    LinePaintingApplication.strokePressureVariationFrequencyUniformVarName,
                    (float) (this.app.strokePressureVariationFrequency));
            program.setUniform1f(
                    LinePaintingApplication.strokeShiftVariationAmplitudeUniformVarName,
                    (float) (this.app.strokeShiftVariationAmplitude));
            program.setUniform1f(
                    LinePaintingApplication.strokeShiftVariationFrequencyUniformVarName,
                    (float) (this.app.strokeShiftVariationFrequency));
            program.setUniform1f(
                    LinePaintingApplication.strokeThicknessVariationAmplitudeUniformVarName,
                    (float) (this.app.strokeThicknessVariationAmplitude));
            program.setUniform1f(
                    LinePaintingApplication.strokeThicknessVariationFrequencyUniformVarName,
                    (float) (this.app.strokeThicknessVariationFrequency));
            // FIXME set textures
        } catch (GLException e1) {
            e1.printStackTrace();
        }
        this.complex = createComplex("paint #" + (new Date().getTime()),
                this.app.getLines(), this.app.getEnvelope().minX(), this.app
                        .getEnvelope().minY(), this.app.getLineWidthFunction(),
                this.app.getLineShiftFunction(), this.app.sampleSize, Math.PI
                        * this.app.minAngle / 180.);
        try {
            this.setGLViewMatrix(this.complex);
            int vaoId = this.complex.getVaoId();
            if (vaoId <= 0) {
                logger.error("invalid VAO ID for complex with "
                        + this.complex.getMeshes().size() + " meshes");
            }
            int vboiId = this.complex.getVboIndicesId();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glViewport(0, 0, this.getWidth(), this.getHeight());
            GL11.glClearColor(1f, 1f, 1f, 1f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK,
                    this.app.wireframe ? GL11.GL_LINE : GL11.GL_FILL);
            // Bind to the VAO that has all the information about the vertices
            GL30.glBindVertexArray(vaoId);

            // Bind to the index VBO that has all the information about the
            // order of the vertices
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);

            //
            // GLTools.displayBuffer(this.complex.getFlippedVerticesBuffer());
            // GLTools.displayBuffer(this.complex.getFlippedIndicesBuffer());
            // Draw the vertices
            for (GLMesh mesh : this.complex.getMeshes()) {
                GL11.glDrawElements(mesh.getGlType(), mesh.getLastIndex()
                        - mesh.getFirstIndex() + 1, GL11.GL_UNSIGNED_INT,
                        mesh.getFirstIndex() * (Integer.SIZE / 8));
            }
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
            //
            // System.err.println("Rendering -----------------------------------");
            // // AffineTransform modelToViewTransform = new AffineTransform(
            // // this.transform);
            // // try {
            // // modelToViewTransform.invert();
            // // } catch (NoninvertibleTransformException e) {
            // // // TODO Auto-generated catch block
            // // e.printStackTrace();
            // // }
            // // double minX = this.complex.getMinX();
            // // double minY = this.complex.getMinY();
            // //
            // // float m00 = (float) (modelToViewTransform.getScaleX());
            // // float m02 = (float) (modelToViewTransform.getTranslateX() +
            // minX
            // // * modelToViewTransform.getScaleX());
            // // float m11 = (float) (modelToViewTransform.getScaleY());
            // // float m12 = (float) (modelToViewTransform.getTranslateY() +
            // minY
            // // * modelToViewTransform.getScaleY());
            // // float screenWidth = this.getWidth();
            // // float screenHeight = this.getHeight();
            // // System.err.println("min = " + minX + " " + minY);
            // // System.err.println("scale = " + m00 + " " + m11);
            // // System.err.println("translate = " + m02 + " " + m12);
            // // System.err.println("screen = " + screenWidth + " " +
            // // screenHeight);
            // // for (GLVertex vertex : this.complex.getVertices()) {
            // // // GLVertex vertex = this.complex.getVertices().get(0);
            // // // {
            // // System.err.println("\tvertex = " + vertex);
            // // // float px = this.complex.getFlippedVerticesBuffer().get(0);
            // // // float py = this.complex.getFlippedVerticesBuffer().get(1);
            // // // System.err.println("\tvertex = " + px + " x " + py);
            // // // float x = -1 + 2 * (px * m00 + m02) / (screenWidth + 1);
            // // // float y = 1 - 2 * (py * m11 + m12) / (screenHeight + 1);
            // // // System.err.println("\tpixel = " + x + "x" + y);
            // // }
            // // for (GLMesh m : this.complex.getMeshes()) {
            // // System.err.println("\tmesh = " + m);
            // // }
            // // Put everything back to default (deselect)
        } catch (GLException e1) {
            e1.printStackTrace();
        }
        try {
            if (this.app.drawLines) {
                this.glContext
                        .setCurrentProgram(LinePaintingApplication.basicProgramName);

                GLComplex lineComplex = createLineComplex("lines #"
                        + (new Date().getTime()), this.app.getLines(), this.app
                        .getEnvelope().minX(), this.app.getEnvelope().minY());
                this.setGLViewMatrix(lineComplex);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                // Bind to the VAO that has all the information about the
                // vertices
                GL30.glBindVertexArray(lineComplex.getVaoId());

                // Bind to the index VBO that has all the information about the
                // order of the vertices
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER,
                        lineComplex.getVboIndicesId());

                // Draw the vertices
                for (GLMesh mesh : lineComplex.getMeshes()) {
                    GL11.glDrawElements(mesh.getGlType(), mesh.getLastIndex()
                            - mesh.getFirstIndex() + 1, GL11.GL_UNSIGNED_INT,
                            mesh.getFirstIndex() * (Integer.SIZE / 8));
                }
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

            }
        } catch (GLException e1) {
            e1.printStackTrace();
        }
        try {
            this.swapBuffers();

        } catch (LWJGLException e) {
            e.printStackTrace();
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
            this.setViewport(this.envelope);
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
}
