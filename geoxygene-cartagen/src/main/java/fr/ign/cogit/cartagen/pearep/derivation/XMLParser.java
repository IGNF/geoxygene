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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.REPPointOfView;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleLine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMaster;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterElement;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterElement.ProcessPriority;
import fr.ign.cogit.cartagen.util.Interval;
import fr.ign.cogit.geoxygene.filter.And;
import fr.ign.cogit.geoxygene.filter.BinaryComparisonOpsType;
import fr.ign.cogit.geoxygene.filter.BinaryLogicOpsType;
import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.filter.Or;
import fr.ign.cogit.geoxygene.filter.PropertyIsEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsGreaterThan;
import fr.ign.cogit.geoxygene.filter.PropertyIsGreaterThanOrEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsLessThan;
import fr.ign.cogit.geoxygene.filter.PropertyIsLessThanOrEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsLike;
import fr.ign.cogit.geoxygene.filter.PropertyIsNotEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsNull;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;

public class XMLParser {

  private File xmlFile;

  public XMLParser(File xmlFile) {
    super();
    this.xmlFile = xmlFile;
  }

  /**
   * Parse the XML File to produce a {@link ScaleMaster} instance, if the file
   * has the correct structure.
   * @return
   */
  @SuppressWarnings("unchecked")
  public ScaleMaster parseScaleMaster(ScaleMasterScheduler scheduler)
      throws ParserConfigurationException, SAXException, IOException,
      DOMException, ClassNotFoundException {

    ScaleMaster scaleMaster = new ScaleMaster();

    // open the xml file
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;

    db = dbf.newDocumentBuilder();
    org.w3c.dom.Document doc;
    doc = db.parse(this.xmlFile);
    doc.getDocumentElement().normalize();
    // get the root of the XML document
    Element root = (Element) doc.getElementsByTagName("pearep-scalemaster")
        .item(0);

    // the scale master point of view
    Element ptOfViewElem = (Element) root.getElementsByTagName("point-of-view")
        .item(0);
    scaleMaster.setPointOfView(REPPointOfView.valueOf(ptOfViewElem
        .getChildNodes().item(0).getNodeValue()));

    // get ScaleMaster elements
    // get scale-lines
    for (int itScaleLine = 0; itScaleLine < root.getElementsByTagName(
        "scale-line").getLength(); itScaleLine++) {
      Element scaleLineElement = (Element) root.getElementsByTagName(
          "scale-line").item(itScaleLine);

      Map<Interval<Integer>, List<ScaleMasterElement>> mapScaleMasterElements = new HashMap<Interval<Integer>, List<ScaleMasterElement>>();

      // get scale-intervals
      for (int itScaleInterval = 0; itScaleInterval < scaleLineElement
          .getElementsByTagName("scale-interval").getLength(); itScaleInterval++) {

        // get scale-interval informations
        Element scaleIntervalElement = (Element) scaleLineElement
            .getElementsByTagName("scale-interval").item(itScaleInterval);
        Element intervalMin = (Element) scaleIntervalElement
            .getElementsByTagName("interval-min").item(0);
        Element intervalMax = (Element) scaleIntervalElement
            .getElementsByTagName("interval-max").item(0);
        Element dbName = (Element) scaleIntervalElement.getElementsByTagName(
            "db-name").item(0);
        Element className = (Element) scaleIntervalElement
            .getElementsByTagName("class-name").item(0);

        Interval<Integer> interval = new Interval<Integer>(
            Integer.valueOf(intervalMin.getChildNodes().item(0).getNodeValue()),
            Integer.valueOf(intervalMax.getChildNodes().item(0).getNodeValue()));
        List<ScaleMasterElement> listScaleMasterElements = new ArrayList<ScaleMasterElement>();
        mapScaleMasterElements.put(interval, listScaleMasterElements);

        ScaleMasterElement scaleMasterElement = new ScaleMasterElement(null,
            interval, dbName.getChildNodes().item(0).getNodeValue());
        listScaleMasterElements.add(scaleMasterElement);

        Set<Class<? extends IGeneObj>> classes = new HashSet<Class<? extends IGeneObj>>();
        classes.add((Class<? extends IGeneObj>) Class.forName(className
            .getChildNodes().item(0).getNodeValue()));

        scaleMasterElement.setClasses(classes);

        List<String> listProcessesToApply = new ArrayList<String>();
        scaleMasterElement.setProcessesToApply(listProcessesToApply);

        List<Map<String, Object>> listProcessParameters = new ArrayList<Map<String, Object>>();
        scaleMasterElement.setParameters(listProcessParameters);

        List<ProcessPriority> listProcessPriorities = new ArrayList<ProcessPriority>();
        scaleMasterElement.setProcessPriorities(listProcessPriorities);

        // get generalisation-processes
        if (scaleIntervalElement.getElementsByTagName(
            "generalisation-processes").getLength() == 0) {
        } else {
          Element generalisationProcesses = (Element) scaleIntervalElement
              .getElementsByTagName("generalisation-processes").item(0);

          for (int itProcess = 0; itProcess < generalisationProcesses
              .getElementsByTagName("process").getLength(); itProcess++) {
            Element process = (Element) generalisationProcesses
                .getElementsByTagName("process").item(itProcess);
            Element processName = (Element) process
                .getElementsByTagName("name").item(0);

            listProcessesToApply.add(processName.getChildNodes().item(0)
                .getNodeValue());

            listProcessPriorities.add(ProcessPriority.values()[Integer
                .valueOf(process.getAttribute("priority"))]);

            Map<String, Object> mapParamProcess = new HashMap<String, Object>();

            if (process.getElementsByTagName("params").getLength() == 0) {
              listProcessParameters.add(mapParamProcess);
            } else {
              Element paramsElement = (Element) process.getElementsByTagName(
                  "params").item(0);

              for (int itParameter = 0; itParameter < paramsElement
                  .getElementsByTagName("parameter").getLength(); itParameter++) {
                Element parameterElement = (Element) paramsElement
                    .getElementsByTagName("parameter").item(itParameter);

                Object valeur = null;
                if (parameterElement.getAttribute("type").equals("Double")) {
                  valeur = Double.valueOf(parameterElement.getChildNodes()
                      .item(0).getNodeValue());
                }
                if (parameterElement.getAttribute("type").equals("Integer")) {
                  valeur = Integer.valueOf(parameterElement.getChildNodes()
                      .item(0).getNodeValue());
                }
                if (parameterElement.getAttribute("type").equals("Boolean")) {
                  valeur = Boolean.valueOf(parameterElement.getChildNodes()
                      .item(0).getNodeValue());
                }
                // TODO autres cas à gérer
                mapParamProcess.put(parameterElement.getAttribute("name"),
                    valeur);
              }
              listProcessParameters.add(mapParamProcess);
            }
          }
        }

        // get attribute-selections
        if (!(scaleIntervalElement.getElementsByTagName("attribute-selection")
            .getLength() == 0)) {
          Element attributeSelection = (Element) scaleIntervalElement
              .getElementsByTagName("attribute-selection").item(0);

          BinaryLogicOpsType ogcLogicComparison = null;
          Element ogcLogicElement = null;
          boolean multipleQuery = false;

          // Check if the attribute-selection is simple or complex
          if (attributeSelection.getElementsByTagName("ogc:And").getLength() != 0) {
            multipleQuery = true;
            ogcLogicComparison = new And();
            ogcLogicElement = (Element) attributeSelection
                .getElementsByTagName("ogc:And").item(0);
          } else if (attributeSelection.getElementsByTagName("ogc:Or")
              .getLength() != 0) {
            multipleQuery = true;
            ogcLogicComparison = new Or();
            ogcLogicElement = (Element) attributeSelection
                .getElementsByTagName("ogc:Or").item(0);
          }

          // for simple queries
          if (multipleQuery == false) {
            BinaryComparisonOpsType ogcPropertyComparison = null;
            for (int i = 0; i < attributeSelection.getChildNodes().getLength(); i++) {
              if (attributeSelection.getChildNodes().item(i) instanceof Element) {
                Element childElement = (Element) attributeSelection
                    .getChildNodes().item(i);
                ogcPropertyComparison = XMLParser
                    .getPropertyComparison(childElement);
              }
            }
            scaleMasterElement.setOgcFilter(ogcPropertyComparison);
          }

          // for multiple queries
          if (ogcLogicComparison != null && ogcLogicElement != null
              && multipleQuery == true) {
            List<Filter> listFilter = new ArrayList<Filter>();
            for (int i = 0; i < ogcLogicElement.getChildNodes().getLength(); i++) {
              if (ogcLogicElement.getChildNodes().item(i) instanceof Element) {
                Element childElement = (Element) ogcLogicElement
                    .getChildNodes().item(i);
                listFilter.add(XMLParser.getPropertyComparison(childElement));
              }
            }
            ogcLogicComparison.setOps(listFilter);
            scaleMasterElement.setOgcFilter(ogcLogicComparison);
          }
          scaleMasterElement.setFilterPriority(ProcessPriority.values()[Integer
              .valueOf(attributeSelection.getAttribute("priority"))]);
        }
      }
      ScaleLine scaleLine = new ScaleLine(
          scheduler.getThemeFromName(scaleLineElement.getAttribute("theme")),
          scaleMaster, mapScaleMasterElements);

      scaleMaster.getScaleLines().add(scaleLine);
    }
    return scaleMaster;
  }

