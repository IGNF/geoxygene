package fr.ign.cogit.geoxygene.osm.importexport;

import java.util.ArrayList;
import java.util.List;

public class OSMRelation extends PrimitiveGeomOSM {
	private TypeRelation type;
	private List<OsmRelationMember> members;

	public enum TypeRelation {
		MULTIPOLYGON, NON_DEF;

		public static TypeRelation valueOfTexte(String txt) {
			if (txt.equals("multipolygon"))
				return MULTIPOLYGON;
			return NON_DEF;
		}
	}

	public enum RoleMembre {
		OUTER, INNER, NON_DEF;

		public static RoleMembre valueOfTexte(String txt) {
			if (txt.equals("inner"))
				return INNER;
			if (txt.equals("outer"))
				return OUTER;
			return NON_DEF;
		}
	}

	public static final String TAG_TYPE = "type";

	public TypeRelation getType() {
		return type;
	}

	public void setType(TypeRelation type) {
		this.type = type;
	}

	public void setMembers(List<OsmRelationMember> members) {
		this.members = members;
	}

	public List<OsmRelationMember> getMembers() {
		return members;
	}

	public OSMRelation(TypeRelation type, List<OsmRelationMember> members) {
		this.type = type;
		this.members = members;
	}

	/**
	 * Get the members of this relation with the role "outer".
	 * 
	 * @return
	 */
	public List<OsmRelationMember> getOuterMembers() {
		List<OsmRelationMember> outers = new ArrayList<OsmRelationMember>();
		for (OsmRelationMember member : this.getMembers()) {
			if (member.getRole().equals(RoleMembre.OUTER))
				outers.add(member);
		}
		return outers;
	}

	/**
	 * Get the members of this relation with the role "inner".
	 * 
	 * @return
	 */
	public List<OsmRelationMember> getInnerMembers() {
		List<OsmRelationMember> inners = new ArrayList<OsmRelationMember>();
		for (OsmRelationMember member : this.getMembers()) {
			if (member.getRole().equals(RoleMembre.INNER))
				inners.add(member);
		}
		return inners;
	}

	/**
	 * Get the members of this relation with the role "inner".
	 * 
	 * @return
	 */
	public List<Long> getMembersID() {
		List<Long> membersID = new ArrayList<Long>();
		for (OsmRelationMember member : this.getMembers()) {
			membersID.add(member.getRef());
		}
		return membersID;
	}

	public boolean isMemberListEquals(OSMRelation relation) {
		if (this.getMembers().equals(relation.getMembers()))
			return true;
		else
			return false;

	}
}
