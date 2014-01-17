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
import java.awt.Color;
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.api.MainFrame;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanelFactory;
import fr.ign.cogit.geoxygene.appli.mode.MainFrameToolBar;
import fr.ign.cogit.geoxygene.appli.task.Task;
import fr.ign.cogit.geoxygene.appli.task.TaskManagerListener;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.appli.task.ui.TaskManagerPopup;
import fr.ign.cogit.geoxygene.appli.ui.JStatusBar;
import fr.ign.cogit.geoxygene.appli.ui.MemoryBar;
import fr.ign.cogit.geoxygene.appli.ui.Message;
import fr.ign.cogit.geoxygene.appli.ui.Message.MessageType;
import fr.ign.cogit.geoxygene.appli.ui.MessageConsole;

/**
 * Common implementation of all MainFrames
 * 
 * @author JeT
 */
public abstract class AbstractMainFrame implements MainFrame, TaskManagerListener, ChangeListener {

    /** Logger. */
    protected static Logger logger = Logger.getLogger(AbstractMainFrame.class.getName());

    /** The associated application. */
    private GeOxygeneApplication application = null;

    /** frame title. */
    private String title = null;

    /** The frame toolbar. */
    private MainFrameToolBar modeSelector = null;

    /** The frame menu bar. */
    private MainFrameMenuBar menuBar = null;
    private JStatusBar statusBar = null;

    /**
     * Tabbed Panes which contains the desktop pane containing the project
     * frames.
     */
    private JTabbedPane desktopTabbedPane = null;

    /** Main GUI window. */
    private JFrame frame = null;

    /** The default width of the frame. */
    private final int defaultFrameWidth = 800;

    /** The default height of the frame. */
    private final int defaultFrameHeight = 800;

    /** message console displayed in the status bar */
    private MessageConsole messageConsole = null;

    /** gui associated with a task manager */
    private TaskManagerPopup taskManagerGui = null;

    /** memory bar */
    private MemoryBar memoryBar = null;

