/**
 * 
 */
package fr.ign.cogit.geoxygene.style;


/**
 * @author Julien Perret
 */
public class NamedLayerFactory extends AbstractLayerFactory implements LayerFactory {
  @Override
  public Layer createLayer() {
    AbstractLayerFactory.logger.info("Create Named Layer " + this.name);
    Layer layer = new NamedLayer(this.model, this.name);
    layer.getStyles().add(this.createStyle());
    return layer;
  }
}
