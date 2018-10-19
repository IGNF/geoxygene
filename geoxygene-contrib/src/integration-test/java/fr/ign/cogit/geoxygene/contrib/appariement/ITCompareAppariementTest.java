package fr.ign.cogit.geoxygene.contrib.appariement;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test de comparaison :
 * <ul>
 *    <li> Comparaison des structures des objets Parametres </li>
 *    <li> Comparaison du nombre de liens trouves </li>
 *    <li> Comparaison du nombre d'arcs et noeuds des cartes topo construites </li> 
 * </ul> 
 *
 */
public class ITCompareAppariementTest {
  
  /**
   * On compare avec le même jeu de données, l'ancien appariement et le nouveau.<br/>
   * - Les paramètres par défaut.<br/>
   * - Sans recalage <br/>
   * 
   */
  @Test
  public void testAppariementDefaut() {
    
    /*URL url = ITCompareAppariementTest.class.getResource("/data/");
    
    // Réseaux
    IPopulation<IFeature> reseau1 = ShapefileReader.read(url.getPath() + "reseau1.shp");
    IPopulation<IFeature> reseau2 = ShapefileReader.read(url.getPath() + "reseau2.shp");
    
    // Résultats de l'appariement avec l'ancienne structure des objets
    ParametresApp paramApp = new ParametresApp();
    paramApp.attributOrientation1 = null;
    paramApp.attributOrientation2 = null;
    paramApp.debugBilanSurObjetsGeo = false;
    paramApp.populationsArcs1.add(reseau1);
    paramApp.populationsArcs2.add(reseau2);
    List<ReseauApp> cartesTopo = new ArrayList<ReseauApp>();
    EnsembleDeLiens edl1 = AppariementIO.appariementDeJeuxGeo(paramApp, cartesTopo);
    
    // Résultats de l'appariement avec la nouvelle structure des objets
    ParamNetworkDataMatching param = new ParamNetworkDataMatching();
    DatasetNetworkDataMatching datasetNetwork1 = new DatasetNetworkDataMatching();
    datasetNetwork1.addPopulationsArcs(reseau1);
    DatasetNetworkDataMatching datasetNetwork2 = new DatasetNetworkDataMatching();
    datasetNetwork2.addPopulationsArcs(reseau2);
    NetworkDataMatching networkDataMatchingProcess = new NetworkDataMatching(param, datasetNetwork1, datasetNetwork2);
    networkDataMatchingProcess.setActions(false, false);
    ResultNetworkDataMatching resultatAppariement2 = networkDataMatchingProcess.networkDataMatching();
    EnsembleDeLiens edl2 = resultatAppariement2.getLiens();
    
    // -----------------------------------------------------------------------------------------------------------------------
    // On peut déjà comparer les paramètres
    compareParam(paramApp, param);
    
    // -----------------------------------------------------------------------------------------------------------------------
    // On compare : bigre comment faire !!!
    compareResultLien(edl1, edl2);
    compareCarteTopo(cartesTopo, resultatAppariement2); */ 
  
  }
  
