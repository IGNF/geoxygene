package fr.ign.cogit.geoxygene.osm.importexport;

import java.util.List;

public class OSMRelation extends PrimitiveGeomOSM {
  private TypeRelation type;
  private List<OsmRelationMember> membres;

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

  public void setMembres(List<OsmRelationMember> membres) {
    this.membres = membres;
  }

  public List<OsmRelationMember> getMembres() {
    return membres;
  }

  public OSMRelation(TypeRelation type, List<OsmRelationMember> membres) {
    this.type = type;
    this.membres = membres;
  }

}
