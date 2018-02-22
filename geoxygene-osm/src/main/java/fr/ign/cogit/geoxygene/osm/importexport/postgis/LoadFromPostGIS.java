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
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.osm.importexport.OSMNode;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.RoleMembre;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.TypeRelation;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.OSMWay;
import fr.ign.cogit.geoxygene.osm.importexport.OsmRelationMember;
import fr.ign.cogit.geoxygene.osm.importexport.PrimitiveGeomOSM;
import twitter4j.JSONException;
import twitter4j.JSONObject;

public class LoadFromPostGIS {
	public String host;
	public String port;
	public String dbName;
	public String dbUser;
	public String dbPwd;
	public Set<OSMResource> myJavaObjects;
	// public HashMap<Long, OSMContributor> myContributors;
	public Map<OsmRelationMember, Long> OsmRelMbList;
	public Set<OSMResource> myJavaRelations;

	public static void main(String[] args) throws Exception {
		LoadFromPostGIS loader = new LoadFromPostGIS("localhost", "5432", "paris", "postgres", "postgres");
		// List<Double> bbox = new ArrayList<Double>();
		// bbox.add(2.3322);
		// bbox.add(48.8489);
		// bbox.add(2.3634);
		// bbox.add(48.8627);
		//
		// List<String> timespan = new ArrayList<String>();
		// timespan.add("2010-01-01");
		// timespan.add("2013-01-01");

		// loader.relationEvolution(bbox, timespan);
		// loader.selectRelations(bbox, timespan);

		Double[] bbox = { 2.3322, 48.8489, 2.3634, 48.8627 };
		String[] timespan = { "2010-01-01", "2010-02-01" };
		loader.getDataFrombbox(bbox, timespan);

	}

	public LoadFromPostGIS(String host, String port, String dbName, String dbUser, String dbPwd) {
		this.host = host;
		this.port = port;
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPwd = dbPwd;
		this.myJavaObjects = new HashSet<OSMResource>();
		// this.myOSMObjects = new HashMap<Long, OSMObject>();
		// this.myContributors = new HashMap<Long, OSMContributor>();
		this.OsmRelMbList = new HashMap<OsmRelationMember, Long>();
		this.myJavaRelations = new HashSet<OSMResource>();
	}

