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
 *            Compute the area difference between two polygons
 * 
 * @author JFGirres
 * 
 */
public class AreaDifference extends PolygonMeasure {
    public AreaDifference(GM_Polygon pgRef, GM_Polygon pgComp) {
        super(pgRef, pgComp);
    }

    @Override
    public void compute() {
        this.setMeasure(this.getPgRef().area() - this.getPgComp().area());
    }
}