  private static BinaryComparisonOpsType getPropertyComparison(
      Element comparisonElement) {

    BinaryComparisonOpsType ogcPropertyComparison = null;

    if (comparisonElement.getTagName().equals("ogc:PropertyIsEqualTo")) {
      ogcPropertyComparison = new PropertyIsEqualTo();
    } else if (comparisonElement.getTagName().equals(
        "ogc:PropertyIsGreaterThan")) {
      ogcPropertyComparison = new PropertyIsGreaterThan();
    } else if (comparisonElement.getTagName().equals(
        "ogc:PropertyIsGreaterThanOrEqualTo")) {
      ogcPropertyComparison = new PropertyIsGreaterThanOrEqualTo();
    } else if (comparisonElement.getTagName().equals("ogc:PropertyIsLessThan")) {
      ogcPropertyComparison = new PropertyIsLessThan();
    } else if (comparisonElement.getTagName().equals(
        "ogc:PropertyIsLessThanOrEqualTo")) {
      ogcPropertyComparison = new PropertyIsLessThanOrEqualTo();
    } else if (comparisonElement.getTagName()
        .equals("ogc:PropertyIsNotEqualTo")) {
      ogcPropertyComparison = new PropertyIsNotEqualTo();
    } else if (comparisonElement.getTagName().equals("ogc:PropertyIsNull")) {
      ogcPropertyComparison = new PropertyIsNull();
    } else if (comparisonElement.getTagName().equals("ogc:PropertyIsLike")) {
      ogcPropertyComparison = new PropertyIsLike();
    }

    if (ogcPropertyComparison != null) {
      ogcPropertyComparison.setPropertyName(new PropertyName(comparisonElement
          .getElementsByTagName("ogc:PropertyName").item(0).getChildNodes()
          .item(0).getNodeValue()));
      ogcPropertyComparison.setLiteral(new Literal(comparisonElement
          .getElementsByTagName("ogc:Literal").item(0).getChildNodes().item(0)
          .getNodeValue()));
    }
    return ogcPropertyComparison;
  }

