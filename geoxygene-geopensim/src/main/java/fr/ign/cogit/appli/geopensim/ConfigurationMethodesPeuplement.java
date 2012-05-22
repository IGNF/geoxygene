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

package fr.ign.cogit.appli.geopensim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fr.ign.cogit.appli.geopensim.algo.TypeDistribution;
import fr.ign.cogit.appli.geopensim.feature.micro.FormeBatiment;
import fr.ign.cogit.appli.geopensim.feature.micro.TypeFonctionnel;
import fr.ign.cogit.appli.geopensim.ConfigurationFormesBatimentsV2.ParametresForme;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.ParametresMethodesPeuplement;



/**
 * @author Florence Curie
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ConfigurationMethodesPeuplement")
public class ConfigurationMethodesPeuplement {

	private static ConfigurationMethodesPeuplement config;
	private static String cheminFichierConfigurationMethodesPeuplement="src/resources/geopensim/ConfigurationMethodesPeuplement.xml";
	
	@XmlJavaTypeAdapter(MyHashMapAdapterMP.class)
	@XmlElement(name="MethodesPeuplement")
	private HashMap<String,ParametresMethodesPeuplement> listType = new HashMap<String,ParametresMethodesPeuplement>();

	/**
	 * @return listType la liste des Méthodes de peuplement
	 */
	public HashMap<String, ParametresMethodesPeuplement> getListType() {return this.listType;}

	/**
	 * @param la liste des Méthodes de peuplement
	 */
	public void setListType(HashMap<String, ParametresMethodesPeuplement> listType) {this.listType = listType;}

	// Constructeur vide indispensable
	public ConfigurationMethodesPeuplement(){}

	// déserialisation du fichier xml
	public static ConfigurationMethodesPeuplement getInstance() {
		try {
			JAXBContext context = JAXBContext.newInstance(ConfigurationMethodesPeuplement.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			config = (ConfigurationMethodesPeuplement) unmarshaller.unmarshal(new FileInputStream(new File(cheminFichierConfigurationMethodesPeuplement)));
			return config;
		}catch (JAXBException e) {e.printStackTrace(); 
		}catch (FileNotFoundException e) {e.printStackTrace();}
		return null;
	}
	
	public static ConfigurationMethodesPeuplement getInstance(String nomChemin) {
		cheminFichierConfigurationMethodesPeuplement = nomChemin;
		return getInstance();
	}

	// sérialisation des objets java
	public void marshall() {
		try {
			JAXBContext context = JAXBContext.newInstance(ConfigurationMethodesPeuplement.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			File fichier = new File(cheminFichierConfigurationMethodesPeuplement);
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(fichier);
				marshaller.marshal(this, fos);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (JAXBException e) {e.printStackTrace();}		
	}
	
	public void marshall(String nomChemin) {
		cheminFichierConfigurationMethodesPeuplement = nomChemin;
		marshall();
	}
	
	// Récupération des paramètres du type fonctionel
	public ParametresMethodesPeuplement getParametresMethodesPeuplement (String nom){
		ParametresMethodesPeuplement param = listType.get(nom);
		return param;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "Distribution")
	public static class Distribution  {
		@XmlElement(name = "TypeDistribution")
		private TypeDistribution typeDistribution;
		@XmlElement(name = "Minimum")
		private double minimum;
		@XmlElement(name = "Maximum")
		private double maximum;
		@XmlElement(name = "Moyenne")
		private double moyenne;
		@XmlElement(name = "EcartType")
		private double ecartType;
		
		// Constructeur vide indispensable
		public Distribution(){}
		
		// Constructeur avec la moyenne et l'écart type (loi normale)
		public Distribution(double moyenne, double ecartType){
			this(TypeDistribution.Normale,-1.0,-1.0,moyenne,ecartType);
		}
		
		// Constructeur avec le type de distribution, le minimum, le maximum et la moyenne
		public Distribution(TypeDistribution distrib,double min,double max,double moy){
			this(distrib,min,max,moy,-1.0);
		}
		
		// Constructeur avec toutes les valeurs
		public Distribution(TypeDistribution distrib,double min,double max,double moy, double ecartT){
			setTypeDistribution(distrib);
			setMinimum(min);
			setMaximum(max);
			setMoyenne(moy);
			setEcartType(ecartT);
		}
		
		/**
		 * @param typeDistribution l'attribut typeDistribution à affecter
		 */
		public void setTypeDistribution(TypeDistribution typeDistribution) {this.typeDistribution = typeDistribution;}
		/**
		 * @return la valeur de l'attribut typeDistribution
		 */
		public TypeDistribution getTypeDistribution() {return typeDistribution;}
		/**
		 * @param minimum la valeur du minimum
		 */
		public void setMinimum(double minimum) {this.minimum = minimum;}
		/**
		 * @return la valeur du minimum
		 */
		public double getMinimum() {return minimum;}
		/**
		 * @param maximum la valeur du maximum
		 */
		public void setMaximum(double maximum) {this.maximum = maximum;}
		/**
		 * @return la valeur du maximum
		 */
		public double getMaximum() {return maximum;}
		/**
		 * @param moyenne la valeur de la moyenne
		 */
		public void setMoyenne(double moyenne) {this.moyenne = moyenne;}
		/**
		 * @return la valeur de la moyenne
		 */
		public double getMoyenne() {return moyenne;}
		/**
		 * @param ecartType la valeur de l'écart type
		 */
		public void setEcartType(double ecartType) {this.ecartType = ecartType;}
		/**
		 * @return la valeur de l'écart type
		 */
		public double getEcartType() {return ecartType;}
	}
	

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "Dates")
	public static class Dates  {
		@XmlElement(name = "DateDebut")
		private Integer dateDebut;
		@XmlElement(name = "DateFin")
		private Integer dateFin;
		
		// Constructeur vide indispensable
		public Dates(){}

		// Constructeur avec toutes les valeurs
		public Dates(Integer dateD,Integer dateF) {
			setDateDebut(dateD);
			setDateFin(dateF);
		}

		/**
		 * @param dateDebut la valeur de la date de Début
		 */
		public void setDateDebut(Integer dateDebut) {this.dateDebut = dateDebut;}

		/**
		 * @return la valeur de la date de Début
		 */
		public Integer getDateDebut() {return dateDebut;}

		/**
		 * @param dateFin la valeur de la date de fin
		 */
		public void setDateFin(Integer dateFin) {this.dateFin = dateFin;}

		/**
		 * @return la valeur de la date de fin
		 */
		public Integer getDateFin() {return dateFin;}
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "Parametres")
	public static class ParametresMethodesPeuplement  {
		@XmlElement(name = "Dates")
		private Dates datesMethode = new Dates();
		@XmlElement(name = "TypeFonctionnel")
		private int typeFonctionnel;
		@XmlElement(name = "ParametreForme")
		private List<ParametresForme> formeBatiment = new ArrayList<ParametresForme>();
		@XmlElement(name = "ParalleleRoute")
		private boolean paralleleRoute;
		@XmlElement(name = "ParalleleBatiment")
		private boolean paralleleBatiment;
		@XmlElement(name = "DistanceRoute")
		private Distribution distanceRoute;
		@XmlElement(name = "DistanceBatiment")
		private Distribution distanceBatiment;

		// Constructeur vide indispensable
		public ParametresMethodesPeuplement(){}

		// Constructeur avec toutes les valeurs
		public ParametresMethodesPeuplement(Dates dates,int typeFonct,List<ParametresForme> forme,boolean paralleleR,boolean paralleleB,Distribution distR,Distribution distB) {
			setDatesMethode(dates);
			setTypeFonctionnel(typeFonct);
			setFormeBatiment(forme);
			setParalleleRoute(paralleleR);
			setParalleleBatiment(paralleleB);
			setDistanceRoute(distR);
			setDistanceBatiment(distB);
		}

		/**
		 * @param typeFonctionnel la valeur du typeFonctionnel du bâtiment
		 */
		public void setTypeFonctionnel(int typeFonctionnel) {this.typeFonctionnel = typeFonctionnel;}

		/**
		 * @return la valeur du typeFonctionnel du bâtiment
		 */
		public int getTypeFonctionnel() {return typeFonctionnel;}

		/**
		 * @param formeBatiment la valeur de la forme du bâtiment
		 */
		public void setFormeBatiment(List<ParametresForme> formeBatiment) {this.formeBatiment = formeBatiment;}

		/**
		 * @return la valeur de la forme du bâtiment
		 */
		public List<ParametresForme> getFormeBatiment() {return formeBatiment;}

		/**
		 * @param paralleleRoute un booleen (true : parallele; false : pas parallele)
		 */
		public void setParalleleRoute(boolean paralleleRoute) {this.paralleleRoute = paralleleRoute;}

		/**
		 * @return un booleen (true : parallele; false : pas parallele)
		 */
		public boolean getParalleleRoute() {return paralleleRoute;}

		/**
		 * @param paralleleBatiment un booleen (true : parallele; false : pas parallele)
		 */
		public void setParalleleBatiment(boolean paralleleBatiment) {this.paralleleBatiment = paralleleBatiment;}

		/**
		 * @return un booleen (true : parallele; false : pas parallele)
		 */
		public boolean getParalleleBatiment() {return paralleleBatiment;}

		/**
		 * @param distanceRoute la valeur de la distance à la route
		 */
		public void setDistanceRoute(Distribution distanceRoute) {this.distanceRoute = distanceRoute;}

		/**
		 * @return la valeur de la distance à la route
		 */
		public Distribution getDistanceRoute() {return distanceRoute;}

		/**
		 * @param distanceBatiment la valeur de la distance au bâtiment
		 */
		public void setDistanceBatiment(Distribution distanceBatiment) {this.distanceBatiment = distanceBatiment;}

		/**
		 * @return la valeur de la distance au bâtiment
		 */
		public Distribution getDistanceBatiment() {return distanceBatiment;}

		/**
		 * @param datesMethode la valeur des dates de Début et de fin
		 */
		public void setDatesMethode(Dates datesMethode) {this.datesMethode = datesMethode;}

		/**
		 * @return la valeur des dates de Début et de fin
		 */
		public Dates getDatesMethode() {return datesMethode;}
	}

	/**
	 * Création ou lecture du fichier de configuration.
	 * @param args arguments (non utilisés)
	 */
	public static void main(String[] args) {

		boolean creation = true;
		boolean lecture = true;
		if (creation){// Serialisation
			ConfigurationMethodesPeuplement configuration = new ConfigurationMethodesPeuplement();
			// Création des différentes Méthodes de peuplement (classique)
			HashMap<String,ParametresMethodesPeuplement> listeMethodes = parametrageInitial();
			for (String nomMethode : listeMethodes.keySet()){
				configuration.listType.put(nomMethode, listeMethodes.get(nomMethode));
			}
			// Création des différentes Méthodes de peuplement (Annabelle)
			HashMap<String,ParametresMethodesPeuplement> listeMethodes2 = parametrageOrleans();
			for (String nomMethode : listeMethodes2.keySet()){
				configuration.listType.put(nomMethode, listeMethodes2.get(nomMethode));
			}		
			// Serialisation et enregistrement
			configuration.marshall();
		}

		if (lecture){// déserialisation
			ConfigurationMethodesPeuplement configuration = ConfigurationMethodesPeuplement.getInstance();
			HashMap<String,ParametresMethodesPeuplement> listeMethode = configuration.getListType();
			for (String nom:listeMethode.keySet()){
				ParametresMethodesPeuplement param = getInstance().getParametresMethodesPeuplement(nom);
				// Affichage
				System.out.println("Nom de la Méthode : " + nom);
				System.out.println("Date Début : " + param.getDatesMethode().getDateDebut());
				System.out.println("Date Fin : " + param.getDatesMethode().getDateFin());
				System.out.println("Type fonctionnel : " + param.getTypeFonctionnel());
				List<ParametresForme> form = param.getFormeBatiment();
				int i=0;
				for (ParametresForme forme:form){
					System.out.println("Type de bâtiment numéro "+ ++i);
					System.out.println("   - Forme : " + forme.getForme());
					System.out.println("   - Taille moyenne : " + forme.getTailleBatiment().getMoyenne());
					System.out.println("   - Ecart type de la taille : " + forme.getTailleBatiment().getEcartType());
					System.out.println("   - Elongation moyenne : " + forme.getElongationBatiment().getMoyenne());
					System.out.println("   - Ecart type de l'élongation : " + forme.getElongationBatiment().getEcartType());
					System.out.println("   - Epaisseur moyenne : " + forme.getEpaisseurBatiment().getMoyenne());
					System.out.println("   - Ecart type de l'épaisseur : " + forme.getEpaisseurBatiment().getEcartType());
					System.out.println("   - Frequence : " + forme.getFrequence());
				}
				System.out.println("parallèle à la route : " + param.getParalleleRoute());
				System.out.println("parallèle aux autres bâtiments : " + param.getParalleleBatiment());
				System.out.println("Distance moyenne par rapport à la route : " + param.getDistanceRoute().getMoyenne());
				System.out.println("Ecart Type de la distance par rapport à la route : " + param.getDistanceRoute().getEcartType());
				System.out.println("Distance moyenne aux autres bâtiments : " + param.getDistanceBatiment().getMoyenne());
				System.out.println("Ecart Type de la distance aux autres bâtiments : " + param.getDistanceBatiment().getEcartType());
				System.out.println();
			}
		}
	}
	
	// Méthodes de peuplement et paramètres établis initialement
	public static HashMap<String,ParametresMethodesPeuplement> parametrageInitial(){
		
		HashMap<String,ParametresMethodesPeuplement> listeMethodes= new HashMap<String, ParametresMethodesPeuplement>();
		
		// Quartier ouvrier
		List<ParametresForme> listeFormeQuartierOuvrier = new ArrayList<ParametresForme>();
		listeFormeQuartierOuvrier.add(new ParametresForme(FormeBatiment.Carre,new Distribution(80.0,5.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),90.0));
		listeFormeQuartierOuvrier.add(new ParametresForme(FormeBatiment.Rectangle,new Distribution(80.0,5.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
		listeMethodes.put("MaisonQuartierOuvrier", new ParametresMethodesPeuplement(new Dates(1900, 2000),TypeFonctionnel.Habitat,listeFormeQuartierOuvrier,true,true,new Distribution(10.0,-1),new Distribution(5.0,-1)));
		
		// Quartier Classe Moyenne
		List<ParametresForme> listeFormeQuartierClasseMoyenne = new ArrayList<ParametresForme>();
		listeFormeQuartierClasseMoyenne.add(new ParametresForme(FormeBatiment.Carre,new Distribution(120.0,20.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),50.0));
		listeFormeQuartierClasseMoyenne.add(new ParametresForme(FormeBatiment.Rectangle,new Distribution(120.0,20.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),30.0));
		listeFormeQuartierClasseMoyenne.add(new ParametresForme(FormeBatiment.FormeL,new Distribution(120.0,20.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),20.0));
		listeMethodes.put("MaisonQuartierClasseMoyenne",new ParametresMethodesPeuplement(new Dates(1900, 2000),TypeFonctionnel.Habitat,listeFormeQuartierClasseMoyenne,true,true,new Distribution(10.0,-1),new Distribution(15.0,-1)));
		
		// Quartier Classe intermédiaire
		List<ParametresForme> listeFormeQuartierClasseIntermediaire = new ArrayList<ParametresForme>();
		listeFormeQuartierClasseIntermediaire.add(new ParametresForme(FormeBatiment.Carre,new Distribution(150.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),30.0));
		listeFormeQuartierClasseIntermediaire.add(new ParametresForme(FormeBatiment.Rectangle,new Distribution(150.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),30.0));
		listeFormeQuartierClasseIntermediaire.add(new ParametresForme(FormeBatiment.FormeL,new Distribution(150.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),40.0));
		listeMethodes.put("MaisonQuartierClasseIntermediaire",new ParametresMethodesPeuplement(new Dates(1900, 2000),TypeFonctionnel.Habitat,listeFormeQuartierClasseIntermediaire,false,true,new Distribution(10.0,-1),new Distribution(15.0,-1)));

		// Quartier Aisé
		List<ParametresForme> listeFormeQuartierAise = new ArrayList<ParametresForme>();
		listeFormeQuartierAise.add(new ParametresForme(FormeBatiment.Carre,new Distribution(200.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
		listeFormeQuartierAise.add(new ParametresForme(FormeBatiment.Rectangle,new Distribution(200.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),40.0));
		listeFormeQuartierAise.add(new ParametresForme(FormeBatiment.FormeL,new Distribution(200.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),30.0));
		listeFormeQuartierAise.add(new ParametresForme(FormeBatiment.FormeU,new Distribution(200.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
		listeFormeQuartierAise.add(new ParametresForme(FormeBatiment.FormeT,new Distribution(200.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
		listeMethodes.put("MaisonQuartierAise",new ParametresMethodesPeuplement(new Dates(1900, 2000),TypeFonctionnel.Habitat,listeFormeQuartierAise,false,true,new Distribution(15.0,-1),new Distribution(20.0,-1)));

		// Quartier HLM
		List<ParametresForme> listeFormeQuartierHLM = new ArrayList<ParametresForme>();
		listeFormeQuartierHLM.add(new ParametresForme(FormeBatiment.Carre,new Distribution(500.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),30.0));
		listeFormeQuartierHLM.add(new ParametresForme(FormeBatiment.Rectangle,new Distribution(1200.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),40.0));
		listeFormeQuartierHLM.add(new ParametresForme(FormeBatiment.Barre,new Distribution(1200.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),30.0));
		listeMethodes.put("BarresTours1",new ParametresMethodesPeuplement(new Dates(1900, 2000),TypeFonctionnel.Habitat,listeFormeQuartierHLM,true,true,new Distribution(15.0,-1),new Distribution(30.0,-1)));			
		
		// Quartier industriel
		List<ParametresForme> listeFormeQuartierIndustriel = new ArrayList<ParametresForme>();
		listeFormeQuartierIndustriel.add(new ParametresForme(FormeBatiment.Carre,new Distribution(1000.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),20.0));
		listeFormeQuartierIndustriel.add(new ParametresForme(FormeBatiment.Rectangle,new Distribution(1200.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),70.0));
		listeMethodes.put("ZoneIndustrielle",new ParametresMethodesPeuplement(new Dates(1900, 2000),TypeFonctionnel.Industriel,listeFormeQuartierIndustriel,true,true,new Distribution(15.0,-1),new Distribution(30.0,-1)));			

		// Quartier sans information
		List<ParametresForme> listeFormeSansConnaissances = new ArrayList<ParametresForme>();
		listeMethodes.put("SansInformation", new ParametresMethodesPeuplement(new Dates(1900, 2000),TypeFonctionnel.Quelconque,listeFormeSansConnaissances,true,true,new Distribution(-1,-1),new Distribution(-1,-1)));
		
		return listeMethodes;
	}
	
	// Méthodes de peuplement et paramètres établis par Annabelle Mas (sur Orléans)
	public static HashMap<String,ParametresMethodesPeuplement> parametrageOrleans(){
		
		HashMap<String,ParametresMethodesPeuplement> listeMethodes= new HashMap<String, ParametresMethodesPeuplement>();

		// Quartier industriel (1 type)
		List<ParametresForme> listeFormeQuartierIndustriel = new ArrayList<ParametresForme>();
		Distribution distribTailleBat = new Distribution(TypeDistribution.Normale, 200, 10000, -1);
		listeFormeQuartierIndustriel.add(new ParametresForme(FormeBatiment.Carre,
				distribTailleBat,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
		listeFormeQuartierIndustriel.add(new ParametresForme(FormeBatiment.Rectangle,
				distribTailleBat,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),30.0));
		listeFormeQuartierIndustriel.add(new ParametresForme(FormeBatiment.FormeL,
				distribTailleBat,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),20.0));
		listeFormeQuartierIndustriel.add(new ParametresForme(FormeBatiment.FormeT,
				distribTailleBat,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),20.0));
		listeFormeQuartierIndustriel.add(new ParametresForme(FormeBatiment.FormeU,
				distribTailleBat,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
		listeFormeQuartierIndustriel.add(new ParametresForme(FormeBatiment.Escalier,
				distribTailleBat,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
		listeMethodes.put("ZoneIndustrielleAnnabelle",new ParametresMethodesPeuplement(new Dates(1900, 2000),TypeFonctionnel.Industriel,listeFormeQuartierIndustriel,true,true,new Distribution(15.0,-1),new Distribution(30.0,-1)));			

		// Zones d'habitat collectif (3 types)
		
		// Habitat collectif années 1950
		List<ParametresForme> listeFormeCollectif1950 = new ArrayList<ParametresForme>();
		listeFormeCollectif1950.add(new ParametresForme(FormeBatiment.Rectangle,
				new Distribution(500.0,100.0),new Distribution(-1.0,-1.0),new Distribution(10.0,-1.0),100.0));
		listeMethodes.put("CollectifAnnees1950",new ParametresMethodesPeuplement(new Dates(1945, 1960),TypeFonctionnel.Habitat,listeFormeCollectif1950,true,true,new Distribution(10.0,-1),new Distribution(5.0,-1)));			
		
		// Habitat collectif années 1970
		List<ParametresForme> listeFormeCollectif1970 = new ArrayList<ParametresForme>();
		listeFormeCollectif1970.add(new ParametresForme(FormeBatiment.Carre,
				new Distribution(350.0,50.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),20.0));
		listeFormeCollectif1970.add(new ParametresForme(FormeBatiment.Rectangle,
				new Distribution(1000.0,200.0),new Distribution(-1.0,-1.0),new Distribution(10.0,-1.0),20.0));
		listeFormeCollectif1970.add(new ParametresForme(FormeBatiment.FormeL,
				new Distribution(1000.0,200.0),new Distribution(-1.0,-1.0),new Distribution(10.0,-1.0),20.0));
		listeFormeCollectif1970.add(new ParametresForme(FormeBatiment.FormeT,
				new Distribution(1000.0,200.0),new Distribution(-1.0,-1.0),new Distribution(10.0,-1.0),20.0));
		listeFormeCollectif1970.add(new ParametresForme(FormeBatiment.FormeU,
				new Distribution(1000.0,200.0),new Distribution(-1.0,-1.0),new Distribution(10.0,-1.0),20.0));
		listeMethodes.put("CollectifAnnees1970",new ParametresMethodesPeuplement(new Dates(1960, 1980),TypeFonctionnel.Habitat,listeFormeCollectif1970,true,true,new Distribution(10.0,-1),new Distribution(10.0,-1)));			
		
		// Habitat collectif années 1990
		List<ParametresForme> listeFormeCollectif1990 = new ArrayList<ParametresForme>();
		listeFormeCollectif1990.add(new ParametresForme(FormeBatiment.Barre,
				new Distribution(600.0,200.0),new Distribution(-1.0,-1.0),new Distribution(10.0,-1.0),95.0));
		listeFormeCollectif1990.add(new ParametresForme(FormeBatiment.Escalier,
				new Distribution(2000.0,-1.0),new Distribution(0.6,-1.0),new Distribution(-1.0,-1.0),5.0));
		listeMethodes.put("CollectifAnnees1990",new ParametresMethodesPeuplement(new Dates(1980, 2500),TypeFonctionnel.Habitat,listeFormeCollectif1990,true,true,new Distribution(10.0,-1),new Distribution(5.0,-1)));			
		
		// Zone d'habitat pavillonaire (2 types)
		
		// Habitat pavillonnaire spontané
		
		
		// Habitat pavillonnaire planifié
		
		
		
		
		return listeMethodes;
	}
	
	
}


@XmlAccessorType(XmlAccessType.FIELD)
class MyHashMapTypeMP {
	@XmlElement(name="MethodePeuplement")
    List<MyHashMapEntryTypeMP> entry = new ArrayList<MyHashMapEntryTypeMP>();
    public void put(String key, ParametresMethodesPeuplement value) {entry.add(new MyHashMapEntryTypeMP(key,value));}
    public ParametresMethodesPeuplement get(String key) {
    	for (MyHashMapEntryTypeMP e:entry) {
    		if (e.key.equals(key)) return e.value;
    	}
    	return null;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class MyHashMapEntryTypeMP {
    @XmlAttribute(name="nomMethode")
    public String key; 
    @XmlElements({@XmlElement(name="parametres",type=ParametresMethodesPeuplement.class)})
    public ParametresMethodesPeuplement value;
    public MyHashMapEntryTypeMP() {}
    public MyHashMapEntryTypeMP(String key, ParametresMethodesPeuplement value) {
    	this.key=key;
    	this.value=value;
    }
}

@SuppressWarnings("unchecked")
@XmlAccessorType(XmlAccessType.FIELD)
final class MyHashMapAdapterMP extends XmlAdapter<MyHashMapTypeMP,HashMap> {

	@Override
	public MyHashMapTypeMP marshal(HashMap v) throws Exception {
		MyHashMapTypeMP map = new MyHashMapTypeMP();
		for(Object o:v.entrySet()) {
			Entry entry = (Entry) o;
			map.put((String) entry.getKey(),(ParametresMethodesPeuplement) entry.getValue());
		}
		return map;
	}

	@Override
	public HashMap unmarshal(MyHashMapTypeMP v) throws Exception {
		HashMap map = new HashMap();
		for(MyHashMapEntryTypeMP entry:v.entry) {map.put(entry.key,entry.value);}
		return map;
	}
}

