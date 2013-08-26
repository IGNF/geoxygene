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
 */
public class AreaDifferenceTest extends PolygonMeasure {
    public AreaDifferenceTest(GM_Polygon pgRef, GM_Polygon pgComp) {
        super(pgRef, pgComp);
    }

    @Override
    public void compute() {
        this.setMeasure(areaTest(this.getPgRef()) - areaTest(this.getPgComp()));
    }

    public double areaTest(GM_Polygon pgTest) {
        double area = 0;
        int nbPts = pgTest.coord().size();
        double sommeProduit = 0;

        for (int i = 0; i < nbPts - 1; i++) {
            double Xi = 0;
            double Yi0 = 0;
            double Yi1 = 0;

            if (i == 0) {
                Xi = pgTest.coord().get(i).getX();
                Yi0 = pgTest.coord().get(nbPts - 2).getY();
                Yi1 = pgTest.coord().get(i + 1).getY();
            } else {
                Xi = pgTest.coord().get(i).getX();
                Yi0 = pgTest.coord().get(i - 1).getY();
                Yi1 = pgTest.coord().get(i + 1).getY();
            }
            double produitCoord = Xi * (Yi0 - Yi1);
            sommeProduit = sommeProduit + produitCoord;
        }

        area = 0.5 * sommeProduit;
        return area;
    }
}
