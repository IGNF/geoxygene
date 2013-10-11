package fr.ign.cogit.geoxygene.appli.event;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;

public class CompassPaintListener implements PaintListener {

  static private ImageIcon compass = new ImageIcon(CompassPaintListener.class.getResource("/images/compass_rose.png")); //$NON-NLS-1$
  static private BufferedImage compassBuf = null;
  static {
    compassBuf = new BufferedImage(compass.getIconWidth(), compass.getIconHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics g = compassBuf.createGraphics();
    // paint the Icon to the BufferedImage.
    compass.paintIcon(null, g, 0, 0);
    g.dispose();
  }

  @Override
  public void paint(LayerViewPanel layerViewPanel, Graphics graphics) {
    int shift = 10;
    int size = 100;
    Font initialFont = graphics.getFont();
    Font font = Font.decode("arial-bold-20"); //$NON-NLS-1$
    graphics.setFont(font);
    int charWidth = graphics.getFontMetrics().charWidth('N');
    // int lineHeight = graphics.getFontMetrics().getHeight();
    graphics.drawImage(CompassPaintListener.compass.getImage(), layerViewPanel.getWidth() - size - shift, layerViewPanel.getHeight() - shift - size,// shift
                                                                                                                                                    // +
                                                                                                                                                    // lineHeight,
        size, size, null);
    AffineTransform at = new AffineTransform(1d, 0d, 0d, 1d, layerViewPanel.getWidth() - (size + charWidth) / 2 - shift, layerViewPanel.getHeight()
        - shift - size);// shift + lineHeight);
    FontRenderContext frc = new FontRenderContext(at, true, true);
    GlyphVector g = font.createGlyphVector(frc, "N"); //$NON-NLS-1$
    Shape shape = g.getOutline();
    shape = at.createTransformedShape(shape);
    Graphics2D g2d = (Graphics2D) graphics;
    graphics.setColor(Color.WHITE);
    g2d.setStroke(new BasicStroke(2));
    g2d.draw(shape);
    // g2d.drawGlyphVector(g, layerViewPanel.getWidth() - (size + charWidth) / 2
    // - shift, shift + lineHeight);
    graphics.setColor(new Color(0, 36, 125));
    g2d.fill(shape);
    // g2d.drawGlyphVector(g, layerViewPanel.getWidth() - (size + charWidth) / 2
    // - shift, shift + lineHeight);
    //graphics.drawString("N", layerViewPanel.getWidth() - (size + charWidth) / 2 - shift, shift + lineHeight); //$NON-NLS-1$
    graphics.setFont(initialFont);
  }

}
