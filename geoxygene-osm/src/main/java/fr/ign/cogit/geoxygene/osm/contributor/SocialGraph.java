package fr.ign.cogit.geoxygene.osm.contributor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import au.com.bytecode.opencsv.CSVWriter;
import fr.ign.cogit.geoxygene.osm.importexport.OSMObject;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.OSMWay;
import fr.ign.cogit.geoxygene.osm.importexport.metrics.ContributorAssessment;
import fr.ign.cogit.geoxygene.osm.importexport.metrics.IntrinsicAssessment;
import fr.ign.cogit.geoxygene.osm.importexport.postgis.LoadFromPostGIS;

public class SocialGraph<V, E> {
	private static double factor;
	int edgeCount = 0;

	public static void main(String[] args) throws Exception {
		LoadFromPostGIS loader = new LoadFromPostGIS("localhost", "5432", "paris", "postgres", "postgres");
		List<Double> bbox = new ArrayList<Double>();
		bbox.add(2.3312);
		bbox.add(48.8479);
		bbox.add(2.3644);
		bbox.add(48.8637);
		List<String> timespan = new ArrayList<String>();
		timespan.add("2011-01-01");
		timespan.add("2014-01-01");

		loader.selectNodes(bbox, timespan);
		loader.selectWays(bbox, timespan);

		// loader.contributionSummary();
		// loader.contributorSummary();
		//
		// List<OSMResource> myJavaObjects = loader.myJavaObjects;
		// HashMap<Long, OSMObject> myOSMObjects = loader.myOSMObjects;
		// HashMap<Long, OSMContributor> myContributors = loader.myContributors;
		//
		// loader.writeContributorSummary(new
		// File("contributeurs_paris_20110101-20110201.csv"));
		//
		// Graph<OSMContributor, DefaultWeightedEdge> globalCollaborationGraph =
		// createCollaborationGraph(myOSMObjects,
		// myContributors, "global");
		// writeGraph2CSV(globalCollaborationGraph, new
		// File("globalCollaborationGraph.csv"));
		// Graph<OSMContributor, DefaultWeightedEdge> widthCollaborationGraph =
		// createCollaborationGraph(myOSMObjects,
		// myContributors, "width");
		// writeGraph2CSV(widthCollaborationGraph, new
		// File("widthCollaborationGraph.csv"));
		// Graph<OSMContributor, DefaultWeightedEdge> depthCollaborationGraph =
		// createCollaborationGraph(myOSMObjects,
		// myContributors, "depth");
		// writeGraph2CSV(depthCollaborationGraph, new
		// File("depthCollaborationGraph.csv"));
		// factor = 2;
		// Graph<OSMContributor, DefaultWeightedEdge> combinedCollaborationGraph
		// = createCollaborationGraph(myOSMObjects,
		// myContributors, "combined");
		// writeGraph2CSV(combinedCollaborationGraph, new
		// File("combinedCollaborationGraph_pw2.csv"));
		//
		// Graph<OSMContributor, DefaultWeightedEdge> coContributionGraph =
		// createCoContributionGraph(myOSMObjects,
		// myContributors);
		// writeGraph2CSV(coContributionGraph, new
		// File("coContributionGraph.csv"));
		//
		// Graph<OSMContributor, DefaultWeightedEdge> coEditionGraph =
		// createCoEditionGraph(myOSMObjects, myContributors);
		// writeGraph2CSV(coEditionGraph, new File("coEditionGraph.csv"));

		// LoadFromPostGIS loaderNepal = new LoadFromPostGIS("localhost",
		// "5432", "nepal", "postgres", "postgres");
		// List<Double> bboxNepal = new ArrayList<Double>();
		// bboxNepal.add(85.33630);
		// bboxNepal.add(27.69640);
		// bboxNepal.add(85.34890);
		// bboxNepal.add(27.70650);
		// List<String> timespanNepal = new ArrayList<String>();
		// timespanNepal.add("2013-01-01");
		// timespanNepal.add("2014-01-01");

		HashMap<Long, OSMContributor> myContributors = ContributorAssessment.contributorSummary(loader.myJavaObjects);
		IntrinsicAssessment.sortJavaObjects(loader.myJavaObjects);
		DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge> usegraph = createUseGraph(
				loader.myJavaObjects, myContributors);
		writeGraph2CSV(usegraph, new File("paris_usegraph.csv"));

		// System.out.println(loaderNepal.myJavaObjects.get(0).getGeom().getClass().getSimpleName());

		// loaderNepal.contributionSummary();
		// loaderNepal.contributorSummary();

		// loaderNepal.writeContributionSummary(new
		// File("nepal_contributions_katmandou2013.csv"));
		// loaderNepal.writeContributorSummary(new
		// File("nepal_contributeurs_katmandou2013.csv"));

	}

