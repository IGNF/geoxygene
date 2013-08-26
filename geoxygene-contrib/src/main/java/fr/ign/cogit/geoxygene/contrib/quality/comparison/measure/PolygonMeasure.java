package fr.ign.cogit.geoxygene.contrib.quality.comparison.measure;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

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
 *            An abstract class for the comparison of two polygons
 * 
 * @author JFGirres
 */
public abstract class PolygonMeasure implements Measure {
    private GM_Polygon pgRef;
    private GM_Polygon pgComp;

    public GM_Polygon getPgRef() {
        return pgRef;
    }

    public GM_Polygon getPgComp() {
        return pgComp;
    }

    private double measure;

    public void setMeasure(double measure) {
        this.measure = measure;
    }

    @Override
    public double getMeasure() {
        return this.measure;
    }

    public PolygonMeasure(GM_Polygon pgRef, GM_Polygon pgComp) {
        this.pgRef = pgRef;
        this.pgComp = pgComp;
        this.compute();
    }

    abstract void compute();
}
