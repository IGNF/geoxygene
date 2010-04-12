package fr.ign.cogit.geoxygene.style;

import java.awt.Color;

import javax.xml.bind.annotation.XmlElement;

public class ColorMap {
    Interpolate interpolate = null;

    @XmlElement(name = "Interpolate")
    public Interpolate getInterpolate() {
        return this.interpolate;
    }

    public void setInterpolate(Interpolate interpolate) {
        this.interpolate = interpolate;
    }

    public int getColor(double value) {
        if (this.interpolate != null) {
            InterpolationPoint previous = null;
            for (InterpolationPoint point : this.interpolate.
                    getInterpolationPoint()) {
                if (value <= point.getData()) {
                    if (previous == null) {
                        return point.getValue().getRGB();
                    }
                    return interpolateColor(value, previous.getData(), previous.getValue(), point.getData(), point.getValue()).getRGB();
                }
                previous = point;
            }
        }
        return 0;
    }

    private Color interpolateColor(double value, double data1, Color color1,
            double data2, Color color2) {
        double r1 = color1.getRed();
        double g1 = color1.getGreen();
        double b1 = color1.getBlue();
        double r2 = color2.getRed();
        double g2 = color2.getGreen();
        double b2 = color2.getBlue();
        return new Color(
                (float) interpolate(value, data1, r1, data2, r2) / 255f,
                (float) interpolate(value, data1, g1, data2, g2) / 255f,
                (float) interpolate(value, data1, b1, data2, b2) / 255f);
    }

    private double interpolate(double value, double data1, double value1,
            double data2, double value2) {
        return value1 + (value - data1) * (value2 - value1) / (data2 - data1);
    }
}
