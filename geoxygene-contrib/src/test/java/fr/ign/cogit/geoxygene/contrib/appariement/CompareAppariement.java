package fr.ign.cogit.geoxygene.contrib.appariement;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.AppariementIO;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.NetworkDataMatchingProcess;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.DatasetNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

public class CompareAppariement {
  
  /**
   * On compare avec le même jeu de données, l'ancien appariement et le nouveau.<br/>
   * - Les paramètres par défaut.<br/>
   * - Sans recalage <br/>
   * 
   * Pour l'instant on compare les objets : EnsembleDeLiens.<br/>
   * 
   */
  @Test
  public void testFiltreNoeudsSimples() {
    
    URL url = CompareAppariement.class.getResource("/data/");
    
    // Réseaux
    IPopulation<IFeature> reseau1 = ShapefileReader.read(url.getPath() + "reseau1.shp");
    IPopulation<IFeature> reseau2 = ShapefileReader.read(url.getPath() + "reseau2.shp");
    
    // Résultats de l'appariement avec l'ancienne structure des objets
    ParametresApp paramApp = new ParametresApp();
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
    NetworkDataMatchingProcess networkDataMatchingProcess = new NetworkDataMatchingProcess(param, datasetNetwork1, datasetNetwork2, false);
    ResultNetworkDataMatching resultatAppariement2 = networkDataMatchingProcess.networkDataMatching();
    EnsembleDeLiens edl2 = resultatAppariement2.getLinkDataSet();
    
    // -----------------------------------------------------------------------------------------------------------------------
    // On peut déjà comparer les paramètres
    compareParam(paramApp, param);
    
    // -----------------------------------------------------------------------------------------------------------------------
    // On compare : bigre comment faire !!!
    compareResultLien(edl1, edl2);
    compareCarteTopo(cartesTopo, resultatAppariement2);  
  
  }
  
  
  private void compareResultLien(EnsembleDeLiens e1, EnsembleDeLiens e2) {
    
    // Nombre de liens
    Assert.assertTrue("\n" + "Nb de liens dans l'ancien algo = " + e1.size() + ",\n" 
        + "Nb de liens dans le nouvel algo = " + e2.size(), e1.size() == e2.size());
    
  }
  
  private void compareCarteTopo(List<ReseauApp> listCarteTopo, ResultNetworkDataMatching resultatAppariement) {
    
    // Nombre d'arcs du reseau 1
    Assert.assertTrue("Nb arcs carte topo reseau 1 = " + listCarteTopo.get(0).getListeArcs().size() + " ? " + resultatAppariement.getReseau1().getListeArcs().size(), 
        listCarteTopo.get(0).getListeArcs().size() == resultatAppariement.getReseau1().getListeArcs().size());
    
    // Nombre de noeuds du reseau 1
    Assert.assertTrue("Nb noeuds carte topo reseau 1 = " + listCarteTopo.get(0).getListeNoeuds().size() + " ? " + resultatAppariement.getReseau1().getListeNoeuds().size(), 
        listCarteTopo.get(0).getListeNoeuds().size() == resultatAppariement.getReseau1().getListeNoeuds().size());
    
    // Nombre d'arcs du reseau 2
    Assert.assertTrue("Nb arcs carte topo reseau 2 = " + listCarteTopo.get(1).getListeArcs().size() + " ? " + resultatAppariement.getReseau2().getListeArcs().size(), 
        listCarteTopo.get(1).getListeArcs().size() == resultatAppariement.getReseau2().getListeArcs().size());
    
    // Nombre de noeuds du reseau 2
    Assert.assertTrue("Nb noeuds carte topo reseau 2 = " + listCarteTopo.get(1).getListeNoeuds().size() + " ? " + resultatAppariement.getReseau2().getListeNoeuds().size(), 
        listCarteTopo.get(1).getListeNoeuds().size() == resultatAppariement.getReseau2().getListeNoeuds().size());
    
  }
  
  
  /**
   * Comparaison des paramètres.
   * 
   * @param paramApp
   * @param param
   */
  private void compareParam(ParametresApp oldParam, ParamNetworkDataMatching newParam) {
    
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
        + newParam.getParamTopoNetwork1().getTopologieGraphePlanaire(), oldParam.topologieGraphePlanaire1 == newParam.getParamTopoNetwork1().getTopologieGraphePlanaire());
    Assert.assertTrue("Comparaison de topologieSeuilFusionNoeuds1 : " + oldParam.topologieSeuilFusionNoeuds1 + " ? "
        + newParam.getParamTopoNetwork1().getTopologieSeuilFusionNoeuds(), oldParam.topologieSeuilFusionNoeuds1 == newParam.getParamTopoNetwork1().getTopologieSeuilFusionNoeuds());
    Assert.assertTrue("Comparaison de topologieFusionArcsDoubles1 : " + oldParam.topologieFusionArcsDoubles1 + " ? "
        + newParam.getParamTopoNetwork1().getTopologieFusionArcsDoubles(), oldParam.topologieFusionArcsDoubles1 == newParam.getParamTopoNetwork1().getTopologieFusionArcsDoubles());
    Assert.assertTrue("Comparaison de topologieElimineNoeudsAvecDeuxArcs1 : " + oldParam.topologieElimineNoeudsAvecDeuxArcs1 + " ? "
        + newParam.getParamTopoNetwork1().getTopologieElimineNoeudsAvecDeuxArcs(), oldParam.topologieElimineNoeudsAvecDeuxArcs1 == newParam.getParamTopoNetwork1().getTopologieElimineNoeudsAvecDeuxArcs());
    
