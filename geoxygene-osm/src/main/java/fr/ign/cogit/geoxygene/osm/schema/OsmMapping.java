package fr.ign.cogit.geoxygene.osm.schema;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayLine;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.ICoastLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.land.ITreePoint;
import fr.ign.cogit.cartagen.core.genericschema.misc.IPointOfInterest;
import fr.ign.cogit.cartagen.core.genericschema.railway.ICable;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementPoint;
import fr.ign.cogit.cartagen.core.genericschema.road.ICycleWay;
import fr.ign.cogit.cartagen.core.genericschema.road.IPathLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISportsField;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISquareArea;
import fr.ign.cogit.cartagen.mrdb.scalemaster.GeometryType;
import fr.ign.cogit.geoxygene.osm.schema.amenity.OsmHospital;
import fr.ign.cogit.geoxygene.osm.schema.amenity.OsmSchool;
import fr.ign.cogit.geoxygene.osm.schema.urban.OsmCemetery;

/**
 * Class that stores mapping between OSM RDF resources and {@link IGeneObj} core
 * schema.
 * 
 * @author Guillaume
 * 
 */
public class OsmMapping {

  private Set<OsmMatching> matchings;

  public class OsmMatching {

    private String tag;
    private Class<?> cartagenClass;
    private GeometryType type;
    private Set<String> tagValues;

    public String getTag() {
      return tag;
    }

    public void setTag(String tag) {
      this.tag = tag;
    }

    public Class<?> getCartagenClass() {
      return cartagenClass;
    }

    public void setCartagenClass(Class<?> cartagenClass) {
      this.cartagenClass = cartagenClass;
    }

    public OsmMatching(String tag, Class<?> cartagenClass, GeometryType type) {
      super();
      this.tag = tag;
      this.cartagenClass = cartagenClass;
      this.setType(type);
      this.tagValues = new HashSet<String>();
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ((tag == null) ? 0 : tag.hashCode());
      result = prime * result
          + ((tagValues == null) ? 0 : tagValues.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      OsmMatching other = (OsmMatching) obj;
      if (!getOuterType().equals(other.getOuterType()))
        return false;
      if (tag == null) {
        if (other.tag != null)
          return false;
      } else if (!tag.equals(other.tag))
        return false;
      if (tagValues == null) {
        if (other.tagValues != null)
          return false;
      } else if (!tagValues.equals(other.tagValues))
        return false;
      if (type == null) {
        if (other.type != null)
          return false;
      } else if (!type.equals(other.type))
        return false;
      return true;
    }

    public Set<String> getTagValues() {
      return tagValues;
    }

    public void setTagValues(Set<String> tagValues) {
      this.tagValues = tagValues;
    }

    public void addTagValue(String value) {
      this.tagValues.add(value);
    }

    public void addTagValue(String[] values) {
      for (String value : values)
        this.tagValues.add(value);
    }

    public GeometryType getType() {
      return type;
    }

    public void setType(GeometryType type) {
      this.type = type;
    }

    private OsmMapping getOuterType() {
      return OsmMapping.this;
    }

    @Override
    public String toString() {
      return "OsmMatching [tag=" + tag + ", cartagenClass=" + cartagenClass
          + ", type=" + type + ", tagValues=" + tagValues + "]";
    }
  }

