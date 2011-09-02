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

package fr.ign.cogit.geoxygene.datatools.ojb;

import java.lang.reflect.Constructor;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;

/**
 * Constructeur de GeodatabaseOjb (Oracle ou Postgis) a partir d'un alias de
 * connection defini dans le fichier de configuration repository_database.xml.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 */
public class GeodatabaseOjbFactory {
  static Logger logger = Logger
      .getLogger(GeodatabaseOjbFactory.class.getName());

  private final static String ORACLE_PRODUCT_NAME = "Oracle"; //$NON-NLS-1$
  private final static String POSTGRES_PRODUCT_NAME = "PostgreSQL"; //$NON-NLS-1$
  private final static String GEODATABASE_OJB_ORACLE_CLASS_NAME = "fr.ign.cogit.geoxygene.datatools.oracle" + //$NON-NLS-1$
      ".GeodatabaseOjbOracle"; //$NON-NLS-1$
  private final static String GEODATABASE_OJB_POSTGIS_CLASS_NAME = "fr.ign.cogit.geoxygene.datatools.postgis" + //$NON-NLS-1$
      ".GeodatabaseOjbPostgis"; //$NON-NLS-1$

  /**
   * Constructeur de GeodatabaseOjb (Oracle ou Postgis) a partir d'un alias de
   * connection defini dans le fichier de configuration repository_database.xml.
   */
  public static Geodatabase newInstance(String jcdAlias) {
    GeodatabaseOjb ojb = new GeodatabaseOjb(jcdAlias);
    Constructor<?> geodatabaseConstructor = null;
    try {
      if (ojb._conn.getMetaData().getDatabaseProductName()
          .compareToIgnoreCase(GeodatabaseOjbFactory.ORACLE_PRODUCT_NAME) == 0) {
        try {
          Class<?> geodatabaseClass = Class
              .forName(GeodatabaseOjbFactory.GEODATABASE_OJB_ORACLE_CLASS_NAME);
          geodatabaseConstructor = geodatabaseClass
              .getConstructor(new Class[] { GeodatabaseOjb.class });
        } catch (Exception notfound) {
          GeodatabaseOjbFactory.logger.fatal("Class " //$NON-NLS-1$
              + GeodatabaseOjbFactory.GEODATABASE_OJB_ORACLE_CLASS_NAME
              + " not found in CLASSPATH"); //$NON-NLS-1$
          GeodatabaseOjbFactory.logger.fatal("Program will stop"); //$NON-NLS-1$
          GeodatabaseOjbFactory.logger.fatal(notfound.getMessage());
          notfound.printStackTrace();
          System.exit(0);
          return null;
        }
      } else if (ojb._conn.getMetaData().getDatabaseProductName()
          .compareToIgnoreCase(GeodatabaseOjbFactory.POSTGRES_PRODUCT_NAME) == 0) {
        try {
          Class<?> geodatabaseClass = Class
              .forName(GeodatabaseOjbFactory.GEODATABASE_OJB_POSTGIS_CLASS_NAME);
          geodatabaseConstructor = geodatabaseClass
              .getConstructor(new Class[] { GeodatabaseOjb.class });
        } catch (Exception notfound) {
          GeodatabaseOjbFactory.logger.fatal("Class " //$NON-NLS-1$
              + GeodatabaseOjbFactory.GEODATABASE_OJB_POSTGIS_CLASS_NAME
              + " not found in CLASSPATH"); //$NON-NLS-1$
          GeodatabaseOjbFactory.logger.fatal("Program will stop"); //$NON-NLS-1$
          GeodatabaseOjbFactory.logger.fatal(notfound.getMessage());
          notfound.printStackTrace();
          System.exit(0);
          return null;
        }
      }

      else {
        GeodatabaseOjbFactory.logger.fatal("Program will stop"); //$NON-NLS-1$
        System.exit(0);
        return null;
      }

    } catch (SQLException sqlex) {
      GeodatabaseOjbFactory.logger.fatal("Program will stop"); //$NON-NLS-1$
      sqlex.printStackTrace();
      System.exit(0);
      return null;
    }

    try {
      return (Geodatabase) geodatabaseConstructor
          .newInstance(new Object[] { ojb });
    } catch (Exception ex) {
      GeodatabaseOjbFactory.logger.fatal("Program will stop"); //$NON-NLS-1$
      GeodatabaseOjbFactory.logger.fatal(ex.getMessage());
      ex.printStackTrace();
      System.exit(0);
      return null;
    }

  }

  /**
   * Constructeur de GeodatabaseOjb (Oracle ou Postgis) a partir de la
   * connection par defaut definie dans le fichier de configuration
   * repository_database.xml.
   */
  public static Geodatabase newInstance() {
    return GeodatabaseOjbFactory.newInstance(null);
  }
}
