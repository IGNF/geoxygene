package fr.ign.cogit.geoxygene.appli.plugin.cartagen.util;

import javax.swing.table.AbstractTableModel;

import fr.ign.cogit.geoxygene.appli.I18N;

/**
 * An abstract table model for tables filled with the layers and columns to
 * select a layer, with a column for picking a color, and a column for picking a
 * line width. This model is used in the frame that enables the display of
 * initial geometries and in the frame that enables the display of eliminated
 * features.
 * @author GTouya
 * 
 */
public class DisplayLayerTableModel extends AbstractTableModel {

  /****/
  private static final long serialVersionUID = 1L;
  private String[] columnNames = {
      I18N.getString("DisplayInitialGeomsFrame.column1"),
      I18N.getString("DisplayInitialGeomsFrame.column1"),
      I18N.getString("DisplayInitialGeomsFrame.column3"),
      I18N.getString("DisplayInitialGeomsFrame.column4") };

  private Object[][] data;

  public DisplayLayerTableModel(Object[][] data) {
    super();
    this.data = data;
  }

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public int getRowCount() {
    return data.length;
  }

  @Override
  public String getColumnName(int col) {
    return columnNames[col];
  }

  @Override
  public Object getValueAt(int row, int col) {
    return data[row][col];
  }

  /*
   * JTable uses this method to determine the default renderer/ editor for each
   * cell. If we didn't implement this method, then the last column would
   * contain text ("true"/"false"), rather than a check box.
   */
  @Override
  public Class<?> getColumnClass(int c) {
    return getValueAt(0, c).getClass();
  }

  @Override
  public boolean isCellEditable(int row, int col) {
    // Note that the data/cell address is constant,
    // no matter where the cell appears onscreen.
    if (col < 1) {
      return false;
    } else {
      return true;
    }
  }

  @Override
  public void setValueAt(Object value, int row, int col) {
    data[row][col] = value;
    fireTableCellUpdated(row, col);
  }
}
