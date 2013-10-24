package fr.ign.cogit.cartagen.core.carto;

import java.awt.Color;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.UserStyle;

public class SLDUtil {

  /**
   * Gets the symbol width of a linear object from the SLD value for this
   * object. The width value is in terrain meters.
   * @param obj
   * @return
   */
  public static double getSymbolMaxWidth(IGeneObjLin obj) {
    String layerName = CartAGenDoc.getInstance().getCurrentDataset()
        .getPopNameFromObj(obj);
    StyledLayerDescriptor sld = CartAGenDoc.getInstance().getCurrentDataset()
        .getSld();
    Layer layer = sld.getLayer(layerName);
    // get the background style (max width is the bottom style width)
    Style style = layer.getStyles().get(0);
    double width = 0.0;
    for (FeatureTypeStyle ftStyle : style.getFeatureTypeStyles()) {
      Rule rule = ftStyle.getRules().get(0);
      if (rule.getFilter() != null)
        if (!rule.getFilter().evaluate(obj))
          continue;

      for (Symbolizer symbolizer : rule.getSymbolizers()) {
        if (symbolizer instanceof LineSymbolizer)
          width = symbolizer.getStroke().getStrokeWidth();
      }
    }
    return width;
  }

  /**
   * Gets the innner symbol width of a linear object from the SLD value for this
   * object. The width value is in terrain meters.
   * @param obj
   * @return
   */
  public static double getSymbolInnerWidth(IGeneObjLin obj) {
    String layerName = CartAGenDoc.getInstance().getCurrentDataset()
        .getPopNameFromObj(obj);
    StyledLayerDescriptor sld = CartAGenDoc.getInstance().getCurrentDataset()
        .getSld();
    Layer layer = sld.getLayer(layerName);
    // get the foreground style (inner width is the upper style width)
    Style style = layer.getStyles().get(layer.getStyles().size() - 1);
    double width = 0.0;
    for (FeatureTypeStyle ftStyle : style.getFeatureTypeStyles()) {
      Rule rule = ftStyle.getRules().get(0);
      if (rule.getFilter() != null)
        if (!rule.getFilter().evaluate(obj))
          continue;

      for (Symbolizer symbolizer : rule.getSymbolizers()) {
        if (symbolizer instanceof LineSymbolizer)
          width = symbolizer.getStroke().getStrokeWidth();
      }
    }
    return width;
  }

  /**
   * Gets the symbol inner width of a linear object from the SLD value for this
   * object. The width value is in map millimeters.
   * @param obj
   * @return
   */
  public static double getSymbolInnerWidthMapMm(IGeneObjLin obj) {
    double width = getSymbolInnerWidth(obj);
    return width / Legend.getSYMBOLISATI0N_SCALE() * 1000.0;
  }

  /**
   * Gets the symbol width of a linear object from the SLD value for this
   * object. The width value is in map millimeters.
   * @param obj
   * @return
   */
  public static double getSymbolMaxWidthMapMm(IGeneObjLin obj) {
    double width = getSymbolMaxWidth(obj);
    return width / Legend.getSYMBOLISATI0N_SCALE() * 1000.0;
  }

  /**
   * Add a user style to display the initial geometry of the features of the
   * given layer, on top of the symbols.
   * @param obj
   */
  public static void addInitialGeomDisplay(Layer layer, Color color, int width) {
    UserStyle style = new UserStyle();
    FeatureTypeStyle ftStyle = new FeatureTypeStyle();
    ftStyle.setName("initial geometry");
    style.getFeatureTypeStyles().add(ftStyle);
    Rule rule = new Rule();
    ftStyle.getRules().add(rule);
    IFeature feat = layer.getFeatureCollection().get(0);
    if (feat.getGeom() instanceof IPolygon) {
      Symbolizer symbolizer = new LineSymbolizer();
      symbolizer.setGeometryPropertyName("initialGeom");
      Stroke stroke = new Stroke();
      stroke.setColor(color);
      stroke.setStrokeWidth(width);
      symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
      symbolizer.setStroke(stroke);
      rule.getSymbolizers().add(symbolizer);
    } else if (feat.getGeom() instanceof ILineString) {
      PolygonSymbolizer symbolizer = new PolygonSymbolizer();
      symbolizer.setGeometryPropertyName("initialGeom");
      Stroke stroke = new Stroke();
      stroke.setColor(color);
      stroke.setStrokeWidth(width);
      symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
      symbolizer.setStroke(stroke);
      rule.getSymbolizers().add(symbolizer);
    } else if (feat.getGeom() instanceof IPoint) {
      PointSymbolizer symbolizer = new PointSymbolizer();
      symbolizer.setGeometryPropertyName("initialGeom");
      symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
      Graphic graphic = new Graphic();
      Mark mark = new Mark();
      mark.setWellKnownName("cross");
      Fill fill = new Fill();
      fill.setColor(color);
      mark.setFill(fill);
      graphic.getMarks().add(mark);
      symbolizer.setGraphic(graphic);
      rule.getSymbolizers().add(symbolizer);
    }

    // add the new style to the layer
    layer.getStyles().add(style);
  }

  /**
   * Removes, if exists, the style that displays the initial geometries of the
   * layer.
   * @param layer
   */
  public static void removeInitialGeomDisplay(Layer layer) {
    Style initialStyle = null;
    for (Style style : layer.getStyles()) {
      if ("initial geometry".equals(style.getFeatureTypeStyles().get(0)
          .getName())) {
        initialStyle = style;
        break;
      }
    }
    if (initialStyle != null)
      layer.getStyles().remove(initialStyle);
  }

  /**
   * Checks if a layer has a style for displaying initial geometries.
   * @param layer
   * @return
   */
  public static boolean layerHasInitialDisplay(Layer layer) {
    for (Style style : layer.getStyles()) {
      if ("initial geometry".equals(style.getFeatureTypeStyles().get(0)
          .getName())) {
        return true;
      }
    }
    return false;
  }
}
