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

package fr.ign.cogit.geoxygene.datatools.oracle;

import java.sql.Connection;

import oracle.jdbc.driver.OracleConnection;
import oracle.sdoapi.adapter.SDOGeometry;
import oracle.sdoapi.adapter.SDOTemplateFactory;
import oracle.sdoapi.adapter.SDOTemplateFactoryImpl;
import oracle.sdoapi.geom.Geometry;
import oracle.sdoapi.geom.GeometryFactory;
import oracle.sdoapi.sref.SRManager;
import oracle.sql.STRUCT;

import org.apache.ojb.broker.util.batch.BatchConnection;

import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Conversion dans les 2 sens entre une SDO_GEOMETRY (format sql.STRUCT) et un GM_Object.
 * Ceci est utilise par OJB.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1  
 *
 */

public class GeomGeOxygene2Oracle  {
	
	// Initialise au demarrage de la Geodatabase Oracle comme un singleton
	public static Connection CONNECTION;
	public static SRManager SRM;
	public static GeometryFactory GF;
    
    
    public static Object sqlToJava (Object object) {
        try {
            Geometry sdoGeom = SDOGeometry.STRUCTtoGeometry((STRUCT)object,GF,SRM);
            GM_Object isoGeom = IsoAndSdo.sdoapi2iso(sdoGeom);
            return isoGeom;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    /** Methode utilisee si on n'utilise PAS les classes maison "OxygenePresistenceBroker" et "GeOxygeneStatementManager".
     * Ceci est a definir dans OJB.properties.
     * Inconvenient : on utilise une variable connection statique, 
     * donc impossible de se connecter a plusieurs bases Oracle simultanement.
     * De plus il y a un bug avec OJB : impossible d'ecrire des geometries nulles.
     */
	public static Object javaToSql (Object object) {    
		try {
			Geometry sdoGeom = IsoAndSdo.iso2sdoapi(GF,(GM_Object)object);   
			SDOTemplateFactory sdoTF;
			if (CONNECTION instanceof BatchConnection)  {// ceci est pour OJB
				OracleConnection oConn = (OracleConnection) ((BatchConnection)CONNECTION).getDelegate();
				sdoTF = new SDOTemplateFactoryImpl(oConn);
			} else
				sdoTF = new SDOTemplateFactoryImpl((OracleConnection)CONNECTION);
            
			STRUCT str = SDOGeometry.geometryToSTRUCT(sdoGeom,sdoTF);           
			return str;
	   } catch (Exception e) {
			e.printStackTrace();
			return null;
	   }                       
	}


	/** Methode utilisee si on utilise les classes maison "GeOxygenePresistenceBroker" et "GeOxygeneStatementManager".
	 * Ceci est a definir dans OJB.properties.
	 * Ceci corrige les deux defauts de la methode qui ne passe pas de connection en parametre.
	 */
    public static Object javaToSql(Object object, Connection conn) {    
        try {
            Geometry sdoGeom = IsoAndSdo.iso2sdoapi(GF,(GM_Object)object);   
            SDOTemplateFactory sdoTF;
            if (conn instanceof BatchConnection)  {// ceci est pour OJB
                OracleConnection oConn = (OracleConnection) ((BatchConnection)conn).getDelegate();
                sdoTF = new SDOTemplateFactoryImpl(oConn);
            } else
                sdoTF = new SDOTemplateFactoryImpl((OracleConnection)conn);
            
            STRUCT str = SDOGeometry.geometryToSTRUCT(sdoGeom,sdoTF);           
            return str;
       } catch (Exception e) {
            e.printStackTrace();
            return null;
       }                       
    }

}
