/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.util.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

/**
 * A custom renderer to display only the simple name of the class object in the
 * cell.
 * @author GTouya
 * 
 */
public class ClassSimpleNameListRenderer extends JLabel
    implements ListCellRenderer<Class<?>> {

  /**
   * 
   */
  private static final long serialVersionUID = 1919682684516255133L;

  public ClassSimpleNameListRenderer() {
    this.setOpaque(true);
    this.setHorizontalAlignment(SwingConstants.LEFT);
    this.setVerticalAlignment(SwingConstants.CENTER);
  }

  /*
   * This method finds the image and text corresponding to the selected value
   * and returns the label, set up to display the text and image.
   */
  @Override
  public Component getListCellRendererComponent(JList<? extends Class<?>> list,
      Class<?> value, int index, boolean isSelected, boolean cellHasFocus) {
    if (isSelected) {
      this.setBackground(list.getSelectionBackground());
      this.setForeground(list.getSelectionForeground());
    } else {
      this.setBackground(list.getBackground());
      this.setForeground(list.getForeground());
    }

    // test that the value is really a class object
    if (value instanceof Class) {
      this.setText(((Class<?>) value).getSimpleName());
      this.setFont(list.getFont());
    } else {
      this.setText(value.toString());
      this.setFont(list.getFont());
    }

    return this;
  }
}
