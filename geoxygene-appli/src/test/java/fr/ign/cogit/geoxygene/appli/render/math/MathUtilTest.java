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

package fr.ign.cogit.geoxygene.appli.render.math;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.math.MathUtil;


/**
 * @author JeT
 *
 */
public class MathUtilTest {

  private static final double EPSILON = 1E-6;

  @Test
  public void testIntersection() {
    this.checkIntersection(0, 0, 1, 0, 10, -5, 0, -1, new Point2d(10, 0));
    this.checkIntersection(-2, 0, 1, 1, 2, 0, -1, 1, new Point2d(0, 2));
    this.checkIntersection(-2, 0, -1, -1, 2, 0, -1, 1, new Point2d(0, 2));
    this.checkIntersection(-2, 0, -1, -1, 2, 0, 1, -1, new Point2d(0, 2));
    this.checkIntersection(-2, 0, 1, 1, 2, 0, 1, -1, new Point2d(0, 2));
    this.checkIntersection(0, 0, 1, 1, 0, 0, 1, 1, null);
    this.checkIntersection(5, 0, 1, 1, 0, 0, 1, 1, null);
    this.checkIntersection(-5, 0, 1, 1, 0, 0, 1, 1, null);
    this.checkIntersection(-5, 0, 1, 1, 0, 5, 1, 1, null);
    this.checkIntersection(-5, 0, 1, 1, 0, 5, -1, -1, null);

  }

  public void checkIntersection(final double p1x, final double p1y, final double v1x, final double v1y, final double p2x, final double p2y,
      final double v2x, final double v2y, final Point2d expected) {
    Point2d inter = MathUtil.intersectionPoint(new Point2d(p1x, p1y), new Vector2d(v1x, v1y), new Point2d(p2x, p2y), new Vector2d(v2x, v2y));
    if (expected == null) {
      Assert.assertNull(inter);

    } else {
      Assert.assertNotNull(inter);
      Assert.assertEquals(expected.x, inter.x, EPSILON);
      Assert.assertEquals(expected.y, inter.y, EPSILON);
    }
  }
}
