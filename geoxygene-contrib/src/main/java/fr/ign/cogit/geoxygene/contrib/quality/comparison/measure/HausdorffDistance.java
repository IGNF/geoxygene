package fr.ign.cogit.geoxygene.contrib.quality.comparison.measure;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;

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
 * 
 *            Compute the Hausdorff distance between two linestrings
 * 
 * @author JFGirres
 */
public class HausdorffDistance extends LineStringMeasure {
    public HausdorffDistance(ILineString lsRef, ILineString lsComp) {
        super(lsRef, lsComp);
    }

    @Override
    public void compute() {
        // Surechantillone les deux linestring
        ILineString lsCompSurech = Operateurs.echantillone(this.getLsComp(), 2);
        ILineString lsRefSurech = Operateurs.echantillone(this.getLsRef(), 2);
        this.setMeasure(Distances.hausdorff(lsRefSurech, lsCompSurech));
    }
}
