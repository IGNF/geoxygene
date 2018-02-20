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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.ign.cogit.geoxygene.osm.anonymization.db.SQLDBPreAnonymization;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;

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

		String url = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
		conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
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
			s.executeQuery("DROP TABLE IF EXISTS changeset;");
			s.executeQuery("CREATE TABLE IF NOT EXISTS changeset ( " + "changesetid BIGINT PRIMARY KEY," + "uid BIGINT,"
					+ "username VARCHAR," + "created_at TIMESTAMP WITH TIME ZONE,"
					+ "closed_at TIMESTAMP WITH TIME ZONE," + "lon_min numeric," + "lat_min numeric,"
					+ "lon_max numeric," + "lat_max numeric," + "tags hstore," + "comments_count INTEGER" + ")");
			s.close();
			conn.close();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Insert into a PostgreSQL changeset table a row containing all information
	 * related to the input changeset ID. The information is fetched from OSM
	 * API.
	 * 
	 * @param changesetID
	 *            ID of the changeset to retrieve.
	 * @author QTTruong
	 * @throws Exception
	 */
	public void insertIntoChangeset(Long changesetID) throws Exception {
		try {

			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			System.out.println(changesetID);
			StringBuffer insertQuery = new StringBuffer();
			insertQuery.append(
					"INSERT INTO changeset (changesetid, uid, username, created_at, closed_at, lon_min, lat_min, lon_max, lat_max, tags, comments_count) VALUES (");
			String urlAPI = "http://api.openstreetmap.org/api/0.6/changeset/" + changesetID
					+ "?include_discussion=true";
			Document xml = SQLDBPreAnonymization.getDataFromAPI(urlAPI);
			Node osm = xml.getFirstChild();
			Element changeset = (Element) osm.getChildNodes().item(1);
			insertQuery.append(changesetID + ",");
			insertQuery.append(changeset.getAttribute("uid") + ",");
			insertQuery.append("\'" + changeset.getAttribute("user") + "\',");
			// Dates
			System.out.println("\'" + changeset.getAttribute("created_at") + "\',");
			System.out.println("\'" + changeset.getAttribute("closed_at") + "\',");
			// System.out.println("\'" +
			// changeset.getAttribute("comments_count") + "\',");
			insertQuery.append("\'" + changeset.getAttribute("created_at") + "\',");
			insertQuery.append("\'" + changeset.getAttribute("closed_at") + "\',");
			insertQuery.append(changeset.getAttribute("min_lon") + ",");
			insertQuery.append(changeset.getAttribute("min_lat") + ",");
			insertQuery.append(changeset.getAttribute("max_lon") + ",");
			insertQuery.append(changeset.getAttribute("max_lat") + ",");
			// Tags & Discussion
			NodeList tagsAndDiscussion = changeset.getChildNodes();
			StringBuffer hstore = new StringBuffer();
			for (int i = 1; i < tagsAndDiscussion.getLength(); i++) {
				if (tagsAndDiscussion.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element child = (Element) tagsAndDiscussion.item(i);
					if (child.getNodeName().equals("tag")) {
						System.out.println("k " + child.getAttribute("k"));
						System.out.println("v " + child.getAttribute("v"));
						hstore.append("\"" + child.getAttribute("k") + "\"=>\"" + child.getAttribute("v") + "\",");
					}

				}
			}
			if (hstore.length() > 0)
				hstore.deleteCharAt(hstore.length() - 1); // Deletes last hstore
															// comma
			insertQuery.append("\'" + hstore + "\',");
			System.out.println(changeset.getAttribute("comments_count"));
			insertQuery.append(changeset.getAttribute("comments_count") + ");");
			System.out.println(insertQuery.toString());
			s.executeQuery(insertQuery.toString());
		} catch (Exception e) {
			// throw e;

		}
	}

	/**
	 * 
	 * @param changesetID
	 * @return true if changesetID is recorded in PostgreSQL database
	 * @throws Exception
	 */
	public boolean isInChangesetTable(Long changesetID) throws Exception {
		try {
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
			Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery("SELECT DISTINCT changeset FROM " + tablename);
			HashSet<Long> changesetID = new HashSet<Long>();
			while (r.next()) {
				changesetID.add(r.getLong(1));
			}
			Iterator<Long> it = changesetID.iterator();
			while (r.next()) {
				// System.out.println("Changeset n°" + r.getLong(1));
				changesetID.add(r.getLong(1));
			}
			while (it.hasNext()) {
				// System.out.println(insertQueryToExecute(it.next()));
				insertIntoChangeset(it.next());
			}
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
			if (!this.isInChangesetTable(Long.valueOf(id)))
				this.insertIntoChangeset(Long.valueOf(id));
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
