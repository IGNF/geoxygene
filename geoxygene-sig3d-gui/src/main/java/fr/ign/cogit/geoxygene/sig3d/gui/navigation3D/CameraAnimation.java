/**
 * Animates the camera from a list of points
 */
package fr.ign.cogit.geoxygene.sig3d.gui.navigation3D;

import java.util.logging.Logger;

import javax.media.j3d.Alpha;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Locale;
import javax.media.j3d.PositionPathInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;


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
 * Classe permettant de créer une animation pour la camera, la faisant parcourir
 * un certain nombre de points. La constante ALLER_RETOUR indique que la caméra
 * effectuera des allers/retours. ALLER_UNIQUEMENT indique que la caméra ne se
 * déplacera que dans un sens puis se téléportera une fois le dernier point
 * atteint.
 * 
 * Class to create an animation for the camera by navigating on a list of
 * points. Constant ALLER_RETOUR means that the camera will make some
 * roundabout. Constant ALLER_UNIQUEMENT means that the camera will go on one
 * way and teleport at the beginning when reaching the last point.
 * 
 */
public class CameraAnimation {

  private final static Logger logger = Logger.getLogger(CameraAnimation.class
      .getName());
  // Indique si une animation est déjà en train de tourner (pour eviter la
  // combinaison de plusieurs animations)
  private static boolean iSCameraRunning = false;

  // bgvu c'est le branche groupe supportant les informations relatives à la
  // vue
  private static BranchGroup bgvu;

  // Il s'agit du bg temporaire qui supportera l'animation
  private static BranchGroup bgTemp;

  // L'animation provoquera des allers/retours
  public final static int ALLER_RETOUR = 0;

  // Les points ne seront parcourus que dans un seul sens
  public final static int ALLER_UNIQUEMENT = 1;

  /**
   * 
   * Arrête une animation et remet le graphe de Scene comme il était avant
   * l'ajout des animations
   */
  public static void stopAnimation() {

    // On enlève le branchgroup actuel
    Locale l = CameraAnimation.bgTemp.getLocale();
    l.removeBranchGraph(CameraAnimation.bgTemp);

    // on détache le bg de vue de l'animation
    CameraAnimation.bgvu.detach();

    // on le rattache sans l'animation
    l.addBranchGraph(CameraAnimation.bgvu);

    // on indique qu'il n'y a plus d'animation
    CameraAnimation.iSCameraRunning = false;

  }

  /**
   * créer une animation de la caméra. Elle survolera une série de points (dpl)
   * d'une hauteur offset à une vitesse constante.
   * 
   * @param iMap3D la carte affichée à l'écran
   * @param dpl la liste des points parcourus
   * @param mode ALLER_RETOUR si on souhaite que des allers retours soient
   *          effectués le long du linéaire. ALLER_UNIQUEMENT si l'on souhaite
   *          seulement parcourir l'arc dans un seul sens
   * @param speed vitesse de parcourt, il s'agit du nombre d'unités
   *          (génèralement m) parcourues en 0.1 secondes
   * @param offset il s'agit de la hateur de survol de la caméra (par rapport
   *          aux points)
   */
  public static void animeCamera(InterfaceMap3D iMap3D,
      IDirectPositionList dpl, int mode, double speed, double offset) {

    // On arrréte l'animation en cours
    if (CameraAnimation.iSCameraRunning) {
      CameraAnimation.stopAnimation();
    }

    if (dpl == null || dpl.size() < 2) {
      CameraAnimation.logger.warning(Messages
          .getString("FenetreAnimationCamera.2PointsNecessary"));
      return;
    }
    CameraAnimation.logger.info(Messages
        .getString("FenetreAnimationCamera.Creation"));

    DirectPositionList dplTemp = (DirectPositionList) dpl.clone();
    // On enlève le systéme de vue actuel et on va y adjoindre l'animation
    CameraAnimation.bgvu = iMap3D.getBgvu();
    Locale l = CameraAnimation.bgvu.getLocale();
    l.removeBranchGraph(CameraAnimation.bgvu);

    // on crée le BranchGroup de l'animation
    CameraAnimation.bgTemp = new BranchGroup();
    CameraAnimation.bgTemp.setCapability(Group.ALLOW_CHILDREN_READ);
    CameraAnimation.bgTemp.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    CameraAnimation.bgTemp.setCapability(Group.ALLOW_CHILDREN_WRITE);
    CameraAnimation.bgTemp.setCapability(BranchGroup.ALLOW_DETACH);

    // Il s'agit du TransformGroup qui àvoluera avec le temps
    TransformGroup objSpin = new TransformGroup();
    objSpin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    // Nombre de ponits parcourus
    int nbPoints = dplTemp.size();
    if (mode == CameraAnimation.ALLER_RETOUR) {
      // On ajoute le chemin inverse
      for (int i = nbPoints - 1; i > -1; i--) {
        dplTemp.add(dplTemp.get(i));
      }
      nbPoints = dplTemp.size();
    }

    Point3f[] positions = new Point3f[nbPoints];

    // Calcul de la distance cumulée à parcourir lors de l'animation
    // elle est utile pour règler les paramètres de vitesse
    double distanceCumul = 0;

    for (int i = 0; i < nbPoints; i++) {

      IDirectPosition dpTemp = dplTemp.get(i);

      // On ne garde que les modifications de déplacement relatif
      positions[i] = new Point3f(
          (float) (dpTemp.getX() - dplTemp.get(0).getX()),
          (float) (dpTemp.getY() - dplTemp.get(0).getY()),
          (float) (dpTemp.getZ() - dplTemp.get(0).getZ() + offset));

      if (i != 0) {
        distanceCumul = distanceCumul + dpTemp.distance(dplTemp.get(i - 1));

      }
    }

    // On créer les noeuds, c'est à dire on lit a un temps t entre 0 et 1,
    // une position de l'espace
    float[] knots = new float[nbPoints];
    double distanceCumul2 = 0;
    for (int i = 0; i < nbPoints; i++) {

      IDirectPosition dpTemp = dplTemp.get(i);
      if (i != 0) {
        distanceCumul2 = distanceCumul2 + dpTemp.distance(dplTemp.get(i - 1));

      }

      knots[i] = (float) (distanceCumul2 / distanceCumul);

    }

    CameraAnimation.logger.info(Messages
        .getString("FenetreAnimationCamera.DistanceTotal")
        + " : "
        + distanceCumul);

    // Alpha correspond au temps nécessaire pour faire un parcours
    Alpha alpha = new Alpha(-1, (int) (distanceCumul * 10 / speed) + 1);

    // On crée l'interpolateur ad hoc et on le lie au tg
    PositionPathInterpolator p = new PositionPathInterpolator(alpha, objSpin,
        new Transform3D(), knots, positions);
    BoundingSphere bounds2 = new BoundingSphere();
    p.setSchedulingBounds(bounds2);
    objSpin.addChild(p);

    // on crée le BranchGroup portant l'animation
    CameraAnimation.bgTemp.addChild(objSpin);
    objSpin.addChild(CameraAnimation.bgvu);

    l.addBranchGraph(CameraAnimation.bgTemp);

    // On centre sur le premier points de la liste en regardant sur le point
    // suivant
    iMap3D.zoomOn(dplTemp.get(0).getX(), dplTemp.get(0).getY(), dplTemp.get(0)
        .getZ() + offset, new Vecteur(dplTemp.get(0), dplTemp.get(1)));

    CameraAnimation.iSCameraRunning = true;
  }

}
