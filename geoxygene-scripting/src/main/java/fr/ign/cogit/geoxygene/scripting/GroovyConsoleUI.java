/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.scripting;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.codehaus.groovy.tools.shell.util.Preferences;

/**
 * @author JeT
 * Groovy editor panel with a toolbar and a result panel. GUI is enclosed in a JDialog
 * Some key binding are hardcoded:
 * Ctrl + s : save
 * Ctrl + Shift + s : save as
 * Ctrl + n : new file
 * Ctrl + o : open file
 */
public class GroovyConsoleUI extends AbstractScriptingConsoleUI implements ActionListener, MouseListener, WindowListener, ChangeListener {

  private static Logger logger = Logger.getLogger(GroovyConsoleUI.class.getName());

  private Window parent = null;
  // UI elements
  private JDialog mainFrame = null;
  private JPanel mainPanel = null;
  private JLabel notificationLabel = null;
  private JToolBar toolbar = null;
  private JButton saveButton = null;
  private JButton saveAsButton = null;
  private JButton applyButton = null;
  private JToggleButton sourceButton = null;
  //  private JButton bindingButton = null;
  private JButton configurationButton = null;
  private JButton runButton = null;
  private JButton openButton = null;
  private JButton newButton = null;
  private JTabbedPane mainTabbedPane = null;
  private static ImageIcon applyIcon = new ImageIcon(GroovyConsoleUI.class.getResource("/fr/irit/vortex/scripting/refresh.png"));
  private static ImageIcon sourceIcon = new ImageIcon(GroovyConsoleUI.class.getResource("/fr/irit/vortex/scripting/sources.png"));
  private static ImageIcon saveIcon = new ImageIcon(GroovyConsoleUI.class.getResource("/fr/irit/vortex/scripting/save.png"));
  private static ImageIcon saveAsIcon = new ImageIcon(GroovyConsoleUI.class.getResource("/fr/irit/vortex/scripting/saveas.png"));
  private static ImageIcon configurationIcon = new ImageIcon(GroovyConsoleUI.class.getResource("/fr/irit/vortex/scripting/variables.png"));
  private static ImageIcon runIcon = new ImageIcon(GroovyConsoleUI.class.getResource("/fr/irit/vortex/scripting/check.png"));
  private static ImageIcon openIcon = new ImageIcon(GroovyConsoleUI.class.getResource("/fr/irit/vortex/scripting/open.png"));
  private static ImageIcon newIcon = new ImageIcon(GroovyConsoleUI.class.getResource("/fr/irit/vortex/scripting/new.png"));
  private static Border emptyBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
  private Map<String, GroovyConsoleTab> tabs = new HashMap<String, GroovyConsoleTab>();
  static JFileChooser fileChooser = new JFileChooser();
  static final String LAST_DIRECTORY = "fr.ign.cogit.geoxygene.scripting.lastDirectory";

  private static final Dimension SEPARATOR_SIZE = new Dimension(25, 25);

  //  private final DisplayConfigurationAction displayConfigurationAction = new DisplayConfigurationAction();
  //  private final RunScriptAction runScriptAction = new RunScriptAction();

  /**
   * Constructor
   * @param id
   * @param groovyShell
   */
  public GroovyConsoleUI(final Window parent, final String title) {
    super(title, title);
    this.parent = parent;
    this.initGui();
  }

  /**
   * Add a new groovy shell and insert it into the console as a tab
   * @param title tab title
   * @param groovyShell groovy shell to display in the tab
   * @return the generated id
   */
  public GroovyConsoleTab addConsoleTab(final String id, final String title, final File file, final TextTransformer tt, final GroovyShell groovyShell) {
    GroovyConsoleTab tab = new GroovyConsoleTab(id, title, file, tt, this, groovyShell);
    this.tabs.put(tab.getId(), tab);
    this.getMainTabbedPane().addTab(tab.getTitle(), new CloseTabIcon(null), tab);
    this.getMainTabbedPane().setSelectedComponent(tab);
    this.fireEvent(this, ConsoleEventType.SCRIPT_OPENED);
    return tab;
  }

