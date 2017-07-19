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

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fr.ign.cogit.cartagen.algorithms.relief.ContourLineSelection;

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

    this.addSeparator();
    this.add(new JMenuItem(new SelectContoursAction()));

  }

  private class SelectContoursAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      String result = JOptionPane.showInputDialog("Step of selection", "2");
      int stepSelection = Integer.parseInt(result);

      ContourLineSelection algorithm = new ContourLineSelection(stepSelection,
          true);
      algorithm.computeSelection();
    }

    public SelectContoursAction() {
      this.putValue(Action.NAME, "Select one contour line out of n");
    }
  }
}
