/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.process;

import java.sql.Time;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.LienReseaux;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;

/**
 * 
 * 
 *
 */
public class NetworkDataMatching {

    /** logger. */
    private static final Logger LOGGER = LogManager.getLogger(NetworkDataMatching.class.getName());

    /** Parameters, Dataset and Actions. */
    private ParametresApp param;
    
    private ReseauApp carteTopo1;
    private ReseauApp carteTopo2;
    
    /**
     * Constructor.
     * @param paramApp
     */
    public NetworkDataMatching(ParametresApp param, ReseauApp network1, ReseauApp network2) {
        this.param = param;
        this.carteTopo1 = network1;
        this.carteTopo2 = network2;
    }

    /**
     * Appariement de réseaux.
     * @param paramApp, les paramètres de l'appariement
     * @return
     */
    @SuppressWarnings("unchecked")
    public EnsembleDeLiens networkDataMatching() {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("------------------------------------------------------------------");
            LOGGER.info("NETWORK MATCHING START");
            LOGGER.info("1 = least detailled data;");
            LOGGER.info("2 = most detailled data");
            LOGGER.info("");
        }

        // ---------------------------------------------------------------------------------------------
        // NB: l'ordre dans lequel les projections sont faites n'est pas neutre
        if (param.projeteNoeuds2SurReseau1) {
            // if (paramApp.projeteNoeuds2SurReseau1) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Projection of network 2 onto network1 "
                        + (new Time(System.currentTimeMillis())).toString());
            }
            carteTopo1.projete(carteTopo2, param.projeteNoeuds2SurReseau1DistanceNoeudArc,
             param.projeteNoeuds2SurReseau1DistanceProjectionNoeud,
             param.projeteNoeuds2SurReseau1ImpassesSeulement);
        }
        // if (paramApp.projeteNoeuds1SurReseau2) {
        if (param.projeteNoeuds1SurReseau2) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Projection of network 1 onto network2 "
                        + (new Time(System.currentTimeMillis())).toString());
            }
            carteTopo2.projete(carteTopo1, param.projeteNoeuds1SurReseau2DistanceNoeudArc,
                param.projeteNoeuds1SurReseau2DistanceProjectionNoeud,
                param.projeteNoeuds1SurReseau2ImpassesSeulement);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Filling of edges and nodes attributes " + (new Time(System.currentTimeMillis())).toString());
        }
        carteTopo1.instancieAttributsNuls(param.distanceNoeudsMax);
        carteTopo2.initialisePoids();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Data Structuring finished : ");
            LOGGER.info("network 1 : " + carteTopo1.getPopArcs().size() + " Edges, " + carteTopo1.getPopNoeuds().size()
                    + " Nodes.");
            LOGGER.info("network 2 : " + carteTopo2.getPopArcs().size() + " Edges, " + carteTopo2.getPopNoeuds().size()
                    + " Nodes.");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("END OF STRUCTURING " + new Time(System.currentTimeMillis()).toString());
        }

        // --------------------------------------------------------------------------------------
        // APPARIEMENT
        // --------------------------------------------------------------------------------------
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("");
            LOGGER.info("NETWORK MATCHING");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("    NETWORK MATCHING START");
        }
        EnsembleDeLiens liens = Appariement.appariementReseaux(carteTopo1, carteTopo2, param);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("    Network Matching finished");
            LOGGER.info("  " + liens.size() + "matching links found");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("    END OF NETWORK MATCHING");
        }

        // --------------------------------------------------------------------------------------
        // EXPORT
        // --------------------------------------------------------------------------------------
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("");
            LOGGER.info("ASSESSMENT AND RESULT EXPORT");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("START OF EXPORT ");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Link geometry assignment");
        }
        
        LienReseaux.exportAppCarteTopo(liens, param);
        

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("######## NETWORK MATCHING END #########");
        }

        return liens;
    }

    
}