  /**
   * Close a new groovy shell and remove it from the console 
   * @param the tab id to close
   */
  public GroovyConsoleTab removeConsoleTab(final String id) {
    GroovyConsoleTab tab = this.tabs.get(id);
    if (tab == null) {
      return null;
    }
    this.getMainTabbedPane().remove(tab);
    this.tabs.remove(tab);
    this.fireEvent(this, ConsoleEventType.SCRIPT_CLOSE);
    return tab;

  }

  private void initGui() {
    this.getMainFrame().setSize(500, 800);
  }

  public JDialog getMainFrame() {
    if (this.mainFrame == null) {
      this.mainFrame = new JDialog(this.parent, this.getTitle());
      this.mainFrame.getContentPane().add(this.getMainPanel(), BorderLayout.CENTER);
      this.mainFrame.addWindowListener(this);
    }
    return this.mainFrame;
  }

  private JPanel getMainPanel() {
    if (this.mainPanel == null) {
      this.mainPanel = new JPanel(new BorderLayout());
      this.mainPanel.add(this.getToolbar(), BorderLayout.NORTH);
      this.mainPanel.add(this.getMainTabbedPane(), BorderLayout.CENTER);
      this.mainPanel.add(this.getNotificationLabel(), BorderLayout.SOUTH);

    }
    return this.mainPanel;
  }

  private JTabbedPane getMainTabbedPane() {
    if (this.mainTabbedPane == null) {
      this.mainTabbedPane = new JTabbedPane(SwingConstants.TOP);
      this.mainTabbedPane.addMouseListener(this);
      this.mainTabbedPane.addChangeListener(this);
    }
    return this.mainTabbedPane;
  }

  private JLabel getNotificationLabel() {
    if (this.notificationLabel == null) {
      this.notificationLabel = new JLabel("Welcome in the groovy console");
      this.notificationLabel.setBorder(BorderFactory.createLoweredBevelBorder());
    }
    return this.notificationLabel;
  }

  private JToolBar getToolbar() {
    if (this.toolbar == null) {
      this.toolbar = new JToolBar();

      this.toolbar.add(this.getSourceButton());
      this.toolbar.addSeparator(SEPARATOR_SIZE);
      this.toolbar.add(this.getNewButton());
      this.toolbar.add(this.getOpenButton());
      this.toolbar.add(this.getSaveButton());
      this.toolbar.add(this.getSaveAsButton());
      this.toolbar.addSeparator(SEPARATOR_SIZE);
      this.toolbar.add(this.getRunButton());
      this.toolbar.add(this.getApplyButton());
      //      this.toolbar.add(this.getBindingButton());
      this.toolbar.addSeparator(SEPARATOR_SIZE);
      this.toolbar.add(this.getConfigurationButton());

    }
    return this.toolbar;
  }

  private JButton getSaveAsButton() {
    if (this.saveAsButton == null) {
      this.saveAsButton = new JButton(saveAsIcon);
      this.saveAsButton.setToolTipText("save the current script in a new file");
      this.saveAsButton.setBorder(emptyBorder);
      this.saveAsButton.addActionListener(this);
    }
    return this.saveAsButton;
  }

  private JButton getSaveButton() {
    if (this.saveButton == null) {
      this.saveButton = new JButton(saveIcon);
      this.saveButton.setToolTipText("save the current script");
      this.saveButton.setBorder(emptyBorder);
      this.saveButton.addActionListener(this);
    }
    return this.saveButton;
  }

  private JButton getOpenButton() {
    if (this.openButton == null) {
      this.openButton = new JButton(openIcon);
      this.openButton.setToolTipText("open a file");
      this.openButton.setBorder(emptyBorder);
      this.openButton.addActionListener(this);
    }
    return this.openButton;
  }

  private JButton getNewButton() {
    if (this.newButton == null) {
      this.newButton = new JButton(newIcon);
      this.newButton.setToolTipText("Create a new file");
      this.newButton.setBorder(emptyBorder);
      this.newButton.addActionListener(this);
    }
    return this.newButton;
  }

