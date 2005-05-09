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

package fr.ign.cogit.geoxygene.datatools.ojb;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import oracle.sdoapi.OraSpatialManager;

import org.odmg.DList;
import org.odmg.OQLQuery;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.oracle.SpatialQuery;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;


/** 
 * Implementation d'une Geodatabase 
 * utilisant OJB comme mappeur 
 * et Oracle comme SGBDR geographique.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 */



public class GeodatabaseOjbOracle extends GeodatabaseOjb implements Geodatabase {


	/** Constructeur direct. */
	public GeodatabaseOjbOracle() {
		super();
		updateConnection();                              
		initGeomMetadata();
	}


    /** Constructeur en specialisant GeodatabaseOjb. 
     * Usage interne, appele par GeodatabaseOjbFactory. */
    GeodatabaseOjbOracle(GeodatabaseOjb ojb) {          
		_conn = ojb._conn;
		_odmg = ojb._odmg;
		_db = ojb._db;
		_tx = ojb._tx;
		_metadataList = ojb._metadataList;
        updateConnection();                              
        initGeomMetadata();     
    }
            

    /** Passe des parametres statiques a la classe de conversion GeomGeOxygene2Oracle. */
     private void updateConnection() {
         try {
             GeomGeOxygene2Oracle.CONNECTION = _conn;
			 GeomGeOxygene2Oracle.GF = OraSpatialManager.getGeometryFactory();
			 GeomGeOxygene2Oracle.SRM = OraSpatialManager.getSpatialReferenceManager();
         } catch(Exception e) { 
            e.printStackTrace();
         }
     }
     

