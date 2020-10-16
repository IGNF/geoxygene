package fr.ign.cogit.geoxygene.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.util.LinearComponentExtracter;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import org.locationtech.jts.precision.GeometryPrecisionReducer;

public class FeaturePolygonizer {
  private static GeometryFactory fact = new GeometryFactory();
  public static Boolean DEBUG = false;

  private static List<Geometry> getLines(List<Geometry> inputFeatures) {
    List<Geometry> linesList = new ArrayList<Geometry>();
    LinearComponentExtracter lineFilter = new LinearComponentExtracter(linesList);
    for (Geometry feature : inputFeatures) feature.apply(lineFilter);
    return linesList;
  }

  private static Point extractPoint(List<Geometry> lines) {
    Point point = null;
    // extract first point from first non-empty geometry
    for (Geometry geometry : lines) {
      if (!geometry.isEmpty()) {
        Coordinate p = geometry.getCoordinate();
        point = geometry.getFactory().createPoint(p);
        break;
      }
    }
    return point;
  }

  private static Geometry nodeLines(List<Geometry> lines) {
    MultiLineString linesGeom = fact.createMultiLineString(lines.toArray(new LineString[lines.size()]));
    Geometry unionInput = fact.createMultiLineString(null);
    Point point = extractPoint(lines);
    if (point != null) unionInput = point;
    return linesGeom.union(unionInput);
  }

  private static void addFeatures(Polygonizer p, List<Geometry> inputFeatures) {
    if (DEBUG) System.out.println(Calendar.getInstance().getTime() + " node lines");
    List<Geometry> reduced = new ArrayList<Geometry>();
    for (Geometry g : inputFeatures) reduced.add(GeometryPrecisionReducer.reduce(g, new PrecisionModel(100)));
    // extract linear components from input geometries
    List<Geometry> lines = getLines(reduced);
    // node all geometries together
    Geometry nodedLines = nodeLines(lines);
    if (nodedLines instanceof MultiLineString) {
      // noding a second time to be sure
      MultiLineString mls = (MultiLineString) nodedLines;
      List<Geometry> geoms = new ArrayList<>(mls.getNumGeometries());
      for (int i = 0; i < mls.getNumGeometries(); i++)
        geoms.add(mls.getGeometryN(i));
      nodedLines = nodeLines(geoms);
    }
    if (DEBUG) System.out.println(Calendar.getInstance().getTime() + " insert lines");
    p.add(nodedLines);
  }

  @SuppressWarnings("unchecked")
  public static List<Polygon> getPolygons(List<Geometry> features) {
    Polygonizer polygonizer = new Polygonizer();
    addFeatures(polygonizer, features);
    if (DEBUG) System.out.println(Calendar.getInstance().getTime() + " now with the real stuff");
    List<Polygon> result = new ArrayList<>();
    result.addAll(polygonizer.getPolygons());
    if (DEBUG) System.out.println(Calendar.getInstance().getTime() + " all done now");
    // for (Polygon p : result)
    // System.out.println(p);
    // System.out.println(Calendar.getInstance().getTime() + " all done now");
    return result;
  }
  public static Geometry getIntersection(List<Geometry> features) {
    List<Polygon> polygons = getPolygons(features);
    List<Polygon> buffer = new ArrayList<>();
    for (Polygon p : polygons) {
      Point point = p.getInteriorPoint();
      if (features.stream().allMatch(g->g.intersects(point))) {
        buffer.add(p);
      }
    }
    return fact.createGeometryCollection(buffer.toArray(new Geometry[buffer.size()])).union();
  }
  @SuppressWarnings("unchecked")
  public static Geometry getDifference(List<Geometry> features, List<Geometry> featuresToRemove) {
    Polygonizer polygonizer = new Polygonizer();
    List<Geometry> allFeatures = new ArrayList<>(features);
    allFeatures.addAll(featuresToRemove);
    addFeatures(polygonizer, allFeatures);
//    addFeatures(polygonizer, features);
//    addFeatures(polygonizer, featuresToRemove);
    List<Polygon> polygons = new ArrayList<>();
    polygons.addAll(polygonizer.getPolygons());
    List<Polygon> buffer = new ArrayList<>();
    for (Polygon p : polygons) {
      Point point = p.getInteriorPoint();
      if (features.stream().anyMatch(g->g.intersects(point)) && featuresToRemove.stream().allMatch(g->!g.intersects(point))) {
        buffer.add(p);
      }
    }
    return fact.createGeometryCollection(buffer.toArray(new Geometry[buffer.size()])).union();
  }
  @SuppressWarnings("unchecked")
  public static Geometry[] getIntersectionDifference(List<Geometry> features, List<Geometry> featuresToRemove) {
    Polygonizer polygonizer = new Polygonizer();
    List<Geometry> allFeatures = new ArrayList<>(features);
    allFeatures.addAll(featuresToRemove);
    addFeatures(polygonizer, allFeatures);
//    addFeatures(polygonizer, features);
//    addFeatures(polygonizer, featuresToRemove);
    List<Polygon> polygons = new ArrayList<>();
    polygons.addAll(polygonizer.getPolygons());
    List<Polygon> intersectionBuffer = new ArrayList<>();
    List<Polygon> differenceBuffer = new ArrayList<>();
    for (Polygon p : polygons) {
      Point point = p.getInteriorPoint();
      if (features.stream().anyMatch(g->g.intersects(point))) {
        if (featuresToRemove.stream().anyMatch(g->g.intersects(point))) {
          intersectionBuffer.add(p);
        } else { //if (featuresToRemove.stream().noneMatch(g->g.intersects(point))) {
          differenceBuffer.add(p);
        }
      }
    }
    Geometry[] reducedIntersection = intersectionBuffer.stream().map(g->GeometryPrecisionReducer.reduce(g, new PrecisionModel(100))).toArray(Geometry[]::new);
    Geometry[] reducedDifference = differenceBuffer.stream().map(g->GeometryPrecisionReducer.reduce(g, new PrecisionModel(100))).toArray(Geometry[]::new);
    Geometry intersection = fact.createGeometryCollection(reducedIntersection).union();
    Geometry difference = fact.createGeometryCollection(reducedDifference).union();
    return new Geometry[] {intersection, difference};
  }
  public static Geometry getUnion(List<Geometry> features) {
    List<Polygon> polygons = getPolygons(features);
    List<Polygon> buffer = new ArrayList<>();
    for (Polygon p : polygons) {
      Point point = p.getInteriorPoint();
      if (features.stream().anyMatch(g->g.intersects(point))) {
        buffer.add(p);
      }
    }
    return fact.createGeometryCollection(buffer.toArray(new Geometry[buffer.size()])).union();
  }
}
