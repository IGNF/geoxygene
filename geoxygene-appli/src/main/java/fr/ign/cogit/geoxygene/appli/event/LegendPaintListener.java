package fr.ign.cogit.geoxygene.appli.event;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.filter.expression.Expression;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LegendGraphic;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.UserStyle;

public class LegendPaintListener implements PaintListener {
  protected static Logger LOGGER = Logger.getLogger(LegendPaintListener.class
      .getName());

  @Override
  public void paint(final LayerViewPanel layerViewPanel, Graphics graphics) {
    List<List<Rule>> rulesWithLegendGraphics = new ArrayList<List<Rule>>(0);
    List<List<Style>> stylesWithLegendGraphics = new ArrayList<List<Style>>(0);
    synchronized (layerViewPanel.getProjectFrame().getSld().getLayers()) {
      for (Layer layer : layerViewPanel.getProjectFrame().getSld().getLayers()) {
        List<Rule> layerRulesWithLegendGraphics = new ArrayList<Rule>(0);
        List<Style> layerStylesWithLegendGraphics = new ArrayList<Style>(0);
        for (Style style : layer.getStyles()) {
          for (FeatureTypeStyle fts : style.getFeatureTypeStyles()) {
            for (Rule rule : fts.getRules()) {
              LegendGraphic legend = rule.getLegendGraphic();
              if (legend != null) {
                layerRulesWithLegendGraphics.add(rule);
                layerStylesWithLegendGraphics.add(style);
              }
            }
          }
        }
        rulesWithLegendGraphics.add(layerRulesWithLegendGraphics);
        stylesWithLegendGraphics.add(layerStylesWithLegendGraphics);
      }
    }
    int shift = 10;
    int maxLineWidth = Integer.MIN_VALUE;
    int maxHeight = 0;
    for (int i = 0; i < rulesWithLegendGraphics.size(); i++) {
      Layer layer = layerViewPanel.getProjectFrame().getSld().getLayers()
          .get(i);
      List<Rule> rules = rulesWithLegendGraphics.get(i);
      List<Style> styles = stylesWithLegendGraphics.get(i);
      if (rules.size() == 1) {
        int maxGraphicHeight = Integer.MIN_VALUE;
        int maxGraphicWidth = Integer.MIN_VALUE;
        for (Rule rule : rules) {
          maxGraphicWidth = Math.max(maxGraphicWidth, (int) rule
              .getLegendGraphic().getGraphic().getWidth());
          maxGraphicHeight = Math.max(maxGraphicHeight, (int) rule
              .getLegendGraphic().getGraphic().getSize());
        }
        String title = layer.getName();
        if (styles.get(0) instanceof UserStyle) {
          String styleTitle = ((UserStyle) styles.get(0)).getTitle();
          if (styleTitle != null) {
            title = styleTitle;
          }
        }
        maxLineWidth = Math.max(maxLineWidth, maxGraphicWidth + shift
            + graphics.getFontMetrics().stringWidth(title));
        maxHeight += Math.max(maxGraphicHeight, graphics.getFontMetrics()
            .getHeight());
      } else {
        if (rules.size() > 1) {
          String title = layer.getName();
          if (styles.get(0) instanceof UserStyle) {
            String styleTitle = ((UserStyle) styles.get(0)).getTitle();
            if (styleTitle != null) {
              title = styleTitle;
            }
          }
          maxLineWidth = Math.max(maxLineWidth, graphics.getFontMetrics()
              .stringWidth(title));
          maxHeight += graphics.getFontMetrics().getHeight();
          for (Rule rule : rules) {
            if (graphics.getFontMetrics() != null && rule.getTitle() != null) {
              maxLineWidth = Math.max(maxLineWidth, graphics.getFontMetrics()
                  .stringWidth(rule.getTitle())
                  + 2
                  * shift
                  + (int) rule.getLegendGraphic().getGraphic().getWidth());
              maxHeight += Math.max((int) rule.getLegendGraphic().getGraphic()
                  .getSize(), graphics.getFontMetrics().getHeight());
            }

          }
        }
      }
    }
    if (maxLineWidth <= 0) {
      return;
    }
    int textBaseLine = layerViewPanel.getWidth() - maxLineWidth - 2 * shift;
    graphics.setColor(Color.WHITE);
    graphics.fillRect(textBaseLine - shift, shift, maxLineWidth + 2 * shift,
        maxHeight + 2 * shift);
    graphics.setColor(Color.BLACK);
    graphics.drawRect(textBaseLine - shift, shift, maxLineWidth + 2 * shift,
        maxHeight + 2 * shift);
    int textCurrentLine = 2 * shift + graphics.getFontMetrics().getHeight();
    for (int i = 0; i < rulesWithLegendGraphics.size(); i++) {
      Layer layer = layerViewPanel.getProjectFrame().getSld().getLayers()
          .get(i);
      List<Rule> list = rulesWithLegendGraphics.get(i);
      List<Style> styles = stylesWithLegendGraphics.get(i);
      if (list.size() == 1) {
        int maxGraphicHeight = Integer.MIN_VALUE;
        int maxGraphicWidth = Integer.MIN_VALUE;
        for (Rule rule : list) {
          maxGraphicWidth = Math.max(maxGraphicWidth, (int) rule
              .getLegendGraphic().getGraphic().getWidth());
          maxGraphicHeight = Math.max(maxGraphicHeight, (int) rule
              .getLegendGraphic().getGraphic().getSize());
        }
        for (Rule rule : list) {
          this.paint(rule.getLegendGraphic().getGraphic(),
              (Graphics2D) graphics, textBaseLine + maxGraphicWidth / 2,
              textCurrentLine - maxGraphicHeight / 2);
        }
        String title = layer.getName();
        if (styles.get(0) instanceof UserStyle) {
          String styleTitle = ((UserStyle) styles.get(0)).getTitle();
          if (styleTitle != null) {
            title = styleTitle;
          }
        }
        graphics.drawString(title, textBaseLine + shift + maxGraphicWidth,
            textCurrentLine);
        textCurrentLine += Math.max(maxGraphicHeight, graphics.getFontMetrics()
            .getHeight());
      } else {
        if (list.size() > 1) {
          String title = layer.getName();
          if (styles.get(0) instanceof UserStyle) {
            String styleTitle = ((UserStyle) styles.get(0)).getTitle();
            if (styleTitle != null) {
              title = styleTitle;
            }
          }
          graphics.drawString(title, textBaseLine, textCurrentLine);
          textCurrentLine += graphics.getFontMetrics().getHeight();
          for (Rule rule : list) {
            this.paint(rule.getLegendGraphic().getGraphic(),
                (Graphics2D) graphics, textBaseLine + shift
                    + (int) rule.getLegendGraphic().getGraphic().getSize() / 2,
                textCurrentLine
                    - (int) rule.getLegendGraphic().getGraphic().getSize() / 2);
            if(rule.getTitle() != null){
                graphics.drawString(rule.getTitle(), textBaseLine + shift + shift
                + (int) rule.getLegendGraphic().getGraphic().getSize(),
                textCurrentLine);
            }
            textCurrentLine += Math.max((int) rule.getLegendGraphic()
                .getGraphic().getSize(), graphics.getFontMetrics().getHeight());
          }
        }
      }
    }
  }

