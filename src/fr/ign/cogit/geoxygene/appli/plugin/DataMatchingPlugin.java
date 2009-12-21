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

package fr.ign.cogit.geoxygene.appli.plugin;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;

/**
 * @author Julien Perret
 *
 */
public class DataMatchingPlugin implements ActionListener {
	public void initialize(GeOxygeneApplication application) {
		Menu menu = new Menu(I18N.getString("fr.ign.cogit.geoxygene.DataMatchingPlugin.DataMatching")); //$NON-NLS-1$
		MenuItem menuItem = new MenuItem(I18N.getString("fr.ign.cogit.geoxygene.DataMatchingPlugin.OpenDataMatchingEnvironment")); //$NON-NLS-1$
		menuItem.addActionListener(this);
		menu.add(menuItem);
		application.getFrame().getMenuBar().add(menu);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
}
