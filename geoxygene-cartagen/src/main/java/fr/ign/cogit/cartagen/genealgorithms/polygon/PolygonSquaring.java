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

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.AbstractGeomFactory;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomengine.AbstractGeometryEngine;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;

/**
 * Naive Implementation of an algorithm to try to square polygons i.e when
 * angles are quasi flat, or right, then try to make them really flat or right
 * @author ILokhat
 * 
 */
public class PolygonSquaring {

  private static final Logger logger = Logger.getLogger(PolygonSquaring.class);

  private int nb_edges;
  private IDirectPositionList points;
  private double[] angles;
  private Vecteur[] vecs;
  private int primaryAxe;
  private double[] align;
  private double angTol = 8 * Math.PI / 180;
  private double correctTol = 0.6 * Math.PI / 180;
  private IPolygon polygon;

  public PolygonSquaring(IPolygon p) {
    GeometryEngine.init();
    this.polygon = p;
  }

  public PolygonSquaring(IPolygon p, double angTol, double correctTol) {
    this(p);
    this.angTol = angTol;
    this.correctTol = correctTol;
  }

  private void simpleSquaringInit() {
    this.points = polygon.exteriorCoord();
    this.nb_edges = points.size() - 1;
    this.vecs = new Vecteur[nb_edges];
    this.angles = new double[nb_edges];
    this.align = new double[nb_edges];

    // constructing vector from points and getting primary axe
    primaryAxe = 0;
    double normeMax = 0;
    for (int i = 0; i < this.nb_edges; ++i) {
      if (logger.isTraceEnabled())
        logger.trace("v[" + i + "] = p" + i + "p" + (i + 1) % nb_edges);
      vecs[i] = new Vecteur(points.get(i), points.get((i + 1) % nb_edges));
      if (logger.isTraceEnabled())
        logger
            .trace("vec" + i + " (" + vecs[i].getX() + " ; " + vecs[i].getY());
      if (vecs[i].norme() > normeMax) {
        normeMax = vecs[i].norme();
        primaryAxe = i;
      }
    }
    // computing angles between vectors
    angles[0] = Math.PI
        - vecs[vecs.length - 1].angleVecteur(vecs[0]).getValeur();
    for (int i = 0; i < vecs.length - 1; ++i) {
      angles[i + 1] = Math.PI - vecs[i].angleVecteur(vecs[i + 1]).getValeur();
      align[i] = Math.abs(vecs[i].prodVectoriel(vecs[primaryAxe]).getZ());
    }
    // this.printAngles();
    align[align.length - 1] = Math.abs(vecs[align.length - 1].prodVectoriel(
        vecs[primaryAxe]).getZ());
  }

  private void update() {
    for (int i = 0; i < nb_edges; ++i) {
      vecs[i] = new Vecteur(points.get(i), points.get((i + 1) % nb_edges));
    }
    angles[0] = Math.PI
        - vecs[vecs.length - 1].angleVecteur(vecs[0]).getValeur();
    for (int i = 0; i < vecs.length - 1; ++i) {
      angles[i + 1] = Math.PI - vecs[i].angleVecteur(vecs[i + 1]).getValeur();
      align[i] = Math.abs(vecs[i].prodVectoriel(vecs[primaryAxe]).getZ());
    }
    align[align.length - 1] = Math.abs(vecs[align.length - 1].prodVectoriel(
        vecs[primaryAxe]).getZ());
  }

  // gets the vectors around point i
  private int[] getVecsAroundPoint(int i) {
    int v1 = i == 0 ? vecs.length - 1 : i - 1;
    int v2 = i;
    return new int[] { v1, v2 };
  }

  // get indice of the point we we'll move
  private int getPointToMove(int indVertice, int indVecToMove) {
    if (indVecToMove == indVertice)
      return (indVertice + 1) % nb_edges;
    if (indVertice == 0)
      return nb_edges - 1;
    return indVertice - 1;
  }

  private double signedAngle(Vecteur v1, Vecteur v2) {
    return Math.atan2(v2.getY(), v2.getX()) - Math.atan2(v1.getY(), v1.getX());
  }

