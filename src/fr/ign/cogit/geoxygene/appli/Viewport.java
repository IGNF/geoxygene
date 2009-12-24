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

package fr.ign.cogit.geoxygene.appli;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Viewport associated with a {@link LayerViewPanel}. This class is responsible
 * for the transformation
 * between view coordinates and model coordinates.
 * 
 * @author Julien Perret
 */
public class Viewport {
        static Logger logger = Logger.getLogger(Viewport.class.getName());
        /**
         * The factor used for zooming the viewport, i.e. the factor used to
         * multiply the panel size when zooming in and out
         */
        private static double ZOOM_FACTOR = 1.2;
        /**
         * The factor used for moving the viewport, i.e. the factor used to
         * multiply the panel size when moving up, down, right and left
         */
        private static double MOVE_FACTOR = 0.2;
        /**
         * The view origin, i.e. the upper left corner of the panel in model
         * coordinates
         */
        private Point2D viewOrigin = new Point2D.Double(0, 0);

        private LayerViewPanel layerViewPanel = null;

        /**
         * @return The {@link LayerViewPanel} associated with the viewport
         */
        public LayerViewPanel getLayerViewPanel() {
                return this.layerViewPanel;
        }

        private double scale = 1;

        /**
         * @return The scale of the viewport
         */
        public double getScale() {
                return this.scale;
        }

        private AffineTransform modelToViewTransform = null;

        /**
         * @return The {@link AffineTransform} corresponding to the
         *         transformation from model to view coordinates.
         * @throws NoninvertibleTransformException
         * @see #update()
         */
        public AffineTransform getModelToViewTransform()
                        throws NoninvertibleTransformException {
                if (this.modelToViewTransform == null) {
                        update();
                }
                return this.modelToViewTransform;
        }

        /**
         * Update the model to view transformation and repaint the panel.
         * 
         * @throws NoninvertibleTransformException
         * @see #modelToViewTransform
         */
        public void update() throws NoninvertibleTransformException {
                this.modelToViewTransform = modelToViewTransform(this.scale,
                                this.viewOrigin, this.layerViewPanel
                                                .getHeight());
                this.layerViewPanel.repaint();
        }

        /**
         * Compute the {@link AffineTransform} corresponding to the
         * transformation from model to view coordinates.
         * 
         * @param scale
         * @param viewOrigin
         * @param height
         * @return The {@link AffineTransform} corresponding to the
         *         transformation from model to view coordinates.
         * @see #getEnvelopeInModelCoordinates()
         * @see #update()
         */
        public static AffineTransform modelToViewTransform(double scale,
                        Point2D viewOrigin, double height) {
                AffineTransform modelToViewTransform = new AffineTransform();
                modelToViewTransform.translate(0, height);
                modelToViewTransform.scale(1, -1);
                modelToViewTransform.scale(scale, scale);
                modelToViewTransform.translate(-viewOrigin.getX(), -viewOrigin
                                .getY());
                return modelToViewTransform;
        }

        /**
         * Constructor of viewport with a {@link LayerViewPanel}.
         * 
         * @param theLayerViewPanel
         *                the layer associated with the viewport
         */
        public Viewport(LayerViewPanel theLayerViewPanel) {
                this.layerViewPanel = theLayerViewPanel;
        }

        /**
         * @return The envelope of the panel in model coordinates.
         */
        public GM_Envelope getEnvelopeInModelCoordinates() {
                double widthAsPerceivedByModel = this.layerViewPanel.getWidth()
                                / this.scale;
                double heightAsPerceivedByModel = this.layerViewPanel
                                .getHeight()
                                / this.scale;
                return new GM_Envelope(this.viewOrigin.getX(), this.viewOrigin
                                .getX()
                                + widthAsPerceivedByModel, this.viewOrigin
                                .getY(), this.viewOrigin.getY()
                                + heightAsPerceivedByModel);
        }

        /**
         * @return The envelope of the panel in model coordinates.
         */
        public GM_Envelope getEnvelopeInModelCoordinates(int x, int y,
                        int width, int height) {
                double xAsPerceivedByModel = x / this.scale;
                double yAsPerceivedByModel = y / this.scale;
                double widthAsPerceivedByModel = width / this.scale;
                double heightAsPerceivedByModel = height / this.scale;
                return new GM_Envelope(this.viewOrigin.getX()
                                + xAsPerceivedByModel,
                                this.viewOrigin.getX() + xAsPerceivedByModel
                                                + widthAsPerceivedByModel,
                                this.viewOrigin.getY() + yAsPerceivedByModel,
                                this.viewOrigin.getY() + yAsPerceivedByModel
                                                + heightAsPerceivedByModel);
        }

