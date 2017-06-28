/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.dataset;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.cartagen.spatialanalysis.urban.UrbanEnrichment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerFactory;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.NamedLayer;
import fr.ign.cogit.geoxygene.style.UserStyle;

public class EnrichFrame extends JDialog implements ActionListener {

  // private static Logger LOGGER =
  // Logger.getLogger(EnrichFrame.class.getName());

  private static final long serialVersionUID = -6992190369890036500L;

  private static EnrichFrame enrichFrame = null;

  /**
   * Recuperation de l'instance unique (singleton)
   * @return instance unique (singleton) de EnrichFrame
   */
  public static EnrichFrame getInstance() {
    if (EnrichFrame.enrichFrame == null) {
      synchronized (EnrichFrame.class) {
        if (EnrichFrame.enrichFrame == null) {
          EnrichFrame.enrichFrame = new EnrichFrame();
        }
      }
    }
    return EnrichFrame.enrichFrame;
  }

  // Components

  private JCheckBox enrichRelief;
  private JCheckBox enrichLandUse;
  private JCheckBox enrichBuildings;
  private JCheckBox enrichBuildingsAlign;
  private JCheckBox enrichRoads;
  private JCheckBox enrichHydro;
  private JLabel espace1;
  private JCheckBox buildNetworkFaces;
  private JLabel espace2;
  private JCheckBox reset;
  private int version = 1;// version 1 pour l'ancien chargeur, version 2 pour le

  // nouveau chargeur

  public boolean isResetSelected() {
    return this.reset.isSelected();
  }

  private final JButton ok = new JButton("Valider");

  private AbstractCreationFactory factory;

  public void setFactory(AbstractCreationFactory factory) {
    this.factory = factory;
  }

  private CartAGenDataSet dataSet;

  public void setDataSet(CartAGenDataSet dataSet) {
    this.dataSet = dataSet;
  }

  /**
   * Construction of the frame
   */

  public EnrichFrame() {
    super((JFrame) null, true);
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    this.initFrame();
    this.pack();
    // this.setLocation(200,100);
    this.setLocationRelativeTo(null);
  }

  /**
   * Initialisation of the frame
   * @return
   */

  public void initFrame() {

    JPanel pnl = new JPanel();
    pnl.setLayout(new BorderLayout());

    // Creation of the checkboxes

    this.enrichRelief = new JCheckBox("Relief enrichment ", false);
    this.enrichRelief.setMnemonic(KeyEvent.VK_R);

    this.enrichLandUse = new JCheckBox("Land use enrichment", false);
    this.enrichLandUse.setMnemonic(KeyEvent.VK_L);

    this.enrichBuildings = new JCheckBox("Building enrichment: towns & blocks",
        false);
    this.enrichBuildings.setMnemonic(KeyEvent.VK_B);
    this.enrichBuildings.setSelected(true);

    this.enrichBuildingsAlign = new JCheckBox(
        "Building enrichment: urban alignments", false);
    this.enrichBuildingsAlign.setMnemonic(KeyEvent.VK_B);
    this.enrichBuildingsAlign.setSelected(true);

    this.enrichRoads = new JCheckBox("Road enrichment", false);
    this.enrichRoads.setMnemonic(KeyEvent.VK_T);
    this.enrichRoads.setSelected(true);

    this.enrichHydro = new JCheckBox("Hydrography enrichment", false);
    this.enrichHydro.setMnemonic(KeyEvent.VK_H);

    this.espace1 = new JLabel("");
    this.buildNetworkFaces = new JCheckBox("Network faces construction", false);
    this.buildNetworkFaces.setMnemonic(KeyEvent.VK_H);

    this.espace2 = new JLabel("");
    this.reset = new JCheckBox("Reset of the whole dataset", true);
    this.reset.setMnemonic(KeyEvent.VK_S);
    this.ok.addActionListener(this);

    JPanel checkPanel = new JPanel(new GridLayout(0, 1));
    checkPanel.setBorder(BorderFactory
        .createTitledBorder("Please select the needed enrichments"));

    checkPanel.add(this.enrichRelief);
    checkPanel.add(this.enrichLandUse);
    checkPanel.add(this.enrichBuildings);
    checkPanel.add(this.enrichBuildingsAlign);
    checkPanel.add(this.enrichRoads);
    checkPanel.add(this.enrichHydro);
    checkPanel.add(this.espace1);
    checkPanel.add(this.buildNetworkFaces);
    checkPanel.add(this.espace2);

    // Validation

    Box bBox = Box.createHorizontalBox();
    bBox.add(this.reset);
    bBox.add(this.ok);
    pnl.add(bBox, "South");
    pnl.add(checkPanel, BorderLayout.LINE_START);
    pnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    this.setContentPane(pnl);

    this.setLocationRelativeTo(null);
  }

