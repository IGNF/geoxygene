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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultDesktopManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.gui.FileChooser;
import fr.ign.cogit.geoxygene.appli.mode.ModeSelector;

/**
 * 
 * @author Julien Perret
 */
public class MainFrame extends JFrame {

    /** Default serial uid. */
    private static final long serialVersionUID = 1L;

    /** Logger of the application. */
    private static Logger LOGGER = Logger.getLogger(MainFrame.class.getName());

    /** The associated application. */
    private GeOxygeneApplication application;

    /** The frame menu bar. */
    private MainFrameMenuBar menuBar;

    /** The default width of the frame. */
    private final int defaultFrameWidth = 800;

    /** The default height of the frame. */
    private final int defaultFrameHeight = 800;

    /** The mode selector. */
    private ModeSelector modeSelector = null;

    /** Tabbed Panes which contains The desktop pane containing the project frames. */
    private JTabbedPane tabbedPane;

    /**
     * Return the desktop pane containing the project frames.
     * 
     * @return the desktop pane containing the project frames
     */
    public final JDesktopPane getDesktopPane() {
        // return this.desktopPane;
        return (JDesktopPane) this.tabbedPane.getSelectedComponent();
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
        return MainFrameMenuBar.fc;
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
        this.menuBar = new MainFrameMenuBar(this);

        tabbedPane = new JTabbedPane();
        
        this.setJMenuBar(this.menuBar);
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                MainFrame.this.getApplication().exit();
            }
        });
        this.modeSelector = new ModeSelector(this);
        
        JDesktopPane desktop0 = new JDesktopPane();
        desktop0.setDesktopManager(new DefaultDesktopManager());
        desktop0.addContainerListener(modeSelector);
        tabbedPane.addTab("Tab 1", new ImageIcon(
                GeOxygeneApplication.class.getResource("/images/icons/shape_group.png")), desktop0);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_0);
        
        tabbedPane.setTabComponentAt(0,
                new ButtonTabComponent(tabbedPane));
    }

    @Override
    public final void dispose() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            JDesktopPane desktop = (JDesktopPane) tabbedPane.getComponentAt(i);
            for (JInternalFrame frame : desktop.getAllFrames()) {
                frame.dispose();
            }
        }
        super.dispose();
    }

    /**
     * Return the selected (current) project frame.
     * 
     * @return the selected (current) project frame
     */
    public final ProjectFrame getSelectedProjectFrame() {
        if (this.tabbedPane == null || this.tabbedPane.getTabCount() < 1) {
            return null;
        } else {
            int tabSelected = this.tabbedPane.getSelectedIndex();
            JDesktopPane desktop = (JDesktopPane) this.tabbedPane.getComponentAt(tabSelected);
            if (desktop.getSelectedFrame() == null || !(desktop.getSelectedFrame() instanceof ProjectFrame)) {
                return null;
            }
            return (ProjectFrame) desktop.getSelectedFrame();
        }
    }

    /**
     * Return all project frames.
     * 
     * @return an array containing all project frames available in the interface
     */
    public final ProjectFrame[] getAllProjectFrames() {
        List<ProjectFrame> projectFrameList = new ArrayList<ProjectFrame>();
        if (this.tabbedPane == null || this.tabbedPane.getTabCount() < 1) {
            return null;
        } 
        
        int tabSelected = this.tabbedPane.getSelectedIndex();
        JDesktopPane desktop = (JDesktopPane) this.tabbedPane.getComponentAt(tabSelected);
        for (JInternalFrame frame : desktop.getAllFrames()) {
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
        LOGGER.log(Level.DEBUG, "New project frame");
        // Add ProjectFrame to the selected tabbedPane
        int tabSelected = this.tabbedPane.getSelectedIndex();
        LOGGER.debug("Tab selected index = " + this.tabbedPane.getSelectedIndex());
        JDesktopPane currentDesktop = ((JDesktopPane) this.tabbedPane.getComponentAt(tabSelected));
        
        ProjectFrame projectFrame = new ProjectFrame(this, new ImageIcon(
                GeOxygeneApplication.class.getResource("/images/icons/application.png")));
        currentDesktop.add(projectFrame);
        try {
            projectFrame.setSize((int)this.getSize().getWidth() / 2, (int)this.getSize().getHeight() / 2);
            projectFrame.setVisible(true);
            projectFrame.setSelected(true);
            projectFrame.setToolTipText(projectFrame.getTitle());
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        
        return projectFrame;
    }
    
    
    public final void newDesktopFrame(String title) {
        LOGGER.log(Level.TRACE, "New desktop");
        int index = tabbedPane.getTabCount();
        LOGGER.log(Level.DEBUG, "New desktop " + index);
        JDesktopPane newDesktopPane = new JDesktopPane();
        newDesktopPane.setDesktopManager(new DefaultDesktopManager());
        newDesktopPane.addContainerListener(modeSelector);
        String tabTitle = "Tab " + (index + 1);
        if (title != null && !title.isEmpty()) {
            tabTitle = title;
        }
        
        tabbedPane.addTab(tabTitle, new ImageIcon(
                GeOxygeneApplication.class.getResource("/images/icons/tab.png")), newDesktopPane);
        // tabbedPane.setMnemonicAt(index, KeyEvent.KEY_LAST + 1);
        tabbedPane.setSelectedIndex(index);
        
        tabbedPane.setTabComponentAt(index,
                new ButtonTabComponent(tabbedPane));
    }
    

    /**
     * 
     * @param desktopName
     * @param frame
     */
    public final void addFrameInDesktop(String desktopName, JInternalFrame frame) {
        LOGGER.log(Level.DEBUG, "New frame");
        // Add ProjectFrame to the selected tabbedPane
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(desktopName)) {
                JDesktopPane currentDesktop = ((JDesktopPane) this.tabbedPane.getComponentAt(i));
                currentDesktop.add(frame);
            }
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
        //unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        this.pane = pane;
        setOpaque(false);
        
        
        
        JLabel picLabel = new JLabel(new ImageIcon(
                GeOxygeneApplication.class.getResource("/images/icons/tab.png").getPath()));
        add(picLabel);
        
        add(new JLabel ("  "));
         
        //make JLabel read titles from JTabbedPane
        JLabel label = new JLabel() {
            public String getText() {
                int i = pane.indexOfTabComponent(ButtonTabComponent.this);
                if (i != -1) {
                    return pane.getTitleAt(i);
                }
                return null;
            }
        };
         
        add(label);
        //add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        //tab button
        JButton button = new TabButton();
        add(button);
        //add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }
 
    
    
    class TabButton extends JButton implements ActionListener {
        
        /** Default serial ID. */
        private static final long serialVersionUID = 1L;

        /**
         * Default constructor.
         */
        public TabButton() {
            super(new ImageIcon(GeOxygeneApplication.class.getResource("/images/icons/16x16/delete.png").getPath()));
            setToolTipText("close this tab");
            // Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            // Make it transparent
            setContentAreaFilled(false);
            // No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }
 
        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(ButtonTabComponent.this);
            if (i != -1) {
                pane.remove(i);
            }
        }
 
        //we don't want to update UI for this button
        public void updateUI() {
        }
 
    }
 
    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }
 
        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
}
