package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public interface ILineSegment extends ILineString {

  public IDirectPosition getStartPoint();

  public IDirectPosition getEndPoint();

  public IGeometry intersection(IGeometry geom);
}
