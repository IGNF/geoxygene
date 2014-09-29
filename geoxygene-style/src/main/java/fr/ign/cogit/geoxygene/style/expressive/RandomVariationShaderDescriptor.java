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

package fr.ign.cogit.geoxygene.style.expressive;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author JeT
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
public class RandomVariationShaderDescriptor extends LineShaderDescriptor {

    @XmlElement(name = "StrokePressureVariationAmplitude")
    private double strokePressureVariationAmplitude = .32;
    @XmlElement(name = "StrokePressureVariationWavelength")
    private double strokePressureVariationWavelength = 1. / .025;
    @XmlElement(name = "StrokeShiftVariationAmplitude")
    private double strokeShiftVariationAmplitude = .92;
    @XmlElement(name = "StrokeShiftVariationWavelength")
    private double strokeShiftVariationWavelength = 1. / .05;
    @XmlElement(name = "StrokeThicknessVariationAmplitude")
    private double strokeThicknessVariationAmplitude = .07;
    @XmlElement(name = "StrokeThicknessVariationWavelength")
    private double strokeThicknessVariationWavelength = 1. / .05;

    /**
     * @return the strokePressureVariationAmplitude
     */
    public double getStrokePressureVariationAmplitude() {
        return this.strokePressureVariationAmplitude;
    }

    /**
     * @param strokePressureVariationAmplitude
     *            the strokePressureVariationAmplitude to set
     */
    public void setStrokePressureVariationAmplitude(
            double strokePressureVariationAmplitude) {
        this.strokePressureVariationAmplitude = strokePressureVariationAmplitude;
    }

    /**
     * @return the strokePressureVariationWavelength
     */
    public double getStrokePressureVariationWavelength() {
        return this.strokePressureVariationWavelength;
    }

    /**
     * @param strokePressureVariationWavelength
     *            the strokePressureVariationWavelength to set
     */
    public void setStrokePressureVariationWavelength(
            double strokePressureVariationWavelength) {
        this.strokePressureVariationWavelength = strokePressureVariationWavelength;
    }

    /**
     * @return the strokeShiftVariationAmplitude
     */
    public double getStrokeShiftVariationAmplitude() {
        return this.strokeShiftVariationAmplitude;
    }

    /**
     * @param strokeShiftVariationAmplitude
     *            the strokeShiftVariationAmplitude to set
     */
    public void setStrokeShiftVariationAmplitude(
            double strokeShiftVariationAmplitude) {
        this.strokeShiftVariationAmplitude = strokeShiftVariationAmplitude;
    }

    /**
     * @return the strokeShiftVariationWavelength
     */
    public double getStrokeShiftVariationWavelength() {
        return this.strokeShiftVariationWavelength;
    }

    /**
     * @param strokeShiftVariationWavelength
     *            the strokeShiftVariationWavelength to set
     */
    public void setStrokeShiftVariationWavelength(
            double strokeShiftVariationWavelength) {
        this.strokeShiftVariationWavelength = strokeShiftVariationWavelength;
    }

    /**
     * @return the strokeThicknessVariationAmplitude
     */
    public double getStrokeThicknessVariationAmplitude() {
        return this.strokeThicknessVariationAmplitude;
    }

    /**
     * @param strokeThicknessVariationAmplitude
     *            the strokeThicknessVariationAmplitude to set
     */
    public void setStrokeThicknessVariationAmplitude(
            double strokeThicknessVariationAmplitude) {
        this.strokeThicknessVariationAmplitude = strokeThicknessVariationAmplitude;
    }

    /**
     * @return the strokeThicknessVariationWavelength
     */
    public double getStrokeThicknessVariationWavelength() {
        return this.strokeThicknessVariationWavelength;
    }

    /**
     * @param strokeThicknessVariationWavelength
     *            the strokeThicknessVariationWavelength to set
     */
    public void setStrokeThicknessVariationWavelength(
            double strokeThicknessVariationWavelength) {
        this.strokeThicknessVariationWavelength = strokeThicknessVariationWavelength;
    }

}
