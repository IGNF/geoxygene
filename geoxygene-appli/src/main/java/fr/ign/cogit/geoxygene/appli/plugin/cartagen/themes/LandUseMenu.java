/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes;

import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JMenu;

public class LandUseMenu extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 6811865464668228808L;

  @SuppressWarnings("unused")
  private Logger logger = Logger.getLogger(LandUseMenu.class.getName());

  private JLabel mOccSolDefault = new JLabel("        empty menu        ");

  public LandUseMenu(String title) {
    super(title);
    this.add(this.mOccSolDefault);
  }

}
