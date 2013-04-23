package fr.ign.cogit.geoxygene.appli.plugin.datamatching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.AppariementIO;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Recalage;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

public class TestAppariement {

  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    
    // extrait-gps
    String filename1 = "D:\\Data\\Appariement\\MesTests\\EXTRAITS-GPS\\Circuits_totaux_SIG_2012.shp";
    String filename2 = "D:\\Data\\Appariement\\MesTests\\EXTRAITS-GPS\\bduni_gps.shp";
    IPopulation<IFeature> gPopRef = ShapefileReader.read(filename1);
    IPopulation<IFeature> gPopComp = ShapefileReader.read(filename2);
    
    float distanceNoeudsMax = 50;
    
    ParametresApp param = new ParametresApp();
    
    param.populationsArcs1.add(gPopRef);
    param.populationsArcs2.add(gPopComp);
    
    // Param 
    param.topologieGraphePlanaire1 = false;
    param.topologieFusionArcsDoubles1 = false;
    param.distanceNoeudsMax = distanceNoeudsMax;
    
    // Fixe
    param.topologieFusionArcsDoubles2 = false;
    param.topologieGraphePlanaire2 = false;
    param.topologieSeuilFusionNoeuds2 = 0.1;
    
    param.varianteFiltrageImpassesParasites = false;
    
    param.projeteNoeuds1SurReseau2 = false;
    param.projeteNoeuds1SurReseau2DistanceNoeudArc = param.distanceNoeudsMax; // 25
    param.projeteNoeuds1SurReseau2DistanceProjectionNoeud = 2*param.distanceNoeudsMax; // 50
    param.projeteNoeuds2SurReseau1 = false;
    param.projeteNoeuds2SurReseau1DistanceNoeudArc = param.distanceNoeudsMax; // 25
    param.projeteNoeuds2SurReseau1DistanceProjectionNoeud = 2*param.distanceNoeudsMax; // 50
    
    param.projeteNoeuds2SurReseau1ImpassesSeulement = false;
    // moins detaille
    param.varianteForceAppariementSimple = false;
    param.distanceArcsMax = 2*param.distanceNoeudsMax;
    param.distanceArcsMin = param.distanceNoeudsMax;
    param.varianteRedecoupageArcsNonApparies = false;
    
    // param.debugTirets = true;
    param.debugBilanSurObjetsGeo = false;
    // param.debugAffichageCommentaires = 2;
    
    param.populationsArcsAvecOrientationDouble = true;
    /*param.attributOrientation2 = "sens_de_circulation";
    param.orientationMap2 = new HashMap<Object, Integer>();
    param.orientationMap2.put("Sens direct", 1);
    param.orientationMap2.put("Sens inverse", -1);
    param.orientationMap2.put("Double sens", 2);*/
    
    List<ReseauApp> cartesTopo = new ArrayList<ReseauApp>();
    EnsembleDeLiens liens = AppariementIO.appariementDeJeuxGeo(param, cartesTopo);
    
    ReseauApp carteTopoReference = cartesTopo.get(0);
    ReseauApp carteTopoComparaison = cartesTopo.get(1);
    CarteTopo reseauRecale = Recalage.recalage(carteTopoReference, carteTopoComparaison, liens);
    // Split les multi
    for (Lien lien : liens) {
      IGeometry geom = lien.getGeom();
      if (geom instanceof GM_Aggregate<?>) {
        GM_MultiCurve<GM_LineString> multiCurve = new GM_MultiCurve<GM_LineString>();
        for (IGeometry lineGeom : ((GM_Aggregate<?>) geom).getList()) {
          if (lineGeom instanceof GM_LineString) {
            multiCurve.add((GM_LineString) lineGeom);
          } else {
            if (lineGeom instanceof GM_MultiCurve<?>) {
              multiCurve.addAll(((GM_MultiCurve<GM_LineString>) lineGeom).getList());
            }
          }
        }
        lien.setGeom(multiCurve);
      }
    }
    IPopulation<Arc> arcs = reseauRecale.getPopArcs();
    
    ShapefileWriter.write(arcs, "D:\\Data\\Appariement\\MesTests\\EXTRAITS-GPS\\reseau-apparie.shp");
    ShapefileWriter.write(liens, "D:\\Data\\Appariement\\MesTests\\EXTRAITS-GPS\\liens.shp");
    
  }
  
}
