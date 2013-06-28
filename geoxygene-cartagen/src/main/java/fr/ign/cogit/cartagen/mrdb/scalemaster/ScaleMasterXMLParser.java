/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.mrdb.scalemaster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.REPPointOfView;
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

public class ScaleMasterXMLParser {

  private File xmlFile;

  public ScaleMasterXMLParser(File xmlFile) {
    super();
    this.xmlFile = xmlFile;
  }

  /**
   * Parse the XML File to produce a {@link ScaleMaster} instance, if the file
   * has the correct structure.
   * @return
   */
  public ScaleMaster parseScaleMaster(Collection<ScaleMasterTheme> themes)
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

    // the scale master name
    if (root.getElementsByTagName("name").getLength() != 0) {
      Element nameElem = (Element) root.getElementsByTagName("name").item(0);
      scaleMaster.setName(nameElem.getChildNodes().item(0).getNodeValue());
    }

    // the scale master point of view
    Element ptOfViewElem = (Element) root.getElementsByTagName("point-of-view")
        .item(0);
    scaleMaster.setPointOfView(REPPointOfView.valueOf(ptOfViewElem
        .getChildNodes().item(0).getNodeValue()));

    // the scale master global range
    Element globalRangeElement = (Element) root.getElementsByTagName(
        "global-range").item(0);
    Element globalRangeMin = (Element) globalRangeElement.getElementsByTagName(
        "interval-min").item(0);
    Element globalRangeMax = (Element) globalRangeElement.getElementsByTagName(
        "interval-max").item(0);
    Interval<Integer> globalRange = new Interval<Integer>(
        Integer.valueOf(globalRangeMin.getChildNodes().item(0).getNodeValue()),
        Integer.valueOf(globalRangeMax.getChildNodes().item(0).getNodeValue()));
    scaleMaster.setGlobalRange(globalRange);

