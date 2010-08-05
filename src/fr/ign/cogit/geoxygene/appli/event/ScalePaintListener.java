package fr.ign.cogit.geoxygene.appli.event;

import java.awt.Color;
import java.awt.Graphics;

import fr.ign.cogit.geoxygene.appli.LayerViewPanel;

public class ScalePaintListener implements PaintListener {

    @Override
    public void paint(final LayerViewPanel layerViewPanel, Graphics graphics) {
        int shift = 10;
        int rightShift = 80;
        int barWidth = 5;
        double dist = (layerViewPanel.getWidth() / 3) / layerViewPanel.getViewport().getScale();
        int log = (int) Math.log10(dist);
        dist = Math.pow(10, log);
        int barLength = (int) (dist * layerViewPanel.getViewport().getScale());
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, layerViewPanel.getHeight() - 3 * shift - barWidth, barLength + 4 * shift, barWidth + 3 * shift);
        graphics.fillRect(layerViewPanel.getWidth() - rightShift, layerViewPanel.getHeight() - 3 * shift, rightShift, 3 * shift);
        graphics.setColor(Color.BLACK);
        graphics.drawRect(0, layerViewPanel.getHeight() - 3 * shift - barWidth, barLength + 4 * shift, barWidth + 3 * shift - 1);
        graphics.drawRect(layerViewPanel.getWidth() - rightShift, layerViewPanel.getHeight() - 3 * shift, rightShift - 1, 3 * shift -1);
        graphics.drawString(Double.toString(dist) + " m", shift + 1, layerViewPanel.getHeight() - shift - barWidth - 1); //$NON-NLS-1$
        graphics.fillRect(shift, layerViewPanel.getHeight() - shift - barWidth, barLength, barWidth);
        int scale = (int) (1.0d / (layerViewPanel.getViewport().getScale() * LayerViewPanel.getMETERS_PER_PIXEL()));
        graphics.drawString("1:" + Integer.toString(scale), layerViewPanel.getWidth() - rightShift + shift, layerViewPanel.getHeight() - shift); //$NON-NLS-1$
    }

}
