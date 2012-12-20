/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class ScaleRulerPanel extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private int minBound = 10000;
  private int maxBound = 5000000;
  private int pixelLength;
  private List<Integer> majorTicks = new ArrayList<Integer>();
  private List<String> majorTickLabels = new ArrayList<String>();
  private Font f;
  private int pixelMinorTick;

  public ScaleRulerPanel(int pixelMinorTick) {
    super();
    this.pixelMinorTick = pixelMinorTick;
    setDefaultMajorTicks();
    this.pixelLength = 25 * pixelMinorTick * (majorTicks.size() - 1);
    repaint();
  }

  private void setDefaultMajorTicks() {
    Integer[] array = new Integer[] { 10000, 25000, 50000, 100000, 250000,
        500000, 1000000, 2000000, 5000000 };
    for (Integer majorTick : array) {
      this.majorTicks.add(majorTick);
      this.majorTickLabels.add(this.scaleToLabel(majorTick));
    }
    // add the bounds, if necessary
    if (!this.majorTicks.contains(this.minBound)) {
      this.majorTicks.add(this.minBound);
      this.majorTickLabels.add(this.scaleToLabel(this.minBound));
    }
    if (!this.majorTicks.contains(this.maxBound)) {
      this.majorTicks.add(this.maxBound);
      this.majorTickLabels.add(this.scaleToLabel(this.maxBound));
    }
  }

  private String scaleToLabel(int scale) {
    if (scale < 1000000) {
      return String.valueOf(new Double(scale / 1000).intValue()) + "K";
    }
    return String.valueOf(new Double(scale / 1000000).intValue()) + "M";
  }

  /**
   * This method gives the x pixel located at {@code this} scale ruler tick
   * corresponding to the scale. It allows to draw components to or from this
   * particular tick (i.e. scale).
   * @param scale
   * @return
   */
  public int getPixelAlign(int scale) {
    if (scale < minBound || scale > maxBound)
      return -1;
    // first get the pixel of the left alignment of the ruler
    int initPix = this.getX();
    // then get the position of the nearest major tick
    int major = getPositionNearestMajorTick(scale);
    // get the pixel of this major Tick
    int majorPix = initPix + 25 * pixelMinorTick * major;
    // get the ratio to position scale between the two nearest major Ticks
    double ratio = getDistRatioFromMajorTick(scale);
    int minorPix = (int) Math.round(25 * pixelMinorTick * ratio);

    return majorPix + minorPix;
  }

  @Override
  public void paint(Graphics graphics) {
    super.paint(graphics);
    Graphics2D g = (Graphics2D) graphics;
    g.setFont(this.f);
    int majorInterval = Math.round(this.pixelLength
        / (this.majorTicks.size() - 1));
    int minorInterval = Math.round(majorInterval / 5);
    // draw first major Tick
    g.drawLine(0, 1, 0, 10);
    g.drawString(this.majorTickLabels.get(0), 0, 19);
    int i = 1;
    // draw the tick marks
    for (int x = 1; x < this.pixelLength; x++) {
      if (x % minorInterval == 0) {
        g.drawLine(x, 1, x, 5);
      }
      if (x % majorInterval == 0) {
        g.drawLine(x, 1, x, 10);
        g.drawString(this.majorTickLabels.get(i), x - 4, 19);
        i++;
      }
    }
    // draw last major Tick
    g.drawLine(this.pixelLength - 1, 1, this.pixelLength - 1, 10);
    g.drawString(this.majorTickLabels.get(this.majorTickLabels.size() - 1),
        this.pixelLength - 4, 19);
  }

  public void updateMinBound(int minBound) {
    this.minBound = minBound;
    setDefaultMajorTicks();
    this.pixelLength = 25 * pixelMinorTick * (majorTicks.size() - 1);
    repaint();
  }

  public void updateMaxBound(int maxBound) {
    this.maxBound = maxBound;
    setDefaultMajorTicks();
    this.pixelLength = 25 * pixelMinorTick * (majorTicks.size() - 1);
    repaint();
  }

  /**
   * Get the nearest major tick that is inferior to the given scale.
   * @param scale
   * @return
   */
  private int getPositionNearestMajorTick(int scale) {
    int nearestP = 0;
    for (int i = 1; i < majorTicks.size(); i++) {
      int major = majorTicks.get(i);
      if (major > scale)
        return nearestP;
      nearestP++;
    }
    return nearestP;
  }

  /**
   * Get the nearest major tick that is inferior to the given scale.
   * @param scale
   * @return
   */
  private double getDistRatioFromMajorTick(int scale) {
    for (int i = 1; i < majorTicks.size(); i++) {
      int major = majorTicks.get(i);
      if (major > scale) {
        return new Double(scale - majorTicks.get(i - 1)).doubleValue()
            / new Double(major - majorTicks.get(i - 1)).doubleValue();
      }
    }
    return 0.0;
  }
}
