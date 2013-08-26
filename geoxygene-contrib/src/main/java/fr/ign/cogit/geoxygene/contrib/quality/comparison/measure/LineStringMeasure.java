package fr.ign.cogit.geoxygene.contrib.quality.comparison.measure;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

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
 *            An abstract class for the comparison of two linestrings
 * 
 * @author JFGirres
 */
public abstract class LineStringMeasure implements Measure {
    private ILineString lsRef;
    private ILineString lsComp;

    public ILineString getLsRef() {
        return lsRef;
    }

    public ILineString getLsComp() {
        return lsComp;
    }

    private double measure;

    public void setMeasure(double measure) {
        this.measure = measure;
    }

    @Override
    public double getMeasure() {
        return this.measure;
    }

    public LineStringMeasure(ILineString lsRef, ILineString lsComp) {
        this.lsRef = lsRef;
        this.lsComp = lsComp;
        this.compute();
    }

    abstract void compute();
}
