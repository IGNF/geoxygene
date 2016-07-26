package fr.ign.cogit.geoxygene.sig3d.representation.modellingfile;

import java.util.ArrayList;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.PointArray;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.representation.Default3DRep;
import fr.ign.cogit.geoxygene.sig3d.representation.modellingfile.manager.Manager3DS;
import fr.ign.cogit.geoxygene.sig3d.representation.modellingfile.manager.ManagerObj;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

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
 * Classe permettant d'associer a un objet ponctuel à un BranchGroup qui fera
 * office de symbole. Ne fonctionne qu'avec des features ayant une géométrie de
 * type GM_Point ou GM_MultiPoint En standard ce type de représentation est
 * utilisé pour charger des formats de modélisation (3DS ou .OBJ) et appliquer
 * une représentation aux objets ponctuels On applique des paramètres de
 * transformations à ces objets - Translation pour correspondre aux coordonnées
 * de l'objet ponctuel - Rotation suivant les axes X,Y,Z - Facteur d'échelle
 * Néanmoins n'importe quel BG peut être utilisé dans cette classe. Elle est
 * donc àtendable pour permettre d'autres types de représentation Le chemin est
 * de la forme "C:/" Class that enable to represent 0D objects by a object from
 * modelling software
 */
public class RepresentationModel extends Default3DRep {

  private final static Logger logger = Logger
      .getLogger(RepresentationModel.class.getName());

  /**
   * créer le lien entre un BranchGroup quelconque et une entité Une rotation de
   * PI/2 est appliqué suivant les vecteurs X et Y afin de s'intégrer dans
   * l'espace A appliquer à des objets de dimension, le centre du symbole se
   * retrouvant aux coordonnées de (ou des) géométries
   * 
   * @param feat l'entité qui sera symbolisée
   * @param path le chemin du fichier de modélisation servant à représenter
   *          l'objet
   */
  public RepresentationModel(IFeature feat, String path) {
    this(feat, path, 0, 0, 0, 1);

  }

  private String path;
  private double angleRotX;
  private double angleRotY;
  private double angleRotZ;
  private double scaleFactor;

  /**
   * Cette classe permet d'associer à un point un symbol grâce au chemin d'un
   * fichier de modélisation (format 3DS ou OBJ) en appliquant des paramètres de
   * transformations A appliquer à des objets de dimension, le centre du symbole
   * se retrouvant aux coordonnées de (ou des) géométries
   * 
   * @param feat Il s'agit de l'entité utilisé pour créer sa représentation
   * @param path le chemin du fichier de modélisation servant à représenter
   *          l'objet
   * @param angleRotX Rotation autour de l'axe des X (rad)
   * @param angleRotY Rotation autour de l'axe des Y (rad)
   * @param angleRotZ Rotation autour de l'axe des Z (rad)
   * @param scaleFactor Facteur d'échelle à appliquer
   */
  public RepresentationModel(IFeature feat, String path, double angleRotX,
      double angleRotY, double angleRotZ, double scaleFactor) {
    this.feat = feat;
    this.path = path;
    this.angleRotX = angleRotX;
    this.angleRotY = angleRotY;
    this.angleRotZ = angleRotZ;
    this.scaleFactor = scaleFactor;
    // On recupere un manager pour charger les objets
    IManagerModel manager = null;

    // On instancie en fonction du type de fichier
    if (path.toUpperCase().contains(".3DS")) { //$NON-NLS-1$
      manager = Manager3DS.getInstance();

    } else if (path.toUpperCase().contains(".OBJ")) { //$NON-NLS-1$
      manager = ManagerObj.getInstance();
    }

    if (manager == null) {
      RepresentationModel.logger.error(Messages
          .getString("FenetreChargement.UnknownFormat"));
      return;
    }

    BranchGroup bgModel = manager.loadingFile(path);

    if (bgModel == null) {
      RepresentationModel.logger.error(Messages
          .getString("FenetreChargement.Error"));
      return;
    }

    ArrayList<TransformGroup> lTG = this.geometry(bgModel, angleRotX,
        angleRotY, angleRotZ, scaleFactor);

    // Passer les informations de l'objet au BG
    int nbSphere = lTG.size();

    // On ajoute les formes à la Branch Group
    for (int i = 0; i < nbSphere; i++) {

      this.bGRep.addChild(lTG.get(i));

    }

    // Optimisation
    this.bGRep.compile();

  }

  /**
   * Permet de créer les objets Java3D en fonction des paramètres
   * 
   * @param shapeIni
   * @param angleRotX
   * @param angleRotY
   * @param angleRotZ
   * @param scaleFactor
   * @return
   */
  private ArrayList<TransformGroup> geometry(BranchGroup shapeIni,
      double angleRotX, double angleRotY, double angleRotZ, double scaleFactor) {
    // On récupère la géométrie qui doit être de type ponctuel
    IGeometry geom = this.feat.getGeom();

    if (geom instanceof GM_MultiPoint) {

      // On peut àventuellement vouloir ajouter des traitements

    } else if (geom instanceof GM_Point) {

      // On peut àventuellement vouloir ajouter des traitements
    } else {
      RepresentationModel.logger.warn(Messages
          .getString("Representation.GeomUnk"));
      return null;
    }

    IDirectPositionList lDP = geom.coord();

    int nbPoints = lDP.size();

    PointArray pA = new PointArray(nbPoints, GeometryArray.COORDINATES
        | GeometryArray.COLOR_3);

    ArrayList<TransformGroup> lTG = new ArrayList<TransformGroup>(nbPoints);

    pA.setCapability(GeometryArray.ALLOW_COLOR_READ);
    pA.setCapability(GeometryArray.ALLOW_COLOR_WRITE);

    for (int i = 0; i < nbPoints; i++) {

      BranchGroup enumChild = (BranchGroup) shapeIni.cloneTree();

      IDirectPosition pTemp = lDP.get(i);

      // Rotation en X, en Y et en Z
      Transform3D rotX = new Transform3D();
      rotX.rotX(angleRotX);
      TransformGroup tgRotX = new TransformGroup(rotX);
      tgRotX.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

      Transform3D rotY = new Transform3D();
      rotY.rotY(angleRotY);
      TransformGroup tgRotY = new TransformGroup(rotY);
      tgRotY.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

      Transform3D rotZ = new Transform3D();
      rotZ.rotZ(angleRotZ);
      TransformGroup tgRotZ = new TransformGroup(rotZ);
      tgRotZ.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

      // Création d'une transformation pour permettre une mise à l'échelle
      Transform3D echelleZ = new Transform3D();
      echelleZ.setScale(new Vector3d(scaleFactor, scaleFactor, scaleFactor));
      TransformGroup tgechelleZ = new TransformGroup(echelleZ);

      // On place le sphère aux bonnes coordonnées
      Transform3D translate = new Transform3D();
      translate.set(new Vector3f((float) pTemp.getX(), (float) pTemp.getY(),
          (float) pTemp.getZ()));

      TransformGroup TG1 = new TransformGroup(translate);
      tgechelleZ.addChild(enumChild);
      tgRotZ.addChild(tgechelleZ);
      tgRotY.addChild(tgRotZ);
      tgRotX.addChild(tgRotY);
      TG1.addChild(tgRotX);

      lTG.add(TG1);

    }

    return lTG;

  }

  public String getPath() {
    return this.path;
  }

  public double getAngleRotX() {
    return this.angleRotX;
  }

  public double getAngleRotY() {
    return this.angleRotY;
  }

  public double getAngleRotZ() {
    return this.angleRotZ;
  }

  public double getScaleFactor() {
    return this.scaleFactor;
  }

}
