/**
 * 
 */
package fr.ign.cogit.geoxygene.style;

import java.awt.Color;
import java.util.Collection;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor;

/**
 * Convenience factory for creating GeOxygene layers.
 * @author Julien Perret
 * @author Bertrand Dumenieu
 */
public interface LayerFactory {
  void setModel(StyledLayerDescriptor model);
  void setGeometryType(Class<? extends IGeometry> geometryType);
  void setFillColor(Color fillColor);
  void setFillOpacity(float opacity);
  void setStrokeColor(Color strokeColor);
  void setStrokeWidth(float strokeWidth);
  void setStrokeOpacity(float opacity);
  void setBorderStrokeColor(Color borderStrokeColor);
  void setBorderStrokeWidth(float borderStrokeWidth);
  void setBorderStrokeOpacity(float opacity);
  void setMark(String wellKnownText);
  void setName(String layerNname);
  void setUnitOfMeasure(String unitOfMeasure);
  void setUndesirableColors(Collection<ColorimetricColor> undesirableColors);
  Stroke createStroke();
  Fill createFill();
  Mark createMark();
  Symbolizer createSymbolizer();
  Rule createRule();
  FeatureTypeStyle createFeatureTypeStyle();
  Style createStyle();
  Layer createLayer();
}
