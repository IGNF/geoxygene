/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.energy.IElectricityLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.partition.IMask;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.ISpotHeight;
import fr.ign.cogit.cartagen.core.genericschema.road.IPathLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.GeneObjImplementation;
import fr.ign.cogit.cartagen.software.dataset.ShapeFileDB;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.AbstractLayerGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.GeneralisationBottomPanelComplement;
import fr.ign.cogit.cartagen.software.interfacecartagen.GeneralisationLeftPanelComplement;
import fr.ign.cogit.cartagen.software.interfacecartagen.GeneralisationMenuComplement;
import fr.ign.cogit.cartagen.software.interfacecartagen.GeneralisationVisuPanelComplement;
import fr.ign.cogit.cartagen.software.interfacecartagen.LayerGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.annexes.DataFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.annexes.ExportFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.annexes.GeneralisationConfigurationFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.annexes.GeneralisationLaunchingFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.dataloading.LoadingFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.dataloading.ProgressFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.dataloading.RexportFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.GeoxygeneFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.InitFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.software.interfacecartagen.mode.ModeSelector;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolList;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolsUtil;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.cartagen.spatialanalysis.urban.UrbanEnrichment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * class de l'application cartagen (contient LA methode main)
 * @author JGaffuri
 */
public class CartagenApplication {
  private static Logger logger = Logger.getLogger(CartagenApplication.class
      .getName());

  /**
   * Singleton
   */
  private static CartagenApplication cartagenApplication = null;

  /**
   * Recuperation de l'instance unique (singleton)
   * @return instance unique (singleton) de CartagenApplication.
   */
  public static CartagenApplication getInstance() {
    if (CartagenApplication.cartagenApplication == null) {
      synchronized (CartagenApplication.class) {
        if (CartagenApplication.cartagenApplication == null) {
          CartagenApplication.cartagenApplication = new CartagenApplication();
        }
      }
    }
    return CartagenApplication.cartagenApplication;
  }

  private double version = 0.13;

  public double getVersion() {
    return this.version;
  }

  private GeoxygeneFrame frame = null;

  public GeoxygeneFrame getFrame() {
    if (this.frame == null) {
      synchronized (CartagenApplication.class) {
        if (this.frame == null) {
          this.frame = new GeoxygeneFrame();
        }
      }
    }
    return this.frame;
  }

  private InitFrame frameInit = null;

  public InitFrame getFrameInit() {
    if (this.frameInit == null) {
      synchronized (CartagenApplication.class) {
        if (this.frameInit == null) {
          this.frameInit = new InitFrame();
        }
      }
    }
    return this.frameInit;
  }

  private CartAGenDoc document = null;

  public CartAGenDoc getDocument() {
    if (this.document == null) {
      synchronized (CartagenApplication.class) {
        if (this.document == null) {
          this.document = CartAGenDoc.getInstance();
        }
      }
    }
    return this.document;
  }

  // les autres fenetres
  private GeneralisationConfigurationFrame frameConfGene;

  public GeneralisationConfigurationFrame getFrameConfigurationGeneralisation() {
    if (this.frameConfGene == null) {
      this.frameConfGene = new GeneralisationConfigurationFrame();
    }
    return this.frameConfGene;
  }

  private DataFrame frameDonnees;

  public DataFrame getFrameDonnees() {
    if (this.frameDonnees == null) {
      this.frameDonnees = new DataFrame();
    }
    return this.frameDonnees;
  }

  private ExportFrame frameExport = null;

  public ExportFrame getFrameExport() {
    if (this.frameExport == null) {
      this.frameExport = new ExportFrame();
    }
    return this.frameExport;
  }

  private LoadingFrame frameLoader = null;

  public LoadingFrame getFrameChargeur() {
    if (this.frameLoader == null) {
      synchronized (CartagenApplication.class) {
        if (this.frameLoader == null) {
          this.frameLoader = new LoadingFrame();
        }
      }
    }
    return this.frameLoader;
  }

  public void resetFrameChargeur() {
    this.frameLoader = new LoadingFrame();
  }

  private RexportFrame frameExportData = null;

  public RexportFrame getFrameExportData() {
    if (this.frameExportData == null) {
      synchronized (CartagenApplication.class) {
        if (this.frameExportData == null) {
          this.frameExportData = new RexportFrame();
        }
      }
    }
    return this.frameExportData;
  }

  public void resetFrameExport() {
    this.frameExportData = new RexportFrame();
  }

  private GeneralisationLaunchingFrame frameLancerGeneralisation = null;

  public GeneralisationLaunchingFrame getFrameLancerGeneralisation() {
    if (this.frameLancerGeneralisation == null) {
      this.frameLancerGeneralisation = new GeneralisationLaunchingFrame();
    }
    return this.frameLancerGeneralisation;
  }

  /**
   * to examine if there is another instance of cartagenapplication if yes we
   * give a warning, and if the user does not close the opened application and
   * click OK, we close the application
   * @return
   */
  public static boolean hasAnotherInstance() {
    FileLock fileLock = null;
    RandomAccessFile file = null;
    try {

      file = new RandomAccessFile("README.txt", "rw");

      // trying to lock the readme.txt
      fileLock = file.getChannel().tryLock();
      if (fileLock != null) {
      } else {
        // warn the user that there is another instance of the application
        JOptionPane.showMessageDialog(null,
            "Close the already opened application please");
        fileLock = file.getChannel().tryLock();
        if (fileLock == null) {

          // if the user ignore our warning, we close the application
          System.out.println("Please close the already opened Application");
          return true;
        }

      }
      return false;

    } catch (IOException e) {
      // TODO Auto-generated catch block

      e.printStackTrace();
      return false;
    }
  }

  /**
   * The layers groups of the application
   */

  private AbstractLayerGroup finalLayerGroup = new LayerGroup();
  private AbstractLayerGroup initialLayerGroup = new LayerGroup();
  private AbstractLayerGroup currentLayerGroup = this.finalLayerGroup;

  public AbstractLayerGroup getLayerGroup() {
    return this.currentLayerGroup;
  }

  public AbstractLayerGroup getInitialLayerGroup() {
    return this.initialLayerGroup;
  }

  public void setLayerGroup(AbstractLayerGroup group) {
    this.currentLayerGroup = group;
  }

  public void setFinalLayerGroup(AbstractLayerGroup group) {
    this.finalLayerGroup = group;
  }

  public void setInitialLayerGroup(AbstractLayerGroup group) {
    this.initialLayerGroup = group;
  }

  /**
   * The creation factory (for objects creation depending on the geographic
   * schema)
   */
  private AbstractCreationFactory creationFactory;

