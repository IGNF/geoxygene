/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea.TaxiwayType;
import fr.ign.cogit.cartagen.core.genericschema.misc.IPointOfInterest;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPBuiltUpArea;
import fr.ign.cogit.cartagen.pearep.vmap.ind.VMAPObstrPoint;
import fr.ign.cogit.cartagen.pearep.vmap.ind.VMAPStoragePoint;
import fr.ign.cogit.cartagen.pearep.vmap.phy.VMAPGround;
import fr.ign.cogit.cartagen.pearep.vmap.pop.VMAPBuiltUpArea;
import fr.ign.cogit.cartagen.pearep.vmap.pop.VMAPTower;
import fr.ign.cogit.cartagen.pearep.vmap.uti.VMAPCommPoint;
import fr.ign.cogit.cartagen.pearep.vmap.uti.VMAPPowerPoint;
import fr.ign.cogit.cartagen.pearep.vmap.veg.VMAPCrop;
import fr.ign.cogit.cartagen.pearep.vmap.veg.VMAPGrass;
import fr.ign.cogit.cartagen.pearep.vmap.veg.VMAPOrchard;
import fr.ign.cogit.cartagen.pearep.vmap.veg.VMAPSwamp;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.cartagen.software.interfacecartagen.AbstractLayerGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.GeneralisationSymbolisation;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Symbolisation;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.VisuPanel;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;

public class SymbolisationPeaRep {

  final static Logger logger = Logger.getLogger(SymbolisationPeaRep.class
      .getName());

  public final static Color GROUND_COLOR = new Color(139, 108, 66);
  public final static Color BUILTUP_COLOR = new Color(221, 152, 92);
  public final static Color GRASS_COLOR = new Color(139, 212, 65);
  public final static Color SWAMP_COLOR = new Color(104, 157, 113);
  public final static Color CROP_COLOR = new Color(58, 242, 75);
  public final static Color ORCHARD_COLOR = new Color(0, 255, 0);

