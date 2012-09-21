package fr.ign.cogit.geoxygene.sig3d.geometry.topology;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.sig3d.Messages;
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
 * Classe permettant de créer une sphère avec une topologie
 * 
 * Class for the calculation of a topologic sphere
 * 
 */
public class TopoSphere {

  private ArrayList<Triangle> lTriangles = null;

  public ArrayList<Triangle> getLTriangles() {
    return this.lTriangles;

  }

  private ArrayList<Vertex> lVertex = new ArrayList<Vertex>();

  public ArrayList<Vertex> getLVertex() {
    return this.lVertex;

  }

  static Logger logger = Logger.getLogger(GM_Solid.class.getName());

  /**
   * Permet de calculer une sphère en appliquant la topologie du package
   * fr.ign.cogit.geoxygene.sig3d.tetraedrisation
   * 
   * @param center le centre de la sphère
   * @param radius le rayon de la sphère
   * @param nbCircles le nombre de méridiens décrivant la sphère
   */
  public TopoSphere(IDirectPosition center, double radius, int nbCircles) {

    ArrayList<Triangle> lOS = new ArrayList<Triangle>();

    // On vire les cas que l'on ne devrait pas considèrer
    // Si le centre est à NAN il y a une erreur
    if (radius <= 0) {
      TopoSphere.logger.error(Messages.getString("Sphere.Rayon"));
      return;

    }

    if (nbCircles <= 0) {

      TopoSphere.logger.error(Messages.getString("Sphere.Cercles"));
      return;
    }

    double xc = center.getX();
    double yc = center.getY();
    double zc = center.getZ();

    // Pour nbCercles on définit 4 * nbCercles sommets pour chaque cercle
    // Cela permet d'avoir un octoèdre pour nbCercles =1

    ArrayList<Vertex> lPointsCerclePrecedent = new ArrayList<Vertex>();
    Vertex poleNord = new Vertex(xc, yc, zc + radius);

    this.lVertex.add(poleNord);

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

      ArrayList<Vertex> lPointsCercleActuel = new ArrayList<Vertex>();

      // On ajoute le premier point (indice 0)
      lPointsCercleActuel.add(new Vertex(xc, yc + rayonCercleActuel, zActuel));
      this.lVertex.add(lPointsCercleActuel.get(0));

      // On construit le cercle pour un Z donné

      for (int j = 1; j < nbCircles * 2 + 2; j++) {

        Vertex pActuel = new Vertex(xc + rayonCercleActuel * Math.sin(pas * j),
            yc + rayonCercleActuel * Math.cos(pas * j), zActuel);

        lPointsCercleActuel.add(pActuel);

        Vertex[] som = new Vertex[3];

        som[0] = lPointsCerclePrecedent.get(j - 1);

        som[1] = lPointsCercleActuel.get(j);
        som[2] = lPointsCercleActuel.get(j - 1);

        this.lVertex.add(lPointsCercleActuel.get(j));

        Triangle t = new Triangle();

        som[0].ajouteTriangle(t);
        som[1].ajouteTriangle(t);
        som[2].ajouteTriangle(t);

        t.setLSommets(som);

        lOS.add(t);

        if (i != 0) {

          t = new Triangle();

          som = new Vertex[3];
          som[0] = lPointsCercleActuel.get(j);
          som[1] = lPointsCerclePrecedent.get(j - 1);
          som[2] = lPointsCerclePrecedent.get(j);

          som[0].ajouteTriangle(t);
          som[1].ajouteTriangle(t);
          som[2].ajouteTriangle(t);

          t.setLSommets(som);

          lOS.add(t);
        }

      }

      // On clos le "collier"

      Vertex[] som = new Vertex[3];
      som[0] = lPointsCerclePrecedent.get(nbCircles * 2 + 1);
      som[1] = lPointsCercleActuel.get(0);
      som[2] = lPointsCercleActuel.get(nbCircles * 2 + 1);

      Triangle t = new Triangle();
      som[0].ajouteTriangle(t);
      som[1].ajouteTriangle(t);
      som[2].ajouteTriangle(t);

      t.setLSommets(som);

      lOS.add(t);

      if (i != 0) {

        som = new Vertex[3];

        som[0] = lPointsCercleActuel.get(0);
        som[1] = lPointsCerclePrecedent.get(nbCircles * 2 + 1);
        som[2] = lPointsCerclePrecedent.get(0);

        t = new Triangle();
        som[0].ajouteTriangle(t);
        som[1].ajouteTriangle(t);
        som[2].ajouteTriangle(t);

        t.setLSommets(som);

        lOS.add(t);

      }

      lPointsCerclePrecedent = lPointsCercleActuel;
    }

    Vertex poleSud = new Vertex(xc, yc, zc - radius);

    // On initialise
    double zActuel = radius * Math.cos(pas * (nbCircles + 1)) + zc;

    double rayonCercleActuel = radius * Math.sin(pas * (nbCircles + 1));

    this.lVertex.add(poleSud);

    ArrayList<Vertex> lPointsCercleActuel = new ArrayList<Vertex>();

    // On ajoute le premier point (indice 0)
    lPointsCercleActuel.add(new Vertex(xc, yc + rayonCercleActuel, zActuel));

    // On construit le cercle pour un Z donné

    // Il manque la partie avec le pole sud

    for (int j = 1; j < nbCircles * 2 + 2; j++) {

      Vertex pActuel = lPointsCerclePrecedent.get(j);

      lPointsCercleActuel.add(pActuel);

      Vertex[] som = new Vertex[3];
      som[0] = poleSud;
      som[1] = lPointsCerclePrecedent.get(j - 1);
      som[2] = lPointsCerclePrecedent.get(j);

      Triangle t = new Triangle();
      som[0].ajouteTriangle(t);
      som[1].ajouteTriangle(t);
      som[2].ajouteTriangle(t);

      t.setLSommets(som);

      lOS.add(t);

    }

    Vertex[] som = new Vertex[3];
    som[0] = lPointsCerclePrecedent.get(nbCircles * 2 + 1);
    som[1] = lPointsCerclePrecedent.get(0);
    som[2] = poleSud;

    Triangle t = new Triangle();
    som[0].ajouteTriangle(t);
    som[1].ajouteTriangle(t);
    som[2].ajouteTriangle(t);

    t.setLSommets(som);

    lOS.add(t);

    this.lTriangles = lOS;

  }

}