  // compute the squared up rotation of VecToMove from VecFixed
  private Vecteur rotateVec(int deg, int vecToMove, int vecFixed, int i) {
    Vecteur vSquared = null;
    if (deg == 90) {
      vSquared = new Vecteur(-vecs[vecFixed].getY(), vecs[vecFixed].getX());
      // vSquared = vSquared.multConstante(vecs[vecToMove].norme()
      // / vSquared.norme());
      vSquared.normalise();
      vSquared = vSquared.multConstante(vecs[vecToMove].norme());
      // si le vecteur à bouger est celui apres le point
      if (vecToMove == i) {
        if (vSquared.prodScalaire(vecs[vecToMove]) < 0)
          vSquared = vSquared.multConstante(-1);
      } else { // si avant le point
        if (vSquared.prodScalaire(vecs[vecToMove]) > 0)
          vSquared = vSquared.multConstante(-1);
      }
      logger.trace("vortho : " + vSquared.getX() + " ; " + vSquared.getY());
      logger.trace("vtomov : " + vecs[vecToMove].getX() + " ; "
          + vecs[vecToMove].getY());
      return vSquared;
    }
    if (deg == 45) {
      // x' = x.cos pi/4 - y.sin pi/4 -- cos Pi/4=0.7071067811865476=sin pi/4
      // y' = x.sin pi/4 + y.cos pi/4
      Vecteur vp = vecs[vecFixed];
      int v1 = vecToMove, v2 = vecFixed;
      // si le vecteur à bouger est celui apres le point
      if (vecToMove == i) {
        vp = vecs[vecFixed].multConstante(-1);
        v1 = vecFixed;
        v2 = vecToMove;
        if (signedAngle(vecs[v1], vecs[v2]) < 0) {
          double x = 0.7071067811865476 * (vp.getX() - vp.getY());
          double y = 0.7071067811865476 * (vp.getX() + vp.getY());
          vSquared = (new Vecteur(x, y));
        } else { // sens anti trigo on tourne de -Pi/4
          double x = 0.7071067811865476 * (vp.getX() + vp.getY());
          double y = 0.7071067811865476 * (-1 * vp.getX() + vp.getY());
          vSquared = (new Vecteur(x, y));
        }
      } else { // vecteur à bouger avant le point
        if (signedAngle(vecs[v1], vecs[v2]) > 0) {
          double x = 0.7071067811865476 * (vp.getX() - vp.getY());
          double y = 0.7071067811865476 * (vp.getX() + vp.getY());
          vSquared = (new Vecteur(x, y));
        } else { // sens anti trigo on tourne de -Pi/4
          double x = 0.7071067811865476 * (vp.getX() + vp.getY());
          double y = 0.7071067811865476 * (-1 * vp.getX() + vp.getY());
          vSquared = (new Vecteur(x, y));
        }
      }
      return vSquared;
    }
    if (deg == 0) {
      Vecteur vp = vecs[vecFixed];
      // si le vecteur à bouger est celui avant le point
      if (vecToMove != i)
        vp = vecs[vecFixed].multConstante(-1).getNormalised();
      vp = vp.getNormalised().multConstante(vecs[vecToMove].norme());
      return vp;
    }
    return vSquared;
  }

