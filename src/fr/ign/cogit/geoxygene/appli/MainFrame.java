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
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultDesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.appli.mode.ModeSelector;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.util.conversion.GeoTiffReader;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

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
    
    /**
     * The previous opened directory.
     */
    private File previousDirectory = new File(""); //$NON-NLS-1$
    
    /**
     * Return the previous opened directory.
     *
     * @return the previous opened directory
     */
    public File getPreviousDirectory() {
		return this.previousDirectory;
	}
    
    /**
	 * Affect the previous opened directory.
	 * @param previousDirectory the previous opened directory
	 */
	public void setPreviousDirectory(File previousDirectory) {
		this.previousDirectory = previousDirectory;
	}

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
        JMenuItem openShapefileMenuItem = new JMenuItem(I18N
                .getString("MainFrame.OpenShapefile")); //$NON-NLS-1$
        openShapefileMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
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
                        JFileChooser choixFichierShape = new JFileChooser();
                        /*
                         * crée un filtre qui n'accepte
                         * que les fichier shp ou les répertoires
                         */
                        choixFichierShape.setFileFilter(new FileFilter() {
                            @Override
                            public boolean accept(final File f) {
                                return (f.isFile()
                                        && (f.getAbsolutePath()
                                                .endsWith(".shp") //$NON-NLS-1$
                                                ||
                                                f.getAbsolutePath()
                                                .endsWith(".SHP") //$NON-NLS-1$
                                                )
                                                || f.isDirectory());
                            }
                            @Override
                            public String getDescription() {
                                return I18N.getString(
                                 "MainFrame.ShapefileDescription" //$NON-NLS-1$
                                );
                            }
                        });
                        choixFichierShape
                                .setFileSelectionMode(JFileChooser.FILES_ONLY);
                        choixFichierShape.setMultiSelectionEnabled(false);
                        choixFichierShape.setCurrentDirectory(
                                getPreviousDirectory());
                        JFrame frame = new JFrame();
                        frame.setVisible(true);
                        int returnVal = choixFichierShape.showOpenDialog(frame);
                        frame.dispose();
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            if (getLogger().isDebugEnabled()) {
                                getLogger().debug(I18N.getString(
                                    "MainFrame.FileChosenDebug" //$NON-NLS-1$
                                        ) + choixFichierShape
                                        .getSelectedFile()
                                        .getAbsolutePath());
                            }
                            setPreviousDirectory(
                            		new File(choixFichierShape
                            				.getSelectedFile()
                            				.getAbsolutePath()));
                            String shapefileName = choixFichierShape
                                    .getSelectedFile().getAbsolutePath();
                            String populationName = shapefileName
                                    .substring(
                                            shapefileName.
                                            lastIndexOf("/") + 1, //$NON-NLS-1$
                                            shapefileName.
                                            lastIndexOf(".")); //$NON-NLS-1$
                            ShapefileReader shapefileReader =
                                new ShapefileReader(
                                    shapefileName, populationName, DataSet
                                            .getInstance(), true);

                            Population<DefaultFeature> population =
                                shapefileReader.getPopulation();
                            if (population != null) {
                                getLogger().info(I18N.getString(
                                   "MainFrame.LoadingPopulation") //$NON-NLS-1$
                                                        + population.getNom());
                                projectFrame.addFeatureCollection(population,
                                        population.getNom());
                            }
                            shapefileReader.read();
                            if (projectFrame.getLayers().size() == 1) {
                                try {
                                    projectFrame.getLayerViewPanel()
                                    .getViewport().zoom(
                                            new GM_Envelope(
                                                    shapefileReader.getMinX(),
                                                    shapefileReader.getMaxX(),
                                                    shapefileReader.getMinY(),
                                                    shapefileReader.getMaxY()));
                                } catch (NoninvertibleTransformException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                });
        JMenuItem openGeoTiffMenuItem = new JMenuItem("Open GeoTiff Image"); //$NON-NLS-1$
        openGeoTiffMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
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
                        JFileChooser choixFichierGeoTiff = new JFileChooser();
                        /*
                         * crée un filtre qui n'accepte
                         * que les fichier GeoTiff ou les répertoires
                         */
                        choixFichierGeoTiff.setFileFilter(new FileFilter() {
                            @Override
                            public boolean accept(final File f) {
                                return (f.isFile()
                                        && (f.getAbsolutePath()
                                                .endsWith(".tif") //$NON-NLS-1$
                                                ||
                                                f.getAbsolutePath()
                                                .endsWith(".TIF") //$NON-NLS-1$
                                                )
                                                || f.isDirectory());
                            }
                            @Override
                            public String getDescription() {
                                return "GeoTiff Image"; //$NON-NLS-1$
                            }
                        });
                        choixFichierGeoTiff
                                .setFileSelectionMode(JFileChooser.FILES_ONLY);
                        choixFichierGeoTiff.setMultiSelectionEnabled(false);
                        choixFichierGeoTiff.setCurrentDirectory(
                                getPreviousDirectory());
                        JFrame frame = new JFrame();
                        frame.setVisible(true);
                        int returnVal = choixFichierGeoTiff.showOpenDialog(frame);
                        frame.dispose();
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            if (getLogger().isDebugEnabled()) {
                                getLogger().debug(I18N.getString(
                                    "MainFrame.FileChosenDebug" //$NON-NLS-1$
                                        ) + choixFichierGeoTiff
                                        .getSelectedFile()
                                        .getAbsolutePath());
                            }
                            setPreviousDirectory(
                            		new File(choixFichierGeoTiff
                            				.getSelectedFile()
                            				.getAbsolutePath()));
                            String shapefileName = choixFichierGeoTiff
                                    .getSelectedFile().getAbsolutePath();
                            String populationName = shapefileName
                                    .substring(
                                            shapefileName.
                                            lastIndexOf("/") + 1, //$NON-NLS-1$
                                            shapefileName.
                                            lastIndexOf(".")); //$NON-NLS-1$
                            double[][] range = new double[2][2];
                            BufferedImage image =
                                GeoTiffReader.loadGeoTiffImage(shapefileName, range);
                            projectFrame.addImage(populationName, image, range);
                            if (projectFrame.getLayers().size() == 1) {
                                try {
                                    projectFrame.getLayerViewPanel()
                                    .getViewport().zoom(
                                            new GM_Envelope(
                                                    range[0][0],
                                                    range[0][1],
                                                    range[1][0],
                                                    range[1][1]));
                                } catch (NoninvertibleTransformException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                });
        JMenuItem exitMenuItem = new JMenuItem(I18N
                .getString("MainFrame.Exit")); //$NON-NLS-1$
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                dispose();
                MainFrame.this.getApplication().exit();
            }
        });
        fileMenu.add(openShapefileMenuItem);
        fileMenu.add(openGeoTiffMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        this.menuBar.setFont(this.application.getFont());
        this.menuBar.add(fileMenu);
        this.menuBar.add(viewMenu);
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
        this.desktopPane.setSelectedFrame(projectFrame);
        return projectFrame;
    }
}
