package fr.ign.cogit.geoxygene.style.gradient;

import java.awt.Color;
import java.awt.PaintContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.ref.WeakReference;

public class TriColorGradientPaintContext implements PaintContext {
    static ColorModel xrgbmodel =
        new DirectColorModel(24, 0x00ff0000, 0x0000ff00, 0x000000ff);
    static ColorModel xbgrmodel =
        new DirectColorModel(24, 0x000000ff, 0x0000ff00, 0x00ff0000);
    static ColorModel cachedModel;
    static WeakReference<Raster> cached;
    static synchronized Raster getCachedRaster(ColorModel cm, int w, int h) {
        if (cm == cachedModel) {
            if (cached != null) {
                Raster ras = cached.get();
                if (ras != null &&
                        ras.getWidth() >= w &&
                        ras.getHeight() >= h)
                {
                    cached = null;
                    return ras;
                }
            }
        }
        return cm.createCompatibleWritableRaster(w, h);
    }
    static synchronized void putCachedRaster(ColorModel cm, Raster ras) {
        if (cached != null) {
            Raster cras = cached.get();
            if (cras != null) {
                int cw = cras.getWidth();
                int ch = cras.getHeight();
                int iw = ras.getWidth();
                int ih = ras.getHeight();
                if (cw >= iw && ch >= ih) {
                    return;
                }
                if (cw * ch >= iw * ih) {
                    return;
                }
            }
        }
        cachedModel = cm;
        cached = new WeakReference<Raster>(ras);
    }
    double x1;
    double y1;
    double x2;
    double y2;
    double x3;
    double y3;
    double hx1;
    double hy1;
    double hx2;
    double hy2;
    double hx3;
    double hy3;
    double dhx1;
    double dhy1;
    double dhx2;
    double dhy2;
    double dhx3;
    double dhy3;
    int r1;
    int r2;
    int r3;
    int g1;
    int g2;
    int g3;
    int b1;
    int b2;
    int b3;

    double dx;
    double dy;
    int interp[];
    Raster saved;
    ColorModel model;

    public TriColorGradientPaintContext(ColorModel cm, Point2D p1, Point2D p2,
            Point2D p3, AffineTransform xform, Color color1, Color color2,
            Color color3) {
        Point2D xvec = new Point2D.Double(1, 0);
        Point2D yvec = new Point2D.Double(0, 1);
        try {
            AffineTransform inverse = xform.createInverse();
            inverse.deltaTransform(xvec, xvec);
            inverse.deltaTransform(yvec, yvec);
        } catch (NoninvertibleTransformException e) {
            xvec.setLocation(0, 0);
            yvec.setLocation(0, 0);
        }
        Point2D dp1 = xform.transform(p1, null);
        Point2D dp2 = xform.transform(p2, null);
        Point2D dp3 = xform.transform(p3, null);

        Point2D h1 = projection(dp1, dp2, dp3);
        Point2D h2 = projection(dp2, dp1, dp3);
        Point2D h3 = projection(dp3, dp1, dp2);
        this.hx1 = h1.getX();
        this.hy1 = h1.getY();
        this.hx2 = h2.getX();
        this.hy2 = h2.getY();
        this.hx3 = h3.getX();
        this.hy3 = h3.getY();

        this.x1 = dp1.getX();
        this.y1 = dp1.getY();
        this.x2 = dp2.getX();
        this.y2 = dp2.getY();
        this.x3 = dp3.getX();
        this.y3 = dp3.getY();

        this.dhx1 = this.hx1 - this.x1;
        this.dhy1 = this.hy1 - this.y1;
        this.dhx2 = this.hx2 - this.x2;
        this.dhy2 = this.hy2 - this.y2;
        this.dhx3 = this.hx3 - this.x3;
        this.dhy3 = this.hy3 - this.y3;

        this.r1 = color1.getRed();
        this.r2 = color2.getRed();
        this.r3 = color3.getRed();
        this.g1 = color1.getGreen();
        this.g2 = color2.getGreen();
        this.g3 = color3.getGreen();
        this.b1 = color1.getBlue();
        this.b2 = color2.getBlue();
        this.b3 = color3.getBlue();
        this.model = ColorModel.getRGBdefault();
    }
    private Point2D projection(Point2D p1, Point2D p2, Point2D p3) {
        double p2p3x = p3.getX() - p2.getX();
        double p2p3y = p3.getY() - p2.getY();
        double length = Math.sqrt(p2p3x * p2p3x + p2p3y * p2p3y );
        if (length == 0) return p2;
        p2p3x /= length;
        p2p3y /= length;
        double p2p1x = p1.getX() - p2.getX();
        double p2p1y = p1.getY() - p2.getY();
        double lambda = p2p3x * p2p1x + p2p3y * p2p1y;
        if (lambda <= 0) return p2;
        if (lambda >= length) return p3;
        return new Point2D.Double(p2.getX() + lambda * p2p3x, p2.getY() + lambda * p2p3y);
    }
    @Override
    public void dispose() {
        if (this.saved != null) {
            putCachedRaster(this.model, this.saved);
            this.saved = null;
        }
    }
    @Override
    public ColorModel getColorModel() {
        return this.model;
    }
    @Override
    public Raster getRaster(int x, int y, int w, int h) {
        Raster rast = this.saved;
        if (rast == null || rast.getWidth() < w || rast.getHeight() < h) {
            rast = getCachedRaster(this.model, w, h);
            this.saved = rast;
        }
        int[] pixels = new int[w * h * 4];
        clipFillRaster(pixels, x, y, w, h);
        WritableRaster r = (WritableRaster) rast;
        r.setPixels(0, 0, w, h, pixels);
        return rast;
    }
    void clipFillRaster(int[] pixels,
            int x, int y, int w, int h) {
        int off = 0;
        int minx = x;
        int maxx = x + w;
        int currenty = y;
        int maxy = y + h;
        do {
            int currentx = minx;
            do {
                double dx1 = currentx - x1;
                double dy1 = currenty - y1;
                double a1 = 1 - (dx1 * dhx1 + dy1 * dhy1) / (dhx1* dhx1 + dhy1 * dhy1);
                double dx2 = currentx - x2;
                double dy2 = currenty - y2;
                double a2 = 1 - (dx2 * dhx2 + dy2 * dhy2) / (dhx2* dhx2 + dhy2 * dhy2);
                double dx3 = currentx - x3;
                double dy3 = currenty - y3;
                double a3 = 1 - (dx3 * dhx3 + dy3 * dhy3) / (dhx3* dhx3 + dhy3 * dhy3);
                double a = a1 + a2 + a3;
                pixels[off++] = (int) ((a1 * this.r1 + a2 * this.r2 + a3 * this.r3) / a);
                pixels[off++] = (int) ((a1 * this.g1 + a2 * this.g2 + a3 * this.g3) / a);
                pixels[off++] = (int) ((a1 * this.b1 + a2 * this.b2 + a3 * this.b3) / a);
                pixels[off++] = 255;
            } while (++currentx < maxx);
        } while (++currenty < maxy);
    }
}
