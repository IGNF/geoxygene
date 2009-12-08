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

package fr.ign.cogit.geoxygene.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.batik.ext.awt.geom.Polygon2D;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;
import fr.ign.cogit.geoxygene.style.UserStyle;

/**
 * @author Julien Perret
 *
 */
public class DessinableGeoxygene implements Dessinable, Runnable {
	private final static Logger logger=Logger.getLogger(DessinableGeoxygene.class.getName());

	protected List<ChangeListener> listenerList = new ArrayList<ChangeListener>();

	/**
	 * Adds a <code>ChangeListener</code>.
	 * @param l the <code>ChangeListener</code> to be added
	 */
	public void addChangeListener(ChangeListener l) {
		if (listenerList==null) {
			if (logger.isTraceEnabled()) logger.trace("bizarre");
			listenerList = new ArrayList<ChangeListener>();
		}
		listenerList.add(l);
	}

	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created.
	 */
	public void fireActionPerformed(ChangeEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.toArray();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-1; i>=0; i-=1) {
			((ChangeListener)listeners[i]).stateChanged(e);
		}
	}

	private int nbPixelsMarge = 20;
	private int width;
	private int height;
	private Graphics2D graphics=null;

	private double taillePixel=10.0;
	@Override
	public double getTaillePixel() { return taillePixel; }
	@Override
	public void setTaillePixel(double tp) { taillePixel=tp; }

	private StyledLayerDescriptor sld;
	/**
	 * Renvoie la valeur de l'attribut sld.
	 * @return la valeur de l'attribut sld
	 */
	public StyledLayerDescriptor getSld() {return this.sld;}
	/**
	 * Affecte la valeur de l'attribut sld.
	 * @param sld l'attribut sld à affecter
	 */
	public void setSld(StyledLayerDescriptor sld) {this.sld = sld;}
	/**
	 * Renvoie la valeur de l'attribut graphics.
	 * @return la valeur de l'attribut graphics
	 */
	public Graphics2D getGraphics() {return this.graphics;}
	
	private BufferedImage image = null;
	/**
	 * Renvoie la valeur de l'attribut image.
	 * @return la valeur de l'attribut image
	 */
	public BufferedImage getImage() {return this.image;}
	/**
	 * @param sld
	 */
	public DessinableGeoxygene(StyledLayerDescriptor sld) {this.sld=sld;}	
	private GM_Envelope enveloppeAffichage = null;
	@Override
	public GM_Envelope getEnveloppeAffichage() {return this.enveloppeAffichage;}
	
	private DirectPosition centreGeo = new DirectPosition(0.0, 0.0);
	@Override
	public DirectPosition getCentreGeo() { return centreGeo; }
	@Override
	public void setCentreGeo(DirectPosition centreGeo) {this.centreGeo = centreGeo;}
	
	@Override
	public synchronized void majLimitesAffichage(int newWidth, int newHeight) {
		this.image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB_PRE);
		this.graphics = image.createGraphics();
		if (logger.isTraceEnabled()) logger.trace("majLimitesAffichage("+newWidth+","+newHeight+")");
		if (this.getGraphics()==null) return;
		this.width=newWidth;
		this.height=newHeight;
		if (logger.isTraceEnabled()) logger.trace("Limites : "+newWidth+" - "+newHeight);
		double XMin = pixToCoordX(-nbPixelsMarge);
		double XMax = pixToCoordX(newWidth+nbPixelsMarge);
		double YMin = pixToCoordY(newHeight+nbPixelsMarge);
		double YMax = pixToCoordY(-nbPixelsMarge);
		enveloppeAffichage = new GM_Envelope( XMin, XMax, YMin, YMax );
		if (logger.isTraceEnabled()) logger.trace("enveloppeAffichage : "+enveloppeAffichage);

		affineTransform = AffineTransform.getTranslateInstance(0, newHeight);
		affineTransform.scale(1/taillePixel,-1/taillePixel);
		affineTransform.translate(newWidth*0.5*taillePixel-centreGeo.getX(),newHeight*0.5*taillePixel-centreGeo.getY());

		if (useCache) majCachedFeatures();
		//clearShapeCache();
	}
	/**
	 * Mise à jour du cache contenant les features à l'intérieur des limites de l'affichage, i.e. les features visibles.
	 */
	private void majCachedFeatures() {
		if (sld==null) return;
		if (logger.isTraceEnabled()) {logger.trace("Début du calcul des features à mettre dans le cache");}
		double debut = System.currentTimeMillis();
		for (Layer layer:sld.getLayers()) setCachedFeatures(layer);
		double fin = System.currentTimeMillis();
		if (logger.isTraceEnabled()) {logger.trace("("+(fin-debut)+"Fin du calcul des features à mettre dans le cache");}
	}

	public int coordToPixX(double x){ return (int)((x-(centreGeo.getX()-width*0.5*taillePixel))/taillePixel);}		
	public int coordToPixY(double y){ return (int)(height+(centreGeo.getY()-height*0.5*taillePixel-y)/taillePixel);}
	public double pixToCoordX(int x){ return centreGeo.getX()-width*0.5*taillePixel+x*taillePixel;}
	public double pixToCoordY(int y){ return centreGeo.getY()-height*0.5*taillePixel+(height-y)*taillePixel;}

	AffineTransform affineTransform=null;
	public Shape toScreen(Shape s) {return affineTransform.createTransformedShape(s);}

	@Override
	public void dessiner(Graphics2D g) throws InterruptedException {
		if (sld==null) {
			logger.info("SLD null");
			return;
		}
		if (logger.isTraceEnabled()) {logger.trace("dessiner() ");}
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAliasing?RenderingHints.VALUE_ANTIALIAS_ON:RenderingHints.VALUE_ANTIALIAS_OFF);
		for (Layer layer:sld.getLayers()) {
			if (logger.isTraceEnabled()) logger.trace("dessiner le layer "+layer);
			dessiner(g, layer, (useCache)?getCachedFeatures(layer):layer.getFeatureCollection());
			fireChange();
		}
	}

	boolean useCache = false;
	Map<Layer,FT_FeatureCollection<? extends FT_Feature>> cachedFeatures = new HashMap<Layer,FT_FeatureCollection<? extends FT_Feature>>();
	/**
	 * @param layer
	 */
	private synchronized void setCachedFeatures(Layer layer) {
		if (layer.getFeatureCollection()==null) return;
		double debut = System.currentTimeMillis();
		cachedFeatures.put(layer, layer.getFeatureCollection().select(this.enveloppeAffichage));
		double fin = System.currentTimeMillis();
		if (logger.isTraceEnabled()) {logger.trace("("+(fin-debut)+") Fin du calcul des features à mettre dans le cache pour la couche "+layer.getName());}
	}
	/**
	 * @param layer
	 * @return the features in cache
	 */
	private FT_FeatureCollection<? extends FT_Feature> getCachedFeatures(Layer layer) {return cachedFeatures.get(layer);}
	
	public void dessiner(Graphics2D g, Layer layer,FT_FeatureCollection<? extends FT_Feature> features) throws InterruptedException {
		if (features == null) return;
		if (logger.isTraceEnabled()) {logger.trace("dessiner() sur "+features.size()+" features");}
		double debut = System.currentTimeMillis();
		for (Style style:layer.getStyles()) {
			dessiner(g, style,features);
			//fireChange();
		}
		double fin = System.currentTimeMillis();
		if (logger.isTraceEnabled()) {logger.trace("dessiner() terminé pour la couche "+layer.getName()+" en "+(fin-debut)+")");}
	}

	/**
	 * @param style
	 * @param features
	 * @throws InterruptedException
	 */
	public void dessiner(Graphics2D g, Style style,FT_FeatureCollection<? extends FT_Feature> features) throws InterruptedException {
		if (style.isUserStyle()) {
			UserStyle userStyle = (UserStyle) style;
			for (FeatureTypeStyle featureTypeStyle:userStyle.getFeatureTypeStyles()) {
				//if (logger.isDebugEnabled()) logger.debug("Dessiner le featureTypeStyle "+featureTypeStyle);
				/**
				 * TODO les règles devraient etre dans l'ordre de priorité et donc 
				 * affichées dans l'ordre inverse (OGC 02-070 p.26)
				 */
				//if (logger.isDebugEnabled()) logger.debug(featureTypeStyle.getRules().size()+" Rules");
				for(int indexRule=featureTypeStyle.getRules().size()-1;indexRule>=0;indexRule--) {
					Rule rule=featureTypeStyle.getRules().get(indexRule);
					if (rule.getFilter()==null) {
						for (Symbolizer symbolizer:rule.getSymbolizers()) this.dessiner(g, symbolizer,features);
					} else {
						FT_FeatureCollection<FT_Feature> filteredFeatures = new FT_FeatureCollection<FT_Feature>();
						int size = features.size();
						for (int index = 0; index < size ; index++) {
							FT_Feature feature=features.get(index);
							if (rule.getFilter().evaluate(feature)) filteredFeatures.add(feature);
						}
						if (logger.isTraceEnabled()) logger.trace(filteredFeatures.size()+" features filtered");
						for (Symbolizer symbolizer:rule.getSymbolizers()) this.dessiner(g, symbolizer,filteredFeatures);
					}
				}
				//fireChange();
			}
		}
	}

	/**
	 * Dessine une liste de Features dans un Graphics2D à l'aide d'un Symbolizer.
	 * Tous les parcours de FT_FeatureCollection de cette classe sont effectués dans cette méthde.
	 * @param symbolizer
	 * @param features
	 */
	@SuppressWarnings("unchecked")
	public void dessiner(Graphics2D g, Symbolizer symbolizer,FT_FeatureCollection<? extends FT_Feature> features)  throws InterruptedException {
		if (symbolizer.isRasterSymbolizer()) {
			RasterSymbolizer rasterSymbolizer=(RasterSymbolizer)symbolizer;
			this.dessiner(rasterSymbolizer);
			return;
		}
		if (symbolizer.isTextSymbolizer()) {
			TextSymbolizer textSymbolizer = (TextSymbolizer) symbolizer;
			if (textSymbolizer.getLabel()==null) return;
			Color fillColor = Color.black;
			if (textSymbolizer.getFill()!=null) fillColor = textSymbolizer.getFill().getColor();
			Font font = null;
			if (textSymbolizer.getFont()!=null) font = textSymbolizer.getFont().toAwfFont();
			if (font==null) font = new java.awt.Font("Default",java.awt.Font.PLAIN,10);
			Color haloColor = null;
			float haloRadius = 1.0f;
			if (textSymbolizer.getHalo()!=null) {
				if (textSymbolizer.getHalo().getFill()!=null) haloColor = textSymbolizer.getHalo().getFill().getColor();
				else haloColor = Color.white;
				haloRadius = textSymbolizer.getHalo().getRadius();
			}

			int size = features.size();
			for (int index = 0; index < size ; index++) {
				FT_Feature feature=features.get(index);
				String texte = (String) feature.getAttribute(textSymbolizer.getLabel());
				if (feature.getGeom() instanceof GM_Point) {
					this.dessinerText(g, fillColor, haloColor, haloRadius, font, texte, ((GM_Point)feature.getGeom()).getPosition());
				} else if (feature.getGeom() instanceof GM_Polygon) {
					this.dessinerText(g, fillColor, haloColor, haloRadius, font, texte, ((GM_Polygon)feature.getGeom()).centroid());
				} else if (feature.getGeom() instanceof GM_MultiSurface) {
					this.dessinerText(g, fillColor, haloColor, haloRadius, font, texte, ((GM_MultiSurface<GM_Polygon>)feature.getGeom()).centroid());
				} else if (feature.getGeom() instanceof GM_LineString) {
					this.dessinerText(g, fillColor, haloColor, haloRadius, font, texte, (GM_LineString)feature.getGeom());
				} else if (feature.getGeom() instanceof GM_MultiCurve) {
					this.dessinerText(g, fillColor, haloColor, haloRadius, font, texte, ((GM_MultiCurve<GM_LineString>)feature.getGeom()).get(0));
				} else {
					logger.info(feature.getGeom().getClass().getSimpleName());
				}
			}
			return;
		}
		if (symbolizer.isPointSymbolizer()) {
			PointSymbolizer pointSymbolizer=(PointSymbolizer)symbolizer;
			int size = features.size();
			for (int index = 0; index < size ; index++) {
				FT_Feature feature=features.get(index);
				if (feature.getGeom() instanceof GM_Point)
					this.dessiner(g, pointSymbolizer, ((GM_Point)feature.getGeom()).getPosition());
				else this.dessiner(g, pointSymbolizer, (feature.getGeom()).centroid());
			}
			return;
		}
		if (symbolizer.isPolygonSymbolizer()) {
			PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) symbolizer;
			Color fillColor = null;
			if (polygonSymbolizer.getFill()!=null) fillColor = polygonSymbolizer.getFill().getColor();
			int size = features.size();
			for (int index = 0; index < size ; index++) {
				FT_Feature feature=features.get(index);
				if (feature.getGeom()==null) continue;
				if (feature.getGeom().isPolygon()) {
					if (fillColor!=null) this.remplir(g, fillColor, (GM_Polygon)feature.getGeom());
					if (symbolizer.getStroke()!=null) {
						if (symbolizer.getStroke().getGraphicType()==null) {
							// Solid color
							this.dessiner(g, symbolizer.getStroke(), (GM_Polygon)feature.getGeom());
						}
					}
				} else if (feature.getGeom().isMultiSurface()) {
					for (GM_Polygon element:((GM_MultiSurface<GM_Polygon>)feature.getGeom()).getList()) {
						if (fillColor!=null) this.remplir(g, fillColor, element);
						if (symbolizer.getStroke()!=null) {
							if (symbolizer.getStroke().getGraphicType()==null) {
								// Solid color
								this.dessiner(g, symbolizer.getStroke(), element);
							}
						}
					}
				}
				fireObjectChange();
			}
			return;
		}
		if (symbolizer.isLineSymbolizer()) {
			if (symbolizer.getStroke()!=null) {
				if (symbolizer.getStroke().getGraphicType()==null) {
					// Solid color
					int size = features.size();
					for (int index = 0; index < size ; index++) {
						FT_Feature feature=features.get(index);
						if (feature.getGeom()==null) continue;
						if (feature.getGeom().isLineString()) {
							this.dessiner(g, symbolizer.getStroke(), (GM_LineString) feature.getGeom());
						} else if (feature.getGeom().isMultiCurve()) {
							for (GM_LineString element:((GM_MultiCurve<GM_LineString>)feature.getGeom()).getList()) {
								this.dessiner(g, symbolizer.getStroke(), element);
							}
						}
					}
				} else {
					logger.warn("Les graphics ne sont pas gérés pour l'instant");
				}
			}
		}
	}
	/**
	 * Dessiner un texte.
	 * @param g l'objet Graphics2D
	 * @param fillColor couleur de remplissage du texte
	 * @param haloColor couleur du halo du texte
	 * @param haloRadius rayon du halo du texte
	 * @param font police du texte
	 * @param texte texte à dessiner
	 * @param position position du texte à dessiner
	 */
	private void dessinerText(Graphics2D g, Color fillColor, Color haloColor, float haloRadius, Font font, String texte, DirectPosition position) {
		if (texte==null) return;
		// Find the size of string s in font f in the current Graphics context g.
		FontMetrics fm = g.getFontMetrics(font);
		java.awt.geom.Rectangle2D rect = fm.getStringBounds(texte, g);
		g.setFont(font);
		int textHeight = (int)(rect.getHeight()); 
		int textWidth  = (int)(rect.getWidth());
		// Center text horizontally and vertically
	    int centreX=coordToPixX(position.getX())-(textWidth/2);
	    int centreY=coordToPixY(position.getY())-(textHeight/2)+ fm.getAscent();
	    
	    FontRenderContext frc = g.getFontRenderContext();
		GlyphVector gv = font.createGlyphVector(frc, texte);
	    //halo
	    if (haloColor!=null) {
	    	Shape shape=gv.getOutline(centreX,centreY);
		    g.setColor(haloColor);
		    g.setStroke(new BasicStroke(haloRadius,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
		    g.draw(shape);
	    }
	    g.setColor(fillColor);
	    g.drawGlyphVector(gv, centreX, centreY);
	    //fireObjectChange();
	}
	/**
	 * Dessiner un texte.
	 * @param g l'objet Graphics2D
	 * @param fillColor couleur de remplissage du texte
	 * @param haloColor couleur du halo du texte
	 * @param haloRadius rayon du halo du texte
	 * @param font police du texte
	 * @param texte texte à dessiner
	 * @param line ligne support du texte à dessiner
	 * TODO à débugger : ça ne marche pas encore bien
	 */
	private void dessinerText(Graphics2D g, Color fillColor, Color haloColor, float haloRadius, Font font, String texte, GM_LineString line) {
		if (texte==null) return;
		FontMetrics fm = g.getFontMetrics(font);
		g.setFont(font);
		int lineLength = line.sizeControlPoint();
		int[] x = new int[lineLength];
		int[] y = new int[lineLength];
		double[] l = new double[lineLength-1];
		double[] a = new double[lineLength-1];
		for (int i = 0 ; i < lineLength ; i++) {
			x[i] = this.coordToPixX(line.getControlPoint(i).getX());
			y[i] = this.coordToPixY(line.getControlPoint(i).getY());
		}
		for (int i = 0 ; i < lineLength-1 ; i++) {
			double dx = x[i+1]-x[i];
			double dy = y[i+1]-y[i];
			l[i] = Math.sqrt(dx*dx+dy*dy);
			a[i] = Math.atan2(dy, dx);
		}
		FontRenderContext frc = g.getFontRenderContext();
		GlyphVector gv = font.createGlyphVector(frc, texte);
		int length = gv.getNumGlyphs();
		int lineIndex = 0;
		double lineU = 0;
		for (int i = 0; i < length; i++) {
			Point2D p = gv.getGlyphPosition(i);
			double angle = a[lineIndex];
			AffineTransform at = AffineTransform.getTranslateInstance(x[lineIndex],y[lineIndex]);
			at.rotate(angle);
			at.translate(lineU,p.getY()-fm.getAscent()/2);
			Shape glyph = gv.getGlyphOutline(i,(float)-p.getX(),0.0f);
			Shape transformedGlyph = at.createTransformedShape(glyph);
		    //halo
		    if (haloColor!=null) {
			    g.setColor(haloColor);
			    g.setStroke(new BasicStroke(haloRadius,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			    g.draw(transformedGlyph);
		    }
		    g.setColor(fillColor);
			g.fill(transformedGlyph);
			lineU+=gv.getGlyphMetrics(i).getAdvance();
			while((lineIndex<lineLength-1)&&(lineU>=l[lineIndex])) {
				lineU-=l[lineIndex];
				lineIndex++;
			}
			if (lineIndex==lineLength-1) lineIndex=lineLength-2;
		}
		//fireObjectChange();
	}
	/**
	 * @param rasterSymbolizer
	 */
	private void dessiner(RasterSymbolizer rasterSymbolizer) {
		// TODO pas géré pour le moment
	}

	/**
	 * @param couleur
	 * @param geom
	 */
	@SuppressWarnings("unchecked")
	public void dessiner(Graphics2D g, Color couleur, GM_Object geom) {
		if (geom.isPoint()) remplirCarre(g, ((GM_Point) geom).getPosition());
		else if (geom.isLineString()) dessiner(g, couleur,(GM_LineString)geom);
		else if (geom.isMultiCurve()) dessiner(g, couleur,(GM_MultiCurve)geom);
		else if (geom.isPolygon()) remplir(g, couleur,(GM_Polygon)geom);
		else if (geom.isMultiSurface()) remplir(g, couleur,(GM_MultiSurface<GM_Polygon>)geom);
	}
	
	/**
	 * Remplit un cercle
	 * @param g l'objet graphics2D
	 * @param position le centre du cercle
	 * @param radius le rayon du cercle
	 */
	public void remplirCercle(Graphics2D g, DirectPosition position, int radius) {
		g.fillOval((int)position.getX()-radius, (int)position.getY()-radius, 2*radius, 2*radius);
	}

	/**
	 * Remplit un carré de 6 de côté. 
	 * @see #remplirCarre(Graphics2D, DirectPosition, int)
	 * @param g l'objet graphics2D
	 * @param position le centre du carré
	 */
	public void remplirCarre(Graphics2D g, DirectPosition position) {remplirCarre(g, position,3);}

	/**
	 * Remplit un carré. 
	 * @param g l'objet graphics2D
	 * @param position le centre du carré
	 * @param radius le demi-côté du carré
	 */
	public void remplirCarre(Graphics2D g, DirectPosition position, int radius) {
		g.fillRect((int)position.getX()-radius, (int)position.getY()-radius, 2*radius, 2*radius);
	}

	/**
	 * Dessine le contour d'un cercle.
	 * @see #dessinerCercle(Graphics2D, int, int, int)
	 * @param g l'objet graphics2D
	 * @param position le centre du cercle
	 * @param radius le rayon du cercle
	 */
	public void dessinerCercle(Graphics2D g, DirectPosition position, int radius) {
		this.dessinerCercle(g, (int)position.getX(), (int)position.getY(), radius);
	}

	/**
	 * Dessine le contour d'un cercle
	 * @param g l'objet graphics2D
	 * @param x la position en X du centre du cercle
	 * @param y la position en Y du centre du cercle
	 * @param radius le rayon du cercle
	 */
	public void dessinerCercle(Graphics2D g, int x ,int y, int radius) {
		g.drawOval(x-radius, y-radius, 2*radius, 2*radius);
	}

	/**
	 * Dessine le contour d'un carré de 6 de côté. 
	 * @param g l'objet graphics2D
	 * @param position le centre du carré
	 */
	public void dessinerCarre(Graphics2D g, DirectPosition position) {dessinerCarre(g, position,3);}

	/**
	 * Dessine le contour d'un carré. 
	 * @param g l'objet graphics2D
	 * @param position le centre du carré
	 * @param radius le demi-côté du carré
	 */
	public void dessinerCarre(Graphics2D g, DirectPosition position, int radius) {
		g.drawRect((int)position.getX()-radius, (int)position.getY()-radius, 2*radius, 2*radius);
	}

	/**
	 * Dessine un point symbolizer.
	 * @param g l'objet graphics2D
	 * @param pointSymbolizer la symbolisation
	 * @param position le centre de la symbolisation
	 */
	private void dessiner(Graphics2D g, PointSymbolizer pointSymbolizer, DirectPosition position) {
		if (pointSymbolizer.getGraphic()==null) return;
		for(Mark mark:pointSymbolizer.getGraphic().getMarks()) {
			Shape shape = mark.toShape();
			float size = pointSymbolizer.getGraphic().getSize();
			AffineTransform at = AffineTransform.getTranslateInstance(coordToPixX(position.getX()),coordToPixY(position.getY()));
			at.rotate(pointSymbolizer.getGraphic().getRotation());
			at.scale(size,size);
			shape = at.createTransformedShape(shape);
			g.setColor((mark.getFill()==null)?Color.gray:mark.getFill().getColor());
			g.fill(shape);
			g.setColor((mark.getStroke()==null)?Color.black:mark.getStroke().getColor());
			g.draw(shape);
		}
		for(ExternalGraphic graphic:pointSymbolizer.getGraphic().getExternalGraphics()) {
			Image onlineImage = graphic.getOnlineResource();
			g.drawImage(onlineImage, coordToPixX(position.getX())-onlineImage.getWidth(null)/2, coordToPixY(position.getY())-onlineImage.getHeight(null)/2, null);
		}
		//fireObjectChange();
	}
	
	/**
	 * Remplit un polygone.
	 * @param g l'objet graphics2D
	 * @param color couleur du remplissage
	 * @param poly géométrie du polygone
	 */
	private void remplir(Graphics2D g, Color color, GM_Polygon poly) {
		g.setColor(color);
		remplir(g, poly);
	}
	static int nbPoly=0;
	/**
	 * Remplit un polygone en utilisant la couleur courante.
	 * @param g l'objet graphics2D
	 * @param poly géométrie du polygone
	 */
	private void remplir(Graphics2D g, GM_Polygon poly) {
		GM_Envelope envelope = poly.envelope();
		if ((envelope.width()<=taillePixel)&&(envelope.height()<=taillePixel)) return;
		//Polygon2D p = (Polygon2D) shapeCache.get(poly);
		//if (p==null) try {
		try{
			int nb=poly.coord().size()+poly.getInterior().size();
			
			float[] geoX=new float[nb], geoY=new float[nb];
			
			//int[] x=new int[nb], y=new int[nb];
			//enveloppe exterieure
			GM_Ring ls=poly.getExterior();
			/*
			int x0=coordToPixX(ls.coord().get(0).getX());
			int y0=coordToPixY(ls.coord().get(0).getY());
			for(int i=0;i<ls.coord().size();i++){
				x[i]=coordToPixX(ls.coord().get(i).getX());
				y[i]=coordToPixY(ls.coord().get(i).getY());
			}
			*/
			double x0=ls.coord().get(0).getX();
			double y0=ls.coord().get(0).getY();
			for(int i=0;i<ls.coord().size();i++){
				geoX[i]=(float) ls.coord().get(i).getX();
				geoY[i]=(float) ls.coord().get(i).getY();
			}
			//trous
			int index=ls.coord().size();
			/*
			for(int j=0;j<poly.getInterior().size();j++){
				ls=poly.getInterior(j);
				for(int i=index;i<index+ls.coord().size();i++){
					x[i]=coordToPixX(ls.coord().get(i-index).getX());
					y[i]=coordToPixY(ls.coord().get(i-index).getY());
				}//i
				x[index+ls.coord().size()]=x0;
				y[index+ls.coord().size()]=y0;
				index+=ls.coord().size()+1;
			}//j
			*/
			for(int j=0;j<poly.getInterior().size();j++){
				ls=poly.getInterior(j);
				for(int i=index;i<index+ls.coord().size();i++){
					geoX[i]=(float) ls.coord().get(i-index).getX();
					geoY[i]=(float) ls.coord().get(i-index).getY();
				}//i
				geoX[index+ls.coord().size()]=(float) x0;
				geoY[index+ls.coord().size()]=(float) y0;
				index+=ls.coord().size()+1;
			}//j
			//getG2d().fillPolygon(x,y,nb);
			Polygon2D p = new Polygon2D(geoX,geoY,nb);
			//shapeCache.put(poly,p);
			g.fill(toScreen(p));
		} catch(Exception e) {
			logger.error("Impossible de remplir le polygone "+poly);
			logger.debug(e.getCause());
			return;
		}
		//g.fill(toScreen(p));
		//fireObjectChange();
	}
	
	/**
	 * Remplit un multi-polygone en utilisant la couleur courante.
	 * @param g l'objet graphics2D
	 * @param multiPoly géométrie du multi-polygone
	 */
	private void remplir(Graphics2D g, GM_MultiSurface<GM_Polygon> multiPoly) {
		for (GM_Polygon poly:multiPoly.getList()) remplir(g, poly);
	}

	/**
	 * Remplit un multi-polygone.
	 * @param g l'objet graphics2D
	 * @param couleur
	 * @param multiPoly géométrie du multi-polygone
	 */
	public void remplir(Graphics2D g, Color couleur, GM_MultiSurface<GM_Polygon> multiPoly) {
		g.setColor(couleur);
		remplir(g, multiPoly);
	}

	/**
	 * Dessine le contour d'un polygone.
	 * @param g l'objet graphics2D
	 * @param stroke le trait utilise pour le dessin
	 * @param poly géométrie du polygone
	 */
	public void dessiner(Graphics2D g, Stroke stroke, GM_Polygon poly) {
		Color color = stroke.getColor();
		java.awt.Stroke bs = stroke.toAwtStroke();
		g.setColor(color);
		g.setStroke(bs);
		dessiner(g, poly);
	}
	
	/**
	 * Dessine le contour d'un multi polygone.
	 * @param g l'objet graphics2D
	 * @param couleur couleur du trait
	 * @param multiPoly le multi polygone à dessiner
	 * @param d la largeur du trait
	 * @param cap type de fin du trait
	 * @param join type de join entre les lignes du trait
	 */
	public void dessiner(Graphics2D g, Color couleur, GM_MultiSurface<GM_Polygon> multiPoly, float d, int cap, int join) {
		g.setStroke(new BasicStroke(d,cap,join));
		g.setColor(couleur);
		for (GM_Polygon poly:multiPoly.getList()) dessiner(g, poly);
	}

	/**
	 * Dessine le contour d'un multi polygone.
	 * @param g l'objet graphics2D
	 * @param couleur couleur du trait
	 * @param multiPoly le multi polygone à dessiner
	 */
	public void dessiner(Graphics2D g, Color couleur, GM_MultiSurface<GM_Polygon> multiPoly) {
		g.setColor(couleur);
		dessiner(g, multiPoly);
	}
	
	/**
	 * Dessine le contour d'un multi polygone.
	 * @param g l'objet graphics2D
	 * @param multiPoly le multi polygone à dessiner
	 */
	public void dessiner(Graphics2D g, GM_MultiSurface<GM_Polygon> multiPoly) {
		for (GM_Polygon poly:multiPoly.getList()) dessiner(g, poly);
	}

	/**
	 * Dessine le contour d'un polygone.
	 * @see #dessiner(Graphics2D, Color, GM_Polygon)
	 * @param g l'objet graphics2D
	 * @param couleur la couleur du trait
	 * @param poly le polygone à dessiner
	 * @param d la largeur du trait
	 * @param cap type de fin du trait
	 * @param join type de join entre les lignes du trait
	 */
	public void dessiner(Graphics2D g, Color couleur, GM_Polygon poly, float d, int cap, int join) {
		g.setStroke(new BasicStroke(d,cap,join));
		dessiner(g, couleur, poly);
	}
	
	/**
	 * Dessine le contour d'un polygone avec le trait courant.
	 * @see #dessiner(Graphics2D, GM_Polygon)
	 * @param g l'objet graphics2D
	 * @param couleur la couleur du trait
	 * @param poly le polygone à dessiner
	 */
	public void dessiner(Graphics2D g, Color couleur, GM_Polygon poly) {
		g.setColor(couleur);
		dessiner(g, poly);
	}

	/**
	 * Dessine le contour d'un polygone en utilisant la couleur et le trait courants.
	 * @see #dessiner(Graphics2D, GM_LineString)
	 * @param g l'objet graphics2D
	 * @param poly le polygone à dessiner
	 */
	public void dessiner(Graphics2D g, GM_Polygon poly) {
		GM_Envelope envelope = poly.envelope();
		if ((envelope.width()<=this.taillePixel)&&(envelope.height()<=this.taillePixel)) {
			DirectPosition p = envelope.center();
			int x = coordToPixX(p.getX());
			int y = coordToPixY(p.getY());
			g.drawLine(x,y,x,y);
			return;
		}
		dessiner(g, poly.exteriorLineString());
		for(int i=0;i<poly.getInterior().size() ;i++) dessiner(g, poly.interiorLineString(i));
		//fireObjectChange();
	}
	/**
	 * Dessine le contour d'une multi ligne.
	 * @see #dessiner(Graphics2D, Color, GM_LineString)
	 * @param g l'objet graphics2D
	 * @param couleur la couleur du trait
	 * @param multiLine la multi ligne à dessiner
	 * @param d la largeur du trait
	 * @param cap type de fin du trait
	 * @param join type de join entre les lignes du trait
	 */
	public void dessiner(Graphics2D g, Color couleur, GM_MultiCurve<GM_LineString> multiLine,float d, int cap, int join) {
		g.setStroke(new BasicStroke(d,cap,join));
		dessiner(g, couleur,multiLine);
	}
	/**
	 * Dessine le contour d'une multi ligne.
	 * @see #dessiner(Graphics2D, GM_MultiCurve)
	 * @param g l'objet graphics2D
	 * @param couleur la couleur du trait
	 * @param multiLine la multi ligne à dessiner
	 */
	public void dessiner(Graphics2D g, Color couleur, GM_MultiCurve<GM_LineString> multiLine) {
		g.setColor(couleur);
		dessiner(g, multiLine);
	}
	/**
	 * Dessine le contour d'une multi ligne.
	 * @see #dessiner(Graphics2D, GM_LineString)
	 * @param g l'objet graphics2D
	 * @param multiLine la multi ligne à dessiner
	 */
	private void dessiner(Graphics2D g, GM_MultiCurve<GM_LineString> multiLine) {
		for (GM_LineString line:multiLine.getList()) dessiner(g, line);
	}

	/**
	 * Dessiner le contour d'une ligne.
	 * @see #dessiner(Graphics2D, Color, GM_LineString)
	 * @param g l'objet graphics2D
	 * @param stroke le trait à utiliser
	 * @param line la ligne à dessiner
	 */
	private void dessiner(Graphics2D g, Stroke stroke, GM_LineString line) {
		g.setStroke(stroke.toAwtStroke());
		dessiner(g, stroke.getColor(),line);
	}
	/**
	 * Dessine le contour d'une ligne.
	 * @see #dessiner(Graphics2D, Color, GM_LineString)
	 * @param g l'objet graphics2D
	 * @param couleur couleur du trait
	 * @param line la ligne à dessiner
	 * @param d la largeur du trait
	 * @param cap type de fin du trait
	 * @param join type de join entre les lignes du trait
	 */
	public void dessiner(Graphics2D g, Color couleur, GM_LineString line, float d, int cap, int join) {
		g.setStroke(new BasicStroke(d,cap,join));
		dessiner(g, couleur,line);
	}
	/**
	 * Dessine le contour d'une ligne.
	 * @see #dessiner(Graphics2D, GM_LineString)
	 * @param g l'objet graphics2D
	 * @param couleur couleur du trait
	 * @param line la ligne à dessiner
	 */
	public void dessiner(Graphics2D g, Color couleur, GM_LineString line) {
		g.setColor(couleur);
		dessiner(g, line);
	}
	/**
	 * Dessine le contour d'une ligne en utilisant la couleur et le trait courants.
	 * @param g l'objet graphics2D
	 * @param line la ligne à dessiner
	 */
	private void dessiner(Graphics2D g, GM_LineString line) {
		//GeneralPath p = (GeneralPath) shapeCache.get(line);
		//if (p==null) {
			DirectPositionList coords=line.coord();
			GeneralPath p=new GeneralPath();

			int nb = coords.size();
			
			/*
			int x=coordToPixX(coords.get(0).getX());
			int y=coordToPixY(coords.get(0).getY());
			p.moveTo(x,y);
			for(int j=1; j<coords.size(); j++){
				x=coordToPixX(coords.get(j).getX());
				y=coordToPixY(coords.get(j).getY());
				p.lineTo(x,y);
			}
			*/
			if (nb==0) {
				logger.error("Impossible de dessiner la ligne "+line);
				return;
			}
			float x=(float) coords.get(0).getX();
			float y=(float) coords.get(0).getY();
			p.moveTo(x,y);
			for(int j=1; j<coords.size(); j++){
				x=(float) coords.get(j).getX();
				y=(float) coords.get(j).getY();
				p.lineTo(x,y);
			}
			
			//shapeCache.put(line,p);
			g.draw(toScreen(p));

		//}
		//g.draw(toScreen(p));
		//fireObjectChange();
	}
	
	//Map<GM_Object,Shape> shapeCache = new HashMap<GM_Object,Shape>();
	/**
	 * Nettoie le cache contenant les formes pour le dessin.
	 */
	//public void clearShapeCache() {shapeCache.clear();}
	
	/**
	 * Lance le processus de dessin.
	 */
	public void start() {
		Thread t = new Thread(this);
    	threadMaj = new ThreadVar(t);
        t.start();
	}
	
	@Override
	public void run() {
		if (logger.isTraceEnabled()) logger.trace("run()");
        final Runnable doFinished = new Runnable() {public void run() { finishedMaj(); }};
        try {
            if (this.graphics!=null) this.dessiner(this.graphics);
        } catch (InterruptedException e) {
       	 if (logger.isTraceEnabled()) logger.trace("InterruptedException pendant majImage()");
		}
        finally {
            //threadMaj.clear();
        }
        SwingUtilities.invokeLater(doFinished);
	}
    /** 
     * Classe pour la gestion du processus de dessin.
     */
    public static class ThreadVar {
        private Thread thread;
        ThreadVar(Thread t) { thread = t; }
        public synchronized Thread get() { return thread; }
        synchronized void clear() { thread = null; }
    }

    private ThreadVar threadMaj = null;

    /**
     * Interromp le processus de dessin.
     */
    public void interruptMaj() {
    	if (threadMaj==null) return;
        Thread t = threadMaj.get();
        if (t != null) {t.interrupt();}
    }
    
    /**
     * Méthode appelée quand le processus est terminé.
     */
    public void finishedMaj() {
    	if (logger.isTraceEnabled()) logger.trace("finishedMaj");
    	fireChange();
    }
    
    /**
     * Informe les listeners que l'image que l'objet dessine a été modifiée.
     */
    private void fireChange() {
    	objectsChange=0;
    	this.fireActionPerformed(new ChangeEvent(this));
    }

    int objectsChange=0;
    /**
     * Informe les listeners que l'image que l'objet dessine a été modifiée.
     */
    private void fireObjectChange() {
    	objectsChange++;
    	if (objectsChange>1000) {fireChange();}
    }

    /**
	 * Renvoie la valeur de l'attribut threadMaj.
	 * @return la valeur de l'attribut threadMaj
	 */
	public ThreadVar getThreadMaj() {
		return this.threadMaj;
	}
	/**
	 * Affecte la valeur de l'attribut threadMaj.
	 * @param threadMaj l'attribut threadMaj à affecter
	 */
	public void setThreadMaj(ThreadVar threadMaj) {
		this.threadMaj = threadMaj;
	}

	/**
	 * indique si l'affichage utilise l'antialiasing
	 */
	public boolean antiAliasing = true;
	/**
	 * Renvoie la valeur de l'attribut antiAliasing.
	 * @return la valeur de l'attribut antiAliasing
	 */
	public boolean isAntiAliasing() {return this.antiAliasing;}
	/**
	 * Affecte la valeur de l'attribut antiAliasing.
	 * @param antiAliasing l'attribut antiAliasing à affecter
	 */
	public void setAntiAliasing(boolean antiAliasing) {this.antiAliasing = antiAliasing;}

	///////////////////////////////
	// Méthodes pour la compatibilité avec Mirage....
	///////////////////////////////
	/**
	 * Dessine le contour d'un multi polygone.
	 * @deprecated
	 * @see #dessiner(Graphics2D, Color, GM_MultiSurface, float, int, int)
	 * @param g l'objet graphics2D
	 * @param couleur couleur du trait
	 * @param multiPoly le multi polygone à dessiner
	 * @param d la largeur du trait
	 * @param cap type de fin du trait
	 * @param join type de join entre les lignes du trait
	 */
	@Deprecated
	public void dessinerLimite(Graphics2D g, Color couleur,GM_MultiSurface<GM_Polygon> multiPoly, double d, int cap, int join) {
		this.dessiner(g,couleur,multiPoly,(float)d,cap,join);
	}
	/**
	 * Dessine le contour d'un cercle.
	 * @deprecated
	 * @see #dessinerRond(Graphics2D, Color, GM_Point, double)
	 * @param g l'objet graphics2D
	 * @param couleur couleur du trait
	 * @param point le centre du cercle
	 * @param radius le rayon du cercle
	 */
	@Deprecated
	public void dessinerRond(Graphics2D g, Color couleur,GM_Point point, int radius) {
		this.dessinerRond(g, couleur, point, (double)radius);
	}
	/**
	 * Dessine le contour d'un cercle pour chaque point du multi point
	 * @deprecated
	 * @see #dessinerRond(Graphics2D, Color, GM_MultiPoint, double)
	 * @param g l'objet graphics2D
	 * @param couleur couleur du trait
	 * @param multiPoint le centre du cercle
	 * @param radius le rayon du cercle
	 */
	@Deprecated
	public void dessinerRond(Graphics2D g, Color couleur, GM_MultiPoint multiPoint, int radius) {
		this.dessinerRond(g, couleur, multiPoint, (double)radius);
	}
	/**
	 * Dessine le contour d'un cercle
	 * @deprecated
	 * @see #dessinerCercle(Graphics2D, DirectPosition, int)
	 * @param g l'objet graphics2D
	 * @param couleur couleur du trait
	 * @param point le centre du cercle
	 * @param radius le rayon du cercle
	 */
	@Deprecated
	public void dessinerRond(Graphics2D g, Color couleur, GM_Point point, double radius) {
		g.setColor(couleur);
		this.dessinerCercle(g, point.getPosition(), (int)radius);
	}
	/**
	 * Dessine le contour d'un cercle pour chaque point du multi point
	 * @deprecated
	 * @see #dessinerCercle(Graphics2D, DirectPosition, int)
	 * @param g l'objet graphics2D
	 * @param couleur couleur du trait
	 * @param multiPoint le centre du cercle
	 * @param radius le rayon du cercle
	 */
	@Deprecated
	public void dessinerRond(Graphics2D g, Color couleur, GM_MultiPoint multiPoint, double radius) {
		g.setColor(couleur);
		for (GM_Point point:multiPoint) this.dessinerCercle(g, point.getPosition(), (int)radius);				
	}
	/**
	 * Dessine le contour d'un cercle
	 * @deprecated
	 * @see #dessinerCercle(Graphics2D, int, int, int)
	 * @param g l'objet graphics2D
	 * @param couleur couleur du trait
	 * @param x position en X du centre du cercle
	 * @param y position en Y du centre du cercle
	 * @param radius le rayon du cercle
	 */
	@Deprecated
	public void dessinerRond(Graphics2D g, Color couleur, double x, double y, double radius) {
		this.dessinerCercle(g, this.coordToPixX(x), this.coordToPixY(y), (int)radius);
	}
	/**
	 * Dessine le contour d'un cercle.
	 * @deprecated
	 * @see #dessinerCercle(Graphics2D, DirectPosition, int)
	 * @param g l'objet graphics2D
	 * @param couleur couleur du trait
	 * @param position le centre du cercle
	 * @param radius le rayon du cercle
	 */
	@Deprecated
	public void dessiner(Graphics2D g, Color couleur, DirectPosition position, int radius) {
		g.setColor(couleur);
		this.dessinerCercle(g, position, radius);
	}
	/**
	 * Dessine un texte
	 * @see #dessinerText(Graphics2D, Color, Color, float, Font, String, DirectPosition)
	 * @see #dessinerText(Graphics2D, Color, Color, float, Font, String, GM_LineString)
	 * @deprecated
	 * @param g l'objet graphics2D
	 * @param couleur couleur du trait
	 * @param font police
	 * @param geom géométrie support du texte
	 * @param texte texte à dessiner
	 */
	@Deprecated
	public void dessinerTexte(Graphics2D g, Color couleur, Font font, GM_Object geom, String texte) {
		if (geom instanceof GM_Point) this.dessinerText(g, couleur, null, 0, font, texte, ((GM_Point)geom).getPosition());
		else if (geom instanceof GM_LineString) this.dessinerText(g, couleur, null, 0, font, texte, (GM_LineString)geom);
		else logger.error("Ne fonctionne pas");
	}
	/**
	 * Dessine un texte
	 * @see #dessinerText(Graphics2D, Color, Color, float, Font, String, DirectPosition)
	 * @see #dessinerText(Graphics2D, Color, Color, float, Font, String, GM_LineString)
	 * @deprecated
	 * @param g l'objet graphics2D
	 * @param couleur couleur du trait
	 * @param geom géométrie support du texte
	 * @param texte texte à dessiner
	 */
	@Deprecated
	public void dessinerTexte(Graphics2D g, Color couleur, GM_Object geom, String texte) {
		this.dessinerTexte(g, couleur, new Font("Default",java.awt.Font.PLAIN,10), geom, texte);
	}
	/**
	 * Dessine un texte
	 * @see #dessinerText(Graphics2D, Color, Color, float, Font, String, DirectPosition)
	 * @deprecated
	 * @param g l'objet graphics2D
	 * @param couleur couleur du trait
	 * @param x position en x
	 * @param y position en y
	 * @param texte texte à dessiner
	 */
	@Deprecated
	public void dessinerTexte(Graphics2D g, Color couleur, double x, double y, String texte) {
		this.dessinerText(g, couleur, null, 0.0f, new Font("Default",java.awt.Font.PLAIN,10), texte, new DirectPosition(x,y));
	}
	/**
	 * Dessine un segment
	 * @deprecated
	 * @param coord1 premier point
	 * @param coord2 second point
	 * @param taille taille du trait
	 */
	@Deprecated
	public void dessinerSegment(Graphics2D g, DirectPosition coord1, DirectPosition coord2, int taille) {
		g.setStroke(new BasicStroke(taille));
		g.drawLine(this.coordToPixX(coord1.getX()), this.coordToPixY(coord1.getY()), this.coordToPixX(coord2.getX()), this.coordToPixY(coord2.getY()));
	}
	/**
	 * Dessine un segment
	 * @deprecated
	 * @param couleur couleur du trait
	 * @param x1 x du premier point
	 * @param y1 y du premier point
	 * @param x2 x du second point
	 * @param y2 y du second point
	 * @param taille taille du trait
	 */
	@Deprecated
	public void dessinerSegment(Graphics2D g, Color couleur, double x1, double y1, double x2, double y2, int taille) {
		g.setStroke(new BasicStroke(taille));
		g.drawLine(this.coordToPixX(x1), this.coordToPixY(y1), this.coordToPixX(x2), this.coordToPixY(y2));
	}
	/**
	 * Dessine un rectangle.
	 * @deprecated
	 * @param couleur couleur du trait
	 * @param x x du centre du rectangle
	 * @param y y du centre du rectangle
	 * @param largeur largeur du rectangle
	 */
	@Deprecated
	public void dessinerRect(Graphics2D g, Color couleur, double x, double y, int largeur) {
		g.setColor(couleur);
		g.fillRect(coordToPixX(x-largeur/2), coordToPixY(y+largeur/2), (int)Math.round(largeur/taillePixel+0.5), (int)Math.round(largeur/taillePixel+0.5));
	}
}
