/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.genealgorithms.polygon;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;

public class PolygonAggregation {

  private static Logger logger = Logger.getLogger(PolygonAggregation.class);
  private IPolygon polygon1, polygon2;

  public PolygonAggregation(IPolygon polygon1, IPolygon polygon2) {
    super();
    this.polygon1 = polygon1;
    this.polygon2 = polygon2;
  }

  /**
   * <p>
   * Amalgame deux surfaces en remplissant l'espace libre entre elles par la
   * méthode décrite dans la thèse de Nicolas Regnauld (page 150). Cette méthode
   * est appropriée pour les bâtiments.<br>
   * 
   * @param seuilLargeur : largeur de pont en-dessous de laquelle l'amalgamation
   *          n'est pas valide
   * @return Geometry : l'amalgamation des deux surfaces ou null si aucune
   *         solution valide n'est trouvée.
   */
  public IPolygon regnauldAmalgamation(double widthThreshold) {

    // si les géométries s'intersectent, renvoie null
    if (polygon1.intersects(polygon2))
      return null;

    // on commence par créer l'enveloppe convexe des deux surfaces
    IPolygon hull = (IPolygon) polygon1.union(polygon2).convexHull();

    // il faut maintenant déterminer les deux segments de l'enveloppe qui lient
    // les
    // deux surfaces à amalgamer.
    // pour cela on va parcourir les vertices de l'enveloppe et marquer le
    // précédent et le suivant lorsque l'on change de géométrie
    int indexA1 = 0, indexA2 = 0, indexB1 = 0, indexB2 = 0;
    int nbFound = 0;
    boolean surA = false;
    // on fait une boucle tant qu'on n'a pas trouvé ces deux segments
    IPolygon currentPol = null;
    int i = 0;
    while (nbFound < 4) {
      IDirectPosition coord = hull.coord().get(i);

      // cas du 1er vertex
      if (currentPol == null) {
        if (polygon1.exteriorLineString().intersects(coord.toGM_Point())) {
          currentPol = polygon1;
          surA = true;
        } else {
          currentPol = polygon2;
        }
        // on passe au vertex suivant
        i++;
        continue;
      }

      // cas général
      if (!currentPol.exteriorLineString().intersects(coord.toGM_Point())) {
        // on a changé de géométrie donc on marque ce point et
        // le précédent
        if (nbFound == 0) {
          // c'est le premier segment
          if (surA) {
            indexA1 = i - 1;
            indexB1 = i;
            surA = false;
            currentPol = polygon2;
          } else {
            indexA1 = i;
            indexB1 = i - 1;
            surA = true;
            currentPol = polygon1;
          }
        } else {
          // c'est le deuxième segment
          if (surA) {
            indexA2 = i - 1;
            indexB2 = i;
            surA = false;
            currentPol = polygon2;
          } else {
            indexA2 = i;
            indexB2 = i - 1;
            surA = true;
            currentPol = polygon1;
          }
        }
        nbFound += 2;
      }
      // on passe au vertex suivant
      i++;
    }// while, boucle sur les vertices de l'enveloppe convexe

    // arrivé là, on a marqué les 4 points. Il faut maintenant construire
    // les 2 arêtes partant de chaque point
    // on commence par les arêtes de geomA dont on récupère les points 1 et 2
    IDirectPosition coordA1 = hull.coord().get(indexA1);
    IDirectPosition coordA2 = hull.coord().get(indexA2);

    // modif Nico, il faut récupérer aussi l'enveloppe convexe du contour de
    // geomA
    // et l'utiliser pour faire le test d'appartenance du milieu de segment a
    // l'interieur
    // de l'enveloppe de l'amalgamme.
    IPolygon hullPol1 = (IPolygon) polygon1.convexHull();

    // il faut marquer ringA à coordA1 et coordA2
    int indexA1Pol = CommonAlgorithmsFromCartAGen
        .getNearestVertexPositionFromPoint(polygon1, coordA1);
    int indexA2Pol = CommonAlgorithmsFromCartAGen
        .getNearestVertexPositionFromPoint(polygon1, coordA2);
    int indexA1Hull = CommonAlgorithmsFromCartAGen
        .getNearestVertexPositionFromPoint(hullPol1, coordA1);
    int indexA2Hull = CommonAlgorithmsFromCartAGen
        .getNearestVertexPositionFromPoint(hullPol1, coordA2);

    // on construit les deux segments internes et externes de A1
    // pour cela, on récupère les vertices précédents et suivants
    IDirectPosition coordPrecA1 = polygon1.coord().get(indexA1Pol - 1);
    IDirectPosition coordSuivA1 = polygon1.coord().get(indexA1Pol + 1);
    IDirectPosition coordConvPrecA1 = null;
    if (indexA1Hull > 0)
      coordConvPrecA1 = hullPol1.coord().get(indexA1Hull - 1);
    else
      coordConvPrecA1 = hullPol1.coord().get(hullPol1.coord().size() - 2);

    // il faut maintenant déterminer lequel est intérieur et lequel est
    // extérieur
    Segment segIA1, segEA1;
    // on construit donc les coordonnées du milieu du segment
    double xMilieu = (coordConvPrecA1.getX() + coordA1.getX()) / 2;
    double yMilieu = (coordConvPrecA1.getY() + coordA1.getY()) / 2;
    if (hull.contains(new DirectPosition(xMilieu, yMilieu).toGM_Point())) {
      segIA1 = new Segment(coordA1, coordPrecA1);
      segEA1 = new Segment(coordA1, coordSuivA1);
    } else {
      segEA1 = new Segment(coordA1, coordPrecA1);
      segIA1 = new Segment(coordA1, coordSuivA1);
    }
    // on construit les deux segments internes et externes de A2
    // pour cela, on récupère les vertices précédents et suivants
    IDirectPosition coordPrecA2 = null;
    if (indexA2Pol > 0)
      coordPrecA2 = polygon1.coord().get(indexA2Pol - 1);
    else
      coordPrecA2 = polygon1.coord().get(polygon1.coord().size() - 2);
    IDirectPosition coordSuivA2 = polygon1.coord().get(indexA2Pol + 1);
    IDirectPosition coordConvPrecA2 = null;
    if (indexA2Hull > 0)
      coordConvPrecA2 = hullPol1.coord().get(indexA2Hull - 1);
    else
      coordConvPrecA2 = hullPol1.coord().get(hullPol1.coord().size() - 2);

    // il faut maintenant déterminer lequel est intérieur et lequel est
    // extérieur
    Segment segIA2, segEA2;
    // on construit donc les coordonnées du milieu du segment
    xMilieu = (coordConvPrecA2.getX() + coordA2.getX()) / 2;
    yMilieu = (coordConvPrecA2.getY() + coordA2.getY()) / 2;
    if (hull.contains(new DirectPosition(xMilieu, yMilieu).toGM_Point())) {
      segIA2 = new Segment(coordA2, coordPrecA2);
      segEA2 = new Segment(coordA2, coordSuivA2);
    } else {
      segEA2 = new Segment(coordA2, coordPrecA2);
      segIA2 = new Segment(coordA2, coordSuivA2);
    }

    // puis on construit les arêtes de geomB dont on récupère les points 1 et 2
    // sous forme de coordonnées
    IDirectPosition coordB1 = hull.coord().get(indexB1);
    IDirectPosition coordB2 = hull.coord().get(indexB2);

    // modif Nico, il faut récupérer l'enveloppe convexe du contour de geomB
    IPolygon hullPol2 = (IPolygon) polygon2.convexHull();
    // il faut marquer ringB à coordB1 et coordB2
    int indexB1Pol = CommonAlgorithmsFromCartAGen
        .getNearestVertexPositionFromPoint(polygon2, coordB1);
    int indexB2Pol = CommonAlgorithmsFromCartAGen
        .getNearestVertexPositionFromPoint(polygon2, coordB2);
    int indexB1Hull = CommonAlgorithmsFromCartAGen
        .getNearestVertexPositionFromPoint(hullPol2, coordB1);
    int indexB2Hull = CommonAlgorithmsFromCartAGen
        .getNearestVertexPositionFromPoint(hullPol2, coordB2);

    // on construit les deux segments internes et externes de B1
    // pour cela, on récupère les vertices précédents et suivants
    IDirectPosition coordPrecB1 = null;
    if (indexB1Pol > 0)
      coordPrecB1 = polygon2.coord().get(indexB1Pol - 1);
    else
      coordPrecB1 = polygon2.coord().get(polygon2.coord().size() - 2);
    IDirectPosition coordSuivB1 = polygon2.coord().get(indexB1Pol + 1);
    IDirectPosition coordConvPrecB1 = null;
    if (indexB1Hull > 0)
      coordConvPrecB1 = hullPol2.coord().get(indexB1Hull - 1);
    else
      coordConvPrecB1 = hullPol2.coord().get(hullPol2.coord().size() - 2);

    // il faut maintenant déterminer lequel est intérieur et lequel est
    // extérieur
    Segment segIB1, segEB1;
    // on construit donc les coordonnées du milieu du segment
    xMilieu = (coordConvPrecB1.getX() + coordB1.getX()) / 2;
    yMilieu = (coordConvPrecB1.getY() + coordB1.getY()) / 2;
    if (hull.contains(new DirectPosition(xMilieu, yMilieu).toGM_Point())) {
      segIB1 = new Segment(coordB1, coordPrecB1);
      segEB1 = new Segment(coordB1, coordSuivB1);
    } else {
      segEB1 = new Segment(coordB1, coordPrecB1);
      segIB1 = new Segment(coordB1, coordSuivB1);
    }
    // on construit les deux segments internes et externes de B2
    // pour cela, on récupère les vertices précédents et suivants
    IDirectPosition coordPrecB2 = null;
    if (indexB2Pol > 0)
      coordPrecB2 = polygon2.coord().get(indexB2Pol - 1);
    else
      coordPrecB2 = polygon2.coord().get(polygon2.coord().size() - 2);
    IDirectPosition coordSuivB2 = polygon2.coord().get(indexB2Pol + 1);
    IDirectPosition coordConvPrecB2 = null;
    if (indexB2Hull > 0)
      coordConvPrecB2 = hullPol2.coord().get(indexB2Hull - 1);
    else
      coordConvPrecB2 = hullPol2.coord().get(hullPol2.coord().size() - 2);

    // il faut maintenant déterminer lequel est intérieur et lequel est
    // extérieur
    Segment segIB2, segEB2;
    // on construit donc les coordonnées du milieu du segment
    xMilieu = (coordConvPrecB2.getX() + coordB2.getX()) / 2;
    yMilieu = (coordConvPrecB2.getY() + coordB2.getY()) / 2;

    // Nico modif, bug correction, 11/05/09
    if (hull.contains(new DirectPosition(xMilieu, yMilieu).toGM_Point())) {
      segIB2 = new Segment(coordB2, coordPrecB2);
      segEB2 = new Segment(coordB2, coordSuivB2);
    } else {
      segEB2 = new Segment(coordB2, coordPrecB2);
      segIB2 = new Segment(coordB2, coordSuivB2);
    }

    // ******************************************************************************
    // arrivé là, on a les 4 segments qui vont nous permettre d'amalgamer les
    // surfaces
    // il faut maintenant calculer les intersections interne/interne et
    // externe/externe
    IDirectPosition coordE1 = segEA1.straightLineIntersection(segEB1);
    IDirectPosition coordE2 = segEA2.straightLineIntersection(segEB2);
    IDirectPosition coordI1 = segIA1.straightLineIntersection(segIB1);
    IDirectPosition coordI2 = segIA2.straightLineIntersection(segIB2);

    // ****************************************************************
    // il faut maintenant choisir les 2 points qui vont servir à l'amalgamation
    // on commence par tester avec I1 et I2
    HashMap<IPolygon, Double> amalgamationsValides = new HashMap<IPolygon, Double>();
    IDirectPosition liaison1 = coordI1, liaison2 = coordI2;
    for (int j = 0; j < 4; j++) {
      IPolygon amalgTest = assembleSurfParPtLiaison(polygon1, coordA1, coordA2,
          liaison1, polygon2, coordB1, coordB2, liaison2);
      if (amalgTest.isValid()) {
        // on récupère l'espace ajouté par l'amalgamation
        boolean valide = true;

        // TODO write a method to measure local minimum width
        double minLargeur = widthThreshold;

        if (minLargeur < widthThreshold) {
          valide = false;
        }
        double aire = amalgTest.area();
        // on teste la validité de cette amalgamation
        if (valide) {
          amalgamationsValides.put(amalgTest, aire);
        }
      }

      if (j == 0) {
        liaison1 = coordE1;
      }
      if (j == 1) {
        liaison2 = coordE2;
      }
      if (j == 2) {
        liaison1 = coordI1;
      }
    }
    // si aucune solution n'est valide, on renvoie null
    if (amalgamationsValides.size() == 0) {
      logger.warn("no valid amalgamation");
      return null;
    }

    if (logger.isTraceEnabled())
      logger.trace(amalgamationsValides);

    // on cherche maintenant la meilleure solution parmi les valides
    // c'est celle dont la surface ajoutée est la plus petite
    double min = 2 * hull.area();
    Iterator<IPolygon> iter = amalgamationsValides.keySet().iterator();
    IPolygon amalgam = null;
    while (iter.hasNext()) {
      IPolygon geom = iter.next();
      double surface = amalgamationsValides.get(geom).doubleValue();
      if (surface < min) {
        min = surface;
        amalgam = geom;
      }
    }

    return amalgam;
  }

