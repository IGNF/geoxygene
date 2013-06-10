/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * @author julien Gaffuri 28 janv. 2009
 */
package fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.lang.reflect.Method;
import java.net.URL;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.SpecialPoint;
import fr.ign.cogit.cartagen.software.dataset.SpecialPointType;
import fr.ign.cogit.cartagen.software.interfacecartagen.AbstractLayerGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.HorizontalHatchTexture;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.VerticalHatchTexture;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.geompool.ColouredFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * une symbolisation de couche. une instance de cette classe fournit une methode
 * de dessin d'un objet sur une fenetre
 * @author julien Gaffuri 28 janv. 2009
 */
public abstract class Symbolisation {
  final static Logger logger = Logger.getLogger(Symbolisation.class.getName());

  /**
   * methode de dessin d'un objet sur une fenetre donnee
   * @param pv
   * @param obj
   * @throws InterruptedException
   */
  public abstract void draw(VisuPanel pv, IFeature obj);

  /**
	 */
  private static Symbolisation defaut = null;
  protected static Color couleurDefaut = new Color(150, 150, 255, 170);

  /**
   * defaut - dessine les objets avec couleur par defaut
   * @return
   */
  public static Symbolisation defaut() {
    if (Symbolisation.defaut == null) {
      Symbolisation.defaut = new Symbolisation() {
        @Override
        public void draw(VisuPanel pv, IFeature obj) {
          if (obj.isDeleted()) {
            return;
          }

          // dessin de l'objet avec la couleur choisie
          pv.draw(Symbolisation.couleurDefaut, obj.getGeom());
        }
      };
    }
    return Symbolisation.defaut;
  }

