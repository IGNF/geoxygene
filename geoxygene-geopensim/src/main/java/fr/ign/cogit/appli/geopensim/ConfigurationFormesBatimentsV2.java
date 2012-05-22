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

import fr.ign.cogit.appli.geopensim.feature.micro.FormeBatiment;
import fr.ign.cogit.appli.geopensim.feature.micro.TypeFonctionnel;
import fr.ign.cogit.appli.geopensim.ConfigurationFormesBatimentsV2.ParametresForme;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.Distribution;


/**
 * @author Florence Curie
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ConfigurationFormesBatiments")
public class ConfigurationFormesBatimentsV2 {

	private static ConfigurationFormesBatimentsV2 config;
	private static String cheminFichierConfigurationTypesFonctionels="src/resources/geopensim/ConfigurationFormesBatiments.xml";
	
	@XmlJavaTypeAdapter(MyHashMapAdapterFB2.class)
	@XmlElement(name="TypesFonctionels")
    HashMap<Integer,List<ParametresForme>> listType = new HashMap<Integer,List<ParametresForme>>();
	
	// Constructeur vide indispensable
	public ConfigurationFormesBatimentsV2(){}

	// déserialisation du fichier xml
	public static ConfigurationFormesBatimentsV2 getInstance() {
		try {
			JAXBContext context = JAXBContext.newInstance(ConfigurationFormesBatimentsV2.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			config = (ConfigurationFormesBatimentsV2) unmarshaller.unmarshal(new FileInputStream(new File(cheminFichierConfigurationTypesFonctionels)));
			return config;
		} catch (JAXBException e) {e.printStackTrace();} 
		catch (FileNotFoundException e) {e.printStackTrace();}
		return null;
	}

	// sérialisation des objets java
	public void marshall() {
		try {
			JAXBContext context = JAXBContext.newInstance(ConfigurationFormesBatimentsV2.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			File fichier = new File(cheminFichierConfigurationTypesFonctionels);
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
	public List<ParametresForme> getParametres (int nom){
		List<ParametresForme> param = listType.get(nom);
		return param;
	}

	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "Parametres")
	public static class ParametresForme  {
		@XmlElement(name = "Forme")
		private FormeBatiment forme;
		@XmlElement(name = "Taille")
		private Distribution tailleBatiment;
		@XmlElement(name = "Elongation")
		private Distribution elongationBatiment;
		@XmlElement(name = "Epaisseur")
		private Distribution epaisseurBatiment;
		@XmlElement(name = "Frequence")
		private Double frequence;
		
		// Constructeur vide indispensable
		public ParametresForme(){}

		// Constructeur avec toutes les valeurs
		public ParametresForme(FormeBatiment form,Distribution taille,Distribution elong,Distribution epaiss,Double freq) {
			setForme(form);
			setTailleBatiment(taille);
			setElongationBatiment(elong);
			setEpaisseurBatiment(epaiss);
			setFrequence(freq);
		}

		/**
		 * @param forme la valeur de la forme du bâtiment
		 */
		public void setForme(FormeBatiment forme) {this.forme = forme;}

		/**
		 * @return la valeur de la forme du bâtiment
		 */
		public FormeBatiment getForme() {return forme;}

		/**
		 * @param tailleBatiment la valeur de la taille du bâtiment
		 */
		public void setTailleBatiment(Distribution tailleBatiment) {this.tailleBatiment = tailleBatiment;}

		/**
		 * @return la valeur de la taille du bâtiment
		 */
		public Distribution getTailleBatiment() {return tailleBatiment;}

		/**
		 * @param elongationBatiment la valeur de l'élongation du bâtiment
		 */
		public void setElongationBatiment(Distribution elongationBatiment) {this.elongationBatiment = elongationBatiment;}

		/**
		 * @return la valeur de l'élongation du bâtiment
		 */
		public Distribution getElongationBatiment() {return elongationBatiment;}

		/**
		 * @param epaisseurBatiment la valeur de l'épaisseur du bâtiment
		 */
		public void setEpaisseurBatiment(Distribution epaisseurBatiment) {this.epaisseurBatiment = epaisseurBatiment;}

		/**
		 * @return la valeur de l'épaisseur du bâtiment
		 */
		public Distribution getEpaisseurBatiment() {return epaisseurBatiment;}

		/**
		 * @param frequence la valeur de la fréquence associée à la forme
		 */
		public void setFrequence(Double frequence) {this.frequence = frequence;}

		/**
		 * @return la valeur de la fréquence associée à la forme
		 */
		public Double getFrequence() {return frequence;}

	}

	/**
	 * Création ou lecture du fichier de configuration.
	 * @param args arguments (non utilisés)
	 */
	public static void main(String[] args) {

		boolean creation = true;
		boolean lecture = true;
		if (creation){
			ConfigurationFormesBatimentsV2 configuration = new ConfigurationFormesBatimentsV2();
			// Création des différents TypeFonctionel
			// Habitat
			List<ParametresForme> listeFormeHabitat = new ArrayList<ParametresForme>();
			listeFormeHabitat.add(new ParametresForme(FormeBatiment.Rectangle,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),90.0));
			listeFormeHabitat.add(new ParametresForme(FormeBatiment.FormeL,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
			configuration.listType.put(TypeFonctionnel.Habitat, listeFormeHabitat);
			// Industriel
			List<ParametresForme> listeFormeIndustriel = new ArrayList<ParametresForme>();
			listeFormeIndustriel.add(new ParametresForme(FormeBatiment.Rectangle,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),50.0));
			listeFormeIndustriel.add(new ParametresForme(FormeBatiment.FormeL,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
			listeFormeIndustriel.add(new ParametresForme(FormeBatiment.FormeU,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
			listeFormeIndustriel.add(new ParametresForme(FormeBatiment.FormeT,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
			listeFormeIndustriel.add(new ParametresForme(FormeBatiment.Escalier,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
			listeFormeIndustriel.add(new ParametresForme(FormeBatiment.Cercle,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
			configuration.listType.put(TypeFonctionnel.Industriel,listeFormeIndustriel);
			// Public
			List<ParametresForme> listeFormePublic = new ArrayList<ParametresForme>();
			listeFormePublic.add(new ParametresForme(FormeBatiment.Rectangle,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),50.0));
			listeFormePublic.add(new ParametresForme(FormeBatiment.FormeL,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
			listeFormePublic.add(new ParametresForme(FormeBatiment.FormeU,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
			listeFormePublic.add(new ParametresForme(FormeBatiment.FormeT,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
			listeFormePublic.add(new ParametresForme(FormeBatiment.Escalier,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
			listeFormePublic.add(new ParametresForme(FormeBatiment.Cercle,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
			configuration.listType.put(TypeFonctionnel.Public,listeFormePublic);
			// Quelconque
			List<ParametresForme> listeFormeQuelconque = new ArrayList<ParametresForme>();
			listeFormeQuelconque.add(new ParametresForme(FormeBatiment.Rectangle,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),70.0));
			listeFormeQuelconque.add(new ParametresForme(FormeBatiment.FormeL,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),5.0));
			listeFormeQuelconque.add(new ParametresForme(FormeBatiment.FormeU,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),5.0));
			listeFormeQuelconque.add(new ParametresForme(FormeBatiment.FormeT,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),10.0));
			listeFormeQuelconque.add(new ParametresForme(FormeBatiment.Escalier,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),5.0));
			listeFormeQuelconque.add(new ParametresForme(FormeBatiment.Cercle,new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),new Distribution(-1.0,-1.0),5.0));
			configuration.listType.put(TypeFonctionnel.Quelconque,listeFormeQuelconque);
			// Serialisation et enregistrement
			configuration.marshall();
		}

		if (lecture){
			// déserialisation
			int nom = TypeFonctionnel.Habitat;
			List<ParametresForme> params = getInstance().getParametres(nom);
			// Affichage
			System.out.println("Type Fonctionnel :" + nom);
			System.out.println(" ");
			int i=0;
			for (ParametresForme param:params){
				System.out.println("Type de bâtiment numéro "+ ++i);
				System.out.println("   - Forme : " + param.getForme());
				System.out.println("   - Taille moyenne : " + param.getTailleBatiment().getMoyenne());
				System.out.println("   - Ecart type de la taille : " +param.getTailleBatiment().getEcartType());
				System.out.println("   - Elongation moyenne : " + param.getElongationBatiment().getMoyenne());
				System.out.println("   - Ecart type de l'élongation : " + param.getElongationBatiment().getEcartType());
				System.out.println("   - Epaisseur moyenne : " + param.getEpaisseurBatiment().getMoyenne());
				System.out.println("   - Ecart type de l'épaisseur : " + param.getEpaisseurBatiment().getEcartType());
				System.out.println("   - Frequence : " + param.getFrequence());
				System.out.println();
			}
		}
	}
}


@XmlAccessorType(XmlAccessType.FIELD)
class MyHashMapTypeFB2 {
	@XmlElement(name="TypeFonctionel")
    List<MyHashMapEntryTypeFB2> entry = new ArrayList<MyHashMapEntryTypeFB2>();
    public void put(Integer key, List<ParametresForme> value) {entry.add(new MyHashMapEntryTypeFB2(key,value));}
    public List<ParametresForme> get(Integer key) {
    	for (MyHashMapEntryTypeFB2 e:entry) {
    		if (e.key.equals(key)) return e.value;
    	}
    	return null;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class MyHashMapEntryTypeFB2 {
    @XmlAttribute(name="nomType")
    public Integer key; 
    @XmlElements({@XmlElement(name="parametres",type=ParametresForme.class)})
    public List<ParametresForme> value;
    public MyHashMapEntryTypeFB2() {}
    public MyHashMapEntryTypeFB2(Integer key, List<ParametresForme> value) {
    	this.key=key;
    	this.value=value;
    }
}

@SuppressWarnings("unchecked")
@XmlAccessorType(XmlAccessType.FIELD)
final class MyHashMapAdapterFB2 extends XmlAdapter<MyHashMapTypeFB2,HashMap> {

	@Override
	public MyHashMapTypeFB2 marshal(HashMap v) throws Exception {
		MyHashMapTypeFB2 map = new MyHashMapTypeFB2();
		for(Object o:v.entrySet()) {
			Entry entry = (Entry) o;
			map.put((Integer) entry.getKey(),(List<ParametresForme>) entry.getValue());
		}
		return map;
	}

	@Override
	public HashMap unmarshal(MyHashMapTypeFB2 v) throws Exception {
		HashMap map = new HashMap();
		for(MyHashMapEntryTypeFB2 entry:v.entry) {map.put(entry.key,entry.value);}
		return map;
	}
}

