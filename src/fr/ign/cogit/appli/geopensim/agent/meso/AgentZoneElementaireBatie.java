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

package fr.ign.cogit.appli.geopensim.agent.meso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.Entity;
import javax.persistence.Transient;

import fr.ign.cogit.appli.geopensim.ConfigurationFormesBatimentsV2;
import fr.ign.cogit.appli.geopensim.ConfigurationLienTypeFonctionnelMethodePeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationFormesBatimentsV2.ParametresForme;
import fr.ign.cogit.appli.geopensim.ConfigurationLienTypeFonctionnelMethodePeuplement.TypeMethode;
import fr.ign.cogit.appli.geopensim.ConfigurationLienTypeFonctionnelMethodePeuplement.TypePeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.ParametresMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.agent.AgentGeographiqueCollection;
import fr.ign.cogit.appli.geopensim.agent.event.AgentCollectionEvent;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentEspaceVide;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTroncon;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTronconChemin;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTronconRoute;
import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.appli.geopensim.feature.meso.ClasseUrbaine;
import fr.ign.cogit.appli.geopensim.feature.meso.GroupeBatiments;
import fr.ign.cogit.appli.geopensim.feature.meso.HomogeneiteTypeFonctionnelBatiments;
import fr.ign.cogit.appli.geopensim.feature.meso.UniteUrbaine;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaire;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.appli.geopensim.feature.micro.EspaceVide;
import fr.ign.cogit.appli.geopensim.feature.micro.Troncon;
import fr.ign.cogit.appli.geopensim.feature.micro.TypeFonctionnel;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.event.FeatureCollectionEvent;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.delaunay.AbstractTriangulation;
import fr.ign.cogit.geoxygene.contrib.delaunay.ChargeurTriangulation;
import fr.ign.cogit.geoxygene.contrib.delaunay.NoeudDelaunay;
import fr.ign.cogit.geoxygene.contrib.delaunay.TriangulationJTS;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.JtsUtil;

/**
 * @author Julien Perret
 *
 */
@Entity
public class AgentZoneElementaireBatie extends AgentZoneElementaire {
	/**
	 *
	 */
	public AgentZoneElementaireBatie() {
		super();
		this.representationClass=ZoneElementaireUrbaine.class;
		this.representationClassString=representationClass.getName();
	}
	/**
	 *
	 */
	public AgentZoneElementaireBatie(int idGeo) {super(idGeo,ZoneElementaireUrbaine.class);}

	private AgentUniteBatie uniteBatie = null;
	/**
	 * @return la valeur de l'attribut uniteBatie
	 */
	@Transient
	public AgentUniteBatie getUniteBatie() {return this.uniteBatie;}
	/**
	 * @param uniteBatie l'attribut uniteBatie à affecter
	 */
	public void setUniteBatie(AgentUniteBatie uniteBatie) {this.uniteBatie = uniteBatie;}

	/**
	 * Renvoie la valeur de l'attribut batiments.
	 * @return la valeur de l'attribut batiments
	 */
	@Transient
	public Set<AgentBatiment> getBatiments() {
		Set<AgentBatiment> listeBatiments = new HashSet<AgentBatiment>(0);
		if (this.getGroupesBatiments() != null) {
		  for (AgentGroupeBatiments groupeBatiments:this.getGroupesBatiments()){
		    listeBatiments.addAll(groupeBatiments.getBatiments());
		  }
		}
		return listeBatiments;
	}
	
	private Set<AgentTroncon> troncons = null;
	/**
	 * @return la valeur de l'attribut troncons
	 */
	@Transient
	public Set<AgentTroncon> getTroncons() {return this.troncons;}
	/**
	 * @param troncons l'attribut troncons à affecter
	 */
	public void setTroncons(Set<AgentTroncon> troncons) {
		this.troncons = troncons;
		for (AgentTroncon troncon:troncons) {troncon.addZoneElementaires(this);}
	}

	private Set<AgentZoneElementaireBatie> zonesElementairesBaties = null;
	/**
	 * @return la valeur de l'attribut zonesElementairesBaties
	 */
	@Transient
    public Set<AgentZoneElementaireBatie> getVoisins() {
	    if (this.zonesElementairesBaties == null) {
	        if (logger.isDebugEnabled()) {
	            logger.debug("recreating neighbourhood");
	        }
	        this.zonesElementairesBaties = new HashSet<AgentZoneElementaireBatie>(0);
            for (AgentTroncon troncon : this.getTroncons()) {
                for (AgentZoneElementaire zone : troncon.getZonesElementaires()) {
                    if (AgentZoneElementaireBatie.class.isAssignableFrom(zone.getClass())) {
                        this.zonesElementairesBaties.add((AgentZoneElementaireBatie) zone);
                    }
                }
            }
            this.zonesElementairesBaties.remove(this);
	    }
        return this.zonesElementairesBaties;
    }

	/**
	 * @param zonesElementairesBaties l'attribut zonesElementairesBaties à affecter
	 */
	public void setVoisins(Set<AgentZoneElementaireBatie> zonesElementairesBaties) {
		this.zonesElementairesBaties = zonesElementairesBaties;
	}

	private Set<AgentEspaceVide> espacesVides = null;
	/**
	 * @return la valeur de l'attribut espacesVides
	 */
	@Transient
	public Set<AgentEspaceVide> getEspacesVides() {return this.espacesVides;}
	/**
	 * @param espacesVides l'attribut espacesVides à affecter
	 */
	public void setEspacesVides(Set<AgentEspaceVide> espacesVides) {this.espacesVides = espacesVides;}

	private Set<AgentGroupeBatiments> groupesBatiments = null;
	/**
	 * @return groupes de bâtiments de la zone élémentaire batie
	 */
	@Transient
	public Set<AgentGroupeBatiments> getGroupesBatiments() {return this.groupesBatiments;}
	/**
	 * @param groupesBatiments groupes de bâtiments de la zone élémentaire batie
	 */
	public void setGroupesBatiments(Set<AgentGroupeBatiments> groupesBatiments) {
		Set<AgentGroupeBatiments> listeGBPrecedent = new HashSet<AgentGroupeBatiments>();
		if (this.getGroupesBatiments()!=null){
			for (AgentGroupeBatiments agentGB:this.getGroupesBatiments())listeGBPrecedent.add(agentGB);
		}
		this.groupesBatiments = groupesBatiments;
		for (AgentGroupeBatiments groupeBatiments:groupesBatiments) {
			groupeBatiments.setZoneElementaireBatie(this);
		}
		AgentGeographiqueCollection collection = AgentGeographiqueCollection.getInstance();
   	collection.fireActionPerformed(new AgentCollectionEvent(
   					collection,
   					this,
   					FeatureCollectionEvent.Type.CHANGED,
   					"GroupesBatiments",
   					listeGBPrecedent,
   					groupesBatiments));
	}


