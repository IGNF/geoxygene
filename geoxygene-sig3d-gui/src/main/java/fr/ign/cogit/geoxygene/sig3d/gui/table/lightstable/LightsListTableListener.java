package fr.ign.cogit.geoxygene.sig3d.gui.table.lightstable;

import javax.swing.DefaultListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu.LightMenu;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 *  
 * @version 0.1
 * 
 * Listener pour la table de gestion d'affichage de lumières associée au menu
 * LightMenu
 * 
 * Listener for the table managing lights associated to a LightMenu object
 * 
 */
public class LightsListTableListener implements ListSelectionListener {

  LightMenu lightMenu;

  /**
   * 
   * @param lightMenu le menu que l'on écoute
   */
  public LightsListTableListener(LightMenu lightMenu) {
    this.lightMenu = lightMenu;
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {

    // Nouvelle sélection on met à jour le formulaire

    if (!e.getValueIsAdjusting()) {
      // On met à jour le menu supérieur
      this.lightMenu.setIndex(((DefaultListSelectionModel) e.getSource())
          .getMinSelectionIndex());

    }
  }

}
