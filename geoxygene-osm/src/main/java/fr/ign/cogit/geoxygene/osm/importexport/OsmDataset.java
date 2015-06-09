package fr.ign.cogit.geoxygene.osm.importexport;

import fr.ign.cogit.cartagen.core.genericschema.land.ITreePoint;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.ICycleWay;
import fr.ign.cogit.cartagen.core.genericschema.urban.ICemetery;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.osm.schema.amenity.OsmHospital;
import fr.ign.cogit.geoxygene.osm.schema.amenity.OsmSchool;
import fr.ign.cogit.geoxygene.osm.schema.urban.OsmCemetery;

public class OsmDataset extends CartAGenDataSet {

  // ///////////////////////////////////////
  // STANDARD NAMES OF DATASET POPULATIONS
  // ///////////////////////////////////////

  public static final String TREE_POINT_POP = "trees";
  public static final String CYCLEWAY_POP = "cycleWay";
  public static final String CEMETERY_POP = "cemeteries";
  public static final String SCHOOL_POP = "schools";
  public static final String HOSPITAL_POP = "hospitals";

  @Override
  public String getPopNameFromObj(IFeature obj) {
    if (obj instanceof ITreePoint) {
      return OsmDataset.TREE_POINT_POP;
    }
    if (obj instanceof ICycleWay) {
      return OsmDataset.CYCLEWAY_POP;
    }
    if (obj instanceof OsmCemetery) {
      return OsmDataset.CEMETERY_POP;
    }
    if (obj instanceof OsmSchool) {
      return OsmDataset.SCHOOL_POP;
    }
    if (obj instanceof OsmHospital) {
      return OsmDataset.HOSPITAL_POP;
    }
    return super.getPopNameFromObj(obj);
  }

  @Override
  public String getPopNameFromClass(Class<?> classObj) {
    if (ITreePoint.class.isAssignableFrom(classObj)) {
      return OsmDataset.TREE_POINT_POP;
    }
    if (ICycleWay.class.isAssignableFrom(classObj)) {
      return OsmDataset.CYCLEWAY_POP;
    }
    if (OsmCemetery.class.isAssignableFrom(classObj)) {
      return OsmDataset.CEMETERY_POP;
    }
    if (OsmSchool.class.isAssignableFrom(classObj)) {
      return OsmDataset.SCHOOL_POP;
    }
    if (OsmHospital.class.isAssignableFrom(classObj)) {
      return OsmDataset.HOSPITAL_POP;
    }
    return super.getPopNameFromClass(classObj);
  }

  @Override
  public String getPopNameFromFeatType(String featureType) {
    if (featureType.equals(ITreePoint.FEAT_TYPE_NAME)) {
      return OsmDataset.TREE_POINT_POP;
    }
    if (featureType.equals(ICycleWay.FEAT_TYPE_NAME)) {
      return OsmDataset.CYCLEWAY_POP;
    }
    if (featureType.equals(OsmCemetery.FEAT_TYPE_NAME)) {
      return OsmDataset.CEMETERY_POP;
    }
    if (featureType.equals(OsmSchool.FEAT_TYPE_NAME)) {
      return OsmDataset.SCHOOL_POP;
    }
    if (featureType.equals(OsmHospital.FEAT_TYPE_NAME)) {
      return OsmDataset.HOSPITAL_POP;
    }
    return super.getPopNameFromFeatType(featureType);
  }

  /**
   * Gets the tree points of the dataset
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ITreePoint> getTreePoints() {
    return (IPopulation<ITreePoint>) this.getCartagenPop(
        OsmDataset.TREE_POINT_POP, ITreePoint.FEAT_TYPE_NAME);
  }

  /**
   * Gets the cycleways of the dataset
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ICycleWay> getCycleWays() {
    return (IPopulation<ICycleWay>) this.getCartagenPop(
        OsmDataset.CYCLEWAY_POP, INetworkSection.FEAT_TYPE_NAME);
  }

  /**
   * Gets the cemeteries of the dataset
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ICemetery> getCemeteries() {
    return (IPopulation<ICemetery>) this.getCartagenPop(
        OsmDataset.CEMETERY_POP, ICemetery.FEAT_TYPE_NAME);
  }

  /**
   * Gets the schools of the dataset
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<OsmSchool> getSchools() {
    return (IPopulation<OsmSchool>) this.getCartagenPop(OsmDataset.SCHOOL_POP,
        OsmSchool.FEAT_TYPE_NAME);
  }

  /**
   * Gets the hospitals of the dataset
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<OsmHospital> getHospitals() {
    return (IPopulation<OsmHospital>) this.getCartagenPop(
        OsmDataset.HOSPITAL_POP, OsmHospital.FEAT_TYPE_NAME);
  }

}
