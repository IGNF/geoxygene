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
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultDesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.gui.FileChooser;
import fr.ign.cogit.geoxygene.appli.mode.ModeSelector;

/**
 * 
 * @author Julien Perret
 */
public class MainFrame extends JFrame {
  
  /** serial uid. */
  private static final long serialVersionUID = 1L;
  
  /** Logger of the application. */
  private static Logger logger = Logger.getLogger(MainFrame.class.getName());
  
  /** The associated application. */
  private GeOxygeneApplication application;
  
  /** The frame menu bar. */
  private GeOxgeneMenuBar menuBar;
  
  /** The default width of the frame. */
  private final int defaultFrameWidth = 800;
  
  /** The default height of the frame. */
  private final int defaultFrameHeight = 800;

  /** The mode selector. */
  private ModeSelector modeSelector = null;

  /**
   * Get the application logger.
   * 
   * @return the application logger
   */
  public static Logger getLogger() {
    return MainFrame.logger;
  }

  /**
   * The desktop pane containing the project frames.
   */
  private JDesktopPane desktopPane = new JDesktopPane() {
    private static final long serialVersionUID = 1L;
    {
      this.setDesktopManager(new DefaultDesktopManager());
    }
  };

  /**
   * Return the desktop pane containing the project frames.
   * 
   * @return the desktop pane containing the project frames
   */
  public final JDesktopPane getDesktopPane() {
    return this.desktopPane;
  }

  

  /**
   * Get the associated application.
   * 
   * @return the associated application
   */
  public final GeOxygeneApplication getApplication() {
    return this.application;
  }

  

  public JMenuBar getmenuBar() {
    return this.menuBar;
  }

  

  /**
   * Return the current application mode.
   * 
   * @return the current application mode
   */
  public final ModeSelector getMode() {
    return this.modeSelector;
  }

  

  public static FileChooser getFilechooser() {
    return GeOxgeneMenuBar.fc;
  }

  /**
   * Constructor using a title and an associated application.
   * 
   * @param title the title of the frame
   * @param theApplication the associated application
   */
  public MainFrame(final String title, final GeOxygeneApplication theApplication) {
    super(title);
    this.application = theApplication;
    this.setIconImage(this.application.getIcon().getImage());
    this.setLayout(new BorderLayout());
    this.setResizable(true);
    this.setSize(this.defaultFrameWidth, this.defaultFrameHeight);
    this.setExtendedState(Frame.MAXIMIZED_BOTH);
    this.menuBar = new GeOxgeneMenuBar(this);
    
    this.setJMenuBar(this.menuBar);
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(this.desktopPane, BorderLayout.CENTER);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(final WindowEvent e) {
        MainFrame.this.getApplication().exit();
      }
    });
    this.modeSelector = new ModeSelector(this);
    this.desktopPane.addContainerListener(modeSelector);
  }

  @Override
  public final void dispose() {
    for (JInternalFrame frame : this.desktopPane.getAllFrames()) {
      frame.dispose();
    }
    super.dispose();
  }

  /**
   * Return the selected (current) project frame.
   * 
   * @return the selected (current) project frame
   */
  public final ProjectFrame getSelectedProjectFrame() {
    if ((this.desktopPane.getSelectedFrame() == null)
        || !(this.desktopPane.getSelectedFrame() instanceof ProjectFrame)) {
      return null;
    }
    return (ProjectFrame) this.desktopPane.getSelectedFrame();
  }

  /**
   * Return all project frames.
   * 
   * @return an array containing all project frames available in the interface
   */
  public final ProjectFrame[] getAllProjectFrames() {
    List<ProjectFrame> projectFrameList = new ArrayList<ProjectFrame>();
    for (JInternalFrame frame : this.desktopPane.getAllFrames()) {
      if (frame instanceof ProjectFrame) {
        projectFrameList.add((ProjectFrame) frame);
      }
    }
    return projectFrameList.toArray(new ProjectFrame[0]);
  }

  /**
   * Create and return a new project frame.
   * 
   * @return the newly created project frame
   */
  public final ProjectFrame newProjectFrame() {
    ProjectFrame projectFrame = new ProjectFrame(this, this.application.getIcon());
    projectFrame.setSize(this.desktopPane.getSize());
    projectFrame.setVisible(true);
    this.desktopPane.add(projectFrame, JLayeredPane.DEFAULT_LAYER);
    try {
      projectFrame.setSelected(true);
    } catch (PropertyVetoException e) {
      e.printStackTrace();
    }
    projectFrame.setToolTipText(projectFrame.getTitle());
    return projectFrame;
  }

}

