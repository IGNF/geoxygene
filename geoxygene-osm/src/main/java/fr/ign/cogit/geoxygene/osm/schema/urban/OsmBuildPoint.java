package fr.ign.cogit.geoxygene.osm.schema.urban;

import java.util.Date;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObjPoint;

public class OsmBuildPoint extends OsmGeneObjPoint implements IBuildPoint {

  public OsmBuildPoint(String contributor, IGeometry geom, int id,
      int changeSet, int version, int uid, Date date) {
    super(contributor, geom, id, changeSet, version, uid, date);
  }

  public OsmBuildPoint(IPoint point) {
    super(point);
  }
}
