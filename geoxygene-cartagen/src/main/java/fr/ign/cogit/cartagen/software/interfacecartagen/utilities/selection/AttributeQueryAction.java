/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.utilities.selection;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;

public class AttributeQueryAction extends AbstractAction {

  /**
   * 
   */
  private static final long serialVersionUID = 833324511619057181L;

  public AttributeQueryAction() {
    putValue(SHORT_DESCRIPTION, I18N.getString("AttributeQueryFrame.menuDescr")); //$NON-NLS-1$
    putValue(NAME, I18N.getString("AttributeQueryFrame.menuName")); //$NON-NLS-1$
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    AttributeQueryFrame frame = new AttributeQueryFrame();
    frame.setVisible(true);
  }

}
