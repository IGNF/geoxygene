package fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

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
 * @author JFGirres
 */
public class AbstractGrid {

    private int tailleCellule;

    public int getTailleCellule() {
        return tailleCellule;
    }

    public void setTailleCellule(int tailleCellule) {
        this.tailleCellule = tailleCellule;
    }

    private int rayon;

    public void setRayon(int rayon) {
        this.rayon = rayon;
    }

    public int getRayon() {
        return rayon;
    }

    private double minX;

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMinX() {
        return minX;
    }

    private double minY;

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMinY() {
        return minY;
    }

    private double maxX;

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMaxX() {
        return maxX;
    }

    private double maxY;

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getMaxY() {
        return maxY;
    }

    public AbstractGrid() {
    }

    public void setEnvelope(IFeatureCollection<?> jddIn) {
        minX = jddIn.envelope().minX();
        minY = jddIn.envelope().minY();
        maxX = jddIn.envelope().maxX();
        maxY = jddIn.envelope().maxY();
    }

}