  /**
   * Validation of the frame
   */

  @Override
  public void actionPerformed(ActionEvent e) {

    Object source = e.getSource();

    // update the CartAGenDB information on enrichments
    CartAGenDB db = this.dataSet.getCartAGenDB();

    if (source == this.ok) {

      // try {
      // StyledLayerDescriptor defaultSld = StyledLayerDescriptor
      // .unmarshall(EnrichFrame.class.getClassLoader().getResourceAsStream(
      // "sld/generalisation_enrichment_sld.xml"));
      //
      // db.getDataSet().setSld(defaultSld);
      //
      // } catch (JAXBException e1) {
      // // TODO Auto-generated catch block
      // e1.printStackTrace();
      // }

      if (this.enrichRelief.isSelected()) {
        // TODO
      }

      if (this.enrichLandUse.isSelected()) {
        // TODO
      }

      if (this.enrichRoads.isSelected()) {
        NetworkEnrichment.enrichNetwork(this.dataSet,
            this.dataSet.getRoadNetwork(), this.factory);
        this.addEnrichmentToFrame(CartAGenDataSet.ROAD_NODES_POP, IPoint.class);

      }

      if (this.enrichHydro.isSelected()) {
        NetworkEnrichment.enrichNetwork(this.dataSet,
            this.dataSet.getHydroNetwork(), this.factory);
        this.addEnrichmentToFrame(CartAGenDataSet.WATER_NODES_POP,
            IPoint.class);
      }

      boolean enrichissementBatiAlign = false;
      if (this.enrichBuildingsAlign.isSelected()) {
        enrichissementBatiAlign = true;
      }

      if (this.enrichBuildings.isSelected()) {
        UrbanEnrichment.buildTowns(dataSet, enrichissementBatiAlign, factory);
        this.addEnrichmentToFrame(CartAGenDataSet.TOWNS_POP, IPolygon.class);
        this.addEnrichmentToFrame(CartAGenDataSet.BLOCKS_POP, IPolygon.class);
        if (enrichissementBatiAlign) {
          // this.addEnrichmentToFrameSLD(CartAGenDataSet.URBAN_ALIGNMENTS_POP);
          this.addEnrichmentToFrame(CartAGenDataSet.URBAN_ALIGNMENTS_POP,
              ILineString.class);
        }
      }

      if (this.buildNetworkFaces.isSelected()) {
        NetworkEnrichment.buildNetworkFaces(this.dataSet, this.factory);
        this.addEnrichmentToFrame(CartAGenDataSet.NETWORK_FACES_POP,
            IPolygon.class);
      }
      this.setVisible(false);
    }
  }

  private void addEnrichmentToFrame(String populationName,
      Class<? extends IGeometry> geometryType) {

    GeOxygeneApplication application = CartAGenPlugin.getInstance()
        .getApplication();

    ProjectFrame frame = application.getMainFrame().getSelectedProjectFrame();

    float opacity = 0.5f;
    float strokeWidth = 0.0f;
    Color fillColor = new Color((float) Math.random(), (float) Math.random(),
        (float) Math.random());
    Layer layer = new NamedLayer(frame.getSld(), populationName);

    if (layer.getFeatureCollection() == null) {
      return;
    }
    if (layer.getFeatureCollection().size() == 0) {
      return;
    }

    UserStyle style = new UserStyle();
    style.setName("Style créé pour le layer " + populationName);//$NON-NLS-1$
    FeatureTypeStyle fts = new FeatureTypeStyle();
    fts.getRules().add(LayerFactory.createRule(geometryType, fillColor.darker(),
        fillColor, opacity, opacity, strokeWidth));
    style.getFeatureTypeStyles().add(fts);
    layer.getStyles().add(style);
    frame.getSld().add(layer);
  }

  @SuppressWarnings("unused")
  private void addEnrichmentToFrameSLD(String populationName) {

    GeOxygeneApplication application = CartAGenPlugin.getInstance()
        .getApplication();

    ProjectFrame frame = application.getMainFrame().getSelectedProjectFrame();

    Layer layer = new NamedLayer(frame.getSld(), populationName);

    frame.getSld().add(layer);
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public int getVersion() {
    return this.version;
  }

}
