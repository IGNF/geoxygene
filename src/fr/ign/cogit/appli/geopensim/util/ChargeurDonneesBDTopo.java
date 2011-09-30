/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 *
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 *
 * See: http://oxygene-project.sourceforge.net
 *
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/
/**
 *
 */
package fr.ign.cogit.appli.geopensim.util;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicBatiment;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicRepresentationFactory;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicTronconChemin;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicTronconCoursEau;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicTronconRoute;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicTronconVoieFerree;
import fr.ign.cogit.appli.geopensim.feature.macro.PopulationUnites;
import fr.ign.cogit.appli.geopensim.feature.meso.UniteUrbaine;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.SchemaConceptuelJeu;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.JtsUtil;



/**
 * Chargement des données BdTopo v1.1 et v1.2.
 * @author Julien Perret
 *
 */
public class ChargeurDonneesBDTopo {
	static Logger logger=Logger.getLogger(ChargeurDonneesBDTopo.class.getName());

	static int idRep = 1;
	static int idGeo = 1;
	static GeometryFactory factory = new GeometryFactory();

	static boolean computeIntersection=false;

	/*
		static Polygon envelope =  factory.createPolygon(factory.createLinearRing(new Coordinate[] {
			new Coordinate(847000,145000),
			new Coordinate(847000,137000),
			new Coordinate(857000,137000),
			new Coordinate(857000,145000),
			new Coordinate(847000,145000)}),null);
	*/

	//Strasbourg
	/*
	static Polygon envelope =  factory.createPolygon(factory.createLinearRing(new Coordinate[] {
			new Coordinate(992000,119000),
			new Coordinate(992000,110000),
			new Coordinate(1003000,110000),
			new Coordinate(1003000,119000),
			new Coordinate(992000,119000)}),null);
	*/
	/*
	static Polygon envelope =  factory.createPolygon(factory.createLinearRing(new Coordinate[] {
			new Coordinate(990000,140000),
			new Coordinate(990000,115000),
			new Coordinate(995000,115000),
			new Coordinate(995000,140000),
			new Coordinate(990000,140000)}),null);
	*/
	/*
	static Polygon envelope =  factory.createPolygon(factory.createLinearRing(new Coordinate[] {
			new Coordinate(995000,119500),
			new Coordinate(995000,118000),
			new Coordinate(998000,118000),
			new Coordinate(998000,119500),
			new Coordinate(995000,119500)}),null);
	*/
	/*
	static Polygon envelope =  factory.createPolygon(factory.createLinearRing(new Coordinate[] {
			new Coordinate(995000,121000),
			new Coordinate(995000,118000),
			new Coordinate(1000700,118000),
			new Coordinate(1000700,121000),
			new Coordinate(995000,121000)}),null);
 	*/
	//Orléans
	/*
	static Polygon envelope =  factory.createPolygon(factory.createLinearRing(new Coordinate[] {
			new Coordinate(560000,2330000),
			new Coordinate(560000,2310000),
			new Coordinate(578000,2310000),
			new Coordinate(578000,2330000),
			new Coordinate(560000,2330000)}),null);
	*/
	/*
	static Polygon envelope =  factory.createPolygon(factory.createLinearRing(new Coordinate[] {
			new Coordinate(560000,2330000),
			new Coordinate(560000,2317000),
			new Coordinate(573000,2317000),
			new Coordinate(573000,2330000),
			new Coordinate(560000,2330000)}),null);
	*/
	/*
	static Polygon envelope =  factory.createPolygon(factory.createLinearRing(new Coordinate[] {
			new Coordinate(566000,2324000),
			new Coordinate(566000,2317000),
			new Coordinate(573000,2317000),
			new Coordinate(573000,2324000),
			new Coordinate(566000,2324000)}),null);
	*/
//	static Polygon envelope =  factory.createPolygon(factory.createLinearRing(new Coordinate[] {
//			new Coordinate(566000,2330000),
//			new Coordinate(566000,2320000),
//			new Coordinate(573000,2320000),
//			new Coordinate(573000,2330000),
//			new Coordinate(566000,2330000)}),null);
	/*
	static Polygon envelope =  factory.createPolygon(factory.createLinearRing(new Coordinate[] {
		new Coordinate(536927,2292332),
		new Coordinate(536927,2348243),
		new Coordinate(597952,2348243),
		new Coordinate(597952,2292332),
		new Coordinate(536927,2292332)}),null);
	//Extraction Orléans pour Annabelle (30/06/2009)
	static Polygon envelope =  factory.createPolygon(factory.createLinearRing(new Coordinate[] {
		new Coordinate(557472.8, 2312730.3),
		new Coordinate(557472.8, 2330318.2),
		new Coordinate(575553, 2330318.2),
		new Coordinate(575553, 2312730.3),
		new Coordinate(557472.8, 2312730.3)}),null);
*/
//	static Polygon envelope =  factory.createPolygon(factory.createLinearRing(new Coordinate[] {
//			new Coordinate(995000,119500),
//			new Coordinate(995000,118000),
//			new Coordinate(998000,118000),
//			new Coordinate(998000,119500),
//			new Coordinate(995000,119500)}),null);