  /**
   * Parse the configuration xml file and fill the scheduler fields.
   * @param scheduler
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  public void parseParameters(ScaleMasterScheduler scheduler)
      throws ParserConfigurationException, SAXException, IOException {
    // open the xml file
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    org.w3c.dom.Document doc;
    doc = db.parse(this.xmlFile);
    doc.getDocumentElement().normalize();
    // get the root of the XML document
    Element root = (Element) doc.getElementsByTagName(
        "PEA-REP-generalisation-DV1-parametres").item(0);

    // get the final scale
    Element scalesElem = (Element) root.getElementsByTagName("echelles-sortie")
        .item(0);
    Element scaleElem = (Element) scalesElem.getElementsByTagName("echelle")
        .item(0);
    scheduler.setScale(Integer.valueOf(scaleElem.getChildNodes().item(0)
        .getNodeValue()));

    // get the export folder
    if (root.getElementsByTagName("dossier-export").getLength() != 0) {
      Element exportElem = (Element) root
          .getElementsByTagName("dossier-export").item(0);
      scheduler.setExportFolder(exportElem.getChildNodes().item(0)
          .getNodeValue());
    }

    // get the databases
    Element bdsElem = (Element) root.getElementsByTagName("BDs-entree").item(0);
    for (int i = 0; i < bdsElem.getElementsByTagName("BD").getLength(); i++) {
      Element bdElem = (Element) bdsElem.getElementsByTagName("BD").item(i);
      Element nomElem = (Element) bdElem.getElementsByTagName("nom").item(0);
      String nom = nomElem.getChildNodes().item(0).getNodeValue();
      if (nom.equals("VMAP2i")) {
        Element folderElem = (Element) bdElem.getElementsByTagName("chemin")
            .item(0);
        scheduler.setVmap2iFolder(folderElem.getChildNodes().item(0)
            .getNodeValue());

        // /Modif
        List<String> listLayer = new ArrayList<String>();
        for (int itLayer = 0; itLayer < bdElem.getElementsByTagName("layer")
            .getLength(); itLayer++) {
          Element layerElement = (Element) bdElem.getElementsByTagName("layer")
              .item(itLayer);
          listLayer.add(layerElement.getChildNodes().item(0).getNodeValue()
              .toString());
        }
        scheduler.setListLayersVmap2i(listLayer);

      } else if (nom.equals("VMAP1")) {
        Element folderElem = (Element) bdElem.getElementsByTagName("chemin")
            .item(0);
        scheduler.setVmap1Folder(folderElem.getChildNodes().item(0)
            .getNodeValue());

        // /Modif
        List<String> listLayer = new ArrayList<String>();
        for (int itLayer = 0; itLayer < bdElem.getElementsByTagName("layer")
            .getLength(); itLayer++) {
          Element layerElement = (Element) bdElem.getElementsByTagName("layer")
              .item(itLayer);
          listLayer.add(layerElement.getChildNodes().item(0).getNodeValue()
              .toString());
        }
        scheduler.setListLayersVmap1(listLayer);

      } else if (nom.equals("VMAP0")) {
        Element folderElem = (Element) bdElem.getElementsByTagName("chemin")
            .item(0);
        scheduler.setVmap0Folder(folderElem.getChildNodes().item(0)
            .getNodeValue());

        // /Modif
        List<String> listLayer = new ArrayList<String>();
        for (int itLayer = 0; itLayer < bdElem.getElementsByTagName("layer")
            .getLength(); itLayer++) {
          Element layerElement = (Element) bdElem.getElementsByTagName("layer")
              .item(itLayer);
          listLayer.add(layerElement.getChildNodes().item(0).getNodeValue()
              .toString());
        }
        scheduler.setListLayersVmap0(listLayer);

      }

      else if (nom.equals("MGCPPlusPlus")) {
        Element folderElem = (Element) bdElem.getElementsByTagName("chemin")
            .item(0);
        scheduler.setMgcpPlusPlusFolder(folderElem.getChildNodes().item(0)
            .getNodeValue());

        // /Modif
        List<String> listLayer = new ArrayList<String>();
        for (int itLayer = 0; itLayer < bdElem.getElementsByTagName("layer")
            .getLength(); itLayer++) {
          Element layerElement = (Element) bdElem.getElementsByTagName("layer")
              .item(itLayer);
          listLayer.add(layerElement.getChildNodes().item(0).getNodeValue()
              .toString());
        }
        scheduler.setListLayersMgcpPlusPlus(listLayer);
      }
    }
  }
}
