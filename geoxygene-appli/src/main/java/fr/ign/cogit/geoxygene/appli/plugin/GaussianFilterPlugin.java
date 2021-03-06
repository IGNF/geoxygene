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

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * Gaussian Filter plugin.
 * @author Julien Perret
 */
public class GaussianFilterPlugin extends AbstractGeOxygeneApplicationPlugin {
  
  /** Logger. */
  static final Logger LOGGER = Logger.getLogger(GaussianFilterPlugin.class.getName());

  /**
   * Initialize the plugin.
   * @param application the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication application) {
    this.application = application;
    
    JMenu menu = addMenu("Geometry Algorithms", "Gaussian Filter");
    application.getMainFrame().getMenuBar().add(menu, application.getMainFrame().getMenuBar().getMenuCount() - 2);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void actionPerformed(final ActionEvent e) {
    ProjectFrame project = this.application.getMainFrame().getSelectedProjectFrame();
    Set<Layer> selectedLayers = project.getLayerLegendPanel().getSelectedLayers();
    if (selectedLayers.size() != 1) {
      javax.swing.JOptionPane.showMessageDialog(null, "You need to select one (and only one) layer.");
      LOGGER.error("You need to select one (and only one) layer."); //$NON-NLS-1$
      return;
    }
    Layer layer = selectedLayers.iterator().next();
    
    double sigma = Double.parseDouble(JOptionPane.showInputDialog(GaussianFilterPlugin.this.application.getMainFrame(), "Sigma")); //$NON-NLS-1$
    Population<DefaultFeature> pop = new Population<DefaultFeature>("GaussianFilter " + layer.getName() + " " + sigma); //$NON-NLS-1$ //$NON-NLS-2$
    pop.setClasse(DefaultFeature.class);
    pop.setPersistant(false);
    for (IFeature f : layer.getFeatureCollection()) {
      ILineString line = null;
      if (ILineString.class.isAssignableFrom(f.getGeom().getClass())) {
        line = (ILineString) f.getGeom();
      } else {
        if (IMultiCurve.class.isAssignableFrom(f.getGeom().getClass())) {
          line = ((IMultiCurve<ILineString>) f.getGeom()).get(0);
        }
      }
      pop.nouvelElement(GaussianFilter.gaussianFilter(line, sigma, 1));
    }
    
    /** créer un featuretype de jeu correspondant */
    FeatureType newFeatureType = new FeatureType();
    newFeatureType.setGeometryType(ILineString.class);
    pop.setFeatureType(newFeatureType);
    project.getDataSet().addPopulation(pop);
    project.addUserLayer(pop, pop.getNom(), layer.getCRS());
  }
}
