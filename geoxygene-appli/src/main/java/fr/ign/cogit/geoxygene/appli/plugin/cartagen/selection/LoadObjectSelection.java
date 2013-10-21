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

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.XMLFileFilter;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;

public class LoadObjectSelection extends AbstractAction {

  /****/
  private static final long serialVersionUID = 1L;
  private GeOxygeneApplication appli;

  @Override
  public void actionPerformed(ActionEvent e) {
    JFileChooser fc = new JFileChooser();
    fc.setFileFilter(new XMLFileFilter());
    fc.setName("Choose the XML file that contains the selection");
    int returnVal = fc.showOpenDialog(appli.getMainFrame().getGui());
    if (returnVal != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File fic = fc.getSelectedFile();
    try {
      LoadSelectionFrame frame = new LoadSelectionFrame(appli, fic);
      frame.setVisible(true);
    } catch (IOException e1) {
      e1.printStackTrace();
    } catch (SAXException e1) {
      e1.printStackTrace();
    } catch (ParserConfigurationException e1) {
      e1.printStackTrace();
    }
  }

  public LoadObjectSelection(GeOxygeneApplication appli) {
    this.appli = appli;
    putValue(SHORT_DESCRIPTION, "Load selected objects from XML");
    putValue(NAME, "Load Object Selection");
  }

}
