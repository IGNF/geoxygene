/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

public class SelectionUtil {

  /**
   * Shortcut to get all the selected objects by code.
   * @return
   */
  public static Set<IFeature> getSelectedObjects(GeOxygeneApplication appli) {
    return appli.getMainFrame().getSelectedProjectFrame().getLayerViewPanel()
        .getSelectedFeatures();
  }

  /**
   * Shortcut to get the selected objects from one layer by code.
   * @return
   */
  public static Set<IFeature> getSelectedObjects(GeOxygeneApplication appli,
      String layerName) {
    Set<IFeature> selected = new HashSet<>(appli.getMainFrame()
        .getSelectedProjectFrame().getLayerViewPanel().getSelectedFeatures());
    Layer layer = appli.getMainFrame().getSelectedProjectFrame()
        .getLayer(layerName);
    selected.retainAll(layer.getFeatureCollection());
    return selected;
  }

  /**
   * Shortcut to get all the selected objects by code.
   * @return
   */
  public static Collection<IGeneObj> getWindowObjects(
      GeOxygeneApplication appli, String popName) {
    IPopulation<IGeneObj> pop = CartAGenDoc.getInstance().getCurrentDataset()
        .getCartagenPop(popName);
    return pop.select(appli.getMainFrame().getSelectedProjectFrame()
        .getLayerViewPanel().getViewport().getEnvelopeInModelCoordinates());
  }

  /**
   * Shortcut to get all the window objects by code.
   * @return
   */
  public static Set<IGeneObj> getAllWindowObjects(GeOxygeneApplication appli) {
    Set<IGeneObj> allWindowObjs = new HashSet<IGeneObj>();
    StyledLayerDescriptor sld = CartAGenDoc.getInstance().getCurrentDataset()
        .getSld();
    for (Layer layer : sld.getLayers()) {
      if (layer.getName().equals(CartAGenDataSet.GEOM_POOL))
        continue;
      IPopulation<IGeneObj> pop = CartAGenDoc.getInstance().getCurrentDataset()
          .getCartagenPop(layer.getName());
      allWindowObjs.addAll(pop.select(appli.getMainFrame()
          .getSelectedProjectFrame().getLayerViewPanel().getViewport()
          .getEnvelopeInModelCoordinates()));
    }
    return allWindowObjs;
  }

  /**
   * Add a feature to the selection.
   * @param appli
   * @param feature
   */
  public static void addFeatureToSelection(GeOxygeneApplication appli,
      IGeneObj feature) {
    appli.getMainFrame().getSelectedProjectFrame().getLayerViewPanel()
        .getSelectedFeatures().add(feature);
  }

  /**
   * Shortcut to get all the objects of the selected layer by code.
   * @return
   */
  public static IFeatureCollection<? extends IFeature> getSelectedLayer(
      GeOxygeneApplication appli) {
    Set<Layer> layers = appli.getMainFrame().getSelectedProjectFrame()
        .getLayerLegendPanel().getSelectedLayers();
    if (layers.size() > 1) {
      Layer layer = layers.iterator().next();
      return layer.getFeatureCollection();
    }
    return new FT_FeatureCollection<IFeature>();
  }

  /**
   * Checks if the selection is empty or not.
   * @param appli
   * @return
   */
  public static boolean isEmpty(GeOxygeneApplication appli) {
    return appli.getMainFrame().getSelectedProjectFrame().getLayerViewPanel()
        .getSelectedFeatures().size() == 0;
  }
}
