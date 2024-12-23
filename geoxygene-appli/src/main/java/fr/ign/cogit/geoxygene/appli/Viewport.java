/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IBezier;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * Viewport associated with a {@link LayerViewPanel}. This class is responsible
 * for the transformation between view coordinates and model coordinates.
 * 
 * @author Julien Perret
 */
public class Viewport {
    /** The logger. */
    private static Logger logger = Logger.getLogger(Viewport.class.getName());
    /**
     * The factor used for zooming the viewport, i.e. the factor used to
     * multiply the panel size when zooming in and out.
     */
    private static final double ZOOM_FACTOR = 1.2;
    /**
     * The factor used for moving the viewport, i.e. the factor used to multiply
     * the panel size when moving up, down, right and left.
     */
    private static final double MOVE_FACTOR = 0.2;
    /**
     * The view origin, i.e. the upper left corner of the panel in model
     * coordinates.
     */
    private Point2D viewOrigin = new Point2D.Double(0, 0);
    /** The layer view panels. */
    private final Collection<LayerViewPanel> layerViewPanels = new ArrayList<LayerViewPanel>(
            0);
    /**
     * The number of pixels used to approximate a curve. It is used both when
     * transforming a curve to a linestring (especially for rendering).
     */
    private double spacingInPixels = 10;

    /**
     * @return The number of pixels used to approximate a curve. It is used both
     *         when transforming a curve to a linestring (especially for
     *         rendering).
     */
    public double getSpacingInPixels() {
        return this.spacingInPixels;
    }

    /**
     * Set the number of pixels used to approximate a curve. It is used both
     * when transforming a curve to a linestring (especially for rendering).
     * 
     * @param spacingInPixels
     *            The number of pixels used to approximate a curve.
     */
    public void setSpacingInixels(final double spacingInPixels) {
        this.spacingInPixels = spacingInPixels;
    }

    /** @return The {@link LayerViewPanel} associated with the viewport */
    public final Collection<LayerViewPanel> getLayerViewPanels() {
        return this.layerViewPanels;
    }

    /**
     * Taille d'un pixel en m (la longueur d'un cote de pixel de l'ecran)
     * utilise pour le calcul de l'echelle courante de la vue. Elle est calculée
     * à partir de la résolution de l'écran en DPI. par exemple si la résolution
     * est 90DPI, c'est: 90 pix/inch = 1/90 inch/pix = 0.0254/90 meter/pix.
     */
    private final static double METERS_PER_PIXEL;
    static {
        METERS_PER_PIXEL = 0.02540005 / Toolkit.getDefaultToolkit()
                .getScreenResolution();
    }

    /**
     * @return Taille d'un pixel en m (la longueur d'un cote de pixel de
     *         l'ecran) utilise pour le calcul de l'echelle courante de la vue.
     */
    public static double getMETERS_PER_PIXEL() {
        return Viewport.METERS_PER_PIXEL;
    }

    /** Default scale. */
    private double scale = 1;

    /** @return The scale of the viewport */
    public final double getScale() {
        return this.scale;
    }

    /** The affine transformation from model to view. */
    private AffineTransform modelToViewTransform = null;

    /**
     * @return The {@link AffineTransform} corresponding to the transformation
     *         from model to view coordinates.
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     * @see #update()
     */
    public final AffineTransform getModelToViewTransform()
            throws NoninvertibleTransformException {
        if (this.modelToViewTransform == null) {
            this.update();
        }
        return this.modelToViewTransform;
    }

    /**
     * Update the model to view transformation and repaint the panel.
     * 
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     * @see #modelToViewTransform
     */
    public final void update() throws NoninvertibleTransformException {
        this.modelToViewTransform = Viewport.modelToViewTransform(this.scale,
                this.viewOrigin, this.layerViewPanels.iterator().next()
                        .getHeight());
        for (LayerViewPanel lvp : this.layerViewPanels) {
            lvp.repaint();
        }
    }

    /**
     * Compute the {@link AffineTransform} corresponding to the transformation
     * from model to view coordinates.
     * 
     * @param xScale
     *            the X scale of the view
     * @param yScale
     *            the Y scale of the view
     * @param viewOrigin
     *            the origin of the view
     * @param height
     *            the height of the view
     * @return The {@link AffineTransform} corresponding to the transformation
     *         from model to view coordinates.
     * @see #getEnvelopeInModelCoordinates()
     * @see #update()
     */
    public static AffineTransform modelToViewTransform(final double xScale,
            final double yScale, final Point2D viewOrigin, final double height) {
        AffineTransform modelToViewTransform = new AffineTransform();
        modelToViewTransform.translate(0, height);
        modelToViewTransform.scale(1, -1);
        modelToViewTransform.scale(xScale, yScale);
        modelToViewTransform.translate(-viewOrigin.getX(), -viewOrigin.getY());
        return modelToViewTransform;
    }

