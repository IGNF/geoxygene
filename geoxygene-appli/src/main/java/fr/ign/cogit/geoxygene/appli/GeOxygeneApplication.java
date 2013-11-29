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

import java.awt.EventQueue;
import java.awt.Font;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.api.MainFrame;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanelFactory;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanelFactory.RenderingType;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.ProjectFramePlugin;
import fr.ign.cogit.geoxygene.appli.task.TaskManager;
import fr.ign.cogit.geoxygene.appli.ui.Message;

/**
 * Base class for GeOxygene applications.
 * 
 * @author Julien Perret
 */
public class GeOxygeneApplication {

  /** The splash Image used when running the application. */
  private static ImageIcon splashImage;
  private final static int MAX_CREATION_TIME = 5000;

  /** Logger. */
  private static Logger logger = Logger.getLogger(GeOxygeneApplication.class
      .getName());

  /**
   * The icon of the icon, i.e. the GeOxygene icon by default. Also used by
   * {@link LayerViewPanel}
   */
  private ImageIcon applicationIcon = new ImageIcon(
      GeOxygeneApplication.class.getResource("/images/icone.gif")); //$NON-NLS-1$

  /**
   * GeOxygene task manager (async process management)
   */
  private final TaskManager taskManager = new TaskManager();

  /**
   * @return the icon of the application
   * @see ImageIcon
   */
  public final ImageIcon getIcon() {
    return this.applicationIcon;
  }

  /** Default font size. */
  private final int fontSize = 10;

  /** The font used by the application. */
  private final Font font = new Font("Arial", //$NON-NLS-1$
      Font.PLAIN, this.fontSize);

  private GeOxygeneApplicationProperties properties = null;
  private URL propertiesFile = null;

  /** private main frame of the application. */
  private MainFrame frame;
  private String frameTitle = "GeOxygene";

  /**
   * Constructor.
   */
  public GeOxygeneApplication() {
    this("GeOxygene"); //$NON-NLS-1$
  }

  /**
   * Constructor.
   * 
   * @param title title of the application
   */
  public GeOxygeneApplication(final String title) {
    this(title, null);
  }

  /**
   * Constructor.
   * 
   * @param title title of the application
   * @param theApplicationIcon the application icon
   */
  public GeOxygeneApplication(final String title,
      final ImageIcon theApplicationIcon) {

    this.frameTitle = title;
    if (theApplicationIcon != null) {
      this.applicationIcon = theApplicationIcon;
    }

    // register application in the event manager
    GeOxygeneEventManager.getInstance().setApplication(this);

    // Load properties from geoxygene-configuation.xml file
    this.loadProperties("./geoxygene-configuration.xml");

    MainFrameMenuBar.fc.setPreviousDirectory(new File(
        GeOxygeneApplication.this.properties.getLastOpenedFile()));

    // Initialize application plugins
    this.initializeApplicationPlugins();
    this.initializeProjectFramePlugins();

    this.preload();
  }

  /**
   * Application title (used as Frame Title)
   */
  public String getFrameTitle() {
    return this.frameTitle;
  }

  /**
   * @return the task manager
   */
  public TaskManager getTaskManager() {
    return this.taskManager;
  }

  /**
   * preload files defined in the configuration file <preload></preload>
   */
  private void preload() {
    final MainFrame mainFrame = this.getMainFrame();
    RenderingType renderingType = RenderingType.valueOf(this.properties
        .getDefaultVisualizationType());
    LayerViewPanelFactory.setRenderingType(renderingType);
    ProjectFrame projectFrame = null;
    // create a new project frame or use the existing one
    // wait for desktopFrames to be created
    long startTime = new Date().getTime();
    while (projectFrame == null
        && new Date().getTime() - startTime < MAX_CREATION_TIME) {
      if (mainFrame.getDesktopProjectFrames().length != 0) {
        projectFrame = mainFrame.getDesktopProjectFrames()[mainFrame
            .getDesktopProjectFrames().length - 1];
      }
      if (projectFrame == null) {
        try {
          projectFrame = mainFrame.newProjectFrame();
        } catch (Exception e) {
          // mainFrame is not ready to generate project frame...
        }
      }
    }

    for (String filename : GeOxygeneApplication.this.properties.getPreloads()) {
      PreloadTask preloadTask = new PreloadTask(mainFrame, projectFrame,
          filename, renderingType);
      preloadTask.run();
      // this.getTaskManager().addTask(preloadTask);
      // preloadTask.start();
    }

  }

  /** @return The font to be used for all menus, etc. */
  public final Font getFont() {
    return this.font;
  }

  /** @return The splash image */
  public static synchronized ImageIcon splashImage() {
    if (GeOxygeneApplication.splashImage == null) {
      synchronized (GeOxygeneApplication.class) {
        GeOxygeneApplication.splashImage = new ImageIcon(
            GeOxygeneApplication.class
                .getResource("/images/geoxygene-logo.png")); //$NON-NLS-1$
      }
    }
    return GeOxygeneApplication.splashImage;
  }

