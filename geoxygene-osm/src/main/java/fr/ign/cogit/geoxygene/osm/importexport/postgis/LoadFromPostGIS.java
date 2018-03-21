/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.osm.importexport.postgis;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import fr.ign.cogit.geoxygene.osm.importexport.OSMNode;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.RoleMembre;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.TypeRelation;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.OSMWay;
import fr.ign.cogit.geoxygene.osm.importexport.OsmRelationMember;
import fr.ign.cogit.geoxygene.osm.importexport.PrimitiveGeomOSM;
import fr.ign.cogit.geoxygene.osm.importexport.metrics.OSMResourceComparator;
import twitter4j.JSONException;
import twitter4j.JSONObject;

public class LoadFromPostGIS {
	public String host;
	public String port;
	public String dbName;
	public String dbUser;
	public String dbPwd;
	public Set<OSMResource> myJavaObjects;
	public Map<OsmRelationMember, Long> OsmRelMbList;
	public Set<OSMResource> myJavaRelations;

	public LoadFromPostGIS(String host, String port, String dbName, String dbUser, String dbPwd) {
		this.host = host;
		this.port = port;
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPwd = dbPwd;
		this.myJavaObjects = new HashSet<OSMResource>();
		this.OsmRelMbList = new HashMap<OsmRelationMember, Long>();
		this.myJavaRelations = new HashSet<OSMResource>();
	}

