package fr.ign.cogit.geoxygene.contrib.quality.comparison.cutting;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.quality.comparison.processing.LineStringExtremityCutting;
import fr.ign.cogit.geoxygene.contrib.quality.comparison.processing.LineStringSectionCutting;

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
 *            Cut the reference linestring in sections of equal length, and
 *            project extremities of each section on the compared linestring to
 *            perform normalized comparisons
 * 
 * @author JFGirres
 * 
 */
public class SectionsCutting extends Cutting<ILineString> {

    private double step = 1000.0;

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public SectionsCutting(double step) {
        super();
        this.setStep(step);
    }

    @Override
    public List<List<ILineString>> cut(ILineString lsRef, ILineString lsComp) {
        List<List<ILineString>> result = new ArrayList<List<ILineString>>();
        if (step > lsRef.length()) {
            return result;
        } else {
            List<ILineString> listLsCompTroncons = new ArrayList<ILineString>();
            List<ILineString> listLsRefTroncons = LineStringSectionCutting.Tronconnage(lsRef, step);

            for (ILineString lsRefTroncon : listLsRefTroncons) {

                // Point de départ et d'arrivée du tronçon de la polyligne à
                // comparer
                IDirectPosition dpNewCompTronconStart = Operateurs.projection(lsRefTroncon.startPoint(), lsComp); // modif
                IDirectPosition dpNewCompTronconEnd = Operateurs.projection(lsRefTroncon.endPoint(), lsComp);// modif
                double positionNewCompTronconStart = LineStringExtremityCutting.computeProjectedPointPosition(lsComp,
                        dpNewCompTronconStart);// modif
                double positionNewCompTronconEnd = LineStringExtremityCutting.computeProjectedPointPosition(lsComp,
                        dpNewCompTronconEnd);// modif

                ILineString lsCompTroncon = LineStringExtremityCutting.createCuttedLineString(dpNewCompTronconStart,
                        positionNewCompTronconStart, dpNewCompTronconEnd, positionNewCompTronconEnd, lsComp);// modif

                // if (lsCompTroncon.getControlPoint().size() == 1) {
                // lsCompTroncon.addControlPoint(lsCompTroncon.getControlPoint(0));
                // }

                if (lsCompTroncon.getControlPoint().size() == 1) {
                } else {
                    if (lsRefTroncon.getControlPoint().size() == 1) {
                    } else {
                        listLsCompTroncons.add(lsCompTroncon);
                        List<ILineString> pair = new ArrayList<ILineString>();
                        pair.add(lsRefTroncon);
                        pair.add(lsCompTroncon);
                        result.add(pair);
                    }
                }
            }
        }
        return result;
    }
}
