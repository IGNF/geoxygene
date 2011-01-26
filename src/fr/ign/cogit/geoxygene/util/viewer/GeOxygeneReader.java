/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.util.viewer;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.DataSource;
import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.GeoLine;
import uk.ac.leeds.ccg.geotools.GeoPoint;
import uk.ac.leeds.ccg.geotools.GeoPolygon;
import uk.ac.leeds.ccg.geotools.GeoShape;
import uk.ac.leeds.ccg.geotools.HSVShader;
import uk.ac.leeds.ccg.geotools.Layer;
import uk.ac.leeds.ccg.geotools.LineLayer;
import uk.ac.leeds.ccg.geotools.PointLayer;
import uk.ac.leeds.ccg.geotools.PolygonLayer;
import uk.ac.leeds.ccg.geotools.Shader;
import uk.ac.leeds.ccg.geotools.ShapeLayer;
import uk.ac.leeds.ccg.geotools.SimpleGeoData;
import uk.ac.leeds.ccg.geotools.Theme;
import uk.ac.leeds.ccg.geotools.UniqueShader;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.browser.ObjectBrowser;

/**
 * A class to simplify the process of loading a FT_FeatureCollection from
 * GeOxygene. Collections MUST be homogeneous. Geometry types in the collection
 * MUST be the same for all features (but some null geometries are accepted).
 * All features in the collection must belong to same class. Works only for
 * collections of features whose geometry type is : GM_Point, GM_LineString,
 * GM_Polygon, GM_MultiPoint, GM_MultiCurve (with linestrings), GM_Multisurface
 * (with polygons) and GM_Aggregate with an homogeneous geometry type inside. ID
 * are fill with hashcode(). One GeOxygeneReader is created by Theme stemming
 * from GeOxygene data.
 * 
 * @author Thierry Badard & Arnaud Braun & Eric Grosso
 * @version 1.1
 * 
 * 
 */

class GeOxygeneReader implements DataSource {

  // Default
  public static boolean SHOW_PUBLIC_ATTRIBUTES = true;
  public static boolean SHOW_PROTECTED_ATTRIBUTES = true;

  // GeOxygene fields
  FT_FeatureCollection<? extends FT_Feature> coll;
  private Class<? extends FT_Feature> featureClass;

  // GEOTOOLS fields
  private Layer layer;
  Theme theme;

  @SuppressWarnings("unchecked")
  public GeOxygeneReader(FT_FeatureCollection<? extends FT_Feature> featColl) {
    this.coll = featColl;

    // guess feature class (type of the first feature ...)
    Iterator<? extends FT_Feature> iterator = this.coll.iterator();
    FT_Feature feature = iterator.next();
    this.featureClass = feature.getClass();

    // guess geometry type (type of the first geometry is the type for all
    // geometries ...)
    iterator = this.coll.iterator();
    GM_Object geom = null;
    while (iterator.hasNext()) {
      geom = iterator.next().getGeom();
      if (geom != null) {
        break;
      }
    }
    if (geom == null) {
      System.out.println("No geometry in "
          + this.coll.get(0).getClass().getName());
      return;
    }
    // init layer
    if (GM_Point.class.isAssignableFrom(geom.getClass())
        || GM_MultiPoint.class.isAssignableFrom(geom.getClass())) {
      this.layer = this.readPoints();
    } else if (GM_LineString.class.isAssignableFrom(geom.getClass())
        || GM_MultiCurve.class.isAssignableFrom(geom.getClass())) {
      this.layer = this.readLines();
    } else if (GM_Polygon.class.isAssignableFrom(geom.getClass())
        || GM_MultiSurface.class.isAssignableFrom(geom.getClass())) {
      this.layer = this.readPolygons();
    } else if (GM_Aggregate.class.isAssignableFrom(geom.getClass())) {
      Class<? extends GM_Object> classe = ((GM_Aggregate<? extends GM_Object>) geom)
          .get(0).getClass();
      if (GM_Point.class.isAssignableFrom(geom.getClass())
          || GM_MultiPoint.class.isAssignableFrom(classe)) {
        this.layer = this.readPoints();
      } else if (GM_LineString.class.isAssignableFrom(classe)
          || GM_MultiCurve.class.isAssignableFrom(classe)) {
        this.layer = this.readLines();
      } else if (GM_Polygon.class.isAssignableFrom(classe)
          || GM_MultiSurface.class.isAssignableFrom(classe)) {
        this.layer = this.readPolygons();
      }
    } else {
      System.out
          .println(" ## GeOxygeneReader works only for GM_Point, GM_LineString or GM_Polygon or MultiPrimitives or GM_Aggregate with the same geometry");
      System.out.println(" ## Have tried to read : "
          + geom.getClass().getName());
    }

    // init theme
    this.theme = new Theme(this.getLayer());
  }

