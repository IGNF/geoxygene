package fr.ign.cogit.cartagen.spatialrelation.properties;

import fr.ign.cogit.cartagen.spatialrelation.api.RelationOperation;
import fr.ign.cogit.cartagen.spatialrelation.api.RelationProperty;
import fr.ign.cogit.cartagen.spatialrelation.relations.PartialParallelism2Lines;

public class DistanceInParallelPortions implements RelationProperty {

	private double distance;
	private PartialParallelism2Lines relation;

	@Override
	public Object getValue() {
		return distance;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public RelationOperation getOperation() {
		// TODO Auto-generated method stub
		return null;
	}

	public PartialParallelism2Lines getRelation() {
		return relation;
	}

	public void setRelation(PartialParallelism2Lines relation) {
		this.relation = relation;
	}

}
