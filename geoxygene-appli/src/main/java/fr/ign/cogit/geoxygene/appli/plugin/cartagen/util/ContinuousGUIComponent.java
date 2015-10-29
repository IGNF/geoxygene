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
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import fr.ign.cogit.cartagen.continuous.BasicMorphing;
import fr.ign.cogit.cartagen.continuous.MorphingVertexMapping;
import fr.ign.cogit.cartagen.continuous.leastsquares.LeastSquaresMorphing;
import fr.ign.cogit.cartagen.continuous.optcor.OptCorMorphing;
import fr.ign.cogit.cartagen.genealgorithms.polygon.VisvalingamWhyatt;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.GeometryPool;
import fr.ign.cogit.geoxygene.api.feature.IDataSet;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

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

  public ContinuousGUIComponent(GeOxygeneApplication appli, String title) {
    super(title);
    this.appli = appli;
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
        Object[][] tableContent = new Object[morphing.getSubLinesIni().size() + 1][morphing
            .getSubLinesFin().size() + 1];
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
          pool.addFeatureToGeometryPool(GeometryEngine.getFactory()
              .createLineSegment(pt, other), Color.RED, 3);
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
      StyledLayerDescriptor sld = appli.getMainFrame()
          .getSelectedProjectFrame().getSld();
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

      for (int i = 0; i < lsMorphing.getMapping().getInitialCoords().size(); i++) {
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
      StyledLayerDescriptor sld = appli.getMainFrame()
          .getSelectedProjectFrame().getSld();
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

}
