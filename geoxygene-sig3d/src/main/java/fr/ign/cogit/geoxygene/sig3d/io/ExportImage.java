package fr.ign.cogit.geoxygene.sig3d.io;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;


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
 * Cette classe permet de sauvegarder l'image 3D en fichier imagae (.jpg) This
 * class enables to export a 3D map in a jpg file.
 */
public final class ExportImage {

  private final static Logger logger = Logger.getLogger(ExportImage.class
      .getName());

  /**
   * Exporter une carte dans une image
   * 
   * @param iMap3D l'interface de carte qui sera exportée
   */
  public static void export(InterfaceMap3D iMap3D) {

    ExportImage.logger.info("Export de la carte au format image");

    // JFileChooser homeChooser = new JFileChooser("Mes documents");
    JFileChooser homeChooser = new JFileChooser(
        Messages.getString("3DGIS.HomeDir"));

    homeChooser.setAcceptAllFileFilterUsed(false);
    homeChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    int returnVal = homeChooser.showOpenDialog(null);

    // Recuperation d'un fichier pour lancer le
    // parseurXML--------------------------------------------------------
    if (returnVal != JFileChooser.APPROVE_OPTION) {
      JOptionPane.showMessageDialog(iMap3D,
          Messages.getString("ExportImage.Fail"),
          Messages.getString("ExportImage.Title"),
          JOptionPane.INFORMATION_MESSAGE);
      return;

    }

    File file = homeChooser.getSelectedFile();

    if (file == null) {

      JOptionPane.showMessageDialog(iMap3D,
          Messages.getString("ExportImage.Fail"),
          Messages.getString("ExportImage.Title"),
          JOptionPane.INFORMATION_MESSAGE);

      return;
    }

    if (!file.exists()) {

      JOptionPane.showMessageDialog(iMap3D,
          Messages.getString("3DGIS.FolderNotExist"),
          Messages.getString("ExportImage.Title"),
          JOptionPane.INFORMATION_MESSAGE);

      return;

    }

    String nomfichier = (String) JOptionPane.showInputDialog(iMap3D,
        Messages.getString("3DGIS.FileFormat"),
        Messages.getString("ExportImage.Title"), JOptionPane.QUESTION_MESSAGE,
        null, null, // c'est ouvert !!!
        "Image"); // valeur initiale

    if (nomfichier == null) {

      JOptionPane.showMessageDialog(iMap3D,
          Messages.getString("ExportImage.Fail"),
          Messages.getString("ExportImage.Title"),
          JOptionPane.INFORMATION_MESSAGE);

      return;
    }

    if (nomfichier.equals("")) {

      nomfichier = "export";
    }

    if (!nomfichier.toUpperCase().contains(".JPG")
        || !nomfichier.toUpperCase().contains(".JPEG")) {
      nomfichier = nomfichier + ".jpg";

    }

    boolean succes = iMap3D.screenCapture(file.getAbsolutePath(), nomfichier);

    if (succes) {
      ExportImage.logger.info(Messages.getString("ExportImage.Succes")
          + file.getPath());
      JOptionPane.showMessageDialog(iMap3D,
          Messages.getString("ExportImage.Succes"),
          Messages.getString("ExportImage.Title"),
          JOptionPane.INFORMATION_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(iMap3D,
          Messages.getString("ExportImage.Fail"),
          Messages.getString("ExportImage.Title"),
          JOptionPane.INFORMATION_MESSAGE);
    }

  }
}
