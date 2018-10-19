package fr.ign.cogit.geoxygene.appli;

import java.awt.Color;
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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.appli.panel.COGITColorChooserPanel;
import fr.ign.cogit.geoxygene.appli.ui.GenericParameterUI;
import fr.ign.cogit.geoxygene.style.CategorizedMap;
import fr.ign.cogit.geoxygene.style.ColorMap;
import fr.ign.cogit.geoxygene.style.Interpolate;
import fr.ign.cogit.geoxygene.style.InterpolationPoint;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.Symbolizer;

public class PolygonePanel  extends JDialog implements ActionListener,
MouseListener, ChangeListener, ItemListener{

    
    private static final String NO_EXPRESSIVE_TAG = "No expressive description";
    private static Logger logger = Logger.getLogger(StyleEditionFrame.class
            .getName());
    private JPanel fillPanel;
    private Color fillColor;
    private float fillOpacity;
    
    private int key;

    private JButton addColorMapButton;
    private JButton addCategorizedMapButton;
    private JComboBox expressiveStrokeComboBox;
    private LinePanel linePanel;
    private JComboBox expressiveFillComboBox;
    private StyleEditionFrame styleEditionFrame;

    // Dialog Elements
    private JColorChooser fillColorChooser;
    private JDialog fillDialog;
    private JLabel fillColorLabel;
    private JLabel fillFinalColorLabel;
    private JSlider fillOpacitySlider;

    private JColorChooser strokeColorChooser;
    private JDialog strokeDialog;
    private boolean creation=true;
    private PolygonSymbolizer polygonSymbolizer;
    private PointPanel pointPanel;
    private PointSymbolizer pointSymbolizer;
    
    
    public PolygonePanel(StyleEditionFrame styleEditionFrame ,PolygonSymbolizer polygonSymbolizer,int key){
        
        this.styleEditionFrame=styleEditionFrame;
        this.key=key;
        JLabel titleLabel = new JLabel(
                I18N.getString("StyleEditionFrame.PolygonNewStyle")); //$NON-NLS-1$
        titleLabel.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 24)); //$NON-NLS-1$

        this.fillPanel = new JPanel();
        
        
        if (polygonSymbolizer.getFill() != null) {
            
        TitledBorder fillTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
        fillTitleBorder.setTitleColor(Color.blue);
        fillTitleBorder.setTitleFont(new Font("Verdana", Font.BOLD, 16)); //$NON-NLS-1$
        fillTitleBorder.setTitle(I18N
                .getString("StyleEditionFrame.PolygonFill")); //$NON-NLS-1$
        this.fillPanel.setBorder(fillTitleBorder);
        this.fillPanel.setPreferredSize(new Dimension(420, 150));

        PolygonSymbolizer symbolizer = polygonSymbolizer;
        
         
        
        this.fillColor = (symbolizer != null && symbolizer.getFill() != null) ? symbolizer
                .getFill().getFill() : Color.BLACK;
        this.fillOpacity = (symbolizer != null) ? symbolizer.getFill()
                .getFillOpacity() : 0.0f;
                
        
        this.fillPanel.add(this.createColorPreviewPanel(this.fillColor,
                this.fillOpacity));

       
        
        this.addColorMapButton = new JButton(
                I18N.getString("StyleEditionFrame.AddColorMap")); //$NON-NLS-1$
        this.addColorMapButton.addActionListener(this);
        this.fillPanel.add(this.addColorMapButton);

        this.addCategorizedMapButton = new JButton(
                I18N.getString("StyleEditionFrame.AddCategorizedMap")); //$NON-NLS-1$
        this.addCategorizedMapButton.addActionListener(this);
        this.fillPanel.add(this.addCategorizedMapButton);
        
//        this.fillPanel.add(this.getExpressiveFillComboBox()); //TODO add new expressive methods
        this.fillPanel.setAlignmentX(LEFT_ALIGNMENT);
        
       
        } else {
            this.fillPanel.setLayout(null);
            JLabel label=new JLabel();
            label.setText("Fill Vide");
            label.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 36));
            label.setBounds(150, 120, 150, 36);
            this.fillPanel.add(label);
        }
        this.linePanel=new LinePanel(this,styleEditionFrame,polygonSymbolizer,key);
       
    }
    public PolygonePanel(JPanel fillPanel) {
        // TODO Auto-generated constructor stub
        this.fillPanel=fillPanel;
        this.linePanel = new LinePanel(new JPanel());

    }
