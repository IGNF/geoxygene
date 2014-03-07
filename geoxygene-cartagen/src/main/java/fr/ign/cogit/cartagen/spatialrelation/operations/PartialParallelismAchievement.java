package fr.ign.cogit.cartagen.spatialrelation.operations;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartagen.spatialrelation.api.AchievementMeasureOperation;
import fr.ign.cogit.cartagen.spatialrelation.relations.PartialParallelism2Lines;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

public class PartialParallelismAchievement implements
		AchievementMeasureOperation {

	private IFeature feature1, feature2;
	/**
	 * List of converging points on feature1 sorted on the feature1 geometry
	 * order.
	 */
	private List<IDirectPosition> convergingPts;
	/**
	 * List of diverging points on feature1 sorted on the feature1 geometry
	 * order.
	 */
	private List<IDirectPosition> divergingPts;

	private PartialParallelism2Lines relation;

	public PartialParallelismAchievement(IFeature feature1, IFeature feature2,
			PartialParallelism2Lines relation) {
		super();
		this.feature1 = feature1;
		this.feature2 = feature2;
		this.relation = relation;
		this.convergingPts = new ArrayList<IDirectPosition>();
		this.divergingPts = new ArrayList<IDirectPosition>();
	}

	@Override
	public void compute() {
		boolean parallel = false;
		ILineString line = null;
		if (feature2.getGeom() instanceof ILineString)
			line = (ILineString) feature2.getGeom();
		else if (feature2.getGeom() instanceof IPolygon)
			line = ((IPolygon) feature2.getGeom()).exteriorLineString();
		for (IDirectPosition pt : feature1.getGeom().coord()) {
			IDirectPosition closest = JtsAlgorithms.getClosestPoint(pt, line);
			double dist = closest.distance2D(pt);
			// if it is a parallel section, check if it's a diverging point
			if (parallel) {
				if (relation.getConditionOfAchievement().validate(
						new Double(dist))) {
					// TODO
				}
			} else {
				// else, check if this point is a converging point
				// TODO
			}
		}
		// TODO fill the relation fieilds if it is achieved

	}

	@Override
	public boolean measureAchievement() {
		if (convergingPts.size() != 0 || divergingPts.size() != 0)
			return true;
		return false;
	}

	public IFeature getFeature1() {
		return feature1;
	}

	public void setFeature1(IFeature feature1) {
		this.feature1 = feature1;
	}

	public IFeature getFeature2() {
		return feature2;
	}

	public void setFeature2(IFeature feature2) {
		this.feature2 = feature2;
	}

	public List<IDirectPosition> getConvergingPts() {
		return convergingPts;
	}

	public void setConvergingPts(List<IDirectPosition> convergingPts) {
		this.convergingPts = convergingPts;
	}

	public List<IDirectPosition> getDivergingPts() {
		return divergingPts;
	}

	public void setDivergingPts(List<IDirectPosition> divergingPts) {
		this.divergingPts = divergingPts;
	}

	public PartialParallelism2Lines getRelation() {
		return relation;
	}

	public void setRelation(PartialParallelism2Lines relation) {
		this.relation = relation;
	}

}
