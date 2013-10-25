package fr.ign.cogit.osm.schema.urban;

import java.util.Date;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.osm.schema.OsmGeneObjSurf;

public class OsmBuilding extends OsmGeneObjSurf implements IBuilding {

  private String nature;

  public OsmBuilding(String contributor, IGeometry geom, int id, int changeSet,
      int version, int uid, Date date) {
    super(contributor, geom, id, changeSet, version, uid, date);
  }

  public OsmBuilding(IPolygon geom) {
    super();
    this.setGeom(geom);
  }

  @Override
  public IUrbanBlock getBlock() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBlock(IUrbanBlock block) {
    // TODO Auto-generated method stub

  }

  @Override
  public String getNature() {
    if (nature == null)
      computeNatureFromTags();
    return nature;
  }

  @Override
  public void setNature(String nature) {
    this.nature = nature;
  }

  @Override
  public IPolygon getSymbolGeom() {
    return getGeom();
  }

  private void computeNatureFromTags() {
    // TODO
  }
}
