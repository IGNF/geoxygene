package fr.ign.cogit.geoxygene.osm.contributor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import au.com.bytecode.opencsv.CSVWriter;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.osm.importexport.OSMObject;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.OSMWay;
import fr.ign.cogit.geoxygene.osm.importexport.metrics.ContributorAssessment;
import fr.ign.cogit.geoxygene.osm.importexport.metrics.IntrinsicAssessment;
import fr.ign.cogit.geoxygene.osm.importexport.postgis.LoadFromPostGIS;

public class SocialGraph<V, E> {
	// private static Logger LOGGER = Logger.getLogger(SocialGraph.class);
	public static String host = "localhost";
	public static String port = "5432";
	public static String dbName = "iledelacite1";
	public static String dbUser = "postgres";
	public static String dbPwd = "postgres";
	private static double factor = 4;
	int edgeCount = 0;

	public static void main(String[] args) throws Exception {
		LoadFromPostGIS loader = new LoadFromPostGIS("localhost", "5432", "iledelacite1", "postgres", "postgres");
		List<Double> bbox = new ArrayList<Double>();
		//ile de la cite
        bbox.add(2.3398);
        bbox.add(48.8522);
        bbox.add(2.3527);
        bbox.add(48.8576);
        List<String> timespan = new ArrayList<String>();
        timespan.add("2010-01-01");
        timespan.add("2014-01-01");

		// Charge les nodes
		loader.selectNodes(bbox, timespan);
		HashMap<Long, OSMObject> nodeOSMObjects = IntrinsicAssessment.osmObjectsInit(loader.myJavaObjects, "OSMNode");

		// ordonne les contributions de chaque objet
		Iterator<Long> objectIDs = nodeOSMObjects.keySet().iterator();
		while (objectIDs.hasNext()) {
			long currentID = objectIDs.next();
			List<OSMResource> contributionList = nodeOSMObjects.get(currentID).getContributions();
			Collections.sort(contributionList, new Comparator<OSMResource>() {
				@Override
				public int compare(OSMResource r1, OSMResource r2) {
					return r1.getDate().compareTo(r2.getDate());
				}
			});
		}
		// Charge les ways
		loader.selectWays(bbox, timespan);
		HashMap<Long, OSMObject> wayOSMObjects = IntrinsicAssessment.osmObjectsInit(loader.myJavaObjects, "OSMWay");

		// Indicateurs contributeurs
		HashMap<Long, OSMContributor> myOSMContributors = ContributorAssessment
				.contributorSummary(loader.myJavaObjects);
		// ContributorAssessment.writeContributorSummary(myOSMContributors,
		// new File("contributeurs-details-qlatin-2010-2015.csv"));

		// Write contributions csv
		// Nodes
		// IntrinsicAssessment.writeContributionSummary(nodeOSMObjects, new
		// File("2010-2015-qlatin-node-summary.csv"));
		// IntrinsicAssessment.writeOSMObjectContributions(new
		// File("2010-2015-qlatin-node-contributions.csv"),
		// nodeOSMObjects);

		/**** Warning: graphs were built from OSM nodes only ***/
		// Building 4 graphs : coedition, collaboration width, collaboration
		// depth, cocontribution OR colocation
		DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> wcollabg = createCollaborationGraph(nodeOSMObjects,
				myOSMContributors, "width");
		 writeGraph2CSV(wcollabg, new
		 File("data/idc-widthCollabGraph-2010-2013.csv"));

		DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> dcollabg = createCollaborationGraph(nodeOSMObjects,
				myOSMContributors, "depth");
		 writeGraph2CSV(dcollabg, new
		 File("data/idc-depthCollabGraph_2010-2013.csv"));

//		DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> coeditg = createCoEditionGraph(nodeOSMObjects,
//				myOSMContributors);
		// writeGraph2CSV(coeditg, new
		// File("qlatin-coeditGraph_2010-2015.csv"));

//		SimpleWeightedGraph<Long, DefaultWeightedEdge> cocontribg = createCoContribGraph(nodeOSMObjects,
//				myOSMContributors);
		// writeSimpleWeightedGraph2CSV(cocontribg, new
		// File("qlatin-cocontribGraph_2010-2015.csv"));

		// SimpleWeightedGraph<Long, DefaultWeightedEdge> colocationg =
		// createCoLocationGraph(myOSMContributors, bbox,
		// timespan, 50);
		// writeSimpleWeightedGraph2CSV(colocationg, new
		// File("paris-centre_coLocationGraph_Delaunay50m_2013-2015.csv"));

		// Ordonne chronologiquement les contributions de type way
//		objectIDs = wayOSMObjects.keySet().iterator();
//		while (objectIDs.hasNext()) {
//			long currentID = objectIDs.next();
//			List<OSMResource> contributionList = wayOSMObjects.get(currentID).getContributions();
//			Collections.sort(contributionList, new Comparator<OSMResource>() {
//				//
//				@Override
//				public int compare(OSMResource r1, OSMResource r2) {
//					return r1.getDate().compareTo(r2.getDate());
//				}
//			});
//		}
		// Write Way contributions
		// IntrinsicAssessment.writeContributionSummary(wayOSMObjects, new
		// File("2013-2015-paris-centre-way-summary.csv"));
		// IntrinsicAssessment.writeOSMObjectContributions(new
		// File("2013-2015-paris-centre-way-contributions.csv"),
		// wayOSMObjects);

//		DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> useg = createUseGraph2(myOSMContributors, wayOSMObjects,
//				nodeOSMObjects, "2013-01-01");

		// writeGraph2CSV(useg, new File("qlatin-useGraph_2010-2015.csv"));

		// Converting into simple weighted graphs i.e. take maximum weight
		// between two parallel edges
//		SimpleWeightedGraph<Long, DefaultWeightedEdge> simplewcollabg = GraphAnalysis
//				.directedGraph2simpleGraph(wcollabg);
		// writeSimpleWeightedGraph2CSV(simplewcollabg, new
		// File("paris-centre_simplewcollabg_2013-2015.csv"));

//		SimpleWeightedGraph<Long, DefaultWeightedEdge> simpledcollabg = GraphAnalysis
//				.directedGraph2simpleGraph(dcollabg);
		// writeSimpleWeightedGraph2CSV(simpledcollabg, new
		// File("paris-centre_simpledcollabg_2013-2015.csv"));

//		SimpleWeightedGraph<Long, DefaultWeightedEdge> simplecoeditg = GraphAnalysis.directedGraph2simpleGraph(coeditg);
		// writeSimpleWeightedGraph2CSV(simplecoeditg, new
		// File("paris-centre_simplecoeditg_2013-2015.csv"));

//		SimpleWeightedGraph<Long, DefaultWeightedEdge> simpleuseg = GraphAnalysis.directedGraph2simpleGraph(useg);
		// writeSimpleWeightedGraph2CSV(simpleuseg, new
		// File("paris-centre_simpleuseg_2013-2015.csv"));

		// Creating adjacency matrices with thresholds:
		// Sans seuil
//		double[][] adjcoedit = GraphAnalysis.createSimpleAdjacencyMatrix(simplecoeditg, 0);
//		double[][] adjcollabw = GraphAnalysis.createSimpleAdjacencyMatrix(simplewcollabg, 0);
//		double[][] adjcollabd = GraphAnalysis.createSimpleAdjacencyMatrix(simpledcollabg, 0);
//		double[][] adjcocontrib = GraphAnalysis.createSimpleAdjacencyMatrix(cocontribg, 0);
		// double[][] adjcolocation =
		// GraphAnalysis.createSimpleAdjacencyMatrix(colocationg, 0);
//		double[][] adjuse = GraphAnalysis.createSimpleAdjacencyMatrix(simpleuseg, 0);

		// Building the multiplex system
//		HashMap<Long, Integer> contributorIndex = GraphAnalysis.contributorIndex(simplecoeditg);

		// Sans seuil 5dim
//		ArrayList<SimpleWeightedGraph<Long, DefaultWeightedEdge>> multiplex5dim = new ArrayList<SimpleWeightedGraph<Long, DefaultWeightedEdge>>();
//		multiplex5dim.add(simplecoeditg);
//		multiplex5dim.add(simplewcollabg);
//		multiplex5dim.add(simpledcollabg);
//		multiplex5dim.add(cocontribg);
		// multiplex5dim.add(colocationg);
//		multiplex5dim.add(simpleuseg);

//		ArrayList<double[][]> adjM5 = new ArrayList<double[][]>();
//		adjM5.add(adjcoedit);
//		adjM5.add(adjcollabw);
//		adjM5.add(adjcollabd);
		// adjM5.add(adjcolocation);
//		adjM5.add(adjuse);
		// Graphe agrégé sans seuil 5dim
		// SimpleWeightedGraph<Long, DefaultWeightedEdge> aggregatedGraph =
		// GraphAnalysis.monoplex(adjM5,
		// contributorIndex);
		// writeSimpleWeightedGraph2CSV(aggregatedGraph, new
		// File("paris-centre-2010-2015-aggregated_graph_5dim.csv"));
		// Sans seuil
		// HashMap<Long, Double> coeffList1 =
		// GraphAnalysis.clusteringCoefficient1(adjM5, contributorIndex);
		// HashMap<Long, Double> coeffList2 =
		// GraphAnalysis.clusteringCoefficient2(adjM5, contributorIndex);
		// HashMap<Long, Double> participationList =
		// GraphAnalysis.participationCoefficientMultiplex(adjM5,
		// contributorIndex);
		// GraphAnalysis.writeIndicators(contributorIndex, adjM5,
		// participationList, coeffList1, coeffList2,
		// "indicateurs_paris-centre_2013-2015_multiplex_2dim_coedit_use.csv");

		// Sans seuil 4dim
		// ArrayList<double[][]> adjM4 = new ArrayList<double[][]>();
		// adjM4.add(adjcoedit);
		// adjM4.add(adjcollabw);
		// adjM4.add(adjcollabd);
		// adjM4.add(adjuse);
		// Graphe agrégé sans seuil 5dim
		// SimpleWeightedGraph<Long, DefaultWeightedEdge> aggregatedGraph4dim =
		// GraphAnalysis.monoplex(adjM4,
		// contributorIndex);
		// writeSimpleWeightedGraph2CSV(aggregatedGraph4dim, new
		// File("paris-centre_aggregated_graph_4dim.csv"));
		// // Sans seuil
		// HashMap<Long, Double> coeffList1_4dim =
		// GraphAnalysis.clusteringCoefficient1(adjM4, contributorIndex);
		// HashMap<Long, Double> coeffList2_4dim =
		// GraphAnalysis.clusteringCoefficient2(adjM4, contributorIndex);
		// HashMap<Long, Double> participationList_4dim =
		// GraphAnalysis.participationCoefficientMultiplex(adjM4,
		// contributorIndex);
		// GraphAnalysis.writeIndicators(contributorIndex, adjM4,
		// participationList_4dim, coeffList1_4dim, coeffList2_4dim,
		// "indicateurs_paris-centre_2013-2015_multiplex_4dim.csv");

		// Avec seuil
		// Co-edition graph : threshold = 3
		// Collaboration width : threshold = 3
		// Collaboration depth : threshold = 2
//		double[][] adjcoedit3 = GraphAnalysis.createSimpleAdjacencyMatrix(simplecoeditg, (double) 3);
//		double[][] adjcollabw2 = GraphAnalysis.createSimpleAdjacencyMatrix(simplewcollabg, (double) 3);
//		double[][] adjcollabd2 = GraphAnalysis.createSimpleAdjacencyMatrix(simpledcollabg, (double) 2);
//		ArrayList<double[][]> adjM5seuil = new ArrayList<double[][]>();
//		adjM5seuil.add(adjcoedit3);
//		adjM5seuil.add(adjcollabw2);
//		adjM5seuil.add(adjcollabd2);
//		adjM5seuil.add(adjcocontrib);
//		adjM5seuil.add(adjuse);
		// Graphe agrégé sans seuil 5dim
//		SimpleWeightedGraph<Long, DefaultWeightedEdge> aggregatedGraph5dimseuil = GraphAnalysis.monoplex(adjM5seuil,
//				contributorIndex);
//		writeSimpleWeightedGraph2CSV(aggregatedGraph5dimseuil, new File("paris-centre_aggregated_graph_5dim.csv"));
//		// // Sans seuil
//		HashMap<Long, Double> coeffList1_5dimseuil = GraphAnalysis.clusteringCoefficient1(adjM5seuil, contributorIndex);
//		HashMap<Long, Double> coeffList2_5dimseuil = GraphAnalysis.clusteringCoefficient2(adjM5seuil, contributorIndex);
//		HashMap<Long, Double> participationList_5dimseuil = GraphAnalysis.participationCoefficientMultiplex(adjM5seuil,
//				contributorIndex);
//		GraphAnalysis.writeIndicators(contributorIndex, adjM5seuil, participationList_5dimseuil, coeffList1_5dimseuil,
//				coeffList2_5dimseuil, "indicateurs_paris-centre_2013-2015_multiplex_5dim_seuil.csv");

		// ArrayList<double[][]> adjM4seuil = new ArrayList<double[][]>();
		// adjM4seuil.add(adjcoedit3);
		// adjM4seuil.add(adjcollabw2);
		// adjM4seuil.add(adjcollabd2);
		// adjM5seuil.add(adjuse);
		// Graphe agrégé sans seuil 5dim
		// SimpleWeightedGraph<Long, DefaultWeightedEdge>
		// aggregatedGraph4dimseuil = GraphAnalysis.monoplex(adjM4seuil,
		// contributorIndex);
		// writeSimpleWeightedGraph2CSV(aggregatedGraph4dimseuil,
		// new File("paris-centre_aggregated_graph_4dim_seuil.csv"));
		// // Sans seuil
		// HashMap<Long, Double> coeffList1_4dimseuil =
		// GraphAnalysis.clusteringCoefficient1(adjM4seuil, contributorIndex);
		// HashMap<Long, Double> coeffList2_4dimseuil =
		// GraphAnalysis.clusteringCoefficient2(adjM4seuil, contributorIndex);
		// HashMap<Long, Double> participationList_4dimseuil =
		// GraphAnalysis.participationCoefficientMultiplex(adjM4seuil,
		// contributorIndex);
		// GraphAnalysis.writeIndicators(contributorIndex, adjM5seuil,
		// participationList_4dimseuil, coeffList1_4dimseuil,
		// coeffList2_4dimseuil,
		// "indicateurs_paris-centre_2013-2015_multiplex_4dim_seuil.csv");

	}

