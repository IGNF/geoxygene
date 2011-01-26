package fr.ign.cogit.geoxygene.style;

import java.util.ArrayList;
import java.util.List;

public class Interpolate {
  private List<InterpolationPoint> interpolationPoint = new ArrayList<InterpolationPoint>();

  public List<InterpolationPoint> getInterpolationPoint() {
    return this.interpolationPoint;
  }

  public void setInterpolationPoint(List<InterpolationPoint> interpolationPoint) {
    this.interpolationPoint = interpolationPoint;
  }
}
