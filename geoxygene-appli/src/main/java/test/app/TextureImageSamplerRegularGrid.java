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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point2d;

import fr.ign.cogit.geoxygene.util.gl.TextureImage;
import fr.ign.cogit.geoxygene.util.gl.TextureImage.TexturePixel;

/**
 * @author JeT
 * 
 */
public class TextureImageSamplerRegularGrid implements SamplingAlgorithm {

    private static final Point2d unitScaleFactor = new Point2d(1., 1.);
    private TextureImage image = null;
    private double scale = 1;
    private double sampleX = 1;
    private double sampleY = 1;
    private List<Sample> samples = null;
    private double jitteringFactor = 0.2;
    private TileChooser tileChooser = null;

    /**
     * Default constructor
     */
    public TextureImageSamplerRegularGrid(TextureImage image, double sampleX, double sampleY, double scale) {
        this.image = image;
        this.scale = scale;
        this.sampleX = sampleX;
        this.sampleY = sampleY;
    }

    /**
     * Default constructor
     */
    public TextureImageSamplerRegularGrid(TextureImage image, double sampleX, double sampleY, double scale, TileChooser tileChooser) {
        this.image = image;
        this.scale = scale;
        this.sampleX = sampleX;
        this.sampleY = sampleY;
        this.tileChooser = tileChooser;
    }

    /**
     * @return the jitteringFactor
     */
    public double getJitteringFactor() {
        return this.jitteringFactor;
    }

    /**
     * @param jitteringFactor
     *            the jitteringFactor to set
     */
    public void setJitteringFactor(double jitteringFactor) {
        this.jitteringFactor = jitteringFactor;
        this.invalidateSamples();
    }

    private void invalidateSamples() {
        this.samples = null;
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
        this.invalidateSamples();
    }

    private void computeSamples() {
        Random rand = new Random(0);
        double xSampleRate = this.image.getWidth() / this.sampleX / this.scale;
        double ySampleRate = this.image.getHeight() / this.sampleY / this.scale;
        this.samples = new ArrayList<Sample>();
        this.samples.clear();
        for (double y = 0; y < this.image.getHeight(); y += ySampleRate) {
            for (double x = 0; x < this.image.getWidth(); x += xSampleRate) {
                TexturePixel pixel = this.image.getPixel((int) x, (int) y);
                if (pixel.in) {
                    double jitterX = 0.;
                    double jitterY = 0.;
                    if (this.getJitteringFactor() > 0.01) {
                        jitterX = (rand.nextDouble() * 2 - 1) * xSampleRate * this.getJitteringFactor();
                        jitterY = (rand.nextDouble() * 2 - 1) * ySampleRate * this.getJitteringFactor();
                    }
                    Point2d location = new Point2d(x + jitterX, y + jitterY);
                    Point2d rotation = new Point2d(pixel.vGradient.x, pixel.vGradient.y);
                    Sample sample = new Sample(location, rotation, unitScaleFactor, null);
                    if (this.tileChooser != null) {
                        Tile tile = this.tileChooser.getTile(sample);
                        if (tile != null) {
                            sample.setTile(tile);
                            this.samples.add(sample);
                        }
                    } else {
                        this.samples.add(sample);
                    }
                }
            }
        }
        this.jitterSamples(xSampleRate, ySampleRate);
    }

    private void jitterSamples(double xSampleRate, double ySampleRate) {
        Random rand = new Random(0);
        for (Sample sample : this.samples) {
            double dx = rand.nextDouble() * xSampleRate * this.jitteringFactor;
            double dy = rand.nextDouble() * ySampleRate * this.jitteringFactor;
            sample.getLocation().x += dx;
            sample.getLocation().y += dy;
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
