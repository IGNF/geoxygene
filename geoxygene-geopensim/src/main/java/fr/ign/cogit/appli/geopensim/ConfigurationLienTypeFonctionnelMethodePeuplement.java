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

import fr.ign.cogit.appli.geopensim.ConfigurationLienTypeFonctionnelMethodePeuplement.TypePeuplement;;

/**
 * @author Florence Curie
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ConfigurationLienTypeFonctionnelMethodePeuplement")
public class ConfigurationLienTypeFonctionnelMethodePeuplement {

	private static ConfigurationLienTypeFonctionnelMethodePeuplement config;
	private static String cheminFichierConfigurationLienTypeFonctionnelMethodePeuplement="src/resources/geopensim/ConfigurationLienTypeFonctionnelMethodePeuplement.xml";
	
	@XmlJavaTypeAdapter(MyHashMapAdapterLTFMP.class)
	@XmlElement(name="TypesFonctionnelsObjectifs")
	private HashMap<String,List<TypePeuplement>> listType = new HashMap<String,List<TypePeuplement>>();
	/**
	 * @return listType la liste des types de peuplement
	 */
	public HashMap<String,List<TypePeuplement>> getListType() {return this.listType;}
	/**
	 * @param la liste des types de peuplement
	 */
	public void setListType(HashMap<String,List<TypePeuplement>> listType) {this.listType = listType;}

	// Constructeur vide indispensable
	public ConfigurationLienTypeFonctionnelMethodePeuplement(){}

	// déserialisation du fichier xml
	public static ConfigurationLienTypeFonctionnelMethodePeuplement getInstance() {
		try {
			JAXBContext context = JAXBContext.newInstance(ConfigurationLienTypeFonctionnelMethodePeuplement.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			config = (ConfigurationLienTypeFonctionnelMethodePeuplement) unmarshaller.unmarshal(new FileInputStream(new File(cheminFichierConfigurationLienTypeFonctionnelMethodePeuplement)));
			return config;
		}catch (JAXBException e) {e.printStackTrace(); 
		}catch (FileNotFoundException e) {e.printStackTrace();}
		return null;
	}
	
	public static ConfigurationLienTypeFonctionnelMethodePeuplement getInstance(String nomChemin) {
		cheminFichierConfigurationLienTypeFonctionnelMethodePeuplement = nomChemin;
		return getInstance();
	}

	// sérialisation des objets java
	public void marshall() {
		try {
			JAXBContext context = JAXBContext.newInstance(ConfigurationLienTypeFonctionnelMethodePeuplement.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			File fichier = new File(cheminFichierConfigurationLienTypeFonctionnelMethodePeuplement);
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
		cheminFichierConfigurationLienTypeFonctionnelMethodePeuplement = nomChemin;
		marshall();
	}
	
	// Récupération des paramètres du type fonctionel
	public List<TypePeuplement> getTypesPeuplement (String nom){
		List<TypePeuplement> param = listType.get(nom);
		return param;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "TypePeuplement")
	public static class TypePeuplement{
		@XmlElement(name = "Frequence")
		private double frequence;
		@XmlElement(name = "ParametresPeuplement")
		private List<TypeMethode> parametresPeuplement = new ArrayList<TypeMethode>();

		// Constructeur vide indispensable
		public TypePeuplement(){}

		// Constructeur avec toutes les valeurs
		public TypePeuplement(double freq,List<TypeMethode> paramPeupl) {
			setFrequence(freq);
			setParametresPeuplement(paramPeupl);
		}

		/**
		 * @param pourcentage l'attribut frequence à affecter
		 */
		public void setFrequence(double frequence) {this.frequence = frequence;}
		/**
		 * @return la valeur de l'attribut frequence
		 */
		public double getFrequence() {return frequence;}
		/**
		 * @param parametresPeuplement l'attribut parametresPeuplement à affecter
		 */
		public void setParametresPeuplement(List<TypeMethode> parametresPeuplement) {this.parametresPeuplement = parametresPeuplement;}
		/**
		 * @return la valeur de l'attribut parametresPeuplement
		 */
		public List<TypeMethode> getParametresPeuplement() {return parametresPeuplement;}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "TypeMethode")
	public static class TypeMethode{
		@XmlElement(name = "Pourcentage")
		private double pourcentage;
		@XmlElement(name = "MethodePeuplement")
		private String nomMethodePeuplement;

		// Constructeur vide indispensable
		public TypeMethode(){}

		// Constructeur avec toutes les valeurs
		public TypeMethode(double pourc,String nomMethodePeuplement) {
			setPourcentage(pourc);
			setNomMethodePeuplement(nomMethodePeuplement);
		}

		/**
		 * @param pourcentage l'attribut pourcentage à affecter
		 */
		public void setPourcentage(double pourcentage) {this.pourcentage = pourcentage;}
		/**
		 * @return la valeur de l'attribut pourcentage
		 */
		public double getPourcentage() {return pourcentage;}
		/**
		 * @param nomMethodePeuplement l'attribut nomMethodePeuplement à affecter
		 */
		public void setNomMethodePeuplement(String nomMethodePeuplement) {this.nomMethodePeuplement = nomMethodePeuplement;}
		/**
		 * @return la valeur de l'attribut nomMethodePeuplement
		 */
		public String getNomMethodePeuplement() {return nomMethodePeuplement;}
	}

	/**
	 * Création ou lecture du fichier de configuration.
	 * @param args arguments (non utilisés)
	 */
	public static void main(String[] args) {

		boolean creation = true;
		boolean lecture = true;
		if (creation){// Serialisation
			ConfigurationLienTypeFonctionnelMethodePeuplement configuration = new ConfigurationLienTypeFonctionnelMethodePeuplement();
			// Création des différentes Méthodes de peuplement (classique)
			HashMap<String,List<TypePeuplement>> listeMethodes = parametrageInitial();
			for (String nomMethode : listeMethodes.keySet()){
				configuration.listType.put(nomMethode, listeMethodes.get(nomMethode));
			}		
			// Serialisation et enregistrement
			configuration.marshall();
		}

		if (lecture){// déserialisation
			ConfigurationLienTypeFonctionnelMethodePeuplement configuration = ConfigurationLienTypeFonctionnelMethodePeuplement.getInstance();
			HashMap<String,List<TypePeuplement>> listeMethode = configuration.getListType();
			for (String nom:listeMethode.keySet()){
				List<TypePeuplement> listeTP = getInstance().getTypesPeuplement(nom);
				// Affichage
				System.out.println("Nom du type fonctionnel objectif : " + nom);
				for (TypePeuplement typeP :listeTP){
					System.out.println("Type de peuplement : ");
					System.out.println("     Fréquence : " + typeP.getFrequence());
					for (TypeMethode typeM:typeP.getParametresPeuplement()){
						System.out.println("     Type de méthode : "+typeM.getNomMethodePeuplement() +" à "+typeM.pourcentage);
					}
				}
				System.out.println();
			}
		}
	}
	
	// Méthodes de peuplement et paramètres établis initialement
	public static HashMap<String,List<TypePeuplement>> parametrageInitial(){
		
		HashMap<String,List<TypePeuplement>> listeMethodes= new HashMap<String,List<TypePeuplement>>();
		// HabitatDiscontinuTypePavillonaireIndividuel
		List<TypeMethode> listeTypeMethode1 = new ArrayList<TypeMethode>();
		listeTypeMethode1.add(new TypeMethode(20,"habitat1"));
		listeTypeMethode1.add(new TypeMethode(80,"habitat2"));
		TypePeuplement typeP1 = new TypePeuplement(10, listeTypeMethode1);
		List<TypeMethode> listeTypeMethode2 = new ArrayList<TypeMethode>();
		listeTypeMethode2.add(new TypeMethode(100,"habitat3"));
		TypePeuplement typeP2 = new TypePeuplement(90, listeTypeMethode2);
		List<TypePeuplement> listeT = new ArrayList<TypePeuplement>();
		listeT.add(typeP1);
		listeT.add(typeP2);
		listeMethodes.put("HabitatDiscontinuTypePavillonaireIndividuel", listeT);
		return listeMethodes;
	}
}


@XmlAccessorType(XmlAccessType.FIELD)
class MyHashMapTypeLTFMP {
	@XmlElement(name="TypeFonctionnelObjectif")
    List<MyHashMapEntryTypeLTFMP> entry = new ArrayList<MyHashMapEntryTypeLTFMP>();
    public void put(String key, List<TypePeuplement> value) {entry.add(new MyHashMapEntryTypeLTFMP(key,value));}
    public List<TypePeuplement> get(String key) {
    	for (MyHashMapEntryTypeLTFMP e:entry) {
    		if (e.key.equals(key)) return e.value;
    	}
    	return null;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class MyHashMapEntryTypeLTFMP {
    @XmlAttribute(name="nomType")
    public String key; 
    @XmlElements({@XmlElement(name="TypePeuplement",type=TypePeuplement.class)})
    public List<TypePeuplement> value;
    public MyHashMapEntryTypeLTFMP() {}
    public MyHashMapEntryTypeLTFMP(String key, List<TypePeuplement> value) {
    	this.key=key;
    	this.value=value;
    }
}

@SuppressWarnings("unchecked")
@XmlAccessorType(XmlAccessType.FIELD)
final class MyHashMapAdapterLTFMP extends XmlAdapter<MyHashMapTypeLTFMP,HashMap> {

	@Override
	public MyHashMapTypeLTFMP marshal(HashMap v) throws Exception {
		MyHashMapTypeLTFMP map = new MyHashMapTypeLTFMP();
		for(Object o:v.entrySet()) {
			Entry entry = (Entry) o;
			map.put((String) entry.getKey(),(List<TypePeuplement>) entry.getValue());
		}
		return map;
	}

	@Override
	public HashMap unmarshal(MyHashMapTypeLTFMP v) throws Exception {
		HashMap map = new HashMap();
		for(MyHashMapEntryTypeLTFMP entry:v.entry) {map.put(entry.key,entry.value);}
		return map;
	}
}

