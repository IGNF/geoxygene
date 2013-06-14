package fr.ign.cogit.geoxygene.appli.plugin.cartetopo;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartetopo.gui.DialogTopoStructurePanel;
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.gui.EditParamPanel;

/**
 * 
 * 
 *
 */
public class TopologieReseauPlugin implements GeOxygeneApplicationPlugin, ActionListener {
    
    /** Classic logger. */
    private static Logger LOGGER = Logger.getLogger(TopologieReseauPlugin.class.getName());
    
    /** GeOxygeneApplication. */
    private GeOxygeneApplication application;
    
    /**
     * Initialize the plugin.
     * @param application the application
     */
    @Override
    public final void initialize(final GeOxygeneApplication application) {
      
      this.application = application;
      
      // Check if the DataMatching menu exists. If not we create it.
      JMenu menu = null;
      String menuName = I18N.getString("CarteTopoPlugin.CarteTopoPlugin"); //$NON-NLS-1$
      for (Component c : application.getFrame().getJMenuBar().getComponents()) {
        if (c instanceof JMenu) {
          JMenu aMenu = (JMenu) c;
          if (aMenu.getText() != null
              && aMenu.getText().equalsIgnoreCase(menuName)) {
            menu = aMenu;
          }
        }
      }
      if (menu == null) {
        menu = new JMenu(menuName);
      }
      
      // Add network data matching menu item to the menu.
      JMenuItem menuItem = new JMenuItem(I18N.getString("CarteTopoPlugin.DoingTopologicalStructure")); //$NON-NLS-1$
      menuItem.addActionListener(this);
      menu.add(menuItem);
      
      // Refresh menu of the application
      application
          .getFrame()
          .getJMenuBar()
          .add(menu, application.getFrame().getJMenuBar().getComponentCount() - 2);
      
    }
    
    /**
     * 
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        
        // Launch parameter network data matching panel.
        DialogTopoStructurePanel dialogTopoStructurePanel = new DialogTopoStructurePanel();
        
    }

}