  /** Layer for use in a Theme. */
  public Layer getLayer() {
    return this.layer;
  }

  /** Theme for use in a Viewer. */
  public Theme getTheme() {
    return this.theme;
  }

  /** Returns a Theme shaded by an attribute. */
  public Theme getTheme(Shader shader, String shadedBy) {
    if (shader instanceof HSVShader) {
      InitHSVShader hsvShader = new InitHSVShader(shader, shadedBy);
      hsvShader.start();
      try {
        hsvShader.join();
        this.theme.setShader(hsvShader.getShader());
      } catch (Exception e) {
        e.printStackTrace();
      }
      return this.theme;

    } else if (shader instanceof UniqueShader) {
      InitUniqueShader uShader = new InitUniqueShader(shader, shadedBy);
      uShader.start();
      try {
        uShader.join();
        this.theme.setShader(uShader.getShader());
      } catch (Exception e) {
        e.printStackTrace();
      }

      return this.theme;
    }

    return this.theme;
  }

  /** Refresh the feature. The feature must belong to the collection. */
  public void refreshFeature(FT_Feature feature) {
    ShapeLayer shLayer = (ShapeLayer) this.layer;
    GM_Object geom = feature.getGeom();
    if (shLayer.getGeoShape(feature.hashCode()) == null) { // new shape
      this.addGeoShape(feature, geom);
    } else {
      GeoShape shape = shLayer.getGeoShape(feature.hashCode());
      shLayer.removeGeoShape(shape);
      this.addGeoShape(feature, geom);
    }
  }

  /**
   * Reads the feature information from the FT_FeatureCollection and produces a
   * PointLayer for use in a Theme.
   */
  @SuppressWarnings("unchecked")
  private Layer readPoints() {
    PointLayer pointLayer = new PointLayer(false);
    Iterator<? extends FT_Feature> iterator = this.coll.iterator();
    while (iterator.hasNext()) {
      FT_Feature feat = iterator.next();
      GM_Object geom = feat.getGeom();
      if (geom != null) {
        if (GM_Point.class.isAssignableFrom(geom.getClass())) {
          pointLayer.addGeoPoint(this.geOxygenePointToGeoPoint(feat.hashCode(),
              geom));
        } else if (GM_MultiPoint.class.isAssignableFrom(geom.getClass())
            || GM_Aggregate.class.isAssignableFrom(geom.getClass())) {
          GM_Aggregate<GM_Object> aggr = (GM_Aggregate<GM_Object>) geom;
          for (GM_Object o : aggr) {
            pointLayer.addGeoPoint(this.geOxygenePointToGeoPoint(feat
                .hashCode(), o));
          }
        }
      }
    }
    return pointLayer;
  }

  /**
   * Reads the feature information from the FT_FeatureCollection and produces a
   * LineLayer for use in a Theme.
   * @return a LineLayer for use in a Theme
   */
  @SuppressWarnings("unchecked")
  private Layer readLines() {
    LineLayer lineLayer = new LineLayer();
    Iterator<? extends FT_Feature> iterator = this.coll.iterator();
    while (iterator.hasNext()) {
      FT_Feature feat = iterator.next();
      GM_Object geom = feat.getGeom();
      if (geom != null) {
        if (GM_LineString.class.isAssignableFrom(geom.getClass())) {
          lineLayer.addGeoLine(this.geOxygeneLineStringToGeoLine(feat
              .hashCode(), geom));
        } else if (GM_MultiCurve.class.isAssignableFrom(geom.getClass())
            || GM_Aggregate.class.isAssignableFrom(geom.getClass())) {
          GM_Aggregate<GM_Object> aggr = (GM_Aggregate<GM_Object>) geom;
          for (GM_Object o : aggr) {
            lineLayer.addGeoLine(this.geOxygeneLineStringToGeoLine(feat
                .hashCode(), o));
          }
        }
      }
    }
    return lineLayer;
  }

