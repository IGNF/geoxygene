package fr.ign.cogit.cartagen.spatialanalysis.network.railways;

import java.util.Map;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public class ParallelismEnding {

  private ParallelismEndingType type;
  /**
   * A mapping between each stroke geometry and the ending position on this
   * stroke geometry
   **/
  private Map<ILineString, IDirectPosition> positionOnLines;

  public ParallelismEndingType getType() {
    return type;
  }

  public void setType(ParallelismEndingType type) {
    this.type = type;
  }

  public Map<ILineString, IDirectPosition> getPositionOnLines() {
    return positionOnLines;
  }

  public void setPositionOnLines(
      Map<ILineString, IDirectPosition> positionOnLines) {
    this.positionOnLines = positionOnLines;
  }

  public ParallelismEnding(ParallelismEndingType type,
      Map<ILineString, IDirectPosition> positionOnLines) {
    super();
    this.type = type;
    this.positionOnLines = positionOnLines;
  }

}
