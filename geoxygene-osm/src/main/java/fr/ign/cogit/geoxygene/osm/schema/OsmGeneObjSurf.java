package fr.ign.cogit.geoxygene.osm.schema;

import java.util.Date;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * This class extends the CartAGenGeoObjDefault class. It handles CartAGen
 * surfacic objects that have a (persistent) artifact in a Gothic database.
 * @author Cecile Duchene, IGN-F, COGIT Lab.
 */
public abstract class OsmGeneObjSurf extends OsmGeneObj implements IGeneObjSurf {

  public OsmGeneObjSurf(String contributor, IGeometry geom, int id,
      int changeSet, int version, int uid, Date date) {
    super(contributor, geom, id, changeSet, version, uid, date);
  }

  public OsmGeneObjSurf() {
    super();
  }

  public OsmGeneObjSurf(IPolygon geom) {
    super();
    this.setInitialGeom(geom);
    this.setGeom(geom);
  }

  @Override
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

}
