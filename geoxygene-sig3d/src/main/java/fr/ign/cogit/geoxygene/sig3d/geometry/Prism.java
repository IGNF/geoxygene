package fr.ign.cogit.geoxygene.sig3d.geometry;

import java.util.ArrayList;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
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
 * Classe permettant de représenter un prisme, utilisé dans l'affichage du
 * calcul de rayonnement
 * 
 * Class to create a prism used for ray tracing calculation
 * 
 */
public class Prism extends GM_Solid {

  /**
   * Permet de calculer un prisme
   * 
   * @param center centre du prisme
   * @param radius rayon du prisme
   * @param step precision du prisme
   * @param angleMinAlpha angle horizontal minimum (sens direct par rapport à
   *          l'axe des Z)
   * @param angleMaxAlpha angle horizontal maximum( sens direct par rapport à
   *          l'axe des Z)
   * @param angleMinLambda angle horizontal minimum (par rapport à la verticale)
   * @param angleMaxLambda angle horizontal maximum (par rapport à la verticale)
   */
  public Prism(IDirectPosition center, double radius, double step,
      double angleMinAlpha, double angleMaxAlpha, double angleMinLambda,
      double angleMaxLambda) {
    // On appelle le constructeur à partir d'une liste de surface
    super(Prism.calculPrisme(center, radius, step, angleMinAlpha,
        angleMaxAlpha, angleMinLambda, angleMaxLambda));
  }