	public static DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> createCoContributionGraph(
			HashMap<Long, OSMObject> myOSMObjects, HashMap<Long, OSMContributor> myContributors) throws IOException {
		DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> g = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		// Add vertices
		for (OSMContributor contributor : myContributors.values()) {
			g.addVertex((long) contributor.getId());
		}
		// Add edges
		for (OSMObject osmObject : myOSMObjects.values()) {
			int lastContributionRange = osmObject.getContributions().size() - 1;
			for (int i = lastContributionRange; i > 0; i--) {
				// Long nodeIni = myContributors.get((long)
				// osmObject.getContributions().get(i).getUid();
				Long nodeIni = (long) osmObject.getContributions().get(i).getUid();
				for (int j = i - 1; j >= 0; j--) {
					// OSMContributor nodeFin = myContributors.get((long)
					// osmObject.getContributions().get(j).getUid());
					Long nodeFin = (long) osmObject.getContributions().get(j).getUid();
//					System.out.println(g.containsEdge(nodeIni, nodeFin));
					if (nodeIni.equals(nodeFin))
						continue;
					if (!g.containsEdge(nodeIni, nodeFin) || !g.containsEdge(nodeFin, nodeIni)) {
						/* DefaultWeightedEdge e = (DefaultWeightedEdge) */g.addEdge(nodeIni, nodeFin);
						// g.setEdgeWeight(e, weight);
					} else {
						// DefaultWeightedEdge e = g.getEdge(nodeIni, nodeFin);
						// g.setEdgeWeight(e, g.getEdgeWeight(e) + weight);
					}
					// System.out.println("Ajout d'un arc entre " + nodeIni + "
					// et " + nodeFin);
					// w++;
				}
			}
		}
		return g;
	}

