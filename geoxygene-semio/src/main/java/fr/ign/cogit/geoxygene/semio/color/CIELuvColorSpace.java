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
import java.util.Arrays;


/**
 * The CIE L*u*v color space.<br/>
 * All the calculations are made in double precision.
 * @see <a
 *      href="http://www.brucelindbloom.com/">http://www.brucelindbloom.com/</a>
 *      for the equations.<br/>
 * @author Bertrand Duménieu
 * @author Julien Perret
 */
public class CIELuvColorSpace extends ColorSpace {

  /** D50 reference white (Default java ICC profile) */
  private static final double[] REFWHITE = new double[] { .9642d, 1d, .8249d };

  /**
   * See http://www.brucelindbloom.com/ Luv to XYZ conversion equations for more
   * information
   */
  private static final double epsilon = 0.008856;
  private static final double kappa = 903.3;
  private static double u0 = 4f * REFWHITE[0]
      / (REFWHITE[0] + 15f * REFWHITE[1] + 3f * REFWHITE[2]);
  private static double v0 = 9f * REFWHITE[1]
      / (REFWHITE[0] + 15f * REFWHITE[1] + 3f * REFWHITE[2]);

  private static float[] zeroes = new float[] { 0f, 0f, 0f };

  private static final long serialVersionUID = -2183631496590229012L;
  private final ColorSpace csXYZ;

  /**
   * @param use_awt_XYZ_ColorSpace : either use the AWT XYZ color space or the
   *          custom Geoxygene XYZ space to convert colors between sRGB and CIE
   *          L*c*h.
   */
  protected CIELuvColorSpace(boolean use_awt_XYZ_ColorSpace) {
    super(ColorSpace.TYPE_Luv, 3);
    if (use_awt_XYZ_ColorSpace) {
      this.csXYZ = ColorSpace.getInstance(CS_CIEXYZ);
    } else {
      this.csXYZ = new CIEXYZColorSpace();
    }
  }

  @Override
  public float[] toRGB(float[] luvvalue) {
    float[] xyz = this.toCIEXYZ(luvvalue);
    return csXYZ.toRGB(xyz);
  }

  @Override
  public float[] fromRGB(float[] rgbvalue) {
    float[] xyz = csXYZ.fromRGB(rgbvalue);
    return this.fromCIEXYZ(xyz);
  }

  /**
   * @author Julien Perret
   * @author Bertrand Duménieu
   */
  @Override
  public float[] toCIEXYZ(float[] luvvalue) {
    float[] xyzvalue = new float[luvvalue.length];
    if (!Arrays.equals(luvvalue, zeroes)) {
      float L = luvvalue[0];
      float u = luvvalue[1];
      float v = luvvalue[2];
      double Y = L > (kappa * epsilon) ? Math.pow((L + 16d) / 116d, 3) : L
          / kappa;
      double a = (0.33333333333) * ((52d * L) / (u + 13d * L * u0) - 1d);
      double b = -5d * Y;
      double c = -0.33333333333;
      double d = Y * ((39d * L) / (v + 13d * L * v0) - 5d);
      double X = (d - b) / (a - c);
      xyzvalue[0] = (float) (X);
      xyzvalue[1] = (float) Y;
      xyzvalue[2] = (float) (X * a + b);
    }
    return xyzvalue;
  }

  /**
   * @author Julien Perret
   * @author Bertrand Duménieu
   */
  @Override
  public float[] fromCIEXYZ(float[] colorvalue) {
    float[] luvvalue = new float[colorvalue.length];
    if (!Arrays.equals(colorvalue, zeroes)) {
      double yr = colorvalue[1] / REFWHITE[1];
      double denom = (colorvalue[0] + 15d * colorvalue[1] + 3 * colorvalue[2]);
      double denomr = (REFWHITE[0] + 15d * REFWHITE[1] + 3 * REFWHITE[2]);
      double up = (4d * colorvalue[0]) / denom;
      double vp = (9d * colorvalue[1]) / denom;
      double upr = (4d * REFWHITE[0]) / denomr;
      double vpr = (9d * REFWHITE[1]) / denomr;

      luvvalue[0] = (float) (yr > epsilon ? 116d * Math.cbrt(yr) - 16d : kappa
          * yr);
      luvvalue[1] = (float) (13d * luvvalue[0] * (up - upr));
      luvvalue[2] = (float) (13d * luvvalue[0] * (vp - vpr));
    }
    return luvvalue;
  }

}
