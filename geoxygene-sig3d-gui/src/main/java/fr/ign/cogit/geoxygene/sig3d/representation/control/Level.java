package fr.ign.cogit.geoxygene.sig3d.representation.control;

import java.awt.Color;
import java.awt.Font;

import javax.media.j3d.Appearance;
import javax.media.j3d.Billboard;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DistanceLOD;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.GeometryInfo;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.sig3d.representation.ConstantRepresentation;
import fr.ign.cogit.geoxygene.sig3d.representation.Default3DRep;


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
 * Classe permettant de définir un objet niveau composé : - d'un plan horizontal
 * - d'information toponymiques
 * 
 */
public class Level extends Default3DRep {
  // La Branche contenant l'ensemble des toponymes
  BranchGroup bgToponymes;

  public static float OPACITY = 0.5f;

  // Des transformations (permettant de gérer "en live" le déplacement suivant
  // l'axe des z)
  TransformGroup tg2;
  Transform3D trans3;

  /**
   * Création d'une représentation de type niveau
   * 
   * @param feat l'entité à laquelle est associée le niveau
   * @param withToponym indique si les coordonnées sont représentées
   * @param z l'altitude à laquelle se trouve le niveau
   * @param policeColor la couleur de la police
   * @param rectangleColor la couleur du rectangle
   * @param inter le nombre de fois qu'apparaitront les informations de
   *          coordonnées
   * @param textSize la taille du texte
   * @param gridMod mode grille
   * @param distance la distance à partir de laquelle les coordonnées
   *          s'affichent
   */
  public Level(IFeature feat, boolean withToponym, double z, Color policeColor,
      Color rectangleColor, int inter, int textSize, boolean gridMod,
      double distance) {
    super();
    // On créé le lien représentation entité
    this.feat = feat;
    feat.setRepresentation(this);

    // On associe au graphe de réprésentation celui qui correspond au niveau
    this.bGRep.addChild(this.generateAll(withToponym, z, policeColor,
        rectangleColor, inter, textSize, gridMod, distance));
  }

  /**
   * Fonction permettant de mettre à jour la représentation d'un niveau (et donc
   * de faire des modifications dans la fenetre
   * 
   * @param withToponym indique si les coordonnées sont représentées
   * @param z l'altitude à laquelle se trouve le niveau
   * @param couleurPolice la couleur de la police
   * @param couleurRectangle la couleur du rectangle
   * @param inter le nombre de fois qu'apparaitront les informations de
   *          coordonnées
   * @param tailleTexte la taille du texte
   * @param modeGrille mode grille
   * @param distance la distance à partir de laquelle les coordonnées
   *          s'affichent
   */
  public void update(boolean withToponym, double z, Color couleurPolice,
      Color couleurRectangle, int inter, int tailleTexte, boolean modeGrille,
      double distance) {

    // On retire les branches liées à la représentation
    if (this.tg2.isLive()) {

      ((BranchGroup) this.tg2.getParent()).detach();

    }
    // On rattache une nouvelle branche
    this.bGRep.addChild(this.generateAll(withToponym, z, couleurPolice,
        couleurRectangle, inter, tailleTexte, modeGrille, distance));
  }

  /**
   * Déplacement la représentation à une altitude Z
   * 
   * @param z
   */
  public void setZ(double z) {
    // On modifie les paramètres de transformation verticle
    this.trans3.setTranslation(new Vector3f((0), 0, (float) z));
    this.tg2.setTransform(this.trans3);
  }

