package fr.ign.cogit.geoxygene.sig3d.gui;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;

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
 * Définition du panneau de droite destiné à recuillir les différents
 * formulaires pour exécuter des opérations. Le changement de formulaire se fait
 * grâce à la méthode setPanel(JComponent pan) Pannel allowing management of
 * forms thanks to setPanel(JComponent pan) method
 */
public class ActionPanel extends JSplitPane {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  MainWindow mainWindow = null;
  int initialWidth;

  /**
   * Création du panneau que l'on ajoutera à droite de la fenêtre principale
   * avec une largeur défini. Il s'agit d'un JSplitPanel
   * 
   * @param mainWindow l'interface associée au paneau
   * @param largeurIni la largeur initiale lors de l'ajout d'un menu
   */
  public ActionPanel(MainWindow mainWindow, int largeurIni) {

    super(JSplitPane.HORIZONTAL_SPLIT, mainWindow.getInterfaceMap3D(),
        new JPanel());
    this.setDividerLocation(largeurIni);

    this.mainWindow = mainWindow;

    this.initialWidth = largeurIni;

  }

  private JComponent currentActionComponent = null;

  /**
   * Permet de modifier le formulaire dans le panneau de droite. La largeur du
   * panneau est remis à jour pour accepter ce nouvel élément
   * 
   * @param actionComponent le formulaire qui trouvera sa place dans le panneau
   *          de droite de l'interface
   */
  public void setActionComponent(JComponent actionComponent) {

    this.currentActionComponent = actionComponent;

    JScrollPane jscrollpan = new JScrollPane(actionComponent);
    jscrollpan
        .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jscrollpan
        .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    this.setRightComponent(jscrollpan);

    int width = this.getWidth() - actionComponent.getWidth();

    this.setDividerLocation(Math.min(width, this.initialWidth));

  }

  /**
   * Permet de récupérer le panneau droit actif
   * 
   * @return le panneau droit actif
   */
  public JComponent getActionComponent() {

    return this.currentActionComponent;
  }

}
