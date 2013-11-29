package fr.ign.cogit.geoxygene.sig3d.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Group;
import javax.media.j3d.Light;
import javax.media.j3d.Locale;
import javax.media.j3d.Node;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.PickInfo;
import javax.media.j3d.PointLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.apache.log4j.Logger;

import com.sun.j3d.exp.swing.JCanvas3D;
import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.pickfast.PickCanvas;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.sig3d.gui.navigation3D.FPSBehavior;
import fr.ign.cogit.geoxygene.sig3d.gui.navigation3D.Picking;
import fr.ign.cogit.geoxygene.sig3d.representation.ConstantRepresentation;
import fr.ign.cogit.geoxygene.sig3d.representation.I3DRepresentation;
import fr.ign.cogit.geoxygene.sig3d.semantic.Layer;
import fr.ign.cogit.geoxygene.sig3d.semantic.Map3D;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;

/**
 * This software is released under the licence CeCILL
 * see LICENSE.TXT
 * see <http://www.cecill.info/ http://www.cecill.info/
 * @copyright IGN
 * @author Brasebin Mickaël
 * @author poupeau
 * @version 0.1
 *          Classe permettant de créer un environnement 3D et d'y representer
 *          une instance de la classe Carte3D La sélection des objets est gérée
 *          dans cette classe This is the 3D map of the application
 */

public class InterfaceMap3D extends JPanel {

  private final static Logger logger = Logger.getLogger(InterfaceMap3D.class.getName());

  private static final long serialVersionUID = 1L;

  // Le JCanvas3D a remplacé le Canvas3D
  // Cela permet d'éviter les problèmes de composant lourds/léger
  private JCanvas3D canvas3D = null;

  // Il s'agit du branchgroupe définissant la scence
  private BranchGroup scene = null;

  // Il s'agit de la vue qui supporte l'affichage
  private View view = null;

  // Il s'agit du Branch group sur lequel se rattache les couches
  private BranchGroup Bgeneral = null;

  // Il s'agit du Branch group sur lequel se rattache les couches
  private BranchGroup bgvu = null;

  // Il s'agit des différentes transformations utilisant pour le déplacement
  private static TransformGroup tgScale, tgScaleZ, tgvu;

  // Il s'agit du changement d'échelle
  private Transform3D scale;

  // Il s'agit de paramètre définissant l'univers
  private Background background3D;

  // Il s'agit de la fenetre dans laquelle s'affiche la carte
  private MainWindow mainWindow;

  // Comportement de sélection d'objets
  private PickCanvas pickCanvas = null;

  // paramètres de derrières
  private List<PointLight> lLights = new ArrayList<PointLight>();

  // Comportement (déplacement, affichage FPS ...)
  private Behavior behaviour;

  // La couche permettant la gestion de la selection d'objet
  private FT_FeatureCollection<IFeature> selection = new FT_FeatureCollection<IFeature>();

  // La Carte3D affichée dans l'inteface
  private Map3D map;
  private Locale locale;

  // il s'agit des informations de changements de repères
  // Une translation est effectuée au démarrage afin que les coordonnées
  // Java3D
  // soient proches de 0,0
  private Vector3f translate = null;
  private TransformGroup tgtranscenter;

