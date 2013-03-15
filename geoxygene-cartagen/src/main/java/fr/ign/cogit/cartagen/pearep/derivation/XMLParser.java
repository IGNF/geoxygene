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
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMaster;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterXMLParser;

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
