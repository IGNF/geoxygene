package fr.ign.cogit.geoxygene.osm.contributor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import au.com.bytecode.opencsv.CSVWriter;

public class GraphAnalysis {
	/**
	 * converts a directed weighted graph into a simple weighted graph by
	 * keeping the maximum weight of 2 parallel edges
	 */
	public static SimpleWeightedGraph<Long, DefaultWeightedEdge> directedGraph2simpleGraph(
			DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> directedG) {
		SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG = new SimpleWeightedGraph<Long, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		// Parcourt les sommets du graphe orienté et les copie dans le graphe
		// simple
		for (Long v : directedG.vertexSet()) {
			simpleG.addVertex(v);
		}
		// Parcourt l'ensemble des arcs du graphe orienté
		for (DefaultWeightedEdge e : directedG.edgeSet()) {
			Long nodeIni = directedG.getEdgeSource(e);
			Long nodeFin = directedG.getEdgeTarget(e);
			double w = directedG.getEdgeWeight(e);
			if (!simpleG.containsEdge(nodeIni, nodeFin)) {
				simpleG.addEdge(nodeIni, nodeFin, e);
			} else {
				// On prend le poids max de deux arcs parallèles
				DefaultWeightedEdge e1 = simpleG.getEdge(nodeFin, nodeIni);
				if (simpleG.getEdgeWeight(e1) < w)
					simpleG.setEdgeWeight(e1, w);
			}
		}
		System.out.println("Graphe simple créé : " + simpleG.vertexSet().size() + " sommets et "
				+ simpleG.edgeSet().size() + " arcs");
		// for (DefaultWeightedEdge e : simpleG.edgeSet()) {
		// if (simpleG.getEdgeSource(e) == (long) 210173)
		// System.out.println(
		// "Source 210173 - Target " + simpleG.getEdgeTarget(e) + " - Poids " +
		// simpleG.getEdgeWeight(e));
		// }
		return simpleG;
	}

	public static HashMap<Long, Long> overlappingDegree(SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG1,
			SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG2,
			SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG3,
			SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG4) {
		HashMap<Long, Long> overlappingDegList = new HashMap<Long, Long>();
		for (Long v : simpleG1.vertexSet()) {
			Long overlap = (long) (simpleG1.degreeOf(v) + simpleG2.degreeOf(v) + simpleG3.degreeOf(v)
					+ simpleG4.degreeOf(v));
			overlappingDegList.put(v, overlap);
			System.out.println("Utilisateur : " + v + " - overlapping degree : " + overlap);
		}
		return overlappingDegList;
	}

	public static HashMap<Long, Double> participationCoefficientMultiplex(
			ArrayList<double[][]> multiplexAdjacencyMatrix, HashMap<Long, Integer> contributorIndex) {
		// nbLayer/(nbLayer-1)
		Double m = Double.valueOf(multiplexAdjacencyMatrix.size())
				/ Double.valueOf(multiplexAdjacencyMatrix.size() - 1);
		HashMap<Long, Double> participationList = new HashMap<Long, Double>();
		for (Long v : contributorIndex.keySet()) {
			double sum = 0;
			// Overlapping degree
			double overlap = 0;
			for (int alpha = 0; alpha < multiplexAdjacencyMatrix.size(); alpha++) {
				overlap += layerDegree(alpha, contributorIndex.get(v), multiplexAdjacencyMatrix);
			}
			if (overlap == 0)
				continue;
			// Degree in a layer
			double degree = 0;
			for (int alpha = 0; alpha < multiplexAdjacencyMatrix.size(); alpha++) {
				degree = layerDegree(alpha, contributorIndex.get(v), multiplexAdjacencyMatrix);
				sum += (degree / overlap) * (degree / overlap);
			}
			double pcoeff = m * (1 - sum);
			participationList.put(v, pcoeff);
			System.out.println("Utilisateur : " + v + " - participation coeff : " + (double) pcoeff);

		}
		return participationList;
	}