  /**
   * colore: objet representes d'une certaine couleur
   * @param couleur
   * @return
   */
  public static Symbolisation couleur(final Color couleur) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }
        // dessin de l'objet
        pv.draw(couleur, obj.getGeom());
      }
    };
  }

  /**
   * Default symbolisation for ColouredFeatures: if they have a Symbolisation
   * uses it. Otherwise simply draws the feature with its colour.
   * @param couleur
   * @return
   */
  public static Symbolisation colouredGeom() {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (!(obj instanceof ColouredFeature)) {
          return;
        }
        ColouredFeature colObj = (ColouredFeature) obj;
        // Case where the object has an associated Symbolisation
        Symbolisation colObjSymbo = colObj.getSymbolisation();
        if (colObjSymbo != null) {
          colObjSymbo.draw(pv, colObj);
          return;
        }
        // Other cases: simply draws the feature with its colour
        pv.draw(colObj.getSymbolColour(), colObj.getGeom());
      }
    };
  }

  /**
   * Symbolisation typically to be associated with a ColouredFeature i.e. passed
   * to the method
   * {@link LayerManager#addToGeometriesPool(IGeometry, Symbolisation)} that
   * constructs the ColouredFeature. This symbolisation is valid for linear or
   * polygonal features. It does not take care of any AbstractLayerGroup. It
   * draws linear features with the given values of line width in pixels, colour
   * and transparency. If the feature is surfacic then its border is drawn with
   * the given values line colour and transparency, and it is filled with the
   * given values of fill colour and transparency. For transparencies, (0 =
   * transparent, 255 = opaque)
   * @param widthPixels Width of the line (of the border if surfacic feature),
   *          in pixels
   * @param lineColour Colour of the line (of the border if surfacic feature).
   *          The transparency component of the colour is ignored
   * @param lineTransparency Transparency of the line (of the border if surfacic
   *          feature)
   * @param fillColour Colour the surfacic feature should be filled with. The
   *          transparency component of the colour is ignored. If linear
   *          feature, the parameter is ignored.
   * @param fillTransparency Transparency of the fill colour. If linear feature,
   *          the parameter is ignored.
   * @return the defined symbolisation
   */
  public static Symbolisation lineOrSurfaceWidthColourTransparency(
      final int widthPixels, final Color lineColour,
      final int lineTransparency, final Color fillColour,
      final int fillTransparency) {
    return new Symbolisation() {
      @SuppressWarnings("unchecked")
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        // Only works for polygons, multipolygons, lines and multilines
        if (!(obj.getGeom() instanceof IPolygon)
            && !(obj.getGeom() instanceof IMultiSurface<?>)
            && !(obj.getGeom() instanceof ILineString)
            && !(obj.getGeom() instanceof IMultiCurve<?>)) {
          Symbolisation.logger
              .warn("Failed to draw feature with symbolisation lineOrSurfaceWidthColourTransparency. Geom type should be line or polygon and is "
                  + obj.getGeom().getClass().getSimpleName());
          return;
        }

        // Computes the actual colour to use for line (/border) from the given
        // colour and transparency
        Color lineActualColour = new Color(lineColour.getRed(),
            lineColour.getGreen(), lineColour.getBlue(), lineTransparency);

        // Polygon case
        if ((obj.getGeom() instanceof IPolygon)
            || (obj.getGeom() instanceof IMultiSurface<?>)) {
          // Computes the actual colour to use to fill the polygon from the
          // given fill colour and transparency
          Color fillActualColour = new Color(fillColour.getRed(),
              fillColour.getGreen(), fillColour.getBlue(), fillTransparency);
          // Fills the polygon
          pv.draw(fillActualColour, obj.getGeom());
          // Draws the border of the polygon
          if (obj.getGeom() instanceof IPolygon) {
            pv.drawLimit(lineActualColour, (IPolygon) obj.getGeom(),
                new BasicStroke(widthPixels, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND));
          } else if (obj.getGeom() instanceof IMultiSurface<?>) {
            pv.drawLimit(lineActualColour, (IMultiSurface<?>) obj.getGeom(),
                new BasicStroke(widthPixels, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND));
          }
          return;
        }

        // Line case: draw it
        if (obj.getGeom() instanceof ILineString) {
          pv.draw(lineActualColour, (ILineString) obj.getGeom(), widthPixels,
              BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
          return;
        }
        if (obj.getGeom() instanceof IMultiCurve<?>) {
          pv.draw(lineActualColour, (IMultiCurve<ILineString>) obj.getGeom(),
              widthPixels, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
          return;
        }
      }
    };
  }

  /**
   * Symbolisation typically to be associated with a ColouredFeature i.e. passed
   * to the method
   * {@link LayerManager#addToGeometriesPool(IGeometry, Symbolisation)} that
   * constructs the ColouredFeature. This symbolisation is valid for linear or
   * polygonal features. It does not take care of any AbstractLayerGroup. It
   * draws linear features with the given values of line width in map
   * millimeters, colour and transparency. If the feature is surfacic then its
   * border is drawn with the given values line colour and transparency, and it
   * is filled with the given values of fill colour and transparency. For
   * transparencies, (0 = transparent, 255 = opaque)
   * @param widthMM Width of the line (of the border if surfacic feature), in
   *          map mm
   * @param lineColour Colour of the line (of the border if surfacic feature).
   *          The transparency component of the colour is ignored
   * @param lineTransparency Transparency of the line (of the border if surfacic
   *          feature)
   * @param fillColour Colour the surfacic feature should be filled with. The
   *          transparency component of the colour is ignored. If linear
   *          feature, the parameter is ignored.
   * @param fillTransparency Transparency of the fill colour. If linear feature,
   *          the parameter is ignored.
   * @return the defined symbolisation
   */
  public static Symbolisation lineOrSurfaceWidthColourTransparency(
      final double widthMM, final Color lineColour, final int lineTransparency,
      final Color fillColour, final int fillTransparency) {
    return new Symbolisation() {
      @SuppressWarnings("unchecked")
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        // Only works for polygons, multipolygons, lines and multilines
        if (!(obj.getGeom() instanceof IPolygon)
            && !(obj.getGeom() instanceof IMultiSurface<?>)
            && !(obj.getGeom() instanceof ILineString)
            && !(obj.getGeom() instanceof IMultiCurve<?>)) {
          Symbolisation.logger
              .warn("Failed to draw feature with symbolisation lineOrSurfaceWidthColourTransparency. Geom type should be line or polygon and is "
                  + obj.getGeom().getClass().getSimpleName());
          return;
        }

        // Computes the actual colour to use for line (/border) from the given
        // colour and transparency
        Color lineActualColour = new Color(lineColour.getRed(),
            lineColour.getGreen(), lineColour.getBlue(), lineTransparency);

        // Polygon case
        if ((obj.getGeom() instanceof IPolygon)
            || (obj.getGeom() instanceof IMultiSurface<?>)) {
          // Computes the actual colour to use to fill the polygon from the
          // given fill colour and transparency
          Color fillActualColour = new Color(fillColour.getRed(),
              fillColour.getGreen(), fillColour.getBlue(), fillTransparency);
          // Fills the polygon
          pv.draw(fillActualColour, obj.getGeom());
          // Draws the border of the polygon
          if (obj.getGeom() instanceof IPolygon) {
            pv.drawLimit(lineActualColour, (IPolygon) obj.getGeom(), widthMM
                * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
          } else if (obj.getGeom() instanceof IMultiSurface<?>) {
            pv.drawLimit(lineActualColour, (IMultiSurface<?>) obj.getGeom(),
                widthMM * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
          }
          return;
        }

        // Line case: draw it
        if (obj.getGeom() instanceof ILineString) {
          pv.draw(lineActualColour, (ILineString) obj.getGeom(), widthMM
              * Legend.getSYMBOLISATI0N_SCALE() / 1000.0, BasicStroke.CAP_BUTT,
              BasicStroke.JOIN_ROUND);
          return;
        }
        if (obj.getGeom() instanceof IMultiCurve<?>) {
          pv.draw(lineActualColour, (IMultiCurve<ILineString>) obj.getGeom(),
              widthMM * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
              BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
          return;
        }
      }
    };
  }

  /**
   * Symbolisation typically to be associated with a ColouredFeature i.e. passed
   * to the method
   * {@link LayerManager#addToGeometriesPool(IGeometry, Symbolisation)} that
   * constructs the ColouredFeature. This symbolisation is valid for linear or
   * polygonal features. It does not take care of any AbstractLayerGroup. It
   * draws the line (resp. the border of the polygon) with the (opaque) colour
   * passed as parameter and the width passed as parameter, and if polygon,
   * fills it with the same colour having the transparency passed as parameter.
   * THE LINE WIDTH IS IN MAP MM
   * @param colour the colour to draw with
   * @param fillTransparency the transparency for the filling of the polygon (0
   *          = transparent, 255 = opaque). Will be ignored if the feature to
   *          draw is linear
   * @param widthMM Width of the line (of the border if surfacic feature), in
   *          map mm
   * @return the defined symbolisation
   */
  public static Symbolisation lineOrSurfaceWidthColourTransparency(
      final Color colour, final int fillTransparency, final double widthMM) {
    return Symbolisation.lineOrSurfaceWidthColourTransparency(widthMM, colour,
        255, colour, fillTransparency);
  }

  /**
   * Symbolisation typically to be associated with a ColouredFeature i.e. passed
   * to the method
   * {@link LayerManager#addToGeometriesPool(IGeometry, Symbolisation)} that
   * constructs the ColouredFeature. This symbolisation is valid for linear or
   * polygonal features. It does not take care of any AbstractLayerGroup. It
   * draws the line (resp. the border of the polygon) with the (opaque) colour
   * passed as parameter and the width passed as parameter, and if polygon,
   * fills it with the same colour having the transparency passed as parameter.
   * THE LINE WIDTH IS IN PIXELS
   * @param colour the colour to draw with
   * @param fillTransparency the transparency for the filling of the polygon (0
   *          = transparent, 255 = opaque). Will be ignored if the feature to
   *          draw is linear
   * @param widthPixels Width of the line (of the border if surfacic feature),
   *          in map mm
   * @return the defined symbolisation
   */
  public static Symbolisation lineOrSurfaceWidthColourTransparency(
      final Color colour, final int fillTransparency, final int widthPixels) {
    return Symbolisation.lineOrSurfaceWidthColourTransparency(widthPixels,
        colour, 255, colour, fillTransparency);
  }

  /**
   * Symbolisation typically to be associated with a ColouredFeature i.e. passed
   * to the method
   * {@link LayerManager#addToGeometriesPool(IGeometry, Symbolisation)} that
   * constructs the ColouredFeature. This symbolisation is valid for linear or
   * polygonal features. It does not take care of any AbstractLayerGroup. It
   * draws the line (resp. the border of the polygon) with the (opaque) colour
   * passed as parameter, and if polygon, fills it with the same colour having
   * the transparency passed as parameter. The width of the line/border is 1
   * pixel
   * @param colour the colour to draw with
   * @param fillTransparency the transparency for the filling of the polygon (0
   *          = transparent, 255 = opaque). Will be ignored if the feature to
   *          draw is linear
   * @return the defined symbolisation
   */
  public static Symbolisation lineOrSurfaceColourTransparency(
      final Color colour, final int fillTransparency) {
    return Symbolisation.lineOrSurfaceWidthColourTransparency(1, colour, 255,
        colour, fillTransparency);
  }

  /**
   * surfacique colore avec contour d'epaisseur 1 pixel
   * @return
   */
  public static Symbolisation surface(final Color couleurSurface,
      final Color couleurContour, final AbstractLayerGroup layerGroup) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification
        if (!(obj.getGeom() instanceof IPolygon)
            && !(obj.getGeom() instanceof IMultiSurface<?>)) {
          Symbolisation.logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type de geometrie: "
              + obj.getGeom().getClass().getSimpleName());
          return;
        }

        if (layerGroup.symbolisationDisplay) {
          // dessin de la surface
          pv.draw(couleurSurface, (IPolygon) obj.getGeom());
          // dessin du contour
          if (obj.getGeom() instanceof IPolygon) {
            pv.drawLimit(couleurContour, (IPolygon) obj.getGeom());
          } else if (obj.getGeom() instanceof IMultiSurface<?>) {
            pv.drawLimit(couleurContour, (IMultiSurface<?>) obj.getGeom());
          }
        } else {
          // dessin du contour
          if (obj.getGeom() instanceof IPolygon) {
            pv.drawLimit(couleurContour, (IPolygon) obj.getGeom());
          } else if (obj.getGeom() instanceof IMultiSurface<?>) {
            pv.drawLimit(couleurContour, (IMultiSurface<?>) obj.getGeom());
          }
        }

      }
    };
  }

  /**
   * surfacique avec contour
   * @return
   */
  public static Symbolisation surface(final Color couleurSurface,
      final Color couleurContour, final double largeurContourmm, final int cap,
      final int join, final AbstractLayerGroup layerGroup) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        if (layerGroup.symbolisationDisplay) {
          // dessin de la surface
          try {
            pv.draw(couleurSurface, obj.getGeom());
          } catch (Exception e) {
            e.printStackTrace();
          }
          // dessin du contour
          if (obj.getGeom() instanceof IPolygon) {
            pv.drawLimit(couleurContour, (IPolygon) obj.getGeom(),
                largeurContourmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                cap, join);
          } else if (obj.getGeom() instanceof IMultiSurface<?>) {
            pv.drawLimit(couleurContour, (IMultiSurface<?>) obj.getGeom(),
                largeurContourmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                cap, join);
          }
        } else {
          // dessin du contour
          if (obj.getGeom() instanceof IPolygon) {
            pv.drawLimit(couleurContour, (IPolygon) obj.getGeom(),
                largeurContourmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                cap, join);
          } else if (obj.getGeom() instanceof IMultiSurface<?>) {
            pv.drawLimit(couleurContour, (IMultiSurface<?>) obj.getGeom(),
                largeurContourmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                cap, join);
          }
        }
      }
    };
  }

  /**
   * symbolize a polygon with a symbol texture fill. The symbol is contained in
   * image file.
   * @param filename the simple filename of the texture image plus the extension
   * @return
   */
  public static Symbolisation areaSymbolFill(final String filename,
      final float size, final Color couleurContour,
      final double largeurContourmm, final int cap, final int join,
      final AbstractLayerGroup layerGroup) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }
        ExternalGraphic tree = new ExternalGraphic();
        URL url = Symbolisation.class
            .getResource("/images/symbols/" + filename); //$NON-NLS-1$
        tree.setHref(url.toString());
        tree.setFormat("png"); //$NON-NLS-1$
        Image image = tree.getOnlineResource();

        if (layerGroup.symbolisationDisplay) {
          // draw the image as texture
          try {
            Shape shape = pv.toShape(obj.getGeom());
            Double width = new Double(Math.max(1, shape.getBounds2D()
                .getWidth()));
            Double height = new Double(Math.max(1, shape.getBounds2D()
                .getHeight()));
            Double shapeHeight = new Double(size);
            double factor = shapeHeight / image.getHeight(null);
            Double shapeWidth = new Double(Math.max(image.getWidth(null)
                * factor, 1));
            AffineTransform transform = AffineTransform.getTranslateInstance(
                shape.getBounds2D().getMinX(), shape.getBounds2D().getMinY());
            Image scaledImage = image.getScaledInstance(shapeWidth.intValue(),
                shapeHeight.intValue(), Image.SCALE_FAST);
            BufferedImage buff = new BufferedImage(shapeWidth.intValue(),
                shapeHeight.intValue(), BufferedImage.TYPE_INT_ARGB);
            buff.getGraphics().drawImage(scaledImage, 0, 0, null);
            ParameterBlock p = new ParameterBlock();
            p.addSource(buff);
            p.add(width.intValue());
            p.add(height.intValue());
            RenderedOp im = JAI.create("pattern", p);//$NON-NLS-1$
            BufferedImage bufferedImage = im.getAsBufferedImage();
            pv.getG2D().drawImage(bufferedImage, transform, null);
            bufferedImage.flush();
            im.dispose();
            scaledImage.flush();
            buff.flush();

          } catch (Exception e) {
            e.printStackTrace();
          }
          // dessin du contour
          if (obj.getGeom() instanceof IPolygon) {
            pv.drawLimit(couleurContour, (IPolygon) obj.getGeom(),
                largeurContourmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                cap, join);
          } else if (obj.getGeom() instanceof IMultiSurface<?>) {
            pv.drawLimit(couleurContour, (IMultiSurface<?>) obj.getGeom(),
                largeurContourmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                cap, join);
          }
        } else {
          // dessin du contour
          if (obj.getGeom() instanceof IPolygon) {
            pv.drawLimit(couleurContour, (IPolygon) obj.getGeom(),
                largeurContourmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                cap, join);
          } else if (obj.getGeom() instanceof IMultiSurface<?>) {
            pv.drawLimit(couleurContour, (IMultiSurface<?>) obj.getGeom(),
                largeurContourmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                cap, join);
          }
        }
      }
    };
  }

  public static Symbolisation surface(final Color couleurSurface,
      final Color couleurContour, final double largeurContourmm,
      final AbstractLayerGroup layerGroup) {
    return Symbolisation.surface(couleurSurface, couleurContour,
        largeurContourmm, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND,
        layerGroup);
  }

  public static Symbolisation surface(final Color couleurSurface,
      final Color couleurContour) {
    return Symbolisation.surface(couleurSurface, couleurContour,
        CartagenApplication.getInstance().getLayerGroup());
  }

  public static Symbolisation surface(final Color couleurSurface,
      final Color couleurContour, final double largeurContourmm, final int cap,
      final int join) {
    return Symbolisation.surface(couleurSurface, couleurContour,
        largeurContourmm, cap, join, CartagenApplication.getInstance()
            .getLayerGroup());
  }

  public static Symbolisation surface(final Color couleurSurface,
      final Color couleurContour, final double largeurContourmm) {
    return Symbolisation.surface(couleurSurface, couleurContour,
        largeurContourmm, CartagenApplication.getInstance().getLayerGroup());
  }

  public static Symbolisation hatchedVerticalPolygon(
      final Color couleurContour, final double largeurContourmm,
      final Color hatchColor, final int hatchOffset, final int hatchBias,
      final int hatchThickness) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        IPolygon poly = (IPolygon) obj.getGeom();
        int nb = poly.coord().size() + poly.getInterior().size();
        int[] x = new int[nb], y = new int[nb];
        // enveloppe exterieure
        IRing ls = poly.getExterior();
        int x0 = pv.coordToPixX(ls.coord().get(0).getX());
        int y0 = pv.coordToPixY(ls.coord().get(0).getY());
        for (int i = 0; i < ls.coord().size(); i++) {
          x[i] = pv.coordToPixX(ls.coord().get(i).getX());
          y[i] = pv.coordToPixY(ls.coord().get(i).getY());
        }
        // trous
        int index = ls.coord().size();
        for (int j = 0; j < poly.getInterior().size(); j++) {
          ls = poly.getInterior(j);
          for (int i = index; i < index + ls.coord().size(); i++) {
            x[i] = pv.coordToPixX(ls.coord().get(i - index).getX());
            y[i] = pv.coordToPixY(ls.coord().get(i - index).getY());
          }// i
          x[index + ls.coord().size()] = x0;
          y[index + ls.coord().size()] = y0;
          index += ls.coord().size() + 1;
        }// j
        Polygon pol = new Polygon(x, y, nb);

        // dessin du contour
        if (obj.getGeom() instanceof IPolygon) {
          pv.drawLimit(couleurContour, (IPolygon) obj.getGeom(),
              largeurContourmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
              BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND);
        } else if (obj.getGeom() instanceof IMultiSurface<?>) {
          pv.drawLimit(couleurContour, (IMultiSurface<?>) obj.getGeom(),
              largeurContourmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
              BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND);
        }

        VerticalHatchTexture texture = new VerticalHatchTexture(pol,
            hatchOffset, hatchBias, hatchColor, hatchThickness);
        texture.apply(pv.getG2D());
      }
    };
  }

  public static Symbolisation hatchedHorizontalPolygon(
      final Color couleurContour, final double largeurContourmm,
      final Color hatchColor, final int hatchOffset, final int hatchBias,
      final int hatchThickness) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        IPolygon poly = (IPolygon) obj.getGeom();
        int nb = poly.coord().size() + poly.getInterior().size();
        int[] x = new int[nb], y = new int[nb];
        // enveloppe exterieure
        IRing ls = poly.getExterior();
        int x0 = pv.coordToPixX(ls.coord().get(0).getX());
        int y0 = pv.coordToPixY(ls.coord().get(0).getY());
        for (int i = 0; i < ls.coord().size(); i++) {
          x[i] = pv.coordToPixX(ls.coord().get(i).getX());
          y[i] = pv.coordToPixY(ls.coord().get(i).getY());
        }
        // trous
        int index = ls.coord().size();
        for (int j = 0; j < poly.getInterior().size(); j++) {
          ls = poly.getInterior(j);
          for (int i = index; i < index + ls.coord().size(); i++) {
            x[i] = pv.coordToPixX(ls.coord().get(i - index).getX());
            y[i] = pv.coordToPixY(ls.coord().get(i - index).getY());
          }// i
          x[index + ls.coord().size()] = x0;
          y[index + ls.coord().size()] = y0;
          index += ls.coord().size() + 1;
        }// j
        Polygon pol = new Polygon(x, y, nb);

        // dessin du contour
        if (obj.getGeom() instanceof IPolygon) {
          pv.drawLimit(couleurContour, (IPolygon) obj.getGeom(),
              largeurContourmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
              BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND);
        } else if (obj.getGeom() instanceof IMultiSurface<?>) {
          pv.drawLimit(couleurContour, (IMultiSurface<?>) obj.getGeom(),
              largeurContourmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
              BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND);
        }

        HorizontalHatchTexture texture = new HorizontalHatchTexture(pol,
            hatchOffset, hatchBias, hatchColor, hatchThickness);
        texture.apply(pv.getG2D());
      }
    };
  }

  /**
   * lineraire avec largeur pixel
   * @return
   */
  public static Symbolisation ligne(final Color couleur, final int largeur,
      final int cap, final int join) {
    return new Symbolisation() {
      @SuppressWarnings("unchecked")
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification du type de geometrie
        if (!(obj.getGeom() instanceof ILineString)
            && !(obj.getGeom() instanceof IMultiCurve)) {
          Symbolisation.logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type de geometrie: "
              + obj.getGeom().getClass().getSimpleName());
          return;
        }

        // dessin de la ligne
        if (obj.getGeom() instanceof ILineString) {
          pv.draw(couleur, (ILineString) obj.getGeom(), largeur, cap, join);
        } else if (obj.getGeom() instanceof IMultiCurve) {
          pv.draw(couleur, (IMultiCurve<ILineString>) obj.getGeom(), largeur,
              cap, join);
        }
      }
    };
  }

  public static Symbolisation ligne(final Color couleur, final int largeur) {
    return Symbolisation.ligne(couleur, largeur, BasicStroke.CAP_ROUND,
        BasicStroke.JOIN_ROUND);
  }

  public static Symbolisation ligne(final Color couleur) {
    return Symbolisation.ligne(couleur, 2);
  }

  public static Symbolisation ligne() {
    return Symbolisation.ligne(Color.RED, 2);
  }

  /**
   * Provides a symbolisation for lines with dashes.
   * @param couleur
   * @param widthmm
   * @param dashes a float array like {dashLength1, spaceLength1, dashLength2,
   *          spaceLength2} with length expressed in metres
   * @return
   */
  public static Symbolisation dashedLine(final Color couleur,
      final double widthmm, final float[] dashes) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification du type de geometrie
        if (!(obj.getGeom() instanceof ILineString)
            && !(obj.getGeom() instanceof IMultiCurve)) {
          Symbolisation.logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type de geometrie: "
              + obj.getGeom().getClass().getSimpleName());
          return;
        }

        // dessin de la ligne
        if (obj.getGeom() instanceof ILineString) {
          pv.draw(couleur, (ILineString) obj.getGeom(),
              widthmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
              BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, dashes);
        }
      }
    };
  }

  /**
   * Provides a symbolisation for lines with dots.
   * @param couleur
   * @param widthmm
   * @param dotSpacing the space between dots in metres
   * @return
   */
  public static Symbolisation dottedLine(final Color couleur,
      final double widthmm, final float dotSpacing) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification du type de geometrie
        if (!(obj.getGeom() instanceof ILineString)
            && !(obj.getGeom() instanceof IMultiCurve)) {
          Symbolisation.logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type de geometrie: "
              + obj.getGeom().getClass().getSimpleName());
          return;
        }
        float[] dots = new float[] { 0, dotSpacing, 0, dotSpacing };
        // dessin de la ligne
        if (obj.getGeom() instanceof ILineString) {
          pv.draw(couleur, (ILineString) obj.getGeom(),
              widthmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
              BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, dots);
        }
      }
    };
  }

  /**
   * Provides a symbolisation for polygons with no fill and dashed outline.
   * @param outlineColor
   * @param widthmm
   * @param dashes a float array like {dashLength1, spaceLength1, dashLength2,
   *          spaceLength2} with length expressed in metres. See
   *          {@link BasicStroke}.
   * @return
   */
  public static Symbolisation dashedArea(final Color outlineColor,
      final double widthmm, final float[] dashes) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification du type de geometrie
        if (!(obj.getGeom() instanceof IPolygon)
            && !(obj.getGeom() instanceof IMultiSurface<?>)) {
          Symbolisation.logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type de geometrie: "
              + obj.getGeom().getClass().getSimpleName());
          return;
        }

        // dessin de la ligne
        if (obj.getGeom() instanceof IPolygon) {
          ILineString outline = ((IPolygon) obj.getGeom()).exteriorLineString();
          pv.draw(outlineColor, outline,
              widthmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
              BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, dashes);
        } else if (obj.getGeom() instanceof IMultiSurface<?>) {
          int n = ((IMultiSurface<?>) obj.getGeom()).size();
          for (int i = 0; i < n; i++) {
            IPolygon geom = (IPolygon) ((IMultiSurface<?>) obj.getGeom())
                .get(i);
            ILineString outline = geom.exteriorLineString();
            pv.draw(outlineColor, outline,
                widthmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, dashes);
          }
        }
      }
    };
  }

  /**
   * Provides a symbolisation for polygons with no fill and dotted outline.
   * @param outlineColor
   * @param widthmm
   * @param dotSpacing the space between dots in metres.
   * @return
   */
  public static Symbolisation dottedArea(final Color outlineColor,
      final double widthmm, final float dotSpacing) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification du type de geometrie
        if (!(obj.getGeom() instanceof IPolygon)
            && !(obj.getGeom() instanceof IMultiSurface<?>)) {
          Symbolisation.logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type de geometrie: "
              + obj.getGeom().getClass().getSimpleName());
          return;
        }
        float[] dots = new float[] { 0, dotSpacing, 0, dotSpacing };
        // dessin de la ligne
        if (obj.getGeom() instanceof IPolygon) {
          ILineString outline = ((IPolygon) obj.getGeom()).exteriorLineString();
          pv.draw(outlineColor, outline,
              widthmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
              BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, dots);
        } else if (obj.getGeom() instanceof IMultiSurface<?>) {
          int n = ((IMultiSurface<?>) obj.getGeom()).size();
          for (int i = 0; i < n; i++) {
            IPolygon geom = (IPolygon) ((IMultiSurface<?>) obj.getGeom())
                .get(i);
            ILineString outline = geom.exteriorLineString();
            pv.draw(outlineColor, outline,
                widthmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, dots);
          }
        }
      }
    };
  }

  /**
   * lineraire avec largeur mm carte
   * @return
   */
  public static Symbolisation ligne(final Color couleur,
      final double largeurmm, final int cap, final int join,
      final AbstractLayerGroup layerGroup) {
    return new Symbolisation() {
      @SuppressWarnings("unchecked")
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification du type de geometrie
        if (!(obj.getGeom() instanceof ILineString)
            && !(obj.getGeom() instanceof IMultiCurve)) {
          Symbolisation.logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type de geometrie: "
              + obj.getGeom().getClass().getSimpleName());
          return;
        }

        // dessin de la ligne
        if (layerGroup.symbolisationDisplay) {
          if (obj.getGeom() instanceof ILineString) {
            pv.draw(couleur, (ILineString) obj.getGeom(),
                largeurmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0, cap, join);
          } else if (obj.getGeom() instanceof IMultiCurve) {
            pv.draw(couleur, (IMultiCurve<ILineString>) obj.getGeom(),
                largeurmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0, cap, join);
          }
        } else if (obj.getGeom() instanceof ILineString) {
          pv.draw(couleur, (ILineString) obj.getGeom(),
              0.15 * 25000.0 / 1000.0, cap, join);
        } else if (obj.getGeom() instanceof IMultiCurve) {
          pv.draw(couleur, obj.getGeom());
        }
      }
    };
  }

  public static Symbolisation ligne(final Color couleur,
      final double largeurmm, final AbstractLayerGroup layerGroup) {
    return Symbolisation.ligne(couleur, largeurmm, BasicStroke.CAP_ROUND,
        BasicStroke.JOIN_ROUND, layerGroup);
  }

  public static Symbolisation ligne(final double largeurmm,
      final AbstractLayerGroup layerGroup) {
    return Symbolisation.ligne(Color.RED, largeurmm, BasicStroke.CAP_ROUND,
        BasicStroke.JOIN_ROUND, layerGroup);
  }

  public static Symbolisation ligne(final Color couleur,
      final double largeurmm, final int cap, final int join) {
    return Symbolisation.ligne(couleur, largeurmm, cap, join,
        CartagenApplication.getInstance().getLayerGroup());
  }

  public static Symbolisation ligne(final Color couleur, final double largeurmm) {
    return Symbolisation.ligne(couleur, largeurmm, CartagenApplication
        .getInstance().getLayerGroup());
  }

  public static Symbolisation ligne(final double largeurmm) {
    return Symbolisation.ligne(largeurmm, CartagenApplication.getInstance()
        .getLayerGroup());
  }

  /**
   * ponctuel: symboles ponctuels sous forme de disques rond colores (taille en
   * pixel écran)
   * @param couleur
   * @param largeur
   * @return
   */
  public static Symbolisation pointRond(final Color couleur, final int largeur) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification du type de geometrie
        if (!(obj.getGeom() instanceof IPoint)
            && !(obj.getGeom() instanceof IMultiPoint)) {

          Symbolisation.logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type de geometrie: "
              + obj.getGeom().getClass().getSimpleName());
          return;
        }

        // dessin des points
        if (obj.getGeom() instanceof IPoint) {
          pv.drawCircle(couleur, ((IPoint) obj.getGeom()), largeur);
        } else if (obj.getGeom() instanceof IMultiPoint) {
          pv.drawCircle(couleur, ((IMultiPoint) obj.getGeom()), largeur);
        }
      }
    };
  }

  public static Symbolisation pointRond() {
    return Symbolisation.pointRond(Color.RED, 4);
  }

  public static Symbolisation pointRond(final int largeur) {
    return Symbolisation.pointRond(Color.RED, largeur);
  }

  public static Symbolisation pointRond(final Color couleur) {
    return Symbolisation.pointRond(couleur, 4);
  }

  /**
   * ponctuel: symboles ponctuels sous forme de disques rond colores (taille en
   * mm carte)
   * @param couleur
   * @param largeurmm
   * @return
   */
  public static Symbolisation pointRond(final Color couleur,
      final double largeurmm, final AbstractLayerGroup layerGroup) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification du type de geometrie
        if (!(obj.getGeom() instanceof IPoint)
            && !(obj.getGeom() instanceof IMultiPoint)) {
          Symbolisation.logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type de geometrie: "
              + obj.getGeom().getClass().getSimpleName());
          return;
        }

        // dessin des points

        if (layerGroup.symbolisationDisplay) {
          if (obj.getGeom() instanceof IPoint) {
            pv.drawCircle(couleur, ((IPoint) obj.getGeom()),
                largeurmm * Legend.getSYMBOLISATI0N_SCALE() / 1000.0);
          } else if (obj.getGeom() instanceof IMultiPoint) {
            pv.drawCircle(couleur, ((IMultiPoint) obj.getGeom()), largeurmm
                * Legend.getSYMBOLISATI0N_SCALE() / 1000.0);
          }
        } else if (obj.getGeom() instanceof IPoint) {
          pv.drawCircle(couleur, ((IPoint) obj.getGeom()), 3);
        } else if (obj.getGeom() instanceof IMultiPoint) {
          pv.drawCircle(couleur, ((IMultiPoint) obj.getGeom()), 3);
        }
      }
    };
  }

  /**
   * Ponctuel sous forme de croix (+). Taille en pixels, ne tient pas compte des
   * layer groups.
   * @param colour
   * @param widthPixels
   * @return the defined symbolisation
   */
  public static Symbolisation pointAsPlusCross(final Color colour,
      final int widthPixels) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification du type de geometrie
        if (!(obj.getGeom() instanceof IPoint)) {
          Symbolisation.logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type de geometrie: "
              + obj.getGeom().getClass().getSimpleName());
          return;
        }
        // dessin du point
        pv.drawPlusCross(colour, (IPoint) obj.getGeom(), widthPixels);
      }
    };
  }

  /**
   * ponctuel: symboles ponctuels sous forme de disques rond colores (taille en
   * mm carte)
   * @param couleur
   * @param largeurmm
   * @return
   */
  public static Symbolisation specialPoint(final boolean symbolisationDisplay) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification du type de geometrie
        if (!(obj instanceof SpecialPoint)) {
          Symbolisation.logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type" + obj.getGeom().getClass().getSimpleName());
          return;
        }

        // dessin des points

        SpecialPoint sp = ((SpecialPoint) obj);
        IPoint pt = sp.getGeom();
        if (symbolisationDisplay) {
          if (sp.getPointType() == SpecialPointType.ACCIDENT) {

            IDirectPosition p0 = new DirectPosition(pt.getPosition().getX(), pt
                .getPosition().getY() - 6);
            IDirectPosition p1 = new DirectPosition(
                pt.getPosition().getX() - 6, pt.getPosition().getY() + 4);
            IDirectPosition p2 = new DirectPosition(
                pt.getPosition().getX() + 6, pt.getPosition().getY() + 4);

            GM_Triangle lineString = new GM_Triangle(p0, p1, p2);

            pv.draw(Color.white, lineString);

            pv.draw(sp.getSymbolColor(), lineString.getExterior(),
                0.15 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

          } else if (sp.getPointType() == SpecialPointType.INTERSECTIONRIVERROAD) {

            // CartagenApplication.getInstance().getFrame().getLayerManager()
            // .addToGeometriesPool(new GM_Point(pt.getPosition()));

            double a = 1.2;
            double b = 1.6;
            double c = 4;
            double x = pt.getPosition().getX();
            double y = pt.getPosition().getY();
            GM_LineString lineString = new GM_LineString();

            lineString.addControlPoint(new DirectPosition(x - b - a - 3, y - c
                - a - 3));
            lineString.addControlPoint(new DirectPosition(x - b - a - 3, y + c
                + a + 3));
            lineString.addControlPoint(new DirectPosition(x + b + a + 3, y + c
                + a + 3));
            lineString.addControlPoint(new DirectPosition(x + b + a + 3, y - c
                - a - 3));
            lineString.addControlPoint(new DirectPosition(x - b - a - 3, y - c
                - a - 3));
            pv.drawLimit(Color.black, new GM_Polygon(lineString),
                0.1 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

            pv.draw(sp.getSymbolColor(), new GM_Polygon(lineString));
            lineString = new GM_LineString();
            lineString
                .addControlPoint(new DirectPosition(x + b + a, y - c - a));
            lineString.addControlPoint(new DirectPosition(x + b, y - c));
            lineString.addControlPoint(new DirectPosition(x + b, y + c));
            lineString
                .addControlPoint(new DirectPosition(x + b + a, y + c + a));
            pv.draw(Color.red, lineString,
                0.1 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

            lineString.clearSegments();
            lineString = new GM_LineString();
            lineString
                .addControlPoint(new DirectPosition(x - b - a, y - c - a));
            lineString.addControlPoint(new DirectPosition(x - b, y - c));
            lineString.addControlPoint(new DirectPosition(x - b, y + c));
            lineString
                .addControlPoint(new DirectPosition(x - b - a, y + c + a));

            pv.draw(Color.red, lineString,
                0.1 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

          } else if (sp.getPointType() == SpecialPointType.ROUNDABOUT) {

            pv.drawCircle(Color.red, new GM_Point(pt.getPosition()), 16);
            pv.drawCircle(sp.getSymbolColor(), new GM_Point(pt.getPosition()),
                13);

          }

          else if (sp.getPointType() == SpecialPointType.CROSSROAD) {
            double c = 8;
            double x = pt.getPosition().getX();
            double y = pt.getPosition().getY();
            GM_LineString lineString = new GM_LineString();
            lineString.addControlPoint(new DirectPosition(x - c, y));
            lineString.addControlPoint(new DirectPosition(x, y - c));
            lineString.addControlPoint(new DirectPosition(x + c, y));
            lineString.addControlPoint(new DirectPosition(x, y + c));
            lineString.addControlPoint(new DirectPosition(x - c, y));
            pv.drawLimit(Color.black, new GM_Polygon(lineString),
                0.1 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

            pv.draw(sp.getSymbolColor(), new GM_Polygon(lineString));

            lineString = new GM_LineString();
            lineString.addControlPoint(new DirectPosition(x - c * 0.6, y));
            lineString.addControlPoint(new DirectPosition(x + c * 0.6, y));
            pv.draw(Color.black, lineString,
                0.07 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE);

            lineString = new GM_LineString();
            lineString.addControlPoint(new DirectPosition(x, y - c * 0.6));
            lineString.addControlPoint(new DirectPosition(x, y + c * 0.6));
            pv.draw(Color.black, lineString,
                0.07 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0,
                BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE);

          }
        }
      }

    };
  }

  public static Symbolisation pointRond(final Color couleur,
      final double largeurmm) {
    return Symbolisation.pointRond(couleur, largeurmm, CartagenApplication
        .getInstance().getLayerGroup());
  }

  /**
   * style qui affiche un texte sur un objet. ce texte est renvoye par une
   * methode de l'objet sans argument
   * @param nomMethode le nom de la methode
   * @return
   */
  public static Symbolisation texte(final String nomMethode, final Font font,
      final Color col) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }
        if (!(obj instanceof IGeneObj)) {
          return;
        }

        // recupere le texte a afficher
        String texte = null;
        try {
          Method m = obj.getClass().getMethod(nomMethode, new Class[0]);
          texte = m.invoke(obj, new Object[0]).toString();
        } catch (Exception e1) {
          try {
            IGeneObj geneObj = (IGeneObj) obj;
            Method m = geneObj.getClass().getMethod(nomMethode, new Class[0]);
            texte = m.invoke(geneObj, new Object[0]).toString();
            double value = Double.parseDouble(texte);
            value = ((int) (value * 100.0)) / 100.0;
            texte = "" + value;
          } catch (Exception e2) {
            Symbolisation.logger
                .warn("probleme dans le dessin de "
                    + obj
                    + ". impossible de recuperer la valeur renvoyee par la methode "
                    + nomMethode);
            return;
          }
        }
        pv.drawText(col, font, obj.getGeom(), texte);
      }
    };
  }

  static Font fontDefaut = new Font("Arial", Font.BOLD, 15);

  public static Symbolisation texte(final String nomMethode, final Color col) {
    return Symbolisation.texte(nomMethode, Symbolisation.fontDefaut, col);
  }

  public static Symbolisation texte(final String nomMethode) {
    return Symbolisation.texte(nomMethode, Color.RED);
  }

  /**
   * style qui dessine un objet d'une couleur ou d'une autre suivant la valeur
   * renvoyée par une méthode booleenne
   * @param nomMethode le nom de la methode renvoyant un booleen
   * @param couleurTrue la couleur dans laquelle dessiner l'objet si la valeur
   *          renvoyée par la methode est true
   * @param couleurFalse la couleur dans laquelle dessiner l'objet si la valeur
   *          renvoyée par la methode est false
   * @return
   */
  public static Symbolisation booleen(final String nomMethode,
      final Color couleurTrue, final Color couleurFalse) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // recupere le texte a afficher
        Boolean b = null;
        try {
          Method m = obj.getClass().getMethod(nomMethode, new Class[0]);
          b = (Boolean) m.invoke(obj, new Object[0]);
        } catch (Exception e) {
          Symbolisation.logger.warn("probleme dans le dessin de " + obj
              + ". impossible de recuperer la valeur renvoyee par la methode "
              + nomMethode);
          e.printStackTrace();
          return;
        }

        if (b == null) {
          Symbolisation.logger.warn("probleme dans le dessin de " + obj
              + ". methode " + nomMethode + " renvoit un booleen null");
          return;
        }
        if (b.booleanValue() && couleurTrue != null) {
          pv.draw(couleurTrue, obj.getGeom());
        } else if (!b.booleanValue() && couleurFalse != null) {
          pv.draw(couleurFalse, obj.getGeom());
        }
      }
    };
  }

  public static Symbolisation booleen(final String nomMethode) {
    return Symbolisation.booleen(nomMethode, Color.GREEN, Color.RED);
  }

  /**
   * surfacique avec ombrage
   * @return
   */
  public static Symbolisation surfaceOmbrage(final Color couleurSurface,
      final Color couleurOmbre, final double decalageOmbragemm,
      final AbstractLayerGroup layerGroup) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification
        if (!(obj.getGeom() instanceof IPolygon)) {
          Symbolisation.logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type de geometrie: "
              + obj.getGeom().getClass().getSimpleName());
          return;
        }

        IPolygon poly = (IPolygon) obj.getGeom();

        if (layerGroup.symbolisationDisplay) {
          // dessin de l'ombre
          pv.draw(couleurOmbre, poly);
          // dessin de l'objet
          double dec = 0.7071 * decalageOmbragemm
              * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
          pv.draw(couleurSurface, CommonAlgorithms.translation(poly, dec, dec));
        } else {
          // dessin de la surface
          pv.draw(couleurSurface, (IPolygon) obj.getGeom());
        }

      }
    };
  }

  public static Symbolisation surfaceOmbrage(final Color couleurSurface,
      final Color couleurOmbre, final double decalageOmbragemm) {
    return Symbolisation.surfaceOmbrage(couleurSurface, couleurOmbre,
        decalageOmbragemm, CartagenApplication.getInstance().getLayerGroup());
  }

}
