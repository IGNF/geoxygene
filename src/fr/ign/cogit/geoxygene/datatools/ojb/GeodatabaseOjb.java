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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.odmg.HasBroker;
import org.apache.ojb.odmg.OJB;
import org.odmg.DList;
import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.OQLQuery;
import org.odmg.Transaction;

import fr.ign.cogit.geoxygene.datatools.Metadata;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;




/** 
 * Implementation d'une Geodatabase utilisant OJB comme mappeur.
 * On utilise la partie ODMG de OJB.
 * Ne pas utiliser directement :
 * classe a specialiser en fonction du SGBD geographique utilise.
 * Attention pour les entiers : 
 * pour Oracle, caster en BigDecimal,
 * pour Postgis caster en Long ...
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 */



public class GeodatabaseOjb {

    /////////////////////////////////////////////////////////////////////////////////////////
    ///// attributs /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////    
    protected Connection _conn;           // connection JDBC
	protected Implementation _odmg;       // implementation ODMG
	protected Database _db;               // interaction avec une base ODMG
	protected Transaction _tx;            // represente une transaction
	protected List _metadataList;         // liste des metadonnnees pour les classes persistantes.


    
    /////////////////////////////////////////////////////////////////////////////////////////
    ///// constructeur //////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////        
    /** Constructeur.  
     * @param jcdAlias : l'alias de connection dans repository_database.xml */
    GeodatabaseOjb(String jcdAlias) {           
        initODMG(jcdAlias);
        initConnection();
        initMetadata();              
    }
    
    
    /** Constructeur avec la connection par defaut dans repository_database.xml */
	protected GeodatabaseOjb () {
    	this (null);
    }
            
   
     
    /////////////////////////////////////////////////////////////////////////////////////
    /// initialisation des attributs ////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /** Initialise la base ODMG et une transaction */
    protected void initODMG (String jcdAlias) {  
        try {	
             _odmg = OJB.getInstance();   
             _db = _odmg.newDatabase(); 
             if (jcdAlias != null)
             	_db.open(jcdAlias, Database.OPEN_READ_WRITE) ;
             else 
				_db.open(null, Database.OPEN_READ_WRITE) ;
             _tx = _odmg.newTransaction();
        } catch ( Exception except ) {
        	System.err.println(" ### PROBLEME A LA LECTURE DES FICHIERS DE MAPPING OJB ... ### ");
			System.err.println(" ### PROGRAMME ARRETE ! ### ");  
			System.err.println(""); 	
            except.printStackTrace( );
            System.exit(0);
         }
    }
        
	/** Initialise la connection JDBC. */
	protected void initConnection() {
		 try {
			 _tx.begin();
			 PersistenceBroker broker = ((HasBroker) _tx).getBroker();
			 _conn = broker.serviceConnectionManager().getConnection();
			 _tx.commit();
		 } catch(Exception e) { 
			e.printStackTrace();
		 }
	 }
              
    /** Renseigne l'attribut _metadataList. */
	protected void initMetadata()  {
        try {
             _tx.begin();            
            PersistenceBroker broker = ((HasBroker) _tx).getBroker();
            DescriptorRepository desc = broker.getDescriptorRepository();
            Iterator enDesc = desc.getDescriptorTable().values().iterator();
            _metadataList = new ArrayList();
            
            while (enDesc.hasNext()) { 
				ClassDescriptor cd = (ClassDescriptor) enDesc.next();
            	String className = (cd.getClassNameOfObject());
            	if (!	(className.equals("org.apache.ojb.broker.util.sequence.HighLowSequence")
            			|| className.equals("org.apache.ojb.odmg.collections.DListImpl_2")
            			|| className.equals("org.apache.ojb.odmg.collections.DListEntry_2")
            			|| className.equals("org.apache.ojb.odmg.collections.DListImpl")
            			|| className.equals("org.apache.ojb.odmg.collections.DListEntry")	)) {
	                Metadata metadataElt = new Metadata();                
	                metadataElt.setClassName(className);
	                metadataElt.setTableName(cd.getFullTableName());
	                FieldDescriptor[] fdPK = cd.getPkFields();
	                if (fdPK.length == 0) {
	                    System.out.println("WARNING - classe sans identifiant : "+cd.getClassNameOfObject());
	                    continue;
	                }
	                if (fdPK.length > 1) {
	                    if (cd.getClassNameOfObject().compareToIgnoreCase("org.apache.ojb.broker.util.sequence.HighLowSequence") != 0)
	                        System.out.println("WARNING - cle primaire composee : "+cd.getClassNameOfObject());
	                        continue;
	                }
	                metadataElt.setIdColumnName(fdPK[0].getColumnName());
					metadataElt.setIdFieldName(fdPK[0].getAttributeName());
	                _metadataList.add(metadataElt);
            	}
            }
             _tx.commit();
         
        } catch (Exception e) {
			System.err.println(" ### PROBLEME A LA LECTURE DES FICHIERS DE MAPPING OJB ... ### ");
			System.err.println(" ### PROGRAMME ARRETE ! ### ");  
			System.err.println(""); 	
			e.printStackTrace( );
			System.exit(0);
        }
    }
    
           
    
