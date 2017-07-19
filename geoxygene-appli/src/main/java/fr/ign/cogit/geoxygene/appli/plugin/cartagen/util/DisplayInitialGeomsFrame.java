/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
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

import fr.ign.cogit.cartagen.core.SLDUtilCartagen;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.util.renderer.ColorRenderer;
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
      FeatureTypeStyle style = SLDUtilCartagen.getLayerInitialDisplay(layer);
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
    this.getContentPane()
        .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
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
        SLDUtilCartagen.addInitialGeomDisplay(layer, color, width);
        frame.getLayerViewPanel().getRenderingManager().addLayer(layer);
      } else
        SLDUtilCartagen.removeInitialGeomDisplay(layer);
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
