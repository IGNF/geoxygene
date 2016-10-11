package fr.ign.cogit.geoxygene.sig3d.gui.table.lightstable;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

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
 * Renderer de la table gérant l'interface pour la gestion des lumières
 * 
 * Renderer for the table managing lights in the scene
 */
public class LightsListTableRenderer implements TableCellRenderer {

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    // Les coordonnées des lumières sont représentées par des JLabel
    if (value instanceof Double || value instanceof Float) {

      JLabel lab = new JLabel(value.toString());
      lab.setBounds(0, 0, 200, 200);

      // C'est sélectionné le fond est cyan
      if (isSelected) {
        lab.setBackground(Color.cyan);
        lab.setOpaque(true);
      }

      return lab;

    }
    // Pour représenter la couleur on utilise un JButton
    if (value instanceof Color) {

      JButton button = new JButton();
      button.setBackground((Color) value);
      if (isSelected) {
        // Fond cyan si sélectionné
        button.setBorder(BorderFactory.createLineBorder(Color.cyan));

      }

      return button;

    }

    return null;
  }

}
