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

package fr.ign.cogit.geoxygene.appli.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor;
import fr.ign.util.ui.JRecentFileChooser;
import fr.ign.util.ui.SliderWithSpinner;
import fr.ign.util.ui.SliderWithSpinner.SliderWithSpinnerModel;

/**
 * @author JeT
 * 
 */
public class BasicTextureExpressiveRenderingUI implements GenericParameterUI {

    private static final int FILE_LENGTH_DISPLAY = 50;
    private JPanel main = null;
    private BasicTextureExpressiveRenderingDescriptor strtex = null;

    private final Preferences prefs = Preferences.userRoot();
    private ProjectFrame parentProjectFrame = null;
    double transitionSize = .5;
    double paperSizeInCm = .5;
    double paperDensity = 0.7;
    double brushDensity = 1.9;
    double strokePressure = 2.64;
    double sharpness = 0.1;
    public String paperTextureFilename = null;
    public String brushTextureFilename = null;
    public int brushStartLength = 100;
    public int brushEndLength = 200;
    public GenericParameterUI shaderUI = null;
    private JLabel paperFilenameLabel = null;
    private JLabel brushFilenameLabel = null;

    private static final String LAST_DIRECTORY = BasicTextureExpressiveRenderingUI.class
            .getSimpleName() + ".lastDirectory";
    private static final String PAPER_LAST_DIRECTORY = BasicTextureExpressiveRenderingUI.class
            .getSimpleName() + ".paperLastDirectory";
    private static final String BRUSH_LAST_DIRECTORY = BasicTextureExpressiveRenderingUI.class
            .getSimpleName() + ".brushLastDirectory";

    /**
     * Constructor
     */
    public BasicTextureExpressiveRenderingUI(
            BasicTextureExpressiveRenderingDescriptor strtex,
            ProjectFrame projectFrame) {
        this.parentProjectFrame = projectFrame;
        this.main = null;
        this.setBasicTextureExpressiveRendering(strtex);

    }

    /**
     * set the managed stroke texture expressive rendering object
     * 
     * @param strtex
     */
    private void setBasicTextureExpressiveRendering(
            BasicTextureExpressiveRenderingDescriptor strtex) {
        this.strtex = strtex;
        this.setValuesFromObject();
    }

    /**
     * set variable values from stroke texture expressive rendering object
     */
    @Override
    public void setValuesFromObject() {
        this.transitionSize = this.strtex.getTransitionSize();
        this.paperSizeInCm = this.strtex.getPaperSizeInCm();
        this.paperDensity = this.strtex.getPaperDensity();
        this.brushDensity = this.strtex.getBrushDensity();
        this.strokePressure = this.strtex.getStrokePressure();
        this.sharpness = this.strtex.getSharpness();
        this.paperTextureFilename = this.strtex.getPaperTextureFilename();
        this.brushTextureFilename = this.strtex.getBrushTextureFilename();
        this.brushStartLength = this.strtex.getBrushStartLength();
        this.brushEndLength = this.strtex.getBrushEndLength();
        this.getShaderUI().setValuesFromObject();
    }

    private GenericParameterUI getShaderUI() {
        if (this.shaderUI == null) {
            this.shaderUI = ShaderUIFactory.getShaderUI(
                    this.strtex.getShaderDescriptor(), this.parentProjectFrame);
        }
        return this.shaderUI;
    }