	public static HashMap<Long, Double> participationCoefficient(
			SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG1,
			SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG2,
			SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG3,
			SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG4, HashMap<Long, Long> overlappingDegList) {
		// nbLayer/(nbLayer-1)
		Double m = Double.valueOf(4) / Double.valueOf(3);
		HashMap<Long, Double> participationList = new HashMap<Long, Double>();
		for (Long v : simpleG1.vertexSet()) {
			Double overlap = Double.valueOf(overlappingDegList.get(v));
			if (overlap == 0)
				continue;
			Double m1 = Double.valueOf(simpleG1.degreeOf(v)) / overlap;
			Double m2 = Double.valueOf(simpleG2.degreeOf(v)) / overlap;
			Double m3 = Double.valueOf(simpleG3.degreeOf(v)) / overlap;
			Double m4 = Double.valueOf(simpleG4.degreeOf(v)) / overlap;
			Double sum = m1 * m1 + m2 * m2 + m3 * m3 + m4 * m4;
			Double pcoeff = m * (1 - sum);
			participationList.put(v, pcoeff);
			// System.out.println("Utilisateur : " + v + " - participation coeff
			// : " + (double) pcoeff);
		}
		return participationList;
	}

	/**
	 * creates the adjacency matrix of a simple weighted graph
	 **/
	public static double[][] createSimpleAdjacencyMatrix(SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG,
			double threshold) {
		HashMap<Long, Integer> contributorIndex = contributorIndex(simpleG);
		// Crée une matrice de taille nbContributeurs x nbContributeurs
		// initialisés à zéro par défaut
		double[][] adj = new double[simpleG.vertexSet().size()][simpleG.vertexSet().size()];
		for (DefaultWeightedEdge e : simpleG.edgeSet()) {
			Long nodeI = simpleG.getEdgeSource(e);
			Long nodeF = simpleG.getEdgeTarget(e);
			double w = simpleG.getEdgeWeight(e);
			// Remplit la matrice symétrique
			int j = contributorIndex.get(nodeI);
			int k = contributorIndex.get(nodeF);
			if (w >= threshold) {
				adj[j][k] = 1;
				adj[k][j] = 1;
			}
		}
		return adj;
	}

	public static double[][] createInteractionMatrix(SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG) {
		HashMap<Long, Integer> contributorIndex = new HashMap<Long, Integer>();
		int i = 0;
		for (Long v : simpleG.vertexSet()) {
			contributorIndex.put(v, i);
			// System.out.println("Ajout de l'utilisateur " + v + " indice " +
			// i);
			i++;
		}
		// Crée une matrice de taille nbContributeurs x nbContributeurs
		// initialisés à zéro par défaut
		double[][] adj = new double[simpleG.vertexSet().size()][simpleG.vertexSet().size()];
		for (DefaultWeightedEdge e : simpleG.edgeSet()) {
			Long nodeI = simpleG.getEdgeSource(e);
			Long nodeF = simpleG.getEdgeTarget(e);
			double w = simpleG.getEdgeWeight(e);
			// Remplit la matrice symétrique
			int j = contributorIndex.get(nodeI);
			int k = contributorIndex.get(nodeF);
			adj[j][k] = w;
			adj[k][j] = w;
		}
		return adj;
	}

	/**
	 * creates the adjacency matrix of a multiplex system composed of 4 simple
	 * weighted graphs
	 **/
	public static ArrayList<double[][]> createMultiplexAdjacencyMatrix(
			SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG1,
			SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG2,
			SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG3,
			SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG4) {
		double[][] adjG1 = createSimpleAdjacencyMatrix(simpleG1, 1);
		double[][] adjG2 = createSimpleAdjacencyMatrix(simpleG2, 1);
		double[][] adjG3 = createSimpleAdjacencyMatrix(simpleG3, 1);
		double[][] adjG4 = createSimpleAdjacencyMatrix(simpleG4, 0);
		ArrayList<double[][]> adjMultiplex = new ArrayList<double[][]>();
		adjMultiplex.add(adjG1);
		adjMultiplex.add(adjG2);
		adjMultiplex.add(adjG3);
		adjMultiplex.add(adjG4);
		return adjMultiplex;

	}

	/**
	 * Indicate the index of each contributor in the adjacency matrix. HashMap
	 * key : contributor ID HashMap - value : adjacency matrix index
	 */
	public static HashMap<Long, Integer> contributorIndex(SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG) {
		HashMap<Long, Integer> contributorIndex = new HashMap<Long, Integer>();
		int i = 0;
		for (Long v : simpleG.vertexSet()) {
			contributorIndex.put(v, i);
			i++;
		}
		return contributorIndex;
	}

	public static double layerDegree(int layer, int v, ArrayList<double[][]> multiplexAdjacencyMatrix) {
		double deg = 0;
		double[][] adj = multiplexAdjacencyMatrix.get(layer);
		for (int col = 0; col < adj.length; col++) {
			deg += adj[v][col];
		}
		return deg;

	}

