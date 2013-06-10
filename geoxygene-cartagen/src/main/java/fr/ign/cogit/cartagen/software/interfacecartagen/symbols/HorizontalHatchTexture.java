package fr.ign.cogit.cartagen.software.interfacecartagen.symbols;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

public class HorizontalHatchTexture extends BufferedImage {

  private TexturePaint texture;
  private Polygon pol;

  /**
   * 
   * @param pol the polygon in which hatches are drawn
   * @param offset the offset between hatches
   * @param bias the bias at the start of the hatches
   * @param color
   * @param thickness
   */
  public HorizontalHatchTexture(Polygon pol, int offset, int bias, Color color,
      int thickness) {
    super((int) pol.getBounds().getWidth(), (int) pol.getBounds().getHeight(),
        TYPE_INT_ARGB);
    texture = new TexturePaint(this, pol.getBounds2D());
    this.pol = pol;
    // Dessin des hachures
    Graphics2D g = createGraphics();
    g.setColor(color);
    g.setBackground(new Color(0, 0, 0, 0));
    for (int i = 0; i < pol.getBounds2D().getHeight(); i += offset)
      for (int j = 0; j < thickness; j++)
        g.drawLine(0, i + j, (int) pol.getBounds2D().getWidth(), i + j + bias);
  }

  public void apply(Graphics2D g2d) {
    g2d.setPaint(texture);
    g2d.fillPolygon(pol);
  }
}
