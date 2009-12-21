/*******************************************************************************
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
 *******************************************************************************/
package fr.ign.cogit.geoxygene.util.loader.gui;

/**
 * Sélecteur de shapefiles simple permettant la sélection simple ou multiple.
 * @author Julien Perret
 *
 */

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

public class GUIShapefileChoice extends JFrame {
	private static final long serialVersionUID = 1L;
	static Logger logger=Logger.getLogger(GUIShapefileChoice.class.getName());

	/**
	 * Dernier dossier ouvert
	 */
	private String directoryPath;
	private boolean multiSelectionEnabled = true;
	/**
	 * Constructeurs de sélecteur de shapefiles
	 * @param MultiSelectionEnabled
	 */
	public GUIShapefileChoice(boolean MultiSelectionEnabled) {
		this.multiSelectionEnabled = MultiSelectionEnabled;
		directoryPath = null;
	}
	
	public GUIShapefileChoice(boolean MultiSelectionEnabled, String latestFolder) {
		this.multiSelectionEnabled = MultiSelectionEnabled;
		directoryPath = latestFolder;
	}
	/**
	 * Ouvre une fenêtre de sélection et renvoie un tableau contenant les fichiers sélectionnés.
	 * Ne permet que la sélection de fichiers.
	 * @return un tableau contenant les fichiers sélectionnés. null si rien n'a été sélectionné
	 */
	public File[] getSelectedFiles() {return getSelectedFilesOrDirectories(true);}
	/**
	 * Ouvre une fenêtre de sélection et renvoie un tableau contenant les répertoires sélectionnés.
	 * Ne permet que la sélection de répertoires.
	 * @return un tableau contenant les répertoires sélectionnés. null si rien n'a été sélectionné
	 */
	public File[] getSelectedDirectories() {return getSelectedFilesOrDirectories(false);}
	/**
	 * Ouvre une fenêtre de sélection et renvoie un tableau contenant les fichiers ou répertoires sélectionnés.
	 * @param selectFiles si vrai, ne permet que la sélection de fichiers, si faux que la sélection de répertoires.
	 * @return un tableau contenant les fichiers ou répertoires sélectionnés. null si rien n'a été sélectionné
	 */
	public File[] getSelectedFilesOrDirectories(boolean selectFiles) {
		JFileChooser chooser = new JFileChooser(directoryPath);
		
		if (selectFiles) {
			FileNameExtensionFilter filter = new FileNameExtensionFilter("ESRI Shapefiles","shp");
			chooser.setFileFilter(filter);		    
		} else {
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		chooser.setMultiSelectionEnabled(multiSelectionEnabled);
		
		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File[] files = chooser.getSelectedFiles();
			
			if (logger.isTraceEnabled()) for(int i = 0; i < files.length ; i++)
				logger.trace("You chose this "+(selectFiles?"file :":" directory ") + files[i].getName());
			return files;
		}
		return null;
	}
}