    /**
     * set variable values from stroke texture expressive rendering object
     */
    @Override
    public void setValuesToObject() {
        this.strtex.setTransitionSize(this.transitionSize);
        this.strtex.setPaperSizeInCm(this.paperSizeInCm);
        this.strtex.setPaperDensity(this.paperDensity);
        this.strtex.setBrushDensity(this.brushDensity);
        this.strtex.setStrokePressure(this.strokePressure);
        this.strtex.setSharpness(this.sharpness);
        this.strtex.setPaperTextureFilename(this.paperTextureFilename);
        this.strtex.setBrushTextureFilename(this.brushTextureFilename);
        this.strtex.setBrushStartLength(this.brushStartLength);
        this.strtex.setBrushEndLength(this.brushEndLength);
        this.shaderUI.setValuesToObject();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ui.ExpressiveRenderingUI#getGui()
     */
    @Override
    public JComponent getGui() {
        return new JScrollPane(this.getMainPanel());
    }

    protected void refresh() {
        this.setValuesToObject();
        this.parentProjectFrame.repaint();
    }

    private JPanel getMainPanel() {
        if (this.main == null) {
            this.main = new JPanel();
            this.main.setLayout(new BoxLayout(this.main, BoxLayout.Y_AXIS));
            this.main.setBorder(BorderFactory
                    .createEtchedBorder(EtchedBorder.LOWERED));

            JPanel paperPanel = new JPanel(new BorderLayout());

            JButton paperBrowseButton = new JButton("paper browse...");
            paperBrowseButton.setToolTipText("Load background paper file");
            paperBrowseButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fc = new JRecentFileChooser(
                            BasicTextureExpressiveRenderingUI.this.prefs.get(
                                    PAPER_LAST_DIRECTORY, "."));
                    if (fc.showOpenDialog(BasicTextureExpressiveRenderingUI.this.parentProjectFrame
                            .getGui()) == JFileChooser.APPROVE_OPTION) {
                        try {
                            File selectedFile = fc.getSelectedFile();
                            BasicTextureExpressiveRenderingUI.this.paperTextureFilename = selectedFile
                                    .getAbsolutePath();
                            BasicTextureExpressiveRenderingUI.this.paperFilenameLabel
                                    .setText(BasicTextureExpressiveRenderingUI.this.paperTextureFilename.substring(Math
                                            .max(0,
                                                    BasicTextureExpressiveRenderingUI.this.paperTextureFilename
                                                            .length()
                                                            - FILE_LENGTH_DISPLAY)));

                            BasicTextureExpressiveRenderingUI.this.prefs.put(
                                    PAPER_LAST_DIRECTORY,
                                    selectedFile.getAbsolutePath());

                            BasicTextureExpressiveRenderingUI.this.refresh();
                        } catch (Exception e1) {
                            JOptionPane
                                    .showMessageDialog(
                                            BasicTextureExpressiveRenderingUI.this.parentProjectFrame
                                                    .getGui(), e1.getMessage());
                            e1.printStackTrace();
                        }
                    }
                }

            });

            paperPanel.add(paperBrowseButton, BorderLayout.EAST);
            this.paperFilenameLabel = new JLabel(
                    this.paperTextureFilename.substring(Math.max(0,
                            this.paperTextureFilename.length()
                                    - FILE_LENGTH_DISPLAY)));
            paperPanel.add(this.paperFilenameLabel, BorderLayout.CENTER);

            this.main.add(paperPanel);

