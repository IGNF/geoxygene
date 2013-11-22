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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @author JeT
 *         Status bar display in main frame footer
 */
public class JStatusBar {

    private static final Insets GRIDBAGINSETS = new Insets(0, 0, 0, 0);
    private JPanel mainPanel;
    private final List<JComponent> components = new ArrayList<JComponent>();
    private final List<Double> componentResizeWeights = new ArrayList<Double>();

    /**
     * constructor
     * 
     * @param mainFrame
     */
    public JStatusBar() {
    }

    /**
     * get status bar gui
     */
    public JComponent getGui() {
        return this.getMainPanel();
    }

    /**
     * Main panel containing all user added components
     * 
     * @return
     */
    private JPanel getMainPanel() {
        if (this.mainPanel == null) {
            this.mainPanel = new JPanel(new GridBagLayout());
        }
        return this.mainPanel;
    }

    /**
     * create a new component container
     * 
     * @param component
     * @return
     */
    private JPanel getComponentContainer(JComponent component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLoweredBevelBorder());
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    /**
     * add a user component to the status bar
     * 
     * @param component
     * @param resizeWeight
     */
    public void addStatusBarComponent(final JComponent component, double resizeWeight) {
        this.components.add(component);
        this.componentResizeWeights.add(resizeWeight);
        this.generateComponentsGui();
    }

    /**
     * remove a user component to the status bar
     * 
     * @param component
     */
    public void removeStatusBarComponent(final JComponent component) {
        int index = this.components.indexOf(component);
        if (index >= 0) {
            this.components.remove(index);
            this.componentResizeWeights.remove(index);
            this.generateComponentsGui();
        }
    }

    /**
     * generate the content of the main panel using added components
     */
    private void generateComponentsGui() {
        this.getMainPanel().removeAll();
        for (int componentIndex = 0; componentIndex < this.components.size(); componentIndex++) {
            this.getMainPanel().add(this.getComponentContainer(this.components.get(componentIndex)),
                    constraint(componentIndex + 1, this.componentResizeWeights.get(componentIndex)));
        }
    }

    /**
     * define a simple constraints for GridBagLayout
     * 
     * @param gridx
     *            X position of the component
     * @param weightx
     *            resize weight of the component
     * @return
     */
    static private GridBagConstraints constraint(int gridx, double weightx) {
        return new GridBagConstraints(gridx, 1, 1, 1, weightx, 1., GridBagConstraints.CENTER, GridBagConstraints.BOTH, GRIDBAGINSETS, 1, 1);
    }
}
