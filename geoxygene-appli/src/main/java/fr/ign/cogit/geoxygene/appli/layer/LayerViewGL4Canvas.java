/**
 * 
 */
package fr.ign.cogit.geoxygene.appli.layer;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import org.lwjgl.LWJGLException;

import fr.ign.cogit.geoxygene.util.gl.GLTools;

/** @author JeT GL drawable canvas inserted into a LayerViewLwjglPanel */
public class LayerViewGL4Canvas extends LayerViewGLCanvas implements
        ComponentListener {

    private static final long serialVersionUID = 2813681374260169340L; // serializable
    private Thread glCanvasThreadOwner = null; // stores the thread that ows gl
                                               // context to check consistency

    /**
     * Constructor
     * 
     * @param parentPanel
     * @throws LWJGLException
     */
    public LayerViewGL4Canvas(final LayerViewGLPanel parentPanel)
            throws LWJGLException {
        super(parentPanel);
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
            // System.err.println("-------------------------------------------------- paint GL --------------------------------");
            // RenderGLUtil.glDraw(null);
            GLTools.glClear(this.getBackground(), GL_COLOR_BUFFER_BIT);
            GLTools.glClear(0f, 0f, 0f, 1f, GL_DEPTH_BUFFER_BIT);
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
        } catch (LWJGLException e) {
            logger.error("Error rendering the LwJGL : " + e.getMessage());
            // e.printStackTrace();
        }
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
