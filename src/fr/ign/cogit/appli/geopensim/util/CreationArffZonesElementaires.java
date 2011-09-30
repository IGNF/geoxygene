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

package fr.ign.cogit.appli.geopensim.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.agent.AgentGeographiqueCollection;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.geoxygene.datatools.GeodatabaseFactory;
import fr.ign.cogit.geoxygene.datatools.GeodatabaseType;
import fr.ign.cogit.geoxygene.feature.DataSet;

/**
 * @author Julien Perret
 *
 */
public class CreationArffZonesElementaires {
	static Logger logger=Logger.getLogger(CreationArffZonesElementaires.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Chargement des données GeOpenSim Stockées dans la BDD OJB
		logger.info("Opening the database");
		DataSet.db = GeodatabaseFactory.newInstance(GeodatabaseType.OJB);
		// Récupération des agents
		logger.info("Loading agents");
		long time = System.currentTimeMillis();
		AgentGeographiqueCollection collection = AgentGeographiqueCollection.getInstance();
		collection.chargerPopulations();
		// Récupération des dates auxquelle on possède des données
		List<Integer> dates = new ArrayList<Integer>(collection.getDates());
		logger.info("Loading agents took "+(System.currentTimeMillis()-time)+" ms");

		for (int periodIndex = 0 ; periodIndex < dates.size()-1 ; periodIndex++) {

			int dateDebutPeriode = dates.get(periodIndex);
			int dateFinPeriode = dates.get(periodIndex+1);
			List<AgentGeographique> zonesElementaires = collection.getElementsGeo(ZoneElementaireUrbaine.class, dateDebutPeriode);

			try {
				//get the file writer
				String path = "zonesElementaires"+dateDebutPeriode+"_"+dateFinPeriode+".arff";
				FileWriter fw = new FileWriter(path);
				fw.write("@RELATION ZoneElementaire\n");
				fw.write("@ATTRIBUTE Id NUMERIC\n");
				fw.write("@ATTRIBUTE DensiteInitiale NUMERIC\n");
				fw.write("@ATTRIBUTE Aire NUMERIC\n");
				fw.write("@ATTRIBUTE Convexite NUMERIC\n");
				fw.write("@ATTRIBUTE Elongation NUMERIC\n");
				fw.write("@ATTRIBUTE NombreBatiments NUMERIC\n");
				fw.write("@ATTRIBUTE MinAireBatiments NUMERIC\n");
				fw.write("@ATTRIBUTE MaxAireBatiments NUMERIC\n");
				fw.write("@ATTRIBUTE MoyenneAiresBatiments NUMERIC\n");
				fw.write("@ATTRIBUTE MedianeAiresBatiments NUMERIC\n");
				fw.write("@ATTRIBUTE HomogeneiteTailleBatiments NUMERIC\n");
				fw.write("@ATTRIBUTE MoyenneConvexiteBatiments NUMERIC\n");
				fw.write("@ATTRIBUTE MoyenneDistanceBatiments NUMERIC\n");
				fw.write("@ATTRIBUTE MoyenneElongationBatiments NUMERIC\n");
				fw.write("@ATTRIBUTE HomogeneiteTypesFonctionnelsBatiments NUMERIC\n");
				fw.write("@ATTRIBUTE TailleBatiments NUMERIC\n");
				fw.write("@ATTRIBUTE RapportBati NUMERIC\n");
				fw.write("@ATTRIBUTE MaxDensiteVoisins NUMERIC\n");
				fw.write("@ATTRIBUTE MoyenneDensiteVoisins NUMERIC\n");
				fw.write("@ATTRIBUTE densiteFinale NUMERIC\n");
				fw.write("@DATA\n");

				for(AgentGeographique agent:zonesElementaires) {
					if (!(agent instanceof AgentZoneElementaireBatie)) continue;
					AgentZoneElementaireBatie agentZone = (AgentZoneElementaireBatie) agent;
					ZoneElementaireUrbaine zoneDebut = (ZoneElementaireUrbaine) agentZone.getRepresentation(dateDebutPeriode);
					ZoneElementaireUrbaine zoneFin = (ZoneElementaireUrbaine) agentZone.getRepresentation(dateFinPeriode);
					if ((zoneDebut==null)||(zoneFin==null)) continue;
					fw.write(""+zoneDebut.getId()+","+
								zoneDebut.getDensite()+","+
								nan(zoneDebut.getAire())+","+
								nan(zoneDebut.getConvexite())+","+
								nan(zoneDebut.getElongation())+","+
								nan(zoneDebut.getNombreBatiments())+","+
								nan(zoneDebut.getMinAiresBatiments())+","+
								nan(zoneDebut.getMaxAiresBatiments())+","+
								nan(zoneDebut.getMoyenneAiresBatiments())+","+
								nan(zoneDebut.getMedianeAiresBatiments())+","+
								nan(zoneDebut.getHomogeneiteTailleBatiments())+","+
								nan(zoneDebut.getMoyenneConvexiteBatiments())+","+
								nan(zoneDebut.getMoyenneDistanceBatiments())+","+
								nan(zoneDebut.getMoyenneElongationBatiments())+","+
								nan(zoneDebut.getHomogeneiteTypesFonctionnelsBatiments())+","+
								nan(zoneDebut.getTailleBatiments())+","+
								nan(zoneDebut.getRapportBati())+","+
								nan(zoneDebut.getMaxDensiteVoisins())+","+
								nan(zoneDebut.getMoyenneDensiteVoisins())+","+
								nan(zoneFin.getDensite())+"\n");
				}
				fw.close();

			} catch (IOException e) { e.printStackTrace(); }
		
		try {
			//get the file writer
			String path = "zonesElementaires_type_evolution_marge_"+marge+"_"+dateDebutPeriode+"_"+dateFinPeriode+".arff";
			FileWriter fw = new FileWriter(path);
			fw.write("@RELATION ZoneElementaire\n");
			fw.write("@ATTRIBUTE Id NUMERIC\n");
			fw.write("@ATTRIBUTE DensiteInitiale NUMERIC\n");
			fw.write("@ATTRIBUTE Aire NUMERIC\n");
			fw.write("@ATTRIBUTE Convexite NUMERIC\n");
			fw.write("@ATTRIBUTE Elongation NUMERIC\n");
			fw.write("@ATTRIBUTE NombreBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MinAireBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MaxAireBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MoyenneAiresBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MedianeAiresBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE HomogeneiteTailleBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MoyenneConvexiteBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MoyenneDistanceBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MoyenneElongationBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE HomogeneiteTypesFonctionnelsBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE TailleBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE RapportBati NUMERIC\n");
			fw.write("@ATTRIBUTE MaxDensiteVoisins NUMERIC\n");
			fw.write("@ATTRIBUTE MoyenneDensiteVoisins NUMERIC\n");
			fw.write("@ATTRIBUTE type {STABLE,AUGMENTATION,DIMINUTION}\n");
			fw.write("@DATA\n");

			for(AgentGeographique agent:zonesElementaires) {
				if (!(agent instanceof AgentZoneElementaireBatie)) continue;
				AgentZoneElementaireBatie agentZone = (AgentZoneElementaireBatie) agent;
				ZoneElementaireUrbaine zoneDebut = (ZoneElementaireUrbaine) agentZone.getRepresentation(dateDebutPeriode);
				ZoneElementaireUrbaine zoneFin = (ZoneElementaireUrbaine) agentZone.getRepresentation(dateFinPeriode);
				if ((zoneDebut==null)||(zoneFin==null)) continue;
				fw.write(""+zoneDebut.getId()+","+
							zoneDebut.getDensite()+","+
							nan(zoneDebut.getAire())+","+
							nan(zoneDebut.getConvexite())+","+
							nan(zoneDebut.getElongation())+","+
							nan(zoneDebut.getNombreBatiments())+","+
							nan(zoneDebut.getMinAiresBatiments())+","+
							nan(zoneDebut.getMaxAiresBatiments())+","+
							nan(zoneDebut.getMoyenneAiresBatiments())+","+
							nan(zoneDebut.getMedianeAiresBatiments())+","+
							nan(zoneDebut.getHomogeneiteTailleBatiments())+","+
							nan(zoneDebut.getMoyenneConvexiteBatiments())+","+
							nan(zoneDebut.getMoyenneDistanceBatiments())+","+
							nan(zoneDebut.getMoyenneElongationBatiments())+","+
							nan(zoneDebut.getHomogeneiteTypesFonctionnelsBatiments())+","+
							nan(zoneDebut.getTailleBatiments())+","+
							nan(zoneDebut.getRapportBati())+","+
							nan(zoneDebut.getMaxDensiteVoisins())+","+
							nan(zoneDebut.getMoyenneDensiteVoisins())+","+
							typeEvolution(zoneDebut,zoneFin)+"\n");
			}
			fw.close();

		} catch (IOException e) { e.printStackTrace(); }
		
		
		try {
			//get the file writer
			String path = "zonesElementaires_aumentation_marge_"+marge+"_"+dateDebutPeriode+"_"+dateFinPeriode+".arff";
			FileWriter fw = new FileWriter(path);
			fw.write("@RELATION ZoneElementaire\n");
			fw.write("@ATTRIBUTE Id NUMERIC\n");
			fw.write("@ATTRIBUTE DensiteInitiale NUMERIC\n");
			fw.write("@ATTRIBUTE Aire NUMERIC\n");
			fw.write("@ATTRIBUTE Convexite NUMERIC\n");
			fw.write("@ATTRIBUTE Elongation NUMERIC\n");
			fw.write("@ATTRIBUTE NombreBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MinAireBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MaxAireBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MoyenneAiresBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MedianeAiresBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE HomogeneiteTailleBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MoyenneConvexiteBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MoyenneDistanceBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MoyenneElongationBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE HomogeneiteTypesFonctionnelsBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE TailleBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE RapportBati NUMERIC\n");
			fw.write("@ATTRIBUTE MaxDensiteVoisins NUMERIC\n");
			fw.write("@ATTRIBUTE MoyenneDensiteVoisins NUMERIC\n");
			fw.write("@ATTRIBUTE Augmentation NUMERIC\n");
			fw.write("@DATA\n");

			for(AgentGeographique agent:zonesElementaires) {
				if (!(agent instanceof AgentZoneElementaireBatie)) continue;
				AgentZoneElementaireBatie agentZone = (AgentZoneElementaireBatie) agent;
				ZoneElementaireUrbaine zoneDebut = (ZoneElementaireUrbaine) agentZone.getRepresentation(dateDebutPeriode);
				ZoneElementaireUrbaine zoneFin = (ZoneElementaireUrbaine) agentZone.getRepresentation(dateFinPeriode);
				if ((zoneDebut==null)||(zoneFin==null)||(typeEvolution(zoneDebut, zoneFin)!="AUGMENTATION")) continue;
				fw.write(""+zoneDebut.getId()+","+
							zoneDebut.getDensite()+","+
							nan(zoneDebut.getAire())+","+
							nan(zoneDebut.getConvexite())+","+
							nan(zoneDebut.getElongation())+","+
							nan(zoneDebut.getNombreBatiments())+","+
							nan(zoneDebut.getMinAiresBatiments())+","+
							nan(zoneDebut.getMaxAiresBatiments())+","+
							nan(zoneDebut.getMoyenneAiresBatiments())+","+
							nan(zoneDebut.getMedianeAiresBatiments())+","+
							nan(zoneDebut.getHomogeneiteTailleBatiments())+","+
							nan(zoneDebut.getMoyenneConvexiteBatiments())+","+
							nan(zoneDebut.getMoyenneDistanceBatiments())+","+
							nan(zoneDebut.getMoyenneElongationBatiments())+","+
							nan(zoneDebut.getHomogeneiteTypesFonctionnelsBatiments())+","+
							nan(zoneDebut.getTailleBatiments())+","+
							nan(zoneDebut.getRapportBati())+","+
							nan(zoneDebut.getMaxDensiteVoisins())+","+
							nan(zoneDebut.getMoyenneDensiteVoisins())+","+
							nan(zoneFin.getDensite()-zoneDebut.getDensite())+"\n");
			}
			fw.close();

		} catch (IOException e) { e.printStackTrace(); }			

		try {
			//get the file writer
			String path = "zonesElementaires_diminution_marge_"+marge+"_"+dateDebutPeriode+"_"+dateFinPeriode+".arff";
			FileWriter fw = new FileWriter(path);
			fw.write("@RELATION ZoneElementaire\n");
			fw.write("@ATTRIBUTE Id NUMERIC\n");
			fw.write("@ATTRIBUTE DensiteInitiale NUMERIC\n");
			fw.write("@ATTRIBUTE Aire NUMERIC\n");
			fw.write("@ATTRIBUTE Convexite NUMERIC\n");
			fw.write("@ATTRIBUTE Elongation NUMERIC\n");
			fw.write("@ATTRIBUTE NombreBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MinAireBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MaxAireBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MoyenneAiresBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MedianeAiresBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE HomogeneiteTailleBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MoyenneConvexiteBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MoyenneDistanceBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE MoyenneElongationBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE HomogeneiteTypesFonctionnelsBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE TailleBatiments NUMERIC\n");
			fw.write("@ATTRIBUTE RapportBati NUMERIC\n");
			fw.write("@ATTRIBUTE MaxDensiteVoisins NUMERIC\n");
			fw.write("@ATTRIBUTE MoyenneDensiteVoisins NUMERIC\n");
			fw.write("@ATTRIBUTE Diminution NUMERIC\n");
			fw.write("@DATA\n");

			for(AgentGeographique agent:zonesElementaires) {
				if (!(agent instanceof AgentZoneElementaireBatie)) continue;
				AgentZoneElementaireBatie agentZone = (AgentZoneElementaireBatie) agent;
				ZoneElementaireUrbaine zoneDebut = (ZoneElementaireUrbaine) agentZone.getRepresentation(dateDebutPeriode);
				ZoneElementaireUrbaine zoneFin = (ZoneElementaireUrbaine) agentZone.getRepresentation(dateFinPeriode);
				if ((zoneDebut==null)||(zoneFin==null)||(typeEvolution(zoneDebut, zoneFin)!="DIMINUTION")) continue;
				fw.write(""+zoneDebut.getId()+","+
							zoneDebut.getDensite()+","+
							nan(zoneDebut.getAire())+","+
							nan(zoneDebut.getConvexite())+","+
							nan(zoneDebut.getElongation())+","+
							nan(zoneDebut.getNombreBatiments())+","+
							nan(zoneDebut.getMinAiresBatiments())+","+
							nan(zoneDebut.getMaxAiresBatiments())+","+
							nan(zoneDebut.getMoyenneAiresBatiments())+","+
							nan(zoneDebut.getMedianeAiresBatiments())+","+
							nan(zoneDebut.getHomogeneiteTailleBatiments())+","+
							nan(zoneDebut.getMoyenneConvexiteBatiments())+","+
							nan(zoneDebut.getMoyenneDistanceBatiments())+","+
							nan(zoneDebut.getMoyenneElongationBatiments())+","+
							nan(zoneDebut.getHomogeneiteTypesFonctionnelsBatiments())+","+
							nan(zoneDebut.getTailleBatiments())+","+
							nan(zoneDebut.getRapportBati())+","+
							nan(zoneDebut.getMaxDensiteVoisins())+","+
							nan(zoneDebut.getMoyenneDensiteVoisins())+","+
							nan(zoneDebut.getDensite()-zoneFin.getDensite())+"\n");
			}
			fw.close();

		} catch (IOException e) { e.printStackTrace(); }			

	}

	}
	public static String nan(double d) {return (Double.isNaN(d)?"?":""+d);}
	private static double marge = 0.001;
	public static String typeEvolution(ZoneElementaireUrbaine zoneDebut, ZoneElementaireUrbaine zoneFin) {
		return (zoneFin.getDensite()<zoneDebut.getDensite()-marge)?"DIMINUTION":(zoneFin.getDensite()>zoneDebut.getDensite()+marge)?"AUGMENTATION":"STABLE";
	}
}