	/*
	 *
	 */
	public static void charge() {
		GUIChargeurDonneesGeOpenSim chargeur = new GUIChargeurDonneesGeOpenSim("Chargeur de données BD Topo",true);
		boolean validated = chargeur.showDialog();
		if (validated) {
			DataSet.db = GeodatabaseOjbFactory.newInstance();
			if (logger.isDebugEnabled()) logger.debug("Nettoyage de la base de données");
			DataSet.db.begin();
			// TODO c'est mal mais ça marche bien :)
			DataSet.db.exeSQLFile("sql/postgis/geopensim/nettoyer_base_bdtopo.sql");
			DataSet.db.commit();
			DataSet.db.begin();
			DataSet.db.clearCache();
			DataSet.db.commit();
			List<File> shapeFiles = chargeur.shapeFiles;
			List<Integer> shapeFilesClasses = chargeur.getShapeFilesClasses();
			String[] javaClassStrings = chargeur.getJavaClassStrings();
			Iterator<File> it_file = shapeFiles.iterator();
			Iterator<Integer> it_javaClass = shapeFilesClasses.iterator();
			PopulationUnites popVilles = new PopulationUnites(BasicBatiment.class,BasicTronconRoute.class, BasicTronconChemin.class, BasicTronconVoieFerree.class, BasicTronconCoursEau.class,chargeur.getDateValue());
			while (it_file.hasNext()&&it_javaClass.hasNext()) {
				File file=it_file.next();
				int javaClassIndex = it_javaClass.next();
				String javaClassName = javaClassStrings[javaClassIndex];
				if (logger.isDebugEnabled()) logger.debug("Chargement du fichier "+file);
				if (javaClassIndex==0) {
					logger.warn("Fichier "+file+" non traité : pas de classe correspondante");
					continue;
				}
				if (logger.isDebugEnabled()) logger.debug(" --- classe java correspondante "+javaClassName);
				charge(file.getAbsolutePath(),javaClassName,popVilles,chargeur.getDateValue());
				System.gc();
			}
			JFrame frame = new JFrame();
			frame.setTitle("Construction des villes");
			Container p = frame.getContentPane();
			p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));

