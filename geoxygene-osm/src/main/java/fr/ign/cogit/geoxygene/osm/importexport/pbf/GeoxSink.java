package fr.ign.cogit.geoxygene.osm.importexport.pbf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

public class GeoxSink implements Sink {

	private static Logger LOGGER = Logger.getLogger(GeoxSink.class);

	private long t1, t2;
	private int nbElem;
	public StringBuffer myQueries;
	StringBuffer nodeValues;
	StringBuffer wayValues;
	public StringBuffer relValues;
	public StringBuffer relmbValues;
	public String name;

	@Override
	public void initialize(Map<String, Object> arg0) {
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
		if (arg0.getEntity().getClass().getSimpleName().toString().equals("Node")) {
			Node myNode = (Node) arg0.getEntity().getWriteableInstance();
			processNode(myNode);

		}
		if (arg0.getEntity().getClass().getSimpleName().toString().equals("Way")) {
			if (nodeValues.length() > 0) {
				nodeValues.deleteCharAt(nodeValues.length() - 1);
				myQueries
						.append("INSERT INTO node (idnode,id,uid,vnode,changeset,username,datemodif, tags, lat, lon, geom) VALUES ")
						.append(nodeValues).append(";");
				executeQuery();
				nodeValues.setLength(0);
			}
			Way myWay = (Way) arg0.getEntity().getWriteableInstance();
			processWay(myWay);
		}
		if (arg0.getEntity().getClass().getSimpleName().toString().equals("Relation")) {
			if (wayValues.length() > 0) {
				wayValues.deleteCharAt(wayValues.length() - 1);
				myQueries
						.append("INSERT INTO way (idway, id, uid, vway, changeset,username,datemodif, tags, composedof) VALUES ")
						.append(wayValues).append(";");
				executeQuery();
				wayValues.setLength(0);
			}
			Relation myRelation = (Relation) arg0.getEntity().getWriteableInstance();

			processRelation(myRelation);
		}
	}

	public void executeQuery() {
		String host = "localhost";
		String port = "5432";
		String dbName = "iledelacite1";
		String dbUser = "postgres";
		String dbPwd = "postgres";
		String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
		long t3 = System.currentTimeMillis();

		try {
			Connection connection = DriverManager.getConnection(url, dbUser, dbPwd);
			Statement stat;
			try {
				stat = connection.createStatement();
				// System.out.println(myQueries.toString());
				stat.executeQuery(myQueries.toString());
			} catch (SQLException e) {
				LOGGER.error(e.getMessage());
//				e.printStackTrace();
			}
			connection.close();
		} catch (SQLException e) {
//			e.printStackTrace();
		}

//		System.out.println("              ************* Exécution faite en " + (System.currentTimeMillis() - t3)
//				+ " ms *************");
		this.nbElem = 0;
		/* Clearing the query container */
		myQueries.setLength(0);

	}

