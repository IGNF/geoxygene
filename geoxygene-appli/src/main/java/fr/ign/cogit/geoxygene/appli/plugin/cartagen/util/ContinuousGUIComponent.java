/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.xml.parsers.ParserConfigurationException;

import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.shapefile.shp.ShapefileReader.Record;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.cartagen.algorithms.polygon.VisvalingamWhyatt;
import fr.ign.cogit.cartagen.continuous.BasicMorphing;
import fr.ign.cogit.cartagen.continuous.LeastSquaresMorphing;
import fr.ign.cogit.cartagen.continuous.MorphingVertexMapping;
import fr.ign.cogit.cartagen.continuous.discontinuities.CoalescenceFunction;
import fr.ign.cogit.cartagen.continuous.discontinuities.DiscontinuitiesMeasure;
import fr.ign.cogit.cartagen.continuous.discontinuities.FrechetDistanceFunction;
import fr.ign.cogit.cartagen.continuous.discontinuities.HausdorffDistanceFunction;
import fr.ign.cogit.cartagen.continuous.discontinuities.MeanLineDistanceFunction;
import fr.ign.cogit.cartagen.continuous.discontinuities.NbOfBendsFunction;
import fr.ign.cogit.cartagen.continuous.discontinuities.TopologyValidationFunction;
import fr.ign.cogit.cartagen.continuous.optcor.OptCorMorphing;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.dataset.geompool.GeometryPool;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SinuousSection;
import fr.ign.cogit.geoxygene.api.feature.IDataSet;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.panel.ShapeFileFilter;
import fr.ign.cogit.geoxygene.appli.panel.XMLFileFilter;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.LoadSelectionFrame;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.ObjectSelection;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.TronconDeRouteImpl;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

/**
 * Extra menu that contains utility functions of CartAGen.
 * 
 * @author GTouya
 * 
 */
public class ContinuousGUIComponent extends JMenu {

  private GeOxygeneApplication appli;
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static Set<SinuousSection> sinuousSections = new HashSet<>();

  public ContinuousGUIComponent(GeOxygeneApplication appli, String title) {
    super(title);
    this.appli = appli;
    JMenu loadingMenu = new JMenu("Loading");
    loadingMenu.add(new JMenuItem(new LoadBendSeriesAction()));
    JMenu morphingMenu = new JMenu("Morphing");
    this.add(morphingMenu);
    morphingMenu.add(new JMenuItem(new BasicMorphingAction()));
    morphingMenu.addSeparator();
    morphingMenu.add(new JMenuItem(new OptCorMorphingAction()));
    morphingMenu.add(new JMenuItem(new OptCorMatchingAction()));
    morphingMenu.add(new JMenuItem(new OptCorMorph2FeatsAction()));
    morphingMenu.addSeparator();
    morphingMenu.add(new JMenuItem(new LSMorphingAction()));
    morphingMenu.add(new JMenuItem(new LSMorph2FeatsAction()));
    // discontinuities menu
    JMenu discontMenu = new JMenu("Discontinuity detection");
    discontMenu.add(new JMenuItem(new MeasureDiscontOptCorAction()));
    discontMenu.add(new JMenuItem(new MeasureDiscontSelAction()));
    this.add(discontMenu);
    // piecewise continuity menu
    JMenu piecewiseMenu = new JMenu("Piecewise continuity");
    this.add(piecewiseMenu);
  }

  public static Set<SinuousSection> getSinuousSections() {
    return sinuousSections;
  }

