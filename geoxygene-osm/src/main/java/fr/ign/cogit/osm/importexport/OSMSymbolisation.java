package fr.ign.cogit.osm.importexport;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.misc.IPointOfInterest;
import fr.ign.cogit.cartagen.software.interfacecartagen.AbstractLayerGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.GeneralisationSymbolisation;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Symbolisation;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.VisuPanel;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

public class OSMSymbolisation {
  final static Logger logger = Logger.getLogger(OSMSymbolisation.class
      .getName());

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
