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
 */
package fr.ign.cogit.geoxygene.appli.example;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.AbstractGeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.algo.SmallestSurroundingRectangleComputation;

/**
 * Calcul zones arborees : bosquets, haies, forêts reclassées.
 * 
 * @author Imran Lockat
 */
public class ZonesArborees extends AbstractGeOxygeneApplicationPlugin {

  /** Logger. */
  private final static Logger LOGGER = Logger.getLogger(ZonesArborees.class
      .getName());


  /**
   * Initialize the plugin.
   * @param application the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication application) {
    this.application = application;

    JMenu menu = addMenu("Example", "Calcul zones arborees");
    application.getMainFrame().getMenuBar()
      .add(menu, application.getMainFrame().getMenuBar().getMenuCount() - 2);
  }


  @Override
  public void actionPerformed(final ActionEvent e) {

    // On récupère la couche sélectionnée
    ProjectFrame project = this.application.getMainFrame()
        .getSelectedProjectFrame();
    Set<Layer> selectedLayers = project.getLayerLegendPanel()
        .getSelectedLayers();
    if (selectedLayers.size() != 1) {
      javax.swing.JOptionPane.showMessageDialog(null,
          "You need to select one (and only one) layer with polygon geometry.");
      ZonesArborees.LOGGER
          .error("You need to select one (and only one) layer.");
      return;
    }
    Layer layer = selectedLayers.iterator().next();

    // On construit une population de DefaultFeature pour les bosquets
    Population<DefaultFeature> popBosquets = new Population<DefaultFeature>(
        "Bosquets " + layer.getName());
    popBosquets.setClasse(DefaultFeature.class);
    popBosquets.setPersistant(false);

    // On construit une population de DefaultFeature pour les haies
    Population<DefaultFeature> popHaies = new Population<DefaultFeature>(
        "Haies " + layer.getName());
    popHaies.setClasse(DefaultFeature.class);
    popHaies.setPersistant(false);

    Population<DefaultFeature> popf = new Population<DefaultFeature>(
        "Forêts " + layer.getName());
    popf.setClasse(DefaultFeature.class);
    popf.setPersistant(false);
    
    int i = 0;

    for (IFeature f : layer.getFeatureCollection()) {
      double comp, conv, allong;
      Rectangle rmin = new Rectangle(SmallestSurroundingRectangleComputation.getSSR(f.getGeom())); 
      //si foret etroite
      if (f.getGeom().area() <= 400 && (rmin.longueur <= 30 || rmin.largeur <= 30)) { 
        ++i;     
        conv = f.getGeom().area() / f.getGeom().convexHull().area();       
        // test un peu plus sophistiqué (?) pour differencier haies et bosquets
        if (conv >= 0.7){ // cas convexe
          allong = rmin.longueur/rmin.largeur;
          if (allong >= 2.5)
            popHaies.nouvelElement(f.getGeom());
          else
            popBosquets.nouvelElement(f.getGeom());
        }
        else{ //cas concave
          comp = 4 * Math.PI * f.getGeom().area() / (f.getGeom().length() * f.getGeom().length());
          if (comp > 0.8)
            popBosquets.nouvelElement(f.getGeom());
          else
            popHaies.nouvelElement(f.getGeom());
        }
      } 
      // objet trop "important" : foret approximative
      else 
        popf.nouvelElement(f.getGeom());
    }
    
    //test sur une haie et un bout de foret particuliere
    /*for (IFeature h: popHaies){
      if (h.getId()== 821)
        System.out.println("haie 821 existe !");;
    }
    for (IFeature f: popf){
      if (f.getId()== 820)
        System.out.println("foret 820 existe !");;
    }*/

