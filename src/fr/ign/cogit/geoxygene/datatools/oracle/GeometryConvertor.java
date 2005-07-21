/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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
import oracle.sdoapi.OraSpatialManager;
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
 * Conversion dans les 2 sens entre une SDO_GEOMETRY (format sql.STRUCT) et un GM_Object
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1  
 */

public class GeometryConvertor  {


    /////////////////////////////////////////////////////////////////////////////////////
    public static Object Sdo2GM_Object(Object object) {
        try {
            GeometryFactory gf = OraSpatialManager.getGeometryFactory();
            SRManager srm = OraSpatialManager.getSpatialReferenceManager();
            Geometry sdoGeom = SDOGeometry.STRUCTtoGeometry((STRUCT)object,gf,srm);
            GM_Object isoGeom = IsoAndSdo.sdoapi2iso(sdoGeom);
            return isoGeom;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    

    /////////////////////////////////////////////////////////////////////////////////////
    public static Object GM_Object2Sdo(Object object, Connection conn) {     
        try {
            GeometryFactory gf = OraSpatialManager.getGeometryFactory();
            Geometry sdoGeom = IsoAndSdo.iso2sdoapi(gf,(GM_Object)object);   
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