  /**
   * Reads the feature information from the FT_FeatureCollection and produces a
   * PolygonLayer for use in a Theme.
   */
  @SuppressWarnings("unchecked")
  private Layer readPolygons() {
    PolygonLayer polygonLayer = new PolygonLayer();
    Iterator<? extends FT_Feature> iterator = this.coll.iterator();
    while (iterator.hasNext()) {
      FT_Feature feat = iterator.next();
      GM_Object geom = feat.getGeom();
      if (geom != null) {
        if (GM_Polygon.class.isAssignableFrom(geom.getClass())) {
          GM_Polygon poly = (GM_Polygon) geom;
          GM_Polygon ext = new GM_Polygon(poly.getExterior());
          polygonLayer.addGeoPolygon(this.geOxygenePolygonToGeoPolygon(feat
              .hashCode(), ext));
          for (int i = 0; i < poly.sizeInterior(); i++) {
            GM_Polygon inter = new GM_Polygon(poly.getInterior(i));
            polygonLayer.addGeoPolygon(this.geOxygenePolygonToGeoPolygon(feat
                .hashCode(), inter));
          }
        } else if (GM_MultiSurface.class.isAssignableFrom(geom.getClass())
            || GM_Aggregate.class.isAssignableFrom(geom.getClass())) {
          GM_Aggregate<GM_Object> aggr = (GM_Aggregate<GM_Object>) geom;
          for (GM_Object o : aggr) {
            GM_Polygon poly = (GM_Polygon) o;
            GM_Polygon ext = new GM_Polygon(poly.getExterior());
            polygonLayer.addGeoPolygon(this.geOxygenePolygonToGeoPolygon(feat
                .hashCode(), ext));
            for (int i = 0; i < poly.sizeInterior(); i++) {
              GM_Polygon inter = new GM_Polygon(poly.getInterior(i));
              polygonLayer.addGeoPolygon(this.geOxygenePolygonToGeoPolygon(feat
                  .hashCode(), inter));
            }
          }
        }
      }
    }
    return polygonLayer;
  }

  /** Convert a GM_Point to a GeoPoint */
  private GeoPoint geOxygenePointToGeoPoint(int id, GM_Object geom) {
    GM_Point geOxyPoint = (GM_Point) geom;
    GeoPoint geoPoint = new GeoPoint(id, geOxyPoint.getPosition().getX(),
        geOxyPoint.getPosition().getY());
    return geoPoint;
  }

  /** Convert a GM_LineString to a GeoLine */
  private GeoLine geOxygeneLineStringToGeoLine(int id, GM_Object geom) {
    GM_LineString geOxyLineString = (GM_LineString) geom;
    DirectPositionList dpl = geOxyLineString.coord();
    int nbPts = dpl.size();
    GeoLine geoLine = new GeoLine(id, dpl.toArrayX(), dpl.toArrayY(), nbPts);
    return geoLine;
  }

  /** Convert a GM_Polygon (single, without holes) to a Polygon */
  private GeoPolygon geOxygenePolygonToGeoPolygon(int id, GM_Object geom) {
    GM_Polygon geOxyPolygon = (GM_Polygon) geom;
    DirectPositionList dpl = geOxyPolygon.coord();
    int nbPts = dpl.size();
    GeoPolygon geoPoly = new GeoPolygon(id, dpl.toArrayX(), dpl.toArrayY(),
        nbPts);
    return geoPoly;
  }

