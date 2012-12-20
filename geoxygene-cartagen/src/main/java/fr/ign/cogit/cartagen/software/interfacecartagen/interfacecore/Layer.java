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
 * Permet gestion du cache d'affichage.
 * @author julien Gaffuri 28 janv. 2009
 */
public abstract class Layer {
  // private final static Logger
  // logger=Logger.getLogger(Couche.class.getName());

  /**
   * les couches symbolisees de cette couche
   */
  public ArrayList<SymbolisedLayer> symbolisedLayers;

  /**
   * le cache des objets parmi ceux de la population a afficher
   */
  protected IFeatureCollection<IFeature> displayCache = new FT_FeatureCollection<IFeature>();

  /**
   * renvoit le cache des objets a afficher en le recalculant eventuellement
   * @param pv le panelvisu dans lequel afficher
   * @return la collection des objets du cache
   * @throws InterruptedException
   */
  public IFeatureCollection<?> getDisplayCache(VisuPanel pv)
      throws InterruptedException {
    if (this.getCacheUpdate()) {
      this.cacheUpdate(pv);
    }
    return this.displayCache;
  }

  /**
   * Vide le cache des objets à afficher
   */
  public void emptyDisplayCache() {
    this.displayCache = new FT_FeatureCollection<IFeature>();
  }

  /**
   * @param pv
   */
  protected abstract void cacheUpdate(VisuPanel pv) throws InterruptedException;

  /**
   * boolean indiquant si le cache des objets aafficher doit être recalculé ou
   * non
   */
  private boolean cacheUpdate = true;

  /**
   * @return
   */
  public boolean getCacheUpdate() {
    return this.cacheUpdate;
  }

  /**
   * @param cacheUpdate
   */
  public void setCacheUpdate(boolean cacheUpdate) {
    this.cacheUpdate = cacheUpdate;
  }

  /**
   * indique si les objets de la couche sont selectionables
   */
  private boolean selectable = false;

  /**
   * @return
   */
  public boolean isSelectable() {
    return this.selectable;
  }

  /**
   * @param selectionnable
   */
  public void setSelectable(boolean selectionnable) {
    this.selectable = selectionnable;
  }

  /**
   * indique si la couche est visible, cad si une des couches symbolisees
   * attachees l'est
   */
  public boolean isVisible() {
    // la couche est visible des qu'une de ses couches symbolisees l'est
    for (SymbolisedLayer cs : this.symbolisedLayers) {
      if (cs.isVisible()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " - symb layers:"
        + this.symbolisedLayers.size();
  }

}
