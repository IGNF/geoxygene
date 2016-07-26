package fr.ign.cogit.geoxygene.sig3d.gui.table.lightstable;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
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
 * JTable permettant de gérer la lumières JTable for light management
 */
public class LightsListTable extends JTable {

  private static final long serialVersionUID = 1L;

  /**
   * Constructeur chargé de récupérer les lumières associées à un objet
   * InterfaceMap3D
   * 
   * @param iMap3D l'objet InterfaceMap3D dont on cherchera à récupérer les
   *          lumières
   * @param lightMenu le menu de lumière dans lequel apparaîtra la table
   */
  public LightsListTable(InterfaceMap3D iMap3D, LightMenu lightMenu) {
    super();

    this.setDefaultRenderer(Object.class, new LightsListTableRenderer());
    this.setModel(new LightsListTableModel(iMap3D));
    LightsListTableListener listener = new LightsListTableListener(lightMenu);
    this.getSelectionModel().addListSelectionListener(listener);
    this.getTableHeader().setReorderingAllowed(false);
    this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.setRowHeight(30);
    this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    this.setPreferredSize(new Dimension(350, 500));

  }

}
