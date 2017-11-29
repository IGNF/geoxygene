package fr.ign.cogit.geoxygene.osm.schema;

import java.util.Date;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

public interface OSMFeature extends IFeature, Comparable<OSMFeature> {

	public String getContributor();

	public void setContributor(String contributor);

	public OsmSource getSource();

	public void setSource(OsmSource source);

	public OsmCaptureTool getCaptureTool();

	public void setCaptureTool(OsmCaptureTool captureTool);

	public long getOsmId();

	public void setOsmId(long id);

	public int getChangeSet();

	public void setChangeSet(int changeSet);

	public int getVersion();

	public void setVersion(int version);

	public int getUid();

	public void setUid(int uid);

	public Map<String, String> getTags();

	public void setTags(Map<String, String> tags);

	public Date getDate();

	public void setDate(Date date);

	// String values for the general tags of OSM data
	public static final String TAG_SOURCE = "source";
	public static final String TAG_OUTIL = "created_by";
	public static final String TAG_NODE = "node";
	public static final String TAG_WAY = "way";
	public static final String TAG_REL = "relation";
	public static final String ATTR_USER = "user";
	public static final String ATTR_DATE = "timestamp";
	public static final String ATTR_SET = "changeset";
	public static final String ATTR_ID = "id";
	public static final String ATTR_UID = "uid";
	public static final String ATTR_VERSION = "version";
	public static final String ATTR_LAT = "lat";
	public static final String ATTR_LON = "lon";

}