  /**
   * On compare avec le même jeu de données, l'ancien appariement et le nouveau.<br/>
   * - Les paramètres par défaut.<br/>
   * - On tient compte dans le reseau 2 du sens de direction
   * - Sans recalage <br/>
   * 
   */
  @Test
  public void testDirectionReseau2() {
    
    /*URL url = ITCompareAppariementTest.class.getResource("/data/");
    
    // Réseaux
    IPopulation<IFeature> reseau1 = ShapefileReader.read(url.getPath() + "reseau1.shp");
    IPopulation<IFeature> reseau2 = ShapefileReader.read(url.getPath() + "reseau2.shp");
    
    // ------------------------------------------------------------------------------------------
    // Résultats de l'appariement avec l'ancienne structure des objets
    ParametresApp paramApp = new ParametresApp();
    paramApp.debugBilanSurObjetsGeo = false;
    paramApp.populationsArcs1.add(reseau1);
    paramApp.populationsArcs2.add(reseau2);
    paramApp.populationsArcsAvecOrientationDouble = false;
    paramApp.attributOrientation2 = "SENS";
    Map<Object, Integer> orientationMap2 = new HashMap<Object, Integer>();
    orientationMap2.put("Direct", OrientationInterface.SENS_DIRECT);
    orientationMap2.put("Inverse", OrientationInterface.SENS_INVERSE);
    orientationMap2.put("Double", OrientationInterface.DOUBLE_SENS);
    paramApp.orientationMap2 = orientationMap2;
    
    List<ReseauApp> cartesTopo = new ArrayList<ReseauApp>();
    EnsembleDeLiens edl1 = AppariementIO.appariementDeJeuxGeo(paramApp, cartesTopo);
    
    // ------------------------------------------------------------------------------------------
    // Résultats de l'appariement avec la nouvelle structure des objets
    ParamNetworkDataMatching param = new ParamNetworkDataMatching();
    ParamDirectionNetworkDataMatching paramDirection1 = new ParamDirectionNetworkDataMatching();
    paramDirection1.setAttributOrientation("orientation");
    param.setParamDirectionNetwork1(paramDirection1);
    ParamDirectionNetworkDataMatching paramDirection2 = new ParamDirectionNetworkDataMatching();
    paramDirection2.setOrientationDouble(false);
    paramDirection2.setAttributOrientation("SENS");
    Map<Integer, String> orientationMap22 = new HashMap<Integer, String>();
    orientationMap22.put(OrientationInterface.SENS_DIRECT, "Direct");
    orientationMap22.put(OrientationInterface.SENS_INVERSE, "Inverse");
    orientationMap22.put(OrientationInterface.DOUBLE_SENS, "Double");
    paramDirection2.setOrientationMap(orientationMap22);
    param.setParamDirectionNetwork2(paramDirection2);
    
    DatasetNetworkDataMatching datasetNetwork1 = new DatasetNetworkDataMatching();
    datasetNetwork1.addPopulationsArcs(reseau1);
    DatasetNetworkDataMatching datasetNetwork2 = new DatasetNetworkDataMatching();
    datasetNetwork2.addPopulationsArcs(reseau2);
    NetworkDataMatching networkDataMatchingProcess = new NetworkDataMatching(param, datasetNetwork1, datasetNetwork2);
    networkDataMatchingProcess.setActions(false, false);
    ResultNetworkDataMatching resultatAppariement2 = networkDataMatchingProcess.networkDataMatching();
    EnsembleDeLiens edl2 = resultatAppariement2.getLiens();
    
    // -----------------------------------------------------------------------------------------------------------------------
    // On peut déjà comparer les paramètres
    compareParam(paramApp, param);
    
    // -----------------------------------------------------------------------------------------------------------------------
    // On compare : bigre comment faire !!!
    compareResultLien(edl1, edl2);
    compareCarteTopo(cartesTopo, resultatAppariement2);  
    */
  }
  
