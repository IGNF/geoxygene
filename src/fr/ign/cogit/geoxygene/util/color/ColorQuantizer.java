/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package fr.ign.cogit.geoxygene.util.color;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

/**
 * @author Julien Perret
 *
 */
public class ColorQuantizer extends JFrame implements ActionListener, MouseListener, MouseWheelListener {
    private static final long serialVersionUID = -4931445688313908737L;
    static Logger logger=Logger.getLogger(ColorQuantizer.class.getName());

    File imageFile;

    JPanel group = new JPanel();

    JComboBox colorSpaceList;
    JComboBox algorithmList;
    JComboBox weightList;
    JComboBox minimumSpanningTreeAlgorithmList;
    JComboBox distanceClusterList;
    JComboBox distanceColorList;

    JSpinner numberOfColorsOctreeSpinner;
    JSpinner finalNumberOfColorsSpinner;

    JButton openButton = new JButton("Open an image");
    JButton computeButton = new JButton("Compute");

    BufferedImage image;

    private JLabel imageLabel = new JLabel();
    private ImageIcon currentIcon = null;
    private JToolBar buttonBar = new JToolBar();
    private MissingIcon placeholderIcon = new MissingIcon();

    /**
     * List of all the descriptions of the image files. These correspond one to
     * one with the image file names
     */
    private String[] imageCaptions = null;

    /**
     * List of all the image files to load.
     */
    private String[] imageFileNames = null;

    /**
     * 
     */
    public ColorQuantizer() {
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setTitle("Palette creator");

	openButton.setMnemonic(KeyEvent.VK_O);
	openButton.setActionCommand("Open");
	openButton.addActionListener(this);
	imageLabel = new JLabel(new MissingIcon(800,600),JLabel.CENTER);
	//imageLabel.setSize(400, 300);
	//imageLabel.setMinimumSize(new Dimension(400,300));
	imageLabel.setVerticalTextPosition(JLabel.BOTTOM);
	imageLabel.setHorizontalTextPosition(JLabel.CENTER);
	imageLabel.setHorizontalAlignment(JLabel.CENTER);
	imageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	buttonBar.add(Box.createGlue());
	buttonBar.add(Box.createGlue());

	String[] colorSpaceStrings = { "RGB","LAB","XYZ" };
	colorSpaceList = new JComboBox(colorSpaceStrings);
	colorSpaceList.setSelectedIndex(0);
	colorSpaceList.addActionListener(this);

	String[] algorithmStrings = { "K-Means" , "Octree" , "Octree+Hierarchical Clustering" };
	algorithmList = new JComboBox(algorithmStrings);
	algorithmList.setSelectedIndex(0);
	algorithmList.addActionListener(this);

	String[] weightStrings = { "UNIFORM","AB","LAABB","RGGB" };
	weightList = new JComboBox(weightStrings);
	weightList.setSelectedIndex(0);
	weightList.addActionListener(this);

	String[] minimumSpanningTreeAlgorithmStrings = { "Kruskal","Prim" };
	minimumSpanningTreeAlgorithmList = new JComboBox(minimumSpanningTreeAlgorithmStrings);
	minimumSpanningTreeAlgorithmList.setSelectedIndex(0);
	minimumSpanningTreeAlgorithmList.addActionListener(this);

	String[] distanceStrings = { "Min","Max", "Avg" };
	distanceClusterList = new JComboBox(distanceStrings);
	distanceClusterList.setSelectedIndex(0);
	distanceClusterList.addActionListener(this);

	distanceColorList = new JComboBox(distanceStrings);
	distanceColorList.setSelectedIndex(0);
	distanceColorList.addActionListener(this);

	SpinnerModel model = new SpinnerNumberModel(64, //initial value
		1, //min
		128, //max
		1);       
	numberOfColorsOctreeSpinner = new JSpinner(model);
	model = new SpinnerNumberModel(16, //initial value
		1, //min
		128, //max
		1);       
	finalNumberOfColorsSpinner = new JSpinner(model);

	computeButton.setMnemonic(KeyEvent.VK_C);
	computeButton.setActionCommand("Compute");
	computeButton.addActionListener(this);
	computeButton.setEnabled(false);

	//setLayout(new BorderLayout());
	initializeGroup();

	add(group, BorderLayout.PAGE_START);
	add(buttonBar, BorderLayout.PAGE_END);
	add(imageLabel, BorderLayout.CENTER);

	//setSize(400, 300);
	// this centers the frame on the screen
	setLocationRelativeTo(null);
	this.addMouseListener(this);
	this.addMouseWheelListener(this);
	pack();

    }

