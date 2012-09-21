package fr.ign.cogit.geoxygene.sig3d.gui.filter;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 * 
 *         Cette classe permet de ne sélectionner que des fichiers .asc
 * 
 *         This class enables to select only .asc
 * 
 */
import java.io.File;

import javax.swing.filechooser.FileFilter;

public class MNTASCFilter extends FileFilter {

  /**
   * Acceptation du fichier
   */
  @Override
  public boolean accept(File fichier) {
    if (fichier.isDirectory()) {
      return true;
    }
    // récupèration de l'extension
    String extension = this.getExtension(fichier);

    // Test pour vérifier si le fichier est du XML
    if (extension != null) {
      if ("asc".equalsIgnoreCase(extension)) {
        return true;

      }
    }
    return false;
  }

  /**
   * La description du filtre
   */
  @Override
  public String getDescription() {
    return "DTM format .asc";
  }

  /**
   * méthode permettant d'extraire l'extension du filtre
   */
  private String getExtension(File f) {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');

    if (i > 0 && i < s.length() - 1) {
      return s.substring(i + 1).toLowerCase();
    }

    return ext;
  }
}
