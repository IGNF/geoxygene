package fr.ign.cogit.geoxygene.matching.hmmm;

import java.util.Arrays;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.matching.hmmm.HMMMapMatcher.Node;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
// import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

/**
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 * 
 * 
 * Minimalistic implementation of "Hidden Markov Map Matching Through Noise and
 * Sparseness", Paul Newson and John Krumm, 17th ACM SIGSPATIAL International
 * Conference on Advances in Geographic Information Systems (ACM SIGSPATIAL GIS
 * 2009).
 * @see {@link http
 *      ://research.microsoft.com/en-us/um/people/jckrumm/MapMatchingData
 *      /data.htm}
 *      <p>
 *      In this implementation, we only use the proposed emission and transition
 *      probabilities but most of the breaks were ignored.
 */
public class HMMMapMatcherTest {
  
  private static final String DATA_DIR = "./data/Seattle\\";
  /** Static logger. */
  private static final Logger LOGGER = Logger.getLogger(HMMMapMatcherTest.class.getName());


  @Test
  public void testTest()  {
    
System.out.println("----------------------------------------------------------------");
    
    // On charge les données GPS
    IPopulation<IFeature> gpsPop = ShapefileReader.read(DATA_DIR + "gps_data_extrait.shp", "traces", null, true);
    Collections.reverse(gpsPop.getElements());

    // On charge la couche route
    IPopulation<IFeature> networkPop = ShapefileReader.read(DATA_DIR + "road_network_extrait.shp", "routes", null, true);
    
    SeattleMapMatcher mapMatcher = new SeattleMapMatcher(
        (FT_FeatureCollection<? extends IFeature>) gpsPop, networkPop, 10.0,
        50.0, 6.0, 2000.0);

    mapMatcher.preprocessPoints();
    LOGGER.info("Map Matching start with " + gpsPop.size());
    Node result = mapMatcher.computeTransitions();
    LOGGER.info("Map Matching finished");
    
    // Création du type géométrique
    FeatureType ftLines = new FeatureType();
    ftLines.setTypeName("Traces");
    ftLines.setNomClasse("DefaultFeature");
    ftLines.setGeometryType(GM_LineString.class);
    Population<DefaultFeature> popTraces = new Population<DefaultFeature>(
        ftLines, false); //$NON-NLS-1$
    popTraces.setClasse(DefaultFeature.class);
    for (int i = 0; i < gpsPop.size() - 1; i++) {
      IFeature p1 = gpsPop.get(i);
      IFeature p2 = gpsPop.get(i + 1);
      popTraces.nouvelElement(new GM_LineString(Arrays.asList(p1.getGeom().centroid(), p2
          .getGeom().centroid())));
    }
    
    LOGGER.info("Traces wrote");
    
    FeatureType ftPoints = new FeatureType();
    ftPoints.setGeometryType(GM_Point.class);
    ftPoints.addFeatureAttribute(new AttributeType("id", "int"));
    Population<DefaultFeature> popMatchedPoints = new Population<DefaultFeature>("Points Recales"); //$NON-NLS-1$
    popMatchedPoints.setFeatureType(ftPoints);
    popMatchedPoints.setClasse(DefaultFeature.class);
    for (int i = 0; i < gpsPop.size(); i++) {
      GM_Point p = (GM_Point) gpsPop.get(i).getGeom();
      ILineString l = result.getStates().get(i).getGeometrie();
      DefaultFeature projectedPoint = popMatchedPoints.nouvelElement();
      projectedPoint.setGeom(JtsAlgorithms.getClosestPoint(p.getPosition(), l).toGM_Point());
      projectedPoint.setId(i);
      gpsPop.get(i).setId(i);
    }
    // popTousPointsRecales.addCollection(carteRecale.getPopNoeuds());
    gpsPop.setFeatureType(ftPoints);
    
    // ShapefileWriter.write(popMatchedPoints, DATA_DIR + "resultat\\points.shp");
    // LOGGER.info("Points wrote");
    
    // ShapefileWriter.write(gpsPop, DATA_DIR + "resultat\\gpsPoints.shp");
    LOGGER.info("Points wrote");
    
    FeatureType ftLignesRecales = new FeatureType();
    ftLignesRecales.setGeometryType(GM_LineString.class);
    ftLignesRecales.addFeatureAttribute(new AttributeType("Poids", "double"));
    Population<Arc> popLignesRecales = new Population<Arc>("Lignes Recales"); //$NON-NLS-1$
    popLignesRecales.setFeatureType(ftLignesRecales);
    double i = 0;
    for (Arc a : result.getPath()) {
      a.setPoids(i++);
    }
    popLignesRecales.addCollection(result.getPath());
    
    // ShapefileWriter.write(popLignesRecales, DATA_DIR + "resultat\\trace2.shp");
    LOGGER.info("Traces wrote");

    // ShapefileWriter.write(mapMatcher.getNetworkMap().getPopNoeuds(), DATA_DIR + "resultat\\noeuds.shp");
    
    FeatureType ftPathSegments = new FeatureType();
    ftPathSegments.setGeometryType(GM_LineString.class);
    ftPathSegments.addFeatureAttribute(new AttributeType("Poids", "double"));
    Population<Arc> popPathSegments = new Population<Arc>("PathSegments"); //$NON-NLS-1$
    popPathSegments.setClasse(Arc.class);
    popPathSegments.setFeatureType(ftPathSegments);
    for (int index = 0; index < result.getGeometry().size() - 1; index++) {
      Arc a = popPathSegments.nouvelElement(result.getGeometry().get(index));
      a.setPoids(index);
    }
    // ShapefileWriter.write(popPathSegments, DATA_DIR + "resultat\\pathSegments.shp");
    
    System.out.println("----------------------------------------------------------------");
    
    // Assert.assertTrue(true);
  }

}
