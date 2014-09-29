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
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ign.cogit.geoxygene.style.expressive.ParameterDescriptorInteger;
import fr.ign.util.ui.SliderWithSpinner;
import fr.ign.util.ui.SliderWithSpinner.SliderWithSpinnerModel;

/**
 * @author JeT
 * 
 */
public class ParameterUIInteger extends AbstractParameterUI {

    private int value;
    private ParameterDescriptorInteger descriptor = null;
    private JPanel main;

    /**
     * @param descriptor
     */
    public ParameterUIInteger(ParameterDescriptorInteger descriptor) {
        super();
        this.descriptor = descriptor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ui.ParameterUI#getGui()
     */
    @Override
    public Component getGui() {
        if (this.main == null) {
            this.main = new JPanel(new BorderLayout());
            SliderWithSpinnerModel model = new SliderWithSpinnerModel(
                    this.value, this.descriptor.getMin(),
                    this.descriptor.getMax(), this.descriptor.getIncrement());
            final SliderWithSpinner spinner = new SliderWithSpinner(model);
            JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner
                    .getEditor();
            editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
            spinner.setBorder(BorderFactory.createTitledBorder(this.descriptor
                    .getName()));
            spinner.setToolTipText(this.descriptor.getDescription());
            spinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    ParameterUIInteger.this.value = (int) (double) (spinner
                            .getValue());
                    ParameterUIInteger.this.refresh();
                }
            });
            this.main.add(spinner, BorderLayout.CENTER);

        }
        return this.main;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ui.ParameterUI#setValuesFromObject()
     */
    @Override
    public void setValuesFromObject() {
        this.value = this.getDescriptor().getValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ui.ParameterUI#setValuesToObject()
     */
    @Override
    public void setValuesToObject() {
        this.getDescriptor().setValue(this.value);

    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ui.ParameterUI#getDescriptor()
     */
    @Override
    public ParameterDescriptorInteger getDescriptor() {
        return this.descriptor;
    }

}
