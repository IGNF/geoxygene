package fr.ign.cogit.geoxygene.sig3d.gui.table.layerstable;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import fr.ign.cogit.geoxygene.sig3d.semantic.Layer;

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
 * Classe au rendu des couches dans la table de gestion de couchses. Il peut
 * être nécessaire de modifier cete table si l'on souhaite de nouveux modes de
 * représentations afin qu'ils soient pris en compte dans l'interface.
 */
public class LayersListTableRenderer implements TableCellRenderer {

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {

    if (value instanceof String) {

      JLabel lab = new JLabel(value.toString());

      lab.setBounds(0, 0, 200, 200);

      return lab;

    }

    if (value instanceof Boolean) {

      JCheckBox jcb = new JCheckBox("", Boolean.parseBoolean(value.toString()));

      jcb.setHorizontalAlignment(SwingConstants.HORIZONTAL);
      return jcb;

    }
    /**
     * TODO : Ajouter une illustration en fonction du type de couche ...
     */
    if (value instanceof Layer) {
      return ((Layer) value).getRepresentationComponent();

    }

    return null;
  }

}
