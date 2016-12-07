/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.XMLFileFilter;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.CrossRoadDetection;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RondPoint;
import fr.ign.cogit.cartagen.spatialanalysis.urban.BuildingClassifierSVM;
import fr.ign.cogit.cartagen.spatialanalysis.urban.BuildingClassifierSVM.BuildingClass;
import fr.ign.cogit.cartagen.spatialanalysis.urban.BuildingClassifierSVM.BuildingDescriptor;
import fr.ign.cogit.cartagen.spatialanalysis.urban.BuildingClassifierSVM.TrainingData;
import fr.ign.cogit.cartagen.spatialanalysis.urban.UrbanAreaComputationJTS;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.IlotImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.NoeudRoutierImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.TronconDeRouteImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.SLDUtil;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.shape.PolygonSignatureFunction;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.shape.PolygonTurningFunction;

public class SpatialAnalysisPlugin
    implements ProjectFramePlugin, GeOxygeneApplicationPlugin {

  private GeOxygeneApplication application = null;
  private Map<TronconDeRoute, IFeature> roadsMap = new HashMap<TronconDeRoute, IFeature>();

  @Override
  public void initialize(GeOxygeneApplication application) {
    this.application = application;
    JMenu menu = new JMenu("Spatial Analysis");
    JMenu roadMenu = new JMenu("Roads");
    roadMenu.add(new JMenuItem(new RoundaboutAction()));
    roadMenu.add(new JMenuItem(new ExportRoundaboutAction()));
    JMenu riverMenu = new JMenu("Rivers");
    JMenu railMenu = new JMenu("Railroads");
    JMenu urbanMenu = new JMenu("Urban areas");
    urbanMenu.add(new JMenuItem(new BoffetUrbanAreasAction()));
    urbanMenu.add(new JMenuItem(new CitinessUrbanAreasAction()));
    JMenu buildClassifMenu = new JMenu("Building Classification");
    buildClassifMenu.add(new JMenuItem(new AddTrainingDataAction()));
    buildClassifMenu.add(new JMenuItem(new BuildingClassifSVMAction()));
    urbanMenu.add(buildClassifMenu);
    menu.add(roadMenu);
    menu.add(riverMenu);
    menu.add(railMenu);
    menu.add(urbanMenu);
    JMenu shapeMenu = new JMenu("Shape");
    shapeMenu.add(new JMenuItem(new DisplayTurningFuncAction()));
    shapeMenu.add(new JMenuItem(new CompareTurningFuncsAction()));
    shapeMenu.add(new JMenuItem(new DisplaySignatureFuncAction()));
    shapeMenu.add(new JMenuItem(new CompareSignatureFuncsAction()));
    menu.add(shapeMenu);
    application.getMainFrame().getMenuBar().add(menu,
        application.getMainFrame().getMenuBar().getMenuCount() - 2);

  }

  @Override
  public void initialize(ProjectFrame projectFrame) {
    // TODO Auto-generated method stub

  }

  /**
   * Identifies the roundabouts in the selected road layer, and creates a new
   * layer with the roundabouts.
   * 
   * @author GTouya
   * 
   */
  class RoundaboutAction extends AbstractAction {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {

      // create the road feature collection from the selected features
      IFeatureCollection<TronconDeRoute> roads = new FT_FeatureCollection<>();
      Reseau res = new ReseauImpl();
      for (IFeature feat : SelectionUtil.getSelectedObjects(application)) {
        TronconDeRoute road = new TronconDeRouteImpl(res, false,
            (ICurve) feat.getGeom());
        roads.add(road);
        roadsMap.put(road, feat);
      }
      // enrich the roads collection by building its topology

      // construction of the topological map based on roads
      CarteTopo carteTopo = new CarteTopo("cartetopo");
      carteTopo.setBuildInfiniteFace(false);
      carteTopo.importClasseGeo(roads, true);
      carteTopo.creeNoeudsManquants(1.0);
      carteTopo.fusionNoeuds(1.0);
      // create the node objects
      for (Noeud n : carteTopo.getPopNoeuds()) {
        NoeudRoutier noeud = new NoeudRoutierImpl(res, n.getGeometrie());
        for (Arc a : n.getEntrants()) {
          ((TronconDeRoute) a.getCorrespondant(0)).setNoeudFinal(noeud);
          noeud.getArcsEntrants().add((TronconDeRoute) a.getCorrespondant(0));
        }
        for (Arc a : n.getSortants()) {
          ((TronconDeRoute) a.getCorrespondant(0)).setNoeudInitial(noeud);
          noeud.getArcsSortants().add((TronconDeRoute) a.getCorrespondant(0));
        }
      }

      // create the blocks
      IFeatureCollection<Ilot> blocks = new FT_FeatureCollection<>();
      // use the same topology map
      carteTopo.filtreDoublons(1.0);
      carteTopo.rendPlanaire(1.0);
      carteTopo.fusionNoeuds(1.0);
      carteTopo.filtreArcsDoublons();
      carteTopo.creeTopologieFaces();
      for (Face face : carteTopo.getListeFaces()) {
        blocks.add(new IlotImpl(face.getGeom()));
      }

      CrossRoadDetection detect = new CrossRoadDetection();
      detect.detectRoundaboutsAndBranching(roads, blocks, false);

      // put the roundabouts in a new layer
      ProjectFrame project = application.getMainFrame()
          .getSelectedProjectFrame();
      project.addUserLayer(detect.getRoundabouts(), "roundabouts", null);
    }

    public RoundaboutAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Create roundabouts in the selected roads and add them as a new layer");
      this.putValue(Action.NAME, "Create roundabouts");
    }
  }

  /**
   * Exports a roundabouts layer as a new column in the road table.
   * 
   * @author GTouya
   * 
   */
  class ExportRoundaboutAction extends AbstractAction {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      ExportRoundaboutFrame frame = new ExportRoundaboutFrame(application);
      frame.setVisible(true);
    }

    public ExportRoundaboutAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Export roundabouts in PostGIS as a boolean column in the roads table");
      this.putValue(Action.NAME, "Export roundabouts in PostGIS");
    }
  }

  class ExportRoundaboutFrame extends JFrame implements ActionListener {

    /****/
    private static final long serialVersionUID = 1L;
    private GeOxygeneApplication application = null;
    private JTextField txtHost, txtPort, txtDb, txtUser, txtPwd, txtTableName,
        txtIdName, txtAttr;

    @Override
    public void actionPerformed(ActionEvent e) {

      if (e.getActionCommand().equals("OK")) {
        try {
          List<GF_AttributeType> featureAttributes = application.getMainFrame()
              .getSelectedProjectFrame().getLayer(txtTableName.getText())
              .getFeatureCollection().getFeatureType().getFeatureAttributes();
          GF_AttributeType idAttr = null;
          for (GF_AttributeType attr : featureAttributes) {
            System.out.println(attr);
            if (attr.getMemberName().equals(txtIdName.getText())) {
              idAttr = attr;
              break;
            }
          }
          System.out.println("attribute: " + idAttr);
          String url = "jdbc:postgresql://" + txtHost.getText() + ":"
              + txtPort.getText() + "/" + txtDb.getText();
          Connection conn = DriverManager.getConnection(url, txtUser.getText(),
              txtPwd.getText());
          String query = "UPDATE " + txtTableName.getText() + " SET "
              + txtAttr.getText() + "='false'";
          Statement stat = conn.createStatement();
          try {
            stat.executeQuery(query);
          } catch (Exception e2) {
            // Do nothing
          }
          for (IFeature feat : application.getMainFrame()
              .getSelectedProjectFrame().getLayer("roundabouts")
              .getFeatureCollection()) {
            RondPoint round = (RondPoint) feat;
            for (TronconDeRoute road : round.getRoutesInternes()) {
              DefaultFeature featRoad = (DefaultFeature) roadsMap.get(road);
              // System.out.println(featRoad.getAttribute(62));
              // System.out.println(featRoad.getAttribute(63));
              String id = (String) featRoad.getAttribute(idAttr);
              // System.out.println(id);
              query = "UPDATE " + txtTableName.getText() + " SET "
                  + txtAttr.getText() + "='true'" + "WHERE "
                  + txtIdName.getText() + "='" + id + "'";
              stat = conn.createStatement();
              try {
                stat.executeQuery(query);
              } catch (Exception e3) {
                // Do nothing
              }
            }
          }
        } catch (Exception e1) {
          e1.printStackTrace();
        }
        this.dispose();
      } else {
        this.dispose();
      }
    }

    ExportRoundaboutFrame(GeOxygeneApplication application) {
      super("Export roundabouts");
      this.application = application;
      setSize(600, 500);
      setPreferredSize(new Dimension(600, 500));

      // define a panel with the connection information
      JPanel connectionPanel = new JPanel();
      // hôte
      JPanel hostPanel = new JPanel();
      txtHost = new JTextField("localhost");
      txtHost.setPreferredSize(new Dimension(100, 20));
      txtHost.setMinimumSize(new Dimension(100, 20));
      txtHost.setMaximumSize(new Dimension(100, 20));
      hostPanel.add(new JLabel("host : "));
      hostPanel.add(txtHost);
      hostPanel.setLayout(new BoxLayout(hostPanel, BoxLayout.X_AXIS));
      hostPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      // port
      JPanel portPanel = new JPanel();
      txtPort = new JTextField("5432");
      txtPort.setPreferredSize(new Dimension(80, 20));
      txtPort.setMinimumSize(new Dimension(80, 20));
      txtPort.setMaximumSize(new Dimension(80, 20));
      portPanel.add(new JLabel("port : "));
      portPanel.add(txtPort);
      portPanel.setLayout(new BoxLayout(portPanel, BoxLayout.X_AXIS));
      portPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      // database
      JPanel dbPanel = new JPanel();
      txtDb = new JTextField("");
      txtDb.setPreferredSize(new Dimension(120, 20));
      txtDb.setMinimumSize(new Dimension(120, 20));
      txtDb.setMaximumSize(new Dimension(120, 20));
      dbPanel.add(new JLabel("database name : "));
      dbPanel.add(txtDb);
      dbPanel.setLayout(new BoxLayout(dbPanel, BoxLayout.X_AXIS));
      dbPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      // user
      JPanel userPanel = new JPanel();
      txtUser = new JTextField("postgres");
      txtUser.setPreferredSize(new Dimension(100, 20));
      txtUser.setMinimumSize(new Dimension(100, 20));
      txtUser.setMaximumSize(new Dimension(100, 20));
      userPanel.add(new JLabel("user : "));
      userPanel.add(txtUser);
      userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.X_AXIS));
      userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      // password
      JPanel pwdPanel = new JPanel();
      txtPwd = new JTextField("postgres");
      txtPwd.setPreferredSize(new Dimension(100, 20));
      txtPwd.setMinimumSize(new Dimension(100, 20));
      txtPwd.setMaximumSize(new Dimension(100, 20));
      pwdPanel.add(new JLabel("password : "));
      pwdPanel.add(txtPwd);
      pwdPanel.setLayout(new BoxLayout(pwdPanel, BoxLayout.X_AXIS));
      pwdPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      connectionPanel.add(hostPanel);
      connectionPanel.add(Box.createVerticalGlue());
      connectionPanel.add(portPanel);
      connectionPanel.add(Box.createVerticalGlue());
      connectionPanel.add(dbPanel);
      connectionPanel.add(Box.createVerticalGlue());
      connectionPanel.add(userPanel);
      connectionPanel.add(Box.createVerticalGlue());
      connectionPanel.add(pwdPanel);
      connectionPanel.add(Box.createVerticalGlue());
      connectionPanel
          .setLayout(new BoxLayout(connectionPanel, BoxLayout.Y_AXIS));

      // define a panel with the table information
      JPanel tablePanel = new JPanel();
      // table name
      JPanel tableNamePanel = new JPanel();
      txtTableName = new JTextField("troncon_de_route");
      txtTableName.setPreferredSize(new Dimension(100, 20));
      txtTableName.setMinimumSize(new Dimension(100, 20));
      txtTableName.setMaximumSize(new Dimension(100, 20));
      tableNamePanel.add(new JLabel("table name : "));
      tableNamePanel.add(txtTableName);
      tableNamePanel.setLayout(new BoxLayout(tableNamePanel, BoxLayout.X_AXIS));
      tableNamePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      // id column name
      JPanel idPanel = new JPanel();
      txtIdName = new JTextField();
      txtIdName.setPreferredSize(new Dimension(100, 20));
      txtIdName.setMinimumSize(new Dimension(100, 20));
      txtIdName.setMaximumSize(new Dimension(100, 20));
      idPanel.add(new JLabel("id column name : "));
      idPanel.add(txtIdName);
      idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.X_AXIS));
      idPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      // roundabout column name
      JPanel attrPanel = new JPanel();
      txtAttr = new JTextField("roundabout");
      txtAttr.setPreferredSize(new Dimension(100, 20));
      txtAttr.setMinimumSize(new Dimension(100, 20));
      txtAttr.setMaximumSize(new Dimension(100, 20));
      attrPanel.add(new JLabel("result column name : "));
      attrPanel.add(txtAttr);
      attrPanel.setLayout(new BoxLayout(attrPanel, BoxLayout.X_AXIS));
      attrPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      tablePanel.add(tableNamePanel);
      tablePanel.add(Box.createVerticalGlue());
      tablePanel.add(idPanel);
      tablePanel.add(Box.createVerticalGlue());
      tablePanel.add(attrPanel);
      tablePanel.add(Box.createVerticalGlue());
      tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));

      JPanel infoPanel = new JPanel();
      infoPanel.add(Box.createHorizontalStrut(5));
      infoPanel.add(connectionPanel);
      infoPanel.add(Box.createHorizontalGlue());
      infoPanel.add(tablePanel);
      infoPanel.add(Box.createHorizontalStrut(5));
      infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));

      // define a panel with the OK and Cancel buttons
      JPanel btnPanel = new JPanel();
      JButton okBtn = new JButton("OK");
      okBtn.addActionListener(this);
      okBtn.setActionCommand("OK");
      JButton cancelBtn = new JButton("Cancel");
      cancelBtn.addActionListener(this);
      cancelBtn.setActionCommand("cancel");
      btnPanel.add(okBtn);
      btnPanel.add(cancelBtn);
      btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

      this.getContentPane().add(infoPanel);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(btnPanel);
      this.getContentPane()
          .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.setAlwaysOnTop(true);
      this.pack();
    }
  }

  /**
   * Creates a new layer with the urban areas computed from a buildings layer,
   * using the method from (Boffet 2000).
   * 
   * @author GTouya
   * 
   */
  class BoffetUrbanAreasAction extends AbstractAction {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      BoffetUrbanAreasFrame frame = new BoffetUrbanAreasFrame(application);
      frame.setVisible(true);
    }

    public BoffetUrbanAreasAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Create urban areas from buildings using the method from (Boffet 2000)");
      this.putValue(Action.NAME,
          "Create urban areas from buildings (Boffet 2000)");
    }
  }

  class BoffetUrbanAreasFrame extends JFrame implements ActionListener {

    /****/
    private static final long serialVersionUID = 1L;

    private GeOxygeneApplication application;
    private JTextField txtLayer;
    private JComboBox<String> cbBuildings;
    private JSpinner spinBuffer, spinErosion, spinQuadrant, spinDouglas,
        spinArea, spinHole;

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("OK")) {
        ArrayList<IGeometry> geoms = new ArrayList<>();
        for (IFeature building : application.getMainFrame()
            .getSelectedProjectFrame()
            .getLayer(cbBuildings.getSelectedItem().toString())
            .getFeatureCollection()) {
          geoms.add(building.getGeom());
        }

        IGeometry complex = UrbanAreaComputationJTS.calculTacheUrbaine(geoms,
            (Double) spinBuffer.getValue(), (Double) spinErosion.getValue(),
            (Integer) spinQuadrant.getValue(), (Double) spinDouglas.getValue(),
            (Double) spinHole.getValue());
        IFeatureCollection<DefaultFeature> urbanAreas = new FT_FeatureCollection<>();
        FeatureType ft = new FeatureType();
        ft.setNomClasse(txtLayer.getText());
        ft.setGeometryType(IPolygon.class);
        urbanAreas.setFeatureType(ft);
        if (complex instanceof IPolygon)
          urbanAreas.add(new DefaultFeature(complex));
        else if (complex instanceof IMultiSurface<?>) {
          for (IPolygon simple : ((IMultiSurface<IPolygon>) complex)) {
            if (simple == null)
              continue;
            if (simple.area() < (Double) spinArea.getValue())
              continue;
            urbanAreas.add(new DefaultFeature(simple));
          }
        }

        // put the urban areas in a new layer
        ProjectFrame project = application.getMainFrame()
            .getSelectedProjectFrame();
        project.addUserLayer(urbanAreas, txtLayer.getText(), null);
        this.dispose();
      } else {
        this.dispose();
      }
    }

    BoffetUrbanAreasFrame(GeOxygeneApplication application) {
      super("Create urban areas from buildings (Boffet 2000)");
      this.application = application;
      this.setPreferredSize(new Dimension(650, 200));

      JPanel infoPanel = new JPanel();
      txtLayer = new JTextField("builtupAreas");
      txtLayer.setPreferredSize(new Dimension(120, 20));
      txtLayer.setMaximumSize(new Dimension(120, 20));
      txtLayer.setMinimumSize(new Dimension(120, 20));
      DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
      for (Layer layer : application.getMainFrame().getSelectedProjectFrame()
          .getLayers())
        model.addElement(layer.getName());
      cbBuildings = new JComboBox<String>(model);
      cbBuildings.setPreferredSize(new Dimension(150, 20));
      cbBuildings.setMaximumSize(new Dimension(150, 20));
      cbBuildings.setMinimumSize(new Dimension(150, 20));
      infoPanel.add(Box.createHorizontalGlue());
      infoPanel.add(new JLabel("Name of the created layer: "));
      infoPanel.add(txtLayer);
      infoPanel.add(Box.createHorizontalGlue());
      infoPanel.add(new JLabel("Select the buildings layer: "));
      infoPanel.add(cbBuildings);
      infoPanel.add(Box.createHorizontalGlue());
      infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));

      JPanel paramPanel = new JPanel();
      SpinnerModel bufferModel = new SpinnerNumberModel(25.0, 5.0, 250.0, 5.0);
      spinBuffer = new JSpinner(bufferModel);
      spinBuffer.setPreferredSize(new Dimension(60, 20));
      spinBuffer.setMaximumSize(new Dimension(60, 20));
      spinBuffer.setMinimumSize(new Dimension(60, 20));
      SpinnerModel areaModel = new SpinnerNumberModel(10000.0, 1000.0, 200000.0,
          1000.0);
      spinArea = new JSpinner(areaModel);
      spinArea.setPreferredSize(new Dimension(80, 20));
      spinArea.setMaximumSize(new Dimension(80, 20));
      spinArea.setMinimumSize(new Dimension(80, 20));
      SpinnerModel erosionModel = new SpinnerNumberModel(10.0, 1.0, 200.0, 1.0);
      spinErosion = new JSpinner(erosionModel);
      spinErosion.setPreferredSize(new Dimension(60, 20));
      spinErosion.setMaximumSize(new Dimension(60, 20));
      spinErosion.setMinimumSize(new Dimension(60, 20));
      SpinnerModel quadrantModel = new SpinnerNumberModel(12, 2, 50, 1);
      spinQuadrant = new JSpinner(quadrantModel);
      spinQuadrant.setPreferredSize(new Dimension(60, 20));
      spinQuadrant.setMaximumSize(new Dimension(60, 20));
      spinQuadrant.setMinimumSize(new Dimension(60, 20));
      SpinnerModel douglasModel = new SpinnerNumberModel(5.0, 1.0, 250.0, 1.0);
      spinDouglas = new JSpinner(douglasModel);
      spinDouglas.setPreferredSize(new Dimension(60, 20));
      spinDouglas.setMaximumSize(new Dimension(60, 20));
      spinDouglas.setMinimumSize(new Dimension(60, 20));
      SpinnerModel holeModel = new SpinnerNumberModel(800.0, 500.0, 50000.0,
          100.0);
      spinHole = new JSpinner(holeModel);
      spinHole.setPreferredSize(new Dimension(80, 20));
      spinHole.setMaximumSize(new Dimension(80, 20));
      spinHole.setMinimumSize(new Dimension(80, 20));
      JPanel paramPanel1 = new JPanel();
      paramPanel1.add(new JLabel("Buffer size: "));
      paramPanel1.add(spinBuffer);
      paramPanel1.add(Box.createHorizontalGlue());
      paramPanel1.add(new JLabel("Erosion threshold: "));
      paramPanel1.add(spinErosion);
      paramPanel1.add(Box.createHorizontalGlue());
      paramPanel1.add(new JLabel("Number of segments in buffers: "));
      paramPanel1.add(spinQuadrant);
      paramPanel1.setLayout(new BoxLayout(paramPanel1, BoxLayout.X_AXIS));
      JPanel paramPanel2 = new JPanel();
      paramPanel2.add(new JLabel("Douglas&Peucker: "));
      paramPanel2.add(spinDouglas);
      paramPanel2.add(Box.createHorizontalGlue());
      paramPanel2.add(new JLabel("Minimum area: "));
      paramPanel2.add(spinArea);
      paramPanel2.add(Box.createHorizontalGlue());
      paramPanel2.add(new JLabel("Minimum hole area: "));
      paramPanel2.add(spinHole);
      paramPanel2.setLayout(new BoxLayout(paramPanel2, BoxLayout.X_AXIS));

      paramPanel.add(paramPanel1);
      paramPanel.add(Box.createVerticalStrut(5));
      paramPanel.add(paramPanel2);
      paramPanel.setBorder(BorderFactory.createTitledBorder(
          BorderFactory.createLineBorder(Color.BLACK), "Parameters"));
      paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.Y_AXIS));

      // define a panel with the OK and Cancel buttons
      JPanel btnPanel = new JPanel();
      JButton okBtn = new JButton("OK");
      okBtn.addActionListener(this);
      okBtn.setActionCommand("OK");
      JButton cancelBtn = new JButton("Cancel");
      cancelBtn.addActionListener(this);
      cancelBtn.setActionCommand("cancel");
      btnPanel.add(okBtn);
      btnPanel.add(cancelBtn);
      btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

      this.getContentPane().add(infoPanel);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(paramPanel);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(btnPanel);
      this.getContentPane()
          .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.setAlwaysOnTop(true);
      this.pack();
    }
  }

  /**
   * Creates a new layer with the urban areas computed from a buildings layer,
   * using the citiness index method from (Chaudhry & Mackaness 2008).
   * 
   * @author GTouya
   * 
   */
  class CitinessUrbanAreasAction extends AbstractAction {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      CitinessUrbanAreasFrame frame = new CitinessUrbanAreasFrame(application);
      frame.setVisible(true);
    }

    public CitinessUrbanAreasAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Create urban areas from buildings using the method from (Chaudhry & Mackaness 2008)");
      this.putValue(Action.NAME,
          "Create urban areas from buildings (Chaudhry & Mackaness 2008)");
    }
  }

  class CitinessUrbanAreasFrame extends JFrame implements ActionListener {

    /****/
    private static final long serialVersionUID = 1L;

    private GeOxygeneApplication application;
    private JTextField txtLayer;
    private JComboBox<String> cbBuildings;
    private JSpinner spinNbNeighbours, spinK, spinQuadrant, spinDouglas,
        spinArea, spinHole;

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("OK")) {
        IFeatureCollection<? extends IFeature> buildings = application
            .getMainFrame().getSelectedProjectFrame()
            .getLayer(cbBuildings.getSelectedItem().toString())
            .getFeatureCollection();

        IGeometry complex = UrbanAreaComputationJTS.computeCitinessBuiltUpArea(
            buildings, (Integer) spinNbNeighbours.getValue(),
            (Double) spinK.getValue(), (Double) spinArea.getValue(),
            (Integer) spinQuadrant.getValue(), (Double) spinDouglas.getValue(),
            (Double) spinHole.getValue());
        IFeatureCollection<DefaultFeature> urbanAreas = new FT_FeatureCollection<>();
        FeatureType ft = new FeatureType();
        ft.setNomClasse(txtLayer.getText());
        ft.setGeometryType(IPolygon.class);
        urbanAreas.setFeatureType(ft);
        if (complex instanceof IPolygon)
          urbanAreas.add(new DefaultFeature(complex));
        else if (complex instanceof IMultiSurface<?>) {
          for (IPolygon simple : ((IMultiSurface<IPolygon>) complex)) {
            if (simple == null)
              continue;
            if (simple.area() < (Double) spinArea.getValue())
              continue;
            urbanAreas.add(new DefaultFeature(simple));
          }
        }

        // put the urban areas in a new layer
        ProjectFrame project = application.getMainFrame()
            .getSelectedProjectFrame();
        project.addUserLayer(urbanAreas, txtLayer.getText(), null);
        this.dispose();
      } else {
        this.dispose();
      }
    }

    CitinessUrbanAreasFrame(GeOxygeneApplication application) {
      super("Create urban areas from buildings (Chaudhry & Mackaness 2008)");
      this.application = application;
      this.setPreferredSize(new Dimension(650, 200));

      JPanel infoPanel = new JPanel();
      txtLayer = new JTextField("builtupAreas");
      txtLayer.setPreferredSize(new Dimension(120, 20));
      txtLayer.setMaximumSize(new Dimension(120, 20));
      txtLayer.setMinimumSize(new Dimension(120, 20));
      DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
      for (Layer layer : application.getMainFrame().getSelectedProjectFrame()
          .getLayers())
        model.addElement(layer.getName());
      cbBuildings = new JComboBox<String>(model);
      cbBuildings.setPreferredSize(new Dimension(150, 20));
      cbBuildings.setMaximumSize(new Dimension(150, 20));
      cbBuildings.setMinimumSize(new Dimension(150, 20));
      infoPanel.add(Box.createHorizontalGlue());
      infoPanel.add(new JLabel("Name of the created layer: "));
      infoPanel.add(txtLayer);
      infoPanel.add(Box.createHorizontalGlue());
      infoPanel.add(new JLabel("Select the buildings layer: "));
      infoPanel.add(cbBuildings);
      infoPanel.add(Box.createHorizontalGlue());
      infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));

      JPanel paramPanel = new JPanel();
      SpinnerModel nbNeighboursModel = new SpinnerNumberModel(20, 1, 100, 1);
      spinNbNeighbours = new JSpinner(nbNeighboursModel);
      spinNbNeighbours.setPreferredSize(new Dimension(60, 20));
      spinNbNeighbours.setMaximumSize(new Dimension(60, 20));
      spinNbNeighbours.setMinimumSize(new Dimension(60, 20));
      SpinnerModel areaModel = new SpinnerNumberModel(10000.0, 1000.0, 200000.0,
          1000.0);
      spinArea = new JSpinner(areaModel);
      spinArea.setPreferredSize(new Dimension(80, 20));
      spinArea.setMaximumSize(new Dimension(80, 20));
      spinArea.setMinimumSize(new Dimension(80, 20));
      SpinnerModel kModel = new SpinnerNumberModel(60.0, 10.0, 200.0, 5.0);
      spinK = new JSpinner(kModel);
      spinK.setPreferredSize(new Dimension(60, 20));
      spinK.setMaximumSize(new Dimension(60, 20));
      spinK.setMinimumSize(new Dimension(60, 20));
      SpinnerModel quadrantModel = new SpinnerNumberModel(12, 2, 50, 1);
      spinQuadrant = new JSpinner(quadrantModel);
      spinQuadrant.setPreferredSize(new Dimension(60, 20));
      spinQuadrant.setMaximumSize(new Dimension(60, 20));
      spinQuadrant.setMinimumSize(new Dimension(60, 20));
      SpinnerModel douglasModel = new SpinnerNumberModel(5.0, 1.0, 250.0, 1.0);
      spinDouglas = new JSpinner(douglasModel);
      spinDouglas.setPreferredSize(new Dimension(60, 20));
      spinDouglas.setMaximumSize(new Dimension(60, 20));
      spinDouglas.setMinimumSize(new Dimension(60, 20));
      SpinnerModel holeModel = new SpinnerNumberModel(800.0, 500.0, 50000.0,
          100.0);
      spinHole = new JSpinner(holeModel);
      spinHole.setPreferredSize(new Dimension(80, 20));
      spinHole.setMaximumSize(new Dimension(80, 20));
      spinHole.setMinimumSize(new Dimension(80, 20));
      JPanel paramPanel1 = new JPanel();
      paramPanel1.add(new JLabel("Nb neighbours for citiness: "));
      paramPanel1.add(spinNbNeighbours);
      paramPanel1.add(Box.createHorizontalGlue());
      paramPanel1.add(new JLabel("k constant: "));
      paramPanel1.add(spinK);
      paramPanel1.add(Box.createHorizontalGlue());
      paramPanel1.add(new JLabel("Number of segments in buffers: "));
      paramPanel1.add(spinQuadrant);
      paramPanel1.setLayout(new BoxLayout(paramPanel1, BoxLayout.X_AXIS));
      JPanel paramPanel2 = new JPanel();
      paramPanel2.add(new JLabel("Douglas&Peucker: "));
      paramPanel2.add(spinDouglas);
      paramPanel2.add(Box.createHorizontalGlue());
      paramPanel2.add(new JLabel("Minimum area: "));
      paramPanel2.add(spinArea);
      paramPanel2.add(Box.createHorizontalGlue());
      paramPanel2.add(new JLabel("Minimum hole area: "));
      paramPanel2.add(spinHole);
      paramPanel2.setLayout(new BoxLayout(paramPanel2, BoxLayout.X_AXIS));

      paramPanel.add(paramPanel1);
      paramPanel.add(Box.createVerticalStrut(5));
      paramPanel.add(paramPanel2);
      paramPanel.setBorder(BorderFactory.createTitledBorder(
          BorderFactory.createLineBorder(Color.BLACK), "Parameters"));
      paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.Y_AXIS));

      // define a panel with the OK and Cancel buttons
      JPanel btnPanel = new JPanel();
      JButton okBtn = new JButton("OK");
      okBtn.addActionListener(this);
      okBtn.setActionCommand("OK");
      JButton cancelBtn = new JButton("Cancel");
      cancelBtn.addActionListener(this);
      cancelBtn.setActionCommand("cancel");
      btnPanel.add(okBtn);
      btnPanel.add(cancelBtn);
      btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

      this.getContentPane().add(infoPanel);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(paramPanel);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(btnPanel);
      this.getContentPane()
          .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.setAlwaysOnTop(true);
      this.pack();
    }
  }

  /**
   * Computes the turning function (Arkin et al 1991) on the polygon geometry of
   * the selected feature, and displays the function in a plot in a frame.
   * 
   * @author GTouya
   * 
   */
  class DisplayTurningFuncAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      IFeature feat = SelectionUtil.getFirstSelectedObject(application);
      if (feat.getGeom() instanceof IPolygon) {
        PolygonTurningFunction function = new PolygonTurningFunction(
            (IPolygon) feat.getGeom());
        function.print();
      }
    }

    public DisplayTurningFuncAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Display in a frame the turning function of selected polygon feature");
      this.putValue(Action.NAME,
          "Display turning function of selected polygon");
    }
  }

  /**
   * Computes the turning functions (Arkin et al 1991) on the polygons geometry
   * of the two selected feature, and displays the functions in a single plot in
   * a frame, in order to compare them.
   * 
   * @author GTouya
   * 
   */
  class CompareTurningFuncsAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      Iterator<IFeature> iterator = SelectionUtil
          .getSelectedObjects(application).iterator();
      IFeature feat1 = iterator.next();
      IFeature feat2 = iterator.next();
      if (feat1.getGeom() instanceof IPolygon
          && feat2.getGeom() instanceof IPolygon) {
        PolygonTurningFunction function1 = new PolygonTurningFunction(
            (IPolygon) feat1.getGeom());
        PolygonTurningFunction function2 = new PolygonTurningFunction(
            (IPolygon) feat2.getGeom());
        function1.print(function2);
      }
    }

    public CompareTurningFuncsAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Compare the two turning functions of the two selected features in a single plot");
      this.putValue(Action.NAME, "Compare two turning functions");
    }
  }

  /**
   * Computes the signature function (Vauglin & Bel Hadj Ali 1998) on the
   * polygon geometry of the selected feature, and displays the function in a
   * plot in a frame.
   * 
   * @author GTouya
   * 
   */
  class DisplaySignatureFuncAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      IFeature feat = SelectionUtil.getFirstSelectedObject(application);
      if (feat.getGeom() instanceof IPolygon) {
        PolygonSignatureFunction function = new PolygonSignatureFunction(
            (IPolygon) feat.getGeom());
        function.print();
      }
    }

    public DisplaySignatureFuncAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Display in a frame the signature function of selected polygon feature");
      this.putValue(Action.NAME,
          "Display signature function of selected polygon");
    }
  }

  /**
   * Computes the signature functions (Vauglin & Bel Hadj Ali 1998) on the
   * polygons geometry of the two selected feature, and displays the functions
   * in a single plot in a frame, in order to compare them.
   * 
   * @author GTouya
   * 
   */
  class CompareSignatureFuncsAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      Iterator<IFeature> iterator = SelectionUtil
          .getSelectedObjects(application).iterator();
      IFeature feat1 = iterator.next();
      IFeature feat2 = iterator.next();
      if (feat1.getGeom() instanceof IPolygon
          && feat2.getGeom() instanceof IPolygon) {
        PolygonSignatureFunction function1 = new PolygonSignatureFunction(
            (IPolygon) feat1.getGeom());
        PolygonSignatureFunction function2 = new PolygonSignatureFunction(
            (IPolygon) feat2.getGeom());
        function1.print(function2);
      }
    }

    public CompareSignatureFuncsAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Compare the two signature functions of the two selected features in a single plot");
      this.putValue(Action.NAME, "Compare two signature functions");
    }
  }

  /**
   * Add training data to a training data file that can be used to train a SVM
   * building classifier.
   * 
   * @author GTouya
   * 
   */
  class AddTrainingDataAction extends AbstractAction {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      AddTrainingDataFrame frame = new AddTrainingDataFrame(application);
      frame.setVisible(true);
    }

    public AddTrainingDataAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Add training data to classify buildings using the method from (Steiniger et al 2008)");
      this.putValue(Action.NAME, "Add Training Data");
    }
  }

  class AddTrainingDataFrame extends JFrame implements ActionListener {

    /****/
    private static final long serialVersionUID = 1L;
    private GeOxygeneApplication application;
    private JComboBox<BuildingClass> comboClass;
    private JComboBox<String> comboLayer;
    private JRadioButton radioNew, radioExisting;
    private JTextField txtFile;
    private JButton btnBrowse;

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("OK")) {
        Set<IFeature> buildings = SelectionUtil.getSelectedObjects(application);
        if (radioNew.isSelected()) {
          try {
            createNewTrainingSet(buildings);
          } catch (Exception e1) {
            e1.printStackTrace();
          }
        } else {
          try {
            addToTrainingSet(buildings);
          } catch (Exception e1) {
            e1.printStackTrace();
          }
        }
        this.dispose();
      }
      if (e.getActionCommand().equals("browse")) {
        // On choisit le fichier
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new XMLFileFilter());
        int returnVal = fc.showDialog(null, "Choose the training set file");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
          return;
        }

        // path est le chemin jusqu'au fichier
        File path = fc.getSelectedFile();
        this.txtFile.setText(path.getPath());

      } else {
        this.dispose();
      }
    }

    private void addToTrainingSet(Set<IFeature> buildings) throws Exception {
      IFeatureCollection<IFeature> fc = new FT_FeatureCollection<>();
      for (IFeature feat : application.getMainFrame().getSelectedProjectFrame()
          .getLayer((String) comboLayer.getSelectedItem())
          .getFeatureCollection()) {
        if (feat.getGeom() instanceof IPolygon)
          fc.add(feat);
      }
      BuildingClassifierSVM classifier = new BuildingClassifierSVM(fc);
      TrainingData trainingData = classifier.new TrainingData(
          new File(txtFile.getText()), classifier.getDescriptorNames());
      if (((BuildingClass) comboClass.getSelectedItem())
          .equals(BuildingClass.INNER_CITY))
        classifier.addInnerCityExamples(trainingData, buildings);
      if (((BuildingClass) comboClass.getSelectedItem())
          .equals(BuildingClass.URBAN))
        classifier.addUrbanExamples(trainingData, buildings);
      if (((BuildingClass) comboClass.getSelectedItem())
          .equals(BuildingClass.SUBURBAN))
        classifier.addSuburbanExamples(trainingData, buildings);
      if (((BuildingClass) comboClass.getSelectedItem())
          .equals(BuildingClass.RURAL))
        classifier.addRuralExamples(trainingData, buildings);
      if (((BuildingClass) comboClass.getSelectedItem())
          .equals(BuildingClass.INDUSTRY))
        classifier.addIndustrialExamples(trainingData, buildings);

      trainingData.writeToXml(new File(txtFile.getText()));
    }

    private void createNewTrainingSet(Set<IFeature> buildings)
        throws Exception {
      IFeatureCollection<IFeature> fc = new FT_FeatureCollection<>();
      for (IFeature feat : application.getMainFrame().getSelectedProjectFrame()
          .getLayer((String) comboLayer.getSelectedItem())
          .getFeatureCollection()) {
        if (feat.getGeom() instanceof IPolygon)
          fc.add(feat);
      }
      BuildingClassifierSVM classifier = new BuildingClassifierSVM(fc);
      TrainingData trainingData = classifier.new TrainingData();
      if (((BuildingClass) comboClass.getSelectedItem())
          .equals(BuildingClass.INNER_CITY))
        classifier.addInnerCityExamples(trainingData, buildings);
      if (((BuildingClass) comboClass.getSelectedItem())
          .equals(BuildingClass.URBAN))
        classifier.addUrbanExamples(trainingData, buildings);
      if (((BuildingClass) comboClass.getSelectedItem())
          .equals(BuildingClass.SUBURBAN))
        classifier.addSuburbanExamples(trainingData, buildings);
      if (((BuildingClass) comboClass.getSelectedItem())
          .equals(BuildingClass.RURAL))
        classifier.addRuralExamples(trainingData, buildings);
      if (((BuildingClass) comboClass.getSelectedItem())
          .equals(BuildingClass.INDUSTRY))
        classifier.addIndustrialExamples(trainingData, buildings);

      trainingData.writeToXml(new File(txtFile.getText()));
    }

    public AddTrainingDataFrame(GeOxygeneApplication application) {
      super("Add training data to a file for the SVM building classifier");
      this.application = application;
      this.setPreferredSize(new Dimension(750, 200));

      // define a panel with the OK and Cancel buttons
      JPanel btnPanel = new JPanel();
      JButton okBtn = new JButton("OK");
      okBtn.addActionListener(this);
      okBtn.setActionCommand("OK");
      JButton cancelBtn = new JButton("Cancel");
      cancelBtn.addActionListener(this);
      cancelBtn.setActionCommand("cancel");
      btnPanel.add(okBtn);
      btnPanel.add(cancelBtn);
      btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

      JPanel infoPanel = new JPanel();
      comboClass = new JComboBox<BuildingClass>(BuildingClass.values());
      comboClass.setPreferredSize(new Dimension(150, 20));
      comboClass.setMaximumSize(new Dimension(150, 20));
      comboClass.setMinimumSize(new Dimension(150, 20));
      radioNew = new JRadioButton("new training set");
      radioExisting = new JRadioButton("existing training set");
      ButtonGroup bg = new ButtonGroup();
      bg.add(radioNew);
      bg.add(radioExisting);
      radioNew.setSelected(true);
      infoPanel.add(new JLabel("Building class of the chosen examples"));
      infoPanel.add(comboClass);
      infoPanel.add(Box.createHorizontalGlue());
      infoPanel.add(radioNew);
      infoPanel.add(radioExisting);
      infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));

      JPanel filePanel = new JPanel();
      txtFile = new JTextField();
      txtFile.setPreferredSize(new Dimension(250, 20));
      txtFile.setMaximumSize(new Dimension(250, 20));
      txtFile.setMinimumSize(new Dimension(250, 20));
      btnBrowse = new JButton();
      btnBrowse.addActionListener(this);
      btnBrowse.setActionCommand("browse");
      DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
      for (Layer layer : application.getMainFrame().getSelectedProjectFrame()
          .getLayers())
        model.addElement(layer.getName());
      comboLayer = new JComboBox<String>(model);
      comboLayer.setPreferredSize(new Dimension(150, 20));
      comboLayer.setMaximumSize(new Dimension(150, 20));
      comboLayer.setMinimumSize(new Dimension(150, 20));
      filePanel.add(txtFile);
      filePanel.add(btnBrowse);
      filePanel.add(Box.createHorizontalGlue());
      filePanel.add(comboLayer);
      filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.X_AXIS));

      this.getContentPane().add(infoPanel);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(filePanel);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(btnPanel);
      this.getContentPane()
          .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.setAlwaysOnTop(true);
      this.pack();
    }
  }

  /**
   * Given a training set, classifies the selected buildings with a SVM
   * classifier.
   * 
   * @author GTouya
   * 
   */
  class BuildingClassifSVMAction extends AbstractAction {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      IFeatureCollection<IFeature> buildings = new FT_FeatureCollection<>();
      Layer layer = null;
      for (IFeature feat : SelectionUtil.getSelectedObjects(application)) {
        if (feat.getGeom() instanceof IPolygon)
          buildings.add(feat);
        if (layer == null)
          layer = application.getMainFrame().getSelectedProjectFrame()
              .getLayerFromFeature(feat);
      }
      // On choisit le fichier
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new XMLFileFilter());
      int returnVal = fc.showDialog(null, "Choose the training set file");
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }

      // path est le chemin jusqu'au fichier
      File path = fc.getSelectedFile();

      Style style = layer.getStyles().get(layer.getStyles().size() - 1);

      BuildingClassifierSVM classifier = new BuildingClassifierSVM(buildings);
      classifier.removeDescriptor(BuildingDescriptor.BCy);
      try {
        TrainingData trainingData = classifier.new TrainingData(path,
            classifier.getDescriptorNames());
        System.out.println("start training...");
        classifier.train(trainingData);
        System.out.println("...end of training");
        for (IFeature building : buildings) {
          BuildingClass result = classifier.predict(building);
          System.out.println(
              "prediction for building " + building + " is: " + result.name());
          // display the output
          PolygonSymbolizer symbolizer = new PolygonSymbolizer();
          Fill fill = new Fill();
          fill.setColor(result.getColor());
          symbolizer.setFill(fill);
          SLDUtil.addFeatureRule(style, building, "classifier output",
              symbolizer);
        }
      } catch (ParserConfigurationException | SAXException | IOException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    public BuildingClassifSVMAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Classifies the selected building using a SVM Building Classifier and the method from (Steiniger et al 2008)");
      this.putValue(Action.NAME, "SVM Building Classifier");
    }
  }

}
