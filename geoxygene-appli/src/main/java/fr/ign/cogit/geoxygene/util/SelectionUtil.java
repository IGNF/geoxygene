/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.style.Layer;

public class SelectionUtil {

    /**
     * Shortcut to get all the selected objects by code.
     * 
     * @return
     */
    public static Set<IFeature> getSelectedObjects(GeOxygeneApplication appli) {
        return appli.getMainFrame().getSelectedProjectFrame().getLayerViewPanel().getSelectedFeatures();
    }

    /**
     * Shortcut to get the first of the selected objects by code.
     * 
     * @return
     */
    public static IFeature getFirstSelectedObject(GeOxygeneApplication appli) {
        Set<IFeature> features = appli.getMainFrame().getSelectedProjectFrame().getLayerViewPanel()
                .getSelectedFeatures();
        return features.iterator().next();
    }

    /**
     * Shortcut to get all the selected objects by code.
     * 
     * @return
     */
    public static List<IFeature> getListOfSelectedObjects(GeOxygeneApplication appli) {
        return new ArrayList<IFeature>(
                appli.getMainFrame().getSelectedProjectFrame().getLayerViewPanel().getSelectedFeatures());
    }

    /**
     * Shortcut to get the selected objects from one layer by code.
     * 
     * @return
     */
    public static Set<IFeature> getSelectedObjects(GeOxygeneApplication appli, String layerName) {
        Set<IFeature> selected = new HashSet<>(
                appli.getMainFrame().getSelectedProjectFrame().getLayerViewPanel().getSelectedFeatures());
        Layer layer = appli.getMainFrame().getSelectedProjectFrame().getLayer(layerName);
        selected.retainAll(layer.getFeatureCollection());
        return selected;
    }

    /**
     * Add a feature to the selection.
     * 
     * @param appli
     * @param feature
     */
    public static void addFeatureToSelection(GeOxygeneApplication appli, IFeature feature) {
        appli.getMainFrame().getSelectedProjectFrame().getLayerViewPanel().getSelectedFeatures().add(feature);
    }

    /**
     * Shortcut to get all the objects of the selected layer by code.
     * 
     * @return
     */
    public static IFeatureCollection<? extends IFeature> getSelectedLayer(GeOxygeneApplication appli) {
        Set<Layer> layers = appli.getMainFrame().getSelectedProjectFrame().getLayerLegendPanel().getSelectedLayers();
        if (layers.size() > 1) {
            Layer layer = layers.iterator().next();
            return layer.getFeatureCollection();
        }
        return new FT_FeatureCollection<IFeature>();
    }

    /**
     * Checks if the selection is empty or not.
     * 
     * @param appli
     * @return
     */
    public static boolean isEmpty(GeOxygeneApplication appli) {
        return appli.getMainFrame().getSelectedProjectFrame().getLayerViewPanel().getSelectedFeatures().size() == 0;
    }

    /**
     * Clears the selection.
     */
    public static void clearSelection(GeOxygeneApplication appli) {
        appli.getMainFrame().getSelectedProjectFrame().getLayerViewPanel().getSelectedFeatures().clear();
    }
}
