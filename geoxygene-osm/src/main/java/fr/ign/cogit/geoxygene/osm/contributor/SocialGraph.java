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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.threeten.extra.Interval;

import au.com.bytecode.opencsv.CSVWriter;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.osm.importexport.OSMObject;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.OSMWay;
import fr.ign.cogit.geoxygene.osm.importexport.metrics.IntrinsicAssessment;
import fr.ign.cogit.geoxygene.osm.importexport.postgis.LoadFromPostGIS;

public class SocialGraph<V, E> {
	// private static Logger LOGGER = Logger.getLogger(SocialGraph.class);
	public static String host = "localhost";
	public static String port = "5432";
	public static String dbName = "idf";
	public static String dbUser = "postgres";
	public static String dbPwd = "postgres";
	private static double factor = 4;
	int edgeCount = 0;

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
				Long nodeIni = (long) osmObject.getContributions().get(i).getUid();
				for (int j = i - 1; j >= 0; j--) {
					Long nodeFin = (long) osmObject.getContributions().get(j).getUid();
					// System.out.println(g.containsEdge(nodeIni, nodeFin));
					if (nodeIni.equals(nodeFin))
						continue;
					if (!g.containsEdge(nodeIni, nodeFin) || !g.containsEdge(nodeFin, nodeIni)) {
						g.addEdge(nodeIni, nodeFin);
					} else {

					}
				}
			}
		}
		return g;
	}

	public static SimpleWeightedGraph<Long, DefaultWeightedEdge> createCoContribGraph(
			HashMap<Long, OSMObject> myOSMObjects, HashMap<Long, OSMContributor> myContributors) throws IOException {
		SimpleWeightedGraph<Long, DefaultWeightedEdge> g = new SimpleWeightedGraph<Long, DefaultWeightedEdge>(
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
		// int nbVertice = myContributors.size();
		// System.out.println("Nombre de sommets dans le graphe :" + nbVertice);

		// Add edges
		for (OSMObject osmObject : myOSMObjects.values()) {
			// On se restreint à la collaboration sur les noeuds seulement pour
			// l'instant
			if (osmObject.getContributions().get(0).getGeom().getClass().getSimpleName().equals("OSMWay"))
				continue;

			subGraph = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			osmObjectGraph = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			sumEdgeGraph = new DefaultDirectedWeightedGraph<Long, DefaultWeightedEdge>(DefaultWeightedEdge.class);

			// System.out.println("OSMObject en cours :" +
			// osmObject.getOsmId());
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
					// System.out.println("j = " + j);
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

		// System.out.println(query);
		java.sql.Connection conn;
		String[][] table;
		try {
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
			conn = DriverManager.getConnection(url, dbUser, dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery(query);
			// System.out.println("------- Query Executed -------");
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
		// System.out.println(query);
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
			conn = DriverManager.getConnection(url, dbUser, dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery(query);
			// System.out.println("------- Query Executed -------");
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
			// System.out.println("------- Query Executed -------");
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
			List<Long> previousComposition = ((OSMWay) previousResource.getGeom()).getVertices();
			List<Long> currentComposition = ((OSMWay) resource.getGeom()).getVertices();
			if (!previousComposition.containsAll(currentComposition))
				isGeomAddition = true;
		}
		return isGeomAddition;
	}

	/**
	 * 
	 * @param myJavaObjects
	 *            set of contributions
	 * @param resource
	 * @return previous version of the object in the set of contributions
	 */
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

	public static SimpleWeightedGraph<Long, DefaultWeightedEdge> createCoTemporalGraph(
			HashMap<Long, OSMContributor> myOSMContributors, String[] timespan) throws Exception {
		SimpleWeightedGraph<Long, DefaultWeightedEdge> g = new SimpleWeightedGraph<Long, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		// Assign changeset dates for each user
		assignChangesets(myOSMContributors, timespan);
		List<OSMContributor> myContributorList = new ArrayList<OSMContributor>();
		for (OSMContributor contributor : myOSMContributors.values()) {
			myContributorList.add(contributor);
		}
		// Parse contributor list
		for (int i = 0; i < myContributorList.size() - 1; i++) {
			List<Interval> changesetsCurrentContributor = myContributorList.get(i).getChangesetDates();
			if (changesetsCurrentContributor == null)
				continue;
			if (!g.containsVertex((long) myContributorList.get(i).getId()))
				g.addVertex((long) myContributorList.get(i).getId());

			// Parcourt les contributeurs suivants de la liste
			for (int j = i + 1; j < myOSMContributors.size(); j++) {
				if (!g.containsVertex((long) myContributorList.get(j).getId()))
					g.addVertex((long) myContributorList.get(j).getId());

				List<Interval> changesetsNextContributor = myContributorList.get(j).getChangesetDates();
				if (changesetsNextContributor == null)
					continue;
				double totalDistance = 0;
				double totalIntersection = 0;
				double totalUnion = 0;
				// Parcourt changesets du contributeur courant
				for (int u = 0; u < changesetsCurrentContributor.size(); u++) {
					Interval currentChangeset = changesetsCurrentContributor.get(u);
					totalUnion += currentChangeset.toDuration().getSeconds();

					// Parcourt les changesets du contributeur suivant
					for (int v = 0; v < changesetsNextContributor.size(); v++) {
						Interval nextChangeset = changesetsNextContributor.get(v);

						// Calcul de la distance surfacique s'il y a
						// intersection
						if (currentChangeset.overlaps(nextChangeset)) {

							System.out.println("Changeset : " + currentChangeset.getStart().toString() + " - "
									+ currentChangeset.getEnd().toString());
							System.out.println("Changeset : " + nextChangeset.getStart().toString() + " - "
									+ nextChangeset.getEnd().toString());
							System.out.println("Intersection = "
									+ currentChangeset.intersection(nextChangeset).getStart().toString() + " - "
									+ currentChangeset.intersection(nextChangeset).getEnd().toString());
							totalIntersection += currentChangeset.intersection(nextChangeset).toDuration().getSeconds();
							System.out.println("Intersection : "
									+ currentChangeset.intersection(nextChangeset).toDuration().getSeconds()
									+ " secondes");

							// Ajoute la différence de nextChangeset avec
							// currentChangeset (qui a été ajouté intialement)
							if (currentChangeset.encloses(nextChangeset) || nextChangeset.encloses(currentChangeset))
								continue;
							if (currentChangeset.contains(nextChangeset.getStart())
									&& !currentChangeset.contains(nextChangeset.getEnd()))
								totalUnion += Interval.of(currentChangeset.getEnd(), nextChangeset.getEnd())
										.toDuration().getSeconds();
							if (!currentChangeset.contains(nextChangeset.getStart())
									&& currentChangeset.contains(nextChangeset.getEnd()))
								totalUnion += Interval.of(nextChangeset.getStart(), nextChangeset.getStart())
										.toDuration().getSeconds();
						} else { // Cas où les deux intervalles sont disjoints
							totalUnion += nextChangeset.toDuration().getSeconds();

						}
					}

				}
				if (totalUnion > 0)
					totalDistance = 1 - totalIntersection / totalUnion;
				if (totalDistance > 0 && totalDistance < 1) {
					DefaultWeightedEdge e = g.addEdge((long) myContributorList.get(i).getId(),
							(long) myContributorList.get(j).getId());
					g.setEdgeWeight(e, 1 / totalDistance);

					System.out.println("Poids de l'arc créé entre " + myContributorList.get(i).getId() + " et "
							+ myContributorList.get(j).getId() + " : " + (1 / totalDistance));

				}

			}
		}

		return g;
	}

	/**
	 * Computes for each OSMContributor of the input list of contributors the
	 * changesets
	 **/
	public static void assignChangesets(HashMap<Long, OSMContributor> myOSMContributors, String[] timespan)
			throws Exception {
		LoadFromPostGIS loader = new LoadFromPostGIS(SocialGraph.host, SocialGraph.port, SocialGraph.dbName,
				SocialGraph.dbUser, SocialGraph.dbPwd);
		for (Long uid : myOSMContributors.keySet()) {
			List<Interval> changesets = loader.getChangesets(uid, timespan);
			myOSMContributors.get(uid).setChangesetDates(changesets);
		}
	}

	public static SimpleWeightedGraph<Long, DefaultWeightedEdge> createCoLocationGraph(
			HashMap<Long, OSMContributor> myOSMContributors, Double[] bbox, String[] timespan, double threshold)
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
				double totalIntersection = 0;
				double totalUnion = 0;
				// Parcourt les zones d'activité du contributeur courant
				for (int u = 0; u < areaCurrentContributor.size(); u++) {
					IGeometry currentZone = areaCurrentContributor.get(u).getGeom();

					totalUnion += currentZone.area();
					// Parcourt les zones d'activité du contributeur suivant
					for (int v = 0; v < areaNextContributor.size(); v++) {
						IGeometry nextZone = areaNextContributor.get(v).getGeom();

						// Calcul de la distance surfacique s'il y a
						// intersection
						if (currentZone.intersects(nextZone)) {
							totalIntersection += currentZone.intersection(nextZone).area();
							totalUnion += nextZone.difference(currentZone).area();

						} else {
							totalUnion += nextZone.area();
						}
					}

				}
				if (totalUnion > 0)
					totalDistance = 1 - totalIntersection / totalUnion;
				if (totalDistance < 1) {
					DefaultWeightedEdge e = g.addEdge((long) myContributorList.get(i).getId(),
							(long) myContributorList.get(j).getId());
					g.setEdgeWeight(e, 1 / totalDistance);
				}

			}
		}

		return g;
	}

	/**
	 * Computes for each OSMContributor of the input list of contributors the
	 * activity areas
	 **/
	public static void assignActivityAreas(HashMap<Long, OSMContributor> myOSMContributors, Double[] bbox,
			String[] timespan, double threshold) throws Exception {
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
		// System.out.println("écriture du fichier csv");
		// write header
		String[] line = new String[3];
		line[0] = "source";
		line[1] = "target";
		line[2] = "weight";
		writer.writeNext(line);
		for (DefaultWeightedEdge e : usegraph.edgeSet()) {
			line = new String[3];
			line[0] = String.valueOf(usegraph.getEdgeSource(e).longValue() + (long) 111);
			System.out.println(usegraph.getEdgeSource(e));
			line[1] = String.valueOf(usegraph.getEdgeTarget(e).longValue() + (long) 111);
			System.out.println(usegraph.getEdgeTarget(e));
			line[2] = String.valueOf(usegraph.getEdgeWeight(e));
			System.out.println(usegraph.getEdgeWeight(e));
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
