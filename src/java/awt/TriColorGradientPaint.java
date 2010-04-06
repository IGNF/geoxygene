package java.awt;

import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

public class TriColorGradientPaint implements Paint {

    Point2D p1;
    Color color1;
    Point2D p2;
    Color color2;
    Point2D p3;
    Color color3;
    public TriColorGradientPaint(
            Point2D pt1, Color color1,
            Point2D pt2, Color color2,
            Point2D pt3, Color color3) {
        if ((color1 == null) || (color2 == null) ||
                (pt1 == null) || (pt2 == null)) {
                throw new NullPointerException(
                        "Colors and points should be non-null"); //$NON-NLS-1$
            }
            this.p1 = new Point2D.Float((float)pt1.getX(), (float)pt1.getY());
            this.p2 = new Point2D.Float((float)pt2.getX(), (float)pt2.getY());
            this.p3 = new Point2D.Float((float)pt3.getX(), (float)pt3.getY());
            this.color1 = color1;
            this.color2 = color2;
            this.color3 = color3;
    }
    @Override
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds,
            Rectangle2D userBounds, AffineTransform xform,
            RenderingHints hints) {
        return new TriColorGradientPaintContext(cm, this.p1, this.p2, this.p3, xform,
                this.color1, this.color2, this.color3);
    }

    @Override
    public int getTransparency() {
        int a1 = this.color1.getAlpha();
        int a2 = this.color2.getAlpha();
        int a3 = this.color3.getAlpha();
        return (((a1 & a2 & a3) == 0xff) ? OPAQUE : TRANSLUCENT);
    }
}
