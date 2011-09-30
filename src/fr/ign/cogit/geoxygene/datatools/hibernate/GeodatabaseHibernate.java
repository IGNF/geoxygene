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

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.Metadata;
import fr.ign.cogit.geoxygene.datatools.postgis.PostgisSpatialQuery;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;

/**
 * @author Julien Perret
 * 
 */
public class GeodatabaseHibernate implements Geodatabase {
  static Logger logger = Logger.getLogger(GeodatabaseHibernate.class.getName());

  // liste des metadonnnees pour les classes persistantes
  protected List<Metadata> metadataList;
  protected Session session;
  protected Transaction transaction;

  /** Constructeur. */
  public GeodatabaseHibernate() {
    this.session = HibernateUtil.getSessionFactory().openSession();
    this.initMetadata();
  }

  public void dropAndCreate() {
    SchemaExport export = new SchemaExport(HibernateUtil.getConfiguration());
    export.drop(true, true);
    export.create(true, true);
  }

  /** Renseigne l'attribut _metadataList. */
  protected void initMetadata() {
    this.metadataList = new ArrayList<Metadata>();
    Map<?, ?> allClassesMetadata = HibernateUtil.getSessionFactory()
        .getAllClassMetadata();

    for (Object key : allClassesMetadata.keySet()) {
      if (GeodatabaseHibernate.logger.isDebugEnabled()) {
        GeodatabaseHibernate.logger.debug("key = " + key);
      }
      ClassMetadata classMetadata = (ClassMetadata) allClassesMetadata.get(key);
      if (GeodatabaseHibernate.logger.isDebugEnabled()) {
        GeodatabaseHibernate.logger.debug("metadata = " + classMetadata);
      }
      String className = (classMetadata.getEntityName());
      if (GeodatabaseHibernate.logger.isDebugEnabled()) {
        GeodatabaseHibernate.logger.debug("entity name = " + className);
      }

      Metadata metadataElt = new Metadata();
      metadataElt.setClassName(className);
      String[] propertyNames = classMetadata.getPropertyNames();
      if (GeodatabaseHibernate.logger.isDebugEnabled()) {
        for (int i = 0; i < propertyNames.length; i++) {
          GeodatabaseHibernate.logger.debug("property name " + i + " = "
              + propertyNames[i]);
        }
      }

      if (classMetadata instanceof AbstractEntityPersister) {
        metadataElt.setTableName(((AbstractEntityPersister) classMetadata)
            .getRootTableName());
        metadataElt.setIdFieldName(((AbstractEntityPersister) classMetadata)
            .getIdentifierPropertyName());
        metadataElt.setIdColumnName(((AbstractEntityPersister) classMetadata)
            .getIdentifierColumnNames()[0]);

        // FIXME a revoir: aussi l'enveloppe, les srid, la dimension, et
        // d'autres...
        metadataElt.setGeomColumnName("geom");

        if (GeodatabaseHibernate.logger.isDebugEnabled()) {
          GeodatabaseHibernate.logger.debug("table name = "
              + metadataElt.getTableName());
          GeodatabaseHibernate.logger.debug("id field name = "
              + metadataElt.getIdFieldName());
          GeodatabaseHibernate.logger.debug("id column name = "
              + metadataElt.getIdColumnName());
        }
      }
      this.metadataList.add(metadataElt);
    }
  }

  @Override
  public void abort() {
    this.session.cancelQuery();
  }

  @Override
  public void begin() {
    this.transaction = this.session.beginTransaction();
  }

  @Override
  public void checkpoint() {
    this.transaction.commit();
    this.transaction = this.session.beginTransaction();
  }

  @Override
  public void clearCache() {
    this.session.clear();
  }

  @Override
  public void close() {
    this.session.close();
  }

  @Override
  public void commit() {
    this.transaction.commit();
  }

  @Override
  public int countObjects(Class<?> theClass) {
    return this.session.createQuery("from " + theClass.getSimpleName()).list()
        .size();
  }

  @Override
  public void deletePersistent(Object obj) {
    this.session.delete(obj);
  }

  @Override
  public void exeSQL(String query) {
    this.session.createSQLQuery(query).executeUpdate();
  }