    /**
     * Compute the {@link AffineTransform} corresponding to the transformation
     * from model to view coordinates.
     * 
     * @param scale
     *            the scale of the view
     * @param viewOrigin
     *            the origin of the view
     * @param height
     *            the height of the view
     * @return The {@link AffineTransform} corresponding to the transformation
     *         from model to view coordinates.
     * @see #getEnvelopeInModelCoordinates()
     * @see #update()
     */
    public static AffineTransform modelToViewTransform(final double scale,
            final Point2D viewOrigin, final double height) {
        return Viewport.modelToViewTransform(scale, scale, viewOrigin, height);
    }

    /** The affine transformation from view to model. */
    private AffineTransform viewToModelTransform = null;

    public final AffineTransform getViewToModelTransform()
            throws NoninvertibleTransformException {
        if (this.viewToModelTransform == null) {
            this.viewToModelTransform = this.getModelToViewTransform()
                    .createInverse();
        }
        return this.viewToModelTransform;
    }

    /**
     * Constructor of viewport with a {@link LayerViewPanel}.
     * 
     * @param aLayerViewPanel
     *            the layer associated with the viewport
     */
    public Viewport(final LayerViewPanel aLayerViewPanel) {
        this.layerViewPanels.add(aLayerViewPanel);
    }

    /**
     * Copy constructor
     * 
     * @param src
     */
    public Viewport(final Viewport src) {
        this.layerViewPanels.addAll(src.getLayerViewPanels());
        this.modelToViewTransform = src.modelToViewTransform == null ? null
                : new AffineTransform(src.modelToViewTransform);
        this.scale = src.scale;
        this.spacingInPixels = src.spacingInPixels;
        this.viewOrigin = this.viewOrigin == null ? null : new Point2D.Double(
                src.viewOrigin.getX(), src.viewOrigin.getY());

        this.viewToModelTransform = src.viewToModelTransform == null ? null
                : new AffineTransform(src.viewToModelTransform);
    }

    /** @return The envelope of the panel in model coordinates. */
    public final IEnvelope getEnvelopeInModelCoordinates() {
        LayerViewPanel lvp = this.layerViewPanels.iterator().next();
        double widthAsPerceivedByModel = lvp.getWidth() / this.scale;
        double heightAsPerceivedByModel = lvp.getHeight() / this.scale;
        return new GM_Envelope(this.viewOrigin.getX(), this.viewOrigin.getX()
                + widthAsPerceivedByModel, this.viewOrigin.getY(),
                this.viewOrigin.getY() + heightAsPerceivedByModel);
    }

    /**
     * Transform the envelope to model coordinates.
     * 
     * @param x
     *            X coordinate of the envelope
     * @param y
     *            Y coordinate of the envelope
     * @param width
     *            width of the envelope
     * @param height
     *            height of the envelope
     * @return The envelope of the panel in model coordinates
     */
    public final IEnvelope getEnvelopeInModelCoordinates(final int x,
            final int y, final int width, final int height) {
        double xAsPerceivedByModel = x / this.scale;
        double yAsPerceivedByModel = y / this.scale;
        double widthAsPerceivedByModel = width / this.scale;
        double heightAsPerceivedByModel = height / this.scale;
        return new GM_Envelope(this.viewOrigin.getX() + xAsPerceivedByModel,
                this.viewOrigin.getX() + xAsPerceivedByModel
                        + widthAsPerceivedByModel, this.viewOrigin.getY()
                        + yAsPerceivedByModel, this.viewOrigin.getY()
                        + yAsPerceivedByModel + heightAsPerceivedByModel);
    }