  /** @return The main frame of the application. */
  public MainFrame getMainFrame() {
    if (this.frame == null) {
      // launch all graphics initialization in the EDT
      try {
        EventQueue.invokeAndWait(new Runnable() {
          @Override
          public void run() {
            System.out
                .println("GeOxygeneApplication.this.properties.getProjectFrameLayout() = "
                    + GeOxygeneApplication.this.properties
                        .getProjectFrameLayout());
            if (GeOxygeneApplication.this.properties.getProjectFrameLayout()
                .compareToIgnoreCase("tabbed") == 0) {
              GeOxygeneApplication.this.frame = new TabbedMainFrame(
                  GeOxygeneApplication.this.getFrameTitle(),
                  GeOxygeneApplication.this);
            } else {
              GeOxygeneApplication.this.frame = new FloatingMainFrame(
                  GeOxygeneApplication.this.getFrameTitle(),
                  GeOxygeneApplication.this);
            }
            GeOxygeneApplication.this.frame.display(true);
            GeOxygeneApplication.this.frame.addMessage(
                Message.MessageType.INFO, "Welcome to GeOxygene");
          }
        });
      } catch (InvocationTargetException e1) {
        logger.error(e1.getMessage());
        e1.printStackTrace();
      } catch (InterruptedException e1) {
        logger.error(e1.getMessage());
        e1.printStackTrace();
      }

    }
    return this.frame;
  }

  private void loadProperties(String propertyFilename) {
    try {
      this.propertiesFile = new URL("file", "", propertyFilename); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    this.properties = GeOxygeneApplicationProperties
        .unmarshall(this.propertiesFile.getFile());
  }

  /**
   * Initialize the application plugins.
   */
  private void initializeApplicationPlugins() {
    for (String pluginName : this.properties.getPlugins()) {
      try {
        Class<?> pluginClass = Class.forName(pluginName);
        if (GeOxygeneApplicationPlugin.class.isAssignableFrom(pluginClass
            .newInstance().getClass())) {
          GeOxygeneApplicationPlugin plugin = (GeOxygeneApplicationPlugin) pluginClass
              .newInstance();
          plugin.initialize(this);
        } else if (ProjectFramePlugin.class.isAssignableFrom(pluginClass
            .newInstance().getClass())) {
          logger
              .error(pluginClass
                  + " should not be called has a GeOxygeneApplicationPlugin, but as a ProjectFramePlugin");
        }
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Initialize projectFrame plugins.
   */
  // This method is public to be called when creating a new ProjectFrame
  public void initializeProjectFramePlugins() {
    if (this.properties.getProjectPlugins().size() != 0
        && this.getMainFrame().getSelectedProjectFrame() == null) {
      this.getMainFrame().newProjectFrame();
    }
    for (String pluginName : this.properties.getProjectPlugins()) {
      try {
        Class<?> pluginClass = Class.forName(pluginName);

        if (ProjectFramePlugin.class.isAssignableFrom(pluginClass.newInstance()
            .getClass())) {
          ProjectFramePlugin plugin = (ProjectFramePlugin) pluginClass
              .newInstance();
          plugin.initialize(this.getMainFrame().getSelectedProjectFrame());
          this.getMainFrame().getGui().repaint();
        } else if (GeOxygeneApplicationPlugin.class
            .isAssignableFrom(pluginClass.newInstance().getClass())) {
          logger
              .error(pluginClass
                  + " should not be called has a ProjectFramePlugin, but as a GeOxygeneApplicationPlugin");
        }
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  /** Exit the application. */
  public final void exit() {
    File previous = MainFrameMenuBar.fc.getPreviousDirectory();
    if (previous != null) {
      this.properties.setLastOpenedFile(previous.getAbsolutePath());
      this.properties.marshall(this.propertiesFile.getFile());
    }
    this.getMainFrame().close();
    this.getMainFrame().display(false);
    this.getMainFrame().dispose();
  }

  /**
   * Main GeOxygene Application.
   * 
   * @param args arguments of the application
   */
  public static void main(final String[] args) {

    // this library is added on Linux system to cover a lack of call of
    // XInitThread by the JVM
    // this call should be placed inside the JVM loading, but this is the very
    // early place we can do it
    // this small hack is only for Linux, other platforms are ok with
    // multithreaded call to X11
    if (System.getProperties().getProperty("java.awt.graphicsenv")
        .contains("X11")) {
      logger
          .info("Current display uses X11. Do a native call to 'xInitThread()' method");
      try {
        System.loadLibrary("xinitthread");
      } catch (Throwable e) {
        logger
            .error("The xinitthread library was not found. You should add ./lib/xinithread/linux-amd64 to the library path (LD_LIBRARY_PATH)");
        logger.info("Current LD_LIBRARY_PATH = "
            + System.getenv("LD_LIBRARY_PATH"));
        logger.info("Current java.library.path = "
            + System.getProperty("java.library.path"));
      }
    }
    final SplashScreen splashScreen = new SplashScreen(
        GeOxygeneApplication.splashImage(), "GeOxygene"); //$NON-NLS-1$
    splashScreen.setVisible(true);
    final GeOxygeneApplication application = new GeOxygeneApplication();
    // application.getMainFrame().newProjectFrame();
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        application.getMainFrame().display(true);
        splashScreen.setVisible(false);
        splashScreen.dispose();

      }
    });
  }

  public GeOxygeneApplicationProperties getProperties() {
    return this.properties;
  }

  public void setProperties(GeOxygeneApplicationProperties properties) {
    this.properties = properties;
  }

  public URL getPropertiesFile() {
    return this.propertiesFile;
  }

  public void setPropertiesFile(URL propertiesFile) {
    this.propertiesFile = propertiesFile;
  }
}
