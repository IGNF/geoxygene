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

import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.TextureImage;
import fr.ign.cogit.geoxygene.appli.gl.TextureImage.TexturePixel;
import fr.ign.cogit.geoxygene.appli.gl.TextureImageUtil;

/**
 * @author JeT
 *         Compute texture coordinates using the distance to the primitive edge
 */
public class DensityFieldParameterizer implements Parameterizer {

    private static final int TEXTURE_WIDTH_HEIGHT = 1024;
    private static Logger logger = Logger.getLogger(DensityFieldParameterizer.class.getName());
    private Viewport viewport = null;
    private ParameterizedPolygon polygon = null;
    private TextureImage image = null;

    /**
     * Constructor
     * 
     * @param shape
     *            shape representing a line
     * @param viewport
     *            viewport in which the shape has been generated
     */
    public DensityFieldParameterizer(final Viewport viewport, final ParameterizedPolygon polygon) {
        this.setViewport(viewport);
        this.setPolygon(polygon);
    }

    public TextureImage getTextureImage() {
        if (this.image == null) {
            // generate the field image
            this.image = new TextureImage(this.polygon, this.viewport);
            this.image.setDimension(TEXTURE_WIDTH_HEIGHT, TEXTURE_WIDTH_HEIGHT);
        }
        return this.image;
    }

    /**
     * @return the viewport
     */
    public final Viewport getViewport() {
        return this.viewport;
    }

    /**
     * @param viewport
     *            the viewport to set
     */
    public final void setViewport(final Viewport viewport) {
        this.viewport = viewport;
    }

    /**
     * @return the polygon
     */
    public final ParameterizedPolygon getPolygon() {
        return this.polygon;
    }

    /**
     * @param polygon
     *            the polygon to set
     */
    public final void setPolygon(ParameterizedPolygon polygon) {
        if (this.polygon == polygon) {
            return;
        }
        this.polygon = polygon;
        this.image = null;  // reinitialize image computation

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.primitive.Parameterizer#initialize()
     */
    @Override
    public void initializeParameterization() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#finalize()
     */
    @Override
    public void finalizeParameterization() {

        //        DecimalFormat df = new DecimalFormat("#.0000");
        //        try {
        //            TextureImageUtil.save(this.getTextureImage(), "./z-" + df.format(this.polygon.getGM_Polygon().area()) + "-polygon");
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.render.primitive.Parameterizer#
     * getTextureCoordinates(double, double)
     */
    /**
     * convert screen coordinates to model (world) coordinates then to texture
     * image coordinates. Image coordinates are directly the texture coordinates
     * (normalized by image size) because source texture has already been
     * applies using applyTexture() method
     */
    @Override
    public Point2d getTextureCoordinates(final double x, final double y) {

        Point2D modelPoint;
        try {
            modelPoint = this.viewport.toModelPoint(new Point2D.Double(x, y));
            Point2D imageCoordinates = this.getTextureImage().worldToProj(modelPoint);
            double xTexture = imageCoordinates.getX() / this.getTextureImage().getWidth();
            double yTexture = imageCoordinates.getY() / this.getTextureImage().getHeight();
            return new Point2d(xTexture, yTexture);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
            return new Point2d(0, 0);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.render.primitive.Parameterizer#
     * getLinearParameter(float, float)
     */
    @Override
    public double getLinearParameter(final double x, final double y) {
        Point2D modelPoint;
        try {
            modelPoint = this.viewport.toModelPoint(new Point2D.Double(x, y));
            Point2D imageCoordinates = this.getTextureImage().worldToProj(modelPoint);
            TexturePixel pixel = this.getTextureImage().getPixel((int) imageCoordinates.getX(), (int) imageCoordinates.getY());
            return pixel.uTexture;
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
            return 0.;
        }
    }

}
