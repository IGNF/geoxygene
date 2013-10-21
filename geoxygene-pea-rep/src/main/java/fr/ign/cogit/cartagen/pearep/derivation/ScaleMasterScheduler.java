/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.derivation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.scalemaster.MultiThemeParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleLine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMaster;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterElement;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterMultiElement;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterMultiProcess;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterTheme;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterXMLParser;
import fr.ign.cogit.cartagen.pearep.derivation.processes.BridgeCollapseProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.CollapseToPointProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.ContourSelectionProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.DisplacementLSAProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.ElectricTypificationProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.FilteringProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.GaussianFilteringProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.KMeansClusteringProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.PointsConvexHullProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.PointsNonConvexHullProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.PolygonSimplification;
import fr.ign.cogit.cartagen.pearep.derivation.processes.RailwaySelectionProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.RaposoSimplifProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.RiverStrokeSelectionProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.RoundaboutCollapseProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.RunwaySimplificationProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.SkeletonizeProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.SpinalizeProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.StrokeSelectionProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.TaxiwaySimplificationProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.UnionProcess;
import fr.ign.cogit.cartagen.pearep.derivation.processes.VisvalingamWhyattProcess;
import fr.ign.cogit.cartagen.pearep.enrichment.CutNetworkPreProcess;
import fr.ign.cogit.cartagen.pearep.enrichment.DeleteDoublePreProcess;
import fr.ign.cogit.cartagen.pearep.enrichment.MakeNetworkPlanar;
import fr.ign.cogit.cartagen.pearep.enrichment.MakeNetworkPlanarDir;
import fr.ign.cogit.cartagen.pearep.enrichment.ScaleMasterPreProcess;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.filter.BinaryLogicOpsType;
import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.filter.UnaryLogicOpsType;

public class ScaleMasterScheduler {

  private Logger logger = Logger
      .getLogger(ScaleMasterScheduler.class.getName());
  public static Logger traceLogger = Logger.getLogger("PeaRep.trace.scheduler");
  public static Logger errorLogger = Logger.getLogger("PeaRep.error.scheduler");

  /**
   * The final generalisation scale for {@code this} scheduler.
   */
  private int scale;
  /**
   * The {@link ScaleMaster} instance that contains the generalisation knowledge
   * for {@code this} scheduler.
   */
  private ScaleMaster scaleMaster;
  /**
   * The folder where the initial data shapefiles are stored. This information
   * can be filled by the parameters XML file.
   */
  private String vmap2iFolder, vmap1Folder, vmap0Folder, mgcpPlusPlusFolder,
      vmap1PlusPlusFolder, shomFolder;
  /**
   * The folder where export shapefiles are generated. If null, export is made
   * in the root.
   */
  private String exportFolder;

  private Map<IFeatureCollection<IFeature>, Map<String, Double>> mapLanduseParamIn = new HashMap<IFeatureCollection<IFeature>, Map<String, Double>>();
  private Double landuseDpFilter;
  private Map<String, String> landuseReclass = new HashMap<String, String>();

  /**
   * Theme that can be used in the scalemasters.
   */
  private Set<ScaleMasterTheme> themes;

  private Set<ScaleMasterGeneProcess> availableProcesses;
  private Set<ScaleMasterPreProcess> availablePreProcesses;
  private Set<ScaleMasterMultiProcess> availableMultiProcesses;

  private List<DataCorrection> corrections = new ArrayList<DataCorrection>();

  private List<String> listLayersVmap2i;
  private List<String> listLayersVmap1;
  private List<String> listLayersVmap0;
  private List<String> listLayersMgcpPlusPlus;
  private List<String> listLayersVmap1PlusPlus;
  private List<String> listLayersShom;

  /**
   * The cell cize (°) used to perform a generalisation with partition.
   */
  private double partitionSize;

  /**
   * Take into account the databases of lower level of detail in the
   * generalization process
   */
  private boolean isAware;

  /**
   * A constructor from the XML configuration files describing the ScaleMaster
   * to use and the general parameters.
   * @param scaleMasterXml
   * @param parameterXml
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   * @throws ClassNotFoundException
   * @throws DOMException
   */
  public ScaleMasterScheduler(File scaleMasterXml, File parameterXml,
      File themesFile) throws ParserConfigurationException, SAXException,
      IOException, DOMException, ClassNotFoundException {
    this.initLoggers();
    this.initThemes(themesFile);
    this.initPreProcesses();
    XMLParser smParser = new XMLParser(scaleMasterXml);
    XMLParser paramParser = new XMLParser(parameterXml);
    this.scaleMaster = smParser.parseScaleMaster(this);
    paramParser.parseParameters(this);
    this.initProcesses();
  }

