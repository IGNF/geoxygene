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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.ign.cogit.geoxygene.osm.anonymization.db.SQLDBPreAnonymization;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.pbf.GeoxSink;

public class ChangesetRetriever {
	private String host;
	private String port;
	private String dbName;
	private String dbUser;
	private String dbPwd;
	private String url;
	private Connection conn;

	public ChangesetRetriever(String host, String port, String dbName, String dbUser, String dbPwd)
			throws SQLException {
		this.setHost(host);
		this.setPort(port);
		this.setDbName(dbName);
		this.setDbUser(dbUser);
		this.setDbPwd(dbPwd);

		this.url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
		conn = DriverManager.getConnection(this.url, this.dbUser, this.dbPwd);
		System.out.println("Connexion réussie !");
	}

	/**
	 * Create a new PostgreSQL table containing OSM changesets.
	 * 
	 * @author QTTruong
	 * @throws Exception
	 * 
	 */
	public void createChangesetTable() throws Exception {
		try {
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			s.executeQuery("DROP TABLE IF EXISTS changeset;" + "CREATE TABLE IF NOT EXISTS changeset ( "
					+ "changesetid BIGINT PRIMARY KEY," + "uid BIGINT," + "username VARCHAR,"
					+ "created_at TIMESTAMP WITH TIME ZONE," + "closed_at TIMESTAMP WITH TIME ZONE,"
					+ "lon_min numeric," + "lat_min numeric," + "lon_max numeric," + "lat_max numeric," + "tags hstore,"
					+ "comments_count INTEGER" + ")");
			s.close();
			conn.close();
		} catch (Exception e) {
			// throw e;
		}
	}

