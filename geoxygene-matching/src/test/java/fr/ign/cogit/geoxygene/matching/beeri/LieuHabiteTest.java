package fr.ign.cogit.geoxygene.matching.beeri;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

/**
 * 
 * 
 * 
 */
public class LieuHabiteTest {
  
  @Test
  public void testAppariementPPV() {

    // Load dataset
    IPopulation<IFeature> popComp = ShapefileReader.read("./data/ign-point/LIEU_DIT_HABITE.shp", "oronyme", null, true);
    IPopulation<IFeature> popRef = ShapefileReader.read("./data/ign-point/ZONE_HABITAT.shp", "Relief", null, true);
    Assert.assertEquals("Pop lieu dit habite mal chargee : ", 445, popComp.size());
    Assert.assertEquals("Pop zone habitat mal chargée : ", 65, popRef.size());
    
    // ------------------------------------------------------------------------------------
    double seuilDistanceMax = 200.0;
    EnsembleDeLiens edl = AppariementBeeri.appariementPPV(popRef, popComp, seuilDistanceMax);
    
    Assert.assertEquals("Nombre de liens trouvés = ", 45, edl.size());

    boolean trouveBousse = false;
    for (Lien lien : edl) {
      String toponyme = lien.getObjetsRef().get(0).getAttribute("TOPONYME").toString();
      String nom = lien.getObjetsComp().get(0).getAttribute("NOM").toString();
    
      if (toponyme.equals("le petit versailles")) {
        // le petit versailles : 1 candidat trouvé (le petit versailles)
        Assert.assertEquals("Candidat attendu pour le toponyme le petit versailles : le petit versailles. ", "le petit versailles", nom);
      } else if (toponyme.equals("bousse")) {
        // bousse : 0 candidat
        trouveBousse = true;
      } else if (toponyme.equals("clermont-créans")) {
        // clermont-créans : 1 candidat trouvé (basse folie)
        Assert.assertEquals("Candidat attendu pour le toponyme clermont-créans : basse folie. ", "basse folie", nom);
      } else if (toponyme.equals("sainte-colombe")) {
        // sainte-colombe : 1 candidat trouvé (sainte-colombe)
        Assert.assertEquals("Candidat attendu pour le toponyme sainte-colombe : sainte-colombe. ", "sainte-colombe", nom);
      } // else {
        // System.out.println(toponyme);
      // }
    }
    Assert.assertEquals("Aucun lien pour bousse. ", false, trouveBousse);
    
    // ------------------------------------------------------------------------------------
    seuilDistanceMax = 10.0;
    edl = AppariementBeeri.appariementPPV(popRef, popComp, seuilDistanceMax);
    
    Assert.assertEquals("Nombre de liens trouvés = ", 32, edl.size());

    trouveBousse = false;
    boolean trouveSainteColombe = false;
    for (Lien lien : edl) {
      String toponyme = lien.getObjetsRef().get(0).getAttribute("TOPONYME").toString();
      String nom = lien.getObjetsComp().get(0).getAttribute("NOM").toString();
    
      if (toponyme.equals("le petit versailles")) {
        // le petit versailles : 1 candidat trouvé (le petit versailles)
        Assert.assertEquals("Candidat attendu pour le toponyme le petit versailles : le petit versailles. ", "le petit versailles", nom);
      } else if (toponyme.equals("bousse")) {
        // bousse : 0 candidat
        trouveBousse = true;
      } else if (toponyme.equals("clermont-créans")) {
        // clermont-créans : 1 candidat trouvé (basse folie)
        Assert.assertEquals("Candidat attendu pour le toponyme clermont-créans : basse folie. ", "basse folie", nom);
      } else if (toponyme.equals("sainte-colombe")) {
        // sainte-colombe : 1 candidat trouvé (sainte-colombe)
        trouveSainteColombe = true;
      }
    }
    Assert.assertEquals("Aucun lien pour bousse. ", false, trouveBousse);
    Assert.assertEquals("Aucun lien pour sainte-colombe. ", false, trouveSainteColombe);
  }
  
  @Test
  public void testAppariementPPVDansLesDeuxSens() {

    // Load dataset
    IPopulation<IFeature> popComp = ShapefileReader.read("./data/ign-point/LIEU_DIT_HABITE.shp", "oronyme", null, true);
    IPopulation<IFeature> popRef = ShapefileReader.read("./data/ign-point/ZONE_HABITAT.shp", "Relief", null, true);
    Assert.assertEquals("Pop lieu dit habite mal chargee : ", 445, popComp.size());
    Assert.assertEquals("Pop zone habitat mal chargée : ", 65, popRef.size());

    double seuilDistanceMax = 50.0;
    EnsembleDeLiens edl = AppariementBeeri.appariementPPVDansLesDeuxSens(popRef, popComp, seuilDistanceMax);
    Assert.assertEquals("Nombre de liens trouvés = ", 33, edl.size());
    
  }
  
  @Test
  public void testAppariementProbabilite() {
    IPopulation<IFeature> popComp = ShapefileReader.read("./data/ign-point/LIEU_DIT_HABITE.shp", "oronyme", null, true);
    IPopulation<IFeature> popRef = ShapefileReader.read("./data/ign-point/ZONE_HABITAT.shp", "Relief", null, true);
    Assert.assertEquals("Pop lieu dit habite mal chargee : ", 445, popComp.size());
    Assert.assertEquals("Pop zone habitat mal chargée : ", 65, popRef.size());
    
    double seuilDistance = 50.0;
    double alpha = 1.1;
    EnsembleDeLiens edl = AppariementBeeri.appariementProbabilite(popRef, popComp, seuilDistance, alpha);
    System.out.println(edl.size());
  }
  
  @Test
  public void testRemplissageMatriceApp() {
    IPopulation<IFeature> popComp = ShapefileReader.read("./data/ign-point/LIEU_DIT_HABITE.shp", "oronyme", null, true);
    IPopulation<IFeature> popRef = ShapefileReader.read("./data/ign-point/ZONE_HABITAT.shp", "Relief", null, true);
    Assert.assertEquals("Pop lieu dit habite mal chargee : ", 445, popComp.size());
    Assert.assertEquals("Pop zone habitat mal chargée : ", 65, popRef.size());
    
    double seuilDistance = 50.0;
    double alpha = 1.1;
    EnsembleDeLiens edl = AppariementBeeri.remplissageMatriceApp(popRef, popComp, seuilDistance, alpha);
    System.out.println(edl.size());
  }

  @Test
  public void testAppariementPPVEvalTop() {
    IPopulation<IFeature> popComp = ShapefileReader.read("./data/ign-point/LIEU_DIT_HABITE.shp", "oronyme", null, true);
    IPopulation<IFeature> popRef = ShapefileReader.read("./data/ign-point/ZONE_HABITAT.shp", "Relief", null, true);
    Assert.assertEquals("Pop lieu dit habite mal chargee : ", 445, popComp.size());
    Assert.assertEquals("Pop zone habitat mal chargée : ", 65, popRef.size());
    
    double seuilEcart = 20.0;
    double seuilDistanceMax = 50.0;
    EnsembleDeLiens edl = AppariementBeeri.appariementPPVEvalTop(popRef, popComp, seuilEcart, seuilDistanceMax, "NOM", "TOPONYME");
    Assert.assertEquals("Nombre de liens trouvés = ", 33, edl.size());
  }
    
}
