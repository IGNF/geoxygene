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

package fr.ign.cogit.geoxygene.util.math;

import java.awt.geom.Point2D;

import javax.vecmath.Point2d;

/**
 * @author JeT Utility class dealing with vectors
 */
public final class VectorUtil {

    /**
     * private constructor for utility class
     */
    private VectorUtil() {
        // utility class
    }

    public static double length(Point2D p) {
        return Math.sqrt(p.getX() * p.getX() + p.getY() * p.getY());
    }

    public static double distance(Point2D p1, Point2D p2) {
        return Math.sqrt((p2.getX() - p1.getX()) * (p2.getX() - p1.getX())
                + (p2.getY() - p1.getY()) * (p2.getY() - p1.getY()));
    }

    public static Point2D mulDoublePoint2D(double d, Point2D p) {
        return new Point2D.Double(d * p.getX(), d * p.getY());
    }

    public static Point2D opposite(Point2D p) {
        return new Point2D.Double(-p.getX(), -p.getY());
    }

    public static Point2D mulPoint2D(Point2D p1, Point2D p2) {
        return new Point2D.Double(p1.getX() * p2.getX(), p1.getY() * p2.getY());
    }

    public static Point2D addPoint2D(Point2D p1, Point2D p2) {
        return new Point2D.Double(p1.getX() + p2.getX(), p1.getY() + p2.getY());
    }

    public static Point2D rotatePoint2D(Point2D center, Point2D p, double angle) {
        return addPoint2D(center, rotateVector(vector(center, p), angle));
    }

    public static Point2D rotateVector(Point2D v, double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new Point2D.Double(v.getX() * c - v.getY() * s, v.getX() * s
                + v.getY() * c);
    }

    public static Point2D vector(Point2D p1, Point2D p2) {
        return new Point2D.Double(p2.getX() - p1.getX(), p2.getY() - p1.getY());
    }

    public static double dot(Point2D a, Point2D b) {
        return a.getX() * b.getX() + a.getY() * b.getY();
    }

    public static Point2D normalize(Point2D p) {
        double l = length(p);
        return new Point2D.Double(p.getX() / l, p.getY() / l);
    }

    public static Point2D lineIntersection(Point2D p0, Point2D e0, Point2D p1,
            Point2D e1) {

        double dx = p1.getX() - p0.getX();
        double dy = p1.getY() - p0.getY();
        double det = e1.getX() * e0.getY() - e1.getY() * e0.getX();
        double u = (dy * e1.getX() - dx * e1.getY()) / det;
        // v = (dy * ad.getX() - dx * ad.getY()) / det
        return new Point2D.Double(p0.getX() + u * e0.getX(), p0.getY() + u
                * e0.getY());
    }

    // ///////////////////////////////////////////////////////////////////////////////////
    // Same method as previous but using Point2d instead of Point2d.Double
    public static double length(final Point2d p) {
        return Math.sqrt(p.x * p.x + p.y * p.y);
    }

    public static double distance(final Point2d p1, final Point2d p2) {
        return Math.sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y)
                * (p2.y - p1.y));
    }

    public static Point2d mulDoublePoint2d(Point2d result, final double d,
            final Point2d p) {
        result.x = p.x * d;
        result.y = p.y * d;
        return result;
    }

    public static Point2d opposite(Point2d result, final Point2d p) {
        result.x = -p.x;
        result.y = -p.y;
        return result;
    }

    public static Point2d mulPoint2d(Point2d result, Point2d p1, Point2d p2) {
        result.x = p1.x * p2.x;
        result.y = p1.y * p2.y;
        return result;
    }

    public static Point2d addPoint2d(Point2d result, Point2d p1, Point2d p2) {
        result.x = p1.x + p2.x;
        result.y = p1.y + p2.y;
        return result;
    }

    public static Point2d subPoint2d(Point2d result, Point2d p1, Point2d p2) {
        result.x = p1.x - p2.x;
        result.y = p1.y - p2.y;
        return result;
    }

    public static Point2d rotatePoint2d(Point2d result, Point2d center,
            Point2d p, double angle) {
        result.x = p.x - center.x;
        result.y = p.y - center.y;
        rotateVector(result, result, angle);
        return addPoint2d(result, result, center);
    }

    public static Point2d rotateVector(Point2d result, Point2d v, double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double vx = v.x;
        double vy = v.y;
        result.x = vx * c - vy * s;
        result.y = vx * s + vy * c;
        return result;
    }

    public static Point2d vector(Point2d result, Point2d p1, Point2d p2) {
        result.x = p2.x - p1.x;
        result.y = p2.y - p1.y;
        return result;
    }

    public static double dot(Point2d a, Point2d b) {
        return a.x * b.x + a.y * b.y;
    }

    public static Point2d copy(Point2d result, Point2d v) {
        result.x = v.x;
        result.y = v.y;
        return result;
    }

    public static Point2d normalize(Point2d result, Point2d v) {
        double l = length(v);
        if (l < 1e-6) {
            result.x = 0;
            result.y = 0;
        } else {
            result.x = v.x / l;
            result.y = v.y / l;
        }
        return result;
    }

    public static Point2d lineIntersection(Point2d result, Point2d p0,
            Point2d e0, Point2d p1, Point2d e1) {

        double dx = p1.x - p0.x;
        double dy = p1.y - p0.y;
        double det = e1.x * e0.y - e1.y * e0.x;
        double u = (dy * e1.x - dx * e1.y) / det;
        // v = (dy * ad.x - dx * ad.y) / det
        result.x = p0.x + u * e0.x;
        result.y = p0.y + u * e0.y;
        return result;
    }

}
