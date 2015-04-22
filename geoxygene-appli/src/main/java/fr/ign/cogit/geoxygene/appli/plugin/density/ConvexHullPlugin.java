/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 */
package fr.ign.cogit.geoxygene.appli.plugin.density;

import java.awt.Color;
import java.awt.event.ActionEvent;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.plugin.density.tools.DensityPlugin;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * Dessine l'enveloppe convex d'un semi de point
 * 
 * @author SFabry
 */
public class ConvexHullPlugin extends DensityPlugin{

  @Override
  public void actionPerformed(ActionEvent e) {
    this.projectFrame = application.getMainFrame().getSelectedProjectFrame();
    IPopulation<? extends IFeature> pop = getPopulation();
    
    GM_MultiPoint agg = new GM_MultiPoint();
    
    for (IFeature iFeature : pop.getElements()) {
      DirectPosition dp = new DirectPosition(iFeature.getGeom().centroid().getX(), iFeature.getGeom().centroid().getY());
      GM_Point p = new GM_Point(dp);
      agg.add(p);
    }
    
    
    IGeometry convexHull = agg.convexHull();
    Population<DefaultFeature> pop2 = new Population<DefaultFeature>("ConvexHull");
    this.projectFrame.getDataSet().addPopulation(pop2);
    
    pop2.add(new DefaultFeature(convexHull));
    
    Layer layerC = projectFrame.getSld().createLayer("ConvexHull",GM_Polygon.class, Color.BLACK, Color.yellow, 1f, 4);
    
    projectFrame.getSld().add(layerC);
    
    projectFrame.getSld().moveLayer(0,1);
    
    
  }


}
