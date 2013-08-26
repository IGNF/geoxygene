package fr.ign.cogit.geoxygene.contrib.quality.comparison.measure;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
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
 *            Compute the mean distance between two linestrings
 * 
 * @author JFGirres
 */
public class MeanDistance extends LineStringMeasure {
    public MeanDistance(ILineString lsRef, ILineString lsComp) {
        super(lsRef, lsComp);
    }

    @Override
    public void compute() {
        // Creation du polygone d'écart entre les deux polylignes
        IPolygon polyDistMoy = Operateurs.surfaceFromLineStrings(this.getLsRef(), this.getLsComp());
        double meanLength = (this.getLsRef().length() + this.getLsComp().length()) / 2;
        this.setMeasure(polyDistMoy.area() / meanLength);
    }
}