    /**
     * @param geometry
     *            a geometry
     * @return A shape representing the geometry in view coordinates
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final Shape toShape(final IGeometry geometry)
            throws NoninvertibleTransformException {
        if (geometry == null) {
            return null;
        }
        IEnvelope envelope = this.getEnvelopeInModelCoordinates();
        try {
            IEnvelope geometryEnvelope = geometry.getEnvelope();
            // if the geometry does not intersect the envelope of
            // the view, return a null shape
            if (!envelope.intersects(geometryEnvelope)) {
                return null;
            }
            if (geometry.isEmpty()) {
                return null;
            }
            if (geometry.isPolygon()) {
                return this.toShape((IPolygon) geometry);
            }
            if (geometry.isMultiSurface()) {
                return this.toShape((IMultiSurface<?>) geometry);
            }
            if (geometry.isLineString()) {
                return this.toShape((ILineString) geometry);
            }
            if (ICurve.class.isAssignableFrom(geometry.getClass())) {
                // Curve other than linestring
                return this.toShape((ICurve) geometry);
            }
            if (geometry instanceof IRing) {
                return this.toShape(new GM_Polygon((IRing) geometry));
            }
            if (geometry.isMultiCurve()) {
                return null;
            }
            if (geometry.isPoint()) {
                return this.toShape((IPoint) geometry);
            }
            if (geometry instanceof IAggregate<?>) {
                return null;
            }
            throw new IllegalArgumentException(
                    I18N.getString("Viewport.UnhandledGeometryClass" //$NON-NLS-1$
                    ) + geometry.getClass());
        } catch (Exception e) {
            Viewport.logger.info(I18N.getString("Viewport.Geometry") //$NON-NLS-1$
                    + geometry);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Transform a multi polygon to an awt shape.
     * 
     * @param geometry
     *            a multi polygon
     * @return A shape representing the multi polygon in view coordinates
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     * @see #toViewDirectPositionList(IPolygon p)
     */
    private Shape toShape(final IMultiSurface<?> geometry)
            throws NoninvertibleTransformException {
        IDirectPositionList viewDirectPositionList = null;
        IDirectPosition lastPosition = null;
        for (IOrientableSurface surface : geometry) {
            if (IPolygon.class.isAssignableFrom(surface.getClass())) {
                IDirectPositionList list = this
                        .toViewDirectPositionList((IPolygon) surface);
                if (viewDirectPositionList == null) {
                    viewDirectPositionList = list;
                    lastPosition = list.get(list.size() - 1);
                } else {
                    viewDirectPositionList.addAll(list);
                    viewDirectPositionList.add(lastPosition);
                }
            }
        }
        return this.toPolygonShape(viewDirectPositionList);
    }

    /**
     * Transform a polygon to an awt shape.
     * 
     * @param p
     *            a polygon
     * @return A shape representing the polygon in view coordinates
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     * @see #toViewDirectPositionList(IPolygon p)
     */
    private Shape toShape(final IPolygon p)
            throws NoninvertibleTransformException {
        return this.toPolygonShape(this.toViewDirectPositionList(p));
    }

    /**
     * Transform a polygon to a directpositionlist in view coordinates.
     * 
     * @param p
     *            a polygon
     * @return a directpositionlist representing the polygon in view
     *         coordinates.
     * @throws NoninvertibleTransformException
     */
    public final IDirectPositionList toViewDirectPositionList(final IPolygon p)
            throws NoninvertibleTransformException {
        IDirectPositionList viewDirectPositionList = this
                .toViewDirectPositionList(p.getExterior().coord());
        if (viewDirectPositionList.isEmpty()) {
            return null;
        }
        IDirectPosition lastExteriorRingDirectPosition = viewDirectPositionList
                .get(viewDirectPositionList.size() - 1);
        for (int i = 0; i < p.sizeInterior(); i++) {
            viewDirectPositionList.addAll(this.toViewDirectPositionList(p
                    .getInterior(i).coord()));
            viewDirectPositionList.add(lastExteriorRingDirectPosition);
        }
        return viewDirectPositionList;
    }

    /**
     * Transform a direct position list in view coordinates to an awt shape.
     * 
     * @param viewDirectPositionList
     *            a direct position list in view coordinates
     * @return A shape representing the polygon in view coordinates
     */
    private Shape toPolygonShape(
            final IDirectPositionList viewDirectPositionList) {
        int numPoints = viewDirectPositionList.size();
        int[] xpoints = new int[numPoints];
        int[] ypoints = new int[numPoints];
        for (int i = 0; i < viewDirectPositionList.size(); i++) {
            IDirectPosition p = viewDirectPositionList.get(i);
            xpoints[i] = (int) p.getX();
            ypoints[i] = (int) p.getY();
        }
        return new Polygon(xpoints, ypoints, numPoints);
    }

