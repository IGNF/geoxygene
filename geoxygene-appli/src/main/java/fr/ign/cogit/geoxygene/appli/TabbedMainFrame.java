/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.icon.CompositeIcon;
import fr.ign.cogit.geoxygene.icon.VTextIcon;

/** @author Julien Perret */
public class TabbedMainFrame extends AbstractMainFrame {

  /** Logger of the application. */
  static Logger logger = Logger.getLogger(TabbedMainFrame.class.getName());

  /** The tabbed pane pane containing the project frames. */
  // private JTabbedPane tabbedPane = null;

  private final Map<JComponent, TabbedProjectFrame> projectFrameMap = new HashMap<JComponent, TabbedProjectFrame>();

  /**
   * Constructor using a title and an associated application.
   * 
   * @param title the title of the frame
   * @param theApplication the associated application
   */
  public TabbedMainFrame(final String title,
      final GeOxygeneApplication application) {
    super(title, application);
  }

  // /**
  // * @return the tabbed pane containing the project frames
  // */
  // public final JTabbedPane getTabbedPane() {
  // if (this.tabbedPane == null) {
  // this.tabbedPane = new JTabbedPane();
  // }
  // return this.tabbedPane;
  // }

  /**
   * Add a new frame into the main frame. implementation should check the frame
   * type before adding it...
   * @param frame frame to add
   */
  void addProjectFrame(final JTabbedPane currentDesktop,
      final ProjectFrame frame) {
    if (frame instanceof TabbedProjectFrame) {
      TabbedProjectFrame tabbedFrame = (TabbedProjectFrame) frame;

      VTextIcon textIcon = new VTextIcon(this.getDesktopTabbedPane(),
          tabbedFrame.getTitle(), VTextIcon.ROTATE_LEFT);
      CompositeIcon icon = new CompositeIcon(tabbedFrame.getIconImage(),
          textIcon);
      currentDesktop.addTab(null, icon, tabbedFrame.getGui());
    } else {
      logger.error("Cannot add a " + frame.getClass().getSimpleName()
          + " into a " + this.getClass().getSimpleName());
    }
  }

  private Component getSelectedComponent() {
    JTabbedPane currentDesktop = this.getCurrentDesktop();
    if (currentDesktop == null)
      return null;
    // for (int nChild = 0; nChild < currentDesktop.getComponentCount();
    // nChild++) {
    // Component child = currentDesktop.getComponent(nChild);
    // System.err.println("child of current desktop #" + nChild + " = "
    // + child.getClass().getSimpleName() + " => " + child);
    // }
    return currentDesktop.getSelectedComponent();
  }

  @Override
  public JTabbedPane getCurrentDesktop() {
    return (JTabbedPane) super.getCurrentDesktop();
  }