			final JProgressBar progressBar = new JProgressBar();
			final JProgressBar progressBarIlots = new JProgressBar();
			progressBar.setStringPainted(true);
			progressBarIlots.setStringPainted(true);
			progressBar.setString("...");
			progressBarIlots.setString("...");
			final JLabel labelGeneral = new JLabel("Processus Général");
			final JLabel labelDetail = new JLabel("Processus détaillé");
			p.add(labelGeneral);
			p.add(progressBar);
			p.add(labelDetail);
			p.add(progressBarIlots);
			ActionListener progressBarActionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					switch(e.getID()) {
					case 0:
						progressBar.setMaximum(e.getModifiers());
						progressBar.setString("0 / "+e.getModifiers());
						labelGeneral.setText(e.getActionCommand());
						break;
					case 1:
						progressBar.setValue(e.getModifiers());
						progressBar.setString(e.getModifiers()+" / "+progressBar.getMaximum());
						break;
					case 2:
						progressBarIlots.setMaximum(e.getModifiers());
						progressBarIlots.setString("0 / "+e.getModifiers());
						labelDetail.setText(e.getActionCommand());
						break;
					case 3:
						progressBarIlots.setValue(e.getModifiers());
						progressBarIlots.setString(e.getModifiers()+" / "+progressBarIlots.getMaximum());
						break;
					default:
						progressBar.setMaximum(0);
						progressBar.setValue(0);
						progressBar.setString("");
						progressBarIlots.setMaximum(0);
						progressBarIlots.setValue(0);
						progressBarIlots.setString("");
						labelGeneral.setText("Processus Général");
						labelDetail.setText("Processus détaillé");
					}
				}
			};
			popVilles.addActionListener(progressBarActionListener);
			JtsUtil.addActionListener(progressBarActionListener);
			JtsAlgorithms.addActionListener(progressBarActionListener);
			frame.pack();
			frame.setVisible(true);
			if (logger.isDebugEnabled()) logger.debug("Création des villes");
			popVilles.setExtraction(chargeur.isExtraction());
			if (chargeur.isExtraction()) logger.info("Les données font partie d'un extraction");
			popVilles.construireUnites();
			if (logger.isDebugEnabled()) logger.debug("Qualification des villes");
			popVilles.qualifier();
			popVilles.sauverPopulations();
			if (logger.isDebugEnabled()) logger.debug(popVilles);
			frame.dispose();
			logger.info("Chargement terminé");
			
		}
		else logger.info("Chargement annulé");
		
	}

	/**
	 * Chargement de représentations Géographiques à partir d'un fichier shapefile à l'interieur d'une collection d'éléments Géographiques.
	 * @param fileName nom du fichier à charger
	 * @param javaClassName nom de la classe Java utilisée pour le chargement des objets géopgraphiques
	 * @param popVilles collection contenant les éléments Géographiques déjà chargés et dans laquelle ajouter les nouveaux.
	 */
	private static void charge(String fileName, String javaClassName, PopulationUnites popVilles,int date) {
	  IPopulation<IFeature> pop = fr.ign.cogit.geoxygene.util.conversion.ShapefileReader.read(fileName);
//		DriverProperties dp = new DriverProperties(fileName);
//		ShapefileReader reader = new ShapefileReader();
//		FeatureCollection featureCollection = null;
//		try {
//			featureCollection = reader.read(dp);
//			logger.info(featureCollection.size()+" objets chargés");
//		} catch (IllegalParametersException e) {
//			e.printStackTrace();
//			return;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
		Hashtable<String, String> mapping= new Hashtable<String, String>(0);
		mapping.put("HAUTEUR", "Hauteur");
		mapping.put("SOURCE", "Source");
		mapping.put("NATURE", "Nature");
		mapping.put("TYPE", "Nature");
		Hashtable<String, Class<?>> mappingType= new Hashtable<String, Class<?>>(0);
		mappingType.put("HAUTEUR", int.class);
		mappingType.put("SOURCE", String.class);
		mappingType.put("NATURE", String.class);
		mappingType.put("TYPE", String.class);

		List<Integer> attributeIndices = new ArrayList<Integer>(0);
		List<String> attributeNames = new ArrayList<String>(0);
        SchemaConceptuelJeu schema = pop.getFeatureType().getSchema();
		for (int index = 0 ; index < schema.getFeatureAttributes().size() ; index++){
			if (logger.isTraceEnabled()) logger.trace("Attribute "+index+" name = "+schema.getFeatureAttributes().get(index).getMemberName()+ " type = "+schema.getFeatureAttributes().get(index).getValueType());
			if (mapping.containsKey(schema.getFeatureAttributes().get(index).getMemberName())) {
				attributeIndices.add(index);
				attributeNames.add(mapping.get(schema.getFeatureAttributes().get(index).getMemberName()));
				if (logger.isTraceEnabled()) logger.trace(" --- attribute ajouté "+mapping.get(schema.getFeatureAttributes().get(index).getMemberName())+ " de type "+mappingType.get(schema.getFeatureAttributes().get(index).getMemberName()));
			}
		}

		List<ElementRepresentation> listeRepresentations = new ArrayList<ElementRepresentation>();
		Iterator<IFeature> iter = pop.iterator();
		while(iter.hasNext()) {
		  DefaultFeature feature = (DefaultFeature) iter.next();
			BasicRepresentationFactory representationFactory = new BasicRepresentationFactory();
			if (javaClassName.equalsIgnoreCase("SurfaceRoute")) {
				String nature = (String) feature.getAttribute("NATURE");
				if (nature.equalsIgnoreCase("parking")) javaClassName="Parking";
				else if (nature.equalsIgnoreCase("carrefour")) javaClassName="Carrefour";
				else {
					logger.error("La nature "+nature+" ne correspond à aucun type");
					continue;
				}
			}
			ElementRepresentation ft_feature = representationFactory.creerElementRepresentation(javaClassName);
			if (ft_feature==null) {
				logger.error("Classe "+javaClassName+" non trouvée");
				continue;
			}
			Class<? extends ElementRepresentation> classe = ft_feature.getClass();
			ft_feature.setIdRep(idRep++);
			for (int i = 0 ; i < attributeIndices.size() ; i++) {
				Class<?> type = mappingType.get(schema.getFeatureAttributes().get(attributeIndices.get(i)).getMemberName());
				Method methode;
				try {
					methode = classe.getMethod("set"+attributeNames.get(i), type);
					if (type.equals(int.class)) {
						methode.invoke(ft_feature, Integer.parseInt((String) feature.getAttribute(attributeIndices.get(i))));
					} else if (type.equals(String.class)) {
						methode.invoke(ft_feature, (String) feature.getAttribute(attributeIndices.get(i)));
					}
				} catch (SecurityException e) {
					logger.warn("La Méthode \"set"+attributeNames.get(i)+"\" n'est pas accessible sur la classe "+classe);
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					logger.warn("La Méthode \"set"+attributeNames.get(i)+"\" n'existe pas sur la classe "+classe);
					e.printStackTrace();
				} catch (NumberFormatException e) {
					logger.warn("\""+(String) feature.getAttribute(attributeIndices.get(i))+"\" n'est pas convertible en entier");
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					logger.warn("Les paramètres founis à la Méthode \"set"+attributeNames.get(i)+"\" ne sont pas du bon type sur la classe "+classe);
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// ne devrait pas être levée : la Méthode getMethod devrait lever une exception SecurityException
					logger.warn("La Méthode \"set"+attributeNames.get(i)+"\" n'est pas accessible sur la classe "+classe);
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					logger.warn("La Méthode \"set"+attributeNames.get(i)+"\" a renvoyé une exception sur la classe "+classe);
					e.printStackTrace();
				}
			}
			try {ft_feature.setGeom(feature.getGeom());}
			catch (Exception e) {
				logger.error("La transformation de la géométrie de l'objet en GM_Object a échouée.");
				logger.error("Aucune géométrie n'a été affectée à l'objet.");
				e.printStackTrace();
			}
			(ft_feature).setDateSourceSaisie(date);
			(ft_feature).qualifier();
			listeRepresentations.add(ft_feature);
		}
		logger.info("Ajout de "+listeRepresentations.size()+" représentations de type "+javaClassName);
		if (javaClassName.equalsIgnoreCase("batiment")) {
			popVilles.addBatiments(listeRepresentations);
		} else if (javaClassName.equalsIgnoreCase("TronconRoute")) {
			popVilles.addTronconsRoute(listeRepresentations);
		} else if (javaClassName.equalsIgnoreCase("TronconChemin")) {
			popVilles.addTronconsChemin(listeRepresentations);
		} else if (javaClassName.equalsIgnoreCase("TronconCoursEau")) {
			popVilles.addTronconsCoursEau(listeRepresentations);
		} else if (javaClassName.equalsIgnoreCase("TronconVoieFerree")) {
			popVilles.addTronconsVoieFerree(listeRepresentations);
		} else {
			DataSet.db.begin();
			for (ElementRepresentation rep:listeRepresentations) DataSet.db.makePersistent(rep);
			DataSet.db.commit();
		}
	}

	public static void construireZonesElementaires() {
		JFrame frame = new JFrame();
		frame.setTitle("Construction des villes");
		Container p = frame.getContentPane();
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));

		final JProgressBar progressBar = new JProgressBar();
		final JProgressBar progressBarIlots = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBarIlots.setStringPainted(true);
		progressBar.setString("...");
		progressBarIlots.setString("...");
		final JLabel labelGeneral = new JLabel("Processus général");
		final JLabel labelDetail = new JLabel("Processus détaillé");
		p.add(labelGeneral);
		p.add(progressBar);
		p.add(labelDetail);
		p.add(progressBarIlots);
		ActionListener progressBarActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(e.getID()) {
				case 0:
					progressBar.setMaximum(e.getModifiers());
					progressBar.setString("0 / "+e.getModifiers());
					labelGeneral.setText(e.getActionCommand());
					break;
				case 1:
					progressBar.setValue(e.getModifiers());
					progressBar.setString(e.getModifiers()+" / "+progressBar.getMaximum());
					break;
				case 2:
					progressBarIlots.setMaximum(e.getModifiers());
					progressBarIlots.setString("0 / "+e.getModifiers());
					labelDetail.setText(e.getActionCommand());
					break;
				case 3:
					progressBarIlots.setValue(e.getModifiers());
					progressBarIlots.setString(e.getModifiers()+" / "+progressBarIlots.getMaximum());
					break;
				default:
					progressBar.setMaximum(0);
					progressBar.setValue(0);
					progressBar.setString("");
					progressBarIlots.setMaximum(0);
					progressBarIlots.setValue(0);
					progressBarIlots.setString("");
					labelGeneral.setText("Processus général");
					labelDetail.setText("Processus détaillé");
				}
			}
		};
		JtsUtil.addActionListener(progressBarActionListener);
		frame.pack();
		frame.setVisible(true);
		DataSet.db = GeodatabaseOjbFactory.newInstance();
		PopulationUnites popVilles = new PopulationUnites(BasicBatiment.class,BasicTronconRoute.class, BasicTronconChemin.class, BasicTronconVoieFerree.class, BasicTronconCoursEau.class, 1989);
		popVilles.addActionListener(progressBarActionListener);
		if (logger.isDebugEnabled()) logger.debug("Chargement des villes");
		popVilles.chargerElements();
		if (popVilles.getUnitePeriUrbaine().getGeom()==null) popVilles.construireUnitePeriUrbaine();
		if (logger.isDebugEnabled()) logger.debug("ConstruireZonesElementaires");
		popVilles.construireZonesElementaires();