  public AbstractCreationFactory getCreationFactory() {
    return this.creationFactory;
  }

  public void setCreationFactory(AbstractCreationFactory creationFactory) {
    this.creationFactory = creationFactory;
  }

  /**
   * Application initialisation
   */

  public void initApplication() {
    System.out.println("Cartagen - version " + this.getVersion()
        + " - COGIT Laboratory, Institut Géographique National");

    // PropertyConfigurator.configure("log4j.properties");
    // CartagenApplication.logger = Logger.getLogger(CartagenApplication.class
    // .getName());
  }

  /**
   * Interface initialisation
   */

  public void initInterface() {

    // Application configuration
    CartagenApplication.cartagenApplication.applicationConfiguration();

    if (CartagenApplication.logger.isInfoEnabled()) {
      CartagenApplication.logger.info("Interface construction");
    }

    // Main window position and display
    CartagenApplication.cartagenApplication.getFrame().setVisible(true);
    CartagenApplication.cartagenApplication.getFrame().setTitle(
        "CartAGen - GéOxygène");

    // Initialisation window
    CartagenApplication.cartagenApplication.getFrameInit().setVisible(true);
    CartagenApplication.cartagenApplication.getFrameInit().lblInterface
        .setForeground(Color.BLACK);
    CartagenApplication.cartagenApplication
        .getFrameInit()
        .isGood(
            CartagenApplication.cartagenApplication.getFrameInit().lblInterfaceIcon);
    CartagenApplication.cartagenApplication.getFrameInit().repaint();

  }

  /**
   * Data loading and enrichment
   */

  /*
   * public void loadAndEnrichData(SourceDLM sourceDlm, int scale) { if
   * (CartagenApplication.logger.isDebugEnabled()) { CartagenApplication.logger
   * .debug("Load data configuration file from " +
   * CartagenApplication.cartagenApplication.cheminFichierConfigurationDonnees);
   * }
   * 
   * 
   * // Data loading CartagenApplication.logger.info("Data loading");
   * CartagenApplication
   * .cartagenApplication.loadData_Special_Cartagen(sourceDlm, scale);
   * 
   * // Position of the visu panel centered on data if
   * (CartagenApplication.cartagenApplication.getFrame().getVisuPanel()
   * .getGeoCenter().getX() == 0.0 &&
   * CartagenApplication.cartagenApplication.getFrame().getVisuPanel()
   * .getGeoCenter().getX() == 0.0) {
   * this.initialiserPositionGeographique(false); }
   * 
   * // Data enrichment CartagenApplication.logger.info("Data enrichment");
   * CartagenApplication.cartagenApplication.enrichData();
   * 
   * }
   */

  // / à supprimer, mais je l'ai garder parce que jérémy l'utilise dans
  // ValidityCriterionTest
  public void loadAndEnrichData(SourceDLM bdsource, int scale) {
    if (CartagenApplication.logger.isDebugEnabled()) {
      CartagenApplication.logger
          .debug("Load data configuration file from "
              + CartagenApplication.cartagenApplication.cheminFichierConfigurationDonnees);
    }
    CartagenApplication.cartagenApplication
        .lectureFichierConfigurationDonnees();

    // Data loading
    CartagenApplication.logger.info("Data loading");
    CartagenApplication.cartagenApplication.loadData(this.cheminDonnees,
        bdsource, scale, CartAGenDoc.getInstance().getCurrentDataset());

    // Position of the visu panel centered on data
    if (CartagenApplication.cartagenApplication.getFrame().getVisuPanel()
        .getGeoCenter().getX() == 0.0
        && CartagenApplication.cartagenApplication.getFrame().getVisuPanel()
            .getGeoCenter().getX() == 0.0) {
      this.initialiserPositionGeographique(false);
    }

    // Data enrichment
    CartagenApplication.logger.info("Data enrichment");
    CartagenApplication.cartagenApplication.enrichData(CartAGenDoc
        .getInstance().getCurrentDataset());

  }

  /**
   * Generalisation configuration
   */

  public void initGeneralisation() {
    if (CartagenApplication.logger.isDebugEnabled()) {
      CartagenApplication.logger
          .debug("Load generalisation configuration file from "
              + CartagenApplication.cartagenApplication.cheminFichierConfigurationGeneralisation);
    }
    CartagenApplication.cartagenApplication
        .lectureFichierConfigurationGeneralisation();

    if (CartagenApplication.logger.isInfoEnabled()) {
      CartagenApplication.logger.info("Interface layers loading");
    }
    CartagenApplication.cartagenApplication.getFrameInit().lblLayers
        .setForeground(Color.BLACK);
    CartagenApplication.cartagenApplication.getFrameInit().repaint();
    /*
     * Kusay moved it to be during the data loading
     * CartagenApplication.cartagenApplication .getLayerGroup() .loadLayers(
     * this.getDocument().getCurrentDataset(),
     * CartagenApplication.getInstance().getLayerGroup().symbolisationDisplay);
     * CartagenApplication.cartagenApplication.getLayerGroup()
     * .loadInterfaceWithLayers(
     * CartagenApplication.getInstance().getFrame().getLayerManager
     * (),CartagenApplication
     * .getInstance().getDocument().getCurrentDataset().getSymbols());
     */
    CartagenApplication.cartagenApplication.getFrameInit().isGood(
        CartagenApplication.cartagenApplication.getFrameInit().lblLayersIcon);
    CartagenApplication.cartagenApplication.getFrameInit().repaint();

    if (CartagenApplication.logger.isInfoEnabled()) {
      CartagenApplication.logger
          .info("Instanciation of generalisation constraints");
    }
    CartagenApplication.cartagenApplication.getFrameInit().lblGeneConstraints
        .setForeground(Color.BLACK);
    CartagenApplication.cartagenApplication.getFrameInit().repaint();
  }

  private String cheminFichierConfigurationDonnees = "/configurationDonnees.xml";

  public String getCheminFichierConfigurationChargementDonnees() {
    return this.cheminFichierConfigurationDonnees;
  }

  private String cheminFichierConfigurationGeneralisation = "/configurationGeneralisation.xml";

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

  private int DEMResolution = -1;

  public void setDEMResolution(int dEMResolution) {
    this.DEMResolution = dEMResolution;
  }

  public int getDEMResolution() {
    return this.DEMResolution;
  }

  private boolean enrichissementBati = false;

  public boolean isEnrichissementBati() {
    return this.enrichissementBati;
  }

  public void setEnrichissementBati(boolean enrichissementBati) {
    CartagenApplication.cartagenApplication.enrichissementBati = enrichissementBati;
  }

