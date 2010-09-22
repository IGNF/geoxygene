/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterJob;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.DefaultDesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.appli.mode.ModeSelector;

/**
 * @author Julien Perret
 */
public class MainFrame extends JFrame {
    /**
     * serial uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger of the application.
     */
    private static Logger logger = Logger.getLogger(MainFrame.class.getName());

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
            setDesktopManager(new DefaultDesktopManager());
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
     * The associated application.
     */
    private GeOxygeneApplication application;

    /**
     * Get the associated application.
     *
     * @return the associated application
     */
    public final GeOxygeneApplication getApplication() {
        return this.application;
    }

    /**
     * The frame menu bar.
     */
    private JMenuBar menuBar;
    public JMenuBar getmenuBar() {
		return this.menuBar;
	}

	/**
     * The mode selector.
     */
    private ModeSelector modeSelector = null;

    /**
     * Return the current application mode.
     *
     * @return the current application mode
     */
    public final ModeSelector getMode() {
        return this.modeSelector;
    }

    /**
     * The default width of the frame.
     */
    private final int defaultFrameWidth = 800;
    /**
     * The default height of the frame.
     */
    private final int defaultFrameHeight = 800;

    public static FileChooser fc = new FileChooser();

	/**
     * Constructor using a title and an associated application.
     *
     * @param title
     *            the title of the frame
     * @param theApplication
     *            the associated application
     */
    public MainFrame(final String title,
            final GeOxygeneApplication theApplication) {
        super(title);
        this.application = theApplication;
        this.setIconImage(this.application.getIcon().getImage());
        this.setLayout(new BorderLayout());
        this.setResizable(true);
        this.setSize(this.defaultFrameWidth, this.defaultFrameHeight);
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        this.menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu(I18N
                .getString("MainFrame.File")); //$NON-NLS-1$
        JMenu viewMenu = new JMenu(I18N
                .getString("MainFrame.View")); //$NON-NLS-1$
        JMenu configurationMenu = new JMenu(I18N
                .getString("MainFrame.Configuration")); //$NON-NLS-1$
        JMenu helpMenu = new JMenu(I18N
                .getString("MainFrame.Help")); //$NON-NLS-1$
        JMenuItem openFileMenuItem = new JMenuItem(I18N
                .getString("MainFrame.OpenFile")); //$NON-NLS-1$
        openFileMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        ProjectFrame projectFrame = (ProjectFrame)
                            MainFrame.this.getDesktopPane().getSelectedFrame();
                        if (projectFrame == null) {
                            if (MainFrame.this.getDesktopPane()
                                    .getAllFrames().length != 0) {
                                // TODO ask the user in which frame (s)he
                                // wants to load into?
                                projectFrame = (ProjectFrame) MainFrame.this
                                        .getDesktopPane().getAllFrames()[0];
                            } else {
                                // TODO create a new project frame?
                                getLogger().info(I18N.getString(
                                 "MainFrame.NoFrameToLoadInto")); //$NON-NLS-1$
                                return;
                            }
                        }
                        File file = fc.getFile(MainFrame.this);
                        if (file != null) {
                            String fileName = file.getAbsolutePath();
                            String extention = fileName.substring(fileName.lastIndexOf('.') + 1);
                            if (extention.equalsIgnoreCase("shp")) { //$NON-NLS-1$
                                projectFrame.addShapefileLayer(fileName);
                                return;
                            }
                            if (extention.equalsIgnoreCase("tif")) { //$NON-NLS-1$
                                projectFrame.addGeotiffLayer(fileName);
                                return;
                            }
                            if (extention.equalsIgnoreCase("asc")) { //$NON-NLS-1$
                                projectFrame.addAscLayer(fileName);
                                return;
                            }
                        }
                    }
                });
        JMenuItem newProjectFrameMenuItem = new JMenuItem(I18N
                .getString("MainFrame.NewProject")); //$NON-NLS-1$
        newProjectFrameMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                    	MainFrame.this.newProjectFrame();
                    }
                });
        JMenuItem saveAsImageMenuItem = new JMenuItem(I18N
                .getString("MainFrame.SaveAsImage")); //$NON-NLS-1$
        saveAsImageMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        ProjectFrame projectFrame = (ProjectFrame)
                            MainFrame.this.getDesktopPane().getSelectedFrame();
                        if (projectFrame == null) {
                            if (MainFrame.this.getDesktopPane()
                                    .getAllFrames().length != 0) {
                                projectFrame = (ProjectFrame) MainFrame.this
                                        .getDesktopPane().getAllFrames()[0];
                            } else {
                                return;
                            }
                        }
                        JFileChooser chooser = new JFileChooser(fc.getPreviousDirectory());
                        int result = chooser.showSaveDialog(MainFrame.this);
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File file = chooser.getSelectedFile();
                            if (file != null) {
                                String fileName = file.getAbsolutePath();
                                projectFrame.saveAsImage(fileName);
                            }
                        }
                    }
                });
        JMenuItem printMenu = new JMenuItem(
                I18N.getString("MainFrame.Print")); //$NON-NLS-1$
        printMenu.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                Thread th=new Thread(new Runnable(){
                    public void run() {
                        try {
                            PrinterJob printJob = PrinterJob.getPrinterJob();
                            printJob.setPrintable(getSelectedProjectFrame()
                                    .getLayerViewPanel());
                            PrintRequestAttributeSet aset
                            = new HashPrintRequestAttributeSet();
                            if (printJob.printDialog(aset)) {
                                printJob.print(aset);
                            }
                        } catch (java.security.AccessControlException ace) {
                            JOptionPane.showMessageDialog(
                                    getSelectedProjectFrame()
                                    .getLayerViewPanel(),
                                    I18N.getString("MainFrame.ImpossibleToPrint") //$NON-NLS-1$
                                    + ";" //$NON-NLS-1$
                                    + I18N.getString("MainFrame.AccessControlProblem") //$NON-NLS-1$
                                    + ace.getMessage(),
                                    I18N.getString("MainFrame.ImpossibleToPrint"), //$NON-NLS-1$
                                    JOptionPane.ERROR_MESSAGE);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                th.start();
            }});
        JMenuItem exitMenuItem = new JMenuItem(I18N
                .getString("MainFrame.Exit")); //$NON-NLS-1$
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dispose();
                MainFrame.this.getApplication().exit();
            }
        });
        fileMenu.add(openFileMenuItem);
        fileMenu.add(newProjectFrameMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(saveAsImageMenuItem);
        fileMenu.add(printMenu);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        this.menuBar.setFont(this.application.getFont());
        this.menuBar.add(fileMenu);
        this.menuBar.add(viewMenu);
    	JMenuItem mScale6250=new JMenuItem("1:6250"); //$NON-NLS-1$
    	mScale6250.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LayerViewPanel layerViewPanel = MainFrame.this
                        .getSelectedProjectFrame().getLayerViewPanel();
                layerViewPanel.getViewport().setScale(
                        1 / (6250 * LayerViewPanel.getMETERS_PER_PIXEL()));
            	layerViewPanel.repaint();
            }
        });
    	JMenuItem mScale12500=new JMenuItem("1:12500"); //$NON-NLS-1$
    	mScale12500.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LayerViewPanel layerViewPanel = MainFrame.this
                        .getSelectedProjectFrame().getLayerViewPanel();
                layerViewPanel.getViewport().setScale(
                        1 / (12500 * LayerViewPanel.getMETERS_PER_PIXEL()));
            	layerViewPanel.repaint();
            }
        });
    	JMenuItem mScale25k=new JMenuItem("1:25k"); //$NON-NLS-1$
    	mScale25k.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LayerViewPanel layerViewPanel = MainFrame.this
                        .getSelectedProjectFrame().getLayerViewPanel();
                layerViewPanel.getViewport().setScale(
                        1 / (25000 * LayerViewPanel.getMETERS_PER_PIXEL()));
            	layerViewPanel.repaint();
            }
        });
    	JMenuItem mScale50k=new JMenuItem("1:50k"); //$NON-NLS-1$
    	mScale50k.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LayerViewPanel layerViewPanel = MainFrame.this
                        .getSelectedProjectFrame().getLayerViewPanel();
                layerViewPanel.getViewport().setScale(
                        1 / (50000 * LayerViewPanel.getMETERS_PER_PIXEL()));
            	layerViewPanel.repaint();
            }
        });
    	JMenuItem mScale100k=new JMenuItem("1:100k"); //$NON-NLS-1$
    	mScale100k.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LayerViewPanel layerViewPanel = MainFrame.this
                        .getSelectedProjectFrame().getLayerViewPanel();
                layerViewPanel.getViewport().setScale(
                        1 / (100000 * LayerViewPanel.getMETERS_PER_PIXEL()));
            	layerViewPanel.repaint();
            }
        });
    	viewMenu.add(mScale6250);
    	viewMenu.add(mScale12500);
    	viewMenu.add(mScale25k);
    	viewMenu.add(mScale50k);
    	viewMenu.add(mScale100k);
        this.menuBar.add(configurationMenu);
        this.menuBar.add(helpMenu);
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
        JMenuItem organizeMenuItem = new JMenuItem("Organize"); //$NON-NLS-1$
        organizeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
            	ProjectFrame[] projectFrames = MainFrame.this.getAllProjectFrames();;
            	MainFrame.this.getDesktopPane().removeAll();
            	GridLayout layout = new GridLayout(0, 2);
            	MainFrame.this.getDesktopPane().setLayout(layout);
            	for (ProjectFrame frame : projectFrames) {
                	MainFrame.this.getDesktopPane().add(frame);
            	}
            	MainFrame.this.getDesktopPane().doLayout();
            }
        });
        configurationMenu.add(organizeMenuItem);
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
                || !(this.desktopPane.getSelectedFrame()
                instanceof ProjectFrame)) {
            return null;
        }
        return (ProjectFrame) this.desktopPane.getSelectedFrame();
    }

    /**
     * Return all project frames.
     *
     * @return an array containing all project frames available in the
     *         interface
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
        ProjectFrame projectFrame = new ProjectFrame(this,
                this.application.getIcon());
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
