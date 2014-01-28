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

package fr.ign.cogit.geoxygene.appli;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.swing.JPanel;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.log4j.Logger;

import com.jhlabs.image.LinearColormap;
import com.jhlabs.image.TextureFilter;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.FT_Coverage;
import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.filter.expression.Expression;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.GraphicFill;
import fr.ign.cogit.geoxygene.style.GraphicStroke;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.texture.PerlinNoiseTexture;
import fr.ign.cogit.geoxygene.util.ColorUtil;

/**
 * @author Julien Perret
 * @author Charlotte Hoarau
 */
public class LayerStylesPanel extends JPanel {
  private static final Logger logger = Logger.getLogger(LayerStylesPanel.class);

  /**
   * Serial uid.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The layer the panel corresponds to.
   */
  private WeakReference<Layer> layer;

  /**
   * @return the layer the panel corresponds to
   */
  public Layer getLayer() {
    return this.layer.get();
  }

  /**
   * A margin around the panel.
   */
  private int margin = 2;

  /**
   * Constructor.
   */
  public LayerStylesPanel(Layer aLayer) {
    this.layer = new WeakReference<Layer>(aLayer);
    this.setBackground(Color.white);
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    // store the initial color of the graphics
    Color originalColor = g.getColor();
    // clear the graphics using the background color
    g.setColor(this.getBackground());
    g2.setStroke(new BasicStroke());
    g.fillRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
    // count the number of filters
    Map<Filter, List<Symbolizer>> map = new HashMap<Filter, List<Symbolizer>>();
    for (Style style : this.layer.get().getStyles()) {
      for (FeatureTypeStyle fts : style.getFeatureTypeStyles()) {
        for (Rule r : fts.getRules()) {
          if (!map.containsKey(r.getFilter())) {
            map.put(r.getFilter(),
                new ArrayList<Symbolizer>(r.getSymbolizers()));
          } else {
            map.get(r.getFilter()).addAll(r.getSymbolizers());
          }
        }
      }
    }
    int numberOfColumns = Math.max(1,
        Math.round((float) Math.sqrt(map.keySet().size())));
    int numberOfRows = Math.max(1, map.keySet().size() / numberOfColumns);
    int columnsWidth = (this.getWidth() - numberOfColumns * this.margin - this.margin)
        / numberOfColumns;
    int rowHeight = (this.getHeight() - numberOfRows * this.margin - this.margin)
        / numberOfRows;
    int currentColumn = 0;
    int currentRow = 0;
    for (Entry<Filter, List<Symbolizer>> entry : map.entrySet()) {
      List<Symbolizer> symbolizers = entry.getValue();
      for (Symbolizer symbolizer : symbolizers) {
        if (symbolizer.isLineSymbolizer()) {
          this.paintLine((LineSymbolizer) symbolizer, g2, currentColumn,
              currentRow, columnsWidth, rowHeight);
        } else {
          if (symbolizer.isPointSymbolizer()) {
            this.paintPoint((PointSymbolizer) symbolizer, g2, currentColumn,
                currentRow, columnsWidth, rowHeight);
          } else {
            if (symbolizer.isPolygonSymbolizer()) {
              this.paintPolygon((PolygonSymbolizer) symbolizer, g2,
                  currentColumn, currentRow, columnsWidth, rowHeight);
            } else {
              if (symbolizer.isRasterSymbolizer()) {
                this.paintRaster((RasterSymbolizer) symbolizer, g2,
                    currentColumn, currentRow, columnsWidth, rowHeight);
              }
            }
          }
        }
      }
      currentColumn++;
      if (currentColumn >= numberOfColumns) {
        currentColumn = 0;
        currentRow++;
      }
    }
    // restore the initial color
    g.setColor(originalColor);
  }

