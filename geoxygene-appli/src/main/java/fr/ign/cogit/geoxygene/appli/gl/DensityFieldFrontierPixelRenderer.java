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

package fr.ign.cogit.geoxygene.appli.gl;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.appli.gl.TextureImage.TexturePixel;

/**
 * @author JeT This pixel renderer is used to draw polygon frontiers in a
 *         DensityField Image. It fills a list of x values for all y values
 */
public class DensityFieldFrontierPixelRenderer implements TexturePixelRenderer {

    private final Map<Integer, List<Integer>> ys = new HashMap<Integer, List<Integer>>();
    private final Set<Integer> yValueAlreadyStored = new HashSet<Integer>();
    private int currentFrontier = 0;
    private final Set<Point> modifiedPixels = new HashSet<Point>();
    private boolean isCusp = false;
    private int x1, y1, x2, y2; // segment coordinates
    private double u1, u2; // linear parameterization values
    private double lineLength = 0; // distance between begin and end point

    /**
     * Constructor
     */
    public DensityFieldFrontierPixelRenderer() {
        super();
    }

    /**
     * @return the modifiedPixels
     */
    public final Set<Point> getModifiedPixels() {
        return this.modifiedPixels;
    }

    /**
     * reset the modifiedPixels
     */
    public final void resetModifiedPixels() {
        this.modifiedPixels.clear();
    }

    public int getX1() {
        return this.x1;
    }

    public int getY1() {
        return this.y1;
    }

    public int getX2() {
        return this.x2;
    }

    public int getY2() {
        return this.y2;
    }

    /**
     * @return the ys
     */
    public final Map<Integer, List<Integer>> getYs() {
        return this.ys;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.gl.DensityFieldPixelAction#renderPixel(fr
     * .ign.cogit.geoxygene.appli.gl.DensityFieldImage.DensityFieldPixel)
     */
    /**
     * @return the closestFrontier
     */
    public final int getCurrentFrontier() {
        return this.currentFrontier;
    }

    /**
     * @param currentFrontier
     *            the closestFrontier to set
     */
    public final void setCurrentFrontier(int currentFrontier) {
        this.currentFrontier = currentFrontier;
    }

    /**
     * For each line drawn a list of y values is filled in order to add only one
     * x value for a given Y
     */
    public void newLine() {
        this.yValueAlreadyStored.clear();
    }

    public void setCusp(boolean b) {
        this.isCusp = b;

    }

    @Override
    public void renderPixel(int x, int y, TextureImage image) {
        TexturePixel pixel = image.getPixel(x, y);
        // fills the x-values array of the given y value (create a new one if needed)
        if (this.y1 == this.y2 || !this.isCusp) {
            // do nothing with horizontal lines
            // set one y value for each y but the first y1 value
            // (not to be duplicated with first next line drawing)
            if (y != this.y1 && !this.yValueAlreadyStored.contains(y)) {
                List<Integer> xys = this.ys.get(y);
                if (xys == null) {
                    xys = new ArrayList<Integer>();
                    this.ys.put(y, xys);
                }
                this.yValueAlreadyStored.add(y);
                xys.add(x);
            }
        } else if (this.isCusp) {
            // in case of a cusp (y0 & y2 on the same half space delimited by y1)
            // set 1 value for all y values (included the last y2)
            if (!this.yValueAlreadyStored.contains(y)) {
                List<Integer> xys = this.ys.get(y);
                if (xys == null) {
                    xys = new ArrayList<Integer>();
                    this.ys.put(y, xys);
                }
                this.yValueAlreadyStored.add(y);
                xys.add(x);
            }
        }

        pixel.closestFrontier = this.getCurrentFrontier();
        pixel.distance = 0;
        pixel.frontier = this.getCurrentFrontier();
        double alpha = Math.sqrt((x - this.x1) * (x - this.x1) + (y - this.y1) * (y - this.y1)) / this.lineLength;
        pixel.uTexture = this.u1 + (this.u2 - this.u1) * alpha;
        // fills the list of modified pixels
        this.modifiedPixels.add(new Point(x, y));
    }

    @Override
    public void renderFirstPixel(int x, int y, TextureImage image) {
        this.newLine();
        this.renderPixel(x, y, image);
        //        DensityFieldPixel pixel = image.getPixel(x, y);
        //        pixel.frontier += 10;
    }

    @Override
    public void renderLastPixel(int x, int y, TextureImage image) {
        this.renderPixel(x, y, image);
        //        DensityFieldPixel pixel = image.getPixel(x, y);
        //        pixel.frontier += 100;
    }

    @Override
    public void setCurrentLine(int xi, int yi, int xf, int yf) {
        this.x1 = xi;
        this.y1 = yi;
        this.x2 = xf;
        this.y2 = yf;
        this.lineLength = Math.sqrt((double) (this.x2 - this.x1) * (this.x2 - this.x1) + (double) (this.y2 - this.y1) * (this.y2 - this.y1));

    }

    /**
     * linear parameterization associated with the current line to render
     * 
     * @param ui
     * @param uf
     */
    public void setLinearParameterization(double ui, double uf) {
        this.u1 = ui;
        this.u2 = uf;
    }

}
