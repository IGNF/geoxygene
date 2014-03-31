/**
 * 
 */
package fr.ign.cogit.geoxygene.appli.layer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;

import org.apache.log4j.Logger;
import org.lwjgl.LWJGLException;

/** @author JeT GL drawable canvas inserted into a LayerViewLwjglPanel */
public class LayerViewLwjglLightWeightCanvas extends GLJPanel implements ComponentListener, GLEventListener {

    private static final long serialVersionUID = 2813681374260169340L; // serializable
                                                                       // UID
    private LayerViewGLPanel parentPanel = null;

    private static Logger logger = Logger.getLogger(LayerViewLwjglLightWeightCanvas.class.getName());

    /** @throws LWJGLException */
    public LayerViewLwjglLightWeightCanvas(LayerViewGLPanel parentPanel) throws LWJGLException {
        this.setParentPanel(parentPanel);
        this.addComponentListener(this);
        this.addGLEventListener(this);
    }

    /**
     * Set the parent panel
     */
    private final void setParentPanel(LayerViewGLPanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    protected void initGL() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, this.getWidth(), this.getHeight(), 0, -1000, 1000);

        glMatrixMode(GL_MODELVIEW);
        glViewport(0, 0, this.getWidth(), this.getHeight());

    }

    private static volatile Object renderingLock = new Object();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // paintGL();
    }

    /**
     * Draw all GL stuff into GL window
     */
    protected void paintGL() {

        if (!this.isDisplayable() || this.getContext() == null) {
            return;
        }

        try {
            synchronized (renderingLock) { // this lock ensure that only one
                                           // AWTGLCanvas can paint at a time
                // RenderGLUtil.glDraw(null);
                glClearColor(1f, 0f, 1f, 1);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                // this.parentPanel.repaint();
                if (this.parentPanel != null && this.parentPanel.getRenderingManager() != null) {
                    this.parentPanel.getRenderingManager().renderAll();
                }
                this.swapBuffers();
            }
        } catch (Exception e) {
            logger.error("Exception thrown in LwJGLLightweightCanvas::paint() : " + e.getMessage());
        }

    }

    @Override
    public void componentResized(ComponentEvent e) {
        // super.componentResized(e);
        try {
            System.err.println("LWJGLLightweightCanvas resized to " + this.getWidth() + "x" + this.getHeight());
        } catch (Exception e1) {
            logger.error("Error resizing the lightweight AWTGLCanvas : " + e1.getMessage());
            // e1.printStackTrace();
        }
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void componentShown(ComponentEvent e) {
        // TODO Auto-generated method stub

    }

}
