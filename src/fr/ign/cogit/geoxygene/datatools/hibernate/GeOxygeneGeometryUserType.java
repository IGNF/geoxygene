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

import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.conversion.ParseException;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

public class GeOxygeneGeometryUserType implements UserType {
	static Logger logger = Logger.getLogger(GeOxygeneGeometryUserType.class
			.getName());
	private static final int[] geometryTypes = new int[] { Types.STRUCT };

	@Override
	public int[] sqlTypes() {
		return geometryTypes;
	}

	/**
	 * Converts the native geometry object to a GeOxygene <code>GM_Object</code>
	 * .
	 * 
	 * @param object
	 *            native database geometry object (depends on the JDBC spatial
	 *            extension of the database)
	 * @return GeOxygene geometry corresponding to geomObj.
	 */
	@SuppressWarnings("unchecked")
	public GM_Object convert2GM_Object(Object object) {
		if (object == null)
			return null;

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
				 * In version 1.0.x of PostGIS, SRID is added to the beginning
				 * of the pgGeom string
				 */

				GM_Object geOxyGeom = WktGeOxygene.makeGeOxygene(pgGeom.toString().substring(pgGeom.toString().indexOf(";") + 1));

				if (geOxyGeom instanceof GM_MultiPoint) {
					GM_MultiPoint aggr = (GM_MultiPoint) geOxyGeom;
					if (aggr.size() == 1) return aggr.get(0);
				}

				if (geOxyGeom instanceof GM_MultiCurve) {
					GM_MultiCurve<GM_OrientableCurve> aggr = (GM_MultiCurve<GM_OrientableCurve>) geOxyGeom;
					if (aggr.size() == 1) return aggr.get(0);
				}

				if (geOxyGeom instanceof GM_MultiSurface) {
					GM_MultiSurface<GM_OrientableSurface> aggr = (GM_MultiSurface<GM_OrientableSurface>) geOxyGeom;
					if (aggr.size() == 1) return aggr.get(0);
				}

				return geOxyGeom;

			} catch (ParseException e) {
				logger.warn("## WARNING ## Postgis to GeOxygene returns NULL ");
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
			if (geom == null) return null;
			PGgeometry pgGeom = new PGgeometry(geom.toString());
			return pgGeom;
		} catch (SQLException e) {
			logger.warn("## WARNING ## GeOxygene to Postgis returns NULL ");
			e.printStackTrace();
			return null;
		}
    }

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {return cached;}

	@Override
	public Object deepCopy(Object value) throws HibernateException {return value;}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {return (Serializable) value;}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) return true;
        if (x == null || y == null) return false;
        return ((GM_Object) x).equalsExact((GM_Object) y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {return x.hashCode();}

	@Override
	public boolean isMutable() {return false;}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
		Object geomObj = rs.getObject(names[0]);
		return convert2GM_Object(geomObj);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        if (value == null) {st.setNull(index, sqlTypes()[0]);}
        else {
            GM_Object geom = (GM_Object) value;
            Object dbGeom = conv2DBGeometry(geom, st.getConnection());
            st.setObject(index, dbGeom);
        }
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {return original;}

	@SuppressWarnings("unchecked")
	@Override
	public Class returnedClass() {return GM_Object.class;}
}
