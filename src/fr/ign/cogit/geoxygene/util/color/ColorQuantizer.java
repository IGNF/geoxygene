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
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

/**
 * @author Julien Perret
 *
 */
public class ColorQuantizer extends JFrame implements ActionListener,
MouseListener, MouseWheelListener {
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
    JToolBar buttonBar = new JToolBar();
    MissingIcon placeholderIcon = new MissingIcon();

    /**
     * List of all the descriptions of the image files. These correspond one to
     * one with the image file names
     */
    String[] imageCaptions = null;

    /**
     * List of all the image files to load.
     */
    String[] imageFileNames = null;

    /**
     * 
     */
    public ColorQuantizer() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Palette creator");

        this.openButton.setMnemonic(KeyEvent.VK_O);
        this.openButton.setActionCommand("Open"); //$NON-NLS-1$
        this.openButton.addActionListener(this);
        this.imageLabel = new JLabel(new MissingIcon(800, 600),
                SwingConstants.CENTER);
        //imageLabel.setSize(400, 300);
        //imageLabel.setMinimumSize(new Dimension(400,300));
        this.imageLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        this.imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        this.imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.imageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.buttonBar.add(Box.createGlue());
        this.buttonBar.add(Box.createGlue());

        String[] colorSpaceStrings = { "RGB", //$NON-NLS-1$
                "LAB", "XYZ" };  //$NON-NLS-1$//$NON-NLS-2$
        this.colorSpaceList = new JComboBox(colorSpaceStrings);
        this.colorSpaceList.setSelectedIndex(0);
        this.colorSpaceList.addActionListener(this);

        String[] algorithmStrings = { "K-Means", //$NON-NLS-1$
                "Octree",  //$NON-NLS-1$
                "Octree+Hierarchical Clustering" }; //$NON-NLS-1$
        this.algorithmList = new JComboBox(algorithmStrings);
        this.algorithmList.setSelectedIndex(0);
        this.algorithmList.addActionListener(this);

        String[] weightStrings
        = { "UNIFORM", "AB", //$NON-NLS-1$ //$NON-NLS-2$
                "LAABB", "RGGB" }; //$NON-NLS-1$ //$NON-NLS-2$
        this.weightList = new JComboBox(weightStrings);
        this.weightList.setSelectedIndex(0);
        this.weightList.addActionListener(this);

        String[] minimumSpanningTreeAlgorithmStrings
        = { "Kruskal", "Prim" }; //$NON-NLS-1$ //$NON-NLS-2$
        this.minimumSpanningTreeAlgorithmList = new JComboBox(
                minimumSpanningTreeAlgorithmStrings);
        this.minimumSpanningTreeAlgorithmList.setSelectedIndex(0);
        this.minimumSpanningTreeAlgorithmList.addActionListener(this);

        String[] distanceStrings
        = { "Min", "Max", "Avg" };   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        this.distanceClusterList = new JComboBox(distanceStrings);
        this.distanceClusterList.setSelectedIndex(0);
        this.distanceClusterList.addActionListener(this);

        this.distanceColorList = new JComboBox(distanceStrings);
        this.distanceColorList.setSelectedIndex(0);
        this.distanceColorList.addActionListener(this);

        SpinnerModel model = new SpinnerNumberModel(64, //initial value
                1, //min
                128, //max
                1);       
        this.numberOfColorsOctreeSpinner = new JSpinner(model);
        model = new SpinnerNumberModel(16, //initial value
                1, //min
                128, //max
                1);       
        this.finalNumberOfColorsSpinner = new JSpinner(model);

        this.computeButton.setMnemonic(KeyEvent.VK_C);
        this.computeButton.setActionCommand("Compute"); //$NON-NLS-1$
        this.computeButton.addActionListener(this);
        this.computeButton.setEnabled(false);

        //setLayout(new BorderLayout());
        initializeGroup();

        add(this.group, BorderLayout.PAGE_START);
        add(this.buttonBar, BorderLayout.PAGE_END);
        add(this.imageLabel, BorderLayout.CENTER);

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
        this.group.removeAll();
        this.group.add(this.openButton,BorderLayout.PAGE_START);
        this.group.add(Box.createHorizontalGlue());
        this.group.add(this.colorSpaceList,BorderLayout.PAGE_START);
        this.group.add(this.algorithmList, BorderLayout.PAGE_START);
        if (this.algorithmList.getSelectedIndex() > 1) {
            this.group.add(this.weightList,BorderLayout.PAGE_START);
            this.group.add(this.minimumSpanningTreeAlgorithmList,
                    BorderLayout.PAGE_START);
            this.group.add(this.distanceClusterList, BorderLayout.PAGE_START);
            this.group.add(this.distanceColorList, BorderLayout.PAGE_START);
            this.group.add(this.numberOfColorsOctreeSpinner,
                    BorderLayout.PAGE_START);
        }
        this.group.add(this.finalNumberOfColorsSpinner,
                BorderLayout.PAGE_START);
        this.group.add(this.computeButton,BorderLayout.PAGE_START);
        pack();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Open".equals(e.getActionCommand())) {open();} //$NON-NLS-1$
        if ("Compute".equals(e.getActionCommand())) {compute();} //$NON-NLS-1$
        if (e.getSource().equals(this.algorithmList)) initializeGroup();
        pack();
    }

    /**
     * 
     */
    private void compute() {
        if (this.image==null) {
            logger.info("No image");
            return;
        }

        int finalNumberOfColors
        = ((Integer) this.finalNumberOfColorsSpinner.getValue()).intValue();

        String baseDirectoryPath
        = this.imageFile.getAbsolutePath().substring(0,
                this.imageFile.getAbsolutePath()
                .lastIndexOf(".")); //$NON-NLS-1$
        File directory = new File(baseDirectoryPath);
        directory.mkdir();

        Set<Color> initialColors = ColorUtil.getColors(this.image);

        ColorUtil.setColorSpace(this.colorSpaceList.getSelectedIndex());
        ColorUtil.setWeights(this.weightList.getSelectedIndex());
        SingleLinkageHierarchicalClusterer.setMinimumSpanningTreeAlgorithm(
                this.minimumSpanningTreeAlgorithmList.getSelectedIndex());
        SingleLinkageHierarchicalClusterer.setDistanceClusterCluster(
                this.distanceClusterList.getSelectedIndex());
        SingleLinkageHierarchicalClusterer.setDistanceColorCluster(
                this.distanceColorList.getSelectedIndex());

        if (this.algorithmList.getSelectedIndex()>0) {
            if (this.algorithmList.getSelectedIndex()>1) {
                int numberOfColorsOctree
                = ((Integer) this.numberOfColorsOctreeSpinner.getValue())
                .intValue();

                String imageDirectory = baseDirectoryPath
                + "/Octree_" + numberOfColorsOctree //$NON-NLS-1$
                + "_HierarchicalClustering_" //$NON-NLS-1$
                + this.minimumSpanningTreeAlgorithmList.getSelectedItem()
                + "_" + finalNumberOfColors + "_" //$NON-NLS-1$ //$NON-NLS-2$
                + this.colorSpaceList.getSelectedItem()
                + "_" + this.weightList.getSelectedItem() + "_" //$NON-NLS-1$ //$NON-NLS-2$
                + this.distanceClusterList.getSelectedItem() + "_" //$NON-NLS-1$
                + this.distanceColorList.getSelectedItem();
                directory = new File(imageDirectory);
                directory.mkdir();

                String initialChromaticityImageName = imageDirectory
                + "/chromaticity_" + initialColors.size() //$NON-NLS-1$
                + ".png"; //$NON-NLS-1$
                String octreeChromaticityImageName = imageDirectory
                + "/chromaticity_" + numberOfColorsOctree //$NON-NLS-1$
                + ".png";  //$NON-NLS-1$
                String finalChromaticityImageName = imageDirectory
                + "/chromaticity_" + finalNumberOfColors //$NON-NLS-1$
                + ".png"; //$NON-NLS-1$
                String paletteImageName = imageDirectory
                + "/palette_" + numberOfColorsOctree //$NON-NLS-1$
                + ".png";  //$NON-NLS-1$
                String proportionalPaletteImageName = imageDirectory
                + "/prop_palette_" + numberOfColorsOctree //$NON-NLS-1$
                + ".png"; //$NON-NLS-1$
                String remappedImageName = imageDirectory
                + "/remapped_" + numberOfColorsOctree //$NON-NLS-1$
                + ".png";  //$NON-NLS-1$
                String octreeImageName = imageDirectory
                + "/octree_" + numberOfColorsOctree //$NON-NLS-1$
                + ".png"; //$NON-NLS-1$
                String hierarchyImageName = imageDirectory
                + "/hierarchy_" + numberOfColorsOctree //$NON-NLS-1$
                + ".png"; //$NON-NLS-1$
                String hierarchyPaletteImageName = imageDirectory
                + "/palette_hierarchy_" + numberOfColorsOctree //$NON-NLS-1$
                + ".png"; //$NON-NLS-1$
                String hierarchyClusterImageName = imageDirectory
                + "/hierarchy_cluster_" + numberOfColorsOctree //$NON-NLS-1$
                + ".png"; //$NON-NLS-1$
                String finalHierarchyImageName = imageDirectory
                + "/hierarchy_" + finalNumberOfColors //$NON-NLS-1$
                + ".png";  //$NON-NLS-1$
                String finalHierarchyPaletteImageName = imageDirectory
                + "/palette_" + finalNumberOfColors //$NON-NLS-1$
                + ".png"; //$NON-NLS-1$
                String finalHierarchyClusterImageName = imageDirectory
                + "/hierarchy_cluster_" + finalNumberOfColors //$NON-NLS-1$
                + ".png"; //$NON-NLS-1$
                String finalHierarchyRemappedImageName = imageDirectory
                + "/remapped_" + finalNumberOfColors //$NON-NLS-1$
                + ".png"; //$NON-NLS-1$

                /**
                 * List of all the image files to load.
                 */
                this.imageFileNames = new String[15];
                this.imageCaptions = new String[15];

                this.imageFileNames[0] = this.imageFile.getAbsolutePath();
                this.imageCaptions[0] = "Original Image";
                this.imageFileNames[1] = finalHierarchyPaletteImageName;
                this.imageCaptions[1] = "Final Palette";
                this.imageFileNames[2] = paletteImageName;
                this.imageCaptions[2] = "Octree Palette";
                this.imageFileNames[3] = hierarchyPaletteImageName;
                this.imageCaptions[3] = "Hierarchy Palette";
                this.imageFileNames[4] = proportionalPaletteImageName;
                this.imageCaptions[4] = "Proportional Palette";
                this.imageFileNames[5] = finalHierarchyClusterImageName;
                this.imageCaptions[5] =
                    "Final Hierarchy in Chromaticity Diagram";
                this.imageFileNames[6] = hierarchyClusterImageName;
                this.imageCaptions[6] =
                    "Initial Hierarchy in Chromaticity Diagram";
                this.imageFileNames[7] = finalHierarchyImageName;
                this.imageCaptions[7] = "Final Hierarchy in Cercle";
                this.imageFileNames[8] = hierarchyImageName;
                this.imageCaptions[8] = "Initial Hierarchy in Cercle";
                this.imageFileNames[9] = initialChromaticityImageName;
                this.imageCaptions[9] = "Initial Chromaticity Diagram";
                this.imageFileNames[10] = octreeChromaticityImageName;
                this.imageCaptions[10] = "Octree Chromaticity Diagram";
                this.imageFileNames[11] = finalChromaticityImageName;
                this.imageCaptions[11] = "Final Chromaticity Diagram";
                this.imageFileNames[12] = octreeImageName;
                this.imageCaptions[12] = "Octree";
                this.imageFileNames[13] = finalHierarchyRemappedImageName;
                this.imageCaptions[13] = "Final Remapped Image";
                this.imageFileNames[14] = remappedImageName;
                this.imageCaptions[14] = "Octree Remapped Image";

                ColorUtil.writeChromaticityImage(initialColors, 512, 3,
                        initialChromaticityImageName);

                OctreeQuantizer quantization = new OctreeQuantizer(this.image,
                        numberOfColorsOctree);
                ColorUtil.writePaletteImage(quantization.getColorLookUpTable(),
                        50, paletteImageName);
                ColorUtil.writeProportionalPaletteImage(
                        quantization.getRemappedImage(),
                        proportionalPaletteImageName);
                ColorUtil.writeImage(quantization.getRemappedImage(),
                        remappedImageName);
                quantization.writeOctreeImage(100, octreeImageName);

                ColorUtil.writeChromaticityImage(
                        Arrays.asList(quantization.getColorLookUpTable()), 512,
                        8,octreeChromaticityImageName);

                SingleLinkageHierarchicalClusterer graph
                = new SingleLinkageHierarchicalClusterer(
                        quantization.getColorLookUpTable(),
                        numberOfColorsOctree);
                SingleLinkageHierarchicalClusterer
                .writeSingleLinkageHierarchicalClustering(graph,
                        hierarchyImageName);
                SingleLinkageHierarchicalClusterer.writeClusterImage(graph,
                        512, 8, hierarchyClusterImageName);
                ColorUtil.writePaletteImage(graph.getColors(), 50,
                        hierarchyPaletteImageName);

                graph = new SingleLinkageHierarchicalClusterer(
                        quantization.getColorLookUpTable(),
                        finalNumberOfColors);
                SingleLinkageHierarchicalClusterer
                .writeSingleLinkageHierarchicalClustering(graph,
                        finalHierarchyImageName);
                SingleLinkageHierarchicalClusterer.writeClusterImage(graph,
                        512, 8, finalHierarchyClusterImageName);
                ColorUtil.writePaletteImage(graph.getColors(), 50,
                        finalHierarchyPaletteImageName);

                ColorUtil.writeChromaticityImage(
                        Arrays.asList(graph.getColors()), 512, 16,
                        finalChromaticityImageName);

                quantization.setColors(graph.getColors());
                quantization.reMap(this.image);
                ColorUtil.writeImage(quantization.getRemappedImage(),
                        finalHierarchyRemappedImageName);

            } else {
                String imageDirectory = baseDirectoryPath
                + "/Octree_" + finalNumberOfColors; //$NON-NLS-1$
                directory = new File(imageDirectory);
                directory.mkdir();

                String initialChromaticityImageName = imageDirectory
                + "/chromaticity_" + initialColors.size() //$NON-NLS-1$
                + ".png"; //$NON-NLS-1$
                String finalChromaticityImageName = imageDirectory
                + "/chromaticity_" + finalNumberOfColors //$NON-NLS-1$
                + ".png"; //$NON-NLS-1$
                String paletteImageName = imageDirectory
                + "/palette_" + finalNumberOfColors //$NON-NLS-1$
                + ".png"; //$NON-NLS-1$
                String proportionalPaletteImageName = imageDirectory
                + "/prop_palette_" + finalNumberOfColors //$NON-NLS-1$
                + ".png"; //$NON-NLS-1$
                String remappedImageName = imageDirectory
                + "/remapped_" + finalNumberOfColors //$NON-NLS-1$
                + ".png";  //$NON-NLS-1$
                String octreeImageName = imageDirectory
                + "/octree_" + finalNumberOfColors //$NON-NLS-1$
                + ".png"; //$NON-NLS-1$
                String graphClusterImageName = imageDirectory
                + "/graph_cluster_" + finalNumberOfColors //$NON-NLS-1$
                + ".png"; //$NON-NLS-1$
                //String graphImageName
                //= imageDirectory+"/graph_"+finalNumberOfColors+".png";

                /**
                 * List of all the image files to load.
                 */
                this.imageFileNames = new String[8];
                this.imageCaptions = new String[8];

                this.imageFileNames[0] = this.imageFile.getAbsolutePath();
                this.imageCaptions[0] = "Original Image";
                this.imageFileNames[1] = paletteImageName;
                this.imageCaptions[1] = "Final Palette";
                this.imageFileNames[2] = proportionalPaletteImageName;
                this.imageCaptions[2] = "Proportional Palette";
                this.imageFileNames[3] = initialChromaticityImageName;
                this.imageCaptions[3] = "Initial Chromaticity Diagram";
                this.imageFileNames[4] = finalChromaticityImageName;
                this.imageCaptions[4] = "Final Chromaticity Diagram";
                this.imageFileNames[5] = octreeImageName;
                this.imageCaptions[5] = "Octree";
                this.imageFileNames[6] = remappedImageName;
                this.imageCaptions[6] = "Final Remapped Image";
                this.imageFileNames[7] = graphClusterImageName;
                this.imageCaptions[7] = "Graph Cluster Image";

                ColorUtil.writeChromaticityImage(initialColors, 512, 3,
                        initialChromaticityImageName);

                OctreeQuantizer quantization = new OctreeQuantizer(this.image,
                        finalNumberOfColors);
                ColorUtil.writeChromaticityImage(Arrays.asList(
                        quantization.getColorLookUpTable()), 512, 8,
                        finalChromaticityImageName);
                ColorUtil.writePaletteImage(quantization.getColorLookUpTable(),
                        50, paletteImageName);
                ColorUtil.writeProportionalPaletteImage(
                        quantization.getRemappedImage(),
                        proportionalPaletteImageName);
                ColorUtil.writeImage(quantization.getRemappedImage(),
                        remappedImageName);
                quantization.writeOctreeImage(100, octreeImageName);
                ImageColorClusterGraph graph = new ImageColorClusterGraph(
                        quantization.getRemappedImage());
                ColorUtil.writeImage(graph.buildGraphClusterImage(),
                        graphClusterImageName);
                //ColorUtil.writeImage(graph.buildGraphImage(), graphImageName);
            }
        } else {
            String imageDirectory = baseDirectoryPath
            + "/KMeans_" + finalNumberOfColors //$NON-NLS-1$
            + "_" + this.colorSpaceList.getSelectedItem(); //$NON-NLS-1$
            directory = new File(imageDirectory);
            directory.mkdir();

            String initialChromaticityImageName = imageDirectory
            + "/chromaticity_" + initialColors.size() //$NON-NLS-1$
            + ".png"; //$NON-NLS-1$
            String finalChromaticityImageName = imageDirectory
            + "/chromaticity_" + finalNumberOfColors //$NON-NLS-1$
            + ".png"; //$NON-NLS-1$
            String paletteImageName = imageDirectory
            + "/palette_" + finalNumberOfColors //$NON-NLS-1$
            + ".png";  //$NON-NLS-1$
            String remappedImageName = imageDirectory
            + "/remapped_" + finalNumberOfColors //$NON-NLS-1$
            + ".png"; //$NON-NLS-1$

            /**
             * List of all the image files to load.
             */
            this.imageFileNames = new String[5];
            this.imageCaptions = new String[5];

            this.imageFileNames[0] = this.imageFile.getAbsolutePath();
            this.imageCaptions[0] = "Original Image";
            this.imageFileNames[1] = paletteImageName;
            this.imageCaptions[1] = "Final Palette";
            this.imageFileNames[2] = initialChromaticityImageName;
            this.imageCaptions[2] = "Initial Chromaticity Diagram";
            this.imageFileNames[3] = finalChromaticityImageName;
            this.imageCaptions[3] = "Final Chromaticity Diagram";
            this.imageFileNames[4] = remappedImageName;
            this.imageCaptions[4] = "Final Remapped Image";

            // transforming colors into float arrays for use by the K-Means
            // Clusterer
            List<float[]> floatArrayColors = new ArrayList<float[]>();
            for(Color color : ColorUtil.getColors(this.image)) {
                floatArrayColors.add(
                        (this.colorSpaceList.getSelectedIndex() == 0)
                        ? color.getRGBColorComponents(null)
                                : (this.colorSpaceList.getSelectedIndex() == 1)
                                ? ColorUtil.toLab(color)
                                        : ColorUtil.toXyz(color));
            }

            KMeansClusterer clusterer = new KMeansClusterer(
                    finalNumberOfColors,
                    floatArrayColors);

            List<KMeansClusterer.Cluster> clusters = clusterer.getClusters();
            List<Color> colors = new ArrayList<Color>();
            for(KMeansClusterer.Cluster cluster:clusters) {
                float[] location = cluster.getLocation();
                colors.add(
                        (this.colorSpaceList.getSelectedIndex() == 0)
                        ? new Color(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                                location, 1)
                        : (this.colorSpaceList.getSelectedIndex() == 1)
                        ? ColorUtil.toColor(location)
                                : new Color(ColorSpace.getInstance(
                                        ColorSpace.CS_CIEXYZ), location, 1));
            }		

            ColorUtil.writeChromaticityImage(initialColors, 512, 3,initialChromaticityImageName);
            ColorUtil.writeChromaticityImage(colors, 512, 16, finalChromaticityImageName);

            ColorUtil.writePaletteImage(colors.toArray(new Color[0]),50,paletteImageName);
            ColorUtil.writeImage(ColorUtil.reMap(this.image, colors), remappedImageName);
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
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "JPG & GIF & PNG & BMP Images"
                , "jpg", "gif"  //$NON-NLS-1$//$NON-NLS-2$
                , "png", "bmp"); //$NON-NLS-1$//$NON-NLS-2$
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal != JFileChooser.APPROVE_OPTION) return;
        this.imageFile = chooser.getSelectedFile();
        try {
            this.image = ImageIO.read(this.imageFile);
            ImageIcon newIcon = new ImageIcon(this.image);
            displayImageLabel(newIcon);
            this.imageLabel.setIcon(newIcon);
            this.computeButton.setEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
            this.computeButton.setEnabled(false);
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    static JFrame frame;
    static void createAndShowGUI() {
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
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() { createAndShowGUI(); }
        });
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

        public MissingIcon() { }
        public MissingIcon(int thewidth, int theheight) {
            this.width = thewidth; this.height = theheight;
        }

        private BasicStroke stroke = new BasicStroke(4);

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setColor(Color.WHITE);
            g2d.fillRect(x + 1, y + 1, this.width - 2, this.height - 2);

            g2d.setColor(Color.BLACK);
            g2d.drawRect(x + 1, y + 1, this.width - 2, this.height - 2);

            g2d.setColor(Color.RED);

            g2d.setStroke(this.stroke);
            g2d.drawLine(x + 10, y + 10,
                    x + this.width - 10, y + this.height - 10);
            g2d.drawLine(x + 10, y + this.height - 10,
                    x + this.width - 10, y + 10);

            g2d.dispose();
        }

        @Override
        public int getIconWidth() { return this.width; }

        @Override
        public int getIconHeight() { return this.height; }
    }

    private void loadImages() {
        this.buttonBar.removeAll();
        /**
         * SwingWorker class that loads the images a background thread and
         * calls publish when a new one is ready to be displayed.
         *
         * We use Void as the first SwingWroker param as we do not need to
         * return anything from doInBackground().
         */
        SwingWorker<Void, ThumbnailAction> loadimages
        = new SwingWorker<Void,ThumbnailAction>() {

            /**
             * Creates full size and thumbnail versions of the target image
             * files.
             */
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0;
                i < ColorQuantizer.this.imageCaptions.length; i++) {
                    ImageIcon icon;
                    icon = createImageIcon(
                            ColorQuantizer.this.imageFileNames[i],
                            ColorQuantizer.this.imageCaptions[i]);

                    ThumbnailAction thumbAction;
                    if(icon != null){

                        ImageIcon thumbnailIcon = new ImageIcon(getScaledImage(
                                icon.getImage(), 32, 32));

                        thumbAction = new ThumbnailAction(icon, thumbnailIcon,
                                ColorQuantizer.this.imageCaptions[i]);

                    }else{
                        // the image failed to load for some reason
                        // so load a placeholder instead
                        thumbAction = new ThumbnailAction(
                                ColorQuantizer.this.placeholderIcon,
                                ColorQuantizer.this.placeholderIcon,
                                ColorQuantizer.this.imageCaptions[i]);
                    }
                    publish(thumbAction);
                }
                // unfortunately we must return something, and only null is
                // valid to return when the return type is void.
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
                    ColorQuantizer.this.buttonBar.add(thumbButton,
                            ColorQuantizer.this.buttonBar
                            .getComponentCount() - 1);
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
    protected ImageIcon createImageIcon(String path, String description) {
        return new ImageIcon(path, description);
    }

    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     */
    Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
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
            this.displayPhoto = photo;

            // The short description becomes the tooltip of a button.
            putValue(SHORT_DESCRIPTION, desc);

            // The LARGE_ICON_KEY is the key for setting the
            // icon when an Action is applied to a button.
            putValue(LARGE_ICON_KEY, thumb);
        }

        /**
         * Shows the full image in the main area and sets the application
         * title.
         */
        @Override
		public void actionPerformed(ActionEvent e) {
            if (this.displayPhoto instanceof ImageIcon) {
                displayImageLabel((ImageIcon) this.displayPhoto);
            }
            //imageLabel.setIcon(displayPhoto);
            setTitle("Color Quantizer: "
                    + getValue(SHORT_DESCRIPTION).toString());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    int scale = 0;
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int wheelRotation = -e.getWheelRotation();
        this.scale += wheelRotation;
        this.scale = Math.max(this.scale, -3);
        this.scale = Math.min(this.scale, 3);
        displayImageLabel(this.currentIcon);
    }

    /**
     * @param icon icon to display
     */
    void displayImageLabel(ImageIcon icon) {
        if (icon==null) { return; }
        Image iconImage = icon.getImage();
        double factor = Math.pow(2, this.scale);
        this.currentIcon=icon;
        this.imageLabel.setIcon(new ImageIcon(getScaledImage(iconImage,
                (int) (icon.getIconWidth() * factor),
                (int) (icon.getIconHeight() * factor))));
    }
}