    /** The minimum number of points we keep during point decimation. */
    private static final int MINIMUM_NUMBER_OF_POINTS = 4;

    /**
     * Transform a direct position list in model coordinates to view
     * coordinates.
     * 
     * @param modelDirectPositionList
     *            a direct position list in model coordinates
     * @return a DirectPositionList of DirectPosition in the screen coordinate
     *         system corresponding to the given DirectPositionList in model
     *         coordinate system
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final IDirectPositionList toViewDirectPositionList(
            final IDirectPositionList modelDirectPositionList)
            throws NoninvertibleTransformException {
        IDirectPositionList viewDirectPositionList = new DirectPositionList();
        if (modelDirectPositionList.isEmpty()) {
            return viewDirectPositionList;
        }
        double threshold = 1 / (this.getScale() * 2); // convert in model units
        IDirectPosition previousPoint = modelDirectPositionList.get(0);
        int numberOfPoints = 0;
        int numberOfModelPoints = modelDirectPositionList.size();
        for (int i = 0; i < numberOfModelPoints; i++) {
            IDirectPosition pi = modelDirectPositionList.get(i);
            // inline Decimator
            double xDifference = Math.abs(previousPoint.getX() - pi.getX());
            double yDifference = Math.abs(previousPoint.getY() - pi.getY());
            // we keep the first 4 points, the last point and all points
            // whose distance with the last point is greater than the
            // threshold
            if (xDifference >= threshold || yDifference >= threshold
                    || numberOfPoints < Viewport.MINIMUM_NUMBER_OF_POINTS
                    || i == numberOfModelPoints - 1) {
                Point2D point2D = this.toViewPoint(pi);
                viewDirectPositionList.add(new DirectPosition(point2D.getX(),
                        point2D.getY()));
                previousPoint = pi;
                numberOfPoints++;
            }
        }
        if (numberOfPoints != numberOfModelPoints) {
            while (viewDirectPositionList.size() > numberOfPoints) {
                viewDirectPositionList
                        .remove(viewDirectPositionList.size() - 1);
            }
            return viewDirectPositionList;
        }
        return viewDirectPositionList;
    }

    /**
     * Transform a direct position in model coordinates to view coordinates.
     * 
     * @param modelDirectPosition
     *            a direct position list in model coordinates
     * @return a Point2D (on the screen) corresponding to the given
     *         DirectPosition in model coordinate system
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final Point2D toViewPoint(final IDirectPosition modelDirectPosition)
            throws NoninvertibleTransformException {
        Point2D.Double pt = new Point2D.Double(modelDirectPosition.getX(),
                modelDirectPosition.getY());
        return this.getModelToViewTransform().transform(pt, pt);
    }

    /**
     * Transform a curve to an awt shape.
     * 
     * @param curve
     *            a curve
     * @return a Shape representing the given curve as an AWT shape
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    private Shape toShape(final ICurve curve)
            throws NoninvertibleTransformException {
        if (IBezier.class.isAssignableFrom(curve.getClass())) {
            IBezier b = (IBezier) curve;
            if (b.getDegree() == 2) {
                IDirectPositionList list = this.toViewDirectPositionList(curve
                        .coord());
                java.awt.geom.QuadCurve2D.Double quadratic = new java.awt.geom.QuadCurve2D.Double(
                        list.get(0).getX(), list.get(0).getY(), list.get(1)
                                .getX(), list.get(1).getY(),
                        list.get(2).getX(), list.get(2).getY());
                return quadratic;
            }
            if (b.getDegree() == 3) {
                IDirectPositionList list = this.toViewDirectPositionList(curve
                        .coord());
                java.awt.geom.CubicCurve2D.Double cubic = new java.awt.geom.CubicCurve2D.Double(
                        list.get(0).getX(), list.get(0).getY(), list.get(1)
                                .getX(), list.get(1).getY(),
                        list.get(2).getX(), list.get(2).getY(), list.get(3)
                                .getX(), list.get(3).getY());
                return cubic;
            }
        }
        // sample the curve using the current scale of the viewport to compute
        // the
        // spacing for the approximation
        ILineString linestring = curve.asLineString(this.getSpacingInPixels()
                / this.getScale(), 0);
        return this.toShape(linestring);
    }

    /**
     * Transform a linestring to an awt general path.
     * 
     * @param lineString
     *            a linestring
     * @return a GeneralPath representing the given linestring as an AWT shape
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    private GeneralPath toShape(final ILineString lineString)
            throws NoninvertibleTransformException {
        return this.toShape(lineString.coord());
    }

    /**
     * Transform a DirectPosition list to an awt general path.
     * 
     * @param list
     *            a DirectPosition list
     * @return a GeneralPath representing the given linestring as an AWT shape
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public GeneralPath toShape(final IDirectPositionList list)
            throws NoninvertibleTransformException {
        IDirectPositionList viewPositionList = this
                .toViewDirectPositionList(list);
        GeneralPath shape = new GeneralPath();
        IDirectPosition p = viewPositionList.get(0);
        shape.moveTo(p.getX(), p.getY());
        for (int i = 1; i < viewPositionList.size(); i++) {
            p = viewPositionList.get(i);
            shape.lineTo(p.getX(), p.getY());
        }
        return shape;
    }

    /**
     * Transform a point to an awt general path.
     * 
     * @param point
     *            a point
     * @return a GeneralPath representing the given point as an AWT shape
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    private GeneralPath toShape(final IPoint point)
            throws NoninvertibleTransformException {
        Point2D p = this.toViewPoint(point.getPosition());
        GeneralPath shape = new GeneralPath();
        shape.moveTo(p.getX(), p.getY());
        return shape;
    }

    /**
     * Zoom to full extent, i.e. to view all the layers of the associated panel.
     * 
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final void zoomToFullExtent() throws NoninvertibleTransformException {
        this.zoom(this.layerViewPanels.iterator().next().getEnvelope());
    }

    /**
     * Zoom to the given extent.
     * 
     * @param extent
     *            extent of the zoom
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final void zoom(final IEnvelope extent)
            throws NoninvertibleTransformException {
        if (extent == null || extent.isEmpty() || extent.width() == 0
                || extent.height() == 0) {
            return;
        }
        LayerViewPanel lvp = this.layerViewPanels.iterator().next();
        this.scale = Math.min(lvp.getWidth() / extent.width(), lvp.getHeight()
                / extent.length());
        double xCenteringOffset = (lvp.getWidth() / this.scale - extent.width()) / 2d;
        double yCenteringOffset = (lvp.getHeight() / this.scale - extent
                .length()) / 2d;
        this.viewOrigin = new Point2D.Double(extent.minX() - xCenteringOffset,
                extent.minY() - yCenteringOffset);
        this.update();
    }

    /**
     * Center on the given point.
     * 
     * @param centroid
     *            point to center on
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final void center(final IDirectPosition centroid)
            throws NoninvertibleTransformException {
        LayerViewPanel lvp = this.layerViewPanels.iterator().next();
        double xCenteringOffset = lvp.getWidth() / this.scale / 2d;
        double yCenteringOffset = lvp.getHeight() / this.scale / 2d;
        this.viewOrigin = new Point2D.Double(
                centroid.getX() - xCenteringOffset, centroid.getY()
                        - yCenteringOffset);
        this.update();
    }

    /**
     * Center on the given feature.
     * 
     * @param feature
     *            feature to center on
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final void center(final IFeature feature)
            throws NoninvertibleTransformException {
        if (feature == null || feature.getGeom() == null) {
            return;
        }
        IDirectPosition centroid = feature.getGeom().centroid();
        this.center(centroid);
    }

    /** A constant holding the value 0.5. */
    private static final double ZERO_POINT_FIVE = 0.5d;

