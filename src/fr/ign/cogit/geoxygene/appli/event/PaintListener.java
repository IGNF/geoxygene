package fr.ign.cogit.geoxygene.appli.event;

import java.awt.Graphics;
import java.util.EventListener;

import fr.ign.cogit.geoxygene.appli.LayerViewPanel;

public interface PaintListener  extends EventListener {
    public void paint(final LayerViewPanel layerViewPanel, final Graphics graphics);
}
