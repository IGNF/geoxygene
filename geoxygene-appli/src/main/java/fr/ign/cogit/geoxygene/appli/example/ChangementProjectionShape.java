package fr.ign.cogit.geoxygene.appli.example;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;

/**
 * Centrer facilement une carte sur une longitude particulière.
 * Très bon exercice pour manipuler les géométries avec les DirectPosition
 * 
 * @author GBrun
 */
public class ChangementProjectionShape implements GeOxygeneApplicationPlugin, ActionListener {
  
  /** Logger. */
  private static final Logger LOGGER = Logger.getLogger(ChangementProjectionShape.class.getName());
  
  private GeOxygeneApplication application = null;
  
  @Override
  public void initialize(final GeOxygeneApplication application) {
    this.application = application;
    
    JMenu menuExample = null;
    String menuName = "Example"; 
    for (Component c : application.getMainFrame().getMenuBar().getComponents()) {
      if (c instanceof JMenu) {
        JMenu aMenu = (JMenu) c;
        if (aMenu.getText() != null
            && aMenu.getText().equalsIgnoreCase(menuName)) {
          menuExample = aMenu;
        }
      }
    }
    if (menuExample == null) {
      menuExample = new JMenu(menuName);
    }
    
    JMenuItem cgtProjectionItem = new JMenuItem("ChangementProjectionShape");
    cgtProjectionItem.addActionListener(this);
    menuExample.add(cgtProjectionItem);

    this.application.getMainFrame()
        .getMenuBar()
        .add(menuExample,
            this.application.getMainFrame().getMenuBar().getComponentCount() - 1);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void actionPerformed(ActionEvent e) {
    LOGGER.debug("Start changement de projection");
    ProjectFrame project = this.application.getMainFrame().getSelectedProjectFrame();
    Set<Layer> selectedLayers = project.getLayerLegendPanel().getSelectedLayers();
    if (selectedLayers.isEmpty() || selectedLayers.size() > 1) {
      javax.swing.JOptionPane.showMessageDialog(null, "You need to select one and only one layer.");
      LOGGER.error("You need to select one and only one layer.");
      return;
    }
    
    IFeatureCollection<IFeature> countryCollection = new FT_FeatureCollection<IFeature>();
    for (Layer layer : selectedLayers) {
      if (IPolygon.class.isAssignableFrom(layer.getFeatureCollection().getFeatureType().getGeometryType()) ||
          IMultiSurface.class.isAssignableFrom(layer.getFeatureCollection().getFeatureType().getGeometryType())) {
        countryCollection = (IFeatureCollection<IFeature>) layer.getFeatureCollection();
      } 
      // layer.setVisible(false);
    }
    
    // On va stocker le Feature sélectionner dans une nouvelle Population
    Population<DefaultFeature> popSelectionnee = new Population<DefaultFeature>(false, "entrees", DefaultFeature.class, true);
    
    // On recherche le code ISO sur 2 chiffres du pays dans le ShapeFile
    for (IFeature feat : countryCollection) {
        // On sélectionne uniquement le polygone des Etats-unis
        if(feat.getAttribute("iso_a2").equals("US")){
            // On stocke le Feature sélectionné dans une nouvelle Population
            popSelectionnee.setFeatureType(countryCollection.getFeatureType());
            popSelectionnee.add((DefaultFeature) feat);
        }
    }
    
    // On crée une nouvelle population qui sera peuplée par les Feature qui auront été translatés
    Population<IFeature> popTranslation = new Population<IFeature>();
    popTranslation.setFeatureType(popSelectionnee.getFeatureType());
    
    // On parcourt la population initiale (il n'y en a qu'une seule en fait)
    for (int i = 0; i < popSelectionnee.size(); i++) {
        
        // On caste le Feature de la population (qui est un MultiPolygon en plusieurs polygones simples
        DefaultFeature ancienFeature = popSelectionnee.get(i);
        GM_MultiSurface<?> multiSurface = (GM_MultiSurface<?>) ancienFeature.getGeom();
                    
        // On parcourt tous les polygones
        for (int j = 0; j < multiSurface.size(); j++) {
            
            // On récupère les coordonnées du polygone courant
            IDirectPositionList coordsInitiales = multiSurface.get(j).coord();
            
            // On crée une liste qui contiendra les coordonnées du polygone translaté
            List<IDirectPosition> listeCoordTranslatees = new ArrayList<IDirectPosition>();
            
            // On parcourt toutes les coordonnées du polygone courant
            for(IDirectPosition coordAncienne : coordsInitiales.getList()){
                
                // On translate l'ancienne coordonnée X de 40
                double ancienneLongitude = coordAncienne.getX();
                double nouvelleLongitude = ancienneLongitude + 40;
                                    
                // Si l'ancienne longitude est supérieure à la limite Est du WGS84, la nouvelle longitude est replacée toute à l'ouest
                if(nouvelleLongitude > 180.0){
                    double diff = nouvelleLongitude - 180.0;
                    nouvelleLongitude = -180 +diff;
                }
                
                // On ajoute la nouvelle paire de coordonnées à la liste de coordonnées
                IDirectPosition coordNouvelle = new DirectPosition(nouvelleLongitude, coordAncienne.getY());
                listeCoordTranslatees.add(coordNouvelle);
            }
        
            // On crée un nouveau Feature qui contiendra le nouveau polygone créé
            DefaultFeature nouveauFeature = new DefaultFeature();
            nouveauFeature.setFeatureType(ancienFeature.getFeatureType());
            
            // On crée un polygone à partir de cette nouvelle liste de coordonnées
            GM_LineString lineString = new GM_LineString(listeCoordTranslatees);
            GM_Polygon nouveauPolygone = new GM_Polygon(lineString);
                    
            // On ajoute le polygone au nouveau Feature, puis ce dernier à la nouvelle Population
            nouveauFeature.setGeom(nouveauPolygone);
            popTranslation.add(nouveauFeature);
            
        }

    }
 
    // Affiche les limites du WGS84 (module InfoTexte)
    Layer layerLimites = this.application.getMainFrame().getSelectedProjectFrame().addUserLayer(genererEmprise(-180, 90, -90, 180), "Limites du WGS84", null);
    
    // Création des couches contenant les Etats-Unis avec les coordonnées normales et translatées
    Layer layerInitial = this.application.getMainFrame().getSelectedProjectFrame().addUserLayer(popSelectionnee, "Etats-Unis avant translation", null);
    Layer layerTranslate = this.application.getMainFrame().getSelectedProjectFrame().addUserLayer(popTranslation, "Etats-Unis après translation", null);
    
    layerInitial.getSymbolizer().setUnitOfMeasurePixel();
    layerTranslate.getSymbolizer().setUnitOfMeasurePixel();
    layerLimites.getSymbolizer().setUnitOfMeasurePixel();
    
    layerInitial.getSymbolizer().getStroke().setStrokeWidth(3f);
    layerTranslate.getSymbolizer().getStroke().setStrokeWidth(3f);
    layerLimites.getSymbolizer().setUnitOfMeasurePixel();
    
    Color darkRed = new Color(80, 0, 0);
    Color lightRed = new Color(255, 0, 0);
    
    Color darkBlue = new Color(0, 0, 80);
    Color lightBlue = new Color(0, 0, 255);
    
    Color darkGreen = new Color(254, 212, 116);
    
    layerInitial.getSymbolizer().getStroke().setColor(darkRed);
    ((PolygonSymbolizer)layerInitial.getSymbolizer()).getFill().setColor(lightRed);
    
    layerTranslate.getSymbolizer().getStroke().setColor(darkBlue);
    ((PolygonSymbolizer)layerTranslate.getSymbolizer()).getFill().setColor(lightBlue);
    
    layerLimites.getSymbolizer().getStroke().setColor(darkGreen);
    ((PolygonSymbolizer)layerLimites.getSymbolizer()).getFill().setColor(darkGreen);
            
    // On zoom sur l'étendue maximale (limites du WGS84)
    try {
      this.application.getMainFrame().getSelectedProjectFrame().getLayerViewPanel().getViewport().zoomToFullExtent();
    } catch (NoninvertibleTransformException e1) {
        e1.printStackTrace();
    }
    
  }
  
  /**
   * 
   * @param ouest
   * @param nord
   * @param sud
   * @param est
   * @return
   */
  private Population<DefaultFeature> genererEmprise(double ouest, double nord, double sud, double est) {

    /* ----------------------------- Création du polygone d'emprise -----------------------------*/
    
    // Création des points
    DirectPosition no = new DirectPosition(ouest, nord);
    DirectPosition ne = new DirectPosition(est, nord);
    DirectPosition se = new DirectPosition(est, sud);
    DirectPosition so = new DirectPosition(ouest, sud);

    // Ajouts des points dans une liste
    List<IDirectPosition> listeDP = new ArrayList<IDirectPosition>();
    listeDP.add(no);
    listeDP.add(ne);
    listeDP.add(se);
    listeDP.add(so);
    
    // Création d'une ligne à partir des points
    GM_LineString ls = new GM_LineString(listeDP);

    // Création du polygone à partir de la ligne
    GM_Polygon polygone = new GM_Polygon(ls);
    
    /* ----------------------------- Ajout du polygone dans une population -----------------------------*/
    
    // Création de nouveaux attributs vides
    Object[] attributes = new Object[] {};
    
    // Création d'un featureType pour polygone (pour l'emprise)
    FeatureType featureTypePolygon = new FeatureType();
    featureTypePolygon.setTypeName("emprise");
    featureTypePolygon.setGeometryType(IPolygon.class);
    
    // Création d'un schéma associé au featureType
    SchemaDefaultFeature schemaPolygon = new SchemaDefaultFeature();
    schemaPolygon.setFeatureType(featureTypePolygon);
    featureTypePolygon.setSchema(schemaPolygon);
    
     // Création de la population de l'emprise générée
    Population<DefaultFeature> popEmprise = new Population<DefaultFeature>(false, "emprise générée", DefaultFeature.class, true);
    popEmprise.setFeatureType(featureTypePolygon);
    
    // On ajoute le polygone d'emprise générée à la population
    DefaultFeature n1 = popEmprise.nouvelElement(polygone);
    n1.setSchema(schemaPolygon);
    n1.setAttributes(attributes);
            
    return popEmprise;
    
  }

}
