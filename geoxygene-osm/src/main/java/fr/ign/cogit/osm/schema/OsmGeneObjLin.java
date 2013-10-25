package fr.ign.cogit.osm.schema;

import java.util.Date;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * This class extends the CartAGenGeoObjDefault class. It handles CartAGen
 * linear objects that have a (persistent) artifact in a Gothic database.
 * @author Cecile Duchene, IGN-F, COGIT Lab.
 */
public abstract class OsmGeneObjLin extends OsmGeneObj implements IGeneObjLin {

  public OsmGeneObjLin(String contributor, IGeometry geom, int id,
      int changeSet, int version, int uid, Date date) {
    super(contributor, geom, id, changeSet, version, uid, date);
  }

  public OsmGeneObjLin() {
    super();
  }

  @Override
  public ILineString getGeom() {
    return (ILineString) super.getGeom();
  }

}