	public static HashMap<Long, Double> clusteringCoefficient1(ArrayList<double[][]> multiplexAdjacencyMatrix,
			// ArrayList<SimpleWeightedGraph<Long, DefaultWeightedEdge>>
			// multiplex,
			HashMap<Long, Integer> contributorIndex) {
		HashMap<Long, Double> clusteringCoeffList = new HashMap<Long, Double>();
		for (Long v : contributorIndex.keySet()) {
			// System.out.println("v " + v);
			double denom = 0;
			double num = 0;
			for (int alpha = 0; alpha < multiplexAdjacencyMatrix.size(); alpha++) {
				double degreeLayer = layerDegree(alpha, contributorIndex.get(v), multiplexAdjacencyMatrix);
				denom += degreeLayer * (degreeLayer - 1);
			}
			denom = denom * (multiplexAdjacencyMatrix.size() - 1);
			// System.out.println("denomTotal = " + denom);
			if (denom == 0)
				continue;
			// Parcourt les couches du système multiplexe
			for (int alpha = 0; alpha < multiplexAdjacencyMatrix.size(); alpha++) {
				for (int beta = 0; beta < multiplexAdjacencyMatrix.size(); beta++) {
					if (alpha == beta)
						continue;
					for (int j = 0; j < contributorIndex.values().size(); j++) {
						int i = contributorIndex.get(v);
						if (j == i)
							continue;
						for (int m = 0; m < contributorIndex.values().size(); m++) {
							if (m == i)
								continue;
							double[][] matalpha = multiplexAdjacencyMatrix.get(alpha);
							double[][] matbeta = multiplexAdjacencyMatrix.get(beta);
							double aij = matalpha[i][j];
							double ajm = matbeta[j][m];
							double ami = matalpha[m][i];
							num += aij * ajm * ami;
						}
					}
				}
				// System.out.println("Num = " + num);
			}
			// System.out.println("NumTotal = " + num);
			double ccoeff = num / denom;
			clusteringCoeffList.put(v, ccoeff);
		}
		return clusteringCoeffList;
	}

	public static HashMap<Long, Double> clusteringCoefficient2(ArrayList<double[][]> multiplexAdjacencyMatrix,
			// ArrayList<SimpleWeightedGraph<Long, DefaultWeightedEdge>>
			// multiplex,
			HashMap<Long, Integer> contributorIndex) {
		HashMap<Long, Double> clusteringCoeffList = new HashMap<Long, Double>();
		for (Long v : contributorIndex.keySet()) {
			// System.out.println("v " + v);
			double denom = 0;
			double num = 0;
			int i = contributorIndex.get(v);
			for (int alpha = 0; alpha < multiplexAdjacencyMatrix.size(); alpha++) {
				for (int beta = 0; beta < multiplexAdjacencyMatrix.size(); beta++) {
					if (alpha == beta)
						continue;
					for (int j = 0; j < contributorIndex.values().size(); j++) {
						if (j == i)
							continue;
						for (int m = 0; m < contributorIndex.values().size(); m++) {
							if (m == i)
								continue;
							double[][] matalpha = multiplexAdjacencyMatrix.get(alpha);
							double[][] matbeta = multiplexAdjacencyMatrix.get(beta);
							double aij = matalpha[i][j];
							double ami = matbeta[m][i];
							denom += aij * ami;
						}
					}
				}
			}
			denom = denom * (multiplexAdjacencyMatrix.size() - 2);
			// System.out.println("denomTotal = " + denom);
			if (denom == 0)
				continue;
			// Parcourt les couches du système multiplexe
			for (int alpha = 0; alpha < multiplexAdjacencyMatrix.size(); alpha++) {
				for (int beta = 0; beta < multiplexAdjacencyMatrix.size(); beta++) {
					if (alpha == beta)
						continue;
					for (int gamma = 0; gamma < multiplexAdjacencyMatrix.size(); gamma++) {
						if (gamma == alpha)
							continue;
						if (gamma == beta)
							continue;
						for (int j = 0; j < contributorIndex.values().size(); j++) {
							if (j == i)
								continue;
							for (int m = 0; m < contributorIndex.values().size(); m++) {
								if (m == i)
									continue;
								double[][] matalpha = multiplexAdjacencyMatrix.get(alpha);
								double[][] matbeta = multiplexAdjacencyMatrix.get(beta);
								double[][] matgamma = multiplexAdjacencyMatrix.get(gamma);
								double aij = matalpha[i][j];
								double ajm = matgamma[j][m];
								double ami = matbeta[m][i];
								num += aij * ajm * ami;
							}
						}

					}
				}
			}
			// System.out.println("numTotal = " + num);
			double ccoeff = num / denom;
			clusteringCoeffList.put(v, ccoeff);
		}
		return clusteringCoeffList;
	}

