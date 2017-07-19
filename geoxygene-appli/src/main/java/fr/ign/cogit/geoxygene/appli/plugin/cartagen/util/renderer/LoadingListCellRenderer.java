/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.util.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import fr.ign.cogit.geoxygene.appli.plugin.cartagen.dataset.LoaderUtil;

public class LoadingListCellRenderer implements ListCellRenderer {

  @Override
  public Component getListCellRendererComponent(JList list, Object value, // value
                                                                          // to
                                                                          // display
      int index, // cell index
      boolean isSelected, // is the cell selected
      boolean cellHasFocus) // the list and the cell have the focus
  {
    String str = value.toString();
    JLabel lbl = new JLabel(str);
    lbl.setForeground(Color.RED);
    for (String s : LoaderUtil.type) {
      if (s.equals(str)) {
        lbl.setForeground(new Color(0, 150, 0));
        break;
      }
    }
    return lbl;
  }

}
