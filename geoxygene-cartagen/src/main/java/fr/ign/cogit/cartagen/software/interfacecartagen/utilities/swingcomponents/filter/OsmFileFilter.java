/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class OsmFileFilter extends FileFilter {

  public OsmFileFilter() {
  }

  // Accept all directories and all txt files.
  @Override
  public boolean accept(File f) {
    if (f.isDirectory()) {
      return true;
    }
    String extension = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');

    if (i > 0 && i < s.length() - 1) {
      extension = s.substring(i + 1).toLowerCase();
    }

    if (extension != null) {
      if (extension.equals("osm")) {
        return true;
      }
      return false;
    }
    return false;
  }

  // The description of this filter
  @Override
  public String getDescription() {
    return "OSM Files";
  }
}
