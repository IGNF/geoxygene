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

import oracle.jdbc.driver.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.apache.ojb.broker.util.batch.BatchConnection;


/**
 * Routine de conversion des tableaux java en VARRAY Oracle.
 * On impose le nom du tableau dans Oracle.
 * double[] -> "VARRAY_OF_DOUBLE".
 * int[] -> "VARRAY_OF_INTEGER".
 * boolean[] -> "VARRAY_OF_BOOLEAN" (défini comme un varray de CHAR(1))
 * string[] -> "VARRAY_OF_STRING" 
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1  
 * 
 */

/* Remarque : on pourrait coder comme GeomGeOxygene2Oracle avec la connection definie statique
 * et initialisee a la construction de GeodatabaseOjbOracle.
 */

public class ArrayGeOxygene2Oracle implements FieldConversion {


    // nom des types Oracle 
    public static String typeArrayDouble = "VARRAY_OF_DOUBLE";
    public static String typeArrayInt = "VARRAY_OF_INTEGER";
    public static String typeArrayBoolean = "VARRAY_OF_BOOLEAN";    
    public static String typeArrayString = "VARRAY_OF_STRING";        


    public Object sqlToJava (Object object) {       
        try {
            Object array = ((ARRAY)object).getArray();
            String type = ((ARRAY)object).getDescriptor().descType();
            if (type.startsWith(typeArrayDouble)) {
                 BigDecimal[] bigdecimals = (BigDecimal[])array;
                 int n = bigdecimals.length;
                 double[] doubles = new double[n];
                 for (int i=0; i<n ;i++)
                     doubles[i] = bigdecimals[i].doubleValue();
                 return doubles;     
            }  else if (type.startsWith(typeArrayInt)) {
                 BigDecimal[] bigdecimals = (BigDecimal[])array;
                 int n = bigdecimals.length;
                 int[] ints = new int[n];
                 for (int i=0; i<n ;i++)
                     ints[i] = bigdecimals[i].intValue();
                 return ints;
            }  else if (type.startsWith(typeArrayBoolean)) {
                 String[] strings = (String[])array;
                 int n = strings.length;
                 boolean[] bools = new boolean[n];
                 for (int i=0; i<n ;i++) {
                    String value = strings[i];
                    boolean bool = false;
                    if (value.compareToIgnoreCase("0") == 0) bool = false;
                    else if (value.compareToIgnoreCase("1") == 0) bool = true;
                    else System.out.println("## PROBLEME VARRAY_OF_BOOLEAN -> ARRAY ##");
                    bools[i] = bool;
                }
                return bools;
            }  else if (type.startsWith(typeArrayString)) {
                 String[] strings = (String[])array;
                 return strings;                
            } else {
                System.out.println("## PROBLEME VARRAY -> ARRAY ##");
                return null;
            }                
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;            
        }
    }
       

    public Object javaToSql(Object object, Connection conn) {     
       String param = null;
       Object objectToConvert = null;;
       if (object instanceof double[]) {
           param = typeArrayDouble;
           objectToConvert = object;
       } else if (object instanceof int[]) {
           param = typeArrayInt;
           objectToConvert = object;
       } else if (object instanceof boolean[]) {
           param = typeArrayBoolean;
           boolean[] bools = (boolean[]) object;
           String[] chars = new String[bools.length];
           for (int i=0; i<bools.length; i++) 
               if (bools[i] == true) chars[i] = "1";
               else chars[i] = "0";
           objectToConvert = chars;
       } else if (object instanceof String[]) {
           param = typeArrayString;
           objectToConvert = object;
       } else 
           System.out.println("## ARRAY EN VARRAY : CAS NON TRAITE ##");
       try {
            ArrayDescriptor arraydesc;
            ARRAY array;
            if (conn instanceof BatchConnection)  {
                OracleConnection oConn = (OracleConnection) ((BatchConnection)conn).getDelegate();   
                arraydesc = ArrayDescriptor.createDescriptor(param, oConn);
                array = new ARRAY(arraydesc,oConn,objectToConvert);
            } else {
                arraydesc = ArrayDescriptor.createDescriptor(param, conn);            
                array = new ARRAY(arraydesc,conn,objectToConvert);
            }
            return array;
       } catch (Exception e) {
            e.printStackTrace();
            return null;
       }                       
    }        
    
    
    public Object javaToSql(Object arg0)  {
        System.out.println("[#### [ERREUR GeOxygene] L'IMPOSSIBLE EST ARRIVE !! ##### ");
        System.out.println("Probleme de conversion GeOxygene -> Oracle");
        return null;
    }
    
}
