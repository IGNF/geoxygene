package fr.ign.cogit.geoxygene.sig3d.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

/**
 * 
 * @brief Root GeOxygene 3D extension package
 * 
 * 
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
 * 
 * @author Brasebin Mickaël
 * @author benoitpoupeau
 * 
 *  @version 0.1
 * 
 *         Cette classe est la fenêtre principale qui contient l'ensemble des
 *         instructions permettant de se déplacer dans une scène 3D
 * 
 *         This is the main frame of the graphic interface of the application
 * 
 */
public class MainWindow extends JFrame implements WindowListener {

  private final static Logger logger = Logger.getLogger(MainWindow.class
      .getName());

  private static final long serialVersionUID = 1L;
  // lien avec la fenêtre de la vue locale
  private InterfaceMap3D iMap3D;
  private ContentPanel pOC;
  private MainMenuBar mainMenuBar;
  private ActionPanel pOA;

  // Taille totale de l'écran
  private final static Dimension SCREEN_DIMENSION = java.awt.Toolkit
      .getDefaultToolkit().getScreenSize();
  // on enleve la hauteur de la barre Windows
  private final static int HEIGHT_APPLICATION = MainWindow.SCREEN_DIMENSION.height - 30;
  private final static int WIDTH_APPLICATION = MainWindow.SCREEN_DIMENSION.width;
  // Taille par défaut du panneau gauche
  private final static int WIDTH_PANELSIDE = 275;

  /**
   * Constructeur par défaut
   */
  public MainWindow() {
    super();

    // Titre
    this.setTitle(" GeOxygene 3D 0.1 ");

    this.setSize(MainWindow.WIDTH_APPLICATION, MainWindow.HEIGHT_APPLICATION);

    // Mettre dan le coin supérieur gauche
    this.setLocation(0, 0);

    // Ajout de la barre de menu
    this.mainMenuBar = new MainMenuBar(this);
    this.setJMenuBar(this.mainMenuBar);

    // Initialisation de l'interface carte
    InterfaceMap3D iCarte3D = new InterfaceMap3D(MainWindow.WIDTH_APPLICATION
        - MainWindow.WIDTH_PANELSIDE, this.getHeight()
        - this.mainMenuBar.getHeight(), this);
    iCarte3D.setBorder(new EmptyBorder(0, 0, 0, 0));
    
    
   // iCarte3D.setBorder(new TitledBorder(new EtchedBorder(), Messages
    //    .getString("FenetrePrincipale.VueGlobale3D")));

    this.iMap3D = iCarte3D;

    // Création du panneau gauche
    ContentPanel pg = new ContentPanel(this);
    pg.setSize(MainWindow.WIDTH_PANELSIDE, this.getPreferredSize().height);
    this.pOC = pg;

    JPanel pan = new JPanel();
    pan.setSize(new Dimension(300, 300));

    // On construit le panneau droit et on décide de la largeur de
    // l'application
    this.pOA = new ActionPanel(this, MainWindow.WIDTH_APPLICATION
        - MainWindow.WIDTH_PANELSIDE * 2);

    // Ajout d'un splitPane pour séparer le panneau gauche et la carte3D
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pg,
        this.pOA);

    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(MainWindow.WIDTH_PANELSIDE);

    this.add(splitPane);

    // this.pack();
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    this.setVisible(true);
    // Ferme la jvm (pour àviter les "oublis")
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        MainWindow.this.dispose();
        System.exit(1);
        // Main.windowClosed();
      }
    });

  }

  /**
   * 
   * @return l'objet ActionPanel attaché à l'application
   */
  public ActionPanel getActionPanel() {
    return this.pOA;
  }

  /**
   * Lancement d'une application vide
   */
  public static void main(String[] args) {

    System.setProperty("user.language", "en");// -Duser.language=en
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);

    // code permettant de changer le mode d'affichage graphique
    String osName = System.getProperty("os.name");
    MainWindow.logger.info(osName);

    MainWindow m = new MainWindow();
    m.setVisible(true);
  }

  // Renvoie les divers éléments de l'application

  /**
   * @return Renvoie la zone d'affichage de la carte
   */
  public InterfaceMap3D getInterfaceMap3D() {
    return this.iMap3D;
  }

  /**
   * @return Renvoie le panneau de gestion de couche
   */
  public ContentPanel getContentPanel() {
    return this.pOC;
  }

  /**
   * @return Renvoie la barre de menu
   */
  public MainMenuBar getMainMenuBar() {
    return this.mainMenuBar;
  }

  @Override
  public void windowActivated(WindowEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void windowClosed(WindowEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void windowClosing(WindowEvent e) {
    // TODO Auto-generated method stub
    this.getInterfaceMap3D().close();

  }

  @Override
  public void windowDeactivated(WindowEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void windowDeiconified(WindowEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void windowIconified(WindowEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void windowOpened(WindowEvent e) {
    // TODO Auto-generated method stub

  }

}