  //  private JButton getBindingButton() {
  //    if (this.bindingButton == null) {
  //      this.bindingButton = new JButton("Binding");
  //      this.bindingButton.setToolTipText("Display the current binding into the result pane");
  //      this.bindingButton.addActionListener(this);
  //    }
  //    return this.bindingButton;
  //  }

  private JButton getConfigurationButton() {
    if (this.configurationButton == null) {
      this.configurationButton = new JButton(configurationIcon);
      this.configurationButton.setBorder(emptyBorder);
      this.configurationButton.setToolTipText("Display the current compiler configuration into the result pane");
      this.configurationButton.addActionListener(this);
    }
    return this.configurationButton;
  }

  private JButton getApplyButton() {
    if (this.applyButton == null) {
      this.applyButton = new JButton(applyIcon);
      this.applyButton.setBorder(emptyBorder);
      this.applyButton.setToolTipText("Send changes");
      this.applyButton.addActionListener(this);
    }
    return this.applyButton;
  }

  JToggleButton getSourceButton() {
    if (this.sourceButton == null) {
      this.sourceButton = new JToggleButton(sourceIcon);
      this.sourceButton.setSelected(true);
      this.sourceButton.setToolTipText("Toggle between source and preprocessed script");
      this.sourceButton.addActionListener(this);
    }
    return this.sourceButton;
  }

  private JButton getRunButton() {
    if (this.runButton == null) {
      this.runButton = new JButton(runIcon);
      this.runButton.setBorder(emptyBorder);
      this.runButton.setToolTipText("Compile the current script and display results into the result pane");
      this.runButton.addActionListener(this);
    }
    return this.runButton;
  }

  @Override
  public void setVisible(final boolean b) {
    this.getMainFrame().setVisible(b);
  }

  @Override
  public boolean isVisible() {
    return this.getMainFrame().isVisible();
  }

  private GroovyConsoleTab getCurrentTab() {
    return (GroovyConsoleTab) this.getMainTabbedPane().getSelectedComponent();
  }

  public String getCurrentId() {
    GroovyConsoleTab tab = (GroovyConsoleTab) this.getMainTabbedPane().getSelectedComponent();
    if (tab == null) {
      return null;
    }
    return tab.getId();
  }

  /**
   * actions performed when buttons are clicked
   * @param arg0
   */
  @Override
  public void actionPerformed(final ActionEvent event) {
    if (event.getSource() == this.getApplyButton()) {
      this.saveScript(false);
      this.fireEvent(this, ConsoleEventType.APPLY);
      this.displayNotification("Change applied");
    } else if (event.getSource() == this.getRunButton()) {
      this.runScript();
      this.fireEvent(this, ConsoleEventType.RUN);
    } else if (event.getSource() == this.getOpenButton()) {
      this.openFile();
    } else if (event.getSource() == this.getNewButton()) {
      this.newFile();
    } else if (event.getSource() == this.getSaveAsButton()) {
      this.saveScript(true);
    } else if (event.getSource() == this.getSaveButton()) {
      this.saveScript(false);
    } else if (event.getSource() == this.getConfigurationButton()) {
      this.displayConfiguration();
    } else if (event.getSource() == this.getSourceButton()) {
      this.toggleSourceView();
    } else {
      logger.warn("Unknown action " + event + " in GroovyConsoleUI");
    }
  }

  /**
   * switch between original view and preprocessed one
   */
  private void toggleSourceView() {
    GroovyConsoleTab tab = this.getCurrentTab();
    if (tab == null) {
      this.displayNotification("Invalid tab");
      return;
    }
    tab.toggleSourceView(this.getSourceButton().isSelected());
  }

  /**
   * generate an id using the current time in nanoseconds
   * @return a newly generated unique id
   */
  private String generateId() {
    return String.valueOf(new Date().getTime());
  }

