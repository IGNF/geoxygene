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
 * @author JeT Gaussian function a*e^-((x-b)^2/(2c^2)) parameterized by a, b and
 *         c
 */
public class GaussFunction implements Function1D {

    private double a = 1.; // a parameter
    private double b = 0.; // b parameter
    private double c = 1.; // c parameter

    /**
     * Constructor
     */
    public GaussFunction() {
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
    public GaussFunction(final double a, final double b, final double c) {
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
        return "f(x)=a*e^-((x-b)^2/(2c^2)). a,b,c double values";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.gl.GeoDisplacementFunction1D#displacement
     * (double)
     */
    @Override
    public Double evaluate(final double x) throws FunctionEvaluationException {
        try {
            return this.a
                    * Math.exp(-(x - this.b) * (x - this.b)
                            / (2 * this.c * this.c));
        } catch (Exception e) {
            throw new FunctionEvaluationException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Gauss[" + this.a + "," + this.b + "," + this.c + "]";
    }

}
