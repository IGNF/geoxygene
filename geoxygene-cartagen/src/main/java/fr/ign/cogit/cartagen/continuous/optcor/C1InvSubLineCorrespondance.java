package fr.ign.cogit.cartagen.continuous.optcor;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

public class C1InvSubLineCorrespondance implements SubLineCorrespondance {

    private ILineString subLine;
    private IDirectPosition characPt;

    public C1InvSubLineCorrespondance(IDirectPosition characPt,
            ILineString subLine) {
        super();
        this.subLine = subLine;
        this.characPt = characPt;
    }

    @Override
    public List<Object> getMatchedFeaturesInitial() {
        List<Object> matchedFeatures = new ArrayList<Object>();
        matchedFeatures.add(subLine);
        return matchedFeatures;
    }

    @Override
    public List<Object> getMatchedFeaturesFinal() {
        List<Object> matchedFeatures = new ArrayList<Object>();
        matchedFeatures.add(characPt);
        return matchedFeatures;
    }

    @Override
    public CorrespondanceType getType() {
        return CorrespondanceType.C1_;
    }

    @Override
    public IDirectPositionList morphCorrespondance(double t) {
        // then, compute the intermediate position between each correspondant
        IDirectPositionList coord = new DirectPositionList();
        for (IDirectPosition pt1 : subLine.coord()) {
            double newX = characPt.getX() + t * (pt1.getX() - characPt.getX());
            double newY = characPt.getY() + t * (pt1.getY() - characPt.getY());
            IDirectPosition newPt = new DirectPosition(newX, newY);
            coord.add(newPt);
        }

        return coord;
    }

}