    /**
     * Constructor
     * 
     * @param title
     *            frame title
     * @param application
     *            associated application
     */
    public AbstractMainFrame(final String title, final GeOxygeneApplication application) {
        super();
        this.setTitle(title);
        this.application = application;
        this.modeSelector = new MainFrameToolBar(this);
        this.application.getTaskManager().addTaskManagerListener(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.MainFrameTmp#newProjectFrame()
     */
    @Override
    synchronized public final ProjectFrame newProjectFrame() {
        return this.newProjectFrame(LayerViewPanelFactory.newLayerViewPanel());
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
            this.frame.getContentPane().add(this.getDesktopTabbedPane(), BorderLayout.CENTER);
            this.frame.getContentPane().add(this.getStatusBar().getGui(), BorderLayout.SOUTH);

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
    @Override
    public JComponent getCurrentDesktop() {
        return (JComponent) this.getDesktopTabbedPane().getSelectedComponent();
    }

    /**
     * @return the tabbed pane containing all desktops
     */
    public final JTabbedPane getDesktopTabbedPane() {
        if (this.desktopTabbedPane == null) {
            this.desktopTabbedPane = new JTabbedPane(JTabbedPane.TOP);
            this.desktopTabbedPane.addChangeListener(this);
        }
        return this.desktopTabbedPane;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.api.MainFrame#close()
     */
    @Override
    public void close() {
        this.getMemoryBar().stop();
        this.getApplication().getTaskManager().removeTaskManagerListener(this);
        this.getTaskManagerGui().stopTimer();
        this.getTaskManagerGui().close();
    }

    /**
     * Create a new desktop Floating implementation creates a JDestopPane to
     * dock
     * JInternalFrame (Gui of FloatingProjectFrame). Tabbed implementation
     * creates
     * a JTabbedPane to dock JPanel (Gui of TabbedProjectFrame). This method
     * should not be called directly. It is internally used by
     * createNewDesktop(Title)
     * 
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
    public final MainFrameToolBar getMode() {
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

    public JStatusBar getStatusBar() {
        if (this.statusBar == null) {
            this.statusBar = new JStatusBar();
            this.statusBar.addStatusBarComponent(this.getMessageConsole().getGui(), 1);
            this.statusBar.addStatusBarComponent(this.getTaskManagerGui().getGui(), 0.3);
            this.statusBar.addStatusBarComponent(this.getMemoryBar().getGui(), 0.);
            JButton gcButton = new JButton("gc");
            gcButton.setToolTipText("run garbage collector");
            gcButton.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
            gcButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Runtime.getRuntime().gc();
                }
            });
            this.statusBar.addStatusBarComponent(gcButton, 0.);
        }
        return this.statusBar;
    }

    private MessageConsole getMessageConsole() {
        if (this.messageConsole == null) {
            this.messageConsole = new MessageConsole();
        }
        return this.messageConsole;
    }

    private MemoryBar getMemoryBar() {
        if (this.memoryBar == null) {
            this.memoryBar = new MemoryBar();
        }
        return this.memoryBar;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.api.MainFrame#addMessage(fr.ign.cogit.geoxygene
     * .appli.Message.MessageType, java.lang.String)
     */
    @Override
    public void addMessage(MessageType type, String message) {
        this.getMessageConsole().addMessage(type, message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.api.MainFrame#setLookAndFeel(java.lang.String
     * )
     */
    @Override
    public boolean setLookAndFeel(final String className) {
        if (className == null) {
            return false;
        }
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this.frame);
            this.getFrame().repaint();
            this.getApplication().getProperties().setDefaultLookAndFeel(className);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Get the GUI associated with the application Task Manager
     * 
     * @return
     */
    private TaskManagerPopup getTaskManagerGui() {
        if (this.taskManagerGui == null) {
            this.taskManagerGui = new TaskManagerPopup(this.getApplication().getTaskManager());
        }
        return this.taskManagerGui;
    }

    @Override
    public final JComponent createNewDesktop(String title) {
        int index = this.getDesktopTabbedPane().getTabCount();
        JComponent newDesktop = this.createNewDesktop();

        String tabTitle = "Desktop #" + (index + 1);
        if (title != null && !title.isEmpty()) {
            tabTitle = title;
        }

        this.getDesktopTabbedPane().addTab(tabTitle, new ImageIcon(GeOxygeneApplication.class.getResource("/images/icons/tab.png")), newDesktop);
        // tabbedPane.setMnemonicAt(index, KeyEvent.KEY_LAST + 1);

        this.getDesktopTabbedPane().setTabComponentAt(index, new ButtonTabComponent(this.getDesktopTabbedPane()));
        this.getDesktopTabbedPane().setSelectedIndex(index);
        return newDesktop;
    }

    /**
     * 
     * @param desktopName
     * @param desktopContent
     */
    @Override
    public final void addFrameInDesktop(String desktopName, JComponent desktopContent) {
        logger.log(Level.DEBUG, "New frame");
        // Add ProjectFrame to the selected desktop
        JComponent currentDesktop = this.getDesktop(desktopName);
        if (currentDesktop == null) {
            logger.error("No desktop named '" + desktopName + "' found");
            return;
        }
        currentDesktop.add(desktopContent);
    }

    /**
     * get a desktop by it's name
     * 
     * @param desktopName
     * @return
     */
    public JComponent getDesktop(String desktopName) {
        for (int i = 0; i < this.getDesktopTabbedPane().getTabCount(); i++) {
            if (this.getDesktopTabbedPane().getTitleAt(i).equals(desktopName)) {
                return (JComponent) this.getDesktopTabbedPane().getComponentAt(i);
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.task.TaskManagerListener#onTaskAdded(fr.
     * ign.cogit.geoxygene.appli.task.Task)
     */
    @Override
    public void onTaskAdded(Task task) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.task.TaskManagerListener#onTaskRemoved(fr
     * .ign.cogit.geoxygene.appli.task.Task)
     */
    @Override
    public void onTaskRemoved(Task task) {
        TaskState state = task.getState();
        this.getMessageConsole().addMessage(Message.MessageType.INFO, "task " + task.getName() + " finished with state " + state.name());
        if (state == TaskState.ERROR) {
            this.getMessageConsole().addMessage(Message.MessageType.ERROR, "\t" + task.getError());
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == this.getDesktopTabbedPane()) {
            // action to perform when a new desktop is selected
        }
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
        this.setOpaque(false);

        JLabel picLabel = new JLabel(new ImageIcon(GeOxygeneApplication.class.getResource("/images/icons/tab.png").getPath()));
        this.add(picLabel);

        this.add(new JLabel("  "));

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

        this.add(label);
        // add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        // tab button
        JButton button = new TabButton();
        this.add(button);
        // add more space to the top of the component
        this.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }

    class TabButton extends JButton implements ActionListener {

        /** Default serial ID. */
        private static final long serialVersionUID = 1L;

        /**
         * Default constructor.
         */
        public TabButton() {
            super(new ImageIcon(GeOxygeneApplication.class.getResource("/images/icons/16x16/delete.png").getPath()));
            this.setToolTipText("close this tab");
            // Make the button looks the same for all Laf's
            this.setUI(new BasicButtonUI());
            // Make it transparent
            this.setContentAreaFilled(false);
            // No need to be focusable
            this.setFocusable(false);
            this.setBorder(BorderFactory.createEtchedBorder());
            this.setBorderPainted(false);
            // Making nice rollover effect
            // we use the same listener for all buttons
            this.addMouseListener(buttonMouseListener);
            this.setRolloverEnabled(true);
            // Close the proper tab by clicking the button
            this.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int i = ButtonTabComponent.this.pane.indexOfTabComponent(ButtonTabComponent.this);
            if (i != -1) {
                ButtonTabComponent.this.pane.remove(i);
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
