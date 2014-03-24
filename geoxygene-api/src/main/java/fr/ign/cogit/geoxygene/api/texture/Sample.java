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

package fr.ign.cogit.geoxygene.api.texture;

import java.awt.geom.Point2D;
import java.util.Comparator;

/**
 * @author JeT
 * 
 */
public class Sample {

    private Point2D location = null;
    private Point2D rotation = null;
    private Point2D scale = null;
    private Tile tile = null;
    public static final Point2D origin = new Point2D.Double(0, 0);
    public static final Point2D scaleUnit = new Point2D.Double(1, 1);

    /**
     * Default constructor
     */
    public Sample() {
        this(origin, null);
    }

    public Sample(Point2D location, Tile tile) {
        this(location, origin, null);
    }

    public Sample(double x, double y, Tile tile) {
        this(new Point2D.Double(x, y), tile);
    }

    public Sample(Point2D location, Point2D rotation, Tile tile) {
        this(location, rotation, scaleUnit, tile);
    }

    public Sample(Point2D location, Point2D rotation, Point2D scale, Tile tile) {
        super();
        this.location = new Point2D.Double(location.getX(), location.getY());
        this.rotation = new Point2D.Double(rotation.getX(), rotation.getY());
        this.scale = new Point2D.Double(scale.getX(), scale.getY());
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
    public Point2D getRotation() {
        return this.rotation;
    }

    /**
     * @param rotation
     *            the rotation to set
     */
    public void setRotation(Point2D rotation) {
        this.rotation = rotation;
    }

    /**
     * @return the scale
     */
    public Point2D getScale() {
        return this.scale;
    }

    /**
     * @param scale
     *            the scale to set
     */
    public void setScale(Point2D scale) {
        this.scale = scale;
    }

    /**
     * @return the location
     */
    public Point2D getLocation() {
        return this.location;
    }

    /**
     * @param location
     *            the location to set
     */
    public void setLocation(Point2D location) {
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
