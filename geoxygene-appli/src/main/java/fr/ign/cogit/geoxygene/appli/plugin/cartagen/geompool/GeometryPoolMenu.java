/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.geompool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.carto.SLDUtilCartagen;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.GeometryPool;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.geompool.GeomPoolFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component.JColorSelectionButton;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.util.ColorEditor;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.util.ColorRenderer;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.util.DisplayLayerTableModel;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.NamedLayer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.UserStyle;
import fr.ign.cogit.geoxygene.util.algo.SmallestSurroundingRectangleComputation;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.BufferComputing;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.Side;

/**
 * Extra menu that contains utility functions of CartAGen.
 * @author GTouya
 * 
 */
public class GeometryPoolMenu extends JMenu {

  static Logger logger = Logger.getLogger(GeometryPoolMenu.class.getName());

  private static GeometryPoolMenu instance;

  /**
   * The Geometry Pool layer
   */
  private NamedLayer geomPoolLayer;
  private Color defaultColor = Color.RED;
  private GeOxygeneApplication application;
  private GeometryPool geometryPool;

  // Geometries pool
  /**
     */
  public JCheckBoxMenuItem mGeomPoolVisible = new JCheckBoxMenuItem(
      new GeomPoolVisibleAction());
  /**
   */
  public JMenuItem mGeomPoolManagement = new JMenuItem(
      new GeomPoolManagementAction());
  /**
     */
  private JMenuItem mGeomPoolEmpty = new JMenuItem(new GeomPoolEmptyAction());
  /**
     */
  private JMenuItem mGeomPoolAddObjects = new JMenuItem(
      new GeomPoolAddObjectsAction());

  private JMenuItem mGeomPoolAddDeleted = new JMenuItem(
      new GeomPoolAddDeletedAction());
  /**
     */
  public JCheckBoxMenuItem mGeomPoolDrawSegments = new JCheckBoxMenuItem(
      "Draw segments");
  public IDirectPositionList mGeomPoolDrawSegmentsCoords = new DirectPositionList();

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public GeometryPoolMenu(String title, GeOxygeneApplication application) {
    super(title);
    this.application = application;
    // geometries pool menu
    this.setToolTipText("geometries pool: a set of geometries usefull to display");
    this.geomPoolLayer = new NamedLayer();
    this.geomPoolLayer.setName(CartAGenDataSet.GEOM_POOL);
    this.geomPoolLayer.getStyles().add(new UserStyle());

    this.mGeomPoolVisible.setSelected(false);
    this.add(this.mGeomPoolVisible);
    this.add(this.mGeomPoolManagement);

    this.add(this.mGeomPoolEmpty);
    this.add(this.mGeomPoolAddObjects);
    this.add(this.mGeomPoolAddDeleted);
    this.add(new JMenuItem(new AddBufferAction()));

    this.mGeomPoolDrawSegments.setSelected(false);
    this.add(this.mGeomPoolDrawSegments);
    instance = this;
  }

  public static GeometryPoolMenu getInstance() {
    return instance;
  }

  public GeometryPool getGeometryPool() {
    return geometryPool;
  }

  public void setGeometryPool(GeometryPool geometryPool) {
    this.geometryPool = geometryPool;
  }