  /**
   * Fonction permettant de créer un BranchGroup correspondant à la
   * représentation d'un niveau
   * 
   * @param withToponym indique si les coordonnées sont représentées
   * @param z l'altitude à laquelle se trouve le niveau
   * @param policeColor la couleur de la police
   * @param rectangleColor la couleur du rectangle
   * @param inter le nombre de fois qu'apparaitront les informations de
   *          coordonnées
   * @param textSize la taille du texte
   * @param gridMod mode grille
   * @param distance la distance à partir de laquelle les coordonnées
   *          s'affichent
   * @return
   */
  private Group generateAll(boolean withToponym, double z, Color policeColor,
      Color rectangleColor, int inter, int textSize, boolean gridMod,
      double distance) {
    // On récupère les coordonnées de l'emprise de la carte
    Box3D b = new Box3D(this.feat.getGeom());

    IDirectPosition pMin = b.getLLDP();
    IDirectPosition pMax = b.getURDP();

    double xMin = pMin.getX();
    double yMin = pMin.getY();

    double xMax = pMax.getX();
    double yMax = pMax.getY();

    // On donne les capacités au BranchGoup Toponymes de se détacher
    // Utile pour enlever les coordonnées
    this.bgToponymes = new BranchGroup();
    this.bgToponymes.setCapability(BranchGroup.ALLOW_DETACH);
    this.bgToponymes.setCapability(Group.ALLOW_CHILDREN_READ);
    this.bgToponymes.setCapability(Group.ALLOW_CHILDREN_WRITE);
    this.bgToponymes.setCapability(Group.ALLOW_CHILDREN_EXTEND);

    // On calcule le nombre de toponymes représentés
    int interX = (int) (xMax - xMin) / inter;
    int interY = (int) (yMax - yMin) / inter;

    GeometryInfo geomInfo = new GeometryInfo(GeometryInfo.QUAD_ARRAY);
    int compteurIni = 0;
    for (int i = 0; i < xMax - xMin; i = i + interX) {

      for (int j = 0; j < yMax - yMin; j = j + interY) {

        // Construction de la géométrie niveau
        if (i == 0 || j == 0) {

          continue;
        }
        compteurIni = compteurIni + 4;

      }

    }

    // On initialise la géométrie du niveau
    Point3d[] tabpoints = new Point3d[compteurIni];
    Color4f[] couleurs = new Color4f[compteurIni];
    Vector3f[] normal = new Vector3f[compteurIni];

    Color4f c = new Color4f(rectangleColor);
    Vector3f norm = new Vector3f(0, 0, 1);
    int compteur = 0;

    // On parcourt les différents noeuds et toponyms
    for (int i = 0; i < xMax - xMin; i = i + interX) {

      for (int j = 0; j < yMax - yMin; j = j + interY) {
        // On calcule les coordonnées
        double x = xMin + i;
        double y = yMin + j;
        // On ajoute un toponyme
        Group g = Level.generateLabel(x, y, "(" + x + "," + y + ")",
            policeColor, (float) distance, textSize);
        this.bgToponymes.addChild(g);

        // Construction de la géométrie niveau
        if (i == 0 || j == 0) {

          continue;
        }

        // On créer les éléments de la grille
        tabpoints[compteur] = new Point3d(x, y, 0);
        couleurs[compteur] = c;
        normal[compteur] = norm;
        compteur++;

        tabpoints[compteur] = new Point3d(x - interX, y, 0);
        couleurs[compteur] = c;
        normal[compteur] = norm;
        compteur++;

        tabpoints[compteur] = new Point3d(x - interX, y - interY, 0);
        couleurs[compteur] = c;
        normal[compteur] = norm;
        compteur++;

        tabpoints[compteur] = new Point3d(x, y - interY, 0);
        couleurs[compteur] = c;
        normal[compteur] = norm;
        compteur++;

      }
    }
    // On finalise la géométrie
    geomInfo.setCoordinates(tabpoints);
    geomInfo.setColors(couleurs);
    geomInfo.setNormals(normal);
    geomInfo.recomputeIndices();

    // On crée l'apparence associée au niveau
    Appearance app = Level.generateApparence(rectangleColor, Level.OPACITY,
        !gridMod);

    // On créer la géométrie Java3D
    Shape3D s = new Shape3D(geomInfo.getGeometryArray(), app);

    // On applique la transformation verticale
    this.trans3 = new Transform3D();
    this.trans3.setTranslation(new Vector3f((0), 0, (float) z));
    this.tg2 = new TransformGroup(this.trans3);
    this.tg2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    this.tg2.setCapability(Group.ALLOW_CHILDREN_READ);
    this.tg2.setCapability(Group.ALLOW_CHILDREN_WRITE);
    this.tg2.setCapability(Group.ALLOW_CHILDREN_EXTEND);

    // On attache à la transformation verticale niveau et toponymes
    BranchGroup bgTemp = new BranchGroup();
    bgTemp.setCapability(BranchGroup.ALLOW_DETACH);
    bgTemp.addChild(s);

    this.bgToponymes.compile();
    this.tg2.addChild(this.bgToponymes);
    this.tg2.addChild(bgTemp);

    // On ernvoie une branche principale
    BranchGroup branchPrincipale = new BranchGroup();
    branchPrincipale.setCapability(BranchGroup.ALLOW_DETACH);
    branchPrincipale.setCapability(Group.ALLOW_CHILDREN_READ);
    branchPrincipale.setCapability(Group.ALLOW_CHILDREN_WRITE);
    branchPrincipale.setCapability(Group.ALLOW_CHILDREN_EXTEND);

    branchPrincipale.addChild(this.tg2);

    return branchPrincipale;
  }

  /**
   * Affiche ou cache les toponymes d'un niveau
   * 
   * @param isDisplayed
   */
  public void visualizeCoordinates(boolean isDisplayed) {

    // On détache ou rattache la branche
    if (isDisplayed) {
      if (!this.bgToponymes.isLive()) {

        this.tg2.addChild(this.bgToponymes);
      }

    } else {
      if (this.bgToponymes.isLive()) {

        this.bgToponymes.detach();
      }

    }
  }

