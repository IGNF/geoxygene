package fr.ign.cogit.cartagen.pearep.gui;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.defaultschema.DefaultCreationFactory;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.PostgisDB;

public class GUIMainClassEclipse {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // ******************************************************
    // launch CartAGen as batch application
    // Objects creation factory
    CartagenApplication.getInstance().setCreationFactory(
        new DefaultCreationFactory());

    // Application initialisation
    CartagenApplication.getInstance().initApplication();
    CartAGenDoc doc = CartAGenDoc.getInstance();
    doc.setName("PEA_REP");
    doc.setPostGisDb(PostgisDB.get("PEA_REP", true));

    String jarPath = "C:\\Users\\Guillaume\\Documents\\travail\\PEA REP\\Module\\version2.0\\";
    String path = jarPath + "\\" + "ScaleMasterThemes.xml";
    File themesFile = new File(path);

    EditScaleMasterFrame frame = null;
    try {
      frame = new EditScaleMasterFrame(themesFile, false);
    } catch (OWLOntologyCreationException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    frame.setVisible(true);
  }

}
