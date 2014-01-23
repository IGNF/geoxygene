/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli.plugin.cartagen.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import fr.ign.cogit.cartagen.core.carto.SLDUtil;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

public class DisplayInitialGeomsFrame extends JFrame implements ActionListener {

  /****/
  private static final long serialVersionUID = 1L;

  private JTable jtable;
  private StyledLayerDescriptor sld;
  private ProjectFrame frame;
  private JButton okBtn, applyBtn, cancelBtn;
  private List<Layer> layers = new ArrayList<Layer>();

  public DisplayInitialGeomsFrame(ProjectFrame frame) {
    super(I18N.getString("DisplayInitialGeomsFrame.title"));
    this.frame = frame;
    this.sld = frame.getSld();
    this.setSize(300, 300);
    this.layers.addAll(sld.getLayers());
    Layer geomPool = sld.getLayer(CartAGenDataSet.GEOM_POOL);
    this.layers.remove(geomPool);
    Object[][] data = new Object[layers.size()][4];
    for (int i = 0; i < layers.size(); i++) {
      Layer layer = layers.get(i);
      String name = layer.getName();
      FeatureTypeStyle style = SLDUtil.getLayerInitialDisplay(layer);
      boolean display = false;
      Color color = Color.RED;
      int width = 1;
      if (style != null) {
        display = true;
        color = style.getSymbolizer().getStroke().getColor();
        width = (int) style.getSymbolizer().getStroke().getStrokeWidth();
      }
      data[i] = new Object[] { name, display, color, width };
    }
    this.setAlwaysOnTop(true);

    // setup the table
    this.jtable = new JTable(new DisplayInitTableModel(data));
    jtable.setDefaultRenderer(Color.class, new ColorRenderer(true));
    jtable.setDefaultEditor(Color.class, new ColorEditor());

    // a panel for buttons
    JPanel pButtons = new JPanel();
    okBtn = new JButton("OK");
    okBtn.addActionListener(this);
    okBtn.setActionCommand("ok");
    applyBtn = new JButton(I18N.getString("MainLabels.lblApply"));
    applyBtn.addActionListener(this);
    applyBtn.setActionCommand("apply");
    cancelBtn = new JButton(I18N.getString("MainLabels.lblCancel"));
    cancelBtn.addActionListener(this);
    cancelBtn.setActionCommand("cancel");
    pButtons.add(okBtn);
    pButtons.add(applyBtn);
    pButtons.add(cancelBtn);
    pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));

    // main frame layout setup
    this.getContentPane().add(new JScrollPane(jtable));
    this.getContentPane().add(pButtons);
    this.getContentPane().setLayout(
        new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("ok")) {
      this.displayInitialGeoms();
      this.dispose();
    } else if (e.getActionCommand().equals("cancel")) {
      this.dispose();
    } else if (e.getActionCommand().equals("apply")) {
      this.displayInitialGeoms();
    }
  }

  /**
   * Add the style to display initial geometries for the layer selected in the
   * table.
   */
  private void displayInitialGeoms() {
    for (int i = 0; i < jtable.getRowCount(); i++) {
      Layer layer = sld.getLayer((String) jtable.getModel().getValueAt(i, 0));
      boolean display = (Boolean) jtable.getValueAt(i, 1);
      if (display) {
        Color color = (Color) jtable.getModel().getValueAt(i, 2);
        Object widthVal = jtable.getModel().getValueAt(i, 3);
        int width = 1;
        if (widthVal instanceof String)
          width = Integer.valueOf((String) widthVal);
        else
          width = (Integer) widthVal;
        SLDUtil.addInitialGeomDisplay(layer, color, width);
      } else
        SLDUtil.removeInitialGeomDisplay(layer);
    }
    frame.getLayerViewPanel().repaint();
  }

  /**
   * The Cell editor for the column with colors.
   * @author GTouya
   * 
   */
  public class ColorEditor extends AbstractCellEditor implements
      TableCellEditor, ActionListener {
    /****/
    private static final long serialVersionUID = 1L;
    Color currentColor;
    JButton button;
    JColorChooser colorChooser;
    JDialog dialog;
    protected static final String EDIT = "edit";

    public ColorEditor() {
      // Set up the editor (from the table's point of view),
      // which is a button.
      // This button brings up the color chooser dialog,
      // which is the editor from the user's point of view.
      button = new JButton();
      button.setActionCommand(EDIT);
      button.addActionListener(this);
      button.setBorderPainted(false);

      // Set up the dialog that the button brings up.
      colorChooser = new JColorChooser();
      dialog = JColorChooser.createDialog(button, "Pick a Color", true, // modal
          colorChooser, this, // OK button handler
          null); // no CANCEL button handler
    }

    /**
     * Handles events from the editor button and from the dialog's OK button.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      if (EDIT.equals(e.getActionCommand())) {
        // The user has clicked the cell, so
        // bring up the dialog.
        button.setBackground(currentColor);
        colorChooser.setColor(currentColor);
        dialog.setVisible(true);

        // Make the renderer reappear.
        fireEditingStopped();

      } else { // User pressed dialog's "OK" button.
        currentColor = colorChooser.getColor();
      }
    }

    // Implement the one CellEditor method that AbstractCellEditor doesn't.
    @Override
    public Object getCellEditorValue() {
      return currentColor;
    }

    // Implement the one method defined by TableCellEditor.
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
      currentColor = (Color) value;
      return button;
    }
  }

  /**
   * The renderer for the color column
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

  class DisplayInitTableModel extends AbstractTableModel {
    /****/
    private static final long serialVersionUID = 1L;
    private String[] columnNames = {
        I18N.getString("DisplayInitialGeomsFrame.column1"),
        I18N.getString("DisplayInitialGeomsFrame.column1"),
        I18N.getString("DisplayInitialGeomsFrame.column3"),
        I18N.getString("DisplayInitialGeomsFrame.column4") };

    private Object[][] data;

    public DisplayInitTableModel(Object[][] data) {
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
     * JTable uses this method to determine the default renderer/ editor for
     * each cell. If we didn't implement this method, then the last column would
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
}
