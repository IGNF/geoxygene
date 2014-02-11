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

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLComplexFactory;
import fr.ign.cogit.geoxygene.appli.task.AbstractTask;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.util.ColorUtil;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;

/**
 * @author JeT
 * 
 */
public class DisplayablePoint extends AbstractTask implements GLDisplayable {

    private static final Logger logger = Logger.getLogger(DisplayablePoint.class.getName()); // logger
    private static final Colorizer partialColorizer = new SolidColorizer(Color.green);
    private Symbolizer symbolizer = null;
    private List<GLComplex> fullRepresentation = null;
    private GLComplex partialRepresentation = null;
    private final List<IGeometry> geometries = new ArrayList<IGeometry>();
    private long displayCount = 0; // number of time it has been displayed
    private Date lastDisplayTime; // last time it has been displayed
    private Viewport viewport = null;

    /**
     * Constructor
     * 
     * @param name
     * @param viewport
     * @param multiPoints
     * @param symbolizer
     */
    public DisplayablePoint(String name, Viewport viewport, IGeometry geometry, Symbolizer symbolizer) {
        super(name);
        this.symbolizer = symbolizer;
        this.viewport = viewport;
        this.addGeometry(geometry);
    }

    /**
     * @param point
     */
    public final void addGeometry(IGeometry geometry) {
        this.geometries.add(geometry);
    }

    @Override
    public boolean isProgressable() {
        return false;
    }

    @Override
    public boolean isPausable() {
        return false;
    }

    @Override
    public boolean isStoppable() {
        return false;
    }

    /**
     * @return the displayCount
     */
    @Override
    public long getDisplayCount() {
        return this.displayCount;
    }

    /**
     * @return the lastDisplayTime
     */
    @Override
    public Date getLastDisplayTime() {
        return this.lastDisplayTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        super.setState(TaskState.INITIALIZING);
        this.fullRepresentation = null;
        super.setState(TaskState.RUNNING);

        //        if (this.getTexture() != null) {
        //            this.generateWithDistanceField();
        //        }

        if (this.symbolizer instanceof PointSymbolizer) {
            PointSymbolizer pointSymbolizer = (PointSymbolizer) this.symbolizer;
            this.generateWithPointSymbolizer(pointSymbolizer);
        } else {
            super.setState(TaskState.ERROR);
            return;
        }
        super.setState(TaskState.FINALIZING);
        super.setState(TaskState.FINISHED);
    }

    private void generateWithPointSymbolizer(PointSymbolizer symbolizer) {
        List<GLComplex> complexes = new ArrayList<GLComplex>();
        IEnvelope envelope = IGeometryUtil.getEnvelope(this.geometries);
        double minX = envelope.minX();
        double minY = envelope.minY();

        for (Mark mark : symbolizer.getGraphic().getMarks()) {
            Shape markShape = mark.toShape();
            float size = symbolizer.getGraphic().getSize();
            // create the shape fo the mark. daptive transform will be done for each point (location, size, etc...)
            AffineTransform at = new AffineTransform();
            at.scale(size, size);
            markShape = at.createTransformedShape(markShape);

            for (IGeometry geometry : this.geometries) {
                AffineTransform atGeometry = new AffineTransform();
                atGeometry.translate(geometry.centroid().getX(), geometry.centroid().getY());
                Shape markShapeGeometry = atGeometry.createTransformedShape(markShape);
                // TODO: add scale (viewport depend) and rotation
                GLComplex markFillComplex = GLComplexFactory.toGLComplex(markShapeGeometry, minX, minY);
                markFillComplex.setColor(mark.getFill().getColor());
                complexes.add(markFillComplex);

                if (mark.getStroke() != null) {
                    // TODO: add scale (viewport depend) and rotation
                    GLComplex markOutlineComplex = GLComplexFactory.createShapeOutline(markShapeGeometry, mark.getStroke(), minX, minY);
                    markOutlineComplex.setColor(mark.getStroke().getColor());
                    complexes.add(markOutlineComplex);
                } else {
                    logger.warn("Mark point has a null stroke. They won't be renderered until a default stroke is set");
                }
            }

        }
        this.fullRepresentation = complexes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.render.primitive.GLDisplayable#
     * getPartialRepresentation()
     */
    @Override
    public GLComplex getPartialRepresentation() {
        if (this.partialRepresentation == null) {
            IEnvelope envelope = IGeometryUtil.getEnvelope(this.geometries);
            double minX = envelope.minX();
            double minY = envelope.minY();
            this.partialRepresentation = GLComplexFactory.createQuickPoints(this.geometries, partialColorizer, null, minX, minY);
        }
        this.displayIncrement();
        return this.partialRepresentation;
    }

    private void displayIncrement() {
        this.displayCount++;
        this.lastDisplayTime = new Date();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.render.primitive.GLDisplayable#
     * getFullRepresentation()
     */
    @Override
    public Collection<GLComplex> getFullRepresentation() {
        if (this.fullRepresentation != null) {
            this.displayIncrement();
        }
        return this.fullRepresentation;
    }

}
