/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.geompool;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;

/**
 * @author JRenard april 2012
 */

public class GeomPoolFrame extends JFrame {
  private static final long serialVersionUID = 1L;

  // //////////////
  // SINGLETON MECHANISM
  // /////////////

  public static GeomPoolFrame geomPoolFrame;

  public static GeomPoolFrame getInstance() {
    if (GeomPoolFrame.geomPoolFrame == null) {
      GeomPoolFrame.geomPoolFrame = new GeomPoolFrame();
    }
    return GeomPoolFrame.geomPoolFrame;
  }

  // //////////////
  // COMPONENTS
  // /////////////

  private static JTabbedPane themes;
  private static JTabbedPane themesInitial;
  private static JTabbedPane dataSets;

  private static JPanel pnlRoads;
  private static JPanel pnlBuildings;
  private static JPanel pnlHydroLines;
  private static JPanel pnlHydroSurfaces;
  private static JPanel pnlRailwayLines;
  private static JPanel pnlElectricityLines;
  private static JPanel pnlContourLines;
  private static JPanel pnlLandUseAreas;
  private static JPanel pnlAdminFields;
  private static JPanel pnlRoadStrokes;
  private static JPanel pnlRoundabouts;
  private static JPanel pnlBranchings;

  private static JPanel pnlRoadsInitial;
  private static JPanel pnlBuildingsInitial;
  private static JPanel pnlHydroLinesInitial;
  private static JPanel pnlHydroSurfacesInitial;
  private static JPanel pnlRailwayLinesInitial;
  private static JPanel pnlElectricityLinesInitial;
  private static JPanel pnlContourLinesInitial;
  private static JPanel pnlLandUseAreasInitial;
  private static JPanel pnlAdminFieldsInitial;
  private static JPanel pnlRoadStrokesInitial;
  private static JPanel pnlRoundaboutsInitial;
  private static JPanel pnlBranchingsInitial;

  private static JPanel panneauBoutons;
  public static JButton bValider;

  /**
   * Constructor
   */
  public GeomPoolFrame() {

    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setResizable(false);
    this.setLocation(100, 100);
    this.setTitle("CartAGen - geometries pool configuration");
    this.initComponents();
    this.placeComponents();
    this.pack();

    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        GeomPoolFrame.this.setVisible(false);
      }

