/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.symbols.geompool;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author JRenard april 2012
 */

public class ColourTableCellRenderer extends DefaultTableCellRenderer {
  private static final long serialVersionUID = 5098500176325838644L;

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {

    ColourTableModel model = (ColourTableModel) table.getModel();

    table.setRowHeight(20);
    for (int i = 0; i < model.getColumnCount(); i++) {
      table.getColumnModel().getColumn(i).setWidth(20);
    }

    // Cell background
    Component cell = super.getTableCellRendererComponent(table, value,
        isSelected, hasFocus, row, column);
    Color col = model.getValueAt(1, column);
    cell.setBackground(col);

    // Cell border if selected
    if (isSelected) {
      ((JComponent) cell).setBorder(BorderFactory.createBevelBorder(
          BevelBorder.LOWERED, Color.WHITE, Color.BLACK));
    }

    return cell;

  }
}
