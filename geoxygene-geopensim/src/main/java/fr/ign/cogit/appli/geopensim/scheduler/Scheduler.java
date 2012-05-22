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
package fr.ign.cogit.appli.geopensim.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.Agent;
import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.util.TimeUtil;

/**
 * Moteur de simulation des agents Géographiques.
 * @author Julien Perret
 *
 */
public class Scheduler implements Runnable {
	static Logger logger=Logger.getLogger(Scheduler.class.getName());

  private List<SchedulerEventListener> listeners = new ArrayList<SchedulerEventListener>(0);
  public void addListener(SchedulerEventListener listener) {
    this.listeners.add(listener);
  }
  public void removeListener(SchedulerEventListener listener) {
    this.listeners.remove(listener);
  }
  public void fireEvent(SchedulerEvent event) {
    for (SchedulerEventListener listener : this.listeners) {
      listener.changed(event);
    }
  }
	
	//le moteur
	private static Scheduler moteur=null;
	/**
	 * Récupération de l'instance unique (singleton) de Moteur. 
	 * @return instance unique (singleton) de Moteur.
	 */
	public static Scheduler getInstance(){
		if (moteur==null) synchronized(Scheduler.class) {if (moteur==null) moteur=new Scheduler();}
		return moteur;
	}

	protected static List<AgentGeographique> liste=new ArrayList<AgentGeographique>(0);
	protected static List<AgentGeographique> agentsAtraiter=new ArrayList<AgentGeographique>(0);
	/**
	 * Renvoie la liste des agents à traiter, i.e. la liste
	 * des agents utilisés pour la simulation.
	 * @return la liste des agents à traiter, i.e. la liste
	 * des agents utilisés pour la simulation.
	 */
	public static List<AgentGeographique> getAgentsAtraiter() {return agentsAtraiter;}
    public static List<AgentGeographique> getListe() {return liste;}
	/**
	 * Affecte la liste des agents à traiter, i.e. la liste
	 * des agents utilisés pour la simulation.
	 * @param agentsAtraiter la liste des agents à traiter, i.e. la liste
	 * des agents utilisés pour la simulation.
	 */
	public static void setAgentsAtraiter(List<AgentGeographique> agentsAtraiter) {Scheduler.agentsAtraiter = agentsAtraiter;}

	protected static Thread thread = null;
	/**
	 * Active le moteur, i.e. lance une simulation.
	 */
	public synchronized void activer() {
		if (thread != null) { return; }
		thread = new Thread(Scheduler.getInstance());
		stop = false;
		thread.start();
	}
	/**
	 * désactiver le moteur.
	 * Interromp la simulation en cours.
	 * FIXME on n'interromp pas vraiment la simulation...
	 */
	public void desactiver(){stop=true;}

	private boolean running = false;
	public boolean isRunning() {return this.running;}
	
	private boolean stop=false;
	public void testIfStop() throws InterruptedException{ if(stop) throw new InterruptedException(); }

	@Override
	public synchronized void run() {
		running = true;
		this.fireEvent(new SchedulerEvent(this, SchedulerEvent.Type.STARTED));
		long time = System.currentTimeMillis();
		logger.info("------------- Lancement du moteur");
		if (logger.isDebugEnabled()) logger.debug("  nombre d'agents a activer: "+liste.size());

		Thread cth=Thread.currentThread();

		try {
		//tant que la file n'est pas vide, activer les points de la pile a tour de role
		while ((!Scheduler.liste.isEmpty())&&(thread==cth)){
			Agent agent=liste.get(0);

			agent.activer();

			liste.remove(0);
			this.fireEvent(new SchedulerEvent(agent, SchedulerEvent.Type.AGENT_FINISHED));
			
			testIfStop();
			
			if (logger.isDebugEnabled()) logger.debug("  nombre d'agents restant a activer: "+liste.size());
		}
		} catch (InterruptedException exception) {
			
		}
		thread=null;
		// Construction de nouvelles représentations à partir de l'état des agents à la fin de la simulation
		//logger.info("------------- Construction des nouvelles représentations");
		//FIXME
		running = false;
		this.fireEvent(new SchedulerEvent(this, SchedulerEvent.Type.FINISHED));
		logger.info("------------- Fin. Temps de Simulation = "+(System.currentTimeMillis()-time)+" ms");
		logger.info("------------- i.e. " + TimeUtil.toTimeLength(System.currentTimeMillis()-time));
	};

	/**
	 * Charge les agents Géographiques présents dans 
	 * la liste des agents à traiter.
	 */
	public static void charger(){
		liste=new ArrayList<AgentGeographique>(agentsAtraiter.size());
		for(AgentGeographique a:agentsAtraiter) {
			logger.info("Chargement de "+a);
			liste.add(a);
		}
	}
}
