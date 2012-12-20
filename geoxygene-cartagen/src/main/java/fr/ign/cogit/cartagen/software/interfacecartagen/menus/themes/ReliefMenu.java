/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.menus.themes;

import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

public class ReliefMenu extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @SuppressWarnings("unused")
  private static Logger logger = Logger.getLogger(ReliefMenu.class.getName());

  public JCheckBoxMenuItem mReliefTrianglesVoirTexteAnglePente = new JCheckBoxMenuItem(
      "Display slope value (in rad)");
  public JCheckBoxMenuItem mReliefTrianglesVoirTexteAngleOrientationPente = new JCheckBoxMenuItem(
      "Display slope vactor orientation (in rad)");
  public JCheckBoxMenuItem mReliefTrianglesVoirContientBatiments = new JCheckBoxMenuItem(
      "Display triangles containing buildings");
  public JCheckBoxMenuItem mReliefTrianglesVoir = new JCheckBoxMenuItem(
      "Display triangles");
  public JCheckBoxMenuItem cVoirVecteurPente = new JCheckBoxMenuItem(
      "Slope vectors", false);

  public ReliefMenu(String title) {
    super(title);

    this.addSeparator();

    this.add(this.mReliefTrianglesVoir);
    this.add(this.cVoirVecteurPente);
    this.add(this.mReliefTrianglesVoirContientBatiments);
    this.add(this.mReliefTrianglesVoirTexteAnglePente);
    this.add(this.mReliefTrianglesVoirTexteAngleOrientationPente);

  }

}
