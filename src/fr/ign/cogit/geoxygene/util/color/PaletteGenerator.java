package fr.ign.cogit.geoxygene.util.color;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_CubicSpline;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public class PaletteGenerator {
    List<Color> colors;

    public PaletteGenerator(List<Color> colors) {
        this.colors = colors;
    }

    public List<Color> getPalette(double distance, int colorSpace) {
        DirectPositionList points = new DirectPositionList();
        for (Color c : this.colors) {
            if (colorSpace == ColorUtil.LAB) {
                float[] lab = ColorUtil.toLab(c);
                DirectPosition p = new DirectPosition(lab[0], lab[1], lab[2]);
                points.add(p);
            } else {
                DirectPosition p = new DirectPosition(c.getRed(), c.getGreen(),
                            c.getBlue());
                points.add(p);
            }
        }
        GM_CubicSpline spline = new GM_CubicSpline(points);
        GM_LineString line = spline.asLineString(distance, 0.0);
        List<Color> palette = new ArrayList<Color>(line.sizeControlPoint());
        for (DirectPosition p : line.getControlPoint()) {
            if (colorSpace == ColorUtil.LAB) {
                Color c = ColorUtil.toColor(new float[] { (float) p.getX(),
                            (float) p.getY(), (float) p.getZ() });
                palette.add(c);
            } else {
                int red = (int) p.getX();
                if (red > 255 || red < 0) {
                    System.out.println("red outside " + red);
                    red = Math.max(0, Math.min(255, red));
                }
                int green = (int) p.getY();
                if (green > 255 || green < 0) {
                    System.out.println("green outside ");
                    green = Math.max(0, Math.min(255, green));
                }
                int blue = (int) p.getZ();
                if (blue > 255 || blue < 0) {
                    System.out.println("blue outside " + blue);
                    blue = Math.max(0, Math.min(255, blue));
                }
                System.out.println("Color " + red+", " + green + ", " + blue);
                palette.add(new Color(red, green, blue));
            }
        }
        return palette;
    }

    public List<Color> getPalette(int nbColors, int colorSpace) {
        DirectPositionList points = new DirectPositionList();
        for (Color c : this.colors) {
            if (colorSpace == ColorUtil.LAB) {
                float[] lab = ColorUtil.toLab(c);
                DirectPosition p = new DirectPosition(lab[0], lab[1], lab[2]);
                points.add(p);
            } else {
                DirectPosition p = new DirectPosition(c.getRed(), c.getGreen(),
                            c.getBlue());
                points.add(p);
            }
        }
        GM_CubicSpline spline = new GM_CubicSpline(points);
        GM_LineString line = spline.asLineString(nbColors);
        List<Color> palette = new ArrayList<Color>(line.sizeControlPoint());
        for (DirectPosition p : line.getControlPoint()) {
            if (colorSpace == ColorUtil.LAB) {
                Color c = ColorUtil.toColor(new float[] { (float) p.getX(),
                            (float) p.getY(), (float) p.getZ() });
                palette.add(c);
            } else {
                int red = (int) p.getX();
                if (red > 255 || red < 0) {
                    System.out.println("red outside " + red);
                    red = Math.max(0, Math.min(255, red));
                }
                int green = (int) p.getY();
                if (green > 255 || green < 0) {
                    System.out.println("green outside ");
                    green = Math.max(0, Math.min(255, green));
                }
                int blue = (int) p.getZ();
                if (blue > 255 || blue < 0) {
                    System.out.println("blue outside " + blue);
                    blue = Math.max(0, Math.min(255, blue));
                }
                System.out.println("Color " + red+", " + green + ", " + blue);
                palette.add(new Color(red, green, blue));
            }
        }
        return palette;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        List<Color> colors = new ArrayList<Color>(3);
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        colors.add(Color.CYAN);
        colors.add(Color.BLUE);
        List<Color> palette = new PaletteGenerator(colors).getPalette(40.0,
                    ColorUtil.LAB);
        for (Color c : palette) {
            System.out.println(c.toString());
        }
        JFileChooser f = new JFileChooser();
        int result = f.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = f.getSelectedFile();
            ColorUtil.writePaletteImage(
                        palette.toArray(new Color[palette.size()]), 100,
                        file.getAbsolutePath(), 1, palette.size());
        }
    }
}
