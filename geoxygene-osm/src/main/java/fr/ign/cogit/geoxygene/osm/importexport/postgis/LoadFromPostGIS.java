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

import java.sql.Array;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import fr.ign.cogit.geoxygene.osm.importexport.OSMNode;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import twitter4j.JSONException;
import twitter4j.JSONObject;

public class LoadFromPostGIS {
    public String host;
    public String port;
    public String dbName;
    public String dbUser;
    public String dbPwd;
    public List<OSMResource> myJavaObjects;
    public List<String> myQuery;
    public HashMap<Long, OSMResource> nodeStore;

    public static void main(String[] args) {
        LoadFromPostGIS loader = new LoadFromPostGIS("localhost", "5432",
                "nepal", "postgres", "postgres");
        // Select the last created nodes of Paris 2015 OSM database
        List<Double> bbox = new ArrayList<Double>();
//        bbox.add(2.3322);
//        bbox.add(48.8489);
//        bbox.add(2.3634);
//        bbox.add(48.8627);
        List<String> timespan = new ArrayList<String>();
        timespan.add("2014-06-23");
        timespan.add("2014-07-23");
        loader.selectNodes(bbox, timespan);
        loader.selectWays();

    }

    public LoadFromPostGIS(String host, String port, String dbName,
            String dbUser, String dbPwd) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPwd = dbPwd;
        this.myJavaObjects = new ArrayList<OSMResource>();
        this.myQuery = new ArrayList<String>();
        this.nodeStore = new HashMap<Long, OSMResource>();
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
     * 
     */
    public void selectNodes(List<Double> bbox, List<String> timespan) {
        if (!(bbox.isEmpty()) && timespan.isEmpty()) {
            // Spatial query only
            this.myQuery
                    .add("SELECT idnode, id, uid, vnode, changeset, username, datemodif, hstore_to_json(tags), lat, lon FROM node WHERE node.geom && ST_MakeEnvelope("
                            + bbox.get(0).toString() + ","
                            + bbox.get(1).toString() + ","
                            + bbox.get(2).toString() + ","
                            + bbox.get(3).toString() + ", 4326);");
        }
        if (bbox.isEmpty() && !(timespan.isEmpty())) {
            // Temporal query only
            this.myQuery
                    .add("SELECT idnode, id, uid, vnode, changeset, username, datemodif, hstore_to_json(tags), lat, lon FROM node WHERE datemodif > \'"
                            + timespan.get(0).toString()
                            + "\' AND datemodif < \'"
                            + timespan.get(1).toString() + "\';");
        }
        if (!bbox.isEmpty() && !(timespan.isEmpty())) {
            // Spatio-temporal query
            this.myQuery
                    .add("SELECT idnode, id, uid, vnode, changeset, username, datemodif, hstore_to_json(tags), lat, lon FROM node WHERE node.geom && ST_MakeEnvelope("
                            + bbox.get(0).toString() + ","
                            + bbox.get(1).toString() + ","
                            + bbox.get(2).toString() + ","
                            + bbox.get(3).toString()
                            + ", 4326) AND datemodif > \'"
                            + timespan.get(0).toString()
                            + "\' AND datemodif < \'"
                            + timespan.get(1).toString() + "\';");
        }
        System.out.println(this.myQuery.get(0).toString());
        try {
            selectFromDB(this.myQuery.get(0), "node");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Retrieves OSM ways from a PostGIS database according to the OSMResources
     * (nodes only) stored in myJavaObjects
     */
    public void selectWays() {
        String query = "SELECT idway, id, uid, vway, changeset, username, datemodif, hstore_to_json(tags), composedof FROM way;";
        try {
            selectFromDB(query, "way");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Executes PostGIS query and adds OSMResource to myJavaObjects Collection.
     */
    public void selectFromDB(String query, String osmDataType)
            throws Exception {
        java.sql.Connection conn;
        try {
            String url = "jdbc:postgresql://" + this.host + ":" + this.port
                    + "/" + this.dbName;
            conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
            Statement s = conn.createStatement();
            ResultSet r = s.executeQuery(query);
            System.out.println("------- Query Executed -------");
            writeOSMResource(r, osmDataType);
            s.close();
            conn.close();
        } catch (Exception e) {
            throw e;
        }
    }

    public void writeOSMResource(ResultSet r, String osmDataType)
            throws SQLException {
        System.out.println("Writing resource...");
        while (r.next()) {
            if (osmDataType.equalsIgnoreCase("node")) {
                System.out.println("r.next is a node");
                if (!nodeStore.containsKey(r.getLong("id"))) {
                    writeNode(r);
                }
            }
            if (osmDataType.equalsIgnoreCase("way")) {
                Array nodeWay = r.getArray("composedof");
                Long[] long_nodeWay = (Long[]) nodeWay.getArray();
                boolean intersectsBBOX = false;
                for (int i = 0; i < long_nodeWay.length; i++) {
                    // Check if a node of the "composedof" column is in
                    // nodeStore
                    while (nodeStore.containsKey(long_nodeWay[i])) {
                        intersectsBBOX = true;
                        break;
                    }
                }
                if (intersectsBBOX) {
                    System.out.println("ID : " + r.getLong("id") + " ; uid : "
                            + r.getInt("uid") + " ; Changeset "
                            + r.getInt("changeset") + " ; Version : "
                            + r.getInt("vway") + " ; date : "
                            + r.getString("datemodif") + " n° noeuds"
                            + r.getString("composedof") + " ; tags : "
                            + r.getString("hstore_to_json"));
                    findVertices(long_nodeWay, r.getLong("uid"),
                            r.getLong("changeset"), r.getString("datemodif"));
                }
            }
        }
    }

    public void findVertices(Long[] idnodeWay, Long uid, Long changeset,
            String datemodif) throws SQLException {
        for (Long idnode : idnodeWay) {
            String nodequery = "SELECT idnode, id, uid, vnode, changeset, username, datemodif,hstore_to_json(tags), datemodif, lat, lon FROM "
                    + "(SELECT idnode, id, uid, vnode, changeset, username,tags, max(datemodif) AS datemodif, lat, lon FROM "
                    + "(SELECT * FROM node WHERE id = " + idnode
                    + " AND datemodif <= \'" + datemodif + "\') AS subquery1 "
                    + "GROUP BY id, idnode, uid, vnode, changeset, username,tags, lat, lon) AS subquery2;";
            try {
                selectFromDB(nodequery, "node");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void writeWay(ResultSet r) throws SQLException {

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
                new OSMNode(r.getDouble("lat"), r.getDouble("lon")),
                r.getLong("id"), r.getInt("changeset"), r.getInt("vnode"),
                r.getInt("uid"), date);
        // Add tags if exist
        if (!r.getString("hstore_to_json").toString().equalsIgnoreCase("{}")) {
            try {
                JSONObject obj = new JSONObject(r.getString("hstore_to_json"));
                for (int i = 0; i < obj.names().length(); i++) {
                    String key = obj.names().getString(i);
                    String value = obj.getString(key);
                    System.out.println(
                            " Ajout du tag {" + key + ", " + value + "}");
                    myOsmResource.addTag(key, value);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            nodeStore.put(myOsmResource.getId(), myOsmResource);
        }
        this.myJavaObjects.add(myOsmResource);
        System.out.println(
                "Java object created ! " + "\nid = " + myOsmResource.getId()
                        + "\nusername = " + myOsmResource.getContributeur()
                        + "     uid = " + myOsmResource.getUid() + "\nvnode = "
                        + myOsmResource.getVersion() + "\ndate = "
                        + myOsmResource.getDate() + "   Changeset = "
                        + myOsmResource.getChangeSet());
        System.out.println("-------------------------------------------");

    }
}