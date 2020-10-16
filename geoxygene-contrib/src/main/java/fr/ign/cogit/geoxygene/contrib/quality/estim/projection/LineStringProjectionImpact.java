package fr.ign.cogit.geoxygene.contrib.quality.estim.projection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;

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
public class LineStringProjectionImpact extends AbstractProjectionImpact {

    static Logger logger = LogManager.getLogger(LineStringProjectionImpact.class.getName());

    private double correctedLength;

    /**
     * Execute projection error estimation for linestrings
     */
    @SuppressWarnings("unchecked")
    public void execute() {

        IFeatureCollection<IFeature> jddIn = this.getJddAEvaluer();
        double initialLengthTotale = 0;
        double correctedLengthTotale = 0;
        for (IFeature feature : jddIn) {
            if (feature.getGeom().isLineString()) {
                ILineString lineString = (ILineString) feature.getGeom();
                correctedLengthTotale = correctedLengthTotale + this.computeCorrectedLength(lineString);
                initialLengthTotale = initialLengthTotale + feature.getGeom().length();
            }
            if (feature.getGeom().isMultiCurve()) {
                IMultiCurve<ILineString> multiLs = (GM_MultiCurve<ILineString>) feature.getGeom();
                for (ILineString lineString : multiLs.getList()) {
                    correctedLengthTotale = correctedLengthTotale + this.computeCorrectedLength(lineString);
                    initialLengthTotale = initialLengthTotale + feature.getGeom().length();
                }
            }
        }
        setCorrectedLength(correctedLengthTotale);
    }

    public double getCorrectedLength() {
        return correctedLength;
    }

    public void setCorrectedLength(double correctedLength) {
        this.correctedLength = correctedLength;
    }
}