  @Override
  public final void dispose() {
    File previous = MainFrameMenuBar.fc.getPreviousDirectory();
    if (previous != null) {
      this.getApplication().getProperties()
          .setLastOpenedFile(previous.getAbsolutePath());
      this.getApplication().getProperties()
          .marshall(this.getApplication().getPropertiesFile().getFile());
    }

    for (ProjectFrame projectFrame : this.getDesktopProjectFrames()) {
      projectFrame.dispose();
    }
    this.removeAllProjectFrames();
    this.getGui().dispose();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.MainFrameTmp#getSelectedProjectFrame()
   */
  @Override
  public final ProjectFrame getSelectedProjectFrame() {
    Component selectedComponent = this.getSelectedComponent();
    if (selectedComponent == null)
      return null;

    try {
      return this.getProjectFrameFromGui(selectedComponent);
    } catch (Exception e) {
      logger
          .error("Selected GUI frame has not be inserted into the list of ProjectFrame ! ("
              + this.getDesktopProjectFrames().length + " elements)");
    }
    return null;
  }

  @Override
  JComponent createNewDesktop() {
    JTabbedPane newDesktopPane = new JTabbedPane(JTabbedPane.LEFT);
    newDesktopPane.addContainerListener(this.getMode());
    return newDesktopPane;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.MainFrameTmp#newProjectFrame()
   */
  @Override
  public final ProjectFrame newProjectFrame() {
    // create the project frame (only TabbedProjectFrame can be inserted in
    // TabbedMainFrame)
    TabbedProjectFrame projectFrame = new TabbedProjectFrame(this,
        new ImageIcon(
            GeOxygeneApplication.class
                .getResource("/images/icons/application.png")));
    // insert it into the managed list
    this.projectFrameMap.put(projectFrame.getGui(), projectFrame);

    logger.log(Level.DEBUG, "New tabbed project frame");
    JTabbedPane currentDesktop = this.getCurrentDesktop();
    if (currentDesktop == null) {
      logger
          .debug("No current desktop found newProjectFrame() method.Create a new desktop with name '"
              + projectFrame.getName() + "'");
      int index = this.getDesktopTabbedPane().getTabCount() + 1;
      currentDesktop = (JTabbedPane) createNewDesktop("Desktop #" + index);
    }
    addProjectFrame(currentDesktop, projectFrame);

    // projectFrame.getInternalFrame().setToolTipText(projectFrame.getTitle());
    return projectFrame;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.MainFrameTmp#getGui()
   */
  @Override
  public JFrame getGui() {
    return this.getFrame();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.appli.MainFrame#getProjectFrameFromGui(java.awt.
   * Component)
   */
  @Override
  public ProjectFrame getProjectFrameFromGui(final Component gui) {
    return projectFrameMap.get(gui);
  }

  @Override
  public void display(final boolean display) {
    if (this.getGui() != null) {
      this.getGui().setVisible(display);
    } else {
      logger.error("Cannot show/hide Frame. Gui has not been created yet");
    }

  }

  @Override
  public void setSelectedFrame(final ProjectFrame projectFrame) {
    for (int nProject = 0; nProject < this.getCurrentDesktop().getTabCount(); nProject++) {
      // use of == operator checks if object pointers are the same
      if (this.getCurrentDesktop().getTabComponentAt(nProject) == projectFrame) {
        this.getCurrentDesktop().setSelectedIndex(nProject);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.MainFrame#removeAllProjectFrames()
   */
  @Override
  public void removeAllProjectFrames() {

    ProjectFrame[] desktopProjectFrames = this.getDesktopProjectFrames();
    for (ProjectFrame projectFrame : desktopProjectFrames) {
      projectFrame.dispose();
      this.projectFrameMap.remove(projectFrame.getGui());
    }
  }

  /**
   * Get all frames of the CURRENT desktop
   */
  @Override
  public final ProjectFrame[] getDesktopProjectFrames() {
    List<ProjectFrame> projectFrameList = new ArrayList<ProjectFrame>();
    JTabbedPane currentDesktop = this.getCurrentDesktop();
    if (currentDesktop == null) {
      return new ProjectFrame[0];
    }

    for (int nComponent = 0; nComponent < currentDesktop.getComponentCount(); nComponent++) {
      Component frameGui = currentDesktop.getComponent(nComponent);
      ProjectFrame projectFrame = this.getProjectFrameFromGui(frameGui);
      if (projectFrame != null) {
        projectFrameList.add(projectFrame);
      }
    }
    return projectFrameList.toArray(new ProjectFrame[0]);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.MainFrame#getSize()
   */
  @Override
  public Dimension getSize() {
    return this.getGui().getSize();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.MainFrame#organizeProjectFrames()
   */
  @Override
  public void organizeCurrentDesktop() {
    logger.warn("No organization has to be done in tabbed project frames");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.MainFrame#add(javax.swing.JComponent,
   * java.lang.String)
   */
  @Override
  public void add(JComponent component, String layout) {
    this.getGui().getContentPane().add(component, layout);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.appli.MainFrame#addComponentInFrame(java.lang.String
   * , javax.swing.JComponent)
   */
  @Override
  public JComponent addComponentInFrame(String title, JComponent component) {
    JPanel tabbedPanel = new JPanel(new BorderLayout());
    tabbedPanel.add(component, BorderLayout.CENTER);
    this.getCurrentDesktop().addTab(title, null, tabbedPanel);
    return tabbedPanel;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.MainFrame#getIconImage()
   */
  @Override
  public Image getIconImage() {
    return this.getGui().getIconImage();
  }

}
