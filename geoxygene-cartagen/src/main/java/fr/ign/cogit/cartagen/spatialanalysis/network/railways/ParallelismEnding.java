package fr.ign.cogit.cartagen.spatialanalysis.network.railways;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

public class ParallelismEnding {

  private ParallelismEndingType type;
  /**
   * A mapping between each stroke geometry and the ending position on this
   * stroke geometry
   **/
  private IDirectPosition position, positionOnCenter;

  public ParallelismEndingType getType() {
    return type;
  }

  public void setType(ParallelismEndingType type) {
    this.type = type;
  }

  public ParallelismEnding(ParallelismEndingType type,
      IDirectPosition position, IDirectPosition positionOnCenter) {
    super();
    this.type = type;
    this.setPositionOnCenter(positionOnCenter);
    this.setPosition(position);
  }

  public IDirectPosition getPosition() {
    return position;
  }

  public void setPosition(IDirectPosition position) {
    this.position = position;
  }

  public IDirectPosition getPositionOnCenter() {
    return positionOnCenter;
  }

  public void setPositionOnCenter(IDirectPosition positionOnCenter) {
    this.positionOnCenter = positionOnCenter;
  }

}
