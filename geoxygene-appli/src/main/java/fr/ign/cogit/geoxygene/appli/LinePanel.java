package fr.ign.cogit.geoxygene.appli;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.appli.panel.COGITColorChooserPanel;
import fr.ign.cogit.geoxygene.appli.ui.ExpressiveRenderingUIFactory;
import fr.ign.cogit.geoxygene.appli.ui.GenericParameterUI;
import fr.ign.cogit.geoxygene.style.AbstractSymbolizer;
import fr.ign.cogit.geoxygene.style.CategorizedMap;
import fr.ign.cogit.geoxygene.style.ColorMap;
import fr.ign.cogit.geoxygene.style.Fill2DDescriptor;
import fr.ign.cogit.geoxygene.style.Interpolate;
import fr.ign.cogit.geoxygene.style.InterpolationPoint;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.expressive.StrokeExpressiveRenderingDescriptor;

public class LinePanel extends JDialog implements ActionListener,
MouseListener, ChangeListener, ItemListener {
    
    
    private boolean exsist=false;
    
    private JPanel strokePanel;
    private Color strokeColor;
    private float strokeOpacity;
    private double strokeWidth;
    private int strokeLineJoin = BasicStroke.JOIN_ROUND;
    private int strokeLineCap = BasicStroke.CAP_ROUND;
    private String unit="";
    

    
    private JLabel strokeColorLabel;
  


    private JLabel strokeFinalColorLabel;
    private JSlider strokeOpacitySlider;
    private JSpinner strokeWidthSpinner;
    private JRadioButton unitMeterRadio;
    private JRadioButton unitPixelRadio;
    
    
    private JComboBox strokeLineJoinCombo;
    private JComboBox strokeLineCapCombo;
    private StrokeSamplePanel strokeSamplePanel;
    private LineSymbolizer LineSymbolizer;
    private final String[] strokeLineJoinNames = { "Miter", //$NON-NLS-1$
            "Round", //$NON-NLS-1$
            "Bevel" //$NON-NLS-1$
    };
    private final String[] strokeLineCapNames = { "Butt", //$NON-NLS-1$
            "Round", //$NON-NLS-1$
            "Square" //$NON-NLS-1$
    };

    private static final String NO_EXPRESSIVE_TAG = "No expressive description";
    private static Logger logger = Logger.getLogger(StyleEditionFrame.class
            .getName());

    private Layer layer;
    
    // Dialog Elements
    private JColorChooser fillColorChooser;
    private JDialog fillDialog;
    private JLabel fillColorLabel;
    private JLabel fillFinalColorLabel;
    private JSlider fillOpacitySlider;

    private JColorChooser strokeColorChooser;
    private JDialog strokeDialog;
    
    // Work variables
    private Color fillColor;
    private float fillOpacity;

    // Main Dialog Elements
    private JPanel visuPanel;
    private LayerStylesPanel stylePanel=null;

    private int key;

    private JButton addColorMapButton;

    private JButton addCategorizedMapButton;

    private JComboBox expressiveStrokeComboBox;

    private StyleEditionFrame styleEditionFrame;

    private PolygonSymbolizer polygonSymbolizer;

    private PolygonePanel fillPanel;

    private PointPanel pointPanel;
    
    
public LinePanel(StyleEditionFrame styleEditionFrame ,LineSymbolizer LineSymbolizer,int key) {
    this.styleEditionFrame=styleEditionFrame;
    this.layer=styleEditionFrame.getLayer();
    this.stylePanel=styleEditionFrame.getStylePanel();
    this.strokeFinalColorLabel=styleEditionFrame.getStrokeFinalColorLabel();

    
    this.strokePanel = new JPanel();

    this.strokePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

    TitledBorder strokeTitleBorder = BorderFactory
            .createTitledBorder(""); //$NON-NLS-1$
    strokeTitleBorder.setTitleColor(Color.blue);
    this.key=key;
    int nT = key + 1;
    strokeTitleBorder.setTitleFont(new Font("Verdana", Font.BOLD, 16)); //$NON-NLS-1$
    strokeTitleBorder.setTitle(I18N
            .getString("StyleEditionFrame.LineStroke") ); //$NON-NLS-1$
    this.strokePanel.setBorder(strokeTitleBorder);
    this.strokePanel.setPreferredSize(new Dimension(360, 250));
    
    this.strokeColor = LineSymbolizer.getStroke().getStroke();
    this.strokeOpacity = LineSymbolizer.getStroke().getStrokeOpacity();
    this.strokeWidth = LineSymbolizer.getStroke().getStrokeWidth();
    this.strokeLineJoin = LineSymbolizer.getStroke().getStrokeLineJoin();
    this.strokeLineCap = LineSymbolizer.getStroke().getStrokeLineCap();
    this.unit =LineSymbolizer.getUnitOfMeasure();

    this.strokePanel.add(this.createColorPreviewPanel(this.strokeColor,
            this.strokeOpacity));
    this.strokePanel.add(this.createWidthPanel(this.strokeWidth,
            this.unit));
    this.strokePanel.add(this.createJoinCapPanel(this.strokeLineJoin,
            this.strokeLineCap));
    this.addColorMapButton = new JButton(
            I18N.getString("StyleEditionFrame.AddColorMap")); //$NON-NLS-1$
    this.addColorMapButton.addActionListener(this);
    // this.strokePanel.add(this.addColorMapButton);

    this.addCategorizedMapButton = new JButton(
            I18N.getString("StyleEditionFrame.AddCategorizedMap")); //$NON-NLS-1$
    this.addCategorizedMapButton.addActionListener(this);
    this.strokePanel.add(this.addColorMapButton);
    this.strokePanel.add(this.addCategorizedMapButton);
    this.strokePanel.add(this.getExpressiveStrokeComboBox());
    this.strokePanel.setAlignmentX(LEFT_ALIGNMENT);

}

public void addStrokePanel(StyleEditionFrame styleEditionFrame ,int key){
    this.styleEditionFrame=styleEditionFrame;
    this.layer=styleEditionFrame.getLayer();
    this.stylePanel=styleEditionFrame.getStylePanel();
    this.strokeFinalColorLabel=styleEditionFrame.getStrokeFinalColorLabel();
    
    this.key=key;
    int indice =layer.getStyles().size();

    this.strokePanel = new JPanel();

    this.strokePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

    TitledBorder strokeTitleBorder = BorderFactory
            .createTitledBorder(""); //$NON-NLS-1$
    strokeTitleBorder.setTitleColor(Color.blue);
    int nT = indice + 1;
    strokeTitleBorder.setTitleFont(new Font("Verdana", Font.BOLD, 16)); //$NON-NLS-1$
    strokeTitleBorder.setTitle(I18N
            .getString("StyleEditionFrame.LineStroke") ); //$NON-NLS-1$
    this.strokePanel.setBorder(strokeTitleBorder);
    this.strokePanel.setPreferredSize(new Dimension(360, 250));

    StyledLayerDescriptor sld;
    try {
        sld = StyledLayerDescriptor
                .unmarshall(StyledLayerDescriptor.class
                        .getClassLoader().getResourceAsStream(
                                "sld/BasicStyles.xml"));
        layer.getStyles().add(
                sld.getLayer("Basic Line").getStyles().get(0)); //$NON-NLS-1$
    } catch (JAXBException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    } //$NON-NLS-1$

    this.strokeColor = ((LineSymbolizer) layer.getStyles().get(1)
            .getSymbolizer()).getStroke().getStroke();
    this.strokeOpacity = ((LineSymbolizer) layer.getStyles()
            .get(1).getSymbolizer()).getStroke().getStrokeOpacity();
    this.strokeWidth = 0;
    
   this.strokePanel.add(this.createColorPreviewPanel(this.strokeColor,this.strokeOpacity));
   this.unit="http://www.opengeospatial.org/se/units/metre";
   this.strokePanel.add(this.createWidthPanel(this.strokeWidth, this.unit));
   this.strokePanel.add(this.createJoinCapPanel(this.strokeLineJoin,this.strokeLineCap));
   
   this.strokePanel.setAlignmentX(LEFT_ALIGNMENT);
   
   this.addColorMapButton = new JButton(
           I18N.getString("StyleEditionFrame.AddColorMap")); //$NON-NLS-1$
   this.addColorMapButton.addActionListener(this);
   // this.strokePanel.add(this.addColorMapButton);

   this.addCategorizedMapButton = new JButton(
           I18N.getString("StyleEditionFrame.AddCategorizedMap")); //$NON-NLS-1$
   this.addCategorizedMapButton.addActionListener(this);
   this.strokePanel.add(this.addColorMapButton);
   this.strokePanel.add(this.addCategorizedMapButton);
   this.strokePanel.add(this.getExpressiveStrokeComboBox());
    
}

    // plygone

    public LinePanel(PolygonePanel fillPanel,StyleEditionFrame styleEditionFrame,
            PolygonSymbolizer polygonSymbolizer, int key) {
        this.styleEditionFrame = styleEditionFrame;
        this.layer = styleEditionFrame.getLayer();
        this.stylePanel = styleEditionFrame.getStylePanel();
        this.strokeFinalColorLabel = styleEditionFrame
                .getStrokeFinalColorLabel();
        this.polygonSymbolizer = polygonSymbolizer;
        this.fillPanel=fillPanel;


        this.strokePanel = new JPanel();

        this.strokePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        TitledBorder strokeTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
        strokeTitleBorder.setTitleColor(Color.blue);
        this.key = key;
        int nT = key + 1;
        strokeTitleBorder.setTitleFont(new Font("Verdana", Font.BOLD, 16)); //$NON-NLS-1$
        strokeTitleBorder.setTitle(I18N
                .getString("StyleEditionFrame.LineStroke")); //$NON-NLS-1$
        this.strokePanel.setBorder(strokeTitleBorder);
        this.strokePanel.setPreferredSize(new Dimension(360, 250));

        this.strokeColor = polygonSymbolizer.getStroke().getStroke();
        this.strokeOpacity = polygonSymbolizer.getStroke().getStrokeOpacity();
        this.strokeWidth = polygonSymbolizer.getStroke().getStrokeWidth();
        
        this.strokeLineJoin = polygonSymbolizer.getStroke().getStrokeLineJoin();
        this.strokeLineCap = polygonSymbolizer.getStroke().getStrokeLineCap();
        
        this.unit = polygonSymbolizer.getUnitOfMeasure();

        
//        System.out.println( "key ="+this.key+" ,color :"+this.strokeColor +" , opaciti :"+
//                this.strokeOpacity+",this.strokeWidth :"+ this.strokeWidth+",this.strokeLineJoin:"
//                        + this.strokeLineJoin+",this.strokeLineCap: "+this.strokeLineCap+",unit :"+this.unit);
        
        
        this.strokePanel.add(this.createColorPreviewPanel(this.strokeColor,
                this.strokeOpacity));
        this.strokePanel
                .add(this.createWidthPanel(this.strokeWidth, this.unit));
        this.strokePanel.add(this.createJoinCapPanel(this.strokeLineJoin,
                this.strokeLineCap));
        this.addColorMapButton = new JButton(
                I18N.getString("StyleEditionFrame.AddColorMap")); //$NON-NLS-1$
        this.addColorMapButton.addActionListener(this);
        // this.strokePanel.add(this.addColorMapButton);

        this.addCategorizedMapButton = new JButton(
                I18N.getString("StyleEditionFrame.AddCategorizedMap")); //$NON-NLS-1$
        this.addCategorizedMapButton.addActionListener(this);
        this.strokePanel.add(this.addColorMapButton);
        this.strokePanel.add(this.addCategorizedMapButton);
        this.strokePanel.add(this.getExpressiveStrokeComboBox());
        this.strokePanel.setAlignmentX(LEFT_ALIGNMENT);
    }

    public LinePanel(JPanel strokePanel) {
        // TODO Auto-generated constructor stub
        this.strokePanel = strokePanel;

    }


    public void addStrokePanelFill(StyleEditionFrame styleEditionFrame ,int key){
        
        this.styleEditionFrame=styleEditionFrame;
        this.key=key;
        this.layer=styleEditionFrame.getLayer();
        this.stylePanel=styleEditionFrame.getStylePanel();
        this.strokeFinalColorLabel=styleEditionFrame.getStrokeFinalColorLabel();
        int indice =styleEditionFrame.getLayer().getStyles().size();

        this.strokePanel = new JPanel();

        this.strokePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        TitledBorder strokeTitleBorder = BorderFactory
                .createTitledBorder(""); //$NON-NLS-1$
        strokeTitleBorder.setTitleColor(Color.blue);
        int nT = indice + 1;
        strokeTitleBorder.setTitleFont(new Font("Verdana", Font.BOLD, 16)); //$NON-NLS-1$
        strokeTitleBorder.setTitle(I18N
                .getString("StyleEditionFrame.LineStroke")); //$NON-NLS-1$
        this.strokePanel.setBorder(strokeTitleBorder);
        this.strokePanel.setPreferredSize(new Dimension(360, 250));

       

        this.strokeColor = ((PolygonSymbolizer) layer.getStyles().get(1)
                .getSymbolizer()).getStroke().getStroke();
        this.strokeOpacity = ((PolygonSymbolizer) layer.getStyles()
                .get(1).getSymbolizer()).getStroke().getStrokeOpacity();
        this.strokeWidth = 0;
        
       this.strokePanel.add(this.createColorPreviewPanel(this.strokeColor,this.strokeOpacity));
       this.unit="http://www.opengeospatial.org/se/units/metre";
       this.strokePanel.add(this.createWidthPanel(this.strokeWidth, this.unit));
       this.strokePanel.add(this.createJoinCapPanel(this.strokeLineJoin,this.strokeLineCap));
       
       this.strokePanel.setAlignmentX(LEFT_ALIGNMENT);
       
       this.addColorMapButton = new JButton(
               I18N.getString("StyleEditionFrame.AddColorMap")); //$NON-NLS-1$
       this.addColorMapButton.addActionListener(this);
       // this.strokePanel.add(this.addColorMapButton);

       this.addCategorizedMapButton = new JButton(
               I18N.getString("StyleEditionFrame.AddCategorizedMap")); //$NON-NLS-1$
       this.addCategorizedMapButton.addActionListener(this);
       this.strokePanel.add(this.addColorMapButton);
       this.strokePanel.add(this.addCategorizedMapButton);
       this.strokePanel.add(this.getExpressiveStrokeComboBox());
       this.updateLayer();
        
    }

public PolygonSymbolizer getPolygonSymbolizer() {
    return polygonSymbolizer;
}

public void setPolygonSymbolizer(PolygonSymbolizer polygonSymbolizer) {
    this.polygonSymbolizer = polygonSymbolizer;
}

//****
//*constructeur pour pointSymbolizer 
public LinePanel(PointPanel pointPanel,
        StyleEditionFrame styleEditionFrame,
        PointSymbolizer pointSymbolizer, int key) {
    
    this.pointPanel=pointPanel;
    this.styleEditionFrame=styleEditionFrame;
    this.layer=styleEditionFrame.getLayer();
    this.stylePanel=styleEditionFrame.getStylePanel();
    this.strokeFinalColorLabel=styleEditionFrame.getStrokeFinalColorLabel();
    this.key=key;
    // TODO Auto-generated constructor stub
    TitledBorder fillTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
    fillTitleBorder.setTitleColor(Color.blue);
    fillTitleBorder.setTitleFont(new Font("Verdana", Font.BOLD, 16)); //$NON-NLS-1$
    fillTitleBorder.setTitle(I18N.getString("StyleEditionFrame.PointFill")); //$NON-NLS-1$
    
    this.strokePanel = new JPanel();
    this.strokePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    TitledBorder strokeTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
    strokeTitleBorder.setTitleColor(fillTitleBorder.getTitleColor());
    strokeTitleBorder.setTitleFont(fillTitleBorder.getTitleFont());
    strokeTitleBorder.setTitle(I18N
            .getString("StyleEditionFrame.PointStroke")); //$NON-NLS-1$
    this.strokePanel.setBorder(strokeTitleBorder);
    this.strokePanel.setPreferredSize(new Dimension(420, 250));

    this.strokeColor = pointSymbolizer.getGraphic().getMarks().get(0).getStroke()
            .getStroke();
    this.strokeOpacity = pointSymbolizer.getGraphic().getMarks().get(0).getStroke()
            .getStrokeOpacity();
    this.strokeWidth = pointSymbolizer.getGraphic().getMarks().get(0).getStroke()
            .getStrokeWidth();
    this.strokeLineJoin = pointSymbolizer.getGraphic().getMarks().get(0).getStroke()
            .getStrokeLineJoin();
    this.strokeLineCap = pointSymbolizer.getGraphic().getMarks().get(0).getStroke()
            .getStrokeLineCap();
    this.unit = pointSymbolizer.getUnitOfMeasure();
    this.strokePanel.add(this.createColorPreviewPanel(this.strokeColor,
            this.strokeOpacity));
    this.strokePanel
            .add(this.createWidthPanel(this.strokeWidth, this.unit));
    this.strokePanel.add(this.createJoinCapPanel(this.strokeLineJoin,
            this.strokeLineCap));
    
    
}

//***************************
private JComboBox getExpressiveStrokeComboBox() {
    if (this.expressiveStrokeComboBox == null) {
        Vector<XmlElement> descriptors = new Vector<XmlElement>();
        final String fieldName = "expressiveRendering";
        Class<Stroke> strokeClass = Stroke.class;
        XmlElement currentSelectedObject = null;
        try {
            // Check field class type
            Field field = strokeClass.getField(fieldName);
            if (!field.getType().equals(
                    StrokeExpressiveRenderingDescriptor.class)) {
                logger.error(fieldName
                        + " field from "
                        + strokeClass.getSimpleName()
                        + " is intended to be of type 'StrokeExpressiveRenderingDescriptor' not "
                        + field.getType().getSimpleName());
                this.expressiveStrokeComboBox = new JComboBox();
                return this.expressiveStrokeComboBox;
            }
            StrokeExpressiveRenderingDescriptor currentStroke = ((AbstractSymbolizer) this.layer
                    .getStyles().get(this.key).getSymbolizer()).getStroke()
                    .getExpressiveRendering();
            Class<?> currentExpressiveStrokeClass = currentStroke == null ? null
                    : currentStroke.getClass();
            // get XmlElements annotations
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation instanceof XmlElements) {
                    XmlElements elts = (XmlElements) annotation;
                    for (XmlElement elt : elts.value()) {
                        descriptors.add(elt);
                        if (elt.type().equals(currentExpressiveStrokeClass)) {
                            currentSelectedObject = elt;
                        }
                    }
                } else {
                    logger.warn(fieldName
                            + " field from "
                            + strokeClass.getSimpleName()
                            + " is intended to have only '@XmlElements' annotation. Ignore '@"
                            + annotation.annotationType().getSimpleName()
                            + "' annotation");
                }

            }
        } catch (NoSuchFieldException e) {
            logger.error("Field '" + fieldName
                    + "' does not exist in class " + strokeClass.getName());
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        descriptors.add(null);
        this.expressiveStrokeComboBox = new JComboBox(descriptors);
        this.expressiveStrokeComboBox
                .setSelectedItem(currentSelectedObject);
        this.expressiveStrokeComboBox
                .setRenderer(new ListCellRenderer<XmlElement>() {
                    private final JLabel label = new JLabel();

                    @Override
                    public Component getListCellRendererComponent(
                            JList<? extends XmlElement> list,
                            XmlElement value, int index,
                            boolean isSelected, boolean cellHasFocus) {

                        this.label
                                .setText(value == null ? NO_EXPRESSIVE_TAG
                                        : value.name());
                        return this.label;
                    }
                });
        this.expressiveStrokeComboBox
                .addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        XmlElement elt = (XmlElement) LinePanel.this.expressiveStrokeComboBox
                                .getSelectedItem();
                        StrokeExpressiveRenderingDescriptor expressiveStroke;
                        try {
                            expressiveStroke = elt == null ? null
                                    : (StrokeExpressiveRenderingDescriptor) elt
                                            .type().newInstance();
                            // Updating the layer style
                            AbstractSymbolizer abstractSymbolizer = (AbstractSymbolizer) LinePanel.this.layer
                                    .getStyles().get(LinePanel.this.key).getSymbolizer();
                            abstractSymbolizer.getStroke()
                                    .setExpressiveRendering(
                                            expressiveStroke);
                            LinePanel.this.updateExpressivePanel();
                        } catch (InstantiationException e1) {
                            e1.printStackTrace();
                        } catch (IllegalAccessException e1) {
                            e1.printStackTrace();
                        }
                    }

                });
    }
    return this.expressiveStrokeComboBox;
}

