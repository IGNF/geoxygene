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
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.ign.cogit.geoxygene.style.expressive.ParameterDescriptorColor;

/**
 * @author JeT
 * 
 */
public class ParameterUIColor extends AbstractParameterUI {

    private Color value;
    private ParameterDescriptorColor descriptor = null;
    private JPanel main;

    /**
     * @param descriptor
     */
    public ParameterUIColor(ParameterDescriptorColor descriptor) {
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
            final JButton colorButton = new JButton();
            colorButton.setSize(20, 20);

            colorButton.setToolTipText(this.descriptor.getDescription());
            colorButton.setBackground(this.value);

            colorButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    Color newColor = JColorChooser.showDialog(null, "Choose "
                            + ParameterUIColor.this.descriptor.getName()
                            + " Color", ParameterUIColor.this.value);
                    if (newColor != null) {
                        ParameterUIColor.this.value = newColor;
                        colorButton.setBackground(newColor);
                        ParameterUIColor.this.refresh();
                    }
                }
            });
            this.main.add(colorButton, BorderLayout.EAST);
            this.main.add(new JLabel(this.descriptor.getName()),
                    BorderLayout.CENTER);

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
    public ParameterDescriptorColor getDescriptor() {
        return this.descriptor;
    }

}
