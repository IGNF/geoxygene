/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.defaultschema.DefaultCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.software.dataset.DatabaseView;
import fr.ign.cogit.cartagen.software.dataset.GeneObjImplementation;
import fr.ign.cogit.cartagen.software.dataset.GeographicClass;
import fr.ign.cogit.cartagen.software.interfacecartagen.menus.ConfigMenuComponent;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.AbstractMainFrame;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.MainFrame;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerFactory;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.geompool.GeometryPoolMenu;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.NamedLayer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.UserStyle;

/**
 * CartAGen plugin.
 * @author Guillaume Touya
 */
public class CartAGenPlugin implements GeOxygeneApplicationPlugin,
    ActionListener {
  /**
   * Logger.
   */
  static Logger logger = Logger.getLogger(CartAGenPlugin.class.getName());

  protected static CartAGenPlugin instance = null;

  /**
   * Recuperation de l'instance unique (singleton)
   * @return instance unique (singleton) de CartagenApplication.
   */
  public static CartAGenPlugin getInstance() {
    return instance;
  }

  private GeOxygeneApplication application = null;

  protected CartAGenRightPanel rightPanel;
  private JSplitPane splitPane;
  private JCheckBoxMenuItem displayRightPanel = new JCheckBoxMenuItem(
      "Display cartaGen right panel");

  /**
   * The {@link CartAGenDocOld} document related to {@code this} plugin.
   */
  private CartAGenDoc document;

  private HashMap<String, ProjectFrame> mapDbFrame = new HashMap<String, ProjectFrame>();

  /**
   * The current GeneObj implementation for the plugin, i.e. the last one used
   * for a database loaded by the plugin.
   */
  private GeneObjImplementation currentGeneObjImpl = GeneObjImplementation
      .getDefaultImplementation();

  // config
  /**
     */
  public ConfigMenuComponent menuConfig = new ConfigMenuComponent(
      "CartAGen-Config");

  private final String cheminFichierConfigurationDonnees = "/configurationDonnees.xml";

  public String getCheminFichierConfigurationChargementDonnees() {
    return this.cheminFichierConfigurationDonnees;
  }

  private final String cheminFichierConfigurationGeneralisation = "/configurationGeneralisation.xml";

  public String getCheminFichierConfigurationGene() {
    return this.cheminFichierConfigurationGeneralisation;
  }

  private String cheminDonnees = null;
  private String cheminDonneesInitial = null;

  public String getCheminDonnees() {
    return this.cheminDonnees;
  }

  public void setCheminDonnees(String chemin) {
    this.cheminDonnees = chemin;
  }

  public String getCheminDonneesInitial() {
    return this.cheminDonneesInitial;
  }

  public void setCheminDonneesInitial(String chemin) {
    this.cheminDonneesInitial = chemin;
  }

  /**
   * Initialize the plugin.
   * @param application the application
   */
  @Override
  public void initialize(final GeOxygeneApplication application) {
    instance = this;
    CartagenApplication.getInstance().setCreationFactory(
        new DefaultCreationFactory());
    this.application = application;

    // add the right panel
    MainFrame frame = application.getMainFrame();
    JTabbedPane pane = ((AbstractMainFrame) frame).getDesktopTabbedPane();
    System.out.println("coucou");
    frame.getGui().getContentPane().remove(pane);
    rightPanel = new CartAGenRightPanel(frame);
    splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pane, rightPanel);
    splitPane.setContinuousLayout(false);
    splitPane.setOneTouchExpandable(false);
    splitPane.resetToPreferredSizes();
    splitPane.setResizeWeight(1.0);
    frame.getGui().getContentPane().add(splitPane);

    JMenuBar menuBar = application.getMainFrame().getMenuBar();
    JMenu cartagenMenu = new JMenu("CartAGen");

    // Add checkbox "display right panel"
    this.displayRightPanel.setSelected(false);
    this.displayRightPanel.addActionListener(this);
    this.displayRightPanel.setFont(menuBar.getFont());
    splitPane.remove(rightPanel);
    cartagenMenu.add(this.displayRightPanel);

    this.menuConfig.add(new GeometryPoolMenu("Geometry Pool", application));
    this.menuConfig.setFont(menuBar.getFont());
    menuBar.add(cartagenMenu, menuBar.getMenuCount() - 1);
    cartagenMenu.add(menuConfig);

    // ajout contenu generalisation au menu
    try {
      GeneralisationMenus.getInstance().add(application, menuBar, cartagenMenu);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    System.out.println("coucou fin");

  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    if (e.getSource() == displayRightPanel) {
      if (displayRightPanel.isSelected()) {
        splitPane.setRightComponent(rightPanel);
      } else {
        splitPane.remove(rightPanel);
      }
    }
  }

  /**
   * Relates a {@link CartAGenDB} to a {@link ProjectFrame} of the application.
   * Fills the layers of the project frame with the database objects.
   * @param db
   */
  public void addDatabaseToFrame(CartAGenDB db) throws JAXBException {
    // s'il y a une seule project frame et qu'elle est vide, on la supprime
    if (application.getMainFrame().getDesktopProjectFrames().length == 1) {
      ProjectFrame frameIni = application.getMainFrame()
          .getDesktopProjectFrames()[0];
      if (frameIni.getLayers().size() == 0) {
        application.getMainFrame().removeAllProjectFrames();
      }
    }
    ProjectFrame frame = application.getMainFrame().newProjectFrame();
    this.mapDbFrame.put(db.getName(), frame);
    frame.getSld().setDataSet(db.getDataSet());
    frame.getLayerViewPanel().getRenderingManager().setHandlingDeletion(true);
    StyledLayerDescriptor defaultSld = StyledLayerDescriptor
        .unmarshall(IGeneObj.class.getClassLoader().getResourceAsStream(
            "XML/CartagenStyles.xml")); //$NON-NLS-1$
    CartAGenDoc.getInstance().getCurrentDataset().setSld(defaultSld);
    float opacity = 0.8f;
    float strokeWidth = 1.0f;
    for (GeographicClass geoClass : db.getClasses()) {
      String populationName = db.getDataSet().getPopNameFromFeatType(
          geoClass.getFeatureTypeName());
      if (frame.getSld().getLayer(populationName) == null) {
        Color fillColor = new Color((float) Math.random(),
            (float) Math.random(), (float) Math.random());
        Layer layer = new NamedLayer(frame.getSld(), populationName);
        if (defaultSld.getLayer(populationName) != null) {
          layer.getStyles().addAll(
              defaultSld.getLayer(populationName).getStyles());
          // set features of differents layer
          for (IFeature f : db.getDataSet().getPopulation(populationName)) {
            ((IGeneObj) f).setSymbolId(-2);
          }
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

  /**
   * Relates a {@link CartAGenDB} to a {@link ProjectFrame} of the application
   * with a given sld. Fills the layers of the project frame with the database
   * objects.
   * @param db
   */
  public ProjectFrame addDatabaseToFrame(CartAGenDB db,
      StyledLayerDescriptor defaultSld) {
    // s'il y a une seule project frame et qu'elle est vide, on la supprime
    if (application.getMainFrame().getDesktopProjectFrames().length == 1) {
      ProjectFrame frameIni = application.getMainFrame()
          .getDesktopProjectFrames()[0];
      if (frameIni.getLayers().size() == 0) {
        application.getMainFrame().removeAllProjectFrames();
      }
    }
    db.getDataSet().setSld(defaultSld);
    ProjectFrame frame = application.getMainFrame().newProjectFrame();
    this.mapDbFrame.put(db.getName(), frame);
    frame.setSld(defaultSld);
    frame.getSld().setDataSet(db.getDataSet());
    frame.getLayerViewPanel().getRenderingManager().setHandlingDeletion(true);
    for (Layer layer : defaultSld.getLayers()) {
      ((NamedLayer) layer).setSld(frame.getSld());
      frame.getLayerViewPanel().layerAdded(layer);
    }

    // initialise the frame with cartagen plugin
    CartAGenProjectPlugin.getInstance().initialize(frame);

    return frame;
  }

  /**
   * Get the Project Frame used to display a {@link CartAGenDB}.
   * @param name
   * @return
   */
  public ProjectFrame getProjectFrameFromDbName(String name) {
    return this.mapDbFrame.get(name);
  }

  public CartAGenDoc getDocument() {
    return document;
  }

  public GeOxygeneApplication getApplication() {
    return application;
  }

  public void setApplication(GeOxygeneApplication application) {
    this.application = application;
  }

  public void setDocument(CartAGenDoc doc) {
    this.document = doc;
  }

  public GeneObjImplementation getGeneObjImpl() {
    return currentGeneObjImpl;
  }

  public void setGeneObjImpl(GeneObjImplementation currentGeneObjImpl) {
    this.currentGeneObjImpl = currentGeneObjImpl;
  }

  public HashMap<String, ProjectFrame> getMapDbFrame() {
    return mapDbFrame;
  }

  public void setMapDbFrame(HashMap<String, ProjectFrame> mapDbFrame) {
    this.mapDbFrame = mapDbFrame;
  }

  public GeneObjImplementation getCurrentGeneObjImpl() {
    return currentGeneObjImpl;
  }

  public void setCurrentGeneObjImpl(GeneObjImplementation currentGeneObjImpl) {
    this.currentGeneObjImpl = currentGeneObjImpl;
  }

  public String getCheminFichierConfigurationDonnees() {
    return cheminFichierConfigurationDonnees;
  }

  public String getCheminFichierConfigurationGeneralisation() {
    return cheminFichierConfigurationGeneralisation;
  }

  /**
   * Update the database view information of each database of the current
   * document with the current configuration of the project frames.
   */
  public void saveWindows() {
    for (String dbName : document.getDatabases().keySet()) {
      // get the project frame for the given database
      ProjectFrame frame = this.getProjectFrameFromDbName(dbName);
      Set<String> displayedLayers = new HashSet<String>();
      for (int i = 0; i < frame.getLayers().size(); i++) {
        Layer layer = frame.getLayers().get(i);
        if (layer.isVisible()) {
          JTable table = frame.getLayerLegendPanel().getLayersTable();
          TableModel model = table.getModel();
          if ((Integer) model.getValueAt(i, 1) > 0)
            displayedLayers.add(layer.getName());
        }
      }
      IEnvelope displayEnvelope = frame.getLayerViewPanel().getViewport()
          .getEnvelopeInModelCoordinates();
      IDirectPosition geoCenter = displayEnvelope.center();
      DatabaseView dbView = new DatabaseView(document, geoCenter,
          displayEnvelope, frame.getSld(), displayedLayers);
      document.getDatabaseViews().put(dbName, dbView);
    }
  }
}
