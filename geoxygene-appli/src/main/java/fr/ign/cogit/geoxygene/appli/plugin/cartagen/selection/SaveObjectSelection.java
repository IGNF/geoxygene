/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.XMLFileFilter;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.XMLUtil;

public class SaveObjectSelection extends AbstractAction {

  /****/
  private static final long serialVersionUID = 1L;
  private GeOxygeneApplication appli;

  @Override
  public void actionPerformed(ActionEvent e) {
    String name = JOptionPane
        .showInputDialog("Enter a name for the current selection");
    JFileChooser fc = new JFileChooser();
    fc.setFileFilter(new XMLFileFilter());
    fc.setName("Choose the XML file to save the selection");
    int returnVal = fc.showSaveDialog(appli.getMainFrame().getGui());
    if (returnVal != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File fic = fc.getSelectedFile();
    try {
      saveSelectionToXml(appli.getMainFrame().getSelectedProjectFrame()
          .getLayerViewPanel().getSelectedFeatures(), name, fic);
    } catch (IOException e1) {
      e1.printStackTrace();
    } catch (SAXException e1) {
      e1.printStackTrace();
    } catch (ParserConfigurationException e1) {
      e1.printStackTrace();
    } catch (TransformerException e1) {
      e1.printStackTrace();
    }
  }

  public SaveObjectSelection(GeOxygeneApplication appli) {
    this.appli = appli;
    putValue(SHORT_DESCRIPTION,
        "Save the selected objects into XML for further re-use");
    putValue(NAME, "Save Object Selection");
  }

  private void saveSelectionToXml(Collection<IFeature> selection, String name,
      File file) throws IOException, SAXException,
      ParserConfigurationException, TransformerException {
    String cartagenDataset = "default";
    if (CartAGenDoc.getInstance().getCurrentDataset() != null)
      cartagenDataset = CartAGenDoc.getInstance().getCurrentDataset()
          .getCartAGenDB().getName();
    // first tests if 'file' is an existing xml file
    if (!file.exists()) {
      Document xmldoc = new DocumentImpl();
      Element root = xmldoc.createElement("object-selection");
      // it's a new file, the complete XML structure has to be written
      addSelectionXML(root, xmldoc, selection, name, cartagenDataset);

      // write in the file
      xmldoc.appendChild(root);
      XMLUtil.writeDocumentToXml(xmldoc, file);
      return;
    }

    // first open the file in order to parse it
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    Document doc;
    doc = db.parse(file);
    doc.getDocumentElement().normalize();

    Element root = (Element) doc.getElementsByTagName("object-selection").item(
        0);

    // add the new object selection in the XML file
    addSelectionXML(root, doc, selection, name, cartagenDataset);

    // on enregistre dans le fichier fic
    XMLUtil.writeDocumentToXml(doc, file);
  }

  private void addSelectionXML(Element root, Document xmldoc,
      Collection<IFeature> objs, String name, String cartagenDataset) {
    // create the element for a new object selection
    Element selElem = xmldoc.createElement("selection");
    root.appendChild(selElem);
    // create the 'name' element and its value
    Element nomElem = xmldoc.createElement("name");
    Node n = xmldoc.createTextNode(name);
    nomElem.appendChild(n);
    selElem.appendChild(nomElem);
    // create the 'cartagen-dataset' element and its value
    Element versionElem = xmldoc.createElement("cartagen-dataset");
    n = xmldoc.createTextNode(cartagenDataset);
    versionElem.appendChild(n);
    selElem.appendChild(versionElem);
    // create the 'objects' element
    Element objsElem = xmldoc.createElement("objects");
    selElem.appendChild(objsElem);
    // loop on the selected objects to store them using their cartagenId
    for (IFeature obj : objs) {
      Element objElem = xmldoc.createElement("object");
      n = xmldoc.createTextNode(String.valueOf(obj.getId()));
      objElem.appendChild(n);
      String layerName = "";
      if (CartAGenDoc.getInstance().getCurrentDataset() == null) {
        for (Layer layer : appli.getMainFrame().getSelectedProjectFrame()
            .getLayers()) {
          if (layer.getFeatureCollection().contains(obj)) {
            layerName = layer.getName();
            break;
          }
        }
      } else {
        layerName = CartAGenDoc.getInstance().getCurrentDataset()
            .getPopNameFromObj(obj);
      }
      objElem.setAttribute("population-name", layerName);
      objsElem.appendChild(objElem);
    }
  }
}
