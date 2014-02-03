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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.util.gl.TextureImage;
import fr.ign.cogit.geoxygene.util.gl.TextureImage.TexturePixel;

/**
 * @author JeT
 * 
 */
public class TextureImageSampler implements SamplingAlgorithm {

    private TextureImage image = null;
    private double scale = 1;
    private double sampleX = 1;
    private double sampleY = 1;
    private List<Sample> samples = null;

    /**
     * Default constructor
     */
    public TextureImageSampler(TextureImage image, double sampleX, double sampleY, double scale) {
        this.image = image;
        this.scale = scale;
        this.sampleX = sampleX;
        this.sampleY = sampleY;
        this.computeSamples();
    }

    /**
     * @return the scale
     */
    public double getScale() {
        return this.scale;
    }

    /**
     * @param scale
     *            the scale to set
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    private void computeSamples() {
        double xSampleRate = this.image.getWidth() / this.sampleX / this.scale;
        double ySampleRate = this.image.getHeight() / this.sampleY / this.scale;
        this.samples = new ArrayList<Sample>();
        this.samples.clear();
        for (double y = 0; y < this.image.getHeight(); y += ySampleRate) {
            for (double x = 0; x < this.image.getWidth(); x += xSampleRate) {
                TexturePixel pixel = this.image.getPixel((int) x, (int) y);
                if (pixel.in) {
                    this.samples.add(new Sample(x, y));
                }
            }
        }
    }

    /**
     * @return the samples
     */
    public List<Sample> getSamples() {
        if (this.samples == null) {
            this.computeSamples();
        }
        return this.samples;
    }

    /*
     * (non-Javadoc)
     * 
     * @see test.app.SamplingAlgorithm#getSample()
     */
    @Override
    public Iterator<Sample> getSampleIterator() {
        return this.getSamples().iterator();
    }

}