    /**
     * 
     */
    private void initializeGroup() {
	group.removeAll();
	group.add(openButton,BorderLayout.PAGE_START);
	group.add(Box.createHorizontalGlue());
	group.add(colorSpaceList,BorderLayout.PAGE_START);
	group.add(algorithmList, BorderLayout.PAGE_START);
	if (algorithmList.getSelectedIndex()>1) {
	    group.add(weightList,BorderLayout.PAGE_START);
	    group.add(minimumSpanningTreeAlgorithmList,BorderLayout.PAGE_START);
	    group.add(distanceClusterList, BorderLayout.PAGE_START);
	    group.add(distanceColorList, BorderLayout.PAGE_START);
	    group.add(numberOfColorsOctreeSpinner,BorderLayout.PAGE_START);
	}
	group.add(finalNumberOfColorsSpinner,BorderLayout.PAGE_START);
	group.add(computeButton,BorderLayout.PAGE_START);
	pack();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
	if ("Open".equals(e.getActionCommand())) {open();}
	if ("Compute".equals(e.getActionCommand())) {compute();}
	if (e.getSource().equals(algorithmList)) initializeGroup();
	pack();
    }

    /**
     * 
     */
    private void compute() {
	if (image==null) {
	    logger.info("No image");
	    return;
	}

	int finalNumberOfColors = (Integer) finalNumberOfColorsSpinner.getValue();

	String baseDirectoryPath = imageFile.getAbsolutePath().substring(0, imageFile.getAbsolutePath().lastIndexOf("."));
	File directory = new File(baseDirectoryPath);
	directory.mkdir();

	Set<Color> initialColors = ColorUtil.getColors(image);

	ColorUtil.setColorSpace(colorSpaceList.getSelectedIndex());
    ColorUtil.setWeights(weightList.getSelectedIndex());
	SingleLinkageHierarchicalClusterer.setMinimumSpanningTreeAlgorithm(minimumSpanningTreeAlgorithmList.getSelectedIndex());
	SingleLinkageHierarchicalClusterer.setDistanceClusterCluster(distanceClusterList.getSelectedIndex());
	SingleLinkageHierarchicalClusterer.setDistanceColorCluster(distanceColorList.getSelectedIndex());
	
	if (algorithmList.getSelectedIndex()>0) {
	    if (algorithmList.getSelectedIndex()>1) {
		int numberOfColorsOctree = (Integer) numberOfColorsOctreeSpinner.getValue();

		String imageDirectory = baseDirectoryPath+
		"/Octree_"+numberOfColorsOctree+
		"_HierarchicalClustering_"+minimumSpanningTreeAlgorithmList.getSelectedItem()+"_"+finalNumberOfColors+"_"+
		colorSpaceList.getSelectedItem()+"_"+weightList.getSelectedItem()+"_"+distanceClusterList.getSelectedItem()+"_"+distanceColorList.getSelectedItem();
		directory = new File(imageDirectory);
		directory.mkdir();

		String initialChromaticityImageName = imageDirectory+"/chromaticity_"+initialColors.size()+".png";
		String octreeChromaticityImageName = imageDirectory+"/chromaticity_"+numberOfColorsOctree+".png";
		String finalChromaticityImageName = imageDirectory+"/chromaticity_"+finalNumberOfColors+".png";
		String paletteImageName = imageDirectory+"/palette_"+numberOfColorsOctree+".png";
		String proportionalPaletteImageName = imageDirectory+"/prop_palette_"+numberOfColorsOctree+".png";
		String remappedImageName = imageDirectory+"/remapped_"+numberOfColorsOctree+".png";
		String octreeImageName = imageDirectory+"/octree_"+numberOfColorsOctree+".png";
		String hierarchyImageName = imageDirectory+"/hierarchy_"+numberOfColorsOctree+".png";
		String hierarchyPaletteImageName = imageDirectory+"/palette_hierarchy_"+numberOfColorsOctree+".png";
		String hierarchyClusterImageName = imageDirectory+"/hierarchy_cluster_"+numberOfColorsOctree+".png";
		String finalHierarchyImageName = imageDirectory+"/hierarchy_"+finalNumberOfColors+".png";
		String finalHierarchyPaletteImageName = imageDirectory+"/palette_"+finalNumberOfColors+".png";
		String finalHierarchyClusterImageName = imageDirectory+"/hierarchy_cluster_"+finalNumberOfColors+".png";
		String finalHierarchyRemappedImageName = imageDirectory+"/remapped_"+finalNumberOfColors+".png";

		/**
		 * List of all the image files to load.
		 */
		imageFileNames = new String[15];
		imageCaptions = new String[15];

		imageFileNames[0] = imageFile.getAbsolutePath();
		imageCaptions[0] = "Original Image";
		imageFileNames[1] = finalHierarchyPaletteImageName;
		imageCaptions[1] = "Final Palette";
		imageFileNames[2] = paletteImageName;
		imageCaptions[2] = "Octree Palette";
		imageFileNames[3] = hierarchyPaletteImageName;
		imageCaptions[3] = "Hierarchy Palette";
		imageFileNames[4] = proportionalPaletteImageName;
		imageCaptions[4] = "Proportional Palette";
		imageFileNames[5] = finalHierarchyClusterImageName;
		imageCaptions[5] = "Final Hierarchy in Chromaticity Diagram";
		imageFileNames[6] = hierarchyClusterImageName;
		imageCaptions[6] = "Initial Hierarchy in Chromaticity Diagram";
		imageFileNames[7] = finalHierarchyImageName;
		imageCaptions[7] = "Final Hierarchy in Cercle";
		imageFileNames[8] = hierarchyImageName;
		imageCaptions[8] = "Initial Hierarchy in Cercle";
		imageFileNames[9] = initialChromaticityImageName;
		imageCaptions[9] = "Initial Chromaticity Diagram";
		imageFileNames[10] = octreeChromaticityImageName;
		imageCaptions[10] = "Octree Chromaticity Diagram";
		imageFileNames[11] = finalChromaticityImageName;
		imageCaptions[11] = "Final Chromaticity Diagram";
		imageFileNames[12] = octreeImageName;
		imageCaptions[12] = "Octree";
		imageFileNames[13] = finalHierarchyRemappedImageName;
		imageCaptions[13] = "Final Remapped Image";
		imageFileNames[14] = remappedImageName;
		imageCaptions[14] = "Octree Remapped Image";

		ColorUtil.writeChromaticityImage(initialColors, 512, 3,initialChromaticityImageName);

		OctreeQuantizer quantization = new OctreeQuantizer(image,numberOfColorsOctree);
		ColorUtil.writePaletteImage(quantization.getColorLookUpTable(),50,paletteImageName);
		ColorUtil.writeProportionalPaletteImage(quantization.getRemappedImage(),proportionalPaletteImageName);
		ColorUtil.writeImage(quantization.getRemappedImage(), remappedImageName);
		quantization.writeOctreeImage(100, octreeImageName);

		ColorUtil.writeChromaticityImage(Arrays.asList(quantization.getColorLookUpTable()), 512, 8,octreeChromaticityImageName);

		SingleLinkageHierarchicalClusterer graph = new SingleLinkageHierarchicalClusterer(quantization.getColorLookUpTable(),numberOfColorsOctree);
		SingleLinkageHierarchicalClusterer.writeSingleLinkageHierarchicalClustering(graph, hierarchyImageName);
		SingleLinkageHierarchicalClusterer.writeClusterImage(graph, 512, 8, hierarchyClusterImageName);
		ColorUtil.writePaletteImage(graph.getColors(),50,hierarchyPaletteImageName);

		graph = new SingleLinkageHierarchicalClusterer(quantization.getColorLookUpTable(),finalNumberOfColors);
		SingleLinkageHierarchicalClusterer.writeSingleLinkageHierarchicalClustering(graph, finalHierarchyImageName);
		SingleLinkageHierarchicalClusterer.writeClusterImage(graph, 512, 8, finalHierarchyClusterImageName);
		ColorUtil.writePaletteImage(graph.getColors(),50,finalHierarchyPaletteImageName);

		ColorUtil.writeChromaticityImage(Arrays.asList(graph.getColors()), 512, 16, finalChromaticityImageName);

		quantization.setColors(graph.getColors());
		quantization.reMap(image);
		ColorUtil.writeImage(quantization.getRemappedImage(), finalHierarchyRemappedImageName);

	    } else {
		String imageDirectory = baseDirectoryPath+"/Octree_"+finalNumberOfColors;
		directory = new File(imageDirectory);
		directory.mkdir();

		String initialChromaticityImageName = imageDirectory+"/chromaticity_"+initialColors.size()+".png";
		String finalChromaticityImageName = imageDirectory+"/chromaticity_"+finalNumberOfColors+".png";
		String paletteImageName = imageDirectory+"/palette_"+finalNumberOfColors+".png";
		String proportionalPaletteImageName = imageDirectory+"/prop_palette_"+finalNumberOfColors+".png";
		String remappedImageName = imageDirectory+"/remapped_"+finalNumberOfColors+".png";
		String octreeImageName = imageDirectory+"/octree_"+finalNumberOfColors+".png";
		String graphClusterImageName = imageDirectory+"/graph_cluster_"+finalNumberOfColors+".png";
		//String graphImageName = imageDirectory+"/graph_"+finalNumberOfColors+".png";

		/**
		 * List of all the image files to load.
		 */
		imageFileNames = new String[8];
		imageCaptions = new String[8];

		imageFileNames[0] = imageFile.getAbsolutePath();
		imageCaptions[0] = "Original Image";
		imageFileNames[1] = paletteImageName;
		imageCaptions[1] = "Final Palette";
		imageFileNames[2] = proportionalPaletteImageName;
		imageCaptions[2] = "Proportional Palette";
		imageFileNames[3] = initialChromaticityImageName;
		imageCaptions[3] = "Initial Chromaticity Diagram";
		imageFileNames[4] = finalChromaticityImageName;
		imageCaptions[4] = "Final Chromaticity Diagram";
		imageFileNames[5] = octreeImageName;
		imageCaptions[5] = "Octree";
		imageFileNames[6] = remappedImageName;
		imageCaptions[6] = "Final Remapped Image";
		imageFileNames[7] = graphClusterImageName;
		imageCaptions[7] = "Graph Cluster Image";

		ColorUtil.writeChromaticityImage(initialColors, 512, 3,initialChromaticityImageName);

		OctreeQuantizer quantization = new OctreeQuantizer(image,finalNumberOfColors);
		ColorUtil.writeChromaticityImage(Arrays.asList(quantization.getColorLookUpTable()), 512, 8,finalChromaticityImageName);
		ColorUtil.writePaletteImage(quantization.getColorLookUpTable(),50,paletteImageName);
		ColorUtil.writeProportionalPaletteImage(quantization.getRemappedImage(),proportionalPaletteImageName);
		ColorUtil.writeImage(quantization.getRemappedImage(), remappedImageName);
		quantization.writeOctreeImage(100, octreeImageName);
		ImageColorClusterGraph graph = new ImageColorClusterGraph(quantization.getRemappedImage());
		ColorUtil.writeImage(graph.buildGraphClusterImage(), graphClusterImageName);
		//ColorUtil.writeImage(graph.buildGraphImage(), graphImageName);

	    }
	} else {
	    String imageDirectory = baseDirectoryPath+"/KMeans_"+finalNumberOfColors+"_"+colorSpaceList.getSelectedItem();
	    directory = new File(imageDirectory);
	    directory.mkdir();

	    String initialChromaticityImageName = imageDirectory+"/chromaticity_"+initialColors.size()+".png";
	    String finalChromaticityImageName = imageDirectory+"/chromaticity_"+finalNumberOfColors+".png";
	    String paletteImageName = imageDirectory+"/palette_"+finalNumberOfColors+".png";
	    String remappedImageName = imageDirectory+"/remapped_"+finalNumberOfColors+".png";

	    /**
	     * List of all the image files to load.
	     */
	    imageFileNames = new String[5];
	    imageCaptions = new String[5];

	    imageFileNames[0] = imageFile.getAbsolutePath();
	    imageCaptions[0] = "Original Image";
	    imageFileNames[1] = paletteImageName;
	    imageCaptions[1] = "Final Palette";
	    imageFileNames[2] = initialChromaticityImageName;
	    imageCaptions[2] = "Initial Chromaticity Diagram";
	    imageFileNames[3] = finalChromaticityImageName;
	    imageCaptions[3] = "Final Chromaticity Diagram";
	    imageFileNames[4] = remappedImageName;
	    imageCaptions[4] = "Final Remapped Image";

	    // transforming colors into float arrays for use by the K-Means Clusterer
	    List<float[]> floatArrayColors = new ArrayList<float[]>();
	    for(Color color : ColorUtil.getColors(image)) {
	    	floatArrayColors.add(
	    			(colorSpaceList.getSelectedIndex()==0)?color.getRGBColorComponents(null):
	    				(colorSpaceList.getSelectedIndex()==1)?ColorUtil.toLab(color):ColorUtil.toXyz(color));
	    }

	    KMeansClusterer clusterer = new KMeansClusterer(finalNumberOfColors,floatArrayColors);

	    List<KMeansClusterer.Cluster> clusters = clusterer.getClusters();
	    List<Color> colors = new ArrayList<Color>();
	    for(KMeansClusterer.Cluster cluster:clusters) {
	    	float[] location = cluster.getLocation();
	    	colors.add(
	    			(colorSpaceList.getSelectedIndex()==0)?new Color(ColorSpace.getInstance(ColorSpace.CS_sRGB),location,1):
	    				(colorSpaceList.getSelectedIndex()==1)?ColorUtil.toColor(location):
	    					new Color(ColorSpace.getInstance(ColorSpace.CS_CIEXYZ),location,1));}		

	    ColorUtil.writeChromaticityImage(initialColors, 512, 3,initialChromaticityImageName);
	    ColorUtil.writeChromaticityImage(colors, 512, 16, finalChromaticityImageName);

	    ColorUtil.writePaletteImage(colors.toArray(new Color[0]),50,paletteImageName);
	    ColorUtil.writeImage(ColorUtil.reMap(image, colors), remappedImageName);
	}

	logger.info("Finished");
	// start the image loading SwingWorker in a background thread
	loadImages();
    }