public void updateExpressivePanel() {
    this.addOrReplaceExpressiveRenderingUI();
}


private void addOrReplaceExpressiveRenderingUI() {
    if (this.styleEditionFrame.getTabPane() == null) {
        throw new IllegalStateException(
                "this method can only be called after tabPane creation");
    }
    String tabTooltip = "";
    this.styleEditionFrame.getTabPane().remove(this.styleEditionFrame.getFillExpressiveScrollPane());
    this.styleEditionFrame.getTabPane().remove(this.styleEditionFrame.getStrokeExpressiveScrollPane());
    // Stroke Expressive UI
    GenericParameterUI ui = null;
    if (((AbstractSymbolizer) this.layer.getStyles().get(this.key).getSymbolizer()).getStroke() != null) {
        StrokeExpressiveRenderingDescriptor expressiveStroke = ((AbstractSymbolizer) (this.layer
                .getStyles().get(this.key).getSymbolizer())).getStroke().getExpressiveRendering();
        if (expressiveStroke != null) {
            ui = ExpressiveRenderingUIFactory
                    .getExpressiveRenderingUI(expressiveStroke,
                            this.styleEditionFrame.getLayerViewPanel().getProjectFrame());
            tabTooltip = expressiveStroke.getClass().getSimpleName();
        }
    }
    if (ui != null) {
        this.styleEditionFrame.getStrokeExpressiveScrollPane().setViewportView(ui.getGui());
        this.styleEditionFrame.getTabPane().addTab("Expressive Stroke", null,
                this.styleEditionFrame.getStrokeExpressiveScrollPane(), tabTooltip);
    }

}


