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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.style.expressive.RandomVariationShaderDescriptor;
import fr.ign.util.ui.SliderWithSpinner;
import fr.ign.util.ui.SliderWithSpinner.SliderWithSpinnerModel;

/**
 * @author JeT
 * 
 */
public class RandomVariationShaderUI implements ExpressiveRenderingUI {

    double strokePressureVariationAmplitude = .32;
    double strokePressureVariationWavelength = 100;
    double strokeShiftVariationAmplitude = .92;
    double strokeShiftVariationWavelength = 100;
    double strokeThicknessVariationAmplitude = .07;
    double strokeThicknessVariationWavelength = 100;
    private JPanel main = null;
    private ProjectFrame parentProjectFrame = null;
    private RandomVariationShaderDescriptor strtex = null;

    /**
     * Constructor
     */
    public RandomVariationShaderUI(RandomVariationShaderDescriptor strtex,
            ProjectFrame projectFrame) {
        this.parentProjectFrame = projectFrame;
        this.setRandomVariationShaderDescriptor(strtex);
    }

    /**
     * @return the strtex
     */
    public RandomVariationShaderDescriptor getRandomVariationShaderDescriptor() {
        return this.strtex;
    }

    /**
     * @param strtex
     *            the strtex to set
     */
    public void setRandomVariationShaderDescriptor(
            RandomVariationShaderDescriptor strtex) {
        this.strtex = strtex;
        this.main = null;
        this.setValuesFromObject();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ui.ExpressiveRenderingUI#getGui()
     */
    @Override
    public JComponent getGui() {
        if (this.main == null) {
            this.main = new JPanel();
            this.main.setLayout(new BoxLayout(this.main, BoxLayout.Y_AXIS));
            this.main.setBorder(BorderFactory
                    .createEtchedBorder(EtchedBorder.LOWERED));
            SliderWithSpinnerModel pressureVariationAmplitudeModel = new SliderWithSpinnerModel(
                    this.strokePressureVariationAmplitude, 0, 100, .1);
            final SliderWithSpinner pressureVariationAmplitudeSpinner = new SliderWithSpinner(
                    pressureVariationAmplitudeModel);
            JSpinner.NumberEditor pressureVariationAmplitudeEditor = (JSpinner.NumberEditor) pressureVariationAmplitudeSpinner
                    .getEditor();
            pressureVariationAmplitudeEditor.getTextField()
                    .setHorizontalAlignment(SwingConstants.CENTER);
            pressureVariationAmplitudeSpinner.setBorder(BorderFactory
                    .createTitledBorder("pressure amplitude"));

            pressureVariationAmplitudeSpinner
                    .addChangeListener(new ChangeListener() {

                        @Override
                        public void stateChanged(ChangeEvent e) {
                            RandomVariationShaderUI.this.strokePressureVariationAmplitude = (pressureVariationAmplitudeSpinner
                                    .getValue());
                            RandomVariationShaderUI.this.refresh();

                        }
                    });
            this.main.add(pressureVariationAmplitudeSpinner);

            SliderWithSpinnerModel pressureVariationWavelengthModel = new SliderWithSpinnerModel(
                    this.strokePressureVariationWavelength, 0.001, 100000, 10);
            final SliderWithSpinner pressureVariationWavelengthSpinner = new SliderWithSpinner(
                    pressureVariationWavelengthModel);
            JSpinner.NumberEditor pressureVariationWavelengthEditor = (JSpinner.NumberEditor) pressureVariationWavelengthSpinner
                    .getEditor();
            pressureVariationWavelengthEditor.getTextField()
                    .setHorizontalAlignment(SwingConstants.CENTER);
            pressureVariationWavelengthSpinner.setBorder(BorderFactory
                    .createTitledBorder("pressure Wavelength"));

            pressureVariationWavelengthSpinner
                    .addChangeListener(new ChangeListener() {

                        @Override
                        public void stateChanged(ChangeEvent e) {
                            RandomVariationShaderUI.this.strokePressureVariationWavelength = (pressureVariationWavelengthSpinner
                                    .getValue());
                            RandomVariationShaderUI.this.refresh();

                        }
                    });
            this.main.add(pressureVariationWavelengthSpinner);

            SliderWithSpinnerModel shiftVariationAmplitudeModel = new SliderWithSpinnerModel(
                    this.strokeShiftVariationAmplitude, 0, 1, .01);
            final SliderWithSpinner shiftVariationAmplitudeSpinner = new SliderWithSpinner(
                    shiftVariationAmplitudeModel);
            JSpinner.NumberEditor shiftVariationAmplitudeEditor = (JSpinner.NumberEditor) shiftVariationAmplitudeSpinner
                    .getEditor();
            shiftVariationAmplitudeEditor.getTextField()
                    .setHorizontalAlignment(SwingConstants.CENTER);
            shiftVariationAmplitudeSpinner.setBorder(BorderFactory
                    .createTitledBorder("shift amplitude"));

            shiftVariationAmplitudeSpinner
                    .addChangeListener(new ChangeListener() {

                        @Override
                        public void stateChanged(ChangeEvent e) {
                            RandomVariationShaderUI.this.strokeShiftVariationAmplitude = (shiftVariationAmplitudeSpinner
                                    .getValue());
                            RandomVariationShaderUI.this.refresh();

                        }
                    });
            this.main.add(shiftVariationAmplitudeSpinner);

            SliderWithSpinnerModel shiftVariationWavelengthModel = new SliderWithSpinnerModel(
                    this.strokeShiftVariationWavelength, 0.001, 100000, 10);
            final SliderWithSpinner shiftVariationWavelengthSpinner = new SliderWithSpinner(
                    shiftVariationWavelengthModel);
            JSpinner.NumberEditor shiftVariationWavelengthEditor = (JSpinner.NumberEditor) shiftVariationWavelengthSpinner
                    .getEditor();
            shiftVariationWavelengthEditor.getTextField()
                    .setHorizontalAlignment(SwingConstants.CENTER);
            shiftVariationWavelengthSpinner.setBorder(BorderFactory
                    .createTitledBorder("shift Wavelength"));

            shiftVariationWavelengthSpinner
                    .addChangeListener(new ChangeListener() {

                        @Override
                        public void stateChanged(ChangeEvent e) {
                            RandomVariationShaderUI.this.strokeShiftVariationWavelength = (shiftVariationWavelengthSpinner
                                    .getValue());
                            RandomVariationShaderUI.this.refresh();

                        }
                    });
            this.main.add(shiftVariationWavelengthSpinner);

            SliderWithSpinnerModel thicknessVariationAmplitudeModel = new SliderWithSpinnerModel(
                    this.strokeThicknessVariationAmplitude, 0, 1, .01);
            final SliderWithSpinner thicknessVariationAmplitudeSpinner = new SliderWithSpinner(
                    thicknessVariationAmplitudeModel);
            JSpinner.NumberEditor thicknessVariationAmplitudeEditor = (JSpinner.NumberEditor) thicknessVariationAmplitudeSpinner
                    .getEditor();
            thicknessVariationAmplitudeEditor.getTextField()
                    .setHorizontalAlignment(SwingConstants.CENTER);
            thicknessVariationAmplitudeSpinner.setBorder(BorderFactory
                    .createTitledBorder("thickness amplitude"));

            thicknessVariationAmplitudeSpinner
                    .addChangeListener(new ChangeListener() {

                        @Override
                        public void stateChanged(ChangeEvent e) {
                            RandomVariationShaderUI.this.strokeThicknessVariationAmplitude = (thicknessVariationAmplitudeSpinner
                                    .getValue());
                            RandomVariationShaderUI.this.refresh();

                        }
                    });
            this.main.add(thicknessVariationAmplitudeSpinner);

            SliderWithSpinnerModel thicknessVariationWavelengthModel = new SliderWithSpinnerModel(
                    this.strokeThicknessVariationWavelength, 0.001, 100000, 10);
            final SliderWithSpinner thicknessVariationWavelengthSpinner = new SliderWithSpinner(
                    thicknessVariationWavelengthModel);
            JSpinner.NumberEditor thicknessVariationWavelengthEditor = (JSpinner.NumberEditor) thicknessVariationWavelengthSpinner
                    .getEditor();
            thicknessVariationWavelengthEditor.getTextField()
                    .setHorizontalAlignment(SwingConstants.CENTER);
            thicknessVariationWavelengthSpinner.setBorder(BorderFactory
                    .createTitledBorder("thickness Wavelength"));

            thicknessVariationWavelengthSpinner
                    .addChangeListener(new ChangeListener() {

                        @Override
                        public void stateChanged(ChangeEvent e) {
                            RandomVariationShaderUI.this.strokeThicknessVariationWavelength = (thicknessVariationWavelengthSpinner
                                    .getValue());
                            RandomVariationShaderUI.this.refresh();

                        }
                    });
            this.main.add(thicknessVariationWavelengthSpinner);
        }
        return this.main;
    }

    /**
     * set variable values from stroke texture expressive rendering object
     */
    @Override
    public void setValuesFromObject() {
        this.strokePressureVariationAmplitude = this.strtex
                .getStrokePressureVariationAmplitude();
        this.strokePressureVariationWavelength = this.strtex
                .getStrokePressureVariationWavelength();
        this.strokeShiftVariationAmplitude = this.strtex
                .getStrokeShiftVariationAmplitude();
        this.strokeShiftVariationWavelength = this.strtex
                .getStrokeShiftVariationWavelength();
        this.strokeThicknessVariationAmplitude = this.strtex
                .getStrokeThicknessVariationAmplitude();
        this.strokeThicknessVariationWavelength = this.strtex
                .getStrokeThicknessVariationWavelength();
    }

    /**
     * set variable values from stroke texture expressive rendering object
     */
    @Override
    public void setValuesToObject() {
        this.strtex
                .setStrokePressureVariationAmplitude(this.strokePressureVariationAmplitude);
        this.strtex
                .setStrokePressureVariationWavelength(this.strokePressureVariationWavelength);
        this.strtex
                .setStrokeShiftVariationAmplitude(this.strokeShiftVariationAmplitude);
        this.strtex
                .setStrokeShiftVariationWavelength(this.strokeShiftVariationWavelength);
        this.strtex
                .setStrokeThicknessVariationAmplitude(this.strokeThicknessVariationAmplitude);
        this.strtex
                .setStrokeThicknessVariationWavelength(this.strokeThicknessVariationWavelength);
    }

    protected void refresh() {
        this.setValuesToObject();
        this.parentProjectFrame.repaint();
    }

}
