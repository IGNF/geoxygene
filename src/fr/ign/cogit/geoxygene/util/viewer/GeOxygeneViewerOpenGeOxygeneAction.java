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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.Metadata;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * This class defines actions for access to GeOxygene repository (menu bar and
 * toolbar).
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

class GeOxygeneViewerOpenGeOxygeneAction implements ActionListener {

  private ObjectViewerInterface objectViewerInterface;
  private Geodatabase db;

  public GeOxygeneViewerOpenGeOxygeneAction(
      ObjectViewerInterface objViewerInterface, Geodatabase data) {
    this.objectViewerInterface = objViewerInterface;
    this.db = data;
  }

  public void actionPerformed(ActionEvent e) {
    String user = null;
    try {
      user = this.db.getConnection().getMetaData().getUserName();
    } catch (Exception exc) {
      exc.printStackTrace();
    }
    System.out.println(user + " est le plus beau !");
    List<String> classList = new ArrayList<String>();
    for (int i = 0; i < this.db.getMetadata().size(); i++) {
      Metadata md = this.db.getMetadata().get(i);
      Class<?> theClass = md.getJavaClass();
      if (FT_Feature.class.isAssignableFrom(theClass)
          && !Modifier.isAbstract(theClass.getModifiers())) {
        classList.add(theClass.getName());
      }
    }

    System.out.println("Selecting geographic classes ...");
    GeOxygeneFilter geOxyFilter = new GeOxygeneFilter(classList.toArray(), user);
    String[] selectedClasses = geOxyFilter
        .showDialog(this.objectViewerInterface);

    for (String selectedClasse : selectedClasses) {
      String themeName = selectedClasse.substring(selectedClasse
          .lastIndexOf(".") + 1);
      Class<?> theClass = null;
      try {
        theClass = Class.forName(selectedClasse);
      } catch (Exception exp) {
        System.out.println(exp.getMessage() + " : " + selectedClasse);
      }
      if (theClass != null) {
        System.out
            .println("Loading " + selectedClasse + " ... please wait ...");
        FT_FeatureCollection<?> coll = this.db.loadAllFeatures(theClass);
        System.out
            .println("   Loading finished. Displaying theme in viewer ...");
        this.objectViewerInterface.addAFeatureCollectionTheme(coll, themeName);
      }
    }
  }

}
