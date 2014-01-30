package fr.ign.cogit.geoxygene.sig3d.convert.transform;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSolid;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

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
 * Classe utilitaire permettant d'effectuer des transformations entre
 * différentes géométries geoxygene. Permet de tansformer certains types
 * d'entites de la 2D à la 3D Class wich enable converting geomtry to one format
 * to an other one This class is utilized to transform Java3D Solid Geometry
 * into GeOxygene Geometry
 * 
 */
public class Extrusion2DObject {

  private final static Logger logger = Logger.getLogger(Extrusion2DObject.class
      .getName());

  /**
   * Convertit une geometrie GeOxygene 2D en geometrie Geoxygene(en appliquant
   * les paramètres 3D) Fait appelle aux autres fonctions de cette classe en
   * fonction du type de géométries choisi
   * 
   * @param geom la géométrie que l'on souhaite extrudé
   * @param zMin altitude minimale finale de l'objet
   * @param zMax altitude maximale finale de l'objet
   * @return une géométrie 2D extrudée (dimension +1 par rapport à l'objet
   *         initial)
   */
  @SuppressWarnings("unchecked")
  public static IGeometry convertFromGeometry(IGeometry geom, double zMin,
      double zMax) {
	  
	if (geom == null){return null;}

    IGeometry geomFinale = null;

    // On essaie de retrouver la classe de la géométrie
    // Ordre de test simple puis complexe de la dim la plus élevée à la
    // plus faible

    if (geom instanceof IPolygon) {

      geomFinale = Extrusion2DObject.convertFromPolygon((GM_Polygon) geom,
          zMin, zMax);

    } else if (geom instanceof IMultiSurface<?>) {

      GM_MultiSurface<IOrientableSurface> multiS = (GM_MultiSurface<IOrientableSurface>) geom;

      if (multiS.size() == 1) {

        geomFinale = Extrusion2DObject.convertFromPolygon(
            (GM_Polygon) multiS.get(0), zMin, zMax);

      } else {

        geomFinale = Extrusion2DObject.convertFromMultiPolygon(multiS, zMin,
            zMax);

      }

    } else if (geom instanceof ILineString) {

      geomFinale = Extrusion2DObject.convertFromLine((GM_LineString) geom,
          zMin, zMax);

    } else if (geom instanceof IMultiCurve<?>) {

      geomFinale = Extrusion2DObject.convertFromMultiLine(
          (GM_MultiCurve<?>) geom, zMin, zMax);

    } else if (geom instanceof GM_Point) {

      geomFinale = Extrusion2DObject.convertFromPoint((GM_Point) geom, zMin,
          zMax);

    } else if (geom instanceof GM_MultiPoint) {
      geomFinale = Extrusion2DObject.convertFromMultiPoint(
          (GM_MultiPoint) geom, zMin, zMax);

    } else {

      Extrusion2DObject.logger.warn(Messages
          .getString("Representation.GeomUnk") + geom.getClass().getName());
      return null;
    }

    return geomFinale;

  }

