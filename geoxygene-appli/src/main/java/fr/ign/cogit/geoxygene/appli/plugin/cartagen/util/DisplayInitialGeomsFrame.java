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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

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
    this.jtable = new JTable(new DisplayLayerTableModel(data));
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
        frame.getLayerViewPanel().getRenderingManager().addLayer(layer);
      } else
        SLDUtil.removeInitialGeomDisplay(layer);
      // IGeneObj obj;
      // for (IFeature f: layer.getFeatureCollection()){
      // obj = (IGeneObj) f;
      // System.out.println(obj.getInitialGeom());
      // System.out.println(obj.getGeom());
      // }
    }
    frame.getLayerViewPanel().getRenderingManager().renderAll();
    // frame.getLayerViewPanel().repaint();
  }

}