    /**
     * Zoom of the given factor centered on the given point.
     * 
     * @param p
     *            center of the zoom
     * @param factor
     *            factor of the zoom
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final void zoom(final Point2D p, final double factor)
            throws NoninvertibleTransformException {
        Point2D zoomPoint = this.toModelPoint(p);
        IEnvelope modelEnvelope = this.getEnvelopeInModelCoordinates();
        IDirectPosition centre = modelEnvelope.center();
        double width = modelEnvelope.width();
        double height = modelEnvelope.length();
        double dx = (zoomPoint.getX() - centre.getX()) / factor;
        double dy = (zoomPoint.getY() - centre.getY()) / factor;
        IEnvelope zoomModelEnvelope = new GM_Envelope(zoomPoint.getX()
                - Viewport.ZERO_POINT_FIVE * (width / factor) - dx,
                zoomPoint.getX() + Viewport.ZERO_POINT_FIVE * (width / factor)
                        - dx, zoomPoint.getY() - Viewport.ZERO_POINT_FIVE
                        * (height / factor) - dy, zoomPoint.getY()
                        + Viewport.ZERO_POINT_FIVE * (height / factor) - dy);
        this.zoom(zoomModelEnvelope);
    }

    /**
     * Zoom to the given coordinates with the given width and height.
     * 
     * @param x
     *            upper left corner X coordinate
     * @param y
     *            upper left corner Y coordinate
     * @param widthOfNewView
     *            widht of the new view
     * @param heightOfNewView
     *            height of the new view
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final void zoom(final int x, final int y,
            final double widthOfNewView, final double heightOfNewView)
            throws NoninvertibleTransformException {
        LayerViewPanel lvp = this.layerViewPanels.iterator().next();
        double zoomFactor = Math.min(lvp.getWidth() / widthOfNewView,
                lvp.getHeight() / heightOfNewView);
        double realWidthOfNewView = lvp.getWidth() / zoomFactor;
        double realHeightOfNewView = lvp.getHeight() / zoomFactor;
        IEnvelope zoomEnvelope;
        try {
            zoomEnvelope = this.toModelEnvelope(x - Viewport.ZERO_POINT_FIVE
                    * realWidthOfNewView, x + Viewport.ZERO_POINT_FIVE
                    * realWidthOfNewView, y - Viewport.ZERO_POINT_FIVE
                    * realHeightOfNewView, y + Viewport.ZERO_POINT_FIVE
                    * realHeightOfNewView);
        } catch (NoninvertibleTransformException ex) {
            this.zoomToFullExtent();
            return;
        }
        this.zoom(zoomEnvelope);
    }

    /**
     * Transform an envelope in view coordinates to an envelope in model
     * coordinates.
     * 
     * @param xMin
     *            upper left corner X coordinate
     * @param xMax
     *            lower right corner X coordinate
     * @param yMin
     *            upper left corner Y coordinate
     * @param yMax
     *            lower right corner Y coordinate
     * @return the envelope in model coordinate system containing
     *         xMin,xMax,yMin,yMax
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    private IEnvelope toModelEnvelope(final double xMin, final double xMax,
            final double yMin, final double yMax)
            throws NoninvertibleTransformException {
        return new GM_Envelope(this.toModelDirectPosition(new Point2D.Double(
                xMax, yMin)), this.toModelDirectPosition(new Point2D.Double(
                xMin, yMax)));
    }

    /**
     * Transform a point in view coordinates to a direct position in model
     * coordinates.
     * 
     * @param viewPoint
     *            a 2D point
     * @return a DirectPosition in the model coordinate system corresponding to
     *         the viewPoint (on the screen)
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final DirectPosition toModelDirectPosition(final Point2D viewPoint)
            throws NoninvertibleTransformException {
        Point2D p = this.toModelPoint(viewPoint);
        return new DirectPosition(p.getX(), p.getY());
    }

    /**
     * Transform a point in view coordinates to a point in model coordinates.
     * 
     * @param viewPoint
     *            a 2D point
     * @return a Point2D in the model coordinate system corresponding to the
     *         viewPoint (on the screen)
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final Point2D toModelPoint(final Point2D viewPoint)
            throws NoninvertibleTransformException {
        return this.getModelToViewTransform().inverseTransform(
                toPoint2DDouble(viewPoint), null);
    }

    /**
     * Conversion function from any Point2D to the equivalent Point2D.Double.
     * 
     * @param p
     *            a 2D point
     * @return a correspondind Point2D.Double
     */
    public static Point2D.Double toPoint2DDouble(final Point2D p) {
        if (p instanceof Point2D.Double) {
            return (Point2D.Double) p;
        }
        return new Point2D.Double(p.getX(), p.getY());
    }

