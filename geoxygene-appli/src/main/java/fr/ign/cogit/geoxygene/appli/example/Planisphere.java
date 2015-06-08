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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.AbstractGeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

/**
 * Centrer facilement une carte sur une longitude particulière.
 * Très bon exercice pour manipuler les géométries avec les DirectPosition
 * 
 * @author GBrun
 */
public class Planisphere extends AbstractGeOxygeneApplicationPlugin {
  
  /** Logger. */
  private static final Logger LOGGER = Logger.getLogger(Planisphere.class.getName());
  
  @Override
  public void initialize(final GeOxygeneApplication application) {
    this.application = application;

    JMenu menu = addMenu("Example", "Center of the World");
    application.getMainFrame().getMenuBar()
      .add(menu, application.getMainFrame().getMenuBar().getMenuCount() - 2);
    
  }
  

  @Override
  public void actionPerformed(ActionEvent e) {
    LOGGER.debug("Start");
    
    try {
      
      this.application.getMainFrame().removeAllProjectFrames();
      
      URL paysURL = new URL("file", "", "./data/ne_admin_0_countries/ne_50m_admin_0_countries.shp");
      IPopulation<IFeature> paysPop = ShapefileReader.read(paysURL.getPath());
      
      double saut = 45.0;
      double sautCourant = 45.0;
      while (sautCourant <= 180.0) {
        System.out.println("Saut = " + sautCourant);
        
        Population<DefaultFeature> popTranslation = new Population<DefaultFeature>();
        popTranslation.setFeatureType(paysPop.getFeatureType());
        
        for (IFeature feature : paysPop.getElements()) {
          String continent = feature.getAttribute("continent").toString().trim();
          if (continent.equals("North America") || continent.equals("Europe")) {
            ArrayList<DefaultFeature> listATraiter = splitMultiPolygonToSinglePolygon(feature);
            for (DefaultFeature feat : listATraiter) {
              List<DefaultFeature> listNouveauFeature = getFeatureTranslate(feat, saut);
              if (listNouveauFeature != null && listNouveauFeature.size() > 0) {
                for (int j = 0; j < listNouveauFeature.size(); j++) {
                  popTranslation.add(listNouveauFeature.get(j));
                }
              }
            }
          
          }
        }
        
        ProjectFrame projectFrame = application.getMainFrame().newProjectFrame();
        Layer layerEntite = projectFrame.addUserLayer(popTranslation, popTranslation.getNom() + "-" + sautCourant, null);
        Float size = 2f;
        ((PolygonSymbolizer) layerEntite.getSymbolizer()).getStroke().setStrokeWidth(size);
        Color darkGreen = new Color(0, 148, 255);
        Color lightGreen = new Color(127, 201, 255);
        ((PolygonSymbolizer) layerEntite.getSymbolizer()).getFill().setColor(lightGreen);
        ((PolygonSymbolizer) layerEntite.getSymbolizer()).getStroke().setStroke(darkGreen);
        layerEntite.getSymbolizer().setUnitOfMeasurePixel();
        
        
        sautCourant = sautCourant + saut;
      }
      
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    
    // Organize
    try {
      this.application.getMainFrame().organizeCurrentDesktop();
      for (int i = 0; i < this.application.getMainFrame().getDesktopProjectFrames().length; i++) {
        ProjectFrame frame = this.application.getMainFrame().getDesktopProjectFrames()[i];
        frame.getLayerLegendPanel().getLayerViewPanel().getViewport().zoomToFullExtent();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    
    System.out.println("----");
    
  }
  
  private static final double X_MAX_WGS84 = 180;
  private static final double Y_MAX_WGS84 = 90;
  
  @SuppressWarnings("unchecked")
  private List<DefaultFeature> getFeatureTranslate(DefaultFeature ancienFeature, double saut) {
    
    List<DefaultFeature> listFeature = new ArrayList<DefaultFeature>();
    
    // On caste la géométrie retournée en polygone pour pouvoir traiter correctement les polygones qui comportent des trous
    GM_Polygon ancienPolygon = (GM_Polygon) ancienFeature.getGeom();
    
    // On instancie un polygone qui contiendra le polygone translate (peut dépasser 180)
    GM_Polygon polygoneTranslate = getGeomTranslate(ancienPolygon, saut, false);
    
    // On récupère l'enveloppe du polygone translate  
    IEnvelope envelopeTranslate = polygoneTranslate.getEnvelope();
    
    double xMin = envelopeTranslate.getLowerCorner().getX();
    double xMax = envelopeTranslate.getUpperCorner().getX();
    if (xMax < X_MAX_WGS84) { // le polygone translaté tout entier reste à gauche de la limite de l'emprise
      // On crée un nouveau Feature qui contiendra le nouveau polygone créé
      DefaultFeature nouveauFeature = new DefaultFeature();
      nouveauFeature.setFeatureType(ancienFeature.getFeatureType());
      // On ajoute le polygone au nouveau Feature, puis ce dernier à la nouvelle Population
      nouveauFeature.setGeom(polygoneTranslate);
      listFeature.add(nouveauFeature);
      
    } else if (xMin > X_MAX_WGS84) {// le polygone translaté tout entier passe à droite de la limite de l'emprise
      
      // On translate le polygone de saut
      GM_Polygon polygoneATranslate = getGeomTranslate(ancienPolygon, saut, true);
      // On crée un nouveau Feature qui contiendra le nouveau polygone créé
      DefaultFeature nouveauFeature = new DefaultFeature();
      nouveauFeature.setFeatureType(ancienFeature.getFeatureType());
      // On ajoute le polygone au nouveau Feature, puis ce dernier à la nouvelle Population
      nouveauFeature.setGeom(polygoneATranslate);
      listFeature.add(nouveauFeature);
    
    } else {//le polygone translaté intersecte la limite du planisphère
      
      IEnvelope envelopeGauche = new GM_Envelope(-X_MAX_WGS84, X_MAX_WGS84, -Y_MAX_WGS84, Y_MAX_WGS84);
      IEnvelope envelopeDroite = new GM_Envelope(X_MAX_WGS84, X_MAX_WGS84 + saut, -Y_MAX_WGS84, Y_MAX_WGS84);
      
      IGeometry geomGauche = envelopeGauche.getGeom().intersection(polygoneTranslate);
      if (geomGauche == null) {
      } else if(geomGauche.isLineString()) {
      } else if(geomGauche.isMultiCurve()) {
      } else if(geomGauche.isPolygon()) {
        // On crée un nouveau Feature qui contiendra le nouveau polygone créé
        DefaultFeature nouveauFeature = new DefaultFeature();
        nouveauFeature.setFeatureType(ancienFeature.getFeatureType());
        // On ajoute le polygone au nouveau Feature, puis ce dernier à la nouvelle Population
        nouveauFeature.setGeom(geomGauche);
        listFeature.add(nouveauFeature);
      } else if (geomGauche.isMultiSurface()) {
        GM_MultiSurface<GM_Polygon> multiSurface = (GM_MultiSurface<GM_Polygon>) geomGauche;
        for (int i = 0; i < multiSurface.getList().size(); i++) {
          // On translate le polygone de saut
          // GM_Polygon polygoneGaucheATranslate = GeometryUtil.getGeomTranslate(multiSurface.getList().get(i), saut, false);
          GM_Polygon polygoneGaucheATranslate = multiSurface.getList().get(i);
          // On crée un nouveau Feature qui contiendra le nouveau polygone créé
          DefaultFeature nouveauFeature = new DefaultFeature();
          nouveauFeature.setFeatureType(ancienFeature.getFeatureType());
          // On ajoute le polygone au nouveau Feature, puis ce dernier à la nouvelle Population
          nouveauFeature.setGeom(polygoneGaucheATranslate);
          listFeature.add(nouveauFeature);
        }
      } /*else {
        DefaultFeature featureGauche = new DefaultFeature();
        featureGauche.setFeatureType(ancienFeature.getFeatureType());
        featureGauche.setGeom(geomGauche);
        listFeature.add(featureGauche);
      }*/
      
      IGeometry geomDroite = envelopeDroite.getGeom().intersection(polygoneTranslate);
      if (geomDroite == null) {
        
      } else if(geomDroite.isLineString()) {
      
      } else if(geomDroite.isMultiCurve()) {
      } else if (geomDroite.isPolygon()) {
        DefaultFeature featureDroite = new DefaultFeature();
        featureDroite.setFeatureType(ancienFeature.getFeatureType());
        GM_Polygon polygone360 = getGeomTranslate((GM_Polygon)geomDroite, 0.0, true);
        featureDroite.setGeom(polygone360);
        listFeature.add(featureDroite);
      } else if (geomDroite.isMultiSurface()) {
        GM_MultiSurface<GM_Polygon> multiSurface = (GM_MultiSurface<GM_Polygon>) geomDroite;
        for (int i = 0; i < multiSurface.getList().size(); i++) {
          // On translate le polygone de saut + retour à gauche
          GM_Polygon polygoneDroiteATranslate = getGeomTranslate(multiSurface.getList().get(i), 0.0, true);
          // On crée un nouveau Feature qui contiendra le nouveau polygone créé
          DefaultFeature nouveauFeature = new DefaultFeature();
          nouveauFeature.setFeatureType(ancienFeature.getFeatureType());
          // On ajoute le polygone au nouveau Feature, puis ce dernier à la nouvelle Population
          nouveauFeature.setGeom(polygoneDroiteATranslate);
          listFeature.add(nouveauFeature);
        }
      } /*else {
        DefaultFeature featureDroite = new DefaultFeature();
        featureDroite.setFeatureType(ancienFeature.getFeatureType());
        featureDroite.setGeom(geomDroite);
        listFeature.add(featureDroite);
      }*/
      
    }
    
    return listFeature;
  }
  
  private GM_Polygon getGeomTranslate(GM_Polygon geom, double saut, boolean modulo) {
    
    GM_Polygon polygoneATranslate = new GM_Polygon();
    
    // On récupère les coordonnées des trous du polygone courant dans une liste
    List<IRing> listeRingsATranslate = geom.getInterior();

    // On récupère les coordonnées extérieures du polygone courant et on l'ajoute à la fin de la liste (afin de ne pas travailler directement sur l'ancien polygone)
    IRing coordsExtATranslate = geom.getExterior();
    listeRingsATranslate.add(coordsExtATranslate);

    // On parcourt toutes les Ring de notre liste
    for (int k = 0; k < listeRingsATranslate.size(); k++) {

        // On récupère les coordonnées du Ring courant
        IDirectPositionList coordsRing = listeRingsATranslate.get(k).coord();
  
        // On crée une liste qui contiendra les nouvelles coordonnées du Ring courant
        List<IDirectPosition> listeCoordsRingTranslate = new ArrayList<IDirectPosition>();
  
        // On parcourt les coordonnées du Ring courant
        for (IDirectPosition coordRingCourant : coordsRing.getList()) {
  
          // On translate l'ancienne coordonnée X de saut
          double ancienneLongitude = coordRingCourant.getX();
          double nouvelleLongitude = ancienneLongitude + saut;
          if (modulo) {
            // double diff = nouvelleLongitude - 180.0;
            // nouvelleLongitude = -180 + diff;
            nouvelleLongitude = nouvelleLongitude - 360.0;
          }
          
          // On ajoute la nouvelle paire de coordonnées à la liste de coordonnées
          IDirectPosition coordNouvelle = new DirectPosition(nouvelleLongitude, coordRingCourant.getY());
          listeCoordsRingTranslate.add(coordNouvelle);
        }
  
        // On crée le Ring translaté à partir de la liste de nouvelles coordonnées
        IRing ringTranslate = new GM_Ring(new GM_LineString(listeCoordsRingTranslate));
  
        // Si le Ring courant est le dernier de la liste, alors il s'agit du contour extérieur du polygone ; sinon, il s'agit d'un trou
        if (k == (listeRingsATranslate.size() - 1)) {
          polygoneATranslate.setExterior(ringTranslate);
        } else {
          polygoneATranslate.addInterior(k, ringTranslate);
        }
    
    }
    
    return polygoneATranslate;
    
  }
  
  
  @SuppressWarnings("unchecked")
  private static ArrayList<DefaultFeature> splitMultiPolygonToSinglePolygon(IFeature feature) {
    
    ArrayList<DefaultFeature> listeFeatures = new ArrayList<DefaultFeature>();
    
    GM_MultiSurface<GM_Polygon> multiSurface = (GM_MultiSurface<GM_Polygon>) feature.getGeom();
    
    LOGGER.trace("On split la multi surface en " + multiSurface.getList().size() + " polygones.");
    
    // On parcourt tous les polygones
    for (int j = 0; j < multiSurface.getList().size(); j++) {
      
      // On crée un nouveau Feature qui contiendra le nouveau polygone créé
      DefaultFeature nouveauFeature = new DefaultFeature();
      nouveauFeature.setFeatureType(new FeatureType());
      nouveauFeature.setGeom((GM_Polygon) multiSurface.getList().get(j));
      ///
      listeFeatures.add(nouveauFeature);
    }
    
    return listeFeatures;
  }

}
