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
 * @author JeT Sigmoid inverse function a * 1 / ( 1+e^-(x-b)*c ) - 0.5
 *         parameterized by a, b and c
 */
public class InverseSigmoidFunction extends AbstractFunction1D {

    private double a = 1.; // a parameter
    private double b = 0.; // b parameter
    private double c = 1.; // c parameter

    /**
     * Constructor
     */
    public InverseSigmoidFunction() {
        super();
    }

    /**
     * Constructor
     * 
     * @param a
     *            a parameter
     * @param b
     *            b parameter
     * @param c
     *            c parameter
     */
    public InverseSigmoidFunction(final double a, final double b, final double c) {
        super();
        this.a = a;
        this.b = b;
        this.c = c;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.function.Function1D#help()
     */
    @Override
    public String help() {
        return "f(x)=a*1/(1+e^-(x-b)*c)-0.5. a,b,c real values";
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
        return this.a / (1 + Math.exp(-(x - this.b) * this.c)) - 0.5;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "InverseSigmoide[" + this.a + "," + this.b + "," + this.c + "]";
    }

}
