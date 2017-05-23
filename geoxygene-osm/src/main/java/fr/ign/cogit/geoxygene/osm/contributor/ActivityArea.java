package fr.ign.cogit.geoxygene.osm.contributor;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.osm.schema.OSMFeature;

public class ActivityArea {
	private Collection<IFeature> polygons;

	public Collection<IFeature> getActivityAreas(IFeatureCollection<OSMFeature> contributions) {
		return polygons;

	}

}
