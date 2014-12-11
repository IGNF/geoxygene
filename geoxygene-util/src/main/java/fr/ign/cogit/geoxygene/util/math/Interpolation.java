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

import java.awt.Color;

/**
 * @brief Static class providing interpolation functions between two values. *
 * 
 *        This class provides an alternative to javax.media.jai.Interpolate for
 *        1D interpolation
 *
 *        Interpolation methods are specified using functors implementing
 *        {@link Functor} and {@link ExtendedFunctor}. Parameter-free methods
 *        can be accessed as static members of {@link Interpolation} (e.g.
 *        {@link #linearFunctor}, {@link #cosineFunctor} and
 *        {@link #cubicFunctor} ), while more complex approaches must be
 *        allocated manually, such as {@link HermiteFunctor}.
 * 
 *        Note that alternative interpolation methods can be defined outside of
 *        this class.
 * 
 * @author nmellado
 * 
 * @see http://paulbourke.net/miscellaneous/interpolation/ for more details
 */
public class Interpolation {
    public static final LinearFunctor linearFunctor = new LinearFunctor();
    public static final CosineFunctor cosineFunctor = new CosineFunctor();
    public static final CubicFunctor cubicFunctor = new CubicFunctor();

    private Interpolation() {

    }

    /************************************************************************
     * Interpolation Functions
     */

    public static double interpolate(double y1, double y2, double mu,
            Interpolation.Functor interFun) {
        return interFun.eval(y1, y2, mu);
    }

    public static double interpolate(double y0, double y1, double y2,
            double y3, double mu, Interpolation.ExtendedFunctor interFun) {
        return interFun.eval(y0, y1, y2, y3, mu);
    }

    public static Color interpolateRGB(Color c1, Color c2, double mu,
            Interpolation.Functor interFun) {

        double red = interFun.eval(c1.getRed(), c2.getRed(), mu);
        double green = interFun.eval(c1.getGreen(), c2.getGreen(), mu);
        double blue = interFun.eval(c1.getBlue(), c2.getBlue(), mu);
        return new Color((float) red / 255.f, (float) green / 255.f,
                (float) blue / 255.f);
    }

    /************************************************************************
     * Functor Interfaces
     */

    /**
     * @brief Functor interpolating between y1 (mu=0.) and y2 (mu=1.)
     * 
     * @param y1
     *            Starting point for the interpolation
     * @param y2
     *            End point for the interpolation
     */
    public static interface Functor {
        public double eval(double y1, double y2, double mu);
    }

    /**
     * @brief Functor interpolating between y1 (mu=0.) and y2 (mu=1.)
     * 
     *        With this interpolation, points before y1 and after y2 are also
     *        required
     * 
     * @param y0
     *            Point before y1
     * @param y1
     *            Starting point for the interpolation
     * @param y2
     *            End point for the interpolation
     * @param y3
     *            Point after y2
     */
    public static interface ExtendedFunctor {
        public double eval(double y0, double y1, double y2, double y3, double mu);
    }

    /************************************************************************
     * Functor Implementations
     */
    public static class LinearFunctor implements Functor {
        @Override
        public double eval(double y1, double y2, double mu) {
            return (y1 * (1. - mu) + y2 * mu);
        }
    }

    public static class CosineFunctor implements Functor {
        @Override
        public double eval(double y1, double y2, double mu) {
            double mu2 = (1. - Math.cos(mu * Math.PI)) / 2;
            return (y1 * (1. - mu2) + y2 * mu2);
        }
    }

    public static class CubicFunctor implements ExtendedFunctor {
        @Override
        public double eval(double y0, double y1, double y2, double y3, double mu) {
            double mu2 = mu * mu;
            double a0 = y3 - y2 - y0 + y1;
            double a1 = y0 - y1 - a0;
            double a2 = y2 - y0;
            double a3 = y1;

            return a0 * mu * mu2 + a1 * mu2 + a2 * mu + a3;
        }
    }

    public static class HermiteFunctor implements ExtendedFunctor {
        /**
         * @brief 1 is high, 0 normal, -1 is low
         */
        public double tension = 0.;
        /**
         * @brief 0 is even, positive is towards first segment, negative towards
         *        the other
         */
        public double bias = 0.;

        @Override
        public double eval(double y0, double y1, double y2, double y3, double mu) {
            double m0, m1, mu2, mu3;
            double a0, a1, a2, a3;

            mu2 = mu * mu;
            mu3 = mu2 * mu;
            m0 = (y1 - y0) * (1. + this.bias) * (1. - this.tension) / 2.;
            m0 += (y2 - y1) * (1. - this.bias) * (1. - this.tension) / 2.;
            m1 = (y2 - y1) * (1. + this.bias) * (1. - this.tension) / 2.;
            m1 += (y3 - y2) * (1. - this.bias) * (1. - this.tension) / 2.;
            a0 = 2. * mu3 - 3. * mu2 + 1;
            a1 = mu3 - 2. * mu2 + mu;
            a2 = mu3 - mu2;
            a3 = -2. * mu3 + 3. * mu2;

            return (a0 * y1 + a1 * m0 + a2 * m1 + a3 * y2);
        }
    }
}
