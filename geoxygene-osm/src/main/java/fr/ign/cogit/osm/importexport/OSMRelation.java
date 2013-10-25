package fr.ign.cogit.osm.importexport;

import java.util.LinkedHashMap;

public class OSMRelation extends PrimitiveGeomOSM {
  private TypeRelation type;
  private LinkedHashMap<Long, RoleMembre> membres;

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

  public void setMembres(LinkedHashMap<Long, RoleMembre> membres) {
    this.membres = membres;
  }

  public LinkedHashMap<Long, RoleMembre> getMembres() {
    return membres;
  }

  public OSMRelation(TypeRelation type, LinkedHashMap<Long, RoleMembre> membres) {
    this.type = type;
    this.membres = membres;
  }

}
