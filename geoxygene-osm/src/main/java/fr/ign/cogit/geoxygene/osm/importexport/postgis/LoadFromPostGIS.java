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

import java.io.IOException;
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
import java.util.List;

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
	public List<OSMResource> myJavaObjects;
	// public HashMap<Long, OSMContributor> myContributors;
	public List<OsmRelationMember> OsmRelMbList;
	public Long idRelPrev;

	public static void main(String[] args) throws IOException {
		LoadFromPostGIS loader = new LoadFromPostGIS("localhost", "5432", "paris", "postgres", "postgres");
		List<Double> bbox = new ArrayList<Double>();
		bbox.add(2.3322);
		bbox.add(48.8489);
		bbox.add(2.3634);
		bbox.add(48.8627);

		List<String> timespan = new ArrayList<String>();
		timespan.add("2011-01-01");
		timespan.add("2015-01-01");
		loader.selectNodes(bbox, timespan);
		;
	}

	public LoadFromPostGIS(String host, String port, String dbName, String dbUser, String dbPwd) {
		this.host = host;
		this.port = port;
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPwd = dbPwd;
		this.myJavaObjects = new ArrayList<OSMResource>();
		// this.myOSMObjects = new HashMap<Long, OSMObject>();
		// this.myContributors = new HashMap<Long, OSMContributor>();
		this.OsmRelMbList = new ArrayList<OsmRelationMember>();
		this.idRelPrev = (long) 0;
	}

	/**
	 * Gets the latest nodes at date beginDate
	 * 
	 * @param beginDate
	 *            : timespan lower boundary
	 */
	public void selectNodesInit(List<Double> bbox, String beginDate) {
		String query = "SELECT idnode, id, uid, vnode, changeset, username, datemodif, hstore_to_json(tags), lat, lon FROM ("
				+ "SELECT * FROM node WHERE datemodif <= \'" + beginDate + "\' AND node.geom && ST_MakeEnvelope("
				+ bbox.get(0).toString() + "," + bbox.get(1).toString() + "," + bbox.get(2).toString() + ","
				+ bbox.get(3).toString() + ") ORDER BY datemodif DESC) AS node_and_tags LIMIT 1;";
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
	 */
	public void selectNodes(List<Double> bbox, List<String> timespan) {
		// Initially existing nodes
		selectNodesInit(bbox, timespan.get(0).toString());
		// Nodes created/edited within timespan
		String query = "SELECT idnode, id, uid, vnode, changeset, username, datemodif, hstore_to_json(tags), lat, lon FROM node WHERE node.geom && ST_MakeEnvelope("
				+ bbox.get(0).toString() + "," + bbox.get(1).toString() + "," + bbox.get(2).toString() + ","
				+ bbox.get(3).toString() + ", 4326) AND datemodif > \'" + timespan.get(0).toString()
				+ "\' AND datemodif <= \'" + timespan.get(1).toString() + "\';";

		System.out.println(query);
		try {
			selectFromDB(query, "node");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void selectWaysInit(List<Double> bbox, String beginDate) {
		String query = "SELECT idway, id, uid, vway, changeset, username, datemodif, hstore_to_json(tags), composedof FROM ("
				+ "SELECT * FROM way WHERE datemodif <= \'" + beginDate + "\' AND lon_min >=" + bbox.get(0).toString()
				+ " AND lat_min >=" + bbox.get(1).toString() + " AND lon_max <=" + bbox.get(2).toString()
				+ "AND lat_max <=" + bbox.get(3).toString() + ") AS way_selected ORDER BY datemodif DESC LIMIT 1;";
		System.out.println(query);
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
	 */
	public void selectWays(List<Double> bbox, List<String> timespan) {
		// Initially existing nodes
		selectWaysInit(bbox, timespan.get(0).toString());
		// Ways created/edited within timespan
		String query = "SELECT idway, id, uid, vway, changeset, username, datemodif, hstore_to_json(tags), composedof FROM way "
				+ "WHERE datemodif > \'" + timespan.get(0).toString() + "\' AND datemodif <= \'"
				+ timespan.get(1).toString() + "\'" + " AND lon_min >=" + bbox.get(0).toString() + " AND lat_min >="
				+ bbox.get(1).toString() + " AND lon_max <=" + bbox.get(2).toString() + " AND lat_max <="
				+ bbox.get(3).toString();
		try {
			selectFromDB(query, "way");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void selectRelInit(List<Double> bbox, String beginDate) {
		// Initially existing node-composed relations : query on RELATION
		// MEMBERS
		System.out.println(
				"******************* node-composed relations : query on RELATION MEMBERS  *******************");
		String subquery = "SELECT DISTINCT ON (id) id FROM node WHERE " + "datemodif <= \'" + beginDate
				+ "\' AND node.geom && ST_MakeEnvelope(" + bbox.get(0).toString() + "," + bbox.get(1).toString() + ","
				+ bbox.get(2).toString() + "," + bbox.get(3).toString() + ")" + "ORDER BY id, vnode DESC";
		String query = "SELECT DISTINCT ON (idrel) * FROM relationmember WHERE idmb in (" + subquery
				+ ") ORDER BY idrel;";
		try {
			selectFromDB(query, "relationmember");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!this.OsmRelMbList.isEmpty()) {
			String lastReltoQuery = "SELECT idrel, id, uid, vrel, changeset, username, datemodif, hstore_to_json(tags) FROM relation WHERE idrel="
					+ this.idRelPrev;
			try {
				selectFromDB(lastReltoQuery, "relation");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Initially existing way-composed relations
		System.out
				.println("******************* way-composed relations : query on RELATION MEMBERS  *******************");
		subquery = "SELECT DISTINCT ON (id) id FROM node WHERE " + "datemodif <= \'" + beginDate
				+ "\' AND node.geom && ST_MakeEnvelope(" + bbox.get(0).toString() + "," + bbox.get(1).toString() + ","
				+ bbox.get(2).toString() + "," + bbox.get(3).toString() + ")" + "ORDER BY id, vnode DESC";
		query = "SELECT DISTINCT ON (idrel) * FROM relationmember WHERE idmb in (" + subquery + ") ORDER BY idrel;";
		try {
			selectFromDB(query, "relationmember");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!this.OsmRelMbList.isEmpty()) {
			String lastReltoQuery = "SELECT idrel, id, uid, vrel, changeset, username, datemodif, hstore_to_json(tags) FROM relation WHERE idrel="
					+ this.idRelPrev;
			try {
				selectFromDB(lastReltoQuery, "relation");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void selectRelations(List<Double> bbox, List<String> timespan) {
		selectRelInit(bbox, timespan.get(0).toString());

		// node-composed relations
		String subquery = "SELECT DISTINCT ON (id) id FROM node WHERE " + "datemodif > \'" + timespan.get(0).toString()
				+ "\' AND datemodif <= \'" + timespan.get(1).toString() + "\'" + "\' AND node.geom && ST_MakeEnvelope("
				+ bbox.get(0).toString() + "," + bbox.get(1).toString() + "," + bbox.get(2).toString() + ","
				+ bbox.get(3).toString() + ")";
		String query = "SELECT DISTINCT ON (idrel) * FROM relationmember WHERE idmb in (" + subquery
				+ ") ORDER BY idrel;";
		try {
			selectFromDB(query, "relationmember");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// way-composed relations

	}

	/**
	 * Executes PostGIS query and adds OSMResource to myJavaObjects Collection.
	 */
	public void selectFromDB(String query, String osmDataType) throws Exception {
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
			conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery(query);
			System.out.println("------- Query Executed -------");
			writeOSMResource(r, osmDataType);
			s.close();
			conn.close();
		} catch (Exception e) {
			throw e;
		}
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
				writeRelation(r);
			}
			if (osmDataType.equalsIgnoreCase("relationmember")) {
				if (!idRelPrev.equals(r.getLong("idrel"))) {
					String query = "SELECT idrel, id, uid, vrel, changeset, username, datemodif, hstore_to_json(tags) FROM relation WHERE idrel="
							+ idRelPrev;
					selectFromDB(query, "relation");
				}
				// Adds a new relation member in the list
				this.OsmRelMbList.add(writeRelationMember(r));
				idRelPrev = r.getLong("idrel");
				System.out.println("idrel =" + idRelPrev);
				System.out.println("OSMRelationMember added in OsmRelMbList.");

			}
		}
	}

	public OsmRelationMember writeRelationMember(ResultSet r) throws Exception {
		boolean isNode = r.getString("typeMb").toLowerCase().equalsIgnoreCase("node");
		long idmb = r.getLong("idmb");
		if (r.getString("rolemb").toLowerCase().equalsIgnoreCase("outer")) {
			OsmRelationMember osmRelMb = new OsmRelationMember(RoleMembre.OUTER, isNode, idmb);
			System.out.println("writeRelationMember(r)");
			return osmRelMb;

		} else if (r.getString("rolemb").toLowerCase().equalsIgnoreCase("inner")) {
			OsmRelationMember osmRelMb = new OsmRelationMember(RoleMembre.INNER, isNode, idmb);
			System.out.println("writeRelationMember(r)");
			return osmRelMb;

		} else {
			OsmRelationMember osmRelMb = new OsmRelationMember(RoleMembre.NON_DEF, isNode, idmb);
			System.out.println("writeRelationMember(r)");
			return osmRelMb;
		}
	}

	public void writeRelation(ResultSet r) throws Exception {
		System.out.println("Writing Relation...");

		// Get date
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");
		Date date = null;
		try {
			date = formatDate.parse(r.getString("datemodif"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// Parses hstore_to_json in order to get relation type and to write an
		// OSMRelation
		PrimitiveGeomOSM myRelation = null;
		if (!r.getString("hstore_to_json").toString().equalsIgnoreCase("{}")) {
			try {
				JSONObject obj = new JSONObject(r.getString("hstore_to_json"));
				for (int i = 0; i < obj.names().length(); i++) {
					String key = obj.names().getString(i);
					if (key.toString().equalsIgnoreCase("type")) {
						String value = obj.getString(key);
						// Creates a PrimitiveGeomOSM with type OSMRelation
						myRelation = new OSMRelation(TypeRelation.valueOfTexte(value), this.OsmRelMbList);
						this.OsmRelMbList.clear();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Creates a new OSMResource
		OSMResource myOsmResource = new OSMResource(r.getString("username"), myRelation, r.getLong("id"),
				r.getInt("changeset"), r.getInt("vrel"), r.getInt("uid"), date);
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

		// Test if relation is also a relation member
		String relIsRelMb = "SELECT DISTINCT ON (idrel) * FROM relationmember WHERE idmb=" + myOsmResource.getId()
				+ " ORDER BY idrel";
		System.out.println("			Test : relation is a relation member...");
		selectFromDB(relIsRelMb, "relationmember");
		System.out.println("										... effectué");
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
		// Add tags if exist
		if (!r.getString("hstore_to_json").toString().equalsIgnoreCase("{}")) {
			try {
				JSONObject obj = new JSONObject(r.getString("hstore_to_json"));
				myOsmResource.setNbTags(obj.names().length());
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
}