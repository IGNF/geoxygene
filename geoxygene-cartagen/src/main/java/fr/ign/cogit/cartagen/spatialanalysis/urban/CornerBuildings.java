/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.urban;

import java.util.HashSet;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Batiment;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;
import fr.ign.cogit.geoxygene.schemageo.api.support.elementsIndependants.ElementIndependant;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;

/**
 * Implémentation de l'algorithme de P. Taillandier pour détecter les bâtiments
 * dans le coin d'un îlot (Annexe 1, p.379). Implementation of P. Taillandier's
 * algorithm to detect corner buildings in a block (see Annex 1, p.379 of his
 * PhD).
 * @author GTouya
 * 
 */
public class CornerBuildings {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static Logger LOGGER = Logger.getLogger(CornerBuildings.class);

  // Private fields //
  private Ilot block;
  private HashSet<Batiment> cornerBuildings;
  /**
   * the tolerance to consider that an angle between roads is right. In degrees.
   * P. Taillandier uses 20° as a default value.
   */
  private double angleTolerance = 25.0;
  /**
   * The length of the edges of the triangle built to represent the corners of
   * the block. P. Taillandier uses 20 m as a default value. But a scale
   * dependant parameter seems more appropriate to different scales so we
   * propose 0.9 map mm
   */
  private double triangleEdge = 0.9;

  private double scale = Legend.getSYMBOLISATI0N_SCALE();

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public CornerBuildings(Ilot block) {
    super();
    this.block = block;
    this.cornerBuildings = new HashSet<Batiment>();
  }

  // Getters and setters //
  public Ilot getBlock() {
    return this.block;
  }

  public void setBlock(Ilot block) {
    this.block = block;
  }

  public HashSet<Batiment> getCornerBuildings() {
    return this.cornerBuildings;
  }

  public void setCornerBuildings(HashSet<Batiment> cornerBuildings) {
    this.cornerBuildings = cornerBuildings;
  }

  // Other public methods //
  public void compute() {
    // first cut the roads at right angles
    HashSet<ILineString> roadParts = this.cutRoads();
    double tol = this.angleTolerance * Math.PI / 180.0;
    // build the corner areas
    HashSet<IPolygon> cornerAreas = new HashSet<IPolygon>();
    HashSet<ILineString> treatedRoads = new HashSet<ILineString>();
    for (ILineString road : roadParts) {
      treatedRoads.add(road);
      // get the connected roads
      for (ILineString road2 : this.getConnectedRoads(road, roadParts)) {
        if (treatedRoads.contains(road2)) {
          continue;
        }
        double angle = CommonAlgorithmsFromCartAGen.angleBetween2Lines(road,
            road2);
        if (angle > Math.PI / 2.0 - tol && angle < Math.PI / 2.0 + tol) {
          // build a triangle as a new corner area
          // first get the triangle three points
          IDirectPosition a = CommonAlgorithmsFromCartAGen
              .getCommonVertexBetween2Lines(road, road2);
          // get second point on road1
          double distAB = this.triangleEdge * this.scale / 1000.0
              * Math.min(Math.PI / 2.0, angle) / Math.max(Math.PI / 2.0, angle);
          IDirectPositionList liste1 = road.coord();
          if (!liste1.get(0).equals2D(a)) {
            liste1.inverseOrdre();
          }
          IDirectPosition b = Operateurs
              .pointEnAbscisseCurviligne(new GM_LineString(liste1), distAB);
          // if distance is bigger than line length, get line last vertex
          if (b == null) {
            b = liste1.get(liste1.size() - 1);
          }
          // get second point on road2
          double distAC = this.triangleEdge * this.scale / 1000.0
              * Math.min(Math.PI / 2.0, angle) / Math.max(Math.PI / 2.0, angle);
          IDirectPositionList liste2 = road2.coord();
          if (!liste2.get(0).equals2D(a)) {
            liste2.inverseOrdre();
          }
          IDirectPosition c = Operateurs
              .pointEnAbscisseCurviligne(new GM_LineString(liste2), distAC);
          // if distance is bigger than line length, get line last vertex
          if (c == null) {
            c = liste2.get(liste2.size() - 1);
          }
          // build the triangle
          cornerAreas.add(GeometryFactory.buildTriangle(a, b, c));
        }
      }
    }

    // get the corner buildings from the triangles
    IFeatureCollection<Batiment> fc = new FT_FeatureCollection<Batiment>();
    for (ElementIndependant component : this.block.getComposants()) {
      fc.add((Batiment) component);
    }
    for (IPolygon triangle : cornerAreas) {
      this.cornerBuildings.addAll(fc.select(triangle));
    }
  }

