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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ign.cogit.geoxygene.appli.render.primitive.DensityFieldPrimitiveRenderer;
import fr.ign.cogit.geoxygene.appli.render.primitive.DensityFieldPrimitiveRenderer.DensityFieldVisualizationType;
import fr.ign.util.ui.JImageBrowser;

/**
 * @author JeT
 *         User interface associated with a density field primitive renderer
 */
public class DensityFieldPrimitiveRendererUI extends AbstractPrimitiveRendererUI implements ChangeListener {

    private static final int ANCHOR = GridBagConstraints.FIRST_LINE_START;
    private static final int FILL = GridBagConstraints.NONE;
    private static Insets INSETS = new Insets(2, 2, 2, 2);
    private DensityFieldPrimitiveRenderer renderer = null;
    private JPanel mainPanel = null;
    private JPanel visuButtonPanel = null;
    private JCheckBox heightCheckBox = null;
    private JCheckBox uvCheckBox = null;
    private JCheckBox texturedCheckBox = null;
    private ButtonGroup visuButtonGroup = null;
    private JImageBrowser imageBrowser = null;

    /**
     * 
     */
    public DensityFieldPrimitiveRendererUI(DensityFieldPrimitiveRenderer renderer) {
        this.renderer = renderer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.primitive.ui.PrimitiveRendererUI#
     * getPrimitiveRenderer()
     */
    @Override
    public DensityFieldPrimitiveRenderer getPrimitiveRenderer() {
        return this.renderer;
    }

    @Override
    public JPanel getMainPanel() {
        if (this.mainPanel == null) {
            this.mainPanel = new JPanel();
            this.mainPanel.setLayout(new GridBagLayout());
            this.mainPanel.add(this.getVisuButtonPanel(), new GridBagConstraints(0, 1, 1, 1, 1., 0., ANCHOR, FILL, INSETS, 0, 0));
            this.mainPanel.add(this.getImageBrowser());
        }
        return this.mainPanel;
    }

    private JImageBrowser getImageBrowser() {
        if (this.imageBrowser == null) {
            this.imageBrowser = new JImageBrowser();
            this.imageBrowser.setBorder(BorderFactory.createTitledBorder("texture image"));
            // TODO: listen to image changes
        }
        return this.imageBrowser;
    }

    private JPanel getVisuButtonPanel() {
        if (this.visuButtonPanel == null) {
            this.visuButtonPanel = new JPanel(new GridLayout(0, 1));
            this.visuButtonPanel.setBorder(BorderFactory.createTitledBorder("visualization type"));
            this.visuButtonPanel.add(this.getHeightCheckBox());
            this.visuButtonPanel.add(this.getUvCheckBox());
            this.visuButtonPanel.add(this.getTexturedCheckBox());
        }
        return this.visuButtonPanel;
    }

    /**
     * @return the texturedCheckBox
     */
    public JCheckBox getTexturedCheckBox() {
        if (this.texturedCheckBox == null) {
            this.texturedCheckBox = new JCheckBox("Applied texture");
            this.getVisuButtonGroup().add(this.texturedCheckBox);
            this.texturedCheckBox.addChangeListener(this);
        }
        return this.texturedCheckBox;
    }

    /**
     * @return the heightCheckBox
     */
    public JCheckBox getHeightCheckBox() {
        if (this.heightCheckBox == null) {
            this.heightCheckBox = new JCheckBox("Height Gray gradient");
            this.getVisuButtonGroup().add(this.heightCheckBox);
            this.heightCheckBox.addChangeListener(this);
        }
        return this.heightCheckBox;
    }

    /**
     * @return the uvCheckBox
     */
    public JCheckBox getUvCheckBox() {
        if (this.uvCheckBox == null) {
            this.uvCheckBox = new JCheckBox("(u,v) coordinates");
            this.getVisuButtonGroup().add(this.uvCheckBox);
            this.uvCheckBox.addChangeListener(this);
        }
        return this.uvCheckBox;
    }

    private ButtonGroup getVisuButtonGroup() {
        if (this.visuButtonGroup == null) {
            this.visuButtonGroup = new ButtonGroup();
        }
        return this.visuButtonGroup;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == this.getUvCheckBox() && this.getUvCheckBox().isSelected()) {
            this.getPrimitiveRenderer().setVisualizationType(DensityFieldVisualizationType.UV);
            this.getPrimitiveRenderer().invalidateTextures();
            this.getMainPanel().repaint();
        }
        if (e.getSource() == this.getTexturedCheckBox() && this.getTexturedCheckBox().isSelected()) {
            this.getPrimitiveRenderer().setVisualizationType(DensityFieldVisualizationType.TEXTURE);
            this.getPrimitiveRenderer().invalidateTextures();
            this.getMainPanel().repaint();
        }
        if (e.getSource() == this.getHeightCheckBox() && this.getHeightCheckBox().isSelected()) {
            this.getPrimitiveRenderer().setVisualizationType(DensityFieldVisualizationType.HEIGHT);
            this.getPrimitiveRenderer().invalidateTextures();
            this.getMainPanel().repaint();
        }
    }
}
