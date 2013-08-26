package fr.ign.cogit.geoxygene.contrib.quality.comparison;

import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.quality.comparison.measure.Measure;

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
 *            An interface to compute comparisons between homologous objects
 * 
 * @author JFGirres
 * 
 */
public interface Comparison<Geom extends IGeometry> {
    /**
     * Execute the comparison between two datasets
     */
    void executeComparison();

    /**
     * Compute comparison indicators
     * @param geomRef
     * @param geomComp
     */
    void computeIndicators(Geom geomRef, Geom geomComp);

    IFeatureCollection<IFeature> getJddRef();

    IFeatureCollection<IFeature> getJddComp();

    List<Double> getMeasurements(Class<? extends Measure> measureClass);

    void fillDatasetsOutputs(Geom geomRef, Geom geomComp);
}
