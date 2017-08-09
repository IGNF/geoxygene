package fr.ign.cogit.geoxygene.sig3d;

import javax.swing.JOptionPane;

import fr.ign.cogit.geoxygene.sig3d.gui.MainWindow;
import fr.ign.cogit.geoxygene.sig3d.sample.AddMenu;
import fr.ign.cogit.geoxygene.sig3d.sample.DTMDisplay;
import fr.ign.cogit.geoxygene.sig3d.sample.DisplayData;
import fr.ign.cogit.geoxygene.sig3d.sample.RGE;
import fr.ign.cogit.geoxygene.sig3d.sample.Symbology;
import fr.ign.cogit.geoxygene.sig3d.sample.Toponym;

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
 *
 * Classe permettant de lancer l'application ainsi que des démos.
 * 
 * Launching class of the application. Empty project and demonstrations are
 * available with this class
 * 
 */
public class Launcher {
  /**
   * Fenetre vide
   */
  private static String PROJET_VIDE = "Projet vide";

  /**
   * Affichage de données de la BD Topo
   */
  private static String PROJET_BDTOPO = "Projet BD Topo";

  /**
   * Exemple simple de construction de géométrie
   */
  private static String AFFICHE_DONNEES = "Exemple d'affichage de donnees";

  /**
   * Affichage d'un MNT
   */
  private static String TEST_MNT = "Affichage d'un MNT";

  /**
   * Affichage d'objets issus de la modélisation
   */
  private static String AFFICHE_MODELES = "Affichage de symboles";

  /**
   * Affichage de toponymes (ici des villes de France)
   */
  private static String AFFICHE_TOPONYME = "Exemple d'affichage de toponymes";

  /**
   * Ajout d'un menu
   */
  private static String AJOUT_MENU = "Ajout d'un menu";

  /**
   * Code affichant un menu permettant de choisir entre : - Une fenetre vide -
   * Differentes demos
   * 
   * @param args
   * @throws Exception
   */
  public static void main(final String[] args) throws Exception {

    // On affiche les choix disponibles et on les récupère
    String[] options = { Launcher.PROJET_VIDE, Launcher.PROJET_BDTOPO,
        Launcher.AFFICHE_DONNEES, Launcher.TEST_MNT, Launcher.AFFICHE_MODELES,
        Launcher.AFFICHE_TOPONYME, Launcher.AJOUT_MENU };

    // Propose de choisir entre les différentes applications
    Object obj = JOptionPane.showInputDialog(null,
        "Quelle application voulez-vous executer ?", "Choix de l'application",
        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    if (obj == null) {

      return;
    }

    int nbOptions = options.length;
    int i;

    for (i = 0; i < nbOptions; i++) {
      if (options[i].equals(obj.toString())) {
        break;
      }

    }

    switch (i) {
      case 0:
        MainWindow mw = new MainWindow();
        mw.setVisible(true);
        break;
      case 1:
        RGE.main(args);
        break;
      case 2:
        DisplayData.main(args);
        break;
      case 3:
        DTMDisplay.main(args);
        break;
      case 4:
        Symbology.main(args);
        break;
      case 5:
        Toponym.main(args);
        break;
      case 6:
        AddMenu.main(args);
        break;
        
      default:
        break;
    }

  }

}
