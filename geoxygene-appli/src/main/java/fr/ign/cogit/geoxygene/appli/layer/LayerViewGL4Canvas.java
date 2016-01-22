/**
 * 
 */
package fr.ign.cogit.geoxygene.appli.layer;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.joda.time.LocalTime;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.appli.GeoxygeneConstants;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLContext;
import fr.ign.cogit.geoxygene.appli.gl.GLSimpleComplex;
import fr.ign.cogit.geoxygene.appli.gl.program.GLProgramBuilder;
import fr.ign.cogit.geoxygene.appli.gl.program.ScreenspaceAntialiasedTextureGLProgramBuilder;
import fr.ign.cogit.geoxygene.appli.gl.program.WorldspaceColorGLProgramBuilder;
import fr.ign.cogit.geoxygene.appli.mode.RenderingTypeMode;
import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.appli.render.stats.RenderingStatistics;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
import fr.ign.cogit.geoxygene.style.BackgroundDescriptor;
import fr.ign.cogit.geoxygene.style.texture.SimpleTexture;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleVertex;
import fr.ign.cogit.geoxygene.util.gl.GLTools;

/** @author JeT GL drawable canvas inserted into a LayerViewLwjglPanel */
public class LayerViewGL4Canvas extends LayerViewGLCanvas implements ComponentListener {

    private static final long serialVersionUID = 2813681374260169340L; // serializable

    private static final String GL_ProgName_ScreenspaceAntialiasedTexture = "GLProgram-Antialiazing";

    private static final String GL_ProgName_WorldspaceColor = "GLProgram-WorldspaceColor";

    private Thread glCanvasThreadOwner = null; // stores the thread that ows gl
                                               // context to check consistency
    private GLSimpleComplex screenQuad = null;
    private GLSimpleVertex screenQuadNW = null;
    private GLSimpleVertex screenQuadNE = null;
    private GLSimpleVertex screenQuadSW = null;
    private GLSimpleVertex screenQuadSE = null;
    private BasicTexture backgroundTexture = null;
    private BackgroundDescriptor storedBackground = null;
    private BufferedImage bg = null;
    private int overlayTextureId = -1;
    private ByteBuffer buffer = null;
    private int[] pixels = null;

    private boolean bFBOactive = false;
    private int layerFboTextureId = -1;
    private int fboId = -1;
    private int fboPingPongId0 = -1;
    private int fboTextureId1 = -1;
    private boolean bFBOmod = true;
    private int fboWidth = -1;
    private int fboHeight = -1;
    private int currentFboPingPongIndex = 2; // alternate 1 & 2
    private boolean gl_context_intialized = false;

    private long time_counter = 0;

    /**
     * Constructor
     * 
     * @param parentPanel
     * @param sld2
     * @throws LWJGLException
     */
    public LayerViewGL4Canvas(final LayerViewGLPanel parentPanel) throws LWJGLException {
        super(parentPanel);
        this.initializeScreenQuad();
        this.currentFboPingPongIndex = 0;
        // multisampling antialiasing doesn't work on my computer (freeze
        // application without message error)
        // PixelFormat pixelFormat = new PixelFormat().withSamples(4);
        // this.setPixelFormat(pixelFormat);

    }

    @Override
    protected void initGL() {
        // System.out.println("initGL");
        super.initGL();
        glViewport(0, 0, this.getWidth(), this.getHeight());
        this.updateFBODimensions();
        updateScreenSizeSharedUniforms();
    }

