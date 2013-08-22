package fr.ign.cogit.cartagen.spatialanalysis.clustering;

import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.util.SpatialQuery;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;

public class KMeansCluster {
  private int id;
  private Set<IGeneObj> features;
  private IDirectPosition center;
  private double diameter;

  public KMeansCluster(int id, Set<IGeneObj> features, IDirectPosition center) {
    super();
    this.id = id;
    this.features = features;
    this.center = center;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Set<IGeneObj> getFeatures() {
    return this.features;
  }

  public void setFeatures(Set<IGeneObj> features) {
    this.features = features;
  }

  public IDirectPosition getCenter() {
    return this.center;
  }

  public void setCenter(IDirectPosition center) {
    this.center = center;
  }

  @Override
  public int hashCode() {
    return this.id;
  }

  public int size() {
    return this.getFeatures().size();
  }

  public void computeCenter() {
    IDirectPositionList ptList = new DirectPositionList();
    for (IGeneObj obj : this.getFeatures()) {
      ptList.add(obj.getGeom().centroid());
    }
    IMultiPoint geom = new GM_MultiPoint(ptList);
    if (!geom.isEmpty()) {
      System.out.println(geom.toString());
      this.center = geom.centroid();
      System.out.println(geom.centroid().toString());
      this.diameter = geom.envelope().width();
    }
  }

  public IGeneObj getCenterNearest() {
    IFeatureCollection<IGeneObj> fc = new FT_FeatureCollection<IGeneObj>();
    fc.addAll(this.features);
    return SpatialQuery.selectNearest(this.center.toGM_Point(), fc,
        this.diameter);
  }

  public Class<?> getFeaturesClass() {
    return this.getFeatures().iterator().next().getClass();
  }
}
