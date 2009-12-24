/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

/**
 * @author Julien Perret
 */
public class LayerLegendPanel extends JPanel implements ChangeListener {
        /**
         * serial version uid.
         */
        private static final long serialVersionUID = -6860364246334166387L;
        /**
         * sld of the layer legend panel.
         */
        private StyledLayerDescriptor sld = null;

        /**
         *
         * @param theSld sld of the layer legend panel.
         */
        public LayerLegendPanel(final StyledLayerDescriptor theSld) {
                super();
                this.sld = theSld;
                this.sld.addChangeListener(this);
        }

        @Override
        public final void stateChanged(final ChangeEvent e) {
                this.update();
        }

        /**
         * Update and repaint the layer legend panel.
         */
        private void update() {
                this.removeAll();
                for (Layer layer : this.sld.getLayers()) {
                        this.add(new JLabel(layer.getName()));
                }
                this.validate();
                this.repaint();
        }
}