  /**
   * Convertit un polygone GeOxygene 2D en polygone Geoxygene(en appliquant les
   * parametres 3D) - Si Zmin <= Zmax le resultat est un polygone d'altitude
   * Zmin - Sinon extrude le polygone Pour convertir un polygone a trou : On
   * extrude chaque segments des frontières (extérieures et interieures) à la
   * hauteur souhaitee Puis on crées 2 polygones pour les faces superieures et
   * inférieures que l'on troue
   * 
   * @param polyIni : la geometrie initiale
   * @param zMin : valeur minimale de Z
   * @param zMax : le Z maximum (Double.NaN non defini)
   * @return Renvoie un GM_Solid ou un GM_OrientableSurface
   */
  public static IGeometry convertFromPolygon(IPolygon polyIni, double zMin,
      double zMax) {

    // Recuperation des coordonnees du contour
    ApproximatedPlanEquation eq = new ApproximatedPlanEquation(polyIni);

    if (eq.getNormale().getZ() > 0) {

      polyIni = new GM_Polygon(polyIni.reverse().boundary());
    }
    // Nombre de contributions
    int contribution = polyIni.getInterior().size() + 1;

    // Liste des trous a l'interieur d'un polygone
    ArrayList<GM_Ring> lHoles = new ArrayList<GM_Ring>();

    // Pour chaque contribution
    // les premieres contributions correspondent aux trous
    // a l'exterieur du polyone

    // Attention boucle inverse pour recuperer les trous en memoire
    // avant de creer les 2 faces sup et inf avec ces trous
    for (int idCont = contribution - 1; idCont >= 0; idCont--) {

      // Recuperation des arretes a etudier
      IDirectPositionList dPL = null;

      if (idCont == 0) {
        dPL = polyIni.getExterior().coord();

      } else {

        dPL = polyIni.getInterior(idCont - 1).coord();

      }

      int nbpoints = dPL.size();

      // On renvoie une polyligne d'altitude Zmin
      if (Double.isNaN(zMax) || (zMin >= zMax)) {

        IDirectPositionList dpl = new DirectPositionList();

        ApproximatedPlanEquation eq2 = new ApproximatedPlanEquation(dPL);

        boolean isInverse = (eq2.getNormale().getZ() < 0);

        if (isInverse) {

          for (int i = nbpoints - 1; i >= 0; i--) {
            // On ajoute un z
            IDirectPosition c = dPL.get(i);

            if (Double.isNaN(zMin)) {
              dpl.add(new DirectPosition(c.getX(), c.getY(), 0));
            } else {

              dpl.add(new DirectPosition(c.getX(), c.getY(), zMin));
            }

          }
        } else {

          for (int i = 0; i < nbpoints; i++) {
            // On ajoute un z
            IDirectPosition c = dPL.get(i);

            if (Double.isNaN(zMin)) {
              dpl.add(new DirectPosition(c.getX(), c.getY(), 0));
            } else {

              dpl.add(new DirectPosition(c.getX(), c.getY(), zMin));
            }

          }

        }

        if (idCont != 0) {
          lHoles.add(new GM_Ring(new GM_LineString(dpl)));

        } else {

          GM_Polygon poly = new GM_Polygon(new GM_LineString(dpl));
          int nbHoles = lHoles.size();

          for (int i = 0; i < nbHoles; i++) {

            if (!isInverse) {
              poly.addInterior(new GM_Ring(lHoles.get(i).getNegative()));

            } else {

              poly.addInterior(lHoles.get(i));
            }

          }

          return poly;

        }

      }

      // Le résultat est un polygone extrudé
      // Un attribut de hauteur existe

      IDirectPosition dpInit = dPL.get(0);

      // Ce point servira lors de la fermeture

      IDirectPosition pPred = new DirectPosition(dpInit.getX(), dpInit.getY(),
          zMin);

      IDirectPosition pPredZ = new DirectPosition(dpInit.getX(), dpInit.getY(),
          zMax);

      // On parcourt chaque point
      IDirectPosition pAct = null;
      IDirectPosition pActZ = null;

      ArrayList<IOrientableSurface> pLFacet = new ArrayList<IOrientableSurface>(
          nbpoints);
      IDirectPositionList lPFaceSup = new DirectPositionList();
      IDirectPositionList lPFaceInf = new DirectPositionList();

      // On boucle en sens inverse pour des questions d'orientation
      // On 'saute' un point car la géométrie est fermée
      // et cela ferait une face avec 2 points
 
      for (int j = nbpoints - 2; j >= 0; j--) {
        DirectPositionList fTemp = new DirectPositionList();
        dpInit = dPL.get(j);

        pAct = new DirectPosition(dpInit.getX(), dpInit.getY(), zMin);
        pActZ = new DirectPosition(dpInit.getX(), dpInit.getY(), zMax);

        // Ceci permet d'éviter des murs "grotesques"
        if (pAct.equals(pPred, 0.0001)) {

          continue;
        }

        lPFaceInf.add(pAct);
        lPFaceSup.add(pActZ);

        fTemp.add(pAct);
        fTemp.add(pActZ);
        fTemp.add(pPredZ);

        fTemp.add(pPred);
        fTemp.add(pAct);

        pPred = pAct;
        pPredZ = pActZ;

        GM_LineString lS = new GM_LineString(fTemp);
        GM_Ring gmRing = new GM_Ring(lS);
        GM_OrientableSurface oS = new GM_Polygon(gmRing);

        pLFacet.add(oS);

      }

      if(lPFaceSup.size() == 0){
        return null;
      }
      
      // On ferme
      lPFaceSup.add(lPFaceSup.get(0));
      lPFaceInf.add(lPFaceInf.get(0));

      GM_LineString lS = new GM_LineString(lPFaceSup);
      GM_Ring gmRing = new GM_Ring(lS);

      GM_LineString lS2 = new GM_LineString(lPFaceInf);

      // La contribution est finie

      if (idCont == 0) {

        // Il s'agit des polygones supérierieurs et inférieurs percés
        GM_Ring gmRing2 = new GM_Ring(lS2.getNegative());

        GM_Polygon poly = new GM_Polygon(gmRing);
        GM_Polygon poly2 = new GM_Polygon(gmRing2);

        int nbTrous = lHoles.size();

        for (int idTrous = 0; idTrous < nbTrous; idTrous = idTrous + 2) {

          poly.addInterior(lHoles.get(idTrous));
          poly2.addInterior(lHoles.get(idTrous + 1));

        }

        // On ajoute les faces sup et inf à la liste des faces formant
        // l'objet
        pLFacet.add(poly);
        
        //On inverse la face inférieure

        pLFacet.add(poly2);

        // On renvoie l'objet
        return new GM_Solid(pLFacet);

      }
      // Il s'agit d'un trou
      GM_Ring gmRing2 = new GM_Ring(lS2);

      // On ajoute un trou pour la surface du haut
      // Un pour la surface du bas
      lHoles.add(gmRing);
      lHoles.add(gmRing2);

    }

    return null;

  }

