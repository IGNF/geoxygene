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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * A custom renderer to display only the simple name of the class object in the
 * cell.
 * @author GTouya
 * 
 */
public class ClassSimpleNameTreeRenderer extends DefaultTreeCellRenderer {

  /**
   * 
   */
  private static final long serialVersionUID = 1919682684516255133L;

  public ClassSimpleNameTreeRenderer() {
    super();
    this.setOpaque(true);
    this.setHorizontalAlignment(SwingConstants.LEFT);
    this.setVerticalAlignment(SwingConstants.CENTER);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value,
      boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    Component c = super.getTreeCellRendererComponent(tree, value, sel,
        expanded, leaf, row, hasFocus);
    if (this.selected) {
      c.setForeground(Color.RED);
    } else {
      c.setForeground(Color.BLACK);
    }

    // test that the value is really a class object
    if (((DefaultMutableTreeNode) value).getUserObject() instanceof Class) {
      ((JLabel) c).setText(((Class) ((DefaultMutableTreeNode) value)
          .getUserObject()).getSimpleName());
      c.setFont(tree.getFont());
    } else {
      ((JLabel) c).setText(value.toString());
      c.setFont(tree.getFont());
    }

    return c;
  }

}