	private void processNode(Node myNode) {
		StringBuffer hstore = new StringBuffer();
		String wkt = "POINT(" + myNode.getLongitude() + " " + myNode.getLatitude() + ")";
		for (Tag tag : myNode.getTags()) {
			String[] decoup = tag.toString().split("'");
			hstore.append("\"" + escapeSQL(decoup[1]) + "\"=>\"" + escapeSQL(decoup[3]) + "\",");

		}
		if (hstore.length() > 0)
			hstore.deleteCharAt(hstore.length() - 1);

		nodeValues.append("(" + myNode.getId() + myNode.getVersion() + "," + myNode.getId() + ","
				+ myNode.getUser().getId() + "," + myNode.getVersion() + "," + (int) myNode.getChangesetId() + ","
				+ "\'" + escapeSQL(myNode.getUser().getName().toString()) + "\'" + "," + "\'" + myNode.getTimestamp()
				+ "\'," + "\'" + hstore + "\'" + "," + myNode.getLatitude() + "," + myNode.getLongitude() + ","
				+ "ST_GeomFromText(\'" + wkt + "\',4326)" + "),");

		this.nbElem++;
		if (nbElem > 10000) {
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
		StringBuffer nodeArray = new StringBuffer();
		StringBuffer nodeArray2 = new StringBuffer();
		for (WayNode nd : myWay.getWayNodes()) {
			nodeArray.append(nd.getNodeId() + ",");
		}
		if (nodeArray.length() > 0)
			nodeArray.deleteCharAt(nodeArray.length() - 1);
		if (nodeArray.length() > 0) {
			nodeArray2.append("ARRAY[").append(nodeArray.toString()).append("]");
			nodeArray = nodeArray2;
		} else
			nodeArray.append("NULL");

		for (Tag tag : myWay.getTags()) {
			String[] decoup = tag.toString().split("'");
			hstore.append("\"").append(escapeSQL(decoup[1])).append("\"=>\"").append(escapeSQL(decoup[3]))
					.append("\",");
		}
		if (hstore.length() > 0)
			hstore.deleteCharAt(hstore.length() - 1);

		wayValues.append("(" + myWay.getId() + myWay.getVersion() + "," + myWay.getId() + "," + myWay.getUser().getId()
				+ "," + myWay.getVersion() + "," + (int) myWay.getChangesetId() + "," + "\'"
				+ escapeSQL(myWay.getUser().getName()) + "\'" + "," + "\'" + myWay.getTimestamp() + "\'," + "\'"
				+ hstore + "\'," + nodeArray + "),");

		this.nbElem++;
		if (nbElem > 10000) {
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
			relmbValues.append("(" + myRelation.getId() + myRelation.getVersion() + "," + mb.getMemberId() + ",'"
					+ myRelation.getId() + myRelation.getVersion() + mb.getMemberId() + "'," + "\'"
					+ escapeSQL(mb.getMemberType().toString()) + "\'" + "," + "\'"
					+ escapeSQL(mb.getMemberRole().toString()) + "\'" + "),");
//			System.out.println("Relmb: " + escapeSQL(mb.getMemberType().toString()) + ","
//					+ escapeSQL(mb.getMemberRole().toString()));
			this.nbElem++;
//			System.out.println(" Fait en " + (System.nanoTime() - t1) + " ns");
		}

		/* Process relation */
//		System.out.println("Ecriture d'une relation...");
		for (Tag tag : myRelation.getTags()) {
			String[] decoup = tag.toString().split("'");
			hstore.append("\"" + escapeSQL(decoup[1]) + "\"=>\"" + escapeSQL(decoup[3]) + "\",");
		}
		if (hstore.length() > 0)
			hstore.deleteCharAt(hstore.length() - 1);

		t2 = System.nanoTime();

		relValues.append("(" + myRelation.getId() + myRelation.getVersion() + "," + myRelation.getId() + ","
				+ myRelation.getUser().getId() + "," + myRelation.getVersion() + "," + (int) myRelation.getChangesetId()
				+ "," + "\'" + escapeSQL(myRelation.getUser().getName()) + "\'" + "," + "\'" + myRelation.getTimestamp()
				+ "\'," + "\'" + hstore + "\'" + "),");
//		System.out.println("Relation" + escapeSQL(myRelation.getUser().getName()) + hstore);
//		System.out.println(" Faite en : " + (System.nanoTime() - t2) + " ns");
		this.nbElem++;
		if (nbElem > 10000) {
			relmbValues.deleteCharAt(relmbValues.length() - 1);
			myQueries.append("INSERT INTO relationmember (idrel,idmb,idrelmb,typemb,rolemb) VALUES ")
					.append(relmbValues).append(";");

			relValues.deleteCharAt(relValues.length() - 1);
			myQueries.append("INSERT INTO relation (idrel, id, uid, vrel,changeset,username, datemodif, tags) VALUES ")
					.append(relValues).append(";");
			executeQuery();
			relmbValues.setLength(0);
			relValues.setLength(0);
		}
	}

	public static String escapeSQL(String s) {
		int length = s.length();
		int newLength = length;
		// first check for characters that might
		// be dangerous and calculate a length
		// of the string that has escapes.
		for (int i = 0; i < length; i++) {
			char c = s.charAt(i);
			switch (c) {
			case '\\':
			case '\"':
			case '\'':
			case '\0': {
				newLength += 1;
			}
				break;
			}
		}
		if (length == newLength) {
			// nothing to escape in the string
			return s;
		}
		StringBuffer sb = new StringBuffer(newLength);
		for (int i = 0; i < length; i++) {
			char c = s.charAt(i);
			switch (c) {
			case '\\': {
				sb.append("\\\\");
			}
				break;
			case '\"': {
				sb.append("\\\"");
			}
				break;
			case '\'': {
				// sb.append("\\\'");
				sb.append("\'\'");
			}
				break;
			case '\0': {
				sb.append("\\0");
			}
				break;
			default: {
				sb.append(c);
			}
			}
		}
		return sb.toString();
	}
}