  /**
   * Paint a line symbolizer at the given position.
   * @param symbolizer the symbolizer
   * @param g2 the graphics to paint in
   * @param currentColumn
   * @param currentRow
   * @param columnsWidth
   * @param rowHeight
   */
  private void paintLine(LineSymbolizer symbolizer, Graphics2D g2,
      int currentColumn, int currentRow, int columnsWidth, int rowHeight) {
    int x1 = currentColumn * (columnsWidth + this.margin);
    int y1 = currentRow * (rowHeight + this.margin) + rowHeight / 2;
    int x2 = (currentColumn + 1) * (columnsWidth + this.margin) + this.margin;
    int y2 = currentRow * (rowHeight + this.margin) + rowHeight / 2;
    int widthDrawn = x2 - x1;

    if (symbolizer.getStroke() != null) {
      if (symbolizer.getStroke().getColor() != null) {
        g2.setStroke(symbolizer.getStroke().toAwtStroke());
        g2.setColor(symbolizer.getStroke().getColor());
        g2.drawLine(x1, y1, x2, y2);
      }
      if (symbolizer.getStroke().getGraphicType() != null) {
        if (GraphicFill.class.isAssignableFrom(symbolizer.getStroke()
            .getGraphicType().getClass())) {
          List<Graphic> graphicList = ((GraphicFill) symbolizer.getStroke()
              .getGraphicType()).getGraphics();
          for (Graphic graphic : graphicList) {
            for (ExternalGraphic external : graphic.getExternalGraphics()) {
              if (external.getFormat().contains("png") || external.getFormat().contains("gif")) { //$NON-NLS-1$ //$NON-NLS-2$
                Image image = external.getOnlineResource();
                drawGraphicFillPolygon(image, graphic.getSize(), g2, getLayer()
                    .getOpacity(), widthDrawn, symbolizer.getStroke()
                    .getStrokeWidth() * 2, rowHeight / 2
                    - symbolizer.getStroke().getStrokeWidth() / 2, 0);
              } else {
                if (external.getFormat().contains("svg")) { //$NON-NLS-1$
                  GraphicsNode node = external.getGraphicsNode();
                  drawGraphicFillPolygon(node, graphic.getSize(), g2,
                      getLayer().getOpacity(), widthDrawn, symbolizer
                          .getStroke().getStrokeWidth() * 2, rowHeight / 2
                          - symbolizer.getStroke().getStrokeWidth() / 2, 0);
                }
              }
              return;
            }
            int markShapeSize = 200;
            for (Mark mark : graphic.getMarks()) {
              Shape markShape = mark.toShape();
              AffineTransform translate = AffineTransform.getTranslateInstance(
                  markShapeSize / 2, markShapeSize / 2);
              if (graphic.getRotation() != null) {
                AffineTransform rotate = AffineTransform
                    .getRotateInstance(Math.PI
                        * Double.parseDouble(graphic.getRotation()
                            .evaluate(null).toString()) / 180.0);
                translate.concatenate(rotate);
              }
              AffineTransform scaleTransform = AffineTransform
                  .getScaleInstance(markShapeSize, markShapeSize);
              translate.concatenate(scaleTransform);
              Shape tranlatedShape = translate
                  .createTransformedShape(markShape);
              BufferedImage buff = new BufferedImage(markShapeSize,
                  markShapeSize, BufferedImage.TYPE_INT_ARGB);
              Graphics2D g = (Graphics2D) buff.getGraphics();
              g.setColor(ColorUtil.getColorWithOpacity(mark.getFill()
                  .getColor(), getLayer().getOpacity()));
              g.fill(tranlatedShape);
              drawGraphicFillPolygon(buff, graphic.getSize(), g2, getLayer()
                  .getOpacity(), widthDrawn, symbolizer.getStroke()
                  .getStrokeWidth() * 2, rowHeight / 2
                  - symbolizer.getStroke().getStrokeWidth() / 2, 0);
            }
          }
        } else if (GraphicStroke.class.isAssignableFrom(symbolizer.getStroke()
            .getGraphicType().getClass())) {
          // TODO Finir l'implémentation pour les GraphicStroke
          g2.setColor(Color.green);
        }
      }
    }
  }

  public static void drawGraphicFillPolygon(GraphicsNode node, float size,
      Graphics2D graphics, double opacity, double widthSymbol,
      double heightSymbol, double offsetYSymbol, double offsetXSymbol) {
    AffineTransform translate = AffineTransform.getTranslateInstance(-node
        .getBounds().getMinX(), -node.getBounds().getMinY());
    node.setTransform(translate);
    BufferedImage buff = new BufferedImage((int) node.getBounds().getWidth(),
        (int) node.getBounds().getHeight(), BufferedImage.TYPE_INT_ARGB);
    node.paint((Graphics2D) buff.getGraphics());
    drawGraphicFillPolygon(buff, size, graphics, opacity, widthSymbol,
        heightSymbol, offsetYSymbol, offsetXSymbol);
  }