      @Override
      public void windowActivated(WindowEvent e) {
      }
    });

  }

  /**
   * Initialisation of the components
   */
  private void initComponents() {

    // Tabbed panels for themes
    GeomPoolFrame.themes = new JTabbedPane();
    GeomPoolFrame.themesInitial = new JTabbedPane();
    GeomPoolFrame.dataSets = new JTabbedPane();

    // Themes tabs
    GeomPoolFrame.pnlRoads = new ThemeTabPanel(CartAGenDataSet.ROADS_POP);
    GeomPoolFrame.pnlBuildings = new ThemeTabPanel(
        CartAGenDataSet.BUILDINGS_POP);
    GeomPoolFrame.pnlHydroLines = new ThemeTabPanel(
        CartAGenDataSet.WATER_LINES_POP);
    GeomPoolFrame.pnlHydroSurfaces = new ThemeTabPanel(
        CartAGenDataSet.WATER_AREAS_POP);
    GeomPoolFrame.pnlRailwayLines = new ThemeTabPanel(
        CartAGenDataSet.RAILWAY_LINES_POP);
    GeomPoolFrame.pnlElectricityLines = new ThemeTabPanel(
        CartAGenDataSet.ELECTRICITY_LINES_POP);
    GeomPoolFrame.pnlContourLines = new ThemeTabPanel(
        CartAGenDataSet.CONTOUR_LINES_POP);
    GeomPoolFrame.pnlHydroLines = new ThemeTabPanel(
        CartAGenDataSet.WATER_LINES_POP);
    GeomPoolFrame.pnlLandUseAreas = new ThemeTabPanel(
        CartAGenDataSet.LANDUSE_AREAS_POP);
    GeomPoolFrame.pnlAdminFields = new ThemeTabPanel(
        CartAGenDataSet.ADMIN_FIELDS_POP);
    GeomPoolFrame.pnlHydroLines = new ThemeTabPanel(
        CartAGenDataSet.WATER_LINES_POP);
    GeomPoolFrame.pnlRoadStrokes = new ThemeTabPanel(
        CartAGenDataSet.ROAD_STROKES_POP);
    GeomPoolFrame.pnlRoundabouts = new ThemeTabPanel(
        CartAGenDataSet.ROUNDABOUTS_POP);
    GeomPoolFrame.pnlBranchings = new ThemeTabPanel(
        CartAGenDataSet.BRANCHINGS_POP);

    // Initial themes tabs
    GeomPoolFrame.pnlRoadsInitial = new ThemeTabPanel(
        CartAGenDataSet.ROADS_POP);
    GeomPoolFrame.pnlBuildingsInitial = new ThemeTabPanel(
        CartAGenDataSet.BUILDINGS_POP);
    GeomPoolFrame.pnlHydroLinesInitial = new ThemeTabPanel(
        CartAGenDataSet.WATER_LINES_POP);
    GeomPoolFrame.pnlHydroSurfacesInitial = new ThemeTabPanel(
        CartAGenDataSet.WATER_AREAS_POP);
    GeomPoolFrame.pnlRailwayLinesInitial = new ThemeTabPanel(
        CartAGenDataSet.RAILWAY_LINES_POP);
    GeomPoolFrame.pnlElectricityLinesInitial = new ThemeTabPanel(
        CartAGenDataSet.ELECTRICITY_LINES_POP);
    GeomPoolFrame.pnlContourLinesInitial = new ThemeTabPanel(
        CartAGenDataSet.CONTOUR_LINES_POP);
    GeomPoolFrame.pnlHydroLinesInitial = new ThemeTabPanel(
        CartAGenDataSet.WATER_LINES_POP);
    GeomPoolFrame.pnlLandUseAreasInitial = new ThemeTabPanel(
        CartAGenDataSet.LANDUSE_AREAS_POP);
    GeomPoolFrame.pnlAdminFieldsInitial = new ThemeTabPanel(
        CartAGenDataSet.ADMIN_FIELDS_POP);
    GeomPoolFrame.pnlHydroLinesInitial = new ThemeTabPanel(
        CartAGenDataSet.WATER_LINES_POP);
    GeomPoolFrame.pnlRoadStrokesInitial = new ThemeTabPanel(
        CartAGenDataSet.ROAD_STROKES_POP);
    GeomPoolFrame.pnlRoundaboutsInitial = new ThemeTabPanel(
        CartAGenDataSet.ROUNDABOUTS_POP);
    GeomPoolFrame.pnlBranchingsInitial = new ThemeTabPanel(
        CartAGenDataSet.BRANCHINGS_POP);

    // Panel for buttons
    GeomPoolFrame.panneauBoutons = new JPanel(new GridBagLayout());
    GeomPoolFrame.bValider = new JButton("Validate");

    // Validation button
    GeomPoolFrame.bValider.setPreferredSize(new Dimension(110, 30));
    GeomPoolFrame.bValider.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        GeomPoolFrame.this.validate();
        GeomPoolFrame.this.setVisible(false);
        GeomPoolFrame.drawGeomPool();
      }
    });

  }

  /**
   * Placement of the components in the frame
   */
  private void placeComponents() {

    // Tabbed pane
    GeomPoolFrame.themes.addTab("Roads", GeomPoolFrame.pnlRoads);
    GeomPoolFrame.themes.addTab("Buildings", GeomPoolFrame.pnlBuildings);
    GeomPoolFrame.themes.addTab("Linear rivers", GeomPoolFrame.pnlHydroLines);
    GeomPoolFrame.themes.addTab("Surfacic rivers",
        GeomPoolFrame.pnlHydroSurfaces);
    GeomPoolFrame.themes.addTab("Railway", GeomPoolFrame.pnlRailwayLines);
    GeomPoolFrame.themes.addTab("Electricity",
        GeomPoolFrame.pnlElectricityLines);
    GeomPoolFrame.themes.addTab("Contour lines", GeomPoolFrame.pnlContourLines);
    GeomPoolFrame.themes.addTab("Land use", GeomPoolFrame.pnlLandUseAreas);
    GeomPoolFrame.themes.addTab("Admin fields", GeomPoolFrame.pnlAdminFields);
    GeomPoolFrame.themes.addTab("Strokes", GeomPoolFrame.pnlRoadStrokes);
    GeomPoolFrame.themes.addTab("Roundabouts", GeomPoolFrame.pnlRoundabouts);
    GeomPoolFrame.themes.addTab("Branchings", GeomPoolFrame.pnlBranchings);

    // Tabbed pane for initial dataset
    GeomPoolFrame.themesInitial.addTab("Roads", GeomPoolFrame.pnlRoadsInitial);
    GeomPoolFrame.themesInitial.addTab("Buildings",
        GeomPoolFrame.pnlBuildingsInitial);
    GeomPoolFrame.themesInitial.addTab("Linear rivers",
        GeomPoolFrame.pnlHydroLinesInitial);
    GeomPoolFrame.themesInitial.addTab("Surfacic rivers",
        GeomPoolFrame.pnlHydroSurfacesInitial);
    GeomPoolFrame.themesInitial.addTab("Railway",
        GeomPoolFrame.pnlRailwayLinesInitial);
    GeomPoolFrame.themesInitial.addTab("Electricity",
        GeomPoolFrame.pnlElectricityLinesInitial);
    GeomPoolFrame.themesInitial.addTab("Contour lines",
        GeomPoolFrame.pnlContourLinesInitial);
    GeomPoolFrame.themesInitial.addTab("Land use",
        GeomPoolFrame.pnlLandUseAreasInitial);
    GeomPoolFrame.themesInitial.addTab("Admin fields",
        GeomPoolFrame.pnlAdminFieldsInitial);
    GeomPoolFrame.themesInitial.addTab("Strokes",
        GeomPoolFrame.pnlRoadStrokesInitial);
    GeomPoolFrame.themesInitial.addTab("Roundabouts",
        GeomPoolFrame.pnlRoundaboutsInitial);
    GeomPoolFrame.themesInitial.addTab("Branchings",
        GeomPoolFrame.pnlBranchingsInitial);

    // Tabbed pane for both datasets
    GeomPoolFrame.dataSets.addTab("Current dataset", GeomPoolFrame.themes);
    GeomPoolFrame.dataSets.addTab("Initial dataset",
        GeomPoolFrame.themesInitial);

    // Validation button
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.insets = new Insets(20, 20, 20, 20);
    GeomPoolFrame.panneauBoutons.add(GeomPoolFrame.bValider, gbc);

    // Central panel for road symbolisation
    this.setLayout(new BorderLayout());
    this.add(GeomPoolFrame.dataSets, BorderLayout.CENTER);

    // South panel for buttons
    this.add(GeomPoolFrame.panneauBoutons, BorderLayout.SOUTH);

  }

  /**
   * Draws the geometries with the chosen colors
   */
  private static void drawGeomPool() {

    CartAGenPlugin.getInstance().getApplication().getMainFrame()
        .getSelectedProjectFrame().getLayer("Geometry Pool")
        .getFeatureCollection().clear();

    // Current datasetThemes
    for (int i = 0; i < GeomPoolFrame.themes.getComponentCount(); i++) {

      // Treatment of each tab for each theme
      ThemeTabPanel tab = (ThemeTabPanel) GeomPoolFrame.themes.getComponent(i);
      for (IGeneObj obj : CartAGenDoc.getInstance().getCurrentDataset()
          .getCartagenPop(tab.popName)) {

        // Initial dataset features
        if (tab.tabInitial.getSelectedColumn() != 0 && !obj.hasBeenCreated()) {
          GeomPoolFrame.drawGeom(tab.tabInitial, obj.getInitialGeom());
        }

        // Current dataset features
        if (tab.tabCurrent.getSelectedColumn() != 0 && !obj.isDeleted()) {
          GeomPoolFrame.drawGeom(tab.tabCurrent, obj.getGeom());
        }

        // Unchanged features
        if (tab.tabUnchanged.getSelectedColumn() != 0 && !obj.hasBeenCreated()
            && !obj.isDeleted() && obj.getGeom().equals(obj.getInitialGeom())) {
          GeomPoolFrame.drawGeom(tab.tabUnchanged, obj.getGeom());
        }

        // Modified features
        if (tab.tabModified.getSelectedColumn() != 0 && !obj.hasBeenCreated()
            && !obj.isDeleted()
            && !obj.getGeom().equals(obj.getInitialGeom())) {
          GeomPoolFrame.drawGeom(tab.tabModified, obj.getGeom());
        }

        // Created features
        if (tab.tabCreated.getSelectedColumn() != 0 && obj.hasBeenCreated()
            && !obj.isDeleted()) {
          GeomPoolFrame.drawGeom(tab.tabCreated, obj.getGeom());
        }

        // Created features
        if (tab.tabDeleted.getSelectedColumn() != 0 && !obj.hasBeenCreated()
            && obj.isDeleted()) {
          GeomPoolFrame.drawGeom(tab.tabDeleted, obj.getInitialGeom());
        }

      }

    }

    // Initial datasetThemes
    for (int i = 0; i < GeomPoolFrame.themesInitial.getComponentCount(); i++) {

      // Treatment of each tab for each theme
      ThemeTabPanel tab = (ThemeTabPanel) GeomPoolFrame.themesInitial
          .getComponent(i);
      for (IGeneObj obj : CartAGenDoc.getInstance().getInitialDataset()
          .getCartagenPop(tab.popName)) {

        // Initial dataset features
        if (tab.tabInitial.getSelectedColumn() != 0 && !obj.hasBeenCreated()) {
          GeomPoolFrame.drawGeom(tab.tabInitial, obj.getInitialGeom());
        }

        // Current dataset features
        if (tab.tabCurrent.getSelectedColumn() != 0 && !obj.isDeleted()) {
          GeomPoolFrame.drawGeom(tab.tabCurrent, obj.getGeom());
        }

        // Unchanged features
        if (tab.tabUnchanged.getSelectedColumn() != 0 && !obj.hasBeenCreated()
            && !obj.isDeleted() && obj.getGeom().equals(obj.getInitialGeom())) {
          GeomPoolFrame.drawGeom(tab.tabUnchanged, obj.getGeom());
        }

        // Modified features
        if (tab.tabModified.getSelectedColumn() != 0 && !obj.hasBeenCreated()
            && !obj.isDeleted()
            && !obj.getGeom().equals(obj.getInitialGeom())) {
          GeomPoolFrame.drawGeom(tab.tabModified, obj.getGeom());
        }

        // Created features
        if (tab.tabCreated.getSelectedColumn() != 0 && obj.hasBeenCreated()
            && !obj.isDeleted()) {
          GeomPoolFrame.drawGeom(tab.tabCreated, obj.getGeom());
        }

        // Created features
        if (tab.tabDeleted.getSelectedColumn() != 0 && !obj.hasBeenCreated()
            && obj.isDeleted()) {
          GeomPoolFrame.drawGeom(tab.tabDeleted, obj.getInitialGeom());
        }

      }

    }

  }

  /**
   * Draw a geometry with a jtable defining its color
   * @param tab
   * @param geom
   */
  private static void drawGeom(JTable tab, IGeometry geom) {
    Color col = ((ColourTableModel) tab.getModel()).getValueAt(1,
        tab.getSelectedColumn());
    IGeometry gm;
    if (geom instanceof IPolygon) {
      gm = ((IPolygon) geom).exteriorLineString();
    } else {
      gm = geom;
    }
    CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
        .addFeatureToGeometryPool(gm, col, 1);
  }

}
