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
import java.io.OutputStream;
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

import fr.ign.cogit.appli.geopensim.comportement.Comportement;
import fr.ign.cogit.appli.geopensim.comportement.ComportementConstructionRoute;
import fr.ign.cogit.appli.geopensim.contrainte.Contrainte;
import fr.ign.cogit.geoxygene.filter.expression.Add;
import fr.ign.cogit.geoxygene.filter.expression.BinaryExpression;
import fr.ign.cogit.geoxygene.filter.expression.Expression;
import fr.ign.cogit.geoxygene.filter.expression.ExpressionFactory;
import fr.ign.cogit.geoxygene.filter.expression.Function;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.Multiply;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.filter.expression.Subtract;
import fr.ign.cogit.geoxygene.filter.function.FunctionImpl;

/**
 * Configuration de la Simulation. La configuration initiale est chargée à partir du fichier "configurationSimulation.xml".
 * Elle peut ensuite etre modifiée à l'aide des setters de la classe.
 * 
 * Exemple d'utilisation : ConfigurationSimulation.getInstance();
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "configurationSimulation")
public class ConfigurationSimulation {
    static Logger logger=Logger.getLogger(ConfigurationSimulation.class.getName());

    private double resolution;
    private double seuilSatisfactionValidite;
    private double seuilSatisfactionDensite;
    private int nbMaxEtatsAVisiter;
    private boolean stockageEtat;

    //@XmlElement(name = "entry", required = true)
    //@XmlElement(name="typeAgent")
    //@XmlElementRef
    //@XmlElementWrapper(name="contraintes")
    @XmlJavaTypeAdapter(MyHashMapAdapter.class)
    HashMap<String,List<Contrainte>> contraintes = new HashMap<String,List<Contrainte>>();

    private volatile static ConfigurationSimulation uniqueInstance;

    public static ConfigurationSimulation getInstance() {
	if (uniqueInstance==null) synchronized (ConfigurationSimulation.class) {if (uniqueInstance==null) uniqueInstance = newInstance();}
	return uniqueInstance;
    }

    private static String cheminFichierConfigurationSimulation="src/resources/geopensim/configurationSimulation.xml";

    public static ConfigurationSimulation newInstance() {
		try {
			JAXBContext context = JAXBContext.newInstance(
					ConfigurationSimulation.class,
					Expression.class,
					Multiply.class,
					Subtract.class,
					Add.class,
					Literal.class,
					PropertyName.class,
					Function.class,
					FunctionImpl.class);
			Unmarshaller m = context.createUnmarshaller();
			return (ConfigurationSimulation) m.unmarshal(new FileInputStream(new File(cheminFichierConfigurationSimulation)));
		} catch (JAXBException e) {e.printStackTrace();} 
		catch (FileNotFoundException e) {e.printStackTrace();}
	    return null;
    }

	public void marshall(OutputStream stream) {
		try {
			JAXBContext context = JAXBContext.newInstance(
					ConfigurationSimulation.class,
					Expression.class,
					Multiply.class,
					Subtract.class,
					Add.class,
					Literal.class,
					PropertyName.class,
					Function.class,
					FunctionImpl.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(this, stream);
		} catch (JAXBException e) {e.printStackTrace();}		
	}
    /**
     * Parcours toutes les classes mères de la classe de représentation afin de
     * trouver toutes les contraintes s'appliquant à cette classe.
     * @param representationClass classe de représentation
     * @return Contraintes liées à la classe passée en paramètre
     */
    public List<Contrainte> getContraintes(Class<?> representationClass) {
	List<Contrainte> listeContraintes = new ArrayList<Contrainte>();
	while (!representationClass.equals(Object.class)) {
	    List<Contrainte> contraintesClass = contraintes.get(representationClass.getSimpleName());
	    if (contraintesClass != null) listeContraintes.addAll(contraintesClass);
	    representationClass=representationClass.getSuperclass();
	}
	return listeContraintes;
    }
    /**
     * @param representationClass classe de représentation
     * @return Contraintes liées à la classe passée en paramètre
    public List<Contrainte> getContraintes(String representationClass) {
	List<Contrainte> listeContraintes = contraintes.get(representationClass);
	if (listeContraintes == null) return new ArrayList<Contrainte>();
	return listeContraintes;
    }
     */
    /**
     * @param representationClass classe de représentation
     * @return Contraintes liées à la classe passée en paramètre
     */
    /*
	public static List<Contrainte> getContraintes(String representationClass) {
		if (logger.isDebugEnabled()) logger.debug("Récupération des contraintes pour le type "+representationClass);
		return getInstance().getContraintes(representationClass);
		if (representationClass.equals(Batiment.class.getName())) {return getContraintesBatiment();}
		if (representationClass.equals(TronconRoute.class.getName())) {return getContraintesTronconRoute();}
		if (representationClass.equals(TronconChemin.class.getName())) {return getContraintesTronconChemin();}
		return null;
	}
     */

    /**
     * @return ContraintesBatiment
     */	
    //	private static List<Contrainte> getContraintesBatiment() {
    //		return null;
    //	}

    /**
     * @return ContraintesTronconRoute
     */
    //	private static List<Contrainte> getContraintesTronconRoute() {
    //		return null;
    //	}

    /**
     * @return ContraintesTronconChemin
     */
    //	private static List<Contrainte> getContraintesTronconChemin() {
    //		return null;
    //	}

    /**
     * Renvoie la valeur de l'attribut resolution.
     * @return la valeur de l'attribut resolution
     */
    public double getResolution() {return this.resolution;}

    /**
     * Affecte la valeur de l'attribut resolution.
     * @param resolution l'attribut resolution à affecter
     */
    public void setResolution(double resolution) {this.resolution = resolution;}

    /**
     * Renvoie la valeur de l'attribut seuilSatisfactionValidite.
     * @return la valeur de l'attribut seuilSatisfactionValidite
     */
    public double getSeuilSatisfactionValidite() {return this.seuilSatisfactionValidite;}

    /**
     * Affecte la valeur de l'attribut seuilSatisfactionValidite.
     * @param seuilSatisfactionValidite l'attribut seuilSatisfactionValidite à affecter
     */
    public void setSeuilSatisfactionValidite(double seuilSatisfactionValidite) {this.seuilSatisfactionValidite = seuilSatisfactionValidite;}

    /**
     * Renvoie la valeur de l'attribut seuilSatisfactionDensite.
     * @return la valeur de l'attribut seuilSatisfactionDensite
     */
    public double getSeuilSatisfactionDensite() {return this.seuilSatisfactionDensite;}

    /**
     * Affecte la valeur de l'attribut seuilSatisfactionDensite.
     * @param seuilSatisfactionDensite l'attribut seuilSatisfactionDensite à affecter
     */
    public void setSeuilSatisfactionDensite(double seuilSatisfactionDensite) {this.seuilSatisfactionDensite = seuilSatisfactionDensite;}

    /**
     * Renvoie la valeur de l'attribut nbMaxEtatsAVisiter.
     * @return la valeur de l'attribut nbMaxEtatsAVisiter
     */
    public int getNbMaxEtatsAVisiter() {return this.nbMaxEtatsAVisiter;}

    /**
     * Affecte la valeur de l'attribut nbMaxEtatsAVisiter.
     * @param nbMaxEtatsAVisiter l'attribut nbMaxEtatsAVisiter à affecter
     */
    public void setNbMaxEtatsAVisiter(int nbMaxEtatsAVisiter) {this.nbMaxEtatsAVisiter = nbMaxEtatsAVisiter;}

    /**
	 * Renvoie la valeur de l'attribut stockageEtat.
	 * @return la valeur de l'attribut stockageEtat
	 */
	public boolean getStockageEtat() {return this.stockageEtat;}

	/**
	 * Affecte la valeur de l'attribut stockageEtat.
	 * @param stockageEtat l'attribut stockageEtat à affecter
	 */
	public void setStockageEtat(boolean stockageEtat) {this.stockageEtat = stockageEtat;}

	/**
     * Renvoie la valeur de l'attribut cheminFichierConfigurationSimulation.
     * @return la valeur de l'attribut cheminFichierConfigurationSimulation
     */
    public static String getCheminFichierConfigurationSimulation() {return cheminFichierConfigurationSimulation;}

    /**
     * Affecte la valeur de l'attribut cheminFichierConfigurationSimulation.
     * @param cheminFichierConfigurationSimulation l'attribut cheminFichierConfigurationSimulation à affecter
     */
    public static void setCheminFichierConfigurationSimulation(String cheminFichierConfigurationSimulation) {
	ConfigurationSimulation.cheminFichierConfigurationSimulation = cheminFichierConfigurationSimulation;
    }

    @Override
    public String toString() {
	String s = this.getClass().getSimpleName()+"\n";
	s+= "\t resolution = "+this.getResolution()+" - seuil = "+this.getSeuilSatisfactionValidite()+" - nbEtats = "+this.getNbMaxEtatsAVisiter()+" - stockageEtats = "+this.getStockageEtat()+"\n";
	s+= "\t contraintes = "+this.contraintes;
	return s;
    }

    /**
     * Lecture du fichier de configuration et affichage de la configuration lue.
     * @param args arguments (non utilisés)
     */
    public static void main(String[] args) {

	System.out.println("*** test de chargement depuis le fichier xml ***");
	ConfigurationSimulation configuration = ConfigurationSimulation.getInstance();
	configuration.marshall(System.out);
	System.out.println("*** test de sauvegarde dans le fichier xml ***");
	configuration = new ConfigurationSimulation();
	configuration.setResolution(1.0);
	configuration.setSeuilSatisfactionValidite(0.5);
	configuration.setNbMaxEtatsAVisiter(50);
	configuration.setStockageEtat(true);
	//Contrainte contrainte = new Densite(new AgentGeographique(), 1.1, 50);
	//configuration.contraintes.put("ZoneElementaire", contrainte);
	Contrainte contrainte = new Contrainte(1.1,50);
	Function expression = (Function) ExpressionFactory.createFunction("max");
	expression.getParameters().add(new Literal("44"));
	expression.getParameters().add(new Literal("10"));
	BinaryExpression mul = (BinaryExpression) ExpressionFactory.createExpression("mul");
	BinaryExpression sub = (BinaryExpression) ExpressionFactory.createExpression("sub");
	sub.getParameters().add(new Literal("4"));
	sub.getParameters().add(new Literal("3"));
	mul.getParameters().add(expression);
	mul.getParameters().add(sub);
	Object res = mul.evaluate(null);
	System.out.println(res);
	contrainte.setExpression(mul);
	Comportement comportement = new ComportementConstructionRoute();
	ArrayList<Comportement> comportements = new ArrayList<Comportement>();
	comportements.add(comportement);
	contrainte.setComportements(comportements);
	ArrayList<Contrainte> contraintes = new ArrayList<Contrainte>();
	contraintes.add(contrainte);
	configuration.contraintes.put("ZoneElementaire", contraintes);
	System.out.println(configuration);
	configuration.marshall(System.out);
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
class MyHashMapType {
	@XmlElement(name="agent")
    List<MyHashMapEntryType> entry = new ArrayList<MyHashMapEntryType>();
    public void put(String key, List<Contrainte> value) {entry.add(new MyHashMapEntryType(key,value));}
    public List<Contrainte> get(String key) {
    	for (MyHashMapEntryType e:entry) {
    		if (e.key.equals(key)) return e.value;
    	}
    	return null;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class MyHashMapEntryType {
    @XmlAttribute(name="type")
    public String key; 
    @XmlElements({@XmlElement(name="contrainte",type=Contrainte.class)})
    public List<Contrainte> value;
    public MyHashMapEntryType() {}
    public MyHashMapEntryType(String key, List<Contrainte> value) {
    	this.key=key;
    	this.value=value;
    }
}

@SuppressWarnings("unchecked")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
final class MyHashMapAdapter extends XmlAdapter<MyHashMapType,HashMap> {

	@Override
	public MyHashMapType marshal(HashMap v) throws Exception {
		MyHashMapType map = new MyHashMapType();
		for(Object o:v.entrySet()) {
			Entry entry = (Entry) o;
			map.put((String) entry.getKey(),(List<Contrainte>) entry.getValue());
		}
		return map;
	}

	@Override
	public HashMap unmarshal(MyHashMapType v) throws Exception {
		HashMap map = new HashMap();
		for(MyHashMapEntryType entry:v.entry) {map.put(entry.key,entry.value);}
		return map;
	}
}
