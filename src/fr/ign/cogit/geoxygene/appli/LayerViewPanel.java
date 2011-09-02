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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.event.CompassPaintListener;
import fr.ign.cogit.geoxygene.appli.event.LegendPaintListener;
import fr.ign.cogit.geoxygene.appli.event.PaintListener;
import fr.ign.cogit.geoxygene.appli.event.ScalePaintListener;
import fr.ign.cogit.geoxygene.appli.mode.AbstractGeometryEditMode;
import fr.ign.cogit.geoxygene.appli.mode.CreateInteriorRingMode;
import fr.ign.cogit.geoxygene.appli.mode.CreateLineStringMode;
import fr.ign.cogit.geoxygene.appli.mode.CreatePolygonMode;
import fr.ign.cogit.geoxygene.appli.mode.Mode;
import fr.ign.cogit.geoxygene.appli.render.RenderUtil;
import fr.ign.cogit.geoxygene.appli.render.RenderingManager;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.conversion.ImgUtil;

/**
 * Panel displaying layers.
 * 
 * @author Julien Perret
 */
public class LayerViewPanel extends JPanel implements Printable, SldListener{
  /**
   * logger.
   */
  private static Logger logger = Logger.getLogger(LayerViewPanel.class
      .getName());
  /**
   * serial uid.
   */
  private static final long serialVersionUID = 1L;

  protected Set<PaintListener> overlayListeners = new HashSet<PaintListener>(0);

  public void addPaintListener(PaintListener listener) {
    this.overlayListeners.add(listener);
  }

  public void paintOverlays(final Graphics graphics) {
    for (PaintListener listener : this.overlayListeners) {
      listener.paint(this, graphics);
    }
  }
  /**
   * Model
   */
  private StyledLayerDescriptor sldmodel;

  /**
   * Rendering manager.
   */
  private RenderingManager renderingManager = new RenderingManager(this);

  /**
   * @return The rendering manager handling the rendering of the layers
   */
  public final RenderingManager getRenderingManager() {
    return this.renderingManager;
  }

  /**
   * The rendering manager handling the rendering of the layers
   */
  public final void setRenderingManager(RenderingManager manager) {
    this.renderingManager = manager;
  }

  /**
   * Private viewport. Use getter or setter.
   */
  private Viewport viewport = null;

  public void setViewport(Viewport viewport) {
    this.viewport = viewport;
  }

  /**
   * Private selected features. Use getter and setter.
   */
  private Set<IFeature> selectedFeatures = new HashSet<IFeature>(0);

  /**
   * The viewport of the panel.
   * 
   * @return the viewport of the panel
   */
  public final Viewport getViewport() {
    return this.viewport;
  }

  private ProjectFrame projectFrame = null;

  public ProjectFrame getProjectFrame() {
    return this.projectFrame;
  }

  /**
   * Default Constructor.
   */
  public LayerViewPanel(final ProjectFrame frame) {
    super();
    this.sldmodel = null;
    this.projectFrame = frame;
    this.viewport = new Viewport(this);
    this.addPaintListener(new ScalePaintListener());
    this.addPaintListener(new CompassPaintListener());
    this.addPaintListener(new LegendPaintListener());
    this.setDoubleBuffered(true);
    this.setOpaque(true);
    this.setBackground(Color.WHITE);
  }

  @Override
  public final void repaint() {
    if (this.renderingManager != null) {
      this.renderingManager.renderAll();
    }
  }

  /**
   * Repaint the panel using the repaint method of the super class
   * {@link JPanel}. Called in order to perform the progressive rendering.
   * 
   * @see #paintComponent(Graphics)
   */
  public final void superRepaint() {
    // logger.info("superRepaint");
    super.repaint();
  }