  private static void drawGraphicFillPolygon(Image image, float size,
      Graphics2D graphics, double opacity, double widthSymbol,
      double heightSymbol, double offsetYSymbol, double offsetXSymbol) {
    Double shapeHeight = new Double(size);
    double factor = shapeHeight / image.getHeight(null);
    Double shapeWidth = new Double(Math.max(image.getWidth(null) * factor, 1));
    AffineTransform transform = AffineTransform.getTranslateInstance(
        offsetXSymbol, offsetYSymbol);
    Image scaledImage = image.getScaledInstance(shapeWidth.intValue(),
        shapeHeight.intValue(), Image.SCALE_FAST);
    BufferedImage buff = new BufferedImage(shapeWidth.intValue(),
        shapeHeight.intValue(), BufferedImage.TYPE_INT_ARGB);
    buff.getGraphics().drawImage(scaledImage, 0, 0, null);
    ParameterBlock p = new ParameterBlock();
    p.addSource(buff);
    p.add((int) widthSymbol);
    p.add((int) heightSymbol);
    RenderedOp im = JAI.create("pattern", p);//$NON-NLS-1$
    BufferedImage bufferedImage = im.getAsBufferedImage();
    graphics.drawImage(bufferedImage, transform, null);
    bufferedImage.flush();
    im.dispose();
    scaledImage.flush();
    buff.flush();
  }

  @Override
  public void setBounds(int x, int y, int w, int h) {
    super.setBounds(x, y, w, h);
    this.validate();
  }

  /**
   * @param symbolizer
   * @param g2
   * @param currentColumn
   * @param currentRow
   * @param columnsWidth
   * @param rowHeight
   */
  private void paintPoint(PointSymbolizer symbolizer, Graphics2D g2,
      int currentColumn, int currentRow, int columnsWidth, int rowHeight) {
    for (Mark mark : symbolizer.getGraphic().getMarks()) {
      Shape markShape = mark.toShape();
      float size = symbolizer.getGraphic().getSize();
      AffineTransform at = AffineTransform.getTranslateInstance(this.margin
          + currentColumn * (columnsWidth + this.margin) + columnsWidth / 2,
          this.margin + currentRow * (rowHeight + this.margin) + rowHeight / 2);
      Expression rotation = symbolizer.getGraphic().getRotation();
      if (rotation instanceof Literal) {
        // if this is a literal, we can evaluate it
        at.rotate(-Double.parseDouble(rotation.evaluate(null).toString())
            * Math.PI / 180.0);
      }
      at.scale(3 * size, 3 * size);
      markShape = at.createTransformedShape(markShape);
      g2.setColor((mark.getFill() == null) ? Color.gray : mark.getFill()
          .getColor());
      g2.fill(markShape);
      if (mark.getStroke() != null)
        g2.setStroke(mark.getStroke().toAwtStroke());
      g2.setColor((mark.getStroke() == null) ? Color.black : mark.getStroke()
          .getColor());
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
      g2.draw(markShape);
    }
    for (ExternalGraphic theGraphic : symbolizer.getGraphic()
        .getExternalGraphics()) {
      String format = theGraphic.getFormat();
      if (format.contains("svg")) { //$NON-NLS-1$
        GraphicsNode node = theGraphic.getGraphicsNode();

        int x1 = currentColumn * (columnsWidth + this.margin);
        int x2 = (currentColumn + 1) * (columnsWidth + this.margin)
            + this.margin;
        int widthDrawn = x2 - x1;

        // FIXME find better parameters to display the symbol display.
        drawGraphicFillPolygon(node, symbolizer.getGraphic().getSize(), g2,
            getLayer().getOpacity(), widthDrawn, widthDrawn, rowHeight / 2
                - widthDrawn / 2, 0);
      } else if (format.contains("png") || format.contains("gif")) { //$NON-NLS-1$ //$NON-NLS-2$

        Image onlineImage = theGraphic.getOnlineResource();
        g2.drawImage(onlineImage,
            this.margin + currentColumn * (columnsWidth + this.margin)
                + columnsWidth / 2 - onlineImage.getWidth(null) / 2,
            this.margin + currentRow * (rowHeight + this.margin) + rowHeight
                / 2 - onlineImage.getHeight(null) / 2, null);
      }
    }
  }

