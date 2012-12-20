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
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.net.URL;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.apache.log4j.Logger;

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
import fr.ign.cogit.cartagen.software.interfacecartagen.AbstractLayerGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.GeneralisationSymbolisation;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Symbolisation;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.VisuPanel;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;

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
          try {
            SymbolisationPeaRep.cropSymbol(layerGroup, pv, obj);
          } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
          }
        }
        if (obj instanceof VMAPSwamp) {
          try {
            SymbolisationPeaRep.swampSymbol(layerGroup, pv, obj);
          } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
          }
        }
        if (obj instanceof VMAPOrchard) {
          SymbolisationPeaRep.orchardSymbol(layerGroup, pv, obj);
        }
      }
    };
  }

  private static void groundSymbol(final AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) {
    pv.draw(SymbolisationPeaRep.GROUND_COLOR, (IPolygon) obj.getGeom());
  }

  private static void grassSymbol(final AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) {
    pv.draw(SymbolisationPeaRep.GRASS_COLOR, (IPolygon) obj.getGeom());
  }

  private static void builtUpSymbol(final AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) {
    pv.draw(SymbolisationPeaRep.BUILTUP_COLOR, (IPolygon) obj.getGeom());
  }

  private static void cropSymbol(final AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) throws NoninvertibleTransformException {
    pv.draw(SymbolisationPeaRep.CROP_COLOR, (IPolygon) obj.getGeom());
  }

  private static void swampSymbol(final AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) throws NoninvertibleTransformException {
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
    BufferedImage buff = new BufferedImage(shapeWidth.intValue(), shapeHeight
        .intValue(), BufferedImage.TYPE_INT_ARGB);
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

  private static void orchardSymbol(final AbstractLayerGroup layerGroup,
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
    BufferedImage buff = new BufferedImage(shapeWidth.intValue(), shapeHeight
        .intValue(), BufferedImage.TYPE_INT_ARGB);
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

  private static void storagePtSymbol(AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) {
    // TODO Auto-generated method stub

  }

  private static void obstrPtSymbol(AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) {
    // TODO Auto-generated method stub

  }

  private static void towerSymbol(AbstractLayerGroup layerGroup, VisuPanel pv,
      IFeature obj) {
    // TODO Auto-generated method stub

  }

  private static void powerPtSymbol(AbstractLayerGroup layerGroup,
      VisuPanel pv, IFeature obj) {
    // TODO Auto-generated method stub

  }

  private static void commPtSymbol(AbstractLayerGroup layerGroup, VisuPanel pv,
      IFeature obj) {
    GeneralisationSymbolisation.drawPtSymbolRaster(layerGroup, pv, obj,
        "/images/symbols/mobilephonetower.png", 40, 40);
  }
}
