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

import java.awt.event.ActionEvent;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.plugin.density.tools.DensityPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.density.tools.GridFrame;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;

/**
 * Affiche une grille d'analyse de densité de points
 * 
 * @author SFabry
 */
public class GridPlugin extends DensityPlugin {

  @SuppressWarnings("unchecked")
  @Override
  public void actionPerformed(ActionEvent e) {
    this.projectFrame = application.getMainFrame().getSelectedProjectFrame();
    IPopulation<? extends IFeature> pop = getPopulation();
    
    GridFrame gf = new GridFrame();
    
    gf.setProjectFrame(projectFrame);
    
    gf.setPop((IPopulation<DefaultFeature>) pop);
    
  }

}
