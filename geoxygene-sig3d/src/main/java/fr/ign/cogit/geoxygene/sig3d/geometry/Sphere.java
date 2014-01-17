package fr.ign.cogit.geoxygene.sig3d.geometry;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_SolidBoundary;

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
 * Classe permettant de créer une géométrie de type sphère
 * 
 * Sphere geometry
 * 
 */
public class Sphere extends GM_Solid {

  static Logger logger = Logger.getLogger(GM_Solid.class.getName());

  /**
   * Permet de Génèrer une sphère et d'en choisir un niveau de détail
   * 
   * 
   * @param center centre de la sphère
   * @param radius rayon de la sphère
   * @param nbCircles détail de la sphère (nombre de disques la représentant)
   */
  public Sphere(IDirectPosition center, double radius, int nbCircles) {

    List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();

    // On vire les cas que l'on ne devrait pas considèrer
    // Si le centre est à NAN il y a une erreur
    if (radius <= 0) {
      Sphere.logger.error(Messages.getString("Sphere.Rayon"));
      return;

    }

    if (nbCircles <= 0) {

      Sphere.logger.error(Messages.getString("Sphere.Cercles"));
      return;
    }

    double xc = center.getX();
    double yc = center.getY();
    double zc = center.getZ();

    // Pour nbCercles on définit 4 * nbCercles sommets pour chaque cercle
    // Cela permet d'avoir un octoèdre pour nbCercles =1

    DirectPositionList lPointsCerclePrecedent = new DirectPositionList();
    DirectPosition poleNord = new DirectPosition(xc, yc, zc + radius);

    // On initialise la première ligne

    for (int i = 0; i < nbCircles * 2 + 2; i++) {

      lPointsCerclePrecedent.add(poleNord);

    }
    // On définit le pas angulaire
    double pas = Math.PI / (nbCircles + 1);

    // Pour chaque cercle
    for (int i = 0; i < nbCircles; i++) {

      // On initialise
      double zActuel = radius * Math.cos(pas * (i + 1)) + zc;

      double rayonCercleActuel = radius * Math.sin(pas * (i + 1));

      DirectPositionList lPointsCercleActuel = new DirectPositionList();

      // On ajoute le premier point (indice 0)
      lPointsCercleActuel.add(new DirectPosition(xc, yc + rayonCercleActuel,
          zActuel));

      // On construit le cercle pour un Z donné

      for (int j = 1; j < nbCircles * 2 + 2; j++) {

        DirectPosition pActuel = new DirectPosition(xc + rayonCercleActuel
            * Math.sin(pas * j), yc + rayonCercleActuel * Math.cos(pas * j),
            zActuel);

        lPointsCercleActuel.add(pActuel);

        DirectPositionList dpl1 = new DirectPositionList();
        dpl1.add(lPointsCerclePrecedent.get(j - 1));

        dpl1.add(lPointsCercleActuel.get(j));
        dpl1.add(lPointsCercleActuel.get(j - 1));
        dpl1.add(lPointsCerclePrecedent.get(j - 1));

        lOS.add(new GM_Polygon(new GM_LineString(dpl1)));

        if (i != 0) {

          DirectPositionList dpl2 = new DirectPositionList();
          dpl2.add(lPointsCercleActuel.get(j));

          dpl2.add(lPointsCerclePrecedent.get(j - 1));
          dpl2.add(lPointsCerclePrecedent.get(j));
          dpl2.add(lPointsCercleActuel.get(j));

          lOS.add(new GM_Polygon(new GM_LineString(dpl2)));
        }

      }

      // On clos le "collier"

      DirectPositionList dpl1 = new DirectPositionList();
      dpl1.add(lPointsCerclePrecedent.get(nbCircles * 2 + 1));

      dpl1.add(lPointsCercleActuel.get(0));
      dpl1.add(lPointsCercleActuel.get(nbCircles * 2 + 1));
      dpl1.add(lPointsCerclePrecedent.get(nbCircles * 2 + 1));

      lOS.add(new GM_Polygon(new GM_LineString(dpl1)));

      if (i != 0) {

        DirectPositionList dpl2 = new DirectPositionList();
        dpl2.add(lPointsCercleActuel.get(0));
        dpl2.add(lPointsCerclePrecedent.get(nbCircles * 2 + 1));
        dpl2.add(lPointsCerclePrecedent.get(0));

        dpl2.add(lPointsCercleActuel.get(0));

        lOS.add(new GM_Polygon(new GM_LineString(dpl2)));

      }

      lPointsCerclePrecedent = lPointsCercleActuel;
    }

    DirectPosition poleSud = new DirectPosition(xc, yc, zc - radius);
    // On initialise
    double zActuel = radius * Math.cos(pas * (nbCircles + 1)) + zc;

    double rayonCercleActuel = radius * Math.sin(pas * (nbCircles + 1));

    DirectPositionList lPointsCercleActuel = new DirectPositionList();

    // On ajoute le premier point (indice 0)
    lPointsCercleActuel.add(new DirectPosition(xc, yc + rayonCercleActuel,
        zActuel));

    // On construit le cercle pour un Z donné

    // Il manque la partie avec le pole sud

    for (int j = 1; j < nbCircles * 2 + 2; j++) {

      DirectPosition pActuel = new DirectPosition(xc + rayonCercleActuel
          * Math.sin(pas * j), yc + rayonCercleActuel * Math.cos(pas * j),
          zActuel);

      lPointsCercleActuel.add(pActuel);

      DirectPositionList dpl2 = new DirectPositionList();
      dpl2.add(poleSud);
      dpl2.add(lPointsCerclePrecedent.get(j - 1));
      dpl2.add(lPointsCerclePrecedent.get(j));

      dpl2.add(poleSud);

      lOS.add(new GM_Polygon(new GM_LineString(dpl2)));

    }

    DirectPositionList dpl1 = new DirectPositionList();
    dpl1.add(lPointsCerclePrecedent.get(nbCircles * 2 + 1));
    dpl1.add(lPointsCerclePrecedent.get(0));
    dpl1.add(poleSud);

    dpl1.add(lPointsCerclePrecedent.get(nbCircles * 2 + 1));

    lOS.add(new GM_Polygon(new GM_LineString(dpl1)));

    this.setBoundary(new GM_SolidBoundary(lOS));
  }

}
