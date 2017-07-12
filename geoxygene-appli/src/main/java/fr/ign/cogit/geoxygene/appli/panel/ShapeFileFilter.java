/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.panel;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * 
 * @author GTouya
 * 
 *         A file filter for file choosers that limits to ESRI shapefiles.
 */
public class ShapeFileFilter extends FileFilter {

  public ShapeFileFilter() {
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
      if (extension.equals("shp")) {
        return true;
      }
      return false;
    }
    return false;
  }

  // The description of this filter
  @Override
  public String getDescription() {
    return "Just Shapefiles";
  }

}