    private int getOverlayTextureId() {
        if (this.overlayTextureId == -1) {
            this.overlayTextureId = GL11.glGenTextures();
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
        mesh.addIndex(this.screenQuad.addVertex(this.screenQuadSW = new GLSimpleVertex(new Point2D.Double(-1, -1), new Point2D.Double(0, 0))));
        mesh.addIndex(this.screenQuad.addVertex(this.screenQuadNW = new GLSimpleVertex(new Point2D.Double(-1, 1), new Point2D.Double(0, 1))));
        mesh.addIndex(this.screenQuad.addVertex(this.screenQuadNE = new GLSimpleVertex(new Point2D.Double(1, 1), new Point2D.Double(1, 1))));
        mesh.addIndex(this.screenQuad.addVertex(this.screenQuadSE = new GLSimpleVertex(new Point2D.Double(1, -1), new Point2D.Double(1, 0))));
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
                logger.info("Thread " + this.glCanvasThreadOwner.getName() + " acquire gl context");
            } else {
                if (this.glCanvasThreadOwner != Thread.currentThread()) {
                    logger.error("LayerViewGLCanvas::paintGL() was not called by the GL Context owner thread !   EXITING !");
                    logger.error("Owner : " + this.glCanvasThreadOwner.getName());
                    logger.error("Current thread : " + Thread.currentThread().getName());
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
            Color bgColor = this.getBackground();
            if (this.getViewBackground() != null && this.getViewBackground().getColor() != null) {
                bgColor = this.getViewBackground().getColor();
            }

            if (this.getParentPanel() == null || this.getParentPanel().getRenderingManager() == null) {
                logger.warn("canvas try to render a non initialized LayerViewGLPanel. Skip rendering.");
                return;
            }
            this.getGlContext().setSharedUniform(GeoxygeneConstants.GL_VarName_ScreenWidth, this.getWidth());
            this.getGlContext().setSharedUniform(GeoxygeneConstants.GL_VarName_ScreenHeight, this.getHeight());
            // No QuickRendering mode = with FBOs
            if (!this.isQuickRendering()) {
                glViewport(0, 0, this.fboWidth, this.fboHeight);
                this.initializeFBO();

                this.getGlContext().setSharedUniform(GeoxygeneConstants.GL_VarName_FboWidth, this.getFBOImageWidth());
                this.getGlContext().setSharedUniform(GeoxygeneConstants.GL_VarName_FboHeight, this.getFBOImageHeight());
                // The background is written in the first ping-pong textures of
                // the FBO.
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.getFboId());
                GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT1);
                GLTools.glClear(bgColor, GL11.GL_COLOR_BUFFER_BIT);
                this.drawBackground(this.getFBOImageWidth(), this.getFBOImageHeight());
                // Set back the window-system framebuffer.
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
            } else {
                // Just draw directly in the backbuffer.
                glViewport(0, 0, this.getWidth(), this.getHeight());
                // If FBOs are not used, we force the FBO size to be the same as
                // the panel.
                this.getGlContext().setSharedUniform(GeoxygeneConstants.GL_VarName_FboWidth, this.getWidth());
                this.getGlContext().setSharedUniform(GeoxygeneConstants.GL_VarName_FboHeight, this.getHeight());
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
                GL11.glDrawBuffer(GL11.GL_BACK);
                GLTools.glClear(bgColor, GL11.GL_COLOR_BUFFER_BIT);
                this.drawBackground(this.getWidth(), this.getHeight());
            }
            // this.getGlContext().setSharedUniform("time", this.time_counter++);
            // Real time
            // TODO: pb when changing day, but it's temporary ...
            this.getGlContext().setSharedUniform("time", LocalTime.now().getMillisOfDay());
            
            // RENDER EVERYTHING
            this.getParentPanel().getRenderingManager().renderAll();
            if (!this.isQuickRendering()) {
                // Apply the antialiazing and copy the FBO texture into the
                // backbuffer.
                this.drawFBOPingPongInBackBuffer();
                if (this.doPaintOverlay()) {
                    RenderingStatistics.startOverlayRendering();
                    this.glPaintOverlays();
                    RenderingStatistics.endOverlayRendering();
                }
                this.getGlContext().setSharedUniform(GeoxygeneConstants.GL_VarName_FboWidth, this.getFBOImageWidth());
                this.getGlContext().setSharedUniform(GeoxygeneConstants.GL_VarName_FboHeight, this.getFBOImageHeight());
            }
            //Save to img if necessary
            if (this.offScreenImgRendering) {
                drawOffscreenImage();
                this.offScreenImgRendering = false;
            }
            // Backbuffer to FrontBuffer
            this.swapBuffers();
            
            RenderingStatistics.endRendering();
            RenderingStatistics.printStatistics(System.err);
        } catch (Exception e) {
            logger.error("Error rendering the LwJGL : " + e.getMessage() + " [" + e.getClass().getSimpleName() + "]");
            e.printStackTrace();
        }

    }

    private void drawOffscreenImage() {
        BufferedImage bimg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        if (!this.getParentPanel().getRenderingManager().getLayers().isEmpty()) {
            int w = bimg.getWidth();
            int h = bimg.getHeight();
            int[] pixels = new int[w * h];
            ByteBuffer pixBuffer = ByteBuffer.allocateDirect(4 * w * h).order(ByteOrder.nativeOrder());
            GL11.glReadPixels(0, 0, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixBuffer);
            int bindex;
            for (int i = 0; i < pixels.length; i++) {
                bindex = i * 4;
                pixels[i] = ((pixBuffer.get(bindex + 3) << 24)) + ((pixBuffer.get(bindex) << 16)) + ((pixBuffer.get(bindex + 1) << 8)) + ((pixBuffer.get(bindex + 2) << 0));
            }
            try {
                // Create a BufferedImage with the RGB pixels then save as PNG
                bimg.setRGB(0, 0, w, h, pixels, 0, w);
            } catch (Exception e) {
                System.out.println("ScreenShot() exception: " + e);
                e.printStackTrace();
                return;
            }
            // Flip the image
            BufferedImage flipped = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
            AffineTransform tran = AffineTransform.getTranslateInstance(0, bimg.getHeight());
            AffineTransform flip = AffineTransform.getScaleInstance(1d, -1d);
            tran.concatenate(flip);
            Graphics2D g = flipped.createGraphics();
            g.setColor(this.getBackgroundColor());
            g.fillRect(0, 0, bimg.getWidth(), bimg.getHeight());
            g.setTransform(tran);
            g.drawImage(bimg, 0, 0, null);
            g.dispose();
            this.offscreenRenderedImg = flipped;
        }
    }

    /**
     * Draw the texture of the FBO into the backbuffer. THis is also where the
     * antialiazing step is done.
     */
    private void drawFBOPingPongInBackBuffer() {
        try {
            GLProgram program = this.getGlContext().getProgram(GL_ProgName_ScreenspaceAntialiasedTexture);
            this.getGlContext().setCurrentProgram(program);
            // Set the backbuffer and activate the FBO textures
            this.readFromPingPong(program);
            this.drawInBackBuffer();

            GL11.glDepthMask(false);
            glDisable(GL11.GL_DEPTH_TEST);

            GL30.glBindVertexArray(LayerViewGLPanel.getScreenQuad().getVaoId());
            program.setUniform(GeoxygeneConstants.GL_VarName_GlobalOpacityVarName, 1f);
            program.setUniform(GeoxygeneConstants.GL_VarName_ObjectOpacityVarName, 1f);
            program.setUniform(GeoxygeneConstants.GL_VarName_ScreenWidth, this.getWidth());
            program.setUniform(GeoxygeneConstants.GL_VarName_ScreenHeight, this.getHeight());
            program.setUniform(GeoxygeneConstants.GL_VarName_AntialiasingSize, ((int) this.getGlContext().getSharedUniform(GeoxygeneConstants.GL_VarName_AntialiasingSize)));

            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            GLTools.glCheckError("before FBO drawing textured quad");
            LwjglLayerRenderer.drawComplex(LayerViewGLPanel.getScreenQuad());
            GLTools.glCheckError("FBO drawing textured quad");

            GL30.glBindVertexArray(0); // unbind VAO
            GLTools.glCheckError("exiting FBO rendering");
            glBindTexture(GL_TEXTURE_2D, 0); // unbind texture
        } catch (GLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {
        this.gl_context_intialized = false;
    }

    private void drawBackground(int width, int height) throws GLException {
        if (this.getViewBackground() == null || this.getBackgroundTexture() == null || this.getBackgroundTexture() == null) {
            return;
        }

        GLProgram program = RenderingMethodDescriptor.retrieveMethod("Background").getGLProgram();
        ;
        if (program == null) {
            logger.error("No method available to paint a Background!");
            return;
        }
        this.getGlContext().setCurrentProgram(program);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL11.GL_POLYGON_SMOOTH);
        glViewport(0, 0, width, height);
        Viewport viewport = this.getParentPanel().getViewport();
        double scale = viewport.getScale();
        // paper scale factor used to convert from map length to world
        // length
        double psf = this.getViewBackground().getPaperHeightInCm() * this.getViewBackground().getPaperReferenceMapScale() / (100. * this.getBackgroundTexture().getTextureHeight());
        double paperWidthInWorldCoordinates = this.getBackgroundTexture().getTextureWidth() * psf;
        double paperHeightInWorldCoordinates = this.getBackgroundTexture().getTextureHeight() * psf;
        double screenWidthInWorldCoordinates = this.getWidth() / scale;
        double screenHeightInWorldCoordinates = this.getHeight() / scale;
        double u0 = (viewport.getViewOrigin().getX() % paperWidthInWorldCoordinates) / paperWidthInWorldCoordinates;
        double v0 = (viewport.getViewOrigin().getY() % paperHeightInWorldCoordinates) / paperHeightInWorldCoordinates;
        double deltaU = screenWidthInWorldCoordinates / paperWidthInWorldCoordinates;
        double deltaV = screenHeightInWorldCoordinates / paperHeightInWorldCoordinates;
        this.screenQuadNW.setUV((float) u0, (float) (v0 + deltaV));
        this.screenQuadNE.setUV((float) (u0 + deltaU), (float) (v0 + deltaV));
        this.screenQuadSE.setUV((float) (u0 + deltaU), (float) v0);
        this.screenQuadSW.setUV((float) u0, (float) v0);
        this.getScreenQuad().invalidateBuffers();
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + 1);
        glBindTexture(GL_TEXTURE_2D, this.getBackgroundTexture().getTextureId());
        program.setUniform("colorTexture1", 1);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        // Setup texture scaling filtering
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glDepthMask(false);
        glDisable(GL11.GL_DEPTH_TEST);

        GL30.glBindVertexArray(this.getScreenQuad().getVaoId());
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        GLTools.glCheckError("before background drawing textured quad");
        for (GLMesh mesh : this.getScreenQuad().getMeshes()) {
            GL11.glDrawElements(mesh.getGlType(), mesh.getLastIndex() - mesh.getFirstIndex() + 1, GL11.GL_UNSIGNED_INT, mesh.getFirstIndex() * (Integer.SIZE / 8));
        }
        this.getScreenQuad().setColor(this.getBackgroundColor());
        GLTools.glCheckError("background textured quad");
        glBindTexture(GL_TEXTURE_2D, 0); // unbind texture
        GL30.glBindVertexArray(0); // unbind VAO
        GLTools.glCheckError("exiting background rendering");
        glDisable(GL_TEXTURE_2D);

    }

    /**
     * @return the glContext
     * @throws GLException
     */
    @Override
    public GLContext getGlContext() {
        if (!gl_context_intialized) {
            GLContext activecontex = GLContext.getActiveGlContext();
            this.initializeGL4Context(activecontex);
            this.gl_context_intialized = true;
        }
        return GLContext.getActiveGlContext();
    }

    /**
     * This static method creates one GLContext containing all programs used to
     * render GeOxygene graphics elements
     * 
     * @return
     * @throws GLException
     */
    public GLContext initializeGL4Context(GLContext glContext) {
        /*
         * Build the default GLPrograms used by Geoxygene
         */
        // Build the default rendering program.
        // TODO : make it a rendering Method
        GLProgramBuilder builder = new GLProgramBuilder();
        builder.addDelegateBuilder(new WorldspaceColorGLProgramBuilder());
        GLProgram worlspacecolorprogram = builder.build(GL_ProgName_WorldspaceColor, null);

        // Build the program dedicated to applying the antialiasing.
        // TODO : make it a rendering Method
        builder = new GLProgramBuilder();
        builder.addDelegateBuilder(new ScreenspaceAntialiasedTextureGLProgramBuilder());
        GLProgram screenspaceAntialiasedTextureProgram = builder.build(GL_ProgName_ScreenspaceAntialiasedTexture, null);

        // Build the program dedicated to rendering the background
        GLProgram backgroundProgram = RenderingMethodDescriptor.retrieveMethod("Background").getGLProgram();

        glContext.addProgram(backgroundProgram);
        glContext.addProgram(worlspacecolorprogram);
        glContext.addProgram(screenspaceAntialiasedTextureProgram);

        return glContext;
    }

    private BasicTexture getBackgroundTexture() {
        if (this.getViewBackground() != this.storedBackground) {
            this.storedBackground = null;
            this.backgroundTexture = null;
        }
        if (this.backgroundTexture == null) {
            if (this.getViewBackground() != null) {
                TextureManager.getInstance();
                // For now , the background can only be a SimpleTexture
                if (this.getViewBackground().getTexture() instanceof SimpleTexture)
                    this.backgroundTexture = (BasicTexture) TextureManager.getTexture(this.getViewBackground().getTexture(), null, this.parentPanel.getViewport());
                this.storedBackground = this.getViewBackground();
            }
        }
        return this.backgroundTexture;
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
        // System.err.println("component resize to " + this.getSize() +
        // " in GLCanvas");
        try {
            this.makeCurrent();
        } catch (LWJGLException exception) {
            // if makeCurrent() throws an exception, then the canvas is not
            // ready
            return;
        } catch (IllegalStateException e2) {
            // The LwjglCanvas is not ready yet;
            return;
        }
        try {
            if (this.getContext() == null) {
                return;
            }
            this.setSize(this.getParentPanel().getSize());
            glViewport(0, 0, this.getWidth(), this.getHeight());
            this.updateFBODimensions();
            this.updateScreenSizeSharedUniforms();
        } catch (Exception e1) {
            // don't know hot to prevent/check this exception.
            // isDisplayable() and isValid() are both true at this point...
            logger.warn("Error resizing the heavyweight AWTGLCanvas : " + e1.getMessage());
        }
        this.repaint();
    }

    private void updateScreenSizeSharedUniforms() {
        this.getGlContext().setSharedUniform(GeoxygeneConstants.GL_VarName_FboWidth, this.getFBOImageWidth());
        this.getGlContext().setSharedUniform(GeoxygeneConstants.GL_VarName_FboHeight, this.getFBOImageHeight());
        this.getGlContext().setSharedUniform(GeoxygeneConstants.GL_VarName_ScreenWidth, this.getWidth());
        this.getGlContext().setSharedUniform(GeoxygeneConstants.GL_VarName_ScreenHeight, this.getHeight());
    }

    /**
     * paint overlays in GL windows
     * 
     * @throws GLException
     */

    // New version, try to optimize this time ...
    // TODO : do the optimization
    public void glPaintOverlays() throws GLException {

        if (this.bg == null || this.bg.getWidth() != this.getWidth() || this.bg.getHeight() != this.getHeight()) {
            this.bg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            this.buffer = BufferUtils.createByteBuffer(this.bg.getWidth() * this.bg.getHeight() * 4); // 4

            this.pixels = new int[this.bg.getWidth() * this.bg.getHeight()];
        }

        Graphics2D g = this.bg.createGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.setColor(Color.red);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        Composite fade = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        g.setComposite(fade);
        this.parentPanel.paintOverlays(g);
        this.bg.getRGB(0, 0, this.bg.getWidth(), this.bg.getHeight(), this.pixels, 0, this.bg.getWidth());
        this.buffer.clear();

        // TODO : delete that crap, please
        // To be replaced by that, pb it is reversed and ABGR
        // buffer.asIntBuffer().put(IntBuffer.wrap(pixels));

        for (int y = this.bg.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < this.bg.getWidth(); x++) {
                int pixel = this.pixels[y * this.bg.getWidth() + x];
                this.buffer.put((byte) (pixel >> 16 & 0xFF)); // Red component
                this.buffer.put((byte) (pixel >> 8 & 0xFF)); // Green component
                this.buffer.put((byte) (pixel >> 0 & 0xFF)); // Blue component
                this.buffer.put((byte) (pixel >> 24 & 0xFF)); // Alpha
                                                              // component.
                // Only for RGBA
                // System.err.println("transparency = " + (pixel >> 24 & 0xFF));
            }
        }
        this.buffer.rewind();
        g.dispose();
        glBindTexture(GL_TEXTURE_2D, this.getOverlayTextureId());

        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        // Setup texture scaling filtering
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.getWidth(), this.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.buffer);
        GLProgram program = this.getGlContext().getProgram(GL_ProgName_ScreenspaceAntialiasedTexture);
        this.getGlContext().setCurrentProgram(program);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, this.getWidth(), this.getHeight());
        GL11.glDrawBuffer(GL11.GL_BACK);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL11.GL_POLYGON_SMOOTH);

        program.setUniform("colorTexture1", 2);
        GLTools.glCheckError("Overlay bind antialiasing");
        program.setUniform(GeoxygeneConstants.GL_VarName_AntialiasingSize, 1);
        GLTools.glCheckError("Overlay activate texture");
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + 2);
        GLTools.glCheckError("Overlay bound texture");
        glBindTexture(GL_TEXTURE_2D, this.getOverlayTextureId()); // --> Usage?
        GLTools.glCheckError("Overlay bound texture");

        GL11.glDepthMask(false);
        glDisable(GL11.GL_DEPTH_TEST);

        GL30.glBindVertexArray(LayerViewGLPanel.getScreenQuad().getVaoId());

        program.setUniform(GeoxygeneConstants.GL_VarName_GlobalOpacityVarName, 1f);
        program.setUniform(GeoxygeneConstants.GL_VarName_ObjectOpacityVarName, 1f);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        //
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        GLTools.glCheckError("before Overlay drawing textured quad");

        // draw the background and legend and other stuff
        LwjglLayerRenderer.drawComplex(LayerViewGLPanel.getScreenQuad());

        // LayerViewGLPanel.getScreenQuad().setColor(new Color(1f, 1f, 1f,
        // .5f));
        GLTools.glCheckError("Overlay drawing textured quad");
        GL30.glBindVertexArray(0); // unbind VAO
        GLTools.glCheckError("exiting Overlay rendering");
        glBindTexture(GL_TEXTURE_2D, 0); // unbind texture

    }

    /*
     * ################ GL DRAWING METHODS ##########
     */

    /**
     * Set the current drawing mode so everything will be drawn in the
     * backbuffer of the default window-system framebuffer.
     * 
     * @throws GLException
     */
    public void drawInBackBuffer() throws GLException {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GLTools.glCheckError("viewport");
        GL11.glDrawBuffer(GL11.GL_BACK);
        GL11.glViewport(0, 0, this.getWidth(), this.getHeight());
        GLTools.glCheckError("Error while drawing in the BACK BUFFER");
    }

    /**
     * Set the current drawing mode so everything will be drawn in the texture
     * of the FBO.
     * 
     * @throws GLException
     */
    public void drawInFBOLayer() throws GLException {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.getFboId());
        GL11.glViewport(0, 0, this.getFBOImageWidth(), this.getFBOImageHeight());
        GLTools.glCheckError("viewport");
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        GLTools.glCheckError("draw Buffer COLOR ATT 0");
        glEnable(GL_TEXTURE_2D);
    }

    /*
     * ######################### FBO DEDICATED METHODS######################/
     */
    /**
     * FBO use status for this Canvas
     * 
     * @return true if the FBO is used.
     */
    public boolean isFBOActivated() {
        return this.bFBOactive;
    }

    /**
     * Activate the FBO for this Canvas
     * 
     * @param useFBO
     *            : true to activate the FBO
     */
    public void setFBO(boolean useFBO) {
        this.bFBOactive = useFBO;
        if (this.bFBOactive) {
            this.updateScreenSizeSharedUniforms();
        }
    }

    /**
     * Get the GL id of the FBo or generate a new one if there is none.
     * 
     * @return the fboId
     */
    public int getFboId() {
        if (this.fboId == -1) {
            this.fboId = glGenFramebuffers();
            if (this.fboId < 0) {
                logger.error("Unable to create frame buffer for FBO rendering");
            }
        }
        return this.fboId;
    }

    /**
     * Get the GL id of the FBO texture or generate a new one if there is none.
     * 
     * @return the FBO texture id.
     */
    public int getFboLayerTextureId() {
        if (this.layerFboTextureId == -1) {
            this.layerFboTextureId = GL11.glGenTextures();
            if (this.layerFboTextureId < 0) {
                logger.error("Unable to use FBO texture");
            }
        }
        return this.layerFboTextureId;
    }

    /**
     * Get the GL id of the first texture used by the ping-pong mecanism of the
     * FBO. Creates a new id if there is none.
     * 
     * @return the Gl id of the first ping-pong texture.
     */
    public int getFboPingPongTextureId1() {
        // System.err.println("get FBO texture ID : " + this.fboTextureId);
        if (this.fboPingPongId0 == -1) {
            this.fboPingPongId0 = GL11.glGenTextures();
            // System.err.println("generated FBO texture ID : "
            // + this.fboTextureId);
            if (this.fboPingPongId0 < 0) {
                logger.error("Unable to use FBO texture 0");
            }
        }
        return this.fboPingPongId0;
    }

    /**
     * Get the GL id of the second texture used by the ping-pong mecanism of the
     * FBO. Creates a new id if there is none.
     * 
     * @return the Gl id of the second ping-pong texture.
     */
    public int getFboPingPongTextureId2() {
        if (this.fboTextureId1 == -1) {
            this.fboTextureId1 = GL11.glGenTextures();
            if (this.fboTextureId1 < 0) {
                logger.error("Unable to use FBO texture ");
            }
        }
        return this.fboTextureId1;
    }

    /**
     * Reset the FBO size. Used to invalidate the FBO.
     */
    public final void invalidateFBO() {
        this.fboHeight = -1;
        this.fboWidth = -1;
        this.bFBOmod = true;
    }

    /**
     * @return the width of the current FBO texture.
     */
    public final int getFBOImageWidth() {
        return this.fboWidth;
    }

    /**
     * @return the height of the current FBO.
     */
    public final int getFBOImageHeight() {
        return this.fboHeight;
    }

    /**
     * The dimensions of the FBO depends on the canvas size and are multiplied
     * by the current antialiazing_size+1.
     */
    public final void updateFBODimensions() {
        int aa = (int) this.getGlContext().getSharedUniform(GeoxygeneConstants.GL_VarName_AntialiasingSize);
        this.fboWidth = (aa + 1) * this.getWidth();
        this.fboHeight = (aa + 1) * this.getHeight();
        this.bFBOmod = true;
    }

    /**
     * Init or refresh the FBO textures and properties. A FBO is refreshed
     * whenever the canvas size or the antialiazing levels change. <b>Also,
     * {@link #initializeFBO()} set the FBO as the current active
     * FrameBuffer.<b>
     * 
     * @throws GLException
     */
    public void initializeFBO() throws GLException {
        // Bind the FBO so it will be used as the FrameBuffer for all Gl
        // operations.
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.getFboId());
        // Is it the first time we create the FBO or did its dimensions change
        // since last rendering?
        if (this.bFBOmod) {
            int fboImageWidth = this.getFBOImageWidth();
            int fboImageHeight = this.getFBOImageHeight();
            GLTools.glCheckError("FBO size modification or first initialization");
            // initialize FBO layer texture. This texture will contain the
            // foreground texture (aka the layer just that was just draw).
            int fboLayerTextureId = this.getFboLayerTextureId();
            glBindTexture(GL_TEXTURE_2D, fboLayerTextureId);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_FALSE);
            GLTools.glCheckError("Error while creating the FBO texture");

            glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA8, fboImageWidth, fboImageHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
            glBindTexture(GL_TEXTURE_2D, 0);
            GLTools.glCheckError("Error while allocating the FBO texture");

            // initialize FBO ping-pong texture 0
            int fboPingPongTextureId0 = this.getFboPingPongTextureId1();
            glBindTexture(GL_TEXTURE_2D, fboPingPongTextureId0);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_FALSE);
            GLTools.glCheckError("texture initialization");
            glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA8, fboImageWidth, fboImageHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

            // initialize FBO ping-pong texture 1
            int fboPingPongTextureId1 = this.getFboPingPongTextureId2();
            glBindTexture(GL_TEXTURE_2D, fboPingPongTextureId1);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_FALSE);
            GLTools.glCheckError("texture initialization");
            glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA8, fboImageWidth, fboImageHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

            // bind FBO layer texture to ATT0
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, fboLayerTextureId, 0);
            int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
            if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
                throw new GLException("Frame Buffer Object is not correctly initialized");
            }
            GLTools.glCheckError("bind framebuffer");

            // bind FBO ping-pong texture 0 to ATT1
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, fboPingPongTextureId0, 0);
            status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
            if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
                throw new GLException("Frame Buffer Object is not correctly initialized");
            }
            GLTools.glCheckError("bind framebuffer ping-pong 0");

            // bind FBO ping-pong texture 1 to ATT2
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, fboPingPongTextureId1, 0);
            status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
            if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
                throw new GLException("Frame Buffer Object is not correctly initialized");
            }

            GLTools.glCheckError("FBO initialization end");
        }
        // Clear everything for a new rendering.
        GL11.glClearColor(0f, 0f, 0f, 0f);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT2);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        this.currentFboPingPongIndex = 2;
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        glEnable(GL_TEXTURE_2D);

        this.bFBOmod = false;
    }

    /**
     * Allow to read the ping-pong textures stored by the FBO. Depending on the
     * current ping-pong state, the first or second ping-pong texture will be
     * activated and set to the given {@link GLProgram}.
     * 
     * @param program
     *            the {@link GLProgram} that will use the FBO ping-pong texture
     * @throws GLException
     */
    public void readFromPingPong(GLProgram program) throws GLException {
        if (this.currentFboPingPongIndex == 2) {
            GLTools.glCheckError("FBO ATTACHMENT  bound");
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + 2);
            GLTools.glCheckError("FBO bound texture 1");
            glBindTexture(GL_TEXTURE_2D, this.getFboPingPongTextureId1());
            GLTools.glCheckError("FBO bound texture");
            program.setUniform("colorTexture1", 2);
        } else {
            GLTools.glCheckError("FBO ATTACHMENT 2 bound");
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + 3);
            GLTools.glCheckError("FBO bound texture 2");
            glBindTexture(GL_TEXTURE_2D, this.getFboPingPongTextureId2());
            GLTools.glCheckError("FBO bound texture");
            program.setUniform("colorTexture1", 3);
        }

    }

    /**
     * Allow a {@link GLProgram} to use the ping-pong textures of the FBO.
     * Depending on the current ping-pong state, the first or the second
     * ping-pong texture will be set. This method is used to draw the current
     * ping-pong texture into the final FBO texture to eventually display.
     * 
     * @param program
     *            the {@link GLProgram} that will use the FBO ping-pong texture
     * @throws GLException
     */
    public void drawInPingPong(GLProgram program) throws GLException {
        logger.debug("drawInPingPong START allocated mem = " + Runtime.getRuntime().totalMemory() / 1024. + " used");
        GLTools.glCheckError("FBO Ping Pong Rendering");

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.getFboId());
        GL11.glViewport(0, 0, this.getFBOImageWidth(), this.getFBOImageHeight());
        glEnable(GL_TEXTURE_2D);
        glDisable(GL11.GL_POLYGON_SMOOTH);
        GLTools.glCheckError("FBO bound");
        if (this.currentFboPingPongIndex != 1) {
            GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT2);
            GLTools.glCheckError("FBO ATTACHMENT_2 bound");

            GL13.glActiveTexture(GL13.GL_TEXTURE0 + 2);
            glBindTexture(GL_TEXTURE_2D, this.getFboPingPongTextureId1());
            GLTools.glCheckError("FBO bound texture");

            GL13.glActiveTexture(GL13.GL_TEXTURE0 + 1);
            glBindTexture(GL_TEXTURE_2D, this.getFboLayerTextureId());
            GLTools.glCheckError("FBO bound texture");

            program.setUniform(GeoxygeneConstants.GL_VarName_FBOBackgroundTexture, 2);
            program.setUniform(GeoxygeneConstants.GL_VarName_FBOForeGroundTexture, 1);
            GLTools.glCheckError("FBO bound texture");
            this.currentFboPingPongIndex = 1;
        } else {
            GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT1);
            GLTools.glCheckError("FBO ATTACHMENT 2 bound");

            GL13.glActiveTexture(GL13.GL_TEXTURE0 + 2);
            glBindTexture(GL_TEXTURE_2D, this.getFboPingPongTextureId2());

            GL13.glActiveTexture(GL13.GL_TEXTURE0 + 1);
            GLTools.glCheckError("FBO bound texture 2");
            glBindTexture(GL_TEXTURE_2D, this.getFboLayerTextureId());

            GLTools.glCheckError("FBO bound texture");
            program.setUniform(GeoxygeneConstants.GL_VarName_FBOBackgroundTexture, 2);

            program.setUniform(GeoxygeneConstants.GL_VarName_FBOForeGroundTexture, 1);
            this.currentFboPingPongIndex = 2;
        }
        // GLTools.glCheckError("FBO bind color texture");
        // program.setUniform(GeoxygeneConstants.GL_VarName_AntialiasingSize,
        // (int)
        // this.getGlContext().getSharedUniform(GeoxygeneConstants.GL_VarName_AntialiasingSize));
        // GLTools.glCheckError("FBO ping pong end");
        logger.debug("drawInPingPong START allocated mem = " + Runtime.getRuntime().totalMemory() / 1024. + " used");
    }

    /**
     * Clear the ping-pong texture correponding to the current ping-pong state.
     * 
     * @throws GLException
     */
    public void clearCurrentPingPongFBO() throws GLException {
        GLTools.glCheckError("FBO Ping Pong Clear");

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.getFboId());
        if (this.currentFboPingPongIndex != 1) {
            GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT2);
            GL11.glClearColor(0f, 0f, 0.f, 0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            GLTools.glCheckError("FBO ATTACHMENT 2 clear");
        } else {
            GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT1);
            GL11.glClearColor(0f, 0f, 0f, 0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            GLTools.glCheckError("FBO ATTACHMENT 1 clear");
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public boolean isQuickRendering() {
        return this.getParentPanel().getProjectFrame().getMainFrame().getMode().getCurrentMode().getRenderingType() != RenderingTypeMode.FINAL || !this.isFBOActivated()
                || this.getParentPanel().useWireframe();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        this.updateFBODimensions();
        this.updateScreenSizeSharedUniforms();
    }
}