  /**
   * On compare avec le même jeu de données, l'ancien appariement et le nouveau.<br/>
   * - Les paramètres par défaut.<br/>
   * - On tient compte dans le reseau 1 du sens de direction
   * - Sans recalage <br/>
   * 
   */
  @Test
  public void testDirectionReseau1() {
    
    /*URL url = ITCompareAppariementTest.class.getResource("/data/");
    
    // Réseaux
    IPopulation<IFeature> reseau1 = ShapefileReader.read(url.getPath() + "reseau1.shp");
    IPopulation<IFeature> reseau2 = ShapefileReader.read(url.getPath() + "reseau2.shp");
    
    // ------------------------------------------------------------------------------------------
    // Résultats de l'appariement avec l'ancienne structure des objets
    ParametresApp paramApp = new ParametresApp();
    paramApp.debugBilanSurObjetsGeo = false;
    paramApp.populationsArcs1.add(reseau1);
    paramApp.populationsArcs2.add(reseau2);
    paramApp.populationsArcsAvecOrientationDouble = false;
    paramApp.attributOrientation1 = "SENS";
    Map<Object, Integer> orientationMap1 = new HashMap<Object, Integer>();
    orientationMap1.put("n'importe quoi", OrientationInterface.SENS_DIRECT);
    orientationMap1.put("et n'importe quoi", OrientationInterface.SENS_INVERSE);
    orientationMap1.put("Double sens", OrientationInterface.DOUBLE_SENS);
    paramApp.orientationMap1 = orientationMap1;
    
    List<ReseauApp> cartesTopo = new ArrayList<ReseauApp>();
    EnsembleDeLiens edl1 = AppariementIO.appariementDeJeuxGeo(paramApp, cartesTopo);
    
    // ------------------------------------------------------------------------------------------
    // Résultats de l'appariement avec la nouvelle structure des objets
    ParamNetworkDataMatching param = new ParamNetworkDataMatching();
    // Direction reseau 1
    ParamDirectionNetworkDataMatching paramDirection1 = new ParamDirectionNetworkDataMatching();
    paramDirection1.setAttributOrientation("SENS");
    Map<Integer, String> orientationMap21 = new HashMap<Integer, String>();
    orientationMap21.put(OrientationInterface.SENS_DIRECT, "n'importe quoi");
    orientationMap21.put(OrientationInterface.SENS_INVERSE, "et n'importe quoi");
    orientationMap21.put(OrientationInterface.DOUBLE_SENS, "Double sens");
    paramDirection1.setOrientationMap(orientationMap21);
    param.setParamDirectionNetwork1(paramDirection1);
    // Direction reseau 2
    ParamDirectionNetworkDataMatching paramDirection2 = new ParamDirectionNetworkDataMatching();
    paramDirection2.setOrientationDouble(true);
    paramDirection2.setAttributOrientation("orientation");
    param.setParamDirectionNetwork2(paramDirection2);
    
    DatasetNetworkDataMatching datasetNetwork1 = new DatasetNetworkDataMatching();
    datasetNetwork1.addPopulationsArcs(reseau1);
    DatasetNetworkDataMatching datasetNetwork2 = new DatasetNetworkDataMatching();
    datasetNetwork2.addPopulationsArcs(reseau2);
    NetworkDataMatching networkDataMatchingProcess = new NetworkDataMatching(param, datasetNetwork1, datasetNetwork2);
    networkDataMatchingProcess.setActions(false, false);
    ResultNetworkDataMatching resultatAppariement2 = networkDataMatchingProcess.networkDataMatching();
    EnsembleDeLiens edl2 = resultatAppariement2.getLiens();
    
    // -----------------------------------------------------------------------------------------------------------------------
    // On peut déjà comparer les paramètres
    compareParam(paramApp, param);
    
    // -----------------------------------------------------------------------------------------------------------------------
    // On compare : bigre comment faire !!!
    compareResultLien(edl1, edl2);
    compareCarteTopo(cartesTopo, resultatAppariement2);  
    */
  }
  