  /**
   * Retrieve the triangles used to find the corner buildings for display
   * purposes.
   * 
   * @return
   * @author GTouya
   */
  public HashSet<IPolygon> getTriangles() {
    // first cut the roads at right angles
    HashSet<ILineString> roadParts = this.cutRoads();
    double tol = this.angleTolerance * Math.PI / 180.0;
    // build the corner areas
    HashSet<IPolygon> cornerAreas = new HashSet<IPolygon>();
    HashSet<ILineString> treatedRoads = new HashSet<ILineString>();
    for (ILineString road : roadParts) {
      treatedRoads.add(road);
      // get the connected roads
      for (ILineString road2 : this.getConnectedRoads(road, roadParts)) {
        if (treatedRoads.contains(road2)) {
          continue;
        }
        double angle = CommonAlgorithmsFromCartAGen.angleBetween2Lines(road,
            road2);
        // System.out.println("angle: " + angle);
        if (angle > Math.PI / 2.0 - tol && angle < Math.PI / 2.0 + tol) {
          // build a triangle as a new corner area
          // first get the triangle three points
          IDirectPosition a = CommonAlgorithmsFromCartAGen
              .getCommonVertexBetween2Lines(road, road2);
          // get second point on road1
          double distAB = this.triangleEdge * this.scale / 1000.0
              * Math.min(Math.PI / 2.0, angle) / Math.max(Math.PI / 2.0, angle);
          IDirectPositionList liste1 = road.coord();
          if (!liste1.get(0).equals2D(a)) {
            liste1.inverseOrdre();
          }
          IDirectPosition b = Operateurs
              .pointEnAbscisseCurviligne(new GM_LineString(liste1), distAB);
          // if distance is bigger than line length, get line last vertex
          if (b == null) {
            b = liste1.get(liste1.size() - 1);
          }
          // get second point on road2
          double distAC = this.triangleEdge * this.scale / 1000.0
              * Math.min(Math.PI / 2.0, angle) / Math.max(Math.PI / 2.0, angle);
          IDirectPositionList liste2 = road2.coord();
          if (!liste2.get(0).equals2D(a)) {
            liste2.inverseOrdre();
          }
          IDirectPosition c = Operateurs
              .pointEnAbscisseCurviligne(new GM_LineString(liste2), distAC);
          // if distance is bigger than line length, get line last vertex
          if (c == null) {
            c = liste2.get(liste2.size() - 1);
          }
          // build the triangle
          cornerAreas.add(GeometryFactory.buildTriangle(a, b, c));
        }
      }
    }

    return cornerAreas;
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////
  private HashSet<ILineString> cutRoad(ILineString road) {
    // initialisation
    HashSet<ILineString> roadParts = new HashSet<ILineString>();
    int nbPtsIter = 0;
    double cumulatedAngle = 0.0;
    IDirectPosition prevPt = null, prevPrevPt = null,
        initialPt = road.coord().get(0);
    // simplify the line to get sharp angles
    ILineString lineSimp = (ILineString) CommonAlgorithms
        .filtreDouglasPeucker(road, 1.0);
    ILineString line = Operateurs.echantillone(lineSimp, 10.0);
    for (IDirectPosition pt : line.coord()) {
      if (nbPtsIter > 3) {
        // add the angle between [0,Pi/2] to the cumulated angle
        double angle = Angle.angleTroisPoints(prevPrevPt, prevPt, pt)
            .angleAPiPres().getValeur();
        if (angle > Math.PI / 2.0) {
          angle = Math.abs(angle - Math.PI);
        }
        cumulatedAngle += angle;
        if (cumulatedAngle > Math.PI / 2.0
            - this.angleTolerance * Math.PI / 180.0) {
          // cut the road at prevPt
          roadParts.add(GeometryFactory.buildSubLine(line, initialPt, prevPt));
          initialPt = prevPt;
          cumulatedAngle = 0.0;
          nbPtsIter = 0;
        }
      }
      nbPtsIter++;
      prevPrevPt = prevPt;
      prevPt = pt;
    }
    ILineString finalLine = GeometryFactory.buildSubLine(line, initialPt,
        road.endPoint());
    // special case with a rounded block
    if (initialPt.equals2D(road.endPoint())) {
      finalLine = road;
    }
    roadParts.add(finalLine);
    return roadParts;
  }

  public HashSet<ILineString> cutRoads() {
    HashSet<ILineString> roadParts = new HashSet<ILineString>();
    if (LOGGER.isTraceEnabled())
      LOGGER.trace(this.block.getArcsReseaux().size()
          + " roads to cut around the block");
    for (ArcReseau road : this.block.getArcsReseaux()) {
      roadParts.addAll(this.cutRoad((ILineString) road.getGeom()));
    }
    return roadParts;
  }

  private HashSet<ILineString> getConnectedRoads(ILineString road,
      HashSet<ILineString> roads) {
    HashSet<ILineString> connectedRoads = new HashSet<ILineString>();
    for (ILineString r : roads) {
      if (r.equals(road)) {
        continue;
      }
      if (r.touches(road)) {
        connectedRoads.add(r);
      }
    }
    return connectedRoads;
  }

}