    // get ScaleMaster elements
    // get scale-lines
    for (int itScaleLine = 0; itScaleLine < root.getElementsByTagName(
        "scale-line").getLength(); itScaleLine++) {
      Element scaleLineElement = (Element) root.getElementsByTagName(
          "scale-line").item(itScaleLine);

      ScaleMasterTheme theme = null;
      for (ScaleMasterTheme th : themes) {
        if (scaleLineElement.getAttribute("theme").equals(th.getName()))
          theme = th;
      }
      ScaleLine scaleLine = new ScaleLine(scaleMaster, theme);

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

        Interval<Integer> interval = new Interval<Integer>(Double.valueOf(
            intervalMin.getChildNodes().item(0).getNodeValue()).intValue(),
            Double.valueOf(intervalMax.getChildNodes().item(0).getNodeValue())
                .intValue());

        ScaleMasterElement scaleMasterElement = new ScaleMasterElement(null,
            interval, dbName.getChildNodes().item(0).getNodeValue());

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
                if (parameterElement.getAttribute("type").equals("String")) {
                  valeur = parameterElement.getChildNodes().item(0)
                      .getNodeValue();
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
                ogcPropertyComparison = ScaleMasterXMLParser
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
                listFilter.add(ScaleMasterXMLParser
                    .getPropertyComparison(childElement));
              }
            }
            ogcLogicComparison.setOps(listFilter);
            scaleMasterElement.setOgcFilter(ogcLogicComparison);
          }
          scaleMasterElement.setFilterPriority(ProcessPriority.values()[Integer
              .valueOf(attributeSelection.getAttribute("priority"))]);
        }
        scaleLine.addElement(scaleMasterElement);
      }

      scaleMaster.getScaleLines().add(scaleLine);

      // now parse the multi-themes line
      Element multiElem = (Element) root.getElementsByTagName(
          "multi-themes-processes").item(0);
      if (multiElem != null) {
        Map<String, List<ScaleMasterMultiElement>> line = new HashMap<String, List<ScaleMasterMultiElement>>();
        for (int itProcess = 0; itProcess < multiElem.getElementsByTagName(
            "multi-themes-process").getLength(); itProcess++) {
          Element procElem = (Element) multiElem.getElementsByTagName(
              "multi-themes-process").item(itProcess);
          Element nameElem = (Element) procElem.getElementsByTagName("name")
              .item(0);
          String processName = nameElem.getChildNodes().item(0).getNodeValue();
          List<ScaleMasterMultiElement> elements = new ArrayList<ScaleMasterMultiElement>();
          for (int itElem = 0; itElem < procElem.getElementsByTagName(
              "multi-themes-element").getLength(); itElem++) {
            Element elemElem = (Element) procElem.getElementsByTagName(
                "multi-themes-element").item(itElem);
            Element intervalMin = (Element) elemElem.getElementsByTagName(
                "interval-min").item(0);
            Element intervalMax = (Element) elemElem.getElementsByTagName(
                "interval-max").item(0);
            Element dbNameElem = (Element) elemElem.getElementsByTagName(
                "db-name").item(0);
            String dbName = dbNameElem.getChildNodes().item(0).getNodeValue();
            Set<ScaleMasterTheme> themesSet = new HashSet<ScaleMasterTheme>();
            for (int itTheme = 0; itTheme < elemElem.getElementsByTagName(
                "theme").getLength(); itTheme++) {
              Element themeElem = (Element) elemElem.getElementsByTagName(
                  "theme").item(itTheme);
              String themeName = themeElem.getChildNodes().item(0)
                  .getNodeValue();
              for (ScaleMasterTheme th : themes) {
                if (th.getName().equals(themeName)) {
                  themesSet.add(th);
                  break;
                }
              }
            }

            // parse the element parameters
            Set<MultiThemeParameter> params = new HashSet<MultiThemeParameter>();
            Element paramsElement = (Element) elemElem.getElementsByTagName(
                "params").item(0);
            for (int itParameter = 0; itParameter < paramsElement
                .getElementsByTagName("parameter").getLength(); itParameter++) {
              Element parameterElement = (Element) paramsElement
                  .getElementsByTagName("parameter").item(itParameter);
              Object value = null;
              String type = parameterElement.getAttribute("type");
              Class<?> typeClass = null;
              if (type.equals("Double")) {
                value = Double.valueOf(parameterElement.getChildNodes().item(0)
                    .getNodeValue());
                typeClass = Double.class;
              }
              if (type.equals("Integer")) {
                value = Integer.valueOf(parameterElement.getChildNodes()
                    .item(0).getNodeValue());
                typeClass = Integer.class;
              }
              if (type.equals("Boolean")) {
                value = Boolean.valueOf(parameterElement.getChildNodes()
                    .item(0).getNodeValue());
                typeClass = Boolean.class;
              }
              if (type.equals("String")) {
                value = parameterElement.getChildNodes().item(0).getNodeValue();
                typeClass = String.class;
              }
              String theme1 = parameterElement.getAttribute("theme1");
              String theme2 = parameterElement.getAttribute("theme2");
              String name = parameterElement.getAttribute("name");
              params.add(new MultiThemeParameter(name, typeClass, value,
                  theme1, theme2));
            }
            // compute the interval of the element
            Interval<Integer> interval = new Interval<Integer>(Double.valueOf(
                intervalMin.getChildNodes().item(0).getNodeValue()).intValue(),
                Double.valueOf(
                    intervalMax.getChildNodes().item(0).getNodeValue())
                    .intValue());
            // create the element object
            ScaleMasterMultiElement element = new ScaleMasterMultiElement(
                dbName, processName, params, interval, themesSet);
            elements.add(element);
          }
          line.put(processName, elements);
        }
      }
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

  public static String getPropertyComparisonElementName(
      BinaryComparisonOpsType filter) {
    if (filter instanceof PropertyIsEqualTo)
      return "ogc:PropertyIsEqualTo";
    if (filter instanceof PropertyIsGreaterThan)
      return "ogc:PropertyIsGreaterThan";
    if (filter instanceof PropertyIsGreaterThanOrEqualTo)
      return "ogc:PropertyIsGreaterThanOrEqualTo";
    if (filter instanceof PropertyIsLessThan)
      return "ogc:PropertyIsLessThan";
    if (filter instanceof PropertyIsLessThanOrEqualTo)
      return "ogc:PropertyIsLessThanOrEqualTo";
    if (filter instanceof PropertyIsNotEqualTo)
      return "ogc:PropertyIsNotEqualTo";
    if (filter instanceof PropertyIsNull)
      return "ogc:PropertyIsNull";
    if (filter instanceof PropertyIsLike)
      return "ogc:PropertyIsLike";
    return null;
  }

  public static String getBinaryLogicOpsElementName(BinaryLogicOpsType filter) {
    if (filter instanceof And)
      return "ogc:And";
    if (filter instanceof Or)
      return "ogc:Or";

    return null;
  }

  public static void writeSimpleFilter(BinaryComparisonOpsType filter,
      Element root, Document xmlDoc) {
    Node n = null;
    String elemName = getPropertyComparisonElementName(filter);
    Element filterElem = xmlDoc.createElement(elemName);
    root.appendChild(filterElem);
    Element propElem = xmlDoc.createElement("ogc:PropertyName");
    n = xmlDoc.createTextNode((filter).getPropertyName().getPropertyName());
    propElem.appendChild(n);
    filterElem.appendChild(propElem);
    Element litElem = xmlDoc.createElement("ogc:Literal");
    n = xmlDoc.createTextNode((filter).getLiteral().getValue());
    litElem.appendChild(n);
    filterElem.appendChild(litElem);
  }

  public static void writeComplexFilter(BinaryLogicOpsType filter,
      Element root, Document xmlDoc) {
    String elemName = getBinaryLogicOpsElementName(filter);
    Element filterElem = xmlDoc.createElement(elemName);
    root.appendChild(filterElem);

    for (Filter subFilter : filter.getOps()) {
      if (subFilter instanceof BinaryComparisonOpsType)
        writeSimpleFilter((BinaryComparisonOpsType) subFilter, filterElem,
            xmlDoc);
      else if (subFilter instanceof BinaryLogicOpsType)
        writeComplexFilter((BinaryLogicOpsType) subFilter, filterElem, xmlDoc);
    }
  }

  @SuppressWarnings("unchecked")
  public Set<ScaleMasterTheme> parseScaleMasterThemes(File themeFile)
      throws ParserConfigurationException, SAXException, IOException,
      DOMException, ClassNotFoundException {
    Set<ScaleMasterTheme> themes = new HashSet<ScaleMasterTheme>();

    // open the xml file
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;

    db = dbf.newDocumentBuilder();
    org.w3c.dom.Document doc;
    doc = db.parse(themeFile);
    doc.getDocumentElement().normalize();
    // get the root of the XML document
    Element root = (Element) doc.getElementsByTagName("scale-master-theme")
        .item(0);

    // loop on the themes
    for (int i = 0; i < root.getElementsByTagName("theme").getLength(); i++) {
      Element themeElem = (Element) root.getElementsByTagName("theme").item(i);
      Element nameElem = (Element) themeElem.getElementsByTagName("name").item(
          0);
      String name = nameElem.getChildNodes().item(0).getNodeValue();
      Element descrElem = (Element) themeElem.getElementsByTagName(
          "description").item(0);
      String descr = descrElem.getChildNodes().item(0).getNodeValue();
      Element conceptElem = (Element) themeElem.getElementsByTagName("concept")
          .item(0);
      String conceptName = conceptElem.getChildNodes().item(0).getNodeValue();
      // TODO use the ontology
      Element geomTypeElem = (Element) themeElem.getElementsByTagName(
          "geometry-type").item(0);
      GeometryType geomType = GeometryType.valueOf(geomTypeElem.getChildNodes()
          .item(0).getNodeValue());
      Set<Class<? extends IGeneObj>> cartagenClasses = new HashSet<Class<? extends IGeneObj>>();
      Element classesElem = (Element) themeElem.getElementsByTagName(
          "cartagen-classes").item(0);
      for (int j = 0; j < classesElem.getElementsByTagName("class").getLength(); j++) {
        Element classElem = (Element) themeElem.getElementsByTagName("class")
            .item(j);
        String className = classElem.getChildNodes().item(0).getNodeValue();
        cartagenClasses.add((Class<? extends IGeneObj>) Class
            .forName(className));
      }
      // build the theme
      ScaleMasterTheme theme = new ScaleMasterTheme(name, cartagenClasses,
          geomType);
      theme.setDescription(descr);
      themes.add(theme);
    }

    return themes;
  }
}
