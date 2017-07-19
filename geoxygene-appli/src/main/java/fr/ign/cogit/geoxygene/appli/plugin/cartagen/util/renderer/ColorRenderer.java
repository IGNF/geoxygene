package fr.ign.cogit.geoxygene.appli.plugin.cartagen.util.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 * A renderer for table columns with a color to choose.
 * @author GTouya
 * 
 */
public class ColorRenderer extends JLabel implements TableCellRenderer {
  /****/
  private static final long serialVersionUID = 1L;
  Border unselectedBorder = null;
  Border selectedBorder = null;
  boolean isBordered = true;

  public ColorRenderer(boolean isBordered) {
    this.isBordered = isBordered;
    setOpaque(true);
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object color,
      boolean isSelected, boolean hasFocus, int row, int column) {
    Color newColor = (Color) color;
    setBackground(newColor);
    if (isBordered) {
      if (isSelected) {
        if (selectedBorder == null) {
          selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
              table.getSelectionBackground());
        }
        setBorder(selectedBorder);
      } else {
        if (unselectedBorder == null) {
          unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
              table.getBackground());
        }
        setBorder(unselectedBorder);
      }
    }
    return this;
  }
}
