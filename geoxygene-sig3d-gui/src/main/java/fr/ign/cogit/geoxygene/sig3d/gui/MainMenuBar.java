package fr.ign.cogit.geoxygene.sig3d.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.calculation.Calculation3D;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu.AlgoKadaMenu;
import fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu.AnimationCameraMenu;
import fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu.BooleanOperatorsMenu;
import fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu.Buffer3DMenu;
import fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu.DissimilarityMenu;
import fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu.EnvironmentMenu;
import fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu.LevelMenu;
import fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu.LightMenu;
import fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu.RayTracingMenu;
import fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu.TetraedrisationMenu;
import fr.ign.cogit.geoxygene.sig3d.gui.navigation3D.Picking;
import fr.ign.cogit.geoxygene.sig3d.gui.table.featurestable.FeaturesListTable;
import fr.ign.cogit.geoxygene.sig3d.gui.toolbar.IOToolBar;
import fr.ign.cogit.geoxygene.sig3d.gui.window.io.LoadingWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.window.io.PostGISLoadingWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.window.io.SavingWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.choice.RepresentationWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.choice.RepresentationWindowFactory;
import fr.ign.cogit.geoxygene.sig3d.io.ExportImage;
import fr.ign.cogit.geoxygene.sig3d.representation.ConstantRepresentation;
import fr.ign.cogit.geoxygene.sig3d.semantic.Layer;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

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
 * @author benoitpoupeau
 * 
 * @version 0.1
 * 
 *  
 * Il s'agit de la barre de menu de la fenêtre principale, elle contient tous
 * les boutons de l'interface The main menu of the application which contains
 * all the buttons and menus
 * 
 */
public class MainMenuBar extends JMenuBar implements ActionListener {

  private static final long serialVersionUID = 1L;
  private JMenu file, view, tools, simplification, calculation;

  private JMenuItem view_x, view_xinf, view_y, view_yinf, view_z, view_zinf,
      animation;

  private JMenuItem prop_open, prop_save_shape3d, prop_close,
      prop_load_database, prop_save_database, prop_exportimg,
      prop_modify_envirronment, prop_modify_ligth, prop_level;

  private JMenuItem booleanOperators, buffer3d, calculation_volume,
      calculation_barycenter, calculation_dissimilarity, calculation_area,
      decompositionWindow, algoKada, window_raytracing;

  private MainWindow mainWindow;
  private JButton butCopy, butInformation, butSup, boutonZoomOn;

