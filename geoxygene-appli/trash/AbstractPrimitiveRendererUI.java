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

package fr.ign.cogit.geoxygene.appli.render.primitive.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author JeT
 *         Common implementation of the primitive renderer UI
 */
public abstract class AbstractPrimitiveRendererUI implements PrimitiveRendererUI {

    private final Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();
    private JPanel mainPanel = null;
    private JButton applyButton = null;

    /**
     * 
     * @return
     */
    public abstract JComponent getMainPanel();

    /**
     * Main panel integrated with somee buttons
     */
    @Override
    public final JPanel getGUI() {
        if (this.mainPanel == null) {
            this.mainPanel = new JPanel(new BorderLayout());
            JPanel toolPanel = new JPanel();
            toolPanel.add(this.getApplyButton());
            this.mainPanel.add(toolPanel, BorderLayout.SOUTH);
            this.mainPanel.add(this.getMainPanel(), BorderLayout.CENTER);
        }
        return this.mainPanel;
    }

    /**
     * @return the applyButton
     */
    private JButton getApplyButton() {
        if (this.applyButton == null) {
            this.applyButton = new JButton("apply");
            this.applyButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    AbstractPrimitiveRendererUI.this.fireChangeEvent();
                }
            });
        }
        return this.applyButton;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.primitive.ui.PrimitiveRendererUI#
     * addChangeListener(javax.swing.event.ChangeListener)
     */
    @Override
    public boolean addChangeListener(ChangeListener listener) {
        return this.changeListeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.primitive.ui.PrimitiveRendererUI#
     * removeChangeListener(javax.swing.event.ChangeListener)
     */
    @Override
    public boolean removeChangeListener(ChangeListener listener) {
        return this.changeListeners.remove(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.primitive.ui.PrimitiveRendererUI#
     * clearChangeListener(javax.swing.event.ChangeListener)
     */
    @Override
    public void clearChangeListener(ChangeListener listener) {
        this.changeListeners.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.primitive.ui.PrimitiveRendererUI#
     * fireChangeEvent(javax.swing.event.ChangeEvent)
     */
    @Override
    public void fireChangeEvent() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener listener : this.changeListeners) {
            listener.stateChanged(event);
        }

    }
}
