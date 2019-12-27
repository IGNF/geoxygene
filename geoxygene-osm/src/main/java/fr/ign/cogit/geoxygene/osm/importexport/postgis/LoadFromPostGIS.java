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

import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.postgresql.util.PSQLException;
import org.threeten.extra.Interval;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.ign.cogit.geoxygene.osm.anonymization.db.SQLDBPreAnonymization;
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

/**
 * A class for retrieving OSM data from a PostGIS database.
 * 
 * @author QTTruong
 *
 */
public class LoadFromPostGIS {
	public String host;
	public String port;
	public String dbName;
	public String dbUser;
	public String dbPwd;
	public Set<OSMResource> myJavaObjects;
	public Map<Long, List<OsmRelationMember>> OsmRelMbList;
	public Set<OSMResource> myJavaRelations;

	public LoadFromPostGIS(String host, String port, String dbName, String dbUser, String dbPwd) {
		this.host = host;
		this.port = port;
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPwd = dbPwd;
		this.myJavaObjects = new HashSet<OSMResource>();
		this.OsmRelMbList = new HashMap<Long, List<OsmRelationMember>>();
		this.myJavaRelations = new HashSet<OSMResource>();
	}

	/**
	 * Récupère les coordonnées lon_min, lat_min, lon_max, lat_max d'une commune
	 * donnée, à partir de la fonction PL/pgSQL relation_boundary (lancer
	 * pgScript sur le fichier relation_boundary.sql).
	 * 
	 * @param city
	 *            : Nom de la commune
	 * @param timestamp
	 *            : Date à laquelle on cherche la dernière version des
	 *            frontières
	 * @return les coordonnées géographiques {xmin, ymin, xmax, ymax} de la
	 *         commune
	 * @throws Exception
	 */
	public Double[] getCityBoundary(String city, String timestamp) throws Exception {
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			Connection conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String query = "SELECT idrel FROM  relation WHERE tags -> 'boundary' = 'administrative' ANd tags->'admin_level'='8' "
					+ "AND tags-> 'name'='" + city + "' AND datemodif <= '" + timestamp
					+ "' ORDER BY vrel DESC LIMIT 1;";
			System.out.println(query);
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
	 * Récupère les coordonnées de limites administratives (Version généralisée
	 * de getCityBoundary au département, ou au pays), à partir de la fonction
	 * PL/pgSQL relation_boundary.
	 * 
	 * @param placeName
	 *            : nom d'une ville, d'un département, d'un pays, d'un village,
	 *            etc..
	 * @param timestamp
	 *            : snapshot date
	 * @return
	 * @throws Exception
	 */
	public Double[] getBoundary(String placeName, String timestamp, String adminLevel) throws Exception {
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s;
			ResultSet r;

			s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			String query = "SELECT idrel FROM  relation WHERE tags -> 'boundary' = 'administrative' ANd tags->'admin_level'='"
					+ adminLevel + "' " + "AND tags-> 'name'='" + placeName + "' AND datemodif <= '" + timestamp
					+ "' ORDER BY vrel DESC LIMIT 1;";
			r = s.executeQuery(query);
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
	 * Get SQL query results.
	 * 
	 * @param query
	 *            : SQL query
	 * @return
	 */
	public ResultSet executeQuery(String query) {
		Connection conn;
		ResultSet r = null;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			r = s.executeQuery(query);
		} catch (Exception e) {
			// throw e;
		}
		return r;

	}

	/**
	 * Execute any SQL query.
	 * 
	 * @param query
	 *            : SQL query
	 * @return
	 */
	public void executeAnyQuery(String query) {
		Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			s.execute(query);
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	/**
	 * Filter OSMResource data which are described by specific tags.
	 * 
	 * @param keyTagslist
	 *            : a list of tag keys
	 * @return a set of OSMResource objects that contain at least one of the
	 *         tags input in keyTagslist
	 */
	public Set<OSMResource> filterByTags(String[] keyTags) {
		Set<OSMResource> filteredData = new HashSet<OSMResource>();
		for (OSMResource r : this.myJavaObjects) {
			for (String s : keyTags) {
				if (r.getTags().containsKey(s)) {
					filteredData.add(r);
					break;
				}
			}
		}
		return filteredData;
	}

	/**
	 * Get the begin date and end date of a changeset.
	 * 
	 * @param uid
	 *            : user identifier
	 * @param timespan
	 *            : temporal interval in which the changesets are queried
	 * @return a list of changeset intervals, corresponding to created_at and
	 *         closed_at attributes from changeset PostGIS table
	 * 
	 * @throws Exception
	 */
	public List<Interval> getChangesets(Long uid, String[] timespan) throws Exception {
		List<Interval> changesets = new ArrayList<Interval>();
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			String query = "SELECT created_at, closed_at FROM changeset WHERE " + "uid = " + uid
					+ " AND created_at >= '" + timespan[0] + "' AND closed_at < '" + timespan[1]
					+ "' ORDER BY created_at";

			ResultSet r = s.executeQuery(query);
			System.out.println("------- Query Executed -------");
			DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");

			while (r.next()) {
				Date beginDate = null;
				Date endDate = null;
				try {
					beginDate = formatDate.parse(r.getString("created_at"));
					endDate = formatDate.parse(r.getString("closed_at"));
					Instant beginInstant = null;
					Instant endInstant = null;
					beginInstant = beginDate.toInstant();
					endInstant = endDate.toInstant();
					// System.out.println(
					// "Date to instant (begin) : " + beginDate.toString() + "
					// -> " + beginInstant.toString());
					// System.out
					// .println("Date to instant (end) : " + endDate.toString()
					// + " -> " + endInstant.toString());
					if (beginInstant.isBefore(endInstant)) {
						Interval interval = Interval.of(beginInstant, endInstant);
						changesets.add(interval);
					}

				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
			s.close();
			conn.close();
			// return invisible;
		} catch (Exception e) {
			throw e;
		}
		return changesets;
	}

	/**
	 * Load the latest version of OSM buildings that are located inside a
	 * bounding box. The buildings can be visible or not.
	 * 
	 * @param borders
	 *            : list of coordinates {xmin, ymin, xmax, ymax} of the study
	 *            area
	 * @param timestamp
	 *            : snapshot date
	 * @throws Exception
	 */
	public Map<Long, OSMResource> getSnapshotBuilding(Double[] borders, String timestamp) throws Exception {
		Map<Long, OSMResource> latestBuildings = new HashMap<Long, OSMResource>();

		// Dernière version de tous les ways à t1 à l'intérieur de la fenêtre
		// spatiale
		getSnapshotVisibleWay(borders, timestamp);
		System.out.println("nombre de ways visibles " + this.myJavaObjects.size());

		// int nbSimpleBuildings = 0;
		for (OSMResource way : this.myJavaObjects) {
			// System.out.println("Way ID : " + way.getId());
			// System.out.println("---- Tags --- ");
			if (way.getTags().containsKey("building"))
				// for (String key : way.getTags().keySet()) {
				// System.out.println("Key " + key + " - Value : " +
				// way.getTags().get(key));
				// }
				// System.out.println("Way ID : " + way.getId());
				// if (way.getTags().containsKey("buiding")) {
				// System.out.println("Le way contient le tag 'building' ");
				latestBuildings.put(way.getId(), way);
			// nbSimpleBuildings++;
			// }
		}
		Map<Long, OSMResource> indexedWays = new HashMap<Long, OSMResource>();
		for (OSMResource w : this.myJavaObjects)
			indexedWays.put(w.getId(), w);
		System.out.println("Nombre de ways chargés " + indexedWays.size());

		// Dernière version de tous les multipolygones en IdF à t1
		Set<OSMResource> latestMultipolygons = getBuildingRelations(timestamp);
		for (OSMResource rel : latestMultipolygons) {
			List<OsmRelationMember> outer = ((OSMRelation) rel.getGeom()).getOuterMembers();
			// System.out.println("outer.size() = " + outer.size());
			if (outer.size() != 1)
				continue;
			// System.out.println(" Outer member ID = " +
			// outer.get(0).getRef());
			if (indexedWays.keySet().contains(outer.get(0).getRef())) {
				System.out.println("Relation " + rel.getId() + " est dans la ville ");
				latestBuildings.remove(outer.get(0).getRef());
				latestBuildings.put(rel.getId(), rel);
				// indexedWays.remove(outer.get(0).getRef());
			}
		}
		// System.out.println("Nombre de ways restants " + indexedWays.size());

		System.out.println("Nombre total batiments : " + latestBuildings.size());
		return latestBuildings;
	}

	/**
	 * Get OSM buildings which attribute 'visible=false'.
	 * 
	 * @param borders
	 *            : list of coordinates {xmin, ymin, xmax, ymax} of the study
	 *            area
	 * @param timestamp
	 *            : snapshot date
	 * @return the set of invisible buildings
	 * @throws Exception
	 */
	public Set<OSMResource> getInvisibleBuilding(Double[] borders, String timestamp) throws Exception {
		Set<OSMResource> invisible = new HashSet<OSMResource>();
		Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			String uniqueWayQuery = "SELECT DISTINCT ON (id) * FROM way WHERE tags ?& ARRAY['building'] "
					+ "AND lon_min >= " + borders[0] + "AND lat_min>= " + borders[1] + "AND lon_max<= " + borders[2]
					+ "AND lat_max<= " + borders[3] + "ORDER BY id, datemodif DESC";
			String infoWayQuery = "SELECT max(way.vway) as max, way.id FROM way, (" + uniqueWayQuery
					+ ") as unique_way WHERE way.id = unique_way.id AND way.datemodif <='" + timestamp
					+ "' AND way.visible IS FALSE GROUP BY way.id";
			String query = "SELECT way.idway, way.id, way.uid, way.vway, way.changeset, way.username, way.datemodif, hstore_to_json(way.tags), way.composedof, way.visible "
					+ "FROM way, (" + infoWayQuery
					+ ") AS info_way WHERE way.id=info_way.id AND way.vway=info_way.max;";

			ResultSet r = s.executeQuery(query);
			System.out.println("------- Query Executed -------");
			while (r.next())
				invisible.add(this.writeWay(r));
			s.close();
			conn.close();
			return invisible;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Get the latest OSM relations that are tagged 'type=multipolygon'
	 * 
	 * @param timestamp
	 *            : snapshot date
	 * @throws Exception
	 */
	public Set<OSMResource> getVisibleMultipolygons(String timestamp) throws Exception {
		Set<OSMResource> multipolygons = new HashSet<OSMResource>();
		Connection conn;
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
	 * Get buildings that are mapped as an OSM relation element.
	 * 
	 * @param timestamp
	 *            : snapshot date
	 * @throws Exception
	 */
	public Set<OSMResource> getBuildingRelations(String timestamp) throws Exception {
		Set<OSMResource> buildings = new HashSet<OSMResource>();
		Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			// Get the latest version of the buildings at the snapshot date
			String queryUniqueRel = "SELECT max(vrel) AS max, id FROM relation WHERE tags ?& ARRAY [ 'building' ] AND datemodif <= '"
					+ timestamp + "' GROUP BY id";
			String multipolygon = "SELECT idrel FROM relation r , (" + queryUniqueRel + ") AS unique_rel  "
					+ "WHERE r.id=unique_rel.id AND r.vrel=unique_rel.max";
			// Get the relation members of the buildings mapped as relations
			String query = "SELECT r.* FROM relationmember r, (" + multipolygon + ") AS multipolygon "
					+ "WHERE r.idrel = multipolygon.idrel";
			ResultSet r = s.executeQuery(query);
			// Writes all the relation members that were queried
			writeOSMResource(r, "relationmember");

			for (Long idrel : this.OsmRelMbList.keySet()) {
				query = "SELECT relation.idrel, relation.id, relation.uid, relation.vrel, relation.changeset, relation.username, relation.datemodif, hstore_to_json(relation.tags), relation.visible"
						+ " FROM relation WHERE idrel = " + idrel;
				r = s.executeQuery(query);
				// Writes all the relations that were queried
				while (r.next()) {
					buildings.add(this.writeRelation(r));
				}
			}
			s.close();
			conn.close();

			return buildings;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Get the history of OSM relation elements (tagged 'type=multipolygon') in
	 * a spatio-temporal area. => Intégrer le test spatial avec la fonction
	 * isInSpatioTemporalArea
	 * 
	 * @param borders
	 *            list of coordinates {xmin, ymin, xmax, ymax} of the study area
	 * @param timespan
	 *            temporal interval {beginDate, endDate}
	 * @return a set of OSM relations
	 * @throws Exception
	 */
	public Set<OSMResource> getEvolutionMultipolygons(Double[] borders, String[] timespan) throws Exception {
		Set<OSMResource> multipolygons = new TreeSet<OSMResource>(new OSMResourceComparator());
		Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String query = "";
			ResultSet r;
			// Query the members of the relations that were created within
			// timespan
			String queryCreatedRelations = "SELECT idrel FROM relation WHERE datemodif > '" + timespan[0]
					+ "' AND datemodif <= '" + timespan[1] + "' AND tags->'type'='multipolygon' AND vrel = 1";
			query = "SELECT * FROM relationmember r, (" + queryCreatedRelations + ") AS rel_crees "
					+ "WHERE r.idrel = rel_crees.idrel";
			r = s.executeQuery(query);
			this.writeOSMResource(r, "relationmember");

			// Query the corresponding relations and write Java objects
			// (i.e. OSMResource)
			queryCreatedRelations = "SELECT relation.idrel, relation.id, relation.uid, relation.vrel, relation.changeset, relation.username, relation.datemodif, hstore_to_json(relation.tags), relation.visible "
					+ "FROM relation WHERE datemodif > '" + timespan[0] + "' AND datemodif <= '" + timespan[1]
					+ "' AND tags->'type'='multipolygon' AND vrel = 1";
			r = s.executeQuery(queryCreatedRelations);
			this.writeOSMResource(r, "relation");

			// Query the created multipolygons' evolution until end date
			queryCreatedRelations = "SELECT id FROM relation WHERE datemodif > '" + timespan[0] + "' AND datemodif <= '"
					+ timespan[1] + "' AND tags->'type'='multipolygon' AND vrel = 1";
			query = "SELECT * FROM relation r, (" + queryCreatedRelations + ") AS rel_crees"
					+ " WHERE r.id = rel_crees.id AND r.vrel > 1 AND r.datemodif <= '" + timespan[1] + "'";
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
	 * Reconstruct the node composition of an OSM way at a given time.
	 * 
	 * @param nodes
	 *            list of the node IDs that compose the way
	 * @param timestamp
	 *            snapshot date
	 * @return the latest version of the nodes which compose an OSM way element
	 * @throws SocketException
	 * @throws PSQLException
	 * @throws Exception
	 */
	public List<OSMResource> getNodes(List<Long> nodes, String timestamp)
			throws SocketException, PSQLException, Exception {
		List<OSMResource> nodeSet = new ArrayList<OSMResource>();
		OSMResource firstResource = null;

		Connection conn;
		String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
		conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
		Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

		for (int i = 0; i < nodes.size() - 1; i++) {
			Long id = nodes.get(i);

			String vmax = "(SELECT max(vnode) AS max FROM node WHERE id = " + id + " AND datemodif <= '" + timestamp
					+ "')";
			System.out.println("Node ID" + id + ", vmax : " + vmax);
			String query = "SELECT node.idnode, node.id,node.uid,node.vnode, node.changeset, node.username, node.datemodif, hstore_to_json(node.tags), node.visible, node.lon, node.lat "
					+ "FROM node WHERE id = " + id + " AND vnode = " + vmax;
			ResultSet r = s.executeQuery(query);
			if (r.next() == false) {
				s.close();
				conn.close();
				return null;
				// OSMResource n = getNodeFromAPI(id, timestamp);
				// nodeSet.add(n);
				// if (i == 0)
				// firstResource = n;

				// TODO : mettre à jour la base de donnée à partir d'un
				// OSMResource
				// continue;
			} else {
				do {
					OSMResource n = this.writeNode(r);
					nodeSet.add(n);
					if (i == 0)
						firstResource = n;
				} while (r.next());
			}

		}
		s.close();
		conn.close();
		nodeSet.add(firstResource);
		return nodeSet;
	}

	/**
	 * Make a Java object from an OSM node element by requesting OSM API.
	 * 
	 * @param nodeID
	 * @param date:
	 *            date string format ''
	 * @return
	 */
	public OSMResource getNodeFromAPI(Long nodeID, String date) {
		OSMResource r = null;
		date = date.concat(":00");
		date = date.replace(" ", "T");
		date = date.replace("+", ".+");
		System.out.println("date = " + date);
		try {
			String urlAPI = "https://api.openstreetmap.org/api/0.6/node/" + nodeID + "/history";
			System.out.println(urlAPI);
			Document xml = SQLDBPreAnonymization.getDataFromAPI(urlAPI);
			Node osm = xml.getFirstChild();
			NodeList versions = osm.getChildNodes();
			for (int i = 1; i < versions.getLength(); i++) {
				if (versions.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element contrib = (Element) versions.item(i);

					// Fetch metadata
					System.out.println("Getfrom API node #" + nodeID + ", latest version before" + date + "...");
					// String primitive = contrib.getTagName();
					String id = contrib.getAttribute("id");
					String version = contrib.getAttribute("version");
					String changeset = contrib.getAttribute("changeset");
					String timestamp = contrib.getAttribute("timestamp");
					if (Instant.parse(timestamp).isAfter(ZonedDateTime.parse(date).toInstant()))
						break;
					String visible = contrib.getAttribute("visible");
					String user = contrib.getAttribute("user");
					String uid = contrib.getAttribute("uid");
					String lon = "";
					String lat = "";
					if (visible.equals("false")) {
						r = new OSMResource(user, new OSMNode(-2000.0, -2000.0), id, changeset, version, uid, timestamp,
								visible);
						continue;
					}
					// if (primitive.equals("node")) {
					lon = contrib.getAttribute("lon");
					lat = contrib.getAttribute("lat");
					r = new OSMResource(user, new OSMNode(Double.valueOf(lat), Double.valueOf(lon)), id, changeset,
							version, uid, timestamp, visible);
					// }

					// System.out.println(primitive + " " + id + " " + version +
					// " " + timestamp);

					// Fetch tags
					NodeList tags = contrib.getChildNodes();
					for (int j = 1; j < tags.getLength(); j++) {
						if (tags.item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element child = (Element) tags.item(j);
							if (child.getNodeName().equals("tag")) {
								r.addTag(child.getAttribute("k"), child.getAttribute("v"));
							}
						}
					}

				}
			}
			return r;
		} catch (NullPointerException e) {
			System.out.println("See node ID : " + nodeID);
		}
		return r;
	}

	/**
	 * Get the latest version of all nodes (visible or not) that are located in
	 * a study area
	 * 
	 * @param borders
	 *            list of coordinates {xmin, ymin, xmax, ymax} of the study area
	 * @param timestamp
	 *            snapshot date
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
	 * parameters: 1) select the later versions of all queried nodes at begin
	 * date 2) select all created nodes inside timespan including their later
	 * versions until end date
	 * 
	 * @param borders:
	 *            list of coordinates {xmin, ymin, xmax, ymax} of the study area
	 * @param timespan:
	 *            temporal interval
	 * @throws Exception
	 */
	public Set<OSMResource> getEvolutionNode(Double[] borders, String[] timespan) throws Exception {
		Set<OSMResource> nodeSet = new HashSet<OSMResource>();
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			// Query the evolution of all nodes which where selected at
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
			System.out.println(query);
			ResultSet r = s.executeQuery(query);
			System.out.println("------- Query Executed -------");
			writeOSMResource(r, "node");

			// Query the nodes that were created inside timespan and all the
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

	public OSMResource getWay(Long id, String timestamp) throws Exception {
		Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			// Query the evolution of all buildings which where selected at
			// timespan[0] until timespan[1]
			String vmax = "(SELECT max(vway) AS max FROM way WHERE id = " + id + " AND datemodif <= '" + timestamp
					+ "')";
			String query = "SELECT way.id,way.uid,way.vway, way.changeset, way.username, way.datemodif, hstore_to_json(way.tags), way.visible,way.composedof "
					+ "FROM way WHERE id = " + id + " AND vway = " + vmax;
			ResultSet r = s.executeQuery(query);
			System.out.println("------- Query Executed -------");
			OSMResource way = null;
			if (r.next() == false) {
				way = getWayFromAPI(id, timestamp);
			} else {
				do {
					way = writeWay(r);
				} while (r.next());
			}
			// while (r.next())
			// way = writeWay(r);
			s.close();
			conn.close();
			return way;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Create OSMResource from OSM API
	 * 
	 * @param wayID
	 *            : ID of a way
	 * @param vway
	 *            : version number of the OSM way
	 * @return
	 */
	public OSMResource getWayFromAPI(Long wayID, String vway) {
		OSMResource r = null;
		try {
			String urlAPI = "https://api.openstreetmap.org/api/0.6/way/" + wayID + "/history";
			Document xml = SQLDBPreAnonymization.getDataFromAPI(urlAPI);
			Node osm = xml.getFirstChild();
			NodeList versions = osm.getChildNodes();
			for (int i = 1; i < versions.getLength(); i++) {
				if (versions.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element contrib = (Element) versions.item(i);
					if (!contrib.getAttribute("version").equals(vway))
						continue;
					// Fetch metadata
					// String primitive = contrib.getTagName();
					String id = contrib.getAttribute("id");
					String version = contrib.getAttribute("version");
					String changeset = contrib.getAttribute("changeset");
					String timestamp = contrib.getAttribute("timestamp");
					String visible = contrib.getAttribute("visible");
					String user = contrib.getAttribute("user");
					String uid = contrib.getAttribute("uid");

					if (visible.equals("false")) {
						r = new OSMResource(user, new OSMWay(new ArrayList<Long>()), id, changeset, version, uid,
								timestamp, visible);
						continue;
					}

					// Fetch tags and nodes
					NodeList tagsAndNodes = contrib.getChildNodes();
					ArrayList<Long> nodeComposition = new ArrayList<Long>();
					int tagindex = 1;
					// Get node composition
					for (int j = 1; j < tagsAndNodes.getLength(); j++) {
						if (tagsAndNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element child = (Element) tagsAndNodes.item(j);
							if (child.getNodeName().equals("nd")) {
								nodeComposition.add(Long.valueOf(child.getAttribute("ref")));
							} else {
								tagindex = j;
								break;
							}

						}
					}
					r = new OSMResource(user, new OSMWay(nodeComposition), id, changeset, version, uid, timestamp,
							visible);
					// Get tags
					for (int k = tagindex; k < tagsAndNodes.getLength(); k++) {
						if (tagsAndNodes.item(k).getNodeType() == Node.ELEMENT_NODE) {
							Element child = (Element) tagsAndNodes.item(k);
							if (child.getNodeName().equals("tag")) {
								r.addTag(child.getAttribute("k"), child.getAttribute("v"));
							}
						}
					}
				}

			}
			return r;
		} catch (NullPointerException e) {
			System.out.println("See way ID : " + wayID);
		}
		return r;
	}

	public OSMResource getRelation(Long id, String timestamp) throws Exception {
		Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet r;
			// Query the evolution of all buildings which where selected at
			// timespan[0] until timespan[1]
			String vmax = "(SELECT max(vrel) AS max FROM relation WHERE id = " + id + " AND datemodif <= '" + timestamp
					+ "')";
			r = s.executeQuery(vmax);
			while (r.next())
				vmax = r.getString(1);
			// Get relation members
			String queryMembers = "SELECT * FROM relationmember WHERE idrel = " + id + vmax;
			System.out.println(queryMembers);
			List<OsmRelationMember> mbList = new ArrayList<OsmRelationMember>();
			r = s.executeQuery(queryMembers);
			while (r.next()) {
				mbList.add(this.writeRelationMember(r));
			}

			String query = "SELECT relation.idrel, relation.id,relation.uid,relation.vrel, relation.changeset, relation.username, relation.datemodif, hstore_to_json(relation.tags), relation.visible "
					+ "FROM relation WHERE id = " + id + " AND vrel = " + vmax;
			r = s.executeQuery(query);
			System.out.println("------- Query Executed -------");
			OSMResource relation = null;
			while (r.next())
				relation = writeRelation(r, mbList);
			s.close();
			conn.close();
			return relation;
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
		Set<OSMResource> waySet = new HashSet<OSMResource>();
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
		Set<OSMResource> waySet = new HashSet<OSMResource>();
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
					+ "WHERE vway = 1 AND datemodif >= '" + timespan[0] + "' AND datemodif <= '" + timespan[1]
					+ "' AND ST_INTERSECTS(ST_MakeEnvelope(" + borders[0] + "," + borders[1] + "," + borders[2] + ","
					+ borders[3] + ",4326), way.geom)";
			/*
			 * "' AND lon_min >= " + borders[0] + " AND lat_min>=" + borders[1]
			 * + " AND lon_max<=" + borders[2] + " AND lat_max<=" + borders[3];
			 */
			r = s.executeQuery(queryCreatedWays);
			while (r.next())
				waySet.add(this.writeWay(r));
			// Query the created ways' evolution until end date
			queryCreatedWays = "SELECT id FROM way " + "WHERE vway = 1 AND datemodif >= '" + timespan[0]
					+ "' AND datemodif <= '" + timespan[1] + "' AND ST_INTERSECTS(ST_MakeEnvelope(" + borders[0] + ","
					+ borders[1] + "," + borders[2] + "," + borders[3] + ",4326), way.geom)";
			/*
			 * "' AND lon_min >= " + borders[0] + " AND lat_min>=" +borders[1] +
			 * " AND lon_max<=" + borders[2] + " AND lat_max<=" + borders[3];
			 */
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
	 * 
	 * @param relMb
	 * @return OSMResource that is a way or a node element
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	public List<OSMResource> getWayOrNodeMemberFromRelation(OSMRelation rel, String timestamp)
			throws NumberFormatException, Exception {
		List<OSMResource> spatializable = new ArrayList<OSMResource>();
		for (OsmRelationMember relMb : rel.getMembers()) {

			if (relMb.isNode()) {
				spatializable.add(this.getNodeFromAPI(relMb.getRef(), timestamp));
			}
			if (relMb.isWay()) {
				spatializable.add(this.getWay(relMb.getRef(), timestamp));
			}
			if (relMb.isRelation()) {
				OSMRelation rel1 = (OSMRelation) this.getRelation(relMb.getRef(), timestamp).getGeom();
				List<OSMResource> listResource = this.getWayOrNodeMemberFromRelation(rel1, timestamp);
				listResource.addAll(spatializable);
			}
		}
		System.out.println("Number of spatialized members : " + spatializable.size());

		return spatializable;

	}

	public Double[] getEnvelope(OSMResource r, String timestamp) throws SocketException, PSQLException, Exception {
		System.out.println(r.getPrimitiveName());
		if (r.getPrimitiveName().equals("OSMNode")) {
			OSMNode primitive = ((OSMNode) r.getGeom());
			Double[] envelope = { primitive.getLongitude(), primitive.getLatitude(), primitive.getLongitude(),
					primitive.getLatitude() };
			return envelope;
		}
		if (r.getPrimitiveName().equals("OSMWay")) {
			OSMWay primitive = ((OSMWay) r.getGeom());
			List<OSMResource> spatialized = this.getNodes(primitive.getVertices(), timestamp);
			Double minLon = 180., minLat = 90., maxLon = -180., maxLat = -90.;
			for (OSMResource r1 : spatialized) {
				OSMNode node = (OSMNode) r1.getGeom();
				if (node.getLongitude() < minLon)
					minLon = node.getLongitude();
				if (node.getLatitude() < minLat)
					minLat = node.getLatitude();
				if (node.getLongitude() > maxLon)
					maxLon = node.getLongitude();
				if (node.getLatitude() > minLat)
					maxLat = node.getLatitude();
			}
			Double[] envelope = { minLon, minLat, maxLon, maxLat };
			return envelope;
		}
		if (r.getPrimitiveName().equals("OSMRelation")) {
			System.out.println("get OSMRelation envelope...");
			OSMRelation primitive = ((OSMRelation) r.getGeom());
			List<OSMResource> nodeAndWays = this.getWayOrNodeMemberFromRelation(primitive, timestamp);
			System.out.println(nodeAndWays.size());
			Double minLon = 180., minLat = 90., maxLon = -180., maxLat = -90.;
			for (OSMResource res : nodeAndWays) {
				Double[] emprise = this.getEnvelope(res, timestamp);
				System.out
						.println("Emprise : " + emprise[0] + ", " + emprise[1] + ", " + emprise[2] + ", " + emprise[3]);
				if (emprise[0] < minLon)
					minLon = emprise[0];
				if (emprise[0] > maxLon)
					maxLon = emprise[0];

				if (emprise[1] < minLat)
					minLat = emprise[1];
				if (emprise[1] > maxLat)
					maxLat = emprise[1];

				if (emprise[2] < minLon)
					minLon = emprise[2];
				if (emprise[2] > maxLat)
					maxLat = emprise[2];

				if (emprise[3] < minLat)
					minLat = emprise[3];
				if (emprise[3] > maxLat)
					maxLat = emprise[3];
			}
			Double[] envelope = { minLon, minLat, maxLon, maxLat };
			return envelope;

		}

		return null;
	}

	/**
	 * Test if an OSMResource belongs to a spatiotemporal area => fonction à
	 * tester
	 * 
	 * @param r
	 * @param borders
	 *            : list of coordinates {xmin, ymin, xmax, ymax} of the study
	 *            area
	 * @param timestamp
	 *            : snapshot date
	 * @return
	 * @throws Exception
	 * @throws PSQLException
	 * @throws SocketException
	 */
	public boolean isInSpatioTemporalArea(OSMResource r, Double[] borders, String timestamp)
			throws SocketException, PSQLException, Exception {
		Double minLon = borders[0], minLat = borders[1], maxLon = borders[2], maxLat = borders[3];
		if (r.getGeom().getClass().equals("OSMNode")) {
			OSMNode primitive = ((OSMNode) r.getGeom());
			if (primitive.getLatitude() >= minLat && primitive.getLatitude() <= maxLat
					&& primitive.getLongitude() >= minLon && primitive.getLatitude() <= maxLon) {
				System.out.println("OSMNode (lon, lat) coordinates : (" + primitive.getLongitude() + ","
						+ primitive.getLatitude() + ")");
				return true;
			}

		}
		if (r.getGeom().getClass().equals("OSMWay")) {
			// get spatial footprint of the way at date 'timestamp'
			OSMWay primitive = ((OSMWay) r.getGeom());
			List<OSMResource> spatialized = this.getNodes(primitive.getVertices(), timestamp);
			for (OSMResource r1 : spatialized) {
				if (!this.isInSpatioTemporalArea(r1, borders, timestamp))
					return false;
			}
			System.out.println("OSMWay (xmin, ymin, xmax, ymax) footprint : ");
			return true;

		} else if (r.getGeom().getClass().equals("OSMRelation")) {
			OSMRelation primitive = ((OSMRelation) r.getGeom());
			for (OSMResource r1 : this.getWayOrNodeMemberFromRelation(primitive, timestamp)) {
				return this.isInSpatioTemporalArea(r1, borders, timestamp);

			}

		}

		return false;

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
		Connection conn;
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

	/**
	 * Make a Java object from an OSM node element
	 * 
	 * @param r
	 *            resultset from an SQL query
	 * @return
	 * @throws SQLException
	 */
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

	/**
	 * Make a Java object from an OSM element
	 * 
	 * @param r
	 *            is the result of an SQL query
	 * @param osmDataType
	 *            indicates the OSM type of the element (node, way or relation)
	 *            or if the element is part of a relation
	 * @throws Exception
	 */
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
				this.myJavaRelations.add(writeRelation(r));
			}
			if (osmDataType.equalsIgnoreCase("relationmember")) {
				// this.OsmRelMbList.put(writeRelationMember(r),
				// r.getLong("idrel"));
				if (this.OsmRelMbList.containsKey(r.getLong("idrel")))
					this.OsmRelMbList.get(r.getLong("idrel")).add(writeRelationMember(r));
				else {
					List<OsmRelationMember> mbList = new ArrayList<OsmRelationMember>();
					mbList.add(writeRelationMember(r));
					this.OsmRelMbList.put(r.getLong("idrel"), mbList);
				}
				// writeRelationMember(r));

			}
		}
	}

	/**
	 * Make a Java object from an OSM element which is part of an OSM relation
	 * element
	 * 
	 * @param r
	 * @return
	 * @throws Exception
	 */
	public OsmRelationMember writeRelationMember(ResultSet r) throws Exception {
		boolean isNode = r.getString("typeMb").toLowerCase().equalsIgnoreCase("n");
		boolean isWay = r.getString("typeMb").toLowerCase().equalsIgnoreCase("w");
		boolean isRelation = r.getString("typeMb").toLowerCase().equalsIgnoreCase("r");
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

	public OSMResource writeRelation(ResultSet r, List<OsmRelationMember> members) throws Exception {
		// Get date
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");
		Date date = null;
		try {
			date = formatDate.parse(r.getString("datemodif"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String user = r.getString("username");
		Long id = r.getLong("id");
		Integer changeset = r.getInt("changeset");
		Integer vrel = r.getInt("vrel");
		Integer uid = r.getInt("uid");
		// Récupère le type de relation (MULTIPOLYGON ou NON_DEF)
		TypeRelation relType = getRelationType(r);
		PrimitiveGeomOSM myRelation = new OSMRelation(relType, members);

		// Creates a new OSMResource
		OSMResource myOsmResource = new OSMResource(user, myRelation, id, changeset, vrel, uid, date);
		// Visible : peut être null
		myOsmResource.setVisible(r.getBoolean("visible"));
		String hstoreToJson = r.getString("hstore_to_json");
		// Add tags if exist
		if (!hstoreToJson.toString().equalsIgnoreCase("{}")) {
			try {
				JSONObject obj = new JSONObject(hstoreToJson);
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
		// this.myJavaRelations.add(myOsmResource);

		System.out.println("-------------------------------------------");
		return myOsmResource;

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
		List<OsmRelationMember> mbList = new ArrayList<OsmRelationMember>();
		// for (OsmRelationMember mb : OsmRelMbList.keySet()) {
		// if (OsmRelMbList.get(mb).equals(Long.valueOf(r.getString("id") +
		// r.getString("vrel"))))
		// mbList.add(mb);
		// }
		Long idrel = r.getLong("idrel");
		String user = r.getString("username");
		Long id = r.getLong("id");
		Integer changeset = r.getInt("changeset");
		Integer vrel = r.getInt("vrel");
		Integer uid = r.getInt("uid");
		mbList = this.OsmRelMbList.get(idrel);
		// Récupère le type de relation (MULTIPOLYGON ou NON_DEF)
		TypeRelation relType = getRelationType(r);
		PrimitiveGeomOSM myRelation = new OSMRelation(relType, mbList);

		// Creates a new OSMResource
		OSMResource myOsmResource = new OSMResource(user, myRelation, id, changeset, vrel, uid, date);
		// Visible : peut être null
		myOsmResource.setVisible(r.getBoolean("visible"));
		String hstoreToJson = r.getString("hstore_to_json");
		// Add tags if exist
		if (!hstoreToJson.toString().equalsIgnoreCase("{}")) {
			try {
				JSONObject obj = new JSONObject(hstoreToJson);
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
		// this.myJavaRelations.add(myOsmResource);

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