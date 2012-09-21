package fr.ign.cogit.geoxygene.sig3d.sample;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.MainMenuBar;
import fr.ign.cogit.geoxygene.sig3d.gui.MainWindow;
import fr.ign.cogit.geoxygene.sig3d.representation.sample.ObjectCartoon;

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
 * Cette classe montre comment modifier l’interface graphique de l’application
 * afin d’ajouter un bouton permettant d’activer le panneau droit afin
 * d’exécuter un morceau de code. L’outil proposé ici permet d’appliquer
 * automatiquement des textures sur le toit et les faces des bâtiments.
 */
public class AddMenu extends JPanel {

  /**
   * Numéro de série générée
   */
  private static final long serialVersionUID = -8663686161472639973L;
  // Besoin de déclarer en static pour faire appel à cette instance lorsque
  // l'on appuie sur le bouton
  private static MainWindow fp;

  // Permet de lancer l'application
  public static void main(String args[]) {
    // On crée une fenetre applicative
    AddMenu.fp = new MainWindow();

    // On récupère la barre principale de l'application (ou l'on veut
    // rajouter un bouton)
    MainMenuBar bp = AddMenu.fp.getMainMenuBar();

    // Le bouton que l'on souhaite ajouter
    // On peut mettre de la même manière un JMenu etc.
    JButton butt = new JButton("Test");
    butt.addActionListener(new ActionListener() {

      // Quand on clic sur le bouton on déclenche l'action suivante
      // Ouverture d'une fenètre dans le panneau droit
      @Override
      public void actionPerformed(ActionEvent e) {
        // Lorsque l'on clique, on ajoute grâce à setPannel
        // Le panneau décrit dans le constructeur AjouteMenu
        AddMenu.fp.getActionPanel().setActionComponent(new AddMenu());
      }
    });

    // On ajoute le bouton au menu
    bp.add(butt);

    // On lance l'application
    AddMenu.fp.setVisible(true);
  }

  /**
   * Constructeur du panneau. Il s'agit d'un JPanel Les classes du package
   * fr.ign.cogit.geoxygene.sig3d.ihm.menupanneaudroit peuvent servir également
   * d'exemple Il contiendra un titre et un bouton de validation
   */
  public AddMenu() {

    super();

    // On définit le titre et une bordure
    this.setBorder(new TitledBorder(new EtchedBorder(), " Outil de test "));

    // On définit le bouton ainsi que l'évènement déclenché si l'on clique
    // dessus
    JButton button = new JButton("Modifier");
    button.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // On lance l'opération
        AddMenu.processStyle(AddMenu.fp.getInterfaceMap3D());
      }
    });
    this.add(button);

    // C'est la taille de la fenêtre
    // Elle apparaîtra de cette taille lorsque le bouton d'ouverture sera
    // cliqué
    this.setSize(300, 84);

  }

  /**
   * Modifie le style des différents objets
   * 
   * @param iCarte l'interface de carte dans laquelle la modification est
   *          effectuée
   */
  public static void processStyle(InterfaceMap3D iCarte) {
    // On récupère la sélection
    FT_FeatureCollection<IFeature> coll = iCarte.getSelection();

    // On parcourt les différents éléments
    int nbElem = coll.size();
    for (int i = 0; i < nbElem; i++) {
      IFeature feat = coll.get(i);

      if (feat.getGeom().dimension() != 3) {

        continue;
      }

      feat.setRepresentation(new ObjectCartoon(feat, Color.red));
    }
    // On rafraichit la représentation de la carte courante'
    iCarte.getCurrent3DMap().refresh();
  }

}
