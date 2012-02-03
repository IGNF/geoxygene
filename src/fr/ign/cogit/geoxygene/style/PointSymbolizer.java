/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.style;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PointSymbolizer extends AbstractSymbolizer {
  @Override
  public boolean isPointSymbolizer() {
    return true;
  }

  @XmlElement(name = "Graphic")
  private Graphic graphic = null;

  public Graphic getGraphic() {
    return this.graphic;
  }

  public void setGraphic(Graphic graphic) {
    this.graphic = graphic;
  }


  @XmlElement(name = "ColorMap")
  ColorMap colorMap = null;

  public ColorMap getColorMap() {
    return this.colorMap;
  }

  public void setColorMap(ColorMap colorMap) {
    this.colorMap = colorMap;
  }

//  @Override
//  public void paint(IFeature feature, Viewport viewport, Graphics2D graphics) {
//    if (this.getGraphic() == null) {
//      return;
//    }
//    Point2D point;
//    IGeometry geometry = feature.getGeom();
//    if (this.getGeometryPropertyName() != null
//        && !this.getGeometryPropertyName().equalsIgnoreCase("geom")) { //$NON-NLS-1$
//      geometry = (IGeometry) feature.getAttribute(this
//          .getGeometryPropertyName());
//    }
//    if (geometry == null) {
//      return;
//    }
//    try {
//      point = viewport.toViewPoint(geometry.centroid());
//    } catch (NoninvertibleTransformException e) {
//      e.printStackTrace();
//      return;
//    }
//    for (Mark mark : this.getGraphic().getMarks()) {
//      Shape markShape = mark.toShape();
//      float size = this.getGraphic().getSize();
//      double scale = 1;
//      if (!this.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
//        try {
//          scale = viewport.getModelToViewTransform().getScaleX();
//        } catch (NoninvertibleTransformException e) {
//          e.printStackTrace();
//        }
//      }
//      size *= scale;
//      AffineTransform at = AffineTransform.getTranslateInstance(point.getX(),
//          point.getY());
//      at.rotate(this.getGraphic().getRotation());
//      at.scale(size, size);
//      markShape = at.createTransformedShape(markShape);
//
//      graphics.setColor((mark.getFill() == null) ? Color.gray : mark.getFill()
//          .getColor());
//      graphics.fill(markShape);
//      graphics.setStroke(mark.getStroke().toAwtStroke((float) scale));
//      graphics.setColor((mark.getStroke() == null) ? Color.black : mark
//          .getStroke().getColor());
//      graphics.draw(markShape);
//    }
//    for (ExternalGraphic theGraphic : this.getGraphic().getExternalGraphics()) {
//      Image onlineImage = theGraphic.getOnlineResource();
//      graphics.drawImage(onlineImage,
//          (int) point.getX() - onlineImage.getWidth(null) / 2,
//          (int) point.getY() - onlineImage.getHeight(null) / 2, null);
//    }
//  }
}
