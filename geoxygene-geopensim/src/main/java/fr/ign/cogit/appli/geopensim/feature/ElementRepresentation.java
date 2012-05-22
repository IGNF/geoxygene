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
package fr.ign.cogit.appli.geopensim.feature;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Type;

import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.comportement.Comportement;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_Feature;

/**
 * Classe mère de toutes les représentation d'objets Géographiques.
 * Elle implèmente les identifiants des représentations ainsi que
 * la classe conCrête pour OJB et la datation des représentations.
 * @author Julien Perret
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ElementRepresentation extends FT_Feature {
	static Logger logger=Logger.getLogger(ElementRepresentation.class.getName());
	static int staticId = 1;
	public static NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();


    @Override
    @Id @GeneratedValue
    public int getId() {return super.getId();}
    @Override
    @Column(name = "geom")
    @Type(type = "fr.ign.cogit.geoxygene.datatools.hibernate.GeOxygeneGeometryUserType")
    public IGeometry getGeom() {return super.getGeom();}

    /**
	 * Constructeur de représentations Géographiques.
	 */
	public ElementRepresentation() {
		super();
		setOjbConcreteClass(this.getClass().getName());
		this.setId(staticId++);
	}
	/**
	 * Constructeur de représentations Géographiques à partir d'une géométrie
	 * @param geom géométrie de la représentation Géographique
	 */
	public ElementRepresentation(IGeometry geom) {
		super(geom);
		setOjbConcreteClass(this.getClass().getName());
		this.setId(staticId++);
	}

	/**
	 * @return identifiant unique de la représentation
	 */
	public int getIdRep() {return this.getId(); }
	/**
	 * @param idRep identifiant unique de la représentation
	 */
	public void setIdRep (int idRep) {this.setId(idRep); }

	protected AgentGeographique agentGeographique = null;

	/**
	 * @return the elementGeo
	 */
	@ManyToOne(optional=true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	public AgentGeographique getAgentGeographique() {return agentGeographique;}
	/**
	 * @param agentGeographique the elementGeo to set
	 */
	public void setAgentGeographique(AgentGeographique agentGeographique) {this.agentGeographique = agentGeographique;}

	/**
	 * La classe conCrête des objets java liés par OJB.
	 */
	protected String ojbConcreteClass = " ";
	/**
	 * @return la classe conCrête des objets java liés par OJB.
	 */
	public String getOjbConcreteClass() {return ojbConcreteClass;}
	/**
	 * @param ojbConcreteClass la classe conCrête des objets java liés par OJB
	 */
	public void setOjbConcreteClass(String ojbConcreteClass) {this.ojbConcreteClass = ojbConcreteClass;}

	/**
	 * Date de diffusion des données.
	 */
	protected int dateDiffusion = 0;
	/**
	 * @return date de diffusion des données
	 */
	public int getDateDiffusion() {return dateDiffusion;}
	/**
	 * @param dateDiffusion date de diffusion des données
	 */
	public void setDateDiffusion(int dateDiffusion) {this.dateDiffusion = dateDiffusion;}

	/**
	 * Date de mise en service des données.
	 */
	protected int dateMiseEnService = 0;
	/**
	 * @return date de mise en service des données
	 */
	public int getDateMiseEnService() {return dateMiseEnService;}
	/**
	 * @param dateMiseEnService date de mise en service des données
	 */
	public void setDateMiseEnService(int dateMiseEnService) {this.dateMiseEnService = dateMiseEnService;}

	/**
	 * Date d'acquisition des données sources utilisées pour la saisie des données.
	 */
	protected int dateSourceSaisie = 0;
	/**
	 * @return date d'acquisition des données sources utilisées pour la saisie des données
	 */
	public int getDateSourceSaisie() {return dateSourceSaisie;}
	/**
	 * @param dateSourceSaisie date d'acquisition des données sources utilisées pour la saisie des données
	 */
	public void setDateSourceSaisie(int dateSourceSaisie) {this.dateSourceSaisie = dateSourceSaisie;}

	protected int changement = Changement.Inconnu;
	/**
	 * @return the changement
	 */
	public int getChangement() {return changement;}
	/**
	 * @param changement the changement to set
	 */
	public void setChangement(int changement) {this.changement = changement;}
	@Transient
	public String getChangementString() {return Changement.toString(changement);}
	
	protected boolean simulated = false;
	/**
	 * @return vrai si la représentation est simulée, faux sinon.
	 */
	public boolean isSimulated() {return simulated;}
	/**
	 * @param simul vrai si la représentation est simulée, faux sinon.
	 */
	public void setSimulated(boolean simul) {this.simulated = simul;}

	protected int idSimul;
	/**
	 * @return la valeur de l'attribut idSimul
	 */
	public int getIdSimul() {return this.idSimul;}
	/**
	 * @param idSimul l'attribut idSimul à affecter
	 */
	public void setIdSimul(int idSimul) {this.idSimul = idSimul;}
	
	
	/**
	 * Qualifier la représentation
	 */
	public abstract void qualifier();
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName()+" "+this.getId()+" ("+this.getDateSourceSaisie()+")";
	}

	double satisfaction;
	/**
	 * Affecte la valeur de la satisfaction de l'agent au moment de la création de la représentation
	 * @param satisfaction La satisfaction de l'agent au moment de la création de la représentation
	 */
	public void setSatisfaction(double satisfaction) {this.satisfaction = satisfaction;}
	/**
	 * @return La satisfaction de l'agent au moment de la création de la représentation
	 */
	public double getSatisfaction() {return satisfaction;}
	
	private ElementRepresentation precedent=null;
	private List<ElementRepresentation> precedents = new ArrayList<ElementRepresentation>(0);
	/**
	 * Affecte la valeur de l'attribut precedent.
	 * @param precedent l'attribut precedent à affecter
	 */
	public void setPrecedent(ElementRepresentation precedent) {this.precedent = precedent;}
	public void setPrecedents(List<ElementRepresentation> precedents) {this.precedents = precedents;}

	/**
	 * @return
	 */
	public ElementRepresentation getPrecedent() {return this.precedent;}
	public List<ElementRepresentation> getPrecedents() {return this.precedents;}


	private List<ElementRepresentation> successeurs = new ArrayList<ElementRepresentation>(0);
	public List<ElementRepresentation> getSuccesseurs() {return this.successeurs;}
	
	private Comportement comportement=null;
	/**
	 * @param comportementAEssayer
	 */
	public void setComportement(Comportement comportement) {this.comportement=comportement;}
	/**
	 * @return
	 */
	public Comportement getComportement() {return this.comportement;}
	
	//Modification Flo
	private List<AgentGeographique> elementsModifies = new ArrayList<AgentGeographique>(0);
	/**
	 * @return la valeur de l'attribut elementsModifies
	 */
	public List<AgentGeographique> getElementsModifies() {return this.elementsModifies;}
	/**
	 * @param elementsModifies l'attribut elementsModifies à affecter
	 */
	public void setElementsModifies(List<AgentGeographique> elementsModifies) {this.elementsModifies = elementsModifies;}

	private List<ElementRepresentation> representationsModifiees = new ArrayList<ElementRepresentation>();
	/**
	 * @param representationsModifiees l'attribut representationsModifiees à affecter
	 */
	public void setRepresentationsModifiees(List<ElementRepresentation> representationsModifiees) {this.representationsModifiees = representationsModifiees;}
	/**
	 * @return la valeur de l'attribut representationsModifiees
	 */
	public List<ElementRepresentation> getRepresentationsModifiees() {return representationsModifiees;}

}
