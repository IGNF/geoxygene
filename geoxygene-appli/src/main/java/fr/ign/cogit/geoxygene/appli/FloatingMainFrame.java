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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultDesktopManager;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;

/** @author Julien Perret */
public class FloatingMainFrame extends AbstractMainFrame {

  /** Serializable UID. */
  private static final long serialVersionUID = -6931105831522187478L;  

  /** Logger of the application. */
  static Logger logger = Logger.getLogger(FloatingMainFrame.class.getName());

  private final Map<JComponent, FloatingProjectFrame> projectFrameMap = new HashMap<JComponent, FloatingProjectFrame>();
  
  /**
   * Constructor using a title and an associated application.
   * 
   * @param title the title of the frame
   * @param theApplication the associated application
   */
  public FloatingMainFrame(final String title, final GeOxygeneApplication application) {
    super(title, application);
  }

  /**
   * Add a new frame into the main frame. implementation should check the frame
   * type before adding it...
   * @param project frame to add
   * @throws PropertyVetoException 
   */
  void addProjectFrame(final JDesktopPane desktop, final ProjectFrame project) {
    if (project instanceof FloatingProjectFrame) {
      
      try {
        FloatingProjectFrame floatingProject = (FloatingProjectFrame) project;
        logger.debug("N°" + desktop.getAllFrames().length);
        desktop.add(floatingProject.getInternalFrame());
        
        floatingProject.getInternalFrame().setVisible(true);
        floatingProject.getInternalFrame().setSelected(true);
        
        desktop.setSelectedFrame(floatingProject.getInternalFrame());
      } catch (Exception e) {
        e.printStackTrace();
        logger.error("not allowed to set floatingProject visible and selected");
      }
    } else {
      logger.error("Cannot add a " + project.getClass().getSimpleName()
          + " into a " + this.getClass().getSimpleName());
    }
  }

  @Override
  public JDesktopPane getCurrentDesktop() {
    return (JDesktopPane) super.getCurrentDesktop();
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

    // FIXME: how to reach all desktops ?
    // for (JInternalFrame frame : this.getIFrameDocker().getAllFrames()) {
    // frame.dispose();
    // }
    this.getGui().dispose();
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
    if (gui == null)
      return null;
    return projectFrameMap.get(gui);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.MainFrameTmp#getSelectedProjectFrame()
   */
  @Override
  public final ProjectFrame getSelectedProjectFrame() {
    JDesktopPane currentDesktop = this.getCurrentDesktop();
    if (currentDesktop == null) {
      return null;
    }
    return getProjectFrameFromGui(currentDesktop.getSelectedFrame());
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.MainFrameTmp#newProjectFrame()
   */
  @Override
  public final ProjectFrame newProjectFrame() {

    // create the project frame (only FloatingProjectFrame can be inserted in
    // FloatingMainFrame)
    FloatingProjectFrame projectFrame = new FloatingProjectFrame(this,
        new ImageIcon(
            GeOxygeneApplication.class
                .getResource("/images/icons/application.png")));
    // insert it into the managed list
    this.projectFrameMap.put(projectFrame.getGui(), projectFrame);
    // set ProjectFrame initial values

    logger.log(Level.DEBUG, "New floating project frame");
    // Add ProjectFrame to the selected tabbedPane
    JDesktopPane currentDesktop = this.getCurrentDesktop();

    if (currentDesktop == null) {
      logger
          .debug("No current desktop found newProjectFrame() method.Create a new desktop with name '"
              + projectFrame.getName() + "'");
      // currentDesktop = (JDesktopPane) createNewDesktop(projectFrame.getName());
      int index = this.getDesktopTabbedPane().getTabCount() + 1;
      currentDesktop = (JDesktopPane) createNewDesktop("Desktop #" + index);
    }

    projectFrame.getInternalFrame().setSize(
        (int) this.getSize().getWidth() / 3 * 2,
        (int) this.getSize().getHeight() / 3 * 2);
    projectFrame.getInternalFrame().setVisible(true);
    addProjectFrame(currentDesktop, projectFrame);
    projectFrame.getInternalFrame().setToolTipText(projectFrame.getTitle());
    return projectFrame;
  }

  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // *
  // fr.ign.cogit.geoxygene.appli.MainFrame#addComponentInFrame(java.lang.String
  // * , javax.swing.JComponent)
  // */
  // @Override
  // public final JInternalFrame addComponentInFrame(String title,
  // JComponent component) {
  // JInternalFrame iFrame = new JInternalFrame(title);
  // iFrame.getContentPane().add(component);
  // return iFrame;
  // }

  @Override
  JComponent createNewDesktop() {
    JDesktopPane newDesktop = new JDesktopPane();
    newDesktop.setDesktopManager(new DefaultDesktopManager());
    newDesktop.addContainerListener(this.getMode());
    logger.debug("Add event in Menu");
    newDesktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    return newDesktop;
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

  @Override
  public void display(final boolean display) {
    if (this.getGui() != null) {
      this.getGui().setVisible(display);
    } else {
      logger.error("Cannot show/hide Frame. Gui has not been created yet");
    }

  }

  /**
   * Get all frames of the CURRENT desktop
   */
  @Override
  public final ProjectFrame[] getDesktopProjectFrames() {
    List<ProjectFrame> projectFrameList = new ArrayList<ProjectFrame>();
    JDesktopPane currentDesktop = this.getCurrentDesktop();
    if (currentDesktop == null) {
      return new ProjectFrame[0];
    }

    for (JInternalFrame frameGui : currentDesktop.getAllFrames()) {
      ProjectFrame projectFrame = this.getProjectFrameFromGui(frameGui);
      if (projectFrame != null) {
        projectFrameList.add(projectFrame);
      }
    }
    return projectFrameList.toArray(new ProjectFrame[0]);
  }

  @Override
  public void setSelectedFrame(final ProjectFrame projectFrame) {
    // Maybe we should check that the project frame is in the CURRENT desktop...
    this.getCurrentDesktop().setSelectedFrame(
        (JInternalFrame) projectFrame.getGui());
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
    JDesktopPane currentDesktop = this.getCurrentDesktop();
    if (currentDesktop == null) {
      return;
    }

    ProjectFrame[] projectFrames = this.getDesktopProjectFrames();

    currentDesktop.removeAll();
    GridLayout layout = new GridLayout(0, 2);
    currentDesktop.setLayout(layout);
    for (ProjectFrame project : projectFrames) {
      currentDesktop.add(project.getGui());
    }
    currentDesktop.doLayout();
    currentDesktop.setLayout(null); // now set layout to free moves
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
   * @see fr.ign.cogit.geoxygene.appli.MainFrame#getIconImage()
   */
  @Override
  public Image getIconImage() {
    return this.getGui().getIconImage();
  }

  @Override
  public JComponent addComponentInFrame(String title, JComponent component) {
    throw new UnsupportedOperationException(
        "Don't understand what this method is intended to do... [JeT]");
  }

}
