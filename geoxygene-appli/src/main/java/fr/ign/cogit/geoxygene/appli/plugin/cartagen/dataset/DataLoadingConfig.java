/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.dataset;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class DataLoadingConfig {

  public void configuration(String newChemin) {

    try {

      // Instanciation de la classe XStream
      XStream xstream = new XStream(new DomDriver());
      xstream.alias("configurationDonnees", DataConfigMapping.class);

      // Fichier de configuration de donnÃ©es ./configurationDonnees.xml
      // File fichier = new File("./configurationDonnees.xml");

      // Redirection du fichier ./configurationDonnees.xml vers un flux
      // d'entrÃ©e fichier
      // FileInputStream fis = DataLoadingConfig.class
      // .getResourceAsStream("/configurationDonnees.xml");

      InputStream fis = DataLoadingConfig.class
          .getResourceAsStream("/configurationDonnees.xml");

      // Désérialisation du fichier ./configurationDonnees.xml vers un nouvel
      // objet article
      DataConfigMapping config = (DataConfigMapping) xstream.fromXML(fis);

      // Modification du contenu de l'attribut cheminRepertoireDonneesSHP
      config.setCheminRepertoireDonneesSHP(newChemin);

      config.setEnrichissementRelief("false");
      config.setEnrichissementOccSol("false");
      config.setEnrichissementBati("false");
      config.setEnrichissementBatiAlign("false");
      config.setEnrichissementRoutier("false");
      config.setEnrichissementHydro("false");
      config.setConstruireFacesReseau("false");

      // Conversion vers XML
      String xml = xstream.toXML(config);

      // Affichage de la conversion XML
      // System.out.println(xml);

      // On ferme le flux en entrÃ©e
      fis.close();

      // Instanciation d'un flux de sortie fichier vers
      // ./configurationDonnees.xml
      FileOutputStream fos = new FileOutputStream(
          DataLoadingConfig.class.getResource("/configurationDonnees.xml")
              .getPath().replaceAll("%20", " "));

      // SÃ©rialisation de l'objet article dans ./configurationDonnees.xml
      xstream.toXML(config, fos);

      // On ferme le flux en entrÃ©e
      fos.close();

    }

    catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

  }

}
