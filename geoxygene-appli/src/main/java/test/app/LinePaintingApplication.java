package test.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.lwjgl.LWJGLException;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.layer.LayerFactory;
import fr.ign.cogit.geoxygene.appli.layer.LayerFactory.LayerType;
import fr.ign.cogit.geoxygene.appli.render.texture.ParameterizedLine;
import fr.ign.cogit.geoxygene.appli.render.texture.ParameterizedPoint;
import fr.ign.cogit.geoxygene.appli.render.texture.ParameterizedSegment;
import fr.ign.cogit.geoxygene.function.ComposeFunction;
import fr.ign.cogit.geoxygene.function.ConstantFunction;
import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.function.LinearFunction;
import fr.ign.cogit.geoxygene.function.NoiseFunction;
import fr.ign.cogit.geoxygene.function.SinFunction;
import fr.ign.cogit.geoxygene.function.StringFunction;
import fr.ign.cogit.geoxygene.function.ui.Function1DListCellRenderer;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLContext;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;

public class LinePaintingApplication {

    private static final Logger logger = Logger
            .getLogger(LinePaintingApplication.class.getName()); // logger

    public static final String paintProgramName = "Paint";
    public static final String basicProgramName = "Basic";
    private static final String LAST_DIRECTORY = "painter.app.lastDirectory";
    private static final String PAPER_LAST_DIRECTORY = "painter.app.paperLastDirectory";
    private static final String BRUSH_LAST_DIRECTORY = "painter.app.brushLastDirectory";

    private JFrame frame = null;
    private JPanel topPanel = null;
    private JPanel bottomPanel = null;
    private LinePaintingGLCanvas canvas = null;
    private JLabel paperFilenameLabel = null;
    private JLabel brushFilenameLabel = null;
    // private String shapeFilename =
    // "/home/turbet/export/geodata/basic-tests/line.5.points.open.shp";
    // private String shapeFilename =
    // "/home/turbet/export/geodata/basic-tests/line.2.points.shp";
    private String shapeFilename = "./src/main/resources/test/app/shapes/troncon_cours_d_eau_25.shp";
    public String paperTextureFilename = "./src/main/resources/test/app/papers/black-normalized.png";
    public String brushTextureFilename = "./src/main/resources/test/app/brushes/chalk2-100-200.png";
    public int brushStartLength = 100;
    public int brushEndLength = 200;
    private final Preferences prefs = Preferences.userRoot();
    double sampleSize = 2.;
    double minAngle = 1.5;
    double brushSize = 8;
    double paperScaleFactor = .5;
    double paperDensity = 0.7;
    double brushDensity = 1.9;
    double strokePressure = 2.64;
    double sharpness = 0.1;
    double strokePressureVariationAmplitude = .32;
    double strokePressureVariationFrequency = .025;
    double strokeShiftVariationAmplitude = .92;
    double strokeShiftVariationFrequency = .05;
    double strokeThicknessVariationAmplitude = .07;
    double strokeThicknessVariationFrequency = .05;

    private final List<ILineString> lines = new ArrayList<ILineString>();
    private IEnvelope envelope = null;
    boolean wireframe = false;
    public static final Function1D DefaultLineWidthFunction = new ConstantFunction(
            1);
    public static final Function1D DefaultLineShiftFunction = new ConstantFunction(
            0);
    private Function1D lineWidthFunction = new ConstantFunction(1);
    private Function1D lineShiftFunction = new ConstantFunction(0);

    public StyledLayerDescriptor sld = new StyledLayerDescriptor();

    public boolean drawLines = false;

    public static final String m00ModelToViewMatrixUniformVarName = "m00";
    public static final String m02ModelToViewMatrixUniformVarName = "m02";
    public static final String m11ModelToViewMatrixUniformVarName = "m11";
    public static final String m12ModelToViewMatrixUniformVarName = "m12";
    public static final String screenWidthUniformVarName = "screenWidth";
    public static final String screenHeightUniformVarName = "screenHeight";
    public static final String paperTextureUniformVarName = "paperSampler";
    public static final String brushTextureUniformVarName = "brushSampler";
    public static final String mapScaleDiv1000UniformVarName = "mapScaleDiv1000";
    public static final String brushWidthUniformVarName = "brushWidth";
    public static final String brushHeightUniformVarName = "brushHeight";
    public static final String brushStartWidthUniformVarName = "brushStartWidth";
    public static final String brushEndWidthUniformVarName = "brushEndWidth";
    // width of one brush pixel (mm)
    public static final String brushScaleUniformVarName = "brushScale";
    public static final String paperScaleUniformVarName = "paperScale";
    public static final String paperDensityUniformVarName = "paperDensity";
    public static final String brushDensityUniformVarName = "brushDensity";
    public static final String strokePressureUniformVarName = "strokePressure";
    public static final String sharpnessUniformVarName = "sharpness";
    public static final String strokePressureVariationAmplitudeUniformVarName = "pressureVariationAmplitude";
    public static final String strokePressureVariationFrequencyUniformVarName = "pressureVariationFrequency";
    public static final String strokeShiftVariationAmplitudeUniformVarName = "shiftVariationAmplitude";
    public static final String strokeShiftVariationFrequencyUniformVarName = "shiftVariationFrequency";
    public static final String strokeThicknessVariationAmplitudeUniformVarName = "thicknessVariationAmplitude";
    public static final String strokeThicknessVariationFrequencyUniformVarName = "thicknessVariationFrequency";

    public LinePaintingApplication() throws Exception {
        this.initializeGui();
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        System.err.println(this.loadShapeFile(this.shapeFilename));
    }

