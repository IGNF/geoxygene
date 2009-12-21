/**
 * 
 */
package fr.ign.cogit.geoxygene.datatools.hibernate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.odmg.OQLQuery;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.Metadata;
import fr.ign.cogit.geoxygene.datatools.postgis.PostgisSpatialQuery;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * @author Julien Perret
 *
 */
public class GeodatabaseHibernate implements Geodatabase {
	static Logger logger=Logger.getLogger(GeodatabaseHibernate.class.getName());

	/////////////////////////////////////////////////////////////////////////////////////////
	///// attributs /////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	protected List<Metadata> metadataList;         // liste des metadonnnees pour les classes persistantes.
	Session session;
	Transaction transaction;

	/////////////////////////////////////////////////////////////////////////////////////////
	///// constructeur //////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/** Constructeur. */
	public GeodatabaseHibernate() {
		session = HibernateUtil.getSessionFactory().openSession();
		initMetadata();
	}
	
	public void dropAndCreate() {
		SchemaExport export = new SchemaExport(HibernateUtil.getConfiguration());
		export.drop(true, true);
		export.create(true, true);
	}

	/** Renseigne l'attribut _metadataList. */
	protected void initMetadata()  {
		metadataList = new ArrayList<Metadata>();
		Map<?,?> allClassesMetadata = HibernateUtil.getSessionFactory().getAllClassMetadata();

		for (Object key:allClassesMetadata.keySet()) {
			if (logger.isDebugEnabled()) logger.debug("key = "+key);
			ClassMetadata classMetadata = (ClassMetadata) allClassesMetadata.get(key);
			if (logger.isDebugEnabled())logger.debug("metadata = "+classMetadata);
			String className = (classMetadata.getEntityName());
			if (logger.isDebugEnabled())logger.debug("entity name = "+className);
			
			Metadata metadataElt = new Metadata();
			metadataElt.setClassName(className);
			String[] propertyNames = classMetadata.getPropertyNames();
			if (logger.isDebugEnabled()) for (int i=0 ; i<propertyNames.length ; i++) logger.debug("property name "+i+" = "+propertyNames[i]);
			
			if (classMetadata instanceof AbstractEntityPersister) {
				metadataElt.setTableName(((AbstractEntityPersister)classMetadata).getRootTableName());
				metadataElt.setIdFieldName(((AbstractEntityPersister)classMetadata).getIdentifierPropertyName());
				metadataElt.setIdColumnName(((AbstractEntityPersister)classMetadata).getIdentifierColumnNames()[0]);
				if (logger.isDebugEnabled()) {
					logger.debug("table name = "+metadataElt.getTableName());
					logger.debug("id field name = "+metadataElt.getIdFieldName());
					logger.debug("id column name = "+metadataElt.getIdColumnName());
				}
			}
			metadataList.add(metadataElt);
		}
	}

	@Override
	public void abort() {session.cancelQuery();}

	@Override
	public void begin() {transaction=session.beginTransaction();}

	@Override
	public void checkpoint() {transaction.commit();transaction=session.beginTransaction();}

	@Override
	public void clearCache() {session.clear();}

	@Override
	public void close() {session.close();}

	@Override
	public void commit() {transaction.commit();}

	@Override
	public int countObjects(Class<?> theClass) {return session.createQuery("from "+theClass.getSimpleName()).list().size();}

	@Override
	public void deletePersistent(Object obj) {session.delete(obj);}

	@Override
	public void exeSQL(String query) {session.createSQLQuery(query);}