  /**
   * A constructor with the Java object parameters already built.
   * @param scaleMaster
   * @param scale
   */
  public ScaleMasterScheduler(ScaleMaster scaleMaster,
      Set<ScaleMasterTheme> themes, int scale) {
    this.themes = themes;
    this.scaleMaster = scaleMaster;
    this.initProcesses();
    this.initPreProcesses();
    this.scale = scale;
  }

  /**
   * A default constructor that builds an empty scheduler, with only the
   * available processes and pre-processes. Useful for the GUI that helps
   * editing xml files.
   */
  public ScaleMasterScheduler() {
    this.initProcesses();
    this.initPreProcesses();
  }

  public int getScale() {
    return this.scale;
  }

  public void setScale(int scale) {
    this.scale = scale;
  }

  public List<String> getListLayersVmap2i() {
    return this.listLayersVmap2i;
  }

  public void setListLayersVmap2i(List<String> listLayersVmap2i) {
    this.listLayersVmap2i = listLayersVmap2i;
  }

  public List<String> getListLayersVmap1() {
    return this.listLayersVmap1;
  }

  public void setListLayersVmap1(List<String> listLayersVmap1) {
    this.listLayersVmap1 = listLayersVmap1;
  }

  public List<String> getListLayersVmap0() {
    return this.listLayersVmap0;
  }

  public void setListLayersVmap0(List<String> listLayersVmap0) {
    this.listLayersVmap0 = listLayersVmap0;
  }

  public ScaleMaster getScaleMaster() {
    return this.scaleMaster;
  }

  public void setScaleMaster(ScaleMaster scaleMaster) {
    this.scaleMaster = scaleMaster;
  }

  public String getVmap2iFolder() {
    return this.vmap2iFolder;
  }

  public void setVmap2iFolder(String vmap2iFolder) {
    this.vmap2iFolder = vmap2iFolder;
  }

  public String getVmap1Folder() {
    return this.vmap1Folder;
  }

  public void setVmap1Folder(String vmap1Folder) {
    this.vmap1Folder = vmap1Folder;
  }

  public void setVmap0Folder(String vmap0Folder) {
    this.vmap0Folder = vmap0Folder;
  }

  public String getVmap0Folder() {
    return this.vmap0Folder;
  }

  public List<DataCorrection> getCorrections() {
    return corrections;
  }

  public void setCorrections(List<DataCorrection> corrections) {
    this.corrections = corrections;
  }

  public Set<ScaleMasterTheme> getThemes() {
    return themes;
  }

  public void setThemes(Set<ScaleMasterTheme> themes) {
    this.themes = themes;
  }

  /**
   * Initialise the {@link ScaleMasterTheme} objects known by {@code this}.
   * @throws ClassNotFoundException
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   * @throws DOMException
   */
  private void initThemes(File themeFile) throws DOMException,
      ParserConfigurationException, SAXException, IOException,
      ClassNotFoundException {
    ScaleMasterXMLParser parser = new ScaleMasterXMLParser(null);
    this.themes = parser.parseScaleMasterThemes(themeFile);
  }

  private void initProcesses() {
    this.availableProcesses = new HashSet<ScaleMasterGeneProcess>();
    this.availableProcesses.add(FilteringProcess.getInstance());
    this.availableProcesses.add(GaussianFilteringProcess.getInstance());
    this.availableProcesses.add(UnionProcess.getInstance());
    this.availableProcesses.add(ContourSelectionProcess.getInstance());
    this.availableProcesses.add(PolygonSimplification.getInstance());
    this.availableProcesses.add(StrokeSelectionProcess.getInstance());
    this.availableProcesses.add(CollapseToPointProcess.getInstance());
    this.availableProcesses.add(RunwaySimplificationProcess.getInstance());
    this.availableProcesses.add(SkeletonizeProcess.getInstance());
    this.availableProcesses.add(SpinalizeProcess.getInstance());
    this.availableProcesses.add(BridgeCollapseProcess.getInstance());
    this.availableProcesses.add(VisvalingamWhyattProcess.getInstance());
    this.availableProcesses.add(RaposoSimplifProcess.getInstance());
    this.availableProcesses.add(KMeansClusteringProcess.getInstance());
    this.availableProcesses.add(PointsConvexHullProcess.getInstance());
    this.availableProcesses.add(PointsNonConvexHullProcess.getInstance());
    this.availableProcesses.add(TaxiwaySimplificationProcess.getInstance());
    this.availableProcesses.add(RiverStrokeSelectionProcess.getInstance());
    this.availableProcesses.add(RailwaySelectionProcess.getInstance());
    this.availableProcesses.add(RoundaboutCollapseProcess.getInstance());
    this.availableProcesses.add(ElectricTypificationProcess.getInstance());
    for (ScaleMasterGeneProcess proc : availableProcesses)
      proc.setScaleMaster(scaleMaster);
    this.availableMultiProcesses = new HashSet<ScaleMasterMultiProcess>();
    this.availableMultiProcesses.add(DisplacementLSAProcess.getInstance());
  }

