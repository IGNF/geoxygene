/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes;

import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JMenu;

public class UrbanAlignmentMenu extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 6811865464668228808L;

  @SuppressWarnings("unused")
  private Logger logger = Logger.getLogger(UrbanAlignmentMenu.class.getName());

  private JLabel mAlignmentDefault = new JLabel("        empty menu        ");

  public UrbanAlignmentMenu(String title) {
    super(title);
    this.add(this.mAlignmentDefault);
  }

}