/**
 * This method creates and return the color preview panel. It contains 3
 * components :
 * <ul>
 * <li>- the raw color,</li>
 * <li>- the level of transparency,</li>
 * <li>- the final color (raw color + transparency)</li>
 * </ul>
 * 
 * @param c
 *            the raw color of the style to be modified.
 * @param transparency
 *            the transparency of the style to be modified.
 * @return the color preview panel.
 */
public JPanel createColorPreviewPanel(Color c, float transparency) {
    JPanel paramColorPanel = new JPanel();
    paramColorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    paramColorPanel.setPreferredSize(new Dimension(300, 90));
    paramColorPanel.add(this.createColorPanel(c));

    paramColorPanel
            .add(this.createOpacityPanel((int) (transparency * 100)));

    JPanel colorPreviewPanel = new JPanel();
    colorPreviewPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    colorPreviewPanel.add(paramColorPanel);
    colorPreviewPanel.add(this.createFinalColorPanel(new Color((float) c
            .getRed() / 255, (float) c.getGreen() / 255, (float) c
            .getBlue() / 255, transparency)));

    return colorPreviewPanel;
}
/**
 * This method creates and return the panel of the raw color. It form a part
 * of the color preview panel.
 * 
 * @param c
 *            the raw color of the style to be modified.
 * @return the panel of the raw color.
 */
