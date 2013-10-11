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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.mode.ModeSelector;

/**
 * Common implementation of all MainFrames
 * @author JeT
 * 
 */
public abstract class AbstractMainFrame implements MainFrame {

  private static Logger logger = Logger.getLogger(AbstractMainFrame.class
      .getName());
  private GeOxygeneApplication application = null; // The associated application
  private String title = null; // frame title
  private ModeSelector modeSelector = null;
  /** The frame menu bar. */
  private MainFrameMenuBar menuBar = null;
  /**
   * Tabbed Panes which contains The desktop pane containing the project frames.
   */
  private JTabbedPane desktopTabbedPane = null;
  private JFrame frame = null; // main GUI window
  /** The default width of the frame. */
  private final int defaultFrameWidth = 800;

  /** The default height of the frame. */
  private final int defaultFrameHeight = 800;

  /**
   * Constructor
   * @param title frame title
   * @param application associated application
   */
  public AbstractMainFrame(final String title,
      final GeOxygeneApplication application) {
    super();
    this.setTitle(title);
    this.application = application;
    this.modeSelector = new ModeSelector(this);
  }

  /**
   * @return the main frame
   */
  public JFrame getFrame() {
    if (this.frame == null) {
      this.frame = new JFrame();
      if (this.application.getIcon() != null) {
        this.frame.setIconImage(this.application.getIcon().getImage());
      }
      this.frame.setLayout(new BorderLayout());
      this.frame.setResizable(true);
      this.frame.setSize(this.defaultFrameWidth, this.defaultFrameHeight);
      this.frame.setExtendedState(Frame.MAXIMIZED_BOTH);

      this.frame.setJMenuBar(this.getMenuBar());
      this.frame.getContentPane().setLayout(new BorderLayout());
      this.frame.getContentPane().add(this.getDesktopTabbedPane(),
          BorderLayout.CENTER);
      this.frame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(final WindowEvent e) {
          AbstractMainFrame.this.getApplication().exit();
        }
      });
    }
    return this.frame;
  }

  /**
   * get the current selected Desktop or null if none
   */
  public JComponent getCurrentDesktop() {
    return (JComponent) this.getDesktopTabbedPane().getSelectedComponent();
  }

  /**
   * @return the tabbed pane containing all desktops
   */
  public final JTabbedPane getDesktopTabbedPane() {
    if (this.desktopTabbedPane == null) {
      this.desktopTabbedPane = new JTabbedPane(JTabbedPane.TOP);
    }
    return this.desktopTabbedPane;
  }

  /**
   * Create a new desktop Floating implementation creates a JDestopPane to dock
   * JInternalFrame (Gui of FloatingProjectFrame). Tabbed implementation creates
   * a JTabbedPane to dock JPanel (Gui of TabbedProjectFrame). This method
   * should not be called directly. It is internally used by
   * createNewDesktop(Title)
   * @return newly created Desktop component
   */
  abstract JComponent createNewDesktop();

  @Override
  public final void setTitle(final String title) {
    this.title = title;
  }

  @Override
  public final GeOxygeneApplication getApplication() {
    return this.application;
  }

  @Override
  public final ModeSelector getMode() {
    return this.modeSelector;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return this.title;
  }

  @Override
  public boolean openFile(File file) {
    ProjectFrame projectFrame = this.getSelectedProjectFrame();
    if (projectFrame == null) {
      if (this.getDesktopProjectFrames().length != 0) {
        // TODO ask the user in which frame (s)he
        // wants to load into?
        projectFrame = this.getDesktopProjectFrames()[0];
      } else {
        // TODO create a new project frame?
        logger.info(I18N.getString("MainFrame.NoFrameToLoadInto")); //$NON-NLS-1$
        return false;
      }
    }
    if (file != null) {
      projectFrame.addLayer(file);
      return true;
    }
    return false;
  }

  @Override
  public MainFrameMenuBar getMenuBar() {
    if (this.menuBar == null) {
      this.menuBar = new MainFrameMenuBar(this);
    }
    return this.menuBar;
  }

  @Override
  public final JComponent createNewDesktop(String title) {
    int index = this.getDesktopTabbedPane().getTabCount();
    JComponent newDesktop = createNewDesktop();

    String tabTitle = "Desktop #" + (index + 1);
    if (title != null && !title.isEmpty()) {
      tabTitle = title;
    }

    getDesktopTabbedPane().addTab(
        tabTitle,
        new ImageIcon(GeOxygeneApplication.class
            .getResource("/images/icons/tab.png")), newDesktop);
    // tabbedPane.setMnemonicAt(index, KeyEvent.KEY_LAST + 1);
    getDesktopTabbedPane().setSelectedIndex(index);

    getDesktopTabbedPane().setTabComponentAt(index,
        new ButtonTabComponent(getDesktopTabbedPane()));
    return newDesktop;
  }

  /**
   * 
   * @param desktopName
   * @param desktopContent
   */
  @Override
  public final void addFrameInDesktop(String desktopName,
      JComponent desktopContent) {
    logger.log(Level.DEBUG, "New frame");
    // Add ProjectFrame to the selected desktop
    JComponent currentDesktop = getDesktop(desktopName);
    if (currentDesktop == null) {
      logger.error("No desktop named '" + desktopName + "' found");
      return;
    }
    currentDesktop.add(desktopContent);
  }

  /**
   * get a desktop by it's name
   * @param desktopName
   * @return
   */
  public JComponent getDesktop(String desktopName) {
    for (int i = 0; i < getDesktopTabbedPane().getTabCount(); i++) {
      if (getDesktopTabbedPane().getTitleAt(i).equals(desktopName)) {
        return (JComponent) this.getDesktopTabbedPane().getComponentAt(i);
      }
    }
    return null;
  }
}

