package fr.ign.cogit.geoxygene.contrib.quality.comparison.cutting;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.quality.comparison.processing.LineStringExtremityCutting;


/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 *            Cut the extremities of homologous linestrings to perform
 *            comparisons
 * 
 * @author JFGirres
 * 
 */
public class ExtremityCutting extends Cutting<ILineString> {

    @Override
    public List<List<ILineString>> cut(ILineString lsRef, ILineString lsComp) {
        // Calcul les points projettés d'une extrémité de polyligne sur la
        // polyligne
        // homologue
        IDirectPosition dpRefStart = lsRef.startPoint();
        IDirectPosition dpRefEnd = lsRef.endPoint();
        IDirectPosition dpCompStart = lsComp.startPoint();
        IDirectPosition dpCompEnd = lsComp.endPoint();
        IDirectPosition dpNewRefStart = Operateurs.projection(dpCompStart, lsRef);
        IDirectPosition dpNewRefEnd = Operateurs.projection(dpCompEnd, lsRef);
        IDirectPosition dpNewCompStart = Operateurs.projection(dpRefStart, lsComp);
        IDirectPosition dpNewCompEnd = Operateurs.projection(dpRefEnd, lsComp);
        double positionNewCompStart = LineStringExtremityCutting.computeProjectedPointPosition(lsComp, dpNewCompStart);
        double positionNewCompEnd = LineStringExtremityCutting.computeProjectedPointPosition(lsComp, dpNewCompEnd);
        double positionNewRefStart = LineStringExtremityCutting.computeProjectedPointPosition(lsRef, dpNewRefStart);
        double positionNewRefEnd = LineStringExtremityCutting.computeProjectedPointPosition(lsRef, dpNewRefEnd);

        // Création des polylignes coupées aux extremités
        ILineString lsRefCut = LineStringExtremityCutting.createCuttedLineString(dpNewRefStart, positionNewRefStart,
                dpNewRefEnd, positionNewRefEnd, lsRef);
        ILineString lsCompCut = LineStringExtremityCutting.createCuttedLineString(dpNewCompStart, positionNewCompStart,
                dpNewCompEnd, positionNewCompEnd, lsComp);

        List<List<ILineString>> result = new ArrayList<List<ILineString>>();

        if (lsCompCut.getControlPoint().size() == 1) {
        } else {
            if (lsRefCut.getControlPoint().size() == 1) {
            } else {
                List<ILineString> pair = new ArrayList<ILineString>();
                pair.add(lsRefCut);
                pair.add(lsCompCut);
                result.add(pair);
            }
        }
        return result;
    }
}