	public void addGroupeBatiments(AgentGroupeBatiments groupeBatiments) {
		Set<AgentGroupeBatiments> listeGBPrecedent = new HashSet<AgentGroupeBatiments>();
		for (AgentGroupeBatiments agentGB:this.getGroupesBatiments())listeGBPrecedent.add(agentGB);
		this.getGroupesBatiments().add(groupeBatiments);
		AgentGeographiqueCollection.getInstance().getGroupesBatiments().add(groupeBatiments);

		Set<AgentGroupeBatiments> listeGBSuivant = new HashSet<AgentGroupeBatiments>();
		for (AgentGroupeBatiments agentGB:this.getGroupesBatiments())listeGBSuivant.add(agentGB);
		AgentGeographiqueCollection collection = AgentGeographiqueCollection.getInstance();
   	collection.fireActionPerformed(new AgentCollectionEvent(
   					collection,
   					this,
   					FeatureCollectionEvent.Type.CHANGED,
   					"GroupesBatiments",
   					listeGBPrecedent,
   					listeGBSuivant));
	}


	@Override
	public void prendreAttributsRepresentation(ElementRepresentation representation) {
		super.prendreAttributsRepresentation(representation);
		//this.setZoneAgregee((AgentZoneAgregee) ((ZoneElementaire)this.getRepresentationDebutSimulation()).getZoneAgregeeUrbaine().getAgentGeographique());
		ZoneElementaireUrbaine zoneElementaireUrbaine = (ZoneElementaireUrbaine)representation;
		if (zoneElementaireUrbaine.getUniteUrbaine()==null) {
		  logger.warn("unite urbaine nulle pour la zone "+zoneElementaireUrbaine.getId()+" à la date "+zoneElementaireUrbaine.getDateSourceSaisie());
		}
		else {
		  this.setUniteBatie((AgentUniteBatie) zoneElementaireUrbaine.getUniteUrbaine().getAgentGeographique());
		}
		//		Set<AgentBatiment> newBatiments = new HashSet<AgentBatiment>();
		//		for (Batiment batiment:zoneElementaireUrbaine.getBatiments()) {
		//			newBatiments.add((AgentBatiment) batiment.getAgentGeographique());
		//		}
		//		this.setBatiments(newBatiments);
		this.setHomogeneiteTypesFonctionnelsBatiments(zoneElementaireUrbaine.getHomogeneiteTypesFonctionnelsBatiments());
		this.setDistanceMoyennePlusProcheBatiment(zoneElementaireUrbaine.getDistanceMoyennePlusProcheBatiment());
		this.setClassificationFonctionnelle(zoneElementaireUrbaine.getClassificationFonctionnelle());
	    this.setDensite(zoneElementaireUrbaine.getDensite());

		AgentGeographiqueCollection.getInstance().getTronconsRoute().removeAll(this.getTroncons());
		Set<AgentTroncon> newTroncons = new HashSet<AgentTroncon>();
		for (Troncon troncon:zoneElementaireUrbaine.getTroncons()) {
		    AgentTroncon agentTroncon = (AgentTroncon)  troncon.getAgentGeographique();
		    agentTroncon.prendreAttributsRepresentation(troncon);
		    newTroncons.add(agentTroncon);
		}
		this.setTroncons(newTroncons);
		AgentGeographiqueCollection.getInstance().getTronconsRoute().addAll(this.getTroncons());

		Set<AgentEspaceVide> newEspacesVides = new HashSet<AgentEspaceVide>(0);
		for (EspaceVide espaceVide:zoneElementaireUrbaine.getEspacesVides()) {
		    AgentEspaceVide agentEspace = (AgentEspaceVide) espaceVide.getAgentGeographique();
		    agentEspace.prendreAttributsRepresentation(espaceVide);
		    newEspacesVides.add(agentEspace);
		}
		this.setEspacesVides(newEspacesVides);

		// les voisins
		Set<AgentZoneElementaireBatie> newZonesElementairesBaties = new HashSet<AgentZoneElementaireBatie>(0);
		for (ZoneElementaire zoneElementaire:zoneElementaireUrbaine.getVoisins()) {
		    AgentZoneElementaireBatie agentZoneElementaireBatie = (AgentZoneElementaireBatie)zoneElementaire.getAgentGeographique();
		    newZonesElementairesBaties.add(agentZoneElementaireBatie);
		}
		this.setVoisins(newZonesElementairesBaties);
//		logger.info("avant " + this.getBatiments().size());
		if (this.getGroupesBatiments()!=null){
			for (AgentGroupeBatiments agentGroupe:this.getGroupesBatiments()) {
				agentGroupe.setSupprime(true);
			}
			AgentGeographiqueCollection.getInstance().getGroupesBatiments().removeAll(this.getGroupesBatiments());
		}
        AgentGeographiqueCollection.getInstance().getBatiments().removeAll(this.getBatiments());
		Set<AgentGroupeBatiments> newGroupesBatiments = new HashSet<AgentGroupeBatiments>(0);
		for (GroupeBatiments groupeBatiments:zoneElementaireUrbaine.getGroupesBatiments()) {
			AgentGroupeBatiments agentGroupe = (AgentGroupeBatiments) groupeBatiments.getAgentGeographique();
			agentGroupe.prendreAttributsRepresentation(groupeBatiments);
			newGroupesBatiments.add(agentGroupe);
		}
		this.setGroupesBatiments(newGroupesBatiments);
		if (this.getGroupesBatiments()!=null) {
		  AgentGeographiqueCollection.getInstance().getGroupesBatiments().addAll(this.getGroupesBatiments());
		}
//        logger.info("apres " + this.getBatiments().size());
        // FIXME Bidouille
        this.setInfinite(zoneElementaireUrbaine.getBordeUniteUrbaine());
	}
	private double densite = -1;
	/**
	 * @return
	 */
	public double getDensite() {
	    if (densite ==  -1) {
	        densite = calculDensite();
	    }
	    return densite;
	}
	public void setDensite(double d) {
	   	this.densite = d;
	}
	public void miseAjourDensite(){
		double densiteAvant = this.getDensite();
		this.densite = calculDensite();
		AgentGeographiqueCollection collection = AgentGeographiqueCollection.getInstance();
	   	collection.fireActionPerformed(new AgentCollectionEvent(
	   					collection,
	   					this,
	   					FeatureCollectionEvent.Type.CHANGED,
	   					"Densite",
	   					densiteAvant,
	   					this.densite));
	}

