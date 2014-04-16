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

package fr.ign.cogit.geoxygene.appli.render.texture;

import fr.ign.cogit.geoxygene.appli.render.texture.TileProbability;
import fr.ign.cogit.geoxygene.util.gl.GradientTextureImage;
import fr.ign.cogit.geoxygene.util.gl.GradientTextureImage.TexturePixel;

/**
 * @author JeT
 *         this function return 1 when the pixel distance is between min and
 *         max value. 0 elsewhere
 */
public class DistanceTileProbability implements TileProbability {

    private GradientTextureImage image = null;
    private double distanceMin = 0;
    private double distanceMax = 0;
    private double inRangeProbability = 1;
    private double outRangeProbability = 0;

    /**
     * Constructor
     * 
     * @param image
     * @param probability
     */
    public DistanceTileProbability(GradientTextureImage image, double distanceMin, double distanceMax, double inRangeProbability, double outRangeProbability) {
        super();
        this.image = image;
        this.distanceMin = distanceMin;
        this.distanceMax = distanceMax;
        this.inRangeProbability = inRangeProbability;
        this.outRangeProbability = outRangeProbability;
    }

    /*
     * (non-Javadoc)
     * 
     * @see test.app.TileProbability#getPprobability(double, double)
     */
    @Override
    /**
     *         this functions return 1 when the pixel distance is between min and
     *         max value. 0 elsewhere
     */
    public double getProbability(double x, double y) {
        TexturePixel pixel = this.image.getPixel((int) x, (int) y);
        //        System.err.println("DistanceTileProbability pixel = " + pixel + " distance = " + pixel.distance);
        if (pixel == null || pixel.distance == Double.NaN) {
            return this.outRangeProbability;
        }
        if (pixel.distance >= this.distanceMin && pixel.distance <= this.distanceMax) {
            return this.inRangeProbability;
        }
        return this.outRangeProbability;
    }

}
