package fr.ign.cogit.geoxygene.appli.plugin.lenses;

import java.awt.Rectangle;

public interface ILensZoneShape {

  public int distance(double x, double y);

  public void setCenter(double x, double y);

  public Rectangle getBounds();

  public boolean contains(double x, double y);

}
