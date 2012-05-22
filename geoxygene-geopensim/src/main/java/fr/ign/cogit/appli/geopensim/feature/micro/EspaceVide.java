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
package fr.ign.cogit.appli.geopensim.feature.micro;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.algo.JtsUtil;
import fr.ign.cogit.geoxygene.util.algo.MesureOrientation;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;


/**
 * @author Julien Perret
 *
 */
@Entity
public class EspaceVide extends MicroRepresentation {
	static Logger logger=Logger.getLogger(EspaceVide.class.getName());
	/**
	 * Constructeur d'espace vides par Défaut.
	 */
	public EspaceVide() {super();}
	
	/**
	 * Constructeur d'espace vides par copie.
	 */
	public EspaceVide(EspaceVide espaceVide) {
		setGeom((GM_Object)espaceVide.getGeom().clone());
		setFeatureType(espaceVide.getFeatureType());
		setNature(espaceVide.getNature());
		setSource(espaceVide.getSource());
		setTopo(espaceVide.getTopo());
		setOuvert(espaceVide.isOuvert());
	}

	/**
	 * Constructeur d'espaces vides à partir d'une géométrie
	 * @param geom géométrie de l'espace vide
	 */
	public EspaceVide(IGeometry geom) {super(geom);}
	/**
	 * Construit un nouvel espace vide
	 * @return un nouvel espace vide
	 */
	public static EspaceVide newInstance() {return new EspaceVide();}
	/**
	 * Construit un nouvel espace vide à partir d'une géométrie
	 * @param geom géométrie de l'espace vide
	 * @return un nouvel espace vide à partir d'une géométrie
	 */
	public static EspaceVide newInstance(IGeometry geom) {return new EspaceVide(geom);}	

	/**
	 * Zone élémentaire urbaine à laquelle appartient l'espace vide.
	 */
	protected ZoneElementaireUrbaine zoneElementaireUrbaine = null;
	/**
	 * Renvoie la zone élémentaire urbaine à laquelle appartient l'espace vide.
	 * @return Zone élémentaire urbaine à laquelle appartient l'espace vide
	 * @see #setZoneElementaireUrbaine(ZoneElementaireUrbaine)
	 */
	@ManyToOne
	public ZoneElementaireUrbaine getZoneElementaireUrbaine() {return zoneElementaireUrbaine;}
	/**
	 * Détermine la zone élémentaire urbaine à laquelle appartient l'espace vide.
	 * @param zone Zone élémentaire urbaine à laquelle appartient l'espace vide
	 * @see #getZoneElementaireUrbaine()
	 * 
	 */
	public void setZoneElementaireUrbaine(ZoneElementaireUrbaine zone) {this.zoneElementaireUrbaine = zone;}

	protected double importanceRelative;
	/*
	 * La valeur Booléenne est vraie si l'espace vide est fermé et fausse s'il est ouvert.
	 */
	protected boolean ouvert=false;
	protected double orientationPrincipale;
	protected double elongation;
	/**
	 * Renvoie l'importance relative de l'espace vide par rapport à la zone élémentaire urbaine à laquelle il appartient.
	 * C'est le rapport de l'aire de l'espace vide sur l'aire de la zone élémentaire urbaine à laquelle il appartient.
	 * @return l'importance relative de l'espace vide par rapport à la zone élémentaire urbaine à laquelle il appartient.
	 * @see #setImportanceRelative(double)
	 */
	public double getImportanceRelative() {return this.importanceRelative;}
	/**
	 * Détermine l'importance relative de l'espace vide par rapport à la zone élémentaire urbaine à laquelle il appartient.
	 * C'est le rapport de l'aire de l'espace vide sur l'aire de la zone élémentaire urbaine à laquelle il appartient.
	 * @param importanceRelative l'importance relative de l'espace vide par rapport à la zone élémentaire urbaine à laquelle il appartient.
	 * @see #getImportanceRelative()
	 */
	public void setImportanceRelative(double importanceRelative) {this.importanceRelative = importanceRelative;}
	/**
	 * Renvoi le type de l'espace vide : ouvert ou fermé.
	 * La valeur Booléenne retournée est vraie si l'espace vide est ouvert et fausse s'il est fermé.
	 * La mesure utilisée pour Déterminer si l'espace vide est ouvert est l'intersection de l'espace vide avec
	 * un buffer de 1 mètre autour du contour de la zone élémentaire urbaine à laquelle il appartient.
	 * @return vrai si l'espace vide est ouvert et faux s'il est fermé.
	 */
	public boolean isOuvert() {return this.ouvert;}
	/**
	 * Détermine le type de l'espace vide : ouvert ou fermé.
	 * La valeur Booléenne est vraie si l'espace vide est ouvert et fausse s'il est fermé.
	 * La mesure utilisée pour Déterminer si l'espace vide est ouvert est l'intersection de l'espace vide avec
	 * un buffer de 1 mètre autour du contour de la zone élémentaire urbaine à laquelle il appartient.
	 * @param ferme vrai si l'espace vide est ouvert et faux s'il est fermé.
	 */
	public void setOuvert(boolean ferme) {this.ouvert = ferme;}
	/**
	 * Renvoie l'orientation principale de l'espace vide.
	 * L'orientation choisie est la même que pour les bâtiments, i.e. l'orientation principale du plus petit rectangle englobant.
	 * @return l'orientation principale de l'espace vide.
	 */
	public double getOrientationPrincipale() {return this.orientationPrincipale;}
	/**
	 * Détermine l'orientation principale de l'espace vide.
	 * L'orientation choisie est la même que pour les bâtiments, i.e. l'orientation principale du plus petit rectangle englobant.
	 * @param orientationPrincipale l'orientation principale de l'espace vide.
	 */
	public void setOrientationPrincipale(double orientationPrincipale) {this.orientationPrincipale = orientationPrincipale;}
	/**
	 * Renvoie l'élongation de l'espace vide.
	 * C'est le quotient de la largeur et de la longueur du plus petit rectangle englobant.
	 * @return l'élongation de l'espace vide
	 */
	public double getElongation() {return this.elongation;}
	/**
	 * Détermine l'élongation de l'espace vide.
	 * C'est le quotient de la largeur et de la longueur du plus petit rectangle englobant.
	 * @param elongation l'élongation de l'espace vide
	 */
	public void setElongation(double elongation) {this.elongation = elongation;}
	
	/* (non-Javadoc)
	 * @see fr.ign.cogit.appli.geopensim.feature.ElementRepresentation#qualifier()
	 */
	@Override
	public void qualifier() {
		double aireZoneElementaire = this.getZoneElementaireUrbaine().getGeom().area();
		this.setImportanceRelative((aireZoneElementaire!=0.0)?this.getGeom().area()/aireZoneElementaire:0.0);
		try {
			Geometry jtsGeom = JtsGeOxygene.makeJtsGeom(this.getGeom());
			this.setElongation(JtsUtil.elongation(jtsGeom));
			MesureOrientation mesureOrientation = new MesureOrientation(jtsGeom);
			this.setOrientationPrincipale(mesureOrientation.getOrientationGenerale());
		} catch(Exception e) {
			logger.error("Erreur pendant la création de la géométrie Jts : "+e.getMessage());
		}
		try {this.setOuvert(this.getZoneElementaireUrbaine().getGeometrie().exteriorLineString().buffer(1.0).intersects(this.getGeom()));}
		catch(Exception e) {logger.error("Erreur pendant la Détermination de la fermeture de l'espace vide");}
		this.setNature("Espace Vide");
		this.setSource("calculé");
	}
}