  @Override
  public void exeSQLFile(String fileName) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(fileName));
      String query = "";
      String line = reader.readLine();
      while (line != null) {
        query += line.trim();
        line = reader.readLine();
      }
      if (!query.isEmpty()) {
        this.exeSQL(query);
      }
    } catch (FileNotFoundException e) {
      GeodatabaseHibernate.logger.error("Le fichier " + fileName
          + " n'existe pas");
    } catch (IOException e) {
      GeodatabaseHibernate.logger
          .error("Erreur pendant l'exécution des requêtes du fichier "
              + fileName);
      GeodatabaseHibernate.logger.error(e.getMessage());
    }
  }

  @Override
  public List<?> exeSQLQuery(String query) {
    return this.session.createSQLQuery(query).list();
  }

  @SuppressWarnings("deprecation")
  @Override
  public Connection getConnection() {
    return this.session.connection();
  }

  @Override
  public int getDBMS() {
    // FIXME a corriger: le sgbd n'est pas forcement postgis
    return Geodatabase.POSTGIS;
  }

  @Override
  public List<Metadata> getMetadata() {
    return this.metadataList;
  }

  @Override
  public Metadata getMetadata(Class<?> theClass) {
    for (int i = 0; i < this.metadataList.size(); i++) {
      if (theClass.getName().compareTo(
          (this.metadataList.get(i)).getClassName()) == 0) {
        return this.metadataList.get(i);
      }
    }
    GeodatabaseHibernate.logger.warn("La classe n'est pas mappée : "
        + theClass.getName());
    return null;
  }

  @Override
  public Metadata getMetadata(String theTable) {
    for (int i = 0; i < this.metadataList.size(); i++) {
      if ((this.metadataList.get(i)).getTableName() != null) {
        if (theTable.compareToIgnoreCase((this.metadataList.get(i))
            .getTableName()) == 0) {
          return this.metadataList.get(i);
        }
      }
    }
    GeodatabaseHibernate.logger.warn("La table n'est pas mappée : " + theTable);
    return null;
  }

  @Override
  public boolean isOpen() {
    return this.session.isOpen();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T load(Class<T> clazz, Object id) {
    return (T) this.session.load(clazz, (Serializable) id);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> List<T> loadAll(Class<T> featureClass) {
    return this.session.createQuery("from " + featureClass.getSimpleName())
        .list();
  }

  @Override
  public <T> T loadAll(Class<?> featureClass, Class<T> featureListClass) {

    // build the output feature list
    T result = null;
    try {
      result = featureListClass.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    // execute the query
    List<?> list = this.session.createQuery(
        "from " + featureClass.getSimpleName()).list();
    try {
      for (Object obj : list) {
        result.getClass().getMethod("add", new Class[] { IFeature.class })
            .invoke(result, new Object[] { (IFeature) obj });
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends IFeature> FT_FeatureCollection<T> loadAllFeatures(
      Class<?> featureClass) {
    return this.loadAll(featureClass, FT_FeatureCollection.class);
  }

  @Override
  public <T> T loadAllFeatures(Class<?> featureClass, Class<T> featureListClass) {
    return this.loadAll(featureClass, featureListClass);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T loadAllFeatures(Class<?> featureClass,
      Class<T> featureListClass, String param, String value) {
    return (T) this.session.createSQLQuery(
        "select x from " + featureClass.getName() + " where " + param + " = "
            + value).list();
  }

  @Override
  public <T extends IFeature> IFeatureCollection<T> loadAllFeatures(
      Class<T> featureClass, IGeometry geom) {
    GeodatabaseHibernate.logger.warn("non implemente");
    return null;
  }

  /**
   * Cree une requete pour permettre de charger tous les objets a partir d'une
   * liste d'identifants. Usage interne.
   */
  protected String createInQuery(List<?> idList, String tableName) {
    String result = "select x from " + tableName + " where id in (";
    StringBuffer strbuff = new StringBuffer(result);
    Iterator<?> i = idList.iterator();
    while (i.hasNext()) {
      int k = ((Number) i.next()).intValue();
      strbuff.append(k);
      strbuff.append(",");
    }
    result = strbuff.toString();
    result = result.substring(0, result.length() - 1);
    result = result + ")";
    return result;
  }

  @Override
  public <T> T loadAllFeatures(Class<?> featureClass,
      Class<T> featureListClass, IGeometry geom) {
    // FIXME: code dependant de PostGIS

    T result = null;
    try {
      result = featureListClass.newInstance();
    } catch (Exception e) {
      GeodatabaseHibernate.logger
          .error("Impossible de créer une nouvelle instance de la classe "
              + featureListClass.getName());
      e.printStackTrace();
      return null;
    }
    if ((FT_Feature.class).isAssignableFrom(featureClass)) {
      // on cherche la liste des identifiants
      List<?> idList = PostgisSpatialQuery.loadAllFeatures(this, featureClass,
          geom);
      // charge tous les objets dont on a trouve l'identifiant
      if (idList.size() > 0) {
        String query = this.createInQuery(idList, featureClass.getName());
        try {
          List<?> list = this.session.createSQLQuery(query).list();
          Iterator<?> iter = list.iterator();
          // on récupère le srid attribué à cette classe dans les métadonnées
          Metadata metadata = this.getMetadata(featureClass);
          int srid = -1;
          if (metadata != null && metadata.getSRID() != 0) {
            srid = metadata.getSRID();
          } else {
            // si cette classe ne contient pas de métadonnées ou si c'est une
            // classe mère de la classe stockée dans le SGBD
            // on récupère le premier élément (s'il existe) et ses métadonnées.
            if (iter.hasNext()) {
              FT_Feature feature = (FT_Feature) iter.next();
              metadata = this.getMetadata(feature.getClass());
              if (metadata != null) {
                srid = metadata.getSRID();
              }
              if (feature.getGeom() != null) {
                feature.getGeom().setCRS(srid);
              }
              result.getClass()
                  .getMethod("add", new Class[] { FT_Feature.class })
                  .invoke(result, new Object[] { feature });
            }
          }
          while (iter.hasNext()) {
            FT_Feature feature = (FT_Feature) iter.next();
            if (feature.getGeom() != null) {
              feature.getGeom().setCRS(srid);
            }
            result.getClass()
                .getMethod("add", new Class[] { FT_Feature.class })
                .invoke(result, new Object[] { feature });
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } else {
      GeodatabaseHibernate.logger
          .warn("loadAllFeatures() : La classe passee en parametre n'est pas une sous-classe de FT_Feature");
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends IFeature> IFeatureCollection<T> loadAllFeatures(
      Class<T> featureClass, IGeometry geom, double dist) {
    // FIXME: code dependant de PostGIS

    FT_FeatureCollection<T> result = new FT_FeatureCollection<T>();

    if (!FT_Feature.class.isAssignableFrom(featureClass)) {
      GeodatabaseHibernate.logger
          .warn("loadAllFeatures() : La classe passee en parametre n'est pas une sous-classe de FT_Feature");
      return result;
    }

    // on cherche la liste des identifiants
    List<?> idList = PostgisSpatialQuery.loadAllFeatures(this, featureClass,
        geom, dist);
    // charge tous les objets dont on a trouve l'identifiant
    if (idList.size() > 0) {
      String query = this.createInQuery(idList, featureClass.getName());
      try {
        List<T> list = this.session.createSQLQuery(query).list();
        Iterator<T> iter = list.iterator();
        // on récupère le srid attribué à cette classe dans les métadonnées
        Metadata metadata = this.getMetadata(featureClass);
        int srid = -1;
        if (metadata != null && metadata.getSRID() != 0) {
          srid = metadata.getSRID();
        } else {
          // si cette classe ne contient pas de métadonnées ou si c'est une
          // classe mère de la classe stockée dans le SGBD
          // on récupère le premier élément (s'il existe) et ses métadonnées.
          if (iter.hasNext()) {
            T feature = iter.next();
            metadata = this.getMetadata(feature.getClass());
            if (metadata != null) {
              srid = metadata.getSRID();
            }
            if (feature.getGeom() != null) {
              feature.getGeom().setCRS(srid);
            }
            result.add(feature);
          }
        }
        while (iter.hasNext()) {
          T feature = iter.next();
          if (feature.getGeom() != null) {
            feature.getGeom().setCRS(srid);
          }
          result.add(feature);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  @Override
  public <T> T loadAllFeatures(Class<?> featureClass,
      Class<T> featureListClass, IGeometry geom, double dist) {
    GeodatabaseHibernate.logger.warn("non implemente");
    return null;
  }

  @Override
  public <T extends IFeature> IFeatureCollection<T> loadAllFeatures(
      FeatureType featureType) {
    GeodatabaseHibernate.logger.warn("non implemente");
    return null;
  }

  @Override
  public List<?> loadOQL(String query, Object param) {
    GeodatabaseHibernate.logger.warn("non implemente");
    return null;
  }

  @Override
  public void makePersistent(Object obj) {
    this.session.saveOrUpdate(obj);
  }

  // public void makePersistent(Object obj) {session.persist(obj);}

  @Override
  public int maxId(Class<?> theClass) {
    GeodatabaseHibernate.logger.warn("non implemente");
    return 0;
  }

  @Override
  public void mbr(Class<?> clazz) {
    GeodatabaseHibernate.logger.warn("non implemente");
  }

  @Override
  public int minId(Class<?> theClass) {
    GeodatabaseHibernate.logger.warn("non implemente");
    return 0;
  }

//  @Override
//  public OQLQuery newOQLQuery() {
//    GeodatabaseHibernate.logger.warn("non implemente");
//    return null;
//  }

  @Override
  public void refreshRepository(File newRepository) throws Exception {
    GeodatabaseHibernate.logger.warn("non implemente");
  }

  @Override
  public void spatialIndex(Class<?> clazz) {
    GeodatabaseHibernate.logger.warn("non implemente");
  }
}
