package fr.ign.cogit.geoxygene.osm.importexport;

import java.util.List;

public class OSMWay extends PrimitiveGeomOSM {

	private List<Long> vertices;

	public List<Long> getVertices() {
		return vertices;
	}

	public void setVertices(List<Long> vertices) {
		this.vertices = vertices;
	}

	public OSMWay(List<Long> vertices) {
		this.vertices = vertices;
	}

	/**
	 * A way represents a polygon if first and last vertex are the same.
	 * 
	 * @return
	 */
	public boolean isPolygon() {
		if (getVertices().get(0).equals(getVertices().get(getVertices().size() - 1)))
			return true;
		return false;
	}

	public boolean isVerticeEquals(OSMWay way) {
		return this.vertices.equals(way.vertices);
	}
}
