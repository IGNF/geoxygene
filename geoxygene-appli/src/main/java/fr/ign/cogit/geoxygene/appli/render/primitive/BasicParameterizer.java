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

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;

/**
 * @author JeT
 * 
 */
public class BasicParameterizer implements Parameterizer {

    private static final Logger logger = Logger
            .getLogger(BasicParameterizer.class.getName()); // logger

    private double minX = 0;
    private double minY = 0;
    private double maxX = 1;
    private double maxY = 1;
    // scale and translate are expressed in parameterized coordinates
    private double scaleX = 1.;
    private double scaleY = 1.;
    private double translateX = 0.;
    private double translateY = 0.;
    private boolean xFlip = false;
    private boolean yFlip = false;

    /**
     * Constructor
     */
    public BasicParameterizer(double minX, double minY, double maxX,
            double maxY, boolean xFlip, boolean yFlip) {
        this.setMinX(minX);
        this.setMaxX(maxX);
        this.setMinY(minY);
        this.setMaxY(maxY);
        this.setXFlip(xFlip);
        this.setYFlip(yFlip);
        // logger.debug("Create Basic parameterizer with bounding box " + minX +
        // "x" + minY + " /" + maxX + "x" + maxY);
    }

    /**
     * Constructor
     */
    public BasicParameterizer(IEnvelope envelope, boolean xFlip, boolean yFlip) {
        // incoming coordinates are stored in object coordinates (0 -> max - min
        // )
        this(0, 0, envelope.maxX() - envelope.minX(), envelope.maxY()
                - envelope.minY(), xFlip, yFlip);
    }

    /**
     * @return the xFlip
     */
    public boolean getXFlip() {
        return this.xFlip;
    }

    /**
     * @param xFlip
     *            the xFlip to set
     */
    public void setXFlip(boolean xFlip) {
        this.xFlip = xFlip;
    }

    /**
     * @return the yFlip
     */
    public boolean getYFlip() {
        return this.yFlip;
    }

    /**
     * @param yFlip
     *            the yFlip to set
     */
    public void setYFlip(boolean yFlip) {
        this.yFlip = yFlip;
    }

    /**
     * @return the minX
     */
    public final double getMinX() {
        return this.minX;
    }

    /**
     * @param minX
     *            the minX to set
     */
    public final void setMinX(double minX) {
        this.minX = minX;
    }

    /**
     * @return the minY
     */
    public final double getMinY() {
        return this.minY;
    }

    /**
     * @param minY
     *            the minY to set
     */
    public final void setMinY(double minY) {
        this.minY = minY;
    }

    /**
     * @return the maxX
     */
    public final double getMaxX() {
        return this.maxX;
    }

    /**
     * @param maxX
     *            the maxX to set
     */
    public final void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    /**
     * @return the maxY
     */
    public final double getMaxY() {
        return this.maxY;
    }

    /**
     * @param maxY
     *            the maxY to set
     */
    public final void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public void scaleX(double scaleX) {
        if (scaleX != 0) {
            this.scaleX *= scaleX;
        }
    }

    public void scaleY(double scaleY) {
        if (scaleY != 0) {
            this.scaleY *= scaleY;
        }
    }

    public void translateX(double translateX) {
        this.translateX += translateX;
    }

    public void translateY(double translateY) {
        this.translateY += translateY;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.render.primitive.Parameterizer#
     * initializeParameterization()
     */
    @Override
    public void initializeParameterization() {
        // nothing to initialize
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.render.primitive.Parameterizer#
     * finalizeParameterization()
     */
    @Override
    public void finalizeParameterization() {
        // nothing to finalize
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.render.primitive.Parameterizer#
     * getTextureCoordinates(double[])
     */
    @Override
    public Point2d getTextureCoordinates(double... vertex) {
        // System.err.println("vertex = " + vertex[0] + " x " + vertex[1]);
        // System.err.println("min x = " + this.minX);
        // System.err.println("max x = " + this.maxX);
        double xTexture = ((vertex[0]) / (this.maxX - this.minX));
        double yTexture = ((vertex[1]) / (this.maxY - this.minY));
        if (this.getXFlip()) {
            xTexture = 1 - xTexture;
        }
        if (this.getYFlip()) {
            yTexture = 1 - yTexture;
        }
        return new Point2d(xTexture * this.scaleX + this.translateX, yTexture
                * this.scaleY + this.translateY);
    }

}
