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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.panel.COGITColorChooserPanel;
import fr.ign.cogit.geoxygene.appli.ui.GenericParameterUI;
import fr.ign.cogit.geoxygene.appli.ui.StyleInterpolationUI;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.LabelPlacement;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LinePlacement;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Placement;
import fr.ign.cogit.geoxygene.style.PointPlacement;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;

/**
 * Style Edition Main Frame.
 * 
 * @author Charlotte Hoarau
 */
public class StyleEditionFrame extends JDialog implements ActionListener,
    MouseListener, ChangeListener, ItemListener {

  private static final long serialVersionUID = 1L;

  private static final String CR = System.getProperty("line.separator");

  private static final int borderSize = 20;

  private static final String NO_EXPRESSIVE_TAG = "No expressive description";

  private static Logger logger = Logger.getLogger(StyleEditionFrame.class
      .getName());

  // Main GeOxygene application elements
  private final LayerLegendPanel layerLegendPanel;
  private final LayerViewPanel layerViewPanel;

  /**
   * The layer which style will be modified. Element of the SLD of the project
   */
  private Layer layer;

  public Layer getLayer() {
    return this.layer;
  }

  /**
   * The initial SLD styles before modifications
   */
  private StyledLayerDescriptor initialSLD = null;

  /**
   * @return The initial SLD styles before modifications
   */
  public StyledLayerDescriptor getInitialSLD() {
    return this.initialSLD;
  }

  /**
   * @param sldToCopy The initial SLD styles before modifications
   */
  public void copyInitialSLD(StyledLayerDescriptor sldToCopy) {
    if (sldToCopy == null) {
      return;
    }
    CharArrayWriter writer = new CharArrayWriter();
    sldToCopy.marshall(writer);
    Reader reader = new CharArrayReader(writer.toCharArray());
    try {
      this.initialSLD = StyledLayerDescriptor.unmarshall(reader);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
  }

  // Main Dialog Elements
  private JPanel visuPanel;
  private static LayerStylesPanel stylePanel;

  private JPanel fillPanel;
  private JPanel strokePanel;
  private JPanel strokePanel2;
  private JPanel symbolPanel;
  private JPanel toponymPanel;
  private JPanel placementPanel;
  private JPanel fontPanel;

  private JButton btnAddStyle;
  private JButton btnApply;
  private JButton btnValid;
  private JButton btnCancel;
  private JTabbedPane tabPane;
  private JPanel textStylePanel;
  private JPanel mainStylePanel;
  private final JPanel graphicStylePanel;
  private JPanel infoPanel;
  private JTextArea infoTextArea;

  // Work variables
  private Color fillColor;
  private float fillOpacity;

  private Color strokeColor;
  private float strokeOpacity;
  private double strokeWidth;
  private int strokeLineJoin = BasicStroke.JOIN_ROUND;
  private int strokeLineCap = BasicStroke.CAP_ROUND;
  private final String[] strokeLineJoinNames = { "Miter", //$NON-NLS-1$
      "Round", //$NON-NLS-1$
      "Bevel" //$NON-NLS-1$
  };
  private final String[] strokeLineCapNames = { "Butt", //$NON-NLS-1$
      "Round", //$NON-NLS-1$
      "Square" //$NON-NLS-1$
  };

  private String unit;

  private Color strokeColor2;
  private float strokeOpacity2;
  private double strokeWidth2;

  private String symbolShape;
  private float symbolSize;

  private TextSymbolizer symbolizer;
  private FeatureTypeStyle featureTypeStyle;

  // modif abdel

  private List<LinePanel> ListLine = null;
  private List<PolygonePanel> ListPolygone = null;
  private List<PointPanel> ListPoint = null;

  private HashMap<Integer, JDialog> listStrokeDialog = new HashMap<Integer, JDialog>(
      10);
  private MyListStroke maList;
  private JPanel panelTrait;
  private final int MAX_STROKE = 15;

  // fin modif

  // Dialog Elements
  private JColorChooser fillColorChooser;
  private JDialog fillDialog;
  private JLabel fillColorLabel;
  private JLabel fillFinalColorLabel;
  private JSlider fillOpacitySlider;

  private JColorChooser strokeColorChooser;
  private JDialog strokeDialog;
  private JLabel strokeColorLabel;
  private JLabel strokeFinalColorLabel;
  private JSlider strokeOpacitySlider;
  private JSpinner strokeWidthSpinner;
  private JRadioButton unitMeterRadio;
  private JRadioButton unitPixelRadio;
  private JComboBox strokeLineJoinCombo;
  private JComboBox strokeLineCapCombo;
  private StrokeSamplePanel strokeSamplePanel;

  private JColorChooser strokeColorChooser2;
  private JDialog strokeDialog2;
  private JLabel strokeColorLabel2;
  private JLabel strokeFinalColorLabel2;
  private JSlider strokeOpacitySlider2;
  private JSpinner strokeWidthSpinner2;

  private JComboBox symbolShapeCombo;
  private JSpinner symbolSizeSpinner;

  private ImageIcon[] images;
  private final String[] symbolsDescription = {
      I18N.getString("StyleEditionFrame.Square"), //$NON-NLS-1$
      I18N.getString("StyleEditionFrame.Circle"), //$NON-NLS-1$
      I18N.getString("StyleEditionFrame.Triangle"), //$NON-NLS-1$
      I18N.getString("StyleEditionFrame.Star"), //$NON-NLS-1$
      I18N.getString("StyleEditionFrame.Cross"), //$NON-NLS-1$
      I18N.getString("StyleEditionFrame.X"), //$NON-NLS-1$
      I18N.getString("StyleEditionFrame.hline"), //$NON-NLS-1$
      I18N.getString("StyleEditionFrame.vline") //$NON-NLS-1$
  };
  private final String[] symbols = { "square", //$NON-NLS-1$
      "circle", //$NON-NLS-1$
      "triangle", //$NON-NLS-1$
      "star", //$NON-NLS-1$
      "cross", //$NON-NLS-1$
      "x", //$NON-NLS-1$
      "hline", //$NON-NLS-1$
      "vline" //$NON-NLS-1$
  };

  private JButton addColorMapButton;
  private JButton addCategorizedMapButton;

  private JCheckBox toponymBtn;
  private JComboBox fields;
  private JLabel textPreviewLabel;
  private JComboBox placements;
  private JCheckBox repeatBtn;

  private JComboBox expressiveStrokeComboBox;
  private JComboBox expressiveFillComboBox;
  private final JScrollPane fillExpressiveScrollPane = new JScrollPane();
  private final JScrollPane strokeExpressiveScrollPane = new JScrollPane();
  private final JScrollPane styleInterpolationScrollPane = new JScrollPane();

  private JPanel buttonPanel;

  private JScrollPane sp;

  private JPanel strokePanel3;

  private Color strokeColor3;

  private float strokeOpacity3;

  private float strokeWidth3;

  private JPanel fillPanel2;

  private Color fillColor2;

  private float fillOpacity2;

  private JButton addColorMapButton2;

  private JButton addCategorizedMapButton2;

  /**
   * Style Edition Main Frame.
   * 
   * @param layerLegendPanel the layerLegendPanel of the style to be modified.
   */
  public StyleEditionFrame(LayerLegendPanel layerLegendPanel) {
    super(SwingUtilities.getWindowAncestor(layerLegendPanel), I18N
        .getString("StyleEditionFrame.StyleEdition"));
    logger.info("StyleEditionFrame");
    this.layerLegendPanel = layerLegendPanel;
    this.layerViewPanel = this.layerLegendPanel.getLayerViewPanel();
    this.graphicStylePanel = new JPanel();

    if (layerLegendPanel.getSelectedLayers().size() == 1) {
      this.layer = layerLegendPanel.getSelectedLayers().iterator().next();
    }
    DataSet dataset = layerLegendPanel.getLayerViewPanel().getProjectFrame()
        .getDataSet();

    // Saving the initial SLD
    this.copyInitialSLD(this.layerViewPanel.getProjectFrame().getSld());

    if (this.layer.getSymbolizer().isPolygonSymbolizer()) {
      // System.out.println("initPolygon");
      this.initPolygon();
    } else if (this.layer.getSymbolizer().isLineSymbolizer()) {
      // System.out.println("initline");
      this.initLine();
    } else if (this.layer.getSymbolizer().isPointSymbolizer()) {
      // System.out.println("initPoint");

      this.initPoint();
    }
    this.initializeGui(false);
  }

  private void initializeGui(boolean forceEdition) {

    this.getContentPane().removeAll();
    this.getContentPane().setLayout(new BorderLayout());

    // if (layerLegendPanel.getLayerViewPanel().getProjectFrame()
    // .getSldEditionLock()) {
    // JPanel panel = new JPanel(new BorderLayout());
    // JLabel label = new JLabel(
    // "<html><center><font color='red' family='bold' size='+2'>"
    // + I18N.getString("EditionFrame.SLDInEditionWarningMessage")
    // + "</font></center></html>");
    //
    // label.setBorder(BorderFactory.createEmptyBorder(borderSize,
    // borderSize, borderSize, borderSize));
    // label.setOpaque(true);
    // label.setBackground(Color.white);
    // panel.add(label, BorderLayout.CENTER);
    // JButton button = new JButton("OK");
    // button.addActionListener(new ActionListener() {
    //
    // @Override
    // public void actionPerformed(ActionEvent e) {
    // StyleEditionFrame.this.dispose();
    // }
    // });
    // panel.add(button, BorderLayout.SOUTH);
    // this.setContentPane(panel);
    // this.setModalityType(ModalityType.APPLICATION_MODAL);
    // this.pack();
    // return;
    //
    // } else {
    // this.addWindowListener(new WindowAdapter() {
    // @Override
    // public void windowClosed(WindowEvent e) {
    // StyleEditionFrame.this.layerLegendPanel.getLayerViewPanel()
    // .getProjectFrame().setSldEditionLock(false);
    // }
    // });
    // layerLegendPanel.getLayerViewPanel().getProjectFrame()
    // .setSldEditionLock(true);
    // }

    if (!forceEdition
        && this.layerLegendPanel.getLayerViewPanel().getProjectFrame()
            .getSldEditionOwners().size() > 0) {
      JPanel panel = new JPanel(new GridBagLayout());
      JLabel label = new JLabel(
          "<html><center><font color='red' family='bold' size='+2'>"
              + I18N.getString("EditionFrame.SLDInEditionWarningMessage")
              + "</font></center></html>");

      label.setBorder(BorderFactory.createEmptyBorder(borderSize, borderSize,
          borderSize, borderSize));
      label.setOpaque(true);
      label.setBackground(Color.white);
      Insets insets = new Insets(2, 2, 2, 2);
      panel.add(label, new GridBagConstraints(0, 0, 2, 1, 1, 1,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets,
          borderSize, borderSize));
      JButton buttonOk = new JButton("Close");
      buttonOk.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          StyleEditionFrame.this.dispose();
        }
      });
      panel.add(buttonOk, new GridBagConstraints(1, 1, 1, 1, 0, 0,
          GridBagConstraints.CENTER, GridBagConstraints.NONE, insets,
          borderSize, borderSize));
      JButton buttonForceEdition = new JButton("Edit at your own risk anyway");
      buttonForceEdition.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          StyleEditionFrame.this.initializeGui(true);
        }
      });
      panel.add(buttonForceEdition, new GridBagConstraints(0, 1, 1, 1, 0, 0,
          GridBagConstraints.CENTER, GridBagConstraints.NONE, insets,
          borderSize, borderSize));
      this.setContentPane(panel);
      this.setModalityType(ModalityType.APPLICATION_MODAL);
      this.pack();
      return;

    } else {
      // /////////////////////////////////// SYMBOLOGY
      this.textStylePanel = new JPanel();
      this.textStylePanel.setLayout(new BoxLayout(this.textStylePanel,
          BoxLayout.Y_AXIS));

      this.initTextStylePanel();

      // //////////////////////////////////// INFO
      this.infoPanel = new JPanel(new BorderLayout());
      this.infoTextArea = new JTextArea();
      this.infoPanel.add(new JScrollPane(this.infoTextArea),
          BorderLayout.CENTER);
      // //////////////////////////////////// TABBED PANELS
      this.tabPane = new JTabbedPane();
      this.tabPane
          .add(
              I18N.getString("StyleEditionFrame.Symbology"), this.graphicStylePanel); //$NON-NLS-1$
      this.tabPane.add(
          I18N.getString("StyleEditionFrame.Toponyms"), this.textStylePanel); //$NON-NLS-1$
      this.tabPane
          .add(I18N.getString("StyleEditionFrame.Info"), this.infoPanel); //$NON-NLS-1$
      this.tabPane.addChangeListener(new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
          StyleEditionFrame.this.updateInfoPanel();

        }
      });

      // modif abdel
      // ajout de tabedpane
      this.add(this.tabPane, BorderLayout.CENTER);
      // creation des boutons
      JButton buttonApply = this.createButtonApply();
      JButton buttonValid = this.createButtonValid();
      JButton buttonCancel = this.createButtonCancel();
      this.buttonPanel = new JPanel();
      this.buttonPanel.add(buttonApply);
      this.buttonPanel.add(buttonValid);
      this.buttonPanel.add(buttonCancel);
      this.buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
      
      // TODO : manage style edition for raster (interactive colormap, etc.)
      if(!layer.getSymbolizer().isRasterSymbolizer()) {
          this.mainStylePanel.add(this.buttonPanel);
      }
      
      this.buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
      // ajout des boutons
      this.add(this.buttonPanel, BorderLayout.SOUTH);

      // fin modif

      // this.add(this.tabPane);
