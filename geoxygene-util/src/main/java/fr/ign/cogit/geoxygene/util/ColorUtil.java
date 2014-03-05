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
package fr.ign.cogit.geoxygene.util;

import java.awt.Color;

/**
 * Tools for color management
 * 
 * @author JeT
 * 
 */
public class ColorUtil {

    /**
     * Private constructor for utility class
     */
    private ColorUtil() {
        // Utlity class
    }

    /**
     * A color with the specified opacity applied to the given color.
     * 
     * @param color
     *            the input color
     * @param opacity
     *            the opacity
     * @return a new color with the specified opacity applied to the given color
     */
    public static Color getColorWithOpacity(Color color, double opacity) {
        float[] symbolizerColorComponenents = color.getComponents(null);
        return new Color(symbolizerColorComponenents[0], symbolizerColorComponenents[1], symbolizerColorComponenents[2], symbolizerColorComponenents[3]
                * (float) opacity);
    }

    /**
     * Convert rgb colors to LAB color model using average observer (2° / D65)
     * 
     * @param rgb
     *            3 floats between 0 & 1
     * @return 3 floats L, a & b (between 0 & 100)
     */
    public static float[] rgbToLab(float... rgb) {
        float[] lab = { 0f, 0f, 0f };
        float[] xyz = rgbToXYZ(rgb);
        final double ref_X = 95.047f;
        final double ref_Y = 100.00f;
        final double ref_Z = 108.883f;
        double var_X = xyz[0] / ref_X;          //ref_X =  95.047   Observer= 2°, Illuminant= D65
        double var_Y = xyz[1] / ref_Y;          //ref_Y = 100.000
        double var_Z = xyz[2] / ref_Z;          //ref_Z = 108.883

        if (var_X > 0.008856) {
            var_X = Math.pow(var_X, (1. / 3));
        } else {
            var_X = (7.787 * var_X) + (16. / 116);
        }
        if (var_Y > 0.008856) {
            var_Y = Math.pow(var_Y, (1. / 3));
        } else {
            var_Y = (7.787 * var_Y) + (16. / 116);
        }
        if (var_Z > 0.008856) {
            var_Z = Math.pow(var_Z, (1. / 3));
        } else {
            var_Z = (7.787 * var_Z) + (16. / 116);
        }

        lab[0] = (float) ((116 * var_Y) - 16);
        lab[1] = (float) (500 * (var_X - var_Y));
        lab[2] = (float) (200 * (var_Y - var_Z));
        return lab;
    }

    /**
     * Convert rgb colors to XYZ color model using average observer (2° / D65)
     * 
     * @param rgb
     *            3 floats between 0 & 1
     * @return 3 floats L, a & b (between 0 & 100)
     */
    public static float[] rgbToXYZ(float... rgb) {
        float[] xyz = { 0f, 0f, 0f };
        double var_R = rgb[0];        //R from 0 to 255
        double var_G = rgb[1];        //G from 0 to 255
        double var_B = rgb[2];        //B from 0 to 255

        if (var_R > 0.04045) {
            var_R = Math.pow(((var_R + 0.055) / 1.055), 2.4);
        } else {
            var_R = var_R / 12.92;
        }
        if (var_G > 0.04045) {
            var_G = Math.pow(((var_G + 0.055) / 1.055), 2.4);
        } else {
            var_G = var_G / 12.92;
        }
        if (var_B > 0.04045) {
            var_B = Math.pow(((var_B + 0.055) / 1.055), 2.4);
        } else {
            var_B = var_B / 12.92;
        }

        var_R = var_R * 100;
        var_G = var_G * 100;
        var_B = var_B * 100;

        //Observer. = 2°, Illuminant = D65
        xyz[0] = (float) (var_R * 0.4124 + var_G * 0.3576 + var_B * 0.1805);
        xyz[1] = (float) (var_R * 0.2126 + var_G * 0.7152 + var_B * 0.0722);
        xyz[2] = (float) (var_R * 0.0193 + var_G * 0.1192 + var_B * 0.9505);
        return xyz;
    }

}
