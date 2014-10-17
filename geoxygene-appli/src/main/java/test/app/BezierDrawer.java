/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package test.app;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Point2d;

import fr.ign.cogit.geoxygene.util.math.VectorUtil;

/**
 * @author JeT
 * 
 */
public class BezierDrawer {

    private static JSlider widthSlider = null;
    private static JSlider sampleSlider = null;
    private static JSlider scaleSlider = null;
    private static JCheckBox curveCheckbox = null;
    private static JCheckBox normalCheckbox = null;
    private static JCheckBox controlCheckbox = null;
    private static JCheckBox strokeCheckbox = null;
    private static JCheckBox widthCheckbox = null;
    private static JCheckBox derivativeCheckbox = null;
    private static JCheckBox curvatureCheckbox = null;
    private static JCheckBox falseCurvatureCheckbox = null;

    private static BezierPanel panel = new BezierPanel();
    private static ChangeListener repaint = null;
    static {
        repaint = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                panel.repaint();

            }
        };
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        JFrame f = new JFrame("Bezier");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(1024, 768);

        panel = new BezierPanel();
        panel.addMouseListener(panel);
        panel.addMouseMotionListener(panel);
        f.getContentPane().add(panel, BorderLayout.CENTER);
        JPanel toolsPanel = new JPanel();
        JPanel slidersPanel = new JPanel();
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(BorderFactory
                .createBevelBorder(BevelBorder.LOWERED));
        southPanel.add(toolsPanel, BorderLayout.SOUTH);
        southPanel.add(slidersPanel, BorderLayout.NORTH);
        f.getContentPane().add(southPanel, BorderLayout.SOUTH);

        widthSlider = new JSlider(1, 1000, 100);
        widthSlider.setPaintTicks(true);
        widthSlider.setMinorTickSpacing(10);
        widthSlider.setMajorTickSpacing(100);
        widthSlider.setPaintTicks(true);
        widthSlider.addChangeListener(repaint);
        slidersPanel.add(new JLabel("width"));
        slidersPanel.add(widthSlider);

        sampleSlider = new JSlider(10, 1000, 100);
        sampleSlider.setPaintTicks(true);
        sampleSlider.setMinorTickSpacing(10);
        sampleSlider.setMajorTickSpacing(100);
        sampleSlider.setPaintTicks(true);
        sampleSlider.addChangeListener(repaint);
        slidersPanel.add(new JLabel("samples"));
        slidersPanel.add(sampleSlider);

        scaleSlider = new JSlider(-1000, 1000, 00);
        scaleSlider.setPaintTicks(true);
        scaleSlider.setMinorTickSpacing(10);
        scaleSlider.setMajorTickSpacing(100);
        scaleSlider.setPaintTicks(true);
        scaleSlider.addChangeListener(repaint);
        slidersPanel.add(new JLabel("scale"));
        slidersPanel.add(scaleSlider);

        curveCheckbox = new JCheckBox("curve");
        curveCheckbox.addChangeListener(repaint);
        curveCheckbox.setSelected(true);
        toolsPanel.add(curveCheckbox);

        controlCheckbox = new JCheckBox("control");
        controlCheckbox.addChangeListener(repaint);
        controlCheckbox.setSelected(true);
        toolsPanel.add(controlCheckbox);

        normalCheckbox = new JCheckBox("normal");
        normalCheckbox.addChangeListener(repaint);
        toolsPanel.add(normalCheckbox);

        strokeCheckbox = new JCheckBox("stroke");
        strokeCheckbox.addChangeListener(repaint);
        toolsPanel.add(strokeCheckbox);

        widthCheckbox = new JCheckBox("width");
        widthCheckbox.addChangeListener(repaint);
        toolsPanel.add(widthCheckbox);

        derivativeCheckbox = new JCheckBox("derivative");
        derivativeCheckbox.addChangeListener(repaint);
        toolsPanel.add(derivativeCheckbox);

        curvatureCheckbox = new JCheckBox("curvature");
        curvatureCheckbox.addChangeListener(repaint);
        toolsPanel.add(curvatureCheckbox);

        falseCurvatureCheckbox = new JCheckBox("false curvature");
        falseCurvatureCheckbox.addChangeListener(repaint);
        toolsPanel.add(falseCurvatureCheckbox);

        f.setVisible(true);

    }

    // /////////////////////////////////////////
    // CANVAS
    // /////////////////////////////////////////
    public static class BezierPanel extends JPanel implements
            MouseMotionListener, MouseListener {
        public Point2d[] p = { new Point2d(200, 400), new Point2d(200, 100),
                new Point2d(800, 100), new Point2d(800, 400) };

        private int selectedPointIndex = -1;
        private double lineWidth = 10;

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        @Override
        protected void paintComponent(Graphics g1) {
            Graphics2D g = (Graphics2D) g1;
            this.lineWidth = widthSlider.getValue();
            this.p[2].x = this.p[1].x;
            this.p[2].y = this.p[1].y;
            super.paintComponent(g);
            if (widthCheckbox.isSelected()) {
                g.setColor(Color.blue);
                this.drawWidth(g);
            }
            if (normalCheckbox.isSelected()) {
                g.setColor(Color.green);
                g.setStroke(new BasicStroke(1));
                this.drawNormals(g);
            }
            if (curveCheckbox.isSelected()) {
                g.setColor(Color.red);
                g.setStroke(new BasicStroke(5));
                this.drawCurve(g);
            }
            if (strokeCheckbox.isSelected()) {
                g.setColor(Color.pink);
                g.setStroke(new BasicStroke(1));
                this.drawStroke(g);
            }
            if (controlCheckbox.isSelected()) {
                g.setColor(Color.black);
                g.setStroke(new BasicStroke(1));
                this.drawControlPoints(g);
            }
            if (derivativeCheckbox.isSelected()) {
                g.setColor(Color.yellow);
                g.setStroke(new BasicStroke(1));
                this.drawDerivative(g);
            }
            if (curvatureCheckbox.isSelected()) {
                g.setColor(Color.blue);
                g.setStroke(new BasicStroke(1));
                this.drawCurvature(g);
            }
            if (falseCurvatureCheckbox.isSelected()) {
                g.setColor(Color.cyan);
                g.setStroke(new BasicStroke(1));
                this.drawFalseCurvature(g);
            }
        }

        private void drawControlPoints(Graphics2D g) {
            int l = 2;
            g.drawRect((int) (this.p[0].x - l), (int) (this.p[0].y - l), 2 * l,
                    2 * l);
            g.drawRect((int) (this.p[1].x - l), (int) (this.p[1].y - l), 2 * l,
                    2 * l);
            g.drawRect((int) (this.p[2].x - l), (int) (this.p[2].y - l), 2 * l,
                    2 * l);
            g.drawRect((int) (this.p[3].x - l), (int) (this.p[3].y - l), 2 * l,
                    2 * l);
            g.drawLine((int) this.p[0].x, (int) this.p[0].y, (int) this.p[1].x,
                    (int) this.p[1].y);
            g.drawLine((int) this.p[2].x, (int) this.p[2].y, (int) this.p[3].x,
                    (int) this.p[3].y);
        }

        /**
         * @param g
         */
        private void drawCurve(Graphics2D g) {
            double incT = .1 / sampleSlider.getValue();
            double p0x = this.p[0].x;
            double p0y = this.p[0].y;
            double p1x = this.p[1].x;
            double p1y = this.p[1].y;
            double p2x = this.p[3].x;
            double p2y = this.p[3].y;
            double x0 = p0x;
            double y0 = p0y;
            for (double t = incT; t <= 1 + incT; t += incT) {
                double x1 = BezierPanel.quadraticValue(p0x, p1x, p2x, t);
                double y1 = BezierPanel.quadraticValue(p0y, p1y, p2y, t);
                g.drawLine((int) x0, (int) y0, (int) x1, (int) y1);
                x0 = x1;
                y0 = y1;
            }
        }

        /**
         * @param g
         */
        private void drawDerivative(Graphics2D g) {
            double incT = 1. / sampleSlider.getValue();
            double scale = Math.exp(scaleSlider.getValue() / 100.);
            double p0x = this.p[0].x;
            double p0y = this.p[0].y;
            double p1x = this.p[1].x;
            double p1y = this.p[1].y;
            double p2x = this.p[3].x;
            double p2y = this.p[3].y;
            for (double t = 0; t <= 1; t += incT) {
                double x1 = BezierPanel.quadraticValue(p0x, p1x, p2x, t);
                double y1 = BezierPanel.quadraticValue(p0y, p1y, p2y, t);
                double xp1 = BezierPanel.quadraticDerivative(p0x, p1x, p2x, t);
                double yp1 = BezierPanel.quadraticDerivative(p0y, p1y, p2y, t);
                g.drawLine((int) x1, (int) y1, (int) (x1 + xp1 * scale),
                        (int) (y1 + yp1 * scale));
            }
        }

        /**
         * @param g
         */
        private void drawCurvature(Graphics2D g) {
            double incT = 1. / sampleSlider.getValue();
            double scale = Math.exp(scaleSlider.getValue() / 100.);
            double p0x = this.p[0].x;
            double p0y = this.p[0].y;
            double p1x = this.p[1].x;
            double p1y = this.p[1].y;
            double p2x = this.p[3].x;
            double p2y = this.p[3].y;
            for (double t = 0; t <= 1; t += incT) {
                double x1 = BezierPanel.quadraticValue(p0x, p1x, p2x, t);
                double y1 = BezierPanel.quadraticValue(p0y, p1y, p2y, t);
                double xp1 = BezierPanel.quadraticDerivative(p0x, p1x, p2x, t);
                double yp1 = BezierPanel.quadraticDerivative(p0y, p1y, p2y, t);

                double xpp1 = BezierPanel.quadraticSecondDerivative(p0x, p1x,
                        p2x, t);
                double ypp1 = BezierPanel.quadraticSecondDerivative(p0y, p1y,
                        p2y, t);
                double curvature = Math.abs(xp1 * ypp1 - yp1 * xpp1)
                        / Math.pow(Math.sqrt(xp1 * xp1 + yp1 * yp1), 3);

                double xn = -yp1;
                double yn = xp1;
                double normn = Math.sqrt(xn * xn + yn * yn);
                xn /= normn;
                yn /= normn;

                double r = 1. / curvature;

                double xc = x1 + xn * r;
                double yc = y1 + yn * r;
                if (curvature > 0) {
                    // g.drawOval((int) (x1 - curvature * scale / 2.),
                    // (int) (y1 - curvature * scale / 2.),
                    // (int) (curvature * scale),
                    // (int) (curvature * scale));
                    g.drawOval((int) (xc - r), (int) (yc - r), (int) (2 * r),
                            (int) (2 * r));
                } else {
                    // g.drawRect((int) (x1 + curvature * scale / 2.),
                    // (int) (y1 + curvature * scale / 2.),
                    // (int) (-curvature * scale),
                    // (int) (-curvature * scale));
                    g.drawOval((int) (xc + r), (int) (yc + r), (int) (-2 * r),
                            (int) (-2 * r));
                }
            }
        }

        /**
         * @param g
         */
        private void drawFalseCurvature(Graphics2D g) {
            double incT = 1. / sampleSlider.getValue();
            double scale = Math.exp(scaleSlider.getValue() / 100.);
            double p0x = this.p[0].x;
            double p0y = this.p[0].y;
            double p1x = this.p[1].x;
            double p1y = this.p[1].y;
            double p2x = this.p[3].x;
            double p2y = this.p[3].y;
            for (double t = 0; t <= 1; t += incT) {
                double x1 = BezierPanel.quadraticValue(p0x, p1x, p2x, t);
                double y1 = BezierPanel.quadraticValue(p0y, p1y, p2y, t);
                double xp1 = BezierPanel.quadraticDerivative(p0x, p1x, p2x, t);
                double yp1 = BezierPanel.quadraticDerivative(p0y, p1y, p2y, t);
                double normp1 = Math.sqrt(xp1 * xp1 + yp1 * yp1);
                xp1 /= Math.pow(normp1, 2);
                yp1 /= Math.pow(normp1, 2);
                double nn1 = xp1 * (p2y - p1y) - yp1 * (p2x - p1x);
                double nn2 = xp1 * (p0y - p1y) - yp1 * (p0x - p1x);
                double falseCurvature = nn1;
                if (Math.abs(nn2) < Math.abs(nn1)) {
                    falseCurvature = nn2;
                }
                double curvature = falseCurvature;

                double xn = -yp1;
                double yn = xp1;
                double normn = Math.sqrt(xn * xn + yn * yn);
                xn /= normn;
                yn /= normn;
                double r = 1. / curvature;

                double xc = x1 + xn * r;
                double yc = y1 + yn * r;

                if (curvature > 0) {
                    g.drawOval((int) (x1 - curvature * scale / 2.),
                            (int) (y1 - curvature * scale / 2.),
                            (int) (curvature * scale),
                            (int) (curvature * scale));
                    // g.drawOval((int) (xc - r), (int) (yc - r), (int) (2 * r),
                    // (int) (2 * r));
                } else {
                    g.drawRect((int) (x1 + curvature * scale / 2.),
                            (int) (y1 + curvature * scale / 2.),
                            (int) (-curvature * scale),
                            (int) (-curvature * scale));
                    // g.drawOval((int) (xc + r), (int) (yc + r), (int) (-2 *
                    // r),
                    // (int) (-2 * r));
                }
            }
        }

        /**
         */
        private static double quadraticValue(double p0, double p1, double p2,
                double t) {
            return p0 * (1 - t) * (1 - t) + 2 * p1 * t * (1 - t) + p2 * t * t;
        }

        private static double quadraticDerivative(double p0, double p1,
                double p2, double t) {
            return -2 * p0 * (1 - t) + 2 * p1 * (1 - 2 * t) + 2 * p2 * t;
        }

        private static double quadraticSecondDerivative(double p0, double p1,
                double p2, double t) {
            return 2 * p0 - 4 * p1 + 2 * p2;
        }

        /**
         */
        // private static double cubicValue(double p0, double p1, double p2,
        // double p3, double t) {
        // return p0 * (1 - t) * (1 - t) * (1 - t) + 3 * p1 * t * (1 - t)
        // * (1 - t) + 3 * p2 * t * t * (1 - t) + p3 * t * t * t;
        // }
        //
        // private static double cubicDerivative(double p0, double p1, double
        // p2,
        // double p3, double t) {
        // return -3 * p0 * (1 - t) * (1 - t) + p1
        // * (3 * (1 - t) * (1 - t) - 6 * (1 - t) * t) + p2
        // * (6 * t * (1 - t) - 3 * t * t) + 3 * p3 * t * t;
        // }

        /**
         * @param g
         */
        private void drawStroke(Graphics2D g) {
            Point2d p0 = this.p[0];
            Point2d p1 = this.p[1];
            Point2d p2 = this.p[3];
            Point2d n0 = this.normal(p0, p1);
            Point2d n2 = this.normal(p1, p2);
            double angle = n0.x * n2.y - n0.y * n2.x;
            int sign = angle > 0 ? 1 : -1;
            Point2d p0low = new Point2d(
                    p0.x + sign * n0.y * this.lineWidth / 2, p0.y - sign * n0.x
                            * this.lineWidth / 2);
            Point2d p0high = new Point2d(p0.x - sign * n0.y * this.lineWidth
                    / 2, p0.y + sign * n0.x * this.lineWidth / 2);
            Point2d p2low = new Point2d(
                    p2.x + sign * n2.y * this.lineWidth / 2, p2.y - sign * n2.x
                            * this.lineWidth / 2);
            Point2d p2high = new Point2d(p2.x - sign * n2.y * this.lineWidth
                    / 2, p2.y + sign * n2.x * this.lineWidth / 2);
            Point2d p1low = this.intersection(p0low, n0, p2low, n2);
            Point2d p1high = this.intersection(p0high, n0, p2high, n2);
            // this.drawLine(g, p0, p0low);
            // this.drawLine(g, p0low, p1low);
            // this.drawLine(g, p1low, p2low);
            // this.drawLine(g, p2low, p2);
            // this.drawLine(g, p2, p2high);
            // this.drawLine(g, p2high, p1high);
            // this.drawLine(g, p1high, p0high);
            // this.drawLine(g, p0high, p0);

            Point2d A = p0low;
            Point2d B = new Point2d();
            Point2d C = new Point2d();
            Point2d D = p1low;
            Point2d E = p1high;
            Point2d F = new Point2d();
            Point2d G = p0high;

            double px = quadraticValue(p0.x, p1.x, p2.x, 0.5);
            double py = quadraticValue(p0.y, p1.y, p2.y, 0.5);
            double vx = quadraticDerivative(p0.x, p1.x, p2.x, 0.5);
            double vy = quadraticDerivative(p0.y, p1.y, p2.y, 0.5);
            Point2d normal = new Point2d(vx, vy);
            VectorUtil.normalize(normal, normal);

            Point2d centerHigh = new Point2d(px - sign * normal.y
                    * this.lineWidth / 2, py + sign * normal.x * this.lineWidth
                    / 2);
            Point2d centerLow = new Point2d(px + sign * normal.y
                    * this.lineWidth / 2, py - sign * normal.x * this.lineWidth
                    / 2);
            VectorUtil.lineIntersection(B, centerLow, normal, p0low, n0);
            VectorUtil.lineIntersection(C, centerLow, normal, p2low, n2);

            VectorUtil.copy(A, p0low);
            VectorUtil.copy(D, p2low);
            VectorUtil.copy(E, p2high);
            F.x = centerHigh.x;
            F.y = centerHigh.y;
            VectorUtil.copy(G, p0high);

            this.drawPoint(g, A, "A");
            this.drawPoint(g, B, "B");
            this.drawPoint(g, C, "C");
            this.drawPoint(g, D, "D");
            this.drawPoint(g, E, "E");
            this.drawPoint(g, F, "F");
            this.drawPoint(g, G, "G");
            this.drawLine(g, A, B);
            this.drawLine(g, B, C);
            this.drawLine(g, C, D);
            this.drawLine(g, D, E);
            this.drawLine(g, E, F);
            this.drawLine(g, F, G);
            this.drawLine(g, G, A);

        }

        private void drawLine(Graphics2D g, Point2d p0, Point2d p1) {
            g.drawLine((int) p0.x, (int) p0.y, (int) p1.x, (int) p1.y);
        }

        private void drawPoint(Graphics2D g, Point2d p0, String string) {
            g.drawString(string, (int) p0.x + 5, (int) p0.y + 5);
            g.drawLine((int) p0.x - 3, (int) p0.y, (int) p0.x + 3, (int) p0.y);
            g.drawLine((int) p0.x, (int) p0.y - 3, (int) p0.x, (int) p0.y + 3);
        }

        private Point2d intersection(Point2d p0, Point2d v0, Point2d p1,
                Point2d v1) {
            Point2d inter = new Point2d();
            VectorUtil.lineIntersection(inter, p0, v0, p1, v1);
            return inter;
        }

        /**
         * @param p0
         * @param p1
         * @return
         */
        private Point2d normal(Point2d p0, Point2d p1) {
            Point2d v = new Point2d(p1.x - p0.x, p1.y - p0.y);
            VectorUtil.normalize(v, v);
            return v;
        }

        /**
         * @param g
         */
        private void drawNormals(Graphics2D g) {
            double incT = 1. / sampleSlider.getValue();
            double scale = Math.exp(scaleSlider.getValue() / 100.);
            for (double t = 0; t <= 1; t += incT) {
                double x = BezierPanel.quadraticValue(this.p[0].x, this.p[1].x,
                        this.p[3].x, t);
                double y = BezierPanel.quadraticValue(this.p[0].y, this.p[1].y,
                        this.p[3].y, t);
                double dx = BezierPanel.quadraticDerivative(this.p[0].x,
                        this.p[1].x, this.p[3].x, t);
                double dy = BezierPanel.quadraticDerivative(this.p[0].y,
                        this.p[1].y, this.p[3].y, t);
                this.drawLine(g, x, y, -dy * scale, dx * scale, 1);
            }
        }

        /**
         * @param g
         */
        private void drawWidth(Graphics2D g) {
            double incT = 1. / sampleSlider.getValue();
            for (double t = 0; t <= 1; t += incT) {
                double x = BezierPanel.quadraticValue(this.p[0].x, this.p[1].x,
                        this.p[3].x, t);
                double y = BezierPanel.quadraticValue(this.p[0].y, this.p[1].y,
                        this.p[3].y, t);
                double dx = BezierPanel.quadraticDerivative(this.p[0].x,
                        this.p[1].x, this.p[3].x, t);
                double dy = BezierPanel.quadraticDerivative(this.p[0].y,
                        this.p[1].y, this.p[3].y, t);
                double norm = Math.sqrt(dx * dx + dy * dy);
                dx = dx / norm;
                dy = dy / norm;
                double size = 1;
                double d = 0.001;
                if ((t > 0.25 - d && t < 0.25 + d)
                        || (t > 0.5 - d && t < 0.5 + d)
                        || (t > 0.75 - d && t < 0.75 + d)) {
                    size = 2;
                }

                this.drawLine(g, x, y, -dy, dx, this.lineWidth * size);
            }
        }

        private void drawLine(Graphics2D g, double x, double y, double dx,
                double dy, double l) {
            g.drawLine((int) (x - l / 2 * dx), (int) (y - l / 2 * dy),
                    (int) (x + l / 2 * dx), (int) (y + l / 2 * dy));
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            this.selectPoint(e.getX(), e.getY());
        }

        /**
         * @param e
         */
        private void selectPoint(double x, double y) {
            double dThreshold = 20;
            double dMin = Double.POSITIVE_INFINITY;
            this.selectedPointIndex = -1;
            for (int n = 0; n < 4; n++) {
                double d = (x - this.p[n].x) * (x - this.p[n].x)
                        + (y - this.p[n].y) * (y - this.p[n].y);
                if (d <= dThreshold && d < dMin) {
                    dMin = d;
                    this.selectedPointIndex = n;
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            this.selectedPointIndex = -1;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (this.selectedPointIndex == -1) {
                this.selectPoint(e.getX(), e.getY());
            }
            if (this.selectedPointIndex != -1) {
                this.p[this.selectedPointIndex] = new Point2d(e.getX(),
                        e.getY());
            }
            this.repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

    }
}
