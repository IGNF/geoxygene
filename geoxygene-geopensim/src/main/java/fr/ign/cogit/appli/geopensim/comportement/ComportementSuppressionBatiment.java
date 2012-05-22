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

package fr.ign.cogit.appli.geopensim.comportement;


import java.util.HashMap;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationFormesBatimentsV2.ParametresForme;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.ParametresMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.agent.Agent;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.feature.micro.TypeFonctionnel;

/**
 * @author Florence Curie
 *
 */
public class ComportementSuppressionBatiment extends Comportement {
	private static Logger logger=Logger.getLogger(ComportementSuppressionBatiment.class.getName());

	@Override
	public void declencher(Agent agent) {
		super.declencher(agent);

		if (agent instanceof AgentZoneElementaireBatie) {
			if (logger.isDebugEnabled()) logger.debug("suppression du bâtiment incompatible avec le peuplement de "+agent);
			AgentZoneElementaireBatie agentZoneElementaireBatie = (AgentZoneElementaireBatie) agent;

			double percET = 10/100;
			// On récupère la Méthode de peuplement à appliquer et ses paramètres
			String methodePeuplement = agentZoneElementaireBatie.getMethodePeuplement();
			ParametresMethodesPeuplement parametresPeuplement = ConfigurationMethodesPeuplement.getInstance().getParametresMethodesPeuplement(methodePeuplement);
			int typeF = parametresPeuplement.getTypeFonctionnel();
			
			AgentBatiment batiASupprimer = null;
			// On considére que les bâtiments avec le mauvais type fonctionnel sont les plus inadaptés
			for(AgentBatiment bati:agentZoneElementaireBatie.getBatiments()) {
				if ((bati.getTypeFonctionnel()!=typeF)&&(typeF!=TypeFonctionnel.Quelconque)){
					batiASupprimer = bati;
					break;
				}
			}
			// Ensuite on considére l'aire du bâtiment
			if (batiASupprimer==null){
				HashMap<AgentBatiment,Double> listeBatiASupp = new HashMap<AgentBatiment,Double>();
				for(AgentBatiment bati:agentZoneElementaireBatie.getBatiments()) {
					double aireBati = bati.getGeom().area();
					boolean aireOK = false;
					double diffMini = Double.MAX_VALUE;
					if(!parametresPeuplement.getFormeBatiment().isEmpty()){
						for (ParametresForme paramF : parametresPeuplement.getFormeBatiment()){
							double aireMoy = paramF.getTailleBatiment().getMoyenne();
							if (aireMoy!=-1){
								double aireET = paramF.getTailleBatiment().getEcartType();
								if (aireET==-1){
									aireET = aireMoy*percET;
								}
								if((aireBati>=aireMoy-2*aireET)&&(aireBati<=aireMoy+2*aireET)){
									aireOK = true;
								}else if(aireBati<aireMoy-2*aireET){
									double difference = aireMoy-2*aireET-aireBati;
									if (difference<diffMini) diffMini = difference;
								}else {
									double difference = aireBati-aireMoy-2*aireET;
									if (difference<diffMini) diffMini = difference;
								}
							}else{
								aireOK = true;
							}
						}
					}else{
						aireOK = true;
					}
					if (aireOK==false){
						listeBatiASupp.put(bati, diffMini);
					}
				}
				// On Détermine le bâtiment dont l'aire s'éloigne le plus de celles acceptées
				double diffMax = Double.MIN_VALUE;
				for (AgentBatiment bati:listeBatiASupp.keySet()){
					logger.debug("bat a supp possib : "+  bati.getGeom());
					if (listeBatiASupp.get(bati)>diffMax){
						batiASupprimer = bati;
						diffMax = listeBatiASupp.get(bati);
					}
				}
			}
			
			// On supprime le bâtiment
			if (batiASupprimer!=null) {
				logger.debug("on supprime le batiment : "+batiASupprimer);
				batiASupprimer.remove();
				batiASupprimer.getGroupeBatiments().removeComposant(batiASupprimer);
				batiASupprimer.miseAJourGB();
			}
			if (logger.isDebugEnabled()) logger.debug("Fin de la suppression du bâtiment incompatible avec le peuplement de "+agent);
			// On met à jour la densité de la zone élémentaire
			agentZoneElementaireBatie.miseAjourDensite();
		} else {
			logger.error("Agent de type "+agent.getClass()+" au lieu de AgentZoneElementaireBatie");
		}
		
	}
}