	/**
	 * @return
	 */
	private double calculDensite() {
		double surfaceBatiments = 0.0;
		int nbBatiments = 0;
		if (this.getBatiments() != null) {
		    for (AgentBatiment batiment:this.getBatiments()) {
		        if (batiment.getGeom() != null) {
	                surfaceBatiments+=batiment.getGeom().area();
	                nbBatiments++;
		        }
		    }
		}
		if (logger.isDebugEnabled()) logger.debug("Nb Batiments = "+nbBatiments);

		double bufferSize = 10;
		IGeometry tronconsUnion = null;
		if (this.getTroncons() != null) {
		    for(AgentTroncon troncon:this.getTroncons()) {
		        if (troncon.getGeom() != null) {
		            if (tronconsUnion == null) {
		                tronconsUnion = troncon.getGeom().buffer(bufferSize);
		            } else {
		                tronconsUnion = tronconsUnion.union(troncon.getGeom().buffer(bufferSize));
		            }
		        }
		    }
		}
		IGeometry geometry = this.getGeom();
		if (tronconsUnion!=null) geometry = geometry.difference(tronconsUnion);
		if (geometry == null) geometry = this.getGeom();

		if (geometry == null) { return 0;}
		double resultat = (geometry.area() <= 1) ? 0 :
		    surfaceBatiments/geometry.area();
		if (logger.isDebugEnabled()) logger.debug("densité = "+resultat);
		return resultat;
	}
	//double densiteInitiale = 0.0;
	/**
	 * @return
	 */
	//public double getDensiteInitiale() {return densiteInitiale;}

	double densiteBut = -1.0;
	/**
	 * @return
	 */
	public double getDensiteBut() {
	    if (densiteBut == -1.0) {
	        return this.getDensite();
	    }
	    return densiteBut;
	}
    public void setDensiteBut(double d) { this.densiteBut = d; }

    public double getDensiteMax() {
        double max = this.getDensite();
        for (AgentZoneElementaireBatie voisin : this.getVoisins()) {
            max = Math.max(max, voisin.getDensite());
        }
        return max;
    }
    
    public double getDensiteMaxRegulee() {
      double max = this.getDensiteMax();
      double area = this.getGeom().area();
      double surfaceInit = this.getDensite() * area;
      double surfaceMax = max * area;
      int nbAnnees = this.getDateSimulee() - this.getDateDebutSimulation();
      if (nbAnnees == 0) {
        logger.info(nbAnnees);
        nbAnnees = 10;
      }
      double surfaceRegulee = Math.min(surfaceMax, surfaceInit + 200 * nbAnnees);
      return Math.max(surfaceRegulee / area, this.getDensite());
    }

    public double getWeighedDensity() {
        double sum = 0;
        double lengthSum = 0;
        for (AgentZoneElementaireBatie voisin : this.getVoisins()) {
            Set<AgentTroncon> tronconsVoisin = voisin.getTroncons();
            Set<AgentTroncon> intersection = new HashSet<AgentTroncon>();
            intersection.addAll(this.getTroncons());
            intersection.retainAll(tronconsVoisin);
            for (AgentTroncon troncon : intersection) {
                double length = troncon.getGeom().length();
                lengthSum += length;
                sum += voisin.getDensite() * length;
                logger.debug("length = " + length + " - density = " + voisin.getDensite());
            }
        }
        double length = this.getGeom().length();
        sum += this.getDensite() * length;
        lengthSum += length;
        double weightedSum = sum / lengthSum;
        logger.debug("lengthSum = " + lengthSum + " - length = " + this.getGeom().length() + " - sum = " + sum + " - weighted sum = " + weightedSum + " - initial density = " + this.getDensite());
        if (weightedSum > this.getDensite()) {
            return weightedSum;
        }
        return this.getDensite();
    }

	//HomogeneiteTypeFonctionnelBatiments
	int homogeneiteTypesFonctionnelsBatiments = 0;
	/**
	 * @return homogénéité des types fonctionnels des bâtiments
	 */
	public int getHomogeneiteTypesFonctionnelsBatiments() {return homogeneiteTypesFonctionnelsBatiments;}

	/**
	 * @param homogeneiteTypesFonctionnelsBatiments homogénéité des types fonctionnels des bâtiments
	 */
	public void setHomogeneiteTypesFonctionnelsBatiments(int homogeneiteTypesFonctionnelsBatiments) {this.homogeneiteTypesFonctionnelsBatiments = homogeneiteTypesFonctionnelsBatiments;}


	int homogeneiteTypesFonctionnelsBatimentsBut = HomogeneiteTypeFonctionnelBatiments.QuasiHomogeneIndustriel;
	/**
	 * @return
	 */
	public int getHomogeneiteTypesFonctionnelsBatimentsBut() {return homogeneiteTypesFonctionnelsBatimentsBut;}

	// Méthode de peuplement
	String methodePeuplement = "SansInformation";
	/**
	 * @return la valeur de la methode de peuplement
	 */
	public String getMethodePeuplement() {return this.methodePeuplement;}

	public String choixMethodePeuplement(){
		double aleaChoix = Math.random() * 100;
		double frequenceCumulee = 0;
		double frequenceCumuleePrecedente = 0;
		String bestMethode = null;
		for (TypeMethode typeM : typePeuplement.getParametresPeuplement()) {
			frequenceCumuleePrecedente = frequenceCumulee;
			frequenceCumulee += typeM.getPourcentage();
			if ((aleaChoix > frequenceCumuleePrecedente)
					&& (aleaChoix < frequenceCumulee)) {
				bestMethode = typeM.getNomMethodePeuplement();
				break;
			}
		}	
		this.methodePeuplement = bestMethode;
		logger.debug("Méthode de peuplement choisie: "+methodePeuplement);
		return this.methodePeuplement;
	}

	public void setMethodePeuplement(String methodePeuplement){this.methodePeuplement = methodePeuplement;}

