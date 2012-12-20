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

import javax.swing.table.DefaultTableModel;

/**
 * @author JRenard april 2012
 */

public class ColourTableModel extends DefaultTableModel {
  private static final long serialVersionUID = -6720036030566221898L;

  public ColourTableModel() {
  }

  @Override
  public int getColumnCount() {
    return 13;
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return super.getColumnClass(columnIndex);
  }

  @Override
  public int getRowCount() {
    return 1;
  }

  @Override
  public Color getValueAt(int row, int column) {
    if (row != 1) {
      return null;
    }
    switch (column) {
      case 0:
        return null;
      case 1:
        return Color.BLUE;
      case 2:
        return new Color(0, 176, 240);
      case 3:
        return new Color(0, 163, 0);
      case 4:
        return new Color(153, 204, 0);
      case 5:
        return Color.YELLOW;
      case 6:
        return new Color(255, 192, 0);
      case 7:
        return Color.RED;
      case 8:
        return new Color(151, 72, 0);
      case 9:
        return new Color(255, 102, 204);
      case 10:
        return new Color(153, 0, 153);
      case 11:
        return new Color(178, 178, 178);
      case 12:
        return Color.BLACK;
    }
    return null;
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

}
