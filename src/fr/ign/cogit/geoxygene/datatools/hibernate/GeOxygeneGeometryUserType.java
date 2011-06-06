package fr.ign.cogit.geoxygene.datatools.hibernate;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.postgis.PGgeometry;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.conversion.ParseException;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

/**
 * @author Julien Perret
 * 
 */
public class GeOxygeneGeometryUserType implements UserType {
  static Logger logger = Logger.getLogger(GeOxygeneGeometryUserType.class
      .getName());
  private static final int[] geometryTypes = new int[] { Types.STRUCT };

  @Override
  public int[] sqlTypes() {
    return GeOxygeneGeometryUserType.geometryTypes;
  }

  /**
   * Converts the native geometry object to a GeOxygene <code>GM_Object</code>.
   * 
   * @param object native database geometry object (depends on the JDBC spatial
   *          extension of the database)
   * @return GeOxygene geometry corresponding to geomObj.
   */
  @SuppressWarnings("unchecked")
  public GM_Object convert2GM_Object(Object object) {
    // logger.error(object.toString());
    // logger.error(object.getClass());
    if (object == null) {
      return null;
    }
    if (object instanceof org.postgresql.util.PGobject) {
      try {
        object = new PGgeometry(object.toString());
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    // in some cases, Postgis returns not PGgeometry objects
    // but org.postgis.Geometry instances.
    // This has been observed when retrieving GeometryCollections
    // as the result of an SQL-operation such as Union.
    if (object instanceof org.postgis.Geometry) {
      object = new PGgeometry((org.postgis.Geometry) object);
    }
    if (object instanceof PGgeometry) {
      PGgeometry pgGeom = (PGgeometry) object;
      try {
        /*
         * In version 1.0.x of PostGIS, SRID is added to the beginning of the
         * pgGeom string
         */
        String geom = pgGeom.toString();
        // logger.info(geom);
        String subString = geom.substring(
            geom.indexOf("=") + 1, geom.indexOf(";")); //$NON-NLS-1$ //$NON-NLS-2$
        // logger.info(subString);
        int srid = Integer.parseInt(subString);
        GM_Object geOxyGeom = WktGeOxygene.makeGeOxygene(geom.substring(geom
            .indexOf(";") + 1)); //$NON-NLS-1$
        if (geOxyGeom instanceof GM_MultiPoint) {
          GM_MultiPoint aggr = (GM_MultiPoint) geOxyGeom;
          if (aggr.size() == 1) {
            geOxyGeom = aggr.get(0);
            geOxyGeom.setCRS(srid);
            return geOxyGeom;
          }
        }
        if (geOxyGeom instanceof GM_MultiCurve) {
          GM_MultiCurve<GM_OrientableCurve> aggr = (GM_MultiCurve<GM_OrientableCurve>) geOxyGeom;
          if (aggr.size() == 1) {
            geOxyGeom = aggr.get(0);
            geOxyGeom.setCRS(srid);
            return geOxyGeom;
          }
        }
        if (geOxyGeom instanceof GM_MultiSurface) {
          GM_MultiSurface<GM_OrientableSurface> aggr = (GM_MultiSurface<GM_OrientableSurface>) geOxyGeom;
          if (aggr.size() == 1) {
            geOxyGeom = aggr.get(0);
            geOxyGeom.setCRS(srid);
            return geOxyGeom;
          }
        }
        geOxyGeom.setCRS(srid);
        return geOxyGeom;
      } catch (ParseException e) {
        GeOxygeneGeometryUserType.logger.warn("## WARNING ## " + //$NON-NLS-1$
            "Postgis to GeOxygene returns NULL"); //$NON-NLS-1$
        e.printStackTrace();
        return null;
      }
    }
    return null;
  }

  /**
   * Converts a GeOxygene <code>GM_Object</code> to a native geometry object.
   * 
   * @param geom GeOxygene GM_Object to convert
   * @param connection the current database connection
   * @return native database geometry object corresponding to geom.
   */
  public Object conv2DBGeometry(GM_Object geom, Connection connection) {
    try {
      if (geom == null) {
        return null;
      }
      String srid = "";
      if (geom.getCRS() != -1) {
        srid = "SRID=" + geom.getCRS() + ";";
      }
//      logger.info("conv2DBGeometry " + srid + geom.toString());
      PGgeometry pgGeom = new PGgeometry(srid + geom.toString());
      return pgGeom;
    } catch (SQLException e) {
      GeOxygeneGeometryUserType.logger
          .warn("## WARNING ## GeOxygene to Postgis returns NULL "); //$NON-NLS-1$
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Object assemble(Serializable cached, Object owner)
      throws HibernateException {
    return cached;
  }

  @Override
  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  @Override
  public Serializable disassemble(Object value) throws HibernateException {
    return (Serializable) value;
  }

  @Override
  public boolean equals(Object x, Object y) throws HibernateException {
    if (x == y) {
      return true;
    }
    if (x == null || y == null) {
      return false;
    }
    return ((GM_Object) x).equalsExact((GM_Object) y);
  }

  @Override
  public int hashCode(Object x) throws HibernateException {
    return x.hashCode();
  }

  @Override
  public boolean isMutable() {
    return false;
  }

  @Override
  public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
      throws HibernateException, SQLException {
    Object geomObj = rs.getObject(names[0]);
    return this.convert2GM_Object(geomObj);
  }

  @Override
  public void nullSafeSet(PreparedStatement st, Object value, int index)
      throws HibernateException, SQLException {
    if (value == null) {
      st.setNull(index, this.sqlTypes()[0]);
    } else {
      if (value instanceof GM_Object) {
        GM_Object geom = (GM_Object) value;
        Object dbGeom = this.conv2DBGeometry(geom, st.getConnection());
        st.setObject(index, dbGeom);
      } else {
        try {
          GM_Object geom = AdapterFactory.toGM_Object((Geometry) value);
          Object dbGeom = this.conv2DBGeometry(geom, st.getConnection());
          st.setObject(index, dbGeom);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public Object replace(Object original, Object target, Object owner)
      throws HibernateException {
    return original;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class returnedClass() {
    return GM_Object.class;
  }
}
