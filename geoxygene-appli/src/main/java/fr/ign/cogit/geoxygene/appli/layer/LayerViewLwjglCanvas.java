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
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.apache.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;

/** @author JeT GL drawable canvas inserted into a LayerViewLwjglPanel */
public class LayerViewLwjglCanvas extends AWTGLCanvas implements
    ComponentListener, MouseMotionListener, MouseListener {

  private static final long serialVersionUID = 2813681374260169340L; // serializable
                                                                     // UID
  private LayerViewGLPanel parentPanel = null;
  private final boolean doPaintOverlay = false;

  private static Logger logger = Logger.getLogger(LayerViewLwjglCanvas.class
      .getName());

  /** @throws LWJGLException */
  public LayerViewLwjglCanvas(final LayerViewGLPanel parentPanel)
      throws LWJGLException {
    super();
    this.setParentPanel(parentPanel);
    this.addComponentListener(this);
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
  }

  /**
   * Set the parent panel
   */
  private final void setParentPanel(final LayerViewGLPanel parentPanel) {
    this.parentPanel = parentPanel;
  }

  /**
   * @return the parentPanel
   */
  public LayerViewGLPanel getParentPanel() {
    return this.parentPanel;
  }

  @Override
  protected void initGL() {
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glOrtho(0, this.getWidth(), this.getHeight(), 0, -1000, 1000);

    glMatrixMode(GL_MODELVIEW);
    glViewport(0, 0, this.getWidth(), this.getHeight());

  }

  // private static volatile Object renderingLock = new Object();

  // info found in JVM source code :
  // http://www.docjar.com/html/api/sun/awt/SunToolkit.java.html (:208)
  // But it doesn't change anything...
  //
  // assert !sun.awt.SunToolkit.isAWTLockHeldByCurrentThread();
  // sun.awt.SunToolkit.awtLock();
  // try {
  // } finally {
  // sun.awt.SunToolkit.awtUnlock();
  // }

  // http://comments.gmane.org/gmane.comp.java.openjdk.awt.devel/5073
  // sun.java2d.opengl.OGLRenderQueue rq =
  // sun.java2d.opengl.OGLRenderQueue.getInstance();
  // rq.tryLock();
  // rq.unlock();
  @Override
  protected void paintGL() {

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
      if (this.getParentPanel() != null
          && this.parentPanel.getRenderingManager() != null) {
        this.getParentPanel().getRenderingManager().renderAll();
      }

      // System.err.println("-------------------------------------------------- swap buffers --------------------------------");

      if (this.doPaintOverlay()) {
        this.getParentPanel().glPaintOverlays();
      }

      this.swapBuffers();
    } catch (LWJGLException e) {
      logger.error("Error rendering the LwJGL : " + e.getMessage());
      // e.printStackTrace();
    }

  }

  /**
   * @return true if overlays have to be painted on rendering media
   */
  private boolean doPaintOverlay() {
    return this.doPaintOverlay;
  }

  @Override
  public void componentResized(final ComponentEvent e) {
    if (this.getSize().equals(this.getParentPanel().getSize()))
      return;
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
      logger.warn("Error resizing the heavyweight AWTGLCanvas : "
          + e1.getMessage());
      // e1.printStackTrace();
    }
    this.repaint(); // super.componentResized(e);
  }

  @Override
  public void mouseClicked(final MouseEvent e) {
    this.getParentPanel().dispatchEvent(e);
  }

  @Override
  public void mouseEntered(final MouseEvent e) {
    this.getParentPanel().dispatchEvent(e);
  }

  @Override
  public void mouseExited(final MouseEvent e) {
    this.getParentPanel().dispatchEvent(e);
  }

  @Override
  public void mousePressed(final MouseEvent e) {
    this.getParentPanel().dispatchEvent(e);
  }

  @Override
  public void mouseReleased(final MouseEvent e) {
    this.getParentPanel().dispatchEvent(e);
  }

  @Override
  public void mouseDragged(final MouseEvent e) {
    this.getParentPanel().dispatchEvent(e);
  }

  @Override
  public void mouseMoved(final MouseEvent e) {
    this.getParentPanel().dispatchEvent(e);
  }

}