  /**
   * @param symbolizer
   * @param g2
   * @param currentColumn
   * @param currentRow
   * @param columnsWidth
   * @param rowHeight
   */
  private void paintPolygon(PolygonSymbolizer symbolizer, Graphics2D g2,
      int currentColumn, int currentRow, int columnsWidth, int rowHeight) {

    int x1 = currentColumn * (columnsWidth + this.margin);
    int x2 = (currentColumn + 1) * (columnsWidth + this.margin) + this.margin;
    int widthDrawn = x2 - x1;

    if (symbolizer.getShadow() != null) {
      g2.setColor(symbolizer.getShadow().getColor());
      g2.fillRect(currentColumn * (columnsWidth + this.margin) + this.margin,
          currentRow * (rowHeight + this.margin) + this.margin, columnsWidth,
          rowHeight);
    }
    if (symbolizer.getFill() != null) {
      if (symbolizer.getFill().getTexture() != null) {
        if (PerlinNoiseTexture.class.isAssignableFrom(symbolizer.getFill()
            .getTexture().getClass())) {
          PerlinNoiseTexture texture = (PerlinNoiseTexture) symbolizer
              .getFill().getTexture();
          TextureFilter filter = new TextureFilter();
          filter.setColormap(new LinearColormap(texture.getColor1().getRGB(),
              texture.getColor2().getRGB()));
          filter.setScale(texture.getScale());
          filter.setStretch(texture.getStretch());
          filter.setAmount(texture.getAmount());
          filter.setAngle(texture.getAngle());
          BufferedImage textureImg = new BufferedImage(columnsWidth, rowHeight,
              BufferedImage.TYPE_INT_ARGB);
          filter.filter(textureImg, textureImg);
          g2.drawImage(textureImg, this.margin, this.margin, null);
        }
      } else if (symbolizer.getFill().getColor() != null) {
        g2.setColor(symbolizer.getFill().getColor());
        g2.fillRect(currentColumn * (columnsWidth + this.margin) + this.margin,
            currentRow * (rowHeight + this.margin) + this.margin, columnsWidth,
            rowHeight);
      }
      if (symbolizer.getFill().getGraphicFill() != null) {
        List<Graphic> graphicList = (symbolizer.getFill().getGraphicFill())
            .getGraphics();
        for (Graphic graphic : graphicList) {
          if (graphic.getExternalGraphics().size() != 0) {
            for (ExternalGraphic external : graphic.getExternalGraphics()) {
              if (external.getFormat().contains("png") || external.getFormat().contains("gif")) { //$NON-NLS-1$ //$NON-NLS-2$
                Image image = external.getOnlineResource();
                drawGraphicFillPolygon(image, graphic.getSize(), g2, getLayer()
                    .getOpacity(), widthDrawn, rowHeight, 0, 0);
              } else {
                if (external.getFormat().contains("svg")) { //$NON-NLS-1$
                  GraphicsNode node = external.getGraphicsNode();
                  drawGraphicFillPolygon(node, graphic.getSize(), g2,
                      getLayer().getOpacity(), widthDrawn, rowHeight, 0, 0);
                }
              }
            }
          } else if (graphic.getMarks().size() != 0) {
            int markShapeSize = 200;
            for (Mark mark : graphic.getMarks()) {
              Shape markShape = mark.toShape();
              AffineTransform translate = AffineTransform.getTranslateInstance(
                  markShapeSize / 2, markShapeSize / 2);
              if (graphic.getRotation() != null) {
                AffineTransform rotate = AffineTransform
                    .getRotateInstance(Math.PI
                        * Double.parseDouble(graphic.getRotation()
                            .evaluate(null).toString()) / 180.0);
                translate.concatenate(rotate);
              }
              AffineTransform scaleTransform = AffineTransform
                  .getScaleInstance(markShapeSize, markShapeSize);
              translate.concatenate(scaleTransform);
              Shape tranlatedShape = translate
                  .createTransformedShape(markShape);
              BufferedImage buff = new BufferedImage(markShapeSize,
                  markShapeSize, BufferedImage.TYPE_INT_ARGB);
              Graphics2D g = (Graphics2D) buff.getGraphics();
              g.setColor(ColorUtil.getColorWithOpacity(mark.getFill()
                  .getColor(), getLayer().getOpacity()));
              g.fill(tranlatedShape);

              drawGraphicFillPolygon(buff, graphic.getSize(), g2, getLayer()
                  .getOpacity(), widthDrawn, rowHeight, 0, 0);
            }
          }
        }
      }
    }
    if (symbolizer.getStroke() != null) {
      if (symbolizer.getStroke().getColor() != null) {
        g2.setColor(symbolizer.getStroke().getColor());
        g2.setStroke(symbolizer.getStroke().toAwtStroke());
        g2.drawRect(currentColumn * (columnsWidth + this.margin) + this.margin,
            currentRow * (rowHeight + this.margin) + this.margin, columnsWidth,
            rowHeight);
      }
      if (symbolizer.getStroke().getGraphicType() != null) {
        if (GraphicFill.class.isAssignableFrom(symbolizer.getStroke()
            .getGraphicType().getClass())) {
          GraphicFill graphicFill = (GraphicFill) symbolizer.getStroke()
              .getGraphicType();
          List<Graphic> graphicList = graphicFill.getGraphics();
          for (Graphic graphic : graphicList) {
            if (graphic.getExternalGraphics().size() != 0) {
              for (ExternalGraphic external : graphic.getExternalGraphics()) {
                if (external.getFormat().contains("png") || external.getFormat().contains("gif")) { //$NON-NLS-1$ //$NON-NLS-2$
                  Image image = external.getOnlineResource();

                  // FIXME Le contour est représenter dans la légende par les 4
                  // bords du symbole de légende
                  // Ce serait plus propre de créer une polygon de la forme du
                  // contour et de le remplir
                  drawGraphicFillPolygon(image, graphic.getSize(), g2,
                      getLayer().getOpacity(), widthDrawn, symbolizer
                          .getStroke().getStrokeWidth() * 2, symbolizer
                          .getStroke().getStrokeWidth() / 2 - this.margin * 2,
                      0);
                  drawGraphicFillPolygon(image, graphic.getSize(), g2,
                      getLayer().getOpacity(), widthDrawn, symbolizer
                          .getStroke().getStrokeWidth() * 2, rowHeight
                          - this.margin * 2
                          + symbolizer.getStroke().getStrokeWidth() / 2, 0);
                  drawGraphicFillPolygon(image, graphic.getSize(), g2,
                      getLayer().getOpacity(), symbolizer.getStroke()
                          .getStrokeWidth() * 2, rowHeight
                          - symbolizer.getStroke().getStrokeWidth()
                          - this.margin * 2, symbolizer.getStroke()
                          .getStrokeWidth() * 2 - this.margin * 2, -this.margin);
                  drawGraphicFillPolygon(image, graphic.getSize(), g2,
                      getLayer().getOpacity(), symbolizer.getStroke()
                          .getStrokeWidth() * 2, rowHeight
                          - symbolizer.getStroke().getStrokeWidth()
                          - this.margin * 2, symbolizer.getStroke()
                          .getStrokeWidth() * 2 - this.margin * 2, columnsWidth
                          - this.margin);

                } else {
                  if (external.getFormat().contains("svg")) { //$NON-NLS-1$
                    GraphicsNode node = external.getGraphicsNode();

                    // FIXME Idem, il faudrait recréer la forme adéquate
                    drawGraphicFillPolygon(node, graphic.getSize(), g2,
                        getLayer().getOpacity(), widthDrawn, symbolizer
                            .getStroke().getStrokeWidth() * 2,
                        symbolizer.getStroke().getStrokeWidth() / 2
                            - this.margin * 2, 0);
                    drawGraphicFillPolygon(node, graphic.getSize(), g2,
                        getLayer().getOpacity(), widthDrawn, symbolizer
                            .getStroke().getStrokeWidth() * 2, rowHeight
                            - this.margin * 2
                            + symbolizer.getStroke().getStrokeWidth() / 2, 0);
                    drawGraphicFillPolygon(node, graphic.getSize(), g2,
                        getLayer().getOpacity(), symbolizer.getStroke()
                            .getStrokeWidth() * 2, rowHeight
                            - symbolizer.getStroke().getStrokeWidth()
                            - this.margin * 2, symbolizer.getStroke()
                            .getStrokeWidth() * 2 - this.margin * 2,
                        -this.margin);
                    drawGraphicFillPolygon(node, graphic.getSize(), g2,
                        getLayer().getOpacity(), symbolizer.getStroke()
                            .getStrokeWidth() * 2, rowHeight
                            - symbolizer.getStroke().getStrokeWidth()
                            - this.margin * 2, symbolizer.getStroke()
                            .getStrokeWidth() * 2 - this.margin * 2,
                        columnsWidth - this.margin);
                  }
                }
              }
            } else if (graphic.getMarks().size() != 0) {
              int markShapeSize = 200;
              for (Mark mark : graphic.getMarks()) {
                Shape markShape = mark.toShape();
                AffineTransform translate = AffineTransform
                    .getTranslateInstance(markShapeSize / 2, markShapeSize / 2);
                if (graphic.getRotation() != null) {
                  AffineTransform rotate = AffineTransform
                      .getRotateInstance(Math.PI
                          * Double.parseDouble(graphic.getRotation()
                              .evaluate(null).toString()) / 180.0);
                  translate.concatenate(rotate);
                }
                AffineTransform scaleTransform = AffineTransform
                    .getScaleInstance(markShapeSize, markShapeSize);
                translate.concatenate(scaleTransform);
                Shape tranlatedShape = translate
                    .createTransformedShape(markShape);
                BufferedImage buff = new BufferedImage(markShapeSize,
                    markShapeSize, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = (Graphics2D) buff.getGraphics();
                g.setColor(ColorUtil.getColorWithOpacity(mark.getFill()
                    .getColor(), getLayer().getOpacity()));
                g.fill(tranlatedShape);

                // FIXME Idem, il faudrait recréer la forme adéquate
                drawGraphicFillPolygon(buff, graphic.getSize(), g2, getLayer()
                    .getOpacity(), widthDrawn, symbolizer.getStroke()
                    .getStrokeWidth() * 2, symbolizer.getStroke()
                    .getStrokeWidth() / 2 - this.margin * 2, 0);
                drawGraphicFillPolygon(buff, graphic.getSize(), g2, getLayer()
                    .getOpacity(), widthDrawn, symbolizer.getStroke()
                    .getStrokeWidth() * 2, rowHeight - this.margin * 2
                    + symbolizer.getStroke().getStrokeWidth() / 2, 0);
                drawGraphicFillPolygon(buff, graphic.getSize(), g2, getLayer()
                    .getOpacity(), symbolizer.getStroke().getStrokeWidth() * 2,
                    rowHeight - symbolizer.getStroke().getStrokeWidth()
                        - this.margin * 2, symbolizer.getStroke()
                        .getStrokeWidth() * 2 - this.margin * 2, -this.margin);
                drawGraphicFillPolygon(buff, graphic.getSize(), g2, getLayer()
                    .getOpacity(), symbolizer.getStroke().getStrokeWidth() * 2,
                    rowHeight - symbolizer.getStroke().getStrokeWidth()
                        - this.margin * 2, symbolizer.getStroke()
                        .getStrokeWidth() * 2 - this.margin * 2, columnsWidth
                        - this.margin);
              }
            }
          }
        } else if (GraphicStroke.class.isAssignableFrom(symbolizer.getStroke()
            .getGraphicType().getClass())) {
          // TODO Finir l'implémentation pour les GraphicStroke
          g2.setColor(symbolizer.getStroke().getColor());
          g2.setStroke(symbolizer.getStroke().toAwtStroke());
          g2.drawRect(currentColumn * (columnsWidth + this.margin)
              + this.margin, currentRow * (rowHeight + this.margin)
              + this.margin, columnsWidth, rowHeight);
        }
      }
    }
  }

  /**
   * @param symbolizer
   * @param g2
   * @param currentColumn
   * @param currentRow
   * @param columnsWidth
   * @param rowHeight
   */
  private void paintRaster(RasterSymbolizer symbolizer, Graphics2D g2,
      int currentColumn, int currentRow, int columnsWidth, int rowHeight) {
    int x = currentColumn * (columnsWidth + this.margin) + this.margin;
    int y = currentRow * (rowHeight + this.margin) + this.margin;
    int width = columnsWidth;
    int height = rowHeight;
    // FIXME Pour l'instant on considère qu'il n'y a qu'un symbolizer
    for (IFeature feature : this.layer.get().getFeatureCollection()) {
      FT_Coverage coverage = (FT_Coverage) feature;
      BufferedImage image = PlanarImage.wrapRenderedImage(
          coverage.coverage().getRenderedImage()).getAsBufferedImage();
      g2.drawImage(image, x, y, width, height, null);
    }
  }
}
