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

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.style.expressive.DefaultLineShaderDescriptor;

/**
 * @author JeT
 * 
 */
public class DefaultLineShaderUI implements GenericParameterUI {

    private JPanel main = null;
    private ProjectFrame parentProjectFrame = null;
    private DefaultLineShaderDescriptor strtex = null;

    /**
     * Constructor
     */
    public DefaultLineShaderUI(DefaultLineShaderDescriptor shaderDescriptor,
            ProjectFrame projectFrame) {
        this.parentProjectFrame = projectFrame;
        this.strtex = shaderDescriptor;
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
            this.main.setLayout(new FlowLayout());
            this.main.setBorder(BorderFactory
                    .createEtchedBorder(EtchedBorder.LOWERED));
            this.main.add(new JLabel("Default Shader type has no parameters"));
        }
        return this.main;
    }

    /**
     * set variable values from stroke texture expressive rendering object
     */
    @Override
    public void setValuesFromObject() {
    }

    /**
     * set variable values from stroke texture expressive rendering object
     */
    @Override
    public void setValuesToObject() {
    }

    protected void refresh() {
        this.setValuesToObject();
        this.parentProjectFrame.repaint();
    }

}