	/**
	 * Insert into a PostgreSQL changeset table a row containing all information
	 * related to the input changeset ID. The information is fetched from OSM
	 * API.
	 * 
	 * @param values
	 *            rows that are to be inserted into the database
	 * @author QTTruong
	 * @throws Exception
	 */
	public void insertIntoChangeset(Set<String> values) throws Exception {
		try {
			conn = DriverManager.getConnection(this.url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			// System.out.println(changesetID);
			StringBuffer insertQuery = new StringBuffer();
			insertQuery.append(
					"INSERT INTO changeset (changesetid, uid, username, created_at, closed_at, lon_min, lat_min, lon_max, lat_max, tags, comments_count) VALUES ");
			Iterator<String> it = values.iterator();
			while (it.hasNext()) {
				insertQuery.append(it.next() + ",");
			}
			insertQuery.deleteCharAt(insertQuery.length() - 1);
			// évite les erreurs de clé dupliquée
			insertQuery.append(" ON CONFLICT (changesetid) DO NOTHING;");
			System.out.println(insertQuery.toString());
			s.executeQuery(insertQuery.toString());
			s.close();
			conn.close();
		} catch (Exception e) {
			// throw e;

		}
	}

	public void insertOneRow(String value) throws Exception {
		try {
			conn = DriverManager.getConnection(this.url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			// System.out.println(changesetID);
			StringBuffer insertQuery = new StringBuffer();
			insertQuery.append(
					"INSERT INTO changeset (changesetid, uid, username, created_at, closed_at, lon_min, lat_min, lon_max, lat_max, tags, comments_count) VALUES ");
			insertQuery.append(value);
			insertQuery.append(";");
			// System.out.println(insertQuery.toString());
			s.executeQuery(insertQuery.toString());
			// s.close();
			// conn.close();
		} catch (Exception e) {
			// throw e;

		}

	}

	public String getChangesetValues(Long changesetID) {
		StringBuffer value = new StringBuffer();
		try {
			String urlAPI = "https://api.openstreetmap.org/api/0.6/changeset/" + changesetID
					+ "?include_discussion=true";
			System.out.println(urlAPI);
			Document xml = SQLDBPreAnonymization.getDataFromAPI(urlAPI);
			Node osm = xml.getFirstChild();
			Element changeset = (Element) osm.getChildNodes().item(1);

			value.append("(" + changesetID + ",");
			value.append(changeset.getAttribute("uid") + ",");
			value.append("\'" + GeoxSink.escapeSQL(changeset.getAttribute("user")) + "\',");
			// Dates
			// System.out.println("\'" + changeset.getAttribute("created_at") +
			// "\',");
			// System.out.println("\'" + changeset.getAttribute("closed_at") +
			// "\',");
			// System.out.println("\'" +
			// changeset.getAttribute("comments_count") +
			// "\',");
			value.append("\'" + changeset.getAttribute("created_at") + "\',");
			value.append("\'" + changeset.getAttribute("closed_at") + "\',");
			String min_lon = (changeset.getAttribute("min_lon").isEmpty() ? "NULL" : changeset.getAttribute("min_lon"));
			String min_lat = (changeset.getAttribute("min_lat").isEmpty() ? "NULL" : changeset.getAttribute("min_lat"));
			String max_lon = (changeset.getAttribute("max_lon").isEmpty() ? "NULL" : changeset.getAttribute("max_lon"));
			String max_lat = (changeset.getAttribute("max_lat").isEmpty() ? "NULL" : changeset.getAttribute("max_lat"));
			value.append(min_lon + ",");
			value.append(min_lat + ",");
			value.append(max_lon + ",");
			value.append(max_lat + ",");
			// Tags & Discussion
			NodeList tagsAndDiscussion = changeset.getChildNodes();
			StringBuffer hstore = new StringBuffer();
			for (int i = 1; i < tagsAndDiscussion.getLength(); i++) {
				if (tagsAndDiscussion.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element child = (Element) tagsAndDiscussion.item(i);
					if (child.getNodeName().equals("tag")) {
						// System.out.println("k " + child.getAttribute("k"));
						// System.out.println("v " + child.getAttribute("v"));
						hstore.append("\"" + GeoxSink.escapeSQL(child.getAttribute("k")) + "\"=>\""
								+ GeoxSink.escapeSQL(child.getAttribute("v")) + "\",");
					}

				}
			}
			if (hstore.length() > 0)
				hstore.deleteCharAt(hstore.length() - 1); // Deletes last hstore
															// comma
			value.append("\'" + hstore + "\',");
			// System.out.println(changeset.getAttribute("comments_count"));
			value.append(changeset.getAttribute("comments_count") + ")");
			return value.toString();
		} catch (NullPointerException e) {
			System.out.println("See changeset ID : " + changesetID);

		}
		return value.toString();
	}

	/**
	 * 
	 * @param changesetID
	 * @return true if changesetID is recorded in PostgreSQL database
	 * @throws Exception
	 */
	public boolean isInChangesetTable(Long changesetID) throws Exception {
		try {
			conn = DriverManager.getConnection(this.url, this.dbUser, this.dbPwd);
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery("SELECT 1 FROM changeset WHERE changesetid = " + changesetID);
			if (r.next())
				return true;
			s.close();
			conn.close();
		} catch (Exception e) {
			throw e;
			// } catch (PSQLException e1) {
			// System.out.println("Aucun résultat retourné par la requête");
		}
		return false;
	}

	public void updateDataFromChangeset() throws Exception {
		try {
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery("SELECT changesetid, uid, username FROM changeset");
			// .executeQuery("SELECT changesetid, uid, username FROM changeset
			// WHERE changesetid =" + changesetID);

			StringBuffer insert = new StringBuffer();
			while (r.next()) {
				Set<Object[]> data = getOsmChange(r.getLong("changesetid"));
				for (Object[] row : data) {
					// System.out.println(row[0] + " " + row[1] + " " + row[2] +
					// " " + row[3] + " " + row[4]);
					// System.out.println("Tags : " + row[6].toString());
					// System.out.println("Node composing" + row[5].toString());
					// System.out.println(row[8].getClass().isArray());

					if (row[0].equals("node"))
						insert.append(
								"INSERT INTO node (idnode,id,vnode,uid,changeset,username,datemodif, lon,	 lat, tags, visible ) VALUES ")
								.append("(" + row[1] + row[2] + "," + row[1] + "," + row[2] + "," + r.getLong("uid")
										+ "," + r.getLong("changesetid") + ",\'" + r.getString("username") + "\',\'"
										+ row[3] + "\'," + row[4] + "," + row[5] + ",\'" + row[6] + "\'," + row[9]
										+ ") ON CONFLICT DO NOTHING;");
					if (row[0].equals("way")) {
						if (row[5].toString().isEmpty())
							insert.append(
									"INSERT INTO way (idway, id, vway, uid,changeset,username,datemodif,composedof, tags, visible) VALUES ")
									.append("(" + row[1] + row[2] + "," + row[1] + "," + row[2] + "," + r.getLong("uid")
											+ "," + r.getLong("changesetid") + ",\'" + r.getString("username") + "\',\'"
											+ row[3] + "\',NULL,\'" + row[6] + "\'," + row[9]
											+ ") ON CONFLICT  DO NOTHING;");
						else
							insert.append(
									"INSERT INTO way (idway, id, vway,uid,changeset,username,datemodif,  composedof,tags, visible) VALUES ")
									.append("(" + row[1] + row[2] + "," + row[1] + "," + row[2] + "," + r.getLong("uid")
											+ "," + r.getLong("changesetid") + ",\'" + r.getString("username") + "\',\'"
											+ row[3] + "\', ARRAY[" + row[5] + "],\'" + row[6] + "\'," + row[9]
											+ ") ON CONFLICT DO NOTHING;");
					}

					if (row[0].equals("relation")) {
						insert.append(
								"INSERT INTO relation (idrel, id,vrel,uid,changeset,username, datemodif, tags, visible) VALUES ")
								.append("(" + row[1] + row[2] + "," + row[1] + "," + row[2] + "," + r.getLong("uid")
										+ "," + r.getLong("changesetid") + ",\'" + r.getString("username") + "\',\'"
										+ row[3] + "\',\'" + row[6] + "\'," + row[9] + ") ON CONFLICT DO NOTHING;");

					}
					if (row[9].equals("false"))
						continue;
					if (row[0].equals("relation"))
						if (((Object[]) row[8]).length > 0) {
							Object[] memberList = (Object[]) row[8];
							for (int i = 0; i < memberList.length; i++) {
								Object[] mb = (Object[]) memberList[i];
								// System.out.println(mb[0].toString() + " " +
								// mb[1].toString() + " " + mb[2].toString());
								insert.append("INSERT INTO relationmember (idrel, idmb, typemb,rolemb) VALUES ")
										.append("(" + row[1]).append(row[2])
										.append("," + mb[1] + ",\'" + mb[0].toString().substring(0, 1) + "\',\'"
												+ mb[2].toString() + "\') ON CONFLICT DO NOTHING;");
							}
						}
					// for (String[] mb : (HashSet<String[]>) row[6]) {
					// System.out.println(mb[0] + " " + mb[1] + " " + mb[2]);
					// insert.append("INSERT INTO relationmember (idrel, idmb,
					// typemb,rolemb) VALUES ")
					// .append("(" + row[1]).append(row[2])
					// .append("," + mb[1].substring(0, 0) + "," + mb[2] +
					// ");");
					// }

				}
				// System.out.println(insert.toString());
				this.executeAnyQuery(insert.toString());
				insert.setLength(0);
			}
			if (insert.length() > 0)
				this.executeAnyQuery(insert.toString());
			s.close();
			conn.close();

		} catch (Exception e) {
			throw e;
			// } catch (PSQLException e1) {
			// System.out.println("Aucun résultat retourné par la requête");
		}

	}

	public void executeAnyQuery(String query) {
		try {
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			s.execute(query);
		} catch (Exception e) {
			// throw e;

		}

	}

	public ResultSet executeQuery(String query) {
		ResultSet r = null;
		try {
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			r = s.executeQuery(query);
		} catch (Exception e) {
			// throw e;
		}
		return r;

	}

	/**
	 * Get all the data from a changeset (using OSM API)
	 * 
	 * @param changesetID
	 * @return A set of data, where each element is a table composed of the
	 *         following elements: {metadata, tags, nodes composing a way,
	 *         members of a relation}
	 */
	public static Set<Object[]> getOsmChange(Long changesetID) {
		Set<Object[]> data = new HashSet<Object[]>();
		try {
			String urlAPI = "http://api.openstreetmap.org/api/0.6/changeset/" + changesetID + "/download";
			Document xml = SQLDBPreAnonymization.getDataFromAPI(urlAPI);
			Node osmChange = xml.getFirstChild();
			NodeList modifications = osmChange.getChildNodes();
			for (int i = 1; i < modifications.getLength(); i++) {
				if (modifications.item(i).getNodeType() == Node.ELEMENT_NODE) {
					// Element changeset = (Element)
					// osm.getChildNodes().item(1);
					Element modif = (Element) modifications.item(i);
					Element osmObj = (Element) modif.getChildNodes().item(1);
					// Fetch metadata
					String primitive = osmObj.getTagName();
					String id = osmObj.getAttribute("id");
					String version = osmObj.getAttribute("version");
					String timestamp = osmObj.getAttribute("timestamp");
					String visible = osmObj.getAttribute("visible");
					String lon = "";
					String lat = "";
					if (visible.equals("false")) {
						Object[] obj = { primitive, id, version, timestamp, lon, lat, "", "", new ArrayList<String[]>(),
								visible };
						data.add(obj);
						continue;
					}
					if (primitive.equals("node")) {
						lon = osmObj.getAttribute("lon");
						lat = osmObj.getAttribute("lat");

					}
					// Fetch tags and members
					NodeList tagsAndMembers = osmObj.getChildNodes();
					// System.out.println(tagsAndMembers.getLength());
					StringBuffer hstore = new StringBuffer();
					Set<String[]> relationMembers = new HashSet<String[]>();
					StringBuffer nodeComposition = new StringBuffer();
					for (int j = 1; j < tagsAndMembers.getLength(); j++) {
						if (tagsAndMembers.item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element child = (Element) tagsAndMembers.item(j);
							if (child.getNodeName().equals("tag")) {
								hstore.append("\"" + GeoxSink.escapeSQL(child.getAttribute("k")) + "\"=>\""
										+ GeoxSink.escapeSQL(child.getAttribute("v")) + "\",");
							}
							if (child.getNodeName().equals("nd"))
								nodeComposition.append(child.getAttribute("ref")).append(",");
							if (child.getNodeName().equals("member")) {
								String type = child.getAttribute("type");
								String ref = child.getAttribute("ref");
								String role = child.getAttribute("role");
								String[] member = { type, ref, role };
								// System.out.println(type + " " + ref + " " + "
								// " + role);
								relationMembers.add(member);
							}
						}
					}
					if (hstore.length() > 0) {// Delete last comma
						hstore.deleteCharAt(hstore.length() - 1);
						// System.out.println(hstore);
					}

					if (nodeComposition.length() > 0) {// Delete last comma
						nodeComposition.deleteCharAt(nodeComposition.length() - 1);
						// System.out.println(nodeComposition);
					}

					Object[] obj = { primitive, id, version, timestamp, lon, lat, hstore, nodeComposition,
							relationMembers.toArray(), visible };
					data.add(obj);
				}

			}
			return data;
		} catch (NullPointerException e) {
			System.out.println("See changeset ID : " + changesetID);
		}
		return null;
	}

	/**
	 * Fill PostgreSQL changeset table based on the changeset IDs contained in
	 * node, way and relation tables. Additional info is fetched from OSM API.
	 * 
	 * @param tablename
	 *            Table name from which the changeset IDs are first selected:
	 *            "node", "way" or "relation".
	 * @author QTTruong
	 * 
	 */
	public void updateChangeSetTable(String tablename) {
		try {
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			// ResultSet r = s.executeQuery("SELECT DISTINCT changeset FROM " +
			// tablename);

			ResultSet r = s.executeQuery("SELECT DISTINCT a.changeset " + "FROM " + tablename
					+ " a WHERE  a.changeset NOT IN ( " + "SELECT DISTINCT changesetid FROM   changeset);");
			HashSet<Long> changesetID = new HashSet<Long>();
			Set<String> values = new HashSet<String>();
			while (r.next()) {
				changesetID.add(r.getLong(1));
			}
			System.out.println("Nombre de changesets uniques : " + changesetID.size());
			Iterator<Long> it = changesetID.iterator();
			while (r.next()) {
				// System.out.println("Changeset n°" + r.getLong(1));
				changesetID.add(r.getLong(1));
			}
			while (it.hasNext()) {
				values.add(getChangesetValues(it.next()));
				// Au bout de 100 valeurs on met à jour la base de données
				if (values.size() >= 100) {
					insertIntoChangeset(values);
					values = new HashSet<String>();
				}

			}
			// insère les dernières lignes
			insertIntoChangeset(values);
			values.clear();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param chgstID
	 * @return changeset's timeframe in minutes
	 * @throws SQLException
	 */
	public Double changesetDuration(int chgstID) throws SQLException {
		// Connnexion to database
		Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		String query = "SELECT closed_at - created_at FROM changeset WHERE id = " + chgstID;
		ResultSet r = s.executeQuery(query);
		String[] duration = r.getString(1).split(":");
		return Double.valueOf(duration[0]) * 60 + Double.valueOf(duration[1]) + Double.valueOf(duration[2]) / 60;

	}

	/**
	 * 
	 * @param chgstIDs
	 * @return average duration of a changeset
	 * @throws Exception
	 */
	public Double getChgstAverageDuration(Set<Integer> chgstIDs) throws Exception {
		Double avg = 0.0;
		for (Integer id : chgstIDs) {
			// if (!this.isInChangesetTable(Long.valueOf(id)))
			// this.insertIntoChangeset(Long.valueOf(id));
			avg += this.changesetDuration(id);
		}
		avg /= chgstIDs.size();
		return avg;

	}

	/**
	 * 
	 * @param chgstID
	 * @return changeset's editor
	 * @throws SQLException
	 */
	public String getChgestEditor(int chgstID) throws SQLException {
		Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		String query = "SELECT tags -> \'created_by\' FROM changeset WHERE id = " + chgstID;
		ResultSet r = s.executeQuery(query);
		return r.getString(1);

	}

	/**
	 * 
	 * @param contributions
	 *            : ensemble des contributions produites dans une fenêtre
	 *            spatio-temporelle
	 * @return ensemble des changesets ID produites dans une fenêtre
	 *         spatio-temporelle
	 */
	public static Set<Integer> getAllChangesets(Set<OSMResource> contributions) {
		HashSet<Integer> changesets = new HashSet<Integer>();
		for (OSMResource r : contributions)
			changesets.add(r.getChangeSet());
		return changesets;
	}

	/**
	 * 
	 * @param changesetIDs
	 *            : les IDs des changesets
	 * @return les coordonnées moyennes {lon mix, lat min, lon max, lat max}
	 *         représentant l'étendue moyenne des changesets en entrée
	 * @throws Exception
	 */
	public Double[] getChangesetsMeanExtent(Set<Integer> changesetIDs) throws Exception {
		conn = DriverManager.getConnection(this.url, this.dbUser, this.dbPwd);
		Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		Double mean_lon_min = 0.0;
		Double mean_lat_min = 0.0;
		Double mean_lon_max = 0.0;
		Double mean_lat_max = 0.0;

		String query = "SELECT lon_min, lat_min, lon_max, lat_max FROM changeset WHERE changesetid  = ANY(ARRAY["
				+ changesetIDs.toString().replaceAll("\\[|\\]", "").replaceAll(" ", "") + "])";
		ResultSet r = s.executeQuery(query);
		int nbRow = 0;
		while (r.next()) {
			mean_lon_min += r.getDouble("lon_min");
			mean_lat_min += r.getDouble("lat_min");
			mean_lon_max += r.getDouble("lon_max");
			mean_lat_max += r.getDouble("lat_max");
			nbRow++;
		}

		if (nbRow == 0) { // Cas où les changesets ne sont pas dans la BDD
			Set<String> values = new HashSet<String>();
			for (Integer changesetid : changesetIDs) {
				String value = this.getChangesetValues(Long.valueOf(changesetid));
				values.add(value);
			}
			this.insertIntoChangeset(values);
		}

		// Et on relance la requête
		r = s.executeQuery(query);
		while (r.next()) {
			mean_lon_min += r.getDouble("lon_min");
			mean_lat_min += r.getDouble("lat_min");
			mean_lon_max += r.getDouble("lon_max");
			mean_lat_max += r.getDouble("lat_max");
			nbRow++;
		}
		s.close();
		conn.close();
		mean_lon_min /= nbRow;
		mean_lat_min /= nbRow;
		mean_lon_max /= nbRow;
		mean_lat_max /= nbRow;

		Double[] meanCoordinates = { mean_lon_min, mean_lat_min, mean_lon_max, mean_lat_max };

		return meanCoordinates;
	}

	/**
	 * 
	 * @param contributions
	 *            : ensemble des contributions produites dans une fenêtre
	 *            spatio-temporelle
	 * @return nombre de changesets produits dans une fenêtre spatio-temporelle
	 */
	public static Integer getNbChangesets(Set<OSMResource> contributions) {
		return getAllChangesets(contributions).size();
	}

	// Getters & Setters
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPwd() {
		return dbPwd;
	}

	public void setDbPwd(String dbPwd) {
		this.dbPwd = dbPwd;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
