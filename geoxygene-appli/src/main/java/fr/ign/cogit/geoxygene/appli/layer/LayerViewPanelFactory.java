/**
 * 
 */
package fr.ign.cogit.geoxygene.appli.layer;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.ProjectFrame;

/** @author JeT This factory creates LayerViewPanel class instances */
public final class LayerViewPanelFactory {

  private static Logger logger = Logger.getLogger(LayerViewPanelFactory.class.getName());

  /** Layer view panel types */
  public enum RenderingType {
    AWT,
    LWJGL
  }

  private static RenderingType layerViewPanelType = RenderingType.AWT;

  /** private constructor */
  private LayerViewPanelFactory() {
    // private constructor for factories
  }

  /** @return the layerViewPanelType */
  public static RenderingType getLayerViewPanelType() {
    return layerViewPanelType;
  }

  /**
   * Change the factory created layer instance types
   * @param layerViewPanelType the layerViewPanelType to set
   */
  public static void setRenderingType(final RenderingType layerViewPanelType) {
    LayerViewPanelFactory.layerViewPanelType = layerViewPanelType;
  }

  /**
   * Create a new layer
   * @param projectFrame parent frame containing the newly created layer
   * @return newly created layer view
   */
  public static LayerViewPanel newLayerViewPanel(final ProjectFrame projectFrame) {
    switch (getLayerViewPanelType()) {
    case AWT:
      return newLayerViewAwtPanel(projectFrame);
    case LWJGL:
      return newLayerViewLwjglPanel(projectFrame);
    }
    logger.error("Unhandled layer type " + getLayerViewPanelType());
    return null;
  }

  /**
   * Create a new AWT layer
   * @param projectFrame parent frame containing the newly created layer
   * @return newly created layer view
   */

  private static LayerViewAwtPanel newLayerViewAwtPanel(final ProjectFrame projectFrame) {
    return new LayerViewAwtPanel(projectFrame);
  }

  /**
   * Create a new LwJGL layer
   * @param projectFrame parent frame containing the newly created layer
   * @return newly created layer view
   */
  private static LayerViewGLPanel newLayerViewLwjglPanel(final ProjectFrame projectFrame) {
    return new LayerViewLwjglPanel(projectFrame);
  }

}
