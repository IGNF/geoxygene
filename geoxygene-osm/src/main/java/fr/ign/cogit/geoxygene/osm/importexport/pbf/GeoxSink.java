package fr.ign.cogit.geoxygene.osm.importexport.pbf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

public class GeoxSink implements Sink {
    private long t1, t2, t3;
    private int nbElem;
    public StringBuffer myQueries;
    private StringBuffer nodeValues;
    private StringBuffer wayValues;
    public StringBuffer relValues;
    private StringBuffer relmbValues;

    @Override
    public void initialize(Map<String, Object> arg0) {
        // TODO Auto-generated method stub
        // GeoxSink.initialize(arg0);
        // this.myQueries = new ArrayList<>();
        this.myQueries = new StringBuffer();
        this.nodeValues = new StringBuffer();
        this.wayValues = new StringBuffer();
        this.relValues = new StringBuffer();
        this.relmbValues = new StringBuffer();
        this.nbElem = 0;
    }

    @Override
    public void complete() {
        // TODO Auto-generated method stub

    }

    @Override
    public void release() {
        // TODO Auto-generated method stub

    }

    @Override
    public void process(EntityContainer arg0) {
        /* Creating an OSMResource object given an EntityContainer */

        t1 = System.nanoTime();
        /* storing the features */
        // if (arg0.getEntity().getClass().getSimpleName().toString()
        // .equals("Node")) {
        // Node myNode = (Node) arg0.getEntity().getWriteableInstance();
        // processNode(myNode);
        // }
        // if (arg0.getEntity().getClass().getSimpleName().toString()
        // .equals("Way")) {
        // if (nodeValues.length() > 0) {
        // nodeValues.deleteCharAt(nodeValues.length() - 1);
        // myQueries
        // .append("INSERT INTO node (idnode, id, uid,
        // vnode,changeset,username,datemodif, tags, lat, lon, geom) VALUES ")
        // .append(nodeValues).append(";");
        // executeQuery();
        // nodeValues.setLength(0);
        // }
        // Way myWay = (Way) arg0.getEntity().getWriteableInstance();
        // processWay(myWay);
        // }
        if (arg0.getEntity().getClass().getSimpleName().toString()
                .equals("Relation")) {
            if (wayValues.length() > 0) {
                wayValues.deleteCharAt(wayValues.length() - 1);
                myQueries
                        .append("INSERT INTO way (idway, id, uid, vway, changeset,username, datemodif, tags, composedof) VALUES")
                        .append(wayValues).append(";");
                executeQuery();
                wayValues.setLength(0);
            }
            Relation myRelation = (Relation) arg0.getEntity()
                    .getWriteableInstance();

            processRelation(myRelation);
        }
    }

