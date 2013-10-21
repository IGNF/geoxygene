/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.xml.parsers.ParserConfigurationException;

import org.geotools.data.shapefile.shp.ShapefileException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.defaultschema.DefaultCreationFactory;
import fr.ign.cogit.cartagen.genealgorithms.landuse.LanduseSimplification;
import fr.ign.cogit.cartagen.pearep.derivation.ScaleMasterScheduler;
import fr.ign.cogit.cartagen.pearep.importexport.MGCPLoader;
import fr.ign.cogit.cartagen.pearep.importexport.SHOMLoader;
import fr.ign.cogit.cartagen.pearep.importexport.ShapeFileExport;
import fr.ign.cogit.cartagen.pearep.importexport.VMAP1PlusPlusLoader;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPSchemaFactory;
import fr.ign.cogit.cartagen.pearep.shom.SHOMSchemaFactory;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPSchemaFactory;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.software.dataset.PostgisDB;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolsUtil;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * The class that contains the main application for generalisation in PEA REP
 * project.
 * @author GTouya
 * 
 */
public class PeaRepGeneralisationPartition implements PropertyChangeListener,
    ActionListener {

  private JProgressBar progressBar;
  JFrame frame;
  private boolean stop;
  public static Logger errorLogger = Logger.getLogger("PeaRep.error.scheduler");

  public PeaRepGeneralisationPartition(JFrame frame, JProgressBar progressBar) {
    this.progressBar = progressBar;
    this.frame = frame;
    this.setStop(false);
  }

  /**
   * @param args
   */
  public static void main(String[] args) {

    // load dlls
    System.loadLibrary("triangulation");

    // create a progress bar
    JFrame frame = new JFrame("Progression de la généralisation");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Create and set up the content pane.
    JPanel newContentPane = new JPanel();
    JProgressBar progressBar = new JProgressBar(0, 100);
    progressBar.setValue(0);
    progressBar.setStringPainted(true);
    JButton stopBtn = new JButton("Stop");
    stopBtn.setActionCommand("stop");
    newContentPane.add(Box.createVerticalGlue());
    newContentPane.add(progressBar);
    newContentPane.add(Box.createVerticalGlue());
    newContentPane.add(stopBtn);
    newContentPane.add(Box.createVerticalGlue());
    newContentPane.setLayout(new BoxLayout(newContentPane, BoxLayout.Y_AXIS));
    newContentPane.setOpaque(true); // content panes must be opaque
    frame.setContentPane(newContentPane);
    frame.setSize(400, 150);

    // Display the window.
    frame.pack();
    frame.setVisible(true);

    PeaRepGeneralisationPartition main = new PeaRepGeneralisationPartition(
        frame, progressBar);
    GeneralisationPartitionTask task = new GeneralisationPartitionTask(main);
    task.addPropertyChangeListener(main);
    stopBtn.addActionListener(main);
    try {
      task.execute();
    } catch (Exception e) {
      frame.setVisible(false);
      PeaRepGeneralisationPartition.errorLogger.severe(e.getMessage());
      for (int i = 0; i < e.getStackTrace().length; i++) {
        PeaRepGeneralisationPartition.errorLogger.severe(e.getStackTrace()[i]
            .toString());
      }
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if ("progress" == evt.getPropertyName()) {
      int progress = (Integer) evt.getNewValue();
      this.progressBar.setValue(progress);
      if (progress == 100) {
        this.frame.setVisible(false);
        System.exit(0);
      }
    }
  }

  public void setStop(boolean stop) {
    this.stop = stop;
  }

  public boolean isStop() {
    return this.stop;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    this.setStop(true);
  }
}

class GeneralisationPartitionTask extends SwingWorker<Void, Void> {

  private PeaRepGeneralisationPartition main;
  private static String SCALE_MASTER_FILE = "ScaleMaster.xml";
  private static String PARAMETER_FILE = "PeaRepParameters.xml";
  private static String THEMES_FILE = "ScaleMasterThemes.xml";
  private static String MGCPPlusPlus_DATASET = "MGCPPlusPlus";
  private static String VMAP1PlusPlus_DATASET = "VMAP1PlusPlus";
  private static String SHOM_DATASET = "SHOM";
  private static Logger logger = Logger
      .getLogger(PeaRepGeneralisationPartition.class.getName());

  public GeneralisationPartitionTask(PeaRepGeneralisationPartition main) {
    super();
    this.main = main;
  }

  /*
   * Main task. Executed in background thread.
   */
  @Override
  public Void doInBackground() {
    try {
      // Initialize progress property.
      this.setProgress(0);
      int progress = 0;

      // ******************************************************
      // launch CartAGen as batch application
      // Objects creation factory
      CartagenApplication.getInstance().setCreationFactory(
          new DefaultCreationFactory());

      // Application initialisation
      CartagenApplication.getInstance().initApplication();
      CartAGenDocOld doc = CartAGenDocOld.getInstance();
      doc.setName("PEA_REP");
      doc.setPostGisDb(PostgisDB.get("PEA_REP", true));
      progress += 5;
      this.setProgress(progress);
      // Sleep for up to one second.
      try {
        if (this.main.isStop()) {
          this.setProgress(100);
        }
        Thread.sleep(500);
      } catch (InterruptedException ignore) {
      }

      // *******************************************************
      // first, create the scheduler by parsing the configuration files
      String jarPath = new File(PeaRepGeneralisationPartition.class
          .getProtectionDomain().getCodeSource().getLocation().toURI()
          .getPath().substring(1)).getParent();
      String pathScale = jarPath + "\\"
          + GeneralisationPartitionTask.SCALE_MASTER_FILE;
      String pathParams = jarPath + "\\"
          + GeneralisationPartitionTask.PARAMETER_FILE;
      String pathThemes = jarPath + "\\"
          + GeneralisationPartitionTask.THEMES_FILE;

      // JOptionPane.showMessageDialog(null, jarPath);

      File scaleMasterXml = new File(pathScale);
      File parameterXml = new File(pathParams);
      File themesFile = new File(pathThemes);
      ScaleMasterScheduler scheduler = null;
      try {
        scheduler = new ScaleMasterScheduler(scaleMasterXml, parameterXml,
            themesFile);
      } catch (DOMException e) {
        GeneralisationPartitionTask.logger
            .severe("Problem in creating the scheduler");
        e.printStackTrace();
      } catch (ParserConfigurationException e) {
        GeneralisationPartitionTask.logger
            .severe("Problem in creating the scheduler");
        e.printStackTrace();
      } catch (SAXException e) {
        GeneralisationPartitionTask.logger
            .severe("Problem in creating the scheduler");
        e.printStackTrace();
      } catch (IOException e) {
        GeneralisationPartitionTask.logger
            .severe("Problem in creating the scheduler");
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        GeneralisationPartitionTask.logger
            .severe("Problem in creating the scheduler");
        e.printStackTrace();
      }
      if (scheduler == null) {
        return null;
      }
      this.setProgress(10);
      // Sleep for up to one second.
      try {
        if (this.main.isStop()) {
          this.setProgress(100);
        }
        Thread.sleep(500);
      } catch (InterruptedException ignore) {
      }

      // *******************************************************

      boolean vmap1ppDb = false;
      boolean mgcpDb = false;
      boolean shomDb = false;

      // creation of a symbol group
      SymbolGroup symbGroup = SymbolsUtil.getSymbolGroup(
          SourceDLM.SPECIAL_CARTAGEN, Legend.getSYMBOLISATI0N_SCALE());

      // *******************************************************
      // Compute the partition of the databases (global envelope and cells grid)

      double cellSize = scheduler.getPartitionSize();

      List<IPolygon> listCellsMgcp = new ArrayList<IPolygon>();
      List<IPolygon> listCellsVmap1pp = new ArrayList<IPolygon>();
      List<IPolygon> listCellsShom = new ArrayList<IPolygon>();

      // For Mgcp++ databases
      if (scheduler.getMgcpPlusPlusFolder() != null) {

        // Compute the envelope of each layer
        MGCPLoader mgcpLoader = new MGCPLoader();
        mgcpLoader.computeEnvelopeTotale(
            new File(scheduler.getMgcpPlusPlusFolder()),
            scheduler.getListLayersMgcpPlusPlus());
        IPolygon envelope = mgcpLoader.getEnvelopeTotale();

        // Compute the cells grid
        // test if the cell size is not too big
        if (cellSize > (envelope.envelope().maxX() - envelope.envelope().minX())
            && cellSize > (envelope.envelope().maxY() - envelope.envelope()
                .minY())) {
          GeneralisationPartitionTask.logger
              .severe("Cell size is too big man...");
          listCellsMgcp.add(envelope);
          mgcpLoader.setCellsGrid(listCellsMgcp);
          // return null;
        } else {
          mgcpLoader.computeCellsGrid(cellSize);
          listCellsMgcp = mgcpLoader.getCellsGrid();
        }
      }

      // For Vmap1++ databases
      if (scheduler.getVmap1PlusPlusFolder() != null) {

        // Compute the envelope of each layer
        VMAP1PlusPlusLoader vmap1ppLoader = new VMAP1PlusPlusLoader();
        vmap1ppLoader.computeEnvelopeTotale(
            new File(scheduler.getVmap1PlusPlusFolder()),
            scheduler.getListLayersVmap1PlusPlus());
        IPolygon envelope = vmap1ppLoader.getEnvelopeTotale();

        // Compute the cells grid

        // test if the cell size is not too big
        if (cellSize > (envelope.envelope().maxX() - envelope.envelope().minX())
            && cellSize > (envelope.envelope().maxY() - envelope.envelope()
                .minY())) {
          GeneralisationPartitionTask.logger.severe("Cell size is too big...");
          listCellsVmap1pp.add(envelope);
          vmap1ppLoader.setCellsGrid(listCellsVmap1pp);
          // return null;
        } else {
          vmap1ppLoader.computeCellsGrid(cellSize);
          listCellsVmap1pp = vmap1ppLoader.getCellsGrid();
        }
      }

      // For Shom databases
      if (scheduler.getShomFolder() != null) {

        // Compute the envelope of each layer
        SHOMLoader shomLoader = new SHOMLoader();
        shomLoader.computeEnvelopeTotale(new File(scheduler.getShomFolder()),
            scheduler.getListLayersShom());
        IPolygon envelope = shomLoader.getEnvelopeTotale();

        // Compute the cells grid
        // test if the cell size is not too big
        if (cellSize > (envelope.envelope().maxX() - envelope.envelope().minX())
            && cellSize > (envelope.envelope().maxY() - envelope.envelope()
                .minY())) {
          GeneralisationPartitionTask.logger
              .severe("Cell size is too big man...");
          listCellsShom.add(envelope);
          shomLoader.setCellsGrid(listCellsShom);
          // return null;
        } else {
          shomLoader.computeCellsGrid(cellSize);
          listCellsShom = shomLoader.getCellsGrid();
        }
      }

      this.setProgress(20);
      // Sleep for up to one second.
      try {
        if (this.main.isStop()) {
          this.setProgress(100);
        }
        Thread.sleep(500);
      } catch (InterruptedException ignore) {
      }

      // *******************************************************
      // import, generalize and export each MGCP partition
      if (scheduler.getMgcpPlusPlusFolder() != null) {
        int numPartMgcp = 1;

        for (IPolygon cell : listCellsMgcp) {

          mgcpDb = true;
          MGCPLoader mgcpLoader = new MGCPLoader(symbGroup,
              GeneralisationPartitionTask.MGCPPlusPlus_DATASET);

          try {
            mgcpLoader.loadDataPartition(
                new File(scheduler.getMgcpPlusPlusFolder()),
                scheduler.getListLayersMgcpPlusPlus(), cell);
          } catch (ShapefileException e1) {
            GeneralisationPartitionTask.logger
                .severe("Problem during MGCP loading");
            e1.printStackTrace();
          } catch (IOException e1) {
            GeneralisationPartitionTask.logger
                .severe("Problem during MGCP loading");
            e1.printStackTrace();
          }
          if (mgcpDb == true) {
            CartagenApplication.getInstance().setCreationFactory(
                new MGCPSchemaFactory());
          }

          // *******************************************************
          // trigger the generalisation
          try {
            scheduler.generalise();
          } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getStackTrace());
          }

          // *******************************************************
          // trigger landuse generalisation
          // get themes to be only exported by the landuse simplification
          // process
          List<String> listThemeLanduse = new ArrayList<String>();
          Map<IFeatureCollection<IFeature>, String> mapFtColOut = new HashMap<IFeatureCollection<IFeature>, String>();

          if (!(scheduler.getMapLanduseParamIn().isEmpty())) {
            Map<IFeatureCollection<IFeature>, Map<String, Double>> mapFtColIn = scheduler
                .getMapLanduseParamIn();
            double dpFiltering = scheduler.getLanduseDpFilter();
            Map<String, String> themeReclassification = scheduler
                .getLanduseReclass();
            GeneralisationPartitionTask.logger
                .info("Début de la généralisation de l'occupation du sol");
            try {
              mapFtColOut = LanduseSimplification.landuseSimplify(mapFtColIn,
                  dpFiltering, themeReclassification);
            } catch (Exception e) {
              e.printStackTrace();
            }
            Iterator<IFeatureCollection<IFeature>> itFtCol = mapFtColIn
                .keySet().iterator();
            while (itFtCol.hasNext()) {
              String nomTheme = mapFtColIn.get(itFtCol.next()).keySet()
                  .iterator().next();
              listThemeLanduse.add(nomTheme);
            }
          }

          // *******************************************************
          // finally, export data
          String exportPath = scheduler.getExportFolder();
          if (exportPath == null) {
            exportPath = jarPath;
          }
          File filePartition = new File(exportPath + scheduler.getScale() + "_"
              + numPartMgcp + "\\");
          filePartition.mkdir();
          ShapeFileExport exportTool = new ShapeFileExport(filePartition,
              doc.getCurrentDataset(), scheduler.getScaleMaster(),
              scheduler.getScale());
          exportTool.setListThemesNotExport(listThemeLanduse);
          exportTool.exportToShapefiles();
          // export generalised landuse
          if (!mapFtColOut.isEmpty()) {
            exportTool.exportLanduseToShapefiles(mapFtColOut);
          }

          // Vide la map...
          Map<IFeatureCollection<IFeature>, Map<String, Double>> mapFtColVide = new HashMap<IFeatureCollection<IFeature>, Map<String, Double>>();
          scheduler.setMapLanduseParamIn(mapFtColVide);
          numPartMgcp = numPartMgcp + 1;
        }
      }

      this.setProgress(50);
      // Sleep for up to one second.
      try {
        if (this.main.isStop()) {
          this.setProgress(100);
        }
        Thread.sleep(500);
      } catch (InterruptedException ignore) {
      }

      // *******************************************************
      // import, generalize and export each VMAP1PP partition

      if (scheduler.getVmap1PlusPlusFolder() != null) {

        int numPartVmap1pp = 1;
        for (IPolygon cell : listCellsVmap1pp) {

          vmap1ppDb = true;
          VMAP1PlusPlusLoader vmap1PlusPlusLoader = new VMAP1PlusPlusLoader(
              symbGroup, GeneralisationPartitionTask.VMAP1PlusPlus_DATASET);
          try {
            vmap1PlusPlusLoader.loadDataPartition(
                new File(scheduler.getVmap1PlusPlusFolder()),
                scheduler.getListLayersVmap1PlusPlus(), cell);
          } catch (ShapefileException e1) {
            GeneralisationPartitionTask.logger
                .severe("Problem during VMAP1++ loading");
            e1.printStackTrace();
          } catch (IOException e1) {
            GeneralisationPartitionTask.logger
                .severe("Problem during VMAP1++ loading");
            e1.printStackTrace();
          }

          if (vmap1ppDb == true) {
            CartagenApplication.getInstance().setCreationFactory(
                new VMAP1PPSchemaFactory());
          }

          // *******************************************************
          // trigger the generalisation
          try {
            scheduler.generalise();
          } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getStackTrace());
          }

          // *******************************************************
          // trigger landuse generalisation
          // get themes to be only exported by the landuse simplification
          // process
          List<String> listThemeLanduse = new ArrayList<String>();
          Map<IFeatureCollection<IFeature>, String> mapFtColOut = new HashMap<IFeatureCollection<IFeature>, String>();
          if (!(scheduler.getMapLanduseParamIn().isEmpty())) {
            Map<IFeatureCollection<IFeature>, Map<String, Double>> mapFtColIn = scheduler
                .getMapLanduseParamIn();
            double dpFiltering = scheduler.getLanduseDpFilter();
            Map<String, String> themeReclassification = scheduler
                .getLanduseReclass();
            GeneralisationPartitionTask.logger
                .info("Début de la généralisation de l'occupation du sol");
            try {
              mapFtColOut = LanduseSimplification.landuseSimplify(mapFtColIn,
                  dpFiltering, themeReclassification);
            } catch (Exception e) {
              e.printStackTrace();
            }
            Iterator<IFeatureCollection<IFeature>> itFtCol = mapFtColIn
                .keySet().iterator();
            while (itFtCol.hasNext()) {
              String nomTheme = mapFtColIn.get(itFtCol.next()).keySet()
                  .iterator().next();
              listThemeLanduse.add(nomTheme);
              System.out.println("Nom Theme : " + nomTheme);
            }
          }

          // *******************************************************
          // finally, export data
          String exportPath = scheduler.getExportFolder();
          if (exportPath == null) {
            exportPath = jarPath;
          }
          File filePartition = new File(exportPath + scheduler.getScale() + "_"
              + numPartVmap1pp + "\\");
          filePartition.mkdir();
          ShapeFileExport exportTool = new ShapeFileExport(filePartition,
              doc.getCurrentDataset(), scheduler.getScaleMaster(),
              scheduler.getScale());
          exportTool.setListThemesNotExport(listThemeLanduse);
          exportTool.exportToShapefiles();
          // export generalised landuse
          if (!mapFtColOut.isEmpty()) {
            exportTool.exportLanduseToShapefiles(mapFtColOut);
          }
          // Vide la map...
          Map<IFeatureCollection<IFeature>, Map<String, Double>> mapFtColVide = new HashMap<IFeatureCollection<IFeature>, Map<String, Double>>();
          scheduler.setMapLanduseParamIn(mapFtColVide);

          numPartVmap1pp = numPartVmap1pp + 1;
        }
      }

      this.setProgress(80);
      // Sleep for up to one second.
      try {
        if (this.main.isStop()) {
          this.setProgress(100);
        }
        Thread.sleep(500);
      } catch (InterruptedException ignore) {
      }

      // *******************************************************
      // import, generalize and export each SHOM partition

      if (scheduler.getShomFolder() != null) {

        int numPartShom = 1;
        for (IPolygon cell : listCellsShom) {

          shomDb = true;
          System.out.println("on a chargé les données SHOM");
          SHOMLoader shomLoader = new SHOMLoader(symbGroup,
              GeneralisationPartitionTask.SHOM_DATASET);
          try {
            shomLoader.loadDataPartition(new File(scheduler.getShomFolder()),
                scheduler.getListLayersShom(), cell);

          } catch (ShapefileException e1) {
            GeneralisationPartitionTask.logger
                .severe("Problem during SHOM loading");
            e1.printStackTrace();
          } catch (IOException e1) {
            GeneralisationPartitionTask.logger
                .severe("Problem during SHOM loading");
            e1.printStackTrace();
          }

          if (shomDb == true) {
            CartagenApplication.getInstance().setCreationFactory(
                new SHOMSchemaFactory());
          }

          // *******************************************************
          // trigger the generalisation
          try {
            scheduler.generalise();
          } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getStackTrace());
          }

          // *******************************************************
          // trigger landuse generalisation
          // get themes to be only exported by the landuse simplification
          // process
          List<String> listThemeLanduse = new ArrayList<String>();
          Map<IFeatureCollection<IFeature>, String> mapFtColOut = new HashMap<IFeatureCollection<IFeature>, String>();
          if (!(scheduler.getMapLanduseParamIn().isEmpty())) {
            Map<IFeatureCollection<IFeature>, Map<String, Double>> mapFtColIn = scheduler
                .getMapLanduseParamIn();
            Map<String, String> themeReclassification = scheduler
                .getLanduseReclass();
            double dpFiltering = scheduler.getLanduseDpFilter();
            GeneralisationPartitionTask.logger
                .info("Début de la généralisation de l'occupation du sol");
            try {
              mapFtColOut = LanduseSimplification.landuseSimplify(mapFtColIn,
                  dpFiltering, themeReclassification);
            } catch (Exception e) {
              e.printStackTrace();
            }
            Iterator<IFeatureCollection<IFeature>> itFtCol = mapFtColIn
                .keySet().iterator();
            while (itFtCol.hasNext()) {
              String nomTheme = mapFtColIn.get(itFtCol.next()).keySet()
                  .iterator().next();

              listThemeLanduse.add(nomTheme);
            }
          }

          // *******************************************************
          // finally, export data
          String exportPath = scheduler.getExportFolder();
          if (exportPath == null) {
            exportPath = jarPath;
          }
          File filePartition = new File(exportPath + scheduler.getScale() + "_"
              + numPartShom + "\\");
          filePartition.mkdir();
          ShapeFileExport exportTool = new ShapeFileExport(filePartition,
              doc.getCurrentDataset(), scheduler.getScaleMaster(),
              scheduler.getScale());
          exportTool.setListThemesNotExport(listThemeLanduse);
          exportTool.exportToShapefiles();
          // export generalised landuse
          if (!mapFtColOut.isEmpty()) {
            exportTool.exportLanduseToShapefiles(mapFtColOut);
          }
          // Vide la map...
          Map<IFeatureCollection<IFeature>, Map<String, Double>> mapFtColVide = new HashMap<IFeatureCollection<IFeature>, Map<String, Double>>();
          scheduler.setMapLanduseParamIn(mapFtColVide);

          numPartShom = numPartShom + 1;
        }
      }

      this.setProgress(100);
      // Sleep for up to one second.
      try {
        Thread.sleep(500);
      } catch (InterruptedException ignore) {
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getStackTrace());
      this.main.frame.setVisible(false);
    }
    return null;
  }

  /*
   * Executed in event dispatching thread
   */
  @Override
  public void done() {
  }
}