  /**
   * Constructeur de l'interface graphique
   * @param width
   *        Largeur de l'interface
   * @param height
   *        Hauteur de l'interface
   * @param mainWindow
   *        La fenetre dans laquelle s'affiche l'interface
   */
  public InterfaceMap3D(int width, int height, MainWindow mainWindow) {
    // On créer un lien réversible entre la carte et sa représentation
    this.map = new Map3D(this);
    this.mainWindow = mainWindow;
    this.setLayout(new BorderLayout());
    this.setSize(width, height);
    // &- Caracteristiques graphiques pour la vue 3D
    GraphicsConfigTemplate3D gc3D = new GraphicsConfigTemplate3D();
    /*
     * gc3D.setDepthSize(24); gc3D.setStencilSize(8);
     * gc3D.setStereo(GraphicsConfigTemplate.REQUIRED);
     */
    // 2-créer un objet VirtualUniverse
    VirtualUniverse virtualUniverse = new VirtualUniverse();
    // 3 - Création du panneau gêrant l'affichage
    this.canvas3D = new JCanvas3D(gc3D);
    this.canvas3D.setResizeMode(JCanvas3D.RESIZE_IMMEDIATELY);
    this.add("Center", this.canvas3D);
    Dimension dim = new Dimension(width, height);
    this.canvas3D.setPreferredSize(dim);
    this.canvas3D.setSize(dim);
    this.canvas3D.setDoubleBuffered(true);
    this.canvas3D.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    this.canvas3D.setVisible(true);
    this.canvas3D.setFocusable(true);
    this.canvas3D.setFocusTraversalKeysEnabled(true);
    // Permet d'activer les déplacements (histoire de JCanvas3D et
    // d'évènements automatiques AWT)
    this.canvas3D.addKeyListener(new java.awt.event.KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        // Permet d'activer les déplacements
      }

      @Override
      public void keyReleased(KeyEvent e) {
        // Permet d'activer les déplacements
      }

      @Override
      public void keyTyped(KeyEvent e) {
        // Permet d'activer les déplacements
      }

    });

    // Permet d'activer les déplacements (histoire de JCanvas3D et
    // d'évènements automatiques AWT)
    // Donne le focus au controle pour les déplacements clavier
    this.canvas3D.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent event) {
        // Permet d'activer les déplacements
      }

      @Override
      public void mouseDragged(MouseEvent event) {
        // Permet de récupèrer le focus si il est perdu et qu'un
        // mouvement est effectué avec la souris
        event.getComponent().requestFocus();
      }
    });

    // Permet d'activer les déplacements (histoire de JCanvas3D et
    // d'évènements automatiques AWT)
    // Donne le focus au controle pour les déplacements clavier
    this.canvas3D.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent event) {
        // Permet de récupèrer le focus si il est perdu et qu'un
        // clic est effectué avec la souris
        event.getComponent().requestFocus();
      }
    });

    // récupèration du composant lourd "caché"
    Canvas3D c = this.canvas3D.getOffscreenCanvas3D();
    this.locale = new Locale(virtualUniverse);
    // Branchgroup associés au VirtualUniverse
    this.bgvu = new BranchGroup();
    this.bgvu.setCapability(Group.ALLOW_CHILDREN_READ);
    this.bgvu.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    this.bgvu.setCapability(Group.ALLOW_CHILDREN_WRITE);
    this.bgvu.setCapability(BranchGroup.ALLOW_DETACH);
    Transform3D translation = new Transform3D();
    translation.set(new Vector3f(0.0f, 0.0f, 0f));
    InterfaceMap3D.tgvu = new TransformGroup();
    InterfaceMap3D.tgvu.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    InterfaceMap3D.tgvu.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    InterfaceMap3D.tgvu.setCapability(Group.ALLOW_CHILDREN_WRITE);
    InterfaceMap3D.tgvu.setCapability(Group.ALLOW_CHILDREN_READ);
    InterfaceMap3D.tgvu.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    InterfaceMap3D.tgvu.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
    InterfaceMap3D.tgvu.setTransform(translation);
    // 4.1-créer un graphe view branch pour locale
    // a-créer un View Object
    this.view = new View();
    // b-créer un ViewPlatform Object
    ViewPlatform viewPlatform = new ViewPlatform();
    viewPlatform.setCapability(ViewPlatform.ALLOW_POLICY_READ);
    viewPlatform.setCapability(ViewPlatform.ALLOW_POLICY_WRITE);
    viewPlatform.setCapability(Group.ALLOW_CHILDREN_READ);
    viewPlatform.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    viewPlatform.setCapability(Group.ALLOW_CHILDREN_WRITE);
    // c-créer un PhysicalBody Object
    PhysicalBody physicalBody = new PhysicalBody();
    // d-créer un PhysicalEnvironment Object
    PhysicalEnvironment physicalEnvironment = new PhysicalEnvironment();
    // e-Attacher les objets ViewPlatform, le PhysicalBody, le
    // PhysicalEnvironment, le canvas3D à l'objet View
    this.view.attachViewPlatform(viewPlatform);
    this.view.setPhysicalBody(physicalBody);
    this.view.setPhysicalEnvironment(physicalEnvironment);
    this.view.addCanvas3D(c);
    this.view.setFrontClipDistance(ConstantRepresentation.frontClip);
    this.view.setBackClipDistance(ConstantRepresentation.backClip);
    // limite le rafraichissement de l'image
    // this.view.setMinimumFrameCycleTime(15);
    // Antialiasing désactivé
    this.view.setSceneAntialiasingEnable(false);
    // Partie graphique
    Point3d origin = new Point3d(0.0, 0.0, 0.0);
    // Ajout du comportement de la souris
    InterfaceMap3D.tgvu.addChild(viewPlatform);
    this.bgvu.addChild(InterfaceMap3D.tgvu);
    // définition du BranchGroup définissant la scène
    this.scene = new BranchGroup();
    this.scene.setCapability(Group.ALLOW_CHILDREN_READ);
    this.scene.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    this.scene.setCapability(Group.ALLOW_CHILDREN_WRITE);
    this.scene.setCapability(BranchGroup.ALLOW_DETACH);
    // Affiche les fps dans la console
    // A désactiver
    if (ConstantRepresentation.visualisingFPS) {
      FPSBehavior fpsbehavior = new FPSBehavior();
      BoundingSphere boundes = new BoundingSphere(origin, Double.POSITIVE_INFINITY);
      fpsbehavior.setSchedulingBounds(boundes);
      this.scene.addChild(fpsbehavior);
    }
    // créer le comportement et l'associe à la scène
    this.behaviour = new Picking(c, this.scene);
    BoundingSphere bounds = new BoundingSphere(origin, Double.POSITIVE_INFINITY);
    this.behaviour.setSchedulingBounds(bounds);
    this.scene.addChild(this.behaviour);
    // PickCanvas Opére sur le Canvas3D (composant AWT dans Swing) invisible
    this.pickCanvas = new PickCanvas(this.canvas3D.getOffscreenCanvas3D(), this.scene);
    this.pickCanvas.setMode(PickInfo.PICK_GEOMETRY);
    // Création d'un fond
    BoundingSphere schedulingBounds = new BoundingSphere(new Point3d(), Double.POSITIVE_INFINITY);// 0000.0);
    this.background3D = new Background(new Color3f(ConstantRepresentation.backGroundColor));
    this.background3D.setApplicationBounds(schedulingBounds);
    this.background3D.setCapability(Background.ALLOW_COLOR_WRITE);
    this.scene.addChild(this.background3D);
    // Bgeneral
    this.Bgeneral = new BranchGroup();
    this.Bgeneral.setCapability(Node.ALLOW_PICKABLE_READ);
    this.Bgeneral.setCapability(Node.ALLOW_PICKABLE_WRITE);
    this.Bgeneral.setCapability(Node.ENABLE_PICK_REPORTING);
    this.Bgeneral.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    this.Bgeneral.setCapability(Group.ALLOW_CHILDREN_READ);
    this.Bgeneral.setCapability(Group.ALLOW_CHILDREN_WRITE);
    this.Bgeneral.setCapability(BranchGroup.ALLOW_DETACH);
    this.Bgeneral.setPickable(true);
    // Pour permettre la récupèration d'informations sur le BG
    BranchGroup branchDeplace = this.interscene(InterfaceMap3D.tgvu);
    branchDeplace.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    branchDeplace.setCapability(Group.ALLOW_CHILDREN_READ);
    branchDeplace.setCapability(Group.ALLOW_CHILDREN_WRITE);
    branchDeplace.setCapability(BranchGroup.ALLOW_DETACH);
    this.scene.addChild(branchDeplace);
    // Lumiere attenuee (ex : source lumineuse en un point)
    PointLight pointlight = new PointLight();
    pointlight.setEnable(true);
    pointlight.setColor(new Color3f(1f, 1f, 1f));
    pointlight.setPosition(new Point3f(2500f, 2500f, 250.0f));
    pointlight.setAttenuation(0f, 0f, 0f);
    pointlight.setInfluencingBounds(new BoundingSphere(new Point3d(), Double.POSITIVE_INFINITY));
    pointlight.setCapability(Light.ALLOW_COLOR_READ);
    pointlight.setCapability(Light.ALLOW_COLOR_WRITE);
    pointlight.setCapability(Light.ALLOW_STATE_READ);
    pointlight.setCapability(Light.ALLOW_STATE_WRITE);
    pointlight.setCapability(PointLight.ALLOW_ATTENUATION_READ);
    pointlight.setCapability(PointLight.ALLOW_ATTENUATION_WRITE);
    pointlight.setCapability(PointLight.ALLOW_POSITION_READ);
    pointlight.setCapability(PointLight.ALLOW_POSITION_WRITE);
    BranchGroup bgTempL = new BranchGroup();
    bgTempL.setCapability(Node.ALLOW_PICKABLE_READ);
    bgTempL.setCapability(Node.ALLOW_PICKABLE_WRITE);
    bgTempL.setCapability(Node.ENABLE_PICK_REPORTING);
    bgTempL.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    bgTempL.setCapability(Group.ALLOW_CHILDREN_READ);
    bgTempL.setCapability(Group.ALLOW_CHILDREN_WRITE);
    bgTempL.setCapability(BranchGroup.ALLOW_DETACH);
    bgTempL.addChild(pointlight);
    this.lLights.add(pointlight);
    AmbientLight amb = new AmbientLight();
    amb.setEnable(true);
    amb.setBounds(new BoundingSphere(new Point3d(0, 0, 0), Double.POSITIVE_INFINITY));
    amb.setColor(new Color3f(1f, 1f, 1f));
    amb.setCapability(Light.ALLOW_STATE_WRITE);
    amb.setCapability(Light.ALLOW_COLOR_WRITE);
    amb.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), Double.POSITIVE_INFINITY));
    // this.scene.addChild(amb);
    // Ajout des àclairages
    // this.scene.addChild(bgTempL);
    // locale.addBranchGraph(amb);
    this.locale.addBranchGraph(bgTempL);
    this.locale.addBranchGraph(this.bgvu);
    this.locale.addBranchGraph(this.scene);
  }

  /**
   * BranchGroup permettant de se déplacer dans la scene en 3D
   * @param transGroup
   *        le TransformGroup qui sera mis à jour avec les
   *        déplacements
   * @return
   */
  private BranchGroup interscene(TransformGroup transGroup) {
    // définition du Tg pour les intéractions souris et clavier
    BranchGroup bg = new BranchGroup();
    bg.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    bg.setCapability(Group.ALLOW_CHILDREN_READ);
    bg.setCapability(Group.ALLOW_CHILDREN_WRITE);
    BoundingSphere souris = null;
    // Création d'une transformation pour permettre une mise à l'échelle
    this.scale = new Transform3D();
    this.scale.setScale(ConstantRepresentation.scaleFactor);
    InterfaceMap3D.tgScale = new TransformGroup(this.scale);
    InterfaceMap3D.tgScale.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    // Création d'une transformation pour permettre une mise à l'échelle
    Transform3D echelleZ = new Transform3D();
    echelleZ.setScale(new Vector3d(1.0, 1.0, ConstantRepresentation.scaleFactorZ));
    InterfaceMap3D.tgScaleZ = new TransformGroup(echelleZ);
    InterfaceMap3D.tgScaleZ.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    InterfaceMap3D.tgScale.addChild(this.Bgeneral);
    InterfaceMap3D.tgScaleZ.addChild(InterfaceMap3D.tgScale);
    bg.addChild(InterfaceMap3D.tgScaleZ);
    souris = new BoundingSphere(new Point3d(), Double.POSITIVE_INFINITY);
    // rotation
    MouseRotate srotate = new MouseRotate(MouseBehavior.INVERT_INPUT);
    srotate.setTransformGroup(transGroup);
    srotate.setSchedulingBounds(souris);
    srotate.setFactor(0.01);
    bg.addChild(srotate);
    // translation
    MouseTranslate stranslate = new MouseTranslate(MouseBehavior.INVERT_INPUT);
    stranslate.setFactor(1.5);
    stranslate.setTransformGroup(transGroup);
    stranslate.setSchedulingBounds(souris);
    bg.addChild(stranslate);
    // Clavier
    KeyNavigatorBehavior keynavbehavior = new KeyNavigatorBehavior(transGroup);
    keynavbehavior.setSchedulingBounds(souris);
    bg.addChild(keynavbehavior);
    // Compiler
    // bg.compile();
    return bg;
  }

  /**
   * Permet d'ajouter une couche dans l'interface
   * @param layer
   *        la couche que l'on souhaite ajouter
   */
  public void addLayerInterface(Layer layer) {
    System.out.println("addLayerInterface " + layer.getLayerName());
    if (this.translate == null) {
      Box3D b = layer.get3DEnvelope();
      double x = (b.getLLDP().getX() + b.getURDP().getX()) / 2;
      double y = (b.getLLDP().getY() + b.getURDP().getY()) / 2;
      double z = (b.getLLDP().getZ() + b.getURDP().getZ()) / 2;
      if (Double.isNaN(z) || Double.isInfinite(z)) {
        z = 0;
      }
      this.translate = new Vector3f((float) -x, (float) -y, (float) -z);
      Transform3D translcentre = new Transform3D();
      translcentre.set(this.translate);
      this.tgtranscenter = new TransformGroup(translcentre);
      this.tgtranscenter.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      this.tgtranscenter.addChild(this.map.getBgCarte());
      BranchGroup Bg_layers = new BranchGroup();
      Bg_layers.setCapability(Group.ALLOW_CHILDREN_READ);
      Bg_layers.setCapability(Group.ALLOW_CHILDREN_EXTEND);
      Bg_layers.setCapability(Group.ALLOW_CHILDREN_WRITE);
      Bg_layers.setCapability(BranchGroup.ALLOW_DETACH);
      Bg_layers.addChild(this.tgtranscenter);
      this.Bgeneral.addChild(Bg_layers);
      // On initialise la derrière au centre très haut
      this.initViewpoint(5);
      this.moveLight(0, 0, b.getURDP().getZ(), 0);
    } else
      if (this.map.getLayerList().size() == 0) {
        Box3D b = layer.get3DEnvelope();
        double x = (b.getLLDP().getX() + b.getURDP().getX()) / 2;
        double y = (b.getLLDP().getY() + b.getURDP().getY()) / 2;
        double z = (b.getLLDP().getZ() + b.getURDP().getZ()) / 2;
        if (Double.isNaN(z) || Double.isInfinite(z)) {
          z = 0;
        }
        this.translate.x = (float) x;
        this.translate.y = (float) y;
        this.translate.z = (float) z;
        Transform3D translcentre = new Transform3D();
        translcentre.set(this.translate);
        this.tgtranscenter.setTransform(translcentre);
        this.initViewpoint(5);
        this.moveLight(b.getURDP().getX() * 2, b.getURDP().getY() * 2, b.getURDP().getZ() * 2, 0);
      }
    AbstractTableModel abstm = (AbstractTableModel) this.mainWindow.getContentPanel()
        .getLayersListTable().getModel();
    abstm.fireTableDataChanged();
  }

  /**
   * Permet d'initialiser un point de vue (1) Vue depuis l'axe des XMax de la
   * scene (2) Vue depuis l'axe des XMin de la scene (3) Vue depuis l'axe des
   * YMax de la scene (4) Vue depuis l'axe des YMin de la scene (5) Vue depuis
   * l'axe des ZMax de la scene (6) Vue depuis l'axe des ZMin de la scene
   * @param axis
   *        Un entier correspondant à l'axe
   * @return la distance entre la caméra et l'objet
   */
  public double initViewpoint(int axis) {
    Transform3D viewTrans = new Transform3D();
    BoundingSphere sceneBounds = new BoundingSphere(this.Bgeneral.getBounds());
    // point the view at the center of the object
    Point3d center = new Point3d();
    sceneBounds.getCenter(center);
    double radius = sceneBounds.getRadius();
    // On effectue cette Opération afin d'avoir toujours quelque chose à
    // l'écran
    radius = Math.min(radius, ConstantRepresentation.backClip * 2);
    Point3d eyePos = new Point3d(center);
    Vector3d up = new Vector3d();
    // pull the eye back far enough to see the whole object
    double eyeDist = radius;
    switch (axis) {
      case 1:
        eyePos.x += eyeDist;
        up.z = 1;
        break;
      case 2:
        eyePos.x -= eyeDist;
        up.z = 1;
        break;
      case 3:
        eyePos.y += eyeDist;
        up.z = 1;
        break;
      case 4:
        eyePos.y -= eyeDist;
        up.z = 1;
        break;
      case 5:
        eyePos.z += eyeDist;
        up.y = 1;
        break;
      case 6:
        eyePos.z -= eyeDist;
        up.y = 1;
        break;
    }
    viewTrans.setIdentity();
    viewTrans.lookAt(eyePos, center, up);
    viewTrans.invert();
    // set the view transform
    InterfaceMap3D.tgvu.setTransform(viewTrans);
    return eyeDist;
  }

  /**
   * Permet de déplacer de manière à centrer sur le point P(x y z )
   * @param x
   * @param y
   * @param z
   * @param direction
   *        Il s'agit de la direction dans laquelle est regardée le
   *        point. La caméra se trouvera à la translation vecteur appliqué à P
   *        La norme du vecteur indique la distance entre la caméra et le
   *        point
   */
  public void zoomOn(double x, double y, double z, Vecteur direction) {
    Transform3D viewTrans = new Transform3D();
    if (direction == null) {
      return;
    }
    if (direction.norme() == 0) {
      return;
    }
    // point the view at the center of the object
    Point3d center = new Point3d(x + this.translate.x, y + this.translate.y, z + this.translate.z);
    Point3d eyePos = new Point3d(x + this.translate.x + direction.getX(), y + this.translate.y
        + direction.getY(), z + this.translate.z + direction.getZ());
    viewTrans.setIdentity();
    viewTrans.lookAt(eyePos, center, new Vector3d(0, 0, 1));
    // set the view transform
    viewTrans.invert();
    InterfaceMap3D.tgvu.setTransform(viewTrans);
  }

  /**
   * @return l'Application dans laquelle est affiché la carte
   */
  public MainWindow getMainWindow() {
    return this.mainWindow;
  }

  /**
   * Permet d'appliquer un facteur d'échelle en Z à la carte
   * @param echelleZ
   */
  public void setFacteurZ(double echelleZ) {
    ConstantRepresentation.scaleFactorZ = echelleZ;
    // Création d'une transformation pour permettre une mise à l'échelle
    Transform3D tEchelleZ = new Transform3D();
    tEchelleZ.setScale(new Vector3d(1.0, 1.0, ConstantRepresentation.scaleFactorZ));
    InterfaceMap3D.tgScaleZ.setTransform(tEchelleZ);
  }

  /**
   * 
   */
  public void addLight(Color couleur, float x, float y, float z) {
    // Lumiere attenuee (ex : source lumineuse en un point)
    PointLight pointlight = new PointLight();
    pointlight.setEnable(true);
    pointlight.setColor(new Color3f(couleur));
    System.out.println(this.getTranslate());
    pointlight.setPosition(new Point3f(x + this.getTranslate().x, y + +this.getTranslate().y, z
        + +this.getTranslate().z));
    pointlight.setAttenuation(0f, 0f, 0f);
    pointlight.setInfluencingBounds(new BoundingSphere(new Point3d(), Double.POSITIVE_INFINITY));
    pointlight.setCapability(Light.ALLOW_COLOR_READ);
    pointlight.setCapability(Light.ALLOW_COLOR_WRITE);
    pointlight.setCapability(Light.ALLOW_STATE_READ);
    pointlight.setCapability(Light.ALLOW_STATE_WRITE);
    pointlight.setCapability(PointLight.ALLOW_ATTENUATION_READ);
    pointlight.setCapability(PointLight.ALLOW_ATTENUATION_WRITE);
    pointlight.setCapability(PointLight.ALLOW_POSITION_READ);
    pointlight.setCapability(PointLight.ALLOW_POSITION_WRITE);
    BranchGroup bgTempL = new BranchGroup();
    bgTempL.setCapability(Node.ALLOW_PICKABLE_READ);
    bgTempL.setCapability(Node.ALLOW_PICKABLE_WRITE);
    bgTempL.setCapability(Node.ENABLE_PICK_REPORTING);
    bgTempL.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    bgTempL.setCapability(Group.ALLOW_CHILDREN_READ);
    bgTempL.setCapability(Group.ALLOW_CHILDREN_WRITE);
    bgTempL.setCapability(BranchGroup.ALLOW_DETACH);
    bgTempL.addChild(pointlight);
    this.getLights().add(pointlight);
    this.getScene().addChild(bgTempL);
  }

  /**
   * Suppression de la lumière
   * @param i
   * @return
   */
  public boolean removeLight(int i) {
    if (i >= this.getLights().size()) {
      return false;
    }
    ((BranchGroup) this.getLights().get(i).getParent()).detach();
    this.getLights().remove(i);
    return true;
  }

  /**
   * Permet de placer la derrière en x y z Il s'agit d'une derrière de type
   * point ligth (comme le soleil ...)
   * @param x
   *        nouvelle coordonnées x
   * @param y
   *        nouvelle coordonnées y
   * @param z
   *        nouvelle coordonnées z
   * @param ind
   *        l'index de la lumière que l'on déplace
   * @return indique si l'opération a été effectuée
   */
  public boolean moveLight(double x, double y, double z, int ind) {
    if (ind > this.lLights.size() - 1) {
      return false;
    }
    this.lLights.get(ind).setPosition(
        new Point3f((float) x + this.getTranslate().x, (float) y + this.getTranslate().y, (float) z
            + this.getTranslate().z));
    return true;
  }

  /**
   * @return Renvoie la liste des lumières de la scene
   */
  public List<PointLight> getLights() {
    return this.lLights;
  }

  /**
   * Ajoute un objet à la selection en cours
   * @param feat
   */
  public void addToSelection(IFeature feat) {
    if (this.getSelection().contains(feat)) {
      return;
    }
    if (feat == null) {
      return;
    }
    ((I3DRepresentation) feat.getRepresentation()).setSelected(true);
    this.getSelection().add(feat);
  }

  /**
   * Remplace la sélection courante par l'entité paramètre
   * @param feat
   *        l'entité que l'on souhaite sélectionner
   */
  public void setSelection(IFeature feat) {
    FT_FeatureCollection<IFeature> lObj = new FT_FeatureCollection<IFeature>();
    if (feat != null) {
      lObj.add(feat);
    }
    this.setSelection(lObj);
    lObj.clear();
  }

  /**
   * Remplace la sélection courante par une liste
   * @param feats
   *        une liste d'entités que l'on souhaite sélectionner
   */

  public void setSelection(IFeatureCollection<IFeature> feats) {
    // On enlève l'ancienne sélection
    int nbelem = this.getSelection().size();
    if (nbelem != 0) {
      // On déselectionne toutes les entités de la selection courante
      for (int i = 0; i < nbelem; i++) {
        IFeature feat = this.selection.get(i);
        Representation rep = feat.getRepresentation();
        if (rep == null) {
          InterfaceMap3D.logger.warn(Messages.getString("Representation.RepNulle"));
          continue;
        }
        if (rep instanceof I3DRepresentation) {
          ((I3DRepresentation) rep).setSelected(false);
        } else {
          InterfaceMap3D.logger.warn(Messages.getString("Representation.RepUnk"));
        }
      }
      this.selection.clear();
    }

    int nb = feats.size();
    // On ajoute et selectionne les nouvelles entites
    for (int i = 0; i < nb; i++) {
      IFeature featTemp = feats.get(i);
      Representation rep = featTemp.getRepresentation();
      if (rep == null) {
        InterfaceMap3D.logger.warn(Messages.getString("Representation.RepNulle"));
        continue;
      }
      if (rep instanceof I3DRepresentation) {
        ((I3DRepresentation) rep).setSelected(true);
        this.selection.add(featTemp);
      } else {
        InterfaceMap3D.logger.warn(Messages.getString("Representation.RepUnk"));
      }
    }
  }

  /**
   * @return Permet d'obtenir la liste des objets sélectionnés Pour l'instant un
   *         seul
   */

  public IFeatureCollection<IFeature> getSelection() {
    if (this.selection == null) {
      this.selection = new FT_FeatureCollection<IFeature>();
    }
    return this.selection;
  }

  /**
   * Supprime les objets selectionnes
   */
  public void suppressSelection() {
    this.deleteFeatureCollection(this.selection);
    this.selection.clear();
  }

  /**
   * Enleve une liste d'entité de la selection
   * @param featColl
   */
  public void deleteFeatureCollection(FT_FeatureCollection<IFeature> featColl) {
    // Permet de supprimer une liste d'objets
    int nobj = featColl.size();
    if (nobj == 0) {
      return;
    }
    for (int i = 0; i < nobj; i++) {
      // la liste featColl se met à jour automatiquement
      IFeature objTemp = featColl.get(0);
      Representation rep = objTemp.getRepresentation();
      if (rep instanceof I3DRepresentation) {
        I3DRepresentation basrep3D = (I3DRepresentation) rep;
        basrep3D.getBGRep().detach();
      }
      List<IFeatureCollection<IFeature>> lFeatC = objTemp.getFeatureCollections();
      if (lFeatC != null && lFeatC.size() != 0) {
        int nbColl = lFeatC.size();
        for (int j = 0; j < nbColl; j++) {
          // La liste lFeatC se met automatiquement à jour
          IFeatureCollection<IFeature> featCollTemp = lFeatC.get(0);
          featCollTemp.remove(objTemp);
        }
      } else {
        int nbLayer = this.getCurrent3DMap().getLayerList().size();
        for (int j = 0; j < nbLayer; j++) {
          Layer l = this.getCurrent3DMap().getLayerList().get(j);
          if (l instanceof VectorLayer) {
            VectorLayer vL = (VectorLayer) l;
            vL.remove(objTemp);
          }
        }
      }
    }
    featColl.clear();
  }

  /**
   * Permet d'enregistrer une image à partir de l'écran Ne fonctionne qu'avec
   * l'IHM actuel (Offset nécessaire) Ne prends pas compte de l'existance d'un
   * fichier de même nom
   * @param path
   *        le dossier dans lequel l'impr ecran sera supprime
   * @param fileName
   *        le nom du fichier
   * @return indique si la capture s'est effectuée avec succès
   */
  public boolean screenCapture(String path, String fileName) {
    try {
      BufferedImage bufImage = new BufferedImage(this.getSize().width, this.getSize().height,
          BufferedImage.TYPE_INT_RGB);
      this.paint(bufImage.createGraphics());
      File fichier = new File(path, fileName);
      if (fichier.exists()) {
        InterfaceMap3D.logger.warn(Messages.getString("ExportImage.Fail"));
        return false;
      } else {
        ImageIO.write(bufImage, "jpg", fichier);
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * @return Renvoie l'instance de Carte3D affichée dans l'interface
   */
  public Map3D getCurrent3DMap() {
    return this.map;
  }

  /**
   * @return La translation appliquée aux objets de de l'espace
   */
  public Vector3f getTranslate() {
    return this.translate;
  }

  /**
   * @return la vue de l'interface
   */
  public View getView() {
    return this.view;
  }

  /**
   * Renvoie les paramètres de fond de carte
   */
  public Background getBackground3D() {
    return this.background3D;
  }

  /**
   * @return Returns the behaviour.
   */
  public Behavior getBehaviour() {
    return this.behaviour;
  }

  /**
   * @return Le BranchGroup sur lequel sont rattachés les noeuds des couches
   */
  public BranchGroup getBgeneral() {
    return this.Bgeneral;
  }

  /**
   * @return la branche de visualisation de la scène
   */
  public BranchGroup getBgvu() {
    return this.bgvu;
  }

  /**
   * @return l'objet JCanvas3D utilisé pour le rendu de la scène RD
   */
  public JCanvas3D getCanvas3D() {
    return this.canvas3D;
  }

  /**
   * @return la scène 3D globale
   */
  public BranchGroup getScene() {
    return this.scene;
  }

  /**
   * @return les informations de transformations liées à la position de la
   *         caméra dans la scène 3D
   */
  public static TransformGroup getTgvu() {
    return InterfaceMap3D.tgvu;
  }

  /**
   * Desttruction de l'univers
   */
  public void close() {
    this.view.removeAllCanvas3Ds();
    this.view.attachViewPlatform(null);
    this.locale.getVirtualUniverse().removeAllLocales();
  }
}