//      this.addOrReplaceExpressiveRenderingUI(); //TODO RESET 
      this.addOrReplaceStyleInterpolationUI();

      this.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
          StyleEditionFrame.this.layerLegendPanel.getLayerViewPanel()
              .getProjectFrame().releaseSldEditionLock(StyleEditionFrame.this);
        }
      });
      this.layerLegendPanel.getLayerViewPanel().getProjectFrame()
          .addSldEditionLock(this);

    }
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        ((JDialog) e.getSource()).dispose();
      }
    });

    // this.pack();
    // this.buttonPanel.setSize(700,30);
    this.textStylePanel.setSize(600, 500);
    this.graphicStylePanel.setSize(620, 700);
    this.setSize(620, 730);
    this.setResizable(false);
    // this.setSize(650, 800);
    this.setLocation(300, 0);
    this.setAlwaysOnTop(false);

    if (this.layer.getSymbolizer().isLineSymbolizer()
        || this.layer.getSymbolizer().isPolygonSymbolizer()) {

      this.listvalueChanged();
    }

  }

  // /**
  // * Special UI for Layer rendered by a primitive renderer object.
  // *
  // * TODO: This method is only used by JeT while (GL) PrimitiveRenderers are
  // not
  // * completely integrated in the whole rendering pipeline. It should be
  // * transparent for AWT rendering pipeline
  // */
  // private void addPrimitiveRendererUI() {
  // if (this.tabPane == null) {
  // throw new IllegalStateException(
  // "this method can only be called after tabPane creation");
  // }
  // RenderingManager renderingManager = this.layerViewPanel
  // .getRenderingManager();
  // if (renderingManager == null) {
  // return;
  // }
  // LayerRenderer renderer = renderingManager.getRenderer(this.layer);
  // if (renderer == null) {
  // return;
  // }
  // if (!(renderer instanceof LwjglLayerRenderer)) {
  // return;
  // }
  // // PrimitiveRendererUI rendererUI =
  // //
  // PrimitiveRendererUIFactory.getPrimitiveRendererUI(((LwjglLayerRenderer)
  // // renderer).getLayerRenderer());
  // // if (rendererUI != null) {
  // // rendererUI.addChangeListener(new ChangeListener() {
  // //
  // // @Override
  // // public void stateChanged(ChangeEvent e) {
  // //
  // StyleEditionFrame.this.layerLegendPanel.getModel().fireActionPerformed(null);
  // // StyleEditionFrame.this.layerLegendPanel.repaint();
  // // StyleEditionFrame.this.layerViewPanel.repaint();
  // // }
  // // });
  // // this.tabPane.add(renderer.getClass().getSimpleName(), new
  // // JScrollPane(rendererUI.getGUI()));
  // // }
  //
  // }

  protected void updateInfoPanel() {
    IFeatureCollection<IFeature> collection = (IFeatureCollection<IFeature>) this.layer
        .getFeatureCollection();

    StringBuilder str = new StringBuilder();
    str.append("Symbolizer class : "
        + this.layer.getSymbolizer().getClass().getSimpleName() + CR);
    str.append("Symbolizer toString: " + this.symbolizer.toString());
    if (this.layer.getSymbolizer().isPolygonSymbolizer()) {
      PolygonSymbolizer pol = ((PolygonSymbolizer) (this.layer.getSymbolizer()));

//      Fill2DDescriptor fill2dDescriptor = pol.getFill().getFill2DDescriptor();
//      if (fill2dDescriptor instanceof Texture) {
//        Texture textureDescriptor = (Texture) fill2dDescriptor;
//        String textureUID = TextureManager.generateTextureUniqueFilename(
//            textureDescriptor, collection);
//        str.append("texture filename : '" + textureUID + "'" + CR);
//
//      }
//      if (fill2dDescriptor instanceof GradientSubshaderDescriptor) {
//        GradientSubshaderDescriptor gradientDescriptor = (GradientSubshaderDescriptor) fill2dDescriptor;
//        // str.append("texture filename : '" + textureUID + "'" + CR);
//
//      }
    }
    this.infoTextArea.setText(str.toString());
  }

  private void addOrReplaceStyleInterpolationUI() {
    if (this.tabPane == null) {
      throw new IllegalStateException(
          "this method can only be called after tabPane creation");
    }
    String tabTooltip = "Style Interpolation";
    this.tabPane.remove(this.styleInterpolationScrollPane);
    // Stroke Expressive UI
    GenericParameterUI ui = new StyleInterpolationUI(this.layer,
        this.layerViewPanel.getProjectFrame().getSld(),
        this.layerViewPanel.getProjectFrame());

    JComponent comp = ui.getGui();

    if (comp != null) {
      this.styleInterpolationScrollPane.setViewportView(comp);
      this.tabPane.addTab(tabTooltip, null, this.styleInterpolationScrollPane,
          tabTooltip);
    }

  }

