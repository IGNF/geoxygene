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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
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
import fr.ign.util.ui.SliderWithSpinner;
import fr.ign.util.ui.SliderWithSpinner.SliderWithSpinnerModel;

/**
 * @author JeT
 * 
 */
public class BasicTextureExpressiveRenderingUI implements ExpressiveRenderingUI {

    private JPanel main = null;
    private BasicTextureExpressiveRenderingDescriptor strtex = null;

    private final Preferences prefs = Preferences.userRoot();
    private ProjectFrame parentProjectFrame = null;
    double aspectRatio = 8;
    double transitionSize = .5;
    String brushTextureFilename = null;
    private JLabel brushFilenameLabel = null;
    private static final String BRUSH_LAST_DIRECTORY = BasicTextureExpressiveRenderingUI.class
            .getSimpleName() + ".brushLastDirectory";

    /**
     * Constructor
     */
    public BasicTextureExpressiveRenderingUI(
            BasicTextureExpressiveRenderingDescriptor strtex,
            ProjectFrame projectFrame) {
        this.parentProjectFrame = projectFrame;
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
        this.main = null;
        this.setValuesFromObject();
    }

    /**
     * set variable values from stroke texture expressive rendering object
     */
    @Override
    public void setValuesFromObject() {
        this.aspectRatio = this.strtex.getAspectRatio();
        this.transitionSize = this.strtex.getTransitionSize();
        this.brushTextureFilename = this.strtex.getBrushTextureFilename();
    }

    /**
     * set variable values from stroke texture expressive rendering object
     */
    @Override
    public void setValuesToObject() {
        this.strtex.setAspectRatio(this.aspectRatio);
        this.strtex.setTransitionSize(this.transitionSize);
        this.strtex.setBrushTextureFilename(this.brushTextureFilename);
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

            SliderWithSpinnerModel aspectRatioModel = new SliderWithSpinnerModel(
                    this.aspectRatio, 0, 180, .1);
            final SliderWithSpinner aspectRatioSpinner = new SliderWithSpinner(
                    aspectRatioModel);
            JSpinner.NumberEditor aspectRatioEditor = (JSpinner.NumberEditor) aspectRatioSpinner
                    .getEditor();
            aspectRatioEditor.getTextField().setHorizontalAlignment(
                    SwingConstants.CENTER);
            aspectRatioSpinner.setBorder(BorderFactory
                    .createTitledBorder("brush aspect ratio"));
            aspectRatioSpinner
                    .setToolTipText("this value changes the brush texture aspect ratio");

            aspectRatioSpinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    BasicTextureExpressiveRenderingUI.this.aspectRatio = (aspectRatioSpinner
                            .getValue());
                    BasicTextureExpressiveRenderingUI.this.refresh();

                }
            });
            this.main.add(aspectRatioSpinner);

            SliderWithSpinnerModel transitionSizeModel = new SliderWithSpinnerModel(
                    this.transitionSize, 0, 180, .1);
            final SliderWithSpinner transitionSizeSpinner = new SliderWithSpinner(
                    transitionSizeModel);
            JSpinner.NumberEditor transitionSizeEditor = (JSpinner.NumberEditor) transitionSizeSpinner
                    .getEditor();
            transitionSizeEditor.getTextField().setHorizontalAlignment(
                    SwingConstants.CENTER);
            transitionSizeSpinner.setBorder(BorderFactory
                    .createTitledBorder("transition size"));
            transitionSizeSpinner
                    .setToolTipText("transition size between segments in polylines (in m)");
            transitionSizeSpinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    BasicTextureExpressiveRenderingUI.this.transitionSize = (transitionSizeSpinner
                            .getValue());
                    BasicTextureExpressiveRenderingUI.this.refresh();

                }
            });
            this.main.add(transitionSizeSpinner);

            JButton brushBrowseButton = new JButton("brush browser...");
            brushBrowseButton.setBorder(BorderFactory.createEmptyBorder(2, 2,
                    2, 2));
            brushBrowseButton.setToolTipText("Load brush file");
            brushBrowseButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fc = new JFileChooser(
                            BasicTextureExpressiveRenderingUI.this.prefs.get(
                                    BRUSH_LAST_DIRECTORY, "."));
                    if (fc.showOpenDialog(BasicTextureExpressiveRenderingUI.this.parentProjectFrame
                            .getGui()) == JFileChooser.APPROVE_OPTION) {
                        try {
                            File selectedFile = fc.getSelectedFile();
                            BasicTextureExpressiveRenderingUI.this.brushTextureFilename = selectedFile
                                    .getAbsolutePath();
                            BasicTextureExpressiveRenderingUI.this.brushFilenameLabel
                                    .setText(BasicTextureExpressiveRenderingUI.this.brushTextureFilename
                                            .substring(BasicTextureExpressiveRenderingUI.this.brushTextureFilename
                                                    .length() - 30));
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
            this.main.add(brushBrowseButton);

            this.brushFilenameLabel = new JLabel(
                    this.brushTextureFilename
                            .substring(this.brushTextureFilename.length() - 30));
            this.main.add(Box.createHorizontalGlue());
            this.main.add(this.brushFilenameLabel);

        }
        return this.main;
    }
}
