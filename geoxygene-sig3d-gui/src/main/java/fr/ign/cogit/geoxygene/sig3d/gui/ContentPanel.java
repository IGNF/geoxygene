package fr.ign.cogit.geoxygene.sig3d.gui;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.dd.FileTransferHandler;
import fr.ign.cogit.geoxygene.sig3d.gui.table.layerstable.LayersListTable;

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
 * Classe contenant le panneau de gestion de couches
 * 
 * This class contains the layer manager
 * 
 */
public class ContentPanel extends JPanel {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  LayersListTable layersListTable;

  static MainWindow mainWindow;

  /**
   * Constructeur par defaut : Panneau de gauche
   */
  public ContentPanel(MainWindow mainWindow) {
    super();
    
    this.setTransferHandler(new FileTransferHandler(mainWindow.getInterfaceMap3D()));
    // On crée le lien entre le panneau gauche et l'application
    ContentPanel.mainWindow = mainWindow;
    // définition d'un nouveau Panneau
    this.setLayout(new BorderLayout());

    // Création du Layout
    Box box = Box.createVerticalBox();

    // Ajout d'un àcart vertical
    box.add(Box.createVerticalStrut(10));

    // On ajoute un JTable contenant les différentes couches
    JPanel jpan = new JPanel();
    jpan.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("3DGIS.Layer")));
    jpan.setLayout(new BorderLayout());

    this.layersListTable = new LayersListTable(mainWindow);
    JScrollPane jscrollpan = new JScrollPane(this.layersListTable);

    jpan.add(jscrollpan);
    this.add(jpan);

  }

  /**
   * 
   * 
   * @return La table contenant les différentes informations relatives aux
   *         couches
   */
  public LayersListTable getLayersListTable() {
    return this.layersListTable;
  }

}
