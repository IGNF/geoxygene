package fr.ign.cogit.geoxygene.appli.plugin.cartagen.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import fr.ign.cogit.cartagen.core.carto.SLDUtil;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component.JColorSelectionButton;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
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
  private Map<String, Boolean> layerSelected = new HashMap<String, Boolean>();

  public DisplayInitialGeomsFrame(ProjectFrame frame) {
    super(I18N.getString("DisplayInitialGeomsFrame.title"));
    this.frame = frame;
    this.sld = frame.getSld();
    this.setSize(300, 300);
    this.layers.addAll(sld.getLayers());
    Layer geomPool = sld.getLayer(CartAGenDataSet.GEOM_POOL);
    this.layers.remove(geomPool);
    for (Layer layer : layers)
      layerSelected.put(layer.getName(), SLDUtil.layerHasInitialDisplay(layer));
    this.setAlwaysOnTop(true);

    // setup the table
    DefaultTableModel tableModel = new LayersTableModel();
    this.jtable = new JTable(tableModel);
    this.jtable.getColumnModel().getColumn(1)
        .setCellRenderer(new CheckBoxCellRenderer());
    this.jtable.getColumnModel().getColumn(1)
        .setCellEditor(new DefaultCellEditor(new JCheckBox()));
    ColorChooserCellEditor colorEditor = new ColorChooserCellEditor();
    this.jtable.getColumnModel().getColumn(2).setCellEditor(colorEditor);
    this.jtable.getColumnModel().getColumn(2).setCellRenderer(colorEditor);
    for (int i = 0; i < layers.size(); i++) {
      Layer layer = layers.get(i);
      this.jtable.getModel().setValueAt(layer.getName(), i, 0);
      this.jtable.getModel().setValueAt(layerSelected.get(layer.getName()), i,
          1);
      this.jtable.getModel().setValueAt(Color.RED, i, 2);
      this.jtable.getModel().setValueAt(1, i, 3);
    }

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
      boolean display = (Boolean) layerSelected.get(layer.getName());
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

  class ColorChooserCellEditor extends AbstractCellEditor implements
      TableCellEditor, TableCellRenderer {

    /****/
    private static final long serialVersionUID = 1L;
    private JColorSelectionButton delegate = new JColorSelectionButton();

    public ColorChooserCellEditor() {
      super();
      delegate.setLocation(DisplayInitialGeomsFrame.this.getBounds().width * 2,
          DisplayInitialGeomsFrame.this.getBounds().height * 2);
    }

    public Object getCellEditorValue() {
      return delegate.getColor();
    }

    void changeColor(Color color) {
      if (color != null) {
        delegate.setColor(color);
      }
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
      changeColor((Color) value);
      return delegate;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      return delegate;
    }
  }

  /**
   * The renderer for the second column of the table.
   */
  class CheckBoxCellRenderer extends JCheckBox implements TableCellRenderer {
    /**
     * Serial uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public CheckBoxCellRenderer() {
      super();
      this.setBackground(Color.white);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int col) {
      Layer layer = layers.get(row);
      if (row < layers.size()) {
        if (isSelected) {
          this.setBackground(new Color(252, 233, 158));
        } else {
          this.setBackground(Color.WHITE);
        }
        if (col == 1) {
          this.setSelected(layerSelected.get(layer.getName()));
        }
        return this;
      }
      return null;
    }
  }

  /**
   * The TableModel of the Frame
   */
  public class LayersTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 1L;

    @Override
    public int getColumnCount() {
      return 4;
    }

    @Override
    public int getRowCount() {
      return layers.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
      Layer layer = layers.get(row);
      if (row < layers.size()) {
        if (col == 0) {
          return new String(layer.getName());
        } else if (col == 1) {
          Boolean value = (Boolean) jtable.getCellEditor(row, col)
              .getCellEditorValue();
          return value;
        } else if (col == 2) {
          Color value = (Color) jtable.getCellEditor(row, col)
              .getCellEditorValue();
          return value;
        } else
          return super.getValueAt(row, col);
      }
      return new Object();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
      if (col != 0)
        return true;
      else
        return false;
    }

    @Override
    public String getColumnName(int column) {
      if (column == 0) {
        return I18N.getString("DisplayInitialGeomsFrame.column1"); //$NON-NLS-1$
      }
      if (column == 1) {
        return I18N.getString("DisplayInitialGeomsFrame.column2"); //$NON-NLS-1$
      }
      if (column == 2) {
        return I18N.getString("DisplayInitialGeomsFrame.column3"); //$NON-NLS-1$
      }
      if (column == 3) {
        return I18N.getString("DisplayInitialGeomsFrame.column4"); //$NON-NLS-1$
      }
      return null;
    }

    @Override
    public Class<?> getColumnClass(int column) {
      if (column == 0)
        return String.class;
      if (column == 1)
        return Boolean.class;
      if (column == 2)
        return Color.class;
      if (column == 4)
        return Integer.class;
      return String.class;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
      Layer layer = layers.get(row);
      if (col == 1) {
        layerSelected.put(layer.getName(), ((Boolean) value).booleanValue());
        // this.fireTableRowsUpdated(row, row);
      } else if (col == 2) {
        ColorChooserCellEditor editor = (ColorChooserCellEditor) jtable
            .getCellEditor(row, col);
        editor.changeColor((Color) value);
        this.fireTableRowsUpdated(row, row);
      } else {
        super.setValueAt(value, row, col);
        this.fireTableCellUpdated(row, col);
      }
    }
  }

}
