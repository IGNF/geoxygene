package fr.ign.cogit.geoxygene.osm.contributor;

import java.util.HashMap;

import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import fr.ign.cogit.geoxygene.osm.importexport.OSMObject;
import fr.ign.cogit.geoxygene.osm.importexport.metrics.ContributorAssessment;
import fr.ign.cogit.geoxygene.osm.importexport.metrics.IntrinsicAssessment;
import fr.ign.cogit.geoxygene.osm.importexport.postgis.LoadFromPostGIS;

public class LabeledEdges {
	private static final String cocontribution = "cocontribution";
	private static final String coedition = "coedition";
	private static final String collaboration = "collaboration";
	private static final String utilization = "utilization";

	public static DirectedWeightedMultigraph<Long, RelationshipEdge> addGraph(
			DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> g, String label,
			DirectedWeightedMultigraph<Long, RelationshipEdge> graph) {
		System.out.println("Add Graph");
		for (Long vertex : g.vertexSet()) {
			graph.addVertex(vertex);
		}
		for (DefaultWeightedEdge e : g.edgeSet()) {
			graph.addEdge(g.getEdgeSource(e), g.getEdgeTarget(e),
					new RelationshipEdge<Long>(g.getEdgeSource(e), g.getEdgeTarget(e), label));
			RelationshipEdge<Long> edge = graph.getEdge(g.getEdgeSource(e), g.getEdgeTarget(e));
			graph.setEdgeWeight(edge, g.getEdgeWeight(e));
			System.out.println("Edge added");
		}
		return graph;
	}

	public static void main(String[] args) throws Exception {
		DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge> g = new DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		DirectedWeightedMultigraph<Long, RelationshipEdge> graph = new DirectedWeightedMultigraph<Long, RelationshipEdge>(
				new ClassBasedEdgeFactory<Long, RelationshipEdge>(RelationshipEdge.class));
		LoadFromPostGIS loader = new LoadFromPostGIS("localhost", "5432", "paris", "postgres", "postgres");
		Double[] bbox = { 2.3322, 48.8489, 2.3634, 48.8627 };
		String[] timespan = { "2014-01-01", "2014-01-02" };
		loader.getEvolutionVisibleNode(bbox, timespan);
		// loader.selectWays(bbox, timespan);
		HashMap<Long, OSMObject> nodeOSMObjects = IntrinsicAssessment.nodeContributionSummary(loader.myJavaObjects);
		// HashMap<Long, OSMObject> wayOSMObjects =
		// IntrinsicAssessment.wayContributionSummary();
		HashMap<Long, OSMContributor> myOSMContributors = ContributorAssessment
				.contributorSummary(loader.myJavaObjects);
		DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> coContributionGraph = SocialGraph
				.createCoEditionGraph(nodeOSMObjects, myOSMContributors);
		graph = addGraph(coContributionGraph, "cocontribution", graph);

		for (RelationshipEdge<Long> relEdge : graph.edgeSet()) {
			System.out.println(
					"V1: " + relEdge.getV1() + " - V2: " + relEdge.getV2() + " poids: " + graph.getEdgeWeight(relEdge));
		}
	}

	public static class RelationshipEdge<V> extends DefaultEdge {
		private V v1;
		private V v2;
		private String label;

		public RelationshipEdge(V v1, V v2, String label) {
			this.v1 = v1;
			this.v2 = v2;
			this.label = label;
		}

		public V getV1() {
			return v1;
		}

		public V getV2() {
			return v2;
		}

		public String toString() {
			return label;
		}
	}

}
