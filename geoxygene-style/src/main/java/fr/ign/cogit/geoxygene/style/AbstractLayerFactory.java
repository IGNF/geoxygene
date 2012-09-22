/**
 * 
 */
package fr.ign.cogit.geoxygene.style;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorReferenceSystem;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor;

/**
 * @author Julien Perret
 */
public abstract class AbstractLayerFactory implements LayerFactory {
  protected static Logger logger = Logger.getLogger(AbstractLayerFactory.class.getName());
  protected StyledLayerDescriptor model = null;
  @Override
  public void setModel(StyledLayerDescriptor newModel) {
    this.model = newModel;
  }
  protected Class<? extends IGeometry> geometryType = null;
  @Override
  public void setGeometryType(Class<? extends IGeometry> newGeometryType) {
    this.geometryType = newGeometryType;
  }
  protected Color fillColor = null;
  public void setFillColor(Color fillColor) {
    this.fillColor = fillColor;
  }
  protected float fillOpacity = 0.8f;
  public void setFillOpacity(float opacity) {
    this.fillOpacity = opacity;
  }
  protected Color strokeColor = null;
  public void setStrokeColor(Color strokeColor) {
    this.strokeColor = strokeColor;
  }
  protected float strokeWidth = 1.0f;
  public void setStrokeWidth(float strokeWidth) {
    this.strokeWidth = strokeWidth;
  }
  protected float strokeOpacity = 0.8f;
  public void setStrokeOpacity(float opacity) {
    this.strokeOpacity = opacity;
  }
  protected Color borderStrokeColor = null;
  public void setBorderStrokeColor(Color borderStrokeColor) {
    this.borderStrokeColor = borderStrokeColor;
  }
  protected float borderStrokeWidth = 2.0f;
  public void setBorderStrokeWidth(float borderStrokeWidth) {
    this.borderStrokeWidth = borderStrokeWidth;
  }
  protected float borderStrokeOpacity = 0.8f;
  public void setBorderStrokeOpacity(float opacity) {
    this.borderStrokeOpacity = opacity;
  }
  protected String mark = null;
  public void setMark(String wellKnownText) {
    this.mark = wellKnownText;
  }
  protected String name = null;
  public void setName(String layerName) {
    this.name = layerName;
  }
  protected String unitOfMeasure = Symbolizer.METRE;
  public String getUnitOfMeasure() {
    return this.unitOfMeasure;
  }
  public void setUnitOfMeasure(String unitOfMeasure) {
    this.unitOfMeasure = unitOfMeasure;
  }
  protected Collection<ColorimetricColor> undesirableColors = null;
  public void setUndesirableColors(Collection<ColorimetricColor> undesirableColors) {
    this.undesirableColors = undesirableColors;
  }
  public Style createStyle() {
    UserStyle style = new UserStyle();
    style.setName("Style créé pour le layer " + this.name);//$NON-NLS-1$
    if (this.borderStrokeColor != null) {
      AbstractLayerFactory.logger.debug("Creating border Feature Type Style");
      style.getFeatureTypeStyles().add(this.createBorderFeatureTypeStyle());
    }
    style.getFeatureTypeStyles().add(this.createFeatureTypeStyle());
    return style;
  }
  public FeatureTypeStyle createFeatureTypeStyle() {
    FeatureTypeStyle fts = new FeatureTypeStyle();
    fts.getRules().add(this.createRule());
    return fts;
  }
  public FeatureTypeStyle createBorderFeatureTypeStyle() {
    FeatureTypeStyle fts = new FeatureTypeStyle();
    fts.getRules().add(this.createBorderRule());
    return fts;
  }
  public Rule createRule() {
    Rule rule = new Rule();
    rule.setLegendGraphic(new LegendGraphic());
    rule.getLegendGraphic().setGraphic(new Graphic());
    rule.getSymbolizers().add(this.createSymbolizer());
    return rule;
  }
  public Rule createBorderRule() {
    Rule rule = new Rule();
    rule.setLegendGraphic(new LegendGraphic());
    rule.getLegendGraphic().setGraphic(new Graphic());
    rule.getSymbolizers().add(this.createBorderSymbolizer());
    return rule;
  }
  public Stroke createStroke() {
    Stroke stroke = new Stroke();
    Color color = this.strokeColor;
    if (color == null) {
      color = getNewColor();
    }
    stroke.setStroke(color);
    stroke.setStrokeOpacity(this.strokeOpacity);
    stroke.setStrokeWidth(this.strokeWidth);
    return stroke;
  }
  public Stroke createBorderStroke() {
    Stroke stroke = new Stroke();
    stroke.setStroke(this.borderStrokeColor);
    stroke.setStrokeOpacity(this.borderStrokeOpacity);
    stroke.setStrokeWidth(this.borderStrokeWidth);
    return stroke;
  }
  public Fill createFill() {
    Fill fill = new Fill();
    Color color = this.fillColor;
    if (color == null) {
      color = getNewColor();
    }
    fill.setFill(color);
    fill.setFillOpacity(this.fillOpacity);
    return fill;
  }
  public Symbolizer createSymbolizer() {
    if (this.geometryType == null) {
      /** Ajoute un raster symbolizer */
      RasterSymbolizer rasterSymbolizer = new RasterSymbolizer();
      rasterSymbolizer.setStroke(this.createStroke());
      return rasterSymbolizer;
    }
    if (IPolygon.class.isAssignableFrom(this.geometryType)
        || IMultiSurface.class.isAssignableFrom(this.geometryType)) {
      /** Ajoute un polygone symbolizer */
      PolygonSymbolizer polygonSymbolizer = new PolygonSymbolizer();
      polygonSymbolizer.setStroke(this.createStroke());
      polygonSymbolizer.setFill(this.createFill());
      polygonSymbolizer.setUnitOfMeasure(this.getUnitOfMeasure());
      return polygonSymbolizer;
    }
    if (ICurve.class.isAssignableFrom(this.geometryType)
        || IMultiCurve.class.isAssignableFrom(this.geometryType)) {
      /** Ajoute un line symbolizer */
      LineSymbolizer lineSymbolizer = new LineSymbolizer();
      lineSymbolizer.setStroke(this.createStroke());
      lineSymbolizer.setUnitOfMeasure(this.getUnitOfMeasure());
      return lineSymbolizer;
    }
    if (IPoint.class.isAssignableFrom(this.geometryType)
        || IMultiPoint.class.isAssignableFrom(this.geometryType)) {
      /** Ajoute un point symbolizer */
      PointSymbolizer pointSymbolizer = new PointSymbolizer();
      Graphic graphic = new Graphic();
      graphic.getMarks().add(this.createMark());
      pointSymbolizer.setGraphic(graphic);
      pointSymbolizer.setUnitOfMeasure(this.getUnitOfMeasure());
      return pointSymbolizer;
    }
    return null;
  }
  public Symbolizer createBorderSymbolizer() {
    if (ICurve.class.isAssignableFrom(this.geometryType)
        || IMultiCurve.class.isAssignableFrom(this.geometryType)) {
      /** Ajoute un line symbolizer */
      LineSymbolizer lineSymbolizer = new LineSymbolizer();
      lineSymbolizer.setStroke(this.createBorderStroke());
      return lineSymbolizer;
    }
    return null;
  }
  public Mark createMark() {
    Mark mark = new Mark();
    mark.setWellKnownName(this.mark);
    mark.setStroke(this.createStroke());
    mark.setFill(this.createFill());
    return mark;
  }
  public Color getNewColor() {
    ColorReferenceSystem crs = ColorReferenceSystem.defaultColorRS();
    if (this.undesirableColors != null) {
      List<ColorimetricColor> colorList = ColorReferenceSystem.getCOGITColors();
      // shuffle the list not to always take the first color
      Collections.shuffle(colorList);
      for(ColorimetricColor color : colorList){
        if(!this.undesirableColors.contains(color)){
          return color.toColor();
        }
      }
    }
    // choose a random color
    List<ColorimetricColor> colors = crs.getAllColors();
    return colors.get(new Random().nextInt(colors.size())).toColor();
  }
  public abstract Layer createLayer();
}