  /**
   * On compare avec le même jeu de données, l'ancien appariement et le nouveau.<br/>
   * - Les paramètres par défaut.<br/>
   * - On tient compte dans le reseau 1 et dans le reseau 2 du sens de direction
   * - Sans recalage <br/>
   * 
   */
  @Test
  public void testDirectionReseau1Reseau2() {
    /*
    URL url = ITCompareAppariementTest.class.getResource("/data/");
    
    // Réseaux
    IPopulation<IFeature> reseau1 = ShapefileReader.read(url.getPath() + "reseau1.shp");
    IPopulation<IFeature> reseau2 = ShapefileReader.read(url.getPath() + "reseau2.shp");
    
    // ------------------------------------------------------------------------------------------
    // Résultats de l'appariement avec l'ancienne structure des objets
    ParametresApp paramApp = new ParametresApp();
    paramApp.debugBilanSurObjetsGeo = false;
    paramApp.populationsArcs1.add(reseau1);
    paramApp.populationsArcs2.add(reseau2);
    paramApp.populationsArcsAvecOrientationDouble = false;
    paramApp.attributOrientation1 = "SENS";
    paramApp.attributOrientation2 = "SENS";
    Map<Object, Integer> orientationMap1 = new HashMap<Object, Integer>();
    orientationMap1.put("n'importe quoi", OrientationInterface.SENS_DIRECT);
    orientationMap1.put("et n'importe quoi", OrientationInterface.SENS_INVERSE);
    orientationMap1.put("Double sens", OrientationInterface.DOUBLE_SENS);
    paramApp.orientationMap1 = orientationMap1;
    Map<Object, Integer> orientationMap2 = new HashMap<Object, Integer>();
    orientationMap2.put("Direct", OrientationInterface.SENS_DIRECT);
    orientationMap2.put("Inverse", OrientationInterface.SENS_INVERSE);
    orientationMap2.put("Double", OrientationInterface.DOUBLE_SENS);
    paramApp.orientationMap2 = orientationMap2;
    
    List<ReseauApp> cartesTopo = new ArrayList<ReseauApp>();
    EnsembleDeLiens edl1 = AppariementIO.appariementDeJeuxGeo(paramApp, cartesTopo);
    
    // ------------------------------------------------------------------------------------------
    // Résultats de l'appariement avec la nouvelle structure des objets
    ParamNetworkDataMatching param = new ParamNetworkDataMatching();
    // Direction reseau 1
    ParamDirectionNetworkDataMatching paramDirection1 = new ParamDirectionNetworkDataMatching();
    paramDirection1.setAttributOrientation("SENS");
    Map<Integer, String> orientationMap21 = new HashMap<Integer, String>();
    orientationMap21.put(OrientationInterface.SENS_DIRECT, "n'importe quoi");
    orientationMap21.put(OrientationInterface.SENS_INVERSE, "et n'importe quoi");
    orientationMap21.put(OrientationInterface.DOUBLE_SENS, "Double sens");
    paramDirection1.setOrientationMap(orientationMap21);
    
    param.setParamDirectionNetwork1(paramDirection1);
    // Direction reseau 2
    ParamDirectionNetworkDataMatching paramDirection2 = new ParamDirectionNetworkDataMatching();
    paramDirection2.setOrientationDouble(false);
    paramDirection2.setAttributOrientation("SENS");
    Map<Integer, String> orientationMap22 = new HashMap<Integer, String>();
    orientationMap22.put(OrientationInterface.SENS_DIRECT, "Direct");
    orientationMap22.put(OrientationInterface.SENS_INVERSE, "Inverse");
    orientationMap22.put(OrientationInterface.DOUBLE_SENS, "Double");
    paramDirection2.setOrientationMap(orientationMap22);
    param.setParamDirectionNetwork2(paramDirection2);
    
    DatasetNetworkDataMatching datasetNetwork1 = new DatasetNetworkDataMatching();
    datasetNetwork1.addPopulationsArcs(reseau1);
    DatasetNetworkDataMatching datasetNetwork2 = new DatasetNetworkDataMatching();
    datasetNetwork2.addPopulationsArcs(reseau2);
    NetworkDataMatching networkDataMatchingProcess = new NetworkDataMatching(param, datasetNetwork1, datasetNetwork2);
    networkDataMatchingProcess.setActions(false, false);
    ResultNetworkDataMatching resultatAppariement2 = networkDataMatchingProcess.networkDataMatching();
    EnsembleDeLiens edl2 = resultatAppariement2.getLiens();
    
    // -----------------------------------------------------------------------------------------------------------------------
    // On peut déjà comparer les paramètres
    compareParam(paramApp, param);
    
    // -----------------------------------------------------------------------------------------------------------------------
    // On compare : bigre comment faire !!!
    compareResultLien(edl1, edl2);
    compareCarteTopo(cartesTopo, resultatAppariement2);  
    */
  }
  
