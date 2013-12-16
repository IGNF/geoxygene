package fr.ign.cogit.geoxygene.osm.schema.amenity;

import fr.ign.cogit.cartagen.core.genericschema.FunctionalSite;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.misc.IBoundedArea;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObj;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObjSurf;

public class OsmHospital extends OsmGeneObjSurf implements IBoundedArea,
    FunctionalSite {

  public static final String FEAT_TYPE_NAME = "HospitalBounds"; //$NON-NLS-1$

  public OsmHospital(IPolygon poly) {
    super(poly);
  }

  @Override
  public int getFunctionalBelonging(IGeneObj obj) {
    if (!(obj instanceof OsmGeneObj))
      return 0;
    int functionalScore = 0;
    OsmGeneObj osmObj = (OsmGeneObj) obj;
    // positive tags
    if (osmObj.getTags().containsKey("building"))
      functionalScore++;
    if ("hospital".equals(osmObj.getTags().get("building")))
      functionalScore += 5;
    if ("church".equals(osmObj.getTags().get("building")))
      functionalScore += 3;
    if ("chapel".equals(osmObj.getTags().get("building")))
      functionalScore += 3;
    if ("civic".equals(osmObj.getTags().get("building")))
      functionalScore += 3;
    if ("hospital".equals(osmObj.getTags().get("amenity")))
      functionalScore += 10;
    if ("doctors".equals(osmObj.getTags().get("amenity")))
      functionalScore += 10;
    if ("nursing_home".equals(osmObj.getTags().get("amenity")))
      functionalScore += 10;
    if ("service".equals(osmObj.getTags().get("highway")))
      functionalScore += 3;

    // negative tags
    if (osmObj.getTags().containsKey("sport"))
      functionalScore -= 3;
    if ("pitch".equals(osmObj.getTags().get("leisure")))
      functionalScore -= 5;

    return functionalScore;
  }

  @Override
  public int getSpatialBelonging(IGeneObj obj) {
    if (!this.getGeom().intersects(obj.getGeom()))
      return -20;
    if (obj.getGeom() instanceof IPolygon) {
      IPolygon pol = (IPolygon) obj.getGeom();
      double overlap = pol.intersection(getGeom()).area() / pol.area();
      // above 0.6, it's a positive likelihood
      if (overlap > 0.6) {
        return (int) Math.round((overlap - 0.6) * 20);
      }
      // below 0.3, it's a negative likelihood
      if (overlap < 0.3) {
        return (int) -Math.round((0.3 - overlap) * 20);
      }
    }
    // the linear case
    if (obj.getGeom() instanceof ILineString) {
      if (this.getGeom().contains(obj.getGeom()))
        return 10;
      IGeometry inter = this.getGeom().exteriorLineString()
          .intersection(obj.getGeom());
      if (inter instanceof IMultiPoint)
        return 5;
    }
    return 0;
  }

}
