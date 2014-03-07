package fr.ign.cogit.cartagen.spatialrelation.properties;

import fr.ign.cogit.cartagen.spatialrelation.api.RelationOperation;
import fr.ign.cogit.cartagen.spatialrelation.api.RelationProperty;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public class ParallelSection implements RelationProperty {

	private ILineString member1Section, member2Section;

	@Override
	public Object getValue() {
		ILineString[] value = new ILineString[2];
		value[0] = member1Section;
		value[1] = member2Section;
		return value;
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

}