  /**
   * On compare avec le même jeu de données, l'ancien appariement et le nouveau.<br/>
   * - Les paramètres par défaut.<br/>
   * - les 2 reseaux ont une topologie planaire 
   * - Sans recalage <br/>
   * 
   */
  @Test
  public void testGraphePlanaire() {
    /*
    URL url = ITCompareAppariementTest.class.getResource("/data/");
    
    // Réseaux
    IPopulation<IFeature> reseau1 = ShapefileReader.read(url.getPath() + "reseau1.shp");
    IPopulation<IFeature> reseau2 = ShapefileReader.read(url.getPath() + "reseau2.shp");
    
    // ------------------------------------------------------------------------------------------
    // Résultats de l'appariement avec l'ancienne structure des objets
    ParametresApp paramApp = new ParametresApp();
    paramApp.attributOrientation1 = null;
    paramApp.attributOrientation2 = null;
    paramApp.debugBilanSurObjetsGeo = false;
    paramApp.populationsArcs1.add(reseau1);
    paramApp.populationsArcs2.add(reseau2);
    paramApp.topologieGraphePlanaire1 = true;
    paramApp.topologieGraphePlanaire2 = true;
    
    List<ReseauApp> cartesTopo = new ArrayList<ReseauApp>();
    EnsembleDeLiens edl1 = AppariementIO.appariementDeJeuxGeo(paramApp, cartesTopo);
    
    // ------------------------------------------------------------------------------------------
    // Résultats de l'appariement avec la nouvelle structure des objets
    ParamNetworkDataMatching param = new ParamNetworkDataMatching();
    
    DatasetNetworkDataMatching datasetNetwork1 = new DatasetNetworkDataMatching();
    datasetNetwork1.addPopulationsArcs(reseau1);
    DatasetNetworkDataMatching datasetNetwork2 = new DatasetNetworkDataMatching();
    datasetNetwork2.addPopulationsArcs(reseau2);
    
    ParamTopologyTreatmentNetwork paramTopo1 = new ParamTopologyTreatmentNetwork();
    paramTopo1.setGraphePlanaire(true);
    param.setParamTopoNetwork1(paramTopo1);
    ParamTopologyTreatmentNetwork paramTopo2 = new ParamTopologyTreatmentNetwork();
    paramTopo2.setGraphePlanaire(true);
    param.setParamTopoNetwork2(paramTopo2);
    
    NetworkDataMatching networkDataMatchingProcess = new NetworkDataMatching(param, datasetNetwork1, datasetNetwork2);
    networkDataMatchingProcess.setActions(false, false);
    ResultNetworkDataMatching resultatAppariement2 = networkDataMatchingProcess.networkDataMatching();
    EnsembleDeLiens edl2 = resultatAppariement2.getLiens();
    
    // -----------------------------------------------------------------------------------------------------------------------
    // On peut déjà comparer les paramètres
    compareParam(paramApp, param);
    
    // -----------------------------------------------------------------------------------------------------------------------
    // On compare : bigre comment faire !!!
    compareResultLien(edl1, edl2);
    compareCarteTopo(cartesTopo, resultatAppariement2);  
    */
  }
  
  
  private void compareResultLien(EnsembleDeLiens e1, EnsembleDeLiens e2) {
    
    // Nombre de liens
    Assert.assertTrue("\n" + "Nb de liens dans l'ancien algo = " + e1.size() + ",\n" 
        + "Nb de liens dans le nouvel algo = " + e2.size(), e1.size() == e2.size());
    
  }
  
  // private void compareCarteTopo(List<ReseauApp> listCarteTopo, ResultNetworkDataMatching resultatAppariement) {
    