  private void paint(Graphic graphic, Graphics2D g2, int x, int y) {
    Color color = g2.getColor();
    for (Mark mark : graphic.getMarks()) {
      Shape markShape = mark.toShape();
      float size = graphic.getSize();
      AffineTransform at = AffineTransform.getTranslateInstance(x, y);
      Expression rotation = graphic.getRotation();
      if (rotation instanceof Literal) {
        // if this is a literal, we can evaluate it
        at.rotate(-Double.parseDouble(rotation.evaluate(null).toString())
            * Math.PI / 180.0);
      }
      at.scale(size, size);
      markShape = at.createTransformedShape(markShape);
      if (mark.getFill() != null) {
        g2.setColor(mark.getFill().getColor());
        g2.fill(markShape);
      }
      if (mark.getStroke() != null) {
        g2.setStroke(mark.getStroke().toAwtStroke(1.0f));
        g2.setColor(mark.getStroke().getColor());
        g2.draw(markShape);
      }
    }
    for (ExternalGraphic theGraphic : graphic.getExternalGraphics()) {
      Image onlineImage = theGraphic.getOnlineResource();
      if (onlineImage != null) {
        g2.drawImage(onlineImage, x - onlineImage.getWidth(null) / 2, y
            - onlineImage.getHeight(null) / 2, null);
      } else {
        LOGGER.error("null online image " + theGraphic.getHref());
      }
    }
    if (graphic.getMarks().isEmpty() && graphic.getExternalGraphics().isEmpty()) {
      int size = (int) graphic.getSize();
      g2.setColor(Color.GRAY);
      g2.fillRect(x - size / 2, y - size / 2, size, size);
      g2.setColor(Color.BLACK);
      g2.drawRect(x - size / 2, y - size / 2, size, size);
    }
    // revert to the initial color
    g2.setColor(color);
  }
}
