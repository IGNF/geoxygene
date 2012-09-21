package fr.ign.cogit.geoxygene.sig3d.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

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
 * @author benoitpoupeau
 *  
 * @version 0.1
 * 
 * Classe de rendu pour les dégradés (Color[]) classe
 * fr.ign.cogit.geoxygene.sig3d.util.ColorShade Classe utilisée pour proposer
 * dans un JCombobox différents dégradés dans le menu de gestion du MNT
 * 
 * Renderer class for color shades (classe
 * fr.ign.cogit.geoxygene.sig3d.util.ColorShade) used in DTM modification menu
 */
public class ColorGradient extends BasicComboBoxRenderer {

  private static final long serialVersionUID = 1L;

  @Override
  public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean cellHasFocus) {
    // On ne traite que les objets de type Color[]
    if (!(value instanceof Color[])) {
      return null;
    }

    Color[] tabColor = (Color[]) value;
    int nbElem = tabColor.length;

    GridLayout grd = new GridLayout(1, nbElem);
    JPanel pan = new JPanel(grd);
    // Changement de bordure en fonction du focus
    if (cellHasFocus) {

      pan.setBorder(BorderFactory.createLineBorder(Color.red, 2));

    } else {
      pan.setBorder(BorderFactory.createLineBorder(Color.black, 1));
    }

    for (int i = 0; i < nbElem; i++) {
      // On affiche dans un panel un JTextfield de chaque couleur
      Color col = tabColor[i];
      JLabel lab = new JLabel();
      lab.setBackground(col);
      lab.setForeground(col);

      // Il faut mettre un texte vide pour avoir un affichage correct
      lab.setText(" ");
      lab.setOpaque(true);
      pan.add(lab);
      lab.setBounds(list.getBounds());

    }
    // On renvoie le panel
    return pan;
  }

}