  @SuppressWarnings("unchecked")
  private void addGeoShape(FT_Feature feature, GM_Object geom) {
    if (GM_Point.class.isAssignableFrom(geom.getClass())) {
      ((PointLayer) this.layer).addGeoPoint(this.geOxygenePointToGeoPoint(
          feature.hashCode(), geom));
    } else if (GM_MultiPoint.class.isAssignableFrom(geom.getClass())) {
      GM_Aggregate<GM_Object> aggr = (GM_Aggregate<GM_Object>) geom;
      for (GM_Object o : aggr) {
        ((PointLayer) this.layer).addGeoPoint(this.geOxygenePointToGeoPoint(
            feature.hashCode(), o));
      }
    } else if (GM_LineString.class.isAssignableFrom(geom.getClass())) {
      ((LineLayer) this.layer).addGeoLine(this.geOxygeneLineStringToGeoLine(
          feature.hashCode(), geom));
    } else if (GM_MultiCurve.class.isAssignableFrom(geom.getClass())) {
      GM_Aggregate<GM_Object> aggr = (GM_Aggregate<GM_Object>) geom;
      for (GM_Object o : aggr) {
        ((LineLayer) this.layer).addGeoLine(this.geOxygeneLineStringToGeoLine(
            feature.hashCode(), o));
      }
    } else if (GM_Polygon.class.isAssignableFrom(geom.getClass())) {
      GM_Polygon poly = (GM_Polygon) geom;
      GM_Polygon ext = new GM_Polygon(poly.getExterior());
      ((PolygonLayer) this.layer).addGeoPolygon(this
          .geOxygenePolygonToGeoPolygon(feature.hashCode(), ext));
      for (int i = 0; i < poly.sizeInterior(); i++) {
        GM_Polygon inter = new GM_Polygon(poly.getInterior(i));
        ((PolygonLayer) this.layer).addGeoPolygon(this
            .geOxygenePolygonToGeoPolygon(feature.hashCode(), inter));
      }
    } else if (GM_MultiSurface.class.isAssignableFrom(geom.getClass())) {
      GM_Aggregate<GM_Object> aggr = (GM_Aggregate<GM_Object>) geom;
      for (GM_Object o : aggr) {
        GM_Polygon poly = (GM_Polygon) o;
        GM_Polygon ext = new GM_Polygon(poly.getExterior());
        ((PolygonLayer) this.layer).addGeoPolygon(this
            .geOxygenePolygonToGeoPolygon(feature.hashCode(), ext));
        for (int i = 0; i < poly.sizeInterior(); i++) {
          GM_Polygon inter = new GM_Polygon(poly.getInterior(i));
          ((PolygonLayer) this.layer).addGeoPolygon(this
              .geOxygenePolygonToGeoPolygon(feature.hashCode(), inter));
        }
      }
    } else {
      System.out
          .println(" ## GeOxygeneReader works only for GM_Point, GM_LineString or GM_Polygon or MultiPrimitives");
      System.out.println(" ## Have tried to read : "
          + geom.getClass().getName());
      return;
    }
  }

  /** Fills geodata objects with all of the fields of the collection. */
  public Object[] readData() {
    Vector<String> columnNames = new Vector<String>();
    Vector<Vector<Object>> rowData = new Vector<Vector<Object>>();

    // Fill columnNames
    Field[] fields = this.getAccessibleFields();
    int nbFields = fields.length;
    for (int i = 0; i < nbFields; i++) {
      columnNames.add(fields[i].getName());
    }

    // Fill rowData
    Iterator<? extends FT_Feature> iterator = this.coll.iterator();
    while (iterator.hasNext()) {
      FT_Feature feature = iterator.next();
      Vector<Object> row = new Vector<Object>();
      for (int i = 0; i < nbFields; i++) {
        try {
          row.add(fields[i].get(feature));
        } catch (Exception e) {
          System.out.println(e.getMessage());
          row.add(null);
        }
      }
      rowData.add(row);
    }

    return new Vector[] { columnNames, rowData };

  }

