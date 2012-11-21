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

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.conversion.ParseException;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

/**
 * 
 * 
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
   * Converts the native geometry object to a GeOxygene <code>GM_Object</code> .
   * 
   * @param object native database geometry object (depends on the JDBC spatial
   *          extension of the database)
   * @return GeOxygene geometry corresponding to geomObj.
   */
  @SuppressWarnings("unchecked")
  public IGeometry convert2GM_Object(Object object) {

    if (object == null) {
      return null;
    }
    System.out.println("01--------------------------------------------------------------------");
    if (object instanceof org.postgresql.util.PGobject) {
      try {
        object = new PGgeometry(object.toString());
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    System.out.println("02--------------------------------------------------------------------");
    // in some cases, Postgis returns not PGgeometry objects
    // but org.postgis.Geometry instances.
    // This has been observed when retrieving GeometryCollections
    // as the result of an SQL-operation such as Union.
    if (object instanceof org.postgis.Geometry) {
      object = new PGgeometry((org.postgis.Geometry) object);
    }
    System.out.println("1--------------------------------------------------------------------");
    if (object instanceof PGgeometry) {
      System.out.println("2--------------------------------------------------------------------");
      PGgeometry pgGeom = (PGgeometry) object;
      System.out.println("3--------------------------------------------------------------------");
      try {

        logger.info("pgGeom = " + pgGeom);
        String geom = pgGeom.toString();

        /*
         * In version 1.0.x of PostGIS, SRID is added to the beginning of the
         * pgGeom string
         */
        IGeometry geOxyGeom = null;
        int srid = -1;
        if (geom.indexOf("=") > -1) {
          String subString = geom.substring(
              geom.indexOf("=") + 1, geom.indexOf(";")); //$NON-NLS-1$ //$NON-NLS-2$
          srid = Integer.parseInt(subString);
          geOxyGeom = WktGeOxygene.makeGeOxygene(pgGeom.toString().substring(
              geom.indexOf("=") + 1));
        } else {
          //
          geOxyGeom = WktGeOxygene.makeGeOxygene(pgGeom.toString());
          // FIXME : il faut le trouver !!!
          // srid = N;
        }

        if (geOxyGeom instanceof IMultiPoint) {
          IMultiPoint aggr = (IMultiPoint) geOxyGeom;
          if (aggr.size() == 1) {
            aggr.get(0);
            aggr.setCRS(srid);
            return aggr;
          }
        }

        if (geOxyGeom instanceof IMultiCurve) {
          IMultiCurve<IOrientableCurve> aggr = (IMultiCurve<IOrientableCurve>) geOxyGeom;
          if (aggr.size() == 1) {
            aggr.get(0);
            aggr.setCRS(srid);
            return aggr;
          }
        }

        if (geOxyGeom instanceof IMultiSurface) {
          IMultiSurface<IOrientableSurface> aggr = (IMultiSurface<IOrientableSurface>) geOxyGeom;
          if (aggr.size() == 1) {
            aggr.get(0);
            aggr.setCRS(srid);
            return aggr;
          }
        }

        geOxyGeom.setCRS(srid);
        return geOxyGeom;

      } catch (ParseException e) {
        GeOxygeneGeometryUserType.logger
            .warn("## WARNING ## Postgis to GeOxygene returns NULL ");
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
  public Object conv2DBGeometry(IGeometry geom, Connection connection) {
    try {
      if (geom == null) {
        return null;
      }
      String srid = "";
      if (geom.getCRS() != -1) {
        srid = "SRID=" + geom.getCRS() + ";";
      }
      // logger.info("conv2DBGeometry " + srid + geom.toString());
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
    return ((IGeometry) x).equalsExact((IGeometry) y);
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
      if (value instanceof IGeometry) {
        IGeometry geom = (IGeometry) value;
        Object dbGeom = this.conv2DBGeometry(geom, st.getConnection());
        st.setObject(index, dbGeom);
      } else {
        try {
          IGeometry geom = AdapterFactory.toGM_Object((Geometry) value);
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

  @Override
  public Class<IGeometry> returnedClass() {
    return IGeometry.class;
  }
}
