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

package fr.ign.cogit.geoxygene.appli.layer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.event.CompassPaintListener;
import fr.ign.cogit.geoxygene.appli.event.LegendPaintListener;
import fr.ign.cogit.geoxygene.appli.event.ScalePaintListener;
import fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.ImgUtil;

/**
 * Panel displaying layers.
 * 
 * @author Julien Perret
 * @author Jérémie Turbet (jul 2013 modifications)
 */
public class LayerViewAwtPanel extends LayerViewPanel {

    /** Serializable UID. */
    private static final long serialVersionUID = -6502924871341284384L;

    /** Logger. */
    private static Logger logger = Logger.getLogger(LayerViewAwtPanel.class.getName());

    /** Rendering manager. */
    private MultithreadedRenderingManager renderingManager = null;

    /** Default visibility Constructor which can be called only by the factory. */
    public LayerViewAwtPanel() {
        super();
        this.addPaintListener(new ScalePaintListener());
        this.addPaintListener(new CompassPaintListener());
        this.addPaintListener(new LegendPaintListener());
        this.setDoubleBuffered(true);
        this.setOpaque(true);
        this.renderingManager = new MultithreadedRenderingManager(this); // rendering manager
    }

    /** @return The rendering manager handling the rendering of the layers */
    @Override
    public MultithreadedRenderingManager getRenderingManager() {
        return this.renderingManager;
    }

    @Override
    public final void repaint() {
        if (this.getRenderingManager() != null) {
            this.getRenderingManager().renderAll();
        }
    }

    /**
     * Repaint the panel using the repaint method of the super class
     * {@link JPanel}. Called in order to perform the progressive rendering.
     * 
     * @see #paintComponent(Graphics)
     */
    @Override
    public final void superRepaint() {
        Container parent = this.getParent();
        if (parent != null) {
            parent.repaint();
        }
    }

    @Override
    public final void paintComponent(final Graphics g) {
        try {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // // super.paintComponent(g);
            // // clear the graphics
            g.setColor(this.getBackground());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            // copy the result of the rendering manager to the panel
            this.getRenderingManager().copyTo((Graphics2D) g);
            // if currently editing geometry
            this.paintGeometryEdition(g);
            this.paintOverlays(g);
            //
            if (this.recording) {
                this.saveImage();
            }
        } catch (Throwable t) {
            LayerViewAwtPanel.logger.error(I18N.getString("LayerViewAwtPanel.PaintError")); //$NON-NLS-1$
            t.printStackTrace();
            // TODO HANDLE EXCEPTIONS
        }
    }

    private void saveImage() {
        LayerViewAwtPanel.logger.debug("record"); //$NON-NLS-1$
        Color bg = this.getBackground();
        BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(bg);
        graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        this.getRenderingManager().copyTo(graphics);
        this.recording = false;
        // this.paintOverlays(graphics);
        graphics.dispose();
        try {
            NumberFormat format = NumberFormat.getInstance();
            format.setMinimumIntegerDigits(3);
            ImgUtil.saveImage(image, this.recordFileName + format.format(this.recordIndex) + ".png"); //$NON-NLS-1$
            this.recordIndex++;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /** Dispose of the panel and its rendering manager. */
    @Override
    public final void dispose() {
        if (this.getRenderingManager() != null) {
            this.getRenderingManager().dispose();
        }
        this.setViewport(null);
        // TODO
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.LayerViewPanelExtracted#getEnvelope()
     */
    @Override
    public final IEnvelope getEnvelope() {
        if (this.getRenderingManager().getLayers().isEmpty()) {
            return null;
        }
        List<Layer> copy = new ArrayList<Layer>(this.getRenderingManager().getLayers());
        Iterator<Layer> layerIterator = copy.iterator();
        IEnvelope envelope = layerIterator.next().getFeatureCollection().envelope();
        while (layerIterator.hasNext()) {
            IFeatureCollection<? extends IFeature> collection = layerIterator.next().getFeatureCollection();
            if (collection != null) {
                IEnvelope env = collection.getEnvelope();
                if (envelope == null) {
                    envelope = env;
                } else {
                    envelope.expand(env);
                }
            }
        }
        return envelope;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex >= 1) {
            return Printable.NO_SUCH_PAGE;
        }
        Graphics2D g2d = (Graphics2D) graphics;
        // translate to the upper left corner of the page format
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        // translate to the middle of the page format
        g2d.translate(pageFormat.getImageableWidth() / 2, pageFormat.getImageableHeight() / 2);
        Dimension d = this.getSize();
        double scale = Math.min(pageFormat.getImageableWidth() / d.width, pageFormat.getImageableHeight() / d.height);
        if (scale < 1.0) {
            g2d.scale(scale, scale);
        }
        // translate of half the size of the graphics to paint for it to be
        // centered
        g2d.translate(-d.width / 2.0, -d.height / 2.0);
        // copy the rendered layers into the graphics
        this.getRenderingManager().copyTo(g2d);
        return Printable.PAGE_EXISTS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.LayerViewPanelExtracted#saveAsImage(java.lang
     * .String)
     */
    @Override
    public void saveAsImage(String fileName) {
        Color bg = this.getBackground();
        BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(bg);
        graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        this.getRenderingManager().copyTo(graphics);
        this.paintOverlays(graphics);
        graphics.dispose();
        try {
            ImgUtil.saveImage(image, fileName);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private boolean recording = false;

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.LayerViewPanelExtracted#isRecording()
     */
    @Override
    public boolean isRecording() {
        return this.recording;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.LayerViewPanelExtracted#setRecord(boolean)
     */
    // @Override
    @Override
    public void setRecord(boolean b) {
        this.recording = b;
    }

    private String recordFileName = ""; //$NON-NLS-1$

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.LayerViewPanelExtracted#getRecordFileName()
     */
    // @Override
    @Override
    public String getRecordFileName() {
        return this.recordFileName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.LayerViewPanelExtracted#setRecordFileName(
     * java.lang.String)
     */
    // @Override
    @Override
    public void setRecordFileName(String recordFileName) {
        this.recordFileName = recordFileName;
        this.recordIndex = 0;
    }

    private int recordIndex = 0;

    // public void setModel(StyledLayerDescriptor sld) {
    // this.sldmodel = sld;
    // this.sldmodel.addSldListener(this);
    //
    // }

    /** Evenements SLD */
    @Override
    public void actionPerformed(ActionEvent e) {
        this.repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.LayerViewPanelExtracted#layerAdded(fr.ign.
     * cogit.geoxygene.style.Layer)
     */
    @Override
    public synchronized void layerAdded(Layer l) {
        if (this.getRenderingManager() != null) {
            this.getRenderingManager().addLayer(l);
        }
        try {
            IEnvelope env = l.getFeatureCollection().getEnvelope();
            if (env == null) {
                env = l.getFeatureCollection().envelope();
            }
            this.getViewport().zoom(env);
        } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.LayerViewPanelExtracted#layerOrderChanged(int
     * ,
     * int)
     */
    @Override
    public void layerOrderChanged(int oldIndex, int newIndex) {
        this.repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.LayerViewPanelExtracted#layersRemoved(java
     * .util.Collection)
     */
    @Override
    public void layersRemoved(Collection<Layer> layers) {
        this.repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel#dislplayGui()
     */
    @Override
    public void displayGui() {
        // nothing to display
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel#hideGui()
     */
    @Override
    public void hideGui() {
        // nothing to hide

    }

}