public JPanel createColorPanel(Color c) {
    JLabel lblColor = new JLabel(I18N.getString("StyleEditionFrame.Color")); //$NON-NLS-1$

    BufferedImage buffImColor = new BufferedImage(100, 30,
            java.awt.image.BufferedImage.TYPE_INT_RGB);

    Graphics2D g = buffImColor.createGraphics();
    g.setColor(Color.white);
    g.fillRect(0, 0, 100, 30);
    g.setColor(c);
    g.fillRect(0, 0, 100, 30);

    JLabel imageColor = new JLabel(new ImageIcon(buffImColor));
    imageColor.addMouseListener(this);

    JPanel colorPanel = new JPanel();
    colorPanel.add(lblColor);
    colorPanel.add(imageColor);

    return colorPanel;
}
/**
 * This method creates and return the panel of the opacity. It form a part
 * of the color preview panel.
 * 
 * @param opacity
 *            the opacity of the style to be modified.
 * @return the panel of the opacity.
 */
public JPanel createOpacityPanel(int opacity) {
    JLabel lblTransparency = new JLabel(
            I18N.getString("StyleEditionFrame.Opacity")); //$NON-NLS-1$

    JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, opacity);
    slider.addChangeListener(this);

    JPanel transparencyPanel = new JPanel();
    transparencyPanel.add(lblTransparency);
    transparencyPanel.add(slider);

    return transparencyPanel;
}
/**
 * This method creates and return the panel of the final color. It form a
 * part of the color preview panel.
 * 
 * @param c
 *            the final color (raw color + transparency) of the style to be
 *            modified.
 * @return the panel of the final color.
 */
