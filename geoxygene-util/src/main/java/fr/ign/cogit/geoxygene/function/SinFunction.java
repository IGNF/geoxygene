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
 * @author JeT Simple sin function parameterized by wavelength, amplitude and
 *         phase
 */
public class SinFunction implements Function1D {

    private double wavelength = 1.; // sin wavelength (period)
    private double amplitude = 1.; // sin amplitude (height)
    private double phase = 0.; // sin phase (X translation factor)
    private double shift = 0.; // sin shift (Y translation factor)

    /**
     * Constructor
     */
    public SinFunction() {
        super();
    }

    /**
     * Constructor
     * 
     * @param wavelength
     *            sin wavelength (period)
     * @param amplitude
     *            sin amplitude (height)
     * @param phase
     *            sin phase (X translation factor)
     * @param shift
     *            sin shift (Y translation factor)
     */
    public SinFunction(final double wavelength, final double amplitude,
            final double phase, final double shift) {
        super();
        this.wavelength = wavelength;
        this.amplitude = amplitude;
        this.phase = phase;
        this.shift = shift;
    }

    /**
     * Constructor
     * 
     * @param wavelength
     *            sin wavelength (period)
     * @param amplitude
     *            sin amplitude (height)
     */
    public SinFunction(final double wavelength, final double amplitude) {
        super();
        this.wavelength = wavelength;
        this.amplitude = amplitude;
        this.phase = 0;
        this.shift = 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.function.Function1D#help()
     */
    @Override
    public String help() {
        return "f(x)=d + sin(c + x * a) * b. a,b,c,d real values (wavelength, amplitude, phase, shift)";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.gl.GeoDisplacementFunction1D#displacement
     * (double)
     */
    @Override
    public Double evaluate(final double parameter) {
        return this.shift + Math.sin(this.phase + parameter * this.wavelength)
                * this.amplitude;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Sin[" + this.wavelength + "," + this.amplitude + ","
                + this.phase + "," + this.shift + "]";
    }

}