        /**
         * @param geometry
         *                a geometry
         * @return A shape representing the geometry in view coordinates
         * @throws NoninvertibleTransformException
         */
        public Shape toShape(GM_Object geometry)
                        throws NoninvertibleTransformException {
                GM_Envelope envelope = this.getEnvelopeInModelCoordinates();
                try {
                        GM_Envelope geometryEnvelope = geometry.envelope();
                        // if the geometry does not intersect the envelope of
                        // the view, return a null shape
                        if (!envelope.intersects(geometryEnvelope))
                                return null;
                        if (geometry.isEmpty())
                                return null;
                        if (geometry.isPolygon())
                                return toShape((GM_Polygon) geometry);
                        if (geometry.isMultiSurface())
                                return null;
                        if (geometry.isLineString())
                                return toShape((GM_LineString) geometry);
                        if (geometry.isMultiCurve())
                                return null;
                        if (geometry.isPoint())
                                return toShape((GM_Point) geometry);
                        if (geometry instanceof GM_Aggregate<?>)
                                return null;
                        throw new IllegalArgumentException(
                                        I18N
                                                        .getString("Viewport.UnhandledGeometryClass") + geometry.getClass()); //$NON-NLS-1$
                } catch (Exception e) {
                        logger
                                        .info(I18N
                                                        .getString("Viewport.Geometry") + geometry); //$NON-NLS-1$
                        e.printStackTrace();
                        return null;
                }
        }

        /**
         * @param p
         *                a polygon
         * @return A shape representing the polygon in view coordinates
         * @throws NoninvertibleTransformException
         */
        private Shape toShape(GM_Polygon p)
                        throws NoninvertibleTransformException {
                DirectPositionList viewDirectPositionList = toViewDirectPositionList(p
                                .getExterior().coord());
                if (viewDirectPositionList.isEmpty())
                        return null;
                DirectPosition lastExteriorRingDirectPosition = viewDirectPositionList
                                .get(viewDirectPositionList.size() - 1);
                for (int i = 0; i < p.sizeInterior(); i++) {
                        viewDirectPositionList
                                        .addAll(toViewDirectPositionList(p
                                                        .getInterior(i).coord()));
                        viewDirectPositionList
                                        .add(lastExteriorRingDirectPosition);
                }
                return toPolygonShape(viewDirectPositionList);
        }

        /**
         * @param viewDirectPositionList
         * @return A shape representing the polygon in view coordinates
         */
        private Shape toPolygonShape(DirectPositionList viewDirectPositionList) {
                int numPoints = viewDirectPositionList.size();
                int[] xpoints = new int[numPoints];
                int[] ypoints = new int[numPoints];
                for (int i = 0; i < viewDirectPositionList.size(); i++) {
                        DirectPosition p = viewDirectPositionList.get(i);
                        xpoints[i] = (int) p.getX();
                        ypoints[i] = (int) p.getY();
                }
                return new Polygon(xpoints, ypoints, numPoints);
        }

        /**
         * @param modelDirectPositionList
         * @return a DirectPositionList of DirectPosition in the screen
         *         coordinate system corresponding to the given
         *         DirectPositionList in model coordinate system
         * @throws NoninvertibleTransformException
         */
        public DirectPositionList toViewDirectPositionList(
                        DirectPositionList modelDirectPositionList)
                        throws NoninvertibleTransformException {
                DirectPositionList viewDirectPositionList = new DirectPositionList();
                if (modelDirectPositionList.isEmpty())
                        return viewDirectPositionList;
                double ps = 0.5 / getScale(); // convert in model units
                DirectPosition p0 = modelDirectPositionList.get(0);
                int npts = 0;
                int mpts = modelDirectPositionList.size();
                for (int i = 0; i < mpts; i++) {
                        DirectPosition pi = modelDirectPositionList.get(i);
                        // inline Decimator
                        double xd = Math.abs(p0.getX() - pi.getX());
                        double yd = Math.abs(p0.getY() - pi.getY());
                        if ((xd >= ps) || (yd >= ps) || (npts < 4)
                                        || (i == mpts - 1)) {
                                Point2D point2D = toViewPoint(pi);
                                viewDirectPositionList
                                                .add(new DirectPosition(point2D
                                                                .getX(),
                                                                point2D.getY()));
                                p0 = pi;
                                npts++;
                        }
                }
                if (npts != mpts) {
                        while (viewDirectPositionList.size() > npts)
                                viewDirectPositionList
                                                .remove(viewDirectPositionList
                                                                .size() - 1);
                        return viewDirectPositionList;
                }
                return viewDirectPositionList;
        }

