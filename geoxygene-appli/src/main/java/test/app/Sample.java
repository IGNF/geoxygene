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

import java.awt.geom.AffineTransform;
import java.util.Comparator;

import javax.vecmath.Point2d;

import fr.ign.util.graphcut.Tile;

/**
 * @author JeT
 * 
 */
public class Sample {

    private Point2d location = null;
    private Point2d rotation = null;
    private Point2d scale = null;
    private Tile tile = null;
    public static final Point2d origin = new Point2d(0, 0);
    public static final Point2d scaleUnit = new Point2d(1, 1);

    /**
     * Default constructor
     */
    public Sample() {
        this(origin, null);
    }

    public Sample(Point2d location, Tile tile) {
        this(location, origin, null);
    }

    public Sample(double x, double y, Tile tile) {
        this(new Point2d(x, y), tile);
    }

    public Sample(Point2d location, Point2d rotation, Tile tile) {
        this(location, rotation, scaleUnit, tile);
    }

    public Sample(Point2d location, Point2d rotation, Point2d scale, Tile tile) {
        super();
        this.location = new Point2d(location);
        this.rotation = new Point2d(rotation);
        this.scale = new Point2d(scale);
        this.tile = tile;
    }

    /**
     * @return the tile
     */
    public Tile getTile() {
        return this.tile;
    }

    /**
     * @param tile
     *            the tile to set
     */
    public void setTile(Tile tile) {
        this.tile = tile;
    }

    /**
     * @return the rotation
     */
    public Point2d getRotation() {
        return this.rotation;
    }

    /**
     * @param rotation
     *            the rotation to set
     */
    public void setRotation(Point2d rotation) {
        this.rotation = rotation;
    }

    /**
     * @return the scale
     */
    public Point2d getScale() {
        return this.scale;
    }

    /**
     * @param scale
     *            the scale to set
     */
    public void setScale(Point2d scale) {
        this.scale = scale;
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Sample [location=" + this.location + "]";
    }

    public static class TileSizeComparator implements Comparator<Sample> {

        @Override
        public int compare(Sample o1, Sample o2) {
            if (o1.getTile() == null && o2.getTile() == null) {
                return 0;
            }
            if (o1.getTile() == null) {
                return -1;
            }
            if (o2.getTile() == null) {
                return 1;
            }
            return Integer.valueOf(o1.getTile().getSize()).compareTo(Integer.valueOf(o2.getTile().getSize()));
        }

    }

}
