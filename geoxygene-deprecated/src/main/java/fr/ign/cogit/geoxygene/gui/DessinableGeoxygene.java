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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
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
  private final static Logger logger = Logger
      .getLogger(DessinableGeoxygene.class.getName());

  protected List<ChangeListener> listenerList = new ArrayList<ChangeListener>();

  /**
   * Adds a <code>ChangeListener</code>.
   * @param l the <code>ChangeListener</code> to be added
   */
  public void addChangeListener(ChangeListener l) {
    if (this.listenerList == null) {
      if (DessinableGeoxygene.logger.isTraceEnabled()) {
        DessinableGeoxygene.logger.trace("bizarre");
      }
      this.listenerList = new ArrayList<ChangeListener>();
    }
    this.listenerList.add(l);
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type. The event instance is lazily created.
   */
  public void fireActionPerformed(ChangeEvent e) {
    // Guaranteed to return a non-null array
    Object[] listeners = this.listenerList.toArray();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 1; i >= 0; i -= 1) {
      ((ChangeListener) listeners[i]).stateChanged(e);
    }
  }

  private int nbPixelsMarge = 20;
  private int width;
  private int height;
  private Graphics2D graphics = null;

  private double taillePixel = 10.0;

  @Override
  public double getTaillePixel() {
    return this.taillePixel;
  }

  @Override
  public void setTaillePixel(double tp) {
    this.taillePixel = tp;
  }

  private StyledLayerDescriptor sld;

  /**
   * Renvoie la valeur de l'attribut sld.
   * @return la valeur de l'attribut sld
   */
  public StyledLayerDescriptor getSld() {
    return this.sld;
  }

  /**
   * Affecte la valeur de l'attribut sld.
   * @param sld l'attribut sld à affecter
   */
  public void setSld(StyledLayerDescriptor sld) {
    this.sld = sld;
  }

  /**
   * Renvoie la valeur de l'attribut graphics.
   * @return la valeur de l'attribut graphics
   */
  public Graphics2D getGraphics() {
    return this.graphics;
  }

  private BufferedImage image = null;

  /**
   * Renvoie la valeur de l'attribut image.
   * @return la valeur de l'attribut image
   */
  public BufferedImage getImage() {
    return this.image;
  }

  /**
   * @param sld
   */
  public DessinableGeoxygene(StyledLayerDescriptor sld) {
    this.sld = sld;
  }

  private IEnvelope enveloppeAffichage = null;

  @Override
  public IEnvelope getEnveloppeAffichage() {
    return this.enveloppeAffichage;
  }

  private IDirectPosition centreGeo = new DirectPosition(0.0, 0.0);

  @Override
  public IDirectPosition getCentreGeo() {
    return this.centreGeo;
  }

  @Override
  public void setCentreGeo(IDirectPosition centreGeo) {
    this.centreGeo = centreGeo;
  }

  @Override
  public synchronized void majLimitesAffichage(int width, int height) {
    this.image = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_ARGB_PRE);
    this.graphics = this.image.createGraphics();
    if (DessinableGeoxygene.logger.isTraceEnabled()) {
      DessinableGeoxygene.logger.trace("majLimitesAffichage(" + width + ","
          + height + ")");
    }
    if (this.getGraphics() == null) {
      return;
    }
    this.width = width;
    this.height = height;
    if (DessinableGeoxygene.logger.isTraceEnabled()) {
      DessinableGeoxygene.logger.trace("Limites : " + width + " - " + height);
    }
    double XMin = this.pixToCoordX(-this.nbPixelsMarge);
    double XMax = this.pixToCoordX(width + this.nbPixelsMarge);
    double YMin = this.pixToCoordY(height + this.nbPixelsMarge);
    double YMax = this.pixToCoordY(-this.nbPixelsMarge);
    this.enveloppeAffichage = new GM_Envelope(XMin, XMax, YMin, YMax);

    this.affineTransform = AffineTransform.getTranslateInstance(0, height);
    this.affineTransform.scale(1 / this.taillePixel, -1 / this.taillePixel);
    this.affineTransform.translate(width * 0.5 * this.taillePixel
        - this.centreGeo.getX(), height * 0.5 * this.taillePixel
        - this.centreGeo.getY());

    if (this.useCache) {
      this.majCachedFeatures();
      // clearShapeCache();
    }
  }

  /**
   * Mise à jour du cache contenant les features à l'intérieur des limites de
   * l'affichage, i.e. les features visibles.
   */
  private void majCachedFeatures() {
    if (this.sld == null) {
      return;
    }
    if (DessinableGeoxygene.logger.isTraceEnabled()) {
      DessinableGeoxygene.logger
          .trace("Début du calcul des features à mettre dans le cache");
    }
    double debut = System.currentTimeMillis();
    for (Layer layer : this.sld.getLayers()) {
      this.setCachedFeatures(layer);
    }
    double fin = System.currentTimeMillis();
    if (DessinableGeoxygene.logger.isTraceEnabled()) {
      DessinableGeoxygene.logger.trace("(" + (fin - debut)
          + "Fin du calcul des features à mettre dans le cache");
    }
  }

  public int coordToPixX(double x) {
    return (int) ((x - (this.centreGeo.getX() - this.width * 0.5
        * this.taillePixel)) / this.taillePixel);
  }

  public int coordToPixY(double y) {
    return (int) (this.height + (this.centreGeo.getY() - this.height * 0.5
        * this.taillePixel - y)
        / this.taillePixel);
  }

  public double pixToCoordX(int x) {
    return this.centreGeo.getX() - this.width * 0.5 * this.taillePixel + x
        * this.taillePixel;
  }

  public double pixToCoordY(int y) {
    return this.centreGeo.getY() - this.height * 0.5 * this.taillePixel
        + (this.height - y) * this.taillePixel;
  }

  AffineTransform affineTransform = null;

  public Shape toScreen(Shape s) {
    return this.affineTransform.createTransformedShape(s);
  }

  @Override
  public void dessiner(Graphics2D g) throws InterruptedException {
    if (this.sld == null) {
      DessinableGeoxygene.logger.info("SLD null");
      return;
    }
    if (DessinableGeoxygene.logger.isTraceEnabled()) {
      DessinableGeoxygene.logger.trace("dessiner() ");
    }
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        this.antiAliasing ? RenderingHints.VALUE_ANTIALIAS_ON
            : RenderingHints.VALUE_ANTIALIAS_OFF);
    for (Layer layer : this.sld.getLayers()) {
      if (DessinableGeoxygene.logger.isTraceEnabled()) {
        DessinableGeoxygene.logger.trace("dessiner le layer " + layer);
      }
      this.dessiner(g, layer, (this.useCache) ? this.getCachedFeatures(layer)
          : layer.getFeatureCollection());
      this.fireChange();
    }
  }

  boolean useCache = true;
  Map<Layer, Collection<? extends IFeature>> cachedFeatures = new HashMap<Layer, Collection<? extends IFeature>>();

  /**
   * @param layer
   * @param selectedFeatures
   */
  private synchronized void setCachedFeatures(Layer layer) {
    if (layer.getFeatureCollection() == null) {
      return;
    }
    double debut = System.currentTimeMillis();
    this.cachedFeatures.put(layer,
        layer.getFeatureCollection().select(this.enveloppeAffichage));
    double fin = System.currentTimeMillis();
    if (DessinableGeoxygene.logger.isTraceEnabled()) {
      DessinableGeoxygene.logger
          .trace("("
              + (fin - debut)
              + ") Fin du calcul des features à mettre dans le cache pour la couche "
              + layer.getName());
    }
  }

  /**
   * @param layer
   * @return
   */
  private Collection<? extends IFeature> getCachedFeatures(Layer layer) {
    return this.cachedFeatures.get(layer);
  }

  public void dessiner(Graphics2D g, Layer layer,
      Collection<? extends IFeature> features) throws InterruptedException {
    if (DessinableGeoxygene.logger.isTraceEnabled()) {
      DessinableGeoxygene.logger.trace("dessiner()");
    }
    if (features == null) {
      return;
    }
    double debut = System.currentTimeMillis();
    for (Style style : layer.getStyles()) {
      this.dessiner(g, style, features);
      // fireChange();
    }
    double fin = System.currentTimeMillis();
    if (DessinableGeoxygene.logger.isTraceEnabled()) {
      DessinableGeoxygene.logger.trace("dessiner() terminé pour la couche "
          + layer.getName() + " en " + (fin - debut) + ")");
    }
  }

  /**
   * @param style
   * @param features
   * @throws InterruptedException
   */
  public void dessiner(Graphics2D g, Style style,
      Collection<? extends IFeature> features) throws InterruptedException {
    if (style.isUserStyle()) {
      UserStyle userStyle = (UserStyle) style;
      for (FeatureTypeStyle featureTypeStyle : userStyle.getFeatureTypeStyles()) {
        // if (logger.isDebugEnabled())
        // logger.debug("Dessiner le featureTypeStyle "+featureTypeStyle);
        /**
         * TODO les règles devraient etre dans l'ordre de priorité et donc
         * affichées dans l'ordre inverse (OGC 02-070 p.26)
         */
        // if (logger.isDebugEnabled())
        // logger.debug(featureTypeStyle.getRules().size()+" Rules");
        for (int indexRule = featureTypeStyle.getRules().size() - 1; indexRule >= 0; indexRule--) {
          Rule rule = featureTypeStyle.getRules().get(indexRule);
          if (rule.getFilter() == null) {
            for (Symbolizer symbolizer : rule.getSymbolizers()) {
              this.dessiner(g, symbolizer, features);
            }
          } else {
            FT_FeatureCollection<IFeature> filteredFeatures = new FT_FeatureCollection<IFeature>();
            for (IFeature feature : features) {
              if (rule.getFilter().evaluate(feature)) {
                filteredFeatures.add(feature);
              }
            }
            if (DessinableGeoxygene.logger.isTraceEnabled()) {
              DessinableGeoxygene.logger.trace(filteredFeatures.size()
                  + " features filtered");
            }
            for (Symbolizer symbolizer : rule.getSymbolizers()) {
              this.dessiner(g, symbolizer, filteredFeatures);
            }
          }
        }
        // fireChange();
      }
    }
  }

  /**
   * Dessine une liste de Features dans un Graphics2D à l'aide d'un Symbolizer.
   * Tous les parcours de FT_FeatureCollection de cette classe sont effectués
   * dans cette méthde.
   * @param symbolizer
   * @param features
   */
  @SuppressWarnings("unchecked")
  public void dessiner(Graphics2D g, Symbolizer symbolizer,
      Collection<? extends IFeature> features) throws InterruptedException {
    if (symbolizer.isRasterSymbolizer()) {
      RasterSymbolizer rasterSymbolizer = (RasterSymbolizer) symbolizer;
      this.dessiner(rasterSymbolizer);
      return;
    }
    if (symbolizer.isTextSymbolizer()) {
      TextSymbolizer textSymbolizer = (TextSymbolizer) symbolizer;
      if (textSymbolizer.getLabel() == null) {
        return;
      }
      Color fillColor = Color.black;
      if (textSymbolizer.getFill() != null) {
        fillColor = textSymbolizer.getFill().getColor();
      }
      Font font = null;
      if (textSymbolizer.getFont() != null) {
        font = textSymbolizer.getFont().toAwfFont();
      }
      if (font == null) {
        font = new java.awt.Font("Default", java.awt.Font.PLAIN, 10);
      }
      Color haloColor = null;
      float haloRadius = 1.0f;
      if (textSymbolizer.getHalo() != null) {
        if (textSymbolizer.getHalo().getFill() != null) {
          haloColor = textSymbolizer.getHalo().getFill().getColor();
        } else {
          haloColor = Color.white;
        }
        haloRadius = textSymbolizer.getHalo().getRadius();
      }
      for (IFeature feature : features) {
        String texte = (String) feature.getAttribute(textSymbolizer.getLabel());
        if (feature.getGeom() instanceof IPoint) {
          this.dessinerText(g, fillColor, haloColor, haloRadius, font, texte,
              ((IPoint) feature.getGeom()).getPosition());
        } else if (feature.getGeom() instanceof IPolygon) {
          this.dessinerText(g, fillColor, haloColor, haloRadius, font, texte,
              ((IPolygon) feature.getGeom()).centroid());
        } else if (feature.getGeom() instanceof IMultiSurface) {
          this.dessinerText(g, fillColor, haloColor, haloRadius, font, texte,
              ((IMultiSurface<IPolygon>) feature.getGeom()).centroid());
        } else if (feature.getGeom() instanceof ILineString) {
          this.dessinerText(g, fillColor, haloColor, haloRadius, font, texte,
              (ILineString) feature.getGeom());
        } else if (feature.getGeom() instanceof IMultiCurve) {
          this.dessinerText(g, fillColor, haloColor, haloRadius, font, texte,
              ((IMultiCurve<ILineString>) feature.getGeom()).get(0));
        } else {
          DessinableGeoxygene.logger.info(feature.getGeom().getClass()
              .getSimpleName());
        }
      }
      return;
    }
    if (symbolizer.isPointSymbolizer()) {
      PointSymbolizer pointSymbolizer = (PointSymbolizer) symbolizer;
      for (IFeature feature : features) {
        if (feature.getGeom() instanceof IPoint) {
          this.dessiner(g, pointSymbolizer,
              ((IPoint) feature.getGeom()).getPosition());
        } else {
          this.dessiner(g, pointSymbolizer, (feature.getGeom()).centroid());
        }
      }
      return;
    }
    if (symbolizer.isPolygonSymbolizer()) {
      PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) symbolizer;
      Color fillColor = null;
      if (polygonSymbolizer.getFill() != null) {
        fillColor = polygonSymbolizer.getFill().getColor();
      }
      for (IFeature feature : features) {
        if (feature.getGeom().isPolygon()) {
          if (fillColor != null) {
            this.remplir(g, fillColor, (IPolygon) feature.getGeom());
          }
          if (symbolizer.getStroke() != null) {
            if (symbolizer.getStroke().getGraphicType() == null) {
              // Solid color
              this.dessiner(g, symbolizer.getStroke(),
                  (IPolygon) feature.getGeom());
            }
          }
        } else if (feature.getGeom().isMultiSurface()) {
          for (IPolygon element : ((IMultiSurface<IPolygon>) feature.getGeom())
              .getList()) {
            if (fillColor != null) {
              this.remplir(g, fillColor, element);
            }
            if (symbolizer.getStroke() != null) {
              if (symbolizer.getStroke().getGraphicType() == null) {
                // Solid color
                this.dessiner(g, symbolizer.getStroke(), element);
              }
            }
          }
        }
        this.fireObjectChange();
      }
      return;
    }
    if (symbolizer.isLineSymbolizer()) {
      if (symbolizer.getStroke() != null) {
        if (symbolizer.getStroke().getGraphicType() == null) {
          // Solid color
          for (IFeature feature : features) {
            if (feature.getGeom().isLineString()) {
              this.dessiner(g, symbolizer.getStroke(),
                  (ILineString) feature.getGeom());
            } else if (feature.getGeom().isMultiCurve()) {
              for (ILineString element : ((IMultiCurve<ILineString>) feature
                  .getGeom()).getList()) {
                this.dessiner(g, symbolizer.getStroke(), element);
              }
            }
          }
        } else {
          DessinableGeoxygene.logger
              .warn("Les graphics ne sont pas gérés pour l'instant");
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
  private void dessinerText(Graphics2D g, Color fillColor, Color haloColor,
      float haloRadius, Font font, String texte, IDirectPosition position) {
    if (texte == null) {
      return;
    }
    // Find the size of string s in font f in the current Graphics context g.
    FontMetrics fm = g.getFontMetrics(font);
    java.awt.geom.Rectangle2D rect = fm.getStringBounds(texte, g);
    g.setFont(font);
    int textHeight = (int) (rect.getHeight());
    int textWidth = (int) (rect.getWidth());
    // Center text horizontally and vertically
    int centreX = this.coordToPixX(position.getX()) - (textWidth / 2);
    int centreY = this.coordToPixY(position.getY()) - (textHeight / 2)
        + fm.getAscent();

    FontRenderContext frc = g.getFontRenderContext();
    GlyphVector gv = font.createGlyphVector(frc, texte);
    // halo
    if (haloColor != null) {
      Shape shape = gv.getOutline(centreX, centreY);
      g.setColor(haloColor);
      g.setStroke(new BasicStroke(haloRadius, BasicStroke.CAP_ROUND,
          BasicStroke.JOIN_ROUND));
      g.draw(shape);
    }
    g.setColor(fillColor);
    g.drawGlyphVector(gv, centreX, centreY);
    // fireObjectChange();
  }

  /**
   * Dessiner un texte.
   * @param g l'objet Graphics2D
   * @param fillColor couleur de remplissage du texte
   * @param haloColor couleur du halo du texte
   * @param haloRadius rayon du halo du texte
   * @param font police du texte
   * @param texte texte à dessiner
   * @param line ligne support du texte à dessiner TODO à débugger : ça ne
   *          marche pas encore bien
   */
  private void dessinerText(Graphics2D g, Color fillColor, Color haloColor,
      float haloRadius, Font font, String texte, ILineString line) {
    if (texte == null) {
      return;
    }
    FontMetrics fm = g.getFontMetrics(font);
    g.setFont(font);
    int lineLength = line.sizeControlPoint();
    int[] x = new int[lineLength];
    int[] y = new int[lineLength];
    double[] l = new double[lineLength - 1];
    double[] a = new double[lineLength - 1];
    for (int i = 0; i < lineLength; i++) {
      x[i] = this.coordToPixX(line.getControlPoint(i).getX());
      y[i] = this.coordToPixY(line.getControlPoint(i).getY());
    }
    for (int i = 0; i < lineLength - 1; i++) {
      double dx = x[i + 1] - x[i];
      double dy = y[i + 1] - y[i];
      l[i] = Math.sqrt(dx * dx + dy * dy);
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
      AffineTransform at = AffineTransform.getTranslateInstance(x[lineIndex],
          y[lineIndex]);
      at.rotate(angle);
      at.translate(lineU, p.getY() - fm.getAscent() / 2);
      Shape glyph = gv.getGlyphOutline(i, (float) -p.getX(), 0.0f);
      Shape transformedGlyph = at.createTransformedShape(glyph);
      // halo
      if (haloColor != null) {
        g.setColor(haloColor);
        g.setStroke(new BasicStroke(haloRadius, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND));
        g.draw(transformedGlyph);
      }
      g.setColor(fillColor);
      g.fill(transformedGlyph);
      lineU += gv.getGlyphMetrics(i).getAdvance();
      while ((lineIndex < lineLength - 1) && (lineU >= l[lineIndex])) {
        lineU -= l[lineIndex];
        lineIndex++;
      }
      if (lineIndex == lineLength - 1) {
        lineIndex = lineLength - 2;
      }
    }
    // fireObjectChange();
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
  public void dessiner(Graphics2D g, Color couleur, IGeometry geom) {
    if (geom.isPoint()) {
      this.remplirCarre(g, ((IPoint) geom).getPosition());
    } else if (geom.isLineString()) {
      this.dessiner(g, couleur, (ILineString) geom);
    } else if (geom.isMultiCurve()) {
      this.dessiner(g, couleur, (IMultiCurve) geom);
    } else if (geom.isPolygon()) {
      this.remplir(g, couleur, (IPolygon) geom);
    } else if (geom.isMultiSurface()) {
      this.remplir(g, couleur, (IMultiSurface<IPolygon>) geom);
    }
  }

  /**
   * Remplit un cercle
   * @param g l'objet graphics2D
   * @param position le centre du cercle
   * @param radius le rayon du cercle
   */
  public void remplirCercle(Graphics2D g, IDirectPosition position, int radius) {
    g.fillOval((int) position.getX() - radius, (int) position.getY() - radius,
        2 * radius, 2 * radius);
  }

  /**
   * Remplit un carré de 6 de côté.
   * @see #remplirCarre(Graphics2D, IDirectPosition, int)
   * @param g l'objet graphics2D
   * @param position le centre du carré
   */
  public void remplirCarre(Graphics2D g, IDirectPosition position) {
    this.remplirCarre(g, position, 3);
  }

  /**
   * Remplit un carré.
   * @param g l'objet graphics2D
   * @param position le centre du carré
   * @param radius le demi-côté du carré
   */
  public void remplirCarre(Graphics2D g, IDirectPosition position, int radius) {
    g.fillRect((int) position.getX() - radius, (int) position.getY() - radius,
        2 * radius, 2 * radius);
  }

  /**
   * Dessine le contour d'un cercle.
   * @see #dessinerCercle(Graphics2D, int, int, int)
   * @param g l'objet graphics2D
   * @param position le centre du cercle
   * @param radius le rayon du cercle
   */
  public void dessinerCercle(Graphics2D g, IDirectPosition position, int radius) {
    this.dessinerCercle(g, (int) position.getX(), (int) position.getY(), radius);
  }

  /**
   * Dessine le contour d'un cercle
   * @param g l'objet graphics2D
   * @param x la position en X du centre du cercle
   * @param y la position en Y du centre du cercle
   * @param radius le rayon du cercle
   */
  public void dessinerCercle(Graphics2D g, int x, int y, int radius) {
    g.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
  }

  /**
   * Dessine le contour d'un carré de 6 de côté.
   * @param g l'objet graphics2D
   * @param position le centre du carré
   */
  public void dessinerCarre(Graphics2D g, IDirectPosition position) {
    this.dessinerCarre(g, position, 3);
  }

  /**
   * Dessine le contour d'un carré.
   * @param g l'objet graphics2D
   * @param position le centre du carré
   * @param radius le demi-côté du carré
   */
  public void dessinerCarre(Graphics2D g, IDirectPosition position, int radius) {
    g.drawRect((int) position.getX() - radius, (int) position.getY() - radius,
        2 * radius, 2 * radius);
  }

  /**
   * Dessine un point symbolizer.
   * @param g l'objet graphics2D
   * @param pointSymbolizer la symbolisation
   * @param position le centre de la symbolisation
   */
  private void dessiner(Graphics2D g, PointSymbolizer pointSymbolizer,
      IDirectPosition position) {
    if (pointSymbolizer.getGraphic() == null) {
      return;
    }
    for (Mark mark : pointSymbolizer.getGraphic().getMarks()) {
      Shape shape = mark.toShape();
      float size = pointSymbolizer.getGraphic().getSize();
      AffineTransform at = AffineTransform.getTranslateInstance(
          this.coordToPixX(position.getX()), this.coordToPixY(position.getY()));
      at.rotate(pointSymbolizer.getGraphic().getRotation());
      at.scale(size, size);
      shape = at.createTransformedShape(shape);
      g.setColor((mark.getFill() == null) ? Color.gray : mark.getFill()
          .getColor());
      g.fill(shape);
      g.setColor((mark.getStroke() == null) ? Color.black : mark.getStroke()
          .getColor());
      g.draw(shape);
    }
    for (ExternalGraphic graphic : pointSymbolizer.getGraphic()
        .getExternalGraphics()) {
      Image image = graphic.getOnlineResource();
      g.drawImage(image,
          this.coordToPixX(position.getX()) - image.getWidth(null) / 2,
          this.coordToPixY(position.getY()) - image.getHeight(null) / 2, null);
    }
    // fireObjectChange();
  }

  /**
   * Remplit un polygone.
   * @param g l'objet graphics2D
   * @param color couleur du remplissage
   * @param poly géométrie du polygone
   */
  private void remplir(Graphics2D g, Color color, IPolygon poly) {
    g.setColor(color);
    this.remplir(g, poly);
  }

  static int nbPoly = 0;

  /**
   * Remplit un polygone en utilisant la couleur courante.
   * @param g l'objet graphics2D
   * @param poly géométrie du polygone
   */
  private void remplir(Graphics2D g, IPolygon poly) {
    IEnvelope envelope = poly.envelope();
    if ((envelope.width() <= this.taillePixel)
        && (envelope.height() <= this.taillePixel)) {
      return;
    }
    /*
     * Polygon2D p = (Polygon2D) shapeCache.get(poly); if (p==null) try { int
     * nb=poly.coord().size(); float[] geoX=new float[nb], geoY=new float[nb];
     * //enveloppe exterieure GM_Ring ls=poly.getExterior(); for(int
     * i=0;i<ls.coord().size();i++) { geoX[i]=(float) ls.coord().get(i).getX();
     * geoY[i]=(float) ls.coord().get(i).getY(); } //trous int
     * index=ls.coord().size(); for(int j=0;j<poly.getInterior().size();j++) {
     * ls=poly.getInterior(j); for(int i=index;i<index+ls.coord().size();i++) {
     * geoX[i]=(float) ls.coord().get(i-index).getX(); geoY[i]=(float)
     * ls.coord().get(i-index).getY(); }//i index+=ls.coord().size(); }//j p =
     * new Polygon2D(geoX,geoY,nb);
     * 
     * shapeCache.put(poly,p); } catch(Exception e) {
     * logger.error("Impossible de remplir le polygone "+poly);
     * e.printStackTrace(); return; } Shape shape = toScreen(p); g.fill(shape);
     */
    // fireObjectChange();
    try {
      int nb = poly.coord().size();
      int[] geoX = new int[nb], geoY = new int[nb];
      // enveloppe exterieure
      IRing ls = poly.getExterior();
      for (int i = 0; i < ls.coord().size(); i++) {
        geoX[i] = this.coordToPixX(ls.coord().get(i).getX());
        geoY[i] = this.coordToPixY(ls.coord().get(i).getY());
      }
      // trous
      int index = ls.coord().size();
      for (int j = 0; j < poly.getInterior().size(); j++) {
        ls = poly.getInterior(j);
        for (int i = index; i < index + ls.coord().size(); i++) {
          geoX[i] = this.coordToPixX(ls.coord().get(i - index).getX());
          geoY[i] = this.coordToPixY(ls.coord().get(i - index).getY());
        }// i
        index += ls.coord().size();
      }// j
      g.fillPolygon(geoX, geoY, nb);
    } catch (Exception e) {
      DessinableGeoxygene.logger.error("Impossible de remplir le polygone "
          + poly);
      e.printStackTrace();
      return;
    }
  }

  /**
   * Remplit un multi-polygone en utilisant la couleur courante.
   * @param g l'objet graphics2D
   * @param multiPoly géométrie du multi-polygone
   */
  private void remplir(Graphics2D g, IMultiSurface<IPolygon> multiPoly) {
    for (IPolygon poly : multiPoly.getList()) {
      this.remplir(g, poly);
    }
  }

  /**
   * Remplit un multi-polygone.
   * @param g l'objet graphics2D
   * @param couleur
   * @param multiPoly géométrie du multi-polygone
   */
  public void remplir(Graphics2D g, Color couleur,
      IMultiSurface<IPolygon> multiPoly) {
    g.setColor(couleur);
    this.remplir(g, multiPoly);
  }

  /**
   * Dessine le contour d'un polygone.
   * @param g l'objet graphics2D
   * @param stroke le trait utilise pour le dessin
   * @param poly géométrie du polygone
   */
  public void dessiner(Graphics2D g, Stroke stroke, IPolygon poly) {
    Color color = stroke.getColor();
    java.awt.Stroke bs = stroke.toAwtStroke();
    g.setColor(color);
    g.setStroke(bs);
    this.dessiner(g, poly);
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
  public void dessiner(Graphics2D g, Color couleur,
      IMultiSurface<IPolygon> multiPoly, float d, int cap, int join) {
    g.setStroke(new BasicStroke(d, cap, join));
    g.setColor(couleur);
    for (IPolygon poly : multiPoly.getList()) {
      this.dessiner(g, poly);
    }
  }

  /**
   * Dessine le contour d'un multi polygone.
   * @param g l'objet graphics2D
   * @param couleur couleur du trait
   * @param multiPoly le multi polygone à dessiner
   */
  public void dessiner(Graphics2D g, Color couleur,
      IMultiSurface<IPolygon> multiPoly) {
    g.setColor(couleur);
    this.dessiner(g, multiPoly);
  }

  /**
   * Dessine le contour d'un multi polygone.
   * @param g l'objet graphics2D
   * @param multiPoly le multi polygone à dessiner
   */
  public void dessiner(Graphics2D g, IMultiSurface<IPolygon> multiPoly) {
    for (IPolygon poly : multiPoly.getList()) {
      this.dessiner(g, poly);
    }
  }

  /**
   * Dessine le contour d'un polygone.
   * @see #dessiner(Graphics2D, Color, IPolygon)
   * @param g l'objet graphics2D
   * @param couleur la couleur du trait
   * @param poly le polygone à dessiner
   * @param d la largeur du trait
   * @param cap type de fin du trait
   * @param join type de join entre les lignes du trait
   */
  public void dessiner(Graphics2D g, Color couleur, IPolygon poly, float d,
      int cap, int join) {
    g.setStroke(new BasicStroke(d, cap, join));
    this.dessiner(g, couleur, poly);
  }

  /**
   * Dessine le contour d'un polygone avec le trait courant.
   * @see #dessiner(Graphics2D, IPolygon)
   * @param g l'objet graphics2D
   * @param couleur la couleur du trait
   * @param poly le polygone à dessiner
   */
  public void dessiner(Graphics2D g, Color couleur, IPolygon poly) {
    g.setColor(couleur);
    this.dessiner(g, poly);
  }

  /**
   * Dessine le contour d'un polygone en utilisant la couleur et le trait
   * courants.
   * @see #dessiner(Graphics2D, ILineString)
   * @param g l'objet graphics2D
   * @param poly le polygone à dessiner
   */
  public void dessiner(Graphics2D g, IPolygon poly) {
    IEnvelope envelope = poly.envelope();
    if ((envelope.width() <= this.taillePixel)
        && (envelope.height() <= this.taillePixel)) {
      IDirectPosition p = envelope.center();
      int x = this.coordToPixX(p.getX());
      int y = this.coordToPixY(p.getY());
      g.drawLine(x, y, x, y);
      return;
    }
    this.dessiner(g, poly.exteriorLineString());
    for (int i = 0; i < poly.getInterior().size(); i++) {
      this.dessiner(g, poly.interiorLineString(i));
      // fireObjectChange();
    }
  }

  /**
   * Dessine le contour d'une multi ligne.
   * @see #dessiner(Graphics2D, Color, ILineString)
   * @param g l'objet graphics2D
   * @param couleur la couleur du trait
   * @param multiLine la multi ligne à dessiner
   * @param d la largeur du trait
   * @param cap type de fin du trait
   * @param join type de join entre les lignes du trait
   */
  public void dessiner(Graphics2D g, Color couleur,
      IMultiCurve<ILineString> multiLine, float d, int cap, int join) {
    g.setStroke(new BasicStroke(d, cap, join));
    this.dessiner(g, couleur, multiLine);
  }

  /**
   * Dessine le contour d'une multi ligne.
   * @see #dessiner(Graphics2D, IMultiCurve)
   * @param g l'objet graphics2D
   * @param couleur la couleur du trait
   * @param multiLine la multi ligne à dessiner
   */
  public void dessiner(Graphics2D g, Color couleur,
      IMultiCurve<ILineString> multiLine) {
    g.setColor(couleur);
    this.dessiner(g, multiLine);
  }

  /**
   * Dessine le contour d'une multi ligne.
   * @see #dessiner(Graphics2D, ILineString)
   * @param g l'objet graphics2D
   * @param multiLine la multi ligne à dessiner
   */
  private void dessiner(Graphics2D g, IMultiCurve<ILineString> multiLine) {
    for (ILineString line : multiLine.getList()) {
      this.dessiner(g, line);
    }
  }

  /**
   * Dessiner le contour d'une ligne.
   * @see #dessiner(Graphics2D, Color, ILineString)
   * @param g l'objet graphics2D
   * @param stroke le trait à utiliser
   * @param line la ligne à dessiner
   */
  private void dessiner(Graphics2D g, Stroke stroke, ILineString line) {
    g.setStroke(stroke.toAwtStroke());
    this.dessiner(g, stroke.getColor(), line);
  }

  /**
   * Dessine le contour d'une ligne.
   * @see #dessiner(Graphics2D, Color, ILineString)
   * @param g l'objet graphics2D
   * @param couleur couleur du trait
   * @param line la ligne à dessiner
   * @param d la largeur du trait
   * @param cap type de fin du trait
   * @param join type de join entre les lignes du trait
   */
  public void dessiner(Graphics2D g, Color couleur, ILineString line, float d,
      int cap, int join) {
    g.setStroke(new BasicStroke(d, cap, join));
    this.dessiner(g, couleur, line);
  }

  /**
   * Dessine le contour d'une ligne.
   * @see #dessiner(Graphics2D, ILineString)
   * @param g l'objet graphics2D
   * @param couleur couleur du trait
   * @param line la ligne à dessiner
   */
  public void dessiner(Graphics2D g, Color couleur, ILineString line) {
    g.setColor(couleur);
    this.dessiner(g, line);
  }

  /**
   * Dessine le contour d'une ligne en utilisant la couleur et le trait
   * courants.
   * @param g l'objet graphics2D
   * @param line la ligne à dessiner
   */
  private void dessiner(Graphics2D g, ILineString line) {
    GeneralPath p = (GeneralPath) this.shapeCache.get(line);
    if (p == null) {
      IDirectPositionList coords = line.coord();
      p = new GeneralPath();

      int nb = coords.size();

      /*
       * int x=coordToPixX(coords.get(0).getX()); int
       * y=coordToPixY(coords.get(0).getY()); p.moveTo(x,y); for(int j=1;
       * j<coords.size(); j++){ x=coordToPixX(coords.get(j).getX());
       * y=coordToPixY(coords.get(j).getY()); p.lineTo(x,y); }
       */
      if (nb == 0) {
        DessinableGeoxygene.logger.error("Impossible de dessiner la ligne "
            + line);
        return;
      }
      float x = (float) coords.get(0).getX();
      float y = (float) coords.get(0).getY();
      p.moveTo(x, y);
      for (int j = 1; j < coords.size(); j++) {
        x = (float) coords.get(j).getX();
        y = (float) coords.get(j).getY();
        p.lineTo(x, y);
      }

      this.shapeCache.put(line, p);
    }
    g.draw(this.toScreen(p));
    // fireObjectChange();
  }

  Map<IGeometry, Shape> shapeCache = new HashMap<IGeometry, Shape>();

  /**
   * Nettoie le cache contenant les formes pour le dessin.
   */
  public void clearShapeCache() {
    this.shapeCache.clear();
  }

  /**
   * Lance le processus de dessin.
   */
  public void start() {
    Thread t = new Thread(this);
    this.threadMaj = new ThreadVar(t);
    t.start();
  }

  @Override
  public void run() {
    if (DessinableGeoxygene.logger.isTraceEnabled()) {
      DessinableGeoxygene.logger.trace("run()");
    }
    final Runnable doFinished = new Runnable() {
      @Override
      public void run() {
        DessinableGeoxygene.this.finishedMaj();
      }
    };
    try {
      if (this.graphics != null) {
        this.dessiner(this.graphics);
      }
    } catch (InterruptedException e) {
      if (DessinableGeoxygene.logger.isTraceEnabled()) {
        DessinableGeoxygene.logger
            .trace("InterruptedException pendant majImage()");
      }
    } finally {
      // threadMaj.clear();
    }
    SwingUtilities.invokeLater(doFinished);
  }

  /**
   * Classe pour la gestion du processus de dessin.
   */
  public static class ThreadVar {
    private Thread thread;

    ThreadVar(Thread t) {
      this.thread = t;
    }

    synchronized Thread get() {
      return this.thread;
    }

    synchronized void clear() {
      this.thread = null;
    }
  }

  private ThreadVar threadMaj = null;

  /**
   * Interromp le processus de dessin.
   */
  public void interruptMaj() {
    if (this.threadMaj == null) {
      return;
    }
    Thread t = this.threadMaj.get();
    if (t != null) {
      t.interrupt();
    }
  }

  /**
   * Méthode appelée quand le processus est terminé.
   */
  public void finishedMaj() {
    if (DessinableGeoxygene.logger.isTraceEnabled()) {
      DessinableGeoxygene.logger.trace("finishedMaj");
    }
    this.fireChange();
  }

  /**
   * Informe les listeners que l'image que l'objet dessine a été modifiée.
   */
  private void fireChange() {
    this.objectsChange = 0;
    this.fireActionPerformed(new ChangeEvent(this));
  }

  int objectsChange = 0;

  /**
   * Informe les listeners que l'image que l'objet dessine a été modifiée.
   */
  private void fireObjectChange() {
    this.objectsChange++;
    if (this.objectsChange > 1000) {
      this.fireChange();
    }
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
  public boolean isAntiAliasing() {
    return this.antiAliasing;
  }

  /**
   * Affecte la valeur de l'attribut antiAliasing.
   * @param antiAliasing l'attribut antiAliasing à affecter
   */
  public void setAntiAliasing(boolean antiAliasing) {
    this.antiAliasing = antiAliasing;
  }

  // /////////////////////////////
  // Méthodes pour la compatibilité avec Mirage....
  // /////////////////////////////
  /**
   * Dessine le contour d'un multi polygone.
   * @deprecated
   * @see #dessiner(Graphics2D, Color, IMultiSurface, float, int, int)
   * @param g l'objet graphics2D
   * @param couleur couleur du trait
   * @param multiPoly le multi polygone à dessiner
   * @param d la largeur du trait
   * @param cap type de fin du trait
   * @param join type de join entre les lignes du trait
   */
  @Deprecated
  public void dessinerLimite(Graphics2D g, Color couleur,
      IMultiSurface<IPolygon> multiPoly, double d, int cap, int join) {
    this.dessiner(g, couleur, multiPoly, (float) d, cap, join);
  }

  /**
   * Dessine le contour d'un cercle.
   * @deprecated
   * @see #dessinerRond(Graphics2D, Color, IPoint, double)
   * @param g l'objet graphics2D
   * @param couleur couleur du trait
   * @param point le centre du cercle
   * @param radius le rayon du cercle
   */
  @Deprecated
  public void dessinerRond(Graphics2D g, Color couleur, IPoint point, int radius) {
    this.dessinerRond(g, couleur, point, (double) radius);
  }

  /**
   * Dessine le contour d'un cercle pour chaque point du multi point
   * @deprecated
   * @see #dessinerRond(Graphics2D, Color, IMultiPoint, double)
   * @param g l'objet graphics2D
   * @param couleur couleur du trait
   * @param multiPoint le centre du cercle
   * @param radius le rayon du cercle
   */
  @Deprecated
  public void dessinerRond(Graphics2D g, Color couleur, IMultiPoint multiPoint,
      int radius) {
    this.dessinerRond(g, couleur, multiPoint, (double) radius);
  }

  /**
   * Dessine le contour d'un cercle
   * @deprecated
   * @see #dessinerCercle(Graphics2D, IDirectPosition, int)
   * @param g l'objet graphics2D
   * @param couleur couleur du trait
   * @param point le centre du cercle
   * @param radius le rayon du cercle
   */
  @Deprecated
  public void dessinerRond(Graphics2D g, Color couleur, IPoint point,
      double radius) {
    g.setColor(couleur);
    this.dessinerCercle(g, point.getPosition(), (int) radius);
  }

  /**
   * Dessine le contour d'un cercle pour chaque point du multi point
   * @deprecated
   * @see #dessinerCercle(Graphics2D, IDirectPosition, int)
   * @param g l'objet graphics2D
   * @param couleur couleur du trait
   * @param multiPoint le centre du cercle
   * @param radius le rayon du cercle
   */
  @Deprecated
  public void dessinerRond(Graphics2D g, Color couleur, IMultiPoint multiPoint,
      double radius) {
    g.setColor(couleur);
    for (IPoint point : multiPoint) {
      this.dessinerCercle(g, point.getPosition(), (int) radius);
    }
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
  public void dessinerRond(Graphics2D g, Color couleur, double x, double y,
      double radius) {
    this.dessinerCercle(g, this.coordToPixX(x), this.coordToPixY(y),
        (int) radius);
  }

  /**
   * Dessine le contour d'un cercle.
   * @deprecated
   * @see #dessinerCercle(Graphics2D, IDirectPosition, int)
   * @param g l'objet graphics2D
   * @param couleur couleur du trait
   * @param position le centre du cercle
   * @param radius le rayon du cercle
   */
  @Deprecated
  public void dessiner(Graphics2D g, Color couleur, IDirectPosition position,
      int radius) {
    g.setColor(couleur);
    this.dessinerCercle(g, position, radius);
  }

  /**
   * Dessine un texte
   * @see #dessinerText(Graphics2D, Color, Color, float, Font, String,
   *      IDirectPosition)
   * @see #dessinerText(Graphics2D, Color, Color, float, Font, String,
   *      ILineString)
   * @deprecated
   * @param g l'objet graphics2D
   * @param couleur couleur du trait
   * @param font police
   * @param geom géométrie support du texte
   * @param texte texte à dessiner
   */
  @Deprecated
  public void dessinerTexte(Graphics2D g, Color couleur, Font font,
      IGeometry geom, String texte) {
    if (geom instanceof IPoint) {
      this.dessinerText(g, couleur, null, 0, font, texte,
          ((IPoint) geom).getPosition());
    } else if (geom instanceof ILineString) {
      this.dessinerText(g, couleur, null, 0, font, texte, (ILineString) geom);
    } else {
      DessinableGeoxygene.logger.error("Ne fonctionne pas");
    }
  }

  /**
   * Dessine un texte
   * @see #dessinerText(Graphics2D, Color, Color, float, Font, String,
   *      IDirectPosition)
   * @see #dessinerText(Graphics2D, Color, Color, float, Font, String,
   *      ILineString)
   * @deprecated
   * @param g l'objet graphics2D
   * @param couleur couleur du trait
   * @param geom géométrie support du texte
   * @param texte texte à dessiner
   */
  @Deprecated
  public void dessinerTexte(Graphics2D g, Color couleur, IGeometry geom,
      String texte) {
    this.dessinerTexte(g, couleur,
        new Font("Default", java.awt.Font.PLAIN, 10), geom, texte);
  }

  /**
   * Dessine un texte
   * @see #dessinerText(Graphics2D, Color, Color, float, Font, String,
   *      IDirectPosition)
   * @deprecated
   * @param g l'objet graphics2D
   * @param couleur couleur du trait
   * @param x position en x
   * @param y position en y
   * @param texte texte à dessiner
   */
  @Deprecated
  public void dessinerTexte(Graphics2D g, Color couleur, double x, double y,
      String texte) {
    this.dessinerText(g, couleur, null, 0.0f, new Font("Default",
        java.awt.Font.PLAIN, 10), texte, new DirectPosition(x, y));
  }

  /**
   * Dessine un segment
   * @deprecated
   * @param coord1 premier point
   * @param coord2 second point
   * @param taille taille du trait
   */
  @Deprecated
  public void dessinerSegment(Graphics2D g, IDirectPosition coord1,
      IDirectPosition coord2, int taille) {
    g.setStroke(new BasicStroke(taille));
    g.drawLine(this.coordToPixX(coord1.getX()),
        this.coordToPixY(coord1.getY()), this.coordToPixX(coord2.getX()),
        this.coordToPixY(coord2.getY()));
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
  public void dessinerSegment(Graphics2D g, Color couleur, double x1,
      double y1, double x2, double y2, int taille) {
    g.setStroke(new BasicStroke(taille));
    g.drawLine(this.coordToPixX(x1), this.coordToPixY(y1),
        this.coordToPixX(x2), this.coordToPixY(y2));
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
  public void dessinerRect(Graphics2D g, Color couleur, double x, double y,
      int largeur) {
    g.setColor(couleur);
    g.fillRect(this.coordToPixX(x - largeur / 2),
        this.coordToPixY(y + largeur / 2),
        (int) Math.round(largeur / this.taillePixel + 0.5),
        (int) Math.round(largeur / this.taillePixel + 0.5));
  }
}