    /** Renseigne l'attribut _metadataList. */
    private void initGeomMetadata()  {
          SpatialQuery.initGeomMetadata(_metadataList, _conn) ;
    }

    
	/** Charge tous les FT_Feature de la classe theClass intersectant le GM_Object geom, dans la classe FT_FeatureCollection.
		 A appeler a l'interieur d'une transaction ouverte. 
		 La classe theClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide. */         
	public FT_FeatureCollection loadAllFeatures(Class featureClass, GM_Object geom) {
   	    FT_FeatureCollection result = new FT_FeatureCollection();
		if ((FT_Feature.class).isAssignableFrom(featureClass)) {
			// on cherche la liste des identifiants
			List idList = SpatialQuery.loadAllFeatures(this, featureClass, geom);
			// charge tous les objets dont on a trouve l'identifiant
			if (idList.size() > 0) {
				String query = createInQuery(idList,featureClass.getName());
				OQLQuery oqlQuery = _odmg.newOQLQuery();
				try {
					oqlQuery.create(query);
					DList list = (DList) oqlQuery.execute();
					Iterator iter = list.iterator();
					while (iter.hasNext()) {
					FT_Feature feature = (FT_Feature) iter.next();
					result.add(feature);
				 }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}               
		} else {
			System.out.println("loadAllFeatures() : La classe passee en parametre n'est pas une sous-classe de FT_Feature");
		}
		return result;    
	}      
	       
	                  
	/** Charge tous les FT_Feature de la classe theClass intersectant le GM_Object geom, dans la classe featureListClass.
		 A appeler a l'interieur d'une transaction ouverte. 
		 La classe theClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide. 
		 La classe featureListClass doit etre un sous classe de FT_FeatureCollection.*/         
	public Object loadAllFeatures(Class featureClass, Class featureListClass, GM_Object geom) {
		Object result = null;
		try {
			result = featureListClass.newInstance();
		} catch (Exception e)  {
			e.printStackTrace();
		}
        if ((FT_Feature.class).isAssignableFrom(featureClass)) {
            // on cherche la liste des identifiants
            List idList = SpatialQuery.loadAllFeatures(this, featureClass, geom);
            // charge tous les objets dont on a trouve l'identifiant
            if (idList.size() > 0) {
                String query = createInQuery(idList,featureClass.getName());
                OQLQuery oqlQuery = _odmg.newOQLQuery();
                try {
                    oqlQuery.create(query);
                    DList list = (DList) oqlQuery.execute();
                    Iterator iter = list.iterator();
                    while (iter.hasNext()) {
                    FT_Feature feature = (FT_Feature) iter.next();
					result.getClass().getMethod("add", new Class[]{FT_Feature.class}).invoke(result,new Object[] {feature});
                 }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }               
        } else {
            System.out.println("loadAllFeatures() : La classe passee en parametre n'est pas une sous-classe de FT_Feature");
        }
        return result;    
    }                    


	/** Charge tous les FT_Feature de la classe theClass a une distance dist du GM_Object geom, dans la classe FT_FeatureCollection.
		Si geom est la geometrie d'un FT_Feature de theClass, alors ce FT_Feature appartiendra au resultat.
		 A appeler a l'interieur d'une transaction ouverte. 
		 La classe theClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide.*/         
	public FT_FeatureCollection loadAllFeatures(Class featureClass, GM_Object geom, double dist) {
	    FT_FeatureCollection result = new FT_FeatureCollection();
		if ((FT_Feature.class).isAssignableFrom(featureClass)) {
			// on cherche la liste des identifiants
			List idList = SpatialQuery.loadAllFeatures(this, featureClass, geom, dist);  
			// charge tous les objets dont on a trouve l'identifiant
			if (idList.size() > 0) {
				String query = createInQuery(idList,featureClass.getName());
				OQLQuery oqlQuery = _odmg.newOQLQuery();
				try {
					oqlQuery.create(query);
					DList list = (DList) oqlQuery.execute();
					Iterator iter = list.iterator();
					while (iter.hasNext()) {
						FT_Feature feature = (FT_Feature) iter.next();
						result.add(feature);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}          
			}               
		} else {
			System.out.println("loadAllFeatures() : La classe passee en parametre n'est pas une sous-classe de FT_Feature");
		}
		return result;    
	}   
        
        
	/** Charge tous les FT_Feature de la classe theClass a une distance dist du GM_Object geom, dans la classe featureListClass.
		Si geom est la geometrie d'un FT_Feature de theClass, alors ce FT_Feature appartiendra au resultat.
		 A appeler a l'interieur d'une transaction ouverte. 
		 La classe theClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide.
		La classe featureListClass doit etre un sous classe de FT_FeatureCollection.*/         
	public Object loadAllFeatures(Class featureClass, Class featureListClass, GM_Object geom, double dist) {	        
		Object result = null;
		try {
			result = (FT_FeatureCollection)featureListClass.newInstance();
		} catch (Exception e)  {
			e.printStackTrace();
		}
        if ((FT_Feature.class).isAssignableFrom(featureClass)) {
	        // on cherche la liste des identifiants
	        List idList = SpatialQuery.loadAllFeatures(this, featureClass, geom, dist);     
	        // charge tous les objets dont on a trouve l'identifiant
	        if (idList.size() > 0) {
	            String query = createInQuery(idList,featureClass.getName());											
	            OQLQuery oqlQuery = _odmg.newOQLQuery();
	            try {
	                oqlQuery.create(query);
	                DList list = (DList) oqlQuery.execute();
	                Iterator iter = list.iterator();													                
	                while (iter.hasNext()) {
	                    FT_Feature feature = (FT_Feature) iter.next();
	                    result.getClass().getMethod("add", new Class[]{FT_Feature.class}).invoke(result,new Object[] {feature});
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            }     
	        }               
        } else {
            System.out.println("loadAllFeatures() : La classe passee en parametre n'est pas une sous-classe de FT_Feature");
        }
        return result;    
    }   
    
    
	/** Cree une requete pour permettre de charger tous les objets a partir d'une liste d'identifants.
	 * Usage interne. */
	private String createInQuery (List idList, String className) {
		StringBuffer strBuff = new StringBuffer("select x from "+className+" where id in (");
		Iterator i = idList.iterator();
		while (i.hasNext()) {
			 int k = ((BigDecimal)i.next()).intValue();
			 strBuff.append(k+",");
		}
		String result = strBuff.toString();
		result = result.substring(0,result.length()-1);
		result = result+")";     
		return result;
	}

    
   /** Calcule DANS LE SGBD l'emprise la table mappee avec la classe.
       La classe doit heriter de FT_Feature, la table doit contenir une geometrie. */
    public void mbr(Class clazz) {
       SpatialQuery.mbr(this, clazz);
    }    
        
        
   /** Calcule DANS LE SGBD un index spatial sur la table mappee avec la classe (R-Tree).
       La classe doit heriter de FT_Feature, la table doit contenir une geometrie. */
    public void spatialIndex(Class clazz) {
        SpatialQuery.spatialIndex(this, clazz);
    }   
    
 
	/** Renvoie le nombre d'objets persistants de la classe theClass.
		A appeler a l'interieur d'une transaction ouverte. */
	public int countObjects(Class theClass)  {
		String tableName = getMetadata(theClass).getTableName();
		String query = "select count(*) from "+tableName;	
		BigDecimal nn = null;
		try {
			Connection conn = getConnection();
			Statement stm = conn.createStatement();
			ResultSet rs = (ResultSet)stm.executeQuery(query);
			while (rs.next())          
				nn = (BigDecimal) rs.getObject(1);
			stm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nn.intValue();
	}      
    
    
	 /** Renvoie l'identifiant maximum de la classe theClass.
		ATTENTION : La classe passee en parametre doit avoir un champ "id" de type int (marche pour les FT_Feature).
		A appeler a l'interieur d'une transaction ouverte. */
	public int maxId(Class theClass)  {
		String idColumnName = getMetadata(theClass).getIdColumnName();
		String tableName = getMetadata(theClass).getTableName();
		String query = "select max("+idColumnName+") from "+tableName;
		BigDecimal nn = null;
		try {
			Connection conn = getConnection();
			Statement stm = conn.createStatement();
			ResultSet rs = (ResultSet)stm.executeQuery(query);
			while (rs.next()) 
				nn = (BigDecimal) rs.getObject(1);
			stm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nn.intValue();
	}      
                        
                        
   /** Renvoie l'identifiant minimum de la classe theClass.
	   ATTENTION : La classe passee en parametre doit avoir un champ "id" de type int (marche pour les FT_Feature).
	   A appeler a l'interieur d'une transaction ouverte. */
	public int minId(Class theClass)  {
		String idColumnName = getMetadata(theClass).getIdColumnName();
		String tableName = getMetadata(theClass).getTableName();
		String query = "select min("+idColumnName+") from "+tableName;
		BigDecimal nn = null;
		try {
			Connection conn = getConnection();
			Statement stm = conn.createStatement();
			ResultSet rs = (ResultSet)stm.executeQuery(query);
			while (rs.next()) 
				nn = (BigDecimal) rs.getObject(1);
			stm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nn.intValue();
	} 
	
	
	/** renvoie le type de SGBD associe. */
	public int getDBMS() {
		return Geodatabase.ORACLE;
	}  
	
	
	/** Utilise par EsayLoader pour recharger un fichier de mapping qui a ete modifie. */              
	public void refreshRepository(File newRepository) throws Exception {
		super.refreshRepository(newRepository);
		initGeomMetadata();
	} 
            
            
}
