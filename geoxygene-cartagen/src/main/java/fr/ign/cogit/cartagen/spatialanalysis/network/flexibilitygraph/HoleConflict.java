package fr.ign.cogit.cartagen.spatialanalysis.network.flexibilitygraph;

import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class HoleConflict extends NetworkConflict {

  public HoleConflict(Set<INetworkSection> sections) {
    super(sections);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((getSections() == null) ? 0 : getSections().hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    HoleConflict other = (HoleConflict) obj;
    if (getSections() == null) {
      if (other.getSections() != null)
        return false;
    } else if (!getSections().equals(other.getSections()))
      return false;
    return true;
  }

  public static boolean isOverlap(INetworkSection section1,
      INetworkSection section2) {
    IGeometry geom1 = section1.getGeom().buffer(section1.getWidth());
    IGeometry geom2 = section2.getGeom().buffer(section2.getWidth());

    return geom1.intersects(geom2);
  }
}
