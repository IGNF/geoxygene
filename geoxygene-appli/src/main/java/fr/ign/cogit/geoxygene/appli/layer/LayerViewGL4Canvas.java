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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLSimpleComplex;
import fr.ign.cogit.geoxygene.style.BackgroundDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLContext;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleVertex;
import fr.ign.cogit.geoxygene.util.gl.GLTexture;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.RenderingStatistics;

/** @author JeT GL drawable canvas inserted into a LayerViewLwjglPanel */
public class LayerViewGL4Canvas extends LayerViewGLCanvas implements
        ComponentListener {

    private static final long serialVersionUID = 2813681374260169340L; // serializable
    private static final int COLORTEXTURE1_SLOT = 1;
    private static final int COLORTEXTURE2_SLOT = 2;
    private Thread glCanvasThreadOwner = null; // stores the thread that ows gl
                                               // context to check consistency
    private GLSimpleComplex screenQuad = null;
    private GLSimpleVertex screenQuadNW = null;
    private GLSimpleVertex screenQuadNE = null;
    private GLSimpleVertex screenQuadSW = null;
    private GLSimpleVertex screenQuadSE = null;
    private GLTexture backgroundTexture = null;
    private BackgroundDescriptor storedBackground = null;
    private GLContext glContext = null;
    private BufferedImage bg = null;
    private int overlayTextureId = -1;

    // // these values should be read from SLD
    // private final double paperHeight = 4; // paper height in cm
    // private final double paperMapScale = 100000; // scale paper height

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

    private int getOverlayTextureId() {
        // System.err.println("get FBO texture ID : " + this.fboTextureId);
        if (this.overlayTextureId == -1) {
            this.overlayTextureId = GL11.glGenTextures();
            // System.err.println("generated FBO texture ID : "
            // + this.fboTextureId);
            if (this.overlayTextureId < 0) {
                logger.error("Unable to use Overlay texture");
            }
        }
        return this.overlayTextureId;
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
            // GL20.glUseProgram(0);
        } catch (Exception e) {
            logger.error("Error rendering the LwJGL : " + e.getMessage());
            // e.printStackTrace();
        }
    }

    @Override
    public void reset() {
        this.glContext = null;
    }

    private void drawBackground() {
        if (this.getViewBackground() == null
                || this.getBackgroundTexture() == null
                || this.getBackgroundTexture().getTextureFilename() == null) {
            return;
        }
        try {
            GLProgram program = this.getGlContext().setCurrentProgram(
                    LayerViewGLPanel.backgroundProgramName);
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
            double psf = this.getViewBackground().getPaperHeightInCm()
                    * this.getViewBackground().getPaperReferenceMapScale()
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
            program.setUniform1i(LayerViewGLPanel.colorTexture1UniformVarName,
                    LayerViewGL4Canvas.COLORTEXTURE1_SLOT);
            GL13.glActiveTexture(GL13.GL_TEXTURE0
                    + LayerViewGL4Canvas.COLORTEXTURE1_SLOT);
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

    /**
     * @return the glContext
     * @throws GLException
     */
    @Override
    public GLContext getGlContext() throws GLException {
        if (this.glContext == null) {
            this.glContext = this.getParentPanel().createNewGL4Context();
        }
        return this.glContext;
    }

    private GLTexture getBackgroundTexture() {
        if (this.getViewBackground() != this.storedBackground) {
            this.storedBackground = null;
            this.backgroundTexture = null;
        }
        if (this.backgroundTexture == null) {
            if (this.getViewBackground() != null) {
                this.backgroundTexture = new GLTexture(this.getViewBackground()
                        .getUrl());
                this.storedBackground = this.getViewBackground();
            }
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

    public void invalidateBackgroundTexture() {
        this.backgroundTexture = null;
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
     * 
     * @throws GLException
     */
    public void glPaintOverlays() throws GLException {
        if (this.bg == null || this.bg.getWidth() != this.getWidth()
                || this.bg.getHeight() != this.getHeight()) {
            this.bg = new BufferedImage(this.getWidth(), this.getHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR);
        }
        Graphics2D g = this.bg.createGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.setColor(Color.red);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        Composite fade = AlphaComposite
                .getInstance(AlphaComposite.SRC_OVER, 1f);
        g.setComposite(fade);
        this.parentPanel.paintOverlays(g);

        int[] pixels = new int[this.bg.getWidth() * this.bg.getHeight()];
        this.bg.getRGB(0, 0, this.bg.getWidth(), this.bg.getHeight(), pixels,
                0, this.bg.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(this.bg.getWidth()
                * this.bg.getHeight() * 4); // 4 for RGBA, 3 for RGB

        for (int y = this.bg.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < this.bg.getWidth(); x++) {
                int pixel = pixels[y * this.bg.getWidth() + x];
                buffer.put((byte) (pixel >> 16 & 0xFF)); // Red component
                buffer.put((byte) (pixel >> 8 & 0xFF)); // Green component
                buffer.put((byte) (pixel >> 0 & 0xFF)); // Blue component
                buffer.put((byte) (pixel >> 24 & 0xFF)); // Alpha component.
                // Only for RGBA
                // System.err.println("transparency = " + (pixel >> 24 & 0xFF));
            }
        }
        buffer.rewind();

        glEnable(GL_BLEND);
        glBindTexture(GL_TEXTURE_2D, this.getOverlayTextureId());

        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
                GL11.GL_REPEAT);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
                GL11.GL_REPEAT);

        // Setup texture scaling filtering
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                GL11.GL_LINEAR);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR);
        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.getWidth(),
                this.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
                buffer);

        GLProgram program = this.getGlContext().setCurrentProgram(
                LayerViewGLPanel.screenspaceAntialiasedTextureProgramName);

        GL11.glViewport(0, 0, this.getWidth(), this.getHeight());
        GL11.glDrawBuffer(GL11.GL_BACK);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL11.GL_POLYGON_SMOOTH);

        program.setUniform1i(LayerViewGLPanel.colorTexture1UniformVarName,
                COLORTEXTURE2_SLOT);
        GLTools.glCheckError("FBO bind antialiasing");
        program.setUniform1i(LayerViewGLPanel.antialiasingSizeUniformVarName, 1);
        GLTools.glCheckError("FBO activate texture");
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + COLORTEXTURE2_SLOT);
        GLTools.glCheckError("FBO bound texture");
        glBindTexture(GL_TEXTURE_2D, this.getOverlayTextureId());
        GLTools.glCheckError("FBO bound texture");

        GL11.glDepthMask(false);
        glDisable(GL11.GL_DEPTH_TEST);

        GL30.glBindVertexArray(this.getScreenQuad().getVaoId());

        program.setUniform1f(LayerViewGLPanel.objectOpacityUniformVarName, 1f);
        program.setUniform1f(LayerViewGLPanel.globalOpacityUniformVarName, 1f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR);
        //
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        GLTools.glCheckError("before FBO drawing textured quad");
        GLTools.drawComplex(this.getScreenQuad());
        // this.getScreenQuad().setColor(new Color(1f, 1f, 1f, .5f));
        GLTools.glCheckError("FBO drawing textured quad");

        GL30.glBindVertexArray(0); // unbind VAO
        GLTools.glCheckError("exiting FBO rendering");
        glBindTexture(GL_TEXTURE_2D, 0); // unbind texture

    }
}