  /**
   * symbolise the VMAP land use parcels.
   * @return
   */
  public static Symbolisation landuse(final AbstractLayerGroup layerGroup) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification
        if (!(obj.getGeom() instanceof IPolygon)
            && !(obj.getGeom() instanceof IMultiSurface<?>)) {
          SymbolisationPeaRep.logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type de geometrie: "
              + obj.getGeom().getClass().getSimpleName());
          return;
        }

        if (obj instanceof VMAPGrass) {
          SymbolisationPeaRep.grassSymbol(layerGroup, pv, obj);
        }
        if (obj instanceof VMAPGround) {
          SymbolisationPeaRep.groundSymbol(layerGroup, pv, obj);
        }
        if (obj instanceof VMAPBuiltUpArea || obj instanceof MGCPBuiltUpArea) {
          SymbolisationPeaRep.builtUpSymbol(layerGroup, pv, obj);
        }
        if (obj instanceof VMAPCrop) {
          SymbolisationPeaRep.cropSymbol(layerGroup, pv, obj);
        }
        if (obj instanceof VMAPSwamp) {
          SymbolisationPeaRep.swampSymbol(layerGroup, pv, obj);
        }
        if (obj instanceof VMAPOrchard) {
          SymbolisationPeaRep.orchardSymbol(layerGroup, pv, obj);
        }
      }
    };
  }

  /**
   * symbolise the runways.
   * @param layerGroup
   * @return
   */
  public static Symbolisation runways(final AbstractLayerGroup layerGroup) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification
        if (!(obj.getGeom() instanceof IPolygon)
            && !(obj.getGeom() instanceof IMultiSurface<?>)) {
          SymbolisationPeaRep.logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type de geometrie: "
              + obj.getGeom().getClass().getSimpleName());
          return;
        }

        if (obj instanceof IRunwayArea) {
          pv.draw(GeneralisationLegend.RUNWAY_SURFACE_COULEUR, obj.getGeom());
        }
        if (obj instanceof ITaxiwayArea) {
          if (((ITaxiwayArea) obj).getType().equals(TaxiwayType.TAXIWAY))
            pv.draw(new Color(210, 202, 236), (IPolygon) obj.getGeom());
          else
            pv.draw(new Color(187, 172, 172), (IPolygon) obj.getGeom());
        }
      }
    };
  }

  /**
   * symbolise the runways.
   * @param layerGroup
   * @return
   */
  public static Symbolisation railway(final AbstractLayerGroup layerGroup) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification
        if (!(obj.getGeom() instanceof ILineString)) {
          SymbolisationPeaRep.logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type de geometrie: "
              + obj.getGeom().getClass().getSimpleName());
          return;
        }

        pv.draw(GeneralisationLegend.RES_FER_COULEUR,
            (ILineString) obj.getGeom(), ((IRailwayLine) obj).getWidth(),
            BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, null);

        // put a 1 mm step between perpendicular symbols
        double step = 1.0 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
        double radius = 0.1 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
        double abscurv = step;
        ILineString line = (ILineString) obj.getGeom();
        while (abscurv < line.length()) {
          IDirectPosition pt = Operateurs.pointEnAbscisseCurviligne(line,
              abscurv);
          IDirectPosition nearest = CommonAlgorithmsFromCartAGen
              .getNearestOtherVertexFromPoint(line, pt);
          // compute the line equation
          Segment segment = new Segment(pt, nearest)
              .getPerpendicularSegment(true);
          Set<IDirectPosition> inter = segment.intersectionWithCircle(pt,
              radius);
          if (inter.size() != 2) {
            abscurv += step;
            continue;
          }
          Iterator<IDirectPosition> iter = inter.iterator();
          ILineSegment seg = new GM_LineSegment(iter.next(), iter.next());
          pv.draw(GeneralisationLegend.RES_FER_COULEUR, seg);
          abscurv += step;
        }
      }
    };
  }

  private static void groundSymbol(
      @SuppressWarnings("unused") final AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) {
    pv.draw(SymbolisationPeaRep.GROUND_COLOR, (IPolygon) obj.getGeom());
  }

  private static void grassSymbol(
      @SuppressWarnings("unused") final AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) {
    pv.draw(SymbolisationPeaRep.GRASS_COLOR, (IPolygon) obj.getGeom());
  }

  private static void builtUpSymbol(
      @SuppressWarnings("unused") final AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) {
    pv.draw(SymbolisationPeaRep.BUILTUP_COLOR, (IPolygon) obj.getGeom());
  }

  private static void cropSymbol(
      @SuppressWarnings("unused") final AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) {
    pv.draw(SymbolisationPeaRep.CROP_COLOR, (IPolygon) obj.getGeom());
  }

  private static void swampSymbol(
      @SuppressWarnings("unused") final AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) {
    ExternalGraphic tree = new ExternalGraphic();
    URL url = Symbolisation.class
        .getResource("/images/symbols/" + "swamp2.png"); //$NON-NLS-1$
    tree.setHref(url.toString());
    tree.setFormat("png"); //$NON-NLS-1$
    Image image = tree.getOnlineResource();

    Shape shape = pv.toShape(obj.getGeom());
    Double width = new Double(Math.max(1, shape.getBounds2D().getWidth()));
    Double height = new Double(Math.max(1, shape.getBounds2D().getHeight()));
    Double shapeHeight = new Double(15);
    double factor = shapeHeight / image.getHeight(null);
    Double shapeWidth = new Double(Math.max(image.getWidth(null) * factor, 1));
    AffineTransform transform = AffineTransform.getTranslateInstance(shape
        .getBounds2D().getMinX(), shape.getBounds2D().getMinY());
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

    // draw limits
    pv.drawLimit(SymbolisationPeaRep.SWAMP_COLOR, (IPolygon) obj.getGeom(),
        0.2 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0, BasicStroke.CAP_SQUARE,
        BasicStroke.JOIN_MITER);
  }

  private static void orchardSymbol(
      @SuppressWarnings("unused") final AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) {
    ExternalGraphic tree = new ExternalGraphic();
    URL url = Symbolisation.class
        .getResource("/images/symbols/" + "Orchard.gif"); //$NON-NLS-1$
    tree.setHref(url.toString());
    tree.setFormat("png"); //$NON-NLS-1$
    Image image = tree.getOnlineResource();

    Shape shape = pv.toShape(obj.getGeom());
    Double width = new Double(Math.max(1, shape.getBounds2D().getWidth()));
    Double height = new Double(Math.max(1, shape.getBounds2D().getHeight()));
    Double shapeHeight = new Double(10);
    double factor = shapeHeight / image.getHeight(null);
    Double shapeWidth = new Double(Math.max(image.getWidth(null) * factor, 1));
    AffineTransform transform = AffineTransform.getTranslateInstance(shape
        .getBounds2D().getMinX(), shape.getBounds2D().getMinY());
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

    // draw limits
    pv.drawLimit(SymbolisationPeaRep.ORCHARD_COLOR, (IPolygon) obj.getGeom(),
        0.2 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0, BasicStroke.CAP_SQUARE,
        BasicStroke.JOIN_MITER);
  }

  /**
   * symbolise the VMAP miscellaneous points.
   * @return
   */
  public static Symbolisation miscPoints(final AbstractLayerGroup layerGroup) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification
        if (!(obj.getGeom() instanceof IPolygon)
            && !(obj.getGeom() instanceof IMultiSurface<?>)) {
          SymbolisationPeaRep.logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type de geometrie: "
              + obj.getGeom().getClass().getSimpleName());
          return;
        }

        if (obj instanceof VMAPCommPoint) {
          SymbolisationPeaRep.commPtSymbol(layerGroup, pv, obj);
        }
        if (obj instanceof VMAPPowerPoint) {
          SymbolisationPeaRep.powerPtSymbol(layerGroup, pv, obj);
        }
        if (obj instanceof VMAPTower) {
          SymbolisationPeaRep.towerSymbol(layerGroup, pv, obj);
        }
        if (obj instanceof VMAPObstrPoint) {
          SymbolisationPeaRep.obstrPtSymbol(layerGroup, pv, obj);
        }
        if (obj instanceof VMAPStoragePoint) {
          SymbolisationPeaRep.storagePtSymbol(layerGroup, pv, obj);
        }
      }

    };
  }

  @SuppressWarnings("unused")
  private static void storagePtSymbol(AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) {
    // TODO Auto-generated method stub

  }

  @SuppressWarnings("unused")
  private static void obstrPtSymbol(AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) {
    // TODO Auto-generated method stub

  }

  @SuppressWarnings("unused")
  private static void towerSymbol(AbstractLayerGroup layerGroup, VisuPanel pv,
      IFeature obj) {
    // TODO Auto-generated method stub

  }

  @SuppressWarnings("unused")
  private static void powerPtSymbol(AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) {
    // TODO Auto-generated method stub

  }

  private static void commPtSymbol(AbstractLayerGroup layerGroup, VisuPanel pv,
      IFeature obj) {
    GeneralisationSymbolisation.drawPtSymbolRaster(layerGroup, pv, obj,
        "/images/symbols/mobilephonetower.png", 40, 40);
  }

  /**
   * symbolise the OSM points of interest.
   * @return
   */
  public static Symbolisation pointsOfInterest(
      final AbstractLayerGroup layerGroup, final float symbolSize) {
    return new Symbolisation() {
      @Override
      public void draw(VisuPanel pv, IFeature obj) {
        if (obj.isDeleted()) {
          return;
        }

        // verification
        if (!(obj.getGeom() instanceof IPoint)
            && !(obj.getGeom() instanceof IMultiSurface<?>)) {
          logger.warn("probleme dans le dessin de " + obj
              + ". Mauvais type de geometrie: "
              + obj.getGeom().getClass().getSimpleName());
          return;
        }

        GeneralisationSymbolisation.drawPtSymbolRaster(layerGroup, pv, obj,
            ((IPointOfInterest) obj).getSymbol(), symbolSize);
      }
    };
  }
}