  /**
   * Create a new empty file
   */
  void newFile() {
    this.addConsoleTab(this.generateId(), "new file", null, null, new GroovyShell());
    this.fireEvent(this, ConsoleEventType.NEW);
  }

  void saveScript(final boolean saveAs) {
    GroovyConsoleTab tab = this.getCurrentTab();
    if (tab == null) {
      this.displayNotification("Invalid tab");
      return;
    }
    if (!tab.saveScript(saveAs)) {
      this.displayNotification("File cannot be saved");
    } else {
      this.displayNotification("File saved");
      this.fireEvent(this, ConsoleEventType.SAVE);
    }
  }

  void runScript() {
    GroovyConsoleTab tab = this.getCurrentTab();
    if (tab == null) {
      this.displayNotification("Invalid tab");
      return;
    }
    this.saveScript(false);
    if (!tab.runScript()) {
      this.displayNotification("Script has errors");
    } else {
      this.displayNotification("Script validated");
      this.fireEvent(this, ConsoleEventType.RUN);
    }
  }

  void displayConfiguration() {
    GroovyConsoleTab tab = this.getCurrentTab();
    if (tab == null) {
      this.displayNotification("Invalid tab");
      return;
    }
    tab.displayConfiguration();
    tab.displayBinding();
    this.displayNotification("Configuration displayed");
  }