  @Override
  public final void paintComponent(final Graphics g) {
    try {
      LayerViewPanel.logger.trace("paintComponent"); //$NON-NLS-1$
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
      // super.paintComponent(g);
      // clear the graphics
      g.setColor(this.getBackground());
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      // copy the result of the rendering manager to the panel
      this.renderingManager.copyTo((Graphics2D) g);
      // if currently editing geometry
      this.paintGeometryEdition(g);
      this.paintOverlays(g);

      if (this.recording) {
        LayerViewPanel.logger.trace("record"); //$NON-NLS-1$
        Color bg = this.getBackground();
        BufferedImage image = new BufferedImage(this.getWidth(),
            this.getHeight(), BufferedImage.TYPE_INT_ARGB);
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
          ImgUtil.saveImage(image,
              this.recordFileName + format.format(this.recordIndex) + ".png"); //$NON-NLS-1$
          this.recordIndex++;
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    } catch (Throwable t) {
      LayerViewPanel.logger.error(I18N.getString("LayerViewPanel.PaintError")); //$NON-NLS-1$
      t.printStackTrace();
      // TODO HANDLE EXCEPTIONS
    }
  }

  private void paintGeometryEdition(Graphics g) {
    Mode mode = this.getProjectFrame().getMainFrame().getMode()
        .getCurrentMode();
    g.setColor(new Color(1f, 0f, 0f));
    if (mode instanceof AbstractGeometryEditMode) {
      IDirectPositionList points = new DirectPositionList();
      points.addAll(((AbstractGeometryEditMode) mode).getPoints());
      if (mode instanceof CreateLineStringMode) {
        if (!points.isEmpty()) {
          points.add(((AbstractGeometryEditMode) mode).getCurrentPoint());
          RenderUtil.draw(new GM_LineString(points), this.getViewport(),
              (Graphics2D) g);
        }
      } else {
        if (mode instanceof CreatePolygonMode) {
          if (!points.isEmpty()) {
            IDirectPosition start = points.get(0);
            points.add(((AbstractGeometryEditMode) mode).getCurrentPoint());
            if (points.size() > 2) {
              points.add(start);
              RenderUtil.draw(new GM_Polygon(new GM_LineString(points)),
                  this.getViewport(), (Graphics2D) g);
            } else {
              if (points.size() == 2) {
                points.add(start);
                RenderUtil.draw(new GM_LineString(points), this.getViewport(),
                    (Graphics2D) g);
              }
            }
          }
        } else {
          if (mode instanceof CreateInteriorRingMode) {
          } else {
          }
        }
      }
    }
  }

  /**
   * Returns the size of a pixel in meters.
   * @return Taille d'un pixel en mètres (la longueur d'un coté de pixel de
   *         l'écran).
   */
  public static double getMETERS_PER_PIXEL() {
    return LayerViewPanel.METERS_PER_PIXEL;
  }

  /**
   * Taille d'un pixel en mètres (la longueur d'un coté de pixel de l'écran)
   * utilisé pour le calcul de l'echelle courante de la vue. Elle est calculée à
   * partir de la résolution de l'écran en DPI. Par exemple si la résolution est
   * 90DPI, c'est: 90 pix/inch = 1/90 inch/pix = 0.0254/90 meter/pix.
   */
  private final static double METERS_PER_PIXEL = 0.02540005 / Toolkit
      .getDefaultToolkit().getScreenResolution();

  /**
   * Dispose of the panel and its rendering manager.
   */
  public final void dispose() {
    this.renderingManager.dispose();
    this.viewport = null;
    // TODO
  }

  /**
   * Get the envelope.
   * 
   * @return The envelope of all layers of the panel in model coordinates
   */
  public final IEnvelope getEnvelope() {
    if (this.getRenderingManager().getLayers().isEmpty()) {
      return null;
    }
    List<Layer> copy = new ArrayList<Layer>(this.getRenderingManager()
        .getLayers());
    Iterator<Layer> layerIterator = copy.iterator();
    IEnvelope envelope = layerIterator.next().getFeatureCollection().envelope();
    while (layerIterator.hasNext()) {
      envelope.expand(layerIterator.next().getFeatureCollection().envelope());
    }
    return envelope;
  }

  /**
   * Getter for the selected feature.
   * 
   * @return the features selected by the user
   */
  public final Set<IFeature> getSelectedFeatures() {
    return this.selectedFeatures;
  }

  @Override
  public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
      throws PrinterException {
    if (pageIndex >= 1) {
      return Printable.NO_SUCH_PAGE;
    }
    Graphics2D g2d = (Graphics2D) graphics;
    // translate to the upper left corner of the page format
    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
    // translate to the middle of the page format
    g2d.translate(pageFormat.getImageableWidth() / 2,
        pageFormat.getImageableHeight() / 2);
    Dimension d = this.getSize();
    double scale = Math.min(pageFormat.getImageableWidth() / d.width,
        pageFormat.getImageableHeight() / d.height);
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

  /**
   * Save the map into an image file. The file format is determined by the given
   * file extension. If there is none or if the given extension is unsupported,
   * the image is saved in PNG format.
   * @param fileName the image file to save into.
   */
  public void saveAsImage(String fileName) {
    Color bg = this.getBackground();
    BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(),
        BufferedImage.TYPE_INT_ARGB);
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

  public boolean isRecording() {
    return this.recording;
  }

  public void setRecord(boolean b) {
    this.recording = b;
  }

  private String recordFileName = ""; //$NON-NLS-1$

  public String getRecordFileName() {
    return this.recordFileName;
  }

  public void setRecordFileName(String recordFileName) {
    this.recordFileName = recordFileName;
    this.recordIndex = 0;
  }

  private int recordIndex = 0;

public void setModel(StyledLayerDescriptor sld) {
    this.sldmodel = sld;
    this.sldmodel.addSldListener(this);

}

    /**
     * Evenements SLD
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        this.repaint();
    }

    @Override
    public synchronized void layerAdded(Layer l) {
        this.renderingManager.addLayer(l);
        try {
            IEnvelope env = l.getFeatureCollection().getEnvelope();
            if (env == null) {
                env = l.getFeatureCollection().envelope();
            }
            this.viewport.zoom(env);
        } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void layerOrderChanged(int oldIndex, int newIndex) {
        this.repaint();

    }

    @Override
    public void layersRemoved(Collection<Layer> layers) {
        this.repaint();

    }
}
