package fr.ign.cogit.geoxygene.sig3d.gui.table.textures;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import fr.ign.cogit.geoxygene.sig3d.Messages;

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
 * Classe utilisée pour représenter une texture (URL d'une image) dans un menu
 * déroulant
 * 
 * Class to represent a texture in a ComboBox
 */
public class GenericTexturesListRenderer implements ListCellRenderer {

  /**
     * 
     */
  private static final long serialVersionUID = 1L;

  @Override
  public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean cellHasFocus) {

    // On ne traite que les objets de type Color[]
    if (!(value instanceof String)) {
      return new JLabel();
    }

    File f = new File(value.toString());

    if (!f.exists()) {
      JLabel lab = new JLabel(Messages.getString("3DGIS.Undefined"));
      lab.setBackground(Color.black);

      return new JLabel(Messages.getString("3DGIS.Undefined"));
    }

    BufferedImage img;
    try {
      img = ImageIO.read(f);

      Image imgF = img.getScaledInstance(50, 50, Image.SCALE_FAST);

      ImageIcon im = new ImageIcon(imgF);

      JLabel label = new JLabel();
      label.setIcon(im);

      if (isSelected) {
        label.setOpaque(true);
        label.setBackground(Color.cyan);
      }

      // On renvoie le panel
      return label;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return new JLabel();
  }

}
