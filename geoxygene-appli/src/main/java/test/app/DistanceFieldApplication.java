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
package test.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.layer.LayerFactory;
import fr.ign.cogit.geoxygene.appli.layer.LayerFactory.LayerType;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.gl.TextureImage.TexturePixel;

/**
 * @author JeT
 * 
 */
public class DistanceFieldApplication {
    private JFrame frame = null;

    private static final Logger logger = Logger.getLogger(DistanceFieldApplication.class.getName()); // logger
    public final StyledLayerDescriptor sld = new StyledLayerDescriptor();
    private DisplayPanel displayPanel = null;
    private final JTextArea stepTextField = new JTextArea(20, 20);
    private final JTextArea pixelLabel = new JTextArea(20, 20);
    private final JLabel uMinLabel = new JLabel();
    private final JLabel uMaxLabel = new JLabel();
    private final JLabel vMinLabel = new JLabel();
    private final JLabel vMaxLabel = new JLabel();
    private final JLabel dMinLabel = new JLabel();
    private final JLabel dMaxLabel = new JLabel();
    private JTextField uSlider = null;
    private JTextField vSlider = null;
    private String method = null;
    private final Preferences prefs = Preferences.userRoot().node(DistanceFieldApplication.class.getName());

    //    private final DataSet dataset = new DataSet();