  /**
   * Default constructor with default matchings.
   */
  public OsmMapping() {
    this.matchings = new HashSet<OsmMapping.OsmMatching>();

    // building matching
    this.matchings.add(new OsmMatching("building", IBuilding.class,
        GeometryType.POLYGON));
    // sports fields matching
    this.matchings.add(new OsmMatching("sport", ISportsField.class,
        GeometryType.POLYGON));
    // rivers matching
    OsmMatching rivermatching = new OsmMatching("waterway", IWaterLine.class,
        GeometryType.LINE);
    rivermatching.addTagValue("stream");
    rivermatching.addTagValue("river");
    rivermatching.addTagValue("canal");
    this.matchings.add(rivermatching);
    OsmMatching riverAreaMatching = new OsmMatching("waterway",
        IWaterArea.class, GeometryType.POLYGON);
    riverAreaMatching.addTagValue("riverbank");
    this.matchings.add(riverAreaMatching);
    // lakes matching
    OsmMatching lakematching = new OsmMatching("natural", IWaterArea.class,
        GeometryType.POLYGON);
    lakematching.addTagValue("water");
    lakematching.addTagValue("lake");
    this.matchings.add(lakematching);
    // shops matchings
    this.matchings.add(new OsmMatching("shop", IPointOfInterest.class,
        GeometryType.POINT));
    this.matchings.add(new OsmMatching("amenity", IPointOfInterest.class,
        GeometryType.POINT));
    OsmMatching busmatching = new OsmMatching("highway",
        IPointOfInterest.class, GeometryType.POINT);
    busmatching.addTagValue("bus_stop");
    this.matchings.add(busmatching);

    // roads matching
    OsmMatching roadmatching = new OsmMatching("highway", IRoadLine.class,
        GeometryType.LINE);
    roadmatching.addTagValue("road");
    roadmatching.addTagValue("track");
    roadmatching.addTagValue("service");
    roadmatching.addTagValue("unclassified");
    roadmatching.addTagValue("residential");
    roadmatching.addTagValue("pedestrian");
    roadmatching.addTagValue("living_street");
    roadmatching.addTagValue("tertiary_link");
    roadmatching.addTagValue("tertiary");
    roadmatching.addTagValue("secondary_link");
    roadmatching.addTagValue("secondary");
    roadmatching.addTagValue("primary_link");
    roadmatching.addTagValue("primary");
    roadmatching.addTagValue("trunk_link");
    roadmatching.addTagValue("motorway_link");
    roadmatching.addTagValue("motorway");
    roadmatching.addTagValue("trunk");
    this.matchings.add(roadmatching);

    // paths matching
    OsmMatching pathmatching = new OsmMatching("highway", IPathLine.class,
        GeometryType.LINE);
    roadmatching.addTagValue("path");
    roadmatching.addTagValue("footway");
    roadmatching.addTagValue("bridleway");
    roadmatching.addTagValue("steps");
    this.matchings.add(pathmatching);

    // airports and runways
    OsmMatching airportMatch = new OsmMatching("aeroway", IAirportArea.class,
        GeometryType.POLYGON);
    airportMatch.addTagValue("aerodrome");
    this.matchings.add(airportMatch);
    OsmMatching runwayAreaMatch = new OsmMatching("aeroway", IRunwayArea.class,
        GeometryType.POLYGON);
    runwayAreaMatch.addTagValue("runway");
    this.matchings.add(runwayAreaMatch);
    OsmMatching runwayLineMatch = new OsmMatching("aeroway", IRunwayLine.class,
        GeometryType.LINE);
    runwayLineMatch.addTagValue("runway");
    this.matchings.add(runwayLineMatch);
    OsmMatching taxiwayLineMatch = new OsmMatching("aeroway",
        ITaxiwayLine.class, GeometryType.LINE);
    taxiwayLineMatch.addTagValue("taxiway");
    this.matchings.add(taxiwayLineMatch);
    OsmMatching taxiwayAreaMatch = new OsmMatching("aeroway",
        ITaxiwayArea.class, GeometryType.POLYGON);
    taxiwayAreaMatch.addTagValue("apron");
    taxiwayAreaMatch.addTagValue("taxiway");
    this.matchings.add(taxiwayAreaMatch);
    OsmMatching terminalMatch = new OsmMatching("aeroway", IBuilding.class,
        GeometryType.POLYGON);
    terminalMatch.addTagValue("terminal");
    terminalMatch.addTagValue("hangar");
    this.matchings.add(terminalMatch);

    // cable matching
    this.matchings.add(new OsmMatching("aerialway", ICable.class,
        GeometryType.LINE));
    // railways
    this.matchings.add(new OsmMatching("railway", IRailwayLine.class,
        GeometryType.LINE));

    // landuse
    OsmMatching vineyardMatch = new OsmMatching("landuse",
        ISimpleLandUseArea.class, GeometryType.POLYGON);
    vineyardMatch.addTagValue("vineyard");
    this.matchings.add(vineyardMatch);
    OsmMatching retailMatch = new OsmMatching("landuse",
        ISimpleLandUseArea.class, GeometryType.POLYGON);
    retailMatch.addTagValue("retail");
    this.matchings.add(retailMatch);
    OsmMatching residentialMatch = new OsmMatching("landuse",
        ISimpleLandUseArea.class, GeometryType.POLYGON);
    residentialMatch.addTagValue("residential");
    this.matchings.add(residentialMatch);
    OsmMatching railwayMatch = new OsmMatching("landuse",
        ISimpleLandUseArea.class, GeometryType.POLYGON);
    railwayMatch.addTagValue("railway");
    this.matchings.add(railwayMatch);
    OsmMatching orchardMatch = new OsmMatching("landuse",
        ISimpleLandUseArea.class, GeometryType.POLYGON);
    orchardMatch.addTagValue("orchard");
    this.matchings.add(orchardMatch);
    OsmMatching meadowMatch = new OsmMatching("landuse",
        ISimpleLandUseArea.class, GeometryType.POLYGON);
    meadowMatch.addTagValue("meadow");
    this.matchings.add(meadowMatch);
    OsmMatching industrialMatch = new OsmMatching("landuse",
        ISimpleLandUseArea.class, GeometryType.POLYGON);
    industrialMatch.addTagValue("industrial");
    this.matchings.add(industrialMatch);
    OsmMatching forestMatch = new OsmMatching("landuse",
        ISimpleLandUseArea.class, GeometryType.POLYGON);
    forestMatch.addTagValue("forest");
    this.matchings.add(forestMatch);
    OsmMatching farmMatch = new OsmMatching("landuse",
        ISimpleLandUseArea.class, GeometryType.POLYGON);
    farmMatch.addTagValue("farmland");
    farmMatch.addTagValue("farm");
    this.matchings.add(farmMatch);
    OsmMatching commMatch = new OsmMatching("landuse",
        ISimpleLandUseArea.class, GeometryType.POLYGON);
    commMatch.addTagValue("commercial");
    this.matchings.add(commMatch);
    OsmMatching beachMatch = new OsmMatching("natural",
        ISimpleLandUseArea.class, GeometryType.POLYGON);
    beachMatch.addTagValue("beach");
    this.matchings.add(beachMatch);

    // tree matching
    OsmMatching treematching = new OsmMatching("natural", ITreePoint.class,
        GeometryType.POINT);
    treematching.addTagValue("tree");
    this.matchings.add(treematching);

    // relief points matching
    OsmMatching reliefPtsMatching = new OsmMatching("natural",
        IReliefElementPoint.class, GeometryType.POINT);
    reliefPtsMatching.addTagValue("peak");
    reliefPtsMatching.addTagValue("saddle");
    this.matchings.add(reliefPtsMatching);

    // cycle way
    OsmMatching cyclewaymatching = new OsmMatching("highway", ICycleWay.class,
        GeometryType.LINE);
    cyclewaymatching.addTagValue("cycleway");
    this.matchings.add(cyclewaymatching);

    // coastlines
    OsmMatching coastlineMatching = new OsmMatching("natural",
        ICoastLine.class, GeometryType.LINE);
    coastlineMatching.addTagValue("coastline");
    this.matchings.add(coastlineMatching);

    // leisure features
    OsmMatching parkMatching = new OsmMatching("leisure", ISquareArea.class,
        GeometryType.POLYGON);
    parkMatching.addTagValue("park");
    this.matchings.add(parkMatching);

    // cemeteries
    OsmMatching cemeteryMatching = new OsmMatching("landuse",
        OsmCemetery.class, GeometryType.POLYGON);
    cemeteryMatching.addTagValue("cemetery");
    this.matchings.add(cemeteryMatching);
    OsmMatching graveyardMatching = new OsmMatching("amenity",
        OsmCemetery.class, GeometryType.POLYGON);
    graveyardMatching.addTagValue("grave_yard");
    this.matchings.add(graveyardMatching);

    // schools
    OsmMatching schoolMatching = new OsmMatching("amenity", OsmSchool.class,
        GeometryType.POLYGON);
    schoolMatching.addTagValue("school");
    schoolMatching.addTagValue("university");
    this.matchings.add(schoolMatching);
    // hospitals
    OsmMatching hospitalMatching = new OsmMatching("amenity",
        OsmHospital.class, GeometryType.POLYGON);
    hospitalMatching.addTagValue("hospital");
    this.matchings.add(hospitalMatching);
    // TODO à remplir
  }

  public Set<OsmMatching> getMatchings() {
    return matchings;
  }

  public void setMatchings(Set<OsmMatching> matchings) {
    this.matchings = matchings;
  }
}