    public void executeQuery() {
        String host = "localhost";
        String port = "5432";
        String dbName = "paris";
        String dbUser = "postgres";
        String dbPwd = "postgres";
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
        long t3 = System.currentTimeMillis();

        try {
            Connection connection = DriverManager.getConnection(url, dbUser,
                    dbPwd);
            Statement stat;
            try {
                stat = connection.createStatement();
                stat.executeQuery(myQueries.toString());
            } catch (SQLException e) {
                // do nothing
                e.printStackTrace();
            }
            connection.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("              ************* Exécution faite en "
                + (System.currentTimeMillis() - t3) + " ms *************");
        this.nbElem = 0;
        /* Clearing the query container */
        myQueries.setLength(0);

    }

    private void processNode(Node myNode) {
        System.out.println("Ecriture d'un node...");
        StringBuffer hstore = new StringBuffer();
        String wkt = "POINT(" + myNode.getLongitude() + " "
                + myNode.getLatitude() + ")";

        for (Tag tag : myNode.getTags()) {
            String[] decoup = tag.toString().split("'");
            // osmResource.addTag(decoup[1], decoup[3]);
            hstore.append("\"" + decoup[1] + "\"=>\"" + decoup[3] + "\",");

        }
        if (hstore.length() > 0)
            hstore.deleteCharAt(hstore.length() - 1);

        nodeValues.append("(" + myNode.getId() + myNode.getVersion() + ","
                + myNode.getId() + "," + myNode.getUser().getId() + ","
                + myNode.getVersion() + "," + (int) myNode.getChangesetId()
                + "," + "\'" + myNode.getUser().getName() + "\'" + "," + "\'"
                + myNode.getTimestamp() + "\'," + "\'" + hstore + "\'" + ","
                + myNode.getLatitude() + "," + myNode.getLongitude() + ","
                + "ST_GeomFromText(\'" + wkt + "\',4326)" + "),");
        System.out.println("                       Fait en "
                + (System.nanoTime() - t1) + " ns");
        this.nbElem++;
        if (nbElem > 5000) {
            nodeValues.deleteCharAt(nodeValues.length() - 1);
            myQueries
                    .append("INSERT INTO node (idnode, id, uid, vnode, changeset, username, datemodif, tags, lat, lon, geom) VALUES ")
                    .append(nodeValues).append(";");
            executeQuery();
            nodeValues.setLength(0);
        }
    }

    private void processWay(Way myWay) {
        StringBuffer hstore = new StringBuffer();
        List<Long> vertices = new ArrayList<Long>();
        StringBuffer nodeArray = new StringBuffer();
        for (WayNode nd : myWay.getWayNodes()) {
            vertices.add(nd.getNodeId());
            nodeArray.append(nd.getNodeId() + ",");
        }
        if (nodeArray.length() > 0)
            nodeArray.deleteCharAt(nodeArray.length() - 1);

        for (Tag tag : myWay.getTags()) {
            String[] decoup = tag.toString().split("'");
            hstore.append("\"").append(decoup[1]).append("\"=>\"")
                    .append(decoup[3]).append("\",");
        }
        if (hstore.length() > 0)
            hstore.deleteCharAt(hstore.length() - 1);

        wayValues.append("(" + myWay.getId() + myWay.getVersion() + ","
                + myWay.getId() + "," + myWay.getUser().getId() + ","
                + myWay.getVersion() + "," + (int) myWay.getChangesetId() + ","
                + "\'" + myWay.getUser().getName() + "\'" + "," + "\'"
                + myWay.getTimestamp() + "\'," + "\'" + hstore + "\',"
                + "ARRAY [" + nodeArray + "]" + "),");

        System.out.println(
                "Ecriture d'un way : " + (System.nanoTime() - t1) + " ns");
        this.nbElem++;
        if (nbElem > 5000) {
            wayValues.deleteCharAt(wayValues.length() - 1);
            myQueries
                    .append("INSERT INTO way (idway, id, uid, vway, changeset,username, datemodif, tags, composedof) VALUES ")
                    .append(wayValues).append(";");
            executeQuery();
            wayValues.setLength(0);
        }
    }

    private void processRelation(Relation myRelation) {
        StringBuffer hstore = new StringBuffer();

        /* Process relation members */
        for (RelationMember mb : myRelation.getMembers()) {
            // System.out.println("Ecriture d'un relation member...\n");
            relmbValues.append("(" + myRelation.getId()
                    + myRelation.getVersion() + "," + mb.getMemberId() + ","
                    + myRelation.getId() + myRelation.getVersion()
                    + mb.getMemberId() + "," + "\'"
                    + mb.getMemberType().toString() + "\'" + "," + "\'"
                    + mb.getMemberRole().toString() + "\'" + "),");
            // System.out.println(
            // "INSERT INTO relationmember (idrel,idmb,idrelmb,typemb,rolemb)
            // VALUES ("
            // + myRelation.getId() + myRelation.getVersion() + ","
            // + mb.getMemberId() + "," + myRelation.getId()
            // + myRelation.getVersion() + mb.getMemberId() + ","
            // + "\'" + mb.getMemberType().toString() + "\'" + ","
            // + "\'" + mb.getMemberRole().toString() + "\'"
            // + "),");
            this.nbElem++;
            // System.out.println(" Fait en "
            // + (System.nanoTime() - t1) + " ns");
        }
        if (nbElem > 10) {
            relmbValues.deleteCharAt(relmbValues.length() - 1);
            myQueries
                    .append("INSERT INTO relationmember (idrel,idmb,idrelmb,typemb,rolemb) VALUES ")
                    .append(relmbValues).append(";");
            executeQuery();
            relmbValues.setLength(0);
        }

        /* Process relation */
        // System.out.println("Ecriture d'une relation...");
        HashMap<String, String> myTags = new HashMap<String, String>();
        for (Tag tag : myRelation.getTags()) {
            String[] decoup = tag.toString().split("'");
            myTags.put(decoup[1].toString(), decoup[3].toString());
            hstore.append("\"" + decoup[1] + "\"=>\"" + decoup[3] + "\",");
        }
        if (hstore.length() > 0)
            hstore.deleteCharAt(hstore.length() - 1);

        t2 = System.nanoTime();

        relValues.append("(" + myRelation.getId() + myRelation.getVersion()
                + "," + myRelation.getId() + "," + myRelation.getUser().getId()
                + "," + myRelation.getVersion() + ","
                + (int) myRelation.getChangesetId() + "," + "\'"
                + myRelation.getUser().getName() + "\'" + "," + "\'"
                + myRelation.getTimestamp() + "\'," + "\'" + hstore + "\'"
                + "),");
        System.out.println(
                "INSERT INTO relation (idrel, id, uid, vrel,changeset,username, datemodif, tags) VALUES ("
                        + myRelation.getId() + myRelation.getVersion() + ","
                        + myRelation.getId() + ","
                        + myRelation.getUser().getId() + ","
                        + myRelation.getVersion() + ","
                        + (int) myRelation.getChangesetId() + "," + "\'"
                        + myRelation.getUser().getName() + "\'" + "," + "\'"
                        + myRelation.getTimestamp() + "\'," + "\'" + hstore
                        + "\'" + "),");
        // System.out.println(" Faite en : "
        // + (System.nanoTime() - t2) + " ns");
        this.nbElem++;
        if (nbElem > 10) {
            relValues.deleteCharAt(relValues.length() - 1);
            myQueries
                    .append("INSERT INTO relation (idrel, id, uid, vrel,changeset,username, datemodif, tags) VALUES ")
                    .append(relValues).append(";");
            // System.out.println("Requête\n" + myQueries.toString());
            executeQuery();
            relValues.setLength(0);
        }
    }
}
