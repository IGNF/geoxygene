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

package test.app;

import javax.vecmath.Point2d;

/**
 * @author JeT
 * 
 */
public class Sample {

    private Point2d location = null;
    public static final Point2d origin = new Point2d(0, 0);

    /**
     * Default constructor
     */
    public Sample() {
        this(origin);
    }

    public Sample(Point2d location) {
        this.location = new Point2d(location);
    }

    public Sample(double x, double y) {
        this(new Point2d(x, y));
    }

    /**
     * @return the location
     */
    public Point2d getLocation() {
        return this.location;
    }

    /**
     * @param location
     *            the location to set
     */
    public void setLocation(Point2d location) {
        this.location = location;
    }

}
