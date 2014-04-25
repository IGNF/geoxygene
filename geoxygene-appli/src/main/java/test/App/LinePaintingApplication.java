package test.App;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
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
    private static final String LAST_DIRECTORY = "painter.app.lastDirectory";

    private JFrame frame = null;
    private JPanel topPanel = null;
    private JPanel bottomPanel = null;
    private LinePaintingGLCanvas canvas = null;
    private final String shapeFilename = "/export/home/kandinsky/turbet/geodata/basic-tests/line.5.points.open.shp";
    private final List<ILineString> lines = new ArrayList<ILineString>();
    private IEnvelope envelope = null;
    boolean wireframe = false;

    public final StyledLayerDescriptor sld = new StyledLayerDescriptor();

    public static final String m00ModelToViewMatrixUniformVarName = "m00";
    public static final String m02ModelToViewMatrixUniformVarName = "m02";
    public static final String m11ModelToViewMatrixUniformVarName = "m11";
    public static final String m12ModelToViewMatrixUniformVarName = "m12";
    public static final String screenWidthUniformVarName = "screenWidth";
    public static final String screenHeightUniformVarName = "screenHeight";

    public LinePaintingApplication() throws Exception {
        this.initializeGui();
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.loadShapeFile(this.shapeFilename);
    }

    public void run() {
        this.frame.setSize(800, 600);
        this.frame.setVisible(true);
    }

    public static void main(String[] args) {
        LinePaintingApplication app;
        try {
            app = new LinePaintingApplication();
            app.run();
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    /**
     * @return the sld
     */
    public StyledLayerDescriptor getSld() {
        return this.sld;
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
        this.topPanel.setBorder(BorderFactory
                .createEtchedBorder(EtchedBorder.LOWERED));
        this.frame.getContentPane().add(this.topPanel, BorderLayout.NORTH);
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
        this.topPanel.add(quitButton);
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
        this.topPanel.add(drawButton);
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
            if (l != null) {
                this.sld.remove(this.sld.getLayers());
                this.sld.add(l);
                this.updateContent();
            }
            for (Layer layer : this.sld.getLayers()) {
                for (IFeature feature : layer.getFeatureCollection()
                        .getElements()) {
                    IGeometry geom = feature.getGeom();
                    if (geom.isMultiCurve()) {
                        IMultiCurve<ICurve> multiCurve = (IMultiCurve<ICurve>) geom;
                        for (ICurve curve : multiCurve.getList()) {
                            this.lines.add(curve.asLineString(0, 0, 0));
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
                for (IDirectPosition p : line.getControlPoint()) {
                    if (this.envelope == null) {
                        this.envelope = new GM_Envelope(p, p);
                    } else {
                        this.envelope.expand(p);
                    }
                }
            }

        }
        this.frame.repaint();
        return "shape file " + shapeFilename + " loaded";
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

    private void updateContent() {
        this.canvas.repaint();

    }

    public static GLContext getGL4Context() throws GLException {
        GLContext glContext = new GLContext();

        int paintVertexShader = GLProgram
                .createVertexShader("/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/test/app/paint.vert.glsl");
        int paintFragmentShader = GLProgram
                .createFragmentShader("/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/test/app/paint.frag.glsl");
        glContext.addProgram(createPaintProgram(paintProgramName,
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
        paintProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m02ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m11ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m12ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(screenWidthUniformVarName);
        paintProgram.addUniform(screenHeightUniformVarName);

        return paintProgram;
    }

    public IEnvelope getEnvelope() {
        return this.envelope;
    }

    /**
     * 
     */
    private void refresh() {
        this.canvas.repaint();
    }

}
