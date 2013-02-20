/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.cartagen.genealgorithms.section;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * Computation of a the coalescence algorithm on a linear SectionAgent
 * @author jGaffuri 09/07/2007 (updated J. Renard 22/04/2010)
 * 
 */

public class LineCurvatureSmoothing {
  private static Logger logger = Logger.getLogger(LineCurvatureSmoothing.class
      .getName());

  /**
   * Linear section on which the curvature smoothing is performed
   */
  private INetworkSection geneObj;

  /**
   * Symbol width: half of the internal part of the line + half of the line
   * border
   */
  private double symbolWidth = 0.0;

  /**
   * Data resolution: defined in the generalisation parameters
   */
  private double resolution = 0.0;

  /**
   * Default constructor
   * @param geoObj
   */
  public LineCurvatureSmoothing(INetworkSection geneObj) {
    this.geneObj = geneObj;
    this.symbolWidth = SectionSymbol.getUsedSymbolWidth(geneObj) / 2;
    this.resolution = GeneralisationSpecifications.getRESOLUTION();
  }

  /**
   * Computation of the curvature smoothing on geoObj
   * @param isPersistantObject
   */

  public void compute(boolean isPersistantObject) {

    // Geometry conversion
    IGeometry geomStart = this.geneObj.getGeom();
    if (!(geomStart instanceof ILineString)) {
      if (LineCurvatureSmoothing.logger.isInfoEnabled()) {
        LineCurvatureSmoothing.logger
            .info("Impossible to perform Curvature smoothing - non linear geometry");
      }
      return;
    }
    if (geomStart.coord().size() < 3) {
      if (LineCurvatureSmoothing.logger.isInfoEnabled()) {
        LineCurvatureSmoothing.logger
            .info("Impossible to perform Curvature smoothing - not enough points on geometry");
      }
      return;
    }
    ILineString geoxLine = (ILineString) geomStart;
    if (geoxLine.isClosed()) {
      return;
    }

    // Algorithm
    ILineString geomResult = LineCurvatureSmoothing.lineCurvatureSmoothing(
        geoxLine, this.symbolWidth, this.resolution);

    // Affectation to the object
    if (geomResult != null) {
      this.geneObj.setGeom(geomResult);
      if (isPersistantObject) {
        this.geneObj.registerDisplacement();
      }
      if (LineCurvatureSmoothing.logger.isInfoEnabled()) {
        LineCurvatureSmoothing.logger.info("Curvature smoothing performed on "
            + this.geneObj);
      }
    }

  }

  /**
   * Static computation of the curvature smoothing on a simple LineString
   * @param ls : the line to be treated
   * @param symbolWidth : symbol width of the line
   * @param resolution : data resolution
   * @return the smoothed simple line
   */