	/**
	 * Récupère les coordonnées lon_min, lat_min, lon_max, lat_max de la commune
	 * Attention: il faut lancer le script
	 * 
	 * @param city
	 *            Nom de la commune
	 * @param timestamp
	 *            Date à laquelle on cherche la dernière version des frontières
	 * @return les coordonnées géographiques {xmin, ymin, xmax, ymax} de la
	 *         commune
	 * @throws Exception
	 */
	public Double[] getCityBoundary(String city, String timestamp) throws Exception {
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String query = "SELECT idrel FROM  relation WHERE tags -> 'boundary' = 'administrative' ANd tags->'admin_level'='8' "
					+ "AND tags-> 'name'='" + city + "' AND datemodif <= '" + timestamp
					+ "' ORDER BY vrel DESC LIMIT 1;";
			ResultSet r = s.executeQuery(query);
			// Get relation members
			Long idrel = null;
			while (r.next()) {
				idrel = r.getLong("idrel");
			}
			System.out.println("idrel = " + idrel);

			query = "DROP TABLE IF EXISTS enveloppe ;"
					+ "CREATE TABLE enveloppe (lon_min numeric DEFAULT 180,lat_min numeric DEFAULT 90,lon_max numeric DEFAULT -180,lat_max numeric DEFAULT -90);"
					+ "INSERT INTO enveloppe VALUES (180, 90, -180 ,-90);";
			query += "SELECT relation_boundary(" + idrel + ", '" + timestamp + "');"; // run
																						// first
																						// pgScript
																						// "relation_boundary.sql"
			s.execute(query);
			query = "SELECT * FROM enveloppe LIMIT 1;";
			ResultSet r1 = s.executeQuery(query);
			Double xmin = null, ymin = null, xmax = null, ymax = null;
			while (r1.next()) {
				xmin = r1.getDouble("lon_min");
				ymin = r1.getDouble("lat_min");
				xmax = r1.getDouble("lon_max");
				ymax = r1.getDouble("lat_max");
				System.out.println(xmin);
				System.out.println(ymin);
				System.out.println(xmax);
				System.out.println(ymax);
			}

			Double[] borders = { xmin, ymin, xmax, ymax };
			s.close();
			conn.close();
			return borders;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Loads the latest version of every building that was created inside the
	 * input borders. The buildings can be visible or not.
	 * 
	 * @param borders
	 *            {xmin, ymin, xmax, ymax}
	 * @param timestamp
	 *            date du snapshot
	 * @throws Exception
	 */
	public Set<OSMResource> getSnapshotBuilding(Double[] borders, String timestamp) throws Exception {
		// Dernière version de tous les multipolygones en IdF à t1
		getVisibleMultipolygons(timestamp);

		// Dernière version de tous les ways en IdF à t1
		this.getSnapshotWay(borders, timestamp); // Contenu dans myJavaObjects

		Set<OSMResource> latestBuildings = new HashSet<OSMResource>();
		// Parcourt tous les ways pour écrire les batiments et les
		// multipolygones
		int nbSimpleBuildings = 0;
		for (OSMResource way : this.myJavaObjects) {
			boolean isMultipolygonMb = false;
			// Cherche si l'objet est un batiment
			if (way.getTags().containsKey("building")) {
				// On l'ajoute directement s'il est invisible
				if (!way.isVisible()) {
					latestBuildings.add(way);
					nbSimpleBuildings++;
					continue;
				}
				// S'il appartient à un multipolygone, on ajoute l'objet
				// relation (multipolygone)
				for (OSMResource rel : this.myJavaRelations) {
					List<Long> mbIDs = ((OSMRelation) rel.getGeom()).getMembersID();
					if (mbIDs.contains(Long.valueOf(way.getId()))) {
						latestBuildings.add(rel);
						isMultipolygonMb = true;
					}
				}
				// Si c'est un batiment simple (polygone), on l'ajoute
				if (!isMultipolygonMb) {
					latestBuildings.add(way);
					nbSimpleBuildings++;
				}
			}
		}
		// Parcourt toutes les relations pour écrire les batiments
		// multipolygones
		for (OSMResource m : this.myJavaRelations) {
			if (latestBuildings.contains(m))
				continue;
			if (m.getTags().containsKey("building")) {
				for (OSMResource w : this.myJavaObjects) {
					if (latestBuildings.contains(w))
						continue;
					List<Long> mbIDs = ((OSMRelation) m.getGeom()).getMembersID();
					if (mbIDs.contains(Long.valueOf(w.getId())))
						latestBuildings.add(m);
				}
			}
		}
		System.out.println("Nombre total batiments : " + latestBuildings.size() + " - Nombre de multipolygones "
				+ (latestBuildings.size() - nbSimpleBuildings));
		return latestBuildings;
	}

	/**
	 * Get the evolution of the input buildings until a certain date and store
	 * them in myJavaObjects attribute. Multipolygones members are stored in
	 * OsmRelMbList attribute.
	 * 
	 * @param snapshotBuildings:
	 *            set of buildings (OSMRelation or OSMWay)
	 * @param borders
	 * @param timespan
	 * @throws Exception
	 */
	public Set<OSMResource> getEvolutionBuilding(Set<OSMResource> snapshotBuildings, Double[] borders,
			String[] timespan) throws Exception {
		Set<OSMResource> buildingsWithinTimespan = new HashSet<OSMResource>();
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String query = "";
			ResultSet r;
			for (OSMResource building : snapshotBuildings) {
				if (building.getGeom().getClass().getSimpleName().equalsIgnoreCase("OSMWay")) {
					// Query PostGIS table way : select the evolution of
					// existing ways
					query = "SELECT way.id,way.uid,way.vway, way.changeset, way.username, way.datemodif, hstore_to_json(way.tags), way.visible,way.composedof FROM way "
							+ "WHERE id = " + building.getId() + " AND vway > " + building.getVersion()
							+ " AND datemodif <='" + timespan[1] + "';";
					r = s.executeQuery(query);
					while (r.next())
						buildingsWithinTimespan.add(this.writeWay(r));

				}
				if (building.getGeom().getClass().getSimpleName().equalsIgnoreCase("OSMRelation")) {
					String queryRelation = "SELECT idrel FROM relation WHERE id = " + building.getId() + " AND vrel > "
							+ building.getVersion() + " AND datemodif <= '" + timespan[1] + "'";
					// First, query PostGIS table relation member
					query = "SELECT * FROM relationmember r, (" + queryRelation
							+ ") AS later_idrel WHERE r.idrel =  later_idrel.idrel";
					r = s.executeQuery(query);
					this.writeOSMResource(r, "relationmember");
					// Then query PostGIS table relation
					queryRelation = "SELECT idrel, id, uid, vrel, changeset, username, datemodif, hstore_to_json(tags), visible FROM relation"
							+ " WHERE id = " + building.getId() + " AND vrel > " + building.getVersion()
							+ " AND datemodif <= '" + timespan[1] + "'";
					r = s.executeQuery(queryRelation);
					while (r.next())
						buildingsWithinTimespan.add(this.writeRelation(r));
				}

			}
			// Query the evolutions of ways and multipolygons that are created
			// within timespan
			Set<OSMResource> waysCreated = this.getEvolutionWay(borders, timespan);
			Set<OSMResource> multipolygonsCreated = this.getEvolutionMultipolygons(borders, timespan);

			// Parcourt les batiments qui sont dans des multipolygones
			for (OSMResource way : waysCreated) {
				if (way.getTags().containsKey("building")) {
					if (!way.isVisible())
						buildingsWithinTimespan.add(way);
					// On commence par déterminer les dates de validité pour
					// lesquelles considérer les multipolygones auxquels peuvent
					// appartenir les ways
					Iterator<OSMResource> it = waysCreated.iterator();
					Date nextVersion = null;
					while (it.hasNext() && nextVersion == null) {
						OSMResource next = it.next();
						if (next.getDate().after(way.getDate()))
							continue;
						if (next.getId() == way.getId())
							nextVersion = next.getDate();
					}
					// Parcourt des multipolygones appartenant à l'intervalle de
					// validité du way
					boolean isMultipolygonMb = false;
					it = multipolygonsCreated.iterator();
					while (it.hasNext()) {
						OSMResource rel = it.next();
						if (rel.getDate().before(way.getDate()))
							continue;
						if (nextVersion != null)
							if (rel.getDate().after(nextVersion))
								break;
						List<Long> mbIDs = ((OSMRelation) rel.getGeom()).getMembersID();
						if (mbIDs.contains(Long.valueOf(way.getId()))) {
							buildingsWithinTimespan.add(rel);
							isMultipolygonMb = true;
						}
					}
					if (!isMultipolygonMb)
						buildingsWithinTimespan.add(way);
				}
			}
			// Parcourt les batiments multipolygones
			for (OSMResource m : multipolygonsCreated) {
				if (buildingsWithinTimespan.contains(m))
					continue;
				Iterator<OSMResource> it = waysCreated.iterator();
				while (it.hasNext()) {
					OSMResource w = it.next();
					if (w.getDate().after(m.getDate()))
						break;
					if (!w.isVisible())
						continue;
					if (!w.getTags().containsKey("building"))
						continue;
					List<Long> mbIDs = ((OSMRelation) m.getGeom()).getMembersID();
					if (mbIDs.contains(Long.valueOf(w.getId()))) {
						buildingsWithinTimespan.add(m);
						break;
					}
				}
			}

			return buildingsWithinTimespan;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Get the latest OSM relations that are tagged type=multipolygon
	 * 
	 * @param timestamp:
	 *            snapshot date
	 * @throws Exception
	 */
	public Set<OSMResource> getVisibleMultipolygons(String timestamp) throws Exception {
		Set<OSMResource> multipolygons = new TreeSet<OSMResource>(new OSMResourceComparator());
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String queryMaxVrel = "SELECT max(vrel) AS max, id FROM relation WHERE datemodif <='" + timestamp
					+ "' GROUP BY id";
			String visibleAtT1 = "SELECT * FROM relation, (" + queryMaxVrel + ") AS max_vrel "
					+ "WHERE relation.visible IS TRUE AND relation.id = max_vrel.id AND relation.vrel = max_vrel.max AND tags->'type'='multipolygon' ORDER BY relation.id";
			String query = "SELECT r.* FROM relationmember r, (" + visibleAtT1 + ") AS visible_at_t1 "
					+ "WHERE r.idrel = visible_at_t1.idrel AND (r.rolemb = 'inner' OR r.rolemb = 'outer') ORDER BY r.idrel;";
			ResultSet r = s.executeQuery(query);
			r = s.executeQuery(query);
			System.out.println("------- Query Executed -------");

			// Writes all the relation members that were queried
			writeOSMResource(r, "relationmember");

			query = "SELECT relation.idrel, relation.id, relation.uid, relation.vrel, relation.changeset, relation.username, relation.datemodif, hstore_to_json(relation.tags), relation.visible FROM relation, ("
					+ queryMaxVrel + ") AS max_vrel "
					+ "WHERE relation.visible IS TRUE AND relation.id = max_vrel.id AND relation.vrel = max_vrel.max AND tags->'type'='multipolygon' ORDER BY relation.id";
			r = s.executeQuery(query);
			// Writes all the relation that were queried
			while (r.next()) {
				multipolygons.add(this.writeRelation(r));
			}
			s.close();
			conn.close();
			return multipolygons;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Get OSM 'type=multipolygon' relations that are created within timespan
	 * and their later versions until end date
	 * 
	 * @param borders:
	 *            {xmin, ymin, ymax, ymax}
	 * @param timespan:
	 *            {beginDate, endDate}
	 * @return a set of multipolygons
	 * @throws Exception
	 */
	public Set<OSMResource> getEvolutionMultipolygons(Double[] borders, String[] timespan) throws Exception {
		Set<OSMResource> multipolygons = new TreeSet<OSMResource>(new OSMResourceComparator());
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String query = "";
			ResultSet r;
			// Query PostGIS multipolygons' members that are created within
			// timespan
			String queryCreatedRelations = "SELECT idrel FROM relation WHERE datemodif > '" + timespan[0]
					+ "' AND datemodif <= '" + timespan[1] + "' AND tags->'type'='multipolygon' AND vrel = 1";
			query = "SELECT * FROM relationmember r, (" + queryCreatedRelations + ") AS rel_crees "
					+ "WHERE r.idrel = rel_crees.idrel";
			r = s.executeQuery(query);
			this.writeOSMResource(r, "relationmember");

			// Query the corresponding relations
			queryCreatedRelations = "SELECT relation.idrel, relation.id, relation.uid, relation.vrel, relation.changeset, relation.username, relation.datemodif, hstore_to_json(relation.tags), relation.visible "
					+ "FROM relation WHERE datemodif > '" + timespan[0] + "' AND datemodif <= '" + timespan[1]
					+ "' AND tags->'type'='multipolygon' AND vrel = 1";
			r = s.executeQuery(queryCreatedRelations);
			this.writeOSMResource(r, "relation");

			// Query the created multipolygons' evolution until end date
			queryCreatedRelations = "SELECT id FROM relation WHERE datemodif > '" + timespan[0] + "' AND datemodif <= '"
					+ timespan[1] + "' AND tags->'type'='multipolygon' AND vrel = 1";
			query = "SELECT * FROM relation r, (" + queryCreatedRelations
					+ ") WHERE r.id = rel_crees.id AND r.vrel > 1 AND r.datemodif <= '" + timespan[1] + "'";
			query = "SELECT * FROM relationmember r, (" + query + ")  AS rel_evol WHERE r.idrel = rel_evol.idrel ";
			r = s.executeQuery(query);
			this.writeOSMResource(r, "relationmember");

			// Query the corresponding relations
			queryCreatedRelations = "SELECT relation.idrel, relation.id, relation.uid, relation.vrel, relation.changeset, relation.username, relation.datemodif, hstore_to_json(relation.tags), relation.visible"
					+ " FROM relation WHERE datemodif > '" + timespan[0] + "' AND datemodif <= '" + timespan[1]
					+ "' AND tags->'type'='multipolygon' AND vrel = 1";
			r = s.executeQuery(queryCreatedRelations);
			while (r.next())
				multipolygons.add(this.writeRelation(r));
			s.close();
			conn.close();
			return multipolygons;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Get the latest version of all nodes (visible or not) that are contained
	 * inside the borders
	 * 
	 * @param borders
	 * @param timestamp
	 * @throws Exception
	 */
	public Set<OSMResource> getSnapshotNodes(Double[] borders, String timestamp) throws Exception {
		Set<OSMResource> nodeSet = new HashSet<OSMResource>();
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			String uniqueNodeQuery = "SELECT DISTINCT ON (id) * FROM node WHERE lon >= " + borders[0] + "AND lat>= "
					+ borders[1] + "AND lon<= " + borders[2] + "AND lat<= " + borders[3]
					+ " ORDER BY id, datemodif DESC";
			String infoNodeQuery = "SELECT max(node.vnode) as max, node.id FROM node, (" + uniqueNodeQuery
					+ ") AS unique_nodes WHERE node.id = unique_nodes.id AND node.datemodif <='" + timestamp
					+ "' GROUP BY node.id";
			String query = "SELECT node.idnode, node.id,node.uid,node.vnode, node.changeset, node.username, node.datemodif, hstore_to_json(node.tags), node.visible, node.lon, node.lat "
					+ " FROM node, (" + infoNodeQuery
					+ ") AS info_node WHERE node.id=info_node.id AND node.vnode=info_node.max;";

			ResultSet r = s.executeQuery(query);
			System.out.println("------- Query Executed -------");
			while (r.next())
				nodeSet.add(this.writeNode(r));
			s.close();
			conn.close();
			return nodeSet;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Retrieves OSM nodes from a PostGIS database according to spatiotemporal
	 * parameters: 1) select the later versions of all queried nodes at
	 * timespan[0] 2) select all created nodes inside timespan including their
	 * later versions until timespan[1]
	 * 
	 * @param borders
	 * @param timespan
	 * @throws Exception
	 */
	public Set<OSMResource> getEvolutionNode(Double[] borders, String[] timespan) throws Exception {
		Set<OSMResource> nodeSet = new HashSet<OSMResource>();
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			// Query the evolution of all buildings which where selected at
			// timespan[0] until timespan[1]
			String uniqueNodeQuery = "SELECT DISTINCT ON (id) * FROM node WHERE lon >= " + borders[0] + " AND lat>= "
					+ borders[1] + " AND lon<= " + borders[2] + " AND lat<= " + borders[3]
					+ " ORDER BY id, datemodif DESC";
			String infoNodeQuery = "SELECT max(node.vnode) as max, node.id FROM node, (" + uniqueNodeQuery
					+ ") as unique_node WHERE node.id = unique_node.id AND node.datemodif <='" + timespan[0]
					+ "' GROUP BY node.id";
			String query = "SELECT node.idnode, node.id,node.uid,node.vnode, node.changeset, node.username, node.datemodif, hstore_to_json(node.tags), node.visible, node.lon, node.lat "
					+ " FROM node, (" + infoNodeQuery
					+ ") AS info_node WHERE node.id = info_node.id AND node.vnode > info_node.max AND node.datemodif <= '"
					+ timespan[1] + "' ORDER BY node.id;";
			ResultSet r = s.executeQuery(query);
			System.out.println("------- Query Executed -------");
			writeOSMResource(r, "node");

			// Query the buildings that were created inside timespan and all the
			// versions which fall between this time interval
			String createdNodes = "SELECT DISTINCT ON (id) * FROM node WHERE lon >= " + borders[0] + " AND lat>="
					+ borders[1] + " AND lon<=" + borders[2] + " AND lat<=" + borders[3]
					+ "AND vnode = 1 AND datemodif > '" + timespan[0] + "' AND datemodif <= '" + timespan[1] + "'";

			query = "SELECT node.idnode, node.id,node.uid,node.vnode, node.changeset, node.username, node.datemodif, hstore_to_json(node.tags), node.visible, node.lon, node.lat "
					+ "FROM node, (" + createdNodes
					+ ") AS node_cree WHERE node.id = node_cree.id AND node.datemodif <='" + timespan[1] + "';";

			r = s.executeQuery(query);
			System.out.println("------- Query Executed -------");
			while (r.next())
				nodeSet.add(this.writeNode(r));
			s.close();
			conn.close();
			return nodeSet;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Get the latest version of all ways (visible or not) that are contained
	 * inside the borders
	 * 
	 * @param borders
	 * @param timestamp
	 * @throws Exception
	 */
	public Set<OSMResource> getSnapshotWay(Double[] borders, String timestamp) throws Exception {
		Set<OSMResource> waySet = new TreeSet<OSMResource>(new OSMResourceComparator());
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			String uniqueWayQuery = "SELECT DISTINCT ON (id) * FROM way WHERE lon_min >= " + borders[0]
					+ "AND lat_min>= " + borders[1] + "AND lon_max<= " + borders[2] + "AND lat_max<= " + borders[3]
					+ "ORDER BY id, datemodif DESC";
			String infoWayQuery = "SELECT max(way.vway) as max, way.id FROM way, (" + uniqueWayQuery
					+ ") as unique_way WHERE way.id = unique_way.id AND way.datemodif <='" + timestamp
					+ "' GROUP BY way.id";
			String query = "SELECT way.idway, way.id, way.uid, way.vway, way.changeset, way.username, way.datemodif, hstore_to_json(way.tags), way.composedof, way.visible "
					+ "FROM way, (" + infoWayQuery
					+ ") AS info_way WHERE way.id=info_way.id AND way.vway=info_way.max;";

			ResultSet r = s.executeQuery(query);
			System.out.println("------- Query Executed -------");
			while (r.next())
				waySet.add(this.writeWay(r));
			s.close();
			conn.close();
			return waySet;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Get all the ways that are created within a timespan and their evolution
	 * (i.e. later versions) until end date
	 * 
	 * @param borders:
	 *            {xmin, ymin, xmax, ymax}
	 * @param timespan:
	 *            {beginDate, endDate}
	 * @throws Exception
	 */
	public Set<OSMResource> getEvolutionWay(Double[] borders, String[] timespan) throws Exception {
		Set<OSMResource> waySet = new TreeSet<OSMResource>(new OSMResourceComparator());
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String query = "";
			ResultSet r;
			// Query PostGIS table way : select ways that are created within
			// timespan
			String queryCreatedWays = "SELECT way.id,way.uid,way.vway, way.changeset, way.username, way.datemodif, hstore_to_json(way.tags), way.visible,way.composedof FROM way "
					+ "WHERE vway = 1 AND datemodif > '" + timespan[0] + "' AND datemodif <= '" + timespan[1]
					+ "' AND lon_min >= " + borders[0] + " AND lat_min>=" + borders[1] + " AND lon_max<=" + borders[2]
					+ " AND lat_max<=" + borders[3];
			r = s.executeQuery(queryCreatedWays);
			this.writeOSMResource(r, "way");
			// Query the created ways' evolution until end date
			queryCreatedWays = "SELECT id FROM way " + "WHERE vway = 1 AND datemodif > '" + timespan[0]
					+ "' AND datemodif <= '" + timespan[1] + "' AND lon_min >= " + borders[0] + " AND lat_min>="
					+ borders[1] + " AND lon_max<=" + borders[2] + " AND lat_max<=" + borders[3];
			query = "SELECT way.id,way.uid,way.vway, way.changeset, way.username, way.datemodif, hstore_to_json(way.tags), way.visible,way.composedof FROM way, ("
					+ queryCreatedWays + ") AS ways_crees "
					+ "WHERE way.id = ways_crees.id AND way.vway > 1 AND way.datemodif <= '" + timespan[1] + "'";
			r = s.executeQuery(query);
			while (r.next())
				waySet.add(this.writeWay(r));
			s.close();
			conn.close();
			return waySet;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Récupère un snapshot des nodes & ways à t1 et leurs évolutions entre t1
	 * et t2
	 * 
	 * @param bbox
	 * @param timespan
	 * @throws Exception
	 */
	public void getDataFrombbox(Double[] bbox, String[] timespan) throws Exception {
		// Nodes at t1
		// selectNodesInit(bbox, timespan[0].toString());
		// Nodes between t1 and t2
		getEvolutionVisibleNode(bbox, timespan);

		// Ways at t1
		// selectWaysInit(bbox, timespan[0].toString());
		// Ways between t1 and t2
		getEvolutionVisibleWay(bbox, timespan);
	}

	public ArrayList<OsmRelationMember> getRelationMemberList(int idrel) throws Exception {
		String query = "SELECT * FROM relationmember WHERE idrel=" + idrel;
		java.sql.Connection conn;
		ArrayList<OsmRelationMember> members = new ArrayList<OsmRelationMember>();
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery(query);
			System.out.println("------- Query Executed -------");
			while (r.next()) {
				OsmRelationMember mb = writeRelationMember(r);
				members.add(mb);
			}
			s.close();
			conn.close();
		} catch (Exception e) {
			throw e;
		}
		return members;

	}

	public TypeRelation getRelationType(ResultSet r) throws SQLException {
		// Parses hstore_to_json in order to get relation type which is needed
		// to write an OSMRelation
		String value = " ";
		if (!r.getString("hstore_to_json").toString().equalsIgnoreCase("{}")) {
			try {
				JSONObject obj = new JSONObject(r.getString("hstore_to_json"));
				int i = 0;
				while (value == " " && i < obj.names().length()) {
					String key = obj.names().getString(i);
					if (key.toString().equalsIgnoreCase("type")) {
						value = obj.getString(key);
					}
					i++;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(" TypeRelation = " + value);
		return TypeRelation.valueOfTexte(value);

	}

	/**
	 * @deprecated
	 * @param bbox
	 * @param timespan
	 * @throws Exception
	 */
	public void relationEvolution(Double[] bbox, String[] timespan) throws Exception {
		String dropViewQuery = "DROP VIEW IF EXISTS "
				+ "relationmemberselected, numberselectedrelmb, allrelationmember, "
				+ "totalnumberselectedrelmb, idrelinbbox, idinbbox CASCADE;"
				+ "DROP VIEW IF EXISTS relmbcomposedofrel,nbrelmbcomposedofrel,allrelmbcomposedofrel,"
				+ "totalnbrelmbcomposedofrel,idrelofrelinbbox CASCADE;";

		String subqueryNode = "SELECT DISTINCT ON (id) id FROM node WHERE datemodif >\'" + timespan[0].toString()
				+ "\' AND datemodif <= \'" + timespan[1].toString() + "\' AND node.geom && ST_MakeEnvelope("
				+ bbox[0].toString() + "," + bbox[1].toString() + "," + bbox[2].toString() + "," + bbox[3].toString()
				+ ")";

		String subqueryWay = "SELECT DISTINCT ON (id) id FROM way WHERE datemodif >\'" + timespan[0].toString()
				+ "\' AND datemodif <= \'" + timespan[1].toString() + "\' AND lon_min >=" + bbox[0].toString()
				+ " AND lat_min >=" + bbox[1].toString() + " AND lon_max <=" + bbox[1].toString() + " AND lat_max <= "
				+ bbox[3].toString();

		String query = "CREATE VIEW relationmemberselected AS SELECT * FROM relationmember WHERE (idmb in ("
				+ subqueryWay + ") AND typemb = 'Way') OR (idmb in (" + subqueryNode
				+ ") AND typemb = 'Node') ORDER BY idrel;";

		// Compte le nombre de membres sélectionnés par relation
		query += "CREATE VIEW numberselectedrelmb AS "
				+ "SELECT idrel, COUNT(*) FROM relationmemberselected GROUP BY idrel;";

		// Selectionne la totalité des membres des relations précédemment
		// sélectionnées
		query += "CREATE VIEW allrelationmember AS "
				+ "SELECT * FROM relationmember WHERE idrel in (SELECT idrel FROM numberselectedrelmb);";

		// Compte le nombre total de membres pour chaque relation
		// précédemment
		// sélectionnée
		query += "CREATE VIEW totalnumberselectedrelmb AS "
				+ "SELECT idrel, COUNT(*) FROM allrelationmember GROUP BY idrel;";

		// Sélection des relations qui ont tous leurs membres dans la table
		// numberselectedrelmb
		query += "CREATE VIEW idrelinbbox AS " + "SELECT a.* FROM numberselectedrelmb a, totalnumberselectedrelmb b "
				+ "WHERE a.idrel = b.idrel AND a.count = b.count;";

		// Même raisonnement pour les relations composées des relations
		// contenues dans la fenêtre
		query += "CREATE VIEW relmbcomposedofrel AS SELECT * FROM relationmember WHERE idmb in ("
				+ "SELECT DISTINCT ON (id) id FROM relation WHERE datemodif >\'" + timespan[0].toString()
				+ "\' AND datemodif <=\'" + timespan[1].toString() + "\' AND idrel in (SELECT idrel FROM idrelinbbox)"
				+ ");";

		// Compte le nombre de membres sélectionnés par relation
		query += "CREATE VIEW nbrelmbcomposedofrel AS "
				+ "SELECT idrel, COUNT(*) FROM relmbcomposedofrel	GROUP BY idrel;";

		// Selectionne la totalité des membres des relations précédemment
		// sélectionnées
		query += "CREATE VIEW allrelmbcomposedofrel AS "
				+ "SELECT * FROM relationmember WHERE idrel in (SELECT idrel FROM nbrelmbcomposedofrel);";

		// Compte le nombre total de membres pour chaque relation
		// précédemment
		// sélectionnée
		query += "CREATE VIEW totalnbrelmbcomposedofrel AS "
				+ "SELECT idrel, COUNT(*)	FROM allrelationmember GROUP BY idrel;";

		// Sélection des relations qui ont tous leurs membres dans la table
		// nbrelmbcomposedofrel
		query += "CREATE VIEW idrelofrelinbbox AS "
				+ "SELECT a.idrel, a.count FROM nbrelmbcomposedofrel a, totalnbrelmbcomposedofrel b "
				+ "WHERE a.idrel = b.idrel AND a.count = b.count;";

		// Création d'un trigger pour mettre à jour la vue idrelinbbox
		// (attention, lancer un PgScript sur PgAdmin avant)
		query += "DROP TRIGGER IF EXISTS idrelinbbox_trig ON idrelinbbox;";
		query += "CREATE TRIGGER idrelinbbox_trig "
				+ "INSTEAD OF INSERT ON idrelinbbox FOR EACH ROW EXECUTE PROCEDURE miseajour_idrelinbbox();";
		query += "INSERT INTO idrelinbbox SELECT idrel, count FROM idrelofrelinbbox; ";
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			System.out.println(dropViewQuery + query);
			s.executeQuery(dropViewQuery + query);
			System.out.println("------- Query Executed -------");
			s.close();
			conn.close();
		} catch (Exception e) {
			// throw e;
		}

	}

	/**
	 * @deprecated
	 * @param bbox
	 * @param beginDate
	 * @throws Exception
	 */
	public void relationSnapshot(Double[] bbox, String beginDate) throws Exception {
		String dropViewQuery = "DROP VIEW IF EXISTS "
				+ "relationmemberselected, numberselectedrelmb, allrelationmember, "
				+ "totalnumberselectedrelmb, idrelinbbox, relationinbbox, idinbbox CASCADE; "
				+ "DROP VIEW IF EXISTS relmbcomposedofrel,nbrelmbcomposedofrel,allrelmbcomposedofrel,"
				+ "totalnbrelmbcomposedofrel,idrelofrelinbbox CASCADE;"
				+ "DROP TRIGGER IF EXISTS idrelinbbox_trig ON idrelinbbox;";
		// Recuperation des membres de relation de type node ou de type way
		String subqueryNode = "SELECT DISTINCT ON (id) id FROM node WHERE datemodif <= \'" + beginDate
				+ "\' AND node.geom && ST_MakeEnvelope(" + bbox[0].toString() + "," + bbox[1].toString() + ","
				+ bbox[2].toString() + "," + bbox[3].toString() + ") ORDER BY id, vnode DESC";
		String subqueryWay = "SELECT DISTINCT ON (id) id FROM way WHERE datemodif <= \'" + beginDate
				+ "\' AND lon_min >=" + bbox[0].toString() + " AND lat_min >=" + bbox[1].toString() + " AND lon_max <="
				+ bbox[1].toString() + " AND lat_max <= " + bbox[3].toString() + " ORDER BY id, datemodif DESC";

		String query = "CREATE VIEW relationmemberselected AS " + "SELECT * FROM relationmember WHERE (idmb in ("
				+ subqueryWay + ") AND typemb = \'Way\') " + "OR (idmb in (" + subqueryNode
				+ ") AND typemb = 'Node') ORDER BY idrel;";

		// Compte le nombre de membres sélectionnés par relation
		query += "CREATE VIEW numberselectedrelmb AS "
				+ "SELECT idrel, COUNT(*)	FROM relationmemberselected GROUP BY idrel;";

		// Selectionne la totalité des membres des relations précédemment
		// sélectionnées
		query += "CREATE VIEW allrelationmember AS "
				+ "SELECT * FROM relationmember WHERE idrel in (SELECT idrel FROM numberselectedrelmb);";

		// Compte le nombre total de membres pour chaque relation précédemment
		// sélectionnée
		query += "CREATE VIEW totalnumberselectedrelmb AS "
				+ "SELECT idrel, COUNT(*) FROM allrelationmember GROUP BY idrel;";

		// Sélection des relations qui ont tous leurs membres dans la table
		// numberselectedrelmb
		query += "CREATE VIEW idrelinbbox AS "
				+ "SELECT a.* FROM numberselectedrelmb a, totalnumberselectedrelmb b WHERE a.idrel = b.idrel AND a.count = b.count;";

		query += "CREATE VIEW idinbbox AS "
				+ "SELECT DISTINCT ON (id) id FROM relation WHERE idrel in (SELECT idrel FROM idrelinbbox);";

		// Même raisonnement pour les relations composées des relations
		// contenues dans la fenêtre
		query += "CREATE VIEW relmbcomposedofrel AS "
				+ "SELECT * FROM relationmember WHERE idmb in (SELECT id FROM idinbbox);";
		// Compte le nombre de membres sélectionnés par relation
		query += "CREATE VIEW nbrelmbcomposedofrel AS "
				+ "SELECT idrel, COUNT(*) FROM relmbcomposedofrel	GROUP BY idrel;";

		// Selectionne la totalité des membres des relations précédemment
		// sélectionnées
		query += "CREATE VIEW allrelmbcomposedofrel AS "
				+ "SELECT * FROM relationmember WHERE idrel in (SELECT idrel FROM nbrelmbcomposedofrel);";

		// Compte le nombre total de membres pour chaque relation précédemment
		// sélectionnée
		query += "CREATE VIEW totalnbrelmbcomposedofrel AS "
				+ "SELECT idrel, COUNT(*)	FROM allrelationmember GROUP BY idrel;";

		// Sélection des relations qui ont tous leurs membres dans la table
		// nbrelmbcomposedofrel
		query += "CREATE VIEW idrelofrelinbbox AS "
				+ "SELECT a.idrel, a.count FROM nbrelmbcomposedofrel a, totalnbrelmbcomposedofrel b "
				+ "WHERE a.idrel = b.idrel AND a.count = b.count;";

		// Création d'un trigger pour mettre à jour la vue idrelinbbox
		// (attention, lancer un PgScript sur PgAdmin avant)
		query += "CREATE TRIGGER idrelinbbox_trig "
				+ "INSTEAD OF INSERT ON idrelinbbox FOR EACH ROW EXECUTE PROCEDURE miseajour_idrelinbbox();";
		query += "INSERT INTO idrelinbbox SELECT idrel, count FROM idrelofrelinbbox; ";

		// Sélection de la dernière version de chaque relation à la date t1
		// (=2010-01-01 ici)
		query += "CREATE VIEW relationinbbox AS SELECT * FROM "
				+ "(SELECT DISTINCT ON (id) * FROM relation WHERE datemodif <=\'" + beginDate
				+ "\' ORDER BY id,datemodif DESC) as relationselected WHERE id in (SELECT * FROM idinbbox);";

		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			System.out.println(dropViewQuery + query);
			s.executeQuery(dropViewQuery + query);
			System.out.println("------- Query Executed -------");
			s.close();
			conn.close();
		} catch (Exception e) {
			// throw e;
		}
	}

	public void selectFromDB(String query, String osmDataType) throws Exception {
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet r = s.executeQuery(query);
			System.out.println("------- Query Executed -------");
			writeOSMResource(r, osmDataType);
			s.close();
			conn.close();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Gets the latest visible nodes at date beginDate
	 * 
	 * @param beginDate
	 *            : timespan lower boundary
	 * @throws SQLException
	 */
	public void getSnapshotVisibleNode(Double[] bbox, String beginDate) throws SQLException {
		String uniqueNodesQuery = "SELECT DISTINCT ON (id) * FROM node	WHERE lon >=" + bbox[0] + " AND lat>= "
				+ bbox[1] + " AND lon<=" + bbox[2] + " AND lat<=" + bbox[3] + " ORDER BY id, datemodif DESC";
		String infoNodesQuery = "SELECT max(node.vnode) as max, node.id FROM node,(" + uniqueNodesQuery
				+ ") as unique_nodes WHERE node.id = unique_nodes.id AND node.datemodif <='" + beginDate
				+ "' GROUP BY node.id";
		// Query visible attribute
		String query = "SELECT node.idnode, node.id,node.uid,node.vnode, node.changeset, node.username, node.datemodif, hstore_to_json(node.tags), node.visible, node.lon, node.lat FROM node, ("
				+ infoNodesQuery
				+ ") as info_node WHERE node.id=info_node.id AND node.vnode=info_node.max AND node.visible IS TRUE;";
		// Query database
		try {
			selectFromDB(query, "node");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Retrieves OSM nodes from a PostGIS database according to spatiotemporal
	 * parameters: 1) select the later versions of visible nodes at timespan[0]
	 * until timespan[1]; 2) select all created nodes inside timespan including
	 * their later versions until timespan[1]
	 * 
	 * @param bbox
	 *            contains the bounding box coordinates in the following order :
	 *            [minLon, minLat, maxLon, maxLat]
	 * 
	 * @param timespan
	 *            is composed of the begin date and end date written in
	 *            timestamp format
	 * @throws SQLException
	 */
	public void getEvolutionVisibleNode(Double[] bbox, String[] timespan) throws SQLException {
		String uniqueNodesQuery = "SELECT DISTINCT ON (id) * FROM node	WHERE lon >=" + bbox[0] + " AND lat>= "
				+ bbox[1] + " AND lon<=" + bbox[2] + " AND lat<=" + bbox[3] + " ORDER BY id, datemodif DESC";
		String infoNodesQuery = "SELECT max(node.vnode) as max, node.id FROM node,(" + uniqueNodesQuery
				+ ") as unique_nodes WHERE node.id = unique_nodes.id AND node.datemodif <='" + timespan[0]
				+ "' GROUP BY node.id";
		String visibleNodeBeginDate = "SELECT node.* FROM node, (" + infoNodesQuery
				+ ") as info_node WHERE node.id=info_node.id AND node.vnode=info_node.max AND node.visible IS TRUE";
		String query = "SELECT node.idnode, node.id,node.uid,node.vnode, node.changeset, node.username, node.datemodif, hstore_to_json(node.tags), node.visible, node.lon, node.lat FROM node, ("
				+ visibleNodeBeginDate + ") AS visible_node_t1 "
				+ " WHERE node.id = visible_node_t1.id AND node.vnode > visible_node_t1.vnode AND node.datemodif <= '"
				+ timespan[1] + "';";
		// Query database
		try {
			selectFromDB(query, "node");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(query);
		}
		// Query the nodes that were created inside timespan and all the
		// versions which fall between this time interval
		String createdNodes = "SELECT DISTINCT ON (id) * FROM node WHERE lon >= " + bbox[0] + " AND lat>=" + bbox[1]
				+ " AND lon<=" + bbox[2] + " AND lat<=" + bbox[3] + "AND vnode = 1 AND datemodif > '" + timespan[0]
				+ "' AND datemodif <= '" + timespan[1] + "'";

		query = "SELECT node.idnode, node.id,node.uid,node.vnode, node.changeset, node.username, node.datemodif, hstore_to_json(node.tags), node.visible, node.lon, node.lat "
				+ "FROM node, (" + createdNodes + ") AS node_cree WHERE node.id = node_cree.id AND node.datemodif <='"
				+ timespan[1] + "';";
		// Query database
		try {
			selectFromDB(query, "node");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(query);
		}
	}

	/**
	 * Gets the latest visible ways at date beginDate
	 * 
	 * @param bbox
	 * @param beginDate
	 * @throws SQLException
	 */
	public void getSnapshotVisibleWay(Double[] bbox, String beginDate) throws SQLException {
		String uniqueWaysQuery = "SELECT DISTINCT ON (id) * FROM way WHERE lon_min >=" + bbox[0] + " AND lat_min>= "
				+ bbox[1] + " AND lon_max<=" + bbox[2] + " AND lat_max<=" + bbox[3] + " ORDER BY id, datemodif DESC";
		String infoWaysQuery = "SELECT max(way.vway) as max, way.id FROM way,(" + uniqueWaysQuery
				+ ") as unique_ways WHERE way.id = unique_ways.id AND way.datemodif <='" + beginDate
				+ "' GROUP BY way.id";
		// Query visible attribute
		String query = "SELECT way.id,way.uid,way.vway, way.changeset, way.username, way.datemodif, hstore_to_json(way.tags), way.visible,way.composedof FROM way, ("
				+ infoWaysQuery
				+ ") as info_way WHERE way.id=info_way.id AND way.vway=info_way.max AND way.visible IS TRUE;";
		// Query database
		try {
			selectFromDB(query, "way");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Retrieves OSM ways from a PostGIS database according to spatiotemporal
	 * parameters
	 * 
	 * @param bbox
	 *            contains the bounding box coordinates in the following order :
	 *            [minLon, minLat, maxLon, maxLat]
	 * 
	 * @param timespan
	 *            is composed of the begin date and end date written in
	 *            timestamp format
	 * @throws Exception
	 */
	public void getEvolutionVisibleWay(Double[] bbox, String[] timespan) throws Exception {
		// Query later versions of visible ways at begin date
		String uniqueWaysQuery = "SELECT DISTINCT ON (id) * FROM way WHERE lon_min >=" + bbox[0] + " AND lat_min>= "
				+ bbox[1] + " AND lon_min<=" + bbox[2] + " AND lat_min<=" + bbox[3] + " ORDER BY id, datemodif DESC";
		String infoWaysQuery = "SELECT max(way.vway) as max, way.id FROM way,(" + uniqueWaysQuery
				+ ") as unique_ways WHERE way.id = unique_ways.id AND way.datemodif <='" + timespan[0]
				+ "' GROUP BY way.id";
		String visibleWayBeginDate = "SELECT way.* FROM way, (" + infoWaysQuery
				+ ") as info_way WHERE way.id=info_way.id AND way.vway=info_way.max AND way.visible IS TRUE";
		String query = "SELECT way.id,way.uid,way.vway, way.changeset, way.username, way.datemodif, hstore_to_json(way.tags), way.visible, way.composedof FROM way, ("
				+ visibleWayBeginDate + ") AS visible_way_t1 "
				+ " WHERE way.id = visible_way_t1.id AND way.vway > visible_way_t1.vway AND way.datemodif <= '"
				+ timespan[1] + "';";
		// Query database
		try {
			selectFromDB(query, "way");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(query);
			// throw e;
		}
		// Query the nodes that were created inside timespan and all the
		// versions which fall between this time interval
		String createdWays = "SELECT DISTINCT ON (id) * FROM way WHERE lon_min >= " + bbox[0] + " AND lat_min>="
				+ bbox[1] + " AND lon_min<=" + bbox[2] + " AND lat_min<=" + bbox[3] + "AND vway = 1 AND datemodif > '"
				+ timespan[0] + "' AND datemodif <= '" + timespan[1] + "'";

		query = "SELECT way.idway, way.id,way.uid,way.vway, way.changeset, way.username, way.datemodif, hstore_to_json(way.tags), way.visible, way.composedof "
				+ "FROM way, (" + createdWays + ") AS way_cree WHERE way.id = way_cree.id AND way.datemodif <='"
				+ timespan[1] + "';";
		// Query database
		try {
			selectFromDB(query, "way");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @deprecated
	 * @param bbox
	 * @param beginDate
	 * @throws Exception
	 */
	public void selectRelInit(Double[] bbox, String beginDate) throws Exception {

		relationSnapshot(bbox, beginDate);

		// Query database
		try {
			// Selectionne les membres de relation
			String query = "SELECT * FROM relationmember WHERE idrel in (SELECT idrel FROM relationinbbox);";
			// Recherche les membres de relations avant t1 et les écrit
			selectFromDB(query, "relationmember");
			// Recherche dans la table relation et écriture d'OSMRelation
			query = "SELECT idrel, id, uid, vrel, changeset, username, datemodif, hstore_to_json(tags), visible "
					+ "FROM (SELECT DISTINCT ON (id) * FROM relation WHERE datemodif <=\'" + beginDate + "\'"
					+ "ORDER BY id,datemodif DESC) as relationselected WHERE id in (SELECT * FROM idinbbox);";
			selectFromDB(query, "relation");
			// Creates Java object
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			// throw e;
		}

		// Cherche si les relations qui viennent d'être créées sont également
		// des membres de relation qui sont dans la BBOX étudiée

	}

	/**
	 * @deprecated
	 * @param bbox
	 * @param timespan
	 * @throws Exception
	 */
	public void selectRelations(Double[] bbox, String[] timespan) throws Exception {
		relationEvolution(bbox, timespan);

		// Query database
		try {
			// Selectionne les membres de relation
			String query = "SELECT * FROM relationmember WHERE idrel in (SELECT idrel FROM idrelinbbox);";
			// Recherche les membres de relations avant t1 et les écrit
			selectFromDB(query, "relationmember");
			// Recherche dans la table relation et écriture d'OSMRelation
			query = "SELECT idrel, id, uid, vrel, changeset, username, datemodif, hstore_to_json(tags), visible FROM "
					+ "(SELECT DISTINCT ON (id) * FROM relation WHERE datemodif >\'" + timespan[0].toString()
					+ "\' AND datemodif <= \'" + timespan[1].toString() + "\') as relationselected"
					+ " WHERE idrel in (SELECT idrel FROM idrelinbbox);";
			selectFromDB(query, "relation");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}

	}

	public OSMResource writeNode(ResultSet r) throws SQLException {
		// Set<OSMResource> myNodeResource = new HashSet<OSMResource>();
		System.out.println("Writing node...");
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");
		Date date = null;
		try {
			date = formatDate.parse(r.getString("datemodif"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// System.out.println(r.getString("datemodif"));
		OSMResource myOsmResource = new OSMResource(r.getString("username"),
				new OSMNode(r.getDouble("lat"), r.getDouble("lon")), r.getLong("id"), r.getInt("changeset"),
				r.getInt("vnode"), r.getInt("uid"), date);
		// Visible : peut être null
		myOsmResource.setVisible(r.getBoolean("visible"));
		// Add tags if exist
		if (!r.getString("hstore_to_json").toString().equalsIgnoreCase("{}")) {
			try {
				JSONObject obj = new JSONObject(r.getString("hstore_to_json"));
				for (int i = 0; i < obj.names().length(); i++) {
					String key = obj.names().getString(i);
					String value = obj.getString(key);
					System.out.println(" Ajout du tag {" + key + ", " + value + "}");
					myOsmResource.addTag(key, value);
					if (key.equalsIgnoreCase("source")) {
						myOsmResource.setSource(value);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// this.myJavaObjects.add(myOsmResource);
		// myNodeResource.add(myOsmResource);
		System.out.println("Java object created ! " + "\nid = " + myOsmResource.getId() + "\nusername = "
				+ myOsmResource.getContributeur() + "     uid = " + myOsmResource.getUid() + "\nvnode = "
				+ myOsmResource.getVersion() + "\ndate = " + myOsmResource.getDate() + "   Changeset = "
				+ myOsmResource.getChangeSet());
		System.out.println("-------------------------------------------");
		return myOsmResource;
	}

	public void writeOSMResource(ResultSet r, String osmDataType) throws Exception {

		while (r.next()) {
			System.out.println("writeOSMResource");
			if (osmDataType.equalsIgnoreCase("node")) {
				System.out.println("Writing resource with type node...");
				System.out.println("r.next is a node");
				this.myJavaObjects.add(writeNode(r));
			}
			if (osmDataType.equalsIgnoreCase("way")) {
				System.out.println("Writing resource with type way...");
				this.myJavaObjects.add(writeWay(r));
			}
			if (osmDataType.equalsIgnoreCase("relation")) {
				System.out.println("Writing resource with type relation...");
				// writeRelation(r);
				this.myJavaObjects.add(writeRelation(r));
			}
			if (osmDataType.equalsIgnoreCase("relationmember")) {
				this.OsmRelMbList.put(writeRelationMember(r), r.getLong("idrel"));

			}
		}
	}

	public OsmRelationMember writeRelationMember(ResultSet r) throws Exception {
		boolean isNode = r.getString("typeMb").toLowerCase().equalsIgnoreCase("node");
		boolean isWay = r.getString("typeMb").toLowerCase().equalsIgnoreCase("way");
		boolean isRelation = r.getString("typeMb").toLowerCase().equalsIgnoreCase("relation");
		Long idmb = r.getLong("idmb");
		if (r.getString("rolemb").toLowerCase().equalsIgnoreCase("outer")) {
			OsmRelationMember osmRelMb = new OsmRelationMember(RoleMembre.OUTER, isNode, isWay, isRelation, idmb);
			System.out.println("writeRelationMember(r)");
			return osmRelMb;

		} else if (r.getString("rolemb").toLowerCase().equalsIgnoreCase("inner")) {
			OsmRelationMember osmRelMb = new OsmRelationMember(RoleMembre.INNER, isNode, isWay, isRelation, idmb);
			System.out.println("writeRelationMember(r)");
			return osmRelMb;

		} else {
			OsmRelationMember osmRelMb = new OsmRelationMember(RoleMembre.NON_DEF, isNode, isWay, isRelation, idmb);
			System.out.println("writeRelationMember(r)");
			return osmRelMb;
		}
	}

	public OSMResource writeRelation(ResultSet r) throws Exception {
		// Get date
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");
		Date date = null;
		try {
			date = formatDate.parse(r.getString("datemodif"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// Crée une liste de membres de relation: parcourt OsmRelMbList
		ArrayList<OsmRelationMember> mbList = new ArrayList<OsmRelationMember>();
		for (OsmRelationMember mb : OsmRelMbList.keySet()) {
			// System.out.println("ID : " + r.getString("id") + " Version : " +
			// r.getString("vrel") + "Concaténation "
			// + Long.valueOf(r.getString("id") + r.getString("vrel")));
			if (OsmRelMbList.get(mb).equals(Long.valueOf(r.getString("id") + r.getString("vrel"))))
				mbList.add(mb);
		}

		// Récupère le type de relation (MULTIPOLYGON ou NON_DEF)
		TypeRelation relType = getRelationType(r);
		PrimitiveGeomOSM myRelation = new OSMRelation(relType, mbList);

		// Creates a new OSMResource
		OSMResource myOsmResource = new OSMResource(r.getString("username"), myRelation, r.getLong("id"),
				r.getInt("changeset"), r.getInt("vrel"), r.getInt("uid"), date);
		// Visible : peut être null
		myOsmResource.setVisible(r.getBoolean("visible"));
		// Add tags if exist
		if (!r.getString("hstore_to_json").toString().equalsIgnoreCase("{}")) {
			try {
				JSONObject obj = new JSONObject(r.getString("hstore_to_json"));
				for (int i = 0; i < obj.names().length(); i++) {
					String key = obj.names().getString(i);
					String value = obj.getString(key);
					System.out.println(" Ajout du tag {" + key + ", " + value + "}");
					myOsmResource.addTag(key, value);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Java object created ! " + "\nid = " + myOsmResource.getId() + "\nusername = "
				+ myOsmResource.getContributeur() + "     uid = " + myOsmResource.getUid() + "\nvrel = "
				+ myOsmResource.getVersion() + "\ndate = " + myOsmResource.getDate() + "   Changeset = "
				+ myOsmResource.getChangeSet());
		this.myJavaRelations.add(myOsmResource);

		System.out.println("-------------------------------------------");
		return myOsmResource;

	}

	public OSMResource writeWay(ResultSet r) throws SQLException {
		System.out.println("Writing way...");
		Long[] nodeWay = (Long[]) r.getArray("composedof").getArray();
		for (long i : nodeWay) {
			System.out.println("nodeWay[i] = " + i);
		}
		List<Long> composedof = Arrays.asList(nodeWay);

		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");
		Date date = null;
		try {
			date = formatDate.parse(r.getString("datemodif"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		OSMResource myOsmResource = new OSMResource(r.getString("username"), new OSMWay(composedof), r.getLong("id"),
				r.getInt("changeset"), r.getInt("vway"), r.getInt("uid"), date);
		// Visible : peut être null
		myOsmResource.setVisible(r.getBoolean("visible"));
		// Add tags if exist
		if (!r.getString("hstore_to_json").toString().equalsIgnoreCase("{}")) {
			try {
				JSONObject obj = new JSONObject(r.getString("hstore_to_json"));
				for (int i = 0; i < obj.names().length(); i++) {
					String key = obj.names().getString(i);
					String value = obj.getString(key);
					System.out.println(" Ajout du tag {" + key + ", " + value + "}");
					myOsmResource.addTag(key, value);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// this.myJavaObjects.add(myOsmResource);
		System.out.println("Java object created ! " + "\nid = " + myOsmResource.getId() + "\nusername = "
				+ myOsmResource.getContributeur() + "     uid = " + myOsmResource.getUid() + "\nvway = "
				+ myOsmResource.getVersion() + "\ndate = " + myOsmResource.getDate() + "   Changeset = "
				+ myOsmResource.getChangeSet());
		System.out.println("-------------------------------------------");
		return myOsmResource;
	}

}