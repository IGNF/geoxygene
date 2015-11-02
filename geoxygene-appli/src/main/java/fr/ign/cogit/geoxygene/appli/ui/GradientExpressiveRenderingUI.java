///*******************************************************************************
// * This file is part of the GeOxygene project source files.
// * 
// * GeOxygene aims at providing an open framework which implements OGC/ISO
// * specifications for the development and deployment of geographic (GIS)
// * applications. It is a open source contribution of the COGIT laboratory at the
// * Institut Géographique National (the French National Mapping Agency).
// * 
// * See: http://oxygene-project.sourceforge.net
// * 
// * Copyright (C) 2005 Institut Géographique National
// * 
// * This library is free software; you can redistribute it and/or modify it under
// * the terms of the GNU Lesser General Public License as published by the Free
// * Software Foundation; either version 2.1 of the License, or any later version.
// * 
// * This library is distributed in the hope that it will be useful, but WITHOUT
// * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
// * details.
// * 
// * You should have received a copy of the GNU Lesser General Public License
// * along with this library (see file LICENSE if present); if not, write to the
// * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
// * 02111-1307 USA
// *******************************************************************************/
//
//package fr.ign.cogit.geoxygene.appli.ui;
//
//import java.awt.BorderLayout;
//import java.text.DecimalFormat;
//
//import javax.swing.BorderFactory;
//import javax.swing.BoxLayout;
//import javax.swing.JComponent;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JSpinner;
//import javax.swing.SwingConstants;
//import javax.swing.border.EtchedBorder;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//
//import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
//import fr.ign.util.ui.SliderWithSpinner;
//import fr.ign.util.ui.SliderWithSpinner.SliderWithSpinnerModel;
//
///**
// * @author JeT
// * 
// */
//public class GradientExpressiveRenderingUI implements GenericParameterUI {
//
//    private JPanel main = null;
//    private GradientSubshaderDescriptor strtex = null;
//
//    private ProjectFrame parentProjectFrame = null;
//    double mapScale = 100000;
//    double maxCoastLineLength = 1000;
//    double resolution = 150;
//    int blurSize = 2;
//    public GenericParameterUI shaderUI = null;
//
//    /**
//     * Constructor
//     */
//    public GradientExpressiveRenderingUI(GradientSubshaderDescriptor strtex,
//            ProjectFrame projectFrame) {
//        this.parentProjectFrame = projectFrame;
//        this.main = null;
//        this.setGradientExpressiveRendering(strtex);
//
//    }
//
//    /**
//     * set the managed stroke texture expressive rendering object
//     * 
//     * @param strtex
//     */
//    private void setGradientExpressiveRendering(
//            GradientSubshaderDescriptor strtex) {
//        this.strtex = strtex;
//        this.setValuesFromObject();
//    }
//
//    /**
//     * set variable values from gradient expressive rendering object
//     */
//    @Override
//    public void setValuesFromObject() {
//        this.mapScale = this.strtex.getMapScale();
//        this.maxCoastLineLength = this.strtex.getMaxCoastlineLength();
//        this.resolution = this.strtex.getTextureResolution();
//        this.blurSize = this.strtex.getBlurSize();
//        this.shaderUI = null;
//        this.getShaderUI().setValuesFromObject();
//    }
//
//    private GenericParameterUI getShaderUI() {
//        if (this.shaderUI == null) {
//            this.shaderUI = ShaderUIFactory.getShaderUI(
//                    this.strtex.getShaderDescriptor(), this.parentProjectFrame);
//        }
//        return this.shaderUI;
//    }
//
//    /**
//     * set variable values to gradient expressive rendering object
//     */
//    @Override
//    public void setValuesToObject() {
//        this.strtex.setMapScale(this.mapScale);
//        this.strtex.setMaxCoastlineLength(this.maxCoastLineLength);
//        this.strtex.setTextureResolution(this.resolution);
//        this.strtex.setBlurSize(this.blurSize);
//        this.shaderUI.setValuesToObject();
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see fr.ign.cogit.geoxygene.appli.ui.ExpressiveRenderingUI#getGui()
//     */
//    @Override
//    public JComponent getGui() {
//        return new JScrollPane(this.getMainPanel());
//    }
//
//    protected void refresh() {
//        this.setValuesToObject();
//        this.parentProjectFrame.repaint();
//    }
//
//    private JPanel getMainPanel() {
//        if (this.main == null) {
//            this.main = new JPanel();
//            this.main.setLayout(new BoxLayout(this.main, BoxLayout.Y_AXIS));
//            this.main.setBorder(BorderFactory
//                    .createEtchedBorder(EtchedBorder.LOWERED));
//
//            SliderWithSpinnerModel mapScaleModel = new SliderWithSpinnerModel(
//                    this.mapScale, 1, 10000000, 1000);
//            final SliderWithSpinner mapScaleSpinner = new SliderWithSpinner(
//                    mapScaleModel);
//            JSpinner.NumberEditor mapScaleEditor = (JSpinner.NumberEditor) mapScaleSpinner
//                    .getEditor();
//            DecimalFormat intFormat = mapScaleEditor.getFormat();
//            intFormat.setMinimumFractionDigits(0);
//            mapScaleEditor.getTextField().setHorizontalAlignment(
//                    SwingConstants.CENTER);
//            mapScaleSpinner.setBorder(BorderFactory
//                    .createTitledBorder("map scale"));
//            mapScaleSpinner
//                    .setToolTipText("inverse map scale value: enter 25000 for 1:25000 real scale");
//            mapScaleSpinner.addChangeListener(new ChangeListener() {
//
//                @Override
//                public void stateChanged(ChangeEvent e) {
//                    GradientExpressiveRenderingUI.this.mapScale = (int) (double) (mapScaleSpinner
//                            .getValue());
//                    GradientExpressiveRenderingUI.this.refresh();
//
//                }
//            });
//            this.main.add(mapScaleSpinner);
//
//            SliderWithSpinnerModel maxCoastLineLengthModel = new SliderWithSpinnerModel(
//                    this.maxCoastLineLength, 0, 100000, 100);
//            final SliderWithSpinner maxCoastLineLengthSpinner = new SliderWithSpinner(
//                    maxCoastLineLengthModel);
//            JSpinner.NumberEditor maxCoastLineLengthEditor = (JSpinner.NumberEditor) maxCoastLineLengthSpinner
//                    .getEditor();
//            maxCoastLineLengthEditor.getTextField().setHorizontalAlignment(
//                    SwingConstants.CENTER);
//            maxCoastLineLengthSpinner.setBorder(BorderFactory
//                    .createTitledBorder("max coast line length"));
//            maxCoastLineLengthSpinner
//                    .setToolTipText("max length (in meter) for edges to be considered as coast line. If greater they are considered as map border");
//            maxCoastLineLengthSpinner.addChangeListener(new ChangeListener() {
//
//                @Override
//                public void stateChanged(ChangeEvent e) {
//                    GradientExpressiveRenderingUI.this.maxCoastLineLength = (maxCoastLineLengthSpinner
//                            .getValue());
//                    GradientExpressiveRenderingUI.this.refresh();
//
//                }
//            });
//            this.main.add(maxCoastLineLengthSpinner);
//
//            SliderWithSpinnerModel resolutionModel = new SliderWithSpinnerModel(
//                    this.resolution, 16, 24800, 100);
//            final SliderWithSpinner resolutionSpinner = new SliderWithSpinner(
//                    resolutionModel);
//            JSpinner.NumberEditor resolutionEditor = (JSpinner.NumberEditor) resolutionSpinner
//                    .getEditor();
//            resolutionEditor.getTextField().setHorizontalAlignment(
//                    SwingConstants.CENTER);
//            resolutionSpinner.setBorder(BorderFactory
//                    .createTitledBorder("resolution"));
//            resolutionSpinner
//                    .setToolTipText("resolution in DPI of the generated texture. greater than 300 may cause memory issues...");
//            resolutionSpinner.addChangeListener(new ChangeListener() {
//
//                @Override
//                public void stateChanged(ChangeEvent e) {
//                    GradientExpressiveRenderingUI.this.resolution = (resolutionSpinner
//                            .getValue());
//                    GradientExpressiveRenderingUI.this.refresh();
//
//                }
//            });
//            this.main.add(resolutionSpinner);
//
//            SliderWithSpinnerModel blurSizeModel = new SliderWithSpinnerModel(
//                    this.blurSize, 0, 100, 1);
//            final SliderWithSpinner blurSizeSpinner = new SliderWithSpinner(
//                    blurSizeModel);
//            JSpinner.NumberEditor blurSizeEditor = (JSpinner.NumberEditor) blurSizeSpinner
//                    .getEditor();
//            intFormat = blurSizeEditor.getFormat();
//            intFormat.setMinimumFractionDigits(0);
//            blurSizeEditor.getTextField().setHorizontalAlignment(
//                    SwingConstants.CENTER);
//            blurSizeSpinner.setBorder(BorderFactory
//                    .createTitledBorder("blur size"));
//            blurSizeSpinner
//                    .setToolTipText("size of the blurring window applied on UV texture map after generation");
//            blurSizeSpinner.addChangeListener(new ChangeListener() {
//
//                @Override
//                public void stateChanged(ChangeEvent e) {
//                    GradientExpressiveRenderingUI.this.blurSize = (int) (double) (blurSizeSpinner
//                            .getValue());
//                    GradientExpressiveRenderingUI.this.refresh();
//
//                }
//            });
//            this.main.add(blurSizeSpinner);
//
//            JPanel subShaderPanel = new JPanel(new BorderLayout());
//            subShaderPanel.setBorder(BorderFactory
//                    .createTitledBorder("subshader parameters"));
//
//            subShaderPanel.add(this.getShaderUI().getGui());
//            this.main.add(subShaderPanel);
//        }
//        return this.main;
//    }
//
//}