  /**
   * Fills a geodata objects with this field. Field type MUST be String ou
   * double.
   */
  GeoData readData(String fieldName) {
    SimpleGeoData data = new SimpleGeoData();
    try {
      Field field = this.getAccessibleField(fieldName);
      Iterator<? extends FT_Feature> iterator = this.coll.iterator();
      if (field.getType().equals(String.class)) {
        data.setDataType(GeoData.CHARACTER);
        while (iterator.hasNext()) {
          FT_Feature feature = iterator.next();
          Object obj = field.get(feature);
          if (obj != null) {
            data.setText(feature.hashCode(), (String) obj);
          }
        }
      } else if (field.getType().equals(double.class)) {
        data.setDataType(GeoData.FLOATING);
        while (iterator.hasNext()) {
          FT_Feature feature = iterator.next();
          Object obj = field.get(feature);
          if (obj != null) {
            data.setValue(feature.hashCode(), ((Double) obj).doubleValue());
          }
        }
      } else if (field.getType().equals(int.class)) {
        data.setDataType(GeoData.FLOATING);
        while (iterator.hasNext()) {
          FT_Feature feature = iterator.next();
          Object obj = field.get(feature);
          if (obj != null) {
            data.setValue(feature.hashCode(), ((Integer) obj).doubleValue());
          }
        }
      } else {
        System.out
            .println("readData() : works only for String or double or int "
                + fieldName);
        return data;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;

  }

  /** Returns all fields name. */
  public String[] getFieldsNames() {
    Field[] fields = ObjectBrowser.getAccessibleFields(this.featureClass,
        GeOxygeneReader.SHOW_PUBLIC_ATTRIBUTES,
        GeOxygeneReader.SHOW_PROTECTED_ATTRIBUTES);
    String[] strings = new String[fields.length];
    for (int i = 0; i < strings.length; i++) {
      strings[i] = fields[i].getName();
    }
    return strings;
  }

  /** Returns all possible values for a litteral field. */
  String[] getPossibleValues(String fieldName) {
    Set<Object> values = new HashSet<Object>();
    Field field = this.getAccessibleField(fieldName);
    Iterator<? extends FT_Feature> iterator = this.coll.iterator();
    while (iterator.hasNext()) {
      FT_Feature feature = iterator.next();
      try {
        values.add(field.get(feature));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    String[] result = new String[values.size()];
    Iterator<Object> it = values.iterator();
    int i = 0;
    while (it.hasNext()) {
      result[i] = (String) it.next();
      i++;
    }
    return result;

  }

  public Class<?> getFieldType(String fieldName) {
    return this.getAccessibleField(fieldName).getType();
  }

  Field getAccessibleField(String fieldName) {
    Field[] fields = this.getAccessibleFields();
    for (Field field : fields) {
      if (field.getName().equals(fieldName)) {
        return field;
      }
    }
    System.out.println("No field found : " + fieldName + " class "
        + this.featureClass);
    return null;
  }

  private Field[] getAccessibleFields() {
    return ObjectBrowser.getAccessibleFields(this.featureClass,
        GeOxygeneReader.SHOW_PUBLIC_ATTRIBUTES,
        GeOxygeneReader.SHOW_PROTECTED_ATTRIBUTES);
  }

  Color getRandomColor() {
    return new Color((float) Math.random(), (float) Math.random(), (float) Math
        .random());
  }

  public FT_FeatureCollection<? extends FT_Feature> getFeatureCollection() {
    return this.coll;
  }

  public FT_Feature getFeatureById(int id) {
    Iterator<? extends FT_Feature> iterator = this.coll.iterator();
    while (iterator.hasNext()) {
      FT_Feature f = iterator.next();
      if (f.hashCode() == id) {
        return f;
      }
    }
    System.out.println("### No feature found - id = " + id);
    return null;
  }

  class InitUniqueShader extends Thread {
    Shader shader;
    String shadedBy;

    public InitUniqueShader(Shader sh, String field) {
      this.shader = sh;
      this.shadedBy = field;
    }

    @Override
    public void run() {
      UniqueShader uShader = (UniqueShader) this.shader;
      Field field = GeOxygeneReader.this.getAccessibleField(this.shadedBy);
      String[] values = GeOxygeneReader.this.getPossibleValues(this.shadedBy);
      int n = values.length;
      Color[] colors = new Color[n];
      for (int i = 0; i < n; i++) {
        colors[i] = GeOxygeneReader.this.getRandomColor();
      }
      Iterator<? extends FT_Feature> iterator = GeOxygeneReader.this.coll
          .iterator();
      while (iterator.hasNext()) {
        FT_Feature feature = iterator.next();
        String value = null;
        try {
          value = (String) field.get(feature);
        } catch (Exception e) {
          e.printStackTrace();
        }
        for (int i = 0; i < n; i++) {
          if (value == null) {
            uShader.setColor(feature.hashCode(),
                ObjectViewerThemeProperties.DEFAULT_MISSING_VALUE_SHADER_COLOR);
            break;
          }
          if (value.equals(values[i])) {
            uShader.setColor(feature.hashCode(), colors[i]);
            break;
          }

        }
      }
    }

    public Shader getShader() {
      return this.shader;
    }
  }

  class InitHSVShader extends Thread {
    Shader shader;
    String shadedBy;

    public InitHSVShader(Shader sh, String field) {
      this.shader = sh;
      this.shadedBy = field;
    }

    @Override
    public void run() {
      SimpleGeoData data = (SimpleGeoData) GeOxygeneReader.this
          .readData(this.shadedBy);
      GeOxygeneReader.this.getTheme().setGeoData(data);
      this.shader.setRange(data);

    }

    public Shader getShader() {
      return this.shader;
    }
  }

}
