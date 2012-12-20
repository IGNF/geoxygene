/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/*
 * Created on 26 juil. 2005
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.software.interfacecartagen.mode.ModeSelector;

/**
 * La fenetre principale de l'interface
 * @author julien Gaffuri
 * 
 */
public class GeoxygeneFrame extends JFrame {
  private static final long serialVersionUID = -3566099212243269188L;
  static Logger logger = Logger.getLogger(GeoxygeneFrame.class.getName());

  /**
   * Le menu
   */
  private GeoxygeneMenu menu = null;

  /**
   * @return
   */
  public GeoxygeneMenu getMenu() {
    if (this.menu == null) {
      synchronized (GeoxygeneFrame.class) {
        if (this.menu == null) {
          this.menu = new GeoxygeneMenu(this);
        }
      }
    }
    return this.menu;
  }

  /**
   * Le panneau de visualisation
   */
  private VisuPanel visuPanel = null;

  /**
   * @return
   */
  public VisuPanel getVisuPanel() {
    if (this.visuPanel == null) {
      synchronized (GeoxygeneFrame.class) {
        if (this.visuPanel == null) {
          this.visuPanel = new VisuPanel(this, this.getLayerManager());
        }
      }
    }
    return this.visuPanel;
  }

  private LayerManager layerManager = null;

  public LayerManager getLayerManager() {
    if (this.layerManager == null) {
      synchronized (GeoxygeneFrame.class) {
        if (this.layerManager == null) {
          this.layerManager = new LayerManager();
        }
      }
    }
    return this.layerManager;
  }

  /**
   * Le panneau en haut, sous le menu (barre d'outils)
   */
  private TopPanel topPanel = null;

  /**
   * @return
   */
  public TopPanel getTopPanel() {
    if (this.topPanel == null) {
      synchronized (GeoxygeneFrame.class) {
        if (this.topPanel == null) {
          this.topPanel = new TopPanel(this);
        }
      }
    }
    return this.topPanel;
  }

  /**
   * Le panneau en bas
   */
  private BottomPanel bottomPanel = null;

  public BottomPanel getBottomPanel() {
    if (this.bottomPanel == null) {
      synchronized (GeoxygeneFrame.class) {
        if (this.bottomPanel == null) {
          this.bottomPanel = new BottomPanel(this);
        }
      }
    }
    return this.bottomPanel;
  }

  /**
   * Le panneau de droite
   */
  private RightPanel rightPanel = null;

  /**
   * @return
   */
  public RightPanel getRightPanel() {
    if (this.rightPanel == null) {
      synchronized (GeoxygeneFrame.class) {
        if (this.rightPanel == null) {
          this.rightPanel = new RightPanel(this);
        }
      }
    }
    return this.rightPanel;
  }

  /**
   * Le panneau de gauche
   */
  private LeftPanel leftPanel = null;

  /**
   * @return
   */
  public LeftPanel getLeftPanel() {
    if (this.leftPanel == null) {
      synchronized (GeoxygeneFrame.class) {
        if (this.leftPanel == null) {
          this.leftPanel = new LeftPanel(this);
        }
      }
    }
    return this.leftPanel;
  }

  /**
   * L'icone
   */
  private Image icon;

  public Image getIcon() {
    if (this.icon == null) {
      this.icon = new ImageIcon(GeoxygeneFrame.class.getResource(
          "/images/icone.gif").getPath().replaceAll("%20", " ")).getImage();
    }
    return this.icon;
  }

  private ModeSelector modeSelector;

  public void setModeSelector(ModeSelector modeSelector) {
    this.modeSelector = modeSelector;
  }

  public ModeSelector getModeSelector() {
    return this.modeSelector;
  }

  /**
   * Constructeur de l'interface CartAGen
   */
  public GeoxygeneFrame() {
    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setLayout(new BorderLayout());
    this.setResizable(true);
    // setLocation(100,100);
    this.setSize(new Dimension(830, 530));
    this.setExtendedState(Frame.MAXIMIZED_BOTH);
    this.setTitle("GéOxygène");
    this.setIconImage(this.getIcon());

    JSplitPane splitPaneGauche = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        this.getLeftPanel(), this.getVisuPanel());
    splitPaneGauche.setContinuousLayout(false);
    splitPaneGauche.setOneTouchExpandable(true);
    splitPaneGauche.resetToPreferredSizes();
    splitPaneGauche.addPropertyChangeListener("dividerLocation",
        new PropertyChangeListener() {
          @Override
          public void propertyChange(PropertyChangeEvent arg0) {
            GeoxygeneFrame.this.getVisuPanel().activate();
          }
        });

    JSplitPane splitPaneCentre = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        splitPaneGauche, this.getRightPanel());
    splitPaneCentre.setContinuousLayout(false);
    splitPaneCentre.setOneTouchExpandable(true);
    splitPaneCentre.resetToPreferredSizes();
    splitPaneCentre.setResizeWeight(1.0);
    splitPaneCentre.addPropertyChangeListener("dividerLocation",
        new PropertyChangeListener() {
          @Override
          public void propertyChange(PropertyChangeEvent arg0) {
            GeoxygeneFrame.this.getVisuPanel().activate();
          }
        });

    this.add(splitPaneCentre, BorderLayout.CENTER);
    this.add(this.getBottomPanel(), BorderLayout.PAGE_END);
    this.add(this.getTopPanel(), BorderLayout.NORTH);

    this.setJMenuBar(this.getMenu());

    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }

      @Override
      public void windowActivated(WindowEvent e) {
      }
    });

    this.addComponentListener(new ComponentListener() {
      @Override
      public void componentHidden(ComponentEvent e) {
      }

      @Override
      public void componentMoved(ComponentEvent e) {
      }

      @Override
      public void componentResized(ComponentEvent e) {
        GeoxygeneFrame.this.getVisuPanel().activate();
      }

      @Override
      public void componentShown(ComponentEvent e) {
        GeoxygeneFrame.this.getVisuPanel().activate();
      }
    });
  }

}