  /**
   * Action that enables the anti-aliasing in the CartAGen display.
   * @author GTouya
   * 
   */
  class GeomPoolManagementAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      GeomPoolFrame.getInstance().setVisible(true);
    }

    public GeomPoolManagementAction() {
      putValue(Action.SHORT_DESCRIPTION,
          "Show the Geometry Pool management frame");
      putValue(Action.NAME, "Management");
    }
  }

  /**
   * @author GTouya
   * 
   */
  class GeomPoolAddObjectsAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      if (CartAGenDoc.getInstance().getCurrentDataset() == null) {
        if (geometryPool == null)
          geometryPool = new GeometryPool(DataSet.getInstance(),
              CartAGenPlugin.getInstance().getApplication().getMainFrame()
                  .getSelectedProjectFrame().getSld());
        for (IFeature feat : CartAGenPlugin.getInstance().getApplication()
            .getMainFrame().getSelectedProjectFrame().getLayerViewPanel()
            .getSelectedFeatures()) {
          geometryPool.addFeatureToGeometryPool(feat, defaultColor);
        }
      } else {
        CartAGenDoc
            .getInstance()
            .getCurrentDataset()
            .getGeometryPool()
            .setSld(
                CartAGenPlugin.getInstance().getApplication().getMainFrame()
                    .getSelectedProjectFrame().getSld());
        for (IFeature feat : CartAGenPlugin.getInstance().getApplication()
            .getMainFrame().getSelectedProjectFrame().getLayerViewPanel()
            .getSelectedFeatures()) {
          if (!(feat instanceof IGeneObj)) {
            continue;
          }
          CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
              .addFeatureToGeometryPool(feat, defaultColor);
        }
      }
      CartAGenPlugin.getInstance().getApplication().getMainFrame()
          .getSelectedProjectFrame().getLayerViewPanel().validate();
    }

    public GeomPoolAddObjectsAction() {
      putValue(Action.NAME, "Add selected objects geometry");
    }
  }

  /**
   * @author GTouya
   * 
   */
  class AddBufferAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      Set<IGeneObj> objects = new HashSet<IGeneObj>();
      for (IFeature feat : CartAGenPlugin.getInstance().getApplication()
          .getMainFrame().getSelectedProjectFrame().getLayerViewPanel()
          .getSelectedFeatures()) {
        if (!(feat instanceof IGeneObj)) {
          continue;
        }
        objects.add((IGeneObj) feat);
      }
      AddBufferEtcFrame frame = new AddBufferEtcFrame(objects);
      frame.setVisible(true);
    }

    public AddBufferAction() {
      putValue(Action.NAME, "Add buffer geometry, etc.");
    }
  }

  class AddBufferEtcFrame extends JFrame implements ActionListener {

    /****/
    private static final long serialVersionUID = 1L;
    private Set<IGeneObj> objects;
    private JCheckBox chkBuffer, chkHalfR, chkHalfL, chkCentroid, chkOffsetR,
        chkOffsetL, chkMbr, chkConvex;
    private JColorSelectionButton colorBuffer, colorHalfR, colorHalfL,
        colorCentroid, colorOffsetR, colorOffsetL, colorMbr, colorConvex;
    private JSpinner spinBuffer, spinHalfR, spinHalfL, spinOffsetR,
        spinOffsetL, spinBufferWidth, spinHalfRWidth, spinHalfLWidth,
        spinOffsetRWidth, spinOffsetLWidth, spinMbrWidth, spinConvexWidth;

    AddBufferEtcFrame(Set<IGeneObj> objects) {
      super("Draw in geometry pool");
      this.objects = objects;
      this.setSize(700, 500);
      this.setAlwaysOnTop(true);
      // panels for each option
      SpinnerModel bufferModel = new SpinnerNumberModel(20.0, 1.0, 100.0, 1.0);
      SpinnerModel widthModel = new SpinnerNumberModel(1, 1, 10, 1);
      // buffer panel
      JPanel pBuffer = new JPanel();
      chkBuffer = new JCheckBox("Buffer");
      pBuffer.add(chkBuffer);
      pBuffer.add(Box.createHorizontalGlue());
      spinBuffer = new JSpinner(bufferModel);
      spinBuffer.setPreferredSize(new Dimension(50, 20));
      spinBuffer.setMaximumSize(new Dimension(50, 20));
      spinBuffer.setMinimumSize(new Dimension(50, 20));
      spinBufferWidth = new JSpinner(widthModel);
      spinBufferWidth.setPreferredSize(new Dimension(40, 20));
      spinBufferWidth.setMaximumSize(new Dimension(40, 20));
      spinBufferWidth.setMinimumSize(new Dimension(40, 20));
      pBuffer.add(new JLabel(" radius: "));
      pBuffer.add(spinBuffer);
      pBuffer.add(Box.createHorizontalGlue());
      pBuffer.add(new JLabel("  line width: "));
      pBuffer.add(spinBufferWidth);
      pBuffer.add(Box.createHorizontalGlue());
      colorBuffer = new JColorSelectionButton();
      pBuffer.add(colorBuffer);
      pBuffer.setLayout(new BoxLayout(pBuffer, BoxLayout.X_AXIS));
      // half buffer right panel
      JPanel pBufferR = new JPanel();
      chkHalfR = new JCheckBox("Half Buffer Right");
      pBufferR.add(chkHalfR);
      pBufferR.add(Box.createHorizontalGlue());
      spinHalfR = new JSpinner(bufferModel);
      spinHalfR.setPreferredSize(new Dimension(50, 20));
      spinHalfR.setMaximumSize(new Dimension(50, 20));
      spinHalfR.setMinimumSize(new Dimension(50, 20));
      spinHalfRWidth = new JSpinner(widthModel);
      spinHalfRWidth.setPreferredSize(new Dimension(40, 20));
      spinHalfRWidth.setMaximumSize(new Dimension(40, 20));
      spinHalfRWidth.setMinimumSize(new Dimension(40, 20));
      pBufferR.add(new JLabel(" radius: "));
      pBufferR.add(spinHalfR);
      pBufferR.add(Box.createHorizontalGlue());
      pBufferR.add(new JLabel("  line width: "));
      pBufferR.add(spinHalfRWidth);
      pBufferR.add(Box.createHorizontalGlue());
      colorHalfR = new JColorSelectionButton();
      pBufferR.add(colorHalfR);
      pBufferR.setLayout(new BoxLayout(pBufferR, BoxLayout.X_AXIS));
      // half buffer left panel
      JPanel pBufferL = new JPanel();
      chkHalfL = new JCheckBox("Half Buffer Left");
      pBufferL.add(chkHalfL);
      pBufferL.add(Box.createHorizontalGlue());
      spinHalfL = new JSpinner(bufferModel);
      spinHalfL.setPreferredSize(new Dimension(50, 20));
      spinHalfL.setMaximumSize(new Dimension(50, 20));
      spinHalfL.setMinimumSize(new Dimension(50, 20));
      spinHalfLWidth = new JSpinner(widthModel);
      spinHalfLWidth.setPreferredSize(new Dimension(40, 20));
      spinHalfLWidth.setMaximumSize(new Dimension(40, 20));
      spinHalfLWidth.setMinimumSize(new Dimension(40, 20));
      pBufferL.add(new JLabel(" radius: "));
      pBufferL.add(spinHalfL);
      pBufferL.add(Box.createHorizontalGlue());
      pBufferL.add(new JLabel("  line width: "));
      pBufferL.add(spinHalfLWidth);
      pBufferL.add(Box.createHorizontalGlue());
      colorHalfL = new JColorSelectionButton();
      pBufferL.add(colorHalfL);
      pBufferL.setLayout(new BoxLayout(pBufferL, BoxLayout.X_AXIS));
      // Offset line right panel
      JPanel pOffsetR = new JPanel();
      chkOffsetR = new JCheckBox("Offset Line Right");
      pOffsetR.add(chkOffsetR);
      pOffsetR.add(Box.createHorizontalGlue());
      spinOffsetR = new JSpinner(bufferModel);
      spinOffsetR.setPreferredSize(new Dimension(50, 20));
      spinOffsetR.setMaximumSize(new Dimension(50, 20));
      spinOffsetR.setMinimumSize(new Dimension(50, 20));
      spinOffsetRWidth = new JSpinner(widthModel);
      spinOffsetRWidth.setPreferredSize(new Dimension(40, 20));
      spinOffsetRWidth.setMaximumSize(new Dimension(40, 20));
      spinOffsetRWidth.setMinimumSize(new Dimension(40, 20));
      pOffsetR.add(new JLabel(" radius: "));
      pOffsetR.add(spinOffsetR);
      pOffsetR.add(Box.createHorizontalGlue());
      pOffsetR.add(new JLabel("  line width: "));
      pOffsetR.add(spinOffsetRWidth);
      pOffsetR.add(Box.createHorizontalGlue());
      colorOffsetR = new JColorSelectionButton();
      pOffsetR.add(colorOffsetR);
      pOffsetR.setLayout(new BoxLayout(pOffsetR, BoxLayout.X_AXIS));
      // Offset line left panel
      JPanel pOffsetL = new JPanel();
      chkOffsetL = new JCheckBox("Offset Line Left");
      pOffsetL.add(chkOffsetL);
      pOffsetL.add(Box.createHorizontalGlue());
      spinOffsetL = new JSpinner(bufferModel);
      spinOffsetL.setPreferredSize(new Dimension(50, 20));
      spinOffsetL.setMaximumSize(new Dimension(50, 20));
      spinOffsetL.setMinimumSize(new Dimension(50, 20));
      spinOffsetLWidth = new JSpinner(widthModel);
      spinOffsetLWidth.setPreferredSize(new Dimension(40, 20));
      spinOffsetLWidth.setMaximumSize(new Dimension(40, 20));
      spinOffsetLWidth.setMinimumSize(new Dimension(40, 20));
      pOffsetL.add(new JLabel(" radius: "));
      pOffsetL.add(spinOffsetL);
      pOffsetL.add(Box.createHorizontalGlue());
      pOffsetL.add(new JLabel("  line width: "));
      pOffsetL.add(spinOffsetLWidth);
      pOffsetL.add(Box.createHorizontalGlue());
      colorOffsetL = new JColorSelectionButton();
      pOffsetL.add(colorOffsetL);
      pOffsetL.setLayout(new BoxLayout(pOffsetL, BoxLayout.X_AXIS));
      // centroid panel
      JPanel pCentroid = new JPanel();
      chkCentroid = new JCheckBox("Centroid");
      pCentroid.add(chkCentroid);
      pCentroid.add(Box.createHorizontalGlue());
      colorCentroid = new JColorSelectionButton();
      pCentroid.add(colorCentroid);
      pCentroid.setLayout(new BoxLayout(pCentroid, BoxLayout.X_AXIS));
      // MBR panel
      JPanel pMbr = new JPanel();
      chkMbr = new JCheckBox("MBR");
      pMbr.add(chkMbr);
      pMbr.add(Box.createHorizontalGlue());
      spinMbrWidth = new JSpinner(widthModel);
      spinMbrWidth.setPreferredSize(new Dimension(40, 20));
      spinMbrWidth.setMaximumSize(new Dimension(40, 20));
      spinMbrWidth.setMinimumSize(new Dimension(40, 20));
      pMbr.add(spinMbrWidth);
      pMbr.add(Box.createHorizontalGlue());
      colorMbr = new JColorSelectionButton();
      pMbr.add(colorMbr);
      pMbr.setLayout(new BoxLayout(pMbr, BoxLayout.X_AXIS));
      // convex hull panel
      JPanel pConvex = new JPanel();
      chkConvex = new JCheckBox("Convex Hull");
      pConvex.add(chkConvex);
      pConvex.add(Box.createHorizontalGlue());
      spinConvexWidth = new JSpinner(widthModel);
      spinConvexWidth.setPreferredSize(new Dimension(40, 20));
      spinConvexWidth.setMaximumSize(new Dimension(40, 20));
      spinConvexWidth.setMinimumSize(new Dimension(40, 20));
      pConvex.add(spinConvexWidth);
      pConvex.add(Box.createHorizontalGlue());
      colorConvex = new JColorSelectionButton();
      pConvex.add(colorConvex);
      pConvex.setLayout(new BoxLayout(pConvex, BoxLayout.X_AXIS));

      // define a panel with the OK and Cancel buttons
      JPanel btnPanel = new JPanel();
      JButton okBtn = new JButton("OK");
      okBtn.addActionListener(this);
      okBtn.setActionCommand("OK");
      JButton cancelBtn = new JButton(I18N.getString("MainLabels.lblCancel"));
      cancelBtn.addActionListener(this);
      cancelBtn.setActionCommand("cancel");
      btnPanel.add(okBtn);
      btnPanel.add(cancelBtn);
      btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

      JPanel mainPanel = new JPanel();
      mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
      mainPanel.add(pBuffer);
      mainPanel.add(Box.createVerticalGlue());
      mainPanel.add(pBufferR);
      mainPanel.add(Box.createVerticalGlue());
      mainPanel.add(pBufferL);
      mainPanel.add(Box.createVerticalGlue());
      mainPanel.add(pOffsetR);
      mainPanel.add(Box.createVerticalGlue());
      mainPanel.add(pOffsetL);
      mainPanel.add(Box.createVerticalGlue());
      mainPanel.add(pCentroid);
      mainPanel.add(Box.createVerticalGlue());
      mainPanel.add(pMbr);
      mainPanel.add(Box.createVerticalGlue());
      mainPanel.add(pConvex);

      this.getContentPane().add(new JScrollPane(mainPanel));
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(btnPanel);
      this.getContentPane().setLayout(
          new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("OK")) {
        drawToGeomPool();
        CartAGenPlugin.getInstance().getApplication().getMainFrame()
            .getSelectedProjectFrame().getLayerViewPanel().validate();
        this.dispose();
      } else {
        this.dispose();
      }
    }

    private void drawToGeomPool() {
      CartAGenDoc
          .getInstance()
          .getCurrentDataset()
          .getGeometryPool()
          .setSld(application.getMainFrame().getSelectedProjectFrame().getSld());
      for (IGeneObj obj : objects) {
        if (chkBuffer.isSelected()) {
          IGeometry buffer = obj.getGeom().buffer(
              (Double) spinBuffer.getValue());
          CartAGenDoc
              .getInstance()
              .getCurrentDataset()
              .getGeometryPool()
              .addFeatureToGeometryPool(buffer, colorBuffer.getColor(),
                  (Integer) spinBufferWidth.getValue());
        }
        if (chkHalfR.isSelected()) {
          if (obj.getGeom() instanceof ILineString) {
            IGeometry buffer = BufferComputing.buildLineHalfBuffer(
                (ILineString) obj.getGeom(), (Double) spinHalfR.getValue(),
                Side.RIGHT);

            CartAGenDoc
                .getInstance()
                .getCurrentDataset()
                .getGeometryPool()
                .addFeatureToGeometryPool(buffer, colorHalfR.getColor(),
                    (Integer) spinHalfRWidth.getValue());
          }
        }
        if (chkHalfL.isSelected()) {
          if (obj.getGeom() instanceof ILineString) {
            IGeometry buffer = BufferComputing.buildLineHalfBuffer(
                (ILineString) obj.getGeom(), (Double) spinHalfL.getValue(),
                Side.LEFT);
            CartAGenDoc
                .getInstance()
                .getCurrentDataset()
                .getGeometryPool()
                .addFeatureToGeometryPool(buffer, colorHalfL.getColor(),
                    (Integer) spinHalfLWidth.getValue());
          }
        }
        if (chkOffsetR.isSelected()) {
          if (obj.getGeom() instanceof ILineString) {
            IGeometry buffer = BufferComputing.buildHalfOffsetLine(Side.RIGHT,
                (ILineString) obj.getGeom(), (Double) spinOffsetR.getValue());
            CartAGenDoc
                .getInstance()
                .getCurrentDataset()
                .getGeometryPool()
                .addFeatureToGeometryPool(buffer, colorOffsetR.getColor(),
                    (Integer) spinOffsetRWidth.getValue());
          }
        }
        if (chkOffsetL.isSelected()) {
          if (obj.getGeom() instanceof ILineString) {
            IGeometry buffer = BufferComputing.buildHalfOffsetLine(Side.LEFT,
                (ILineString) obj.getGeom(), (Double) spinOffsetL.getValue());
            CartAGenDoc
                .getInstance()
                .getCurrentDataset()
                .getGeometryPool()
                .addFeatureToGeometryPool(buffer, colorOffsetL.getColor(),
                    (Integer) spinOffsetLWidth.getValue());
          }
        }
        if (chkCentroid.isSelected()) {
          IGeometry centroid = obj.getGeom().centroid().toGM_Point();
          CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
              .addFeatureToGeometryPool(centroid, colorCentroid.getColor(), 1);
        }
        if (chkMbr.isSelected()) {
          IGeometry mbr = SmallestSurroundingRectangleComputation.getSSR(obj
              .getGeom());
          CartAGenDoc
              .getInstance()
              .getCurrentDataset()
              .getGeometryPool()
              .addFeatureToGeometryPool(mbr, colorMbr.getColor(),
                  (Integer) spinMbrWidth.getValue());
        }
        if (chkConvex.isSelected()) {
          IGeometry hull = obj.getGeom().convexHull();
          CartAGenDoc
              .getInstance()
              .getCurrentDataset()
              .getGeometryPool()
              .addFeatureToGeometryPool(hull, colorConvex.getColor(),
                  (Integer) spinConvexWidth.getValue());
        }
      }
    }
  }

  /**
   * Empty the geometry pool.
   * @author GTouya
   * 
   */
  class GeomPoolEmptyAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      if (CartAGenDoc.getInstance().getCurrentDataset() != null)
        CartAGenDoc.getInstance().getCurrentDataset().getGeometryPoolPop()
            .clear();
      else
        application.getMainFrame().getSelectedProjectFrame()
            .getLayer("Geometry Pool").getFeatureCollection().clear();
    }

    public GeomPoolEmptyAction() {
      putValue(Action.SHORT_DESCRIPTION, "Empty the Geometry Pool");
      putValue(Action.NAME, "Empty");
    }
  }

  /**
   * Add the geometries of deleted objects to the geometry pool.
   * @author GTouya
   * 
   */
  class GeomPoolAddDeletedAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      GeomPoolAddDeletedFrame frame = new GeomPoolAddDeletedFrame();
      frame.setVisible(true);
    }

    public GeomPoolAddDeletedAction() {
      putValue(Action.SHORT_DESCRIPTION,
          "Add the geometries of deleted objects to the geometry pool");
      putValue(Action.NAME, "Add deleted objects to geometry pool");
    }
  }

  class GeomPoolAddDeletedFrame extends JFrame implements ActionListener {

    /****/
    private static final long serialVersionUID = 1L;

    private JTable jtable;
    private StyledLayerDescriptor sld;
    private ProjectFrame frame;
    private JButton okBtn, applyBtn, cancelBtn;
    private List<Layer> layers = new ArrayList<Layer>();

    GeomPoolAddDeletedFrame() {
      super(I18N.getString("Display Deleted Features"));
      this.frame = application.getMainFrame().getSelectedProjectFrame();
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
      okBtn.setActionCommand("OK");
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
    public void actionPerformed(ActionEvent arg0) {
      if (arg0.getActionCommand().equals("OK")) {
        this.displayGeoms();
        this.dispose();
      } else if (arg0.getActionCommand().equals("cancel")) {
        this.dispose();
      } else if (arg0.getActionCommand().equals("apply")) {
        this.displayGeoms();
      }
    }

    private void displayGeoms() {
      if (CartAGenDoc.getInstance().getCurrentDataset() == null) {
        if (geometryPool == null)
          geometryPool = new GeometryPool(DataSet.getInstance(),
              CartAGenPlugin.getInstance().getApplication().getMainFrame()
                  .getSelectedProjectFrame().getSld());
      } else {
        CartAGenDoc
            .getInstance()
            .getCurrentDataset()
            .getGeometryPool()
            .setSld(
                CartAGenPlugin.getInstance().getApplication().getMainFrame()
                    .getSelectedProjectFrame().getSld());
        geometryPool = CartAGenDoc.getInstance().getCurrentDataset()
            .getGeometryPool();
      }

      for (int i = 0; i < jtable.getRowCount(); i++) {
        Layer layer = sld.getLayer((String) jtable.getModel().getValueAt(i, 0));
        boolean display = (Boolean) jtable.getValueAt(i, 1);
        if (display) {
          for (IFeature feat : layer.getFeatureCollection()) {
            if (feat.isDeleted()) {
              Color color = (Color) jtable.getModel().getValueAt(i, 2);
              Object widthVal = jtable.getModel().getValueAt(i, 3);
              int width = 1;
              if (widthVal instanceof String)
                width = Integer.valueOf((String) widthVal);
              else
                width = (Integer) widthVal;
              geometryPool.addFeatureToGeometryPool(feat.getGeom(), color,
                  width);
            }
          }
        }
      }

      CartAGenPlugin.getInstance().getApplication().getMainFrame()
          .getSelectedProjectFrame().getLayerViewPanel().validate();
    }
  }

  /**
   * Make the Geometry Pool layer visible or not.
   * @author GTouya
   * 
   */
  class GeomPoolVisibleAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      geomPoolLayer.setSld(CartAGenPlugin.getInstance().getApplication()
          .getMainFrame().getSelectedProjectFrame().getSld());
      if (geomPoolLayer != null) {
        geomPoolLayer.setVisible(mGeomPoolVisible.isSelected());
        ProjectFrame frame = CartAGenPlugin.getInstance().getApplication()
            .getMainFrame().getSelectedProjectFrame();
        if (mGeomPoolVisible.isSelected()) {
          if (DataSet.getInstance()
              .getPopulation(geomPoolLayer.getName()) == null) {
            geomPoolLayer
                .getSld()
                .getDataSet()
                .addPopulation(
                    new Population<IFeature>(geomPoolLayer.getName()));
          }
          frame.addLayer(geomPoolLayer);
        } else {
          List<Layer> toRemove = new ArrayList<Layer>();
          toRemove.add(geomPoolLayer);
          frame.removeLayers(toRemove);
        }
      }
    }

    public GeomPoolVisibleAction() {
      putValue(Action.SHORT_DESCRIPTION,
          "Make the Geometry Pool visible or not");
      putValue(Action.NAME, "Visible");
    }
  }

}