  /**
   * Initialise the available pre-processes for the scheduler.
   */
  private void initPreProcesses() {
    this.availablePreProcesses = new HashSet<ScaleMasterPreProcess>();
    this.availablePreProcesses.add(DeleteDoublePreProcess.getInstance());
    this.availablePreProcesses.add(MakeNetworkPlanar.getInstance());
    this.availablePreProcesses.add(MakeNetworkPlanarDir.getInstance());
    this.availablePreProcesses.add(CutNetworkPreProcess.getInstance());
  }

  private void initLoggers() throws SecurityException, IOException {
    for (Handler handler : ScaleMasterScheduler.traceLogger.getHandlers()) {
      if (handler instanceof FileHandler) {
        handler = new FileHandler("/trace_" + this.scale + "_"
            + new Date().toString() + ".log", 5000000, 1, true);
      }
    }
    for (Handler handler : ScaleMasterScheduler.errorLogger.getHandlers()) {
      if (handler instanceof FileHandler) {
        handler = new FileHandler("/log_erreurs_" + this.scale + "_"
            + new Date().toString() + ".log", 5000000, 1, true);
      }
    }
  }

  /**
   * Get the {@link ScaleMasterTheme} object stored in this scheduler that
   * corresponds to the given name.
   * @param name
   * @return
   */
  public ScaleMasterTheme getThemeFromName(String name) {
    for (ScaleMasterTheme theme : this.themes) {
      if (theme.getName().equals(name)) {
        return theme;
      }
    }
    return null;
  }

  public void generalise() throws Exception {

    // first, trigger the preprocesses to correct data
    for (DataCorrection correction : corrections) {
      ScaleMasterScheduler.traceLogger.info("début de la correction "
          + correction.getProcess().getPreProcessName() + " des thèmes "
          + correction.getThemes());
      correction.triggerDataCorrection();
    }

    // sets the final scale
    Legend.setSYMBOLISATI0N_SCALE(scale);

    // loop on the lines of the ScaleMaster
    for (ScaleLine line : this.scaleMaster.getScaleLines()) {

      // get the element corresponding to final scale
      ScaleMasterElement elem = line.getElementFromScale(this.scale);

      // case with a scale master without rule for the current final scale and
      // current line. No need to delete features as the export of this line
      // will be skipped.
      if (elem == null) {
        continue;
      }

      this.logger.fine(elem.toString());
      ScaleMasterScheduler.traceLogger
          .info("début de la généralisation du thème " + line.getTheme());
      // test if the element relates to an existing database
      if (!CartAGenDocOld.getInstance().getDatabases().keySet()
          .contains(elem.getDbName()))
        continue;

      // get the dataset related to the element
      CartAGenDataSet dataset = CartAGenDocOld.getInstance().getDataset(
          elem.getDbName());
      CartAGenDocOld.getInstance().setCurrentDataset(dataset);

      // get the corresponding feature population
      Class<?> classObj = elem.getClasses().iterator().next();
      IPopulation<IGeneObj> features = new Population<IGeneObj>();
      IPopulation<IGeneObj> pop = dataset.getCartagenPop(dataset
          .getPopNameFromClass(classObj));

      if (pop == null) {
        // these features have not been imported
        continue;
      }
      for (IGeneObj obj : pop) {
        if (classObj.isInstance(obj)) {
          features.add(obj);
        }
      }

      // orders the processes and filter to apply
      List<OrderedProcess> procList = this.orderProcesses(elem, features);

      // test if there are features for this theme in the dbs
      if (features.size() == 0) {
        continue;
      }

      // apply the processes in the priority order
      for (OrderedProcess orderedProc : procList) {
        // test if the current process is the filter
        if (orderedProc.isFilter()) {
          // apply the OGCFilter and mark deleted features
          ScaleMasterScheduler.traceLogger.info("Application du filtre "
              + this.filterToString((Filter) orderedProc.getProcess())
              + " sur " + elem.getClasses() + " de " + elem.getDbName());
          this.applyOGCFilter(elem, features);
          continue;
        }

        // apply the process
        // get the generalisation process name
        String procName = (String) orderedProc.getProcess();

        // if (!(procName.equals("LanduseSimplify"))) {

        // get the generalisation process named procName
        ScaleMasterGeneProcess process = this.getProcessFromName(procName);
        // get the parameters
        Map<String, Object> parameters = elem.getParameters().get(
            elem.getProcessesToApply().indexOf(procName));
        for (String paramName : parameters.keySet()) {
          Object value = parameters.get(paramName);
          ProcessParameter param = new ProcessParameter(paramName,
              value.getClass(), value);
          process.addParameter(param);
        }

        // parameterise the process
        process.parameterise();
        ScaleMasterScheduler.traceLogger.info("Application du processus "
            + procName + " avec comme parametres " + parameters + " sur "
            + elem.getClasses() + " de " + elem.getDbName());
        // execute the process on the features
        try {
          process.execute(features);
          // update features
          for (IGeneObj objPop : dataset.getCartagenPop(dataset
              .getPopNameFromClass(classObj))) {
            if (!features.contains(objPop))
              features.add(objPop);
          }
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, e.getStackTrace(), e.getClass()
              .getSimpleName(), JOptionPane.ERROR_MESSAGE);
        }
      }

      if (isAware == true) {
        ScaleMasterScheduler.traceLogger
            .info("Application du processus de generalisation consciente sur "
                + elem.getClasses() + " de " + elem.getDbName());
        generalizeAware(line, elem, classObj, pop);
      }

    }

