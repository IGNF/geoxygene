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

package fr.ign.cogit.geoxygene.appli.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.style.BlendingMode;
import fr.ign.cogit.geoxygene.style.Shadow;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.filter.LayerFilter;

/**
 * A renderer to render a {@link fr.ign.cogit.geoxygene.style.Layer} into a
 * {@link LayerViewPanel}.
 * 
 * @author Julien Perret
 * @see RenderingManager
 * @see fr.ign.cogit.geoxygene.style.Layer
 * @see LayerViewPanel
 */
public class SelectionRenderer extends AbstractLayerRenderer {

    private LayerViewPanel layerViewPanel = null;

    /** The logger. */
    private static Logger LOGGER = Logger.getLogger(SelectionRenderer.class
            .getName());

    /** The image the renderer renders into. */
    private BufferedImage image = null;

    /**
     * @param bufferedImage
     *            The image the renderer renders into
     */
    public final void setImage(final BufferedImage bufferedImage) {
        this.image = bufferedImage;
    }

    /** @return The image the renderer renders into */
    public final BufferedImage getImage() {
        return this.image;
    }

    /** Fill color. */
    private final Color fillColor = new Color(1f, 1f, 0f, 1 / 2f);

    /** @return The fill color */
    public final Color getFillColor() {
        return this.fillColor;
    }

    /** Stroke color. */
    private final Color strokeColor = new Color(1f, 1f, 0f, 1f);

    /** @return the stroke color */
    public final Color getStrokeColor() {
        return this.strokeColor;
    }

    /** Stroke width. */
    private final float strokeWidth = 2f;

    /** @return The stroke width */
    public final float getStrokeWidth() {
        return this.strokeWidth;
    }

    /** Radius of the rendered points. */
    private final int pointRadius = 2;

    /** @return The point radius */
    public final int getPointRadius() {
        return this.pointRadius;
    }

    /** The symbolizer. */
    private final Symbolizer symbolizer = new Symbolizer() {
        @Override
        public String getGeometryPropertyName() {
            return null;
        }

        @Override
        public Stroke getStroke() {
            return null;
        }

        @Override
        public boolean isLineSymbolizer() {
            return false;
        }

        @Override
        public boolean isPointSymbolizer() {
            return false;
        }

        @Override
        public boolean isPolygonSymbolizer() {
            return false;
        }

        @Override
        public boolean isRasterSymbolizer() {
            return false;
        }

        @Override
        public boolean isTextSymbolizer() {
            return false;
        }

        //
        // @Override
        // public void paint(final IFeature feature, final Viewport viewport,
        // final Graphics2D graphics) {
        // if (feature.getGeom() == null) {
        // return;
        // }
        // if (feature.getGeom().isPolygon() ||
        // feature.getGeom().isMultiSurface())
        // {
        // graphics.setColor(SelectionRenderer.this.getFillColor());
        // RenderUtil.fill(feature.getGeom(), viewport, graphics);
        // }
        // java.awt.Stroke bs = new BasicStroke(
        // SelectionRenderer.this.getStrokeWidth(), BasicStroke.CAP_SQUARE,
        // BasicStroke.JOIN_MITER);
        // graphics.setColor(SelectionRenderer.this.getStrokeColor());
        // graphics.setStroke(bs);
        // RenderUtil.draw(feature.getGeom(), viewport, graphics);
        // try {
        // graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE,
        // BasicStroke.JOIN_MITER));
        // for (IDirectPosition position : viewport
        // .toViewDirectPositionList(feature.getGeom().coord())) {
        // GeneralPath shape = new GeneralPath();
        // shape.moveTo(
        // position.getX() - SelectionRenderer.this.getPointRadius(),
        // position.getY() - SelectionRenderer.this.getPointRadius());
        // shape.lineTo(
        // position.getX() + SelectionRenderer.this.getPointRadius(),
        // position.getY() - SelectionRenderer.this.getPointRadius());
        // shape.lineTo(
        // position.getX() + SelectionRenderer.this.getPointRadius(),
        // position.getY() + SelectionRenderer.this.getPointRadius());
        // shape.lineTo(
        // position.getX() - SelectionRenderer.this.getPointRadius(),
        // position.getY() + SelectionRenderer.this.getPointRadius());
        // shape.lineTo(
        // position.getX() - SelectionRenderer.this.getPointRadius(),
        // position.getY() - SelectionRenderer.this.getPointRadius());
        // graphics.setColor(SelectionRenderer.this.getStrokeColor());
        // graphics.fill(shape);
        // graphics.setColor(Color.black);
        // graphics.draw(shape);
        // }
        // } catch (NoninvertibleTransformException e) {
        // e.printStackTrace();
        // }
        // }

        @Override
        public void setGeometryPropertyName(final String geometryPropertyName) {
        }

        @Override
        public void setStroke(final Stroke stroke) {
        }

        @Override
        public String getUnitOfMeasure() {
            return Symbolizer.PIXEL;
        }

        @Override
        public void setUnitOfMeasure(final String uom) {
        }

        @Override
        public void setUnitOfMeasureFoot() {
        }

        @Override
        public void setUnitOfMeasureMetre() {
        }

        @Override
        public void setUnitOfMeasurePixel() {
        }

        @Override
        public Shadow getShadow() {
            return null;
        }

        @Override
        public void setShadow(final Shadow shadow) {
        }

        @Override
        public void reset() {
        }

        @Override
        public void updateInternal() {
        }

        @Override
        public BlendingMode getBlendingMode() {
            return null;
        }

        @Override
        public void setBlendingMode(BlendingMode blendingMode) {
        }

        @Override
        public LayerFilter getFilter() {
            return null;
        }

        @Override
        public void setFilter(LayerFilter filter) {
        }

    };

