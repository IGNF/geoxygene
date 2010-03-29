/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.style;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import javax.xml.bind.annotation.XmlElement;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;

/**
 * @author Julien Perret
 *
 */
public class RasterSymbolizer extends AbstractSymbolizer {

    @XmlElement(name = "ShadedRelief")
    ShadedRelief shadedRelief = null;

	public ShadedRelief getShadedRelief() {
        return this.shadedRelief;
    }

    public void setShadedRelief(ShadedRelief shadedRelief) {
        this.shadedRelief = shadedRelief;
    }
    
    @XmlElement(name = "ColorMap")
    ColorMap colorMap = null;

    public ColorMap getColorMap() {
        return this.colorMap;
    }

    public void setColorMap(ColorMap colorMap) {
        this.colorMap = colorMap;
    }

    @Override
	public boolean isRasterSymbolizer() { return true; }

	@Override
	public void paint(FT_Feature feature, Viewport viewport, Graphics2D graphics) {
        BufferedImage image = viewport.getLayerViewPanel().getProjectFrame().getImage(feature);
        if (image == null) {
            return;
        }
        GM_Envelope envelope = feature.getGeom().envelope();

        if (this.shadedRelief != null) {
            //double reliefFactor = this.shadedRelief.getReliefFactor();
            // TODO use reliefFactor
            int dimensionX = image.getWidth();
            int dimensionY = image.getHeight();
            double width = envelope.width();
            double height = envelope.length();
            double dx = width / dimensionX;
            double dy = height / dimensionY;
            Raster raster = image.getData();
            GM_MultiSurface<GM_Triangle> multi = new GM_MultiSurface<GM_Triangle>();
            //GM_PointGrid pointGrid = new GM_PointGrid();
            DirectPosition[][] positions= new DirectPosition[dimensionX][dimensionY];
            for (int x = 0; x < dimensionX; x++) {
                //DirectPositionList row = new DirectPositionList();
                double pointx = envelope.minX() + (0.5 + x) * dx;
                for (int y = 0; y < dimensionY; y++) {
                    double pointy = envelope.maxY() - (0.5 + y) * dy;
                    double[] value = raster.getPixel(x, y, new double[1]);
//                    System.out.println("-> " + pointx + " " + pointy+ " " + value[0]);
                    //row.add(new DirectPosition(pointx, pointy, value[0]));
                    positions[x][y] = new DirectPosition(pointx, pointy, value[0]);
                    if (y > 0 && x > 0) {
                        multi.add(new GM_Triangle(positions[x -1][y - 1], positions[x - 1][y], positions[x][y]));
                        multi.add(new GM_Triangle(positions[x -1][y - 1], positions[x][y], positions[x][y - 1]));
                   }
                }
                //pointGrid.addRow(row);
            }
            //GM_GriddedSurface grid = new GM_GriddedSurface(pointGrid);
            for (GM_Triangle triangle : multi) {
                draw(viewport, graphics, triangle);
            }
            return;
        }
        BufferedImage imageToDraw = image;
        if (this.colorMap != null) {
            imageToDraw = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Raster raster = image.getData();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    double[] value = raster.getPixel(x, y, new double[1]);
                    imageToDraw.setRGB(x, y, this.colorMap.getColor(value[0]));
                }
            }
        }
        
        try {
            Shape shape = viewport.toShape(envelope.getGeom());
            double minX = shape.getBounds().getMinX();
            double minY = shape.getBounds().getMinY();
            double maxX = shape.getBounds().getMaxX();
            double maxY = shape.getBounds().getMaxY();
            graphics.drawImage(imageToDraw, (int) minX, (int) minY,
                    (int) (maxX - minX), (int) (maxY - minY), null);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
            return;
        }
	}

    private void draw(Viewport viewport, Graphics2D graphics, GM_Triangle triangle) {
        if (!viewport.getEnvelopeInModelCoordinates().getGeom().intersects(triangle)) {
            return;
        }
        Vecteur light1 = new Vecteur(1,1,-1);
        light1.normalise();
        Vecteur v1 = new Vecteur(triangle.getCorners(0), triangle.getCorners(1)); 
        Vecteur v2 = new Vecteur(triangle.getCorners(0), triangle.getCorners(2));
        Vecteur normal = v2.prodVectoriel(v1);
        normal.normalise();
        double diffuseIntesity1 = light1.prodScalaire(normal);
        if (diffuseIntesity1 < 0) {
            diffuseIntesity1 = 0;
        }
        Vecteur light2 = new Vecteur(-1,1,-1);
        light2.normalise();
        double diffuseIntesity2 = light2.prodScalaire(normal);
        try {
            Shape shape = viewport.toShape(triangle);
            graphics.setColor(new Color(
                    (float)(diffuseIntesity1+diffuseIntesity2)/2,
                    (float)(diffuseIntesity1+diffuseIntesity2)/2,
                    (float)diffuseIntesity1/2));
            graphics.fill(shape);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
            return;
        }
    }
}
