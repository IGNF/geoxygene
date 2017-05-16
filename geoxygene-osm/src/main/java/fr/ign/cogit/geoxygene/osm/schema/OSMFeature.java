package fr.ign.cogit.geoxygene.osm.schema;

import java.util.Date;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

public interface OSMFeature extends IFeature {

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

}
