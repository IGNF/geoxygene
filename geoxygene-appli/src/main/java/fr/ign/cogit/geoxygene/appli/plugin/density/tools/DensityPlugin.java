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
package fr.ign.cogit.geoxygene.appli.plugin.density.tools;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JOptionPane;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.AbstractGeOxygeneApplicationPlugin;

/**
 * Classe mere des plugins
 * 
 * @see AlphaHullPlugin, ConvexHullPlugin, GridPlugin, TriangulationPlugin, VoronoiPlugin 
 * 
 * @author SFabry
 */
public abstract class DensityPlugin extends AbstractGeOxygeneApplicationPlugin {

  protected final static Logger LOGGER = Logger.getLogger(DensityPlugin.class.getName());

  private final static String DENSITY_TITLE = "Density";
  
  protected ProjectFrame projectFrame;
  
  /**
   * Initialize construit le JMenu Density et y ajoute notre plugin
   * @param application instance de l'application  
   */
  @Override
  public void initialize(final GeOxygeneApplication application) {
    this.application = application;
    this.projectFrame = application.getMainFrame().getSelectedProjectFrame();
    
    String name = this.getClass().getName();
    name = name.substring(name.lastIndexOf(".")+1, name.length()-6);
    JMenu menuAwt = addMenu(DENSITY_TITLE, name);
    application.getMainFrame().getMenuBar()
      .add(menuAwt, application.getMainFrame().getMenuBar().getMenuCount() - 2);
    
  }
  
  /**
   * Demande à l'utilisateur la population sur lequel appliquer le plugin.
   * Si une couche est chargee renvoie celle-ci, si plusieurs couches sont chargées, il sera demandé à l'utilisateur.
   * 
   * @return renvoie null si il n'y a pas de couche de chargee, 
   *    ou si l'utilisateur annule la selection de couche, renvoie la population sinon.
   */
  public IPopulation<? extends IFeature> getPopulation() {
    List<IPopulation<? extends IFeature>> list = this.projectFrame.getDataSet().getPopulations();
    
    if (list.size() == 0) {
      JOptionPane.showMessageDialog(null, "Il faut avoir chargé au moins une couche", "erreur", JOptionPane.ERROR_MESSAGE);
      return null;
    } else if (list.size() == 1) {
      return list.get(0);
    } else {

      Object[] possibilities = new Object[list.size()];
      int i = 0;
      for (IPopulation<? extends IFeature> p : list) {
        possibilities[i] = p.getNom();
        i++;
      }

      String s = (String) JOptionPane.showInputDialog(null, "Layer: ", "info", JOptionPane.PLAIN_MESSAGE, null, possibilities, possibilities[0]);

      if(s==null)
        return null;

      for (IPopulation<? extends IFeature> p : list) {
        if(p.getNom().equals(s)) {
          return p;
        }
      }
      return null;
    }
  }
  
  @Override
  public abstract void actionPerformed(ActionEvent e);
}
