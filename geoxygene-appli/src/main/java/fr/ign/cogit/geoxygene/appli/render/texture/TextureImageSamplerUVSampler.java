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

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.vecmath.Point2d;

import fr.ign.cogit.geoxygene.appli.gl.GradientTextureImage;
import fr.ign.cogit.geoxygene.appli.gl.GradientTextureImage.TexturePixel;
import fr.ign.cogit.geoxygene.util.gl.Sample;

/**
 * @author JeT
 * 
 */
public class TextureImageSamplerUVSampler implements SamplingAlgorithm {

    private GradientTextureImage image = null;
    private double scale = 1;
    private double vDistanceInPixels = 50;
    private double minDistanceInPixels = 50;
    private List<Sample> samples = null;
    private double jitteringFactor = 0.5;

    //    private double jitteringFactor = 0.;

    /**
     * Default constructor
     */
    public TextureImageSamplerUVSampler(GradientTextureImage image, double minDistanceInPixels, double vDistanceInPixels) {
        this.image = image;
        this.vDistanceInPixels = vDistanceInPixels;
        this.minDistanceInPixels = minDistanceInPixels;
    }

    /**
     * @return the jitteringFactor
     */
    public double getJitteringFactor() {
        return this.jitteringFactor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.texture.SamplingAlgorithm#getSampleCount
     * ()
     */
    @Override
    public int getSampleCount() {
        return this.getSamples() == null ? 0 : this.getSamples().size();
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

    //    private void computeSamples() {
    //        this.samples = new ArrayList<Sample>();
    //        this.samples.clear();
    //        for (int y = 0; y < this.image.getHeight(); y++) {
    //            Double previousVDistanceInPixels = null;
    //            for (int x = 0; x < this.image.getWidth(); x++) {
    //                this.tryToAddSample(x, y, previousVDistanceInPixels);
    //            }
    //        }
    //    }

    private void computeSamples() {
        this.samples = new ArrayList<Sample>();
        this.samples.clear();
        // fill the list of pixels to treat
        Set<Point> pixelsToTreat = new HashSet<Point>();

        for (int y = 0; y < this.image.getHeight(); y++) {
            for (int x = 0; x < this.image.getWidth(); x++) {
                TexturePixel pixel = this.image.getPixel(x, y);
                pixel.weightSum = 1; // weightSum is used to mark pixels as treated when = 0 (Ugly)
                if (pixel.in && pixel.frontier != 0 && pixel.distance < 0.0001) {
                    pixelsToTreat.add(new Point(x, y));
                }
            }
        }
        while (!pixelsToTreat.isEmpty()) {
            Set<Point> neighborhood = new HashSet<Point>();

            for (Point p : pixelsToTreat) {
                TexturePixel pixel = this.image.getPixel(p.x, p.y);
                pixel.weightSum = 0;
                //                this.tryToAddSample(p.x, p.y, pixel.vGradient.x, pixel.vGradient.y);
                this.tryToAddSampleMinimizeCoverage(p.x, p.y, pixel.vGradient.x, pixel.vGradient.y);
                this.tryToAddNeighbor(p.x + 1, p.y, neighborhood);
                this.tryToAddNeighbor(p.x - 1, p.y, neighborhood);
                this.tryToAddNeighbor(p.x, p.y + 1, neighborhood);
                this.tryToAddNeighbor(p.x, p.y - 1, neighborhood);
            }
            pixelsToTreat = neighborhood;
        }
        this.jitterSamples();
        //        System.err.println(this.samples.size() + " samples created");
    }

    private void jitterSamples() {
        Random rand = new Random(0);
        for (Sample sample : this.samples) {
            double dx = rand.nextDouble() * this.minDistanceInPixels * this.jitteringFactor;
            double dy = rand.nextDouble() * this.minDistanceInPixels * this.jitteringFactor;
            sample.getLocation().setLocation(sample.getLocation().getX() + dx, sample.getLocation().getY() + dy);
        }

    }

    private void tryToAddNeighbor(int x, int y, Set<Point> neighborhood) {
        TexturePixel pixel = this.image.getPixel(x, y);
        if (pixel != null && pixel.in && pixel.weightSum > 0.5) {
            neighborhood.add(new Point(x, y));
            pixel.weightSum = 0;

        }

    }

    /**
     * @param y
     * @param previousVDistanceInPixels
     * @param x
     * @param uGradient
     * @param vGradient
     */
    private boolean tryToAddSample(int x, int y, double xGradient, double yGradient) {
        TexturePixel pixel = this.image.getPixel(x, y);
        if (pixel != null && pixel.in) {
            if (this.isDistanceStep(pixel, x + 1, y) || this.isDistanceStep(pixel, x - 1, y) || this.isDistanceStep(pixel, x, y + 1)
                    || this.isDistanceStep(pixel, x, y - 1)) {
                if (this.minDistanceToSamples(x, y) > this.minDistanceInPixels) {
                    this.samples.add(new Sample(new Point2D.Double(x, y), new Point2D.Double(xGradient, yGradient), null));
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * @param y
     * @param previousVDistanceInPixels
     * @param x
     * @param uGradient
     * @param vGradient
     */
    private boolean tryToAddSampleMinimizeCoverage(int x, int y, double xGradient, double yGradient) {
        TexturePixel pixel = this.image.getPixel(x, y);
        if (pixel != null && pixel.in) {
            if (this.isDistanceStep(pixel, x + 1, y) || this.isDistanceStep(pixel, x - 1, y) || this.isDistanceStep(pixel, x, y + 1)
                    || this.isDistanceStep(pixel, x, y - 1)) {
                if (this.minDistanceToSamples(x, y) > this.minDistanceInPixels) {
                    this.samples.add(new Sample(new Point2D.Double(x, y), new Point2D.Double(xGradient, yGradient), null));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isDistanceStep(TexturePixel pixel, int x, int y) {
        TexturePixel neighbor = this.image.getPixel(x, y);
        if (neighbor == null || !neighbor.in || neighbor.distance == Double.POSITIVE_INFINITY || neighbor.distance == Double.NaN) {
            return false;
        }
        double nNeighbor = (int) Math.max(0, neighbor.distance / this.vDistanceInPixels + 0.5);
        double nPixel = (int) Math.max(0, pixel.distance / this.vDistanceInPixels + 0.5);
        return nNeighbor != nPixel;
    }

    private double minDistanceToSamples(double x, double y) {
        double min = Double.POSITIVE_INFINITY;
        for (Sample sample : this.samples) {
            double d = Math.sqrt((x - sample.getLocation().getX()) * (x - sample.getLocation().getX()) + (y - sample.getLocation().getY())
                    * (y - sample.getLocation().getY()));
            if (d < min) {
                min = d;
            }
        }
        return min;
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