	@Override
	public void exeSQLFile(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String query = "";
			String line = reader.readLine();
			while (line!=null) {
				query += line.trim();
				line = reader.readLine();
			}
			if (!query.isEmpty()) exeSQL(query);
		} catch (FileNotFoundException e) {
			logger.error("Le fichier "+fileName+" n'existe pas");
		} catch (IOException e) {
			logger.error("Erreur pendant l'ex�cution des requ�tes du fichier "+fileName);
			logger.error(e.getMessage());
		}
	}

	@Override
	public List<?> exeSQLQuery(String query) {return session.createSQLQuery(query).list();}

	@SuppressWarnings("deprecation")
	@Override
	public Connection getConnection() {return session.connection();}

	@Override
	public int getDBMS() {return Geodatabase.POSTGIS;}

	@Override
	public List<Metadata> getMetadata() {return metadataList;}

	@Override
	public Metadata getMetadata(Class<?> theClass) {
		for (int i=0; i<metadataList.size(); i++)
			if (theClass.getName().compareTo((metadataList.get(i)).getClassName()) == 0)
				return metadataList.get(i);
		logger.warn("La classe n'est pas mapp�e : "+theClass.getName());
		return null;
	}

	@Override
	public Metadata getMetadata(String theTable) {
		for (int i=0; i<metadataList.size(); i++)
			if ((metadataList.get(i)).getTableName() != null)
				if (theTable.compareToIgnoreCase((metadataList.get(i)).getTableName()) == 0)
					return metadataList.get(i);
		logger.warn("La table n'est pas mapp�e : "+theTable);
		return null;
	}

	@Override
	public boolean isOpen() {return session.isOpen();}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T load(Class<T> clazz, Object id) {return (T) session.load(clazz, (Serializable) id);}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> loadAll(Class<T> featureClass) {return session.createQuery("from "+featureClass.getSimpleName()).list();}

	@Override
	public <T> T loadAll(Class<?> featureClass, Class<T> featureListClass) {
		T result = null;
		try {
			result = featureListClass.newInstance();
		} catch (Exception e)  {
			e.printStackTrace();
			return null;
		}
		List<?> list = session.createQuery("from "+featureClass.getSimpleName()).list();
		Iterator<?> iter = list.iterator();
		try {
			while (iter.hasNext()) {
				Object feature = iter.next();
				result.getClass()
						.getMethod("add", new Class[] { featureClass }).invoke(
								result, new Object[] { feature });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends FT_Feature> FT_FeatureCollection<T> loadAllFeatures(Class<?> featureClass) {
		return loadAll(featureClass,FT_FeatureCollection.class);
	}

	@Override
	public <T> T loadAllFeatures(Class<?> featureClass,Class<T> featureListClass) {
		return loadAll(featureClass,featureListClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T loadAllFeatures(Class<?> featureClass, Class<T> featureListClass, String param, String value) {
		return (T) session.createSQLQuery("select x from "+featureClass.getName()+" where "+param+" = "+value).list();
	}

	@Override
	public <T extends FT_Feature> FT_FeatureCollection<T> loadAllFeatures(Class<T> featureClass, GM_Object geom) {return null;}

	/** Cree une requete pour permettre de charger tous les objets a partir d'une liste d'identifants.
	 * Usage interne. */
	private String createInQuery (List<?> idList, String className) {
		String result = "select x from "+className+" where id in (";
		StringBuffer strbuff = new StringBuffer(result);
		Iterator<?> i = idList.iterator();
		while (i.hasNext()) {
			int k = ((Number)i.next()).intValue();
			strbuff.append(k);
			strbuff.append(",");
		}
		result = strbuff.toString();
		result = result.substring(0,result.length()-1);
		result = result+")";
		return result;
	}
	@Override
	public <T> T loadAllFeatures(Class<?> featureClass, Class<T> featureListClass, GM_Object geom) {
		T result = null;
		try {
			result = featureListClass.newInstance();
		} catch (Exception e)  {
			logger.error("Impossible de créer une nouvelle instance de la classe "+featureListClass.getName());
			e.printStackTrace();
			return null;
		}
		if ((FT_Feature.class).isAssignableFrom(featureClass)) {
			// on cherche la liste des identifiants
			List<?> idList = PostgisSpatialQuery.loadAllFeatures(this, featureClass, geom);
			// charge tous les objets dont on a trouve l'identifiant
			if (idList.size() > 0) {
				String query = createInQuery(idList,featureClass.getName());
				try {
					List<?> list = session.createSQLQuery(query).list();
					Iterator<?> iter = list.iterator();
					// on Récupère le srid attribu� à cette classe dans les métadonnées
					Metadata metadata = this.getMetadata(featureClass);
					int srid = -1;
					if (metadata!=null&&metadata.getSRID()!=0) {
						srid=metadata.getSRID();
					} else {
						// si cette classe ne contient pas de métadonnées ou si c'est une classe mère de la classe stockée dans le SGBD
						// on Récupère le premier élément (s'il existe) et ses métadonnées.
						if (iter.hasNext()) {
							FT_Feature feature = (FT_Feature) iter.next();
							metadata = this.getMetadata(feature.getClass());
							if (metadata!=null) {
								srid=metadata.getSRID();
							}
							if (feature.getGeom()!=null) feature.getGeom().setCRS(srid);
							result.getClass().getMethod("add", new Class[]{FT_Feature.class}).invoke(result,new Object[] {feature});
						}
					}
					while (iter.hasNext()) {
						FT_Feature feature = (FT_Feature) iter.next();
						if (feature.getGeom()!=null) feature.getGeom().setCRS(srid);
						result.getClass().getMethod("add", new Class[]{FT_Feature.class}).invoke(result,new Object[] {feature});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			logger.warn("loadAllFeatures() : La classe passee en parametre n'est pas une sous-classe de FT_Feature");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends FT_Feature> FT_FeatureCollection<T> loadAllFeatures(Class<T> featureClass, GM_Object geom, double dist) {
		FT_FeatureCollection<T> result = new FT_FeatureCollection<T>();
		if ((FT_Feature.class).isAssignableFrom(featureClass)) {
			// on cherche la liste des identifiants
			List<?> idList = PostgisSpatialQuery.loadAllFeatures(this, featureClass, geom, dist);
			// charge tous les objets dont on a trouve l'identifiant
			if (idList.size() > 0) {
				String query = createInQuery(idList,featureClass.getName());
				try {
					List<T> list = session.createSQLQuery(query).list();
					Iterator<T> iter = list.iterator();
					// on Récupère le srid attribu� à cette classe dans les métadonnées
					Metadata metadata = this.getMetadata(featureClass);
					int srid = -1;
					if (metadata!=null&&metadata.getSRID()!=0) {
						srid=metadata.getSRID();
					} else {
						// si cette classe ne contient pas de métadonnées ou si c'est une classe mère de la classe stockée dans le SGBD
						// on Récupère le premier élément (s'il existe) et ses métadonnées.
						if (iter.hasNext()) {
							T feature = iter.next();
							metadata = this.getMetadata(feature.getClass());
							if (metadata!=null) {
								srid=metadata.getSRID();
							}
							if (feature.getGeom()!=null) feature.getGeom().setCRS(srid);
							result.add(feature);
						}
					}
					while (iter.hasNext()) {
						T feature = iter.next();
						if (feature.getGeom()!=null) feature.getGeom().setCRS(srid);
						result.add(feature);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			logger.warn("loadAllFeatures() : La classe passee en parametre n'est pas une sous-classe de FT_Feature");
		}
		return result;
	}

	@Override
	public <T> T loadAllFeatures(Class<?> featureClass, Class<T> featureListClass, GM_Object geom, double dist) {return null;}
	@Override
	public <T extends FT_Feature> FT_FeatureCollection<T> loadAllFeatures(FeatureType featureType) {return null;}
	@Override
	public List<?> loadOQL(String query, Object param) {
		// TODO à implémenter
		return null;
	}
	@Override
	public void makePersistent(Object obj) {session.saveOrUpdate(obj);}
	//public void makePersistent(Object obj) {session.persist(obj);}	
	@Override
	public int maxId(Class<?> theClass) {return 0;}
	@Override
	public void mbr(Class<?> clazz) {}
	@Override
	public int minId(Class<?> theClass) {return 0;}
	@Override
	public OQLQuery newOQLQuery() {return null;}
	@Override
	public void refreshRepository(File newRepository) throws Exception {}
	@Override
	public void spatialIndex(Class<?> clazz) {}
}
