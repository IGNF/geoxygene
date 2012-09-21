package fr.ign.cogit.geoxygene.sig3d.calculation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.j3d.PickInfo;
import javax.media.j3d.SceneGraphPath;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.pickfast.PickTool;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.representation.I3DRepresentation;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
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
 *
 * Classe permettant d'effecteur des calculs de rayonnements, l'algorithme
 * utilise les fonctions de Java3D Resultats possibles : - Selection des objets
 * heurtes par un rayon - Liste des impacts
 * 
 * TODO Proposer une visualisation par rayons
 * 
 * Class for ray tracing calculation

 */
public class RayTracing {
  /**
   * Nom de la couche dans laquelle seront stockés les points générés
   */
  public static final String POINTS_GENERES = "PointsGeneres";
  // variable pour récupèrer les coordonnées àcran
  int x, y;

  /**
   * Methode permettant d'effectuer un calcul de rayonnement
   * 
   * @param iMap3D Le composant carte dans lequel est effectue le rayonnement
   * @param alpha La direction horizontale de visee (par rapport au nord en à)
   * @param lambda La direction verticale de visee (par rapport à l'horiztonale
   *          en à)
   * @param angle L'angle sur lequel est effectue le rayonnement (Le centre sera
   *          alpha, lambda et les rayons parcourant l'espace compris entre
   *          alpha - angle, lambda - angle, alpha + angle et lambda +angle)
   * @param radius La distance sur laquelle est calculee les intersections
   * @param step Le pas angulaire, permet de determiner la densite des rayons
   * @param centre Le centre a partir duquel on rayonne
   * @param mod Le resultat issu du rayonnement si true, c'est une liste de
   *          points si false c'est juste de la selection d'objets
   */
  public static void processRayonnement(InterfaceMap3D iMap3D, int alpha,
      int lambda, int angle, int radius, double step, IDirectPosition centre,
      boolean mod) {
    if (mod) {
      RayTracing.cloudPointsRayTracing(iMap3D, alpha, lambda, angle, radius,
          step, centre);
    } else {

      RayTracing.selectRayTracing(iMap3D, alpha, lambda, angle, radius, step,
          centre);
    }

  }

  /**
   * Effectue une selection des objets touchés par le rayonnement
   * 
   * @param iMap3D
   * @param alpha
   * @param lambda
   * @param angle
   * @param radius
   * @param step
   * @param center
   */
  private static void selectRayTracing(InterfaceMap3D iMap3D, int alpha,
      int lambda, int angle, int radius, double step, IDirectPosition center) {
    // On se place dans les coordonnnees reelles
    double xini = iMap3D.getTranslate().getX();
    double yini = iMap3D.getTranslate().getY();
    double zini = iMap3D.getTranslate().getZ() + 1;

    // On vide la selection si nous sommes en mode selection d'objets

    iMap3D.setSelection(new FT_FeatureCollection<IFeature>());

    PickTool pickCanvas = new PickTool(iMap3D.getBgeneral());
    pickCanvas.setMode(PickInfo.PICK_GEOMETRY);

    // On passe le point du reprere terrain au repere Java3D
    Point3d p = new Point3d(center.getX() + xini, center.getY() + yini,
        center.getZ() + zini);
    // Liste servant à contenir les points au cas ou le resultat souhaite
    // est une liste de points

    double angleAlpha, angleLambda;

    // On calcule les angles min et max horizontaux
    double angleHorMin = alpha - angle;
    double angleHorMax = alpha + angle;

    // On calcul les angle min est max
    double angleLambdaMin = Math.min(-(Math.PI / 2), lambda - angle);
    double angleLambdaMax = Math.max((Math.PI / 2), lambda + angle);

    // On parcourt l'espace et effectue pour chaque pas un lancer de rayon
    for (double i = angleHorMin; i < angleHorMax; i = i + step) {

      for (double j = angleLambdaMin; j < angleLambdaMax; j = j + step) {
        // On calcul le vecteur direction dans lequel le tire sera
        // effectue
        angleAlpha = i * Math.PI / 180;
        angleLambda = j * Math.PI / 180;

        double cosLambda = Math.cos(angleLambda);

        Vector3d vect = new Vector3d(

        Math.cos(angleAlpha) * cosLambda, Math.sin(angleAlpha) * cosLambda,
            Math.sin(angleLambda));
        // Point vise
        Point3d pFinal = new Point3d(p.getX() + radius * vect.x, p.getY()
            + radius * vect.y, p.getZ() + radius * vect.z);

        if (pickCanvas == null) {

          continue;
        }
        // Pour avoir des infos à partir des frontières de l'objet
        // intersecté
        pickCanvas.setFlags(PickInfo.SCENEGRAPHPATH);
        pickCanvas.setShapeSegment(p, pFinal);

        PickInfo pickResult = pickCanvas.pickClosest();

        if (pickResult == null) {

          continue;
        }

        SceneGraphPath bg = pickResult.getSceneGraphPath();

        if (bg == null) {
          continue;
        }

        Object obj = bg.getNode(bg.nodeCount() - 1).getUserData();
        // On complete la selection si besoin est
        if (obj instanceof I3DRepresentation) {
          I3DRepresentation rep = (I3DRepresentation) obj;
          if (rep.isSelected()) {
            continue;
          }
          iMap3D.addToSelection(rep.getFeature());

        }

      }

    }

  }