    LOGGER.info("end of parsing: " + i + " zones etroites");
    LOGGER.info("Nombre bosquets avant reclassement : "+popBosquets.size());
    LOGGER.info("Nombre haies avant reclassement : "+popHaies.size());
    LOGGER.info("Nombre element foret avant reclassement : "+popf.size());
    LOGGER.info("Nombre elements totaux avant reclassement : "+ (popHaies.size()+popBosquets.size()+popf.size()));
    LOGGER.info("Recherche haies et bosquets adjacents à la forêt ");
    i = 0;
    //for (IFeature foret: popf){
//    for (int k = 0 ; k < popf.size() ; ++k){
//      IFeature foret,b,h;
//      foret = popf.get(k);
//      if(foret.getId()==3018)
//        System.out.println("cette foret existe !");
//      //for (IFeature b: popBosquets){
//      for (int j = 0; j < popBosquets.size(); ++j){
//        b = popBosquets.get(j);
//        if (b.getGeom().intersects(foret.getGeom())){ //si on est adjacent
//            foret.setGeom(b.getGeom().union(foret.getGeom()));
//          // on ne peut pas faire ca apparemment...
//          //popBosquets.enleveElement((DefaultFeature) b);
//          popBosquets.enleveElement((DefaultFeature) b);         
//        }
//        else if (b.getGeom().isWithinDistance(foret.getGeom(),10) ){ //si on est  à moins de 10 m
//          if (b.getGeom().area() <= 8){ // si tres petit on supprime
//            popBosquets.enleveElement((DefaultFeature) b);
//            System.out.println("suppression d'un bosquet petit et adjacent à une forêt");
//          }
//          else { // sinon on classe comme foret
//            ++i;
//            popf.nouvelElement(b.getGeom());
//            popBosquets.enleveElement((DefaultFeature) b);
//          }
//        }
//      }
//      //for (IFeature h: popHaies){
//      for (int j = 0; j < popHaies.size(); ++j){
//        h = popHaies.get(j);
//        boolean traceur = (h.getId() == 1368) && (foret.getId() == 3018);
//        if (h.getId() == 1368 && foret.getId() == 3018)
//          System.out.println("**********cas testé***************");
//        if (h.getGeom().intersects(foret.getGeom())){ //si on est adjacent
//          if(traceur)
//            System.out.println("cas intersect");
//          foret.setGeom(h.getGeom().union(foret.getGeom()));
//          // on ne peut pas faire ca apparemment...
//          //popBosquets.enleveElement((DefaultFeature) b);
//          popHaies.enleveElement((DefaultFeature) h);         
//        }
//        //else if (h.getGeom().isWithinDistance(foret.getGeom(),10) ){ //si on est  à moins de 10 m
//        else if (h.getGeom().distance(foret.getGeom())<=10 ){ //si on est  à moins de 10 m
//          if(traceur)
//            System.out.println("cas dist<10");
//          if (h.getGeom().area() <= 8){ // si tres petit on supprime
//            if(traceur)
//              System.out.println("cas surf<8");
//            popHaies.enleveElement((DefaultFeature) h);
//            System.out.println("suppression d'une haie petite et adjacente à une forêt");
//          }
//          else { // sinon on classe comme foret
//            ++i;
//            if (traceur)
//              System.out.println("je suis bien à ma place moi la haie qui est à 2m de la foret !");
//            popf.nouvelElement(h.getGeom());
//            popHaies.enleveElement((DefaultFeature) h);
//          }
//        }
//      }
//    }
    
    // Listes contenant les haies et bosquets à supprimer,
    // et les element de foret à ajouter
    List<IFeature> haies = new ArrayList<>();
    List<IFeature> bosquets = new ArrayList<>();
    List<IFeature> forets = new ArrayList<>();
    
    int bidi = 0, biddi = 0, bide = 0;
    int hidi = 0, hiddi = 0, hide = 0;
    List<Population<DefaultFeature>> bosqha = new ArrayList<>();
    bosqha.add(popHaies);
    bosqha.add(popBosquets);
    for (IFeature foret: popf){
      // n = 0 => haies ; 1 => bosquets
      for (int n = 0; n < 2 ; ++n){
        for (IFeature b: bosqha.get(n)){
          if (b.getGeom().intersects(foret.getGeom())){ // si adjacentes on les marque
            foret.setGeom(foret.getGeom().union(b.getGeom()));
            switch (n) {
              case 0:
                haies.add(b);
                hidi++;
                break;
              case 1:
                bosquets.add(b);
                bidi++;
                break;
            }
          } else if(b.getGeom().isWithinDistance(foret.getGeom(), 10)){ // si proches
            switch(n){
              case 0:
                haies.add(b);
                forets.add(b);
                hiddi++;
                break;
              case 1:
                bosquets.add(b);
                forets.add(b);
                biddi++;
                break;
            }
          } else {
            switch(n){
              case 0:
                hide++;
                break;
              case 1:
                bide++;
                break;
            }      
          }
        }
      }
    }
    // on va essayer de reclasser des haies et bosquets en foret
    // si elles sont trop proches ou adjacentes à une foreot 
//    for (IFeature foret: popf){
//      // d'abord les bosquets
//      for (IFeature b: popBosquets){
//        if (b.getGeom().intersects(foret.getGeom())){ // si adjacentes on les marque
//          bidi++;
//          foret.setGeom(foret.getGeom().union(b.getGeom()));
//          bosquets.add(b);
//        } else if(b.getGeom().isWithinDistance(foret.getGeom(), 10)){ // si proches
//          biddi++;
//          bosquets.add(b);
//          forets.add(b);
//        } else {
//          bide++;
//        }      
//      }
//      // bis repetitae pour les haies, code à factoriser.. eventuellement
//      for (IFeature h: popHaies){
//        if (h.getGeom().intersects(foret.getGeom())){
//          hidi++;
//          foret.setGeom(foret.getGeom().union(h.getGeom()));
//          haies.add(h);
//        } else if(h.getGeom().isWithinDistance(foret.getGeom(), 10)){
//          hiddi++;
//          haies.add(h);
//          forets.add(h);
//        } else {
//          hide++;
//        }      
//      }   
//    }
    
