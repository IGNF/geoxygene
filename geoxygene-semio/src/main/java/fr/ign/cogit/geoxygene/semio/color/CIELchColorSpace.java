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
 * The polar coordinate representation of the CIE L*a*b color space.<br/>
 * All the calculations are made in double precision.
 * @see <a
 *      href="http://www.brucelindbloom.com/">http://www.brucelindbloom.com/</a>
 *      for the equations.<br/>
 * @author Bertrand Duménieu
 */
public class CIELchColorSpace extends ColorSpace {

  private static final long serialVersionUID = 2793695973419565427L;

  private final CIELabColorSpace csLab;

  /**
   * @param use_awt_WYZ_ColorSpace : either use the AWT XYZ color space or the
   *          custom Geoxygene XYZ space to convert colors between sRGB and CIE
   *          L*c*h.
   */
  public CIELchColorSpace(boolean use_awt_XYZ_ColorSpace) {
    super(ColorSpace.TYPE_Lab, 3);
    csLab = new CIELabColorSpace(use_awt_XYZ_ColorSpace);
  }

  @Override
  public float[] toRGB(float[] lchvalue) {
    float[] lab = this.toCIELab(lchvalue);
    return csLab.toRGB(lab);
  }

  /*
   * Equations from http://www.brucelindbloom.com/index.html?Equations.html
   * 
   * @see java.awt.color.ColorSpace#toCIEXYZ(float[])
   */
  @Override
  public float[] fromRGB(float[] rgbvalue) {
    float[] lab = csLab.fromRGB(rgbvalue);
    return this.fromCIELab(lab);
  }

  /*
   * Equations from http://www.brucelindbloom.com/index.html?Equations.html
   * 
   * @see java.awt.color.ColorSpace#toCIEXYZ(float[])
   */
  @Override
  public float[] toCIEXYZ(float[] lchvalue) {
    float[] lab = this.toCIELab(lchvalue);
    return csLab.toCIEXYZ(lab);
  }

  /*
   * Equations from http://www.brucelindbloom.com/
   * 
   * @see java.awt.color.ColorSpace#toCIEXYZ(float[])
   */
  @Override
  public float[] fromCIEXYZ(float[] xyzvalue) {
    float[] lab = csLab.fromCIEXYZ(xyzvalue);
    return this.fromCIELab(lab);
  }

  @Override
  public float getMinValue(int component) {
    assert (component < this.getNumComponents());
    return 0f;
  }

  @Override
  public float getMaxValue(int component) {
    assert (component < this.getNumComponents());
    return component == 2 ? 100f : 360f;
  }

  @Override
  public String getName(int component) {
    switch (component) {
      case 0:
        return "L";
      case 1:
        return "c";
      case 2:
        return "h";
    }
    return "";
  }

  /**
   * Convenience method to convert from polar CIELch coordinates to CIE Lab
   * Cartesian coordinates.
   * @param a color in CIE Lch(ab) coordinates.
   * @return the color in CIE L*a*b* coordinates
   */
  private float[] toCIELab(float[] Lch) {
    float L = Lch[0];
    double c = Lch[1];
    double h = Lch[2];

    float Hr = (float) Math.toRadians(h);
    float a = (float) (c * Math.cos(Hr));
    float b = (float) (c * Math.sin(Hr));
    return new float[] { L, a, b };
  }

  /**
   * Convenience method to convert from Cartesian CIELab coordinates to CIE Lch
   * polar coordinates.
   * @param a color in CIE L*a*b* coordinates.
   * @return the color in CIE Lch(ab) coordinates
   */
  private float[] fromCIELab(float[] Lab) {
    float L = Lab[0];
    double a = Lab[1];
    double b = Lab[2];

    double c = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    double h = Math.toDegrees(Math.atan2(b, a));
    h += h < 0 ? 360 : 0;
    h -= h >= 360 ? 360 : 0;
    return new float[] { L, (float) c, (float) h };
  }

}
