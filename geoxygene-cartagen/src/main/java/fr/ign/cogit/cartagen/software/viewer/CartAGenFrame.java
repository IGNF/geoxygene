package fr.ign.cogit.cartagen.software.viewer;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.geoxygene.appli.FloatingMainFrame;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;


public class CartAGenFrame extends FloatingMainFrame  {

  /****/
  private static final long serialVersionUID = 1L;
  /** The associated application. */
  private CartAGenApplicationTest application;

  public CartAGenFrame(String title, GeOxygeneApplication theApplication) {
    super(title, theApplication);
    this.application = (CartAGenApplicationTest) theApplication;
  }

  public CartAGenApplicationTest getCartAGenApplication() {
    return application;
  }

  public void setCartAGenApplication(CartAGenApplicationTest application) {
    this.application = application;
  }

  /**
   * Return all project frames.
   * 
   * @return an array containing all project frames available in the interface
   */
  public CartAGenProjectFrame[] getAllCartProjectFrames() {
    List<CartAGenProjectFrame> projectFrameList = new ArrayList<CartAGenProjectFrame>();
    for (ProjectFrame frame : this.getDesktopProjectFrames()) {
      if (frame instanceof CartAGenProjectFrame) {
        projectFrameList.add((CartAGenProjectFrame) frame);
      }
    }
    return projectFrameList.toArray(new CartAGenProjectFrame[0]);
  }

  /**
   * Create and return a new project frame.
   * 
   * @return the newly created project frame
   */
  public final CartAGenProjectFrame newCartProjectFrame() {
    CartAGenProjectFrame projectFrame = new CartAGenProjectFrame(this,
        this.application.getIcon());
    projectFrame.getInternalFrame().setSize(this.getGui().getSize());
    projectFrame.getInternalFrame().setVisible(true);
    this.addComponentInFrame(projectFrame.getTitle(), projectFrame.getInternalFrame());
    try {
      projectFrame.getInternalFrame().setSelected(true);
    } catch (PropertyVetoException e) {
      e.printStackTrace();
    }
    projectFrame.getInternalFrame().setToolTipText(projectFrame.getTitle());
    return projectFrame;
  }

  /**
   * Gets the project frame of a given {@link CartAGenDB} identified by its
   * name.
   * @param dbName
   * @return
   */
  public CartAGenProjectFrame getProjectFrameFromDb(String dbName) {
    for (CartAGenProjectFrame frame : this.getAllCartProjectFrames()) {
        if ( frame.getDb() == null)
          continue;
        if (frame.getDb().getName().equals(dbName))
          return (CartAGenProjectFrame) frame;
    }
    return null;
  }
}
