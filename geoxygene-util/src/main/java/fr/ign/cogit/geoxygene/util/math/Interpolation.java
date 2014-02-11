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

/**
 * @author JeT
 * 
 */
public class Interpolation {

    /**
     * Private constructor
     */
    private Interpolation() {
        // TODO Auto-generated constructor stub
    }

    // cubic bernstein polynom
    static public float bernstein3(int i, float t) {
        switch (i) {
        case 0:
            return (1 - t) * (1 - t) * (1 - t);
        case 1:
            return 3 * t * (1 - t) * (1 - t);
        case 2:
            return 3 * t * t * (1 - t);
        case 3:
            return t * t * t;
        }
        return 0; //we only get here if an invalid i is specified
    }

    // quadratic bernstein polynom
    static public float bernstein2(int i, float t) {
        switch (i) {
        case 0:
            return (1 - t) * (1 - t);
        case 1:
            return 2 * t * (1 - t);
        case 2:
            return t * t;
        }
        return 0; //we only get here if an invalid i is specified
    }

    //evaluate a point on the B spline
    public static double interpolateQuadratic(double v0, double v1, double v2, float t) {
        return bernstein2(0, t) * v0 + bernstein2(1, t) * v1 + bernstein2(2, t) * v2;
    }

    //evaluate a point on the B spline
    public static double interpolateCubic(double v0, double v1, double v2, double v3, float t) {
        return bernstein3(0, t) * v0 + bernstein3(1, t) * v1 + bernstein3(2, t) * v2 + bernstein3(3, t) * v3;
    }

}
