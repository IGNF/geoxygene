/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.measures;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

/**
 * 
 * Computation of a the coalescence algorithm on a linear feature, based on
 * Sébastien Mustière's work for GALBE.
 * 
 * 
 * @author AMaudet
 * @author GTouya
 * 
 */
public class LineCoalescence {

  private static Logger logger = Logger.getLogger(LineCoalescence.class
      .getName());

  // Fields for different kinds of coalescence
  public static final int NONE = 0;
  public static final int LEFT = 1;
  public static final int RIGHT = 2;
  public static final int BOTH = 3;
  public static final int HETEROG = 4;

  /**
   * Linear section on which colascence is detected
   */
  private INetworkSection geneObj;
  /**
   * The geometry of the linear feature being measured.
   */
  private ILineString line;

  /**
   * Symbol width: half of the internal part of the line + half of the line
   * border
   */
  private double symbolWidth = 0.0;

  /**
   * Legibility fact: Gothic parameter usually fixed to 1.7
   */
  private double legibilityFact = 0.0;

  /**
   * List of sections separated because of their different coalescence types
   */
  private ArrayList<ILineString> sections = null;

  public ArrayList<ILineString> getSections() {
    return this.sections;
  }

  private boolean debug = false;

  private ArrayList<ILineString> vectors = new ArrayList<ILineString>();

  public ArrayList<ILineString> getVectors() {
    return vectors;
  }

  private ArrayList<Boolean> vectorsBoolean = new ArrayList<Boolean>();

  public ArrayList<Boolean> getVectorsBoolean() {
    return vectorsBoolean;
  }

  /**
   * List of coalescence types for separated sections
   */
  private ArrayList<Integer> coalescenceTypes = null;

  /**
   * @return
   */
  public ArrayList<Integer> getCoalescenceTypes() {
    return this.coalescenceTypes;
  }

  /**
   * Number of separated sections
   */
  private int nbSections = 0;

  public int getNbSections() {
    return this.nbSections;
  }

  public LineCoalescence(INetworkSection geneObj) {
    this(geneObj, false, Legend.getSYMBOLISATI0N_SCALE());
  }

  public LineCoalescence(INetworkSection geneObj, double scale) {
    this(geneObj, false, scale);
  }

  public LineCoalescence(ILineString line, double scale, double symbolWidth) {
    this.geneObj = null;
    this.symbolWidth = symbolWidth * scale / 2000;
    if (LineCoalescence.logger.isTraceEnabled()) {

      LineCoalescence.logger.trace("SymbolWidth: " + symbolWidth);
    }
    this.legibilityFact = 1.7;
    this.debug = false;
    this.line = line;
  }

  /**
   * Default constructor
   * @param geoObj
   */

  public LineCoalescence(INetworkSection geneObj, boolean debug, double scale) {
    this.geneObj = geneObj;
    this.symbolWidth = geneObj.getWidth() * scale / 2000;
    if (LineCoalescence.logger.isTraceEnabled()) {

      LineCoalescence.logger.trace("SymbolWidth: " + symbolWidth);
    }
    this.legibilityFact = 1.7;
    this.debug = debug;
    if (debug) {
      this.vectors = new ArrayList<ILineString>();
      this.vectorsBoolean = new ArrayList<Boolean>();
    }

  }