class ButtonTabComponent extends JPanel {
  /**
     * 
     */
  private static final long serialVersionUID = 1L;
  private final JTabbedPane pane;

  public ButtonTabComponent(final JTabbedPane pane) {
    // unset default FlowLayout' gaps
    super(new FlowLayout(FlowLayout.LEFT, 0, 0));
    if (pane == null) {
      throw new NullPointerException("TabbedPane is null");
    }
    this.pane = pane;
    setOpaque(false);

    JLabel picLabel = new JLabel(new ImageIcon(GeOxygeneApplication.class
        .getResource("/images/icons/tab.png").getPath()));
    add(picLabel);

    add(new JLabel("  "));

    // make JLabel read titles from JTabbedPane
    JLabel label = new JLabel() {
      @Override
      public String getText() {
        int i = pane.indexOfTabComponent(ButtonTabComponent.this);
        if (i != -1) {
          return pane.getTitleAt(i);
        }
        return null;
      }
    };

    add(label);
    // add more space between the label and the button
    label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    // tab button
    JButton button = new TabButton();
    add(button);
    // add more space to the top of the component
    setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
  }

  class TabButton extends JButton implements ActionListener {

    /** Default serial ID. */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public TabButton() {
      super(new ImageIcon(GeOxygeneApplication.class.getResource(
          "/images/icons/16x16/delete.png").getPath()));
      setToolTipText("close this tab");
      // Make the button looks the same for all Laf's
      setUI(new BasicButtonUI());
      // Make it transparent
      setContentAreaFilled(false);
      // No need to be focusable
      setFocusable(false);
      setBorder(BorderFactory.createEtchedBorder());
      setBorderPainted(false);
      // Making nice rollover effect
      // we use the same listener for all buttons
      addMouseListener(buttonMouseListener);
      setRolloverEnabled(true);
      // Close the proper tab by clicking the button
      addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      int i = pane.indexOfTabComponent(ButtonTabComponent.this);
      if (i != -1) {
        pane.remove(i);
      }
    }

    // we don't want to update UI for this button
    @Override
    public void updateUI() {
    }

  }

  private final static MouseListener buttonMouseListener = new MouseAdapter() {
    @Override
    public void mouseEntered(MouseEvent e) {
      Component component = e.getComponent();
      if (component instanceof AbstractButton) {
        AbstractButton button = (AbstractButton) component;
        button.setBorderPainted(true);
      }
    }

    @Override
    public void mouseExited(MouseEvent e) {
      Component component = e.getComponent();
      if (component instanceof AbstractButton) {
        AbstractButton button = (AbstractButton) component;
        button.setBorderPainted(false);
      }
    }
  };

}