        /**
         * @param modelDirectPosition
         * @return a Point2D (on the screen) corresponding to the given
         *         DirectPosition in model coordinate system
         */
        public Point2D toViewPoint(DirectPosition modelDirectPosition)
                        throws NoninvertibleTransformException {
                Point2D.Double pt = new Point2D.Double(modelDirectPosition
                                .getX(), modelDirectPosition.getY());
                return getModelToViewTransform().transform(pt, pt);
        }

        /**
         * @param lineString
         * @return a GeneralPath representing the given linestring as an AWT
         *         shape
         * @throws NoninvertibleTransformException
         */
        private GeneralPath toShape(GM_LineString lineString)
                        throws NoninvertibleTransformException {
                DirectPositionList viewPositionList = toViewDirectPositionList(lineString
                                .coord());
                GeneralPath shape = new GeneralPath();
                DirectPosition p = viewPositionList.get(0);
                shape.moveTo(p.getX(), p.getY());
                for (int i = 1; i < viewPositionList.size(); i++) {
                        p = viewPositionList.get(i);
                        shape.lineTo(p.getX(), p.getY());
                }
                return shape;
        }

        /**
         * @param point
         * @return a GeneralPath representing the given point as an AWT shape
         * @throws NoninvertibleTransformException
         */
        private GeneralPath toShape(GM_Point point)
                        throws NoninvertibleTransformException {
                Point2D p = toViewPoint(point.getPosition());
                GeneralPath shape = new GeneralPath();
                shape.moveTo(p.getX(), p.getY());
                return shape;
        }

        /**
         * Zoom to full extent, i.e. to view all the layers of the associated
         * panel
         * 
         * @throws NoninvertibleTransformException
         */
        public void zoomToFullExtent() throws NoninvertibleTransformException {
                zoom(this.layerViewPanel.getEnvelope());
        }

        /**
         * @param extent
         * @throws NoninvertibleTransformException
         */
        public void zoom(GM_Envelope extent)
                        throws NoninvertibleTransformException {
                if (extent.isEmpty()) {
                        return;
                }
                this.scale = Math.min(this.layerViewPanel.getWidth()
                                / extent.width(), this.layerViewPanel
                                .getHeight()
                                / extent.length());
                double xCenteringOffset = ((this.layerViewPanel.getWidth() / this.scale) - extent
                                .width()) / 2d;
                double yCenteringOffset = ((this.layerViewPanel.getHeight() / this.scale) - extent
                                .length()) / 2d;
                this.viewOrigin = new Point2D.Double(extent.minX()
                                - xCenteringOffset, extent.minY()
                                - yCenteringOffset);
                update();
        }

        /**
         * @param p
         * @param factor
         * @throws NoninvertibleTransformException
         */
        public void zoom(Point2D p, double factor)
                        throws NoninvertibleTransformException {
                Point2D zoomPoint = toModelPoint(p);
                GM_Envelope modelEnvelope = getEnvelopeInModelCoordinates();
                DirectPosition centre = modelEnvelope.center();
                double width = modelEnvelope.width();
                double height = modelEnvelope.length();
                double dx = (zoomPoint.getX() - centre.getX()) / factor;
                double dy = (zoomPoint.getY() - centre.getY()) / factor;
                GM_Envelope zoomModelEnvelope = new GM_Envelope(zoomPoint
                                .getX()
                                - (0.5 * (width / factor)) - dx, zoomPoint
                                .getX()
                                + (0.5 * (width / factor)) - dx, zoomPoint
                                .getY()
                                - (0.5 * (height / factor)) - dy, zoomPoint
                                .getY()
                                + (0.5 * (height / factor)) - dy);
                zoom(zoomModelEnvelope);
        }

        /**
         * @param x
         * @param y
         * @param widthOfNewView
         * @param heightOfNewView
         * @throws NoninvertibleTransformException
         */
        public void zoom(int x, int y, double widthOfNewView,
                        double heightOfNewView)
                        throws NoninvertibleTransformException {
                double zoomFactor = Math.min(this.layerViewPanel.getWidth()
                                / widthOfNewView, this.layerViewPanel
                                .getHeight()
                                / heightOfNewView);
                double realWidthOfNewView = this.layerViewPanel.getWidth()
                                / zoomFactor;
                double realHeightOfNewView = this.layerViewPanel.getHeight()
                                / zoomFactor;
                GM_Envelope zoomEnvelope;
                try {
                        zoomEnvelope = toModelEnvelope(x
                                        - (0.5 * realWidthOfNewView), x
                                        + (0.5 * realWidthOfNewView), y
                                        - (0.5 * realHeightOfNewView), y
                                        + (0.5 * realHeightOfNewView));
                } catch (NoninvertibleTransformException ex) {
                        zoomToFullExtent();
                        return;
                }
                zoom(zoomEnvelope);
        }