//		if (logger.isDebugEnabled()) logger.debug("ConstruireGroupesBatiments");
//		popVilles.construireGroupesBatiments();
		if (logger.isDebugEnabled()) logger.debug("ConstruireCarrefours");
		popVilles.construireCarrefours();
		if (logger.isDebugEnabled()) logger.debug("Qualification des villes");
		popVilles.qualifier();
		if (logger.isDebugEnabled()) logger.debug("Sauvegarde des villes");
		popVilles.sauverPopulations();
		if (logger.isDebugEnabled()) logger.debug(popVilles);
		frame.dispose();
	}

	public static void requalifierUnites() {
		DataSet.db = GeodatabaseOjbFactory.newInstance();
		DataSet.db.begin();
		IFeatureCollection<UniteUrbaine> collection = DataSet.db.loadAllFeatures(UniteUrbaine.class);
		for (UniteUrbaine unite:collection) {unite.qualifier();}
		DataSet.db.commit();
	}

	/*
	public static Class<?> toRepresentationClass(String name) {
		if (name.equalsIgnoreCase("Batiment")) return Batiment.class;
		if (name.equalsIgnoreCase("Cimetiere")) return Cimetiere.class;
		if (name.equalsIgnoreCase("Parking")) return Parking.class;
		if (name.equalsIgnoreCase("SurfaceEau")) return SurfaceEau.class;
		if (name.equalsIgnoreCase("SurfaceRoute")) return SurfaceRoute.class;
		if (name.equalsIgnoreCase("TerrainSport")) return TerrainSport.class;
		if (name.equalsIgnoreCase("TronconChemin")) return TronconChemin.class;
		if (name.equalsIgnoreCase("TronconCoursEau")) return TronconCoursEau.class;
		if (name.equalsIgnoreCase("TronconRoute")) return TronconRoute.class;
		if (name.equalsIgnoreCase("TronconVoieFerree")) return TronconVoieFerree.class;
		if (name.equalsIgnoreCase("Vegetation")) return Vegetation.class;
		return null;
	}
	 */
	/**
	 * Lancement de l'interface de chargement des données du LIV.
	 * @param args pas d'arguments utilisé
	 */
	public static void main(String[] args) {
		charge();
		System.exit(0);
	}

}
