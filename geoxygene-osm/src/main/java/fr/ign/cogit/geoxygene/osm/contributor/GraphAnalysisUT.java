package fr.ign.cogit.geoxygene.osm.contributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class GraphAnalysisUT {
	public static void main(String[] args) {
		/*
		 * Construction des 4 graphes
		 */
		DefaultWeightedEdge e;
		// Graphe de coédition
		System.out.println("****** GRAPHE DE COEDITION *****");
		SimpleWeightedGraph<Long, DefaultWeightedEdge> coeditionG = new SimpleWeightedGraph<Long, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		coeditionG.addVertex(Long.valueOf(1));
		coeditionG.addVertex(Long.valueOf(2));
		coeditionG.addVertex(Long.valueOf(3));
		coeditionG.addVertex(Long.valueOf(4));
		coeditionG.addVertex(Long.valueOf(5));

		e = coeditionG.addEdge(Long.valueOf(1), Long.valueOf(2));
		coeditionG.setEdgeWeight(e, 6);
		e = coeditionG.addEdge(Long.valueOf(1), Long.valueOf(3));
		coeditionG.setEdgeWeight(e, 6);
		e = coeditionG.addEdge(Long.valueOf(1), Long.valueOf(4));
		coeditionG.setEdgeWeight(e, 3);
		e = coeditionG.addEdge(Long.valueOf(1), Long.valueOf(5));
		coeditionG.setEdgeWeight(e, 1);

		e = coeditionG.addEdge(Long.valueOf(2), Long.valueOf(3));
		coeditionG.setEdgeWeight(e, 3);
		e = coeditionG.addEdge(Long.valueOf(2), Long.valueOf(4));
		coeditionG.setEdgeWeight(e, 1);
		e = coeditionG.addEdge(Long.valueOf(2), Long.valueOf(5));
		coeditionG.setEdgeWeight(e, 2);

		e = coeditionG.addEdge(Long.valueOf(3), Long.valueOf(5));
		coeditionG.setEdgeWeight(e, 3);

		System.out.println("degré 1 " + coeditionG.degreeOf(Long.valueOf(1)));
		System.out.println("degré 2 " + coeditionG.degreeOf(Long.valueOf(2)));
		System.out.println("degré 3 " + coeditionG.degreeOf(Long.valueOf(3)));
		System.out.println("degré 4 " + coeditionG.degreeOf(Long.valueOf(4)));
		System.out.println("degré 5 " + coeditionG.degreeOf(Long.valueOf(5)));

		// Graphe de collaboration width
		System.out.println("****** GRAPHE DE COLLAB WIDTH *****");
		SimpleWeightedGraph<Long, DefaultWeightedEdge> collabwG = new SimpleWeightedGraph<Long, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		collabwG.addVertex(Long.valueOf(1));
		collabwG.addVertex(Long.valueOf(2));
		collabwG.addVertex(Long.valueOf(3));
		collabwG.addVertex(Long.valueOf(4));
		collabwG.addVertex(Long.valueOf(5));

		e = collabwG.addEdge(Long.valueOf(1), Long.valueOf(2));
		collabwG.setEdgeWeight(e, 3);
		e = collabwG.addEdge(Long.valueOf(1), Long.valueOf(3));
		collabwG.setEdgeWeight(e, 3);
		e = collabwG.addEdge(Long.valueOf(1), Long.valueOf(4));
		collabwG.setEdgeWeight(e, 2);
		e = collabwG.addEdge(Long.valueOf(1), Long.valueOf(5));
		collabwG.setEdgeWeight(e, 1);

		e = collabwG.addEdge(Long.valueOf(2), Long.valueOf(3));
		collabwG.setEdgeWeight(e, 3);
		e = collabwG.addEdge(Long.valueOf(2), Long.valueOf(4));
		collabwG.setEdgeWeight(e, 1);
		e = collabwG.addEdge(Long.valueOf(2), Long.valueOf(5));
		collabwG.setEdgeWeight(e, 1);

		e = collabwG.addEdge(Long.valueOf(3), Long.valueOf(4));
		collabwG.setEdgeWeight(e, 1);
		e = collabwG.addEdge(Long.valueOf(3), Long.valueOf(5));
		collabwG.setEdgeWeight(e, 2);

		System.out.println("degré 1 " + collabwG.degreeOf(Long.valueOf(1)));
		System.out.println("degré 2 " + collabwG.degreeOf(Long.valueOf(2)));
		System.out.println("degré 3 " + collabwG.degreeOf(Long.valueOf(3)));
		System.out.println("degré 4 " + collabwG.degreeOf(Long.valueOf(4)));
		System.out.println("degré 5 " + collabwG.degreeOf(Long.valueOf(5)));

		// Graphe de collaboration depth
		System.out.println("****** GRAPHE DE COLLAB DEPTH *****");
		SimpleWeightedGraph<Long, DefaultWeightedEdge> collabdG = new SimpleWeightedGraph<Long, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		collabdG.addVertex(Long.valueOf(1));
		collabdG.addVertex(Long.valueOf(2));
		collabdG.addVertex(Long.valueOf(3));
		collabdG.addVertex(Long.valueOf(4));
		collabdG.addVertex(Long.valueOf(5));

		e = collabdG.addEdge(Long.valueOf(1), Long.valueOf(2));
		collabdG.setEdgeWeight(e, 3);
		e = collabdG.addEdge(Long.valueOf(1), Long.valueOf(3));
		collabdG.setEdgeWeight(e, 3);
		e = collabdG.addEdge(Long.valueOf(1), Long.valueOf(4));
		collabdG.setEdgeWeight(e, 3);
		e = collabdG.addEdge(Long.valueOf(1), Long.valueOf(5));
		collabdG.setEdgeWeight(e, 2);

		e = collabdG.addEdge(Long.valueOf(2), Long.valueOf(3));
		collabdG.setEdgeWeight(e, 2);
		e = collabdG.addEdge(Long.valueOf(2), Long.valueOf(4));
		collabdG.setEdgeWeight(e, 2);
		e = collabdG.addEdge(Long.valueOf(2), Long.valueOf(5));
		collabdG.setEdgeWeight(e, 3);

		e = collabdG.addEdge(Long.valueOf(3), Long.valueOf(4));
		collabdG.setEdgeWeight(e, 1);
		e = collabdG.addEdge(Long.valueOf(3), Long.valueOf(5));
		collabdG.setEdgeWeight(e, 3);

		e = collabdG.addEdge(Long.valueOf(4), Long.valueOf(5));
		collabdG.setEdgeWeight(e, 1);

		System.out.println("degré 1 " + collabdG.degreeOf(Long.valueOf(1)));
		System.out.println("degré 2 " + collabdG.degreeOf(Long.valueOf(2)));
		System.out.println("degré 3 " + collabdG.degreeOf(Long.valueOf(3)));
		System.out.println("degré 4 " + collabdG.degreeOf(Long.valueOf(4)));
		System.out.println("degré 5 " + collabdG.degreeOf(Long.valueOf(5)));

		// Graphe de cocontribution
		System.out.println("****** GRAPHE DE COCONTRIBUTION *****");
		SimpleWeightedGraph<Long, DefaultWeightedEdge> cocontribG = new SimpleWeightedGraph<Long, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		cocontribG.addVertex(Long.valueOf(1));
		cocontribG.addVertex(Long.valueOf(2));
		cocontribG.addVertex(Long.valueOf(3));
		cocontribG.addVertex(Long.valueOf(4));
		cocontribG.addVertex(Long.valueOf(5));

		e = cocontribG.addEdge(Long.valueOf(1), Long.valueOf(2));
		cocontribG.setEdgeWeight(e, 3);
		e = cocontribG.addEdge(Long.valueOf(1), Long.valueOf(3));
		cocontribG.setEdgeWeight(e, 3);
		e = cocontribG.addEdge(Long.valueOf(1), Long.valueOf(4));
		cocontribG.setEdgeWeight(e, 2);
		e = cocontribG.addEdge(Long.valueOf(1), Long.valueOf(5));
		cocontribG.setEdgeWeight(e, 1);

		e = cocontribG.addEdge(Long.valueOf(2), Long.valueOf(3));
		cocontribG.setEdgeWeight(e, 3);
		e = cocontribG.addEdge(Long.valueOf(2), Long.valueOf(4));
		cocontribG.setEdgeWeight(e, 1);
		e = cocontribG.addEdge(Long.valueOf(2), Long.valueOf(5));
		cocontribG.setEdgeWeight(e, 1);

		e = cocontribG.addEdge(Long.valueOf(3), Long.valueOf(4));
		cocontribG.setEdgeWeight(e, 1);
		e = cocontribG.addEdge(Long.valueOf(3), Long.valueOf(5));
		cocontribG.setEdgeWeight(e, 2);

		System.out.println("degré 1 " + cocontribG.degreeOf(Long.valueOf(1)));
		System.out.println("degré 2 " + cocontribG.degreeOf(Long.valueOf(2)));
		System.out.println("degré 3 " + cocontribG.degreeOf(Long.valueOf(3)));
		System.out.println("degré 4 " + cocontribG.degreeOf(Long.valueOf(4)));
		System.out.println("degré 5 " + cocontribG.degreeOf(Long.valueOf(5)));

		/*
		 * Test overlappingDegree function
		 */
		HashMap<Long, Long> overlapList = GraphAnalysis.overlappingDegree(coeditionG, collabwG, collabdG, cocontribG);
		/*
		 * Test participationCoefficient function
		 */
		HashMap<Long, Double> participationList = GraphAnalysis.participationCoefficient(coeditionG, collabwG, collabdG,
				cocontribG, overlapList);

		/*
		 * Test createSimpleAdjacencyMatrix function
		 */
		System.out.println("******* TEST createSimpleAdjacencyMatrix FUNCTION ******");
		double[][] adj1 = GraphAnalysis.createSimpleAdjacencyMatrix(collabdG, 1);

		double[][] adjcoedit = { { 0, 1, 1, 1, 0 }, { 1, 0, 1, 0, 1 }, { 1, 1, 0, 0, 1 }, { 1, 0, 0, 0, 0 },
				{ 0, 1, 1, 0, 0 } };
		double[][] adjcollabw = { { 0, 1, 1, 1, 0 }, { 1, 0, 1, 0, 0 }, { 1, 1, 0, 0, 1 }, { 1, 0, 0, 0, 0 },
				{ 0, 0, 1, 0, 0 } };
		double[][] adjcollabd = { { 0, 1, 1, 1, 1 }, { 1, 0, 1, 1, 1 }, { 1, 1, 0, 0, 1 }, { 1, 1, 0, 0, 0 },
				{ 1, 1, 1, 0, 0 } };
		double[][] adjcocontrib = { { 0, 1, 1, 1, 1 }, { 1, 0, 1, 1, 1 }, { 1, 1, 0, 1, 1 }, { 1, 1, 1, 0, 0 },
				{ 1, 1, 1, 0, 0 } };

		System.out.println("égalité = " + Arrays.deepEquals(adj1, adjcollabd));
		// Matrice d'interaction
		double[][] intercollabd = GraphAnalysis.createInteractionMatrix(collabdG);
		for (int i = 0; i < intercollabd.length; i++) {
			System.out.println(intercollabd[i][0] + " " + intercollabd[i][1] + " " + intercollabd[i][2] + " "
					+ intercollabd[i][3] + " " + intercollabd[i][4]);
		}
		/*
		 * Test createMultiplexAdjacencyMatrix function
		 */
		System.out.println("******* TEST createMultiplexAdjacencyMatrix FUNCTION ******");
		ArrayList<double[][]> adjM = GraphAnalysis.createMultiplexAdjacencyMatrix(coeditionG, collabwG, collabdG,
				cocontribG);
		System.out.println("Matrices d'adjacence coédition, égalité = " + Arrays.deepEquals(adjM.get(0), adjcoedit));
		System.out.println("Matrices d'adjacence collabw, égalité = " + Arrays.deepEquals(adjM.get(1), adjcollabw));
		System.out.println("Matrices d'adjacence collabd, égalité = " + Arrays.deepEquals(adjM.get(2), adjcollabd));
		System.out.println("Matrices d'adjacence cocontrib, égalité = " + Arrays.deepEquals(adjM.get(3), adjcocontrib));

		/*
		 * Test clustering coefficient functions
		 */
		HashMap<Long, Integer> contributorIndex = GraphAnalysis.contributorIndex(coeditionG);
		ArrayList<SimpleWeightedGraph<Long, DefaultWeightedEdge>> multiplex = new ArrayList<SimpleWeightedGraph<Long, DefaultWeightedEdge>>();
		multiplex.add(coeditionG);
		multiplex.add(collabwG);
		multiplex.add(collabdG);
		multiplex.add(cocontribG);
		HashMap<Long, Double> coeffList1 = GraphAnalysis.clusteringCoefficient1(adjM, contributorIndex);
		HashMap<Long, Double> coeffList2 = GraphAnalysis.clusteringCoefficient2(adjM, contributorIndex);
		System.out.println("Coefficients de clustering 1 : ");
		for (long i : coeffList1.keySet()) {
			System.out.println(coeffList1.get(i));
		}

		System.out.println("Coefficients de clustering 2 : ");
		for (long i : coeffList2.keySet()) {
			System.out.println(coeffList2.get(i));
		}

		/*
		 * Test participationCoefficientMultiplex function
		 */
		GraphAnalysis.participationCoefficientMultiplex(adjM, contributorIndex);

	}

}
