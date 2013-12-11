package fr.ign.cogit.geoxygene.osm.importexport;

import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.RoleMembre;

public class OsmRelationMember {
  private RoleMembre role;
  private boolean node;
  private long ref;

  public OsmRelationMember(RoleMembre role, boolean node, long ref) {
    super();
    this.role = role;
    this.node = node;
    this.ref = ref;
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
}