  private boolean enrichissementBatiAlign = false;

  public boolean isEnrichissementBatiAlign() {
    return this.enrichissementBatiAlign;
  }

  public void setEnrichissementBatiAlign(boolean enrichissementBatiAlign) {
    CartagenApplication.cartagenApplication.enrichissementBatiAlign = enrichissementBatiAlign;
  }

  private boolean enrichissementRoutier = false;

  public boolean isEnrichissementRoutier() {
    return this.enrichissementRoutier;
  }

  public void setEnrichissementRoutier(boolean enrichissementRoutier) {
    CartagenApplication.cartagenApplication.enrichissementRoutier = enrichissementRoutier;
  }

  private boolean enrichissementHydro = false;

  public boolean isEnrichissementHydro() {
    return this.enrichissementHydro;
  }

  public void setEnrichissementHydro(boolean enrichissementHydro) {
    CartagenApplication.cartagenApplication.enrichissementHydro = enrichissementHydro;
  }

  private boolean enrichissementRelief = false;

  public boolean isEnrichissementRelief() {
    return this.enrichissementRelief;
  }

  public void setEnrichissementRelief(boolean enrichissementRelief) {
    CartagenApplication.cartagenApplication.enrichissementRelief = enrichissementRelief;
  }

  private boolean enrichissementOccSol = false;

  public boolean isEnrichissementOccSol() {
    return this.enrichissementOccSol;
  }

  public void setEnrichissementOccSol(boolean enrichissementOccSol) {
    CartagenApplication.cartagenApplication.enrichissementOccSol = enrichissementOccSol;
  }

  private boolean constructNetworkFaces = false;

  public boolean isConstructNetworkFaces() {
    return this.constructNetworkFaces;
  }

  public void setConstructNetworkFaces(boolean constructNetworkFaces) {
    CartagenApplication.cartagenApplication.constructNetworkFaces = constructNetworkFaces;
  }

  /**
   * Data configuration
   */

