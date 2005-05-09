/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
  * This class defines the status bar of the ObjectViewer's GUI.
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

class ObjectViewerStatusBar extends JPanel {
	//Default
	public static final String DEFAULT_STATUS = "Ready.";

	//Public variables
	public ObjectViewerInterface objectViewerInterface;
	private JLabel statusJLabel;

	public ObjectViewerStatusBar(ObjectViewerInterface objectViewerInterface) {

		this.objectViewerInterface = objectViewerInterface;
        statusJLabel=new JLabel(DEFAULT_STATUS, JLabel.LEFT);
		add(statusJLabel, BorderLayout.WEST);
	}

	public void setObjectViewerInterface(ObjectViewerInterface objectViewerInterface) {
		this.objectViewerInterface = objectViewerInterface;
	}

	public ObjectViewerInterface getObjectViewerInterface() {
		return objectViewerInterface;
	}

    public void setText(String text) {
		statusJLabel.setText(text);
    }
    
	/**
	 * @return the Label of the status bar.
	 */
	public JLabel getStatusJLabel() {
		return statusJLabel;
	}

	/**
	 * @param label
	 */
	public void setStatusJLabel(JLabel label) {
		statusJLabel = label;
	}

}