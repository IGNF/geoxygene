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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.style.gradient.TriColorGradientPaint;

/**
 * @author Julien Perret
 * 
 */
public class RasterSymbolizer extends AbstractSymbolizer {
  @XmlElement(name = "ShadedRelief")
  ShadedRelief shadedRelief = null;

  public ShadedRelief getShadedRelief() {
    return this.shadedRelief;
  }

  public void setShadedRelief(ShadedRelief shadedRelief) {
    this.shadedRelief = shadedRelief;
  }

  @XmlElement(name = "ColorMap")
  ColorMap colorMap = null;

  public ColorMap getColorMap() {
    return this.colorMap;
  }

  public void setColorMap(ColorMap colorMap) {
    this.colorMap = colorMap;
  }

  @Override
  public boolean isRasterSymbolizer() {
    return true;
  }

  @XmlTransient
  Map<IFeature, GM_MultiSurface<GM_Triangle>> map = new HashMap<IFeature, GM_MultiSurface<GM_Triangle>>();
  @XmlTransient
  Map<GM_Triangle, Vecteur> normalMap = new HashMap<GM_Triangle, Vecteur>();
  @XmlTransient
  Map<IDirectPosition, List<GM_Triangle>> triangleMap = new HashMap<IDirectPosition, List<GM_Triangle>>();
  @XmlTransient
  Map<IDirectPosition, Vecteur> positionMap = new HashMap<IDirectPosition, Vecteur>();

  @Override
  public void paint(IFeature feature, Viewport viewport, Graphics2D graphics) {
    // FIXME c'est tout juste horrible !!!
    BufferedImage image = viewport.getLayerViewPanels().iterator().next()
        .getProjectFrame().getImage(feature);
    if (image == null) {
      return;
    }
    IEnvelope envelope = feature.getGeom().envelope();
    int dimensionX = image.getWidth();
    int dimensionY = image.getHeight();

    if (this.shadedRelief != null) {
      GM_MultiSurface<GM_Triangle> multi = this.map.get(feature);
      if (multi == null) {
        synchronized (this.map) {
          multi = new GM_MultiSurface<GM_Triangle>();
          // double reliefFactor = this.shadedRelief.getReliefFactor();
          // TODO use reliefFactor

          double width = envelope.width();
          double height = envelope.length();
          double dx = width / dimensionX;
          double dy = height / dimensionY;
          Raster raster = image.getData();
          IDirectPosition[][] positions = new DirectPosition[dimensionX][dimensionY];
          for (int x = 0; x < dimensionX; x++) {
            double pointx = envelope.minX() + (0.5 + x) * dx;
            for (int y = 0; y < dimensionY; y++) {
              double pointy = envelope.maxY() - (0.5 + y) * dy;
              double[] value = raster.getPixel(x, y, new double[1]);
              positions[x][y] = new DirectPosition(pointx, pointy, value[0]);
              if (y > 0 && x > 0) {
                GM_Triangle triangle = new GM_Triangle(positions[x - 1][y - 1],
                    positions[x][y], positions[x][y - 1]);
                this.addNeighbour(positions[x - 1][y - 1], triangle);
                this.addNeighbour(positions[x][y], triangle);
                this.addNeighbour(positions[x][y - 1], triangle);
                multi.add(triangle);
                Vecteur v1 = new Vecteur(triangle.getCorners(0).getDirect(),
                    triangle.getCorners(1).getDirect());
                Vecteur v2 = new Vecteur(triangle.getCorners(0).getDirect(),
                    triangle.getCorners(2).getDirect());
                Vecteur normal = v1.prodVectoriel(v2);
                normal.normalise();
                this.normalMap.put(triangle, normal);
                GM_Triangle triangle2 = new GM_Triangle(positions[x][y],
                    positions[x - 1][y - 1], positions[x - 1][y]);
                this.addNeighbour(positions[x - 1][y - 1], triangle2);
                this.addNeighbour(positions[x - 1][y], triangle2);
                this.addNeighbour(positions[x][y], triangle2);
                Vecteur v3 = new Vecteur(triangle2.getCorners(0).getDirect(),
                    triangle2.getCorners(1).getDirect());
                Vecteur v4 = new Vecteur(triangle2.getCorners(0).getDirect(),
                    triangle2.getCorners(2).getDirect());
                Vecteur normal2 = v3.prodVectoriel(v4);
                normal2.normalise();
                this.normalMap.put(triangle2, normal2);
                multi.add(triangle2);
              }
            }
          }
          this.map.put(feature, multi);
          for (int x = 0; x < dimensionX; x++) {
            for (int y = 0; y < dimensionY; y++) {
              Vecteur normal = new Vecteur(0, 0, 0);
              for (GM_Triangle neighbour : this.triangleMap
                  .get(positions[x][y])) {
                normal = normal.ajoute(this.normalMap.get(neighbour));
              }
              normal.normalise();
              this.positionMap.put(positions[x][y], normal);
            }
          }
        }
      }
      for (GM_Triangle triangle : multi) {
        // draw(viewport, graphics, triangle);
        this.drawWithNormals(viewport, graphics, triangle);
      }
      return;
    }
    BufferedImage imageToDraw = image;
    if (this.colorMap != null) {
      imageToDraw = new BufferedImage(image.getWidth(), image.getHeight(),
          BufferedImage.TYPE_INT_ARGB);
      Raster raster = image.getData();
      for (int x = 0; x < image.getWidth(); x++) {
        for (int y = 0; y < image.getHeight(); y++) {
          double[] value = raster.getPixel(x, y, new double[1]);
          imageToDraw.setRGB(x, y, this.colorMap.getColor(value[0]));
        }
      }
    }

    try {
      Shape shape = viewport.toShape(envelope.getGeom());
      double minX = shape.getBounds().getMinX();
      double minY = shape.getBounds().getMinY();
      double maxX = shape.getBounds().getMaxX();
      double maxY = shape.getBounds().getMaxY();
      graphics.drawImage(imageToDraw, (int) minX, (int) minY,
          (int) (maxX - minX), (int) (maxY - minY), null);
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
      return;
    }
  }

