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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import uk.ac.leeds.ccg.geotools.Theme;

/**
  * This class defines the PopMenu for the ThemeButton of the ObjectViewer's GUI.
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

class ObjectViewerThemePopupMenu extends JPopupMenu {
	//Default

	//Public variables
	public ObjectViewerInterface objectViewerInterface;
	public ObjectViewerThemeButton objectViewerThemeButton;
	public ObjectViewerThemeProperties objectViewerThemeProperties;

	public ObjectViewerThemePopupMenu(
		ObjectViewerInterface objectViewerInterface,
		ObjectViewerThemeButton objectViewerThemeButton,
		ObjectViewerThemeProperties objectViewerThemeProperties) {

		super();

		setObjectViewerInterface(objectViewerInterface);
		setObjectViewerThemeButton(objectViewerThemeButton);
		setObjectViewerThemeProperties(objectViewerThemeProperties);

		addDefaultItems();
	}

	private void addDefaultItems() {
		URL imageUrl;
		imageUrl = this.getClass().getResource("images/checked.gif");
		final Icon checked = new ImageIcon(imageUrl);
		imageUrl = this.getClass().getResource("images/unchecked.gif");
		final Icon unchecked = new ImageIcon(imageUrl);
		
		ButtonGroup rbGroup = new ButtonGroup();
		JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("Visible");
		if (getObjectViewerThemeProperties().isVisible()) rbMenuItem.setSelected(true);
		rbGroup.add(rbMenuItem);
		rbMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getObjectViewerThemeButton()
					.getIcon()
					.toString()
					.equals(unchecked.toString())) {
					getObjectViewerThemeButton().setIcon(checked);
					getObjectViewerThemeProperties().setVisible(true);
					getObjectViewerInterface().view.setThemeIsVisible(
						getObjectViewerThemeProperties().getObjectViewerTheme(),
						true,
						true);
				}
			}
		});
		add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("Unvisible");
		if (!getObjectViewerThemeProperties().isVisible()) rbMenuItem.setSelected(true);
		rbGroup.add(rbMenuItem);
		rbMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getObjectViewerThemeButton()
					.getIcon()
					.toString()
					.equals(checked.toString())) {
					getObjectViewerThemeButton().setIcon(unchecked);
					getObjectViewerThemeProperties().setVisible(false);
					getObjectViewerInterface().view.setThemeIsVisible(
						getObjectViewerThemeProperties().getObjectViewerTheme(),
						false,
						true);
				}
			}
		});
		add(rbMenuItem);
		
		JMenuItem menuItem = new JMenuItem("Up");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Theme t = getObjectViewerThemeProperties().getObjectViewerTheme();
				getObjectViewerInterface().upTheme(t);
			}
		});
		add(menuItem);

		menuItem = new JMenuItem("Down");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Theme t = getObjectViewerThemeProperties().getObjectViewerTheme();
				getObjectViewerInterface().downTheme(t);
			}
		});
		add(menuItem);
				
		menuItem = new JMenuItem("Remove");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getObjectViewerInterface().removeTheme(getObjectViewerThemeProperties());
			}
		});
		add(menuItem);

		menuItem = new JMenuItem("Show attributes");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Showing theme attributes ...");

				final ObjectViewerAttributesTableFrame themeAttributesFrame =
					new ObjectViewerAttributesTableFrame(
						getObjectViewerThemeProperties().getObjectViewerTheme(),
						getObjectViewerThemeProperties().getDataSourceType(),
						getObjectViewerThemeProperties().getDataSource()										
							);
																		
				themeAttributesFrame.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						themeAttributesFrame.dispose();
					}
				});
				themeAttributesFrame.setResizable(true);
				themeAttributesFrame.pack();
				themeAttributesFrame.setVisible(true);
			}
		});
		add(menuItem);

		addSeparator();

		menuItem = new JMenuItem("Properties");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//final JFrame ccframe = new ObjectViewerColorChooser();
				//final ObjectViewerThemePropertiesFrame themePropertiesFrame = new ObjectViewerThemePropertiesFrame(objectViewerThemeProperties.getFillInThemeColor(),objectViewerThemeProperties.getOutlineThemeColor());
				final ObjectViewerThemePropertiesFrame themePropertiesFrame =
					new ObjectViewerThemePropertiesFrame(objectViewerThemeProperties);
				themePropertiesFrame.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						themePropertiesFrame.dispose();
						//System.exit(0);
					}
				});
				themePropertiesFrame.setResizable(false);
				themePropertiesFrame.pack();
				themePropertiesFrame.setVisible(true);
			}
		});
		add(menuItem);

	}

	public void setObjectViewerInterface(ObjectViewerInterface objectViewerInterface) {
		this.objectViewerInterface = objectViewerInterface;
	}

	public ObjectViewerInterface getObjectViewerInterface() {
		return objectViewerInterface;
	}

	public void setObjectViewerThemeButton(ObjectViewerThemeButton objectViewerThemeButton) {
		this.objectViewerThemeButton = objectViewerThemeButton;
	}

	public ObjectViewerThemeButton getObjectViewerThemeButton() {
		return objectViewerThemeButton;
	}

	public void setObjectViewerThemeProperties(ObjectViewerThemeProperties objectViewerThemeProperties) {
		this.objectViewerThemeProperties = objectViewerThemeProperties;
	}

	public ObjectViewerThemeProperties getObjectViewerThemeProperties() {
		return objectViewerThemeProperties;
	}

}