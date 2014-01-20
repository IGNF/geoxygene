package fr.ign.cogit.tests;

import java.awt.Color;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartographie.Emprise;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.util.TestOs;

/**
 * Centrer facilement une carte sur une longitude particulière.
 * Très bon exercice pour manipuler les géométries avec les DirectPosition
 * 
 * @author GBrun
 */
public class TestChangementProjectionShape {

	public static void main(String[] args) {
		
		// Définition du ShapeFile contenant les limites des pays du monde
		File dossierShapes = new File("shapes");
		String pathShape = "";
		
		if(TestOs.testerOs()){
			pathShape = dossierShapes.getAbsolutePath()+"/ne_10m_admin_0_countries/ne_10m_admin_0_countries.shp";
			System.out.println("Nom du fichier Shape contenant les limites des pays du monde : "+pathShape);
		}else{
			pathShape = dossierShapes.getAbsolutePath()+"\\ne_10m_admin_0_countries\\ne_10m_admin_0_countries.shp";
			System.out.println("Nom du fichier Shape contenant les limites des pays du monde : "+pathShape);
		}
		
		// On stocke le ShapeFile dans une FeatureCollection, puis dans
		IFeatureCollection<IFeature> featCollInitiale = ShapefileReader.read(pathShape);
		
		// On va stocker le Feature sélectionner dans une nouvelle Population
		Population<DefaultFeature> popSelectionnee = new Population<DefaultFeature>(false, "entrees", DefaultFeature.class, true);
		
		// On recherche le code ISO sur 2 chiffres du pays dans le ShapeFile
		for (IFeature feat : featCollInitiale) {
			
			// On sélectionne uniquement le polygone des Etats-unis
			if(feat.getAttribute("iso_a2").equals("US")){
				
				// On stocke le Feature sélectionné dans une nouvelle Population
				popSelectionnee.setFeatureType(featCollInitiale.getFeatureType());
				popSelectionnee.add((DefaultFeature) feat);
			}
			
		}		

		System.out.println("\n");
		
		// On crée une nouvelle population qui sera peuplée par les Feature qui auront été translatés
		Population<IFeature> popTranslation = new Population<IFeature>();
		popTranslation.setFeatureType(popSelectionnee.getFeatureType());
				
		// On parcourt la population initiale (il n'y en a qu'une seule en fait)
		for (int i = 0; i < popSelectionnee.size(); i++) {
			
			// On caste le Feature de la population (qui est un MultiPolygon en plusieurs polygones simples
			DefaultFeature ancienFeature = popSelectionnee.get(i);
			GM_MultiSurface multiSurface = (GM_MultiSurface) ancienFeature.getGeom();
						
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
										
					// Si l'ancienne longitude est supérieure à la limite Est du WGS84, 
					//    la nouvelle longitude est replacée toute à l'ouest
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
		
		// Nouvelle application GeOxygene
		GeOxygeneApplication application = new GeOxygeneApplication();
		ProjectFrame projectFrame = application.getMainFrame().newProjectFrame();
		
		// Affiche les limites du WGS84 (module InfoTexte)
		Emprise limitesWGS84 = new Emprise(90, 180, -90, -180);
		Layer layerLimites = projectFrame.addUserLayer(limitesWGS84.getPopulation(), "Limites du WGS84", null);
			
		// Création des couches contenant les Etats-Unis avec les coordonnées normales et translatées
		Layer layerInitial = projectFrame.addUserLayer(popSelectionnee, "Etats-Unis avant translation", null);
		Layer layerTranslate = projectFrame.addUserLayer(popTranslation, "Etats-Unis après translation", null);
				
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
		
		Color darkGreen = new Color(0, 80, 0);
		Color lightGreen = new Color(0, 255, 0);
		
		layerInitial.getSymbolizer().getStroke().setColor(darkRed);
		((PolygonSymbolizer)layerInitial.getSymbolizer()).getFill().setColor(lightRed);
		
		layerTranslate.getSymbolizer().getStroke().setColor(darkBlue);
		((PolygonSymbolizer)layerTranslate.getSymbolizer()).getFill().setColor(lightBlue);
		
		layerLimites.getSymbolizer().getStroke().setColor(darkGreen);
		((PolygonSymbolizer)layerLimites.getSymbolizer()).getFill().setColor(lightGreen);
				
		// On zoom sur l'étendue maximale (limites du WGS84)
		try {
			projectFrame.getLayerViewPanel().getViewport().zoomToFullExtent();
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