	public static SimpleWeightedGraph<Long, DefaultWeightedEdge> createCoContribGraph(
			HashMap<Long, OSMObject> myOSMObjects, HashMap<Long, OSMContributor> myContributors) throws IOException {
		SimpleWeightedGraph<Long, DefaultWeightedEdge> g = new SimpleWeightedGraph<Long, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		// SimpleWeightedGraph<Long, DefaultWeightedEdge> gseuil = new
		// SimpleWeightedGraph<Long, DefaultWeightedEdge>(
		// DefaultWeightedEdge.class);
		// Add vertices
		for (OSMContributor contributor : myContributors.values()) {
			g.addVertex((long) contributor.getId());
			// gseuil.addVertex((long) contributor.getId());
		}
		// Add edges
		for (OSMObject osmObject : myOSMObjects.values()) {
			// On se restreint à la collaboration sur les noeuds seulement pour
			// l'instant
			if (osmObject.getContributions().get(0).getGeom().getClass().getSimpleName().equals("OSMWay"))
				continue;
			int lastContributionRange = osmObject.getContributions().size() - 1;
			for (int i = lastContributionRange; i > 0; i--) {
				Long nodeIni = (long) osmObject.getContributions().get(i).getUid();
				for (int j = i - 1; j >= 0; j--) {
					Long nodeFin = (long) osmObject.getContributions().get(j).getUid();
					// System.out.println(" g.containsEdge(nodeIni, nodeFin) : "
					// + g.containsEdge(nodeIni, nodeFin));
					if (nodeIni.equals(nodeFin))
						continue;
					if (!g.containsEdge(nodeIni, nodeFin)) {
						DefaultWeightedEdge e = g.addEdge(nodeIni, nodeFin);
						g.setEdgeWeight(e, 1);
					} else {
						DefaultWeightedEdge e = g.getEdge(nodeIni, nodeFin);
						g.setEdgeWeight(e, g.getEdgeWeight(e) + 1);
					}
				}
			}
		}
		// Keep only edges which weight > 1
		// for (DefaultWeightedEdge e : g.edgeSet()) {
		// if (g.getEdgeWeight(e) < 2) {
		// gseuil.addEdge(g.getEdgeSource(e), g.getEdgeTarget(e));
		// gseuil.setEdgeWeight(e, g.getEdgeWeight(e));
		// }
		// }
		// return gseuil;
		return g;
	}