  /**
   * Convertir un multipolygon GeOxygene 2D en 3D en appliquant les paramètres
   * Zmin et Zmax Il s'agit d'appliquer a chaque polygone du multipolygone La
   * transformation convertitFromPolygon
   * 
   * @param mp : la geometrie transforme (multipolygon)
   * @param zMin : valeur minimale de Z
   * @param zMax : le Z maximum (Double.NaN non defini)
   * @return un GM_MultiSolid ou un GM_MultiSurface
   */
  public static IGeometry convertFromMultiPolygon(
      IMultiSurface<IOrientableSurface> mp, double zMin, double zMax) {
    // Recuperation des coordonnees du contour

    int nbPolygon = mp.size();

    if (nbPolygon == 0) {

      return null;
    }

    // 2 cas possible :
    // - soit on recupere des surfaces si il n'y a pas de hauteur
    // - soit on recupere des solides
    GM_MultiSolid<ISolid> mSolid = new GM_MultiSolid<ISolid>();
    GM_MultiSurface<IPolygon> mSurface = new GM_MultiSurface<IPolygon>();

    // On décompose le multiPolygon en liste de polygones
    for (int indpoly = 0; indpoly < nbPolygon; indpoly++) {

      // On applique la transformation a chaque polygone
      IPolygon p = (GM_Polygon) mp.get(indpoly);
      IGeometry obj = Extrusion2DObject.convertFromPolygon(p, zMin, zMax);
      
      if(obj == null){
        continue;
      }

      // On utilise la contribution de chaque polygone
      // Pour instancier la geometrie ad hoc
      if (Double.isNaN(zMax) || (zMin >= zMax)) {
        // Polygon 'plat'
        mSurface.add((GM_Polygon) obj);

      } else {

        // Solide
        mSolid.add((GM_Solid) obj);

      }

    }

    // On renvoie la bonne geometrie
    if (Double.isNaN(zMax) || (zMin >= zMax)) {
      // Polygon 'plat'
      return mSurface;
    }
    // Solide
    return mSolid;

  }

