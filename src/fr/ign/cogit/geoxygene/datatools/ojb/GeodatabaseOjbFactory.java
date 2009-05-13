/*
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
 * 
 */

package fr.ign.cogit.geoxygene.datatools.ojb;

import java.lang.reflect.Constructor;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;

/**
 * Constructeur de GeodatabaseOjb (Oracle ou Postgis) a partir d'un alias de connection
 * defini dans le fichier de configuration repository_database.xml.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 *
 */
public class GeodatabaseOjbFactory {
	static Logger logger=Logger.getLogger(GeodatabaseOjbFactory.class.getName());

	private final static String ORACLE_PRODUCT_NAME = "Oracle";
	private final static String POSTGRES_PRODUCT_NAME = "PostgreSQL";
	private final static String GEODATABASE_OJB_ORACLE_CLASS_NAME =
		"fr.ign.cogit.geoxygene.datatools.oracle.GeodatabaseOjbOracle";
	private final static String GEODATABASE_OJB_POSTGIS_CLASS_NAME =
		"fr.ign.cogit.geoxygene.datatools.postgis.GeodatabaseOjbPostgis";

	/** Constructeur de GeodatabaseOjb (Oracle ou Postgis) a partir d'un alias de connection
	 * defini dans le fichier de configuration repository_database.xml.*/
	public static Geodatabase newInstance(String jcdAlias) {
		GeodatabaseOjb ojb = new GeodatabaseOjb(jcdAlias);

		/*	 if (ojb._conn instanceof PGConnection)
			 return new GeodatabaseOjbPostgis(ojb);
		 else if (ojb._conn instanceof OracleConnection)
			 return new GeodatabaseOjbOracle(ojb);
		 else {
			 System.out.println("### Cas non traite : "+ojb._conn.getClass().getName());
			 System.exit(0);
			 return null;
		 }*/

		Constructor<?> geodatabaseConstructor = null;

		try {
			if (ojb._conn.getMetaData().getDatabaseProductName().compareToIgnoreCase(ORACLE_PRODUCT_NAME) == 0) {
				try {
					Class<?> geodatabaseClass = Class.forName(GEODATABASE_OJB_ORACLE_CLASS_NAME);
					geodatabaseConstructor = geodatabaseClass.getConstructor(new Class[] {GeodatabaseOjb.class} );
				} catch (Exception notfound) {
					logger.fatal("Classe "+ GEODATABASE_OJB_ORACLE_CLASS_NAME + " non trouvée dans le CLASSPATH");
					logger.fatal("## PROGRAMME ARRETE !! ## ");
					logger.fatal(notfound.getMessage());
					notfound.printStackTrace();
					System.exit(0);
					return null;
				}
			}

			else if (ojb._conn.getMetaData().getDatabaseProductName().compareToIgnoreCase(POSTGRES_PRODUCT_NAME) == 0) {
				try {
					Class<?> geodatabaseClass = Class.forName(GEODATABASE_OJB_POSTGIS_CLASS_NAME);
					geodatabaseConstructor = geodatabaseClass.getConstructor(new Class[] {GeodatabaseOjb.class} );
				} catch (Exception notfound) {
					logger.fatal("Classe "+ GEODATABASE_OJB_POSTGIS_CLASS_NAME + " non trouvée dans le CLASSPATH");
					logger.fatal("## PROGRAMME ARRETE !! ## ");
					logger.fatal(notfound.getMessage());
					notfound.printStackTrace();
					System.exit(0);
					return null;
				}
			}

			else {
				logger.fatal("### Cas non traite : "+ojb._conn.getClass().getName());
				logger.fatal("## PROGRAMME ARRETE !! ## ");
				System.exit(0);
				return null;
			}

		} catch (SQLException sqlex) {
			logger.fatal("## PROGRAMME ARRETE !! ## ");
			sqlex.printStackTrace();
			System.exit(0);
			return null;
		}

		try {
			return (Geodatabase) geodatabaseConstructor.newInstance(new Object[] {ojb});
		} catch (Exception ex) {
			logger.fatal("## PROGRAMME ARRETE !! ## ");
			logger.fatal(ex.getMessage());
			ex.printStackTrace();
			System.exit(0);
			return null;
		}


	}

	/** Constructeur de GeodatabaseOjb (Oracle ou Postgis) a partir de la connection par defaut
	 * definie dans le fichier de configuration repository_database.xml.*/
	public static Geodatabase newInstance() {
		return GeodatabaseOjbFactory.newInstance(null);
	}

}
