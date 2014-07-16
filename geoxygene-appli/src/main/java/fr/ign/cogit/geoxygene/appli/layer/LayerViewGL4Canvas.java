/**
 * 
 */
package fr.ign.cogit.geoxygene.appli.layer;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.primitive.GL4FeatureRenderer;
import fr.ign.cogit.geoxygene.style.BackgroundDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleComplex;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleVertex;
import fr.ign.cogit.geoxygene.util.gl.GLTexture;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.RenderingStatistics;

/** @author JeT GL drawable canvas inserted into a LayerViewLwjglPanel */
public class LayerViewGL4Canvas extends LayerViewGLCanvas implements
        ComponentListener {

    private static final long serialVersionUID = 2813681374260169340L; // serializable
    private Thread glCanvasThreadOwner = null; // stores the thread that ows gl
                                               // context to check consistency
    private GLSimpleComplex screenQuad = null;
    private GLSimpleVertex screenQuadNW = null;
    private GLSimpleVertex screenQuadNE = null;
    private GLSimpleVertex screenQuadSW = null;
    private GLSimpleVertex screenQuadSE = null;
    private GLTexture backgroundTexture = null;
    // these values should be read from SLD
    private final double paperHeight = 4; // paper height in cm
    private final double paperMapScale = 100000; // scale paper height

    /**
     * Constructor
     * 
     * @param parentPanel
     * @param sld2
     * @throws LWJGLException
     */
    public LayerViewGL4Canvas(final LayerViewGLPanel parentPanel)
            throws LWJGLException {
        super(parentPanel);
        this.initializeScreenQuad();
        // multisampling antialiasing doesn't work on my computer (freeze
        // application without message error)
        // PixelFormat pixelFormat = new PixelFormat().withSamples(4);
        // this.setPixelFormat(pixelFormat);
    }

    @Override
    protected void initGL() {
        super.initGL();
        glViewport(0, 0, this.getWidth(), this.getHeight());
        // glEnable(GL13.GL_MULTISAMPLE);
    }

    /**
     * @param sld
     *            the sld background to set
     */
    @Override
    public void setViewBackground(BackgroundDescriptor background) {
        super.setViewBackground(background);
        // this.backgroundTexture = null;
    }

    /**
     * 
     */
    private void initializeScreenQuad() {
        this.screenQuad = new GLSimpleComplex("screen", 0f, 0f);
        GLMesh mesh = this.screenQuad.addGLMesh(GL11.GL_QUADS);
        mesh.addIndex(this.screenQuad
                .addVertex(this.screenQuadSW = new GLSimpleVertex(
                        new Point2D.Double(-1, -1), new Point2D.Double(0, 0))));
        mesh.addIndex(this.screenQuad
                .addVertex(this.screenQuadNW = new GLSimpleVertex(
                        new Point2D.Double(-1, 1), new Point2D.Double(0, 1))));
        mesh.addIndex(this.screenQuad
                .addVertex(this.screenQuadNE = new GLSimpleVertex(
                        new Point2D.Double(1, 1), new Point2D.Double(1, 1))));
        mesh.addIndex(this.screenQuad
                .addVertex(this.screenQuadSE = new GLSimpleVertex(
                        new Point2D.Double(1, -1), new Point2D.Double(1, 0))));
        this.screenQuad.setColor(Color.blue);
        this.screenQuad.setOverallOpacity(0.5);
    }

    /**
     * @return the screenQuad
     */
    public GLSimpleComplex getScreenQuad() {
        if (this.screenQuad == null) {
            this.initializeScreenQuad();
        }
        return this.screenQuad;
    }

    @Override
    protected void paintGL() {
        super.paintGL();
        if (!this.isDisplayable() || this.getContext() == null) {
            return;
        }

        try {
            if (this.glCanvasThreadOwner == null) {
                this.glCanvasThreadOwner = Thread.currentThread();
                logger.info("Thread " + this.glCanvasThreadOwner.getName()
                        + " acquire gl context");
            } else {
                if (this.glCanvasThreadOwner != Thread.currentThread()) {
                    logger.error("LayerViewGLCanvas::paintGL() was not called by the GL Context owner thread !   EXITING !");
                    logger.error("Owner : "
                            + this.glCanvasThreadOwner.getName());
                    logger.error("Current thread : "
                            + Thread.currentThread().getName());
                    return;
                }
            }
            if (!this.isCurrent()) {
                this.makeCurrent();
            }

        } catch (LWJGLException exception) {
            // if makeCurrent() throws an exception, then the canvas is not
            // ready
            return;
        }

        try {
            RenderingStatistics.startRendering();

            // System.err.println("-------------------------------------------------- paint GL --------------------------------");
            // RenderGLUtil.glDraw(null);
            Color bgColor = this.getBackground();
            if (this.getViewBackground() != null
                    && this.getViewBackground().getColor() != null) {
                bgColor = this.getViewBackground().getColor();
            }
            GLTools.glClear(bgColor, GL_COLOR_BUFFER_BIT);
            GLTools.glClear(0f, 0f, 0f, 1f, GL_DEPTH_BUFFER_BIT);

            this.drawBackground();

            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_BLEND);
            // this.parentPanel.repaint();
            if (this.getParentPanel() != null
                    && this.getParentPanel().getRenderingManager() != null) {
                this.getParentPanel().getRenderingManager().renderAll();
            }

            // System.err.println("-------------------------------------------------- swap buffers --------------------------------");

            if (this.doPaintOverlay()) {
                this.glPaintOverlays();
            }

            this.swapBuffers();
            RenderingStatistics.endRendering();
            RenderingStatistics.printStatistics();
        } catch (LWJGLException e) {
            logger.error("Error rendering the LwJGL : " + e.getMessage());
            // e.printStackTrace();
        }
    }

    private void drawBackground() {
        // if (this.getViewBackground() == null
        // || this.getBackgroundTexture() == null) {
        // return;
        // }
        try {
            GLProgram program = LwjglLayerRenderer
                    .getGL4Context()
                    .setCurrentProgram(LwjglLayerRenderer.backgroundProgramName);
            if (program == null) {
                return;
            }
            glEnable(GL_TEXTURE_2D);
            glDisable(GL11.GL_POLYGON_SMOOTH);

            Viewport viewport = this.getParentPanel().getViewport();
            double scale = viewport.getScale();
            // double mapScale = 1. / (Viewport.getMETERS_PER_PIXEL() * scale);
            // double paperScale = this.paperHeight / 2.540005 * (this.printDPI)
            // / this.getHeight();
            // paper scale factor used to convert from map length to world
            // length
            double psf = this.paperHeight * this.paperMapScale
                    / (100. * this.getBackgroundTexture().getTextureHeight());
            double paperWidthInWorldCoordinates = this.getBackgroundTexture()
                    .getTextureWidth() * psf;
            double paperHeightInWorldCoordinates = this.getBackgroundTexture()
                    .getTextureHeight() * psf;
            double screenWidthInWorldCoordinates = this.getWidth() / scale;
            double screenHeightInWorldCoordinates = this.getHeight() / scale;
            double u0 = (viewport.getViewOrigin().getX() % paperWidthInWorldCoordinates)
                    / paperWidthInWorldCoordinates;
            double v0 = (viewport.getViewOrigin().getY() % paperHeightInWorldCoordinates)
                    / paperHeightInWorldCoordinates;
            double deltaU = screenWidthInWorldCoordinates
                    / paperWidthInWorldCoordinates;
            double deltaV = screenHeightInWorldCoordinates
                    / paperHeightInWorldCoordinates;
            this.screenQuadNW.setUV((float) u0, (float) (v0 + deltaV));
            this.screenQuadNE.setUV((float) (u0 + deltaU),
                    (float) (v0 + deltaV));
            this.screenQuadSE.setUV((float) (u0 + deltaU), (float) v0);
            this.screenQuadSW.setUV((float) u0, (float) v0);
            this.getScreenQuad().invalidateBuffers();
            program.setUniform1i(
                    LwjglLayerRenderer.colorTexture1UniformVarName,
                    GL4FeatureRenderer.COLORTEXTURE1_SLOT);
            GL13.glActiveTexture(GL13.GL_TEXTURE0
                    + GL4FeatureRenderer.COLORTEXTURE1_SLOT);
            glBindTexture(GL_TEXTURE_2D, this.getBackgroundTexture()
                    .getTextureId());
            GL11.glDepthMask(false);
            glDisable(GL11.GL_DEPTH_TEST);

            GL30.glBindVertexArray(this.getScreenQuad().getVaoId());
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            //
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

            GLTools.glCheckError("before background drawing textured quad");
            for (GLMesh mesh : this.getScreenQuad().getMeshes()) {
                GL11.glDrawElements(mesh.getGlType(), mesh.getLastIndex()
                        - mesh.getFirstIndex() + 1, GL11.GL_UNSIGNED_INT,
                        mesh.getFirstIndex() * (Integer.SIZE / 8));
            }
            this.getScreenQuad().setColor(new Color(1f, 0f, 1f, 1f));
            GLTools.glCheckError("background textured quad");
            glBindTexture(GL_TEXTURE_2D, 0); // unbind texture
            GL30.glBindVertexArray(0); // unbind VAO
            GLTools.glCheckError("exiting background rendering");
        } catch (GLException e) {
            logger.error("An error ocurred drawing background : "
                    + e.getMessage());
            e.printStackTrace();
        }

    }

    private GLTexture getBackgroundTexture() {
        if (this.backgroundTexture == null) {
            this.backgroundTexture = new GLTexture(
                    "./src/main/resources/textures/papers/canvas-bg.png");
        }
        return this.backgroundTexture;

        // if (this.getViewBackground() == null
        // || this.getViewBackground().getTextureImage() == null) {
        // return null;
        // }
        // if (this.backgroundTexture == null
        // || this.backgroundTexture.getTextureImage() != this
        // .getViewBackground().getTextureImage()) {
        // this.backgroundTexture = new GLTexture(this.getViewBackground()
        // .getTextureImage());
        // }
        // return this.backgroundTexture;
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        super.componentResized(e);
        if (this.getParentPanel() == null) {
            return;
        }
        if (this.getSize().equals(this.getParentPanel().getSize())) {
            return;
        }
        // System.err.println("component resize to " + this.getSize() +
        // " in GLCanvas");
        try {
            this.makeCurrent();
        } catch (LWJGLException exception) {
            // if makeCurrent() throws an exception, then the canvas is not
            // ready
            return;
        }
        try {
            if (this.getContext() == null) {
                return;
            }
            this.setSize(this.getParentPanel().getSize());
            // glMatrixMode(GL_PROJECTION);
            // glLoadIdentity();
            // glOrtho(0, this.getWidth(), this.getHeight(), 0, -1000, 1000);
            // glMatrixMode(GL_MODELVIEW);
            System.err.println("resize window to " + this.getWidth() + "x"
                    + this.getHeight());
            glViewport(0, 0, this.getWidth(), this.getHeight());
        } catch (Exception e1) {
            // don't know hot to prevent/check this exception.
            // isDisplayable() and isValid() are both true at this point...
            logger.warn("Error resizing the heavyweight AWTGLCanvas : "
                    + e1.getMessage());
            // e1.printStackTrace();
        }
        this.repaint(); // super.componentResized(e);
    }

    /**
     * paint overlays in GL windows
     */
    public void glPaintOverlays() {
    }

}