  public void compute() {
    // Geometry conversion
    IGeometry geomStart = line;
    if (line == null)
      geomStart = this.geneObj.getGeom();
    if (!(geomStart instanceof ILineString)) {
      if (logger.isTraceEnabled()) {
        logger
            .trace("Immpossible to perform Coalescence detection - non linear geometry");
      }
      return;
    }
    if (geomStart.coord().size() < 3) {
      if (logger.isTraceEnabled()) {
        logger
            .trace("Impossible to perform Coalescence detection - not enough points on geometry");
      }
      return;
    }

    ILineString geom = (ILineString) geomStart;

    // get offset from each side of the line
    IMultiCurve<ILineString> offset1 = JtsAlgorithms.offsetCurve(geom,
        this.symbolWidth);
    IMultiCurve<ILineString> offset2 = JtsAlgorithms.offsetCurve(geom,
        -this.symbolWidth);

    double minDistance = this.symbolWidth * this.legibilityFact;

    this.sections = new ArrayList<ILineString>();
    this.coalescenceTypes = new ArrayList<Integer>();

    // For each point of the line
    List<IDirectPosition> positionsForCurrentLine = new ArrayList<IDirectPosition>();
    int coalescenceType = NONE;
    for (IDirectPosition directPosition : geom.coord()) {
      boolean coa = false;
      IPoint point = new GM_Point(directPosition);

      if (logger.isTraceEnabled()) {
        logger.trace("Point position: " + directPosition);
      }
      IDirectPosition nearestPoint1 = null;
      if (!(offset1.isEmpty())) {
        nearestPoint1 = CommonAlgorithms.getNearestPoint(offset1, point);
      }
      IDirectPosition nearestPoint2 = null;
      if (!(offset2.isEmpty())) {
        nearestPoint2 = CommonAlgorithms.getNearestPoint(offset2, point);
      }

      if (debug) {
        if (!(offset1.isEmpty())) {
          List<IDirectPosition> vector1 = new ArrayList<IDirectPosition>();
          vector1.add(nearestPoint1);
          vector1.add(directPosition);
          vectors.add(new GM_LineString(vector1));
        }
        if (!(offset2.isEmpty())) {
          List<IDirectPosition> vector2 = new ArrayList<IDirectPosition>();
          vector2.add(nearestPoint2);
          vector2.add(directPosition);
          vectors.add(new GM_LineString(vector2));
        }

      }

      if (logger.isTraceEnabled()) {

        logger.trace("nearestPoint1: " + nearestPoint1);

        logger.trace("nearestPoint2: " + nearestPoint2);
        if (!(offset1.isEmpty())) {
          logger.trace("Distance at left: "
              + nearestPoint1.distance(directPosition));
        }
        if (!(offset2.isEmpty())) {
          logger.trace("Distance at right: "
              + nearestPoint2.distance(directPosition));
        }
      }

      if (offset1.isEmpty()
          || nearestPoint1.distance(directPosition) >= minDistance) {
        if (debug && !offset1.isEmpty())
          vectorsBoolean.add(true);
        coa = true;
        if (coalescenceType == NONE) {
          addNewLineString(positionsForCurrentLine, directPosition,
              minDistance, coalescenceType);

          positionsForCurrentLine = new ArrayList<IDirectPosition>();
          coalescenceType = LEFT;
        } else if (coalescenceType == RIGHT) {
          coalescenceType = BOTH;
        }
      } else {
        if (debug && !offset1.isEmpty())
          vectorsBoolean.add(false);
      }
      if (offset2.isEmpty()
          || nearestPoint2.distance(directPosition) >= minDistance) {
        if (debug && !offset2.isEmpty())
          vectorsBoolean.add(true);
        coa = true;
        if (coalescenceType == NONE) {
          addNewLineString(positionsForCurrentLine, directPosition,
              minDistance, coalescenceType);

          positionsForCurrentLine = new ArrayList<IDirectPosition>();
          coalescenceType = RIGHT;
        } else if (coalescenceType == LEFT) {
          coalescenceType = BOTH;
        }
      } else {
        if (debug && !offset2.isEmpty())
          vectorsBoolean.add(false);
      }
      if (!coa) {
        if (coalescenceType != NONE) {
          addNewLineString(positionsForCurrentLine, directPosition,
              minDistance, coalescenceType);

          positionsForCurrentLine = new ArrayList<IDirectPosition>();
          coalescenceType = NONE;
        }
      }
      positionsForCurrentLine.add(directPosition);
    }
    this.sections.add(new GM_LineString(positionsForCurrentLine));
    this.coalescenceTypes.add(coalescenceType);

    this.nbSections = this.sections.size();
    this.fusionLineString(minDistance);
  }

  /**
   * Merges coalesced connected geometries
   * @param distanceMin
   */
  private void fusionLineString(double distanceMin) {
    if (this.getSections().size() < 3)
      return;

    ILineString previousPreviousLine = this.getSections().get(0);
    int previousPreviousType = this.getCoalescenceTypes().get(0);

    ILineString previousLine = this.getSections().get(1);
    int previousType = this.getCoalescenceTypes().get(1);

    for (int i = 2; i < this.getNbSections(); i++) {
      ILineString line = this.getSections().get(i);
      int type = this.getCoalescenceTypes().get(i);

      if (previousPreviousType != NONE
          && type != NONE
          && previousPreviousLine.endPoint().distance(line.startPoint()) < distanceMin) {
        previousPreviousLine.removeControlPoint(previousPreviousLine
            .sizeControlPoint() - 1);
        for (IDirectPosition value : previousLine.coord()) {
          previousPreviousLine.addControlPoint(value);
        }
        previousPreviousLine.removeControlPoint(previousPreviousLine
            .sizeControlPoint() - 1);
        for (IDirectPosition value : line.coord()) {
          previousPreviousLine.addControlPoint(value);
        }

        this.getSections().remove(this.getSections().size() - 1);
        this.getSections().remove(this.getSections().size() - 1);
        this.getCoalescenceTypes()
            .remove(this.getCoalescenceTypes().size() - 1);
        this.getCoalescenceTypes()
            .remove(this.getCoalescenceTypes().size() - 1);
        if (previousPreviousType != type || previousType != NONE
            && (previousType != previousPreviousType || previousType != type)) {
          this.getCoalescenceTypes().remove(
              this.getCoalescenceTypes().size() - 1);
          this.getCoalescenceTypes().add(BOTH);
          previousPreviousType = BOTH;
        }
        this.nbSections -= 2;

        i++;
        if (i >= this.getNbSections())
          break;
        previousLine = this.getSections().get(i);
        previousType = this.getCoalescenceTypes().get(i);

      } else {
        previousPreviousLine = previousLine;
        previousLine = line;
        previousPreviousType = previousType;
        previousType = type;

      }
    }
  }

  private void addNewLineString(List<IDirectPosition> positionsForCurrentLine,
      IDirectPosition directPosition, double minDistance, int coalescenceType) {
    if (!positionsForCurrentLine.isEmpty()) {
      positionsForCurrentLine.add(directPosition);
      ILineString newLineString = new GM_LineString(positionsForCurrentLine);

      this.sections.add(newLineString);
      this.coalescenceTypes.add(coalescenceType);
    }
  }
}