  private static ArrayList<IOrientableSurface> calculPrisme(
      IDirectPosition center, double radius, double step, double angleMinAlpha,
      double angleMaxAlpha, double angleMinLambda, double angleMaxLambda) {

    ArrayList<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();

    // On vire les cas que l'on ne devrait pas considèrer
    // Si le centre est à NAN il y a une erreur

    double xc = center.getX();
    double yc = center.getY();
    double zc = center.getZ();

    // Pour nbCercles on définit 4 * nbCercles sommets pour chaque cercle
    // Cela permet d'avoir un octoèdre pour nbCercles =1

    int nbElemAlpha = (int) ((angleMaxAlpha - angleMinAlpha) / 0.1);
    int nbElemLambda = (int) ((angleMaxLambda - angleMinLambda) / 0.1);

    DirectPositionList lPointsCerclePrecedent = new DirectPositionList();

    // On initialise la première ligne

    for (int i = 0; i < nbElemAlpha; i++) {

      lPointsCerclePrecedent.add(new DirectPosition(

      xc + Math.cos(angleMinAlpha + i * step) * Math.cos(angleMinLambda)
          * radius, yc + Math.sin(angleMinAlpha + i * step)
          * Math.cos(angleMinLambda) * radius, zc + Math.sin(angleMinLambda)
          * radius

      ));

    }

    lPointsCerclePrecedent.add(new DirectPosition(

    xc + Math.cos(angleMaxAlpha) * Math.cos(angleMinLambda) * radius, yc
        + Math.sin(angleMaxAlpha) * Math.cos(angleMinLambda) * radius, zc
        + Math.sin(angleMinLambda) * radius

    ));

    IDirectPositionList lBord1 = Prism.invertList(lPointsCerclePrecedent);
    lBord1.add(center);
    lBord1.add(0, center);

    IDirectPositionList lBord3 = new DirectPositionList();
    IDirectPositionList lBord4 = new DirectPositionList();

    lBord3.add(lPointsCerclePrecedent.get(0));
    lBord4.add(lPointsCerclePrecedent.get(lPointsCerclePrecedent.size() - 1));

    // Pour chaque cercle
    for (int j = 1; j < nbElemLambda; j++) {

      double zActuel = radius * Math.sin(step * j + angleMinLambda) + zc;

      DirectPositionList lPointsCercleActuel = new DirectPositionList();

      for (int i = 0; i < nbElemAlpha; i++) {

        lPointsCercleActuel.add(new DirectPosition(

        xc + Math.cos(angleMinAlpha + i * step)
            * Math.cos(step * j + angleMinLambda) * radius, yc
            + Math.sin(angleMinAlpha + i * step)
            * Math.cos(step * j + angleMinLambda) * radius, zActuel

        ));

      }

      lPointsCercleActuel.add(new DirectPosition(

      xc + Math.cos(angleMaxAlpha) * Math.cos(step * j + angleMinLambda)
          * radius, yc + Math.sin(angleMaxAlpha)
          * Math.cos(step * j + angleMinLambda) * radius, zActuel

      ));

      lBord3.add(lPointsCercleActuel.get(0));
      lBord4.add(lPointsCercleActuel.get(lPointsCercleActuel.size() - 1));

      for (int i = 1; i < nbElemAlpha + 1; i++) {

        DirectPositionList dpl1 = new DirectPositionList();
        dpl1.add(lPointsCerclePrecedent.get(i - 1));

        dpl1.add(lPointsCercleActuel.get(i));
        dpl1.add(lPointsCercleActuel.get(i - 1));
        dpl1.add(lPointsCerclePrecedent.get(i - 1));

        lOS.add(new GM_Polygon(new GM_LineString(dpl1)));

        if (i != 0) {

          DirectPositionList dpl2 = new DirectPositionList();
          dpl2.add(lPointsCercleActuel.get(i));

          dpl2.add(lPointsCerclePrecedent.get(i - 1));
          dpl2.add(lPointsCerclePrecedent.get(i));
          dpl2.add(lPointsCercleActuel.get(i));

          lOS.add(new GM_Polygon(new GM_LineString(dpl2)));
        }

      }

      lPointsCerclePrecedent = lPointsCercleActuel;
    }

    double zActuel = radius * Math.sin(angleMaxLambda) + zc;

    DirectPositionList lPointsCercleActuel = new DirectPositionList();

    for (int i = 0; i < nbElemAlpha; i++) {

      lPointsCercleActuel.add(new DirectPosition(

      xc + Math.cos(angleMinAlpha + i * step) * Math.cos(angleMaxLambda)
          * radius, yc + Math.sin(angleMinAlpha + i * step)
          * Math.cos(angleMaxLambda) * radius, zActuel

      ));

    }

    lPointsCercleActuel.add(new DirectPosition(

    xc + Math.cos(angleMaxAlpha) * Math.cos(angleMaxLambda) * radius, yc
        + Math.sin(angleMaxAlpha) * Math.cos(angleMaxLambda) * radius, zActuel

    ));

    lBord3.add(lPointsCercleActuel.get(0));
    lBord4.add(lPointsCercleActuel.get(lPointsCercleActuel.size() - 1));

    lBord4 = Prism.invertList(lBord4);

    lBord3.add(center);
    lBord3.add(0, center);

    lBord4.add(center);
    lBord4.add(0, center);

    DirectPositionList lBord2 = (lPointsCercleActuel.clone());
    lBord2.add(center);
    lBord2.add(0, center);

    for (int i = 1; i < nbElemAlpha + 1; i++) {

      DirectPositionList dpl1 = new DirectPositionList();
      dpl1.add(lPointsCerclePrecedent.get(i - 1));

      dpl1.add(lPointsCercleActuel.get(i));
      dpl1.add(lPointsCercleActuel.get(i - 1));
      dpl1.add(lPointsCerclePrecedent.get(i - 1));

      lOS.add(new GM_Polygon(new GM_LineString(dpl1)));

      if (i != 0) {

        DirectPositionList dpl2 = new DirectPositionList();
        dpl2.add(lPointsCercleActuel.get(i));

        dpl2.add(lPointsCerclePrecedent.get(i - 1));
        dpl2.add(lPointsCerclePrecedent.get(i));
        dpl2.add(lPointsCercleActuel.get(i));

        lOS.add(new GM_Polygon(new GM_LineString(dpl2)));
      }

    }

    lOS.add(new GM_Polygon(new GM_LineString(lBord1)));
    lOS.add(new GM_Polygon(new GM_LineString(lBord2)));
    lOS.add(new GM_Polygon(new GM_LineString(lBord3)));
    lOS.add(new GM_Polygon(new GM_LineString(lBord4)));

    return lOS;

  }

  private static IDirectPositionList invertList(IDirectPositionList dpl) {
    IDirectPositionList lFinal = new DirectPositionList();

    int nbElem = dpl.size();

    for (int i = nbElem - 1; i >= 0; i--) {

      lFinal.add(dpl.get(i));
    }

    return lFinal;

  }

}
