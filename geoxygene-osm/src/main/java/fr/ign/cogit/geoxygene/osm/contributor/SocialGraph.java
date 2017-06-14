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

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import au.com.bytecode.opencsv.CSVWriter;
import fr.ign.cogit.geoxygene.osm.importexport.OSMObject;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.OSMWay;
import fr.ign.cogit.geoxygene.osm.importexport.metrics.IntrinsicAssessment;
import fr.ign.cogit.geoxygene.osm.importexport.postgis.LoadFromPostGIS;

public class SocialGraph<V, E> {
	// private static Logger LOGGER = Logger.getLogger(SocialGraph.class);
	public static String host = "localhost";
	public static String port = "5432";
	public static String dbName = "paris";
	public static String dbUser = "postgres";
	public static String dbPwd = "postgres";
	private static double factor = 4;
	int edgeCount = 0;

	public static void main(String[] args) throws Exception {
		LoadFromPostGIS loader = new LoadFromPostGIS("localhost", "5432", "paris", "postgres", "postgres");
		List<Double> bbox = new ArrayList<Double>();
		bbox.add(2.3322);
		bbox.add(48.8489);
		bbox.add(2.3634);
		bbox.add(48.8627);
		// bbox.add(2.3322);
		// bbox.add(48.8509);
		// bbox.add(2.3614);
		// bbox.add(48.8607);
		List<String> timespan = new ArrayList<String>();
		timespan.add("2010-01-01");
		timespan.add("2015-01-01");
		loader.selectNodes(bbox, timespan);
		loader.selectWays(bbox, timespan);
		DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> usegraph = createUseGraph(loader.myJavaObjects);
		writeGraph2CSV(usegraph, new File("paris_usegraph_20100101_20150101.csv"));
		// HashMap<Long, OSMObject> nodeOSMObjects =
		// IntrinsicAssessment.nodeContributionSummary(loader.myJavaObjects);
		// HashMap<Long, OSMObject> wayOSMObjects =
		// IntrinsicAssessment.wayContributionSummary(loader.myJavaObjects);
		// HashMap<Long, OSMContributor> myOSMContributors =
		// ContributorAssessment
		// .contributorSummary(loader.myJavaObjects);

		// DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>
		// coeditionGraph = createCoEditionGraph(nodeOSMObjects,
		// myOSMContributors);
		// System.out.println("nombre de noeuds :" +
		// coeditionGraph.vertexSet().size() + "\n" + "nombre d'arcs : "
		// + coeditionGraph.edgeSet().size());
		// writeGraph2CSV(coeditionGraph, new
		// File("paris_coEditionGraph_20140101_20140201.csv"));

		// DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>
		// wayspatialCollabgraph = createCollaborationGraph(
		// nodeOSMObjects, myOSMContributors, "width");
		// writeGraph2CSV(wayspatialCollabgraph, new
		// File("paris_waywidthcollabgraph_01janvier2011.csv"));
		// mergeGraph(nodespatialCollabgraph, wayspatialCollabgraph);
		// writeGraph2CSV(wayspatialCollabgraph, new
		// File("paris_mergewidthcollabgraph_01janvier2011.csv"));

		/**** Warning: graphs were built from OSM nodes only ***/

		// DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> wcollabg =
		// createCollaborationGraph(nodeOSMObjects,
		// myOSMContributors, "width");
		// writeGraph2CSV(wcollabg, new
		// File("paris_widthCollabGraph_20100101_20100201.csv"));
		//
		// DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> dcollabg =
		// createCollaborationGraph(nodeOSMObjects,
		// myOSMContributors, "depth");
		// writeGraph2CSV(dcollabg, new
		// File("paris_depthCollabGraph_20100101_20100201.csv"));
		//
		// DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> gcollabg =
		// createCollaborationGraph(nodeOSMObjects,
		// myOSMContributors, "global");
		// writeGraph2CSV(gcollabg, new
		// File("paris_globalCollabGraph_20100101_20100201.csv"));
		//
		// DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> comcollabg =
		// createCollaborationGraph(nodeOSMObjects,
		// myOSMContributors, "global");
		// writeGraph2CSV(comcollabg, new
		// File("paris_combinedCollabGraph_factor4_20100101_20100201.csv"));

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
					System.out.println(g.containsEdge(nodeIni, nodeFin));
					if (nodeIni.equals(nodeFin))
						continue;
					if (!g.containsEdge(nodeIni, nodeFin) || !g.containsEdge(nodeFin, nodeIni)) {
						/* DefaultWeightedEdge e = (DefaultWeightedEdge) */g.addEdge(nodeIni, nodeFin);
						// g.setEdgeWeight(e, weight);
					} else {
						// DefaultWeightedEdge e = g.getEdge(nodeIni, nodeFin);
						// g.setEdgeWeight(e, g.getEdgeWeight(e) + weight);
					}
					System.out.println("Ajout d'un arc entre " + nodeIni + " et " + nodeFin);
					// w++;
				}
			}
		}
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
		System.out.println("Nombre de sommets dans le graphe :" + nbVertice);

		// Add edges
		for (OSMObject osmObject : myOSMObjects.values()) {
			subGraph = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			osmObjectGraph = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			sumEdgeGraph = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(DefaultWeightedEdge.class);

			System.out.println("OSMObject en cours :" + osmObject.getOsmId());
			// Récupère le nombre de versions de l'objet étudié
			int lastContributionRange = osmObject.getContributions().size() - 1;
			System.out.println("lastContributionRange :" + lastContributionRange);
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
					System.out.println("j = " + j);
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

	public static DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> createUseGraph(
			List<OSMResource> myJavaObjects) throws Exception {
		DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> g = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		List<Long> listTarget = new ArrayList<Long>();
		for (int i = 0; i < myJavaObjects.size(); i++) {
			Long uidway = (long) myJavaObjects.get(i).getUid();
			// Cherche si l'objet (node ou way) n'est pas réutilisé par
			// quelqu'un d'autre dans une relation
			List<Long> listOfidrel = getRelationMembersIdrel(myJavaObjects.get(i));
			if (!listOfidrel.isEmpty()) {
				List<Long> listOfRelUid = getRelationUid(myJavaObjects.get(i), listOfidrel);
				if (!listOfRelUid.isEmpty())
					for (Long relUid : listOfRelUid)
						if (relUid != uidway)
							addEdgeUseGraph(g, relUid, uidway);
			}
			// Dans le cas d'un way, cherche si les points sont des nodes
			// réutilisés
			if (myJavaObjects.get(i).getGeom().getClass().getSimpleName().equals("OSMWay")) {
				listTarget = selectAuthorsOfUsedNodes(myJavaObjects.get(i));
				for (Long targetID : listTarget) {
					if (targetID != uidway)
						addEdgeUseGraph(g, uidway, targetID);
				}
			}
		}
		return g;
	}

	public static void addEdgeUseGraph(DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge> g, Long idIni,
			Long idFin) {
		System.out.println("idIni = " + idIni + " - idFin = " + idFin + "- égalité = " + (idIni.equals(idFin)));
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

	public static List<Long> getRelationUid(OSMResource resource, List<Long> listOfidrel) throws Exception {
		List<Long> listOfuid = new ArrayList<Long>();
		String query = "SELECT * FROM relation WHERE (idrel = " + listOfidrel.get(0);
		// if (!listOfidrel.isEmpty()) {
		// query = "SELECT * FROM relation WHERE idrel = " + listOfidrel.get(0);
		if (listOfidrel.size() > 1) {
			for (int i = 1; i < listOfidrel.size(); i++) {
				query += " OR idrel = " + listOfidrel.get(i);
			}
		}
		query += ") AND datemodif >=\'" + resource.getDate() + "\';";
		System.out.println(query);
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
			conn = DriverManager.getConnection(url, dbUser, dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery(query);
			System.out.println("------- Query Executed -------");
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
