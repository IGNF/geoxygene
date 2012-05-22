package fr.ign.cogit.appli.geopensim.appli.tracking;

import java.awt.Graphics;

import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.geoxygene.appli.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.appli.event.LegendPaintListener;
import fr.ign.cogit.geoxygene.appli.event.PaintListener;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * @author julien Gaffuri
 * 
 */
public class PanelVisuInfoAgent extends LayerViewPanel {
  private static final long serialVersionUID = -5037001262393524360L;
  private ElementRepresentation etatAAfficher = null;
  public void setEtatAAfficher(ElementRepresentation etatAAfficher) {
    this.etatAAfficher = etatAAfficher;
  }
  public ElementRepresentation getEtatAAfficher() {
    return this.etatAAfficher;
  }
  public PanelVisuInfoAgent(MainFrame mainFrame) {
    super(mainFrame.getSelectedProjectFrame());
    for (Layer layer : mainFrame.getSelectedProjectFrame().getLayers()) {
      this.getRenderingManager().addLayer(layer);
    }
  }
  @Override
  public void paintOverlays(final Graphics graphics) {
    for (PaintListener listener : this.overlayListeners) {
      if (!(listener instanceof LegendPaintListener)) {
        listener.paint(this, graphics);
      }
    }
  }
}