	public void getDataFrombbox(Double[] bbox, String[] timespan) throws Exception {
		// Nodes at t1
		selectNodesInit(bbox, timespan[0].toString());
		// Nodes between t1 and t2
		selectNodes(bbox, timespan);

		// Ways at t1
		// selectWaysInit(bbox, timespan[0].toString());
		// Ways between t1 and t2
		// selectWays(bbox, timespan);

		// Relations at t1
		// relationSnapshot(bbox, timespan[0].toString());
		// selectRelInit(bbox, timespan[0].toString());
		// Relations between t1 and t2
		// relationEvolution(bbox, timespan);
		// selectRelations(bbox, timespan);
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
				// for (int i = 0; i < obj.names().length(); i++) {
				// String key = obj.names().getString(i);
				// if (key.toString().equalsIgnoreCase("type")) {
				// value = obj.getString(key);
				// }
				// }
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
			System.out.println("------- Query Executed : Snapshot Relation -------");
			writeOSMResource(r, osmDataType);
			s.close();
			conn.close();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Gets the latest nodes at date beginDate
	 * 
	 * @param beginDate
	 *            : timespan lower boundary
	 * @throws SQLException
	 */
	public void selectNodesInit(Double[] bbox, String beginDate) throws SQLException {
		// String query = "SELECT idnode, id, uid, vnode, changeset, username,
		// datemodif, hstore_to_json(tags), lat, lon FROM ("
		// + "SELECT DISTINCT ON (id) * FROM node WHERE datemodif <= \'" +
		// beginDate
		// + "\' AND node.geom && ST_MakeEnvelope(" + bbox[0].toString() + "," +
		// bbox[1].toString() + ","
		// + bbox[2].toString() + "," + bbox[3].toString() + ") ORDER BY id,
		// datemodif DESC) AS node_and_tags;";
		// Query visible attribute
		String query = "SELECT idnode, id, uid, vnode, changeset, username, datemodif, hstore_to_json(tags), lat, lon, visible FROM ("
				+ "SELECT DISTINCT ON (id) * FROM node WHERE datemodif <= \'" + beginDate
				+ "\' AND node.geom && ST_MakeEnvelope(" + bbox[0].toString() + "," + bbox[1].toString() + ","
				+ bbox[2].toString() + "," + bbox[3].toString() + ") ORDER BY id, datemodif DESC) AS node_and_tags;";
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
	 * parameters
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
	public void selectNodes(Double[] bbox, String[] timespan) throws SQLException {

		// Nodes created/edited within timespan
		String query = "SELECT idnode, id, uid, vnode, changeset, username, datemodif, hstore_to_json(tags), lat, lon, visible FROM node WHERE node.geom && ST_MakeEnvelope("
				+ bbox[0].toString() + "," + bbox[1].toString() + "," + bbox[2].toString() + "," + bbox[3].toString()
				+ ", 4326) AND datemodif > \'" + timespan[0].toString() + "\' AND datemodif <= \'"
				+ timespan[1].toString() + "\';";
		// Query database
		try {
			selectFromDB(query, "node");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void selectWaysInit(Double[] bbox, String beginDate) throws SQLException {
		String query = "SELECT idway, id, uid, vway, changeset, username, datemodif, hstore_to_json(tags), composedof, visible FROM ("
				+ "SELECT DISTINCT ON (id) * FROM way WHERE datemodif <= \'" + beginDate + "\' AND lon_min >="
				+ bbox[0].toString() + " AND lat_min >=" + bbox[1].toString() + " AND lon_max <=" + bbox[2].toString()
				+ "AND lat_max <=" + bbox[3].toString() + " ORDER BY id, datemodif DESC) AS way_selected;";
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
	 * @throws SQLException
	 */
	public void selectWays(Double[] bbox, String[] timespan) throws SQLException {
		// Ways created/edited within timespan
		String query = "SELECT idway, id, uid, vway, changeset, username, datemodif, hstore_to_json(tags), composedof, visible FROM way "
				+ "WHERE datemodif > \'" + timespan[0].toString() + "\' AND datemodif <= \'" + timespan[1].toString()
				+ "\'" + " AND lon_min >=" + bbox[0].toString() + " AND lat_min >=" + bbox[1].toString()
				+ " AND lon_max <=" + bbox[2].toString() + " AND lat_max <=" + bbox[3].toString();
		// Query database
		try {
			selectFromDB(query, "way");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

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
			throw e;
		}

		// Cherche si les relations qui viennent d'être créées sont également
		// des membres de relation qui sont dans la BBOX étudiée

	}

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

	public void writeNode(ResultSet r) throws SQLException {
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
		this.myJavaObjects.add(myOsmResource);
		System.out.println("Java object created ! " + "\nid = " + myOsmResource.getId() + "\nusername = "
				+ myOsmResource.getContributeur() + "     uid = " + myOsmResource.getUid() + "\nvnode = "
				+ myOsmResource.getVersion() + "\ndate = " + myOsmResource.getDate() + "   Changeset = "
				+ myOsmResource.getChangeSet());
		System.out.println("-------------------------------------------");
	}

	public void writeOSMResource(ResultSet r, String osmDataType) throws Exception {

		while (r.next()) {
			System.out.println("writeOSMResource");
			if (osmDataType.equalsIgnoreCase("node")) {
				System.out.println("Writing resource with type node...");
				System.out.println("r.next is a node");
				writeNode(r);
			}
			if (osmDataType.equalsIgnoreCase("way")) {
				System.out.println("Writing resource with type way...");
				writeWay(r);
			}
			if (osmDataType.equalsIgnoreCase("relation")) {
				System.out.println("Writing resource with type relation...");
				// writeRelation(r);
				writeRelation(r);
			}
			if (osmDataType.equalsIgnoreCase("relationmember")) {
				// if (!idRelPrev.equals(r.getLong("idrel"))) {
				// String query = "SELECT idrel, id, uid, vrel, changeset,
				// username, datemodif, hstore_to_json(tags) FROM relation WHERE
				// idrel="
				// + idRelPrev;
				// selectFromDB(query, "relation");
				// }
				// Adds a new relation member in the list
				// this.OsmRelMbList.add(writeRelationMember(r));
				// idRelPrev = r.getLong("idrel");
				// System.out.println("idrel =" + idRelPrev);
				// System.out.println("OSMRelationMember added in
				// OsmRelMbList.");

				this.OsmRelMbList.put(writeRelationMember(r), r.getLong("idrel"));

			}
		}
	}

	public OsmRelationMember writeRelationMember(ResultSet r) throws Exception {
		boolean isNode = r.getString("typeMb").toLowerCase().equalsIgnoreCase("node");
		boolean isWay = r.getString("typeMb").toLowerCase().equalsIgnoreCase("way");
		boolean isRelation = r.getString("typeMb").toLowerCase().equalsIgnoreCase("relation");
		long idmb = r.getLong("idmb");
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

	public void writeRelation(ResultSet r) throws Exception {
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
			if (OsmRelMbList.get(mb) == (r.getLong("id") + r.getLong("vrel")))
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

	}

	public void writeWay(ResultSet r) throws SQLException {
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
		this.myJavaObjects.add(myOsmResource);
		System.out.println("Java object created ! " + "\nid = " + myOsmResource.getId() + "\nusername = "
				+ myOsmResource.getContributeur() + "     uid = " + myOsmResource.getUid() + "\nvway = "
				+ myOsmResource.getVersion() + "\ndate = " + myOsmResource.getDate() + "   Changeset = "
				+ myOsmResource.getChangeSet());
		System.out.println("-------------------------------------------");
	}

}