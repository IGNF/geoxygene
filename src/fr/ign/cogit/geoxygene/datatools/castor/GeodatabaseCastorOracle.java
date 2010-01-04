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

package fr.ign.cogit.geoxygene.datatools.castor;

//import feature.FT_Feature;
//import feature.FT_FeatureCollection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Metadata;


/**  NON MAINTENU.
 *   Implementation d'une Geodatabase utilisant Castor comme mappeur et Oracle comme SGBDR geographique.
 *   N'EST PLUS MAINTENU DEPUIS LE PASSAGE A OJB.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 */



public class GeodatabaseCastorOracle/* implements Geodatabase */{

	/////////////////////////////////////////////////////////////////////////////////////////
	///// attributs /////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	private String _oxygeneMapping;     // variable d'environnememt GEOXYGENE_MAPPING
	private Connection _conn;           // connection JDBC
	//    private JDO _jdo;                   // JDO Castor
	//    private Database _db;               // Database Castor
	private PrintWriter _writer;        // destination des messages de sortie
	private List<?> _metadataList;         // liste des metadonnnees pour les classes persistantes.
	private static String  _databaseName="cogit";   // nom de la base qui apparait dans le fichier database.xml





	/////////////////////////////////////////////////////////////////////////////////////////
	///// constructeurs /////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/** Constructeur, avec un writer pour l'affichage des messages Castor. */
	public GeodatabaseCastorOracle(PrintWriter writer) {
		try {
			initWriter(writer);
			initEnv();
			if (writer != null) writer.println("Convertisseur de g?om?trie pour Oracle initialis?.");
			initJDO(_databaseName, writer);
			if (writer != null) writer.println("Moteur JDO de Castor initialis?.");
			initDatabase();
			if (writer != null) writer.println("Geodatabase initialis?e.");
			initConnection();
			if (writer != null) writer.println("Connection JDBC initialis?e.");
			initMetadata();
			if (writer != null) writer.println("M?tadonn?es initialis?es.");
		} catch (Exception e) {this._writer.println(e.getMessage());}
	}

	/** Constructeur, sans writer donc sans affichage des messages Castor. */
	public GeodatabaseCastorOracle() {
		this(null);
	}

	/** Constructeur sans initialiser la Geodatabase si le boolean en param?tre vaut false.
        Utilise uniquement dans EasyLoader pour creer le fichier de mapping. */
	public GeodatabaseCastorOracle(boolean Castor) {
		try {
			initWriter(null);
			initEnv();
			initJDO(_databaseName, null);
			if (Castor == true)  {
				initDatabase();
				initConnection();
				initMetadata();
			} else {
				initConnectionWithoutJDO();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());}
	}





	/////////////////////////////////////////////////////////////////////////////////////
	/// initialisation des attributs ////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Initialise le Writer. Si on avait passe null en parametre du constructeur de DataSource, alors
         initialise un writer sur la sortie standard qui est utilise uniquement pour afficher les messages de cette classe. */
	private void initWriter (PrintWriter writer) {
		if (writer != null) {
			this._writer = writer;
		} else {
			this._writer = new PrintWriter(System.out,true);
		}
	}

