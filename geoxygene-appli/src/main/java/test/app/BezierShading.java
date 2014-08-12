/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package test.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
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
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.util.ui.SliderWithSpinner;
import fr.ign.util.ui.SliderWithSpinner.SliderWithSpinnerModel;

/**
 * @author JeT
 * 
 */
public class BezierShading {

    private static final Logger logger = Logger.getLogger(BezierShading.class
            .getName()); // logger

    private static final String LAST_DIRECTORY = "bezier.shading.lastDirectory";
    private final Preferences prefs = Preferences.userRoot();
    private final JFrame frame = new JFrame("BezierShading");
    private JPanel topPanel = null;
    private final JPanel bottomPanel = null;
    private String shapeFilename = "./src/main/resources/test/app/shapes/troncon_route_25.shp";
    // private String shapeFilename =
    // "/export/home/kandinsky/turbet/geodata/basic-tests/line.5.points.open.shp";

    private BezierShadingGLCanvas canvas = null;
    public boolean wireframe = false;
    public double lineWidth = 2.;
    public double transitionSize = 2.;
    public boolean drawLines = false;
    private final List<ILineString> lines = new ArrayList<ILineString>();
    private IEnvelope envelope = null;
    public StyledLayerDescriptor sld = new StyledLayerDescriptor();

    /**
     * @throws LWJGLException
     * 
     */
    public BezierShading() throws LWJGLException {
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.canvas = new BezierShadingGLCanvas(this);
        this.frame.getContentPane().add(this.canvas);
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
                new ImageIcon(BezierShading.class
                        .getResource("/images/icons/16x16/exit-power-quit.png")));
        quitButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        quitButton.setToolTipText("Exit application");
        quitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BezierShading.this.frame.dispose();
            }
        });
        buttonPanel.add(quitButton);
        JButton reloadButton = new JButton(new ImageIcon(
                BezierShading.class
                        .getResource("/images/icons/16x16/refresh.png")));
        reloadButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        reloadButton.setToolTipText("Reload shaders");
        reloadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    BezierShading.this.canvas.reloadContext();
                    BezierShading.this.refresh();
                } catch (GLException e1) {
                    JOptionPane.showMessageDialog(BezierShading.this.frame,
                            e1.getMessage());
                    e1.printStackTrace();
                }
            }
        });
        buttonPanel.add(reloadButton);
        JButton loadButton = new JButton(
                new ImageIcon(
                        BezierShading.class
                                .getResource("/images/icons/16x16/plus.png")));
        loadButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        loadButton.setToolTipText("Open Shape File");
        loadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser(BezierShading.this.prefs
                        .get(LAST_DIRECTORY, "."));
                if (fc.showOpenDialog(BezierShading.this.frame) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File selectedFile = fc.getSelectedFile();
                        BezierShading.this.shapeFilename = selectedFile
                                .getAbsolutePath();
                        BezierShading.this.prefs.put(LAST_DIRECTORY,
                                selectedFile.getAbsolutePath());
                        JOptionPane
                                .showMessageDialog(
                                        BezierShading.this.frame,
                                        BezierShading.this
                                                .loadShapeFile(BezierShading.this.shapeFilename));
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(BezierShading.this.frame,
                                e1.getMessage());
                        e1.printStackTrace();
                    }
                }
            }

        });
        buttonPanel.add(loadButton);

        JButton drawButton = new JButton(new ImageIcon(
                BezierShading.class
                        .getResource("/images/icons/16x16/wireframe.png")));
        drawButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        drawButton.setToolTipText("Toggle Wireframe/Plain rendering");
        drawButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BezierShading.this.wireframe = !BezierShading.this.wireframe;
                BezierShading.this.refresh();
            }
        });
        buttonPanel.add(drawButton);
        JButton linesButton = new JButton(new ImageIcon(
                BezierShading.class
                        .getResource("/images/icons/16x16/move2.png")));
        linesButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        linesButton.setToolTipText("Toggle Line rendering");
        linesButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BezierShading.this.drawLines = !BezierShading.this.drawLines;
                BezierShading.this.refresh();
            }
        });
        buttonPanel.add(linesButton);

        this.topPanel.add(buttonPanel);

        SliderWithSpinnerModel lineWidthModel = new SliderWithSpinnerModel(
                this.lineWidth, 0.1, 100., 1.);
        final SliderWithSpinner lineWidthSpinner = new SliderWithSpinner(
                lineWidthModel);
        JSpinner.NumberEditor lineWidthEditor = (JSpinner.NumberEditor) lineWidthSpinner
                .getEditor();
        lineWidthEditor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER);
        lineWidthSpinner.setBorder(BorderFactory
                .createTitledBorder("line width"));
        lineWidthSpinner.setToolTipText("width of the line");
        lineWidthSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                BezierShading.this.lineWidth = (lineWidthSpinner.getValue());
                BezierShading.this.refresh();

            }
        });
        this.topPanel.add(lineWidthSpinner);

        SliderWithSpinnerModel transitionSizeModel = new SliderWithSpinnerModel(
                this.transitionSize, 0.1, 100., 1.);
        final SliderWithSpinner transitionSizeSpinner = new SliderWithSpinner(
                transitionSizeModel);
        JSpinner.NumberEditor transitionSizeEditor = (JSpinner.NumberEditor) transitionSizeSpinner
                .getEditor();
        transitionSizeEditor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER);
        transitionSizeSpinner.setBorder(BorderFactory
                .createTitledBorder("transition size"));
        transitionSizeSpinner
                .setToolTipText("if two consecutive segments are greater than this size, use a bezier transition");
        transitionSizeSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                BezierShading.this.transitionSize = (transitionSizeSpinner
                        .getValue());
                BezierShading.this.refresh();

            }
        });
        this.topPanel.add(transitionSizeSpinner);
        this.frame.setSize(800, 600);

        try {
            this.loadShapeFile(this.shapeFilename);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (JAXBException e1) {
            e1.printStackTrace();
        }
    }

    protected void refresh() {
        this.canvas.repaint();
    }

    public void run() {
        this.frame.setVisible(true);
    }

    public static void main(String[] args) throws LWJGLException {
        BezierShading app = new BezierShading();
        app.run();
        try {
            // JOptionPane.showMessageDialog(app.frame,
            // app.loadShapeFile(app.shapeFilename));
            app.loadShapeFile(app.shapeFilename);
            app.canvas.setViewport(app.getEnvelope());
            app.refresh();
        } catch (FileNotFoundException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } catch (JAXBException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

    }

    /**
     * @return the envelope
     */
    public IEnvelope getEnvelope() {
        return this.envelope;
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
            logger.info("Read file " + fileName);
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
            logger.info("envelope = " + this.envelope);

        }
        this.canvas.setViewport(this.envelope);
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

}
