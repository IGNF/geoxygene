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

public class C3InvSubLineCorrespondance implements SubLineCorrespondance {

    private List<Object> initialFeatures;
    private ILineString subLine;
    private List<ILineString> initialLines;

    public C3InvSubLineCorrespondance(ILineString subLine,
            List<ILineString> initialLines) {
        super();
        this.subLine = subLine;
        this.initialLines = initialLines;
        initialFeatures = new ArrayList<>();
        initialFeatures.add(initialLines.get(0).startPoint());
        for (ILineString initialSubLine : initialLines) {
            initialFeatures.add(initialSubLine);
            initialFeatures.add(initialSubLine.endPoint());
        }
    }

    @Override
    public List<Object> getMatchedFeaturesInitial() {
        return initialFeatures;
    }

    @Override
    public List<Object> getMatchedFeaturesFinal() {
        List<Object> matchedFeatures = new ArrayList<Object>();
        matchedFeatures.add(subLine);
        return matchedFeatures;
    }

    @Override
    public CorrespondanceType getType() {
        return CorrespondanceType.C3_;
    }

    @Override
    public IDirectPositionList morphCorrespondance(double t) {
        ILineString merged = Operateurs.compileArcs(initialLines);

        Map<IDirectPosition, IDirectPosition> mapping = new HashMap<>();
        double dist = 0.0;
        double total = merged.length();
        double totalFinal = subLine.length();
        IDirectPosition prevPt = null;
        for (IDirectPosition pt : merged.coord()) {
            if (prevPt == null) {
                prevPt = pt;
                mapping.put(pt, subLine.startPoint());
                continue;
            }
            dist += pt.distance2D(prevPt);
            double ratio = dist / total;

            // get the point at the curvilinear coordinate corresponding to
            // ratio
            double curvi = totalFinal * ratio;
            IDirectPosition finalPt = Operateurs.pointEnAbscisseCurviligne(
                    subLine, curvi);
            mapping.put(pt, finalPt);
            prevPt = pt;
        }

        // then, compute the intermediate position between each correspondant
        IDirectPositionList coord = new DirectPositionList();
        for (IDirectPosition pt1 : merged.coord()) {
            IDirectPosition pt2 = mapping.get(pt1);
            double newX = pt1.getX() + t * (pt2.getX() - pt1.getX());
            double newY = pt1.getY() + t * (pt2.getY() - pt1.getY());
            IDirectPosition newPt = new DirectPosition(newX, newY);
            coord.add(newPt);
        }

        return coord;
    }

}