    /**
     * Constructor
     */
    public DistanceFieldApplication() {
        this.frame = new JFrame("Distance Field Application");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.PAGE_AXIS));
        toolPanel.setPreferredSize(new Dimension(300, 600));
        this.displayPanel = new DisplayPanel(this);
        this.displayPanel.setPreferredSize(new Dimension(1000, 800));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        JButton stepButton = new JButton("step");
        stepButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final String stepText = DistanceFieldApplication.this.doStep();
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        DistanceFieldApplication.this.stepTextField.insert(stepText + "\n", 0);
                        DistanceFieldApplication.this.stepTextField.setCaretPosition(0);
                        DistanceFieldApplication.this.displayPanel.repaint();
                        DistanceFieldApplication.this.updateContent();
                    }
                });
            }
        });
        buttonPanel.add(stepButton);
        JButton runButton = new JButton("run");
        runButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                while (!DistanceFieldApplication.this.displayPanel.hasNoStepLeft) {
                    DistanceFieldApplication.this.updateContent();
                    final String stepText = DistanceFieldApplication.this.doStep();
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            DistanceFieldApplication.this.stepTextField.insert(stepText + "\n", 0);
                            DistanceFieldApplication.this.stepTextField.setCaretPosition(0);
                            DistanceFieldApplication.this.displayPanel.repaint();
                            DistanceFieldApplication.this.updateContent();
                        }
                    });
                }
            }
        });
        buttonPanel.add(runButton);
        JButton resetButton = new JButton("reset");
        resetButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final String stepText = DistanceFieldApplication.this.reset();
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        DistanceFieldApplication.this.stepTextField.setText(stepText);
                        ;
                        DistanceFieldApplication.this.stepTextField.setCaretPosition(0);
                        DistanceFieldApplication.this.displayPanel.repaint();
                        DistanceFieldApplication.this.updateContent();
                    }
                });
            }
        });
        buttonPanel.add(resetButton);
        JButton loadButton = new JButton("load");
        loadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                String lastDir = DistanceFieldApplication.this.prefs.get("lastDir", ".");
                if (lastDir != null) {
                    fc.setCurrentDirectory(new File(lastDir));
                }
                fc.setFileFilter(new FileFilter() {

                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().endsWith(".shp") || f.getName().endsWith(".SHP");
                    }

                    @Override
                    public String getDescription() {
                        return "shape files";
                    }
                });

                int returnVal = fc.showOpenDialog(DistanceFieldApplication.this.frame);
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                try {
                    final File file = fc.getSelectedFile();
                    DistanceFieldApplication.this.prefs.put("lastDir", file.getAbsolutePath());
                    final String stepText = DistanceFieldApplication.this.loadShapeFile(file.getAbsolutePath());
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            DistanceFieldApplication.this.stepTextField.setText(stepText + "\n");
                            DistanceFieldApplication.this.stepTextField.setCaretPosition(0);
                            DistanceFieldApplication.this.displayPanel.repaint();
                            DistanceFieldApplication.this.updateContent();
                        }
                    });
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (JAXBException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        buttonPanel.add(loadButton);

        toolPanel.add(buttonPanel);
        JComboBox<String> methodComboBox = new JComboBox<String>();
        methodComboBox.setBorder(BorderFactory.createTitledBorder("Distance computation type"));
        methodComboBox.addItem("Shrink 4");
        methodComboBox.addItem("Shrink 4 exact distance");
        methodComboBox.addItem("Shrink 8");
        methodComboBox.addItem("IDW");
        methodComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                DistanceFieldApplication.this.method = ((String) e.getItem());
                DistanceFieldApplication.this.displayPanel.repaint();

            }
        });
        methodComboBox.setSelectedItem("Shrink 4 exact distance");
        toolPanel.add(methodComboBox);

        this.stepTextField.setBorder(BorderFactory.createTitledBorder("Progression"));
        this.stepTextField.setEditable(false);
        this.stepTextField.setLineWrap(true);
        JScrollPane comp = new JScrollPane(this.stepTextField);
        comp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        comp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        toolPanel.add(comp);
        this.pixelLabel.setBorder(BorderFactory.createTitledBorder("Pixel information"));

        this.pixelLabel.setEditable(false);
        this.pixelLabel.setLineWrap(true);
        JScrollPane comp2 = new JScrollPane(this.pixelLabel);
        comp2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        comp2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        toolPanel.add(comp2);
        JComboBox<String> vizComboBox = new JComboBox<String>();
        vizComboBox.setBorder(BorderFactory.createTitledBorder("visualization"));
        vizComboBox.addItem("U HSV");
        vizComboBox.addItem("Distance BW");
        vizComboBox.addItem("Distance WB");
        vizComboBox.addItem("Distance HSV");
        vizComboBox.addItem("Distance Strip 10");
        vizComboBox.addItem("Distance Strip 50");
        vizComboBox.addItem("Distance Strip 200");
        vizComboBox.addItem("Distance Strip 500");
        vizComboBox.addItem("UV HSV + light");
        vizComboBox.addItem("UV textured");
        vizComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                DistanceFieldApplication.this.displayPanel.setViz((String) e.getItem());
                DistanceFieldApplication.this.displayPanel.invalidateImage();
                DistanceFieldApplication.this.displayPanel.repaint();

            }
        });
        vizComboBox.setSelectedItem("Distance BW");
        toolPanel.add(vizComboBox);

        JPanel gPanel = new JPanel();
        gPanel.setLayout(new BoxLayout(gPanel, BoxLayout.LINE_AXIS));
        gPanel.setBorder(BorderFactory.createTitledBorder("gradient"));

        JComboBox<String> gradComboBox = new JComboBox<String>();
        gradComboBox.addItem("None");
        gradComboBox.addItem("3x3");
        gradComboBox.addItem("5x5");
        gradComboBox.addItem("7x7");
        gradComboBox.addItem("10x10");
        gradComboBox.addItem("20x20");
        gradComboBox.addItem("30x30");
        gradComboBox.addItem("40x40");
        gradComboBox.addItem("50x50");
        gradComboBox.addItem("100x100");
        gradComboBox.addItem("200x200");
        gradComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                DistanceFieldApplication.this.displayPanel.setGradientViz((String) e.getItem());
                DistanceFieldApplication.this.displayPanel.invalidateImage();
                DistanceFieldApplication.this.displayPanel.repaint();

            }
        });

        gPanel.add(gradComboBox);
        final JSlider gSlider = new JSlider();
        gSlider.setMinimum(-1000);
        gSlider.setMinimum(1000);
        gSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                double scale = Math.exp(gSlider.getValue() / 100.);
                gSlider.setToolTipText(String.valueOf(scale));
                DistanceFieldApplication.this.displayPanel.setGradientScale(scale);
                DistanceFieldApplication.this.displayPanel.invalidateImage();
                DistanceFieldApplication.this.displayPanel.repaint();
            }
        });
        gPanel.add(gSlider);

        toolPanel.add(gPanel);

        this.uSlider = new JTextField();
        this.uSlider.setBorder(BorderFactory.createTitledBorder("U scale sactor"));

        this.uSlider.setText("1");
        this.uSlider.addKeyListener(new java.awt.event.KeyListener() {

            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
            }

            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    DistanceFieldApplication.this.uSlider.setToolTipText("u value = " + DistanceFieldApplication.this.getScaleU());
                    DistanceFieldApplication.this.displayPanel.invalidateImage();
                    DistanceFieldApplication.this.displayPanel.repaint();
                }

            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
            }
        });
        toolPanel.add(this.uSlider);
        this.vSlider = new JTextField();
        this.vSlider.setBorder(BorderFactory.createTitledBorder("V scale sactor"));
        this.vSlider.setText("1");
        this.vSlider.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    DistanceFieldApplication.this.vSlider.setToolTipText("v value = " + DistanceFieldApplication.this.getScaleV());
                    DistanceFieldApplication.this.displayPanel.invalidateImage();
                    DistanceFieldApplication.this.displayPanel.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
        toolPanel.add(this.vSlider);

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.LINE_AXIS));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtering"));

        final JComboBox<String> filterCombo = new JComboBox<String>();
        filterCombo.addItem("None");
        filterCombo.addItem("Blur UV 3px");
        filterCombo.addItem("Blur UV 10px");
        filterCombo.addItem("Blur UV 30px");
        filterCombo.addItem("Blur distance 3px");
        filterCombo.addItem("Blur distance 10px");
        filterCombo.addItem("Blur distance 30px");
        filterPanel.add(filterCombo);

        JButton filterButton = new JButton("apply");
        filterButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String returnValue = DistanceFieldApplication.this.displayPanel.applyFilter((String) filterCombo.getSelectedItem());
                DistanceFieldApplication.this.stepTextField.insert(returnValue + "\n", 0);
                DistanceFieldApplication.this.stepTextField.setCaretPosition(0);
                DistanceFieldApplication.this.displayPanel.invalidateImage();
                DistanceFieldApplication.this.displayPanel.repaint();

            }
        });
        filterPanel.add(filterButton);

        toolPanel.add(filterPanel);

        JPanel imageInfoPanel = new JPanel();
        imageInfoPanel.setLayout(new BoxLayout(imageInfoPanel, BoxLayout.PAGE_AXIS));
        imageInfoPanel.setBorder(BorderFactory.createTitledBorder("Image information"));

        imageInfoPanel.add(this.uMinLabel);
        imageInfoPanel.add(this.uMaxLabel);
        imageInfoPanel.add(this.vMinLabel);
        imageInfoPanel.add(this.vMaxLabel);
        imageInfoPanel.add(this.dMinLabel);
        imageInfoPanel.add(this.dMaxLabel);
        toolPanel.add(imageInfoPanel);
        this.frame.getContentPane().add(this.displayPanel, BorderLayout.CENTER);
        this.frame.getContentPane().add(toolPanel, BorderLayout.WEST);
    }

    public void show() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DistanceFieldApplication.this.frame.setLocation(0, 0);
                DistanceFieldApplication.this.frame.setSize(800, 600);
                DistanceFieldApplication.this.frame.setVisible(true);
            }
        });

    }

    public double getScaleU() {
        return Double.valueOf(this.uSlider.getText());

    }

    public double getScaleV() {
        return Double.valueOf(this.vSlider.getText());
    }

    public String loadShapeFile(String shapeFilename) throws FileNotFoundException, JAXBException {
        File file = new File(shapeFilename);

        if (file != null) {
            String fileName = file.getAbsolutePath();
            String extention = fileName.substring(fileName.lastIndexOf('.') + 1);
            LayerFactory factory = new LayerFactory(this.sld);
            Layer l = null;
            if (extention.equalsIgnoreCase("shp")) { //$NON-NLS-1$
                l = factory.createLayer(fileName, LayerType.SHAPEFILE);
            } else if (extention.equalsIgnoreCase("tif")) { //$NON-NLS-1$
                l = factory.createLayer(fileName, LayerType.GEOTIFF);
            } else if (extention.equalsIgnoreCase("asc")) { //$NON-NLS-1$
                l = factory.createLayer(fileName, LayerType.ASC);
            } else if (extention.equalsIgnoreCase("txt")) { //$NON-NLS-1$
                l = factory.createLayer(fileName, LayerType.TXT);
            }
            if (l != null) {
                this.sld.add(l);
                this.displayPanel.updateContent();
                this.updateContent();
            }
        }
        this.frame.repaint();
        return "shape file " + shapeFilename + " loaded";
    }

    private void updateContent() {
        if (this.displayPanel.getTexImage() != null) {
            this.displayPanel.getTexImage().invalidateUVBounds();
        }
        this.uMinLabel.setText("min u = "
                + (this.displayPanel.getTexImage() == null ? "not defined" : String.valueOf(this.displayPanel.getTexImage().getuMin())));
        this.uMaxLabel.setText("max u = "
                + (this.displayPanel.getTexImage() == null ? "not defined" : String.valueOf(this.displayPanel.getTexImage().getuMax())));
        this.vMinLabel.setText("min v = "
                + (this.displayPanel.getTexImage() == null ? "not defined" : String.valueOf(this.displayPanel.getTexImage().getvMin())));
        this.vMaxLabel.setText("max v = "
                + (this.displayPanel.getTexImage() == null ? "not defined" : String.valueOf(this.displayPanel.getTexImage().getvMax())));
        this.dMinLabel.setText("min d = "
                + (this.displayPanel.getTexImage() == null ? "not defined" : String.valueOf(this.displayPanel.getTexImage().getdMin())));
        this.dMaxLabel.setText("max d = "
                + (this.displayPanel.getTexImage() == null ? "not defined" : String.valueOf(this.displayPanel.getTexImage().getdMax())));
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        DistanceFieldApplication app = new DistanceFieldApplication();

        app.show();
        String shapeFilename = "/home/turbet/export/expressivemaps/data/JDD_Plancoet/mer_sans_sable.shp";
        try {
            app.loadShapeFile(shapeFilename);
        } catch (FileNotFoundException e) {
            logger.error(e);
            e.printStackTrace();
        } catch (JAXBException e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    /**
     * p is expressed in pixels in the texture image
     * 
     * @param p
     */
    public void updatePixelContent(Point2D p) {
        if (this.displayPanel.texImage == null) {
            this.pixelLabel.setText("no texture image");
            return;
        }
        int x = (int) p.getX();
        int y = (int) p.getY();
        if (x < 0 || x >= this.displayPanel.texImage.getWidth() || y < 0 || y >= this.displayPanel.texImage.getHeight()) {
            this.pixelLabel.setText("pixel " + x + "x" + y + " outside image");
            return;
        }
        TexturePixel pixel = this.displayPanel.texImage.getPixel(x, y);
        this.pixelLabel.setText("pixel " + x + "x" + y + " :\n distance = " + pixel.distance + "\n u = " + pixel.uTexture + "\n v = " + pixel.vTexture
                + "\n closest point (pixels) = " + pixel.closestPoint + "\n frontier = " + pixel.closestFrontier + "\n in ?= " + pixel.in + "\n sum weight = "
                + pixel.weightSum + "\n U sum weight = " + pixel.uTextureWeightSum + "\n V sum weight = " + pixel.vTextureWeightSum + pixel.weightSum
                + "\n U weighted = " + (pixel.uTextureWeightSum >= 1E-6 ? pixel.uTexture / pixel.uTextureWeightSum : "NaN") + "\n V weighted = "
                + (pixel.vTextureWeightSum >= 1E-6 ? pixel.vTexture / pixel.vTextureWeightSum : "NaN") + "\n V Gradient = " + pixel.vGradient);

    }

    /**
     * @return
     */
    private String reset() {
        this.displayPanel.reset();
        return "TextureImage reset";
    }

    /**
     * @return
     */
    private String doStep() {
        if ("IDW".equals(this.method)) {
            DistanceFieldApplication.this.displayPanel.doStepIDW();
        } else if ("Shrink 8".equals(this.method)) {
            return DistanceFieldApplication.this.displayPanel.doStepShrink8();
        } else if ("Shrink 4".equals(this.method)) {
            return DistanceFieldApplication.this.displayPanel.doStepShrink4();
        } else if ("Shrink 4 exact distance".equals(this.method)) {
            return DistanceFieldApplication.this.displayPanel.doStepShrink4ExactDistance();
        }
        return "unknown method type '" + this.method + "'";
    }
}
