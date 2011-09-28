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

package fr.ign.cogit.geoxygene.appli;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.render.LayerRenderer;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

/**
 * Panel displaying layer legends.
 * @author Julien Perret
 * @author Sylvain Becuwe
 * @author Charlotte Hoarau
 */
public class LayerLegendPanel extends JPanel implements ChangeListener,ActionListener, SldListener{
  /**
   * serial version uid.
   */
  private static final long serialVersionUID = -6860364246334166387L;

  static Logger logger = Logger.getLogger(LayerLegendPanel.class.getName());
  
  /**
   * Model for the layerlegendpanel
   */
  
   private StyledLayerDescriptor sldmodel;
  
  /**
     * parent component
     */
  private ProjectFrame parent = null;

  /**
   * Get the layerViewPanel of the layer legend panel.
   * @return the layerViewPanel of the layer legend panel.
   */
  public LayerViewPanel getLayerViewPanel() {
    return this.parent.getLayerViewPanel();
  }

  /**
     *
     */
  DefaultTableModel tablemodel = null;
  /**
     *
     */
  JTable layersTable = null;

  JButton plusButton = new JButton(new ImageIcon(this.getClass().getResource(
      "/images/icons/16x16/plus.png"))); //$NON-NLS-1$
  JButton topButton = new JButton(new ImageIcon(this.getClass().getResource(
      "/images/icons/16x16/top.png"))); //$NON-NLS-1$
  JButton upButton = new JButton(new ImageIcon(this.getClass().getResource(
      "/images/icons/16x16/up.png"))); //$NON-NLS-1$
  JButton downButton = new JButton(new ImageIcon(this.getClass().getResource(
      "/images/icons/16x16/down.png"))); //$NON-NLS-1$
  JButton bottomButton = new JButton(new ImageIcon(this.getClass().getResource(
      "/images/icons/16x16/bottom.png"))); //$NON-NLS-1$
  JButton minusButton = new JButton(new ImageIcon(this.getClass().getResource(
      "/images/icons/16x16/minus.png"))); //$NON-NLS-1$
  JButton attributeButton = new JButton(new ImageIcon(this.getClass().getResource(
      "/images/icons/16x16/editAttributes.png"))); //$NON-NLS-1$
  
  JPopupMenu popupMenu = new JPopupMenu();
  JMenuItem newLayerMenuItem = new JMenuItem(
      I18N.getString("LayerLegendPanel.CreateLayer")); //$NON-NLS-1$
  JMenuItem selectMenuItem = new JMenuItem(I18N.getString("LayerLegendPanel." + //$NON-NLS-1$
      "SelectAllObjectsInLayer")); //$NON-NLS-1$
  JMenuItem unselectMenuItem = new JMenuItem(
      I18N.getString("LayerLegendPanel." + //$NON-NLS-1$
          "UnselectAllObjectsInLayer")); //$NON-NLS-1$
  JMenuItem renameMenuItem = new JMenuItem(
      I18N.getString("LayerLegendPanel.RenameLayer")); //$NON-NLS-1$
  JMenuItem editMenuItem = new JMenuItem(I18N.getString("LayerLegendPanel." + //$NON-NLS-1$
      "EditLayerAttributes"), //$NON-NLS-1$
      new ImageIcon(this.getClass().getResource(
          "/images/icons/16x16/editAttributes.png"))); //$NON-NLS-1$
  JMenuItem deleteMenuItem = new JMenuItem(
      I18N.getString("LayerLegendPanel.DeleteLayer")); //$NON-NLS-1$
  JMenuItem editSldMenuItem = new JMenuItem(
      I18N.getString("LayerLegendPanel.EditStyle"), //$NON-NLS-1$
      new ImageIcon(this.getClass().getResource(
          "/images/icons/16x16/editStyles.png"))); //$NON-NLS-1$
  JMenu changeStyleMenu = new JMenu("Change Style"); //$NON-NLS-1$
  JMenuItem centerViewMenuItem = new JMenuItem(
      I18N.getString("LayerLegendPanel." + //$NON-NLS-1$
          "CenterViewOnLayer")); //$NON-NLS-1$

