package fr.ign.cogit.geoxygene.osm.contributor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
import fr.ign.cogit.geoxygene.osm.importexport.metrics.IntrinsicAssessment;
import fr.ign.cogit.geoxygene.osm.importexport.postgis.LoadFromPostGIS;

public class SocialGraph<V, E> {
	public static String host = "localhost";
	public static String port = "5432";
	public static String dbName = "paris";
	public static String dbUser = "postgres";
	public static String dbPwd = "postgres";
	private static double factor;
	int edgeCount = 0;

	public static void main(String[] args) throws Exception {
		LoadFromPostGIS loader = new LoadFromPostGIS("localhost", "5432", "paris", "postgres", "postgres");
		List<Double> bbox = new ArrayList<Double>();
		bbox.add(2.3322);
		bbox.add(48.8489);
		bbox.add(2.3634);
		bbox.add(48.8627);
		List<String> timespan = new ArrayList<String>();
		timespan.add("2014-01-01");
		timespan.add("2014-03-01");
		loader.selectNodes(bbox, timespan);
		loader.selectWays(bbox, timespan);
		String query = null;
		for (OSMResource obj : loader.myJavaObjects) {
			List<Long> listOfidrel = getRelationMembersIdrel(obj);
			if (!listOfidrel.isEmpty()) {
				query = "SELECT * FROM relation WHERE idrel = " + listOfidrel.get(0);
				if (listOfidrel.size() > 1) {
					for (int i = 1; i < listOfidrel.size(); i++) {
						query += " OR idrel = " + listOfidrel.get(i);
					}
				}
			}

			System.out.println(query);
		}
		// loader.selectNodesInit(bbox, "2014-01-01");
		// IntrinsicAssessment.writeContributionDetails(new
		// File("snapshot_paris_20110101.csv"), loader.myJavaObjects);
		// HashMap<Long, OSMContributor> myContributors =
		// ContributorAssessment.contributorSummary(loader.myJavaObjects);
		// ContributorAssessment.writeContributorSummary(myContributors,
		// new File("contributeurs_paris_20140101-20140301.csv"));
		// System.out.println("myContributors.size() = " +
		// myContributors.size());
		// IntrinsicAssessment.writeContributionDetails(new
		// File("paris_20110101-20110301.csv"), loader.myJavaObjects);
		// DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> usegraph =
		// createUseGraph(loader.myJavaObjects);
		// writeGraph2CSV(usegraph, new File("paris_usegraph.csv"));
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

	// public static DefaultDirectedWeightedGraph<OSMContributor,
	// DefaultWeightedEdge> createUseGraph(
	// List<OSMResource> myJavaObjects, HashMap<Long, OSMContributor>
	// myContributors) {
	// DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge> g = new
	// DefaultDirectedWeightedGraph<OSMContributor, DefaultWeightedEdge>(
	// DefaultWeightedEdge.class);
	// // Add vertices
	// for (OSMContributor contributor : myContributors.values()) {
	// g.addVertex(contributor);
	// System.out.println("myContributors: ajout de " + contributor.getName());
	// }
	// System.out.print("nombre de sommets dans le graphe :" +
	// g.vertexSet().size());
	// // Add edges
	// for (int i = 0; i < myJavaObjects.size(); i++) {
	// System.out.println("i = " + i);
	// if
	// (myJavaObjects.get(i).getGeom().getClass().getSimpleName().equals("OSMWay"))
	// {
	// int uidway = myJavaObjects.get(i).getUid();
	// List<OSMResource> myNodes =
	// IntrinsicAssessment.getNodesComposingWay(myJavaObjects,
	// myJavaObjects.get(i));
	// if (myNodes.isEmpty())
	// break;
	// System.out.println("Nombre de nodes composant " +
	// myJavaObjects.get(i).getId()
	// + myJavaObjects.get(i).getVersion() + " : " + myNodes.size());
	// if (myJavaObjects.get(i).getVersion() == 1) {
	// System.out.println("myJavaObjects.get(i).getVersion() == 1");
	// for (OSMResource node : myNodes) {
	// if (node.getUid() != uidway) {
	// System.out.println("Contributeur du way différent du contributeur du
	// node");
	// // créer un lien entre les deux utilisateurs
	// OSMContributor nodeIni = myContributors.get(uidway);
	// System.out.println("Auteur du way créé : " + nodeIni.getName());
	// OSMContributor nodeFin = myContributors.get(node.getUid());
	// System.out.println("Auteur du node utilisé : " + nodeFin.getName());
	// if (nodeIni != null && nodeFin != null)
	// if (!g.containsEdge(nodeIni, nodeFin)) {
	// System.out
	// .println(nodeIni.getName() + " a utilisé le node de " +
	// nodeFin.getName());
	// // g.addEdge(nodeIni, nodeFin);
	// DefaultWeightedEdge e = g.addEdge(nodeIni, nodeFin);
	// g.setEdgeWeight(e, 1);
	// } else {
	// DefaultWeightedEdge e = g.getEdge(nodeIni, nodeFin);
	// g.setEdgeWeight(e, g.getEdgeWeight(e) + 1);
	// }
	// }
	// }
	// } else {
	// System.out.println("myJavaObjects.get(i).getVersion() > 1");
	// if (isGeomAddition(myJavaObjects, myJavaObjects.get(i))) {
	// System.out.println("isGeomAddition");
	// if (getPreviousVersion(myJavaObjects, myJavaObjects.get(i)) != null) {
	// System.out.println("getPreviousVersion(myJavaObjects,
	// myJavaObjects.get(i)) != null");
	// List<OSMResource> addedNodesList = getAddedNodes(myJavaObjects,
	// myJavaObjects.get(i));
	// for (OSMResource addedNode : addedNodesList) {
	// if (addedNode.getUid() != myJavaObjects.get(i).getUid()) {
	// // créer un lien entre les deux utilisateurs
	// OSMContributor nodeIni = myContributors.get(uidway);
	// OSMContributor nodeFin = myContributors.get(addedNode.getUid());
	// if (nodeIni != null && nodeFin != null)
	// if (!g.containsEdge(nodeIni, nodeFin)) {
	// System.out.println(
	// nodeIni.getName() + " a utilisé le node de " + nodeFin.getName());
	// // g.addEdge(nodeIni, nodeFin);
	// DefaultWeightedEdge e = g.addEdge(nodeIni, nodeFin);
	// g.setEdgeWeight(e, 1);
	// } else {
	// DefaultWeightedEdge e = g.getEdge(nodeIni, nodeFin);
	// g.setEdgeWeight(e, g.getEdgeWeight(e) + 1);
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	// return g;
	// }

	public static DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> createUseGraph(
			List<OSMResource> myJavaObjects) throws Exception {
		DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> g = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		// Add vertices
		// for (OSMContributor contributor : myContributors.values()) {
		// g.addVertex(contributor);
		// System.out.println("myContributors: ajout de " +
		// contributor.getName());
		// }
		// System.out.print("nombre de sommets dans le graphe :" +
		// g.vertexSet().size());
		// Add edges
		List<Integer> listTarget = new ArrayList<Integer>();
		for (int i = 0; i < myJavaObjects.size(); i++) {
			if (myJavaObjects.get(i).getGeom().getClass().getSimpleName().equals("OSMWay")) {
				listTarget = selectAuthorsOfUsedNodes(myJavaObjects.get(i));
				Long uidway = (long) myJavaObjects.get(i).getUid();
				if (!g.vertexSet().contains(uidway))
					g.addVertex(uidway);
				for (Integer targetID : listTarget) {
					if (!g.vertexSet().contains(Integer.toUnsignedLong(targetID)))
						g.addVertex(Integer.toUnsignedLong(targetID));
					// créer un lien entre les deux utilisateurs
					if (!g.containsEdge(uidway, Integer.toUnsignedLong(targetID))) {
						DefaultWeightedEdge e = g.addEdge(uidway, Integer.toUnsignedLong(targetID));
						g.setEdgeWeight(e, 1);
					} else {
						DefaultWeightedEdge e = g.getEdge(uidway, Integer.toUnsignedLong(targetID));
						g.setEdgeWeight(e, g.getEdgeWeight(e) + 1);
					}
				}

			}
		}
		return g;

	}

	public static List<Integer> selectAuthorsOfUsedNodes(OSMResource way) throws Exception {
		List<Integer> listOfTarget = new ArrayList<Integer>();
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

	public static List<Integer> selectNodesFromWay(OSMResource way, List<Long> nodesID) throws Exception {
		List<Integer> listOfTarget = new ArrayList<Integer>();
		// Nodes created/edited within timespan
		String queryNodes = "SELECT DISTINCT ON (id) * FROM node WHERE id =" + nodesID.get(0);
		System.out.println(queryNodes);
		for (int i = 1; i < nodesID.size(); i++) {
			System.out.println("numéro i =" + i);
			queryNodes += " OR id=" + nodesID.get(i);
		}
		queryNodes += " ORDER BY id, datemodif DESC";
		String query = "SELECT DISTINCT (uid) uid FROM (" + queryNodes + ") AS nodes_composing_way;";
		System.out.println(query);
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
			conn = DriverManager.getConnection(url, dbUser, dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery(query);
			System.out.println("------- Query Executed -------");
			while (r.next()) {
				if (r.getInt("uid") != way.getUid())
					listOfTarget.add(r.getInt("uid"));
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
		System.out.println(query1);
		java.sql.Connection conn;
		ArrayList<Long> composedOf1 = new ArrayList<Long>();
		ArrayList<Long> composedOf2 = new ArrayList<Long>();
		try {
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
			conn = DriverManager.getConnection(url, dbUser, dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery(query1);
			System.out.println("------- Query Executed -------");
			while (r.next()) {
				System.out.println(r.getString("composedof"));
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
					System.out.println(nodeID.toString());
					composedOf1.add(Long.valueOf(nodeID.toString()));
				}
			}
			r = s.executeQuery(query2);
			System.out.println("------- Query Executed -------");
			while (r.next()) {
				System.out.println(r.getString("composedof"));
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
					System.out.println(nodeID.toString());
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

	public static List<Long> getRelationUid(OSMResource resource, List<Long> listOfidrel, String datemax)
			throws Exception {
		List<Long> listOfuid = new ArrayList<Long>();
		String query = "SELECT * FROM relation WHERE (idrel = " + listOfidrel.get(0);
		// if (!listOfidrel.isEmpty()) {
		// query = "SELECT * FROM relation WHERE idrel = " + listOfidrel.get(0);
		if (listOfidrel.size() > 1) {
			for (int i = 1; i < listOfidrel.size(); i++) {
				query += " OR idrel = " + listOfidrel.get(i);
			}
		}
		query += ") AND datemodif >=" + resource.getDate() + "AND datemodif <= " + datemax + ";";
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
			conn = DriverManager.getConnection(url, dbUser, dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery(query);
			System.out.println("------- Query Executed -------");
			while (r.next()) {
				listOfuid.add(r.getLong("idrel"));
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
		idrelQuery = "SELECT idrel FROM relationmember WHERE idmb = " + resource.getId();
		// System.out.println("resource ID : " + resource.getId() + " - date :"
		// + resource.getDate());

		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
			conn = DriverManager.getConnection(url, dbUser, dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery(idrelQuery);
			System.out.println("------- Query Executed -------");
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

	public static boolean isGeomAddition(List<OSMResource> myJavaObjects, OSMResource resource) {
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

	public static void writeGraph2CSV(DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> usegraph, File file)
			throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(file), ';');
		// write header
		String[] line = new String[3];
		line[0] = "source";
		line[1] = "target";
		line[2] = "weight";
		writer.writeNext(line);
		for (DefaultWeightedEdge e : usegraph.edgeSet()) {
			line = new String[3];
			line[0] = String.valueOf(usegraph.getEdgeSource(e).longValue());
			line[1] = String.valueOf(usegraph.getEdgeTarget(e).longValue());
			line[2] = String.valueOf(usegraph.getEdgeWeight(e));
			writer.writeNext(line);
		}
		writer.close();
	}

}
