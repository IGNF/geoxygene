package fr.ign.cogit.geoxygene.sig3d.gui.dd;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.window.io.IOWindowChooser;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 *          Classe utilisée pour gérer le drag & drop dans la partie gauche de
 *          la fenêtre
 * 
 *          This class is used for file transfer drag & drop in the left panel
 *          of the window
 * 
 */
public class FileTransferHandler extends TransferHandler {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  InterfaceMap3D imp = null;

  public FileTransferHandler(InterfaceMap3D imp) {
    this.imp = imp;
  }

  /**
   * 
   */
  public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
    for (int i = 0; i < arg1.length; i++) {
      DataFlavor flavor = arg1[i];
      if (flavor.equals(DataFlavor.javaFileListFlavor)) {
        return true;
      }
      if (flavor.equals(DataFlavor.stringFlavor)) {
        return true;
      }
    }
    // Didn't find any that match, so:
    return false;
  }

  /**
   * Do the actual import.
   * 
   * @see javax.swing.TransferHandler#importData(javax.swing.JComponent,
   *      java.awt.datatransfer.Transferable)
   */
  @SuppressWarnings("unchecked")
public boolean importData(JComponent comp, Transferable t) {
    DataFlavor[] flavors = t.getTransferDataFlavors();
    for (int i = 0; i < flavors.length; i++) {
      DataFlavor flavor = flavors[i];
      try {
        if (flavor.equals(DataFlavor.javaFileListFlavor)) {
          List<File> l = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
          Iterator<File> iter = l.iterator();
          while (iter.hasNext()) {

            File file = (File) iter.next();

            try {
              if (IOWindowChooser.chooseWindows(imp, file)) {
                break;
              }
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }

          }
          return true;
        } else if (flavor.equals(DataFlavor.stringFlavor)) {

          String fileOrURL = (String) t.getTransferData(flavor);

          try {
            URL url = new URL(fileOrURL);

            try {
              if (IOWindowChooser.chooseWindows(imp, url.getPath())) {
                break;
              }
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }

            return true;
          } catch (MalformedURLException ex) {

            return false;
          }

        }
      } catch (IOException ex) {
        ex.printStackTrace();
      } catch (UnsupportedFlavorException e) {
        e.printStackTrace();
      }
    }
    // If you get here, I didn't like the flavor.
    Toolkit.getDefaultToolkit().beep();
    return false;
  }

}
