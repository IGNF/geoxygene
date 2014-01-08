/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JTextArea;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.api.MainFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.mode.GeometryToolBar;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

/**
 * Project Frame.
 * 
 * @author Julien Perret
 */
public class FloatingProjectFrame extends AbstractProjectFrame implements ActionListener {

    private GeometryToolBar geometryToolBar = null;

    /** The default frame width. */
    private static final int DEFAULT_WIDTH = 600;

    /** The default frame height. */
    private static final int DEFAULT_HEIGHT = 400;

    /** internal frame. */
    private JInternalFrame internalFrame = null;

    // containing the

    /**
     * Constructor.
     * 
     * @param iconImage
     *            the project icon image
     * @param layerViewPanel
     */
    public FloatingProjectFrame(final MainFrame frame, final LayerViewPanel layerViewPanel, final ImageIcon iconImage) {
        super(frame, layerViewPanel, iconImage);
        ShapefileReader.addActionListener(this);
    }

    /**
     * @return the internalFrame
     */
    public JInternalFrame getInternalFrame() {
        if (this.internalFrame == null) {
            this.internalFrame = new JInternalFrame(this.getTitle(), true, true, true, true);
            // Setting the tool tip text to the frame and its sub components
            this.internalFrame.setToolTipText(this.internalFrame.getTitle());
            this.internalFrame.setSize(FloatingProjectFrame.DEFAULT_WIDTH, FloatingProjectFrame.DEFAULT_HEIGHT);
            this.internalFrame.setFrameIcon(this.getIconImage());
            this.internalFrame.getDesktopIcon().setToolTipText(this.getTitle());
            for (Component c : this.internalFrame.getDesktopIcon().getComponents()) {
                if (c instanceof JComponent) {
                    ((JComponent) c).setToolTipText(this.getTitle());
                }
            }
            this.internalFrame.getContentPane().setLayout(new BorderLayout());
            this.internalFrame.getContentPane().add(this.getSplitPane(), BorderLayout.CENTER);
            this.internalFrame.setVisible(true);

        }
        return this.internalFrame;
    }

    @Override
    public void setGeometryToolsVisible(boolean b) {
        if (b) {
            if (this.geometryToolBar == null) {
                this.geometryToolBar = new GeometryToolBar(this);
                this.getInternalFrame().getContentPane().add(this.geometryToolBar, BorderLayout.EAST);
            }
            this.geometryToolBar.setVisible(true);
            this.validate();
        } else {
            this.geometryToolBar.setVisible(false);
            this.validate();
        }
    }

    /** Dispose of the frame an its {@link LayerViewPanel}. */
    @Override
    public final void dispose() {
        this.getLayerViewPanel().dispose();
        this.getInternalFrame().dispose();
        super.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getID() == 2) {
            // loading finished
            IPopulation<?> p = (IPopulation<?>) e.getSource();
            Layer l = this.getLayer(p.getNom());
            this.getLayerViewPanel().getRenderingManager().render(this.getLayerViewPanel().getRenderingManager().getRenderer(l));
            this.getLayerViewPanel().superRepaint();
        }
    }

    @Override
    public JComponent getGui() {
        return this.getInternalFrame();
    }

    @Override
    public void setMenuBar(JMenuBar menuBar) {
        this.getInternalFrame().setJMenuBar(menuBar);
    }

    @Override
    public JMenuBar getMenuBar() {
        return this.getInternalFrame().getJMenuBar();
    }

    @Override
    public void setSize(int x, int y) {
        this.getInternalFrame().setSize(x, y);
    }

    @Override
    public void setLocation(int x, int y) {
        this.getInternalFrame().setLocation(x, y);

    }

    @Override
    public void addComponentInProjectFrame(JComponent component, String layout) {
        this.getInternalFrame().getContentPane().add(component, layout);
    }
}
