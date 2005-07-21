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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import oracle.sdoapi.OraSpatialManager;
import oracle.sdoapi.geom.Curve;
import oracle.sdoapi.geom.CurvePolygon;
import oracle.sdoapi.geom.CurveString;
import oracle.sdoapi.geom.Geometry;
import oracle.sdoapi.geom.GeometryFactory;
import oracle.sdoapi.geom.LineString;
import oracle.sdoapi.geom.MultiCurve;
import oracle.sdoapi.geom.MultiCurvePolygon;
import oracle.sdoapi.geom.MultiCurveString;
import oracle.sdoapi.geom.MultiLineString;
import oracle.sdoapi.geom.MultiPolygon;
import oracle.sdoapi.geom.MultiSurface;
import oracle.sdoapi.geom.Polygon;
import oracle.sdoapi.geom.Surface;
import oracle.sql.ARRAY;
import oracle.sql.STRUCT;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.Metadata;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;


/**
 * Methode pour encapsuler l'appel d'une requete spatiale dans Oracle, ou d'une methode de la SDOAPI. 
 * Il peut s'agir de requetes geometriques, calcul d'index spatial, chargement d'objets, initialisation des metadonnees spatiales ...
 * Cette classe est appelee par les methodes de GM_Object, par des methodes de FT_Feature, par des methodes de Geodatabase.
 * Le but est d'assurer l'independance de ces classes vis a vis d'Oracle,
 * et de concentrer dans une classe tout ce qui depend d'Oracle.
 *
 * <P>On suppose l'existence d'une table TEMP_REQUETE avec une colonne GID(number) et GEOM(SDO_GEOMETRY).
 * On vide cette table, puis on recopie le(s) GM_Object passe(s) en parametre dans cette table,
 * et on execute sur lui(eux) la requete passee en parametre.
 *
 * INUTILE DEPUIS LE PASSAGE A JTS (sauf pour l'extraction par zone, et a l'initialisation des metadonnees) !
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 *
 */



