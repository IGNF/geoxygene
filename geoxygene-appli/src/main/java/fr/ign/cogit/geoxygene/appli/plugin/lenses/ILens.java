package fr.ign.cogit.geoxygene.appli.plugin.lenses;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public interface ILens {

  public void setVisuPanel(JComponent visuPanel);

  public JComponent getVisuPanel();

  public void apply(Graphics2D g2d, BufferedImage offscreen);

}
