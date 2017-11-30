/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.osm.importexport.pbf;

import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.openstreetmap.osmosis.pbf2.v0_6.PbfReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.ign.cogit.geoxygene.osm.anonymization.db.SQLDBPreAnonymization;

public class GeoxPbfReader {

	public static GeoxSink mySink = new GeoxSink();

	public static void main(String[] args) {
		pbf2postgis("data/iledelacite_osmconvert.osm.pbf");

	}

	/*******
	 * Parses a PBF file and fills a PostGIS database.
	 * 
	 * @param pbfFilePath:
	 *            path to PBF file
	 */
	public static void pbf2postgis(String pbfFilePath) {
		long tdeb = System.currentTimeMillis();
		PbfReader reader = new PbfReader(new File(pbfFilePath), 1);
		reader.setSink(mySink);
		System.out.println("on entre dans le run");
		reader.run();
		if (mySink.nodeValues.length() > 0) {
			mySink.nodeValues.deleteCharAt(mySink.nodeValues.length() - 1);
			mySink.myQueries
					.append("INSERT INTO node (idnode,id,uid,vnode,changeset,username,datemodif, tags, lat, lon, geom) VALUES ")
					.append(mySink.nodeValues).append(";");
			mySink.executeQuery();
			mySink.nodeValues.setLength(0);
		}
		if (mySink.wayValues.length() > 0) {
			mySink.wayValues.deleteCharAt(mySink.wayValues.length() - 1);
			mySink.myQueries
					.append("INSERT INTO way (idway, id, uid, vway, changeset,username,datemodif, tags, composedof) VALUES ")
					.append(mySink.wayValues).append(";");
			mySink.executeQuery();
			mySink.wayValues.setLength(0);
		}
		if (mySink.relValues.length() > 0) {
			mySink.relValues.deleteCharAt(mySink.relValues.length() - 1);
			mySink.myQueries
					.append("INSERT INTO relation (idrel, id, uid, vrel,changeset,username, datemodif, tags) VALUES ")
					.append(mySink.relValues).append(";");
			// System.out.println("Requête\n" + mySink.myQueries.toString());
			mySink.executeQuery();
			mySink.relValues.setLength(0);
		}

		if (mySink.relmbValues.length() > 0) {
			mySink.relmbValues.deleteCharAt(mySink.relmbValues.length() - 1);
			mySink.myQueries.append("INSERT INTO relationmember (idrel,idmb,idrelmb,typemb,rolemb) VALUES ")
					.append(mySink.relmbValues).append(";");
			// System.out.println("Requête\n" + mySink.myQueries.toString());
			mySink.executeQuery();
			mySink.relmbValues.setLength(0);
		}
		System.out.println("Durée traitement : " + ((System.currentTimeMillis() - tdeb) / 1000 / 60) + " min");

	}

	/*****
	 * Adds a new column "visible" to a PostgreSQL table
	 * 
	 * @param host
	 * @param port
	 * @param dbName
	 * @param dbUser
	 * @param dbPwd
	 * @param type
	 *            : table name ("node", "way" or "relation")
	 * @throws SQLException
	 */

	public static void addVisibleColumn(String host, String port, String dbName, String dbUser, String dbPwd,
			String type) throws SQLException {
		// Connnexion to database
		java.sql.Connection conn;
		try {
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
			conn = DriverManager.getConnection(url, dbUser, dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String query = "ALTER TABLE " + type.toLowerCase() + " ADD COLUMN visible boolean;";
			s.executeQuery(query);
			s.close();
			conn.close();
		} catch (Exception e) {
			// throw e
		}
	}

	/*****
	 * Fetches "visible"(= true or false) attribute on OpenStreetMap API and
	 * updates the "visible" column of a PostgreSQL table. Warning:
	 * setVisibleAttr only updates the existing rows, it does not adds more
	 * recent OSM data.
	 * 
	 * @param host
	 * @param port
	 * @param dbName
	 * @param dbUser
	 * @param dbPwd
	 * @param type:
	 *            table name ("node", "way" or "relation")
	 * @throws Exception
	 */
	public static void setVisibleAttr(String host, String port, String dbName, String dbUser, String dbPwd, String type)
			throws Exception {
		// Connnexion to database
		java.sql.Connection conn;
		String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
		conn = DriverManager.getConnection(url, dbUser, dbPwd);
		Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		// Select all nodes from database
		String query = "SELECT DISTINCT ON (id) id FROM " + type + " WHERE visible IS NULL;";
		ResultSet r = s.executeQuery(query);
		String urlAPI;

		while (r.next()) {
			urlAPI = "http://www.openstreetmap.org/api/0.6/" + type.toLowerCase() + "/" + r.getString("id")
					+ "/history";
			Document xml = SQLDBPreAnonymization.getDataFromAPI(urlAPI);

			Node osm = xml.getFirstChild();
			NodeList list = osm.getChildNodes();
			int nbnode = list.getLength();
			for (int i = 0; i < nbnode; i++) {
				if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
					final Element elt = (Element) list.item(i);
					// System.out.println("id : " + elt.getAttribute("id"));
					// System.out.println("version : " +
					// elt.getAttribute("version"));
					// System.out.println("visible : " +
					// elt.getAttribute("visible"));

					// PK of the table (idnode, idway or idrel)
					String idPK = elt.getAttribute("id") + elt.getAttribute("version");
					updateVisibleColumn("localhost", "5432", "paris", "postgres", "postgres", type, idPK,
							elt.getAttribute("visible"));
				}
			}
		}
	}

	/*****
	 * Updates the "visible" column of a PostgreSQL table
	 * 
	 * @param host
	 * @param port
	 * @param dbName
	 * @param dbUser
	 * @param dbPwd
	 * @param idPK:
	 *            primary key of the table i.e. idnode for node table, idway for
	 *            way table, idrel for relation table. Reminder: idPK is
	 *            composed of the concatenation of the OSM feature's ID and OSM
	 *            feature's no version.
	 * @param visible:
	 *            "true" or "false"
	 * @throws Exception
	 */
	public static void updateVisibleColumn(String host, String port, String dbName, String dbUser, String dbPwd,
			String type, String idPK, String visible) throws Exception {
		java.sql.Connection conn;
		String query = "UPDATE " + type.toLowerCase() + " SET visible = " + visible.toUpperCase() + " WHERE ";
		switch (type.toLowerCase()) {
		case "node":
			query += "idnode = ";
			break;
		case "way":
			query += "idway = ";
			break;
		case "relation":
			query += "idrel = ";
			break;
		}
		query += idPK + ";";
		try {
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
			conn = DriverManager.getConnection(url, dbUser, dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			s.executeQuery(query);
			// System.out.println("------- Query Executed : " + query);
			s.close();
			conn.close();
		} catch (Exception e) {
			// throw e;
		}

	}
}