  /**
   * Transforme une LineString GeOxygene 2D en un objet 3D - Si Zmin >= Zmax le
   * resultat est une ligne d'altitude Zmin - Sinon, il y a extrusion (ensemble
   * de faces verticales en sortie)
   * 
   * @param lsIni : la géométrie JTS à convertir
   * @param zMin : valeur minimale de Z
   * @param zMax : le Z maximum (Double.NaN non défini)
   * @return Un GM_LineString ou un GM_MultiSurface
   */
  public static IGeometry convertFromLine(ILineString lsIni, double zMin,
      double zMax) {

    IDirectPositionList coordSeq = lsIni.coord();

    GM_MultiSurface<IPolygon> surf = new GM_MultiSurface<IPolygon>();

    int nbpoints = coordSeq.size();

    // On renvoie une polyligne d'altitude Zmin
    if (Double.isNaN(zMax) || (zMin >= zMax)) {

      IDirectPositionList dpl = new DirectPositionList();
      for (int i = 0; i < nbpoints; i++) {
        // On ajoute un z
        IDirectPosition c = coordSeq.get(i);
        if (Double.isNaN(zMin)) {

          dpl.add(new DirectPosition(c.getX(), c.getY(), 0));
        } else {
          dpl.add(new DirectPosition(c.getX(), c.getY(), zMin));
        }

      }

      return new GM_LineString(dpl);

    }

    // Le résultat est une polyligne extrudée car
    // Un attribut de hauteur existe

    // On initialize
    IDirectPosition c = coordSeq.get(0);

    DirectPosition pPred = new DirectPosition(c.getX(), c.getY(), zMin);

    DirectPosition pPredZ = new DirectPosition(c.getX(), c.getY(), zMax);

    // On parcours chaque point
    DirectPosition pAct = null;
    DirectPosition pActZ = null;

    // On trace un part un les plans verticaux
    // correspondant aux différents segments de la polyligne
    for (int j = 1; j < nbpoints; j++) {
      DirectPositionList fTemp = new DirectPositionList();
      c = coordSeq.get(j);

      pAct = new DirectPosition(c.getX(), c.getY(), zMin);
      pActZ = new DirectPosition(c.getX(), c.getY(), zMax);

      if (pAct.equals(pPred, 0.1)) {

        continue;
      }

      // On constuit une face verticale
      fTemp.add(pAct);
      fTemp.add(pPred);
      fTemp.add(pPredZ);
      fTemp.add(pActZ);

      fTemp.add(pAct);

      pPred = pAct;
      pPredZ = pActZ;

      GM_LineString lS = new GM_LineString(fTemp);
      GM_Polygon oS = new GM_Polygon(lS);

      // On l'ajoute à la liste des faces verticales
      surf.add(oS);

    }

    return surf;

  }

  /**
   * On applique successivement convertitFromLine aux différents LineString de
   * la géométrie
   * 
   * @param mls : la multiLineString à transformer
   * @param zMin : valeur minimale de Z
   * @param zMax : le Z maximum (Double.NaN non défini)
   * @return un GM_MultiCurve ou un GM_MultiSurface
   */
  @SuppressWarnings("unchecked")
  public static IGeometry convertFromMultiLine(IMultiCurve<?> mls, double zMin,
      double zMax) {

    int nbLignes = mls.size();

    if (nbLignes == 0) {

      return null;
    }

    // 2 cas possible :
    // - soit on récupère une line string
    // - soit on récupère des surfaces

    GM_MultiCurve<GM_LineString> mCurve = new GM_MultiCurve<GM_LineString>();
    GM_MultiSurface<GM_OrientableSurface> mSurf = new GM_MultiSurface<GM_OrientableSurface>();

    // On décompose le multiPolygon en liste de polygones
    for (int indligne = 0; indligne < nbLignes; indligne++) {

      // On applique la transformation à chaque polygone
      GM_LineString l = (GM_LineString) mls.get(indligne);
      IGeometry obj = Extrusion2DObject.convertFromLine(l, zMin, zMax);

      // On utilise la contribution de chaque polygone
      // Pour instancier la géométrie ad hoc
      if (Double.isNaN(zMax) || (zMin >= zMax)) {
        // MultiLineString
        mCurve.add((GM_LineString) obj);
      } else {

        // MultiSurface

        GM_MultiSurface<GM_OrientableSurface> surf = (GM_MultiSurface<GM_OrientableSurface>) obj;
        mSurf.addAll(surf.getList());

      }

    }

    // On renvoie la bonne géométrie
    if (Double.isNaN(zMax) || (zMin >= zMax)) {

      // MultiLineString
      return mCurve;
    }
    // MultiSurface
    return mSurf;
  }