//  private void addOrReplaceExpressiveRenderingUI() {
//    if (this.tabPane == null) {
//      throw new IllegalStateException(
//          "this method can only be called after tabPane creation");
//    }
//    String tabTooltip = "";
//    this.tabPane.remove(this.fillExpressiveScrollPane);
//    this.tabPane.remove(this.strokeExpressiveScrollPane);
//    // Stroke Expressive UI
//    GenericParameterUI ui = null;
//    if (((AbstractSymbolizer) this.layer.getSymbolizer()).getStroke() != null) {
//      StrokeExpressiveRenderingDescriptor expressiveStroke = ((AbstractSymbolizer) (this.layer
//          .getSymbolizer())).getStroke().getExpressiveRendering();
//      if (expressiveStroke != null) {
//        ui = ExpressiveRenderingUIFactory.getExpressiveRenderingUI(
//            expressiveStroke, this.layerViewPanel.getProjectFrame());
//        tabTooltip = expressiveStroke.getClass().getSimpleName();
//      }
//    }
//    if (ui != null) {
//      this.strokeExpressiveScrollPane.setViewportView(ui.getGui());
//      this.tabPane.addTab("Expressive Stroke", null,
//          this.strokeExpressiveScrollPane, tabTooltip);
//    }
//    // Fill expressive UI
//    ui = null;
//    if (this.layer.getSymbolizer().isPolygonSymbolizer()
//        && ((PolygonSymbolizer) (this.layer.getSymbolizer())).getFill()
//            .getFill2DDescriptor() != null) {
//      PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) this.layer
//          .getSymbolizer();
//      Fill2DDescriptor expressiveFill = polygonSymbolizer.getFill()
//          .getFill2DDescriptor();
//      if (expressiveFill != null) {
//        ui = ExpressiveRenderingUIFactory.getExpressiveRenderingUI(
//            expressiveFill, this.layerViewPanel.getProjectFrame());
//        tabTooltip = expressiveFill.getClass().getSimpleName();
//      }
//    }
//    if (ui != null) {
//      this.fillExpressiveScrollPane.setViewportView(ui.getGui());
//      this.tabPane.addTab("Expressive Fill", null,
//          this.fillExpressiveScrollPane, tabTooltip);
//    }
//  }

  public void initTextStylePanel() {
    // Initialisation du symboliser s'il y a lieu
    Style lastStyle = this.layer.getStyles().get(
        this.layer.getStyles().size() - 1);
    Symbolizer featureSymbolizer = lastStyle.getFeatureTypeStyles()
        .get(lastStyle.getFeatureTypeStyles().size() - 1).getSymbolizer();
    if (featureSymbolizer != null && featureSymbolizer.isTextSymbolizer()
        && this.symbolizer == null) {
      this.symbolizer = (TextSymbolizer) featureSymbolizer;
    }
    if (this.symbolizer == null) {
      this.createTextSymbolizer();
    }
    if (this.layer.getFeatureCollection().getFeatureType() != null) {
      this.toponymPanel = this.createToponymPanel();
      this.toponymPanel.setAlignmentX(LEFT_ALIGNMENT);
      this.textStylePanel.add(this.toponymPanel, BorderLayout.NORTH);
    }
    this.placementPanel = this.createPlacementPanel();
    this.placementPanel.setAlignmentX(LEFT_ALIGNMENT);
    this.fontPanel = this.createFontPanel();
    this.fontPanel.setAlignmentX(LEFT_ALIGNMENT);

    this.textStylePanel.add(this.placementPanel);
    this.textStylePanel.add(this.fontPanel);
    //        JButton closeBtn = new JButton(I18N.getString("AttributeTable.Close")); //$NON-NLS-1$
    // closeBtn.addActionListener(new ActionListener() {
    // @Override
    // public void actionPerformed(ActionEvent e) {
    // StyleEditionFrame.this.dispose();
    // }
    // });
    // closeBtn.setSize(150, 20);
    // this.textStylePanel.add(closeBtn);
  }

  public JPanel createToponymPanel() {

    JPanel toponymPanel = new JPanel();
    toponymPanel.setLayout(new BoxLayout(toponymPanel, BoxLayout.Y_AXIS));

    TitledBorder fillTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
    fillTitleBorder.setTitleColor(Color.blue);
    fillTitleBorder.setTitleFont(new Font("Verdana", Font.BOLD, 16)); //$NON-NLS-1$
    fillTitleBorder.setTitle("Toponyms"); //$NON-NLS-1$
    toponymPanel.setBorder(fillTitleBorder);
    toponymPanel.setPreferredSize(new Dimension(400, 100));

    JLabel label = new JLabel(I18N.getString("StyleEditionFrame.DisplayField")); //$NON-NLS-1$

    JPanel fieldsPanel = new JPanel();
    fieldsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    fieldsPanel.add(label);

    if (this.layer.getFeatureCollection().getFeatureType() != null) {
      String[] fieldsStr = new String[this.layer.getFeatureCollection()
          .getFeatureType().getFeatureAttributes().size()];
      for (int i = 0; i < fieldsStr.length; i++) {
        fieldsStr[i] = this.layer.getFeatureCollection().getFeatureType()
            .getFeatureAttributes().get(i).getMemberName();
      }
      this.fields = new JComboBox(fieldsStr);
      this.fields.setPreferredSize(this.fields.getPreferredSize());
      this.fields.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          logger.error(e.getActionCommand());
          if (StyleEditionFrame.this.symbolizer != null
              && ((JComboBox) e.getSource()).getSelectedItem() != null) {
            StyleEditionFrame.this.symbolizer.setLabel(((JComboBox) e
                .getSource()).getSelectedItem().toString());
          }
          StyleEditionFrame.this.update();
        }
      });
      fieldsPanel.add(this.fields);
    }
    JPanel topoBtnPanel = new JPanel();
    topoBtnPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    this.toponymBtn = new JCheckBox(
        I18N.getString("StyleEditionFrame.DisplayToponyms")); //$NON-NLS-1$
    topoBtnPanel.add(this.toponymBtn);
    if (this.symbolizer != null && this.symbolizer.getLabel() != null) {
      this.fields.setSelectedItem(this.symbolizer.getLabel());
      this.toponymBtn.setSelected(true);
    }
    this.toponymBtn.addItemListener(this);
    fieldsPanel.setAlignmentX(LEFT_ALIGNMENT);
    toponymPanel.add(fieldsPanel);
    topoBtnPanel.setAlignmentX(LEFT_ALIGNMENT);
    toponymPanel.add(topoBtnPanel);

    return toponymPanel;
  }

  /**
   * Enable toponyms display.
   */
  protected void enableToponyms() {
    Layer layer = StyleEditionFrame.this.layer;
    if (StyleEditionFrame.this.featureTypeStyle == null) {
      Style lastStyle = layer.getStyles().get(layer.getStyles().size() - 1);
      StyleEditionFrame.this.featureTypeStyle = new FeatureTypeStyle();
      Rule rule = new Rule();
      StyleEditionFrame.this.featureTypeStyle.getRules().add(rule);
      lastStyle.getFeatureTypeStyles().add(
          StyleEditionFrame.this.featureTypeStyle);
    }
    if (StyleEditionFrame.this.symbolizer == null) {
      this.createTextSymbolizer();
    }
    this.symbolizer.setFont(new fr.ign.cogit.geoxygene.style.Font(
        this.textPreviewLabel.getFont()));
    this.symbolizer.setLabel(this.fields.getSelectedItem().toString());
    this.symbolizer.setFill(new Fill());
    this.symbolizer.getFill().setFill(this.textPreviewLabel.getForeground());
    this.featureTypeStyle.getRules().get(0).getSymbolizers()
        .add(0, this.symbolizer);
  }

  private void createTextSymbolizer() {
    this.symbolizer = new TextSymbolizer();
    this.symbolizer.setUnitOfMeasurePixel();
    this.pointPlacement = new PointPlacement();
    this.linePlacement = new LinePlacement();
    boolean usePointPlacement = true;
    if (this.layer.getFeatureCollection().getFeatureType() != null) {
      Class<? extends IGeometry> geomType = this.layer.getFeatureCollection()
          .getFeatureType().getGeometryType();
      if (ICurve.class.isAssignableFrom(geomType)
          || IMultiCurve.class.isAssignableFrom(geomType)) {
        usePointPlacement = false;
      }
    }
    LabelPlacement labelPlacement = new LabelPlacement();
    Placement selectedPlacement = usePointPlacement ? this.pointPlacement
        : this.linePlacement;
    labelPlacement.setPlacement(selectedPlacement);
    this.symbolizer.setLabelPlacement(labelPlacement);
    this.symbolizer.setFont(new fr.ign.cogit.geoxygene.style.Font(new Font(
        "Verdana", Font.BOLD, 12))); //$NON-NLS-1$
    this.symbolizer.setFill(new Fill());
    this.symbolizer.getFill().setFill(Color.BLACK);
  }

  /**
   * Disable toponyms display.
   */
  protected void disableToponyms() {
    StyleEditionFrame.this.featureTypeStyle.getRules().get(0).getSymbolizers()
        .remove(this.symbolizer);
  }

  /**
   * Update the display (layer legend and layer view).
   */
  public void update() {
    this.layerLegendPanel.getModel().fireActionPerformed(null);
    this.layerLegendPanel.repaint();
    this.layerViewPanel.repaint();
  }

  public JPanel createPlacementPanel() {
    JPanel placementPanel = new JPanel();
    placementPanel.setLayout(new BoxLayout(placementPanel, BoxLayout.Y_AXIS));
    // create the title
    TitledBorder fillTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
    fillTitleBorder.setTitleColor(Color.blue);
    fillTitleBorder.setTitleFont(new Font("Verdana", Font.BOLD, 16)); //$NON-NLS-1$
    fillTitleBorder.setTitle("Placement"); //$NON-NLS-1$
    placementPanel.setBorder(fillTitleBorder);
    placementPanel.setPreferredSize(new Dimension(400, 100));
    // create a panel for the placement type
    JPanel placementTypePanel = new JPanel();
    placementTypePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    placementTypePanel.setAlignmentX(LEFT_ALIGNMENT);
    // create a label to ask for the type of placement
    JLabel label = new JLabel(I18N.getString("StyleEditionFrame.PlacementType")); //$NON-NLS-1$
    placementTypePanel.add(label);
    this.placements = new JComboBox(new String[] {
        PointPlacement.class.getSimpleName(),
        LinePlacement.class.getSimpleName() });
    this.placements.setPreferredSize(this.placements.getPreferredSize());
    Placement currentPlacement = this.symbolizer.getLabelPlacement()
        .getPlacement();
    if (currentPlacement != null) {
      this.placements.setSelectedItem(currentPlacement.getClass()
          .getSimpleName());
    }
    this.placements.addItemListener(this);
    placementTypePanel.add(this.placements);
    this.repeatBtn = new JCheckBox(I18N.getString("StyleEditionFrame.Repeat")); //$NON-NLS-1$
    this.repeatBtn.addItemListener(this);
    placementPanel.add(placementTypePanel);
    placementPanel.add(this.repeatBtn);
    return placementPanel;
  }

  public JPanel createFontPanel() {
    // TODO ajouter le soulignement et l'ombrage
    this.textPreviewLabel = new JLabel(
        I18N.getString("StyleEditionFrame.TextPreview")); //$NON-NLS-1$
    if (this.symbolizer != null) {
      this.textPreviewLabel.setFont(this.symbolizer.getFont().toAwfFont());
      this.textPreviewLabel.setForeground(this.symbolizer.getFill().getFill());
    } else {
      this.textPreviewLabel.setFont(new Font("Verdana", Font.BOLD, 12)); //$NON-NLS-1$
    }

    JPanel textPreviewPanel = new JPanel();
    textPreviewPanel.add(this.textPreviewLabel);
    textPreviewPanel.setAlignmentX(LEFT_ALIGNMENT);

    JPanel fontPanel = new JPanel();
    fontPanel.setLayout(new BoxLayout(fontPanel, BoxLayout.Y_AXIS));

    TitledBorder fillTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
    fillTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
    fillTitleBorder.setTitleColor(Color.blue);
    fillTitleBorder.setTitleFont(new Font("Verdana", Font.BOLD, 16)); //$NON-NLS-1$
    fillTitleBorder.setTitle("Font"); //$NON-NLS-1$
    fontPanel.setBorder(fillTitleBorder);
    fontPanel.setPreferredSize(new Dimension(400, 100));

    JLabel policeLabel = new JLabel(
        "   Police                                    " + //$NON-NLS-1$
            "                           Style                       Taille"); //$NON-NLS-1$
    policeLabel.setAlignmentX(LEFT_ALIGNMENT);

    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    String[] familyFonts = ge.getAvailableFontFamilyNames();
    JComboBox familyFontCombo = new JComboBox(familyFonts);
    familyFontCombo
        .setSelectedItem(this.textPreviewLabel.getFont().getFamily());
    familyFontCombo.setSize(familyFontCombo.getPreferredSize());
    familyFontCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Font newFont = new Font(((JComboBox) e.getSource()).getSelectedItem()
            .toString(), StyleEditionFrame.this.textPreviewLabel.getFont()
            .getStyle(), StyleEditionFrame.this.textPreviewLabel.getFont()
            .getSize());
        StyleEditionFrame.this.textPreviewLabel.setFont(newFont);
        if (StyleEditionFrame.this.symbolizer != null) {
          StyleEditionFrame.this.symbolizer
              .setFont(new fr.ign.cogit.geoxygene.style.Font(newFont));
        }
        StyleEditionFrame.this.update();
      }
    });

    String[] typeFont = { "regular", //$NON-NLS-1$
        "bold", //$NON-NLS-1$
        "italic", //$NON-NLS-1$
        "bold & italic" //$NON-NLS-1$
    };
    JComboBox styleFontCombo = new JComboBox(typeFont);
    styleFontCombo.setSelectedIndex(this.textPreviewLabel.getFont().getStyle());
    styleFontCombo.setPreferredSize(styleFontCombo.getPreferredSize());
    styleFontCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Font newFont = new Font(StyleEditionFrame.this.textPreviewLabel
            .getFont().getFamily(), ((JComboBox) e.getSource())
            .getSelectedIndex(), StyleEditionFrame.this.textPreviewLabel
            .getFont().getSize());
        StyleEditionFrame.this.textPreviewLabel.setFont(newFont);
        if (StyleEditionFrame.this.symbolizer != null) {
          StyleEditionFrame.this.symbolizer
              .setFont(new fr.ign.cogit.geoxygene.style.Font(newFont));
        }
        StyleEditionFrame.this.layerLegendPanel.getModel().fireActionPerformed(
            null);
        StyleEditionFrame.this.layerLegendPanel.repaint();
        StyleEditionFrame.this.layerViewPanel.repaint();
      }
    });

    String[] sizeFont = { "6", "7", "8", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "9", "10", "11", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "12", "14", "16", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "18", "20", "22", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "24", "26", "28", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "36", "48", "76" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    JComboBox sizeFontCombo = new JComboBox(sizeFont);
    sizeFontCombo.setSelectedItem(((Integer) this.textPreviewLabel.getFont()
        .getSize()).toString());
    sizeFontCombo.setPreferredSize(styleFontCombo.getPreferredSize());
    sizeFontCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Font newFont = new Font(StyleEditionFrame.this.textPreviewLabel
            .getFont().getFamily(), StyleEditionFrame.this.textPreviewLabel
            .getFont().getStyle(), Integer.parseInt(((JComboBox) e.getSource())
            .getSelectedItem().toString()));
        StyleEditionFrame.this.textPreviewLabel.setFont(newFont);
        if (StyleEditionFrame.this.symbolizer != null) {
          StyleEditionFrame.this.symbolizer
              .setFont(new fr.ign.cogit.geoxygene.style.Font(newFont));
        }
        StyleEditionFrame.this.layerLegendPanel.getModel().fireActionPerformed(
            null);
        StyleEditionFrame.this.layerLegendPanel.repaint();
        StyleEditionFrame.this.layerViewPanel.repaint();
      }
    });

    JButton colorButton = new JButton(I18N.getString("StyleEditionFrame.Color")); //$NON-NLS-1$
    colorButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Color c = COGITColorChooserPanel.showDialog(new JButton(),
            I18N.getString("StyleEditionFrame.PickAColor"), //$NON-NLS-1$
            StyleEditionFrame.this.textPreviewLabel.getForeground());
        StyleEditionFrame.this.textPreviewLabel.setForeground(c);
        logger.info(c);
        if (StyleEditionFrame.this.symbolizer != null) {
          logger.info(StyleEditionFrame.this.symbolizer.getFill().getFill());
          StyleEditionFrame.this.symbolizer
              .setFont(new fr.ign.cogit.geoxygene.style.Font(
                  StyleEditionFrame.this.textPreviewLabel.getFont()));
          StyleEditionFrame.this.symbolizer.getFill().setFill(c);
          logger.info(StyleEditionFrame.this.symbolizer.getFill().getFill());
        }

        StyleEditionFrame.this.layerLegendPanel.getModel().fireActionPerformed(
            null);
        StyleEditionFrame.this.layerLegendPanel.repaint();
        StyleEditionFrame.this.layerViewPanel.repaint();
        StyleEditionFrame.this.layerViewPanel.updateUI();
        StyleEditionFrame.this.layerViewPanel.superRepaint();
        StyleEditionFrame.this.layerViewPanel.getProjectFrame().repaint();
      }
    });

    JPanel comboPanel = new JPanel();
    comboPanel.setLayout(new FlowLayout());
    ((FlowLayout) comboPanel.getLayout()).setAlignment(FlowLayout.LEFT);
    comboPanel.add(familyFontCombo);
    comboPanel.add(styleFontCombo);
    comboPanel.add(sizeFontCombo);
    comboPanel.add(colorButton);
    comboPanel.setAlignmentX(LEFT_ALIGNMENT);

    fontPanel.add(policeLabel);
    fontPanel.add(comboPanel);
    fontPanel.add(textPreviewPanel);

    return fontPanel;
  }

  public void initPolygon() {
    this.graphicStylePanel.setLayout(new BorderLayout());
    this.visuPanel = this.createVisuPanel();
    this.graphicStylePanel.add(this.visuPanel, BorderLayout.WEST);

    // ****************
    JLabel titleLabel = new JLabel(
        I18N.getString("StyleEditionFrame.PolygonNewStyle"));
    ;

    titleLabel.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 24)); //$NON-NLS-1$

    // ************************
    // creation du trait
    int n = this.layer.getStyles().size();
    // numero du trait
    int nT;
    this.ListPolygone = new ArrayList<PolygonePanel>(this.MAX_STROKE);
    for (int i = 0; i < n; i++) {

      // ajouter le trait a la list "liststroke"
      this.ListPolygone
          .add(new PolygonePanel(this, (PolygonSymbolizer) this.layer
              .getStyles().get(i).getSymbolizer(), i));

    }

    /*
     * n= nbrs de styles ajouter des StrokePanel dans la list pour pouvoir
     * mettre les informations des nouveaux style ajouter
     */
    for (int i = n; i < this.MAX_STROKE; i++) {
      this.ListPolygone.add(new PolygonePanel(new JPanel()));
    }

    this.panelTrait = new JPanel();
    this.panelTrait.setLayout(new BoxLayout(this.panelTrait, BoxLayout.Y_AXIS));

    this.mainStylePanel = new JPanel();
    this.mainStylePanel.setLayout(null);

    titleLabel.setAlignmentX(LEFT_ALIGNMENT);
    titleLabel.setBounds(2, 10, 330, 50);

    this.ListPolygone.get(0).getLinePanel().getStrokePanel()
        .setAlignmentX(LEFT_ALIGNMENT);
    this.panelTrait.add(this.ListPolygone.get(0).getLinePanel()
        .getStrokePanel());
    // }

    this.panelTrait.setBounds(15, 65, 430, 315);
    // ************************+

    this.btnAddStyle = new JButton("Ajouter un UserStyle"); //$NON-NLS-1$
    this.btnAddStyle.addActionListener(this);
    // ajouter listener pour pouvoir ajouter un elmt a la list
    this.btnAddStyle.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        StyleEditionFrame.this.addElmMyList();

      }
    });
    // ajouter jlist

    this.maList = new MyListStroke(this.layer.getStyles().size());
    this.maList.getList().addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {

        StyleEditionFrame.this.listvalueChanged();
      }
    });
    this.maList.getList().addKeyListener(new java.awt.event.KeyAdapter() {
      @Override
      public void keyReleased(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_UP
            || (evt.getKeyCode() == KeyEvent.VK_DOWN))
          StyleEditionFrame.this.listvalueChanged();
      }
    });

    // selectioné le style dans Jlist
    if (this.layer.getStyles().size() != 0)
      this.maList.getList().setSelectedIndex(0);

    // ajouter les bouton monter et descendre avec leur listener
    JButton Fill = new JButton("Fill");
    JButton Stroke = new JButton("Stroke");
    Fill.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        StyleEditionFrame.this.showFill();

      }
    });
    Stroke.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        StyleEditionFrame.this.showStroke();

      }
    });
    // ajouter les bouton monter et descendre avec leur listener
    JButton monterTrait = new JButton("Monter le UserStyle");
    JButton descendreTrait = new JButton("Descendre le UserStyle");

    monterTrait.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {

        StyleEditionFrame.this.permuterFill("monter");
      }
    });

    // mainButton.add(this.btnAddStyle);
    descendreTrait.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {

        StyleEditionFrame.this.permuterFill("descendre");
      }
    });

    JButton suprimer = new JButton("Suprimer le UserStyle");

    suprimer.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {

        StyleEditionFrame.this.removeUserStyle();
      }
    });

    // ajouter les composants dans le Jpanel mainStulePanel
    this.mainStylePanel.add(titleLabel);
    this.mainStylePanel.add(this.panelTrait);
    // *************
    TitledBorder strokeTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
    strokeTitleBorder.setTitleColor(Color.blue);
    JPanel k = new JPanel();
    k.setLayout(null);
    k.setBorder(strokeTitleBorder);

    Fill.setBounds(90, 5, 100, 30);
    Stroke.setBounds(220, 5, 100, 30);
    // symbol.setBounds(270, 5, 100, 30);

    k.setBounds(15, 385, 430, 40);

    k.add(Fill);
    k.add(Stroke);
    // k.add(symbol);
    this.mainStylePanel.add(k);
    // ***************************

    JPanel j = new JPanel();
    j.setLayout(null);
    j.setBorder(strokeTitleBorder);

    // positionnee le jpanel
    ((JPanel) this.maList).setBounds(30, 15, 100, 140);

    monterTrait.setBounds(200, 15, 170, 30);
    this.btnAddStyle.setBounds(200, 55, 170, 30);
    suprimer.setBounds(200, 90, 170, 30);
    descendreTrait.setBounds(200, 125, 170, 30);
    j.setBounds(15, 440, 430, 170);
    // ajouter le jpanel
    j.add(this.maList);
    j.add(descendreTrait);
    j.add(this.btnAddStyle);
    j.add(suprimer);
    j.add(monterTrait);

    this.mainStylePanel.add(j);

    // *************************
    // fin ajout

    this.graphicStylePanel.add(this.mainStylePanel, BorderLayout.CENTER);

    this.pack();

  }

  protected void showStroke() {
    // TODO Auto-generated method stub
    int i = 1 + this.maList.getList().getSelectedIndex();
    // System.out.println("trait : " + i);
    this.panelTrait.removeAll();

    if (this.layer.getSymbolizer().isPolygonSymbolizer()) {
      this.ListPolygone.get(this.maList.getList().getSelectedIndex())
          .getLinePanel().getStrokePanel().setAlignmentX(LEFT_ALIGNMENT);
      this.panelTrait.add(this.ListPolygone
          .get(this.maList.getList().getSelectedIndex()).getLinePanel()
          .getStrokePanel());
      this.ListPolygone.get(this.maList.getList().getSelectedIndex())
          .getLinePanel().updateExpressivePanel();

    }
    if (this.layer.getSymbolizer().isPointSymbolizer()) {
      this.ListPoint.get(this.maList.getList().getSelectedIndex())
          .getLinePanel().getStrokePanel().setAlignmentX(LEFT_ALIGNMENT);
      this.panelTrait.add(this.ListPoint
          .get(this.maList.getList().getSelectedIndex()).getLinePanel()
          .getStrokePanel());

    }
    if (this.layer.getSymbolizer().isLineSymbolizer()) {
      this.ListLine.get(this.maList.getList().getSelectedIndex())
          .getStrokePanel().setAlignmentX(LEFT_ALIGNMENT);
      this.panelTrait.add(this.ListLine.get(
          this.maList.getList().getSelectedIndex()).getStrokePanel());
      this.ListLine.get(this.maList.getList().getSelectedIndex())
          .updateExpressivePanel();

    }
    this.panelTrait.validate();
    this.panelTrait.repaint();
  }

  protected void showSymbol() {
    // TODO Auto-generated method stub
    // TODO Auto-generated method stub
    int i = 1 + this.maList.getList().getSelectedIndex();
    // System.out.println("trait : " + i);
    this.panelTrait.removeAll();

    if (this.layer.getSymbolizer().isPointSymbolizer()) {
      this.ListPoint.get(this.maList.getList().getSelectedIndex())
          .getSymbolPanel().setAlignmentX(LEFT_ALIGNMENT);
      this.panelTrait.add(this.ListPoint.get(
          this.maList.getList().getSelectedIndex()).getSymbolPanel());
      // this.ListFill.get(this.maList.getList().getSelectedIndex())
      // .getLinePanel().updateExpressivePanel();

    }
    this.panelTrait.validate();
    this.panelTrait.repaint();

  }

  protected void showFill() {
    // TODO Auto-generated method stub
    int i = 1 + this.maList.getList().getSelectedIndex();
    // System.out.println("Fill : " + i);
    this.panelTrait.removeAll();

    if (this.layer.getSymbolizer().isPolygonSymbolizer()) {
      this.ListPolygone.get(this.maList.getList().getSelectedIndex())
          .getFillPanel().setAlignmentX(LEFT_ALIGNMENT);
      this.panelTrait.add(this.ListPolygone.get(
          this.maList.getList().getSelectedIndex()).getFillPanel());
      this.ListPolygone.get(this.maList.getList().getSelectedIndex())
          .updateExpressivePanel();
    }
    if (this.layer.getSymbolizer().isPointSymbolizer()) {
      this.ListPoint.get(this.maList.getList().getSelectedIndex())
          .getPolygonePanel().getFillPanel().setAlignmentX(LEFT_ALIGNMENT);
      this.panelTrait.add(this.ListPoint
          .get(this.maList.getList().getSelectedIndex()).getPolygonePanel()
          .getFillPanel());
      // this.ListPoint.get(this.maList.getList().getSelectedIndex()).updateExpressivePanel();
    }

    this.panelTrait.validate();
    this.panelTrait.repaint();

  }

  private void updateInterpolationPanel() {
    this.addOrReplaceStyleInterpolationUI();
  }

  // modif abdel
  public void initLine() {
    this.graphicStylePanel.setLayout(new BorderLayout());
    this.visuPanel = this.createVisuPanel();
    this.graphicStylePanel.add(this.visuPanel, BorderLayout.WEST);
    this.ListLine = new ArrayList<LinePanel>(this.MAX_STROKE);
    JLabel titleLabel = new JLabel(
        I18N.getString("StyleEditionFrame.LineNewStyle")); //$NON-NLS-1$
    titleLabel.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 24)); //$NON-NLS-1$
    // creation du trait
    int n = this.layer.getStyles().size();
    // numero du trait
    int nT;

    for (int i = 0; i < n; i++) {

      // ajouter le trait a la list "liststroke"
      this.ListLine.add(new LinePanel(this, (LineSymbolizer) this.layer
          .getStyles().get(i).getSymbolizer(), i));

    }

    /*
     * n= nbrs de styles ajouter des StrokePanel dans la list pour pouvoir
     * mettre les informations des nouveaux style ajouter
     */
    for (int i = n; i < this.MAX_STROKE; i++) {
      this.ListLine.add(new LinePanel(new JPanel()));
    }

    this.mainStylePanel = new JPanel();
    this.mainStylePanel.setLayout(null);
    titleLabel.setAlignmentX(LEFT_ALIGNMENT);
    titleLabel.setBounds(2, 10, 330, 50);

    this.btnAddStyle = new JButton("Ajouter un UserStyle"); //$NON-NLS-1$
    this.btnAddStyle.addActionListener(this);
    // ajouter listener pour pouvoir ajouter un elmt a la list
    this.btnAddStyle.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        StyleEditionFrame.this.addElmMyList();

      }
    });

    this.panelTrait = new JPanel();
    this.panelTrait.setLayout(new BoxLayout(this.panelTrait, BoxLayout.Y_AXIS));

    this.panelTrait.setBounds(15, 65, 430, 315);

    // ajouter jlist

    this.maList = new MyListStroke(this.layer.getStyles().size());
    this.maList.getList().addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {

        StyleEditionFrame.this.listvalueChanged();
      }
    });
    this.maList.getList().addKeyListener(new java.awt.event.KeyAdapter() {
      @Override
      public void keyReleased(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_UP
            || (evt.getKeyCode() == KeyEvent.VK_DOWN))
          StyleEditionFrame.this.listvalueChanged();
      }
    });

    // selectioné le style dans Jlist
    if (this.layer.getStyles().size() != 0)
      this.maList.getList().setSelectedIndex(0);

    // ajouter les bouton monter et descendre avec leur listener
    JButton monterTrait = new JButton("Monter le UserStyle");
    JButton descendreTrait = new JButton("Descendre le UserStyle");

    monterTrait.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {

        StyleEditionFrame.this.permuterLine("monter");
      }
    });

    descendreTrait.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {

        StyleEditionFrame.this.permuterLine("descendre");
      }
    });

    JButton suprimer = new JButton("Suprimer le UserStyle");

    suprimer.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {

        StyleEditionFrame.this.removeUserStyle();
      }
    });
    // ajouter les composants dans le Jpanel mainStulePanel
    this.mainStylePanel.add(titleLabel);
    this.mainStylePanel.add(this.panelTrait);
    // ********
    TitledBorder strokeTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
    strokeTitleBorder.setTitleColor(Color.blue);
    JPanel j = new JPanel();
    j.setLayout(null);
    j.setBorder(strokeTitleBorder);

    // positionnee le jpanel
    ((JPanel) this.maList).setBounds(30, 30, 100, 140);

    monterTrait.setBounds(200, 30, 170, 30);
    this.btnAddStyle.setBounds(200, 70, 170, 30);
    suprimer.setBounds(200, 105, 170, 30);
    descendreTrait.setBounds(200, 140, 170, 30);
    j.setBounds(15, 400, 430, 200);
    // ajouter le jpanel
    j.add(this.maList);
    j.add(descendreTrait);
    j.add(this.btnAddStyle);
    j.add(suprimer);
    j.add(monterTrait);

    this.mainStylePanel.add(j);

    // *****

    this.graphicStylePanel.add(this.mainStylePanel, BorderLayout.CENTER);//
    this.pack();
    this.repaint();

  }

  public LayerLegendPanel getLayerLegendPanel() {
    return this.layerLegendPanel;
  }

  public LayerViewPanel getLayerViewPanel() {
    return this.layerViewPanel;
  }

  public static LayerStylesPanel getStylePanel() {
    return stylePanel;
  }

  public static void setStylePanel(LayerStylesPanel stylePanel) {
    StyleEditionFrame.stylePanel = stylePanel;
  }

  public JLabel getStrokeFinalColorLabel() {
    return this.strokeFinalColorLabel;
  }

  public void setStrokeFinalColorLabel(JLabel strokeFinalColorLabel) {
    this.strokeFinalColorLabel = strokeFinalColorLabel;
  }

  protected void permuterPoint(String string) {
    // TODO Auto-generated method stub

    List<Style> list = this.layer.getStyles();
    if (!this.maList.getList().isSelectionEmpty()) {
      int TraitSelect = this.maList.getList().getSelectedIndex();
      if (string.equals("monter")) {
        if (this.maList.getList().getSelectedIndex() != 0) {
          list.add(TraitSelect - 1, this.layer.getStyles().get(TraitSelect));
          list.remove(TraitSelect + 1);

          this.ListPoint.remove(TraitSelect);
          this.ListPoint.remove(TraitSelect - 1);

          this.ListPoint.add(TraitSelect - 1, (new PointPanel(this,
              (PointSymbolizer) this.layer.getStyles().get(TraitSelect - 1)
                  .getSymbolizer(), TraitSelect - 1)));
          this.ListPoint.add(TraitSelect, (new PointPanel(this,
              (PointSymbolizer) this.layer.getStyles().get(TraitSelect)
                  .getSymbolizer(), TraitSelect)));

          this.maList.getList().setSelectedIndex(TraitSelect - 1);

          this.panelTrait.removeAll();
          this.panelTrait.add(this.ListPoint.get(TraitSelect - 1)
              .getLinePanel().getStrokePanel());
          this.ListPoint.get(TraitSelect - 1).updatePointPanel();
          this.ListPoint.get(TraitSelect).updatePointPanel();

          this.panelTrait.validate();
          this.panelTrait.repaint();

        }

      } else {
        if (this.maList.getList().getSelectedIndex() != ((this.maList
            .getCounter()) - 1)) {
          list.add(TraitSelect + 2, this.layer.getStyles().get(TraitSelect));
          list.remove(TraitSelect);

          this.ListPoint.remove(TraitSelect);
          this.ListPoint.remove(TraitSelect + 1);

          this.ListPoint.add(TraitSelect, (new PointPanel(this,
              (PointSymbolizer) this.layer.getStyles().get(TraitSelect)
                  .getSymbolizer(), TraitSelect)));
          this.ListPoint.add(TraitSelect + 1, (new PointPanel(this,
              (PointSymbolizer) this.layer.getStyles().get(TraitSelect + 1)
                  .getSymbolizer(), TraitSelect + 1)));

          this.maList.getList().setSelectedIndex(TraitSelect + 1);

          this.panelTrait.removeAll();
          this.panelTrait.add(this.ListPoint.get(TraitSelect + 1)
              .getLinePanel().getStrokePanel());
          this.ListPoint.get(TraitSelect + 1).updatePointPanel();
          this.ListPoint.get(TraitSelect).updatePointPanel();

          this.panelTrait.validate();
          this.panelTrait.repaint();

        }

      }

    }
  }

  protected void permuterLine(String string) {

    List<Style> list = this.layer.getStyles();
    if (!this.maList.getList().isSelectionEmpty()) {
      int TraitSelect = this.maList.getList().getSelectedIndex();
      if (string.equals("monter")) {
        if (this.maList.getList().getSelectedIndex() != 0) {
          list.add(TraitSelect - 1, this.layer.getStyles().get(TraitSelect));
          list.remove(TraitSelect + 1);

          this.ListLine.remove(TraitSelect);
          this.ListLine.remove(TraitSelect - 1);

          this.ListLine.add(TraitSelect - 1, (new LinePanel(this,
              (LineSymbolizer) this.layer.getStyles().get(TraitSelect - 1)
                  .getSymbolizer(), TraitSelect - 1)));
          this.ListLine.add(TraitSelect, (new LinePanel(this,
              (LineSymbolizer) this.layer.getStyles().get(TraitSelect)
                  .getSymbolizer(), TraitSelect)));
          this.maList.getList().setSelectedIndex(TraitSelect - 1);

          this.panelTrait.removeAll();
          this.panelTrait.add(this.ListLine.get(TraitSelect - 1)
              .getStrokePanel());
          this.ListLine.get(TraitSelect - 1).updateLinePanel();
          this.ListLine.get(TraitSelect).updateLinePanel();
          this.panelTrait.validate();
          this.panelTrait.repaint();

        }

      } else {
        if (this.maList.getList().getSelectedIndex() != ((this.maList
            .getCounter()) - 1)) {
          list.add(TraitSelect + 2, this.layer.getStyles().get(TraitSelect));
          list.remove(TraitSelect);

          this.ListLine.remove(TraitSelect);
          this.ListLine.remove(TraitSelect + 1);

          this.ListLine.add(TraitSelect, (new LinePanel(this,
              (LineSymbolizer) this.layer.getStyles().get(TraitSelect)
                  .getSymbolizer(), TraitSelect)));
          this.ListLine.add(TraitSelect + 1, (new LinePanel(this,
              (LineSymbolizer) this.layer.getStyles().get(TraitSelect + 1)
                  .getSymbolizer(), TraitSelect + 1)));
          this.maList.getList().setSelectedIndex(TraitSelect + 1);

          this.panelTrait.removeAll();
          this.panelTrait.add(this.ListLine.get(TraitSelect + 1)
              .getStrokePanel());
          this.ListLine.get(TraitSelect + 1).updateLinePanel();
          this.ListLine.get(TraitSelect).updateLinePanel();
          this.panelTrait.validate();
          this.panelTrait.repaint();

        }

      }

    }

  }

  /*
   * La fonction de permutation de deux styles l'utilisateur peut selectionner
   * un trait de la liste on cliquant sur les bouttons indiquant le haut et le
   * bas le clic sur l'un de ces bouttons fera appel a la fonction et elle
   * permutra les données de 2 traits
   */
  protected void permuterFill(String string) {

    List<Style> list = this.layer.getStyles();
    if (!this.maList.getList().isSelectionEmpty()) {
      int TraitSelect = this.maList.getList().getSelectedIndex();
      if (string.equals("monter")) {
        if (this.maList.getList().getSelectedIndex() != 0) {
          list.add(TraitSelect - 1, this.layer.getStyles().get(TraitSelect));
          list.remove(TraitSelect + 1);

          this.ListPolygone.remove(TraitSelect);
          this.ListPolygone.remove(TraitSelect - 1);

          this.ListPolygone.add(TraitSelect - 1, (new PolygonePanel(this,
              (PolygonSymbolizer) this.layer.getStyles().get(TraitSelect - 1)
                  .getSymbolizer(), TraitSelect - 1)));
          this.ListPolygone.add(TraitSelect, (new PolygonePanel(this,
              (PolygonSymbolizer) this.layer.getStyles().get(TraitSelect)
                  .getSymbolizer(), TraitSelect)));

          this.maList.getList().setSelectedIndex(TraitSelect - 1);

          this.panelTrait.removeAll();
          this.panelTrait.add(this.ListPolygone.get(TraitSelect - 1)
              .getLinePanel().getStrokePanel());
          this.ListPolygone.get(this.maList.getList().getSelectedIndex())
              .getLinePanel().updateExpressivePanel();

          this.ListPolygone.get(TraitSelect - 1).updatePolygonePanel();
          ;
          this.ListPolygone.get(TraitSelect).updatePolygonePanel();

          this.panelTrait.validate();
          this.panelTrait.repaint();

        }

      } else {
        if (this.maList.getList().getSelectedIndex() != ((this.maList
            .getCounter()) - 1)) {
          list.add(TraitSelect + 2, this.layer.getStyles().get(TraitSelect));
          list.remove(TraitSelect);

          this.ListPolygone.remove(TraitSelect);
          this.ListPolygone.remove(TraitSelect + 1);

          this.ListPolygone.add(TraitSelect, (new PolygonePanel(this,
              (PolygonSymbolizer) this.layer.getStyles().get(TraitSelect)
                  .getSymbolizer(), TraitSelect)));
          this.ListPolygone.add(TraitSelect + 1, (new PolygonePanel(this,
              (PolygonSymbolizer) this.layer.getStyles().get(TraitSelect + 1)
                  .getSymbolizer(), TraitSelect + 1)));

          this.maList.getList().setSelectedIndex(TraitSelect + 1);

          this.panelTrait.removeAll();
          this.panelTrait.add(this.ListPolygone.get(TraitSelect + 1)
              .getLinePanel().getStrokePanel());
          this.ListPolygone.get(this.maList.getList().getSelectedIndex())
              .getLinePanel().updateExpressivePanel();
          this.ListPolygone.get(TraitSelect + 1).updatePolygonePanel();
          this.ListPolygone.get(TraitSelect).updatePolygonePanel();

          this.panelTrait.validate();
          this.panelTrait.repaint();

        }

      }

    }

  }

  /*
   * fonction listvalueChanged est appeler quand l'utilisateur parcours la list
   * avec les boutton du clavier
   */
  private void listvalueChanged() {
    // TODO add your handling code here:

    int i = 1 + this.maList.getList().getSelectedIndex();
    // System.out.println("trait : " + i);
    this.panelTrait.removeAll();

    if (this.layer.getSymbolizer().isPolygonSymbolizer()) {
      this.ListPolygone.get(this.maList.getList().getSelectedIndex())
          .getLinePanel().getStrokePanel().setAlignmentX(LEFT_ALIGNMENT);
      this.panelTrait.add(this.ListPolygone
          .get(this.maList.getList().getSelectedIndex()).getLinePanel()
          .getStrokePanel());
      this.ListPolygone.get(this.maList.getList().getSelectedIndex())
          .getLinePanel().updateExpressivePanel();

    } else if (this.layer.getSymbolizer().isLineSymbolizer()) {
      this.ListLine.get(this.maList.getList().getSelectedIndex())
          .getStrokePanel().setAlignmentX(LEFT_ALIGNMENT);
      this.panelTrait.add(this.ListLine.get(
          this.maList.getList().getSelectedIndex()).getStrokePanel());
      this.ListLine.get(this.maList.getList().getSelectedIndex())
          .updateExpressivePanel();

    } else if (this.layer.getSymbolizer().isPointSymbolizer()) {
      this.ListPoint.get(this.maList.getList().getSelectedIndex())
          .getLinePanel().getStrokePanel().setAlignmentX(LEFT_ALIGNMENT);
      this.panelTrait.add(this.ListPoint
          .get(this.maList.getList().getSelectedIndex()).getLinePanel()
          .getStrokePanel());
      // this.ListPoint.get( this.maList.getList().getSelectedIndex()
      // ).updateExpressivePanel();

    }

    this.panelTrait.validate();
    this.panelTrait.repaint();

  }

  /*
   * quand l'utilisateur ajoute un Style cette fct et appeler et permet
   * d'ajouter un elmt a la list
   */
  protected void addElmMyList() {
    // TODO Auto-generated method stub+
    int i = 1 + this.layer.getStyles().size();
    this.maList.getModel().addElement("UserStyle " + i);
    MyListStroke.setCounter(MyListStroke.getCounter() + 1);

  }

  // fin modif abdel
  public void initPoint() {
    this.graphicStylePanel.setLayout(new BorderLayout());
    this.visuPanel = this.createVisuPanel();
    this.graphicStylePanel.add(this.visuPanel, BorderLayout.WEST);
    // **************************************
    JLabel titleLabel = new JLabel(
        I18N.getString("StyleEditionFrame.PointNewStyle")); //$NON-NLS-1$
    titleLabel.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 24)); //$NON-NLS-1$
    // **************************************
    // ************************
    // creation du trait
    int n = this.layer.getStyles().size();
    // numero du trait
    int nT;
    this.ListPoint = new ArrayList<PointPanel>(this.MAX_STROKE);
    for (int i = 0; i < n; i++) {

      // ajouter le trait a la list "liststroke"
      this.ListPoint.add(new PointPanel(this, (PointSymbolizer) this.layer
          .getStyles().get(i).getSymbolizer(), i));

    }

    /*
     * n= nbrs de styles ajouter des StrokePanel dans la list pour pouvoir
     * mettre les informations des nouveaux style ajouter
     */
    for (int i = n; i < this.MAX_STROKE; i++) {
      this.ListPoint.add(new PointPanel(new JPanel()));
    }

    this.panelTrait = new JPanel();
    this.panelTrait.setLayout(new BoxLayout(this.panelTrait, BoxLayout.Y_AXIS));

    this.mainStylePanel = new JPanel();
    this.mainStylePanel.setLayout(null);

    titleLabel.setAlignmentX(LEFT_ALIGNMENT);
    titleLabel.setBounds(2, 10, 330, 50);

    this.ListPoint.get(0).getLinePanel().getStrokePanel()
    .setAlignmentX(LEFT_ALIGNMENT);
    this.panelTrait.add(this.ListPoint.get(0).getLinePanel().getStrokePanel());

    this.panelTrait.setBounds(15, 65, 430, 310);
    // ************************+

    this.btnAddStyle = new JButton("Ajouter un UserStyle"); //$NON-NLS-1$
    this.btnAddStyle.addActionListener(this);
    // ajouter listener pour pouvoir ajouter un elmt a la list
    this.btnAddStyle.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        StyleEditionFrame.this.addElmMyList();

      }
    });
    // ajouter jlist

    this.maList = new MyListStroke(this.layer.getStyles().size());
    this.maList.getList().addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {

        StyleEditionFrame.this.listvalueChanged();
      }
    });
    this.maList.getList().addKeyListener(new java.awt.event.KeyAdapter() {
      @Override
      public void keyReleased(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_UP
            || (evt.getKeyCode() == KeyEvent.VK_DOWN))
          StyleEditionFrame.this.listvalueChanged();
      }
    });

    // selectioné le style dans Jlist
    if (this.layer.getStyles().size() != 0)
      this.maList.getList().setSelectedIndex(0);

    // ajouter les bouton monter et descendre avec leur listener
    JButton Fill = new JButton("Fill");
    JButton Stroke = new JButton("Stroke");
    JButton symbol = new JButton("symbol");

    Fill.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        StyleEditionFrame.this.showFill();

      }
    });

    Stroke.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        StyleEditionFrame.this.showStroke();

      }
    });

    symbol.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        StyleEditionFrame.this.showSymbol();

      }
    });
    // ajouter les bouton monter et descendre avec leur listener
    JButton monterTrait = new JButton("Monter le UserStyle");
    JButton descendreTrait = new JButton("Descendre le UserStyle");

    monterTrait.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {

        StyleEditionFrame.this.permuterPoint("monter");
      }
    });

    descendreTrait.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {

        StyleEditionFrame.this.permuterPoint("descendre");
      }
    });

    JButton suprimer = new JButton("Suprimer le UserStyle");

    suprimer.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {

        StyleEditionFrame.this.removeUserStyle();
      }
    });

    // **************************************
    // ajouter les composants dans le Jpanel mainStulePanel
    this.mainStylePanel.add(titleLabel);
    this.mainStylePanel.add(this.panelTrait);
    // ****************************
    TitledBorder strokeTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
    strokeTitleBorder.setTitleColor(Color.blue);
    JPanel k = new JPanel();
    k.setLayout(null);
    k.setBorder(strokeTitleBorder);

    Fill.setBounds(50, 5, 100, 30);
    Stroke.setBounds(160, 5, 100, 30);
    symbol.setBounds(270, 5, 100, 30);

    k.setBounds(15, 385, 430, 40);

    k.add(Fill);
    k.add(Stroke);
    k.add(symbol);
    this.mainStylePanel.add(k);
    // ***************************

    JPanel j = new JPanel();
    j.setLayout(null);
    j.setBorder(strokeTitleBorder);

    // positionnee le jpanel
    ((JPanel) this.maList).setBounds(30, 15, 100, 140);

    monterTrait.setBounds(200, 15, 170, 30);
    this.btnAddStyle.setBounds(200, 55, 170, 30);
    suprimer.setBounds(200, 90, 170, 30);
    descendreTrait.setBounds(200, 125, 170, 30);
    j.setBounds(15, 440, 430, 170);
    // ajouter le jpanel
    j.add(this.maList);
    j.add(descendreTrait);
    j.add(this.btnAddStyle);
    j.add(suprimer);
    j.add(monterTrait);

    this.mainStylePanel.add(j);

    // *************************
    // fin ajout

    this.graphicStylePanel.add(this.mainStylePanel, BorderLayout.CENTER);

    this.pack();

  }

  protected void removeUserStyle() {
    // TODO Auto-generated method stub
    if (!this.maList.getList().isSelectionEmpty()
        && this.layer.getStyles().size() > 1) {
      int TraitSelect = this.maList.getList().getSelectedIndex();
      List<Style> list = this.layer.getStyles();
      int key = 0;
      if (this.layer.getSymbolizer().isPolygonSymbolizer()) {
        this.ListPolygone.remove(TraitSelect);
        this.ListPolygone.add(new PolygonePanel(new JPanel()));
        list.remove(TraitSelect);
        this.maList.updateList(this.layer.getStyles().size());
        this.maList.getList().setSelectedIndex(0);
        for (ListIterator<PolygonePanel> it = this.ListPolygone.listIterator(); it
            .hasNext();) {

          PolygonePanel fillPanel = it.next();
          fillPanel.setKey(key);
          key++;
          if (key == this.layer.getStyles().size())
            break;
        }
        this.ListPolygone.get(0).updatePolygonePanel();
        this.showStroke();

      } else if (this.layer.getSymbolizer().isPointSymbolizer()) {
        this.ListPoint.remove(TraitSelect);
        this.ListPoint.add(new PointPanel(new JPanel()));
        list.remove(TraitSelect);
        this.maList.updateList(this.layer.getStyles().size());
        this.maList.getList().setSelectedIndex(0);
        for (ListIterator<PointPanel> it = this.ListPoint.listIterator(); it
            .hasNext();) {

          PointPanel pointPanel = it.next();
          pointPanel.setKey(key);
          key++;
          if (key == this.layer.getStyles().size())
            break;
        }
        this.ListPoint.get(0).updatePointPanel();
        this.showStroke();

      } else if (this.layer.getSymbolizer().isLineSymbolizer()) {
        this.ListLine.remove(TraitSelect);
        this.ListLine.add(new LinePanel(new JPanel()));
        list.remove(TraitSelect);
        this.maList.updateList(this.layer.getStyles().size());
        this.maList.getList().setSelectedIndex(0);
        for (ListIterator<LinePanel> it = this.ListLine.listIterator(); it
            .hasNext();) {

          LinePanel linePanel = it.next();
          linePanel.setKey(key);
          key++;
          if (key == this.layer.getStyles().size())
            break;
        }
        this.ListLine.get(0).updateLinePanel();
        this.showStroke();

      }

    }
  }

  /**
   * This method creates and return the panel showing the style.
   * 
   * @return the panel showing the style.
   */
  public JPanel createVisuPanel() {
    JPanel visuPanel = new JPanel();
    visuPanel.setPreferredSize(new Dimension(150, 600));
    visuPanel.setBackground(Color.white);

    StyleEditionFrame.stylePanel = new LayerStylesPanel(this.layer);
    StyleEditionFrame.stylePanel.setMaximumSize(new Dimension(60, 80));
    StyleEditionFrame.stylePanel.setMinimumSize(new Dimension(60, 80));
    StyleEditionFrame.stylePanel.setPreferredSize(new Dimension(60, 80));
    visuPanel.add(StyleEditionFrame.stylePanel);

    JLabel explanation = new JLabel(""); //$NON-NLS-1$
    visuPanel.add(explanation);

    return visuPanel;
  }

  public JButton createButtonValid() {
    this.btnValid = new JButton(I18N.getString("StyleEditionFrame.Ok")); //$NON-NLS-1$
    this.btnValid.addActionListener(this);
    this.btnValid.setBounds(50, 50, 100, 20);
    return this.btnValid;
  }

  public JButton createButtonApply() {
    this.btnApply = new JButton(I18N.getString("StyleEditionFrame.Apply")); //$NON-NLS-1$
    this.btnApply.addActionListener(this);
    this.btnApply.setBounds(50, 50, 100, 20);
    return this.btnApply;
  }

  public JButton createButtonCancel() {
    this.btnCancel = new JButton(I18N.getString("StyleEditionFrame.Cancel")); //$NON-NLS-1$
    this.btnCancel.addActionListener(this);
    this.btnCancel.setBounds(50, 50, 100, 20);
    return this.btnCancel;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // System.out.println("actionPerformed");
    this.getDialogElements();

    // When the user add a style (LineSymbolizer case).
    if (e.getSource() == this.btnAddStyle) {

      int indice = this.layer.getStyles().size();

      if (this.layer.getSymbolizer().isLineSymbolizer()) {

        this.ListLine.get(indice).addStrokePanel(this, indice);
        this.panelTrait.removeAll();
        this.panelTrait.add(this.ListLine.get(indice).getStrokePanel());
        this.ListLine.get(indice).getStrokePanel().setVisible(true);
        this.maList.getList().setSelectedIndex(
            this.layer.getStyles().size() - 1);
        this.ListLine.get(indice).updateLinePanel();

      } else if (this.layer.getSymbolizer().isPolygonSymbolizer()) {
        this.ListPolygone.get(indice).addFillPanel(this, indice);
        this.panelTrait.removeAll();
        this.panelTrait.add(this.ListPolygone.get(indice).getLinePanel()
            .getStrokePanel());
        this.ListPolygone.get(indice).getLinePanel().getStrokePanel()
            .setVisible(true);
        this.maList.getList().setSelectedIndex(
            this.layer.getStyles().size() - 1);
        this.ListPolygone.get(indice).updatePolygonePanel();

      } else if (this.layer.getSymbolizer().isPointSymbolizer()) {
        this.ListPoint.get(indice).addPointPanel(this, indice);
        this.panelTrait.removeAll();
        this.panelTrait.add(this.ListPoint.get(indice).getLinePanel()
            .getStrokePanel());
        this.maList.getList().setSelectedIndex(
            this.layer.getStyles().size() - 1);
        this.ListPoint.get(indice).updatePointPanel();

      }

      int nbrTrai = this.layer.getStyles().size() + 1;
      this.panelTrait.validate();
      // this.updateLayer();
      if (nbrTrai == this.MAX_STROKE)
        this.btnAddStyle.setEnabled(false);
    }

    // When the user apply style modifications to the map and the legend
    if (e.getSource() == this.btnApply) {
      this.layerLegendPanel.getModel().fireActionPerformed(null);
      this.layerLegendPanel.repaint();
      this.layerViewPanel.repaint();

      this.updateLayer();
    }

    // When the user cancel style modifications in the main interface
    if (e.getSource() == this.btnCancel) {
      this.reset();
      StyleEditionFrame.this.dispose();
    }

    // When the user validate style modifications
    if (e.getSource() == this.btnValid) {
      this.layerLegendPanel.getModel().fireActionPerformed(null);
      this.layerLegendPanel.repaint();
      this.layerViewPanel.repaint();
      this.updateLayer();
      StyleEditionFrame.this.dispose();
    }
  }

  @Override
  public void mouseClicked(MouseEvent arg0) {
    this.getDialogElements();

  }

  @Override
  public void mouseEntered(MouseEvent arg0) {
  }

  @Override
  public void mouseExited(MouseEvent arg0) {
  }

  @Override
  public void mousePressed(MouseEvent arg0) {
  }

  @Override
  public void mouseReleased(MouseEvent arg0) {
  }

  @Override
  public void stateChanged(ChangeEvent arg0) {
    this.getDialogElements();

  }

  public void updateLayer() {
    Symbolizer symbolizer = this.layer.getStyles().get(0).getSymbolizer();
    int k = 0;
    if (symbolizer.isPolygonSymbolizer()) {
      for (ListIterator<PolygonePanel> it = this.ListPolygone.listIterator(); it
          .hasNext();) {

        PolygonePanel fillPanel = it.next();
        fillPanel.updatePolygonePanel();
        ;
        k++;
        if (k == this.layer.getStyles().size())
          break;
      }
    } else if (symbolizer.isPointSymbolizer()) {
      for (ListIterator<PointPanel> it = this.ListPoint.listIterator(); it
          .hasNext();) {

        PointPanel pointPanel = it.next();
        pointPanel.updatePointPanel();
        k++;
        if (k == this.layer.getStyles().size())
          break;
      }
    } else if (symbolizer.isLineSymbolizer()) {
      for (ListIterator<LinePanel> it = this.ListLine.listIterator(); it
          .hasNext();) {

        LinePanel linePanel = it.next();
        linePanel.updateLinePanel();
        k++;
        if (k == this.layer.getStyles().size())
          break;
      }
    }
    // Updating the preview style panel

    StyleEditionFrame.stylePanel.paintComponent(StyleEditionFrame.stylePanel
        .getGraphics());
  }

  public void reset() {
    this.layerViewPanel.getProjectFrame().loadSLD(this.initialSLD, true);

    // Updating the preview style panel
    StyleEditionFrame.stylePanel.paintComponent(StyleEditionFrame.stylePanel
        .getGraphics());
  }

  public void getDialogElements() {
    // if ( this.layer.getStyles().get(0).getSymbolizer().isPointSymbolizer()) {
    // this.strokeColorLabel = (JLabel) ((JPanel) ((JPanel) ((JPanel)
    // this.strokePanel
    // .getComponent(0)).getComponent(0)).getComponent(0))
    // .getComponent(1);
    // this.strokeFinalColorLabel = (JLabel) ((JPanel) ((JPanel)
    // this.strokePanel
    // .getComponent(0)).getComponent(1)).getComponent(1);
    // this.strokeOpacitySlider = (JSlider) ((JPanel) ((JPanel) ((JPanel)
    // this.strokePanel
    // .getComponent(0)).getComponent(0)).getComponent(1))
    // .getComponent(1);
    // this.strokeWidthSpinner = (JSpinner) ((JPanel) this.strokePanel
    // .getComponent(1)).getComponent(1);
    // this.unitMeterRadio = (JRadioButton) ((JPanel) ((JPanel) this.strokePanel
    // .getComponent(1)).getComponent(2)).getComponent(1);
    // this.unitPixelRadio = (JRadioButton) ((JPanel) ((JPanel) this.strokePanel
    // .getComponent(1)).getComponent(2)).getComponent(2);
    // this.strokeLineJoinCombo = (JComboBox) ((JPanel) ((JPanel)
    // this.strokePanel
    // .getComponent(2)).getComponent(0)).getComponent(0);
    // this.strokeLineCapCombo = (JComboBox) ((JPanel) ((JPanel)
    // this.strokePanel
    // .getComponent(2)).getComponent(0)).getComponent(1);
    // this.strokeSamplePanel = (StrokeSamplePanel) ((JPanel) this.strokePanel
    // .getComponent(2)).getComponent(1);
    // }

    // if (this.layer.getStyles().get(0).getSymbolizer().isPolygonSymbolizer()
    // || this.layer.getStyles().get(0).getSymbolizer()
    // .isPointSymbolizer()) {
    // this.fillColorLabel = (JLabel) ((JPanel) ((JPanel) ((JPanel)
    // this.fillPanel
    // .getComponent(0)).getComponent(0)).getComponent(0))
    // .getComponent(1);
    // this.fillFinalColorLabel = (JLabel) ((JPanel) ((JPanel) this.fillPanel
    // .getComponent(0)).getComponent(1)).getComponent(1);
    // this.fillOpacitySlider = (JSlider) ((JPanel) ((JPanel) ((JPanel)
    // this.fillPanel
    // .getComponent(0)).getComponent(0)).getComponent(1))
    // .getComponent(1);
    // }
    // if (this.layer.getStyles().get(0).getSymbolizer().isPointSymbolizer()) {
    // this.symbolShapeCombo = (JComboBox) ((JPanel) ((JPanel) this.symbolPanel
    // .getComponent(0)).getComponent(1)).getComponent(0);
    // this.symbolSizeSpinner = (JSpinner) ((JPanel) this.symbolPanel
    // .getComponent(1)).getComponent(1);
    // }
  }

  PointPlacement pointPlacement;
  LinePlacement linePlacement;

  @Override
  public void itemStateChanged(ItemEvent e) {
    this.getDialogElements();
    if (e.getSource() == this.toponymBtn) {
      logger.info("toponymBtn");
      if (e.getStateChange() == ItemEvent.SELECTED) {
        StyleEditionFrame.this.enableToponyms();
      } else {
        StyleEditionFrame.this.disableToponyms();
      }
    }
    if (e.getSource() == this.placements) {
      logger.info("placements");
      this.symbolizer.getLabelPlacement().setPlacement(
          this.placements.getSelectedIndex() == 0 ? this.pointPlacement
              : this.linePlacement);
      this.showPlacementParameters(this.placements.getSelectedIndex());
    }
    if (e.getSource() == this.repeatBtn) {
      logger.info("repeatBtn " + e.getStateChange());
      this.linePlacement.setRepeated(this.repeatBtn.isSelected());
    }

    this.update();
  }

  private void showPlacementParameters(int selectedIndex) {
  }

  public JScrollPane getStrokeExpressiveScrollPane() {
    // TODO Auto-generated method stub
    return this.strokeExpressiveScrollPane;
  }

  public JTabbedPane getTabPane() {
    // TODO Auto-generated method stub
    return this.tabPane;
  }

  public JScrollPane getFillExpressiveScrollPane() {
    // TODO Auto-generated method stub
    return this.fillExpressiveScrollPane;
  }
}

