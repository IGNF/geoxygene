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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ign.cogit.geoxygene.style.expressive.ParameterDescriptorTime;

/**
 * @author JeT
 * 
 */
public class ParameterUITime extends AbstractParameterUI {

    private ParameterDescriptorTime descriptor = null;
    private JPanel main;

    /**
     * @param descriptor
     */
    public ParameterUITime(ParameterDescriptorTime descriptor) {
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

            final JLabel timeLabel = new JLabel(String.valueOf(this.descriptor
                    .getValue()));
            timeLabel.setToolTipText(this.descriptor.getDescription());
            this.main.add(timeLabel, BorderLayout.CENTER);
            JButton updateButton = new JButton("update");
            updateButton.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    timeLabel.setText(String
                            .valueOf(ParameterUITime.this.descriptor.getValue()));
                    ParameterUITime.this.refresh();
                }
            });
            this.main.add(updateButton, BorderLayout.EAST);

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
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ui.ParameterUI#setValuesToObject()
     */
    @Override
    public void setValuesToObject() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ui.ParameterUI#getDescriptor()
     */
    @Override
    public ParameterDescriptorTime getDescriptor() {
        return this.descriptor;
    }

}
