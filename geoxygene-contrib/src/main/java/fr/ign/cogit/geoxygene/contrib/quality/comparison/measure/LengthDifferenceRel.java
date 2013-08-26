package fr.ign.cogit.geoxygene.contrib.quality.comparison.measure;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

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
 *            Compute the relative length difference between two linestrings
 * 
 * @author JFGirres
 */
public class LengthDifferenceRel extends LineStringMeasure {
    public LengthDifferenceRel(GM_LineString lsRef, GM_LineString lsComp) {
        super(lsRef, lsComp);
    }

    @Override
    public void compute() {
        this.setMeasure((this.getLsRef().length() - this.getLsComp().length()) / this.getLsComp().length());
    }
}