public JPanel createFinalColorPanel(Color c) {
    JPanel finalColorPanel = new JPanel();
    JTextArea lblColor = new JTextArea(
            I18N.getString("StyleEditionFrame.ColorPreview")); //$NON-NLS-1$
    lblColor.setBackground(finalColorPanel.getBackground());
    lblColor.setFont(new Font("Arial", Font.BOLD, 12)); //$NON-NLS-1$

    BufferedImage buffImColor = new BufferedImage(50, 40,
            java.awt.image.BufferedImage.TYPE_INT_RGB);
    Graphics2D g = buffImColor.createGraphics();
    g.setColor(Color.white);
    g.fillRect(0, 0, 50, 40);
    g.setColor(c);
    g.fillRect(0, 0, 50, 40);
    JLabel imageColor = new JLabel(new ImageIcon(buffImColor));

    finalColorPanel.setPreferredSize(new Dimension(80, 90));
    finalColorPanel.add(lblColor);
    finalColorPanel.add(imageColor);

    return finalColorPanel;
}
/**
 * This method creates and return the panel of the width. It used for the
 * stroke elements and contains the unit panel.
 * 
 * @param width
 *            the width of the stroke of the style to be modified.
 * @param unit
 *            the unit of the style to be modified.
 * @return the panel of the width.
 */
public JPanel createWidthPanel(double width, String unit) {
    JLabel lblWidth = new JLabel(I18N.getString("StyleEditionFrame.Width")); //$NON-NLS-1$

    SpinnerModel model = new SpinnerNumberModel(width, // initial value
            0d, // min
            1000d, // max
            0.5d);
    JSpinner spinner = new JSpinner(model);
    spinner.addChangeListener(this);

    JPanel widthPanel = new JPanel();
    widthPanel.add(lblWidth);
    widthPanel.add(spinner);
    widthPanel.add(this.createUnitPanel(unit));
    return widthPanel;
}
/**
 * This method creates and return the panel of the unit of the style. It
 * form a part of the panel of the width and impact the size of a point
 * symbol.
 * 
 * @param unit
 *            the unit of the style.
 * @return the panel of the unit of the style.
 */
public JPanel createUnitPanel(String unit) {
    JLabel lblDist = new JLabel(I18N.getString("StyleEditionFrame.Unit")); //$NON-NLS-1$

    JRadioButton meter = new JRadioButton(
            I18N.getString("StyleEditionFrame.meter")); //$NON-NLS-1$
    meter.setActionCommand("meter"); //$NON-NLS-1$
    meter.addActionListener(this);

    JRadioButton pixel = new JRadioButton(
            I18N.getString("StyleEditionFrame.pixel")); //$NON-NLS-1$
    pixel.setActionCommand("pixel"); //$NON-NLS-1$
    pixel.addActionListener(this);

    if (unit.equalsIgnoreCase(Symbolizer.METRE)) {
        meter.setSelected(true);
    } else if (unit.equalsIgnoreCase(Symbolizer.PIXEL)) {
        pixel.setSelected(true);
    }

    ButtonGroup group = new ButtonGroup();
    group.add(meter);
    group.add(pixel);

    JPanel distPanel = new JPanel();
    distPanel.add(lblDist);
    distPanel.add(meter);
    distPanel.add(pixel);

    return distPanel;
}
/**
 * This method creates and return the panel to choose a line join and cap.
 * 
 * @return the panel of the cap & join choice.
 */
public JPanel createJoinCapPanel(int joinValue, int capValue) {

    JPanel panel = new JPanel();

    JPanel leftPanel = new JPanel();
    // leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));

    JComboBox joinCombo = new JComboBox(this.strokeLineJoinNames);
    joinCombo.setBorder(BorderFactory.createTitledBorder(I18N
            .getString("StyleEditionFrame.LineJoinTitle")));
    joinCombo.setSelectedIndex(joinValue);

    leftPanel.add(joinCombo);
    joinCombo.addItemListener(this);

    JComboBox capCombo = new JComboBox(this.strokeLineCapNames);
    capCombo.setBorder(BorderFactory.createTitledBorder(I18N
            .getString("StyleEditionFrame.LineCapTitle")));
    capCombo.setSelectedIndex(capValue);

    leftPanel.add(capCombo);
    capCombo.addItemListener(this);

    panel.add(leftPanel);
    StrokeSamplePanel strokeSample = new StrokeSamplePanel(joinValue,
            capValue);
    strokeSample.setPreferredSize(new Dimension(150, 50));
    panel.add(strokeSample);

    return panel;
}

public void updateLinePanel(){
    this.updateLayer();
  
}

public JLabel getStrokeColorLabel() {
    return strokeColorLabel;
}


public void setStrokeColorLabel(JLabel strokeColorLabel) {
    this.strokeColorLabel = strokeColorLabel;
}


public JLabel getStrokeFinalColorLabel() {
    return strokeFinalColorLabel;
}


public void setStrokeFinalColorLabel(JLabel strokeFinalColorLabel) {
    this.strokeFinalColorLabel = strokeFinalColorLabel;
}


public JSlider getStrokeOpacitySlider() {
    return strokeOpacitySlider;
}


public void setStrokeOpacitySlider(JSlider strokeOpacitySlider) {
    this.strokeOpacitySlider = strokeOpacitySlider;
}


public JSpinner getStrokeWidthSpinner() {
    return strokeWidthSpinner;
}


public void setStrokeWidthSpinner(JSpinner strokeWidthSpinner) {
    this.strokeWidthSpinner = strokeWidthSpinner;
}


public JRadioButton getUnitMeterRadio() {
    return unitMeterRadio;
}


public void setUnitMeterRadio(JRadioButton unitMeterRadio) {
    this.unitMeterRadio = unitMeterRadio;
}


public JRadioButton getUnitPixelRadio() {
    return unitPixelRadio;
}


public void setUnitPixelRadio(JRadioButton unitPixelRadio) {
    this.unitPixelRadio = unitPixelRadio;
}

public boolean isExsist() {
    return exsist;
}


public void setExsist(boolean exsist) {
    this.exsist = exsist;
}



public JPanel getStrokePanel() {
    return strokePanel;
}


public void setStrokePanel(JPanel strokePanel) {
    this.strokePanel = strokePanel;
}


public Color getStrokeColor() {
    return strokeColor;
}


public void setStrokeColor(Color strokeColor) {
    this.strokeColor = strokeColor;
}


public float getStrokeOpacity() {
    return strokeOpacity;
}