public PolygonePanel(PointPanel pointPanel,StyleEditionFrame styleEditionFrame ,PointSymbolizer pointSymbolizer,int key){
        
        this.pointPanel=pointPanel;
        this.styleEditionFrame=styleEditionFrame;
        this.key=key;
        JLabel titleLabel = new JLabel(
                I18N.getString("StyleEditionFrame.PolygonNewStyle")); //$NON-NLS-1$
        titleLabel.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 24)); //$NON-NLS-1$

        this.fillPanel = new JPanel();
        this.pointSymbolizer=pointSymbolizer;
        
     
            
        TitledBorder fillTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
        fillTitleBorder.setTitleColor(Color.blue);
        fillTitleBorder.setTitleFont(new Font("Verdana", Font.BOLD, 16)); //$NON-NLS-1$
        fillTitleBorder.setTitle(I18N
                .getString("StyleEditionFrame.PolygonFill")); //$NON-NLS-1$

        this.fillPanel.setBorder(fillTitleBorder);
        this.fillPanel.setPreferredSize(new Dimension(420, 150));

        this.fillColor = pointSymbolizer.getGraphic().getMarks().get(0).getFill()
                .getFill();
        this.fillOpacity = pointSymbolizer.getGraphic().getMarks().get(0).getFill()
                .getFillOpacity();
        this.fillPanel.add(this.createColorPreviewPanel(this.fillColor,
                this.fillOpacity));

        this.addColorMapButton = new JButton(
                I18N.getString("StyleEditionFrame.AddColorMap")); //$NON-NLS-1$
        this.addColorMapButton.addActionListener(this);
        this.fillPanel.add(this.addColorMapButton);

        this.addCategorizedMapButton = new JButton(
                I18N.getString("StyleEditionFrame.AddCategorizedMap")); //$NON-NLS-1$
        this.addCategorizedMapButton.addActionListener(this);
        this.fillPanel.add(this.addCategorizedMapButton);
        
       
         
}
    public void addFillPanel(StyleEditionFrame styleEditionFrame, int key) {
        // TODO Auto-generated method stub
        this.styleEditionFrame=styleEditionFrame;
        this.key=key;
            
       StyledLayerDescriptor sld;
        try {
            sld = StyledLayerDescriptor
                    .unmarshall(StyledLayerDescriptor.class
                            .getClassLoader().getResourceAsStream(
                                    "sld/BasicStyles.xml"));
            styleEditionFrame.getLayer().getStyles().add(
                    sld.getLayer("Polygon").getStyles().get(0)); //$NON-NLS-1$
        } catch (JAXBException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } //$NON-NLS-1$
        
        //ajouter stroke
        this.linePanel.addStrokePanelFill(styleEditionFrame, key);
        //ajouter fill
        JLabel titleLabel = new JLabel(
                I18N.getString("StyleEditionFrame.PolygonNewStyle")); //$NON-NLS-1$
        titleLabel.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 24)); //$NON-NLS-1$

        this.fillPanel = new JPanel();
        TitledBorder fillTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
        fillTitleBorder.setTitleColor(Color.blue);
        fillTitleBorder.setTitleFont(new Font("Verdana", Font.BOLD, 16)); //$NON-NLS-1$
        fillTitleBorder.setTitle(I18N
                .getString("StyleEditionFrame.PolygonFill")); //$NON-NLS-1$
        this.fillPanel.setBorder(fillTitleBorder);
        this.fillPanel.setPreferredSize(new Dimension(420, 150));

        
        
        PolygonSymbolizer symbolizer = (PolygonSymbolizer )styleEditionFrame.getLayer().getStyles().get(this.key).getSymbolizer();
        
        this.fillColor = (symbolizer != null && symbolizer.getFill() != null) ? symbolizer
                .getFill().getFill() : Color.BLACK;
        this.fillOpacity = 0.0f;
        this.fillPanel.add(this.createColorPreviewPanel(this.fillColor,
                this.fillOpacity));

        
        
        this.addColorMapButton = new JButton(
                I18N.getString("StyleEditionFrame.AddColorMap")); //$NON-NLS-1$
        this.addColorMapButton.addActionListener(this);
        this.fillPanel.add(this.addColorMapButton);

        this.addCategorizedMapButton = new JButton(
                I18N.getString("StyleEditionFrame.AddCategorizedMap")); //$NON-NLS-1$
        this.addCategorizedMapButton.addActionListener(this);
        this.fillPanel.add(this.addCategorizedMapButton);
        
        this.fillPanel.add(this.getExpressiveFillComboBox());
        this.fillPanel.setAlignmentX(LEFT_ALIGNMENT);
        this.updateLayer();

    }

    private JComboBox getExpressiveFillComboBox() {
        return null;
//        if (this.expressiveFillComboBox == null) {
//            Vector<XmlElement> descriptors = new Vector<XmlElement>();
//            final String fieldName = "fill2dDescriptor";
//            Class<Fill> fillClass = Fill.class;
//            XmlElement currentSelectedObject = null;
//            try {
//                // Check field class type
//                Field field = fillClass.getField(fieldName);
//                if (!field.getType().equals(Fill2DDescriptor.class)) {
//                    logger.error(fieldName
//                            + " field from "
//                            + fillClass.getSimpleName()
//                            + " is intended to be of type 'Fill2DDescriptor' not "
//                            + field.getType().getSimpleName());
//                    this.expressiveFillComboBox = new JComboBox();
//                    return this.expressiveFillComboBox;
//                }
//                Fill currentFill = ((PolygonSymbolizer) this.styleEditionFrame.getLayer()
//                        .getStyles().get(this.key).getSymbolizer()).getFill();
//                Class<?> currentExpressiveFillClass = currentFill == null ? null
//                        : currentFill.getClass();
//                // get XmlElements annotations
//                for (Annotation annotation : field.getDeclaredAnnotations()) {
//                    if (annotation instanceof XmlElements) {
//                        XmlElements elts = (XmlElements) annotation;
//                        for (XmlElement elt : elts.value()) {
//                            descriptors.add(elt);
//                            if (elt.type().equals(currentExpressiveFillClass)) {
//                                currentSelectedObject = elt;
//                            }
//                        }
//                    } else {
//                        logger.warn(fieldName
//                                + " field from "
//                                + fillClass.getSimpleName()
//                                + " is intended to have only '@XmlElements' annotation. Ignore '@"
//                                + annotation.annotationType().getSimpleName()
//                                + "' annotation");
//                    }
//
//                }
//            } catch (NoSuchFieldException e) {
//                logger.error("Field '" + fieldName
//                        + "' does not exist in class " + fillClass.getName());
//            } catch (SecurityException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            descriptors.add(null);
//            this.expressiveFillComboBox = new JComboBox(descriptors);
//            this.expressiveFillComboBox.setSelectedItem(currentSelectedObject);
//            this.expressiveFillComboBox
//                    .setRenderer(new ListCellRenderer<XmlElement>() {
//                        private final JLabel label = new JLabel();
//
//                        @Override
//                        public Component getListCellRendererComponent(
//                                JList<? extends XmlElement> list,
//                                XmlElement value, int index,
//                                boolean isSelected, boolean cellHasFocus) {
//
//                            this.label
//                                    .setText(value == null ? NO_EXPRESSIVE_TAG
//                                            : value.name());
//                            return this.label;
//                        }
//                    });
//            this.expressiveFillComboBox.addActionListener(new ActionListener() {
//
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    XmlElement elt = (XmlElement) PolygonePanel.this.expressiveFillComboBox
//                            .getSelectedItem();
//                    Fill2DDescriptor expressiveFill;
//                    try {
//                        expressiveFill = elt == null ? null
//                                : (Fill2DDescriptor) elt.type().newInstance();
//                        // Updating the layer style
//                        PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) PolygonePanel.this.styleEditionFrame.getLayer()
//                                .getStyles().get(PolygonePanel.this.key).getSymbolizer();
////                        polygonSymbolizer.getFill().setFill2DDescriptor(
////                                expressiveFill);
//                        PolygonePanel.this.updateExpressivePanel();
//                    } catch (InstantiationException e1) {
//                        e1.printStackTrace();
//                    } catch (IllegalAccessException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//
//            });
//        }
//        return this.expressiveFillComboBox;
    }
    public void updateExpressivePanel() {
        this.addOrReplaceExpressiveRenderingUI();
    }
    private void addOrReplaceExpressiveRenderingUI() {
       // System.out.println("addOrReplaceExpressiveRenderingUI");
        if (this.styleEditionFrame.getTabPane() == null) {
            throw new IllegalStateException(
                    "this method can only be called after tabPane creation");
        }
        String tabTooltip = "";
        this.styleEditionFrame.getTabPane().remove(this.styleEditionFrame.getFillExpressiveScrollPane());
        this.styleEditionFrame.getTabPane().remove(this.styleEditionFrame.getStrokeExpressiveScrollPane());

        GenericParameterUI ui = null;      
        // Fill expressive UI
//        if ( this.styleEditionFrame.getLayer().getStyles().get(this.key).getSymbolizer().isPolygonSymbolizer()
//                && ((PolygonSymbolizer)this.styleEditionFrame.getLayer().getStyles().get(this.key).getSymbolizer()).getFill() !=null
//                && ((PolygonSymbolizer) (this.styleEditionFrame.getLayer().getStyles().get(this.key).getSymbolizer())).getFill()
//                        .getFill2DDescriptor() != null) {
//            PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) this.styleEditionFrame.getLayer().getStyles().get(this.key)
//                    .getSymbolizer();
//            Fill2DDescriptor expressiveFill = polygonSymbolizer.getFill()
//                    .getFill2DDescriptor();
//            if (expressiveFill != null) {
//                ui = ExpressiveRenderingUIFactory.getExpressiveRenderingUI(
//                        expressiveFill, this.styleEditionFrame.getLayerViewPanel().getProjectFrame());
//                tabTooltip = expressiveFill.getClass().getSimpleName();
//            }
//        }
        if (ui != null) {
            this.styleEditionFrame.getFillExpressiveScrollPane().setViewportView(ui.getGui());
            this.styleEditionFrame.getTabPane().addTab("Expressive Fill", null,
            this.styleEditionFrame.getFillExpressiveScrollPane(), tabTooltip);
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
    @Override
    public void itemStateChanged(ItemEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stateChanged(ChangeEvent arg0) {

        this.getDialogElements();

        if (arg0.getSource() == this.fillOpacitySlider) {
            this.fillOpacity = ((float) this.fillOpacitySlider.getValue()) / 100;
    
            // Updating the label of the final color (with opacity)
            BufferedImage buffImFinalColor = new BufferedImage(50, 40,
                    java.awt.image.BufferedImage.TYPE_INT_RGB);
            Graphics g2 = buffImFinalColor.getGraphics();
            g2.setColor(Color.white);
            g2.fillRect(0, 0, 50, 40);
            g2.setColor(new Color((float) this.fillColor.getRed() / 255,
                    (float) this.fillColor.getGreen() / 255,
                    (float) this.fillColor.getBlue() / 255, this.fillOpacity));
            g2.fillRect(0, 0, 50, 40);
            this.fillFinalColorLabel.setIcon(new ImageIcon(buffImFinalColor));
    
            this.updateLayer();
        }
     

    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        this.getDialogElements();
    
        if (arg0.getSource() == this.fillColorLabel) {
            this.fillColorChooser = new JColorChooser();
            this.fillColorChooser.addChooserPanel(new COGITColorChooserPanel());
            this.fillDialog = JColorChooser.createDialog(this,
                    I18N.getString("StyleEditionFrame.PickAColor"), //$NON-NLS-1$
                    true, this.fillColorChooser, this, null);
            this.fillDialog.setVisible(true);
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

    public void updatePolygonePanel(){
        this.updateLayer();
        this.linePanel.updateLayer();
        
    }
    public JPanel getFillPanel() {
        return fillPanel;
    }
    public void setFillPanel(JPanel fillPanel) {
        this.fillPanel = fillPanel;
    }
    public Color getFillColor() {
        return fillColor;
    }
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }
    public float getFillOpacity() {
        return fillOpacity;
    }
    public void setFillOpacity(float fillOpacity) {
        this.fillOpacity = fillOpacity;
    }
    public int getKey() {
        return key;
    }
    public void setKey(int key) {
        this.key = key;
        if(this.getLinePanel()!=null)
            this.getLinePanel().setKey(this.key);
    }
    public JButton getAddColorMapButton() {
        return addColorMapButton;
    }
    public void setAddColorMapButton(JButton addColorMapButton) {
        this.addColorMapButton = addColorMapButton;
    }
    public JButton getAddCategorizedMapButton() {
        return addCategorizedMapButton;
    }
    public void setAddCategorizedMapButton(JButton addCategorizedMapButton) {
        this.addCategorizedMapButton = addCategorizedMapButton;
    }
    public JComboBox getExpressiveStrokeComboBox() {
        return expressiveStrokeComboBox;
    }
    public void setExpressiveStrokeComboBox(JComboBox expressiveStrokeComboBox) {
        this.expressiveStrokeComboBox = expressiveStrokeComboBox;
    }
    public LinePanel getLinePanel() {
        return this.linePanel;
    }
    public void setStrokePanel(LinePanel strokePanel) {
        this.linePanel = strokePanel;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        this.getDialogElements();

        if (e.getSource() == this.addColorMapButton) {

            List<GF_AttributeType> attributes = this.styleEditionFrame.getLayer()
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
                for (IFeature f : this.styleEditionFrame.getLayer().getFeatureCollection()) {
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
                if ( this.styleEditionFrame.getLayer().getSymbolizer().isPolygonSymbolizer()) {
                    PolygonSymbolizer ps = (PolygonSymbolizer)  this.styleEditionFrame.getLayer().getStyles().get(this.key)
                            .getSymbolizer();
                    ps.setColorMap(cm);
                  //  ps.setFill(null);
                    //**********************
//                    this.fillPanel.removeAll();
//                    this.fillPanel.setLayout(null);
//                    JLabel label=new JLabel();
//                    label.setText("Fill Vide");
//                    label.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 36));
//                    label.setBounds(150, 120, 150, 36);
//                    this.fillPanel.add(label);
//                    this.fillPanel.validate();
//                    this.fillPanel.repaint();
//                    
                    //*********************
                    return;
                }
                if (this.styleEditionFrame.getLayer().getSymbolizer().isPointSymbolizer()) {
                    PointSymbolizer pts = (PointSymbolizer) this.styleEditionFrame.getLayer().getStyles().get(this.key)
                            .getSymbolizer();
                    pts.setColorMap(cm);
                    return;
                }

            }
            return;
        }
        if (e.getSource() == this.addCategorizedMapButton) {
            List<GF_AttributeType> attributes = this.styleEditionFrame.getLayer()
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
                if (this.styleEditionFrame.getLayer().getSymbolizer().isPolygonSymbolizer()) {
                    PolygonSymbolizer ps = (PolygonSymbolizer) this.styleEditionFrame.getLayer().getStyles().get(this.key)
                            .getSymbolizer();
                    ps.setCategorizedMap(categorizedMap);
//                    ps.setFill(null);
//                    //**********************
//                    this.fillPanel.removeAll();
//                    this.fillPanel.setLayout(null);
//                    JLabel label=new JLabel();
//                    label.setText("Fill Vide");
//                    label.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 36));
//                    label.setBounds(150, 120, 150, 36);
//                    this.fillPanel.add(label);
//                    this.fillPanel.validate();
//                    this.fillPanel.repaint();
//                    
//                    //*********************
                    return;
                    
                } 
                if (this.styleEditionFrame.getLayer().getSymbolizer().isPointSymbolizer()) {
                    PointSymbolizer pts = (PointSymbolizer) this.styleEditionFrame.getLayer().getStyles().get(this.key)
                            .getSymbolizer();
                    pts.setCategorizedMap(categorizedMap);
                    return;
                }
            }
           
            return;
        }
   

//        // When the user change the shape of a point symbol.
//         if (e.getSource() == this.symbolShapeCombo) {
//         this.symbolShape = this.symbols[this.symbolShapeCombo
//         .getSelectedIndex()];
//         }

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

                this.updateLayer();
                
            }
        }
        

    }
    public void getDialogElements() {
      

        if (this.styleEditionFrame.getLayer().getStyles().get(this.key).getSymbolizer().isPolygonSymbolizer()
                || this.styleEditionFrame.getLayer().getStyles().get(this.key).getSymbolizer().isPointSymbolizer()) {
            this.fillColorLabel = (JLabel) ((JPanel) ((JPanel) ((JPanel) this.fillPanel
                    .getComponent(0)).getComponent(0)).getComponent(0))
                    .getComponent(1);
            this.fillFinalColorLabel = (JLabel) ((JPanel) ((JPanel) this.fillPanel
                    .getComponent(0)).getComponent(1)).getComponent(1);
            this.fillOpacitySlider = (JSlider) ((JPanel) ((JPanel) ((JPanel) this.fillPanel
                    .getComponent(0)).getComponent(0)).getComponent(1))
                    .getComponent(1);
        }
       
    }

    public void updateLayer() {
        Symbolizer symbolizer = this.styleEditionFrame.getLayer().getStyles().get(this.key).getSymbolizer();

        // Updating the layer style
        if (symbolizer.isPolygonSymbolizer()) {
                
            PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) this.styleEditionFrame.getLayer()
                    .getStyles().get(this.key).getSymbolizer();
            if (polygonSymbolizer.getFill() != null) {
                polygonSymbolizer.getFill().setColor(this.fillColor);
                polygonSymbolizer.getFill().setFillOpacity(this.fillOpacity);
            }
            this.linePanel.setPolygonSymbolizer(polygonSymbolizer);
            

        } 
       
        // Updating the preview style panel
//        this.stylePanel.paintComponent(this.stylePanel.getGraphics());
        if (symbolizer.isPointSymbolizer()) {
            PointSymbolizer pointSymbolizer = (PointSymbolizer) this.styleEditionFrame.getLayer()
                    .getStyles().get(this.key).getSymbolizer();
            Mark mark = pointSymbolizer.getGraphic().getMarks().get(0);
            mark.getFill().setColor(this.fillColor);
            mark.getFill().setFillOpacity(this.fillOpacity);
            this.pointPanel.setPointSymbolizer(pointSymbolizer);
            
        }
           
        this.styleEditionFrame.getStylePanel().paintComponent(this.styleEditionFrame.getStylePanel().getGraphics());
    }

    
}
