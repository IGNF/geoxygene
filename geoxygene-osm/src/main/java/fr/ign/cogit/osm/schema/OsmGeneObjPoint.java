package fr.ign.cogit.osm.schema;

import java.util.Date;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * This class extends the OsmGeneObj class for ponctual objects, overriding the
 * getGeom() method.
 * @author gtouya
 */
public abstract class OsmGeneObjPoint extends OsmGeneObj implements
    IGeneObjPoint {

  public OsmGeneObjPoint(String contributor, IGeometry geom, int id,
      int changeSet, int version, int uid, Date date) {
    super(contributor, geom, id, changeSet, version, uid, date);
  }

  public OsmGeneObjPoint() {
    super();
  }

  @Override
  public IPoint getGeom() {
    return (IPoint) super.getGeom();
  }
}