    /**
     * Conversion function from an IDirectPosition to the equivalent
     * Point2D.Double. Only X & Y values are used. Z is ignored
     * 
     * @param p
     *            an IDirectPosition
     * @return a corresponding Point2D.Double
     */
    public static Point2D toPoint2D(final IDirectPosition p) {
        return new Point2D.Double(p.getX(), p.getY());
    }

    /**
     * Zoom in.
     * 
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final void zoomIn() throws NoninvertibleTransformException {
        IEnvelope envelope = this.getEnvelopeInModelCoordinates();
        envelope.expandBy(1 / Viewport.ZOOM_FACTOR, 1 / Viewport.ZOOM_FACTOR);
        this.zoom(envelope);
    }

    /**
     * Zoom out.
     * 
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final void zoomOut() throws NoninvertibleTransformException {
        IEnvelope envelope = this.getEnvelopeInModelCoordinates();
        envelope.expandBy(Viewport.ZOOM_FACTOR, Viewport.ZOOM_FACTOR);
        this.zoom(envelope);
    }

    /**
     * Zoom in centered on the given point.
     * 
     * @param p
     *            a point
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final void zoomInTo(final Point2D p)
            throws NoninvertibleTransformException {
        this.zoom(p, Viewport.ZOOM_FACTOR);
    }

    /**
     * Zoom out centered on the given point.
     * 
     * @param p
     *            a point
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final void zoomOutTo(final Point2D p)
            throws NoninvertibleTransformException {
        this.zoom(p, 1 / Viewport.ZOOM_FACTOR);
    }

    public final void zoomToScale(final double scale)
            throws NoninvertibleTransformException {
        IDirectPosition p = this.getEnvelopeInModelCoordinates().center();
        this.scale = scale;
        this.update();
        this.center(p);
    }

    /**
     * Move up.
     * 
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final void moveUp() throws NoninvertibleTransformException {
        this.moveOf(0, this.layerViewPanels.iterator().next().getHeight()
                * Viewport.MOVE_FACTOR);
    }

    /**
     * Move down.
     * 
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final void moveDown() throws NoninvertibleTransformException {
        this.moveOf(0, -this.layerViewPanels.iterator().next().getHeight()
                * Viewport.MOVE_FACTOR);
    }

    /**
     * Move right.
     * 
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final void moveRight() throws NoninvertibleTransformException {
        this.moveOf(this.layerViewPanels.iterator().next().getWidth()
                * Viewport.MOVE_FACTOR, 0);
    }

    /**
     * Move left.
     * 
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final void moveLeft() throws NoninvertibleTransformException {
        this.moveOf(-this.layerViewPanels.iterator().next().getWidth()
                * Viewport.MOVE_FACTOR, 0);
    }

    /**
     * Move the screen of the given X and Y pixels.
     * 
     * @param x
     *            Number of pixels to move in X coordinates
     * @param y
     *            Number of pixels to move in Y coordinates
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final void moveOf(final double x, final double y)
            throws NoninvertibleTransformException {
        // logger.debug(getMETERS_PER_PIXEL());
        this.viewOrigin.setLocation(this.viewOrigin.getX() + x / this.scale,
                this.viewOrigin.getY() + y / this.scale);
        this.update();
    }

    /**
     * Move the screen to the given point.
     * 
     * @param point
     *            the point to center the screen on
     * @throws NoninvertibleTransformException
     *             throws an exception when the transformation fails
     */
    public final void moveTo(final Point point)
            throws NoninvertibleTransformException {
        Point2D modelPoint = this.toModelPoint(point);
        LayerViewPanel lvp = this.layerViewPanels.iterator().next();
        modelPoint.setLocation(modelPoint.getX() - lvp.getWidth()
                / (2 * this.scale), modelPoint.getY() - lvp.getHeight()
                / (2 * this.scale));
        this.viewOrigin.setLocation(modelPoint);
        this.update();
    }

    /**
     * Set a new Scale to the viewport.
     * 
     * @param newScale
     *            new scale of the viewport
     */
    public final void setScale(final double newScale) {
        IDirectPosition center = this.getEnvelopeInModelCoordinates().center();
        this.scale = newScale;
        // update the spacing in the adapter factory (used to approximate curves
        // such as bezier)
        AdapterFactory.setSpacing(this.getSpacingInPixels() / this.getScale());
        Point2D modelPoint = new Point2D.Double(center.getX(), center.getY());
        LayerViewPanel lvp = this.layerViewPanels.iterator().next();
        modelPoint.setLocation(modelPoint.getX() - lvp.getWidth()
                / (2 * this.scale), modelPoint.getY() - lvp.getHeight()
                / (2 * this.scale));
        this.viewOrigin.setLocation(modelPoint);
        try {
            this.update();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    /**
     * get the position of the display window top-left corner in world
     * coordinates
     * 
     * @return the viewOrigin in world coordinates
     */
    public Point2D getViewOrigin() {
        return this.viewOrigin;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Viewport [viewOrigin=" + this.viewOrigin + ", scale="
                + this.scale + ", modelToViewTransform="
                + this.modelToViewTransform + "]";
    }

}
