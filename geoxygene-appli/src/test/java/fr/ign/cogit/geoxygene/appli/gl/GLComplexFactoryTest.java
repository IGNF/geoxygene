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

package fr.ign.cogit.geoxygene.appli.gl;

import javax.vecmath.Point2d;

import org.junit.Test;

import fr.ign.cogit.geoxygene.util.math.BernsteinPolynomial;

/**
 * @author JeT
 * 
 */
public class GLComplexFactoryTest {

    @Test
    public void testSimpleCubicCase() {
        final Point2d p0 = new Point2d(0, 0);
        final Point2d p1 = new Point2d(0, 1);
        final Point2d p2 = new Point2d(1, 1);
        final Point2d p3 = new Point2d(1, 0);
        System.err.println("Cubic interpolation");
        for (float t = 0f; t <= 1f; t += 0.01f) {
            double px = BernsteinPolynomial
                    .evalCubic(p0.x, p1.x, p2.x, p3.x, t);
            double py = BernsteinPolynomial
                    .evalCubic(p0.y, p1.y, p2.y, p3.y, t);
            System.err.println(px + " " + py);
        }
    }

    @Test
    public void testSimpleQuadraticCase() {
        final Point2d p0 = new Point2d(0, 0);
        final Point2d p1 = new Point2d(0.5, 1);
        final Point2d p2 = new Point2d(1, 0);
        System.err.println("Quadratic interpolation");
        for (float t = 0f; t <= 1f; t += 0.01f) {
            double px = BernsteinPolynomial.evalQuadratic(p0.x, p1.x, p2.x, t);
            double py = BernsteinPolynomial.evalQuadratic(p0.y, p1.y, p2.y, t);
            System.err.println(px + " " + py);
        }
    }

    @Test
    public void testHighRangeFloatCubicCase() {
        final Point2d p0 = new Point2d(1000000f, -1000000f);
        final Point2d p1 = new Point2d(1000000f, 1000000f);
        final Point2d p2 = new Point2d(100000000f, 1000000f);
        final Point2d p3 = new Point2d(100000000f, -1000000f);
        System.err.println("High Range Cubic interpolation");
        for (float t = 0f; t <= 1f; t += 0.01f) {
            double px = BernsteinPolynomial
                    .evalCubic(p0.x, p1.x, p2.x, p3.x, t);
            double py = BernsteinPolynomial
                    .evalCubic(p0.y, p1.y, p2.y, p3.y, t);
            System.err.println(px + " " + py);
        }
    }

}
