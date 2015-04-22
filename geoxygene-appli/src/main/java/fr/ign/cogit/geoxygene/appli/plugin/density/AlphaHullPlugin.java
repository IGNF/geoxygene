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
import fr.ign.cogit.geoxygene.appli.plugin.density.tools.FrameAHull;

/**
 * Plugin d'analyse des alpha hulls
 * 
 * @author SFabry
 */
public class AlphaHullPlugin extends DensityPlugin {

  @Override
  public void actionPerformed(ActionEvent e) {
    this.projectFrame = application.getMainFrame().getSelectedProjectFrame();
    IPopulation<? extends IFeature> pop = getPopulation();

    FrameAHull f = new FrameAHull();
    f.setProjectFrame(this.projectFrame);
    f.setPop(pop);

  }

}
