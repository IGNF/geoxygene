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
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;

/**
 * Computation of a the coalescence algorithm on a linear SectionAgent THIS ALGO
 * SHOULD NOT BE USED ANYMORE => USE THE GAUSSIAN FILTER OF GEOXYGENE INSTEAD
 * @author jGaffuri 09/07/2007 (updated J. Renard 22/04/2010)
 * 
 */

public class LineGaussianSmoothing {
  private static Logger logger = Logger.getLogger(LineGaussianSmoothing.class
      .getName());

  /**
   * Linear section on which gaussian smoothing is performed
   */
  private INetworkSection geneObj;

  /**
   * Sigma
   */
  private double sigma = 0.0;

  /**
   * Data resolution: defined in the generalisation parameters
   */
  private double resolution = 0.0;

  /**
   * Global constructor
   * @param geoObj
   */

  public LineGaussianSmoothing(INetworkSection geneObj, double sigma) {
    this.geneObj = geneObj;
    this.sigma = sigma;
    this.resolution = GeneralisationSpecifications.getRESOLUTION();
  }

  /**
   * Default constructor with empiric parameters
   * @param geoObj
   */

  public LineGaussianSmoothing(INetworkSection geneObj) {
    this.geneObj = geneObj;
    double symbolWidth = SectionSymbol.getUsedSymbolWidth(geneObj) / 2;
    this.sigma = 150 * symbolWidth;
    if (symbolWidth > 0.4) {
      this.sigma = 215 * symbolWidth;
    }
    this.resolution = GeneralisationSpecifications.getRESOLUTION();
  }

  /**
   * Computation of the gaussian smoothing on geoObj
   * @param isPersistantObject : determines if the geographic object is
   *          persistent in the database or just exists to apply the algorithm
   */

  public void compute(boolean isPersistantObject) {

    // Geometry line
    IGeometry geomStart = this.geneObj.getGeom();
    if (!(geomStart instanceof ILineString)) {
      if (LineGaussianSmoothing.logger.isTraceEnabled()) {
        LineGaussianSmoothing.logger
            .trace("Impossible to perform Gaussian smoothing - non linear geometry");
      }
      return;
    }
    if (geomStart.coord().size() < 3) {
      if (LineGaussianSmoothing.logger.isTraceEnabled()) {
        LineGaussianSmoothing.logger
            .trace("Impossible to perform Gaussian smoothing - not enough points on geometry");
      }
      return;
    }
    ILineString geoxLine = (ILineString) geomStart;
    if (geoxLine.isClosed()) {
      return;
    }

    // Algorithm computation
    ILineString geomResult = LineGaussianSmoothing.lineGaussianSmoothing(
        geoxLine, this.sigma, this.resolution);

    // Affectation to the object
    if (geomResult != null) {
      this.geneObj.setGeom(geomResult);
      if (isPersistantObject) {
        this.geneObj.registerDisplacement();
      }
      if (LineGaussianSmoothing.logger.isTraceEnabled()) {
        LineGaussianSmoothing.logger.trace("Gaussian smoothing performed on "
            + this.geneObj);
      }
    }

  }

  /**
   * Static computation of the curvature smoothing on a simple LineString
   * @param ls : the line to be treated
   * @param sigma : sigma
   * @param resolution : data resolution
   * @return the smoothed simple line
   */

  public static ILineString lineGaussianSmoothing(ILineString ls, double sigma,
      double resolution) {

    // message au cas ou la ligne est fermee
    if (ls.isClosed()) {
      LineGaussianSmoothing.logger
          .warn("lissage gaussien ne traite pas les lignes fermees.");
      return null;
    }

    if (ls.coord().size() <= 2) {
      return null;
    }

    // tableau de coordonnees de la ligne densifiee
    IDirectPositionList coordsDens = LineDensification.densification(ls, 1.0)
        .coord();

    // calcul des coefficients de gauss necessaires
    int nb = 7 * (int) sigma;
    double gauss[] = new double[nb + 1];
    double c1 = sigma * Math.sqrt(2 * Math.PI);
    double c2 = 2 * sigma * sigma;
    for (int i = 0; i < nb + 1; i++) {
      gauss[i] = Math.exp(-i * i / c2) / c1;
    }

    // tableau des coordonnees de la ligne lissee
    int nbPoints = LineDensification.densification(ls, 1.0).coord().size();
    Coordinate[] coordsRes = new Coordinate[nbPoints + 1];

    // remplissage
    double x, y, dx, dy;
    IDirectPosition c;
    int q;
    IDirectPosition c0 = coordsDens.get(0);
    IDirectPosition cN = coordsDens.get(nbPoints - 1);
    for (int i = 0; i < nbPoints; i++) {

      // calcul des coordonnees du point i de la ligne lissee (moyenne de gauss)
      x = 0.0;
      y = 0.0;
      for (int j = -nb; j <= nb; j++) {
        q = i + j;
        if (-q > nbPoints - 1 || q > 2 * nbPoints - 1) {
          continue;
        } else if (q < 0) {
          c = coordsDens.get(-q);
          // prendre le symetrique par rapport au point initial
          dx = 2 * c0.getX() - c.getX();
          dy = 2 * c0.getY() - c.getY();
        } else if (q > nbPoints - 1) {
          c = coordsDens.get(2 * nbPoints - q - 1);
          // prendre le symetrique par rapport au point final
          dx = 2 * cN.getX() - c.getX();
          dy = 2 * cN.getY() - c.getY();
        } else {
          c = coordsDens.get(q);
          dx = c.getX();
          dy = c.getY();
        }

        x += gauss[j >= 0 ? j : -j] * dx;
        y += gauss[j >= 0 ? j : -j] * dy;
      }
      coordsRes[i] = new Coordinate(x, y);
    }

    // les premier et dernier point
    IDirectPosition lastCoordsDens = coordsDens.get(coordsDens.size() - 1);
    coordsRes[nbPoints] = new Coordinate(lastCoordsDens.getX(), lastCoordsDens
        .getY());

    // filtrage
    DouglasPeuckerSimplifier dp = new DouglasPeuckerSimplifier(
        new GeometryFactory().createLineString(coordsRes));
    dp.setDistanceTolerance(resolution);

    // Conversion géométrie JTS -> géométrie GeOxygene
    Coordinate[] coordsDP = dp.getResultGeometry().getCoordinates();
    IDirectPositionList coordsDP_GM = new DirectPositionList();
    for (Coordinate coordDP : coordsDP) {
      coordsDP_GM.add(new DirectPosition(coordDP.x, coordDP.y));
    }

    return new GM_LineString(coordsDP_GM);

  }

}
