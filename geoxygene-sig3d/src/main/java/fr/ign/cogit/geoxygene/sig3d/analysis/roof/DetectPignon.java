package fr.ign.cogit.geoxygene.sig3d.analysis.roof;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
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
 * @version 1.7
 * 
 **/
public class DetectPignon {

  /*
   * public static List<ILineString> detectPignon(IPolygon contourBati,
   * IMultiCurve<IOrientableCurve> listInteriorArc, double espilonDist, double
   * epsilonAngle) {
   * 
   * IDirectPositionList dpl = contourBati.coord(); int nbP = dpl.size();
   * List<ILineString> countourBati = new ArrayList<ILineString>();
   * 
   * for (int i = 0; i < nbP - 1; i++) { IDirectPositionList dplTemp = new
   * DirectPositionList(); dplTemp.add(dpl.get(i)); dplTemp.add(dpl.get(i + 1));
   * 
   * countourBati.add(new GM_LineString(dpl));
   * 
   * }
   * 
   * return detectPignon(countourBati, listInteriorArc, espilonDist,
   * epsilonAngle);
   * 
   * }
   * 
   * 
   * 
   * public static List<ILineString> detectPignon(IMultiCurve<IOrientableCurve>
   * listInteriorArc, double epsilonAngle) {
   * 
   * List<ILineString> lLS = new ArrayList<ILineString>();
   * 
   * List<IOrientableCurve> lC = new ArrayList<IOrientableCurve>();
   * IDirectPosition lPoint = null;
   * 
   * 
   * for(IOrientableCurve o:listInteriorArc){
   * 
   * if(lPoint == null){ lPoint = o.coord().get(1); lC.add(o); continue;
   * 
   * }
   * 
   * if(o.coord().get(0).equals2D(lPoint)){
   * 
   * lC.add(o); lPoint = o.coord().get(0); }else{ IDirectPositionList dpl = new
   * DirectPositionList(); dpl.add(o.coord().get(1)); dpl.add(o.coord().get(0));
   * 
   * 
   * lC.add(new GM_LineString(dpl));
   * 
   * lPoint = o.coord().get(1); }
   * 
   * 
   * 
   * }
   * 
   * 
   * 
   * Box3D b = new Box3D(listInteriorArc); double zmin = b.getLLDP().getZ();
   * 
   * int nbP = lC.size();
   * 
   * int index = -1;
   * 
   * // On prépare le départ for (int i = 0; i < nbP; i++) { IDirectPosition dp1
   * = lC.get(i).coord().get(0); IDirectPosition dp2 = lC.get(i).coord().get(1);
   * 
   * if (dp1.getZ() == zmin) { index = i; break; }
   * 
   * if (dp2.getZ() == zmin) { index = i; break; }
   * 
   * }
   * 
   * 
   * 
   * double penteIni = pente(lC.get(index).coord().get(0),
   * lC.get(index).coord().get(1));
   * 
   * 
   * if(penteIni < -epsilonAngle){
   * 
   * while(penteIni < -epsilonAngle){ index = index +1;
   * 
   * 
   * if(index > lC.size() -1){ index =0; }
   * 
   * penteIni = pente(lC.get(index).coord().get(0),
   * lC.get(index).coord().get(1));
   * 
   * 
   * }
   * 
   * 
   * }
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * for (int i = 0; i < index; i++) { lC.add(lC.get(0)); lC.remove(0); }
   * 
   * 
   * 
   * 
   * 
   * // On a le point le plus bas au départ (forcément en bas d'un descente)
   * 
   * List<ILineString> lLSTemp = new ArrayList<ILineString>();
   * 
   * boolean isPhaseAscendante = false;
   * 
   * boolean isPhaseDescendante = false;
   * 
   * for (int i = 0; i < nbP - 1; i++) { IOrientableCurve c = lC.get(i); double
   * pente = pente(c.coord().get(0), c.coord().get(1));
   * 
   * if (Math.abs(pente) < epsilonAngle) { pente = 0; }
   * 
   * if (pente == 0) {
   * 
   * 
   * 
   * if (isPhaseAscendante ) { // On monte puis on stagne, mais on peut remonter
   * après
   * 
   * 
   * 
   * lLSTemp.clear();
   * 
   * 
   * }else if(isPhaseDescendante){
   * 
   * lLS.addAll(lLSTemp); lLSTemp.clear(); }
   * 
   * isPhaseAscendante = false; isPhaseDescendante = false; continue;
   * 
   * 
   * 
   * } else if (pente < 0) {
   * 
   * if (isPhaseAscendante) { isPhaseDescendante = true; isPhaseAscendante =
   * false;
   * 
   * 
   * 
   * lLSTemp.add((ILineString)c); }
   * 
   * } else { // Phase ascendante if (isPhaseDescendante) { isPhaseDescendante =
   * false; isPhaseAscendante = true;
   * 
   * 
   * lLS.addAll(lLSTemp); lLSTemp.clear();
   * 
   * 
   * 
   * 
   * lLSTemp.add((ILineString)c);
   * 
   * }else{ isPhaseDescendante = false; isPhaseAscendante = true;
   * 
   * lLSTemp.add((ILineString)c);
   * 
   * 
   * }
   * 
   * }
   * 
   * }
   * 
   * return lLS;
   * 
   * }
   * 
   * private static double pente(IDirectPosition dp1, IDirectPosition dp2) {
   * 
   * return ((dp2.getZ() - dp1.getZ()) / 2); }
   * 
   * public static List<ILineString> detectPignon2(
   * IMultiCurve<IOrientableCurve> listInteriorArc, double epsilonAngle) {
   * 
   * List<ILineString> lSOut = new ArrayList<ILineString>();
   * 
   * int nbElem = listInteriorArc.size();
   * 
   * // On cherche 1 qui monte suivit d'un qui descend
   * 
   * bouclemain: for (int i = 0; i < nbElem; i++) {
   * 
   * IOrientableCurve oC = listInteriorArc.get(i); double pente = getPente(oC);
   * 
   * if (Math.abs(pente) < epsilonAngle) { continue; }
   * 
   * IDirectPosition dp2 = oC.coord().get(1);
   * 
   * if (pente > epsilonAngle) { // On Monte
   * 
   * int index = i - 1; if (i == 0) { index = nbElem - 1; }
   * 
   * IOrientableCurve oCTemp = listInteriorArc.get(index); IDirectPosition
   * dpTemp1 = oCTemp.coord().get(0); IDirectPosition dpTemp2 =
   * oCTemp.coord().get(1);
   * 
   * boolean isNiceWay = false;
   * 
   * if (dpTemp1.distance2D(dp2) == 0) {
   * 
   * isNiceWay = true;
   * 
   * } else if (dpTemp2.distance2D(dp2) == 0) {
   * 
   * IDirectPosition dpInutile = dpTemp1; dpTemp1 = dpTemp2; dpTemp2 =
   * dpInutile;
   * 
   * isNiceWay = true;
   * 
   * // Dans ce cas, dpTemp1 est au même endroit que dp2
   * 
   * }
   * 
   * if (!isNiceWay) {
   * 
   * index = i + 1; if (i == nbElem - 1) { index = 0; }
   * 
   * oCTemp = listInteriorArc.get(index); dpTemp1 = oCTemp.coord().get(0);
   * dpTemp2 = oCTemp.coord().get(1);
   * 
   * if (dpTemp1.distance2D(dp2) == 0) {
   * 
   * isNiceWay = true;
   * 
   * } else if (dpTemp2.distance2D(dp2) == 0) {
   * 
   * IDirectPosition dpInutile = dpTemp1; dpTemp1 = dpTemp2; dpTemp2 =
   * dpInutile;
   * 
   * isNiceWay = true;
   * 
   * // Dans ce cas, dpTemp1 est au même endroit que dp2
   * 
   * }
   * 
   * }
   * 
   * if (!isNiceWay) { System.out.println("Big Probleme"); }
   * 
   * double pente2 = getPente(oCTemp);
   * 
   * if (Math.abs(pente2) < epsilonAngle) { pente2 = 0; }
   * 
   * if (pente > 0 && pente2 < 0) { lSOut.add((ILineString) oC);
   * lSOut.add((ILineString) oCTemp); System.out.println("Jy p"); continue
   * bouclemain; }
   * 
   * }
   * 
   * 
   * 
   * }
   * 
   * return lSOut; }
   * 
   * private static double getPente(IOrientableCurve oC) {
   * 
   * IDirectPosition dp1 = oC.coord().get(0); IDirectPosition dp2 =
   * oC.coord().get(1);
   * 
   * Vecteur v = new Vecteur(dp1, dp2); v.normalise();
   * 
   * return v.getZ(); }
   * 
   * public static List<ILineString> detectPignon(List<ILineString>
   * countourBati, IMultiCurve<IOrientableCurve> listInteriorArc, double
   * espilonDist, double epsilonAngle) {
   * 
   * List<ILineString> lSOut = new ArrayList<ILineString>();
   * 
   * for (ILineString lsCount : countourBati) {
   * 
   * for (IOrientableCurve lsInterior : listInteriorArc) {
   * 
   * if (lsCount.distance(lsInterior) > espilonDist) { continue; }
   * 
   * Vecteur v1 = new Vecteur(lsCount.coord().get(0), lsCount.coord().get(1));
   * Vecteur v2 = new Vecteur(lsInterior.coord().get(0), lsInterior.coord()
   * .get(1));
   * 
   * v1.setZ(0); v2.setZ(0);
   * 
   * v1.normalise(); v2.normalise();
   * 
   * double scalaire = v1.prodScalaire(v2);
   * 
   * if (Math.abs(scalaire) < epsilonAngle) {
   * 
   * lSOut.add(lsCount); // featCollOut.add(new DefaultFeature(lsCount));
   * 
   * }
   * 
   * }
   * 
   * }
   * 
   * return lSOut;
   * 
   * }
   */