        /**
         * @param xMin
         * @param xMax
         * @param yMin
         * @param yMax
         * @return the envelope in model coordinate system containing
         *         xMin,xMax,yMin,yMax
         * @throws NoninvertibleTransformException
         */
        private GM_Envelope toModelEnvelope(double xMin, double xMax,
                        double yMin, double yMax)
                        throws NoninvertibleTransformException {
                return new GM_Envelope(
                                toModelDirectPosition(new Point2D.Double(xMax,
                                                yMin)),
                                toModelDirectPosition(new Point2D.Double(xMin,
                                                yMax)));
        }

        /**
         * @param viewPoint
         *                a 2D point
         * @return a DirectPosition in the model coordinate system corresponding
         *         to the viewPoint (on the screen)
         * @throws NoninvertibleTransformException
         */
        public DirectPosition toModelDirectPosition(Point2D viewPoint)
                        throws NoninvertibleTransformException {
                Point2D p = toModelPoint(viewPoint);
                return new DirectPosition(p.getX(), p.getY());
        }

        /**
         * @param viewPoint
         *                a 2D point
         * @return a Point2D in the model coordinate system corresponding to the
         *         viewPoint (on the screen)
         * @throws NoninvertibleTransformException
         */
        public Point2D toModelPoint(Point2D viewPoint)
                        throws NoninvertibleTransformException {
                return getModelToViewTransform().inverseTransform(
                                toPoint2DDouble(viewPoint), null);
        }

        /**
         * Conversion function from any Point2D to the equivalent Point2D.Double
         * 
         * @param p
         *                a 2D point
         * @return a correspondin Point2D.Double
         */
        private Point2D.Double toPoint2DDouble(Point2D p) {
                if (p instanceof Point2D.Double) {
                        return (Point2D.Double) p;
                }
                return new Point2D.Double(p.getX(), p.getY());
        }

        /**
         * @throws NoninvertibleTransformException
         */
        public void zoomIn() throws NoninvertibleTransformException {
                GM_Envelope envelope = this.getEnvelopeInModelCoordinates();
                envelope.expandBy(1 / ZOOM_FACTOR, 1 / ZOOM_FACTOR);
                this.zoom(envelope);
        }

        /**
         * @throws NoninvertibleTransformException
         */
        public void zoomOut() throws NoninvertibleTransformException {
                GM_Envelope envelope = this.getEnvelopeInModelCoordinates();
                envelope.expandBy(ZOOM_FACTOR, ZOOM_FACTOR);
                this.zoom(envelope);
        }

        /**
         * @param p
         * @throws NoninvertibleTransformException
         */
        public void zoomInTo(Point2D p) throws NoninvertibleTransformException {
                this.zoom(p, ZOOM_FACTOR);
        }

        /**
         * @param p
         * @throws NoninvertibleTransformException
         */
        public void zoomOutTo(Point2D p) throws NoninvertibleTransformException {
                this.zoom(p, 1 / ZOOM_FACTOR);
        }

        /**
         * @throws NoninvertibleTransformException
         */
        public void moveUp() throws NoninvertibleTransformException {
                this.moveOf(0, this.layerViewPanel.getHeight() * MOVE_FACTOR
                                / this.scale);
        }

        /**
         * @throws NoninvertibleTransformException
         */
        public void moveDown() throws NoninvertibleTransformException {
                this.moveOf(0, -this.layerViewPanel.getHeight() * MOVE_FACTOR
                                / this.scale);
        }

        /**
         * @throws NoninvertibleTransformException
         */
        public void moveRight() throws NoninvertibleTransformException {
                this.moveOf(this.layerViewPanel.getWidth() * MOVE_FACTOR
                                / this.scale, 0);
        }

        /**
         * @throws NoninvertibleTransformException
         */
        public void moveLeft() throws NoninvertibleTransformException {
                this.moveOf(-this.layerViewPanel.getWidth() * MOVE_FACTOR
                                / this.scale, 0);
        }

        /**
         * @param x
         * @param y
         * @throws NoninvertibleTransformException
         */
        public void moveOf(double x, double y)
                        throws NoninvertibleTransformException {
                this.viewOrigin.setLocation(this.viewOrigin.getX() + x,
                                this.viewOrigin.getY() + y);
                update();
        }

        /**
         * @param point
         */
        public void moveTo(Point point) throws NoninvertibleTransformException {
                Point2D modelPoint = toModelPoint(point);
                modelPoint.setLocation(modelPoint.getX()
                                - this.layerViewPanel.getWidth() * 0.5
                                / this.scale, modelPoint.getY()
                                - this.layerViewPanel.getHeight() * 0.5
                                / this.scale);
                this.viewOrigin.setLocation(modelPoint);
                update();
        }
}