    // Trigger the multi-line processes if necessary
    // get the element corresponding to final scale
    if (this.scaleMaster.getMultiLine() != null) {
      for (ScaleMasterMultiElement elem : this.scaleMaster.getMultiLine()
          .getElementsFromScale(this.scale)) {
        // get the dataset related to the element
        CartAGenDataSet dataset = CartAGenDocOld.getInstance().getDataset(
            elem.getDbName());
        CartAGenDocOld.getInstance().setCurrentDataset(dataset);

        // get the corresponding feature population
        IPopulation<IGeneObj> features = new Population<IGeneObj>();
        for (ScaleMasterTheme theme : elem.getThemes()) {
          Class<?> classObj = theme.getImplementationClasses(dataset
              .getCartAGenDB().getGeneObjImpl());
          IPopulation<IGeneObj> pop = dataset.getCartagenPop(dataset
              .getPopNameFromClass(classObj));
          if (pop == null) {
            // these features have not been imported
            continue;
          }
          for (IGeneObj obj : pop) {
            if (classObj.isInstance(obj)) {
              features.add(obj);
            }
          }
        }

        // get the generalisation process named procName
        ScaleMasterMultiProcess process = this.getMultiProcessFromName(elem
            .getProcessName());
        for (MultiThemeParameter param : elem.getParams())
          process.addParameter(param);
        // parameterise the process
        process.parameterise();

        // execute the process on the features
        try {
          process.execute(features);
          // update features
          for (ScaleMasterTheme theme : elem.getThemes()) {
            Class<?> classObj = theme.getImplementationClasses(dataset
                .getCartAGenDB().getGeneObjImpl());
            for (IGeneObj objPop : dataset.getCartagenPop(dataset
                .getPopNameFromClass(classObj))) {
              if (!features.contains(objPop))
                features.add(objPop);
            }
          }
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, e.getStackTrace());
        }
      }
    }
  }

  /**
   * Apply the OGCFilter and mark the deleted features of the population.
   * @param features
   */
  protected void applyOGCFilter(ScaleMasterElement elem,
      IPopulation<IGeneObj> features) {
    for (IGeneObj obj : features) {
      if (!elem.getOgcFilter().evaluate(obj)) {
        obj.eliminateBatch();
      }
    }
  }

  protected ScaleMasterGeneProcess getProcessFromName(String procName) {
    for (ScaleMasterGeneProcess proc : this.availableProcesses) {
      if (proc.getProcessName().equals(procName)) {
        return proc;
      }
    }
    return null;
  }

  protected ScaleMasterPreProcess getPreProcessFromName(String procName) {
    for (ScaleMasterPreProcess proc : this.availablePreProcesses) {
      if (proc.getPreProcessName().equals(procName)) {
        return proc;
      }
    }
    return null;
  }

  protected ScaleMasterMultiProcess getMultiProcessFromName(String procName) {
    for (ScaleMasterMultiProcess proc : this.availableMultiProcesses) {
      if (proc.getProcessName().equals(procName)) {
        return proc;
      }
    }
    return null;
  }

  /**
   * Order the processes and the filter to apply according to their priority.
   * Remove landuse simplification processes from the list, and fill the landuse
   * simplification process map when needed.
   * @param features
   * @param elem
   * @return
   */
  protected List<OrderedProcess> orderProcesses(ScaleMasterElement elem,
      IPopulation<IGeneObj> features) {

    List<OrderedProcess> procList = new ArrayList<OrderedProcess>();
    if (elem.getOgcFilter() != null) {
      procList.add(new OrderedProcess(elem.getFilterPriority(), elem
          .getOgcFilter()));
    }
    for (int i = 0; i < elem.getProcessesToApply().size(); i++) {
      procList.add(new OrderedProcess(elem.getProcessPriorities().get(i), elem
          .getProcessesToApply().get(i)));
    }
    Collections.sort(procList);
    Collections.reverse(procList);

    // Remove landuse simplification processes from the list and fill the
    // landuse simplification process map
    Iterator<OrderedProcess> itProcess = procList.iterator();

    while (itProcess.hasNext()) {
      OrderedProcess orderedProc = itProcess.next();

      if (!orderedProc.isFilter()) {

        String procName = (String) orderedProc.getProcess();
        if (procName.equals("LanduseSimplify")) {

          fillLanduseSimplificationProcess(elem, orderedProc, features);
          itProcess.remove();
        }
      }
    }

    return procList;
  }

  /**
   * Fill the landuse simplification process map.
   * @param elem
   * @param orderedProc
   * @param features
   */
  protected void fillLanduseSimplificationProcess(ScaleMasterElement elem,
      OrderedProcess orderedProc, IPopulation<IGeneObj> features) {
    // get the parameters
    String procName = (String) orderedProc.getProcess();
    Map<String, Object> parameters = elem.getParameters().get(
        elem.getProcessesToApply().indexOf(procName));
    String name = elem.getScaleLine().getTheme().toString();
    double areaMin = (Double) parameters.get("min_area");
    if (!(parameters.get("dp_filtering") == null)) {
      double dpFiltering = (Double) parameters.get("dp_filtering");
      this.landuseDpFilter = dpFiltering;
    }
    if (parameters.get("final_theme") != null) {
      String finalTheme = (String) parameters.get("final_theme");
      if (!finalTheme.equals(""))
        this.landuseReclass.put(elem.getScaleLine().getTheme().getName(),
            finalTheme);
    }
    Map<String, Double> mapNameArea = new HashMap<String, Double>();
    mapNameArea.put(name, areaMin);
    IFeatureCollection<IFeature> ftCol = new FT_FeatureCollection<IFeature>();
    for (Object obj : features) {
      IFeature ft = (IFeature) obj;
      ftCol.add(ft);
    }
    if (!(ftCol.isEmpty() && mapNameArea.isEmpty())) {
      this.mapLanduseParamIn.put(ftCol, mapNameArea);
    }
  }

  /**
   * Generalize by taking account of lower levels of detail.
   * @param ftCol
   */
  protected void generalizeAware(ScaleLine line, ScaleMasterElement elem,
      Class<?> classObj, IPopulation<IGeneObj> pop) {

    // Get the generalized population
    IPopulation<IGeneObj> featuresOut = new Population<IGeneObj>();
    for (IGeneObj obj : pop) {
      if (classObj.isInstance(obj) && (!obj.isEliminated())) {
        featuresOut.add(obj);
      }
    }

    // Get the elements with lower level of details
    ScaleMasterElement elemSup = null;
    for (ScaleMasterElement element : line.getAllElements()) {
      if (!element.getDbName().equals(elem.getDbName())) {
        if (element.getInterval().getMinimum() >= elem.getInterval()
            .getMaximum()) {
          elemSup = element;
        }
      }
    }

    // Get the population with lower level of details
    CartAGenDataSet datasetSup = CartAGenDocOld.getInstance().getDataset(
        elemSup.getDbName());
    IPopulation<IGeneObj> popSup = datasetSup.getCartagenPop(datasetSup
        .getPopNameFromClass(classObj));

    // cancel the elimination of objects present in the database with lower
    // level of detail
    for (IFeature ftSup : popSup) {
      String idappSup = ftSup.getAttribute("idapp").toString();
      if (!(idappSup.equals("0"))) {
        boolean deleted = true;
        for (IFeature ftOut : featuresOut) {
          String idappOut = ftOut.getAttribute("idapp").toString();
          if (idappOut == idappSup) {
            deleted = false;
          }
        }
        if (deleted == true) {
          for (IGeneObj obj : pop) {
            String idappInf = obj.getAttribute("idapp").toString();
            if (idappInf.equals(idappSup)) {
              obj.cancelElimination();
              // featuresOut.add(obj);
            }
          }
        }
      }
    }
  }

  private String filterToString(Filter filter) {
    String strAff = "";
    if (filter != null) {
      if (filter instanceof UnaryLogicOpsType) {
        try {
          strAff = ((UnaryLogicOpsType) filter).toString();
        } catch (Exception e) {
          // display nothing
        }
      }
      if (filter instanceof BinaryLogicOpsType) {
        try {
          strAff = ((BinaryLogicOpsType) filter).toString();
        } catch (Exception e) {
          // display nothing
        }
      }
    }
    return strAff;
  }

  public void setExportFolder(String exportFolder) {
    this.exportFolder = exportFolder;
  }

  public String getExportFolder() {
    return this.exportFolder;
  }

  public String getMgcpPlusPlusFolder() {
    return this.mgcpPlusPlusFolder;
  }

  public void setMgcpPlusPlusFolder(String mgcpPlusPlusFolder) {
    this.mgcpPlusPlusFolder = mgcpPlusPlusFolder;
  }

  public List<String> getListLayersMgcpPlusPlus() {
    return this.listLayersMgcpPlusPlus;
  }

  public void setListLayersMgcpPlusPlus(List<String> listLayersMgcpPlusPlus) {
    this.listLayersMgcpPlusPlus = listLayersMgcpPlusPlus;
  }

  public Set<ScaleMasterPreProcess> getAvailablePreProcesses() {
    return availablePreProcesses;
  }

  public void setAvailablePreProcesses(
      Set<ScaleMasterPreProcess> availablePreProcesses) {
    this.availablePreProcesses = availablePreProcesses;
  }

  public String getVmap1PlusPlusFolder() {
    return vmap1PlusPlusFolder;
  }

  public void setVmap1PlusPlusFolder(String vmap1PlusPlusFolder) {
    this.vmap1PlusPlusFolder = vmap1PlusPlusFolder;
  }

  public List<String> getListLayersVmap1PlusPlus() {
    return listLayersVmap1PlusPlus;
  }

  public void setListLayersVmap1PlusPlus(List<String> listLayersVmap1PlusPlus) {
    this.listLayersVmap1PlusPlus = listLayersVmap1PlusPlus;
  }

  public Map<IFeatureCollection<IFeature>, Map<String, Double>> getMapLanduseParamIn() {
    return mapLanduseParamIn;
  }

  public void setMapLanduseParamIn(
      Map<IFeatureCollection<IFeature>, Map<String, Double>> mapLanduseParamIn) {
    this.mapLanduseParamIn = mapLanduseParamIn;
  }

  public Double getLanduseDpFilter() {
    return landuseDpFilter;
  }

  public void setLanduseDpFilter(Double landuseDpFilter) {
    this.landuseDpFilter = landuseDpFilter;
  }

  public String getShomFolder() {
    return shomFolder;
  }

  public void setShomFolder(String shomFolder) {
    this.shomFolder = shomFolder;
  }

  public List<String> getListLayersShom() {
    return listLayersShom;
  }

  public void setListLayersShom(List<String> listLayersShom) {
    this.listLayersShom = listLayersShom;
  }

  public double getPartitionSize() {
    return partitionSize;
  }

  public void setPartitionSize(double partitionSize) {
    this.partitionSize = partitionSize;
  }

  public boolean isAware() {
    return isAware;
  }

  public void setAware(boolean isAware) {
    this.isAware = isAware;
  }

  public Map<String, String> getLanduseReclass() {
    return landuseReclass;
  }

  public void setLanduseReclass(Map<String, String> landuseReclass) {
    this.landuseReclass = landuseReclass;
  }

}