    LOGGER.info(" bosquets ** intersections: " +bidi+ " - dist < 10: "+ biddi + " - cas nop: "+ bide);
    LOGGER.info(" haies ** intersections: " +hidi+ " - dist < 10: "+ hiddi + " - cas nop: "+ hide);
    LOGGER.info("Removing " +bosquets.size()+ " bosquets and "+ haies.size()+ " haies: ");
    popBosquets.removeAll(bosquets);
    popHaies.removeAll(haies);
    LOGGER.info("Adding " +forets.size()+ " elements in foret");
    for (IFeature f: forets)
      popf.nouvelElement(f.getGeom());
       
    System.out.println();
    LOGGER.info("Bosquet et haies: " + (hiddi + biddi) + " reclassements comme foret par proximite");
    LOGGER.info("Nombre bosquets apres reclassement : "+popBosquets.size());
    LOGGER.info("Nombre haies apres reclassement : "+popHaies.size());
    LOGGER.info("Nombre element foret apres reclassement : "+popf.size());
    LOGGER.info("Nombre elements totaux apres reclassement : "+ (popHaies.size()+popBosquets.size()+popf.size()));
    
    // test car doute sur reclassement d'une haie en forêt
    /*IFeature haie = null;
    for (IFeature h: popHaies){
      if (h.getId()== 821)
        haie = h;
    }
    IFeature foret = null;
    for (IFeature f: popf){
      if (f.getId()== 820)
        foret = f;
    }
    if (foret != null && haie != null)
      System.out.println("distance couple test foret-haie : " +foret.getGeom().distance(haie.getGeom()));*/
    
    // Créer les métadonnées du jeu correspondant et on l'ajoute à la population
    FeatureType newFeatureType = new FeatureType();
    newFeatureType.setGeometryType(layer.getFeatureCollection().get(0)
        .getGeom().getClass());

    popf.setFeatureType(newFeatureType);
    // On ajoute au ProjectFrame la nouvelle couche créée à partir de la
    // nouvelle population
    project.getDataSet().addPopulation(popf);
    project.addFeatureCollection(popf, popf.getNom(), layer.getCRS());

    popBosquets.setFeatureType(newFeatureType);
    // On ajoute au ProjectFrame la nouvelle couche créée à partir de la
    // nouvelle population
    project.getDataSet().addPopulation(popBosquets);
    project.addFeatureCollection(popBosquets, popBosquets.getNom(),
        layer.getCRS());

    popHaies.setFeatureType(newFeatureType);
    // On ajoute au ProjectFrame la nouvelle couche créée à partir de la
    // nouvelle population
    project.getDataSet().addPopulation(popHaies);
    project.addFeatureCollection(popHaies, popHaies.getNom(), layer.getCRS());
    
    LOGGER.info("fin du process de calcul");
  }
  
  //classe interne pour rendre le code qui utilise 
  //le rectangle englobant minimal plus expressif
  //.. et pour manipuler...
  private class Rectangle{
    private double longueur, largeur;
    Rectangle(IPolygon p){
      IDirectPositionList coords = p.coord();
      longueur = coords.get(0).distance(coords.get(1));
      largeur = coords.get(1).distance(coords.get(2));
      if (longueur < largeur){
        double t = longueur;
        longueur = largeur;
        largeur = t;
      }
    }
    // getters qui n'ont pas de sens.. Comme on voit dans le code,
    // les attributs de la classe interne sont accessibles à la classe "englobante"
    // quelles que soient leurs visibilités (ici private...)
    /*public double longueur(){
      return longueur;
    }
    public double largeur(){
      return largeur;
    }*/
  }
}
