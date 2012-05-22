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

package fr.ign.cogit.geoxygene.appli.plugin;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.contrib.delaunay.Triangulation;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_CubicSpline;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * Triangulation plugin.
 * @author Julien Perret
 */
public class CubicSplinePlugin implements GeOxygeneApplicationPlugin,
    ActionListener {
  /**
   * Logger.
   */
  static Logger logger = Logger.getLogger(Triangulation.class.getName());

  private GeOxygeneApplication application = null;

  /**
   * Initialize the plugin.
   * @param application the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication application) {
    this.application = application;
    JMenu menu = null;
    for (Component c : application.getFrame().getJMenuBar().getComponents()) {
      if (c instanceof JMenu) {
        JMenu aMenu = (JMenu) c;
        if (aMenu.getText() != null
            && aMenu.getText().equalsIgnoreCase("Curve")) { //$NON-NLS-1$
          menu = aMenu;
        }
      }
    }
    if (menu == null) {
      menu = new JMenu("Curve");//$NON-NLS-1$
    }
    JMenuItem menuItem = new JMenuItem("Cubic Spline" //$NON-NLS-1$
    );
    menuItem.addActionListener(this);
    menu.add(menuItem);
    application.getFrame().getJMenuBar()
        .add(menu, application.getFrame().getJMenuBar().getMenuCount() - 2);
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    ProjectFrame project = this.application.getFrame()
        .getSelectedProjectFrame();
    Set<Layer> selectedLayers = project.getLayerLegendPanel()
        .getSelectedLayers();
    if (selectedLayers.size() != 1) {
      CubicSplinePlugin.logger
          .error("You need to select one (and only one) layer."); //$NON-NLS-1$
      return;
    }
    Layer layer = selectedLayers.iterator().next();
    String tangentMethod = (String) JOptionPane
        .showInputDialog(CubicSplinePlugin.this.application.getFrame(),
            "Choose a Tangent Method", //$NON-NLS-1$
            "Tangent Method", JOptionPane.QUESTION_MESSAGE, null, //$NON-NLS-1$
            new String[] {
                "finiteDifference", "cardinalSpline", "kochanekBartels" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "kochanekBartels"); //$NON-NLS-1$
    double tension = 0.0;
    double bias = 0.0;
    double continuity = 0.0;
    if (tangentMethod.equalsIgnoreCase("cardinalSpline")) { //$NON-NLS-1$
      tension = Double.parseDouble(JOptionPane.showInputDialog("tension")); //$NON-NLS-1$
    }
    if (tangentMethod.equalsIgnoreCase("kochanekBartels")) { //$NON-NLS-1$
      tension = Double.parseDouble(JOptionPane.showInputDialog("tension")); //$NON-NLS-1$
      bias = Double.parseDouble(JOptionPane.showInputDialog("bias")); //$NON-NLS-1$
      continuity = Double
          .parseDouble(JOptionPane.showInputDialog("continuity")); //$NON-NLS-1$
    }
    Population<DefaultFeature> popHermite = new Population<DefaultFeature>(
        "CubicSpline " + tangentMethod + " " + tension + " " + bias + " " + continuity); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    popHermite.setClasse(DefaultFeature.class);
    popHermite.setPersistant(false);
    for (IFeature f : layer.getFeatureCollection()) {
      GM_CubicSpline s = new GM_CubicSpline(f.getGeom().coord());
      s.setTangentMethod(tangentMethod);
      s.setTension(tension);
      s.setBias(bias);
      s.setContinuity(continuity);
      popHermite.nouvelElement(s);
    }
    /** créer un featuretype de jeu correspondant */
    fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType newFeatureTypeHermite = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType();
    newFeatureTypeHermite.setGeometryType(GM_LineString.class);
    popHermite.setFeatureType(newFeatureTypeHermite);
    project.getDataSet().addPopulation(popHermite);
    project.addFeatureCollection(popHermite, popHermite.getNom(), null);
  }
}
