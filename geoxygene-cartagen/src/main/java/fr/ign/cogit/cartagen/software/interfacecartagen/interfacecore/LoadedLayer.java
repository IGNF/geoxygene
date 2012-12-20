/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
/**
 * @author julien Gaffuri 28 janv. 2009
 */
package fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore;

import java.util.ArrayList;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * une couche d'objets destine a être symbolise et affiché dans un panelvisu
 * gestion du cache d'objets a afficher
 * @author julien Gaffuri 28 janv. 2009
 */
public class LoadedLayer extends Layer {
  // private final static Logger
  // logger=Logger.getLogger(Couche.class.getName());

  /**
   * les objets de la couche a afficher
   */
  private IFeatureCollection<? extends IFeature> features = null;

  public IFeatureCollection<? extends IFeature> getFeatures() {
    return this.features;
  }

  public void setFeatures(IFeatureCollection<? extends IFeature> features) {
    this.features = features;
  }

  public LoadedLayer(IFeatureCollection<? extends IFeature> featureCollection,
      boolean selectionnable) {
    this.features = featureCollection;
    this.setSelectable(selectionnable);
    this.symbolisedLayers = new ArrayList<SymbolisedLayer>();
  }

  public LoadedLayer(IFeatureCollection<? extends IFeature> featureCollection) {
    // par defaut, non selectionnable
    this(featureCollection, false);
  }

  public LoadedLayer() {
    this(new FT_FeatureCollection<IFeature>());
  }

  @Override
  protected void cacheUpdate(VisuPanel pv) throws InterruptedException {
    // vide la cache precedent
    this.displayCache.clear();

    // parcours des objets et ajout éventuel de chacun d'eux au cache
    for (IFeature obj : this.features) {
      pv.stopDisplayTest();
      if (pv.hasToBeDisplayed(obj)) {
        this.displayCache.add(obj);
      }
    }

    // le cache est a jour...
    this.setCacheUpdate(false);
  }

  @Override
  public String toString() {
    String type = "";
    if (this.getFeatures() != null && this.getFeatures().size() > 0) {
      type = " - type: " + this.getFeatures().get(0).getClass().getSimpleName();
    }
    return this.getClass().getSimpleName() + type + " - symb layers:"
        + this.symbolisedLayers.size();
  }

}
