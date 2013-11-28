package fr.ign.cogit.geoxygene.appli.render;

import java.util.Collection;

import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * RenderingManager is in charge of rendering images on request. It draws all
 * layers and displays a packing of them
 * @author Jérémie Turbet (Interface extracted from initial RenderingManager
 *         which is now MultithreadedRenderingManager)
 */
public interface RenderingManager {

  /**
   * Set the managed {@link LayerViewPanel} panel.
   * @param aLayerViewPanel the managed {@link LayerViewPanel} panel.
   */
  public abstract void setLayerViewPanel(LayerViewPanel aLayerViewPanel);

  /**
   * Return the collection of managed renderers.
   * @return the collection of managed renderers
   * @see AwtLayerRenderer
   */
  public abstract Collection<? extends LayerRenderer> getRenderers();

  /** Render all managed layers. */
  public abstract void renderAll();

  /**
   * Add a new layer to the manager and create the corresponding renderer.
   * @param layer the new layer to manage and render
   * @see Layer
   * @see AwtLayerRenderer
   */
  public abstract void addLayer(Layer layer);

  /**
   * Remove a layer from the manager.
   * @param layer the layer to remove
   * @see Layer
   * @see AwtLayerRenderer
   */
  public abstract void removeLayer(Layer layer);

  /**
   * Render a layer using the given renderer.
   * @param renderer the renderer to run
   * @see AwtLayerRenderer
   */
  public abstract void render(LayerRenderer renderer);

  /**
   * Render a layer.
   * @param layerName the name of the layer to run
   * @see AwtLayerRenderer
   */
  public abstract void render(String layerName);

  /**
   * Return the collection of managed layers in the same order they were added.
   * @return the collection of managed layers
   * @see Layer
   */
  public abstract Collection<Layer> getLayers();

  public abstract LayerRenderer getRenderer(Layer layer);

  public abstract void repaint();

  public abstract boolean isRendering();

  /** finish RenderingManager execution before quitting */

  void dispose();

  /** @return The selection renderer used to render the selected features */

  SelectionRenderer getSelectionRenderer();

  /**
   * Is the rendering manager handling the deletion of features when they are
   * rendered.
   * @return
   */
  public boolean isHandlingDeletion();

  public void setHandlingDeletion(boolean handlingDeletion);

}
