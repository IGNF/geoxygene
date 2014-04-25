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

}