  /**
   * Open an existing file
   */
  void openFile() {
    if (Preferences.get(LAST_DIRECTORY) != null) {
      fileChooser.setCurrentDirectory(new File(Preferences.get(LAST_DIRECTORY)));
    }
    int returnVal = fileChooser.showOpenDialog(this.getMainFrame());
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File f = fileChooser.getSelectedFile();
      this.addConsoleTab(f.getName() + "-" + this.generateId(), f.getName(), f, null, new GroovyShell());
      this.fireEvent(this, ConsoleEventType.SCRIPT_OPENED);

    }
  }

  //  public boolean setScriptText(final String text) {
  //    GroovyConsoleTab currentTab = this.getCurrentTab();
  //    if (currentTab == null) {
  //      return false;
  //    }
  //    currentTab.setScriptText(text);
  //    return true;
  //
  //  }

  public boolean setBinding(final String id, final Binding binding) {
    GroovyConsoleTab tab = this.tabs.get(id);
    if (tab == null) {
      return false;
    }
    tab.setBinding(binding);
    return true;
  }

  //  public boolean setScriptText(final String id, final String text) {
  //    GroovyConsoleTab tab = this.tabs.get(id);
  //    if (tab == null) {
  //      return false;
  //    }
  //    tab.setScriptText(text);
  //    return true;
  //  }

  @Override
  public String getScriptText() {
    GroovyConsoleTab currentTab = this.getCurrentTab();
    if (currentTab == null) {
      return null;
    }
    return currentTab.getScriptText();
  }

  @Override
  public String getScriptText(final String id) {
    GroovyConsoleTab tab = this.tabs.get(id);
    if (tab == null) {
      return null;
    }
    return tab.getScriptText();
  }

  /**
   * Callback method called when a console tab has new results
   * @param id id of the tab emitter
   */
  void newResults(final String id) {
    logger.debug("Tab " + id + " has new results");
  }

  void displayNotification(final String notification) {
    this.getNotificationLabel().setText(notification);
  }

  /***************************************************************************
   * Managing closeable tabs
   */
  /**
   * The class which generates the 'X' icon for the tabs. The constructor
   * accepts an icon which is extra to the 'X' icon, so you can have tabs
   * like in JBuilder. This value is null if no extra icon is required.
   */
  class CloseTabIcon implements Icon {
    private int x_pos;
    private int y_pos;
    private int width;
    private int height;
    private Icon fileIcon;

    /**
     * CloseTabIcon
     * @param fileIcon the file icon
     */
    public CloseTabIcon(final Icon fileIcon) {
      this.fileIcon = fileIcon;
      this.width = 16;
      this.height = 16;
    }

    /**
     *  Paint icon
     * @param c component
     * @param g graphics g
     * @param x x
     * @param y y
     */
    @Override
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
      this.x_pos = x;
      this.y_pos = y;

      Color col = g.getColor();

      g.setColor(Color.black);
      int y_p = y + 2;
      g.drawLine(x + 1, y_p, x + 12, y_p);
      g.drawLine(x + 1, y_p + 13, x + 12, y_p + 13);
      g.drawLine(x, y_p + 1, x, y_p + 12);
      g.drawLine(x + 13, y_p + 1, x + 13, y_p + 12);
      g.drawLine(x + 3, y_p + 3, x + 10, y_p + 10);
      g.drawLine(x + 3, y_p + 4, x + 9, y_p + 10);
      g.drawLine(x + 4, y_p + 3, x + 10, y_p + 9);
      g.drawLine(x + 10, y_p + 3, x + 3, y_p + 10);
      g.drawLine(x + 10, y_p + 4, x + 4, y_p + 10);
      g.drawLine(x + 9, y_p + 3, x + 3, y_p + 9);
      g.setColor(col);
      if (this.fileIcon != null) {
        this.fileIcon.paintIcon(c, g, x + this.width, y_p);
      }
    }

    /**
     * get the icon width
     * @return the width
     */
    @Override
    public int getIconWidth() {
      return this.width + (this.fileIcon != null ? this.fileIcon.getIconWidth() : 0);
    }

    /**
     * get icon height
     * @return height 
     */
    @Override
    public int getIconHeight() {
      return this.height;
    }

    /**
     * get the boundaries
     * @return bounds
     */
    public Rectangle getBounds() {
      return new Rectangle(this.x_pos, this.y_pos, this.width, this.height);
    }
  }

  /**
   * What to do when mouse clicked
   * @param e <code>MouseEvent</code>
   */
  @Override
  public void mouseClicked(final MouseEvent e) {
    int nTab = this.getMainTabbedPane().getUI().tabForCoordinate(this.getMainTabbedPane(), e.getX(), e.getY());
    if (nTab < 0) {
      return;
    }
    GroovyConsoleTab tab = (GroovyConsoleTab) this.getMainTabbedPane().getComponentAt(nTab);
    Rectangle rect = ((CloseTabIcon) this.getMainTabbedPane().getIconAt(nTab)).getBounds();
    if (rect.contains(e.getX(), e.getY())) {
      this.removeConsoleTab(tab.getId());

    }
  }

  @Override
  public void mouseEntered(final MouseEvent arg0) {
    // nothing to do with this event
  }

  @Override
  public void mouseExited(final MouseEvent arg0) {
    // nothing to do with this event
  }

  @Override
  public void mousePressed(final MouseEvent arg0) {
    // nothing to do with this event
  }

  @Override
  public void mouseReleased(final MouseEvent arg0) {
    // nothing to do with this event
  }

  @Override
  public void windowActivated(final WindowEvent e) {
    // nothing to do with this event
  }

  @Override
  public void windowClosed(final WindowEvent e) {
    this.fireEvent(this, ConsoleEventType.CONSOLE_HIDE);
  }

  @Override
  public void windowClosing(final WindowEvent e) {
    this.fireEvent(this, ConsoleEventType.CONSOLE_HIDE);
  }

  @Override
  public void windowDeactivated(final WindowEvent e) {
    // nothing to do with this event
  }

  @Override
  public void windowDeiconified(final WindowEvent e) {
    // nothing to do with this event
  }

  @Override
  public void windowIconified(final WindowEvent e) {
    // nothing to do with this event
  }

  @Override
  public void windowOpened(final WindowEvent e) {
    this.fireEvent(this, ConsoleEventType.CONSOLE_SHOW);
  }

  /**
   * callback when  tab is changed
   * @param changeevent
   */
  @Override
  public void stateChanged(final ChangeEvent changeevent) {
    if (((GroovyConsoleTab) this.getMainTabbedPane().getSelectedComponent()).getFile() == null) {
      this.getApplyButton().setEnabled(false);
    } else {
      this.getApplyButton().setEnabled(true);
    }

  }

}
