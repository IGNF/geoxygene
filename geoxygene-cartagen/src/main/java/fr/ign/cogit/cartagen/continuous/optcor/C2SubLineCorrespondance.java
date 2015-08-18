package fr.ign.cogit.cartagen.continuous.optcor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

public class C2SubLineCorrespondance implements SubLineCorrespondance {

    private ILineString subLine1;
    private ILineString subLine2;

    public C2SubLineCorrespondance(ILineString subLineInitial,
            ILineString subLineFinal) {
        super();
        this.subLine1 = subLineInitial;
        this.subLine2 = subLineFinal;
    }

    @Override
    public List<Object> getMatchedFeaturesInitial() {
        List<Object> matchedFeatures = new ArrayList<Object>();
        matchedFeatures.add(subLine1);
        return matchedFeatures;
    }

    @Override
    public List<Object> getMatchedFeaturesFinal() {
        List<Object> matchedFeatures = new ArrayList<Object>();
        matchedFeatures.add(subLine2);
        return matchedFeatures;
    }

    @Override
    public CorrespondanceType getType() {
        return CorrespondanceType.C2;
    }

    @Override
    public IDirectPositionList morphCorrespondance(double t) {
        Map<IDirectPosition, IDirectPosition> mapping = new HashMap<>();
        double dist = 0.0;
        double total = subLine1.length();
        double totalFinal = subLine2.length();
        IDirectPosition prevPt = null;
        for (IDirectPosition pt : subLine1.coord()) {
            if (prevPt == null) {
                prevPt = pt;
                mapping.put(pt, subLine2.startPoint());
                continue;
            }
            dist += pt.distance2D(prevPt);
            double ratio = dist / total;

            // get the point at the curvilinear coordinate corresponding to
            // ratio
            double curvi = totalFinal * ratio;
            IDirectPosition finalPt = Operateurs.pointEnAbscisseCurviligne(
                    subLine2, curvi);
            mapping.put(pt, finalPt);
            prevPt = pt;
        }

        // then, compute the intermediate position between each correspondant
        IDirectPositionList coord = new DirectPositionList();
        for (IDirectPosition pt1 : subLine1.coord()) {
            IDirectPosition pt2 = mapping.get(pt1);
            double newX = pt1.getX() + t * (pt2.getX() - pt1.getX());
            double newY = pt1.getY() + t * (pt2.getY() - pt1.getY());
            IDirectPosition newPt = new DirectPosition(newX, newY);
            coord.add(newPt);
        }

        return coord;
    }

}
