package fr.ign.cogit.geoxygene.appli;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.Symbolizer;

public class StyleEditionFrame 
					extends JFrame
					implements ActionListener, MouseListener, ChangeListener{
	
	private static final long serialVersionUID = 1L;

	//Main GeOxygene application elements
	private LayerLegendPanel layerLegendPanel;
	private LayerViewPanel layerViewPanel;
	
	/**
	 * The layer which style will be modified.
	 * Element of the SLD of the project 
	 */
	private Layer layer;
	public Layer getLayer(){
		return this.layer;
	}
	
	//Main Dialog Elements
	private JPanel visuPanel;
	private LayerStylesPanel stylePanel;
	
	private JPanel fillPanel;
	private JPanel strokePanel;
	private JPanel strokePanel2;
	private JPanel symbolPanel;
	
	private JButton btnAddStyle;
	private JButton btnValid;
	private JPanel mainPanel;
	
	// Work variables
	private Color fillColor;
	private float fillOpacity;
	
	private Color strokeColor;
	private float strokeOpacity;
	private double strokeWidth;
	private String unit;
	
	private Color strokeColor2;
	private float strokeOpacity2;
	private double strokeWidth2;
	
	private String symbolShape;
	private float symbolSize;

	
	//Dialog Elements
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
	
	private JColorChooser strokeColorChooser2;
	private JDialog strokeDialog2;
	private JLabel strokeColorLabel2;
	private JLabel strokeFinalColorLabel2;
	private JSlider strokeOpacitySlider2;
	private JSpinner strokeWidthSpinner2;
	
	private JComboBox symbolShapeCombo;
	private JSpinner symbolSizeSpinner;
	
	private ImageIcon[] images;
	private String[] symbols = {
			I18N.getString("StyleEditionFrame.Square"), //$NON-NLS-1$
			I18N.getString("StyleEditionFrame.Circle"), //$NON-NLS-1$
			I18N.getString("StyleEditionFrame.Triangle"), //$NON-NLS-1$
			I18N.getString("StyleEditionFrame.Star"),  //$NON-NLS-1$
			I18N.getString("StyleEditionFrame.Cross"),  //$NON-NLS-1$
			I18N.getString("StyleEditionFrame.X")};  //$NON-NLS-1$


	/**
	 * Style Edition Main Frame.
	 * 
	 * @author Charlotte Hoarau
	 * @param layerLegendPanel the layerLegendPanel of the style to be modified.
	 */
	public StyleEditionFrame(LayerLegendPanel layerLegendPanel) {
		this.layerLegendPanel = layerLegendPanel;
		this.layerViewPanel = layerLegendPanel.getLayerViewPanel();

		if (layerLegendPanel.getSelectedLayers().size()==1) {
     		layer = layerLegendPanel.getSelectedLayers().iterator().next();
		}

		if (layer.getSymbolizer().isPolygonSymbolizer()) {
			init_Polygon();
		} else if (layer.getSymbolizer().isLineSymbolizer()) { 
			init_Line();
		} else if (layer.getSymbolizer().isPointSymbolizer()) {
			init_Point();
		}
		
		setTitle(I18N.getString("StyleEditionFrame.StyleEdition"));  //$NON-NLS-1$
		setSize(600, 700);
		setLocation(200,200);
		setAlwaysOnTop(true);
	}
	
	public void init_Polygon(){
		setLayout(new BorderLayout());
		visuPanel = createVisuPanel();
		add(visuPanel, BorderLayout.WEST);
		
		JLabel titleLabel = new JLabel(I18N.getString("StyleEditionFrame.PolygonNewStyle"));  //$NON-NLS-1$
		titleLabel.setFont(new Font("Verdana",Font.ROMAN_BASELINE,24)); //$NON-NLS-1$
		
		fillPanel = new JPanel();
		TitledBorder fillTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
		fillTitleBorder.setTitleColor(Color.blue);
		fillTitleBorder.setTitleFont(new Font("Verdana",Font.BOLD,16)); //$NON-NLS-1$
		fillTitleBorder.setTitle(I18N.getString("StyleEditionFrame.PolygonFill"));  //$NON-NLS-1$
		fillPanel.setBorder(fillTitleBorder);
		fillPanel.setPreferredSize(new Dimension(420, 200));

		fillColor = ((PolygonSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getFill().getFill();
		fillOpacity = ((PolygonSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getFill().getFillOpacity();
		fillPanel.add(createColorPreviewPanel(fillColor, fillOpacity));
		
		strokePanel = new JPanel();
		strokePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		TitledBorder strokeTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
		strokeTitleBorder.setTitleColor(fillTitleBorder.getTitleColor());
		strokeTitleBorder.setTitleFont(fillTitleBorder.getTitleFont());
		strokeTitleBorder.setTitle(I18N.getString("StyleEditionFrame.PolygonStroke"));  //$NON-NLS-1$
		strokePanel.setBorder(strokeTitleBorder);
		strokePanel.setPreferredSize(new Dimension(420, 200));

		strokeColor = ((PolygonSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getStroke().getStroke();
		strokeOpacity = ((PolygonSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getStroke().getStrokeOpacity();
		strokeWidth = ((PolygonSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getStroke().getStrokeWidth();
		unit = ((PolygonSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getUnitOfMeasure();

		strokePanel.add(createColorPreviewPanel(strokeColor, strokeOpacity));
		strokePanel.add(createWidthPanel(strokeWidth, unit));
		
		btnValid = new JButton(I18N.getString("StyleEditionFrame.Apply"));  //$NON-NLS-1$
		btnValid.addActionListener(this);
		btnValid.setBounds(50, 50, 100, 20);
		
		mainPanel = new JPanel();
		mainPanel.add(titleLabel);
		mainPanel.add(fillPanel);
		mainPanel.add(strokePanel);
		mainPanel.add(btnValid);
		add(mainPanel, BorderLayout.CENTER);
		
		pack();pack();
		repaint();
		setVisible(true);
		
		addWindowListener (new WindowAdapter(){
			@Override
			public void windowClosing (WindowEvent e){
				((JFrame)e.getSource()).dispose();
			}
		});
	}
	
	public void init_Line(){
		setLayout(new BorderLayout());
		visuPanel = createVisuPanel();
		add(visuPanel, BorderLayout.WEST);
		
		JLabel titleLabel = new JLabel(I18N.getString("StyleEditionFrame.LineNewStyle"));  //$NON-NLS-1$
		titleLabel.setFont(new Font("Verdana",Font.ROMAN_BASELINE,24)); //$NON-NLS-1$
		
		strokePanel = new JPanel();
		strokePanel.setLayout(new FlowLayout(FlowLayout.LEFT));		
		
		TitledBorder strokeTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
		strokeTitleBorder.setTitleColor(Color.blue);
		strokeTitleBorder.setTitleFont(new Font("Verdana",Font.BOLD,16)); //$NON-NLS-1$
		strokeTitleBorder.setTitle(I18N.getString("StyleEditionFrame.LineStroke"));  //$NON-NLS-1$
		strokePanel.setBorder(strokeTitleBorder);
		strokePanel.setPreferredSize(new Dimension(420, 200));

		strokeColor = ((LineSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getStroke().getStroke();
		strokeOpacity = ((LineSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getStroke().getStrokeOpacity();
		strokeWidth = ((LineSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getStroke().getStrokeWidth();
		unit = ((LineSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getUnitOfMeasure();
		
		strokePanel.add(createColorPreviewPanel(strokeColor, strokeOpacity));
		strokePanel.add(createWidthPanel(strokeWidth, unit));
		
		if(layer.getStyles().size()==2){
			strokePanel2 = new JPanel();
			strokePanel2.setLayout(new FlowLayout(FlowLayout.LEFT));		
			
			TitledBorder strokeTitleBorder2 = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
			strokeTitleBorder2.setTitleColor(Color.blue);
			strokeTitleBorder2.setTitleFont(new Font("Verdana",Font.BOLD,16)); //$NON-NLS-1$
			strokeTitleBorder2.setTitle(I18N.getString("StyleEditionFrame.LineStroke"));  //$NON-NLS-1$ 
			strokePanel2.setBorder(strokeTitleBorder2);
			strokePanel2.setPreferredSize(new Dimension(420, 200));

			strokeColor2 = ((LineSymbolizer)layer.getStyles().get(1)
					.getSymbolizer()).getStroke().getStroke();
			strokeOpacity2 = ((LineSymbolizer)layer.getStyles().get(1)
					.getSymbolizer()).getStroke().getStrokeOpacity();
			strokeWidth2 = ((LineSymbolizer)layer.getStyles().get(1)
					.getSymbolizer()).getStroke().getStrokeWidth();
			
			strokePanel2.add(createColorPreviewPanel(strokeColor2, strokeOpacity2));
			strokePanel2.add(createWidthPanel(strokeWidth2, unit));
		}else{
			strokePanel2 = new JPanel();
			strokePanel2.setVisible(false);
		}
		
		btnAddStyle = new JButton(I18N.getString("StyleEditionFrame.AddAStyle"));  //$NON-NLS-1$
		btnAddStyle.addActionListener(this);
		btnAddStyle.setBounds(50, 50, 100, 20);
		
		btnValid = new JButton(I18N.getString("StyleEditionFrame.Apply"));  //$NON-NLS-1$
		btnValid.addActionListener(this);
		btnValid.setBounds(50, 50, 100, 20);
		
		mainPanel = new JPanel();
		mainPanel.add(titleLabel);
		mainPanel.add(strokePanel2);
		mainPanel.add(strokePanel);
		mainPanel.add(btnAddStyle);
		if(layer.getStyles().size()==2){
			btnAddStyle.setEnabled(false);
		}
		mainPanel.add(btnValid);
		add(mainPanel, BorderLayout.CENTER);
		
		pack();pack();
		repaint();
		setVisible(true);
		
		addWindowListener (new WindowAdapter(){
			@Override
			public void windowClosing (WindowEvent e){
				((JFrame)e.getSource()).dispose();
			}
		});
	}
	
	public void init_Point(){
		setLayout(new BorderLayout());
		visuPanel = createVisuPanel();
		add(visuPanel, BorderLayout.WEST);
		
		JLabel titleLabel = new JLabel(I18N.getString("StyleEditionFrame.PointNewStyle"));  //$NON-NLS-1$
		titleLabel.setFont(new Font("Verdana",Font.ROMAN_BASELINE,24)); //$NON-NLS-1$
		
		fillPanel = new JPanel();
		TitledBorder fillTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
		fillTitleBorder.setTitleColor(Color.blue);
		fillTitleBorder.setTitleFont(new Font("Verdana",Font.BOLD,16)); //$NON-NLS-1$
		fillTitleBorder.setTitle(I18N.getString("StyleEditionFrame.PointFill"));  //$NON-NLS-1$
		fillPanel.setBorder(fillTitleBorder);
		fillPanel.setPreferredSize(new Dimension(420, 180));
		
		fillColor = ((PointSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getGraphic().getMarks().get(0).getFill().getFill();
		fillOpacity = ((PointSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getGraphic().getMarks().get(0).getFill().getFillOpacity();
		fillPanel.add(createColorPreviewPanel(fillColor, fillOpacity));
		
		strokePanel = new JPanel();
		strokePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		TitledBorder strokeTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
		strokeTitleBorder.setTitleColor(fillTitleBorder.getTitleColor());
		strokeTitleBorder.setTitleFont(fillTitleBorder.getTitleFont());
		strokeTitleBorder.setTitle(I18N.getString("StyleEditionFrame.PointStroke"));  //$NON-NLS-1$
		strokePanel.setBorder(strokeTitleBorder);
		strokePanel.setPreferredSize(new Dimension(420, 200));

		strokeColor = ((PointSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getGraphic().getMarks().get(0).getStroke().getStroke();
		strokeOpacity = ((PointSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getGraphic().getMarks().get(0).getStroke().getStrokeOpacity();
		strokeWidth = ((PointSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getGraphic().getMarks().get(0).getStroke().getStrokeWidth();
		unit = ((PointSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getUnitOfMeasure();
		strokePanel.add(createColorPreviewPanel(strokeColor, strokeOpacity));
		strokePanel.add(createWidthPanel(strokeWidth, unit));
		
		symbolPanel = new JPanel();
		symbolPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		TitledBorder symbolTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
		symbolTitleBorder.setTitleColor(fillTitleBorder.getTitleColor());
		symbolTitleBorder.setTitleFont(fillTitleBorder.getTitleFont());
		symbolTitleBorder.setTitle(I18N.getString("StyleEditionFrame.PointSymbol"));  //$NON-NLS-1$
		symbolPanel.setBorder(symbolTitleBorder);
		symbolPanel.setPreferredSize(new Dimension(420, 180));
		
		symbolShape = ((PointSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getGraphic().getMarks().get(0).getWellKnownName();
		symbolSize = ((PointSymbolizer)layer.getStyles().get(0)
				.getSymbolizer()).getGraphic().getSize();
		
		symbolPanel.add(createSymbolPanel(symbolShape));
		symbolPanel.add(createSizePanel(symbolSize));
		
		btnValid = new JButton(I18N.getString("StyleEditionFrame.Apply"));  //$NON-NLS-1$
		btnValid.addActionListener(this);
		btnValid.setBounds(50, 50, 100, 20);
		
		mainPanel = new JPanel();
		mainPanel.add(titleLabel);
		mainPanel.add(fillPanel);
		mainPanel.add(strokePanel);
		mainPanel.add(symbolPanel);
		mainPanel.add(btnValid);
		add(mainPanel, BorderLayout.CENTER);
		
		pack();pack();
		repaint();
		setVisible(true);
		
		addWindowListener (new WindowAdapter(){
			@Override
			public void windowClosing (WindowEvent e){
				((JFrame)e.getSource()).dispose();
			}
		});
	}
	
	/**
	 * This method creates and return the panel showing the style.
	 * @return the panel showing the style.
	 */
	public JPanel createVisuPanel(){
		
		JPanel visuPanel = new JPanel();
		visuPanel.setPreferredSize(new Dimension(150, 600));
		visuPanel.setBackground(Color.white);
		
		stylePanel = new LayerStylesPanel(layer);
		stylePanel.setMaximumSize(new Dimension(60,80));
		stylePanel.setMinimumSize(new Dimension(60, 80));
		stylePanel.setPreferredSize(new Dimension(60,80));
		visuPanel.add(stylePanel);
		
		JLabel explanation = new JLabel("");	 //$NON-NLS-1$	
		visuPanel.add(explanation);
		
		return visuPanel;
	}
	
	/**
	 * This method creates and return the color preview panel.
	 * It contains 3 components :
	 *        - the raw color, 
	 *        - the level of transparency,
	 *        - the final color (raw color + transparency) 
	 * @param c the raw color of the style to be modified.
	 * @param transparency the transparency of the style to be modified.
	 * @return the color preview panel.
	 */
	public JPanel createColorPreviewPanel(Color c, float transparency){
		JPanel paramColorPanel = new JPanel();
		paramColorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		paramColorPanel.setPreferredSize(new Dimension(300,90));
		paramColorPanel.add(createColorPanel(c));
		
		paramColorPanel.add(createOpacityPanel((int)(transparency*100)));
		
		JPanel colorPreviewPanel = new JPanel();
		colorPreviewPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		colorPreviewPanel.add(paramColorPanel);
		colorPreviewPanel.add(createFinalColorPanel(
				new Color(
						(float)c.getRed()/255,
						(float)c.getGreen()/255,
						(float)c.getBlue()/255,
						transparency)));
		
		return colorPreviewPanel;		
	}
	
	/**
	 * This method creates and return the panel of the raw color.
	 * It form a part of the color preview panel.
	 * @param c the raw color of the style to be modified.
	 * @return the panel of the raw color.
	 */
	public JPanel createColorPanel(Color c){
		JLabel lblColor = new JLabel(I18N.getString("StyleEditionFrame.Color"));  //$NON-NLS-1$
		
		BufferedImage buffImColor =
			new BufferedImage(100,30,java.awt.image.BufferedImage.TYPE_INT_RGB);
	
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
	 * This method creates and return the panel of the final color.
	 * It form a part of the color preview panel.
	 * @param c the final color (raw color + transparency) of the style to be modified.
	 * @return the panel of the final color.
	 */
	public JPanel createFinalColorPanel(Color c){
		JPanel finalColorPanel = new JPanel();
		JTextArea lblColor = new JTextArea(I18N.getString("StyleEditionFrame.ColorPreview"));  //$NON-NLS-1$
		lblColor.setBackground(finalColorPanel.getBackground());
		lblColor.setFont(new Font("Arial", Font.BOLD, 12)); //$NON-NLS-1$
		
		BufferedImage buffImColor =
			new BufferedImage(50,40,java.awt.image.BufferedImage.TYPE_INT_RGB);
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
	 * This method creates and return the panel of the opacity.
	 * It form a part of the color preview panel.
	 * @param opacity the opacity of the style to be modified.
	 * @return the panel of the opacity.
	 */
	public JPanel createOpacityPanel(int opacity){
		JLabel lblTransparency = new JLabel(I18N.getString("StyleEditionFrame.Opacity"));  //$NON-NLS-1$

		JSlider slider = new JSlider(
				JSlider.HORIZONTAL, 0, 100, opacity);
		slider.addChangeListener(this);
		
		JPanel transparencyPanel = new JPanel();
		transparencyPanel.add(lblTransparency);
		transparencyPanel.add(slider);
		
		return transparencyPanel;
	}
	
	/**
	 * This method creates and return the panel of the width.
	 * It used for the stroke elements and contains the unit panel.
	 * @param width the width of the stroke of the style to be modified.
	 * @param unit the unit of the style to be modified.
	 * @return the panel of the width.
	 */
	public JPanel createWidthPanel(double width, String unit){
		JLabel lblWidth = new JLabel(I18N.getString("StyleEditionFrame.Width"));  //$NON-NLS-1$

		SpinnerModel model =
	        new SpinnerNumberModel(width, //initial value
	                               0d, //min
	                               1000d, //max
	                               0.5d); 
		JSpinner spinner = new JSpinner(model);
		spinner.addChangeListener(this);

		JPanel widthPanel = new JPanel();
		widthPanel.add(lblWidth);
		widthPanel.add(spinner);
		widthPanel.add(createUnitPanel(unit));
		return widthPanel;
	}
	
	/**
	 * This method creates and return the panel of the shape of a point symbol.
	 * The shape is one of the well known shapes of the SVG standard; so, it can be:
	 *        - a square
	 *        - a circle
	 *        - a triangle
	 *        - a star
	 *        - a cross
	 *        - a X (cross with a rotation of 45°)
	 * @param shape the shape of the style to be modified.
	 * @return the panel of the shape of a point symbol.
	 */
	public JPanel createSymbolPanel(String shape){
		JLabel lblShape = new JLabel(I18N.getString("StyleEditionFrame.Symbol"));  //$NON-NLS-1$
		
		JPanel comboPanel = new JPanel();
		comboPanel.setLayout(new BorderLayout());
		
		int indexShape = 0;
		images = new ImageIcon[symbols.length];
        Integer[] intArray = new Integer[symbols.length];
        for (int i = 0; i < symbols.length; i++) {
            intArray[i] = new Integer(i);
            images[i] = new ImageIcon(this.getClass().getResource(
            		"/images/shapes/" + symbols[i] + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
            if (images[i] != null) {
                images[i].setDescription(symbols[i]);
            }
            if (symbols[i].equalsIgnoreCase(shape)){
            	indexShape = i;
            }
        }
        
		JComboBox comboSymbol = new JComboBox(intArray);
		comboSymbol.setSelectedIndex(indexShape);
		
		ComboBoxRenderer renderer= new ComboBoxRenderer();
        renderer.setPreferredSize(new Dimension(100, 20));
        comboSymbol.setRenderer(renderer);
        comboSymbol.setMaximumRowCount(3);
        comboSymbol.addActionListener(this);
        
        //Lay out the demo.
        comboPanel.add(comboSymbol, BorderLayout.PAGE_START);
        comboPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		
		JPanel symbolPanel = new JPanel();
		symbolPanel.add(lblShape);
		symbolPanel.add(comboPanel);
		
		return symbolPanel;
	}
	
	/**
	 * This method creates and return the panel of the size of a point symbol.
	 * @param size the size of the style to be modified.
	 * @return the panel of the size of a point symbol.
	 */
	public JPanel createSizePanel(float size){
		JLabel lblSize = new JLabel(I18N.getString("StyleEditionFrame.Size"));  //$NON-NLS-1$

		SpinnerModel model =
	        new SpinnerNumberModel(size, //initial value
	                               0f, //min
	                               1000f, //max
	                               0.5f); 
		JSpinner spinner = new JSpinner(model);
		spinner.addChangeListener(this);

		JPanel sizePanel = new JPanel();
		sizePanel.add(lblSize);
		sizePanel.add(spinner);
		
		return sizePanel;
	}
	
	/**
	 * This method creates and return the panel of the unit of the style.
	 * It form a part of the panel of the width and impact the size of a point symbol.
	 * @param unit the unit of the style.
	 * @return the panel of the unit of the style.
	 */
	public JPanel createUnitPanel(String unit){
		JLabel lblDist = new JLabel(I18N.getString("StyleEditionFrame.Unit"));  //$NON-NLS-1$

		JRadioButton meter = new JRadioButton(I18N.getString("StyleEditionFrame.meter"));  //$NON-NLS-1$
		meter.setActionCommand("meter"); //$NON-NLS-1$
		meter.addActionListener(this);
        
		JRadioButton pixel = new JRadioButton(I18N.getString("StyleEditionFrame.pixel"));  //$NON-NLS-1$
		pixel.setActionCommand("pixel"); //$NON-NLS-1$
		pixel.addActionListener(this);
		
		if(unit.equalsIgnoreCase(Symbolizer.METRE)){
			meter.setSelected(true);
		}else if(unit.equalsIgnoreCase(Symbolizer.PIXEL)){
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
	 * Renderer class to display the ComboBox with all the symbols.
	 */
	class ComboBoxRenderer 	extends JLabel
    						implements ListCellRenderer {
		private static final long serialVersionUID = 1L;
		
		public ComboBoxRenderer() {
			setOpaque(true);
			setHorizontalAlignment(LEFT);
			setVerticalAlignment(CENTER);
		}
		
		/**
		 * This method finds the image and text corresponding
		 * to the selected value and returns the label, set up
		 * to display the text and image.
		 */
		public Component getListCellRendererComponent(
				JList list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
			
			//Get the selected index.
			//(The index parameter isn't always valid, so just use the value.)
			int selectedIndex = ((Integer)value).intValue();
			
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			
			//Set the icon and text.
			ImageIcon icon = images[selectedIndex];
			String pet = symbols[selectedIndex];
			setIcon(icon);
			setText(pet);
			setFont(list.getFont());

			return this;
		}
	}

	/**
	 * Main method of the class.
	 * Just used to test the edition frame width different kinds of style.
	 * @param args
	 */
	public static void main(String[] args) {
		StyledLayerDescriptor sld = StyledLayerDescriptor.unmarshall(
				StyleEditionFrame.class.getResource("/sld/BasicStyles.xml").getPath()); //$NON-NLS-1$
		GeOxygeneApplication geoxAppli = new GeOxygeneApplication();
		geoxAppli.getFrame().newProjectFrame();
		LayerLegendPanel layerLegendPanel = 
			geoxAppli.getFrame().getSelectedProjectFrame().getLayerLegendPanel();
		
		Layer layerPoly = layerLegendPanel.getLayerViewPanel().getProjectFrame()
				.createLayer("Polygon", GM_Polygon.class); //$NON-NLS-1$
		layerPoly.setStyles(sld.getLayer("Polygon").getStyles()); //$NON-NLS-1$
		
		Layer layerLine = layerLegendPanel.getLayerViewPanel().getProjectFrame()
				.createLayer("Simple Line", GM_LineString.class); //$NON-NLS-1$
		layerLine.setStyles(sld.getLayer("Basic Line").getStyles()); //$NON-NLS-1$
		
		Layer layerLine2 = layerLegendPanel.getLayerViewPanel().getProjectFrame()
				.createLayer("Double Line", GM_LineString.class); //$NON-NLS-1$
		layerLine2.setStyles(sld.getLayer("Line with contour").getStyles()); //$NON-NLS-1$
		
		Layer layerLine3 = layerLegendPanel.getLayerViewPanel().getProjectFrame()
				.createLayer("Dasharay Line", GM_LineString.class); //$NON-NLS-1$
		layerLine3.setStyles(sld.getLayer("Line Dasharray").getStyles()); //$NON-NLS-1$
		
		Layer layerPoint = layerLegendPanel.getLayerViewPanel().getProjectFrame()
				.createLayer("Point", GM_Point.class); //$NON-NLS-1$
		layerPoint.setStyles(sld.getLayer("Point").getStyles()); //$NON-NLS-1$
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getDialogElements();
		
		//When the user change the unit of a style. 
		if (e.getSource() == unitMeterRadio){
			unit = Symbolizer.METRE;
		}
		if (e.getSource() == unitPixelRadio){
			unit = Symbolizer.PIXEL;
		}
		
		//When the user change the shape of a point symbol.
		if (e.getSource() == this.symbolShapeCombo){
			symbolShape = symbols[symbolShapeCombo.getSelectedIndex()];
		}
		
		//When the user add a style (LineSymbolizer case).
		if(e.getSource() == this.btnAddStyle){
			strokePanel2.setLayout(new FlowLayout(FlowLayout.LEFT));
			TitledBorder strokeTitleBorder = BorderFactory.createTitledBorder(""); //$NON-NLS-1$
			strokeTitleBorder.setTitleColor(((TitledBorder)strokePanel.getBorder()).getTitleColor());
			strokeTitleBorder.setTitleFont(((TitledBorder)strokePanel.getBorder()).getTitleFont());
			strokeTitleBorder.setTitle(I18N.getString("StyleEditionFrame.LineStroke"));  //$NON-NLS-1$
			strokePanel2.setBorder(strokeTitleBorder);
			strokePanel2.setPreferredSize(new Dimension(420, 200));

			StyledLayerDescriptor sld = StyledLayerDescriptor.unmarshall(
					StyleEditionFrame.class.getResource("/sld/BasicStyles.xml").getPath()); //$NON-NLS-1$
			layer.getStyles().add(sld.getLayer("Basic Line").getStyles().get(0)); //$NON-NLS-1$
			
			strokeColor2 = ((LineSymbolizer)layer.getStyles().get(1)
					.getSymbolizer()).getStroke().getStroke();
			strokeOpacity2 = ((LineSymbolizer)layer.getStyles().get(1)
					.getSymbolizer()).getStroke().getStrokeOpacity();
			strokeWidth2 = ((LineSymbolizer)layer.getStyles().get(1)
					.getSymbolizer()).getStroke().getStrokeWidth();
			
			strokePanel2.add(createColorPreviewPanel(strokeColor2, strokeOpacity2));
			strokePanel2.add(createWidthPanel(strokeWidth2, unit));

			strokePanel2.setVisible(true);
			btnAddStyle.setEnabled(false);
		}

		//When the user validate a color in the Color Chooser interface
		if (e.getSource().getClass() != JComboBox.class
				&& e.getSource().getClass() != JRadioButton.class) {
			if (((JButton)e.getSource()).getActionCommand()== "OK") {  //$NON-NLS-1$
				
				JDialog dialog = (JDialog)((JButton)e.getSource())
					.getParent().getParent().getParent().getParent().getParent();
				if(dialog == fillDialog){
					//Getting the color of the dialog
					fillColor = fillColorChooser.getColor();
					
					//Updating the label of the raw color
					BufferedImage buffImColor =
						new BufferedImage(100,30,java.awt.image.BufferedImage.TYPE_INT_RGB);
					Graphics g = buffImColor.getGraphics();
					g.setColor(Color.white);
					g.fillRect(0, 0, 100, 30);
					g.setColor(fillColor);
					g.fillRect(0, 0, 100, 30);
					fillColorLabel.setIcon(new ImageIcon(buffImColor));
					
					//Updating the label of the final color (with opacity)
					BufferedImage buffImFinalColor =
						new BufferedImage(50,40,java.awt.image.BufferedImage.TYPE_INT_RGB);
					Graphics g2 = buffImFinalColor.getGraphics();
					g2.setColor(Color.white);
					g2.fillRect(0, 0, 50, 40);
					g2.setColor(new Color(
							(float)fillColor.getRed()/255,
							(float)fillColor.getGreen()/255,
							(float)fillColor.getBlue()/255,
							fillOpacity));
					g2.fillRect(0, 0, 50, 40);
					fillFinalColorLabel.setIcon(new ImageIcon(buffImFinalColor));
				}
				if(dialog == strokeDialog){
					//Getting the color of the dialog
					strokeColor = strokeColorChooser.getColor();
	
					//Updating the label of the raw color
					BufferedImage buffImColor =
						new BufferedImage(100,30,java.awt.image.BufferedImage.TYPE_INT_RGB);
					Graphics g = buffImColor.getGraphics();
					g.setColor(Color.white);
					g.fillRect(0, 0, 100, 30);
					g.setColor(strokeColor);
					g.fillRect(0, 0, 100, 30);
					strokeColorLabel.setIcon(new ImageIcon(buffImColor));
					
					//Updating the label of the final color (with opacity)
					BufferedImage buffImFinalColor =
						new BufferedImage(50,40,java.awt.image.BufferedImage.TYPE_INT_RGB);
					Graphics g2 = buffImFinalColor.getGraphics();
					g2.setColor(Color.white);
					g2.fillRect(0, 0, 50, 40);
					g2.setColor(new Color(
							(float)strokeColor.getRed()/255,
							(float)strokeColor.getGreen()/255,
							(float)strokeColor.getBlue()/255,
							strokeOpacity));
					g2.fillRect(0, 0, 50, 40);
					strokeFinalColorLabel.setIcon(new ImageIcon(buffImFinalColor));
				}
				if(dialog == strokeDialog2){
					//Getting the color of the dialog
					strokeColor2 = strokeColorChooser2.getColor();
	
					//Updating the label of the raw color
					BufferedImage buffImColor =
						new BufferedImage(100,30,java.awt.image.BufferedImage.TYPE_INT_RGB);
					Graphics g = buffImColor.getGraphics();
					g.setColor(Color.white);
					g.fillRect(0, 0, 100, 30);
					g.setColor(strokeColor2);
					g.fillRect(0, 0, 100, 30);
					strokeColorLabel2.setIcon(new ImageIcon(buffImColor));
					
					//Updating the label of the final color (with opacity)
					BufferedImage buffImFinalColor =
						new BufferedImage(50,40,java.awt.image.BufferedImage.TYPE_INT_RGB);
					Graphics g2 = buffImFinalColor.getGraphics();
					g2.setColor(Color.white);
					g2.fillRect(0, 0, 50, 40);
					g2.setColor(new Color(
							(float)strokeColor2.getRed()/255,
							(float)strokeColor2.getGreen()/255,
							(float)strokeColor2.getBlue()/255,
							strokeOpacity2));
					g2.fillRect(0, 0, 50, 40);
					strokeFinalColorLabel2.setIcon(new ImageIcon(buffImFinalColor));
				}
			}
		}
		
		updateLayer();
		
		//When the user validate the styles in the main interface
		if(e.getSource() == this.btnValid){
			((JFrame)StyleEditionFrame.this).dispose();
			layerLegendPanel.repaint();
			layerViewPanel.repaint();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		getDialogElements();
		
		if(arg0.getSource()==fillColorLabel){
			fillColorChooser = new JColorChooser();
			fillColorChooser.addChooserPanel(new COGITColorChooserPanel());
			fillDialog = JColorChooser.createDialog(
					this, I18N.getString("StyleEditionFrame.PickAColor"), true, fillColorChooser, this, null);  //$NON-NLS-1$
			fillDialog.setVisible(true);
		}
		if(arg0.getSource()==strokeColorLabel){
			strokeColorChooser = new JColorChooser();
			strokeColorChooser.addChooserPanel(new COGITColorChooserPanel());
			strokeDialog = JColorChooser.createDialog(
					this, I18N.getString("StyleEditionFrame.PickAColor"), true, strokeColorChooser, this, null);  //$NON-NLS-1$
			strokeDialog.setVisible(true);
		}
		if(arg0.getSource()==strokeColorLabel2){
			strokeColorChooser2 = new JColorChooser();
			strokeColorChooser2.addChooserPanel(new COGITColorChooserPanel());
			strokeDialog2 = JColorChooser.createDialog(
					this, I18N.getString("StyleEditionFrame.PickAColor"), true, strokeColorChooser2, this, null);  //$NON-NLS-1$
			strokeDialog2.setVisible(true);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		
		getDialogElements();
		
		if(arg0.getSource() == fillOpacitySlider){
			fillOpacity = ((float)fillOpacitySlider.getValue())/100;
			
			//Updating the label of the final color (with opacity)
			BufferedImage buffImFinalColor =
				new BufferedImage(50,40,java.awt.image.BufferedImage.TYPE_INT_RGB);
			Graphics g2 = buffImFinalColor.getGraphics();
			g2.setColor(Color.white);
			g2.fillRect(0, 0, 50, 40);
			g2.setColor(new Color(
					(float)fillColor.getRed()/255,
					(float)fillColor.getGreen()/255,
					(float)fillColor.getBlue()/255,
					fillOpacity));
			g2.fillRect(0, 0, 50, 40);
			fillFinalColorLabel.setIcon(new ImageIcon(buffImFinalColor));
			
			updateLayer();
		}
		if(arg0.getSource() == strokeOpacitySlider){
			strokeOpacity = ((float)strokeOpacitySlider.getValue())/100;
			
			//Updating the label of the final color (with opacity)
			BufferedImage buffImFinalColor =
				new BufferedImage(50,40,java.awt.image.BufferedImage.TYPE_INT_RGB);
			Graphics g2 = buffImFinalColor.getGraphics();
			g2.setColor(Color.white);
			g2.fillRect(0, 0, 50, 40);
			g2.setColor(new Color(
					(float)strokeColor.getRed()/255,
					(float)strokeColor.getGreen()/255,
					(float)strokeColor.getBlue()/255,
					strokeOpacity));
			g2.fillRect(0, 0, 50, 40);
			strokeFinalColorLabel.setIcon(new ImageIcon(buffImFinalColor));
			
			updateLayer();
		}
		
		if(arg0.getSource() == strokeWidthSpinner){
			strokeWidth = ((SpinnerNumberModel)strokeWidthSpinner
					.getModel()).getNumber().doubleValue();
			updateLayer();
		}
		
		if (arg0.getSource() == symbolSizeSpinner){
			symbolSize = ((SpinnerNumberModel)symbolSizeSpinner
					.getModel()).getNumber().floatValue();
			updateLayer();
		}
		if(arg0.getSource() == strokeOpacitySlider2){
			strokeOpacity2 = ((float)strokeOpacitySlider2.getValue())/100;
			
			//Updating the label of the final color (with opacity)
			BufferedImage buffImFinalColor =
				new BufferedImage(50,40,java.awt.image.BufferedImage.TYPE_INT_RGB);
			Graphics g2 = buffImFinalColor.getGraphics();
			g2.setColor(Color.white);
			g2.fillRect(0, 0, 50, 40);
			g2.setColor(new Color(
					(float)strokeColor2.getRed()/255,
					(float)strokeColor2.getGreen()/255,
					(float)strokeColor2.getBlue()/255,
					strokeOpacity2));
			g2.fillRect(0, 0, 50, 40);
			strokeFinalColorLabel2.setIcon(new ImageIcon(buffImFinalColor));
			
			updateLayer();
		}
		
		if(arg0.getSource() == strokeWidthSpinner2){
			strokeWidth2 = ((SpinnerNumberModel)strokeWidthSpinner2
					.getModel()).getNumber().doubleValue();
			
			updateLayer();
		}
	}
	
	public void updateLayer(){
		//Updating the layer style
		if(layer.getStyles().get(0).getSymbolizer().isPolygonSymbolizer()) {
			
			((PolygonSymbolizer)layer.getStyles().get(0).getSymbolizer()).getFill()
			.setColor(fillColor);
	
			((PolygonSymbolizer)layer.getStyles().get(0).getSymbolizer()).getFill()
			.setFillOpacity(fillOpacity);
	
			((PolygonSymbolizer)layer.getStyles().get(0).getSymbolizer()).getStroke()
					.setColor(strokeColor);
			
			((PolygonSymbolizer)layer.getStyles().get(0).getSymbolizer()).getStroke()
					.setStrokeOpacity(strokeOpacity);
			
			((PolygonSymbolizer)layer.getStyles().get(0).getSymbolizer()).getStroke()
					.setStrokeWidth((float)strokeWidth);
			
			((PolygonSymbolizer)layer.getStyles().get(0).getSymbolizer()).setUnitOfMeasure(unit);
		}
		else if(layer.getStyles().get(0).getSymbolizer().isLineSymbolizer()) {
			
			((LineSymbolizer)layer.getStyles().get(0).getSymbolizer()).getStroke()
					.setColor(strokeColor);
			
			((LineSymbolizer)layer.getStyles().get(0).getSymbolizer()).getStroke()
					.setStrokeOpacity(strokeOpacity);
			
			((LineSymbolizer)layer.getStyles().get(0).getSymbolizer()).getStroke()
					.setStrokeWidth((float)strokeWidth);
			
			((LineSymbolizer)layer.getStyles().get(0).getSymbolizer()).setUnitOfMeasure(unit);
			
			if (layer.getStyles().size()==2) {
				((LineSymbolizer)layer.getStyles().get(1).getSymbolizer()).getStroke()
						.setColor(strokeColor2);
				
				((LineSymbolizer)layer.getStyles().get(1).getSymbolizer()).getStroke()
						.setStrokeOpacity(strokeOpacity2);
				
				((LineSymbolizer)layer.getStyles().get(1).getSymbolizer()).getStroke()
						.setStrokeWidth((float)strokeWidth2);
				
				((LineSymbolizer)layer.getStyles().get(1).getSymbolizer()).setUnitOfMeasure(unit);
			}
		}
		else if(layer.getStyles().get(0).getSymbolizer().isPointSymbolizer()) {
			
			((PointSymbolizer)layer.getStyles().get(0).getSymbolizer())
					.getGraphic().getMarks().get(0).getFill()
						.setColor(fillColor);
	
			((PointSymbolizer)layer.getStyles().get(0).getSymbolizer())
					.getGraphic().getMarks().get(0).getFill()
						.setFillOpacity(fillOpacity);
	
			((PointSymbolizer)layer.getStyles().get(0).getSymbolizer())
					.getGraphic().getMarks().get(0).getStroke()
						.setColor(strokeColor);
			
			((PointSymbolizer)layer.getStyles().get(0).getSymbolizer())
					.getGraphic().getMarks().get(0).getStroke()
						.setStrokeOpacity(strokeOpacity);
			
			((PointSymbolizer)layer.getStyles().get(0).getSymbolizer())
					.getGraphic().getMarks().get(0).getStroke()
						.setStrokeWidth((float)strokeWidth);
			
			((PointSymbolizer)layer.getStyles().get(0).getSymbolizer())
					.setUnitOfMeasure(unit);
			
			((PointSymbolizer)layer.getStyles().get(0)
					.getSymbolizer()).getGraphic().getMarks().get(0)
						.setWellKnownName(symbolShape);
			
			((PointSymbolizer)layer.getStyles().get(0)
					.getSymbolizer()).getGraphic().setSize(symbolSize);
		}

		//Updating the preview style panel
		stylePanel.paintComponent(stylePanel.getGraphics());
	}
	
	public void getDialogElements(){
		if(layer.getStyles().get(0).getSymbolizer().isPolygonSymbolizer()
				|| layer.getStyles().get(0).getSymbolizer().isLineSymbolizer()
				|| layer.getStyles().get(0).getSymbolizer().isPointSymbolizer()){
			strokeColorLabel = 
				(JLabel)((JPanel)((JPanel)((JPanel)strokePanel
						.getComponent(0)).getComponent(0)).getComponent(0)).getComponent(1);
			strokeFinalColorLabel = (JLabel)((JPanel)((JPanel)strokePanel
					.getComponent(0)).getComponent(1)).getComponent(1);
			strokeOpacitySlider = (JSlider)((JPanel)((JPanel)((JPanel)strokePanel
					.getComponent(0)).getComponent(0)).getComponent(1)).getComponent(1);
			strokeWidthSpinner = (JSpinner)((JPanel)strokePanel
					.getComponent(1)).getComponent(1);
			unitMeterRadio = (JRadioButton)((JPanel)((JPanel)strokePanel
					.getComponent(1)).getComponent(2)).getComponent(1);
			unitPixelRadio = (JRadioButton)((JPanel)((JPanel)strokePanel
					.getComponent(1)).getComponent(2)).getComponent(2);
		}
		if (layer.getStyles().get(0).getSymbolizer().isLineSymbolizer()
				&&layer.getStyles().size()==2) {
			strokeColorLabel2 = 
				(JLabel)((JPanel)((JPanel)((JPanel)strokePanel2
						.getComponent(0)).getComponent(0)).getComponent(0)).getComponent(1);
			strokeFinalColorLabel2 = (JLabel)((JPanel)((JPanel)strokePanel2
					.getComponent(0)).getComponent(1)).getComponent(1);
			strokeOpacitySlider2 = (JSlider)((JPanel)((JPanel)((JPanel)strokePanel2
					.getComponent(0)).getComponent(0)).getComponent(1)).getComponent(1);
			strokeWidthSpinner2 = (JSpinner)((JPanel)strokePanel2
					.getComponent(1)).getComponent(1);
		}
		if(layer.getStyles().get(0).getSymbolizer().isPolygonSymbolizer()
				|| layer.getStyles().get(0).getSymbolizer().isPointSymbolizer()){
			fillColorLabel = 
				(JLabel)((JPanel)((JPanel)((JPanel)fillPanel
						.getComponent(0)).getComponent(0)).getComponent(0)).getComponent(1);
			fillFinalColorLabel = (JLabel)((JPanel)((JPanel)fillPanel
					.getComponent(0)).getComponent(1)).getComponent(1);
			fillOpacitySlider = (JSlider)((JPanel)((JPanel)((JPanel)fillPanel
					.getComponent(0)).getComponent(0)).getComponent(1)).getComponent(1);
		}
		if(layer.getStyles().get(0).getSymbolizer().isPointSymbolizer()){
			symbolShapeCombo = (JComboBox)((JPanel)((JPanel)symbolPanel
					.getComponent(0)).getComponent(1)).getComponent(0);
			symbolSizeSpinner = (JSpinner)((JPanel)symbolPanel
					.getComponent(1)).getComponent(1);
		}
	}
}