	/** Recupere la valeur de la variable d'environnement GEOXYGENE_MAPPING  */
	// on essaie d'assurer l'independance vis a vis du systeme d'exploitation
	private void initEnv()  {
		try {       // ceci est pour Windows
			String[] command = {"cmd.exe","/c","set"};
			Process pr = Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader( pr.getInputStream()));
			String ligne = br.readLine();
			while (ligne != null) {
				if ((ligne.length() >= 15) && (ligne.substring(0,15).compareTo("GEOXYGENE_MAPPING") == 0)) {
					this._oxygeneMapping = ligne.substring(16);
					break;
				}
				ligne = br.readLine();
			}
		} catch (Exception e1) {
			try {       // ceci est pour Solaris / Linux
				String command = "env";
				Process pr = Runtime.getRuntime().exec(command);
				BufferedReader br = new BufferedReader(new InputStreamReader( pr.getInputStream()));
				String ligne = br.readLine();
				while (ligne != null) {
					if ((ligne.length() >= 15) && (ligne.substring(0,15).compareTo("GEOXYGENE_MAPPING") == 0)) {
						this._oxygeneMapping = ligne.substring(16);
						break;
					}
					ligne = br.readLine();
				}
			} catch (Exception e2) {
				try {       // ceci est pour Windows
					String[] command = {"command.com","/c","set"};
					Process pr = Runtime.getRuntime().exec(command);
					BufferedReader br = new BufferedReader(new InputStreamReader( pr.getInputStream()));
					String ligne = br.readLine();
					while (ligne != null) {
						if ((ligne.length() >= 15) && (ligne.substring(0,15).compareTo("GEOXYGENE_MAPPING") == 0)) {
							this._oxygeneMapping = ligne.substring(16);
							break;
						}
						ligne = br.readLine();
					}
				} catch (Exception e3) {
					System.out.println("GeOxygene : env, cmd ou command ne sont pas reconnus");
					e1.printStackTrace();
					e2.printStackTrace();
					e3.printStackTrace();
				}
			}
		}
		if (this._oxygeneMapping == null)
			this._writer.println(" ### La variable d'environnement GEOXYGENE_MAPPING n'est pas renseignée ###");
	}

	/**
	 *  Initialise le JDO (moteur utilis? par Castor pour se connecter ? la base). 
	 * @param databaseName
	 * @param writer
	 */
	private void initJDO (String databaseName, PrintWriter writer) {
		/*        try {
             File databasePath = new File(_oxygeneMapping);
             File databaseFile = new File(databasePath,"database.xml");
             URL databaseURL = databaseFile.toURL();
            _jdo = new JDO();
            if (writer != null) {_jdo.setLogWriter( writer );}
            _jdo.setConfiguration( databaseURL.toString());
            _jdo.setDatabaseName( databaseName );
        } catch ( Exception except ) {
                except.printStackTrace( _writer );
         }

		 */    }

	/** Ouvre une Database Castor (repr?sente une connection ouverte au SGBD) */
	private void initDatabase()  {
		/*          try {
            _db = _jdo.getDatabase();
          } catch ( Exception except ) {
              except.printStackTrace( _writer );
          }
		 */    }

	/** Initialise la connection JDBC. */
	//  On cree une connection JDBC avec les parametres Castor
	private void initConnection() {
		/*         try {
             _conn =  DatabaseRegistry.getDatabaseRegistry(_jdo.getDatabaseName()).createConnection();
         } catch(Exception e) {
            e.printStackTrace();
         }
		 */     }

	/** Initialise la connection JDBC sans passer par JDO. */
	//  On parse la fichier "database.xml" a l'aide des fonctionnalites XML de Castor.
	private void initConnectionWithoutJDO() {
		/*         try {
             File databasePath = new File(_oxygeneMapping);
             File databaseFile = new File(databasePath,"database.xml");
             FileReader reader = new FileReader(databaseFile);
             datatools.castor.conf.Database _db = (datatools.castor.conf.Database)Unmarshaller.unmarshal(datatools.castor.conf.Database.class,reader);
             java.lang.Class.forName(_db.getDriver().getClassName()).newInstance();
             _conn = DriverManager.getConnection(_db.getDriver().getUrl(), _db.getDriver().getUserName(),
                                                _db.getDriver().getPassword());

         } catch(Exception e) {
            e.printStackTrace();
         }
		 */    }

	/** Renseigne l'attribut _metadataList. */
	private void initMetadata()  {
		/*        try {
            MappingResolver mr = DatabaseRegistry.getDatabaseRegistry(_jdo.getDatabaseName()).getMappingResolver();
            Enumeration listDesc = mr.listDescriptors();
            _metadataList = new ArrayList();

            while (listDesc.hasMoreElements()) {
                JDOClassDescriptor cd = (JDOClassDescriptor)listDesc.nextElement();
                Metadata metadataElt = new Metadata();
                metadataElt.setClassName(cd.getJavaClass().getName());
                metadataElt.setTableName(cd.getTableName());
                if (cd.getIdentity() != null) {
                    JDOFieldDescriptor fd = (JDOFieldDescriptor)cd.getIdentity();
                    metadataElt.setIdColumnName(fd.getSQLName()[0]);
                } else _writer.println("WARNING - classe sans identifiant : "+cd.getJavaClass().getName());
                _metadataList.add(metadataElt);
            }

            // on recupere les parametres de la table user_sdo_geom_metadata d'Oracle
            SpatialQuery.initGeomMetadata(_metadataList, _conn) ;

        } catch (Exception e) {
            e.printStackTrace();
        }
		 */    }





	/////////////////////////////////////////////////////////////////////////////////////////
	///// gestion des transactions //////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/** Ouvre une transaction. */
	public void begin() {
		/*        try {
            _db.begin();
        } catch (Exception e) {
            e.printStackTrace();
        }
		 */    }

	/** Commit la transaction sans la fermer. */
	public void checkpoint() {
		/*       try {
            _db.commit();
            _db.begin();
        } catch (Exception e) {
            e.printStackTrace();
        }
		 */   }

	/** Commite et ferme la transaction. */
	public void commit() {
		/*        try {
            _db.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
		 */    }

	/** Annule et ferme la transaction. */
	public void abort() {
		/*        try {
            _db.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        }
		 */    }

	/** Renvoie true si la transaction est active. */
	/*   public boolean isOpen() {
        return _db.isActive();
    }*/

	/** Ferme la connection (libere les ressources). */
	public void close() {
		/*        try {
            _db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
		 */    }

	/** NON IMPLEMENTE */
	public void clearCache() {
		System.out.println("clearCache() : fonction non implementee pour Castor");
	}



	/////////////////////////////////////////////////////////////////////////////////////////
	///// gestion de la persistance /////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Rend persistant un objet.
        A appeler a l'interieur d'une transaction ouverte.
	 * @param obj
	 */
	public void makePersistent(Object obj) {
		/*        try {
            _db.create(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
		 */    }

	/** 
	 * Detruit un objet persistant.
        A appeler a l'intwrieur d'une transaction ouverte. 
	 * @param obj
	 */
	public void deletePersistent(Object obj) {
		/*        try {
            _db.remove(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
		 */    }

	/////////////////////
	/** A REVOIR (encapsuler dans le makePersistent). */
	/*   public void update(java.lang.Object obj) throws org.exolab.castor.jdo.ClassNotPersistenceCapableException, org.exolab.castor.jdo.TransactionNotInProgressException, org.exolab.castor.jdo.PersistenceException {
        _db.update(obj);
    }
	 */




	/////////////////////////////////////////////////////////////////////////////////////////
	///// chargement d'objets ///////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/** Charge l'objet d'identifiant id.
        Passer un Integer pour id, si l'identifiant est un int.
        Renvoie null si l'objet d'identifiant id n'existe pas.*
        A appeler a l'interieur d'une transaction ouverte. */
	/*   public java.lang.Object load(Class clazz, Object id) {
        try {
            Object obj  = _db.load(clazz,id);
            return obj;
        } catch (org.exolab.castor.jdo.ObjectNotFoundException e) {
            System.out.println("objet non trouve - id = "+id);
            return null;
        } catch (Exception ee) {
            ee.printStackTrace();
            return null;
        }
    }     */

	/** Charge tous les objets persistants de la classe theClass et les met dans une liste.
        A appeler a l'interieur d'une transaction ouverte. */
	/*  public List loadAll(Class theClass) {
        List result = new ArrayList();
        QueryResults  results;
        OQLQuery oql;
        try {
            oql = _db.getOQLQuery("SELECT x FROM "+theClass.getName()+" x");
            results = oql.execute();
            while (results.hasMore()) { result.add(results.next());}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }   */

	/** Charge tous les FT_Feature de la classe theClass.
        A appeler a l'interieur d'une transaction ouverte.
        La classe theClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide. */
	/*  public FT_FeatureCollection loadAllFeatures(Class featureClass, Class featureListClass) {
//		FT_FeatureList result = new FT_FeatureList();
		FT_FeatureCollection result = null;
	  try {
		  result = (FT_FeatureCollection)featureListClass.newInstance();
	  } catch (Exception e)  {
		  e.printStackTrace();
	  }
        QueryResults  results;
        OQLQuery oql;
        if ((FT_Feature.class).isAssignableFrom(featureClass)) {
            try {
                String tableName = getMetadata(featureClass).getTableName();
                oql = _db.getOQLQuery("SELECT x FROM "+featureClass.getName()+" x");
                results = oql.execute();
                while (results.hasMore()) { result.add((FT_Feature)results.next());}
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            _writer.println("loadAllFeatures() : La classe passee en parametre n'est pas une sous-classe de FT_Feature");
        }
        return result;
    }*/

	/** OBSOLETE Inactif avec Castor (renvoie un loadAllFeatures). */
	/*  public FT_FeatureList expressLoadAllFeatures(Class theClass) {
        return loadAllFeatures(theClass);
    }*/

	/** OBSOLETE Charge tous les FT_Feature de la classe theClass appartenant a une dalle.
        La table doit avoir ete mise en coherence avec un dallage existant.
        A appeler a l'interieur d'une transaction ouverte.
        La classe theClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide. */
	/*  public FT_FeatureList loadAllFeatures(Class theClass, int dalleNumber) {
        FT_FeatureList result = new FT_FeatureList();
        QueryResults  results;
        OQLQuery oql;
        if ((FT_Feature.class).isAssignableFrom(theClass)) {
            try {
                oql = _db.getOQLQuery("SELECT x FROM "+theClass.getName()+" x WHERE x.dalle_id="+dalleNumber);
                results = oql.execute();
                while (results.hasMore()) { result.add((FT_Feature)results.next());}
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            _writer.println("loadAllFeatures() : La classe passee en parametre n'est pas une sous-classe de FT_Feature");
        }
        return result;
    }      */

	/** Charge tous les FT_Feature de la classe theClass intersectant le GM_Object geom.
        A appeler a l'interieur d'une transaction ouverte.
        La classe theClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide. */
	/*   public FT_FeatureCollection loadAllFeatures(Class featureClass, Class featureListClass, GM_Object geom) {
//		FT_FeatureList result = new FT_FeatureList();
		FT_FeatureCollection result = null;
	  try {
		  result = (FT_FeatureCollection)featureListClass.newInstance();
	  } catch (Exception e)  {
		  e.printStackTrace();
	  }
        if ((FT_Feature.class).isAssignableFrom(featureClass)) {
            // on cherche la liste des identifiants
            List idList = SpatialQuery.FT_FeatureCollection(this, featureClass, geom);
            // charge tous les objets dont on a trouve l'identifiant
            Iterator i = idList.iterator();
            while (i.hasNext()) {
                int k = ((BigDecimal)i.next()).intValue();
                try {
                    Object obj = _db.load(featureClass, new Integer(k));
                    result.add((FT_Feature)obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
        _writer.println("loadAllFeatures() : La classe passee en parametre n'est pas une sous-classe de FT_Feature");
        }
        return result;
    }      */

	/** Charge tous les FT_Feature de la classe theClass a une distance dist du GM_Object geom.
        Si geom est la geometrie d'un FT_Feature de theClass, alors ce FT_Feature appartiendra au resultat.
        A appeler a l'interieur d'une transaction ouverte.
        La classe theClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide. */
	/*  public FT_FeatureCollection loadAllFeatures(Class featureClass, Class featureListClass, GM_Object geom, double dist) {
//		FT_FeatureList result = new FT_FeatureList();
		FT_FeatureCollection result = null;
	  try {
		  result = (FT_FeatureCollection)featureListClass.newInstance();
	  } catch (Exception e)  {
		  e.printStackTrace();
	  }
        if ((FT_Feature.class).isAssignableFrom(featureClass)) {
            // on cherche la liste des identifiants
            List idList = SpatialQuery.loadAllFeatures(this, featureClass, geom, dist);
            // charge tous les objets dont on a trouve l'identifiant
            Iterator i = idList.iterator();
            while (i.hasNext()) {
                int k = ((BigDecimal)i.next()).intValue();
                try {
                    Object obj = _db.load(featureClass, new Integer(k));
                    result.add((FT_Feature)obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
        _writer.println("loadAllFeatures() : La classe passee en parametre n'est pas une sous-classe de FT_Feature");
        }
        return result;
    }   */

	/** Execute la requete OQL query, la lie avec le parametre param, et met le resultat dans une liste.
        A appeler a l'interieur d'une transaction ouverte.
        On peut passer null pour param, si on ne souhaite lier la requete a aucune variable. */
	/*  public  List loadOQL(String query, Object param) {
        List result = new ArrayList();
        QueryResults  results;
        OQLQuery oql;
        try {
            oql = _db.getOQLQuery(query);
            if (param != null) oql.bind(param);
            results = oql.execute();
            while (results.hasMore()) { result.add(results.next());}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }        */





	/////////////////////////////////////////////////////////////////////////////////////////
	///// OQL ///////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/** Cree une query vide. */
	/*    public org.exolab.castor.jdo.Query getQuery() {
        return _db.getQuery();
    }

    /** Cree une OQLQuery vide. */
	/*    public org.exolab.castor.jdo.OQLQuery getOQLQuery() {
         return _db.getOQLQuery();
    }

    /** Cree une OQLQuery avec la chaine passee en parametre. */
	/*    public org.exolab.castor.jdo.OQLQuery getOQLQuery(java.lang.String str) throws org.exolab.castor.jdo.QueryException {
        return _db.getOQLQuery(str);
    }

    // pour l'implementation de Geodatabase, n'a rien a voir avec Castor, renvoie null
/*    public org.odmg.OQLQuery newOQLQuery() {
        return null;
    }
	 */




	/////////////////////////////////////////////////////////////////////////////////////////
	///// Gestion de l'information spatiale /////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/** Renvoie le tableau des metadonnees. */
	public List<?> getMetadata() {
		return this._metadataList;
	}

	/** Renvoie les metadonnees de la classe theClass.
        theClass doit etre une classe definie dans le mapping.*/
	public Metadata getMetadata(Class<?> theClass) {
		for (int i=0; i<this._metadataList.size(); i++)
			if (theClass.getName().compareTo(((Metadata)this._metadataList.get(i)).getClassName()) == 0)
				return (Metadata)this._metadataList.get(i);
		System.out.println("La classe n'est pas dans le fichier de mapping : "+theClass.getName());
		return null;
	}

	/** Renvoie les metadonnees de la classe mappee avec la table theTable.
        theTable doit etre une table d?finie dans le mapping.
        Si theTable est mappee avec plusieurs classes, en renvoie une. */
	public Metadata getMetadata(String theTable) {
		for (int i=0; i<this._metadataList.size(); i++)
			if (theTable.compareToIgnoreCase(((Metadata)this._metadataList.get(i)).getTableName()) == 0)
				return (Metadata)this._metadataList.get(i);
		System.out.println("La table n'est pas dans le fichier de mapping : "+theTable);
		return null;
	}

	/** 
	 * Calcule l'emprise la table mappee avec la classe.
       La classe doit heriter de FT_Feature, la table doit contenir une geometrie.
	 * @param clazz
	 */
	public void mbr(Class<?> clazz) {
		//       SpatialQuery.mbr(this, clazz);
	}

	/** 
	 * Calcule un index spatial sur la table mappee avec la classe (R-Tree).
       La classe doit heriter de FT_Feature, la table doit contenir une geometrie.
	 * @param clazz
	 */
	public void spatialIndex(Class<?> clazz) {
		//        SpatialQuery.spatialIndex(this, clazz);
	}





	/////////////////////////////////////////////////////////////////////////////////////////
	///// SQL ///////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/** Renvoie la connection JDBC sous-jacente. */
	public Connection getConnection() {
		return this._conn;
	}

	/** Execute une commande SQL.
        Cette commande ne doit pas renvoyer de r?sultat : INSERT, UPDATE, DELETE, mais pas SELECT. */
	public void exeSQL(String query) {
		try {
			Connection conn =getConnection();
			Statement stm = conn.createStatement();
			stm.executeQuery(query);
			stm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Execute une requ?te et met les resultats dans une liste de tableau d'objets.
        Les tableaux ont la taille du nombre d'objets demand?s dans le SELECT.
        Exemple d'utilisation du resultat :
        <tt> List edges = db.exeSQLQuery("SELECT edgeID FROM tableName WHERE ..."). </tt>
        Pour recuperer le premier resultat :
        <tt> edgeId = ( (BigDecimal) ((Object[]) (edges.get(0)) )[0] ).intValue(); </tt>  */
	public List<?> exeSQLQuery(String query) {
		List<Object[]> result = new ArrayList<Object[]>();
		try {
			Connection conn = getConnection();
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
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





	/////////////////////////////////////////////////////////////////////////////////////////
	///// divers ////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/** Renvoie le nombre d'objets persistants de la classe theClass.
        A appeler a l'interieur d'une transaction ouverte. */
	/*   public int countObjects(Class theClass)  {
        BigDecimal nn = null;
        OQLQuery      tOql;
        QueryResults  results;
        try {
            tOql = _db.getOQLQuery( "SELECT COUNT(*) FROM " + theClass.getName());
            results = tOql.execute();
            while (results.hasMore()) {nn = (BigDecimal)results.next();}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nn.intValue();
    }

     /** Renvoie l'identifiant maximum de la classe theClass.
        ATTENTION : La classe passee en parametre doit avoir un champ "id" de type int (marche pour les FT_Feature).
        A appeler a l'interieur d'une transaction ouverte. */
	/*  public int maxId(Class theClass)  {
        BigDecimal nn = null;
        OQLQuery      tOql;
        QueryResults  results;
        try {
            tOql = _db.getOQLQuery( "SELECT MAX(x.id) FROM " + theClass.getName()+" x ");
            results = tOql.execute();
            while (results.hasMore()) {nn = (BigDecimal)results.next();}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nn.intValue();
    }

   /** Renvoie l'identifiant minimum de la classe theClass.
       ATTENTION : La classe pass?e en param?tre doit avoir un champ "id" de type int (marche pour les FT_Feature).
       A appeler ? l'int?rieur d'une transaction ouverte. */
	/*   public int minId(Class theClass)  {
        BigDecimal nn = null;
        OQLQuery      tOql;
        QueryResults  results;
        try {
            tOql = _db.getOQLQuery( "SELECT MIN(x.id) FROM " + theClass.getName()+" x ");
            results = tOql.execute();
            while (results.hasMore()) {nn = (BigDecimal)results.next();}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nn.intValue();
    }




    public List loadAllElements(Class theClass, GM_Object geom) {
        return null;
    }

	 */

}
