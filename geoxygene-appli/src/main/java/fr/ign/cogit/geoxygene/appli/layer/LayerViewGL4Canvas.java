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

import java.awt.event.ComponentEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lwjgl.LWJGLException;

import fr.ign.cogit.geoxygene.appli.mode.MainFrameToolBar;
import fr.ign.cogit.geoxygene.appli.render.LayerRenderer;

/** @author JeT GL drawable canvas inserted into a LayerViewLwjglPanel */
public class LayerViewGL4Canvas extends LayerViewGLCanvas implements ChangeListener {

    private static final long serialVersionUID = 2813681374260169340L; // serializable
    private JToggleButton wireframeToggleButton = null;
    private JButton clearCacheButton = null;
    private boolean wireframe = false;

    /**
     * Constructor
     * 
     * @param parentPanel
     * @throws LWJGLException
     */
    public LayerViewGL4Canvas(final LayerViewGLPanel parentPanel) throws LWJGLException {
        super(parentPanel);
        parentPanel.getProjectFrame().getMainFrame().getMode().getToolBar().addSeparator();
        parentPanel.getProjectFrame().getMainFrame().getMode().getToolBar().add(this.getWireframeButton());
        parentPanel.getProjectFrame().getMainFrame().getMode().getToolBar().add(this.getClearCacheButton());
    }

    //    private void setupTextures() {
    //        try {
    //            this.texIds[0] = GLTools.loadTexture("./src/main/resources/textures/water.png");
    //            this.texIds[1] = GLTools.loadTexture("./src/main/resources/textures/cell02.png");
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //    }

    private JToggleButton getWireframeButton() {
        if (this.wireframeToggleButton == null) {
            this.wireframeToggleButton = new JToggleButton();
            this.wireframeToggleButton.setIcon(new ImageIcon(MainFrameToolBar.class.getResource("/images/icons/16x16/wireframe.png")));
            this.wireframeToggleButton.setSelected(this.isWireframe());
            this.wireframeToggleButton.addChangeListener(this);
        }
        return this.wireframeToggleButton;
    }

    private JButton getClearCacheButton() {
        if (this.clearCacheButton == null) {
            this.clearCacheButton = new JButton();
            this.clearCacheButton.setIcon(new ImageIcon(MainFrameToolBar.class.getResource("/images/icons/16x16/clear.png")));
            this.clearCacheButton.addChangeListener(this);
        }
        return this.clearCacheButton;
    }

    /**
     * @return the wireframe
     */
    @Override
    public boolean isWireframe() {
        return this.wireframe;
    }

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
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(0, this.getWidth(), this.getHeight(), 0, -1000, 1000);
            glMatrixMode(GL_MODELVIEW);
            glViewport(0, 0, this.getWidth(), this.getHeight());
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

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == this.getWireframeButton()) {
            this.wireframe = this.getWireframeButton().isSelected();
            this.getParentPanel().repaint();
        }

        if (e.getSource() == this.getClearCacheButton()) {
            for (LayerRenderer renderer : this.getParentPanel().getRenderingManager().getRenderers()) {
                renderer.reset();
            }
            this.getParentPanel().repaint();
        }

    }
}