	public static Graph<OSMContributor, DefaultWeightedEdge> createCoContributionGraph(
			HashMap<Long, OSMObject> myOSMObjects, HashMap<Long, OSMContributor> myContributors) throws IOException {
		DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge> g = new DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		// Add vertices
		for (OSMContributor contributor : myContributors.values()) {
			g.addVertex(contributor);
		}
		// Add edges
		for (OSMObject osmObject : myOSMObjects.values()) {
			int lastContributionRange = osmObject.getContributions().size() - 1;
			for (int i = lastContributionRange; i > 0; i--) {
				OSMContributor nodeIni = myContributors.get((long) osmObject.getContributions().get(i).getUid());
				// int w = 1;
				for (int j = i - 1; j >= 0; j--) {
					OSMContributor nodeFin = myContributors.get((long) osmObject.getContributions().get(j).getUid());
					System.out.println(g.containsEdge(nodeIni, nodeFin));
					// double weight = (double) 1 / w;
					if (nodeIni.equals(nodeFin))
						continue;
					if (!g.containsEdge(nodeIni, nodeFin) || !g.containsEdge(nodeFin, nodeIni)) {
						/* DefaultWeightedEdge e = (DefaultWeightedEdge) */g.addEdge(nodeIni, nodeFin);
						// g.setEdgeWeight(e, weight);
					} else {
						// DefaultWeightedEdge e = g.getEdge(nodeIni, nodeFin);
						// g.setEdgeWeight(e, g.getEdgeWeight(e) + weight);
					}
					System.out.println("Ajout d'un arc entre " + nodeIni.getName() + " et " + nodeFin.getName());
					// w++;
				}
			}
		}
		return g;
	}

