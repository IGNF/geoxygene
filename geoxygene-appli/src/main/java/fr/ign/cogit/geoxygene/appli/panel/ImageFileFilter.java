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
 *         A file filter for file choosers that limits to image files (.gif,
 *         .tif, .tiff, .png, .jpg, .jpeg, .bmp)
 */
public class ImageFileFilter extends FileFilter {

  public ImageFileFilter() {
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
      if (extension.equals("gif")) {
        return true;
      }
      if (extension.equals("tif")) {
        return true;
      }
      if (extension.equals("tiff")) {
        return true;
      }
      if (extension.equals("png")) {
        return true;
      }
      if (extension.equals("jpg")) {
        return true;
      }
      if (extension.equals("jpeg")) {
        return true;
      }
      if (extension.equals("bmp")) {
        return true;
      }
      return false;
    }
    return false;
  }

  // The description of this filter
  @Override
  public String getDescription() {
    return "Just Image Files";
  }

}