  /**
   * Permet de transformer une géométrie ponctuelle en un point (X Y Zmin) ou un
   * segment vertical(( X Y Zmin), (X Y Zmax))
   * 
   * @param p : le point à transformer
   * @param zMin : valeur minimale de Z
   * @param zMax : le Z maximum (Double.NaN non défini)
   * @return renvoie un GM_Point ou un GM_LineString
   */
  public static IGeometry convertFromPoint(IPoint p, double zMin, double zMax) {

    IDirectPosition dp = p.getPosition();
    // On regarde si il y a un Zmax
    if (Double.isNaN(zMax) || (zMin >= zMax)) {
      // Pas de ZMax on a juste un point en sorti
      if (Double.isNaN(zMin)) {
        return new GM_Point(new DirectPosition(dp.getX(), dp.getY(), 0));
      }
      return new GM_Point(new DirectPosition(dp.getX(), dp.getY(), zMin));
    }
    // Un Zmax, on trace un segment
    DirectPosition p1 = new DirectPosition(dp.getX(), dp.getY(), zMin);
    DirectPosition p2 = new DirectPosition(dp.getX(), dp.getY(), zMax);

    DirectPositionList dpl = new DirectPositionList();
    dpl.add(p1);
    dpl.add(p2);

    return new GM_LineString(dpl);

  }

  /**
   * Permet de transformer une géométrie ponctuelle en un point (X Y Zmin) ou un
   * segment (( X Y Zmin), (X Y Zmax))
   * 
   * @param mp : le MultiPoint qui sera converti
   * @param zMin : valeur minimale de Z
   * @param zMax : le Z maximum (Double.NaN non défini)
   * @return un GM_MultiPoint ou un GM_MultiSurface
   */

  public static IGeometry convertFromMultiPoint(IMultiPoint mp, double zMin,
      double zMax) {

    // Nombre de poolygones présents dans la géométrie
    int nbPoints = mp.size();

    if (nbPoints == 0) {

      return null;
    }

    // 2 cas possible :
    // - soit on récupère des points si il n'y a pas de hauteur
    // - soit on récupère des lignes
    GM_MultiPoint mPoints = new GM_MultiPoint();
    GM_MultiCurve<GM_OrientableCurve> mCurve = new GM_MultiCurve<GM_OrientableCurve>();

    // On décompose le multiPoints en liste de points
    for (int indpoint = 0; indpoint < nbPoints; indpoint++) {
      // On applique la transformation à chaque polygone
      IPoint p = mp.get(indpoint);
      IGeometry obj = Extrusion2DObject.convertFromPoint(p, zMin, zMax);

      // On utilise la contribution de chaque polygone
      // Pour instancier la géométrie ad hoc
      if (Double.isNaN(zMax) || (zMin >= zMax)) {
        // Multipoints
        mPoints.add((GM_Point) obj);
      } else {
        // MultiCurve
        mCurve.add((GM_OrientableCurve) obj);

      }

    }

    // On renvoie la bonne géométrie
    if (Double.isNaN(zMax) || (zMin >= zMax)) {
      // Multipoints
      return mPoints;
    }
    // MultiCurve
    return mCurve;

  }

}
