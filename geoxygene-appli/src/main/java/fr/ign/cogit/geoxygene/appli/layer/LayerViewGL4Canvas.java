/**
 * 
 */
package fr.ign.cogit.geoxygene.appli.layer;

import static org.lwjgl.opengl.GL11.*;

import java.awt.event.ComponentEvent;

import org.lwjgl.LWJGLException;

/** @author JeT GL drawable canvas inserted into a LayerViewLwjglPanel */
public class LayerViewGL4Canvas extends LayerViewGLCanvas {

    private static final long serialVersionUID = 2813681374260169340L; // serializable

    /**
     * Constructor
     * 
     * @param parentPanel
     * @throws LWJGLException
     */
    public LayerViewGL4Canvas(final LayerViewGLPanel parentPanel) throws LWJGLException {
        super(parentPanel);
    }

    //    private void setupTextures() {
    //        try {
    //            this.texIds[0] = GLTools.loadTexture("./src/main/resources/textures/water.png");
    //            this.texIds[1] = GLTools.loadTexture("./src/main/resources/textures/cell02.png");
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //    }

    @Override
    protected void initGL() {
        super.initGL();
        glViewport(0, 0, this.getWidth(), this.getHeight());

    }

    @Override
    protected void paintGL() {
        super.paintGL();
        if (!this.isDisplayable() || this.getContext() == null) {
            return;
        }

        try {
            this.makeCurrent();
        } catch (LWJGLException exception) {
            // if makeCurrent() throws an exception, then the canvas is not ready
            return;
        }

        // synchronized (renderingLock) { // this lock ensure that only one
        // AWTGLCanvas can paint at a time
        try {
            // System.err.println("-------------------------------------------------- paint GL --------------------------------");
            // RenderGLUtil.glDraw(null);
            glClearColor(1f, 1f, 0.9f, 1);
            glClear(GL_COLOR_BUFFER_BIT);
            glClearColor(0f, 0f, 0f, 1);
            glClear(GL_DEPTH_BUFFER_BIT);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_BLEND);
            // this.parentPanel.repaint();
            if (this.getParentPanel() != null && this.getParentPanel().getRenderingManager() != null) {
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
            // if makeCurrent() throws an exception, then the canvas is not ready
            return;
        }
        try {
            if (this.getContext() == null) {
                return;
            }
            this.setSize(this.getParentPanel().getSize());
            //            glMatrixMode(GL_PROJECTION);
            //            glLoadIdentity();
            //            glOrtho(0, this.getWidth(), this.getHeight(), 0, -1000, 1000);
            //            glMatrixMode(GL_MODELVIEW);
            //            glViewport(0, 0, this.getWidth(), this.getHeight());
        } catch (Exception e1) {
            // don't know hot to prevent/check this exception.
            // isDisplayable() and isValid() are both true at this point...
            logger.warn("Error resizing the heavyweight AWTGLCanvas : " + e1.getMessage());
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