  ImageIcon[] images = new ImageIcon[21];

  /**
   * @param parentPFrame the parent frame for this panel.
   */
  public LayerLegendPanel(final ProjectFrame parentPFrame) {
    super();
    this.parent = parentPFrame;

    for (int n = 0; n < 20; n++) {
      ImageIcon image = new ImageIcon(
          LayerLegendPanel.class
              .getResource("/images/icons/32x32/munsellcolorwheel_" //$NON-NLS-1$
                  + n + ".png")); //$NON-NLS-1$
      this.images[n] = image;
    }
    ImageIcon image = new ImageIcon(
        LayerLegendPanel.class
            .getResource("/images/icons/32x32/munsellcolorwheel.png")); //$NON-NLS-1$
    this.images[20] = image;

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    this.plusButton.setMargin(new Insets(0, 0, 0, 0));
    this.topButton.setMargin(new Insets(0, 0, 0, 0));
    this.upButton.setMargin(new Insets(0, 0, 0, 0));
    this.downButton.setMargin(new Insets(0, 0, 0, 0));
    this.bottomButton.setMargin(new Insets(0, 0, 0, 0));
    this.minusButton.setMargin(new Insets(0, 0, 0, 0));
    this.attributeButton.setMargin(new Insets(0, 0, 0, 0));
    panel.add(Box.createHorizontalGlue());
    panel.add(this.plusButton);
    panel.add(Box.createHorizontalGlue());
    panel.add(this.topButton);
    panel.add(Box.createHorizontalGlue());
    panel.add(this.upButton);
    panel.add(Box.createHorizontalGlue());
    panel.add(this.downButton);
    panel.add(Box.createHorizontalGlue());
    panel.add(this.bottomButton);
    panel.add(Box.createHorizontalGlue());
    panel.add(this.minusButton);
    panel.add(Box.createHorizontalGlue());
    panel.add(this.attributeButton);
    panel.add(Box.createHorizontalGlue());
    this.add(panel);
    this.minusButton.addActionListener(this);
    this.minusButton.setActionCommand("remove"); //$NON-NLS-1$
    this.plusButton.addActionListener(this);
    this.plusButton.setActionCommand("add"); //$NON-NLS-1$
    this.topButton.addActionListener(this);
    this.topButton.setActionCommand("top"); //$NON-NLS-1$
    this.bottomButton.addActionListener(this);
    this.bottomButton.setActionCommand("bottom"); //$NON-NLS-1$
    this.upButton.addActionListener(this);
    this.upButton.setActionCommand("up"); //$NON-NLS-1$
    this.downButton.addActionListener(this);
    this.downButton.setActionCommand("down"); //$NON-NLS-1$
    this.attributeButton.addActionListener(this);
    this.attributeButton.setActionCommand("attributes"); //$NON-NLS-1$

    this.tablemodel = new LayersTableModel();
    this.layersTable = new JTable(this.tablemodel);

    this.layersTable.setDragEnabled(false);
    this.layersTable.setSize(this.getSize());
    this.layersTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    this.layersTable
        .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    JScrollPane scrollpane = new JScrollPane(this.layersTable);

    Icon selectionIcon = new ImageIcon(this.getClass().getResource(
        "/images/icons/16x16/selection.png")); //$NON-NLS-1$
    Icon visibleIcon = new ImageIcon(this.getClass().getResource(
        "/images/icons/16x16/visible.png")); //$NON-NLS-1$
    Icon symbolizeIcon = new ImageIcon(this.getClass().getResource(
        "/images/icons/16x16/symbolize.png")); //$NON-NLS-1$
    JLabel selectionLabel = new JLabel("", selectionIcon, //$NON-NLS-1$
        SwingConstants.CENTER);
    selectionLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder")); //$NON-NLS-1$
    JLabel visibleLabel = new JLabel("", visibleIcon, //$NON-NLS-1$
        SwingConstants.CENTER);
    visibleLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder")); //$NON-NLS-1$
    JLabel symbolizeLabel = new JLabel("", symbolizeIcon, //$NON-NLS-1$
        SwingConstants.CENTER);
    symbolizeLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder")); //$NON-NLS-1$
    TableCellRenderer renderer = new JComponentTableCellRenderer();

    int width = this.getWidth();
    TableColumn col = this.layersTable.getColumnModel().getColumn(0);
    col.setMinWidth(22);
    col.setMaxWidth(22);
    col.setWidth(22);
    col.setResizable(false);
    col.setHeaderRenderer(renderer);
    col.setHeaderValue(selectionLabel);
    col.setCellRenderer(new CheckBoxCellRenderer());
    col.setCellEditor(new DefaultCellEditor(new JCheckBox()));
    col = this.layersTable.getColumnModel().getColumn(1);
    col.setMinWidth(22);
    col.setMaxWidth(22);
    col.setWidth(22);
    col.setResizable(false);
    col.setHeaderRenderer(renderer);
    col.setHeaderValue(visibleLabel);
    col.setCellRenderer(new CheckBoxCellRenderer());
    col.setCellEditor(new DefaultCellEditor(new JCheckBox()));
    col = this.layersTable.getColumnModel().getColumn(2);
    col.setMinWidth(22);
    col.setMaxWidth(22);
    col.setWidth(22);
    col.setResizable(false);
    col.setHeaderRenderer(renderer);
    col.setHeaderValue(symbolizeLabel);
    col.setCellRenderer(new CheckBoxCellRenderer());
    col.setCellEditor(new DefaultCellEditor(new JCheckBox()));
    col = this.layersTable.getColumnModel().getColumn(3);
    col.setMinWidth(60);
    col.setMaxWidth(60);
    col.setWidth(60);
    col.setResizable(false);
    col.setCellRenderer(new StyleRenderer());
    JTextField txtStyle = new JTextField();
    txtStyle.setEditable(false);
    col.setCellEditor(new StyleEditor(txtStyle));
    col = this.layersTable.getColumnModel().getColumn(4);
    col.setWidth(width - 126);
    col.setResizable(true);
    col.setCellRenderer(new LayersNameCellRenderer());
    JTextField txtName = new JTextField();
    txtName.setEditable(true);
    txtName.addKeyListener(new KeyListener() {
      @Override
      public void keyPressed(KeyEvent e) {
      }

      @Override
      public void keyReleased(KeyEvent e) {
        if (LayerLegendPanel.this.getSelectedLayers().size() == 1) {
          Layer layer = LayerLegendPanel.this.getSelectedLayers().iterator()
              .next();
          String newName = ((JTextField) e.getSource()).getText();
          LayerLegendPanel.this.parent.getDataSet().getPopulation(layer.getName())
          .setNom(newName);
          layer.setName(newName);
        }
      }

      @Override
      public void keyTyped(KeyEvent e) {
      }
    });
    col.setCellEditor(new DefaultCellEditor(txtName));
    this.layersTable.getTableHeader().setResizingColumn(col);

    this.layersTable.setFillsViewportHeight(true);
    this.layersTable.setRowHeight(40);
    this.layersTable.setRowMargin(3);
    this.layersTable.addKeyListener(new KeyListener() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
          LayerLegendPanel.this.removeSelectedLayers();
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
      }

      @Override
      public void keyTyped(KeyEvent e) {
      }
    });
    this.layersTable.getSelectionModel().addListSelectionListener(
        new ListSelectionListener() {
          @Override
          public void valueChanged(ListSelectionEvent e) {
            LayerLegendPanel.this.update();
          }
        });
    this.add(scrollpane);
    this.newLayerMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        LayerLegendPanel.this.parent.askAndAddNewLayer();
      }
    });
    this.selectMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        LayerViewPanel lvPanel = LayerLegendPanel.this.getLayerViewPanel();
        Collection<IFeature> features = lvPanel.getSelectedFeatures();
        for (Layer layer : LayerLegendPanel.this.getSelectedLayers()) {
          features.addAll(layer.getFeatureCollection());
        }
        lvPanel.getRenderingManager().render(
            lvPanel.getRenderingManager().getSelectionRenderer());
        lvPanel.superRepaint();
      }
    });
    this.unselectMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        LayerViewPanel lvPanel = LayerLegendPanel.this.getLayerViewPanel();
        Collection<IFeature> features = lvPanel.getSelectedFeatures();
        for (Layer layer : LayerLegendPanel.this.getSelectedLayers()) {
          features.removeAll(layer.getFeatureCollection());
        }
        lvPanel.getRenderingManager().render(
            lvPanel.getRenderingManager().getSelectionRenderer());
        lvPanel.superRepaint();
      }
    });
    this.centerViewMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        LayerViewPanel lvPanel = LayerLegendPanel.this.getLayerViewPanel();
        if (LayerLegendPanel.this.getSelectedLayers().size() == 1) {
          Layer layer = LayerLegendPanel.this.getSelectedLayers().iterator()
              .next();
          if (layer.getFeatureCollection() != null) {
            try {
              lvPanel.getViewport().zoom(
                  layer.getFeatureCollection().envelope());
            } catch (NoninvertibleTransformException e1) {
              e1.printStackTrace();
            }
            lvPanel.superRepaint();
          }
        }
      }
    });
    this.renameMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (LayerLegendPanel.this.getSelectedLayers().size() == 1) {
          Layer layer = LayerLegendPanel.this.getSelectedLayers().iterator()
              .next();
          String newName = JOptionPane.showInputDialog(LayerLegendPanel.this,
              I18N.getString("LayerLegendPanel.RenameLayer")); //$NON-NLS-1$
          if( newName == null || newName.isEmpty()){
              return;
          }
          LayerLegendPanel.this.parent.getDataSet().getPopulation(
              layer.getName()).setNom(newName);
          layer.setName(newName);

          LayerLegendPanel.this.repaint();
        }
      }
    });
    this.editSldMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        StyleEditionFrame styleEditionFrame = new StyleEditionFrame(
            LayerLegendPanel.this);
        styleEditionFrame.setVisible(true);
      }
    });
    this.deleteMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        LayerLegendPanel.this.removeSelectedLayers();
      }
    });
    this.editMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        displayAttrbuteTable();
      }
    });
    this.update();
    this.popupMenu.add(this.newLayerMenuItem);
    this.popupMenu.addSeparator();
    this.popupMenu.add(this.selectMenuItem);
    this.popupMenu.add(this.unselectMenuItem);
    this.popupMenu.addSeparator();
    this.popupMenu.add(this.centerViewMenuItem);
    this.popupMenu.add(this.editMenuItem);
    this.popupMenu.add(this.renameMenuItem);
    this.popupMenu.add(this.editSldMenuItem);
    this.popupMenu.add(this.changeStyleMenu);
    this.popupMenu.addSeparator();
    this.popupMenu.add(this.deleteMenuItem);
    this.layersTable.addMouseListener(new PopupListener());
  }

  @Override
  public final void stateChanged(final ChangeEvent e) {
    this.update();
    this.getLayerViewPanel().repaint();
  }

  /**
   * Update and repaint the layer legend panel.
   */
  void update() {
    boolean isSelectionEmpty = this.layersTable.getSelectedRows().length == 0;
    this.minusButton.setEnabled(!isSelectionEmpty
        && this.layersTable.getRowCount() > 0);
    if (isSelectionEmpty || this.layersTable.getRowCount() <= 1) {
      this.downButton.setEnabled(false);
      this.bottomButton.setEnabled(false);
      this.upButton.setEnabled(false);
      this.topButton.setEnabled(false);
    } else {
      boolean containsTopRow = (Arrays.binarySearch(
          this.layersTable.getSelectedRows(), 0) == 0);
      this.upButton.setEnabled(!containsTopRow);
      this.topButton.setEnabled(!containsTopRow);
      boolean containsBottomRow = (Arrays.binarySearch(
          this.layersTable.getSelectedRows(),
          this.layersTable.getRowCount() - 1) >= 0);
      this.downButton.setEnabled(!containsBottomRow);
      this.bottomButton.setEnabled(!containsBottomRow);
    }
    this.repaint();
  }

  /**
   * Remove the selected layers.
   */
  private void removeSelectedLayers() {
    if(this.layersTable.getRowCount() == 0){
        return;
    }
    List<Layer> toRemove = new ArrayList<Layer>();
    List<Integer> rowsToRemove = new ArrayList<Integer>();
    for (int row : this.layersTable.getSelectedRows()) {
      toRemove.add(this.getLayer(row));
      rowsToRemove.add(0, new Integer(row));
    }
    this.parent.removeLayers(toRemove);   

  }

  public Set<Layer> getSelectedLayers() {
    Set<Layer> selectedLayers = new HashSet<Layer>();
    for (int row : this.layersTable.getSelectedRows()) {
      Layer layer = this.getLayer(row);
      selectedLayers.add(layer);
    }
    return selectedLayers;
  }

    /**
     * Move the selected layers up.
     */
    private void moveSelectedLayersUp() {
        for (int row : this.layersTable.getSelectedRows()) {
            int sldIndex = row - 1;
            this.sldmodel.moveLayer(row, sldIndex);
        }
    }

  /**
   * Move the selected layers down.
   */
  private void moveSelectedLayersDown() {
      int[] liste = this.layersTable.getSelectedRows();
      for (int i = 0; i < (liste.length / 2); i++) {
          int temp = liste[i];
          liste[i] = liste[liste.length - i - 1];
          liste[liste.length - i - 1] = temp;
      }
      for (int row :liste) {
          int sldIndex = row + 1;
          this.sldmodel.moveLayer(row, sldIndex);
      }
  }

  /**
   * Move the Selected Layers To the Top.
   */
  private void moveSelectedLayersToTop() {
      int begin = 0;
      for (int row : this.layersTable.getSelectedRows()) {
          int sldIndex = begin;
          this.sldmodel.moveLayer(row, sldIndex);
          begin++;
      }
  }

    /**
     * Move the Selected Layers To the Bottom.
     */
    private void moveSelectedLayersToBottom() {
        int end = this.tablemodel.getRowCount() - 1;
        int[] liste = this.layersTable.getSelectedRows();
        for (int i = 0; i < (liste.length / 2); i++) {
            int temp = liste[i];
            liste[i] = liste[liste.length - i - 1];
            liste[liste.length - i - 1] = temp;
        }
        for (int row : liste) {
            int sldIndex = end;
            this.sldmodel.moveLayer(row, sldIndex);
            end--;
        }
    }

  private void displayAttrbuteTable() {
    AttributeTable ta = new AttributeTable(LayerLegendPanel.this
        .getLayerViewPanel().getProjectFrame(), I18N
        .getString("LayerLegendPanel.EditAttributes"), //$NON-NLS-1$
        LayerLegendPanel.this.getLayerViewPanel().getFeatures());
    ta.setVisible(true);
  }
  
  /**
   * Return the layer corresponding to the given row.
   * @param row row of the layer
   * @return the layer corresponding to the given row
   */
  public Layer getLayer(int row) {
      return this.sldmodel.getLayerAt(row);

  }
  

  /**
   * The TableModel of the Layer Legend Panel
   */
    public class LayersTableModel extends DefaultTableModel {
        private static final long serialVersionUID = 1L;

        @Override
        public int getColumnCount() {
            return 6;
        }

        @Override
        public int getRowCount() {
            return (LayerLegendPanel.this.sldmodel == null) ? 0
                    : LayerLegendPanel.this.sldmodel.layersCount();

        }

    @Override
    public Object getValueAt(int row, int col) {
      Layer layer = LayerLegendPanel.this.sldmodel.getLayerAt(row);
      if (row < LayerLegendPanel.this.sldmodel.layersCount()) {
        if (col == 0) {
          return new Boolean(layer.isSelectable());
        }
        if (col == 1) {
          return new Boolean(layer.isVisible());
        }
        if (col == 2) {
          return new Boolean(layer.isSymbolized());
        }
        if (col == 4) {
          return layer.getName();
        }
        if (col == 5) {
          ImageIcon image = layer.getIcon();
          if (image == null) {
            image = LayerLegendPanel.this.images[20];
            layer.setIcon(image);
          }
          return image;
        }
        return new JPanel();
      }
      return new Object();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
      return (col == 0 || col == 1 || col == 2 || col == 3 || col == 4);
    }

    @Override
    public String getColumnName(int column) {
      if (column == 0) {
        return I18N.getString("LayerLegendPanel.Selectable"); //$NON-NLS-1$
      }
      if (column == 1) {
        return I18N.getString("LayerLegendPanel.Visible"); //$NON-NLS-1$
      }
      if (column == 2) {
        return I18N.getString("LayerLegendPanel.Symbolized"); //$NON-NLS-1$
      }
      if (column == 4) {
        return I18N.getString("LayerLegendPanel.LayerName"); //$NON-NLS-1$
      }
      if (column == 5) {
        return "Rendering Progress"; //$NON-NLS-1$
      }
      return I18N.getString("LayerLegendPanel.Styles"); //$NON-NLS-1$
    }

    @Override
    public Class<?> getColumnClass(int column) {
      return this.getValueAt(0, column).getClass();
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
      Layer layer = LayerLegendPanel.this.getLayer(row);
      if (col == 0) {
        layer.setSelectable(((Boolean) value).booleanValue());
        // sld.fireActionPerformed();
        this.fireTableRowsUpdated(row, row);
      } else if (col == 1) {
        boolean visible = ((Boolean) value).booleanValue();
        layer.setVisible(visible);
        if (!visible) {
          // deselectionnerCouche(row);
        }
        LayerLegendPanel.this.getLayerViewPanel().repaint();
        this.fireTableCellUpdated(row, col);
      } else if (col == 2) {
        layer.setSymbolized(((Boolean) value).booleanValue());
        this.fireTableCellUpdated(row, col);
      } else if (col == 4) {
        String oldLayerName = layer.getName();
        String newLayerName = (String) value;
        if (!oldLayerName.equals(newLayerName)) {
          // newLayerName = getFrame().checkLayerName(newLayerName);
        }
        layer.setName(newLayerName);
        // frame.getPanelVisu().setSld(sld);
        for (int i = 0; i <LayerLegendPanel.this.sldmodel.layersCount(); i++) {
          String layerName = layer.getName();
          if (layerName.equals(oldLayerName)) {
            layer.setName(newLayerName);
            // frame.getPanelVisu().getDataset().getPopulations().
            // get(i).setNom(newLayerName);
            this.fireTableCellUpdated(i, col);
          }
        }
        // String fileName = frame.session.getFichiers().get(oldLayerName);
        // fichiers.remove(oldLayerName);
        // fichiers.put(newLayerName, fileName);
      } else {
        // couleurs.set(row, (JPanel)value);
        this.fireTableCellUpdated(row, col);
      }
    }
  }

  /**
     */
  class JComponentTableCellRenderer implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      return (JComponent) value;
    }
  }

  /**
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
      Layer layer = LayerLegendPanel.this.getLayer(row);
      if (row < LayerLegendPanel.this.sldmodel.layersCount()) {
        if (isSelected) {
          this.setBackground(new Color(252, 233, 158));
        } else {
          this.setBackground(Color.WHITE);
        }
        if (col == 0) {
          this.setToolTipText(I18N.getString("LayerLegendPanel." + //$NON-NLS-1$
              "SelectableToolTip")); //$NON-NLS-1$
          this.setSelected(layer.isSelectable());
        } else {
          if (col == 1) {
            this.setToolTipText(I18N.getString("LayerLegendPanel." + //$NON-NLS-1$
                "VisibleToolTip")); //$NON-NLS-1$
            this.setSelected(layer.isVisible());
          } else {
            if (col == 2) {
              this.setToolTipText(I18N.getString("LayerLegendPanel." + //$NON-NLS-1$
                  "SymbolizedToolTip")); //$NON-NLS-1$
              this.setSelected(layer.isSymbolized());
            }
          }
        }
        return this;
      }
      return null;
    }
  }

  /**
     */
  class LayersNameCellRenderer extends JTextField implements TableCellRenderer {
    /**
     * Serial uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public LayersNameCellRenderer() {
      super();
      this.setBackground(Color.white);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int col) {
      Layer layer = LayerLegendPanel.this.getLayer(row);
      if (row < LayerLegendPanel.this.sldmodel.layersCount()) {
        if (isSelected) {
          this.setBackground(new Color(252, 233, 158));
        } else {
          this.setBackground(Color.WHITE);
        }
        this.setText(layer.getName());
        return this;
      }
      return null;
    }
  }

  /**
     */
  class StyleRenderer extends JLabel implements TableCellRenderer {
    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean isFocus, int row, int col) {
      Layer layer = LayerLegendPanel.this.getLayer(row);
      LayerStylesPanel panel = new LayerStylesPanel(layer);
      return panel;
    }
  }

  /**
     */
  class StyleEditor extends DefaultCellEditor {

    private static final long serialVersionUID = 1L;
    protected JPanel panel;

    /**
     * Constructor
     * @param textField JTextField of the Layer Styles Panel.
     */
    public StyleEditor(JTextField textField) {
      super(textField);
      this.panel = new JPanel();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
      LayerLegendPanel layerLegendPanel = LayerLegendPanel.this;

      StyleEditionFrame styleEditionFrame = new StyleEditionFrame(
          layerLegendPanel);

      layerLegendPanel.getSelectedLayers().iterator().next()
          .setStyles(styleEditionFrame.getLayer().getStyles());

      JPanel pan = new LayerStylesPanel(styleEditionFrame.getLayer());

      layerLegendPanel.repaint();
      layerLegendPanel.getLayerViewPanel().superRepaint();
      return pan;
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("add")) { //$NON-NLS-1$
      this.parent.askAndAddNewLayer();
      return;
    }
    if (e.getActionCommand().equals("remove")) { //$NON-NLS-1$
      this.removeSelectedLayers();
      return;
    }
    if (e.getActionCommand().equals("up")) { //$NON-NLS-1$
      this.moveSelectedLayersUp();
      return;
    }
    if (e.getActionCommand().equals("down")) { //$NON-NLS-1$
      this.moveSelectedLayersDown();
      return;
    }
    if (e.getActionCommand().equals("top")) { //$NON-NLS-1$
      this.moveSelectedLayersToTop();
      return;
    }
    if (e.getActionCommand().equals("bottom")) { //$NON-NLS-1$
      this.moveSelectedLayersToBottom();
      return;
    }
    if (e.getActionCommand().equals("attributes")) { //$NON-NLS-1$
      this.displayAttrbuteTable();
      return;
    }
    if (e.getID() == 3) { // rendering starts
    }
    if (e.getID() == 4) { // rendering a feature
      LayerRenderer renderer = (LayerRenderer) e.getSource();
      int n = e.getModifiers() * 2 / 10;
      renderer.getLayer().setIcon(this.images[n]);
      this.tablemodel.fireTableDataChanged();
      this.layersTable.repaint();
      this.update();
    }
    if (e.getID() == 5) { // rendering finished
      LayerRenderer renderer = (LayerRenderer) e.getSource();
      renderer.getLayer().setIcon(this.images[20]);
      this.tablemodel.fireTableDataChanged();
      this.layersTable.repaint();
      this.update();
    }
  }
  /**
     *
     */
  class PopupListener extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent e) {
      this.maybeShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      this.maybeShowPopup(e);
    }

    /**
     * Affiche un menu popup si l'évènement souris est l'évènement d'affichage
     * du menu popup.
     * @param e évènement souris
     */
    private void maybeShowPopup(MouseEvent e) {
      int selectedRow = e.getY()
          / LayerLegendPanel.this.layersTable.getRowHeight();
      if (SwingUtilities.isRightMouseButton(e)) {
        if (selectedRow < LayerLegendPanel.this.layersTable.getRowCount()) {
          LayerLegendPanel.this.centerViewMenuItem.setEnabled(true);
          LayerLegendPanel.this.unselectMenuItem.setEnabled(true);
          LayerLegendPanel.this.selectMenuItem.setEnabled(true);
          LayerLegendPanel.this.deleteMenuItem.setEnabled(true);
          LayerLegendPanel.this.renameMenuItem.setEnabled(true);
          LayerLegendPanel.this.editSldMenuItem.setEnabled(true);
          LayerLegendPanel.this.editMenuItem.setEnabled(true);
          LayerLegendPanel.this.changeStyleMenu.removeAll();
          LayerLegendPanel.this.changeStyleMenu.setEnabled(false);
          if (!LayerLegendPanel.this.getSelectedLayers().isEmpty()) {
            Layer layer = LayerLegendPanel.this.getSelectedLayers().iterator()
                .next();
            if (layer.getGroups().size() > 1) {
              LayerLegendPanel.this.changeStyleMenu.setEnabled(true);
            }
            for (final String group : layer.getGroups()) {
              JMenuItem item = new JMenuItem(group);
              LayerLegendPanel.this.changeStyleMenu.add(item);
              item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  Layer layer = LayerLegendPanel.this.getSelectedLayers()
                      .iterator().next();
                  layer.setActiveGroup(group);
                  LayerViewPanel lvPanel = LayerLegendPanel.this
                      .getLayerViewPanel();
                  lvPanel.repaint();
                }
              });
            }
          }
          LayerLegendPanel.this.layersTable.setRowSelectionInterval(
              selectedRow, selectedRow);
          LayerLegendPanel.this.popupMenu.show(e.getComponent(), e.getX(),
              e.getY());
        } else {
          LayerLegendPanel.this.centerViewMenuItem.setEnabled(false);
          LayerLegendPanel.this.unselectMenuItem.setEnabled(false);
          LayerLegendPanel.this.selectMenuItem.setEnabled(false);
          LayerLegendPanel.this.editMenuItem.setEnabled(false);
          LayerLegendPanel.this.renameMenuItem.setEnabled(false);
          LayerLegendPanel.this.editSldMenuItem.setEnabled(false);
          LayerLegendPanel.this.deleteMenuItem.setEnabled(false);
          LayerLegendPanel.this.popupMenu.show(e.getComponent(), e.getX(),
              e.getY());
        }
      } else {
        if (SwingUtilities.isLeftMouseButton(e)) {
          if (LayerLegendPanel.this.tablemodel.getRowCount() > 0
              && selectedRow >= LayerLegendPanel.this.tablemodel.getRowCount()) {
            LayerLegendPanel.this.layersTable.removeRowSelectionInterval(0,
                LayerLegendPanel.this.tablemodel.getRowCount() - 1);
          }
        }
      }
    }
  }

public void setModel(StyledLayerDescriptor sld) {
    this.sldmodel = sld;
    this.sldmodel.addSldListener(this);
}

public StyledLayerDescriptor getModel() {
    return this.sldmodel;
}

/**
 * Listener SLD
 */

    @Override
    public void layerOrderChanged(int oldIndex, int newIndex) {
        this.repaint();
        this.layersTable.getSelectionModel().clearSelection();
        logger.info("layer moved from " + oldIndex + "to " + newIndex //$NON-NLS-1$ //$NON-NLS-2$
                + " caught by LayerLegendPane"); //$NON-NLS-1$
    }

    @Override
    public void layerAdded(Layer l) {
        this.repaint();
        logger.info("layer addition caught by LayerLegendPane"); //$NON-NLS-1$

    }

    @Override
    public void layersRemoved(Collection<Layer> layers) {
        this.repaint();
        logger.info("layers deletion caught by LayerLegendPane"); //$NON-NLS-1$

    }



}
