package fr.ign.cogit.osm.schema.network;

import java.util.Date;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.NetworkSectionType;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;
import fr.ign.cogit.osm.schema.OsmGeneObjLin;

public abstract class OsmNetworkSection extends OsmGeneObjLin implements
    INetworkSection {

  private int importance;
  private Direction direction;
  private boolean deadEnd;
  private NetworkSectionType type;
  private INetworkFace rightFace, leftFace;

  public OsmNetworkSection(String contributor, IGeometry geom, int id,
      int changeSet, int version, int uid, Date date) {
    super(contributor, geom, id, changeSet, version, uid, date);
  }

  public OsmNetworkSection() {
    super();
    direction = Direction.DIRECT;
    deadEnd = false;
  }

  @Override
  public int getImportance() {
    return importance;
  }

  @Override
  public void setImportance(int importance) {
    this.importance = importance;
  }

  @Override
  public boolean isAnalog(INetworkSection at) {
    return this.importance == at.getImportance();
  }

  @Override
  public Direction getDirection() {
    return direction;
  }

  @Override
  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  @Override
  public boolean isDeadEnd() {
    return deadEnd;
  }

  @Override
  public void setDeadEnd(boolean deadEnd) {
    this.deadEnd = deadEnd;
  }

  @Override
  public NetworkSectionType getNetworkSectionType() {
    return type;
  }

  @Override
  public void setNetworkSectionType(NetworkSectionType type) {
    this.type = type;
  }

  @Override
  public INetworkFace getRightFace() {
    return rightFace;
  }

  public void setRightFace(INetworkFace rightFace) {
    this.rightFace = rightFace;
  }

  @Override
  public INetworkFace getLeftFace() {
    return leftFace;
  }

  public void setLeftFace(INetworkFace leftFace) {
    this.leftFace = leftFace;
  }

}
