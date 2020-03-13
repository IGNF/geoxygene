package fr.ign.cogit.geoxygene.osm.importexport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.RoleMembre;
import fr.ign.cogit.geoxygene.osm.importexport.postgis.LoadFromPostGIS;

public class OsmRelationMember {
	private RoleMembre role;
	private boolean node;
	private boolean way;
	private boolean relation;
	private long ref;

	public static String host = "localhost";
	public static String port = "5432";
	public static String dbName = ""; // A définir avant de faire appel à la BD
	public static String dbUser = "postgres";
	public static String dbPwd = "postgres";

	public OsmRelationMember(RoleMembre role, boolean node, long ref) {
		this.role = role;
		this.node = node;
		this.ref = ref;
	}

	public OsmRelationMember(RoleMembre role, boolean node, boolean way, boolean relation, long ref) {
		this.role = role;
		this.node = node;
		this.way = node;
		this.relation = node;
		this.ref = ref;
	}

	/**
	 * Get the members of an OSM relation element.
	 * 
	 * @param id
	 *            : OSM relation element ID
	 * @param version
	 *            : version of the relation element
	 * @return
	 * @throws SQLException
	 */
	public static List<OsmRelationMember> makeMember(Long id, Integer version) throws SQLException {
		LoadFromPostGIS ld = new LoadFromPostGIS(host, port, dbName, dbUser, dbPwd);
		System.out.println("SELECT * FROM relationmember WHERE idrel=" + id + "" + version);
		ResultSet r = ld.executeQuery("SELECT * FROM relationmember WHERE idrel=" + id + "" + version + ";");
		List<OsmRelationMember> members = new ArrayList<OsmRelationMember>();

		while (r.next()) {
			boolean isNode = false, isWay = false, isRel = false;
			if (r.getString("typemb").equals("n")) {
				isNode = true;
			} else if (r.getString("typemb").equals("w")) {
				isWay = true;
			} else if (r.getString("typemb").equals("r")) {
				isRel = true;
			}
			OsmRelationMember mb = new OsmRelationMember(RoleMembre.valueOfTexte(r.getString("rolemb")), isNode, isWay,
					isRel, r.getLong("idmb"));
			members.add(mb);
		}
		return members;
	}

	public RoleMembre getRole() {
		return role;
	}

	public void setRole(RoleMembre role) {
		this.role = role;
	}

	public boolean isNode() {
		return node;
	}

	public void setNode(boolean node) {
		this.node = node;
	}

	public long getRef() {
		return ref;
	}

	public void setRef(long ref) {
		this.ref = ref;
	}

	public boolean isWay() {
		return way;
	}

	public void setWay(boolean way) {
		this.way = way;
	}

	public boolean isRelation() {
		return relation;
	}

	public void setRelation(boolean relation) {
		this.relation = relation;
	}
}
