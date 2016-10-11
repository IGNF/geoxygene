package fr.ign.cogit.geoxygene.sig3d.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.table.layerstable.LayersListTableModel;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.DTMWindow;
import fr.ign.cogit.geoxygene.sig3d.semantic.DTM;
import fr.ign.cogit.geoxygene.sig3d.semantic.Map3D;

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
 * @author benoitpoupeau
 * 
 * @version 0.1
 * 
 * 
 * Menu qui s'affiche lors d'un clic droit sur un objet de la couche MNT
 * 
 * Menu displayed when a MNT is right clic ine the table of layer management
 * 
 * 
 */
public class DTMMenu extends JPopupMenu {

  /**
		 * 
		 */
  private static final long serialVersionUID = 1L;

  /**
   * Le constructeur qui affiche les options. Le MNT, le model de la table de
   * gestion de couche et la carte dans laquelle est affichée le MNT sont
   * nécessaires
   * 
   * @param dtm le MNT dont on souhaite le menu contextuel
   * @param map3D l'interface de carte dans laquelle il s'affiche
   * @param mjtc le LayersListTableModel associé
   */
  public DTMMenu(final DTM dtm, final Map3D map3D,
      final LayersListTableModel mjtc) {
    super();
    JMenuItem item = new JMenuItem();
    item.setText(Messages.getString("MenuMNT.Edition"));
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        DTMWindow fen = new DTMWindow(dtm);
        fen.setVisible(true);
        mjtc.fireTableDataChanged();
      }
    });

    JMenuItem item2 = new JMenuItem();
    item2.setText(Messages.getString("MenuMNT.Delete"));
    item2.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        map3D.removeLayer(dtm.getLayerName());
        mjtc.fireTableDataChanged();
      }
    });

    this.add(item);
    this.add(item2);
  }

}
