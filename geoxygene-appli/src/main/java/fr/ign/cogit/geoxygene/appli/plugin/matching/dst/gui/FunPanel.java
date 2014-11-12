package fr.ign.cogit.geoxygene.appli.plugin.matching.dst.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import fr.ign.cogit.geoxygene.function.Function1D;

/**
 * 
 * 
 *
 */
public class FunPanel extends JPanel {
  
  private final static int marge = 10;
  private double borneMax = 0;
  
  /** . */
  protected Function1D[] fs;
  
  /** Default serial ID. */
  private static final long serialVersionUID = 1L;
  
  /**
   * Constructor.
   * @param fs
   */
  public FunPanel(Function1D[] fs) {
    this.fs = fs;
    setPreferredSize(new java.awt.Dimension(100, 50));
  }
  
   
  /**
   * Affichage des fonctions.
   */
  @Override 
  protected void paintComponent(Graphics g2) { 
    
    Graphics2D g = (Graphics2D) g2; 
    g.setFont(new Font("TimesRoman", Font.PLAIN, 10));

    int h = this.getHeight(); 
    int w = this.getWidth(); 

    g.setColor(Color.LIGHT_GRAY); 
    g.fillRect(marge, marge, w - 2 * marge, h - 2 * marge); 
    
    // Tracé des axes  
    g.setColor(Color.BLACK); 
    ((Graphics2D) g).setStroke(new BasicStroke(1f,
        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g.drawLine(marge, h - marge, w, h - marge);
    g.drawLine(marge, h - marge, marge, 0);
    
    // milieu
    g.setColor(Color.GRAY); 
    ((Graphics2D) g).setStroke(new BasicStroke(1f,
        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g.drawLine(marge, h /2, w, h /2);
    
    initBorneMax();
    g.drawString(Integer.toString((int)0), marge, this.getHeight() - 1);
    g.drawString(Integer.toString((int)borneMax), this.getWidth() - 3 * marge, this.getHeight() - 1);
    g.drawString(Integer.toString((int)1), 1, marge);
    g.drawString(Integer.toString((int)0), 1, h - marge);
    
    // On dessine morceaux par morceaux
    if (fs != null) {
      for (int i = 0; i < fs.length; i++) {
        Function1D f = fs[i];
        drawF(g, f);
      }
    } 
    
  }
  
  private void initBorneMax() {
    if (fs != null) {
      for (int i = 0; i < fs.length; i++) {
        Function1D f = fs[i];
        double upper = f.getUpperBoundDF();
        if (upper > borneMax) {
          borneMax = upper;
        }
      }
    }
  }
  
  
  /**
   * Dessine la fonction f, morceaux par morceaux.
   * 
   * @param g
   * @param f
   */
  private void drawF(Graphics2D g, Function1D f) {
    
    try {
      
      // Start at (borneInf, borneSup) in (x, y) coordinate system.
      double lower = f.getLowerBoundDF();
      double upper = f.getUpperBoundDF();

      int h = this.getHeight() - 2 * marge;
      int w = this.getWidth() - 2 * marge;
      double x0 = lower;
      double y0 = f.evaluate(x0);
      
      double x = x0, y = y0;
      double pas = (upper - lower) / 10;
      for (int i = 0; i <= 10; i++ ) {
        
        if (i > 0) {
          g.setColor(Color.RED);
          ((Graphics2D) g).setStroke(new BasicStroke(2f,
              BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
          // g.draw(new Line2D.Double(marge + x*80, h - marge - y*80, marge + x0*80, h - marge - y0*80));
          g.draw(new Line2D.Double(marge + w / borneMax * x, h * (1 - y) + marge, 
              marge + w / borneMax * x0, h * (1 - y0) + marge));
        }
        
        // Avance 1 pas
        x0 = x;
        y0 = y;
        
        // Increment x and y.
        x = x + pas;
        y = f.evaluate(x);  // apply equation.
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}
