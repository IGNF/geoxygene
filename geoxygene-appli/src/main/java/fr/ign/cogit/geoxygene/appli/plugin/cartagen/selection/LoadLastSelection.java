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
import java.util.Map;

import javax.swing.AbstractAction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.util.LastSessionParameters;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;

public class LoadLastSelection extends AbstractAction {

  /****/
  private static final long serialVersionUID = 1L;
  private GeOxygeneApplication appli;

  @Override
  public void actionPerformed(ActionEvent e) {
    LastSessionParameters params = LastSessionParameters.getInstance();
    if (params.hasParameter("last object selection")) {
      String path = (String) params.getParameterValue("last object selection");
      Map<String, String> attrs = params
          .getParameterAttributes("last object selection");
      String selName = attrs.get("selectionName");
      try {
        // on commence par ouvrir le doucment XML pour le parser
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        db = dbf.newDocumentBuilder();
        Document doc;

        doc = db.parse(new File(path));
        doc.getDocumentElement().normalize();

        // on récupère la racine du fichier
        Element root = (Element) doc.getElementsByTagName("object-selection")
            .item(0);
        for (int i = 0; i < root.getElementsByTagName("selection").getLength(); i++) {
          Element elem = (Element) root.getElementsByTagName("selection").item(
              i);
          Element nameElem = (Element) elem.getElementsByTagName("name")
              .item(0);
          String name = nameElem.getChildNodes().item(0).getNodeValue();
          if (name.equals(selName)) {
            ObjectSelection sel = new ObjectSelection(appli, elem);
            sel.addToSelection();
            break;
          }
        }
      } catch (SAXException | IOException | ParserConfigurationException e1) {
        e1.printStackTrace();
      }

    } else {
      // DO NOTHING
      return;
    }

  }

  public LoadLastSelection(GeOxygeneApplication appli) {
    this.appli = appli;
    putValue(SHORT_DESCRIPTION, "Load the last selected objects from XML");
    putValue(NAME, "Load last selection");
  }

}
