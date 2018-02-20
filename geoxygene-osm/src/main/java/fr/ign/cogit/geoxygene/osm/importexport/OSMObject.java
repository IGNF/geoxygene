package fr.ign.cogit.geoxygene.osm.importexport;

import java.util.ArrayList;
import java.util.List;

public class OSMObject {

	// private List<OSMFeature> contributions;
	private List<OSMResource> contributions;
	private long osmId;
	public List<List<Long>> wayComposition;
	private String primitiveGeomOSM;

	public long getOsmId() {
		return osmId;
	}

	public void addcontribution(OSMResource resource) {
		this.getContributions().add(resource);

	}

	public void setContributions(List<OSMResource> contribution) {
		this.contributions = contribution;
	}

	public List<OSMResource> getContributions() {
		return contributions;
	}

	public OSMObject(long id) {
		this.osmId = id;
		this.contributions = new ArrayList<OSMResource>();
	}

	public String getPrimitiveGeomOSM() {
		return primitiveGeomOSM;
	}

	public void setPrimitiveGeomOSM(String primitiveGeomOSM) {
		this.primitiveGeomOSM = primitiveGeomOSM;
	}

}