	public ParametresMethodesPeuplement getParametresMethodesPeuplement() {
	    return ConfigurationMethodesPeuplement.getInstance().
        getParametresMethodesPeuplement(this.getMethodePeuplement());
	}
	public ParametresForme getParametresForme(int typeFonctionnel) {
        ParametresMethodesPeuplement parametresPeuplement = this.getParametresMethodesPeuplement();

        logger.debug("Methode de peuplement = "+this.getMethodePeuplement());
        List<ParametresForme> listeParametresForme = new ArrayList<ParametresForme>();
        // Si il y a des formes de bâtiment pour cette méthode de peuplement
        if (!parametresPeuplement.getFormeBatiment().isEmpty()) {
            listeParametresForme = parametresPeuplement.getFormeBatiment();
        } else {// On Détermine la forme en fonction du type fonctionnel
            listeParametresForme = ConfigurationFormesBatimentsV2.getInstance().getParametres(typeFonctionnel);
        }

        double aleaChoix = Math.random() * 100;
        logger.debug("choix = "+aleaChoix);
        double frequenceCumulee = 0;
        double frequenceCumuleePrecedente = 0;
        ParametresForme bestParameters = null;
        for (ParametresForme param : listeParametresForme) {
            frequenceCumuleePrecedente = frequenceCumulee;
            frequenceCumulee += param.getFrequence();
            if ((aleaChoix > frequenceCumuleePrecedente)
                    && (aleaChoix < frequenceCumulee)) {
                bestParameters = param;
                logger.debug("choisie = "+param.getForme());
                break;
            }
        }
        if (bestParameters == null) {
            logger.error("Aucune forme choisie, vérifier les paramètres de la méthode");
            return listeParametresForme.get(0);
        }
        return bestParameters;
	}

	private double distanceMoyennePlusProcheBatiment=0.0;
	/**
	 * @return distanceMoyennePlusProcheBatiment
	 */
	public double getDistanceMoyennePlusProcheBatiment() {return this.distanceMoyennePlusProcheBatiment;}
	/**
	 * @param distanceMoyennePlusProcheBatiment
	 */
	public void setDistanceMoyennePlusProcheBatiment(double distanceMoyennePlusProcheBatiment) {this.distanceMoyennePlusProcheBatiment = distanceMoyennePlusProcheBatiment;}

	//Classification fonctionnelle Urbaine
	protected int classificationFonctionnelle = ClasseUrbaine.Inconnu;
	/**
	 * @return classificationFonctionnelle
	 */
	public int getClassificationFonctionnelle() {return classificationFonctionnelle;}
	/**
	 * @param classificationFonctionnelle classificationFonctionnelle à Définir
	 */
	public void setClassificationFonctionnelle(int classificationFonctionnelle) {this.classificationFonctionnelle = classificationFonctionnelle;}

	protected int classificationFonctionnelleBut = ClasseUrbaine.Inconnu;

	public int getClassificationFonctionnelleBut() {
	    if (classificationFonctionnelleBut == ClasseUrbaine.Inconnu) {
	        return this.getClassificationFonctionnelle();
	    }
	    return classificationFonctionnelleBut;
	}
	public void setClassificationFonctionnelleBut(int d) { 
    	this.classificationFonctionnelleBut = d; 
    	String nomClasseUrbaine = ClasseUrbaine.toString(this.getClassificationFonctionnelleBut());
        logger.debug("Classification fonctionnelle initiale : "+ClasseUrbaine.toString(this.getClassificationFonctionnelle()));
		logger.debug("Classification fonctionnelle objectif : "+nomClasseUrbaine);
		ConfigurationLienTypeFonctionnelMethodePeuplement configurationLien = ConfigurationLienTypeFonctionnelMethodePeuplement.getInstance();
		List<TypePeuplement> typesP = configurationLien.getTypesPeuplement(nomClasseUrbaine);
		if (typesP!=null){
			logger.debug("Nombre de type de peuplement : "+typesP.size());
			double aleaChoix = Math.random() * 100;
	        double frequenceCumulee = 0;
	        double frequenceCumuleePrecedente = 0;
	        TypePeuplement bestType = null;
	        for (TypePeuplement typ : typesP) {
	            frequenceCumuleePrecedente = frequenceCumulee;
	            frequenceCumulee += typ.getFrequence();
	            if ((aleaChoix > frequenceCumuleePrecedente)
	                    && (aleaChoix < frequenceCumulee)) {
	                bestType = typ;
	                break;
	            }
	        }
            this.setTypePeuplement(bestType);
		}
		String str = "";
        for (TypeMethode typeM:typePeuplement.getParametresPeuplement()){
        	if(!str.equals(""))str += " + ";
        	str += typeM.getNomMethodePeuplement() +" ("+typeM.getPourcentage()+"%)";
        }
        logger.debug("Type de peuplement choisi = "+str);
    }
    
    TypePeuplement typePeuplement = new TypePeuplement(100, Arrays.asList(new TypeMethode(100, "SansInformation")));
	/**
	 * @return la valeur de l'attribut typePeuplement
	 */
	public TypePeuplement getTypePeuplement() {return this.typePeuplement;}
	/**
	 * @param typePeuplement l'attribut typePeuplement à affecter
	 */
	public void setTypePeuplement(TypePeuplement typePeuplement) {this.typePeuplement = typePeuplement;}

	public int getClassificationFonctionnelleMajo() {
        int classificationFonctionnelleMajo = this.getClassificationFonctionnelle();
        int nbMajo = 0;
        logger.debug("classif majo init : "+classificationFonctionnelleMajo);
        Map<Integer,Integer> mapClasse = new HashMap<Integer, Integer>();
        if ((classificationFonctionnelleMajo!=ClasseUrbaine.ReseauCommunication)){
        	for (AgentZoneElementaireBatie voisin : this.getVoisins()) {
                logger.debug("classif voisin : "+voisin.getClassificationFonctionnelle());
            	if(voisin.getClassificationFonctionnelle()==classificationFonctionnelleMajo){
            		nbMajo++;
            	}
            	if(mapClasse.containsKey(voisin.getClassificationFonctionnelle())){
            		mapClasse.put(voisin.getClassificationFonctionnelle(),mapClasse.get(voisin.getClassificationFonctionnelle())+1);
            	}else{
            		mapClasse.put(voisin.getClassificationFonctionnelle(),1);
            	}
            }
            for (Integer val:mapClasse.keySet()){
            	logger.debug("type "+val+" avec : " +mapClasse.get(val));
            	if ((mapClasse.get(val)>nbMajo)&&(val!=ClasseUrbaine.ReseauCommunication)){
            		nbMajo = mapClasse.get(val);
            		classificationFonctionnelleMajo = val;
            	}
            }
        }
        logger.debug("classif majo fin : "+classificationFonctionnelleMajo);
        return classificationFonctionnelleMajo;
    }