    // Nombre d'arcs du reseau 1
    /*Assert.assertTrue("Nb arcs carte topo reseau 1 = " + listCarteTopo.get(0).getListeArcs().size() + " ? " + resultatAppariement.getReseauStat1().getReseauApp().getListeArcs().size(), 
        listCarteTopo.get(0).getListeArcs().size() == resultatAppariement.getReseauStat1().getReseauApp().getListeArcs().size());
    
    // Nombre de noeuds du reseau 1
    Assert.assertTrue("Nb noeuds carte topo reseau 1 = " + listCarteTopo.get(0).getListeNoeuds().size() + " ? " + resultatAppariement.getReseauStat1().getReseauApp().getListeNoeuds().size(), 
        listCarteTopo.get(0).getListeNoeuds().size() == resultatAppariement.getReseauStat1().getReseauApp().getListeNoeuds().size());
    
    // Nombre d'arcs du reseau 2
    Assert.assertTrue("Nb arcs carte topo reseau 2 = " + listCarteTopo.get(1).getListeArcs().size() + " ? " + resultatAppariement.getReseauStat2().getReseauApp().getListeArcs().size(), 
        listCarteTopo.get(1).getListeArcs().size() == resultatAppariement.getReseauStat2().getReseauApp().getListeArcs().size());
    
    // Nombre de noeuds du reseau 2
    Assert.assertTrue("Nb noeuds carte topo reseau 2 = " + listCarteTopo.get(1).getListeNoeuds().size() + " ? " + resultatAppariement.getReseauStat2().getReseauApp().getListeNoeuds().size(), 
        listCarteTopo.get(1).getListeNoeuds().size() == resultatAppariement.getReseauStat2().getReseauApp().getListeNoeuds().size());
    */
  // }
  
  
  /**
   * Comparaison des paramètres.
   * 
   * @param paramApp
   * @param param
   */
  /*private void compareParam(ParametresApp oldParam, ParamNetworkDataMatching newParam) {
    
    // Distance
    Assert.assertTrue("Comparaison de distanceArcsMax : " + oldParam.distanceArcsMax + " ? "
        + newParam.getParamDistance().getDistanceArcsMax(), oldParam.distanceArcsMax == newParam.getParamDistance().getDistanceArcsMax());
    Assert.assertTrue("Comparaison de distanceArcsMin : " + oldParam.distanceArcsMin + " ? "
        + newParam.getParamDistance().getDistanceArcsMin(), oldParam.distanceArcsMin == newParam.getParamDistance().getDistanceArcsMin());
    Assert.assertTrue("Comparaison de distanceNoeudsMax : " + oldParam.distanceNoeudsMax + " ? "
        + newParam.getParamDistance().getDistanceNoeudsMax(), oldParam.distanceNoeudsMax == newParam.getParamDistance().getDistanceNoeudsMax());
    Assert.assertTrue("Comparaison de distanceNoeudsImpassesMax : " + oldParam.distanceNoeudsImpassesMax + " ? "
        + newParam.getParamDistance().getDistanceNoeudsImpassesMax(), oldParam.distanceNoeudsImpassesMax == newParam.getParamDistance().getDistanceNoeudsImpassesMax());
    
    // Topologie du réseau 1
    Assert.assertTrue("Comparaison de topologieGraphePlanaire1 : " + oldParam.topologieGraphePlanaire1 + " ? "
        + newParam.getParamTopoNetwork1().getGraphePlanaire(), oldParam.topologieGraphePlanaire1 == newParam.getParamTopoNetwork1().getGraphePlanaire());
    Assert.assertTrue("Comparaison de topologieSeuilFusionNoeuds1 : " + oldParam.topologieSeuilFusionNoeuds1 + " ? "
        + newParam.getParamTopoNetwork1().getSeuilFusionNoeuds(), oldParam.topologieSeuilFusionNoeuds1 == newParam.getParamTopoNetwork1().getSeuilFusionNoeuds());
    Assert.assertTrue("Comparaison de topologieFusionArcsDoubles1 : " + oldParam.topologieFusionArcsDoubles1 + " ? "
        + newParam.getParamTopoNetwork1().getFusionArcsDoubles(), oldParam.topologieFusionArcsDoubles1 == newParam.getParamTopoNetwork1().getFusionArcsDoubles());
    Assert.assertTrue("Comparaison de topologieElimineNoeudsAvecDeuxArcs1 : " + oldParam.topologieElimineNoeudsAvecDeuxArcs1 + " ? "
        + newParam.getParamTopoNetwork1().getElimineNoeudsAvecDeuxArcs(), oldParam.topologieElimineNoeudsAvecDeuxArcs1 == newParam.getParamTopoNetwork1().getElimineNoeudsAvecDeuxArcs());
    
    // Topologie du réseau 2
    Assert.assertTrue("Comparaison de topologieGraphePlanaire2 : " + oldParam.topologieGraphePlanaire2 + " ? "
        + newParam.getParamTopoNetwork2().getGraphePlanaire(), oldParam.topologieGraphePlanaire2 == newParam.getParamTopoNetwork2().getGraphePlanaire());
    Assert.assertTrue("Comparaison de topologieSeuilFusionNoeuds2 : " + oldParam.topologieSeuilFusionNoeuds2 + " ? "
        + newParam.getParamTopoNetwork2().getSeuilFusionNoeuds(), oldParam.topologieSeuilFusionNoeuds2 == newParam.getParamTopoNetwork2().getSeuilFusionNoeuds());
    Assert.assertTrue("Comparaison de topologieFusionArcsDoubles2 : " + oldParam.topologieFusionArcsDoubles2 + " ? "
        + newParam.getParamTopoNetwork2().getFusionArcsDoubles(), oldParam.topologieFusionArcsDoubles2 == newParam.getParamTopoNetwork2().getFusionArcsDoubles());
    Assert.assertTrue("Comparaison de topologieElimineNoeudsAvecDeuxArcs2 : " + oldParam.topologieElimineNoeudsAvecDeuxArcs2 + " ? "
        + newParam.getParamTopoNetwork2().getElimineNoeudsAvecDeuxArcs(), oldParam.topologieElimineNoeudsAvecDeuxArcs2 == newParam.getParamTopoNetwork2().getElimineNoeudsAvecDeuxArcs());
    
    // Projection des noeuds1 sur reseau2 
    Assert.assertTrue("Comparaison de projeteNoeuds1SurReseau2 : " + oldParam.projeteNoeuds1SurReseau2 + " ? "
        + newParam.getParamProjNetwork1().getProjeteNoeuds1SurReseau2(), 
        oldParam.projeteNoeuds1SurReseau2 == newParam.getParamProjNetwork1().getProjeteNoeuds1SurReseau2());
    Assert.assertTrue("Comparaison de projeteNoeuds1SurReseau2DistanceNoeudArc : " + oldParam.projeteNoeuds1SurReseau2DistanceNoeudArc + " ? "
        + newParam.getParamProjNetwork1().getProjeteNoeuds1SurReseau2DistanceNoeudArc(), 
        oldParam.projeteNoeuds1SurReseau2DistanceNoeudArc == newParam.getParamProjNetwork1().getProjeteNoeuds1SurReseau2DistanceNoeudArc());
    Assert.assertTrue("Comparaison de projeteNoeuds1SurReseau2DistanceProjectionNoeud : " + oldParam.projeteNoeuds1SurReseau2DistanceProjectionNoeud + " ? "
        + newParam.getParamProjNetwork1().getProjeteNoeuds1SurReseau2DistanceProjectionNoeud(), 
        oldParam.projeteNoeuds1SurReseau2DistanceProjectionNoeud == newParam.getParamProjNetwork1().getProjeteNoeuds1SurReseau2DistanceProjectionNoeud());
    Assert.assertTrue("Comparaison de projeteNoeuds1SurReseau2ImpassesSeulement : " + oldParam.projeteNoeuds1SurReseau2ImpassesSeulement + " ? "
        + newParam.getParamProjNetwork1().getProjeteNoeuds1SurReseau2ImpassesSeulement(), 
        oldParam.projeteNoeuds1SurReseau2ImpassesSeulement == newParam.getParamProjNetwork1().getProjeteNoeuds1SurReseau2ImpassesSeulement());
    
    // Projection des noeuds2 sur reseau1 
    Assert.assertTrue("Comparaison de projeteNoeuds2SurReseau1 : " + oldParam.projeteNoeuds2SurReseau1 + " ? "
        + newParam.getParamProjNetwork2().getProjeteNoeuds1SurReseau2(), 
        oldParam.projeteNoeuds2SurReseau1 == newParam.getParamProjNetwork2().getProjeteNoeuds1SurReseau2());
    Assert.assertTrue("Comparaison de projeteNoeuds2SurReseau1DistanceNoeudArc : " + oldParam.projeteNoeuds2SurReseau1DistanceNoeudArc + " ? "
        + newParam.getParamProjNetwork2().getProjeteNoeuds1SurReseau2DistanceNoeudArc(), 
        oldParam.projeteNoeuds2SurReseau1DistanceNoeudArc == newParam.getParamProjNetwork2().getProjeteNoeuds1SurReseau2DistanceNoeudArc());
    Assert.assertTrue("Comparaison de projeteNoeuds2SurReseau1DistanceProjectionNoeud : " + oldParam.projeteNoeuds2SurReseau1DistanceProjectionNoeud + " ? "
        + newParam.getParamProjNetwork2().getProjeteNoeuds1SurReseau2DistanceProjectionNoeud(), 
        oldParam.projeteNoeuds2SurReseau1DistanceProjectionNoeud == newParam.getParamProjNetwork2().getProjeteNoeuds1SurReseau2DistanceProjectionNoeud());
    Assert.assertTrue("Comparaison de projeteNoeuds2SurReseau1ImpassesSeulement : " + oldParam.projeteNoeuds2SurReseau1ImpassesSeulement + " ? "
        + newParam.getParamProjNetwork2().getProjeteNoeuds1SurReseau2ImpassesSeulement(), 
        oldParam.projeteNoeuds2SurReseau1ImpassesSeulement == newParam.getParamProjNetwork2().getProjeteNoeuds1SurReseau2ImpassesSeulement());
    
    // Recalage
    Assert.assertTrue("Comparaison de recalage : " + oldParam.debugBilanSurObjetsGeo + " ? false", oldParam.debugBilanSurObjetsGeo == false);
    
    // Export
    // oldParam.exportGeometrieLiens2vers1
    
    
    // Le hic c'est si le double sens n'est pas gerer de la même façon dans les 2 reseaux
    if (newParam.getParamDirectionNetwork1().getOrientationDouble() != newParam.getParamDirectionNetwork2().getOrientationDouble()) {
      
      // Direction reseau 1
      //Assert.assertTrue("Comparaison de attributOrientation1 : " + oldParam.attributOrientation1 + " ? " + newParam.getParamDirectionNetwork1().getAttributOrientation(), 
      //  oldParam.attributOrientation1 == newParam.getParamDirectionNetwork1().getAttributOrientation());
      // + table de hachage
    
      // Direction reseau 2
      //Assert.assertTrue("Comparaison de attributOrientation2 : " + oldParam.attributOrientation2 + " ? " + newParam.getParamDirectionNetwork2().getAttributOrientation(), 
      //  oldParam.attributOrientation2 == newParam.getParamDirectionNetwork2().getAttributOrientation());
      // + table de hachage
      
    } else {
      
      // Un des 2 reseaux n'est pas en double sens
      
      // Direction reseau 1
     // Assert.assertTrue("Comparaison de populationsArcsAvecOrientationDouble r1 : " + oldParam.populationsArcsAvecOrientationDouble + " ? " + newParam.getParamDirectionNetwork1().getOrientationDouble(), 
     //   oldParam.populationsArcsAvecOrientationDouble == newParam.getParamDirectionNetwork1().getOrientationDouble());
     // Assert.assertTrue("Comparaison de attributOrientation1 : " + oldParam.attributOrientation1 + " ? " + newParam.getParamDirectionNetwork1().getAttributOrientation(), 
      //  oldParam.attributOrientation1 == newParam.getParamDirectionNetwork1().getAttributOrientation());
      // + table de hachage
    
      // Direction reseau 2
      //Assert.assertTrue("Comparaison de populationsArcsAvecOrientationDouble r2 : " + oldParam.populationsArcsAvecOrientationDouble + " ? " + newParam.getParamDirectionNetwork2().getOrientationDouble(), 
      //    oldParam.populationsArcsAvecOrientationDouble == newParam.getParamDirectionNetwork2().getOrientationDouble());
      //Assert.assertTrue("Comparaison de attributOrientation2 : " + oldParam.attributOrientation2 + " ? " + newParam.getParamDirectionNetwork2().getAttributOrientation(), 
      //    oldParam.attributOrientation2 == newParam.getParamDirectionNetwork2().getAttributOrientation());
      // + table de hachage
    }
    
    // Variantes
    
    // Debug
    
  }*/
  

}
