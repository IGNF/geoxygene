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

import fr.ign.cogit.appli.geopensim.ConfigurationComparaison.ParametresComparaison;

/**
 * @author Florence Curie
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ConfigurationComparaison")
public class ConfigurationComparaison {
	private static ConfigurationComparaison config;
	
  private static String cheminFichierConfigurationComparaison = "src/resources/geopensim/ConfigurationComparaison.xml"; //$NON-NLS-1$
	
	@XmlJavaTypeAdapter(MyHashMapAdapterCompar.class)
	@XmlElement(name="ParametresComparaison")
    HashMap<String,List<ParametresComparaison>> listType = new HashMap<String,List<ParametresComparaison>>();
	
	/**
	 * @return listType la liste des paramétrages
	 */
	public HashMap<String, List<ParametresComparaison>> getListType() {return this.listType;}

	// Constructeur vide indispensable
	public ConfigurationComparaison(){}

	// déserialisation du fichier xml
	public static ConfigurationComparaison getInstance() {
		try {
			JAXBContext context = JAXBContext.newInstance(ConfigurationComparaison.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			config = (ConfigurationComparaison) unmarshaller.unmarshal(new FileInputStream(new File(cheminFichierConfigurationComparaison)));
			return config;
		} catch (JAXBException e) {e.printStackTrace();} 
		catch (FileNotFoundException e) {e.printStackTrace();}
		return null;
	}

	// sérialisation des objets java
	public void marshall() {
		try {
			JAXBContext context = JAXBContext.newInstance(ConfigurationComparaison.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			File fichier = new File(cheminFichierConfigurationComparaison);
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
	
	// Récupération des paramètres du type fonctionel
	public List<ParametresComparaison> getParametres (String nom){
		List<ParametresComparaison> param = listType.get(nom);
		return param;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "Parametres")
	public static class ParametresComparaison  {
		@XmlElement(name = "Method")
		private String methode;
		@XmlElement(name = "TypeOperation")
		private String typeOperation;
		@XmlElement(name = "Ponderation")
		private Double ponderation;
		
		// Constructeur vide indispensable
		public ParametresComparaison(){}

		// Constructeur avec toutes les valeurs
		public ParametresComparaison(String meth,String typeOp,Double pond) {
			setMethode(meth);
			setTypeOperation(typeOp);
			setPonderation(pond);
		}

		/**
		 * @param methode la méthode à appliquer
		 */
		public void setMethode(String meth) {this.methode = meth;}
		/**
		 * @return la valeur de la méthode à appliquer
		 */
		public String getMethode() {return methode;}
		/**
		 * @param typeOperation le type d'opération pour aggrégation
		 */
		public void setTypeOperation(String typeOp) {this.typeOperation = typeOp;}
		/**
		 * @return la valeur du type d'opération pour aggrégation
		 */
		public String getTypeOperation() {return typeOperation;}
		/**
		 * @param ponderation la valeur de la ponderation à appliquer
		 */
		public void setPonderation(Double ponderation) {this.ponderation = ponderation;}
		/**
		 * @return a valeur de la ponderation à appliquer
		 */
		public Double getPonderation() {return ponderation;}
	}

	/**
	 * Création ou lecture du fichier de configuration.
	 * @param args arguments (non utilisés)
	 */
	public static void main(String[] args) {
		boolean creation = true;
		boolean lecture = true;
		if (creation){
			ConfigurationComparaison configuration = new ConfigurationComparaison();
			// Création d'un premier paramétrage
			List<ParametresComparaison> listeParametresComparaison = new ArrayList<ParametresComparaison>();
			listeParametresComparaison.add(new ParametresComparaison("getAire", "moyenne", 1.0));
			listeParametresComparaison.add(new ParametresComparaison("getNombreBatiments", "somme", 2.0));
			configuration.listType.put("param1", listeParametresComparaison);
			// Serialisation et enregistrement
			configuration.marshall();
		}

		if (lecture){
			// déserialisation
			String nom = "param1";
			List<ParametresComparaison> params = getInstance().getParametres(nom);
			// Affichage
			System.out.println("Nom du paramétrage :" + nom);
			System.out.println(" ");
			int i=0;
			for (ParametresComparaison param:params){
				System.out.println("Type de paramètres numéro "+ ++i);
				System.out.println("   - Méthode : " + param.getMethode());
				System.out.println("   - Type d'opération : " + param.getTypeOperation());
				System.out.println("   - Pondération : " +param.getPonderation());
				System.out.println();
			}
		}
	}
}

@XmlAccessorType(XmlAccessType.FIELD)
class MyHashMapTypeCompar {
	@XmlElement(name="TypeComparaison")
    List<MyHashMapEntryTypeCompar> entry = new ArrayList<MyHashMapEntryTypeCompar>();
    public void put(String key, List<ParametresComparaison> value) {entry.add(new MyHashMapEntryTypeCompar(key,value));}
    public List<ParametresComparaison> get(Integer key) {
    	for (MyHashMapEntryTypeCompar e:entry) {
    		if (e.key.equals(key)) return e.value;
    	}
    	return null;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class MyHashMapEntryTypeCompar {
    @XmlAttribute(name="nomType")
    public String key; 
    @XmlElements({@XmlElement(name="parametres",type=ParametresComparaison.class)})
    public List<ParametresComparaison> value;
    public MyHashMapEntryTypeCompar() {}
    public MyHashMapEntryTypeCompar(String key, List<ParametresComparaison> value) {
    	this.key=key;
    	this.value=value;
    }
}

@SuppressWarnings({ "unchecked", "rawtypes" })
@XmlAccessorType(XmlAccessType.FIELD)
final class MyHashMapAdapterCompar extends XmlAdapter<MyHashMapTypeCompar,HashMap> {

	@Override
	public MyHashMapTypeCompar marshal(HashMap v) throws Exception {
		MyHashMapTypeCompar map = new MyHashMapTypeCompar();
		for(Object o:v.entrySet()) {
			Entry entry = (Entry) o;
			map.put((String) entry.getKey(),(List<ParametresComparaison>) entry.getValue());
		}
		return map;
	}

	@Override
	public HashMap unmarshal(MyHashMapTypeCompar v) throws Exception {
		HashMap map = new HashMap();
		for(MyHashMapEntryTypeCompar entry:v.entry) {map.put(entry.key,entry.value);}
		return map;
	}
}

