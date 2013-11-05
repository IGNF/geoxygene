package fr.ign.cogit.geoxygene.appli.plugin.osm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.bind.JAXBException;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.DataSetZone;
import fr.ign.cogit.cartagen.software.dataset.DigitalCartographicModel;
import fr.ign.cogit.cartagen.software.dataset.GeographicClass;
import fr.ign.cogit.cartagen.software.dataset.PostgisDB;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.OsmFileFilter;
import fr.ign.cogit.cartagen.spatialanalysis.clustering.AdjacencyClustering;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerFactory;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.ProjectFramePlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenProjectPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.NamedLayer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.UserStyle;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;
import fr.ign.cogit.osm.importexport.OSMLoader;
import fr.ign.cogit.osm.importexport.OSMLoader.OsmLoadingTask;
import fr.ign.cogit.osm.importexport.OpenStreetMapDb;
import fr.ign.cogit.osm.importexport.OsmDataset;
import fr.ign.cogit.osm.schema.OsmGeneObj;
import fr.ign.cogit.osm.schema.network.OsmNetworkSection;
import fr.ign.cogit.osm.schema.urban.OsmBuilding;

public class OSMPlugin implements ProjectFramePlugin,
    GeOxygeneApplicationPlugin {

  private GeOxygeneApplication application = null;
  private Runnable fillLayersTask;

  @Override
  public void initialize(GeOxygeneApplication application) {
    this.application = application;
    JMenu menu = new JMenu("OSM");
    menu.add(new JMenuItem(new ImportOSMFileAction()));
    menu.addSeparator();
    menu.add(new JMenuItem(new BrowseTagsAction()));
    menu.addSeparator();
    JMenu correctionMenu = new JMenu("Data Correction");
    correctionMenu.add(new JMenuItem(new AggrBuildingsAction()));
    correctionMenu.add(new JMenuItem(new PlanarNetworkAction()));
    menu.add(correctionMenu);
    menu.addSeparator();
    JMenu analysisMenu = new JMenu("LoD Analysis");

    JMenu harmoniseMenu = new JMenu("LoD Harmonisation");
    menu.add(analysisMenu);
    menu.add(harmoniseMenu);
    // TODO Auto-generated method stub
    application.getMainFrame().getMenuBar()
        .add(menu, application.getMainFrame().getMenuBar().getMenuCount() - 2);
  }

  @Override
  public void initialize(ProjectFrame projectFrame) {
    // TODO ajoute un écouteur pour ouvrir le browser de tags.

  }

  /**
   * Create a new empty dataset, with its zone details, in which data can be
   * added later.
   * 
   * @author GTouya
   * 
   */
  public class ImportOSMFileAction extends AbstractAction implements
      PropertyChangeListener {

    /****/
    private static final long serialVersionUID = 1L;
    private JDialog dialog;
    private JTextArea taskOutput;
    private JProgressBar progressBar;
    private JLabel taskLabel;
    private OSMLoader loader;
    private OsmLoadingTask currentTask = OsmLoadingTask.POINTS;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new OsmFileFilter());
      fc.setCurrentDirectory(new File("src/main/resources/XML/"));
      int returnVal = fc.showSaveDialog(CartagenApplication.getInstance()
          .getFrame());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File file = fc.getSelectedFile();
      CartAGenDoc doc = CartAGenDoc.getInstance();
      String name = null;
      if (doc.getName() == null) {
        name = file.getName().substring(0, file.getName().length() - 4);
        doc.setName(name);
        doc.setPostGisDb(PostgisDB.get(name, true));
      }
      // build database & dataset
      CartAGenDoc.getInstance().setZone(new DataSetZone(name, null));
      // create the new CartAGen dataset
      final OpenStreetMapDb database = new OpenStreetMapDb(name);
      database.setSourceDLM(SourceDLM.OpenStreetMap);
      database.setSymboScale(25000);
      database.setDocument(CartAGenDoc.getInstance());
      OsmDataset dataset = new OsmDataset();
      CartAGenDoc.getInstance().addDatabase(name, database);
      CartAGenDoc.getInstance().setCurrentDataset(dataset);
      database.setDataSet(dataset);
      database.setType(new DigitalCartographicModel());

      fillLayersTask = new Runnable() {
        public void run() {
          try {
            addOsmDatabaseToFrame(database);
          } catch (JAXBException e) {
            e.printStackTrace();
          }
          application.getMainFrame().getGui().setCursor(null);
        }
      };

      loader = new OSMLoader(file, dataset, fillLayersTask);
      createDialog();
      loader.setDialog(dialog);
      dialog.setVisible(true);
      application.getMainFrame().getGui()
          .setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      loader.addPropertyChangeListener(this);
      loader.execute();

    }

    public ImportOSMFileAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Import OSM data from file in a new Dataset");
      this.putValue(Action.NAME, "Import OSM File");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      if ("progress" == evt.getPropertyName()) {
        int progress = (Integer) evt.getNewValue();
        progressBar.setValue(progress);
        taskOutput.append(String.format("Completed %d%% of task.\n",
            loader.getProgress()));
        if (!currentTask.equals(loader.getCurrentTask())) {
          this.currentTask = loader.getCurrentTask();
          taskLabel.setText(currentTask.getLabel() + " loading...");
        }
      }
    }

    private void createDialog() {
      JPanel panel = new JPanel(new BorderLayout());

      taskLabel = new JLabel(loader.getCurrentTask().getLabel() + " loading...");
      progressBar = new JProgressBar(0, 100);
      progressBar.setValue(0);
      progressBar.setStringPainted(true);

      taskOutput = new JTextArea(5, 20);
      taskOutput.setMargin(new Insets(5, 5, 5, 5));
      taskOutput.setEditable(false);

      JPanel panel1 = new JPanel();
      panel1.add(taskLabel);
      panel1.add(progressBar);

      panel.add(panel1, BorderLayout.PAGE_START);
      panel.add(new JScrollPane(taskOutput), BorderLayout.CENTER);
      panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
      dialog = new JDialog(application.getMainFrame().getGui());
      dialog.add(panel);
      dialog.pack();
    }
  }

  /**
   * Create a new empty dataset, with its zone details, in which data can be
   * added later.
   * 
   * @author GTouya
   * 
   */
  class BrowseTagsAction extends AbstractAction {

    /**
   * 
   */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      int x = (int) CartagenApplication.getInstance().getFrame().getVisuPanel()
          .getBounds().getCenterX();
      int y = (int) CartagenApplication.getInstance().getFrame().getVisuPanel()
          .getBounds().getCenterY();
      List<OsmGeneObj> selectedObjs = new ArrayList<OsmGeneObj>();
      for (IFeature obj : SelectionUtil.getSelectedObjects(application)) {
        if (!(obj instanceof OsmGeneObj)) {
          continue;
        }
        selectedObjs.add((OsmGeneObj) obj);
      }
      try {
        OSMTagBrowser browser = new OSMTagBrowser(new Point(x, y), selectedObjs);
        browser.setVisible(true);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      }
    }

    public BrowseTagsAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Browse the OSM tags of the selected OSM features");
      this.putValue(Action.NAME, "Browse Tags");
    }
  }

  /**
   * Create a new empty dataset, with its zone details, in which data can be
   * added later.
   * 
   * @author GTouya
   * 
   */
  class AggrBuildingsAction extends AbstractAction {

    /**
   * 
   */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent arg0) {
      try {
        // get the building population
        IPopulation<IBuilding> pop = CartAGenDoc.getInstance()
            .getCurrentDataset().getBuildings();
        AdjacencyClustering clusters = new AdjacencyClustering(pop);
        System.out.println("début clustering");
        Set<Set<IGeneObj>> clusterSet = clusters.getClusters();
        System.out.println(clusterSet.size() + " clusters");
        for (Set<IGeneObj> cluster : clusterSet) {
          OsmBuilding remainingB = null;
          int maxTags = 0;
          List<Geometry> list = new ArrayList<Geometry>();
          for (IGeneObj build : cluster) {
            list.add(JtsGeOxygene.makeJtsGeom(build.getGeom()));
            build.eliminate();
            if (((OsmGeneObj) build).getTags().size() > maxTags) {
              maxTags = ((OsmGeneObj) build).getTags().size();
              remainingB = (OsmBuilding) build;
            }
          }
          if (remainingB == null) {
            return;
          }
          // union of the geometries
          Geometry jtsUnion = JtsAlgorithms.union(list);
          IGeometry union = JtsGeOxygene.makeGeOxygeneGeom(jtsUnion);
          if (union instanceof IPolygon) {
            remainingB.cancelElimination();
            remainingB.setGeom(union);
          } else if (union instanceof IMultiSurface) {
            for (int i = 0; i < ((IMultiSurface<IOrientableSurface>) union)
                .getList().size(); i++) {
              IGeometry newGeom = ((IMultiSurface<IOrientableSurface>) union)
                  .getList().get(i);
              if (i == 0) {
                remainingB.cancelElimination();
                remainingB.setGeom(newGeom);
              } else {
                OsmBuilding newBuilding = new OsmBuilding(
                    remainingB.getContributor(), newGeom, remainingB.getId(),
                    remainingB.getChangeSet(), remainingB.getVersion(),
                    remainingB.getUid(), remainingB.getDate());
                pop.add(newBuilding);
              }
            }
          }
          System.out.println("1 cluster traité");
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    public AggrBuildingsAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Aggregate the intersecting buildings to correct the digitising problems");
      this.putValue(Action.NAME, "Aggregate buildings");
    }
  }

  /**
   * Make the chosen network planar cutting sections at intersections with other
   * sections. Cut sections keep the same tags.
   * 
   * @author GTouya
   * 
   */
  class PlanarNetworkAction extends AbstractAction {

    /**
   * 
   */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      PlanarNetworkFrame frame = new PlanarNetworkFrame();
      frame.setVisible(true);
    }

    public PlanarNetworkAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Make the chosen network planar cutting sections at intersections");
      this.putValue(Action.NAME, "Make network planar");
    }
  }

  class PlanarNetworkFrame extends JFrame implements ActionListener {

    /****/
    private static final long serialVersionUID = 1L;
    private JComboBox combo;

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("cancel")) {
        this.setVisible(false);
      } else {
        IPopulation<IGeneObj> iterable = new Population<IGeneObj>();
        IPopulation<IGeneObj> pop = CartAGenDoc.getInstance()
            .getCurrentDataset()
            .getCartagenPop((String) this.combo.getSelectedItem());
        iterable.addAll(pop);
        CarteTopo carteTopo = new CarteTopo("make OSM planar");
        carteTopo.importClasseGeo(pop, true);
        carteTopo.rendPlanaire(1.0);
        for (IGeneObj obj : iterable) {
          if (!(obj instanceof OsmNetworkSection)) {
            continue;
          }
          OsmNetworkSection section = (OsmNetworkSection) obj;
          try {
            // test if the section has been cut by topological map
            if (section.getCorrespondants().size() == 1) {
              continue;
            }

            // update the section geometry with the first edge of
            // the
            // topological
            // map
            section.setGeom(section.getCorrespondants().get(0).getGeom());

            // loop on the other edges to make new instances
            Class<? extends IGeneObj> classObj = obj.getClass();
            Constructor<? extends IGeneObj> constr;

            constr = classObj.getConstructor(String.class, IGeometry.class,
                int.class, int.class, int.class, int.class, Date.class);

            for (int i = 1; i < section.getCorrespondants().size(); i++) {
              ILineString newLine = (ILineString) section.getCorrespondants()
                  .get(i).getGeom();
              OsmNetworkSection newObj = (OsmNetworkSection) constr
                  .newInstance(section.getContributor(), newLine,
                      section.getId(), section.getChangeSet(),
                      section.getVersion(), section.getUid(), section.getDate());
              newObj.setTags(section.getTags());
              newObj.setImportance(section.getImportance());
              pop.add(newObj);
            }

          } catch (SecurityException e1) {
            e1.printStackTrace();
          } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
          } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
          } catch (InstantiationException e1) {
            e1.printStackTrace();
          } catch (IllegalAccessException e1) {
            e1.printStackTrace();
          } catch (InvocationTargetException e1) {
            e1.printStackTrace();
          }
        }
        this.setVisible(false);
      }
    }

    PlanarNetworkFrame() {
      super("Make network planar");
      this.setSize(200, 200);
      this.combo = new JComboBox(new String[] { CartAGenDataSet.ROADS_POP,
          CartAGenDataSet.RAILWAY_LINES_POP, CartAGenDataSet.PATHS_POP,
          CartAGenDataSet.WATER_LINES_POP });
      this.combo.setMaximumSize(new Dimension(140, 30));
      this.combo.setMinimumSize(new Dimension(140, 30));
      this.combo.setPreferredSize(new Dimension(140, 30));

      // a panel for the buttons
      JPanel pButtons = new JPanel();
      JButton btnOk = new JButton("OK");
      btnOk.addActionListener(this);
      btnOk.setActionCommand("ok");
      JButton btnCancel = new JButton("Cancel");
      btnCancel.addActionListener(this);
      btnCancel.setActionCommand("cancel");
      pButtons.add(btnOk);
      pButtons.add(btnCancel);
      pButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));

      // layout of the frame
      this.getContentPane().add(this.combo);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(pButtons);
      this.getContentPane().setLayout(
          new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.pack();
    }
  }

  /**
   * Relates a {@link OpenSteetMapDB} to a {@link ProjectFrame} of the
   * application. Fills the layers of the project frame with the database
   * objects.
   * @param db
   */
  public void addOsmDatabaseToFrame(OpenStreetMapDb db) throws JAXBException {
    // s'il y a une seule project frame et qu'elle est vide, on la supprime
    if (application.getMainFrame().getDesktopProjectFrames().length == 1) {
      ProjectFrame frameIni = application.getMainFrame()
          .getDesktopProjectFrames()[0];
      if (frameIni.getLayers().size() == 0) {
        application.getMainFrame().removeAllProjectFrames();
      }
    }
    ProjectFrame frame = application.getMainFrame().newProjectFrame();
    CartAGenPlugin.getInstance().getMapDbFrame().put(db.getName(), frame);
    frame.getSld().setDataSet(db.getDataSet());
    StyledLayerDescriptor defaultSld = compileOsmSlds();
    StyledLayerDescriptor.unmarshall(OSMLoader.class.getClassLoader()
        .getResourceAsStream("sld/roads_sld.xml")); //$NON-NLS-1$
    float opacity = 0.8f;
    float strokeWidth = 1.0f;
    for (GeographicClass geoClass : db.getClasses()) {
      String populationName = db.getDataSet().getPopNameFromFeatType(
          geoClass.getFeatureTypeName());
      if (frame.getSld().getLayer(populationName) == null) {
        Color fillColor = new Color((float) Math.random(),
            (float) Math.random(), (float) Math.random());
        Layer layer = new NamedLayer(frame.getSld(), populationName);
        if (layer.getFeatureCollection().size() == 0)
          continue;
        if (defaultSld.getLayer(populationName) != null) {
          layer.getStyles().clear();
          layer.getStyles().addAll(
              defaultSld.getLayer(populationName).getStyles());
        } else {
          UserStyle style = new UserStyle();
          style.setName("Style créé pour le layer " + populationName);//$NON-NLS-1$
          FeatureTypeStyle fts = new FeatureTypeStyle();
          fts.getRules()
              .add(
                  LayerFactory.createRule(geoClass.getGeometryType(),
                      fillColor.darker(), fillColor, opacity, opacity,
                      strokeWidth));
          style.getFeatureTypeStyles().add(fts);
          layer.getStyles().add(style);
        }
        frame.getSld().add(layer);
      }
    }

    // initialise the frame with cartagen plugin
    CartAGenProjectPlugin.getInstance().initialize(frame);
  }

  private StyledLayerDescriptor compileOsmSlds() throws JAXBException {
    // load road sld
    StyledLayerDescriptor defaultSld = StyledLayerDescriptor
        .unmarshall(OSMLoader.class.getClassLoader().getResourceAsStream(
            "sld/roads_sld.xml")); //$NON-NLS-1$
    // load buildings sld
    StyledLayerDescriptor buildingSld = StyledLayerDescriptor
        .unmarshall(OSMLoader.class.getClassLoader().getResourceAsStream(
            "sld/buildings_sld.xml")); //$NON-NLS-1$
    for (Layer layer : buildingSld.getLayers())
      defaultSld.add(layer);
    // load waterway sld
    StyledLayerDescriptor waterSld = StyledLayerDescriptor
        .unmarshall(OSMLoader.class.getClassLoader().getResourceAsStream(
            "sld/waterway_sld.xml")); //$NON-NLS-1$
    for (Layer layer : waterSld.getLayers())
      defaultSld.add(layer);
    // load landuse sld
    StyledLayerDescriptor landuseSld = StyledLayerDescriptor
        .unmarshall(OSMLoader.class.getClassLoader().getResourceAsStream(
            "sld/landuse_sld.xml")); //$NON-NLS-1$
    for (Layer layer : landuseSld.getLayers())
      defaultSld.add(layer);
    // load point features sld
    StyledLayerDescriptor ptsSld = StyledLayerDescriptor
        .unmarshall(OSMLoader.class.getClassLoader().getResourceAsStream(
            "sld/point_features_sld.xml")); //$NON-NLS-1$
    for (Layer layer : ptsSld.getLayers())
      defaultSld.add(layer);
    // load railways sld
    StyledLayerDescriptor railSld = StyledLayerDescriptor
        .unmarshall(OSMLoader.class.getClassLoader().getResourceAsStream(
            "sld/railway_sld.xml")); //$NON-NLS-1$
    for (Layer layer : railSld.getLayers())
      defaultSld.add(layer);
    // TODO fill with the other SLDs
    return defaultSld;
  }
}