  public MainMenuBar(MainWindow mainWindow) {
	    // Association a la JFrame
	    mainWindow.setJMenuBar(this);
	    
	

    // Recuperation de la JFrame
    this.mainWindow = mainWindow;

    // Menu des fichiers
    this.file = new JMenu(Messages.getString("BarrePrincipale.File"));

    // Items des menus
    // ouvrir
    this.prop_open = new JMenuItem(
        Messages.getString("BarrePrincipale.OpenFile"));
    // Ajout au menu fichier

    this.prop_open.addActionListener(this);
    this.file.add(this.prop_open);

    /*
     * // Sauvegarder la couche sélectionnée this.prop_sauvegarde = new
     * JMenuItem(Messages.getString("BarrePrincipale.SaveFile")); // Creation
     * d'un listener this.prop_sauvegarde.addActionListener(this);
     * this.file.add(this.prop_sauvegarde);
     */

    // Sauvegarder la couche sélectionnée
    this.prop_save_shape3d = new JMenuItem(
        Messages.getString("BarrePrincipale.SaveFileSHP3D"));

    // Creation d'un listener
    this.prop_save_shape3d.addActionListener(this);

    this.file.add(this.prop_save_shape3d);

    // Charge tous les objets en base
    this.prop_load_database = new JMenuItem(
        Messages.getString("BarrePrincipale.LoadDB"));

    // Creation d'un listener
    this.prop_load_database.addActionListener(this);

    this.file.add(this.prop_load_database);

    // Sauvegarder tous les objets en base
    this.prop_save_database = new JMenuItem(
        Messages.getString("BarrePrincipale.SaveDB"));

    // Creation d'un listener
    this.prop_save_database.addActionListener(this);

    this.file.add(this.prop_save_database);

    this.prop_exportimg = new JMenuItem(Messages.getString("ExportImage.Title"));
    this.prop_exportimg.addActionListener(this);

    this.file.add(this.prop_exportimg);

    // Quitte le programme
    this.prop_close = new JMenuItem(Messages.getString("BarrePrincipale.Quit"));

    // Creation d'un listener
    this.prop_close.addActionListener(this);

    this.file.add(this.prop_close);

    this.add(this.file);
this.add(
 
    new IOToolBar(mainWindow));
    // Menu vue

    this.view = new JMenu(Messages.getString("3DGIS.Vue"));

    this.view_x = new JMenuItem(Messages.getString("3DGIS.Vue") + " X");
    this.view_xinf = new JMenuItem(
        Messages.getString("BarrePrincipale.VueInverse") + " X");
    this.view_y = new JMenuItem(Messages.getString("3DGIS.Vue") + " Y");
    this.view_yinf = new JMenuItem(
        Messages.getString("BarrePrincipale.VueInverse") + " Y");
    this.view_z = new JMenuItem(Messages.getString("3DGIS.Vue") + " Z");
    this.view_zinf = new JMenuItem(
        Messages.getString("BarrePrincipale.VueInverse") + " Z");
    this.prop_modify_envirronment = new JMenuItem(
        Messages.getString("BarrePrincipale.ModifyScreen"));
    this.prop_modify_ligth = new JMenuItem(
        Messages.getString("BarrePrincipale.ModifyLight"));
    this.animation = new JMenuItem(
        Messages.getString("FenetreAnimationCamera.Titre"));

    this.view_x.addActionListener(this);
    this.view_xinf.addActionListener(this);
    this.view_y.addActionListener(this);
    this.view_yinf.addActionListener(this);
    this.view_z.addActionListener(this);
    this.view_zinf.addActionListener(this);
    this.prop_modify_envirronment.addActionListener(this);
    this.prop_modify_ligth.addActionListener(this);
    this.animation.addActionListener(this);

    this.view.add(this.view_x);
    this.view.add(this.view_xinf);
    this.view.add(this.view_y);
    this.view.add(this.view_yinf);
    this.view.add(this.view_z);
    this.view.add(this.view_zinf);
    this.view.add(this.prop_modify_envirronment);
    this.view.add(this.prop_modify_ligth);
    this.view.add(this.animation);

    this.add(this.view);

    // Menu Outils

    this.tools = new JMenu(Messages.getString("3DGIS.Tools"));

    // Sous menu simplification
    this.simplification = new JMenu(Messages.getString("3DGIS.Simplification"));
    // Menu des différents algos
    this.algoKada = new JMenuItem(
        Messages.getString("FenetreSimplification.Title"));
    this.algoKada.addActionListener(this);
    this.simplification.add(this.algoKada);

    this.prop_level = new JMenuItem(Messages.getString("FenetreNiveau.Title"));
    this.prop_level.addActionListener(this);

    this.tools.add(this.simplification);
    this.tools.add(this.prop_level);

    this.add(this.tools);

    // Outil de calculs
    this.calculation = new JMenu(Messages.getString("3DIGS.Calcul"));

    this.booleanOperators = new JMenuItem(
        Messages.getString("ColculOpBoolean.Title"));
    this.booleanOperators.addActionListener(this);

    this.buffer3d = new JMenuItem(Messages.getString("CalculBuffer3D.Title"));
    this.buffer3d.addActionListener(this);

    this.decompositionWindow = new JMenuItem(
        Messages.getString("Triangulation.Title"));
    this.decompositionWindow.addActionListener(this);

    this.calculation_volume = new JMenuItem(
        Messages.getString("BarrePrincipale.VolumeCal"));
    this.calculation_volume.addActionListener(this);

    this.calculation_barycenter = new JMenuItem(
        Messages.getString("BarrePrincipale.CGCal"));
    this.calculation_barycenter.addActionListener(this);

    this.calculation_area = new JMenuItem(
        Messages.getString("BarrePrincipale.CGAire"));
    this.calculation_area.addActionListener(this);

    this.calculation_dissimilarity = new JMenuItem(
        Messages.getString("BarrePrincipale.CDifference"));
    this.calculation_dissimilarity.addActionListener(this);

    this.window_raytracing = new JMenuItem(
        Messages.getString("FenetreRayonnement.Title"));
    this.window_raytracing.addActionListener(this);

    this.calculation.add(this.calculation_barycenter);
    this.calculation.add(this.calculation_area);
    this.calculation.add(this.calculation_volume);

    this.calculation.add(this.booleanOperators);
    this.calculation.add(this.decompositionWindow);
    this.calculation.add(this.buffer3d);
    this.calculation.add(this.calculation_dissimilarity);
    this.calculation.add(this.window_raytracing);

    this.add(this.calculation);

    this.butSup = new JButton(Messages.getString("3DGIS.Delete"));
    this.butSup.addActionListener(this);
    this.add(this.butSup);

    this.butCopy = new JButton(Messages.getString("BarrePrincipale.Copy"));
    this.butCopy.addActionListener(this);
    this.add(this.butCopy);

    this.butInformation = new JButton(
        Messages.getString("BarrePrincipale.Information"));
    this.butInformation.addActionListener(this);
    this.butInformation.setBackground(Color.red);
    this.add(this.butInformation);

    this.boutonZoomOn = new JButton(
        Messages.getString("BarrePrincipale.Center"));
    this.boutonZoomOn.addActionListener(this);
    this.add(this.boutonZoomOn);


  }