	public static DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> createCoEditionGraph(
			HashMap<Long, OSMObject> myOSMObjects, HashMap<Long, OSMContributor> myContributors) {
		DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> g = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		// Add vertices
		for (OSMContributor contributor : myContributors.values()) {
			g.addVertex((long) contributor.getId());
		}
		// Add edges
		for (OSMObject osmObject : myOSMObjects.values()) {
			// On se restreint à la collaboration sur les noeuds seulement pour
			// l'instant
			if (osmObject.getContributions().get(0).getGeom().getClass().getSimpleName().equals("OSMWay"))
				continue;
			int lastContributionRange = osmObject.getContributions().size() - 1;
			for (int i = lastContributionRange; i > 0; i--) {
				Long nodeIni = (long) osmObject.getContributions().get(i).getUid();
				Long nodeFin = (long) osmObject.getContributions().get(i - 1).getUid();
				boolean egalite = nodeIni.equals(nodeFin);
				// if (egalite)
				// continue;
				// else {
				if (!egalite) {
					if (!g.containsEdge(nodeIni, nodeFin)) {
						// g.addEdge(nodeIni, nodeFin);
						DefaultWeightedEdge e = g.addEdge(nodeIni, nodeFin);
						g.setEdgeWeight(e, 1);
					} else {
						DefaultWeightedEdge e = g.getEdge(nodeIni, nodeFin);
						g.setEdgeWeight(e, g.getEdgeWeight(e) + 1);
					}
				}
			}
		}
		return g;
	}

	/***
	 * collaborationType values : global, width, depth, combined
	 * 
	 ***/

	public static DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> createCollaborationGraph(
			HashMap<Long, OSMObject> myOSMObjects, HashMap<Long, OSMContributor> myContributors,
			String collaborationType) {
		DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> g = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);

		DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> subGraph;
		DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> osmObjectGraph;
		DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> sumEdgeGraph;
		// Add vertices
		for (OSMContributor contributor : myContributors.values()) {
			g.addVertex((long) contributor.getId());
		}
		int nbVertice = myContributors.size();
//		System.out.println("Nombre de sommets dans le graphe :" + nbVertice);

