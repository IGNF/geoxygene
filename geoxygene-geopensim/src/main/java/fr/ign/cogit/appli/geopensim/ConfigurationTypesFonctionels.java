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

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.feature.meso.HomogeneiteTypeFonctionnelBatiments;
import fr.ign.cogit.appli.geopensim.feature.micro.TypeFonctionnel;
import fr.ign.cogit.appli.geopensim.ConfigurationTypesFonctionels.Parametres;


/**
 * @author Florence Curie
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ConfigurationTypesFonctionels")
public class ConfigurationTypesFonctionels {
    private static Logger logger = Logger
    .getLogger(ConfigurationTypesFonctionels.class.getName());

	private static ConfigurationTypesFonctionels config;
	private static String cheminFichierConfigurationTypesFonctionels="src/resources/geopensim/configurationTypesFonctionels.xml";

	@XmlJavaTypeAdapter(MyHashMapAdapterTF.class)
	@XmlElement(name="TypesFonctionels")
    HashMap<Integer,Parametres> listType = new HashMap<Integer,Parametres>();


	// Constructeur vide indispensable
	public ConfigurationTypesFonctionels(){}

	// déserialisation du fichier xml
	public static ConfigurationTypesFonctionels getInstance() {
		try {
			JAXBContext context = JAXBContext.newInstance(ConfigurationTypesFonctionels.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			config = (ConfigurationTypesFonctionels) unmarshaller.unmarshal(new FileInputStream(new File(cheminFichierConfigurationTypesFonctionels)));
			return config;
		} catch (JAXBException e) {e.printStackTrace();}
		catch (FileNotFoundException e) {e.printStackTrace();}
		return null;
	}

	// sérialisation des objets java
	public void marshall() {
		try {
			JAXBContext context = JAXBContext.newInstance(ConfigurationTypesFonctionels.class);
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
	public Parametres getParametres (int nom){
		Parametres param = listType.get(nom);
		return param;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "Parametres")
	public static class Parametres  {
		@XmlElement(name = "PourcentageHabitatMin")
		private Double pourcentageHabitatMin;
		@XmlElement(name = "PourcentageHabitatMax")
		private Double pourcentageHabitatMax;
		@XmlElement(name = "PourcentageIndustrielMin")
		private Double pourcentageIndustrielMin;
		@XmlElement(name = "PourcentageIndustrielMax")
		private Double pourcentageIndustrielMax;
		@XmlElement(name = "PourcentagePublicMin")
		private Double pourcentagePublicMin;
		@XmlElement(name = "PourcentagePublicMax")
		private Double pourcentagePublicMax;

		// Constructeur vide indispensable
		public Parametres(){}

		// Constructeur avec toutes les valeurs
		public Parametres(double pHMin,double pHMax,double pIMin,double pIMax,double pPMin,double pPMax) {
			pourcentageHabitatMin = pHMin;
			pourcentageHabitatMax = pHMax;
			pourcentageIndustrielMin = pIMin;
			pourcentageIndustrielMax = pIMax;
			pourcentagePublicMin = pPMin;
			pourcentagePublicMax = pPMax;
		}

		/**
		 * @return pourcentageHabitatMin la valeur du pourcentage minimal d'habitat
		 */
		public Double getPourcentageHabitatMin() {return this.pourcentageHabitatMin;}

		/**
		 * @param pourcentageHabitatMin la valeur du pourcentage minimal d'habitat
		 */
		public void setPourcentageHabitatMin(Double pourcentageHabitatMin) {this.pourcentageHabitatMin = pourcentageHabitatMin;}

		/**
		 * @return pourcentageHabitatMax la valeur du pourcentage maximal d'habitat
		 */
		public Double getPourcentageHabitatMax() {return this.pourcentageHabitatMax;}

		/**
		 * @param pourcentageHabitatMax la valeur du pourcentage maximal d'habitat
		 */
		public void setPourcentageHabitatMax(Double pourcentageHabitatMax) {this.pourcentageHabitatMax = pourcentageHabitatMax;}

		/**
		 * @return pourcentageIndustrielMin la valeur du pourcentage minimal d'industrie
		 */
		public Double getPourcentageIndustrielMin() {return this.pourcentageIndustrielMin;}

		/**
		 * @param pourcentageIndustrielMin la valeur du pourcentage minimal d'industrie
		 */
		public void setPourcentageIndustrielMin(Double pourcentageIndustrielMin) {this.pourcentageIndustrielMin = pourcentageIndustrielMin;}

		/**
		 * @return pourcentageIndustrielMax la valeur du pourcentage maximal d'industrie
		 */
		public Double getPourcentageIndustrielMax() {return this.pourcentageIndustrielMax;}

		/**
		 * @param pourcentageIndustrielMax la valeur du pourcentage maximal d'industrie
		 */
		public void setPourcentageIndustrielMax(Double pourcentageIndustrielMax) {this.pourcentageIndustrielMax = pourcentageIndustrielMax;}

		/**
		 * @return pourcentagePublicMin la valeur du pourcentage minimal de public
		 */
		public Double getPourcentagePublicMin() {return this.pourcentagePublicMin;}

		/**
		 * @param pourcentagePublicMin la valeur du pourcentage minimal de public
		 */
		public void setPourcentagePublicMin(Double pourcentagePublicMin) {this.pourcentagePublicMin = pourcentagePublicMin;}

		/**
		 * @return pourcentagePublicMax la valeur du pourcentage maximal de public
		 */
		public Double getPourcentagePublicMax() {return this.pourcentagePublicMax;}

		/**
		 * @param pourcentagePublicMax la valeur du pourcentage maximal de public
		 */
		public void setPourcentagePublicMax(Double pourcentagePublicMax) {this.pourcentagePublicMax = pourcentagePublicMax;}

	}

	/**
	 * Création ou lecture du fichier de configuration.
	 * @param args arguments (non utilisés)
	 */
	public static void main(String[] args) {

		boolean creation = true;
		boolean lecture = true;
		if (creation){
			ConfigurationTypesFonctionels configuration = new ConfigurationTypesFonctionels();
			// Création des différents TypeFonctionel
			configuration.listType.put(HomogeneiteTypeFonctionnelBatiments.HomogeneHabitat, new Parametres(100,100,0,0,0,0));
			configuration.listType.put(HomogeneiteTypeFonctionnelBatiments.HomogeneIndustriel,new Parametres(0,0,100,100,0,0));
			configuration.listType.put(HomogeneiteTypeFonctionnelBatiments.HomogenePublic,new Parametres(0,0,0,0,100,100));
			configuration.listType.put(HomogeneiteTypeFonctionnelBatiments.QuasiHomogeneHabitat,new Parametres(70,100,0,30,0,30));
			configuration.listType.put(HomogeneiteTypeFonctionnelBatiments.QuasiHomogeneIndustriel,new Parametres(0,30,70,100,0,30));
			configuration.listType.put(HomogeneiteTypeFonctionnelBatiments.QuasiHomogenePublic,new Parametres(0,30,0,30,70,100));
			configuration.listType.put(HomogeneiteTypeFonctionnelBatiments.Heterogene,new Parametres(0,70,0,70,0,70));
			// Serialisation et enregistrement
			configuration.marshall();
		}

		if (lecture){
			// déserialisation
			int nom = HomogeneiteTypeFonctionnelBatiments.HomogeneIndustriel;
			Parametres param = getInstance().getParametres(nom);
			// Affichage
			System.out.println("Type ");
			System.out.println("Nom   : " + nom);
			System.out.println("Pourcentage Habitat Min  : " + param.getPourcentageHabitatMin());
			System.out.println("Pourcentage Habitat Max : " + param.getPourcentageHabitatMax());
			System.out.println("Pourcentage Industriel Min  : " + param.getPourcentageIndustrielMin());
			System.out.println("Pourcentage Industriel Max : " + param.getPourcentageIndustrielMax());
			System.out.println("Pourcentage Public Min  : " + param.getPourcentagePublicMin());
			System.out.println("Pourcentage Public Max : " + param.getPourcentagePublicMax());
			System.out.println();
		}
	}

    /**
     * @param agentZoneElementaireBatie
     * @return
     */
    public int getTypeFonctionnelNewBuilding(
            AgentZoneElementaireBatie agentZoneElementaireBatie) {
        // On Détermine le type fonctionel du nouveau bâtiment en fonction du type fonctionel objectif
        // On récupère les pourcentages objectifs de chaque type de bâtiments
        int homogeneiteTypeFonctionnelBatimentsBut = agentZoneElementaireBatie
                .getHomogeneiteTypesFonctionnelsBatimentsBut();
        Parametres parametresType = ConfigurationTypesFonctionels
                .getInstance().getParametres(
                        homogeneiteTypeFonctionnelBatimentsBut);
        // On calcule les pourcentages de chaque type de bâtiments
        int nbBatimentsHab = 0;
        int nbBatimentsPub = 0;
        int nbBatimentsInd = 0;
        for (AgentBatiment batiment : agentZoneElementaireBatie.getBatiments()) {
            if (batiment.getTypeFonctionnel() == TypeFonctionnel.Habitat) nbBatimentsHab++;
            else if (batiment.getTypeFonctionnel() == TypeFonctionnel.Public) nbBatimentsPub++;
            else if (batiment.getTypeFonctionnel() == TypeFonctionnel.Industriel)
                nbBatimentsInd++;
        }
        int nbBatTotal = agentZoneElementaireBatie.getBatiments().size();
        double pourcHab = (double) nbBatimentsHab / nbBatTotal * 100;
        double pourcInd = (double) nbBatimentsInd / nbBatTotal * 100;
        double pourcPub = (double) nbBatimentsPub / nbBatTotal * 100;

        // On ajoute le batiment en plus grande carence sinon
        // tirage au sort parmi les trois
        double diffMaxi = 0;
        int typeFonctionnel = -1;
        if (parametresType.getPourcentageHabitatMin() - pourcHab > diffMaxi) {
            diffMaxi = parametresType.getPourcentageHabitatMin() - pourcHab;
            typeFonctionnel = TypeFonctionnel.Habitat;
            logger.debug("diffMaxi hab : " + diffMaxi);
        }
        if (parametresType.getPourcentageIndustrielMin() - pourcInd > diffMaxi) {
            diffMaxi = parametresType.getPourcentageIndustrielMin() - pourcInd;
            typeFonctionnel = TypeFonctionnel.Industriel;
            logger.debug("diffMaxi ind : " + diffMaxi);
        }
        if (parametresType.getPourcentagePublicMin() - pourcPub > diffMaxi) {
            diffMaxi = parametresType.getPourcentagePublicMin() - pourcPub;
            typeFonctionnel = TypeFonctionnel.Public;
            logger.debug("diffMaxi pub : " + diffMaxi);
        }
        if (typeFonctionnel == -1) {
            double alea = Math.random();
            if (alea < (1 / 3)) {typeFonctionnel = TypeFonctionnel.Habitat;}
            else if (alea < (2 / 3)) {typeFonctionnel = TypeFonctionnel.Industriel;} 
            else {typeFonctionnel = TypeFonctionnel.Public;}
        }
        return typeFonctionnel;
    }
}


