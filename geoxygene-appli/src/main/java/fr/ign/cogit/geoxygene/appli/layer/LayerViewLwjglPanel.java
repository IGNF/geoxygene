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
import java.awt.Graphics;

import org.apache.log4j.Logger;
import org.lwjgl.LWJGLException;

import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.event.CompassPaintListener;
import fr.ign.cogit.geoxygene.appli.event.LegendPaintListener;
import fr.ign.cogit.geoxygene.appli.event.ScalePaintListener;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanelFactory.RenderingType;
import fr.ign.cogit.geoxygene.appli.render.SyncRenderingManager;

/**
 * Panel displaying layers using LWJGL library to display GL elements.
 * 
 * @author Jérémie Turbet (jul 2013)
 */
public class LayerViewLwjglPanel extends LayerViewGLPanel {

  private static final long serialVersionUID = -6502924871351281384L; // Serializable
                                                                      // UID
  static Logger logger = Logger.getLogger(LayerViewLwjglPanel.class.getName()); // logger
  private LayerViewLwjglCanvas lwJGLCanvas = null; // canvas containing the GL
                                                   // context
  // private boolean firstPaint = true; //
  // false after the first paintComponent()
  // method call
  SyncRenderingManager renderingManager = null; // Rendering Manager used for GL
                                                // rendering

  /** Default visibility Constructor which can be called only by the factory. */
  LayerViewLwjglPanel(final ProjectFrame frame) {
    super(frame);

    this.addPaintListener(new ScalePaintListener());
    this.addPaintListener(new CompassPaintListener());
    this.addPaintListener(new LegendPaintListener());
    this.setBackground(new Color(255, 255, 220));
    // Attach LWJGL to the created canvas
    try {
      // this.lwJGLCanvas = new LayerViewLwjglLightWeightCanvas(this);
      this.lwJGLCanvas = new LayerViewLwjglCanvas(this);
      this.setGLComponent(this.lwJGLCanvas);
      this.lwJGLCanvas.setBackground(new Color(255, 255, 220));

    } catch (LWJGLException e) {
      logger.error("LWJGL creation error");
      logger.error(e.getMessage());
      e.printStackTrace();
    }
    // insert the canvas (containing the LW JGL canvas) into the JPanel
    // this.add(this.lwJGLCanvas, BorderLayout.CENTER);
    this.renderingManager = new SyncRenderingManager(this, RenderingType.LWJGL);

  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.layerview.LayerViewGLPanel#dispose()
   */
  @Override
  public void dispose() {
    if (this.lwJGLCanvas != null) {
      try {
        if (this.lwJGLCanvas.getContext() != null) {
          this.lwJGLCanvas.releaseContext();
        }
        this.lwJGLCanvas = null;
      } catch (Exception e) {
        logger
            .error("An error occurred releasing GL context " + e.getMessage());
      }
    }
    super.dispose();
  }

  @Override
  public final void repaint() {
    if (this.lwJGLCanvas != null) {
      this.lwJGLCanvas.repaint();
    }

  }

  @Override
  public final void paintComponent(final Graphics g) {
    try {
      this.lwJGLCanvas.paintGL();
    } catch (Exception e1) {
      // e1.printStackTrace();
      logger
          .error(I18N.getString("LayerViewPanel.PaintError") + " " + e1.getMessage()); //$NON-NLS-1$
    }
  }

  @Override
  public SyncRenderingManager getRenderingManager() {
    return this.renderingManager;
  }

}