		// Add edges
		for (OSMObject osmObject : myOSMObjects.values()) {
			// On se restreint à la collaboration sur les noeuds seulement pour
			// l'instant
			if (osmObject.getContributions().get(0).getGeom().getClass().getSimpleName().equals("OSMWay"))
				continue;

			subGraph = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			osmObjectGraph = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			sumEdgeGraph = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(DefaultWeightedEdge.class);

//			System.out.println("OSMObject en cours :" + osmObject.getOsmId());
			// Récupère le nombre de versions de l'objet étudié
			int lastContributionRange = osmObject.getContributions().size() - 1;
			// System.out.println("lastContributionRange :" +
			// lastContributionRange);
			for (int i = lastContributionRange; i > 0; i--) {
				Long nodeIni = (long) osmObject.getContributions().get(i).getUid();
				// Ajout du noeud dans le graphe
				if (!subGraph.containsVertex(nodeIni))
					subGraph.addVertex(nodeIni);
				if (collaborationType.equalsIgnoreCase("depth") && !osmObjectGraph.containsVertex(nodeIni))
					osmObjectGraph.addVertex(nodeIni);
				if (collaborationType.equalsIgnoreCase("combined") && !osmObjectGraph.containsVertex(nodeIni))
					sumEdgeGraph.addVertex(nodeIni);

				// Cherche l'indice de l'avant dernière contribution du
				// contributeur courant
				int currentContributorPreviousContribution = 0;
				Long nodeFin = null;
				boolean egalite = false;
				int j = i - 1;
				while (!egalite && j > 0) {
//					System.out.println("j = " + j);
					nodeFin = (long) osmObject.getContributions().get(j).getUid();
					if (nodeIni.equals(nodeFin)) {
						currentContributorPreviousContribution = j;
						egalite = true;
					}
					j--;
				}
				for (int k = i - 1; k >= currentContributorPreviousContribution; k--) {
					nodeFin = (long) osmObject.getContributions().get(k).getUid();
					if (!nodeFin.equals(nodeIni)) {
						if (!subGraph.containsVertex(nodeFin))
							subGraph.addVertex(nodeFin);
						if (collaborationType.equalsIgnoreCase("combined") && !osmObjectGraph.containsVertex(nodeFin))
							sumEdgeGraph.addVertex(nodeFin);

						if (!subGraph.containsEdge(nodeIni, nodeFin)) {
							DefaultWeightedEdge e = (DefaultWeightedEdge) subGraph.addEdge(nodeIni, nodeFin);
							subGraph.setEdgeWeight(e, 1);
							if (collaborationType.equalsIgnoreCase("depth"))
								if (!osmObjectGraph.containsVertex(nodeFin))
									osmObjectGraph.addVertex(nodeFin);

						} else {
							if (collaborationType.equalsIgnoreCase("depth")) {
								DefaultWeightedEdge e = subGraph.getEdge(nodeIni, nodeFin);
								subGraph.setEdgeWeight(e, subGraph.getEdgeWeight(e) + 1);
							}
						}
					}
				}

				// for (int j = i - 1; j >= 0; j--) {
				// nodeFin = (long)
				// osmObject.getContributions().get(j).getUid();
				// if (nodeIni.equals(nodeFin)) {
				// currentContributorPreviousContribution = j;
				// break;
				// }
				// }
				// Parcours les éditions faites entre la dernière et l'avant
				// dernière précédente contribution du
				// contributeur courant
				// for (int k = i - 1; k >
				// currentContributorPreviousContribution; k--) {
				// nodeFin = (long)
				// osmObject.getContributions().get(k).getUid();
				// if (!subGraph.containsVertex(nodeFin))
				// subGraph.addVertex(nodeFin);
				// if (collaborationType.equalsIgnoreCase("combined") &&
				// !osmObjectGraph.containsVertex(nodeFin))
				// sumEdgeGraph.addVertex(nodeFin);
				//
				// if (!subGraph.containsEdge(nodeIni, nodeFin)) {
				// DefaultWeightedEdge e = (DefaultWeightedEdge)
				// subGraph.addEdge(nodeIni, nodeFin);
				// subGraph.setEdgeWeight(e, 1);
				// if (collaborationType.equalsIgnoreCase("depth"))
				// if (!osmObjectGraph.containsVertex(nodeFin))
				// osmObjectGraph.addVertex(nodeFin);
				//
				// } else {
				// if (collaborationType.equalsIgnoreCase("depth")) {
				// DefaultWeightedEdge e = subGraph.getEdge(nodeIni, nodeFin);
				// subGraph.setEdgeWeight(e, subGraph.getEdgeWeight(e) + 1);
				// }
				// }
				// }
				if (collaborationType.equalsIgnoreCase("global")) {
					mergeSubGraph(g, subGraph);
					subGraph = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(DefaultWeightedEdge.class);
				}
				if (collaborationType.equalsIgnoreCase("depth")) {
					mergeSubGraph(osmObjectGraph, subGraph);
					subGraph = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(DefaultWeightedEdge.class);
					getMaxEdge(g, osmObjectGraph);
					osmObjectGraph = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(
							DefaultWeightedEdge.class);
				}
				if (collaborationType.equalsIgnoreCase("combined")) {
					// On met à la puissance le total des interactions
					// comptabilisées dans le graphe g sur un même objet
					mergeSubGraph(sumEdgeGraph, subGraph);
					subGraph = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(DefaultWeightedEdge.class);
				}
			} // Fin de l'objet courant, on passe au suivant
			if (collaborationType.equalsIgnoreCase("width")) {
				mergeSubGraph(g, subGraph);
				subGraph = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			}
			if (collaborationType.equalsIgnoreCase("combined")) {
				for (DefaultWeightedEdge edge : sumEdgeGraph.edgeSet()) {
					g.setEdgeWeight(edge, Math.pow(sumEdgeGraph.getEdgeWeight(edge), factor));
				}
				mergeSubGraph(g, sumEdgeGraph);
				sumEdgeGraph = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			}
		} // fin de l'objet courant, on passe au suivant
		if (collaborationType.equalsIgnoreCase("combined")) {
			// On met sous la racine le poids total calculé dans le graphe final
			for (DefaultWeightedEdge edge : g.edgeSet()) {
				g.setEdgeWeight(edge, Math.pow(g.getEdgeWeight(edge), (double) 1 / factor));
			}
		}
		return g;
	}

	/** Creates a use graph from scratch **/
	public static DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> createUseGraph(Set<OSMResource> myJavaObjects,
			String dateMin, String dateMax) throws Exception {
		DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> g = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);

		List<Long> listTarget = new ArrayList<Long>();
		for (OSMResource resource : myJavaObjects) {
			Long uidway = (long) resource.getUid();
			// // Cherche si l'objet (node ou way) n'est pas membre d'une
			// relation
			// // Requete dans la table relationmember
			// List<Long> listOfidrel = getRelationMembersIdrel(resource);
			// if (!listOfidrel.isEmpty()) {
			// // Requete dans la table relation pour filtrer les relations qui
			// // sont postérieures à la date de leurs membres
			// List<Long> listOfRelUid = getRelationUid(resource, listOfidrel);
			// if (!listOfRelUid.isEmpty())
			// for (Long relUid : listOfRelUid)
			// if (relUid != uidway)
			// addEdgeUseGraph(g, relUid, uidway);
			// }
			// Dans le cas d'un way, cherche si les points sont des nodes
			// réutilisés
			if (resource.getGeom().getClass().getSimpleName().equals("OSMWay")) {
				listTarget = selectAuthorsOfUsedNodes(resource);
				for (Long targetID : listTarget) {
					if (targetID != uidway)
						addEdgeUseGraph(g, uidway, targetID);
				}
			}
		}
		return g;
	}

	/**
	 * Creates a use graph from nodes that have been loaded beforehand. Method
	 * to use if the graph composes a layer of a multiplex system
	 **/
	public static DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> createUseGraph2(
			HashMap<Long, OSMContributor> myContributors, HashMap<Long, OSMObject> wayOSMObjects,
			HashMap<Long, OSMObject> nodeOSMObjects, String tdeb) throws Exception {

		DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> g = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);

		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
		Date dateIni = null;
		try {
			dateIni = formatDate.parse(tdeb);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// Add vertices
		for (OSMContributor contributor : myContributors.values()) {
			g.addVertex((long) contributor.getId());
		}
		// List<Long> listTarget = new ArrayList<Long>();
		for (OSMObject objet : wayOSMObjects.values()) {
			int v = 0;
			if (objet.getContributions().get(v).getDate().before(dateIni)
					|| objet.getContributions().get(v).getDate().equals(dateIni))
				continue;
			for (OSMResource resource : objet.getContributions()) {
				// Get v-version of way's composition
				List<Long> currentComposition = objet.wayComposition.get(v);
				if (v == 0) {
					// parcourt OSMNodeObjects et trouve les noeuds qui
					// correspondent à la version du way
					edgeUseGraph(resource, currentComposition, nodeOSMObjects, g);
				} else {
					// If (v-1)-version of way's composition is the same of
					// v-version's : it means that v-version is not a geometric
					// edition : skip this one
					List<Long> previousComposition = objet.wayComposition.get(v - 1);
					if (currentComposition.equals(previousComposition))
						continue;
					// parcourt OSMNodeObjects et trouve les noeuds qui
					// correspondent à la version du way
					edgeUseGraph(resource, currentComposition, nodeOSMObjects, g);
				}
			}
		}
		return g;
	}

	public static void edgeUseGraph(OSMResource way, List<Long> wayComposition, HashMap<Long, OSMObject> nodeOSMObjects,
			DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> g) {
		for (Long nodeId : wayComposition) {
			OSMObject nodeObject = nodeOSMObjects.get(nodeId);
			// Look for the right node version that matches way input
			boolean before = true;
			int i = 0;
			if (nodeObject == null)
				// Cas où le way est composé d'un noeud qui n'a pas été chargé:
				// celui-ci a dû être supprimé après coup
				continue; // Ou interroger la base de données ?

			if (nodeObject.getContributions().size() > 0) {
				while (before && ((i + 1) < nodeObject.getContributions().size())) {
					if (nodeObject.getContributions().get(i).getDate().after(way.getDate()))
						before = false;
					else
						i++;
				}
				if (nodeObject.getContributions().get(i).getUid() != way.getUid())
					addEdgeUseGraph(g, (long) way.getUid(), (long) nodeObject.getContributions().get(i).getUid());
			}
		}
	}

	public static void addEdgeUseGraph(DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> g, Long idIni,
			Long idFin) {
		// System.out.println("idIni = " + idIni + " - idFin = " + idFin + "-
		// égalité = " + (idIni.equals(idFin)));
		if (!g.vertexSet().contains(idIni))
			g.addVertex(idIni);
		if (!g.vertexSet().contains(idFin))
			g.addVertex(idFin);
		if (!idIni.equals(idFin))
			if (!g.containsEdge(idIni, idFin)) {
				DefaultWeightedEdge e = g.addEdge(idIni, idFin);
				g.setEdgeWeight(e, 1);
			} else {
				DefaultWeightedEdge e = g.getEdge(idIni, idFin);
				g.setEdgeWeight(e, g.getEdgeWeight(e) + 1);
			}
	}

	public static List<Long> selectAuthorsOfUsedNodes(OSMResource way) throws Exception {
		List<Long> listOfTarget = new ArrayList<Long>();
		if (way.getVersion() == 1) {
			List<Long> nodesID = ((OSMWay) way.getGeom()).getVertices();
			listOfTarget = selectNodesFromWay(way, nodesID);
		} else {
			List<Long> nodesID = compareWayVersions(way);
			if (nodesID.size() != 0)
				listOfTarget = selectNodesFromWay(way, nodesID);
		}
		return listOfTarget;
	}

	public static List<Long> selectNodesFromWay(OSMResource way, List<Long> nodesID) throws Exception {
		List<Long> listOfTarget = new ArrayList<Long>();
		// Nodes created/edited within timespan
		String queryNodes = "SELECT DISTINCT ON (id) * FROM node WHERE id =" + nodesID.get(0);
		for (int i = 1; i < nodesID.size(); i++) {
			queryNodes += " OR id=" + nodesID.get(i);
		}
		queryNodes += " ORDER BY id, datemodif DESC";
		String query = "SELECT DISTINCT (uid) uid FROM (" + queryNodes + ") AS nodes_composing_way;";
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
			conn = DriverManager.getConnection(url, dbUser, dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery(query);
			// System.out.println("------- Query Executed -------");
			while (r.next()) {
				if (r.getInt("uid") != way.getUid())
					listOfTarget.add(r.getLong("uid"));
			}
			s.close();
			conn.close();
		} catch (Exception e) {
			throw e;
		}
		return listOfTarget;
	}

	public static List<Long> compareWayVersions(OSMResource way) throws Exception {
		// Write a query that fetches way version v and way version v-1
		// Nodes created/edited within timespan
		String query1 = "SELECT to_json(composedof) AS composedof FROM way WHERE id= " + way.getId() + " AND vway = "
				+ way.getVersion() + ";";
		String query2 = "SELECT to_json(composedof) AS composedof FROM way WHERE id= " + way.getId() + " AND vway = "
				+ (way.getVersion() - 1) + ";";
		java.sql.Connection conn;
		ArrayList<Long> composedOf1 = new ArrayList<Long>();
		ArrayList<Long> composedOf2 = new ArrayList<Long>();
		try {
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
			conn = DriverManager.getConnection(url, dbUser, dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery(query1);
			// System.out.println("------- Query Executed -------");
			while (r.next()) {
				String[] splitter = r.getString("composedof").split(",");
				StringBuffer nodeID = null;
				for (int i = 0; i < splitter.length; i++) {
					nodeID = new StringBuffer();
					nodeID.append(splitter[i]);
					if (i == 0) {
						nodeID.deleteCharAt(0);
					}
					if (i == splitter.length - 1) {
						nodeID.deleteCharAt(nodeID.length() - 1);
					}
					composedOf1.add(Long.valueOf(nodeID.toString()));
				}
			}
			r = s.executeQuery(query2);
			// System.out.println("------- Query Executed -------");
			while (r.next()) {
				String[] splitter = r.getString("composedof").split(",");
				StringBuffer nodeID = null;
				for (int i = 0; i < splitter.length; i++) {
					nodeID = new StringBuffer();
					nodeID.append(splitter[i]);
					if (i == 0) {
						nodeID.deleteCharAt(0);
					}
					if (i == splitter.length - 1) {
						nodeID.deleteCharAt(nodeID.length() - 1);
					}
					composedOf2.add(Long.valueOf(nodeID.toString()));
				}
			}
			s.close();
			conn.close();
		} catch (Exception e) {
			throw e;
		}
		List<Long> nodesID = new ArrayList<Long>();
		for (int i = 0; i < composedOf2.size(); i++) {
			if (!composedOf1.contains(composedOf2.get(i)))
				nodesID.add(composedOf2.get(i));
		}
		return nodesID;
	}

	public static HashMap<OSMResource, List<String[][]>> getRelationByMembers(HashMap<Long, OSMObject> myOSMObjects,
			String dateMin, String dateMax) throws Exception {
		HashMap<OSMResource, List<String[][]>> membersByRelation = new HashMap<OSMResource, List<String[][]>>();
		String idmbArray = "\'{";
		for (Long idmb : myOSMObjects.keySet()) {
			idmbArray += idmb + ",";
		}
		idmbArray.substring(0, idmbArray.length());
		idmbArray += "}\'";
		String query = "SELECT DISTINCT ON (b.idrel) b.idrel, b.uid, a.idmb FROM relationmember a, relation b WHERE b.datemodif >= \'"
				+ dateMin + "\' AND b.datemodif <=\'" + dateMax + "\' AND b.idrel = a.idrel AND a.idmb = ANY("
				+ idmbArray + "::int[]);";

//		System.out.println(query);
		java.sql.Connection conn;
		String[][] table;
		try {
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
			conn = DriverManager.getConnection(url, dbUser, dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery(query);
//			System.out.println("------- Query Executed -------");
			table = new String[r.getFetchSize()][4];
			int i = 0;
			while (r.next()) {
				table[i][0] = r.getString("idrel");
				table[i][1] = r.getString("uid");
				table[i][2] = r.getString("datemodif");
				table[i][3] = r.getString("idmb");
				table[i][4] = r.getString("typemb");
				i++;
			}
			s.close();
			conn.close();
		} catch (Exception e) {
			throw e;
		}

		for (OSMObject obj : myOSMObjects.values()) {
			for (int i = 0; i < table.length; i++) {
				// TO DO
			}
		}
		return membersByRelation;
	}

	public static List<Long> getRelationUid(OSMResource resource, List<Long> listOfidrel) throws Exception {
		List<Long> listOfuid = new ArrayList<Long>();
		String query = "SELECT * FROM relation WHERE (idrel = " + listOfidrel.get(0);
		if (listOfidrel.size() > 1) {
			for (int i = 1; i < listOfidrel.size(); i++) {
				query += " OR idrel = " + listOfidrel.get(i);
			}
		}
		// query += ") AND datemodif >=\'" + resource.getDate() + "\'" + " AND
		// datemodif <=\'" + dateMax + "\';";
		query += ") AND datemodif >=\'" + resource.getDate() + "\';";
//		System.out.println(query);
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
			conn = DriverManager.getConnection(url, dbUser, dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery(query);
//			System.out.println("------- Query Executed -------");
			while (r.next()) {
				listOfuid.add(r.getLong("uid"));
			}
			s.close();
			conn.close();
		} catch (Exception e) {
			throw e;
		}
		return listOfuid;
	}

	public static List<Long> getRelationMembersIdrel(OSMResource resource) throws Exception {
		List<Long> listOfidrel = new ArrayList<Long>();
		String idrelQuery = null;
		String idrelArray = "\'{";
		// Cherche les relations composées de la resource
		idrelQuery = "SELECT idrel FROM relationmember WHERE idmb = " + resource.getId();
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
			conn = DriverManager.getConnection(url, dbUser, dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery(idrelQuery);
//			System.out.println("------- Query Executed -------");
			while (r.next()) {
				listOfidrel.add(r.getLong("idrel"));
			}
			s.close();
			conn.close();
		} catch (Exception e) {
			throw e;
		}
		return listOfidrel;
	}

	public static boolean isGeomAddition(Set<OSMResource> myJavaObjects, OSMResource resource) {
		boolean isGeomAddition = false;
		OSMResource previousResource = getPreviousVersion(myJavaObjects, resource);
		// Détermination du type d'édition entre la v et la v-1
		if (previousResource != null) {
			// System.out.println("Objet : " + previousResource.getId() + " -
			// version: " + previousResource.getVersion());
			List<Long> previousComposition = ((OSMWay) previousResource.getGeom()).getVertices();
			// System.out.println("Nombre de nodes : " +
			// previousComposition.size());
			List<Long> currentComposition = ((OSMWay) resource.getGeom()).getVertices();
			if (!previousComposition.containsAll(currentComposition))
				isGeomAddition = true;
		}
		// System.out.println("IsGeomAddition : " + isGeomAddition);
		return isGeomAddition;
	}

	public static OSMResource getPreviousVersion(Set<OSMResource> myJavaObjects, OSMResource resource) {
		OSMResource previousResource = null;
		// Recherche la v-1 de l'objet
		for (OSMResource contribution : myJavaObjects) {
			if (contribution.getId() == resource.getId() && contribution.getVersion() == resource.getVersion() - 1) {
				previousResource = contribution;
			}
		}
		return previousResource;
	}

	public static List<OSMResource> getAddedNodes(Set<OSMResource> myJavaObjects, OSMResource resource) {
		List<OSMResource> listAddedNodes = new ArrayList<OSMResource>();
		if (isGeomAddition(myJavaObjects, resource)) {
			OSMResource previousResource = getPreviousVersion(myJavaObjects, resource);
			List<OSMResource> currentComposition = IntrinsicAssessment.getNodesComposingWay(myJavaObjects, resource);
			List<OSMResource> previousComposition = IntrinsicAssessment.getNodesComposingWay(myJavaObjects,
					previousResource);
			for (OSMResource node : currentComposition) {
				if (!previousComposition.contains(node)) {
					listAddedNodes.add(node);
				}
			}
		}
		return listAddedNodes;
	}

	public static void getMaxEdge(DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> g,
			DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> subg) {
		Collection<DefaultWeightedEdge> edges = subg.edgeSet();
		for (DefaultWeightedEdge edge : edges) {
			if (!g.containsEdge(subg.getEdgeSource(edge), subg.getEdgeTarget(edge))) {
				g.addEdge(subg.getEdgeSource(edge), subg.getEdgeTarget(edge));
				DefaultWeightedEdge newEdge = g.getEdge(subg.getEdgeSource(edge), subg.getEdgeTarget(edge));
				g.setEdgeWeight(newEdge, subg.getEdgeWeight(edge));
			} else {
				DefaultWeightedEdge existingEdge = g.getEdge(subg.getEdgeSource(edge), subg.getEdgeTarget(edge));
				double depthIni = g.getEdgeWeight(existingEdge);
				double depthToCompare = subg.getEdgeWeight(edge);
				g.setEdgeWeight(existingEdge, Math.max(depthIni, depthToCompare));
			}
		}
	}

	public static void mergeSubGraph(DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> g,
			DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> subg) {
		Collection<DefaultWeightedEdge> edges = subg.edgeSet();
		for (DefaultWeightedEdge edge : edges) {
			if (!g.containsEdge(subg.getEdgeSource(edge), subg.getEdgeTarget(edge))) {
				g.addEdge(subg.getEdgeSource(edge), subg.getEdgeTarget(edge));
				DefaultWeightedEdge newEdge = g.getEdge(subg.getEdgeSource(edge), subg.getEdgeTarget(edge));
				g.setEdgeWeight(newEdge, subg.getEdgeWeight(edge));
			} else {
				DefaultWeightedEdge existingEdge = g.getEdge(subg.getEdgeSource(edge), subg.getEdgeTarget(edge));
				g.setEdgeWeight(existingEdge, g.getEdgeWeight(existingEdge) + subg.getEdgeWeight(edge));
			}
		}
	}

	public static void mergeGraph(DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> g1,
			DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> g2) {
		Collection<DefaultWeightedEdge> edges = g2.edgeSet();
		for (DefaultWeightedEdge edge : edges) {
			if (!g1.containsVertex(g2.getEdgeTarget(edge)))
				g1.addVertex(g2.getEdgeTarget(edge));
			if (!g1.containsVertex(g2.getEdgeSource(edge)))
				g1.addVertex(g2.getEdgeSource(edge));
			if (!g1.containsEdge(g2.getEdgeSource(edge), g2.getEdgeTarget(edge))) {
				g1.addEdge(g2.getEdgeSource(edge), g2.getEdgeTarget(edge));
				DefaultWeightedEdge newEdge = g1.getEdge(g2.getEdgeSource(edge), g2.getEdgeTarget(edge));
				g1.setEdgeWeight(newEdge, g2.getEdgeWeight(edge));
			} else {
				DefaultWeightedEdge existingEdge = g1.getEdge(g2.getEdgeSource(edge), g2.getEdgeTarget(edge));
				g1.setEdgeWeight(existingEdge, g1.getEdgeWeight(existingEdge) + g2.getEdgeWeight(edge));
			}
		}
	}

	public static SimpleWeightedGraph<Long, DefaultWeightedEdge> createCoLocationGraph(
			HashMap<Long, OSMContributor> myOSMContributors, List<Double> bbox, List<String> timespan, double threshold)
			throws Exception {
		SimpleWeightedGraph<Long, DefaultWeightedEdge> g = new SimpleWeightedGraph<Long, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		// Remplit pour chaque OSMContributor l'attribut ActivityAreas
		assignActivityAreas(myOSMContributors, bbox, timespan, threshold);
		List<OSMContributor> myContributorList = new ArrayList<OSMContributor>();
		for (OSMContributor contributor : myOSMContributors.values()) {
			myContributorList.add(contributor);
		}
		// Parcourt la liste des contributeurs
		for (int i = 0; i < myContributorList.size() - 1; i++) {
			IFeatureCollection<DefaultFeature> areaCurrentContributor = myContributorList.get(i).getActivityAreas();
			if (areaCurrentContributor == null)
				continue;
			if (!g.containsVertex((long) myContributorList.get(i).getId()))
				g.addVertex((long) myContributorList.get(i).getId());
			// Parcourt les contributeurs suivants de la liste
			for (int j = i + 1; j < myOSMContributors.size(); j++) {
				if (!g.containsVertex((long) myContributorList.get(j).getId()))
					g.addVertex((long) myContributorList.get(j).getId());
				IFeatureCollection<DefaultFeature> areaNextContributor = myContributorList.get(j).getActivityAreas();
				if (areaNextContributor == null)
					continue;
				double totalDistance = 0;
				// Parcourt les zones d'activité du contributeur courant
				for (int u = 0; u < areaCurrentContributor.size(); u++) {
					IGeometry currentZone = areaCurrentContributor.get(u).getGeom();
					// Parcourt les zones d'activité du contributeur suivant
					for (int v = 0; v < areaCurrentContributor.size(); v++) {
						IGeometry nextZone = areaCurrentContributor.get(v).getGeom();
						// Calcul de la distance surfacique s'il y a
						// intersection
						if (currentZone.intersects(nextZone)) {
							double union = currentZone.union(nextZone).area();
							double intersection = currentZone.intersection(nextZone).area();
							double distance = 1 - intersection / union;
							totalDistance += distance;
						}
					}

				}
				if (totalDistance > 0) {
					// double union =
					// areaCurrentContributor.union(areaNextContributor).area();
					// double intersection =
					// areaCurrentContributor.intersection(areaNextContributor).area();
					// double distance = 1 - intersection / union;
					DefaultWeightedEdge e = g.addEdge((long) myContributorList.get(i).getId(),
							(long) myContributorList.get(j).getId());
					g.setEdgeWeight(e, 1 / totalDistance);
					// if (distance > 0)
					// g.setEdgeWeight(e, 1 / distance);
					// else
					// g.setEdgeWeight(e, Double.POSITIVE_INFINITY);
				}

			}
		}

		return g;
	}

	/**
	 * Computes for each OSMContributor of the input list of contributors the
	 * activity areas
	 **/
	public static void assignActivityAreas(HashMap<Long, OSMContributor> myOSMContributors, List<Double> bbox,
			List<String> timespan, double threshold) throws Exception {
		for (Long uid : myOSMContributors.keySet()) {
			List<OSMResource> uidNodes = ActivityArea.selectNodesByUid(uid, bbox, timespan);
			IGeometry uidArea = ActivityArea.getActivityAreas(uidNodes, threshold);
			IFeatureCollection<DefaultFeature> denseActivityCollection = ActivityArea.getDenseActivityAreas(uidArea,
					uidNodes, 5);
			myOSMContributors.get(uid).setActivityAreas(denseActivityCollection);
		}
	}

	/**
	 * Warning: use writeColocationGraph2CSV to export colocation graphs into
	 * CSV files.
	 */
	public static void writeGraph2CSV(DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> usegraph, File file)
			throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(file), ';');
//		System.out.println("écriture du fichier csv");
		// write header
		String[] line = new String[3];
		line[0] = "source";
		line[1] = "target";
		line[2] = "weight";
		writer.writeNext(line);
		for (DefaultWeightedEdge e : usegraph.edgeSet()) {
			line = new String[3];
			line[0] = String.valueOf(usegraph.getEdgeSource(e).longValue() + (long) 111);
			line[1] = String.valueOf(usegraph.getEdgeTarget(e).longValue() + (long) 111);
			line[2] = String.valueOf(usegraph.getEdgeWeight(e));
			writer.writeNext(line);
		}
		writer.close();
	}

	public static void writeSimpleWeightedGraph2CSV(SimpleWeightedGraph<Long, DefaultWeightedEdge> colocationgraph,
			File file) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(file), ';');
		// write header
		String[] line = new String[4];
		line[0] = "source";
		line[1] = "target";
		line[2] = "weight";
		line[3] = "type";
		writer.writeNext(line);
		for (DefaultWeightedEdge e : colocationgraph.edgeSet()) {
			line = new String[4];
			line[0] = String.valueOf(colocationgraph.getEdgeSource(e).longValue() + (long) 111);
			line[1] = String.valueOf(colocationgraph.getEdgeTarget(e).longValue() + (long) 111);
			line[2] = String.valueOf(colocationgraph.getEdgeWeight(e));
			line[3] = "undirected";
			writer.writeNext(line);
		}
		writer.close();
	}

}