  private IPolygon assembleSurfParPtLiaison(IPolygon geomA,
      IDirectPosition pointA1, IDirectPosition pointA2,
      IDirectPosition liaison1, IPolygon geomB, IDirectPosition pointB1,
      IDirectPosition pointB2, IDirectPosition liaison2) {

    IDirectPositionList coords = new DirectPositionList();

    // on part de la géométrie A
    ILineString contourA = geomA.exteriorLineString();
    IPolygon enveloppe = (IPolygon) polygon1.union(polygon2).convexHull();
    IDirectPosition depart = pointA1;
    IDirectPosition courante = null;

    // arrivé à pointA1, on l'ajoute à ring
    coords.add(depart);
    // on ajoute liaison1
    coords.add(liaison1);
    // on ajoute pointB1
    coords.add(pointB1);

    // on parcourt maintenant geomB jusqu'à pointB2
    ILineString contourB = geomB.exteriorLineString();
    int indexB1 = CommonAlgorithmsFromCartAGen
        .getNearestVertexPositionFromPoint(contourB, pointB1);

    // modif Nico, to step vertex in right direction 11/05/09
    // contourB.mStepVertex(1, 1);
    int step = getStepDirection(enveloppe, contourB, indexB1);
    int index = indexB1;
    index += step;
    if (index == -1)
      index = contourB.coord().size() - 2;
    courante = contourB.getControlPoint(index);
    while (!courante.equals(pointB2)) {
      // on ajoute courante à surf
      coords.add(courante);
      // on passe au vertex suivant
      // modif Nico, to step vertex in right direction 11/05/09
      index += step;
      if (index == -1)
        index = contourB.coord().size() - 2;
      courante = contourB.getControlPoint(index);
    }

    // arrivé à pointB2, on l'ajoute à ring
    coords.add(pointB2);
    // on ajoute liaison2
    coords.add(liaison2);
    // on ajoute pointA2
    coords.add(pointA2);

    // on parcourt maintenant geomA jusqu'à depart
    int indexA2 = CommonAlgorithmsFromCartAGen
        .getNearestVertexPositionFromPoint(contourA, pointA2);
    // modif Nico, to step vertex in right direction 11/05/09
    // contourA.mStepVertex(2, 1);
    step = getStepDirection(enveloppe, contourA, indexA2);
    index = indexA2;
    index += step;
    if (index == -1)
      index = contourA.coord().size() - 2;
    courante = contourA.getControlPoint(index);
    while (!courante.equals(depart)) {
      // on ajoute courante à surf
      coords.add(courante);
      // on passe au vertex suivant
      // modif Nico, to step vertex in right direction 11/05/09
      index += step;
      if (index == -1)
        index = contourA.coord().size() - 2;
      courante = contourA.getControlPoint(index);
    }

    // close the ring
    coords.add(depart);

    return GeometryEngine.getFactory().createIPolygon(coords);
  }

  private int getStepDirection(IPolygon enveloppe, ILineString contour,
      int contourIndex) {
    int res = 0;
    contour.convexHull();
    IDirectPosition nextPt = contour.coord().get(contourIndex + 1);

    if (enveloppe.contains(nextPt.toGM_Point())
        && enveloppe.exteriorLineString().disjoint(nextPt.toGM_Point())) {
      res = -1;
    } else
      res = 1;

    return res;
  }
}
