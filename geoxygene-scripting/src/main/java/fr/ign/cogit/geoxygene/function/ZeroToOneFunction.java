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
package fr.ign.cogit.geoxygene.function;

/**
 * @author JeT This function goes from (0,0) to (1,1) with a given slope (alpha)
 *         Properties are: f(0) = 0, f(x<=0) = 0 f(1) = 1, f(x>=1) = 1 f'(0) =
 *         0; f'(1) = 0 f(1/2) = 1/2 f'(1/2) = alpha
 * 
 *         parameterized by alpha
 */
public class ZeroToOneFunction implements Function1D {

    private double a = 1.; // a parameter
    private double b = 0.; // b parameter
    private double c = 1.; // c parameter
    private double d = 1.; // d parameter

    /**
     * Constructor
     */
    public ZeroToOneFunction() {
        super();
    }

    /**
     * Constructor
     */
    public ZeroToOneFunction(final double alpha) {
        super();
        this.setSlope(alpha);
    }

    private void setSlope(double alpha) {
        this.a = 16 * alpha - 9;
        this.b = 22.5 - 40 * alpha;
        this.c = 32 * alpha - 20;
        this.d = 7.5 - 8 * alpha;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.function.Function1D#help()
     */
    @Override
    public String help() {
        return "f(0)=0, f(1)=1, f'(0)=0, f'(1)=0, f(1/2) = 1/2, f'(1/2) = alpha";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.gl.GeoDisplacementFunction1D#displacement
     * (double)
     */
    @Override
    public Double evaluate(final double x) {
        return this.a * x * x * x * x * x + this.b * x * x * x * x + this.c * x
                * x * x + this.d * x * x;
    }

}