	public static void writeIndicators(HashMap<Long, Integer> contributorIndex, ArrayList<double[][]> adjM,
			HashMap<Long, Double> participationList, HashMap<Long, Double> coeffList1, HashMap<Long, Double> coeffList2,
			String filename) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(filename), ';');
		// write header

		String[] line = new String[adjM.size() + 4];
		line[0] = "uid";
		for (int i = 1; i <= adjM.size(); i++) {
			line[i] = "degre" + i;
		}
		line[adjM.size() + 1] = "participation";
		line[adjM.size() + 2] = "ccluster1";
		line[adjM.size() + 3] = "ccluster2";
		// String[] line = new String[9];
		// line[0] = "uid";
		// line[1] = "degre1";
		// line[2] = "degre2";
		// line[3] = "degre3";
		// line[4] = "degre4";
		// line[5] = "degre5";
		// line[6] = "participation";
		// line[7] = "ccluster1";
		// line[8] = "ccluster2";
		writer.writeNext(line);
		for (Long uid : contributorIndex.keySet()) {
			// line = new String[9];
			line = new String[adjM.size() + 4];
			long uid_anonym = uid + (long) 111;
			line[0] = String.valueOf(uid_anonym);
			for (int i = 1; i <= adjM.size(); i++) {
				line[i] = String.valueOf(GraphAnalysis.layerDegree(i - 1, contributorIndex.get(uid), adjM));
			}
			// line[1] = String.valueOf(GraphAnalysis.layerDegree(0,
			// contributorIndex.get(uid), adjM));
			// line[2] = String.valueOf(GraphAnalysis.layerDegree(1,
			// contributorIndex.get(uid), adjM));
			// line[3] = String.valueOf(GraphAnalysis.layerDegree(2,
			// contributorIndex.get(uid), adjM));
			// line[4] = String.valueOf(GraphAnalysis.layerDegree(3,
			// contributorIndex.get(uid), adjM));
			// line[5] = String.valueOf(GraphAnalysis.layerDegree(4,
			// contributorIndex.get(uid), adjM));
			// line[6] = String.valueOf(participationList.get(uid));
			// line[7] = String.valueOf(coeffList1.get(uid));
			// line[8] = String.valueOf(coeffList2.get(uid));
			line[adjM.size() + 1] = String.valueOf(participationList.get(uid));
			line[adjM.size() + 2] = String.valueOf(coeffList1.get(uid));
			line[adjM.size() + 3] = String.valueOf(coeffList2.get(uid));
			writer.writeNext(line);
		}
		writer.close();
	}

	public static SimpleWeightedGraph<Long, DefaultWeightedEdge> monoplex(
			ArrayList<double[][]> multiplexAdjacencyMatrix, HashMap<Long, Integer> contributorIndex) {
		SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleG = new SimpleWeightedGraph<Long, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		// Parcourt les sommets du graphe orienté et les copie dans le graphe
		// simple
		for (Long v : contributorIndex.keySet()) {
			simpleG.addVertex(v);
		}

		// Add edges
		int nbContributors = multiplexAdjacencyMatrix.get(0).length;
		int dim = multiplexAdjacencyMatrix.size();
		System.out.println("nb contributors = " + nbContributors + "; nb dimensions = " + dim);

		for (int i = 0; i < nbContributors - 1; i++) {
			Long uid1 = (Long) contributorIndex.keySet().toArray()[i];
			for (int j = i + 1; j < nbContributors; j++) {
				Long uid2 = (Long) contributorIndex.keySet().toArray()[j];
				int sum = 0;

				for (int d = 0; d < dim; d++) {
					sum += multiplexAdjacencyMatrix.get(d)[i][j];
				}
				if (sum == 0)
					continue;

				DefaultWeightedEdge e = simpleG.addEdge(uid1, uid2);
				simpleG.setEdgeWeight(e, (double) sum);
			}

		}
		return simpleG;

	}

}