  /**
   * Ecouteurs pour afficher, fermer et charger des donnees dans la vue globale
   */

  @Override
  public void actionPerformed(ActionEvent actionevent) {

    Object source = actionevent.getSource();

    // Chargement de données
    if (source == this.prop_open) {

      (new LoadingWindow(this.getInterfaceMap3D())).setVisible(true);
      return;

    }

    // Chargement de tous les objets en base
    if (source == this.prop_load_database) {
      new PostGISLoadingWindow(this.getInterfaceMap3D());
      return;

    }

    // Chargement d'une couche en bases
    if (source == this.prop_save_database) {
      SavingWindow.saveLayerPostGIS(this.getInterfaceMap3D().getCurrent3DMap());
      return;

    }

    // Chargement d'une couche en shape3d
    if (source == this.prop_save_shape3d) {
      try {
        SavingWindow.saveLayerShapeFile3D(this.getInterfaceMap3D()
            .getCurrent3DMap());
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
      return;

    }

    // Export d'une image
    if (source == this.prop_exportimg) {
      ExportImage.export(this.getInterfaceMap3D());
      return;
    }

    if (source == this.prop_close) {

      this.mainWindow.dispose();
      System.exit(1);
      return;
    }

    // Voir la scene à partir de XMAX
    if (source == this.view_x) {
      this.getInterfaceMap3D().initViewpoint(1);
      return;

    }

    // Voir la scene à partir de XMIN
    if (source == this.view_xinf) {

      this.getInterfaceMap3D().initViewpoint(2);
      return;

    }

    // Voir la scene à partir de YMAX
    if (source == this.view_y) {

      this.getInterfaceMap3D().initViewpoint(3);
      return;

    }

    // Voir la scene à partir de YMIN
    if (source == this.view_yinf) {

      this.getInterfaceMap3D().initViewpoint(4);
      return;
    }

    // Voir la scene à partir de ZMAX
    if (source == this.view_z) {

      this.getInterfaceMap3D().initViewpoint(5);
      return;

    }

    // Voir la scene à partir de ZMIN
    if (source == this.view_zinf) {

      this.getInterfaceMap3D().initViewpoint(6);
      return;

    }

    // Ouverture de la fenetre de gestion d'affichage
    if (source == this.prop_modify_envirronment) {
      JPanel p = new EnvironmentMenu(this.getInterfaceMap3D());
      this.mainWindow.getActionPanel().setActionComponent(p);
      return;

    }

    // Fenetre de l'algo de kada
    if (source == this.algoKada) {
      JPanel p = new AlgoKadaMenu(this.getInterfaceMap3D());
      this.mainWindow.getActionPanel().setActionComponent(p);
      return;

    }

    // Fenetre de niveau
    if (source == this.prop_level) {
      JPanel p = new LevelMenu(this.getInterfaceMap3D());
      this.mainWindow.getActionPanel().setActionComponent(p);
      return;
    }

    // Active ou desactive l'affichage d'information lors du Picking
    if (source == this.butInformation) {

      Picking.info = !Picking.info;

      if (Picking.info) {

        this.butInformation.setBackground(Color.green);
      } else {

        this.butInformation.setBackground(Color.red);

      }

      return;
    }
    // Bouton permettant de cloner les objets selectionnés
    if (source == this.butCopy) {

      // Permet de cloner une liste d'objets
      int nobj = this.getInterfaceMap3D().getSelection().size();

      if (nobj == 0) {
        JOptionPane.showMessageDialog(this.mainWindow,
            Messages.getString("3DGIS.NoSlection"),
            Messages.getString("BarrePrincipale.Copy"),
            JOptionPane.WARNING_MESSAGE);
        return;
      }

      // La liste initiale
      IFeatureCollection<IFeature> lObj = new FT_FeatureCollection<IFeature>();
      lObj = this.getInterfaceMap3D().getSelection();
      int n = lObj.size();

      // Contient la liste des objets clonés
      IFeatureCollection<IFeature> lObjClone = new FT_FeatureCollection<IFeature>();

      for (int i = 0; i < n; i++) {
        IFeature objTemp = lObj.get(i);

        IFeature obj = new DefaultFeature((GM_Object) objTemp.getGeom().clone());

        lObjClone.add(obj);

      }

      // On affiche un menu correspondant à la dimension
      RepresentationWindow repW = RepresentationWindowFactory.generateDialog(
          this.getInterfaceMap3D(), lObjClone);

      ((JDialog) repW).setVisible(true);

      return;
    }

    // Bouton supprimant une selection
    if (source == this.butSup) {

      this.getInterfaceMap3D().suppressSelection();

      JComponent pan = this.mainWindow.getActionPanel().getActionComponent();

      if (pan instanceof FeaturesListTable) {
        ((FeaturesListTable) pan).refresh();
      }

      JOptionPane.showMessageDialog(this.mainWindow,
          Messages.getString("BarrePrincipale.ODeleted"),
          Messages.getString("3DGIS.Delete"), JOptionPane.INFORMATION_MESSAGE);

      return;

    }

    // Fenetre permettant des Opérations booléennes
    if (source.equals(this.booleanOperators)) {
      JPanel pan = new BooleanOperatorsMenu(this.getInterfaceMap3D());
      this.mainWindow.getActionPanel().setActionComponent(pan);

      return;
    }

    // Fenetre permettant le calcul d'un buffer3D
    if (source.equals(this.buffer3d)) {
      JPanel p = new Buffer3DMenu(this.getInterfaceMap3D());
      this.mainWindow.getActionPanel().setActionComponent(p);
      return;
    }

    // Ouverture de la fenetre de calcul de rayonnement
    if (source.equals(this.window_raytracing)) {
      Layer cTemp = this.getInterfaceMap3D().getCurrent3DMap()
          .getLayer(Picking.NOM_COUCHE_POINTS);

      if (cTemp == null) {
        JOptionPane.showMessageDialog(this.mainWindow,
            Messages.getString("BarrePrincipale.NoPointSel"),
            Messages.getString("FenetreRayonnement.Title"),
            JOptionPane.WARNING_MESSAGE);
        return;
      }

      JPanel p = new RayTracingMenu(this.getInterfaceMap3D());
      this.mainWindow.getActionPanel().setActionComponent(p);
      return;
    }

    // Fenetre de calcul de décomposition d'objets
    if (source.equals(this.decompositionWindow)) {
      JPanel p = new TetraedrisationMenu(this.getInterfaceMap3D());
      this.mainWindow.getActionPanel().setActionComponent(p);
      return;
    }

    // Fenetre différence
    if (source.equals(this.calculation_dissimilarity)) {
      JPanel p = new DissimilarityMenu(this.getInterfaceMap3D());
      this.mainWindow.getActionPanel().setActionComponent(p);
      return;
    }

    // Modification de l'environnement lumineux
    if (source == this.prop_modify_ligth) {
      JPanel p = new LightMenu(this.getInterfaceMap3D());
      this.mainWindow.getActionPanel().setActionComponent(p);
      return;
    }

    if (source == this.animation) {
      JPanel p = new AnimationCameraMenu(this.getInterfaceMap3D());
      this.mainWindow.getActionPanel().setActionComponent(p);
      return;

    }

    // _____________________________________________________________
    // Un objet doit être sélectionné pour les options suivantes
    // _____________________________________________________________
    if (this.getInterfaceMap3D().getSelection().size() == 0) {

      JOptionPane.showMessageDialog(this.mainWindow,
          Messages.getString("3DGIS.NoSlection"),
          Messages.getString("3DIGS.Calcul"), JOptionPane.WARNING_MESSAGE);
      return;

    }

    // On utilise le premier objet sélecionné
    IFeatureCollection<IFeature> featColl = this.getInterfaceMap3D()
        .getSelection();
    IFeature obj = featColl.get(0);
    int nbElem = featColl.size();

    // Centre sur le premier objet selectionne
    if (source == this.boutonZoomOn) {

      Box3D b = new Box3D(obj.getGeom());

      for (int i = 1; i < nbElem; i++) {
        b = b.union(new Box3D(featColl.get(i).getGeom()));
      }

      IDirectPosition dp = b.getURDP();
      IDirectPosition dp2 = b.getLLDP();

      double distance = Math.min(2 * dp.distance(dp2),
          ConstantRepresentation.backClip / 2);

      distance = Math.max(distance, 20);

      Vecteur v = new Vecteur(distance, distance, distance

      );

      this.getInterfaceMap3D().zoomOn((dp.getX() + dp2.getX()) / 2,
          (dp.getY() + dp2.getY()) / 2, (dp.getZ() + dp2.getZ()) / 2, v);

    }

    /**
     * Calcul le volume du premier objet de la sélection
     */

    if (source == this.calculation_volume) {

      double d = Calculation3D.volume((GM_Solid) obj.getGeom());

      JOptionPane.showMessageDialog(this.mainWindow,
          Messages.getString("3DGIS.Volume") + " : " + d,
          Messages.getString("BarrePrincipale.VolumeCal"),
          JOptionPane.INFORMATION_MESSAGE);
      return;

    }

    /**
     * Calcul le centre de gravité du premier objet de la sélection
     */

    if (source.equals(this.calculation_barycenter)) {

      IDirectPosition dp = Calculation3D.centerOfGravity((GM_Solid) obj
          .getGeom());
      if (Double.isNaN(dp.getX())) {
        JOptionPane.showMessageDialog(this.mainWindow,
            Messages.getString("Triangulation.Error"),
            Messages.getString("BarrePrincipale.CGCal"), ImageObserver.ERROR);
        return;

      }
      GM_Point p = new GM_Point(dp);
      IFeature objG = new DefaultFeature(p);

      FT_FeatureCollection<IFeature> lObj = new FT_FeatureCollection<IFeature>();
      lObj.add(objG);
      RepresentationWindowFactory
          .generateDialog(this.getInterfaceMap3D(), lObj);

      return;
    }

    /**
     * Calcul l'aire du premier objet de la sélection
     */

    if (source.equals(this.calculation_area)) {
      double d = Calculation3D.area((GM_Solid) obj.getGeom());

      JOptionPane.showMessageDialog(this.mainWindow,
          Messages.getString("3DIGS.Aire") + " : " + d,
          Messages.getString("BarrePrincipale.CGAire"),
          JOptionPane.INFORMATION_MESSAGE);

      return;
    }

  }

  /**
   * 
   * 
   * @return Renvoie l'application dans laquelle s'affiche cette barre de menu
   */
  public InterfaceMap3D getInterfaceMap3D() {
    return this.mainWindow.getInterfaceMap3D();
  }

}