  /**
   * Naive Implementation of an algorithm to try to square polygons i.e when
   * angles are quasi flat, or right, then try to make them really flat or
   * right.
   * @returna a IPolygon with its angles squared or flattened
   */
  public IPolygon simpleSquaring() {
    // initialisation
    logger.debug("initialisation");
    simpleSquaringInit();

    // first pass : flattening quasi flat angles
    logger.debug("first pass -- flattening angles");

    for (int i = 0; i < angles.length; ++i) {
      if (Math.abs((Math.PI - angles[i])) <= this.angTol
          && Math.abs((Math.PI - angles[i])) > correctTol) {
        int[] v = getVecsAroundPoint(i);
        Vecteur vv = vecs[v[0]].ajoute(vecs[v[1]]);
        vv.normalise();
        double norme = vecs[v[0]].norme()
            * Math.cos(vecs[v[0]].angleVecteur(vv).getValeur());
        Vecteur vSquared = vv.multConstante(norme);
        IDirectPosition p = new DirectPosition(points.get(v[0]).getX(), points
            .get(v[0]).getY());
        p.setCoordinate(p.getX() + vSquared.getX(), p.getY() + vSquared.getY());
        points.set(i, p);
        update();
        logger.trace("Angle " + i + " now flattened " + angles[i] * 180
            / Math.PI);
      }
    }

    // second pass right angles and half right angles
    logger.debug("second pass");
    for (int i = 0; i < angles.length; ++i) {
      logger.debug("Testing point " + i + " -- Angle " + angles[i] * 180
          / Math.PI);
      // testing pi/2 candidates
      if (Math.abs((Math.PI / 2 - angles[i])) <= this.angTol
          && Math.abs((Math.PI / 2 - angles[i])) > correctTol) {
        int[] v = getVecsAroundPoint(i);
        int vecToMove = v[0], vecFixed = v[1];
        if (align[vecToMove] < align[vecFixed]) {
          vecToMove = v[1];
          vecFixed = v[0];
        }
        int pointToMove = getPointToMove(i, vecToMove);
        int angleBefore = v[0];// i == 0 ? nb_edges - 1 : i - 1;
        boolean anglePrecIsRight = Math.abs(angles[angleBefore] - Math.PI / 2) < correctTol;
        if (anglePrecIsRight && vecToMove == pointToMove)
          continue;
        logger.trace("Calculating new pos for point " + pointToMove);
        Vecteur vSquared = rotateVec(90, vecToMove, vecFixed, i);
        IDirectPosition p = new DirectPosition(points.get(i).getX(), points
            .get(i).getY());
        p.setCoordinate(p.getX() + vSquared.getX(), p.getY() + vSquared.getY());
        points.set(pointToMove, p);

        update();
        logger
            .trace("Angle " + i + " now squared " + angles[i] * 180 / Math.PI);
      } // testing pi/4 candidates
      else if (Math.abs((Math.PI / 4 - angles[i])) <= this.angTol
          && Math.abs((Math.PI / 4 - angles[i])) > 0.01) {
        int[] v = getVecsAroundPoint(i);
        int vecToMove = v[0], vecFixed = v[1];
        if (align[vecToMove] < align[vecFixed]) {
          vecToMove = v[1];
          vecFixed = v[0];
        }
        int pointToMove = getPointToMove(i, vecToMove);
        int angleBefore = v[0];// i == 0 ? nb_edges - 1 : i - 1;
        int angleAfter = (i + 1) % nb_edges;
        boolean angleBeforeIsRight = Math.abs(angles[angleBefore]
            - angles[angleBefore]) < correctTol;
        boolean angleAfterIsRight = Math.abs(angles[angleAfter]
            - angles[angleAfter]) < correctTol;
        if (angleBeforeIsRight && angleAfterIsRight)
          continue;
        logger.trace("Calculating new pos for point " + pointToMove);
        Vecteur vSquared = rotateVec(45, vecToMove, vecFixed, i);
        IDirectPosition p = new DirectPosition(points.get(i).getX(), points
            .get(i).getY());
        p.setCoordinate(p.getX() + vSquared.getX(), p.getY() + vSquared.getY());
        points.set(pointToMove, p);
        update();
        logger.trace("Angle " + i + " now half squared " + angles[i] * 180
            / Math.PI);
      }
    }
    points.set(points.size() - 1, points.get(0));
    AbstractGeomFactory factory = AbstractGeometryEngine.getFactory();
    return factory.createIPolygon(points);
  }

  /**
   * returns a wkt string of the polygon
   * @return
   */
  public String asWKT() {
    String s = "POLYGON((";
    for (int i = 0; i < nb_edges; ++i)
      s += points.get(i).getX() + " " + points.get(i).getY() + ", ";
    s += points.get(0).getX() + " " + points.get(0).getY() + "))";
    return s;
  }

}