  private void drawWithNormals(Viewport viewport, Graphics2D graphics,
      GM_Triangle triangle) {
    if (!viewport.getEnvelopeInModelCoordinates().getGeom()
        .intersects(triangle)) {
      return;
    }
    try {
      Shape shape = viewport.toShape(triangle);
      Vecteur normal0 = this.positionMap.get(triangle.getCorners(0));
      Color color0 = this.getColor(normal0);
      Vecteur normal1 = this.positionMap.get(triangle.getCorners(1));
      Color color1 = this.getColor(normal1);
      Vecteur normal2 = this.positionMap.get(triangle.getCorners(2));
      Color color2 = this.getColor(normal2);

      graphics.setPaint(new TriColorGradientPaint(viewport.toViewPoint(triangle
          .getCorners(0).getDirect()), color0, viewport.toViewPoint(triangle
          .getCorners(1).getDirect()), color1, viewport.toViewPoint(triangle
          .getCorners(2).getDirect()), color2));
      /*
       * graphics.setColor(new Color( (color0.getRed() + color1.getRed() +
       * color2.getRed() )/3, (color0.getGreen() + color1.getGreen() +
       * color2.getGreen() )/3, (color0.getBlue() + color1.getBlue() +
       * color2.getBlue() )/3 ));
       */
      graphics.draw(shape);
      graphics.fill(shape);

    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
      return;
    }
  }

  private Color getColor(Vecteur normal) {
    int transparence = 255;
    double dirShadeBlackX = 1;
    double dirShadeBlackY = -1;
    double dirShadeBlackZ = -1;

    double dirShadeYellowX = -1;
    double dirShadeYellowY = -1;
    double dirShadeYellowZ = -1;

    double[] or = { normal.getX(), normal.getY(), normal.getZ() };

    double shadeBlack = -(or[0] * dirShadeBlackX + or[1] * dirShadeBlackY + or[2]
        * dirShadeBlackZ)
        / Math.sqrt(dirShadeBlackX * dirShadeBlackX + dirShadeBlackY
            * dirShadeBlackY + dirShadeBlackZ * dirShadeBlackZ);
    double shadeYellow = -(or[0] * dirShadeYellowX + or[1] * dirShadeYellowY + or[2]
        * dirShadeYellowZ)
        / Math.sqrt(dirShadeYellowX * dirShadeYellowX + dirShadeYellowY
            * dirShadeYellowY + dirShadeYellowZ * dirShadeYellowZ);

    double intensityBlack = 0.5, intensityYellow = 0.8;
    shadeBlack = intensityBlack + shadeBlack * (1 - intensityBlack);
    shadeYellow = intensityYellow + shadeYellow * (1 - intensityYellow);

    Color color = null;
    if (shadeBlack <= 0) {
      color = Color.BLACK;
    } else if (shadeYellow <= 0) {
      color = new Color((int) (255.0 * shadeBlack), (int) (255.0 * shadeBlack),
          0);
    } else {
      color = new Color((int) (255.0 * shadeBlack), (int) (255.0 * shadeBlack),
          (int) (255.0 * shadeYellow * shadeBlack), transparence);
    }
    return color;
  }

  private void addNeighbour(IDirectPosition directPosition, GM_Triangle triangle) {
    List<GM_Triangle> list = this.triangleMap.get(directPosition);
    if (list == null) {
      list = new ArrayList<GM_Triangle>();
      this.triangleMap.put(directPosition, list);
    }
    list.add(triangle);
  }
}