//      this.fileMenu = new JMenu(I18N.getString("MainFrame.File")); //$NON-NLS-1$
//      this.viewMenu = new JMenu(I18N.getString("MainFrame.View")); //$NON-NLS-1$
// this.configurationMenu = new JMenu(
//          I18N.getString("MainFrame.Configuration")); //$NON-NLS-1$
//      this.helpMenu = new JMenu(I18N.getString("MainFrame.Help")); //$NON-NLS-1$
//
// JMenuItem openFileMenuItem = new JMenuItem(
//          I18N.getString("MainFrame.OpenFile")); //$NON-NLS-1$
// openFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
// @Override
// public void actionPerformed(final ActionEvent e) {
// ProjectFrame projectFrame = AbstractMainFrame.this
// .getSelectedProjectFrame();
// if (projectFrame == null) {
// if (AbstractMainFrame.this.getAllProjectFrames().length != 0) {
// // TODO ask the user in which frame (s)he
// // wants to load into?
// projectFrame = AbstractMainFrame.this.getAllProjectFrames()[0];
// } else {
// // TODO create a new project frame?
// AbstractMainFrame.logger.info(I18N
//                  .getString("MainFrame.NoFrameToLoadInto")); //$NON-NLS-1$
// return;
// }
// }
// File file = MainFrameMenuBar.fc.getFile( AbstractMainFrame.this.getGui());
// if (file != null) {
// projectFrame.addLayer(file);
// }
// }
// });
//
// JMenuItem newProjectFrameMenuItem = new JMenuItem(
//          I18N.getString("MainFrame.NewProject")); //$NON-NLS-1$
// newProjectFrameMenuItem
// .addActionListener(new java.awt.event.ActionListener() {
// @Override
// public void actionPerformed(final ActionEvent e) {
// /**
// * popup for the visualization type
// */
// String[] visuOptions = new String[] { "GL (lwJGL)",
// "native (AWT)" };
// String input = (String) JOptionPane.showInputDialog(
// AbstractMainFrame.this.getGui(),
// "Please select a visualization engine",
// "visualization engine", JOptionPane.INFORMATION_MESSAGE,
// null, visuOptions, visuOptions[0]);
// if (input.equals(visuOptions[0])) {
// LayerViewPanelFactory.setRenderingType(RenderingType.LWJGL);
// }
//
// if (input.equals(visuOptions[2])) {
// LayerViewPanelFactory.setRenderingType(RenderingType.AWT);
// }
//
// AbstractMainFrame.this.newProjectFrame();
// }
// });
//
// JMenuItem saveAsShpMenuItem = new JMenuItem(
//          I18N.getString("MainFrame.SaveAsShp")); //$NON-NLS-1$
// saveAsShpMenuItem.addActionListener(new java.awt.event.ActionListener() {
// @Override
// public void actionPerformed(final ActionEvent e) {
// ProjectFrame projectFrame = AbstractMainFrame.this
// .getSelectedProjectFrame();
// Set<Layer> selectedLayers = projectFrame.getLayerLegendPanel()
// .getSelectedLayers();
// if (selectedLayers.size() != 1) {
// AbstractMainFrame.logger
//                .error("You must select one (and only one) layer."); //$NON-NLS-1$
// return;
// }
// Layer layer = selectedLayers.iterator().next();
//
// IFeatureCollection<? extends IFeature> layerfeatures = layer
// .getFeatureCollection();
// if (layerfeatures == null) {
// AbstractMainFrame.logger
//                .error("The layer selected does not contain any feature."); //$NON-NLS-1$
// return;
// }
// JFileChooser chooser = new
// JFileChooser(MainFrameMenuBar.fc.getPreviousDirectory());
// int result = chooser.showSaveDialog(AbstractMainFrame.this.getGui());
// if (result == JFileChooser.APPROVE_OPTION) {
// File file = chooser.getSelectedFile();
// if (file != null) {
// String fileName = file.getAbsolutePath();
// projectFrame.saveAsShp(fileName, layer);
// }
// }
// }
// });
//
// JMenuItem saveAsImageMenuItem = new JMenuItem(
//          I18N.getString("MainFrame.SaveAsImage")); //$NON-NLS-1$
// saveAsImageMenuItem
// .addActionListener(new java.awt.event.ActionListener() {
// @Override
// public void actionPerformed(final ActionEvent e) {
// ProjectFrame projectFrame = AbstractMainFrame.this
// .getSelectedProjectFrame();
// if (projectFrame == null) {
// if (AbstractMainFrame.this.getAllProjectFrames().length != 0) {
// projectFrame = AbstractMainFrame.this.getAllProjectFrames()[0];
// } else {
// return;
// }
// }
// JFileChooser chooser = new
// JFileChooser(MainFrameMenuBar.fc.getPreviousDirectory());
// int result = chooser.showSaveDialog(AbstractMainFrame.this
// .getGui());
// if (result == JFileChooser.APPROVE_OPTION) {
// File file = chooser.getSelectedFile();
// if (file != null) {
// String fileName = file.getAbsolutePath();
// projectFrame.saveAsImage(fileName);
// }
// }
// }
// });
//
//      JMenuItem printMenu = new JMenuItem(I18N.getString("MainFrame.Print")); //$NON-NLS-1$
// printMenu.addActionListener(new ActionListener() {
// @Override
// public void actionPerformed(final ActionEvent arg0) {
// Thread th = new Thread(new Runnable() {
// @Override
// public void run() {
// try {
// PrinterJob printJob = PrinterJob.getPrinterJob();
// printJob.setPrintable(AbstractMainFrame.this
// .getSelectedProjectFrame().getLayerViewPanel());
// PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
// if (printJob.printDialog(aset)) {
// printJob.print(aset);
// }
// } catch (java.security.AccessControlException ace) {
// JOptionPane.showMessageDialog(AbstractMainFrame.this
// .getSelectedProjectFrame().getLayerViewPanel(),
//                    I18N.getString("MainFrame.ImpossibleToPrint") //$NON-NLS-1$
//                        + ";" //$NON-NLS-1$
//                        + I18N.getString("MainFrame.AccessControlProblem") //$NON-NLS-1$
// + ace.getMessage(), I18N
//                        .getString("MainFrame.ImpossibleToPrint"), //$NON-NLS-1$
// JOptionPane.ERROR_MESSAGE);
// } catch (Exception ex) {
// ex.printStackTrace();
// }
// }
// });
// th.start();
// }
// });
//
//      JMenuItem exitMenuItem = new JMenuItem(I18N.getString("MainFrame.Exit")); //$NON-NLS-1$
// exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
// @Override
// public void actionPerformed(final ActionEvent e) {
// AbstractMainFrame.this.dispose();
// AbstractMainFrame.this.getApplication().exit();
// }
// });
//
// this.fileMenu.add(openFileMenuItem);
// this.fileMenu.add(newProjectFrameMenuItem);
// this.fileMenu.addSeparator();
// this.fileMenu.add(saveAsShpMenuItem);
// this.fileMenu.add(saveAsImageMenuItem);
// this.fileMenu.add(printMenu);
// this.fileMenu.addSeparator();
// this.fileMenu.add(exitMenuItem);
// this.menuBar.setFont(this.getApplication().getFont());
// this.menuBar.add(this.fileMenu);
// this.menuBar.add(this.viewMenu);
//      JMenuItem mScale6250 = new JMenuItem("1:6250"); //$NON-NLS-1$
// mScale6250.addActionListener(new java.awt.event.ActionListener() {
// @Override
// public void actionPerformed(final ActionEvent e) {
// LayerViewPanel layerViewPanel = AbstractMainFrame.this
// .getSelectedProjectFrame().getLayerViewPanel();
// layerViewPanel.getViewport().setScale(
// 1 / (6250 * LayerViewPanel.getMETERS_PER_PIXEL()));
// layerViewPanel.repaint();
// }
// });
//      JMenuItem mScale12500 = new JMenuItem("1:12500"); //$NON-NLS-1$
// mScale12500.addActionListener(new java.awt.event.ActionListener() {
// @Override
// public void actionPerformed(final ActionEvent e) {
// LayerViewPanel layerViewPanel = AbstractMainFrame.this
// .getSelectedProjectFrame().getLayerViewPanel();
// layerViewPanel.getViewport().setScale(
// 1 / (12500 * LayerViewPanel.getMETERS_PER_PIXEL()));
// layerViewPanel.repaint();
// }
// });
//      JMenuItem mScale25k = new JMenuItem("1:25k"); //$NON-NLS-1$
// mScale25k.addActionListener(new java.awt.event.ActionListener() {
// @Override
// public void actionPerformed(final ActionEvent e) {
// LayerViewPanel layerViewPanel = AbstractMainFrame.this
// .getSelectedProjectFrame().getLayerViewPanel();
// layerViewPanel.getViewport().setScale(
// 1 / (25000 * LayerViewPanel.getMETERS_PER_PIXEL()));
// layerViewPanel.repaint();
// }
// });
//      JMenuItem mScale50k = new JMenuItem("1:50k"); //$NON-NLS-1$
// mScale50k.addActionListener(new java.awt.event.ActionListener() {
// @Override
// public void actionPerformed(final ActionEvent e) {
// LayerViewPanel layerViewPanel = AbstractMainFrame.this
// .getSelectedProjectFrame().getLayerViewPanel();
// layerViewPanel.getViewport().setScale(
// 1 / (50000 * LayerViewPanel.getMETERS_PER_PIXEL()));
// layerViewPanel.repaint();
// }
// });
//      JMenuItem mScale100k = new JMenuItem("1:100k"); //$NON-NLS-1$
// mScale100k.addActionListener(new java.awt.event.ActionListener() {
// @Override
// public void actionPerformed(final ActionEvent e) {
// LayerViewPanel layerViewPanel = AbstractMainFrame.this
// .getSelectedProjectFrame().getLayerViewPanel();
// layerViewPanel.getViewport().setScale(
// 1 / (100000 * LayerViewPanel.getMETERS_PER_PIXEL()));
// layerViewPanel.repaint();
// }
// });
// JMenuItem mScaleCustom = new JMenuItem(
//          I18N.getString("MainFrame.CustomScale")); //$NON-NLS-1$
// mScaleCustom.addActionListener(new java.awt.event.ActionListener() {
// @Override
// public void actionPerformed(final ActionEvent e) {
// int scale = Integer.parseInt(JOptionPane.showInputDialog(I18N
//              .getString("MainFrame.NewScale"))); //$NON-NLS-1$
// LayerViewPanel layerViewPanel = AbstractMainFrame.this
// .getSelectedProjectFrame().getLayerViewPanel();
// layerViewPanel.getViewport().setScale(
// 1 / (scale * LayerViewPanel.getMETERS_PER_PIXEL()));
// layerViewPanel.repaint();
// }
// });
// this.viewMenu.add(mScale6250);
// this.viewMenu.add(mScale12500);
// this.viewMenu.add(mScale25k);
// this.viewMenu.add(mScale50k);
// this.viewMenu.add(mScale100k);
// this.viewMenu.add(mScaleCustom);
// this.viewMenu.addSeparator();
//
//      JMenuItem mGoTo = new JMenuItem(I18N.getString("MainFrame.GoTo")); //$NON-NLS-1$
// mGoTo.addActionListener(new java.awt.event.ActionListener() {
// @Override
// public void actionPerformed(final ActionEvent e) {
// LayerViewPanel layerViewPanel = AbstractMainFrame.this
// .getSelectedProjectFrame().getLayerViewPanel();
//
//          String lat = JOptionPane.showInputDialog("Latitude"); //$NON-NLS-1$
// if (lat == null) {
// return;
// }
// double latitude = Double.parseDouble(lat);
//          String lon = JOptionPane.showInputDialog("Longitude"); //$NON-NLS-1$
// if (lon == null) {
// return;
// }
// double longitude = Double.parseDouble(lon);
// try {
// layerViewPanel.getViewport().center(
// new DirectPosition(latitude, longitude));
// } catch (NoninvertibleTransformException e1) {
// e1.printStackTrace();
// }
// layerViewPanel.repaint();
// }
// });
// this.viewMenu.add(mGoTo);
//
// JMenuItem mCoord = new JCheckBoxMenuItem(
//          I18N.getString("MainFrame.Coordinate")); //$NON-NLS-1$
// mCoord.addActionListener(new java.awt.event.ActionListener() {
// @Override
// public void actionPerformed(final ActionEvent e) {
// LayerViewPanel layerViewPanel = AbstractMainFrame.this
// .getSelectedProjectFrame().getLayerViewPanel();
// if (((JCheckBoxMenuItem) e.getSource()).isSelected()) {
// layerViewPanel.addMouseMotionListener(new CoordPaintListener());
// } else {
// for (MouseMotionListener m : layerViewPanel
// .getMouseMotionListeners()) {
// if (m.getClass().equals(CoordPaintListener.class)) {
// layerViewPanel.removeMouseMotionListener(m);
// layerViewPanel.repaint();
// }
// }
// }
// }
// });
// this.viewMenu.add(mCoord);
//
// this.menuBar.add(this.configurationMenu);
// this.menuBar.add(this.helpMenu);
//