  /**
   * Déétection des pignons à partir de l'égout du toit, du faitage.
   * 
   * @param ext
   * @param faitage
   * @param dist
   * @param epsilonAngle
   * @return
   */
  public static List<ILineString> detectPignon(
      IMultiCurve<IOrientableCurve> ext, IMultiCurve<IOrientableCurve> faitage,
      double dist, double epsilonAngle) {
    //Il s'a&git de la liste des segments proche du faitage et de pente non nulle
    List<ILineString> lS = new ArrayList<ILineString>();

    boucleAlpha: for (IOrientableCurve oc : ext) {

      for (IOrientableCurve fait : faitage) {

        if (oc.distance(fait) < dist) {

          if (Math.abs(pente(oc)) > epsilonAngle) {

            lS.add((ILineString) oc);

          }

          continue boucleAlpha;
        }

      }

    }



    for (int e = 0; e < 10; e++) { // c'est moche mais ça marche ....
                                   // normalement ca devrait être une boucle
                                   // while

      

      int nbELem = lS.size();

      for (int i = 0; i < nbELem; i++) {

        ILineString l = lS.get(i);

        for (IOrientableCurve oc : ext) {

          if (oc.equals(l)) {
            continue;
          }

          if (lS.contains(oc)) {
            continue;
          }

          if (oc.distance(l) == 0) {

    

            if (Math.abs(pente(oc)) > epsilonAngle) {

              lS.add((ILineString) oc);

            }

          } else if (oc.distance(l) < dist) {

     

            if (Math.abs(pente(oc)) > epsilonAngle) {

              IPoint p1 = new GM_Point(oc.coord().get(0));
              IPoint p2 = new GM_Point(oc.coord().get(1));

              if (l.distance(p1) > l.distance(p2)) {

                p1 = p2;
                p2 = p1;
              }

              IPoint p3 = new GM_Point(l.coord().get(0));
              IPoint p4 = new GM_Point(l.coord().get(1));

              if (oc.distance(p3) > oc.distance(p4)) {
                p3 = p4;
              }

              // System.out.println("je move");

              IDirectPositionList dplTemp = new DirectPositionList();
              dplTemp.add(p3.coord().get(0));
              dplTemp.add(p2.coord().get(0));

              lS.add(new GM_LineString(dplTemp));

            }

          }

        }

      }

    }

    // On va créer des listes de listes

    boolean hasFusionned = true;

    boucleWhile: while (hasFusionned) {
      hasFusionned = false;

      int nbElem = lS.size();

      for (int i = 0; i < nbElem; i++) {

        ILineString lsi = lS.get(i);

        for (int j = i + 1; j < nbElem; j++) {
          ILineString lsj = lS.get(j);

          if (lsi.touches(lsj)) {
            List<ILineString> lSTemp = new ArrayList<ILineString>();

            lSTemp.add(lsi);
            lSTemp.add(lsj);

            ILineString lsOut = Operateurs.union(lSTemp);
            lS.add(lsOut);
            lS.remove(j);
            lS.remove(i);

            hasFusionned = true;
            continue boucleWhile;

          }

        }

      }

    }

    // Nettoyage

    for (int i = 0; i < lS.size(); i++) {

      ILineString ls = lS.get(i);

      if (ls.length() < 25 * dist) {
        lS.remove(i);
        i--;
        continue;

      }

    }

    return lS;
  }

  private static double pente(IOrientableCurve oc) {

    Vecteur v = new Vecteur(oc.coord().get(0), oc.coord().get(1));

    return v.getZ() / oc.length();
  }

}
