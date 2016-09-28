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
 * CIE L*a*b* color space. All the calculations are made in double precision.
 * @See <a
 *      href="http://www.brucelindbloom.com/">http://www.brucelindbloom.com/</a>
 *      for the equations.
 * @author Bertrand Duménieu
 */
public class CIELabColorSpace extends ColorSpace {

  /** D50 reference white (Default java ICC profile) */
  private static final double[] REFWHITE = new double[] { .9642d, 1d, .8249d };

  /** See http://www.brucelindbloom.com/ for more information */
  private static final double epsilon = 0.008856;
  private static final double kappa = 903.3;

  private ColorSpace csXYZ;

  private static final long serialVersionUID = 8345940285009872825L;

  /**
   * @param use_awt_WYZ_ColorSpace : either use the AWT XYZ color space or the
   *          custom Geoxygene XYZ space to convert colors between sRGB and CIE
   *          L*a*b*.
   */
  public CIELabColorSpace(boolean use_awt_XYZ_ColorSpace) {
    super(TYPE_Lab, 3);
    if (use_awt_XYZ_ColorSpace) {
      this.csXYZ = ColorSpace.getInstance(CS_CIEXYZ);
    } else {
      this.csXYZ = new CIEXYZColorSpace();
    }
  }

  @Override
  public float[] toRGB(float[] colorvalue) {
    return csXYZ.toRGB(this.toCIEXYZ(colorvalue));
  }

  @Override
  public float[] fromRGB(float[] rgbvalue) {
    float xyz[] = csXYZ.fromRGB(rgbvalue);
    return this.fromCIEXYZ(xyz);
  }

  /*
   * Equations from http://www.brucelindbloom.com/
   * 
   * @see java.awt.color.ColorSpace#toCIEXYZ(float[])
   */
  @Override
  public float[] fromCIEXYZ(float[] xyz) {
    double xr = xyz[0] / REFWHITE[0];
    double yr = xyz[1] / REFWHITE[1];
    double zr = xyz[2] / REFWHITE[2];

    double[] f = new double[3];

    f[0] = xr > epsilon ? Math.cbrt(xr) : (kappa * xr + 16d) / 116d;
    f[1] = yr > epsilon ? Math.cbrt(yr) : (kappa * yr + 16d) / 116d;
    f[2] = zr > epsilon ? Math.cbrt(zr) : (kappa * zr + 16d) / 116d;

    // Non normalized LAB coordinates in [0;100]*[-128;127]*[-128;127]
    double[] unLAB = new double[3];
    unLAB[0] = 116d * f[1] - 16d;
    unLAB[1] = 500d * (f[0] - f[1]);
    unLAB[2] = 200d * (f[1] - f[2]);

    return new float[] { (float) unLAB[0], (float) (unLAB[1]), (float) unLAB[2] };
  }

  /*
   * Equations from http://www.brucelindbloom.com/
   * 
   * @see java.awt.color.ColorSpace#toCIEXYZ(float[])
   */
  @Override
  public float[] toCIEXYZ(float[] lab) {
    double fy = (lab[0] + 16d) / 116d;
    double fx = lab[1] / 500d + fy;
    double fz = fy - lab[2] / 200d;

    double fx3 = Math.pow(fx, 3);
    double fy3 = Math.pow(fy, 3);
    double fz3 = Math.pow(fz, 3);

    double[] xyz = new double[3];

    xyz[0] = fx3 > epsilon ? fx3 : (116d * fx - 16d) / kappa;
    xyz[1] = lab[0] > epsilon * kappa ? fy3 : lab[0] / kappa;
    xyz[2] = fz3 > epsilon ? fz3 : (116d * fz - 16d) / kappa;

    xyz[0] = xyz[0] * REFWHITE[0];
    xyz[1] = xyz[1] * REFWHITE[1];
    xyz[2] = xyz[2] * REFWHITE[2];

    return new float[] { (float) xyz[0], (float) xyz[1], (float) xyz[2] };
  }

  @Override
  public float getMinValue(int component) {
    return component == 0 ? 0f : -128f;
  }

  @Override
  public float getMaxValue(int component) {
    return component == 0 ? 100f : 127f;
  }

}
