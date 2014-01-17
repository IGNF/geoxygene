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

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;

/**
 * @author JeT
 * 
 */
public class BasicParameterizer implements Parameterizer {

    private double minX = 0;
    private double minY = 0;
    private double maxX = 1;
    private double maxY = 1;
    // scale and translate are expressed in parameterized coordinates
    private double scaleX = 1.;
    private double scaleY = 1.;
    private double translateX = 0.;
    private double translateY = 0.;

    /**
     * Constructor
     */
    public BasicParameterizer(double minX, double minY, double maxX, double maxY) {
        this.setMinX(minX);
        this.setMaxX(maxX);
        this.setMinY(minY);
        this.setMaxY(maxY);
    }

    /**
     * Constructor
     */
    public BasicParameterizer(IEnvelope envelope) {
        this.setMinX(envelope.minX());
        this.setMaxX(envelope.maxX());
        this.setMinY(envelope.minY());
        this.setMaxY(envelope.maxY());
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
    public Point2d getTextureCoordinates(double[] vertex) {
        return new Point2d(((vertex[0] - this.minX) / (this.maxX - this.minX)) * this.scaleX + this.translateX,
                ((vertex[1] - this.minY) / (this.maxY - this.minY)) * this.scaleY + this.translateY);
    }

}