  /**
   * Effectue un ray tracing calculant des sommets et les ajoutant dans la
   * couche ad hoc
   * @param iMap3D
   * @param alpha
   * @param lambda
   * @param angle
   * @param radius
   * @param step
   * @param center
   */
  private static void cloudPointsRayTracing(InterfaceMap3D iMap3D, int alpha,
      int lambda, int angle, int radius, double step, IDirectPosition center) {

    // On se place dans les coordonnnees reelles
    double xini = iMap3D.getTranslate().getX();
    double yini = iMap3D.getTranslate().getY();
    double zini = iMap3D.getTranslate().getZ() + 1;

    PickTool pickCanvas = new PickTool(iMap3D.getBgeneral());
    pickCanvas.setMode(PickInfo.PICK_GEOMETRY);

    // On passe le point du reprere terrain au repere Java3D
    Point3d p = new Point3d(center.getX() + xini, center.getY() + yini,
        center.getZ() + zini);

    double angleAlpha, angleLambda;

    // On calcul les angle min est max
    double angleLambdaMin = Math.min(-(Math.PI / 2), lambda - angle);
    double angleLambdaMax = Math.max((Math.PI / 2), lambda + angle);

    // On calcule les angles min et max horizontaux
    double angleHorMin = alpha - angle;
    double angleHorMax = alpha + angle;

    // On regarde si il y a déjà des éléments dans la couche résultat
    VectorLayer c2 = (VectorLayer) iMap3D.getCurrent3DMap().getLayer(
        RayTracing.POINTS_GENERES);

    AttributeType attType = new AttributeType();
    attType.setMemberName("Distance");
    attType.setNomField("Distance");
    attType.setValueType("Double");

    Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>();
    attLookup.put(0,
        new String[] { attType.getNomField(), attType.getMemberName() });

    List<String> lCol = new ArrayList<String>();
    lCol.add("Distance");

    FeatureType featType = new FeatureType();
    featType.addFeatureAttribute(attType);

    FT_FeatureCollection<IFeature> ftColl = new FT_FeatureCollection<IFeature>();

    // On parcourt l'espace et effectue pour chaque pas un lancer de rayon
    for (double i = angleHorMin; i < angleHorMax; i = i + step) {

      for (double j = angleLambdaMin; j < angleLambdaMax; j = j + step) {
        // On calcul le vecteur direction dans lequel le tire sera
        // effectue
        angleAlpha = i * Math.PI / 180;
        angleLambda = j * Math.PI / 180;

        double cosLambda = Math.cos(angleLambda);

        Vector3d vect = new Vector3d(

        Math.cos(angleAlpha) * cosLambda, Math.sin(angleAlpha) * cosLambda,
            Math.sin(angleLambda));
        // Point vise
        Point3d pFinal = new Point3d(p.getX() + radius * vect.x, p.getY()
            + radius * vect.y, p.getZ() + radius * vect.z);

        // On effectue le tire et on recupere le resultat
        pickCanvas.setShapeSegment(p, pFinal);
        pickCanvas.setFlags(PickInfo.CLOSEST_INTERSECTION_POINT);

        PickInfo pickResult = pickCanvas.pickClosest();

        if (pickResult == null) {

          continue;
        }

        Point3d pTemp = pickResult.getClosestIntersectionPoint();

        if (pTemp == null) {
          continue;
        }

        GM_Point pt = new GM_Point(
            new DirectPosition(pTemp.x, pTemp.y, pTemp.z));

        DefaultFeature feat = new DefaultFeature(pt);
        feat.setFeatureType(featType);

        SchemaDefaultFeature sft = new SchemaDefaultFeature();
        sft.addFeatureType(featType);
        sft.setColonnes(lCol);
        sft.setAttLookup(attLookup);

        feat.setSchema(sft);
        feat.setAttributes(new Object[1]);

        feat.setAttribute(attType, new Double(pickResult.getClosestDistance()));
        ftColl.add(feat);

      }

    }
    // Si nous sommes en mode recuperation de points la couche est ajoutee a
    // la carte
    // On ecrase la couche de nom PointsGeneres si elle existe

    if (c2 == null) {

      c2 = new VectorLayer(ftColl, RayTracing.POINTS_GENERES, true, Color.red,
          0.5, true);

      iMap3D.getCurrent3DMap().addLayer(c2);

    } else {

      c2.clear();
      c2.addAll(ftColl);
      c2.updateStyle(true, Color.red, 0.5, true);

    }

  }
}