	public static Graph<OSMContributor, DefaultWeightedEdge> createCoEditionGraph(HashMap<Long, OSMObject> myOSMObjects,
			HashMap<Long, OSMContributor> myContributors) {
		DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge> g = new DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		// Add vertices
		for (OSMContributor contributor : myContributors.values()) {
			g.addVertex(contributor);
		}
		// Add edges
		for (OSMObject osmObject : myOSMObjects.values()) {
			int lastContributionRange = osmObject.getContributions().size() - 1;
			for (int i = lastContributionRange; i > 0; i--) {
				OSMContributor nodeIni = myContributors.get((long) osmObject.getContributions().get(i).getUid());
				OSMContributor nodeFin = myContributors.get((long) osmObject.getContributions().get(i - 1).getUid());
				if (nodeIni.equals(nodeFin))
					continue;
				else {
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

	public static Graph<OSMContributor, DefaultWeightedEdge> createCollaborationGraph(
			HashMap<Long, OSMObject> myOSMObjects, HashMap<Long, OSMContributor> myContributors,
			String collaborationType) {
		DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge> g = new DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);

		DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge> subGraph;
		DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge> osmObjectGraph;
		DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge> sumEdgeGraph;
		// Add vertices
		for (OSMContributor contributor : myContributors.values()) {
			g.addVertex(contributor);
		}

		// Add edges
		for (OSMObject osmObject : myOSMObjects.values()) {
			subGraph = new DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			osmObjectGraph = new DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge>(
					DefaultWeightedEdge.class);
			sumEdgeGraph = new DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge>(
					DefaultWeightedEdge.class);

			int lastContributionRange = osmObject.getContributions().size() - 1;
			for (int i = lastContributionRange; i > 0; i--) {
				OSMContributor nodeIni = myContributors.get((long) osmObject.getContributions().get(i).getUid());
				if (!subGraph.containsVertex(nodeIni))
					subGraph.addVertex(nodeIni);
				if (collaborationType.equalsIgnoreCase("depth") && !osmObjectGraph.containsVertex(nodeIni))
					osmObjectGraph.addVertex(nodeIni);
				if (collaborationType.equalsIgnoreCase("combined") && !osmObjectGraph.containsVertex(nodeIni))
					sumEdgeGraph.addVertex(nodeIni);

				// Cherche l'indice de l'avant dernière contribution du
				// contributeur courant
				int currentContributorPreviousContribution = 0;
				OSMContributor nodeFin = null;
				for (int j = i - 1; j >= 0; j--) {
					nodeFin = myContributors.get((long) osmObject.getContributions().get(j).getUid());
					if (nodeIni.equals(nodeFin)) {
						currentContributorPreviousContribution = j;
						break;
					}
				}
				// Parcours des éditions faites entre la contribution du
				// contributeur courant et la précédente
				for (int k = i - 1; k > currentContributorPreviousContribution; k--) {
					nodeFin = myContributors.get((long) osmObject.getContributions().get(k).getUid());
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
				if (collaborationType.equalsIgnoreCase("global")) {
					mergeSubGraph(g, subGraph);
					subGraph = new DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge>(
							DefaultWeightedEdge.class);
				}
				if (collaborationType.equalsIgnoreCase("depth")) {
					mergeSubGraph(osmObjectGraph, subGraph);
					subGraph = new DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge>(
							DefaultWeightedEdge.class);
					getMaxEdge(g, osmObjectGraph);
					osmObjectGraph = new DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge>(
							DefaultWeightedEdge.class);
				}
				if (collaborationType.equalsIgnoreCase("combined")) {
					// On met à la puissance le total des interactions
					// comptabilisées dans le graphe g sur un même objet
					mergeSubGraph(sumEdgeGraph, subGraph);
					subGraph = new DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge>(
							DefaultWeightedEdge.class);
				}
			} // Fin de l'objet courant, on passe au suivant
			if (collaborationType.equalsIgnoreCase("width")) {
				mergeSubGraph(g, subGraph);
				subGraph = new DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge>(
						DefaultWeightedEdge.class);
			}
			if (collaborationType.equalsIgnoreCase("combined")) {
				for (DefaultWeightedEdge edge : sumEdgeGraph.edgeSet()) {
					g.setEdgeWeight(edge, Math.pow(sumEdgeGraph.getEdgeWeight(edge), factor));
				}
				mergeSubGraph(g, sumEdgeGraph);
				sumEdgeGraph = new DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge>(
						DefaultWeightedEdge.class);
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

	public static DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge> createUseGraph(
			List<OSMResource> myJavaObjects, HashMap<Long, OSMContributor> myContributors) {
		DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge> g = new DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		// Add vertices
		for (OSMContributor contributor : myContributors.values()) {
			g.addVertex(contributor);
		}
		// Add edges
		for (OSMResource resource : myJavaObjects) {
			if (resource.getGeom().getClass().getSimpleName().equals("OSMWay")) {
				OSMWay myWay = (OSMWay) resource.getGeom();
				int uidway = resource.getUid();
				List<OSMResource> myNodes = IntrinsicAssessment.getNodesComposingWay(myJavaObjects, myWay);
				if (resource.getVersion() == 1) {
					for (OSMResource node : myNodes) {
						if (node.getUid() != uidway) {
							// créer un lien entre les deux utilisateurs
							OSMContributor nodeIni = myContributors.get(uidway);
							OSMContributor nodeFin = myContributors.get(node.getUid());
							g.addEdge(nodeIni, nodeFin);
						}
					}
				} else {
					System.out.println("Version ressource > 1");
					if (isGeomAddition(myJavaObjects, resource)) {
						if (getPreviousVersion(myJavaObjects, resource) != null) {
							System.out.println("isGeomAddition");
							List<OSMResource> addedNodesList = getAddedNodes(myJavaObjects, resource);
							System.out.println("Nombre de noeuds ajoutés = " + addedNodesList.size());
							for (OSMResource addedNode : addedNodesList) {
								System.out.println(
										"Uid précédent = " + addedNode.getUid() + " - Uid courant = " + uidway);
								if (addedNode.getUid() != resource.getUid()) {
									// créer un lien entre les deux utilisateurs
									OSMContributor nodeIni = myContributors.get(uidway);
									OSMContributor nodeFin = myContributors.get(addedNode.getUid());
									g.addEdge(nodeIni, nodeFin);
								}
							}
						}

					}
				}
			}
		}
		return g;
	}

	/**
	 * @param resource
	 *            : OSMResource (way type) whose version > 1
	 * @return isGeomAddition: if version v contains more node than version v-1
	 *         then isGeomAddition values TRUE, FALSE otherwise
	 **/
	public static boolean isGeomAddition(List<OSMResource> myJavaObjects, OSMResource resource) {
		boolean isGeomAddition = false;
		OSMResource previousResource = getPreviousVersion(myJavaObjects, resource);
		// Détermination du type d'édition entre la v et la v-1
		if (previousResource != null) {
			System.out.println("Objet : " + previousResource.getId() + " - version: " + previousResource.getVersion());
			List<Long> previousComposition = ((OSMWay) previousResource.getGeom()).getVertices();
			System.out.println("Nombre de nodes : " + previousComposition.size());
			List<Long> currentComposition = ((OSMWay) resource.getGeom()).getVertices();
			if (!previousComposition.containsAll(currentComposition))
				isGeomAddition = true;
		}
		System.out.println("IsGeomAddition : " + isGeomAddition);
		return isGeomAddition;
	}

	public static OSMResource getPreviousVersion(List<OSMResource> myJavaObjects, OSMResource resource) {
		OSMResource previousResource = null;
		// Recherche la v-1 de l'objet
		for (OSMResource contribution : myJavaObjects) {
			if (contribution.getId() == resource.getId() && contribution.getVersion() == resource.getVersion() - 1) {
				previousResource = contribution;
			}
		}
		return previousResource;
	}

	public static List<OSMResource> getAddedNodes(List<OSMResource> myJavaObjects, OSMResource resource) {
		List<OSMResource> listAddedNodes = new ArrayList<OSMResource>();
		if (isGeomAddition(myJavaObjects, resource)) {
			OSMResource previousResource = getPreviousVersion(myJavaObjects, resource);
			List<OSMResource> currentComposition = IntrinsicAssessment.getNodesComposingWay(myJavaObjects,
					(OSMWay) resource.getGeom());
			System.out.println("currentComposition size =" + currentComposition.size());
			List<OSMResource> previousComposition = IntrinsicAssessment.getNodesComposingWay(myJavaObjects,
					(OSMWay) previousResource.getGeom());
			System.out.println("previousComposition size =" + previousComposition.size());
			for (OSMResource node : currentComposition) {
				System.out.println("node is contained is previous composition" + previousComposition.contains(node));
				if (!previousComposition.contains(node)) {
					listAddedNodes.add(node);
				}
			}
		}
		return listAddedNodes;
	}

	public static void getMaxEdge(DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge> g,
			DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge> subg) {
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

	public static void mergeSubGraph(DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge> g,
			DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge> subg) {
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

	public static void writeGraph2CSV(Graph<OSMContributor, DefaultWeightedEdge> g, File file) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(file), ';');
		// write header
		String[] line = new String[3];
		line[0] = "source";
		line[1] = "target";
		line[2] = "weight";
		writer.writeNext(line);
		System.out.println(g.edgeSet().size());
		for (DefaultWeightedEdge e : g.edgeSet()) {
			line = new String[3];
			line[0] = String.valueOf(g.getEdgeSource(e).getId());
			line[1] = String.valueOf(g.getEdgeTarget(e).getId());
			line[2] = String.valueOf(g.getEdgeWeight(e));
			writer.writeNext(line);
		}
		writer.close();
	}

}