final class StrokeSamplePanel extends JPanel {

  private static final long serialVersionUID = 3453526756981264877L;
  private int cap = BasicStroke.CAP_ROUND;
  private int join = BasicStroke.JOIN_ROUND;
  private final int strokeWidth = 10;
  private final int inset = 2;

  public StrokeSamplePanel() {
    this.setPreferredSize(new Dimension(100, 100));
  }

  public StrokeSamplePanel(int join, int cap) {
    this.join = join;
    this.cap = cap;
    this.setPreferredSize(new Dimension(150, 70));
    this.setOpaque(false);
  }

  /**
   * @param cap the cap to set
   */
  public final void setCap(int cap) {
    this.cap = cap;
  }

  /**
   * @param join the join to set
   */
  public final void setJoin(int join) {
    this.join = join;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    int d = this.strokeWidth + this.inset;
    int[] x = new int[] { d, this.getWidth() / 2, this.getWidth() - d };
    int[] y = new int[] { d, this.getHeight() - d, d };
    Graphics2D g2 = (Graphics2D) g;
    // g2.setColor(java.awt.Color.white);
    // g2.fillRect(0, 0, this.getWidth(), this.getHeight());
    g2.setColor(new java.awt.Color(20, 20, 100));
    BasicStroke stroke = new BasicStroke(10, this.cap, this.join);
    g2.setStroke(stroke);
    g2.drawPolyline(x, y, 3);
    g2.setStroke(new BasicStroke(0));
    g2.setColor(java.awt.Color.lightGray);
    for (int n = 0; n < 3; n++) {
      g2.fillRect(x[n] - 1, y[n] - 1, 3, 3);
    }
  }

}
