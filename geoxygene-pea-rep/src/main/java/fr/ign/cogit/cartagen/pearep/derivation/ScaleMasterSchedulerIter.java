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
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import fr.ign.cogit.cartagen.pearep.importexport.MGCPLoader;
import fr.ign.cogit.cartagen.pearep.importexport.ShapeFileExport;
import fr.ign.cogit.cartagen.pearep.importexport.VMAP1PlusPlusLoader;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.filter.BinaryLogicOpsType;
import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.filter.UnaryLogicOpsType;

public class ScaleMasterSchedulerIter extends ScaleMasterScheduler {

  private Logger logger = Logger.getLogger(ScaleMasterSchedulerIter.class
      .getName());
  public Logger traceLogger = Logger.getLogger("PeaRep.trace.scheduler");
  public Logger errorLogger = Logger.getLogger("PeaRep.error.scheduler");

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
  public ScaleMasterSchedulerIter(File scaleMasterXml, File parameterXml,
      File themesFile) throws ParserConfigurationException, SAXException,
      IOException, DOMException, ClassNotFoundException {
    super(scaleMasterXml, parameterXml, themesFile);
  }

  /**
   * A constructor with the Java object parameters already built.
   * @param scaleMaster
   * @param scale
   */
  public ScaleMasterSchedulerIter(ScaleMaster scaleMaster,
      Set<ScaleMasterTheme> themes, int scale) {
    super(scaleMaster, themes, scale);
  }

  public void generaliseIter(MGCPLoader mgcpLoad,
      VMAP1PlusPlusLoader vmap1Load, String jarPath) throws Exception {

    // loop on the lines of the ScaleMaster
    for (ScaleLine line : this.getScaleMaster().getScaleLines()) {
      // get the element corresponding to final scale
      ScaleMasterElement elem = line.getElementFromScale(this.getScale());
      // case with a scale master without rule for the current final scale and
      // current line. No need to delete features as the export of this line
      // will be skipped.
      if (elem == null) {
        continue;
      }
      this.logger.fine(elem.toString());
      this.traceLogger.info("début de la généralisation du thème "
          + line.getTheme());

      // test if the element relates to an existing database
      if (!CartAGenDocOld.getInstance().getDatabases().keySet()
          .contains(elem.getDbName()))
        continue;
      // get the dataset related to the element
      CartAGenDataSet dataset = CartAGenDocOld.getInstance().getDataset(
          elem.getDbName());
      CartAGenDocOld.getInstance().setCurrentDataset(dataset);

      // load data
      Class<?> classObj = elem.getClasses().iterator().next();
      if (dataset.getCartAGenDB().getSourceDLM().equals(SourceDLM.MGCPPlusPlus)) {
        mgcpLoad.loadData(new File(this.getMgcpPlusPlusFolder()),
            this.getListLayersMgcpPlusPlus(), classObj);
      } else if (dataset.getCartAGenDB().getSourceDLM()
          .equals(SourceDLM.VMAP1PlusPlus)) {
        vmap1Load.loadData(new File(this.getMgcpPlusPlusFolder()),
            this.getListLayersMgcpPlusPlus(), classObj);
      }

      // get the corresponding feature population
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
      List<OrderedProcess> procList = this.orderProcesses(elem, features);

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
        this.traceLogger.info("Application du processus " + procName
            + " avec comme parametres " + parameters + " sur "
            + elem.getClasses() + " de " + elem.getDbName());
        // execute the process on the features
        try {
          process.execute(features, dataset);
          // update features
          for (IGeneObj objPop : dataset.getCartagenPop(dataset
              .getPopNameFromClass(classObj))) {
            if (!features.contains(objPop))
              features.add(objPop);
          }
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, e.getStackTrace());
        }

      }
      // export data
      String exportPath = this.getExportFolder();
      if (exportPath == null) {
        exportPath = jarPath;
      }

      ShapeFileExport exportTool = new ShapeFileExport(new File(exportPath),
          dataset, this.getScaleMaster(), this.getScale());
      exportTool.exportToShapefiles(line);

      // clear dataset
      pop.clear();
    }

    // Trigger the multi-line processes if necessary
    // get the element corresponding to final scale
    if (this.getScaleMaster().getMultiLine() != null) {
      for (ScaleMasterMultiElement elem : this.getScaleMaster().getMultiLine()
          .getElementsFromScale(this.getScale())) {
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

}