    /**
     * 
     */
    private void open() {
	JFileChooser chooser = new JFileChooser();
	FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF & PNG & BMP Images", "jpg", "gif","png", "bmp");
	chooser.setFileFilter(filter);
	int returnVal = chooser.showOpenDialog(this);
	if(returnVal != JFileChooser.APPROVE_OPTION) return;
	imageFile = chooser.getSelectedFile();
	try {
	    image = ImageIO.read(imageFile);
	    ImageIcon newIcon = new ImageIcon(image);
	    displayImageLabel(newIcon);
	    imageLabel.setIcon(newIcon);
	    computeButton.setEnabled(true);
	} catch (IOException e) {
	    e.printStackTrace();	computeButton.setEnabled(false);
	}
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    static JFrame frame;
    private static void createAndShowGUI() {
	//Create and set up the window.
	frame = new ColorQuantizer();
	frame.setVisible(true);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	//Schedule a job for the event-dispatching thread:
	//creating and showing this application's GUI.
	javax.swing.SwingUtilities.invokeLater(new Runnable() {public void run() {createAndShowGUI();}});
    }


    /**
     * The "missing icon" is a white box with a black border and a red x.
     * It's used to display something when there are issues loading an
     * icon from an external location.
     *
     * @author Collin Fagan
     */
    class MissingIcon implements Icon{

	private int width = 32;
	private int height = 32;

	public MissingIcon() {}
	public MissingIcon(int thewidth, int theheight) {this.width=thewidth;this.height=theheight;}

	private BasicStroke stroke = new BasicStroke(4);

	public void paintIcon(Component c, Graphics g, int x, int y) {
	    Graphics2D g2d = (Graphics2D) g.create();

	    g2d.setColor(Color.WHITE);
	    g2d.fillRect(x +1 ,y + 1,width -2 ,height -2);

	    g2d.setColor(Color.BLACK);
	    g2d.drawRect(x +1 ,y + 1,width -2 ,height -2);

	    g2d.setColor(Color.RED);

	    g2d.setStroke(stroke);
	    g2d.drawLine(x +10, y + 10, x + width -10, y + height -10);
	    g2d.drawLine(x +10, y + height -10, x + width -10, y + 10);

	    g2d.dispose();
	}

	public int getIconWidth() {return width;}
	public int getIconHeight() {return height;}
    }

    private void loadImages() {
	buttonBar.removeAll();
	/**
	 * SwingWorker class that loads the images a background thread and calls publish
	 * when a new one is ready to be displayed.
	 *
	 * We use Void as the first SwingWroker param as we do not need to return
	 * anything from doInBackground().
	 */
	SwingWorker<Void, ThumbnailAction> loadimages = new SwingWorker<Void, ThumbnailAction>() {

	    /**
	     * Creates full size and thumbnail versions of the target image files.
	     */
	    @Override
	    protected Void doInBackground() throws Exception {
		for (int i = 0; i < imageCaptions.length; i++) {
		    ImageIcon icon;
		    icon = createImageIcon(imageFileNames[i], imageCaptions[i]);

		    ThumbnailAction thumbAction;
		    if(icon != null){

			ImageIcon thumbnailIcon = new ImageIcon(getScaledImage(icon.getImage(), 32, 32));

			thumbAction = new ThumbnailAction(icon, thumbnailIcon, imageCaptions[i]);

		    }else{
			// the image failed to load for some reason
			// so load a placeholder instead
			thumbAction = new ThumbnailAction(placeholderIcon, placeholderIcon, imageCaptions[i]);
		    }
		    publish(thumbAction);
		}
		// unfortunately we must return something, and only null is valid to
		// return when the return type is void.
		return null;
	    }

	    /**
	     * Process all loaded images.
	     */
	    @Override
	    protected void process(List<ThumbnailAction> chunks) {
		for (ThumbnailAction thumbAction : chunks) {
		    JButton thumbButton = new JButton(thumbAction);
		    // add the new button BEFORE the last glue
		    // this centers the buttons in the toolbar
		    buttonBar.add(thumbButton, buttonBar.getComponentCount() - 1);
		}
	    }
	};
	loadimages.execute();
    }

    /**
     * Creates an ImageIcon if the path is valid.
     * @param String - absolute path
     * @param String - description of the file
     */
    protected ImageIcon createImageIcon(String path, String description) {return new ImageIcon(path, description);}

    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     */
    private Image getScaledImage(Image srcImg, int w, int h){
	BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	Graphics2D g2 = resizedImg.createGraphics();
	g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	g2.drawImage(srcImg, 0, 0, w, h, null);
	g2.dispose();
	return resizedImg;
    }

    /**
     * Action class that shows the image specified in it's constructor.
     */
    private class ThumbnailAction extends AbstractAction {
	private static final long serialVersionUID = -3888790390915067792L;
	/**
	 *The icon if the full image we want to display.
	 */
	private Icon displayPhoto;

	/**
	 * @param photo - The full size photo to show in the button.
	 * @param thumb - The thumbnail to show in the button.
	 * @param desc - The descriptioon of the icon.
	 */
	public ThumbnailAction(Icon photo, Icon thumb, String desc){
	    displayPhoto = photo;

	    // The short description becomes the tooltip of a button.
	    putValue(SHORT_DESCRIPTION, desc);

	    // The LARGE_ICON_KEY is the key for setting the
	    // icon when an Action is applied to a button.
	    putValue(LARGE_ICON_KEY, thumb);
	}

	/**
	 * Shows the full image in the main area and sets the application title.
	 */
	public void actionPerformed(ActionEvent e) {
	    if (displayPhoto instanceof ImageIcon) displayImageLabel((ImageIcon)displayPhoto);
	    //imageLabel.setIcon(displayPhoto);
	    setTitle("Color Quantizer: " + getValue(SHORT_DESCRIPTION).toString());
	}
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    int scale = 0;
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
	int wheelRotation = -e.getWheelRotation();
	scale+=wheelRotation;
	scale=Math.max(scale, -3);
	scale=Math.min(scale, 3);
	displayImageLabel(currentIcon);
    }

    /**
     * @param currentIcon2
     */
    private void displayImageLabel(ImageIcon icon) {
	if (icon==null) return;
	Image iconImage = icon.getImage();
	double factor = Math.pow(2, scale);
	currentIcon=icon;
	imageLabel.setIcon(new ImageIcon(getScaledImage(iconImage,(int)(icon.getIconWidth()*factor), (int)(icon.getIconHeight()*factor))));
    }
}
