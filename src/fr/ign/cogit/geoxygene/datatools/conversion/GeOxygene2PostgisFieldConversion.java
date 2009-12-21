/**
 * 
 */
package fr.ign.cogit.geoxygene.datatools.conversion;

import java.sql.SQLException;

import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.postgis.PGgeometry;

import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.conversion.ParseException;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

/**
 * Classe de test. Elle n'est pas utilisée pour l'instant.
 * @author Julien Perret
 *
 */
public class GeOxygene2PostgisFieldConversion implements FieldConversion {
//	  private BinaryParser parser = new BinaryParser();
//	  private BinaryWriter writer = new BinaryWriter();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#javaToSql(java.lang.Object)
	 */
	public Object javaToSql(Object source) throws ConversionException {
		if (source instanceof GM_Object) {
			try {
				PGgeometry pgGeom = new PGgeometry(((GM_Object)source).toString());
				return pgGeom;
			} catch (SQLException e) {
				System.out.println("failed to transforme the geometry");
				e.printStackTrace();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#sqlToJava(java.lang.Object)
	 */
	public Object sqlToJava(Object source) throws ConversionException {
		if (source instanceof PGgeometry) {
			PGgeometry geom = (PGgeometry) source;
			try {
				return WktGeOxygene.makeGeOxygene(geom.toString());
			} catch (ParseException e) {
				System.out.println("failed to transform the geometry");
				e.printStackTrace();
			}
		}
		return null;
	}

}