public void setStrokeOpacity(float strokeOpacity) {
    this.strokeOpacity = strokeOpacity;
}


public int getKey() {
    return key;
}

public void setKey(int key) {
    this.key = key;
}

public double getStrokeWidth() {
    return strokeWidth;
}


public void setStrokeWidth(double strokeWidth) {
    this.strokeWidth = strokeWidth;
}


public int getStrokeLineJoin() {
    return strokeLineJoin;
}


public void setStrokeLineJoin(int strokeLineJoin) {
    this.strokeLineJoin = strokeLineJoin;
}


public int getStrokeLineCap() {
    return strokeLineCap;
}


public void setStrokeLineCap(int strokeLineCap) {
    this.strokeLineCap = strokeLineCap;
}


public String getUnit() {
    return unit;
}


public void setUnit(String unit) {
    this.unit = unit;
}

public JComboBox getStrokeLineJoinCombo() {
    return strokeLineJoinCombo;
}

public void setStrokeLineJoinCombo(JComboBox strokeLineJoinCombo) {
    this.strokeLineJoinCombo = strokeLineJoinCombo;
}

public JComboBox getStrokeLineCapCombo() {
    return strokeLineCapCombo;
}

public void setStrokeLineCapCombo(JComboBox strokeLineCapCombo) {
    this.strokeLineCapCombo = strokeLineCapCombo;
}

public StrokeSamplePanel getStrokeSamplePanel() {
    return strokeSamplePanel;
}

public void setStrokeSamplePanel(StrokeSamplePanel strokeSamplePanel) {
    this.strokeSamplePanel = strokeSamplePanel;
}

public LineSymbolizer getLineSymbolizer() {
    return LineSymbolizer;
}

public void setLineSymbolizer(LineSymbolizer lineSymbolizer) {
    LineSymbolizer = lineSymbolizer;
}
@Override
public void itemStateChanged(ItemEvent e) {
    this.getDialogElements();

    if (e.getSource() == this.strokeLineJoinCombo) {
        this.strokeLineJoin = this.strokeLineJoinCombo.getSelectedIndex();
        this.strokeSamplePanel.setJoin(this.strokeLineJoin);
        this.strokeSamplePanel.repaint();
    }
    if (e.getSource() == this.strokeLineCapCombo) {
        this.strokeLineCap = this.strokeLineCapCombo.getSelectedIndex();
        this.strokeSamplePanel.setCap(this.strokeLineCap);
        this.strokeSamplePanel.repaint();
    }
  
    this.update();
}
/**
 * Update the display (layer legend and layer view).
 */
