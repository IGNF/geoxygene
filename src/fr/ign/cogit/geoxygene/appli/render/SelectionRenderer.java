/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.appli.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.Symbolizer;

/**
 * A renderer to render a {@link Layer} into a {@link LayerViewPanel}.
 * 
 * @author Julien Perret
 * @see RenderingManager
 * @see Layer
 * @see LayerViewPanel
 */
public class SelectionRenderer implements Renderer {
	static Logger logger=Logger.getLogger(SelectionRenderer.class.getName());

	LayerViewPanel layerViewPanel = null;
	protected volatile boolean cancelled = false;
	protected volatile boolean rendering = false;
	protected volatile boolean rendered = false;
	BufferedImage image = null;
	Color fillColor = new Color (1f,1f,0f,0.5f);
	Color strokeColor = new Color (1f,1f,0f,1f);
	float strokeWidth = 2f;
	public Color getStrokeColor() {return this.strokeColor;}
	public void setStrokeColor(Color color) {this.strokeColor = color;}
	int pointRadius = 2;

	private Symbolizer symbolizer = new Symbolizer() {
		@Override
		public String getGeometryPropertyName() {return null;}
		@Override
		public Stroke getStroke() {return null;}
		@Override
		public boolean isLineSymbolizer() {return false;}
		@Override
		public boolean isPointSymbolizer() {return false;}
		@Override
		public boolean isPolygonSymbolizer() {return false;}
		@Override
		public boolean isRasterSymbolizer() {return false;}
		@Override
		public boolean isTextSymbolizer() {return false;}
		@Override
		public void paint(FT_Feature feature, Viewport viewport, Graphics2D graphics) {
			if (feature.getGeom()==null) return;
			if (feature.getGeom().isPolygon()||feature.getGeom().isMultiSurface()) {
				graphics.setColor(SelectionRenderer.this.fillColor);
				RenderUtil.fill(feature.getGeom(), viewport, graphics);
			}
			java.awt.Stroke bs = new BasicStroke(SelectionRenderer.this.strokeWidth,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f);
			graphics.setColor(SelectionRenderer.this.strokeColor);
			graphics.setStroke(bs);
			RenderUtil.draw(feature.getGeom(), viewport, graphics);
			try {
				graphics.setStroke(new BasicStroke(1,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f));
				for(DirectPosition position:viewport.toViewDirectPositionList(feature.getGeom().coord())) {
					GeneralPath shape = new GeneralPath();
					shape.moveTo(position.getX()-SelectionRenderer.this.pointRadius, position.getY()-SelectionRenderer.this.pointRadius);
					shape.lineTo(position.getX()+SelectionRenderer.this.pointRadius, position.getY()-SelectionRenderer.this.pointRadius);
					shape.lineTo(position.getX()+SelectionRenderer.this.pointRadius, position.getY()+SelectionRenderer.this.pointRadius);
					shape.lineTo(position.getX()-SelectionRenderer.this.pointRadius, position.getY()+SelectionRenderer.this.pointRadius);
					shape.lineTo(position.getX()-SelectionRenderer.this.pointRadius, position.getY()-SelectionRenderer.this.pointRadius);
					graphics.setColor(SelectionRenderer.this.strokeColor);
					graphics.fill(shape);
					graphics.setColor(Color.black);
					graphics.draw(shape);
				}
			} catch (NoninvertibleTransformException e) {}
		}

		@Override
		public void setGeometryPropertyName(String geometryPropertyName) {}

		@Override
		public void setStroke(Stroke stroke) {}
	}; 

	/**
	 * Constructor of renderer using a {@link Layer} and a {@link LayerViewPanel}
	 * @param theLayerViewPanel the panel to draws into
	 */
	public SelectionRenderer(LayerViewPanel theLayerViewPanel) {this.layerViewPanel=theLayerViewPanel;}

	@Override
	public boolean isRendering() {return this.rendering;}
	@Override
	public boolean isRendered() {return this.rendered;}

	/**
	 * Cancel the rendering. This method does not actually interrupt the thread but lets the thread know it should stop.
	 * @see Runnable
	 * @see Thread
	 */
	public void cancel() {this.cancelled = true;}

	/**
	 * Copy the rendered image the a 2D graphics
	 * @param graphics the 2D graphics to draw into
	 */
	public void copyTo(Graphics2D graphics) {if (this.image != null) graphics.drawImage(this.image, 0, 0, null);}

	/**
	 * Create a runnable for the renderer. A renderer create a new image to draw into. 
	 * If cancel() is called, the rendering stops as soon as possible. 
	 * When finished, set the variable rendering to false.
	 * @return a new runnable
	 * @see Runnable
	 * @see #cancel()
	 * @see #isRendering()
	 */
	public Runnable createRunnable() {
		if (this.image != null) {return null;}
		this.cancelled = false;
		return new Runnable() {
			public void run() {
				SelectionRenderer.this.rendering = true;
				SelectionRenderer.this.rendered = false;
				try {
					// it the rendering is cancel, stop
					if (SelectionRenderer.this.cancelled) return;
					// if either the width or the height of the panel is lesser or equal to 0, stop
					if (Math.min(SelectionRenderer.this.layerViewPanel.getWidth(),SelectionRenderer.this.layerViewPanel.getHeight())<=0) return;
					// create a new image
					SelectionRenderer.this.image = new BufferedImage(SelectionRenderer.this.layerViewPanel.getWidth(),SelectionRenderer.this.layerViewPanel.getHeight(),BufferedImage.TYPE_INT_ARGB);
					// do the actual rendering
					try {renderHook(SelectionRenderer.this.image);}
					catch (Throwable t) {
						// TODO WARN THE USER?
						t.printStackTrace(System.err);
						return;
					}
					// when time comes, repaint the panel
					SwingUtilities.invokeLater(new Runnable() {public void run() {SelectionRenderer.this.layerViewPanel.superRepaint();}});
				} finally {
					// the renderer is not rendering anymore ( used by isRendering() )
					SelectionRenderer.this.rendering = false;
					SelectionRenderer.this.rendered = true;
				}
			}
		};
	}

	/**
	 * Actually renders the layer in an image. Stop if cancelled is true.
	 * @param theImage the image to draw into
	 * @see #cancel()
	 */
	void renderHook(BufferedImage theImage) {
		if (this.cancelled) return;
		for (FT_Feature feature:this.layerViewPanel.getSelectedFeatures()) {
			if (this.cancelled) return;
			if (feature.getGeom() != null && !feature.getGeom().isEmpty()) render(feature,theImage);
		}	
	}

	/**
	 * Render a feature into an image using the given symbolizer
	 * @param feature
	 * @param theImage
	 */
	private void render(FT_Feature feature, BufferedImage theImage) {
		this.symbolizer.paint(feature,this.layerViewPanel.getViewport(),(Graphics2D) theImage.getGraphics());
	}

	/**
	 * Clear the image cache, i.e. delete the current image.
	 */
	public void clearImageCache() {this.image = null;}
	@Override
	public void clearImageCache(int x, int y, int width, int height) {
		if (this.cancelled) return;
		for(int i = Math.max(x, 0) ; i < Math.min(x+width, this.layerViewPanel.getWidth()) ; i++)
			for(int j = Math.max(y, 0) ; j < Math.min(y+height, this.layerViewPanel.getHeight()) ; j++) 
				this.image.setRGB(i, j, Transparency.TRANSLUCENT);
	}
	@Override
	public Runnable createFeatureRunnable(FT_Feature feature) {return null;}
	@Override
	public Runnable createLocalRunnable(GM_Object geom) {return null;}
}