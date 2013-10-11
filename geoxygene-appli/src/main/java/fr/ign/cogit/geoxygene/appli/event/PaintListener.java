package fr.ign.cogit.geoxygene.appli.event;

import java.awt.Graphics;
import java.util.EventListener;

import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;

public interface PaintListener extends EventListener {
  /**
   * Paint overlays in an AWT window
   * @param layerViewPanel layer window to draw into
   * @param graphics associated AWT graphics
   */
  public void paint(final LayerViewPanel layerViewPanel, final Graphics graphics);

}
