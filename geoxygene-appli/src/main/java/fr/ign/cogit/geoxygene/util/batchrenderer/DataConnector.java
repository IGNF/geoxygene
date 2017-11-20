package fr.ign.cogit.geoxygene.util.batchrenderer;

import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;

public interface DataConnector {
    /**
     * get a list of all tables/layers in the data source
     * @return
     */
    List<String> getCouches();
    
    /**
     * get a collection of features from table intersecting extent
     * @param table
     * @param extent
     * @return
     */
    IPopulation<IFeature> getPopulation(String table, IEnvelope extent);
}
