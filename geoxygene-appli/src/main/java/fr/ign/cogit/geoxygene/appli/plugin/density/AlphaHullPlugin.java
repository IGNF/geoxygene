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

    IPopulation<? extends IFeature> pop = getPopulation();

    FrameAHull f = new FrameAHull();
    f.setProjectFrame(projectFrame);
    f.setPop(pop);

  }

}
