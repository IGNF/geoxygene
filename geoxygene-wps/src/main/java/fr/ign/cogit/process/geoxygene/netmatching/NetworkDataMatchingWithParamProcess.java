package fr.ign.cogit.process.geoxygene.netmatching;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.process.Chargeur;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.process.NetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.data.EnsembleDeLiensData;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.util.conversion.GeOxygeneGeoToolsTypes;
import fr.ign.cogit.process.geoxygene.GeoxygeneProcess;
import fr.ign.parameters.Parameters;

/**
 * 
 * @author MDVan-Damme
 */
@DescribeProcess(title = "AppariementRéseaux", description = "Appariement")
public class NetworkDataMatchingWithParamProcess implements GeoxygeneProcess {

  /** LOGGER. */
  private final static Logger LOGGER = Logger.getLogger(NetworkDataMatchingWithParamProcess.class.getName());

  @SuppressWarnings("unchecked")
  @DescribeResult(name = "EnsembleDeLiensData", description = "EnsembleDeLiensData")
  public EnsembleDeLiensData execute(
      // @DescribeParameter(name = "param", description = "Paramètres")
      // Parameters param,
      @DescribeParameter(name = "reseau1", description = "Réseau 1 (moins détaillé)") SimpleFeatureCollection reseau1,
      @DescribeParameter(name = "reseau2", description = "Réseau 2 (plus détaillé)") SimpleFeatureCollection reseau2) {

    // LOGGER.debug("====================================================================");
    // LOGGER.debug("NetworkDataMatchingWithParamProcess");
    // LOGGER.debug(param.toString());
    // LOGGER.debug("====================================================================");

    // Set parameters
    LOGGER.debug("Start setting parameters");
    ParametresApp param = new ParametresApp();

    // Set datasets

    // Converting networks
    LOGGER.debug("Start converting reference network");
    DataSet datasetNetwork1 = new DataSet();
    IPopulation<IFeature> gReseau1 = (IPopulation<IFeature>) GeOxygeneGeoToolsTypes.convert2IFeatureCollection(reseau1);
    gReseau1.setNom("Edge");
    datasetNetwork1.addPopulation(gReseau1);
    IPopulation<IFeature> popNode1 = new Population<IFeature>("Node");
    datasetNetwork1.addPopulation(popNode1);
    List<IFeatureCollection<? extends IFeature>> list1 = new ArrayList<IFeatureCollection<? extends IFeature>>();
    list1.add((IPopulation<Arc>) datasetNetwork1.getPopulation("Edge"));
    param.populationsArcs1 = list1;

    LOGGER.debug("Start converting reference network");
    DataSet datasetNetwork2 = new DataSet();
    IPopulation<IFeature> gReseau2 = (IPopulation<IFeature>) GeOxygeneGeoToolsTypes.convert2IFeatureCollection(reseau2);
    gReseau2.setNom("Edge");
    datasetNetwork2.addPopulation(gReseau2);
    IPopulation<IFeature> popNode2 = new Population<IFeature>("Node");
    datasetNetwork2.addPopulation(popNode2);
    List<IFeatureCollection<? extends IFeature>> list2 = new ArrayList<IFeatureCollection<? extends IFeature>>();
    list2.add((IPopulation<Arc>)datasetNetwork2.getPopulation("Edge"));
    param.populationsArcs2 = list2;
    
    LOGGER.debug("End converting networks");

    
    ReseauApp carteTopo1 = Chargeur.importCarteTopo1(datasetNetwork1, param);
    ReseauApp carteTopo2 = Chargeur.importCarteTopo2(datasetNetwork2, param);

    NetworkDataMatching networkDataMatchingProcess = new NetworkDataMatching(param, carteTopo1, carteTopo2);
    
    EnsembleDeLiens liens = networkDataMatchingProcess.networkDataMatching();
    
    // Split les multi des liens
    Population<DefaultFeature> collection = new Population<DefaultFeature>();
    fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType newFeatureType = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType();
    newFeatureType.setTypeName("aaa");
    newFeatureType.setGeometryType(GM_Polygon.class);
    SchemaDefaultFeature schemaDefaultFeature = new SchemaDefaultFeature();
    schemaDefaultFeature.setNom("aaa");
    schemaDefaultFeature.setNomSchema("aaa");
    schemaDefaultFeature.setFeatureType(newFeatureType);
    collection.setFeatureType(newFeatureType);
    
    for (Lien lien : liens) {
      IGeometry geom = lien.getGeom();
      if (geom instanceof GM_Aggregate<?>) {
          GM_MultiCurve<GM_LineString> multiCurve = new GM_MultiCurve<GM_LineString>();
          for (IGeometry lineGeom : ((GM_Aggregate<?>) geom).getList()) {
              if (lineGeom instanceof GM_LineString) {
                  multiCurve.add((GM_LineString) lineGeom);
              } else if (lineGeom instanceof GM_MultiCurve<?>) {
                  multiCurve.addAll(((GM_MultiCurve<GM_LineString>) lineGeom).getList());
              } else if (lineGeom instanceof GM_Polygon) {
                  DefaultFeature defaultFeature = new DefaultFeature();

                  defaultFeature.setFeatureType(schemaDefaultFeature.getFeatureType());
                  defaultFeature.setSchema(schemaDefaultFeature);
                  defaultFeature.setGeom((GM_Polygon) lineGeom);
                  collection.add(defaultFeature);
              }
          }
          lien.setGeom(multiCurve);
      }
    }

    EnsembleDeLiensData liensData = new EnsembleDeLiensData();
    liensData.setLinks(liens);

    return liensData;
  }

}