    /**
     * Constructor of renderer using a
     * {@link fr.ign.cogit.geoxygene.style.Layer} and a {@link LayerViewPanel}.
     * 
     * @param theLayerViewPanel
     *            the panel to draws into
     */
    public SelectionRenderer(final LayerViewPanel theLayerViewPanel) {
        super(null);
        this.setLayerViewPanel(theLayerViewPanel);
    }

    /**
     * Copy the rendered image the a 2D graphics.
     * 
     * @param graphics
     *            the 2D graphics to draw into
     */
    public final void copyTo(final Graphics2D graphics) {
        if (this.getImage() != null) {
            graphics.drawImage(this.getImage(), 0, 0, null);
        }
    }

    /**
     * @return the layerViewPanel
     */
    @Override
    public LayerViewPanel getLayerViewPanel() {
        return this.layerViewPanel;
    }

    /**
     * @param layerViewPanel
     *            the layerViewPanel to set
     */
    public void setLayerViewPanel(LayerViewPanel layerViewPanel) {
        this.layerViewPanel = layerViewPanel;
    }

    /**
     * Create a runnable for the renderer. A renderer create a new image to draw
     * into. If cancel() is called, the rendering stops as soon as possible.
     * When finished, set the variable rendering to false.
     * 
     * @return a new runnable
     * @see Runnable
     * @see #cancel()
     * @see #isRendering()
     */
    @Override
    public final Runnable createRunnable() {

        if (this.getImage() != null) {
            return null;
        }
        this.setCancelled(false);
        return new Runnable() {
            @Override
            public void run() {
                SelectionRenderer.this.setRendering(true);
                SelectionRenderer.this.setRendered(false);
                try {
                    // it the rendering is cancel, stop
                    if (SelectionRenderer.this.isCancelled()) {
                        return;
                    }
                    // if either the width or the height of the panel is
                    // lesser or equal to 0, stop
                    if (Math.min(SelectionRenderer.this.getLayerViewPanel()
                            .getWidth(), SelectionRenderer.this
                            .getLayerViewPanel().getHeight()) <= 0) {
                        return;
                    }
                    // create a new image
                    SelectionRenderer.this.setImage(new BufferedImage(
                            SelectionRenderer.this.getLayerViewPanel()
                                    .getWidth(), SelectionRenderer.this
                                    .getLayerViewPanel().getHeight(),
                            BufferedImage.TYPE_INT_ARGB));
                    // do the actual rendering
                    try {
                        SelectionRenderer.this
                                .renderHook(SelectionRenderer.this.getImage());
                    } catch (Throwable t) {
                        // TODO WARN THE USER?
                        t.printStackTrace(System.err);
                        return;
                    }
                } finally {
                    // the renderer is not rendering anymore
                    // ( used by isRendering() )
                    SelectionRenderer.this.setRendering(false);
                    SelectionRenderer.this.setRendered(true);
                    if (SelectionRenderer.LOGGER.isTraceEnabled()) {
                        SelectionRenderer.LOGGER
                                .trace("Selection Renderer finished"); //$NON-NLS-1$
                    }
                    SelectionRenderer.this.getLayerViewPanel()
                            .getRenderingManager().repaint();
                }
            }
        };
    }

    /**
     * Actually renders the layer in an image. Stop if cancelled is true.
     * 
     * @param theImage
     *            the image to draw into
     * @see #cancel()
     */
    final void renderHook(final BufferedImage theImage) {
        if (this.isCancelled()) {
            return;
        }
        for (IFeature feature : this.getLayerViewPanel().getSelectedFeatures()) {
            if (this.isCancelled()) {
                return;
            }
            if (feature.getGeom() != null && !feature.getGeom().isEmpty()) {
                this.render(feature, theImage);
            }
        }
    }

    /**
     * Render a feature into an image using the given symbolizer.
     * 
     * @param feature
     *            the feature to render
     * @param theImage
     *            the image to render into
     */
    private void render(final IFeature feature, final BufferedImage theImage) {
        // FIXME OPACITY FIX
        RenderUtil.paint(this.symbolizer, feature, this.getLayerViewPanel()
                .getViewport(), (Graphics2D) theImage.getGraphics(), 1.0f,
                theImage);
    }

    /** Clear the image cache, i.e. delete the current image. */
    public final void clearImageCache() {
        this.setImage(null);
    }

    public final void clearImageCache(final int x, final int y,
            final int width, final int height) {
        if (this.isCancelled()) {
            return;
        }
        for (int i = Math.max(x, 0); i < Math.min(x + width, this
                .getLayerViewPanel().getWidth()); i++) {
            for (int j = Math.max(y, 0); j < Math.min(y + height, this
                    .getLayerViewPanel().getHeight()); j++) {
                this.getImage().setRGB(i, j, Transparency.TRANSLUCENT);
            }
        }
    }

    /**
     * Method called before each rendering
     */
    @Override
    public void initializeRendering() {
        super.initializeRendering();
        this.clearImageCache();
        /*
         * this.setImage(new BufferedImage(
         * SelectionRenderer.this.getLayerViewPanel().getWidth(),
         * SelectionRenderer.this.getLayerViewPanel().getHeight(),
         * BufferedImage.TYPE_INT_ARGB));
         */
    }

    @Override
    public void reset() {
        this.clearImageCache();
    }

}