    // Topologie du réseau 2
    Assert.assertTrue("Comparaison de topologieGraphePlanaire2 : " + oldParam.topologieGraphePlanaire2 + " ? "
        + newParam.getParamTopoNetwork2().getTopologieGraphePlanaire(), oldParam.topologieGraphePlanaire2 == newParam.getParamTopoNetwork2().getTopologieGraphePlanaire());
    Assert.assertTrue("Comparaison de topologieSeuilFusionNoeuds2 : " + oldParam.topologieSeuilFusionNoeuds2 + " ? "
        + newParam.getParamTopoNetwork2().getTopologieSeuilFusionNoeuds(), oldParam.topologieSeuilFusionNoeuds2 == newParam.getParamTopoNetwork2().getTopologieSeuilFusionNoeuds());
    Assert.assertTrue("Comparaison de topologieFusionArcsDoubles2 : " + oldParam.topologieFusionArcsDoubles2 + " ? "
        + newParam.getParamTopoNetwork2().getTopologieFusionArcsDoubles(), oldParam.topologieFusionArcsDoubles2 == newParam.getParamTopoNetwork2().getTopologieFusionArcsDoubles());
    Assert.assertTrue("Comparaison de topologieElimineNoeudsAvecDeuxArcs2 : " + oldParam.topologieElimineNoeudsAvecDeuxArcs2 + " ? "
        + newParam.getParamTopoNetwork2().getTopologieElimineNoeudsAvecDeuxArcs(), oldParam.topologieElimineNoeudsAvecDeuxArcs2 == newParam.getParamTopoNetwork2().getTopologieElimineNoeudsAvecDeuxArcs());
    
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
    
    // Direction reseau 1
    Assert.assertTrue("Comparaison de populationsArcsAvecOrientationDouble r1 : " + oldParam.populationsArcsAvecOrientationDouble + " ? " + newParam.getParamDirectionNetwork1().getOrientationDouble(), 
        oldParam.populationsArcsAvecOrientationDouble == newParam.getParamDirectionNetwork1().getOrientationDouble());
    Assert.assertTrue("Comparaison de attributOrientation1 : " + oldParam.attributOrientation1 + " ? " + newParam.getParamDirectionNetwork1().getAttributOrientation(), 
        oldParam.attributOrientation1 == newParam.getParamDirectionNetwork1().getAttributOrientation());
    // + table de hachage
    
    // Direction reseau 2
    Assert.assertTrue("Comparaison de populationsArcsAvecOrientationDouble r2 : " + oldParam.populationsArcsAvecOrientationDouble + " ? " + newParam.getParamDirectionNetwork2().getOrientationDouble(), 
        oldParam.populationsArcsAvecOrientationDouble == newParam.getParamDirectionNetwork2().getOrientationDouble());
    Assert.assertTrue("Comparaison de attributOrientation2 : " + oldParam.attributOrientation2 + " ? " + newParam.getParamDirectionNetwork2().getAttributOrientation(), 
        oldParam.attributOrientation2 == newParam.getParamDirectionNetwork2().getAttributOrientation());
    // + table de hachage
    
    // Variantes
    
    // Debug
    
  }
  

}
