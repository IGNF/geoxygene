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
package fr.ign.cogit.geoxygene.appli.layer;

import java.awt.Color;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;

import fr.ign.cogit.geoxygene.appli.GeoxygeneConstants;
import fr.ign.cogit.geoxygene.appli.gl.GLContext;
import fr.ign.cogit.geoxygene.style.BackgroundDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLException;

public abstract class LayerViewGLCanvas extends AWTGLCanvas implements
        ComponentListener, MouseListener, MouseMotionListener {
    
    private static final long serialVersionUID = 1095977885262623231L; // Serializable UID
    private static final Color DEFAULT_BACKGROUND_COLOR= Color.WHITE; 
    protected LayerViewGLPanel parentPanel = null;
    private final boolean doPaintOverlay = true;
    private Color activeBackgroundColor;
   
    protected BufferedImage offscreenRenderedImg;
    protected boolean offScreenImgRendering = false;

    
    
    protected static Logger logger = Logger.getLogger(LayerViewGLCanvas.class
            .getName());

    /**
     * Constructor
     * 
     * @param parentPanel
     * @throws LWJGLException
     */
    public LayerViewGLCanvas(LayerViewGLPanel parentPanel)
            throws LWJGLException {
        super();
        if (parentPanel == null) {
            throw new IllegalArgumentException("invalid null parent Panel for "
                    + this.getClass().getSimpleName());
        }
        this.setParentPanel(parentPanel);
        this.addComponentListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.activeBackgroundColor = DEFAULT_BACKGROUND_COLOR;
        this.setBackground(DEFAULT_BACKGROUND_COLOR);
    }

    /**
     * @return the sld background
     */
    public BackgroundDescriptor getViewBackground() {
        return this.parentPanel.getViewBackground();
    }

    /**
     * Set the parent panel
     */
    protected final void setParentPanel(final LayerViewGLPanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    /**
     * @return the parentPanel
     */
    public LayerViewGLPanel getParentPanel() {
        return this.parentPanel;
    }

    /**
     * @return true if overlays have to be painted on rendering media
     */
    protected boolean doPaintOverlay() {
        return this.doPaintOverlay;
    }

    public final void doPaint() {
        this.paintGL();
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        if (this.getParentPanel() != null) {
            this.getParentPanel().dispatchEvent(e);
        }
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        if (this.getParentPanel() != null) {
            this.getParentPanel().dispatchEvent(e);
        }
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        if (this.getParentPanel() != null) {
            this.getParentPanel().dispatchEvent(e);
        }
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if (this.getParentPanel() != null) {
            this.getParentPanel().dispatchEvent(e);
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (this.getParentPanel() != null) {
            this.getParentPanel().dispatchEvent(e);
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        if (this.getParentPanel() != null) {
            this.getParentPanel().dispatchEvent(e);
        }
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        if (this.getParentPanel() != null) {
            this.getParentPanel().dispatchEvent(e);
        }
    }

    public void activateContext() {
        try {
            this.makeCurrent();
        } catch (LWJGLException e) {
            logger.error(e.getMessage());
        }

    }

    abstract public void reset();

    public abstract GLContext getGlContext() throws GLException;

    //TODO  this method should also check if FBO are available!    
    public abstract boolean isFBOActivated();

    public abstract void setFBO(boolean selected);

    public void setBackgroundColor(Color c){
        this.activeBackgroundColor  = c;
        this.repaint();
    }
    
    public Color getBackgroundColor(){
        return this.activeBackgroundColor;
    }


    public void renderToImage() {
        this.offScreenImgRendering = true;
    }

    public BufferedImage getOffscreenImage() {
        return this.offscreenRenderedImg;
    }


    
    
}