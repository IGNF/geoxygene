/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.utilities.selection;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.XMLFileFilter;
import fr.ign.cogit.geoxygene.api.feature.IFeature;

public class SaveObjectSelection extends AbstractAction {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public void actionPerformed(ActionEvent e) {
    CartagenApplication appli = CartagenApplication.getInstance();
    String name = JOptionPane
        .showInputDialog("Enter a name for the current selection");
    JFileChooser fc = new JFileChooser();
    fc.setFileFilter(new XMLFileFilter());
    fc.setName("Choose the XML file to save the selection");
    int returnVal = fc.showSaveDialog(appli.getFrame());
    if (returnVal != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File fic = fc.getSelectedFile();
    try {
      saveSelectionToXml(appli.getFrame().getVisuPanel().selectedObjects, name,
          fic);
    } catch (IOException e1) {
      e1.printStackTrace();
    } catch (SAXException e1) {
      e1.printStackTrace();
    } catch (ParserConfigurationException e1) {
      e1.printStackTrace();
    }
  }

  public SaveObjectSelection() {
    putValue(SHORT_DESCRIPTION,
        "Save the selected objects into XML for further re-use");
    putValue(NAME, "Save Object Selection");
  }

  private void saveSelectionToXml(Collection<IFeature> selection, String name,
      File file) throws IOException, SAXException, ParserConfigurationException {
    String cartagenDataset = CartAGenDocOld.getInstance().getCurrentDataset()
        .getCartAGenDB().getName();
    // first tests if 'file' is an existing xml file
    if (!file.exists()) {
      Document xmldoc = new DocumentImpl();
      Element root = xmldoc.createElement("object-selection");
      // it's a new file, the complete XML structure has to be written
      addSelectionXML(root, xmldoc, selection, name, cartagenDataset);

      // write in the file
      xmldoc.appendChild(root);
      FileOutputStream fos = new FileOutputStream(file);
      // XERCES 1 or 2 additionnal classes.
      OutputFormat of = new OutputFormat("XML", "ISO-8859-1", true);
      of.setIndent(1);
      of.setIndenting(true);
      XMLSerializer serializer = new XMLSerializer(fos, of);
      // As a DOM Serializer
      serializer.asDOMSerializer();
      serializer.serialize(xmldoc.getDocumentElement());
      fos.close();

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
    FileOutputStream fos = new FileOutputStream(file);
    // XERCES 1 or 2 additionnal classes.
    OutputFormat of = new OutputFormat("XML", "ISO-8859-1", true);
    of.setIndent(1);
    of.setIndenting(true);
    XMLSerializer serializer = new XMLSerializer(fos, of);
    // As a DOM Serializer
    serializer.asDOMSerializer();
    serializer.serialize(doc.getDocumentElement());
    fos.close();
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
      objElem.setAttribute("population-name", CartAGenDocOld.getInstance()
          .getCurrentDataset().getPopNameFromObj(obj));
      objsElem.appendChild(objElem);
    }
  }
}
