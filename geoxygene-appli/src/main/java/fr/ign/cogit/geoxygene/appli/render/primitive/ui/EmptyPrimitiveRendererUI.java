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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.ign.cogit.geoxygene.appli.render.primitive.PrimitiveRenderer;

/**
 * @author JeT
 *         User interface associated with a density field primitive renderer
 */
public class EmptyPrimitiveRendererUI extends AbstractPrimitiveRendererUI {

    private PrimitiveRenderer renderer = null;
    private JPanel mainPanel = null;

    /**
     * 
     */
    public EmptyPrimitiveRendererUI(PrimitiveRenderer renderer) {
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
    public PrimitiveRenderer getPrimitiveRenderer() {
        return this.renderer;
    }

    @Override
    public JPanel getMainPanel() {
        if (this.mainPanel == null) {
            this.mainPanel = new JPanel(new BorderLayout());
            String rendererClassname = this.getPrimitiveRenderer() == null ? "null" : this.getPrimitiveRenderer().getClass().getSimpleName();
            this.mainPanel.add(new JLabel("no ui for renderer " + rendererClassname), BorderLayout.CENTER);
        }
        return this.mainPanel;
    }
}
