/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package fr.ign.cogit.geoxygene.util.viewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import uk.ac.leeds.ccg.geotools.Theme;

/**
 * This class implements clickable theme buttons of the (Geo)Object viewer.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

class ObjectViewerThemeButton extends JToggleButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4702754757694872148L;
	private ObjectViewerInterface objectViewerInterface;
	private ObjectViewerThemeProperties themeProperties;

	public ObjectViewerThemeButton(ObjectViewerInterface objectViewerInterface,	ObjectViewerThemeProperties themeProp) {

		super(themeProp.getObjectViewerTheme().getName(), themeProp.isActive());
		URL imageUrl;

		final Theme theme = themeProp.getObjectViewerTheme();
		boolean themeIsVisible = themeProp.isVisible();
		//boolean themeIsActive = themeProp.isActive();

		if (themeIsVisible) {
			imageUrl = this.getClass().getResource("images/checked.gif");
			setIcon(new ImageIcon(imageUrl));
		} else {
			imageUrl = this.getClass().getResource("images/unchecked.gif");
			setIcon(new ImageIcon(imageUrl));
		}

		this.objectViewerInterface = objectViewerInterface;
		this.themeProperties = themeProp;

		setHorizontalAlignment(SwingConstants.LEFT);

		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getThemeProperties().isActive()) {
					//System.out.println("Theme unpressed !");
					getObjectViewerInterface().getActiveThemes().remove(theme);
					getThemeProperties().setActive(false);
					theme.setSelectionManager(null);
					theme.setHighlightManager(null);
				} else {
					//System.out.println("Theme pressed !");
					getObjectViewerInterface().getActiveThemes().add(theme);
					getThemeProperties().setActive(true);
					theme.setSelectionManager(getThemeProperties().getThemeSelectionManager());
					theme.setHighlightManager(getThemeProperties().getThemeHighlightManager());
				}
			}
		});

	}


	public ObjectViewerInterface getObjectViewerInterface() {
		return objectViewerInterface;
	}

	public ObjectViewerThemeProperties getThemeProperties() {
		return themeProperties;
	}

}