  /**
   * Fonction permettant de créer un label
   * 
   * @param x
   * @param y
   * @param text
   * @param color
   * @param distance distance d'apparition
   * @param textSize taille du texte
   * @return distance à partir de laquelle apparaitra l'objet
   */
  private static Group generateLabel(double x, double y, String text,
      Color color, float distance, int textSize) {

    // On prend une font de type Arial
    Font3D f3d = new Font3D(new Font("Arial", Font.PLAIN, 1),
        new FontExtrusion());

    // On crée le texte 3D
    Text3D t = new Text3D(f3d, text, new Point3f(0, 0, 0), Text3D.ALIGN_CENTER,
        Text3D.PATH_RIGHT);

    // On le dimensionne
    Transform3D trans1 = new Transform3D();
    trans1.setScale(new Vector3d(textSize, textSize, textSize / 3));

    TransformGroup tg = new TransformGroup(trans1);

    // On le place au bon endroit
    Transform3D trans = new Transform3D();
    trans.setTranslation(new Vector3d(x, y, 0));
    TransformGroup tg2 = new TransformGroup(trans);

    // On lui applique une apparence
    Appearance ap = Level.generateApparence(color, 1, true);
    Shape3D s = new Shape3D(t, ap);

    // Create the transformgroup used for the billboard
    TransformGroup billBoardGroup = new TransformGroup();
    // Set the access rights to the group
    billBoardGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    // Add the cube to the group
    billBoardGroup.addChild(s);

    // Gestion du Billboard pour le voir toujours de face
    Billboard myBillboard = new Billboard(billBoardGroup,

    Billboard.ROTATE_ABOUT_POINT, new Vector3f());

    myBillboard.setSchedulingBounds(billBoardGroup.getBounds());

    tg.addChild(billBoardGroup);
    tg.addChild(myBillboard);

    // Création d'un switch permettant de gérer le LOD (disparition lorsque
    // l'objet est trop loin)
    Switch targetSwitch = new Switch();
    targetSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);

    // add visual objects to the target switch
    targetSwitch.addChild(tg);

    BoundingSphere bounds = new BoundingSphere();
    // create DistanceLOD object
    float[] distances = { distance };
    DistanceLOD dLOD = new DistanceLOD(distances);

    dLOD.setSchedulingBounds(bounds);
    dLOD.addSwitch(targetSwitch);

    tg2.addChild(targetSwitch);
    tg2.addChild(dLOD);

    return tg2;
  }

  /**
   * Permet de créer l'apparence en fonction de paramètres Dans le cadre d'un
   * ponctuel, certains paramètres n'ont aucun sens
   * 
   * @param color couleur
   * @param coefOpacity coefficient d'opacité
   * @param isSolid mode solide
   * @return
   */
  private static Appearance generateApparence(Color color, double coefOpacity,
      boolean isSolid) {

    // Création de l'apparence
    Appearance apparenceFinale = new Appearance();

    // Autorisations pour l'apparence
    apparenceFinale.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
    apparenceFinale.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

    // Autorisations pour le material
    apparenceFinale.setCapability(Appearance.ALLOW_MATERIAL_READ);
    apparenceFinale.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
    apparenceFinale.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
    apparenceFinale.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);

    // Création du material (gestion des couleurs et de l'affichage)
    Material material = new Material();

    Color3f col3f = new Color3f(color);

    material.setAmbientColor(col3f);
    material.setDiffuseColor(col3f);
    material.setEmissiveColor(col3f);
    material.setLightingEnable(true);
    material.setSpecularColor(col3f);
    material.setShininess(1);

    // et de material
    apparenceFinale.setMaterial(material);

    if (coefOpacity != 1) {

      TransparencyAttributes t_attr =

      new TransparencyAttributes(TransparencyAttributes.NICEST,
          (float) (1 - coefOpacity));

      apparenceFinale.setTransparencyAttributes(t_attr);
    }

    // Création des attributs du polygone
    PolygonAttributes pa = new PolygonAttributes();

    pa.setCullFace(PolygonAttributes.CULL_NONE);
    pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);

    if (isSolid) {

      pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);

      if (ConstantRepresentation.cullMode) {
        pa.setCullFace(PolygonAttributes.CULL_BACK);

      }

    } else {

      pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);

    }

    pa.setBackFaceNormalFlip(false);

    // Association à l'apparence des attributs de géométrie et de material
    apparenceFinale.setPolygonAttributes(pa);

    return apparenceFinale;

  }

}
