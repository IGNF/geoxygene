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
import java.net.URISyntaxException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.pearep.PeaRepGeneralisation;

public class ConfigLoggers {

  public ConfigLoggers() throws SecurityException, IOException,
      URISyntaxException {
    String jarPath = new File(PeaRepGeneralisation.class.getProtectionDomain()
        .getCodeSource().getLocation().toURI().getPath().substring(1))
        .getParent();
    Logger l = Logger.getLogger("PeaRep.trace");
    l.addHandler(new FileHandler(jarPath + "\\trace.log"));
    l = Logger.getLogger("PeaRep.error");
    l.addHandler(new FileHandler(jarPath + "\\error.log"));
  }
}