public void update() {
   // System.out.println("update");
    this.styleEditionFrame.getLayerLegendPanel().getModel().fireActionPerformed(null);
    this.styleEditionFrame.getLayerLegendPanel().repaint();
    this.styleEditionFrame.getLayerViewPanel().repaint();
}
@Override
public void stateChanged(ChangeEvent arg0) {
  //  System.out.println("stateChanged");
    this.getDialogElements();

    if (arg0.getSource() == this.strokeOpacitySlider) {
        this.strokeOpacity = ((float) this.strokeOpacitySlider.getValue()) / 100;

        // Updating the label of the final color (with opacity)
        BufferedImage buffImFinalColor = new BufferedImage(50, 40,
                java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics g2 = buffImFinalColor.getGraphics();
        g2.setColor(Color.white);
        g2.fillRect(0, 0, 50, 40);
        g2.setColor(new Color((float) this.strokeColor.getRed() / 255,
                (float) this.strokeColor.getGreen() / 255,
                (float) this.strokeColor.getBlue() / 255,
                this.strokeOpacity));
        g2.fillRect(0, 0, 50, 40);
        this.strokeFinalColorLabel.setIcon(new ImageIcon(buffImFinalColor));

        this.updateLayer();
    }

    if (arg0.getSource() == this.strokeWidthSpinner) {
        this.strokeWidth = ((SpinnerNumberModel) this.strokeWidthSpinner
                .getModel()).getNumber().doubleValue();
        this.updateLayer();
    }

}

@Override
public void mouseClicked(MouseEvent arg0) {
   // System.out.println("mouseClicked");
    this.getDialogElements();

    if (arg0.getSource() == this.strokeColorLabel) {
        this.strokeColorChooser = new JColorChooser();
        this.strokeColorChooser
                .addChooserPanel(new COGITColorChooserPanel());
        this.strokeDialog = JColorChooser.createDialog(this,
                I18N.getString("StyleEditionFrame.PickAColor"), //$NON-NLS-1$
                true, this.strokeColorChooser, this, null);
        this.strokeDialog.setVisible(true);
    }

}
@Override
public void mousePressed(MouseEvent e) {
    // TODO Auto-generated method stub
    
}
@Override
public void mouseReleased(MouseEvent e) {
    // TODO Auto-generated method stub
    
}
@Override
public void mouseEntered(MouseEvent e) {
    // TODO Auto-generated method stub
    
}
@Override
public void mouseExited(MouseEvent e) {
    // TODO Auto-generated method stub
    
}


    @Override
    public void actionPerformed(ActionEvent e) {
      //  System.out.println("actionPerformed");
        this.getDialogElements();

        if (e.getSource() == this.addColorMapButton) {

            List<GF_AttributeType> attributes = this.layer
                    .getFeatureCollection().getFeatureType()
                    .getFeatureAttributes();
            if (attributes.isEmpty()) {
                return;
            }
            Object[] possibilities = attributes.toArray();
            GF_AttributeType select = ((GF_AttributeType) JOptionPane
                    .showInputDialog(
                            this,
                            I18N.getString("StyleEditionFrame.ChooseAttribute"), //$NON-NLS-1$
                            I18N.getString("StyleEditionFrame.ChooseAttributeWindowTitle"), //$NON-NLS-1$
                            JOptionPane.PLAIN_MESSAGE, null, possibilities,
                            attributes.get(0).toString()));
            String attributeName = "";
            try {
                attributeName = select.getMemberName();

            } catch (Exception ex) {
                return;
            }

            // TODO If a string was returned, say so.
            if ((attributeName != null) && (attributeName.length() > 0)) {
                double min = Double.POSITIVE_INFINITY;
                double max = Double.NEGATIVE_INFINITY;
                for (IFeature f : this.layer.getFeatureCollection()) {
                    try {
                        Double v = Double.parseDouble(f.getAttribute(
                                attributeName).toString());
                        min = Math.min(min, v);
                        max = Math.max(max, v);
                    } catch (NumberFormatException exception) {
                    }
                }
                double diff = max - min;
                double range = diff / 5;
                ColorMap cm = new ColorMap();
                Interpolate interpolate = new Interpolate();
                double value = min;
                interpolate.getInterpolationPoint().add(
                        new InterpolationPoint(value, Color.black));
                value += range;
                interpolate.getInterpolationPoint().add(
                        new InterpolationPoint(value, new Color(128, 0, 0)));
                value += range;
                interpolate.getInterpolationPoint().add(
                        new InterpolationPoint(value, new Color(255, 0, 0)));
                value += range;
                interpolate.getInterpolationPoint().add(
                        new InterpolationPoint(value, new Color(255, 128, 0)));
                value += range;
                interpolate.getInterpolationPoint().add(
                        new InterpolationPoint(value, new Color(255, 255, 0)));
                value += range;
                interpolate.getInterpolationPoint().add(
                        new InterpolationPoint(max, Color.white));
                cm.setInterpolate(interpolate);
                cm.setPropertyName(attributeName);
                if (this.layer.getStyles().get(this.key).getSymbolizer().isPolygonSymbolizer()) {
                    PolygonSymbolizer ps = (PolygonSymbolizer)  this.styleEditionFrame.getLayer().getStyles().get(this.key)
                            .getSymbolizer();
                    ps.setColorMap(cm);
                    ps.setFill(null);
                  //**********************
                    this.fillPanel.getFillPanel().removeAll();
                    this.fillPanel.getFillPanel().setLayout(null);
                    JLabel label=new JLabel();
                    label.setText("Fill Vide");
                    label.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 36));
                    label.setBounds(150, 120, 150, 36);
                    this.fillPanel.getFillPanel().add(label);

                    
                    //*********************
                    return;
                } else if (this.layer.getStyles().get(this.key).getSymbolizer().isLineSymbolizer()) {
                    LineSymbolizer ls = (LineSymbolizer) this.layer.getStyles()
                            .get(this.key).getSymbolizer();
                    ls.setColorMap(cm);
                    return;
                }

            }
            return;
        }
        if (e.getSource() == this.addCategorizedMapButton) {
            List<GF_AttributeType> attributes = this.layer
                    .getFeatureCollection().getFeatureType()
                    .getFeatureAttributes();
            if (attributes.isEmpty()) {
                return;
            }
            Object[] possibilities = attributes.toArray();
            GF_AttributeType select = ((GF_AttributeType) JOptionPane
                    .showInputDialog(
                            this,
                            I18N.getString("StyleEditionFrame.ChooseAttribute"), //$NON-NLS-1$
                            I18N.getString("StyleEditionFrame.ChooseAttributeWindowTitle"), //$NON-NLS-1$
                            JOptionPane.PLAIN_MESSAGE, null, possibilities,
                            attributes.get(0).toString()));
            String attributeName = "";
            try {
                attributeName = select.getMemberName();

            } catch (Exception ex) {
                return;
            }

            if ((attributeName != null) && (attributeName.length() > 0)) {
                CategorizedMap categorizedMap = new CategorizedMap();
                categorizedMap.setPropertyName(attributeName);
                if (this.layer.getStyles().get(this.key).getSymbolizer().isLineSymbolizer()) {
                    LineSymbolizer ls = (LineSymbolizer) this.layer.getStyles()
                            .get(key).getSymbolizer();
                    ls.setCategorizedMap(categorizedMap);
                    return;
                }
                if (this.styleEditionFrame.getLayer().getSymbolizer().isPolygonSymbolizer()) {
                    PolygonSymbolizer ps = (PolygonSymbolizer) this.styleEditionFrame.getLayer().getStyles().get(this.key)
                            .getSymbolizer();
                    ps.setCategorizedMap(categorizedMap);
                    ps.setFill(null);
                  //**********************
                    this.fillPanel.getFillPanel().removeAll();
                    this.fillPanel.getFillPanel().setLayout(null);
                    JLabel label=new JLabel();
                    label.setText("Fill Vide");
                    label.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 36));
                    label.setBounds(150, 120, 150, 36);
                    this.fillPanel.getFillPanel().add(label);

                    
                    //*********************
                    return;
                } 
            }
            return;
        }
        // When the user change the unit of a style.
        if (e.getSource() == this.unitMeterRadio) {
            this.unit = Symbolizer.METRE;
            
        }
        if (e.getSource() == this.unitPixelRadio) {
            this.unit = Symbolizer.PIXEL;
        }
        
        // When the user validate a color in the Color Chooser interface
        if (e.getSource().getClass() != JComboBox.class
                && e.getSource().getClass() != JRadioButton.class
                && e.getSource().getClass() != JCheckBox.class) {
            if (((JButton) e.getSource()).getActionCommand() == "OK") { //$NON-NLS-1$

                JDialog dialog = (JDialog) ((JButton) e.getSource())
                        .getParent().getParent().getParent().getParent()
                        .getParent();
                if (dialog == this.fillDialog) {
                    // Getting the color of the dialog
                    this.fillColor = this.fillColorChooser.getColor();

                    // Updating the label of the raw color
                    BufferedImage buffImColor = new BufferedImage(100, 30,
                            java.awt.image.BufferedImage.TYPE_INT_RGB);
                    Graphics g = buffImColor.getGraphics();
                    g.setColor(Color.white);
                    g.fillRect(0, 0, 100, 30);
                    g.setColor(this.fillColor);
                    g.fillRect(0, 0, 100, 30);
                    this.fillColorLabel.setIcon(new ImageIcon(buffImColor));

                    // Updating the label of the final color (with opacity)
                    BufferedImage buffImFinalColor = new BufferedImage(50, 40,
                            java.awt.image.BufferedImage.TYPE_INT_RGB);
                    Graphics g2 = buffImFinalColor.getGraphics();
                    g2.setColor(Color.white);
                    g2.fillRect(0, 0, 50, 40);
                    g2.setColor(new Color(
                            (float) this.fillColor.getRed() / 255,
                            (float) this.fillColor.getGreen() / 255,
                            (float) this.fillColor.getBlue() / 255,
                            this.fillOpacity));
                    g2.fillRect(0, 0, 50, 40);
                    this.fillFinalColorLabel.setIcon(new ImageIcon(
                            buffImFinalColor));
                }

                if (dialog == this.strokeDialog) {
                    // Getting the color of the dialog
                    this.strokeColor = this.strokeColorChooser.getColor();

                    // Updating the label of the raw color
                    BufferedImage buffImColor = new BufferedImage(100, 30,
                            java.awt.image.BufferedImage.TYPE_INT_RGB);
                    Graphics g = buffImColor.getGraphics();
                    g.setColor(Color.white);
                    g.fillRect(0, 0, 100, 30);
                    g.setColor(this.strokeColor);
                    g.fillRect(0, 0, 100, 30);
                    this.strokeColorLabel.setIcon(new ImageIcon(buffImColor));

                    // Updating the label of the final color (with opacity)
                    BufferedImage buffImFinalColor = new BufferedImage(50, 40,
                            java.awt.image.BufferedImage.TYPE_INT_RGB);
                    Graphics g2 = buffImFinalColor.getGraphics();
                    g2.setColor(Color.white);
                    g2.fillRect(0, 0, 50, 40);
                    g2.setColor(new Color(
                            (float) this.strokeColor.getRed() / 255,
                            (float) this.strokeColor.getGreen() / 255,
                            (float) this.strokeColor.getBlue() / 255,
                            this.strokeOpacity));
                    g2.fillRect(0, 0, 50, 40);
                    this.strokeFinalColorLabel.setIcon(new ImageIcon(
                            buffImFinalColor));
                }
                this.updateLayer();       
            }
        }
        

    }