public class OracleSpatialQuery {
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /// methodes generiques //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /** 1 seul objet geometrique passe en parametre - renvoie une geometrie */
    private static GM_Object executeGeometry(Geodatabase data, GM_Object isoGeom, String query) {
       
        // ceci sera le result
        GM_Object result = null;
        
        try {
            // initialise la connection a Oracle
            Connection conn = data.getConnection();
            
            // vide la table TEMP_REQUETE
            String update = "DELETE FROM TEMP_REQUETE";
            PreparedStatement ps = conn.prepareStatement(update);
            ps.executeUpdate();
            ps.close();            
            
            // convertit isoGeom en type sdoapi, et l'ecrit dans la table TEMP_REQUETE            
            STRUCT str = (STRUCT) GeometryConvertor.GM_Object2Sdo(isoGeom, conn);
            update = "INSERT INTO TEMP_REQUETE VALUES (0,?)";
            ps = conn.prepareStatement(update);
            ps.setObject(1, str);
            ps.executeUpdate();
            ps.close();
            
            // execute la requete
            ps = conn.prepareStatement(query);            
            ResultSet rs = (ResultSet)ps.executeQuery();
            while (rs.next()) 
                result = (GM_Object)(GeometryConvertor.Sdo2GM_Object (rs.getObject(1)));
            rs.close();
            ps.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
            
        return result;
    }
    
        
    /** 2 objets geometriques passes en parametre - renvoie une geometrie */
	private static GM_Object executeGeometry(Geodatabase data, GM_Object isoGeom1, GM_Object isoGeom2, String query)  {
        
        // ceci sera le result
        GM_Object result = null;
        
        try {       
            // initialise la connection a Oracle
            Connection conn = data.getConnection();
            
            // vide la table TEMP_REQUETE
            String update = "DELETE FROM TEMP_REQUETE";
            PreparedStatement ps = conn.prepareStatement(update);
            ps.executeUpdate();
            ps.close();            

            // convertit isoGeom1 en type sdoapi, et l'ecrit dans la table TEMP_REQUETE
            STRUCT str = (STRUCT) GeometryConvertor.GM_Object2Sdo(isoGeom1, conn);
            update = "INSERT INTO TEMP_REQUETE VALUES (0,?)";
            ps = conn.prepareStatement(update);
            ps.setObject(1, str);
            ps.executeUpdate();
            ps.close();            

            // convertit isoGeom2 en type sdoapi, et l'ecrit dans la table TEMP_REQUETE
            str = (STRUCT) GeometryConvertor.GM_Object2Sdo(isoGeom2, conn);
            update = "INSERT INTO TEMP_REQUETE VALUES (1,?)";
            ps = conn.prepareStatement(update);
            ps.setObject(1, str);
            ps.executeUpdate();
            ps.close();            

            // execute la requete
            ps = conn.prepareStatement(query);            
            ResultSet rs = (ResultSet)ps.executeQuery();
            while (rs.next()) 
                result = (GM_Object)(GeometryConvertor.Sdo2GM_Object (rs.getObject(1)));
            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
        
    /** 2 objets geometriques passes en parametre - renvoie un string */
	private static String executeString(Geodatabase data, GM_Object isoGeom1, GM_Object isoGeom2, String query)  {
        
        // ceci sera le result
        String result = "";
        
        try {        
            // initialise la connection a Oracle
            Connection conn = data.getConnection();

            // vide la table TEMP_REQUETE
            String update = "DELETE FROM TEMP_REQUETE";
            PreparedStatement ps = conn.prepareStatement(update);
            ps.executeUpdate();
            ps.close();            

            // convertit isoGeom1 en type sdoapi, et l'ecrit dans la table TEMP_REQUETE
            STRUCT str = (STRUCT) GeometryConvertor.GM_Object2Sdo(isoGeom1, conn);
            update = "INSERT INTO TEMP_REQUETE VALUES (0,?)";
            ps = conn.prepareStatement(update);
            ps.setObject(1, str);
            ps.executeUpdate();
            ps.close();            

            // convertit isoGeom2 en type sdoapi, et l'ecrit dans la table TEMP_REQUETE
            str = (STRUCT) GeometryConvertor.GM_Object2Sdo(isoGeom2, conn);
            update = "INSERT INTO TEMP_REQUETE VALUES (1,?)";
            ps = conn.prepareStatement(update);
            ps.setObject(1, str);
            ps.executeUpdate();
            ps.close();            

            // execute la requete
            ps = conn.prepareStatement(query);            
            ResultSet rs = (ResultSet)ps.executeQuery();
            while (rs.next()) 
                result = rs.getString(1);
            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
             
        // renvoi du resultat
        return result;
    }
    
        
    /** 2 objets geometriques passes en parametre - renvoie un double */
    private static double executeDouble(Geodatabase data, GM_Object isoGeom1, GM_Object isoGeom2, String query)  {
        
        // ceci sera le result
        double result = 0.0;
        
        try {
            // la requete renvoie un BigDecimal, qu'on convertira ensuite en double
            Object obj = null;
            BigDecimal theBig = null;

            // initialise la connection a Oracle
            Connection conn = data.getConnection();
            
            // vide la table TEMP_REQUETE
            String update = "DELETE FROM TEMP_REQUETE";
            PreparedStatement ps = conn.prepareStatement(update);
            ps.executeUpdate();
            ps.close();            

            // convertit isoGeom1 en type sdoapi, et l'ecrit dans la table TEMP_REQUETE
            STRUCT str = (STRUCT) GeometryConvertor.GM_Object2Sdo(isoGeom1, conn);
            update = "INSERT INTO TEMP_REQUETE VALUES (0,?)";
            ps = conn.prepareStatement(update);
            ps.setObject(1, str);
            ps.executeUpdate();
            ps.close();            

            // convertit isoGeom2 en type sdoapi, et l'ecrit dans la table TEMP_REQUETE
            str = (STRUCT) GeometryConvertor.GM_Object2Sdo(isoGeom2, conn);
            update = "INSERT INTO TEMP_REQUETE VALUES (1,?)";
            ps = conn.prepareStatement(update);
            ps.setObject(1, str);
            ps.executeUpdate();
            ps.close();
            
            // execute la requete
            ps = conn.prepareStatement(query);
            ResultSet rs = (ResultSet)ps.executeQuery();
            while (rs.next()) 
                obj = rs.getObject(1);
            rs.close();
            ps.close();

            // conversion du resultat en double
            theBig = (BigDecimal)obj;
            result = theBig.doubleValue();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        // renvoi du resultat
        return result;
    }
    

    /** 1 objet geometrique passe en parametre - renvoie un double */
	private static double executeDouble(Geodatabase data, GM_Object isoGeom1, String query)  {
        
        // ceci sera le result
        double result = 0.0;
        
        try {
            // la requete renvoie un BigDecimal, qu'on convertira ensuite en double
            Object obj = null;
            BigDecimal theBig = null;

            // initialise la connection a Oracle
            Connection conn = data.getConnection(); 

            // vide la table TEMP_REQUETE
            String update = "DELETE FROM TEMP_REQUETE";
            PreparedStatement ps = conn.prepareStatement(update);
            ps.executeUpdate();
            ps.close();            

            // convertit isoGeom1 en type sdoapi, et l'ecrit dans la table TEMP_REQUETE
            STRUCT str = (STRUCT) GeometryConvertor.GM_Object2Sdo(isoGeom1, conn);
            update = "INSERT INTO TEMP_REQUETE VALUES (0,?)";
            ps = conn.prepareStatement(update);
            ps.setObject(1, str);
            ps.executeUpdate();
            ps.close();            

            // execute la requete
            ps = conn.prepareStatement(query);
            ResultSet rs = (ResultSet)ps.executeQuery();
            while (rs.next()) 
                obj = rs.getObject(1);
            rs.close();
            ps.close();

            // conversion du resultat en double
            theBig = (BigDecimal)obj;
            result = theBig.doubleValue();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        // renvoi du resultat
        return result;
    }
  
    
    /** 1 classe de FT_Feature et 1 GM_Objet en parametre - renvoie une liste d'identifiants */ 
	private static List executeFeatureList(Geodatabase data, GM_Object geom, Class theClass, String theQuery) {
        
        // ceci sera le resultat
        List idList = new ArrayList();
        
        try {
            // recherche du tableName
            String tableName = data.getMetadata(theClass).getTableName();
            String pkColumn = data.getMetadata(theClass).getIdColumnName();
            // initialise la connection a Oracle
            Connection conn = data.getConnection();

            // vide la table TEMP_REQUETE
            String update = "DELETE FROM TEMP_REQUETE";
            PreparedStatement ps = conn.prepareStatement(update);
            ps.executeUpdate();
            ps.close();           

            // convertit geom en type sdoapi, et l'ecrit dans la table TEMP_REQUETE
            STRUCT str = (STRUCT) GeometryConvertor.GM_Object2Sdo(geom, conn);
            update = "INSERT INTO TEMP_REQUETE VALUES (0,?)";
            ps = conn.prepareStatement(update);
            ps.setObject(1, str);
            ps.executeUpdate();
            ps.close();

            // execute la requete
            String query = "SELECT t."+pkColumn+" FROM "+tableName+" t, TEMP_REQUETE tt ";
            query = query+theQuery;
            ps = conn.prepareStatement(query);
            ResultSet rs = (ResultSet)ps.executeQuery();
            while (rs.next()) 
                idList.add(rs.getObject(1));
            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // renvoi du resultat
        return idList;    
    }
  
    

    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /// methodes de la SDOAPI ////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public static boolean isSimple (GM_Object isoGeom)  {
        try {
            GeometryFactory gf = OraSpatialManager.getGeometryFactory();
            Geometry sdoGeom = IsoAndSdo.iso2sdoapi(gf,isoGeom);
            return sdoGeom.isSimple();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }        
    }
    
    
    public static int getCoorDim (GM_Object isoGeom)  {
        try {
            GeometryFactory gf = OraSpatialManager.getGeometryFactory();
            Geometry sdoGeom = IsoAndSdo.iso2sdoapi(gf,isoGeom);
            return sdoGeom.getCoordinateDimension();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    
    public static double length (GM_Object isoGeom)  {
        try {
            GeometryFactory gf = OraSpatialManager.getGeometryFactory();
            Geometry sdoGeom = IsoAndSdo.iso2sdoapi(gf,isoGeom);
            if (sdoGeom.getGeometryType() == Curve.class) return ((Curve)sdoGeom).length();
            else if (sdoGeom.getGeometryType() == LineString.class) return ((LineString)sdoGeom).length();
            else if (sdoGeom.getGeometryType() == CurveString.class) return ((CurveString)sdoGeom).length();
            else if (sdoGeom.getGeometryType() == MultiCurve.class) return ((MultiCurve)sdoGeom).length();
            else if (sdoGeom.getGeometryType() == MultiCurveString.class) return ((MultiCurveString)sdoGeom).length();
            else if (sdoGeom.getGeometryType() == MultiLineString.class) return ((MultiLineString)sdoGeom).length();
            else {
                System.out.println("### ATTENTION oracle.SpatialQuery.length() : mauvais type - renvoie -1 ###");
                return -1.0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1.0;
        }
    }
    
    
    public static double area (GM_Object isoGeom)  {
        try {
            GeometryFactory gf = OraSpatialManager.getGeometryFactory();
            Geometry sdoGeom = IsoAndSdo.iso2sdoapi(gf,isoGeom);
            if (sdoGeom.getGeometryType() == Surface.class) return ((Surface)sdoGeom).area();
            else if (sdoGeom.getGeometryType() == CurvePolygon.class) return ((CurvePolygon)sdoGeom).area();
            else if (sdoGeom.getGeometryType() == Polygon.class) return ((Polygon)sdoGeom).area();
            else if (sdoGeom.getGeometryType() == MultiSurface.class) return ((MultiSurface)sdoGeom).area();
            else if (sdoGeom.getGeometryType() == MultiCurvePolygon.class) return ((MultiCurvePolygon)sdoGeom).area();
            else if (sdoGeom.getGeometryType() == MultiPolygon.class) return ((MultiPolygon)sdoGeom).area();
            else {
                System.out.println("### ATTENTION oracle.SpatialQuery.area() : mauvais type - renvoie -1 ###");
                return -1.0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1.0;
        }
    }

    
    public static double perimeter (GM_Object isoGeom)  {
        try {
            GeometryFactory gf = OraSpatialManager.getGeometryFactory();
            Geometry sdoGeom = IsoAndSdo.iso2sdoapi(gf,isoGeom);
            if (sdoGeom.getGeometryType() == Surface.class) return ((Surface)sdoGeom).perimeter();
            else if (sdoGeom.getGeometryType() == CurvePolygon.class) return ((CurvePolygon)sdoGeom).perimeter();
            else if (sdoGeom.getGeometryType() == Polygon.class) return ((Polygon)sdoGeom).perimeter();
            else if (sdoGeom.getGeometryType() == MultiSurface.class) return ((MultiSurface)sdoGeom).perimeter();
            else if (sdoGeom.getGeometryType() == MultiCurvePolygon.class) return ((MultiCurvePolygon)sdoGeom).perimeter();
            else if (sdoGeom.getGeometryType() == MultiPolygon.class) return ((MultiPolygon)sdoGeom).perimeter();
            else {
                System.out.println("### ATTENTION oracle.SpatialQuery.perimeter() : mauvais type - renvoie -1 ###");
                return -1.0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1.0;
        }
    }
    

    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /// requetes geometriques Oracle sur des objets //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////    
    public static double distance (Geodatabase data, double tolerance, GM_Object geom1, GM_Object geom2) {
        String query = "SELECT SDO_GEOM.SDO_DISTANCE(t.geom,tt.geom,"+tolerance+") FROM TEMP_REQUETE t, TEMP_REQUETE tt WHERE t.GID=0 AND tt.GID=1";
        double result = OracleSpatialQuery.executeDouble(data,geom1,geom2,query);
        return result;
    }
    
    
    public static DirectPosition centroid (Geodatabase data, double tolerance, GM_Object geom1) {
        String query = "SELECT SDO_GEOM.SDO_CENTROID(t.geom,"+tolerance+") FROM TEMP_REQUETE t WHERE t.GID = 0";
        GM_Point thePoint = (GM_Point)OracleSpatialQuery.executeGeometry(data,geom1,query);
        return thePoint.getPosition();
    }
        
    
    public static DirectPosition representativePoint (Geodatabase data, double tolerance, GM_Object geom1) {
        String query = "SELECT SDO_GEOM.SDO_POINTONSURFACE(t.geom,"+tolerance+") FROM TEMP_REQUETE t WHERE t.GID = 0";
        GM_Point thePoint = (GM_Point)OracleSpatialQuery.executeGeometry(data,geom1,query);
        return thePoint.getPosition();
    }
    
    
    public static GM_Polygon convexHull (Geodatabase data, double tolerance, GM_Object geom1) {
        String query = "SELECT SDO_GEOM.SDO_CONVEXHULL(t.geom,"+tolerance+") FROM TEMP_REQUETE t WHERE t.GID = 0";
        return (GM_Polygon)OracleSpatialQuery.executeGeometry(data,geom1,query);
    }        
        
        
    public static GM_Object buffer (Geodatabase data, double tolerance, double radius, GM_Object geom1) {        
        String query = "SELECT SDO_GEOM.SDO_BUFFER(t.geom,"+radius+","+tolerance+") FROM TEMP_REQUETE t WHERE t.GID = 0";
        return OracleSpatialQuery.executeGeometry(data,geom1,query);
    }
                   
            
    public static boolean intersects (Geodatabase data, double tolerance, GM_Object geom1, GM_Object geom2) {  
        String query = "SELECT SDO_GEOM.RELATE(t.geom,'disjoint',tt.geom,"+tolerance+") FROM TEMP_REQUETE t, TEMP_REQUETE tt WHERE t.GID=0 AND tt.GID=1";
        String result = OracleSpatialQuery.executeString(data,geom1,geom2,query);
        if (result.compareToIgnoreCase("FALSE") == 0) {
            query = "SELECT SDO_GEOM.RELATE(t.geom,'touch',tt.geom,"+tolerance+") FROM TEMP_REQUETE t, TEMP_REQUETE tt WHERE t.GID=0 AND tt.GID=1";
            result = OracleSpatialQuery.executeString(data,geom1,geom2,query);
            if (result.compareToIgnoreCase("FALSE") == 0)
                return true;
        }
        return false;
    }
    
    
    public static boolean equals (Geodatabase data, double tolerance, GM_Object geom1, GM_Object geom2) {      
        String query = "SELECT SDO_GEOM.RELATE(t.geom,'equal',tt.geom,"+tolerance+") FROM TEMP_REQUETE t, TEMP_REQUETE tt WHERE t.GID=0 AND tt.GID=1";
        String result = OracleSpatialQuery.executeString(data,geom1,geom2,query);
        if (result.compareToIgnoreCase("TRUE") == 0) return true;
        else return false;
    }        
        
        
    public static GM_Object union (Geodatabase data, double tolerance, GM_Object geom1, GM_Object geom2) {            
        String query = "SELECT SDO_GEOM.SDO_UNION(t.geom,tt.geom,"+tolerance+") FROM TEMP_REQUETE t, TEMP_REQUETE tt WHERE t.GID = 0 AND tt.GID = 1";
        return OracleSpatialQuery.executeGeometry(data,geom1,geom2,query);
    }

    
    public static GM_Object intersection (Geodatabase data, double tolerance, GM_Object geom1, GM_Object geom2) {            
        String query = "SELECT SDO_GEOM.SDO_INTERSECTION(t.geom,tt.geom,"+tolerance+") FROM TEMP_REQUETE t, TEMP_REQUETE tt WHERE t.GID = 0 AND tt.GID = 1";
        return OracleSpatialQuery.executeGeometry(data,geom1,geom2,query);        
    }
     
    
    public static GM_Object difference (Geodatabase data, double tolerance, GM_Object geom1, GM_Object geom2) {     
        String query = "SELECT SDO_GEOM.SDO_DIFFERENCE(t.geom,tt.geom,"+tolerance+") FROM TEMP_REQUETE t, TEMP_REQUETE tt WHERE t.GID = 0 AND tt.GID = 1";
        return OracleSpatialQuery.executeGeometry(data,geom1,geom2,query);
    }

    
    public static GM_Object symmetricDifference (Geodatabase data, double tolerance, GM_Object geom1, GM_Object geom2) {  
        String query = "SELECT SDO_GEOM.SDO_XOR(t.geom,tt.geom,"+tolerance+") FROM TEMP_REQUETE t, TEMP_REQUETE tt WHERE t.GID = 0 AND tt.GID = 1";
        return OracleSpatialQuery.executeGeometry(data,geom1,geom2,query);
    }    
    
    
    public static boolean contains (Geodatabase data, double tolerance, GM_Object geom1, GM_Object geom2) {      
        String query = "SELECT SDO_GEOM.RELATE(t.geom,'contains',tt.geom,"+tolerance+") FROM TEMP_REQUETE t, TEMP_REQUETE tt WHERE t.GID=0 AND tt.GID=1";
        String result = OracleSpatialQuery.executeString(data,geom1,geom2,query);
        if (result.compareToIgnoreCase("FALSE") == 0) return false;
        else return true;
    }    
    

    public static boolean contains (Geodatabase data, double tolerance, GM_Object geom1, DirectPosition P) {
        GM_Point g = new GM_Point(P);
        return OracleSpatialQuery.contains(data,tolerance,geom1,g);
    }
    

    public static GM_Envelope mbr (Geodatabase data, GM_Object geom1) {        
        String query = "SELECT SDO_GEOM.SDO_MIN_MBR_ORDINATE(t.geom,1) FROM TEMP_REQUETE t WHERE t.GID = 0";
        double Xmin = OracleSpatialQuery.executeDouble(data,geom1,query);
        query = "SELECT SDO_GEOM.SDO_MAX_MBR_ORDINATE(t.geom,1) FROM TEMP_REQUETE t WHERE t.GID = 0";
        double Xmax = OracleSpatialQuery.executeDouble(data,geom1,query);
        query = "SELECT SDO_GEOM.SDO_MIN_MBR_ORDINATE(t.geom,2) FROM TEMP_REQUETE t WHERE t.GID = 0";
        double Ymin = OracleSpatialQuery.executeDouble(data,geom1,query);
        query = "SELECT SDO_GEOM.SDO_MAX_MBR_ORDINATE(t.geom,2) FROM TEMP_REQUETE t WHERE t.GID = 0";
        double Ymax = OracleSpatialQuery.executeDouble(data,geom1,query);                
        return new GM_Envelope(Xmin,Xmax,Ymin,Ymax);
    }


    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /// requetes Oracle pour charger des objets //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public static List loadAllFeatures(Geodatabase data, Class theClass, GM_Object geom) {
        String query = "WHERE tt.GID=0 AND SDO_RELATE(t.geom,tt.geom,'mask=ANYINTERACT querytype=WINDOW') = 'TRUE'";
        return OracleSpatialQuery.executeFeatureList(data,geom,theClass,query);
    }

    
    public static List loadAllFeatures(Geodatabase data, Class theClass, GM_Object geom, double dist) {
        String query = " WHERE tt.GID=0 AND SDO_WITHIN_DISTANCE(t.geom,tt.geom,'distance="+dist+"') = 'TRUE'";
        return OracleSpatialQuery.executeFeatureList(data,geom,theClass,query);
    }    
    
   
   	 //////////////////////////////////////////////////////////////////////////////////////////////////////
     /// calcul de buffer "optimise" pour les agregats ... ////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////    
     public static GM_Object bufferAgregat (Geodatabase data, double tolerance, double radius, GM_Object geom) {   

         // ceci sera le result
         GM_Object result = null;
                        
         try { 
             // initialise la connection a Oracle
             Connection conn = data.getConnection();
             Statement stm = conn.createStatement(); 
             
             // vide la table TEMP_REQUETE
             String update = "DELETE FROM TEMP_REQUETE";
             stm.executeQuery(update);
             
             // test
             if (!(GM_Aggregate.class).isAssignableFrom(geom.getClass())) {
                 System.out.println("le GM_Object doit etre un agregat (GM_Aggregate ou sous-classe)");
                 System.out.println("le calcul de buffer renvoie NULL");
                 return null;                 
             }
             
             // copie des elements de l'agregat dans la table TEMP_REQUETE
             GM_Aggregate aggr = (GM_Aggregate)geom;
             aggr.initIterator();
             int i=0;             
             while (aggr.hasNext()) {
                 i++;
                 STRUCT str = (STRUCT) GeometryConvertor.GM_Object2Sdo(aggr.next(), conn);
                 update = "INSERT INTO TEMP_REQUETE VALUES ("+i+",?)";
                 PreparedStatement ps = conn.prepareStatement(update);
                 ps.setObject(1, str);
                 ps.executeUpdate();
                 ps.close();
             }
             
             // calcul des buffers
             update = "UPDATE TEMP_REQUETE t SET t.GEOM = (";
             update = update+"SELECT SDO_GEOM.SDO_BUFFER(tt.geom, "+radius+","+tolerance+") FROM TEMP_REQUETE tt WHERE tt.gid = t.gid)";      
             stm.executeQuery(update);
             
             // linearisation des geometries (sinon ca plante !!)
             update = "UPDATE TEMP_REQUETE t SET t.GEOM = (";
             update = update+"SELECT SDO_GEOM.SDO_ARC_DENSIFY(tt.geom, "+tolerance;
             update = update+", 'arc_tolerance=0,1') FROM TEMP_REQUETE tt WHERE tt.gid = t.gid)";
             stm.executeQuery(update);
                  
             // calcul de l'agregat result de l'union des buffer (c'est ca qui optimise en theorie )
             String query = "SELECT SDO_AGGR_UNION(MDSYS.SDOAGGRTYPE(t.geom, "+tolerance+")) FROM TEMP_REQUETE t";
             ResultSet rs = (ResultSet)stm.executeQuery(query);
             while (rs.next()) 
                   result = (GM_Object)(GeometryConvertor.Sdo2GM_Object (rs.getObject(1)));
             stm.close();
            
         } catch (Exception e) {
             e.printStackTrace();
         }
            
         return result;
     }
   

    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /// calcul d'index spatial ///////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void spatialIndex(Geodatabase data, Class clazz) {
        try {
            Connection conn = data.getConnection();
            Statement stm = conn.createStatement();
            String tableName = data.getMetadata(clazz).getTableName().toUpperCase();
            String columnName = data.getMetadata(clazz).getGeomColumnName().toUpperCase();
                        
            // on cherche si un index spatial existe - a revoir pour la multi-representation
            String query = "SELECT INDEX_NAME FROM USER_SDO_INDEX_INFO WHERE TABLE_NAME='"+tableName+"' AND COLUMN_NAME='"+columnName+"'" ;
            stm.executeQuery(query);
            ResultSet rs = (ResultSet)stm.executeQuery(query);
            String indexName = "";
            while (rs.next()) {
                indexName = rs.getString(1);
            }
            
            // creation de l'index
            if (indexName.compareTo("") == 0) {
                System.out.println("index spatial sur "+tableName+" inexistant...");
                                                
                // on est oblige de faire ceci, sinon message d'erreur d'Oracle : nom d'index trop long...
                if (tableName.length()>24) indexName = tableName.substring(0,24)+"_spidx";
                else indexName = tableName+"_spidx";
                
                System.out.println("creation index...");
                query = "CREATE INDEX "+indexName+" ON "+tableName+"("+columnName+")";
                query=query+"INDEXTYPE IS MDSYS.SPATIAL_INDEX ";
              //  query=query+"PARAMETERS ('TABLESPACE = USER_IDX')";
                stm.executeQuery(query);
                System.out.println("index spatial sur "+tableName+" cree (R-Tree) - nom : "+indexName);   
            } else {
                System.out.println("index spatial sur "+tableName+" existant...");
                                                
                System.out.println("reconstruction index...");
                query = "ALTER INDEX "+indexName+" REBUILD";
                stm.executeQuery(query);
                System.out.println("index spatial sur "+tableName+" reconstruit (R-Tree) - nom : "+indexName);   
            }                

            stm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }        
            


    
                
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /// calcul d'emprise ///////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void mbr(Geodatabase data, Class clazz) {
        try {   
            Connection conn = data.getConnection();
            Statement stm = conn.createStatement();            
            String tableName = data.getMetadata(clazz).getTableName().toUpperCase();
            String columnName = data.getMetadata(clazz).getGeomColumnName().toUpperCase();
            
            // on recupere l'enveloppe avec la fonction "sdo__aggr_mbr" d'Oracle
            GM_Surface rect = null;       
            String query = "SELECT SDO_AGGR_MBR("+columnName+") FROM "+tableName;
            ResultSet rs = (ResultSet)stm.executeQuery(query);
            while (rs.next()) {
                rect = (GM_Surface) GeometryConvertor.Sdo2GM_Object(rs.getObject(1));
            }

            // on recupere les coordonnees du rectangle
            DirectPositionList theCoord = rect.exteriorCoord();
            double Xmin = theCoord.get(0).getX();
            double Ymin = theCoord.get(0).getY();
            double Xmax = theCoord.get(2).getX();
            double Ymax = theCoord.get(2).getY();
            
            // on recupere le DimInfo d'Oracle et le met a jour - revoir pour geometrie multiple
            query = "SELECT DIMINFO FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME = '"+tableName+"'";
            rs = (ResultSet)stm.executeQuery(query);
            String sdoDimArrayString = "MDSYS.SDO_DIM_ARRAY(";
            ARRAY sqlDiminfo = null;
            while (rs.next()) 
                sqlDiminfo = (oracle.sql.ARRAY)rs.getObject(1);
                
            int dim;

            // si pas de diminfo, on se met en 2D et on affecte une tolerance par defaut !!!!
            if (sqlDiminfo == null) {
                sdoDimArrayString=sdoDimArrayString+"MDSYS.SDO_DIM_ELEMENT('X', "+Xmin+", "+Xmax+", 0.0000000005),";
                sdoDimArrayString=sdoDimArrayString+"MDSYS.SDO_DIM_ELEMENT('Y', "+Ymin+", "+Ymax+", 0.0000000005))";                    

            } else {

                dim = sqlDiminfo.length();

                if (dim == 2) {
                    ResultSet X = (ResultSet)sqlDiminfo.getResultSet(1,1);
                    X.next();
                    STRUCT XX = (STRUCT)X.getObject(2);
                    Object[] attrX = XX.getAttributes();
                    double Xtol = ((BigDecimal)attrX[3]).doubleValue();

                    ResultSet Y = (ResultSet)sqlDiminfo.getResultSet(2,1);
                    Y.next();
                    STRUCT YY = (STRUCT)Y.getObject(2);
                    Object[] attrY = YY.getAttributes();
                    double Ytol = ((BigDecimal)attrY[3]).doubleValue();

                    sdoDimArrayString=sdoDimArrayString+"MDSYS.SDO_DIM_ELEMENT('X', "+Xmin+", "+Xmax+", "+Xtol+"),";
                    sdoDimArrayString=sdoDimArrayString+"MDSYS.SDO_DIM_ELEMENT('Y', "+Ymin+", "+Ymax+", "+Ytol+"))";
                }

                if (dim == 3) {
                    ResultSet X = (ResultSet)sqlDiminfo.getResultSet(1,1);
                    X.next();
                    STRUCT XX = (STRUCT)X.getObject(2);
                    Object[] attrX = XX.getAttributes();
                    double Xtol = ((BigDecimal)attrX[3]).doubleValue();

                    ResultSet Y = (ResultSet)sqlDiminfo.getResultSet(2,1);
                    Y.next();
                    STRUCT YY = (STRUCT)Y.getObject(2);
                    Object[] attrY = YY.getAttributes();
                    double Ytol = ((BigDecimal)attrY[3]).doubleValue();

                    ResultSet Z = (ResultSet)sqlDiminfo.getResultSet(3,1);
                    Z.next();
                    STRUCT ZZ = (STRUCT)Z.getObject(2);
                    Object[] attrZ = ZZ.getAttributes();
                    double Ztol = ((BigDecimal)attrZ[3]).doubleValue();

                    sdoDimArrayString=sdoDimArrayString+"MDSYS.SDO_DIM_ELEMENT('X', "+Xmin+", "+Xmax+", "+Xtol+"),";
                    sdoDimArrayString=sdoDimArrayString+"MDSYS.SDO_DIM_ELEMENT('Y', "+Ymin+", "+Ymax+", "+Ytol+"),";
                    sdoDimArrayString=sdoDimArrayString+"MDSYS.SDO_DIM_ELEMENT('Z', -1000.0, 10000.0, "+Ztol+"))";
                }
            }
            
            // on ecrit le resultat de la mise a jour dans Oracle - revoir pour les geometries multiples
            String update = "UPDATE USER_SDO_GEOM_METADATA SET DIMINFO = "+sdoDimArrayString+" WHERE TABLE_NAME = '"+tableName+"'";;
            stm.executeUpdate(update);
            
            // close and commit
            System.out.println(tableName+" : emprise de la table renseignee");
            stm.close();
            conn.commit();
       
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
            
            
     
    
            
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /// intialisation des metadonnees ////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void initGeomMetadata (List metadataList, Connection conn) {    
        try {
            int n = metadataList.size();
            Statement stm = conn.createStatement();
            String query = "SELECT TABLE_NAME, COLUMN_NAME, DIMINFO, SRID FROM USER_SDO_GEOM_METADATA";
            ResultSet rs = (ResultSet)stm.executeQuery(query);
            while (rs.next()) {
                String sqlTableName = rs.getString(1);             
                for (int i=0; i<n; i++) {
                    String arrayTableName = ((Metadata)metadataList.get(i)).getTableName();
                    if (arrayTableName != null)  // ceci car pour les classes abstraites, pas de table name
                    // On compare le nom de table de user_sdo_geom_metadata et le nom de table issu du mapping
                    if (sqlTableName.compareToIgnoreCase(arrayTableName) == 0) {
                        Metadata metadataElt = (Metadata)metadataList.get(i);
                        // colonne portant la geometrie
                        String sqlGeomcolumn = rs.getString(2);
                        metadataElt.setGeomColumnName(sqlGeomcolumn);
                        // SRID
                     //   int sqlSRID = rs.getInt(4);
                     //   metadataElt.setSRID(sqlSRID);
                        // DimInfo -> pour la tolerance et l'enveloppe
                        oracle.sql.ARRAY sqlDiminfo = (oracle.sql.ARRAY)rs.getObject(3);
                        if (sqlDiminfo != null) {
                            int dim = sqlDiminfo.length();
                            // on est en 2D
                            if (dim == 2) {
                                metadataElt.setDimension(2);
                                metadataElt.setTolerance(new double[2]);
                                ResultSet X = (ResultSet)sqlDiminfo.getResultSet(1,1);
                                X.next();
                                oracle.sql.STRUCT XX = (oracle.sql.STRUCT)X.getObject(2);
                                Object[] attrX = XX.getAttributes();
                                double Xmin = ((BigDecimal)attrX[1]).doubleValue();
                                double Xmax = ((BigDecimal)attrX[2]).doubleValue();
                                metadataElt.setTolerance(0,((BigDecimal)attrX[3]).doubleValue());

                                ResultSet Y = (ResultSet)sqlDiminfo.getResultSet(2,1);
                                Y.next();
                                oracle.sql.STRUCT YY = (oracle.sql.STRUCT)Y.getObject(2);
                                Object[] attrY = YY.getAttributes();
                                double Ymin = ((BigDecimal)attrY[1]).doubleValue();
                                double Ymax = ((BigDecimal)attrY[2]).doubleValue();

                                DirectPosition UpperCorner = new DirectPosition (Xmax,Ymax);
                                DirectPosition LowerCorner = new DirectPosition (Xmin,Ymin);
                                GM_Envelope theEnvelope = new GM_Envelope(UpperCorner,LowerCorner);
                                metadataElt.setEnvelope(theEnvelope);                  
                                metadataElt.setTolerance(1,((BigDecimal)attrY[3]).doubleValue());
                            }
                            // on est en 3D
                            else if (dim == 3) {
                                metadataElt.setDimension(3);
                                metadataElt.setTolerance (new double[3]);
                                ResultSet X = (ResultSet)sqlDiminfo.getResultSet(1,1);
                                X.next();
                                oracle.sql.STRUCT XX = (oracle.sql.STRUCT)X.getObject(2);
                                Object[] attrX = XX.getAttributes();
                                double Xmin = ((BigDecimal)attrX[1]).doubleValue();
                                double Xmax = ((BigDecimal)attrX[2]).doubleValue();
                                metadataElt.setTolerance(0,((BigDecimal)attrX[3]).doubleValue());

                                ResultSet Y = (ResultSet)sqlDiminfo.getResultSet(2,1);
                                Y.next();
                                oracle.sql.STRUCT YY = (oracle.sql.STRUCT)Y.getObject(2);
                                Object[] attrY = YY.getAttributes();
                                double Ymin = ((BigDecimal)attrY[1]).doubleValue();
                                double Ymax = ((BigDecimal)attrY[2]).doubleValue();
                                metadataElt.setTolerance(1,((BigDecimal)attrY[3]).doubleValue());

                                ResultSet Z = (ResultSet)sqlDiminfo.getResultSet(3,1);
                                Z.next();
                                oracle.sql.STRUCT ZZ = (oracle.sql.STRUCT)Z.getObject(2);
                                Object[] attrZ = ZZ.getAttributes();
                                double Zmin = ((BigDecimal)attrZ[1]).doubleValue();
                                double Zmax = ((BigDecimal)attrZ[2]).doubleValue();

                                DirectPosition UpperCorner = new DirectPosition (Xmax,Ymax,Zmax);
                                DirectPosition LowerCorner = new DirectPosition (Xmin,Ymin,Zmin);
                                GM_Envelope theEnvelope = new GM_Envelope(UpperCorner,LowerCorner);
                                metadataElt.setEnvelope(theEnvelope);         
                                metadataElt.setTolerance(2,((BigDecimal)attrZ[3]).doubleValue());
                            }
                        
                        // on n'est ni en 2D, ni en 3D !
                        else throw new Exception("Problème pour lire le DIMINFO de user_sdo_geom_metadata");
                        }
                        
                        // sortie de boucle quand on a trouve une egalite entre tableName de user_sdo_geom_metadata et tableName du mapping
                        break;              
                    }           
                }
            }
            stm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
