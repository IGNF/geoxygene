package fr.ign.cogit.geoxygene.sig3d.gui.table.layerstable;

import javax.swing.JTable;

import fr.ign.cogit.geoxygene.sig3d.gui.MainWindow;

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
 * 
 * La JTable permettant l'affichage de la liste des couches
 * 
 * -Affectation d'un modèle TableListCouchesModel
 * 
 * -Affection d'un rendu TableListCouchesRenderer
 * 
 * - Affectation d'évènvements TableListCouchesListener
 * 
 * 
 */
public class LayersListTable extends JTable {

  private static final long serialVersionUID = 1L;

  /**
   * Table listant les couches chargées dans l'application
   * @param mainWindow
   */
  public LayersListTable(MainWindow mainWindow) {
    super();
    this.setDefaultRenderer(Object.class, new LayersListTableRenderer());
    this.setModel(new LayersListTableModel(mainWindow.getInterfaceMap3D()));
    this.addMouseListener(new LayersListTableListener(this, mainWindow));

    this.setRowHeight(30);
    this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    this.getColumnModel().getColumn(LayersListTableModel.IND_COLNAME)
        .setMinWidth(75);
    this.getColumnModel().getColumn(LayersListTableModel.IND_COLNAME)
        .setMaxWidth(200);

    this.getColumnModel().getColumn(LayersListTableModel.IND_COLVIS)
        .setMinWidth(50);
    this.getColumnModel().getColumn(LayersListTableModel.IND_COLVIS)
        .setMaxWidth(50);

    this.getColumnModel().getColumn(LayersListTableModel.IND_COLSEL)
        .setMinWidth(50);
    this.getColumnModel().getColumn(LayersListTableModel.IND_COLSEL)
        .setMaxWidth(50);

    this.getColumnModel().getColumn(LayersListTableModel.IND_COLREP)
        .setMinWidth(50);
    this.getColumnModel().getColumn(LayersListTableModel.IND_COLREP)
        .setMaxWidth(100);

  }

}
