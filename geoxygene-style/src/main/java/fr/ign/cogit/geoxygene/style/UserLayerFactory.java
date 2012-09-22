/**
 * 
 */
package fr.ign.cogit.geoxygene.style;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;



/**
 * @author Julien Perret
 */
public class UserLayerFactory extends AbstractLayerFactory implements LayerFactory {
  private IFeatureCollection<? extends IFeature> collection;
  public void setCollection(IFeatureCollection<? extends IFeature> collection) {
    this.collection = collection;
  }
  @Override
  public Layer createLayer() {
    AbstractLayerFactory.logger.info("Create User Layer " + this.name);
    Layer layer = new UserLayer(this.collection, this.name);
    layer.getStyles().add(this.createStyle());
    return layer;
  }
}
