package fr.ign.cogit.geoxygene.osm.importexport.metrics;

import java.util.Comparator;

import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;

public class OSMResourceComparator implements Comparator<OSMResource> {

	@Override
	public int compare(OSMResource o1, OSMResource o2) {
		return o1.getDate().compareTo(o2.getDate());
	}

}
