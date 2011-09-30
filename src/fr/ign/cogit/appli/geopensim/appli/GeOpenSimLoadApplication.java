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

package fr.ign.cogit.appli.geopensim.appli;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.feature.basic.BasicBatiment;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.datatools.GeodatabaseFactory;
import fr.ign.cogit.geoxygene.datatools.GeodatabaseType;
import fr.ign.cogit.geoxygene.feature.DataSet;

/**
 * Base class for GeOxygene applications.
 *
 * @author Julien Perret
 *
 */
public class GeOpenSimLoadApplication  {
	static Logger logger = Logger.getLogger(GeOpenSimLoadApplication.class
			.getName());
	public static void main(String[] args) {
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Chargement des données GeOpenSim Stockées dans la BDD OJB
        logger.info("Opening the database");
        DataSet.db = GeodatabaseFactory.newInstance(GeodatabaseType.OJB);
        // Récupération des agents
        logger.info("Loading agents");
        long time = System.currentTimeMillis();
        IFeatureCollection<BasicBatiment> agentsGeo = DataSet.db.loadAllFeatures(BasicBatiment.class);
        // Récupération des dates auxquelle on possède des données
        logger.info("Loading agents took "
                + (System.currentTimeMillis() - time) + " ms");
        /*
        for (Batiment b : agentsGeo) {
            logger.info("Batiment " + b.getId());
            logger.info("\t Agent = " + b.getAgentGeographique());
        }
        */
        try {
            DataSet.db.getConnection().close();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        DataSet.db.close();
        logger.info(agentsGeo.size() + " objects loaded");
        while (true) {
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }            
        }
	}
}
