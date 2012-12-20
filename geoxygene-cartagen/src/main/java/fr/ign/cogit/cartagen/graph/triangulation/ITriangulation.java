/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.graph.triangulation;

import java.util.Collection;
import java.util.List;

import fr.ign.cogit.cartagen.graph.IGraph;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;

public interface ITriangulation extends IGraph {

  /**
   * @return The points of the triangulation
   */
  public List<TriangulationPoint> getPoints();

  /**
   * @return The segments of the triangulation
   */
  public Collection<TriangulationSegment> getSegments();

  /**
   * @return The triangles of the triangulation
   */
  public Collection<TriangulationTriangle> getTriangles();

  public IPopulation<Arc> getPopVoronoiEdges();

  public IPopulation<Noeud> getPopVoronoiVertices();

  public IPopulation<Face> getPopVoronoiFaces();

  public CarteTopo getVoronoiDiagram();
}