  public static ILineString lineCurvatureSmoothing(ILineString ls,
      double symbolWidth, double resolution) {

    // message au cas ou la ligne est fermee
    if (ls.isClosed()) {
      LineCurvatureSmoothing.logger
          .warn("lissage courbure ne traite pas les lignes fermees.");
      return null;
    }

    if (ls.coord().size() <= 2) {
      return null;
    }

    // table des abscisses curvilignes de points de la ligne initiale
    double[] absCurv = new double[ls.coord().size()];
    absCurv[0] = 0.0;
    double s = 0.0;
    for (int k = 1; k < ls.coord().size(); k++) {
      s += ls.coord().get(k - 1).distance(ls.coord().get(k));
      absCurv[k] = s;
    }

    // table des directions des segments de la ligne initiale (en radian entre
    // -Pi et Pi)
    double[] direction = new double[ls.coord().size() - 1];
    for (int k = 0; k < ls.coord().size() - 1; k++) {
      direction[k] = Math.atan2(ls.coord().get(k + 1).getY()
          - ls.coord().get(k).getY(), ls.coord().get(k + 1).getX()
          - ls.coord().get(k).getX());
    }

    // table des ecarts d'orientation au niveau de chaque point de la ligne
    // initiale (en radian entre -Pi et Pi)
    double[] deviations = new double[ls.coord().size() - 1];
    deviations[0] = direction[0];
    for (int k = 1; k < ls.coord().size() - 1; k++) {

      // calcul de la deviation
      double dev = direction[k] - direction[k - 1];

      // dev doit etre entre -Pi et Pi
      if (dev < -Math.PI) {
        dev += 2 * Math.PI;
      }
      if (dev > Math.PI) {
        dev -= 2 * Math.PI;
      }

      deviations[k] = dev;
    }

    // tableau des coordonnees de la ligne resultat
    int nbPoints = (int) ls.length();
    Coordinate[] coordsRes = new Coordinate[nbPoints];

    // remplissage du tableau des coordonnees de la ligne resultat
    coordsRes[0] = new Coordinate(ls.coord().get(0).getX(), ls.coord().get(0)
        .getY());
    double angle = 0.0;
    for (int i = 1; i < nbPoints; i++) {

      // recupere la deviation lissee au point i
      double dAngle = LineCurvatureSmoothing.deviationLissee(absCurv,
          deviations, i - 1, symbolWidth);
      angle += dAngle;
      if (LineCurvatureSmoothing.logger.isInfoEnabled()) {
        LineCurvatureSmoothing.logger.info("angle=" + angle * 180 / Math.PI);
      }

      // construit nouveau point
      coordsRes[i] = new Coordinate(coordsRes[i - 1].x + Math.cos(angle),
          coordsRes[i - 1].y + Math.sin(angle));
    }

    // filtrage
    DouglasPeuckerSimplifier dp = new DouglasPeuckerSimplifier(
        new GeometryFactory().createLineString(coordsRes));
    dp.setDistanceTolerance(resolution);
    LineString lsRes = new GeometryFactory().createLineString(dp
        .getResultGeometry().getCoordinates());

    // rotation (en principe, tres faible)
    double angle1 = Math.atan2(ls.coord().get(ls.coord().size() - 1).getY()
        - ls.coord().get(0).getY(), ls.coord().get(ls.coord().size() - 1)
        .getX()
        - ls.coord().get(0).getX());
    coordsRes = lsRes.getCoordinates();
    double angle2 = Math.atan2(coordsRes[coordsRes.length - 1].y
        - coordsRes[0].y, coordsRes[coordsRes.length - 1].x - coordsRes[0].x);
    angle = angle1 - angle2;
    if (angle < -Math.PI) {
      angle += 2 * Math.PI;
    }
    if (angle > Math.PI) {
      angle -= 2 * Math.PI;
    }

    lsRes = CommonAlgorithms.rotation(lsRes, new Coordinate(ls.coord().get(0)
        .getX(), ls.coord().get(0).getY()), angle);

    // affinite
    coordsRes = lsRes.getCoordinates();
    double coef = ls.coord().get(ls.coord().size() - 1)
        .distance(ls.coord().get(0))
        / coordsRes[coordsRes.length - 1].distance(coordsRes[0]);
    Coordinate[] coordLineJTS = CommonAlgorithms.affinite(lsRes,
        new Coordinate(ls.coord().get(0).getX(), ls.coord().get(0).getY()),
        angle1, coef).getCoordinates();

    // Conversion en GeOxygene
    IDirectPositionList geoxCoords = new DirectPositionList();
    for (Coordinate coord : coordLineJTS) {
      geoxCoords.add(new DirectPosition(coord.x, coord.y));
    }
    return new GM_LineString(geoxCoords);

  }

  /**
   * returns the angle of deflection smooth point of curvilinear abscissa i (in
   * radian between -Pi et Pi)
   * @param absCurv
   * @param deviations
   * @param i
   * @param symbolWidth
   * @return
   */

  private static double deviationLissee(double[] absCurv, double[] deviations,
      int i, double symbolWidth) {

    // si c'est le premier point
    if (i == 0) {
      return deviations[0];
    }

    double dl = 0.5 * symbolWidth * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;

    // LA formule basee sur le filtrage de courbure et l'utilisation des
    // distributions
    double angle = 0.0;
    double c = dl * dl * Math.PI * 2;
    for (int k = 1; k < deviations.length; k++) {
      angle += deviations[k] * Math.exp(-Math.pow(i - absCurv[k], 2) / c);
    }
    return angle / (dl * Math.PI * 1.4142);
  }

}
