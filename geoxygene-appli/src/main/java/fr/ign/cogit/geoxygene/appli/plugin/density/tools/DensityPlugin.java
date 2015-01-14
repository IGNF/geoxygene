package fr.ign.cogit.geoxygene.appli.plugin.density.tools;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;

/**
 * Classe mere des plugins
 * 
 * @see AlphaHullPlugin, ConvexHullPlugin, GridPlugin, TriangulationPlugin, VoronoiPlugin 
 * @author SFabry
 */
public abstract class DensityPlugin implements GeOxygeneApplicationPlugin, ActionListener{

  protected final static Logger LOGGER = Logger.getLogger(DensityPlugin.class.getName());
  public ProjectFrame projectFrame;
  
  
  /**
   * Initialize construit le JMenu Density et y ajoute notre plugin
   * @param application instance de l'application  
   */
  @Override
  public void initialize(final GeOxygeneApplication application) {
    this.projectFrame = application.getMainFrame().getSelectedProjectFrame();
    JMenu menuExample = null;
    String menuName = "Density";
    for (Component c : application.getMainFrame().getMenuBar().getComponents()) {
      if (c instanceof JMenu) {
        JMenu aMenu = (JMenu) c;
        if (aMenu.getText() != null
            && aMenu.getText().equalsIgnoreCase(menuName)) {
          menuExample = aMenu;
        }
      }
    }
    if (menuExample == null) {
      menuExample = new JMenu(menuName);
    }
    
    String name = this.getClass().getName();
    name = name.substring(name.lastIndexOf(".")+1, name.length()-6);
    JMenuItem menuItem = new JMenuItem(name);
    menuItem.addActionListener(this);
    menuExample.add(menuItem);
    application.getMainFrame().getMenuBar().add(menuExample, application.getMainFrame().getMenuBar().getComponentCount() - 2);
  }
  
  /**
   * Demande à l'utilisateur la population sur lequel appliquer le plugin, si une couche est chargee renvoie celle-ci, si plusieurs couches sont chargées, il sera demandé à l'utilisateur.
   * @return renvoie null si il n'y a pas de couche de chargee, ou si l'utilisateur annule la selection de couche, renvoie la population sinon.
   */
  public IPopulation<? extends IFeature> getPopulation(){
    List<IPopulation<? extends IFeature>> list = projectFrame.getDataSet().getPopulations();
    if(list.size()==0){
      JOptionPane.showMessageDialog(null, "Il faut avoir chargé au moins une couche", "erreur", JOptionPane.ERROR_MESSAGE);
      return null;
    }else if(list.size()==1)
      return list.get(0);
    else{

      Object[] possibilities = new Object[list.size()];
      int i = 0;
      for (IPopulation<? extends IFeature> p : list) {
        possibilities[i] = p.getNom();
        i++;
      }

      String s = (String) JOptionPane.showInputDialog(null, "Layer: ", "info", JOptionPane.PLAIN_MESSAGE, null, possibilities, possibilities[0]);

      if(s==null)
        return null;

      for (IPopulation<? extends IFeature> p : list)
        if(p.getNom().equals(s))
          return p;
      return null;
    }
  }
  
  @Override
  public abstract void actionPerformed(ActionEvent e);
}