    public void run() {
        this.frame.setSize(800, 600);
        this.frame.setVisible(true);
    }

    public static void main(String[] args) {

        // testLoading();
        // System.exit(0);
        LinePaintingApplication app;
        try {
            app = new LinePaintingApplication();
            app.run();
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    private static void testLoading() {
        StyledLayerDescriptor sld = new StyledLayerDescriptor();
        File file = new File(
                "/home/turbet/export/expressivemaps/data/bdcarto/troncon_cours_eau.shp");

        String fileName = file.getAbsolutePath();
        String extention = fileName.substring(fileName.lastIndexOf('.') + 1);
        LayerFactory factory = new LayerFactory(sld);
        Layer l = null;
        if (extention.equalsIgnoreCase("shp")) { //$NON-NLS-1$
            l = factory.createLayer(fileName, LayerType.SHAPEFILE);
        } else if (extention.equalsIgnoreCase("tif")) { //$NON-NLS-1$
            l = factory.createLayer(fileName, LayerType.GEOTIFF);
        } else if (extention.equalsIgnoreCase("asc")) { //$NON-NLS-1$
            l = factory.createLayer(fileName, LayerType.ASC);
        } else if (extention.equalsIgnoreCase("txt")) { //$NON-NLS-1$
            l = factory.createLayer(fileName, LayerType.TXT);
        }
        if (l != null) {
            sld.add(l);
        }
        for (Layer layer : sld.getLayers()) {
            int count = 0;
            while (layer.getFeatureCollection().getElements().size() < 24) {
                System.err.println("Layer " + layer.getName() + " has "
                        + layer.getFeatureCollection().getElements().size()
                        + " elements");
                count++;
            }
            System.err.println("it has been counted " + count
                    + " times with value < 24 !!!");
        }

    }

    /**
     * @return the sld
     */
    public StyledLayerDescriptor getSld() {
        return this.sld;
    }

    /**
     * @return the lineWidthFunction
     */
    public Function1D getLineWidthFunction() {
        return this.lineWidthFunction;
    }

    /**
     * @return the lineShiftFunction
     */
    public Function1D getLineShiftFunction() {
        return this.lineShiftFunction;
    }

    private void initializeGui() {
        this.frame = new JFrame();
        try {
            this.canvas = new LinePaintingGLCanvas(this);
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }
        this.canvas.setBackground(Color.pink);
        JPanel canvasContainer = new JPanel(new BorderLayout());
        canvasContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
        canvasContainer.add(this.canvas, BorderLayout.CENTER);
        this.frame.getContentPane().add(canvasContainer, BorderLayout.CENTER);
        this.topPanel = new JPanel();
        // this.topPanel.setLayout(new BoxLayout(this.topPanel,
        // BoxLayout.Y_AXIS));
        this.topPanel.setPreferredSize(new Dimension(250, 0));
        this.topPanel.setBorder(BorderFactory
                .createEtchedBorder(EtchedBorder.LOWERED));
        this.frame.getContentPane().add(this.topPanel, BorderLayout.WEST);
        JPanel buttonPanel = new JPanel();
        JButton quitButton = new JButton(
                new ImageIcon(LinePaintingApplication.class
                        .getResource("/images/icons/16x16/exit-power-quit.png")));
        quitButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        quitButton.setToolTipText("Exit application");
        quitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LinePaintingApplication.this.frame.dispose();
            }
        });
        buttonPanel.add(quitButton);
        JButton reloadButton = new JButton(new ImageIcon(
                LinePaintingApplication.class
                        .getResource("/images/icons/16x16/refresh.png")));
        reloadButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        reloadButton.setToolTipText("Reload shaders");
        reloadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    LinePaintingApplication.this.canvas.reloadContext();
                    LinePaintingApplication.this.refresh();
                } catch (GLException e1) {
                    JOptionPane.showMessageDialog(
                            LinePaintingApplication.this.frame, e1.getMessage());
                    e1.printStackTrace();
                }
            }
        });
        buttonPanel.add(reloadButton);
        JButton loadButton = new JButton(new ImageIcon(
                LinePaintingApplication.class
                        .getResource("/images/icons/16x16/plus.png")));
        loadButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        loadButton.setToolTipText("Open Shape File");
        loadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser(
                        LinePaintingApplication.this.prefs.get(LAST_DIRECTORY,
                                "."));
                if (fc.showOpenDialog(LinePaintingApplication.this.frame) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File selectedFile = fc.getSelectedFile();
                        LinePaintingApplication.this.shapeFilename = selectedFile
                                .getAbsolutePath();
                        LinePaintingApplication.this.prefs.put(LAST_DIRECTORY,
                                selectedFile.getAbsolutePath());
                        JOptionPane
                                .showMessageDialog(
                                        LinePaintingApplication.this.frame,
                                        LinePaintingApplication.this
                                                .loadShapeFile(LinePaintingApplication.this.shapeFilename));
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(
                                LinePaintingApplication.this.frame,
                                e1.getMessage());
                        e1.printStackTrace();
                    }
                }
            }

        });
        buttonPanel.add(loadButton);

        JButton drawButton = new JButton(new ImageIcon(
                LinePaintingApplication.class
                        .getResource("/images/icons/16x16/wireframe.png")));
        drawButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        drawButton.setToolTipText("Toggle Wireframe/Plain rendering");
        drawButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LinePaintingApplication.this.wireframe = !LinePaintingApplication.this.wireframe;
                LinePaintingApplication.this.refresh();
            }
        });
        buttonPanel.add(drawButton);
        JButton linesButton = new JButton(new ImageIcon(
                LinePaintingApplication.class
                        .getResource("/images/icons/16x16/move2.png")));
        linesButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        linesButton.setToolTipText("Toggle Line rendering");
        linesButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LinePaintingApplication.this.drawLines = !LinePaintingApplication.this.drawLines;
                LinePaintingApplication.this.refresh();
            }
        });
        buttonPanel.add(linesButton);

        this.topPanel.add(buttonPanel);

        JComboBox lineWidthComboBox = new JComboBox();
        lineWidthComboBox.setRenderer(new Function1DListCellRenderer());
        lineWidthComboBox.addItem(new ConstantFunction(5));
        lineWidthComboBox.addItem(new ConstantFunction(10));
        lineWidthComboBox.addItem(new ConstantFunction(100));
        lineWidthComboBox.addItem(new ConstantFunction(100));
        lineWidthComboBox.addItem(new NoiseFunction(1, 0.5, 0, 1));
        lineWidthComboBox.addItem(new NoiseFunction(0.1, 0.5, 0, 1));
        lineWidthComboBox.addItem(new NoiseFunction(0.01, 0.5, 0, 1));
        lineWidthComboBox.addItem(new NoiseFunction(0.001, 0.5, 0, 1));
        lineWidthComboBox.addItem(new LinearFunction(0.01, 1));
        lineWidthComboBox.addItem(new ComposeFunction(
                new LinearFunction(0.5, 1), new SinFunction(0.1, 5)));
        lineWidthComboBox.addItem(new ComposeFunction(
                new LinearFunction(0.5, 1), new SinFunction(0.2, 5)));
        lineWidthComboBox.addItem(new StringFunction("sin(x)"));
        lineWidthComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    LinePaintingApplication.this.lineWidthFunction = (Function1D) e
                            .getItem();
                    LinePaintingApplication.this.refresh();
                }

            }
        });
        this.topPanel.add(lineWidthComboBox);
        lineWidthComboBox.setSelectedIndex(1);

        JComboBox lineShiftComboBox = new JComboBox();
        lineShiftComboBox.setRenderer(new Function1DListCellRenderer());
        lineShiftComboBox.addItem(new ConstantFunction(0));
        lineShiftComboBox.addItem(new ConstantFunction(10));
        lineShiftComboBox.addItem(new NoiseFunction(1, 0.5, 1, 1));
        lineShiftComboBox.addItem(new NoiseFunction(0.1, 0.5, 1, 1));
        lineShiftComboBox.addItem(new NoiseFunction(0.01, 0.5, 1, 1));
        lineShiftComboBox.addItem(new NoiseFunction(0.001, 0.5, 1, 1));

        lineShiftComboBox.addItem(new SinFunction(0.1, 10));
        lineShiftComboBox.addItem(new SinFunction(0.2, 10));
        lineShiftComboBox.addItem(new StringFunction("cos(x)"));
        lineShiftComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    LinePaintingApplication.this.lineShiftFunction = (Function1D) e
                            .getItem();
                    LinePaintingApplication.this.refresh();
                }

            }
        });
        this.topPanel.add(lineShiftComboBox);

        lineShiftComboBox.setSelectedIndex(0);
        Dimension d = new Dimension(150, 40);
        SpinnerNumberModel model = new SpinnerNumberModel(this.sampleSize, 0.1,
                1000., 1.);
        final JSpinner spinner = new JSpinner(model);
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner
                .getEditor();
        DecimalFormat format = editor.getFormat();
        format.setMinimumFractionDigits(3);
        editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
        spinner.setPreferredSize(d);
        spinner.setBorder(BorderFactory.createTitledBorder("sample size"));
        spinner.setToolTipText("distance between samples during line tesselation");
        spinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                LinePaintingApplication.this.sampleSize = (Double) (spinner
                        .getValue());
                LinePaintingApplication.this.refresh();

            }
        });
        this.topPanel.add(spinner);

        SpinnerNumberModel minAngleModel = new SpinnerNumberModel(
                this.minAngle, 0, 180, .1);
        final JSpinner minAngleSpinner = new JSpinner(minAngleModel);
        JSpinner.NumberEditor minAngleEditor = (JSpinner.NumberEditor) minAngleSpinner
                .getEditor();
        minAngleEditor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER);
        minAngleSpinner.setPreferredSize(d);
        minAngleSpinner
                .setBorder(BorderFactory.createTitledBorder("min angle"));
        minAngleSpinner
                .setToolTipText("minimum angle in tesselation under which edges are considered colinear");
        minAngleSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                LinePaintingApplication.this.minAngle = (Double) (minAngleSpinner
                        .getValue());
                LinePaintingApplication.this.refresh();

            }
        });
        this.topPanel.add(minAngleSpinner);

        SpinnerNumberModel brushSizeModel = new SpinnerNumberModel(
                this.brushSize, 0, 180, .1);
        final JSpinner brushSizeSpinner = new JSpinner(brushSizeModel);
        JSpinner.NumberEditor brushSizeEditor = (JSpinner.NumberEditor) brushSizeSpinner
                .getEditor();
        brushSizeEditor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER);
        brushSizeSpinner.setPreferredSize(d);
        brushSizeSpinner.setBorder(BorderFactory
                .createTitledBorder("brush size"));
        brushSizeSpinner
                .setToolTipText("size of one pixel of the brush (in mm)");

        brushSizeSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                LinePaintingApplication.this.brushSize = (Double) (brushSizeSpinner
                        .getValue());
                LinePaintingApplication.this.refresh();

            }
        });
        this.topPanel.add(brushSizeSpinner);

        JButton paperBrowseButton = new JButton("paper browse...");
        paperBrowseButton
                .setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        paperBrowseButton.setToolTipText("Load background paper file");
        paperBrowseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser(
                        LinePaintingApplication.this.prefs.get(
                                PAPER_LAST_DIRECTORY, "."));
                if (fc.showOpenDialog(LinePaintingApplication.this.frame) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File selectedFile = fc.getSelectedFile();
                        LinePaintingApplication.this.paperTextureFilename = selectedFile
                                .getAbsolutePath();
                        LinePaintingApplication.this.paperFilenameLabel
                                .setText(LinePaintingApplication.this.paperTextureFilename
                                        .substring(LinePaintingApplication.this.paperTextureFilename
                                                .length() - 30));

                        LinePaintingApplication.this.prefs.put(
                                PAPER_LAST_DIRECTORY,
                                selectedFile.getAbsolutePath());
                        LinePaintingApplication.this.canvas
                                .invalidatePaperTexture();
                        LinePaintingApplication.this.refresh();
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(
                                LinePaintingApplication.this.frame,
                                e1.getMessage());
                        e1.printStackTrace();
                    }
                }
            }

        });
        this.topPanel.add(paperBrowseButton);
        this.paperFilenameLabel = new JLabel(this.paperTextureFilename);
        this.topPanel.add(this.paperFilenameLabel);

        SpinnerNumberModel paperScaleFactorModel = new SpinnerNumberModel(
                this.paperScaleFactor, 0, 180, .1);
        final JSpinner paperScaleFactorSpinner = new JSpinner(
                paperScaleFactorModel);
        JSpinner.NumberEditor paperScaleFactorEditor = (JSpinner.NumberEditor) paperScaleFactorSpinner
                .getEditor();
        paperScaleFactorEditor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER);
        paperScaleFactorSpinner.setBorder(BorderFactory
                .createTitledBorder("paper scale"));
        paperScaleFactorSpinner.setToolTipText("paper texture scale factor");
        paperScaleFactorSpinner.setPreferredSize(d);
        paperScaleFactorSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                LinePaintingApplication.this.paperScaleFactor = (Double) (paperScaleFactorSpinner
                        .getValue());
                LinePaintingApplication.this.refresh();

            }
        });
        this.topPanel.add(paperScaleFactorSpinner);

        SpinnerNumberModel brushStartModel = new SpinnerNumberModel(
                this.brushStartLength, 1, 5000, 1);
        final JSpinner brushStartSpinner = new JSpinner(brushStartModel);
        JSpinner.NumberEditor brushStartEditor = (JSpinner.NumberEditor) brushStartSpinner
                .getEditor();
        DecimalFormat intFormat = brushStartEditor.getFormat();
        intFormat.setMinimumFractionDigits(0);
        brushStartEditor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER);
        brushStartSpinner.setPreferredSize(d);
        brushStartSpinner.setBorder(BorderFactory
                .createTitledBorder("brush start"));
        brushStartSpinner.setToolTipText("length of the brush start");
        brushStartSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                LinePaintingApplication.this.brushStartLength = (Integer) (brushStartSpinner
                        .getValue());
                LinePaintingApplication.this.refresh();

            }
        });

        SpinnerNumberModel brushEndModel = new SpinnerNumberModel(
                this.brushEndLength, 1, 5000, 1);
        final JSpinner brushEndSpinner = new JSpinner(brushEndModel);
        JSpinner.NumberEditor brushEndEditor = (JSpinner.NumberEditor) brushEndSpinner
                .getEditor();
        intFormat.setMinimumFractionDigits(0);
        brushEndEditor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER);
        brushEndSpinner.setPreferredSize(d);
        brushEndSpinner
                .setBorder(BorderFactory.createTitledBorder("brush end"));
        brushEndSpinner.setToolTipText("length of the brush end");
        brushEndSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                LinePaintingApplication.this.brushEndLength = (Integer) (brushEndSpinner
                        .getValue());
                LinePaintingApplication.this.refresh();

            }
        });

        JButton brushBrowseButton = new JButton("brush browser...");
        brushBrowseButton
                .setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        brushBrowseButton.setToolTipText("Load brush file");
        brushBrowseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser(
                        LinePaintingApplication.this.prefs.get(
                                BRUSH_LAST_DIRECTORY, "."));
                if (fc.showOpenDialog(LinePaintingApplication.this.frame) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File selectedFile = fc.getSelectedFile();
                        LinePaintingApplication.this.brushTextureFilename = selectedFile
                                .getAbsolutePath();
                        LinePaintingApplication.this.brushFilenameLabel
                                .setText(LinePaintingApplication.this.brushTextureFilename
                                        .substring(LinePaintingApplication.this.brushTextureFilename
                                                .length() - 30));
                        Pattern pattern = Pattern.compile("([0-9]+)-([0-9]+)");
                        Matcher matcher = pattern
                                .matcher(LinePaintingApplication.this.brushTextureFilename);
                        if (matcher.matches()) {
                            int start = Integer.valueOf(matcher.group(1));
                            int end = Integer.valueOf(matcher.group(2));
                            brushStartSpinner.setValue(start);
                            brushEndSpinner.setValue(end);
                            LinePaintingApplication.this.brushStartLength = (Integer) (brushStartSpinner
                                    .getValue());
                            LinePaintingApplication.this.brushEndLength = (Integer) (brushEndSpinner
                                    .getValue());
                        }
                        LinePaintingApplication.this.prefs.put(
                                BRUSH_LAST_DIRECTORY,
                                selectedFile.getAbsolutePath());
                        LinePaintingApplication.this.canvas
                                .invalidateBrushTexture();
                        LinePaintingApplication.this.refresh();
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(
                                LinePaintingApplication.this.frame,
                                e1.getMessage());
                        e1.printStackTrace();
                    }
                }
            }

        });
        this.topPanel.add(brushBrowseButton);

        this.brushFilenameLabel = new JLabel(this.brushTextureFilename);
        this.topPanel.add(this.brushFilenameLabel);

        this.topPanel.add(brushStartSpinner);
        this.topPanel.add(brushEndSpinner);

        SpinnerNumberModel brushDensityModel = new SpinnerNumberModel(
                this.brushDensity, 0, 10, .1);
        final JSpinner brushDensitySpinner = new JSpinner(brushDensityModel);
        JSpinner.NumberEditor brushDensityEditor = (JSpinner.NumberEditor) brushDensitySpinner
                .getEditor();
        brushDensityEditor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER);
        brushDensitySpinner.setPreferredSize(d);
        brushDensitySpinner.setBorder(BorderFactory
                .createTitledBorder("brush density"));
        brushDensitySpinner.setToolTipText("brush height scale factor");
        brushDensitySpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                LinePaintingApplication.this.brushDensity = (Double) (brushDensitySpinner
                        .getValue());
                LinePaintingApplication.this.refresh();

            }
        });
        this.topPanel.add(brushDensitySpinner);

        SpinnerNumberModel paperDensityModel = new SpinnerNumberModel(
                this.paperDensity, 0, 10, .1);
        final JSpinner paperDensitySpinner = new JSpinner(paperDensityModel);
        JSpinner.NumberEditor paperDensityEditor = (JSpinner.NumberEditor) paperDensitySpinner
                .getEditor();
        paperDensityEditor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER);
        paperDensitySpinner.setPreferredSize(d);
        paperDensitySpinner.setBorder(BorderFactory
                .createTitledBorder("paper density"));
        paperDensitySpinner.setToolTipText("scale factor for paper height");
        paperDensitySpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                LinePaintingApplication.this.paperDensity = (Double) (paperDensitySpinner
                        .getValue());
                LinePaintingApplication.this.refresh();

            }
        });
        this.topPanel.add(paperDensitySpinner);

        SpinnerNumberModel pressureModel = new SpinnerNumberModel(
                this.strokePressure, 0.01, 100, .01);
        final JSpinner pressureSpinner = new JSpinner(pressureModel);
        JSpinner.NumberEditor pressureEditor = (JSpinner.NumberEditor) pressureSpinner
                .getEditor();
        pressureEditor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER);
        pressureSpinner.setPreferredSize(d);
        pressureSpinner.setBorder(BorderFactory
                .createTitledBorder("stroke pressure"));
        pressureSpinner.setToolTipText("distance between brush and paper");
        pressureSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                LinePaintingApplication.this.strokePressure = (Double) (pressureSpinner
                        .getValue());
                LinePaintingApplication.this.refresh();

            }
        });
        this.topPanel.add(pressureSpinner);
        SpinnerNumberModel sharpnessModel = new SpinnerNumberModel(
                this.sharpness, 0.0001, 10, .001);
        final JSpinner sharpnessSpinner = new JSpinner(sharpnessModel);
        JSpinner.NumberEditor sharpnessEditor = (JSpinner.NumberEditor) sharpnessSpinner
                .getEditor();
        sharpnessEditor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER);
        sharpnessSpinner.setPreferredSize(d);
        sharpnessSpinner.setBorder(BorderFactory
                .createTitledBorder("blending sharpness"));
        sharpnessSpinner
                .setToolTipText("blending contrast between brush and paper");
        sharpnessSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                LinePaintingApplication.this.sharpness = (Double) (sharpnessSpinner
                        .getValue());
                LinePaintingApplication.this.refresh();

            }
        });
        this.topPanel.add(sharpnessSpinner);

        SpinnerNumberModel pressureVariationAmplitudeModel = new SpinnerNumberModel(
                this.strokePressureVariationAmplitude, 0, 100, .1);
        final JSpinner pressureVariationAmplitudeSpinner = new JSpinner(
                pressureVariationAmplitudeModel);
        JSpinner.NumberEditor pressureVariationAmplitudeEditor = (JSpinner.NumberEditor) pressureVariationAmplitudeSpinner
                .getEditor();
        pressureVariationAmplitudeEditor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER);
        pressureVariationAmplitudeSpinner.setPreferredSize(d);
        pressureVariationAmplitudeSpinner.setBorder(BorderFactory
                .createTitledBorder("pressure amplitude"));

        pressureVariationAmplitudeSpinner
                .addChangeListener(new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent e) {
                        LinePaintingApplication.this.strokePressureVariationAmplitude = (Double) (pressureVariationAmplitudeSpinner
                                .getValue());
                        LinePaintingApplication.this.refresh();

                    }
                });
        this.topPanel.add(pressureVariationAmplitudeSpinner);

        SpinnerNumberModel pressureVariationFrequencyModel = new SpinnerNumberModel(
                this.strokePressureVariationFrequency, 0.001, 1000, 0.025);
        final JSpinner pressureVariationFrequencySpinner = new JSpinner(
                pressureVariationFrequencyModel);
        JSpinner.NumberEditor pressureVariationFrequencyEditor = (JSpinner.NumberEditor) pressureVariationFrequencySpinner
                .getEditor();
        pressureVariationFrequencyEditor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER);
        pressureVariationFrequencySpinner.setPreferredSize(d);
        pressureVariationFrequencySpinner.setBorder(BorderFactory
                .createTitledBorder("pressure frequency"));

        pressureVariationFrequencySpinner
                .addChangeListener(new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent e) {
                        LinePaintingApplication.this.strokePressureVariationFrequency = (Double) (pressureVariationFrequencySpinner
                                .getValue());
                        LinePaintingApplication.this.refresh();

                    }
                });
        this.topPanel.add(pressureVariationFrequencySpinner);

        SpinnerNumberModel shiftVariationAmplitudeModel = new SpinnerNumberModel(
                this.strokeShiftVariationAmplitude, 0, 1, .01);
        final JSpinner shiftVariationAmplitudeSpinner = new JSpinner(
                shiftVariationAmplitudeModel);
        JSpinner.NumberEditor shiftVariationAmplitudeEditor = (JSpinner.NumberEditor) shiftVariationAmplitudeSpinner
                .getEditor();
        shiftVariationAmplitudeEditor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER);
        shiftVariationAmplitudeSpinner.setPreferredSize(d);
        shiftVariationAmplitudeSpinner.setBorder(BorderFactory
                .createTitledBorder("shift amplitude"));

        shiftVariationAmplitudeSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                LinePaintingApplication.this.strokeShiftVariationAmplitude = (Double) (shiftVariationAmplitudeSpinner
                        .getValue());
                LinePaintingApplication.this.refresh();

            }
        });
        this.topPanel.add(shiftVariationAmplitudeSpinner);

        SpinnerNumberModel shiftVariationFrequencyModel = new SpinnerNumberModel(
                this.strokeShiftVariationFrequency, 0.001, 1000, 0.025);
        final JSpinner shiftVariationFrequencySpinner = new JSpinner(
                shiftVariationFrequencyModel);
        JSpinner.NumberEditor shiftVariationFrequencyEditor = (JSpinner.NumberEditor) shiftVariationFrequencySpinner
                .getEditor();
        shiftVariationFrequencyEditor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER);
        shiftVariationFrequencySpinner.setPreferredSize(d);
        shiftVariationFrequencySpinner.setBorder(BorderFactory
                .createTitledBorder("shift frequency"));

        shiftVariationFrequencySpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                LinePaintingApplication.this.strokeShiftVariationFrequency = (Double) (shiftVariationFrequencySpinner
                        .getValue());
                LinePaintingApplication.this.refresh();

            }
        });
        this.topPanel.add(shiftVariationFrequencySpinner);

        SpinnerNumberModel thicknessVariationAmplitudeModel = new SpinnerNumberModel(
                this.strokeThicknessVariationAmplitude, 0, 1, .01);
        final JSpinner thicknessVariationAmplitudeSpinner = new JSpinner(
                thicknessVariationAmplitudeModel);
        JSpinner.NumberEditor thicknessVariationAmplitudeEditor = (JSpinner.NumberEditor) thicknessVariationAmplitudeSpinner
                .getEditor();
        thicknessVariationAmplitudeEditor.getTextField()
                .setHorizontalAlignment(SwingConstants.CENTER);
        thicknessVariationAmplitudeSpinner.setPreferredSize(d);
        thicknessVariationAmplitudeSpinner.setBorder(BorderFactory
                .createTitledBorder("thickness amplitude"));

        thicknessVariationAmplitudeSpinner
                .addChangeListener(new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent e) {
                        LinePaintingApplication.this.strokeThicknessVariationAmplitude = (Double) (thicknessVariationAmplitudeSpinner
                                .getValue());
                        LinePaintingApplication.this.refresh();

                    }
                });
        this.topPanel.add(thicknessVariationAmplitudeSpinner);

        SpinnerNumberModel thicknessVariationFrequencyModel = new SpinnerNumberModel(
                this.strokeThicknessVariationFrequency, 0.001, 1000, 0.025);
        final JSpinner thicknessVariationFrequencySpinner = new JSpinner(
                thicknessVariationFrequencyModel);
        JSpinner.NumberEditor thicknessVariationFrequencyEditor = (JSpinner.NumberEditor) thicknessVariationFrequencySpinner
                .getEditor();
        thicknessVariationFrequencyEditor.getTextField()
                .setHorizontalAlignment(SwingConstants.CENTER);
        thicknessVariationFrequencySpinner.setPreferredSize(d);
        thicknessVariationFrequencySpinner.setBorder(BorderFactory
                .createTitledBorder("thickness frequency"));

        thicknessVariationFrequencySpinner
                .addChangeListener(new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent e) {
                        LinePaintingApplication.this.strokeThicknessVariationFrequency = (Double) (thicknessVariationFrequencySpinner
                                .getValue());
                        LinePaintingApplication.this.refresh();

                    }
                });
        this.topPanel.add(thicknessVariationFrequencySpinner);

        this.bottomPanel = new JPanel();
        this.bottomPanel.setBorder(BorderFactory
                .createEtchedBorder(EtchedBorder.LOWERED));
        this.frame.getContentPane().add(this.bottomPanel, BorderLayout.SOUTH);

    }

    /**
     * Load a shape file
     * 
     * @param shapeFilename
     * @return
     * @throws FileNotFoundException
     * @throws JAXBException
     */
    public String loadShapeFile(String shapeFilename)
            throws FileNotFoundException, JAXBException {
        File file = new File(shapeFilename);

        if (file != null) {
            String fileName = file.getAbsolutePath();
            String extention = fileName
                    .substring(fileName.lastIndexOf('.') + 1);
            LayerFactory factory = new LayerFactory(this.sld);
            Layer l = null;
            if (extention.equalsIgnoreCase("shp")) { //$NON-NLS-1$
                l = factory.createLayer(fileName, LayerType.SHAPEFILE);
            } else if (extention.equalsIgnoreCase("tif")) { //$NON-NLS-1$
                l = factory.createLayer(fileName, LayerType.GEOTIFF);
            } else if (extention.equalsIgnoreCase("asc")) { //$NON-NLS-1$
                l = factory.createLayer(fileName, LayerType.ASC);
            } else if (extention.equalsIgnoreCase("txt")) { //$NON-NLS-1$
                l = factory.createLayer(fileName, LayerType.TXT);
            }
            this.sld = new StyledLayerDescriptor();
            if (l != null) {
                this.sld.add(l);
                this.refresh();
            }
            try { // UGLY hack to wait for asynchronous shape loading to finish
                  // (hopefully)
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            for (Layer layer : this.sld.getLayers()) {

                for (IFeature feature : layer.getFeatureCollection()
                        .getElements()) {
                    IGeometry geom = feature.getGeom();
                    if (geom.isMultiCurve()) {
                        IMultiCurve<ICurve> multiCurve = (IMultiCurve<ICurve>) geom;
                        // System.err.println("add multicurve from feature "
                        // + feature.getId());
                        for (ICurve curve : multiCurve.getList()) {
                            ILineString lineString = curve
                                    .asLineString(0, 0, 0);
                            this.lines.add(lineString);
                        }
                    } else if (geom.isLineString()) {
                        this.lines.add((ILineString) geom);
                    } else {
                        logger.warn("geometry type "
                                + geom.getClass().getSimpleName()
                                + " is ignored");
                    }
                }
            }
            this.envelope = null;
            for (ILineString line : this.lines) {
                // System.err
                // .println("line ----------------------------------------");
                for (IDirectPosition p : line.getControlPoint()) {
                    // System.err.println("\tp: " + p.toString());
                    if (this.envelope == null) {
                        this.envelope = new GM_Envelope(p, p);
                    } else {
                        this.envelope.expand(p);
                    }
                }
            }

        }
        this.frame.repaint();
        return "shape file " + shapeFilename + " loaded: " + this.lines.size()
                + " lines";
    }

    /**
     * Convert the loaded shapefile to lines
     */
    private static Collection<? extends ParameterizedLine> convertSLDToLines(
            StyledLayerDescriptor sld) {
        List<ParameterizedLine> lines = new ArrayList<ParameterizedLine>();

        for (Layer layer : sld.getLayers()) {
            lines.addAll(toParameterizedLine(layer));

        }
        return lines;
    }

    private static Collection<? extends ParameterizedLine> toParameterizedLine(
            Layer layer) {
        List<ParameterizedLine> lines = new ArrayList<ParameterizedLine>();
        for (IFeature feature : layer.getFeatureCollection().getElements()) {
            lines.addAll(toParameterizedLine(feature));
        }
        return lines;
    }

    private static Collection<? extends ParameterizedLine> toParameterizedLine(
            IFeature feature) {
        return toParameterizedLine(feature.getGeom());
    }

    private static Collection<? extends ParameterizedLine> toParameterizedLine(
            IGeometry geom) {
        List<ParameterizedLine> lines = new ArrayList<ParameterizedLine>();
        if (geom.isMultiCurve()) {
            IMultiCurve<IOrientableCurve> multiCurve = (IMultiCurve<IOrientableCurve>) geom;
            for (IOrientableCurve curve : multiCurve.getList()) {
                lines.add(toParameterizedLine(curve.coord()));
            }
        } else if (geom.isLineString()) {
            lines.add(toParameterizedLine(geom.coord()));
        } else {
            logger.warn("geometry type " + geom.getClass().getSimpleName()
                    + " is ignored");
        }
        return lines;
    }

    private static ParameterizedLine toParameterizedLine(
            IDirectPositionList coords) {
        ParameterizedLine line = new ParameterizedLine();
        IDirectPosition p0 = null;
        double t = 0;
        for (int index = 0; index < coords.size(); index++) {
            IDirectPosition p1 = coords.get(index);
            if (p0 != null) {
                double segmentLength = p0.distance(p1);
                ParameterizedPoint pt0 = new ParameterizedPoint(p0.getX(),
                        p0.getY(), t, 0);
                ParameterizedPoint pt1 = new ParameterizedPoint(p0.getX(),
                        p0.getY(), t + segmentLength, 0);
                ParameterizedSegment segment = new ParameterizedSegment(pt0,
                        pt1);
                line.addSegment(segment);
                t += segmentLength;

            }
            p0 = p1;
        }
        return line;
    }

    /**
     * @return the lines
     */
    public List<ILineString> getLines() {
        return this.lines;
    }

    public static GLContext getGL4Context() throws GLException {
        GLContext glContext = new GLContext();

        int paintVertexShader = GLProgram
                .createVertexShader("/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/test/app/paint.vert.glsl");
        int paintFragmentShader = GLProgram
                .createFragmentShader("/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/test/app/paint.frag.glsl");
        glContext.addProgram(createPaintProgram(paintProgramName,
                paintVertexShader, paintFragmentShader));
        paintVertexShader = GLProgram
                .createVertexShader("/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/test/app/paint.vert.glsl");
        paintFragmentShader = GLProgram
                .createFragmentShader("/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/test/app/paint-basic.frag.glsl");
        glContext.addProgram(createPaintProgram(basicProgramName,
                paintVertexShader, paintFragmentShader));
        return glContext;
    }

    /**
     * @throws GLException
     */
    private static GLProgram createPaintProgram(String paintProgramName,
            int basicVertexShader, int basicFragmentShader) throws GLException {
        // basic program
        GLProgram paintProgram = new GLProgram(paintProgramName);
        paintProgram.setVertexShader(basicVertexShader);
        paintProgram.setFragmentShader(basicFragmentShader);
        paintProgram.addInputLocation(
                GLPaintingVertex.vertexPositionVariableName,
                GLPaintingVertex.vertexPositionLocation);
        paintProgram.addInputLocation(GLPaintingVertex.vertexUVVariableName,
                GLPaintingVertex.vertexUVLocation);
        paintProgram.addInputLocation(
                GLPaintingVertex.vertexNormalVariableName,
                GLPaintingVertex.vertexNormalLocation);
        paintProgram.addInputLocation(
                GLPaintingVertex.vertexCurvatureVariableName,
                GLPaintingVertex.vertexCurvatureLocation);
        paintProgram.addInputLocation(
                GLPaintingVertex.vertexThicknessVariableName,
                GLPaintingVertex.vertexThicknessLocation);
        paintProgram.addInputLocation(GLPaintingVertex.vertexColorVariableName,
                GLPaintingVertex.vertexColorLocation);
        paintProgram.addInputLocation(GLPaintingVertex.vertexMaxUVariableName,
                GLPaintingVertex.vertexMaxULocation);
        paintProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m02ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m11ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m12ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(screenWidthUniformVarName);
        paintProgram.addUniform(screenHeightUniformVarName);
        paintProgram.addUniform(paperTextureUniformVarName);
        paintProgram.addUniform(brushTextureUniformVarName);
        paintProgram.addUniform(mapScaleDiv1000UniformVarName);
        paintProgram.addUniform(brushWidthUniformVarName);
        paintProgram.addUniform(brushHeightUniformVarName);
        paintProgram.addUniform(brushStartWidthUniformVarName);
        paintProgram.addUniform(brushEndWidthUniformVarName);
        paintProgram.addUniform(brushScaleUniformVarName);
        paintProgram.addUniform(paperScaleUniformVarName);
        paintProgram.addUniform(paperDensityUniformVarName);
        paintProgram.addUniform(brushDensityUniformVarName);
        paintProgram.addUniform(strokePressureUniformVarName);
        paintProgram.addUniform(sharpnessUniformVarName);
        paintProgram.addUniform(strokePressureVariationAmplitudeUniformVarName);
        paintProgram.addUniform(strokePressureVariationFrequencyUniformVarName);
        paintProgram.addUniform(strokeShiftVariationAmplitudeUniformVarName);
        paintProgram.addUniform(strokeShiftVariationFrequencyUniformVarName);
        paintProgram
                .addUniform(strokeThicknessVariationAmplitudeUniformVarName);
        paintProgram
                .addUniform(strokeThicknessVariationFrequencyUniformVarName);

        return paintProgram;
    }

    // /**
    // * @throws GLException
    // */
    // private static GLProgram createLineProgram(String paintProgramName,
    // int basicVertexShader, int basicFragmentShader) throws GLException {
    // // basic program
    // GLProgram paintProgram = new GLProgram(paintProgramName);
    // paintProgram.setVertexShader(basicVertexShader);
    // paintProgram.setFragmentShader(basicFragmentShader);
    // paintProgram.addInputLocation(
    // GLPaintingVertex.vertexPositionVariableName,
    // GLPaintingVertex.vertexPositionLocation);
    // paintProgram.addInputLocation(GLPaintingVertex.vertexUVVariableName,
    // GLPaintingVertex.vertexUVLocation);
    // paintProgram.addInputLocation(
    // GLPaintingVertex.vertexNormalVariableName,
    // GLPaintingVertex.vertexNormalLocation);
    // paintProgram.addInputLocation(
    // GLPaintingVertex.vertexCurvatureVariableName,
    // GLPaintingVertex.vertexCurvatureLocation);
    // paintProgram.addInputLocation(
    // GLPaintingVertex.vertexThicknessVariableName,
    // GLPaintingVertex.vertexThicknessLocation);
    // paintProgram.addInputLocation(GLPaintingVertex.vertexColorVariableName,
    // GLPaintingVertex.vertexColorLocation);
    // paintProgram.addInputLocation(GLPaintingVertex.vertexMaxUVariableName,
    // GLPaintingVertex.vertexMaxULocation);
    // paintProgram.addUniform(m00ModelToViewMatrixUniformVarName);
    // paintProgram.addUniform(m02ModelToViewMatrixUniformVarName);
    // paintProgram.addUniform(m00ModelToViewMatrixUniformVarName);
    // paintProgram.addUniform(m11ModelToViewMatrixUniformVarName);
    // paintProgram.addUniform(m12ModelToViewMatrixUniformVarName);
    // paintProgram.addUniform(screenWidthUniformVarName);
    // paintProgram.addUniform(screenHeightUniformVarName);
    // paintProgram.addUniform(paperTextureUniformVarName);
    // paintProgram.addUniform(brushTextureUniformVarName);
    // paintProgram.addUniform(mapScaleDiv1000UniformVarName);
    // paintProgram.addUniform(brushWidthUniformVarName);
    // paintProgram.addUniform(brushHeightUniformVarName);
    // paintProgram.addUniform(brushStartWidthUniformVarName);
    // paintProgram.addUniform(brushEndWidthUniformVarName);
    // paintProgram.addUniform(brushScaleUniformVarName);
    // paintProgram.addUniform(paperScaleUniformVarName);
    // paintProgram.addUniform(paperDensityUniformVarName);
    // paintProgram.addUniform(brushDensityUniformVarName);
    // paintProgram.addUniform(strokePressureUniformVarName);
    // return paintProgram;
    // }

    public void reloadShaders() {
    }

    public IEnvelope getEnvelope() {
        return this.envelope;
    }

    /**
     * 
     */
    private void refresh() {
        this.canvas.updateViewport();
        this.canvas.repaint();
    }

}