	@Override
	public ElementRepresentation construireRepresentationCourante() {
		ZoneElementaireUrbaine zone = (ZoneElementaireUrbaine) super.construireRepresentationCourante();
		zone.setUniteUrbaine((UniteUrbaine) this.getUniteBatie().getRepresentationCourante());
		zone.setDateSourceSaisie(this.getDateSimulee());
		zone.setClassificationFonctionnelle(this.getClassificationFonctionnelle());
		zone.setDensite(this.getDensite());
		// HomogeneiteTypesFonctionnelsBatiments
		zone.setHomogeneiteTypesFonctionnelsBatiments(this.calculHomogeneiteTypesFonctionnelsBatiments());
		// DistanceMoyennePlusProcheBatiment
		zone.setDistanceMoyennePlusProcheBatiment(this.calculDistanceMoyennePlusProcheBatiment());
		for(AgentTroncon agentTroncon:this.getTroncons()) {
			Troncon troncon = (Troncon) agentTroncon.construireRepresentationCourante();
			zone.addTroncon(troncon);
			troncon.addZoneElementaire(zone);
		}
		for(AgentEspaceVide agentEspaceVide:this.getEspacesVides()) {
			EspaceVide espaceVide=(EspaceVide) agentEspaceVide.construireRepresentationCourante();
			zone.addEspaceVide(espaceVide);
			espaceVide.setZoneElementaireUrbaine(zone);
		}
		// les voisins
		//ZoneElementaireUrbaine repCourante = (ZoneElementaireUrbaine) this.getRepresentationCourante();
		for(AgentZoneElementaireBatie agentZoneElementaireVoisine:this.getVoisins()) {
			ZoneElementaireUrbaine zoneElementaireVoisine=(ZoneElementaireUrbaine) agentZoneElementaireVoisine.getRepresentationCourante();
			zone.addVoisin(zoneElementaireVoisine);
			//if (repCourante!=null) zoneElementaireVoisine.getVoisins().remove(repCourante);
		}
		// les groupes de batiments
		logger.debug("nbgrbat : " + this.getGroupesBatiments().size());
		for(AgentGroupeBatiments agentGroupeBatiments:this.getGroupesBatiments()) {
			GroupeBatiments groupeBatiments=(GroupeBatiments) agentGroupeBatiments.construireRepresentationCourante();
			zone.addGroupeBatiments(groupeBatiments);
			groupeBatiments.setZoneElementaireUrbaine(zone);
//			zone.addAllBatiment(groupeBatiments.getBatiments());
		}
		// FIXME Bidouille
		zone.setBordeUniteUrbaine(this.infinite);
		if (logger.isDebugEnabled()) {
		    logger.debug("Agent possédant "+this.getTroncons().size()+" troncons "+this.getGroupesBatiments().size()+" groupes");
		    logger.debug("représentation possédant "+zone.getTroncons().size()+" troncons "+zone.getGroupesBatiments().size()+" groupes");
		}
		return zone;
	}

	@Override
	public List<AgentGeographique> getComposants() {
		List<AgentGeographique> composants = new ArrayList<AgentGeographique> ();
		//	    composants.addAll(this.getBatiments());
		composants.addAll(this.getTroncons());
		composants.addAll(this.getEspacesVides());
		composants.addAll(this.getGroupesBatiments());
		return composants;
	}
	@Override
	public void removeComposant(AgentGeographique composantASupprimer) {
		//		this.getBatiments().remove(composantASupprimer);
		this.getTroncons().remove(composantASupprimer);
		this.getEspacesVides().remove(composantASupprimer);

		if (composantASupprimer instanceof AgentGroupeBatiments){
			Set<AgentGroupeBatiments> listeGBPrecedent = new HashSet<AgentGroupeBatiments>();
			for (AgentGroupeBatiments agentGB:this.getGroupesBatiments())listeGBPrecedent.add(agentGB);

			this.getGroupesBatiments().remove(composantASupprimer);

			Set<AgentGroupeBatiments> listeGBSuivant = new HashSet<AgentGroupeBatiments>();
			for (AgentGroupeBatiments agentGB:this.getGroupesBatiments())listeGBSuivant.add(agentGB);
			AgentGeographiqueCollection collection = AgentGeographiqueCollection.getInstance();
			collection.fireActionPerformed(new AgentCollectionEvent(
					collection,
					this,
					FeatureCollectionEvent.Type.CHANGED,
					"GroupesBatiments",
					listeGBPrecedent,
					listeGBSuivant));
		}
	}

	IPoint pointLePlusLoinDeLaRoute = null;
	public IPoint getPointLePlusLoinDeLaRoute() {return this.pointLePlusLoinDeLaRoute;}
	public void setPointLePlusLoinDeLaRoute(IPoint pointLePlusLoinDeLaRoute) {this.pointLePlusLoinDeLaRoute = pointLePlusLoinDeLaRoute;}

    private AbstractTriangulation roadTriangulation = null;
    private IFeatureCollection<AgentTroncon> tronconsRoutiers = null;
    public void buildRoadTriangulation() {
        if (this.tronconsRoutiers == null) {
            this.tronconsRoutiers = new FT_FeatureCollection<AgentTroncon>();
            for(AgentTroncon troncon : this.getTroncons()) {
                if ((troncon instanceof AgentTronconRoute)
                        || (troncon instanceof AgentTronconChemin)) {
                    this.tronconsRoutiers.add(troncon);
                }
            }
        }
        try {
          if (this.roadTriangulation != null) {
//            this.roadTriangulation = new TriangulationJTS("RoadTriangulation");
//          } else {
            this.roadTriangulation.nettoyer();
          }
          this.roadTriangulation = new TriangulationJTS("RoadTriangulation");
          ChargeurTriangulation.importSegments(this.tronconsRoutiers,
              this.roadTriangulation);
          if (this.roadTriangulation.getPopNoeuds().size() <= 2) { return; }
          this.roadTriangulation.triangule("czevBQ"); // Ajouter v pour activer le voronoi Q pour quiet
        } catch (Exception e1) {
            this.roadTriangulation = null;
            e1.printStackTrace();
        }
    }

