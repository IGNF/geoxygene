/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 * A custom renderer to display only the simple name of the class object in the
 * cell.
 * @author GTouya
 * 
 */
public class ClassSimpleNameTableRenderer extends JLabel implements
    TableCellRenderer {

  /**
   * 
   */
  private static final long serialVersionUID = 1919682684516255133L;

  public ClassSimpleNameTableRenderer() {
    this.setOpaque(true);
    this.setHorizontalAlignment(SwingConstants.LEFT);
    this.setVerticalAlignment(SwingConstants.CENTER);
  }

  /*
   * This method finds the image and text corresponding to the selected value
   * and returns the label, set up to display the text and image.
   */
  @SuppressWarnings("rawtypes")
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean cellHasFocus, int row, int column) {
    if (isSelected) {
      this.setBackground(table.getSelectionBackground());
      this.setForeground(table.getSelectionForeground());
    } else {
      this.setBackground(table.getBackground());
      this.setForeground(table.getForeground());
    }

    // test that the value is really a class object
    if (value instanceof Class) {
      this.setText(((Class) value).getSimpleName());
      this.setFont(table.getFont());
    } else if (value instanceof String) {
      int index = value.toString().lastIndexOf(".");
      this.setText(value.toString().substring(index + 1));
      this.setFont(table.getFont());
    } else {
      this.setText(value.toString());
      this.setFont(table.getFont());
    }
    return this;
  }
}
