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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.Set;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.api.MainFrame;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.event.CoordPaintListener;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanelFactory;
import fr.ign.cogit.geoxygene.appli.panel.AddPostgisLayer;
import fr.ign.cogit.geoxygene.appli.panel.FileChooser;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * File, view, configuration and help menu in GeOxygene main frame.
 * 
 */
public class MainFrameMenuBar extends JMenuBar {

    private final MainFrame mainFrame;

    public static FileChooser fc = new FileChooser();

    /** Logger of the application. */
    private static Logger LOGGER = Logger.getLogger(MainFrameMenuBar.class
            .getName());

    /** serial version uid. */
    private static final long serialVersionUID = -6860364246334166387L;

    /**
     * Constructor.
     * 
     * @param frame
     */
    public MainFrameMenuBar(MainFrame frame) {

        super();

        this.mainFrame = frame;
        this.setFont(this.mainFrame.getApplication().getFont());

        // Init all menus
        this.initFileMenu();
        this.initViewMenu();
        this.initConfigurationMenu();
        this.initHelpMenu();
    }

    /**
     * 
     */
    private void initViewMenu() {
        JMenu viewMenu = new JMenu(I18N.getString("MainFrame.View")); //$NON-NLS-1$

        JMenuItem mScale6250 = new JMenuItem("1:6250"); //$NON-NLS-1$
        mScale6250.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LayerViewPanel layerViewPanel = MainFrameMenuBar.this.mainFrame
                        .getSelectedProjectFrame().getLayerViewPanel();
                layerViewPanel.getViewport().setScale(
                        1 / (6250 * LayerViewPanel.getMETERS_PER_PIXEL()));
                layerViewPanel.repaint();
            }
        });
        JMenuItem mScale12500 = new JMenuItem("1:12500"); //$NON-NLS-1$
        mScale12500.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LayerViewPanel layerViewPanel = MainFrameMenuBar.this.mainFrame
                        .getSelectedProjectFrame().getLayerViewPanel();
                layerViewPanel.getViewport().setScale(
                        1 / (12500 * LayerViewPanel.getMETERS_PER_PIXEL()));
                layerViewPanel.repaint();
            }
        });
        JMenuItem mScale25k = new JMenuItem("1:25k"); //$NON-NLS-1$
        mScale25k.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LayerViewPanel layerViewPanel = MainFrameMenuBar.this.mainFrame
                        .getSelectedProjectFrame().getLayerViewPanel();
                layerViewPanel.getViewport().setScale(
                        1 / (25000 * LayerViewPanel.getMETERS_PER_PIXEL()));
                layerViewPanel.repaint();
            }
        });
        JMenuItem mScale50k = new JMenuItem("1:50k"); //$NON-NLS-1$
        mScale50k.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LayerViewPanel layerViewPanel = MainFrameMenuBar.this.mainFrame
                        .getSelectedProjectFrame().getLayerViewPanel();
                layerViewPanel.getViewport().setScale(
                        1 / (50000 * LayerViewPanel.getMETERS_PER_PIXEL()));
                layerViewPanel.repaint();
            }
        });
        JMenuItem mScale100k = new JMenuItem("1:100k"); //$NON-NLS-1$
        mScale100k.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LayerViewPanel layerViewPanel = MainFrameMenuBar.this.mainFrame
                        .getSelectedProjectFrame().getLayerViewPanel();
                layerViewPanel.getViewport().setScale(
                        1 / (100000 * LayerViewPanel.getMETERS_PER_PIXEL()));
                layerViewPanel.repaint();
            }
        });
        JMenuItem mScaleCustom = new JMenuItem(
                I18N.getString("MainFrame.CustomScale")); //$NON-NLS-1$
        mScaleCustom.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                int scale = Integer.parseInt(JOptionPane.showInputDialog(I18N
                        .getString("MainFrame.NewScale"))); //$NON-NLS-1$
                LayerViewPanel layerViewPanel = MainFrameMenuBar.this.mainFrame
                        .getSelectedProjectFrame().getLayerViewPanel();
                layerViewPanel.getViewport().setScale(
                        1 / (scale * LayerViewPanel.getMETERS_PER_PIXEL()));
                layerViewPanel.repaint();
            }
        });
        viewMenu.add(mScale6250);
        viewMenu.add(mScale12500);
        viewMenu.add(mScale25k);
        viewMenu.add(mScale50k);
        viewMenu.add(mScale100k);
        viewMenu.add(mScaleCustom);
        viewMenu.addSeparator();

        JMenuItem mGoTo = new JMenuItem(I18N.getString("MainFrame.GoTo")); //$NON-NLS-1$
        mGoTo.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LayerViewPanel layerViewPanel = MainFrameMenuBar.this.mainFrame
                        .getSelectedProjectFrame().getLayerViewPanel();

                String lat = JOptionPane.showInputDialog("Latitude"); //$NON-NLS-1$
                if (lat == null) {
                    return;
                }
                double latitude = Double.parseDouble(lat);
                String lon = JOptionPane.showInputDialog("Longitude"); //$NON-NLS-1$
                if (lon == null) {
                    return;
                }
                double longitude = Double.parseDouble(lon);
                try {
                    layerViewPanel.getViewport().center(
                            new DirectPosition(latitude, longitude));
                } catch (NoninvertibleTransformException e1) {
                    e1.printStackTrace();
                }
                layerViewPanel.repaint();
            }
        });
        viewMenu.add(mGoTo);

        JMenuItem mCoord = new JCheckBoxMenuItem(
                I18N.getString("MainFrame.Coordinate")); //$NON-NLS-1$
        mCoord.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LayerViewPanel layerViewPanel = MainFrameMenuBar.this.mainFrame
                        .getSelectedProjectFrame().getLayerViewPanel();
                if (((JCheckBoxMenuItem) e.getSource()).isSelected()) {
                    layerViewPanel
                            .addMouseMotionListener(new CoordPaintListener());
                } else {
                    for (MouseMotionListener m : layerViewPanel
                            .getMouseMotionListeners()) {
                        if (m.getClass().equals(CoordPaintListener.class)) {
                            layerViewPanel.removeMouseMotionListener(m);
                            layerViewPanel.repaint();
                        }
                    }
                }
            }
        });
        viewMenu.add(mCoord);

        this.add(viewMenu);
    }

    private void initHelpMenu() {
        JMenu helpMenu = new JMenu(I18N.getString("MainFrame.Help")); //$NON-NLS-1$

        // ...

        this.add(helpMenu);
    }

    private void initConfigurationMenu() {

        JMenu configurationMenu = new JMenu(
                I18N.getString("MainFrame.Configuration")); //$NON-NLS-1$

        JMenuItem organizeMenuItem = new JMenuItem("Organize"); //$NON-NLS-1$
        organizeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MainFrameMenuBar.this.mainFrame.organizeCurrentDesktop();
            }
        });
        configurationMenu.add(organizeMenuItem);

        configurationMenu.addSeparator();

        // add a Look and feel submenu
        JMenu lafMenu = new JMenu(I18N.getString("MainFrame.LookAndFeel")); //$NON-NLS-1$

        // list all available look and feel
        for (final UIManager.LookAndFeelInfo lafInfo : UIManager
                .getInstalledLookAndFeels()) {

            JMenuItem lafMenuItem = new JMenuItem(lafInfo.getName()); //$NON-NLS-1$
            lafMenuItem.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    MainFrameMenuBar.this.mainFrame.setLookAndFeel(lafInfo
                            .getClassName());
                }
            });
            lafMenu.add(lafMenuItem);

        }
        configurationMenu.add(lafMenu);

        this.add(configurationMenu);

    }

    /**
     * File menu : .
     */
    private void initFileMenu() {

        JMenu fileMenu = new JMenu(I18N.getString("MainFrame.File")); //$NON-NLS-1$

        // New Desktop
        JMenuItem newDesktopFrameMenuItem = new JMenuItem(
                I18N.getString("MainFrame.NewDesktop"), //$NON-NLS-1$
                new ImageIcon(
                        GeOxygeneApplication.class
                                .getResource("/images/icons/tab_add.png")));
        newDesktopFrameMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        MainFrameMenuBar.this.mainFrame.createNewDesktop(null);
                    }
                });
        fileMenu.add(newDesktopFrameMenuItem);

        // New Project
        JMenu newProjectFrameSubMenu = new JMenu(
                I18N.getString("MainFrame.NewProject"));

        // New Default project
        JMenuItem newDefaultProjectFrameMenuItem = new JMenuItem(
                I18N.getString("MainFrame.NewDefaultProject"), //$NON-NLS-1$
                new ImageIcon(GeOxygeneApplication.class
                        .getResource("/images/icons/application_add.png")));
        newDefaultProjectFrameMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        // LayerViewPanelFactory.setRenderingType(RenderingType.AWT);
                        MainFrameMenuBar.this.mainFrame.newProjectFrame();
                    }
                });
        newProjectFrameSubMenu.add(newDefaultProjectFrameMenuItem);

        // New AWT project
        JMenuItem newAWTProjectFrameMenuItem = new JMenuItem(
                I18N.getString("MainFrame.NewAWTProject"), //$NON-NLS-1$
                new ImageIcon(GeOxygeneApplication.class
                        .getResource("/images/icons/application_add.png")));
        newAWTProjectFrameMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        // LayerViewPanelFactory.setRenderingType(RenderingType.AWT);
                        MainFrameMenuBar.this.mainFrame
                                .newProjectFrame(LayerViewPanelFactory
                                        .newLayerViewAwtPanel());
                    }
                });
        newProjectFrameSubMenu.add(newAWTProjectFrameMenuItem);

        // New AWT project
        JMenuItem newGLProjectFrameMenuItem = new JMenuItem(
                I18N.getString("MainFrame.NewGLProject"), //$NON-NLS-1$
                new ImageIcon(GeOxygeneApplication.class
                        .getResource("/images/icons/application_add.png")));
        newGLProjectFrameMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        // LayerViewPanelFactory.setRenderingType(RenderingType.AWT);
                        MainFrameMenuBar.this.mainFrame
                                .newProjectFrame(LayerViewPanelFactory
                                        .newLayerViewGLPanel());
                    }
                });
        newProjectFrameSubMenu.add(newGLProjectFrameMenuItem);

        fileMenu.add(newProjectFrameSubMenu);

        // Open File
        JMenuItem openFileMenuItem = new JMenuItem(
                I18N.getString("MainFrame.OpenFile"), //$NON-NLS-1$
                new ImageIcon(GeOxygeneApplication.class
                        .getResource("/images/icons/16x16/page_white_add.png")));

        openFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ProjectFrame projectFrame = MainFrameMenuBar.this.mainFrame
                        .getSelectedProjectFrame();
                if (projectFrame != null) {
                    projectFrame.askAndAddNewLayer();
                }
            }
        });
        fileMenu.add(openFileMenuItem);

        /** SCH */
        JMenuItem loadSLDMenuItem = new JMenuItem(
                I18N.getString("MainFrame.LoadSLD"), //$NON-NLS-1$
                new ImageIcon(
                        GeOxygeneApplication.class
                                .getResource("/images/toolbar/page_white_paintbrush.png")));
        loadSLDMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ProjectFrame projectFrame = MainFrameMenuBar.this.mainFrame
                        .getSelectedProjectFrame();
                if (projectFrame == null) {
                    LOGGER.info("Cannot save SLD, no selected project");
                    return;
                }
                JFileChooser chooser = new JFileChooser(fc
                        .getPreviousDirectory());
                chooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return (f.isFile()
                                && (f.getAbsolutePath().endsWith(".xml") || f
                                        .getAbsolutePath().endsWith(".XML")) || f
                                .isDirectory());
                    }

                    @Override
                    public String getDescription() {
                        return "XMLfileReader";
                    }
                });
                int result = chooser
                        .showOpenDialog(MainFrameMenuBar.this.mainFrame
                                .getGui());
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    if (file != null) {
                        // String fileName = file.getAbsolutePath();
                        try {
                            projectFrame.loadSLD(file);
                            MainFrameMenuBar.this.mainFrame.getApplication()
                                    .getProperties()
                                    .setLastOpenedFile(file.getAbsolutePath());
                            fc.setPreviousDirectory(file);
                            projectFrame.getLayerViewPanel().reset();
                            projectFrame.getLayerViewPanel().repaint();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        fileMenu.add(loadSLDMenuItem);

        // New Connection
        JMenuItem newPgLayerMenuItem = new JMenuItem(
                I18N.getString("MainFrame.NewPgLayer"), //$NON-NLS-1$
                new ImageIcon(GeOxygeneApplication.class
                        .getResource("/images/toolbar/database_add.png")));
        newPgLayerMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        ProjectFrame projectFrame = MainFrameMenuBar.this.mainFrame
                                .getSelectedProjectFrame();
                        if (projectFrame == null) {
                            if (MainFrameMenuBar.this.mainFrame
                                    .getDesktopProjectFrames().length != 0) {
                                // TODO ask the user in which frame (s)he
                                // wants to load into?
                                projectFrame = MainFrameMenuBar.this.mainFrame
                                        .getDesktopProjectFrames()[0];
                            } else {
                                // TODO create a new project frame?
                                LOGGER.info(I18N
                                        .getString("MainFrame.NoFrameToLoadInto")); //$NON-NLS-1$
                                return;
                            }
                        }
                        AddPostgisLayer addPostgisLayerPanel = new AddPostgisLayer(
                                projectFrame.getLayerLegendPanel());
                        addPostgisLayerPanel.setSize(600, 500);
                    }
                });
        fileMenu.add(newPgLayerMenuItem);

        // separator
        fileMenu.addSeparator();

        // Save as Shp
        JMenuItem saveAsShpMenuItem = new JMenuItem(
                I18N.getString("MainFrame.SaveAsShp"), //$NON-NLS-1$
                new ImageIcon(
                        GeOxygeneApplication.class
                                .getResource("/images/icons/disk.png")));
        saveAsShpMenuItem
                .addActionListener(new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        MainFrameMenuBar.this.saveAsShp();
                    }
                });
        fileMenu.add(saveAsShpMenuItem);

        //
        JMenuItem saveAsImageMenuItem = new JMenuItem(
                I18N.getString("MainFrame.SaveAsImage"), //$NON-NLS-1$
                new ImageIcon(
                        GeOxygeneApplication.class
                                .getResource("/images/icons/image.png")));
        saveAsImageMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        ProjectFrame projectFrame = MainFrameMenuBar.this.mainFrame
                                .getSelectedProjectFrame();
                        if (projectFrame == null) {
                            if (MainFrameMenuBar.this.mainFrame
                                    .getDesktopProjectFrames().length != 0) {
                                projectFrame = MainFrameMenuBar.this.mainFrame
                                        .getDesktopProjectFrames()[0];
                            } else {
                                return;
                            }
                        }
                        JFileChooser chooser = new JFileChooser(fc
                                .getPreviousDirectory());
                        int result = chooser
                                .showSaveDialog(MainFrameMenuBar.this.mainFrame
                                        .getGui());
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File file = chooser.getSelectedFile();
                            if (file != null) {
                                String fileName = file.getAbsolutePath();
                                projectFrame.saveAsImage(fileName);
                            }
                        }
                    }
                });
        fileMenu.add(saveAsImageMenuItem);

        /** SCH */
        JMenuItem saveAsSLDMenuItem = new JMenuItem(
                I18N.getString("MainFrame.SaveAsSLD")); //$NON-NLS-1$
        saveAsSLDMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        ProjectFrame projectFrame = MainFrameMenuBar.this.mainFrame
                                .getSelectedProjectFrame();
                        if (projectFrame == null) {
                            LOGGER.info("Cannot save SLD, no selected project");
                            return;
                        }
                        JFileChooser chooser = new JFileChooser(
                                MainFrameMenuBar.this.mainFrame
                                        .getApplication().getProperties()
                                        .getLastOpenedFile());
                        int result = chooser
                                .showSaveDialog(MainFrameMenuBar.this.mainFrame
                                        .getGui());
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File file = chooser.getSelectedFile();
                            if (file != null) {
                                String fileName = file.getAbsolutePath();
                                projectFrame.saveAsSLD(fileName);
                                MainFrameMenuBar.this.mainFrame
                                        .getApplication().getProperties()
                                        .setLastOpenedFile(fileName);
                                fc.setPreviousDirectory(file);
                            }
                        }
                    }
                });
        fileMenu.add(saveAsSLDMenuItem);

        JMenuItem printMenu = new JMenuItem(I18N.getString("MainFrame.Print"), //$NON-NLS-1$
                new ImageIcon(
                        GeOxygeneApplication.class
                                .getResource("/images/icons/printer.png")));
        printMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PrinterJob printJob = PrinterJob.getPrinterJob();
                            printJob.setPrintable(MainFrameMenuBar.this.mainFrame
                                    .getSelectedProjectFrame()
                                    .getLayerViewPanel());
                            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
                            if (printJob.printDialog(aset)) {
                                printJob.print(aset);
                            }
                        } catch (java.security.AccessControlException ace) {
                            JOptionPane.showMessageDialog(
                                    MainFrameMenuBar.this.mainFrame
                                            .getSelectedProjectFrame()
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
            }
        });
        fileMenu.add(printMenu);

        // Separator
        fileMenu.addSeparator();

        // Exit
        JMenuItem exitMenuItem = new JMenuItem(I18N.getString("MainFrame.Exit")); //$NON-NLS-1$
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MainFrameMenuBar.this.mainFrame.dispose();
                MainFrameMenuBar.this.mainFrame.getApplication().exit();
            }
        });
        fileMenu.add(exitMenuItem);

        this.add(fileMenu);
    }

    public void saveAsShp() {
        ProjectFrame project = MainFrameMenuBar.this.mainFrame
                .getSelectedProjectFrame();
        Set<Layer> selectedLayers = project.getLayerLegendPanel()
                .getSelectedLayers();
        if (selectedLayers.size() != 1) {
            LOGGER.error("You must select one (and only one) layer."); //$NON-NLS-1$
            return;
        }
        Layer layer = selectedLayers.iterator().next();

        IFeatureCollection<? extends IFeature> layerfeatures = layer
                .getFeatureCollection();
        if (layerfeatures == null) {
            LOGGER.error("The layer selected does not contain any feature."); //$NON-NLS-1$
            return;
        }
        JFileChooser chooser = new JFileChooser(fc.getPreviousDirectory());
        int result = chooser.showSaveDialog(MainFrameMenuBar.this.mainFrame
                .getGui());
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file != null) {
                String fileName = file.getAbsolutePath();
                project.saveAsShp(fileName, layer);
            }
        }
    }
}
