package fr.ign.cogit.geoxygene.appli.render.primitive;

import java.util.Collection;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;

/**
 * @author JeT
 * Defines how primitives are rendered on the screen.
 */
public interface PrimitiveRenderer {

  /**
   * Retrieve the current viewport
   */
  Viewport getViewport();

  /**
   * Set the renderer viewport. This method MUST be called before launching the render() method
   * @param viewport viewport associated with the rendering window
   */
  void setViewport(final Viewport viewport);

  /**
   * Add/set one primitive to the list of managed primitives
   * @param primitive primitive to add
   */
  void addPrimitive(DrawingPrimitive primitive);

  void setPrimitive(DrawingPrimitive primitive);

  /**
   * Add/set a collection of primitives to the list of managed primitives.
   * (it internally call addPrimitive successively)
   * @param primitive primitive collection to add
   */
  void addPrimitives(Collection<? extends DrawingPrimitive> primitives);

  void setPrimitives(Collection<? extends DrawingPrimitive> primitives);

  /**
   * Initialize the rendering process. It is called once before render() method
   * is iteratively called
   * @throws RenderingException
   */
  void initializeRendering() throws RenderingException;

  /**
   * Launch the rendering. viewport must have been set and primitives added
   * before calling this method.
   * @throws RenderingException
   */
  void render() throws RenderingException;

  /**
   * Finalize the rendering process. It is called once after render() method
   * has been iteratively called
   * @throws RenderingException
   */
  void finalizeRendering() throws RenderingException;

  /**
   * remove all managed primitives
   */
  void removeAllPrimitives();

  /**
   * get the collection of managed primitives
   */
  Collection<DrawingPrimitive> getPrimitives();

}
