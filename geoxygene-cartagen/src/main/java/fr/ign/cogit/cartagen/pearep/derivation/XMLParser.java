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
import fr.ign.cogit.cartagen.mrdb.scalemaster.GeometryType;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMaster;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterTheme;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterXMLParser;
import fr.ign.cogit.cartagen.pearep.enrichment.ScaleMasterPreProcess;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;

public class XMLParser {

  private File xmlFile;
  private int scale;
  private String exportFolder;
  private Map<String, String> mapPath;
  private Map<String, List<String>> mapLayers;

  public XMLParser(File xmlFile) {
    super();
    this.xmlFile = xmlFile;
    this.mapLayers = new HashMap<String, List<String>>();
    this.mapPath = new HashMap<String, String>();
  }

  /**
   * Parse the XML File to produce a {@link ScaleMaster} instance, if the file
   * has the correct structure.
   * @return
   */
  public ScaleMaster parseScaleMaster(ScaleMasterScheduler scheduler)
      throws ParserConfigurationException, SAXException, IOException,
      DOMException, ClassNotFoundException {

    return new ScaleMasterXMLParser(xmlFile).parseScaleMaster(scheduler
        .getThemes());
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
    if (scheduler != null)
      scheduler.setScale(Integer.valueOf(scaleElem.getChildNodes().item(0)
          .getNodeValue()));
    else
      this.scale = Integer.valueOf(scaleElem.getChildNodes().item(0)
          .getNodeValue());

    // get the export folder
    if (root.getElementsByTagName("dossier-export").getLength() != 0) {
      Element exportElem = (Element) root
          .getElementsByTagName("dossier-export").item(0);
      if (scheduler != null)
        scheduler.setExportFolder(exportElem.getChildNodes().item(0)
            .getNodeValue());
      else
        this.exportFolder = exportElem.getChildNodes().item(0).getNodeValue();
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
        if (scheduler != null)
          scheduler.setMgcpPlusPlusFolder(folderElem.getChildNodes().item(0)
              .getNodeValue());
        else
          this.mapPath.put(nom, folderElem.getChildNodes().item(0)
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
        if (scheduler != null)
          scheduler.setListLayersMgcpPlusPlus(listLayer);
        else
          this.mapLayers.put(nom, listLayer);
      }

      else if (nom.equals("VMAP1PlusPlus")) {
        Element folderElem = (Element) bdElem.getElementsByTagName("chemin")
            .item(0);
        scheduler.setVmap1PlusPlusFolder(folderElem.getChildNodes().item(0)
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
        scheduler.setListLayersVmap1PlusPlus(listLayer);
      }
    }

    // parse data corrections
    for (int i = 0; i < root.getElementsByTagName("pre-traitement").getLength(); i++) {
      Element correctionElement = (Element) root.getElementsByTagName(
          "pre-traitement").item(i);
      Element nomElem = (Element) correctionElement.getElementsByTagName(
          "nom-processus").item(0);
      String nom = nomElem.getChildNodes().item(0).getNodeValue();
      ScaleMasterPreProcess process = scheduler.getPreProcessFromName(nom);
      if (process == null)
        continue;
      Element dbElem = (Element) correctionElement.getElementsByTagName(
          "base-de-donnees").item(0);
      String nomBD = dbElem.getChildNodes().item(0).getNodeValue();
      Set<ScaleMasterTheme> themes = new HashSet<ScaleMasterTheme>();
      for (int i1 = 0; i1 < correctionElement.getElementsByTagName("theme")
          .getLength(); i1++) {
        Element themeElem = (Element) root.getElementsByTagName("theme").item(
            i1);
        themes.add(scheduler.getThemeFromName(themeElem.getChildNodes().item(0)
            .getNodeValue()));
      }
      scheduler.getCorrections().add(
          new DataCorrection(process, themes, SourceDLM.valueOf(nomBD)));
    }
  }

  /**
   * Parse the xml file that contains existing themes and put them into the
   * parameter collection.
   * @param existingThemes
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   * @throws ClassNotFoundException
   */
  @SuppressWarnings("unchecked")
  public Set<ScaleMasterTheme> parseExistingThemes()
      throws ParserConfigurationException, SAXException, IOException,
      ClassNotFoundException {
    Set<ScaleMasterTheme> existingThemes = new HashSet<ScaleMasterTheme>();
    // open the xml file
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    org.w3c.dom.Document doc;
    doc = db.parse(this.xmlFile);
    doc.getDocumentElement().normalize();
    // get the root of the XML document
    Element root = (Element) doc.getElementsByTagName("scale-master-theme")
        .item(0);
    for (int i = 0; i < root.getElementsByTagName("theme").getLength(); i++) {
      Element themeElem = (Element) root.getElementsByTagName("theme").item(i);
      Element nameElem = (Element) themeElem.getElementsByTagName("name").item(
          0);
      String name = nameElem.getChildNodes().item(0).getNodeValue();
      Element conceptElem = (Element) themeElem.getElementsByTagName("concept")
          .item(0);
      @SuppressWarnings("unused")
      String conceptName = conceptElem.getChildNodes().item(0).getNodeValue();
      // TODO add the ontology
      Element descrElem = (Element) themeElem.getElementsByTagName(
          "description").item(0);
      String description = descrElem.getChildNodes().item(0).getNodeValue();
      Element geomElem = (Element) themeElem.getElementsByTagName(
          "geometry-type").item(0);
      GeometryType geometryType = GeometryType.valueOf(geomElem.getChildNodes()
          .item(0).getNodeValue());
      Set<Class<? extends IGeneObj>> relatedClasses = new HashSet<Class<? extends IGeneObj>>();
      Element classesElem = (Element) themeElem.getElementsByTagName(
          "cartagen-classes").item(0);
      for (int j = 0; j < classesElem.getElementsByTagName("class").getLength(); j++) {
        Element classElem = (Element) classesElem.getElementsByTagName("class")
            .item(j);
        String className = classElem.getChildNodes().item(0).getNodeValue();
        relatedClasses
            .add((Class<? extends IGeneObj>) Class.forName(className));
      }
      ScaleMasterTheme theme = new ScaleMasterTheme(name, relatedClasses,
          geometryType);
      theme.setDescription(description);
      existingThemes.add(theme);
    }

    return existingThemes;
  }

  public int getScale() {
    return scale;
  }

  public void setScale(int scale) {
    this.scale = scale;
  }

  public String getExportFolder() {
    return exportFolder;
  }

  public void setExportFolder(String exportFolder) {
    this.exportFolder = exportFolder;
  }

  public Map<String, String> getMapPath() {
    return mapPath;
  }

  public void setMapPath(Map<String, String> mapPath) {
    this.mapPath = mapPath;
  }

  public Map<String, List<String>> getMapLayers() {
    return mapLayers;
  }

  public void setMapLayers(Map<String, List<String>> mapLayers) {
    this.mapLayers = mapLayers;
  }

}
