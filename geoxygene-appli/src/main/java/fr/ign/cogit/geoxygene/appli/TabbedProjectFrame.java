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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.mode.GeometryToolBar;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

/**
 * Project Frame.
 * 
 * @author Julien Perret
 */
public class TabbedProjectFrame extends AbstractProjectFrame implements ActionListener {

    private GeometryToolBar geometryToolBar = null;
    /** The default frame width. */
    private static final int DEFAULT_WIDTH = 600;
    /** The default frame height. */
    private static final int DEFAULT_HEIGHT = 400;
    /** The default frame divider location. */
    private JPanel internalPanel = null; // internal frame containing the gui
    private JMenuBar menuBar = null;

    /**
     * Constructor.
     * 
     * @param iconImage
     *            the project icon image
     */
    public TabbedProjectFrame(final TabbedMainFrame frame, LayerViewPanel layerViewPanel, final ImageIcon iconImage) {
        super(frame, layerViewPanel, iconImage);

        ShapefileReader.addActionListener(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ProjectFrame#getMainFrame()
     */
    @Override
    public TabbedMainFrame getMainFrame() {
        return (TabbedMainFrame) super.getMainFrame();
    }

    /**
     * @return the internalFrame
     */
    public JPanel getPanel() {
        if (this.internalPanel == null) {
            this.internalPanel = new JPanel();
            // Setting the tool tip text to the frame and its sub components
            // this.internalPanel.setToolTipText(this.getTitle());
            this.internalPanel.setSize(TabbedProjectFrame.DEFAULT_WIDTH, TabbedProjectFrame.DEFAULT_HEIGHT);
            // this.internalPanel.setFrameIcon(this.getIconImage());
            // this.internalFrame.getDesktopIcon().setToolTipText(this.getTitle());
            // for (Component c : internalFrame.getDesktopIcon().getComponents()) {
            // if (c instanceof JComponent) {
            // ((JComponent) c).setToolTipText(this.getTitle());
            // }
            // }
            this.internalPanel.setLayout(new BorderLayout());
            this.internalPanel.add(this.getSplitPane(), BorderLayout.CENTER);
        }
        return this.internalPanel;
    }

    @Override
    public void setGeometryToolsVisible(final boolean b) {
        if (b) {
            if (this.geometryToolBar == null) {
                this.geometryToolBar = new GeometryToolBar(this);
                this.getPanel().add(this.geometryToolBar, BorderLayout.EAST);
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
        super.dispose();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
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
        return this.getPanel();
    }

    @Override
    public void setMenuBar(final JMenuBar menuBar) {
        this.menuBar = menuBar;
        this.internalPanel.add(menuBar, BorderLayout.NORTH);
    }

    @Override
    public JMenuBar getMenuBar() {
        return this.menuBar;
    }

    @Override
    public void setSize(final int x, final int y) {
        this.getPanel().setSize(x, y);
    }

    @Override
    public void setLocation(final int x, final int y) {
        this.getPanel().setLocation(x, y);

    }

    @Override
    public void addComponentInProjectFrame(JComponent component, String layout) {
        this.getGui().add(component, layout);
    }
}
