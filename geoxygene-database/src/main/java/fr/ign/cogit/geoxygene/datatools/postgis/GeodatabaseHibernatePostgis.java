/**
 * 
 */
package fr.ign.cogit.geoxygene.datatools.postgis;

import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.hibernate.GeodatabaseHibernate;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * @author JGaffuri
 * 
 */
public class GeodatabaseHibernatePostgis extends GeodatabaseHibernate implements
    Geodatabase {
  static Logger logger = Logger.getLogger(GeodatabaseHibernatePostgis.class
      .getName());

  public GeodatabaseHibernatePostgis() {
    super();
  }

  @Override
  public int getDBMS() {
    return Geodatabase.POSTGIS;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void initMetadata() {
    super.initMetadata();
    PostgisSpatialQuery.initGeomMetadata(this.getMetadata(),
        this.session.connection());
  }

  @Override
  public <T> T loadAllFeatures(Class<?> featureClass,
      Class<T> featureListClass, IGeometry geom) {
    return this.loadAllFeatures(featureClass, featureListClass, geom, 0.0);
  }

  @Override
  public <T extends IFeature> IFeatureCollection<T> loadAllFeatures(
      Class<T> featureClass, IGeometry geom) {
    return this.loadAllFeatures(featureClass, geom, 0.0);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends IFeature> IFeatureCollection<T> loadAllFeatures(
      Class<T> featureClass, IGeometry geom, double dist) {
    return this.loadAllFeatures(featureClass, FT_FeatureCollection.class, geom,
        dist);
  }

  @Override
  public <T> T loadAllFeatures(Class<?> featureClass,
      Class<T> featureListClass, IGeometry geom, double dist) {

    T result = null;

    try {
      result = featureListClass.newInstance();
    } catch (Exception e) {
      GeodatabaseHibernatePostgis.logger
          .error("Impossible de créer une nouvelle instance de la classe "
              + featureListClass.getName());
      e.printStackTrace();
      return null;
    }

    if (!IFeature.class.isAssignableFrom(featureClass)) {
      GeodatabaseHibernatePostgis.logger
          .warn("loadAllFeatures() : La classe passee en parametre n'est pas une sous-classe de FT_Feature");
      return result;
    }

    // recupere la liste des identifiants des objets dans le geometrie
    List<?> idList;
    if (dist <= 0) {
      idList = PostgisSpatialQuery.loadAllFeatures(this, featureClass, geom);
    } else {
      idList = PostgisSpatialQuery.loadAllFeatures(this, featureClass, geom,
          dist);
    }

    if (idList.size() == 0) {
      return result;
    }

    try {
      // charge tous les objets dont on a trouve l'identifiant
      for (Object obj : idList) {
        IFeature feature = (IFeature) this.load(featureClass, obj);
        result.getClass().getMethod("add", new Class[] { IFeature.class })
            .invoke(result, new Object[] { feature });
      }

      /*
       * List<?> list = session.createSQLQuery( createInQuery(idList,
       * featureClass.getName()) ).list(); Iterator<?> iter = list.iterator();
       * 
       * // on recupere le srid attribué à cette classe dans les métadonnées
       * Metadata metadata = this.getMetadata(featureClass); // si cette classe
       * ne contient pas de métadonnées ou si c'est une classe mère de la classe
       * stockée dans le SGBD // on récupère le premier élément (s'il existe) et
       * ses métadonnées. int srid = -1; if (metadata != null &&
       * metadata.getSRID() != 0) srid=metadata.getSRID(); else { //recupere le
       * premier objet eventuel et son SRID, ajoute cet objet a la collection
       * resusltat if (iter.hasNext()) { FT_Feature feature = (FT_Feature)
       * iter.next(); metadata = this.getMetadata(feature.getClass()); if
       * (metadata!=null) srid=metadata.getSRID(); if (feature.getGeom()!=null)
       * feature.getGeom().setCRS(srid); result.getClass().getMethod("add", new
       * Class[]{FT_Feature.class}).invoke(result,new Object[] {feature}); } }
       * 
       * //ajoute les objets a la collection resusltat while (iter.hasNext()) {
       * FT_Feature feature = (FT_Feature) iter.next(); if (feature.getGeom() !=
       * null) feature.getGeom().setCRS(srid);
       * result.getClass().getMethod("add", new
       * Class[]{FT_Feature.class}).invoke(result,new Object[] {feature}); }
       */
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  @Override
  public int maxId(Class<?> theClass) {
    GeodatabaseHibernatePostgis.logger.warn("non implemente");
    return 0;
  }

  @Override
  public int minId(Class<?> theClass) {
    GeodatabaseHibernatePostgis.logger.warn("non implemente");
    return 0;
  }

  @Override
  public void spatialIndex(Class<?> classe) {
    PostgisSpatialQuery.spatialIndex(this, classe);
  }

  /**
   * corrige une colonne geometrique de table: supprimme la colonne et la recree
   * avec AddGeometryColumn
   * 
   * @param tableName
   * @param geomColumnName
   * @param SRID
   * @param geometryType
   * @param dimension
   */
  public void corrigerGeomColumn(String tableName, String geomColumnName,
      int SRID, String geometryType, int dimension) {
    this.exeSQL("ALTER TABLE " + tableName + " DROP " + geomColumnName + ";");
    this.exeSQLQuery("SELECT AddGeometryColumn('" + tableName + "', '"
        + geomColumnName + "'," + SRID + ",'" + geometryType + "'," + dimension
        + ");");
  }

}
