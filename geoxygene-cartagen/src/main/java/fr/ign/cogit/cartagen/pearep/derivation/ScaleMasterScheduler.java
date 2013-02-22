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
import java.util.HashSet;
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
import fr.ign.cogit.cartagen.mrdb.scalemaster.GeometryType;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleLine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMaster;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterElement;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterTheme;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPBuildPoint;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPBuiltUpArea;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPContourLine;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPWaterArea;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPWaterLine;
import fr.ign.cogit.cartagen.pearep.mgcp.transport.MGCPRoadLine;
import fr.ign.cogit.cartagen.pearep.vmap.elev.VMAPContourLine;
import fr.ign.cogit.cartagen.pearep.vmap.hydro.VMAPWaterArea;
import fr.ign.cogit.cartagen.pearep.vmap.hydro.VMAPWaterLine;
import fr.ign.cogit.cartagen.pearep.vmap.pop.VMAPBuildPoint;
import fr.ign.cogit.cartagen.pearep.vmap.pop.VMAPBuiltUpArea;
import fr.ign.cogit.cartagen.pearep.vmap.transport.VMAPRoadLine;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.filter.BinaryLogicOpsType;
import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.filter.UnaryLogicOpsType;

public class ScaleMasterScheduler {

  private Logger logger = Logger
      .getLogger(ScaleMasterScheduler.class.getName());
  public Logger traceLogger = Logger.getLogger("PeaRep.trace.scheduler");
  public Logger errorLogger = Logger.getLogger("PeaRep.error.scheduler");

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
  private String vmap2iFolder, vmap1Folder, vmap0Folder, mgcpPlusPlusFolder;
  /**
   * The folder where export shapefiles are generated. If null, export is made
   * in the root.
   */
  private String exportFolder;

  /**
   * Theme that can be used in the scalemasters.
   */
  private Set<ScaleMasterTheme> themes;

  private Set<ScaleMasterGeneProcess> availableProcesses;

  private List<String> listLayersVmap2i;
  private List<String> listLayersVmap1;
  private List<String> listLayersVmap0;
  private List<String> listLayersMgcpPlusPlus;

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
  public ScaleMasterScheduler(File scaleMasterXml, File parameterXml)
      throws ParserConfigurationException, SAXException, IOException,
      DOMException, ClassNotFoundException {
    this.initLoggers();
    this.initThemes();
    this.initProcesses();
    XMLParser smParser = new XMLParser(scaleMasterXml);
    XMLParser paramParser = new XMLParser(parameterXml);
    this.scaleMaster = smParser.parseScaleMaster(this);
    paramParser.parseParameters(this);
  }

  /**
   * A constructor with the Java object parameters already built.
   * @param scaleMaster
   * @param scale
   */
  public ScaleMasterScheduler(ScaleMaster scaleMaster, int scale) {
    this.initThemes();
    this.initProcesses();
    this.scaleMaster = scaleMaster;
    this.scale = scale;
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

  /**
   * Initialise the {@link ScaleMasterTheme} objects known by {@code this}.
   */
  private void initThemes() {
    this.themes = new HashSet<ScaleMasterTheme>();
    this.themes.add(new ScaleMasterTheme("roadl", new Class[] {
        VMAPRoadLine.class, MGCPRoadLine.class }, GeometryType.LINE));
    this.themes.add(new ScaleMasterTheme("builtupa", new Class[] {
        VMAPBuiltUpArea.class, MGCPBuiltUpArea.class }, GeometryType.POLYGON));
    this.themes.add(new ScaleMasterTheme("waterl", new Class[] {
        VMAPWaterLine.class, MGCPWaterLine.class }, GeometryType.LINE));
    this.themes.add(new ScaleMasterTheme("buildingp", new Class[] {
        VMAPBuildPoint.class, MGCPBuildPoint.class }, GeometryType.POINT));
    this.themes.add(new ScaleMasterTheme("contourl", new Class[] {
        VMAPContourLine.class, MGCPContourLine.class }, GeometryType.LINE));
    this.themes.add(new ScaleMasterTheme("lakeresa", new Class[] {
        VMAPWaterArea.class, MGCPWaterArea.class }, GeometryType.POLYGON));
    // this.themes.add(new ScaleMasterTheme("buildinga",
    // new Class[] { MGCPBuilding.class }, GeometryType.POLYGON));
    // TODO
  }

  private void initProcesses() {
    this.availableProcesses = new HashSet<ScaleMasterGeneProcess>();
    this.availableProcesses.add(FilteringProcess.getInstance());
    this.availableProcesses.add(GaussianFilteringProcess.getInstance());
    this.availableProcesses.add(UnionProcess.getInstance());
    this.availableProcesses.add(ContourSelectionProcess.getInstance());
    this.availableProcesses.add(PolygonSimplification.getInstance());
    this.availableProcesses.add(StrokeSelectionProcess.getInstance());
  }

  private void initLoggers() throws SecurityException, IOException {
    for (Handler handler : this.traceLogger.getHandlers()) {
      if (handler instanceof FileHandler) {
        handler = new FileHandler("/trace_" + this.scale + "_"
            + new Date().toString() + ".log", 5000000, 1, true);
      }
    }
    for (Handler handler : this.errorLogger.getHandlers()) {
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
      this.traceLogger.info("début de la généralisation du thème "
          + line.getTheme());

      // get the dataset related to the element
      CartAGenDataSet dataset = CartAGenDoc.getInstance().getDataset(
          elem.getDbName());

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

      // test if there are features for this theme in the dbs
      if (features.size() == 0) {
        continue;
      }

      // orders the processes and filter to apply
      List<OrderedProcess> procList = this.orderProcesses(elem);

      // apply the processes in the priority order
      for (OrderedProcess orderedProc : procList) {
        // test if the current process is the filter
        if (orderedProc.isFilter()) {
          // apply the OGCFilter and mark deleted features
          this.traceLogger.info("Application du filtre "
              + this.filterToString((Filter) orderedProc.getProcess())
              + " sur " + elem.getClasses() + " de " + elem.getDbName());
          this.applyOGCFilter(elem, features);
          continue;
        }

        // apply the process
        // get the generalisation process name
        String procName = (String) orderedProc.getProcess();
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
        this.traceLogger.info("Application du processus " + procName
            + " avec comme parametres " + parameters + " sur "
            + elem.getClasses() + " de " + elem.getDbName());
        // execute the process on the features
        try {
          process.execute(features);
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
  private void applyOGCFilter(ScaleMasterElement elem,
      IPopulation<IGeneObj> features) {
    for (IGeneObj obj : features) {
      if (!elem.getOgcFilter().evaluate(obj)) {
        obj.eliminateBatch();
      }
    }
  }

  private ScaleMasterGeneProcess getProcessFromName(String procName) {
    for (ScaleMasterGeneProcess proc : this.availableProcesses) {
      if (proc.getProcessName().equals(procName)) {
        return proc;
      }
    }
    return null;
  }

  /**
   * Order the processes and the filter to apply according to their priority.
   * @param elem
   * @return
   */
  private List<OrderedProcess> orderProcesses(ScaleMasterElement elem) {
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
    return procList;
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
}
