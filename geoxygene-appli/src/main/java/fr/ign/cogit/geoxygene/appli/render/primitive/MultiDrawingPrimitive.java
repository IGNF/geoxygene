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
package fr.ign.cogit.geoxygene.appli.render.primitive;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.Viewport;

/**
 * Drawing primitive composed of multiple drawing primitives. It is marked as a
 * leaf when it is composed
 * of only one child
 * The order of insertion is kept.
 * 
 * @author JeT
 * 
 */
public class MultiDrawingPrimitive implements DrawingPrimitive {

    private static Logger logger = Logger.getLogger(MultiDrawingPrimitive.class.getName()); // logger
    private final List<DrawingPrimitive> primitives = new ArrayList<DrawingPrimitive>(); // list of primitives
    /*
     * sum of the previous count and the nth primitive point count
     * the size of this list is always one element bigger than the primitive
     * size
     * pointsCount(n) is the sum of the n firsts primitives points
     */
    private final List<Integer> pointsCount = new ArrayList<Integer>();
    private final List<Shape> pathes = new ArrayList<Shape>();

    /**
     * Default constructor
     */
    public MultiDrawingPrimitive() {
        this.pointsCount.add(0); // first element is always zero for consistency
    }

    /**
     * add a primitive into this multi primitive. If the added primitive is a
     * multi primitive
     * its content is inserted instead of the multiprimitive
     * 
     * @param primitive
     *            primitive to add
     */
    public void addPrimitive(final DrawingPrimitive primitive) {
        if (primitive == null) {
            logger.warn("Try to add a null primitive into a " + this.getClass().getSimpleName());
            return;
        }
        // do not add itself !
        if (primitive == this) {
            logger.warn("Try to add itself into a " + this.getClass().getSimpleName());
            return;
        }
        if (primitive instanceof MultiDrawingPrimitive) {
            // flatten multiprimitive contained into multiprimitives
            for (DrawingPrimitive flattenedPrimitive : primitive.getPrimitives()) {
                this.addPrimitive(flattenedPrimitive);
            }
        } else {
            this.primitives.add(primitive);
            int n = this.pointsCount.get(this.pointsCount.size() - 1) + primitive.getPointCount();
            this.pointsCount.add(n);
            //      this.checkConsistency();
        }
        this.invalidateShape();
    }

    private void invalidateShape() {
        this.pathes.clear();
    }

    private void display(final String string) {
        System.err.println("--------------------------------- " + string);
        for (int i = 0; i < this.primitives.size(); i++) {
            System.err.print(i + " nbp= " + this.primitives.get(i).getPointCount() + "   [ " + this.pointsCount.get(i) + "  -  " + this.pointsCount.get(i + 1)
                    + "  :");
            for (int j = 0; j < this.primitives.get(i).getPointCount(); j++) {
                System.err.print(this.primitives.get(i).getPoint(j) + " ");
            }
            System.err.println();
        }
        System.err.println("-------------------------------------------");
    }

    /**
     * Costly method used for debug purpose only
     */
    private void checkConsistency() {
        for (int i = 0; i < this.getPointCount(); i++) {
            Point2d p = this.getPoint(i);
            if (p == null) {
                System.err.println(" p #" + i + " = null");
                this.display("this");
                p = this.getPoint(i);
            }
        }
    }

    /**
     * add a collection of primitive into this multi primitive
     * 
     * @param primitives
     *            primitive collection to add
     */
    public void addPrimitives(final Collection<? extends DrawingPrimitive> primitives) {
        if (primitives == null) {
            return;
        }
        for (DrawingPrimitive primitive : primitives) {
            this.addPrimitive(primitive);
        }
        this.invalidateShape();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive#getPrimitives
     * ()
     */
    @Override
    public List<DrawingPrimitive> getPrimitives() {
        return this.primitives;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive#getPoint
     * (int)
     */
    @Override
    public Point2d getPoint(final int n) {
        if (n < 0 || n >= this.getPointCount()) {
            throw new IndexOutOfBoundsException(n + " not in [0 .. " + this.getPointCount() + "[");
        }
        if (n < this.pointsCount.get(0)) {
            return this.primitives.get(0).getPoint(n);
        }
        int index = 1; // first index is always 0
        for (DrawingPrimitive primitive : this.primitives) {
            if (this.pointsCount.get(index) > n) {
                Point2d point = primitive.getPoint(n - this.pointsCount.get(index - 1));
                if (point == null) {
                    System.err.println("n = " + n + " index = " + index + " #i = " + this.pointsCount.get(index) + " #i-1 = " + this.pointsCount.get(index - 1)
                            + " #primitive = " + primitive.getPointCount());
                    System.err.println(" primitive.getPoint(" + n + " - " + this.pointsCount.get(index - 1) + " = " + (n - this.pointsCount.get(index - 1))
                            + ") = " + point);
                }
                return point;
            }
            index++;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive#getPointCount
     * ()
     */
    @Override
    public int getPointCount() {
        return this.pointsCount.get(this.primitives.size());
    }

    @Override
    public boolean isLeaf() {
        return this.getPrimitives().size() == 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MultiDrawingPrimitive [primitives count=" + this.primitives.size() + "]";
    }

    @Override
    public List<Shape> getShapes() {
        if (this.pathes.size() == 0) {
            for (DrawingPrimitive primitive : this.primitives) {
                this.pathes.addAll(primitive.getShapes());
            }
        }
        return this.pathes;
    }

    @Override
    public void generateParameterization(Parameterizer parameterizer) {
        for (DrawingPrimitive primitive : this.primitives) {
            primitive.generateParameterization(parameterizer);
        }

    }

    @Override
    public void update(Viewport viewport) {
        for (DrawingPrimitive primitive : this.primitives) {
            primitive.update(viewport);
        }

    }

}
