/*
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
 */

package fr.ign.cogit.geoxygene.util.viewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;

/**
 * This class defines the menu bar of the ObjectViewer's GUI.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

class ObjectViewerMenuBar extends JMenuBar {

  /**
	 * 
	 */
  private static final long serialVersionUID = -3309139662086042655L;
  private ObjectViewerInterface objectViewerInterface;

  public ObjectViewerMenuBar(ObjectViewerInterface objectViewerInterface,
      Geodatabase db) {

    this.objectViewerInterface = objectViewerInterface;

    // "File" Menu
    JMenu file = new JMenu("File");

    // Item "Open"
    JMenuItem item = new JMenuItem("Open");
    item.addActionListener(new GeOxygeneViewerOpenFileAction(
        objectViewerInterface));
    file.add(item);

    // Item "GeOxygene"
    if (db != null) {
      item = new JMenuItem("GeOxygene");
      item.addActionListener(new GeOxygeneViewerOpenGeOxygeneAction(
          objectViewerInterface, db));
      file.add(item);
    }

    file.addSeparator();

    // Item "Close"
    item = new JMenuItem("Close");
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // System.out.println("On ferme la fenetre !!! ;-))");
        ObjectViewerMenuBar.this.getObjectViewerInterface().dispose();
      }
    });
    file.add(item);

    // Item "Exit"
    item = new JMenuItem("Exit");
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // System.out.println("On ferme la fenetre !!! ;-))");
        ObjectViewerMenuBar.this.getObjectViewerInterface().dispose();
        System.exit(0);
      }
    });
    file.add(item);

    // Add the "File" menu to the menubar
    this.add(file);

  }

  public ObjectViewerInterface getObjectViewerInterface() {
    return this.objectViewerInterface;
  }

}
