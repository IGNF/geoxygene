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
package fr.ign.cogit.appli.geopensim.feature.basic;

import java.lang.reflect.InvocationTargetException;

import fr.ign.cogit.appli.geopensim.feature.AbstractRepresentationFactory;
import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.appli.geopensim.feature.meso.Alignement;
import fr.ign.cogit.appli.geopensim.feature.meso.GroupeBatiments;
import fr.ign.cogit.appli.geopensim.feature.meso.UniteUrbaine;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.appli.geopensim.feature.micro.EspaceVide;

/**
 * @author Julien Perret
 *
 */
public class BasicRepresentationFactory extends AbstractRepresentationFactory {
	@Override
	public ElementRepresentation creerBatiment() {
		return new BasicBatiment();
	}
	@Override
	public ElementRepresentation creerCimetiere() {
		return new BasicCimetiere();
	}
	@Override
	public ElementRepresentation creerCommune() {return null;}
	@Override
	public ElementRepresentation creerParking() {
		return new BasicParking();
	}
	@Override
	public ElementRepresentation creerCarrefour() {
		return new BasicCarrefour();
	}
	@Override
	public ElementRepresentation creerSurfaceEau() {
		return new BasicSurfaceEau();
	}
	@Override
	public ElementRepresentation creerGroupe() {return null;}
	@Override
	public ElementRepresentation creerIlot() {
		return new ZoneElementaireUrbaine();
	}
	@Override
	public ElementRepresentation creerQuartier() {return null;}
	@Override
	public ElementRepresentation creerTerrainSport() {
		return new BasicTerrainSport();
	}
	@Override
	public ElementRepresentation creerTronconChemin() {
		return new BasicTronconChemin();
	}
	@Override
	public ElementRepresentation creerTronconCoursEau() {
		return new BasicTronconCoursEau();
	}
	@Override
	public ElementRepresentation creerTronconRoute() {
		return new BasicTronconRoute();
	}
	@Override
	public ElementRepresentation creerTronconVoieFerree() {
		return new BasicTronconVoieFerree();
	}
	@Override
	public ElementRepresentation creerVille() {
		return new UniteUrbaine();
	}
	@Override
	public ElementRepresentation creerVegetation() {
		return new BasicVegetation();
	}
	@Override
	public ElementRepresentation creerGroupeBatiments() {
		return new GroupeBatiments();
	}
	@Override
	public ElementRepresentation creerAlignement() {
		return new Alignement();
	}
	
	@Override
	public ElementRepresentation creerElementRepresentation(String nom) {
		if (nom.equalsIgnoreCase("Batiment")) return creerBatiment();
		if (nom.equalsIgnoreCase("Cimetiere")) return creerCimetiere();
		if (nom.equalsIgnoreCase("Parking")) return creerParking();
		if (nom.equalsIgnoreCase("Carrefour")) return creerCarrefour();
		if (nom.equalsIgnoreCase("EspaceVide")) return creerEspaceVide();
		if (nom.equalsIgnoreCase("SurfaceEau")) return creerSurfaceEau();
		if (nom.equalsIgnoreCase("TerrainSport")) return creerTerrainSport();
		if (nom.equalsIgnoreCase("TronconChemin")) return creerTronconChemin();
		if (nom.equalsIgnoreCase("TronconCoursEau")) return creerTronconCoursEau();
		if (nom.equalsIgnoreCase("TronconRoute")) return creerTronconRoute();
		if (nom.equalsIgnoreCase("TronconVoieFerree")) return creerTronconVoieFerree();
		if (nom.equalsIgnoreCase("Vegetation")) return creerVegetation();
		if (nom.equalsIgnoreCase("AireTriage")) return creerAireTriage();
		if (nom.equalsIgnoreCase("PisteAerodrome")) return creerPisteAerodrome();

		if (nom.equalsIgnoreCase("ZoneElementaireUrbaine")||nom.equalsIgnoreCase("Ilot")) return creerIlot();
		if (nom.equalsIgnoreCase("Groupe")) return creerGroupe();
		if (nom.equalsIgnoreCase("ZoneAgregee")||nom.equalsIgnoreCase("Quartier")) return creerQuartier();
		if (nom.equalsIgnoreCase("UniteUrbaine")||nom.equalsIgnoreCase("Ville")) return creerVille();
		if (nom.equalsIgnoreCase("GroupeBatiments")) return creerGroupeBatiments();
		if (nom.equalsIgnoreCase("Alignement")) return creerAlignement();
		return null;
	}
	/**
	 * @return
	 */
	private ElementRepresentation creerEspaceVide() {return new EspaceVide();}
	
	@Override
	public ElementRepresentation creerElementRepresentation(ElementRepresentation representation) {
		try {
			//TODO pour que ça marche, chaque classe Representation doit avoir un copy constructor
			return representation.getClass().getConstructor(representation.getClass()).newInstance(representation);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	// TODO ajouter à la classe mère ?
	public ElementRepresentation creerPisteAerodrome() {return new BasicPisteAerodrome();}

	// TODO ajouter à la classe mère ?
	public ElementRepresentation creerAireTriage() {return new BasicAireTriage();}
}
