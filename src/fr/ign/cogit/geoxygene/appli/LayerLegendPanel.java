/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

/**
 * Panel displaying layer legends.
 * @author Julien Perret
 * @author Sylvain Becuwe
 */
public class LayerLegendPanel extends JPanel implements ChangeListener {
    /**
     * serial version uid.
     */
    private static final long serialVersionUID = -6860364246334166387L;
    /**
     * sld of the layer legend panel.
     */
    private StyledLayerDescriptor sld = null;
    /**
     * Get sld of the layer legend panel.
     * @return sld of the layer legend panel
     */
    public StyledLayerDescriptor getSld() { return this.sld; }

    /**
     * 
     */
    private LayerViewPanel layerViewPanel = null;
    /**
     * @return
     */
    public LayerViewPanel getLayerViewPanel() { return this.layerViewPanel; }
    
    /**
     * 
     */
    private DefaultTableModel tablemodel = null;
    /**
     * 
     */
    private JTable layersTable = null;

    /**
     * @param theSld
     *            sld of the layer legend panel.
     * @param theLayerViewPanel
     *            the layer view panel associated with the layer legend panel
     */
    public LayerLegendPanel(final StyledLayerDescriptor theSld,
            final LayerViewPanel theLayerViewPanel) {
        super();
        this.sld = theSld;
        this.sld.addChangeListener(this);
        this.layerViewPanel = theLayerViewPanel;

        this.tablemodel = new LayersTableModel();
        this.layersTable = new JTable(this.tablemodel);

        this.layersTable.setDragEnabled(false);
        this.layersTable.setSize(getSize());
        this.layersTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        this.layersTable.setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollpane = new JScrollPane(this.layersTable);

        Icon selectionIcon = new ImageIcon(
                "images/icons/16x16/selection.png"); //$NON-NLS-1$
        Icon visibleIcon = new ImageIcon(
                "images/icons/16x16/visible.png"); //$NON-NLS-1$
        Icon symbolizeIcon = new ImageIcon(
                "images/icons/16x16/symbolize.png"); //$NON-NLS-1$
        JLabel selectionLabel = new JLabel("", selectionIcon, //$NON-NLS-1$
                SwingConstants.CENTER);
        selectionLabel.setBorder(UIManager.getBorder(
                "TableHeader.cellBorder")); //$NON-NLS-1$
        JLabel visibleLabel = new JLabel("", visibleIcon, //$NON-NLS-1$
                SwingConstants.CENTER);
        visibleLabel.setBorder(UIManager.getBorder(
                "TableHeader.cellBorder")); //$NON-NLS-1$
        JLabel symbolizeLabel = new JLabel("", symbolizeIcon, //$NON-NLS-1$
                SwingConstants.CENTER);
        symbolizeLabel.setBorder(UIManager.getBorder(
                "TableHeader.cellBorder")); //$NON-NLS-1$
        TableCellRenderer renderer = new JComponentTableCellRenderer();

        int width = getWidth();
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
        //col.setCellEditor(new CouleursEditor(new JTextField()));
        col = this.layersTable.getColumnModel().getColumn(4);
        col.setWidth(width-126);
        col.setResizable(true);
        col.setCellRenderer(new LayersNameCellRenderer());
        col.setCellEditor(new DefaultCellEditor(new JTextField()));
        this.layersTable.getTableHeader().setResizingColumn(col);

        this.layersTable.setFillsViewportHeight(true);
        this.layersTable.setRowHeight(40);
        this.layersTable.setRowMargin(3);
        this.layersTable.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    //removeSelectedRow();
                }
            }
            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {}
        });
        add(scrollpane);
    }

    @Override
    public final void stateChanged(final ChangeEvent e) {
        this.update();
    }

    /**
     * Update and repaint the layer legend panel.
     */
    private void update() {
        /*
        this.removeAll();
        for (Layer layer : this.sld.getLayers()) {
            this.add(new JLabel(layer.getName()));
        }
        this.validate();
        */
        this.repaint();
    }
    
    /**
     * Return the layer corresponding to the given row
     * @param row row of the layer
     * @return the layer corresponding to the given row
     */
    public Layer getLayer(int row) {
        return this.getSld().getLayers().get(
                this.getSld().getLayers().size() - 1 - row);
    }
    /**
     * Le tableModel du panneauLegende
     */
    public class LayersTableModel extends DefaultTableModel{
        private static final long serialVersionUID = 1L;
        @Override
        public int getColumnCount() { return 5; }
        @Override
        public int getRowCount() { return LayerLegendPanel.this.getSld().getLayers().size(); }
        @Override
        public Object getValueAt(int row, int col) {
            Layer layer = LayerLegendPanel.this.getLayer(row);
            if (row < LayerLegendPanel.this.getSld().getLayers().size()) {
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
                return I18N.getString(
                        "LayerLegendPanel.Selectable"); //$NON-NLS-1$
            }
            if (column == 1) {
                return I18N.getString(
                        "LayerLegendPanel.Visible"); //$NON-NLS-1$
            }
            if (column == 2) {
                return I18N.getString(
                        "LayerLegendPanel.Symbolized"); //$NON-NLS-1$
            }
            if (column == 4) {
                return I18N.getString(
                        "LayerLegendPanel.LayerName"); //$NON-NLS-1$
            }
            return I18N.getString(
                    "LayerLegendPanel.Styles"); //$NON-NLS-1$
        }
        @Override
        public Class<?> getColumnClass(int column) {
            return getValueAt(0,column).getClass();
        }
        @Override
        public void setValueAt(Object value, int row, int col) {
            Layer layer = LayerLegendPanel.this.getLayer(row);
            if (col == 0) {
                layer.setSelectable(((Boolean) value).booleanValue());
                //sld.fireActionPerformed();
                fireTableRowsUpdated(row,row);
            }
            else if (col == 1) {
                boolean visible = ((Boolean)value).booleanValue();
                layer.setVisible(visible);
                if (!visible) {
                    //deselectionnerCouche(row);
                }
                getLayerViewPanel().repaint();
                fireTableCellUpdated(row,col);
            }
            else if (col == 2) {                    
                layer.setSymbolized(((Boolean)value).booleanValue());
                fireTableCellUpdated(row,col);
            }
            else if (col == 4) {
                String oldLayerName = layer.getName();
                String newLayerName =(String)value;
                if (!oldLayerName.equals(newLayerName)) {
                     //newLayerName = getFrame().checkLayerName(newLayerName);
                }
                layer.setName(newLayerName);
                //frame.getPanelVisu().setSld(sld);
                for (int i = 0 ; i < LayerLegendPanel.this.getSld()
                .getLayers().size() ; i++) {
                    String layerName = layer.getName();
                    if (layerName.equals(oldLayerName)) {
                        layer.setName(newLayerName);
                        //frame.getPanelVisu().getDataset().getPopulations().get(i).setNom(newLayerName);
                        fireTableCellUpdated(i,col);    
                    }
                }
                //String fileName = frame.session.getFichiers().get(oldLayerName);
                //fichiers.remove(oldLayerName);
                //fichiers.put(newLayerName, fileName);
            } else {
                //couleurs.set(row, (JPanel)value);
                fireTableCellUpdated(row,col);                  
            }
        }
    }
    /**
     */
    class JComponentTableCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
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
            setBackground(Color.white);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int col) {
            Layer layer = LayerLegendPanel.this.getLayer(row);
            if (row < LayerLegendPanel.this.getSld().getLayers().size()) {
                if (isSelected) {
                    setBackground(new Color(252, 233, 158));
                } else {
                    setBackground(Color.WHITE);
                }
                if (col == 0) {
                    setToolTipText(I18N.getString(
                            "LayerLegendPanel." + //$NON-NLS-1$
                            "SelectableToolTip")); //$NON-NLS-1$
                    setSelected(layer.isSelectable());
                } else {
                    if (col == 1) {
                        setToolTipText(I18N.getString(
                                "LayerLegendPanel." + //$NON-NLS-1$
                                "VisibleToolTip")); //$NON-NLS-1$
                        setSelected(layer.isVisible());
                    } else { 
                        if (col == 2) {
                            setToolTipText(I18N.getString(
                                    "LayerLegendPanel." + //$NON-NLS-1$
                                    "SymbolizedToolTip")); //$NON-NLS-1$
                            setSelected(layer.isSymbolized());
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
    class LayersNameCellRenderer extends JTextField
    implements TableCellRenderer {
        /**
         * Serial uid.
         */
        private static final long serialVersionUID = 1L;
        /**
         * Constructor.
         */
        public LayersNameCellRenderer() {
            super();
            setBackground(Color.white);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int col) {
            Layer layer = LayerLegendPanel.this.getLayer(row);
            if (row < LayerLegendPanel.this.getSld().getLayers().size()) {
                if (isSelected) {
                    setBackground(new Color(252, 233, 158));
                } else {
                    setBackground(Color.WHITE);
                }
                setText(layer.getName());
                return this;
            }
            return null;
        }
    }
    class StyleRenderer extends JLabel implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean isFocus, int row, int col) {
            Layer layer = LayerLegendPanel.this.getLayer(row);
            LayerStylesPanel panel = new LayerStylesPanel(layer);
            return panel;
        }
    }
}
