package fr.ign.cogit.geoxygene.contrib.quality.estim.projection;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;

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
public class PolygonProjectionImpact extends AbstractProjectionImpact {

    static Logger logger = Logger.getLogger(LineStringProjectionImpact.class.getName());

    private double correctedArea;

    public void setCorrectedArea(double correctedArea) {
        this.correctedArea = correctedArea;
    }

    public double getCorrectedArea() {
        return correctedArea;
    }

    /**
     * Execute projection error estimation for polygons
     */
    public void execute() {

        IFeatureCollection<IFeature> jddIn = this.getJddAEvaluer();

        double initialAreaTotale = 0;
        double correctedAreaTotale = 0;

        for (IFeature feature : jddIn) {

            if (feature.getGeom().isPolygon()) {
                IPolygon polygon = (IPolygon) feature.getGeom();
                correctedAreaTotale = correctedAreaTotale + this.computeCorrectedArea(polygon);
                initialAreaTotale = initialAreaTotale + feature.getGeom().area();
            }

            if (feature.getGeom().isMultiSurface()) {
                IMultiSurface<IPolygon> multiPoly = (GM_MultiSurface<IPolygon>) feature.getGeom();
                for (IPolygon polygon : multiPoly.getList()) {
                    correctedAreaTotale = correctedAreaTotale + this.computeCorrectedArea(polygon);
                    initialAreaTotale = initialAreaTotale + feature.getGeom().area();
                }
            }
        }

        setCorrectedArea(correctedAreaTotale);

    }

}