public void updateLayer() {
    Symbolizer symbolizer = this.layer.getStyles().get(this.key).getSymbolizer();

    //System.out.println("updateLayer");
     if (symbolizer.isLineSymbolizer()) {
        // System.out.println("isLineSymbolizer");
        LineSymbolizer lineSymbolizer = (LineSymbolizer) this.layer
                .getStyles().get(this.key).getSymbolizer();

        lineSymbolizer.getStroke().setColor(this.strokeColor);
        lineSymbolizer.getStroke().setStrokeOpacity(this.strokeOpacity);
        lineSymbolizer.getStroke().setStrokeWidth((float) this.strokeWidth);
        lineSymbolizer.getStroke().setStrokeLineJoin(this.strokeLineJoin);
        lineSymbolizer.getStroke().setStrokeLineCap(this.strokeLineCap);
        lineSymbolizer.setUnitOfMeasure(this.unit);

            this.LineSymbolizer=lineSymbolizer;
    }
     if (symbolizer.isPolygonSymbolizer()) {
     //    System.out.println("isPolygonSymbolizer");
         PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) this.layer
                 .getStyles().get(this.key).getSymbolizer();
      
         polygonSymbolizer.getStroke().setColor(this.strokeColor);
         polygonSymbolizer.getStroke().setStrokeOpacity(this.strokeOpacity);
         polygonSymbolizer.getStroke().setStrokeWidth((float) this.strokeWidth);
         polygonSymbolizer.getStroke().setStrokeLineJoin(this.strokeLineJoin);
         polygonSymbolizer.getStroke().setStrokeLineCap(this.strokeLineCap);
         polygonSymbolizer.setUnitOfMeasure(this.unit);
//         System.out.println( polygonSymbolizer.getStroke().getColor()+" , "+
//                 polygonSymbolizer.getStroke().getStrokeWidth()+","+ polygonSymbolizer.getStroke().getStrokeLineCap()+","
//                         + polygonSymbolizer.getStroke().getStrokeLineJoin());
         this.polygonSymbolizer=polygonSymbolizer;
     } 
     
     if (symbolizer.isPointSymbolizer()) {
         PointSymbolizer pointSymbolizer = (PointSymbolizer) this.layer
                 .getStyles().get(this.key).getSymbolizer();
         Mark mark = pointSymbolizer.getGraphic().getMarks().get(0);
         mark.getStroke().setColor(this.strokeColor);
         mark.getStroke().setStrokeOpacity(this.strokeOpacity);
         mark.getStroke().setStrokeWidth((float) this.strokeWidth);
         mark.getStroke().setStrokeLineJoin(this.strokeLineJoin);
         mark.getStroke().setStrokeLineCap(this.strokeLineCap);
         pointSymbolizer.setUnitOfMeasure(this.unit);
         
         
     }

    this.stylePanel.paintComponent(this.stylePanel.getGraphics());
}

public LayerStylesPanel getStylePanel() {
    return stylePanel;
}

public void setStylePanel(LayerStylesPanel stylePanel) {
    this.stylePanel = stylePanel;
}

public void getDialogElements() {
    if ( this.layer.getStyles().get(this.key).getSymbolizer()
                    .isLineSymbolizer() 
                    || this.layer.getStyles().get(this.key).getSymbolizer().isPolygonSymbolizer() 
                    || this.layer.getStyles().get(this.key).getSymbolizer().isPointSymbolizer()) {
        this.strokeColorLabel = (JLabel) ((JPanel) ((JPanel) ((JPanel) this.strokePanel
                .getComponent(0)).getComponent(0)).getComponent(0))
                .getComponent(1);
        this.strokeFinalColorLabel = (JLabel) ((JPanel) ((JPanel) this.strokePanel
                .getComponent(0)).getComponent(1)).getComponent(1);
        this.strokeOpacitySlider = (JSlider) ((JPanel) ((JPanel) ((JPanel) this.strokePanel
                .getComponent(0)).getComponent(0)).getComponent(1))
                .getComponent(1);
        this.strokeWidthSpinner = (JSpinner) ((JPanel) this.strokePanel
                .getComponent(1)).getComponent(1);
        this.unitMeterRadio = (JRadioButton) ((JPanel) ((JPanel) this.strokePanel
                .getComponent(1)).getComponent(2)).getComponent(1);
        this.unitPixelRadio = (JRadioButton) ((JPanel) ((JPanel) this.strokePanel
                .getComponent(1)).getComponent(2)).getComponent(2);
        this.strokeLineJoinCombo = (JComboBox) ((JPanel) ((JPanel) this.strokePanel
                .getComponent(2)).getComponent(0)).getComponent(0);
        this.strokeLineCapCombo = (JComboBox) ((JPanel) ((JPanel) this.strokePanel
                .getComponent(2)).getComponent(0)).getComponent(1);
        this.strokeSamplePanel = (StrokeSamplePanel) ((JPanel) this.strokePanel
                .getComponent(2)).getComponent(1);
    }

          

}


}

