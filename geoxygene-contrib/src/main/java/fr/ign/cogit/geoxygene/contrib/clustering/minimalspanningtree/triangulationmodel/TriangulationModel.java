package fr.ign.cogit.geoxygene.contrib.clustering.minimalspanningtree.triangulationmodel;

import fr.ign.cogit.geoxygene.contrib.delaunay.Triangulation;
import fr.ign.cogit.geoxygene.feature.Population;

/**
 * Triangulation model to extend the Delaunay triangulation one in order to
 * allow the computation of a minimal spanning tree clustering algorithm based
 * on this kind of triangulation.
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 */
public class TriangulationModel extends Triangulation {

  /**
   * Default constructor.
   */
  public TriangulationModel() {
  }

  /**
   * Constructor.
   * 
   * @param name the name of triangulation model
   */
  public TriangulationModel(String name) {
    this.ojbConcreteClass = this.getClass().getName();
    this.setNom(name);
    this.setPersistant(false);
    this.getPopulations().clear();

    Population<EdgeSpecific> edges = new Population<EdgeSpecific>(false, "Edge", EdgeSpecific.class, true);
    this.addPopulation(edges);

    Population<NodeSpecific> nodes = new Population<NodeSpecific>(false, "Node", NodeSpecific.class, true);
    this.addPopulation(nodes);

    Population<TriangleSpecific> faces = new Population<TriangleSpecific>(false, "Face", TriangleSpecific.class, true);
    this.addPopulation(faces);
  }

}