	double distanceALaRoute = 0;
	public void setDistanceALaRoute(double distanceALaRoute) {this.distanceALaRoute = distanceALaRoute;}
	public double getDistanceALaRoute() {
		if (this.pointLePlusLoinDeLaRoute == null) {
		    // distance max à la route
		    this.buildRoadTriangulation();
			this.distanceALaRoute=0;
			if (this.roadTriangulation != null) {
			    for(Noeud noeudVoronoi:roadTriangulation.getPopVoronoiVertices()) {
			        if (this.getGeom().contains(noeudVoronoi.getGeom())) {
			            double distance = noeudVoronoi.getGeom().distance(tronconsRoutiers.getGeomAggregate());
			            if (distance>this.distanceALaRoute) {
			                this.distanceALaRoute=distance;
			                this.pointLePlusLoinDeLaRoute = noeudVoronoi.getGeometrie();
			            }
			        }
			    }
			    if (logger.isDebugEnabled()) {
			        logger.debug("distance = "+distanceALaRoute);
			    }
			} else {
	             if (logger.isDebugEnabled()) {
	                    logger.debug("Road Triangulation failed");
	             }
			}
		}
		return distanceALaRoute;
	}

    private AbstractTriangulation buildingTriangulation = null;
	public AbstractTriangulation getBuildingTriangulation() {
        return this.buildingTriangulation;
    }
    @SuppressWarnings("unchecked")
    public void buildBuildingTriangulation() {
        if (this.buildingTriangulation != null) {
//            this.buildingTriangulation = new TriangulationJTS("BuildingTriangulation");
//        } else {
//            if (this.buildingTriangulation.getPopNoeuds() != null) {
//                this.buildingTriangulation.getPopNoeuds().clear();
//            } else {
//                logger.error("Null node population");
//            }
//            if (this.buildingTriangulation.getPopArcs() != null) {
//                this.buildingTriangulation.getPopArcs().clear();
//            } else {
//                logger.error("Null edge population : " + I18N.getString("CarteTopo.Edge"));
//                this.buildingTriangulation.addPopulation(
//                        new Population<ArcDelaunay>(false,
//                        I18N.getString("CarteTopo.Edge"), //$NON-NLS-1$
//                        ArcDelaunay.class, true));
//                if (this.buildingTriangulation.getPopArcs() == null) {
//                    logger.error("Null edge population again");
//                    for (Population<?> p : this.buildingTriangulation.getPopulations()) {
//                        logger.error(p.getNom());
//                    }
//                }
//            }
//            if (this.buildingTriangulation.getPopFaces() != null) {
//              this.buildingTriangulation.getPopFaces().clear();
//            } else {
//              logger.error("Null face population");
//            }
          this.buildingTriangulation.nettoyer();
        }
        this.buildingTriangulation = new TriangulationJTS("BuildingTriangulation");
        if (this.roadTriangulation == null) {
	        this.buildRoadTriangulation();
	    }
	    if (this.roadTriangulation == null) {
	        return;
	    }
	    for (AgentBatiment batiment : this.getBatiments()) {
            Map<AgentTroncon, Double> map = new HashMap<AgentTroncon, Double>();
            double minDistance = Double.MAX_VALUE;
            AgentTroncon closestTroncon = null;
	        for (AgentTroncon troncon : this.getTroncons()) {
	            double distance = batiment.getGeom().distance(troncon.getGeom());
	            map.put(troncon, new Double(distance));
	            if (distance < minDistance) {
	                minDistance = distance;
	                closestTroncon = troncon;
	            }
	        }
	        if (closestTroncon == null || minDistance > 30) { continue; }
	        map.remove(closestTroncon);
	        ILineString line = (ILineString) closestTroncon.getGeom();
	        IDirectPosition projection = Operateurs.projection(batiment
                    .getGeom().centroid(), line);
	        if (!projection.equals(line.getControlPoint(0))
	                && !projection.equals(line.getControlPoint(line.sizeControlPoint() - 1))) {
	            Noeud node = this.buildingTriangulation.getPopNoeuds()
	            .nouvelElement(new GM_Point(projection));
	            node.addCorrespondant(closestTroncon);
	            if (batiment.getGeom().intersects(this.roadTriangulation
	                    .getPopVoronoiEdges().getGeomAggregate())) {
	                // the building is close to 2 edges
	                minDistance = Double.MAX_VALUE;
	                for (Entry<AgentTroncon, Double> entry : map.entrySet()) {
	                    if (entry.getValue() < minDistance) {
	                        minDistance = entry.getValue();
	                        closestTroncon = entry.getKey();
	                    }
	                }
	                if (closestTroncon == null || minDistance > 30) { continue; }
	                line = (GM_LineString) closestTroncon.getGeom();
	                projection = Operateurs.projection(batiment
	                        .getGeom().centroid(), line);
	                if (!projection.equals(line.getControlPoint(0))
	                        && !projection.equals(line.getControlPoint(line.sizeControlPoint() - 1))) {
	                    node = this.buildingTriangulation.getPopNoeuds()
	                    .nouvelElement(new GM_Point(projection));
	                    node.addCorrespondant(closestTroncon);
	                }
	            }
	        }
	    }
	    try {
            ChargeurTriangulation.importCentroidesPolygones(
                    this.getBatiments(), this.buildingTriangulation);
            if (logger.isDebugEnabled()) {
                logger.debug("Edges before "+this.buildingTriangulation.getPopArcs().size());
            }
            this.buildingTriangulation.triangule();
            if (logger.isDebugEnabled()) {
                logger.debug("Edges before "+this.buildingTriangulation.getPopArcs().size());
            }
            this.updateProximity();
            DataSet.getInstance().getPopulation("Triangulation").clear();
            Population<Arc> triangulationPopulation = (Population<Arc>) DataSet
            .getInstance().getPopulation("Triangulation");
            for (Arc arc : this.buildingTriangulation.getPopArcs()) {
                if (this.getGeom().intersects(arc.getGeom())
                            && (arc.getNoeudIni().getCorrespondant(0) instanceof AgentBatiment
                                        || arc.getNoeudFin().getCorrespondant(0) instanceof AgentBatiment)) {
                    triangulationPopulation.add(arc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public void updateProximity() {
        for (Arc arc : this.buildingTriangulation.getPopArcs()) {
//            arc.setPoids(arc.getNoeudIni().getCorrespondant(0).getGeom()
//                    .distance(arc.getNoeudFin().getCorrespondant(0)
//                            .getGeom()));        
            arc.setPoids(arc.getGeometrie().length());
        }
	}

	public Map<AgentBatiment, NoeudDelaunay> getDisplaceableNodes() {
        Map<AgentBatiment, NoeudDelaunay> displaceableNodes
        = new HashMap<AgentBatiment, NoeudDelaunay>();
        for (Noeud node : this.getBuildingTriangulation().getPopNoeuds()) {
            IFeature feature = node.getCorrespondant(0);
            if ((feature instanceof AgentBatiment)
                    && ((AgentGeographique) feature).isSimulated()) {
                displaceableNodes.put((AgentBatiment) feature,
                        (NoeudDelaunay) node);
            }
        }
        return displaceableNodes;
	}

	public double getSatisfactionProximity() {
	    if (this.buildingTriangulation == null) {
	        this.buildBuildingTriangulation();
	    }
	    Map<AgentBatiment, NoeudDelaunay> displaceableNodes = this.getDisplaceableNodes();
        double distOver = 0;
        int numberOfConstraints = 0;
        for (AgentBatiment batiment : displaceableNodes.keySet()) {
            NoeudDelaunay node = displaceableNodes.get(batiment);
            List<Arc> edges = node.arcs();
            for (Arc edge : edges) {
                numberOfConstraints++;
                NoeudDelaunay otherNode
                = (NoeudDelaunay) edge.getNoeudIni();
                if (otherNode.equals(node)) {
                    otherNode = (NoeudDelaunay) edge.getNoeudFin();
                }
                IFeature feature = otherNode.getCorrespondant(0);
                double distance = edge.getPoids();
                if (feature instanceof AgentBatiment) {
                    if (distance
                            < this.getParametresMethodesPeuplement().getDistanceBatiment().getMoyenne()) {
                        distOver
                        += (distance
                                / this.getParametresMethodesPeuplement().getDistanceBatiment().getMoyenne());
                    } else {
                        distOver += 1;
                    }
                } else {
                    if (feature instanceof AgentTroncon) {
                        if (distance
                                < this.getParametresMethodesPeuplement().getDistanceRoute().getMoyenne()) {
                            distOver
                            += (distance
                                    / this.getParametresMethodesPeuplement().getDistanceRoute().getMoyenne());
                        } else {
                            distOver += 1;
                        }
                    }
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("distOver = " + distOver + " numberOfConstraints = " + numberOfConstraints);
            logger.debug("satisfaction = " + distOver * 100.0 / numberOfConstraints);
        }
        if (numberOfConstraints == 0) {
            return 100;
        }
	    return distOver * 100.0 / numberOfConstraints;
	}
	private double roadDistance = 40;
	private double roadSubtractionDistance = 10;
	private double buildingDistance = 15;

	/**
	 * @return
	 */
	public IGeometry getZoneConstructible() {
		// Récupération des valeurs des Méthodes de peuplement
		String methodePeuplementUtilisee = this.getMethodePeuplement();
		ParametresMethodesPeuplement parametresPeuplement = ConfigurationMethodesPeuplement.getInstance().getParametresMethodesPeuplement(methodePeuplementUtilisee);
		if (parametresPeuplement.getDistanceRoute().getMoyenne()!=-1){
			roadSubtractionDistance = parametresPeuplement.getDistanceRoute().getMoyenne();
		}
		if (parametresPeuplement.getDistanceBatiment().getMoyenne()!=-1){
			buildingDistance = parametresPeuplement.getDistanceBatiment().getMoyenne();
		}
		// construction des zones constructibles
		IGeometry zoneConstructible = null;
		if (logger.isDebugEnabled()) logger.debug(this.getTroncons().size()+" troncons");
		for(AgentTroncon troncon:this.getTroncons()) {
			if ((troncon instanceof AgentTronconRoute)||(troncon instanceof AgentTronconChemin)){
			    IGeometry buffer = troncon.getGeom().buffer(roadDistance);
				if (zoneConstructible==null) zoneConstructible=buffer;
				else zoneConstructible=zoneConstructible.union(buffer);
			}
		}
		if (zoneConstructible != null) {
		    IGeometry buildingsBuffer = JtsUtil.bufferPolygones(this.getBatiments(), buildingDistance);
			if (buildingsBuffer!=null) zoneConstructible=zoneConstructible.difference(buildingsBuffer);
			zoneConstructible=zoneConstructible.intersection(this.getGeom());
		}
		for(AgentTroncon troncon:this.getTroncons()) {
		    IGeometry buffer = troncon.getGeom().buffer(roadSubtractionDistance);
			if (zoneConstructible != null) zoneConstructible=zoneConstructible.difference(buffer);
		}
//		logger.debug("zone Constructible = "+zoneConstructible);
		return zoneConstructible;
	}

	/**
	 * @return
	 */
	public IGeometry getZoneInaccessible() {
	    IGeometry zoneInaccessible = this.getGeom();
		for(AgentTroncon troncon:this.getTroncons()) {
			if ((troncon instanceof AgentTronconRoute)||(troncon instanceof AgentTronconChemin)){
			    IGeometry buffer = troncon.getGeom().buffer(roadDistance);
				if (buffer!=null) zoneInaccessible = zoneInaccessible.difference(buffer);
			}
		}
		IGeometry zoneConstructible = getZoneConstructible();
		if (zoneConstructible!=null) zoneInaccessible = zoneInaccessible.difference(zoneConstructible);
		return zoneInaccessible;
	}

	public double getSatisfactionTypesBatiments(){
		double satisfactionTB = 0;
		double aireBatimentOK = 0;
		double aireBatimentTotal = 0;
		double percET = 10/100;
		// On récupère la Méthode de peuplement et ses paramètres
		String methodePeuplementUtilise = this.getMethodePeuplement();
		ParametresMethodesPeuplement parametresPeuplement = ConfigurationMethodesPeuplement.getInstance().getParametresMethodesPeuplement(methodePeuplementUtilise);
		int typeF = parametresPeuplement.getTypeFonctionnel();
		for (AgentBatiment bati : this.getBatiments()){
			double aireBati = bati.getGeom().area();
			// discrimination sur le type fonctionnel du batiment
			if ((bati.getTypeFonctionnel()!=typeF)&&(typeF!=TypeFonctionnel.Quelconque)){
				aireBatimentTotal += aireBati;
			}else{// discrimination sur la taille du batiment
				boolean aireOK = true;
				if(!parametresPeuplement.getFormeBatiment().isEmpty()){
					aireOK = false;
					for (ParametresForme paramF : parametresPeuplement.getFormeBatiment()){
						double aireMoy = paramF.getTailleBatiment().getMoyenne();
						if (aireMoy!=-1){
							double aireET = paramF.getTailleBatiment().getEcartType();
							if (aireET==-1){aireET = aireMoy*percET;}
							if((aireBati>aireMoy-2*aireET)&&(aireBati<aireMoy+2*aireET)){aireOK = true;}
						}else{aireOK = true;}
					}
				}
				if (aireOK==false){aireBatimentTotal += aireBati;}
				else{
					aireBatimentOK += aireBati;
					aireBatimentTotal += aireBati;
				}
			}
		}
		if (aireBatimentTotal!=0)satisfactionTB = 100*(aireBatimentOK/aireBatimentTotal);
		return satisfactionTB;
	}

	/**
	 * Calcul de la distance moyenne au batiment le plus proche
	 */
	public double calculDistanceMoyennePlusProcheBatiment(){
		double distanceMoyennePPBatiment = 0;
		if (this.getBatiments().size()>1){
			for(AgentBatiment batiment:this.getBatiments()){
				distanceMoyennePPBatiment += batiment.getDistanceBatimentLePlusProche();
			}
			distanceMoyennePPBatiment/=this.getBatiments().size();
		}
		return distanceMoyennePPBatiment;
	}

	/**
	 * Calcul de l'homogénéité des types fonctionels des bâtiments contenus dans une zone élémentaire
	 */
	public int calculHomogeneiteTypesFonctionnelsBatiments(){
		int nbBatimentsHabitat = 0;
		int nbBatimentsPublic = 0;
		int nbBatimentsIndustriel = 0;
		for(AgentBatiment batiment:this.getBatiments()) {
			if (batiment.getTypeFonctionnel()==TypeFonctionnel.Habitat) nbBatimentsHabitat++;
			else if (batiment.getTypeFonctionnel()==TypeFonctionnel.Public) nbBatimentsPublic++;
			else if (batiment.getTypeFonctionnel()==TypeFonctionnel.Industriel) nbBatimentsIndustriel++;
		}
		int nbBatTot = this.getBatiments().size();
		int homogeneite = HomogeneiteTypeFonctionnelBatiments.Heterogene;
		if (nbBatTot == 0){
			homogeneite = HomogeneiteTypeFonctionnelBatiments.Vide;
		} else if (nbBatimentsHabitat==nbBatTot) {
			homogeneite = HomogeneiteTypeFonctionnelBatiments.HomogeneHabitat;
		} else if (nbBatimentsPublic==nbBatTot) {
			homogeneite = HomogeneiteTypeFonctionnelBatiments.HomogenePublic;
		} else if (nbBatimentsIndustriel==nbBatTot) {
			homogeneite = HomogeneiteTypeFonctionnelBatiments.HomogeneIndustriel;
		} else if (100*nbBatimentsHabitat/nbBatTot>70) {
			homogeneite = HomogeneiteTypeFonctionnelBatiments.QuasiHomogeneHabitat;
		} else if (100*nbBatimentsPublic/nbBatTot>70) {
			homogeneite = HomogeneiteTypeFonctionnelBatiments.QuasiHomogenePublic;
		} else if (100*nbBatimentsIndustriel/nbBatTot>70) {
			homogeneite = HomogeneiteTypeFonctionnelBatiments.QuasiHomogeneIndustriel;
		}
		if (logger.isDebugEnabled()) logger.debug("homogénéité du Type fonctionnel de la zone élémentaire = "+homogeneite);
		return homogeneite;
	}
	public double getNormalLaw() {
	    double sigma = 0.3;
	    double currentDensity = this.getDensite();
	    double goalDensity = this.getDensiteBut();
	    double difference = currentDensity - goalDensity;
	    double result = 100.0
        * Math.exp(-(difference * difference) / (2 * sigma * sigma));
//	    if (logger.isTraceEnabled()) {
	    logger.debug("NormalLaw : " + currentDensity + " " + goalDensity
	        + " result = " + result);
//	    }
	    return result;
	    /// (sigma * Math.sqrt(2 * Math.PI));
	}
	@Override
	public String toString() {
	    return "AgentZoneElementaireBatie [densiteBut=" + this.densiteBut
		    //+ ", densiteInitiale=" + this.densiteInitiale
		    + ", homogeneiteTypesFonctionnelsBatimentsBut=" + this.homogeneiteTypesFonctionnelsBatimentsBut
		    + ", homogeneiteTypesFonctionnelsBatiments=" + this.homogeneiteTypesFonctionnelsBatiments
		    + ", espacesVides=" + this.espacesVides
		    + ", groupesBatiments=" + this.groupesBatiments
		    + ", troncons=" + this.troncons + ", uniteBatie="
		    + this.uniteBatie + "]";
	}
	public double getPourcentageMethodePeuplement(String name) {
	  for (TypeMethode type : this.getTypePeuplement().getParametresPeuplement()) {
	    if (type.getNomMethodePeuplement().equalsIgnoreCase(name)) {
	      return type.getPourcentage();
	    }
	  }
	  return 0.0;
	}
    public double getIndividuelSpontanne() {
      return this.getPourcentageMethodePeuplement("IndividuelSpontanne");
    }
    public double getIndividuelProgramme() {
      return this.getPourcentageMethodePeuplement("IndividuelProgramme");
    }
    public double getMaisonQuartierOuvrier() {
      return this.getPourcentageMethodePeuplement("MaisonQuartierOuvrier");
    }
    public double getMaisonQuartierClasseMoyenne() {
      return this.getPourcentageMethodePeuplement("MaisonQuartierClasseMoyenne");
    }
    public double getMaisonQuartierClasseIntermediaire() {
      return this.getPourcentageMethodePeuplement("MaisonQuartierClasseIntermediaire");
    }
    public double getMaisonQuartierAise() {
      return this.getPourcentageMethodePeuplement("MaisonQuartierAise");
    }
    public double getPetitCollectif() {
      return this.getPourcentageMethodePeuplement("PetitCollectif");
    }
    public double getBarre() {
      return this.getPourcentageMethodePeuplement("Barre");
    }
    public double getGrandEnsemble() {
      return this.getPourcentageMethodePeuplement("GrandEnsemble");
    }
    public double getZoneIndustrielle() {
      return this.getPourcentageMethodePeuplement("ZoneIndustrielle");
    }
    public double getZoneIndustrielleAnnabelle() {
      return this.getPourcentageMethodePeuplement("ZoneIndustrielleAnnabelle");
    }
    public double getSansInformation() {
      return this.getPourcentageMethodePeuplement("SansInformation");
    }
    private boolean infinite = false;
    public boolean isInfinite() {
      return this.infinite;
    }
    public void setInfinite(boolean infinite) {
      this.infinite = infinite;
    }
    public double getDifferenceDensite() {
      return Math.abs(this.getDensite() - this.getDensiteBut());
    }
}