@XmlAccessorType(XmlAccessType.FIELD)
class MyHashMapTypeTF {
	@XmlElement(name="TypeFonctionel")
    List<MyHashMapEntryTypeTF> entry = new ArrayList<MyHashMapEntryTypeTF>();
    public void put(Integer key, Parametres value) {entry.add(new MyHashMapEntryTypeTF(key,value));}
    public Parametres get(Integer key) {
    	for (MyHashMapEntryTypeTF e:entry) {
    		if (e.key.equals(key)) return e.value;
    	}
    	return null;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class MyHashMapEntryTypeTF {
    @XmlAttribute(name="nomType")
    public Integer key;
    @XmlElements({@XmlElement(name="parametres",type=Parametres.class)})
    public Parametres value;
    public MyHashMapEntryTypeTF() {}
    public MyHashMapEntryTypeTF(Integer key, Parametres value) {
    	this.key=key;
    	this.value=value;
    }
}

@SuppressWarnings("unchecked")
@XmlAccessorType(XmlAccessType.FIELD)
final class MyHashMapAdapterTF extends XmlAdapter<MyHashMapTypeTF,HashMap> {

	@Override
	public MyHashMapTypeTF marshal(HashMap v) throws Exception {
		MyHashMapTypeTF map = new MyHashMapTypeTF();
		for(Object o:v.entrySet()) {
			Entry entry = (Entry) o;
			map.put((Integer) entry.getKey(),(Parametres) entry.getValue());
		}
		return map;
	}

	@Override
	public HashMap unmarshal(MyHashMapTypeTF v) throws Exception {
		HashMap map = new HashMap();
		for(MyHashMapEntryTypeTF entry:v.entry) {map.put(entry.key,entry.value);}
		return map;
	}
}