  public void lectureFichierConfigurationDonnees() {

    // le document XML
    Document docXML = null;
    try {
      docXML = DocumentBuilderFactory
          .newInstance()
          .newDocumentBuilder()
          .parse(
              CartagenApplication.class
                  .getResourceAsStream(this.cheminFichierConfigurationDonnees));
    } catch (FileNotFoundException e) {
      CartagenApplication.logger.error("Fichier non trouvé: "
          + this.cheminFichierConfigurationDonnees);
      e.printStackTrace();
      return;
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (docXML == null) {
      CartagenApplication.logger.error("Erreur lors de la lecture de: "
          + this.cheminFichierConfigurationDonnees);
      return;
    }

    Element configurationDonneesMirageXML = (Element) docXML
        .getElementsByTagName("configurationDonnees").item(0);
    if (configurationDonneesMirageXML == null) {
      return;
    }

    Element elXML = null;

    // chemin du repertoire SHP
    elXML = (Element) configurationDonneesMirageXML.getElementsByTagName(
        "cheminRepertoireDonneesSHP").item(0);
    if (elXML != null) {
      this.cheminDonnees = elXML.getFirstChild().getNodeValue();
    }

    // resolution MNT
    elXML = (Element) configurationDonneesMirageXML.getElementsByTagName(
        "resolutionMNT").item(0);
    if (elXML != null) {
      this.setDEMResolution(Integer.parseInt(elXML.getFirstChild()
          .getNodeValue()));
    }

    // bati pour villes et ilots
    elXML = (Element) configurationDonneesMirageXML.getElementsByTagName(
        "enrichissementBati").item(0);
    if (elXML != null) {
      this.enrichissementBati = Boolean.parseBoolean(elXML.getFirstChild()
          .getNodeValue());
    }

    // bati pour alignements urbains
    elXML = (Element) configurationDonneesMirageXML.getElementsByTagName(
        "enrichissementBatiAlign").item(0);
    if (elXML != null) {
      this.enrichissementBatiAlign = Boolean.parseBoolean(elXML.getFirstChild()
          .getNodeValue());
    }

    // routier
    elXML = (Element) configurationDonneesMirageXML.getElementsByTagName(
        "enrichissementRoutier").item(0);
    if (elXML != null) {
      this.enrichissementRoutier = Boolean.parseBoolean(elXML.getFirstChild()
          .getNodeValue());
    }

    // hydro
    elXML = (Element) configurationDonneesMirageXML.getElementsByTagName(
        "enrichissementHydro").item(0);
    if (elXML != null) {
      this.enrichissementHydro = Boolean.parseBoolean(elXML.getFirstChild()
          .getNodeValue());
    }

    // relief
    elXML = (Element) configurationDonneesMirageXML.getElementsByTagName(
        "enrichissementRelief").item(0);
    if (elXML != null) {
      this.enrichissementRelief = Boolean.parseBoolean(elXML.getFirstChild()
          .getNodeValue());
    }

    // occ sol
    elXML = (Element) configurationDonneesMirageXML.getElementsByTagName(
        "enrichissementOccSol").item(0);
    if (elXML != null) {
      this.enrichissementOccSol = Boolean.parseBoolean(elXML.getFirstChild()
          .getNodeValue());
    }

    // construction des faces reseau (pour CartACom)
    elXML = (Element) configurationDonneesMirageXML.getElementsByTagName(
        "construireFacesReseau").item(0);
    if (elXML != null) {
      this.constructNetworkFaces = Boolean.parseBoolean(elXML.getFirstChild()
          .getNodeValue());
    }

  }

  /**
   * Data loading
   */

  public void loadData_Special_Cartagen(String absolutePath,
      SourceDLM sourceDlm, int scale) {

    CartagenApplication.cartagenApplication.getFrameInit().lblLoading
        .setForeground(Color.BLACK);
    CartagenApplication.cartagenApplication.getFrameInit().repaint();

    ProgressFrame progressFrame = new ProgressFrame(
        "Data loading in progress...", true);
    progressFrame.setTextAndValue("Loading buildings", 0);
    progressFrame.setVisible(true);

    CartAGenDataSet dataSet = this.getDocument().getCurrentDataset();

    try {
      SymbolGroup symbGroup = SymbolsUtil.getSymbolGroup(sourceDlm, scale);
      dataSet.setSymbols(SymbolList.getSymbolList(symbGroup));
      if (CartagenApplication.logger.isInfoEnabled()) {
        CartagenApplication.logger.info("buildings spatial index creation");
      }

      if (dataSet.loadBuildingsFromSHP(absolutePath + "/batiment")) {
        // create a new ShapeFileClass object in the CartAGen dataset
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/batiment", IBuilding.FEAT_TYPE_NAME);
      }
      dataSet.getBuildings().initSpatialIndex(Tiling.class, false);

      progressFrame.setTextAndValue("Loading road network", 30);
      if (dataSet.loadRoadLinesFromSHP(absolutePath + "/troncon_route",
          sourceDlm, dataSet.getSymbols())) {
        // create a new ShapeFileClass object in the CartAGen dataset
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/troncon_route", IRoadLine.FEAT_TYPE_NAME);
      }
      progressFrame.setTextAndValue("Loading hydrologic network", 60);
      if (dataSet.loadWaterLinesFromSHP(absolutePath + "/troncon_cours_eau",
          dataSet.getSymbols())) {
        // create a new ShapeFileClass object in the CartAGen dataset
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/troncon_cours_eau", IWaterLine.FEAT_TYPE_NAME);
      }
      if (dataSet.loadWaterAreasFromSHP(absolutePath + "/surface_eau",
          dataSet.getSymbols())) {
        // create a new ShapeFileClass object in the CartAGen dataset
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/surface_eau", IWaterArea.FEAT_TYPE_NAME);
      }

      progressFrame.setTextAndValue("Loading railo roads and electric network",
          70);
      if (dataSet.loadRailwayLineFromSHP(absolutePath + "/troncon_voie_ferree",
          dataSet.getSymbols())) {
        // create a new ShapeFileClass object in the CartAGen dataset
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/troncon_voie_ferree", IRailwayLine.FEAT_TYPE_NAME);
      }
      if (dataSet.loadElectricityLinesFromSHP(absolutePath
          + "/troncon_electrique", dataSet.getSymbols())) {
        // create a new ShapeFileClass object in the CartAGen dataset
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/troncon_electrique", IElectricityLine.FEAT_TYPE_NAME);
      }
      if (dataSet.loadContourLinesFromSHP(absolutePath + "/cn",
          dataSet.getSymbols())) {
        // create a new ShapeFileClass object in the CartAGen dataset
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/cn", IContourLine.FEAT_TYPE_NAME);
      }

      progressFrame.setTextAndValue("Loading contour lines and DTM", 90);
      if (dataSet.loadReliefElementLinesFromSHP(absolutePath
          + "/ligne_orographique", dataSet.getSymbols())) {
        // create a new ShapeFileClass object in the CartAGen dataset
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/ligne_orographique", IReliefElementLine.FEAT_TYPE_NAME);
      }
      dataSet.loadDEMPixelsFromSHP(absolutePath + "/mnt");

      if (dataSet.loadSpotHeightsFromSHP(absolutePath + "/point_cote",
          dataSet.getSymbols())) {
        // create a new ShapeFileClass object in the CartAGen dataset
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/point_cote", ISpotHeight.FEAT_TYPE_NAME);
      }

      // cartagenApplication.getDataSet().chargerAdminSHP(cheminDonnees+"/administratif");
      // cartagenApplication.getDataSet().chargerZoneOccSolSHP(cheminDonnees+"/occ_sol",
      // 3.0);

      if (dataSet.loadMaskFromSHP(absolutePath + "/masque")) {
        // create a new ShapeFileClass object in the CartAGen dataset
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/masque", IMask.FEAT_TYPE_NAME);
      }

      if (CartagenApplication.logger.isInfoEnabled()) {
        CartagenApplication.logger.info("end data loading");
      }
      CartagenApplication.cartagenApplication
          .getFrameInit()
          .isGood(
              CartagenApplication.cartagenApplication.getFrameInit().lblLoadingIcon);
      CartagenApplication.cartagenApplication.getFrameInit().repaint();

    } catch (IOException e) {
      e.printStackTrace();
      CartagenApplication.cartagenApplication
          .getFrameInit()
          .isWrong(
              CartagenApplication.cartagenApplication.getFrameInit().lblLoadingIcon);
      CartagenApplication.cartagenApplication.getFrameInit().repaint();
    } finally {
      progressFrame.setVisible(false);

      progressFrame = null;
    }

    // Refresh
    CartagenApplication.cartagenApplication.getFrame().getVisuPanel()
        .activate();
    CartagenApplication.cartagenApplication.getFrame().getVisuPanel()
        .activateAutomaticRefresh();

  }

  public void loadData_BDCarto(String absolutePath, int scale,
      CartAGenDataSet dataSet) {

    ProgressFrame progressFrame = new ProgressFrame(
        "Data loading in progress...", true);
    progressFrame.setVisible(true);

    SymbolGroup symbGroup = SymbolsUtil.getSymbolGroup(SourceDLM.BD_CARTO,
        scale);
    dataSet.setSymbols(SymbolList.getSymbolList(symbGroup));
    progressFrame.setTextAndValue("Loading road network", 0);

    try {
      dataSet.loadRoadLinesFromSHP(absolutePath + "/troncon_route",
          SourceDLM.BD_CARTO, dataSet.getSymbols());

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    progressFrame.dispose();

  }

  public void loadData_BDTopoV1(String absolutePath, int scale,
      CartAGenDataSet dataSet) {
    SymbolGroup symbGroup = SymbolsUtil.getSymbolGroup(SourceDLM.BD_TOPO_V1,
        scale);
    dataSet.setSymbols(SymbolList.getSymbolList(symbGroup));

    try {
      this.getDocument()
          .getCurrentDataset()
          .loadRoadLinesBDTopoV1_25FromSHP(
              // absolutePath + "/A_RESEAU_ROUTIER/CHEMIN", 2.0);
              absolutePath + "/troncon_route", 2.0, SourceDLM.BD_TOPO_V1,
              this.getDocument().getCurrentDataset().getSymbols());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    CartagenApplication.logger.info("tronçons chargés");
  }

  public void loadData_BDTopoV2(String absolutePath, int scale,
      CartAGenDataSet dataSet) {

    ProgressFrame progressFrame = new ProgressFrame(
        "Data loading in progress...", true);
    progressFrame.setVisible(true);

    SymbolGroup symbGroup = SymbolsUtil.getSymbolGroup(SourceDLM.BD_TOPO_V2,
        scale);
    dataSet.setSymbols(SymbolList.getSymbolList(symbGroup));
    progressFrame.setTextAndValue("Loading road network", 0);
    ((ShapeFileDB) dataSet.getCartAGenDB()).setSystemPath(absolutePath);
    try {
      if (dataSet.loadRoadLinesBDTopoV2_25FromSHP(absolutePath
          + "/A_RESEAU_ROUTIER/ROUTE", SourceDLM.BD_TOPO_V2,
          dataSet.getSymbols())) {
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/A_RESEAU_ROUTIER/ROUTE", IRoadLine.FEAT_TYPE_NAME);
      }

      if (dataSet.loadPathsBDTopoV2_25FromSHP(absolutePath
          + "/A_RESEAU_ROUTIER/CHEMIN", SourceDLM.BD_TOPO_V2,
          dataSet.getSymbols())) {
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/A_RESEAU_ROUTIER/CHEMIN", IPathLine.FEAT_TYPE_NAME);
      }

      progressFrame.setTextAndValue("Loading buildings", 20);

      if (dataSet.loadBuildingsFromSHP(absolutePath
          + "/E_BATI/BATI_INDIFFERENCIE")) {
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/E_BATI/BATI_INDIFFERENCIE", IBuilding.FEAT_TYPE_NAME);
      }

      if (dataSet
          .loadBuildingsFromSHP(absolutePath + "/E_BATI/BATI_INDUSTRIEL")) {
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/E_BATI/BATI_INDUSTRIEL", IBuilding.FEAT_TYPE_NAME);
      }

      if (dataSet.loadBuildingsFromSHP(absolutePath
          + "/E_BATI/BATI_REMARQUABLE")) {
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/E_BATI/BATI_REMARQUABLE", IBuilding.FEAT_TYPE_NAME);
      }

      dataSet.loadBuildingsFromSHP(absolutePath
          + "/E_BATI/CONSTRUCTION_SURFACIQUE");
      if (CartagenApplication.logger.isInfoEnabled()) {
        CartagenApplication.logger.info("buildings spatial index creation");
      }

      dataSet.getBuildings().initSpatialIndex(Tiling.class, false);

      progressFrame.setTextAndValue("Loading hydrographic network ", 60);

      if (dataSet.loadWaterLinesFromSHP(absolutePath
          + "/D_HYDROGRAPHIE/TRONCON_COURS_EAU", dataSet.getSymbols())) {
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/D_HYDROGRAPHIE/TRONCON_COURS_EAU", IWaterLine.FEAT_TYPE_NAME);
      }

      if (dataSet.loadWaterAreasFromSHP(absolutePath
          + "/D_HYDROGRAPHIE/SURFACE_EAU", dataSet.getSymbols())) {
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/D_HYDROGRAPHIE/SURFACE_EAU", IWaterArea.FEAT_TYPE_NAME);
      }

      progressFrame.setTextAndValue("Loading rail roads and other networks ",
          70);

      if (dataSet.loadRailwayLineFromSHP(absolutePath
          + "/B_VOIES_FERREES_ET_AUTRES/TRONCON_VOIE_FERREE",
          dataSet.getSymbols())) {
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/B_VOIES_FERREES_ET_AUTRES/TRONCON_VOIE_FERREE",
            IRailwayLine.FEAT_TYPE_NAME);
      }

      if (dataSet.loadElectricityLinesFromSHP(absolutePath
          + "/C_TRANSPORT_ENERGIE/LIGNE_ELECTRIQUE", dataSet.getSymbols())) {
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/C_TRANSPORT_ENERGIE/LIGNE_ELECTRIQUE",
            IElectricityLine.FEAT_TYPE_NAME);
      }

      progressFrame.setTextAndValue("Loading landuse", 80);

      if (dataSet.loadLandUseAreasFromSHP(absolutePath
          + "/F_VEGETATION/ZONE_VEGETATION", 1.0, 1)) {
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/F_VEGETATION/ZONE_VEGETATION",
            ISimpleLandUseArea.FEAT_TYPE_NAME);
      }

      if (dataSet.loadLandUseAreasFromSHP(absolutePath
          + "/I_ZONE_ACTIVITE/ZONE_ACTIVITE", 1.0, 2)) {
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/I_ZONE_ACTIVITE/ZONE_ACTIVITE",
            ISimpleLandUseArea.FEAT_TYPE_NAME);
      }

      progressFrame.setTextAndValue("Loading Contour lines", 90);

      if (dataSet.loadReliefElementLinesFromSHP(absolutePath
          + "/G_OROGRAPHIE/LIGNE_OROGRAPHIQUE", dataSet.getSymbols())) {
        ((ShapeFileDB) dataSet.getCartAGenDB()).addShapeFile(absolutePath
            + "/G_OROGRAPHIE/LIGNE_OROGRAPHIQUE",
            IReliefElementLine.FEAT_TYPE_NAME);
      }

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      progressFrame.setVisible(false);

      progressFrame = null;
    }

  }

  private void loadData_BDCete1(String absolutePath, int scale,
      CartAGenDataSet dataset) {

    ProgressFrame progressFrame = new ProgressFrame(
        "Data loading in progress...", true);
    progressFrame.setVisible(true);

    SymbolGroup symbGroup = SymbolsUtil.getSymbolGroup(SourceDLM.BD_Cete1,
        scale);
    dataset.setSymbols(SymbolList.getSymbolList(symbGroup));

    progressFrame.setTextAndValue("Loading road network", 0);

    try {
      dataset.loadRoadLinesFromSHPBasic(absolutePath + "/RESEAU_CG83_polyline",
          0);// ,SourceDLM.BD_Cete1,dataset.getSymbols());

      dataset.loadRoadLinesFromSHPBasic(absolutePath
          + "/RESEAU_Etat_Var_polyline", 0);// ,SourceDLM.BD_Cete1,dataset.getSymbols());

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    progressFrame.dispose();

  }

  public void loadData(String absolutePath, SourceDLM bdsource, int scale,
      CartAGenDataSet dataset) {

    CartagenApplication.cartagenApplication.getFrameInit().lblLoading
        .setForeground(Color.BLACK);
    CartagenApplication.cartagenApplication.getFrameInit().repaint();

    if (bdsource == SourceDLM.SPECIAL_CARTAGEN) {
      this.loadData_Special_Cartagen(absolutePath, bdsource, scale);
    } else if (bdsource == SourceDLM.BD_TOPO_V2) {
      this.loadData_BDTopoV2(absolutePath, scale, dataset);
    } else if (bdsource == SourceDLM.BD_TOPO_V1) {
      this.loadData_BDTopoV1(absolutePath, scale, dataset);
    } else if (bdsource == SourceDLM.BD_CARTO) {
      this.loadData_BDCarto(absolutePath, scale, dataset);

    } else if (bdsource == SourceDLM.BD_Cete1) {
      this.loadData_BDCete1(absolutePath, scale, dataset);

    } else if (bdsource == SourceDLM.BD_OSInitial) {

      this.loadData_BDOSInitial(absolutePath, scale, dataset);

    } else if (bdsource == SourceDLM.BD_OSFinal) {

      this.loadData_BDOSFinal(absolutePath, scale, dataset);

    }

    if (CartagenApplication.logger.isInfoEnabled()) {
      CartagenApplication.logger.info("end data loading");
    }

    CartagenApplication.cartagenApplication.getFrameInit().isGood(
        CartagenApplication.cartagenApplication.getFrameInit().lblLoadingIcon);
    CartagenApplication.cartagenApplication.getFrameInit().repaint();

    // Refresh
    CartagenApplication.cartagenApplication.getFrame().getVisuPanel()
        .activate();
    CartagenApplication.cartagenApplication.getFrame().getVisuPanel()
        .activateAutomaticRefresh();

  }

  private void loadData_BDOSInitial(String absolutePath, int scale,
      CartAGenDataSet dataset) {

    SymbolGroup symbGroup = SymbolsUtil.getSymbolGroup(SourceDLM.BD_OSInitial,
        scale);
    dataset.setSymbols(SymbolList.getSymbolList(symbGroup));

    ProgressFrame progressFrame = new ProgressFrame(
        "Data loading in progress...", true);
    progressFrame.setVisible(true);

    progressFrame.setTextAndValue("Loading road network", 0);

    dataset
        .loadOSRoads(absolutePath + "/MAIA_NETWORK_LINK_FEAT_line", 0, "KEY");// ,SourceDLM.BD_Cete1,dataset.getSymbols());

    progressFrame.dispose();

  }

  private void loadData_BDOSFinal(String absolutePath, int scale,
      CartAGenDataSet dataset) {

    SymbolGroup symbGroup = SymbolsUtil.getSymbolGroup(SourceDLM.BD_OSFinal,
        scale);
    dataset.setSymbols(SymbolList.getSymbolList(symbGroup));

    ProgressFrame progressFrame = new ProgressFrame(
        "Data loading in progress...", true);
    progressFrame.setVisible(true);

    progressFrame.setTextAndValue("Loading road network", 0);

    dataset.loadOSRoads(absolutePath + "/tout meridian", 0, "OSODR");// ,SourceDLM.BD_Cete1,dataset.getSymbols());

    progressFrame.dispose();

  }

  public void enrichData(CartAGenDataSet dataSet) {

    CartagenApplication.cartagenApplication.getFrameInit().lblEnrichment
        .setForeground(Color.BLACK);
    CartagenApplication.cartagenApplication.getFrameInit().repaint();

    ProgressFrame progressFrame = new ProgressFrame(
        "Enrichement in progress...", true);
    progressFrame.setVisible(true);
    progressFrame.setTextAndValue("Network faces construction", 0);

    if (this.constructNetworkFaces) {

      if (CartagenApplication.logger.isInfoEnabled()) {
        CartagenApplication.logger.info("network faces construction");
      }
      NetworkEnrichment.buildNetworkFaces(dataSet);
      GeneralisationLeftPanelComplement.getInstance().cSelectNetworkFaces
          .setEnabled(true);
      GeneralisationLeftPanelComplement.getInstance().lNetworkFaces
          .setEnabled(true);
    }

    if (this.enrichissementRoutier) {

      progressFrame.setTextAndValue("Road network enrichment", 0);

      if (CartagenApplication.logger.isInfoEnabled()) {
        CartagenApplication.logger.info("road enrichment");
      }
      NetworkEnrichment.enrichNetwork(CartAGenDoc.getInstance()
          .getCurrentDataset().getRoadNetwork());

    }

    if (this.enrichissementBati) {

      progressFrame.setTextAndValue("Urban enrichment", 0);

      if (CartagenApplication.logger.isInfoEnabled()) {
        CartagenApplication.logger.info("building enrichment");
      }
      UrbanEnrichment.buildTowns(dataSet, this.enrichissementBatiAlign);

      GeneralisationLeftPanelComplement.getInstance().cVoirVille
          .setEnabled(true);
      GeneralisationLeftPanelComplement.getInstance().cVoirVille
          .setSelected(true);
      GeneralisationLeftPanelComplement.getInstance().cSelectVille
          .setEnabled(true);
      GeneralisationLeftPanelComplement.getInstance().cVoirVilleInitial
          .setEnabled(true);
      GeneralisationLeftPanelComplement.getInstance().lVille.setEnabled(true);
      GeneralisationLeftPanelComplement.getInstance().cVoirIlot
          .setEnabled(true);
      GeneralisationLeftPanelComplement.getInstance().cSelectIlot
          .setEnabled(true);
      GeneralisationLeftPanelComplement.getInstance().cVoirIlotInitial
          .setEnabled(true);
      GeneralisationLeftPanelComplement.getInstance().lIlot.setEnabled(true);
      if (this.enrichissementBati) {
        GeneralisationLeftPanelComplement.getInstance().cVoirAlign
            .setEnabled(true);
        GeneralisationLeftPanelComplement.getInstance().cSelectAlign
            .setEnabled(true);
        GeneralisationLeftPanelComplement.getInstance().cVoirAlignInitial
            .setEnabled(true);
        GeneralisationLeftPanelComplement.getInstance().lAlign.setEnabled(true);
      }
    }

    if (this.enrichissementHydro) {

      progressFrame.setTextAndValue("Hydro network enrichment", 0);

      if (CartagenApplication.logger.isInfoEnabled()) {
        CartagenApplication.logger.info("hydrography enrichment");
      }
      NetworkEnrichment.enrichNetwork(CartAGenDoc.getInstance()
          .getCurrentDataset().getHydroNetwork());

    }

    if (this.enrichissementRelief || this.enrichissementOccSol
        || this.enrichissementBati || this.enrichissementRoutier
        || this.enrichissementHydro || this.constructNetworkFaces) {
      CartagenApplication.cartagenApplication
          .getFrameInit()
          .isGood(
              CartagenApplication.cartagenApplication.getFrameInit().lblEnrichmentIcon);
    } else {
      CartagenApplication.cartagenApplication
          .getFrameInit()
          .isWrong(
              CartagenApplication.cartagenApplication.getFrameInit().lblEnrichmentIcon);
    }
    CartagenApplication.cartagenApplication.getFrameInit().repaint();

    // Refresh
    CartagenApplication.cartagenApplication.getFrame().getVisuPanel()
        .activate();

    /*
     * CarteTopo carteTopo = new CarteTopo("cartetopo"); //for (RoadNetworkAgent
     * res : (RoadNetworkAgent)this.getReseauRoutier()) { if
     * (GeneralisationDataSet
     * .getInstance().getReseauRoutier().getTroncons().size() > 0) {
     * carteTopo.importClasseGeo
     * (GeneralisationDataSet.getInstance().getReseauRoutier().getTroncons(),
     * true); }
     * 
     * if (CartagenApplication.logger.isTraceEnabled()) {
     * CartagenApplication.logger.trace("creation des noeuds"); }
     * carteTopo.creeNoeudsManquants(1.0);
     * 
     * if (CartagenApplication.logger.isTraceEnabled()) {
     * CartagenApplication.logger.trace("fusion des noeuds"); }
     * carteTopo.fusionNoeuds(1.0);
     * 
     * if (CartagenApplication.logger.isTraceEnabled()) {
     * CartagenApplication.logger.trace("filtrage des arcs doublons"); }
     * carteTopo.filtreArcsDoublons();
     * 
     * if (CartagenApplication.logger.isTraceEnabled()) {
     * CartagenApplication.logger.trace("rend planaire"); }
     * carteTopo.rendPlanaire(1.0);
     * 
     * if (CartagenApplication.logger.isTraceEnabled()) {
     * CartagenApplication.logger.trace("fusion des doublons"); }
     * carteTopo.fusionNoeuds(1.0);
     * 
     * if (CartagenApplication.logger.isTraceEnabled()) {
     * CartagenApplication.logger.trace("filtrage des arcs doublons"); }
     * carteTopo.filtreArcsDoublons();
     * 
     * if (CartagenApplication.logger.isTraceEnabled()) {
     * CartagenApplication.logger.trace("creation de la topologie des Faces"); }
     * carteTopo.creeTopologieFaces();
     * 
     * if (CartagenApplication.logger.isDebugEnabled()) {
     * CartagenApplication.logger.debug(carteTopo.getListeFaces().size() +
     * " faces trouvées"); }
     * 
     * if (CartagenApplication.logger.isDebugEnabled()) {
     * CartagenApplication.logger
     * .debug("construction de l'index spatial sur les faces"); }
     * carteTopo.filtreNoeudsSimples();
     */
    progressFrame.setVisible(false);
    progressFrame = null;

  }

  /**
   * Generalisation configuration file
   */

  public void lectureFichierConfigurationGeneralisation() {

    // le document XML
    Document docXML = null;
    try {
      docXML = DocumentBuilderFactory
          .newInstance()
          .newDocumentBuilder()
          .parse(
              CartagenApplication.class
                  .getResourceAsStream(this.cheminFichierConfigurationGeneralisation));
    } catch (FileNotFoundException e) {
      CartagenApplication.logger.error("Fichier non trouvé: "
          + this.cheminFichierConfigurationGeneralisation);
      e.printStackTrace();
      return;
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (docXML == null) {
      CartagenApplication.logger.error("Erreur lors de la lecture de: "
          + this.cheminFichierConfigurationDonnees);
      return;
    }

    Element configurationGeneralisationMirageXML = (Element) docXML
        .getElementsByTagName("configurationGeneralisationCartagen").item(0);
    if (configurationGeneralisationMirageXML == null) {
      return;
    }

    // general
    Element generalXML = (Element) configurationGeneralisationMirageXML
        .getElementsByTagName("general").item(0);
    if (generalXML != null) {
      Element elXML = null;
      // description
      elXML = (Element) generalXML.getElementsByTagName("description").item(0);
      if (elXML != null) {
        GeneralisationSpecifications.setDESCRIPTION(elXML.getFirstChild()
            .getNodeValue());
      }

      // echelle cible
      elXML = (Element) generalXML.getElementsByTagName("echelleCible").item(0);
      if (elXML != null) {
        Legend.setSYMBOLISATI0N_SCALE(Double.parseDouble(elXML.getFirstChild()
            .getNodeValue()));
      }

      // resolution
      elXML = (Element) generalXML.getElementsByTagName("resolution").item(0);
      if (elXML != null) {
        GeneralisationSpecifications.setRESOLUTION(Double.parseDouble(elXML
            .getFirstChild().getNodeValue()));
      }

    }

    // themes
    Element contrainteXML = (Element) configurationGeneralisationMirageXML
        .getElementsByTagName("themes").item(0);
    if (contrainteXML != null) {

      Element contXML = null;
      Element elXML = null;

      // bati
      Element batiXML = (Element) contrainteXML.getElementsByTagName("bati")
          .item(0);
      if (batiXML != null) {

        // taille
        contXML = (Element) batiXML.getElementsByTagName("taille").item(0);
        if (contXML != null) {

          elXML = (Element) contXML.getElementsByTagName("aireMinimale")
              .item(0);
          if (elXML != null) {
            GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }

          elXML = (Element) contXML
              .getElementsByTagName("aireSeuilSuppression").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.AIRE_SEUIL_SUPPRESSION_BATIMENT = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // granularite
        contXML = (Element) batiXML.getElementsByTagName("granularite").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("longueurMinimale")
              .item(0);
          if (elXML != null) {
            GeneralisationSpecifications.LONGUEUR_MINI_GRANULARITE = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // equarrite
        contXML = (Element) batiXML.getElementsByTagName("equarrite").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("toleranceAngle")
              .item(0);
          if (elXML != null) {
            GeneralisationSpecifications.TOLERANCE_ANGLE = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // convexite
        contXML = (Element) batiXML.getElementsByTagName("convexite").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("convexiteMini").item(
              0);
          if (elXML != null) {
            GeneralisationSpecifications.BUILDING_CONVEXITE_MINI = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // elongation
        contXML = (Element) batiXML.getElementsByTagName("elongation").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("elongationMini")
              .item(0);
          if (elXML != null) {
            GeneralisationSpecifications.BUILDING_ELONGATION_MINI = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // altitude
        contXML = (Element) batiXML.getElementsByTagName("altitude").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("denivelleeMini")
              .item(0);
          if (elXML != null) {
            GeneralisationSpecifications.DENIVELLEE_MINI = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // proximite
        contXML = (Element) batiXML.getElementsByTagName(
            "proximiteBatimentsIlot").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName(
              "distanceSeparationBatiments").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_BATIMENT = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }

          elXML = (Element) contXML.getElementsByTagName(
              "distancMaxDeplacementBatiment").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.DISTANCE_MAX_DEPLACEMENT_BATIMENT = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }

          elXML = (Element) contXML.getElementsByTagName(
              "distanceSeparationBatimentsRoutes").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.DISTANCE_SEPARATION_BATIMENT_ROUTE = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }

          elXML = (Element) contXML.getElementsByTagName(
              "seuilTauxSuperpositionSuppression").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.SEUIL_TAUX_SUPERPOSITION_SUPPRESSION = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }

          elXML = (Element) contXML
              .getElementsByTagName("distanceMaxProximite").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.DISTANCE_MAX_PROXIMITE = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // densite ilots
        contXML = (Element) batiXML.getElementsByTagName("densiteIlot").item(0);
        if (contXML != null) {

          elXML = (Element) contXML.getElementsByTagName("ratioDensiteIlot")
              .item(0);
          if (elXML != null) {
            GeneralisationSpecifications.RATIO_BLOCK_DENSITY = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }

          elXML = (Element) contXML
              .getElementsByTagName("densiteLimiteGrisage").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.DENSITE_LIMITE_GRISAGE_ILOT = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }

          elXML = (Element) contXML.getElementsByTagName(
              "ratioDensiteReduction").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.DENSITE_RATIO_REDUCTION_MAX = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // grands batiments
        contXML = (Element) batiXML.getElementsByTagName("grandsBatimentse")
            .item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("aireMin").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.GRANDS_BATIMENTS_AIRE = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

      }

      // routier
      Element routierXML = (Element) contrainteXML.getElementsByTagName(
          "routier").item(0);
      if (routierXML != null) {

        // enpatement
        contXML = (Element) routierXML.getElementsByTagName("empatement").item(
            0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("coeffPropagation")
              .item(0);
          if (elXML != null) {
            GeneralisationSpecifications.ROUTIER_COEFF_PROPAGATION_EMPATEMENT = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // impasses
        contXML = (Element) routierXML.getElementsByTagName("impasses").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("longueurMin").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.ROADS_DEADEND_MIN_LENGTH = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // densite Ville
        contXML = (Element) routierXML.getElementsByTagName("densiteVille")
            .item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("densiteRouteVille")
              .item(0);
          if (elXML != null) {
            GeneralisationSpecifications.ROAD_TOWN_DENSITY = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

      }

      // hydro
      Element hydroXML = (Element) contrainteXML.getElementsByTagName("hydro")
          .item(0);
      if (hydroXML != null) {

        // proximite routier
        contXML = (Element) hydroXML.getElementsByTagName("proximiteRoutier")
            .item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName(
              "distanceSeparationHydroRoute").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.DISTANCE_SEPARATION_HYDRO_ROUTIER = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }

          elXML = (Element) contXML.getElementsByTagName(
              "tauxSuperpositionHydroRoute").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.TAUX_SUPERPOSITION_HYDRO_ROUTIER = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

      }

      // relief
      Element reliefXML = (Element) contrainteXML
          .getElementsByTagName("relief").item(0);
      if (reliefXML != null) {

        // courbes de niveau
        contXML = (Element) reliefXML.getElementsByTagName("courbesNiveau")
            .item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("interDistance").item(
              0);
          if (elXML != null) {
            GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_CN = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

      }

      // occupation du sol
      Element occSolXML = (Element) contrainteXML
          .getElementsByTagName("occSol").item(0);
      if (occSolXML != null) {
      }

      if (CartagenApplication.logger.isDebugEnabled()) {
        CartagenApplication.logger
            .debug("fin chargement de la configuration de généralisation");
      }
    }

  }

  /**
   * Application configuration, including visu panel paramters and
   * initialisation of layers menu
   */

  public void applicationConfiguration() {

    // zoom
    CartagenApplication.cartagenApplication.getFrame().getVisuPanel()
        .setPixelSize(1);

    // position
    CartagenApplication.cartagenApplication.getFrame().getVisuPanel()
        .setGeoCenter(new DirectPosition(0.0, 0.0));

    // symbolisation
    CartagenApplication.cartagenApplication.getFrame().getLeftPanel().cSymbol
        .setSelected(true);
    // CartagenApplication.cartagenApplication.getFrame().getVisuPanel().symbolisationDisplay
    // = true;

    // symbolisation initiale
    CartagenApplication.cartagenApplication.getFrame().getLeftPanel().cSymbolInitial
        .setSelected(false);
    // CartagenApplication.cartagenApplication.getFrame().getVisuPanel().initialSymbolisationDisplay
    // = false;

    // distance selection
    CartagenApplication.cartagenApplication.getFrame().getVisuPanel()
        .setDistanceSelection(5.0);

    // affichage selection
    CartagenApplication.cartagenApplication.getFrame().getRightPanel().cDisplaySelection
        .setSelected(true);

    // antialiasing
    CartagenApplication.cartagenApplication.getFrame().getMenu().menuConfig.mAntiAliasing
        .setSelected(true);
    CartagenApplication.cartagenApplication.getFrame().getVisuPanel().antiAliasing = true;

    // Layer menu initialisation
    // getDocument().getCurrentDataset().resetDataSet();

  }

  /**
   * initialise the default geographic position of the interface
   */

  public void initialiserPositionGeographique(boolean extent) {

    if (!extent) {
      CartagenApplication.getInstance().getFrame().getVisuPanel()
          .zoomToFullExtent();
    } else {
      CartagenApplication.getInstance().getFrame().getVisuPanel()

      .zoomToFullExtent();
      CartAGenDoc
          .getInstance()
          .getCurrentDataset()
          .getCartAGenDB()
          .getDocument()
          .getZone()
          .setExtent(
              new GM_Polygon(CartagenApplication.getInstance().getFrame()
                  .getVisuPanel().getEnvelope()));

    }

    // Refresh
    CartagenApplication.cartagenApplication.getFrame().getVisuPanel()
        .activate();

  }

  /**
   * method to plug the interface components for generalisation
   */

  public void initialiserInterfacePourGeneralisation() {

    // ajout contenu generalisation au menu
    GeneralisationMenuComplement.getInstance().add();

    // ajout contenu generalisation panneau de gauche
    GeneralisationLeftPanelComplement.getInstance().add();

    // ajout contenu generalisation panneau visu
    GeneralisationVisuPanelComplement.getInstance().add();

    // ajout contenu generalisation panneau du bas
    GeneralisationBottomPanelComplement.getInstance().add();

    // add the mode selector
    this.getFrame().setModeSelector(new ModeSelector(this.getFrame()));

    CartagenApplication.cartagenApplication.getFrame().repaint();

  }

  /**
   * Get the standard {@link GeneObjImplementation} for {@code this}
   * application.
   * @return
   */
  private GeneObjImplementation geneObjImpl;

  public GeneObjImplementation getStandardImplementation() {
    return geneObjImpl;
  }

  public void setStandardImplementation(GeneObjImplementation impl) {
    this.geneObjImpl = impl;
  }
}