            SliderWithSpinnerModel brushStartModel = new SliderWithSpinnerModel(
                    this.brushStartLength, 1, 5000, 1);
            final SliderWithSpinner brushStartSpinner = new SliderWithSpinner(
                    brushStartModel);
            JSpinner.NumberEditor brushStartEditor = (JSpinner.NumberEditor) brushStartSpinner
                    .getEditor();
            DecimalFormat intFormat = brushStartEditor.getFormat();
            intFormat.setMinimumFractionDigits(0);
            brushStartEditor.getTextField().setHorizontalAlignment(
                    SwingConstants.CENTER);
            brushStartSpinner.setBorder(BorderFactory
                    .createTitledBorder("brush start"));
            brushStartSpinner.setToolTipText("length of the brush start");
            brushStartSpinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    BasicTextureExpressiveRenderingUI.this.brushStartLength = (int) (double) (brushStartSpinner
                            .getValue());
                    BasicTextureExpressiveRenderingUI.this.refresh();

                }
            });

            SliderWithSpinnerModel brushEndModel = new SliderWithSpinnerModel(
                    this.brushEndLength, 1, 5000, 1);
            final SliderWithSpinner brushEndSpinner = new SliderWithSpinner(
                    brushEndModel);
            JSpinner.NumberEditor brushEndEditor = (JSpinner.NumberEditor) brushEndSpinner
                    .getEditor();
            intFormat.setMinimumFractionDigits(0);
            brushEndEditor.getTextField().setHorizontalAlignment(
                    SwingConstants.CENTER);
            brushEndSpinner.setBorder(BorderFactory
                    .createTitledBorder("brush end"));
            brushEndSpinner.setToolTipText("length of the brush end");
            brushEndSpinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    BasicTextureExpressiveRenderingUI.this.brushEndLength = (int) (double) (brushEndSpinner
                            .getValue());
                    BasicTextureExpressiveRenderingUI.this.refresh();

                }
            });

            JPanel brushPanel = new JPanel(new BorderLayout());

            JButton brushBrowseButton = new JButton("brush browser...");
            brushBrowseButton.setToolTipText("Load brush file");
            brushBrowseButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fc = new JRecentFileChooser(
                            BasicTextureExpressiveRenderingUI.this.prefs.get(
                                    BRUSH_LAST_DIRECTORY, "."));
                    if (fc.showOpenDialog(BasicTextureExpressiveRenderingUI.this.parentProjectFrame
                            .getGui()) == JFileChooser.APPROVE_OPTION) {
                        try {
                            File selectedFile = fc.getSelectedFile();
                            BasicTextureExpressiveRenderingUI.this.brushTextureFilename = selectedFile
                                    .getAbsolutePath();
                            BasicTextureExpressiveRenderingUI.this.brushFilenameLabel
                                    .setText(BasicTextureExpressiveRenderingUI.this.brushTextureFilename.substring(Math
                                            .max(0,
                                                    BasicTextureExpressiveRenderingUI.this.brushTextureFilename
                                                            .length()
                                                            - FILE_LENGTH_DISPLAY)));
                            Pattern pattern = Pattern
                                    .compile("([0-9]+)-([0-9]+)");
                            Matcher matcher = pattern
                                    .matcher(BasicTextureExpressiveRenderingUI.this.brushTextureFilename);
                            if (matcher.matches()) {
                                int start = Integer.valueOf(matcher.group(1));
                                int end = Integer.valueOf(matcher.group(2));
                                brushStartSpinner.setValue(start);
                                brushEndSpinner.setValue(end);
                                BasicTextureExpressiveRenderingUI.this.brushStartLength = (int) (double) (brushStartSpinner
                                        .getValue());
                                BasicTextureExpressiveRenderingUI.this.brushEndLength = (int) (double) (brushEndSpinner
                                        .getValue());
                            }
                            BasicTextureExpressiveRenderingUI.this.prefs.put(
                                    BRUSH_LAST_DIRECTORY,
                                    selectedFile.getAbsolutePath());
                            BasicTextureExpressiveRenderingUI.this.refresh();
                        } catch (Exception e1) {
                            JOptionPane
                                    .showMessageDialog(
                                            BasicTextureExpressiveRenderingUI.this.parentProjectFrame
                                                    .getGui(), e1.getMessage());
                            e1.printStackTrace();
                        }
                    }
                }

            });

            this.brushFilenameLabel = new JLabel(
                    this.brushTextureFilename.substring(Math.max(0,
                            this.brushTextureFilename.length()
                                    - FILE_LENGTH_DISPLAY)));
            brushPanel.add(brushBrowseButton, BorderLayout.EAST);
            brushPanel.add(this.brushFilenameLabel, BorderLayout.CENTER);

            this.main.add(brushPanel);

            SliderWithSpinnerModel brushDensityModel = new SliderWithSpinnerModel(
                    this.brushDensity, 0, 10, .1);
            final SliderWithSpinner brushDensitySpinner = new SliderWithSpinner(
                    brushDensityModel);
            JSpinner.NumberEditor brushDensityEditor = (JSpinner.NumberEditor) brushDensitySpinner
                    .getEditor();
            brushDensityEditor.getTextField().setHorizontalAlignment(
                    SwingConstants.CENTER);
            brushDensitySpinner.setBorder(BorderFactory
                    .createTitledBorder("brush density"));
            brushDensitySpinner.setToolTipText("brush height scale factor");
            brushDensitySpinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    BasicTextureExpressiveRenderingUI.this.brushDensity = (brushDensitySpinner
                            .getValue());
                    BasicTextureExpressiveRenderingUI.this.refresh();

                }
            });
            this.main.add(brushDensitySpinner);

            SliderWithSpinnerModel paperDensityModel = new SliderWithSpinner.SliderWithSpinnerModel(
                    this.paperDensity, 0, 10, 0.1, .001);
            final SliderWithSpinner paperDensitySpinner = new SliderWithSpinner(
                    paperDensityModel);
            JSpinner.NumberEditor paperDensityEditor = (JSpinner.NumberEditor) paperDensitySpinner
                    .getEditor();
            paperDensityEditor.getTextField().setHorizontalAlignment(
                    SwingConstants.CENTER);
            paperDensitySpinner.setBorder(BorderFactory
                    .createTitledBorder("paper density"));
            paperDensitySpinner.setToolTipText("scale factor for paper height");
            paperDensitySpinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    BasicTextureExpressiveRenderingUI.this.paperDensity = (paperDensitySpinner
                            .getValue());
                    BasicTextureExpressiveRenderingUI.this.refresh();

                }
            });
            this.main.add(paperDensitySpinner);

            SliderWithSpinnerModel pressureModel = new SliderWithSpinnerModel(
                    this.strokePressure, 0.01, 100, .01);
            final SliderWithSpinner pressureSpinner = new SliderWithSpinner(
                    pressureModel);
            JSpinner.NumberEditor pressureEditor = (JSpinner.NumberEditor) pressureSpinner
                    .getEditor();
            pressureEditor.getTextField().setHorizontalAlignment(
                    SwingConstants.CENTER);
            pressureSpinner.setBorder(BorderFactory
                    .createTitledBorder("stroke pressure"));
            pressureSpinner.setToolTipText("distance between brush and paper");
            pressureSpinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    BasicTextureExpressiveRenderingUI.this.strokePressure = (pressureSpinner
                            .getValue());
                    BasicTextureExpressiveRenderingUI.this.refresh();

                }
            });
            this.main.add(pressureSpinner);
            SliderWithSpinnerModel sharpnessModel = new SliderWithSpinnerModel(
                    this.sharpness, 0.0001, 10, .001);
            final SliderWithSpinner sharpnessSpinner = new SliderWithSpinner(
                    sharpnessModel);
            JSpinner.NumberEditor sharpnessEditor = (JSpinner.NumberEditor) sharpnessSpinner
                    .getEditor();
            sharpnessEditor.getTextField().setHorizontalAlignment(
                    SwingConstants.CENTER);
            sharpnessSpinner.setBorder(BorderFactory
                    .createTitledBorder("blending sharpness"));
            sharpnessSpinner
                    .setToolTipText("blending contrast between brush and paper");
            sharpnessSpinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    BasicTextureExpressiveRenderingUI.this.sharpness = (sharpnessSpinner
                            .getValue());
                    BasicTextureExpressiveRenderingUI.this.refresh();

                }
            });
            this.main.add(sharpnessSpinner);

            JPanel subShaderPanel = new JPanel(new BorderLayout());
            subShaderPanel.setBorder(BorderFactory
                    .createTitledBorder("subshader parameters"));

            subShaderPanel.add(this.getShaderUI().getGui());
            this.main.add(subShaderPanel);
        }
        return this.main;
    }
    //
    //
    //
    //
    // private JPanel main = null;
    // private BasicTextureExpressiveRenderingDescriptor strtex = null;
    //
    // private final Preferences prefs = Preferences.userRoot();
    // private ProjectFrame parentProjectFrame = null;
    // double aspectRatio = 8;
    // double transitionSize = .5;
    // double brushAspectRatio = 8;
    // double paperScaleFactor = .5;
    // double paperDensity = 0.7;
    // double brushDensity = 1.9;
    // double strokePressure = 2.64;
    // double sharpness = 0.1;
    // public String paperTextureFilename = null;
    // public String brushTextureFilename = null;
    // public int brushStartLength = 100;
    // public int brushEndLength = 200;
    // public ExpressiveRenderingUI shaderUI = null;
    // private JLabel paperFilenameLabel = null;
    // private JLabel brushFilenameLabel = null;
    //
    // private static final String LAST_DIRECTORY =
    // BasicTextureExpressiveRenderingUI.class
    // .getSimpleName() + ".lastDirectory";
    // private static final String PAPER_LAST_DIRECTORY =
    // BasicTextureExpressiveRenderingUI.class
    // .getSimpleName() + ".paperLastDirectory";
    // private static final String BRUSH_LAST_DIRECTORY =
    // BasicTextureExpressiveRenderingUI.class
    // .getSimpleName() + ".brushLastDirectory";
    //
    // /**
    // * Constructor
    // */
    // public BasicTextureExpressiveRenderingUI(
    // BasicTextureExpressiveRenderingDescriptor strtex,
    // ProjectFrame projectFrame) {
    // this.parentProjectFrame = projectFrame;
    // this.setBasicTextureExpressiveRendering(strtex);
    // }
    //
    // /**
    // * set the managed stroke texture expressive rendering object
    // *
    // * @param strtex
    // */
    // private void setBasicTextureExpressiveRendering(
    // BasicTextureExpressiveRenderingDescriptor strtex) {
    // this.strtex = strtex;
    // this.main = null;
    // this.setValuesFromObject();
    // }
    //
    // /**
    // * set variable values from stroke texture expressive rendering object
    // */
    // @Override
    // public void setValuesFromObject() {
    // this.aspectRatio = this.strtex.getBrushAspectRatio();
    // this.transitionSize = this.strtex.getTransitionSize();
    // this.brushTextureFilename = this.strtex.getBrushTextureFilename();
    // }
    //
    // /**
    // * set variable values from stroke texture expressive rendering object
    // */
    // @Override
    // public void setValuesToObject() {
    // this.strtex.setBrushAspectRatio(this.aspectRatio);
    // this.strtex.setTransitionSize(this.transitionSize);
    // this.strtex.setBrushTextureFilename(this.brushTextureFilename);
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see fr.ign.cogit.geoxygene.appli.ui.ExpressiveRenderingUI#getGui()
    // */
    // @Override
    // public JComponent getGui() {
    // return new JScrollPane(this.getMainPanel());
    // }
    //
    // protected void refresh() {
    // this.setValuesToObject();
    // this.parentProjectFrame.repaint();
    // }
    //
    // private JPanel getMainPanel() {
    // if (this.main == null) {
    // this.main = new JPanel();
    // this.main.setLayout(new BoxLayout(this.main, BoxLayout.Y_AXIS));
    // this.main.setBorder(BorderFactory
    // .createEtchedBorder(EtchedBorder.LOWERED));
    //
    // SliderWithSpinnerModel aspectRatioModel = new SliderWithSpinnerModel(
    // this.aspectRatio, 0, 180, .1);
    // final SliderWithSpinner aspectRatioSpinner = new SliderWithSpinner(
    // aspectRatioModel);
    // JSpinner.NumberEditor aspectRatioEditor = (JSpinner.NumberEditor)
    // aspectRatioSpinner
    // .getEditor();
    // aspectRatioEditor.getTextField().setHorizontalAlignment(
    // SwingConstants.CENTER);
    // aspectRatioSpinner.setBorder(BorderFactory
    // .createTitledBorder("brush aspect ratio"));
    // aspectRatioSpinner
    // .setToolTipText("this value changes the brush texture aspect ratio");
    //
    // aspectRatioSpinner.addChangeListener(new ChangeListener() {
    //
    // @Override
    // public void stateChanged(ChangeEvent e) {
    // BasicTextureExpressiveRenderingUI.this.aspectRatio = (aspectRatioSpinner
    // .getValue());
    // BasicTextureExpressiveRenderingUI.this.refresh();
    //
    // }
    // });
    // this.main.add(aspectRatioSpinner);
    //
    // SliderWithSpinnerModel transitionSizeModel = new SliderWithSpinnerModel(
    // this.transitionSize, 0, 180, .1);
    // final SliderWithSpinner transitionSizeSpinner = new SliderWithSpinner(
    // transitionSizeModel);
    // JSpinner.NumberEditor transitionSizeEditor = (JSpinner.NumberEditor)
    // transitionSizeSpinner
    // .getEditor();
    // transitionSizeEditor.getTextField().setHorizontalAlignment(
    // SwingConstants.CENTER);
    // transitionSizeSpinner.setBorder(BorderFactory
    // .createTitledBorder("transition size"));
    // transitionSizeSpinner
    // .setToolTipText("transition size between segments in polylines (in m)");
    // transitionSizeSpinner.addChangeListener(new ChangeListener() {
    //
    // @Override
    // public void stateChanged(ChangeEvent e) {
    // BasicTextureExpressiveRenderingUI.this.transitionSize =
    // (transitionSizeSpinner
    // .getValue());
    // BasicTextureExpressiveRenderingUI.this.refresh();
    //
    // }
    // });
    // this.main.add(transitionSizeSpinner);
    //
    // JButton brushBrowseButton = new JButton("brush browser...");
    // brushBrowseButton.setBorder(BorderFactory.createEmptyBorder(2, 2,
    // 2, 2));
    // brushBrowseButton.setToolTipText("Load brush file");
    // brushBrowseButton.addActionListener(new ActionListener() {
    //
    // @Override
    // public void actionPerformed(ActionEvent e) {
    // JFileChooser fc = new JRecentFileChooser(
    // BasicTextureExpressiveRenderingUI.this.prefs.get(
    // BRUSH_LAST_DIRECTORY, "."));
    // if
    // (fc.showOpenDialog(BasicTextureExpressiveRenderingUI.this.parentProjectFrame
    // .getGui()) == JFileChooser.APPROVE_OPTION) {
    // try {
    // File selectedFile = fc.getSelectedFile();
    // BasicTextureExpressiveRenderingUI.this.brushTextureFilename =
    // selectedFile
    // .getAbsolutePath();
    // BasicTextureExpressiveRenderingUI.this.brushFilenameLabel
    // .setText(BasicTextureExpressiveRenderingUI.this.brushTextureFilename
    // .substring(BasicTextureExpressiveRenderingUI.this.brushTextureFilename
    // .length() - 30));
    // BasicTextureExpressiveRenderingUI.this.prefs.put(
    // BRUSH_LAST_DIRECTORY,
    // selectedFile.getAbsolutePath());
    // BasicTextureExpressiveRenderingUI.this.refresh();
    // } catch (Exception e1) {
    // JOptionPane
    // .showMessageDialog(
    // BasicTextureExpressiveRenderingUI.this.parentProjectFrame
    // .getGui(), e1.getMessage());
    // e1.printStackTrace();
    // }
    // }
    // }
    //
    // });
    // this.main.add(brushBrowseButton);
    //
    // this.brushFilenameLabel = new JLabel(
    // this.brushTextureFilename
    // .substring(this.brushTextureFilename.length() - 30));
    // this.main.add(Box.createHorizontalGlue());
    // this.main.add(this.brushFilenameLabel);
    //
    // }
    // return this.main;
    // }

}