  /**
   * Test the basic morphing on the selected object and a simplified version of
   * the object. Morphed and simplified geometries are displayed in the geometry
   * pool.
   * 
   * @author GTouya
   * 
   */
  class BasicMorphingAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      GeometryPool pool = dataset.getGeometryPool();
      pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());

      IFeature feat = SelectionUtil.getFirstSelectedObject(appli);
      VisvalingamWhyatt algo = new VisvalingamWhyatt(1000.0);
      if (feat.getGeom() instanceof IPolygon) {
        IPolygon simplified = algo.simplify((IPolygon) feat.getGeom());
        pool.addFeatureToGeometryPool(simplified.exteriorLineString(),
            Color.BLUE, 3);
        BasicMorphing morphing = new BasicMorphing(
            ((IPolygon) feat.getGeom()).exteriorLineString(),
            simplified.exteriorLineString());
        ILineString morph1 = (ILineString) morphing
            .continuousGeneralisation(0.25);
        ILineString morph2 = (ILineString) morphing
            .continuousGeneralisation(0.5);
        ILineString morph3 = (ILineString) morphing
            .continuousGeneralisation(0.75);
        pool.addFeatureToGeometryPool(morph1, Color.YELLOW, 3);
        pool.addFeatureToGeometryPool(morph2, Color.ORANGE, 3);
        pool.addFeatureToGeometryPool(morph3, Color.RED, 3);
      } else if (feat.getGeom() instanceof ILineString) {
        ILineString simplified = algo.simplify((ILineString) feat.getGeom());
        pool.addFeatureToGeometryPool(simplified, Color.BLUE, 3);
        BasicMorphing morphing = new BasicMorphing(
            ((ILineString) feat.getGeom()), simplified);
        ILineString morph1 = (ILineString) morphing
            .continuousGeneralisation(0.25);

        ILineString morph2 = (ILineString) morphing
            .continuousGeneralisation(0.5);
        ILineString morph3 = (ILineString) morphing
            .continuousGeneralisation(0.75);

        pool.addFeatureToGeometryPool(morph1, Color.YELLOW, 3);
        pool.addFeatureToGeometryPool(morph2, Color.ORANGE, 3);
        pool.addFeatureToGeometryPool(morph3, Color.RED, 3);
      }
    }

    public BasicMorphingAction() {
      putValue(Action.NAME, "Basic morphing on selection");
    }
  }

  /**
   * Test the {@link OptCorMorphing} on the selected object and a simplified
   * version of the object. Morphed and simplified geometries are displayed in
   * the geometry pool.
   * 
   * @author GTouya
   * 
   */
  class OptCorMorphingAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      GeometryPool pool = dataset.getGeometryPool();
      pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());

      IFeature feat = SelectionUtil.getFirstSelectedObject(appli);
      VisvalingamWhyatt algo = new VisvalingamWhyatt(500.0);
      if (feat.getGeom() instanceof IPolygon) {
        IPolygon simplified = algo.simplify((IPolygon) feat.getGeom());
        pool.addFeatureToGeometryPool(simplified.exteriorLineString(),
            Color.BLUE, 3);
        OptCorMorphing morphing = new OptCorMorphing(
            ((IPolygon) feat.getGeom()).exteriorLineString(),
            simplified.exteriorLineString());
        ILineString morph1 = (ILineString) morphing
            .continuousGeneralisation(0.25);
        ILineString morph2 = (ILineString) morphing
            .continuousGeneralisation(0.5);
        ILineString morph3 = (ILineString) morphing
            .continuousGeneralisation(0.75);
        pool.addFeatureToGeometryPool(morph1, Color.YELLOW, 3);
        pool.addFeatureToGeometryPool(morph2, Color.ORANGE, 3);
        pool.addFeatureToGeometryPool(morph3, Color.RED, 3);
      } else if (feat.getGeom() instanceof ILineString) {
        ILineString simplified = algo.simplify((ILineString) feat.getGeom());
        pool.addFeatureToGeometryPool(simplified, Color.BLUE, 3);
        OptCorMorphing morphing = new OptCorMorphing(
            ((ILineString) feat.getGeom()), simplified);
        ILineString morph1 = (ILineString) morphing
            .continuousGeneralisation(0.25);

        ILineString morph2 = (ILineString) morphing
            .continuousGeneralisation(0.5);
        ILineString morph3 = (ILineString) morphing
            .continuousGeneralisation(0.75);

        pool.addFeatureToGeometryPool(morph1, Color.YELLOW, 3);
        pool.addFeatureToGeometryPool(morph2, Color.ORANGE, 3);
        pool.addFeatureToGeometryPool(morph3, Color.RED, 3);
      }
    }

    public OptCorMorphingAction() {
      putValue(Action.NAME, "OPTCOR morphing on selection");
    }
  }

  /**
   * Test the {@link OptCorMorphing} vertex matching on the selected object and
   * a simplified version of the object. A segment between matched vertices is
   * displayed in the geometry pool.
   * 
   * @author GTouya
   * 
   */
  class OptCorMatchingAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      GeometryPool pool = dataset.getGeometryPool();
      pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());

      IFeature feat = SelectionUtil.getFirstSelectedObject(appli);
      VisvalingamWhyatt algo = new VisvalingamWhyatt(500.0);
      if (feat.getGeom() instanceof ILineString) {
        ILineString simplified = algo.simplify((ILineString) feat.getGeom());
        pool.addFeatureToGeometryPool(simplified, Color.BLUE, 3);
        OptCorMorphing morphing = new OptCorMorphing(
            ((ILineString) feat.getGeom()), simplified);
        MorphingVertexMapping mapping = morphing.matchLinesVertices();

        double[][] distanceTable = morphing.getDistanceTable();
        Object[][] tableContent = new Object[morphing.getSubLinesIni().size()
            + 1][morphing.getSubLinesFin().size() + 1];
        for (int i = 0; i <= morphing.getSubLinesIni().size(); i++) {
          for (int j = 0; j <= morphing.getSubLinesFin().size(); j++) {
            tableContent[i][j] = distanceTable[i][j];
          }
        }

        Object[] headers = new String[morphing.getSubLinesFin().size() + 1];
        for (int i = 0; i < headers.length; i++)
          headers[i] = String.valueOf(i);
        TableModel tableModel = new DefaultTableModel(tableContent, headers);
        JTable table = new JTable(tableModel);
        JDialog dialog = new JDialog();
        dialog.add(table);
        dialog.setSize(500, 500);
        dialog.setVisible(true);

        for (IDirectPosition pt : mapping.getInitialCoords()) {
          IDirectPosition other = mapping.getMapping(pt);
          pool.addFeatureToGeometryPool(
              GeometryEngine.getFactory().createLineSegment(pt, other),
              Color.RED, 3);
        }
      }
    }

    public OptCorMatchingAction() {
      putValue(Action.NAME, "OPTCOR vertex matching on selection");
    }
  }

  /**
   * 
   * Test the {@link LeastSquaresMorphing} process on the selected object and a
   * simplified version of the object.
   * 
   * @author GTouya
   * 
   */
  class LSMorphingAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      GeometryPool pool = dataset.getGeometryPool();
      pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());

      IFeature feat = SelectionUtil.getFirstSelectedObject(appli);
      VisvalingamWhyatt algo = new VisvalingamWhyatt(500.0);
      if (feat.getGeom() instanceof ILineString) {
        ILineString simplified = algo.simplify((ILineString) feat.getGeom());
        pool.addFeatureToGeometryPool(simplified, Color.BLUE, 3);

        LeastSquaresMorphing LsMorphing = new LeastSquaresMorphing(
            (ILineString) feat.getGeom(), simplified);
        LsMorphing.setK(10);
        IGeometry morphed = LsMorphing.continuousGeneralisation(0.6);
        pool.addFeatureToGeometryPool(morphed, Color.RED, 3);

        for (ILineString line : LsMorphing.getIntermediateLines()) {
          pool.addFeatureToGeometryPool(line, Color.ORANGE, 1);
        }
      }
    }

    public LSMorphingAction() {
      putValue(Action.NAME, "Least squares morphing on selection");
    }
  }

  /**
   * 
   * Test the {@link LeastSquaresMorphing} process on two selected objects (the
   * first is the initial line).
   * 
   * @author GTouya
   * 
   */
  class LSMorph2FeatsAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent arg0) {
      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      StyledLayerDescriptor sld = appli.getMainFrame().getSelectedProjectFrame()
          .getSld();
      IDataSet<?> dataset2 = sld.getDataSet();
      GeometryPool pool = null;
      if (dataset == null) {
        pool = new GeometryPool(dataset2, sld);
      } else {
        pool = dataset.getGeometryPool();
      }
      pool.setSld(sld);

      Set<ILineString> layer1Feats = new HashSet<>();
      Set<ILineString> layer2Feats = new HashSet<>();
      String layer1 = sld.getLayers().get(0).getName();
      String layer2 = sld.getLayers().get(1).getName();
      for (IFeature feat : SelectionUtil.getSelectedObjects(appli, layer1)) {
        if (feat.getGeom() instanceof ILineString)
          layer1Feats.add((ILineString) feat.getGeom());
        else {
          IMultiCurve<IOrientableCurve> complex = (IMultiCurve<IOrientableCurve>) feat
              .getGeom();
          ILineString line = (ILineString) complex.get(0);
          layer1Feats.add(line);
        }
      }
      for (IFeature feat : SelectionUtil.getSelectedObjects(appli, layer2)) {
        if (feat.getGeom() instanceof ILineString)
          layer2Feats.add((ILineString) feat.getGeom());
        else {
          IMultiCurve<IOrientableCurve> complex = (IMultiCurve<IOrientableCurve>) feat
              .getGeom();
          ILineString line = (ILineString) complex.get(0);
          layer2Feats.add(line);
        }
      }

      // merge geometries
      ILineString merged1 = Operateurs.compileArcs(layer1Feats, 0.001);
      ILineString merged2 = Operateurs.compileArcs(layer2Feats, 0.001);

      pool.addFeatureToGeometryPool(merged1, Color.BLUE, 3);
      pool.addFeatureToGeometryPool(merged2, Color.GREEN, 3);

      LeastSquaresMorphing lsMorphing = new LeastSquaresMorphing(merged1,
          merged2);
      lsMorphing.setK(10);
      LeastSquaresMorphing lsMorphing2 = new LeastSquaresMorphing(merged2,
          merged1);
      lsMorphing2.setK(10);
      IGeometry morphed = lsMorphing.continuousGeneralisation(0.6);
      // IGeometry morphed2 = lsMorphing2.continuousGeneralisation(0.6);

      for (int i = 0; i < lsMorphing.getMapping().getInitialCoords()
          .size(); i++) {
        IDirectPosition startPoint = lsMorphing.getMapping().getInitialCoords()
            .get(i);
        IDirectPosition endPoint = lsMorphing.getMapping().getFinalCoords()
            .get(i);
        pool.addFeatureToGeometryPool(new GM_LineSegment(startPoint, endPoint),
            Color.GRAY, 1);
      }

      pool.addFeatureToGeometryPool(morphed, Color.RED, 3);

      for (ILineString line : lsMorphing.getIntermediateLines()) {
        pool.addFeatureToGeometryPool(line, Color.ORANGE, 1);
      }

      // pool.addFeatureToGeometryPool(morphed2, Color.MAGENTA, 3);
    }

    public LSMorph2FeatsAction() {
      putValue(Action.NAME, "Least squares morphing on two selected features");
    }
  }

  /**
   * 
   * Test the {@link OptCorMorphing} process on two selected objects (the first
   * is the initial line).
   * 
   * @author GTouya
   * 
   */
  class OptCorMorph2FeatsAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent arg0) {
      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      StyledLayerDescriptor sld = appli.getMainFrame().getSelectedProjectFrame()
          .getSld();
      IDataSet<?> dataset2 = sld.getDataSet();
      GeometryPool pool = null;
      if (dataset == null) {
        pool = new GeometryPool(dataset2, sld);
      } else {
        pool = dataset.getGeometryPool();
      }
      pool.setSld(sld);

      Set<ILineString> layer1Feats = new HashSet<>();
      Set<ILineString> layer2Feats = new HashSet<>();
      String layer1 = sld.getLayers().get(0).getName();
      String layer2 = sld.getLayers().get(1).getName();
      for (IFeature feat : SelectionUtil.getSelectedObjects(appli, layer1)) {
        if (feat.getGeom() instanceof ILineString)
          layer1Feats.add((ILineString) feat.getGeom());
        else {
          IMultiCurve<IOrientableCurve> complex = (IMultiCurve<IOrientableCurve>) feat
              .getGeom();
          ILineString line = (ILineString) complex.get(0);
          layer1Feats.add(line);
        }
      }
      for (IFeature feat : SelectionUtil.getSelectedObjects(appli, layer2)) {
        if (feat.getGeom() instanceof ILineString)
          layer2Feats.add((ILineString) feat.getGeom());
        else {
          IMultiCurve<IOrientableCurve> complex = (IMultiCurve<IOrientableCurve>) feat
              .getGeom();
          ILineString line = (ILineString) complex.get(0);
          layer2Feats.add(line);
        }
      }

      // merge geometries
      ILineString merged1 = Operateurs.compileArcs(layer1Feats, 0.001);
      ILineString merged2 = Operateurs.compileArcs(layer2Feats, 0.001);

      pool.addFeatureToGeometryPool(merged1, Color.BLUE, 3);
      pool.addFeatureToGeometryPool(merged2, Color.GREEN, 3);

      OptCorMorphing morphing = new OptCorMorphing(merged1, merged2);
      ILineString morph1 = (ILineString) morphing
          .continuousGeneralisation(0.25);

      ILineString morph2 = (ILineString) morphing.continuousGeneralisation(0.5);
      ILineString morph3 = (ILineString) morphing
          .continuousGeneralisation(0.75);

      pool.addFeatureToGeometryPool(morph1, Color.YELLOW, 3);
      pool.addFeatureToGeometryPool(morph2, Color.ORANGE, 3);
      pool.addFeatureToGeometryPool(morph3, Color.RED, 3);
    }

    public OptCorMorph2FeatsAction() {
      putValue(Action.NAME, "OptCor morphing on two selected features");
    }
  }

  /**
   * 
   * Measure the discontinuities for {@link OptCorMorphing} process on two
   * selected objects (the first is the initial line).
   * 
   * @author GTouya
   * 
   */
  class MeasureDiscontOptCorAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent arg0) {

      double iniScale = new Double(
          JOptionPane.showInputDialog("Enter initial scale", 25000.0));
      double finScale = new Double(
          JOptionPane.showInputDialog("Enter final scale", 250000.0));
      int nbScales = new Integer(JOptionPane
          .showInputDialog("Enter the number of intermediate scales", 100));

      JFileChooser fc = new JFileChooser("Save the measures to CSV file");
      int returnVal = fc.showSaveDialog(appli.getMainFrame().getGui());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File file = fc.getSelectedFile();

      StyledLayerDescriptor sld = appli.getMainFrame().getSelectedProjectFrame()
          .getSld();

      Set<ILineString> layer1Feats = new HashSet<>();
      Set<ILineString> layer2Feats = new HashSet<>();
      String layer1 = sld.getLayers().get(0).getName();
      String layer2 = sld.getLayers().get(1).getName();
      for (IFeature feat : SelectionUtil.getSelectedObjects(appli, layer1)) {
        if (feat.getGeom() instanceof ILineString)
          layer1Feats.add((ILineString) feat.getGeom());
        else {
          IMultiCurve<IOrientableCurve> complex = (IMultiCurve<IOrientableCurve>) feat
              .getGeom();
          ILineString line = (ILineString) complex.get(0);
          layer1Feats.add(line);
        }
      }
      for (IFeature feat : SelectionUtil.getSelectedObjects(appli, layer2)) {
        if (feat.getGeom() instanceof ILineString)
          layer2Feats.add((ILineString) feat.getGeom());
        else {
          IMultiCurve<IOrientableCurve> complex = (IMultiCurve<IOrientableCurve>) feat
              .getGeom();
          ILineString line = (ILineString) complex.get(0);
          layer2Feats.add(line);
        }
      }

      // merge geometries
      ILineString merged1 = Operateurs.compileArcs(layer1Feats, 0.001);
      ILineString merged2 = Operateurs.compileArcs(layer2Feats, 0.001);

      OptCorMorphing morphing = new OptCorMorphing(merged1, merged2);
      SortedMap<Double, IGeometry> continuousGeoms = new TreeMap<Double, IGeometry>();
      IFeatureCollection<IFeature> featureCollection = new FT_FeatureCollection<>();
      FeatureType featureType = new FeatureType();
      featureType.setNomClasse("continuous");
      GF_AttributeType attr = new AttributeType("scale", "scale", "Integer");
      featureType.addFeatureAttribute(attr);
      featureType.setGeometryType(ILineString.class);
      featureCollection.setFeatureType(featureType);
      SchemaDefaultFeature schema = new SchemaDefaultFeature();
      schema.addFeatureType(featureType);
      schema.getAttLookup().put(0, new String[] { "scale", "continuous" });
      double t = 0.0;
      double step = 1.0 / nbScales;
      t += step;
      double scaleDiff = finScale - iniScale;
      while (t < 1.0) {
        double scale = scaleDiff * t + iniScale;
        ILineString morph = (ILineString) morphing.continuousGeneralisation(t);
        continuousGeoms.put(scale, morph);
        System.out.println("t= " + t);
        t += step;
        // create a default feature with the geometry to export it
        DefaultFeature feat = new DefaultFeature(morph);
        feat.setAttributes(new Object[1]);
        feat.setFeatureType(featureType);
        feat.setSchema(schema);
        feat.setAttribute(attr, (int) Math.round(scale));
        featureCollection.add(feat);
      }
      DiscontinuitiesMeasure measure = new DiscontinuitiesMeasure(
          continuousGeoms);
      measure.addLegibilityFunction(new CoalescenceFunction(0.8));
      measure.addLegibilityFunction(new TopologyValidationFunction());
      measure.addDistanceFunction(new HausdorffDistanceFunction("Hausdorff"));
      measure.addDistanceFunction(
          new MeanLineDistanceFunction("Mean line distance"));
      measure
          .addDistanceFunction(new FrechetDistanceFunction("Fréchet distance"));
      measure.computeDiscontinuities();
      try {
        measure.writeToCsv(file);
      } catch (IOException e) {
        e.printStackTrace();
      }

      // write the continuous geometries in a shapefile
      ShapefileWriter.chooseAndWriteShapefile(featureCollection);
    }

    public MeasureDiscontOptCorAction() {
      putValue(Action.SHORT_DESCRIPTION,
          "Measure discontinuities for OptCor morphing on two selected features");
      putValue(Action.NAME, "Measure discontinuities for OptCor morphing");
    }
  }

  /**
   * 
   * Measure the discontinuities for {@link OptCorMorphing} process on a list of
   * selected lines stored in an XML file.
   * 
   * @author GTouya
   * 
   */
  class MeasureDiscontSelAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent arg0) {

      double iniScale = new Double(
          JOptionPane.showInputDialog("Enter initial scale", 10000.0));
      double finScale = new Double(
          JOptionPane.showInputDialog("Enter final scale", 250000.0));
      int nbScales = new Integer(JOptionPane
          .showInputDialog("Enter the number of intermediate scales", 100));

      JFileChooser fc = new JFileChooser("Select the file with selected lines");
      fc.setFileFilter(new XMLFileFilter());
      int returnVal = fc.showOpenDialog(appli.getMainFrame().getGui());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File selectionFile = fc.getSelectedFile();

      JFileChooser fc2 = new JFileChooser(
          "Select a directory for output files");
      fc2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      returnVal = fc2.showSaveDialog(appli.getMainFrame().getGui());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File directory = fc2.getSelectedFile();

      StyledLayerDescriptor sld = appli.getMainFrame().getSelectedProjectFrame()
          .getSld();

      // get the ObjectSelection by parsing the file
      try {
        List<ObjectSelection> selections = new ArrayList<ObjectSelection>();

        LoadSelectionFrame.chargerSelections(selectionFile, appli, selections);
        for (ObjectSelection selection : selections) {
          String selectionName = selection.getName();
          Set<ILineString> layer1Feats = new HashSet<>();
          Set<ILineString> layer2Feats = new HashSet<>();
          String layer1 = sld.getLayers().get(0).getName();
          String layer2 = sld.getLayers().get(1).getName();
          for (IFeature feat : selection.getObjs()) {
            if (sld.getLayer(layer1).getFeatureCollection().contains(feat)) {
              if (feat.getGeom() instanceof ILineString)
                layer1Feats.add((ILineString) feat.getGeom());
              else {
                IMultiCurve<IOrientableCurve> complex = (IMultiCurve<IOrientableCurve>) feat
                    .getGeom();
                ILineString line = (ILineString) complex.get(0);
                layer1Feats.add(line);
              }
            } else if (sld.getLayer(layer2).getFeatureCollection()
                .contains(feat)) {
              if (feat.getGeom() instanceof ILineString)
                layer2Feats.add((ILineString) feat.getGeom());
              else {
                IMultiCurve<IOrientableCurve> complex = (IMultiCurve<IOrientableCurve>) feat
                    .getGeom();
                ILineString line = (ILineString) complex.get(0);
                layer2Feats.add(line);
              }
            }
          }

          // merge geometries
          ILineString merged1 = Operateurs.compileArcs(layer1Feats, 0.001);
          ILineString merged2 = Operateurs.compileArcs(layer2Feats, 0.001);

          OptCorMorphing morphing = new OptCorMorphing(merged1, merged2);
          SortedMap<Double, IGeometry> continuousGeoms = new TreeMap<Double, IGeometry>();
          IFeatureCollection<IFeature> featureCollection = new FT_FeatureCollection<>();
          FeatureType featureType = new FeatureType();
          featureType.setNomClasse("continuous");
          GF_AttributeType attr = new AttributeType("scale", "scale",
              "Integer");
          featureType.addFeatureAttribute(attr);
          featureType.setGeometryType(ILineString.class);
          featureCollection.setFeatureType(featureType);
          SchemaDefaultFeature schema = new SchemaDefaultFeature();
          schema.addFeatureType(featureType);
          schema.getAttLookup().put(0, new String[] { "scale", "continuous" });
          double t = 0.0;
          double step = 1.0 / nbScales;
          t += step;
          double scaleDiff = finScale - iniScale;
          while (t < 1.0) {
            double scale = scaleDiff * t + iniScale;
            ILineString morph = (ILineString) morphing
                .continuousGeneralisation(t);
            continuousGeoms.put(scale, morph);
            System.out.println("t= " + t);
            t += step;
            // create a default feature with the geometry to export it
            DefaultFeature feat = new DefaultFeature(morph);
            feat.setAttributes(new Object[1]);
            feat.setFeatureType(featureType);
            feat.setSchema(schema);
            feat.setAttribute(attr, (int) Math.round(scale));
            featureCollection.add(feat);
          }
          // add both initial lines in the shapefile
          DefaultFeature featIni = new DefaultFeature(merged1);
          featIni.setAttributes(new Object[1]);
          featIni.setFeatureType(featureType);
          featIni.setSchema(schema);
          featIni.setAttribute(attr, (int) Math.round(iniScale));
          featureCollection.add(featIni);
          DefaultFeature featFin = new DefaultFeature(merged2);
          featFin.setAttributes(new Object[1]);
          featFin.setFeatureType(featureType);
          featFin.setSchema(schema);
          featFin.setAttribute(attr, (int) Math.round(finScale));
          featureCollection.add(featFin);

          DiscontinuitiesMeasure measure = new DiscontinuitiesMeasure(
              continuousGeoms);
          measure.addLegibilityFunction(new CoalescenceFunction(0.8));
          measure.addLegibilityFunction(new NbOfBendsFunction(15.0));
          measure.addLegibilityFunction(new NbOfBendsFunction(75.0));
          measure.addLegibilityFunction(new NbOfBendsFunction(100.0));
          measure.addLegibilityFunction(new TopologyValidationFunction());
          measure
              .addDistanceFunction(new HausdorffDistanceFunction("Hausdorff"));
          measure.addDistanceFunction(
              new MeanLineDistanceFunction("Mean line distance"));
          measure.addDistanceFunction(
              new FrechetDistanceFunction("Fréchet distance"));
          measure.computeDiscontinuities();
          try {
            File file = new File(
                directory.getPath() + "//" + selectionName + ".csv");
            measure.writeToCsv(file);
          } catch (IOException e) {
            e.printStackTrace();
          }

          // write the continuous geometries in a shapefile
          String shapefileName = directory.getPath() + "//" + selectionName
              + ".shp";
          ShapefileWriter.write(featureCollection, shapefileName, null);
        }
      } catch (SAXException | IOException e1) {
        e1.printStackTrace();
      } catch (ParserConfigurationException e1) {
        e1.printStackTrace();
      }
    }

    public MeasureDiscontSelAction() {
      putValue(Action.SHORT_DESCRIPTION,
          "Measure discontinuities with OptCor morphing for file selection");
      putValue(Action.NAME, "Measure discontinuities for file selection");
    }
  }

  class LoadBendSeriesAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();

      LoadBendSeriesFrame frame = new LoadBendSeriesFrame(dataset);
      frame.setVisible(true);
    }

    public LoadBendSeriesAction() {
      putValue(Action.NAME, "Load pre-decomposed bend series shapefiles");
    }
  }

  class LoadBendSeriesFrame extends JFrame implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private CartAGenDataSet dataset;
    private JButton okBtn, cancelBtn, browseParts, browseSeries, browseHeads,
        browseRoads;
    private JTextField txtParts, txtSeries, txtHeads, txtRoads;
    private File roadsFile, partsFile, seriesFile, headsFile;

    // internationalisation values
    private String okLbl, cancelLbl;

    public LoadBendSeriesFrame(CartAGenDataSet dataset)
        throws HeadlessException {
      super("Load pre-decomposed bend series shapefiles");
      this.dataset = dataset;
      internationalisation();

      JPanel roadsPanel = new JPanel();
      txtRoads = new JTextField();
      txtRoads.setMaximumSize(new Dimension(140, 20));
      txtRoads.setMinimumSize(new Dimension(140, 20));
      txtRoads.setPreferredSize(new Dimension(140, 20));
      txtRoads.setEditable(false);
      browseRoads = new JButton(new ImageIcon("images/icons/16x16/browse.png"));
      browseRoads.addActionListener(this);
      browseRoads.setActionCommand("roads");
      roadsPanel.add(txtRoads);
      roadsPanel.add(browseRoads);
      roadsPanel.setLayout(new BoxLayout(roadsPanel, BoxLayout.X_AXIS));

      JPanel partsPanel = new JPanel();
      txtParts = new JTextField();
      txtParts.setMaximumSize(new Dimension(140, 20));
      txtParts.setMinimumSize(new Dimension(140, 20));
      txtParts.setPreferredSize(new Dimension(140, 20));
      txtParts.setEditable(false);
      browseParts = new JButton(new ImageIcon("images/icons/16x16/browse.png"));
      browseParts.addActionListener(this);
      browseParts.setActionCommand("roadParts");
      partsPanel.add(txtParts);
      partsPanel.add(browseParts);
      partsPanel.setLayout(new BoxLayout(partsPanel, BoxLayout.X_AXIS));

      JPanel seriesPanel = new JPanel();
      txtSeries = new JTextField();
      txtSeries.setMaximumSize(new Dimension(140, 20));
      txtSeries.setMinimumSize(new Dimension(140, 20));
      txtSeries.setPreferredSize(new Dimension(140, 20));
      txtSeries.setEditable(false);
      browseSeries = new JButton(
          new ImageIcon("images/icons/16x16/browse.png"));
      browseSeries.addActionListener(this);
      browseSeries.setActionCommand("bendSeries");
      seriesPanel.add(txtSeries);
      seriesPanel.add(browseSeries);
      seriesPanel.setLayout(new BoxLayout(seriesPanel, BoxLayout.X_AXIS));

      JPanel headsPanel = new JPanel();
      txtHeads = new JTextField();
      txtHeads.setMaximumSize(new Dimension(140, 20));
      txtHeads.setMinimumSize(new Dimension(140, 20));
      txtHeads.setPreferredSize(new Dimension(140, 20));
      txtHeads.setEditable(false);
      browseHeads = new JButton(new ImageIcon("images/icons/16x16/browse.png"));
      browseHeads.addActionListener(this);
      browseHeads.setActionCommand("bendHeads");
      headsPanel.add(txtHeads);
      headsPanel.add(browseHeads);
      headsPanel.setLayout(new BoxLayout(headsPanel, BoxLayout.X_AXIS));

      // define a panel with the OK and Cancel buttons
      JPanel btnPanel = new JPanel();
      okBtn = new JButton(okLbl);
      okBtn.addActionListener(this);
      okBtn.setActionCommand("ok");
      cancelBtn = new JButton(cancelLbl);
      cancelBtn.addActionListener(this);
      cancelBtn.setActionCommand("cancel");
      btnPanel.add(okBtn);
      btnPanel.add(cancelBtn);
      btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

      this.getContentPane().add(roadsPanel);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(partsPanel);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(seriesPanel);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(headsPanel);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(btnPanel);
      this.getContentPane()
          .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("roads")) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new ShapeFileFilter());
        int returnVal = fc.showOpenDialog(null);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
          return;
        }
        roadsFile = fc.getSelectedFile();
        txtRoads.setEditable(true);
        txtRoads.setText(roadsFile.getPath());
        txtRoads.setEditable(false);
      } else if (e.getActionCommand().equals("roadParts")) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new ShapeFileFilter());
        int returnVal = fc.showOpenDialog(null);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
          return;
        }
        partsFile = fc.getSelectedFile();
        txtParts.setEditable(true);
        txtParts.setText(partsFile.getPath());
        txtParts.setEditable(false);
      } else if (e.getActionCommand().equals("bendSeries")) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new ShapeFileFilter());
        int returnVal = fc.showOpenDialog(null);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
          return;
        }
        seriesFile = fc.getSelectedFile();
        txtSeries.setEditable(true);
        txtSeries.setText(seriesFile.getPath());
        txtSeries.setEditable(false);
      }
      if (e.getActionCommand().equals("bendHeads")) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new ShapeFileFilter());
        int returnVal = fc.showOpenDialog(null);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
          return;
        }
        headsFile = fc.getSelectedFile();
        txtHeads.setEditable(true);
        txtHeads.setText(headsFile.getPath());
        txtHeads.setEditable(false);
      } else if (e.getActionCommand().equals("ok")) {
        try {
          this.loadData();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
        setVisible(false);
      } else if (e.getActionCommand().equals("cancel")) {
        setVisible(false);
      }
    }

    private void loadData() throws IOException {
      // first load roads
      Map<ILineString, Map<String, Object>> roads = loadRoadLinesFromSHP(
          txtRoads.getText(), dataset);
      Map<Integer, SinuousSection> ids = new HashMap<>();
      IPopulation<IRoadLine> pop = dataset.getRoads();
      for (ILineString line : roads.keySet()) {
        Map<String, Object> fields = roads.get(line);
        IRoadLine tr = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory()
            .createRoadLine(new TronconDeRouteImpl(
                (Reseau) dataset.getRoadNetwork().getGeoxObj(), false, line),
                1);
        if (fields.containsKey("id"))
          tr.setId((Integer) fields.get("id"));
        pop.add(tr);
        dataset.getRoadNetwork().addSection(tr);
        ids.put((Integer) fields.get("id"), new SinuousSection(tr));
      }

      // then, load road parts
      Map<ILineString, Map<String, Object>> roadParts = loadRoadLinesFromSHP(
          txtParts.getText(), dataset);

      // then, load bend series parts
      Map<ILineString, Map<String, Object>> roadSeries = loadRoadLinesFromSHP(
          txtSeries.getText(), dataset);

      // finally load bend heads
      Map<ILineString, Map<String, Object>> roadHeads = loadRoadLinesFromSHP(
          txtHeads.getText(), dataset);
    }

    private Map<ILineString, Map<String, Object>> loadRoadLinesFromSHP(
        String path, CartAGenDataSet dataset) throws IOException {
      ShapefileReader shr = null;
      DbaseFileReader dbr = null;
      try {
        ShpFiles shpf = new ShpFiles(path);
        shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
        dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        return null;
      }

      Map<ILineString, Map<String, Object>> outputMap = new HashMap<>();
      while (shr.hasNext() && dbr.hasNext()) {
        Record objet = shr.nextRecord();
        // compute the symbol from the fields according to source DLM
        Object[] champs = dbr.readEntry();
        Map<String, Object> fields = new HashMap<String, Object>();
        for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
          fields.put(dbr.getHeader().getFieldName(i), champs[i]);
        }

        // recupere la geometrie
        IGeometry geom = null;
        try {
          geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
        if (geom == null) {
          continue;
        }
        if (geom instanceof ILineString) {
          outputMap.put((ILineString) geom, fields);

        } else if (geom instanceof IMultiCurve<?>) {
          for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
            outputMap.put((ILineString) ((IMultiCurve<?>) geom).get(i), fields);
          }
        }
      }
      shr.close();
      dbr.close();

      return outputMap;
    }

    /**
     * Fix the values of Frame labels according to Language Locale.
     */
    private void internationalisation() {
      okLbl = I18N.getString("MainLabels.lblOk");
      cancelLbl = I18N.getString("MainLabels.lblCancel");
    }

  }
}