    /////////////////////////////////////////////////////////////////////////////////////////
    ///// gestion des transactions //////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////        
    /** Ouvre une transaction. */
    public void begin() {
        try {
            _tx.begin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Commit la transaction sans la fermer. */
    public void checkpoint() {
        try {
            _tx.checkpoint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
    /** Commite et ferme la transaction. */
    public void commit() {
        try {
            _tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Annule et ferme la transaction. */
    public void abort() {
        try {
            _tx.abort();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Renvoie true si la transaction est active. */
    public boolean isOpen() {
        return _tx.isOpen();
    }
    
    /** Ferme la connection (libere les ressources). */
    public void close() {
        try {
            _db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
               
    /** Vide le cache de la transaction.
     A appeler a l'interieur d'une transaction ouverte. */    
    public void clearCache() {
        PersistenceBroker broker = ((HasBroker) _tx).getBroker();
        broker.clearCache();
    }
    
        
    
    /////////////////////////////////////////////////////////////////////////////////////////
    ///// gestion de la persistance /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////        
    /** Rend persistant un objet. 
        A appeler a l'interieur d'une transaction ouverte.*/
    public void makePersistent(Object obj) {
        try {
            _db.makePersistent(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
       
    /** Detruit un objet persistant. 
        A appeler a l'interieur d'une transaction ouverte. */
    public void deletePersistent(Object obj) {
        try {
            _db.deletePersistent(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }        
                
    
    
    /////////////////////////////////////////////////////////////////////////////////////////
    ///// chargement d'objets ///////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    /** Charge l'objet d'identifiant id. 
        Passer un Integer pour id, si l'identifiant est un int.
        Renvoie null si l'objet d'identifiant id n'existe pas.*
        A appeler a l'interieur d'une transaction ouverte. */
    public java.lang.Object load(Class clazz, Object id) {
        try {                
            OQLQuery query = _odmg.newOQLQuery();
            query.create("select x from "+clazz.getName()+" where id = $0");
            query.bind( id );
            DList result = (DList) query.execute();
            if (result.size() > 0) return result.get(0);
            else {                  
                System.out.println("objet non trouve - id = "+id); 
                return null;
            }
        } catch (Exception ee) {
            ee.printStackTrace();
            return null;
        }
    }            
    
    /** Charge tous les objets persistants de la classe theClass et les met dans une liste.
        A appeler a l'interieur d'une transaction ouverte. */    
    public List loadAll(Class theClass) {
        try {
            OQLQuery query = _odmg.newOQLQuery();
            query.create("select x from "+theClass.getName());
            DList result = (DList) query.execute();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }        
    
	/** Charge tous les FT_Feature de la classe theClass dans la classe FT_FeatureCollection.
		A appeler a l'interieur d'une transaction ouverte. 
		La classe theClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide. */
	public FT_FeatureCollection loadAllFeatures(Class featureClass) {
	  FT_FeatureCollection result = new FT_FeatureCollection();
	  if ((FT_Feature.class).isAssignableFrom(featureClass)) {
		try {
			OQLQuery query = _odmg.newOQLQuery();
			query.create("select x from "+featureClass.getName());
			DList list = (DList) query.execute();
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				FT_Feature feature = (FT_Feature) iter.next();
				result.add(feature);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	  } else {
		System.out.println("loadAllFeatures() : La classe passee en parametre n'est pas une sous-classe de FT_Feature");
	  }
	  return result;
	}
    
	/** Charge tous les FT_Feature de la classe theClass dans la classe featureListClass.
		A appeler a l'interieur d'une transaction ouverte. 
		La classe theClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide.
		La classe featureListClass doit etre un sous classe de FT_FeatureCollection.*/
	public Object loadAllFeatures(Class featureClass, Class featureListClass) {
	  Object result = null;
	  try {
		  result = featureListClass.newInstance();
	  } catch (Exception e)  {
		  e.printStackTrace();
	  }
	  if ((FT_Feature.class).isAssignableFrom(featureClass)) {
	  	try {
            OQLQuery query = _odmg.newOQLQuery();
            query.create("select x from "+featureClass.getName());
            DList list = (DList) query.execute();
            Iterator iter = list.iterator();
            while (iter.hasNext()) {
                FT_Feature feature = (FT_Feature) iter.next();      
                result.getClass().getMethod("add", new Class[]{FT_Feature.class}).invoke(result,new Object[] {feature});
            }
	  	} catch (Exception e) {
	  		e.printStackTrace();
	  	}
	  } else {
		System.out.println("loadAllFeatures() : La classe passee en parametre n'est pas une sous-classe de FT_Feature");
	  }
	  return result;
    }
    



	/////////////////////////////////////////////////////////////////////////////////////////
	///// OQL ///////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////                    
    /** Execute la requete OQL query, la lie avec le parametre param, et met le resultat dans une liste.
        A appeler a l'interieur d'une transaction ouverte.  
        On peut passer null pour param, si on ne souhaite lier la requete a aucune variable. */
    public  List loadOQL(String query, Object param) {
        OQLQuery oqlQuery = _odmg.newOQLQuery();
        try {
            oqlQuery.create(query);
            oqlQuery.bind( param );
            DList result = (DList) oqlQuery.execute();        
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }        

    /** Cree une requete OQL */
    public OQLQuery newOQLQuery() {
        return _odmg.newOQLQuery() ;
    }
    
    
        
    /////////////////////////////////////////////////////////////////////////////////////////
    ///// Metadonnees sur le mapping ////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////    
    /** Renvoie le tableau des metadonnees. */
    public List getMetadata() {
        return _metadataList;
    }
    
    /** Renvoie les metadonnees de la classe theClass. 
        theClass doit etre une classe definie dans le mapping.*/
    public Metadata getMetadata(Class theClass) {
        for (int i=0; i<_metadataList.size(); i++)
            if (theClass.getName().compareTo(((Metadata)_metadataList.get(i)).getClassName()) == 0)
                return (Metadata)_metadataList.get(i);
        System.out.println("La classe n'est pas dans le fichier de mapping : "+theClass.getName());
        return null;
    }
                
    /** Renvoie les metadonnees de la classe mappee avec la table theTable.
        theTable doit etre une table definie dans le mapping. 
        Si theTable est mappee avec plusieurs classes, en renvoie une. */
    public Metadata getMetadata(String theTable) {
        for (int i=0; i<_metadataList.size(); i++)
            if (((Metadata)_metadataList.get(i)).getTableName() != null)
                if (theTable.compareToIgnoreCase(((Metadata)_metadataList.get(i)).getTableName()) == 0)
                    return (Metadata)_metadataList.get(i);
        System.out.println("La table n'est pas dans le fichier de mapping : "+theTable);
        return null;
    }
    

               
    /////////////////////////////////////////////////////////////////////////////////////////
    ///// SQL ///////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    /** Renvoie la connection JDBC sous-jacente. */
    public Connection getConnection() {
        return _conn;
    }        
            
    /** Execute une commande SQL.
        Cette commande ne doit pas renvoyer de resultat : INSERT, UPDATE, DELETE, mais pas SELECT. */
    public void exeSQL(String query) {
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            stm.executeQuery(query);
            stm.close();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }        
    
    /** Execute une requete et met les resultats dans une liste de tableau d'objets.
        Les tableaux ont la taille du nombre d'objets demandes dans le SELECT.
        Exemple d'utilisation du resultat : 
        <tt> List edges = db.exeSQLQuery("SELECT edgeID FROM tableName WHERE ..."). </tt>
        Pour recuperer le premier resultat :
        <tt> edgeId = ( (BigDecimal) ((Object[]) (edges.get(0)) )[0] ).intValue(); </tt>  */
    public List exeSQLQuery(String query) {
        List result = new ArrayList();
        try {
            Connection conn = getConnection();
            Statement stm = conn.createStatement();
            ResultSet rs = (ResultSet)stm.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int nbCol = rsmd.getColumnCount();
            while (rs.next()) {
                Object[] array = new Object[nbCol];
                for (int i=1; i<= nbCol; i++)
                    array[i-1] = rs.getObject(i);
                result.add(array);
            }                
            stm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }          
    
       
       
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // getters ODMG ///////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    public Implementation getODMGImplementation() {
    	return _odmg;
    }
   
	public Database getODMGDatabase() {
    	return _db;
    }
    
	public Transaction getODMGTransaction() {
    	return _tx;
    }
   	
}
