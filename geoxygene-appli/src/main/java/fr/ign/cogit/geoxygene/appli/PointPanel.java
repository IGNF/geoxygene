package fr.ign.cogit.geoxygene.appli;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LinePlacement;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointPlacement;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.Symbolizer;

public class PointPanel extends JDialog implements ActionListener,
MouseListener, ChangeListener, ItemListener {
    
    private StyleEditionFrame styleEditionFrame;
    private int key;
    private PolygonePanel polygonePanel;
    private Color fillColor;
    private float fillOpacity;
    private JButton addColorMapButton;
    private JButton addCategorizedMapButton;
    private Layer layer;
    private LinePanel linePanel;
    private PointSymbolizer pointSymbolizer;
    private JPanel symbolPanel;
    private String symbolShape;
    private float symbolSize;
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
    private JLabel fillColorLabel;
    private JLabel fillFinalColorLabel;
    private JSlider fillOpacitySlider;
    private JComboBox symbolShapeCombo;
    private JSpinner symbolSizeSpinner;
    private LayerStylesPanel stylePanel;
  
    private static Logger logger = Logger.getLogger(StyleEditionFrame.class
            .getName());
    
    
    public  PointPanel(StyleEditionFrame styleEditionFrame ,PointSymbolizer pointSymbolizer,int key){
        
        this.styleEditionFrame=styleEditionFrame;
        this.stylePanel=styleEditionFrame.getStylePanel();
        this.key=key;
        this.layer=styleEditionFrame.getLayer();
        this.pointSymbolizer=pointSymbolizer;
        
      //  this.fillPanel = new JPanel();
     
        
       this.polygonePanel=new PolygonePanel(this,this.styleEditionFrame, this.pointSymbolizer, this.key);
       
        //**************************************

        this.linePanel=new LinePanel(this,styleEditionFrame,this.pointSymbolizer,key);
        
        //**************************************
        TitledBorder fillTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
        fillTitleBorder.setTitleColor(Color.blue);
        fillTitleBorder.setTitleFont(new Font("Verdana", Font.BOLD, 16)); //$NON-NLS-1$
        fillTitleBorder.setTitle(I18N.getString("StyleEditionFrame.PointFill")); //$NON-NLS-1$
        
        this.symbolPanel = new JPanel();
        this.symbolPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        TitledBorder symbolTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
        symbolTitleBorder.setTitleColor(fillTitleBorder.getTitleColor());
        symbolTitleBorder.setTitleFont(fillTitleBorder.getTitleFont());
        symbolTitleBorder.setTitle(I18N
                .getString("StyleEditionFrame.PointSymbol")); //$NON-NLS-1$
        this.symbolPanel.setBorder(symbolTitleBorder);
        this.symbolPanel.setPreferredSize(new Dimension(420, 180));

        this.symbolShape = pointSymbolizer.getGraphic().getMarks().get(0)
                .getWellKnownName();
        this.symbolSize = pointSymbolizer.getGraphic().getSize();

        this.symbolPanel.add(this.createSymbolPanel(this.symbolShape));
        this.symbolPanel.add(this.createSizePanel(this.symbolSize));
      //**************************************
        
    }
    
    public void addPointPanel(StyleEditionFrame styleEditionFrame, int key) {
        // TODO Auto-generated method stub
    
        
      //  this.strokePanel=new StrokePanel(new JPanel());
       
        
        StyledLayerDescriptor sld;
        try {
            sld = StyledLayerDescriptor
                    .unmarshall(StyledLayerDescriptor.class
                            .getClassLoader().getResourceAsStream(
                                    "sld/BasicStyles.xml"));
            styleEditionFrame.getLayer().getStyles().add(
                    sld.getLayer("Point").getStyles().get(0)); //$NON-NLS-1$
        } catch (JAXBException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } //$NON-NLS-1$
        
        this.styleEditionFrame=styleEditionFrame;
        this.stylePanel=styleEditionFrame.getStylePanel();
        this.key=key;
        this.layer=styleEditionFrame.getLayer();
        this.pointSymbolizer=(PointSymbolizer) this.layer
                .getStyles().get(this.key).getSymbolizer();
        
      //**************************************
        TitledBorder fillTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
        fillTitleBorder.setTitleColor(Color.blue);
        fillTitleBorder.setTitleFont(new Font("Verdana", Font.BOLD, 16)); //$NON-NLS-1$
        fillTitleBorder.setTitle(I18N.getString("StyleEditionFrame.PointFill")); //$NON-NLS-1$
        
        this.symbolPanel = new JPanel();
        this.symbolPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        TitledBorder symbolTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
        symbolTitleBorder.setTitleColor(fillTitleBorder.getTitleColor());
        symbolTitleBorder.setTitleFont(fillTitleBorder.getTitleFont());
        symbolTitleBorder.setTitle(I18N
                .getString("StyleEditionFrame.PointSymbol")); //$NON-NLS-1$
        this.symbolPanel.setBorder(symbolTitleBorder);
        this.symbolPanel.setPreferredSize(new Dimension(420, 180));

        this.symbolShape = pointSymbolizer.getGraphic().getMarks().get(0)
                .getWellKnownName();
        this.symbolSize = pointSymbolizer.getGraphic().getSize();

        this.symbolPanel.add(this.createSymbolPanel(this.symbolShape));
        this.symbolPanel.add(this.createSizePanel(this.symbolSize));
        //ajouter stroke
        this.polygonePanel=new PolygonePanel(this,this.styleEditionFrame, this.pointSymbolizer, this.key);
        //this.polygonePanel.setOpacity(0.0f);
        this.linePanel=new LinePanel(this,styleEditionFrame,this.pointSymbolizer,key);
        
    }
    
    public void updatePointPanel(){
        this.updateLayer();
        this.polygonePanel.updateLayer();
        this.linePanel.updateLayer();
        
    }
    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
        this.polygonePanel.setKey(this.key);
        this.linePanel.setKey(this.key);
    }

    public PolygonePanel getPolygonePanel() {
        return polygonePanel;
    }
    public void setPolygonePanel(PolygonePanel polygonePanel) {
        this.polygonePanel = polygonePanel;
    }
    public PointSymbolizer getPointSymbolizer() {
        return pointSymbolizer;
    }
    public void setPointSymbolizer(PointSymbolizer pointSymbolizer) {
        this.pointSymbolizer = pointSymbolizer;
    }
    public PointPanel(JPanel pointPanel) {
        // TODO Auto-generated constructor stub
        //this.fillPanel=new ;
        this.linePanel = new LinePanel(new JPanel());
        this.symbolPanel=new JPanel();

    }
    /**
     * This method creates and return the panel of the shape of a point symbol.
     * The shape is one of the well known shapes of the SVG standard; so, it can
     * be: - a square - a circle - a triangle - a star - a cross - a X (cross
     * with a rotation of 45°)
     * 
     * @param shape
     *            the shape of the style to be modified.
     * @return the panel of the shape of a point symbol.
     */
    public JPanel createSymbolPanel(String shape) {
        JLabel lblShape = new JLabel(I18N.getString("StyleEditionFrame.Symbol")); //$NON-NLS-1$

        JPanel comboPanel = new JPanel();
        comboPanel.setLayout(new BorderLayout());

        int indexShape = 0;
        this.images = new ImageIcon[this.symbols.length];
        Integer[] intArray = new Integer[this.symbols.length];
        for (int i = 0; i < this.symbols.length; i++) {
            intArray[i] = new Integer(i);
            this.images[i] = new ImageIcon(this.getClass().getResource(
                    "/images/shapes/" + this.symbols[i] + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
            if (this.images[i] != null) {
                this.images[i].setDescription(this.symbols[i]);
            }
            if (this.symbols[i].equalsIgnoreCase(shape)) {
                indexShape = i;
            }
        }

        JComboBox comboSymbol = new JComboBox(intArray);
        comboSymbol.setSelectedIndex(indexShape);

        ComboBoxRenderer renderer = new ComboBoxRenderer();
        renderer.setPreferredSize(new Dimension(200, 20));
        comboSymbol.setRenderer(renderer);
        comboSymbol.setMaximumRowCount(3);
        comboSymbol.addActionListener(this);

        // Lay out the demo.
        comboPanel.add(comboSymbol, BorderLayout.PAGE_START);
        comboPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel symbolPanel = new JPanel();
        symbolPanel.add(lblShape);
        symbolPanel.add(comboPanel);

        return symbolPanel;
    }
 
    public LinePanel getLinePanel() {
        return linePanel;
    }
    public void setLinePanel(LinePanel linePanel) {
        this.linePanel = linePanel;
    }
    public JPanel getSymbolPanel() {
        return symbolPanel;
    }
    public void setSymbolPanel(JPanel symbolPanel) {
        this.symbolPanel = symbolPanel;
    }
    /**
     * Renderer class to display the ComboBox with all the symbols.
     */
    class ComboBoxRenderer extends JLabel implements ListCellRenderer {
        private static final long serialVersionUID = 1L;

        public ComboBoxRenderer() {
            this.setOpaque(true);
            this.setHorizontalAlignment(SwingConstants.LEFT);
            this.setVerticalAlignment(SwingConstants.CENTER);
        }

        /**
         * This method finds the image and text corresponding to the selected
         * value and returns the label, set up to display the text and image.
         */
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {

            // Get the selected index.
            // (The index parameter isn't always valid, so just use the value.)
            int selectedIndex = ((Integer) value).intValue();

            if (isSelected) {
                this.setBackground(list.getSelectionBackground());
                this.setForeground(list.getSelectionForeground());
            } else {
                this.setBackground(list.getBackground());
                this.setForeground(list.getForeground());
            }

            // Set the icon and text.
            ImageIcon icon = PointPanel.this.images[selectedIndex];
            String pet = PointPanel.this.symbolsDescription[selectedIndex];
            this.setIcon(icon);
            this.setText(pet);
            this.setFont(list.getFont());

            return this;
        }
    }

    public StyleEditionFrame getStyleEditionFrame() {
        return styleEditionFrame;
    }

    public void setStyleEditionFrame(StyleEditionFrame styleEditionFrame) {
        this.styleEditionFrame = styleEditionFrame;
    }

    /**
     * This method creates and return the panel of the size of a point symbol.
     * 
     * @param size
     *            the size of the style to be modified.
     * @return the panel of the size of a point symbol.
     */
    public JPanel createSizePanel(float size) {
        JLabel lblSize = new JLabel(I18N.getString("StyleEditionFrame.Size")); //$NON-NLS-1$

        SpinnerModel model = new SpinnerNumberModel(size, // initial value
                0f, // min
                1000f, // max
                0.5f);
        JSpinner spinner = new JSpinner(model);
        spinner.addChangeListener(this);

        JPanel sizePanel = new JPanel();
        sizePanel.add(lblSize);
        sizePanel.add(spinner);

        return sizePanel;
    }

   
    
    
    public void getDialogElements() {

        if (this.pointSymbolizer.isPointSymbolizer()) {
            this.symbolShapeCombo = (JComboBox) ((JPanel) ((JPanel) this.symbolPanel
                    .getComponent(0)).getComponent(1)).getComponent(0);
            this.symbolSizeSpinner = (JSpinner) ((JPanel) this.symbolPanel
                    .getComponent(1)).getComponent(1);
        }
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
    
    public void updateLayer() {
        Symbolizer symbolizer = this.layer.getStyles().get(0).getSymbolizer();
      //  System.out.println("updateLayer");

        if (symbolizer.isPointSymbolizer()) {
            PointSymbolizer pointSymbolizer = (PointSymbolizer) this.layer
                    .getStyles().get(this.key).getSymbolizer();
            Mark mark = pointSymbolizer.getGraphic().getMarks().get(0);
            mark.setWellKnownName(this.symbolShape);
            pointSymbolizer.getGraphic().setSize(this.symbolSize);
            this.pointSymbolizer=pointSymbolizer;
        }

        // Updating the preview style panel
       
        this.stylePanel.paintComponent(this.stylePanel.getGraphics());
    }
    //***************
    //listner 
    
    PointPlacement pointPlacement;
    LinePlacement linePlacement;
    private JColorChooser fillColorChooser;
    private JDialog fillDialog;

    @Override
    public void itemStateChanged(ItemEvent e) {
        this.getDialogElements();
//        if (e.getSource() == this.toponymBtn) {
//            logger.info("toponymBtn");
//            if (e.getStateChange() == ItemEvent.SELECTED) {
//                StyleEditionFrame.this.enableToponyms();
//            } else {
//                StyleEditionFrame.this.disableToponyms();
//            }
//        }
//        if (e.getSource() == this.placements) {
//            logger.info("placements");
//            this.symbolizer
//                    .getLabelPlacement()
//                    .setPlacement(
//                            this.placements.getSelectedIndex() == 0 ? this.pointPlacement
//                                    : this.linePlacement);
//            this.showPlacementParameters(this.placements.getSelectedIndex());
//        }
//        if (e.getSource() == this.repeatBtn) {
//            logger.info("repeatBtn " + e.getStateChange());
//            this.linePlacement.setRepeated(this.repeatBtn.isSelected());
//        }
//      

        this.update();
    }

    @Override
    public void stateChanged(ChangeEvent arg0) {
       // System.out.println("stateChanged");
        this.getDialogElements();

 

      

        if (arg0.getSource() == this.symbolSizeSpinner) {
            this.symbolSize = ((SpinnerNumberModel) this.symbolSizeSpinner
                    .getModel()).getNumber().floatValue();
            this.updateLayer();
        }

    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        this.getDialogElements();
       // System.out.println("mouseClicked");

      
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
       // System.out.println("actionPerformed");
        this.getDialogElements();
 
        // When the user change the shape of a point symbol.
        if (e.getSource() == this.symbolShapeCombo) {
            this.symbolShape = this.symbols[this.symbolShapeCombo
                    .getSelectedIndex()];
            this.updateLayer();
        }

     }

       

   

    
}

    
