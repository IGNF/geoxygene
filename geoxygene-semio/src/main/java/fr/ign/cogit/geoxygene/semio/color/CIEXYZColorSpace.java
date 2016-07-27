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

package fr.ign.cogit.geoxygene.semio.color;

import java.awt.color.ColorSpace;

/**
 * Custom CIE XYZ color space consistent with the equations available at <a
 * href="http://www.brucelindbloom.com/">http://www.brucelindbloom.com/</a>. <br/>
 * The RGB/XYZ matrices are relative to the D50 reference white
 * (Bradford-adapted). <br/>
 * All the calculations are made in double precision.
 * @author Bertrand Duménieu
 *
 */
public class CIEXYZColorSpace extends ColorSpace {

  private static final long serialVersionUID = -5521863990867319119L;
  /* sRGB companding constants */
  private static final float XYZ_to_sRGB_companding = 0.0031308f;
  private static final float sRGB_to_XYZ_companding = 0.04045f;
  /*
   * Bradford-adapted D50 matrices to deal with the fact that sRGB is natively
   * adapted for the D65 illuminant. See
   * http://www.russellcottrell.com/photo/matrixCalculator.htm with the adapted
   * primaries with a bradford transform that can be found here:
   * http://www.brucelindbloom.com/index.html?WorkingSpaceInfo.html
   */
  private static final double[] XYZ2sRGB = new double[] { 3.1338561,
      -1.6168667, -0.4906146, -0.9787684, 1.9161415, 0.0334540, 0.0719453,
      -0.2289914, 1.4052427 };
  private static final double[] sRGB2XYZ = new double[] { 0.4361764, 0.3850532,
      0.1430661, 0.2225550, 0.7168324, 0.0606126, 0.0139329, 0.0970894,
      0.7140823 };

  protected CIEXYZColorSpace() {
    super(ColorSpace.TYPE_XYZ, 3);
  }

  @Override
  public float[] toRGB(float[] xyz) {

    double[] rgb = new double[xyz.length];

    rgb[0] = xyz[0] * XYZ2sRGB[0] + xyz[1] * XYZ2sRGB[1] + xyz[2] * XYZ2sRGB[2];
    rgb[1] = xyz[0] * XYZ2sRGB[3] + xyz[1] * XYZ2sRGB[4] + xyz[2] * XYZ2sRGB[5];
    rgb[2] = xyz[0] * XYZ2sRGB[6] + xyz[1] * XYZ2sRGB[7] + xyz[2] * XYZ2sRGB[8];

    rgb[0] = XYZ_to_sRGB_Companding(rgb[0]);
    rgb[1] = XYZ_to_sRGB_Companding(rgb[1]);
    rgb[2] = XYZ_to_sRGB_Companding(rgb[2]);

    return new float[] { (float) rgb[0], (float) rgb[1], (float) rgb[2] };
  }

  @Override
  public float[] fromRGB(float[] rgb) {
    double[] rgbc = new double[rgb.length];
    rgbc[0] = sRGB_to_XYZ_Companding(rgb[0]);
    rgbc[1] = sRGB_to_XYZ_Companding(rgb[1]);
    rgbc[2] = sRGB_to_XYZ_Companding(rgb[2]);

    double[] xyz = new double[rgb.length];

    xyz[0] = rgbc[0] * sRGB2XYZ[0] + rgbc[1] * sRGB2XYZ[1] + rgbc[2]
        * sRGB2XYZ[2];
    xyz[1] = rgbc[0] * sRGB2XYZ[3] + rgbc[1] * sRGB2XYZ[4] + rgbc[2]
        * sRGB2XYZ[5];
    xyz[2] = rgbc[0] * sRGB2XYZ[6] + rgbc[1] * sRGB2XYZ[7] + rgbc[2]
        * sRGB2XYZ[8];

    return new float[] { (float) xyz[0], (float) xyz[1], (float) xyz[2] };
  }

  @Override
  public float[] toCIEXYZ(float[] xyz) {
    return xyz;
  }

  @Override
  public float[] fromCIEXYZ(float[] xyz) {
    return xyz;
  }

  private double XYZ_to_sRGB_Companding(double v) {
    return v <= XYZ_to_sRGB_companding ? v * 12.92 : 1.055 * Math.pow(v,
        1d / 2.4) - 0.055;
  }

  private double sRGB_to_XYZ_Companding(double v) {
    return v <= sRGB_to_XYZ_companding ? v / 12.92 : Math.pow(
        (v + 0.055) / 1.055, 2.4);
  }
}
